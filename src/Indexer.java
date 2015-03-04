

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.util.Version;


public class Indexer {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String indexDir = "C:/Users/ASUS/Desktop/lucene-test";
		String dataDir = "C:/Users/ASUS/Desktop/pages";
		File indexDirFile = new File(indexDir);
		File dataDirFile = new File(dataDir);
		
		// Verify that both directories are valid
		if(!indexDirFile.isDirectory())
		{
			System.out.println("Specified index location is not a valid directory");
			return;
		}
		if(!dataDirFile.isDirectory())
		{
			System.out.println("Specified data location is not a valid directory");
			return;
		}
		
		// Create an index at indexDir using files from dataDir
		Indexer indexer = new Indexer(indexDir);
		indexer.addFilesFromDirectory(dataDir);
		
		System.out.println("Done!");
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
			File indexDirFile = new File(indexDir);
			Directory dir = FSDirectory.open(Paths.get(indexDirFile.toURI()));
			
			//Token analyzer for processing Documents
			StandardAnalyzer analyzer = new StandardAnalyzer();
			analyzer.setVersion(luceneVersion);
			
			//Configuration settings for the IndexWriter
			IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
			
			return new IndexWriter(dir, writerConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Return null if the IndexWriter was not set up properly
		return null;
	}
	
	public void addFilesFromDirectory(String dataDir)
	{
		//Get all Files in the dataDir directory
		File dataDirFile = new File(dataDir);
		File[] dataFiles = dataDirFile.listFiles();
		
		//Add each file to the index
		for(File file : dataFiles)
			indexFile(file);
	}
	
	void indexFile(File file)
	{
		try {
			Document doc = getDocument(file);
			writer.addDocument(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	Document getDocument(File f) throws FileNotFoundException
	{
		//Construct the Document
		Document doc = new Document();
		doc.add(new TextField("contents", new FileReader(f)));
		//TODO: ADD MORE FIELDS
		return doc;
	}
	
	
}
