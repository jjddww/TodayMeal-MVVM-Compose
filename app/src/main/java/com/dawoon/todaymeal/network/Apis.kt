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


//    interface NeisApiService {
//
//        // ======================
//        // 초등학교 시간표
//        // ======================
//        @GET("elsTimetable")
//        suspend fun getElsTimetable(
//            @Query("KEY") key: String,
//            @Query("Type") type: String = "json",
//            @Query("pIndex") pIndex: Int = 1,
//            @Query("pSize") pSize: Int = 1000,
//
//            @Query("ATPT_OFCDC_SC_CODE") atptCode: String,
//            @Query("SD_SCHUL_CODE") schoolCode: String,
//
//            @Query("AY") ay: String,               // 학년도 (예: 2025)
//            @Query("SEM") sem: String,              // 학기 (1, 2)
//            @Query("ALL_TI_YMD") ymd: String,       // 날짜 (yyyyMMdd)
//
//            @Query("GRADE") grade: String,          // 학년
//            @Query("CLASS_NM") classNm: String      // 반
//        ): Response<ElsTimetableResponseDto>
//
//
//        // ======================
//        // 중학교 시간표
//        // ======================
//        @GET("misTimetable")
//        suspend fun getMisTimetable(
//            @Query("KEY") key: String,
//            @Query("Type") type: String = "json",
//            @Query("pIndex") pIndex: Int = 1,
//            @Query("pSize") pSize: Int = 1000,
//
//            @Query("ATPT_OFCDC_SC_CODE") atptCode: String,
//            @Query("SD_SCHUL_CODE") schoolCode: String,
//
//            @Query("AY") ay: String,
//            @Query("SEM") sem: String,
//            @Query("ALL_TI_YMD") ymd: String,
//
//            @Query("GRADE") grade: String,
//            @Query("CLASS_NM") classNm: String
//        ): Response<MisTimetableResponseDto>
//
//
//        // ======================
//        // 고등학교 시간표
//        // ======================
//        @GET("hisTimetable")
//        suspend fun getHisTimetable(
//            @Query("KEY") key: String,
//            @Query("Type") type: String = "json",
//            @Query("pIndex") pIndex: Int = 1,
//            @Query("pSize") pSize: Int = 1000,
//
//            @Query("ATPT_OFCDC_SC_CODE") atptCode: String,
//            @Query("SD_SCHUL_CODE") schoolCode: String,
//
//            @Query("AY") ay: String,
//            @Query("SEM") sem: String,
//            @Query("ALL_TI_YMD") ymd: String,
//
//            @Query("GRADE") grade: String,
//            @Query("CLASS_NM") classNm: String
//            // 필요하면 여기 아래에
//            // @Query("DDDEP_NM") dddepNm: String?
//            // @Query("ORD_SC_NM") ordScNm: String?
//            // 같은 고등학교 전용 조건 추가 가능
//        ): Response<HisTimetableResponseDto>
//
//
//        // ======================
//        // 학사일정
//        // ======================
//        @GET("SchoolSchedule")
//        suspend fun getSchoolSchedule(
//            @Query("KEY") key: String,
//            @Query("Type") type: String = "json",
//            @Query("pIndex") pIndex: Int = 1,
//            @Query("pSize") pSize: Int = 1000,
//
//            @Query("ATPT_OFCDC_SC_CODE") atptCode: String,
//            @Query("SD_SCHUL_CODE") schoolCode: String,
//
//            // 단일 날짜 조회
//            @Query("AA_YMD") aaYmd: String? = null,
//
//            // 기간 조회 (보통 이걸 씀)
//            @Query("AA_FROM_YMD") fromYmd: String? = null,
//            @Query("AA_TO_YMD") toYmd: String? = null,
//
//            @Query("AY") ay: String? = null          // 학년도
//        ): Response<SchoolScheduleResponseDto>
//    }

}