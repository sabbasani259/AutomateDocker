package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.TestimonialFeedBackBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.ApprovedFeedBackReqContract;
import remote.wise.service.datacontract.TestimonialFeedBackReqContract;
import remote.wise.service.datacontract.TestimonialFeedBackRespContract;

public class TestimonialFeedBackImpl {

	private int Feedback_ID;
	private String Contact_ID;
	private String Current_Date;
	private String FeedBack;
	private String role;
	private byte[] image;
	private String Organisation_Name;
	private String Edited_By;
	private String Category_Type;
	private int rating;
	private double count;
	
	/**
	 * @return the rating
	 */
	public int getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(int rating) {
		this.rating = rating;
	}

	

	/**
	 * @return the count
	 */
	public double getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(double count) {
		this.count = count;
	}

	/**
	 * @return the category_Type
	 */
	public String getCategory_Type() {
		return Category_Type;
	}

	/**
	 * @param category_Type the category_Type to set
	 */
	public void setCategory_Type(String category_Type) {
		Category_Type = category_Type;
	}

	/**
	 * @return the edited_By
	 */
	public String getEdited_By() {
		return Edited_By;
	}

	/**
	 * @param edited_By the edited_By to set
	 */
	public void setEdited_By(String edited_By) {
		Edited_By = edited_By;
	}

	/**
	 * @return the organisation_Name
	 */
	public String getOrganisation_Name() {
		return Organisation_Name;
	}

	/**
	 * @param organisation_Name the organisation_Name to set
	 */
	public void setOrganisation_Name(String organisation_Name) {
		Organisation_Name = organisation_Name;
	}

	/**
	 * @return the image
	 */
	public byte[] getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(byte[] image) {
		this.image = image;
	}

	/**
	 * @return the feedback_ID
	 */
	public int getFeedback_ID() {
		return Feedback_ID;
	}

	/**
	 * @param feedback_ID the feedback_ID to set
	 */
	public void setFeedback_ID(int feedback_ID) {
		Feedback_ID = feedback_ID;
	}

	/**
	 * @return the current_Date
	 */
	public String getCurrent_Date() {
		return Current_Date;
	}

	/**
	 * @param current_Date the current_Date to set
	 */
	public void setCurrent_Date(String current_Date) {
		Current_Date = current_Date;
	}

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
	 * @return the feedBack
	 */
	public String getFeedBack() {
		return FeedBack;
	}

	/**
	 * @param feedBack the feedBack to set
	 */
	public void setFeedBack(String feedBack) {
		FeedBack = feedBack;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	public String setFeedBackForm(TestimonialFeedBackReqContract reqObj) {
		// TODO Auto-generated method stub
		Logger bLogger = BusinessErrorLoggerClass.logger;

	try{	
		if(reqObj.getContact_ID()==null)
		{
			throw new CustomFault("Invalid User Credentials please provide Contact ID");
		}
		if(reqObj.getFeedBack()==null)
		{
			throw new CustomFault("Please give Feedback ");
		}
		if(reqObj.getImage()!=null && reqObj.getImage().length > (256*1024*1024))
		{
			throw new CustomFault("Image Length should not exceed 56KB");
		}
	}catch(CustomFault e)
	{
		bLogger.error("Custom Fault: "+ e.getFaultInfo());
	}
		String response = new TestimonialFeedBackBO().setFeedBackForm(reqObj.getContact_ID(),reqObj.getImage(),reqObj.getFeedBack(),reqObj.getRating(),reqObj.getEdited_By(),reqObj.getOrganisation_Name(),reqObj.getCategory_Type());
		return response;
	}

	public String setFeedBackApproval(TestimonialFeedBackReqContract reqObj) {
		// TODO Auto-generated method stub
		Logger bLogger = BusinessErrorLoggerClass.logger;
		try{	
			if(reqObj.getFeedback_ID()==0)
			{
				throw new CustomFault("Not approved any feedback");
			}
		}catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
			String response = new TestimonialFeedBackBO().setFeedBackApproval(reqObj.getFeedback_ID(),reqObj.isApproved(),reqObj.getApproved_By());
			return response;
		}

	public List<TestimonialFeedBackRespContract> getApprovedFeedBacks(ApprovedFeedBackReqContract reqObj) {
		// TODO Auto-generated method stub
		List<TestimonialFeedBackRespContract> responseObjList = new LinkedList<TestimonialFeedBackRespContract>();
		String contact_Id =null;
		if(reqObj != null)
			contact_Id = reqObj.getContact_Id();
		List<TestimonialFeedBackImpl> implObjList = new TestimonialFeedBackBO().getApprovedFeedBacks(contact_Id);
		if(implObjList!=null && implObjList.size()>0)
		{
			if(implObjList.get(0).getFeedback_ID()!=0)
			{
			for(int i=0;i<implObjList.size();i++)
			{
				TestimonialFeedBackRespContract responseObj = new TestimonialFeedBackRespContract();
				responseObj.setFeedback_ID(implObjList.get(i).getFeedback_ID());
				responseObj.setCurrent_Date(implObjList.get(i).getCurrent_Date());
				responseObj.setContact_ID(implObjList.get(i).getContact_ID());
				responseObj.setFeedBack(implObjList.get(i).getFeedBack());
				responseObj.setImage(implObjList.get(i).getImage());
				responseObj.setRole(implObjList.get(i).getRole());
				responseObj.setOrganisation_Name(implObjList.get(i).getOrganisation_Name());
				responseObj.setEdited_By(implObjList.get(i).getEdited_By());
				responseObj.setCategory_Type(implObjList.get(i).getCategory_Type());
				responseObj.setRating(implObjList.get(i).getRating());
				responseObjList.add(responseObj);
			}
			}
			else
			{
				for(int i=0;i<implObjList.size();i++)
				{
					TestimonialFeedBackRespContract responseObj = new TestimonialFeedBackRespContract();
					responseObj.setRating(implObjList.get(i).getRating());
					responseObj.setCount(implObjList.get(i).getCount());
					responseObjList.add(responseObj);
				}
			}
		}
		
		return responseObjList;
	}
}
