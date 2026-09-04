package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlayerState
import com.example.data.model.Ayah
import com.example.data.model.ReelConfig
import com.example.data.quran.QuranRepository
import com.example.export.ReelExporter
import com.example.ui.components.ReelCanvasView
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Gold400
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StudioScreen(
    config: ReelConfig,
    activeAyah: Ayah?,
    allAyahs: List<Ayah>,
    audioState: AudioPlayerState,
    isLoadingVerses: Boolean,
    onSelectSurah: (Int) -> Unit,
    onSetAyahRange: (Int, Int) -> Unit,
    onSelectReciter: (String) -> Unit,
    onSelectBackground: (String) -> Unit,
    onSetAspectRatio: (String) -> Unit,
    onSetFontSize: (Float) -> Unit,
    onSetFontFamily: (String) -> Unit,
    onSetTextColor: (String) -> Unit,
    onToggleTranslation: () -> Unit,
    onToggleSurahBadge: () -> Unit,
    onToggleReciterBadge: () -> Unit,
    onToggleFrame: () -> Unit,
    onSetMotionEffect: (String) -> Unit,
    onSetDarkOverlay: (Float) -> Unit,
    onTogglePlayPause: () -> Unit,
    onNextAyah: () -> Unit,
    onPreviousAyah: () -> Unit,
    onSeekAudio: (Float) -> Unit,
    onSaveProject: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showFullscreenReel by remember { mutableStateOf(false) }
    var showSurahPickerSheet by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: الآيات, 1: القارئ, 2: الخلفية, 3: التصميم

    val currentSurah = QuranRepository.allSurahs.firstOrNull { it.number == config.surahNumber }
        ?: QuranRepository.allSurahs.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
            .testTag("studio_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = Gold400,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "صانع ريلز وفيديوهات القرآن",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Quran Reels Studio",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Save project button
                IconButton(
                    onClick = {
                        onSaveProject()
                        Toast.makeText(context, "تم حفظ المشروع بنجاح في مشاريعك", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                        .testTag("save_project_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = "حفظ",
                        tint = Gold400,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Share / Export button
                IconButton(
                    onClick = { showShareSheet = true },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                        .testTag("share_reel_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "تصدير ومشاركة",
                        tint = Emerald400,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Fullscreen button
                IconButton(
                    onClick = { showFullscreenReel = true },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Emerald500)
                        .testTag("fullscreen_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "ملء الشاشة",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Live Reel Canvas Preview (Responsive card)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            val previewHeight = when (config.aspectRatio) {
                "1:1" -> 320.dp
                "16:9" -> 220.dp
                else -> 400.dp
            }

            ReelCanvasView(
                config = config,
                activeAyah = activeAyah,
                allAyahs = allAyahs,
                isPlaying = audioState.isPlaying,
                modifier = Modifier
                    .height(previewHeight)
                    .widthIn(max = 420.dp)
                    .clickable { showFullscreenReel = true }
            )

            if (isLoadingVerses) {
                CircularProgressIndicator(
                    color = Gold400,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        // Playback Controller Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Seekbar slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (audioState.durationMs > 0) {
                            val pos = audioState.positionMs / 1000
                            String.format("%02d:%02d", pos / 60, pos % 60)
                        } else "00:00",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Slider(
                        value = audioState.progress,
                        onValueChange = onSeekAudio,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .testTag("audio_seek_slider"),
                        colors = SliderDefaults.colors(
                            thumbColor = Gold400,
                            activeTrackColor = Gold400,
                            inactiveTrackColor = DarkBorder
                        )
                    )
                    Text(
                        text = if (audioState.durationMs > 0) {
                            val dur = audioState.durationMs / 1000
                            String.format("%02d:%02d", dur / 60, dur % 60)
                        } else "00:00",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                // Control buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Ayah
                    IconButton(
                        onClick = onPreviousAyah,
                        enabled = audioState.currentAyah > config.fromAyah
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "الآية السابقة",
                            tint = if (audioState.currentAyah > config.fromAyah) TextPrimary else TextSecondary.copy(alpha = 0.3f)
                        )
                    }

                    // Play / Pause Button
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Gold400)
                            .testTag("play_pause_button")
                    ) {
                        if (audioState.isLoading) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (audioState.isPlaying) "إيقاف مؤقت" else "تشغيل",
                                tint = Color.Black,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    // Next Ayah
                    IconButton(
                        onClick = onNextAyah,
                        enabled = audioState.currentAyah < config.toAyah
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "الآية التالية",
                            tint = if (audioState.currentAyah < config.toAyah) TextPrimary else TextSecondary.copy(alpha = 0.3f)
                        )
                    }
                }

                if (audioState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = audioState.errorMessage,
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Customization Tool Panels Navigation Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf(
                Pair(0, "📖 السورة والآيات"),
                Pair(1, "🎙️ القارئ"),
                Pair(2, "🎨 الخلفية"),
                Pair(3, "📐 النمط والأبعاد")
            )

            tabs.forEach { (index, title) ->
                FilterChip(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    label = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Emerald500,
                        selectedLabelColor = Color.Black,
                        containerColor = DarkSurfaceElevated,
                        labelColor = TextPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = DarkBorder,
                        selectedBorderColor = Emerald400,
                        enabled = true,
                        selected = selectedTab == index
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }

        // Tab Content Panels
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // TAB 0: SURAH & AYAH RANGE
                        Text(
                            text = "اختيار السورة ونطاق الآيات",
                            color = TextGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Surah Picker Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { showSurahPickerSheet = true }
                                .testTag("surah_picker_card"),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = Emerald400
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "سورة ${currentSurah.nameArabic}",
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "${currentSurah.nameEnglish} • ${currentSurah.versesCount} آية (${currentSurah.revelationType})",
                                            color = TextSecondary,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "تغيير السورة",
                                    tint = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Ayah Range Selectors
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // From Ayah
                            Column(modifier = Modifier.weight(1f)) {
                                Text("من الآية: ${config.fromAyah}", color = TextPrimary, fontSize = 13.sp)
                                Slider(
                                    value = config.fromAyah.toFloat(),
                                    onValueChange = { newFrom ->
                                        val f = newFrom.toInt().coerceIn(1, currentSurah.versesCount)
                                        val t = maxOf(f, config.toAyah)
                                        onSetAyahRange(f, t)
                                    },
                                    valueRange = 1f..currentSurah.versesCount.toFloat(),
                                    steps = (currentSurah.versesCount - 2).coerceAtLeast(0),
                                    colors = SliderDefaults.colors(
                                        thumbColor = Emerald400,
                                        activeTrackColor = Emerald400
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // To Ayah
                            Column(modifier = Modifier.weight(1f)) {
                                Text("إلى الآية: ${config.toAyah}", color = TextPrimary, fontSize = 13.sp)
                                Slider(
                                    value = config.toAyah.toFloat(),
                                    onValueChange = { newTo ->
                                        val t = newTo.toInt().coerceIn(1, currentSurah.versesCount)
                                        val f = minOf(t, config.fromAyah)
                                        onSetAyahRange(f, t)
                                    },
                                    valueRange = 1f..currentSurah.versesCount.toFloat(),
                                    steps = (currentSurah.versesCount - 2).coerceAtLeast(0),
                                    colors = SliderDefaults.colors(
                                        thumbColor = Gold400,
                                        activeTrackColor = Gold400
                                    )
                                )
                            }
                        }
                    }

                    1 -> {
                        // TAB 1: RECITERS
                        Text(
                            text = "اختيار القارئ والتلاوة",
                            color = TextGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            QuranRepository.allReciters.forEach { reciter ->
                                val isSelected = reciter.id == config.reciterId
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onSelectReciter(reciter.id) }
                                        .testTag("reciter_item_${reciter.id}"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Emerald500.copy(alpha = 0.2f) else DarkSurfaceCard
                                    ),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Emerald400) else null
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Mic,
                                                contentDescription = null,
                                                tint = if (isSelected) Emerald400 else TextSecondary
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = reciter.nameArabic,
                                                    color = if (isSelected) TextGold else TextPrimary,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 15.sp
                                                )
                                                Text(
                                                    text = "${reciter.riwayah} • ${reciter.bitRate}",
                                                    color = TextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "محدد",
                                                tint = Emerald400
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // TAB 2: BACKGROUNDS & EFFECTS
                        Text(
                            text = "الخلفيات والمؤثرات البصرية",
                            color = TextGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Background Themes Chips
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuranRepository.backgroundThemes.forEach { theme ->
                                val isSelected = theme.id == config.backgroundId
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onSelectBackground(theme.id) },
                                    label = { Text(theme.nameArabic) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.Black else TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Gold400,
                                        selectedLabelColor = Color.Black,
                                        containerColor = DarkSurfaceCard,
                                        labelColor = TextPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Motion Effect Selector
                        Text(
                            text = "حركة المؤثر التفاعلي (Motion Effect):",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val motionEffects = listOf(
                            Pair("particles", "جزيئات ذهبية"),
                            Pair("stars", "نجوم متلألئة"),
                            Pair("rain", "مطر هادئ"),
                            Pair("zoom", "تكبير سينمائي"),
                            Pair("none", "بدون حركة")
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            motionEffects.forEach { (id, label) ->
                                FilterChip(
                                    selected = config.motionEffect == id,
                                    onClick = { onSetMotionEffect(id) },
                                    label = { Text(label, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Emerald500,
                                        selectedLabelColor = Color.Black,
                                        containerColor = DarkSurfaceCard,
                                        labelColor = TextPrimary
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dark Overlay Slider
                        Text(
                            text = "شفافية طبقة التعتيم لقراءة واضحة: ${(config.darkOverlayAlpha * 100).toInt()}%",
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                        Slider(
                            value = config.darkOverlayAlpha,
                            onValueChange = onSetDarkOverlay,
                            valueRange = 0.1f..0.85f,
                            colors = SliderDefaults.colors(
                                thumbColor = Emerald400,
                                activeTrackColor = Emerald400
                            )
                        )
                    }

                    3 -> {
                        // TAB 3: ASPECT RATIO, TYPOGRAPHY & TOGGLES
                        Text(
                            text = "الأبعاد والخط والتنسيق",
                            color = TextGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Aspect Ratio Switcher
                        Text("مقاس الفيديو والريلز:", color = TextPrimary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Pair("9:16", "ريلز / شورتس (9:16)"),
                                Pair("1:1", "مربع إنستغرام (1:1)"),
                                Pair("16:9", "شاشة عرضية (16:9)")
                            ).forEach { (ratio, label) ->
                                FilterChip(
                                    selected = config.aspectRatio == ratio,
                                    onClick = { onSetAspectRatio(ratio) },
                                    label = { Text(label, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Gold400,
                                        selectedLabelColor = Color.Black,
                                        containerColor = DarkSurfaceCard,
                                        labelColor = TextPrimary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Font size slider
                        Text(
                            text = "حجم خط الآيات: ${config.fontSize.toInt()}sp",
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                        Slider(
                            value = config.fontSize,
                            onValueChange = onSetFontSize,
                            valueRange = 18f..40f,
                            colors = SliderDefaults.colors(
                                thumbColor = Emerald400,
                                activeTrackColor = Emerald400
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Font Style
                        Text("نوع الخط العربي:", color = TextPrimary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                Pair("uthmani", "المصحف"),
                                Pair("amiri", "أميري"),
                                Pair("naskh", "نسخ"),
                                Pair("ruqah", "رقعة")
                            ).forEach { (fontKey, fontTitle) ->
                                FilterChip(
                                    selected = config.fontFamilyType == fontKey,
                                    onClick = { onSetFontFamily(fontKey) },
                                    label = { Text(fontTitle, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Emerald500,
                                        selectedLabelColor = Color.Black,
                                        containerColor = DarkSurfaceCard,
                                        labelColor = TextPrimary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Text Color Chips
                        Text("لون خط الآيات:", color = TextPrimary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(
                                Pair("#FFFFFF", "أبيض ناصع"),
                                Pair("#FFDF7D", "ذهب إسلامي"),
                                Pair("#FEF08A", "عاجي دافئ"),
                                Pair("#6EE7B7", "أخضر زمردي")
                            ).forEach { (colorHex, name) ->
                                val isSelected = config.textColorHex == colorHex
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(colorHex)))
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) Emerald400 else Color.Gray,
                                            shape = CircleShape
                                        )
                                        .clickable { onSetTextColor(colorHex) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Toggles row
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Translation Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("إظهار الترجمة الإنجليزية للآية", color = TextPrimary, fontSize = 13.sp)
                                Switch(
                                    checked = config.showTranslation,
                                    onCheckedChange = { onToggleTranslation() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Emerald400,
                                        checkedTrackColor = Emerald900
                                    )
                                )
                            }

                            // Islamic Frame Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("إظهار إطار زخرفة إسلامية ذهبي", color = TextPrimary, fontSize = 13.sp)
                                Switch(
                                    checked = config.showFrame,
                                    onCheckedChange = { onToggleFrame() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Gold400,
                                        checkedTrackColor = Color(0xFF332505)
                                    )
                                )
                            }

                            // Surah Badge Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("إظهار شريط اسم السورة في الأعلى", color = TextPrimary, fontSize = 13.sp)
                                Switch(
                                    checked = config.showSurahBadge,
                                    onCheckedChange = { onToggleSurahBadge() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Emerald400,
                                        checkedTrackColor = Emerald900
                                    )
                                )
                            }

                            // Reciter Badge Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("إظهار اسم القارئ في الأسفل", color = TextPrimary, fontSize = 13.sp)
                                Switch(
                                    checked = config.showReciterBadge,
                                    onCheckedChange = { onToggleReciterBadge() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Emerald400,
                                        checkedTrackColor = Emerald900
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet to choose Surahs
    if (showSurahPickerSheet) {
        val pickerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        ModalBottomSheet(
            onDismissRequest = { showSurahPickerSheet = false },
            sheetState = pickerSheetState,
            containerColor = DarkSurfaceElevated
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "اختر سورة من القرآن الكريم",
                    color = TextGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(QuranRepository.allSurahs, key = { it.number }) { surah ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onSelectSurah(surah.number)
                                    coroutineScope.launch {
                                        pickerSheetState.hide()
                                        showSurahPickerSheet = false
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (surah.number == config.surahNumber) Emerald500.copy(alpha = 0.2f) else DarkSurfaceCard
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${surah.number}.",
                                        color = Emerald400,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "سورة ${surah.nameArabic}",
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "${surah.versesCount} آية (${surah.revelationType})",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for Sharing / Exporting
    if (showShareSheet) {
        val shareSheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showShareSheet = false },
            sheetState = shareSheetState,
            containerColor = DarkSurfaceElevated
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "تصدير ومشاركة ريلز القرآن",
                    color = TextGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Option 1: Export HD Image Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            coroutineScope.launch {
                                shareSheetState.hide()
                                showShareSheet = false
                                Toast.makeText(context, "جاري تحضير بطاقة ريلز فائقة الدقة...", Toast.LENGTH_SHORT).show()
                                ReelExporter.exportAndShareImageCard(context, config, activeAyah)
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Gold400),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "مشاركة كبطاقة ريلز مصورة عالية الدقة (HD)",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "صورة سينمائية جاهزة للنشر على إنستغرام، تيك توك، واتساب وسناب شات",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Option 2: Share Formatted Quran Text & Audio Link
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            coroutineScope.launch {
                                shareSheetState.hide()
                                showShareSheet = false
                                ReelExporter.shareReelText(context, config, activeAyah)
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Emerald400),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "مشاركة الآيات مع رابط التلاوة الصوتية",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "نص الآيات بالرسم العثماني مع اسم القارئ ورابط الاستماع المباشر",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Fullscreen Reel Preview Dialog
    if (showFullscreenReel) {
        FullscreenReelDialog(
            config = config,
            activeAyah = activeAyah,
            allAyahs = allAyahs,
            audioState = audioState,
            onTogglePlayPause = onTogglePlayPause,
            onNextAyah = onNextAyah,
            onPreviousAyah = onPreviousAyah,
            onDismiss = { showFullscreenReel = false }
        )
    }
}
