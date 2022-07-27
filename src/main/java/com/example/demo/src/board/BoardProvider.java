package com.example.demo.src.board;

import com.example.demo.src.board.model.GetBoardImgRes;
import com.example.demo.src.board.model.GetBoardRes;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardProvider {
    private final BoardDao boardDao;

    public BoardProvider(BoardDao boardDao) {
        this.boardDao = boardDao;
    }

    public List<GetBoardRes> getMainBoard(Long userId,int paging) {
        List<GetBoardRes> getBoardImgRes=boardDao.getMainBoard(userId,paging);
        return getBoardImgRes;
    }
}
