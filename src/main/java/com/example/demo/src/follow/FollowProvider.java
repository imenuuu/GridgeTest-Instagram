package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import com.example.demo.src.follow.model.GetFollowKeepRes;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

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
    public int checkUser(Long userId) throws BaseException{
        try{
            return followDao.checkUser(userId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public Long getRequestUserId(Long requestId) throws BaseException {
            return followDao.getRequestUserId(requestId);
    }

    public Long getUserId(Long requestId) {
        return followDao.getUserId(requestId);
    }

    public List<GetFollowKeepRes> getFollowKeep(Long userId) {
        return followDao.getFollowKeep(userId);
    }

    public int checkRequest(Long requestId) {
        return followDao.checkRequest(requestId);
    }
}
