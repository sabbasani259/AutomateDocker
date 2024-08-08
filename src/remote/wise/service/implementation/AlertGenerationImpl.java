package remote.wise.service.implementation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
//import org.w3c.dom.NodeList;

import remote.wise.businessentity.AlertPacketDetailsEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetEventEntity;
import remote.wise.businessentity.AssetExtendedDetailsEntity;
import remote.wise.businessentity.EventEntity;
import remote.wise.businessentity.EventTypeEntity;
import remote.wise.businessentity.LandmarkAssetEntity;
import remote.wise.businessentity.LandmarkEntity;
import remote.wise.businessentity.LandmarkLogDetailsEntity;
import remote.wise.businessentity.MonitoringParameters;
import remote.wise.businessentity.RecordTypeEntity;
import remote.wise.businessentity.ServiceScheduleEntity;
import remote.wise.businessentity.TenancyEntity;
import remote.wise.businessentity.UomMasterEntity;
import remote.wise.businessobject.AssetMonitoringDetailsBO;
import remote.wise.businessobject.EventDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.AssetServiceScheduleGetReqContract;
import remote.wise.service.datacontract.AssetServiceScheduleGetRespContract;
import remote.wise.util.Geofencing;

import remote.wise.util.FwVersion;
import remote.wise.util.HibernateUtil;
import remote.wise.util.IstGmtTimeConversion;
import remote.wise.util.ListToStringConversion;
import remote.wise.util.ParameterUOMmanagement;
import remote.wise.util.StaticProperties;
//import remote.wise.util.WiseLogger;
import remote.wise.util.XmlParser;

/** Implementation class that handles Alert Generation
 * @author Rajani Nagaraju
 *
 */
