/**
 * 
 */
package remote.wise.service.implementation;

import java.util.ArrayList;


import remote.wise.businessobject.SMSTrigger;
import remote.wise.util.ResponseObject;
//import remote.wise.util.WiseLogger;

/**
 * @author sunayak
 *
 */
public class HAJsendSMSpktImpl {

/*	public static WiseLogger businessError = WiseLogger.getLogger("HAJsendSMSpktImpl:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("HAJsendSMSpktImpl:","fatalError");
	WiseLogger infoLogger = WiseLogger.getLogger("HAJsendSMSpktImpl:","info");*/
	
	public ArrayList<ResponseObject> HAJsendSMSpkt(String phone_Number,String otp_msg,String user_EmailId) {
	
		ArrayList<ResponseObject> feedData = null;
		SMSTrigger smsTrigger = new SMSTrigger();
		try {
			feedData = smsTrigger.SendSMS(phone_Number, otp_msg, user_EmailId);
		} catch (Exception e) {
			
		}
		
		return feedData;
	}
	
	public ArrayList<ResponseObject> HAJsendEmailpkt(String otp_msg,String user_EmailId) {
		
		ArrayList<ResponseObject> feedData = null;
		SMSTrigger smsTrigger = new SMSTrigger();
		try {
			feedData = smsTrigger.sendEmail(otp_msg, user_EmailId);
		} catch (Exception e) {
			
		}
		
		return feedData;
	}
}
