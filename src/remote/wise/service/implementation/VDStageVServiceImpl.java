/**
 * CR397 : 20230831 : Dhiraj Kumar : StageV VD development changes
 */
 //CR461 :20240226:Sai Divya :Stage V BHL model code inclusion
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.DpfFillLevelPowerTrainResponseContract;
import remote.wise.service.datacontract.DpfServiceLineGraphResponseContract;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;

public class VDStageVServiceImpl {

    	//List<String> bhlModelCodes = Arrays.asList("3DXS5", "3DXU5", "3DXX5", "4DXS5");//CR461.o
  	List<String> bhlModelCodes = Arrays.asList("3DXS5", "3DXU5", "3DXX5", "4DXS5","ZLC2","ZLC4","ZLC3","ZLC1");//CR461.n
  	List<String> lodalModelCodes = Arrays.asList("ZL01", "ZL54", "ZL53", "5DCMM", "5DDMM", "5DEMM");
  	List<String> sslModelCodes = Arrays.asList("ZRO5", "ZRO3", "3DJDB", "3DHDB");
  	//List<String> wlsModelCodes = Arrays.asList("433S5", "440S5");//CR464.o
  	//List<String> wlsModelCodes = Arrays.asList("433S5", "440S5","433B5", "440B5");//CR464.n//CR472.o
  	List<String> wlsModelCodes = Arrays.asList("433S5", "440S5","433B5", "440B5","Z45B","Z33B","Z40B");//CR472.n
  	//List<String> compModelCodes = Arrays.asList("COMS5");//CR464.o
  	List<String> compModelCodes = Arrays.asList("COMS5","ZC2W", "ZC3W","ZDOM");//CR464.n

