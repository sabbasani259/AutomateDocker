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

public class AempApprovelClientImpl {

	public String approveClient(HttpHeaders httpHeaders) {
		Logger iLogger = InfoLoggerClass.logger;
		String status = "SUCCESS";
		String result =null;
		

		String userID = null;
		String role = null;

		// 1. Validate Login Id
		if (httpHeaders.getRequestHeader("LoginId").get(0) != null) {
			iLogger.info("Login Id received from UI"+httpHeaders.getRequestHeader("LoginId").get(0));
			userID = new CommonUtil().getUserId(httpHeaders.getRequestHeader("LoginId").get(0));
//			 userID=httpHeaders.getRequestHeader("LoginId").get(0);
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

		// 2. Validate if Login id is Super Admin
		role = new AempClientDao().getUserRole(userID);
		if (!role.replace(" ", "").equalsIgnoreCase("SuperAdmin")) {
			status = "FAILURE-Logged in user do not access to proceed.";
			iLogger.info(status);
			return status;
		}

		// 3. 
		// Approve client from Aemp
				List<ClientDetailsDAO> viewClient = new AempClientDao().
						viewApprovedClient();
				
				
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
