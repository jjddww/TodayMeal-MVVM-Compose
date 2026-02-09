package com.dawoon.todaymeal.network.model

data class MealServiceDietInfoResponseDto(
    val mealServiceDietInfo: List<MealServiceDietInfoBlockDto>? = null
)

data class MealServiceDietInfoBlockDto(
    val head: List<NeisHeadItemDto>? = null,
    val row: List<MealRowDto>? = null
)

data class MealRowDto(
    val ATPT_OFCDC_SC_CODE: String? = null,
    val SD_SCHUL_CODE: String? = null,
    val SCHUL_NM: String? = null,

    val MMEAL_SC_CODE: String? = null,
    val MMEAL_SC_NM: String? = null,

    val MLSV_YMD: String? = null,
    val DDISH_NM: String? = null,
    val ORPLC_INFO: String? = null,
    val CAL_INFO: String? = null,
    val NTR_INFO: String? = null,

    val LOAD_DTM: String? = null
)