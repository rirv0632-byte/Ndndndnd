package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ReelConfig

@Entity(tableName = "reel_projects")
data class ReelProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val surahNumber: Int,
    val surahName: String,
    val fromAyah: Int,
    val toAyah: Int,
    val reciterId: String,
    val backgroundId: String,
    val aspectRatio: String,
    val fontSize: Float,
    val fontFamilyType: String,
    val textColorHex: String,
    val showTranslation: Boolean,
    val showSurahBadge: Boolean,
    val showReciterBadge: Boolean,
    val showFrame: Boolean,
    val motionEffect: String,
    val darkOverlayAlpha: Float,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toReelConfig(): ReelConfig = ReelConfig(
        surahNumber = surahNumber,
        surahName = surahName,
        fromAyah = fromAyah,
        toAyah = toAyah,
        reciterId = reciterId,
        backgroundId = backgroundId,
        aspectRatio = aspectRatio,
        fontSize = fontSize,
        fontFamilyType = fontFamilyType,
        textColorHex = textColorHex,
        showTranslation = showTranslation,
        showSurahBadge = showSurahBadge,
        showReciterBadge = showReciterBadge,
        showFrame = showFrame,
        motionEffect = motionEffect,
        darkOverlayAlpha = darkOverlayAlpha
    )

    companion object {
        fun fromConfig(title: String, config: ReelConfig): ReelProjectEntity = ReelProjectEntity(
            title = title,
            surahNumber = config.surahNumber,
            surahName = config.surahName,
            fromAyah = config.fromAyah,
            toAyah = config.toAyah,
            reciterId = config.reciterId,
            backgroundId = config.backgroundId,
            aspectRatio = config.aspectRatio,
            fontSize = config.fontSize,
            fontFamilyType = config.fontFamilyType,
            textColorHex = config.textColorHex,
            showTranslation = config.showTranslation,
            showSurahBadge = config.showSurahBadge,
            showReciterBadge = config.showReciterBadge,
            showFrame = config.showFrame,
            motionEffect = config.motionEffect,
            darkOverlayAlpha = config.darkOverlayAlpha
        )
    }
}
