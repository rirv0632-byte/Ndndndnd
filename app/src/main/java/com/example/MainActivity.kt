package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.QuranBrowserScreen
import com.example.ui.screens.SavedProjectsScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Gold400
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    QuranReelsApp()
                }
            }
        }
    }
}

@Composable
fun QuranReelsApp(viewModel: StudioViewModel = viewModel()) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val config by viewModel.reelConfig.collectAsState()
    val activeAyah by viewModel.activeAyah.collectAsState()
    val allAyahs by viewModel.versesList.collectAsState()
    val audioState by viewModel.audioState.collectAsState()
    val isLoadingVerses by viewModel.isLoadingVerses.collectAsState()
    val filteredSurahs by viewModel.filteredSurahs.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val savedProjects by viewModel.savedProjects.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurfaceElevated,
                contentColor = TextPrimary,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "الاستوديو",
                            tint = if (selectedTab == 0) Gold400 else TextSecondary
                        )
                    },
                    label = {
                        Text(
                            text = "الاستوديو",
                            fontSize = 12.sp,
                            color = if (selectedTab == 0) Gold400 else TextSecondary
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Emerald500.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.testTag("nav_tab_studio")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "المصحف",
                            tint = if (selectedTab == 1) Emerald400 else TextSecondary
                        )
                    },
                    label = {
                        Text(
                            text = "المصحف",
                            fontSize = 12.sp,
                            color = if (selectedTab == 1) Emerald400 else TextSecondary
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Emerald500.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.testTag("nav_tab_quran")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "مشاريعي",
                            tint = if (selectedTab == 2) Gold400 else TextSecondary
                        )
                    },
                    label = {
                        Text(
                            text = "مشاريعي",
                            fontSize = 12.sp,
                            color = if (selectedTab == 2) Gold400 else TextSecondary
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Emerald500.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.testTag("nav_tab_projects")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkSurface)
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> StudioScreen(
                    config = config,
                    activeAyah = activeAyah,
                    allAyahs = allAyahs,
                    audioState = audioState,
                    isLoadingVerses = isLoadingVerses,
                    onSelectSurah = { viewModel.selectSurah(it) },
                    onSetAyahRange = { from, to -> viewModel.setAyahRange(from, to) },
                    onSelectReciter = { viewModel.selectReciter(it) },
                    onSelectBackground = { viewModel.selectBackground(it) },
                    onSetAspectRatio = { viewModel.setAspectRatio(it) },
                    onSetFontSize = { viewModel.setFontSize(it) },
                    onSetFontFamily = { viewModel.setFontFamilyType(it) },
                    onSetTextColor = { viewModel.setTextColorHex(it) },
                    onToggleTranslation = { viewModel.toggleTranslation() },
                    onToggleSurahBadge = { viewModel.toggleSurahBadge() },
                    onToggleReciterBadge = { viewModel.toggleReciterBadge() },
                    onToggleFrame = { viewModel.toggleFrame() },
                    onSetMotionEffect = { viewModel.setMotionEffect(it) },
                    onSetDarkOverlay = { viewModel.setDarkOverlayAlpha(it) },
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onNextAyah = { viewModel.nextAyah() },
                    onPreviousAyah = { viewModel.previousAyah() },
                    onSeekAudio = { viewModel.seekAudio(it) },
                    onSaveProject = { viewModel.saveCurrentProject() }
                )

                1 -> QuranBrowserScreen(
                    surahs = filteredSurahs,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    onSelectForStudio = { surahNum, from, to ->
                        viewModel.selectSurah(surahNum)
                        viewModel.setAyahRange(from, to)
                        selectedTab = 0
                    }
                )

                2 -> SavedProjectsScreen(
                    projects = savedProjects,
                    onLoadInStudio = { project ->
                        viewModel.loadProject(project)
                        selectedTab = 0
                    },
                    onDeleteProject = { project ->
                        viewModel.deleteProject(project)
                    }
                )
            }
        }
    }
}
