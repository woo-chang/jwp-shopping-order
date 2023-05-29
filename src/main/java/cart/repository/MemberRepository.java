package cart.repository;

import cart.dao.MemberDao;
import cart.dao.entity.MemberEntity;
import cart.domain.member.Member;
import cart.exception.MemberException.NotFound;
import cart.repository.mapper.MemberMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    private final MemberDao memberDao;

    public MemberRepository(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public List<Member> getAllMembers() {
        List<MemberEntity> memberEntities = memberDao.getAllMembers();
        return memberEntities.stream()
                .map(MemberMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Member getMemberByEmailAndPassword(String email, String password) {
        MemberEntity memberEntity = memberDao.getMemberByEmailAndPassword(email, password)
                .orElseThrow(() -> new NotFound());
        return MemberMapper.toDomain(memberEntity);
    }
}
