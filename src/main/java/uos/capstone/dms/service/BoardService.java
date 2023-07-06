package uos.capstone.dms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uos.capstone.dms.domain.ImageDTO;
import uos.capstone.dms.domain.board.*;
import uos.capstone.dms.domain.user.MemberImage;
import uos.capstone.dms.mapper.BoardMapper;
import uos.capstone.dms.repository.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BoardService {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FileService fileService;
    private final PostImageRepository imageRepository;
    private final MemberRepository memberRepository;

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;


    public List<BoardDTO> getBoardList() {
        return boardRepository.findAll().stream().map(board -> BoardMapper.INSTANCE.boardToBoardDTO(board)).collect(Collectors.toList());
    }

    public List<PostShortDTO> getPostList(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(()-> new RuntimeException("존재하지 않는 게시판 번호입니다."));


        List<Post> postList = postRepository.findAllByBoard(board);

        return postList.stream().map(post -> {
            PostShortDTO postShortDTO = PostShortDTO.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .likeCounts(post.getLikeCounts())
                    .commentCount((int) commentRepository.findAllByPostId(post.getPostId()).stream().count())
                    .modifiedDate(post.getModifiedDate())
                    .writerId(post.getWriter().getId())
                    .writerName(post.getWriter().getNickname())
                    .build();

            return postShortDTO;
        }).collect(Collectors.toList());
    }

    @Transactional
    public Long registerPost(PostRegisterDTO registerDTO, String userId, Long boardId) {
        Post post = BoardMapper.INSTANCE.postRegisterDTOToPost(registerDTO, boardId, userId);

        Post savedPost = postRepository.save(post);

        if(registerDTO.getImages() != null) {
            registerDTO.getImages().stream().forEach(image -> savePostImage(image, savedPost.getPostId()));
        }

        return savedPost.getPostId();
    }


    @Transactional
    private PostImage savePostImage(MultipartFile file, Long postId) {
        String originalName = file.getOriginalFilename();
        Path root = Paths.get(uploadPath, "post");

        try {
            ImageDTO imageDTO =  fileService.createImageDTO(originalName, root);
            PostImage postImage = PostImage.builder()
                    .postId(postId)
                    .uuid(imageDTO.getUuid())
                    .fileName(imageDTO.getFileName())
                    .fileUrl(imageDTO.getFileUrl())
                    .build();

            file.transferTo(Paths.get(imageDTO.getFileUrl()));

            return imageRepository.save(postImage);
        } catch (IOException e) {
            log.warn("업로드 폴더 생성 실패: " + e.getMessage());
        }

        return null;
    }

    @Transactional
    public void updatePost(PostDTO postDTO, String userId, Long boardId, List<String> imagesToDelete, List<MultipartFile> imagesToAdd) {
        if(imagesToDelete != null) {
            imagesToDelete.stream().forEach(uuid -> imageRepository.deleteByUuid(uuid));
        }

        if(imagesToAdd != null) {
            imagesToAdd.stream().forEach(image -> savePostImage(image, postDTO.getPostId()));
        }

        postRepository.save(BoardMapper.INSTANCE.postDTOToPost(postDTO));

    }

    @Transactional(readOnly = true)
    public PostDTO getPostByPostId(Long postId) {
        PostDTO postDTO = postRepository.findById(postId).map(post -> BoardMapper.INSTANCE.postToPostDTO(post)).orElseThrow(() -> new RuntimeException("존재하지 않는 게시물입니다."));

        List<String> imageUuid = imageRepository.findAllByPostId(postId).stream().map(image -> image.getUuid()).collect(Collectors.toList());
        postDTO.setImageUuid(imageUuid);

        return postDTO;
    }

    @Transactional
    public void deletePost(Long postId) {
        imageRepository.deleteAllByPostId(postId);
        postRepository.deleteById(postId);
    }

    @Transactional(readOnly = true)
    public PostDetailDTO getPostDetail(Long postId) {
        PostDetailDTO postDetailDTO = postRepository.findById(postId).map(post -> BoardMapper.INSTANCE.postToPostDetailDTO(post)).orElseThrow(() -> new RuntimeException("존재하지 않는 게시물입니다."));

        List<String> imageUuid = imageRepository.findAllByPostId(postId).stream().map(image -> image.getUuid()).collect(Collectors.toList());
        postDetailDTO.setImageUuid(imageUuid);
        postDetailDTO.setWriterName(memberRepository.findByUserId(postDetailDTO.getWriterId()).get().getNickname());

        List<CommentDTO> comments = commentRepository.findAllByPostId(postId).stream().map(comment -> BoardMapper.INSTANCE.commentToCommentDTO(comment, comment.getWriter())).collect(Collectors.toList());
        postDetailDTO.setComments(comments);

        return postDetailDTO;
    }


}
