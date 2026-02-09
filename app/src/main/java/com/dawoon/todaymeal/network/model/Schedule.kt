package com.dawoon.todaymeal.network.model

data class SchoolScheduleResponseDto(
    val SchoolSchedule: List<SchoolScheduleBlockDto>? = null
)

data class SchoolScheduleBlockDto(
    val head: List<NeisHeadItemDto>? = null,
    val row: List<SchoolScheduleRowDto>? = null
)

data class SchoolScheduleRowDto(
    val ATPT_OFCDC_SC_CODE: String? = null,
    val SD_SCHUL_CODE: String? = null,
    val AY: String? = null,

    val AA_YMD: String? = null,
    val EVENT_NM: String? = null,
    val EVENT_CNTNT: String? = null,
    val SBTR_DD_SC_NM: String? = null,

    val LOAD_DTM: String? = null
)
