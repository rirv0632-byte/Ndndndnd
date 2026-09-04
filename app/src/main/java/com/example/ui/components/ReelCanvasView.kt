package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.BackgroundType
import com.example.data.model.ReelConfig
import com.example.data.quran.QuranRepository
import com.example.ui.theme.Gold400
import com.example.ui.theme.TextGold
import kotlin.random.Random

@Composable
fun ReelCanvasView(
    config: ReelConfig,
    activeAyah: Ayah?,
    allAyahs: List<Ayah>,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val aspect = when (config.aspectRatio) {
        "1:1" -> 1f
        "16:9" -> 16f / 9f
        else -> 9f / 16f
    }

    val theme = QuranRepository.backgroundThemes.firstOrNull { it.id == config.backgroundId }
        ?: QuranRepository.backgroundThemes.first()

    val reciter = QuranRepository.allReciters.firstOrNull { it.id == config.reciterId }
        ?: QuranRepository.allReciters.first()

    // Infinite animation for background zoom / Ken Burns effect
    val infiniteTransition = rememberInfiniteTransition(label = "reel_animation")
    val zoomScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (config.motionEffect == "zoom") 1.08f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zoom_anim"
    )

    val particleTick by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_tick"
    )

    val textParsedColor = remember(config.textColorHex) {
        try {
            Color(android.graphics.Color.parseColor(config.textColorHex))
        } catch (e: Exception) {
            Color.White
        }
    }

    val fontFamily = when (config.fontFamilyType) {
        "amiri" -> FontFamily.Serif
        "naskh" -> FontFamily.Default
        "ruqah" -> FontFamily.Cursive
        else -> FontFamily.Serif // uthmani default
    }

    Box(
        modifier = modifier
            .testTag("reel_canvas_container")
            .shadow(16.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .aspectRatio(aspect)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // 1. Background Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(zoomScale)
        ) {
            if (theme.type == BackgroundType.DRAWABLE && theme.drawableRes != null) {
                Image(
                    painter = painterResource(id = theme.drawableRes),
                    contentDescription = theme.nameArabic,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val colors = if (theme.gradientColors.isNotEmpty()) {
                    theme.gradientColors.map { Color(it) }
                } else {
                    listOf(Color(0xFF0F4D3B), Color(0xFF041C15))
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(colors)
                        )
                )
            }
        }

        // 2. Dynamic Motion Canvas Layer (Stars / Particles / Rain)
        if (config.motionEffect != "none") {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                when (config.motionEffect) {
                    "stars", "particles" -> {
                        val random = Random(42)
                        val count = 28
                        for (i in 0 until count) {
                            val baseX = random.nextFloat() * width
                            val baseY = random.nextFloat() * height
                            val speed = 0.3f + random.nextFloat() * 0.7f
                            val currentY = (baseY + particleTick * height * speed) % height
                            val radius = (1.5f + random.nextFloat() * 3.5f)
                            val alpha = 0.2f + 0.6f * kotlin.math.sin((particleTick + i * 0.2f) * kotlin.math.PI).toFloat().coerceIn(0f, 1f)

                            drawCircle(
                                color = if (config.motionEffect == "stars") Color.White.copy(alpha = alpha) else Gold400.copy(alpha = alpha),
                                radius = radius,
                                center = Offset(baseX, currentY)
                            )
                        }
                    }
                    "rain" -> {
                        val random = Random(77)
                        val rainDrops = 35
                        for (i in 0 until rainDrops) {
                            val startX = random.nextFloat() * width
                            val startY = (random.nextFloat() * height + particleTick * height * 1.5f) % height
                            val length = 15f + random.nextFloat() * 25f
                            drawLine(
                                color = Color.White.copy(alpha = 0.25f),
                                start = Offset(startX, startY),
                                end = Offset(startX - 2f, startY + length),
                                strokeWidth = 1.2f
                            )
                        }
                    }
                }
            }
        }

        // 3. Dark Overlay Layer for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = config.darkOverlayAlpha))
        )

        // 4. Optional Islamic Border Frame
        if (config.showFrame) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Gold400.copy(alpha = 0.8f),
                                Gold400.copy(alpha = 0.2f),
                                Gold400.copy(alpha = 0.8f)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
            )
        }

        // 5. Quran Content Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Surah Header Badge
            if (config.showSurahBadge) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, Gold400.copy(alpha = 0.5f), RoundedCornerShape(30.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Gold400,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "سورة ${config.surahName}",
                            color = TextGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (config.fromAyah == config.toAyah) {
                            Text(
                                text = " • الآية ${config.fromAyah}",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        } else {
                            Text(
                                text = " • الآيات ${config.fromAyah}-${config.toAyah}",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Center Section: The Sacred Quranic Verses
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .padding(vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // If single active ayah or multiple ayahs
                val displayAyah = activeAyah ?: allAyahs.firstOrNull()
                if (displayAyah != null) {
                    // Sacred Arabic Verse Text
                    Text(
                        text = displayAyah.textArabic,
                        color = textParsedColor,
                        fontSize = config.fontSize.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = (config.fontSize * 1.6f).sp,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            textDirection = TextDirection.Rtl,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.9f),
                                offset = Offset(2f, 2f),
                                blurRadius = 8f
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )

                    // Ayah Number Badge
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                            .border(1.dp, Gold400.copy(alpha = 0.6f), CircleShape)
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "﴿ ${displayAyah.numberInSurah} ﴾",
                            color = Gold400,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Optional English Translation or Tafsir
                    if (config.showTranslation && displayAyah.textEnglish.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = displayAyah.textEnglish,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = (config.fontSize * 0.55f).coerceAtLeast(12f).sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(horizontal = 12.dp)
                        )
                    }
                }
            }

            // Bottom Section: Reciter Badge & Audio Pulse Wave
            if (config.showReciterBadge) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "تلاوة",
                            tint = if (isPlaying) Gold400 else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = reciter.nameArabic,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
