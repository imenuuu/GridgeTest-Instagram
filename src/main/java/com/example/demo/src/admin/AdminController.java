package com.example.demo.src.admin;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.admin.model.*;
import com.example.demo.utils.JwtService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
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
        try {
            GetUserReq getUserReq = new GetUserReq(nameQuery, userIdQuery, statusQuery, dateQuery, paging);
            List<GetUserRes> getUserRes = adminProvider.getUsers(getUserReq);
            return new BaseResponse<>(getUserRes);
        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/users/{userId}")
    public BaseResponse<List<GetUserInfoRes>> getUserInfo(@PathVariable("userId")Long userId){
        try {
            List<GetUserInfoRes> getUserInfoRes = adminProvider.getUserInfo(userId);
            return new BaseResponse<>(getUserInfoRes);
        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }

    }
    @ResponseBody
    @PatchMapping("/users/{userId}")
    public BaseResponse<String> userSuspension(@PathVariable("userId")Long userId) {
        try {
            String result = "유저 계정 정지 성공";
            adminService.userSuspension(userId);
            return new BaseResponse<>(result);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/boards/{boardId}")
    public BaseResponse<List<GetBoardInfoRes>> getBoardInfo(@PathVariable("boardId")Long boardId){
        try {
            List<GetBoardInfoRes> getUserInfoRes = adminProvider.getBoardInfo(boardId);
            return new BaseResponse<>(getUserInfoRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
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
        try {
            GetBoardReq getBoardReq = new GetBoardReq(userIdQuery, statusQuery, dateQuery, paging);
            List<GetBoardRes> getBoardRes = adminProvider.getBoards(getBoardReq);
            return new BaseResponse<>(getBoardRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/report/boards")
    public BaseResponse<List<GetBoardReportRes>> getBoardReport(@RequestParam(value = "paging",defaultValue = "1")int paging ){
        try {
            List<GetBoardReportRes> getBoardReportRes = adminProvider.getBoardReport(paging);
            return new BaseResponse<>(getBoardReportRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/report/comments")
    public BaseResponse<List<GetCommentReportRes>> getCommentReport(@RequestParam(value = "paging",defaultValue = "1")int paging ){
        try {
            List<GetCommentReportRes> getCommentReportRes = adminProvider.getCommentReport(paging);
            return new BaseResponse<>(getCommentReportRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/report/reComments")
    public BaseResponse<List<GetReCommentReportRes>> getReCommentReport(@RequestParam(value = "paging",defaultValue = "1")int paging ){
        try {
            List<GetReCommentReportRes> getReCommentReportRes = adminProvider.getReCommentReport(paging);
            return new BaseResponse<>(getReCommentReportRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @DeleteMapping("/report/boards/{reportId}")
    public BaseResponse<String> deleteBoardReport(@PathVariable("reportId") Long reportId){
        try {
            String result = "신고 삭제 성공";
            adminService.deleteBoardReport(reportId);
            return new BaseResponse<>(result);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @DeleteMapping("/report/comments/{reportId}")
    public BaseResponse<String> deleteCommentReport(@PathVariable("reportId") Long reportId){
        try {
            String result = "신고 삭제 성공";
            adminService.deleteCommentReport(reportId);
            return new BaseResponse<>(result);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @DeleteMapping("/report/reComments/{reportId}")
    public BaseResponse<String> deleteReCommentReport(@PathVariable("reportId") Long reportId){
        try {
            String result = "신고 삭제 성공";
            adminService.deleteReCommentReport(reportId);
            return new BaseResponse<>(result);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/report/boards/{reportId}")
    public BaseResponse<List<GetBoardReportInfoRes>> getBoardReportInfo(@PathVariable("reportId")Long reportId){
        try{
            List<GetBoardReportInfoRes> getBoardReportInfoList=adminProvider.getBoardReportInfo(reportId);
            return new BaseResponse<>(getBoardReportInfoList);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/report/boards/{boardId}")
    public BaseResponse<String> deleteBoard(@PathVariable("boardId") Long boardId){
        try{
            String result="삭제 처리 성공";
            adminService.deleteBoard(boardId);
            return new BaseResponse<>(result);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/report/comments/{commentId}")
    public BaseResponse<String> deleteComment(@PathVariable("commentId") Long commentId){
        try{
            String result="삭제 처리 성공";
            adminService.deleteComment(commentId);
            return new BaseResponse<>(result);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/report/reComments/{reCommentId}")
    public BaseResponse<String> deleteReComment(@PathVariable("reCommentId") Long reCommentId){
        try{
            String result="삭제 처리 성공";
            adminService.deleteReComment(reCommentId);
            return new BaseResponse<>(result);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/log/boards")
    public BaseResponse<List<GetLogRes>> getBoardLog(@RequestParam(required = false,value="type",defaultValue="0") int type,
                                                     @RequestParam(required = false,value="startDate",defaultValue="all") String startDate,
                                                     @RequestParam(required = false,value="finishDate",defaultValue="all") String finishDate,
                                                     @RequestParam(required = false,value = "paging",defaultValue = "1") int paging) throws ParseException {

        String typeQuery="";
        //활성화
        System.out.println(type);
        if (type==1) {
            typeQuery = "and type='CREATE'";
        }
        //본인삭제
        else if (type==2) {
            typeQuery = "and type='UPDATE'";
        }
        //admin 에서 삭제처리
        else if (type==3) {
            typeQuery = "and type='DELETE'";
        }
        String dateQuery="";
        if(!startDate.equals("all")) {
            String startFormat = null;
            if (!startDate.equals("all")) {
                SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");
                // String 타입을 Date 타입으로 변환
                Date formatDate = dtFormat.parse(startDate);
                // Date타입의 변수를 새롭게 지정한 포맷으로 변환
                startFormat = newDtFormat.format(formatDate);
            }
            String finishFormat = null;
            if (!finishDate.equals("all")) {
                SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");
                // String 타입을 Date 타입으로 변환
                Date formatDate = dtFormat.parse(finishDate);
                // Date타입의 변수를 새롭게 지정한 포맷으로 변환
                finishFormat = newDtFormat.format(formatDate);
            }
            dateQuery = "DATE(BL.createdDate) BETWEEN '" + startFormat + "' AND '" + finishFormat + "'";

        }

        GetLogQueryReq getLogQueryReq=new GetLogQueryReq(typeQuery,dateQuery,paging);
        try {
            List<GetLogRes> getLogRes = adminProvider.getBoardLog(getLogQueryReq);
            return new BaseResponse<>(getLogRes);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }

    }

    @ResponseBody
    @GetMapping("/log/comments")
    public BaseResponse<List<GetLogRes>> getCommentLog(@RequestParam(required = false,value="type",defaultValue="0") int type,
                                                     @RequestParam(required = false,value="startDate",defaultValue="all") String startDate,
                                                     @RequestParam(required = false,value="finishDate",defaultValue="all") String finishDate,
                                                     @RequestParam(required = false,value = "paging",defaultValue = "1") int paging) throws ParseException {

        String typeQuery="";
        //활성화
        System.out.println(type);
        if (type==1) {
            typeQuery = "and type='CREATE'";
        }
        //본인삭제
        else if (type==2) {
            typeQuery = "and type='UPDATE'";
        }
        //admin 에서 삭제처리
        else if (type==3) {
            typeQuery = "and type='DELETE'";
        }
        String dateQuery="";
        if(!startDate.equals("all")) {
            String startFormat = null;
            if (!startDate.equals("all")) {
                SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");
                // String 타입을 Date 타입으로 변환
                Date formatDate = dtFormat.parse(startDate);
                // Date타입의 변수를 새롭게 지정한 포맷으로 변환
                startFormat = newDtFormat.format(formatDate);
            }
            String finishFormat = null;
            if (!finishDate.equals("all")) {
                SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");
                // String 타입을 Date 타입으로 변환
                Date formatDate = dtFormat.parse(finishDate);
                // Date타입의 변수를 새롭게 지정한 포맷으로 변환
                finishFormat = newDtFormat.format(formatDate);
            }
            dateQuery = "DATE(CL.createdDate) BETWEEN '" + startFormat + "' AND '" + finishFormat + "'";

        }

        GetLogQueryReq getLogQueryReq=new GetLogQueryReq(typeQuery,dateQuery,paging);
        try {
            List<GetLogRes> getLogRes = adminProvider.getCommentLog(getLogQueryReq);
            return new BaseResponse<>(getLogRes);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }

    }

    @ResponseBody
    @GetMapping("/log/reComments")
    public BaseResponse<List<GetLogRes>> getReCommentLog(@RequestParam(required = false,value="type",defaultValue="0") int type,
                                                       @RequestParam(required = false,value="startDate",defaultValue="all") String startDate,
                                                       @RequestParam(required = false,value="finishDate",defaultValue="all") String finishDate,
                                                       @RequestParam(required = false,value = "paging",defaultValue = "1") int paging) throws ParseException {

        String typeQuery="";
        //활성화
        if (type==1) {
            typeQuery = "and type='CREATE'";
        }
        //본인삭제
        else if (type==2) {
            typeQuery = "and type='UPDATE'";
        }
        //admin 에서 삭제처리
        else if (type==3) {
            typeQuery = "and type='DELETE'";
        }
        String dateQuery="";
        if(!startDate.equals("all")) {
            String startFormat = null;
            if (!startDate.equals("all")) {
                SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");
                // String 타입을 Date 타입으로 변환
                Date formatDate = dtFormat.parse(startDate);
                // Date타입의 변수를 새롭게 지정한 포맷으로 변환
                startFormat = newDtFormat.format(formatDate);
            }
            String finishFormat = null;
            if (!finishDate.equals("all")) {
                SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");
                // String 타입을 Date 타입으로 변환
                Date formatDate = dtFormat.parse(finishDate);
                // Date타입의 변수를 새롭게 지정한 포맷으로 변환
                finishFormat = newDtFormat.format(formatDate);
            }
            dateQuery = "DATE(CL.createdDate) BETWEEN '" + startFormat + "' AND '" + finishFormat + "'";

        }

        GetLogQueryReq getLogQueryReq=new GetLogQueryReq(typeQuery,dateQuery,paging);
        try {
            List<GetLogRes> getLogRes = adminProvider.getReCommentLog(getLogQueryReq);
            return new BaseResponse<>(getLogRes);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }

    }


}
