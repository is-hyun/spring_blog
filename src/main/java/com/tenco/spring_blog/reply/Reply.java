package com.tenco.spring_blog.reply;

import com.tenco.spring_blog._core.util.MyDateUtil;
import com.tenco.spring_blog.board.Board;
import com.tenco.spring_blog.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "reply_tb")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String comment;

    @CreationTimestamp
    private Timestamp createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Reply(String comment, Board board, User user) {
        this.comment = comment;
        this.board = board;
        this.user = user;
    }

    public String getTime() {
        return MyDateUtil.timestampFormat(createdAt);
    }

    // 댓글 작성자 본인 확인
    public boolean isOwner(Long userId) {
        if (userId == null) {
            return false;
        }
        return this.user.getId().equals(userId);
    }

}
