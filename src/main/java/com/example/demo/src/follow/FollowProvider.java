package com.example.demo.src.follow;

import org.springframework.stereotype.Service;

@Service
public class FollowProvider {
    private final FollowDao followDao;

    public FollowProvider(FollowDao followDao) {
        this.followDao = followDao;
    }

    public String getUserPublic(Long followUserId) {
        String userPublic=followDao.getUserPublic(followUserId);
        return userPublic;
    }
}
