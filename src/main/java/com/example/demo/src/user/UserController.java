package com.example.demo.src.user;

import com.example.demo.src.board.model.PostLogReq;
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
import static com.example.demo.utils.ValidationRegex.*;

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
    public BaseResponse<String> getUser(@PathVariable("userId") Long userId) throws BaseException {
        // Get Users
        List<GetUserIdRes> getUserIdRes=userService.getUserId(userId);

        for(int i=0;i<getUserIdRes.size();i++){
            Long chatId=userService.createChat();
            userService.createChatRoomJoin(chatId,getUserIdRes.get(i).getId());
            userService.createChatRoomJoin(chatId,userId);
        }
        String result="채팅방 생성";
        return new BaseResponse<>(result);

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
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) throws BaseException {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        if(postUserReq.getUserId().length() <1){
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
        if(userProvider.checkPhoneNumber(postUserReq.getPhoneNumber())==1){
            return new BaseResponse<>(POST_USERS_PHONE_NUMBER);
        }



        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            PostLogReq postLogReq = new PostLogReq("CREATE",postUserRes.getUserId());
            userService.createLog(postLogReq);

            List<GetUserIdRes> getUserIdRes=userService.getUserId(postUserRes.getUserId());

            for(int i=0;i<getUserIdRes.size();i++){
               Long chatId=userService.createChat();
               userService.createChatRoomJoin(chatId,getUserIdRes.get(i).getId());
               userService.createChatRoomJoin(chatId,postUserRes.getUserId());
            }

            followService.createFollow(postUserRes.getUserId(),postUserRes.getUserId());

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
    @GetMapping("/getUserId/{userId}")
    public BaseResponse<List<GetUserIdRes>> getUserIdRes(@PathVariable("userId")Long userId) throws BaseException {
        List<GetUserIdRes> getUserIdRes=userService.getUserId(userId);
        return new BaseResponse<>(getUserIdRes);
    }

    @ResponseBody
    @PostMapping("/logIn")
    @ApiOperation(value="로그인",notes="로그인 API")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        try{
            PostLoginRes postLoginRes=null;
            if(isRegexPhoneNumber(postLoginReq.getId())){
                 if(userProvider.checkPhoneNumber(postLoginReq.getId())!=1){
                     return new BaseResponse<>(FAILED_TO_LOGIN);
                 }
                 postLoginRes=userProvider.phoneLogin(postLoginReq);
            }
            else if(isRegexId(postLoginReq.getId())){

                if(isRegexIdString(postLoginReq.getId())){
                    return new BaseResponse<>(POST_USERS_INVALID_ID);
                }
                if(userProvider.checkId(postLoginReq.getId())!=1){
                    return new BaseResponse<>(FAILED_TO_LOGIN);
                }
                postLoginRes = userProvider.logIn(postLoginReq);
            }

            userService.updateLogInDate(postLoginRes.getUserId());

            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }




    @ResponseBody
    @GetMapping("/check/sendSMS")
    public BaseResponse<String> sendSMS(@RequestParam(value="to")String to)throws CoolsmsException {
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
            if(userId==profileUserId){
                return new BaseResponse<>(MY_PROFILE_USER);
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

    //카카오 회원가입
    @ResponseBody
    @PostMapping("/kakao")
    @ApiOperation(value="카카오 유저 회원가입",notes="카카오 유저 회원가입 API")
    public BaseResponse<PostUserRes> createKakaoUser(@RequestBody PostKakaoUserReq postKakaoUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        if(postKakaoUserReq.getUserId().length() <1){
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
        if(isRegexIdString(postKakaoUserReq.getUserId())){
            return new BaseResponse<>(POST_USERS_INVALID_ID);
        }
        try {
            KakaoInfo kakaoInfo = userService.getKakaoUser(postKakaoUserReq.getAccessToken());
            PostUserRes postUserRes = null;
            if (userProvider.checkKakaoUser(kakaoInfo.getKakaoEmail()) == 0) {
                postUserRes = userService.createKakaoUserToken(postKakaoUserReq);
                userService.createKakaoUser(kakaoInfo,postUserRes.getUserId());

            }
            PostLogReq postLogReq = new PostLogReq("CREATE",postUserRes.getUserId());

            List<GetUserIdRes> getUserIdRes=userService.getUserId(postUserRes.getUserId());

            for(int i=0;i<getUserIdRes.size();i++){
                Long chatId=userService.createChat();
                userService.createChatRoomJoin(chatId,getUserIdRes.get(i).getId());
                userService.createChatRoomJoin(chatId,postUserRes.getUserId());
            }
            userService.createLog(postLogReq);
            userService.logIn(postUserRes.getUserId());
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/block/{userId}/{blockUserId}")
    public BaseResponse<String> userBlock(@PathVariable("userId") Long userId,@PathVariable("blockUserId") Long blockUserId){
        try {
            Long userIdByJwt = jwtService.getUserIdx();
            //userId와 접근한 유저가 같은지 확인
            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(userProvider.checkUser(blockUserId)!=1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            followService.unFollow(userId,blockUserId);

            followService.unFollow(blockUserId,userId);

            userService.userBlock(userId,blockUserId);
            PostLogReq postLogReq = new PostLogReq("CREATE",userId);
            userService.createLog(postLogReq);
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
            if(isRegexIdString(patchProfileReq.getUserId())){
                return new BaseResponse<>(POST_USERS_INVALID_STRING);
            }
            if (!isRegexId(patchProfileReq.getUserId())) {
                return new BaseResponse<>(POST_USERS_INVALID_ID);
            }
            PostLogReq postLogReq = new PostLogReq("UPDATE",userId);
            userService.createLog(postLogReq);
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
            PostLogReq postLogReq = new PostLogReq("UPDATE",userId);
            userService.createLog(postLogReq);
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
            if(userProvider.checkBlock(userId,profileUserId)==1){
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
            PostLogReq postLogReq = new PostLogReq("UPDATE",userId);
            userService.createLog(postLogReq);

            return new BaseResponse<>(result);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/drop/{userId}")
    public BaseResponse<String> deleteUser(@PathVariable("userId") Long userId){
        try {
            Long userIdByJwt = jwtService.getUserIdx();
            //userId와 접근한 유저가 같은지 확인
            if (userId != userIdByJwt) {
            }
            PostLogReq postLogReq = new PostLogReq("DELETE",userId);
            userService.createLog(postLogReq);
            String result="";
            userService.updateAllStatus(userId);

            return new BaseResponse<>(result);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }






}
