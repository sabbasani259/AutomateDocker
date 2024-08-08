/**
 * CR337 : 20220721 : Dhiraj K : Property file read.
 */
package remote.wise.service.implementation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import remote.wise.dao.PopulateUnderUtilizedMachinesServiceDao;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PopulateUnderUtilizedMachinesServiceImpl {
 
	public String getUnderUtilizedMachinesFromMoolLayer(){
		List<LinkedHashMap<String, Object>> MDAOutputMap = null;
		String MDAoutput = null;
		String MDAresult = null;
		String result="failure";
		List<LinkedHashMap<String, Object>> responseList = new ArrayList<LinkedHashMap<String, Object>>();
		LinkedHashMap<String, Object> respObj = null;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		//CR337.sn
		String connIP=null;
		String connPort=null;
		Properties prop = null;
		try{
			prop = CommonUtil.getDepEnvProperties();
			connIP = prop.getProperty("MDA_ServerIP");
			connPort = prop.getProperty("MDA_ServerPort");
			iLogger.info("PopulateUnderUtilizedMachinesServiceImpl:MDAIP" + connIP + " :: MDAPort" +connPort);
		}catch(Exception e){
			fLogger.fatal("PopulateUnderUtilizedMachinesServiceImpl: " +
					"Exception in getting Server Details for MDA Layer from properties file: " +e);
		}
		//CR337.en
		try {
			//String url = "http://10.210.196.206:26020/MoolDAReports/UnderUtilizedMachinesService/getUnderUtilizedMachinesBatch?noOfDays=30&loginID=BatchUser&CountryCode=%2B91"; //CR337.o
			String url = "http://"+connIP+":"+connPort+"/MoolDAReports/UnderUtilizedMachinesService/getUnderUtilizedMachinesBatch?noOfDays=30&loginID=BatchUser&CountryCode=%2B91"; //CR337.n

			URL MDAUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) MDAUrl
					.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
				fLogger.fatal("issue with connection MOOLDA");
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			iLogger.info("step1 completed connection to Mool layer successfull");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			while ((MDAoutput = br.readLine()) != null) {
				MDAresult = MDAoutput;
			}
			MDAOutputMap = new Gson().fromJson(MDAresult,
					new TypeToken<ArrayList<LinkedHashMap<String, Object>>>() {
					}.getType());
			iLogger.info("step 2 completed,sucessfully parsed the returned string to map");
			System.out.println("MDAOutputMap" +MDAOutputMap);
			for (HashMap<String, Object> map : MDAOutputMap) {
				respObj = new LinkedHashMap<String, Object>();
				for (Entry<String, Object> mapEntry : map.entrySet()) {
					
						if (mapEntry.getKey().toString()
								.equalsIgnoreCase("AssetID")) {
							respObj.put("AssetID", mapEntry.getValue());
						}
						if (mapEntry.getKey().toString().equalsIgnoreCase("PeriodHMR")) {
							respObj.put("PeriodHMR", mapEntry.getValue());
						}
						if (mapEntry.getKey().toString().equalsIgnoreCase("AvgUtilizationPerct")) {
							respObj.put("AvgUtilizationPerct", mapEntry.getValue());
						}
					} 
				responseList.add(respObj);
				}
				
			PopulateUnderUtilizedMachinesServiceDao.updateDB(responseList);
			conn.disconnect();
			result="Success";
		} catch (Exception e) {
			e.printStackTrace();
			// fLogger.fatal("Error occured while connecting to Mongo DB "+e.getMessage());
		}
		return result;
	}
}
