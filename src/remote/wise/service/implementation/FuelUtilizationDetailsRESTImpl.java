package remote.wise.service.implementation;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.FuelUtilizationDetailBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.FuelUtilizationDetailReqContract;
import remote.wise.service.datacontract.FuelUtilizationDetailRespContract;

/**
 * FuelUtilizationDetailImpl will allow to List ofFuel Utilization Details for specified LoginId and SerialNumber
 * @author KO369761
 *
 */

public class FuelUtilizationDetailsRESTImpl {


	public HashMap<String, TreeMap<Integer, String>> getFuelUtilizationDetails(String loginId, String serialNumber, String period) throws CustomFault{

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		HashMap<String, TreeMap<Integer, String>> response = null;

		if(loginId==null){			
			bLogger.error("Please pass a LoginId");
			throw new CustomFault("Please pass a LoginId");
		}

		if(serialNumber==null){
			bLogger.error("Please pass a SerialNumber");
			throw new CustomFault("Please pass a SerialNumber");
		}

		if(period==null){
			bLogger.error("Please pass a period");
			throw new CustomFault("Please pass a period");
		}
		
		FuelUtilizationDetailBO fuelUtilizationDetailBO=new FuelUtilizationDetailBO();	
		response = fuelUtilizationDetailBO.getFuelUtilizationDetailListForWeek(period, loginId, serialNumber);

		return response;
	}
}
