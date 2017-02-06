package com.ql.craw;

import com.ql.crawl.Craw;
import com.ql.entity.News;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by 邱亮 on 2017/2/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class TestCraw {

    @Autowired
    Craw craw;

    @Test
    public void testCraw(){
        craw.crawAndIntoMongo();
    }
    @Test
    public void testCraw2(){
        try {
            List<News> newses = craw.crawlgdNews();
            for(News news:newses){
                System.out.println("==================================");
                System.out.println(news);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testCraw3(){
        try {
            List<News> newses = craw.crawl163News();
            for(News news:newses){
                System.out.println("==================================");
                System.out.println(news);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
