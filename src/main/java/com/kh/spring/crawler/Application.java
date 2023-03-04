package com.kh.spring.crawler;

import java.util.ArrayList;

import com.kh.spring.crawler.model.vo.items;

public class Application {
	
	public static void main(String[] args) {
		BungeCrawler Bunge = new BungeCrawler();
		
			items item = new items();
			Bunge.crawling(item);
	}
}
