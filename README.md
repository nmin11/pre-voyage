# Tech Spec

## Summary

항해 플러스 8기의 본 과정을 수행하기 이전에, Kotlin + Spring Boot 기술의 사용법을 복습하기 위해 간단한 게시판 형태의 REST API 서버를 구축합니다. 본 사전 과제는 게시글에 대한 CRUD 및 인증/인가 기능을 포함합니다. 본 프로젝트는 과제 검토를 위해 AWS 배포 또한 진행되어야 합니다.

## Goals

- [x] Usecase Diagram 작성
- [x] API 명세서 작성
- [x] ERD 작성
- [x] 게시글 CRUD API 구현
- [x] 회원가입 API 구현
- [x] 로그인 API 구현
- [x] Admin의 회원 권한 부여 기능 구현
- [x] 댓글 CRUD API 구현
- [x] Thymeleaf를 활용해서 기본적인 UI 구현
- [x] 커피 주문 시스템 관련 API 구현
- [x] AWS 배포
- [x] 테스트 케이스 작성

## Plan

### Usecase Diagram

<img src="docs/usecase.drawio.svg">

### ERD

<img src="docs/erd.dbdiagramio.svg">

### API Specification

<a href="https://nmin1124.gitbook.io/pre-voyage" target="_blank">API 명세서 (by GitBook)</a>

## Milestones

1주차 (2/17 ~ 2/23)

- Draw.io 활용해서 Use Case Diagram 작성
- GitBook 활용해서 API 명세서 작성
- Draw.io 활용해서 ERD 작성

2주차 (2/24 ~ 3/2)

- 회원 인증 및 로그인 기능 구현
- 게시글 CRUD 기능 구현
- 기본적인 Thymeleaf 기반 UI 구현

3주차 (3/3 ~ 3/9)

- Admin의 회원 권한 부여 기능 구현
- 댓글 CRUD API 구현

4주차 (3/10 ~ 3/16)

- AWS 배포

5주차 (3/17 ~ 3/21)

- 커피 주문 시스템 관련 API 구현
- 테스트 케이스 작성

# Deployment

## 배포 프로세스

GitHub Actions를 활용해 EC2로 배포

1. 코드 체크아웃
2. JDK 셋업
3. Gradle 캐시 정리
4. Gradle 테스트
5. Gradle 빌드
6. scp 명령어를 활용해서 빌드된 JAR 파일을 EC2로 복사
7. EC2의 Spring 애플리케이션 재실행

## 참고 사항

- 로컬 환경에서도 간편하게 배포할 수 있도록 `deploy.sh` 파일 마련
  - 스크립트는 GitHub Actions 로직과 유사
  - 환경 변수 값들이 있으므로 git ignore 처리되어 있음
- 간편한 웹 UI를 활용할 수 있도록 'Thymeleaf' 프로필로 배포 중
- 4월 중 EC2 종료 예정

**배포 URL: http://3.35.173.239:8080/login**
