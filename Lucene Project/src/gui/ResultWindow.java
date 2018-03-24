package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import utils.HitComparator;
import utils.SearchReturn;

public class ResultWindow extends JFrame{
	private String historyPath = "";  
	private String indexPath = "";
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JPanel panel;
	private JScrollPane scrollPanel;
	private JPanel resultWindowPanel;
	private ResultElement previousSelectedElement;
	private int hitsPerPage = 10;
	private int currentPageNumber = 0;
	private int currentPage = 0;
	private ScoreDoc hits[];
	private static IndexSearcher searcher;
	private JLabel currentPageLabel = new JLabel();
	private JButton previousPageButton = new JButton("\u21E6");
	private JButton nextPageButton = new JButton("\u21E8");
	private ResultElement resultElements[];
	private BufferedReader br;
	private File file;
	private JMenuBar sortBar;
	private JMenu sortMenu;
	private JMenuItem sortItem;
	private boolean sortEnabled = false;
	
	public ResultWindow(double x, double y, SearchReturn searchReturn){
		resultElements = new ResultElement[hitsPerPage];
		hits = searchReturn.getHits();
		searcher = searchReturn.getSearcher();
		Arrays.sort(hits, HitComparator.ScoreComparator);
		previousPageButton.setFocusable(false);
		nextPageButton.setFocusable(false);
		nextPageButton.setEnabled(false);
		previousPageButton.setEnabled(false);
		if  (hits.length > hitsPerPage){
			nextPageButton.setEnabled(true);
		}

		createPageButtonListeners();

		if (Math.abs(((double)hits.length)/hitsPerPage - hits.length/hitsPerPage) < 0.1*(10^12)){
			currentPageNumber = hits.length/hitsPerPage;
		}
		else{
			currentPageNumber = hits.length/hitsPerPage + 1;
		}
		if (currentPageNumber == 0 && hits.length > 0) currentPageNumber = 1;
		currentPage = 0;
		topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
		topPanel.add(previousPageButton);
		topPanel.add(nextPageButton);
		createSortMenu();
		bottomPanel = new JPanel();
		bottomPanel.add(currentPageLabel);
		panel = new JPanel();
		resultWindowPanel = new JPanel();
		resultWindowPanel.setLayout(new BorderLayout());
		setBounds((int)x, (int)y, 600, 300);
		setResizable(false);
		setLayout(new BorderLayout());
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		setVisible(true);
		scrollPanel = new JScrollPane(panel);
		scrollPanel.setPreferredSize(new Dimension(600, 300));
		showPage(currentPage);
		resultWindowPanel.add(scrollPanel, BorderLayout.CENTER);
		resultWindowPanel.add(topPanel, BorderLayout.NORTH);
		resultWindowPanel.add(bottomPanel, BorderLayout.SOUTH);
		add(resultWindowPanel);
		panel.revalidate();
		panel.repaint();
	}
	
	public void showPage(int pageNumber){
		if (previousSelectedElement != null){
			previousSelectedElement.refreshPanel();
			previousSelectedElement = null;
		}
		
		Document doc = null;
		int upperBound = Math.min(hits.length, pageNumber*hitsPerPage + hitsPerPage);
		int counter = pageNumber*hitsPerPage + 1;
		int occurences = 0, timesClicked = 0;
	
		for (int i = pageNumber*hitsPerPage; i < upperBound; i++){
			occurences = 0;
			timesClicked = 0;
			try {
				doc = searcher.doc(hits[i].doc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
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
			
			if (resultElements[(counter-1)%hitsPerPage] == null){
				initResultElement((counter-1)%hitsPerPage, counter, doc.get("user"), doc.get("tweetID"),doc.get("tweetText"));
				resultElements[(counter-1)%hitsPerPage].setHistory(occurences, timesClicked, historyPath);
			}
			else{
				resultElements[(counter-1)%hitsPerPage].setResultElement(counter, doc.get("user"), doc.get("tweetID"),doc.get("tweetText"));
				resultElements[(counter-1)%hitsPerPage].setHistory(occurences, timesClicked, historyPath);
			}
	
			counter = counter + 1;
		}
		if (hits.length == 0){
			currentPageLabel.setText("No results found");
		}
		else{
			currentPageLabel.setText("Page " + (pageNumber+1) + " of " + currentPageNumber);
		}
	}
	
	public void createPageButtonListeners(){
		previousPageButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
		nextPageButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
		previousPageButton.addActionListener(new ActionListener(){
			  public void actionPerformed(ActionEvent e){
				  currentPage = currentPage - 1;
				  if (currentPage == 0){
					  previousPageButton.setEnabled(false);
				  }
				  showPage(currentPage);
				  nextPageButton.setEnabled(true);
			  }
		});
		nextPageButton.addActionListener(new ActionListener(){
			  public void actionPerformed(ActionEvent e){
				  currentPage = currentPage + 1;
				  if (currentPage == currentPageNumber - 1){
					  nextPageButton.setEnabled(false);
				  }
				  showPage(currentPage);
				  previousPageButton.setEnabled(true);
			  }
		});
	}
	
	public void createSortMenu(){
		
		sortBar = new JMenuBar();
		sortMenu = new JMenu("default sort");
		if (hits.length <= 1){
			sortMenu.setEnabled(false);
		}
		sortMenu.setForeground(new Color(95,158,160));
		sortItem = new JMenuItem("sort by time");
		sortItem.setForeground(new Color(95,158,160));
		sortMenu.add(sortItem);
		sortBar.add(Box.createHorizontalGlue());
		sortBar.add(sortMenu);
		sortItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (sortEnabled){
            		sortMenu.setText("default sort");
            		sortItem.setText("sort by time");
            		sortEnabled = false;
            		Arrays.sort(hits, HitComparator.ScoreComparator);
            		showPage(currentPage);
            		
            	}
            	else{
            		sortMenu.setText("sort by time");
            		sortItem.setText("default sort");
            		sortEnabled = true;
            		Arrays.sort(hits, HitComparator.TweetIDComparator);
            		showPage(currentPage);
            	}
            }

        });
		sortBar.setOpaque(false);
		topPanel.add(sortBar);
	}
	
	public void initResultElement(int i, int counter, String user, String tweetID, String tweetText){
		resultElements[i] = new ResultElement(counter, user, tweetID, tweetText);
		addResultElementListener(resultElements[i]);
		panel.add(resultElements[i]);
	}
	
	public void addResultElementListener(ResultElement resultElement){
		resultElement.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
            	if (previousSelectedElement != null){
            		previousSelectedElement.refreshPanel();
            	}
            	previousSelectedElement = resultElement;
                resultElement.panelClicked(historyPath);
            }
            
            public void mouseEntered(MouseEvent e){
                resultElement.panelEntered();
            }
            
            public void mouseExited(MouseEvent e){
            	resultElement.panelExited();
            }
            	
        });
	}
	
	public static IndexSearcher getSearcher(){
		return searcher;
	}

}
