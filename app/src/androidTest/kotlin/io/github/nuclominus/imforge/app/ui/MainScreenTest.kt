package io.github.nuclominus.imforge.app.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.nuclominus.imforge.app.App
import io.github.nuclominus.imforge.app.ui.viewmodel.DashboardViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun mainScreenTest() {
        composeTestRule.setContent {
            val viewModel: DashboardViewModel = hiltViewModel()
            App(viewModel) {}
        }

        composeTestRule.onNodeWithText("Press + to add image for compressing").assertExists()
    }
}