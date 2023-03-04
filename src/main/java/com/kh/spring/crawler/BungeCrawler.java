package com.kh.spring.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kh.spring.crawler.model.vo.items;

public class BungeCrawler {
	
	public void crawling(items item) {
		
		//크롤링할 url
		String url = "https://www.daangn.com/hot_articles";
		
		//사이트의 html태그를 담을 Document
		Document doc = null;
		
		//document에서 가져올 요소들 담는 Elements
		Elements tmp;
		
		String title = null;
		String price = null;
		
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//리스트를 감싼 큰 태그
		Elements etitle = doc.select("section.cards-wrap");
		
		Map<String, String> map = new HashMap<String, String>();
		for(int i=0; i<etitle.size(); i++) {
			Element el = etitle.get(i);
			
			title = el.select("h2.card-title").text();
			map.put("title",title);
			
			price = el.select("div.card-price").text();
			map.put("price", price);
			
			String src = el.select("img").get(0).attr("src");
			map.put("src", src);
			
			System.out.println("제목 : "+title+"\n"+
							  "가격 : "+price+"\n"+
							  "이미지src: "+src+"\n\n");
		}
		
		//제목
//		Iterator<Element> ie1 = etitle.select("h2.card-title").iterator();
//		//이미지 링크
////		Iterator<Element> ie2 = etitle.get(0).select("img").attr("src");
//		//타이틀
//		Iterator<Element> ie3 = etitle.select("div.card-price").iterator();
//
//		while(ie1.hasNext()) {
//			System.out.println("제목: " +ie1.next().text()+"\n"
//							   +"가격: " +ie3.next().text()+"\n");
////							   +"이미지링크(src): "+ie2.next().text()+"\n");
//		}
//		
//		for(int i=0; i<8; i++) {
//			Element titles = etitle.get(i);
//			Element prices = eprice.get(i);
//			System.out.println(titles + "의 가격은 "+prices.text());
//		}
		
//			tmp = element.select("div.ellipsis.sc-hgHYgh");
//			title = tmp.get(1).toString();
			
			
//			price = tmp.text();
//			
//			item.setTitle(title);
//			item.setPrice(price);
			
//			System.out.println(item.getTitle());
		System.out.println("Crawling complete");
		
	}
}
