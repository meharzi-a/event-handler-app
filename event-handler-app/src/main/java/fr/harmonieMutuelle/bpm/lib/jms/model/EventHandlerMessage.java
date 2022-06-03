package fr.harmonieMutuelle.bpm.lib.jms.model;

public class EventHandlerMessage {
	
	private long idDemande;
	private String typeEvent;
	private String dateEvent;
	private String typeProcessus;	
	private String businessDataType;
	
	public long getIdDemande() {
		return idDemande;
	}
	public void setIdDemande(long idDemande) {
		this.idDemande = idDemande;
	}
	public String getTypeEvent() {
		return typeEvent;
	}
	public void setTypeEvent(String typeEvent) {
		this.typeEvent = typeEvent;
	}
	public String getDateEvent() {
		return dateEvent;
	}
	public void setDateEvent(String dateEvent) {
		this.dateEvent = dateEvent;
	}
	public String getTypeProcessus() {
		return typeProcessus;
	}
	public void setTypeProcessus(String typeProcessus) {
		this.typeProcessus = typeProcessus;
	}
	public String getBusinessDataType() {
		return businessDataType;
	}
	public void setBusinessDataType(String businessDataType) {
		this.businessDataType = businessDataType;
	}
	
}
