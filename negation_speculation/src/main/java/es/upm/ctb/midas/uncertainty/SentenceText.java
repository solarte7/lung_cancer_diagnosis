package es.upm.ctb.midas.uncertainty;

public class SentenceText {
	int id;	
	String cues="";
	int cuesNumber=0;
	String sentence="";
	int tokensNumber=0;
	int begin=0;
	int end=0;
	String filePath="";
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	public String getCues() {
		return cues;
	}
	public void setCues(String cues) {
		this.cues = cues;
	}
	public int getCuesNumber() {
		return cuesNumber;
	}
	public void setCuesNumber(int cuesNumber) {
		this.cuesNumber = cuesNumber;
	}
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public int getTokensNumber() {
		return tokensNumber;
	}
	public void setTokensNumber(int tokensNumber) {
		this.tokensNumber = tokensNumber;
	}
	
	public int getBegin() {
		return begin;
	}
	public void setBegin(int begin) {
		this.begin = begin;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
