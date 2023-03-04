package com.kh.spring.board.model.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.board.model.dao.BoardDao;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardImg;
import com.kh.spring.board.model.vo.BoardType;
import com.kh.spring.board.model.vo.Reply;
import com.kh.spring.common.Utils;
import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.common.template.Pagination;

@Service
public class BoardServiceImpl implements BoardService{
	
	private BoardDao boardDao;
	
	private SqlSession sqlSession;
	
	private Pagination pagination;
	
	public BoardServiceImpl(BoardDao boardDao, SqlSession sqlSession, Pagination pagination) {
		this.boardDao = boardDao;
		this.sqlSession = sqlSession;
		this.pagination = pagination;
	}
	
	@Override
	public int selectListCount(String boardCode) {
		return boardDao.selectListCount(sqlSession, boardCode);
	}
	
	@Override
	public int selectListCount(Map<String, Object> paramMap) {
		return boardDao.selectListCount(sqlSession, paramMap);
	}
	
//	@Override
//	public Map<String, Object> selectList(int currentPage, String boardCode){
//		
//		Map<String, Object> map = new HashMap();
//		
//		// 1. 페이징 처리 작업.
//		int listCount = selectListCount(boardCode);
//								
//		int pageLimit = 10;
//		int boardLimit = 5;
//		PageInfo pi = pagination.getPageInfo(listCount, currentPage, pageLimit, boardLimit);
//		map.put("pi", pi);			
//		
//		ArrayList<Board> list = boardDao.selectList(sqlSession, pi, boardCode);
//		map.put("list", list);
//		
//		return map;
//	}
	
	@Override
	public Map<String, Object> selectList(Map<String, Object> paramMap){
		
		Map<String, Object> map = new HashMap();
		
		// 1. 페이징 처리 작업.
		int listCount = selectListCount(paramMap);
					
		int pageLimit = 10;
		int boardLimit = 5;
		
		PageInfo pi = pagination.getPageInfo(listCount, ((Integer)paramMap.get("currentPage")), pageLimit, boardLimit);
		paramMap.put("pi", pi);			
		
		ArrayList<Board> list = boardDao.selectList(sqlSession, paramMap);
		map.put("list", list);
		
		return map;
	}
	
	/*
	 * 게시글 삽입 + 이미지 삽입
	 * 
	 * @Transactional : 4개의 이미지 삽입처리를 한번에 도와주는 어노테이션
	 * 
	 * AOP를 이용해서 DAO -> Service의 코드를 수행하는 시점에 예외가 발생한다면 -> rollback처리하도록 도와줌
	 * 
	 * @Transactional 선언적 트랜잭션 처리방법
	 * 		-> RuntimeException(unchecked Exception) 처리를 기본값으로 갖는다.
	 * 
	 * checked Exception : 예외처리가 필수 -> I/O관련
	 * unChecked Exception : 예외처리가 필수X -> NullPointerException, ArrayOutOfBoundsException 등등
	 * 
	 * rollbackFor : rollback을 수행하기위한 예외의 종류
	 */
	
	@Transactional(rollbackFor = {Exception.class}) //모든종류의 예외에 대해서 발생시, rollback시킨다.
	@Override
	public int insertBoard(Board b, List<MultipartFile> list, String webPath, String serverFolderPath) throws Exception{
		
		b.setBoardTitle(Utils.XSSHandling(b.getBoardTitle()));
		b.setBoardContent(Utils.XSSHandling(b.getBoardContent()));
		b.setBoardContent(Utils.newLineHandling(b.getBoardContent()));
		
		// 1) 게시글 삽입
		// 게시글 등록후, 해당게시글의 pk값을 반환받음 => boardNo
		int boardNo = boardDao.insertBoard(sqlSession, b);
		
		if(boardNo > 0 && list != null) {
			// 2) 이미지 삽입.
			
			// list -> 실제 파일이 담겨있는 리스트
			
			List<BoardImg> boardImageList = new ArrayList();
			// boardImageList : db에 등록할 데이터를 모아놓은 컬렉션
			
			List<String> renameList = new ArrayList();
			// renameList : 변경된 파일명을 저장할 리스트
			
			//list에 담겨있는 파일정보 중 실제로 업로드된 파일만 분류하기
			for(int i=0; i<list.size(); i++) {
				if(list.get(i).getSize() > 0) {//i번쨰 요소에 업로드된 이미지가 존재하는 경우
					
					//변경된 파일명 
					String changeName = Utils.saveFile(list.get(i), serverFolderPath);
					renameList.add(changeName);
					
					//BoardImg객체를 생성해서 값을 추가한 후, boardImageList에 값 하나하나 추가
					BoardImg img = new BoardImg();
					
					img.setRefBno(boardNo);//게시글 번호
					img.setImgLevel(i); //이미지 순서
					img.setOriginName(list.get(i).getOriginalFilename());//파일원본이름
					img.setChangeName(changeName);
					
					boardImageList.add(img);
				}
			}
			
			//분류작업 완료 후 boardImageList가 비어있다 ? --> 등록한 이미지가 없다.
			//							   있다면   ? --> 등록한 이미지가 있다.
			if(!boardImageList.isEmpty()) {

				int result = boardDao.insertBoardImgList(sqlSession, boardImageList);
				if(result == boardImageList.size()) {
					//삽입된 행의 개수와 업로드된 이미지 수가 같은 경우 -> 성공
					
					//서버에 이미지 저장
					for(int i=0; i<boardImageList.size(); i++) {
						int level = boardImageList.get(i).getImgLevel();
						list.get(level).transferTo(new File(serverFolderPath+renameList.get(i)));
					}
					
				}else {//이미지 삽입 실패시
					
					//강제로 예외발생시키기
					throw new Exception("예외발생");		
				}
			}
		}
		return boardNo;
		
	}
	
	

