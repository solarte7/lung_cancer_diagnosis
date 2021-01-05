package es.upm.ctb.midas.annotator.textPreprocessing;

import java.util.Date;

public class Document {
	int docId;
	Date fechaNacimiento;
	Date fechaInicio;
	Date fechaCreaObjeto;
	int ehr;
	String sexo;
	String estadoProceso;
	String tipoObjeto;
	String plantillaNota;
	String descripcion;
	String tituloInforme;
	String textoInforme;
	
	
	public Document() {
		
	}
	public int  getDocId() {
		return docId;
	}
	public void setDocId(int  docId) {
		this.docId = docId;
	}
	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	public Date getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	public Date getFechaCreaObjeto() {
		return fechaCreaObjeto;
	}
	public void setFechaCreaObjeto(Date fechaCreaObjeto) {
		this.fechaCreaObjeto = fechaCreaObjeto;
	}
	public int getEhr() {
		return ehr;
	}
	public void setEhr(int ehr) {
		this.ehr = ehr;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getEstadoProceso() {
		return estadoProceso;
	}
	public void setEstadoProceso(String estadoProceso) {
		this.estadoProceso = estadoProceso;
	}
	public String getTipoObjeto() {
		return tipoObjeto;
	}
	public void setTipoObjeto(String tipoObjeto) {
		this.tipoObjeto = tipoObjeto;
	}
	public String getPlantillaNota() {
		return plantillaNota;
	}
	public void setPlantillaNota(String plantillaNota) {
		this.plantillaNota = plantillaNota;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getTituloInforme() {
		return tituloInforme;
	}
	public void setTituloInforme(String tituloInforme) {
		this.tituloInforme = tituloInforme;
	}
	public String getTextoInforme() {
		return textoInforme;
	}
	public void setTextoInforme(String textoInforme) {
		this.textoInforme = textoInforme;
	}
	
	
}
