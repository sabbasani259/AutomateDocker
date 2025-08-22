package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.SoftwareIdLogContract;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;

public class SoftwareIdLogServiceImpl {

	public String getSoftwareIdLog(String vin, String date) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String responseString = "FAILURE";

		iLogger.info("Webservice Input:vin:" + vin + ":date:" + date);

		// Create map for ecu source id and description
		Map<String, String> sourceMap = new HashMap<>();
		sourceMap.put("4A", "LiveLink");
		sourceMap.put("00", "EECU");
		sourceMap.put("28", "DECU");
		sourceMap.put("27", "MECU");
		sourceMap.put("2E", "HECU");
		sourceMap.put("54", "FrontDisplay");
		sourceMap.put("3D", "DCU");
		sourceMap.put("EC", "IntelliLoadController");
		sourceMap.put("03", "TECU");
		sourceMap.put("68", "RotaryKnob");
		sourceMap.put("05", "GearSelector");
		sourceMap.put("89", "AccelerometerSensor");

		// 1. Get serial number if machine number provided as input and not deactivated
		vin = new CommonUtil().getVin(vin);

		// 2. Validate startDate and endDate
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		try {
			dtf.parse(date);
		} catch (Exception e) {
			String msg = "Invalid startDate or endDate format";
			fLogger.info(msg + ": VIN:" + vin + ":date:" + date);
			return responseString;
		}

		LocalDate startDateTime = LocalDate.parse(date, dtf);
		String txnStart = startDateTime.minusDays(1).format(dtf) + " 18:30:00";
		String txnEnd = startDateTime.format(dtf) + " 18:30:00";

		LocalDateTime txnStartDateTime = LocalDateTime.parse(txnStart, dtf2);
		LocalDateTime txnEndDateTime = LocalDateTime.parse(txnEnd, dtf2);

		LinkedHashSet<String> dynamicTables = new LinkedHashSet<>();
		while (txnStartDateTime.isBefore(txnEndDateTime)) {
			String dynamicTable = new DateUtil().getMOSPDynamicTable("SoftwareIdLog",
					Date.from(txnStartDateTime.atZone(ZoneId.systemDefault()).toInstant()));
			dynamicTables.add(dynamicTable);
			txnStartDateTime = txnStartDateTime.plusWeeks(1);
		}
		String endDynamicTable = new DateUtil().getMOSPDynamicTable("SoftwareIdLog",
				Date.from(txnEndDateTime.atZone(ZoneId.systemDefault()).toInstant()));
		dynamicTables.add(endDynamicTable);
		iLogger.info("Dynamic Tables:" + dynamicTables);

