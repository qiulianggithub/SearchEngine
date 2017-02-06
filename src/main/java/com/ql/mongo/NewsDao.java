package com.ql.mongo;

import com.ql.entity.News;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.List;

public class NewsDao {

	private MongoOperations mongoTemplate;

	public void setMongoTemplate(MongoOperations mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public List<News> get() {
		List<News> news = mongoTemplate.findAll(News.class);
		return news;
	}


	public void insert(News u) {
		mongoTemplate.insert(u);
	}

    public void insertBatch(List<News> newses){
        mongoTemplate.insertAll(newses);
    }



}
