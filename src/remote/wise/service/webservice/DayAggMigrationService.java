package remote.wise.service.webservice;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.DayAggMigrationImpl;

@Path("/migrateDayAgg")
public class DayAggMigrationService{
	
	
	

	@GET()
	@Produces("text/plain")
	public String migrateDayAgg(@QueryParam("startDate")String startDate,@QueryParam("endDate")String endDate,@QueryParam("VIN")String VIN,@QueryParam("migrationType")String migrationType) {
	  String result = "SUCCESS";
	  
		//result = new DayAggMigrationImpl().migrateDayAgg(startDate, endDate, VIN,migrationType);
	  
	  new DayAggMigrationImpl(startDate, endDate, VIN,migrationType);
	 return result;
	}
	
}


