# side-backend

**사부작**은 지역 기반 모임 관리 및 활동을 돕는 웹 서비스입니다.

## 1. 기술 스택

<img width="635" alt="사부작 기술 스택" src="https://github.com/SIDETEAM001/side-backend/assets/53341392/69f26ce4-1054-4eba-8db8-702621305335">

## 2. 아키텍처

### AWS 인프라 구성
<img width="635" alt="사부작 아키텍쳐" src="https://github.com/SIDETEAM001/side-backend/assets/53341392/43795de7-07e6-462b-b33a-e9746eb9f3bd">

### CI/CD

<img width="635" alt="사부작 CI CD" src="https://github.com/SIDETEAM001/side-backend/assets/53341392/4afea87e-276c-429d-99d6-f154e5fbf58e">

- `Github Actions`를 사용하여 CI/CD 파이프라인을 구축하였습니다.
- CI/CD 결과를 확인하기 위해 Slack으로 알림을 받을 수 있도록 설정하였습니다.
- `main` 브랜치에 푸시가 발생하면 자동으로 빌드 및 테스트를 수행하고, 테스트가 성공하면 `deploy` 브랜치로 배포합니다.
- `deploy` 브랜치에 푸시가 발생하면 자동으로 배포 스크립트를 실행하여 배포를 수행합니다.

## 3. 프로젝트 구조

```
📁 src
|_ 📁 main
|_ |_ 📁 domain # API 기능
|_ |_ 📁 global # 공통 기능
|_ |_ 📁 infra # 외부 기능
```
