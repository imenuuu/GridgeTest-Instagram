package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.src.comment.model.GetCommentInfo;
import com.example.demo.src.comment.model.GetReCommentReq;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class CommentProvider {
    private final CommentDao commentDao;

    public CommentProvider(CommentDao commentDao) {
        this.commentDao = commentDao;
    }
    public List<GetCommentInfo> getComment(Long userId, Long boardId, int paging) throws BaseException {
        try {
            return commentDao.getComment(userId, boardId, paging);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkComment(Long commentId) throws BaseException {
        try{
            return commentDao.checkComment(commentId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkBoard(Long boardId) throws BaseException {
        try{
            return commentDao.checkBoard(boardId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkUser(Long userId) throws BaseException {
        try{
            return commentDao.checkUser(userId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkReComment(Long reCommentId) throws BaseException {
        try {
            return commentDao.checkReComment(reCommentId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetReCommentReq> getReComment(Long userId, Long commentId, int paging) throws BaseException {
        try {
            return commentDao.getReComment(userId, commentId, paging);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkReCommentLike(Long userId, Long reCommentId) throws BaseException {
        try{
            return commentDao.checkReCommentLike(userId,reCommentId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

        }

    public int checkCommentLike(Long userId, Long commentId) throws BaseException {
        try {
            return commentDao.checkCommentLike(userId, commentId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        }

    public Long checkCommentUserId(Long boardId) throws BaseException {
        try {
            return commentDao.checkCommentUserId(boardId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public Long checkReCommentUser(Long reCommentId) throws BaseException {
        try{
            return commentDao.checkReCommentUser(reCommentId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
    }

    }

    public Long checkReCommentUserId(Long commentId) throws BaseException {
        try {
            return commentDao.checkReCommentUserId(commentId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
