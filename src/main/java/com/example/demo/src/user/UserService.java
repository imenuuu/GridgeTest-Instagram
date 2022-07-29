package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.src.user.model.KakaoInfo;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    //POST
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        //중복
        if(userProvider.checkId(postUserReq.getUserId()) ==1){
            throw new BaseException(POST_USERS_EXISTS_ID);
        }

        String pwd;
        try{
            //암호화
            pwd = new SHA256().encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);

        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            Long userId = userDao.createUser(postUserReq);
            //jwt 발급.
            String jwt = jwtService.createJwt(userId);
            return new PostUserRes(jwt,userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyUserName(PatchUserReq patchUserReq) throws BaseException {
        try{
            int result = userDao.modifyUserName(patchUserReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public String PhoneNumberCheck(String to) throws CoolsmsException {
        String api_key = "NCSSDCQD8ZPSDL7H";
        String api_secret = "3JNFD9A9JPB14TMPOUFOOXT6RVC43BFD";
        Message coolsms = new Message(api_key, api_secret);

        Random rand  = new Random();
        String numStr = "";
        for(int i=0; i<4; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            numStr+=ran;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", to);    // 수신전화번호
        params.put("from", "01049177671");    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("type", "sms");
        params.put("text", "인증번호는 [" + numStr + "] 입니다.");

        coolsms.send(params); // 메시지 전송

        return numStr;
    }

    public String getKaKaoAccessToken(String code) throws BaseException{
        String access_Token="";
        String refresh_Token ="";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=98dde62928c7a676ea17b1241492fe0b"); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=http://localhost:9000/users/oauth"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();
            br.close();
            bw.close();
            return access_Token;
        }catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }

    public KakaoInfo getKakaoUser(String token) throws BaseException {


        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            int id = element.getAsJsonObject().get("id").getAsInt();
            String nickname=element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

            KakaoInfo kakaoInfo = new KakaoInfo(nickname, (long) id,email);

            br.close();
            return kakaoInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Transactional(rollbackFor = Exception.class)
    public PostUserRes createKakaoUserToken(PostKakaoUserReq postKakaoUserReq) throws BaseException {
        if(userProvider.checkId(postKakaoUserReq.getUserId()) ==1){
            throw new BaseException(POST_USERS_EXISTS_ID);
        }
        Long userIdx=userDao.createUserByKakao(postKakaoUserReq);
        String jwt=jwtService.createJwt(userIdx);
        return new PostUserRes(jwt,userIdx);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createKakaoUser(KakaoInfo kakaoInfo, Long userId) throws BaseException {

        try {
            userDao.createKakaoUser(kakaoInfo, userId);

        }catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
    @Transactional(rollbackFor = Exception.class)
    public void userBlock(Long userId, Long blockUserId) throws BaseException {
        try {
            userDao.userBlock(userId, blockUserId);
        }catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyProfile(Long userId, PatchProfileReq patchProfileReq) throws BaseException {
        try {
            userDao.modifyProfile(userId,patchProfileReq);
        }catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyProfileImg(Long userId, PatchProfileImgReq patchProfileImgReq) throws BaseException {
        try {
            userDao.modifyProfileImg(userId,patchProfileImgReq);
        }catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyPassword(PatchPasswordRes patchPasswordRes) throws BaseException {
        try {
            String pwd = new SHA256().encrypt(patchPasswordRes.getPassword());
            patchPasswordRes.setPassword(pwd);
            userDao.modifyPassword(patchPasswordRes);
        }catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
