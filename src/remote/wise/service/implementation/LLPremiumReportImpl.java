package remote.wise.service.implementation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import com.opencsv.CSVWriter;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;

public class LLPremiumReportImpl {

	public String reportForLLPremiumEligibleVins() {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement stmt = null;
		Statement stmt1 = null;
		int count = 0;
		int loopFlag = 0;
		try {
			connection = connFactory.getConnection();
			if (connection == null) {
				iLogger.info(" mysql connection" + connection);
				return "Fail Update the Records due to sql connection issues.!";
			} else {

				stmt = connection.createStatement();
				String countQuery = "select count(*) from (select aos1.serial_number,ag.Asseet_Group_Name as profile,at.asset_type_name as model,croe.Zone ,croe.Dealer_Name,croe.CustomerName,croe.Customer_Mobile,croe.Comm_City,croe.Comm_District,croe.Comm_State, croe.Comm_Address,croe.Installed_date,croe.Pkt_Created_TS,croe.Pkt_Recd_TS, croe.Roll_Off_Date , croe.lAT ,croe.lON ,croe.Renewal_date ,croe.Renew_state from (select serial_number,Asset_Type_ID,Asset_Group_ID from asset_owner_snapshot where account_id in (select account_id from account_tenancy where tenancy_id in ("+100+"))) as aos1  inner join (select * from asset where premFlag = '"+0+"') a ON a.serial_number = aos1.serial_number left outer  JOIN (select serial_number ,Ownership_Start_Date from asset_owner_snapshot aos where aos.account_type = 'Dealer') aos2 ON aos1.serial_number = aos2.serial_number  inner join (select * from asset_type where Asset_Type_Code in (select Asset_Type_Code from LLPremiumModels)) at ON aos1.Asset_Type_ID = at.Asset_Type_ID inner join asset_group ag ON aos1.Asset_Group_ID = ag.Asset_Group_ID  left outer join  com_rep_oem_enhanced croe ON  aos1.serial_number=croe.serial_number) aa";
				
				ResultSet rs = stmt.executeQuery(countQuery);

				while (rs.next()) {
					count = rs.getInt("count(*)");
					iLogger.info("Count of LLPremiumEligible Machines " + count);
				}

				File file = new File("/user/JCBLiveLink/LLPremiumRepors/LLPremiumEligibleReport.csv");
				FileWriter outputfile = new FileWriter(file);

				CSVWriter writer = new CSVWriter(outputfile);
				String[] header = { "Machine no", "Profile", "Model", "Zone", "Dealer", "customer", "Customer no",
						"Vin city", "Vin  District", "Vin State", "Vin address", "installed date", "Pkt_Created_TS",
						"Pkt_Recd_TS", "Roll_Off_Date", "Lat", "long", "LL renewal date", "renewal status" };

				writer.writeNext(header);
				while (loopFlag < count) {
					stmt1 = connection.createStatement();

//				String	query =  "select aos1.serial_number,ag.Asseet_Group_Name as profile,at.asset_type_name as model,croe.Zone ,croe.Dealer_Name,croe.CustomerName,croe.Customer_Mobile,"
//						           + " croe.Comm_City,croe.Comm_District,croe.Comm_State, croe.Comm_Address,croe.Installed_date,croe.Pkt_Created_TS,croe.Pkt_Recd_TS, croe.Roll_Off_Date , croe.lAT ,croe.lON ,croe.Renewal_date ,croe.Renew_state from (select serial_number,Asset_Type_ID,Asset_Group_ID from asset_owner_snapshot where account_id in (select account_id from account_tenancy where tenancy_id in ("+100+"))) as aos1" 
//									+" inner join (select * from asset where premFlag = '"+0+"') a ON a.serial_number = aos1.serial_number" 
//									+" inner join (select serial_number,max(SubsEndDate) as Renewal_Date  from asset_renewal_data group by serial_number) ard ON aos1.serial_number = ard.serial_number"
//									+" inner join (select * from asset_type where Asset_Type_Code in (select Asset_Type_Code from LLPremiumModels)) at ON aos1.Asset_Type_ID = at.Asset_Type_ID"
//									+" inner join asset_group ag ON aos1.Asset_Group_ID = ag.Asset_Group_ID"
//									+" left outer join  com_rep_oem_enhanced croe ON  aos1.serial_number=croe.serial_number limit "+ loopFlag + "," + "5000";

					String	query =  "select aos1.serial_number,ag.Asseet_Group_Name as profile,at.asset_type_name as model,croe.Zone ,croe.Dealer_Name,croe.CustomerName,croe.Customer_Mobile,"
					           + " croe.Comm_City,croe.Comm_District,croe.Comm_State, croe.Comm_Address,croe.Installed_date,croe.Pkt_Created_TS,croe.Pkt_Recd_TS, croe.Roll_Off_Date , croe.lAT ,croe.lON ,croe.Renewal_date ,croe.Renew_state from (select serial_number,Asset_Type_ID,Asset_Group_ID from asset_owner_snapshot where account_id in (select account_id from account_tenancy where tenancy_id in ("+100+"))) as aos1" 
								+" inner join (select * from asset where premFlag = '"+0+"') a ON a.serial_number = aos1.serial_number" 
								+" left outer  JOIN (select serial_number ,Ownership_Start_Date from asset_owner_snapshot aos where aos.account_type = 'Dealer') aos2 ON aos1.serial_number = aos2.serial_number"
								+" inner join (select * from asset_type where Asset_Type_Code in (select Asset_Type_Code from LLPremiumModels)) at ON aos1.Asset_Type_ID = at.Asset_Type_ID"
								+" inner join asset_group ag ON aos1.Asset_Group_ID = ag.Asset_Group_ID"
								+" left outer join  com_rep_oem_enhanced croe ON  aos1.serial_number=croe.serial_number limit "+ loopFlag + "," + "5000";
					
					iLogger.info(query);

					ResultSet rs1 = stmt1.executeQuery(query);

					while (rs1.next()) {
						
						if(rs1.getString("Dealer_Name") != null){
						String SerialNumber;
						String profile = null;
						String model = null;
						String Zone = null;
						String DealerName = null;
						String CustomerName = null;
						String CustomerNumber = null;
						String Comm_City;
						String Comm_District;
						String Comm_State;
						String Comm_Address;
						String Installed_date;
						String Pkt_Created_TS;
						String Pkt_Recd_TS;
						String Roll_Off_Date;

						String Lat;
						String Lon;
						String renewalDate;
						String renew_state;
					

						SerialNumber = rs1.getString("serial_number");
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
						if (rs1.getString("CustomerName") != null) {
							CustomerName = rs1.getString("CustomerName");
						} else {
							CustomerName = "NA";
						}
						if (rs1.getString("Customer_Mobile") != null) {
							CustomerNumber = rs1.getString("Customer_Mobile");
						} else {
							CustomerNumber = "NA";
						}

						if (rs1.getString("Pkt_Created_TS") != null) {
							Pkt_Created_TS = rs1.getString("Pkt_Created_TS").split("\\.")[0];
						} else {
							Pkt_Created_TS = "NA";
						}
						if (rs1.getString("Pkt_Recd_TS") != null) {
							Pkt_Recd_TS = rs1.getString("Pkt_Recd_TS").split("\\.")[0];
						} else {
							Pkt_Recd_TS = "NA";
						}
						if (rs1.getString("Roll_Off_Date") != null) {
							Roll_Off_Date = rs1.getString("Roll_Off_Date").split("\\.")[0];
						} else {
							Roll_Off_Date = "NA";
						}
						if (rs1.getString("Installed_date") != null) {
							Installed_date = rs1.getString("Installed_date").split("\\.")[0];
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
						if (rs1.getString("renew_state") != null) {
							renew_state = rs1.getString("renew_state");
						} else {
							renew_state = "NA";
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
						if (rs1.getString("Renewal_date") != null) {
							renewalDate = rs1.getString("Renewal_date").split("\\.")[0];
						} else {
							renewalDate = "NA";
						}
						

						String[] data1 = { SerialNumber, profile, model, Zone, DealerName, CustomerName, CustomerNumber,
								Comm_City, Comm_District, Comm_State, Comm_Address, Installed_date, Pkt_Created_TS,
								Pkt_Recd_TS, Roll_Off_Date, Lat, Lon, renewalDate, renew_state };

						writer.writeNext(data1);
					}
					loopFlag = loopFlag + 5000;
				} }
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

	public String insertIntoCSVFile() {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement stmt = null;
		Statement stmt1 = null;
		int count = 0;
		int loopFlag = 0;
		try {
			connection = connFactory.getConnection();
			if (connection == null) {
				iLogger.info(" mysql connection" + connection);
				return "Fail Update the Records due to sql connection issues.!";
			} else {

				stmt = connection.createStatement();
				String countQuery = "select count(DISTINCT lp.Serial_Number) from LLPremiumSubs lp , com_rep_oem_enhanced come where lp.Serial_Number = come.Serial_Number";
				ResultSet rs = stmt.executeQuery(countQuery);

				while (rs.next()) {
					count = rs.getInt("count(DISTINCT lp.Serial_Number)");
					iLogger.info("Count of LLPremiumSubs Machines " + count);
				}
				String sourceDir = null;
				Properties prop = new Properties();
				try {
					prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
					sourceDir= prop.getProperty("LLPremiumReportsPath");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					fLogger.fatal("issue in while getting path from configuration path"
							+ e1.getMessage());
				}
				File file = new File(sourceDir+"/LivelinkPremiumStatus.csv");
				//File file = new File("C:\\unAllocatedReport\\LLPremiumReports\\LivelinkPremiumStatus.csv");
				iLogger.info("path for LLPremiumEligibleReport" + file);
				FileWriter outputfile = new FileWriter(file);
				CSVWriter writer = new CSVWriter(outputfile);
				String[] header = { "Machine no", "Profile", "Model", "Zone", "Dealer", "customer", "Customer no",
						"Vin city", "Vin  District", "Vin State", "Vin address", "installed date", "Pkt_Created_TS",
						"Pkt_Recd_TS", "Roll_Off_Date", "Lat", "long", "LL renewal date", "renewal status",
						"Premium subscription date", "Premium end date" ,"Premium PunchingDate","Sale Date" };

				writer.writeNext(header);
				while (loopFlag < count) {
					stmt1 = connection.createStatement();

//					String query = "select max(lp.UpdatedOn) ,come.Serial_Number ,Profile ,Model , Zone ,Dealer_Name,CustomerName,Customer_Mobile ,Comm_City,Comm_District,Comm_State, Comm_Address,Installed_date,Pkt_Created_TS,Pkt_Recd_TS, Roll_Off_Date , lAT ,lON ,Renewal_date ,Renew_state,lp.LLPremStartdate,lp.LLPremEnddate "
//							+ "from LLPremiumSubs lp , com_rep_oem_enhanced come where lp.Serial_Number = come.Serial_Number and NoOfyears = 1  group by lp.Serial_Number  limit "+ loopFlag + "," + "5000";

					String query = "SELECT " + 
			                "    lp.serial_number," + 
			                "    MAX(lp.UpdatedOn) AS updated_on," + 
			                "    DATE_FORMAT(MAX(lp.LLPremStartdate), '%Y-%m-%d') AS LLPremStartdate," + 
			                "    DATE_FORMAT(MAX(lp.LLPremEnddate), '%Y-%m-%d') AS LLPremEnddate," + 
			                "    ANY_VALUE(Model) AS Model," + 
			                "    ANY_VALUE(Profile) AS Profile," + 
			                "    ANY_VALUE(Zone) AS Zone," + 
			                "    ANY_VALUE(Dealer_Name) AS Dealer_Name," + 
			                "    ANY_VALUE(CustomerName) AS CustomerName," + 
			                "    ANY_VALUE(Customer_Mobile) AS Customer_Mobile," + 
			                "    ANY_VALUE(Comm_City) AS Comm_City," + 
			                "    ANY_VALUE(Comm_District) AS Comm_District," + 
			                "    ANY_VALUE(Comm_State) AS Comm_State," + 
			                "    ANY_VALUE(Comm_Address) AS Comm_Address," + 
			                "    ANY_VALUE(Installed_date) AS Installed_date," + 
			                "    ANY_VALUE(Pkt_Created_TS) AS Pkt_Created_TS," + 
			                "    ANY_VALUE(Pkt_Recd_TS) AS Pkt_Recd_TS," + 
			                "    ANY_VALUE(Roll_Off_Date) AS Roll_Off_Date," + 
			                "    DATE_FORMAT(ANY_VALUE(com.SaleDate), '%Y-%m-%d') AS SaleDate, "+
			                "    ANY_VALUE(lAT) AS lAT," + 
			                "    ANY_VALUE(lON) AS lON," + 
			                "    ANY_VALUE(Renewal_date) AS Renewal_date," + 
			                "    ANY_VALUE(Renew_state) AS Renew_state," + 
			                "    DATE_FORMAT(ANY_VALUE(lp.LLPremPunchingDate), '%Y-%m-%d') AS LLPremPunchingDate" + 
			                " FROM" + 
			                "    LLPremiumSubs lp," + 
			                "    (SELECT " + 
			                "        serial_number, MAX(UpdatedOn) AS updatedOn" + 
			                "    FROM" + 
			                "        LLPremiumSubs" + 
			                "    GROUP BY serial_number) lp1," + 
			                "    com_rep_oem_enhanced com" + 
			                " WHERE" + 
			                "    lp.Serial_Number = com.Serial_Number" + 
			                "        AND lp.serial_number = lp1.serial_number" + 
			                "        AND lp.UpdatedOn = lp1.updatedOn" + 
			                "        AND lp.NoOfyears > 0" + 
			                " GROUP BY lp.serial_number" + 
			                " LIMIT " + loopFlag + ", 5000";

			iLogger.info(query);


					ResultSet rs1 = stmt1.executeQuery(query);

					while (rs1.next()) {
						String SerialNumber;
						String profile = null;
						String model = null;
						String Zone = null;
						String DealerName = null;
						String CustomerName = null;
						String CustomerNumber = null;
						String Comm_City;
						String Comm_District;
						String Comm_State;
						String Comm_Address;
						String Installed_date;
						String Pkt_Created_TS;
						String Pkt_Recd_TS;
						String Roll_Off_Date;

						String Lat;
						String Lon;
						String renewalDate;
						String renew_state;
						String premiumStartDate;
						String premiumEndDate;
						String punchingDate;
						String saleDate;

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
						if (rs1.getString("CustomerName") != null) {
							CustomerName = rs1.getString("CustomerName");
						} else {
							CustomerName = "NA";
						}
						if (rs1.getString("Customer_Mobile") != null) {
							CustomerNumber = rs1.getString("Customer_Mobile");
						} else {
							CustomerNumber = "NA";
						}

						if (rs1.getString("Pkt_Created_TS") != null) {
							Pkt_Created_TS = rs1.getString("Pkt_Created_TS").split("\\.")[0];
						} else {
							Pkt_Created_TS = "NA";
						}
						if (rs1.getString("Pkt_Recd_TS") != null) {
							Pkt_Recd_TS = rs1.getString("Pkt_Recd_TS").split("\\.")[0];
						} else {
							Pkt_Recd_TS = "NA";
						}
						if (rs1.getString("Roll_Off_Date") != null) {
							Roll_Off_Date = rs1.getString("Roll_Off_Date").split("\\.")[0];
						} else {
							Roll_Off_Date = "NA";
						}
						if (rs1.getString("Installed_date") != null) {
							Installed_date = rs1.getString("Installed_date").split("\\.")[0];
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
						if (rs1.getString("renew_state") != null) {
							renew_state = rs1.getString("renew_state");
						} else {
							renew_state = "NA";
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
						if (rs1.getString("Renewal_date") != null) {
							renewalDate = rs1.getString("Renewal_date").split("\\.")[0];
						} else {
							renewalDate = "NA";
						}
						if (rs1.getString("LLPremStartdate") != null) {
							premiumStartDate = rs1.getString("LLPremStartdate").split("\\.")[0];
						} else {
							premiumStartDate = "NA";
						}
						if (rs1.getString("LLPremEnddate") != null) {
							premiumEndDate = rs1.getString("LLPremEnddate").split("\\.")[0];
						} else {
							premiumEndDate = "NA";
						}

						if (rs1.getString("LLPremPunchingDate") != null) {
							punchingDate = rs1.getString("LLPremPunchingDate").split("\\.")[0];
						} else {
							punchingDate = "NA";
						}
						if(rs1.getString("saleDate")!=null) {
							saleDate=rs1.getString("saleDate");
						}
						else
						{
							saleDate="NA";
						}
						String[] data1 = { SerialNumber, profile, model, Zone, DealerName, CustomerName, CustomerNumber,
								Comm_City, Comm_District, Comm_State, Comm_Address, Installed_date, Pkt_Created_TS,
								Pkt_Recd_TS, Roll_Off_Date, Lat, Lon, renewalDate, renew_state, premiumStartDate,
								premiumEndDate,punchingDate,saleDate };

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

	 public List<HashMap<String, String>> reportForDealerLLPremiumEligibleVins(LinkedHashMap<String, Object> reqObj) {
	        List<HashMap<String, String>> result = new ArrayList<>();
	 
	        Logger flogger = FatalLoggerClass.logger;
	        Logger iLogger = InfoLoggerClass.logger;
	        ConnectMySQL connFactory = new ConnectMySQL();
	        Connection connection = null;
	        Statement stmt = null;
	 
	        try {
	        	String accountIdStringList=new DateUtil().getAccountListForTheTenancy((List<Integer>) reqObj.get("tenancyIdList"));
	            connection = connFactory.getConnection();
	            if (connection == null) {
	            	iLogger.info("mysql connection" + connection);
	                return null; 
	            }
	            else if (accountIdStringList == null || accountIdStringList.isEmpty()) {
	                iLogger.info("accountIdStringList is null or empty. Skipping query execution.");
	                flogger.info("accountIdStringList is null or empty. Skipping query execution.");
	                return result;
	            } 
	            else {
	                stmt = connection.createStatement();
	              String query="SELECT * FROM (SELECT aos1.serial_number, aos1.Account_ID, ag.Asseet_Group_Name AS profile, at.asset_type_name AS model, croe.Zone, croe.Dealer_Name, croe.CustomerName, croe.Customer_Mobile, croe.Comm_City, croe.Comm_District, croe.Comm_State, croe.Comm_Address, croe.Installed_date, croe.Pkt_Created_TS, croe.Pkt_Recd_TS, croe.Roll_Off_Date, croe.lAT, croe.lON, croe.Renewal_date, croe.Renew_state FROM (SELECT serial_number, Asset_Type_ID, Asset_Group_ID, Account_ID FROM asset_owner_snapshot WHERE account_id IN ("+ accountIdStringList +")) aos1 INNER JOIN (SELECT * FROM asset WHERE premFlag = 0) a ON a.serial_number = aos1.serial_number LEFT OUTER JOIN (SELECT serial_number, Ownership_Start_Date FROM asset_owner_snapshot aos WHERE aos.account_type = 'Dealer') aos2 ON aos1.serial_number = aos2.serial_number INNER JOIN (SELECT * FROM asset_type WHERE Asset_Type_Code IN (SELECT Asset_Type_Code FROM LLPremiumModels)) at ON aos1.Asset_Type_ID = at.Asset_Type_ID INNER JOIN asset_group ag ON aos1.Asset_Group_ID = ag.Asset_Group_ID LEFT OUTER JOIN com_rep_oem_enhanced croe ON aos1.serial_number = croe.serial_number) aa";
	                iLogger.info(query);
	                ResultSet rs = stmt.executeQuery(query);
	 
	                while (rs.next()) {
	                    HashMap<String, String> record = new HashMap<>();
	                    record.put("serial_number", rs.getString("Serial_Number"));
	                    record.put("profile", rs.getString("profile"));
	                    record.put("model", rs.getString("model"));
	                    record.put("Zone", rs.getString("Zone"));
	                    record.put("Dealer_Name", rs.getString("Dealer_Name"));
	                    record.put("CustomerName", rs.getString("CustomerName"));
	                    record.put("Customer_Mobile", rs.getString("Customer_Mobile"));
	                    record.put("Comm_City", rs.getString("Comm_City"));
	                    record.put("Comm_District", rs.getString("Comm_District"));
	                    record.put("Comm_State", rs.getString("Comm_State"));
	                    record.put("Comm_Address", rs.getString("Comm_Address"));
	                    record.put("Installed_date", rs.getString("Installed_date"));
	                    record.put("Pkt_Created_TS", rs.getString("Pkt_Created_TS"));
	                    record.put("Pkt_Recd_TS", rs.getString("Pkt_Recd_TS"));
	                    record.put("Roll_Off_Date", rs.getString("Roll_Off_Date"));
	                    record.put("LAT", rs.getString("LAT"));
	                    record.put("LON", rs.getString("LON"));
	                    record.put("Renewal_date", rs.getString("Renewal_date"));
	                    record.put("Renew_state", rs.getString("Renew_state"));
	                    record.put("Account_ID", rs.getString("Account_ID"));
	                    result.add(record);
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null; 
	        } finally {
	           
	            try {
	                if (stmt != null) {
	                    stmt.close();
	                }
	                if (connection != null) {
	                    connection.close();
	                }
	            } catch (SQLException e) {
	                e.printStackTrace();
	                flogger.error("Exception:"+e.getMessage());
	            }
	        }
	 
	        return result; 
	    }
	 
	public List<HashMap<String, String>> reportForDealerLLPremiumVins(LinkedHashMap<String, Object> reqObj,
				String startDate, String endDate,String dateType) {
		 List<HashMap<String, String>> result = new ArrayList<>();
		 
		 Logger flogger = FatalLoggerClass.logger;
		 Logger iLogger = InfoLoggerClass.logger;
		 ConnectMySQL connFactory = new ConnectMySQL();
		 Connection connection = null;
		 Statement stmt = null;

 try {
     String accountIdStringList = new DateUtil().getAccountListForTheTenancy((List<Integer>) reqObj.get("tenancyIdList"));
     connection = connFactory.getConnection();
     if (connection == null) {
         iLogger.info("mysql connection" + connection);
         return null;
     } else if (accountIdStringList == null || accountIdStringList.isEmpty()) {
         iLogger.info("accountIdStringList is null or empty. Skipping query execution.");
         flogger.info("accountIdStringList is null or empty. Skipping query execution.");
         return result;
     }else {
         StringBuilder queryBuilder = new StringBuilder();
         queryBuilder.append("SELECT ")
         .append("    lp.serial_number, ")
         .append("    MAX(lp.UpdatedOn) AS updated_on, ")
         .append("    DATE_FORMAT(MAX(lp.LLPremStartdate), '%Y-%m-%d') AS LLPremStartdate, ")
         .append("    DATE_FORMAT(MAX(lp.LLPremEnddate), '%Y-%m-%d') AS LLPremEnddate, ")
         .append("    ANY_VALUE(Model) AS Model, ")
         .append("    ANY_VALUE(Profile) AS Profile, ")
         .append("    ANY_VALUE(Zone) AS Zone, ")
         .append("    ANY_VALUE(Dealer_Name) AS Dealer_Name, ")
         .append("    ANY_VALUE(CustomerName) AS CustomerName, ")
         .append("    ANY_VALUE(Customer_Mobile) AS Customer_Mobile, ")
         .append("    ANY_VALUE(Comm_City) AS Comm_City, ")
         .append("    ANY_VALUE(Comm_District) AS Comm_District, ")
         .append("    ANY_VALUE(Comm_State) AS Comm_State, ")
         .append("    ANY_VALUE(Comm_Address) AS Comm_Address, ")
         .append("    ANY_VALUE(Installed_date) AS Installed_date, ")
         .append("    ANY_VALUE(Pkt_Created_TS) AS Pkt_Created_TS, ")
         .append("    ANY_VALUE(Pkt_Recd_TS) AS Pkt_Recd_TS, ")
         .append("    ANY_VALUE(Roll_Off_Date) AS Roll_Off_Date, ")
         .append("    ANY_VALUE(lAT) AS lAT, ")
         .append("    ANY_VALUE(lON) AS lON, ")
         .append("    ANY_VALUE(Renewal_date) AS Renewal_date, ")
         .append("    ANY_VALUE(Renew_state) AS Renew_state, ")
         .append("    DATE_FORMAT(ANY_VALUE(lp.LLPremPunchingDate), '%Y-%m-%d') AS LLPremPunchingDate, ")
         .append("    DATE_FORMAT(ANY_VALUE(com.SaleDate), '%Y-%m-%d') AS SaleDate ")
         .append("FROM ")
         .append("    LLPremiumSubs lp, ")
         .append("    com_rep_oem_enhanced com ")
         .append("JOIN ")
         .append("    asset_owner_snapshot aos ON com.serial_number = aos.serial_number  ")
         .append("WHERE ")
         .append("    aos.Account_ID IN (").append(accountIdStringList).append(") ")
         .append("    AND lp.Serial_Number = com.Serial_Number ")
         .append("    AND lp.NoOfyears > 0 ");

     if (startDate != null && endDate != null && !endDate.isEmpty() && !startDate.isEmpty() && endDate.compareTo(startDate) >= 0) {
         if ("LLpunchingDate".equalsIgnoreCase(dateType)) {
             queryBuilder.append("    AND lp.LLPremPunchingDate >= '").append(startDate).append("' ")
                 .append("    AND lp.LLPremPunchingDate <= '").append(endDate).append("' ");
         } else if ("LLpremDate".equalsIgnoreCase(dateType)) {
             queryBuilder.append("    AND lp.LLPremStartdate >= '").append(startDate).append("' ")
                 .append("    AND lp.LLPremStartdate <= '").append(endDate).append("' ");
         }
     }

     queryBuilder.append("GROUP BY lp.serial_number;");


         String query = queryBuilder.toString();
         iLogger.info(query);

         stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(query);

         while (rs.next()) {
             HashMap<String, String> record = new HashMap<>();
             record.put("Serial_Number", rs.getString("Serial_Number"));
             record.put("Profile", rs.getString("Profile"));
             record.put("Model", rs.getString("Model"));
             record.put("Zone", rs.getString("Zone"));
             record.put("Dealer_Name", rs.getString("Dealer_Name"));
             record.put("CustomerName", rs.getString("CustomerName"));
             record.put("Customer_Mobile", rs.getString("Customer_Mobile"));
             record.put("Comm_City", rs.getString("Comm_City"));
             record.put("Comm_District", rs.getString("Comm_District"));
             record.put("Comm_State", rs.getString("Comm_State"));
             record.put("Comm_Address", rs.getString("Comm_Address"));
             record.put("Installed_date", rs.getString("Installed_date"));
             record.put("Pkt_Created_TS", rs.getString("Pkt_Created_TS"));
             record.put("Pkt_Recd_TS", rs.getString("Pkt_Recd_TS"));
             record.put("Roll_Off_Date", rs.getString("Roll_Off_Date"));
             record.put("LAT", rs.getString("LAT"));
             record.put("LON", rs.getString("LON"));
             record.put("Renewal_date", rs.getString("Renewal_date"));
             record.put("Renew_state", rs.getString("Renew_state"));
             record.put("LLPremStartdate", rs.getString("LLPremStartdate"));
             record.put("LLPremEnddate", rs.getString("LLPremEnddate"));
             record.put("LLPremPunchingDate", rs.getString("LLPremPunchingDate"));
             record.put("SaleDate", rs.getString("SaleDate"));
             result.add(record);
         }
     }
 } catch (Exception e) {
     e.printStackTrace();
     return null;
 } finally {
     try {
         if (stmt != null) {
             stmt.close();
         }
         if (connection != null) {
             connection.close();
         }
     } catch (SQLException e) {
         e.printStackTrace();
         flogger.error("Exception:" + e.getMessage());
     }
 }

 return result;
}

}
