package remote.wise.service.datacontract;

import remote.wise.businessentity.ContactEntity;

public class JcbNewsReqContract {
	private String Contact_ID;
	String headlines;
	String url;
	
	
	/**
	 * @return the contact_ID
	 */
	public String getContact_ID() {
		return Contact_ID;
	}
	/**
	 * @param contact_ID the contact_ID to set
	 */
	public void setContact_ID(String contact_ID) {
		Contact_ID = contact_ID;
	}
	/**
	 * @return the headlines
	 */
	public String getHeadlines() {
		return headlines;
	}
	/**
	 * @param headlines the headlines to set
	 */
	public void setHeadlines(String headlines) {
		this.headlines = headlines;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
}
