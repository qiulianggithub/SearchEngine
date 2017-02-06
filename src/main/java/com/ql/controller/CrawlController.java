package com.ql.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ql.crawl.Craw;
import com.ql.entity.News;

import java.util.List;

@Controller
@RequestMapping("/crawl")
public class CrawlController {
	
	@Autowired(required = false) IndexWriter indexWriter;
	@Autowired(required = false) IndexSearcher indexSearcher;

	@ResponseBody
	@RequestMapping("ntes")
	public String ntes() throws Exception{
		//抓取网易新闻头条
		List<News> list= new Craw().crawltechNews();
		//重复抓取会重复添加索引
		//indexWriter.deleteAll();
		
		Document doc = null;

		for (News news : list) {
			doc = new Document();
			
			Field title = new Field("title", news.getTitle(), Field.Store.YES,Field.Index.ANALYZED);
			Field content = new Field("content", news.getContent(),Field.Store.YES, Field.Index.ANALYZED);
			Field shortContent = new Field("shortContent", news.getShortContent(),Field.Store.YES, Field.Index.NO);
			Field url = new Field("url", news.getUrl(), Field.Store.YES,Field.Index.NO);
			Field date = new Field("date", news.getDate(), Field.Store.YES,Field.Index.NO);
			doc.add(title);
			doc.add(content);
			doc.add(shortContent);
			doc.add(url);
			doc.add(date);
			indexWriter.addDocument(doc);
		}
		indexWriter.commit();
		return JSONObject.toJSONString(list);
	}
	
}
