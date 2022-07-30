package com.example.demo.src.comment;

import com.example.demo.src.comment.model.GetCommentInfo;
import com.example.demo.src.comment.model.PostCommentReq;
import com.example.demo.src.comment.model.PostReCommentReq;
import com.example.demo.src.follow.FollowDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final CommentDao commentDao;

    public CommentService(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    public void postComment(PostCommentReq postCommentReq) {
        commentDao.postComment(postCommentReq);
    }

    public void postCommentLike(Long userId, Long commentId) {
        commentDao.postCommentLike(userId, commentId);
    }

    public void deleteCommentLike(Long userId, Long commentId) {
        commentDao.deleteCommentLike(userId, commentId);
    }

    public void postReComment(PostReCommentReq postCommentReq) {
        commentDao.postReComment(postCommentReq);
    }

    public void postReCommentLike(Long userId, Long reCommentId) {
        commentDao.postReCommentLike(userId, reCommentId);
    }

    public void deleteReCommentLike(Long userId, Long reCommentId) {
        commentDao.deleteReCommentLike(userId,reCommentId);
    }
}
