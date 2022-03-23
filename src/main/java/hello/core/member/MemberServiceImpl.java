package hello.core.member;

public class MemberServiceImpl implements MemberService {

    /** 의존관계가 인터페이스뿐만 아니라 구현까지 동시에 의존하는 문제가 생김 -> OCP를 잘 지키지 않고, DIP 위반 **/
    private final MemberRepository memberRepository = new MemoryMemberRepository();

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
