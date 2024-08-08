package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UserDetailsReqContract;
import remote.wise.service.datacontract.UserDetailsRespContract;
import remote.wise.service.implementation.UserDetailsImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;

@Path("/UserDetailsHelperRestService")
public class UserDetailsHelperRestService {

	@GET
	@Path("/getMachineGroup")
	@Produces(MediaType.APPLICATION_JSON)
	public List<HashMap<String, List<String>>>  getMachineGroupDetails(@QueryParam("userId") String userId,@QueryParam("tenancyId") int tenancyId)throws CustomFault{
		
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		
		List<HashMap<String, List<String>>> resp = new ArrayList<HashMap<String, List<String>>>();
		HashMap<String, List<String>> userResp = new HashMap<String, List<String>>();
		UserDetailsReqContract userReqContract = new UserDetailsReqContract();
		
		//added to fix edited user_roles in settings 
		List<Integer> tenancyIds=null;
		if(userId !=null){
			tenancyIds=  new UserDetailsImpl().getTenacyIds(userId);
		}
		userReqContract.setUserId(userId);
		if(tenancyIds.size()!=0){
			userReqContract.setTenancy_id(tenancyIds.get(0));
			}
			else
			{
				userReqContract.setTenancy_id(tenancyId);
			}
		List<UserDetailsRespContract> userRespObj =  new UserDetailsImpl().getUserDetails(userReqContract);
		iLogger.info("---- Webservice Input ------");
		iLogger.info("tenancy_id:"+tenancyId+",  "+"userId:"+userId);
		
		CommonUtil util = new CommonUtil();
		String isValidinput=null;
		
		for(int i=0; i<userRespObj.size(); i++)
		{
			iLogger.info("First Name:"+userRespObj.get(i).getFirst_name()+",    "+"Last Name:"+userRespObj.get(i).getLast_name()+",   " +
					"CountryCode: "+userRespObj.get(i).getCountryCode()+", role_name: "+userRespObj.get(i).getRole_name()+",   " +
					"role_id: "+userRespObj.get(i).getRole_id()+", PrimaryEmailId: "+userRespObj.get(i).getPrimaryEmailId()+",   " +
					"PrimaryMobNumber: "+userRespObj.get(i).getPrimaryMobNumber()+", Tenancy_id: "+userRespObj.get(i).getTenancy_id()+",   " +
					"LoginId: "+userRespObj.get(i).getLoginId()+",country_code:"+ userRespObj.get(i).getCountryCode()+", TimeZone: "+userRespObj.get(i).getTimeZone()+",   " +
					"Language: "+userRespObj.get(i).getLanguage()+",   " +"AssetGroupName: "+userRespObj.get(i).getAsset_group_name());
			/*isValidinput = util.inputFieldValidation(userRespObj.get(i).getFirst_name());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			userResp.put("First Name",userRespObj.get(i).getFirst_name());
			
			isValidinput = util.inputFieldValidation(userRespObj.get(i).getLast_name());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			userResp.put("Last Name",userRespObj.get(i).getLast_name());
			
			isValidinput = util.inputFieldValidation(userRespObj.get(i).getPrimaryEmailId());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			userResp.put("PrimaryEmailId",userRespObj.get(i).getPrimaryEmailId());
			
			isValidinput = util.inputFieldValidation(userRespObj.get(i).getPrimaryMobNumber());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			userResp.put("PrimaryMobileNumber",userRespObj.get(i).getPrimaryMobNumber());
			
			isValidinput = util.inputFieldValidation(userRespObj.get(i).getLoginId());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			userResp.put("LoginId",userRespObj.get(i).getLoginId());
			
			isValidinput = util.inputFieldValidation(userRespObj.get(i).getLanguage());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			userResp.put("Language",userRespObj.get(i).getLanguage());
			
			isValidinput = util.inputFieldValidation(userRespObj.get(i).getRole_name());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			userResp.put("RoleName",userRespObj.get(i).getRole_name());
			
			isValidinput = util.inputFieldValidation(userRespObj.get(i).getTimeZone());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			userResp.put("TimeZone",userRespObj.get(i).getTimeZone());*/
			
			ListToStringConversion convObj = new ListToStringConversion();
			/*String asssetGrpIdList = convObj.getIntegerListString(userRespObj.get(i).getAsset_group_id()).toString();
			isValidinput = util.inputFieldValidation(asssetGrpIdList);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			userResp.put("AssetGroupId",asssetGrpIdList);*/

			String asssetGrpNameList = convObj.getStringList(userRespObj.get(i).getAsset_group_name()).toString();
			isValidinput = util.inputFieldValidation(asssetGrpNameList);
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			userResp.put("AssetGroupName",userRespObj.get(i).getAsset_group_name());
			
			/*isValidinput = util.inputFieldValidation(String.valueOf(userRespObj.get(i).getIs_tenancy_admin()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			userResp.put("TenancyAdmin",String.valueOf(userRespObj.get(i).getIs_tenancy_admin()));
			
			isValidinput = util.inputFieldValidation(String.valueOf(userRespObj.get(i).getRole_id()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			userResp.put("RoleId",String.valueOf(userRespObj.get(i).getRole_id()));
			
			isValidinput = util.inputFieldValidation(String.valueOf(userRespObj.get(i).getTenancy_id()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			userResp.put("TenancyId",String.valueOf(userRespObj.get(i).getTenancy_id()));
			
			
			isValidinput = util.inputFieldValidation(String.valueOf(userRespObj.get(i).getTenancyAdminCount()));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			userResp.put("TenancyAdminCount",String.valueOf(userRespObj.get(i).getTenancyAdminCount()));*/
			resp.add(userResp);
		}
		
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:UserDetailsHelperRestService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return resp;
	}
	

	@GET
	@Path("/getTenancyid")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> getTenancyIds(@QueryParam("userId") String userId,@QueryParam("tenancyId") int tenancyId)throws CustomFault{
		
	Logger iLogger = InfoLoggerClass.logger;
		List<Integer> tenancyIds=null;
		if(userId !=null){
			tenancyIds=  new UserDetailsImpl().getTenacyIds(userId);
		}
		iLogger.info("---- Webservice output ------");
		iLogger.info("tenancy_id:"+tenancyIds.get(0).intValue()+",  "+"userId:"+userId);
		return tenancyIds;
	}
	
	
	/**
	 * method to get the UserRole
	 * @param userId
	 * @return userRoleName
	 */
	@GET
	@Path("/getUserRole")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUserRole(@QueryParam("userId") String userId)throws CustomFault{
		
	Logger iLogger = InfoLoggerClass.logger;
	iLogger.info("---- Webservice Input ------");
	iLogger.info("userId:"+userId);
		String userRoleName=null;
		if(userId !=null){
			userRoleName=  new UserDetailsImpl().getUserRole(userId);
		}
		iLogger.info("---- Webservice output ------");
		iLogger.info("userRole:"+userRoleName);
		return userRoleName;
	}
}
