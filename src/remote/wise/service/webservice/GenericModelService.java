package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ProfileCodeImpl;

@Path("/GenericModelService")
public class GenericModelService {

	@GET()
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getModels")
	public HashMap getModels(@QueryParam("loginTenancyId") String loginTenancyId,@QueryParam("roleId") Integer roleId,@QueryParam("assetGroupIds") String assetGroupIds) {
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("input:loginTenancyId:"+loginTenancyId+"-roleId:"+roleId+"-assetGroupIds:"+assetGroupIds);
		ProfileCodeImpl omplObj = new ProfileCodeImpl();
		String assetTypes=null;
		 HashMap assetProfile=null;
				
		if(roleId>0 && roleId!=null ){
			  assetProfile = omplObj.getAssetModels(loginTenancyId,roleId,assetGroupIds);
		}
		

		return assetProfile;
	}

}
