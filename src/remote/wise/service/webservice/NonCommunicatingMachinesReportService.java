package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.NonCommunicationDrillDownOutputContract;
import remote.wise.service.datacontract.ZoneOrDealerOrInstallationWiseSummaryOutputContract;
import remote.wise.service.implementation.NonCommunicatingMachinesReportImpl;

/**
 * @author Z1007653 : Zakir
 * 20201118
 */

@Path("/NonCommunicatingMachinesReportService")
public class NonCommunicatingMachinesReportService {
	
	/**
	 * @author Z1007653
	 * Method clearData ->
	 * This method is to clear all the data of table: 
	 * @return SUCCESS/FAILURE
	 */
	@GET
	@Path("/clearData")
	@Produces("text/plain")
	public String clearData(@QueryParam("table") String tableName) {
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		String result = new NonCommunicatingMachinesReportImpl().clearData(tableName);
		long endTime = System.currentTimeMillis();
		
		iLogger.info("NonCommunicatingMachinesReportService : clearData : service complete : total execution time " + (startTime - endTime) + " ms" );
		
		return result;
	}
	
	/**
	 * @author Z1007653
	 * Method: zonalUpdate -> updates table: zone_wise_non_comm_machine_indian/saarc as per the non communicated machine count.
	 * @return SUCCESS/FAILURE
	 */
	@GET
	@Path("/zonalUpdate")
	@Produces("text/plain")
	public String zonalUpdate(@QueryParam("range") String range, @QueryParam("nation") String nation) {
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();

		String result = "FAILURE";
		
		result = new NonCommunicatingMachinesReportImpl().zonalUpdate(range, nation);
		long endTime = System.currentTimeMillis();
		iLogger.info("NonCommunicatingMachinesReportService : zonalUpdate : service complete : total execution time " + (startTime - endTime) + " ms" );

		return result;
	}
	
	/**
	 * @author Z1007653
	 * Method: dealerUpdate -> updates table: dealer_wise_non_comm_machine_indian/saarc as per the non communicated machine count.
	 * @return SUCCESS/FAILURE
	 */
	@GET
	@Path("/dealerUpdate")
	@Produces("text/plain")
	public String dealerUpdate(@QueryParam("range") String range, @QueryParam("nation") String nation) {
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();

		String result = "FAILURE";
		
		result = new NonCommunicatingMachinesReportImpl().dealerUpdate(range, nation);
		long endTime = System.currentTimeMillis();
		iLogger.info("NonCommunicatingMachinesReportService : dealerUpdate : service complete : total execution time " + (startTime - endTime) + " ms" );

		return result;
	}
	/**
	 * @author Z1007653
	 * Method: installationDateWiseUpdate -> updates table: installation_wise_non_comm_machine_indian/saarc as per the non communicated machine count.
	 * @return SUCCESS/FAILURE
	 */
	@GET
	@Path("/installationDateWiseUpdate")
	@Produces("text/plain")
	public String installationDateWiseUpdate(@QueryParam("range") String range, @QueryParam("nation") String nation) {
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();

		String result = "FAILURE";
		
		result = new NonCommunicatingMachinesReportImpl().installationDateWiseUpdate(range, nation);
		long endTime = System.currentTimeMillis();
		iLogger.info("NonCommunicatingMachinesReportService : dealerUpdate : service complete : total execution time " + (startTime - endTime) + " ms" );

		return result;
	}
	
	/**
	 * @author Z1007653
	 * Method: NonCommDrillDown -> It fetches the data from com_rep_oem_enhanced table for warranty 'YES/NO' and for orderBy Option i.e. 'zone/dealer'
	 * @return JSON(List<NonCommunicationDrillDownOutputContract>)
	 */
	@GET
	@Path("/NonCommDrillDown")
	@Produces(MediaType.APPLICATION_JSON)
	public List<NonCommunicationDrillDownOutputContract> NonCommDrillDown(@QueryParam("range") String range,@QueryParam("warranty") String warranty,
							@QueryParam("orderBy") String orderBy, @QueryParam("nation") String nation) {
		Logger iLogger = InfoLoggerClass.logger;

		if(warranty == null || warranty.equals("")) {
			iLogger.info("NonCommunicatingMachinesReportService : NonCommDrillDown : incorrect warranty received : returning empty array" );
			return new ArrayList<NonCommunicationDrillDownOutputContract>(); 
		}
		if(orderBy == null || (!(orderBy.equalsIgnoreCase("zone")) && !(orderBy.equalsIgnoreCase("dealer")) && !(orderBy.equalsIgnoreCase("installationdate")))) {
			iLogger.info("NonCommunicatingMachinesReportService : NonCommDrillDown : incorrect orderBy received : returning empty array" );
			return new ArrayList<NonCommunicationDrillDownOutputContract>();
		}
		
		long startTime = System.currentTimeMillis();
		List<NonCommunicationDrillDownOutputContract> result = new NonCommunicatingMachinesReportImpl().NonCommDrillDown(warranty, orderBy,range,nation);
		long endTime = System.currentTimeMillis();
		iLogger.info("NonCommunicatingMachinesReportService : NonCommDrillDown : service complete : total execution time " + (startTime - endTime) + " ms" );

		return result;
	}
	
	
	/**
	 * @author Z1007653 : Zakir
	 * Method: zoneWiseSummary -> It fetches the all the data from zone_wise_non_comm_machine table
	 * @return JSON(List<ZoneAndDealerWiseSummaryOutputContract>)
	 */
	@GET
	@Path("/zoneWiseSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ZoneOrDealerOrInstallationWiseSummaryOutputContract> zoneWiseSummary(@QueryParam("warranty") String warranty, @QueryParam("nation") String nation) {
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		
		List<ZoneOrDealerOrInstallationWiseSummaryOutputContract> result = new NonCommunicatingMachinesReportImpl().getZoneWiseSummaryData(warranty, nation);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("NonCommunicatingMachinesReportService : zoneWiseSummary : service complete : total execution time " + (startTime - endTime) + " ms" );
		
		return result;
	}

	/**
	 * @author YA20167922 : Yaseswini
	 * Method: dealerWiseSummary -> It fetches all  the data from dealer_wise_non_comm_machine table
	 * @return JSON(List<ZoneAndDealerWiseSummaryOutputContract>)
	 */
	@GET
	@Path("/dealerWiseSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ZoneOrDealerOrInstallationWiseSummaryOutputContract> dealerWiseSummary(@QueryParam("warranty") String warranty, @QueryParam("nation") String nation) {
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		
		List<ZoneOrDealerOrInstallationWiseSummaryOutputContract> result = new NonCommunicatingMachinesReportImpl().getdealerWiseSummaryData(warranty, nation);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("NonCommunicatingMachinesReportService : dealerWiseSummary : service complete : total execution time " + (startTime - endTime) + " ms" );
		
		return result;
	}

	
	/**
	 * @author YA20167922 : Yaseswini
	 */
	@GET
	@Path("/installationWiseSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ZoneOrDealerOrInstallationWiseSummaryOutputContract> installationWiseSummary(@QueryParam("warranty") String warranty, @QueryParam("nation") String nation) {
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		
		List<ZoneOrDealerOrInstallationWiseSummaryOutputContract> result = new NonCommunicatingMachinesReportImpl().getinstallationWiseSummaryData(warranty, nation);
		
		long endTime = System.currentTimeMillis();
		iLogger.info("NonCommunicatingMachinesReportService : installationWiseSummary : service complete : total execution time " + (startTime - endTime) + " ms" );
		
		return result;
	}

}
 
