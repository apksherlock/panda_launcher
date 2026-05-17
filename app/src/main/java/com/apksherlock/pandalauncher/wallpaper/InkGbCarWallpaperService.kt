package com.apksherlock.pandalauncher.wallpaper

import android.content.res.Configuration
import android.opengl.GLES20
import android.service.wallpaper.WallpaperService
import android.view.Choreographer
import android.view.SurfaceHolder
import com.apksherlock.pandalauncher.ui.theme.InkColorRgb
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.egl.EGLSurface

/**
 * Live wallpaper that runs [ink_gb_car_wallpaper.frag] (LCD wave) via GLES 2.0.
 *
 * Set via: long-press home → Wallpapers → Live wallpapers → Panda Launcher,
 * or [android.app.WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER].
 */
class InkGbCarWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = InkGbCarEngine()

    inner class InkGbCarEngine : Engine() {

        private val choreographer = Choreographer.getInstance()
        private val frameCallback = Choreographer.FrameCallback { drawFrame() }

        private var egl: EGL10? = null
        private var eglDisplay: EGLDisplay? = null
        private var eglConfig: EGLConfig? = null
        private var eglContext: EGLContext? = null
        private var eglSurface: EGLSurface? = null

        private var program = 0
        private var width = 1
        private var height = 1

        private var uResolution = -1
        private var uBaseColor = -1
        private var uAccentColor = -1
        private var uPatternScale = -1
        private var uScanlineOpacity = -1
        private var aPosition = -1

        private val quadVertices: FloatBuffer = ByteBuffer
            .allocateDirect(4 * 4 * 2)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(
                    floatArrayOf(
                        -1f, -1f,
                        1f, -1f,
                        -1f, 1f,
                        1f, 1f,
                    ),
                )
                position(0)
            }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(false)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            initEgl(holder)
            initGl()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            this.width = width.coerceAtLeast(1)
            this.height = height.coerceAtLeast(1)
            GLES20.glViewport(0, 0, this.width, this.height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            choreographer.removeFrameCallback(frameCallback)
            releaseGl()
            releaseEgl()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                choreographer.postFrameCallback(frameCallback)
            } else {
                choreographer.removeFrameCallback(frameCallback)
            }
        }

        override fun onDestroy() {
            choreographer.removeFrameCallback(frameCallback)
            super.onDestroy()
        }

        private fun initEgl(holder: SurfaceHolder) {
            val egl = EGLContext.getEGL() as EGL10
            this.egl = egl

            val display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
            egl.eglInitialize(display, null)
            eglDisplay = display

            val configAttribs = intArrayOf(
                EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL10.EGL_NONE,
            )
            val configs = arrayOfNulls<EGLConfig>(1)
            val numConfig = IntArray(1)
            egl.eglChooseConfig(display, configAttribs, configs, 1, numConfig)
            val config = configs[0] ?: error("No EGL config")
            eglConfig = config

            val contextAttribs = intArrayOf(
                EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE,
            )
            eglContext = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, contextAttribs)

            val surfaceAttribs = intArrayOf(EGL10.EGL_NONE)
            eglSurface = egl.eglCreateWindowSurface(display, config, holder.surface, surfaceAttribs)
            makeCurrent()
        }

        private fun makeCurrent() {
            val egl = egl ?: return
            egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
        }

        private fun initGl() {
            val vert = loadAsset("shaders/ink_gb_car_wallpaper.vert")
            val frag = loadAsset("shaders/ink_gb_car_wallpaper.frag")
            program = linkProgram(vert, frag)

            aPosition = GLES20.glGetAttribLocation(program, "aPosition")
            uResolution = GLES20.glGetUniformLocation(program, "resolution")
            uBaseColor = GLES20.glGetUniformLocation(program, "baseColor")
            uAccentColor = GLES20.glGetUniformLocation(program, "accentColor")
            uPatternScale = GLES20.glGetUniformLocation(program, "patternScale")
            uScanlineOpacity = GLES20.glGetUniformLocation(program, "scanlineOpacity")
        }

        private fun drawFrame() {
            if (eglSurface == null || program == 0) return
            makeCurrent()

            val dark = isDarkTheme()
            val base = InkColorRgb.base(dark)
            val accent = InkColorRgb.accent(dark)

            GLES20.glUseProgram(program)
            GLES20.glUniform2f(uResolution, width.toFloat(), height.toFloat())
            GLES20.glUniform4fv(uBaseColor, 1, base, 0)
            GLES20.glUniform4fv(uAccentColor, 1, accent, 0)
            GLES20.glUniform1f(uPatternScale, PATTERN_SCALE)
            GLES20.glUniform1f(uScanlineOpacity, SCANLINE_OPACITY)

            GLES20.glEnableVertexAttribArray(aPosition)
            GLES20.glVertexAttribPointer(aPosition, 2, GLES20.GL_FLOAT, false, 0, quadVertices)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
            GLES20.glDisableVertexAttribArray(aPosition)

            egl?.eglSwapBuffers(eglDisplay, eglSurface)
            choreographer.postFrameCallback(frameCallback)
        }

        private fun isDarkTheme(): Boolean {
            val night = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return night == Configuration.UI_MODE_NIGHT_YES
        }

        private fun releaseGl() {
            if (program != 0) {
                GLES20.glDeleteProgram(program)
                program = 0
            }
        }

        private fun releaseEgl() {
            val egl = egl ?: return
            egl.eglMakeCurrent(
                eglDisplay,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT,
            )
            if (eglSurface != null) {
                egl.eglDestroySurface(eglDisplay, eglSurface)
                eglSurface = null
            }
            if (eglContext != null) {
                egl.eglDestroyContext(eglDisplay, eglContext)
                eglContext = null
            }
            if (eglDisplay != null) {
                egl.eglTerminate(eglDisplay)
                eglDisplay = null
            }
            this.egl = null
        }

        private fun loadAsset(path: String): String =
            assets.open(path).bufferedReader().use { it.readText() }

        private fun linkProgram(vertSrc: String, fragSrc: String): Int {
            val vert = compileShader(GLES20.GL_VERTEX_SHADER, vertSrc)
            val frag = compileShader(GLES20.GL_FRAGMENT_SHADER, fragSrc)
            val program = GLES20.glCreateProgram()
            GLES20.glAttachShader(program, vert)
            GLES20.glAttachShader(program, frag)
            GLES20.glLinkProgram(program)
            GLES20.glDeleteShader(vert)
            GLES20.glDeleteShader(frag)

            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                val log = GLES20.glGetProgramInfoLog(program)
                GLES20.glDeleteProgram(program)
                error("Program link failed: $log")
            }
            return program
        }

        private fun compileShader(type: Int, source: String): Int {
            val shader = GLES20.glCreateShader(type)
            GLES20.glShaderSource(shader, source)
            GLES20.glCompileShader(shader)
            val compiled = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                val log = GLES20.glGetShaderInfoLog(shader)
                GLES20.glDeleteShader(shader)
                error("Shader compile failed: $log")
            }
            return shader
        }
    }

    private companion object {
        private const val EGL_CONTEXT_CLIENT_VERSION = 0x3098
        private const val EGL_OPENGL_ES2_BIT = 4
        /** Wave coarseness / band size (see fragment shader). */
        private const val PATTERN_SCALE = 0.32f
        private const val SCANLINE_OPACITY = 0.12f
    }
}
