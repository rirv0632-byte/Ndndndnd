package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.Ayah
import com.example.data.model.ReelConfig
import com.example.ui.components.ReelCanvasView
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun quran_reel_screenshot() {
        val sampleAyah = Ayah(
            numberInSurah = 1,
            textArabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            textEnglish = "In the name of Allah, the Entirely Merciful, the Especially Merciful."
        )
        val config = ReelConfig(
            surahNumber = 1,
            surahName = "الفاتحة",
            fromAyah = 1,
            toAyah = 1,
            backgroundId = "emerald_dark"
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                ReelCanvasView(
                    config = config,
                    activeAyah = sampleAyah,
                    allAyahs = listOf(sampleAyah),
                    isPlaying = false
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
    }
}
