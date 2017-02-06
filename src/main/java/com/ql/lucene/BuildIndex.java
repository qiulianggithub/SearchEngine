package com.ql.lucene;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

/**
 * @author zjc
 * @version 2016/7/27 0:17
 * @description 建立索引
 */
public class BuildIndex {

	private IndexWriter writer; //写索引的实例

	//构造方法,实例化IndexWriter


	public BuildIndex(String indexDir) throws Exception {
		Directory dir = FSDirectory.open(Paths.get(indexDir));
		Analyzer analyzer = new StandardAnalyzer(); // 标准分词器，会自动去掉空格啊，is a the等单词
		IndexWriterConfig config = new IndexWriterConfig(analyzer); //将标准分词器配到写索引的配置中W
		writer = new IndexWriter(dir, config); //实例化写索引的对象
	}

	//关闭写索引
	public void close() throws Exception {
		writer.close();
	}


	//索引指定目录下的所有文件
	public int indexAll(String dataDir) throws Exception {
		File[] files = new File(dataDir).listFiles();//获取该路径下的所有文件
		if (files == null) return 0;

		for (File f : files) {
			indexFile(f); //调用下面的indexFile方法，对每个文件进行索引
		}
		return writer.numDocs(); //返回索引的文件数
	}


	//索引指定的文件
	private void indexFile(File file) throws Exception {
		System.out.println("索引文件的路径:" + file.getCanonicalPath());
		Document doc = getDocument(file);
		writer.addDocument(doc);
	}

	//获取文档，文档里再设置每个字段，就类似于数据库中的一行记录
	private Document getDocument(File file) throws Exception {
		Document doc = new Document();

		//添加字段
		doc.add(new TextField("contents", new FileReader(file)));//添加文件内容
		doc.add(new TextField("fileName", file.getName(), Field.Store.YES));//添加文件名，并把这个字段存到索引文件里
		doc.add(new TextField("fullPath", file.getCanonicalPath(), Field.Store.YES));//添加文件路径
		return doc;
	}

	public static void main(String[] args) {
		String indexDir = "F:\\lucene";//将索引保存到的路径
		String dataDir = "F:\\lucene\\data";//需要索引的文件数据存放的目录
		BuildIndex index = null;
		int indexedNum = 0;
		long startTime = System.currentTimeMillis();//记录索引开始时间
		try {
			index = new BuildIndex(indexDir);
			indexedNum = index.indexAll(dataDir);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (index != null) {
					index.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis(); //记录索引结束时间
		System.out.println("索引耗时" + (endTime - startTime) + "毫秒");
		System.out.println("共索引了" + indexedNum + "个文件");
	}
}
