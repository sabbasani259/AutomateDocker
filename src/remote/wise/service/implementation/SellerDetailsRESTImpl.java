package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountMapping;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;

public class SellerDetailsRESTImpl {

	public List<HashMap<String, String>> getSellerDetails() {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<HashMap<String, String>> response = new LinkedList<HashMap<String,String>>();
		HashMap<String, String> eccAccObj = null;
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet=null;
		
		try{
			iLogger.info("SellerDetailsRESTImpl::getSellerDetails Invoked");
			
			ConnectMySQL connectionObj = new ConnectMySQL();
			con = connectionObj.getConnection();
			statement = con.createStatement();

			resultSet = statement.executeQuery("select * from account_mapping group by ECC_Account_Name");

			while(resultSet.next())
			{
				eccAccObj = new HashMap<String, String>();
				eccAccObj.put(resultSet.getString("ECC_Code"), resultSet.getString("ECC_Account_name"));
				response.add(eccAccObj);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			fLogger.fatal("Exception Caught ::"+e.getMessage());
			e.printStackTrace();
		}
		finally{
			try{			
				if(resultSet != null){
					resultSet.close();
				}
				if(statement != null){
					statement.close();
				}
				if(con != null){
					con.close();
				}
			}catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("Exception in closing the connection. :"+e);
			}

		}
		
		return response;
	}
}
