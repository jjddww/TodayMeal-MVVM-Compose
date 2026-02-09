package com.dawoon.todaymeal.network.model

data class MisTimetableResponseDto(
    val misTimetable: List<MisTimetableBlockDto>? = null
)

data class MisTimetableBlockDto(
    val head: List<NeisHeadItemDto>? = null,
    val row: List<MisTimetableRowDto>? = null
)

data class MisTimetableRowDto(
    val ATPT_OFCDC_SC_CODE: String? = null,
    val SD_SCHUL_CODE: String? = null,
    val SCHUL_NM: String? = null,

    val AY: String? = null,
    val SEM: String? = null,
    val ALL_TI_YMD: String? = null,

    val DGHT_CRSE_SC_NM: String? = null,
    val GRADE: String? = null,
    val CLASS_NM: String? = null,
    val PERIO: String? = null,
    val ITRT_CNTNT: String? = null,

    val LOAD_DTM: String? = null
)
