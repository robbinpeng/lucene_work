package test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

public class TestMain {

	public static void main(String[] args) {
		Analyzer analyzer = new StandardAnalyzer();
		
		//store the index in memory:
		//Directory directory = new RAMDirectory();
		//To store an index on disk, use this instead:
		Path path;
		try {
			//path = Files.createTempDirectory(null, "/tmp/testindex");
			Directory directory = FSDirectory.open(new File("/tmp/testindex").toPath());
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter iwriter = new IndexWriter(directory, config);
			Document doc = new Document();
			String text = "This is the text to be indexed.";
			doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
			iwriter .addDocument(doc);
			iwriter.close();
			DirectoryReader ireader = DirectoryReader.open(directory);
			IndexSearcher iSearcher = new IndexSearcher(ireader);
			//parse a simple query that searches for "text":
			QueryParser parser = new QueryParser("fieldname", analyzer);
			Query query = parser.parse("text");
			ScoreDoc[] hits = iSearcher.search(query,1000).scoreDocs;
			System.out.println(hits.length + "records found");
			//Iterate through the results:
			for (int i = 0; i<hits.length; i++) {
				Document hitDoc = iSearcher.doc(hits[i].doc);
				System.out.println(hitDoc.get("fieldname"));
			}
			
			ireader.close();
		
			directory.close();
		}  catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
