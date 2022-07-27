package com.example.demo.src.board;

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

    public Long createBoard(Long userId, String description) {
        return boardDao.createBoard(userId,description);
    }

    public void createBoardImg(Long userId, Long lastInsertId, String boardImgUrl) {
        boardDao.createBoardImg(userId,lastInsertId,boardImgUrl);
    }
}
