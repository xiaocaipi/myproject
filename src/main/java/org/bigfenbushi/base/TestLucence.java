package org.bigfenbushi.base;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;




public class TestLucence {

	private static String indexPath = "G:\\project\\lucence";
	private static Analyzer analyzer= new IKAnalyzer();
	
	public static void createIndex() throws Exception{
		File dir= new File(indexPath);
		Directory directory=FSDirectory.open(dir);
		IndexWriterConfig indexWriterConfig= new IndexWriterConfig(Version.LUCENE_CURRENT,analyzer );
		indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		Document doc= new Document();
		doc.add(new Field("name","我们都是好孩子",Field.Store.YES,Field.Index.ANALYZED));
		IndexWriter indexWriter=null;
		indexWriter=new IndexWriter(directory,indexWriterConfig);
		indexWriter.addDocument(doc);
		indexWriter.close();
	}
	
	public static void searchIndex() throws Exception{
		String q="我们";
		File dir= new File(indexPath);
		IndexReader reader=null;
		IndexSearcher searcher =null;
		Directory directory=FSDirectory.open(dir);
		reader= IndexReader.open(directory);
		searcher= new IndexSearcher(reader);
		WildcardQuery termquery=new WildcardQuery(new Term("name","*" + q + "*"));
		TopDocs hits= searcher.search(termquery,1);
		ScoreDoc[] hitss=hits.scoreDocs;
		Document doc=searcher.doc(hitss[0].doc);
		String name=doc.get("name");
		SimpleHTMLFormatter formatter=new SimpleHTMLFormatter("<font color='#CC0000'>","</font>");
		Highlighter highlighter=new Highlighter(formatter,new QueryScorer(termquery));
		highlighter.setTextFragmenter(new SimpleFragmenter(40));
		TokenStream tokenStream=analyzer.tokenStream("content", new StringReader(name));
		//highLightText=highlighter.getBestFragment(analyzer,"content", content);
		String highLightText=highlighter.getBestFragments(tokenStream,name, 3, "...");
		System.out.println(highLightText);
	}
	
	public static void testAyalyzer() throws Exception{
		String encontent = "i am a chinese man";
		String zhcontent = "我们都是还好子";
		
		Analyzer standardAnalyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		Analyzer cjkAnalyzer = new CJKAnalyzer(Version.LUCENE_CURRENT);
		Analyzer ikAnalyzer = new IKAnalyzer();
		analyze(standardAnalyzer, zhcontent);
		analyze(cjkAnalyzer, zhcontent);
		analyze(ikAnalyzer, zhcontent);
		
	}
	
	public static void analyze(Analyzer analyzer,String text) throws Exception{
		System.out.println("\n分词器 ；"+analyzer.getClass());
		TokenStream stream = null;  
	    try {  
	        stream = analyzer.tokenStream("content", new StringReader(text));  
	        CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);  
	        stream.reset();  
	        while(stream.incrementToken()){  
	            System.out.println(attr.toString());
	        }  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }finally{  
	        if(stream != null){  
	            try {  
	                stream.close();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	        }  
	    }  
		
	}
	
	
	public static void main(String[] args) throws Exception {
//		createIndex();
//		searchIndex();
		testAyalyzer();
		
	}
	
}
