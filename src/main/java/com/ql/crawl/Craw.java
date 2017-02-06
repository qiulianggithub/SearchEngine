package com.ql.crawl;

import com.ql.entity.News;
import com.ql.mongo.NewsDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Craw {

    @Autowired
    NewsDao newsDao;
	
	public  List<News> crawltechNews() throws IOException, ParseException{
		
		Whitelist.simpleText();
		//加载文档
		Document doc=Jsoup.connect("http://tech.163.com/").get();
		//获取头条新闻
		Elements es=doc.getElementsByAttributeValueContaining("class", "list_item");
		List<News> list=new ArrayList<News>();
		News news=null;
		
		for(Element element:es){
			news=new News();
			news.setTitle(element.getElementsByTag("a").text());
			news.setUrl(element.getElementsByTag("a").attr("href"));
			news.setShortContent(element.getElementsByTag("a").text());
            news.setDate(element.getElementsByClass("nl-time").text());
			doc=Jsoup.connect(element.getElementsByTag("a").attr("href")).get();
			//移除广告标签
			doc.getElementsByAttributeValue("class", "gg200x300").remove();
			if(null!=doc.getElementById("endText")){
				news.setContent(doc.getElementById("endText").children().text());
			}else{
				news.setContent("链接无法访问");
			}
			list.add(news);
		}
		return list;
		
	}

    public  List<News> crawlgdNews() throws IOException, ParseException{

        Whitelist.simpleText();
        List<News> list=new ArrayList<News>();
        for (int i=0;i<20;i++){
            String page = leftpad(2,i);
            //加载文档
            Document doc=Jsoup.connect("http://tech.163.com/special/gd2016_"+page+"/").get();
            //获取头条新闻
            Elements es=doc.getElementsByClass("newsList");
            Elements ess = es.first().children();
            News news=null;
            for(Element element:ess){
                Element a = element.getElementsByTag("a").first();
                if(a==null){
                    continue;
                }
                String date = element.getElementsByClass("sourceDate").first().text();
                date =  filterChinese(date);
                news=new News();
                news.setTitle(a.text());
                news.setUrl(a.attr("href"));
                news.setShortContent(a.text());
                news.setDate(date);
                doc=Jsoup.connect(a.attr("href")).get();
                if(null!=doc.getElementById("endText")){
                    news.setContent(doc.getElementById("endText").children().text());
                }else{
                    news.setContent("链接无法访问");
                }
                list.add(news);
            }
        }
        return list;
    }
    public  List<News> crawl163News() throws IOException, ParseException{
        Whitelist.simpleText();
        List<News> list=new ArrayList<News>();
        Document doc=Jsoup.connect("http://www.163.com").get();
        Elements es = doc.getElementsByTag("a");
        for(Element e:es){
            if(e!=null&&e.attr("href")!=null){
                Document doc2 = Jsoup.connect(e.attr("href")).get();
                News n = new News();
                if(null!=doc2.getElementById("endText")){
                    n.setTitle(e.text());
                    n.setShortContent(e.text());
                    n.setUrl(e.attr("href"));
                    n.setContent(doc.getElementById("endText").children().text());
                }else{
                    //不是新闻详情页，继续往深入爬虫
                    Document doc3 = Jsoup.connect(e.attr("href")).get();
                    Elements es2 = doc3.getElementsByTag("a");
                    for(Element e2:es2){
                        if(e2!=null&&e2.attr("href")!=null){
                            if(null!=e2.getElementById("endText")){
                                n.setTitle(e2.text());
                                n.setShortContent(e2.text());
                                n.setUrl(e2.attr("href"));
                                n.setContent(doc3.getElementById("endText").children().text());
                            }else{
                                n.setContent("链接不能访问");
                            }
                        }

                    }

                }
            }
        }
        return list;
    }

    private String filterChinese(String str) {
        String reg = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(reg);
        Matcher mat=pat.matcher(str);
        String repickStr = mat.replaceAll("");
        return repickStr;
    }

    private String leftpad(int i, int i1) {
        return i<10?"0"+i:String.valueOf(i);
    }

    public void crawAndIntoMongo(){
        try {
//            List<News> newses = crawltechNews();
            List<News> newses = crawlgdNews();
            newsDao.insertBatch(newses);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
