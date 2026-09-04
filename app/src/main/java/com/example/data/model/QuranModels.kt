package com.example.data.model

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val versesCount: Int,
    val revelationType: String // "مكية" or "مدنية"
)

data class Ayah(
    val numberInSurah: Int,
    val textArabic: String,
    val textEnglish: String = "",
    val tafsir: String = ""
)

data class Reciter(
    val id: String,
    val nameArabic: String,
    val nameEnglish: String,
    val subpath: String,
    val riwayah: String = "حفص عن عاصم",
    val bitRate: String = "128kbps"
) {
    fun getAyahAudioUrl(surahNumber: Int, ayahNumber: Int): String {
        val s = String.format("%03d", surahNumber)
        val a = String.format("%03d", ayahNumber)
        return "https://everyayah.com/data/$subpath/$s$a.mp3"
    }
}

enum class BackgroundType {
    DRAWABLE,
    GRADIENT
}

data class BackgroundTheme(
    val id: String,
    val nameArabic: String,
    val nameEnglish: String,
    val type: BackgroundType,
    val drawableRes: Int? = null,
    val gradientColors: List<Long> = emptyList()
)

data class ReelConfig(
    val surahNumber: Int = 1,
    val surahName: String = "الفاتحة",
    val fromAyah: Int = 1,
    val toAyah: Int = 7,
    val reciterId: String = "alafasy",
    val backgroundId: String = "kaaba",
    val aspectRatio: String = "9:16", // "9:16", "1:1", "16:9"
    val fontSize: Float = 26f,
    val fontFamilyType: String = "uthmani", // uthmani, amiri, naskh, ruqah
    val textColorHex: String = "#FFFFFF",
    val showTranslation: Boolean = false,
    val showSurahBadge: Boolean = true,
    val showReciterBadge: Boolean = true,
    val showFrame: Boolean = true,
    val motionEffect: String = "particles", // "particles", "stars", "rain", "zoom", "none"
    val darkOverlayAlpha: Float = 0.45f
)
