/**
 * 
 */
package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.EAReprocessImpl;

/**
 * @author KO369761
 * 
 */
@Path("/EAInterfaceReprocess")
public class EAReprocessRESTService {
	
	@GET
	@Path("rolloff")
	@Produces({ MediaType.TEXT_PLAIN })
	public String reprocessAssetRollOff(@QueryParam("file_name")String fileName,@QueryParam("serial_number") String serialNumber) {
		Logger iLogger = InfoLoggerClass.logger;
		String response = null;
		iLogger.info("EA Reprocessing:AssetGateOut::serialNumber: "+serialNumber);
		long startTime = System.currentTimeMillis();
		response = new EAReprocessImpl().reprocessRollOffInterface(serialNumber, fileName);
		long endTime=System.currentTimeMillis();

		iLogger.info("serviceName:EAReprocessRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;
	}
	
	@GET
	@Path("personality")
	@Produces({ MediaType.TEXT_PLAIN })
	public String reprocessAssetPersonality(@QueryParam("file_name")String fileName,@QueryParam("serial_number") String serialNumber) {
		Logger iLogger = InfoLoggerClass.logger;
		String response = null;
		iLogger.info("EA Reprocessing:AssetGateOut::serialNumber: "+serialNumber);
		long startTime = System.currentTimeMillis();
		response = new EAReprocessImpl().reprocessPersonalityInterface(serialNumber, fileName);
		long endTime=System.currentTimeMillis();

		iLogger.info("serviceName:EAReprocessRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;
	}
	
	@GET
	@Path("gateout")
	@Produces({ MediaType.TEXT_PLAIN })
	public String reprocessAssetGateOut(@QueryParam("file_name")String fileName,@QueryParam("serial_number") String serialNumber) {
		Logger iLogger = InfoLoggerClass.logger;
		String response = null;
		iLogger.info("EA Reprocessing:AssetGateOut::serialNumber: "+serialNumber);
		long startTime = System.currentTimeMillis();
		response = new EAReprocessImpl().reprocessGateOutInterface(serialNumber, fileName);
		long endTime=System.currentTimeMillis();

		iLogger.info("serviceName:EAReprocessRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;
	}
	
	@GET
	@Path("saleFromD2C")
	@Produces({ MediaType.TEXT_PLAIN })
	public String reprocessAssetSaleFromD2C(@QueryParam("file_name")String fileName,@QueryParam("serial_number") String serialNumber) {
		Logger iLogger = InfoLoggerClass.logger;
		String response = null;
		iLogger.info("EA Reprocessing:AssetGateOut::serialNumber: "+serialNumber);
		long startTime = System.currentTimeMillis();
		response = new EAReprocessImpl().reprocessSaleFromD2CInterface(serialNumber, fileName);
		long endTime=System.currentTimeMillis();

		iLogger.info("serviceName:EAReprocessRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;
	}
	
	@GET
	@Path("installation")
	@Produces({ MediaType.TEXT_PLAIN })
	public String reprocessAssetInstallation(@QueryParam("file_name")String fileName,@QueryParam("serial_number") String serialNumber) {
		Logger iLogger = InfoLoggerClass.logger;
		String response = null;
		iLogger.info("EA Reprocessing:AssetGateOut::serialNumber: "+serialNumber);
		long startTime = System.currentTimeMillis();
		response = new EAReprocessImpl().reprocessInstallationInterface(serialNumber, fileName);
		long endTime=System.currentTimeMillis();

		iLogger.info("serviceName:EAReprocessRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;
	}
	
	@GET
	@Path("serviceHistory")
	@Produces({ MediaType.TEXT_PLAIN })
	public String reprocessAssetServiceHistory(@QueryParam("file_name")String fileName,@QueryParam("serial_number") String serialNumber) {
		Logger iLogger = InfoLoggerClass.logger;
		String response = null;
		iLogger.info("EA Reprocessing:AssetGateOut::serialNumber: "+serialNumber);
		long startTime = System.currentTimeMillis();
		response = new EAReprocessImpl().reprocessServiceHistoryInterface(serialNumber, fileName);
		long endTime=System.currentTimeMillis();

		iLogger.info("serviceName:EAReprocessRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;
	}
	
	@GET
	@Path("primaryDealerTransfer")
	@Produces({ MediaType.TEXT_PLAIN })
	public String reprocessPrimaryDealerTransfer(@QueryParam("file_name")String fileName,@QueryParam("serial_number") String serialNumber) {
		Logger iLogger = InfoLoggerClass.logger;
		String response = null;
		iLogger.info("EA Reprocessing:AssetGateOut::serialNumber: "+serialNumber);
		long startTime = System.currentTimeMillis();
		response = new EAReprocessImpl().reprocessPrimaryDealerTransferInterface(serialNumber, fileName);
		long endTime=System.currentTimeMillis();

		iLogger.info("serviceName:EAReprocessRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		return response;
	}
}