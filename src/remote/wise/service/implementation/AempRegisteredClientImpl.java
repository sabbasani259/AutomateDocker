 // CR419 :Santosh : 20230714 :Aemp Changes
package remote.wise.service.implementation;

import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import remote.wise.dao.AempClientDao;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.ClientDetailsDAO;
import remote.wise.util.CommonUtil;

public class AempRegisteredClientImpl {

	public String viewClient(HttpHeaders httpHeaders, String tenancyId) {
		Logger iLogger = InfoLoggerClass.logger;
		
		String status="SUCCESS";
		String result = null;
		String userID=null;
		userID=httpHeaders.getRequestHeader("LoginId").get(0);
		
		
//		1. Validate Login Id
		if(httpHeaders.getRequestHeader("LoginId").get(0) !=null) {
			iLogger.info("Login Id received from UI"+httpHeaders.getRequestHeader("LoginId").get(0));
			userID = new CommonUtil().getUserId(httpHeaders.getRequestHeader("LoginId").get(0));
//			userID=httpHeaders.getRequestHeader("LoginId").get(0);
			iLogger.info("LoginId : " + userID);
			if(userID==null) {
				status = "FAILURE:LoginId is invalid";
				iLogger.info(status);
				return status;
			}
		} else {
			status = "FAILURE:LoginId is invalid";
			iLogger.info(status);
			return status;
		}
		
		//Get all tenancy ids for login user
		List<Integer> tenancyIds = new CommonUtil().getTenancyIdListFromLoginId(userID);
		if (tenancyIds.isEmpty())
			return "No tenancy Id is assigned to logged in user " +  userID;
		
		// view client from Aemp
		List<ClientDetailsDAO> viewClient = new AempClientDao().
				viewClientByTenancyId(tenancyIds, userID);
		
		
		if(viewClient.isEmpty()) {
			System.out.println("checking result......for account already created");
			//1. validate if there is account with same  mapping code as for provided tenancy id 
			result =new AempClientDao().toCheckAlreadyCreated(tenancyId);
			
//			result=new AempClientDao().
//			toCheckWatingForApproval(tenancyId);
			if (result != null)
			return "An account is already created for your organisation " +result;
		}
		if(viewClient.isEmpty()) {
			System.out.println("checking result is empty......for pending account");
				//2. validate if there is account pending  for approval with same  mapping code as for provided tenancy id
			result =new AempClientDao().toCheckWatingForApproval(tenancyId);
			if (result !=null)
				return "An account is already created and pending for approval for your organisation " + result;;
			}
		
		if(viewClient.equals(null)) {
			status = "FAILURE-Error occured in view Client with tenancyId. Please try again";
			iLogger.info(status);
			return status;
		}else {
		Gson gson1 = new GsonBuilder().create();
		
		result = gson1.toJson(viewClient);
		}
		
		
		return result;
}
}
