package es.upm.ctb.midas.uncertainty;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TreeManagerTemp {
	String head="";
	public TreeManagerTemp() {
		head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	}

	public int getScope(String xmlTree, String sentence, String cue) {
		int scope=0;        
		String xml = head + "\n" + xmlTree;     
        NodeList nodeList = null;
        
        //Si el cue es compuesto por varios tokens ej. No feriere
        try {
        	String cues[] = cue.split(" ");
        	if (cues.length > 1) {
        		cue = cues[0];
        	}
        }
        catch(Exception e) {      	
        }
      
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(xml));
	        Document doc = db.parse(is);	       
	        nodeList = doc.getElementsByTagName("NODE");       
		     
		} catch (Exception e) {
			System.out.println ("Error Parsing XML Scope. -> TreeManager.java");
			//e.printStackTrace();
		}
   
		//System.out.println("Nodes size: " + nodeList.getLength()); 
		for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);            
            Element elem = (Element) node;
            String nodeName = elem.getAttribute("form");
            
            if (nodeName.equalsIgnoreCase(cue) || nodeName.equalsIgnoreCase(cue+",") || nodeName.equalsIgnoreCase(cue+".") ){
            	System.out.println("\nCurrent Element: " + node.getNodeName() + " -> " + nodeName);            	      	
            	NodeList childrenList = node.getChildNodes();
            	Node parent = node.getParentNode();
            	
            	  
            	
            	if(parent.getNodeType() == Node.ELEMENT_NODE){
   				 Element e = (Element)parent;
   				 String name = e.getAttribute("form");	
   				 System.out.println("Parent: " +name);  
            	}
                      	
            	
            }//if
		}//for
		
		return scope;
    }//end method
	
	/**
	 * Analiza el parent node del cue y calcula el scope
	 */
	public int analyzeParentNode(Node parentNode, String cue1, String sentence1) {
        int scope=0;
    	Element parentElement = null;            	
    	String parentName="";    	    	

    	NodeList parentChilds = parentNode.getChildNodes();
    	
    	
    	if (parentChilds.getLength()> 0) { 
    		try {
    			parentElement = (Element) parentNode;            	
    	    	parentName = parentElement.getAttribute("form"); 
    	    	//System.out.println("\n ParentChilds Number: " + parentChilds.getLength() + "\t Parent: " + parentName + "\t Current cue:  " + cue1);
    	    	
    			Node nodeExp = getNodeToExplore(parentChilds, parentNode, cue1, sentence1 );   
    			//System.out.println("Node to explore ->2 " + nodeExp );
    			if ((nodeExp != null)) {    				
        				ArrayList<Node> visited = deepFirstSearch(nodeExp);
            			scope = getScopeUsingTree(visited, sentence1);
    			}		
    			else {                				
    				scope=sentence1.indexOf(parentName) + parentName.length() + 1;    				
    			 }
    			
    		 }catch(Exception e) {
    			 scope=sentence1.indexOf(parentName) + parentName.length() + 1;
			 }
    		
        }else {
        	 scope=sentence1.indexOf(parentName) + parentName.length() + 1;//osp
        }
    	
    	return scope;
		
	}//end method
	
	/**
	 * Obtiene el nodo a explorar a partir del nodo padre.
	 * @param nodeList
	 * @param baseNode
	 * @param cue
	 * @param sent
	 * @return
	 */
	public Node getNodeToExplore(NodeList nodeList, Node baseNode, String cue, String sent) {
		//System.out.println("\n ** Searching for node to explore ***");
		ArrayList <String> candidateNodes = new ArrayList<String>();
		HashMap<String, Node> map1= new HashMap<String, Node>();
		Node nodeToExplore = null;
		 
		int indexCue = sent.indexOf(cue);
	    
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
					 
			 if(node.getNodeType() == Node.ELEMENT_NODE){
				 Element e = (Element)node;
				 String name = e.getAttribute("form");				
				 //System.out.println("Exploring node (" + name + ")");
				 
				 if (name.equalsIgnoreCase(cue) == false) {						 
					 int indexNode = sent.indexOf(name);					 
					 if (indexNode > indexCue) {
						 NodeList nodeList1 = node.getChildNodes();
						 if (nodeList1.getLength() > 0) {
							 candidateNodes.add(name);
							 map1.put(name, node);
						 }
					 }
				 }
			}
			 
		}//end for
		
		String candidateName="";	
		try {
			int min=sent.length() + 1;
			
			for (String nodeName: candidateNodes) {			
				int nodeIndex = sent.indexOf(nodeName);
				if (nodeIndex < min) {
					min= nodeIndex;	
					candidateName= nodeName;
				}
			}//for
			
			nodeToExplore = map1.get(candidateName);
		}
		catch(Exception e) {
			nodeToExplore = null;
		}
		
		//System.out.println("Node to explore 1-> " + nodeToExplore +  " Name: " + candidateName);
		//System.out.println("**** -----------------------------------------------------  *** \n");
		return nodeToExplore;
	}//end method
	
	/**
	 * Obtain the candidate node from cantidates
	 */
	public void getCanditate(HashMap<String, Node> map1,  ArrayList <String> nodeNames, int begin, String cue, String sent) {
		String candidateName="";		
		int min=1000;
		for (String nodeName: nodeNames) {			
			int nodeIndex = sent.indexOf(nodeName);
			if (nodeIndex < min) {
				min= nodeIndex;	
				candidateName= nodeName;
			}
		}//for
		
	}
	
	/**
	 * Deep firts seach
	 * @param baseNode
	 * @return
	 */
	public ArrayList<Node> deepFirstSearch(Node baseNode) {
		Stack<Node> DFS_stack = new Stack<Node>();
		ArrayList<Node> visited = new ArrayList<Node>();
		//node.setVisited(true);
	    if(baseNode.getNodeType() == Node.ELEMENT_NODE){				
			Element e = (Element)baseNode;
			String name = e.getAttribute("form");
			//System.out.println ("node: " + name);
			//System.out.println("Base Node: " + name );
					
		}
		
		DFS_stack.add(baseNode);		
		while (!DFS_stack.isEmpty()) {
			Node nodeRemove = DFS_stack.pop();
			visited.add(nodeRemove);
			
			//Element e = (Element) nodeRemove;
			//String name = e.getAttribute("form");
			//System.out.println ("stack " + name);
			
					
			NodeList childs = nodeRemove.getChildNodes();
			//System.out.println ("sons list: " + childs.getLength());
			for (int i = 0; i < childs.getLength(); i++) {
				Node currentNode = childs.item(i);
				if(currentNode != null ) {
					DFS_stack.push(currentNode);	
					//Element e1 = (Element)currentNode;
					//String name1 = e1.getAttribute("form");
					//System.out.println ("node x: " + name1);
				}
				
			}//for

		}//while

		//System.out.println("\n +++ Visisted Nodes ++++ \n");
		
		for (Node node1 : visited) {
			
			if(node1.getNodeType() == Node.ELEMENT_NODE){
				 Element e = (Element)node1;
				 String name = e.getAttribute("form");
				 //System.out.println ("node: " + name);
			}
		}
		
	return visited;
  }//end
	
	/**
	 * Obtain the scope Using tree.
	 * @param nodes
	 * @param sentence
	 * @return
	 */
	
	public int getScopeUsingTree(ArrayList<Node> nodes, String sentence) {
		int scope=0;
	
		for (Node node: nodes) {					 
				 if(node.getNodeType() == Node.ELEMENT_NODE){
					 Element e = (Element)node;
					 String name = e.getAttribute("form");	
					 int index = sentence.indexOf(name);
					 if (index > scope) {
						 scope= index + name.length() ;
					 }
					 
				 }//if
				
		}//for
		
		return scope;
	}//end method
	
	public static void main(String args[]) {
		TreeManagerTemp tree =new TreeManagerTemp();
		TestUDPipe udpipe = new TestUDPipe();
		String sentence="Ha sido diagnosticada de cáncer de pulmón en marzo de 2019, tras consultar a su MAP por disnea moderada persistente tras cuadro catarral, sin mejoría tras varios ciclos de antibiótico y corticoides desde diciembre de 2018 .";  
		sentence = sentence.replaceAll(",", " , ");
		sentence = sentence.replace(".", " . ");
		sentence = sentence.replace(";", " ; ");
		sentence = sentence.replace(":", " : ");
		System.out.println(sentence);		
	    String cue= "diciembre";
	    
	    String xml = udpipe.process(sentence);		
		int scope1 = tree.getScope(xml, sentence, cue);

    	//System.out.println("\n Scope = " + scope1 );
    	
    	int begin = sentence.lastIndexOf(cue);
    	
    	//System.out.println("sentence lenght: " + sentence.length() + " begin: " + begin  + " scope:" +  scope1);   
    	//System.out.println(sentence.substring(begin, scope1));     
	}
}



