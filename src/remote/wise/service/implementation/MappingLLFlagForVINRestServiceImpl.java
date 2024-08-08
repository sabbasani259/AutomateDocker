package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.Statement;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class MappingLLFlagForVINRestServiceImpl {
	Logger infoLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;
	public String updateLLFlagForMachine(String serialNumber, String llFlag) throws CustomFault {
		Connection connection = null;
		Statement statement = null;		
		String response = "SUCCESS";
		if (serialNumber == null) {
			throw new CustomFault("Serial Number should be specified");
		}
		try {			
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();			
			statement = connection.createStatement();			
			String updateQuery = "UPDATE asset SET LLFlag = '"+llFlag+"' WHERE serial_number='"+serialNumber+"';";
			infoLogger.info(" constructed Query : ---> " + updateQuery);
			statement.executeUpdate(updateQuery);
			infoLogger.info(" FFlag"+ llFlag + "updated in Asset table corresponding to the  VIN "+ serialNumber);			
		}catch (Exception e) {
			fLogger.fatal("Exception in Making connection to DS"+e.getMessage());
			return "FAILURE";
		}finally{			
			    try { statement.close(); } catch (Exception e) { e.printStackTrace(); }
			    try { connection.close(); } catch (Exception e) { e.printStackTrace(); }
		}
		return response;
	}

}
