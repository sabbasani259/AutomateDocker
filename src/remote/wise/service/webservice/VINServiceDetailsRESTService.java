package remote.wise.service.webservice;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.VinDetailsResponseContract;
import remote.wise.service.implementation.VINServiceDetailsRESTImpl;
import remote.wise.service.implementation.VinServiceDetailsImpl;

/**
 * @author su334449
 *
 */

@Path("/VINService")
public class VINServiceDetailsRESTService {

	@GET
	@Path("getdetails")
	@Produces(MediaType.APPLICATION_JSON)
	public List<VinDetailsResponseContract> getVINService(@QueryParam("serialNumber") String serialNumber) throws CustomFault{
		Logger flogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		
		List<VinDetailsResponseContract>vinDetailsList=null;
		VINServiceDetailsRESTImpl implObj = new VINServiceDetailsRESTImpl();
		try{
			if(!(serialNumber.equals(null))||(serialNumber.isEmpty())){
				vinDetailsList = implObj.getVINServiceDetails(serialNumber);
			}
		}catch(Exception e){
			flogger.info("Exception in VINServiceDetailsRESTService: "+e);
		}
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:VinServiceDetailsService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return vinDetailsList;
	}
	
	//DF20191021 - Rajani Nagaraju - Extended Warranty Changes - START 
		//Adding REST service wrapper for the existing implementation so that serive_details_report table population can be done for a specific VIN, if VIN is provided as input
		@GET
		@Path("setServiceDetails")
		public String setVinServiceDetails(@QueryParam("serialNumber") String serialNumber)
		{
			Logger iLogger = InfoLoggerClass.logger;
			iLogger.info("---- Webservice Input: VinServiceDetailsService ------ serialNumber:"+serialNumber);
			
			long startTime = System.currentTimeMillis();
			String response = new VinServiceDetailsImpl().setServiceDetails(serialNumber);
			iLogger.info("----- Webservice Output: VinServiceDetailsService ----- response:"+response);
			long endTime=System.currentTimeMillis();
			
			iLogger.info("VinServiceDetailsService: Webservice Execution Time in ms:"+(endTime-startTime));
			
			return response;
		}
		//DF20191021 - Rajani Nagaraju - Extended Warranty Changes - END 
}