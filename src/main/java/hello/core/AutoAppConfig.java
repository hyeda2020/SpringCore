package hello.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = { "hello.core" }, // 컴포넌트 탐색 시작 패키지 위치 지정
        basePackageClasses = AutoAppConfig.class, // 컴포넌트 탐색 시작 클래스 지정
        /* 만약 컴포넌트 시작 패키지 위치를 지정하지 않으면 디폴트로 이 AutoAppConfig가 위치한 패키지를 탐색 시작 위치로 지정 */


        /** 기존 AppConfig도 포함하여 스캔해 Bean으로 등록해버리기 때문에
         *  ('@Configuriation'이 컴포넌트 스캔의 대상이 된 이유는 '@Configuration' 안에 '@Component'가 있기 때문)
         * 예제 코드로서 AppConfig는 남길 것이므로, 이런 경우는 Component 대상에서 제외 필요 **/
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {

}
