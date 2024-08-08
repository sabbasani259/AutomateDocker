package remote.wise.service.implementation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessobject.AssetMonitoringDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.AssetEventLogReqContract;
import remote.wise.service.datacontract.AssetEventRespContract;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;


public class AssetEventLogImpl 
{

	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("AssetEventLogImpl:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("AssetDetailsBO:","fatalError");*/
	
	private String EventGeneratedTime;
	private int SequenceId;
	//private int MonitoringParamId;
	private String ParamName;
	@Override
	public String toString() {
		return "AssetEventLogImpl [EventGeneratedTime=" + EventGeneratedTime
				+ ", SequenceId=" + SequenceId + ", ParamName=" + ParamName
				+ ", ParameterValue=" + ParameterValue + ", latitude="
				+ latitude + ", longitude=" + longitude + ", alertSeverity="
				+ alertSeverity + ", transactionNumber=" + transactionNumber
				+ ", Record_Type_Id=" + Record_Type_Id + "]";
	}



	private String ParameterValue;
	private String latitude;
	private String longitude;
	private String alertSeverity;
	private int transactionNumber;
	private int Record_Type_Id;
	private String isengineOn;
	/*private int dtcCode;
	
	
	
	public int getDtcCode() {
		return dtcCode;
	}

	public void setDtcCode(int dtcCode) {
		this.dtcCode = dtcCode;
	}*/

	public String getIsengineOn() {
		return isengineOn;
	}

	public void setIsengineOn(String isengineOn) {
		this.isengineOn = isengineOn;
	}
	
	public int getRecord_Type_Id() {
		return Record_Type_Id;
	}

	public void setRecord_Type_Id(int record_Type_Id) {
		Record_Type_Id = record_Type_Id;
	}

	/**
	 * @return the eventGeneratedTime
	 */
	public String getEventGeneratedTime() {
		return EventGeneratedTime;
	}

	/**
	 * @param eventGeneratedTime the eventGeneratedTime to set
	 */
	public void setEventGeneratedTime(String eventGeneratedTime) {
		EventGeneratedTime = eventGeneratedTime;
	}

	/**
	 * @return the sequenceId
	 */
	public int getSequenceId() {
		return SequenceId;
	}

	/**
	 * @param sequenceId the sequenceId to set
	 */
	public void setSequenceId(int sequenceId) {
		SequenceId = sequenceId;
	}

	/**
	 * @return the paramName
	 */
	public String getParamName() {
		return ParamName;
	}

	/**
	 * @param paramName the paramName to set
	 */
	public void setParamName(String paramName) {
		ParamName = paramName;
	}

	/**
	 * @return the parameterValue
	 */
	public String getParameterValue() {
		return ParameterValue;
	}

	/**
	 * @param parameterValue the parameterValue to set
	 */
	public void setParameterValue(String parameterValue) {
		ParameterValue = parameterValue;
	}

	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the alertSeverity
	 */
	public String getAlertSeverity() {
		return alertSeverity;
	}

	/**
	 * @param alertSeverity the alertSeverity to set
	 */
	public void setAlertSeverity(String alertSeverity) {
		this.alertSeverity = alertSeverity;
	}
	
	/**
	 * @return the transactionNumber
	 */
	public int getTransactionNumber() {
		return transactionNumber;
	}

	/**
	 * @param transactionNumber the transactionNumber to set
	 */
	public void setTransactionNumber(int transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	
	

	/**
	 * This method returns the Machine Activity Log details
	 * @param reqObj Specifies the serialNumber and the time period
	 * @return List<AssetEventRespContract> List of objects with the activity log details
	 * @throws CustomFault
	 */
	public List<AssetEventRespContract> getAssetEvents(AssetEventLogReqContract reqObj) throws CustomFault
	{
		List<AssetEventRespContract> respList = new LinkedList<AssetEventRespContract>();
		
		AssetMonitoringDetailsBO assetMonitoringBO=new AssetMonitoringDetailsBO();
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		try
		{
			if(reqObj.getSerialNumber()==null)
				throw new CustomFault("Serial Number is not passed");
			
			if(reqObj.getPeriod()==null)
				throw new CustomFault("Period is NULL");
			
			if(reqObj.getLoginTenancyId()==0)
				throw new CustomFault("Login TenancyId not specified");
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		
		try
		{
			String longitude = null;
			String latitude = null;
			
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			longitude = prop.getProperty("Longitude");
			latitude = prop.getProperty("Latitude");
			
			List<AssetEventLogImpl> assetEventLog = new LinkedList<AssetEventLogImpl>();
			assetEventLog = assetMonitoringBO.getAssetEventLog(reqObj.getSerialNumber(),reqObj.getPeriod(),reqObj.getLoginTenancyId());
			
			Collections.sort(assetEventLog, new transactionComparator());
		
			 if(! (session.isOpen() ))
             {
                         session = HibernateUtil.getSessionFactory().getCurrentSession();
                         session.getTransaction().begin();
             }
			 
			for(int i=0;i<assetEventLog.size();i++)
			{
				AssetEventRespContract respObj = new AssetEventRespContract();
				
				respObj.setParameterName(assetEventLog.get(i).getParamName());
				respObj.setSequenceId(i+1);
				respObj.setTransactionTime(assetEventLog.get(i).getEventGeneratedTime());
				respObj.setParameterValue(assetEventLog.get(i).getParameterValue());
				respObj.setAlertSeverity(assetEventLog.get(i).getAlertSeverity());
				respObj.setLatitude(assetEventLog.get(i).getLatitude());
				respObj.setLongitude(assetEventLog.get(i).getLongitude());
				//respObj.setDtccode(assetEventLog.get(i).getDtcCode());
				
			/*	// DefectID:1308 - Rajani Nagaraju - 20130925 - Query Tweaking as it was taking much time
				//If the event is a Service Closure received from DBMS service history record
				if(assetEventLog.get(i).getTransactionNumber()==0)
				{
					//If this is the first transaction for the day
					if(i==0)
					{
						//If any transactional record exists for the day other than this
						if(assetEventLog.size()>1)
						{
							respObj.setLatitude(assetEventLog.get(i+1).getLatitude());
							respObj.setLongitude(assetEventLog.get(i+1).getLongitude());
						}
						else
						{
							//Query AMH and AMD to get the last received transaction for the machine
							Query query = session.createQuery(" select d.parameterName, c.parameterValue from AssetMonitoringHeaderEntity a, AssetMonitoringDetailEntity c," +
									" MonitoringParameters d " +
									" where a.serialNumber ='"+reqObj.getSerialNumber()+"'" +
									" and a.transactionNumber=c.transactionNumber and c.parameterId=d.parameterId " +
									" and a.transactionTime = (select " +
									" max(b.transactionTime) from AssetMonitoringHeaderEntity b where b.serialNumber='"+reqObj.getSerialNumber()+"' " +
									" and b.transactionTime < '"+assetEventLog.get(i).getEventGeneratedTime()+"') " +
									" and d.parameterName in ('" + latitude+"', '"+longitude+"') ");
							Iterator itr = query.list().iterator();
							Object[] result =null;
							while(itr.hasNext())
							{
								result = (Object[])itr.next();
								String paramName = (String) result[0];
								if(paramName.equalsIgnoreCase(latitude))
								{
									respObj.setLatitude((String)result[1]);
								}
									
								else if (paramName.equalsIgnoreCase(longitude))
								{
									respObj.setLongitude((String)result[1]);
								}
							}
							
						}
						
					}
					else
					{
						respObj.setLatitude(assetEventLog.get(i-1).getLatitude());
						respObj.setLongitude(assetEventLog.get(i-1).getLongitude());
					}
				}
				
				else
				{
					respObj.setLatitude(assetEventLog.get(i).getLatitude());
					respObj.setLongitude(assetEventLog.get(i).getLongitude());
				}
				
				*/
				
				respList.add(respObj);
			}	
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		}
        
        finally
        {
              if(session.getTransaction().isActive())
              {
                    session.getTransaction().commit();
              }
              
              if(session.isOpen())
              {
                    session.flush();
                    session.close();
              }
              
        }
        
		return respList;	
	}
}


class transactionComparator implements Comparator<AssetEventLogImpl>
{
//	public static WiseLogger infoLogger = WiseLogger.getLogger("AssetDiagnosticImpl:","info");
	
	 Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalError = FatalLoggerClass.logger;
		Logger businessError = BusinessErrorLoggerClass.logger;
	@Override
	////DefectID:1308 - Rajani Nagaraju - 20130918 - Not to display Application closed alerts
	public int compare(AssetEventLogImpl arg0, AssetEventLogImpl arg1) 
	{
		// TODO Auto-generated method stub
		long t1=0;
		long t2=0;
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = format.parse(arg0.getEventGeneratedTime());
			Date date2 = format.parse(arg1.getEventGeneratedTime());
			t1 = date1.getTime();
			t2 = date2.getTime();
			
		}
		
		catch(ParseException e)
		{
			infoLogger.info(e);
		}
		//defect id : 1354: Rajani
		return (int) (t2 - t1);
	}
	
	
}
