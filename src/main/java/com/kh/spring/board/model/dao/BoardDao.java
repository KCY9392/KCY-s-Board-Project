package com.kh.spring.board.model.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardImg;
import com.kh.spring.board.model.vo.BoardType;
import com.kh.spring.board.model.vo.Reply;
import com.kh.spring.common.model.vo.PageInfo;

@Repository
public class BoardDao {
	public int selectListCount(SqlSession sqlSession, String boardCode) {
		return sqlSession.selectOne("boardMapper.selectListCount", boardCode);
	}
	
	public int selectListCount(SqlSession sqlSession, Map<String, Object> paramMap) {
		return sqlSession.selectOne("boardMapper.searchListCount",paramMap);
	}
	
	
	public ArrayList<Board> selectList(SqlSession sqlSession,Map<String, Object> paramMap){
		
		int offset = (((PageInfo)paramMap.get("pi")).getCurrentPage() -1)*((PageInfo)paramMap.get("pi")).getBoardLimit();
		int limit = ((PageInfo)paramMap.get("pi")).getBoardLimit();
		
		RowBounds rowBounds = new RowBounds(offset,limit);
		
		return (ArrayList) sqlSession.selectList("boardMapper.searchList", paramMap ,rowBounds);
	}
	
	
	
//	public ArrayList<Board> selectList(SqlSession sqlSession, PageInfo pi, String boardCode){
//		
//		int offset = (pi.getCurrentPage() -1)*pi.getBoardLimit();
//		int limit = pi.getBoardLimit();
//		
//		RowBounds rowBounds = new RowBounds(offset,limit);
//		
//		return (ArrayList) sqlSession.selectList("boardMapper.selectList", boardCode ,rowBounds);
//	}
	
	
	public int insertBoard(SqlSession sqlSession, Board b) {
		int result = sqlSession.insert("boardMapper.insertBoard", b);
		
		if(result > 0) {
			result = b.getBoardNo();
		}
		
		// 게시글 삽입 성공 시 -> selectkey태그를 이용해서 세팅한 boardNo값을 b에 담아서 반환시켜줌
		
		return result;
	}
	
	public int increaseCount(SqlSession sqlSession, int bno) {
		return sqlSession.update("boardMapper.increaseCount",bno);
	}
	
	public Board selectBoard(SqlSession sqlSession, int bno) {
		return sqlSession.selectOne("boardMapper.selectBoard", bno);
	}

	public ArrayList<Reply> selectReplyList(SqlSession sqlSession, int bno) {
		return (ArrayList)sqlSession.selectList("boardMapper.selectReplyList", bno);
	}

	public int insertReply(SqlSession sqlSession, Reply r) {
		return sqlSession.insert("boardMapper.insertReply", r);
	}

	public int searchListCount(SqlSession sqlSession, Map<String, String> p) {
		return sqlSession.selectOne("boardMapper.searchListCount", p);
	}

	public List<String> selectImg(SqlSession sqlSession) {
		return sqlSession.selectList("boardMapper.selectImg");
	}
	

	public List<BoardType> selectBoardTypeList(SqlSession sqlSession) {
		
		return sqlSession.selectList("boardMapper.selectBoardTypeList");
	}

	public int insertBoardImgList(SqlSession sqlSession, List<BoardImg> boardImageList) {
		
		return sqlSession.insert("boardMapper.insertBoardImgList",boardImageList);
	}

	
	public int updateBoard(SqlSession sqlSession, Board b) {
		return sqlSession.update("boardMapper.updateBoard",b);
	}

	public int updateBoardImg(SqlSession sqlSession, BoardImg img) {
		return sqlSession.update("boardMapper.updateBoardImg", img);
	}

	public int insertBoardImg(SqlSession sqlSession, BoardImg img) {
		return sqlSession.insert("boardMapper.insertBoardImg", img);
	}

	public int deleteBoardImage(SqlSession sqlSession, Map<String, Object> map) {
		return sqlSession.delete("boardMapper.deleteBoardImage", map);
	}
	
	
	
	
	

}
