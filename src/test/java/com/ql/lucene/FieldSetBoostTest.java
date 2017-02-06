package com.ql.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * @author zjc
 * @version 2016/7/27 23:28
 * @description 文档域加权
 */
public class FieldSetBoostTest {

	private Directory dir; //存放索引的位置

	//准备一下数据，四个人写了四篇文章，Json是boss
	private String ids[] = {"1", "2", "3", "4"};
	private String authors[] = {"Jack", "Marry", "John", "Json"};
	private String positions[] = {"accounting", "technician", "salesperson", "boss"};
	private String titles[] = {"Java is a good language.", "Java is a cross platform language", "Java powerful", "You should learn java"};
	private String contents[] = {
			"If possible, use the same JRE major version at both index and search time.",
			"When upgrading to a different JRE major version, consider re-indexing. ",
			"Different JRE major versions may implement different versions of Unicode.",
			"For example: with Java 1.4, `LetterTokenizer` will split around the character U+02C6."
	};


	//建立索引
	@Test
	public void index() throws Exception {
		IndexWriter writer = getWriter();
		for (int i = 0; i < ids.length; i++) {
			Document doc = new Document();
			doc.add(new StringField("id", ids[i], Field.Store.YES));
			doc.add(new StringField("author", authors[i], Field.Store.YES));
			doc.add(new StringField("position", positions[i], Field.Store.YES));

			//这部分就是加权操作了，对title这个Field进行加权，因为等会我要查这个Field
			TextField field = new TextField("title", titles[i], Field.Store.YES);

			//先判断之个人对应的职位是不是boss，如果是就加权(其实相当于整个文档都被加权了,查询任何一个字段都优先排序)
			if ("boss".equals(positions[i])) {
				field.setBoost(1.5f);//加权操作，默认为1，1.5表示加权了，小于1就降权了
			}
			doc.add(field);
			doc.add(new TextField("content", contents[i], Field.Store.NO));
			writer.addDocument(doc);
		}
		writer.close();//close了才真正写到文档中
	}


	//获取IndexWriter实例
	private IndexWriter getWriter() throws Exception {
		dir = FSDirectory.open(Paths.get("F:\\lucene3"));
		Analyzer analyzer = new StandardAnalyzer(); //标准分词器，会自动去掉空格啊，is a the等单词
		IndexWriterConfig config = new IndexWriterConfig(analyzer); //将标准分词器配到写索引的配置中
		IndexWriter writer = new IndexWriter(dir, config); //实例化写索引对象
		return writer;
	}

	//文档域加权测试
	@Test
	public void search() throws Exception {
		dir = FSDirectory.open(Paths.get("F:\\lucene3"));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		String searchField = "title"; //要查询的Field
		String q = "java"; //要查询的字符串
		Term term = new Term(searchField, q);

		Query query = new TermQuery(term);
		TopDocs hits = searcher.search(query, 10);
		System.out.println("匹配" + q + "总共查询到" + hits.totalHits + "个文档");
		for (ScoreDoc score : hits.scoreDocs) {
			Document doc = searcher.doc(score.doc);
			System.out.println(doc.get("author"));//打印一下查出来记录对应的作者
		}
		reader.close();

	}
}
