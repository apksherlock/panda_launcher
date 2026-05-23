package com.apksherlock.pandalauncher.favorite

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.model.LaunchableApp

object FavoriteLauncher {

    /**
     * Opens the favorite app's main activity with a left-to-right enter animation.
     * Starts in the launcher's task so Back returns to the home screen.
     */
    fun launchGuest(from: Activity, app: LaunchableApp) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            component = app.componentName
        }
        val options = ActivityOptions.makeCustomAnimation(
            from,
            R.anim.slide_in_from_left,
            R.anim.stay,
        )
        from.startActivity(intent, options.toBundle())
    }
}
