package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderServiceImpl implements OrderService {

    private MemberRepository memberRepository;
    private DiscountPolicy discountPolicy;

    /**
     * 수정자 주입
     * 선택, 변경 가능성이 있는 의존관계에 사용
     * 수정자 주입을 포함한 나머지 주입 방식은 모두 생성자 이후에 호출되므로, 필드에 final 키워드 사용 불가
     * 가끔 옵션이 필요할 때 사용(생성자 주입과 동시에 사용 가능)
     * */
    @Autowired
    public void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Autowired
    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }

    /**
     * 필드 주입
     * 코드가 간결해서 많은 개발자들을 유혹하지만 외부에서 변경이 불가능해서 테스트 하기 힘들다는
     * 치명적인 단점이 있다.
     * DI 프레임워크가 없으면 아무것도 할 수 없다.
     * 사용하지 말자!
     * */
    // Autowired private MemberRepository memberRepository;
    // Autowired private DiscountPolicy discountPolicy;

    /**
     * 생성자로 의존관계 주입
     * 불변, 필수 의존관계에 사용
     * 이때는 필드(memberRepository, discountPolicy)에 final 키워드를 명시할 수 있다는 점에서 유용
     * 생성자 호출 시점에 딱 1번만 호출되는 것이 보장됨
     * 프레임워크에 의존하지 않고 순수한 자바 언어의 특징을 잘 살리는 방법임
     * */
    @Autowired // 생성자가 딱 하나만 있으면 @Autowired 생략 가능
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    /**
     * 참고로, @Autowired의 기본 동작은 주입할 대상이 없으면 오류 발생
     * 만약 주입할 대상이 없어도 동작하게 하려면
     * @Autowired(required = false) 로 지정하면 됨.
     * */

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId); // 회원정보 조회
        int discountPrice = discountPolicy.discount(member, itemPrice); // 회원 등급에 따른 할인 결과 반환

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
