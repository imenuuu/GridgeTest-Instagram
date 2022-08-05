package com.example.demo.src.board;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.board.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/boards")
public class BoardController {
    final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final BoardProvider boardProvider;
    @Autowired
    private final BoardService boardService;
    @Autowired
    private final JwtService jwtService;

    public BoardController(BoardProvider boardProvider, BoardService boardService, JwtService jwtService) {
        this.boardProvider = boardProvider;
        this.boardService = boardService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> createBoard(@RequestBody PostBoardReq postBoardReq){
        try {

            Long userIdxByJwt = jwtService.getUserIdx();
            if (postBoardReq.getUserId() != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(postBoardReq.getBoardImg().size()>10){
                return new BaseResponse<>(MANY_PHOTO_BOARD);
            }
            if(postBoardReq.getDescription().length()>=1000){
                return new BaseResponse<>(LONG_NUMBER_CHARACTERS);
            }
            Long lastInsertId = boardService.createBoard(postBoardReq);
            for(BoardImg boardImg: postBoardReq.getBoardImg()){
                boardService.createBoardImg(postBoardReq.getUserId(),lastInsertId,boardImg.getBoardImgUrl());
            }
            PostLogReq postLogReq = new PostLogReq("CREATE",postBoardReq.getUserId());
            boardService.createLog(postLogReq);
            String result="게시글 등록 성공";
            return new BaseResponse<>(result);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/like/{userId}/{boardId}")
    public BaseResponse<String> postBoardLike(@PathVariable("userId") Long userId,@PathVariable("boardId") Long boardId){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            String result="";
            if(boardProvider.checkBoard(boardId)!=1){
                return new BaseResponse<>(NOT_EXIST_BOARD);
            }
            if(boardProvider.checkBoardLike(userId,boardId)!=1){
                boardService.postBoardLike(userId,boardId);
                result="좋아요 성공";
            }
            else{
                boardService.deleteBoardLike(userId,boardId);
                result="좋아요 취소 성공";
            }
            return new BaseResponse<>(result);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<List<GetBoardRes>> getMainBoard(@PathVariable("userId") Long userId,@RequestParam(value = "paging",defaultValue = "1") int paging){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetBoardRes> getBoardRes=boardProvider.getMainBoard(userId,paging);
            return new BaseResponse<>(getBoardRes);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }
    @ResponseBody
    @GetMapping("/profile/{userId}/{profileUserId}")
    public BaseResponse<List<GetBoardRes>> getProfileBoard(@PathVariable("userId") Long userId,@PathVariable("profileUserId") Long profileUserId,@RequestParam(value = "paging",defaultValue = "1") int paging) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetBoardRes> getBoardRes = boardProvider.getProfileBoard(userId, profileUserId, paging);
            return new BaseResponse<>(getBoardRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    @ResponseBody
    @PatchMapping("")
    public BaseResponse<String> patchBoard(@RequestBody PatchBoardReq patchBoardReq){
        try {
            Long userIdxByJwt = jwtService.getUserIdx();
            if (patchBoardReq.getUserId() != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(patchBoardReq.getDescription().length()>=1000){
                return new BaseResponse<>(LONG_NUMBER_CHARACTERS);
            }
            boardService.patchBoard(patchBoardReq);
            String result="게시글 수정 성공";
            PostLogReq postLogReq = new PostLogReq("UPDATE",patchBoardReq.getUserId());
            boardService.createLog(postLogReq);
            return new BaseResponse<>(result);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/report")
    public BaseResponse<String> postBoardReport(PostBoardReportReq postBoardReportReq){
        try{
            Long userIdxByJwt = jwtService.getUserIdx();
            if (postBoardReportReq.getUserId() != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(boardProvider.checkBoard((postBoardReportReq.getBoardId()))!=1){
                return new BaseResponse<>(NOT_EXIST_BOARD);
            }
            if(boardProvider.checkBoardUserId(postBoardReportReq.getBoardId())==postBoardReportReq.getUserId()){
                return new BaseResponse<>(CANT_REPORT_BOARD);
            }
            boardService.postBoardReport(postBoardReportReq);
            String result="게시글 신고 성공";
            PostLogReq postLogReq = new PostLogReq("BOARD",postBoardReportReq.getUserId());
            boardService.createReportLog(postLogReq);
            return new BaseResponse<>(result);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/drop/{userId}/{boardId}")
    public BaseResponse<String> deleteBoard(@PathVariable("userId") Long userId, @PathVariable("boardId") Long boardId){
        try{
            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(boardProvider.checkBoard(boardId)!=1){
                return new BaseResponse<>(NOT_EXIST_BOARD);
            }
            if(boardProvider.checkBoardUserId(boardId)!=userId){
                return new BaseResponse<>(NOT_DELETE_INVALID_USER);
            }
            boardService.deleteBoard(boardId);
            PostLogReq postLogReq = new PostLogReq("DELETE", userId);
            boardService.createLog(postLogReq);
            String result="게시글 삭제 성공";
            return new BaseResponse<>(result);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }


}
