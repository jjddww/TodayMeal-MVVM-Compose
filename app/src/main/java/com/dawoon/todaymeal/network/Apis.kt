package com.dawoon.todaymeal.network

import com.dawoon.todaymeal.network.model.ElsTimetableResponseDto
import com.dawoon.todaymeal.network.model.HisTimetableResponseDto
import com.dawoon.todaymeal.network.model.MealServiceDietInfoResponseDto
import com.dawoon.todaymeal.network.model.MisTimetableResponseDto
import com.dawoon.todaymeal.network.model.SchoolInfoResponseDto
import com.dawoon.todaymeal.network.model.SchoolScheduleResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Apis {
    @GET("schoolInfo")
    suspend fun getSchoolInfo(
        @Query("KEY") key: String,
        @Query("Type") type: String = "json",
        @Query("SCHUL_NM") schoolName: String
    ): Response<SchoolInfoResponseDto>

    @GET("mealServiceDietInfo")
    suspend fun getMeal(
        @Query("KEY") key: String,
        @Query("Type") type: String = "json",
        @Query("pIndex") pIndex: Int = 1,
        @Query("pSize") pSize: Int = 100,
        @Query("ATPT_OFCDC_SC_CODE") atptCode: String,
        @Query("SD_SCHUL_CODE") schoolCode: String,
//        @Query("MLSV_YMD") ymd: String,
        @Query("MMEAL_SC_CODE") mealCode: String? = null,
        @Query("MLSV_FROM_YMD") fromYmd: String? = null,
        @Query("MLSV_TO_YMD") toYmd: String? = null
    ): Response<MealServiceDietInfoResponseDto>


    @GET("SchoolSchedule")
    suspend fun getSchoolSchedule(
        @Query("KEY") key: String,
        @Query("Type") type: String = "json",

        @Query("ATPT_OFCDC_SC_CODE") atptCode: String,
        @Query("SD_SCHUL_CODE") schoolCode: String,
        @Query("AA_FROM_YMD") fromYmd: String? = null,
        @Query("AA_TO_YMD") toYmd: String? = null,

        @Query("AY") ay: String? = null          // 학년도
    ): Response<SchoolScheduleResponseDto>



    @GET("elsTimetable")
    suspend fun getElsTimetable(
        @Query("KEY") key: String,
        @Query("Type") type: String = "json",
        @Query("ATPT_OFCDC_SC_CODE") atptCode: String,
        @Query("SD_SCHUL_CODE") schoolCode: String,
        @Query("GRADE") grade: String,          // 학년
        @Query("CLASS_NM") classNm: String,      // 반
        @Query("TI_FROM_YMD") fromYmd: String,
        @Query("TI_TO_YMD") toYmd: String,
    ): Response<ElsTimetableResponseDto>


    // ======================
    // 중학교 시간표
    // ======================
    @GET("misTimetable")
    suspend fun getMisTimetable(
        @Query("KEY") key: String,
        @Query("Type") type: String = "json",
        @Query("ATPT_OFCDC_SC_CODE") atptCode: String,
        @Query("SD_SCHUL_CODE") schoolCode: String,
        @Query("GRADE") grade: String,
        @Query("CLASS_NM") classNm: String,
        @Query("TI_FROM_YMD") fromYmd: String,
        @Query("TI_TO_YMD") toYmd: String
    ): Response<MisTimetableResponseDto>


    // ======================
    // 고등학교 시간표
    // ======================
    @GET("hisTimetable")
    suspend fun getHisTimetable(
        @Query("KEY") key: String,
        @Query("Type") type: String = "json",
        @Query("ATPT_OFCDC_SC_CODE") atptCode: String,
        @Query("SD_SCHUL_CODE") schoolCode: String,
        @Query("GRADE") grade: String,
        @Query("CLASS_NM") classNm: String,
        @Query("TI_FROM_YMD") fromYmd: String,
        @Query("TI_TO_YMD") toYmd: String,
    ): Response<HisTimetableResponseDto>

}