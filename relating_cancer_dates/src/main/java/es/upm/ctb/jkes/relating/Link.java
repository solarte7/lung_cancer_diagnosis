package es.upm.ctb.jkes.relating;

public class Link {
	int id;
	int sentenceId;
	String sentence;
	String cancerEntity;
	String date;
	String event;
	String normalized;
	String patient;
	
	public String getPatient() {
		return patient;
	}
	public void setPatient(String patient) {
		this.patient = patient;
	}
	public String getNormalized() {
		return normalized;
	}
	public void setNormalized(String normalized) {
		this.normalized = normalized;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSentenceId() {
		return sentenceId;
	}
	public void setSentenceId(int sentenceId) {
		this.sentenceId = sentenceId;
	}
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public String getCancerEntity() {
		return cancerEntity;
	}
	public void setCancerEntity(String cancer) {
		this.cancerEntity = cancer;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	
	
}
