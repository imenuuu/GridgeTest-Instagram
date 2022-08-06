package com.example.demo.src.admin;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.admin.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class AdminProvider {
    private final AdminDao adminDao;

    public AdminProvider(AdminDao adminDao) {
        this.adminDao = adminDao;
    }
    public List<GetUserRes> getUsers(GetUserReq getUserReq) throws BaseException {
        try {
            return adminDao.getUsers(getUserReq);
        }
        catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserInfoRes> getUserInfo(Long userId) throws BaseException {
        try {
            List<GetUserInfoRes> getUserInfoRes = adminDao.getUserInfo(userId);
            return getUserInfoRes;
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetBoardRes> getBoards(GetBoardReq getBoardReq) throws BaseException {
        try {
            return adminDao.getBoards(getBoardReq);
        }
        catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetBoardInfoRes> getBoardInfo(Long boardId) throws BaseException {
        try {
            return adminDao.getBoardInfo(boardId);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetBoardReportRes> getBoardReport(int paging) throws BaseException {
        try {
            return adminDao.getBoardReport(paging);
        }
        catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
        }

    public List<GetCommentReportRes> getCommentReport(int paging) throws BaseException {
        try {
            return adminDao.getCommentReport(paging);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetReCommentReportRes> getReCommentReport(int paging) throws BaseException {
        try {
            return adminDao.getReCommentReport(paging);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetBoardReportInfoRes> getBoardReportInfo(Long reportId) throws BaseException {
        try{
            return adminDao.getBoardReportInfo(reportId);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetLogRes> getBoardLog(GetLogQueryReq getLogQueryReq) throws BaseException {
        try{
            return adminDao.getBoardLog(getLogQueryReq);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetLogRes> getCommentLog(GetLogQueryReq getLogQueryReq) throws BaseException {
        try{
            return adminDao.getCommentLog(getLogQueryReq);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetLogRes> getReCommentLog(GetLogQueryReq getLogQueryReq) throws BaseException {
        try{
            return adminDao.getReCommentLog(getLogQueryReq);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetLogRes> getReportLog(GetLogQueryReq getLogQueryReq) throws BaseException {
        try{
            return adminDao.getReportLog(getLogQueryReq);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetLogRes> getUserLog(GetLogQueryReq getLogQueryReq) throws BaseException {
        try{
            return adminDao.getUserLog(getLogQueryReq);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
