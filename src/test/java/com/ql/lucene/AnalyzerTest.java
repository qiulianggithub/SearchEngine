//package com.ql.lucene;
//
//import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
//import org.apache.lucene.document.*;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.index.IndexWriterConfig;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.FSDirectory;
//import org.junit.Test;
//
//import java.nio.file.Paths;
//
///**
// * @author zjc
// * @version 2016/7/28 0:21
// * @description 添加中文分词器测试
// */
//public class AnalyzerTest {
//
//	private Directory dir; //存放索引的位置
//
//	//准备一下用来测试的数据
//	private Integer ids[] = {1, 2, 3}; //用来标识文档
//	private String citys[] = {"上海", "南京", "青岛"};
//	private String descs[] = {
//			"上海是个繁华的城市。",
//			"南京是一个有文化的城市。",
//			"青岛是一个美丽的城市。"
//	};
//
//	//生成索引
//	@Test
//	public void index(String indexDir) throws Exception {
//		dir = FSDirectory.open(Paths.get(indexDir));
//		IndexWriter writer = getWriter();
//		for (int i = 0; i < ids.length; i++) {
//			Document doc = new Document();
//			doc.add(new IntField("id", ids[i], Field.Store.YES));
//			doc.add(new StringField("city", citys[i], Field.Store.YES));
//			doc.add(new TextField("desc", descs[i], Field.Store.YES));
//			writer.addDocument(doc); //添加文档
//		}
//		writer.close(); //close了才真正写到文档中
//	}
//
//	//获取IndexWriter实例
//	private IndexWriter getWriter() throws Exception {
//		SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();//使用中文分词器
//		IndexWriterConfig config = new IndexWriterConfig(analyzer); //将标准分词器配到写索引的配置中
//		IndexWriter writer = new IndexWriter(dir, config); //实例化写索引对象
//		return writer;
//	}
//
//	public static void main(String[] args) throws Exception {
//		new AnalyzerTest().index("F:\\lucene4");
//	}
//
//}
