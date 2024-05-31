package io.github.nuclominus.imforge.app.ui.state

sealed class SideEffect {
    data class ScrollTo(val position: Int) : SideEffect()
}