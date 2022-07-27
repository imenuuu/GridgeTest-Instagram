package com.example.demo.src.board;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.board.model.BoardImg;
import com.example.demo.src.board.model.GetBoardImgRes;
import com.example.demo.src.board.model.GetBoardRes;
import com.example.demo.src.board.model.PostBoardReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

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
    @PostMapping("/{userId}")
    public BaseResponse<String> createBoard(@PathVariable("userId") Long userId, @RequestBody PostBoardReq postBoardReq){
        try {

            Long userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            Long lastInsertId = boardService.createBoard(userId,postBoardReq.getDescription());
            for(BoardImg boardImg: postBoardReq.getBoardImg()){
                boardService.createBoardImg(userId,lastInsertId,boardImg.getBoardImgUrl());
            }
            String result="게시글 등록 성공";
            return new BaseResponse<>(result);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<List<GetBoardRes>> getMainBoard(@PathVariable("userId") Long userId,@RequestParam("paging") int paging){
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

}
