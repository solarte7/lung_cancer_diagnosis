package es.upm.ctb.midas.dx.calculator;

import java.util.Date;

public class DxAnnotation {
	int id;
	int   documentId;
	String  ehr;
	String category;
	String subCategory;
	String documentDate;
	Date documentDateNew;
	int sentenceId;
	String  date;
	Date dateNew;
	int begin;
	int end;
	String  negated;	
	String annotadeText;
	String sentence;
	String section;
	int dateDay;
	int dateMonth;
	int dateYear;
	String jkesId;
	String description = "";
	String dateValue;
	Date dateValueNew;
	
	public String getDateValue() {
		return dateValue;
	}
	public void setDateValue(String dateValue) {
		this.dateValue = dateValue;
	}
	public Date getDateValueNew() {
		return dateValueNew;
	}
	public void setDateValueNew(Date dateValueNew) {
		this.dateValueNew = dateValueNew;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDocumentId() {
		return documentId;
	}
	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}
	public String getEhr() {
		return ehr;
	}
	public void setEhr(String ehr) {
		this.ehr = ehr;
	}
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
	public String getDocumentDate() {
		return documentDate;
	}
	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}
	public Date getDocumentDateNew() {
		return documentDateNew;
	}
	public void setDocumentDateNew(Date documentDateNew) {
		this.documentDateNew = documentDateNew;
	}
	public int getSentenceId() {
		return sentenceId;
	}
	public void setSentenceId(int sentenceId) {
		this.sentenceId = sentenceId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Date getDateNew() {
		return dateNew;
	}
	public void setDateNew(Date dateNew) {
		this.dateNew = dateNew;
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
	public String getNegated() {
		return negated;
	}
	public void setNegated(String negated) {
		this.negated = negated;
	}
	public String getAnnotadeText() {
		return annotadeText;
	}
	public void setAnnotadeText(String annotadeText) {
		this.annotadeText = annotadeText;
	}
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public int getDateDay() {
		return dateDay;
	}
	public void setDateDay(int dateDay) {
		this.dateDay = dateDay;
	}
	public int getDateMonth() {
		return dateMonth;
	}
	public void setDateMonth(int dateMonth) {
		this.dateMonth = dateMonth;
	}
	public int getDateYear() {
		return dateYear;
	}
	public void setDateYear(int dateYear) {
		this.dateYear = dateYear;
	}
	public String getJkesId() {
		return jkesId;
	}
	public void setJkesId(String jkesId) {
		this.jkesId = jkesId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

}
