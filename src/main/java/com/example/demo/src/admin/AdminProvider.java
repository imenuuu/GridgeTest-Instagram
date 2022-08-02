package com.example.demo.src.admin;

import com.example.demo.src.admin.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminProvider {
    private final AdminDao adminDao;

    public AdminProvider(AdminDao adminDao) {
        this.adminDao = adminDao;
    }
    public List<GetUserRes> getUsers(GetUserReq getUserReq) {
        return adminDao.getUsers(getUserReq);
    }

    public List<GetUserInfoRes> getUserInfo(Long userId) {
        List<GetUserInfoRes> getUserInfoRes=adminDao.getUserInfo(userId);
        return getUserInfoRes;
    }

    public List<GetBoardRes> getBoards(GetBoardReq getBoardReq) {
        return adminDao.getBoards(getBoardReq);
    }

    public List<GetBoardInfoRes> getBoardInfo(Long boardId) {
        return adminDao.getBoardInfo(boardId);
    }
}
