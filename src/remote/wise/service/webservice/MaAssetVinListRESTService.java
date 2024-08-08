package remote.wise.service.webservice;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.MaAssetVinListServiceImpl;

@Path("/MaAssetVinListRESTService")
public class MaAssetVinListRESTService {
	@GET
	@Path("/getCMAAssetList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getCMAAssetList() {
		Logger iLogger = InfoLoggerClass.logger;
		List<String> response;
		
		response = new MaAssetVinListServiceImpl().getMaAssetVinList();
		iLogger.info("response from getMaAssetvinList() "+response);
		return response;
		
	}

}
