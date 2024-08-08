package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class PseudoAccessImpl {
	public List<HashMap<String,String>> getPseudoTenancyList(){
	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;
	
	List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
	HashMap<String,String> records = null;
	Connection connection = null;
	Statement statement = null;
	ResultSet rs = null;
	System.out.println("Into the Pseudo Access Implimentation class::");
	try {
		//Connection to DB
		ConnectMySQL connFactory = new ConnectMySQL();
		connection = connFactory.getConnection();
		
		String interfaceListQuery = null;
		
		interfaceListQuery = "select acc.Account_ID, acc.Account_Name, acc.Account_Code, acc.mapping_code,at.Tenancy_ID,ten.Tenancy_Name from account acc, account_tenancy at,tenancy ten where acc.mapping_code in (select mapping_code from account where MAFlag=1 ) and acc.account_id = at.account_id and at.Tenancy_ID = ten.Tenancy_ID ";
		
		statement = connection.createStatement();
		rs = statement.executeQuery(interfaceListQuery);
		while(rs.next()) {
			records = new HashMap<String,String>();
			records.put("Account_ID", rs.getString("Account_ID"));
			records.put("Account_Name", rs.getString("Account_Name"));
			records.put("Account_Code", rs.getString("Account_Code"));
			records.put("mapping_code", rs.getString("mapping_code"));
			records.put("Tenancy_ID", rs.getString("Tenancy_ID"));
			records.put("Tenancy_Name", rs.getString("Tenancy_Name"));
			list.add(records);
		}
		System.out.println("Length received is "+list.size());
	}
	catch(Exception e){
		System.out.println("Error:"+e);
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
	//System.out.println("List is"+list);
	System.out.println("Out of Interface List Implimentation class::");
	return list;
	}
}
