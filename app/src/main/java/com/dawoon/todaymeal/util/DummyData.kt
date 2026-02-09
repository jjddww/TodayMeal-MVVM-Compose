package com.dawoon.todaymeal.util

import com.dawoon.todaymeal.network.model.Notice

val dummyNotices = listOf(
    Notice(
        id = 1,
        title = "[공지] 서비스 점검 안내",
        contents = "안정적인 서비스 제공을 위해 2월 10일 02:00~04:00까지 시스템 점검이 진행됩니다."
    ),
    Notice(
        id = 2,
        title = "[업데이트] 신규 기능 업데이트",
        contents = "알림 설정 기능이 추가되었습니다. 설정 화면에서 자유롭게 관리할 수 있습니다."
    ),
    Notice(
        id = 3,
        title = "[공지] 이용약관 변경 안내",
        contents = "관련 법령 개정에 따라 이용약관이 일부 변경됩니다. 자세한 내용은 공지사항을 확인해주세요."
    )
)