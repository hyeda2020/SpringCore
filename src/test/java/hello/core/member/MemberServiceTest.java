package hello.core.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/** JUnit Test를 활용한 멤버서비스 테스트 **/
public class MemberServiceTest {

    MemberService memberService = new MemberServiceImpl();

    @Test
    void join() {
        // given
        Member member = new Member(1L, "memberA", Grade.VIP);

        // when
        memberService.join(member);
        Member findMember = memberService.findMember(1L);

        // then
        Assertions.assertThat(member).isEqualTo(findMember);
    }
}
