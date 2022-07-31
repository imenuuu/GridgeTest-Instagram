package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.follow.model.GetFollowKeepRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/follows")
public class FollowController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final FollowProvider followProvider;
    @Autowired
    private final FollowService followService;
    @Autowired
    private final JwtService jwtService;

    public FollowController(FollowProvider followProvider,FollowService followService, JwtService jwtService){
        this.followProvider = followProvider;
        this.followService = followService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @PostMapping("/{userId}/{followUserId}")
    public BaseResponse<String> followUser(@PathVariable("userId") Long userId,@PathVariable("followUserId") Long followUserId){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(followProvider.checkUser(followUserId)!=1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            String result = "";
            //공개 계정인지 비공개 계정인지 확인
            if(followProvider.getUserPublic(followUserId).equals("TRUE")){
                followService.createFollow(userId,followUserId);
                result="팔로우 성공";
            }
            else{
                followService.requestFollow(userId,followUserId);
                result="팔로우 요청 성공";
            }

            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @DeleteMapping("/{userId}/{followUserId}")
    public BaseResponse<String> unFollowUser(@PathVariable("userId") Long userId,@PathVariable("followUserId") Long followUserId){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(followProvider.checkUser(followUserId)!=1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            followService.unFollow(userId,followUserId);
            String result="언팔로우 성공";

            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //팔로우요청수락
    @ResponseBody
    @PostMapping("/keep/{userId}/{requestId}")
    public BaseResponse<String> agreeFollow(@PathVariable("userId") Long userId,@PathVariable("requestId") Long requestId){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(followProvider.checkRequest(requestId)==1){
                return new BaseResponse<>(NOT_EXIST_FOLLOW);
            }

            Long followUserId=followProvider.getRequestUserId(requestId);
            followService.deleteRequest(requestId);
            followService.createFollow(followUserId,userId);
            String result="팔로우 수락 완료";

            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @DeleteMapping("/keep/{userId}/{requestId}")
    public BaseResponse<String> deleteFollowRequest(@PathVariable("userId") Long userId,@PathVariable("requestId") Long requestId){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(followProvider.checkRequest(requestId)==1){
                return new BaseResponse<>(NOT_EXIST_FOLLOW);
            }
            if(followProvider.getUserId(requestId)!=userId){
                return new BaseResponse<>(INVALID_USER_ACCESS);
            }
            followService.deleteRequest(requestId);
            String result="팔로우 요청 거절 완료";

            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @DeleteMapping("/request/{userId}/{requestId}")
    public BaseResponse<String> cancelFollowRequest(@PathVariable("userId") Long userId,@PathVariable("requestId") Long requestId){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(followProvider.getRequestUserId(requestId)!=userId){
                return new BaseResponse<>(INVALID_USER_ACCESS);
            }
            followService.deleteRequest(requestId);
            String result="팔로우 요청 취소 완료";

            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/keep/{userId}")
    public BaseResponse<List<GetFollowKeepRes>> getFollowKeep(@PathVariable("userId") Long userId){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetFollowKeepRes> getFollowKeepRes=followProvider.getFollowKeep(userId);
            return new BaseResponse<>(getFollowKeepRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
