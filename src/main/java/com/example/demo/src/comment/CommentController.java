package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.comment.model.GetCommentInfo;
import com.example.demo.src.comment.model.PostCommentReq;
import com.example.demo.src.comment.model.PostReCommentReq;
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
    public BaseResponse<List<GetCommentInfo>> getComment(@PathVariable("userId")Long userId,@PathVariable("boardId")Long boardId){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetCommentInfo> getCommentInfo=commentProvider.getComment(userId,boardId);
            return new BaseResponse<>(getCommentInfo);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @ResponseBody
    @PostMapping("/")
    public BaseResponse<String> postComment(@RequestBody PostCommentReq postCommentReq){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(postCommentReq.getUserId() != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(commentProvider.checkUser(postCommentReq.getUserId())==1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            if(postCommentReq.getComment()==null){
                return new BaseResponse<>(POST_COMMENT_EMPTY);
            }
            if(postCommentReq.getComment().length()>200){
                return new BaseResponse<>(LONG_COMMENT_CHARACTER);
            }
            if(commentProvider.checkBoard(postCommentReq.getBoardId())==1){
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
            if(commentProvider.checkUser(userId)==1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            if(commentProvider.checkComment(commentId)==1){
                return new BaseResponse<>(NOT_EXIST_COMMENT);
            }
            commentService.postCommentLike(userId,commentId);
            String result="댓글 좋아요 성공";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @DeleteMapping("/like/{userId}/{commentId}")
    public BaseResponse<String> deleteCommentLike(@PathVariable("userId")Long userId,@PathVariable("commentId")Long commentId){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(commentProvider.checkUser(userId)==1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            if(commentProvider.checkComment(commentId)==1){
                return new BaseResponse<>(NOT_EXIST_COMMENT);
            }
            commentService.deleteCommentLike(userId,commentId);
            String result="댓글 좋아요 성공";
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
            if(commentProvider.checkUser(postCommentReq.getUserId())==1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            if(postCommentReq.getReComment()==null){
                return new BaseResponse<>(POST_COMMENT_EMPTY);
            }
            if(postCommentReq.getReComment().length()>200){
                return new BaseResponse<>(LONG_COMMENT_CHARACTER);
            }
            if(commentProvider.checkComment(postCommentReq.getCommentId())==1){
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
            if(commentProvider.checkUser(userId)==1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            if(commentProvider.checkReComment(reCommentId)==1){
                return new BaseResponse<>(NOT_EXIST_COMMENT);
            }
            commentService.postReCommentLike(userId,reCommentId);
            String result="댓글 좋아요 성공";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //대댓글 좋아요 취소
    @ResponseBody
    @DeleteMapping("/re/like/{userId}/{reCommentId}")
    public BaseResponse<String> deleteReCommentLike(@PathVariable("userId")Long userId,@PathVariable("reCommentId")Long reCommentId){
        try {
            //jwt에서 idx 추출.
            Long userIdByJwt = jwtService.getUserIdx();
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(commentProvider.checkUser(userId)==1){
                return new BaseResponse<>(NOT_EXIST_USER);
            }
            if(commentProvider.checkReComment(reCommentId)==1){
                return new BaseResponse<>(NOT_EXIST_RECOMMENT);
            }
            commentService.deleteReCommentLike(userId,reCommentId);
            String result="댓글 좋아요 성공";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
