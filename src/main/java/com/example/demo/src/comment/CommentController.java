package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.board.model.PostBoardReportReq;
import com.example.demo.src.comment.model.*;
import com.example.demo.src.follow.FollowProvider;
import com.example.demo.src.follow.FollowService;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/comments")
public class CommentController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final CommentProvider commentProvider;
    @Autowired
    private final CommentService commentService;
    @Autowired
    private final JwtService jwtService;

    public CommentController(CommentProvider commentProvider,CommentService commentService, JwtService jwtService){
        this.commentProvider = commentProvider;
        this.commentService = commentService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("/{userId}/{boardId}")
    public BaseResponse<List<GetCommentInfo>> getComment(@PathVariable("userId")Long userId,@PathVariable("boardId")Long boardId,@RequestParam(value = "paging",defaultValue = "1")int paging){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetCommentInfo> getCommentInfo=commentProvider.getComment(userId,boardId,paging);
            return new BaseResponse<>(getCommentInfo);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/re/{userId}/{commentId}")
    public BaseResponse<List<GetReCommentReq>> getReComment(@PathVariable("userId")Long userId, @PathVariable("commentId")Long commentId,@RequestParam(value = "paging",defaultValue = "1")int paging){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetReCommentReq> getReCommentReq=commentProvider.getReComment(userId,commentId,paging);
            return new BaseResponse<>(getReCommentReq);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> postComment(@RequestBody PostCommentReq postCommentReq){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(postCommentReq.getUserId() != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(commentProvider.checkUser(postCommentReq.getUserId())!=1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            if(postCommentReq.getComment().length()<1){
                return new BaseResponse<>(POST_COMMENT_EMPTY);
            }
            if(postCommentReq.getComment().length()>200){
                return new BaseResponse<>(LONG_COMMENT_CHARACTER);
            }
            if(commentProvider.checkBoard(postCommentReq.getBoardId())!=1){
                return new BaseResponse<>(NOT_EXIST_BOARD);
            }
            commentService.postComment(postCommentReq);
            String result="댓글 달기 성공";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/like/{userId}/{commentId}")
    public BaseResponse<String> postCommentLike(@PathVariable("userId")Long userId,@PathVariable("commentId")Long commentId){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(commentProvider.checkUser(userId)!=1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            if(commentProvider.checkComment(commentId)!=1){
                return new BaseResponse<>(NOT_EXIST_COMMENT);
              }
            String result="";
            if(commentProvider.checkCommentLike(userId,commentId)==1){
                commentService.deleteCommentLike(userId,commentId);
                result="댓글 좋아요 취소 성공";
            }
            else {
                commentService.postCommentLike(userId, commentId);
                result="댓글 좋아요 성공";
            }

            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ResponseBody
    @PostMapping("/re")
    public BaseResponse<String> postReComment(@RequestBody PostReCommentReq postCommentReq){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(postCommentReq.getUserId() != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(commentProvider.checkUser(postCommentReq.getUserId())!=1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            if(postCommentReq.getReComment().length()<1){
                return new BaseResponse<>(POST_COMMENT_EMPTY);
            }
            if(postCommentReq.getReComment().length()>200){
                return new BaseResponse<>(LONG_COMMENT_CHARACTER);
            }
            if(commentProvider.checkComment(postCommentReq.getCommentId())!=1){
                return new BaseResponse<>(NOT_EXIST_COMMENT);
            }
            commentService.postReComment(postCommentReq);
            String result="대댓글 달기 성공";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //대댓글좋아요
    @ResponseBody
    @PostMapping("/re/like/{userId}/{reCommentId}")
    public BaseResponse<String> postReCommentLike(@PathVariable("userId")Long userId,@PathVariable("reCommentId")Long reCommentId){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(commentProvider.checkUser(userId)!=1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            if(commentProvider.checkReComment(reCommentId)!=1){
                return new BaseResponse<>(NOT_EXIST_RECOMMENT);
            }
            String result="";
            if(commentProvider.checkReCommentLike(userId,reCommentId)==1){
                commentService.deleteReCommentLike(userId,reCommentId);
                result="대댓글 좋아요 취소 성공";
            }
            else{
                commentService.postReCommentLike(userId,reCommentId);
                result="대댓글 좋아요 성공";
            }

            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/report")
    public BaseResponse<String> postCommentReport(PostCommentReportReq postCommentReportReq){
        try{
            Long userIdxByJwt = jwtService.getUserIdx();
            if (postCommentReportReq.getUserId() != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(commentProvider.checkBoardUserId(postCommentReportReq.getCommentId())==postCommentReportReq.getUserId()){
                return new BaseResponse<>(CANT_REPORT_COMMENT);
            }
            commentService.postCommentReport(postCommentReportReq);
            String result="댓글 신고 성공";
            return new BaseResponse<>(result);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

}