public class AlertGenerationImpl implements Runnable
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger fatalError = WiseLogger.getLogger("AlertGeneration - Impl:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("AlertGeneration - Impl:","info");*/

	Thread t;
	XmlParser parser;
	String serialNumber,transactionTime,currentLat,currentLong,currentGPSFix,currentInternalBatteryLevel,prevTxnIgnitionStatus,prevTxnEngnieStatus;
	String prevTxnLat,prevTxnLong;
	int prevLogtransactionNumber;
	String prevLogFuelLevel;
	

	/** This method is to determine the Firmware and Application Alerts
	 * @param parser Parsed XML remote monitoring data
	 * @throws CustomFault
	 */

	public AlertGenerationImpl()
	{

	}
	
	public AlertGenerationImpl(XmlParser parser)
	{
	/*	t = new Thread(this, "Demo Thread");
		this.parser= parser;
		// Start the thread
		t.start();*/
	}
	//DF20160719 @Roopa Taking PrevLog fuel level which is getting added from mool side when calling AGS client
	public AlertGenerationImpl(XmlParser parser, String serialNumber, String transactionTime, String currentLat, String currentLong, 
			String currentGPSFix, String currentInternalBatteryLevel, String prevTxnIgnitionStatus, String prevTxnEngnieStatus,
			String prevTxnLat, String prevTxnLong, int prevLogtransactionNumber, String prevLogFuelLevel)
	{
		t = new Thread(this, "AGS Thread");
		this.parser= parser;
		this.serialNumber=serialNumber;
		this.transactionTime=transactionTime;
		this.currentLat=currentLat;
		this.currentLong=currentLong;
		this.currentGPSFix=currentGPSFix;
		this.currentInternalBatteryLevel=currentInternalBatteryLevel;
		this.prevTxnIgnitionStatus=prevTxnIgnitionStatus;
		this.prevTxnEngnieStatus=prevTxnEngnieStatus;
		this.prevTxnLat=prevTxnLat;
		this.prevTxnLong=prevTxnLong;
		this.prevLogtransactionNumber=prevLogtransactionNumber;
		this.prevLogFuelLevel=prevLogFuelLevel;
		
		// Start the thread
		t.start();
		/*generateAlertsNew(this.parser,this.serialNumber, this.transactionTime, this.currentLat, this.currentLong, 
				this.currentGPSFix, this.currentInternalBatteryLevel, this.prevTxnIgnitionStatus, this.prevTxnEngnieStatus,
				this.prevTxnLat, this.prevTxnLong, this.prevLogtransactionNumber);*/

	}

	public void run() 
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		try
		{
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
		
			try 
			{
				//----------------------------- Insert the Input data into AlertPacketDetails table
				//Convert XMLParser Obj to Byte array to store the same as BLOB in DB
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = null;
				int alertPacketDetailsId =0;
				try
				{ 
					 DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					 out = new ObjectOutputStream(bos); 
					 out.writeObject(this.parser);
					 byte[] xmlParserObj = bos.toByteArray();
					  
					 AlertPacketDetailsEntity packetDetailsObj = new AlertPacketDetailsEntity();
					 packetDetailsObj.setSerialNumber(this.serialNumber);
					 packetDetailsObj.setTransactionTime(this.transactionTime);
					 packetDetailsObj.setCreatedTimestamp(df.format(new Date()));
					 packetDetailsObj.setParserObject(xmlParserObj);
					 
					 //DF20160822 @Roopa insering prevlogfuel into alert details table, which will be used in alert re processing
					 
					 packetDetailsObj.setOtherInfo(this.currentLat+"|"+this.currentLong+"|"+this.currentGPSFix+"|"+this.currentInternalBatteryLevel
							 +"|"+this.prevTxnIgnitionStatus+"|"+this.prevTxnEngnieStatus+"|"+this.prevTxnLat+"|"+this.prevTxnLong+"|"+this.prevLogtransactionNumber+"|"+this.prevLogFuelLevel);
					 
					 session.save(packetDetailsObj);
					 
					 alertPacketDetailsId = packetDetailsObj.getPacketDetailsId();
					 
					 iLogger.info("AGS:"+this.serialNumber+":"+this.transactionTime+": Record inserted to alert packet details with ID: "+alertPacketDetailsId);
					 
				}
				catch(Exception e)
				{
					fLogger.fatal("AGS:"+this.serialNumber+":"+this.transactionTime+": Exception in Inserting to AlertPacketDetails:"+e);
				}
				finally
				{
					if (out != null) 
					      out.close();
					 bos.close();
					 try
						{
							if(session!=null && session.isOpen())  
								if(session.getTransaction().isActive())
								{
									session.getTransaction().commit();
								}
						}
						catch(Exception e)
						{
							fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+" :Exception :"+e);
						
						}
						if(session.isOpen())
						{
							if(session.getTransaction().isActive())
								session.flush();
							session.close();
						}
					 
				}
			
			
				String alertGenerationStatus = generateAlertsNew(this.parser,this.serialNumber, this.transactionTime, this.currentLat, this.currentLong, 
												this.currentGPSFix, this.currentInternalBatteryLevel, this.prevTxnIgnitionStatus, this.prevTxnEngnieStatus,
												this.prevTxnLat, this.prevTxnLong, this.prevLogtransactionNumber, this.prevLogFuelLevel);
				
				iLogger.info("AGS:"+this.serialNumber+":"+this.transactionTime+":alertGenerationStatus::"+alertGenerationStatus);
				
				
				//--------------- If the alert is generated successfully, delete the record from AlertPacketDetails table
			/*	if(! (session.isOpen() ))
	            {
	                        session = HibernateUtil.getSessionFactory().getCurrentSession();
	                        session.getTransaction().begin();
					session = HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction();
	            }*/
				
			    
			    
				if(alertGenerationStatus.equalsIgnoreCase("SUCCESS"))
				{
					
					iLogger.info("AGS:"+this.serialNumber+":"+this.transactionTime+":Inside AlertPacketDetail deletion::");
					
					session= HibernateUtil.getSessionFactory().openSession();
				    
				    session.beginTransaction();
					
					Query deletePktDetailsQ = session.createQuery(" delete from AlertPacketDetailsEntity where packetDetailsId="+alertPacketDetailsId);
					int rowsAffected = deletePktDetailsQ.executeUpdate();
					if(rowsAffected>0)
					{
						iLogger.info("AGS:"+this.serialNumber+":"+this.transactionTime+": Record deleted from alert packet details with ID: "+alertPacketDetailsId);
					}
				}
			}
			
			catch(Exception e)
			{
				fLogger.fatal("AGS:"+this.serialNumber+":"+this.transactionTime+"Exception :"+e);
				Writer result = new StringWriter();
				PrintWriter printWriter = new PrintWriter(result);
				e.printStackTrace(printWriter);
				String err = result.toString();
				fLogger.fatal("AGS:"+this.serialNumber+":"+this.transactionTime+"Exception trace: "+err);
				try 
				{
					printWriter.close();
					result.close();
				} 

				catch (IOException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			finally
			{
				try
				{
					if(session!=null && session.isOpen())  
						if(session.getTransaction().isActive())
						{
							session.getTransaction().commit();
						}
				}
				catch(Exception e)
				{
					fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+" :Exception :"+e);
				}
	
				if(session!=null && session.isOpen())
				{
					if(session.getTransaction().isActive())
						session.flush();
					session.close();
				}
			}
		}

		catch (Exception e) 
		{
			fLogger.fatal("AGS:"+this.serialNumber+":"+this.transactionTime+"Child Interrupted");
			fLogger.fatal("AGS:"+this.serialNumber+":"+this.transactionTime+"Exception :"+e);
		}

	}


	//DefectID 955 - Rajani Nagaraju - 20130715 - Landmark Alerts to be generated only based on Log Packets
	public String generateAlerts(XmlParser parser)
	{
		//Logger bLogger = BusinessErrorLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+" Inside Child Thread");
		/*try {
			Thread.currentThread().sleep(6000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		infoLogger.info(" Resume Child Thread");*/
		
		String serialNumber = parser.getSerialNumber().substring(3);
		Timestamp transactionTime = parser.getTransactionTime(); 
		HashMap<String, HashMap<String,String>> hashMap = parser.getParamType_parametersMap();
		String receivedMessgeId= parser.getMessageId();

		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{
			EventDetailsBO eventDetails = new EventDetailsBO();

			int validSerialNum = 0;
			AssetEntity asset=null;

			//Before all check for the valid serial number
			Query serialNumberCheck = session.createQuery("from AssetEntity where serial_number='"+serialNumber+"'");
			Iterator serialNumItr = serialNumberCheck.list().iterator();
			while (serialNumItr.hasNext())
			{
				asset = (AssetEntity) serialNumItr.next();
				validSerialNum = 1;
			}

			if(validSerialNum==0)
			{
				return "SUCCESS";
			}


			//Step 1: Check for Event parameters to generate Alerts directly
			String eventParameter = null;
			String CumulativeOperatingHours = null;
			String ServiceAlerts = null;
			String OperatingHourEvent = null;
			String Location = null;
			String Latitude = null;
			String Longitude = null;
			String LandmarkArrival = null;
			String LandmarkDeparture = null;
			String LandmarkEventTypeName=null;
			String ExternalBatteryRemovedEventId=null;
			String InternalBatteryLowEventId=null;
			String InternalBatteryCharge =null;
			String LogParameters=null;
			String LowFuelLevel=null;
			String FuelLevel=null;
			//Added by Rajani Nagaraju - 20130715 
			String eventRecordType = null;
			String logRecordType = null;
			//Added by Rajani Nagaraju - 20130730 - To Handle Hello Packet - DefectID:1017 
			String HelloPacketParamName =null;
			//DefectID: CR20131022 - Rajani Nagaraju - To Close Tow away Alert with next IgnitionON packet
			String Ignition_ON=null;
			String TowAwayEventName=null;
			String LowFuelLevelCritical=null;
			//DefectID: DF20131031 - Rajani Nagaraju - Capture GPX_Fix to decide the SMS content for Tow away alert
			String FirmwareParameters=null;
			String GPSFix=null;
			//DF20140502 - Rajani Nagaraju - Generate/ Closure of Fuel theft Alert
			String FuelTheftEventID=null;
			//DefectID:20140811 - Suprava Nayak - Compare Firmware Version Number
			String versionNum = null;

			//Properties prop = new Properties();
			//prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			eventParameter ="EventParameters";
			CumulativeOperatingHours = "CumulativeOperatingHours";
			ServiceAlerts ="Service";
			OperatingHourEvent ="Usage outside Operating Hours";
			Location = "Location";
			Latitude = "Latitude";
			Longitude ="Longitude";
			LandmarkArrival = "Arrival";
			LandmarkDeparture = "Departure";
			ExternalBatteryRemovedEventId =  "20";
			InternalBatteryLowEventId =  "6";
			InternalBatteryCharge = "InternalBatteryCharge";
			LogParameters = "LogParameters";
			LowFuelLevel = "Low Fuel Level";
			//DefectId: DF20131023 - Rajani Nagaraju - With diff event names as 'Low Fuel Level' and 'Low Fuel Level Critical', event was getting generated twice
			LowFuelLevelCritical = "Low Fuel Level Critical";
			FuelLevel = "FuelLevel";
			//Added by Rajani Nagaraju - 20130715 - DefectID 955
			eventRecordType = "Event Packet";
			logRecordType = "Log Packet";
			//Added by Rajani Nagaraju - 20130730 - To Handle Hello Packet - 1017 
			HelloPacketParamName = "HELLO";
			//DefectID: CR20131022 - Rajani Nagaraju - To Close Tow away Alert with next IgnitionON packet
			Ignition_ON= "IGNITION_ON";
			TowAwayEventName = "Intrusion Detected/Possible Tow-away";
			//DefectID: DF20131031 - Rajani Nagaraju - Capture GPX_Fix to decide the SMS content for Tow away alert
			FirmwareParameters = "GPSFix";
			GPSFix = "GPSFix";
			//DF20140502 - Rajani Nagaraju - Generate/ Closure of Fuel theft Alert
			FuelTheftEventID = "25";			
			//DefectID:20140811 - Suprava Nayak - Compare Firmware Version Number
			
			/*if(prop.getProperty("deployenvironment").equalsIgnoreCase("PROD")){
				versionNum = prop.getProperty("VersionNumberProd");
			}else{
				versionNum = prop.getProperty("VersionNumberSIT");
			}*/
			
			versionNum = "7.0.0";
			//DF20140502 - Rajani Nagaraju - Generate/ Closure of Fuel theft Alert
			List<EventEntity> lowFuelEventList = new LinkedList<EventEntity>();
			List<EventEntity> newlowFuelEventList = new LinkedList<EventEntity>();


			//get the Latitude and Longitude
			String latValue = null;
			String longValue = null;
			if(hashMap.containsKey(Location))
			{
				HashMap<String, String> paramValuesMap = (HashMap<String, String>) hashMap.get(Location);
				for(int i=0; i<paramValuesMap.size(); i++)
				{
					if( ( (String)paramValuesMap.keySet().toArray()[i] ).equalsIgnoreCase(Latitude) )
					{
						latValue = paramValuesMap.get((String)paramValuesMap.keySet().toArray()[i]);

						//convert to the valid Latitude value
						double paramValueInDouble = Double.valueOf(latValue.replace('N', ' ').replace('E', ' ').trim());
						Double convertedValueInDouble = (((paramValueInDouble/100)%1)*2+(paramValueInDouble/100)*3)/3;
						latValue = convertedValueInDouble.toString();
					}
					if( ( (String)paramValuesMap.keySet().toArray()[i] ).equalsIgnoreCase(Longitude) )
					{
						longValue = paramValuesMap.get((String)paramValuesMap.keySet().toArray()[i]);

						//convert to the valid longitude value
						double paramValueInDouble = Double.valueOf(longValue.replace('N', ' ').replace('E', ' ').trim());
						Double convertedValueInDouble = (((paramValueInDouble/100)%1)*2+(paramValueInDouble/100)*3)/3;
						longValue = convertedValueInDouble.toString();
					}
				}
			}

			//DefectID: DF20131031 - Rajani Nagaraju - Capture GPS Fix value for deciding the SMS content for Tow-away alert
			String currentGPSfix =null;
			if(hashMap.containsKey(FirmwareParameters))
			{
				HashMap<String, String> paramValuesMap = (HashMap<String, String>) hashMap.get(FirmwareParameters);
				for(int i=0; i<paramValuesMap.size(); i++)
				{
					if( ( (String)paramValuesMap.keySet().toArray()[i] ).equalsIgnoreCase(GPSFix) )
					{
						currentGPSfix = paramValuesMap.get((String)paramValuesMap.keySet().toArray()[i]);
					}
				}
			}

			//DefectID: CR20131022 - Rajani Nagaraju - To Close Tow away Alert with next IgnitionON packet
			if(hashMap.containsKey(eventParameter))
			{
				HashMap<String, String> paramValues = (HashMap<String, String>) hashMap.get(eventParameter);
				if(paramValues.containsKey(Ignition_ON))
				{
					String ignitionStatus = paramValues.get(Ignition_ON);
					if(ignitionStatus.equalsIgnoreCase("1"))
					{
						//Check whether any active tow away alert exists for the VIN. If so, close the tow away alert
						Query towAwayQuery = session.createQuery(" select a " +
								" from AssetEventEntity a join a.eventId b where a.serialNumber='"+serialNumber+"'" +
								" and b.eventName like '"+TowAwayEventName+"'" +
						" and a.activeStatus=1");
						Iterator towAwayItr = towAwayQuery.list().iterator();
						while(towAwayItr.hasNext())
						{
							AssetEventEntity assetEvent = (AssetEventEntity)towAwayItr.next();
							assetEvent.setActiveStatus(0);
							assetEvent.setEventClosedTime(transactionTime);
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+"  :"+"Close Tow-away alert");
							session.update(assetEvent);
						}
					}
				}

			}

			List<String> eventParameterList = new LinkedList<String>();
			HashMap<EventEntity, String> eventIdValueMap = new HashMap<EventEntity, String>();

			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			if(hashMap.containsKey(eventParameter))
			{
				iLogger.info(serialNumber+":"+transactionTime+"  :"+"---------------- Health Alerts-------------------");
				HashMap<String, String> paramValues = (HashMap<String, String>) hashMap.get(eventParameter);
				for(int i=0; i<paramValues.size(); i++)
				{
					eventParameterList.add((String)paramValues.keySet().toArray()[i]);
				}


				//get the list of event Ids corresponding to this parameter
				Query query1 = session.createQuery("select a.EventId,c.parameterName from EventCondition a, ConditionsEntity b, MonitoringParameters c" +
				" where a.ConditionId = b.ConditionId and b.ParameterId = c.parameterId and c.parameterName in (:list)").setParameterList("list", eventParameterList);
				Iterator itr1 = query1.list().iterator();
				Object[] result1 = null;
				//List<EventEntity> lowFuelEventList = new LinkedList<EventEntity>();
				Query existsQuery=null;int eventID=0;
				while(itr1.hasNext())
				{
					result1 = (Object[]) itr1.next();
					EventEntity eventEntity = (EventEntity)result1[0];
					String paramValue = result1[1].toString();
					if(eventEntity!=null){
						eventID = eventEntity.getEventId();
					}
					//					Keerthi : 07/02/14
					//					check whether asset event table has an entry with same PIN, event_generated_time,event id,active status=1.
					//					if it has, don't insert. continue with another iteration
					
					//DF20140511 - Rajani Nagaraju - Supress Multiple Alert Generation if the same pkt is received multiple times
					if( (paramValues.get(paramValue).equals("1")) && ( eventEntity.getEventId()==16 || eventEntity.getEventId()==17) ) 
					//DF20140511 - Rajani Nagaraju - Supress Multiple Alert Generation if the same pkt is received multiple times
					{
						existsQuery = session.createQuery("SELECT ae.serialNumber" +
								"	FROM AssetEventEntity ae" +
								" WHERE ae.serialNumber='"+serialNumber+"' " +
								" AND ae.eventGeneratedTime = '"+transactionTime+"'" +
								" AND ae.eventId in (16,17)"+
						" AND ae.activeStatus=1");
						if(existsQuery.list().size()>0){
							iLogger.info("serialNumber "+serialNumber+ " transactionTime "+ transactionTime +" eventID "+eventID +" with active status 1 already exists");
							//System.out.println("Continue");
							continue;
						}
					}
					
					
					else if(paramValues.get(paramValue).equals("1")){

						existsQuery = session.createQuery("SELECT ae.serialNumber" +
								"	FROM AssetEventEntity ae" +
								" WHERE ae.serialNumber='"+serialNumber+"' " +
								" AND ae.eventGeneratedTime = '"+transactionTime+"'" +
								" AND ae.eventId="+eventID+
						" AND ae.activeStatus=1");
						if(existsQuery.list().size()>0){
							iLogger.info("serialNumber "+serialNumber+ " transactionTime "+ transactionTime +" eventID "+eventID +" with active status 1 already exists");
							continue;
						}
					}
					//*********************** generate the Alert for Low Internal Battery Charge *******************************
					HashMap<String, String> logParamValuesMap = (HashMap<String, String>) hashMap.get(LogParameters);
					String internalBatteryValue = logParamValuesMap.get(InternalBatteryCharge);

					//get the converted UOM value of internal battery charge
					ParameterUOMmanagement getParamValue = new ParameterUOMmanagement();
					String internalBatteryVal = getParamValue.getParameterValue(internalBatteryValue);
					double internalBattery = Double.parseDouble(internalBatteryVal);


					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}

					//Get the eventId for InternalBatteryLow
					int eventId = Integer.parseInt(InternalBatteryLowEventId);
					//get the eventEntity for Internal Battery low
					Query query3= session.createQuery(" from EventEntity where eventId="+eventId);
					Iterator itr3 = query3.list().iterator();
					EventEntity event =null;
					while(itr3.hasNext())
					{
						event = (EventEntity)itr3.next();

					}


					if( (eventEntity.getEventId()== (Integer.parseInt(ExternalBatteryRemovedEventId)) ) && (paramValues.get(paramValue).equals("1"))
							&& (internalBattery <= 75) )
					{
						iLogger.info(serialNumber+":"+transactionTime+"  :"+"External Battery Removed + Internal Battery Low Alert ");
						eventIdValueMap.put(event, internalBatteryVal);

					}



					if(internalBattery>75)
					{
						//Nullify the Internal Battery Low event if the internal battery charge is greater than 75
						Query internalBatteryUpdate =  session.createQuery("update AssetEventEntity set activeStatus=0, eventClosedTime= '"+transactionTime+"' " +
								" where eventId="+event.getEventId()+" and" +
								" serialNumber ='"+serialNumber+"' and activeStatus=1 ");
						int rowsAffected = internalBatteryUpdate.executeUpdate();
						iLogger.info(serialNumber+":"+transactionTime+"  :"+"Nullify Internal Battery Low Alert if Present");
					}



					//***************************************** Nullify the alert if the problem is resolved **********************************
					if(paramValues.get(paramValue).equals("0"))
					{
						Query query2 = session.createQuery("update AssetEventEntity set activeStatus=0, eventClosedTime= '"+transactionTime+"' " +
								" where eventId="+eventEntity.getEventId()+" and" +
								" serialNumber ='"+serialNumber+"' and activeStatus=1 ");
						int rowsAffected = query2.executeUpdate();
						iLogger.info(serialNumber+":"+transactionTime+"  :"+"Nullify the Alert: "+eventEntity.getEventName());

						//DF20140605 - Rajani Nagaraju - Adding Condition for Low Fuel Event
						if( (eventEntity.getEventName().equalsIgnoreCase(LowFuelLevel)) || (eventEntity.getEventName().equalsIgnoreCase(LowFuelLevelCritical)) )
							newlowFuelEventList.add(eventEntity);

					}


					//********************************* Generate an Alert for Health Alerts ***************************************
					else
					{
						//DefectId: DF20131023 - Rajani Nagaraju - With diff event names as 'Low Fuel Level' and 'Low Fuel Level Critical', event was getting generated twice
						if( (eventEntity.getEventName().equalsIgnoreCase(LowFuelLevel)) || (eventEntity.getEventName().equalsIgnoreCase(LowFuelLevelCritical)) )
						{
							lowFuelEventList.add(eventEntity);
							newlowFuelEventList.add(eventEntity);
						}

						else
						{
							eventIdValueMap.put(eventEntity, paramValues.get(paramValue));
						}
					}

				}


				if(lowFuelEventList.size()>0)
				{
					//get the current fuelLevel in volts
					HashMap<String, String> logParamValuesMap = (HashMap<String, String>) hashMap.get(LogParameters);
					String FuelLevelValue = logParamValuesMap.get(FuelLevel);

					//get the converted UOM value of Fuel Level
					ParameterUOMmanagement getParamValue = new ParameterUOMmanagement();
					String fuelLevel = getParamValue.getParameterValue(FuelLevelValue);
					double fuelLevelInDouble = Double.parseDouble(fuelLevel);

					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}

					int currentThreshold=100;
					EventEntity lowFuelEvent=null;

					int present =0;

					//DefectId: DF20131023 - Rajani Nagaraju - With diff event names as 'Low Fuel Level' and 'Low Fuel Level Critical', event was getting generated twice
					EventEntity fuelEventToGenerate =null;
					for(int i=0; i<lowFuelEventList.size(); i++)
					{
						EventEntity event = (EventEntity) lowFuelEventList.get(i);
						if( (fuelLevelInDouble<=event.getFrequency()) && (event.getFrequency()<=currentThreshold) )
						{
							present =1;
							lowFuelEvent = event;
							currentThreshold=event.getFrequency();
						}
					}

					if(present==1)
					{
						eventIdValueMap.put(lowFuelEvent, fuelLevel);
						fuelEventToGenerate = lowFuelEvent;
						iLogger.info(serialNumber+":"+transactionTime+"  :"+" Low Fuel Event Generation - Yellow Alert");
					}

					//DF20140511 - Rajani Nagaraju - Supress Multiple Alert Generation if the same pkt is received multiple times
					/*else
					{
						EventEntity redEvent = null;
						for(int i=0; i<lowFuelEventList.size(); i++)
						{
							if(lowFuelEventList.get(i).getEventSeverity().equalsIgnoreCase("RED"))
							{
								redEvent = lowFuelEventList.get(i);
							}
						}

						infoLogger.info(serialNumber+":"+transactionTime+"  :"+" Low Fuel Event Generation - Red Alert");
						eventIdValueMap.put(redEvent, fuelLevel);
						fuelEventToGenerate = redEvent;
					}*/

					//DefectID: - Rajani Nagaraju - 20130918
					//If it is the RED Low fuel event to be generated, nullify the Yellow Low fuel level alert if present
					//DefectId: DF20131023 - Rajani Nagaraju - With diff event names as 'Low Fuel Level' and 'Low Fuel Level Critical', event was getting generated twice
					for(int i=0; i<lowFuelEventList.size(); i++)
					{	
						if( (fuelEventToGenerate!=null) && (lowFuelEventList.get(i).equals(fuelEventToGenerate)) )
						{
							if(lowFuelEventList.get(i).getEventSeverity().equalsIgnoreCase("RED"))
							{
								int yellowFuelEventID =0;
								Query fuelLevelQuery = session.createQuery(" from EventEntity where eventName like '"+LowFuelLevel+"%' and " +
								" eventSeverity like 'YELLOW'");
								Iterator fuelLevelItr = fuelLevelQuery.list().iterator();
								while(fuelLevelItr.hasNext())
								{
									EventEntity event = (EventEntity)fuelLevelItr.next();
									yellowFuelEventID = event.getEventId();
								}

								//Nullify the alert if present
								Query fuelUpdateQuery = session.createQuery(" update AssetEventEntity set activeStatus=0, eventClosedTime= '"+transactionTime+"' " +
										" where eventId="+yellowFuelEventID+" and" +
										" serialNumber ='"+serialNumber+"' and activeStatus=1 ");
								int rowsAffected = fuelUpdateQuery.executeUpdate();
								iLogger.info(serialNumber+":"+transactionTime+"  :"+" Close the previous Yellow Low Fuel Event");
							}

							else if(lowFuelEventList.get(i).getEventSeverity().equalsIgnoreCase("YELLOW"))
							{
								int redFuelEventID =0;
								Query fuelLevelQuery = session.createQuery(" from EventEntity where eventName like '"+LowFuelLevel+"%' and " +
								" eventSeverity like 'RED'");
								Iterator fuelLevelItr = fuelLevelQuery.list().iterator();
								while(fuelLevelItr.hasNext())
								{
									EventEntity event = (EventEntity)fuelLevelItr.next();
									redFuelEventID = event.getEventId();
								}

								//Nullify the alert if present
								Query fuelUpdateQuery = session.createQuery(" update AssetEventEntity set activeStatus=0, eventClosedTime= '"+transactionTime+"' " +
										" where eventId="+redFuelEventID+" and" +
										" serialNumber ='"+serialNumber+"' and activeStatus=1 ");
								int rowsAffected = fuelUpdateQuery.executeUpdate();
								iLogger.info(serialNumber+":"+transactionTime+"  :"+" Close the previous Red Low Fuel Event");

							}
						}
					}
				}
				//Defect ID : 881: Rajani nagaraju 2013-07-01
				//DefectID: DF20131031 - Rajani Nagaraju - Capture GPX_Fix to deceide the SMS content for Tow-away alert

				iLogger.info(serialNumber+":"+transactionTime+"  :"+" Generate Health Alert ");
				eventDetails.alertGenerationLogic(serialNumber,transactionTime,eventIdValueMap, true, true, latValue, longValue,null, 0, currentGPSfix);


			}


			//Defect Id: 1017 - Rajani Nagaraju - 20130730 - To handle Hello Packets	
			int isHelloPacket =0;
			//Determine if this is a hello packet
			if(hashMap.containsKey(eventParameter))
			{
				HashMap<String, String> paramValuesMap = (HashMap<String, String>) hashMap.get(eventParameter);
				if(paramValuesMap.containsKey(HelloPacketParamName))
				{
					isHelloPacket=1;
				}
			}


			//Commenting the old Service Alert Generation Logic - Rajani Nagaraju - 20130822
			/*if(! (session.isOpen() ))
            {
                session = HibernateUtil.getSessionFactory().getCurrentSession();
                session.getTransaction().begin();
            }
			if(asset.getProductId()!=null && isHelloPacket==0)
			{
				Query q = session.createQuery("from EventTypeEntity where eventTypeName='"+ServiceAlerts+"'");
				Iterator it = q.list().iterator();
				int eventTypeId =0;
				while(it.hasNext())
				{
					EventTypeEntity e = (EventTypeEntity) it.next();
					eventTypeId = e.getEventTypeId();
				}

				HashMap<String,String> parameterValueMap = hashMap.get("CumulativeOperatingHours");
				Double firmwareEngineHourDouble = Double.parseDouble((String) parameterValueMap.values().toArray()[0]);
				int firmwareEngineHour = firmwareEngineHourDouble.intValue();

				String serviceScheduleFromQuery = " from ServiceScheduleEntity ";
				String serviceScheduleWhereQuery = " where engineHoursSchedule >= "+firmwareEngineHour+" " ;
				String serviceScheduleOrderByQuery = " order by engineHoursSchedule ";

				if(asset.getProductId().getAssetTypeId()!=null)
				{
					serviceScheduleWhereQuery = serviceScheduleWhereQuery + " and assetTypeId ="+asset.getProductId().getAssetTypeId().getAsset_type_id()+" ";
				}
				else
				{
					serviceScheduleWhereQuery = serviceScheduleWhereQuery + " and assetTypeId = NULL ";
				}

				if(asset.getProductId().getAssetGroupId()!=null)
				{
					serviceScheduleWhereQuery = serviceScheduleWhereQuery + " and assetGroupId="+asset.getProductId().getAssetGroupId().getAsset_group_id()+" " ;
				}
				else
				{
					serviceScheduleWhereQuery = serviceScheduleWhereQuery + " and assetGroupId = NULL ";
				}

				if(asset.getProductId().getEngineTypeId()!=null)
				{
					serviceScheduleWhereQuery = serviceScheduleWhereQuery + " and engineTypeId="+asset.getProductId().getEngineTypeId().getEngineTypeId()+" ";
				}
				else
				{
					serviceScheduleWhereQuery = serviceScheduleWhereQuery + " and engineTypeId = NULL ";
				}

				serviceScheduleFromQuery = serviceScheduleFromQuery+serviceScheduleWhereQuery+serviceScheduleOrderByQuery;

				Query query3 = session.createQuery(" from ServiceScheduleEntity where assetTypeId="+asset.getProductId().getAssetTypeId().getAsset_type_id()+" " +
						" and assetGroupId="+asset.getProductId().getAssetGroupId().getAsset_group_id()+" and engineTypeId="+asset.getProductId().getEngineTypeId().getEngineTypeId()+" " +
						" and engineHoursSchedule >= "+firmwareEngineHour+" order by engineHoursSchedule");

				Query query3 = session.createQuery(serviceScheduleFromQuery);
				query3.setMaxResults(1);
				Iterator itr3 = query3.list().iterator();
				long serviceScheduleEnginehour = 0;
				while(itr3.hasNext())
				{
					ServiceScheduleEntity serviceSchedule = (ServiceScheduleEntity) itr3.next();
					serviceScheduleEnginehour = serviceSchedule.getEngineHoursSchedule(); 
				}

				long differenceFreq = serviceScheduleEnginehour - firmwareEngineHour;
				int due =0;

				if(differenceFreq!=0 && serviceScheduleEnginehour!=0)
				{
					Query query4 = session.createQuery(" from EventEntity where eventTypeId= "+eventTypeId+" and frequency >= "+differenceFreq+ " order by frequency");
					query4.setMaxResults(1);
					Iterator itr4 = query4.list().iterator();

					while(itr4.hasNext())
					{
						due =1;
						EventEntity e = (EventEntity) itr4.next();
						int eventId= e.getEventId();

						Query query6 = session.createQuery("from EventEntity where eventId="+eventId);
						Iterator itr6 = query6.list().iterator();
						EventEntity evt = null;
						while(itr6.hasNext())
						{
							evt = (EventEntity) itr6.next();
						}


						Query query5 = session.createQuery(" from AssetEventEntity where serialNumber='"+serialNumber+"'  and activeStatus=1 and eventTypeId="+eventTypeId+"");

						int generateNewAlert = 0;
						if(query5.list()!=null)
						{
							Iterator itr5 = query5.list().iterator();

							while(itr5.hasNext())
							{
								generateNewAlert =1;

								AssetEventEntity assetEvent = (AssetEventEntity) itr5.next();
								if(assetEvent.getEventId().getEventId()==eventId)
								{
									//continue;
								}
								else
								{
									assetEvent.setActiveStatus(0);
									assetEvent.setEventClosedTime(transactionTime);
									session.update(assetEvent);


									eventIdValueMap = new HashMap<EventEntity, String>();
									eventIdValueMap.put(evt, String.valueOf(firmwareEngineHour));
									//Defect ID : 881: Rajani nagaraju 2013-07-01
									eventDetails.alertGenerationLogic(serialNumber, transactionTime, eventIdValueMap, true, false, latValue, longValue,null);
								}
							}
						}
						if(generateNewAlert==0)
						{
							eventIdValueMap = new HashMap<EventEntity, String>();
							eventIdValueMap.put(evt, String.valueOf(firmwareEngineHour));
							eventDetails.alertGenerationLogic(serialNumber, transactionTime, eventIdValueMap, true, false, latValue, longValue, null);
						}
					}


				}


				//Otherwise it is a Overdue
				//---------Check in AssetEvent if the machine has any Due for service - If yes, make it overdue
				if(! (session.isOpen() ))
	            {
	                session = HibernateUtil.getSessionFactory().getCurrentSession();
	                session.getTransaction().begin();
	            }

				if(due==0 || differenceFreq==0)
				{
					Query query8 = session.createQuery(" from EventEntity where eventTypeId="+eventTypeId+" and frequency=0");
					Iterator itr8 = query8.list().iterator();
					EventEntity overdueEvent = null;
					while(itr8.hasNext())
					{
						overdueEvent = (EventEntity) itr8.next();
					}


					Query query7 = session.createQuery(" from AssetEventEntity where serialNumber='"+serialNumber+"'  and activeStatus=1" +
							" and eventTypeId="+eventTypeId+" and eventSeverity='YELLOW' ");
					if(query7.list()!=null)
					{
						Iterator itr7 = query7.list().iterator();
						while(itr7.hasNext())
						{
							AssetEventEntity assetEvent = (AssetEventEntity) itr7.next();

							assetEvent.setActiveStatus(0);
							assetEvent.setEventClosedTime(transactionTime);
							session.update(assetEvent);

							eventIdValueMap = new HashMap<EventEntity, String>();
							eventIdValueMap.put(overdueEvent, String.valueOf(firmwareEngineHour));
							//Defect ID : 881: Rajani nagaraju 2013-07-01
							eventDetails.alertGenerationLogic(serialNumber, transactionTime, eventIdValueMap, true, false, latValue, longValue,null);


						}
					}
				}	

			}*/
			//Commenting the old Service Alert Generation Logic - Rajani Nagaraju - 20130822

			//***********************************Step 3: Application Alert - If the Machine is outside operating hours
			//get the time part of transaction time
			iLogger.info(serialNumber+":"+transactionTime+"  :"+"----------------- Machine Usage Outside Operating Hours-------------");
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			//DefectId: CR20131022 - Rajani Nagaraju - Generate MachineOutsideOPHour Alert only once in a day
			//DF20131126 - Rajani Nagaraju - To Handle the packets that are received with backdated timestamp
			//Get the transactionTime in IST
			//Get the Date Part of the transaction time
			String startDate=null;
			String endDate=null;

			Date txnDate = transactionTime;
			int txnHour = transactionTime.getHours();
			int txnMin = transactionTime.getMinutes();

			String txnDateInString = new SimpleDateFormat("yyyy-MM-dd").format(txnDate);

			//Get TxnDate -1 
			Calendar cal = Calendar.getInstance();
			cal.setTime(txnDate);
			cal.add(Calendar.DAY_OF_YEAR,-1);
			Date oneDayBefore= cal.getTime();
			String prevTxnDateInString = new SimpleDateFormat("yyyy-MM-dd").format(oneDayBefore);

			//Get TxnDate +1
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(txnDate);
			cal1.add(Calendar.DAY_OF_YEAR,1);
			Date oneDayAfter= cal1.getTime();
			String nextTxnDateInString = new SimpleDateFormat("yyyy-MM-dd").format(oneDayAfter);


			if(txnHour==18)
			{
				if(txnMin<30)
				{
					startDate=prevTxnDateInString;
					endDate=txnDateInString;
				}
				else
				{
					startDate =txnDateInString;
					endDate=nextTxnDateInString;
				}
			}

			else if (txnHour<18)
			{
				startDate=prevTxnDateInString;
				endDate=txnDateInString;
			}

			else if (txnHour>18)
			{
				startDate =txnDateInString;
				endDate=nextTxnDateInString;
			}


			int alertPresent=0;
			Query assetEventQry = session.createQuery(" select a.assetEventId from AssetEventEntity a join a.eventId b " +
					" where a.serialNumber='"+serialNumber+"' and " +
					" b.eventName ='"+OperatingHourEvent+"' and a.eventGeneratedTime >= '"+startDate+" 18:30:00' and " +
					" a.eventGeneratedTime<='"+endDate+" 18:29:59'");
			Iterator assetEvItr = assetEventQry.list().iterator();
			while(assetEvItr.hasNext())
			{
				int assetEventId = (Integer)assetEvItr.next();
				alertPresent =1;
			}

			if(alertPresent==0)
			{
				//DF20140313 - Rajani Nagaraju - Machine Outside OP Hour should not be generated when Ignition is OFF. 
				int isIgnitionOff =0;
				Query ignitionStatusQ = session.createQuery(" select b.parameterValue from AssetMonitoringHeaderEntity a, AssetMonitoringDetailEntity b, MonitoringParameters mp " +
						" where a.transactionNumber = b.transactionNumber " +
						" and b.parameterId= mp.parameterId " +
						" and mp.parameterName='"+Ignition_ON+"'" +
						"and a.transactionTime= " +
						"( select max(c.transactionTime) from AssetMonitoringHeaderEntity c where c.serialNumber='"+serialNumber+"') " +
						" and a.serialNumber = '"+serialNumber+"'");
				Iterator ignitionStatusItr = ignitionStatusQ.list().iterator();
				while(ignitionStatusItr.hasNext())
				{
					String ignitionOn = (String)ignitionStatusItr.next();
					if(ignitionOn.equalsIgnoreCase("0"))
					{
						isIgnitionOff=1;
					}
				}

				//infoLogger.info("isIgnitionOff:"+isIgnitionOff);
				//Defect Id: 1017 - Rajani Nagaraju - 20130730 - To handle Hello Packets
				if(isHelloPacket==0 && isIgnitionOff==0)
				{
					int transactionHours = transactionTime.getHours();
					Timestamp operatingStartTime = null;
					Timestamp operatingEndTime = null;

					//get the operating start and end time
					Query query8 = session.createQuery(" from AssetExtendedDetailsEntity where serial_number='"+serialNumber+"'");
					Iterator itr8 = query8.list().iterator();
					while(itr8.hasNext())
					{
						AssetExtendedDetailsEntity extendedDetails = (AssetExtendedDetailsEntity) itr8.next();
						operatingStartTime = extendedDetails.getOperatingStartTime();
						operatingEndTime = extendedDetails.getOperatingEndTime();
					}

					if(operatingStartTime==null)
					{
						//DefectID: Asset Owner Changes - Rajani Nagaraju - 20130719
						/*Query query9 = session.createQuery(" select b.tenancy_id from AssetAccountMapping a, AccountTenancyMapping b where " +
								" a.accountId = b.account_id and a.serialNumber='"+serialNumber+"'");*/
						Query query9 = session.createQuery(" select b.tenancy_id from AssetEntity a, AccountTenancyMapping b where " +
								" a.primary_owner_id = b.account_id and a.serial_number='"+serialNumber+"'");

						Iterator itr9 = query9.list().iterator();
						while(itr9.hasNext())
						{
							TenancyEntity tenancy = (TenancyEntity) itr9.next();
							operatingStartTime = tenancy.getOperating_Start_Time();
							operatingEndTime = tenancy.getOperating_End_Time();
						}
					}

					if(operatingStartTime==null)
					{
						//get the operating start time and operating end time from asset group profile
						Query query11 = session.createQuery("select a.operating_Start_Time, a.Operating_end_time from AssetGroupProfileEntity a, AssetEntity b, ProductEntity c" +
								" where a.asset_grp_id = c.assetGroupId and b.productId = c.productId and b.serial_number='"+serialNumber+"'");
						Iterator itr11 = query11.list().iterator();
						Object[] result = null;
						while(itr11.hasNext())
						{
							result = (Object[]) itr11.next();
							operatingStartTime = (Timestamp)result[0];
							operatingEndTime = (Timestamp)result[1];
						}
					}

					int startTime=0;
					if(operatingStartTime!=null)
						startTime= operatingStartTime.getHours();
					int endTime =0;
					if(operatingEndTime!=null)
						endTime = operatingEndTime.getHours();


					if((transactionHours==endTime) && transactionHours!=0)
					{
						int minutes = transactionTime.getMinutes();
						if(minutes>0 || (transactionTime.getSeconds()>0) )
						{
							endTime = endTime-1;
						}
					}

					iLogger.info(serialNumber+":"+transactionTime+"  :"+"----- Machine Usage Outside Operating Hours-----"+"Defined Start Time:"+startTime+", Defined End Time:"+endTime);
					if( (transactionHours<startTime || transactionHours>endTime) && (startTime!=0 && endTime!=0) )
					{
						//get the event Id for operating hours event
						EventEntity eve = null;
						Query query10 = session.createQuery(" from EventEntity where eventName = '"+OperatingHourEvent+"'");
						Iterator itr10 = query10.list().iterator();
						while(itr10.hasNext())
						{
							eve = (EventEntity) itr10.next();

						}

						eventIdValueMap = new HashMap<EventEntity, String>();
						eventIdValueMap.put(eve, Integer.toString(transactionHours));
						//Defect ID : 881: Rajani nagaraju 2013-07-01

						//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
						iLogger.info(serialNumber+":"+transactionTime+"  :"+" Generate Machine Usage Outside OP Hour Alert ");
						eventDetails.alertGenerationLogic(serialNumber, transactionTime, eventIdValueMap, false, false, latValue, longValue, null, 0, currentGPSfix);
					}
				}
			}


			//**************Step 4: Generate Application Alert - Landmark Alerts
			//check whether the serial Number comes under any landmark
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			//DF20140502 - Rajani Nagaraju - Generate/ Closure of Fuel theft Alert
			RecordTypeEntity receivedRecordType = null;
			Query messageTypeQuery = session.createQuery(" from RecordTypeEntity where messageId='"+receivedMessgeId+"'");
			Iterator messageTypeItr = messageTypeQuery.list().iterator();
			while(messageTypeItr.hasNext())
			{
				receivedRecordType= (RecordTypeEntity)messageTypeItr.next();
			}


			//Defect Id: 1017 - Rajani Nagaraju - 20130730 - To handle Hello Packets
			if(isHelloPacket==0)
			{
				iLogger.info(serialNumber+":"+transactionTime+"  :"+"----------------- Landmark Alert Generation -------------");
				//Added by Rajani Nagaraju - 20130715 -DefectID 955
				//Generate Landmark Alerts based only on Log Packets
				//Get the record type of the received packet
				if(receivedRecordType.getRecordTypeName().equalsIgnoreCase(logRecordType) )
				{
					//get the EventEntity for Landmark Arrival and departure
					LandmarkEventTypeName = "Landmark";
					Query landmarkQuery = session.createQuery("select a from EventEntity a, EventTypeEntity b where a.eventTypeId= b.eventTypeId " +
							" and b.eventTypeName='"+LandmarkEventTypeName+"'");
					Iterator landmarkItr = landmarkQuery.list().iterator();
					EventEntity departureEntity = null;
					EventEntity arrivalEntity = null;

					while(landmarkItr.hasNext())
					{
						EventEntity landmarkEvent = (EventEntity)landmarkItr.next();
						if(landmarkEvent.getEventName().equalsIgnoreCase(LandmarkDeparture))
						{
							departureEntity = landmarkEvent;
						}
						else if (landmarkEvent.getEventName().equalsIgnoreCase(LandmarkArrival))
						{
							arrivalEntity = landmarkEvent;
						}

					}

					//20140110: 1931  : Rajani Nagaraju - To generate Landmark Alerts only for "Active" Landmarks
					//Query query12 = session.createQuery("from LandmarkAssetEntity where Serial_number = '"+serialNumber+"'");
					Query query12 = session.createQuery(" select a from LandmarkAssetEntity a, LandmarkEntity b " +
							" where a.Serial_number = '"+serialNumber+"' and a.Landmark_id=b.Landmark_id and b.ActiveStatus=1 ");
					Iterator itr12 = query12.list().iterator();
					//List<LandmarkEntity> landmarkList = new LinkedList<LandmarkEntity>();
					List<Integer> landmarkIdList = new LinkedList<Integer>();

					while(itr12.hasNext())
					{
						LandmarkAssetEntity landmarkAsset = (LandmarkAssetEntity) itr12.next();
						LandmarkEntity landmark = landmarkAsset.getLandmark_id();
						landmarkIdList.add(landmark.getLandmark_id());
					}


					for(int p=0; p<landmarkIdList.size(); p++)
					{
						String machineLandmarkStatus = null;

						LandmarkEntity landmark = null;
						Query getLandmarkQuery = session.createQuery(" from LandmarkEntity where Landmark_id="+landmarkIdList.get(p));
						Iterator getLandmarkItr = getLandmarkQuery.list().iterator();
						while(getLandmarkItr.hasNext())
						{
							landmark = (LandmarkEntity)getLandmarkItr.next();
						}

						if(landmark!=null)
						{
							//If the landmark is not defined with latitude or longitude, just skip that landmark
							if( (landmark.getLatitude()==null) || (landmark.getLatitude().equals("0")) )
								continue;
							double landmarkLat = Double.parseDouble(landmark.getLatitude());

							if( (landmark.getLongitude()==null) || (landmark.getLongitude().equals("0")) )
								continue;
							double landmarkLong = Double.parseDouble(landmark.getLongitude());

							double radius = landmark.getRadius();

							EventEntity landmarkEvent =null;

							//get current Latitude and Longitude
							HashMap<String,String> locationParams= hashMap.get(Location);
							//Trim Off the last character since it is 'N'/'S' and cannot be converted to double
							//double currentLat = Double.parseDouble(locationParams.get(Latitude).substring(0, locationParams.get(Latitude).length()-1));
							//double currentLong = Double.parseDouble(locationParams.get(Longitude).substring(0, locationParams.get(Longitude).length()-1));

							double currentLat= Double.parseDouble(latValue);
							double currentLong = Double.parseDouble(longValue);

							//get previous latitude and longitude of the serial number
							if(! (session.isOpen() ))
							{
								session = HibernateUtil.getSessionFactory().getCurrentSession();
								session.getTransaction().begin();
							}


							/*Query query13 = session.createQuery(" select b.parameterValue, c.parameterName from AssetMonitoringHeaderEntity a, AssetMonitoringDetailEntity b, " +
								"MonitoringParameters c where a.transactionNumber = b.transactionNumber and b.parameterId = c.parameterId and a.serialNumber = '"+serialNumber+"' and " +
								" c.parameterName in ('"+Latitude+"' , '"+Longitude+"' ) and a.transactionNumber = ( " +
								" select max(transactionNumber) from AssetMonitoringHeaderEntity where serialNumber = '"+serialNumber+"' and " +
								" transactionTime!= '"+transactionTime+"' and recordTypeId = "+receivedRecordType.getRecordTypeId()+" )"); */

							//Added by Rajani Nagaraju - 20130715- DefectID 955

							Query query13 = session.createQuery(" select b.parameterValue, c.parameterName from AssetMonitoringHeaderEntity a, AssetMonitoringDetailEntity b, " +
									"MonitoringParameters c where a.transactionNumber = b.transactionNumber and b.parameterId = c.parameterId and a.serialNumber = '"+serialNumber+"' and " +
									" c.parameterName in ('"+Latitude+"' , '"+Longitude+"' ) and a.transactionTime = ( " +
									" select max(transactionTime) from AssetMonitoringHeaderEntity where serialNumber = '"+serialNumber+"' and " +
									" transactionTime!= '"+transactionTime+"' and recordTypeId = "+receivedRecordType.getRecordTypeId()+" )" +
									" and a.recordTypeId="+receivedRecordType.getRecordTypeId()); 
							Iterator itr13 = query13.list().iterator();
							Object[] result13 = null;
							double prevLat = 0;
							double prevLong = 0;


							while(itr13.hasNext())
							{
								result13 = (Object[]) itr13.next();
								String value = result13[0].toString();
								if(result13[1].toString().equalsIgnoreCase(Latitude))
								{
									prevLat = Double.parseDouble(value.substring(0, value.length()-1));
								}
								else if (result13[1].toString().equalsIgnoreCase(Longitude))
								{
									prevLong = Double.parseDouble(value.substring(0, value.length()-1));
								}
							}


							int wasPreviouslyPresent =0;
							int isCurrentlyPresent =0;
							Geofencing geo = new Geofencing();

							if(prevLat!=0 && prevLong!=0 )
							{
								//find whether the machine was in landmark
								double distance = geo.calDistance(landmarkLat, landmarkLong, prevLat, prevLong);
								iLogger.info(serialNumber+":"+transactionTime+"  :"+"Previous Distance From Landmark :" + distance);
								if(distance<radius)
								{
									wasPreviouslyPresent =1;
									iLogger.info(serialNumber+":"+transactionTime+"  :"+"Previously Present");
								}
								else
								{
									wasPreviouslyPresent =2;
									iLogger.info(serialNumber+":"+transactionTime+"  :"+"Previously not Present");
								}
							}

							if(currentLat!=0 && currentLong!=0 )
							{
								//find whether the machine is in landmark
								double distance = geo.calDistance(landmarkLat, landmarkLong, currentLat, currentLong);
								iLogger.info(serialNumber+":"+transactionTime+"  :"+"Current Distance From Landmark :" + distance);
								if(distance<radius)
								{
									isCurrentlyPresent =1;
									iLogger.info(serialNumber+":"+transactionTime+"  :"+"Currently Present");;
								}
								else
								{
									isCurrentlyPresent =2;
									iLogger.info(serialNumber+":"+transactionTime+"  :"+"Currently not Present");
								}
							}


							//find if isArrival is selected for the landmark and check for the condition
							if(wasPreviouslyPresent==1) // Indicates the machine was in landmark
							{
								if(isCurrentlyPresent==1)// Indicates the machine is currently in landmark
								{
									iLogger.info(serialNumber+":"+transactionTime+"  :"+"Machine is residing in the same location");
									//This means the machine is residing in the same landmark since the last packet was received
								}
								else
								{
									iLogger.info(serialNumber+":"+transactionTime+"  :"+"Machine Departed");
									if(landmark.getIsDeparture()==1)
									{
										eventIdValueMap = new HashMap<EventEntity, String>();
										eventIdValueMap.put(departureEntity, landmark.getLandmark_Name());
										//Defect ID : 881: Rajani nagaraju 2013-07-01
										//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
										iLogger.info(serialNumber+":"+transactionTime+"  :"+" Generate Landmark Alert - Departure ");
										eventDetails.alertGenerationLogic(serialNumber, transactionTime, eventIdValueMap, false, false, latValue, longValue, landmark, 0, currentGPSfix);
									}

									//This means the machine has departed from the landmark
									machineLandmarkStatus = "Departure";

									AssetEventEntity landmarkDepartureEvent =null;
									if(! (session.isOpen() ))
									{
										session = HibernateUtil.getSessionFactory().getCurrentSession();
										session.getTransaction().begin();
									}

									if(landmark.getIsDeparture()==1)
									{

										Query assetEventQuery = session.createQuery("from AssetEventEntity where assetEventId= ( select " +
												" max(assetEventId) from AssetEventEntity where serialNumber='"+serialNumber+"' and " +
												" eventGeneratedTime='"+transactionTime+"' and activeStatus=1 )");
										Iterator itr = assetEventQuery.list().iterator();

										while(itr.hasNext())
										{
											landmarkDepartureEvent = (AssetEventEntity)itr.next();
										}
									}
									iLogger.info(serialNumber+":"+transactionTime+"  :"+"Insert Landmark Log details for Machine Departure");
									LandmarkLogDetailsEntity landmarkLogs = new LandmarkLogDetailsEntity();
									landmarkLogs.setSerialNumber(asset);
									landmarkLogs.setLandmarkId(landmark);
									landmarkLogs.setTransactionTimestamp(transactionTime);
									landmarkLogs.setMachineLandmarkStatus("Departure");
									if(landmarkDepartureEvent!=null)
										landmarkLogs.setAssetEventId(landmarkDepartureEvent);
									session.save(landmarkLogs);
								}
							}


							else if (wasPreviouslyPresent==2 || wasPreviouslyPresent==0)//Indicates the machine was outside the landmark
							{

								if(isCurrentlyPresent==1) //Indicates the machine is currently in the landmark
								{
									iLogger.info(serialNumber+":"+transactionTime+"  :"+"Machine Arrived to the landmark");
									if(landmark.getIsArrival()==1)
									{
										eventIdValueMap = new HashMap<EventEntity, String>();
										eventIdValueMap.put(arrivalEntity, landmark.getLandmark_Name());
										//Defect ID : 881: Rajani nagaraju 2013-07-01
										//DF20140102 - Rajani Nagaraju - To provide the InfoLoggers in a specific pattern
										iLogger.info(serialNumber+":"+transactionTime+"  :"+" Generate Landmark Alert - Arrival ");
										eventDetails.alertGenerationLogic(serialNumber, transactionTime, eventIdValueMap, false, false, latValue, longValue, landmark, 0, currentGPSfix);
									}

									//This means the machine has arrived to the landmark
									machineLandmarkStatus = "Arrival";

									AssetEventEntity landmarkArrivalEvent =null;

									if(! (session.isOpen() ))
									{
										session = HibernateUtil.getSessionFactory().getCurrentSession();
										session.getTransaction().begin();
									}

									if(landmark.getIsArrival()==1)
									{
										Query assetEventQuery = session.createQuery("from AssetEventEntity where assetEventId= ( select " +
												" max(assetEventId) from AssetEventEntity where serialNumber='"+serialNumber+"' and " +
												" eventGeneratedTime='"+transactionTime+"' and activeStatus=1 )");
										Iterator itr = assetEventQuery.list().iterator();

										while(itr.hasNext())
										{
											landmarkArrivalEvent = (AssetEventEntity)itr.next();
										}
									}

									iLogger.info(serialNumber+":"+transactionTime+"  :"+"Insert Landmark details for machine Arrival");
									LandmarkLogDetailsEntity landmarkLogs = new LandmarkLogDetailsEntity();
									landmarkLogs.setSerialNumber(asset);
									landmarkLogs.setLandmarkId(landmark);
									landmarkLogs.setTransactionTimestamp(transactionTime);
									landmarkLogs.setMachineLandmarkStatus("Arrival");
									if(landmarkArrivalEvent!=null)
										landmarkLogs.setAssetEventId(landmarkArrivalEvent);
									session.save(landmarkLogs);



								}
							}
						}



						//Check for the closure of Landmark Alerts
						if(machineLandmarkStatus!= null)
						{
							iLogger.info(serialNumber+":"+transactionTime+"  :"+"Check for closure of landmark Alerts");
							String previousMachineStatus =null;

							if(machineLandmarkStatus.equalsIgnoreCase("Departure"))
							{
								previousMachineStatus = "Arrival";
							}
							else if(machineLandmarkStatus.equalsIgnoreCase("Arrival"))
							{
								previousMachineStatus = "Departure";
							}

							if(! (session.isOpen() ))
							{
								session = HibernateUtil.getSessionFactory().getCurrentSession();
								session.getTransaction().begin();
							}
							Query landmarkLogDetailsQuery = session.createQuery(" from LandmarkLogDetailsEntity where serialNumber='"+serialNumber+"' " +
									" and landmarkId="+landmark.getLandmark_id()+" and machineLandmarkStatus='"+previousMachineStatus+"' order by transactionTimestamp desc ");
							landmarkLogDetailsQuery.setMaxResults(1);
							Iterator landmarkLogDetailsItr = landmarkLogDetailsQuery.list().iterator();

							AssetEventEntity deactivateAssetEvent = null;
							while(landmarkLogDetailsItr.hasNext())
							{
								LandmarkLogDetailsEntity landmarkLog = (LandmarkLogDetailsEntity)landmarkLogDetailsItr.next();
								if(landmarkLog.getAssetEventId()!=null)
								{
									deactivateAssetEvent = landmarkLog.getAssetEventId();
								}
							}

							if(deactivateAssetEvent!=null)
							{
								iLogger.info(serialNumber+":"+transactionTime+"  :"+"Deactivate Landmark Alert "+previousMachineStatus+" for the Landmark "+landmark.getLandmark_id());
								//Query inactiveAssetEvent = session.createQuery(" update AssetEventEntity set activeStatus=0 where assetEventId="+deactivateAssetEvent.getAssetEventId());
								deactivateAssetEvent.setActiveStatus(0);
								deactivateAssetEvent.setEventClosedTime(transactionTime);
								session.update(deactivateAssetEvent);

							}
						}
					}

				}

				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}

			}

			//Step 2: Get Application Service Alert
			//get the eventtype id for service alerts
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			iLogger.info(serialNumber+":"+transactionTime+"  :"+"----------------- Service Alert Generation -------------");
			//Changes done by Rajani Nagaraju - 20130822
			//DefectId: DF20131022- Rajani Nagaraju - To move Service Alert Generation Logic to the last
			//DefectID:1163 - To include the logic for Service Alert based on service date to be also checked for every 10 min packet
			//Call getAssetServiceSchedule service to get the required info - This includes the logic to check for the valid next service both based on service date and engine hours
			AssetServiceScheduleImpl assetServiceImplObj = new AssetServiceScheduleImpl();
			int eventId=0,present=0, serviceScheduleId =0;
			AssetServiceScheduleGetReqContract reqObj = new AssetServiceScheduleGetReqContract();
			reqObj.setSerialNumber(serialNumber);

			List<AssetServiceScheduleGetRespContract> responseList = assetServiceImplObj.getAssetserviceSchedule(reqObj);
			for(int i=0; i<responseList.size(); i++)
			{
				if(responseList.get(i).getHoursToNextService()!=null)
				{
					present=1;
					eventId = responseList.get(i).getEventId();
					serviceScheduleId = responseList.get(i).getServiceScheduleId();
				}
			}

			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}


			if(present==1 && eventId!=0)
			{
				//Get the EventTypeId for serviceAlert
				Query qt = session.createQuery("from EventTypeEntity where eventTypeName='"+ServiceAlerts+"'");
				Iterator it = qt.list().iterator();
				int eventTypeId =0;
				while(it.hasNext())
				{
					EventTypeEntity e = (EventTypeEntity) it.next();
					eventTypeId = e.getEventTypeId();
				}

				//If any other service alert is there for the VIN, nullify  the same before generating the new service alert
				Query serviceAlertQuery = session.createQuery(" from AssetEventEntity where serialNumber='"+serialNumber+"' and eventTypeId="+eventTypeId+" and activeStatus=1");
				Iterator serviceAlertItr = serviceAlertQuery.list().iterator();
				int generateAlert =1;
				while(serviceAlertItr.hasNext())
				{
					AssetEventEntity assetEvent = (AssetEventEntity) serviceAlertItr.next();
					if( (assetEvent.getEventId().getEventId()==eventId) && (assetEvent.getServiceScheduleId()==serviceScheduleId) )
					{
						generateAlert=0;
					}

					else
					{
						//Nullify the previous Alert
						assetEvent.setActiveStatus(0);
						assetEvent.setEventClosedTime(transactionTime);
						session.update(assetEvent);
					}
				}


				if(generateAlert==1)
				{
					HashMap<String,String> parameterValueMap = hashMap.get("CumulativeOperatingHours");
					Double firmwareEngineHourDouble = Double.parseDouble((String) parameterValueMap.values().toArray()[0]);
					int firmwareEngineHour = firmwareEngineHourDouble.intValue();

					EventEntity evt = null;
					Query q = session.createQuery("from EventEntity where eventId="+eventId);
					Iterator itr = q.list().iterator();
					while(itr.hasNext())
					{
						evt = (EventEntity) itr.next();
					}

					if(evt!=null)
					{
						eventIdValueMap = new HashMap<EventEntity, String>();
						eventIdValueMap.put(evt, String.valueOf(firmwareEngineHour));

						iLogger.info(serialNumber+":"+transactionTime+"  :"+" Generate Service alert");
						eventDetails.alertGenerationLogic(serialNumber, transactionTime, eventIdValueMap, true, false, latValue, longValue, null, serviceScheduleId, currentGPSfix);
					}

				}
			}


			//-------------------------------------------- Application Alert - Fuel Theft Alert --------------------------------------------
			//DF20140502 - Rajani Nagaraju - Generate/ Closure of Fuel theft Alert
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			//infoLogger.info(serialNumber+":"+transactionTime+"  :"+"----------- Fuel Theft Alert Generation - Application Generated Alert-------------");
			//IF the received packet is a log packet/ Low Fuel Level Event, Check for the Fuel Theft
			/*if( (receivedRecordType.getRecordTypeName().equalsIgnoreCase(logRecordType))  || ( newlowFuelEventList.size()>0) )
			{
				//get the current fuelLevel in volts
				HashMap<String, String> logParamValuesMap = (HashMap<String, String>) hashMap.get(LogParameters);
				//get the converted UOM value of Fuel Level
				String FuelLevelValue = logParamValuesMap.get(FuelLevel);
				ParameterUOMmanagement getParamValue = new ParameterUOMmanagement();
				String fuelLevelString = getParamValue.getParameterValue(FuelLevelValue);
				double currentFuelLevelInDouble = Double.parseDouble(fuelLevelString);

				//10 min buffer - instead of exact 1 hour it would be packet received at last 65 minutes or last 55 minutes
				Date prevStartTime =  new Date(transactionTime.getTime() - 3900 * 1000);
				String prevStartPkt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(prevStartTime); 

				Date prevEndTime =  new Date(transactionTime.getTime() - 3300 * 1000);
				String prevEndPkt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(prevEndTime); 

				if(! (session.isOpen() ))
	            {
	                session = HibernateUtil.getSessionFactory().getCurrentSession();
	                session.getTransaction().begin();
	            }

				//Identify the fuel level that was sent at least 60 minutes before the transaction timestamp sent in current packet
				int lastPkt=0;
				double prevFuelLevel=0;
				Timestamp transactionTimestamp = null;

				Query last1HourPkt = session.createQuery(" select b.parameterValue, a.transactionTime from AssetMonitoringHeaderEntity a, " +
						" AssetMonitoringDetailEntity b, MonitoringParameters c " +
						" where a.serialNumber='"+serialNumber+"'" +
						" and a.recordTypeId=3 " +
						" and a.transactionTime = (select max(transactionTime) from  AssetMonitoringHeaderEntity where serialNumber='"+serialNumber+"'" +
												" and transactionTime between '"+prevStartPkt+"' and '"+prevEndPkt+"' and recordTypeId=3 )" +
						" and a.transactionNumber=b.transactionNumber " +
						" and b.parameterId = c.parameterId " +
						" and c.parameterName='"+FuelLevel+"'");
				Iterator last1HourPktItr = last1HourPkt.list().iterator();
				Object[] fuelResultSet = null;
				while(last1HourPktItr.hasNext())
				{
					lastPkt=1;
					fuelResultSet = (Object[]) last1HourPktItr.next();
					String fuelLevel = (String)fuelResultSet[0];
					prevFuelLevel = Double.parseDouble(fuelLevel);
					transactionTimestamp = (Timestamp)fuelResultSet[1];
				}

				//if(lastPkt==0)
				//{
					Query last1HourEvtPkt = session.createQuery(" select d.parameterValue,a.transactionTime from AssetMonitoringHeaderEntity a, " +
							" AssetMonitoringDetailEntity d, MonitoringParameters e, " +
							" AssetEventEntity b, EventEntity c " +
							" where a.serialNumber=b.serialNumber and a.transactionTime=b.eventGeneratedTime " +
							" and a.serialNumber='"+serialNumber+"'" +
							" and a.transactionTime between '"+prevStartPkt+"' and '"+prevEndPkt+"' and b.eventId=c.eventId " +
							" and c.eventName in ('"+LowFuelLevel+"', '"+LowFuelLevelCritical+"')" +
							" and a.transactionNumber=d.transactionNumber " +
							" and d.parameterId = e.parameterId " +
							" and e.parameterName='"+FuelLevel+"'");
					Iterator last1HourEvtPktItr = last1HourEvtPkt.list().iterator();
					Object[] fuelEvtResultSet = null;
					while(last1HourEvtPktItr.hasNext())
					{
						lastPkt=1;
						fuelEvtResultSet = (Object[])last1HourEvtPktItr.next();
						String fuelLevel = (String)fuelEvtResultSet[0];
						if(transactionTimestamp!=null)
						{
							Timestamp evtTxnTimestamp = (Timestamp)fuelEvtResultSet[1];
							if(evtTxnTimestamp.after(transactionTimestamp))
							{
								prevFuelLevel = Double.parseDouble(fuelLevel);
							}
						}
						else
							prevFuelLevel = Double.parseDouble(fuelLevel);
					}
				//}

				//If Either Log Packet or LowFuelEvent Packet is received in last 60 min, check for Fuel Theft
				if(lastPkt==1)
				{
					if( (prevFuelLevel-currentFuelLevelInDouble) > 12 )
					{
						Query fuelTheftQry = session.createQuery(" from AssetEventEntity where serialNumber='"+serialNumber+"' " +
									" and eventId="+FuelTheftEventID+" and activeStatus=1 ");
						Iterator fuelTheftItr = fuelTheftQry.list().iterator();
						int theftAlertPresent=0;
						while(fuelTheftItr.hasNext())
						{
							AssetEventEntity assetEvent = (AssetEventEntity) fuelTheftItr.next();
							theftAlertPresent=1;
						}

						if(theftAlertPresent==0)
						{
							//Generate FuelTheft event
							EventEntity evt = null;
							Query q = session.createQuery("from EventEntity where eventId="+FuelTheftEventID);
							Iterator itr = q.list().iterator();
							while(itr.hasNext())
							{
								evt = (EventEntity) itr.next();
							}

							if(evt!=null)
							{
								eventIdValueMap = new HashMap<EventEntity, String>();
								eventIdValueMap.put(evt, "1");

								infoLogger.info(serialNumber+":"+transactionTime+"  :"+" Generate Fuel Theft alert: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+prevFuelLevel);
								eventDetails.alertGenerationLogic(serialNumber, transactionTime, eventIdValueMap, true, true, latValue, longValue, null, serviceScheduleId, currentGPSfix);
							}
						}
					}


					else if( (prevFuelLevel-currentFuelLevelInDouble) <= 11 )
					{
						Query query2 = session.createQuery("update AssetEventEntity set activeStatus=0, eventClosedTime= '"+transactionTime+"' " +
								" where eventId="+FuelTheftEventID+" and" +
								" serialNumber ='"+serialNumber+"' and activeStatus=1 ");
						int rowsAffected = query2.executeUpdate();
						if(rowsAffected>0)
							infoLogger.info(serialNumber+":"+transactionTime+"  :"+"Nullify the Fuel Theft Alert: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+prevFuelLevel);
					}
				}

			}*/

			//IF the received packet is a log packet/ Low Fuel Level Event, Check for the Fuel Theft
			//DefectId:20140627 @Suprava Sn
			//Start of comment line @Suprava 2014-08-04 Not to Put in Production
			//DefectId:20140806 New logic applied in fuel theft @suprava
			//DefectId:20140811 @Suprava Check for the firmware Version Number Before Generate Fuel Theft Alert
			int fwmPkt =0;
			String FWVersionNumber1 = null;
			FWVersionNumber1 = parser.getFwVersionNumber();

			String FWVersionNum1 =  FWVersionNumber1.substring(0, FWVersionNumber1.indexOf("**")).toString();

			FwVersion fwVersion =new FwVersion();
			int result = fwVersion.compareNumber(versionNum, FWVersionNum1);
			if(result>0){
				fwmPkt =1;

			}else if(result<0){
				fwmPkt =2;   

			}
			//DefectId:20140820 New logic applied in fuel theft @suprava
			String ignitionOn = null;
			String engineOn = null;
			//To get the machine Ignition/Engine status from AMH and AMD table 
			Query prevLocQuery1 = session.createQuery(" select a.serialNumber, b.parameterValue, " +
					" b.parameterId as parameterId, a.transactionTime " +
					" from AssetMonitoringHeaderEntity a , AssetMonitoringDetailEntity b " +
					" where a.transactionNumber = b.transactionNumber " +
					" and a.serialNumber='"+serialNumber+"' " +
					" and b.parameterId in (12,18)" +
					" and a.transactionTime = (select max(c.transactionTime) from AssetMonitoringHeaderEntity c " +
					" where c.serialNumber='"+serialNumber+"' " +
					" and c.transactionTime < '"+transactionTime+"')");
			Iterator prevLocItr = prevLocQuery1.list().iterator();
			Object[] resultSet1 = null;
			while(prevLocItr.hasNext())
			{
				resultSet1 = (Object[]) prevLocItr.next();
				MonitoringParameters paramObj = (MonitoringParameters)resultSet1[2];
				int parameterId = paramObj.getParameterId();
				if(parameterId==12)//IgnitionOn
				{
					ignitionOn = ((String)resultSet1[1]);
				}
				else if(parameterId==18)//EngineOn
				{
					engineOn = ((String)resultSet1[1]);
				}
			}
			//DefectId:20141117 To Handle Null check
			if( (receivedRecordType.getRecordTypeName().equalsIgnoreCase(logRecordType))&& (fwmPkt==2) && (ignitionOn!=null && ignitionOn.equalsIgnoreCase("1") && engineOn!=null && engineOn.equalsIgnoreCase("1")))
			{
				ListToStringConversion conversionObj = new ListToStringConversion();
				//get the current fuelLevel in volts
				HashMap<String, String> logParamValuesMap = (HashMap<String, String>) hashMap.get(LogParameters);
				//get the converted UOM value of Fuel Level
				String FuelLevelValue = logParamValuesMap.get(FuelLevel);
				ParameterUOMmanagement getParamValue = new ParameterUOMmanagement();
				String fuelLevelString =null;
				if(FuelLevelValue!=null){
					fuelLevelString = getParamValue.getParameterValue(FuelLevelValue);
				}

				double currentFuelLevelInDouble=0.0;
				if(fuelLevelString!=null){
					currentFuelLevelInDouble = Double.parseDouble(fuelLevelString);
				}
//	2015-01-22: Changed the initial value for fuel level from 11 to 5
				if(fuelLevelString!=null && (currentFuelLevelInDouble >= 5 || currentFuelLevelInDouble <=100))
				{
					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}
					//Identify the fuel level that was sent before the transaction timestamp sent in current packet
					int lastPkt=0;
					double prevFuelLevel=0.0;
					String fuelLevel=null;
					double avgFuelLevel= 0.0;
					double avgFuelLevel1 =0.0;
					Timestamp transactionTimestamp = null;
					String transactionTimestampString = null;
					int transactionNumber1 =0;
					List<String> transactionTimestampListFinal = new LinkedList<String>();
					List<Integer> transactionNumberListFinal = new LinkedList<Integer>();

					if (serialNumber != null && transactionTime != null) {

						Query query = session.createQuery(" select transactionTime,transactionNumber from  AssetMonitoringHeaderEntity where serialNumber='"+serialNumber+"'" +
								" and transactionTime < '"+transactionTime+"' and recordTypeId=3 order by transactionTime desc ").setFirstResult(0).setMaxResults(3);
						Iterator itr = query.list().iterator();
						Object[] fuelResultSet1 = null;
						while(itr.hasNext())
						{
							lastPkt=1;
							fuelResultSet1 = (Object[]) itr.next();
							transactionTimestamp = (Timestamp)fuelResultSet1[0];
							transactionTimestampString = transactionTimestamp.toString();
							transactionTimestampListFinal.add(transactionTimestampString);
							transactionNumber1 = (Integer)fuelResultSet1[1];
							transactionNumberListFinal.add(transactionNumber1);
							
							}

					}

					String transactionNumberAsStringList = conversionObj.getIntegerListString(transactionNumberListFinal).toString();
					String transactionTimestampAsStringList = conversionObj.getStringList(
							transactionTimestampListFinal).toString();

					if(transactionNumberAsStringList!=null){
					Query last1HourPkt1 = session.createQuery(" select sum(b.parameterValue),a.serialNumber from AssetMonitoringHeaderEntity a, " +
							" AssetMonitoringDetailEntity b, MonitoringParameters c " +
							" where a.serialNumber='"+serialNumber+"'" +
							" and a.recordTypeId=3 " +
							" and a.transactionNumber in ("+transactionNumberAsStringList+")" +
							" and a.transactionNumber=b.transactionNumber " +
							" and b.parameterId = c.parameterId " +
							" and c.parameterName='"+FuelLevel+"'").setFirstResult(0).setMaxResults(2);
					Iterator last1HourPktItr1 = last1HourPkt1.list().iterator();
					Object[] fuelResultSet2 = null;
					while(last1HourPktItr1.hasNext())
					{
						lastPkt=1;
						fuelResultSet2 = (Object[]) last1HourPktItr1.next();
						fuelLevel = (String)fuelResultSet2[0];
						//System.out.println("Total fuelLevel value of 3 packets:"+fuelLevel);

						if(fuelLevel!=null){
							prevFuelLevel = Double.parseDouble(fuelLevel);
							//System.out.println("Total fuelLevel value of 3 packets:"+prevFuelLevel);
						}
					}
					}
					avgFuelLevel=prevFuelLevel/3;
					iLogger.info(serialNumber+":"+transactionTime+"  :"+"Avg FuelLevel value of 3 packets:"+avgFuelLevel);
					
					/*Query last1HourPkt = session.createQuery(" select sum(b.parameterValue),a.serialNumber from AssetMonitoringHeaderEntity a, " +
							" AssetMonitoringDetailEntity b, MonitoringParameters c " +
							" where a.serialNumber='"+serialNumber+"'" +
							" and a.recordTypeId=3 " +
							" and a.transactionTime in ("+transactionTimestampAsStringList+")" +
							" and a.transactionNumber=b.transactionNumber " +
							" and b.parameterId = c.parameterId " +
							" and c.parameterName='"+FuelLevel+"'").setFirstResult(0).setMaxResults(3);
					Iterator last1HourPktItr = last1HourPkt.list().iterator();
					Object[] fuelResultSet = null;
					while(last1HourPktItr.hasNext())
					{
						lastPkt=1;
						fuelResultSet = (Object[]) last1HourPktItr.next();
						fuelLevel = (String)fuelResultSet[0];
						System.out.println("Total fuelLevel value of 3 packets:"+fuelLevel);

						if(fuelLevel!=null){
							prevFuelLevel = Double.parseDouble(fuelLevel);
							System.out.println("Total fuelLevel value of 3 packets:"+prevFuelLevel);
						}
					}*/
					//avgFuelLevel=prevFuelLevel/3;
					//System.out.println("Avg FuelLevel value of 3 packets:"+avgFuelLevel);

					if(lastPkt==1){
						
						if( (avgFuelLevel-currentFuelLevelInDouble) >= 12 )
						{
							//Generate FuelTheft event
							EventEntity evt = null;
							Query q = session.createQuery("from EventEntity where eventId="+FuelTheftEventID);
							Iterator itr = q.list().iterator();
							while(itr.hasNext())
							{
								evt = (EventEntity) itr.next();
							}

							if(evt!=null)
							{
								eventIdValueMap = new HashMap<EventEntity, String>();
								eventIdValueMap.put(evt, "1");

								//System.out.println(serialNumber+":"+transactionTime+"  :"+"Generate Fuel Theft alert: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+avgFuelLevel);
								iLogger.info(serialNumber+":"+transactionTime+"  :"+"Generate Fuel Theft alert: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+avgFuelLevel);
								
								//DF20140511 - Rajani Nagaraju - Supress Multiple Alert Generation if the same pkt is received multiple times
								int generateAlert=1;
								Query existsQuery = session.createQuery("SELECT ae.serialNumber" +
											"	FROM AssetEventEntity ae" +
											" WHERE ae.serialNumber='"+serialNumber+"' " +
											" AND ae.eventGeneratedTime = '"+transactionTime+"'" +
											" AND ae.eventId ="+evt.getEventId()+
									" AND ae.activeStatus=1");
									if(existsQuery.list().size()>0)
									{
										iLogger.info("serialNumber "+serialNumber+ " transactionTime "+ transactionTime +" eventID "+evt.getEventId() +" with active status 1 already exists");
										//System.out.println("Continue");
										generateAlert=0;
									}
							
								if(generateAlert==1)//DF20140511 - Rajani Nagaraju - Supress Multiple Alert Generation if the same pkt is received multiple times
								eventDetails.alertGenerationLogic(serialNumber, transactionTime, eventIdValueMap, true, true, latValue, longValue, null, serviceScheduleId, currentGPSfix);
							}
						}



						else if( (avgFuelLevel-currentFuelLevelInDouble) <= -12 )
						{
							Query query2 = session.createQuery("update AssetEventEntity set activeStatus=0, eventClosedTime= '"+transactionTime+"' " +
									" where eventId="+FuelTheftEventID+" and" +
									" serialNumber ='"+serialNumber+"' and activeStatus=1 ");
							int rowsAffected = query2.executeUpdate();
							if(rowsAffected>0){

								//System.out.println(serialNumber+":"+transactionTime+"  :"+" Nullify the Fuel Theft Alert: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+avgFuelLevel);
								iLogger.info(serialNumber+":"+transactionTime+"  :"+" Nullify the Fuel Theft Alert: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+avgFuelLevel);
							}
							//If no fuel theft alert already exists for that machine
							else {

								//System.out.println(serialNumber+":"+transactionTime+"  :"+" Do nothing for fuel theft Continue processing packet: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+avgFuelLevel);
								iLogger.info(serialNumber+":"+transactionTime+"  :"+" Do nothing for fuel theft Continue processing packet: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+avgFuelLevel);
							}

						}

						//(Difference> -12) && (Difference < 12)
						else
						{
							//System.out.println(serialNumber+":"+transactionTime+"  :"+" Do nothing for fuel theft Continue processing packet: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+avgFuelLevel);
							iLogger.info(serialNumber+":"+transactionTime+"  :"+" Do nothing for fuel theft Continue processing packet: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+avgFuelLevel);
						}
					}
				}
			}
			//DefectId:20140820 @End
			/*
			if( (receivedRecordType.getRecordTypeName().equalsIgnoreCase(logRecordType))&& (fwmPkt==2 || fwmPkt==0))
			{
			//get the current fuelLevel in volts
			HashMap<String, String> logParamValuesMap = (HashMap<String, String>) hashMap.get(LogParameters);
			//get the converted UOM value of Fuel Level
			String FuelLevelValue = logParamValuesMap.get(FuelLevel);
			ParameterUOMmanagement getParamValue = new ParameterUOMmanagement();
			String fuelLevelString =null;
				if(FuelLevelValue!=null){
				fuelLevelString = getParamValue.getParameterValue(FuelLevelValue);
				}

				double currentFuelLevelInDouble=0.0;
				if(fuelLevelString!=null){
				currentFuelLevelInDouble = Double.parseDouble(fuelLevelString);
				}

								if(fuelLevelString!=null && (currentFuelLevelInDouble >= 11 || currentFuelLevelInDouble <=100))
			{
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				//Identify the fuel level that was sent before the transaction timestamp sent in current packet
				int lastPkt=0;
					double prevFuelLevel=0.0;
				Timestamp transactionTimestamp = null;

				Query last1HourPkt = session.createQuery(" select b.parameterValue, a.transactionTime from AssetMonitoringHeaderEntity a, " +
						" AssetMonitoringDetailEntity b, MonitoringParameters c " +
						" where a.serialNumber='"+serialNumber+"'" +
						" and a.recordTypeId=3 " +
						" and a.transactionTime = (select max(transactionTime) from  AssetMonitoringHeaderEntity where serialNumber='"+serialNumber+"'" +
						" and transactionTime < '"+transactionTime+"' and recordTypeId=3 )" +
						" and a.transactionNumber=b.transactionNumber " +
						" and b.parameterId = c.parameterId " +
						" and c.parameterName='"+FuelLevel+"'");
				Iterator last1HourPktItr = last1HourPkt.list().iterator();
				Object[] fuelResultSet = null;
				while(last1HourPktItr.hasNext())
				{
					lastPkt=1;
					fuelResultSet = (Object[]) last1HourPktItr.next();
					String fuelLevel = (String)fuelResultSet[0];

						if(fuelLevel!=null){
						prevFuelLevel = Double.parseDouble(fuelLevel);
						}
					transactionTimestamp = (Timestamp)fuelResultSet[1];

					}

					//Check machine is marked as Probable Fuel theft
					String profuelTheft = null;
					Query fuelTheftPkt = session.createQuery(" select a.description, a.serial_number from AssetEntity a " +
							" where a.serial_number='"+serialNumber+"'");
					Iterator fuelTheftPktItr = fuelTheftPkt.list().iterator();
					Object[] fuelTheftResultSet = null;
					while(fuelTheftPktItr.hasNext())
					{
						fuelTheftResultSet = (Object[]) fuelTheftPktItr.next();
						String fuelTheft = (String)fuelTheftResultSet[0];

						if(fuelTheft!=null){
							profuelTheft = fuelTheft;

						}
					}


				//If Either Log Packet or LowFuelEvent Packet is received check for Fuel Theft
					if(lastPkt==1 && profuelTheft.equalsIgnoreCase("0"))
					{
						if(! (session.isOpen() ))
						{
							session = HibernateUtil.getSessionFactory().getCurrentSession();
							session.getTransaction().begin();
						}
					if( (prevFuelLevel-currentFuelLevelInDouble) >= 9 )
					{
							//Update Asset table with probable FuelTheft event
							Query query3 = session.createQuery("update AssetEntity set description=1 " +
									" where serial_Number ='"+serialNumber+"' and active_status=1 ");
							int rowsAffected1 = query3.executeUpdate();


							if(rowsAffected1>0)
							{

								infoLogger.info(serialNumber+":"+transactionTime+"  :"+"Don't Generate Fuel Theft alert wait for the next log packet and Update Asset table with probable FuelTheft event: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+prevFuelLevel);
							}
						}

					else if( (prevFuelLevel-currentFuelLevelInDouble) <= -9 )
					{
						Query query2 = session.createQuery("update AssetEventEntity set activeStatus=0, eventClosedTime= '"+transactionTime+"' " +
								" where eventId="+FuelTheftEventID+" and" +
								" serialNumber ='"+serialNumber+"' and activeStatus=1 ");
						int rowsAffected = query2.executeUpdate();
						if(rowsAffected>0){
							infoLogger.info(serialNumber+":"+transactionTime+"  :"+"Nullify the Fuel Theft Alert: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+prevFuelLevel);
						}
						//If no fuel theft alert already exists for that machine
						else {
							infoLogger.info(serialNumber+":"+transactionTime+"  :"+" Do nothing for fuel theft Continue processing packet: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+prevFuelLevel);
						}
					}

					//(Difference> -9) && (Difference < 9)
					else
					{
						infoLogger.info(serialNumber+":"+transactionTime+"  :"+" Do nothing for fuel theft Continue processing packet: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+prevFuelLevel);
					}
				}

					//If Machine is marked as Probable Fuel theft
					else if(lastPkt==1 && profuelTheft.equalsIgnoreCase("1"))
					{
						//Get the 2nd privious log packet From the AMH table
						int lastPkt1=0;
						double prevFuelLevel1=0.0;


						Query last1HourPkt1 = session.createQuery(" select b.parameterValue, a.transactionTime from AssetMonitoringHeaderEntity a, " +
								" AssetMonitoringDetailEntity b, MonitoringParameters c " +
								" where a.serialNumber='"+serialNumber+"'" +
								" and a.recordTypeId=3 " +
								" and a.transactionTime = (select max(transactionTime) from  AssetMonitoringHeaderEntity where serialNumber='"+serialNumber+"'" +
								" and transactionTime < '"+transactionTimestamp+"' and recordTypeId=3 )" +
								" and a.transactionNumber=b.transactionNumber " +
								" and b.parameterId = c.parameterId " +
								" and c.parameterName='"+FuelLevel+"'");
						Iterator last1HourPktItr1 = last1HourPkt1.list().iterator();
						Object[] fuelResultSet1 = null;
						while(last1HourPktItr1.hasNext())
						{
							lastPkt1=1;
							fuelResultSet1 = (Object[]) last1HourPktItr1.next();
							String fuelLevel1 = (String)fuelResultSet1[0];

							if(fuelLevel1!=null){
								prevFuelLevel1 = Double.parseDouble(fuelLevel1);

							}

						}

						if( (prevFuelLevel1-currentFuelLevelInDouble) >= 9 )
						{
							//Generate FuelTheft event
							EventEntity evt = null;
							Query q = session.createQuery("from EventEntity where eventId="+FuelTheftEventID);
							Iterator itr = q.list().iterator();
							while(itr.hasNext())
							{
								evt = (EventEntity) itr.next();
							}

							if(evt!=null)
							{
								eventIdValueMap = new HashMap<EventEntity, String>();
								eventIdValueMap.put(evt, "1");

								infoLogger.info(serialNumber+":"+transactionTime+"  :"+"2nd packet Generate Fuel Theft alert: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+prevFuelLevel);
								eventDetails.alertGenerationLogic(serialNumber, transactionTime, eventIdValueMap, true, true, latValue, longValue, null, serviceScheduleId, currentGPSfix);
							}
							//Nullify the Probable Fuel Theft Mark for the machine/Update the the Asset Table
							if(! (session.isOpen() ))
							{
								session = HibernateUtil.getSessionFactory().getCurrentSession();
								session.getTransaction().begin();
							}
							Query query4 = session.createQuery("update AssetEntity set description=0 " +
									" where serial_Number ='"+serialNumber+"' and active_status=1 ");
							int rowsAffected2 = query4.executeUpdate();


							if(rowsAffected2>0)
							{
								infoLogger.info(serialNumber+":"+transactionTime+"  :"+"2nd packet After Generated the fuel Theft Update Asset table with nullify probable FuelTheft event: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+prevFuelLevel1);
							}
						}



						else if( (prevFuelLevel1-currentFuelLevelInDouble) <= -9 )
						{
							Query query2 = session.createQuery("update AssetEventEntity set activeStatus=0, eventClosedTime= '"+transactionTime+"' " +
									" where eventId="+FuelTheftEventID+" and" +
									" serialNumber ='"+serialNumber+"' and activeStatus=1 ");
							int rowsAffected = query2.executeUpdate();
							if(rowsAffected>0){

								infoLogger.info(serialNumber+":"+transactionTime+"  :"+"2nd packet Nullify the Fuel Theft Alert: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+prevFuelLevel1);
							}
							//If no fuel theft alert already exists for that machine
							else {

								infoLogger.info(serialNumber+":"+transactionTime+"  :"+"2nd packet Do nothing for fuel theft Continue processing packet: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+prevFuelLevel1);
							}

						}

						//(Difference> -9) && (Difference < 9)
						else
						{
							infoLogger.info(serialNumber+":"+transactionTime+"  :"+"2nd packet Do nothing for fuel theft Continue processing packet: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+prevFuelLevel1);
						}
						//Nullify the Probable Fuel Theft Mark for the machine/Update the the Asset Table
						if(! (session.isOpen() ))
						{
							session = HibernateUtil.getSessionFactory().getCurrentSession();
							session.getTransaction().begin();
						}
						//DefectId:20140814 @Suprava Consider first three packet
						Query query4 = session.createQuery("update AssetEntity set description=0 " +
								" where serial_Number ='"+serialNumber+"' and active_status=1 ");
						int rowsAffected2 = query4.executeUpdate();


						if(rowsAffected2>0)
						{
							infoLogger.info(serialNumber+":"+transactionTime+"  :"+"2nd packet After Generated the fuel Theft Update Asset table with nullify probable FuelTheft event: Curr FuelLevel:"+currentFuelLevelInDouble+"; PrevFuelLevel:"+prevFuelLevel1);
						}
						//DefectId:20140814 End

					}



			}
			//DefectId:20140806 End
			//End of comment line @Suprava 2014-08-04 Not to Put in Production	

			if(! (session.isOpen() ))
            { 
                session = HibernateUtil.getSessionFactory().getCurrentSession();
                session.getTransaction().begin();
			}
            }*/
		}

		catch(Exception e)
		{
			fLogger.fatal( parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"  :"+"Exception :"+e);
			
			Writer result = new StringWriter();
    	    PrintWriter printWriter = new PrintWriter(result);
    	    e.printStackTrace(printWriter);
    	    String err = result.toString();
    	    fLogger.fatal( parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+"Exception trace: "+err);
    	    try 
    	    {
    	    	printWriter.close();
        	    result.close();
			} 
    	    
    	    catch (IOException e1) 
    	    {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	    
			e.printStackTrace();
		}

		finally
		{
			//Df20150107 - Rajani Nagaraju - Handle Session Closed Exception
			if(session.isOpen())
			{
				if(session.getTransaction().isActive())
				{
	
					session.getTransaction().commit();
				}
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}
			
			iLogger.info(parser.getSerialNumber().substring(3)+":"+parser.getTransactionTime()+" Child Thread Exiting ");

		}

		return "SUCCESS";
	}
	
	
	
	public String generateAlertsNew(XmlParser parser, String serialNumber, String transactionTime, String currentLat, String currentLong, 
					String currentGPSFix, String currentInternalBatteryLevel, String prevTxnIgnitionStatus, String prevTxnEngnieStatus,
					String prevTxnLat, String prevTxnLong, int prevLogtransactionNumber, String prevLogFuelLevelFromMOOL)
	{
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
		String status="SUCCESS";
		
		iLogger.info("AGS:"+serialNumber+":"+transactionTime+" AGS Processing Logic - START");
		
		boolean skip_service_alerts_for_ignitionON= false;
		
		try
		{
			/*Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();*/
			Session session = HibernateUtil.getSessionFactory().openSession();
			//session.beginTransaction();
			
			try
			{
				HashMap<String, HashMap<String,String>> hashMap = parser.getParamType_parametersMap();
				HashMap<String, String> logParamValuesMap = (HashMap<String, String>) hashMap.get("LogParameters");
				HashMap<String, String> eventParamValuesMap = (HashMap<String, String>) hashMap.get("EventParameters");
				EventDetailsBO eventDetailsBoObj = new EventDetailsBO();
				Timestamp currentTime = new Timestamp(new Date().getTime());
				Properties prop=null;
				SimpleDateFormat dateFormat = null;
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Read from property file - START");
				try
				{
					prop= StaticProperties.getConfProperty();
					dateFormat = StaticProperties.dateFormat;
				}
				catch(Exception e)
				{
					fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+" -Error in intializing property File :"+e);
					return "FAILURE";
				}
				String TowAwayEventId = prop.getProperty("TowAwayEventId");
				String InternalBatteryLowEventId= prop.getProperty("InternalBatteryLowEventId");
				String UsageOutsideOHEventId=prop.getProperty("UsageOutsideOHEventId");
			//	String LandmarkEventTypeId=prop.getProperty("LandmarkEventTypeId");
				String LandmarkArrival=prop.getProperty("LandmarkArrival");
				String LandmarkDeparture=prop.getProperty("LandmarkDeparture");
				String ExternalBatteryRemovedEventId=prop.getProperty("ExternalBatteryRemovedEventId");
				String versionNum=null;
				if(prop.getProperty("deployenvironment").equalsIgnoreCase("PROD"))
					versionNum = prop.getProperty("VersionNumberProd");
				else
					versionNum = prop.getProperty("VersionNumberSIT");
				String FuelTheftEventID = prop.getProperty("FuelTheftEventID");
				
				//************************************ Case0: Check for valid input parameters
				if(currentLat==null || currentLong==null)
				{
					HashMap<String, String> locparamValuesMap = (HashMap<String, String>) hashMap.get("Location");
					currentLat=locparamValuesMap.get("Latitude");
					double latValueInDouble = Double.valueOf(currentLat.replace('N', ' ').replace('E', ' ').trim());
					Double convertedLatValueInDouble = (((latValueInDouble/100)%1)*2+(latValueInDouble/100)*3)/3;
					currentLat = convertedLatValueInDouble.toString();
					
					currentLong=locparamValuesMap.get("Longitude");
					double longValueInDouble = Double.valueOf(currentLong.replace('N', ' ').replace('E', ' ').trim());
					Double convertedLongValueInDouble = (((longValueInDouble/100)%1)*2+(longValueInDouble/100)*3)/3;
					currentLong = convertedLongValueInDouble.toString();
				}
				
				//************************************* Case 1: APPLICATION Alert Closure for Tow-away ************************************
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+": APPLICATION Alert Closure for Tow-away - START");
				session.beginTransaction();
				//20160721 - added by Suresh check for not generating service alerts for IgnitionON Event
				if( (hashMap.containsKey("EventParameters")) && 
						  ( (eventParamValuesMap.containsKey("IGNITION_ON")))){
								skip_service_alerts_for_ignitionON = true;
						  }
				if( (hashMap.containsKey("EventParameters")) && 
				  ( (eventParamValuesMap.containsKey("IGNITION_ON")) && ( eventParamValuesMap.get("IGNITION_ON").equalsIgnoreCase("1")) ) )
				{
					Query towAwayQuery = session.createQuery(" select a from AssetEventEntity a join a.eventId b " +
							" where a.serialNumber='"+serialNumber+"' and b.eventId='"+TowAwayEventId+"' and a.activeStatus=1");
					Iterator towAwayItr = towAwayQuery.list().iterator();
					while(towAwayItr.hasNext())
					{
						AssetEventEntity assetEvent = (AssetEventEntity)towAwayItr.next();
						assetEvent.setActiveStatus(0);
						assetEvent.setEventClosedTime(parser.getTransactionTime());
						
						assetEvent.setCreated_timestamp(currentTime);
						session.update(assetEvent);
						
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Close Tow-away alert : AssetEventId: "+assetEvent.getAssetEventId());
						
					}
				}
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+": APPLICATION Alert Closure for Tow-away - END");
				
				
				//************************************* Case 2: FIRMWARE Alert - Health Alerts Generation / Closure ***************************
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+": FIRMWARE Alerts Generation / Closure for Health Alerts - START");
				
				if( (hashMap.containsKey("EventParameters")) && !((eventParamValuesMap.containsKey("IGNITION_ON")) || (eventParamValuesMap.containsKey("ENGINE_ON"))) )
				{
					//-------------- STEP1: Get the event Ids corresponding to the received event - This will take care of Ignition and Engine packets as there is no event corresponding to the same
					List<String> eventParameterList = new LinkedList<String>();
					HashMap<EventEntity, String> eventIdValueMap = new HashMap<EventEntity, String>();
					HashMap<Integer,EventEntity> freqEventObjMap = new HashMap<Integer,EventEntity>();
					List<Integer> lowFuelFreqList = new LinkedList<Integer>();
					
					for(int i=0; i<eventParamValuesMap.size(); i++)
					{
						eventParameterList.add((String)eventParamValuesMap.keySet().toArray()[i]);
					}
					Query eventIdQuery = session.createQuery("select a.EventId,c.parameterName from EventCondition a, ConditionsEntity b, " +
							" MonitoringParameters c where a.ConditionId = b.ConditionId and b.ParameterId = c.parameterId " +
							" and c.parameterName in (:list)").setParameterList("list", eventParameterList);
					Iterator eventIdItr = eventIdQuery.list().iterator();
					Object[] eventIdresult = null;
					
											
					//-------------- STEP2: Generate OR close the received alert
					while(eventIdItr.hasNext())
					{
						eventIdresult = (Object[])eventIdItr.next();
						EventEntity eventEntity = (EventEntity)eventIdresult[0];
						String paramName = eventIdresult[1].toString();
						
						//----------------------- Closure of alert
						if( eventParamValuesMap.get(paramName).equalsIgnoreCase("0"))
						{
							
							Query nullifyAlertQ=null;
							
							//---------- Closure of Internal Battery Low event if External Battery closure is received.
							//This is dependent on External Battery because once the external battery is connected, Firmware cannot determine the battery level is from Internal Battery/External Battery
							// And since by design once the external battery is connected, internal battery should get charged automatically - Alert for Internal Battery can be closed at this point irrespective of battery level
							if(eventEntity.getEventId()== (Integer.parseInt(ExternalBatteryRemovedEventId)) )
							{
								nullifyAlertQ = session.createQuery(" from AssetEventEntity where eventId in ("+eventEntity.getEventId()+"," +
										InternalBatteryLowEventId+") and" +
										" serialNumber ='"+serialNumber+"' and activeStatus=1 ");
							}
							
							else
							{
								nullifyAlertQ = session.createQuery(" from AssetEventEntity where eventId="+eventEntity.getEventId()+" and" +
																	" serialNumber ='"+serialNumber+"' and activeStatus=1 ");
							}
							
							Iterator nullifyAlertItr = nullifyAlertQ.list().iterator();
							while(nullifyAlertItr.hasNext())
							{
								AssetEventEntity assetEvent = (AssetEventEntity)nullifyAlertItr.next();
								assetEvent.setActiveStatus(0);
								assetEvent.setEventClosedTime(parser.getTransactionTime());
								assetEvent.setCreated_timestamp(currentTime);
								session.update(assetEvent);
								
								iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Close the alert :"+assetEvent.getEventId().getEventId()+" AssetEventId: "+assetEvent.getAssetEventId());
							}
							
							
						}
						
						//----------------------- Generation of new alert (Any Alert other than Low Fuel Level)
						else
						{
							//Supress the Multiple alert generation (Same event with same transaction timestamp)
							//DF20151119 - Rajani Nagaraju - Condition Check on active status not required. There was an issue where in the generation pkt is processed and then
							//closure packet is processed, and the same generation pkt was processed again since persitence returned FAILURE and hence same pkt in the seq of
							//Generation-Closure was processed 46 times and hence 46 alerts got generated for the same pkt. Hence removing the check for active status
							/*String duplicatePkt = "SELECT ae.serialNumber FROM AssetEventEntity ae WHERE ae.serialNumber='"+serialNumber+"'" +
												" AND ae.eventGeneratedTime = '"+transactionTime+"' and ae.activeStatus=1";*/
							String duplicatePkt = "SELECT ae.serialNumber FROM AssetEventEntity ae WHERE ae.serialNumber='"+serialNumber+"'" +
									" AND ae.eventGeneratedTime = '"+transactionTime+"'";
							if( eventEntity.getEventId()==16 || eventEntity.getEventId()==17)
								duplicatePkt=duplicatePkt+" and ae.eventId in (16,17) ";
							else
								duplicatePkt=duplicatePkt+" and ae.eventId="+eventEntity.getEventId();
							
							Query duplicatePktQ = session.createQuery(duplicatePkt);
							if(duplicatePktQ.list().size()>0)
							{
								//DF20151119 - Rajani Nagaraju - Changing the Logger statemtent to say pkt with same txn time and not the active status
								//iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" EventID "+eventEntity.getEventId() +" with active status 1 already exists - Duplicate Packet Received");
								iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" EventID "+eventEntity.getEventId() +" with same packet details already exists - Duplicate Packet Received");
								continue;
							}
							
							
							//For LowFuelEvent compare the frequency and then generate the alert
							if( eventEntity.getEventId()==16 || eventEntity.getEventId()==17)
							{
								freqEventObjMap.put(eventEntity.getFrequency(), eventEntity);
								lowFuelFreqList.add(eventEntity.getFrequency());
							}
							
							else //Generate the alert
							{
								eventIdValueMap.put(eventEntity, "1");
								iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" Generate the Alert for the EventID "+eventEntity.getEventId());
								
								
								//************************************* Case 3: APPLICATION Alert - Internal Battery Low Alert Generation/Closure  ***************************
								//Generate APPLICATION Alert - Internal Battery Low Alert Generation/Closure - If the external Battery is removed and internal battery charge < 50%
								// This dependency on External Battery removal is - In normal condition, FirmWare cannot determine whether the battery charge is w.r.t Internal Battery/External Battery when both are ON
								// Once the External Battery is removed, Firmware will continously check for Internal Battey Status and send the alert 'EXTERNAL_BATTERY_REMOVED' when internal battery goes less than 50%
								if( (eventEntity.getEventId()== (Integer.parseInt(ExternalBatteryRemovedEventId)) ) &&
										( eventParamValuesMap.get(paramName).equalsIgnoreCase("1")) )
								{
									
									iLogger.info("AGS:"+serialNumber+":"+transactionTime+" APPLICATION Alert Generation/Closure for Internal Battery Low - START");
									double internalBatteryPercentage = Double.parseDouble(currentInternalBatteryLevel);
									
									if(internalBatteryPercentage<50)
									{
										//--------- get the eventEntity for Internal Battery low
										Query internalBatterEventQ= session.createQuery(" from EventEntity where eventId="+ Integer.parseInt(InternalBatteryLowEventId));
										Iterator internalBatteryEventItr = internalBatterEventQ.list().iterator();
										EventEntity internalBatLowEvent =null;
										if(internalBatteryEventItr.hasNext())
										{
											internalBatLowEvent = (EventEntity)internalBatteryEventItr.next();

										}
																				
										eventIdValueMap.put(internalBatLowEvent, currentInternalBatteryLevel);
										iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" Generate the Alert for the Interal Battery Level Low, Received battery level:"+internalBatteryPercentage);
									}
									
									iLogger.info("AGS:"+serialNumber+":"+transactionTime+" APPLICATION Alert Generation/Closure for Internal Battery Low - END");
								}
							}
						}
						
					}
					
					//------------- Generation of LowFuelEvent
					if( ! (freqEventObjMap==null || freqEventObjMap.isEmpty()) )
					{
						//get the current fuelLevel in volts
						String FuelLevelValue = logParamValuesMap.get("FuelLevel");
						ParameterUOMmanagement getParamValue = new ParameterUOMmanagement();
						String currFuelLevel = getParamValue.getParameterValue(FuelLevelValue);
						double fuelLevelInDouble = Double.parseDouble(currFuelLevel);
						EventEntity eventToGenerate = null;
						
						if(! (session.isOpen() ))
						{
							/*session = HibernateUtil.getSessionFactory().getCurrentSession();
							session.getTransaction().begin();*/
							session = HibernateUtil.getSessionFactory().openSession();
							session.beginTransaction();
						}
						
						Collections.sort(lowFuelFreqList);
						
						for(int i=0; i<lowFuelFreqList.size(); i++)
						{
							if(fuelLevelInDouble<lowFuelFreqList.get(i))
							{
								eventToGenerate = freqEventObjMap.get(lowFuelFreqList.get(i));
								eventIdValueMap.put(eventToGenerate, "1");
								iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" Generate the Alert for the EventID "+eventToGenerate.getEventId());
								break;
							}
						}
						
						if(eventToGenerate!=null)
						{
							String eventToClose = null;
							//Close the Previous Fuel alert
							if(eventToGenerate.getEventSeverity().equalsIgnoreCase("RED"))
								eventToClose="YELLOW";
							else
								eventToClose="RED";
							
							Query lowFuelClosureQ = session.createQuery(" select ae from AssetEventEntity ae, EventEntity event " +
									" where ae.serialNumber='"+serialNumber+"'" +
									" and ae.activeStatus=1 and ae.eventId= event.eventId and event.eventId in (16,17)" +
									" and event.eventSeverity like '"+eventToClose+"'");
							Iterator lowFuelClosureItr = lowFuelClosureQ.list().iterator();
							while(lowFuelClosureItr.hasNext())
							{
								AssetEventEntity assetEvent = (AssetEventEntity)lowFuelClosureItr.next();
								assetEvent.setActiveStatus(0);
								assetEvent.setEventClosedTime(parser.getTransactionTime());
								assetEvent.setCreated_timestamp(currentTime);
								session.update(assetEvent);
								
								iLogger.info("AGS:"+serialNumber+":"+transactionTime+"  :"+"Close the Low Fuel alert :"+assetEvent.getEventId()+" AssetEventId: "+assetEvent.getAssetEventId());
							}
							
						}
					
					}
					
					String healthAlertStatus=null;
					if(eventIdValueMap!=null && eventIdValueMap.size()>0)
						healthAlertStatus=eventDetailsBoObj.alertGenerationLogicNew(serialNumber,parser.getTransactionTime(),eventIdValueMap, true, true, currentLat, currentLong,null, 0, currentGPSFix, null);
					
					if(healthAlertStatus!=null && healthAlertStatus.equalsIgnoreCase("FAILURE")){
						status="FAILURE";
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+" FIRMWARE Alerts Generation / Closure for Health Alerts Status::"+status);	
					}
				
				}
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+" FIRMWARE Alerts Generation / Closure for Health Alerts - END");
				
				
				
				
				
				//************************************* Case 4: APPLICATION Alert - Machine Usage outside Operating hours  ***************************
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+" APPLICATION Alert - Machine Usage outside operating hours - START");
				if(! (session.isOpen() ))
				{
					/*session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();*/
					session = HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction();
				}
				
				//Generate Machine Usage outside operating hour alert only if the ignition is ON and the received packet is not a HELLO packet
				if(prevTxnIgnitionStatus.equalsIgnoreCase("1") && !(eventParamValuesMap!=null && eventParamValuesMap.containsKey("HELLO")) )
				{
					String startDate=null;
					String endDate=null;

					Date txnDate = parser.getTransactionTime();
					int txnHour = parser.getTransactionTime().getHours();
					int txnMin = parser.getTransactionTime().getMinutes();
					String txnDateInString = dateFormat.format(txnDate);

					//Get TxnDate -1 
					Calendar cal = Calendar.getInstance();
					cal.setTime(txnDate);
					cal.add(Calendar.DAY_OF_YEAR,-1);
					Date oneDayBefore= cal.getTime();
					String prevTxnDateInString = dateFormat.format(oneDayBefore);

					//Get TxnDate +1
					Calendar cal1 = Calendar.getInstance();
					cal1.setTime(txnDate);
					cal1.add(Calendar.DAY_OF_YEAR,1);
					Date oneDayAfter= cal1.getTime();
					String nextTxnDateInString = dateFormat.format(oneDayAfter);


					if(txnHour==18)
					{
						if(txnMin<30)
						{
							startDate=prevTxnDateInString;
							endDate=txnDateInString;
						}
						else
						{
							startDate =txnDateInString;
							endDate=nextTxnDateInString;
						}
					}

					else if (txnHour<18)
					{
						startDate=prevTxnDateInString;
						endDate=txnDateInString;
					}

					else if (txnHour>18)
					{
						startDate =txnDateInString;
						endDate=nextTxnDateInString;
					}
					
					
					
					int opHourAlertPresent=0;
					//Generate Machine usage outside operating hour only once in a day 
					Query assetEventQ = session.createQuery(" select a.assetEventId from AssetEventEntity a join a.eventId b " +
							" where a.serialNumber='"+serialNumber+"' and " +
							" b.eventId ='"+UsageOutsideOHEventId+"' and a.eventGeneratedTime >= '"+startDate+" 18:30:00' and " +
							" a.eventGeneratedTime<='"+endDate+" 18:29:59'");
					Iterator assetEvItr = assetEventQ.list().iterator();
					while(assetEvItr.hasNext())
					{
						int assetEventId = (Integer)assetEvItr.next();
						opHourAlertPresent =1;
						iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Machine Usage Outside OP Hour Alert already generated for the day");
					}
					
					
					if(opHourAlertPresent==0)
					{
						Timestamp operatingStartTime = null;
						Timestamp operatingEndTime = null;

						//Stage1: Get the Operating hours defined for the machine
						Query machineLevelOpHrQ = session.createQuery(" from AssetExtendedDetailsEntity where serial_number='"+serialNumber+"'");
						Iterator machineLevelOpHrItr = machineLevelOpHrQ.list().iterator();
						if(machineLevelOpHrItr.hasNext())
						{
							AssetExtendedDetailsEntity extendedDetails = (AssetExtendedDetailsEntity) machineLevelOpHrItr.next();
							operatingStartTime = extendedDetails.getOperatingStartTime();
							operatingEndTime = extendedDetails.getOperatingEndTime();
						}
						
						//Stage2: Get the Operating hours defined at tenancy level
						if(operatingStartTime==null)
						{
							Query tenancyLevelopHrQ = session.createQuery(" select b.tenancy_id from AssetEntity a, AccountTenancyMapping b where " +
									" a.primary_owner_id = b.account_id and a.serial_number='"+serialNumber+"'");

							Iterator tenancyLevelopHrItr = tenancyLevelopHrQ.list().iterator();
							if(tenancyLevelopHrItr.hasNext())
							{
								TenancyEntity tenancy = (TenancyEntity) tenancyLevelopHrItr.next();
								operatingStartTime = tenancy.getOperating_Start_Time();
								operatingEndTime = tenancy.getOperating_End_Time();
							}
						}
						
						//Stage3: Get the Operating hours defined at Machine profile Level
						if(operatingStartTime==null)
						{
							Query profileLevelOpHrQ = session.createQuery("select a.operating_Start_Time, a.Operating_end_time from AssetGroupProfileEntity a, AssetEntity b, ProductEntity c" +
									" where a.asset_grp_id = c.assetGroupId and b.productId = c.productId and b.serial_number='"+serialNumber+"'");
							Iterator profileLevelOpHrItr = profileLevelOpHrQ.list().iterator();
							Object[] result = null;
							if(profileLevelOpHrItr.hasNext())
							{
								result = (Object[]) profileLevelOpHrItr.next();
								operatingStartTime = (Timestamp)result[0];
								operatingEndTime = (Timestamp)result[1];
							}
						}
					
						if(operatingStartTime!=null && operatingEndTime!=null)
						{
							SimpleDateFormat sdf = StaticProperties.timeFormat;
							String opStartTime= sdf.format(operatingStartTime);
							String opEndTime = sdf.format(operatingEndTime);
							String txnTime = sdf.format(parser.getTransactionTime());
							
							
							if(opStartTime!=null && opEndTime!=null && ( (txnTime.compareTo(opStartTime)<0) || (txnTime.compareTo(opEndTime)>0) ))
							{
								//get the event Id for operating hours event
								EventEntity opHrEvent = null;
								Query opHrEventQ = session.createQuery(" from EventEntity where eventId = '"+UsageOutsideOHEventId+"'");
								Iterator opHrEventItr = opHrEventQ.list().iterator();
								if(opHrEventItr.hasNext())
									opHrEvent = (EventEntity) opHrEventItr.next();
	
								HashMap<EventEntity, String> eventIdValueMap = new HashMap<EventEntity, String>();
								eventIdValueMap.put(opHrEvent, Integer.toString(txnHour));
								
								iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" Generate Machine Usage Outside OP Hour Alert. Defined Start Time: "+opStartTime+", Defined End Time:"+opEndTime);
								String OpHourAlertStatus=null;
								OpHourAlertStatus= eventDetailsBoObj.alertGenerationLogicNew(serialNumber,parser.getTransactionTime(),eventIdValueMap, false, false, currentLat, currentLong,null, 0, currentGPSFix, null);
								
								if(OpHourAlertStatus!=null && OpHourAlertStatus.equalsIgnoreCase("FAILURE"))
								{
									status = "FAILURE";
									
									iLogger.info("AGS:"+serialNumber+":"+transactionTime+" APPLICATION Alert - Machine Usage outside operating hours Status::"+status);
									
								}
							}
						}
					}
				}
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+" APPLICATION Alert - Machine Usage outside operating hours - END");
				
				
				//************************************* Case 4: APPLICATION Alert - Landmark Alert Generation/Closure ***************************
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+" APPLICATION Alert - Landmark Alert Generation/Closure - START");
				if(! (session.isOpen() ))
				{
					/*session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();*/
					session = HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction();
				}
				
				//Check for the Landmark Alerts only if the received packet is not an HELLO packet
			//	if( (!(eventParamValuesMap==null || eventParamValuesMap.containsKey("HELLO")) ) && (parser.getMessageId().equalsIgnoreCase("010")) )
				if(parser.getMessageId().equalsIgnoreCase("010"))
				{
					HashMap<EventEntity, String> eventIdValueMap = null;
										
					List<Integer> landmarkIdList = new LinkedList<Integer>();
				
					//get the EventEntity for Landmark Arrival and departure
					/*Query landmarkEventQuery = session.createQuery(" from EventEntity where eventTypeId="+LandmarkEventTypeId);
					Iterator landmarkEventItr = landmarkEventQuery.list().iterator();
					EventEntity departureEntity = null;
					EventEntity arrivalEntity = null;

					while(landmarkEventItr.hasNext())
					{
						EventEntity landmarkEvent = (EventEntity)landmarkEventItr.next();
						
						if(landmarkEvent.getEventName().equalsIgnoreCase("Departure"))
							departureEntity = landmarkEvent;
						
						else if (landmarkEvent.getEventName().equalsIgnoreCase("Arrival"))
							arrivalEntity = landmarkEvent;
						
					}*/
					
					
					//Get the List of Landmarks for which the machine is tagged to
					Query assetLandmarkQ = session.createQuery(" select a from LandmarkAssetEntity a, LandmarkEntity b " +
							" where a.Serial_number = '"+serialNumber+"' and a.Landmark_id=b.Landmark_id and b.ActiveStatus=1 ");
					Iterator assetLandmarkItr = assetLandmarkQ.list().iterator();
					while(assetLandmarkItr.hasNext())
					{
						LandmarkAssetEntity landmarkAsset = (LandmarkAssetEntity) assetLandmarkItr.next();
						landmarkIdList.add(landmarkAsset.getLandmark_id().getLandmark_id());
					}
					
					//Iterate through each landmark to find whether the machine has crossed the landmark
					Geofencing geo = new Geofencing();
					for(int k=0; k<landmarkIdList.size(); k++)
					{
						if(! (session.isOpen() ))
						{
							/*session = HibernateUtil.getSessionFactory().getCurrentSession();
							session.getTransaction().begin();*/
							session = HibernateUtil.getSessionFactory().openSession();
							session.beginTransaction();
						}
						
						EventEntity departureEntity = null;
						EventEntity arrivalEntity = null;
						
						LandmarkEntity landmark=null;
						Query landmarkQ = session.createQuery(" from LandmarkEntity where Landmark_id="+landmarkIdList.get(k));
						Iterator landmarkItr = landmarkQ.list().iterator();
						if(landmarkItr.hasNext())
						{
							landmark = (LandmarkEntity)landmarkItr.next();
						}
						
						
						//------------------ Step1: If the landmark is not defined with latitude or longitude, just skip that landmark
						if( (landmark.getLatitude()==null) || (landmark.getLatitude().equals("0")) )
							continue;
						double landmarkLat = Double.parseDouble(landmark.getLatitude());

						if( (landmark.getLongitude()==null) || (landmark.getLongitude().equals("0")) )
							continue;
						double landmarkLong = Double.parseDouble(landmark.getLongitude());

						double radius = landmark.getRadius();
						
						int wasPreviouslyPresent =0;
						int isCurrentlyPresent =0;
						
						if(prevTxnLat!=null && prevTxnLong!=null && currentLat!=null && currentLong!=null)
						{
							//Get the AssetEntity for the received VIN
							AssetEntity asset = null;
							Query assetQ = session.createQuery(" from AssetEntity where serial_number='"+serialNumber+"'");
							Iterator assetItr = assetQ.list().iterator();
							if(assetItr.hasNext())
							{
								asset = (AssetEntity) assetItr.next();
							}
							
							
							//------------------ Step2: Find whether the machine was in landmark
							double prevLanddistance = geo.calDistance(landmarkLat, landmarkLong, Double.parseDouble(prevTxnLat), Double.parseDouble(prevTxnLong));
							if(prevLanddistance<radius)
							{
								wasPreviouslyPresent =1;
								iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Landmark Alert - Machine Previously Present in landmark :"+landmark.getLandmark_id());
							}
							
							//------------------ Step3: Find whether the machine is in landmark
							double currLanddistance = geo.calDistance(landmarkLat, landmarkLong, Double.parseDouble(currentLat), Double.parseDouble(currentLong));
							if(currLanddistance<radius)
							{
								isCurrentlyPresent =1;
								iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Landmark Alert - Machine Currently Present in landmark :"+landmark.getLandmark_id());
							}
							
							//--------------- Step4: Machine residing inside the landmark
							if(wasPreviouslyPresent==1 && isCurrentlyPresent==1)
							{
								iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Landmark Alert - Machine residing in the same landmark location:"+landmark.getLandmark_id());
							}
							
							//----------------- Step5: Check for the Closure of Previous landmark alerts
							if ((wasPreviouslyPresent==0 && isCurrentlyPresent==1) || (wasPreviouslyPresent==1 && isCurrentlyPresent==0) )
							{
								Query landmarkClosureQ = session.createQuery("select assetEvent from LandmarkLogDetailsEntity llog " +
										" join llog.assetEventId assetEvent " +
										" where assetEvent.activeStatus=1 and llog.landmarkId="+landmark.getLandmark_id());
								Iterator landmarkClosureItr = landmarkClosureQ.list().iterator();
								while(landmarkClosureItr.hasNext())
								{
									AssetEventEntity assetEvent = (AssetEventEntity)landmarkClosureItr.next();
									assetEvent.setActiveStatus(0);
									assetEvent.setEventClosedTime(parser.getTransactionTime());
									assetEvent.setCreated_timestamp(currentTime);
									session.update(assetEvent);
									
									iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Landmark Alert - Close the Landmark Alert for the landmark :"+landmark.getLandmark_id()+" and AssetEventId:"+assetEvent.getAssetEventId());
								}
							}
							
							//--------------- Step6: Machine arrived to the landmark
							if(wasPreviouslyPresent==0 && isCurrentlyPresent==1)
							{
								if(landmark.getIsArrival()==1)
								{
									//Get the Landmark Arrival Event
									Query landmarkArrivalEventQ = session.createQuery(" from EventEntity where eventName='"+LandmarkArrival+"'");
									Iterator landmarkArrivalEventItr = landmarkArrivalEventQ.list().iterator();
									if(landmarkArrivalEventItr.hasNext())
									{
										arrivalEntity = (EventEntity)landmarkArrivalEventItr.next();
									}
									
									
									eventIdValueMap = new HashMap<EventEntity, String>();
									iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Landmark Alert - Generate Machine Arrival alert for the landmark :"+landmark.getLandmark_id());
									eventIdValueMap.put(arrivalEntity, landmark.getLandmark_Name());
									
									String landmarkAlertStatus = eventDetailsBoObj.alertGenerationLogicNew(serialNumber, parser.getTransactionTime(), eventIdValueMap, false, false, currentLat, currentLong, landmark, 0, currentGPSFix, "Arrival");
									//Insert into Landmark log details table - Moved to EventDetailsBO
									
									if(landmarkAlertStatus!=null && landmarkAlertStatus.equalsIgnoreCase("FAILURE"))
									{
										status="FAILURE";
										
										iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Landmark Alert - Generate Machine Arrival alert for the landmark Status::"+status);
										
									}
								}
								
								else
								{
									LandmarkLogDetailsEntity landmarkLogs = new LandmarkLogDetailsEntity();
									landmarkLogs.setSerialNumber(asset);
									landmarkLogs.setLandmarkId(landmark);
									landmarkLogs.setTransactionTimestamp(parser.getTransactionTime());
									landmarkLogs.setMachineLandmarkStatus("Arrival");
									session.save(landmarkLogs);
								}
								
							}
							
							//---------------- Step7: Machine Departed from the landmark
							if(wasPreviouslyPresent==1 && isCurrentlyPresent==0)
							{
								if( landmark.getIsDeparture()==1)
								{
									//Get the Landmark Deaprture Event
									Query landmarkDepEventQ = session.createQuery(" from EventEntity where eventName='"+LandmarkDeparture+"'");
									Iterator landmarkDepEventItr = landmarkDepEventQ.list().iterator();
									if(landmarkDepEventItr.hasNext())
									{
										departureEntity = (EventEntity)landmarkDepEventItr.next();
									}
									
									eventIdValueMap = new HashMap<EventEntity, String>();
									iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Landmark Alert - Generate Machine Departure for the landmark :"+landmark.getLandmark_id());
									eventIdValueMap.put(departureEntity, landmark.getLandmark_Name());
									
									String landmarkAlertStatus = eventDetailsBoObj.alertGenerationLogicNew(serialNumber, parser.getTransactionTime(), eventIdValueMap, false, false, currentLat, currentLong, landmark, 0, currentGPSFix,"Departure");
									//Insert into Landmark log details table - Moved to EventDetailsBO
									
									if(landmarkAlertStatus!=null && landmarkAlertStatus.equalsIgnoreCase("FAILURE"))
									{
										status="FAILURE";
										iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Landmark Alert - Generate Machine Departure alert for the landmark Status::"+status);	
									}
								}
								
								else
								{
									LandmarkLogDetailsEntity landmarkLogs = new LandmarkLogDetailsEntity();
									landmarkLogs.setSerialNumber(asset);
									landmarkLogs.setLandmarkId(landmark);
									landmarkLogs.setTransactionTimestamp(parser.getTransactionTime());
									landmarkLogs.setMachineLandmarkStatus("Departure");
									session.save(landmarkLogs);
								}
							}
						}
					}
					
					
					
				}
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+" APPLICATION Alert - Landmark Alert Generation/Closure - END");
				
				
				
				
				//************************************* Case 5: APPLICATION Alert - Service Alert Generation/Closure ***************************
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+" APPLICATION Alert - Service Alert Generation/Closure - START");
				
				if(!skip_service_alerts_for_ignitionON){
					/*if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
						session = HibernateUtil.getSessionFactory().openSession();
						session.beginTransaction();
					}*/
					
					int eventId=0,serviceScheduleId =0;
					AssetServiceScheduleGetReqContract reqObj = new AssetServiceScheduleGetReqContract();
					reqObj.setSerialNumber(serialNumber);
	
					List<AssetServiceScheduleGetRespContract> responseList = new AssetServiceScheduleImpl().getAssetserviceSchedule(reqObj);
					for(int i=0; i<responseList.size(); i++)
					{
						if(responseList.get(i).getHoursToNextService()!=null)
						{
							eventId = responseList.get(i).getEventId();
							serviceScheduleId = responseList.get(i).getServiceScheduleId();
						}
					}
					
					//DF20160330 - Rajani Nagaraju - Issue: Packets not getting deleted from alert_packet_details table
					//Because Exception is thrown at this point saying no operations allowed after the session is closed
					/*if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}*/
					
					if(session !=null && ! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().openSession();
						session.beginTransaction();
					}
					
					if(eventId!=0)
					{
						//----------------- STEP1: If any other active service alert is there for the VIN, nullify  the same before generating the new service alert
						List<AssetEventEntity> serviceAlertList = new EventDetailsBO().checkDuplicateAlert(serialNumber, 0, 1,1);
						//Query serviceAlertQuery = session.createQuery(" from AssetEventEntity where serialNumber='"+serialNumber+"' and eventTypeId=1 and activeStatus=1");
						//Iterator serviceAlertItr = serviceAlertQuery.list().iterator();
						
						Iterator serviceAlertItr = serviceAlertList.iterator();
						int generateAlert =1;
						/*while(serviceAlertItr.hasNext())
						{
							AssetEventEntity assetEvent = (AssetEventEntity) serviceAlertItr.next();
							//------------------- STEP2: Check for the Duplicate Service alert generation for the same service
							if( (assetEvent.getEventId().getEventId()==eventId) && (assetEvent.getServiceScheduleId()==serviceScheduleId) )
							{
								generateAlert=0;
							}
	
							else
							{
								//--------------- STEP3: Nullify the active service alert on this machine for previous service 
								assetEvent.setActiveStatus(0);
								assetEvent.setEventClosedTime(parser.getTransactionTime());
								session.update(assetEvent);
								iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Service Alert - Close the alert :"+assetEvent.getEventId()+" for the service schedule:"+serviceScheduleId+" - AssetEventId:"+assetEvent.getAssetEventId());
							}
						}*/
						Session session1 = new HibernateUtil().getSessionFactory().openSession();

						try{
							session1.beginTransaction();
							//					Keerthi : 20.07.2016 - service alert generation logic changes
							while(serviceAlertItr.hasNext())
							{
								AssetEventEntity assetEvent = (AssetEventEntity) serviceAlertItr.next();
								//------------------- STEP2: Check for the Duplicate Service alert generation for the same service
								//						case 1: asset_event table alreday has a higher schedule id - DO NOTHING
								if(assetEvent.getServiceScheduleId()>serviceScheduleId){
									generateAlert = 0;
								}
								//						case 2 : asset_event table has a lesser schedule id - CLOSE the previous and GENERATE THE ALERT
								else if (assetEvent.getServiceScheduleId()<serviceScheduleId){
									assetEvent.setActiveStatus(0);
									assetEvent.setEventClosedTime(parser.getTransactionTime());
									assetEvent.setCreated_timestamp(currentTime);
									session1.update(assetEvent);
									iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Service Alert - Close the alert :"+assetEvent.getEventId()+" for the service schedule:"+serviceScheduleId+" - AssetEventId:"+assetEvent.getAssetEventId());							
								}
								//						case 3 : asset_event schedule id = service_schedule_id
								else{
									//							case 3.a : asset_event table's event_id >= event_id - DO NOTHING
									if(assetEvent.getEventId().getEventId()>=eventId){
										generateAlert = 0;
									}
									//							case 3.b : asset_event table's event_id < event_id - CLOSE the previous one and GENERATE THE ALERT
									else{
										assetEvent.setActiveStatus(0);
										assetEvent.setEventClosedTime(parser.getTransactionTime());
										assetEvent.setCreated_timestamp(currentTime);
										session1.update(assetEvent);
										iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Service Alert - Close the alert :"+assetEvent.getEventId()+" for the service schedule:"+serviceScheduleId+" - AssetEventId:"+assetEvent.getAssetEventId());
									}
								}
							}
						}catch(Exception e){
							fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception :"+e);
							
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Exception::"+e);
						}
						finally{
							if(session1 !=null && session1.getTransaction().isActive()){
								session1.flush();
								session1.getTransaction().commit();
							}
							if(session1 !=null && session1.isOpen()){
								session1.close();
							}

						}
						
						//------------------- STEP4: Generate Service Alert
						if(generateAlert==1)
						{
							HashMap<String,String> parameterValueMap = hashMap.get("CumulativeOperatingHours");
							Double firmwareEngineHourDouble = Double.parseDouble((String) parameterValueMap.values().toArray()[0]);
							int firmwareEngineHour = firmwareEngineHourDouble.intValue();
	
							EventEntity serviceEvent = null;
							Query serviceEventQ = session.createQuery("from EventEntity where eventId="+eventId);
							Iterator serviceEventItr = serviceEventQ.list().iterator();
							if(serviceEventItr.hasNext())
							{
								serviceEvent = (EventEntity) serviceEventItr.next();
								HashMap<EventEntity, String> eventIdValueMap = new HashMap<EventEntity, String>();
								eventIdValueMap.put(serviceEvent, String.valueOf(firmwareEngineHour));
	
								iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Service Alert - Generate the alert for the service schedule:"+serviceScheduleId+" and EventID:"+serviceEvent.getEventId());
								String serviceScheduleStatus = eventDetailsBoObj.alertGenerationLogicNew(serialNumber, parser.getTransactionTime(), eventIdValueMap, true, false, currentLat, currentLong, null, serviceScheduleId, currentGPSFix, null);
								if(serviceScheduleStatus!=null && serviceScheduleStatus.equalsIgnoreCase("FAILURE"))
								{
									status="FAILURE";
									iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Service Alert - Generate the alert for the service schedule:"+serviceScheduleId+" and EventID:"+serviceEvent.getEventId()+":: Status::"+status);
									
								}
							}
						}
	
					}
				}
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+" APPLICATION Alert - Service Alert Generation/Closure - END");
				
				
				//************************************* Case 5: APPLICATION Alert - Fuel Theft Alert Generation/Closure ***************************
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+" APPLICATION Alert - Fuel Theft Alert Generation/Closure - START");
				if(! (session.isOpen() ))
				{
					/*session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();*/
					session = HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction();
				}
				
				//--------------- Step1: Get the FW Version Number
				int validateFuelTheft=0;
				if(parser.getFwVersionNumber()!=null && versionNum!=null)
				{
					int endIndex =parser.getFwVersionNumber().length();
					//DF20151005 - Rajani Nagaraju - MOOL processing - Check for ** in Firmwareversion since for the newer FW, ** might not be present 
					if(parser.getFwVersionNumber().contains("**"))
						endIndex=parser.getFwVersionNumber().indexOf("**");
					String firmwareVersion = parser.getFwVersionNumber().substring(0, endIndex).toString();
					int result = FwVersion.compareNumber(versionNum, firmwareVersion);
					
					if(result<0)
						validateFuelTheft=1;
					
				}
				
				//--------------- Step2: Fuel Theft will be generated when Received Record is Log, Current machine status is Ignition ON and Engine ON
				if( (parser.getMessageId().equalsIgnoreCase("010")) &&( (prevTxnIgnitionStatus.equalsIgnoreCase("1"))
					|| (prevTxnEngnieStatus.equalsIgnoreCase("1")) ) && (validateFuelTheft==1)	
				  )
				{
					//------------- Get the Current FuelLevel in volts
					String FuelLevelValue = logParamValuesMap.get("FuelLevel");
					ParameterUOMmanagement getParamValue = new ParameterUOMmanagement();
					String currFuelLevel = getParamValue.getParameterValue(FuelLevelValue);
					double currFuelLevelInDouble = Double.parseDouble(currFuelLevel);
					
					if(! (session.isOpen() ))
					{
						/*session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();*/
						session = HibernateUtil.getSessionFactory().openSession();
						session.beginTransaction();
					}
					
					//String prevLogFuelLevel =null;
					String prevLogFuelLevel = prevLogFuelLevelFromMOOL;
					if(currFuelLevel!=null && (currFuelLevelInDouble >= 5 || currFuelLevelInDouble <=100) && prevLogtransactionNumber!=0 )
					{
						//----------------- Get the Fuel level of previous Log transaction
						
						//DF20160719 @Roopa commenting the below part Bcoz Prev log fuel level is coming as part of request param from MOOL
						
						/*Query prevFuelLevelQ = session.createQuery(" select amd.parameterValue from AssetMonitoringDetailEntity amd " +
								" where amd.transactionNumber="+prevLogtransactionNumber+" and amd.parameterId=5");
						Iterator prevFuelLevelItr = prevFuelLevelQ.list().iterator();
						while(prevFuelLevelItr.hasNext())
						{
							prevLogFuelLevel=(String) prevFuelLevelItr.next();
						}*/
						
						//------------------ If this is the first log packet received for the VIN, cant check for Fuel Theft
						if(prevLogFuelLevel!=null && ! prevLogFuelLevel.equalsIgnoreCase("null"))
						{
							double prevFuelLevelInDouble = Double.parseDouble(prevLogFuelLevel);
							//--------------- Sudden Fuel Drop above 12% will be Fuel theft
							if( (prevFuelLevelInDouble-currFuelLevelInDouble) >=12 )
							{
								//Generate Fuel Theft Alert
								EventEntity fuelTheftEvent =null;
								Query fuelTheftEventQ = session.createQuery(" from EventEntity where eventId="+FuelTheftEventID);
								Iterator fuelTheftEventItr =  fuelTheftEventQ.list().iterator();
								if(fuelTheftEventItr.hasNext())
								{
									fuelTheftEvent = (EventEntity)fuelTheftEventItr.next();
								}
								
								HashMap<EventEntity, String> eventIdValueMap = new HashMap<EventEntity, String>();
								eventIdValueMap.put(fuelTheftEvent,"1");

								iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Fuel Theft Alert - Generate the alert; Current Fuel Level:"+currFuelLevelInDouble+"; Prev Log pkt fuel level:"+prevFuelLevelInDouble);
								String fuelTheftAlertStatus=eventDetailsBoObj.alertGenerationLogicNew(serialNumber, parser.getTransactionTime(), eventIdValueMap, true, true, currentLat, currentLong, null, 0, currentGPSFix, null);
								
								if(fuelTheftAlertStatus!=null && fuelTheftAlertStatus.equalsIgnoreCase("FAILURE"))
								{
									status="FAILURE";
									
									iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Fuel Theft Alert Generation Status ::"+status);
									
								}
							
							}
							
							else if ( (prevFuelLevelInDouble-currFuelLevelInDouble) <= -12 )
							{
								Query nullifyAlertQ = session.createQuery(" from AssetEventEntity where eventId="+FuelTheftEventID+" and" +
																			" serialNumber ='"+serialNumber+"' and activeStatus=1 ");
								Iterator nullifyAlertItr = nullifyAlertQ.list().iterator();
								while(nullifyAlertItr.hasNext())
								{
									AssetEventEntity assetEvent = (AssetEventEntity)nullifyAlertItr.next();
									assetEvent.setActiveStatus(0);
									assetEvent.setEventClosedTime(parser.getTransactionTime());
									assetEvent.setCreated_timestamp(currentTime);
									session.update(assetEvent);
									
									iLogger.info("AGS:"+serialNumber+":"+transactionTime+"  :"+"Close the Fuel Theft Alert; Current Fuel Level:"+currFuelLevelInDouble+"; " +
											"Prev Log pkt fuel level:"+prevFuelLevelInDouble+" AssetEventId: "+assetEvent.getAssetEventId());
								}
							}
						}
					}
				
				}
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+" APPLICATION Alert - Fuel Theft Alert Generation/Closure - END");
				
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				status = "FAILURE";
											
				fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":Exception :" + e.getMessage());
				
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+":Exception :" + e.getMessage());
				
				Writer result = new StringWriter();
	    	    PrintWriter printWriter = new PrintWriter(result);
	    	    e.printStackTrace(printWriter);
	    	    String err = result.toString();
	    	    fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":Exception trace: "+err);
	    	    
	    	    iLogger.info("AGS:"+serialNumber+":"+transactionTime+":Exception trace: "+err);
	    	    
	    	    try 
	    	    {
	    	    	printWriter.close();
	        	    result.close();
				} 
	    	    
	    	    catch (IOException e1) 
	    	    {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
			finally
			{
				try
				{
					/*if(session.isOpen())
		                  if(session.getTransaction().isActive())
		                  {
		                        session.getTransaction().commit();
		                  }
				}*/
				/*catch(Exception e)
				{
					fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+" :Exception :"+e);
					
					iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" :Exception :"+e);
					
					iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" :Duplicate Entry Exception :"+e);
					return "FAILURE";
				}*/
				//changing the order of commit - 2016-09-21 - Deepthi . Earlier it was commit, flush and close,. Hence modified the same
				if(session!=null &&  (session.isOpen()) ) 
				{
					//DF20160401- Rajani Nagaraju - To Handle the Exception: org.hibernate.HibernateException: flush is not valid without active transaction
					if( (session.getTransaction()!=null ) &&  (session.getTransaction().isActive()) ){
						try{
						session.flush();
						 session.getTransaction().commit();
						}
						catch(Exception e){
							//iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" :Duplicate Entry Exception :"+e);
							
							fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+" :Exception :"+e);
							
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" :Exception :"+e);
							
							iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" :Duplicate Entry Exception :"+e);
							return "FAILURE";
						}
					}
					
					session.close();
				}
			}catch(Exception e)
			{
				iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+" :Duplicate Entry Exception :"+e);
				return "FAILURE";
			}
		}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception :"+e);
			
			iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Exception :"+e);
			
			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			String err = result.toString();
			fLogger.fatal("AGS:"+serialNumber+":"+transactionTime+":"+"Exception trace: "+err);
			
			iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Exception trace: "+err);
			
			try 
			{
				printWriter.close();
				result.close();
			} 

			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			status= "FAILURE";
			
			iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"Exception at Final Catch Block Status:: "+status+":"+err);
		}
		
		iLogger.info("AGS:"+serialNumber+":"+transactionTime+":"+"AGS Processing Logic - END");
		
		return status;	
	}
	
}
