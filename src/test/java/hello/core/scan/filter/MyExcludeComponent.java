package hello.core.scan.filter;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
// 컴포넌트 스캔 대상에서 제외시킬 어노테이션
public @interface MyExcludeComponent {
}
