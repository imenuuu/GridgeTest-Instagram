package com.example.demo.src.admin;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.admin.model.*;
import com.example.demo.utils.JwtService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private final AdminProvider adminProvider;
    @Autowired
    private final AdminService adminService;
    @Autowired
    private final JwtService jwtService;

    public AdminController(AdminProvider adminProvider, AdminService adminService, JwtService jwtService) {
        this.adminProvider = adminProvider;
        this.adminService = adminService;
        this.jwtService = jwtService;
    }

    @SneakyThrows
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false,defaultValue="all") String name,
                                                   @RequestParam(required = false,value="userId",defaultValue="all") String userId,
                                                   @RequestParam(required = false,value="status",defaultValue="0") int status,
                                                   @RequestParam(required = false,value="date",defaultValue="all") String date,
                                                   @RequestParam(required = false,value = "paging",defaultValue = "1") int paging){
        String nameQuery="";
        if(!name.equals("all")){
            nameQuery="and name like '%"+name+"%'";
        }

        String userIdQuery="";
        if(!userId.equals("all")){
            userIdQuery="and userId like '%"+userId+"%'";
        }
        System.out.println(status);
        String statusQuery="";
            //활성화
            if (status==1) {
                statusQuery = "and userStatus='TRUE'";
            }
            //탈퇴
            else if (status==2) {
                statusQuery = "and userStatus='FALSE'";
            }
            //정지
            else if (status==3) {
                statusQuery = "and suspensionStatus='TRUE'";
            }
        String dateQuery="";
        if(!date.equals("all")){
            SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");
            // String 타입을 Date 타입으로 변환
            Date formatDate = dtFormat.parse(date);
            // Date타입의 변수를 새롭게 지정한 포맷으로 변환
            String strNewDtFormat = newDtFormat.format(formatDate);
            dateQuery="and DATE(createdDate)='"+strNewDtFormat+"'";
        }
        System.out.println(date);
        System.out.println(dateQuery);
        GetUserReq getUserReq=new GetUserReq(nameQuery,userIdQuery,statusQuery,dateQuery,paging);
        List<GetUserRes> getUserRes=adminProvider.getUsers(getUserReq);
        return new BaseResponse<>(getUserRes);
    }

    @ResponseBody
    @GetMapping("/users/{userId}")
    public BaseResponse<List<GetUserInfoRes>> getUserInfo(@PathVariable("userId")Long userId){
        List<GetUserInfoRes> getUserInfoRes=adminProvider.getUserInfo(userId);
        return new BaseResponse<>(getUserInfoRes);

    }
    @ResponseBody
    @PatchMapping("/users/{userId}")
    public BaseResponse<String> userSuspension(@PathVariable("userId")Long userId){
        String result="유저 계정 정지 성공";
        adminService.userSuspension(userId);
        return new BaseResponse<>(result);
    }

    @ResponseBody
    @GetMapping("/boards/{boardId}")
    public BaseResponse<List<GetBoardInfoRes>> getBoardInfo(@PathVariable("boardId")Long boardId){
        List<GetBoardInfoRes> getUserInfoRes=adminProvider.getBoardInfo(boardId);
        return new BaseResponse<>(getUserInfoRes);
    }

    @SneakyThrows
    @ResponseBody
    @GetMapping("/boards")
    public BaseResponse<List<GetBoardRes>> getBoard(@RequestParam(required = false,value="userId",defaultValue="all") String userId,
                                                    @RequestParam(required = false,value="status",defaultValue="0") int status,
                                                    @RequestParam(required = false,value="date",defaultValue="all") String date,
                                                    @RequestParam(required = false,value = "paging",defaultValue = "1") int paging){

        String userIdQuery="";
        if(!userId.equals("all")){
            userIdQuery="and userId like '%"+userId+"%'";
        }
        System.out.println(status);
        String statusQuery="";
            //활성화
            if (status==1) {
                statusQuery = "and status='TRUE'";
            }
            //본인삭제
            else if (status==2) {
                statusQuery = "and status='FALSE'";
            }
            //admin 에서 삭제처리
            else if (status==3) {
                statusQuery = "and suspensionStatus='TRUE'";
            }
        System.out.println(date);
        String dateQuery="";
        if(!date.equals("all")){
            SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");
            // String 타입을 Date 타입으로 변환
            Date formatDate = dtFormat.parse(date);
            // Date타입의 변수를 새롭게 지정한 포맷으로 변환
            String strNewDtFormat = newDtFormat.format(formatDate);
            dateQuery="and DATE(B.createdDate)='"+strNewDtFormat+"'";
        }
        GetBoardReq getBoardReq=new GetBoardReq(userIdQuery,statusQuery,dateQuery,paging);
        List<GetBoardRes> getBoardRes=adminProvider.getBoards(getBoardReq);
        return new BaseResponse<>(getBoardRes);
    }

    @ResponseBody
    @GetMapping("/report/boards")
    public BaseResponse<List<GetBoardReportRes>> getBoardReport(@RequestParam(value = "paging",defaultValue = "1")int paging ){
        List<GetBoardReportRes> getBoardReportRes = adminProvider.getBoardReport(paging);
        return new BaseResponse<>(getBoardReportRes);
    }

    @ResponseBody
    @GetMapping("/report/comments")
    public BaseResponse<List<GetCommentReportRes>> getCommentReport(@RequestParam(value = "paging",defaultValue = "1")int paging ){
        List<GetCommentReportRes> getCommentReportRes = adminProvider.getCommentReport(paging);
        return new BaseResponse<>(getCommentReportRes);
    }

    @ResponseBody
    @GetMapping("/report/reComments")
    public BaseResponse<List<GetReCommentReportRes>> getReCommentReport(@RequestParam(value = "paging",defaultValue = "1")int paging ){
        List<GetReCommentReportRes> getReCommentReportRes = adminProvider.getReCommentReport(paging);
        return new BaseResponse<>(getReCommentReportRes);
    }


}
