package remote.wise.service.implementation;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.UserDetailsReqContract;
import remote.wise.service.datacontract.UserDetailsRespContract;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;
/**
 * UserDetailsImpl will allow to get and set User Details
 * @author jgupta41
 *
 */
public class UserDetailsImpl {
	//Defect Id 1337 - Logger changes
	//public static WiseLogger businessError = WiseLogger.getLogger("UserDetailsImpl:","businessError");
	//static Logger businessError = Logger.getLogger("businessErrorLogger");
	//******************************************Get UserDetails for given user_Id,List of tenancy_id***************************************		
/**
 * This method will return list of user Details for given user_Id,List of tenancy_id
 * @param reqObj:userId and tenancy_id
 * @return userResp :List of user details
 * @throws CustomFault:custom exception is thrown when the user_Id is not specified or invalid,tenancy_id is invalid when specified
*/

	public List<UserDetailsRespContract> getUserDetails(UserDetailsReqContract reqObj) throws CustomFault{
		
		List<Integer> tenancyId = new LinkedList<Integer>();
		List<UserDetailsRespContract> userResp = new LinkedList<UserDetailsRespContract>();
		
		if(reqObj.getMACustConsolidatedLogin()!=null){
			if(reqObj.getTenacyIdList()!=null)
			tenancyId.addAll(reqObj.getTenacyIdList());
		}else
			tenancyId.add(reqObj.getTenancy_id());
		
		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

		if(tenancyId!=null && tenancyId.size()>0){
			tenancyId=new DateUtil().getLinkedTenancyListForTheTenancy(tenancyId);
		}
		
		
		
		UserDetailsBO userBO = new UserDetailsBO();
		//Code Added by Juhi on 17June 2013 for getting data for LoginID of particular User
		ContactEntity contact=null;
		List<UserDetailsBO> userDetailBO =new LinkedList<UserDetailsBO>();
		//This if/else block to check was to include MACustomer consolidated changes, added by Ajay on 17th July 2020
		if(reqObj.getMACustConsolidatedLogin()==null)
	 {		
		if(reqObj.getTenancy_id()!=0)
		{
		userDetailBO= userBO.getUserDetails(reqObj.getUserId(),tenancyId,reqObj.getAssetGroupId());
		}
		else if(reqObj.getUserId()!=null && reqObj.getTenancy_id()==0 )
		{
			contact= userBO.getContactEntity(reqObj.getUserId());
			
		}
	}
		else
			userDetailBO= userBO.getUserDetails(reqObj.getUserId(),tenancyId,reqObj.getAssetGroupId());	
		
		for(int i = 0;i<userDetailBO.size();i++ )
		{
			UserDetailsRespContract userRespObj = new UserDetailsRespContract();
			
			userRespObj.setLoginId(userDetailBO.get(i).getLoginId());
			userRespObj.setFirst_name(userDetailBO.get(i).getFirst_name());
			userRespObj.setLast_name(userDetailBO.get(i).getLast_name());
			userRespObj.setRole_id(userDetailBO.get(i).getRole_id());
			userRespObj.setRole_name(userDetailBO.get(i).getRole_name());
			userRespObj.setAsset_group_id(userDetailBO.get(i).getAsset_group_id());
			userRespObj.setAsset_group_name(userDetailBO.get(i).getAsset_group_name());
			userRespObj.setIs_tenancy_admin(userDetailBO.get(i).getIs_tenancy_admin());
			userRespObj.setCountryCode(userDetailBO.get(i).getCountryCode());
			userRespObj.setPrimaryMobNumber(userDetailBO.get(i).getPrimaryMobileNumber());
			userRespObj.setLanguage(userDetailBO.get(i).getLanguage());
			userRespObj.setTimeZone(userDetailBO.get(i).getTimeZone());
			userRespObj.setPrimaryEmailId(userDetailBO.get(i).getPrimaryEmailId());
			userRespObj.setTenancy_id(userDetailBO.get(i).getTenancyId());
//			Keerthi : Defect ID : 1069 : Tenancy admin count : Deleting user
			userRespObj.setTenancyAdminCount(userDetailBO.get(i).getTenancyAdminCount());
			userResp.add(userRespObj);
			
		}
		if(reqObj.getMACustConsolidatedLogin()==null){
		if(reqObj.getUserId()!=null && reqObj.getTenancy_id()==0 )
		{
			UserDetailsRespContract userRespObj = new UserDetailsRespContract();
			userRespObj.setLoginId(contact.getContact_id());
			userRespObj.setFirst_name(contact.getFirst_name());
			userRespObj.setLast_name(contact.getLast_name());
			userRespObj.setLanguage(contact.getLanguage());
			userRespObj.setCountryCode(contact.getCountryCode());
			userRespObj.setRole_id(contact.getRole().getRole_id());
			userRespObj.setRole_name(contact.getRole().getRole_name());
			userRespObj.setIs_tenancy_admin(contact.getIs_tenancy_admin());
			userRespObj.setPrimaryEmailId(contact.getPrimary_email_id());
			userRespObj.setPrimaryMobNumber(contact.getPrimary_mobile_number());
			userRespObj.setTimeZone(contact.getTimezone());
			
			userResp.add(userRespObj);
		}
		}
		
		return userResp;
		
	}
	//****************************************** End of Get UserDetails for given user_Id,List of tenancy_id***************************************		
	//******************************************Set UserDetails for given LoginId****************************************************
	/**
	 *  This method will set user Details for given LoginId
	 * @param userDetailsResp :User Details to be setted.
	 * @return:Return the status String as either Success/Failure.
	 * @throws CustomFault:custom exception is thrown when the user login id,tenancy_id,role_id is not specified or invalid
	 * @throws IOException 
	 */
	public String setUserDetails(UserDetailsRespContract userDetailsResp)throws CustomFault, IOException
	{
		//DF20171011: KO369761 - Security Check added for input text fields.
		CommonUtil util = new CommonUtil();
		String isValidinput=null;
		
		isValidinput = util.inputFieldValidation(userDetailsResp.getFirst_name());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(userDetailsResp.getLast_name());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(userDetailsResp.getPrimaryEmailId());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(userDetailsResp.getPrimaryMobNumber());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(userDetailsResp.getLoginId());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(userDetailsResp.getLanguage());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(userDetailsResp.getRole_name());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(userDetailsResp.getTimeZone());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		ListToStringConversion convObj = new ListToStringConversion();
		String asssetGrpIdList = convObj.getIntegerListString(userDetailsResp.getAsset_group_id()).toString();
		isValidinput = util.inputFieldValidation(String.valueOf(asssetGrpIdList));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}

