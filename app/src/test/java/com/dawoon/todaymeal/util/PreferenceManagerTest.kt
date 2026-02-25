package com.dawoon.todaymeal.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import kotlinx.coroutines.yield
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class PreferenceManagerTest {

    private lateinit var preferenceManager: PreferenceManager

    // 가짜 DataStore 사용
    private class InMemoryDataStore : DataStore<Preferences> {
        private val _data = MutableStateFlow(mutablePreferencesOf())
        override val data: Flow<Preferences> = _data

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            val newData = transform(_data.value)
            _data.value = newData.toMutablePreferences()
            return newData
        }
    }

    @Before
    fun setup() {
        preferenceManager = PreferenceManager(InMemoryDataStore())
    }

    @Test
    fun `saveSchool should store all school information and classify type`() = runTest {

        preferenceManager.saveSchool("T10", "12345", "서울고등학교")

        assertEquals("T10", preferenceManager.getAtptCode())
        assertEquals("12345", preferenceManager.getSchoolCode())
        assertEquals("서울고등학교", preferenceManager.getSchoolName())
        assertEquals("HIGH", preferenceManager.getSchoolType())

        preferenceManager.clearAll()
        preferenceManager.saveSchool("B10", "678910", "테스트초등학교")
        assertEquals("ELEMENTARY", preferenceManager.getSchoolType())


    }

    @Test
    fun `saveGradeAndClass should store grade and class numbers`() = runTest {
        preferenceManager.saveGradeAndClass("3", "12")

        assertEquals("3", preferenceManager.getGrade())
        assertEquals("12", preferenceManager.getClass())
    }

    @Test
    fun `clearAll should reset all values to default`() = runTest {
        preferenceManager.saveSchool("B10", "678910", "테스트초등학교")
        preferenceManager.saveGradeAndClass("6", "1")

        preferenceManager.clearAll()

        assertEquals("", preferenceManager.getAtptCode())
        assertEquals("", preferenceManager.getSchoolName())
        assertEquals("", preferenceManager.getSchoolType())
        assertEquals("1", preferenceManager.getGrade()) // 기본값 1
        assertEquals("1", preferenceManager.getClass()) // 기본값 1
    }

    @Test
    fun `getters should return default values when data is empty`() = runTest {
        assertEquals("", preferenceManager.getAtptCode())
        assertEquals("", preferenceManager.getSchoolCode())
        assertEquals("1", preferenceManager.getGrade())
        assertEquals("1", preferenceManager.getClass())
    }

    @Test
    fun `schoolNameFlow should emit updated school name`() = runTest {
        preferenceManager.saveSchool("T10", "1", "첫번째학교")
        assertEquals("첫번째학교", preferenceManager.schoolNameFlow.first())

        preferenceManager.saveSchool("T10", "2", "두번째학교")
        assertEquals("두번째학교", preferenceManager.schoolNameFlow.first())
    }
}