package com.example.demo.src.board;

import com.example.demo.config.BaseException;
import com.example.demo.src.board.model.GetBoardRes;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class BoardProvider {
    private final BoardDao boardDao;

    public BoardProvider(BoardDao boardDao) {
        this.boardDao = boardDao;
    }

    public List<GetBoardRes> getMainBoard(Long userId,int paging) throws BaseException {
        try {
            return boardDao.getMainBoard(userId, paging);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        }

    public Long checkBoardUserId(Long boardId) {
        return boardDao.checkBoardUserId(boardId);
    }

    public List<GetBoardRes> getProfileBoard(Long userId, Long profileUserId,int paging) {
        return boardDao.getProfileBoard(userId, profileUserId, paging);
    }

    public int checkBoardLike(Long userId, Long boardId) throws BaseException {
        try {
            return boardDao.checkBoardLike(userId, boardId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }


    }

    public int checkBoard(Long boardId) throws BaseException {
        try{
            return boardDao.checkBoard(boardId);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
