package com.example.demo.src.admin;

import com.example.demo.config.BaseException;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class AdminService {
    private final AdminDao adminDao;

    public AdminService(AdminDao adminDao) {
        this.adminDao = adminDao;
    }
    public void userSuspension(Long userId) throws BaseException {
        try {
            adminDao.userSuspension(userId);
        }catch (Exception exception){
        throw new BaseException(DATABASE_ERROR);
    }
    }

    public void deleteReCommentReport(Long reportId) throws BaseException {
        try {
            adminDao.deleteReCommentReport(reportId);
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteBoardReport(Long reportId) throws BaseException {
        try {
            adminDao.deleteBoardReport(reportId);
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteCommentReport(Long reportId) throws BaseException {
        try {
            adminDao.deleteCommentReport(reportId);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        }

    public void deleteBoard(Long boardId) throws BaseException {
        try {
            adminDao.deleteBoard(boardId);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteComment(Long commentId) throws BaseException {
        try {
            adminDao.deleteComment(commentId);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteReComment(Long reCommentId) throws BaseException {
        try {
            adminDao.deleteReComment(reCommentId);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
