package com.dawoon.todaymeal.network.model

data class SchoolInfoResponseDto(
    val schoolInfo: List<SchoolInfoBlockDto>? = null
)

data class SchoolInfoBlockDto(
    val head: List<NeisHeadItemDto>? = null,
    val row: List<SchoolRowDto>? = null
)

data class SchoolRowDto(
    val ATPT_OFCDC_SC_CODE: String? = null,
    val ATPT_OFCDC_SC_NM: String? = null,
    val SD_SCHUL_CODE: String? = null,
    val SCHUL_NM: String? = null,
    val ENG_SCHUL_NM: String? = null,
    val ORG_RDNMA: String? = null,
    val ORG_RDNDA: String? = null,
    val ORG_TELNO: String? = null,
    val HMPG_ADRES: String? = null,
    val LOAD_DTM: String? = null
)