package remote.wise.service.implementation;

import java.sql.SQLException;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.service.datacontract.AssetControlUnitReqContract;
import remote.wise.service.datacontract.AssetControlUnitRespContract;
//import remote.wise.util.WiseLogger;


/** This method sets the VIN details
 * @author Deepthi 
 *
 */
public class AssetProvisioningImpl 
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("AssetProvisioningImpl:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("AssetProvisioningImpl:","fatalError");
*/	
	private String engineHours;
	
	/**
	 * @return the engineHours
	 */
	public String getEngineHours() {
		return engineHours;
	}
	
	/**
	 * @param engineHours the engineHours to set
	 */
	public void setEngineHours(String engineHours) {
		this.engineHours = engineHours;
	}


	/** This method sets the VIN details to DB
	 * @param controlReq VIN registration details
	 * @return Status message as String
	 * @throws CustomFault
	 * @throws SQLException
	 */
	public String setAssetProvisioningDetails(AssetControlUnitRespContract controlReq)
	{
		String flag= "FAILURE";
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
        //Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		//Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
		AssetDetailsBO assetBOObj= new AssetDetailsBO();	
		if(controlReq.getIMEI().length()==0 || controlReq.getSIM_NO()==null || controlReq.getSerialNumber().length()==0 )
		{
			return flag;
		}
		
		try
		{
			flag = assetBOObj.setAssetProvisionDetails(controlReq.getSerialNumber(),controlReq.getIMEI(),controlReq.getSIM_NO(), controlReq.getRegistrationDate());
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}
		
		return flag;		
	}

}
