# 오늘 급식

## 🔎 오늘 급식은?
<p align="center">
  <img src="https://github.com/user-attachments/assets/e0c12519-2ca7-4661-9c57-a5453bff7e2e">
</p>

급식, 시간표, 학사일정을 조회할 수 있는 안드로이드 앱입니다.</br>
사용자 편의를 위해 위젯 기능과 다크모드 기능을 지원합니다.
</br></br></br>


## 🤖 아키텍처 & 기술 스택
* Architecture: MVVM
* Language: Kotlin
* UI: Jetpack Compose, Glance(위젯)
* Async / Concurrency: Coroutine, StateFlow
* Local Storage: Jetpack DataStore
* Network: Retrofit2, Okhttp
* DI: Hilt
* Test: JUnit5, MockK, Robolectric
* Server - 나이스 Open API 활용

</br>

## 🛠️ Tools
* Design - Figma
* API 명세 - Postman

</br>

## ⌛️ 개발 기간
2026년 2월 6일 ~ 2026년 2월 26일

</br></br></br>

## ⭐️ 기능 소개

### 학교 설정
첫 화면에서 학교와 학년, 반 정보를 설정합니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/f454dcde-bbff-4e8a-8908-2357c76e3562" width="30%">
  <img src="https://github.com/user-attachments/assets/0dee4878-dbd4-427c-8039-c9c68f387498" width="30%">
</p>

</br>

### 홈화면 - 메뉴 확인
메인 홈화면에서 드롭 다운 메뉴를 눌러 아침, 점심, 저녁 메뉴를 전환하며 확인할 수 있습니다.
또한 오늘 날짜로부터 앞 뒤로 60일까지의 메뉴 확인이 가능합니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/36b035db-470b-417c-bff0-a3e6057ccf1a" width="20%">
  <img src="https://github.com/user-attachments/assets/07a3bc0b-2240-48d8-a6a5-ee196bbbf11d" width="20%">
  <img src="https://github.com/user-attachments/assets/cf62852a-1c33-4a8c-83ff-7a9a226ea361" width="20%">
  <img src="https://github.com/user-attachments/assets/f8ddcab7-04e2-484b-a2fd-7d2c58531921" width="20%">
</p>

</br>

### 영양정보 확인
메뉴 밑의 영양정보 버튼을 통해 해당 식단의 칼로리 및 영양정보를 확인할 수 있습니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/e19aa360-e682-4532-844f-aed82b2f3789" width="30%">
  <img src="https://github.com/user-attachments/assets/430a2db1-ba75-4a97-8542-178a805a539c" width="30%">
</p>

</br></br>

### 공지사항
앱 이용 관련 공지사항 확인 화면입니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/2128f5ed-3ed5-47a7-9f7c-19fc2c8e4cac" width="30%">
  <img src="https://github.com/user-attachments/assets/8e1a4fa4-477e-45ac-b6f7-53623b97523e" width="30%">
</p>

</br></br>

### 시간표 확인
사용자가 설정한 학년과 반에 따른 시간표 정보를 불러옵니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/3ea69e91-2eee-4f64-a3dc-0ab092d127d0" width="30%">
  <img src="https://github.com/user-attachments/assets/7dba6684-d88b-4de9-8083-2bc56a5cac65" width="30%">
</p>

</br></br>

### 주간 급식
해당 달의 급식을 주별로 확인할 수 있는 화면입니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/e8197d8d-fe81-4ba8-9d23-068ff427155c" width="30%">
  <img src="https://github.com/user-attachments/assets/1bff1e7a-7f34-474d-83b8-bd6c3bac2099" width="30%">
</p>

</br></br>

### 학사일정
한 달 단위로 학사일정을 확인할 수 있습니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/ffe50b75-6005-4856-a21a-78037de1f4e1" width="30%">
  <img src="https://github.com/user-attachments/assets/afe8385c-0209-42d6-8fd0-432c111b6560" width="30%">
</p>


### 문의기능
개발자에게 문의 메일을 보낼 수 있는 기능입니다.
이메일 주소를 클릭하면 메일 앱으로 연결되고, 매일 앱이 없다면 이메일이 클립보드에 복사됩니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/0a809276-b3e2-4ddf-9903-030f11839d17" width="30%">
  <img src="https://github.com/user-attachments/assets/29bb46f7-8738-4e89-959f-8a88006ec9f5" width="30%">
</p>

</br></br>

### 설정 정보 초기화
학교, 학년, 반 정보를 초기화하고 설정 화면으로 되돌아갑니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/195638ef-e1d0-48d2-9d44-5d210cf16617" width="30%">
  <img src="https://github.com/user-attachments/assets/22109568-f253-4c4e-bd58-e2a773350d12" width="30%">
</p>

</br></br>


### 위젯 기능
앱을 열지 않고 디바이스 홈화면에서 바로 그날의 급식을 확인할 수 있는 기능을 지원합니다.</br>
화살표를 클릭하여 아침, 점심, 저녁 정보를 확인할 수 있습니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/00cb8187-1d06-4721-b5df-0436af93cb49" width="40%">
</p>

</br></br>




