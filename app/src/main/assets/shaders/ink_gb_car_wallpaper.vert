// Companion vertex shader for GLSurfaceView / WallpaperService GLES2 pipeline.
// Pass-through full-screen triangle strip; fragment shader uses gl_FragCoord.

#version 100

attribute vec4 aPosition;

void main() {
    gl_Position = aPosition;
}
