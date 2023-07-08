package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uos.capstone.dms.domain.board.PostImage;

import java.util.List;
import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findAllByPostId(Long postId);

    void deleteByUuid(String uuid);

    void deleteAllByPostId(Long postId);

    Optional<PostImage> findByUuid(String uuid);
}