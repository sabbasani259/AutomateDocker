package remote.wise.service.webservice;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.BHLLifeReportImpl;
import remote.wise.util.CommonUtil;

@Path("/BHLLife")
public class BHLLifeReportRESTService {

	@GET
	@Path("getBHLLife")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String,Object> getBHLLife(@QueryParam("assetID") String assetID, @QueryParam("loginTenancyId") int loginTenancyId){

		Logger iLogger = InfoLoggerClass.logger;
		HashMap<String,Object> response = null;

		iLogger.info("VisualizationDashBoardRESTService:WebService Input-----> serial_number:"+assetID);
		long startTime = System.currentTimeMillis();
		String msgId=null;
		String VIN=null;

		if(assetID!=null){
			
			if(loginTenancyId!=0){

				VIN=new CommonUtil().validateVIN(loginTenancyId, assetID);

				if(VIN==null){

					response = new HashMap<String, Object>();

					response.put("Error", "Machine is not under the given user hierarchy.");

					return response;
				}
			}
			else{


				response = new HashMap<String, Object>();

				response.put("Error", "loginTenancyId is mandatory.");

				return response;

			}

			msgId=new CommonUtil().getMsgIdForVIN(assetID);

			if(msgId==null || (! msgId.equalsIgnoreCase("030") && ! msgId.equalsIgnoreCase("003"))){

				response = new HashMap<String, Object>();

				response.put("Error", "The entered VIN is not of provided machine type.");

				return response;
			}

		}

		response = new BHLLifeReportImpl().getBHLLifeReport(assetID);

		long endTime=System.currentTimeMillis();
		iLogger.info("BHLLifeReportRESTService:WebService Output -----> response:"+response);
		iLogger.info("serviceName:BHLLifeReportRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		return response;
	}
}
