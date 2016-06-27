package test;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SimpleDBSearcher {
	private static final String LUCENE_QUERY = "Name:China AND Continent:Asia";
	private static final int MAX_HITS = 100;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		File indexDir = new File(SimpleDBIndexer.INDEX_DIR);
		String query = LUCENE_QUERY;
		SimpleDBSearcher searcher = new SimpleDBSearcher();
		searcher.searchIndex(indexDir, query);
		
	}
	
	private void searchIndex(File indexDir, String queryStr) throws Exception {
		Analyzer analyzer = new StandardAnalyzer();
		Directory directory = FSDirectory.open(indexDir.toPath());
		MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[] {"Name","Continent"}, analyzer);
		DirectoryReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		queryParser.setPhraseSlop(0);
		queryParser.setLowercaseExpandedTerms(true);
		Query query = queryParser.parse(queryStr);
		
		TopDocs topDocs = searcher.search(query, MAX_HITS);
		
		ScoreDoc[] hits = topDocs.scoreDocs;
		System.out.println(hits.length + " Record(s) Found");
		for (int i = 0; i<hits.length; i++) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println("\"Country Name:\" " + d.get("Name") + ",\"Code:\" " + d.get("Code"));
		}
		if (hits.length==0) {
			System.out.println("No Data Founds");
		}
	}

}
