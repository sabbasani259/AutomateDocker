package remote.wise.service.datacontract;

public class JcbNewsrespContract {

	private String Contact_ID;
	private int news_id;
	private String headlines;
	private String url;
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
