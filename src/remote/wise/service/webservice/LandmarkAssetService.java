package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.LandmarkAssetReqContract;
import remote.wise.service.datacontract.LandmarkAssetRespContract;
import remote.wise.service.implementation.LandmarkAssetImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;
/**
 * LandmarkAssetService will allow to get and set Asset for given Landmark
 * @author jgupta41
 *
 */
@WebService(name = "LandmarkAssetService")

public class LandmarkAssetService {
	
	/**
	 * This method gets all asset that belongs specified Landmark Id and Login Id

	 *  @param reqObj Get the assets for  a given Landmark 	  
	 * @return respObj Returns the assets attached for the landmark defined. 
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetLandmarkAsset", action = "GetLandmarkAsset")
	public List<LandmarkAssetRespContract> getLandmarkAsset(LandmarkAssetReqContract reqObj) throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("LandmarkAssetService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Serial_numbers:"+reqObj.getSerial_numbers()+","+"login_id:"+reqObj.getLogin_id()+","+"Landmark_id:"+reqObj.getLandmark_id()+ "");
		
		// DF20181017 Avinash Xavier : CSRF Token Validation ---Start---.
		String csrfToken = null;
		String pageSize = null;
		String pagenumber = null;
		int pageSiz =0 ;
		int pagenum = 0 ;
		boolean isValidCSRF = false;
		String loginId = null;
		CommonUtil util = new CommonUtil();
 
		
		if (reqObj.getLogin_id() != null) {
			if (reqObj.getLogin_id().split("\\|").length > 1) {
				pageSize = reqObj.getLogin_id().split("\\|")[3];
				pagenumber = reqObj.getLogin_id().split("\\|")[2];
				csrfToken = reqObj.getLogin_id().split("\\|")[1];
				loginId = reqObj.getLogin_id().split("\\|")[0];
				reqObj.setLogin_id(reqObj.getLogin_id().split("\\|")[0]);
			}
		}

		if (csrfToken != null) {
			isValidCSRF = util.validateANTICSRFTOKEN(loginId, csrfToken);
			iLogger.info("LandmarkAssetsService :: getLandmarkAssets ::   csrftoken isValidCSRF :: "
					+ isValidCSRF);
			if (!isValidCSRF) {
				iLogger.info("LandmarkAssetsService :: getLandmarkAssets ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			}
		}
		if (pagenumber == null ) {
			pagenum = 1;
			iLogger.info("----- pagenumber is not passing please pass-----");
			
		} else {
			if(pageSize != null){
			pageSiz = Integer.valueOf(pageSize);}
			else{
				pageSiz = 500;
			}
		
			pagenum = Integer.valueOf(pagenumber);
		}
		// DF20181017 Avinash Xavier CsrfToken Validation ---End----.
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(reqObj.getLogin_id());
				reqObj.setLogin_id(UserID);
				iLogger.info("Decoded userId::"+reqObj.getLogin_id());
				
		List<LandmarkAssetRespContract> respObj = new LandmarkAssetImpl().getLandmarkAsset(reqObj , pagenum , pageSiz);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<respObj.size(); i++){
			iLogger.info("Landmark_id:"+respObj.get(i).getLandmark_id()+","+"Serial_number:"+respObj.get(i).getSerial_number()+","+"asset_group_id:"+respObj.get(i).getAsset_group_id()+","+"Login_Id:"+respObj.get(i).getLogin_Id()+"");
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:LandmarkAssetService~executionTime:"+(endTime-startTime)+"~"+loginId+"~");
		return respObj;
	}


	/**
	 * This method sets all asset that belongs specified Landmark Id 
	 * @param setLandmarkAsset Get the landmark and set assets to the corresponding to it
	 * @return Return the status String as either Success/Failure.
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "SetLandmarkAsset", action = "SetLandmarkAsset")
	public String setLandmarkAsset(@WebParam(name="setLandmarkAsset" ) LandmarkAssetRespContract setLandmarkAsset
	)  throws CustomFault

	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("LandmarkAssetService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Landmark_id:"+setLandmarkAsset.getLandmark_id()+","+"Serial_number:"+setLandmarkAsset.getSerial_number()+","+"asset_group_id:"+setLandmarkAsset.getAsset_group_id()+","+"Login_Id:"+setLandmarkAsset.getLogin_Id()+"");
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(setLandmarkAsset.getLogin_Id());
		setLandmarkAsset.setLogin_Id(UserID);
		iLogger.info("Decoded userId::"+setLandmarkAsset.getLogin_Id());
		
		String response_msg= new LandmarkAssetImpl().setLandmarkAsset(setLandmarkAsset);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Staus:"+response_msg+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:LandmarkAssetService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response_msg);
		return response_msg;
	}
}
