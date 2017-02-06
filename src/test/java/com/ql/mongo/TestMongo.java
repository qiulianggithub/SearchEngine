package com.ql.mongo;

import com.ql.entity.News;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 邱亮 on 2017/2/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext-mongodb.xml")
public class TestMongo {

    @Autowired
    NewsDao newsDao;

    @Test
    public void testaddNews(){
        News n = new News();
        n.setTitle("测试");
        n.setUrl("baidu.com");
        n.setShortContent("test");
        n.setContent("test...test......");
        n.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        newsDao.insert(n);
    }
    @Test
    public void testbatchaddNews(){
        List newes = new ArrayList();
        for(int i=0;i<1000;i++){
            News n = new News();
            n.setTitle("测试");
            n.setUrl("baidu.com");
            n.setShortContent("test");
            n.setContent("test...test......");
            n.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            newes.add(n);
        }
        newsDao.insertBatch(newes);
    }

    @Test
    public void testgetNews(){
        List<News> news = newsDao.get();
        for(News n:news){
            System.out.println(n);
        }
    }


}
