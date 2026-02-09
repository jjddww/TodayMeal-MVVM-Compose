package com.dawoon.todaymeal.network.model

data class ElsTimetableResponseDto(
    val elsTimetable: List<ElsTimetableBlockDto>? = null
)

data class ElsTimetableBlockDto(
    val head: List<NeisHeadItemDto>? = null,
    val row: List<ElsTimetableRowDto>? = null
)

data class ElsTimetableRowDto(
    val ATPT_OFCDC_SC_CODE: String? = null,
    val SD_SCHUL_CODE: String? = null,
    val SCHUL_NM: String? = null,

    val AY: String? = null,
    val SEM: String? = null,
    val ALL_TI_YMD: String? = null,

    val GRADE: String? = null,
    val CLASS_NM: String? = null,
    val PERIO: String? = null,
    val ITRT_CNTNT: String? = null,

    val LOAD_DTM: String? = null
)
