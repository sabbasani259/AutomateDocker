package remote.wise.service.webservice;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.MonitoringParameters;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AlertGenerationImpl;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
import remote.wise.util.StaticProperties;
import remote.wise.util.XmlParser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//DF20151005 - Rajani Nagaraju - Adding a wrapper service for Alert Generation that would be called from MOOL
@WebService(name = "AlertGenerationService")
public class AlertGenerationService 
{
	@WebMethod(operationName = "generateAlerts", action = "generateAlerts")
	public String generateAlerts(@WebParam(name="payload" ) String payload)
	{
		String alertGenStatus="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		try
		{
			long startTime = System.currentTimeMillis();
			String prevLogFuelLevel=null;
			
			String[] payloadArr =  payload.split("SEP"); 
			payload = payloadArr[0];
			String prevTxnIgnitionStatus= payloadArr[1];
			String prevTxnEngineStatus= payloadArr[2];
			String prevTxnLat= payloadArr[3];
			String prevTxnLong= payloadArr[4];
			int prevLogtransactionNumber= Integer.parseInt(payloadArr[5]);
			
			//DF20160719 @Roopa Taking PrevLog fuel level which is getting added from mool side when calling AGS client
			
			if(payloadArr.length>6)
			prevLogFuelLevel=payloadArr[6];
			
			//Convert payload received as JSON String to HashMap
			HashMap<String, String> payloadMap = new Gson().fromJson(payload, new TypeToken<HashMap<String, String>>() {}.getType());
			
			iLogger.info("AGS:"+payloadMap.get("ASSET_ID")+":"+payloadMap.get("TXN_TIME")+":"+" ADS Service Input :" +
					""+payload+"SEP"+prevTxnIgnitionStatus+"SEP"+prevTxnEngineStatus+"SEP"+prevTxnLat+"SEP"+prevTxnLong+"SEP"+prevLogtransactionNumber);
			
			
			List<String> paramKey = new ArrayList<String>(payloadMap.keySet());
			String paramKeyAsString =  new ListToStringConversion().getStringList(paramKey).toString();
			
			HashMap<String,HashMap<String,String>> paramTypeNameValueMap = new HashMap<String,HashMap<String,String>>();
			
			//Build the HashMap in the format <ParameterType,<MonitoringParameter,value with Unit>> which is required by Alert Generation Impl
			try
			{
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
				if(session.getTransaction().isActive() && session.isDirty())
				{
				   	iLogger.info("Opening a new session");
				   	session = HibernateUtil.getSessionFactory().openSession();
				}
				session.beginTransaction();
				
				try
				{
					Query monParamQ = session.createQuery(" from MonitoringParameters where parameterKey in ("+paramKeyAsString+")");
					Iterator monParamItr = monParamQ.list().iterator();
					while(monParamItr.hasNext())
					{
						MonitoringParameters param = (MonitoringParameters)monParamItr.next();
						
						String value = payloadMap.get(param.getParameterKey());
						
						if(value!=null)
						{
							HashMap<String,String> paramValueMap =null;
							if(paramTypeNameValueMap.containsKey(param.getParameterTypeId().getParameterTypeName()))
								paramValueMap = paramTypeNameValueMap.get(param.getParameterTypeId().getParameterTypeName());
							else
								paramValueMap = new HashMap<String,String> ();
							
							paramValueMap.put(param.getParameterName(), value);
							
							paramTypeNameValueMap.put(param.getParameterTypeId().getParameterTypeName(), paramValueMap);
						}	
						
					}
					
					//Invoke AlertGeneration Impl 
					Timestamp txnTimestamp = null;
					try
					{
						Date txnDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(payloadMap.get("TXN_TIME"));
						txnTimestamp = new Timestamp(txnDate.getTime());
					}
					catch(Exception e)
					{
						fLogger.fatal("AGS:"+payloadMap.get("ASSET_ID")+":"+payloadMap.get("TXN_TIME")+":"+" :Exception :"+e);
					}
					
					XmlParser parser = new XmlParser();
					parser.setFwVersionNumber(payloadMap.get("FW_VER"));
					parser.setMessageId(payloadMap.get("MSG_ID"));
					parser.setSerialNumber(payloadMap.get("ASSET_ID"));
					parser.setTransactionTime(txnTimestamp);
					parser.setParamType_parametersMap(paramTypeNameValueMap);
					
					String internalBattery = payloadMap.get("INT_BAT_PERCT").replaceAll("unit Percentage", "").trim();
					new AlertGenerationImpl(parser, payloadMap.get("ASSET_ID"), payloadMap.get("TXN_TIME"), payloadMap.get("LAT"), payloadMap.get("LONG"), 
							payloadMap.get("GPS_FIX"), internalBattery, prevTxnIgnitionStatus, prevTxnEngineStatus,
							prevTxnLat, prevTxnLong, prevLogtransactionNumber, prevLogFuelLevel);
					
				}
				catch(Exception e)
				{
					alertGenStatus="FAILURE";
					fLogger.fatal("AGS:"+payloadMap.get("ASSET_ID")+":"+payloadMap.get("TXN_TIME")+":"+"Exception :"+e);
				}
				finally
				{
					if(session!=null && session.isOpen())  
						if(session.getTransaction().isActive())
						{
							session.getTransaction().commit();
						}

					if(session.isOpen())
					{
						if(session.getTransaction().isActive())
							session.flush();
						session.close();
					}
				}
				
			}
			
			catch(Exception e)
			{
				alertGenStatus="FAILURE";
				fLogger.fatal("AGS:"+payloadMap.get("ASSET_ID")+":"+payloadMap.get("TXN_TIME")+":"+"Exception in getting session:"+e);
			}
			
			long endTime = System.currentTimeMillis();
			
			iLogger.info("AGS:"+payloadMap.get("ASSET_ID")+":"+payloadMap.get("TXN_TIME")+":"+"AGS: alertGenStatus::"+alertGenStatus+"Webservice execution time in ms:"+(endTime-startTime));
						
		}
		
		catch(Exception e)
		{
			alertGenStatus="FAILURE";
			fLogger.fatal("AGS:"+payload+":"+"Exception :"+e);
		}
		
		return alertGenStatus;
	}
}
