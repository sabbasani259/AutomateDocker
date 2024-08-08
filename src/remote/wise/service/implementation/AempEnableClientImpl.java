 // CR419 :Santosh : 20230714 :Aemp Changes
package remote.wise.service.implementation;

import javax.ws.rs.core.HttpHeaders;

import org.apache.logging.log4j.Logger;

import remote.wise.dao.AempClientDao;
import remote.wise.handler.SendEmailWithKafka;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

public class AempEnableClientImpl {

	public String enableClient(HttpHeaders httpHeaders, int clientId) {
		Logger iLogger = InfoLoggerClass.logger;
		String status = "SUCCESS";

		String userID = null;
		String role = null;
		boolean isUserPresent = false;

		// get from db
		String emailId = null;
		String clientName = null;

		// 1. Validate Login Id
		if (httpHeaders.getRequestHeader("LoginId").get(0) != null) {
			iLogger.info("Login Id received from UI" + httpHeaders.getRequestHeader("LoginId").get(0));
			userID = new CommonUtil().getUserId(httpHeaders.getRequestHeader("LoginId").get(0));
//			 userID=httpHeaders.getRequestHeader("LoginId").get(0);
			iLogger.info("LoginId : " + userID);
			if (userID == null) {
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

		// 3. Get email from if user with email is already present in
		// ‘aemp_user_details’ table
		String isEmailPresent = new AempClientDao().getEmailId(clientId);
		emailId = isEmailPresent;
		if (isEmailPresent.isEmpty()) {
			status = "FAILURE-User provided email id is already registered.";
			iLogger.info(status);
			return status;
		}
		
		// 4. Generate Secret code
		String secretCode = new CommonUtil().createAESSecretCode();
		if (secretCode == null) {
			status = "FAILURE-Error in generating secret code. Please try again";
			iLogger.info(status);
			return status;
		}

		// 5. Update Active status to 1 in ‘aemp_user_details’ table
		isUserPresent = new AempClientDao().updateEnableActiveStatus(clientId,secretCode);
		if (!isUserPresent) {
			status = "FAILURE-User provided User id is not updating.";
			iLogger.info(status);
			return status;	
		}

		// 6. Get Client name by client Id 
		clientName =new AempClientDao().getClientName(clientId);
		if (!isUserPresent) {
			status = "FAILURE-User provided client id is not updating.";
			iLogger.info(status);
			return status;	
		}
				
		

		// 7. Send mail to registered client
		SendEmailWithKafka sendEmail = new SendEmailWithKafka();
		iLogger.info("send Email to: emailID= " + emailId);
		String emailSubject = "JCB AEMP Service Registration";
		String emailBody="Dear "+clientName+"," + 
				"\r\n\n" + 
				"Thanks for Registering for our AEMP LL machine data service.\r\n" + 
				"Please use below Token URL to authenticate and access Snapshot and Time series data for your machines.\r\n" + 
				"Token URL:\r\n" + 
				"https://3.111.165.155/AEMPGateway/Authenticate\r\n" + 
				"ClientId: "+clientId+"\r\n" + 
				"SecretCode: "+secretCode+"\r\n" + 
				"\r\n" + 
				"You need to append below Strings and variables in responded URL to get the data.\r\n" + 
				"For Snapshot data,\r\n" + 
				"-     “Fleet/{pageNumber}” for Fleet snapshot data.\r\n" + 
				"-     “Fleet/Equipment/{VIN}” for Single Fleet snapshot data.\r\n" + 
				"For Timeseries data,\r\n" + 
				"-	   “Fleet/Equipment/{VIN}/{startDate}/{endDate}”\r\n" + 
				"where,\r\n" + 
				"-	pageNumber is required page number.\r\n" + 
				"-	VIN is 17-digit or 7-digit PIN/machine number.\r\n" + 
				"-	startDate is start date in format “yyyy-MM-dd”.\r\n" + 
				"-	endDate is end date in format “yyyy-MM-dd”.\r\n" + 
				"\r\n\n" + 
				"Regards,\r\n" + 
				"JCB LiveLink Team\r\n" + 
				"";
		String result = sendEmail.sendMail(emailId, emailSubject, emailBody, null, null);

		iLogger.info("Send Mail  result: " + result);

		if (result.equalsIgnoreCase("FAILURE")) {
			iLogger.info("Mail send failed");
		}
		iLogger.info("Mail send SUCCESS");

		return status;
	}

}
