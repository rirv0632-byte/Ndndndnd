package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val QuranColorScheme = darkColorScheme(
  primary = Emerald400,
  onPrimary = Emerald900,
  primaryContainer = Emerald800,
  onPrimaryContainer = Emerald300,
  secondary = Gold400,
  onSecondary = Color(0xFF332000),
  secondaryContainer = Color(0xFF4A3B10),
  onSecondaryContainer = Gold200,
  tertiary = Emerald300,
  onTertiary = Emerald900,
  background = DarkSurface,
  onBackground = TextPrimary,
  surface = DarkSurfaceElevated,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceCard,
  onSurfaceVariant = TextSecondary,
  outline = DarkBorder,
  outlineVariant = Color(0xFF1E3830)
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = QuranColorScheme,
    typography = Typography,
    content = content
  )
}
