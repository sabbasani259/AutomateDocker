package remote.wise.service.implementation;
/**
 * @author Z1007653
 */
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.NonCommunicatingMachinesReportContract;
import remote.wise.service.datacontract.NonCommunicationDrillDownOutputContract;
import remote.wise.service.datacontract.ZoneOrDealerOrInstallationWiseSummaryOutputContract;
import remote.wise.util.ConnectMySQL;

public class NonCommunicatingMachinesReportImpl {
	
	public String clearData(String tableName) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		if(!tableName.equals("zone_wise_non_comm_machine_indian") && !tableName.equals("dealer_wise_non_comm_machine_indian") &&
				!tableName.equals("zone_wise_non_comm_machine_saarc") && !tableName.equals("dealer_wise_non_comm_machine_saarc") &&
				!tableName.equals("installation_wise_non_comm_machine_indian") && !tableName.equals("installation_wise_non_comm_machine_saarc")) { return "Invalid Request: "+tableName; }
		
		Connection connection = null;
		Statement statement = null;
		
		try {
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			
			//TODO - Execute the update query as per the tableName requested
			int status = statement.executeUpdate("UPDATE " + tableName + " SET one_to_seven = null, eight_to_fifteen = null, sixteen_to_thirty = null, thirtyone_to_sixty = null, sixty_plus = null, grand_total = null WHERE unique_id > 0");
			iLogger.info("NonCommunicatingMachinesReportImpl : clearData : data cleared : rows effected - " + status);
			
		} catch (Exception e){
			Writer writer = new StringWriter();
			PrintWriter ps = new PrintWriter(writer);
			e.printStackTrace(ps);
			fLogger.fatal("NonCommunicatingMachinesReportImpl : clearData -> Exception caught:\n" + ps.toString());
			
			return "FAILURE";
		} finally {
			try {
	            if (statement != null)
	            statement.close();
	         } catch (Exception ex) {
	        	 fLogger.fatal("NonCommunicatingMachinesReportImpl : clearData -> Exception caught while closing statement " + ex.getMessage());
	         }
	         try {
	            if (connection != null)
	            connection.close();
	         } catch (Exception ex) {
	        	fLogger.fatal("NonCommunicatingMachinesReportImpl : clearData -> Exception caught while closing connections " + ex.getMessage());
	         }
		}
		
