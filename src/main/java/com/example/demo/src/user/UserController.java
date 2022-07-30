package com.example.demo.src.user;

import com.example.demo.src.follow.FollowProvider;
import com.example.demo.src.follow.FollowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexId;
import static com.example.demo.utils.ValidationRegex.isRegexPhoneNumber;

@RestController
@RequestMapping("/users")
@Api(tags={"GridgeTest API"})
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;

    @Autowired
    FollowService followService;

    @Autowired
    FollowProvider followProvider;




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService,FollowService followService,FollowProvider followProvider){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
        this.followService=followService;
        this.followProvider=followProvider;
    }


    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String Email) {
        try{
            if(Email == null){
                List<GetUserRes> getUsersRes = userProvider.getUsers();
                return new BaseResponse<>(getUsersRes);
            }
            // Get Users
            List<GetUserRes> getUsersRes = userProvider.getUsersByEmail(Email);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원 1명 조회 API
     * [GET] /users/:userId
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userId}") // (GET) 127.0.0.1:9000/app/users/:userId
    public BaseResponse<GetUserRes> getUser(@PathVariable("userId") int userId) {
        // Get Users
        try{
            GetUserRes getUserRes = userProvider.getUser(userId);
            return new BaseResponse<>(getUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    @ApiOperation(value="회원가입",notes="회원가입 API")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        if(postUserReq.getUserId() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_ID);
        }
        if(postUserReq.getUserId().length()>=20){
            return new BaseResponse<>(LONG_USER_ID_CHARACTERS);
        }
        //아이디 정규표현
        if (!isRegexId(postUserReq.getUserId())) {
            return new BaseResponse<>(POST_USERS_INVALID_ID);
        }
        if(!isRegexPhoneNumber(postUserReq.getPhoneNumber())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    @ApiOperation(value="로그인",notes="로그인 API")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        try{
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            userService.logIn(postLoginRes.getUserId());
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저정보변경 API
     * [PATCH] /users/:userId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userId}")
    public BaseResponse<String> modifyUserName(@PathVariable("userId") int userId, @RequestBody User user){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            //userId와 접근한 유저가 같은지 확인
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저네임 변경
            PatchUserReq patchUserReq = new PatchUserReq(userId,user.getName());
            userService.modifyUserName(patchUserReq);

            String result = "";
        return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ResponseBody
    @GetMapping("/check/sendSMS")
    public BaseResponse<String> sendSMS(@RequestParam(value="to")String to)throws CoolsmsException {
        if(!isRegexPhoneNumber(to)){
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }
        String result = userService.PhoneNumberCheck(to);
        return new BaseResponse<>(result);
    }

    @ResponseBody
    @GetMapping("/my_profile/{userId}")
    public BaseResponse<List<GetMyProfileRes>> getMyProfile(@PathVariable("userId") Long userId){
        try {
            Long userIdByJwt = jwtService.getUserIdx();
            //userId와 접근한 유저가 같은지 확인
            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }if(userProvider.checkUser(userId)!=1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            List<GetMyProfileRes> getMyProfileRes = userProvider.getMyProfile(userId);
            return new BaseResponse<>(getMyProfileRes);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }


    @ResponseBody
    @GetMapping("/profile/{userId}/{profileUserId}")
    public BaseResponse<List<GetUserProfileRes>> getUserProfile(@PathVariable("userId") Long userId,@PathVariable("profileUserId") Long profileUserId){
        try {
            Long userIdByJwt = jwtService.getUserIdx();
            //userId와 접근한 유저가 같은지 확인

            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(userProvider.checkUser(profileUserId)!=1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            if(userProvider.checkBlock(userId,profileUserId)==1){
                return new BaseResponse<>(BLOCKED_BY_PROFILE_USER);
            }
            List<GetUserProfileRes> getUserProfileRes = null;
            if(userProvider.getUserPublic(profileUserId).equals("FALSE")){
                if(userProvider.checkFollow(userId,profileUserId)==1){
                    getUserProfileRes = userProvider.getUserProfile(userId,profileUserId);
                    return new BaseResponse<>(getUserProfileRes);

                }else {
                    return new BaseResponse<>(NOT_PUBLIC_USER);
                }
            }



            getUserProfileRes = userProvider.getUserProfile(userId,profileUserId);
            return new BaseResponse<>(getUserProfileRes);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 카카오 로그인 액세스 토큰 발급
     */
    @ResponseBody
    @GetMapping("/oauth")
    public BaseResponse<String> getKaKaoAccessToken(@RequestParam String code){
        List<GetKakaoTokenRes> getKaKaoAccessToken = null;
        try {
            String accessToken = userService.getKaKaoAccessToken(code);
            return  new BaseResponse<>(accessToken);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }


    /**
     * 카카오 로그인 API
     * [POST] /users/oauth/code?=
     *
     * @return BaseResponse<PostUserKakaoRes>
     */


    @ResponseBody
    @PostMapping("/oauth/logIn")
    public BaseResponse<PostLoginRes> KakaoLogIn(@RequestBody PostKakaoLogInReq postKakaoLogInReq) {

        try {

            KakaoInfo kakaoInfo = userService.getKakaoUser(postKakaoLogInReq.getAccessToken());
            PostLoginRes postLoginRes = null;

            //만약 유저 정보가 없으면 회원가입창으로 이동한다.
            if (userProvider.checkKakaoUser(kakaoInfo.getKakaoEmail()) == 0) {
                return new BaseResponse<>(NOT_EXIST_KAKAO_USER);
            }
            //만약 유저 정보가 카카오 테이블에 있으면 로그인 후 jwt access_Token 발급
            else if (userProvider.checkKakaoUser(kakaoInfo.getKakaoEmail()) == 1) {
                postLoginRes = userProvider.logInKakao(kakaoInfo.getKakaoEmail());
            }
            userService.logIn(postLoginRes.getUserId());
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/kakao")
    @ApiOperation(value="카카오 유저 회원가입",notes="카카오 유저 회원가입 API")
    public BaseResponse<PostUserRes> createKakaoUser(@RequestBody PostKakaoUserReq postKakaoUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        if(postKakaoUserReq.getUserId() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_ID);
        }
        if(postKakaoUserReq.getUserId().length()>=20){
            return new BaseResponse<>(LONG_USER_ID_CHARACTERS);
        }
        if(!isRegexPhoneNumber(postKakaoUserReq.getPhoneNumber())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }
        //아이디 정규표현
        if (!isRegexId(postKakaoUserReq.getUserId())) {
            return new BaseResponse<>(POST_USERS_INVALID_ID);
        }
        try {
            KakaoInfo kakaoInfo = userService.getKakaoUser(postKakaoUserReq.getAccessToken());
            PostUserRes postUserRes = null;
            if (userProvider.checkKakaoUser(kakaoInfo.getKakaoEmail()) == 0) {
                postUserRes = userService.createKakaoUserToken(postKakaoUserReq);
                userService.createKakaoUser(kakaoInfo,postUserRes.getUserId());

            }
            userService.logIn(postUserRes.getUserId());
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/userBlock/{userId}/{blockUserId}")
    public BaseResponse<String> userBlock(@PathVariable("userId") Long userId,@PathVariable("blockUserId") Long blockUserId){
        try {
            Long userIdByJwt = jwtService.getUserIdx();
            //userId와 접근한 유저가 같은지 확인
            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(userProvider.checkUser(blockUserId)==1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            followService.unFollow(userId,blockUserId);

            followService.unFollow(blockUserId,userId);

            userService.userBlock(userId,blockUserId);
            String result="차단 성공";
            return new BaseResponse<>(result);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/profile/{userId}")
    public BaseResponse<String> modifyProfile(@PathVariable("userId") Long userId,@RequestBody PatchProfileReq patchProfileReq){
        try {
            Long userIdByJwt = jwtService.getUserIdx();
            //userId와 접근한 유저가 같은지 확인
            if(patchProfileReq.getUserId().isEmpty()){
                return new BaseResponse<>(POST_USERS_EMPTY_ID);
            }
            if(patchProfileReq.getUserId().length()>=20){
                return new BaseResponse<>(LONG_USER_ID_CHARACTERS);
            }
            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(userProvider.checkId(patchProfileReq.getUserId())==1){
                return new BaseResponse<>(POST_USERS_EXISTS_ID);
            }
            userService.modifyProfile(userId, patchProfileReq);
            String result="수정 성공";
            return new BaseResponse<>(result);
        }catch(BaseException exception){
                return new BaseResponse<>((exception.getStatus()));
            }
    }

    @ResponseBody
    @PatchMapping("/profileImg/{userId}")
    public BaseResponse<String> modifyProfileImg(@PathVariable("userId") Long userId,@RequestBody PatchProfileImgReq patchProfileImgReq){
        try {
            Long userIdByJwt = jwtService.getUserIdx();
            //userId와 접근한 유저가 같은지 확인
            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.modifyProfileImg(userId, patchProfileImgReq);
            String result="수정 성공";
            return new BaseResponse<>(result);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/closedProfile/{userId}/{profileUserId}")
    public BaseResponse<List<GetClosedProfileRes>> closedProfile(@PathVariable("userId") Long userId,@PathVariable("profileUserId") Long profileUserId){
        try {
            Long userIdByJwt = jwtService.getUserIdx();
            //userId와 접근한 유저가 같은지 확인
            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(userProvider.checkUser(profileUserId)!=1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            if(userProvider.checkBlock(userId,profileUserId)!=1){
                return new BaseResponse<>(BLOCKED_BY_PROFILE_USER);
            }
            List<GetClosedProfileRes> getClosedProfileRes = userProvider.getCloesdProfile(userId,profileUserId);
            return new BaseResponse<>(getClosedProfileRes);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    //비밀번호를 변경할 때 유저 아이디와 전화번호를 같이 입력받고 계정에 정보와 일치하는지 확인 후 변경
    @ResponseBody
    @PatchMapping("/password")
    public BaseResponse<String> modifyPassword(@RequestBody PatchPasswordRes patchPasswordRes){
        try {
            //userId와 접근한 유저가 같은지 확인
            if(!isRegexPhoneNumber(patchPasswordRes.getPhoneNumber())){
                return new BaseResponse<>(POST_USERS_INVALID_PHONE);
            }
            if(userProvider.checkUserPhoneNumber(patchPasswordRes)==0){
                return new BaseResponse<>(NOT_SUCCESS_USER_INFO);
            }
            userService.modifyPassword(patchPasswordRes);
            String result="비밀번호 변경 성공";
            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/public/{userId}")
    public BaseResponse<String> modifyPublic(@PathVariable("userId")Long userId){
        try {
            Long userIdByJwt = jwtService.getUserIdx();
            //userId와 접근한 유저가 같은지 확인
            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String result="";
            if(userProvider.getUserPublic(userId).equals("FALSE")){
                userProvider.modifyPublicTrue(userId);
                result="공개 계정 설정 성공";
            }
            else if(userProvider.getUserPublic(userId).equals("TRUE")){
                userProvider.modifyPublicFalse(userId);
                result="비공개 계정 설정 성공";
            }

            return new BaseResponse<>(result);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }




}
