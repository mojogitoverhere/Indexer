

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.util.Version;


public class Indexer {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String indexDir = "C:/Users/ASUS/Desktop/LUCENE";
		File indexDirHandle = new File(indexDir);
		if(!indexDirHandle.isDirectory())
		{
			System.out.println("is not a valid directory");
			return;
		}
		Indexer indexer = new Indexer(indexDir);
	}
	
	private IndexWriter writer;
	private static final Version luceneVersion = Version.LUCENE_5_0_0;
	
	public Indexer(String indexDir)
	{
		writer = getIndexWriter(indexDir);
		
	}
	
	IndexWriter getIndexWriter(String indexDir)
	{
		try {
			//Directory used to store index
			File dirFileHandle = new File(indexDir);
			Directory dir = FSDirectory.open(Paths.get(dirFileHandle.toURI()));
			
			//Token analyzer for processing Documents
			StandardAnalyzer analyzer = new StandardAnalyzer();
			analyzer.setVersion(luceneVersion);
			
			//Configuration settings for the IndexWriter
			IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
			
			return new IndexWriter(dir, writerConfig);
		} catch (IOException e) {
			System.out.println("Index directory path is invalid.");
			//System.out.println(e.getMessage());
		}
		//Return null if the IndexWriter was not set up properly
		return null;
	}
}
