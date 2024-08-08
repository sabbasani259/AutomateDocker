/*
 *  20230724 : CR416 : Prasanna Lakshmi : Download PT Packet Changes
 */
package remote.wise.service.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.DownloadPTPacketImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;

@Path("/LogDownloadPTPacketService")
public class DownloadPTPacket {
	
	@POST
	@Path("/getDownloadPTPacketReport")
	@Produces(MediaType.APPLICATION_JSON) 
	@Consumes(MediaType.APPLICATION_JSON)
	public List<HashMap<String, String>> getDownloadPTPacketReport(HashMap<String, Object> requestObj) throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
	
		List<HashMap<String, String>> response=null;
		
		ArrayList<String> vinList = (ArrayList<String>) requestObj.get("vinList");
		String startdate = (String)requestObj.get("startdate");
		String enddate = (String)requestObj.get("enddate");
		infoLogger.info("--------getDownloadPTPacketReport------Webservice Start------");
		infoLogger.info("getDownloadPTPacketReport vinList: " + vinList + " startdate: "+startdate+"  enddate :"+enddate);
		infoLogger.info("vinList Size :"+vinList.size());
		 ListToStringConversion str=new ListToStringConversion();
		   String SerialNumber =	str.getStringList(vinList).toString();
		   infoLogger.info("serialnumber   :"+SerialNumber);
		   infoLogger.info("length check of the vin follows   "+vinList.size());
		   
		CommonUtil util = new CommonUtil();
		if( vinList.size()>5||vinList==null || vinList.isEmpty()|| startdate==null || startdate.isEmpty()|| enddate.isEmpty()||enddate==null) {
			infoLogger.info(" Invalid data entry "+response);
			return response;
		}
		else {
		for(int i=0;i<vinList.size();i++) {
			String isValidinput1 = util.inputFieldValidation(String.valueOf(vinList.get(i)));
			if(!isValidinput1.equals("SUCCESS")){
				fLogger.info("Invalid input parameter: "+vinList.get(i));
				throw new CustomFault(isValidinput1);}
			
			if(vinList.size()>5||vinList.get(i).length()!=17||vinList.get(i).length()>17) {
				infoLogger.info(" Invalid data "+response);	
			return response;
		}
		}
		String isValidinput2 = util.inputFieldValidation(String.valueOf(startdate));
		if(!isValidinput2.equals("SUCCESS")){
			fLogger.info("Invalid input parameter: "+startdate);
			throw new CustomFault(isValidinput2);
		}
		String isValidinput3 = util.inputFieldValidation(String.valueOf(enddate));
		if(!isValidinput3.equals("SUCCESS")){
			fLogger.info("Invalid input parameter: "+enddate);
			throw new CustomFault(isValidinput3);
		}
		
		 response = new DownloadPTPacketImpl().getDownloadPTPacketReport(vinList,startdate,enddate);
		 infoLogger.info("--------getDownloadPTPacketReport------Webservice End------ : "+response);
		return response;
		
		}
	
	}
}

