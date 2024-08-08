package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

import remote.wise.service.implementation.ReturnMAFlagImpl;

//CR308 : DHIRAJ K : 20220426 : Beyond Warranty closures from the web potal for MA users
@Path("/ReturnMAFlagService")
public class ReturnMAFlagService {
	@GET
	@Path("/getReturnMAFlagService")
	@Produces("text/plain")
	public String getReturnMAFlagService(@QueryParam("contactId") String contactId) throws Exception {
		System.out.println("contactId"+contactId);
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = new ReturnMAFlagImpl().ReturnMAFlag(contactId);
		return response;
	}
}
