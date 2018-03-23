package lucene;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import utils.CustomQuery;
import utils.HitComparator;
import utils.SearchReturn;

public class SearchFiles {

  private IndexReader reader;
  private IndexSearcher searcher;
  private Analyzer analyzer;
  private static String historyPath = "fill history path";  
  private String index = "fill index path";
  private String queries = null;
  private boolean raw = false;
  private String queryString = null;
  private int hitsPerPage = 10;
  private BufferedReader in = null;
  private static HashMap<String, Integer> usernames = new HashMap<String, Integer>();
  private static HashMap<String, Integer> locations = new HashMap<String, Integer>();
  private static HashMap<String, Integer> hashtags = new HashMap<String, Integer>();
  
  public SearchFiles() {
	  try {
		init();
	} catch (IOException | ParseException e) {
		e.printStackTrace();
	}
  }

  public void init() throws IOException, ParseException {
    reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
    searcher = new IndexSearcher(reader);
    analyzer = new StandardAnalyzer();

    if (queries != null) {
      in = Files.newBufferedReader(Paths.get(queries), StandardCharsets.UTF_8);
    } else {
      in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    }
  }
  
  public SearchReturn search(ArrayList<CustomQuery> customQueries) throws ParseException, IOException{
	  BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
	  QueryParser parser = new QueryParser("null", analyzer);;
	  for(CustomQuery customQuery : customQueries){
		  if(!customQuery.getField().equals(parser.getField())){
			  parser = new QueryParser(customQuery.getField(), analyzer);
		  }
		  finalQuery.add(parser.parse(customQuery.getSearchTerm()), Occur.MUST);
	  }
	 
	   BooleanQuery query = finalQuery.build();	   
	   return(new SearchReturn(doPagingSearch(in, searcher, query, hitsPerPage, raw, queries == null && queryString == null), searcher));
}

  public static ScoreDoc[] doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, 
                                     int hitsPerPage, boolean raw, boolean interactive) throws IOException {
 
    TopDocs results = searcher.search(query, 5 * hitsPerPage);
    ScoreDoc[] hits = results.scoreDocs;
    
    int numTotalHits = results.totalHits;
    System.out.println(numTotalHits + " total matching documents");
    int occurences = 0, timesClicked = 0;
    double ratio;
    BufferedReader br;
    Arrays.sort(hits, HitComparator.ScoreComparator);
    for (int i = 0; i < hits.length; i++){
    	Document doc = searcher.doc(hits[i].doc);
    	try {
			br = new BufferedReader(new FileReader(new File(historyPath + doc.get("tweetID") + ".txt")));
			br.readLine();
			String splitted[] = br.readLine().split(" ");
			if (splitted.length == 2){
				occurences = Integer.parseInt(splitted[0]);
				timesClicked = Integer.parseInt(splitted[1]);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	ratio = (double)timesClicked/occurences;
    	hits[i].score += hits[i].score *ratio;
    	
    	
    	if (i > 9) continue;
    	String key = doc.get("user");
    	if (usernames.containsKey(key)) usernames.put(key, usernames.get(key) +1);
    	else usernames.put(key, 1);
    	key = doc.get("location");
    	if (locations.containsKey(key)) locations.put(key, locations.get(key) +1);
    	else locations.put(key, 1);
    	for (String hashtag : doc.get("hh").split(" ")){
    		key = hashtag;
    		if (hashtags.containsKey(key)) hashtags.put(key, hashtags.get(key) +1);
        	else hashtags.put(key, 1);
    	}
    	    	
    }
    
    updateRecommendations(usernames, "usernames");
    updateRecommendations(locations, "locations");
    updateRecommendations(hashtags, "hashtags");
  
    return(hits);
  }
  
  public static void updateRecommendations(HashMap<String, Integer> map, String fileName){
	  BufferedReader br;
	  PrintWriter writer;
	  File file;
	  String field = "", counter = "", line;
	  String path = "C:\\Users\\Chryssa\\Documents\\cse\\8osemester\\anakthshPlhroforias\\project\\splitTweetWithTabs\\recommendations\\";
	  try {
		file = new File(path + fileName + ".txt");
		br = new BufferedReader(new FileReader(file));
		writer = new PrintWriter(path + fileName + "2.txt");
		line = br.readLine();
		while (line != null){
			field = line.split("~~~")[0];
			counter = line.split("~~~")[1];
			if (map.containsKey(field)){
				writer.println(field + "~~~" + (Integer.parseInt(counter) + map.get(field)));
				map.remove(field);
			}
			else{
				writer.println(field + "~~~" + counter);
			}
			line = br.readLine();
		}
		
		for(String key : map.keySet()){
			writer.println(key + "~~~" + map.get(key));
		}
		
		br.close();
		file.delete();
		writer.close();
		(new File(path + fileName + "2.txt")).renameTo(new File(path + fileName + ".txt"));
		
	} catch (IOException e) {
		e.printStackTrace();
	}
	  
	  map.clear();
  }

}
