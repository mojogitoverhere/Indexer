

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;


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
		try {
			indexer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	Document getDocument(File f) throws IOException
	{
		String title = "";
		String body = "";
		String url = "";
		
		//Parse the html file
		org.jsoup.nodes.Document jdoc = Jsoup.parse(f, "UTF-8", "");
		
		//Extract title
		Elements metaOgTitle = jdoc.select("meta[property=og:title]");
		if(metaOgTitle.hasAttr("content"))
		{
			title = metaOgTitle.attr("content");
		}
		
		//Extract url
		Elements urlElements = jdoc.getElementsByTag("url");
		url = urlElements.first().text(); //there is only one url tag
		
		//Extract body content
		// --Remove all script tags
		Elements scriptElements = jdoc.select("script");
		for(Element script : scriptElements)
			script.remove();
		
		// --Remove all style tags
		Elements styleElements = jdoc.select("style");
		for(Element style : styleElements)
			style.remove();
		body = jdoc.text();
		
		//Construct the Lucene Document
		Document doc = new Document();
		doc.add(new TextField("title", title, Field.Store.YES));
		doc.add(new TextField("body", body, Field.Store.YES));
		doc.add(new StringField("url", url, Field.Store.YES));
		doc.add(new StringField("fullpath", f.getCanonicalPath(), Field.Store.YES)); //throws IOException
		return doc;
	}
	
	void close() throws IOException
	{
		writer.close();
	}
}
