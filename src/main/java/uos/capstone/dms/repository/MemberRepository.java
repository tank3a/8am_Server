package uos.capstone.dms.repository;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uos.capstone.dms.domain.user.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findByUserId(String userId);

    @Query("select m from Member m left join fetch m.memberImage where m.userId = :userId")
    Optional<Member> findByUserIdEagerLoadImage(String userId);
    Optional<Member> findByEmail(String email);
    boolean existsByUserId(String userId);

    @OnDelete(action = OnDeleteAction.CASCADE)
    void deleteByUserId(String userId);
}
