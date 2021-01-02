package es.upm.ctb.midas.uncertainty;
import javax.swing.*;

import edu.stanford.nlp.util.CoreMap;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;  


public class GUI implements ActionListener{
	SpanishTokenizer tokenizer;
	Annotator annotator1;
	ArrayList<SentenceText>  sentenceObjects;
	
	
	
	
	JLabel label1,label2, label3;  
	JTextArea area1, area2, area3;
	JScrollPane scroll1, scroll2,scroll3;
	JButton button1, button2;  
	GUI() {  
		 initialice();
	    JFrame frame = new JFrame("Negation & Uncertainty");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // set the size of the frame
        frame.setSize(1024, 450);
        
        // set the rows and cols of the grid, as well the distances between them
        GridLayout grid = new GridLayout(3,2);
        // what layout we want to use for our frame
        frame.setLayout(grid);
        frame.setLayout(grid);
       
        // what layout we want to use for our frame
        
	    label1=new JLabel("Sentence");	    
	    label2=new JLabel("Scope");  
	    label3= new JLabel ("---");
	    
	    area1 = new JTextArea(7, 400); area1.setLineWrap(true);
	    area2 = new JTextArea(14, 400); area2.setLineWrap(true);
	    area3 = new JTextArea(5, 200); area3.setLineWrap(true);
	    area1.setFont(new Font("Arial", Font.PLAIN, 14));
	    area2.setFont(new Font("Arial", Font.PLAIN, 14));
	    
	    scroll1 = new JScrollPane(area1);	    
	    scroll2 = new JScrollPane(area2);
	    scroll3 = new JScrollPane(area3);	    
	    
	    button1= new JButton("Execute");
	    button2= new JButton("Clean");
	    scroll1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    scroll1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    
	    scroll2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    scroll2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    
	    scroll3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    scroll3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    
	    frame.add(label1);
	    frame.add(scroll1);
	    frame.add(label2);
	    frame.add(scroll2);
	    	    
	    frame.add(button2);
	    frame.add(button1);
	    
	 
	    button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
               String sentence = area1.getText();              
               processSentence(sentence);   
               print();
            		   
            		   
            }
        });
	    
	    button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                area1.setText("");
                area2.setText("");
                annotator1.cleanScopeList();
            }
        });
	    
	    frame.setVisible(true);
	    
	}  
	public void actionPerformed(ActionEvent e){ 
	   
	} 
	
	public void initialice() {		
			tokenizer =  new SpanishTokenizer();
			annotator1 = new Annotator();
			
	}
	
	public void processSentence(String text) {
		List<CoreMap> sentences = tokenizer.tokenizeDocument(text);
		 annotator1.process(sentences, "path");		 
				
	}
	
	public void  print() {
		
		ArrayList<Scope> scopeList = annotator1.getScopeList();	
		
		String message="";
        for(Scope scope: scopeList) {
        	  String sentence =scope.getSentence();
			  String cue =scope.getPhrase();
			
			  int begin = scope.getBegin();
			  int end = scope.getEnd();
			  String type= scope.getType();
			  try {
				  String scopeText = sentence.substring(begin, end);
				  message = "Cue: " +  cue + "   ["  + type + "]" + "   Begin: " + begin  +  "   End: " + end + "\n";
				  message = message + "Scope: " + scopeText + "\n ";
				  message = message + "----------------------------------------------------------------------\n ";
			  }
			  catch (Exception e) {
				 
			  }
			 
			  area2.append(message );
			  message="";
        }//for
	}
	
	public static void main(String[] args) {  
	    new GUI();  
	}  
	

}
