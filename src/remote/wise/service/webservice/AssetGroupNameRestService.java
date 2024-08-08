package remote.wise.service.webservice;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AssetGroupNameListImpl;
import remote.wise.util.CommonUtil;


@Path("/AssetGroupNameRestService")
public class AssetGroupNameRestService {
	
	@GET
	@Path("getAssetGroupName")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String,String> getAssetGroupName(@QueryParam("loginId") String loginId) throws CustomFault{
		
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		
		HashMap<String,String> response = new HashMap<String,String>();
		String isValidinput=null;
		CommonUtil util = new CommonUtil();
		
		String userId="";
		if(loginId!=null){
			userId=new CommonUtil().getUserId(loginId);
			isValidinput = util.inputFieldValidation(userId.toString());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		iLogger.info("AssetGroupNameService Input : loginId "+loginId);
		response = new AssetGroupNameListImpl().getAssetGroupNameDetails(userId);
		iLogger.info("AssetGroupNameService Response : loginId "+loginId);
		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:AssetGroupNameRestService~executionTime:"+(endTime-startTime)+"~"+userId+"~");
		return response;
	}
}
