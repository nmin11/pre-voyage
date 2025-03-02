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
- [ ] Admin의 회원 권한 부여 기능 구현
- [ ] 댓글 CRUD API 구현
- [ ] Thymeleaf를 활용해서 기본적인 UI 구현
- [ ] 커피 주문 시스템 관련 API 구현

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
