package remote.wise.service.webservice;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.ModelCodeResponseContract;
import remote.wise.service.implementation.ProfileCodeImpl;

@Path("/GenericModelCodeService")
public class GenericModelCodeService {


	@GET()
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getModels")
	public HashMap getModels(@QueryParam("loginTenancyId") String loginTenancyId,@QueryParam("roleId") Integer roleId,@QueryParam("assetGroupIds") String assetGroupIds) {
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("input:loginTenancyId:"+loginTenancyId+"-roleId:"+roleId+"-assetGroupIds:"+assetGroupIds);
		ProfileCodeImpl omplObj = new ProfileCodeImpl();
		String assetTypes=null;
		 HashMap modelMap=null;
		 ModelCodeResponseContract respObj=null;
		if(roleId>0 && roleId!=null ){
			modelMap = omplObj.getModels(loginTenancyId,roleId,assetGroupIds);
		}
		

		return modelMap;
	}
	@GET()
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getModelCodes")
	public HashMap getModelCodes(@QueryParam("loginTenancyId") String loginTenancyId,@QueryParam("roleId") Integer roleId,@QueryParam("assetGroupCodes") String assetGroupCodes) {
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("input:loginTenancyId:"+loginTenancyId+"-roleId:"+roleId+"-assetGroupCodes:"+assetGroupCodes);
		ProfileCodeImpl omplObj = new ProfileCodeImpl();
		String assetTypes=null;
		 HashMap modelMap=null;
		 ModelCodeResponseContract respObj=null;
		if(roleId>0 && roleId!=null ){
			modelMap = omplObj.getModelCodes(loginTenancyId,roleId,assetGroupCodes);
		}
		

		return modelMap;
	}
	
	//CR519 :20250403: Sai Divya : DTC Changes Phase 2.sn
	@GET()
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getBSVModels")
	public HashMap getBSVModels(@QueryParam("loginTenancyId") String loginTenancyId,
			@QueryParam("roleId") Integer roleId, @QueryParam("assetGroupIds") String assetGroupIds,
			@QueryParam("isBSV") String isBSV) {
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info(
				"input:loginTenancyId:" + loginTenancyId + "-roleId:" + roleId + "-assetGroupIds:" + assetGroupIds);
		ProfileCodeImpl omplObj = new ProfileCodeImpl();
		String assetTypes = null;
		HashMap modelMap = null;
		ModelCodeResponseContract respObj = null;
		if (roleId > 0 && roleId != null) {
			modelMap = omplObj.getBSVModels(loginTenancyId, roleId, assetGroupIds, isBSV);
		}

		return modelMap;
	}
	
	@GET()
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getBSVModelCodes")
	public HashMap getBSVModelCodes(@QueryParam("loginTenancyId") String loginTenancyId,
			@QueryParam("roleId") Integer roleId, @QueryParam("assetGroupCodes") String assetGroupCodes,@QueryParam("isBSV") String isBSV) {
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info(
				"input:loginTenancyId:" + loginTenancyId + "-roleId:" + roleId + "-assetGroupCodes:" + assetGroupCodes);
		ProfileCodeImpl omplObj = new ProfileCodeImpl();
		String assetTypes = null;
		HashMap modelMap = null;
		ModelCodeResponseContract respObj = null;
		if (roleId > 0 && roleId != null) {
			modelMap = omplObj.getBSVModelCodes(loginTenancyId, roleId, assetGroupCodes,isBSV);
		}

		return modelMap;
	}
	//CR519 :20250403: Sai Divya : DTC Changes Phase 2.en
}
