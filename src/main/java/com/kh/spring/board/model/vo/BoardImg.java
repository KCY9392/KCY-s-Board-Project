package com.kh.spring.board.model.vo;

import lombok.Data;

@Data
public class BoardImg {
	private int boardImgNo;
	private String originName;
	private String changeName;
	private int refBno;
	private int imgLevel;
	
	/*
	   BOARD_IMG_NO
	   ORIGIN_NAME
	   CHANGE_NAME
	   REF_BNO
	   IMG_LEVEL
	 */
}
