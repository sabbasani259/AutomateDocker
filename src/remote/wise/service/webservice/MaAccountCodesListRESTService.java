package remote.wise.service.webservice;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.Logger;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.MaAccountCodesListServiceImpl;
import remote.wise.service.implementation.MaAccountTenancyIdListServiceImpl;

@Path("/MaAccountCodesListRESTService")
public class MaAccountCodesListRESTService {

	@GET
	@Path("/getMaAccountCodesList")
	@Produces(MediaType.TEXT_PLAIN)
	public List<String> getMaAccountCodesList() {
		Logger iLogger = InfoLoggerClass.logger;
		List<String> response;
		
		
		//Implementation method here
		response = new MaAccountCodesListServiceImpl().getMaAccountCodesList();
		iLogger.info("response from getMaAccountCodesList() "+response);
		return response;
		
	}
}
