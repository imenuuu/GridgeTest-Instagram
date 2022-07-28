package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class FollowService {
    private final FollowDao followDao;

    public FollowService(FollowDao followDao) {
        this.followDao = followDao;
    }

    public void requestFollow(Long userId, Long followUserId) throws BaseException {
        try {
            followDao.requestFollow(userId, followUserId);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public void createFollow(Long userId, Long followUserId) throws BaseException {
        try {
            followDao.createFollow(userId, followUserId);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void unFollow(Long userId, Long blockUserId) throws BaseException {
        try {
            followDao.unFollow(userId, blockUserId);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
