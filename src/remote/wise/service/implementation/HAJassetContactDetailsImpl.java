package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import remote.wise.businessobject.HAJassetDetailsBO;
import remote.wise.service.datacontract.HAJAssetContactDetailsRespContract;
import remote.wise.service.datacontract.HAJAssetDetailsRespContract;

public class HAJassetContactDetailsImpl {
	    private String contactID;
	    private int accountID;
	    private String first_Name;
	    private String last_Name;
	    private String mobile_Number;
	    private String password;
	    
	    /**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * @param password the password to set
		 */
		public void setPassword(String password) {
			this.password = password;
		}

		/**
		 * @return the accountID
		 */
		public int getAccountID() {
			return accountID;
		}

		/**
		 * @param accountID the accountID to set
		 */
		public void setAccountID(int accountID) {
			this.accountID = accountID;
		}

		private String email_Id;
	    /**
		 * @return the contactID
		 */
		public String getContactID() {
			return contactID;
		}

		/**
		 * @param contactID the contactID to set
		 */
		public void setContactID(String contactID) {
			this.contactID = contactID;
		}

		/**
		 * @return the first_Name
		 */
		public String getFirst_Name() {
			return first_Name;
		}

		/**
		 * @param first_Name the first_Name to set
		 */
		public void setFirst_Name(String first_Name) {
			this.first_Name = first_Name;
		}

		/**
		 * @return the last_Name
		 */
		public String getLast_Name() {
			return last_Name;
		}

		/**
		 * @param last_Name the last_Name to set
		 */
		public void setLast_Name(String last_Name) {
			this.last_Name = last_Name;
		}

		/**
		 * @return the mobile_Number
		 */
		public String getMobile_Number() {
			return mobile_Number;
		}

		/**
		 * @param mobile_Number the mobile_Number to set
		 */
		public void setMobile_Number(String mobile_Number) {
			this.mobile_Number = mobile_Number;
		}

		/**
		 * @return the email_Id
		 */
		public String getEmail_Id() {
			return email_Id;
		}

		/**
		 * @param email_Id the email_Id to set
		 */
		public void setEmail_Id(String email_Id) {
			this.email_Id = email_Id;
		}

	public List<HAJAssetContactDetailsRespContract> getHAJassetContactDetail() {
		List<HAJAssetContactDetailsRespContract> listRespObj=new LinkedList<HAJAssetContactDetailsRespContract>();
		
		HAJassetDetailsBO HAJassetDetailsObj=new HAJassetDetailsBO();
		List<HAJassetContactDetailsImpl> implObj=HAJassetDetailsObj.getHAJassetContactDetail();

		HAJAssetContactDetailsRespContract respObj=null;
		for(int i=0;i<implObj.size();i++)
		{
				respObj=new HAJAssetContactDetailsRespContract();
				respObj.setContactid(implObj.get(i).getContactID());
				respObj.setAccountid(implObj.get(i).getAccountID());
				respObj.setFirstname(implObj.get(i).getFirst_Name());
				respObj.setLastname(implObj.get(i).getLast_Name());
				respObj.setPhonenumber(implObj.get(i).getMobile_Number());
				respObj.setEmailid(implObj.get(i).getEmail_Id());
				respObj.setPassword(implObj.get(i).getPassword());
				listRespObj.add(respObj);
		}
	
	return listRespObj;
	}

	public String setHAJassetContactDetailsData() {
		// TODO Auto-generated method stub
		HAJassetDetailsBO HAJassetDetailsObj=new HAJassetDetailsBO();
		String flag=HAJassetDetailsObj.setHAJassetContactData(); 
		return flag;
	}

	public void updateHAJassetContactDetail() {
		// TODO Auto-generated method stub
		HAJassetDetailsBO HAJassetDetailsObj=new HAJassetDetailsBO();
		HAJassetDetailsObj.updateHAJassetContactDetailService();
		
	}

}
