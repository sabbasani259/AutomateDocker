/*
 * CR428 : 20230830 : Dhiraj Kumar : Sea Ports (Landmark) Configurations
 */
package remote.wise.service.webservice;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.SetSeaportLandmarkAssetReqContract;
import remote.wise.service.implementation.LandmarkAssetImpl;
import remote.wise.util.CommonUtil;

@Path("/SeaPortLandmarkAssetService")
public class SeaPortLandmarkAssetService {

	@POST
	@Path("setLandmarkAsset")
	@Produces(MediaType.TEXT_PLAIN)
	public String setLandmarkAsset(SetSeaportLandmarkAssetReqContract reqObj) {

		String response = "FAILURE";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		long start = System.currentTimeMillis();
		try {
			iLogger.info("Webservice Input : loginId:" + reqObj.getLoginId() + ":landmarkId:" + reqObj.getLandMarkId());
			String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
			reqObj.setLoginId(UserID);
			iLogger.info("Decoded userId::"+reqObj.getLoginId());
			
			LandmarkAssetImpl impl = new LandmarkAssetImpl();
			response = impl.setSeaportLandmarkAsset(reqObj);
		} catch (Exception e) {
			fLogger.fatal("Exception occurred : " + e.getMessage());
		}
		long end = System.currentTimeMillis();
		iLogger.info("Webservice Output :"+response + " : Time taken:"+ (end-start) + "ms~~" );
		return response;

	}

}
