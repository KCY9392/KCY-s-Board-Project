package com.kh.spring.chat.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;

@Service
public interface ChatService {
	
	public List<ChatRoom> selectChatRoomList();

	public int openChatRoom(ChatRoom cr);

	public int insertMessage(ChatMessage chatMessage);

	public List<ChatMessage> joinChatRoom(ChatRoomJoin join);

	public int exitChatRoom(ChatRoomJoin join);
	
}
