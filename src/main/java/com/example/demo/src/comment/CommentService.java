package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.src.board.model.PostBoardReportReq;
import com.example.demo.src.board.model.PostLogReq;
import com.example.demo.src.comment.model.*;
import com.example.demo.src.follow.FollowDao;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class CommentService {
    private final CommentDao commentDao;

    public CommentService(CommentDao commentDao) {
       this.commentDao = commentDao;
    }

    public void postComment(PostCommentReq postCommentReq) throws BaseException {
        try{
            commentDao.postComment(postCommentReq);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void postCommentLike(Long userId, Long commentId) throws BaseException {
        try{
            commentDao.postCommentLike(userId, commentId);
        }  catch (Exception exception){
        throw new BaseException(DATABASE_ERROR);
    }
    }

    public void deleteCommentLike(Long userId, Long commentId) throws BaseException {
        try{
            commentDao.deleteCommentLike(userId, commentId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void postReComment(PostReCommentReq postCommentReq) throws BaseException {
        try {
            commentDao.postReComment(postCommentReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void postReCommentLike(Long userId, Long reCommentId) throws BaseException {
        try{
            commentDao.postReCommentLike(userId, reCommentId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteReCommentLike(Long userId, Long reCommentId) throws BaseException {
        try {
            commentDao.deleteReCommentLike(userId, reCommentId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void postCommentReport(PostCommentReportReq postCommentReportReq) throws BaseException {
        try{
            commentDao.postCommentReport(postCommentReportReq);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteReComment(Long userId, Long reCommentId) throws BaseException {
        try{
            commentDao.deleteReComment(userId,reCommentId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteReCommentLikeById(Long reCommentId) throws BaseException {
        try{
            commentDao.deleteReCommentLikeById(reCommentId);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void postReCommentReport(PostReCommentReportReq postReCommentReportReq) throws BaseException {
        try{
            commentDao.postReCommentReport(postReCommentReportReq);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteComment(Long commentId) throws BaseException {
        try{
            commentDao.deleteComment(commentId);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void createLog(PostLogReq postLogReq) throws BaseException {
        try{
            commentDao.createLog(postLogReq);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void createReCommentLog(PostLogReq postLogReq) throws BaseException {
        try{
            commentDao.createReCommentLog(postLogReq);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void createReportLog(PostLogReq postLogReq) throws BaseException {
        try{
            commentDao.createReportLog(postLogReq);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
