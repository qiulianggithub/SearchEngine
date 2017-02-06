package com.ql.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * @author zjc
 * @version 2016/7/27 23:50
 * @description 测试文档搜索
 */
public class SearchTest {

	private Directory dir;
	private IndexReader reader;
	private IndexSearcher search;


	@Before
	public void setUp() throws Exception {

		dir = FSDirectory.open(Paths.get("F:\\Lucene")); //F:\Lucene
		reader = DirectoryReader.open(dir); //根据目录获取IndexReader
		search = new IndexSearcher(reader); //根据IndexReader获取IndexSearcher
	}

	@After
	public void tearDown() throws Exception {
		reader.close(); //关闭InderxReader
	}

	//对特定项进行搜索
	@Test
	public void testTermQuery() throws Exception {
		String searchField = "contents";
		String q = "particular";
		Term term = new Term(searchField, q);
		Query query = new TermQuery(term);
		TopDocs hits = search.search(query, 10);
		System.out.println("匹配" + q + "总共查询到" + hits.totalHits + "个文档");
		for (ScoreDoc score : hits.scoreDocs) {
			Document doc = search.doc(score.doc);
			System.out.println(doc.get("fullPath"));
		}
	}

	//支持查询表达式
	@Test
	public void testQueryParser() throws Exception {
		Analyzer analyzer = new StandardAnalyzer(); //标准分词器，会自动去掉空格啊，is a the等单词
		String searchField = "contents";
		String q = "particular";    //OR AND particular~
		QueryParser parser = new QueryParser(searchField, analyzer); //查询解析器
		Query query = parser.parse(q); //通过解析要查询的String，获取查询对象
		TopDocs docs = search.search(query, 10);//开始查询，查询前10条数据，将记录保存在docs中
		System.out.println("匹配" + q + "总共查询到" + docs.totalHits + "个文档");
		for (ScoreDoc scoreDoc : docs.scoreDocs) { //取出每条查询结果
			Document doc = search.doc(scoreDoc.doc); //scoreDoc.doc相当于docID,根据这个docID来获取文档
			System.out.println(doc.get("fullPath")); //fullPath是刚刚建立索引的时候我们定义的一个字段
		}
	}

	//查询两个id之间的所有项
	@Test
	public void testNumericRangeQuery() throws Exception {
		NumericRangeQuery<Integer> query = NumericRangeQuery.newIntRange("id", 1, 2, true, true);
		TopDocs hits = search.search(query, 10);
		System.out.println("总共查询到" + hits.totalHits + "个文档");
		for (ScoreDoc score : hits.scoreDocs) {
			Document doc = search.doc(score.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
		}
	}

	//将要搜索的字段和开头的字符串传进去，然后再搜索
	@Test
	public void testPrefixQuery() throws Exception {
		PrefixQuery query = new PrefixQuery(new Term("city", "s"));
		TopDocs hits = search.search(query, 10);
		System.out.println("总共查询到" + hits.totalHits + "个文档");
		for (ScoreDoc score : hits.scoreDocs) {
			Document doc = search.doc(score.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
		}
	}

	//组合查询（功能比较强大，一般选择这种）
	//id为1到2之间，然后city又是s开头的
	@Test
	public void testBooleanQuery() throws Exception {
		NumericRangeQuery<Integer> query1 = NumericRangeQuery.newIntRange("id", 1, 2, true, true);
		PrefixQuery query2 = new PrefixQuery(new Term("city", "s"));
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

		booleanQuery.add(query1, BooleanClause.Occur.MUST);
		booleanQuery.add(query2, BooleanClause.Occur.MUST);

		TopDocs hits = search.search(booleanQuery.build(), 10);

		System.out.println("总共查询到" + hits.totalHits + "个文档");
		for (ScoreDoc score : hits.scoreDocs) {
			Document doc = search.doc(score.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
		}
	}
}
