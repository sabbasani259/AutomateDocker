package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AssetOwnershipReqContract;
import remote.wise.service.datacontract.AssetOwnershipRespContract;
import remote.wise.service.implementation.AssetOwnershipManagementImpl;
//import remote.wise.util.WiseLogger;

/** Webservice to handle Asset Ownership 
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "AssetOwnershipManagementService")
public class AssetOwnershipManagementService{
	
	
	/** Webservice method that gets the ownership details of a given Asset
	 * @param reqObject serialNumber for which the ownership details is required
	 * @return Returns ownership details for a given serialNumber
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetAssetOwnership", action = "GetAssetOwnership")
	public AssetOwnershipRespContract getAssetOwnership(@WebParam(name="reqObject" ) AssetOwnershipReqContract reqObject) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetOwnershipManagementService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Serial Number:"+reqObject.getSerialNumber());
		AssetOwnershipRespContract response= new AssetOwnershipManagementImpl().getAssetOwnership(reqObject);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Serial Number:"+response.getSerialNumber()+",  "+"OemAccountId:"+response.getOemAccountId()+",   " +
				"dealerAccountId:"+response.getDealerAccountId()+",  "+"customerAccountId:"+response.getCustomerAccountId()+",  " +
				"dealerName:"+response.getDealerName()+",  " +"dealerPhoneNumber:"+response.getDealerPhoneNumber()+",  "+
				"dealerEmail: "+response.getDealerEmail()+",  " +"customerName:"+response.getCustomerName()+",  "+
				"customerPhoneNumber: "+response.getCustomerPhoneNumber()+",  " +"customerEmail:"+response.getCustomerEmail()+",  "+
				"driverName: "+response.getDriverName()+",  " +"driverPhoneNumber:"+response.getDriverPhoneNumber());
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AssetOwnershipManagementService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}



	/** Webservice method to set AssetOwnership details
	 * @param reqObject specifies the owner to be set for a serialNumber
	 * @return Returns the status string
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "SetAssetOwnership", action = "SetAssetOwnership")
	public String setAssetOwnership(@WebParam(name="reqObject" ) AssetOwnershipRespContract reqObject) throws CustomFault
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetOwnershipManagementService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		if(reqObject.getCustomerAccountId() != 0)
			iLogger.info("OwnerAccountId:"+reqObject.getCustomerAccountId());
		else if(reqObject.getDealerAccountId() != 0)
			iLogger.info("OwnerAccountId:"+reqObject.getDealerAccountId());
		if(reqObject.getOemAccountId() != 0)
			iLogger.info("OwnerAccountId:"+reqObject.getOemAccountId());
		iLogger.info("Serial Number:"+reqObject.getSerialNumber());
		String response= new AssetOwnershipManagementImpl().setAssetOwnership(reqObject);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AssetOwnershipManagementService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;
	}


}
