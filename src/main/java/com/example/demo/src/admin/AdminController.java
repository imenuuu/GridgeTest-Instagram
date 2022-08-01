package com.example.demo.src.admin;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.admin.model.GetUserReq;
import com.example.demo.src.admin.model.GetUserRes;
import com.example.demo.src.board.BoardProvider;
import com.example.demo.src.board.BoardService;
import com.example.demo.src.board.model.GetBoardRes;
import com.example.demo.utils.JwtService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/admins")
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
                                                   @RequestParam(required = false,value="status",defaultValue="all") String status,
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
        if(!status.equals("all")) {
            //활성화
            if (status=="activation") {
                statusQuery = "and userStatus='TRUE'";
            }
            //탈퇴
            else if (status=="drop") {
                statusQuery = "and dropStatus='TRUE'";
            }
            //정지
            else if (status=="suspension") {
                statusQuery = "and suspensionStatus='TRUE'";
            }
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
            dateQuery="and DATE(createdDate)='"+strNewDtFormat+"'";
        }
        System.out.println(date);
        System.out.println(dateQuery);
        GetUserReq getUserReq=new GetUserReq(nameQuery,userIdQuery,statusQuery,dateQuery,paging);
        List<GetUserRes> getUserRes=adminProvider.getUsers(getUserReq);
        return new BaseResponse<>(getUserRes);
    }

}
