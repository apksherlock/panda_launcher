package com.apksherlock.pandalauncher.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apksherlock.pandalauncher.R
import com.apksherlock.pandalauncher.ui.components.InkBlinkingPrompt
import com.apksherlock.pandalauncher.ui.components.InkText
import com.apksherlock.pandalauncher.ui.theme.InkThemeAccessor

@Composable
fun InkHeader(
    username: String,
    dateLine: String,
    modifier: Modifier = Modifier,
) {
    val text = InkThemeAccessor.text
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            InkBlinkingPrompt()
            InkText(
                text = " ${stringResource(R.string.ink_greeting)}",
                style = text.greeting,
            )
            InkText(
                text = username,
                style = text.username,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        InkText(text = dateLine, style = text.dateCaps)
        Spacer(Modifier.height(16.dp))
    }
}
