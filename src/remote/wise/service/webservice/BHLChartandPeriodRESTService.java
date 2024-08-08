/**
 * 
 */
package remote.wise.service.webservice;

import java.util.HashMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.BHLChartandPeriodImpl;
import remote.wise.util.CommonUtil;

/**
 * @author koti
 *
 */
@Path("/BHLCharts")
public class BHLChartandPeriodRESTService {

	@GET
	@Path("getBHLChartsandPeriods")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String,Object> getBHLChartsAndPeriods(@QueryParam("assetID") String assetID, @QueryParam("loginTenancyId") int loginTenancyId,@QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate,@QueryParam("MsgID") String MsgID,@QueryParam("model") String model,@QueryParam("region") String region,@QueryParam("zone") String zone,@QueryParam("dealer") String dealer,@QueryParam("customer") String customer){

		Logger iLogger = InfoLoggerClass.logger;
		HashMap<String,Object> response = null;

		iLogger.info("BHLChartandPeriodRESTService:WebService Input-----> serial_number:"+assetID+" start_date:"+startDate+" end_date:"+endDate);
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

		response = new BHLChartandPeriodImpl().getBHLChartsAndPeriods(assetID,startDate,endDate,MsgID,model,region,zone,dealer,customer);

		long endTime=System.currentTimeMillis();
		iLogger.info("BHLChartandPeriodRESTService:WebService Output -----> response:"+response);
		iLogger.info("serviceName:BHLChartandPeriodRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		return response;


	}

}
