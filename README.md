# GridgeTest(외주 개발 모의 테스트-SNS)




`logback-spring.xml`

logs 폴더에 로그 기록을 어떤 형식으로 남길 것인지 설정한다. logs 폴더에 어떻게 기록이 남겨져 있는지 확인해보시라. (커스텀 하지 않아도 된다면`logback-spring.xml` 를 수정할 필요는 없다.)

### src - main - java

`com.example.demo` 패키지에는 크게 `config` 폴더, `src` 폴더와 이 프로젝트의 시작점인 `DemoApplication.java`가 있다.

`DemoApplication.java` 은 스프링 부트 프로젝트의 시작을 알리는 `@SpringBootApplication` 어노테이션을 사용하고 있다. (구글링 통해 `@SpringBootApplication`의 다른 기능도 살펴보자.)

`src`폴더에는 실제 **API가 동작하는 프로세스**를 담았고 `config` 폴더에는 `src`에서 필요한 Secret key, Base 클래스, 상수 클래스를, `util` 폴더에는 JWT, 암호화, 정규표현식 등의 클래스를 모아놨다.

`src`를 자세하게 살펴보자. `src`는 각 **도메인**별로 패키지를 구분해 놓는다. 현재는 `user` 도메인과 `test` 도메인이 있다. **도메인**이란 게시글, 댓글, 회원, 정산, 결제 등 소프트웨어에 대한 요구사항 혹은 문제 영역이라고 생각하면 된다.

이 도메인들은 API 통신에서 어떤 프로세스로 처리되는가? API 통신의 기본은 Request → Response이다. 스프링 부트에서 **어떻게 Request를 받아서, 어떻게 처리하고, 어떻게 Response 하는지**를 중점적으로 살펴보자. 전반적인 API 통신 프로세스는 다음과 같다.

> **Request** → `XXXController.java`(=Router+Controller) → `Service` (CUD) / `Provider` (R) (=Business Logic) → `Dao` (DB) → **Response**

#### 1. Controller / `UserController.java`  / @RestController

> 1) API 통신의 **Routing** 처리
> 2) Request를 다른 계층에 넘기고 처리된 결과 값을 Response 해주는 로직
>  + Request의 **형식적 Validation** 처리 (DB를 거치지 않고도 검사할 수 있는)

**1) `@Autowired`**

UserController의 생성자에 `@Autowired` 어노테이션이 붙어있다. 이는 **의존성 주입**을 위한 것으로, `UserController`  뿐만 아니라 다음에 살펴볼 `UserService`, `UserProvider`의 생성자에도 각각 붙어 있는 것을 확인할 수 있다. 간단히 요약하면 객체 생성을 자동으로 해주는 역할이다. 자세한 프로세스는 구글링을 통해 살펴보자.

나머지 어노테이션들 역시 구글링을 통해 이해하자.

**2) `BaseResponse`**

Response할 때, 공통 부분은 묶고 다른 부분은 제네릭을 통해 구현함으로써 반복되는 코드를 줄여준다. (`BaseResponse.java` 코드 살펴 볼 것. 여기에 쓰이는`BaseResponseStatus` 는 `enum`을 통해 Status 값을 관리하고 있다.)

**3) 메소드 네이밍룰**

이 템플릿에서는 사용되는 메소드 명명 규칙은 다음과 같다.

> HTTP Method + 핵심 URI

- **GET** `/users` 를 처리하는 메소드명 → getUsers
- **PATCH** `/users` 를 처리하는 메소드명 →patchUsers

항상 이 규칙을 따라야 하는 것은 아니지만, 네이밍은 통일성 있게 해주는 게 좋다.

**4) Res, Req 네이밍룰**

각 메소드에서 사용되는 Res, Req 모델의 명명 규칙도 메소드 명과 비슷하다.

> HTTP Method + 핵심 URI +**Res/Req**

**Patch** `/users/:userId` → PatchUserRes / PatchUserReq

이 Res, Req 모델은 `(도메인명) / models` 폴더에 만들면 된다.

#### 2. Service 와 Provider / `UserService.java` `UserProvider.java` / @Service

> 1) **비즈니스 로직**을 다루는 곳 (DB 접근[CRUD], DB에서 받아온 것 형식화)
>  + Request의 **의미적** **Validation** 처리 (DB를 거쳐야 검사할 수 있는)

`Service`와 `Provider`는 비즈니스 로직을 다루는 곳이다. **CRUD** 중 **R(Read)** 에 해당하는 코드가 긴 경우가 많기 때문에 **R(Read)** 만 따로 분리해 `Service`는 **CUD(Create, Update, Delete)** 를, `Provider`는 **R(Read)** 를 다루도록 했다. 유지 보수가 용이해진다.

`Provider`
> **R(Read)** 와 관련된 곳이다. DB에서 select 해서 얻어온 값을 가공해서 뱉어준다.

`Service`
> **CUD(Create, Update, Delete)** 와 관련된 곳이다. **CUD**에서 **R**이 필요한 경우가 있는데, 그럴 때는 `Provider`에 구성되어 있는 것을 `Service`에서 사용하면 된다.

**1) 메소드명**

메소드의 prefix로 다음 규칙을 따르고 있다.

C → createXXX `createInfo`

R → retrieveXXX `retrieveInfoList`

U → updateXXX `updateInfo`

D → deleteXXX `deleteInfo`

**2) BaseException**

`BaseException`을 통해 `Service`나 `Provider`에서 `Controller`에 Exception을 던진다. 마찬가지로 Status 값은 `BaseResponseStatus` 의 `enum`을 통해 관리한다.

#### 3. DAO / `UserDao.java`
JdbcTemplate을 사용하여 구성되어 있다. 자세한 내용은 이곳 [공식 문서](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html) 와 템플릿의 기본 예제를 참고하자.

