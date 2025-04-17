package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.GensetCurrentDataResponseContract;
import remote.wise.service.datacontract.GensetDefFillLevelResponseContract;
import remote.wise.service.datacontract.GensetDummyLoadBankDataContract;
import remote.wise.service.datacontract.GensetTrendChartDataContract;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;

//CR424 : 20231127 : prasad : Genset CPCB4+ for Stage V machines 
//LL98: 04172025: Prapoorna: DefLevel value for Genset Machines
public class GensetServiceImpl {


	List<String> gensetModelCodes = Arrays.asList("EBA8Z", "EBJ3Z");


	public String getCurrentData(String vin) throws CustomFault {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		boolean flag=true;
		String responseString = "FAILURE";
		// Get serial number if machine number provided as input and not deactivated
		vin = new CommonUtil().getVin(vin);
		if (vin == null) {
			fLogger.fatal("GensetServiceImpl: getCurrentTab: Provided in either deactivated or invalid: "
					+ vin);
		} else {
			ConnectMySQL factory = new ConnectMySQL();
			String selectQuery = "select Serial_Number, Replace(txndata->\"$.ID_Generator_Frequency\",'\"','') as IdGeneratorFrequency, "
					+ "Replace(txndata->\"$.GEN_PHASEA_LINE_NEUTRAL_VOLTAGE\",'\"','') as Gen_PhaseA_Line_Neutral_Voltage, "
					+ "Replace(txndata->\"$.GEN_PHASEB_LINE_NEUTRAL_VOLTAGE\",'\"','') as  Gen_PhaseB_Line_Neutral_Voltage, "
					+ "Replace(txndata->\"$.GEN_PHASEC_LINE_NEUTRAL_VOLTAGE\",'\"','') as Gen_PhaseC_Line_Neutral_Voltage, "
					+ "Replace(txndata->\"$.Generator_Phase_VoltageL1_L2\",'\"','') as GeneratorPhaseVoltageL1_L2, "
					+ "Replace(txndata->\"$.Generator_Phase_VoltageL2_L3\",'\"','') as GeneratorPhaseVoltageL2_L3, "
					+ "Replace(txndata->\"$.Generator_Phase_VoltageL3_L1\",'\"','') as GeneratorPhaseVoltageL3_L1, "
					+ "Replace(txndata->\"$.GEN_CURRENT_PHASEA\",'\"','') as Gen_Current_PhaseA, "
					+ "Replace(txndata->\"$.GEN_CURRENT_PHASEB\",'\"','') as Gen_Current_PhaseB, "
					+ "Replace(txndata->\"$.GEN_CURRENT_PHASEC\",'\"','') as Gen_Current_PhaseC, "
					+ "Replace(txndata->\"$.EXT_BAT_VOLT\",'\"','') as Ext_Bat_Volt "
					+ "from asset_monitoring_snapshot where serial_number='" + vin + "'";
			iLogger.info("query :" +selectQuery);
			try (Connection conn = factory.getConnection();
					Statement statement = conn.createStatement();

					ResultSet rs = statement.executeQuery(selectQuery)) {
				while (rs.next()) {
					String serialNumber = rs.getString("Serial_Number");
					if (flag) {
						if (serialNumber == null) {
							break;
						}
						String selectQuery1 = "SELECT c.Asset_Type_Code from asset a, products b, asset_type c  where a.product_id=b.product_id"
								+ " and b.asset_type_id=c.asset_type_id and a.serial_number='"+serialNumber+"'";
						Statement statement1 = conn.createStatement();
						ResultSet rs1 = statement1.executeQuery(selectQuery1);
						String modelCode = null;

						if (rs1.next()) {
							modelCode = rs1.getString("Asset_Type_Code");
						}
						if (modelCode == null  || !gensetModelCodes.contains(modelCode)) {
							iLogger.info("GensetServiceImpl: getCurrentData: VIN " + vin + "is not eligible.");
							break;
						}
						flag=false;
					}
					GensetCurrentDataResponseContract response = new GensetCurrentDataResponseContract();
					//Frequency
					if(rs.getString("IdGeneratorFrequency") != null) {
						response.setFrequency(rs.getString("IdGeneratorFrequency"));
					}else {
						response.setFrequency("NA");
					}
					//Current
					if(rs.getString("Gen_Current_PhaseA") != null) {
						response.setCurrent_L1(rs.getString("Gen_Current_PhaseA"));
					}else {
						response.setCurrent_L1("NA");
					}
					if(rs.getString("Gen_Current_PhaseB") != null) {
						response.setCurrent_L2(rs.getString("Gen_Current_PhaseB"));
					}else {
						response.setCurrent_L2("NA");
					}
					if(rs.getString("Gen_Current_PhaseC") != null) {
						response.setCurrent_L3(rs.getString("Gen_Current_PhaseA"));
					}else {
						response.setCurrent_L3("NA");
					}
					//
					if(rs.getString("GEN_PHASEA_LINE_NEUTRAL_VOLTAGE") != null) {
						response.setVoltageLN_L1(rs.getString("GEN_PHASEA_LINE_NEUTRAL_VOLTAGE"));
					}else {
						response.setVoltageLN_L1("NA");
					}
					if(rs.getString("GEN_PHASEB_LINE_NEUTRAL_VOLTAGE") != null) {
						response.setVoltageLN_L2(rs.getString("GEN_PHASEB_LINE_NEUTRAL_VOLTAGE"));
					}else {
						response.setVoltageLN_L2("NA");
					}
					if(rs.getString("GEN_PHASEC_LINE_NEUTRAL_VOLTAGE") != null) {
						response.setVoltageLN_L3(rs.getString("GEN_PHASEC_LINE_NEUTRAL_VOLTAGE"));
					}else {
						response.setVoltageLN_L3("NA");
					}
					// 
					if(rs.getString("GeneratorPhaseVoltageL1_L2") != null) {
						response.setVoltageLL_L1(rs.getString("GeneratorPhaseVoltageL1_L2"));
					}else {
						response.setVoltageLL_L1("NA");
					}
					if(rs.getString("GeneratorPhaseVoltageL2_L3") != null) {
						response.setVoltageLL_L2(rs.getString("GeneratorPhaseVoltageL2_L3"));
					}else {
						response.setVoltageLL_L2("NA");
					}
					if(rs.getString("GeneratorPhaseVoltageL3_L1") != null) {
						response.setVoltageLL_L3(rs.getString("GeneratorPhaseVoltageL3_L1"));
					}else {
						response.setVoltageLL_L3("NA");
					}
					//
					if(rs.getString("EXT_BAT_VOLT") != null) {
						response.setBatteryVolt(rs.getString("EXT_BAT_VOLT"));
					}else {
						response.setBatteryVolt("EXT_BAT_VOLT");
					}

					responseString = new ObjectMapper().writeValueAsString(response);
					iLogger.info("GensetServiceImpl: getCurrentData:" + responseString);

				}} catch (SQLException e) {
					e.printStackTrace();
					fLogger.fatal(
							"GensetServiceImpl: getCurrentData: SQLException occurred:" + e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					fLogger.fatal("GensetServiceImpl: getCurrentData: Exception occurred:" + e.getMessage());
				}
		}
		return responseString;
	}
	
	public String getDefFillLevel(String vin) {
	    Logger iLogger = InfoLoggerClass.logger;
	    Logger fLogger = FatalLoggerClass.logger;

	    boolean flag = true;
	    String responseString = "FAILURE";
	    GensetDefFillLevelResponseContract response = new GensetDefFillLevelResponseContract();
	    // Get serial number if machine number provided as input and not deactivated
	    vin = new CommonUtil().getVin(vin);
	    if (vin == null) {
	        fLogger.fatal("GensetServiceImpl: getCurrentTab: Provided in either deactivated or invalid: " + vin);
	    } else {
	        ConnectMySQL factory = new ConnectMySQL();
	        String selectQuery = "select serial_number, REPLACE(txndata->'$.DEF_Level', '\"', '') as Def_Level ,"
					+ "convert_tz(Transaction_Timestamp_Log,'+00:00','+05:30') as txnTS "
	        		+ "from asset_monitoring_snapshot where serial_number='" + vin + "'";
	        iLogger.info("query :" + selectQuery);
	        try (Connection conn = factory.getConnection();
	             Statement statement = conn.createStatement();
	             ResultSet rs = statement.executeQuery(selectQuery)) {
	            while (rs.next()) {
	                String serialNumber = rs.getString("serial_number");
	                if (serialNumber == null) {
						break;
					}
	                String txnTimestampPT = null;
					if (rs.getString("txnTS") != null)
						txnTimestampPT = rs.getString("txnTS").split("\\.")[0];
					else
						txnTimestampPT = "NA";
					double defFillLevel = Double.parseDouble(rs.getString("Def_Level"));
					if(defFillLevel>0) {
						response.setDef_Level(String.valueOf((int) Math.round(defFillLevel)));
						response.setStatus("#00cc00");
						response.setTxnTimestampPT(txnTimestampPT);
						flag=false;
					}
					if(flag) {
						response.setDef_Level("NA");
						response.setStatus("NA");
						response.setTxnTimestampPT("NA");
					}
	                responseString = new ObjectMapper().writeValueAsString(response);
	                iLogger.info("GensetServiceImpl: getDefLevelData:" + responseString);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            fLogger.fatal("GensetServiceImpl: getDefLevelData: SQLException occurred:" + e.getMessage());
	        } catch (Exception e) {
	            e.printStackTrace();
	            fLogger.fatal("GensetServiceImpl: getDefLevelData: Exception occurred:" + e.getMessage());
	        }
	    }
	    return responseString;
	}


	public String getTrendsChartData(String vin, String date) throws CustomFault {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		String TAssetMonQuery = null;
		String startTAssetMonQuery = null;
		String endTAssetMonQuery = null;
		String responseString = "FAILURE";

		if(vin ==null){
			fLogger.error("Please pass a SerialNumber");
			throw new CustomFault("Please pass a SerialNumber");
		}
		if(date==null){
			fLogger.error("Please pass a valid Date");
			throw new CustomFault("Please pass a valid Date");
		}
		DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd");
		Date TxnTS = null;
		try {
			TxnTS = dateStr.parse(date);
		} catch (ParseException e2) {
			e2.printStackTrace();
			throw new CustomFault("Please pass a valid Date");
		}

		String endTS = date + " 18:30:00";
		String startTAssetMonTable = null;
		String endTAssetMonTable = null;

		String dynamicTable = new DateUtil().getDynamicTable("GenSetPower", TxnTS);
		if (dynamicTable != null) {
			endTAssetMonTable = dynamicTable;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(TxnTS);
		cal.add(Calendar.DAY_OF_YEAR, -1);

		String startTS = dateStr.format(cal.getTime());
		// data will be take from previous day 18:30 to today 18:30
		startTS = startTS + " 18:30:00";
		Timestamp starttxnTimestamp = new Timestamp(cal.getTimeInMillis());
		String startDynamicTable = new DateUtil().getDynamicTable("GenSetPower", starttxnTimestamp);

		if (startDynamicTable != null) {
			startTAssetMonTable = startDynamicTable;
		} else {
			startTAssetMonTable = endTAssetMonTable;
		}
		iLogger.info("startTAssetMonTable "+ startTAssetMonTable);
		System.out.println("startTAssetMonTable "+ startTAssetMonTable);

		if (startTAssetMonTable.equals(endTAssetMonTable)) {
			TAssetMonQuery = "select serial_number, JSON_UNQUOTE(json_extract(TxnData,'$.Apparent_Power')) as ApparentPower,"
					+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.Real_Power')) as RealPower,"
					+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.Reacive_Power')) as ReactivePower,"
					+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.GEN_CURRENT_PHASEA')) as L1,"
					+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.GEN_CURRENT_PHASEB')) as L2,"
					+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.GEN_CURRENT_PHASEC')) as L3,"
					+ " convert_tz(Transaction_Timestamp,'+00:00','+05:30') as txnTS "
					+ " FROM "+ startTAssetMonTable
					+ " WHERE serial_number ='"+vin+"' AND Transaction_Timestamp >= '" + startTS +"' AND Transaction_Timestamp <='"+ endTS+"'"
					+ " AND JSON_EXTRACT(TxnData,'$.MSG_ID') = '041' ORDER BY Transaction_timestamp ";
		} else {
			startTAssetMonQuery = "select serial_number, JSON_UNQUOTE(json_extract(TxnData,'$.Apparent_Power')) as ApparentPower,"
					+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.Real_Power')) as RealPower,"
					+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.Reacive_Power')) as ReactivePower,"
					+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.GEN_CURRENT_PHASEA')) as L1,"
					+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.GEN_CURRENT_PHASEB')) as L2,"
					+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.GEN_CURRENT_PHASEC')) as L3,"
					+ " convert_tz(Transaction_Timestamp,'+00:00','+05:30') as txnTS"
					+ " FROM "+ startTAssetMonTable
					+ " WHERE serial_number ='"+vin+"' AND Transaction_Timestamp >= '" + startTS +"' AND Transaction_Timestamp <='"+ endTS+"'"
					+ " AND JSON_EXTRACT(TxnData,'$.MSG_ID') = '041' ORDER BY Transaction_timestamp ";
			endTAssetMonQuery = "select serial_number, JSON_UNQUOTE(json_extract(TxnData,'$.Apparent_Power')) as ApparentPower,"
					+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.Real_Power')) as RealPower,"
					+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.Reacive_Power')) as ReactivePower,"
					+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.GEN_CURRENT_PHASEA')) as L1,"
					+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.GEN_CURRENT_PHASEB')) as L2,"
					+ " JSON_UNQUOTE(JSON_EXTRACT(TxnData,'$.GEN_CURRENT_PHASEC')) as L3,"
					+ " convert_tz(Transaction_Timestamp,'+00:00','+05:30') as txnTS"
					+ " FROM "+ endTAssetMonTable
					+ " WHERE serial_number ='"+vin+"' AND Transaction_Timestamp >= '" + startTS +"' AND Transaction_Timestamp <='"+ endTS+"'"
					+ " AND JSON_EXTRACT(TxnData,'$.MSG_ID') = '041' ORDER BY Transaction_timestamp ";
		}

		iLogger.info("getGensetPower TAssetMonQuery : "+TAssetMonQuery);
		iLogger.info("getGensetPower startTAssetMonQuery : "+startTAssetMonQuery);
		iLogger.info("getGensetPower endTAssetMonQuery : "+endTAssetMonQuery);

		ConnectMySQL connectionObj = new ConnectMySQL();
		List<GensetTrendChartDataContract> response = new ArrayList<>();
		try(Connection con = connectionObj.getDatalakeConnection_3309();
				Statement statement = con.createStatement()){
			if(TAssetMonQuery !=null) {
				ResultSet resultSet = statement.executeQuery(TAssetMonQuery);
				while(resultSet.next()){

					GensetTrendChartDataContract contract = new GensetTrendChartDataContract();
					int apparentPower = 0;
					int realPower = 0;
					int reactivePower = 0;
					int l1 = 0;
					int l2 = 0;
					int l3 = 0;
					contract.setTxnDate(resultSet.getString("txnTS"));
					apparentPower = resultSet.getInt("ApparentPower");
					contract.setpBlockPeriodKVAHrs(apparentPower);
					realPower = resultSet.getInt("RealPower");
					contract.setpBlockPeriodKWHrs(realPower);
					reactivePower = resultSet.getInt("ReactivePower");
					contract.setpBlockPeriodKVArHrs(reactivePower);
					l1 = resultSet.getInt("L1");
					contract.setGeneratorPhaseACurrent(l1);
					l2 = resultSet.getInt("L2");
					contract.setGeneratorPhaseBCurrent(l2);
					l3 = resultSet.getInt("L3");
					contract.setGeneratorPhaseCCurrent(l3);

					response.add(contract);
				}
			}else {
				ResultSet resultSet = statement.executeQuery(startTAssetMonQuery);
				while(resultSet.next()){

					GensetTrendChartDataContract contract = new GensetTrendChartDataContract();
					int apparentPower = 0;
					int realPower = 0;
					int reactivePower = 0;
					int l1 = 0;
					int l2 = 0;
					int l3 = 0;
					contract.setTxnDate(resultSet.getString("txnTS"));
					apparentPower = resultSet.getInt("ApparentPower");
					contract.setpBlockPeriodKVAHrs(apparentPower);
					realPower = resultSet.getInt("RealPower");
					contract.setpBlockPeriodKWHrs(realPower);
					reactivePower = resultSet.getInt("ReactivePower");
					contract.setpBlockPeriodKVArHrs(reactivePower);
					l1 = resultSet.getInt("L1");
					contract.setGeneratorPhaseACurrent(l1);
					l2 = resultSet.getInt("L2");
					contract.setGeneratorPhaseBCurrent(l2);
					l3 = resultSet.getInt("L3");
					contract.setGeneratorPhaseCCurrent(l3);

					response.add(contract);
				}

				ResultSet resultSet2 = statement.executeQuery(endTAssetMonQuery);
				while(resultSet2.next()){

					GensetTrendChartDataContract contract = new GensetTrendChartDataContract();
					int apparentPower = 0;
					int realPower = 0;
					int reactivePower = 0;
					int l1 = 0;
					int l2 = 0;
					int l3 = 0;
					contract.setTxnDate(resultSet.getString("txnTS"));
					apparentPower = resultSet.getInt("ApparentPower");
					contract.setpBlockPeriodKVAHrs(apparentPower);
					realPower = resultSet.getInt("RealPower");
					contract.setpBlockPeriodKWHrs(realPower);
					reactivePower = resultSet.getInt("ReactivePower");
					contract.setpBlockPeriodKVArHrs(reactivePower);
					l1 = resultSet.getInt("L1");
					contract.setGeneratorPhaseACurrent(l1);
					l2 = resultSet.getInt("L2");
					contract.setGeneratorPhaseBCurrent(l2);
					l3 = resultSet.getInt("L3");
					contract.setGeneratorPhaseCCurrent(l3);

					response.add(contract);
				}
			}
			responseString = new ObjectMapper().writeValueAsString(response);

		} catch (Exception ex) {
			ex.printStackTrace();
			fLogger.fatal("Exception occurred while fetching Genset Treds Data details: "
					+ ex.getMessage());
		}
		return responseString;
	}


	public String getDummyLoadBankData(String vin, String startDate, String endDate) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		String responseString = "FAILURE";

		// 1. Get serial number if machine number provided as input and not deactivated
		vin = new CommonUtil().getVin(vin);

		// 2. Validate startDate and endDate
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss");

		try {
			dtf.parse(startDate);
			dtf.parse(endDate);
		} catch (Exception e) {
			String msg = "Invalid startDate or endDate format";
			iLogger.info(msg + ": VIN:" + vin + ":startDate:" + startDate + ":endDate" + endDate);
			return responseString;
		}
		LocalDate startDateTime = LocalDate.parse(startDate, dtf);
		LocalDate endDateTime = LocalDate.parse(endDate, dtf);
		LocalDate currentDateTime = LocalDate.now();

		if (startDateTime.isAfter(endDateTime)) {
			String msg = "Invalid startDate or endDate. endDate must not be before startDate";
			iLogger.info(msg + ": VIN:" + vin + ":startDate:" + startDate + ":endDate" + endDate);
			return responseString;
		}
		if (!(startDateTime.plusMonths(3).isAfter(endDateTime) || startDateTime.plusMonths(3).equals(endDateTime))) {
			String msg = "Invalid startDate or endDate. Difference must not be greater than 3 months";
			iLogger.info(msg + ": VIN:" + vin + ":startDate:" + startDate + ":endDate" + endDate);
			return responseString;
		}
		if (!(startDateTime.plusMonths(3).isAfter(currentDateTime)
				|| startDateTime.plusWeeks(4).equals(currentDateTime))) {
			String msg = "Invalid startDate. Start date must not be before 3 months than current date";
			iLogger.info(msg + ": VIN:" + vin + ":startDate:" + startDate);
			return responseString;
		}

		// 3. Get tables names based on start date and end date.
		String txnStart = startDateTime.minusDays(1).format(dtf) + " 18:30:00";
		String txnEnd = endDateTime.format(dtf) + " 18:30:00";

		LocalDateTime txnStartDateTime = LocalDateTime.parse(txnStart, dtf2);
		LocalDateTime txnEndDateTime = LocalDateTime.parse(txnEnd, dtf2);

		LinkedHashSet<String> dynamicTables = new LinkedHashSet<>();
		while (txnStartDateTime.isBefore(txnEndDateTime)) {
			String dynamicTable = new DateUtil().getDynamicTable("DummyLoadBankData",
					Date.from(txnStartDateTime.atZone(ZoneId.systemDefault()).toInstant()));
			dynamicTables.add(dynamicTable);
			txnStartDateTime = txnStartDateTime.plusWeeks(1);
		}
		String endDynamicTable = new DateUtil().getDynamicTable("DummyLoadBankData",
				Date.from(txnEndDateTime.atZone(ZoneId.systemDefault()).toInstant()));
		dynamicTables.add(endDynamicTable);

		iLogger.info("GensetServiceImpl: DummyLoadBankData:Tables:" + dynamicTables);
		System.out.println(dynamicTables);

		List<GensetDummyLoadBankDataContract> responseList = new ArrayList<>();

		ConnectMySQL factory = new ConnectMySQL();
		try (Connection conn = factory.getDatalakeConnection_3309();
				Connection conn1 = factory.getConnection();
				Statement statement = conn.createStatement();
				Statement statement1 = conn1.createStatement()) {
			for (String dynamicTable : dynamicTables) {
				String query = "select * from ( select Serial_Number, "
						+ " convert_tz(Transaction_Timestamp,'+00:00','+05:30') as txnTS,"
						+ " Replace(TxnData->'$.Dummy_Load_Bank_ON_OFF_status','\"','') as dummyLoadBankStatus from " + dynamicTable
						+ " where serial_number= '" + vin + "' " + "and TxnData->'$.MSG_ID'='041') a where a.txnTS >='"
						+ startDate + "' " + "and  a.txnTS <= '" + endDate + " 23:59:59' order by txnTS";
				ResultSet rs = statement.executeQuery(query);

				while (rs.next()) {
					if (rs.getString("dummyLoadBankStatus") != null) {
						GensetDummyLoadBankDataContract response = new GensetDummyLoadBankDataContract();
						String txnDateStr = rs.getString("txnTS").split("\\.")[0]; 
						LocalDateTime txnDate = LocalDateTime.parse(txnDateStr, dtf2);
						response.setDate(txnDate.format(dtf3));
						response.setValue(Integer.parseInt(rs.getString("dummyLoadBankStatus")));
						responseList.add(response);
					}

				}
			}
			responseString = new ObjectMapper().writeValueAsString(responseList);
		} catch (SQLException e) {
			e.printStackTrace();
			fLogger.fatal("GensetServiceImpl: getDummyLoadBankData: SQLException occurred:" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("GensetServiceImpl: getDummyLoadBankData: Exception occurred:" + e.getMessage());
		}

		return responseString;
	}
}
