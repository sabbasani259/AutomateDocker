package remote.wise.service.webservice;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;


import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.ServiceDueOverDueReqContract;
import remote.wise.service.datacontract.ServiceDueOverDueRespContract;
import remote.wise.service.implementation.ServiceDueOverDueImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
//import remote.wise.service.implementation.ServiceHistoryImpl;
//import remote.wise.util.WiseLogger;

@WebService(name="ServiceDueOverDueService")
public class ServiceDueOverDueService {
		
	@WebMethod(operationName = "GetServiceDueOverDueDetails", action = "GetServiceDueOverDueDetails")
	public ServiceDueOverDueRespContract getServiceDueoverDue(@WebParam(name="reqObj")ServiceDueOverDueReqContract reqObj)  throws CustomFault{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
				//WiseLogger infoLogger = WiseLogger.getLogger("ServiceDueOverDueService:","info");
				Logger iLogger = InfoLoggerClass.logger;
				//Logger infoLogger = Logger.getLogger("infoLogger");
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
				String startDate = sdf.format(cal.getTime());
				iLogger.info("Current Startdate: "+startDate);
				long startTime = System.currentTimeMillis();
				iLogger.info("---- Webservice Input ------");
				iLogger.info("flag :"+reqObj.isFlag()+"LoginId:"+reqObj.getLoginId()+ "Period:"+reqObj.getPeriod()+ "TenancyIdList :"+reqObj.getTenancyIdList()+ "AssetGroupIdList :"+reqObj.getAssetGroupIdList()+ "AssetTypeIdList :"+reqObj.getAssetTypeIdList()+ "customAssetGroupIdList :"+reqObj.getCustomAssetGroupIdList()
						+" ActiveAlerts:"+reqObj.isActiveAlerts());
				
				//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(reqObj.getLoginId());
				reqObj.setLoginId(UserID);
				iLogger.info("Decoded userId::"+reqObj.getLoginId());
				
				//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

				if(reqObj.getLoginTenancyIdList()!=null && reqObj.getLoginTenancyIdList().size()>0){
					reqObj.setLoginTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getLoginTenancyIdList()));
				}
				
				if(reqObj.getTenancyIdList()!=null && reqObj.getTenancyIdList().size()>0){
					reqObj.setTenancyIdList(new DateUtil().getLinkedTenancyListForTheTenancy(reqObj.getTenancyIdList()));
				}
				
				ServiceDueOverDueRespContract respObj = new ServiceDueOverDueImpl().getServiceDueOverdueDetails(reqObj);
				iLogger.info("----- Webservice Output-----");
				iLogger.info("ServiceDueCount:"+respObj.getServiceDueCount()+",  "+"ServiceOverDueCount:"+respObj.getServiceOverDueCount());
				Calendar cal1 = Calendar.getInstance();
				SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
				String endDate = sdf1.format(cal1.getTime());
				iLogger.info("Current Enddate: "+endDate);
				long endTime=System.currentTimeMillis();
				iLogger.info("serviceName:ServiceDueOverDueService~executionTime:"+(endTime-startTime)+"~"+UserID+"~");
				return respObj;
			}
		}
