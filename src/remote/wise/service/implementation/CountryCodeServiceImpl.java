/**
 * 
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;
import remote.wise.exception.CustomFault;

import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;

/**
 * @author ROOPN5
 *
 */
public class CountryCodeServiceImpl {

	public List<HashMap<String,Object>> getCountryCodeData(){



		List<HashMap<String, Object>> respList = new ArrayList<HashMap<String,Object>>();
		Logger fLogger = FatalLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;



		try{
			String selectQuery="Select * from country_codes";
			
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(selectQuery);

			while(rs.next()){
				HashMap<String, Object> respObj = new HashMap<String, Object>();
				
				respObj.put("country_name", rs.getString("country_name"));
				respObj.put("country_code", rs.getString("country_code"));
				respObj.put("TimeZone", rs.getString("TimeZones"));
				
				
				respList.add(respObj);
			}
		}

		catch(Exception e){
			fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorLifeData:: Exception Caught:"+e.getMessage());
		}
		finally{
			try { 
				if (rs != null)
					rs.close(); 
			} catch (Exception e) {
				fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorLifeData:: Exception Caught:"+e.getMessage());
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorLifeData:: Exception Caught:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("ExcavatorDashboardRESTService:getExcavatorLifeData:: Exception Caught:"+e.getMessage());
				}
			}
		}

		return respList;

	}
	public String vinCountryCode(String vin)
	{
		Logger fLogger = FatalLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String countrycode="";


		try{
			String loginValidation = new CommonUtil().inputFieldValidation(vin);
			if(!loginValidation.equalsIgnoreCase("SUCCESS"))
			{
				throw new CustomFault("Invalid request.");
			}
			String selectQuery="Select country_code from asset where Serial_Number=\'"+vin+"\'";
			
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(selectQuery);
			
			while(rs.next()){
				countrycode=rs.getString("country_code");
			}
		}

		catch(Exception e){
			fLogger.fatal("CountryCodeRESTService: vinCountryCode : Exception Caught:"+e.getMessage());
		}
		finally{
			try { 
				if (rs != null)
					rs.close(); 
			} catch (Exception e) {
				fLogger.fatal("CountryCodeRESTService: vinCountryCode ::: Exception Caught:"+e.getMessage());
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("CountryCodeRESTService: vinCountryCode ::: Exception Caught:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("CountryCodeRESTService: vinCountryCode ::: Exception Caught:"+e.getMessage());
				}
			}
		}
		return countrycode;
	}


}
