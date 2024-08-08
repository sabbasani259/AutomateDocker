package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.BHLDateReportImpl;
import remote.wise.util.CommonUtil;

@Path("/BHLDate")
public class BHLDateReportRESTService {

	@GET
	@Path("getBHLDate")
	@Produces(MediaType.APPLICATION_JSON)
	public List<HashMap<String,Object>> getBHLDate(@QueryParam("assetID") String assetID, @QueryParam("loginTenancyId") int loginTenancyId, @QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate,@QueryParam("MsgID") String MsgID,@QueryParam("model") String model,@QueryParam("region") String region,@QueryParam("zone") String zone,@QueryParam("dealer") String dealer,@QueryParam("customer") String customer){

		Logger iLogger = InfoLoggerClass.logger;
		List<HashMap<String,Object>> response = null;

		iLogger.info("VisualizationDashBoardRESTService:WebService Input-----> serial_number:"+assetID);
		long startTime = System.currentTimeMillis();

		String msgId=null;
		String VIN=null;

		if(assetID!=null){

			if(loginTenancyId!=0){

				VIN=new CommonUtil().validateVIN(loginTenancyId, assetID);

				if(VIN==null){


					response = new  LinkedList<HashMap<String,Object>>();

					HashMap<String, Object> respObj1  = new HashMap<String, Object>();
					respObj1.put("Error", "Machine is not under the given user hierarchy.");
					response.add(respObj1);

					return response;
				}
			}
			else{

				response = new  LinkedList<HashMap<String,Object>>();

				HashMap<String, Object> respObj1  = new HashMap<String, Object>();
				respObj1.put("Error", "loginTenancyId is mandatory.");
				response.add(respObj1);

				return response;


			}

			msgId=new CommonUtil().getMsgIdForVIN(assetID);

			if(msgId==null || (! msgId.equalsIgnoreCase("030") && ! msgId.equalsIgnoreCase("003"))){

				response = new  LinkedList<HashMap<String,Object>>();

				HashMap<String, Object> respObj1  = new HashMap<String, Object>();
				respObj1.put("Error", "The entered VIN is not of provided machine type.");
				response.add(respObj1);

				return response;
			}

		}

		response = new BHLDateReportImpl().getBHLDateReport(assetID,startDate,endDate,MsgID,model,region,zone,dealer,customer);

		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:BHLDateReportRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		return response;
	}
}
