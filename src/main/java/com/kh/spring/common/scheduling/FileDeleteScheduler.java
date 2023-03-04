package com.kh.spring.common.scheduling;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kh.spring.board.model.service.BoardService;

@Component
public class FileDeleteScheduler{
	
	//sysout쓰지않기위해 logger등록
	private Logger logger = LoggerFactory.getLogger(FileDeleteScheduler.class);

	
	@Autowired
	private BoardService boardService;
	
	@Autowired
	private ServletContext application; //서버폴더경로를 얻어오기위해 사용
	
	@Scheduled(fixedDelay=5000)
	public void deleteFile() {
		logger.info("파일삭제시작");
		
		//1. Board테이블안에 있는 이미지 목록들을 조회하여
		List<String> list = boardService.selectImg();
		
		String[] str = new String[list.size()];
		
		//2. resource/uploadFiles디렉토리안에 있는 이미지들과 대조하여
		File path = new File(application.getRealPath("/resources/uploadFiles"));
		File [] files = path.listFiles(); //path경로가 참조하고있는 폴더에 들어가있는 모든 파일을 얻어와서 File배열로 반환
		List<File> fileList = Arrays.asList(files);
		
		logger.info(list.toString());
		logger.info(fileList.toString());
		
		
		//3. 두 목록을 비교해서 일치하지않는 이미지 파일을 삭제
		// file안의 delete(); 를 사용
		if(!list.isEmpty()) {
			//server : C:\Spring-workspace .. xxx.jpg
			//list	 : xxx.jpg
			
			for( File serverFile : fileList ) {
				String fileName = serverFile.getName(); //파일명만 얻어오는 메소드
				//server : xxx.jpg
				//list	 : xxx.jpg
				
				// List.indexOf(value) : List에 value과 같은 값이 있으면 인덱스를 반환 / 없으면 -1을 반환
				if(list.indexOf(fileName) == -1) {
					
					// select해온 db목록에는 없는 데, 실제 server에는 저장된 파일인경우
					logger.info(serverFile.getName()+"삭제함");
					serverFile.delete();
				}
			}
		}
		logger.info("서버파일 삭제작업 끝");
		
	}
}
