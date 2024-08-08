package remote.wise.service.implementation;

import remote.wise.businessobject.EventDetailsBO;
import remote.wise.handler.SmsTemplate;

/**
 * @author Rajani Nagaraju
 * DefectId: 1364 - Rajani Nagaraju 20130926 - Pull SMS feature implementation
 */
public class PullSmsImpl 
{
	/** This method implements the PULL SMS functionality
	 * @param mobileNumber Mobile Number of the user who initiated the Pull SMS
	 * @param VIN Msg body as part of Pull SMS
	 * @return Returns the processing status
	 */
	public String pullSMS(String mobileNumber, String serialNumber)
	{
		String status ;
		
		//Verify the mandatory parameters
		if(mobileNumber==null)
		{
			//DF20140605 - Rajani Nagaraju - Return the VIN details in the same call to KAPSYS instead of sending new request to KAPSYS and returning "SUCCESS" in the current request
			return "Mobile Number not provided";
		}
		
		if(serialNumber==null)
		{
			//DF20140605 - Rajani Nagaraju - Return the VIN details in the same call to KAPSYS instead of sending new request to KAPSYS and returning "SUCCESS" in the current request
			return "PIN not provided";
		}
		
		String checkMobilenNum = mobileNumber.replaceAll("\\s","") ;
		String checkVIN = serialNumber.replaceAll("\\s","");
		
		if( (!(checkMobilenNum.length()>0))|| (!(checkVIN.length()>0)))
		{
			//DF20140605 - Rajani Nagaraju - Return the VIN details in the same call to KAPSYS instead of sending new request to KAPSYS and returning "SUCCESS" in the current request
			status ="PIN not provided";
			return status;
		}
		
		//Message what we receive from KAPSYSTEM would be the Keyword space Actual Message. Need to parse for the message in Application
		//For demo account keyword is KAPSYS
		//serialNumber = serialNumber.substring(7, serialNumber.length());
		//DF20131111 - Rajani Nagaraju - Always to take last text in the message as VIN (This caters to both Primary Keyword and Primary+Secondary Keyword)
		String str[]= serialNumber.split("\\s");
		serialNumber = str[str.length-1];
		
		serialNumber = serialNumber.replaceAll("\\s+","");
	
		//If 91 is added remove that to validate in mobileNumber
		if(mobileNumber.length()==12)
			mobileNumber = mobileNumber.substring(2, mobileNumber.length());
		
		EventDetailsBO boObj = new EventDetailsBO();
		status = boObj.getPullSmsDetails(mobileNumber, serialNumber);
		
		return status;
	}
	/**
	 * @author Suresh Soorneedi 
	 * defect Id: 20062015 -suresh setting the language preference via sms request
	 * This method implements the language preference setting
	 * @param dest_mobilenois the Mobile number of the user who initiated the Pull SMS
	 * @param language is the language the the user preferred
	 * @return Returns the processing status
	  */
	public String setLanguagePreference(String dest_mobileno, String language) {
		// TODO Auto-generated method stub
		//Verify the mandatory parameters
		String status ;
		System.out.println(dest_mobileno);
				if(dest_mobileno==null)
				{
					return "Mobile Number not provided";
				}
				
				if(language==null)
				{
					return "Wrong format! please follow the following format: LLI LAN (first three letters of your language) (Your mobile Number)";
				}
				String checkMobilenNum = dest_mobileno.replaceAll("\\s","") ;
				String checkLang = language.replaceAll("\\s", "");
				if(!(checkMobilenNum.length()>0) && !(checkLang.length()>0))
				{
					status ="PIN not provided";
					return status;
				}
				String mobileNumber = null;
				String msg[] = language.split("\\s");
				if(msg.length==4)
					mobileNumber = msg[3];
				String lang = null;
			
				if(msg.length>=3 && msg[2]!=null)
					lang = msg[2];
				else
					return "Language is not provided";
				
				if(dest_mobileno.length()==12)
				{
					dest_mobileno = dest_mobileno.substring(2, dest_mobileno.length());
				}
				EventDetailsBO eventObj = new EventDetailsBO();
				status = eventObj.setLanguagePreference(mobileNumber,dest_mobileno,lang);
				return status;
	}
}
