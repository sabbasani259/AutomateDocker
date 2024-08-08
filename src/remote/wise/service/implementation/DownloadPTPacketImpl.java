/*
 *  20230724 : CR416 : Prasanna Lakshmi : Download PT Packet Changes
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.ListToStringConversion;

public class DownloadPTPacketImpl {
	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;

	@SuppressWarnings("resource")
	public List<HashMap<String, String>> getDownloadPTPacketReport(List<String> Serial_Number, String stratdate,String enddate) {
		String TAssetMonQuery = null;
		String startTAssetMonQuery = null;
		String endTAssetMonQuery = null;
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		List<HashMap<String, String>> mapList = new ArrayList<>();
		
	  ListToStringConversion str=new ListToStringConversion();
	   String SerialNumber =	str.getStringList(Serial_Number).toString();
	   
	  iLogger.info("getLDownloadPTPacketReportImpl::start:: SerialNumber : "+Serial_Number);
		try {

			DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd");

			Date TxnTS = null;
			Date TxnED=null;
			try {
				TxnTS = dateStr.parse(stratdate);
				TxnED = dateStr.parse(enddate);
			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			String startTAssetMonTable = null;
			String endTAssetMonTable = null;

			String dynamicTable = new DateUtil().getDynamicTable("PTMonitor", TxnTS);

			if (dynamicTable != null) {
				endTAssetMonTable = dynamicTable;
             }

			Calendar cal = Calendar.getInstance();
			cal.setTime(TxnTS);
			//cal.add(Calendar.DAY_OF_YEAR, -1);
			String startTS = dateStr.format(cal.getTime());
			startTS = startTS + " 06:30:00";
			
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(TxnED);
			String endTS = dateStr.format(cal1.getTime());
			endTS=endTS+ " 18:30:00";
			Timestamp starttxnTimestamp = new Timestamp(cal.getTimeInMillis());
			
			iLogger.info("  starttxnTimestamp : "+starttxnTimestamp);
			iLogger.info(" startTS :"+startTS+" endTS :" +endTS);
			String startDynamicTable = new DateUtil().getDynamicTable("PTLogMonitor", starttxnTimestamp);

			if (startDynamicTable != null) {
				startTAssetMonTable = startDynamicTable;
			} else {
				startTAssetMonTable = endTAssetMonTable;
			}
            	
			if (startTAssetMonTable.equals(endTAssetMonTable)) {
				TAssetMonQuery ="select JSON_UNQUOTE(Serial_Number) as Vin ,JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) as MSG_ID ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.CMH')) as Hours ,"+ 
						" CONVERT_TZ(Transaction_timestamp,'+00:00','+05:30') as Transactiontimestamp,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Ad_Blue_Consumption')) as AdBlueConsumption,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_DEF_Conc')) as DEF_Concentration,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_DEF_Temp')) as DEF_Temperature,"+ 
						//"JSON_UNQUOTE(json_extract(TxnData,'$.')) as EngineOnCount,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Power_Rating_Request')) as PowerRatingRequest,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange1')) as SpeedRange1FuelRange1 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange2')) as SpeedRange1FuelRange2 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange3')) as SpeedRange1FuelRange3 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange4')) as SpeedRange1FuelRange4 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange5')) as SpeedRange1FuelRange5 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange6')) as SpeedRange1FuelRange6 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange7')) as SpeedRange1FuelRange7 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange8')) as SpeedRange1FuelRange8 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange9')) as SpeedRange1FuelRange9 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange10')) as SpeedRange1FuelRange10 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange1')) as SpeedRange2FuelRange1 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange2')) as SpeedRange2FuelRange2 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange3')) as SpeedRange2FuelRange3 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange4')) as SpeedRange2FuelRange4 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange5')) as SpeedRange2FuelRange5 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange6')) as SpeedRange2FuelRange6 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange7')) as SpeedRange2FuelRange7 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange8')) as SpeedRange2FuelRange8 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange9')) as SpeedRange2FuelRange9 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange10')) as SpeedRange2FuelRange10 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange1')) as SpeedRange3FuelRange1 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange2')) as SpeedRange3FuelRange2 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange3')) as SpeedRange3FuelRange3 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange4')) as SpeedRange3FuelRange4 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange5')) as SpeedRange3FuelRange5 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange6')) as SpeedRange3FuelRange6 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange7')) as SpeedRange3FuelRange7 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange8')) as SpeedRange3FuelRange8 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange9')) as SpeedRange3FuelRange9 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange10')) as SpeedRange3FuelRange10 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange1')) as SpeedRange4FuelRange1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange2')) as SpeedRange4FuelRange2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange3')) as SpeedRange4FuelRange3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange4')) as SpeedRange4FuelRange4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange5')) as SpeedRange4FuelRange5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange6')) as SpeedRange4FuelRange6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange7')) as SpeedRange4FuelRange7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange8')) as SpeedRange4FuelRange8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange9')) as SpeedRange4FuelRange9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange10')) as SpeedRange4FuelRange10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange1')) as SpeedRange5FuelRange1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange2')) as SpeedRange5FuelRange2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange3')) as SpeedRange5FuelRange3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange4')) as SpeedRange5FuelRange4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange5')) as SpeedRange5FuelRange5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange6')) as SpeedRange5FuelRange6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange7')) as SpeedRange5FuelRange7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange8')) as SpeedRange5FuelRange8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange9')) as SpeedRange5FuelRange9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange10')) as SpeedRange5FuelRange10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange1')) as SpeedRange6FuelRange1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange2')) as SpeedRange6FuelRange2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange3')) as SpeedRange6FuelRange3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange4')) as SpeedRange6FuelRange4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange5')) as SpeedRange6FuelRange5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange6')) as SpeedRange6FuelRange6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange7')) as SpeedRange6FuelRange7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange8')) as SpeedRange6FuelRange8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange9')) as SpeedRange6FuelRange9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange10')) as SpeedRange6FuelRange10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange1')) as SpeedRange7FuelRange1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange2')) as SpeedRange7FuelRange2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange3')) as SpeedRange7FuelRange3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange4')) as SpeedRange7FuelRange4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange5')) as SpeedRange7FuelRange5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange6')) as SpeedRange7FuelRange6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange7')) as SpeedRange7FuelRange7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange8')) as SpeedRange7FuelRange8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange9')) as SpeedRange7FuelRange9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange10')) as SpeedRange7FuelRange10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range1')) as CoolantTempratureResidenceRange1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range2')) as CoolantTempratureResidenceRange2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range3')) as CoolantTempratureResidenceRange3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range4')) as CoolantTempratureResidenceRange4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range5')) as CoolantTempratureResidenceRange5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range6')) as CoolantTempratureResidenceRange6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range7')) as CoolantTempratureResidenceRange7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range8')) as CoolantTempratureResidenceRange8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range9')) as CoolantTempratureResidenceRange9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range10')) as CoolantTempratureResidenceRange10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range11')) as CoolantTempratureResidenceRange11,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range12')) as CoolantTempratureResidenceRange12,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range1')) as EEGTRR1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range2')) as EEGTRR2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range3')) as EEGTRR3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range4')) as EEGTRR4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range5')) as EEGTRR5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range6')) as EEGTRR6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range7')) as EEGTRR7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range8')) as EEGTRR8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range9')) as EEGTRR9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range10')) as EEGTRR10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range11')) as EEGTRR11,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range12')) as EEGTRR12,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range1')) as FuelTempResidenceRange1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range2')) as FuelTempResidenceRange2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range3')) as FuelTempResidenceRange3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range4')) as FuelTempResidenceRange4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range5')) as FuelTempResidenceRange5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range6')) as FuelTempResidenceRange6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range7')) as FuelTempResidenceRange7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range8')) as FuelTempResidenceRange8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range1')) as InletAir1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range2')) as InletAir2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range3')) as InletAir3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range4')) as InletAir4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range5')) as InletAir5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range6')) as InletAir6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range7')) as InletAir7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range8')) as InletAir8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range9')) as InletAir9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range10')) as InletAir10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range1')) as Barometric1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range2')) as Barometric2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range3')) as Barometric3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range4')) as Barometric4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range5')) as Barometric5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range6')) as Barometric6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range7')) as Barometric7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range8')) as Barometric8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range9')) as Barometric9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range10')) as Barometric10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Max_Engine_Speed')) as MaxEngineSpeed ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range1_Count')) as EOERC1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range2_Count')) as EOERC2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range3_Count')) as EOERC3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range4_Count')) as EOERC4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range5_Count')) as EOERC5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range6_Count')) as EOERC6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range7_Count')) as EOERC7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range8_Count')) as EOERC8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range9_Count')) as EOERC9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range10_Count')) as EOERC10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range1_Duration')) as EOERD1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range2_Duration')) as EOERD2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range3_Duration')) as EOERD3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range4_Duration')) as EOERD4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range5_Duration')) as EOERD5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range6_Duration')) as EOERD6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range7_Duration')) as EOERD7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range8_Duration')) as EOERD8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range9_Duration')) as EOERD9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range10_Duration')) as EOERD10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Thermal_Cycles')) as EngineThermalCycles ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_EngStCnt')) as EngineStartCount ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_ECUOTIGNOnT')) as ECUOntime,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR1')) as Ambient1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR2')) as Ambient2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR3')) as Ambient3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR4')) as Ambient4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR5')) as Ambient5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR6')) as Ambient6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR7')) as Ambient7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR8')) as Ambient8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR9')) as Ambient9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR10')) as Ambient10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR11')) as Ambient11,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT1')) as DOCOutlet1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT2')) as DOCOutlet2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT3')) as DOCOutlet3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT4')) as DOCOutlet4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT5')) as DOCOutlet5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT6')) as DOCOutlet6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT7')) as DOCOutlet7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT8')) as DOCOutlet8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT9')) as DOCOutlet9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT10')) as DOCOutlet10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT11')) as DOCOutlet11,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT12')) as DOCOutlet12,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT13')) as DOCOutlet13,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT14')) as DOCOutlet14,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_SulpMont')) as SulphurMonitor,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_SUCActRst')) as ActiveRestorations,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng1')) as Aftertreatment1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng2')) as Aftertreatment2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng3')) as Aftertreatment3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng4')) as Aftertreatment4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng5')) as Aftertreatment5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng6')) as Aftertreatment6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng7')) as Aftertreatment7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng8')) as Aftertreatment8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng9')) as Aftertreatment9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng10')) as Aftertreatment10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng11')) as Aftertreatment11,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng12')) as Aftertreatment12,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng13')) as Aftertreatment13,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng14')) as Aftertreatment14,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng1')) as PCSDPRRng1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng2')) as PCSDPRRng2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng3')) as PCSDPRRng3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng4')) as PCSDPRRng4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng5')) as PCSDPRRng5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng6')) as PCSDPRRng6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng7')) as PCSDPRRng7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng8')) as PCSDPRRng8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng9')) as PCSDPRRng9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng10')) as PCSDPRRng10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_SucStRstAtmpts')) as StationaryRestoration,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_TSLRestAmpt')) as LastRestoration,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_SootMon')) as SootMonitor,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AshMon')) as AshMonitor,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1TFU')) as AftertreatmentTFU,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1TRT')) as AftertreatmentTRT,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR1')) as DPFOutlet1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR2')) as DPFOutlet2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR3')) as DPFOutlet3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR4')) as DPFOutlet4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR5')) as DPFOutlet5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR6')) as DPFOutlet6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR7')) as DPFOutlet7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR8')) as DPFOutlet8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR9')) as DPFOutlet9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR10')) as DPFOutlet10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR11')) as DPFOutlet11,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR12')) as DPFOutlet12,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR13')) as DPFOutlet13,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR14')) as DPFOutlet14,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFARFBS')) as DieselSWITCH,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFARFBT')) as DieselTOOL,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISS')) as AftertreatmentStatus,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISIOL')) as AftertreatmentIOL,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISISLR')) as AftertreatmentISLR,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISTTIOL')) as AftertreatmentTTIOL,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISTTISLR')) as AftertreatmentLR,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_ESHTLC')) as ESHTLC,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSRNR')) as DPFSRNR,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSMRA')) as DPFSMRA,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSSRR')) as DPFSSRR,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSRS')) as DPFSRS,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSRPL2')) as DPFSRPL2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSMRRL3')) as DPFSMRRL3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSRA')) as DPFSRA,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSRC')) as DPFSRC,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISRABIBO')) as AARIDTISRABIBO "+ 
						" from  "+startTAssetMonTable + " where JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 068 and serial_number in ("+SerialNumber+") and Transaction_Timestamp >= '"+startTS+"'"+ 
						" and Transaction_Timestamp <= '"+endTS+"' ";
				
			} else {
				startTAssetMonQuery ="select JSON_UNQUOTE(Serial_Number) as Vin,JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) as MSG_ID ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.CMH')) as Hours ,"+ 
						" CONVERT_TZ(Transaction_timestamp,'+00:00','+05:30') as Transactiontimestamp,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Ad_Blue_Consumption')) as AdBlueConsumption,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_DEF_Conc')) as DEF_Concentration,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_DEF_Temp')) as DEF_Temperature,"+ 
						//"JSON_UNQUOTE(json_extract(TxnData,'$.')) as EngineOnCount,"+  
						"JSON_UNQUOTE(json_extract(TxnData,'$.Power_Rating_Request')) as PowerRatingRequest,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange1')) as SpeedRange1FuelRange1 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange2')) as SpeedRange1FuelRange2 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange3')) as SpeedRange1FuelRange3 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange4')) as SpeedRange1FuelRange4 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange5')) as SpeedRange1FuelRange5 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange6')) as SpeedRange1FuelRange6 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange7')) as SpeedRange1FuelRange7 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange8')) as SpeedRange1FuelRange8 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange9')) as SpeedRange1FuelRange9 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange10')) as SpeedRange1FuelRange10 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange1')) as SpeedRange2FuelRange1 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange2')) as SpeedRange2FuelRange2 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange3')) as SpeedRange2FuelRange3 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange4')) as SpeedRange2FuelRange4 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange5')) as SpeedRange2FuelRange5 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange6')) as SpeedRange2FuelRange6 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange7')) as SpeedRange2FuelRange7 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange8')) as SpeedRange2FuelRange8 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange9')) as SpeedRange2FuelRange9 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange10')) as SpeedRange2FuelRange10 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange1')) as SpeedRange3FuelRange1 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange2')) as SpeedRange3FuelRange2 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange3')) as SpeedRange3FuelRange3 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange4')) as SpeedRange3FuelRange4 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange5')) as SpeedRange3FuelRange5 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange6')) as SpeedRange3FuelRange6 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange7')) as SpeedRange3FuelRange7 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange8')) as SpeedRange3FuelRange8 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange9')) as SpeedRange3FuelRange9 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange10')) as SpeedRange3FuelRange10 ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange1')) as SpeedRange4FuelRange1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange2')) as SpeedRange4FuelRange2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange3')) as SpeedRange4FuelRange3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange4')) as SpeedRange4FuelRange4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange5')) as SpeedRange4FuelRange5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange6')) as SpeedRange4FuelRange6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange7')) as SpeedRange4FuelRange7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange8')) as SpeedRange4FuelRange8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange9')) as SpeedRange4FuelRange9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange10')) as SpeedRange4FuelRange10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange1')) as SpeedRange5FuelRange1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange2')) as SpeedRange5FuelRange2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange3')) as SpeedRange5FuelRange3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange4')) as SpeedRange5FuelRange4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange5')) as SpeedRange5FuelRange5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange6')) as SpeedRange5FuelRange6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange7')) as SpeedRange5FuelRange7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange8')) as SpeedRange5FuelRange8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange9')) as SpeedRange5FuelRange9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange10')) as SpeedRange5FuelRange10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange1')) as SpeedRange6FuelRange1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange2')) as SpeedRange6FuelRange2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange3')) as SpeedRange6FuelRange3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange4')) as SpeedRange6FuelRange4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange5')) as SpeedRange6FuelRange5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange6')) as SpeedRange6FuelRange6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange7')) as SpeedRange6FuelRange7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange8')) as SpeedRange6FuelRange8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange9')) as SpeedRange6FuelRange9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange10')) as SpeedRange6FuelRange10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange1')) as SpeedRange7FuelRange1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange2')) as SpeedRange7FuelRange2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange3')) as SpeedRange7FuelRange3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange4')) as SpeedRange7FuelRange4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange5')) as SpeedRange7FuelRange5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange6')) as SpeedRange7FuelRange6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange7')) as SpeedRange7FuelRange7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange8')) as SpeedRange7FuelRange8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange9')) as SpeedRange7FuelRange9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange10')) as SpeedRange7FuelRange10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range1')) as CoolantTempratureResidenceRange1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range2')) as CoolantTempratureResidenceRange2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range3')) as CoolantTempratureResidenceRange3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range4')) as CoolantTempratureResidenceRange4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range5')) as CoolantTempratureResidenceRange5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range6')) as CoolantTempratureResidenceRange6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range7')) as CoolantTempratureResidenceRange7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range8')) as CoolantTempratureResidenceRange8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range9')) as CoolantTempratureResidenceRange9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range10')) as CoolantTempratureResidenceRange10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range11')) as CoolantTempratureResidenceRange11,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range12')) as CoolantTempratureResidenceRange12,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range1')) as EEGTRR1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range2')) as EEGTRR2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range3')) as EEGTRR3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range4')) as EEGTRR4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range5')) as EEGTRR5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range6')) as EEGTRR6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range7')) as EEGTRR7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range8')) as EEGTRR8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range9')) as EEGTRR9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range10')) as EEGTRR10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range11')) as EEGTRR11,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range12')) as EEGTRR12,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range1')) as FuelTempResidenceRange1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range2')) as FuelTempResidenceRange2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range3')) as FuelTempResidenceRange3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range4')) as FuelTempResidenceRange4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range5')) as FuelTempResidenceRange5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range6')) as FuelTempResidenceRange6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range7')) as FuelTempResidenceRange7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range8')) as FuelTempResidenceRange8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range1')) as InletAir1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range2')) as InletAir2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range3')) as InletAir3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range4')) as InletAir4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range5')) as InletAir5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range6')) as InletAir6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range7')) as InletAir7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range8')) as InletAir8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range9')) as InletAir9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range10')) as InletAir10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range1')) as Barometric1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range2')) as Barometric2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range3')) as Barometric3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range4')) as Barometric4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range5')) as Barometric5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range6')) as Barometric6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range7')) as Barometric7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range8')) as Barometric8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range9')) as Barometric9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range10')) as Barometric10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Max_Engine_Speed')) as MaxEngineSpeed ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range1_Count')) as EOERC1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range2_Count')) as EOERC2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range3_Count')) as EOERC3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range4_Count')) as EOERC4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range5_Count')) as EOERC5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range6_Count')) as EOERC6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range7_Count')) as EOERC7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range8_Count')) as EOERC8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range9_Count')) as EOERC9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range10_Count')) as EOERC10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range1_Duration')) as EOERD1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range2_Duration')) as EOERD2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range3_Duration')) as EOERD3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range4_Duration')) as EOERD4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range5_Duration')) as EOERD5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range6_Duration')) as EOERD6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range7_Duration')) as EOERD7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range8_Duration')) as EOERD8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range9_Duration')) as EOERD9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range10_Duration')) as EOERD10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Thermal_Cycles')) as EngineThermalCycles ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_EngStCnt')) as EngineStartCount ,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_ECUOTIGNOnT')) as ECUOntime,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR1')) as Ambient1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR2')) as Ambient2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR3')) as Ambient3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR4')) as Ambient4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR5')) as Ambient5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR6')) as Ambient6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR7')) as Ambient7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR8')) as Ambient8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR9')) as Ambient9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR10')) as Ambient10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR11')) as Ambient12,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT1')) as DOCOutlet1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT2')) as DOCOutlet2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT3')) as DOCOutlet3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT4')) as DOCOutlet4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT5')) as DOCOutlet5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT6')) as DOCOutlet6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT7')) as DOCOutlet7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT8')) as DOCOutlet8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT9')) as DOCOutlet9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT10')) as DOCOutlet10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT11')) as DOCOutlet11,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT12')) as DOCOutlet12,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT13')) as DOCOutlet13,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT14')) as DOCOutlet14,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_SulpMont')) as SulphurMonitor,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_SUCActRst')) as ActiveRestorations,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng1')) as Aftertreatment1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng2')) as Aftertreatment2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng3')) as Aftertreatment3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng4')) as Aftertreatment4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng5')) as Aftertreatment5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng6')) as Aftertreatment6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng7')) as Aftertreatment7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng8')) as Aftertreatment8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng9')) as Aftertreatment9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng10')) as Aftertreatment10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng11')) as Aftertreatment11,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng12')) as Aftertreatment12,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng13')) as Aftertreatment13,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng14')) as Aftertreatment14,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng1')) as PCSDPRRng1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng2')) as PCSDPRRng2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng3')) as PCSDPRRng3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng4')) as PCSDPRRng4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng5')) as PCSDPRRng5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng6')) as PCSDPRRng6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng7')) as PCSDPRRng7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng8')) as PCSDPRRng8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng9')) as PCSDPRRng9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng10')) as PCSDPRRng10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_SucStRstAtmpts')) as StationaryRestoration,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_TSLRestAmpt')) as LastRestoration,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_SootMon')) as SootMonitor,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AshMon')) as AshMonitor,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1TFU')) as AftertreatmentTFU,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1TRT')) as AftertreatmentTRT,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR1')) as DPFOutlet1,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR2')) as DPFOutlet2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR3')) as DPFOutlet3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR4')) as DPFOutlet4,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR5')) as DPFOutlet5,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR6')) as DPFOutlet6,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR7')) as DPFOutlet7,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR8')) as DPFOutlet8,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR9')) as DPFOutlet9,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR10')) as DPFOutlet10,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR11')) as DPFOutlet11,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR12')) as DPFOutlet12,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR13')) as DPFOutlet13,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR14')) as DPFOutlet14,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFARFBS')) as DieselSWITCH,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFARFBT')) as DieselTOOL,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISS')) as AftertreatmentStatus,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISIOL')) as AftertreatmentIOL,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISISLR')) as AftertreatmentISLR,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISTTIOL')) as AftertreatmentTTIOL,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISTTISLR')) as AftertreatmentLR,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_ESHTLC')) as ESHTLC,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSRNR')) as DPFSRNR,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSMRA')) as DPFSMRA,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSSRR')) as DPFSSRR,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSRS')) as DPFSRS,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSRPL2')) as DPFSRPL2,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSMRRL3')) as DPFSMRRL3,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSRA')) as DPFSRA,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSRC')) as DPFSRC,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISRABIBO')) as AARIDTISRABIBO,"+ 
						" from  "+startTAssetMonTable + " where JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 068 and serial_number in ("+SerialNumber+") and Transaction_Timestamp >= '"+startTS+"'"+ 
						" and Transaction_Timestamp <= '"+endTS+"' ";
						
				endTAssetMonQuery = "select JSON_UNQUOTE(Serial_Number) as Vin ,JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) as MSG_ID ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.CMH')) as Hours ," + 
						" CONVERT_TZ(Transaction_timestamp,'+00:00','+05:30') as Transactiontimestamp," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Ad_Blue_Consumption')) as AdBlueConsumption," +  
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_DEF_Conc')) as DEF_Concentration,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_DEF_Temp')) as DEF_Temperature,"+ 
						//"JSON_UNQUOTE(json_extract(TxnData,'$.')) as EngineOnCount,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Power_Rating_Request')) as PowerRatingRequest," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange1')) as SpeedRange1FuelRange1 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange2')) as SpeedRange1FuelRange2 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange3')) as SpeedRange1FuelRange3 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange4')) as SpeedRange1FuelRange4 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange5')) as SpeedRange1FuelRange5 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange6')) as SpeedRange1FuelRange6 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange7')) as SpeedRange1FuelRange7 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange8')) as SpeedRange1FuelRange8 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange9')) as SpeedRange1FuelRange9 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange1_FuelRange10')) as SpeedRange1FuelRange10 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange1')) as SpeedRange2FuelRange1 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange2')) as SpeedRange2FuelRange2 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange3')) as SpeedRange2FuelRange3 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange4')) as SpeedRange2FuelRange4 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange5')) as SpeedRange2FuelRange5 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange6')) as SpeedRange2FuelRange6 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange7')) as SpeedRange2FuelRange7 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange8')) as SpeedRange2FuelRange8 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange9')) as SpeedRange2FuelRange9 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange2_FuelRange10')) as SpeedRange2FuelRange10 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange1')) as SpeedRange3FuelRange1 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange2')) as SpeedRange3FuelRange2 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange3')) as SpeedRange3FuelRange3 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange4')) as SpeedRange3FuelRange4 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange5')) as SpeedRange3FuelRange5 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange6')) as SpeedRange3FuelRange6 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange7')) as SpeedRange3FuelRange7 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange8')) as SpeedRange3FuelRange8 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange9')) as SpeedRange3FuelRange9 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange3_FuelRange10')) as SpeedRange3FuelRange10 ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange1')) as SpeedRange4FuelRange1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange2')) as SpeedRange4FuelRange2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange3')) as SpeedRange4FuelRange3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange4')) as SpeedRange4FuelRange4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange5')) as SpeedRange4FuelRange5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange6')) as SpeedRange4FuelRange6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange7')) as SpeedRange4FuelRange7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange8')) as SpeedRange4FuelRange8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange9')) as SpeedRange4FuelRange9," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange4_FuelRange10')) as SpeedRange4FuelRange10," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange1')) as SpeedRange5FuelRange1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange2')) as SpeedRange5FuelRange2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange3')) as SpeedRange5FuelRange3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange4')) as SpeedRange5FuelRange4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange5')) as SpeedRange5FuelRange5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange6')) as SpeedRange5FuelRange6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange7')) as SpeedRange5FuelRange7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange8')) as SpeedRange5FuelRange8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange9')) as SpeedRange5FuelRange9," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange5_FuelRange10')) as SpeedRange5FuelRange10," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange1')) as SpeedRange6FuelRange1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange2')) as SpeedRange6FuelRange2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange3')) as SpeedRange6FuelRange3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange4')) as SpeedRange6FuelRange4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange5')) as SpeedRange6FuelRange5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange6')) as SpeedRange6FuelRange6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange7')) as SpeedRange6FuelRange7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange8')) as SpeedRange6FuelRange8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange9')) as SpeedRange6FuelRange9," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange6_FuelRange10')) as SpeedRange6FuelRange10," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange1')) as SpeedRange7FuelRange1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange2')) as SpeedRange7FuelRange2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange3')) as SpeedRange7FuelRange3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange4')) as SpeedRange7FuelRange4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange5')) as SpeedRange7FuelRange5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange6')) as SpeedRange7FuelRange6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange7')) as SpeedRange7FuelRange7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange8')) as SpeedRange7FuelRange8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange9')) as SpeedRange7FuelRange9," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.SpeedRange7_FuelRange10')) as SpeedRange7FuelRange10," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range1')) as CoolantTempratureResidenceRange1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range2')) as CoolantTempratureResidenceRange2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range3')) as CoolantTempratureResidenceRange3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range4')) as CoolantTempratureResidenceRange4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range5')) as CoolantTempratureResidenceRange5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range6')) as CoolantTempratureResidenceRange6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range7')) as CoolantTempratureResidenceRange7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range8')) as CoolantTempratureResidenceRange8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range9')) as CoolantTempratureResidenceRange9," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range10')) as CoolantTempratureResidenceRange10," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range11')) as CoolantTempratureResidenceRange11," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Coolant_Temprature_Residence_Range12')) as CoolantTempratureResidenceRange12," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range1')) as EEGTRR1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range2')) as EEGTRR2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range3')) as EEGTRR3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range4')) as EEGTRR4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range5')) as EEGTRR5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range6')) as EEGTRR6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range7')) as EEGTRR7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range8')) as EEGTRR8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range9')) as EEGTRR9," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range10')) as EEGTRR10," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range11')) as EEGTRR11," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Exhaust_Gas_Temp_Residence_Range12')) as EEGTRR12," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range1')) as FuelTempResidenceRange1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range2')) as FuelTempResidenceRange2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range3')) as FuelTempResidenceRange3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range4')) as FuelTempResidenceRange4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range5')) as FuelTempResidenceRange5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range6')) as FuelTempResidenceRange6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range7')) as FuelTempResidenceRange7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Fuel_Temp_Residence_Range8')) as FuelTempResidenceRange8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range1')) as InletAir1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range2')) as InletAir2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range3')) as InletAir3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range4')) as InletAir4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range5')) as InletAir5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range6')) as InletAir6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range7')) as InletAir7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range8')) as InletAir8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range9')) as InletAir9," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Inlet_Air_Temp_Residence_Range10')) as InletAir10," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range1')) as Barometric1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range2')) as Barometric2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range3')) as Barometric3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range4')) as Barometric4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range5')) as Barometric5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range6')) as Barometric6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range7')) as Barometric7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range8')) as Barometric8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range9')) as Barometric9," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Barometric_Pressure_Residence_Range10')) as Barometric10," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Max_Engine_Speed')) as MaxEngineSpeed ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range1_Count')) as EOERC1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range2_Count')) as EOERC2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range3_Count')) as EOERC3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range4_Count')) as EOERC4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range5_Count')) as EOERC5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range6_Count')) as EOERC6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range7_Count')) as EOERC7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range8_Count')) as EOERC8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range9_Count')) as EOERC9," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range10_Count')) as EOERC10," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range1_Duration')) as EOERD1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range2_Duration')) as EOERD2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range3_Duration')) as EOERD3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range4_Duration')) as EOERD4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range5_Duration')) as EOERD5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range6_Duration')) as EOERD6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range7_Duration')) as EOERD7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range8_Duration')) as EOERD8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range9_Duration')) as EOERD9," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Overspeed_Event_Range10_Duration')) as EOERD10," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.Engine_Thermal_Cycles')) as EngineThermalCycles ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_EngStCnt')) as EngineStartCount ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_ECUOTIGNOnT')) as ECUOntime," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR1')) as Ambient1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR2')) as Ambient2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR3')) as Ambient3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR4')) as Ambient4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR5')) as Ambient5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR6')) as Ambient6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR7')) as Ambient7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR8')) as Ambient8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR9')) as Ambient9," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR10')) as Ambient10," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AATRR11')) as Ambient11," +
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT1')) as DOCOutlet1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT2')) as DOCOutlet2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT3')) as DOCOutlet3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT4')) as DOCOutlet4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT5')) as DOCOutlet5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT6')) as DOCOutlet6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT7')) as DOCOutlet7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT8')) as DOCOutlet8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT9')) as DOCOutlet9," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT10')) as DOCOutlet10," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT11')) as DOCOutlet11," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT12')) as DOCOutlet12," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT13')) as DOCOutlet13," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DOCOT14')) as DOCOutlet14," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_SulpMont')) as SulphurMonitor," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_SUCActRst')) as ActiveRestorations," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng1')) as Aftertreatment1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng2')) as Aftertreatment2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng3')) as Aftertreatment3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng4')) as Aftertreatment4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng5')) as Aftertreatment5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng6')) as Aftertreatment6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng7')) as Aftertreatment7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng8')) as Aftertreatment8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng9')) as Aftertreatment9," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng10')) as Aftertreatment10," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng11')) as Aftertreatment11," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng12')) as Aftertreatment12," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng13')) as Aftertreatment13," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1EGT1Rng14')) as Aftertreatment14," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng1')) as PCSDPRRng1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng2')) as PCSDPRRng2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng3')) as PCSDPRRng3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng4')) as PCSDPRRng4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng5')) as PCSDPRRng5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng6')) as PCSDPRRng6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng7')) as PCSDPRRng7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng8')) as PCSDPRRng8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng9')) as PCSDPRRng9," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_PCSDPRRng10')) as PCSDPRRng10," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_SucStRstAtmpts')) as StationaryRestoration," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_TSLRestAmpt')) as LastRestoration," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_SootMon')) as SootMonitor," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AshMon')) as AshMonitor," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1TFU')) as AftertreatmentTFU," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AT1TRT')) as AftertreatmentTRT," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR1')) as DPFOutlet1," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR2')) as DPFOutlet2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR3')) as DPFOutlet3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR4')) as DPFOutlet4," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR5')) as DPFOutlet5," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR6')) as DPFOutlet6," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR7')) as DPFOutlet7," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR8')) as DPFOutlet8," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR9')) as DPFOutlet9," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR10')) as DPFOutlet10," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR11')) as DPFOutlet11," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR12')) as DPFOutlet12," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR13')) as DPFOutlet13," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFOTR14')) as DPFOutlet14," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFARFBS')) as DieselSWITCH," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFARFBT')) as DieselTOOL," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISS')) as AftertreatmentStatus," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISIOL')) as AftertreatmentIOL," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISISLR')) as AftertreatmentISLR," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISTTIOL')) as AftertreatmentTTIOL," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISTTISLR')) as AftertreatmentLR," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_ESHTLC')) as ESHTLC," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSRNR')) as DPFSRNR," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSMRA')) as DPFSMRA," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSSRR')) as DPFSSRR," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSRS')) as DPFSRS," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSRPL2')) as DPFSRPL2," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSMRRL3')) as DPFSMRRL3," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSRA')) as DPFSRA," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_DPFSRC')) as DPFSRC," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.LOG_AARIDTISRABIBO')) as AARIDTISRABIBO " + 
						" from  "+endTAssetMonTable + " where JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 068 and serial_number in ("+SerialNumber+") and Transaction_Timestamp >= '"+startTS+"'"+ 
						" and Transaction_Timestamp <= '"+endTS+"' ";

			}
			iLogger.info("getLogDownloadPTPacketReportImpl TAssetMonQuery : "+TAssetMonQuery);
			iLogger.info("getLogDownloadPTPacketReportImpl startTAssetMonQuery : "+startTAssetMonQuery);
			iLogger.info("getLogDownloadPTPacketReportImpl endTAssetMonQuery : "+endTAssetMonQuery);
		
			ConnectMySQL connectionObj = new ConnectMySQL();
			con = connectionObj.getDatalakeConnection_3309();
			statement = con.createStatement();
			HashMap<String, String> tableMap = null;
			String hours = null;
			String transactiontimestamp =null;
			String adBlueConsumption= null;
			String dEF_Concentration=null;
			String dEF_Temperature=null;
			//String engineOnCount=null;
			String powerRatingRequest= null;
			String speedRange1FuelRange1 = null;
			String speedRange1FuelRange2 = null;
			String speedRange1FuelRange3 = null;
			String speedRange1FuelRange4 = null;
			String speedRange1FuelRange5 = null;
			String speedRange1FuelRange6 = null;
			String speedRange1FuelRange7 = null;
			String speedRange1FuelRange8 = null;
			String speedRange1FuelRange9 = null;
			String speedRange1FuelRange10 = null;
			String speedRange2FuelRange1 = null;
			String speedRange2FuelRange2 = null;
			String speedRange2FuelRange3 = null;
			String speedRange2FuelRange4 = null;
			String speedRange2FuelRange5 = null;
			String speedRange2FuelRange6 = null;
			String speedRange2FuelRange7 = null;
			String speedRange2FuelRange8 = null;
			String speedRange2FuelRange9 = null;
			String speedRange2FuelRange10 = null;
			String speedRange3FuelRange1 = null;
			String speedRange3FuelRange2 = null;
			String speedRange3FuelRange3 = null;
			String speedRange3FuelRange4 = null;
			String speedRange3FuelRange5 = null;
			String speedRange3FuelRange6 = null;
			String speedRange3FuelRange7 = null;
			String speedRange3FuelRange8 = null;
			String speedRange3FuelRange9 = null;
			String speedRange3FuelRange10 = null;
			String speedRange4FuelRange1= null;
			String speedRange4FuelRange2= null;
			String speedRange4FuelRange3= null;
			String speedRange4FuelRange4= null;
			String speedRange4FuelRange5= null;
			String speedRange4FuelRange6= null;
			String speedRange4FuelRange7= null;
			String speedRange4FuelRange8= null;
			String speedRange4FuelRange9= null;
			String speedRange4FuelRange10= null;
			String speedRange5FuelRange1= null;
			String speedRange5FuelRange2= null;
			String speedRange5FuelRange3= null;
			String speedRange5FuelRange4= null;
			String speedRange5FuelRange5= null;
			String speedRange5FuelRange6= null;
			String speedRange5FuelRange7= null;
			String speedRange5FuelRange8= null;
			String speedRange5FuelRange9= null;
			String speedRange5FuelRange10= null;
			String speedRange6FuelRange1= null;
			String speedRange6FuelRange2= null;
			String speedRange6FuelRange3= null;
			String speedRange6FuelRange4= null;
			String speedRange6FuelRange5= null;
			String speedRange6FuelRange6= null;
			String speedRange6FuelRange7= null;
			String speedRange6FuelRange8= null;
			String speedRange6FuelRange9= null;
			String speedRange6FuelRange10= null;
			String speedRange7FuelRange1= null;
			String speedRange7FuelRange2= null;
			String speedRange7FuelRange3= null;
			String speedRange7FuelRange4= null;
			String speedRange7FuelRange5= null;
			String speedRange7FuelRange6= null;
			String speedRange7FuelRange7= null;
			String speedRange7FuelRange8= null;
			String speedRange7FuelRange9= null;
			String speedRange7FuelRange10= null;
			String coolantTempratureResidenceRange1= null;
			String coolantTempratureResidenceRange2= null;
			String coolantTempratureResidenceRange3= null;
			String coolantTempratureResidenceRange4= null;
			String coolantTempratureResidenceRange5= null;
			String coolantTempratureResidenceRange6= null;
			String coolantTempratureResidenceRange7= null;
			String coolantTempratureResidenceRange8= null;
			String coolantTempratureResidenceRange9= null;
			String coolantTempratureResidenceRange10= null;
			String coolantTempratureResidenceRange11= null;
			String coolantTempratureResidenceRange12= null;
			String eEGTRR1= null;
			String eEGTRR2= null;
			String eEGTRR3= null;
			String eEGTRR4= null;
			String eEGTRR5= null;
			String eEGTRR6= null;
			String eEGTRR7= null;
			String eEGTRR8= null;
			String eEGTRR9= null;
			String eEGTRR10= null;
			String eEGTRR11= null;
			String eEGTRR12= null;
			String fuelTempResidenceRange1= null;
			String fuelTempResidenceRange2= null;
			String fuelTempResidenceRange3= null;
			String fuelTempResidenceRange4= null;
			String fuelTempResidenceRange5= null;
			String fuelTempResidenceRange6= null;
			String fuelTempResidenceRange7= null;
			String fuelTempResidenceRange8= null;
			String inletAir1= null;
			String inletAir2= null;
			String inletAir3= null;
			String inletAir4= null;
			String inletAir5= null;
			String inletAir6= null;
			String inletAir7= null;
			String inletAir8= null;
			String inletAir9= null;
			String inletAir10= null;
			String barometric1= null;
			String barometric2= null;
			String barometric3= null;
			String barometric4= null;
			String barometric5= null;
			String barometric6= null;
			String barometric7= null;
			String barometric8= null;
			String barometric9= null;
			String barometric10= null;
			String maxEngineSpeed = null;
			String eOERC1= null;
			String eOERC2= null;
			String eOERC3= null;
			String eOERC4= null;
			String eOERC5= null;
			String eOERC6= null;
			String eOERC7= null;
			String eOERC8= null;
			String eOERC9= null;
			String eOERC10= null;
			String eOERD1= null;
			String eOERD2= null;
			String eOERD3= null;
			String eOERD4= null;
			String eOERD5= null;
			String eOERD6= null;
			String eOERD7= null;
			String eOERD8= null;
			String eOERD9= null;
			String eOERD10= null;
			String engineThermalCycles = null;
			String engineStartCount = null;
			String eCUOntime= null;
			String ambient1= null;
			String ambient2= null;
			String ambient3= null;
			String ambient4= null;
			String ambient5= null;
			String ambient6= null;
			String ambient7= null;
			String ambient8= null;
			String ambient9= null;
			String ambient10= null;
			String ambient11= null;
			String dOCOutlet1= null;
			String dOCOutlet2= null;
			String dOCOutlet3= null;
			String dOCOutlet4= null;
			String dOCOutlet5= null;
			String dOCOutlet6= null;
			String dOCOutlet7= null;
			String dOCOutlet8= null;
			String dOCOutlet9= null;
			String dOCOutlet10= null;
			String dOCOutlet11= null;
			String dOCOutlet12= null;
			String dOCOutlet13= null;
			String dOCOutlet14= null;
			String sulphurMonitor= null;
			String activeRestorations= null;
			String aftertreatment1= null;
			String aftertreatment2= null;
			String aftertreatment3= null;
			String aftertreatment4= null;
			String aftertreatment5= null;
			String aftertreatment6= null;
			String aftertreatment7= null;
			String aftertreatment8= null;
			String aftertreatment9= null;
			String aftertreatment10= null;
			String aftertreatment11= null;
			String aftertreatment12= null;
			String aftertreatment13= null;
			String aftertreatment14= null;
			String pCSDPRRng1= null;
			String pCSDPRRng2= null;
			String pCSDPRRng3= null;
			String pCSDPRRng4= null;
			String pCSDPRRng5= null;
			String pCSDPRRng6= null;
			String pCSDPRRng7= null;
			String pCSDPRRng8= null;
			String pCSDPRRng9= null;
			String pCSDPRRng10= null;
			String stationaryRestoration= null;
			String lastRestoration= null;
			String sootMonitor= null;
			String ashMonitor= null;
			String aftertreatmentTFU= null;
			String aftertreatmentTRT= null;
			String dPFOutlet1= null;
			String dPFOutlet2= null;
			String dPFOutlet3= null;
			String dPFOutlet4= null;
			String dPFOutlet5= null;
			String dPFOutlet6= null;
			String dPFOutlet7= null;
			String dPFOutlet8= null;
			String dPFOutlet9= null;
			String dPFOutlet10= null;
			String dPFOutlet11= null;
			String dPFOutlet12= null;
			String dPFOutlet13= null;
			String dPFOutlet14= null;
			String dieselSWITCH= null;
			String dieselTOOL= null;
			String aftertreatmentStatus= null;
			String aftertreatmentIOL= null;
			String aftertreatmentISLR= null;
			String aftertreatmentTTIOL= null;
			String aftertreatmentLR= null;
			String eSHTLC= null;
			String dPFSRNR= null;
			String dPFSMRA= null;
			String dPFSSRR= null;
			String dPFSRS= null;
			String dPFSRPL2= null;
			String dPFSMRRL3= null;
			String dPFSRA= null;
			String dPFSRC= null;
			String aARIDTISRABIBO =null;
			String tDate=null;
			if(TAssetMonQuery !=null) {
				resultSet = statement.executeQuery(TAssetMonQuery);
				while(resultSet.next())
				{
					tableMap = new HashMap<String,String>();
					tableMap.put("Vin",resultSet.getString("Vin")); 					
					 
					hours = resultSet.getString("Hours");
					if(hours !=null) {
						tableMap.put("Hours",hours);
						}
					else {
						tableMap.put("Hours","NA");
						}
					tDate=resultSet.getString("Transactiontimestamp").substring(0,19); 
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
					transactiontimestamp = LocalDateTime.parse(tDate, dtf).format(dtf2);
					if(tDate !=null) {
						tableMap.put("Transactiontimestamp",transactiontimestamp);
						}else {
							tableMap.put("Transactiontimestamp","NA");
							}
					 adBlueConsumption= resultSet.getString("AdBlueConsumption");if(adBlueConsumption !=null) {tableMap.put("AdBlueConsumption",adBlueConsumption);}else {tableMap.put("AdBlueConsumption","NA");}
					 dEF_Concentration= resultSet.getString("DEF_Concentration");if(dEF_Concentration !=null) {tableMap.put("DEF_Concentration",dEF_Concentration);}else {tableMap.put("DEF_Concentration","NA");}
					 dEF_Temperature= resultSet.getString("DEF_Temperature");if(dEF_Temperature !=null) {tableMap.put("DEF_Temperature",dEF_Temperature);}else {tableMap.put("DEF_Temperature","NA");}
					// engineOnCount= resultSet.getString("EngineOnCount");if(engineOnCount !=null) {tableMap.put("EngineOnCount",engineOnCount);}else {tableMap.put("EngineOnCount","NA");}
					 powerRatingRequest= resultSet.getString("PowerRatingRequest");if(powerRatingRequest !=null) {tableMap.put("PowerRatingRequest",powerRatingRequest);}else {tableMap.put("PowerRatingRequest","NA");}
					 speedRange1FuelRange1 = resultSet.getString("SpeedRange1FuelRange1");if(speedRange1FuelRange1 !=null) {tableMap.put("SpeedRange1FuelRange1",speedRange1FuelRange1);}else {tableMap.put("SpeedRange1FuelRange1","NA");}
					 speedRange1FuelRange2 = resultSet.getString("SpeedRange1FuelRange2");if( speedRange1FuelRange2!=null) {tableMap.put("SpeedRange1FuelRange2",speedRange1FuelRange2);}else {tableMap.put("SpeedRange1FuelRange2","NA");}
					 speedRange1FuelRange3 = resultSet.getString("SpeedRange1FuelRange3");if( speedRange1FuelRange3!=null) {tableMap.put("SpeedRange1FuelRange3",speedRange1FuelRange3);}else {tableMap.put("SpeedRange1FuelRange3","NA");}
					 speedRange1FuelRange4 = resultSet.getString("SpeedRange1FuelRange4");if( speedRange1FuelRange4!=null) {tableMap.put("SpeedRange1FuelRange4",speedRange1FuelRange4);}else {tableMap.put("SpeedRange1FuelRange4","NA");}
					 speedRange1FuelRange5 = resultSet.getString("SpeedRange1FuelRange5");if( speedRange1FuelRange5!=null) {tableMap.put("SpeedRange1FuelRange5",speedRange1FuelRange5);}else {tableMap.put("SpeedRange1FuelRange5","NA");}
					 speedRange1FuelRange6 = resultSet.getString("SpeedRange1FuelRange6");if( speedRange1FuelRange6!=null) {tableMap.put("SpeedRange1FuelRange6",speedRange1FuelRange6);}else {tableMap.put("SpeedRange1FuelRange6","NA");}
					 speedRange1FuelRange7 = resultSet.getString("SpeedRange1FuelRange7");if( speedRange1FuelRange7!=null) {tableMap.put("SpeedRange1FuelRange7",speedRange1FuelRange7);}else {tableMap.put("SpeedRange1FuelRange7","NA");}
					 speedRange1FuelRange8 = resultSet.getString("SpeedRange1FuelRange8");if( speedRange1FuelRange8!=null) {tableMap.put("SpeedRange1FuelRange8",speedRange1FuelRange8);}else {tableMap.put("SpeedRange1FuelRange8","NA");}
					 speedRange1FuelRange9 = resultSet.getString("SpeedRange1FuelRange9");if( speedRange1FuelRange9!=null) {tableMap.put("SpeedRange1FuelRange9",speedRange1FuelRange9);}else {tableMap.put("SpeedRange1FuelRange9","NA");}
					 speedRange1FuelRange10 = resultSet.getString("SpeedRange1FuelRange10");if( speedRange1FuelRange10!=null) {tableMap.put("SpeedRange1FuelRange10",speedRange1FuelRange10);}else {tableMap.put("SpeedRange1FuelRange10","NA");}
					 speedRange2FuelRange1 = resultSet.getString("SpeedRange2FuelRange1");if( speedRange2FuelRange1!=null) {tableMap.put("SpeedRange2FuelRange1",speedRange2FuelRange1);}else {tableMap.put("SpeedRange2FuelRange1","NA");}
					 speedRange2FuelRange2 = resultSet.getString("SpeedRange2FuelRange2");if( speedRange2FuelRange2!=null) {tableMap.put("SpeedRange2FuelRange2",speedRange2FuelRange2);}else {tableMap.put("SpeedRange2FuelRange2","NA");}
					 speedRange2FuelRange3 = resultSet.getString("SpeedRange2FuelRange3");if( speedRange2FuelRange3!=null) {tableMap.put("SpeedRange2FuelRange3",speedRange2FuelRange3);}else {tableMap.put("SpeedRange2FuelRange3","NA");}
					 speedRange2FuelRange4 = resultSet.getString("SpeedRange2FuelRange4");if( speedRange2FuelRange4!=null) {tableMap.put("SpeedRange2FuelRange4",speedRange2FuelRange4);}else {tableMap.put("SpeedRange2FuelRange4","NA");}
					 speedRange2FuelRange5 = resultSet.getString("SpeedRange2FuelRange5");if( speedRange2FuelRange5!=null) {tableMap.put("SpeedRange2FuelRange5",speedRange2FuelRange5);}else {tableMap.put("SpeedRange2FuelRange5","NA");}
					 speedRange2FuelRange6 = resultSet.getString("SpeedRange2FuelRange6");if( speedRange2FuelRange6!=null) {tableMap.put("SpeedRange2FuelRange6",speedRange2FuelRange6);}else {tableMap.put("SpeedRange2FuelRange6","NA");}
					 speedRange2FuelRange7 = resultSet.getString("SpeedRange2FuelRange7");if( speedRange2FuelRange7!=null) {tableMap.put("SpeedRange2FuelRange7",speedRange2FuelRange7);}else {tableMap.put("SpeedRange2FuelRange7","NA");}
					 speedRange2FuelRange8 = resultSet.getString("SpeedRange2FuelRange8");if( speedRange2FuelRange8!=null) {tableMap.put("SpeedRange2FuelRange8",speedRange2FuelRange8);}else {tableMap.put("SpeedRange2FuelRange8","NA");}
					 speedRange2FuelRange9 = resultSet.getString("SpeedRange2FuelRange9");if( speedRange2FuelRange9!=null) {tableMap.put("SpeedRange2FuelRange9",speedRange2FuelRange9);}else {tableMap.put("SpeedRange2FuelRange9","NA");}
					 speedRange2FuelRange10 = resultSet.getString("SpeedRange2FuelRange10");if( speedRange2FuelRange10!=null) {tableMap.put("SpeedRange2FuelRange10",speedRange2FuelRange10);}else {tableMap.put("SpeedRange2FuelRange10","NA");}
					 speedRange3FuelRange1 = resultSet.getString("SpeedRange3FuelRange1");if( speedRange3FuelRange1!=null) {tableMap.put("SpeedRange3FuelRange1",speedRange3FuelRange1);}else {tableMap.put("SpeedRange3FuelRange1","NA");}
					 speedRange3FuelRange2 = resultSet.getString("SpeedRange3FuelRange2");if( speedRange3FuelRange2!=null) {tableMap.put("SpeedRange3FuelRange2",speedRange3FuelRange2);}else {tableMap.put("SpeedRange3FuelRange2","NA");}
					 speedRange3FuelRange3 = resultSet.getString("SpeedRange3FuelRange3");if( speedRange3FuelRange3!=null) {tableMap.put("SpeedRange3FuelRange3",speedRange3FuelRange3);}else {tableMap.put("SpeedRange3FuelRange3","NA");}
					 speedRange3FuelRange4 = resultSet.getString("SpeedRange3FuelRange4");if( speedRange3FuelRange4!=null) {tableMap.put("SpeedRange3FuelRange4",speedRange3FuelRange4);}else {tableMap.put("SpeedRange3FuelRange4","NA");}
					 speedRange3FuelRange5 = resultSet.getString("SpeedRange3FuelRange5");if( speedRange3FuelRange5!=null) {tableMap.put("SpeedRange3FuelRange5",speedRange3FuelRange5);}else {tableMap.put("SpeedRange3FuelRange5","NA");}
					 speedRange3FuelRange6 = resultSet.getString("SpeedRange3FuelRange6");if( speedRange3FuelRange6!=null) {tableMap.put("SpeedRange3FuelRange6",speedRange3FuelRange6);}else {tableMap.put("SpeedRange3FuelRange6","NA");}
					 speedRange3FuelRange7 = resultSet.getString("SpeedRange3FuelRange7");if( speedRange3FuelRange7!=null) {tableMap.put("SpeedRange3FuelRange7",speedRange3FuelRange7);}else {tableMap.put("SpeedRange3FuelRange7","NA");}
					 speedRange3FuelRange8 = resultSet.getString("SpeedRange3FuelRange8");if( speedRange3FuelRange8!=null) {tableMap.put("SpeedRange3FuelRange8",speedRange3FuelRange8);}else {tableMap.put("SpeedRange3FuelRange8","NA");}
					 speedRange3FuelRange9 = resultSet.getString("SpeedRange3FuelRange9");if( speedRange3FuelRange9!=null) {tableMap.put("SpeedRange3FuelRange9",speedRange3FuelRange9);}else {tableMap.put("SpeedRange3FuelRange9","NA");}
					 speedRange3FuelRange10 = resultSet.getString("SpeedRange3FuelRange10");if( speedRange3FuelRange10!=null) {tableMap.put("SpeedRange3FuelRange10",speedRange3FuelRange10);}else {tableMap.put("SpeedRange3FuelRange10","NA");}
					 speedRange4FuelRange1= resultSet.getString("SpeedRange4FuelRange1");if( speedRange4FuelRange1!=null) {tableMap.put("SpeedRange4FuelRange1",speedRange4FuelRange1);}else {tableMap.put("SpeedRange4FuelRange1","NA");}
					 speedRange4FuelRange2= resultSet.getString("SpeedRange4FuelRange2");if( speedRange4FuelRange2!=null) {tableMap.put("SpeedRange4FuelRange2",speedRange4FuelRange2);}else {tableMap.put("SpeedRange4FuelRange2","NA");}
					 speedRange4FuelRange3= resultSet.getString("SpeedRange4FuelRange3");if( speedRange4FuelRange3!=null) {tableMap.put("SpeedRange4FuelRange3",speedRange4FuelRange3);}else {tableMap.put("SpeedRange4FuelRange3","NA");}
					 speedRange4FuelRange4= resultSet.getString("SpeedRange4FuelRange4");if( speedRange4FuelRange4!=null) {tableMap.put("SpeedRange4FuelRange4",speedRange4FuelRange4);}else {tableMap.put("SpeedRange4FuelRange4","NA");}
					 speedRange4FuelRange5= resultSet.getString("SpeedRange4FuelRange5");if( speedRange4FuelRange5!=null) {tableMap.put("SpeedRange4FuelRange5",speedRange4FuelRange5);}else {tableMap.put("SpeedRange4FuelRange5","NA");}
					 speedRange4FuelRange6= resultSet.getString("SpeedRange4FuelRange6");if( speedRange4FuelRange6!=null) {tableMap.put("SpeedRange4FuelRange6",speedRange4FuelRange6);}else {tableMap.put("SpeedRange4FuelRange6","NA");}
					 speedRange4FuelRange7= resultSet.getString("SpeedRange4FuelRange7");if( speedRange4FuelRange7!=null) {tableMap.put("SpeedRange4FuelRange7",speedRange4FuelRange7);}else {tableMap.put("SpeedRange4FuelRange7","NA");}
					 speedRange4FuelRange8= resultSet.getString("SpeedRange4FuelRange8");if( speedRange4FuelRange8!=null) {tableMap.put("SpeedRange4FuelRange8",speedRange4FuelRange8);}else {tableMap.put("SpeedRange4FuelRange8","NA");}
					 speedRange4FuelRange9= resultSet.getString("SpeedRange4FuelRange9");if( speedRange4FuelRange9!=null) {tableMap.put("SpeedRange4FuelRange9",speedRange4FuelRange9);}else {tableMap.put("SpeedRange4FuelRange9","NA");}
					 speedRange4FuelRange10= resultSet.getString("SpeedRange4FuelRange10");if( speedRange4FuelRange10!=null) {tableMap.put("SpeedRange4FuelRange10",speedRange4FuelRange10);}else {tableMap.put("SpeedRange4FuelRange10","NA");}
					 speedRange5FuelRange1= resultSet.getString("SpeedRange5FuelRange1");if( speedRange5FuelRange1!=null) {tableMap.put("SpeedRange5FuelRange1",speedRange5FuelRange1);}else {tableMap.put("SpeedRange5FuelRange1","NA");}
					 speedRange5FuelRange2= resultSet.getString("SpeedRange5FuelRange2");if( speedRange5FuelRange2!=null) {tableMap.put("SpeedRange5FuelRange2",speedRange5FuelRange2);}else {tableMap.put("SpeedRange5FuelRange2","NA");}
					 speedRange5FuelRange3= resultSet.getString("SpeedRange5FuelRange3");if( speedRange5FuelRange3!=null) {tableMap.put("SpeedRange5FuelRange3",speedRange5FuelRange3);}else {tableMap.put("SpeedRange5FuelRange3","NA");}
					 speedRange5FuelRange4= resultSet.getString("SpeedRange5FuelRange4");if( speedRange5FuelRange4!=null) {tableMap.put("SpeedRange5FuelRange4",speedRange5FuelRange4);}else {tableMap.put("SpeedRange5FuelRange4","NA");}
					 speedRange5FuelRange5= resultSet.getString("SpeedRange5FuelRange5");if( speedRange5FuelRange5!=null) {tableMap.put("SpeedRange5FuelRange5",speedRange5FuelRange5);}else {tableMap.put("SpeedRange5FuelRange5","NA");}
					 speedRange5FuelRange6= resultSet.getString("SpeedRange5FuelRange6");if( speedRange5FuelRange6!=null) {tableMap.put("SpeedRange5FuelRange6",speedRange5FuelRange6);}else {tableMap.put("SpeedRange5FuelRange6","NA");}
					 speedRange5FuelRange7= resultSet.getString("SpeedRange5FuelRange7");if( speedRange5FuelRange7!=null) {tableMap.put("SpeedRange5FuelRange7",speedRange5FuelRange7);}else {tableMap.put("SpeedRange5FuelRange7","NA");}
					 speedRange5FuelRange8= resultSet.getString("SpeedRange5FuelRange8");if( speedRange5FuelRange8!=null) {tableMap.put("SpeedRange5FuelRange8",speedRange5FuelRange8);}else {tableMap.put("SpeedRange5FuelRange8","NA");}
					 speedRange5FuelRange9= resultSet.getString("SpeedRange5FuelRange9");if( speedRange5FuelRange9!=null) {tableMap.put("SpeedRange5FuelRange9",speedRange5FuelRange9);}else {tableMap.put("SpeedRange5FuelRange9","NA");}
					 speedRange5FuelRange10= resultSet.getString("SpeedRange5FuelRange10");if( speedRange5FuelRange10!=null) {tableMap.put("SpeedRange5FuelRange10",speedRange5FuelRange10);}else {tableMap.put("SpeedRange5FuelRange10","NA");}
					 speedRange6FuelRange1= resultSet.getString("SpeedRange6FuelRange1");if( speedRange6FuelRange1!=null) {tableMap.put("SpeedRange6FuelRange1",speedRange6FuelRange1);}else {tableMap.put("SpeedRange6FuelRange1","NA");}
					 speedRange6FuelRange2= resultSet.getString("SpeedRange6FuelRange2");if( speedRange6FuelRange2!=null) {tableMap.put("SpeedRange6FuelRange2",speedRange6FuelRange2);}else {tableMap.put("SpeedRange6FuelRange2","NA");}
					 speedRange6FuelRange3= resultSet.getString("SpeedRange6FuelRange3");if( speedRange6FuelRange3!=null) {tableMap.put("SpeedRange6FuelRange3",speedRange6FuelRange3);}else {tableMap.put("SpeedRange6FuelRange3","NA");}
					 speedRange6FuelRange4= resultSet.getString("SpeedRange6FuelRange4");if( speedRange6FuelRange4!=null) {tableMap.put("SpeedRange6FuelRange4",speedRange6FuelRange4);}else {tableMap.put("SpeedRange6FuelRange4","NA");}
					 speedRange6FuelRange5= resultSet.getString("SpeedRange6FuelRange5");if( speedRange6FuelRange5!=null) {tableMap.put("SpeedRange6FuelRange5",speedRange6FuelRange5);}else {tableMap.put("SpeedRange6FuelRange5","NA");}
					 speedRange6FuelRange6= resultSet.getString("SpeedRange6FuelRange6");if( speedRange6FuelRange6!=null) {tableMap.put("SpeedRange6FuelRange6",speedRange6FuelRange6);}else {tableMap.put("SpeedRange6FuelRange6","NA");}
					 speedRange6FuelRange7= resultSet.getString("SpeedRange6FuelRange7");if( speedRange6FuelRange7!=null) {tableMap.put("SpeedRange6FuelRange7",speedRange6FuelRange7);}else {tableMap.put("SpeedRange6FuelRange7","NA");}
					 speedRange6FuelRange8= resultSet.getString("SpeedRange6FuelRange8");if( speedRange6FuelRange8!=null) {tableMap.put("SpeedRange6FuelRange8",speedRange6FuelRange8);}else {tableMap.put("SpeedRange6FuelRange8","NA");}
					 speedRange6FuelRange9= resultSet.getString("SpeedRange6FuelRange9");if( speedRange6FuelRange9!=null) {tableMap.put("SpeedRange6FuelRange9",speedRange6FuelRange9);}else {tableMap.put("SpeedRange6FuelRange9","NA");}
					 speedRange6FuelRange10= resultSet.getString("SpeedRange6FuelRange10");if( speedRange6FuelRange10!=null) {tableMap.put("SpeedRange6FuelRange10",speedRange6FuelRange10);}else {tableMap.put("SpeedRange6FuelRange10","NA");}
					 speedRange7FuelRange1= resultSet.getString("SpeedRange7FuelRange1");if( speedRange7FuelRange1!=null) {tableMap.put("SpeedRange7FuelRange1",speedRange7FuelRange1);}else {tableMap.put("SpeedRange7FuelRange1","NA");}
					 speedRange7FuelRange2= resultSet.getString("SpeedRange7FuelRange2");if( speedRange7FuelRange2!=null) {tableMap.put("SpeedRange7FuelRange2",speedRange7FuelRange2);}else {tableMap.put("SpeedRange7FuelRange2","NA");}
					 speedRange7FuelRange3= resultSet.getString("SpeedRange7FuelRange3");if( speedRange7FuelRange3!=null) {tableMap.put("SpeedRange7FuelRange3",speedRange7FuelRange3);}else {tableMap.put("SpeedRange7FuelRange3","NA");}
					 speedRange7FuelRange4= resultSet.getString("SpeedRange7FuelRange4");if( speedRange7FuelRange4!=null) {tableMap.put("SpeedRange7FuelRange4",speedRange7FuelRange4);}else {tableMap.put("SpeedRange7FuelRange4","NA");}
					 speedRange7FuelRange5= resultSet.getString("SpeedRange7FuelRange5");if( speedRange7FuelRange5!=null) {tableMap.put("SpeedRange7FuelRange5",speedRange7FuelRange5);}else {tableMap.put("SpeedRange7FuelRange5","NA");}
					 speedRange7FuelRange6= resultSet.getString("SpeedRange7FuelRange6");if( speedRange7FuelRange6!=null) {tableMap.put("SpeedRange7FuelRange6",speedRange7FuelRange6);}else {tableMap.put("SpeedRange7FuelRange6","NA");}
					 speedRange7FuelRange7= resultSet.getString("SpeedRange7FuelRange7");if( speedRange7FuelRange7!=null) {tableMap.put("SpeedRange7FuelRange7",speedRange7FuelRange7);}else {tableMap.put("SpeedRange7FuelRange7","NA");}
					 speedRange7FuelRange8= resultSet.getString("SpeedRange7FuelRange8");if( speedRange7FuelRange8!=null) {tableMap.put("SpeedRange7FuelRange8",speedRange7FuelRange8);}else {tableMap.put("SpeedRange7FuelRange8","NA");}
					 speedRange7FuelRange9= resultSet.getString("SpeedRange7FuelRange9");if( speedRange7FuelRange9!=null) {tableMap.put("SpeedRange7FuelRange9",speedRange7FuelRange9);}else {tableMap.put("SpeedRange7FuelRange9","NA");}
					 speedRange7FuelRange10= resultSet.getString("SpeedRange7FuelRange10");if( speedRange7FuelRange10!=null) {tableMap.put("SpeedRange7FuelRange10",speedRange7FuelRange10);}else {tableMap.put("SpeedRange7FuelRange10","NA");}
					 coolantTempratureResidenceRange1= resultSet.getString("CoolantTempratureResidenceRange1");if( coolantTempratureResidenceRange1!=null) {tableMap.put("CoolantTempratureResidenceRange1",coolantTempratureResidenceRange1);}else {tableMap.put("CoolantTempratureResidenceRange1","NA");}
					 coolantTempratureResidenceRange2= resultSet.getString("CoolantTempratureResidenceRange2");if( coolantTempratureResidenceRange2!=null) {tableMap.put("CoolantTempratureResidenceRange2",coolantTempratureResidenceRange2);}else {tableMap.put("CoolantTempratureResidenceRange2","NA");}
					 coolantTempratureResidenceRange3= resultSet.getString("CoolantTempratureResidenceRange3");if( coolantTempratureResidenceRange3!=null) {tableMap.put("CoolantTempratureResidenceRange3",coolantTempratureResidenceRange3);}else {tableMap.put("CoolantTempratureResidenceRange3","NA");}
					 coolantTempratureResidenceRange4= resultSet.getString("CoolantTempratureResidenceRange4");if( coolantTempratureResidenceRange4!=null) {tableMap.put("CoolantTempratureResidenceRange4",coolantTempratureResidenceRange4);}else {tableMap.put("CoolantTempratureResidenceRange4","NA");}
					 coolantTempratureResidenceRange5= resultSet.getString("CoolantTempratureResidenceRange5");if( coolantTempratureResidenceRange5!=null) {tableMap.put("CoolantTempratureResidenceRange5",coolantTempratureResidenceRange5);}else {tableMap.put("CoolantTempratureResidenceRange5","NA");}
					 coolantTempratureResidenceRange6= resultSet.getString("CoolantTempratureResidenceRange6");if( coolantTempratureResidenceRange6!=null) {tableMap.put("CoolantTempratureResidenceRange6",coolantTempratureResidenceRange6);}else {tableMap.put("CoolantTempratureResidenceRange6","NA");}
					 coolantTempratureResidenceRange7= resultSet.getString("CoolantTempratureResidenceRange7");if( coolantTempratureResidenceRange7!=null) {tableMap.put("CoolantTempratureResidenceRange7",coolantTempratureResidenceRange7);}else {tableMap.put("CoolantTempratureResidenceRange7","NA");}
					 coolantTempratureResidenceRange8= resultSet.getString("CoolantTempratureResidenceRange8");if( coolantTempratureResidenceRange8!=null) {tableMap.put("CoolantTempratureResidenceRange8",coolantTempratureResidenceRange8);}else {tableMap.put("CoolantTempratureResidenceRange8","NA");}
					 coolantTempratureResidenceRange9= resultSet.getString("CoolantTempratureResidenceRange9");if( coolantTempratureResidenceRange9!=null) {tableMap.put("CoolantTempratureResidenceRange9",coolantTempratureResidenceRange9);}else {tableMap.put("CoolantTempratureResidenceRange19","NA");}
					 coolantTempratureResidenceRange10= resultSet.getString("CoolantTempratureResidenceRange10");if( coolantTempratureResidenceRange10!=null) {tableMap.put("CoolantTempratureResidenceRange10",coolantTempratureResidenceRange10);}else {tableMap.put("CoolantTempratureResidenceRange10","NA");}
					 coolantTempratureResidenceRange11= resultSet.getString("CoolantTempratureResidenceRange11");if( coolantTempratureResidenceRange11!=null) {tableMap.put("CoolantTempratureResidenceRange11",coolantTempratureResidenceRange11);}else {tableMap.put("CoolantTempratureResidenceRange11","NA");}
					 coolantTempratureResidenceRange12= resultSet.getString("CoolantTempratureResidenceRange12");if( coolantTempratureResidenceRange12!=null) {tableMap.put("CoolantTempratureResidenceRange12",coolantTempratureResidenceRange12);}else {tableMap.put("CoolantTempratureResidenceRange12","NA");}
					 eEGTRR1= resultSet.getString("EEGTRR1");if( eEGTRR1!=null) {tableMap.put("EEGTRR1",eEGTRR1);}else {tableMap.put("EEGTRR1","NA");}
					 eEGTRR2= resultSet.getString("EEGTRR2");if( eEGTRR2!=null) {tableMap.put("EEGTRR2",eEGTRR2);}else {tableMap.put("EEGTRR2","NA");}
					 eEGTRR3= resultSet.getString("EEGTRR3");if( eEGTRR3!=null) {tableMap.put("EEGTRR3",eEGTRR3);}else {tableMap.put("EEGTRR3","NA");}
					 eEGTRR4= resultSet.getString("EEGTRR4");if( eEGTRR4!=null) {tableMap.put("EEGTRR4",eEGTRR4);}else {tableMap.put("EEGTRR4","NA");}
					 eEGTRR5= resultSet.getString("EEGTRR5");if( eEGTRR5!=null) {tableMap.put("EEGTRR5",eEGTRR5);}else {tableMap.put("EEGTRR5","NA");}
					 eEGTRR6= resultSet.getString("EEGTRR6");if( eEGTRR6!=null) {tableMap.put("EEGTRR6",eEGTRR6);}else {tableMap.put("EEGTRR6","NA");}
					 eEGTRR7= resultSet.getString("EEGTRR7");if( eEGTRR7!=null) {tableMap.put("EEGTRR7",eEGTRR7);}else {tableMap.put("EEGTRR7","NA");}
					 eEGTRR8= resultSet.getString("EEGTRR8");if( eEGTRR8!=null) {tableMap.put("EEGTRR8",eEGTRR8);}else {tableMap.put("EEGTRR8","NA");}
					 eEGTRR9= resultSet.getString("EEGTRR9");if( eEGTRR9!=null) {tableMap.put("EEGTRR9",eEGTRR9);}else {tableMap.put("EEGTRR9","NA");}
					 eEGTRR10= resultSet.getString("EEGTRR10");if( eEGTRR10!=null) {tableMap.put("EEGTRR10",eEGTRR10);}else {tableMap.put("EEGTRR10","NA");}
					 eEGTRR11= resultSet.getString("EEGTRR11");if( eEGTRR11!=null) {tableMap.put("EEGTRR11",eEGTRR11);}else {tableMap.put("EEGTRR11","NA");}
					 eEGTRR12= resultSet.getString("EEGTRR12");if( eEGTRR12!=null) {tableMap.put("EEGTRR12",eEGTRR12);}else {tableMap.put("EEGTRR12","NA");}
					 fuelTempResidenceRange1= resultSet.getString("FuelTempResidenceRange1");if( fuelTempResidenceRange1!=null) {tableMap.put("FuelTempResidenceRange1", fuelTempResidenceRange1);}else {tableMap.put("FuelTempResidenceRange1","NA");}
					 fuelTempResidenceRange2= resultSet.getString("FuelTempResidenceRange2");if( fuelTempResidenceRange2!=null) {tableMap.put("FuelTempResidenceRange2", fuelTempResidenceRange2);}else {tableMap.put("FuelTempResidenceRange2","NA");}
					 fuelTempResidenceRange3= resultSet.getString("FuelTempResidenceRange3");if( fuelTempResidenceRange3!=null) {tableMap.put("FuelTempResidenceRange3", fuelTempResidenceRange3);}else {tableMap.put("FuelTempResidenceRange3","NA");}
					 fuelTempResidenceRange4= resultSet.getString("FuelTempResidenceRange4");if( fuelTempResidenceRange4!=null) {tableMap.put("FuelTempResidenceRange4", fuelTempResidenceRange4);}else {tableMap.put("FuelTempResidenceRange4","NA");}
					 fuelTempResidenceRange5= resultSet.getString("FuelTempResidenceRange5");if( fuelTempResidenceRange5!=null) {tableMap.put("FuelTempResidenceRange5", fuelTempResidenceRange5);}else {tableMap.put("FuelTempResidenceRange5","NA");}
					 fuelTempResidenceRange6= resultSet.getString("FuelTempResidenceRange6");if( fuelTempResidenceRange6!=null) {tableMap.put("FuelTempResidenceRange6", fuelTempResidenceRange6);}else {tableMap.put("FuelTempResidenceRange6","NA");}
					 fuelTempResidenceRange7= resultSet.getString("FuelTempResidenceRange7");if( fuelTempResidenceRange7!=null) {tableMap.put("FuelTempResidenceRange7", fuelTempResidenceRange7);}else {tableMap.put("FuelTempResidenceRange7","NA");}
					 fuelTempResidenceRange8= resultSet.getString("FuelTempResidenceRange8");if( fuelTempResidenceRange8!=null) {tableMap.put("FuelTempResidenceRange8", fuelTempResidenceRange8);}else {tableMap.put("FuelTempResidenceRange8","NA");}
					 inletAir1= resultSet.getString("InletAir1");if(inletAir1!=null) {tableMap.put("InletAir1",inletAir1);}else {tableMap.put("InletAir1","NA");}
					 inletAir2= resultSet.getString("InletAir2");if(inletAir2!=null) {tableMap.put("InletAir2",inletAir2);}else {tableMap.put("InletAir2","NA");}
					 inletAir3= resultSet.getString("InletAir3");if(inletAir3!=null) {tableMap.put("InletAir3",inletAir3);}else {tableMap.put("InletAir3","NA");}
					 inletAir4= resultSet.getString("InletAir4");if(inletAir4!=null) {tableMap.put("InletAir4",inletAir4);}else {tableMap.put("InletAir4","NA");}
					 inletAir5= resultSet.getString("InletAir5");if(inletAir5!=null) {tableMap.put("InletAir5",inletAir5);}else {tableMap.put("InletAir5","NA");}
					 inletAir6= resultSet.getString("InletAir6");if(inletAir6!=null) {tableMap.put("InletAir6",inletAir6);}else {tableMap.put("InletAir6","NA");}
					 inletAir7= resultSet.getString("InletAir7");if(inletAir7!=null) {tableMap.put("InletAir7",inletAir7);}else {tableMap.put("InletAir7","NA");}
					 inletAir8= resultSet.getString("InletAir8");if(inletAir8!=null) {tableMap.put("InletAir8",inletAir8);}else {tableMap.put("InletAir8","NA");}
					 inletAir9= resultSet.getString("InletAir9");if(inletAir9!=null) {tableMap.put("InletAir9",inletAir9);}else {tableMap.put("InletAir9","NA");}
					 inletAir10= resultSet.getString("InletAir10");if(inletAir10!=null) {tableMap.put("InletAir10",inletAir10);}else {tableMap.put("InletAir10","NA");}
					 barometric1= resultSet.getString("Barometric1");if(barometric1!=null) {tableMap.put("Barometric1",barometric1);}else {tableMap.put("Barometric1","NA");}
					 barometric2= resultSet.getString("Barometric2");if(barometric2!=null) {tableMap.put("Barometric2",barometric2);}else {tableMap.put("Barometric2","NA");}
					 barometric3= resultSet.getString("Barometric3");if(barometric3!=null) {tableMap.put("Barometric3",barometric3);}else {tableMap.put("Barometric3","NA");}
					 barometric4= resultSet.getString("Barometric4");if(barometric4!=null) {tableMap.put("Barometric4",barometric4);}else {tableMap.put("Barometric4","NA");}
					 barometric5= resultSet.getString("Barometric5");if(barometric5!=null) {tableMap.put("Barometric5",barometric5);}else {tableMap.put("Barometric5","NA");}
					 barometric6= resultSet.getString("Barometric6");if(barometric6!=null) {tableMap.put("Barometric6",barometric6);}else {tableMap.put("Barometric6","NA");}
					 barometric7= resultSet.getString("Barometric7");if(barometric7!=null) {tableMap.put("Barometric7",barometric7);}else {tableMap.put("Barometric7","NA");}
					 barometric8= resultSet.getString("Barometric8");if(barometric8!=null) {tableMap.put("Barometric8",barometric8);}else {tableMap.put("Barometric8","NA");}
					 barometric9= resultSet.getString("Barometric9");if(barometric9!=null) {tableMap.put("Barometric9",barometric9);}else {tableMap.put("Barometric9","NA");}
					 barometric10= resultSet.getString("Barometric10");if(barometric10!=null) {tableMap.put("Barometric10",barometric10);}else {tableMap.put("Barometric10","NA");}
					 maxEngineSpeed = resultSet.getString("MaxEngineSpeed");if( maxEngineSpeed!=null) {tableMap.put("MaxEngineSpeed",maxEngineSpeed);}else {tableMap.put("MaxEngineSpeed","NA");}
					 eOERC1= resultSet.getString("EOERC1");if(eOERC1!=null) {tableMap.put("EOERC1",eOERC1);}else {tableMap.put("EOERC1","NA");}
					 eOERC2= resultSet.getString("EOERC2");if(eOERC2!=null) {tableMap.put("EOERC2",eOERC2);}else {tableMap.put("EOERC2","NA");}
					 eOERC3= resultSet.getString("EOERC3");if(eOERC3!=null) {tableMap.put("EOERC3",eOERC3);}else {tableMap.put("EOERC3","NA");}
					 eOERC4= resultSet.getString("EOERC4");if(eOERC4!=null) {tableMap.put("EOERC4",eOERC4);}else {tableMap.put("EOERC4","NA");}
					 eOERC5= resultSet.getString("EOERC5");if(eOERC5!=null) {tableMap.put("EOERC5",eOERC5);}else {tableMap.put("EOERC5","NA");}
					 eOERC6= resultSet.getString("EOERC6");if(eOERC6!=null) {tableMap.put("EOERC6",eOERC6);}else {tableMap.put("EOERC6","NA");}
					 eOERC7= resultSet.getString("EOERC7");if(eOERC7!=null) {tableMap.put("EOERC7",eOERC7);}else {tableMap.put("EOERC7","NA");}
					 eOERC8= resultSet.getString("EOERC8");if(eOERC8!=null) {tableMap.put("EOERC8",eOERC8);}else {tableMap.put("EOERC8","NA");}
					 eOERC9= resultSet.getString("EOERC9");if(eOERC9!=null) {tableMap.put("EOERC9",eOERC9);}else {tableMap.put("EOERC9","NA");}
					 eOERC10= resultSet.getString("EOERC10");if(eOERC10!=null) {tableMap.put("EOERC10",eOERC10);}else {tableMap.put("EOERC10","NA");}
					 eOERD1= resultSet.getString("EOERD1");if(eOERD1!=null) {tableMap.put("EOERD1",eOERD1);}else {tableMap.put("EOERD1","NA");}
					 eOERD2= resultSet.getString("EOERD2");if(eOERD2!=null) {tableMap.put("EOERD2",eOERD2);}else {tableMap.put("EOERD2","NA");}
					 eOERD3= resultSet.getString("EOERD3");if(eOERD3!=null) {tableMap.put("EOERD3",eOERD3);}else {tableMap.put("EOERD3","NA");}
					 eOERD4= resultSet.getString("EOERD4");if(eOERD4!=null) {tableMap.put("EOERD4",eOERD4);}else {tableMap.put("EOERD4","NA");}
					 eOERD5= resultSet.getString("EOERD5");if(eOERD5!=null) {tableMap.put("EOERD5",eOERD5);}else {tableMap.put("EOERD5","NA");}
					 eOERD6= resultSet.getString("EOERD6");if(eOERD6!=null) {tableMap.put("EOERD6",eOERD6);}else {tableMap.put("EOERD6","NA");}
					 eOERD7= resultSet.getString("EOERD7");if(eOERD7!=null) {tableMap.put("EOERD7",eOERD7);}else {tableMap.put("EOERD7","NA");}
					 eOERD8= resultSet.getString("EOERD8");if(eOERD8!=null) {tableMap.put("EOERD8",eOERD8);}else {tableMap.put("EOERD8","NA");}
					 eOERD9= resultSet.getString("EOERD9");if(eOERD9!=null) {tableMap.put("EOERD9",eOERD9);}else {tableMap.put("EOERD9","NA");}
					 eOERD10= resultSet.getString("EOERD10");if(eOERD10!=null) {tableMap.put("EOERD10",eOERD10);}else {tableMap.put("EOERD10","NA");}
					 engineThermalCycles = resultSet.getString("EngineThermalCycles");if( engineThermalCycles!=null) {tableMap.put("EngineThermalCycles",engineThermalCycles);}else {tableMap.put("EngineThermalCycles","NA");}
					 engineStartCount = resultSet.getString("EngineStartCount");if( engineStartCount!=null) {tableMap.put("EngineStartCount",engineStartCount);}else {tableMap.put("EngineStartCount","NA");}
					 eCUOntime= resultSet.getString("ECUOntime");if( eCUOntime!=null) {tableMap.put("ECUOntime",eCUOntime);}else {tableMap.put("ECUOntime","NA");}
					 ambient1= resultSet.getString("Ambient1");if( ambient1!=null) {tableMap.put("Ambient1", ambient1);}else {tableMap.put("Ambient1","NA");}
					 ambient2= resultSet.getString("Ambient2");if( ambient2!=null) {tableMap.put("Ambient2", ambient2);}else {tableMap.put("Ambient2","NA");}
					 ambient3= resultSet.getString("Ambient3");if( ambient3!=null) {tableMap.put("Ambient3", ambient3);}else {tableMap.put("Ambient3","NA");}
					 ambient4= resultSet.getString("Ambient4");if( ambient4!=null) {tableMap.put("Ambient4", ambient4);}else {tableMap.put("Ambient4","NA");}
					 ambient5= resultSet.getString("Ambient5");if( ambient5!=null) {tableMap.put("Ambient5", ambient5);}else {tableMap.put("Ambient5","NA");}
					 ambient6= resultSet.getString("Ambient6");if( ambient6!=null) {tableMap.put("Ambient6", ambient6);}else {tableMap.put("Ambient6","NA");}
					 ambient7= resultSet.getString("Ambient7");if( ambient7!=null) {tableMap.put("Ambient7", ambient7);}else {tableMap.put("Ambient7","NA");}
					 ambient8= resultSet.getString("Ambient8");if( ambient8!=null) {tableMap.put("Ambient8", ambient8);}else {tableMap.put("Ambient8","NA");}
					 ambient9= resultSet.getString("Ambient9");if( ambient9!=null) {tableMap.put("Ambient9", ambient9);}else {tableMap.put("Ambient9","NA");}
					 ambient10= resultSet.getString("Ambient10");if( ambient10!=null) {tableMap.put("Ambient10", ambient10);}else {tableMap.put("Ambient10","NA");}
					 ambient11= resultSet.getString("Ambient11");if( ambient11!=null) {tableMap.put("Ambient11", ambient11);}else {tableMap.put("Ambient11","NA");}
					 dOCOutlet1= resultSet.getString("DOCOutlet1");if(dOCOutlet1!=null) {tableMap.put("DOCOutlet1",dOCOutlet1);}else {tableMap.put("DOCOutlet1","NA");}
					 dOCOutlet2= resultSet.getString("DOCOutlet2");if(dOCOutlet2!=null) {tableMap.put("DOCOutlet2",dOCOutlet2);}else {tableMap.put("DOCOutlet2","NA");}
					 dOCOutlet3= resultSet.getString("DOCOutlet3");if(dOCOutlet3!=null) {tableMap.put("DOCOutlet3",dOCOutlet3);}else {tableMap.put("DOCOutlet3","NA");}
					 dOCOutlet4= resultSet.getString("DOCOutlet4");if(dOCOutlet4!=null) {tableMap.put("DOCOutlet4",dOCOutlet4);}else {tableMap.put("DOCOutlet4","NA");}
					 dOCOutlet5= resultSet.getString("DOCOutlet5");if(dOCOutlet5!=null) {tableMap.put("DOCOutlet5",dOCOutlet5);}else {tableMap.put("DOCOutlet5","NA");}
					 dOCOutlet6= resultSet.getString("DOCOutlet6");if(dOCOutlet6!=null) {tableMap.put("DOCOutlet6",dOCOutlet6);}else {tableMap.put("DOCOutlet6","NA");}
					 dOCOutlet7= resultSet.getString("DOCOutlet7");if(dOCOutlet7!=null) {tableMap.put("DOCOutlet7",dOCOutlet7);}else {tableMap.put("DOCOutlet7","NA");}
					 dOCOutlet8= resultSet.getString("DOCOutlet8");if(dOCOutlet8!=null) {tableMap.put("DOCOutlet8",dOCOutlet8);}else {tableMap.put("DOCOutlet8","NA");}
					 dOCOutlet9= resultSet.getString("DOCOutlet9");if(dOCOutlet9!=null) {tableMap.put("DOCOutlet9",dOCOutlet9);}else {tableMap.put("DOCOutlet9","NA");}
					 dOCOutlet10= resultSet.getString("DOCOutlet10");if(dOCOutlet10!=null) {tableMap.put("DOCOutlet10",dOCOutlet10);}else {tableMap.put("DOCOutlet10","NA");}
					 dOCOutlet11= resultSet.getString("DOCOutlet11");if(dOCOutlet11!=null) {tableMap.put("DOCOutlet11",dOCOutlet11);}else {tableMap.put("DOCOutlet11","NA");}
					 dOCOutlet12= resultSet.getString("DOCOutlet12");if(dOCOutlet12!=null) {tableMap.put("DOCOutlet12",dOCOutlet12);}else {tableMap.put("DOCOutlet12","NA");}
					 dOCOutlet13= resultSet.getString("DOCOutlet13");if(dOCOutlet13!=null) {tableMap.put("DOCOutlet13",dOCOutlet13);}else {tableMap.put("DOCOutlet13","NA");}
					 dOCOutlet14= resultSet.getString("DOCOutlet14");if(dOCOutlet14!=null) {tableMap.put("DOCOutlet14",dOCOutlet14);}else {tableMap.put("DOCOutlet14","NA");}
					 sulphurMonitor= resultSet.getString("SulphurMonitor");if( sulphurMonitor!=null) {tableMap.put("SulphurMonitor",sulphurMonitor);}else {tableMap.put("sulphurMonitor","NA");}
					 activeRestorations= resultSet.getString("ActiveRestorations");if( activeRestorations!=null) {tableMap.put("ActiveRestorations",activeRestorations);}else {tableMap.put("ActiveRestorations","NA");}
					 aftertreatment1= resultSet.getString("Aftertreatment1");if(aftertreatment1!=null) {tableMap.put("Aftertreatment1",aftertreatment1);}else {tableMap.put("Aftertreatment1","NA");}
					 aftertreatment2= resultSet.getString("Aftertreatment2");if(aftertreatment2!=null) {tableMap.put("Aftertreatment2",aftertreatment2);}else {tableMap.put("Aftertreatment2","NA");}
					 aftertreatment3= resultSet.getString("Aftertreatment3");if(aftertreatment3!=null) {tableMap.put("Aftertreatment3",aftertreatment3);}else {tableMap.put("Aftertreatment3","NA");}
					 aftertreatment4= resultSet.getString("Aftertreatment4");if(aftertreatment4!=null) {tableMap.put("Aftertreatment4",aftertreatment4);}else {tableMap.put("Aftertreatment4","NA");}
					 aftertreatment5= resultSet.getString("Aftertreatment5");if(aftertreatment5!=null) {tableMap.put("Aftertreatment5",aftertreatment5);}else {tableMap.put("Aftertreatment5","NA");}
					 aftertreatment6= resultSet.getString("Aftertreatment6");if(aftertreatment6!=null) {tableMap.put("Aftertreatment6",aftertreatment6);}else {tableMap.put("Aftertreatment6","NA");}
					 aftertreatment7= resultSet.getString("Aftertreatment7");if(aftertreatment7!=null) {tableMap.put("Aftertreatment7",aftertreatment7);}else {tableMap.put("Aftertreatment7","NA");}
					 aftertreatment8= resultSet.getString("Aftertreatment8");if(aftertreatment8!=null) {tableMap.put("Aftertreatment8",aftertreatment8);}else {tableMap.put("Aftertreatment8","NA");}
					 aftertreatment9= resultSet.getString("Aftertreatment9");if(aftertreatment9!=null) {tableMap.put("Aftertreatment9",aftertreatment9);}else {tableMap.put("Aftertreatment9","NA");}
					 aftertreatment10= resultSet.getString("Aftertreatment10");if(aftertreatment10!=null) {tableMap.put("Aftertreatment10",aftertreatment10);}else {tableMap.put("Aftertreatment10","NA");}
					 aftertreatment11= resultSet.getString("Aftertreatment11");if(aftertreatment11!=null) {tableMap.put("Aftertreatment11",aftertreatment11);}else {tableMap.put("Aftertreatment11","NA");}
					 aftertreatment12= resultSet.getString("Aftertreatment12");if(aftertreatment12!=null) {tableMap.put("Aftertreatment12",aftertreatment12);}else {tableMap.put("Aftertreatment12","NA");}
					 aftertreatment13= resultSet.getString("Aftertreatment13");if(aftertreatment13!=null) {tableMap.put("Aftertreatment13",aftertreatment13);}else {tableMap.put("Aftertreatment13","NA");}
					 aftertreatment14= resultSet.getString("Aftertreatment14");if(aftertreatment14!=null) {tableMap.put("Aftertreatment14",aftertreatment14);}else {tableMap.put("Aftertreatment14","NA");}
					 pCSDPRRng1= resultSet.getString("PCSDPRRng1");if(pCSDPRRng1!=null) {tableMap.put("PCSDPRRng1",pCSDPRRng1);}else {tableMap.put("PCSDPRRng1","NA");}
					 pCSDPRRng2= resultSet.getString("PCSDPRRng2");if(pCSDPRRng2!=null) {tableMap.put("PCSDPRRng2",pCSDPRRng2);}else {tableMap.put("PCSDPRRng2","NA");}
					 pCSDPRRng3= resultSet.getString("PCSDPRRng3");if(pCSDPRRng3!=null) {tableMap.put("PCSDPRRng3",pCSDPRRng3);}else {tableMap.put("PCSDPRRng3","NA");}
					 pCSDPRRng4= resultSet.getString("PCSDPRRng4");if(pCSDPRRng4!=null) {tableMap.put("PCSDPRRng4",pCSDPRRng4);}else {tableMap.put("PCSDPRRng4","NA");}
					 pCSDPRRng5= resultSet.getString("PCSDPRRng5");if(pCSDPRRng5!=null) {tableMap.put("PCSDPRRng5",pCSDPRRng5);}else {tableMap.put("PCSDPRRng5","NA");}
					 pCSDPRRng6= resultSet.getString("PCSDPRRng6");if(pCSDPRRng6!=null) {tableMap.put("PCSDPRRng6",pCSDPRRng6);}else {tableMap.put("PCSDPRRng6","NA");}
					 pCSDPRRng7= resultSet.getString("PCSDPRRng7");if(pCSDPRRng7!=null) {tableMap.put("PCSDPRRng7",pCSDPRRng7);}else {tableMap.put("PCSDPRRng7","NA");}
					 pCSDPRRng8= resultSet.getString("PCSDPRRng8");if(pCSDPRRng8!=null) {tableMap.put("PCSDPRRng8",pCSDPRRng8);}else {tableMap.put("PCSDPRRng8","NA");}
					 pCSDPRRng9= resultSet.getString("PCSDPRRng9");if(pCSDPRRng9!=null) {tableMap.put("PCSDPRRng9",pCSDPRRng9);}else {tableMap.put("PCSDPRRng9","NA");}
					 pCSDPRRng10= resultSet.getString("PCSDPRRng10");if(pCSDPRRng10!=null) {tableMap.put("PCSDPRRng10",pCSDPRRng10);}else {tableMap.put("PCSDPRRng10","NA");}
					 stationaryRestoration= resultSet.getString("StationaryRestoration");if( stationaryRestoration!=null) {tableMap.put("StationaryRestoration",stationaryRestoration);}else {tableMap.put("StationaryRestoration","NA");}
					 lastRestoration= resultSet.getString("LastRestoration");if( lastRestoration!=null) {tableMap.put("LastRestoration",lastRestoration);}else {tableMap.put("LastRestoration","NA");}
					 sootMonitor= resultSet.getString("SootMonitor");if( sootMonitor!=null) {tableMap.put("SootMonitor",sootMonitor);}else {tableMap.put("SootMonitor","NA");}
					 ashMonitor= resultSet.getString("AshMonitor");if( ashMonitor!=null) {tableMap.put("AshMonitor",ashMonitor);}else {tableMap.put("AshMonitor","NA");}
					 aftertreatmentTFU= resultSet.getString("AftertreatmentTFU");if( aftertreatmentTFU!=null) {tableMap.put("AftertreatmentTFU",aftertreatmentTFU);}else {tableMap.put("AftertreatmentTFU","NA");}
					 aftertreatmentTRT= resultSet.getString("AftertreatmentTRT");if( aftertreatmentTRT!=null) {tableMap.put("AftertreatmentTRT",aftertreatmentTRT);}else {tableMap.put("AftertreatmentTRT","NA");}
					 dPFOutlet1= resultSet.getString("DPFOutlet1");if(dPFOutlet1!=null) {tableMap.put("DPFOutlet1",dPFOutlet1);}else {tableMap.put("DPFOutlet1","NA");}
					 dPFOutlet2= resultSet.getString("DPFOutlet2");if(dPFOutlet2!=null) {tableMap.put("DPFOutlet2",dPFOutlet2);}else {tableMap.put("DPFOutlet2","NA");}
					 dPFOutlet3= resultSet.getString("DPFOutlet3");if(dPFOutlet3!=null) {tableMap.put("DPFOutlet3",dPFOutlet3);}else {tableMap.put("DPFOutlet3","NA");}
					 dPFOutlet4= resultSet.getString("DPFOutlet4");if(dPFOutlet4!=null) {tableMap.put("DPFOutlet4",dPFOutlet4);}else {tableMap.put("DPFOutlet4","NA");}
					 dPFOutlet5= resultSet.getString("DPFOutlet5");if(dPFOutlet5!=null) {tableMap.put("DPFOutlet5",dPFOutlet5);}else {tableMap.put("DPFOutlet5","NA");}
					 dPFOutlet6= resultSet.getString("DPFOutlet6");if(dPFOutlet6!=null) {tableMap.put("DPFOutlet6",dPFOutlet6);}else {tableMap.put("DPFOutlet6","NA");}
					 dPFOutlet7= resultSet.getString("DPFOutlet7");if(dPFOutlet7!=null) {tableMap.put("DPFOutlet7",dPFOutlet7);}else {tableMap.put("DPFOutlet7","NA");}
					 dPFOutlet8= resultSet.getString("DPFOutlet8");if(dPFOutlet8!=null) {tableMap.put("DPFOutlet8",dPFOutlet8);}else {tableMap.put("DPFOutlet8","NA");}
					 dPFOutlet9= resultSet.getString("DPFOutlet9");if(dPFOutlet9!=null) {tableMap.put("DPFOutlet9",dPFOutlet9);}else {tableMap.put("DPFOutlet9","NA");}
					 dPFOutlet10= resultSet.getString("DPFOutlet10");if(dPFOutlet10!=null) {tableMap.put("DPFOutlet10",dPFOutlet10);}else {tableMap.put("DPFOutlet10","NA");}
					 dPFOutlet11= resultSet.getString("DPFOutlet11");if(dPFOutlet11!=null) {tableMap.put("DPFOutlet11",dPFOutlet11);}else {tableMap.put("DPFOutlet11","NA");}
					 dPFOutlet12= resultSet.getString("DPFOutlet12");if(dPFOutlet12!=null) {tableMap.put("DPFOutlet12",dPFOutlet12);}else {tableMap.put("DPFOutlet12","NA");}
					 dPFOutlet13= resultSet.getString("DPFOutlet13");if(dPFOutlet13!=null) {tableMap.put("DPFOutlet13",dPFOutlet13);}else {tableMap.put("DPFOutlet13","NA");}
					 dPFOutlet14= resultSet.getString("DPFOutlet14");if(dPFOutlet14!=null) {tableMap.put("DPFOutlet14",dPFOutlet14);}else {tableMap.put("DPFOutlet14","NA");}
					 dieselSWITCH= resultSet.getString("DieselSWITCH");if( dieselSWITCH!=null) {tableMap.put("DieselSWITCH",dieselSWITCH);}else {tableMap.put("DieselSWITCH","NA");}
					 dieselTOOL= resultSet.getString("DieselTOOL");if( dieselTOOL!=null) {tableMap.put("DieselTOOL",dieselTOOL);}else {tableMap.put("DieselTOOL","NA");}
					 aftertreatmentStatus= resultSet.getString("AftertreatmentStatus");if( aftertreatmentStatus!=null) {tableMap.put("AftertreatmentStatus",aftertreatmentStatus);}else {tableMap.put("AftertreatmentStatus","NA");}
					 aftertreatmentIOL= resultSet.getString("AftertreatmentIOL");if( aftertreatmentIOL!=null) {tableMap.put("AftertreatmentIOL",aftertreatmentIOL);}else {tableMap.put("AftertreatmentIOL","NA");}
					 aftertreatmentISLR= resultSet.getString("AftertreatmentISLR");if( aftertreatmentISLR!=null) {tableMap.put("AftertreatmentISLR",aftertreatmentISLR);}else {tableMap.put("AftertreatmentISLR","NA");}
					 aftertreatmentTTIOL= resultSet.getString("AftertreatmentTTIOL");if( aftertreatmentTTIOL!=null) {tableMap.put("AftertreatmentTTIOL",aftertreatmentTTIOL);}else {tableMap.put("AftertreatmentTTIOL","NA");}
					 aftertreatmentLR= resultSet.getString("AftertreatmentLR");if( aftertreatmentLR!=null) {tableMap.put("AftertreatmentLR",aftertreatmentLR);}else {tableMap.put("AftertreatmentLR","NA");}
					 eSHTLC= resultSet.getString("ESHTLC");if( eSHTLC!=null) {tableMap.put("ESHTLC",eSHTLC);}else {tableMap.put("ESHTLC","NA");}
					 dPFSRNR= resultSet.getString("DPFSRNR");if( dPFSRNR!=null) {tableMap.put("DPFSRNR",dPFSRNR);}else {tableMap.put("DPFSRNR","NA");}
					 dPFSMRA= resultSet.getString("DPFSMRA");if( dPFSMRA!=null) {tableMap.put("DPFSMRA",dPFSMRA);}else {tableMap.put("DPFSMRA","NA");}
					 dPFSSRR= resultSet.getString("DPFSSRR");if( dPFSSRR!=null) {tableMap.put("DPFSSRR",dPFSSRR);}else {tableMap.put("DPFSSRR","NA");}
					 dPFSRS= resultSet.getString("DPFSRS");if( dPFSRS!=null) {tableMap.put("DPFSRS",dPFSRS);}else {tableMap.put("DPFSRS","NA");}
					 dPFSRPL2= resultSet.getString("DPFSRPL2");if( dPFSRPL2!=null) {tableMap.put("DPFSRPL2",dPFSRPL2);}else {tableMap.put("DPFSRPL2","NA");}
					 dPFSMRRL3= resultSet.getString("DPFSMRRL3");if( dPFSMRRL3!=null) {tableMap.put("DPFSMRRL3",dPFSMRRL3);}else {tableMap.put("DPFSMRRL3","NA");}
					 dPFSRA= resultSet.getString("DPFSRA");if( dPFSRA!=null) {tableMap.put("DPFSRA",dPFSRA);}else {tableMap.put("DPFSRA","NA");}
					 dPFSRC= resultSet.getString("DPFSRC");if( dPFSRC!=null) {tableMap.put("DPFSRC",dPFSRC);}else {tableMap.put("DPFSRC","NA");}
					 aARIDTISRABIBO = resultSet.getString("AARIDTISRABIBO");if( aARIDTISRABIBO!=null) {tableMap.put("AARIDTISRABIBO",aARIDTISRABIBO);}else {tableMap.put("AARIDTISRABIBO","NA");}
					
				 mapList.add(tableMap);
			}
			}else {
				resultSet = statement.executeQuery(startTAssetMonQuery);
				while(resultSet.next())
				{
					tableMap = new HashMap<String,String>();
					tableMap.put("Vin",resultSet.getString("Vin")); 					
					 hours = resultSet.getString("Hours");if(hours !=null) {tableMap.put("Hours",hours);}else {tableMap.put("Hours","NA");}
					 tDate=resultSet.getString("Transactiontimestamp").substring(0,19); 
						DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
						DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
						transactiontimestamp = LocalDateTime.parse(tDate, dtf).format(dtf2);if(tDate !=null) {tableMap.put("Transactiontimestamp",transactiontimestamp);}else {tableMap.put("Transactiontimestamp","NA");}
					 adBlueConsumption= resultSet.getString("AdBlueConsumption");if(adBlueConsumption !=null) {tableMap.put("AdBlueConsumption",adBlueConsumption);}else {tableMap.put("AdBlueConsumption","NA");}
					 dEF_Concentration= resultSet.getString("DEF_Concentration");if(dEF_Concentration !=null) {tableMap.put("DEF_Concentration",dEF_Concentration);}else {tableMap.put("DEF_Concentration","NA");}
					 dEF_Temperature= resultSet.getString("DEF_Temperature");if(dEF_Temperature !=null) {tableMap.put("DEF_Temperature",dEF_Temperature);}else {tableMap.put("DEF_Temperature","NA");}
					// engineOnCount= resultSet.getString("EngineOnCount");if(engineOnCount !=null) {tableMap.put("EngineOnCount",engineOnCount);}else {tableMap.put("EngineOnCount","NA");}
					 powerRatingRequest= resultSet.getString("PowerRatingRequest");if(powerRatingRequest !=null) {tableMap.put("PowerRatingRequest",powerRatingRequest);}else {tableMap.put("PowerRatingRequest","NA");}
					 speedRange1FuelRange1 = resultSet.getString("SpeedRange1FuelRange1");if(speedRange1FuelRange1 !=null) {tableMap.put("SpeedRange1FuelRange1",speedRange1FuelRange1);}else {tableMap.put("SpeedRange1FuelRange1","NA");}
					 speedRange1FuelRange2 = resultSet.getString("SpeedRange1FuelRange2");if( speedRange1FuelRange2!=null) {tableMap.put("SpeedRange1FuelRange2",speedRange1FuelRange2);}else {tableMap.put("SpeedRange1FuelRange2","NA");}
					 speedRange1FuelRange3 = resultSet.getString("SpeedRange1FuelRange3");if( speedRange1FuelRange3!=null) {tableMap.put("SpeedRange1FuelRange3",speedRange1FuelRange3);}else {tableMap.put("SpeedRange1FuelRange3","NA");}
					 speedRange1FuelRange4 = resultSet.getString("SpeedRange1FuelRange4");if( speedRange1FuelRange4!=null) {tableMap.put("SpeedRange1FuelRange4",speedRange1FuelRange4);}else {tableMap.put("SpeedRange1FuelRange4","NA");}
					 speedRange1FuelRange5 = resultSet.getString("SpeedRange1FuelRange5");if( speedRange1FuelRange5!=null) {tableMap.put("SpeedRange1FuelRange5",speedRange1FuelRange5);}else {tableMap.put("SpeedRange1FuelRange5","NA");}
					 speedRange1FuelRange6 = resultSet.getString("SpeedRange1FuelRange6");if( speedRange1FuelRange6!=null) {tableMap.put("SpeedRange1FuelRange6",speedRange1FuelRange6);}else {tableMap.put("SpeedRange1FuelRange6","NA");}
					 speedRange1FuelRange7 = resultSet.getString("SpeedRange1FuelRange7");if( speedRange1FuelRange7!=null) {tableMap.put("SpeedRange1FuelRange7",speedRange1FuelRange7);}else {tableMap.put("SpeedRange1FuelRange7","NA");}
					 speedRange1FuelRange8 = resultSet.getString("SpeedRange1FuelRange8");if( speedRange1FuelRange8!=null) {tableMap.put("SpeedRange1FuelRange8",speedRange1FuelRange8);}else {tableMap.put("SpeedRange1FuelRange8","NA");}
					 speedRange1FuelRange9 = resultSet.getString("SpeedRange1FuelRange9");if( speedRange1FuelRange9!=null) {tableMap.put("SpeedRange1FuelRange9",speedRange1FuelRange9);}else {tableMap.put("SpeedRange1FuelRange9","NA");}
					 speedRange1FuelRange10 = resultSet.getString("SpeedRange1FuelRange10");if( speedRange1FuelRange10!=null) {tableMap.put("SpeedRange1FuelRange10",speedRange1FuelRange10);}else {tableMap.put("SpeedRange1FuelRange10","NA");}
					 speedRange2FuelRange1 = resultSet.getString("SpeedRange2FuelRange1");if( speedRange2FuelRange1!=null) {tableMap.put("SpeedRange2FuelRange1",speedRange2FuelRange1);}else {tableMap.put("SpeedRange2FuelRange1","NA");}
					 speedRange2FuelRange2 = resultSet.getString("SpeedRange2FuelRange2");if( speedRange2FuelRange2!=null) {tableMap.put("SpeedRange2FuelRange2",speedRange2FuelRange2);}else {tableMap.put("SpeedRange2FuelRange2","NA");}
					 speedRange2FuelRange3 = resultSet.getString("SpeedRange2FuelRange3");if( speedRange2FuelRange3!=null) {tableMap.put("SpeedRange2FuelRange3",speedRange2FuelRange3);}else {tableMap.put("SpeedRange2FuelRange3","NA");}
					 speedRange2FuelRange4 = resultSet.getString("SpeedRange2FuelRange4");if( speedRange2FuelRange4!=null) {tableMap.put("SpeedRange2FuelRange4",speedRange2FuelRange4);}else {tableMap.put("SpeedRange2FuelRange4","NA");}
					 speedRange2FuelRange5 = resultSet.getString("SpeedRange2FuelRange5");if( speedRange2FuelRange5!=null) {tableMap.put("SpeedRange2FuelRange5",speedRange2FuelRange5);}else {tableMap.put("SpeedRange2FuelRange5","NA");}
					 speedRange2FuelRange6 = resultSet.getString("SpeedRange2FuelRange6");if( speedRange2FuelRange6!=null) {tableMap.put("SpeedRange2FuelRange6",speedRange2FuelRange6);}else {tableMap.put("SpeedRange2FuelRange6","NA");}
					 speedRange2FuelRange7 = resultSet.getString("SpeedRange2FuelRange7");if( speedRange2FuelRange7!=null) {tableMap.put("SpeedRange2FuelRange7",speedRange2FuelRange7);}else {tableMap.put("SpeedRange2FuelRange7","NA");}
					 speedRange2FuelRange8 = resultSet.getString("SpeedRange2FuelRange8");if( speedRange2FuelRange8!=null) {tableMap.put("SpeedRange2FuelRange8",speedRange2FuelRange8);}else {tableMap.put("SpeedRange2FuelRange8","NA");}
					 speedRange2FuelRange9 = resultSet.getString("SpeedRange2FuelRange9");if( speedRange2FuelRange9!=null) {tableMap.put("SpeedRange2FuelRange9",speedRange2FuelRange9);}else {tableMap.put("SpeedRange2FuelRange9","NA");}
					 speedRange2FuelRange10 = resultSet.getString("SpeedRange2FuelRange10");if( speedRange2FuelRange10!=null) {tableMap.put("SpeedRange2FuelRange10",speedRange2FuelRange10);}else {tableMap.put("SpeedRange2FuelRange10","NA");}
					 speedRange3FuelRange1 = resultSet.getString("SpeedRange3FuelRange1");if( speedRange3FuelRange1!=null) {tableMap.put("SpeedRange3FuelRange1",speedRange3FuelRange1);}else {tableMap.put("SpeedRange3FuelRange1","NA");}
					 speedRange3FuelRange2 = resultSet.getString("SpeedRange3FuelRange2");if( speedRange3FuelRange2!=null) {tableMap.put("SpeedRange3FuelRange2",speedRange3FuelRange2);}else {tableMap.put("SpeedRange3FuelRange2","NA");}
					 speedRange3FuelRange3 = resultSet.getString("SpeedRange3FuelRange3");if( speedRange3FuelRange3!=null) {tableMap.put("SpeedRange3FuelRange3",speedRange3FuelRange3);}else {tableMap.put("SpeedRange3FuelRange3","NA");}
					 speedRange3FuelRange4 = resultSet.getString("SpeedRange3FuelRange4");if( speedRange3FuelRange4!=null) {tableMap.put("SpeedRange3FuelRange4",speedRange3FuelRange4);}else {tableMap.put("SpeedRange3FuelRange4","NA");}
					 speedRange3FuelRange5 = resultSet.getString("SpeedRange3FuelRange5");if( speedRange3FuelRange5!=null) {tableMap.put("SpeedRange3FuelRange5",speedRange3FuelRange5);}else {tableMap.put("SpeedRange3FuelRange5","NA");}
					 speedRange3FuelRange6 = resultSet.getString("SpeedRange3FuelRange6");if( speedRange3FuelRange6!=null) {tableMap.put("SpeedRange3FuelRange6",speedRange3FuelRange6);}else {tableMap.put("SpeedRange3FuelRange6","NA");}
					 speedRange3FuelRange7 = resultSet.getString("SpeedRange3FuelRange7");if( speedRange3FuelRange7!=null) {tableMap.put("SpeedRange3FuelRange7",speedRange3FuelRange7);}else {tableMap.put("SpeedRange3FuelRange7","NA");}
					 speedRange3FuelRange8 = resultSet.getString("SpeedRange3FuelRange8");if( speedRange3FuelRange8!=null) {tableMap.put("SpeedRange3FuelRange8",speedRange3FuelRange8);}else {tableMap.put("SpeedRange3FuelRange8","NA");}
					 speedRange3FuelRange9 = resultSet.getString("SpeedRange3FuelRange9");if( speedRange3FuelRange9!=null) {tableMap.put("SpeedRange3FuelRange9",speedRange3FuelRange9);}else {tableMap.put("SpeedRange3FuelRange9","NA");}
					 speedRange3FuelRange10 = resultSet.getString("SpeedRange3FuelRange10");if( speedRange3FuelRange10!=null) {tableMap.put("SpeedRange3FuelRange10",speedRange3FuelRange10);}else {tableMap.put("SpeedRange3FuelRange10","NA");}
					 speedRange4FuelRange1= resultSet.getString("SpeedRange4FuelRange1");if( speedRange4FuelRange1!=null) {tableMap.put("SpeedRange4FuelRange1",speedRange4FuelRange1);}else {tableMap.put("SpeedRange4FuelRange1","NA");}
					 speedRange4FuelRange2= resultSet.getString("SpeedRange4FuelRange2");if( speedRange4FuelRange2!=null) {tableMap.put("SpeedRange4FuelRange2",speedRange4FuelRange2);}else {tableMap.put("SpeedRange4FuelRange2","NA");}
					 speedRange4FuelRange3= resultSet.getString("SpeedRange4FuelRange3");if( speedRange4FuelRange3!=null) {tableMap.put("SpeedRange4FuelRange3",speedRange4FuelRange3);}else {tableMap.put("SpeedRange4FuelRange3","NA");}
					 speedRange4FuelRange4= resultSet.getString("SpeedRange4FuelRange4");if( speedRange4FuelRange4!=null) {tableMap.put("SpeedRange4FuelRange4",speedRange4FuelRange4);}else {tableMap.put("SpeedRange4FuelRange4","NA");}
					 speedRange4FuelRange5= resultSet.getString("SpeedRange4FuelRange5");if( speedRange4FuelRange5!=null) {tableMap.put("SpeedRange4FuelRange5",speedRange4FuelRange5);}else {tableMap.put("SpeedRange4FuelRange5","NA");}
					 speedRange4FuelRange6= resultSet.getString("SpeedRange4FuelRange6");if( speedRange4FuelRange6!=null) {tableMap.put("SpeedRange4FuelRange6",speedRange4FuelRange6);}else {tableMap.put("SpeedRange4FuelRange6","NA");}
					 speedRange4FuelRange7= resultSet.getString("SpeedRange4FuelRange7");if( speedRange4FuelRange7!=null) {tableMap.put("SpeedRange4FuelRange7",speedRange4FuelRange7);}else {tableMap.put("SpeedRange4FuelRange7","NA");}
					 speedRange4FuelRange8= resultSet.getString("SpeedRange4FuelRange8");if( speedRange4FuelRange8!=null) {tableMap.put("SpeedRange4FuelRange8",speedRange4FuelRange8);}else {tableMap.put("SpeedRange4FuelRange8","NA");}
					 speedRange4FuelRange9= resultSet.getString("SpeedRange4FuelRange9");if( speedRange4FuelRange9!=null) {tableMap.put("SpeedRange4FuelRange9",speedRange4FuelRange9);}else {tableMap.put("SpeedRange4FuelRange9","NA");}
					 speedRange4FuelRange10= resultSet.getString("SpeedRange4FuelRange10");if( speedRange4FuelRange10!=null) {tableMap.put("SpeedRange4FuelRange10",speedRange4FuelRange10);}else {tableMap.put("SpeedRange4FuelRange10","NA");}
					 speedRange5FuelRange1= resultSet.getString("SpeedRange5FuelRange1");if( speedRange5FuelRange1!=null) {tableMap.put("SpeedRange5FuelRange1",speedRange5FuelRange1);}else {tableMap.put("SpeedRange5FuelRange1","NA");}
					 speedRange5FuelRange2= resultSet.getString("SpeedRange5FuelRange2");if( speedRange5FuelRange2!=null) {tableMap.put("SpeedRange5FuelRange2",speedRange5FuelRange2);}else {tableMap.put("SpeedRange5FuelRange2","NA");}
					 speedRange5FuelRange3= resultSet.getString("SpeedRange5FuelRange3");if( speedRange5FuelRange3!=null) {tableMap.put("SpeedRange5FuelRange3",speedRange5FuelRange3);}else {tableMap.put("SpeedRange5FuelRange3","NA");}
					 speedRange5FuelRange4= resultSet.getString("SpeedRange5FuelRange4");if( speedRange5FuelRange4!=null) {tableMap.put("SpeedRange5FuelRange4",speedRange5FuelRange4);}else {tableMap.put("SpeedRange5FuelRange4","NA");}
					 speedRange5FuelRange5= resultSet.getString("SpeedRange5FuelRange5");if( speedRange5FuelRange5!=null) {tableMap.put("SpeedRange5FuelRange5",speedRange5FuelRange5);}else {tableMap.put("SpeedRange5FuelRange5","NA");}
					 speedRange5FuelRange6= resultSet.getString("SpeedRange5FuelRange6");if( speedRange5FuelRange6!=null) {tableMap.put("SpeedRange5FuelRange6",speedRange5FuelRange6);}else {tableMap.put("SpeedRange5FuelRange6","NA");}
					 speedRange5FuelRange7= resultSet.getString("SpeedRange5FuelRange7");if( speedRange5FuelRange7!=null) {tableMap.put("SpeedRange5FuelRange7",speedRange5FuelRange7);}else {tableMap.put("SpeedRange5FuelRange7","NA");}
					 speedRange5FuelRange8= resultSet.getString("SpeedRange5FuelRange8");if( speedRange5FuelRange8!=null) {tableMap.put("SpeedRange5FuelRange8",speedRange5FuelRange8);}else {tableMap.put("SpeedRange5FuelRange8","NA");}
					 speedRange5FuelRange9= resultSet.getString("SpeedRange5FuelRange9");if( speedRange5FuelRange9!=null) {tableMap.put("SpeedRange5FuelRange9",speedRange5FuelRange9);}else {tableMap.put("SpeedRange5FuelRange9","NA");}
					 speedRange5FuelRange10= resultSet.getString("SpeedRange5FuelRange10");if( speedRange5FuelRange10!=null) {tableMap.put("SpeedRange5FuelRange10",speedRange5FuelRange10);}else {tableMap.put("SpeedRange5FuelRange10","NA");}
					 speedRange6FuelRange1= resultSet.getString("SpeedRange6FuelRange1");if( speedRange6FuelRange1!=null) {tableMap.put("SpeedRange6FuelRange1",speedRange6FuelRange1);}else {tableMap.put("SpeedRange6FuelRange1","NA");}
					 speedRange6FuelRange2= resultSet.getString("SpeedRange6FuelRange2");if( speedRange6FuelRange2!=null) {tableMap.put("SpeedRange6FuelRange2",speedRange6FuelRange2);}else {tableMap.put("SpeedRange6FuelRange2","NA");}
					 speedRange6FuelRange3= resultSet.getString("SpeedRange6FuelRange3");if( speedRange6FuelRange3!=null) {tableMap.put("SpeedRange6FuelRange3",speedRange6FuelRange3);}else {tableMap.put("SpeedRange6FuelRange3","NA");}
					 speedRange6FuelRange4= resultSet.getString("SpeedRange6FuelRange4");if( speedRange6FuelRange4!=null) {tableMap.put("SpeedRange6FuelRange4",speedRange6FuelRange4);}else {tableMap.put("SpeedRange6FuelRange4","NA");}
					 speedRange6FuelRange5= resultSet.getString("SpeedRange6FuelRange5");if( speedRange6FuelRange5!=null) {tableMap.put("SpeedRange6FuelRange5",speedRange6FuelRange5);}else {tableMap.put("SpeedRange6FuelRange5","NA");}
					 speedRange6FuelRange6= resultSet.getString("SpeedRange6FuelRange6");if( speedRange6FuelRange6!=null) {tableMap.put("SpeedRange6FuelRange6",speedRange6FuelRange6);}else {tableMap.put("SpeedRange6FuelRange6","NA");}
					 speedRange6FuelRange7= resultSet.getString("SpeedRange6FuelRange7");if( speedRange6FuelRange7!=null) {tableMap.put("SpeedRange6FuelRange7",speedRange6FuelRange7);}else {tableMap.put("SpeedRange6FuelRange7","NA");}
					 speedRange6FuelRange8= resultSet.getString("SpeedRange6FuelRange8");if( speedRange6FuelRange8!=null) {tableMap.put("SpeedRange6FuelRange8",speedRange6FuelRange8);}else {tableMap.put("SpeedRange6FuelRange8","NA");}
					 speedRange6FuelRange9= resultSet.getString("SpeedRange6FuelRange9");if( speedRange6FuelRange9!=null) {tableMap.put("SpeedRange6FuelRange9",speedRange6FuelRange9);}else {tableMap.put("SpeedRange6FuelRange9","NA");}
					 speedRange6FuelRange10= resultSet.getString("SpeedRange6FuelRange10");if( speedRange6FuelRange10!=null) {tableMap.put("SpeedRange6FuelRange10",speedRange6FuelRange10);}else {tableMap.put("SpeedRange6FuelRange10","NA");}
					 speedRange7FuelRange1= resultSet.getString("SpeedRange7FuelRange1");if( speedRange7FuelRange1!=null) {tableMap.put("SpeedRange7FuelRange1",speedRange7FuelRange1);}else {tableMap.put("SpeedRange7FuelRange1","NA");}
					 speedRange7FuelRange2= resultSet.getString("SpeedRange7FuelRange2");if( speedRange7FuelRange2!=null) {tableMap.put("SpeedRange7FuelRange2",speedRange7FuelRange2);}else {tableMap.put("SpeedRange7FuelRange2","NA");}
					 speedRange7FuelRange3= resultSet.getString("SpeedRange7FuelRange3");if( speedRange7FuelRange3!=null) {tableMap.put("SpeedRange7FuelRange3",speedRange7FuelRange3);}else {tableMap.put("SpeedRange7FuelRange3","NA");}
					 speedRange7FuelRange4= resultSet.getString("SpeedRange7FuelRange4");if( speedRange7FuelRange4!=null) {tableMap.put("SpeedRange7FuelRange4",speedRange7FuelRange4);}else {tableMap.put("SpeedRange7FuelRange4","NA");}
					 speedRange7FuelRange5= resultSet.getString("SpeedRange7FuelRange5");if( speedRange7FuelRange5!=null) {tableMap.put("SpeedRange7FuelRange5",speedRange7FuelRange5);}else {tableMap.put("SpeedRange7FuelRange5","NA");}
					 speedRange7FuelRange6= resultSet.getString("SpeedRange7FuelRange6");if( speedRange7FuelRange6!=null) {tableMap.put("SpeedRange7FuelRange6",speedRange7FuelRange6);}else {tableMap.put("SpeedRange7FuelRange6","NA");}
					 speedRange7FuelRange7= resultSet.getString("SpeedRange7FuelRange7");if( speedRange7FuelRange7!=null) {tableMap.put("SpeedRange7FuelRange7",speedRange7FuelRange7);}else {tableMap.put("SpeedRange7FuelRange7","NA");}
					 speedRange7FuelRange8= resultSet.getString("SpeedRange7FuelRange8");if( speedRange7FuelRange8!=null) {tableMap.put("SpeedRange7FuelRange8",speedRange7FuelRange8);}else {tableMap.put("SpeedRange7FuelRange8","NA");}
					 speedRange7FuelRange9= resultSet.getString("SpeedRange7FuelRange9");if( speedRange7FuelRange9!=null) {tableMap.put("SpeedRange7FuelRange9",speedRange7FuelRange9);}else {tableMap.put("SpeedRange7FuelRange9","NA");}
					 speedRange7FuelRange10= resultSet.getString("SpeedRange7FuelRange10");if( speedRange7FuelRange10!=null) {tableMap.put("SpeedRange7FuelRange10",speedRange7FuelRange10);}else {tableMap.put("SpeedRange7FuelRange10","NA");}
					 coolantTempratureResidenceRange1= resultSet.getString("CoolantTempratureResidenceRange1");if( coolantTempratureResidenceRange1!=null) {tableMap.put("CoolantTempratureResidenceRange1",coolantTempratureResidenceRange1);}else {tableMap.put("CoolantTempratureResidenceRange1","NA");}
					 coolantTempratureResidenceRange2= resultSet.getString("CoolantTempratureResidenceRange2");if( coolantTempratureResidenceRange2!=null) {tableMap.put("CoolantTempratureResidenceRange2",coolantTempratureResidenceRange2);}else {tableMap.put("CoolantTempratureResidenceRange2","NA");}
					 coolantTempratureResidenceRange3= resultSet.getString("CoolantTempratureResidenceRange3");if( coolantTempratureResidenceRange3!=null) {tableMap.put("CoolantTempratureResidenceRange3",coolantTempratureResidenceRange3);}else {tableMap.put("CoolantTempratureResidenceRange3","NA");}
					 coolantTempratureResidenceRange4= resultSet.getString("CoolantTempratureResidenceRange4");if( coolantTempratureResidenceRange4!=null) {tableMap.put("CoolantTempratureResidenceRange4",coolantTempratureResidenceRange4);}else {tableMap.put("CoolantTempratureResidenceRange4","NA");}
					 coolantTempratureResidenceRange5= resultSet.getString("CoolantTempratureResidenceRange5");if( coolantTempratureResidenceRange5!=null) {tableMap.put("CoolantTempratureResidenceRange5",coolantTempratureResidenceRange5);}else {tableMap.put("CoolantTempratureResidenceRange5","NA");}
					 coolantTempratureResidenceRange6= resultSet.getString("CoolantTempratureResidenceRange6");if( coolantTempratureResidenceRange6!=null) {tableMap.put("CoolantTempratureResidenceRange6",coolantTempratureResidenceRange6);}else {tableMap.put("CoolantTempratureResidenceRange6","NA");}
					 coolantTempratureResidenceRange7= resultSet.getString("CoolantTempratureResidenceRange7");if( coolantTempratureResidenceRange7!=null) {tableMap.put("CoolantTempratureResidenceRange7",coolantTempratureResidenceRange7);}else {tableMap.put("CoolantTempratureResidenceRange7","NA");}
					 coolantTempratureResidenceRange8= resultSet.getString("CoolantTempratureResidenceRange8");if( coolantTempratureResidenceRange8!=null) {tableMap.put("CoolantTempratureResidenceRange8",coolantTempratureResidenceRange8);}else {tableMap.put("CoolantTempratureResidenceRange8","NA");}
					 coolantTempratureResidenceRange9= resultSet.getString("CoolantTempratureResidenceRange9");if( coolantTempratureResidenceRange9!=null) {tableMap.put("CoolantTempratureResidenceRange9",coolantTempratureResidenceRange9);}else {tableMap.put("CoolantTempratureResidenceRange19","NA");}
					 coolantTempratureResidenceRange10= resultSet.getString("CoolantTempratureResidenceRange10");if( coolantTempratureResidenceRange10!=null) {tableMap.put("CoolantTempratureResidenceRange10",coolantTempratureResidenceRange10);}else {tableMap.put("CoolantTempratureResidenceRange10","NA");}
					 coolantTempratureResidenceRange11= resultSet.getString("CoolantTempratureResidenceRange11");if( coolantTempratureResidenceRange11!=null) {tableMap.put("CoolantTempratureResidenceRange11",coolantTempratureResidenceRange11);}else {tableMap.put("CoolantTempratureResidenceRange11","NA");}
					 coolantTempratureResidenceRange12= resultSet.getString("CoolantTempratureResidenceRange12");if( coolantTempratureResidenceRange12!=null) {tableMap.put("CoolantTempratureResidenceRange12",coolantTempratureResidenceRange12);}else {tableMap.put("CoolantTempratureResidenceRange12","NA");}
					 eEGTRR1= resultSet.getString("EEGTRR1");if( eEGTRR1!=null) {tableMap.put("EEGTRR1",eEGTRR1);}else {tableMap.put("EEGTRR1","NA");}
					 eEGTRR2= resultSet.getString("EEGTRR2");if( eEGTRR2!=null) {tableMap.put("EEGTRR2",eEGTRR2);}else {tableMap.put("EEGTRR2","NA");}
					 eEGTRR3= resultSet.getString("EEGTRR3");if( eEGTRR3!=null) {tableMap.put("EEGTRR3",eEGTRR3);}else {tableMap.put("EEGTRR3","NA");}
					 eEGTRR4= resultSet.getString("EEGTRR4");if( eEGTRR4!=null) {tableMap.put("EEGTRR4",eEGTRR4);}else {tableMap.put("EEGTRR4","NA");}
					 eEGTRR5= resultSet.getString("EEGTRR5");if( eEGTRR5!=null) {tableMap.put("EEGTRR5",eEGTRR5);}else {tableMap.put("EEGTRR5","NA");}
					 eEGTRR6= resultSet.getString("EEGTRR6");if( eEGTRR6!=null) {tableMap.put("EEGTRR6",eEGTRR6);}else {tableMap.put("EEGTRR6","NA");}
					 eEGTRR7= resultSet.getString("EEGTRR7");if( eEGTRR7!=null) {tableMap.put("EEGTRR7",eEGTRR7);}else {tableMap.put("EEGTRR7","NA");}
					 eEGTRR8= resultSet.getString("EEGTRR8");if( eEGTRR8!=null) {tableMap.put("EEGTRR8",eEGTRR8);}else {tableMap.put("EEGTRR8","NA");}
					 eEGTRR9= resultSet.getString("EEGTRR9");if( eEGTRR9!=null) {tableMap.put("EEGTRR9",eEGTRR9);}else {tableMap.put("EEGTRR9","NA");}
					 eEGTRR10= resultSet.getString("EEGTRR10");if( eEGTRR10!=null) {tableMap.put("EEGTRR10",eEGTRR10);}else {tableMap.put("EEGTRR10","NA");}
					 eEGTRR11= resultSet.getString("EEGTRR11");if( eEGTRR11!=null) {tableMap.put("EEGTRR11",eEGTRR11);}else {tableMap.put("EEGTRR11","NA");}
					 eEGTRR12= resultSet.getString("EEGTRR12");if( eEGTRR12!=null) {tableMap.put("EEGTRR12",eEGTRR12);}else {tableMap.put("EEGTRR12","NA");}
					 fuelTempResidenceRange1= resultSet.getString("FuelTempResidenceRange1");if( fuelTempResidenceRange1!=null) {tableMap.put("FuelTempResidenceRange1", fuelTempResidenceRange1);}else {tableMap.put("FuelTempResidenceRange1","NA");}
					 fuelTempResidenceRange2= resultSet.getString("FuelTempResidenceRange2");if( fuelTempResidenceRange2!=null) {tableMap.put("FuelTempResidenceRange2", fuelTempResidenceRange2);}else {tableMap.put("FuelTempResidenceRange2","NA");}
					 fuelTempResidenceRange3= resultSet.getString("FuelTempResidenceRange3");if( fuelTempResidenceRange3!=null) {tableMap.put("FuelTempResidenceRange3", fuelTempResidenceRange3);}else {tableMap.put("FuelTempResidenceRange3","NA");}
					 fuelTempResidenceRange4= resultSet.getString("FuelTempResidenceRange4");if( fuelTempResidenceRange4!=null) {tableMap.put("FuelTempResidenceRange4", fuelTempResidenceRange4);}else {tableMap.put("FuelTempResidenceRange4","NA");}
					 fuelTempResidenceRange5= resultSet.getString("FuelTempResidenceRange5");if( fuelTempResidenceRange5!=null) {tableMap.put("FuelTempResidenceRange5", fuelTempResidenceRange5);}else {tableMap.put("FuelTempResidenceRange5","NA");}
					 fuelTempResidenceRange6= resultSet.getString("FuelTempResidenceRange6");if( fuelTempResidenceRange6!=null) {tableMap.put("FuelTempResidenceRange6", fuelTempResidenceRange6);}else {tableMap.put("FuelTempResidenceRange6","NA");}
					 fuelTempResidenceRange7= resultSet.getString("FuelTempResidenceRange7");if( fuelTempResidenceRange7!=null) {tableMap.put("FuelTempResidenceRange7", fuelTempResidenceRange7);}else {tableMap.put("FuelTempResidenceRange7","NA");}
					 fuelTempResidenceRange8= resultSet.getString("FuelTempResidenceRange8");if( fuelTempResidenceRange8!=null) {tableMap.put("FuelTempResidenceRange8", fuelTempResidenceRange8);}else {tableMap.put("FuelTempResidenceRange8","NA");}
					 inletAir1= resultSet.getString("InletAir1");if(inletAir1!=null) {tableMap.put("InletAir1",inletAir1);}else {tableMap.put("InletAir1","NA");}
					 inletAir2= resultSet.getString("InletAir2");if(inletAir2!=null) {tableMap.put("InletAir2",inletAir2);}else {tableMap.put("InletAir2","NA");}
					 inletAir3= resultSet.getString("InletAir3");if(inletAir3!=null) {tableMap.put("InletAir3",inletAir3);}else {tableMap.put("InletAir3","NA");}
					 inletAir4= resultSet.getString("InletAir4");if(inletAir4!=null) {tableMap.put("InletAir4",inletAir4);}else {tableMap.put("InletAir4","NA");}
					 inletAir5= resultSet.getString("InletAir5");if(inletAir5!=null) {tableMap.put("InletAir5",inletAir5);}else {tableMap.put("InletAir5","NA");}
					 inletAir6= resultSet.getString("InletAir6");if(inletAir6!=null) {tableMap.put("InletAir6",inletAir6);}else {tableMap.put("InletAir6","NA");}
					 inletAir7= resultSet.getString("InletAir7");if(inletAir7!=null) {tableMap.put("InletAir7",inletAir7);}else {tableMap.put("InletAir7","NA");}
					 inletAir8= resultSet.getString("InletAir8");if(inletAir8!=null) {tableMap.put("InletAir8",inletAir8);}else {tableMap.put("InletAir8","NA");}
					 inletAir9= resultSet.getString("InletAir9");if(inletAir9!=null) {tableMap.put("InletAir9",inletAir9);}else {tableMap.put("InletAir9","NA");}
					 inletAir10= resultSet.getString("InletAir10");if(inletAir10!=null) {tableMap.put("InletAir10",inletAir10);}else {tableMap.put("InletAir10","NA");}
					 barometric1= resultSet.getString("Barometric1");if(barometric1!=null) {tableMap.put("Barometric1",barometric1);}else {tableMap.put("Barometric1","NA");}
					 barometric2= resultSet.getString("Barometric2");if(barometric2!=null) {tableMap.put("Barometric2",barometric2);}else {tableMap.put("Barometric2","NA");}
					 barometric3= resultSet.getString("Barometric3");if(barometric3!=null) {tableMap.put("Barometric3",barometric3);}else {tableMap.put("Barometric3","NA");}
					 barometric4= resultSet.getString("Barometric4");if(barometric4!=null) {tableMap.put("Barometric4",barometric4);}else {tableMap.put("Barometric4","NA");}
					 barometric5= resultSet.getString("Barometric5");if(barometric5!=null) {tableMap.put("Barometric5",barometric5);}else {tableMap.put("Barometric5","NA");}
					 barometric6= resultSet.getString("Barometric6");if(barometric6!=null) {tableMap.put("Barometric6",barometric6);}else {tableMap.put("Barometric6","NA");}
					 barometric7= resultSet.getString("Barometric7");if(barometric7!=null) {tableMap.put("Barometric7",barometric7);}else {tableMap.put("Barometric7","NA");}
					 barometric8= resultSet.getString("Barometric8");if(barometric8!=null) {tableMap.put("Barometric8",barometric8);}else {tableMap.put("Barometric8","NA");}
					 barometric9= resultSet.getString("Barometric9");if(barometric9!=null) {tableMap.put("Barometric9",barometric9);}else {tableMap.put("Barometric9","NA");}
					 barometric10= resultSet.getString("Barometric10");if(barometric10!=null) {tableMap.put("Barometric10",barometric10);}else {tableMap.put("Barometric10","NA");}
					 maxEngineSpeed = resultSet.getString("MaxEngineSpeed");if( maxEngineSpeed!=null) {tableMap.put("MaxEngineSpeed",maxEngineSpeed);}else {tableMap.put("MaxEngineSpeed","NA");}
					 eOERC1= resultSet.getString("EOERC1");if(eOERC1!=null) {tableMap.put("EOERC1",eOERC1);}else {tableMap.put("EOERC1","NA");}
					 eOERC2= resultSet.getString("EOERC2");if(eOERC2!=null) {tableMap.put("EOERC2",eOERC2);}else {tableMap.put("EOERC2","NA");}
					 eOERC3= resultSet.getString("EOERC3");if(eOERC3!=null) {tableMap.put("EOERC3",eOERC3);}else {tableMap.put("EOERC3","NA");}
					 eOERC4= resultSet.getString("EOERC4");if(eOERC4!=null) {tableMap.put("EOERC4",eOERC4);}else {tableMap.put("EOERC4","NA");}
					 eOERC5= resultSet.getString("EOERC5");if(eOERC5!=null) {tableMap.put("EOERC5",eOERC5);}else {tableMap.put("EOERC5","NA");}
					 eOERC6= resultSet.getString("EOERC6");if(eOERC6!=null) {tableMap.put("EOERC6",eOERC6);}else {tableMap.put("EOERC6","NA");}
					 eOERC7= resultSet.getString("EOERC7");if(eOERC7!=null) {tableMap.put("EOERC7",eOERC7);}else {tableMap.put("EOERC7","NA");}
					 eOERC8= resultSet.getString("EOERC8");if(eOERC8!=null) {tableMap.put("EOERC8",eOERC8);}else {tableMap.put("EOERC8","NA");}
					 eOERC9= resultSet.getString("EOERC9");if(eOERC9!=null) {tableMap.put("EOERC9",eOERC9);}else {tableMap.put("EOERC9","NA");}
					 eOERC10= resultSet.getString("EOERC10");if(eOERC10!=null) {tableMap.put("EOERC10",eOERC10);}else {tableMap.put("EOERC10","NA");}
					 eOERD1= resultSet.getString("EOERD1");if(eOERD1!=null) {tableMap.put("EOERD1",eOERD1);}else {tableMap.put("EOERD1","NA");}
					 eOERD2= resultSet.getString("EOERD2");if(eOERD2!=null) {tableMap.put("EOERD2",eOERD2);}else {tableMap.put("EOERD2","NA");}
					 eOERD3= resultSet.getString("EOERD3");if(eOERD3!=null) {tableMap.put("EOERD3",eOERD3);}else {tableMap.put("EOERD3","NA");}
					 eOERD4= resultSet.getString("EOERD4");if(eOERD4!=null) {tableMap.put("EOERD4",eOERD4);}else {tableMap.put("EOERD4","NA");}
					 eOERD5= resultSet.getString("EOERD5");if(eOERD5!=null) {tableMap.put("EOERD5",eOERD5);}else {tableMap.put("EOERD5","NA");}
					 eOERD6= resultSet.getString("EOERD6");if(eOERD6!=null) {tableMap.put("EOERD6",eOERD6);}else {tableMap.put("EOERD6","NA");}
					 eOERD7= resultSet.getString("EOERD7");if(eOERD7!=null) {tableMap.put("EOERD7",eOERD7);}else {tableMap.put("EOERD7","NA");}
					 eOERD8= resultSet.getString("EOERD8");if(eOERD8!=null) {tableMap.put("EOERD8",eOERD8);}else {tableMap.put("EOERD8","NA");}
					 eOERD9= resultSet.getString("EOERD9");if(eOERD9!=null) {tableMap.put("EOERD9",eOERD9);}else {tableMap.put("EOERD9","NA");}
					 eOERD10= resultSet.getString("EOERD10");if(eOERD10!=null) {tableMap.put("EOERD10",eOERD10);}else {tableMap.put("EOERD10","NA");}
					 engineThermalCycles = resultSet.getString("EngineThermalCycles");if( engineThermalCycles!=null) {tableMap.put("EngineThermalCycles",engineThermalCycles);}else {tableMap.put("EngineThermalCycles","NA");}
					 engineStartCount = resultSet.getString("EngineStartCount");if( engineStartCount!=null) {tableMap.put("EngineStartCount",engineStartCount);}else {tableMap.put("EngineStartCount","NA");}
					 eCUOntime= resultSet.getString("ECUOntime");if( eCUOntime!=null) {tableMap.put("ECUOntime",eCUOntime);}else {tableMap.put("ECUOntime","NA");}
					 ambient1= resultSet.getString("Ambient1");if( ambient1!=null) {tableMap.put("Ambient1", ambient1);}else {tableMap.put("Ambient1","NA");}
					 ambient2= resultSet.getString("Ambient2");if( ambient2!=null) {tableMap.put("Ambient2", ambient2);}else {tableMap.put("Ambient2","NA");}
					 ambient3= resultSet.getString("Ambient3");if( ambient3!=null) {tableMap.put("Ambient3", ambient3);}else {tableMap.put("Ambient3","NA");}
					 ambient4= resultSet.getString("Ambient4");if( ambient4!=null) {tableMap.put("Ambient4", ambient4);}else {tableMap.put("Ambient4","NA");}
					 ambient5= resultSet.getString("Ambient5");if( ambient5!=null) {tableMap.put("Ambient5", ambient5);}else {tableMap.put("Ambient5","NA");}
					 ambient6= resultSet.getString("Ambient6");if( ambient6!=null) {tableMap.put("Ambient6", ambient6);}else {tableMap.put("Ambient6","NA");}
					 ambient7= resultSet.getString("Ambient7");if( ambient7!=null) {tableMap.put("Ambient7", ambient7);}else {tableMap.put("Ambient7","NA");}
					 ambient8= resultSet.getString("Ambient8");if( ambient8!=null) {tableMap.put("Ambient8", ambient8);}else {tableMap.put("Ambient8","NA");}
					 ambient9= resultSet.getString("Ambient9");if( ambient9!=null) {tableMap.put("Ambient9", ambient9);}else {tableMap.put("Ambient9","NA");}
					 ambient10= resultSet.getString("Ambient10");if( ambient10!=null) {tableMap.put("Ambient10", ambient10);}else {tableMap.put("Ambient10","NA");}
					 ambient11= resultSet.getString("Ambient11");if( ambient11!=null) {tableMap.put("Ambient11", ambient11);}else {tableMap.put("Ambient11","NA");}
					 dOCOutlet1= resultSet.getString("DOCOutlet1");if(dOCOutlet1!=null) {tableMap.put("DOCOutlet1",dOCOutlet1);}else {tableMap.put("DOCOutlet1","NA");}
					 dOCOutlet2= resultSet.getString("DOCOutlet2");if(dOCOutlet2!=null) {tableMap.put("DOCOutlet2",dOCOutlet2);}else {tableMap.put("DOCOutlet2","NA");}
					 dOCOutlet3= resultSet.getString("DOCOutlet3");if(dOCOutlet3!=null) {tableMap.put("DOCOutlet3",dOCOutlet3);}else {tableMap.put("DOCOutlet3","NA");}
					 dOCOutlet4= resultSet.getString("DOCOutlet4");if(dOCOutlet4!=null) {tableMap.put("DOCOutlet4",dOCOutlet4);}else {tableMap.put("DOCOutlet4","NA");}
					 dOCOutlet5= resultSet.getString("DOCOutlet5");if(dOCOutlet5!=null) {tableMap.put("DOCOutlet5",dOCOutlet5);}else {tableMap.put("DOCOutlet5","NA");}
					 dOCOutlet6= resultSet.getString("DOCOutlet6");if(dOCOutlet6!=null) {tableMap.put("DOCOutlet6",dOCOutlet6);}else {tableMap.put("DOCOutlet6","NA");}
					 dOCOutlet7= resultSet.getString("DOCOutlet7");if(dOCOutlet7!=null) {tableMap.put("DOCOutlet7",dOCOutlet7);}else {tableMap.put("DOCOutlet7","NA");}
					 dOCOutlet8= resultSet.getString("DOCOutlet8");if(dOCOutlet8!=null) {tableMap.put("DOCOutlet8",dOCOutlet8);}else {tableMap.put("DOCOutlet8","NA");}
					 dOCOutlet9= resultSet.getString("DOCOutlet9");if(dOCOutlet9!=null) {tableMap.put("DOCOutlet9",dOCOutlet9);}else {tableMap.put("DOCOutlet9","NA");}
					 dOCOutlet10= resultSet.getString("DOCOutlet10");if(dOCOutlet10!=null) {tableMap.put("DOCOutlet10",dOCOutlet10);}else {tableMap.put("DOCOutlet10","NA");}
					 dOCOutlet11= resultSet.getString("DOCOutlet11");if(dOCOutlet11!=null) {tableMap.put("DOCOutlet11",dOCOutlet11);}else {tableMap.put("DOCOutlet11","NA");}
					 dOCOutlet12= resultSet.getString("DOCOutlet12");if(dOCOutlet12!=null) {tableMap.put("DOCOutlet12",dOCOutlet12);}else {tableMap.put("DOCOutlet12","NA");}
					 dOCOutlet13= resultSet.getString("DOCOutlet13");if(dOCOutlet13!=null) {tableMap.put("DOCOutlet13",dOCOutlet13);}else {tableMap.put("DOCOutlet13","NA");}
					 dOCOutlet14= resultSet.getString("DOCOutlet14");if(dOCOutlet14!=null) {tableMap.put("DOCOutlet14",dOCOutlet14);}else {tableMap.put("DOCOutlet14","NA");}
					 sulphurMonitor= resultSet.getString("SulphurMonitor");if( sulphurMonitor!=null) {tableMap.put("SulphurMonitor",sulphurMonitor);}else {tableMap.put("sulphurMonitor","NA");}
					 activeRestorations= resultSet.getString("ActiveRestorations");if( activeRestorations!=null) {tableMap.put("ActiveRestorations",activeRestorations);}else {tableMap.put("ActiveRestorations","NA");}
					 aftertreatment1= resultSet.getString("Aftertreatment1");if(aftertreatment1!=null) {tableMap.put("Aftertreatment1",aftertreatment1);}else {tableMap.put("Aftertreatment1","NA");}
					 aftertreatment2= resultSet.getString("Aftertreatment2");if(aftertreatment2!=null) {tableMap.put("Aftertreatment2",aftertreatment2);}else {tableMap.put("Aftertreatment2","NA");}
					 aftertreatment3= resultSet.getString("Aftertreatment3");if(aftertreatment3!=null) {tableMap.put("Aftertreatment3",aftertreatment3);}else {tableMap.put("Aftertreatment3","NA");}
					 aftertreatment4= resultSet.getString("Aftertreatment4");if(aftertreatment4!=null) {tableMap.put("Aftertreatment4",aftertreatment4);}else {tableMap.put("Aftertreatment4","NA");}
					 aftertreatment5= resultSet.getString("Aftertreatment5");if(aftertreatment5!=null) {tableMap.put("Aftertreatment5",aftertreatment5);}else {tableMap.put("Aftertreatment5","NA");}
					 aftertreatment6= resultSet.getString("Aftertreatment6");if(aftertreatment6!=null) {tableMap.put("Aftertreatment6",aftertreatment6);}else {tableMap.put("Aftertreatment6","NA");}
					 aftertreatment7= resultSet.getString("Aftertreatment7");if(aftertreatment7!=null) {tableMap.put("Aftertreatment7",aftertreatment7);}else {tableMap.put("Aftertreatment7","NA");}
					 aftertreatment8= resultSet.getString("Aftertreatment8");if(aftertreatment8!=null) {tableMap.put("Aftertreatment8",aftertreatment8);}else {tableMap.put("Aftertreatment8","NA");}
					 aftertreatment9= resultSet.getString("Aftertreatment9");if(aftertreatment9!=null) {tableMap.put("Aftertreatment9",aftertreatment9);}else {tableMap.put("Aftertreatment9","NA");}
					 aftertreatment10= resultSet.getString("Aftertreatment10");if(aftertreatment10!=null) {tableMap.put("Aftertreatment10",aftertreatment10);}else {tableMap.put("Aftertreatment10","NA");}
					 aftertreatment11= resultSet.getString("Aftertreatment11");if(aftertreatment11!=null) {tableMap.put("Aftertreatment11",aftertreatment11);}else {tableMap.put("Aftertreatment11","NA");}
					 aftertreatment12= resultSet.getString("Aftertreatment12");if(aftertreatment12!=null) {tableMap.put("Aftertreatment12",aftertreatment12);}else {tableMap.put("Aftertreatment12","NA");}
					 aftertreatment13= resultSet.getString("Aftertreatment13");if(aftertreatment13!=null) {tableMap.put("Aftertreatment13",aftertreatment13);}else {tableMap.put("Aftertreatment13","NA");}
					 aftertreatment14= resultSet.getString("Aftertreatment14");if(aftertreatment14!=null) {tableMap.put("Aftertreatment14",aftertreatment14);}else {tableMap.put("Aftertreatment14","NA");}
					 pCSDPRRng1= resultSet.getString("PCSDPRRng1");if(pCSDPRRng1!=null) {tableMap.put("PCSDPRRng1",pCSDPRRng1);}else {tableMap.put("PCSDPRRng1","NA");}
					 pCSDPRRng2= resultSet.getString("PCSDPRRng2");if(pCSDPRRng2!=null) {tableMap.put("PCSDPRRng2",pCSDPRRng2);}else {tableMap.put("PCSDPRRng2","NA");}
					 pCSDPRRng3= resultSet.getString("PCSDPRRng3");if(pCSDPRRng3!=null) {tableMap.put("PCSDPRRng3",pCSDPRRng3);}else {tableMap.put("PCSDPRRng3","NA");}
					 pCSDPRRng4= resultSet.getString("PCSDPRRng4");if(pCSDPRRng4!=null) {tableMap.put("PCSDPRRng4",pCSDPRRng4);}else {tableMap.put("PCSDPRRng4","NA");}
					 pCSDPRRng5= resultSet.getString("PCSDPRRng5");if(pCSDPRRng5!=null) {tableMap.put("PCSDPRRng5",pCSDPRRng5);}else {tableMap.put("PCSDPRRng5","NA");}
					 pCSDPRRng6= resultSet.getString("PCSDPRRng6");if(pCSDPRRng6!=null) {tableMap.put("PCSDPRRng6",pCSDPRRng6);}else {tableMap.put("PCSDPRRng6","NA");}
					 pCSDPRRng7= resultSet.getString("PCSDPRRng7");if(pCSDPRRng7!=null) {tableMap.put("PCSDPRRng7",pCSDPRRng7);}else {tableMap.put("PCSDPRRng7","NA");}
					 pCSDPRRng8= resultSet.getString("PCSDPRRng8");if(pCSDPRRng8!=null) {tableMap.put("PCSDPRRng8",pCSDPRRng8);}else {tableMap.put("PCSDPRRng8","NA");}
					 pCSDPRRng9= resultSet.getString("PCSDPRRng9");if(pCSDPRRng9!=null) {tableMap.put("PCSDPRRng9",pCSDPRRng9);}else {tableMap.put("PCSDPRRng9","NA");}
					 pCSDPRRng10= resultSet.getString("PCSDPRRng10");if(pCSDPRRng10!=null) {tableMap.put("PCSDPRRng10",pCSDPRRng10);}else {tableMap.put("PCSDPRRng10","NA");}
					 stationaryRestoration= resultSet.getString("StationaryRestoration");if( stationaryRestoration!=null) {tableMap.put("StationaryRestoration",stationaryRestoration);}else {tableMap.put("StationaryRestoration","NA");}
					 lastRestoration= resultSet.getString("LastRestoration");if( lastRestoration!=null) {tableMap.put("LastRestoration",lastRestoration);}else {tableMap.put("LastRestoration","NA");}
					 sootMonitor= resultSet.getString("SootMonitor");if( sootMonitor!=null) {tableMap.put("SootMonitor",sootMonitor);}else {tableMap.put("SootMonitor","NA");}
					 ashMonitor= resultSet.getString("AshMonitor");if( ashMonitor!=null) {tableMap.put("AshMonitor",ashMonitor);}else {tableMap.put("AshMonitor","NA");}
					 aftertreatmentTFU= resultSet.getString("AftertreatmentTFU");if( aftertreatmentTFU!=null) {tableMap.put("AftertreatmentTFU",aftertreatmentTFU);}else {tableMap.put("AftertreatmentTFU","NA");}
					 aftertreatmentTRT= resultSet.getString("AftertreatmentTRT");if( aftertreatmentTRT!=null) {tableMap.put("AftertreatmentTRT",aftertreatmentTRT);}else {tableMap.put("AftertreatmentTRT","NA");}
					 dPFOutlet1= resultSet.getString("DPFOutlet1");if(dPFOutlet1!=null) {tableMap.put("DPFOutlet1",dPFOutlet1);}else {tableMap.put("DPFOutlet1","NA");}
					 dPFOutlet2= resultSet.getString("DPFOutlet2");if(dPFOutlet2!=null) {tableMap.put("DPFOutlet2",dPFOutlet2);}else {tableMap.put("DPFOutlet2","NA");}
					 dPFOutlet3= resultSet.getString("DPFOutlet3");if(dPFOutlet3!=null) {tableMap.put("DPFOutlet3",dPFOutlet3);}else {tableMap.put("DPFOutlet3","NA");}
					 dPFOutlet4= resultSet.getString("DPFOutlet4");if(dPFOutlet4!=null) {tableMap.put("DPFOutlet4",dPFOutlet4);}else {tableMap.put("DPFOutlet4","NA");}
					 dPFOutlet5= resultSet.getString("DPFOutlet5");if(dPFOutlet5!=null) {tableMap.put("DPFOutlet5",dPFOutlet5);}else {tableMap.put("DPFOutlet5","NA");}
					 dPFOutlet6= resultSet.getString("DPFOutlet6");if(dPFOutlet6!=null) {tableMap.put("DPFOutlet6",dPFOutlet6);}else {tableMap.put("DPFOutlet6","NA");}
					 dPFOutlet7= resultSet.getString("DPFOutlet7");if(dPFOutlet7!=null) {tableMap.put("DPFOutlet7",dPFOutlet7);}else {tableMap.put("DPFOutlet7","NA");}
					 dPFOutlet8= resultSet.getString("DPFOutlet8");if(dPFOutlet8!=null) {tableMap.put("DPFOutlet8",dPFOutlet8);}else {tableMap.put("DPFOutlet8","NA");}
					 dPFOutlet9= resultSet.getString("DPFOutlet9");if(dPFOutlet9!=null) {tableMap.put("DPFOutlet9",dPFOutlet9);}else {tableMap.put("DPFOutlet9","NA");}
					 dPFOutlet10= resultSet.getString("DPFOutlet10");if(dPFOutlet10!=null) {tableMap.put("DPFOutlet10",dPFOutlet10);}else {tableMap.put("DPFOutlet10","NA");}
					 dPFOutlet11= resultSet.getString("DPFOutlet11");if(dPFOutlet11!=null) {tableMap.put("DPFOutlet11",dPFOutlet11);}else {tableMap.put("DPFOutlet11","NA");}
					 dPFOutlet12= resultSet.getString("DPFOutlet12");if(dPFOutlet12!=null) {tableMap.put("DPFOutlet12",dPFOutlet12);}else {tableMap.put("DPFOutlet12","NA");}
					 dPFOutlet13= resultSet.getString("DPFOutlet13");if(dPFOutlet13!=null) {tableMap.put("DPFOutlet13",dPFOutlet13);}else {tableMap.put("DPFOutlet13","NA");}
					 dPFOutlet14= resultSet.getString("DPFOutlet14");if(dPFOutlet14!=null) {tableMap.put("DPFOutlet14",dPFOutlet14);}else {tableMap.put("DPFOutlet14","NA");}
					 dieselSWITCH= resultSet.getString("DieselSWITCH");if( dieselSWITCH!=null) {tableMap.put("DieselSWITCH",dieselSWITCH);}else {tableMap.put("DieselSWITCH","NA");}
					 dieselTOOL= resultSet.getString("DieselTOOL");if( dieselTOOL!=null) {tableMap.put("DieselTOOL",dieselTOOL);}else {tableMap.put("DieselTOOL","NA");}
					 aftertreatmentStatus= resultSet.getString("AftertreatmentStatus");if( aftertreatmentStatus!=null) {tableMap.put("AftertreatmentStatus",aftertreatmentStatus);}else {tableMap.put("AftertreatmentStatus","NA");}
					 aftertreatmentIOL= resultSet.getString("AftertreatmentIOL");if( aftertreatmentIOL!=null) {tableMap.put("AftertreatmentIOL",aftertreatmentIOL);}else {tableMap.put("AftertreatmentIOL","NA");}
					 aftertreatmentISLR= resultSet.getString("AftertreatmentISLR");if( aftertreatmentISLR!=null) {tableMap.put("AftertreatmentISLR",aftertreatmentISLR);}else {tableMap.put("AftertreatmentISLR","NA");}
					 aftertreatmentTTIOL= resultSet.getString("AftertreatmentTTIOL");if( aftertreatmentTTIOL!=null) {tableMap.put("AftertreatmentTTIOL",aftertreatmentTTIOL);}else {tableMap.put("AftertreatmentTTIOL","NA");}
					 aftertreatmentLR= resultSet.getString("AftertreatmentLR");if( aftertreatmentLR!=null) {tableMap.put("AftertreatmentLR",aftertreatmentLR);}else {tableMap.put("AftertreatmentLR","NA");}
					 eSHTLC= resultSet.getString("ESHTLC");if( eSHTLC!=null) {tableMap.put("ESHTLC",eSHTLC);}else {tableMap.put("ESHTLC","NA");}
					 dPFSRNR= resultSet.getString("DPFSRNR");if( dPFSRNR!=null) {tableMap.put("DPFSRNR",dPFSRNR);}else {tableMap.put("DPFSRNR","NA");}
					 dPFSMRA= resultSet.getString("DPFSMRA");if( dPFSMRA!=null) {tableMap.put("DPFSMRA",dPFSMRA);}else {tableMap.put("DPFSMRA","NA");}
					 dPFSSRR= resultSet.getString("DPFSSRR");if( dPFSSRR!=null) {tableMap.put("DPFSSRR",dPFSSRR);}else {tableMap.put("DPFSSRR","NA");}
					 dPFSRS= resultSet.getString("DPFSRS");if( dPFSRS!=null) {tableMap.put("DPFSRS",dPFSRS);}else {tableMap.put("DPFSRS","NA");}
					 dPFSRPL2= resultSet.getString("DPFSRPL2");if( dPFSRPL2!=null) {tableMap.put("DPFSRPL2",dPFSRPL2);}else {tableMap.put("DPFSRPL2","NA");}
					 dPFSMRRL3= resultSet.getString("DPFSMRRL3");if( dPFSMRRL3!=null) {tableMap.put("DPFSMRRL3",dPFSMRRL3);}else {tableMap.put("DPFSMRRL3","NA");}
					 dPFSRA= resultSet.getString("DPFSRA");if( dPFSRA!=null) {tableMap.put("DPFSRA",dPFSRA);}else {tableMap.put("DPFSRA","NA");}
					 dPFSRC= resultSet.getString("DPFSRC");if( dPFSRC!=null) {tableMap.put("DPFSRC",dPFSRC);}else {tableMap.put("DPFSRC","NA");}
					 aARIDTISRABIBO = resultSet.getString("AARIDTISRABIBO");if( aARIDTISRABIBO!=null) {tableMap.put("AARIDTISRABIBO",aARIDTISRABIBO);}else {tableMap.put("AARIDTISRABIBO","NA");}
				
					mapList.add(tableMap);
				}
				
				resultSet = statement.executeQuery(endTAssetMonQuery);
				while(resultSet.next())
				{
					tableMap = new HashMap<String,String>();
					tableMap.put("Vin",resultSet.getString("Vin")); 					
     				 hours = resultSet.getString("Hours");if(hours !=null) {tableMap.put("Hours",hours);}else {tableMap.put("Hours","NA");}
     				tDate=resultSet.getString("Transactiontimestamp").substring(0,19); 
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
					transactiontimestamp = LocalDateTime.parse(tDate, dtf).format(dtf2);if(tDate !=null) {tableMap.put("Transactiontimestamp",transactiontimestamp);}else {tableMap.put("Transactiontimestamp","NA");}
					 adBlueConsumption= resultSet.getString("AdBlueConsumption");if(adBlueConsumption !=null) {tableMap.put("AdBlueConsumption",adBlueConsumption);}else {tableMap.put("AdBlueConsumption","NA");}
					 dEF_Concentration= resultSet.getString("DEF_Concentration");if(dEF_Concentration !=null) {tableMap.put("DEF_Concentration",dEF_Concentration);}else {tableMap.put("DEF_Concentration","NA");}
					 dEF_Temperature= resultSet.getString("DEF_Temperature");if(dEF_Temperature !=null) {tableMap.put("DEF_Temperature",dEF_Temperature);}else {tableMap.put("DEF_Temperature","NA");}
					// engineOnCount= resultSet.getString("EngineOnCount");if(engineOnCount !=null) {tableMap.put("EngineOnCount",engineOnCount);}else {tableMap.put("EngineOnCount","NA");}
					 powerRatingRequest= resultSet.getString("PowerRatingRequest");if(powerRatingRequest !=null) {tableMap.put("PowerRatingRequest",powerRatingRequest);}else {tableMap.put("PowerRatingRequest","NA");}
					 speedRange1FuelRange1 = resultSet.getString("SpeedRange1FuelRange1");if(speedRange1FuelRange1 !=null) {tableMap.put("SpeedRange1FuelRange1",speedRange1FuelRange1);}else {tableMap.put("SpeedRange1FuelRange1","NA");}
					 speedRange1FuelRange2 = resultSet.getString("SpeedRange1FuelRange2");if( speedRange1FuelRange2!=null) {tableMap.put("SpeedRange1FuelRange2",speedRange1FuelRange2);}else {tableMap.put("SpeedRange1FuelRange2","NA");}
					 speedRange1FuelRange3 = resultSet.getString("SpeedRange1FuelRange3");if( speedRange1FuelRange3!=null) {tableMap.put("SpeedRange1FuelRange3",speedRange1FuelRange3);}else {tableMap.put("SpeedRange1FuelRange3","NA");}
					 speedRange1FuelRange4 = resultSet.getString("SpeedRange1FuelRange4");if( speedRange1FuelRange4!=null) {tableMap.put("SpeedRange1FuelRange4",speedRange1FuelRange4);}else {tableMap.put("SpeedRange1FuelRange4","NA");}
					 speedRange1FuelRange5 = resultSet.getString("SpeedRange1FuelRange5");if( speedRange1FuelRange5!=null) {tableMap.put("SpeedRange1FuelRange5",speedRange1FuelRange5);}else {tableMap.put("SpeedRange1FuelRange5","NA");}
					 speedRange1FuelRange6 = resultSet.getString("SpeedRange1FuelRange6");if( speedRange1FuelRange6!=null) {tableMap.put("SpeedRange1FuelRange6",speedRange1FuelRange6);}else {tableMap.put("SpeedRange1FuelRange6","NA");}
					 speedRange1FuelRange7 = resultSet.getString("SpeedRange1FuelRange7");if( speedRange1FuelRange7!=null) {tableMap.put("SpeedRange1FuelRange7",speedRange1FuelRange7);}else {tableMap.put("SpeedRange1FuelRange7","NA");}
					 speedRange1FuelRange8 = resultSet.getString("SpeedRange1FuelRange8");if( speedRange1FuelRange8!=null) {tableMap.put("SpeedRange1FuelRange8",speedRange1FuelRange8);}else {tableMap.put("SpeedRange1FuelRange8","NA");}
					 speedRange1FuelRange9 = resultSet.getString("SpeedRange1FuelRange9");if( speedRange1FuelRange9!=null) {tableMap.put("SpeedRange1FuelRange9",speedRange1FuelRange9);}else {tableMap.put("SpeedRange1FuelRange9","NA");}
					 speedRange1FuelRange10 = resultSet.getString("SpeedRange1FuelRange10");if( speedRange1FuelRange10!=null) {tableMap.put("SpeedRange1FuelRange10",speedRange1FuelRange10);}else {tableMap.put("SpeedRange1FuelRange10","NA");}
					 speedRange2FuelRange1 = resultSet.getString("SpeedRange2FuelRange1");if( speedRange2FuelRange1!=null) {tableMap.put("SpeedRange2FuelRange1",speedRange2FuelRange1);}else {tableMap.put("SpeedRange2FuelRange1","NA");}
					 speedRange2FuelRange2 = resultSet.getString("SpeedRange2FuelRange2");if( speedRange2FuelRange2!=null) {tableMap.put("SpeedRange2FuelRange2",speedRange2FuelRange2);}else {tableMap.put("SpeedRange2FuelRange2","NA");}
					 speedRange2FuelRange3 = resultSet.getString("SpeedRange2FuelRange3");if( speedRange2FuelRange3!=null) {tableMap.put("SpeedRange2FuelRange3",speedRange2FuelRange3);}else {tableMap.put("SpeedRange2FuelRange3","NA");}
					 speedRange2FuelRange4 = resultSet.getString("SpeedRange2FuelRange4");if( speedRange2FuelRange4!=null) {tableMap.put("SpeedRange2FuelRange4",speedRange2FuelRange4);}else {tableMap.put("SpeedRange2FuelRange4","NA");}
					 speedRange2FuelRange5 = resultSet.getString("SpeedRange2FuelRange5");if( speedRange2FuelRange5!=null) {tableMap.put("SpeedRange2FuelRange5",speedRange2FuelRange5);}else {tableMap.put("SpeedRange2FuelRange5","NA");}
					 speedRange2FuelRange6 = resultSet.getString("SpeedRange2FuelRange6");if( speedRange2FuelRange6!=null) {tableMap.put("SpeedRange2FuelRange6",speedRange2FuelRange6);}else {tableMap.put("SpeedRange2FuelRange6","NA");}
					 speedRange2FuelRange7 = resultSet.getString("SpeedRange2FuelRange7");if( speedRange2FuelRange7!=null) {tableMap.put("SpeedRange2FuelRange7",speedRange2FuelRange7);}else {tableMap.put("SpeedRange2FuelRange7","NA");}
					 speedRange2FuelRange8 = resultSet.getString("SpeedRange2FuelRange8");if( speedRange2FuelRange8!=null) {tableMap.put("SpeedRange2FuelRange8",speedRange2FuelRange8);}else {tableMap.put("SpeedRange2FuelRange8","NA");}
					 speedRange2FuelRange9 = resultSet.getString("SpeedRange2FuelRange9");if( speedRange2FuelRange9!=null) {tableMap.put("SpeedRange2FuelRange9",speedRange2FuelRange9);}else {tableMap.put("SpeedRange2FuelRange9","NA");}
					 speedRange2FuelRange10 = resultSet.getString("SpeedRange2FuelRange10");if( speedRange2FuelRange10!=null) {tableMap.put("SpeedRange2FuelRange10",speedRange2FuelRange10);}else {tableMap.put("SpeedRange2FuelRange10","NA");}
					 speedRange3FuelRange1 = resultSet.getString("SpeedRange3FuelRange1");if( speedRange3FuelRange1!=null) {tableMap.put("SpeedRange3FuelRange1",speedRange3FuelRange1);}else {tableMap.put("SpeedRange3FuelRange1","NA");}
					 speedRange3FuelRange2 = resultSet.getString("SpeedRange3FuelRange2");if( speedRange3FuelRange2!=null) {tableMap.put("SpeedRange3FuelRange2",speedRange3FuelRange2);}else {tableMap.put("SpeedRange3FuelRange2","NA");}
					 speedRange3FuelRange3 = resultSet.getString("SpeedRange3FuelRange3");if( speedRange3FuelRange3!=null) {tableMap.put("SpeedRange3FuelRange3",speedRange3FuelRange3);}else {tableMap.put("SpeedRange3FuelRange3","NA");}
					 speedRange3FuelRange4 = resultSet.getString("SpeedRange3FuelRange4");if( speedRange3FuelRange4!=null) {tableMap.put("SpeedRange3FuelRange4",speedRange3FuelRange4);}else {tableMap.put("SpeedRange3FuelRange4","NA");}
					 speedRange3FuelRange5 = resultSet.getString("SpeedRange3FuelRange5");if( speedRange3FuelRange5!=null) {tableMap.put("SpeedRange3FuelRange5",speedRange3FuelRange5);}else {tableMap.put("SpeedRange3FuelRange5","NA");}
					 speedRange3FuelRange6 = resultSet.getString("SpeedRange3FuelRange6");if( speedRange3FuelRange6!=null) {tableMap.put("SpeedRange3FuelRange6",speedRange3FuelRange6);}else {tableMap.put("SpeedRange3FuelRange6","NA");}
					 speedRange3FuelRange7 = resultSet.getString("SpeedRange3FuelRange7");if( speedRange3FuelRange7!=null) {tableMap.put("SpeedRange3FuelRange7",speedRange3FuelRange7);}else {tableMap.put("SpeedRange3FuelRange7","NA");}
					 speedRange3FuelRange8 = resultSet.getString("SpeedRange3FuelRange8");if( speedRange3FuelRange8!=null) {tableMap.put("SpeedRange3FuelRange8",speedRange3FuelRange8);}else {tableMap.put("SpeedRange3FuelRange8","NA");}
					 speedRange3FuelRange9 = resultSet.getString("SpeedRange3FuelRange9");if( speedRange3FuelRange9!=null) {tableMap.put("SpeedRange3FuelRange9",speedRange3FuelRange9);}else {tableMap.put("SpeedRange3FuelRange9","NA");}
					 speedRange3FuelRange10 = resultSet.getString("SpeedRange3FuelRange10");if( speedRange3FuelRange10!=null) {tableMap.put("SpeedRange3FuelRange10",speedRange3FuelRange10);}else {tableMap.put("SpeedRange3FuelRange10","NA");}
					 speedRange4FuelRange1= resultSet.getString("SpeedRange4FuelRange1");if( speedRange4FuelRange1!=null) {tableMap.put("SpeedRange4FuelRange1",speedRange4FuelRange1);}else {tableMap.put("SpeedRange4FuelRange1","NA");}
					 speedRange4FuelRange2= resultSet.getString("SpeedRange4FuelRange2");if( speedRange4FuelRange2!=null) {tableMap.put("SpeedRange4FuelRange2",speedRange4FuelRange2);}else {tableMap.put("SpeedRange4FuelRange2","NA");}
					 speedRange4FuelRange3= resultSet.getString("SpeedRange4FuelRange3");if( speedRange4FuelRange3!=null) {tableMap.put("SpeedRange4FuelRange3",speedRange4FuelRange3);}else {tableMap.put("SpeedRange4FuelRange3","NA");}
					 speedRange4FuelRange4= resultSet.getString("SpeedRange4FuelRange4");if( speedRange4FuelRange4!=null) {tableMap.put("SpeedRange4FuelRange4",speedRange4FuelRange4);}else {tableMap.put("SpeedRange4FuelRange4","NA");}
					 speedRange4FuelRange5= resultSet.getString("SpeedRange4FuelRange5");if( speedRange4FuelRange5!=null) {tableMap.put("SpeedRange4FuelRange5",speedRange4FuelRange5);}else {tableMap.put("SpeedRange4FuelRange5","NA");}
					 speedRange4FuelRange6= resultSet.getString("SpeedRange4FuelRange6");if( speedRange4FuelRange6!=null) {tableMap.put("SpeedRange4FuelRange6",speedRange4FuelRange6);}else {tableMap.put("SpeedRange4FuelRange6","NA");}
					 speedRange4FuelRange7= resultSet.getString("SpeedRange4FuelRange7");if( speedRange4FuelRange7!=null) {tableMap.put("SpeedRange4FuelRange7",speedRange4FuelRange7);}else {tableMap.put("SpeedRange4FuelRange7","NA");}
					 speedRange4FuelRange8= resultSet.getString("SpeedRange4FuelRange8");if( speedRange4FuelRange8!=null) {tableMap.put("SpeedRange4FuelRange8",speedRange4FuelRange8);}else {tableMap.put("SpeedRange4FuelRange8","NA");}
					 speedRange4FuelRange9= resultSet.getString("SpeedRange4FuelRange9");if( speedRange4FuelRange9!=null) {tableMap.put("SpeedRange4FuelRange9",speedRange4FuelRange9);}else {tableMap.put("SpeedRange4FuelRange9","NA");}
					 speedRange4FuelRange10= resultSet.getString("SpeedRange4FuelRange10");if( speedRange4FuelRange10!=null) {tableMap.put("SpeedRange4FuelRange10",speedRange4FuelRange10);}else {tableMap.put("SpeedRange4FuelRange10","NA");}
					 speedRange5FuelRange1= resultSet.getString("SpeedRange5FuelRange1");if( speedRange5FuelRange1!=null) {tableMap.put("SpeedRange5FuelRange1",speedRange5FuelRange1);}else {tableMap.put("SpeedRange5FuelRange1","NA");}
					 speedRange5FuelRange2= resultSet.getString("SpeedRange5FuelRange2");if( speedRange5FuelRange2!=null) {tableMap.put("SpeedRange5FuelRange2",speedRange5FuelRange2);}else {tableMap.put("SpeedRange5FuelRange2","NA");}
					 speedRange5FuelRange3= resultSet.getString("SpeedRange5FuelRange3");if( speedRange5FuelRange3!=null) {tableMap.put("SpeedRange5FuelRange3",speedRange5FuelRange3);}else {tableMap.put("SpeedRange5FuelRange3","NA");}
					 speedRange5FuelRange4= resultSet.getString("SpeedRange5FuelRange4");if( speedRange5FuelRange4!=null) {tableMap.put("SpeedRange5FuelRange4",speedRange5FuelRange4);}else {tableMap.put("SpeedRange5FuelRange4","NA");}
					 speedRange5FuelRange5= resultSet.getString("SpeedRange5FuelRange5");if( speedRange5FuelRange5!=null) {tableMap.put("SpeedRange5FuelRange5",speedRange5FuelRange5);}else {tableMap.put("SpeedRange5FuelRange5","NA");}
					 speedRange5FuelRange6= resultSet.getString("SpeedRange5FuelRange6");if( speedRange5FuelRange6!=null) {tableMap.put("SpeedRange5FuelRange6",speedRange5FuelRange6);}else {tableMap.put("SpeedRange5FuelRange6","NA");}
					 speedRange5FuelRange7= resultSet.getString("SpeedRange5FuelRange7");if( speedRange5FuelRange7!=null) {tableMap.put("SpeedRange5FuelRange7",speedRange5FuelRange7);}else {tableMap.put("SpeedRange5FuelRange7","NA");}
					 speedRange5FuelRange8= resultSet.getString("SpeedRange5FuelRange8");if( speedRange5FuelRange8!=null) {tableMap.put("SpeedRange5FuelRange8",speedRange5FuelRange8);}else {tableMap.put("SpeedRange5FuelRange8","NA");}
					 speedRange5FuelRange9= resultSet.getString("SpeedRange5FuelRange9");if( speedRange5FuelRange9!=null) {tableMap.put("SpeedRange5FuelRange9",speedRange5FuelRange9);}else {tableMap.put("SpeedRange5FuelRange9","NA");}
					 speedRange5FuelRange10= resultSet.getString("SpeedRange5FuelRange10");if( speedRange5FuelRange10!=null) {tableMap.put("SpeedRange5FuelRange10",speedRange5FuelRange10);}else {tableMap.put("SpeedRange5FuelRange10","NA");}
					 speedRange6FuelRange1= resultSet.getString("SpeedRange6FuelRange1");if( speedRange6FuelRange1!=null) {tableMap.put("SpeedRange6FuelRange1",speedRange6FuelRange1);}else {tableMap.put("SpeedRange6FuelRange1","NA");}
					 speedRange6FuelRange2= resultSet.getString("SpeedRange6FuelRange2");if( speedRange6FuelRange2!=null) {tableMap.put("SpeedRange6FuelRange2",speedRange6FuelRange2);}else {tableMap.put("SpeedRange6FuelRange2","NA");}
					 speedRange6FuelRange3= resultSet.getString("SpeedRange6FuelRange3");if( speedRange6FuelRange3!=null) {tableMap.put("SpeedRange6FuelRange3",speedRange6FuelRange3);}else {tableMap.put("SpeedRange6FuelRange3","NA");}
					 speedRange6FuelRange4= resultSet.getString("SpeedRange6FuelRange4");if( speedRange6FuelRange4!=null) {tableMap.put("SpeedRange6FuelRange4",speedRange6FuelRange4);}else {tableMap.put("SpeedRange6FuelRange4","NA");}
					 speedRange6FuelRange5= resultSet.getString("SpeedRange6FuelRange5");if( speedRange6FuelRange5!=null) {tableMap.put("SpeedRange6FuelRange5",speedRange6FuelRange5);}else {tableMap.put("SpeedRange6FuelRange5","NA");}
					 speedRange6FuelRange6= resultSet.getString("SpeedRange6FuelRange6");if( speedRange6FuelRange6!=null) {tableMap.put("SpeedRange6FuelRange6",speedRange6FuelRange6);}else {tableMap.put("SpeedRange6FuelRange6","NA");}
					 speedRange6FuelRange7= resultSet.getString("SpeedRange6FuelRange7");if( speedRange6FuelRange7!=null) {tableMap.put("SpeedRange6FuelRange7",speedRange6FuelRange7);}else {tableMap.put("SpeedRange6FuelRange7","NA");}
					 speedRange6FuelRange8= resultSet.getString("SpeedRange6FuelRange8");if( speedRange6FuelRange8!=null) {tableMap.put("SpeedRange6FuelRange8",speedRange6FuelRange8);}else {tableMap.put("SpeedRange6FuelRange8","NA");}
					 speedRange6FuelRange9= resultSet.getString("SpeedRange6FuelRange9");if( speedRange6FuelRange9!=null) {tableMap.put("SpeedRange6FuelRange9",speedRange6FuelRange9);}else {tableMap.put("SpeedRange6FuelRange9","NA");}
					 speedRange6FuelRange10= resultSet.getString("SpeedRange6FuelRange10");if( speedRange6FuelRange10!=null) {tableMap.put("SpeedRange6FuelRange10",speedRange6FuelRange10);}else {tableMap.put("SpeedRange6FuelRange10","NA");}
					 speedRange7FuelRange1= resultSet.getString("SpeedRange7FuelRange1");if( speedRange7FuelRange1!=null) {tableMap.put("SpeedRange7FuelRange1",speedRange7FuelRange1);}else {tableMap.put("SpeedRange7FuelRange1","NA");}
					 speedRange7FuelRange2= resultSet.getString("SpeedRange7FuelRange2");if( speedRange7FuelRange2!=null) {tableMap.put("SpeedRange7FuelRange2",speedRange7FuelRange2);}else {tableMap.put("SpeedRange7FuelRange2","NA");}
					 speedRange7FuelRange3= resultSet.getString("SpeedRange7FuelRange3");if( speedRange7FuelRange3!=null) {tableMap.put("SpeedRange7FuelRange3",speedRange7FuelRange3);}else {tableMap.put("SpeedRange7FuelRange3","NA");}
					 speedRange7FuelRange4= resultSet.getString("SpeedRange7FuelRange4");if( speedRange7FuelRange4!=null) {tableMap.put("SpeedRange7FuelRange4",speedRange7FuelRange4);}else {tableMap.put("SpeedRange7FuelRange4","NA");}
					 speedRange7FuelRange5= resultSet.getString("SpeedRange7FuelRange5");if( speedRange7FuelRange5!=null) {tableMap.put("SpeedRange7FuelRange5",speedRange7FuelRange5);}else {tableMap.put("SpeedRange7FuelRange5","NA");}
					 speedRange7FuelRange6= resultSet.getString("SpeedRange7FuelRange6");if( speedRange7FuelRange6!=null) {tableMap.put("SpeedRange7FuelRange6",speedRange7FuelRange6);}else {tableMap.put("SpeedRange7FuelRange6","NA");}
					 speedRange7FuelRange7= resultSet.getString("SpeedRange7FuelRange7");if( speedRange7FuelRange7!=null) {tableMap.put("SpeedRange7FuelRange7",speedRange7FuelRange7);}else {tableMap.put("SpeedRange7FuelRange7","NA");}
					 speedRange7FuelRange8= resultSet.getString("SpeedRange7FuelRange8");if( speedRange7FuelRange8!=null) {tableMap.put("SpeedRange7FuelRange8",speedRange7FuelRange8);}else {tableMap.put("SpeedRange7FuelRange8","NA");}
					 speedRange7FuelRange9= resultSet.getString("SpeedRange7FuelRange9");if( speedRange7FuelRange9!=null) {tableMap.put("SpeedRange7FuelRange9",speedRange7FuelRange9);}else {tableMap.put("SpeedRange7FuelRange9","NA");}
					 speedRange7FuelRange10= resultSet.getString("SpeedRange7FuelRange10");if( speedRange7FuelRange10!=null) {tableMap.put("SpeedRange7FuelRange10",speedRange7FuelRange10);}else {tableMap.put("SpeedRange7FuelRange10","NA");}
					 coolantTempratureResidenceRange1= resultSet.getString("CoolantTempratureResidenceRange1");if( coolantTempratureResidenceRange1!=null) {tableMap.put("CoolantTempratureResidenceRange1",coolantTempratureResidenceRange1);}else {tableMap.put("CoolantTempratureResidenceRange1","NA");}
					 coolantTempratureResidenceRange2= resultSet.getString("CoolantTempratureResidenceRange2");if( coolantTempratureResidenceRange2!=null) {tableMap.put("CoolantTempratureResidenceRange2",coolantTempratureResidenceRange2);}else {tableMap.put("CoolantTempratureResidenceRange2","NA");}
					 coolantTempratureResidenceRange3= resultSet.getString("CoolantTempratureResidenceRange3");if( coolantTempratureResidenceRange3!=null) {tableMap.put("CoolantTempratureResidenceRange3",coolantTempratureResidenceRange3);}else {tableMap.put("CoolantTempratureResidenceRange3","NA");}
					 coolantTempratureResidenceRange4= resultSet.getString("CoolantTempratureResidenceRange4");if( coolantTempratureResidenceRange4!=null) {tableMap.put("CoolantTempratureResidenceRange4",coolantTempratureResidenceRange4);}else {tableMap.put("CoolantTempratureResidenceRange4","NA");}
					 coolantTempratureResidenceRange5= resultSet.getString("CoolantTempratureResidenceRange5");if( coolantTempratureResidenceRange5!=null) {tableMap.put("CoolantTempratureResidenceRange5",coolantTempratureResidenceRange5);}else {tableMap.put("CoolantTempratureResidenceRange5","NA");}
					 coolantTempratureResidenceRange6= resultSet.getString("CoolantTempratureResidenceRange6");if( coolantTempratureResidenceRange6!=null) {tableMap.put("CoolantTempratureResidenceRange6",coolantTempratureResidenceRange6);}else {tableMap.put("CoolantTempratureResidenceRange6","NA");}
					 coolantTempratureResidenceRange7= resultSet.getString("CoolantTempratureResidenceRange7");if( coolantTempratureResidenceRange7!=null) {tableMap.put("CoolantTempratureResidenceRange7",coolantTempratureResidenceRange7);}else {tableMap.put("CoolantTempratureResidenceRange7","NA");}
					 coolantTempratureResidenceRange8= resultSet.getString("CoolantTempratureResidenceRange8");if( coolantTempratureResidenceRange8!=null) {tableMap.put("CoolantTempratureResidenceRange8",coolantTempratureResidenceRange8);}else {tableMap.put("CoolantTempratureResidenceRange8","NA");}
					 coolantTempratureResidenceRange9= resultSet.getString("CoolantTempratureResidenceRange9");if( coolantTempratureResidenceRange9!=null) {tableMap.put("CoolantTempratureResidenceRange9",coolantTempratureResidenceRange9);}else {tableMap.put("CoolantTempratureResidenceRange19","NA");}
					 coolantTempratureResidenceRange10= resultSet.getString("CoolantTempratureResidenceRange10");if( coolantTempratureResidenceRange10!=null) {tableMap.put("CoolantTempratureResidenceRange10",coolantTempratureResidenceRange10);}else {tableMap.put("CoolantTempratureResidenceRange10","NA");}
					 coolantTempratureResidenceRange11= resultSet.getString("CoolantTempratureResidenceRange11");if( coolantTempratureResidenceRange11!=null) {tableMap.put("CoolantTempratureResidenceRange11",coolantTempratureResidenceRange11);}else {tableMap.put("CoolantTempratureResidenceRange11","NA");}
					 coolantTempratureResidenceRange12= resultSet.getString("CoolantTempratureResidenceRange12");if( coolantTempratureResidenceRange12!=null) {tableMap.put("CoolantTempratureResidenceRange12",coolantTempratureResidenceRange12);}else {tableMap.put("CoolantTempratureResidenceRange12","NA");}
					 eEGTRR1= resultSet.getString("EEGTRR1");if( eEGTRR1!=null) {tableMap.put("EEGTRR1",eEGTRR1);}else {tableMap.put("EEGTRR1","NA");}
					 eEGTRR2= resultSet.getString("EEGTRR2");if( eEGTRR2!=null) {tableMap.put("EEGTRR2",eEGTRR2);}else {tableMap.put("EEGTRR2","NA");}
					 eEGTRR3= resultSet.getString("EEGTRR3");if( eEGTRR3!=null) {tableMap.put("EEGTRR3",eEGTRR3);}else {tableMap.put("EEGTRR3","NA");}
					 eEGTRR4= resultSet.getString("EEGTRR4");if( eEGTRR4!=null) {tableMap.put("EEGTRR4",eEGTRR4);}else {tableMap.put("EEGTRR4","NA");}
					 eEGTRR5= resultSet.getString("EEGTRR5");if( eEGTRR5!=null) {tableMap.put("EEGTRR5",eEGTRR5);}else {tableMap.put("EEGTRR5","NA");}
					 eEGTRR6= resultSet.getString("EEGTRR6");if( eEGTRR6!=null) {tableMap.put("EEGTRR6",eEGTRR6);}else {tableMap.put("EEGTRR6","NA");}
					 eEGTRR7= resultSet.getString("EEGTRR7");if( eEGTRR7!=null) {tableMap.put("EEGTRR7",eEGTRR7);}else {tableMap.put("EEGTRR7","NA");}
					 eEGTRR8= resultSet.getString("EEGTRR8");if( eEGTRR8!=null) {tableMap.put("EEGTRR8",eEGTRR8);}else {tableMap.put("EEGTRR8","NA");}
					 eEGTRR9= resultSet.getString("EEGTRR9");if( eEGTRR9!=null) {tableMap.put("EEGTRR9",eEGTRR9);}else {tableMap.put("EEGTRR9","NA");}
					 eEGTRR10= resultSet.getString("EEGTRR10");if( eEGTRR10!=null) {tableMap.put("EEGTRR10",eEGTRR10);}else {tableMap.put("EEGTRR10","NA");}
					 eEGTRR11= resultSet.getString("EEGTRR11");if( eEGTRR11!=null) {tableMap.put("EEGTRR11",eEGTRR11);}else {tableMap.put("EEGTRR11","NA");}
					 eEGTRR12= resultSet.getString("EEGTRR12");if( eEGTRR12!=null) {tableMap.put("EEGTRR12",eEGTRR12);}else {tableMap.put("EEGTRR12","NA");}
					 fuelTempResidenceRange1= resultSet.getString("FuelTempResidenceRange1");if( fuelTempResidenceRange1!=null) {tableMap.put("FuelTempResidenceRange1", fuelTempResidenceRange1);}else {tableMap.put("FuelTempResidenceRange1","NA");}
					 fuelTempResidenceRange2= resultSet.getString("FuelTempResidenceRange2");if( fuelTempResidenceRange2!=null) {tableMap.put("FuelTempResidenceRange2", fuelTempResidenceRange2);}else {tableMap.put("FuelTempResidenceRange2","NA");}
					 fuelTempResidenceRange3= resultSet.getString("FuelTempResidenceRange3");if( fuelTempResidenceRange3!=null) {tableMap.put("FuelTempResidenceRange3", fuelTempResidenceRange3);}else {tableMap.put("FuelTempResidenceRange3","NA");}
					 fuelTempResidenceRange4= resultSet.getString("FuelTempResidenceRange4");if( fuelTempResidenceRange4!=null) {tableMap.put("FuelTempResidenceRange4", fuelTempResidenceRange4);}else {tableMap.put("FuelTempResidenceRange4","NA");}
					 fuelTempResidenceRange5= resultSet.getString("FuelTempResidenceRange5");if( fuelTempResidenceRange5!=null) {tableMap.put("FuelTempResidenceRange5", fuelTempResidenceRange5);}else {tableMap.put("FuelTempResidenceRange5","NA");}
					 fuelTempResidenceRange6= resultSet.getString("FuelTempResidenceRange6");if( fuelTempResidenceRange6!=null) {tableMap.put("FuelTempResidenceRange6", fuelTempResidenceRange6);}else {tableMap.put("FuelTempResidenceRange6","NA");}
					 fuelTempResidenceRange7= resultSet.getString("FuelTempResidenceRange7");if( fuelTempResidenceRange7!=null) {tableMap.put("FuelTempResidenceRange7", fuelTempResidenceRange7);}else {tableMap.put("FuelTempResidenceRange7","NA");}
					 fuelTempResidenceRange8= resultSet.getString("FuelTempResidenceRange8");if( fuelTempResidenceRange8!=null) {tableMap.put("FuelTempResidenceRange8", fuelTempResidenceRange8);}else {tableMap.put("FuelTempResidenceRange8","NA");}
					 inletAir1= resultSet.getString("InletAir1");if(inletAir1!=null) {tableMap.put("InletAir1",inletAir1);}else {tableMap.put("InletAir1","NA");}
					 inletAir2= resultSet.getString("InletAir2");if(inletAir2!=null) {tableMap.put("InletAir2",inletAir2);}else {tableMap.put("InletAir2","NA");}
					 inletAir3= resultSet.getString("InletAir3");if(inletAir3!=null) {tableMap.put("InletAir3",inletAir3);}else {tableMap.put("InletAir3","NA");}
					 inletAir4= resultSet.getString("InletAir4");if(inletAir4!=null) {tableMap.put("InletAir4",inletAir4);}else {tableMap.put("InletAir4","NA");}
					 inletAir5= resultSet.getString("InletAir5");if(inletAir5!=null) {tableMap.put("InletAir5",inletAir5);}else {tableMap.put("InletAir5","NA");}
					 inletAir6= resultSet.getString("InletAir6");if(inletAir6!=null) {tableMap.put("InletAir6",inletAir6);}else {tableMap.put("InletAir6","NA");}
					 inletAir7= resultSet.getString("InletAir7");if(inletAir7!=null) {tableMap.put("InletAir7",inletAir7);}else {tableMap.put("InletAir7","NA");}
					 inletAir8= resultSet.getString("InletAir8");if(inletAir8!=null) {tableMap.put("InletAir8",inletAir8);}else {tableMap.put("InletAir8","NA");}
					 inletAir9= resultSet.getString("InletAir9");if(inletAir9!=null) {tableMap.put("InletAir9",inletAir9);}else {tableMap.put("InletAir9","NA");}
					 inletAir10= resultSet.getString("InletAir10");if(inletAir10!=null) {tableMap.put("InletAir10",inletAir10);}else {tableMap.put("InletAir10","NA");}
					 barometric1= resultSet.getString("Barometric1");if(barometric1!=null) {tableMap.put("Barometric1",barometric1);}else {tableMap.put("Barometric1","NA");}
					 barometric2= resultSet.getString("Barometric2");if(barometric2!=null) {tableMap.put("Barometric2",barometric2);}else {tableMap.put("Barometric2","NA");}
					 barometric3= resultSet.getString("Barometric3");if(barometric3!=null) {tableMap.put("Barometric3",barometric3);}else {tableMap.put("Barometric3","NA");}
					 barometric4= resultSet.getString("Barometric4");if(barometric4!=null) {tableMap.put("Barometric4",barometric4);}else {tableMap.put("Barometric4","NA");}
					 barometric5= resultSet.getString("Barometric5");if(barometric5!=null) {tableMap.put("Barometric5",barometric5);}else {tableMap.put("Barometric5","NA");}
					 barometric6= resultSet.getString("Barometric6");if(barometric6!=null) {tableMap.put("Barometric6",barometric6);}else {tableMap.put("Barometric6","NA");}
					 barometric7= resultSet.getString("Barometric7");if(barometric7!=null) {tableMap.put("Barometric7",barometric7);}else {tableMap.put("Barometric7","NA");}
					 barometric8= resultSet.getString("Barometric8");if(barometric8!=null) {tableMap.put("Barometric8",barometric8);}else {tableMap.put("Barometric8","NA");}
					 barometric9= resultSet.getString("Barometric9");if(barometric9!=null) {tableMap.put("Barometric9",barometric9);}else {tableMap.put("Barometric9","NA");}
					 barometric10= resultSet.getString("Barometric10");if(barometric10!=null) {tableMap.put("Barometric10",barometric10);}else {tableMap.put("Barometric10","NA");}
					 maxEngineSpeed = resultSet.getString("MaxEngineSpeed");if( maxEngineSpeed!=null) {tableMap.put("MaxEngineSpeed",maxEngineSpeed);}else {tableMap.put("MaxEngineSpeed","NA");}
					 eOERC1= resultSet.getString("EOERC1");if(eOERC1!=null) {tableMap.put("EOERC1",eOERC1);}else {tableMap.put("EOERC1","NA");}
					 eOERC2= resultSet.getString("EOERC2");if(eOERC2!=null) {tableMap.put("EOERC2",eOERC2);}else {tableMap.put("EOERC2","NA");}
					 eOERC3= resultSet.getString("EOERC3");if(eOERC3!=null) {tableMap.put("EOERC3",eOERC3);}else {tableMap.put("EOERC3","NA");}
					 eOERC4= resultSet.getString("EOERC4");if(eOERC4!=null) {tableMap.put("EOERC4",eOERC4);}else {tableMap.put("EOERC4","NA");}
					 eOERC5= resultSet.getString("EOERC5");if(eOERC5!=null) {tableMap.put("EOERC5",eOERC5);}else {tableMap.put("EOERC5","NA");}
					 eOERC6= resultSet.getString("EOERC6");if(eOERC6!=null) {tableMap.put("EOERC6",eOERC6);}else {tableMap.put("EOERC6","NA");}
					 eOERC7= resultSet.getString("EOERC7");if(eOERC7!=null) {tableMap.put("EOERC7",eOERC7);}else {tableMap.put("EOERC7","NA");}
					 eOERC8= resultSet.getString("EOERC8");if(eOERC8!=null) {tableMap.put("EOERC8",eOERC8);}else {tableMap.put("EOERC8","NA");}
					 eOERC9= resultSet.getString("EOERC9");if(eOERC9!=null) {tableMap.put("EOERC9",eOERC9);}else {tableMap.put("EOERC9","NA");}
					 eOERC10= resultSet.getString("EOERC10");if(eOERC10!=null) {tableMap.put("EOERC10",eOERC10);}else {tableMap.put("EOERC10","NA");}
					 eOERD1= resultSet.getString("EOERD1");if(eOERD1!=null) {tableMap.put("EOERD1",eOERD1);}else {tableMap.put("EOERD1","NA");}
					 eOERD2= resultSet.getString("EOERD2");if(eOERD2!=null) {tableMap.put("EOERD2",eOERD2);}else {tableMap.put("EOERD2","NA");}
					 eOERD3= resultSet.getString("EOERD3");if(eOERD3!=null) {tableMap.put("EOERD3",eOERD3);}else {tableMap.put("EOERD3","NA");}
					 eOERD4= resultSet.getString("EOERD4");if(eOERD4!=null) {tableMap.put("EOERD4",eOERD4);}else {tableMap.put("EOERD4","NA");}
					 eOERD5= resultSet.getString("EOERD5");if(eOERD5!=null) {tableMap.put("EOERD5",eOERD5);}else {tableMap.put("EOERD5","NA");}
					 eOERD6= resultSet.getString("EOERD6");if(eOERD6!=null) {tableMap.put("EOERD6",eOERD6);}else {tableMap.put("EOERD6","NA");}
					 eOERD7= resultSet.getString("EOERD7");if(eOERD7!=null) {tableMap.put("EOERD7",eOERD7);}else {tableMap.put("EOERD7","NA");}
					 eOERD8= resultSet.getString("EOERD8");if(eOERD8!=null) {tableMap.put("EOERD8",eOERD8);}else {tableMap.put("EOERD8","NA");}
					 eOERD9= resultSet.getString("EOERD9");if(eOERD9!=null) {tableMap.put("EOERD9",eOERD9);}else {tableMap.put("EOERD9","NA");}
					 eOERD10= resultSet.getString("EOERD10");if(eOERD10!=null) {tableMap.put("EOERD10",eOERD10);}else {tableMap.put("EOERD10","NA");}
					 engineThermalCycles = resultSet.getString("EngineThermalCycles");if( engineThermalCycles!=null) {tableMap.put("EngineThermalCycles",engineThermalCycles);}else {tableMap.put("EngineThermalCycles","NA");}
					 engineStartCount = resultSet.getString("EngineStartCount");if( engineStartCount!=null) {tableMap.put("EngineStartCount",engineStartCount);}else {tableMap.put("EngineStartCount","NA");}
					 eCUOntime= resultSet.getString("ECUOntime");if( eCUOntime!=null) {tableMap.put("ECUOntime",eCUOntime);}else {tableMap.put("ECUOntime","NA");}
					 ambient1= resultSet.getString("Ambient1");if( ambient1!=null) {tableMap.put("Ambient1", ambient1);}else {tableMap.put("Ambient1","NA");}
					 ambient2= resultSet.getString("Ambient2");if( ambient2!=null) {tableMap.put("Ambient2", ambient2);}else {tableMap.put("Ambient2","NA");}
					 ambient3= resultSet.getString("Ambient3");if( ambient3!=null) {tableMap.put("Ambient3", ambient3);}else {tableMap.put("Ambient3","NA");}
					 ambient4= resultSet.getString("Ambient4");if( ambient4!=null) {tableMap.put("Ambient4", ambient4);}else {tableMap.put("Ambient4","NA");}
					 ambient5= resultSet.getString("Ambient5");if( ambient5!=null) {tableMap.put("Ambient5", ambient5);}else {tableMap.put("Ambient5","NA");}
					 ambient6= resultSet.getString("Ambient6");if( ambient6!=null) {tableMap.put("Ambient6", ambient6);}else {tableMap.put("Ambient6","NA");}
					 ambient7= resultSet.getString("Ambient7");if( ambient7!=null) {tableMap.put("Ambient7", ambient7);}else {tableMap.put("Ambient7","NA");}
					 ambient8= resultSet.getString("Ambient8");if( ambient8!=null) {tableMap.put("Ambient8", ambient8);}else {tableMap.put("Ambient8","NA");}
					 ambient9= resultSet.getString("Ambient9");if( ambient9!=null) {tableMap.put("Ambient9", ambient9);}else {tableMap.put("Ambient9","NA");}
					 ambient10= resultSet.getString("Ambient10");if( ambient10!=null) {tableMap.put("Ambient10", ambient10);}else {tableMap.put("Ambient10","NA");}
					 ambient11= resultSet.getString("Ambient11");if( ambient11!=null) {tableMap.put("Ambient11", ambient11);}else {tableMap.put("Ambient11","NA");}
					 dOCOutlet1= resultSet.getString("DOCOutlet1");if(dOCOutlet1!=null) {tableMap.put("DOCOutlet1",dOCOutlet1);}else {tableMap.put("DOCOutlet1","NA");}
					 dOCOutlet2= resultSet.getString("DOCOutlet2");if(dOCOutlet2!=null) {tableMap.put("DOCOutlet2",dOCOutlet2);}else {tableMap.put("DOCOutlet2","NA");}
					 dOCOutlet3= resultSet.getString("DOCOutlet3");if(dOCOutlet3!=null) {tableMap.put("DOCOutlet3",dOCOutlet3);}else {tableMap.put("DOCOutlet3","NA");}
					 dOCOutlet4= resultSet.getString("DOCOutlet4");if(dOCOutlet4!=null) {tableMap.put("DOCOutlet4",dOCOutlet4);}else {tableMap.put("DOCOutlet4","NA");}
					 dOCOutlet5= resultSet.getString("DOCOutlet5");if(dOCOutlet5!=null) {tableMap.put("DOCOutlet5",dOCOutlet5);}else {tableMap.put("DOCOutlet5","NA");}
					 dOCOutlet6= resultSet.getString("DOCOutlet6");if(dOCOutlet6!=null) {tableMap.put("DOCOutlet6",dOCOutlet6);}else {tableMap.put("DOCOutlet6","NA");}
					 dOCOutlet7= resultSet.getString("DOCOutlet7");if(dOCOutlet7!=null) {tableMap.put("DOCOutlet7",dOCOutlet7);}else {tableMap.put("DOCOutlet7","NA");}
					 dOCOutlet8= resultSet.getString("DOCOutlet8");if(dOCOutlet8!=null) {tableMap.put("DOCOutlet8",dOCOutlet8);}else {tableMap.put("DOCOutlet8","NA");}
					 dOCOutlet9= resultSet.getString("DOCOutlet9");if(dOCOutlet9!=null) {tableMap.put("DOCOutlet9",dOCOutlet9);}else {tableMap.put("DOCOutlet9","NA");}
					 dOCOutlet10= resultSet.getString("DOCOutlet10");if(dOCOutlet10!=null) {tableMap.put("DOCOutlet10",dOCOutlet10);}else {tableMap.put("DOCOutlet10","NA");}
					 dOCOutlet11= resultSet.getString("DOCOutlet11");if(dOCOutlet11!=null) {tableMap.put("DOCOutlet11",dOCOutlet11);}else {tableMap.put("DOCOutlet11","NA");}
					 dOCOutlet12= resultSet.getString("DOCOutlet12");if(dOCOutlet12!=null) {tableMap.put("DOCOutlet12",dOCOutlet12);}else {tableMap.put("DOCOutlet12","NA");}
					 dOCOutlet13= resultSet.getString("DOCOutlet13");if(dOCOutlet13!=null) {tableMap.put("DOCOutlet13",dOCOutlet13);}else {tableMap.put("DOCOutlet13","NA");}
					 dOCOutlet14= resultSet.getString("DOCOutlet14");if(dOCOutlet14!=null) {tableMap.put("DOCOutlet14",dOCOutlet14);}else {tableMap.put("DOCOutlet14","NA");}
					 sulphurMonitor= resultSet.getString("SulphurMonitor");if( sulphurMonitor!=null) {tableMap.put("SulphurMonitor",sulphurMonitor);}else {tableMap.put("sulphurMonitor","NA");}
					 activeRestorations= resultSet.getString("ActiveRestorations");if( activeRestorations!=null) {tableMap.put("ActiveRestorations",activeRestorations);}else {tableMap.put("ActiveRestorations","NA");}
					 aftertreatment1= resultSet.getString("Aftertreatment1");if(aftertreatment1!=null) {tableMap.put("Aftertreatment1",aftertreatment1);}else {tableMap.put("Aftertreatment1","NA");}
					 aftertreatment2= resultSet.getString("Aftertreatment2");if(aftertreatment2!=null) {tableMap.put("Aftertreatment2",aftertreatment2);}else {tableMap.put("Aftertreatment2","NA");}
					 aftertreatment3= resultSet.getString("Aftertreatment3");if(aftertreatment3!=null) {tableMap.put("Aftertreatment3",aftertreatment3);}else {tableMap.put("Aftertreatment3","NA");}
					 aftertreatment4= resultSet.getString("Aftertreatment4");if(aftertreatment4!=null) {tableMap.put("Aftertreatment4",aftertreatment4);}else {tableMap.put("Aftertreatment4","NA");}
					 aftertreatment5= resultSet.getString("Aftertreatment5");if(aftertreatment5!=null) {tableMap.put("Aftertreatment5",aftertreatment5);}else {tableMap.put("Aftertreatment5","NA");}
					 aftertreatment6= resultSet.getString("Aftertreatment6");if(aftertreatment6!=null) {tableMap.put("Aftertreatment6",aftertreatment6);}else {tableMap.put("Aftertreatment6","NA");}
					 aftertreatment7= resultSet.getString("Aftertreatment7");if(aftertreatment7!=null) {tableMap.put("Aftertreatment7",aftertreatment7);}else {tableMap.put("Aftertreatment7","NA");}
					 aftertreatment8= resultSet.getString("Aftertreatment8");if(aftertreatment8!=null) {tableMap.put("Aftertreatment8",aftertreatment8);}else {tableMap.put("Aftertreatment8","NA");}
					 aftertreatment9= resultSet.getString("Aftertreatment9");if(aftertreatment9!=null) {tableMap.put("Aftertreatment9",aftertreatment9);}else {tableMap.put("Aftertreatment9","NA");}
					 aftertreatment10= resultSet.getString("Aftertreatment10");if(aftertreatment10!=null) {tableMap.put("Aftertreatment10",aftertreatment10);}else {tableMap.put("Aftertreatment10","NA");}
					 aftertreatment11= resultSet.getString("Aftertreatment11");if(aftertreatment11!=null) {tableMap.put("Aftertreatment11",aftertreatment11);}else {tableMap.put("Aftertreatment11","NA");}
					 aftertreatment12= resultSet.getString("Aftertreatment12");if(aftertreatment12!=null) {tableMap.put("Aftertreatment12",aftertreatment12);}else {tableMap.put("Aftertreatment12","NA");}
					 aftertreatment13= resultSet.getString("Aftertreatment13");if(aftertreatment13!=null) {tableMap.put("Aftertreatment13",aftertreatment13);}else {tableMap.put("Aftertreatment13","NA");}
					 aftertreatment14= resultSet.getString("Aftertreatment14");if(aftertreatment14!=null) {tableMap.put("Aftertreatment14",aftertreatment14);}else {tableMap.put("Aftertreatment14","NA");}
					 pCSDPRRng1= resultSet.getString("PCSDPRRng1");if(pCSDPRRng1!=null) {tableMap.put("PCSDPRRng1",pCSDPRRng1);}else {tableMap.put("PCSDPRRng1","NA");}
					 pCSDPRRng2= resultSet.getString("PCSDPRRng2");if(pCSDPRRng2!=null) {tableMap.put("PCSDPRRng2",pCSDPRRng2);}else {tableMap.put("PCSDPRRng2","NA");}
					 pCSDPRRng3= resultSet.getString("PCSDPRRng3");if(pCSDPRRng3!=null) {tableMap.put("PCSDPRRng3",pCSDPRRng3);}else {tableMap.put("PCSDPRRng3","NA");}
					 pCSDPRRng4= resultSet.getString("PCSDPRRng4");if(pCSDPRRng4!=null) {tableMap.put("PCSDPRRng4",pCSDPRRng4);}else {tableMap.put("PCSDPRRng4","NA");}
					 pCSDPRRng5= resultSet.getString("PCSDPRRng5");if(pCSDPRRng5!=null) {tableMap.put("PCSDPRRng5",pCSDPRRng5);}else {tableMap.put("PCSDPRRng5","NA");}
					 pCSDPRRng6= resultSet.getString("PCSDPRRng6");if(pCSDPRRng6!=null) {tableMap.put("PCSDPRRng6",pCSDPRRng6);}else {tableMap.put("PCSDPRRng6","NA");}
					 pCSDPRRng7= resultSet.getString("PCSDPRRng7");if(pCSDPRRng7!=null) {tableMap.put("PCSDPRRng7",pCSDPRRng7);}else {tableMap.put("PCSDPRRng7","NA");}
					 pCSDPRRng8= resultSet.getString("PCSDPRRng8");if(pCSDPRRng8!=null) {tableMap.put("PCSDPRRng8",pCSDPRRng8);}else {tableMap.put("PCSDPRRng8","NA");}
					 pCSDPRRng9= resultSet.getString("PCSDPRRng9");if(pCSDPRRng9!=null) {tableMap.put("PCSDPRRng9",pCSDPRRng9);}else {tableMap.put("PCSDPRRng9","NA");}
					 pCSDPRRng10= resultSet.getString("PCSDPRRng10");if(pCSDPRRng10!=null) {tableMap.put("PCSDPRRng10",pCSDPRRng10);}else {tableMap.put("PCSDPRRng10","NA");}
					 stationaryRestoration= resultSet.getString("StationaryRestoration");if( stationaryRestoration!=null) {tableMap.put("StationaryRestoration",stationaryRestoration);}else {tableMap.put("StationaryRestoration","NA");}
					 lastRestoration= resultSet.getString("LastRestoration");if( lastRestoration!=null) {tableMap.put("LastRestoration",lastRestoration);}else {tableMap.put("LastRestoration","NA");}
					 sootMonitor= resultSet.getString("SootMonitor");if( sootMonitor!=null) {tableMap.put("SootMonitor",sootMonitor);}else {tableMap.put("SootMonitor","NA");}
					 ashMonitor= resultSet.getString("AshMonitor");if( ashMonitor!=null) {tableMap.put("AshMonitor",ashMonitor);}else {tableMap.put("AshMonitor","NA");}
					 aftertreatmentTFU= resultSet.getString("AftertreatmentTFU");if( aftertreatmentTFU!=null) {tableMap.put("AftertreatmentTFU",aftertreatmentTFU);}else {tableMap.put("AftertreatmentTFU","NA");}
					 aftertreatmentTRT= resultSet.getString("AftertreatmentTRT");if( aftertreatmentTRT!=null) {tableMap.put("AftertreatmentTRT",aftertreatmentTRT);}else {tableMap.put("AftertreatmentTRT","NA");}
					 dPFOutlet1= resultSet.getString("DPFOutlet1");if(dPFOutlet1!=null) {tableMap.put("DPFOutlet1",dPFOutlet1);}else {tableMap.put("DPFOutlet1","NA");}
					 dPFOutlet2= resultSet.getString("DPFOutlet2");if(dPFOutlet2!=null) {tableMap.put("DPFOutlet2",dPFOutlet2);}else {tableMap.put("DPFOutlet2","NA");}
					 dPFOutlet3= resultSet.getString("DPFOutlet3");if(dPFOutlet3!=null) {tableMap.put("DPFOutlet3",dPFOutlet3);}else {tableMap.put("DPFOutlet3","NA");}
					 dPFOutlet4= resultSet.getString("DPFOutlet4");if(dPFOutlet4!=null) {tableMap.put("DPFOutlet4",dPFOutlet4);}else {tableMap.put("DPFOutlet4","NA");}
					 dPFOutlet5= resultSet.getString("DPFOutlet5");if(dPFOutlet5!=null) {tableMap.put("DPFOutlet5",dPFOutlet5);}else {tableMap.put("DPFOutlet5","NA");}
					 dPFOutlet6= resultSet.getString("DPFOutlet6");if(dPFOutlet6!=null) {tableMap.put("DPFOutlet6",dPFOutlet6);}else {tableMap.put("DPFOutlet6","NA");}
					 dPFOutlet7= resultSet.getString("DPFOutlet7");if(dPFOutlet7!=null) {tableMap.put("DPFOutlet7",dPFOutlet7);}else {tableMap.put("DPFOutlet7","NA");}
					 dPFOutlet8= resultSet.getString("DPFOutlet8");if(dPFOutlet8!=null) {tableMap.put("DPFOutlet8",dPFOutlet8);}else {tableMap.put("DPFOutlet8","NA");}
					 dPFOutlet9= resultSet.getString("DPFOutlet9");if(dPFOutlet9!=null) {tableMap.put("DPFOutlet9",dPFOutlet9);}else {tableMap.put("DPFOutlet9","NA");}
					 dPFOutlet10= resultSet.getString("DPFOutlet10");if(dPFOutlet10!=null) {tableMap.put("DPFOutlet10",dPFOutlet10);}else {tableMap.put("DPFOutlet10","NA");}
					 dPFOutlet11= resultSet.getString("DPFOutlet11");if(dPFOutlet11!=null) {tableMap.put("DPFOutlet11",dPFOutlet11);}else {tableMap.put("DPFOutlet11","NA");}
					 dPFOutlet12= resultSet.getString("DPFOutlet12");if(dPFOutlet12!=null) {tableMap.put("DPFOutlet12",dPFOutlet12);}else {tableMap.put("DPFOutlet12","NA");}
					 dPFOutlet13= resultSet.getString("DPFOutlet13");if(dPFOutlet13!=null) {tableMap.put("DPFOutlet13",dPFOutlet13);}else {tableMap.put("DPFOutlet13","NA");}
					 dPFOutlet14= resultSet.getString("DPFOutlet14");if(dPFOutlet14!=null) {tableMap.put("DPFOutlet14",dPFOutlet14);}else {tableMap.put("DPFOutlet14","NA");}
					 dieselSWITCH= resultSet.getString("DieselSWITCH");if( dieselSWITCH!=null) {tableMap.put("DieselSWITCH",dieselSWITCH);}else {tableMap.put("DieselSWITCH","NA");}
					 dieselTOOL= resultSet.getString("DieselTOOL");if( dieselTOOL!=null) {tableMap.put("DieselTOOL",dieselTOOL);}else {tableMap.put("DieselTOOL","NA");}
					 aftertreatmentStatus= resultSet.getString("AftertreatmentStatus");if( aftertreatmentStatus!=null) {tableMap.put("AftertreatmentStatus",aftertreatmentStatus);}else {tableMap.put("AftertreatmentStatus","NA");}
					 aftertreatmentIOL= resultSet.getString("AftertreatmentIOL");if( aftertreatmentIOL!=null) {tableMap.put("AftertreatmentIOL",aftertreatmentIOL);}else {tableMap.put("AftertreatmentIOL","NA");}
					 aftertreatmentISLR= resultSet.getString("AftertreatmentISLR");if( aftertreatmentISLR!=null) {tableMap.put("AftertreatmentISLR",aftertreatmentISLR);}else {tableMap.put("AftertreatmentISLR","NA");}
					 aftertreatmentTTIOL= resultSet.getString("AftertreatmentTTIOL");if( aftertreatmentTTIOL!=null) {tableMap.put("AftertreatmentTTIOL",aftertreatmentTTIOL);}else {tableMap.put("AftertreatmentTTIOL","NA");}
					 aftertreatmentLR= resultSet.getString("AftertreatmentLR");if( aftertreatmentLR!=null) {tableMap.put("AftertreatmentLR",aftertreatmentLR);}else {tableMap.put("AftertreatmentLR","NA");}
					 eSHTLC= resultSet.getString("ESHTLC");if( eSHTLC!=null) {tableMap.put("ESHTLC",eSHTLC);}else {tableMap.put("ESHTLC","NA");}
					 dPFSRNR= resultSet.getString("DPFSRNR");if( dPFSRNR!=null) {tableMap.put("DPFSRNR",dPFSRNR);}else {tableMap.put("DPFSRNR","NA");}
					 dPFSMRA= resultSet.getString("DPFSMRA");if( dPFSMRA!=null) {tableMap.put("DPFSMRA",dPFSMRA);}else {tableMap.put("DPFSMRA","NA");}
					 dPFSSRR= resultSet.getString("DPFSSRR");if( dPFSSRR!=null) {tableMap.put("DPFSSRR",dPFSSRR);}else {tableMap.put("DPFSSRR","NA");}
					 dPFSRS= resultSet.getString("DPFSRS");if( dPFSRS!=null) {tableMap.put("DPFSRS",dPFSRS);}else {tableMap.put("DPFSRS","NA");}
					 dPFSRPL2= resultSet.getString("DPFSRPL2");if( dPFSRPL2!=null) {tableMap.put("DPFSRPL2",dPFSRPL2);}else {tableMap.put("DPFSRPL2","NA");}
					 dPFSMRRL3= resultSet.getString("DPFSMRRL3");if( dPFSMRRL3!=null) {tableMap.put("DPFSMRRL3",dPFSMRRL3);}else {tableMap.put("DPFSMRRL3","NA");}
					 dPFSRA= resultSet.getString("DPFSRA");if( dPFSRA!=null) {tableMap.put("DPFSRA",dPFSRA);}else {tableMap.put("DPFSRA","NA");}
					 dPFSRC= resultSet.getString("DPFSRC");if( dPFSRC!=null) {tableMap.put("DPFSRC",dPFSRC);}else {tableMap.put("DPFSRC","NA");}
					 aARIDTISRABIBO = resultSet.getString("AARIDTISRABIBO");if( aARIDTISRABIBO!=null) {tableMap.put("AARIDTISRABIBO",aARIDTISRABIBO);}else {tableMap.put("AARIDTISRABIBO","NA");}
				
					mapList.add(tableMap);
					
				}
				
			}

		} catch (Exception ex) {

			ex.printStackTrace();
			fLogger.fatal("Exception occurred while fetching LogDownloadDataService details in LogDownloadDataServiceImpl: "
					+ ex.getMessage());

		} finally {
			if (resultSet != null)
				try {
					resultSet.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		  iLogger.info("--------getDownloadPTPacketReportImpl------------End---------------");
		return mapList;
	}

}