	//게시글 수정
	@Transactional(rollbackFor= {Exception.class})
	@Override
	public int updateBoard(Board b, List<MultipartFile> list, String webPath, String serverFolderPath, String deleteList) throws Exception{
		
		// 1) XSS, 개행문자 처리
		b.setBoardTitle(Utils.XSSHandling(b.getBoardTitle()));
		b.setBoardContent(Utils.XSSHandling(b.getBoardContent()));
		b.setBoardContent(Utils.newLineHandling(b.getBoardContent()));
		
		// 2) 게시글 업데이트 수정
		int result = boardDao.updateBoard(sqlSession, b);
		
		if(result > 0) {
			
			// 3) 업로드된 이미지만 분류하는 작업 수행
			List<BoardImg> boardImgList = new ArrayList();
			List<String> reNameList = new ArrayList();
			
			if(list != null) {
				
				for(int i=0; i<list.size(); i++) {
					if(list.get(i).getSize() > 0) {//업로드된 이미지가 있다?
						String changeName = Utils.saveFile(list.get(i), serverFolderPath);
						reNameList.add(changeName);
						
						//BoardImg객체를 생성해서 값을 추가한 후, boardImageList에 값 하나하나 추가
						BoardImg img = new BoardImg();
						
						img.setRefBno(b.getBoardNo());//게시글 번호
						img.setImgLevel(i); //이미지 순서
						img.setOriginName(list.get(i).getOriginalFilename());//파일원본이름
						img.setChangeName(changeName);
						
						boardImgList.add(img);
					}
				}
			}
			
			
			// 4) x버튼 눌렀을 때, 해당 이미지를 db에서 삭제하는 용도로 만들기
			if(deleteList != null && !deleteList.equals("")) {
				
				Map<String, Object> map = new HashMap();
				
				map.put("boardNo", b.getBoardNo());
				map.put("deleteList", deleteList);
				
				result = boardDao.deleteBoardImage(sqlSession, map);
			}
			
			
			// 5) db에서 삭제를 성공했다면
			if(result > 0) {
				
				//boardImg객체 하나하나 업데이트
				for(  BoardImg img : boardImgList ) {
					result = boardDao.updateBoardImg(sqlSession, img); //변경된 이름, 원본파일이름, 게시글 번호, 레벨
				
					//결과가 1이다 -> 수정작업 성공 -> 기존에 이미 이미지가 있었다.
					//결과가 0이다 -> 수정작업 실패 -> 기존에 이미지가 없었다.
					
					// 6) 결과값이 0이면 update에는 실패했고, 실제로 db에 올라가야하는 데이터이기 때문에 insert해야함
					if(result == 0) {
						result = boardDao.insertBoardImg(sqlSession, img);
						
						// -> 값을 하나씩 대입해서 삽입하는 경우, 결과가 0이 나올수가 없다.
						// 단, 예외는 발생할 수 있음
					}
				}
			}
			
			// 7) 업로드된 이미지가 있다면 서버에 저장
			if(!boardImgList.isEmpty() && result != 0) {
				for(int i=0; i<boardImgList.size(); i++) {
					int level = boardImgList.get(i).getImgLevel();
					
					list.get(level).transferTo(new File(serverFolderPath+reNameList.get(i)));
				}
			}
			
			
			
		}
		return result;
	}
	
	
	@Override
	public int increaseCount(int bno) {
		return boardDao.increaseCount(sqlSession, bno);
	}
	
	@Override
	public Board selectBoard(int bno) throws Exception {
		
//		if(true) {
//			throw new Exception("강제예외처리");
//		}
		return boardDao.selectBoard(sqlSession, bno);
	}
	
	@Override
	public ArrayList<Reply> selectReplyList(int bno){
		return boardDao.selectReplyList(sqlSession, bno);
	}
	
	@Override
	public int insertReply(Reply r) {
		return boardDao.insertReply(sqlSession, r);
	}
	
	
	@Override
	public List<String> selectImg() {
		return boardDao.selectImg(sqlSession);
	}

	
	@Override
	public List<BoardType> selectBoardTypeList(){
		return boardDao.selectBoardTypeList(sqlSession);
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
