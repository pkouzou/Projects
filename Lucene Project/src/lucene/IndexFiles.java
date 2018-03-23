package lucene;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexFiles {	
  private static final File folder = new File("");
  private static PrintWriter historyWriter;
  private static String historyPath = "fill history path";  
  private IndexFiles() {}
  
  public static void main(String[] args) {
    String usage = "java org.apache.lucene.demo.IndexFiles"
                 + " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
                 + "This indexes the documents in DOCS_PATH, creating a Lucene index"
                 + "in INDEX_PATH that can be searched with SearchFiles";
    deletePreviousIndex(folder);
    String indexPath = "fill index path";
    String docsPath = "fill focs path";

    final Path docDir = Paths.get(docsPath);
    if (!Files.isReadable(docDir)) {
      System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
      System.exit(1);
    }
    
    Date start = new Date();
    try {
      System.out.println("Indexing to directory '" + indexPath + "'...");

      Directory dir = FSDirectory.open(Paths.get(indexPath));
      Analyzer analyzer = new StandardAnalyzer();
      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
      iwc.setOpenMode(OpenMode.CREATE);
      IndexWriter writer = new IndexWriter(dir, iwc);
      indexDocs(writer, docDir);
      writer.close();
    
      Date end = new Date();
      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

    } catch (IOException e) {
      System.out.println(" caught a " + e.getClass() +
       "\n with message: " + e.getMessage());
    }
  }

  static void indexDocs(final IndexWriter writer, Path path) throws IOException {
    if (Files.isDirectory(path)) {
      Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          try {
            indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
          } catch (IOException ignore) {
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } else {
      indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
    }
  }

  static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
    try (InputStream stream = Files.newInputStream(file)) {
      String tweet = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).readLine();
      if (tweet == null){
    	  return;
      }
      String splittedTweet[] = tweet.split("\t");
      if (splittedTweet.length < 6){
    	  return;
      }
      String userName = splittedTweet[0];
      String userLocation = splittedTweet[1];
      String hashtagsAndHandles[] = splittedTweet[2].split(" ");
      String text = splittedTweet[3];
      String tweetID = splittedTweet[4].trim();
      String tweetText = splittedTweet[5].trim();
      
      Document doc = new Document();
      
      doc.add(new StringField("user", userName.trim(), Field.Store.YES));
      doc.add(new StringField("tweetID", tweetID.trim(), Field.Store.YES));
      doc.add(new StringField("tweetText", tweetText.trim(), Field.Store.YES));
      doc.add(new StringField("occurences", "0", Field.Store.YES));
      
      try{
    	    historyWriter = new PrintWriter(historyPath + doc.get("tweetID") + ".txt");
    	    historyWriter.println("occurences/times clicked");
    	    historyWriter.println("0 0");
    	    historyWriter.close();
    	} catch (IOException e) {}
      
      doc.add(new LongPoint("modified", lastModified));
      doc.add(new TextField("username", getBufferedReader(userName))); 
      doc.add(new TextField("location", getBufferedReader(userLocation)));
      doc.add(new StringField("location", userLocation.trim(), Field.Store.YES));
      doc.add(new StringField("hh", splittedTweet[2].trim(), Field.Store.YES));
      
      for (String hh : hashtagsAndHandles){
    	  doc.add(new TextField("hashtags", getBufferedReader(hh)));
      }
      doc.add(new TextField("keyword", getBufferedReader(text))); 
      
      System.out.println("adding " + file);
      writer.addDocument(doc);

    }
  }
  
  static BufferedReader getBufferedReader(String str){
	  InputStream is = new ByteArrayInputStream(str.getBytes());
	  BufferedReader br = new BufferedReader(new InputStreamReader(is));
	  return br;
  }
  
  public static void deletePreviousIndex(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	    	fileEntry.delete();
	    }
	}
  
}