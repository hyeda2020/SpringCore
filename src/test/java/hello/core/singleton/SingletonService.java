package hello.core.singleton;

public class SingletonService {

    /** 
     * 객체를 미리 생성해두는 단순하면서도 안전한 싱글톤 패턴 사용
     * 하지만, 스프링 컨테이너는 싱글턴 패턴을 적용하지 않아도, 객체 인스턴스를 싱글톤으로 관리함
     * 즉, 스프링 컨테이너는 싱글톤 컨테이너 역할을 수행
     **/

    /**
     * 싱글톤 방식은 여러 클라이언트가 하나의 같은 객체 인스턴스를 공유하기 때문에
     * 싱글톤 객체는 상태를 무상태(stateless)로 설계해야 한다!
     **/

    private static final SingletonService instance = new SingletonService();

    public static SingletonService getInstance() {
        return instance;
    }

    private SingletonService() {
    }
}
