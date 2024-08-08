/**
 * 
 */
package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import remote.wise.businessobject.PushDataHireAJCB;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.HAJAssetContactDetailsRespContract;
import remote.wise.service.implementation.HAJassetContactDetailsImpl;
import remote.wise.service.implementation.HAJassetDetailsImpl;
//import remote.wise.util.WiseLogger;

/**
 * @author sunayak
 *
 */
@WebService(name = "HAJassetContactDetailsService")
public class HAJassetContactDetailsService {

	/**
	 * This method gets All the contact and related Data i.e contactId,MobileNumber,EmailId,Firstname,Lastname and accountId.
	 * @param reqObj:Get all List of assets
	 * @return respObj:Returns List of contact and related Data i.e contactId,MobileNumber,EmailId,Firstname,Lastname and accountId where sendFlag is 1.
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetHAJassetContactDetails", action = "GetHAJassetContactDetails")	
	public List<HAJAssetContactDetailsRespContract> GetHAJassetContactDetails()throws CustomFault{

//		WiseLogger infoLogger = WiseLogger.getLogger("GetHAJassetContactDetails:","info");
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		Gson gson = new Gson();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		infoLogger.info("----- Webservice Input-----");
		List<HAJAssetContactDetailsRespContract> response = new HAJassetContactDetailsImpl().getHAJassetContactDetail();
		infoLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++){
		infoLogger.info("Account_Id:"+response.get(i).getAccountid()+","+"contactID:"+response.get(i).getContactid()+","+"email_Id:"+response.get(i).getEmailid()+","+"mobile_Number:"+response.get(i).getPhonenumber()+"" +
				"first_Name:"+response.get(i).getFirstname()+","+"last_Name:"+response.get(i).getLastname()+","+"Password :"+response.get(i).getPassword()+"");
		
		}
		String assetContactStringJson = gson.toJson(response);
		
		String url = "http://10.106.68.9:26000/mowbly/mowblyserver/postusers/jcb/prod/JCB";
		String uname = "hireajcbadmin@jcb.com";
		String passwd = "hireajcb";
		PushDataHireAJCB.pushData(url, uname, passwd, assetContactStringJson);
					
	    System.out.println("assetContactStringJson "+assetContactStringJson);
		//Clear the Flag after asset details send 
		new HAJassetContactDetailsImpl().updateHAJassetContactDetail();
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		infoLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		infoLogger.info("serviceName:HAJassetContactDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}

	/**
	 * This method will allow to set data in  HAJAssetContactSnapshotEntity table for a each round off time.
	 * @return response_msg
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "SetHAJassetContactDetails", action = "http://webservice.service.wise.remote/SetHAJassetContactDetails")
	  public String SetHAJassetContactDetails()  throws CustomFault

	  {
		
		//WiseLogger infoLogger = WiseLogger.getLogger("SetHAJassetContactDetails:","info");
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		infoLogger.info("---- Webservice Input ------");
		long startTime = System.currentTimeMillis();
		String response_msg= new HAJassetContactDetailsImpl().setHAJassetContactDetailsData();
		long endTime = System.currentTimeMillis();
		infoLogger.info("serviceName:HAJassetContactDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		infoLogger.info("----- Webservice Output-----");
		return response_msg;
	  }

}
