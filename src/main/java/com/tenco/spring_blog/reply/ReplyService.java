package com.tenco.spring_blog.reply;

import com.tenco.spring_blog._core.error.Exception403;
import com.tenco.spring_blog._core.error.Exception404;
import com.tenco.spring_blog.board.Board;
import com.tenco.spring_blog.board.BoardJpaRepository;
import com.tenco.spring_blog.user.SessionUserDto;
import com.tenco.spring_blog.user.User;
import com.tenco.spring_blog.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private final ReplyJpaRepository replyJpaRepository;
    private final BoardJpaRepository boardJpaRepository;
    private final UserJpaRepository userJpaRepository;

    // A. 게시글 댓글 목록
    // 로그아웃 상태에서 sessionUserId = null
    public List<ReplyResponse.ListDto> findAllByBoardId(Long boardId, Long sessionUserId) {
        List<Reply> replyList = replyJpaRepository.findAllByBoardIdJoinUser(boardId);
        return replyList.stream()
                .map(reply -> new ReplyResponse.ListDto(reply, sessionUserId))
                .toList();
    }

    // B. 댓글 작성
    @Transactional
    public void save(ReplyRequest.SaveDto saveDto, SessionUserDto sessionUserDto) {

        // 1. 게시글 조회
        Board board = boardJpaRepository.findById(saveDto.getBoardId())
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다"));

        // 2. 작성자 정보 조회
        User user = userJpaRepository.findById(sessionUserDto.getId())
                .orElseThrow(() -> new Exception404("작성자 정보를 확인할 수 없습니다"));

        // 3. 저장
        replyJpaRepository.save(saveDto.toEntity(board, user));
    }

    // C. 댓글 삭제
    @Transactional
    public void deleteById(Long replyId, SessionUserDto sessionUserDto) {

        // 1. 댓글 조회
        Reply reply = replyJpaRepository.findById(replyId)
                .orElseThrow(() -> new Exception404("댓글을 찾을 수 없습니다"));
        // 2. 권한 확인
        if (!reply.isOwner(sessionUserDto.getId())) {
            throw new Exception403("댓글 삭제 권한이 없습니다");
        }
        Long boardId = reply.getBoard().getId();
        // 3. 삭제
        replyJpaRepository.delete(reply);
    }

//    // D. 게시글 댓글 전체 삭제
//    @Transactional
//    public void deleteAllReplyById(Long boardId, SessionUserDto sessionUserDto) {
//        // 권한 확인 필요할까
//        replyJpaRepository.deleteByBoardId(boardId);
//    }

}
