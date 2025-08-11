package io.github.nuclominus.imforge.app.ui.state

import android.net.Uri

sealed class SideEffect {
    data class ScrollTo(val position: Int) : SideEffect()
    data class ShowResolutionPicker(val uri: Uri) : SideEffect()
}