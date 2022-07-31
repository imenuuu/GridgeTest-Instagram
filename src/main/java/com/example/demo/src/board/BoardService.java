package com.example.demo.src.board;

import com.example.demo.src.board.model.PatchBoardReq;
import com.example.demo.src.board.model.PostBoardReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class BoardService {
    private final BoardDao boardDao;
    private final BoardProvider boardProvider;
    @Autowired
    DataSource dataSource;

    @Autowired
    public BoardService(BoardDao boardDao, BoardProvider boardProvider) {
        this.boardDao = boardDao;
        this.boardProvider = boardProvider;
    }

    public Long createBoard(PostBoardReq postBoardReq) {
        return boardDao.createBoard(postBoardReq);
    }

    public void createBoardImg(Long userId, Long lastInsertId, String boardImgUrl) {
        boardDao.createBoardImg(userId,lastInsertId,boardImgUrl);
    }

    public void patchBoard(PatchBoardReq patchBoardReq) {
        boardDao.patchBoard(patchBoardReq);
    }
}
