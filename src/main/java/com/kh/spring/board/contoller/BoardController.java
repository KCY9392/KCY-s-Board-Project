package com.kh.spring.board.contoller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.Reply;
import com.kh.spring.common.Utils;
import com.kh.spring.common.template.Pagination;
import com.kh.spring.member.model.vo.Member;

@Controller
@RequestMapping("/board")
// 클래스에서도 리퀘스트매핑 추가가능.
// 그럼 현재 컨트롤러는 /spring/board 의 경로로 들어오는 모든 url요청을 받아준다.
public class BoardController {

	private BoardService boardService;
	
	
	
	public BoardController(BoardService boardService , Pagination pagination) {
		this.boardService =  boardService;
		
	}
	
	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	
	
	
	@RequestMapping("/list/{boardCode}")
	//@PathVariable("value") : URL경로에 포함되어있는 값을 변수로 사용할 수 있게 해준다. 
	// => 자동으로 requestScope에 저장된다. 
	// => jsp에서도 el태그로 사용가능하다.
	
	// /spring/board/list/C 혹은 T 혹은 N ...
	public String selectList(
			@PathVariable("boardCode") String boardCode,
			@RequestParam(value="cpage", defaultValue="1") int currentPage ,Model model,
			@RequestParam Map<String,Object> paramMap) {
			//검색요청이 들어왔다면 paramMap안에는 key, condition이 존재할 것임.
		
		Map<String, Object> map = new HashMap();
		
		paramMap.put("currentPage", currentPage);
		paramMap.put("boardCode", boardCode);
		
		if(paramMap.get("condition") == null) { //검색요청을 하지않은 경우
			
			map = boardService.selectList(paramMap);
			
		}else { //검색요청을 한 경우
			
			//검색에 필요한 데이터를 paramMap을 넣어서 호출
			//condition, keyword
			map = boardService.selectList(paramMap);
				
		}
		
		model.addAttribute("map" , map);
		model.addAttribute("paramMap", paramMap);
		
		logger.info(currentPage+"boardList");
		
		return "board/boardListView";

	}
	
	@RequestMapping("/enrollForm/{boardCode}")
	public String boardEnrollForm(@PathVariable("boardCode") String boardCode, 
								  Model model,
								  @RequestParam(value="mode", required=false, defaultValue="insert") String mode,
								  @RequestParam(value="bno", required=false, defaultValue="0") int bno) {
		
		if(mode.equals("update")) {
			//수정하기페이지 요청
			try {
				Board b = boardService.selectBoard(bno);
				
				//개행문자가 <br>태그로 치환되어있는 상태
				//textarea에 출력할 것이기 때문에 \n으로 변경해줌
				b.setBoardContent(Utils.newLineClear(b.getBoardContent()));
				
				model.addAttribute("b",b);
				
			} catch (Exception e) {
				logger.error("selectBoard메소드 에러");
				e.printStackTrace();
			}
		}
		
		if(boardCode.equals("C")) {
			
			return "board/boardEnrollForm";
		
		}else {
			
			return "board/boardPictureEnrollForm";

		}
		
	}
	
	@RequestMapping("/insert/{boardCode}")
	public String insertBoard(Board b , 
							  @RequestParam(value="images",required=false) List<MultipartFile> imgList,
							  MultipartFile upfile, // 첨부파일
							  @PathVariable("boardCode") String boardCode,
							  HttpSession session, 
							  Model model,
							  @RequestParam(value="mode", required=false, defaultValue="insert" ) String mode,
							  @RequestParam(value="deleteList" , required=false) String deleteList) throws Exception {
		
		// 사진게시판 이미지들 저장경로 얻어오기
		String webPath = "/resources/images/boardT/";
		
		// 서버상에서 실제파일 경로 얻어오기
		String serverFolderPath = session.getServletContext().getRealPath(webPath);
		b.setBoardCd(boardCode);
		int result = 0;

		
		// 첨부파일을 선택하고 안하고 상관없이 객체는 생성됨(upfile) 
		// 전달된 파일이 있는지 없는지는 filename으로 첨부파일 유무 확인. 
		
		// 전달된 파일이 있을경우 -> 파일명 수정 작업후 서버업로드 -> 원본파일명과, 수정된 파일명, 서버에 업로드된 경로를 b에 안에담기
		if(!upfile.getOriginalFilename().equals("")) {
			String savePath = session.getServletContext().getRealPath("/resources/uploadFiles/");
			String changeName = Utils.saveFile(upfile, savePath);
			
			b.setChangeName("resources/uploadFiles/"+changeName);
			b.setOriginName(upfile.getOriginalFilename());
		}
		//db에 board테이블에 데이터 추가		
		
		
		try {
			result = boardService.insertBoard(b, imgList, webPath, serverFolderPath);
		} catch (Exception e) {
			logger.error("에러발생");
		}
		
		if(mode.equals("insert")) {
			
		}else {//수정
		
			//게시글 수정 서비스 호출
			// b객체안에 boardNo값 추가하기
			result = boardService.updateBoard(b, imgList, webPath, serverFolderPath, deleteList);
		}
		//성공적으로 추가시 
		// alertMsg 게시글을 작성하셨습니다. -> session
		// list.bo
		if(result > 0) {
			session.setAttribute("alertMsg", "게시글을 작성에 성공하셨습니다.");
			return "redirect:../list/"+boardCode;
		}else {// errorPage 
			model.addAttribute("errorMsg","게시글 작성 실패");
			return "common/errorPage";
		}
		
		
		// 실패시
		// errorMsg 게시글 작성 실패 -> request
		// errorpage
		
	}
	
