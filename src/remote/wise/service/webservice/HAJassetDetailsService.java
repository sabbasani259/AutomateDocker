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
import remote.wise.service.datacontract.HAJAssetDetailsRespContract;
import remote.wise.service.implementation.HAJassetDetailsImpl;
//import remote.wise.util.WiseLogger;

/**
 * @author sunayak
 *
 */
@WebService(name = "HAJassetDetailsService")
public class HAJassetDetailsService {
	/**
	 * This method gets All the asset and related Data i.e AssetTypeName,AssetGroupName and primaryOwnerId.
	 * @param reqObj:Get all List of assets
	 * @return respObj:Returns List of assets and related Data i.e AssetTypeName,AssetGroupName and primaryOwnerId where sendFlag is 1.
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetHAJassetDetails", action = "GetHAJassetDetails")	
	public List<HAJAssetDetailsRespContract> GetHAJassetDetails()throws CustomFault{
		Gson gson = new Gson();
	//	WiseLogger infoLogger = WiseLogger.getLogger("HAJassetDetailsService:","info");
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		infoLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		infoLogger.info("----- Webservice Input-----");
		List<HAJAssetDetailsRespContract> response = new HAJassetDetailsImpl().getHAJassetDetail();
		infoLogger.info("----- Webservice Output-----");
		
		for(int i=0; i<response.size(); i++){
		infoLogger.info("Serial_Number:"+response.get(i).getVin()+","+"PrimaryAccountId:"+response.get(i).getAccountid()+","+"AssetGroupName:"+response.get(i).getProfilecode()+","+"AssetTypeName:"+response.get(i).getModelcode()+"");
		}
		
		String assetStringJson = gson.toJson(response);
		
		String url = "http://10.106.68.9:26000/mowbly/mowblyserver/postmachines/jcb/prod/JCB";
		String uname = "hireajcbadmin@jcb.com";
		String passwd = "hireajcb";
		PushDataHireAJCB.pushData(url, uname, passwd, assetStringJson);
	    
		System.out.println("assetStringJson "+assetStringJson);
	    
		//Clear the Flag after asset details send 
		new HAJassetDetailsImpl().updateHAJassetDetail();
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		infoLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		infoLogger.info("serviceName:HAJassetDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}

	/**
	 * This method will allow to set data in  HAJassetSnapshot table for a each round off time.
	 * @return response_msg
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "SetHAJassetDetails", action = "http://webservice.service.wise.remote/SetHAJassetDetails")
	  public String SetHAJassetDetails()  throws CustomFault

	  {
		//WiseLogger infoLogger = WiseLogger.getLogger("SetHAJassetDetails:","info");
		
		 Logger infoLogger = InfoLoggerClass.logger;
			Logger fatalError = FatalLoggerClass.logger;
			Logger businessError = BusinessErrorLoggerClass.logger;
		infoLogger.info("---- Webservice Input ------");
		long startTime = System.currentTimeMillis();
		String response_msg= new HAJassetDetailsImpl().setHAJassetDetailsData();
		long endTime = System.currentTimeMillis();
		infoLogger.info("serviceName:HAJassetDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response_msg);
		infoLogger.info("----- Webservice Output-----");
		return response_msg;
	  }
}
 