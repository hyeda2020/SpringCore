# SpringCore
리포지토리 설명 : 인프런 김영한님의 강의 '스프링 핵심 원리 - 기본편' 스터디 정리  
https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8

# 1. 스프링이란?
자바 언어 기반 프레임워크로, 자바가 가진 객체 지향 프로그래밍의 장점을 활용하여 편리하게 애플리케이션을 개발할 수 있게 도와줌  
※ 객체 지향 프로그래밍의 핵심은 바로 '역할'과 '구현'의 분리를 통해 프로그램을 유연하고 변경 용이하게 개발하는 것!  

# 2. 스프링 컨테이너와 스프링 빈
[의존관계 주입을 고려하지 않은 주문 도메인 클래스 예시]
![OrderServicelmpl](https://github.com/hyeda2020/SpringCore/assets/139141270/16463f0d-c891-4730-bcb3-ba3befd766cc)
- 
    /* 주문 서비스 구현체 */
    public class OrderServiceImpl implements OrderService {
      // 할인정책 인터페이스에 의존하면서 동시에 고정 할인정책 구현 클래스에도 의존
      private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
      
      // ... 이하 생략
    }

[AppConfig 도입을 통한 사용 영역과 구성 영역 분리]
![page17image41872144](https://github.com/hyeda2020/SpringCore/assets/139141270/7441eb8f-6d31-413b-a4bf-49ee5365f59d)
- 
    /* 의존관계 주입을 위한 AppConfig 클래스 */
    public class AppConfig {
      public OrderService orderService() {
        return new OrderServiceImpl(new FixDiscountPolicy()); // 고정 할인 정책
      }
    }

    /* OrderServiceImpl - 생성자 주입 */
    public class OrderServiceImpl implements OrderService {
      private final DiscountPolicy discountPolicy;
      public OrderServiceImpl(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
      }
    }  
      
AppConfig 도입을 통해 OrderServiceImpl 입장에서는 생성자를 통해 어떤 구현 객체가 들어올지(주입될지) 알 필요가 없어졌으며  
OrderServiceImpl의 생성자를 통해서 어떤 구현 객체을 주입할지는 오직 외부(AppConfig)에서 결정함으로써  
OrderServiceImpl은 실행에만 집중 가능.  
또한, 할인 정책이 FixDiscountPolicy -> RateDiscountPolicy로 변경된다고 하여도  
클라이언트(OrderServiceImpl)의 코드는 변경할 필요 없이 오직 AppConfig 소스 변경만으로 유연하게 할인 정책 변경 가능.
  
[스프링 컨테이너 적용]  
<img width="833" alt="109380930-bbb14680-791a-11eb-99a0-f7b8a49875f8" src="https://github.com/hyeda2020/SpringCore/assets/139141270/d28f8544-fc66-4efd-8ab6-3227c03eb03b">
- 
기존엔 개발자가 AppConfig를 사용해서 직접 객체를 생성하고 의존 관계를 주입했다면  
스프링 컨테이너를 사용할 경우 `@Configuration` 애노테이션이 붙은 구성 정보를 지정해주면  
해당 구성 정보에 `@Bean` 이라 적힌 메서드를 모두 호출해서 반환된 객체를 스프링 컨테이너에 등록하여 의존 관계를 주입해줌  

    /* 스프링 컨테이너를 생성할 때 지정해주기 위한 구성 정보 설정 */
    @Configuration
    public class AppConfig {
      @Bean
      public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
      }
       
      @Bean
      public OrderService orderService() {
        return new OrderServiceImpl(
          memberRepository(),
          discountPolicy());
      }
      
      @Bean
      public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
      }
       
      @Bean
      public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
      }
    }
    

    
    /* 스프링 컨테이너 생성 */
    ApplicationContext applicationContext = 
      new AnnotationConfigApplicationContext(AppConfig.class); // AppConfig.class를 구성 정보로 지정

<img width="833" alt="스프링 컨테이너" src="https://github.com/hyeda2020/SpringCore/assets/139141270/dcd43789-d90d-43ff-a80b-1a93eb2b9e6a">  

