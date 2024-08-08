 // CR419 :Santosh : 20230714 :Aemp Changes
package remote.wise.service.implementation;

import java.util.HashMap;

import javax.ws.rs.core.HttpHeaders;

import org.apache.logging.log4j.Logger;

import remote.wise.dao.AempClientDao;
import remote.wise.handler.SendEmailWithKafka;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

public class AempClientImpl {

	public String createClient(HttpHeaders httpHeaders, HashMap<String, String> clientDetails) {
		
		Logger iLogger = InfoLoggerClass.logger;
		String status="SUCCESS";
		String userID=null;
		String role=null;
		boolean isPresent=false;
		boolean isTenancyIdPresent=false;
		String clientName=clientDetails.get("clientName");
		String emailId=clientDetails.get("email");
		String tenancyId=clientDetails.get("tenancyId");
		String mobileNo=clientDetails.get("mobileNo");
		
		//1. Validate Login Id
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
		
		//2. Validate if Login id is Super Admin
		role = new AempClientDao().getUserRole(userID);
//		if(!role.replace(" " , "").equalsIgnoreCase("SuperAdmin")) {
//			status = "FAILURE-Logged in user do not access to proceed.";
//			iLogger.info(status);
//			return status;
//		}
		//3. Validate if user with mobile is already present in ‘aemp_user_details’ table
				isPresent = new AempClientDao().ifMobileNoPresent(mobileNo);
				if(isPresent) {
					status = "User provided email : "+mobileNo +"  is already registered.";
					iLogger.info(status);
					return status;
				}
		
		//3. Validate if user with email is already present in ‘aemp_user_details’ table
		isPresent = new AempClientDao().checkIfPresent(emailId);
		if(isPresent) {
			status = "User provided email : "+emailId +" id is already registered.";
			iLogger.info(status);
			return status;
		}
		
		//4. Validate if user with accountId is already present in ‘aemp_user_details’ table
				isTenancyIdPresent = new AempClientDao().checkIfTenancyIdPresent(tenancyId);
				if(isTenancyIdPresent) {
					status = "User provided Account with is already registered.";
					iLogger.info(status);
					return status;
				}
		
		//5. Generate Secret code
		String secretCode=new CommonUtil().createAESSecretCode();
		if(secretCode==null) {
			status = "FAILURE-Error in generating secret code. Please try again";
			iLogger.info(status);
			return status;
		}
		
		//6. Insert details in ‘aemp_user_details’ table
		String insertStatus = new AempClientDao().insertClientDetails(userID,clientName, tenancyId, mobileNo, secretCode, emailId);
		if(!insertStatus.equalsIgnoreCase("SUCCESS")) {
			status = "FAILURE-Error occured in Client creation. Please try again";
			iLogger.info(status);
			return status;
		}
		
		//7. Get clientId
		int clientId=new AempClientDao().getClientId(emailId);

		//8. Send mail to registered client
		SendEmailWithKafka sendEmail = new SendEmailWithKafka();
		iLogger.info("send Email to: emailID= " + emailId);
		String emailSubject = "JCB AEMP Service Registration";
		String emailBody="Dear "+clientName+"," + 
				"\r\n\n" + 
				"Thanks for requesting  for our AEMP LiveLink machine data service.\r\n" + 
				"Kindly, do contact our Operational team for the request approval.\r\n\n" + 
			
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