		return "SUCCESS";
	}
	
	public String zonalUpdate(String range, String nation) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		int tillDay = -1;
		int fromDay = -1;
		
		boolean indianNationFlag = false;
		
		//variable to store the data to be updated
		List<NonCommunicatingMachinesReportContract> dataToUpdate = new ArrayList<NonCommunicatingMachinesReportContract>();
		
		//check if the range is valid and exactly the same as columns name in zone_wise_non_comm_machine table
		if(range.equals("one_to_seven")) { tillDay = 0; fromDay = 7; }
		else if(range.equals("eight_to_fifteen")) { tillDay = 7; fromDay = 15; }
		else if(range.equals("sixteen_to_thirty")) { tillDay = 15; fromDay = 30; }
		else if(range.equals("thirtyone_to_sixty")) { tillDay = 30; fromDay = 60; }
		else if(range.equals("sixty_plus")) { tillDay = 60; }
		else { return "Unknown Input"; }
		
		//check if nation is valid input or not		
		if(nation != null && nation.toLowerCase().equals("indian")) {
			indianNationFlag = true;
		}
		else if( nation !=null && nation.toLowerCase().equals("saarc")) {
			indianNationFlag = false;
		}
		else {
			return "Invalid Nation";
		}
		
		
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		
		try {
			NonCommunicatingMachinesReportContract data = new NonCommunicatingMachinesReportContract();
			
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			
			/**
			 * Fetch required data from com_rep_oem_enhanced_20210110and save it to List<NonCommunicatingMachinesReportContract> dataToUpdate
			 */
			String selectQuery="SELECT Zone as zone,extended_warranty as warranty,count(Serial_Number) as machine_count, countrycode "
					+ "FROM com_rep_oem_enhanced_20210110 WHERE ";
			
			if( fromDay != -1)  selectQuery += "Pkt_Recd_TS >= DATE_ADD(CURDATE(), INTERVAL -" + fromDay + " DAY) AND ";
			selectQuery += "Pkt_Recd_TS < DATE_ADD(CURDATE(), INTERVAL -" + tillDay + " DAY) ";
			
			//Zakir : 2020-12-16 : updating implementation
			if(indianNationFlag) selectQuery += "AND countrycode = '+91' ";
			else selectQuery += "AND countrycode != '+91' AND SIM_No like '2040%' ";
			
			selectQuery += "AND  Renewal_Flag = 1 group by Zone , extended_warranty, countrycode ";
			
			
			
			//TODO - remove after testing..
			System.out.println("Query: " + selectQuery);
			
			rs = statement.executeQuery(selectQuery);
			while(rs.next())
			{
				data = new NonCommunicatingMachinesReportContract();
				data.setKey((rs.getString("zone") == null)? "(blank)": rs.getString("zone"));
				data.setWarranty(rs.getString("warranty"));
				data.setCount(rs.getString("machine_count"));
				data.setCountryCode(rs.getString("countrycode"));				
				dataToUpdate.add(data);
			}
			
			//TODO - Remove
			System.out.println(dataToUpdate.toString());
			
			//Iterate through the dataToUpdate for further updates in table zone_wise_non_comm_machine
			String insertUpdateQuery = "";
			for(NonCommunicatingMachinesReportContract record: dataToUpdate) {
				if(record.getKey() != null) {
					boolean zoneExistInTable = false;
					int uniqueId = 0; 		//To hold the primary key in case of UPDATE in table
					int maxUniqueId = 0;	//To hold the max primary key in case of INSERT in table 
					
					//1. Check if zone is already present in table: zone_wise_non_comm_machine
					rs1 = statement.executeQuery("SELECT unique_id FROM zone_wise_non_comm_machine_"+nation+" WHERE zone = '"+record.getKey()+"' and warranty = '"+record.getWarranty()+"'");
					while(rs1.next()) {
						//if zone is already present, change zoneExist flag to true to make Update query
						//else, make insert query
						if(rs1.getString("unique_id") != null && Integer.parseInt(rs1.getString("unique_id")) > 0) {
							zoneExistInTable = true;
							uniqueId = Integer.parseInt(rs1.getString("unique_id"));
						}
					}
					
					System.out.println("Got uniqueId: "+uniqueId);
					
					//2. Make an Update or Insert query, accordingly
					if(zoneExistInTable) {
						//Update query with uniqueId
						insertUpdateQuery = "UPDATE zone_wise_non_comm_machine_"+nation+" SET " + range + " = " + record.getCount() + " WHERE (unique_id = " + uniqueId + ")";
					}
					else {
						//Insert query
						insertUpdateQuery = "INSERT INTO zone_wise_non_comm_machine_"+nation+" (zone, " + range + ", warranty, country_code) VALUES ('" 
														+ record.getKey() + "', " + record.getCount()+ ",'" + record.getWarranty() + "','" + record.getCountryCode() + "')";
						
						//Also fetch the maxUniqueId in order to maintain grandTotal
						rs2 = statement.executeQuery("SELECT max(unique_id) as max_unique_id FROM zone_wise_non_comm_machine_"+nation);
						while(rs2.next()) {
							//This is to keep the unique id that is going to be inserted into table, in order to update grand total 
							if(rs2.getString("max_unique_id") != null && Integer.parseInt(rs2.getString("max_unique_id")) > 0) {
								maxUniqueId = Integer.parseInt(rs2.getString("max_unique_id"));
							}
						}
					}
					
					//3. Update or Insert into table: zone_wise_non_comm_machine
					int status = statement.executeUpdate(insertUpdateQuery);
					iLogger.info("NonCommunicatingMachinesReportImpl: zonalUpdate : day wise records updated -> " + status);
					
					System.out.println("NonCommunicatingMachinesReportImpl: zonalUpdate : day wise records updated -> " + status);
					
					//4. Fetch Grand Total
					int grandTotal = 0;
					String grandTotalUpdateQuery;
					
					//if zone is already present in table, fetch the sum of all columns
					if(zoneExistInTable) {
						String grandTotalQuery = "SELECT SUM(IFNULL(one_to_seven,0) + IFNULL(eight_to_fifteen,0) + IFNULL(sixteen_to_thirty,0) + IFNULL(thirtyone_to_sixty,0) + IFNULL(sixty_plus,0)) AS total FROM zone_wise_non_comm_machine_"+nation+" " +
								"WHERE unique_id = "+uniqueId;
						rs3 = statement.executeQuery(grandTotalQuery);
						while(rs3.next()) {
							if(rs3.getString("total") != null && Integer.parseInt(rs3.getString("total")) > 0) grandTotal = Integer.parseInt(rs3.getString("total"));
						}
						//TODO - remove
						System.out.println("Got grand Total: " + grandTotal);
						grandTotalUpdateQuery = "UPDATE zone_wise_non_comm_machine_"+nation+" SET grand_total = " + grandTotal + " WHERE (unique_id = " + uniqueId + ")";
					}
					//else consider current record.getCount() as grand total
					else {
						maxUniqueId++;
						grandTotal = Integer.parseInt(record.getCount());
						grandTotalUpdateQuery = "UPDATE zone_wise_non_comm_machine_"+nation+" SET grand_total = " + grandTotal + " WHERE (unique_id = " + maxUniqueId + ")";
					}
					
					//5. Update the grand_total column in table: zone_wise_non_comm_machine
					int updateGrandTotalStatus = statement.executeUpdate(grandTotalUpdateQuery);
					iLogger.info("NonCommunicatingMachinesReportImpl: zonalUpdate : grand total record updated -> " + updateGrandTotalStatus);
					System.out.println("NonCommunicatingMachinesReportImpl: zonalUpdate : grand total record updated -> " + updateGrandTotalStatus);

				}
			}
			
		} catch (Exception e) {
			//TODO - Remove
			e.printStackTrace();
			Writer writer = new StringWriter();
			PrintWriter ps = new PrintWriter(writer);
			e.printStackTrace(ps);
			fLogger.fatal("NonCommunicatingMachinesReportImpl: zonalUpdate : Exception caught\n" + ps.toString());
			
			return "FAILURE";
		}
		finally {
			try{
				if(rs != null)
					rs.close();
				if(rs1 != null)
					rs1.close();
				if(rs2 != null)
					rs2.close();
				if(rs3 != null)
					rs3.close();
				if(statement != null)
					statement.close();
				if(connection != null)
					connection.close();

			}catch (Exception e) {
				fLogger.fatal("NonCommunicatingMachinesReportImpl: zonalUpdate : Exception caught while closing connections - " + e.getMessage());
			}
		}
		
		return "SUCCESS";
	}
	
	public String dealerUpdate(String range, String nation) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		int tillDay = -1;
		int fromDay = -1;
		
		boolean indianNationFlag = false;
		
		//variable to store the data to be updated
		List<NonCommunicatingMachinesReportContract> dataToUpdate = new ArrayList<NonCommunicatingMachinesReportContract>();
		
		//check if the range is valid and exactly the same as columns name in dealer_wise_non_comm_machine table
		if(range.equals("one_to_seven")) { tillDay = 0; fromDay = 7; }
		else if(range.equals("eight_to_fifteen")) { tillDay = 7; fromDay = 15; }
		else if(range.equals("sixteen_to_thirty")) { tillDay = 15; fromDay = 30; }
		else if(range.equals("thirtyone_to_sixty")) { tillDay = 30; fromDay = 60; }
		else if(range.equals("sixty_plus")) { tillDay = 60; }
		else { return "Unknown Input"; }
		
		//check if nation is valid input or not		
		if(nation != null && nation.toLowerCase().equals("indian")) {
			indianNationFlag = true;
		}
		else if( nation !=null && nation.toLowerCase().equals("saarc")) {
			indianNationFlag = false;
		}
		else {
			return "Invalid Nation";
		}
		
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		
		try {
			NonCommunicatingMachinesReportContract data = new NonCommunicatingMachinesReportContract();
			
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			
			/**
			 * Fetch required data from com_rep_oem_enhanced_20210110and save it to List<NonCommunicatingMachinesReportContract> dataToUpdate
			 */
			String selectQuery="SELECT Dealer_Name as dealer,extended_warranty as warranty,count(Serial_Number) as machine_count, countrycode "
					+ "FROM com_rep_oem_enhanced_20210110 WHERE ";
			
			if( fromDay != -1)  selectQuery += "Pkt_Recd_TS >= DATE_ADD(CURDATE(), INTERVAL -" + fromDay + " DAY) AND ";
			selectQuery += "Pkt_Recd_TS < DATE_ADD(CURDATE(), INTERVAL -" + tillDay + " DAY) ";
			
			//Zakir : 2020-12-16 : updating implementation
			if(indianNationFlag) selectQuery += "AND countrycode = '+91' ";
			else selectQuery += "AND countrycode != '+91' AND SIM_No like '2040%' ";
			
			selectQuery += "AND  Renewal_Flag = 1 group by Dealer_Name , extended_warranty, countrycode ";
			
			
			//TODO - remove after testing..
			System.out.println("Query: " + selectQuery);
			
			rs = statement.executeQuery(selectQuery);
			while(rs.next())
			{
				data = new NonCommunicatingMachinesReportContract();
				data.setKey((rs.getString("dealer") == null)? "(blank)": rs.getString("dealer"));
				data.setWarranty(rs.getString("warranty"));
				data.setCount(rs.getString("machine_count"));
				data.setCountryCode(rs.getString("countrycode"));
				
				dataToUpdate.add(data);
			}
			
			//TODO - Remove
			System.out.println(dataToUpdate.toString());
			
			//Iterate through the dataToUpdate for further updates in table dealer_wise_non_comm_machine
			String insertUpdateQuery = "";
			for(NonCommunicatingMachinesReportContract record: dataToUpdate) {
				if(record.getKey() != null) {
					boolean dealerExistInTable = false;
					int uniqueId = 0; 		//To hold the primary key in case of UPDATE in table
					int maxUniqueId = 0;	//To hold the max primary key in case of INSERT in table 
					
					//1. Check if dealer is already present in table: dealer_wise_non_comm_machine
					rs1 = statement.executeQuery("SELECT unique_id FROM dealer_wise_non_comm_machine_"+nation+" WHERE dealer = '"+record.getKey()+"' and warranty = '"+record.getWarranty()+"'");
					while(rs1.next()) {
						//if dealer is already present, change dealerExist flag to true to make Update query
						//else, make insert query
						if(rs1.getString("unique_id") != null && Integer.parseInt(rs1.getString("unique_id")) > 0) {
							dealerExistInTable = true;
							uniqueId = Integer.parseInt(rs1.getString("unique_id"));
						}
					}
					
					System.out.println("Got uniqueId: "+uniqueId);
					
					//2. Make an Update or Insert query, accordingly
					if(dealerExistInTable) {
						//Update query with uniqueId
						insertUpdateQuery = "UPDATE dealer_wise_non_comm_machine_"+nation+" SET " + range + " = " + record.getCount() + " WHERE (unique_id = " + uniqueId + ")";
					}
					else {
						//Insert query
						insertUpdateQuery = "INSERT INTO dealer_wise_non_comm_machine_"+nation+" (dealer, " + range + ", warranty, country_code) VALUES ('" 
											+ record.getKey() + "', " + record.getCount()+ ",'" + record.getWarranty() + "','" + record.getCountryCode() + "')";
						
						//Also fetch the maxUniqueId in order to maintain grandTotal
						rs2 = statement.executeQuery("SELECT max(unique_id) as max_unique_id FROM dealer_wise_non_comm_machine_"+nation+"");
						while(rs2.next()) {
							//This is to keep the unique id that is going to be inserted into table, in order to update grand total 
							if(rs2.getString("max_unique_id") != null && Integer.parseInt(rs2.getString("max_unique_id")) > 0) {
								maxUniqueId = Integer.parseInt(rs2.getString("max_unique_id"));
							}
						}
					}
					
					//3. Update or Insert into table: dealer_wise_non_comm_machine
					int status = statement.executeUpdate(insertUpdateQuery);
					iLogger.info("NonCommunicatingMachinesReportImpl: dealerUpdate : day wise records updated -> " + status);
					
					System.out.println("NonCommunicatingMachinesReportImpl: dealerUpdate : day wise records updated -> " + status);
					
					//4. Fetch Grand Total
					int grandTotal = 0;
					String grandTotalUpdateQuery;
					
					//if dealer is already present in table, fetch the sum of all columns
					if(dealerExistInTable) {
						String grandTotalQuery = "SELECT SUM(IFNULL(one_to_seven,0) + IFNULL(eight_to_fifteen,0) + IFNULL(sixteen_to_thirty,0) + IFNULL(thirtyone_to_sixty,0) + IFNULL(sixty_plus,0)) AS total FROM dealer_wise_non_comm_machine_"+nation+" " +
								"WHERE unique_id = "+uniqueId;
						rs3 = statement.executeQuery(grandTotalQuery);
						while(rs3.next()) {
							if(rs3.getString("total") != null && Integer.parseInt(rs3.getString("total")) > 0) grandTotal = Integer.parseInt(rs3.getString("total"));
						}
						//TODO - remove
						System.out.println("Got grand Total: " + grandTotal);
						grandTotalUpdateQuery = "UPDATE dealer_wise_non_comm_machine_"+nation+" SET grand_total = " + grandTotal + " WHERE (unique_id = " + uniqueId + ")";
					}
					//else consider current record.getCount() as grand total
					else {
						maxUniqueId++;
						grandTotal = Integer.parseInt(record.getCount());
						grandTotalUpdateQuery = "UPDATE dealer_wise_non_comm_machine_"+nation+" SET grand_total = " + grandTotal + " WHERE (unique_id = " + maxUniqueId + ")";
					}
					
					//5. Update the grand_total column in table: dealer_wise_non_comm_machine
					int updateGrandTotalStatus = statement.executeUpdate(grandTotalUpdateQuery);
					iLogger.info("NonCommunicatingMachinesReportImpl: dealerUpdate : grand total record updated -> " + updateGrandTotalStatus);
					System.out.println("NonCommunicatingMachinesReportImpl: dealerUpdate : grand total record updated -> " + updateGrandTotalStatus);

				}
			}
			
		} catch (Exception e) {
			//TODO - Remove
			e.printStackTrace();
			Writer writer = new StringWriter();
			PrintWriter ps = new PrintWriter(writer);
			e.printStackTrace(ps);
			fLogger.fatal("NonCommunicatingMachinesReportImpl: dealerUpdate : Exception caught\n" + ps.toString());
			
			return "FAILURE";
		}
		finally {
			try{
				if(rs != null)
					rs.close();
				if(rs1 != null)
					rs1.close();
				if(rs2 != null)
					rs2.close();
				if(rs3 != null)
					rs3.close();
				if(statement != null)
					statement.close();
				if(connection != null)
					connection.close();

			}catch (Exception e) {
				fLogger.fatal("NonCommunicatingMachinesReportImpl: dealerUpdate : Exception caught while closing connections - " + e.getMessage());
			}
		}
		
		return "SUCCESS";
	}
	
	public String installationDateWiseUpdate(String range, String nation) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		int tillDay = -1;
		int fromDay = -1;
		
		boolean indianNationFlag = false;
		
		//variable to store the data to be updated
		List<NonCommunicatingMachinesReportContract> dataToUpdate = new ArrayList<NonCommunicatingMachinesReportContract>();
		
		//check if the range is valid and exactly the same as columns name in dealer_wise_non_comm_machine table
		if(range.equals("one_to_seven")) { tillDay = 0; fromDay = 7; }
		else if(range.equals("eight_to_fifteen")) { tillDay = 7; fromDay = 15; }
		else if(range.equals("sixteen_to_thirty")) { tillDay = 15; fromDay = 30; }
		else if(range.equals("thirtyone_to_sixty")) { tillDay = 30; fromDay = 60; }
		else if(range.equals("sixty_plus")) { tillDay = 60; }
		else { return "Unknown Input"; }
		
		//check if nation is valid input or not		
		if(nation != null && nation.toLowerCase().equals("indian")) {
			indianNationFlag = true;
		}
		else if( nation !=null && nation.toLowerCase().equals("saarc")) {
			indianNationFlag = false;
		}
		else {
			return "Invalid Nation";
		}
		
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		
		try {
			NonCommunicatingMachinesReportContract data = new NonCommunicatingMachinesReportContract();
			
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			
			/**
			 * Fetch required data from com_rep_oem_enhanced_20210110and save it to List<NonCommunicatingMachinesReportContract> dataToUpdate
			 */
			String selectQuery="SELECT YEAR(Installed_date) AS install_year,extended_warranty as warranty,count(Serial_Number) as machine_count, countrycode "
					+ "FROM com_rep_oem_enhanced_20210110 WHERE ";
			
			if( fromDay != -1)  selectQuery += "Pkt_Recd_TS >= DATE_ADD(CURDATE(), INTERVAL -" + fromDay + " DAY) AND ";
			selectQuery += "Pkt_Recd_TS < DATE_ADD(CURDATE(), INTERVAL -" + tillDay + " DAY) ";
			
			//Zakir : 2020-12-16 : updating implementation
			if(indianNationFlag) selectQuery += "AND countrycode = '+91' ";
			else selectQuery += "AND countrycode != '+91' AND SIM_No like '2040%' ";
			
			selectQuery += "AND  Renewal_Flag = 1 group by install_year , extended_warranty, countrycode ";
			
			
			//TODO - remove after testing..
			System.out.println("Query: " + selectQuery);
			
			rs = statement.executeQuery(selectQuery);
			while(rs.next())
			{
				data = new NonCommunicatingMachinesReportContract();
				data.setKey((rs.getString("install_year") == null)? "(blank)": rs.getString("install_year"));
				data.setWarranty(rs.getString("warranty"));
				data.setCount(rs.getString("machine_count"));
				data.setCountryCode(rs.getString("countrycode"));
				
				dataToUpdate.add(data);
			}
			
			//TODO - Remove
			System.out.println(dataToUpdate.toString());
			
			//Iterate through the dataToUpdate for further updates in table dealer_wise_non_comm_machine
			String insertUpdateQuery = "";
			for(NonCommunicatingMachinesReportContract record: dataToUpdate) {
				if(record.getKey() != null) {
					boolean installYearExistInTable = false;
					int uniqueId = 0; 		//To hold the primary key in case of UPDATE in table
					int maxUniqueId = 0;	//To hold the max primary key in case of INSERT in table 
					
					//1. Check if dealer is already present in table: dealer_wise_non_comm_machine
					rs1 = statement.executeQuery("SELECT unique_id FROM installation_wise_non_comm_machine_"+nation+" WHERE install_year = '"+record.getKey()+"' and warranty = '"+record.getWarranty()+"'");
					while(rs1.next()) {
						//if dealer is already present, change dealerExist flag to true to make Update query
						//else, make insert query
						if(rs1.getString("unique_id") != null && Integer.parseInt(rs1.getString("unique_id")) > 0) {
							installYearExistInTable = true;
							uniqueId = Integer.parseInt(rs1.getString("unique_id"));
						}
					}
					
					System.out.println("Got uniqueId: "+uniqueId);
					
					//2. Make an Update or Insert query, accordingly
					if(installYearExistInTable) {
						//Update query with uniqueId
						insertUpdateQuery = "UPDATE installation_wise_non_comm_machine_"+nation+" SET " + range + " = " + record.getCount() + " WHERE (unique_id = " + uniqueId + ")";
					}
					else {
						//Insert query
						insertUpdateQuery = "INSERT INTO installation_wise_non_comm_machine_"+nation+" (install_year, " + range + ", warranty, country_code) VALUES ('" 
											+ record.getKey() + "', " + record.getCount()+ ",'" + record.getWarranty() + "','" + record.getCountryCode() + "')";
						
						//Also fetch the maxUniqueId in order to maintain grandTotal
						rs2 = statement.executeQuery("SELECT max(unique_id) as max_unique_id FROM installation_wise_non_comm_machine_"+nation+"");
						while(rs2.next()) {
							//This is to keep the unique id that is going to be inserted into table, in order to update grand total 
							if(rs2.getString("max_unique_id") != null && Integer.parseInt(rs2.getString("max_unique_id")) > 0) {
								maxUniqueId = Integer.parseInt(rs2.getString("max_unique_id"));
							}
						}
					}
					
					//3. Update or Insert into table: dealer_wise_non_comm_machine
					int status = statement.executeUpdate(insertUpdateQuery);
					iLogger.info("NonCommunicatingMachinesReportImpl: installationDateWiseUpdate : day wise records updated -> " + status);
					
					System.out.println("NonCommunicatingMachinesReportImpl: installationDateWiseUpdate : day wise records updated -> " + status);
					
					//4. Fetch Grand Total
					int grandTotal = 0;
					String grandTotalUpdateQuery;
					
					//if install_year is already present in table, fetch the sum of all columns
					if(installYearExistInTable) {
						String grandTotalQuery = "SELECT SUM(IFNULL(one_to_seven,0) + IFNULL(eight_to_fifteen,0) + IFNULL(sixteen_to_thirty,0) + IFNULL(thirtyone_to_sixty,0) + IFNULL(sixty_plus,0)) AS total FROM installation_wise_non_comm_machine_"+nation+" " +
								"WHERE unique_id = "+uniqueId;
						rs3 = statement.executeQuery(grandTotalQuery);
						while(rs3.next()) {
							if(rs3.getString("total") != null && Integer.parseInt(rs3.getString("total")) > 0) grandTotal = Integer.parseInt(rs3.getString("total"));
						}
						//TODO - remove
						System.out.println("Got grand Total: " + grandTotal);
						grandTotalUpdateQuery = "UPDATE installation_wise_non_comm_machine_"+nation+" SET grand_total = " + grandTotal + " WHERE (unique_id = " + uniqueId + ")";
					}
					//else consider current record.getCount() as grand total
					else {
						maxUniqueId++;
						grandTotal = Integer.parseInt(record.getCount());
						grandTotalUpdateQuery = "UPDATE installation_wise_non_comm_machine_"+nation+" SET grand_total = " + grandTotal + " WHERE (unique_id = " + maxUniqueId + ")";
					}
					
					//5. Update the grand_total column in table: dealer_wise_non_comm_machine
					int updateGrandTotalStatus = statement.executeUpdate(grandTotalUpdateQuery);
					iLogger.info("NonCommunicatingMachinesReportImpl: installationDateWiseUpdate : grand total record updated -> " + updateGrandTotalStatus);
					System.out.println("NonCommunicatingMachinesReportImpl: installationDateWiseUpdate : grand total record updated -> " + updateGrandTotalStatus);

				}
			}
			
		} catch (Exception e) {
			//TODO - Remove
			e.printStackTrace();
			Writer writer = new StringWriter();
			PrintWriter ps = new PrintWriter(writer);
			e.printStackTrace(ps);
			fLogger.fatal("NonCommunicatingMachinesReportImpl: installationDateWiseUpdate : Exception caught\n" + ps.toString());
			
			return "FAILURE";
		}
		finally {
			try{
				if(rs != null)
					rs.close();
				if(rs1 != null)
					rs1.close();
				if(rs2 != null)
					rs2.close();
				if(rs3 != null)
					rs3.close();
				if(statement != null)
					statement.close();
				if(connection != null)
					connection.close();

			}catch (Exception e) {
				fLogger.fatal("NonCommunicatingMachinesReportImpl: installationDateWiseUpdate : Exception caught while closing connections - " + e.getMessage());
			}
		}
		
		return "SUCCESS";
	
	}
	
	/**
	 * 
	 */
	public List<NonCommunicationDrillDownOutputContract> NonCommDrillDown(String warranty, String orderBy,String range ,String nation) {
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		int tillDay = -1;
		int fromDay = -1;
		boolean indianNationFlag = false;
		
		List<NonCommunicationDrillDownOutputContract> result = new ArrayList<NonCommunicationDrillDownOutputContract>();
		
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		try {
			NonCommunicationDrillDownOutputContract data = new NonCommunicationDrillDownOutputContract();
			if(range.equals("one_to_seven")) { tillDay = 0; fromDay = 7; }
			else if(range.equals("eight_to_fifteen")) { tillDay = 7; fromDay = 15; }
			else if(range.equals("sixteen_to_thirty")) { tillDay = 15; fromDay = 30; }
			else if(range.equals("thirtyone_to_sixty")) { tillDay = 30; fromDay = 60; }
			else if(range.equals("sixty_plus")) { tillDay = 60; }
			else { return result; }
			
			
			//check if nation is valid input or not		
			if(nation != null && nation.toLowerCase().equals("indian")) {
				indianNationFlag = true;
			}
			else if( nation !=null && nation.toLowerCase().equals("saarc")) {
				indianNationFlag = false;
			}
			else {
				return result;
			}
			
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			
			/**
			 * Fetch required data from com_rep_oem_enhanced_20210110and save it to List<NonCommunicationDrillDownOutputContract> result
			 */
			String selectQuery="SELECT serial_number as vin, Zone as zone, Dealer_Name as dealer, CustomerName as customer, Pkt_Recd_TS as lastCommunicatedDate, Installed_date as installationDate, "
					+ "Roll_Off_Date as rollOffDate, Renew_state as renewalStatus,SIM_No as imsi, Renewal_date as renewalExpiryDate, tmh as machineHours, "
					+ "Model as model, Profile as profile, version as firmware, Comm_State as vinState, Comm_City as vinCity, Comm_Address as vinAddress " +
					"FROM com_rep_oem_enhanced_20210110 where extended_warranty = '" + warranty + "'";
			
			selectQuery += " AND  Renewal_Flag = 1 " ;
					
			if(indianNationFlag) selectQuery += "AND countrycode = '+91' ";
			else selectQuery += "AND countrycode != '+91' AND SIM_No like '2040%' ";
			
			if( fromDay != -1)  selectQuery += "AND Pkt_Recd_TS >= DATE_ADD(CURDATE(), INTERVAL -" + fromDay + " DAY) ";
			selectQuery += "AND Pkt_Recd_TS < DATE_ADD(CURDATE(), INTERVAL -" + tillDay + " DAY) ";
			
			
			String orderQuery = " order by " + orderBy;
			
			String finalQuery = selectQuery +orderQuery;
			
			//TODO - remove after testing..
			System.out.println("Query: " + finalQuery);
			
			iLogger.info("NonCommunicatingMachinesReportImpl: NonCommDrillDown : Running query -> " + finalQuery);
			
			rs = statement.executeQuery(finalQuery);
			while(rs.next())
			{
				data = new NonCommunicationDrillDownOutputContract();
				
				data.setVin(rs.getString("vin"));
				data.setZone(rs.getString("zone"));
				data.setDealer(rs.getString("dealer"));
				data.setCustomer(rs.getString("customer"));
				data.setLastCommunicatedDate(rs.getString("lastCommunicatedDate"));
				data.setInstallationDate(rs.getString("installationDate"));
				data.setRollOffDate(rs.getString("rollOffDate"));
				data.setRenewalStatus(rs.getString("renewalStatus"));
				data.setImsi(rs.getString("imsi"));
				data.setRenewalExpiryDate(rs.getString("renewalExpiryDate"));
				data.setMachineHours(rs.getString("machineHours"));
				data.setModel(rs.getString("model"));
				data.setProfile(rs.getString("profile"));
				data.setFirmware(rs.getString("firmware"));
				data.setVinState(rs.getString("vinState"));
				data.setVinCity(rs.getString("vinCity"));
				data.setVinAddress(rs.getString("vinAddress"));
				
				result.add(data);
			}
			
		} catch (Exception e) {
			//TODO - Remove
			e.printStackTrace();
			Writer writer = new StringWriter();
			PrintWriter ps = new PrintWriter(writer);
			e.printStackTrace(ps);
			fLogger.fatal("NonCommunicatingMachinesReportImpl: NonCommDrillDown : Exception caught\n" + ps.toString());
			
		} finally {
			try{
				if(rs != null)
					rs.close();
				if(statement != null)
					statement.close();
				if(connection != null)
					connection.close();
			}catch (Exception e) {
				fLogger.fatal("NonCommunicatingMachinesReportImpl: NonCommDrillDown : Exception caught while closing connections - " + e.getMessage());
			}
		}
		return result;
	}
	
	
	/**
	 * @author Z1007653 : Zakir
	 * method : 
	 * output : 
	 */
	public List<ZoneOrDealerOrInstallationWiseSummaryOutputContract> getZoneWiseSummaryData(String warranty, String nation) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		List<ZoneOrDealerOrInstallationWiseSummaryOutputContract> result = new ArrayList<ZoneOrDealerOrInstallationWiseSummaryOutputContract>();
		
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		try {
			//TODO - Here all implementation logic will be performed
			
			ZoneOrDealerOrInstallationWiseSummaryOutputContract data = new ZoneOrDealerOrInstallationWiseSummaryOutputContract();
			
			//Get Connections
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			
			String selectQuery = "SELECT * FROM zone_wise_non_comm_machine_"+ nation + " WHERE zone != '(blank)' ";
			if(warranty != null) selectQuery += " AND warranty = '" + warranty +"' ";
			
			System.out.println("Query executing: " + selectQuery);
			iLogger.info("NonCommunicatingMachinesReportImpl: NonCommDrillDown : Running query -> " + selectQuery);
			
			rs = statement.executeQuery(selectQuery);
			
			while(rs.next()) {
				data = new ZoneOrDealerOrInstallationWiseSummaryOutputContract();
				
				data.setKey(rs.getString("zone"));
				data.setOneToSeven(rs.getString("one_to_seven"));
				data.setEightToFifteen(rs.getString("eight_to_fifteen"));
				data.setSixteenToThirty(rs.getString("sixteen_to_thirty"));
				data.setThirtyOneToSixty(rs.getString("thirtyone_to_sixty"));
				data.setSixtyPlux(rs.getString("sixty_plus"));
				data.setGrandTotal(rs.getString("grand_total"));
				data.setWarranty(rs.getString("warranty"));
				data.setCountryCode(rs.getString("country_code"));
				
				result.add(data);
			}

		} catch(Exception e) {
			Writer writer = new StringWriter();
			PrintWriter ps = new PrintWriter(writer);
			e.printStackTrace(ps);
			fLogger.fatal("NonCommunicatingMachinesReportImpl: getZoneWiseSummaryData : Exception caught\n" + ps.toString());
			
		} finally {
			try{
				if(rs != null)
					rs.close();
				if(statement != null)
					statement.close();
				if(connection != null)
					connection.close();
			}catch (Exception e) {
				fLogger.fatal("NonCommunicatingMachinesReportImpl: getZoneWiseSummaryData : Exception caught while closing connections - " + e.getMessage());
			}
		}
		
		return result;
		
	}

	/**
	 * @author YA20167922 : Yaseswini
	 */
	public List<ZoneOrDealerOrInstallationWiseSummaryOutputContract> getdealerWiseSummaryData(String warranty, String nation) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		List<ZoneOrDealerOrInstallationWiseSummaryOutputContract> result = new ArrayList<ZoneOrDealerOrInstallationWiseSummaryOutputContract>();
		
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		try {
			//TODO - Here all implementation logic will be performed
			
			ZoneOrDealerOrInstallationWiseSummaryOutputContract data = new ZoneOrDealerOrInstallationWiseSummaryOutputContract();
			
			//Get Connections
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			
			String selectQuery = "SELECT * FROM dealer_wise_non_comm_machine_"+nation+ " WHERE dealer != '(blank)' ";
			if(warranty != null) selectQuery += " AND warranty = '" + warranty +"' ";
			
			System.out.println("Query executing: " + selectQuery);
			iLogger.info("NonCommunicatingMachinesReportImpl: getdealerWiseSummaryData : Running query -> " + selectQuery);
			
			rs = statement.executeQuery(selectQuery);
			
			while(rs.next()) {
				data = new ZoneOrDealerOrInstallationWiseSummaryOutputContract();
				
				data.setKey(rs.getString("dealer"));
				data.setOneToSeven(rs.getString("one_to_seven"));
				data.setEightToFifteen(rs.getString("eight_to_fifteen"));
				data.setSixteenToThirty(rs.getString("sixteen_to_thirty"));
				data.setThirtyOneToSixty(rs.getString("thirtyone_to_sixty"));
				data.setSixtyPlux(rs.getString("sixty_plus"));
				data.setGrandTotal(rs.getString("grand_total"));
				data.setWarranty(rs.getString("warranty"));
				data.setCountryCode(rs.getString("country_code"));
				
				result.add(data);
			}
	
		} catch(Exception e) {
			Writer writer = new StringWriter();
			PrintWriter ps = new PrintWriter(writer);
			e.printStackTrace(ps);
			fLogger.fatal("NonCommunicatingMachinesReportImpl: getdealerWiseSummaryData : Exception caught\n" + ps.toString());
			
		} finally {
			try{
				if(rs != null)
					rs.close();
				if(statement != null)
					statement.close();
				if(connection != null)
					connection.close();
			}catch (Exception e) {
				fLogger.fatal("NonCommunicatingMachinesReportImpl: getdealerWiseSummaryData : Exception caught while closing connections - " + e.getMessage());
			}
		}
		
		return result;
	}

