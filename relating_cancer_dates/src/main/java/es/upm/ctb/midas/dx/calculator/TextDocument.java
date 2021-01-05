package es.upm.ctb.midas.dx.calculator;

import java.util.Date;

public class TextDocument{
	String id;	
	String creationDate;
	String textDoc;
	String category;
	String subCategory;
	String ehr;
	String section;
	
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	public String getEhr() {
		return ehr;
	}
	public void setEhr(String ehr) {
		this.ehr = ehr;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
		
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String  creationDate) {
		this.creationDate = creationDate;
	}
	public String getTextDoc() {
		return textDoc;
	}
	public void setTextDoc(String textDoc) {
		this.textDoc = textDoc;
	}
	

}
