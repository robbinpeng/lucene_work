package test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SimpleDBIndexer {
	public static final String INDEX_DIR = "G:/tmp/";
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/world";
	private static final String USER_NAME = "root";
	private static final String PASSWORD = "1234";
	
	private static final String QUERY = "SELECT Code, Name, Continent, Region, Capital from country";
	
	public static void main(String[] args) throws Exception {
		File indexDir = new File(INDEX_DIR);
		SimpleDBIndexer indexer = new SimpleDBIndexer();
		try{
			Class.forName(JDBC_DRIVER).newInstance();
			Connection conn = DriverManager.getConnection(CONNECTION_URL, USER_NAME, PASSWORD);
			SimpleAnalyzer analyzer = new SimpleAnalyzer();
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			IndexWriter indexWriter = new IndexWriter(FSDirectory.open(indexDir.toPath()),indexWriterConfig);
			System.out.println("Indexing to direcotry '" + indexDir + "'....");
			int indexedDocumentCount = indexer.indexDocs(indexWriter, conn);
			indexWriter.close();
			System.out.println(indexedDocumentCount + " records have been indexed successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int indexDocs(IndexWriter writer, Connection conn) throws Exception {
		String sql = QUERY;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		int i = 0;
		while(rs.next()){
			Document d = new Document();
			d.add(new TextField("Code", rs.getString("Code"), Field.Store.YES));
			d.add(new TextField("Name", rs.getString("Name"), Field.Store.YES));
			d.add(new TextField("Continent", rs.getString("Continent"), Field.Store.YES));
			//d.add(new TextField("Region", rs.getString("Region"), Field.Store.YES));
			//d.add(new TextField("Capital", rs.getString("Capital"), Field.Store.YES));
			
			writer.addDocument(d);
			i++;
		}
		return i;
	}
}
