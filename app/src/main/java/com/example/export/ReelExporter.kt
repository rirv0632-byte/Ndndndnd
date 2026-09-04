package com.example.export

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.content.FileProvider
import com.example.R
import com.example.data.model.Ayah
import com.example.data.model.BackgroundType
import com.example.data.model.ReelConfig
import com.example.data.quran.QuranRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ReelExporter {

    fun shareReelText(context: Context, config: ReelConfig, ayah: Ayah?) {
        val reciter = QuranRepository.allReciters.firstOrNull { it.id == config.reciterId }
            ?: QuranRepository.allReciters.first()

        val audioUrl = reciter.getAyahAudioUrl(config.surahNumber, ayah?.numberInSurah ?: config.fromAyah)

        val message = buildString {
            append("✨ قال تعالى في سورة ${config.surahName}:\n\n")
            if (ayah != null) {
                append("﴿ ${ayah.textArabic} ﴾ [الآية: ${ayah.numberInSurah}]\n\n")
                if (ayah.textEnglish.isNotBlank()) {
                    append("\"${ayah.textEnglish}\"\n\n")
                }
            } else {
                append("﴿ الآيات من ${config.fromAyah} إلى ${config.toAyah} ﴾\n\n")
            }
            append("🎙️ بصوت القارئ: ${reciter.nameArabic}\n")
            append("🎧 استمع للتلاوة العذبة:\n$audioUrl\n\n")
            append("تم التصميم عبر تطبيق صانع ريلز القرآن 📱✨")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "مشاركة الآية والتلاوة"))
    }

    suspend fun exportAndShareImageCard(
        context: Context,
        config: ReelConfig,
        ayah: Ayah?
    ) = withContext(Dispatchers.IO) {
        try {
            // Determine dimensions based on aspect ratio
            val (width, height) = when (config.aspectRatio) {
                "1:1" -> Pair(1080, 1080)
                "16:9" -> Pair(1920, 1080)
                else -> Pair(1080, 1920) // 9:16 vertical reels
            }

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val theme = QuranRepository.backgroundThemes.firstOrNull { it.id == config.backgroundId }
                ?: QuranRepository.backgroundThemes.first()

            // Draw background
            if (theme.type == BackgroundType.DRAWABLE && theme.drawableRes != null) {
                val bgBitmap = BitmapFactory.decodeResource(context.resources, theme.drawableRes)
                if (bgBitmap != null) {
                    val srcRect = Rect(0, 0, bgBitmap.width, bgBitmap.height)
                    val destRect = Rect(0, 0, width, height)
                    canvas.drawBitmap(bgBitmap, srcRect, destRect, null)
                    bgBitmap.recycle()
                } else {
                    canvas.drawColor(android.graphics.Color.parseColor("#092820"))
                }
            } else {
                val gradPaint = Paint().apply {
                    shader = android.graphics.LinearGradient(
                        0f, 0f, 0f, height.toFloat(),
                        android.graphics.Color.parseColor("#0F4D3B"),
                        android.graphics.Color.parseColor("#041C15"),
                        android.graphics.Shader.TileMode.CLAMP
                    )
                }
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), gradPaint)
            }

            // Dark overlay
            val overlayAlpha = (config.darkOverlayAlpha * 255).toInt().coerceIn(0, 255)
            val overlayPaint = Paint().apply {
                color = android.graphics.Color.argb(overlayAlpha, 0, 0, 0)
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)

            // Optional Golden Frame
            if (config.showFrame) {
                val framePaint = Paint().apply {
                    color = android.graphics.Color.parseColor("#D4AF37")
                    style = Paint.Style.STROKE
                    strokeWidth = 4f
                    isAntiAlias = true
                }
                val frameRect = RectF(40f, 40f, width - 40f, height - 40f)
                canvas.drawRoundRect(frameRect, 30f, 30f, framePaint)
            }

            // Surah Header Badge
            if (config.showSurahBadge) {
                val badgePaint = Paint().apply {
                    color = android.graphics.Color.argb(160, 0, 0, 0)
                    style = Paint.Style.FILL
                }
                val badgeBorderPaint = Paint().apply {
                    color = android.graphics.Color.parseColor("#E6C65C")
                    style = Paint.Style.STROKE
                    strokeWidth = 3f
                    isAntiAlias = true
                }
                val badgeTextPaint = Paint().apply {
                    color = android.graphics.Color.parseColor("#FFDF7D")
                    textSize = 42f
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }

                val badgeRect = RectF(width / 2f - 240f, 100f, width / 2f + 240f, 180f)
                canvas.drawRoundRect(badgeRect, 40f, 40f, badgePaint)
                canvas.drawRoundRect(badgeRect, 40f, 40f, badgeBorderPaint)
                canvas.drawText("سورة ${config.surahName}", width / 2f, 155f, badgeTextPaint)
            }

            // Quran Verse Text
            val textPaint = Paint().apply {
                color = try {
                    android.graphics.Color.parseColor(config.textColorHex)
                } catch (e: Exception) {
                    android.graphics.Color.WHITE
                }
                textSize = (config.fontSize * 2.2f).coerceAtLeast(46f)
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                setShadowLayer(10f, 3f, 3f, android.graphics.Color.BLACK)
            }

            val verseText = ayah?.textArabic ?: "﴿ سورة ${config.surahName} ﴾"
            // Multi-line wrap
            val words = verseText.split(" ")
            val lines = mutableListOf<String>()
            var currentLine = StringBuilder()

            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                val measure = textPaint.measureText(testLine)
                if (measure > width - 180) {
                    lines.add(currentLine.toString())
                    currentLine = StringBuilder(word)
                } else {
                    currentLine = StringBuilder(testLine)
                }
            }
            if (currentLine.isNotEmpty()) {
                lines.add(currentLine.toString())
            }

            val lineHeight = textPaint.textSize * 1.6f
            val totalTextHeight = lines.size * lineHeight
            var startY = (height / 2f) - (totalTextHeight / 2f) + textPaint.textSize

            for (line in lines) {
                canvas.drawText(line, width / 2f, startY, textPaint)
                startY += lineHeight
            }

            // Ayah number badge
            ayah?.let {
                val ayahNumPaint = Paint().apply {
                    color = android.graphics.Color.parseColor("#E6C65C")
                    textSize = 40f
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText("﴿ ${it.numberInSurah} ﴾", width / 2f, startY + 30f, ayahNumPaint)
            }

            // Reciter Badge
            if (config.showReciterBadge) {
                val reciter = QuranRepository.allReciters.firstOrNull { it.id == config.reciterId }
                    ?: QuranRepository.allReciters.first()

                val reciterPaint = Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 34f
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    setShadowLayer(8f, 2f, 2f, android.graphics.Color.BLACK)
                }
                canvas.drawText("القارئ: ${reciter.nameArabic}", width / 2f, height - 100f, reciterPaint)
            }

            // Save to cache file and launch share sheet
            val cachePath = File(context.cacheDir, "images").apply { mkdirs() }
            val file = File(cachePath, "quran_reel_${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            withContext(Dispatchers.Main) {
                val contentUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    type = "image/png"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "مشاركة بطاقة ريلز القرآن"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
