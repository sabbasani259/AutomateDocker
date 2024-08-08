package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.AssetGroupUsersReqContract;
import remote.wise.service.datacontract.AssetGroupUsersRespContract;
//import remote.wise.util.WiseLogger;

/** Implementation class to set and get the mode of Users associated to a Asset group or to the tenancy  if the asset group is not specified.
 * @author Deepthi Rao
 *
 */
public class AssetGroupUsersImpl {

	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("AssetGroupUsersImpl:","businessError");
	
	//********************************************** get Users belonging to Asset *****************************************
	/** This method returns the users associated to the Asset group or to the tenancy if the asset group is not specified.
	 * @param reqObj User belonging to the Asset group or to the tenancy can be fetched through this reqObj
	 * @return the users  associated to the asset group 
	 * @throws CustomFault custom exception 
	 */
	public AssetGroupUsersRespContract getAssetGroupUsers(AssetGroupUsersReqContract reqObject) throws CustomFault
	{
		List<String> contactId = new LinkedList<String>(); 
		
		AssetGroupUsersRespContract userRespObj = new AssetGroupUsersRespContract();
		
		if(reqObject.getLoginId()==null)
		{
			throw new CustomFault("Provide Login Id");
		}
		if(reqObject.getLoginTenancyId() ==0)
		{
			throw new CustomFault("Provide a Login tenancy Id");
		}
		
		UserDetailsBO userBO = new UserDetailsBO();
		List<UserDetailsBO> userDetailBO = userBO.getAllUsers(reqObject.getLoginId(),reqObject.getGroupId(),null,reqObject.getLoginTenancyId());
	
		for(int i = 0;i<userDetailBO.size();i++ )
		{
			contactId.add(userDetailBO.get(i).getContact().getContact_id());
		}
		
		userRespObj.setContactId(contactId);
		userRespObj.setGroupId(reqObject.getGroupId());
		userRespObj.setLoginId(reqObject.getLoginId());
		return userRespObj;
	}
	
	//********************************************** End GetUsers belonging to Asset *****************************************
	
	//********************************************** SetUsers belonging to Asset *****************************************
	/** Set the Users to the Asset Group
	 * @param reqObject Users belonging to the Asset Group or to the tenancy if the Asset group is not specified
	 * @return status String is returned as SUCCESS/FAILURE
	 * @throws CustomFault
	 */
	public String setAssetGroupUsers(AssetGroupUsersRespContract reqObject) throws CustomFault
	{
		UserDetailsBO setAssetGroupUsers = new UserDetailsBO();
		
		String status = setAssetGroupUsers.setAssetGroupUsers(reqObject.getLoginId(),reqObject.getGroupId(),reqObject.getContactId(),reqObject.getLoginTenancyId());
		return status;
		
	}
	//**************************************** End Set Method for Asset group users
	
}