스프링 컨테이너는 파라미터로 넘어온 설정 클래스 정보를 사용해서 `@Bean` 애노테이션이 붙은 메서드들을 스프링 빈으로 등록하고

<img width="833" alt="스프링 컨테이너" src="https://github.com/hyeda2020/SpringCore/assets/139141270/4d9a7e05-c4bd-41ee-b4f8-6b37c3528d7a">

설정 정보를 참고해서 자동적으로 의존관계를 주입(DI)해줌.

# 3. 싱글톤 컨테이너
[스프링 컨테이너 적용 전의 AppConfig의 객체 생성 방식]
![image](https://github.com/hyeda2020/SpringCore/assets/139141270/811dc203-8d86-4573-ae04-b5ef64fdf9b6)
- 
- 스프링 없는 순수한 DI 컨테이너인 AppConfig로는 요청이 올 때 마다 객체를 새로 생성
- 고객 트래픽이 초당 100건이 나오면 초당 100개 객체가 생성되고 소멸되므로 메모리 낭비가 심함  
 -> 해결방안은 해당 객체가 딱 1개만 생성되고, 이를 공유하도록 설계(싱글톤 패턴)  
 -> 하지만, 싱글톤 패턴을 적용하게 되면 추가적인 코드가 필요하고 구체 클래스에 의존하게 되며 private 생성자로 인한 자식 클래스 생성이 불가  

[스프링의 싱글톤 컨테이너]
![image](https://github.com/hyeda2020/SpringCore/assets/139141270/6a1e6f05-a164-4a0b-b6b8-c62fea8fdad6)
- 
스프링 컨테이너는 싱글턴 패턴을 직접 적용하지 않아도 객체 인스턴스를 싱글톤으로 관리해주며,  
덕분에 구체 클래스 의존 및 private 생성자로부터 자유롭게 싱글톤을 사용할 수 있음

[싱글톤 방식의 주의점]
-  

싱글톤 패턴은 여러 클라이언트가 하나의 같은 객체 인스턴스를 공유하기 때문에 싱글톤 객체는 반드시 무상태(stateless)로 설계해야 함.
1) 특정 클라이언트에 의존적인 필드가 있으면 안됨  
2) 특정 클라이언트가 값을 변경할 수 있는 필드가 있으면 안됨  
3) 가급적 읽기만 가능해야 함  
4) 필드 대신에 자바에서 공유되지 않는, 지역변수, 파라미터, ThreadLocal 등을 사용해야 함

[`@Configuration`과 싱글톤]
- 
스프링 컨테이너에 구성 정보로 지정된 AppConfig 소스를 보면 memberRepository()가 두 번 호출되는 것 처럼 보이나  
실제로는 memberRepository 인스턴스는 모두 같은 인스턴스가 공유되어 사용됨.  
      
    @Configuration
    public class AppConfig {
      @Bean
      public MemberService memberService() {
        return new MemberServiceImpl(memberRepository()); // memberRepository() 한번 호출
      }
       
      @Bean
      public OrderService orderService() {
        return new OrderServiceImpl(
          memberRepository(), // memberRepository() 두번 호출
          discountPolicy());
      }
      
      @Bean
      public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
      }
       
      @Bean
      public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
      }
    }
  
사실 스프링 컨테이너 AnnotationConfigApplicationContext에 파라미터로 넘겨진 구성 정보 AppConfig를 포함하여  
스프링에서는 `@Bean` 애노테이션이 붙은 클래스 자체가 아닌, 해당 클래스를 상속받은 임의의 다른 클래스를 빈으로 등록하고  
CGLIB라는 바이트코드 조작 라이브러리를 사용해서 그 임의의 다른 클래스가 바로 싱글톤이 되도록 보장함.  

