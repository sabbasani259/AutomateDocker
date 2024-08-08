package remote.wise.businessentity;

public class JcbNewsEntity {
	private ContactEntity Contact_ID;
	int news_id;
	String headlines;
	String url;
	
	
	/**
	 * @return the contact_ID
	 */
	public ContactEntity getContact_ID() {
		return Contact_ID;
	}
	/**
	 * @param contact_ID the contact_ID to set
	 */
	public void setContact_ID(ContactEntity contact_ID) {
		Contact_ID = contact_ID;
	}
	/**
	 * @return the news_id
	 */
	public int getNews_id() {
		return news_id;
	}
	/**
	 * @param news_id the news_id to set
	 */
	public void setNews_id(int news_id) {
		this.news_id = news_id;
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
