package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class InterfaceListIMPL {
	public HashMap<Integer,String> getInterfaceList(String master_data) {
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		//DF20190307 :: new impl file for listing all the interfaces 
		HashMap<Integer,String> list = new HashMap<Integer,String>();;
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		System.out.println("Into the Interface List Implimentation class::");
		iLogger.info("Into the Interface List Implimentation class::");
		
		try {
			//Connection to DB
			ConnectMySQL connFactory = new ConnectMySQL();
			connection = connFactory.getConnection();
			
			String interfaceListQuery = null;
			//Query to fetch all distinct interface list
			if("Date".equalsIgnoreCase(master_data))
				interfaceListQuery = "SELECT * FROM ea_interfaces_master";
			else if("VIN".equalsIgnoreCase(master_data))
				interfaceListQuery = "SELECT * FROM ea_interfaces_master where master_data = 0";
			statement = connection.createStatement();
			rs = statement.executeQuery(interfaceListQuery);
			
			int i = 1;
			while(rs.next()) {
				list.put(i, rs.getString("Interface_Name"));
				i++;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("::Exception Caught in InterfaceIMPL ::"+e.getMessage());
		}
		finally{
			try{
				if(rs != null)
					rs.close();
				if(statement != null)
					statement.close();
				if(connection != null)
					connection.close();
			}catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("::Exception Caught while closing the connection::"+e.getMessage());
			}
		}
		/*System.out.println(list);*/
		System.out.println("Out of Interface List Implimentation class::");
		iLogger.info("Out of Interface List Implimentation class::");
		
		return list;
	}
}
