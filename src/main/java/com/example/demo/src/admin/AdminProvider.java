package com.example.demo.src.admin;

import com.example.demo.src.admin.model.GetUserReq;
import com.example.demo.src.admin.model.GetUserRes;
import com.example.demo.src.board.BoardDao;
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
}
