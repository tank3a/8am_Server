package uos.capstone.dms.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import uos.capstone.dms.domain.board.*;
import uos.capstone.dms.security.SecurityUtil;
import uos.capstone.dms.service.BoardService;
import uos.capstone.dms.service.MemberService;

import java.io.IOException;
import java.util.List;

@RestController
@Log4j2
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final MemberService memberService;

    @GetMapping("/list")
    public List<BoardDTO> getBoardList() {

        return boardService.getBoardList();
    }

    @GetMapping("/{boardId}/list")
    public List<PostShortDTO> getPostListInBoard(@PathVariable("boardId") Long boardId) {
        return boardService.getPostList(boardId);
    }

    @PostMapping("/{boardId}/register")
    public void registerPost(@PathVariable("boardId") Long boardId, @ModelAttribute PostRegisterDTO postRegisterDTO, HttpServletResponse response) throws IOException {
        String userId = SecurityUtil.getCurrentUsername();
        if(userId == null) {
            throw new RuntimeException("로그인 후 게시글을 작성할 수 있습니다.");
        }

        Long postId = boardService.registerPost(postRegisterDTO, userId, boardId);

        response.sendRedirect("/board/" + boardId + "/read?postId=" + postId);
    }

    @PostMapping("/{boardId}/modify")
    public void modifyPost(@PathVariable("boardId") Long boardId, @RequestParam("postId") Long postId, @ModelAttribute PostModifyDTO postModifyDTO) {
        String userId = SecurityUtil.getCurrentUsername();
        PostDTO postDTO = boardService.getPostByPostId(postId);
        if(userId != postDTO.getWriterId()) {
            throw new RuntimeException("잘못된 접근입니다. 게시글 작성자만 수정할 수 있습니다.");
        }

        postDTO.setContent(postModifyDTO.getContent());
        postDTO.setTitle(postModifyDTO.getTitle());

        boardService.updatePost(postDTO, userId, boardId, postModifyDTO.getImageToDelete(), postModifyDTO.getImages());
    }

    @DeleteMapping("/{boardId}/delete")
    public void deletePost(@PathVariable("boardId") Long boardId, @RequestParam("postId") Long postId) {
        String userId = SecurityUtil.getCurrentUsername();
        PostDTO postDTO = boardService.getPostByPostId(postId);
        if(userId != postDTO.getWriterId()) {
            throw new RuntimeException("잘못된 접근입니다. 게시글 작성자만 삭제할 수 있습니다.");
        }

        boardService.deletePost(postId);
    }

    @GetMapping("/{boardId}/read")
    public PostDetailDTO getPostDetail(@PathVariable("boardId") Long boardId, @RequestParam("postId") Long postId) {
        return boardService.getPostDetail(postId);
    }

    @PostMapping("/{boardId}/comment/add")
    public void addComment(@PathVariable("boardId") Long boardId, @RequestParam("postId") Long postId, @ModelAttribute CommentAddDTO commentToAdd, HttpServletResponse response) throws IOException {
        String userId = SecurityUtil.getCurrentUsername();

        if(commentToAdd.getContent() == null || commentToAdd.getContent().isEmpty()) {
            throw new RuntimeException("빈 댓글은 등록할 수 없습니다.");
        }

        commentToAdd.setCommentId(null);
        commentToAdd.setPostId(postId);
        commentToAdd.setWriterId(userId);


        boardService.addComment(commentToAdd);

        response.sendRedirect("/board/" + boardId + "/readComment?postId=" + postId);
    }

    @PostMapping("/{boardId}/comment/update")
    public void updateComment(@PathVariable("boardId") Long boardId, @RequestParam("postId") Long postId, @RequestParam("commentId") Long commentId, @ModelAttribute CommentAddDTO commentToAdd, HttpServletResponse response) throws IOException {
        String userId = SecurityUtil.getCurrentUsername();

        if(commentToAdd.getContent() == null || commentToAdd.getContent().isEmpty()) {
            throw new RuntimeException("빈 댓글은 등록할 수 없습니다.");
        }

        CommentDTO commentDTO = boardService.getComment(commentId);
        if(commentDTO.getPostId() != postId || !commentDTO.getWriterId().equals(userId)) {
            throw new RuntimeException("잘못된 접근입니다.");
        }

        commentToAdd.setCommentId(commentDTO.getCommentId());
        commentToAdd.setPostId(postId);
        commentToAdd.setWriterId(userId);

        boardService.addComment(commentToAdd);

        response.sendRedirect("/board/" + boardId + "/readComment?postId=" + postId);
    }

    @DeleteMapping("/{boardId}/comment/delete")
    public void deleteComment(@PathVariable("boardId") Long boardId, @RequestParam("postId") Long postId, @RequestParam("commentId") Long commentId, HttpServletResponse response) throws IOException {
        CommentDTO commentDTO = boardService.getComment(commentId);
        String userId = SecurityUtil.getCurrentUsername();

        if(!userId.equals(commentDTO.getWriterId())) {
            throw new RuntimeException("작성자만 삭제할 수 있습니다.");
        }

        if(commentDTO.getPostId() != postId) {
            throw new RuntimeException("잘못된 접근입니다.");
        }

        boardService.deleteComment(commentId);

        response.sendRedirect("/board/" + boardId + "/readComment?postId=" + postId);

    }
}