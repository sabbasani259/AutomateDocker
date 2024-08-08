package remote.wise.service.webservice;

import java.sql.SQLException;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AssetControlUnitReqContract;
import remote.wise.service.datacontract.AssetControlUnitRespContract;
import remote.wise.service.datacontract.AssetExtendedRespContract;

import remote.wise.service.implementation.AssetExtendedImpl;
import remote.wise.service.implementation.AssetProvisioningImpl;
//import remote.wise.util.WiseLogger;

@WebService
public class AssetProvisioningService 
{
	
	
	@WebMethod
	public String SetAssetProvisioningDetails(@WebParam(name="SerialNumber") String serialNumber,
			@WebParam(name="Imei") String ImeiNumber, @WebParam(name="Sim") String SimNumber) throws CustomFault, SQLException{
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throught the application
		//WiseLogger infoLogger = WiseLogger.getLogger("AssetProvisioningService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("SerialNumber:"+serialNumber+","+"Imei:"+ImeiNumber+","+"Sim:"+SimNumber+"");
		AssetControlUnitRespContract  setControlUnitDetails=new AssetControlUnitRespContract();
		setControlUnitDetails.setIMEI(ImeiNumber);
		setControlUnitDetails.setSerialNumber(serialNumber);
		setControlUnitDetails.setSIM_NO(SimNumber);
		String response= new AssetProvisioningImpl().setAssetProvisioningDetails(setControlUnitDetails);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:AssetProvisioningService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
	}

	/*public String SetAssetProvisioningDetails(@WebParam(name="reqObj" ) AssetControlUnitRespContract reqObj ) throws CustomFault, SQLException{

		String response = new AssetProvisioningImpl().setAssetProvisioningDetails(reqObj);
		return response;


	}*/


}
