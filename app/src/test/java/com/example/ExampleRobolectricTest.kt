package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.ReelProjectEntity
import com.example.data.model.ReelConfig
import com.example.data.quran.QuranRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testAppNameResource() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        val appNameAr = context.getString(R.string.app_name_ar)
        assertEquals("Quran Reels Studio", appName)
        assertEquals("صانع ريلز القرآن", appNameAr)
    }

    @Test
    fun testQuranRepositoryStructure() {
        // All 114 Surahs must be present
        assertEquals(114, QuranRepository.allSurahs.size)
        val fatihah = QuranRepository.allSurahs[0]
        assertEquals("الفاتحة", fatihah.nameArabic)
        assertEquals(7, fatihah.versesCount)

        // Reciters must be available
        assertTrue(QuranRepository.allReciters.isNotEmpty())
        val alafasy = QuranRepository.allReciters.firstOrNull { it.id == "alafasy" }
        assertNotNull(alafasy)

        // Background themes
        assertTrue(QuranRepository.backgroundThemes.size >= 5)
    }

    @Test
    fun testRoomDatabaseSaveAndRetrieveProject() = runBlocking {
        val dao = database.reelProjectDao()
        val config = ReelConfig(
            surahNumber = 1,
            surahName = "الفاتحة",
            fromAyah = 1,
            toAyah = 7,
            reciterId = "alafasy"
        )
        val entity = ReelProjectEntity.fromConfig("مشروع الفاتحة التجريبي", config)
        val insertedId = dao.insertProject(entity)
        assertTrue(insertedId > 0)

        val retrieved = dao.getProjectById(insertedId)
        assertNotNull(retrieved)
        assertEquals("مشروع الفاتحة التجريبي", retrieved?.title)
        assertEquals(1, retrieved?.surahNumber)
        assertEquals(7, retrieved?.toAyah)

        val all = dao.getAllProjects().first()
        assertEquals(1, all.size)

        // Test delete
        retrieved?.let { dao.deleteProject(it) }
        val afterDelete = dao.getAllProjects().first()
        assertEquals(0, afterDelete.size)
    }
}
