## 사용된 기술

- Spring Boot
- Spring MVC
- Spring JDBC
- MYSQL - SQL
- thymeleaf 템플릿 엔진

## 아키텍처

'''
                      Spring Core
                      Spring MVC             Spring JDBC   MySQL
브라우저 ---- 요청 ---> Controller --> Service ----> DAO --> DB
 <-- 응답 ---템플릿<--             <--           <--
                <--------------- layer간에 데이터 전송은 DTO -->
'''