![image](https://github.com/hyeda2020/SpringCore/assets/139141270/f549f9d5-2169-4515-a743-19bb4f0485d6)

※ `@Configuration` 을 적용하지 않고, `@Bean` 만 적용하면 어떻게 될까?
-
스프링 구성 정보에 `@Configuration` 을 붙이면 바이트코드를 조작하는 CGLIB 기술을 사용해서 싱글톤을 보장하지만,  
만약 다음과 같이 `@Bean`만 적용하면 해당 설정 정보 내의 객체들이 스프링 빈으로 등록은 되지만 싱글톤은 적용되지 않음.  

    public class AppConfig {
      @Bean
      public MemberService memberService() { // 해당 객체는 싱글톤 보장 X 
        return new MemberServiceImpl(memberRepository());
      }
      // ... 이하 생략
    }

따라서, 스프링 설정 정보는 항상 `@Configuration` 을 사용

# 4. 컴포넌트 스캔 및 의존관계 자동 주입
[`@Component` 애노테이션]
-  
컴포넌트 스캔은 이름 그대로 `@Component` 애노테이션이 붙은 클래스를 스캔해서 스프링 빈으로 등록.  
컴포넌트 스캔은 `@Component` 뿐만 아니라 다음과 내용도 추가로 대상에 포함함.  
- `@Component` : 컴포넌트 스캔에서 사용
- `@Controlller` : 스프링 MVC 컨트롤러에서 사용
- `@Service` : 스프링 비즈니스 로직에서 사용
- `@Repository` : 스프링 데이터 접근 계층에서 사용
- `@Configuration` : 스프링 설정 정보에서 사용

[`@Autowired` 애노테이션]
-  
`@Configuration` 애노테이션을 활용한 스프링 구성 정보에서는 `@Bean`으로 직접 설정 정보를 작성했고, 의존관계도 직접 명시할 수 있으나  
이를 클래스 안에서 해결해야 한다면 이런 설정 정보 자체가 없기 때문에, 클래스에서 자체적으로 `@Autowired` 애노테이션을 써서 의존관계를 자동으로 주입해야 함.  

    @Component
    public class MemberServiceImpl implements MemberService {
        private final MemberRepository memberRepository;
        
        @Autowired // 생성자에 @Autowired 를 지정하여 의존관계 자동 주입
        public MemberServiceImpl(MemberRepository memberRepository) {
            this.memberRepository = memberRepository;
        }
    }

[1. `@ComponentScan`]
-  
![image](https://github.com/hyeda2020/SpringCore/assets/139141270/fcb1164b-886e-4a6c-af98-30669ffae5c4)
`@ComponentScan` 은 `@Component` 가 붙은 모든 클래스를 스프링 빈으로 등록.  
(이때 스프링 빈의 기본 이름은 클래스명을 사용하되 맨 앞글자만 소문자를 사용)  

[2. `@Autowired` 의존관계 자동 주입]
-
![image](https://github.com/hyeda2020/SpringCore/assets/139141270/22c9c9e5-e71f-4642-81b7-c6e6660ee1ec)
생성자에 `@Autowired`를 지정하면, 스프링 컨테이너가 자동으로 해당 스프링 빈을 찾아서 주입  

[다양한 의존관계 주입 방법]
-
1. 생성자 주입(권장)  
※ 생성자가 딱 1개만 있으면 `@Autowired`를 생략해도 자동 주입 됨(물론 스프링 빈에만 해당)

  
       @Component  
       public class OrderServiceImpl implements OrderService {
   
           private final MemberRepository memberRepository;

           @Autowired
           public OrderServiceImpl(MemberRepository memberRepository) {
               this.memberRepository = memberRepository;
           }
        }
       
3. 수정자(setter) 주입(비권장)  
※ `@Autowired`의 기본 동작은 주입할 대상이 없으면 오류가 발생하므로  
주입할 대상이 없어도 동작하게 하려면 @Autowired(required = false) 로 지정

  
       @Component  
       public class OrderServiceImpl implements OrderService {

           private MemberRepository memberRepository;

           @Autowired
           public void setMemberRepository(MemberRepository memberRepository) {
               this.memberRepository = memberRepository;
           }
       }
   
5. 필드 주입(절대 쓰지 말자)
   
  
       @Component  
       public class OrderServiceImpl implements OrderService {

           @Autowired
           private MemberRepository memberRepository;
        }


   
