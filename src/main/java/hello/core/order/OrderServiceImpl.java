package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository = new MemoryMemberRepository();

    /**
     * 할인 정책의 클라이언트인 OrderServiceImpl은 DiscountPolicy 인터페이스 뿐만 아니라,
     * 구현체인 Fix/RateDiscountPolicy도 함께 의존중 (OCP 위반!)
     *  **/
    //private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    private final DiscountPolicy discountPolicy = new RateDiscountPolicy(); // 할인 정책 변경

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId); // 회원정보 조회
        int discountPrice = discountPolicy.discount(member, itemPrice); // 회원 등급에 따른 할인 결과 반환

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
