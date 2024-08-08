package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.Logger;

import remote.wise.dao.InsertComRepoEnchIntoCVSFileDAO;
import remote.wise.dao.UpdateServiceAlertsDAO;
import remote.wise.log.InfoLogging.InfoLoggerClass;

//CR409-20230308-prasad
@Path("/InsertComRepOemEchnIntoXlsFile")
public class InsertComRepOemEchnIntoXlsFile {
	@GET()
	@Path("insertComRepOemEchnIntoXlsFile")
	@Produces("text/plain")
	public String UpdateServiceAlerts(){
		
	   
		Logger iLogger = InfoLoggerClass.logger;
		
		String response_msg= new InsertComRepoEnchIntoCVSFileDAO().insertIntoCSVGile();
		
		iLogger.info("Webservice Output: " + response_msg );
		return response_msg;
		
	}


}
