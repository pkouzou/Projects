package utils;

import java.io.IOException;
import java.util.Comparator;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

import gui.ResultWindow;

public class HitComparator {
	
	public static Comparator<ScoreDoc> TweetIDComparator = new Comparator<ScoreDoc>() {

		@Override
		public int compare(ScoreDoc hit1, ScoreDoc hit2) {
			return tweetIDCompare(hit1, hit2);
		}
	};
	
	public static Comparator<ScoreDoc> ScoreComparator = new Comparator<ScoreDoc>() {

		@Override
		public int compare(ScoreDoc hit1, ScoreDoc hit2) {
			return scoreCompare(hit1, hit2);
		}
	};
	
	public static int tweetIDCompare(ScoreDoc hit1, ScoreDoc hit2) {
		Document doc1 = null, doc2 = null;
		try {
			doc1 = ResultWindow.getSearcher().doc(hit1.doc);
			doc2 = ResultWindow.getSearcher().doc(hit2.doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc2.get("tweetID").compareToIgnoreCase(doc1.get("tweetID"));
		
	}
	
	public static int scoreCompare(ScoreDoc hit1, ScoreDoc hit2) {
		return (int)Math.signum((hit2.score - hit1.score));
	}
}