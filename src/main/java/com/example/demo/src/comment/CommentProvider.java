package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.src.comment.model.GetCommentInfo;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class CommentProvider {
    private final CommentDao commentDao;

    public CommentProvider(CommentDao commentDao) {
        this.commentDao = commentDao;
    }
    public List<GetCommentInfo> getComment(Long userId, Long boardId) {
        List<GetCommentInfo> getComment= commentDao.getComment(userId, boardId);
        return getComment;

    }

    public int checkComment(Long commentId) {
        return commentDao.checkComment(commentId);
    }

    public int checkBoard(Long boardId) {
        return commentDao.checkBoard(boardId);
    }
    public int checkUser(Long userId) throws BaseException {
        try{
            return commentDao.checkUser(userId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkReComment(Long reCommentId) {
        return commentDao.checkReComment(reCommentId);
    }
}
