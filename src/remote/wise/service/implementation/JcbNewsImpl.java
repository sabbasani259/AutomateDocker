package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.JcbNewsBo;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.JcbNewsReqContract;
import remote.wise.service.datacontract.JcbNewsrespContract;
import remote.wise.service.datacontract.TestimonialFeedBackRespContract;

public class JcbNewsImpl {

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

	public String setJcbNews(JcbNewsReqContract reqObj) {
		// TODO Auto-generated method stub
		Logger bLogger = BusinessErrorLoggerClass.logger;
		try{
		if(reqObj.getHeadlines()==null && reqObj.getUrl()==null)
		{
			throw new CustomFault("Please provide both Headlines and Url");
		}
		}catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		String response = new JcbNewsBo().setJcbNews(reqObj.getContact_ID(),reqObj.getHeadlines(),reqObj.getUrl());
		return response;
	}

	public List<JcbNewsrespContract> getJcbNews() {
		// TODO Auto-generated method stub
		List<JcbNewsrespContract> resObj = new LinkedList<JcbNewsrespContract>();
		List<JcbNewsImpl> implObjList = new JcbNewsBo().getJcbNews();
		if(implObjList.get(0).getNews_id()!=0)
		{
		for(int i=0;i<implObjList.size();i++)
		{
			JcbNewsrespContract responseObj = new JcbNewsrespContract();
			responseObj.setNews_id(implObjList.get(i).getNews_id());
			responseObj.setContact_ID(implObjList.get(i).getContact_ID());
			responseObj.setHeadlines(implObjList.get(i).getHeadlines());
			responseObj.setUrl(implObjList.get(i).getUrl());
			
			resObj.add(responseObj);
		}
		}
		return resObj;
	}

}
