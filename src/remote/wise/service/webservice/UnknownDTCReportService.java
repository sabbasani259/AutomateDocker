//CR344-20220817-Vidya SagarM-CombineToFotaReport to combine required fields to wisetraceability schema .
package remote.wise.service.webservice;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.UnknownDTCAlertReportImpl;
import remote.wise.util.UnknownDTCAlertReportRespContract;
//CR344-20220811-VidyaSagarM-UnknownDTCReportService for unknown DTC 
//CR344.sn
@Path("/unknownDTCReportService")
public class UnknownDTCReportService {
	
	public static Logger iLogger = InfoLoggerClass.logger;
	public static Logger fLogger = FatalLoggerClass.logger;

	@GET
	@Path("generateUnknownDTCReport")
	@Produces({"application/json"})
	public List<UnknownDTCAlertReportRespContract> generateUnknownDTCReport(
			@QueryParam("dmType") int dmType,
			@QueryParam("dateFilter") String dateFilter,
			@QueryParam("value1") String value1, @QueryParam("value2") String value2,
			@QueryParam("modelCodeList") String modelCodeList,
			@QueryParam("profileCodeList") String profileCodeList, 
			@QueryParam("loginID") String loginID,
			@QueryParam("AssetID") String AssetID, @QueryParam("sessionID") String sessionID){

		long startTime = System.currentTimeMillis();
		try{
			byte[] encodedBytes = Base64.decodeBase64(loginID.getBytes());
			loginID= new String(encodedBytes);
		}
		catch(Exception e){
			fLogger.fatal("UnknownDTCReportService:loginID:"+loginID+"Exeption:LoginID"+e.getMessage());
		}
		
		List<UnknownDTCAlertReportRespContract> reportList=new UnknownDTCAlertReportImpl().
		getAlertReportData(dmType, dateFilter, value1, value2, modelCodeList, loginID, 
				AssetID, profileCodeList);
		long endTime = System.currentTimeMillis();

		iLogger.info("UnknownDTCReportService:loginID:"+loginID+
				" Service Execution Time in ms:"+(endTime-startTime));
		return reportList;
	}
	
	
}
//CR344.en
