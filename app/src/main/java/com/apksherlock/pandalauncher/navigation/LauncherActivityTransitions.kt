package com.apksherlock.pandalauncher.navigation

import android.app.Activity
import android.content.Intent
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.favorite.FavoriteActivity

object LauncherActivityTransitions {

    fun openFavoritePicker(from: Activity) {
        from.startActivity(Intent(from, FavoriteActivity::class.java))
        from.overridePendingTransition(R.anim.slide_in_from_left, R.anim.stay)
    }

    fun closeFavorite(activity: Activity) {
        activity.finish()
        activity.overridePendingTransition(R.anim.stay, R.anim.slide_out_to_left)
    }
}