		String asssetGrpNameList = convObj.getStringList(userDetailsResp.getAsset_group_name()).toString();
		isValidinput = util.inputFieldValidation(asssetGrpNameList);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(String.valueOf(userDetailsResp.getIs_tenancy_admin()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(String.valueOf(userDetailsResp.getRole_id()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(String.valueOf(userDetailsResp.getTenancy_id()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		isValidinput = util.inputFieldValidation(String.valueOf(userDetailsResp.getTenancyAdminCount()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		UserDetailsBO userDetailsBO=new UserDetailsBO();
		String flag=userDetailsBO.setUserDetails
				(userDetailsResp.getLoginId(),
				userDetailsResp.getFirst_name(),
				userDetailsResp.getLast_name(),		
				userDetailsResp.getRole_id(),		
				userDetailsResp.getRole_name(),
				userDetailsResp.getPrimaryMobNumber(),
				userDetailsResp.getIs_tenancy_admin(),
				userDetailsResp.getCountryCode(),
				userDetailsResp.getTenancy_id(),
				userDetailsResp.getAsset_group_id(),
				userDetailsResp.getAsset_group_name(),			
				userDetailsResp.getLanguage(),
				userDetailsResp.getTimeZone(),
				userDetailsResp.getPrimaryEmailId());	
		return flag;
	}
	
	//******************************************End of Set UserDetails for given LoginId****************************************************
	/** 
	 * @author suresh soorneedi
	 * this method will get all the languages that supports sms translation
	 * @return responseList: returns the list of languages
	 * throws exception CustomFault
	 */
	public List<String> getLanguages() throws CustomFault{
		// TODO Auto-generated method stub
		UserDetailsBO userDetailsBO=new UserDetailsBO();
		List<String> responseList = userDetailsBO.getLanguages();
		return responseList;
	}
	
	//*****************************************End of Get Languages for Language Preference***********************************************


	//20200220--Ramu B getting tenancy id
	public List<Integer> getTenacyIds(String accountId) throws CustomFault{
		
		UserDetailsBO userDetailsBO=new UserDetailsBO();
		List<Integer> responseList = userDetailsBO.getTenancyIds(accountId);
		return responseList;
	}
	

	/**
	 * method to get the UserRole
	 * @param accountId
	 * @return userRoleName
	 */
	//20200220--Ramu B getting UserRole for account id
	public String getUserRole(String accountId) throws CustomFault{
		UserDetailsBO userDetailsBO=new UserDetailsBO();
		String userRoleName = userDetailsBO.getUserRole(accountId);
		return userRoleName;
	}
}
