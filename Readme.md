## 프로젝트 목적


## 설명



## 사용된 기술

- Spring Boot
- Spring MVC
- Spring JDBC
- MYSQL - SQL
- thymeleaf 템플릿 엔진

## 아키텍처

1. 요청 흐름

        브라우저에서 요청이 발생합니다.
        요청은 Controller로 전달됩니다.
        Controller는 Service 계층으로 요청을 전달합니다.
        Service 계층은 **DAO (Data Access Object)**를 통해 데이터베이스와 상호작용합니다.
        DAO는 MySQL 데이터베이스에 접근하여 필요한 작업을 수행합니다.

2. 응답 흐름

    DAO에서 데이터를 가져와 Service 계층으로 전달합니다.
    Service 계층은 데이터를 Controller로 전달합니다.
    Controller는 템플릿 엔진을 사용하여 데이터를 뷰에 전달합니다.
    브라우저에 응답이 반환됩니다.

3. 데이터전송
        레이어 간에 데이터는 **DTO (Data Transfer Object)**를 통해 전송됩니다.
