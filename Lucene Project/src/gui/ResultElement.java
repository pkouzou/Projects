package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ResultElement extends JPanel{
	private JLabel header = new JLabel("");
	private JTextArea text = new JTextArea("");
	private JScrollPane scrollPanel;
	private String tweetID;
	private int occurences, timesClicked;
	private String labelText;
	private boolean alreadyClicked = false;
	
	public ResultElement(int counter, String userName, String tweetID, String tweet){
		alreadyClicked = false;
		this.tweetID = tweetID;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		labelText = counter + ") " + userName + " - " + tweetID;
		header.setText(labelText + "   ~   " + getClickRatio());
		header.setForeground(new Color(25, 25, 112));
		text = new JTextArea(tweet, 2, 1);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setEditable(false);
	    scrollPanel = new JScrollPane(text);
		add(header);
		add(Box.createRigidArea(new Dimension(0,5)));
	}
	
	public void setResultElement(int counter, String userName, String tweetID, String tweet){
		alreadyClicked = false;
		this.tweetID = tweetID;
		labelText = counter + ") " + userName + " - " + tweetID;
		header.setText(labelText + "   ~   " + getClickRatio());
		header.setForeground(new Color(25, 25, 112));
		text.setText(tweet);
	}
	
	public void setHistory(int occurences, int timesClicked, String historyPath){
		this.occurences = occurences;
		this.timesClicked = timesClicked;
		this.occurences = this.occurences + 1;
		header.setText(labelText + "   ~   " + getClickRatio());
		try {
			PrintWriter writer = new PrintWriter(historyPath + tweetID + ".txt");
			writer.println("occurences/times clicked");
    	    writer.println(this.occurences + " " + this.timesClicked);
    	    writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void panelClicked(String historyPath){
		
		if (!alreadyClicked) {
			timesClicked += 1;
			header.setText(labelText + "   ~   " + getClickRatio());
			try {
				PrintWriter writer = new PrintWriter(historyPath + tweetID + ".txt");
				writer.println("occurences/times clicked");
	    	    writer.println(this.occurences + " " + this.timesClicked);
	    	    writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			alreadyClicked = true;
		}
		add(scrollPanel);
		revalidate();
		repaint();
	}
	
	public void panelEntered(){
		header.setForeground(new Color(70, 130, 180));
	}
	
	public void panelExited(){
		header.setForeground(new Color(25, 25, 112));
	}
	
	public void refreshPanel(){
		remove(scrollPanel);
		revalidate();
		repaint();
	}
	
	public void setText(String text){
		this.text.setText(text);
	}
	
	public String getTweetID(){
		return tweetID;
	}
	
	public int getOccurences(){
		return occurences;
	}
	
	public int getTimesClicked(){
		return timesClicked;
	}
	
	public double getClickRatio(){
		if (occurences == 0) return 0;
		if (timesClicked == 0) return 0;
		return (double)timesClicked/occurences;
	}
	
}