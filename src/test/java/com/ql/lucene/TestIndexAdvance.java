package com.ql.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * @author zjc
 * @version 2016/7/27 22:53
 * @description 测试Lucene建立索引、添加文档、文档加权、修改文档和删除文档
 */
public class TestIndexAdvance {

	private Directory dir;  //索引存放的位置

	//测试数据
	private String[] ids = {"1", "2", "3"};
	private String[] citys = {"shanghai", "guangzhou", "qingdao"};
	private String[] descs = {
			"Shanghai is a bustling city.",
			"Nanjing is a city of culture.",
			"Qingdao is a beautiful city"
	};


	//生成索引
	@Test
	public void index() throws Exception {
		IndexWriter writer = getWriter();//获取写索引的实例
		for (int i = 0; i < ids.length; i++) {
			Document doc = new Document();
			doc.add(new StringField("id", ids[i], Field.Store.YES));
			doc.add(new StringField("city", citys[i], Field.Store.YES));
			doc.add(new StringField("descs", descs[i], Field.Store.YES));
			writer.addDocument(doc); //添加文档
		}
		writer.close();//close了才真正写到文档中
	}

	//获取IndexWriter实例
	private IndexWriter getWriter() throws Exception {
		dir = FSDirectory.open(Paths.get("F:\\lucene2"));
		Analyzer analyzer = new StandardAnalyzer();//标准分词器，会自动去掉空格啊，is a the等单词
		IndexWriterConfig config = new IndexWriterConfig(analyzer); //将标准分词器配到写索引的配置中
		return new IndexWriter(dir, config);
	}

	/***********  下面来测试了  ****************/
	//测试写入了几个文档
	@Test
	public void testIndexWriter() throws Exception {
		IndexWriter writer = getWriter();
		System.out.println("总共写入了" + writer.numDocs() + "个文档");
		writer.close();
	}


	/***********  下面来测试了  ****************/
	//测试读取了几个文档
	@Test
	public void testIndexReader() throws Exception {
		dir = FSDirectory.open(Paths.get("F:\\lucene2"));
		IndexReader reader = DirectoryReader.open(dir);
		System.out.println("最大文档数：" + reader.maxDoc());
		System.out.println("实际文档数：" + reader.numDocs());
		reader.close();
	}

//	删除文档有两种方式，这两种方式各有特点。
//	一种是在合并前删除，另一种是在合并后删除。
//	合并前删除指的是并没有真正删除这个文档，只是在这个文档上做一个标记而已（reader.maxDoc()可以获取到这类被删除的文档数量）；
//	而合并后删除指的是真正删掉了这个文档了

	//测试删除文档，在合并前(其实并没有合并操作)
	@Test
	public void testDeleteBeforeMerge() throws Exception {
		IndexWriter writer = getWriter();
		System.out.println("删除前有" + writer.numDocs() + "个文档");
		writer.deleteDocuments(new Term("id", "1")); //删除'id' = '1'对应的文档
		writer.commit(); //提交删除,并没有真正删除
		System.out.println("删除后最大文档数：" + writer.maxDoc());
		System.out.println("删除后实际文档数：" + writer.numDocs());
		writer.close();
	}

	//测试删除文档，在合并之后
	@Test
	public void testDeleteAfterMerge() throws Exception {
		IndexWriter writer = getWriter();
		System.out.println("删除前有" + writer.numDocs() + "个文档");
		writer.deleteDocuments(new Term("id", "1")); //删除'id' = '1'对应的文档
		writer.forceMergeDeletes(); //强制合并（强制删除），没有索引了
		writer.commit(); //提交删除,真正删除
		System.out.println("删除后最大文档数：" + writer.maxDoc());
		System.out.println("删除后实际文档数：" + writer.numDocs());
		writer.close();
	}

	//测试更新
	@Test
	public void testUpdate() throws Exception {
		IndexWriter writer = getWriter();
		//新建一个Document
		Document doc = new Document();
		doc.add(new StringField("id", ids[1], Field.Store.YES));
		doc.add(new StringField("city", "shanghai22", Field.Store.YES));
		doc.add(new TextField("descs", "shanghai update", Field.Store.YES));

		//将原来id为1对应的文档，用新建的文档替换
		writer.updateDocument(new Term("id", "1"), doc);
		writer.close();
		System.out.println(doc.getField("descs"));
	}
}
