package remote.wise.service.webservice;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.Logger;

import remote.wise.dao.InsertComRepoEnchIntoCVSFileDAO;
import remote.wise.dao.CommRepHOIntoXlsFileDAO;
import remote.wise.log.InfoLogging.InfoLoggerClass;

//Prasanna:20250519
@Path("/CommRepHOIntoXlsFile")
public class CommRepHOIntoXlsFile {
	@GET()
	@Path("insertCommRepHOIntoXlsFile")
	@Produces("text/plain")
	public String UpdateCommRepHOIntoXlsFile(){
		
	   
		Logger iLogger = InfoLoggerClass.logger;
		
		String response_msg= new CommRepHOIntoXlsFileDAO().insertIntoCSVGile();
		
		iLogger.info("Webservice Output: " + response_msg );
		return response_msg;
	}
}

