package com.ql.crawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 邱亮 on 2017/2/6.
 */
public class GetSinaInfo {

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        getSinaInforamtion();
    }
    public static  void getSinaInforamtion()
    {
        Map<String,String> pathMap=createNewFiles();
        try {
            getSinaYaoWen(pathMap);
            getSinaChangJing(pathMap);
            getSinaBank(pathMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void getSinaYaoWen(Map<String,String> pathMap) throws IOException
    {
        String YaoWenTextPath=pathMap.get("yaowen")+"//yaowen"+GetDate()+"outputText.txt";
        String YaoWenTitlePath=pathMap.get("yaowen")+"//yaowen"+GetDate()+"outputTitle.txt";
        String YaoWenUrlPath=pathMap.get("yaowen")+"//"+GetDate()+"url.txt";

        FileWriter urlWriter = new FileWriter(YaoWenUrlPath);
        FileWriter textWriter = new FileWriter(YaoWenTextPath);
        FileWriter titleWriter = new FileWriter(YaoWenTitlePath);

        String oldUrlPath=pathMap.get("yaowen")+"//"+GetYesterday()+"url.txt";
        String[] oldUrls=GetYesterdayInfo(oldUrlPath);

        Document doc = Jsoup.connect("http://finance.sina.com.cn/").timeout(5000).get();
        Elements ListDiv = doc.getElementsByAttributeValue("class","fin_tabs0_c0");
        //System.out.println(ListDiv);
        for (Element element :ListDiv) {
            Elements links = element.getElementsByTag("a");
            for (Element link : links) {
                String linkHref = link.attr("href").trim();
                String linkText = link.text().trim();
                if(judgeDup(oldUrls,linkHref))
                {
                    getWebText(linkHref,linkText,textWriter,titleWriter,urlWriter);
                }

            }
        }
        textWriter.close();
        titleWriter.close();
        urlWriter.close();
    }

    public static void getSinaChangJing(Map<String,String> pathMap) throws IOException
    {
        String ChanJingTextPath=pathMap.get("chanjing")+"//chanjing"+GetDate()+"outputText.txt";
        String ChanJingTitlePath=pathMap.get("chanjing")+"//chanjing"+GetDate()+"outputTitle.txt";
        String ChanJingUrlPath=pathMap.get("chanjing")+"//"+GetDate()+"url.txt";
        FileWriter urlWriter = new FileWriter(ChanJingUrlPath);
        FileWriter textWriter = new FileWriter(ChanJingTextPath);
        FileWriter titleWriter = new FileWriter(ChanJingTitlePath);

        String oldUrlPath=pathMap.get("chanjing")+"//"+GetYesterday()+"url.txt";
        String[] oldUrls=GetYesterdayInfo(oldUrlPath);

        Document doc = Jsoup.connect("http://finance.sina.com.cn/chanjing/").timeout(5000).get();
        Elements ListDiv = doc.getElementsByAttributeValue("class","blk_03");
        //System.out.println(ListDiv);
        for (Element element :ListDiv) {
            Elements links = element.getElementsByTag("a");
            for (Element link : links) {

                String linkHref = link.attr("href").trim();
                String linkText = link.text().trim();
                if(judgeDup(oldUrls,linkHref))
                {
                    getWebText(linkHref,linkText,textWriter,titleWriter,urlWriter);
                }
            }
        }
        textWriter.close();
        titleWriter.close();
        urlWriter.close();
    }
    public static void getSinaBank(Map<String,String> pathMap) throws IOException
    {

        String bankTextPath=pathMap.get("bank")+"//bank"+GetDate()+"outputText.txt";
        String bankTitlePath=pathMap.get("bank")+"//bank"+GetDate()+"outputTitle.txt";
        String bankUrlPath=pathMap.get("bank")+"//"+GetDate()+"url.txt";
        FileWriter urlWriter = new FileWriter(bankUrlPath);
        FileWriter textWriter = new FileWriter(bankTextPath);
        FileWriter titleWriter = new FileWriter(bankTitlePath);

        String oldUrlPath=pathMap.get("bank")+"//"+GetYesterday()+"url.txt";
        String[] oldUrls=GetYesterdayInfo(oldUrlPath);

        Document doc = Jsoup.connect("http://finance.sina.com.cn/money/bank/").timeout(5000).get();
        Elements ListDiv = doc.getElementsByAttributeValue("class","blk05");
        //System.out.println(ListDiv);

        for (Element element :ListDiv) {
            Elements links = element.getElementsByTag("a");
            for (Element link : links) {

                String linkHref = link.attr("href").trim();
                String linkText = link.text().trim();
                if(judgeDup(oldUrls,linkHref))
                {
                    getWebText(linkHref,linkText,textWriter,titleWriter,urlWriter);
                }
            }
        }
        textWriter.close();
        titleWriter.close();
        urlWriter.close();
    }

    public static void getWebText(String url,String subTitle,
                                  FileWriter textWriter,FileWriter titleWriter,
                                  FileWriter urlWriter) throws IOException
    {

        Document doc;
        doc = Jsoup.connect(url).timeout(10000).get();
        Elements ListDiv = doc.getElementsByAttributeValue("class","blkContainerSblkCon BSHARE_POP");
        if(ListDiv.isEmpty()!=true)
        {
            String webTitleKeywords=getTitleAndWebsite(url,subTitle)+getKeyWords(doc);
            System.out.println(webTitleKeywords);
            writeSTK(webTitleKeywords, titleWriter);
            textWriter.write(webTitleKeywords+"\n");
            urlWriter.write(url+"\n");
            for (Element element :ListDiv) {
                Elements links = element.getElementsByTag("p");
                for (Element link : links) {
                    String linkText = link.text().trim();
                    textWriter.write(linkText+"\n");
                    //  System.out.println(linkText);
                }
            }
        }
    }
    public static String getTitleAndWebsite(String url,String subTitle)
    {
        String titleAndWebsite;
        titleAndWebsite=url+"\t"+subTitle;
        return titleAndWebsite;
    }
    public static void writeSTK(String webTitleKeywords,FileWriter writeWebTitle)
    {
        try {
            writeWebTitle.write(webTitleKeywords+"\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static String getKeyWords(Document doc)
    {
        Elements listKey=doc.getElementsByAttributeValue("class","art_keywords");
        String keywords ="\t keywords:";
        for(Element element:listKey)
        {
            Elements links = element.getElementsByTag("a");
            for (Element link : links) {
                String linkText = link.text().trim();
                keywords = keywords+linkText+",";
            }
        }
        return keywords;

    }

    public static String GetDate()
    {
        Date dt=new Date();
        SimpleDateFormat simpleDate=new SimpleDateFormat("yyyy-MM-dd");
        // System.out.println(simpleDate.format(dt));
        return simpleDate.format(dt);
    }

    public static String GetYesterday()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String  yestedayDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        // System.out.println(yestedayDate);
        return yestedayDate;
    }
    public static String[] GetYesterdayInfo(String oldFilePath) throws IOException
    {
        String encoding="Utf-8";
        File file=new File(oldFilePath);
        if(file.exists())
        {
            return getOldUrls(file,encoding);
        }
        else
        {
            file.createNewFile();
            return getOldUrls(file,encoding);
        }

    }
    public static String[] getOldUrls(File file,String encoding) throws IOException
    {

        FileInputStream fis=new FileInputStream(file);
        InputStreamReader inStream=new InputStreamReader(fis,encoding);
        BufferedReader input=new BufferedReader(inStream);
        String url=input.readLine();
        StringBuilder sb = new StringBuilder("");
        while(url!=null){
            sb.append(url.trim());
            sb.append(",");
            url=input.readLine();
        }
        String sbStr = sb.toString();
        String oldUrls[]=sbStr.split(",");
        return oldUrls;

    }

    public static boolean judgeDup(String[] oldUrls ,String newUrl)
    {
        for(int i=0;i<oldUrls.length;i++)
        {
            if(newUrl.equals(oldUrls[i])==true)
            {
                return false;
            }
        }
        return true;
    }

    public static Map<String,String> createNewFiles()
    {
        String path=getWorkPath()+"//output";
        String [] fileNames = {"yaowen","chanjing","bank"};
        Map<String,String> pathMap=new HashMap<String,String>();
        String pathArray[] = new String[fileNames.length];
        for(int i=0;i<fileNames.length;i++)
        {
            String filePath=path+"//"+fileNames[i];
            File file=new File(filePath);
            if(!file.exists())
            {
                file.mkdirs();
            }
            pathArray[i]=file.getPath().replace("\\", "//");
            pathMap.put(fileNames[i], pathArray[i]);
        }
        return pathMap;
    }

    public static String getWorkPath()
    {
        String workspacePath = null;
        try {
            File directory = new File("");//参数为空
            workspacePath = directory.getCanonicalPath() ;
            //System.out.println(workspacePath);
            workspacePath = workspacePath.replace("\\", "//");
            //System.out.println(workspacePath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return workspacePath;
    }
}
