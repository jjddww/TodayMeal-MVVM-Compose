package com.dawoon.todaymeal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dawoon.todaymeal.util.PreferenceManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class PreferenceManagerTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var preferenceManager: PreferenceManager


    @Before
    fun setup() {
        hiltRule.inject()
        runTest {
            preferenceManager.clearAll()
        }
    }

    @Test
    fun saveSchool_should_store_all_school_information_and_classify_type() = runTest {

        preferenceManager.saveSchool("T10", "12345", "서울고등학교")

        Assert.assertEquals("T10", preferenceManager.getAtptCode())
        Assert.assertEquals("12345", preferenceManager.getSchoolCode())
        Assert.assertEquals("서울고등학교", preferenceManager.getSchoolName())
        Assert.assertEquals("HIGH", preferenceManager.getSchoolType())

        preferenceManager.clearAll()
        preferenceManager.saveSchool("B10", "678910", "테스트초등학교")
        Assert.assertEquals("ELEMENTARY", preferenceManager.getSchoolType())


    }

    @Test
    fun saveGradeAndClass_should_store_grade_and_class_numbers() = runTest {
        preferenceManager.saveGradeAndClass("3", "12")

        Assert.assertEquals("3", preferenceManager.getGrade())
        Assert.assertEquals("12", preferenceManager.getClass())
    }

    @Test
    fun clearAll_should_reset_all_values_to_default() = runTest {
        preferenceManager.saveSchool("B10", "678910", "테스트초등학교")
        preferenceManager.saveGradeAndClass("6", "1")

        preferenceManager.clearAll()

        Assert.assertEquals("", preferenceManager.getAtptCode())
        Assert.assertEquals("", preferenceManager.getSchoolName())
        Assert.assertEquals("", preferenceManager.getSchoolType())
        Assert.assertEquals("1", preferenceManager.getGrade()) // 기본값 1
        Assert.assertEquals("1", preferenceManager.getClass()) // 기본값 1
    }

    @Test
    fun getters_should_return_default_values_when_data_is_empty() = runTest {
        Assert.assertEquals("", preferenceManager.getAtptCode())
        Assert.assertEquals("", preferenceManager.getSchoolCode())
        Assert.assertEquals("1", preferenceManager.getGrade())
        Assert.assertEquals("1", preferenceManager.getClass())
    }

    @Test
    fun schoolNameFlow_should_emit_updated_school_name() = runTest {
        preferenceManager.saveSchool("T10", "1", "첫번째학교")
        Assert.assertEquals("첫번째학교", preferenceManager.schoolNameFlow.first())

        preferenceManager.saveSchool("T10", "2", "두번째학교")
        Assert.assertEquals("두번째학교", preferenceManager.schoolNameFlow.first())
    }
}