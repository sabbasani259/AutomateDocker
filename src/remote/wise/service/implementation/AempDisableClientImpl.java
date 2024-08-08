 // CR419 :Santosh : 20230714 :Aemp Changes
package remote.wise.service.implementation;

import javax.ws.rs.core.HttpHeaders;

import org.apache.logging.log4j.Logger;

import remote.wise.dao.AempClientDao;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

public class AempDisableClientImpl {

	public String disableClient(HttpHeaders httpHeaders, int clientId) {
		Logger iLogger = InfoLoggerClass.logger;
		String status = "SUCCESS";

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

		// 3. Update Active status to 1 in ‘aemp_user_details’ table
		boolean isUserPresent = new AempClientDao().updateDisableActiveStatus(clientId);
		if (!isUserPresent) {
			status = "FAILURE-User provided User id is not updating.";
			iLogger.info(status);
			return status;
		}

		return status;
	}
}
