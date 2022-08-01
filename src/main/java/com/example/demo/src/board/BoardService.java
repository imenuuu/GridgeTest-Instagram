package com.example.demo.src.board;

import com.example.demo.config.BaseException;
import com.example.demo.src.board.model.PatchBoardReq;
import com.example.demo.src.board.model.PostBoardReportReq;
import com.example.demo.src.board.model.PostBoardReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

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

    public Long createBoard(PostBoardReq postBoardReq) throws BaseException {
        try {
            return boardDao.createBoard(postBoardReq);
        }catch (Exception exception){
                throw new BaseException(DATABASE_ERROR);
            }
    }

    public void createBoardImg(Long userId, Long lastInsertId, String boardImgUrl) throws BaseException {
        try {
            boardDao.createBoardImg(userId, lastInsertId, boardImgUrl);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void patchBoard(PatchBoardReq patchBoardReq) throws BaseException {
        try {
            boardDao.patchBoard(patchBoardReq);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        }

    public void postBoardReport(PostBoardReportReq postBoardReportReq) throws BaseException {
        try{
            boardDao.postBoardReport(postBoardReportReq);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
