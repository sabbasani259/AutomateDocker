package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ProfileCodeImpl;

@Path("/GenericProfileService")
public class GenericProfileService {
	
	@GET()
	@Produces("text/plain")
	@Path("/getAssetProfiles")
	public String getAssetProfiles(@QueryParam("loginTenancyId") String loginTenancyId,@QueryParam("roleId") Integer roleId) {
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("loginTenancyId:"+loginTenancyId+"-roleId:"+roleId);
		ProfileCodeImpl omplObj = new ProfileCodeImpl();
		String profiles=null;

		if(roleId>0 && roleId!=null ){
			profiles= omplObj.getAssetProfiles(loginTenancyId,roleId);
			
		}
		

		return profiles;
	}
	
	@GET()
	@Produces("text/plain")
	@Path("/getAssetProfileCodes")
	public String getAssetProfileCodes(@QueryParam("loginTenancyId") String loginTenancyId,@QueryParam("roleId") Integer roleId) {
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("input:loginTenancyId:"+loginTenancyId+"-roleId:"+roleId);
		ProfileCodeImpl omplObj = new ProfileCodeImpl();
		String profiles=null;

		if(roleId>0 && roleId!=null ){
			profiles= omplObj.getAssetProfileCodes(loginTenancyId,roleId);
			
		}
		

		return profiles;
	}


}
