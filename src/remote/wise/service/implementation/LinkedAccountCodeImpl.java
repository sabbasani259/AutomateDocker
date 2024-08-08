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

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.ListToStringConversion;

/**
 * @author ROOPN5
 *
 */
public class LinkedAccountCodeImpl {
	public List<HashMap<String, Object>> getListofLinkedAccountCodes(List accountCodeList){
		HashMap<String, Object> respObj = null;
		List<HashMap<String, Object>> responseList=new ArrayList<HashMap<String,Object>>();
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String finalQuery=null;
		ListToStringConversion conversion = new ListToStringConversion();
		
		try{
			
			String accountCodeListString = conversion.getStringList(accountCodeList).toString();
			
			finalQuery="select Account_Code from account a where mapping_code in("+accountCodeListString+")";
			
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(finalQuery);
			
			while(rs.next()){
				respObj = new HashMap<String, Object>();

				respObj.put("Account_Code", rs.getString("Account_Code"));
				
				responseList.add(respObj);
			}
			
		}
		catch(Exception e){
			fLogger.fatal("LinkedAccountCodeService:getListofLinkedAccountCodes:: Exception Caught:"+e.getMessage());
		}
		finally{
			try { 
				if (rs != null)
					rs.close(); 
			} catch (Exception e) {
				fLogger.fatal("LinkedAccountCodeService:getListofLinkedAccountCodes:: Exception Caught:"+e.getMessage());
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("LinkedAccountCodeService:getListofLinkedAccountCodes:: Exception Caught:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("LinkedAccountCodeService:getListofLinkedAccountCodes:: Exception Caught:"+e.getMessage());
				}
			}
		}

		return responseList;
		
	}

}
