package es.upm.ctb.jkes.relating;

public class Annotation {
	int id;
	int ehr;
	int documentId;
	String documentType;
	String category;
	int senteceId;
	String sentence;
	String entity;
	String entityValue;
	String normalized;
	String negated;
	String ancestor;
	String event;
	
	public String getAncestor() {
		return ancestor;
	}
	public void setAncestor(String ancestor) {
		this.ancestor = ancestor;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	
	
	public String getNegated() {
		return negated;
	}
	public void setNegated(String negated) {
		this.negated = negated;
	}
	public String getSpeculated() {
		return speculated;
	}
	public void setSpeculated(String speculated) {
		this.speculated = speculated;
	}
	String speculated;
	
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
	public int getEhr() {
		return ehr;
	}
	public void setEhr(int ehr) {
		this.ehr = ehr;
	}
	public int getDocumentId() {
		return documentId;
	}
	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getSenteceId() {
		return senteceId;
	}
	public void setSenteceId(int senteceId) {
		this.senteceId = senteceId;
	}
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public String getEntityValue() {
		return entityValue;
	}
	public void setEntityValue(String entityValue) {
		this.entityValue = entityValue;
	}
	
	

}
