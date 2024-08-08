package remote.wise.service.webservice;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.DisconnectedVINSImpl;


/**
 * @author AJ20119610
 *
 */
@Path("/DisconnectedVINSFromVFRESTService")
public class DisconnectedVINSFromVFRESTService {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/updateDisconnectedVINSFromVF")
	public String updateDisconnectedVINSFromVF(@QueryParam("sourceDir") String sourceDir,@QueryParam("destinationDir") String destinationDir) {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		sourceDir = "/user/JCBLiveLink/Data/SimDiscconectedData/current_data/";
		infoLogger.info("Webservice input : "
				+ "directory location is  : "+sourceDir);
		String response = null;
		
		try {
		
			File fileLoc=new File(sourceDir);
			String[] files=fileLoc.list();
			infoLogger.info("files: " + files.toString());
			List<String> filesList=Arrays.asList(files);
			if(!filesList.isEmpty()){
				infoLogger.info("filesList: " + filesList.toString());
			DisconnectedVINSImpl implObj = new DisconnectedVINSImpl();
			response=implObj.updateDisconnectedVINSFromVF(filesList,sourceDir,destinationDir);
			}else
			{
				response="no files there for processing";
			}
			infoLogger.info("Webservice Output: " + response);
			
		} catch (Exception e) {
			response="FAILURE";
			e.printStackTrace();
			fLogger.fatal("Exception:" + e.getMessage());
		}
		return response;
		
	}

}
	
