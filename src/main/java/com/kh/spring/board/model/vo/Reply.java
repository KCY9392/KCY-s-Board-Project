package com.kh.spring.board.model.vo;

import java.sql.Date;

import lombok.Data;

@Data
public class Reply {

	private String replyNo;			//REPLY_NO
	private String replyContent;	//REPLY_CONTENT
	private String refBNo;			//REF_BNO
	private String replyWriter;		//REPLY_WRITER
	private String createDate;		//CREATE_DATE
	private String status;			//STATUS
//
}
