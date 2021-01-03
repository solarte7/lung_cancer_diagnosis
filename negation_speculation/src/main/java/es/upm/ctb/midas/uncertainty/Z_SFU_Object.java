package es.upm.ctb.midas.uncertainty;

public class Z_SFU_Object {
	String cue;
	int freq;
	int cues;

	
	public String getCue() {
		return cue;
	}
	public void setCue(String cue) {
		this.cue = cue;
	}
	public int getFreq() {
		return freq;
	}
	public void setFreq(int freq) {
		this.freq = freq;
	}
	
	
	String sentence;
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	
	
	public int getCues() {
		return cues;
	}
	public void setCues(int cues) {
		this.cues = cues;
	}
	
	public Z_SFU_Object() {
		cues=0;
	}
	
}