	@RequestMapping("/detail/{boardCode}/{boardNo}")
	public ModelAndView selectBoard(
			@PathVariable("boardCode") String boardCode,
			@PathVariable("boardNo") int boardNo,
			HttpSession session,
			HttpServletRequest req,
			HttpServletResponse res,
			ModelAndView mv) throws Exception{
		
		Board b = boardService.selectBoard(boardNo);
		
		// 쿠키를 이용해서 조회수 중복으로 증가되지 않도록 방지 + 본인의 글은 애초에 조회수가 증가되지않도록
		if(b != null) { //상세 조회 성공
			Member loginUser = (Member)session.getAttribute("loginUser");
			
			int userNo = 0;
			if(loginUser != null) {
				userNo = loginUser.getUserNo();
			}
			
			//작성자의 번호와 현재세션의 유저번호가 같지않을 때만 조회수 증가
			if(!b.getBoardWriter().equals(userNo+"")) {
				
				Cookie cookie = null; //기존의 존재하던 쿠키를 저장하는 변수
				
				Cookie[] cArr = req.getCookies(); //쿠키얻기
				
				if(cArr != null && cArr.length > 0) {
					for( Cookie c : cArr) {
						if(c.getName().equals("readBoardNo")) {
							cookie = c;
							break;
						}
					}
				}
				
				int result = 0;
				if(cookie == null) { //원래 readBoardNo라는 이름의 쿠키가 없다
					//쿠키생성
					cookie = new Cookie("readBoardNo", boardNo+"");
					
					//조회수 증가서비스 호출
					//1. 조회수 증가
					result = boardService.increaseCount(boardNo);

				}else { //기존에 readboardNo라는 이름의 쿠키가 있다 -> 쿠키에 저장된 값 뒤쪽에 현재 조회된 게시글번호(boardNo) 추가
						//										단, 기존쿠키값에 중복되는 번호가 없는 경우에만 추가
					
					String[] arr = cookie.getValue().split("/");
					
					//"readBoardNo" : 1/2/5/14/103/115 -> [1,2,5,14,103,115]
					
					List<String> list = Arrays.asList(arr); //컬렉션으로 변환
					
					// "100".indexOf("100") -> 0
					// "100".indexOf("10") -> 0
					// "100".indexOf("1") -> 0
					
					// List.indexOf(Object) : 
					// - list안에서 매개변수로 들어온 Object와 일치하는 부분의 인덱스 반환
					// - 일치하는 부분이 없으면 -1 반환
					
					if(list.indexOf(boardNo+"") == -1) {
						//기존값에 같은 글 번호가 없다면,
						
						cookie.setValue(cookie.getValue()+"/"+boardNo);
						
						//조회수 증가서비스 호출
						result = boardService.increaseCount(boardNo);
					}
				}
				
				if(result > 0) { // 성공적으로 조회수가 증가되었다.
					b.setCount(b.getCount()+1);
					
					cookie.setPath(req.getContextPath());
					cookie.setMaxAge(60*60*1); //1시간
					res.addCookie(cookie); //응답객체인 response에 쿠키가 담겨져서 사용자에게 전송됨
					
					
				}
			}
			//상세보기할 정보를 조회
			
			mv.addObject("b",b);
			mv.setViewName("board/boardDetailView");
		}else {
			
			mv.addObject("errorMsg","게시글 조회 실패");
			mv.setViewName("common/errorPage");
			
		}
		
		return mv;
	}
	
	
	//댓글목록 불러오기
	@ResponseBody
	@RequestMapping(value="reply.bo", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	//responseBody : 별도의 뷰페이지가 아니라 리턴값을 직접 지정해야하는 경우 사용
	public String selectReplyList(@RequestParam("bno") int boardNo) throws Exception{
		
		int bno = (boardNo == 0) ? 1 : boardNo;
		
		//댓글목록 조회
		String result = "ddd";
		ArrayList<Reply> list = boardService.selectReplyList(bno);
		
		//gson으로 파싱
		if (!list.isEmpty()) {
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
			// [reply, reply, reply]
			return gson.toJson(list);
		}

		return result;
	}
	
	//댓글등록하기
	@ResponseBody
	@RequestMapping("rinsert.bo")
	public String insertReply(Reply r, HttpSession session) {
		
		Member m = (Member)session.getAttribute("loginUser");
		
		if(m != null) {
			r.setReplyWriter(m.getUserNo()+"");
		}
			int result = boardService.insertReply(r);
			
			if(result > 0) {
				return "success";
			}else {
				return "fail";
			}
		
	}
	
	
	
	
	
	//내가 만든 search.bo
//	@RequestMapping("search.bo")
//	public String searchBoard(@RequestParam(value="cpage", defaultValue="1") int currentPage ,
//							 String condition, String keyword,
//			  				 Model model) {
//		
//		Map<String, String> p = new HashMap<>();
//		p.put("condition", condition);
//		p.put("keyword", keyword);
//		
//		int slistCount = boardService.searchListCount(p);
//		
//		int pageLimit = 10;
//		int boardLimit = 5;
//		PageInfo pi = pagination.getPageInfo(slistCount, currentPage, pageLimit, boardLimit);
//		
//		// 2. 게시글 셀렉트
//		ArrayList<Board> list = boardService.searchList(pi, p);
//			
//		// 3. 페이지 포워딩(pi 객체와 , list객체 저장시키면서)
//		model.addAttribute("list" , list);
//		model.addAttribute("pi",pi);
//		model.addAttribute("keyword", keyword);
//		model.addAttribute("condition", condition);
//		
//		return "board/boardListView";
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