	public String getDpfFillLevelBarGraphPT(String vin, String profile, String model) throws CustomFault {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		String responseString = "FAILURE";
		DpfFillLevelPowerTrainResponseContract response = new DpfFillLevelPowerTrainResponseContract();
		boolean flag = true;
		// Get serial number if machine number provided as input and not deactivated
		vin = new CommonUtil().getVin(vin);
		if (vin == null) {
			fLogger.fatal("VDStageVServiceImpl: getDpfFillLevelBarGraphPT: Provided in either deactivated or invalid: "
					+ vin);
		} else {
			ConnectMySQL factory = new ConnectMySQL();
			String selectQuery = "select Serial_Number, Replace(txndata->\"$.LOG_SootMon\",'\"','') as dpfFillLevel, "
					+ "Replace(txndata->\"$.LOG_DPFSMRA\",'\"','') as ManualRegenerationAvailable, "
					+ "Replace(txndata->\"$.LOG_DPFSSRR\",'\"','') as  ServiceRegenerationRequired, "
					+ "Replace(txndata->\"$.LOG_DPFSMRRL3\",'\"','') as ManualRegenerationRequiredLevel3, "
					+ "Replace(txndata->\"$.LOG_DPFSRPL2\",'\"','') as RegenerationPressingLevel2, "
					+ "convert_tz(Transaction_Timestamp_PT,'+00:00','+05:30') as txnTS "
					+ "from asset_monitoring_snapshot where serial_number='" + vin + "'";

			try (Connection conn = factory.getConnection();
					Statement statement = conn.createStatement();
					ResultSet rs = statement.executeQuery(selectQuery)) {
				while (rs.next()) {
					String serialNumber = rs.getString("Serial_Number");
					if (serialNumber == null) {
						break;
					}
					String txnTimestampPT = null;
					if (rs.getString("txnTS") != null)
						txnTimestampPT = rs.getString("txnTS").split("\\.")[0];
					else
						txnTimestampPT = "NA";
					String selectQuery1 = "SELECT Asset_Type_Code from asset_type where Asset_Type_Name='" + model
							+ "'";
					Statement statement1 = conn.createStatement();
					ResultSet rs1 = statement1.executeQuery(selectQuery1);
					String modelCode = null;
					if (rs1.next()) {
						modelCode = rs1.getString("Asset_Type_Code");
					}
					if (modelCode == null) {
						iLogger.info("VDStageVServiceImpl: getDpfServiceBarGraphDataPT: VIN " + vin + " with model "
								+ model + " and profile " + profile + " is not eligible.");
						break;
					}

					double dpfFillLevel = Double.parseDouble(rs.getString("dpfFillLevel"));
					if ((profile.equalsIgnoreCase("Backhoe") && bhlModelCodes.contains(modelCode))
							|| (profile.equalsIgnoreCase("Compactors") && compModelCodes.contains(modelCode))
							|| (profile.equalsIgnoreCase("Loadall") && lodalModelCodes.contains(modelCode))) {
						if (dpfFillLevel <= 30)
							response.setDpfFillLevel("0");
						else {
							dpfFillLevel = ((dpfFillLevel - 30) / 97) * 100;
							if (dpfFillLevel > 100)
								response.setDpfFillLevel("100");
							else
								response.setDpfFillLevel(String.valueOf((int) Math.round(dpfFillLevel)));
						}
					} else if (profile.equalsIgnoreCase("Wheel Laoders") && wlsModelCodes.contains(modelCode)) {
						if (dpfFillLevel <= 20)
							response.setDpfFillLevel("0");
						else {
							dpfFillLevel = ((dpfFillLevel - 20) / 112) * 100;
							if (dpfFillLevel > 100)
								response.setDpfFillLevel("100");
							else
								response.setDpfFillLevel(String.valueOf((int) Math.round(dpfFillLevel)));
						}
					} else if (profile.equalsIgnoreCase("Skid Steers") && sslModelCodes.contains(modelCode)) {
						if (dpfFillLevel <= 20)
							response.setDpfFillLevel("0");
						else {
							dpfFillLevel = ((dpfFillLevel - 20) / 70) * 100;
							if (dpfFillLevel > 100)
								response.setDpfFillLevel("100");
							else
								response.setDpfFillLevel(String.valueOf((int) Math.round(dpfFillLevel)));
						}
					} else {
						iLogger.info("VDStageVServiceImpl: getDpfServiceBarGraphDataPT: VIN " + vin + " with model "
								+ model + " is not eligible.");
						break;
					}
					String ManualRegenerationAvailable = rs.getString("ManualRegenerationAvailable");
					if (Integer.parseInt(ManualRegenerationAvailable) == 1) {
						//response.setStatus("Green");20240503:SaiDivya:DPF colourname changed to colourcode.old 
						response.setStatus("#00cc00");//new
						response.setMessage("Manual Regeneration Possible");
						response.setTxnTimestampPT(txnTimestampPT);
						flag = false;
						break;
					}
					String ServiceRegenerationRequired = rs.getString("ServiceRegenerationRequired");
					if (Integer.parseInt(ServiceRegenerationRequired) == 1) {
						//response.setStatus("Red");20240503:SaiDivya:DPF colourname changed to colourcode.old
						response.setStatus("#f52222");//new
						response.setMessage("Service Regeneration Required");
						response.setTxnTimestampPT(txnTimestampPT);
						flag = false;
						break;
					}
					String ManualRegenerationRequiredLevel3 = rs.getString("ManualRegenerationRequiredLevel3");
					if (Integer.parseInt(ManualRegenerationRequiredLevel3) == 1) {
						//response.setStatus("Red");20240503:SaiDivya:DPF colourname changed to colourcode.old
						response.setStatus("#f52222");//new
						response.setMessage("Manual Regeneration Required");
						response.setTxnTimestampPT(txnTimestampPT);
						flag = false;
						break;
					}
					String RegenerationPressingLevel2 = rs.getString("RegenerationPressingLevel2");
					if (Integer.parseInt(RegenerationPressingLevel2) == 1) {
						//response.setStatus("Amber");20240503:SaiDivya:DPF colourname changed to colourcode.old
						response.setStatus("#fba733");//new
						response.setMessage("Manual Regeneration Recommended");
						response.setTxnTimestampPT(txnTimestampPT);
						flag = false;
						break;
					}
					//response.setStatus("Green");20240503:SaiDivya:DPF colourname changed to colourcode.old
					response.setStatus("#00cc00");//new
					response.setMessage("");
					response.setTxnTimestampPT(txnTimestampPT);
					flag = false;
				}
				if (flag) {
					response.setDpfFillLevel("NA");
					response.setStatus("NA");
					response.setMessage("NA");
					response.setTxnTimestampPT("NA");
				}
				responseString = new ObjectMapper().writeValueAsString(response);
				iLogger.info("VDStageVServiceImpl: getDpfFillLevelBarGraphPT:" + responseString);

			} catch (SQLException e) {
				e.printStackTrace();
				fLogger.fatal(
						"VDStageVServiceImpl: getDpfFillLevelBarGraphPT: SQLException occurred:" + e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("VDStageVServiceImpl: getDpfFillLevelBarGraphPT: Exception occurred:" + e.getMessage());
			}
		}
		return responseString;
	}

	public String getDpfServiceLineGraphDataPT(String vin, String startDate, String endDate, String profile,
			String model) {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		String responseString = "FAILURE";

		// 1. Get serial number if machine number provided as input and not deactivated
		vin = new CommonUtil().getVin(vin);

		// 2. Validate startDate and endDate
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
		if (!(startDateTime.plusWeeks(4).isAfter(endDateTime) || startDateTime.plusWeeks(4).equals(endDateTime))) {
			String msg = "Invalid startDate or endDate. Difference must not be greater than 4 weeks";
			iLogger.info(msg + ": VIN:" + vin + ":startDate:" + startDate + ":endDate" + endDate);
			return responseString;
		}
		if (!(startDateTime.plusWeeks(4).isAfter(currentDateTime)
				|| startDateTime.plusWeeks(4).equals(currentDateTime))) {
			String msg = "Invalid startDate. Start date must not be before 4 weeks than current date";
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
			String dynamicTable = new DateUtil().getDynamicTable("DpfServicePTLineGraph",
					Date.from(txnStartDateTime.atZone(ZoneId.systemDefault()).toInstant()));
			dynamicTables.add(dynamicTable);
			txnStartDateTime = txnStartDateTime.plusWeeks(1);
		}
		String endDynamicTable = new DateUtil().getDynamicTable("DpfServicePTLineGraph",
				Date.from(txnEndDateTime.atZone(ZoneId.systemDefault()).toInstant()));
		dynamicTables.add(endDynamicTable);

		iLogger.info("VDStageVServiceImpl: getDpfServiceLineGraphDataPT:Tables:" + dynamicTables);
		// System.out.println(dynamicTables);

		List<DpfServiceLineGraphResponseContract> responseList = new ArrayList<>();

		ConnectMySQL factory = new ConnectMySQL();
		try (Connection conn = factory.getDatalakeConnection_3309();
				Connection conn1 = factory.getConnection();
				Statement statement = conn.createStatement();
				Statement statement1 = conn1.createStatement()) {
			for (String dynamicTable : dynamicTables) {
				String query = "select * from ( select Serial_Number, Replace(TxnData->'$.CMH','\"','') as hmr, "
						+ "Replace(txndata->'$.LOG_SootMon','\"','') as dpfFillLevel, "
						+ "convert_tz(Transaction_Timestamp,'+00:00','+05:30') as txnTS,"
						+ "Replace(txndata->'$.LOG_DPFSRS','\"','') as RegenerationStatus, "
						+ "Replace(txndata->'$.PREV_LOG_DPFSRS','\"','') as PrevRegenerationStatus, "
						+ "Replace(TxnData->'$.LOG_DPFSRC','\"','') as RegenerationEnd, "
						+ "Replace(TxnData->'$.LOG_AARIDTISRABIBO','\"','') as OperatorInhibit from " + dynamicTable
						+ " where serial_number= '" + vin + "' " + "and TxnData->'$.MSG_ID'='068') a where a.txnTS >='"
						//2023-09-29 : Dhiraj k :Include current date data in dpf line chart
						//+ startDate + "' " + "and  a.txnTS <= '" + endDate + "' order by txnTS";
						+ startDate + "' " + "and  a.txnTS <= '" + endDate + " 23:59:59' order by txnTS";
				ResultSet rs = statement.executeQuery(query);

				while (rs.next()) {
					DpfServiceLineGraphResponseContract response = new DpfServiceLineGraphResponseContract();

					String selectQuery1 = "SELECT Asset_Type_Code from asset_type where Asset_Type_Name='" + model
							+ "'";
					ResultSet rs1 = statement1.executeQuery(selectQuery1);
					String modelCode = null;
					if (rs1.next()) {
						modelCode = rs1.getString("Asset_Type_Code");
					}
					if (modelCode == null) {
						iLogger.info("VDStageVServiceImpl: getDpfServiceLineGraphDataPT: VIN " + vin + " with model"
								+ model + " is not eligible.");
						break;
					}

					if (rs.getString("dpfFillLevel") != null) {
						double dpfFillLevel = Double.parseDouble(rs.getString("dpfFillLevel"));

						if ((profile.equalsIgnoreCase("Backhoe") && bhlModelCodes.contains(modelCode))
								|| (profile.equalsIgnoreCase("Compactors") && compModelCodes.contains(modelCode))
								|| (profile.equalsIgnoreCase("Loadall") && lodalModelCodes.contains(modelCode))) {
							if (dpfFillLevel <= 30)
								response.setDpfFillLevel(0);
							else {
								dpfFillLevel = ((dpfFillLevel - 30) / 97) * 100;
								if (dpfFillLevel > 100)
									response.setDpfFillLevel(100);
								else
									response.setDpfFillLevel((int) Math.round(dpfFillLevel));
							}
						} else if (profile.equalsIgnoreCase("Wheel Laoders") && wlsModelCodes.contains(modelCode)) {
							if (dpfFillLevel <= 20)
								response.setDpfFillLevel(0);
							else {
								dpfFillLevel = ((dpfFillLevel - 20) / 112) * 100;
								if (dpfFillLevel > 100)
									response.setDpfFillLevel(100);
								else
									response.setDpfFillLevel((int) Math.round(dpfFillLevel));
							}
						} else if ((profile.equalsIgnoreCase("Skid Steers") || profile.equalsIgnoreCase("Robot")) && sslModelCodes.contains(modelCode)) {
							if (dpfFillLevel <= 30)
								response.setDpfFillLevel(0);
							else {
								dpfFillLevel = ((dpfFillLevel - 30) / 70) * 100;
								if (dpfFillLevel > 100)
									response.setDpfFillLevel(100);
								else
									response.setDpfFillLevel((int) Math.round(dpfFillLevel));
							}
						} else {
							iLogger.info("VDStageVServiceImpl: getDpfServiceLineGraphDataPT: VIN " + vin
									+ " with model " + model + " and profile " + profile + " is not eligible.");
							break;
						}
						response.setHmr(Double.parseDouble(rs.getString("hmr")));
						response.setTimestamp(rs.getString("txnTS").split("\\.")[0]);
						if (rs.getString("OperatorInhibit").equals("2")) {
							response.setOperatorInhibit(1);
						} else {
							response.setOperatorInhibit(0);
						}
						if (rs.getString("RegenerationEnd").equals("1")) {
							response.setDpfRegenerationEnd(1);
						} else {
							response.setDpfRegenerationEnd(0);
						}
						if (rs.getString("RegenerationStatus").equals("3")) {
							response.setAutomaticRegeneration(1);
						} else {
							response.setAutomaticRegeneration(0);
						}
						if (rs.getString("RegenerationStatus").equals("4")) {
							response.setManualRegeneration(1);
						} else {
							response.setManualRegeneration(0);
						}
						if (rs.getString("RegenerationStatus").equals("5")) {
							response.setServiceRegeneration(1);
						} else {
							response.setServiceRegeneration(0);
						}
						if (rs.getString("RegenerationStatus").equals("3")
								&& rs.getString("PrevRegenerationStatus") != null
								&& (rs.getString("PrevRegenerationStatus").equals("1")
										|| rs.getString("PrevRegenerationStatus").equals("2"))) {
							response.setDpfRegenerationStart(1);
						} else {
							response.setDpfRegenerationStart(0);
						}
						responseList.add(response);
					}
				}
			}
			responseString = new ObjectMapper().writeValueAsString(responseList);
		} catch (SQLException e) {
			e.printStackTrace();
			fLogger.fatal("VDStageVServiceImpl: getDpfServiceLineGraphDataPT: SQLException occurred:" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("VDStageVServiceImpl: getDpfServiceLineGraphDataPT: Exception occurred:" + e.getMessage());
		}

		return responseString;
	}

}
