package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

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
            followService.unFollow(userId,followUserId);
            String result="언팔로우 성공";

            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



}
