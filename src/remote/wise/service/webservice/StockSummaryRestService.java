package remote.wise.service.webservice;
/*
 *CR406: 20250725 : Sai Divya : Convert the Soap API  to Rest API
 */
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.StockSummaryReqContract;
import remote.wise.service.datacontract.StockSummaryRespContract;
import remote.wise.service.implementation.StockSummaryImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;
//LLOPS-164 : Sai Divya : 20250821 : Soap to Rest
/** Webservice class that handles StockSummary details
 * @author Rajani Nagaraju
 *
 */
@Path("StockSummaryRestService")
public class StockSummaryRestService 
{
	/** Webservice method that returns the Stock details associated with each stakeholder
	 * @param reqObj userLoginId and tenancyId is specified through this reqObj
	 * @return the StockCount at each stakeholder
	 * @throws CustomFault
	 */
	@Path("GetStockSummary")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<StockSummaryRespContract> getStockSummary(StockSummaryReqContract reqObj) 
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("StockSummaryService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		List<StockSummaryRespContract> response=null;
		iLogger.info("loginId:"+reqObj.getLoginId()+",  "+"TenancyId:"+reqObj.getTenancyId());
		try {
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
		reqObj.setLoginId(UserID);
		iLogger.info("Decoded userId::"+reqObj.getLoginId());

		//DF20180827-KO369761-Security Fix
		if(UserID == null)
			throw new CustomFault("Invalid Login ID");
				
		response = new StockSummaryImpl().getStockSummaryDetails(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("zonalTenancyId:"+response.get(i).getZonalTenancyId()+",  "+"zonalTenancyName:"+response.get(i).getZonalTenancyName()+",   " +
					"zonalMachineCount:"+response.get(i).getZonalMachineCount()+",  "+"dealerIdNameCountMap:"+response.get(i).getDealerIdNameCountMap());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:StockSummaryService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			iLogger.info("Error"+e);
		}
		return response;
	}

}
