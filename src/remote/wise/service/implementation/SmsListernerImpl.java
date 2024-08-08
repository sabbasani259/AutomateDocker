/**
 * 
 */
package remote.wise.service.implementation;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.EAintegration.dataContract.JcbRollOffInputContract;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessobject.EventDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

/**
 * @author sunayak
 *
 */
public class SmsListernerImpl {

	/*public static WiseLogger businessError = WiseLogger.getLogger("AssetDashboardImpl:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("AssetDashboardImpl:","fatalError");
	WiseLogger infoLogger = WiseLogger.getLogger("SMSListernerService:","info");*/
	public String SmsListerner(String smsListerber_Str) {
		// TODO Auto-generated method stub
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String status =null;
		String GPRS_NA =null;
		String GPRS_Service_NA =null;
		String GPRS_Sever_comm =null;
		String alert1=null;
		String alert2=null;
		String alert3=null;
		String alert4=null;
		String tow_away=null;
		String lat_long=null;
		String HMR=null;
		String Fuel_Level=null;
		String GPS_Fix=null;
		String GPS_Satelite=null;
		String ext_battery_vol=null;
		String int_battery_vol=null;
		String sms_date=null;
		String temp=null;
		String serial_number=null;
		String FW_version_num=null;

		if(smsListerber_Str==null || (!(smsListerber_Str.length()>0)))
		{
			status ="SMS received is not in correct Format";
			return status;
		}

		if(!(smsListerber_Str.length()==116))
		{
			status ="SMS received is not in correct Format";
			return status;
		}

		String[] msgString = smsListerber_Str.split("\\,");

		if(msgString.length>0)
			GPRS_NA=msgString[0];
		iLogger.info("GPRS Not available"+GPRS_NA);

		if(msgString.length>1)
			GPRS_Service_NA=msgString[1];
		iLogger.info("GPRS service not allowed"+GPRS_Service_NA);

		if(msgString.length>2)
			GPRS_Sever_comm=msgString[2];
		iLogger.info("GPRS Server communication Failed"+GPRS_Sever_comm);

		if(msgString.length>3)
			alert1=msgString[3];
		iLogger.info("Low Engine Oil Pressure"+alert1);

		if(msgString.length>4)
			alert2=msgString[4];
		iLogger.info("High Coolant Temperature"+alert2);

		if(msgString.length>5)
			alert3=msgString[5];
		iLogger.info("Water in Fuel"+alert3);

		if(msgString.length>6)
			alert4=msgString[6];
		iLogger.info("Blocked Air Filter"+alert4);

		if(msgString.length>7)
			tow_away=msgString[7];
		iLogger.info("Tow-away ID as per TLCP protocol"+tow_away);

		if(msgString.length>8)
			lat_long=msgString[8];
		iLogger.info("GPS Lat & long "+lat_long);

		if(msgString.length>9)
			HMR=msgString[9];
		iLogger.info("Machine Hour"+HMR);

		if(msgString.length>10)
			Fuel_Level=msgString[10];
		iLogger.info("Fuel_Level"+Fuel_Level);

		if(msgString.length>11)
			GPS_Fix=msgString[11];
		iLogger.info("GPS_Fix"+GPS_Fix);

		if(msgString.length>12)
			GPS_Satelite=msgString[12];
		iLogger.info("GPS_Satelite"+GPS_Satelite);

		if(msgString.length>13)
			ext_battery_vol=msgString[13];
		iLogger.info("ext_battery_vol"+ext_battery_vol);

		if(msgString.length>14)
			int_battery_vol=msgString[14];
		iLogger.info("int_battery_vol"+int_battery_vol);

		if(msgString.length>15)
			sms_date=msgString[15];
		iLogger.info("sms_date"+sms_date);

		if(msgString.length>16)
			temp=msgString[16];
		iLogger.info("temperture"+temp);

		if(msgString.length>17)
			serial_number=msgString[17];
		iLogger.info("serial_number"+serial_number);

		if(msgString.length>18)
			FW_version_num=msgString[18];
		iLogger.info("FW_version_num"+FW_version_num);

		if(GPRS_NA==null || GPRS_Service_NA==null ||GPRS_Sever_comm==null || HMR==null || ext_battery_vol==null || int_battery_vol==null ||serial_number==null || alert1==null || alert2==null || alert3==null ||alert4==null)
		{
			status ="SMS parameter received is null";
			return status;
		}
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		AssetEntity asset=null;
		try{
			Query serialNumberCheck = session.createQuery("from AssetEntity where serial_number='"+serial_number+"'");
			Iterator serialNumItr = serialNumberCheck.list().iterator();
			while (serialNumItr.hasNext())
			{
				asset = (AssetEntity) serialNumItr.next();
			}
			if(asset==null)
			{
				iLogger.info("Serial Number is invalid");
				iLogger.info("Serial Number is invalid in side catch");
				status= "Serial Number is invalid";
				return status;
			}
		  }
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
			bLogger.error("Serial Number is invalid");
			iLogger.info("Serial Number is invalid");
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
		
		EventDetailsBO boObj = new EventDetailsBO();
		status = boObj.setSmsListernerDetails(GPRS_NA,GPRS_Service_NA,GPRS_Sever_comm,HMR,ext_battery_vol,int_battery_vol,serial_number,alert1,alert2,alert3,alert4,tow_away,sms_date,GPS_Fix);
		return status;
	}

}
