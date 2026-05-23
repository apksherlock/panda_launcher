package com.apksherlock.pandalauncher.ui.launcher

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

internal object LauncherNavTransitions {
    private const val DurationMs = 300

    fun <T> AnimatedContentTransitionScope<T>.enterFromBottom() =
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Up,
            animationSpec = tween(DurationMs),
        ) + fadeIn(animationSpec = tween(DurationMs))

    fun <T> AnimatedContentTransitionScope<T>.popExitToBottom() =
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Down,
            animationSpec = tween(DurationMs),
        ) + fadeOut(animationSpec = tween(DurationMs))

    fun <T> AnimatedContentTransitionScope<T>.enterFromEnd() =
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(DurationMs),
        ) + fadeIn(animationSpec = tween(DurationMs))

    fun <T> AnimatedContentTransitionScope<T>.popExitToEnd() =
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(DurationMs),
        ) + fadeOut(animationSpec = tween(DurationMs))

    fun <T> AnimatedContentTransitionScope<T>.enterFromTop() =
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Down,
            animationSpec = tween(DurationMs),
        ) + fadeIn(animationSpec = tween(DurationMs))

    fun <T> AnimatedContentTransitionScope<T>.popExitToTop() =
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Up,
            animationSpec = tween(DurationMs),
        ) + fadeOut(animationSpec = tween(DurationMs))
}
