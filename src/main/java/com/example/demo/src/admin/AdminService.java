package com.example.demo.src.admin;

import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final AdminDao adminDao;

    public AdminService(AdminDao adminDao) {
        this.adminDao = adminDao;
    }
    public void userSuspension(Long userId) {
        adminDao.userSuspension(userId);
    }
}
