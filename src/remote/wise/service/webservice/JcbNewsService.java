package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.JcbNewsReqContract;
import remote.wise.service.datacontract.JcbNewsrespContract;
import remote.wise.service.datacontract.TestimonialFeedBackRespContract;
import remote.wise.service.implementation.JcbNewsImpl;
import remote.wise.service.implementation.TestimonialFeedBackImpl;
import remote.wise.util.CommonUtil;

@WebService(name="JcbNewsService")
public class JcbNewsService {
	@WebMethod(operationName="setJcbnews" , action="setJcbnews")
	public String setJcbnews(@WebParam(name="reqObj")JcbNewsReqContract reqObj){
		String response = "Failure";
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Customer/Dealer Id:"+reqObj.getContact_ID()+"headlines:  "+reqObj.getHeadlines()+"url:"+reqObj.getUrl());
		
		//DF20170919 @Roopa getting decoded UserId
				String UserID=new CommonUtil().getUserId(reqObj.getContact_ID());
				reqObj.setContact_ID(UserID);
				iLogger.info("Decoded userId::"+reqObj.getContact_ID());
		 
		response = new JcbNewsImpl().setJcbNews(reqObj);
		
		iLogger.info("----- Webservice Output-----");
		iLogger.info("response:"+response);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:JcbNewsService~executionTime:"+(endTime-startTime)+"~"+UserID+"~"+response);
		return response;
	}
	@WebMethod(operationName = "getJcbNews" , action = "getJcbNews")
	public List<JcbNewsrespContract> getJcbNews(){
		List<JcbNewsrespContract> resObj = null;
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		 
		resObj = new JcbNewsImpl().getJcbNews();
		
		iLogger.info("----- Webservice Output-----");
		if(resObj.size()>0)
		{
			for(int i=0;i<resObj.size();i++)
				iLogger.info("news id:"+resObj.get(i).getNews_id()+"headlines:"+resObj.get(i).getHeadlines()+"Customer ID:"+resObj.get(i).getContact_ID()+"Url:"+resObj.get(i).getUrl());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:JcbNewsService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return resObj;
	}
}
