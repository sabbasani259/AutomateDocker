/*
 *  CR416 : 20230713 : Prasanna lakshmi : TxnData Download for Log Packet on Eng server
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

public class LogDownloadDataServiceImpl {
	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;

	@SuppressWarnings("resource")
	public List<HashMap<String, String>> getLogDownloadDataReport(List<String> Serial_Number, String stratdate,String enddate) {
		String TAssetMonQuery = null;
		String startTAssetMonQuery = null;
		String endTAssetMonQuery = null;
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		List<HashMap<String, String>> mapList = new ArrayList<>();
		
	  ListToStringConversion str=new ListToStringConversion();
	   String SerialNumber =	str.getStringList(Serial_Number).toString();
	  iLogger.info("getLogDownloadDataReportImpl::start::SerialNumber::"+Serial_Number);
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

			String dynamicTable = new DateUtil().getDynamicTable("Monitor", TxnTS);

			if (dynamicTable != null) {
				endTAssetMonTable = dynamicTable;
             }

			Calendar cal = Calendar.getInstance();
			cal.setTime(TxnTS);
			//cal.add(Calendar.DAY_OF_YEAR, -1);
			String startTS = dateStr.format(cal.getTime());
			startTS = startTS + " 00:00:00";
			
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(TxnED);
			String endTS = dateStr.format(cal1.getTime());
			endTS=endTS+ " 23:59:59";
			Timestamp starttxnTimestamp = new Timestamp(cal.getTimeInMillis());
			iLogger.info(" startTS :"+startTS+" endTS :" +endTS);
			String startDynamicTable = new DateUtil().getDynamicTable("LogMonitor", starttxnTimestamp);

			if (startDynamicTable != null) {
				startTAssetMonTable = startDynamicTable;
			} else {
				startTAssetMonTable = endTAssetMonTable;
			}
            	
			if (startTAssetMonTable.equals(endTAssetMonTable)) {
				TAssetMonQuery = "select JSON_UNQUOTE(Serial_Number) as Vin ,JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) as MSG_ID,"+
						 "JSON_UNQUOTE(json_extract(TxnData,'$.FW_VER')) as LiveLinkfirmware, "+
						"CONVERT_TZ(Transaction_timestamp,'+00:00','+05:30') as Transaction_timestamp," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.CMH')) as Hours ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TSALI1')) as LowIdleTime," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TSAHI1')) as HighIdleTime," + 
						"JSON_EXTRACT(TxnData,'$.EVT_ESBAESB3')/60.0  as Enginespeedband1,"+
						"JSON_EXTRACT(TxnData,'$.EVT_AESB2')/60.0  as Enginespeedband2,"+
					    "JSON_EXTRACT(TxnData,'$.EVT_AESB3')/60.0 as Enginespeedband3,"+
					    "JSON_EXTRACT(TxnData,'$.EVT_AESB4')/60.0  as Enginespeedband4,"+
						"JSON_EXTRACT(TxnData,'$.EVT_AESB5')/60.0 as Enginespeedband5,"+
					    "JSON_EXTRACT(TxnData,'$.EVT_AESB6')/60.0 as Enginespeedband6, "+
						"JSON_EXTRACT(TxnData,'$.EVT_AESB7')/60.0  as Enginespeedband7,"+
						"JSON_EXTRACT(TxnData,'$.EVT_AESB8')/60.0  as Enginespeedband8,"+
						"json_extract(TxnData,'$.EVT_TSALJ1')/3600.0 as LoadingJob," + 
						"json_extract(TxnData,'$.EVT_TSIRJ1')/3600.0 as RoadingJob ," + 
						"json_extract(TxnData,'$.EVT_TSAO2')/3600.0 as AttachmentJob," + 
						"json_extract(TxnData,'$.AHIEJ')/3600.0 as ExcavationJob," + 
						"json_extract(TxnData,'$.EVT_TSIEIEM1')/3600.0 as ExacavationEconomyMode," + 
						"json_extract(TxnData,'$.EVT_TSIEISM1')/3600.0 as ExcavationStandardMode," + 
						"json_extract(TxnData,'$.EVT_TSIEIPM1')/3600.0 as ExcavationPlusMode," + 
						"json_extract(TxnData,'$.EVT_ADTIR2')/1000.0 as RoadingDistance," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.FUEL_PERCT')) as FuelLevel," +
						"json_extract(TxnData,'$.EVT_TSIG1')/3600.0 as Gear1," + 
						"json_extract(TxnData,'$.EVT_TSIG2')/3600.0 as Gear2," + 
						"json_extract(TxnData,'$.EVT_TSIG3')/3600.0 as Gear3," + 
						"json_extract(TxnData,'$.EVT_TSIG4')/3600.0 as Gear4," + 
						"json_extract(TxnData,'$.EVT_TSIGN')/3600.0 as GearNeutral ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TSIFD1')) as ForwardDirection," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TSIRD1')) as ReverseDirection," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TSIND1')) as NeutralDirection," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_NOAIE2')) as NoOfAutoIdleEvents," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_NOASE2')) as NoOfAutoShutdownEvents," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_NOKE2')) as NoOfKickdownEvents," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUINLI1')) as FuelUsedinLowIDLE," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUINHI1')) as FuelUsedinHighIDLE," + 
						"JSON_EXTRACT(TxnData,'$.EVT_FUAEPM1') + JSON_EXTRACT(TxnData,'$.EVT_FUAESM2') + JSON_EXTRACT(TxnData,'$.EVT_FUAESE1')  as FuelUsedatExcavationMode," +
						"JSON_EXTRACT(TxnData,'$.EVT_FUINHI1') + JSON_EXTRACT(TxnData,'$.EVT_FUINLI1') as FuelusedinIDLEMODE," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUAESE1')) as FuelUsedinExcavationEcoMode ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUAESM2')) as FuelUsedatExcavationStandardMode," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUAEPM1')) as FuelUsedatExcavationPlusMode," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUALM1')) as FuelUsedatLoadingMode," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUARM1')) as FuelUsedatRoadingMode,"+ 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TFU7')) as TFU,"+
					    "JSON_UNQUOTE(json_extract(TxnData,'$.Total_Fuel_Used')) as TotalfuelUsed,"+
						"json_extract(TxnData,'$.EVT_AUPB1')/3600.0 as PumpUpstreamPressureBand1," + 
						"json_extract(TxnData,'$.EVT_AUPB2')/3600.0 as PumpUpstreamPressureBand2," + 
						"json_extract(TxnData,'$.EVT_AUPB3')/3600.0 as PumpUpstreamPressureBand3," + 
						"json_extract(TxnData,'$.EVT_AUPB4')/3600.0 as PumpUpstreamPressureBand4," + 
						"json_extract(TxnData,'$.EVT_AUPB5')/3600.0 as PumpUpstreamPressureBand5," + 
						"json_extract(TxnData,'$.EVT_AUPB6')/3600.0 as PumpUpstreamPressureBand6," + 
						"json_extract(TxnData,'$.EVT_AUPB7')/3600.0 as PumpUpstreamPressureBand7," + 
						"json_extract(TxnData,'$.EVT_AUPB8')/3600.0 as PumpUpstreamPressureBand8," + 
						"json_extract(TxnData,'$.EVT_ADPB1')/3600.0 as PumpDownstreamPressureBand1," + 
						"json_extract(TxnData,'$.EVT_ADPB2')/3600.0 as PumpDownstreamPressureBand2 ," + 
						"json_extract(TxnData,'$.EVT_ADPB3')/3600.0 as PumpDownstreamPressureBand3," + 
						"json_extract(TxnData,'$.EVT_ADPB4')/3600.0 as PumpDownstreamPressureBand4," + 
						"json_extract(TxnData,'$.EVT_ADPB5')/3600.0 as PumpDownstreamPressureBand5," + 
						"json_extract(TxnData,'$.EVT_ADPB6')/3600.0 as PumpDownstreamPressureBand6," + 
						"json_extract(TxnData,'$.EVT_ADPB7')/3600.0 as PumpDownstreamPressureBand7," + 
						"json_extract(TxnData,'$.EVT_ADPB8')/3600.0 as PumpDownstreamPressureBand8 ," + 
						"json_extract(TxnData,'$.EVT_APDB1')/3600.0 as PumpDisplacementBand1," + 
						"json_extract(TxnData,'$.EVT_APDB2')/3600.0 as PumpDisplacementBand2," + 
						"json_extract(TxnData,'$.EVT_APDB3')/3600.0 as PumpDisplacementBand3," + 
						"json_extract(TxnData,'$.EVT_APDB4')/3600.0 as PumpDisplacementBand4," + 
						"json_extract(TxnData,'$.EVT_APDB5')/3600.0 as PumpDisplacementBand5," + 
						"json_extract(TxnData,'$.EVT_APDB6')/3600.0 as PumpDisplacementBand6," + 
						"json_extract(TxnData,'$.EVT_APDB7')/3600.0 as PumpDisplacementBand7," + 
						"json_extract(TxnData,'$.EVT_APDB8')/3600.0 as PumpDisplacementBand8 " + 
						"from  "+startTAssetMonTable + " where JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) != 008  and"
								+ " ( JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 062 or "
								+ "JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 063 or"
								+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 064 or "
								+ "JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 065 or "
								+ "JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 066 or"
								+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 067) "
								+ "and serial_number in ("+SerialNumber+") and Transaction_Timestamp >= '"+startTS+"'"
						+ "and Transaction_Timestamp <= '"+endTS+"'";
			} else {
				startTAssetMonQuery ="select JSON_UNQUOTE(Serial_Number) as Vin ,JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) as MSG_ID," +
			            "JSON_UNQUOTE(json_extract(TxnData,'$.FW_VER')) as LiveLinkfirmware,"+
						"JSON_UNQUOTE(json_extract(TxnData,'$.CMH')) as Hours ,"+
						" CONVERT_TZ(Transaction_timestamp,'+00:00','+05:30') as Transaction_timestamp," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TSALI1')) as LowIdleTime," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TSAHI1')) as HighIdleTime," + 
						"JSON_EXTRACT(TxnData,'$.EVT_ESBAESB3')/60.0  as Enginespeedband1,"+
						"JSON_EXTRACT(TxnData,'$.EVT_AESB2')/60.0  as Enginespeedband2,"+
					    "JSON_EXTRACT(TxnData,'$.EVT_AESB3')/60.0 as Enginespeedband3,"+
					    "JSON_EXTRACT(TxnData,'$.EVT_AESB4')/60.0  as Enginespeedband4,"+
						"JSON_EXTRACT(TxnData,'$.EVT_AESB5')/60.0 as Enginespeedband5,"+
					    "JSON_EXTRACT(TxnData,'$.EVT_AESB6')/60.0 as Enginespeedband6, "+
						"JSON_EXTRACT(TxnData,'$.EVT_AESB7')/60.0  as Enginespeedband7,"+  
						"JSON_EXTRACT(TxnData,'$.EVT_AESB8')/60.0  as Enginespeedband8,"+
						"json_extract(TxnData,'$.EVT_TSALJ1')/3600.0 as LoadingJob," + 
						"json_extract(TxnData,'$.EVT_TSIRJ1')/3600.0 as RoadingJob ," + 
						"json_extract(TxnData,'$.EVT_TSAO2')/3600.0 as AttachmentJob," + 
						"json_extract(TxnData,'$.AHIEJ')/3600.0 as ExcavationJob," + 
						"json_extract(TxnData,'$.EVT_TSIEIEM1')/3600.0 as ExacavationEconomyMode," + 
						"json_extract(TxnData,'$.EVT_TSIEISM1')/3600.0 as ExcavationStandardMode," + 
						"json_extract(TxnData,'$.EVT_TSIEIPM1')/3600.0 as ExcavationPlusMode," + 
						"json_extract(TxnData,'$.EVT_ADTIR2')/1000.0 as RoadingDistance," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.FUEL_PERCT')) as FuelLevel," +
						"json_extract(TxnData,'$.EVT_TSIG1')/3600.0 as Gear1," + 
						"json_extract(TxnData,'$.EVT_TSIG2')/3600.0 as Gear2," + 
						"json_extract(TxnData,'$.EVT_TSIG3')/3600.0 as Gear3," + 
						"json_extract(TxnData,'$.EVT_TSIG4')/3600.0 as Gear4," + 
						"json_extract(TxnData,'$.EVT_TSIGN')/3600.0 as GearNeutral ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TSIFD1')) as ForwardDirection," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TSIRD1')) as ReverseDirection," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TSIND1')) as NeutralDirection," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_NOAIE2')) as NoOfAutoIdleEvents," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_NOASE2')) as NoOfAutoShutdownEvents," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_NOKE2')) as NoOfKickdownEvents," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUINLI1')) as FuelUsedinLowIDLE," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUINHI1')) as FuelUsedinHighIDLE," + 
						"JSON_EXTRACT(TxnData,'$.EVT_FUAEPM1') + JSON_EXTRACT(TxnData,'$.EVT_FUAESM2') + JSON_EXTRACT(TxnData,'$.EVT_FUAESE1')  as FuelUsedatExcavationMode," +
						"JSON_EXTRACT(TxnData,'$.EVT_FUINHI1') + JSON_EXTRACT(TxnData,'$.EVT_FUINLI1') as FuelusedinIDLEMODE," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUAESE1')) as FuelUsedinExcavationEcoMode ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUAESM2')) as FuelUsedatExcavationStandardMode," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUAEPM1')) as FuelUsedatExcavationPlusMode," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUALM1')) as FuelUsedatLoadingMode," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUARM1')) as FuelUsedatRoadingMode," +  
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TFU7')) as TFU,"+
					    "JSON_UNQUOTE(json_extract(TxnData,'$.Total_Fuel_Used')) as TotalfuelUsed,"+ 
						"json_extract(TxnData,'$.EVT_AUPB1')/3600.0 as PumpUpstreamPressureBand1," + 
						"json_extract(TxnData,'$.EVT_AUPB2')/3600.0 as PumpUpstreamPressureBand2," + 
						"json_extract(TxnData,'$.EVT_AUPB3')/3600.0 as PumpUpstreamPressureBand3," + 
						"json_extract(TxnData,'$.EVT_AUPB4')/3600.0 as PumpUpstreamPressureBand4," + 
						"json_extract(TxnData,'$.EVT_AUPB5')/3600.0 as PumpUpstreamPressureBand5," + 
						"json_extract(TxnData,'$.EVT_AUPB6')/3600.0 as PumpUpstreamPressureBand6," + 
						"json_extract(TxnData,'$.EVT_AUPB7')/3600.0 as PumpUpstreamPressureBand7," + 
						"json_extract(TxnData,'$.EVT_AUPB8')/3600.0 as PumpUpstreamPressureBand8," + 
						"json_extract(TxnData,'$.EVT_ADPB1')/3600.0 as PumpDownstreamPressureBand1," + 
						"json_extract(TxnData,'$.EVT_ADPB2')/3600.0 as PumpDownstreamPressureBand2 ," + 
						"json_extract(TxnData,'$.EVT_ADPB3')/3600.0 as PumpDownstreamPressureBand3," + 
						"json_extract(TxnData,'$.EVT_ADPB4')/3600.0 as PumpDownstreamPressureBand4," + 
						"json_extract(TxnData,'$.EVT_ADPB5')/3600.0 as PumpDownstreamPressureBand5," + 
						"json_extract(TxnData,'$.EVT_ADPB6')/3600.0 as PumpDownstreamPressureBand6," + 
						"json_extract(TxnData,'$.EVT_ADPB7')/3600.0 as PumpDownstreamPressureBand7," + 
						"json_extract(TxnData,'$.EVT_ADPB8')/3600.0 as PumpDownstreamPressureBand8 ," + 
						"json_extract(TxnData,'$.EVT_APDB1')/3600.0 as PumpDisplacementBand1," + 
						"json_extract(TxnData,'$.EVT_APDB2')/3600.0 as PumpDisplacementBand2," + 
						"json_extract(TxnData,'$.EVT_APDB3')/3600.0 as PumpDisplacementBand3," + 
						"json_extract(TxnData,'$.EVT_APDB4')/3600.0 as PumpDisplacementBand4," + 
						"json_extract(TxnData,'$.EVT_APDB5')/3600.0 as PumpDisplacementBand5," + 
						"json_extract(TxnData,'$.EVT_APDB6')/3600.0 as PumpDisplacementBand6," + 
						"json_extract(TxnData,'$.EVT_APDB7')/3600.0 as PumpDisplacementBand7," + 
						"json_extract(TxnData,'$.EVT_APDB8')/3600.0 as PumpDisplacementBand8 " + 
						"from  "+startTAssetMonTable+ " where JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) != 008  and "
								+ "( JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 062 or"
								+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 063 or"
								+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 064 or"
								+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 065 or"
								+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 066 or"
								+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 067) "
								+ "and serial_number in ("+SerialNumber+") and Transaction_Timestamp >= '"+startTS+"'"
								+ "and Transaction_Timestamp <= '"+endTS+"' ";
						
				endTAssetMonQuery = "select JSON_UNQUOTE(Serial_Number) as Vin ,JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) as MSG_ID," +
				        "JSON_UNQUOTE(json_extract(TxnData,'$.FW_VER')) as LiveLinkfirmware,"+
						"JSON_UNQUOTE(json_extract(TxnData,'$.CMH')) as Hours ,"+
						" CONVERT_TZ(Transaction_timestamp,'+00:00','+05:30') as Transaction_timestamp," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TSALI1')/3600.0) as LowIdleTime," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TSAHI1')/3600.0) as HighIdleTime," + 
						"JSON_EXTRACT(TxnData,'$.EVT_ESBAESB3')/60.0  as Enginespeedband1,"+
						"JSON_EXTRACT(TxnData,'$.EVT_AESB2')/60.0  as Enginespeedband2,"+
					    "JSON_EXTRACT(TxnData,'$.EVT_AESB3')/60.0 as Enginespeedband3,"+
					    "JSON_EXTRACT(TxnData,'$.EVT_AESB4')/60.0  as Enginespeedband4,"+
						"JSON_EXTRACT(TxnData,'$.EVT_AESB5')/60.0 as Enginespeedband5,"+
					    "JSON_EXTRACT(TxnData,'$.EVT_AESB6')/60.0 as Enginespeedband6, "+
						"JSON_EXTRACT(TxnData,'$.EVT_AESB7')/60.0  as Enginespeedband7,"+
						"JSON_EXTRACT(TxnData,'$.EVT_AESB8')/60.0  as Enginespeedband8,"+
						"json_extract(TxnData,'$.EVT_TSALJ1')/3600.0 as LoadingJob," + 
						"json_extract(TxnData,'$.EVT_TSIRJ1')/3600.0 as RoadingJob ," + 
						"json_extract(TxnData,'$.EVT_TSAO2')/3600.0 as AttachmentJob," + 
						"json_extract(TxnData,'$.AHIEJ')/3600.0 as ExcavationJob," + 
						"json_extract(TxnData,'$.EVT_TSIEIEM1')/3600.0 as ExacavationEconomyMode," + 
						"json_extract(TxnData,'$.EVT_TSIEISM1')/3600.0 as ExcavationStandardMode," + 
						"json_extract(TxnData,'$.EVT_TSIEIPM1')/3600.0 as ExcavationPlusMode," + 
						"json_extract(TxnData,'$.EVT_ADTIR2')/1000.0 as RoadingDistance," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.FUEL_PERCT')) as FuelLevel," +
						"json_extract(TxnData,'$.EVT_TSIG1')/3600.0 as Gear1," + 
						"json_extract(TxnData,'$.EVT_TSIG2')/3600.0 as Gear2," + 
						"json_extract(TxnData,'$.EVT_TSIG3')/3600.0 as Gear3," + 
						"json_extract(TxnData,'$.EVT_TSIG4')/3600.0 as Gear4," + 
						"json_extract(TxnData,'$.EVT_TSIGN')/3600.0 as GearNeutral," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TSIFD1')) as ForwardDirection," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TSIRD1')) as ReverseDirection," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TSIND1')) as NeutralDirection," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_NOAIE2')) as NoOfAutoIdleEvents," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_NOASE2')) as NoOfAutoShutdownEvents," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_NOKE2')) as NoOfKickdownEvents," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUINLI1')) as FuelUsedinLowIDLE," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUINHI1')) as FuelUsedinHighIDLE," + 
						"JSON_EXTRACT(TxnData,'$.EVT_FUAEPM1') + JSON_EXTRACT(TxnData,'$.EVT_FUAESM2') + JSON_EXTRACT(TxnData,'$.EVT_FUAESE1')  as FuelUsedatExcavationMode," + 
						"JSON_EXTRACT(TxnData,'$.EVT_FUINHI1') + JSON_EXTRACT(TxnData,'$.EVT_FUINLI1') as FuelusedinIDLEMODE," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUAESE1')) as FuelUsedinExcavationEcoMode ," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUAESM2')) as FuelUsedatExcavationStandardMode," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUAEPM1')) as FuelUsedatExcavationPlusMode," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUALM1')) as FuelUsedatLoadingMode," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_FUARM1')) as FuelUsedatRoadingMode," + 
						"JSON_UNQUOTE(json_extract(TxnData,'$.EVT_TFU7')) as TFU,"+
					    "JSON_UNQUOTE(json_extract(TxnData,'$.Total_Fuel_Used')) as TotalfuelUsed,"+
					 	"json_extract(TxnData,'$.EVT_AUPB1')/3600.0 as PumpUpstreamPressureBand1," + 
						"json_extract(TxnData,'$.EVT_AUPB2')/3600.0 as PumpUpstreamPressureBand2," + 
						"json_extract(TxnData,'$.EVT_AUPB3')/3600.0 as PumpUpstreamPressureBand3," + 
						"json_extract(TxnData,'$.EVT_AUPB4')/3600.0 as PumpUpstreamPressureBand4," + 
						"json_extract(TxnData,'$.EVT_AUPB5')/3600.0 as PumpUpstreamPressureBand5," + 
						"json_extract(TxnData,'$.EVT_AUPB6')/3600.0 as PumpUpstreamPressureBand6," + 
						"json_extract(TxnData,'$.EVT_AUPB7')/3600.0 as PumpUpstreamPressureBand7," + 
						"json_extract(TxnData,'$.EVT_AUPB8')/3600.0 as PumpUpstreamPressureBand8," + 
						"json_extract(TxnData,'$.EVT_ADPB1')/3600.0 as PumpDownstreamPressureBand1," + 
						"json_extract(TxnData,'$.EVT_ADPB2')/3600.0 as PumpDownstreamPressureBand2 ," + 
						"json_extract(TxnData,'$.EVT_ADPB3')/3600.0 as PumpDownstreamPressureBand3," + 
						"json_extract(TxnData,'$.EVT_ADPB4')/3600.0 as PumpDownstreamPressureBand4," + 
						"json_extract(TxnData,'$.EVT_ADPB5')/3600.0 as PumpDownstreamPressureBand5," + 
						"json_extract(TxnData,'$.EVT_ADPB6')/3600.0 as PumpDownstreamPressureBand6," + 
						"json_extract(TxnData,'$.EVT_ADPB7')/3600.0 as PumpDownstreamPressureBand7," + 
						"json_extract(TxnData,'$.EVT_ADPB8')/3600.0 as PumpDownstreamPressureBand8 ," + 
						"json_extract(TxnData,'$.EVT_APDB1')/3600.0 as PumpDisplacementBand1," + 
						"json_extract(TxnData,'$.EVT_APDB2')/3600.0 as PumpDisplacementBand2," + 
						"json_extract(TxnData,'$.EVT_APDB3')/3600.0 as PumpDisplacementBand3," + 
						"json_extract(TxnData,'$.EVT_APDB4')/3600.0 as PumpDisplacementBand4," + 
						"json_extract(TxnData,'$.EVT_APDB5')/3600.0 as PumpDisplacementBand5," + 
						"json_extract(TxnData,'$.EVT_APDB6')/3600.0 as PumpDisplacementBand6," + 
						"json_extract(TxnData,'$.EVT_APDB7')/3600.0 as PumpDisplacementBand7," + 
						"json_extract(TxnData,'$.EVT_APDB8')/3600.0 as PumpDisplacementBand8 " + 
						"from  "+endTAssetMonTable+" where JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) != 008  and "
								+ "( JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 062 or"
								+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 063 or"
								+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 064 or"
								+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 065 or"
								+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 066 or"
								+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.MSG_ID')) = 067) "
								+ "and serial_number in ("+SerialNumber+") and Transaction_Timestamp >= '"+startTS+"' "
								+ " and Transaction_Timestamp <= '"+endTS+"'" ;

			}
			iLogger.info("getLogDownloadDataReportImpl TAssetMonQuery : "+TAssetMonQuery);
			iLogger.info("getLogDownloadDataReportImpl startTAssetMonQuery : "+startTAssetMonQuery);
			iLogger.info("getLogDownloadDataReportImpl endTAssetMonQuery : "+endTAssetMonQuery);

			ConnectMySQL connectionObj = new ConnectMySQL();
			con = connectionObj.getDatalakeConnection_3309();
			statement = con.createStatement();
			HashMap<String, String> tableMap = null;
			String llFirmware=null;
			String hours = null;
			String txnTs=null;
			String lowIdleTime = null;
			String highIdleTime = null;
			String enginespeedband1 = null;
			String enginespeedband2 = null;
			String enginespeedband3 = null;
			String enginespeedband4=null;
			String enginespeedband5=null;
			String enginespeedband6=null;
			String enginespeedband7=null;
			String enginespeedband8 = null;
			String loadingJob=null;
			String roadingJob=null;
			String attachmentJob=null;
			String excavationJob=null;
			String exacavationEconomyMode=null;
			String excavationStandardMode=null;
			String excavationPlusMode=null;
			String roadingDistance=null;
			String fuelLevel=null;
			String gear1=null;
			String gear2=null;
			String gear3=null;
			String gear4=null;
			String gearNeutral=null;
			String forwardDirection=null;
			String reverseDirection=null;
			String neutralDirection=null;
			String noOfAutoIdleEvents=null;
			String noOfAutoShutdownEvents=null;
			String noOfKickdownEvents=null;
			String fuelUsedinLowIDLE=null;
			String  fuelUsedinHighIDLE=null;
			String  fuelUsedatExcavationMode=null;
			String  fuelusedinIDLEMODE=null;
			String  fuelUsedinExcavationEcoMode=null;
			String  fuelUsedatExcavationStandardMode=null;
			String  fuelUsedatExcavationPlusMode=null;
			String  fuelUsedatLoadingMode=null;
			String  fuelUsedatRoadingMode=null;
			String  totalFuelUsed=null;
			String  pumpUpstreamPressureBand1=null;
			String  pumpUpstreamPressureBand2=null;
			String  pumpUpstreamPressureBand3=null;
			String  pumpUpstreamPressureBand4=null;
			String  pumpUpstreamPressureBand5=null;
			String  pumpUpstreamPressureBand6=null;
			String  pumpUpstreamPressureBand7=null;
			String  pumpUpstreamPressureBand8=null;
			String  pumpDownstreamPressureBand1=null;
			String  pumpDownstreamPressureBand2=null;
			String  pumpDownstreamPressureBand3=null;
			String  pumpDownstreamPressureBand4=null;
			String  pumpDownstreamPressureBand5=null;
			String  pumpDownstreamPressureBand6=null;
			String  pumpDownstreamPressureBand7=null;
			String  pumpDownstreamPressureBand8=null;
			String  pumpDisplacementBand1=null;
			String  pumpDisplacementBand2=null;
			String  pumpDisplacementBand3=null;
			String  pumpDisplacementBand4=null;
			String  pumpDisplacementBand5=null;
			String  pumpDisplacementBand6=null;
			String  pumpDisplacementBand7=null;
			String  pumpDisplacementBand8=null;
			String tfu=null;
			String msg_id=null;
			String tDate=null;
			if(TAssetMonQuery !=null) {
				resultSet = statement.executeQuery(TAssetMonQuery);
				while(resultSet.next())
				{
					
					tableMap = new HashMap<String,String>();
					tableMap.put("Vin",resultSet.getString("Vin"));
					tDate=resultSet.getString("Transaction_timestamp").substring(0,19); 
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
					txnTs = LocalDateTime.parse(tDate, dtf).format(dtf2);
					if(tDate !=null) {
						tableMap.put("Transaction_timestamp",txnTs);
					}else {
						tableMap.put("Transaction_timestamp","NA");
					}
				    msg_id=resultSet.getString("MSG_ID");//063,062 TFU //064,065,066,067 Totalfuelused
				    tfu = resultSet.getString("TFU");
				    totalFuelUsed = resultSet.getString("TotalFuelUsed");
				    
				    if(msg_id!=null) {
					if(msg_id.equals("062") || msg_id.equals("063"))
					{
						if(tfu!=null ) {
						tableMap.put("TotalFuelUsed",tfu);
						}else {
						tableMap.put("TotalFuelUsed","NA");
						}
					}
					if(msg_id.equals("064")|| msg_id.equals("065")||msg_id.equals("066")||msg_id.equals("067"))
					{
						if( totalFuelUsed!=null) {
						tableMap.put("TotalFuelUsed",totalFuelUsed);
						}else {
						tableMap.put("TotalFuelUsed","NA");
						}	
					}
				    }
					llFirmware=resultSet.getString("LiveLinkfirmware");
					if(llFirmware !=null) {
					tableMap.put("LiveLinkfirmware",llFirmware);
					}else {
						tableMap.put("LiveLinkfirmware","NA");
					}
					fuelLevel= resultSet.getString("FuelLevel");
					if(fuelLevel !=null) {
						tableMap.put("FuelLevel",fuelLevel);
					}else {
						tableMap.put("FuelLevel","NA");
					}
					hours = resultSet.getString("Hours");
					if(hours !=null) {
						tableMap.put("Hours",hours);
					}else {
						tableMap.put("Hours","NA");
					}
							
					lowIdleTime = resultSet.getString("LowIdleTime");
					if(lowIdleTime !=null) {
						tableMap.put("LowIdleTime",lowIdleTime);
					}else {
						tableMap.put("LowIdleTime","NA");
					}
					
					highIdleTime = resultSet.getString("HighIdleTime");
					if(highIdleTime !=null) {
						tableMap.put("HighIdleTime",highIdleTime);
					}else {
						tableMap.put("HighIdleTime","NA");
					}
					
					enginespeedband1 = resultSet.getString("Enginespeedband1");
					if(enginespeedband1 !=null) {
						tableMap.put("Enginespeedband1",enginespeedband1);
					}else {
						tableMap.put("Enginespeedband1","NA");
					}
					enginespeedband2 = resultSet.getString("Enginespeedband2");
					if(enginespeedband2 !=null) {
						tableMap.put("Enginespeedband2",enginespeedband2);
					}else {
						tableMap.put("Enginespeedband2","NA");
					}
					
					enginespeedband3 = resultSet.getString("Enginespeedband3");
					if(enginespeedband3 !=null) {
						tableMap.put("Enginespeedband3",enginespeedband3);
					}else {
						tableMap.put("Enginespeedband3","NA");
					}
					
					enginespeedband4 = resultSet.getString("Enginespeedband4");
					if(enginespeedband4 !=null) {
						tableMap.put("Enginespeedband4",enginespeedband4);
					}else {
						tableMap.put("Enginespeedband4","NA");
					}
					enginespeedband5 = resultSet.getString("Enginespeedband5");
					if(enginespeedband5 !=null) {
						tableMap.put("Enginespeedband5",enginespeedband5);
					}else {
						tableMap.put("Enginespeedband5","NA");
					}
					
					enginespeedband6 = resultSet.getString("Enginespeedband6");
					if(enginespeedband6 !=null) {
						tableMap.put("Enginespeedband6",enginespeedband6);
					}else {
						tableMap.put("Enginespeedband6","NA");
					}
					enginespeedband7 = resultSet.getString("Enginespeedband7");
					if(enginespeedband7 !=null) {
						tableMap.put("Enginespeedband7",enginespeedband7);
					}else {
						tableMap.put("Enginespeedband7","NA");
					}
					enginespeedband8 = resultSet.getString("Enginespeedband8");
					if(enginespeedband8 !=null) {
						tableMap.put("Enginespeedband8",enginespeedband8);
					}else {
						tableMap.put("Enginespeedband8","NA");
					}
					
					loadingJob = resultSet.getString("LoadingJob");
					if(loadingJob !=null) {
						tableMap.put("LoadingJob",loadingJob);
					}else {
						tableMap.put("LoadingJob","NA");
					}
					
					roadingJob = resultSet.getString("RoadingJob");
					if(roadingJob !=null) {
						tableMap.put("RoadingJob",roadingJob);
					}else {
						tableMap.put("RoadingJob","NA");
					}
					attachmentJob = resultSet.getString("AttachmentJob");
					if(attachmentJob !=null) {
						tableMap.put("AttachmentJob",attachmentJob);
					}else {
						tableMap.put("AttachmentJob","NA");
					}
					excavationJob = resultSet.getString("ExcavationJob");
					if(excavationJob !=null) {
						tableMap.put("ExcavationJob",excavationJob);
					}else {
						tableMap.put("ExcavationJob","NA");
					}
					exacavationEconomyMode = resultSet.getString("ExacavationEconomyMode");
					if(exacavationEconomyMode !=null) {
						tableMap.put("ExacavationEconomyMode",exacavationEconomyMode);
					}else {
						tableMap.put("ExacavationEconomyMode","NA");
					}
					excavationStandardMode = resultSet.getString("ExcavationStandardMode");
					if(excavationStandardMode !=null) {
						tableMap.put("ExcavationStandardMode",excavationStandardMode);
					}else {
						tableMap.put("ExcavationStandardMode","NA");
					}
					excavationPlusMode = resultSet.getString("ExcavationPlusMode");
					if(excavationPlusMode!=null) {
						tableMap.put("ExcavationPlusMode",excavationPlusMode);
					}else {
						tableMap.put("ExcavationPlusMode","NA");
					} 
					roadingDistance = resultSet.getString("RoadingDistance");
					if(roadingDistance!=null) {
						tableMap.put("RoadingDistance",roadingDistance);
					}else {
						tableMap.put("RoadingDistance","NA");
					}
					
					gear1 = resultSet.getString("Gear1");
					if(gear1!=null) {
						tableMap.put("Gear1",gear1);
					}else {
						tableMap.put("Gear1","NA");
					}
					gear2 = resultSet.getString("Gear2");
					if(gear2!=null) {
						tableMap.put("Gear2",gear2);
					}else {
						tableMap.put("Gear2","NA");
					}
					gear3 = resultSet.getString("Gear3");
					if(gear3!=null) {
						tableMap.put("Gear3",gear3);
					}else {
						tableMap.put("Gear3","NA");
					}
					gear4 = resultSet.getString("Gear4");
					if(gear4!=null) {
						tableMap.put("Gear4",gear4);
					}else {
						tableMap.put("Gear4","NA");
					}
					gearNeutral = resultSet.getString("GearNeutral");
					if(gearNeutral!=null) {
						tableMap.put("GearNeutral",gearNeutral);
					}else {
						tableMap.put("GearNeutral","NA");
					}
					forwardDirection = resultSet.getString("ForwardDirection");
					if(forwardDirection!=null) {
					tableMap.put("ForwardDirection",forwardDirection);
					}else {
					tableMap.put("ForwardDirection","NA");
					}
					reverseDirection = resultSet.getString("ReverseDirection");
					if(reverseDirection!=null) {
					tableMap.put("ReverseDirection",reverseDirection);
					}else {
					tableMap.put("ReverseDirection","NA");
					}
					neutralDirection = resultSet.getString("NeutralDirection");
					if(neutralDirection!=null) {
					tableMap.put("NeutralDirection",neutralDirection);
					}else {
					tableMap.put("NeutralDirection","NA");
					}
					noOfAutoIdleEvents = resultSet.getString("NoOfAutoIdleEvents");
					if(noOfAutoIdleEvents!=null) {
					tableMap.put("NoOfAutoIdleEvents",noOfAutoIdleEvents);
					}else {
					tableMap.put("NoOfAutoIdleEvents","NA");
					}
					noOfAutoShutdownEvents = resultSet.getString("NoOfAutoShutdownEvents");
					if(noOfAutoShutdownEvents!=null) {
					tableMap.put("NoOfAutoShutdownEvents",noOfAutoShutdownEvents);
					}else {
					tableMap.put("NoOfAutoShutdownEvents","NA");
					}
					noOfKickdownEvents = resultSet.getString("NoOfKickdownEvents");
					if(noOfKickdownEvents!=null) {
					tableMap.put("NoOfKickdownEvents",noOfKickdownEvents);
					}else {
					tableMap.put("NoOfKickdownEvents","NA");
					}
					fuelUsedinLowIDLE = resultSet.getString("FuelUsedinLowIDLE");
					if(fuelUsedinLowIDLE!=null) {
					tableMap.put("FuelUsedinLowIDLE",fuelUsedinLowIDLE);
					}else {
					tableMap.put("FuelUsedinLowIDLE","NA");
					}
					fuelUsedinHighIDLE = resultSet.getString("FuelUsedinHighIDLE");
					if(fuelUsedinHighIDLE!=null) {
					tableMap.put("FuelUsedinHighIDLE",fuelUsedinHighIDLE);
					}else {
					tableMap.put("FuelUsedinHighIDLE","NA");
					}
					fuelUsedatExcavationMode= resultSet.getString("FuelUsedatExcavationMode");
					if(fuelUsedatExcavationMode!=null) {
					tableMap.put("FuelUsedatExcavationMode",fuelUsedatExcavationMode);
					}else {
					tableMap.put("FuelUsedatExcavationMode","NA");
					}
					fuelusedinIDLEMODE = resultSet.getString("FuelusedinIDLEMODE");
					if(fuelusedinIDLEMODE!=null) {
					tableMap.put("FuelusedinIDLEMODE",fuelusedinIDLEMODE);
					}else {
					tableMap.put("FuelusedinIDLEMODE","NA");
					}
					fuelUsedinExcavationEcoMode = resultSet.getString("FuelUsedinExcavationEcoMode");
					if(fuelUsedinExcavationEcoMode!=null) {
					tableMap.put("FuelUsedinExcavationEcoMode",fuelUsedinExcavationEcoMode);
					}else {
					tableMap.put("FuelUsedinExcavationEcoMode","NA");
					}
					fuelUsedatExcavationStandardMode = resultSet.getString("FuelUsedatExcavationStandardMode");
					if(fuelUsedatExcavationStandardMode!=null) {
					tableMap.put("FuelUsedatExcavationStandardMode",fuelUsedatExcavationStandardMode);
					}else {
					tableMap.put("FuelUsedatExcavationStandardMode","NA");
					}
					fuelUsedatExcavationPlusMode = resultSet.getString("FuelUsedatExcavationPlusMode");
					if(fuelUsedatExcavationPlusMode!=null) {
					tableMap.put("FuelUsedatExcavationPlusMode",fuelUsedatExcavationPlusMode);
					}else {
					tableMap.put("FuelUsedatExcavationPlusMode","NA");
					}
					fuelUsedatLoadingMode = resultSet.getString("FuelUsedatLoadingMode");
					if(fuelUsedatLoadingMode!=null) {
					tableMap.put("FuelUsedatLoadingMode",fuelUsedatLoadingMode);
					}else {
					tableMap.put("FuelUsedatLoadingMode","NA");
					}
					fuelUsedatRoadingMode = resultSet.getString("FuelUsedatRoadingMode");
					if(fuelUsedatRoadingMode!=null) {
					tableMap.put("FuelUsedatRoadingMode",fuelUsedatRoadingMode);
					}else {
					tableMap.put("FuelUsedatRoadingMode","NA");
					}
					pumpUpstreamPressureBand1 = resultSet.getString("PumpUpstreamPressureBand1");
					if(pumpUpstreamPressureBand1!=null) {
					tableMap.put("PumpUpstreamPressureBand1",pumpUpstreamPressureBand1);
					}else {
					tableMap.put("PumpUpstreamPressureBand1","NA");
					}
					pumpUpstreamPressureBand2 = resultSet.getString("PumpUpstreamPressureBand2");
					if(pumpUpstreamPressureBand2!=null) {
					tableMap.put("PumpUpstreamPressureBand2",pumpUpstreamPressureBand2);
					}else {
					tableMap.put("PumpUpstreamPressureBand2","NA");
					}
					pumpUpstreamPressureBand3 = resultSet.getString("PumpUpstreamPressureBand3");
					if(pumpUpstreamPressureBand3!=null) {
					tableMap.put("PumpUpstreamPressureBand3",pumpUpstreamPressureBand3);
					}else {
					tableMap.put("PumpUpstreamPressureBand3","NA");
					}
					pumpUpstreamPressureBand4 = resultSet.getString("PumpUpstreamPressureBand4");
					if(pumpUpstreamPressureBand4!=null) {
					tableMap.put("PumpUpstreamPressureBand4",pumpUpstreamPressureBand4);
					}else {
					tableMap.put("PumpUpstreamPressureBand4","NA");
					}
					pumpUpstreamPressureBand5 = resultSet.getString("PumpUpstreamPressureBand5");
					if(pumpUpstreamPressureBand5!=null) {
					tableMap.put("PumpUpstreamPressureBand5",pumpUpstreamPressureBand5);
					}else {
					tableMap.put("PumpUpstreamPressureBand5","NA");
					}
					pumpUpstreamPressureBand6 = resultSet.getString("PumpUpstreamPressureBand6");
					if(pumpUpstreamPressureBand6!=null) {
					tableMap.put("PumpUpstreamPressureBand6",pumpUpstreamPressureBand6);
					}else {
					tableMap.put("PumpUpstreamPressureBand6","NA");
					}
					pumpUpstreamPressureBand7 = resultSet.getString("PumpUpstreamPressureBand7");
					if(pumpUpstreamPressureBand7!=null) {
					tableMap.put("PumpUpstreamPressureBand7",pumpUpstreamPressureBand7);
					}else {
					tableMap.put("PumpUpstreamPressureBand7","NA");
					}
					pumpUpstreamPressureBand8 = resultSet.getString("PumpUpstreamPressureBand8");
					if(pumpUpstreamPressureBand8!=null) {
					tableMap.put("PumpUpstreamPressureBand8",pumpUpstreamPressureBand8);
					}else {
					tableMap.put("PumpUpstreamPressureBand8","NA");
					}
					pumpDownstreamPressureBand1 = resultSet.getString("PumpDownstreamPressureBand1");
					if(pumpDownstreamPressureBand1!=null) {
					tableMap.put("PumpDownstreamPressureBand1",pumpDownstreamPressureBand1);
					}else {
					tableMap.put("PumpDownstreamPressureBand1","NA");
					}
					pumpDownstreamPressureBand2 = resultSet.getString("PumpDownstreamPressureBand2");
					if(pumpDownstreamPressureBand2!=null) {
					tableMap.put("PumpDownstreamPressureBand2",pumpDownstreamPressureBand2);
					}else {
					tableMap.put("PumpDownstreamPressureBand2","NA");
					}
					pumpDownstreamPressureBand3 = resultSet.getString("PumpDownstreamPressureBand3");
					if(pumpDownstreamPressureBand3!=null) {
					tableMap.put("PumpDownstreamPressureBand3",pumpDownstreamPressureBand3);
					}else {
					tableMap.put("PumpDownstreamPressureBand3","NA");
					}
					pumpDownstreamPressureBand4 = resultSet.getString("PumpDownstreamPressureBand4");
					if(pumpDownstreamPressureBand4!=null) {
					tableMap.put("PumpDownstreamPressureBand4",pumpDownstreamPressureBand4);
					}else {
					tableMap.put("PumpDownstreamPressureBand4","NA");
					}
					pumpDownstreamPressureBand5 = resultSet.getString("PumpDownstreamPressureBand5");
					if(pumpDownstreamPressureBand5!=null) {
					tableMap.put("PumpDownstreamPressureBand5",pumpDownstreamPressureBand5);
					}else {
					tableMap.put("PumpDownstreamPressureBand5","NA");
					}
					pumpDownstreamPressureBand6 = resultSet.getString("PumpDownstreamPressureBand6");
					if(pumpDownstreamPressureBand6!=null) {
					tableMap.put("PumpDownstreamPressureBand6",pumpDownstreamPressureBand6);
					}else {
					tableMap.put("PumpDownstreamPressureBand6","NA");
					}
					pumpDownstreamPressureBand7 = resultSet.getString("PumpDownstreamPressureBand7");
					if(pumpDownstreamPressureBand7!=null) {
					tableMap.put("PumpDownstreamPressureBand7",pumpDownstreamPressureBand7);
					}else {
					tableMap.put("PumpDownstreamPressureBand7","NA");
					}
					pumpDownstreamPressureBand8 = resultSet.getString("PumpDownstreamPressureBand8");
					if(pumpDownstreamPressureBand8!=null) {
					tableMap.put("PumpDownstreamPressureBand8",pumpDownstreamPressureBand8);
					}else {
					tableMap.put("PumpDownstreamPressureBand8","NA");
					} 
					pumpDisplacementBand1 = resultSet.getString("PumpDisplacementBand1");
					if(pumpDisplacementBand1!=null) {
					tableMap.put("PumpDisplacementBand1",pumpDisplacementBand1);
					}else {
					tableMap.put("PumpDisplacementBand1","NA");
					}
					pumpDisplacementBand2 = resultSet.getString("PumpDisplacementBand2");
					if(pumpDisplacementBand2!=null) {
					tableMap.put("PumpDisplacementBand2",pumpDisplacementBand2);
					}else {
					tableMap.put("PumpDisplacementBand2","NA");
					}
					pumpDisplacementBand3 = resultSet.getString("PumpDisplacementBand3");
					if(pumpDisplacementBand3!=null) {
					tableMap.put("PumpDisplacementBand3",pumpDisplacementBand3);
					}else {
					tableMap.put("PumpDisplacementBand3","NA");
					}
					pumpDisplacementBand4 = resultSet.getString("PumpDisplacementBand4");
					if(pumpDisplacementBand4!=null) {
					tableMap.put("PumpDisplacementBand4",pumpDisplacementBand4);
					}else {
					tableMap.put("PumpDisplacementBand4","NA");
					}
					pumpDisplacementBand5 = resultSet.getString("PumpDisplacementBand5");
					if(pumpDisplacementBand5!=null) {
					tableMap.put("PumpDisplacementBand5",pumpDisplacementBand5);
					}else {
					tableMap.put("PumpDisplacementBand5","NA");
					}
					pumpDisplacementBand6 = resultSet.getString("PumpDisplacementBand6");
					if(pumpDisplacementBand6!=null) {
					tableMap.put("PumpDisplacementBand6",pumpDisplacementBand6);
					}else {
					tableMap.put("PumpDisplacementBand6","NA");
					}
					pumpDisplacementBand7 = resultSet.getString("PumpDisplacementBand7");
					if(pumpDisplacementBand7!=null) {
					tableMap.put("PumpDisplacementBand7",pumpDisplacementBand7);
					}else {
					tableMap.put("PumpDisplacementBand7","NA");
					}
					pumpDisplacementBand8 = resultSet.getString("PumpDisplacementBand8");
					if(pumpDisplacementBand8!=null) {
					tableMap.put("PumpDisplacementBand8",pumpDisplacementBand8);
					}else {
					tableMap.put("PumpDisplacementBand8","NA");
					}
					
					mapList.add(tableMap);
				}
			}else {
				resultSet = statement.executeQuery(startTAssetMonQuery);
				while(resultSet.next())
				{
					
					tableMap = new HashMap<String,String>();
					tableMap.put("Vin",resultSet.getString("Vin")); 
					tDate=resultSet.getString("Transaction_timestamp").substring(0,19); 
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
					txnTs = LocalDateTime.parse(tDate, dtf).format(dtf2);
					if(txnTs !=null) {
						tableMap.put("Transaction_timestamp",txnTs);
					}else {
						tableMap.put("Transaction_timestamp","NA");
					}
					
					 msg_id=resultSet.getString("MSG_ID");//0063,062 TFU //064,065,066,067 Totalfuelused
					 tfu = resultSet.getString("TFU");
					 totalFuelUsed = resultSet.getString("TotalFuelUsed");
						if(msg_id.equals("062") || msg_id.equals("063"))
						{
						//	tfu = resultSet.getString("TFU");
							if(tfu!=null ) {
							tableMap.put("TotalFuelUsed",tfu);
							}else {
							tableMap.put("TotalFuelUsed","NA");
							}
						}
						if(msg_id.equals("064")|| msg_id.equals("065")||msg_id.equals("066")||msg_id.equals("067"))
						{
							if( totalFuelUsed!=null) {
							tableMap.put("TotalFuelUsed",totalFuelUsed);
							}else {
							tableMap.put("TotalFuelUsed","NA");
							}	
						}
						
					llFirmware=resultSet.getString("LiveLinkfirmware");
					if(llFirmware !=null) {
					tableMap.put("LiveLinkfirmware",llFirmware);
					}else {
						tableMap.put("LiveLinkfirmware","NA");
					}
					fuelLevel= resultSet.getString("FuelLevel");
					if(fuelLevel !=null) {
						tableMap.put("FuelLevel",fuelLevel);
					}else {
						tableMap.put("FuelLevel","NA");
					}
					hours = resultSet.getString("Hours");
					if(hours !=null) {
						tableMap.put("Hours",hours);
					}else {
						tableMap.put("Hours","NA");
					}
					
					lowIdleTime = resultSet.getString("LowIdleTime");
					if(lowIdleTime !=null) {
						tableMap.put("LowIdleTime",lowIdleTime);
					}else {
						tableMap.put("LowIdleTime","NA");
					}
					
					highIdleTime = resultSet.getString("HighIdleTime");
					if(highIdleTime !=null) {
						tableMap.put("HighIdleTime",highIdleTime);
					}else {
						tableMap.put("HighIdleTime","NA");
					}
					enginespeedband1 = resultSet.getString("Enginespeedband1");
					if(enginespeedband1 !=null) {
						tableMap.put("Enginespeedband1",enginespeedband1);
					}else {
						tableMap.put("Enginespeedband1","NA");
					}
					enginespeedband2 = resultSet.getString("Enginespeedband2");
					if(enginespeedband2 !=null) {
						tableMap.put("Enginespeedband2",enginespeedband2);
					}else {
						tableMap.put("Enginespeedband2","NA");
					}
					
					enginespeedband3 = resultSet.getString("Enginespeedband3");
					if(enginespeedband3 !=null) {
						tableMap.put("Enginespeedband3",enginespeedband3);
					}else {
						tableMap.put("Enginespeedband3","NA");
					}
					
					enginespeedband4 = resultSet.getString("Enginespeedband4");
					if(enginespeedband4 !=null) {
						tableMap.put("Enginespeedband4",enginespeedband4);
					}else {
						tableMap.put("Enginespeedband4","NA");
					}
					enginespeedband5 = resultSet.getString("Enginespeedband5");
					if(enginespeedband5 !=null) {
						tableMap.put("Enginespeedband5",enginespeedband5);
					}else {
						tableMap.put("Enginespeedband5","NA");
					}
					enginespeedband6 = resultSet.getString("Enginespeedband6");
					if(enginespeedband6 !=null) {
						tableMap.put("Enginespeedband6",enginespeedband6);
					}else {
						tableMap.put("Enginespeedband6","NA");
					}
					enginespeedband7 = resultSet.getString("Enginespeedband7");
					if(enginespeedband7 !=null) {
						tableMap.put("Enginespeedband7",enginespeedband7);
					}else {
						tableMap.put("Enginespeedband7","NA");
					}
					enginespeedband8 = resultSet.getString("Enginespeedband8");
					if(enginespeedband8 !=null) {
						tableMap.put("Enginespeedband8",enginespeedband8);
					}else {
						tableMap.put("Enginespeedband8","NA");
					}
					loadingJob = resultSet.getString("LoadingJob");
					if(loadingJob !=null) {
						tableMap.put("LoadingJob",loadingJob);
					}else {
						tableMap.put("LoadingJob","NA");
					}
					
					roadingJob = resultSet.getString("RoadingJob");
					if(roadingJob !=null) {
						tableMap.put("RoadingJob",roadingJob);
					}else {
						tableMap.put("RoadingJob","NA");
					}
					attachmentJob = resultSet.getString("AttachmentJob");
					if(attachmentJob !=null) {
						tableMap.put("AttachmentJob",attachmentJob);
					}else {
						tableMap.put("AttachmentJob","NA");
					}
					excavationJob = resultSet.getString("ExcavationJob");
					if(excavationJob !=null) {
						tableMap.put("ExcavationJob",excavationJob);
					}else {
						tableMap.put("ExcavationJob","NA");
					}
					exacavationEconomyMode = resultSet.getString("ExacavationEconomyMode");
					if(exacavationEconomyMode !=null) {
						tableMap.put("ExacavationEconomyMode",exacavationEconomyMode);
					}else {
						tableMap.put("ExacavationEconomyMode","NA");
					}
					excavationStandardMode = resultSet.getString("ExcavationStandardMode");
					if(excavationStandardMode !=null) {
						tableMap.put("ExcavationStandardMode",excavationStandardMode);
					}else {
						tableMap.put("ExcavationStandardMode","NA");
					}
					excavationPlusMode = resultSet.getString("ExcavationPlusMode");
					if(excavationPlusMode!=null) {
						tableMap.put("ExcavationPlusMode",excavationPlusMode);
					}else {
					   tableMap.put("ExcavationPlusMode","NA");
					}
					roadingDistance = resultSet.getString("RoadingDistance");
					if(roadingDistance!=null) {
						tableMap.put("RoadingDistance",roadingDistance);
					}else {
						tableMap.put("RoadingDistance","NA");
					}

					gear1 = resultSet.getString("Gear1");
					if(gear1!=null) {
						tableMap.put("Gear1",gear1);
					}else {
						tableMap.put("Gear1","NA");
					}
					gear2 = resultSet.getString("Gear2");
					if(gear2!=null) {
						tableMap.put("Gear2",gear2);
					}else {
						tableMap.put("Gear2","NA");
					}
					gear3 = resultSet.getString("Gear3");
					if(gear3!=null) {
						tableMap.put("Gear3",gear3);
					}else {
						tableMap.put("Gear3","NA");
					}
					gear4 = resultSet.getString("Gear4");
					if(gear4!=null) {
						tableMap.put("Gear4",gear4);
					}else {
						tableMap.put("Gear4","NA");
					}
					gearNeutral = resultSet.getString("GearNeutral");
					if(gearNeutral!=null) {
						tableMap.put("GearNeutral",gearNeutral);
					}else {
						tableMap.put("GearNeutral","NA");
					}
					forwardDirection = resultSet.getString("ForwardDirection");
					if(forwardDirection!=null) {
					tableMap.put("ForwardDirection",forwardDirection);
					}else {
					tableMap.put("ForwardDirection","NA");
					}
					reverseDirection = resultSet.getString("ReverseDirection");
					if(reverseDirection!=null) {
					tableMap.put("ReverseDirection",reverseDirection);
					}else {
					tableMap.put("ReverseDirection","NA");
					}
					neutralDirection = resultSet.getString("NeutralDirection");
					if(neutralDirection!=null) {
					tableMap.put("NeutralDirection",neutralDirection);
					}else {
					tableMap.put("NeutralDirection","NA");
					}
					noOfAutoIdleEvents = resultSet.getString("NoOfAutoIdleEvents");
					if(noOfAutoIdleEvents!=null) {
					tableMap.put("NoOfAutoIdleEvents",noOfAutoIdleEvents);
					}else {
					tableMap.put("NoOfAutoIdleEvents","NA");
					}
					noOfAutoShutdownEvents = resultSet.getString("NoOfAutoShutdownEvents");
					if(noOfAutoShutdownEvents!=null) {
					tableMap.put("NoOfAutoShutdownEvents",noOfAutoShutdownEvents);
					}else {
					tableMap.put("NoOfAutoShutdownEvents","NA");
					}
					noOfKickdownEvents = resultSet.getString("NoOfKickdownEvents");
					if(noOfKickdownEvents!=null) {
					tableMap.put("NoOfKickdownEvents",noOfKickdownEvents);
					}else {
					tableMap.put("NoOfKickdownEvents","NA");
					}
					fuelUsedinLowIDLE = resultSet.getString("FuelUsedinLowIDLE");
					if(fuelUsedinLowIDLE!=null) {
					tableMap.put("FuelUsedinLowIDLE",fuelUsedinLowIDLE);
					}else {
					tableMap.put("FuelUsedinLowIDLE","NA");
					}
					fuelUsedinHighIDLE = resultSet.getString("FuelUsedinHighIDLE");
					if(fuelUsedinHighIDLE!=null) {
					tableMap.put("FuelUsedinHighIDLE",fuelUsedinHighIDLE);
					}else {
					tableMap.put("FuelUsedinHighIDLE","NA");
					}
					fuelusedinIDLEMODE = resultSet.getString("FuelusedinIDLEMODE");
					if(fuelusedinIDLEMODE!=null) {
					tableMap.put("FuelusedinIDLEMODE",fuelusedinIDLEMODE);
					}else {
					tableMap.put("FuelusedinIDLEMODE","NA");
					}
					fuelUsedinExcavationEcoMode = resultSet.getString("FuelUsedinExcavationEcoMode");
					if(fuelUsedinExcavationEcoMode!=null) {
					tableMap.put("FuelUsedinExcavationEcoMode",fuelUsedinExcavationEcoMode);
					}else {
					tableMap.put("FuelUsedinExcavationEcoMode","NA");
					}
					fuelUsedatExcavationStandardMode = resultSet.getString("FuelUsedatExcavationStandardMode");
					if(fuelUsedatExcavationStandardMode!=null) {
					tableMap.put("FuelUsedatExcavationStandardMode",fuelUsedatExcavationStandardMode);
					}else {
					tableMap.put("FuelUsedatExcavationStandardMode","NA");
					}
					fuelUsedatExcavationPlusMode = resultSet.getString("FuelUsedatExcavationPlusMode");
					if(fuelUsedatExcavationPlusMode!=null) {
					tableMap.put("FuelUsedatExcavationPlusMode",fuelUsedatExcavationPlusMode);
					}else {
					tableMap.put("FuelUsedatExcavationPlusMode","NA");
					}
					fuelUsedatLoadingMode = resultSet.getString("FuelUsedatLoadingMode");
					if(fuelUsedatLoadingMode!=null) {
					tableMap.put("FuelUsedatLoadingMode",fuelUsedatLoadingMode);
					}else {
					tableMap.put("FuelUsedatLoadingMode","NA");
					}
					fuelUsedatRoadingMode = resultSet.getString("FuelUsedatRoadingMode");
					if(fuelUsedatRoadingMode!=null) {
					tableMap.put("FuelUsedatRoadingMode",fuelUsedatRoadingMode);
					}else {
					tableMap.put("FuelUsedatRoadingMode","NA");
					}
					pumpUpstreamPressureBand1 = resultSet.getString("PumpUpstreamPressureBand1");
					if(pumpUpstreamPressureBand1!=null) {
					tableMap.put("PumpUpstreamPressureBand1",pumpUpstreamPressureBand1);
					}else {
					tableMap.put("PumpUpstreamPressureBand1","NA");
					}
					pumpUpstreamPressureBand2 = resultSet.getString("PumpUpstreamPressureBand2");
					if(pumpUpstreamPressureBand2!=null) {
					tableMap.put("PumpUpstreamPressureBand2",pumpUpstreamPressureBand2);
					}else {
					tableMap.put("PumpUpstreamPressureBand2","NA");
					}
					pumpUpstreamPressureBand3 = resultSet.getString("PumpUpstreamPressureBand3");
					if(pumpUpstreamPressureBand3!=null) {
					tableMap.put("PumpUpstreamPressureBand3",pumpUpstreamPressureBand3);
					}else {
					tableMap.put("PumpUpstreamPressureBand3","NA");
					}
					pumpUpstreamPressureBand4 = resultSet.getString("PumpUpstreamPressureBand4");
					if(pumpUpstreamPressureBand4!=null) {
					tableMap.put("PumpUpstreamPressureBand4",pumpUpstreamPressureBand4);
					}else {
					tableMap.put("PumpUpstreamPressureBand4","NA");
					}
					pumpUpstreamPressureBand5 = resultSet.getString("PumpUpstreamPressureBand5");
					if(pumpUpstreamPressureBand5!=null) {
					tableMap.put("PumpUpstreamPressureBand5",pumpUpstreamPressureBand5);
					}else {
					tableMap.put("PumpUpstreamPressureBand5","NA");
					}
					pumpUpstreamPressureBand6 = resultSet.getString("PumpUpstreamPressureBand6");
					if(pumpUpstreamPressureBand6!=null) {
					tableMap.put("PumpUpstreamPressureBand6",pumpUpstreamPressureBand6);
					}else {
					tableMap.put("PumpUpstreamPressureBand6","NA");
					}
					pumpUpstreamPressureBand7 = resultSet.getString("PumpUpstreamPressureBand7");
					if(pumpUpstreamPressureBand7!=null) {
					tableMap.put("PumpUpstreamPressureBand7",pumpUpstreamPressureBand7);
					}else {
					tableMap.put("PumpUpstreamPressureBand7","NA");
					}
					pumpUpstreamPressureBand8 = resultSet.getString("PumpUpstreamPressureBand8");
					if(pumpUpstreamPressureBand8!=null) {
					tableMap.put("PumpUpstreamPressureBand8",pumpUpstreamPressureBand8);
					}else {
					tableMap.put("PumpUpstreamPressureBand8","NA");
					}
					pumpDownstreamPressureBand1 = resultSet.getString("PumpDownstreamPressureBand1");
					if(pumpDownstreamPressureBand1!=null) {
					tableMap.put("PumpDownstreamPressureBand1",pumpDownstreamPressureBand1);
					}else {
					tableMap.put("PumpDownstreamPressureBand1","NA");
					}
					pumpDownstreamPressureBand2 = resultSet.getString("PumpDownstreamPressureBand2");
					if(pumpDownstreamPressureBand2!=null) {
					tableMap.put("PumpDownstreamPressureBand2",pumpDownstreamPressureBand2);
					}else {
					tableMap.put("PumpDownstreamPressureBand2","NA");
					}
					pumpDownstreamPressureBand3 = resultSet.getString("PumpDownstreamPressureBand3");
					if(pumpDownstreamPressureBand3!=null) {
					tableMap.put("PumpDownstreamPressureBand3",pumpDownstreamPressureBand3);
					}else {
					tableMap.put("PumpDownstreamPressureBand3","NA");
					}
					pumpDownstreamPressureBand4 = resultSet.getString("PumpDownstreamPressureBand4");
					if(pumpDownstreamPressureBand4!=null) {
					tableMap.put("PumpDownstreamPressureBand4",pumpDownstreamPressureBand4);
					}else {
					tableMap.put("PumpDownstreamPressureBand4","NA");
					}
					pumpDownstreamPressureBand5 = resultSet.getString("PumpDownstreamPressureBand5");
					if(pumpDownstreamPressureBand5!=null) {
					tableMap.put("PumpDownstreamPressureBand5",pumpDownstreamPressureBand5);
					}else {
					tableMap.put("PumpDownstreamPressureBand5","NA");
					}
					pumpDownstreamPressureBand6 = resultSet.getString("PumpDownstreamPressureBand6");
					if(pumpDownstreamPressureBand6!=null) {
					tableMap.put("PumpDownstreamPressureBand6",pumpDownstreamPressureBand6);
					}else {
					tableMap.put("PumpDownstreamPressureBand6","NA");
					}
					pumpDownstreamPressureBand7 = resultSet.getString("PumpDownstreamPressureBand7");
					if(pumpDownstreamPressureBand7!=null) {
					tableMap.put("PumpDownstreamPressureBand7",pumpDownstreamPressureBand7);
					}else {
					tableMap.put("PumpDownstreamPressureBand7","NA");
					}
					pumpDownstreamPressureBand8 = resultSet.getString("PumpDownstreamPressureBand8");
					if(pumpDownstreamPressureBand8!=null) {
					tableMap.put("PumpDownstreamPressureBand8",pumpDownstreamPressureBand8);
					}else {
					tableMap.put("PumpDownstreamPressureBand8","NA");
					}
					pumpDisplacementBand1 = resultSet.getString("PumpDisplacementBand1");
					if(pumpDisplacementBand1!=null) {
					tableMap.put("PumpDisplacementBand1",pumpDisplacementBand1);
					}else {
					tableMap.put("PumpDisplacementBand1","NA");
					}
					pumpDisplacementBand2 = resultSet.getString("PumpDisplacementBand2");
					if(pumpDisplacementBand2!=null) {
					tableMap.put("PumpDisplacementBand2",pumpDisplacementBand2);
					}else {
					tableMap.put("PumpDisplacementBand2","NA");
					}
					pumpDisplacementBand3 = resultSet.getString("PumpDisplacementBand3");
					if(pumpDisplacementBand3!=null) {
					tableMap.put("PumpDisplacementBand3",pumpDisplacementBand3);
					}else {
					tableMap.put("PumpDisplacementBand3","NA");
					}
					pumpDisplacementBand4 = resultSet.getString("PumpDisplacementBand4");
					if(pumpDisplacementBand4!=null) {
					tableMap.put("PumpDisplacementBand4",pumpDisplacementBand4);
					}else {
					tableMap.put("PumpDisplacementBand4","NA");
					}
					pumpDisplacementBand5 = resultSet.getString("PumpDisplacementBand5");
					if(pumpDisplacementBand5!=null) {
					tableMap.put("PumpDisplacementBand5",pumpDisplacementBand5);
					}else {
					tableMap.put("PumpDisplacementBand5","NA");
					}
					pumpDisplacementBand6 = resultSet.getString("PumpDisplacementBand6");
					if(pumpDisplacementBand6!=null) {
					tableMap.put("PumpDisplacementBand6",pumpDisplacementBand6);
					}else {
					tableMap.put("PumpDisplacementBand6","NA");
					}
					pumpDisplacementBand7 = resultSet.getString("PumpDisplacementBand7");
					if(pumpDisplacementBand7!=null) {
					tableMap.put("PumpDisplacementBand7",pumpDisplacementBand7);
					}else {
					tableMap.put("PumpDisplacementBand7","NA");
					}
					pumpDisplacementBand8 = resultSet.getString("PumpDisplacementBand8");
					if(pumpDisplacementBand8!=null) {
					tableMap.put("PumpDisplacementBand8",pumpDisplacementBand8);
					}else {
					tableMap.put("PumpDisplacementBand8","NA");
					}
					
					mapList.add(tableMap);
				}
				
				resultSet = statement.executeQuery(endTAssetMonQuery);
				while(resultSet.next())
				{
					
					tableMap = new HashMap<String,String>();
					tableMap.put("Vin",resultSet.getString("Vin"));
					tDate=resultSet.getString("Transaction_timestamp").substring(0,19); 
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
					txnTs = LocalDateTime.parse(tDate, dtf).format(dtf2);
					if(txnTs !=null) {
						tableMap.put("Transaction_timestamp",txnTs);
					}else {
						tableMap.put("Transaction_timestamp","NA");
					}
					
					msg_id=resultSet.getString("MSG_ID");//0063,062 TFU //064,065,066,067 Totalfuelused
					 tfu = resultSet.getString("TFU");
					 totalFuelUsed = resultSet.getString("TotalFuelUsed");
						if(msg_id.equals("062") || msg_id.equals("063"))
						{
							if(tfu!=null) {
							tableMap.put("TotalFuelUsed",tfu);
							}else {
							tableMap.put("TotalFuelUsed","NA");
							}
						}
						if(msg_id.equals("064")|| msg_id.equals("065")||msg_id.equals("066")||msg_id.equals("067"))
						{
							if(totalFuelUsed!=null) {
							tableMap.put("TotalFuelUsed",totalFuelUsed);
							}else {
							tableMap.put("TotalFuelUsed","NA");
							}	
						}
					
					llFirmware=resultSet.getString("LiveLinkfirmware");
					if(llFirmware !=null) {
					tableMap.put("LiveLinkfirmware",llFirmware);
					}else {
						tableMap.put("LiveLinkfirmware","NA");
					}
					fuelLevel= resultSet.getString("FuelLevel");
					if(fuelLevel !=null) {
						tableMap.put("FuelLevel",fuelLevel);
					}else {
						tableMap.put("FuelLevel","NA");
					}
					hours = resultSet.getString("Hours");
					if(hours !=null) {
						tableMap.put("Hours",hours);
					}else {
						tableMap.put("Hours","NA");
					}
					
					lowIdleTime = resultSet.getString("LowIdleTime");
					if(lowIdleTime !=null) {
						tableMap.put("LowIdleTime",lowIdleTime);
					}else {
						tableMap.put("LowIdleTime","NA");
					}
					
					highIdleTime = resultSet.getString("HighIdleTime");
					if(highIdleTime !=null) {
						tableMap.put("HighIdleTime",highIdleTime);
					}else {
						tableMap.put("HighIdleTime","NA");
					}
					enginespeedband1 = resultSet.getString("Enginespeedband1");
					if(enginespeedband1 !=null) {
						tableMap.put("Enginespeedband1",enginespeedband1);
					}else {
						tableMap.put("Enginespeedband1","NA");
					}
					enginespeedband2 = resultSet.getString("Enginespeedband2");
					if(enginespeedband2 !=null) {
						tableMap.put("Enginespeedband2",enginespeedband2);
					}else {
						tableMap.put("Enginespeedband2","NA");
					}
					
					enginespeedband3 = resultSet.getString("Enginespeedband3");
					if(enginespeedband3 !=null) {
						tableMap.put("Enginespeedband3",enginespeedband3);
					}else {
						tableMap.put("Enginespeedband3","NA");
					}
					
					enginespeedband4 = resultSet.getString("Enginespeedband4");
					if(enginespeedband4 !=null) {
						tableMap.put("Enginespeedband4",enginespeedband4);
					}else {
						tableMap.put("Enginespeedband4","NA");
					}
					enginespeedband5 = resultSet.getString("Enginespeedband5");
					if(enginespeedband5 !=null) {
						tableMap.put("Enginespeedband5",enginespeedband5);
					}else {
						tableMap.put("Enginespeedband5","NA");
					}
					
					enginespeedband6 = resultSet.getString("Enginespeedband6");
					if(enginespeedband6 !=null) {
						tableMap.put("Enginespeedband6",enginespeedband6);
					}else {
						tableMap.put("Enginespeedband6","NA");
					}
					enginespeedband7 = resultSet.getString("Enginespeedband7");
					if(enginespeedband7 !=null) {
						tableMap.put("Enginespeedband7",enginespeedband7);
					}else {
						tableMap.put("Enginespeedband7","NA");
					}
					enginespeedband8 = resultSet.getString("Enginespeedband8");
					if(enginespeedband8 !=null) {
						tableMap.put("Enginespeedband8",enginespeedband8);
					}else {
						tableMap.put("Enginespeedband8","NA");
					}
					loadingJob = resultSet.getString("LoadingJob");
					if(loadingJob !=null) {
						tableMap.put("LoadingJob",loadingJob);
					}else {
						tableMap.put("LoadingJob","NA");
					}
					
					roadingJob = resultSet.getString("RoadingJob");
					if(roadingJob !=null) {
						tableMap.put("RoadingJob",roadingJob);
					}else {
						tableMap.put("RoadingJob","NA");
					}
					attachmentJob = resultSet.getString("AttachmentJob");
					if(attachmentJob !=null) {
						tableMap.put("AttachmentJob",attachmentJob);
					}else {
						tableMap.put("AttachmentJob","NA");
					}
					excavationJob = resultSet.getString("ExcavationJob");
					if(excavationJob !=null) {
						tableMap.put("ExcavationJob",excavationJob);
					}else {
						tableMap.put("ExcavationJob","NA");
					}
					exacavationEconomyMode = resultSet.getString("ExacavationEconomyMode");
					if(exacavationEconomyMode !=null) {
						tableMap.put("ExacavationEconomyMode",exacavationEconomyMode);
					}else {
						tableMap.put("ExacavationEconomyMode","NA");
					}
					excavationStandardMode = resultSet.getString("ExcavationStandardMode");
					if(excavationStandardMode !=null) {
						tableMap.put("ExcavationStandardMode",excavationStandardMode);
					}else {
						tableMap.put("ExcavationStandardMode","NA");
					}
					excavationPlusMode = resultSet.getString("ExcavationPlusMode");
					if(excavationPlusMode!=null) {
						tableMap.put("ExcavationPlusMode",excavationPlusMode);
					}else {
						tableMap.put("ExcavationPlusMode","NA");
					}
					roadingDistance = resultSet.getString("RoadingDistance");
					if(roadingDistance!=null) {
						tableMap.put("RoadingDistance",roadingDistance);
					}else {
						tableMap.put("RoadingDistance","NA");
					}
				
					gear1 = resultSet.getString("Gear1");
					if(gear1!=null) {
						tableMap.put("Gear1",gear1);
					}else {
						tableMap.put("Gear1","NA");
					}
					gear2 = resultSet.getString("Gear2");
					if(gear2!=null) {
						tableMap.put("Gear2",gear2);
					}else {
						tableMap.put("Gear2","NA");
					}
					gear3 = resultSet.getString("Gear3");
					if(gear3!=null) {
						tableMap.put("Gear3",gear3);
					}else {
						tableMap.put("Gear3","NA");
					}
					gear4 = resultSet.getString("Gear4");
					if(gear4!=null) {
						tableMap.put("Gear4",gear4);
					}else {
						tableMap.put("Gear4","NA");
					}
					gearNeutral = resultSet.getString("GearNeutral");
					if(gearNeutral!=null) {
						tableMap.put("GearNeutral",gearNeutral);
					}else {
						tableMap.put("GearNeutral","NA");
					}
					forwardDirection = resultSet.getString("ForwardDirection");
					if(forwardDirection!=null) {
					tableMap.put("ForwardDirection",forwardDirection);
					}else {
					tableMap.put("ForwardDirection","NA");
					}
					reverseDirection = resultSet.getString("ReverseDirection");
					if(reverseDirection!=null) {
					tableMap.put("ReverseDirection",reverseDirection);
					}else {
					tableMap.put("ReverseDirection","NA");
					}
					neutralDirection = resultSet.getString("NeutralDirection");
					if(neutralDirection!=null) {
					tableMap.put("NeutralDirection",neutralDirection);
					}else {
					tableMap.put("NeutralDirection","NA");
					}
					noOfAutoIdleEvents = resultSet.getString("NoOfAutoIdleEvents");
					if(noOfAutoIdleEvents!=null) {
					tableMap.put("NoOfAutoIdleEvents",noOfAutoIdleEvents);
					}else {
					tableMap.put("NoOfAutoIdleEvents","NA");
					}
					noOfAutoShutdownEvents = resultSet.getString("NoOfAutoShutdownEvents");
					if(noOfAutoShutdownEvents!=null) {
					tableMap.put("NoOfAutoShutdownEvents",noOfAutoShutdownEvents);
					}else {
					tableMap.put("NoOfAutoShutdownEvents","NA");
					}
					noOfKickdownEvents = resultSet.getString("NoOfKickdownEvents");
					if(noOfKickdownEvents!=null) {
					tableMap.put("NoOfKickdownEvents",noOfKickdownEvents);
					}else {
					tableMap.put("NoOfKickdownEvents","NA");
					}
					fuelUsedinLowIDLE = resultSet.getString("FuelUsedinLowIDLE");
					if(fuelUsedinLowIDLE!=null) {
					tableMap.put("FuelUsedinLowIDLE",fuelUsedinLowIDLE);
					}else {
					tableMap.put("FuelUsedinLowIDLE","NA");
					}
					fuelUsedinHighIDLE = resultSet.getString("FuelUsedinHighIDLE");
					if(fuelUsedinHighIDLE!=null) {
					tableMap.put("FuelUsedinHighIDLE",fuelUsedinHighIDLE);
					}else {
					tableMap.put("FuelUsedinHighIDLE","NA");
					}
					fuelusedinIDLEMODE = resultSet.getString("FuelusedinIDLEMODE");
					if(fuelusedinIDLEMODE!=null) {
					tableMap.put("FuelusedinIDLEMODE",fuelusedinIDLEMODE);
					}else {
					tableMap.put("FuelusedinIDLEMODE","NA");
					}
					fuelUsedinExcavationEcoMode = resultSet.getString("FuelUsedinExcavationEcoMode");
					if(fuelUsedinExcavationEcoMode!=null) {
					tableMap.put("FuelUsedinExcavationEcoMode",fuelUsedinExcavationEcoMode);
					}else {
					tableMap.put("FuelUsedinExcavationEcoMode","NA");
					}
					fuelUsedatExcavationStandardMode = resultSet.getString("FuelUsedatExcavationStandardMode");
					if(fuelUsedatExcavationStandardMode!=null) {
					tableMap.put("FuelUsedatExcavationStandardMode",fuelUsedatExcavationStandardMode);
					}else {
					tableMap.put("FuelUsedatExcavationStandardMode","NA");
					}
					fuelUsedatExcavationPlusMode = resultSet.getString("FuelUsedatExcavationPlusMode");
					if(fuelUsedatExcavationPlusMode!=null) {
					tableMap.put("FuelUsedatExcavationPlusMode",fuelUsedatExcavationPlusMode);
					}else {
					tableMap.put("FuelUsedatExcavationPlusMode","NA");
					}
					fuelUsedatLoadingMode = resultSet.getString("FuelUsedatLoadingMode");
					if(fuelUsedatLoadingMode!=null) {
					tableMap.put("FuelUsedatLoadingMode",fuelUsedatLoadingMode);
					}else {
					tableMap.put("FuelUsedatLoadingMode","NA");
					}
					fuelUsedatRoadingMode = resultSet.getString("FuelUsedatRoadingMode");
					if(fuelUsedatRoadingMode!=null) {
					tableMap.put("FuelUsedatRoadingMode",fuelUsedatRoadingMode);
					}else {
					tableMap.put("FuelUsedatRoadingMode","NA");
					}
					pumpUpstreamPressureBand1 = resultSet.getString("PumpUpstreamPressureBand1");
					if(pumpUpstreamPressureBand1!=null) {
					tableMap.put("PumpUpstreamPressureBand1",pumpUpstreamPressureBand1);
					}else {
					tableMap.put("PumpUpstreamPressureBand1","NA");
					}
					pumpUpstreamPressureBand2 = resultSet.getString("PumpUpstreamPressureBand2");
					if(pumpUpstreamPressureBand2!=null) {
					tableMap.put("PumpUpstreamPressureBand2",pumpUpstreamPressureBand2);
					}else {
					tableMap.put("PumpUpstreamPressureBand2","NA");
					}
					pumpUpstreamPressureBand3 = resultSet.getString("PumpUpstreamPressureBand3");
					if(pumpUpstreamPressureBand3!=null) {
					tableMap.put("PumpUpstreamPressureBand3",pumpUpstreamPressureBand3);
					}else {
					tableMap.put("PumpUpstreamPressureBand3","NA");
					}
					pumpUpstreamPressureBand4 = resultSet.getString("PumpUpstreamPressureBand4");
					if(pumpUpstreamPressureBand4!=null) {
					tableMap.put("PumpUpstreamPressureBand4",pumpUpstreamPressureBand4);
					}else {
					tableMap.put("PumpUpstreamPressureBand4","NA");
					}
					pumpUpstreamPressureBand5 = resultSet.getString("PumpUpstreamPressureBand5");
					if(pumpUpstreamPressureBand5!=null) {
					tableMap.put("PumpUpstreamPressureBand5",pumpUpstreamPressureBand5);
					}else {
					tableMap.put("PumpUpstreamPressureBand5","NA");
					}
					pumpUpstreamPressureBand6 = resultSet.getString("PumpUpstreamPressureBand6");
					if(pumpUpstreamPressureBand6!=null) {
					tableMap.put("PumpUpstreamPressureBand6",pumpUpstreamPressureBand6);
					}else {
					tableMap.put("PumpUpstreamPressureBand6","NA");
					}
					pumpUpstreamPressureBand7 = resultSet.getString("PumpUpstreamPressureBand7");
					if(pumpUpstreamPressureBand7!=null) {
					tableMap.put("PumpUpstreamPressureBand7",pumpUpstreamPressureBand7);
					}else {
					tableMap.put("PumpUpstreamPressureBand7","NA");
					}
					pumpUpstreamPressureBand8 = resultSet.getString("PumpUpstreamPressureBand8");
					if(pumpUpstreamPressureBand8!=null) {
					tableMap.put("PumpUpstreamPressureBand8",pumpUpstreamPressureBand8);
					}else {
					tableMap.put("PumpUpstreamPressureBand8","NA");
					}
					pumpDownstreamPressureBand1 = resultSet.getString("PumpDownstreamPressureBand1");
					if(pumpDownstreamPressureBand1!=null) {
					tableMap.put("PumpDownstreamPressureBand1",pumpDownstreamPressureBand1);
					}else {
					tableMap.put("PumpDownstreamPressureBand1","NA");
					}
					pumpDownstreamPressureBand2 = resultSet.getString("PumpDownstreamPressureBand2");
					if(pumpDownstreamPressureBand2!=null) {
					tableMap.put("PumpDownstreamPressureBand2",pumpDownstreamPressureBand2);
					}else {
					tableMap.put("PumpDownstreamPressureBand2","NA");
					}
					pumpDownstreamPressureBand3 = resultSet.getString("PumpDownstreamPressureBand3");
					if(pumpDownstreamPressureBand3!=null) {
					tableMap.put("PumpDownstreamPressureBand3",pumpDownstreamPressureBand3);
					}else {
					tableMap.put("PumpDownstreamPressureBand3","NA");
					}
					pumpDownstreamPressureBand4 = resultSet.getString("PumpDownstreamPressureBand4");
					if(pumpDownstreamPressureBand4!=null) {
					tableMap.put("PumpDownstreamPressureBand4",pumpDownstreamPressureBand4);
					}else {
					tableMap.put("PumpDownstreamPressureBand4","NA");
					}
					pumpDownstreamPressureBand5 = resultSet.getString("PumpDownstreamPressureBand5");
					if(pumpDownstreamPressureBand5!=null) {
					tableMap.put("PumpDownstreamPressureBand5",pumpDownstreamPressureBand5);
					}else {
					tableMap.put("PumpDownstreamPressureBand5","NA");
					}
					pumpDownstreamPressureBand6 = resultSet.getString("PumpDownstreamPressureBand6");
					if(pumpDownstreamPressureBand6!=null) {
					tableMap.put("PumpDownstreamPressureBand6",pumpDownstreamPressureBand6);
					}else {
					tableMap.put("PumpDownstreamPressureBand6","NA");
					}
					pumpDownstreamPressureBand7 = resultSet.getString("PumpDownstreamPressureBand7");
					if(pumpDownstreamPressureBand7!=null) {
					tableMap.put("PumpDownstreamPressureBand7",pumpDownstreamPressureBand7);
					}else {
					tableMap.put("PumpDownstreamPressureBand7","NA");
					}
					pumpDownstreamPressureBand8 = resultSet.getString("PumpDownstreamPressureBand8");
					if(pumpDownstreamPressureBand8!=null) {
					tableMap.put("PumpDownstreamPressureBand8",pumpDownstreamPressureBand8);
					}else {
					tableMap.put("PumpDownstreamPressureBand8","NA");
					}
					pumpDisplacementBand1 = resultSet.getString("PumpDisplacementBand1");
					if(pumpDisplacementBand1!=null) {
					tableMap.put("PumpDisplacementBand1",pumpDisplacementBand1);
					}else {
					tableMap.put("PumpDisplacementBand1","NA");
					}
					pumpDisplacementBand2 = resultSet.getString("PumpDisplacementBand2");
					if(pumpDisplacementBand2!=null) {
					tableMap.put("PumpDisplacementBand2",pumpDisplacementBand2);
					}else {
					tableMap.put("PumpDisplacementBand2","NA");
					}
					pumpDisplacementBand3 = resultSet.getString("PumpDisplacementBand3");
					if(pumpDisplacementBand3!=null) {
					tableMap.put("PumpDisplacementBand3",pumpDisplacementBand3);
					}else {
					tableMap.put("PumpDisplacementBand3","NA");
					}
					pumpDisplacementBand4 = resultSet.getString("PumpDisplacementBand4");
					if(pumpDisplacementBand4!=null) {
					tableMap.put("PumpDisplacementBand4",pumpDisplacementBand4);
					}else {
					tableMap.put("PumpDisplacementBand4","NA");
					}
					pumpDisplacementBand5 = resultSet.getString("PumpDisplacementBand5");
					if(pumpDisplacementBand5!=null) {
					tableMap.put("PumpDisplacementBand5",pumpDisplacementBand5);
					}else {
					tableMap.put("PumpDisplacementBand5","NA");
					}
					pumpDisplacementBand6 = resultSet.getString("PumpDisplacementBand6");
					if(pumpDisplacementBand6!=null) {
					tableMap.put("PumpDisplacementBand6",pumpDisplacementBand6);
					}else {
					tableMap.put("PumpDisplacementBand6","NA");
					}
					pumpDisplacementBand7 = resultSet.getString("PumpDisplacementBand7");
					if(pumpDisplacementBand7!=null) {
					tableMap.put("PumpDisplacementBand7",pumpDisplacementBand7);
					}else {
					tableMap.put("PumpDisplacementBand7","NA");
					}
					pumpDisplacementBand8 = resultSet.getString("PumpDisplacementBand8");
					if(pumpDisplacementBand8!=null) {
					tableMap.put("PumpDisplacementBand8",pumpDisplacementBand8);
					}else {
					tableMap.put("PumpDisplacementBand8","NA");
					}
					
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

		  iLogger.info("getLogDownloadDataReportImpl::End::"+mapList);
		  return mapList;
	}

}