/**
 * @author YA20167922 : Yaseswini
 */
public List<ZoneOrDealerOrInstallationWiseSummaryOutputContract> getinstallationWiseSummaryData(String warranty, String nation) {
	Logger fLogger = FatalLoggerClass.logger;
	Logger iLogger = InfoLoggerClass.logger;
	
	List<ZoneOrDealerOrInstallationWiseSummaryOutputContract> result = new ArrayList<ZoneOrDealerOrInstallationWiseSummaryOutputContract>();
	
	Connection connection = null;
	Statement statement = null;
	ResultSet rs = null;
	
	try {
		//TODO - Here all implementation logic will be performed
		
		ZoneOrDealerOrInstallationWiseSummaryOutputContract data = new ZoneOrDealerOrInstallationWiseSummaryOutputContract();
		
		//Get Connections
		ConnectMySQL connMySql = new ConnectMySQL();
		connection = connMySql.getConnection();
		statement = connection.createStatement();
		
		String selectQuery = "SELECT * FROM installation_wise_non_comm_machine_"+nation + " WHERE install_year != '(blank)'  ";
		if(warranty != null) selectQuery += " AND warranty = '" + warranty +"' ";
		selectQuery +=" ORDER BY install_year ";
		
		System.out.println("Query executing: " + selectQuery);
		iLogger.info("NonCommunicatingMachinesReportImpl: getinstallationWiseSummaryData : Running query -> " + selectQuery);
		
		rs = statement.executeQuery(selectQuery);
		
		while(rs.next()) {
			data = new ZoneOrDealerOrInstallationWiseSummaryOutputContract();
			
			data.setKey(rs.getString("install_year"));
			data.setOneToSeven(rs.getString("one_to_seven"));
			data.setEightToFifteen(rs.getString("eight_to_fifteen"));
			data.setSixteenToThirty(rs.getString("sixteen_to_thirty"));
			data.setThirtyOneToSixty(rs.getString("thirtyone_to_sixty"));
			data.setSixtyPlux(rs.getString("sixty_plus"));
			data.setGrandTotal(rs.getString("grand_total"));
			data.setWarranty(rs.getString("warranty"));
			data.setCountryCode(rs.getString("country_code"));
			
			result.add(data);
		}

	} catch(Exception e) {
		Writer writer = new StringWriter();
		PrintWriter ps = new PrintWriter(writer);
		e.printStackTrace(ps);
		fLogger.fatal("NonCommunicatingMachinesReportImpl: getinstallationWiseSummaryData : Exception caught\n" + ps.toString());
		
	} finally {
		try{
			if(rs != null)
				rs.close();
			if(statement != null)
				statement.close();
			if(connection != null)
				connection.close();
		}catch (Exception e) {
			fLogger.fatal("NonCommunicatingMachinesReportImpl: getinstallationWiseSummaryData : Exception caught while closing connections - " + e.getMessage());
		}
	}
	
	return result;
}
public static void main(String[] args) {
	NonCommunicatingMachinesReportImpl NonCommunicatingMachinesReportImpl = new NonCommunicatingMachinesReportImpl();
	NonCommunicatingMachinesReportImpl.NonCommDrillDown("YES", "Zone", "one_to_seven", "indian");
}



}
