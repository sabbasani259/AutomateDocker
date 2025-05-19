/*
 * CR447 : 20240108 : Dhiraj K : Column addition in offline Comm Report
 * * CR490 : 20240903 : Sai Divya : Column addition in offline Comm Report
 */
package remote.wise.dao;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.Logger;

import com.opencsv.CSVWriter;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

//CR409-20230308-prasad
public class CommRepHOIntoXlsFileDAO {

	public String insertIntoCSVGile() {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		String result = null;
		Statement stmt = null;
		Statement stmt1 = null;
		String countrycode="+91";
		int count = 0;
		int loopFlag = 0;
		try {
			connection = connFactory.getConnection();
			if (connection == null) {
				iLogger.info(" mysql connection" + connection);
				return "Fail Update the Records due to sql connection issues.!";
			} else {

				stmt = connection.createStatement();
				String countQuery = "select count(*) from com_rep_oem_enhanced where countrycode='+91'";
				ResultSet rs = stmt.executeQuery(countQuery);

				while (rs.next()) {
					count = rs.getInt("count(*)");

				}

				File file = new File("/user/JCBLiveLink/COMMReport/CommReportHO.csv");
				//File file = new File("D://CommReport.csv");
				FileWriter outputfile = new FileWriter(file);

				CSVWriter writer = new CSVWriter(outputfile);
				String[] header = { "Serial_Number", "Profile", "Model", "Zone", "Dealer_Name", "Owner",
						"Machines hours", "version", "Pkt_Created_TS", "Pkt_Recd_TS", "Roll_Off_Date", "Installed_date",
						"Lat", "Lon", "City", "State", "device_status", "SIM_No", "Network provider", "renew_state",
						"country", "plant", "extended_warranty", "Comm_State", "Comm_District", "Comm_City",
						"Comm_Address", "Machine type", "NIP", "version", "BP_code" ,"Altitude in Meters"  , "Customer Contact No",
						"Usage Category","Sale Date","Warranty Type","Pin Code","Tehsil"};//CR446.n

				writer.writeNext(header);
				while (loopFlag < count) {
					stmt1 = connection.createStatement();

					//String query = "select * from com_rep_oem_enhanced limit " + loopFlag + "," + "5000";//CR446.o
					//String query = "SELECT a.*, b.usgaeCategory FROM com_rep_oem_enhanced a LEFT OUTER JOIN asset_profile b" + " ON a.Serial_Number=b.SerialNumber LIMIT " + loopFlag + "," + "5000";//CR446.n //CR462.O
					
					String query = "SELECT a.*, b.usgaeCategory FROM com_rep_oem_enhanced a LEFT OUTER JOIN asset_profile b" + " ON a.Serial_Number=b.SerialNumber where a.countrycode in ('+91') and a.plant in ('HAR','PUN','RAJ') LIMIT " + loopFlag + "," + "5000" ;//CR462.n
					
					iLogger.info(query);

					ResultSet rs1 = stmt1.executeQuery(query);

					while (rs1.next()) {
						String SerialNumber;
						String profile = null;
						String model = null;
						String Zone = null;
						String DealerName = null;
						String Owner = null;
						String tmh;
						String version = null;
						String Pkt_Created_TS;
						String Pkt_Recd_TS;
						String Roll_Off_Date;
						String Installed_date;
						String Lat;
						String Lon;
						String City;
						String State;
						String device_status;
						String SIM_No = null;
						String renew_state;
						String country;
						String plant;
						String extended_warranty;
						String Comm_State;
						String Comm_District;
						String Comm_City;
						String Comm_Address;
						String NIP;
						String mipVersion;
						String BP_code;
						String altitude; //CR446.n
						String customerContact; //CR446.n
						String usageCategory;//CR446.n
						//CR475.n
						String SaleDate;
						String WarrantyType;
						//CR490.n
						String Sub_District;
						String Pin_Code;
						SerialNumber = rs1.getString("Serial_Number");
						if (rs1.getString("Profile") != null) {
							profile = rs1.getString("Profile");
						} else {
							profile = "NA";
						}
						if (rs1.getString("Model") != null) {
							model = rs1.getString("Model");
						} else {
							model = "NA";
						}
						if (rs1.getString("Zone") != null) {
							Zone = rs1.getString("Zone");
						} else {
							Zone = "NA";
						}
						if (rs1.getString("Dealer_Name") != null) {
							DealerName = rs1.getString("Dealer_Name");
						} else {
							DealerName = "NA";
						}
						if (rs1.getString("Owner") != null) {
							Owner = rs1.getString("Owner");
						} else {
							Owner = "NA";
						}
						if (rs1.getString("tmh") != null) {
							tmh = rs1.getString("tmh");
						} else {
							tmh = "NA";
						}

						if (rs1.getString("version") != null) {
							version = rs1.getString("version");
						} else {
							version = "NA";
						}
						if (rs1.getString("Pkt_Created_TS") != null) {
							Pkt_Created_TS = rs1.getString("Pkt_Created_TS");
						} else {
							Pkt_Created_TS = "NA";
						}
						if (rs1.getString("Pkt_Recd_TS") != null) {
							Pkt_Recd_TS = rs1.getString("Pkt_Recd_TS");
						} else {
							Pkt_Recd_TS = "NA";
						}
						if (rs1.getString("Roll_Off_Date") != null) {
							Roll_Off_Date = rs1.getString("Roll_Off_Date");
						} else {
							Roll_Off_Date = "NA";
						}
						if (rs1.getString("Installed_date") != null) {
							Installed_date = rs1.getString("Installed_date");
						} else {
							Installed_date = "NA";
						}
						if (rs1.getString("Lat") != null) {
							Lat = rs1.getString("Lat");
						} else {
							Lat = "NA";
						}
						if (rs1.getString("Lon") != null) {
							Lon = rs1.getString("Lon");
						} else {
							Lon = "NA";
						}
						if (rs1.getString("City") != null) {
							City = rs1.getString("City");
						} else {
							City = "NA";
						}
						if (rs1.getString("State") != null) {
							State = rs1.getString("State");
						} else {
							State = "NA";
						}
						if (rs1.getString("device_status") != null) {
							device_status = rs1.getString("device_status");
						} else {
							device_status = "NA";
						}
						if (rs1.getString("SIM_No") != null) {
							SIM_No = rs1.getString("SIM_No");
						} else {
							SIM_No = "NA";
						}
						if (rs1.getString("renew_state") != null) {
							renew_state = rs1.getString("renew_state");
						} else {
							renew_state = "NA";
						}
						if (rs1.getString("country") != null) {
							country = rs1.getString("country");
						} else {
							country = "NA";
						}
						if (rs1.getString("plant") != null) {
							plant = rs1.getString("plant");
						} else {
							plant = "NA";
						}
						if (rs1.getString("extended_warranty") != null) {
							extended_warranty = rs1.getString("extended_warranty");
						} else {
							extended_warranty = "NA";
						}
						if (rs1.getString("Comm_State") != null) {
							Comm_State = rs1.getString("Comm_State");
						} else {
							Comm_State = "NA";
						}
						if (rs1.getString("Comm_District") != null) {
							Comm_District = rs1.getString("Comm_District");
						} else {
							Comm_District = "NA";
						}
						if (rs1.getString("Comm_City") != null) {
							Comm_City = rs1.getString("Comm_City");
						} else {
							Comm_City = "NA";
						}
						if (rs1.getString("Comm_Address") != null) {
							Comm_Address = rs1.getString("Comm_Address");
						} else {
							Comm_Address = "NA";
						}
						if (rs1.getString("NIP") != null) {
							NIP = rs1.getString("NIP");
						} else {
							NIP = "NA";
						}
						if (rs1.getString("version") != null) {
							mipVersion = rs1.getString("version");
						} else {
							mipVersion = "NA";
						}
						if (rs1.getString("BP_code") != null) {
							BP_code = rs1.getString("BP_code");
						} else {
							BP_code = "NA";
						}
						String networkprovider = null;
						if (!SIM_No.equalsIgnoreCase("NA") && SIM_No != null  ) {
						String simTypeCheckFlag = SIM_No.substring(0, 5);
						
						if (simTypeCheckFlag.contains("40411"))
							networkprovider = "Vi";
						else if (simTypeCheckFlag.contains("40410"))
							networkprovider = "Airtel"; //MEID100007589-prasad-20230516.n
						else if (simTypeCheckFlag.contains("405"))
							networkprovider = "Jio";
						else
							networkprovider ="Others"; //MEID100007589-prasad-20230516.n
						}
						else{
							networkprovider ="NA";
						}
						
//						String meachinetype = null;//CR465.so
//						if (!version.equalsIgnoreCase("NA") && version != null  ) {
//							String v = version.split("\\.")[0];
//					       
//							int meachinetypeCheckFlag = Integer.parseInt(v);
//                             //MEID100007576-prasad-20230516.n
//							if (meachinetypeCheckFlag >= 30 && meachinetypeCheckFlag < 50) {
//								meachinetype = "LL4";
//
//							}  //MEID100007576-prasad-20230516.n
//							else if (meachinetypeCheckFlag >= 50) {
//								meachinetype = "LL Lite";
//							} else {
//								meachinetype = "LL2";
//							}
//						} else {
//							meachinetype = "NA";
//						}//CR465.eo
						String machineType=null;//CR465.sn
						if (version != null && !version.equalsIgnoreCase("NA"))
						{
							String versionParts = version.split("\\.")[0];
							int machinetypeCheckFlag= Integer.parseInt(versionParts);
							
							if(machinetypeCheckFlag < 30)
							{
								machineType="LL2";
							}
							else if (machinetypeCheckFlag >= 30 && machinetypeCheckFlag < 50) {
								machineType = "LL4";
							
							}  
							else if (machinetypeCheckFlag == 50) {
								machineType = "LL Lite";
							} else if (machinetypeCheckFlag > 50) {
								machineType = "LL4";
							}
						} else {
							machineType = "NA";
						}//CR465.en
						if (rs1.getString("altitude") != null) { //CR446.sn
							altitude = rs1.getString("altitude");
							altitude =altitude.replace("m", "");
						
						} else {
							altitude = "NA";
						} //CR446.en
						
						if (rs1.getString("Customer_Mobile") != null) {
							customerContact = rs1.getString("Customer_Mobile");
						} else {
							customerContact = "NA";
						}
						//CR447.sn
						if(rs1.getString("usgaeCategory") == null || rs1.getString("usgaeCategory").equals("") 
								|| rs1.getString("usgaeCategory").equals("0")) {
							usageCategory = "NA";
						} else {
							usageCategory = rs1.getString("usgaeCategory");
						}
						if(rs1.getString("SaleDate")!=null)
						{
							SaleDate=rs1.getString("SaleDate");
						}
						else
						{
							SaleDate="NA";
						}
						if(rs1.getString("WarrantyType")!=null)
						{
							WarrantyType=rs1.getString("WarrantyType");
						}
						else
						{
							WarrantyType="NA";
						}
						//CR447.en
						//CR490.sn
						if(rs1.getString("Pin_Code")==null || "undefined".equals(rs1.getString("Pin_Code")))
						{
							Pin_Code="NA";
						}
						else
						{
							Pin_Code=rs1.getString("Pin_Code");
						}
						if(rs1.getString("Sub_District")==null || "undefined".equals(rs1.getString("Sub_District")))
						{
							Sub_District="NA";
						}
						else
						{
							Sub_District=rs1.getString("Sub_District");
						}
						//CR490.en
						String[] data1 = { SerialNumber, profile, model, Zone, DealerName, Owner, tmh, version,
								Pkt_Created_TS, Pkt_Recd_TS, Roll_Off_Date, Installed_date, Lat, Lon, City, State,
								device_status, SIM_No, networkprovider, renew_state, country, plant, extended_warranty,
								Comm_State, Comm_District, Comm_City, Comm_Address, machineType, NIP, mipVersion,
								BP_code , altitude , customerContact, usageCategory,SaleDate,WarrantyType,Pin_Code,Sub_District };
					
						writer.writeNext(data1);
					}
					loopFlag = loopFlag + 5000;
				}
				writer.close();
			}

			return "Succuss";

		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception e " + e.getMessage());
			return e.getMessage();
		} finally {
			try {

				if (stmt != null)
					stmt.close();

				if (stmt1 != null)
					stmt1.close();

				if (connection != null)
					connection.close();

			}

			catch (SQLException e) {
				fLogger.fatal("SQLException :" + e);
			}
		}

	}

}
