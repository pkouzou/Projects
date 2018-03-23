package utils;

import java.util.ArrayList;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

public class SearchReturn {
	private ScoreDoc hits[];
	private IndexSearcher searcher;
	
	public SearchReturn(ScoreDoc hits[], IndexSearcher searcher){
		this.hits = hits;
		this.searcher = searcher;
	}
	
	public ScoreDoc[] getHits(){
		return(hits);
	}
	
	public IndexSearcher getSearcher(){
		return(searcher);
	}
}