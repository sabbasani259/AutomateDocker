package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.DeleteLandmarkCategoryRespContract;
import remote.wise.service.implementation.DeleteLandmarkCategoryImpl;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;


@WebService(name = "DeleteLandmarkCategoryService")
public class DeleteLandmarkCategoryService 
{
	@WebMethod(operationName = "SetDeleteLandmarkCategory", action = "SetDeleteLandmarkCategory")
	public String setDeleteLandmarkCategory(@WebParam(name="setDeleteLandmarkCategory" )DeleteLandmarkCategoryRespContract setDeleteLandmarkCategory) throws CustomFault
	{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("DeleteLandmarkCategoryService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Landmark_Category_id:"+setDeleteLandmarkCategory.getLandmark_Category_id()+"");
		
		//DF20181008-KO369761-XSS validation of input request contract
		CommonUtil util = new CommonUtil();
		String isValidinput=null;

		isValidinput = util.inputFieldValidation(String.valueOf(setDeleteLandmarkCategory.getLandmark_Category_id()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		String response_msg= new DeleteLandmarkCategoryImpl().setDeleteLandmarkCategory(setDeleteLandmarkCategory);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("staus:"+response_msg+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:DeleteLandmarkCategoryService~executionTime:"+(endTime - startTime)+"~"+""+"~"+response_msg);
		return response_msg;
	}

}