		List<SoftwareIdLogContract> responseList = new LinkedList<>();
		ConnectMySQL factory = new ConnectMySQL();
		try (Connection conn = factory.getDatalakeConnection_3309(); Statement statement = conn.createStatement()) {
			for (String dynamicTable : dynamicTables) {
				String query = "select * from (select CONVERT_TZ(Transaction_Timestamp, '+00:00', '+05:30') AS ActivityDate,"
						+ " json_extract(TxnData,'$.Software_ID_ECU') as MachineECU,"
						+ " json_extract(TxnData,'$.Software_ID_ECU_2') as MachineECU2,"
						+ " json_extract(TxnData,'$.Software_ID_ECU_3') as MachineECU3,"
						+ " json_extract(TxnData,'$.Software_ID_ECU_4') as MachineECU4" + " FROM "+dynamicTable+" WHERE serial_number = '"+vin+"' ) a"
						+ " where a.ActivityDate >='"+ date + "' " + "and  a.ActivityDate <= '" + date + " 23:59:59' order by ActivityDate";
				iLogger.info("query : "+query);
				String machineECUAddr = null;
				String machineSourcId = null;
				String machineSourcDescription = null;
				String machineECUSoftwareID = null;
				ResultSet rs = statement.executeQuery(query);

				while (rs.next()) {

					if(rs.getObject("MachineECU") != null) {
						machineECUAddr = rs.getObject("MachineECU").toString().replace("\"", "");
						if (machineECUAddr.substring(0, 3).equalsIgnoreCase("215")) {
							if (sourceMap.containsKey(machineECUAddr.substring(3, 5))) {
								if(machineECUAddr.contains("*")) {
									SoftwareIdLogContract contract = new SoftwareIdLogContract();
									machineECUSoftwareID = machineECUAddr.split("\\*", 2)[1];
									machineSourcId = machineECUAddr.substring(3,5);
									machineSourcDescription = sourceMap.get(machineECUAddr.substring(3,5));
									contract.setActivityDate(rs.getString("ActivityDate").split("\\.")[0]);
									contract.setSoftwareDescription(machineECUSoftwareID);
									contract.setSourceDescription(machineSourcDescription);
									contract.setSourceId(machineSourcId);
									responseList.add(contract);
								}
							}
						}
					}
					if(rs.getObject("MachineECU2") != null) {
						machineECUAddr = rs.getObject("MachineECU2").toString().replace("\"", "");
						if (machineECUAddr.substring(0, 3).equalsIgnoreCase("215")) {

							if (sourceMap.containsKey(machineECUAddr.substring(3, 5))) {
								if(machineECUAddr.contains("*")) {
									SoftwareIdLogContract contract = new SoftwareIdLogContract();
									machineECUSoftwareID = machineECUAddr.split("\\*", 2)[1];
									machineSourcId = machineECUAddr.substring(3,5);
									machineSourcDescription = sourceMap.get(machineECUAddr.substring(3,5));
									contract.setActivityDate(rs.getString("ActivityDate").split("\\.")[0]);
									contract.setSoftwareDescription(machineECUSoftwareID);
									contract.setSourceDescription(machineSourcDescription);
									contract.setSourceId(machineSourcId);
									responseList.add(contract);
								}
							}
						}
					}
					if(rs.getObject("MachineECU3") != null) {
						machineECUAddr = rs.getObject("MachineECU3").toString().replace("\"", "");
						if (machineECUAddr.substring(0, 3).equalsIgnoreCase("215")) {
							if (sourceMap.containsKey(machineECUAddr.substring(3, 5))) {
								if(machineECUAddr.contains("*")) {
									SoftwareIdLogContract contract = new SoftwareIdLogContract();
									machineECUSoftwareID = machineECUAddr.split("\\*", 2)[1];
									machineSourcId = machineECUAddr.substring(3,5);
									machineSourcDescription = sourceMap.get(machineECUAddr.substring(3,5));
									contract.setActivityDate(rs.getString("ActivityDate").split("\\.")[0]);
									contract.setSoftwareDescription(machineECUSoftwareID);
									contract.setSourceDescription(machineSourcDescription);
									contract.setSourceId(machineSourcId);
									responseList.add(contract);
								}
							}
						}
					}
					if(rs.getObject("MachineECU4") != null) {
						machineECUAddr = rs.getObject("MachineECU4").toString().replace("\"", "");
						if (machineECUAddr.substring(0, 3).equalsIgnoreCase("215")) {
							if (sourceMap.containsKey(machineECUAddr.substring(3, 5))) {
								if(machineECUAddr.contains("*")) {
									SoftwareIdLogContract contract = new SoftwareIdLogContract();
									machineECUSoftwareID = machineECUAddr.split("\\*", 2)[1];
									machineSourcId = machineECUAddr.substring(3,5);
									machineSourcDescription = sourceMap.get(machineECUAddr.substring(3,5));
									contract.setActivityDate(rs.getString("ActivityDate").split("\\.")[0]);
									contract.setSoftwareDescription(machineECUSoftwareID);
									contract.setSourceDescription(machineSourcDescription);
									contract.setSourceId(machineSourcId);
									responseList.add(contract);
								}
							}
						}
					}
				}
			}
			responseString = new ObjectMapper().writeValueAsString(responseList);
		} catch (SQLException e) {
			e.printStackTrace();
			fLogger.fatal("SQLException occurred:" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception occurred:" + e.getMessage());
		}

		return responseString;
	}

}
