package remote.wise.service.datacontract;

public class TestimonialFeedBackRespContract {

	private int Feedback_ID;
	private String Contact_ID;
	private String organisation_Name;
	private String FeedBack;
	private String role;
	private byte[] image;
	private String Current_Date;
	private int Rating;
	private double count;
	private boolean Approved;
	private String Approved_By;
	private String Category_Type;
	private String Approved_Date;
	private String Edited_By;
	
	
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
	 * @return the organisation_Name
	 */
	public String getOrganisation_Name() {
		return organisation_Name;
	}
	/**
	 * @param organisation_Name the organisation_Name to set
	 */
	public void setOrganisation_Name(String organisation_Name) {
		this.organisation_Name = organisation_Name;
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
	 * @return the rating
	 */
	public int getRating() {
		return Rating;
	}
	/**
	 * @param rating the rating to set
	 */
	public void setRating(int rating) {
		Rating = rating;
	}
	/**
	 * @return the approved
	 */
	public boolean isApproved() {
		return Approved;
	}
	/**
	 * @param approved the approved to set
	 */
	public void setApproved(boolean approved) {
		Approved = approved;
	}
	/**
	 * @return the approved_By
	 */
	public String getApproved_By() {
		return Approved_By;
	}
	/**
	 * @param approved_By the approved_By to set
	 */
	public void setApproved_By(String approved_By) {
		Approved_By = approved_By;
	}
	/**
	 * @return the approved_Date
	 */
	public String getApproved_Date() {
		return Approved_Date;
	}
	/**
	 * @param approved_Date the approved_Date to set
	 */
	public void setApproved_Date(String approved_Date) {
		Approved_Date = approved_Date;
	}
	
}
