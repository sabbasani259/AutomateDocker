package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class DTCAlertReportServiceImpl {
	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;

	public List<String> getAlertPreferenceList(int roleId,int preference){
		String selectQuery = "select alertmap_display from alert_role_mapping where role_id="+roleId;
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet=null;
		List<String> alertCodeList = new ArrayList<>();
		String alertMap = null;
		HashMap<String,Integer> alertMapDetails=new HashMap<String, Integer>();
		try{
		
		ConnectMySQL connectionObj = new ConnectMySQL();
		con = connectionObj.getConnection();
		statement = con.createStatement();
		
		
		resultSet = statement.executeQuery(selectQuery);
		while(resultSet.next())
		{
			alertMap=resultSet.getObject("alertmap_display").toString();
		}
		alertMapDetails=new Gson().fromJson(alertMap, new TypeToken<HashMap<String, Integer>>() {}.getType());
		
		Iterator<HashMap.Entry<String, Integer>> itr = alertMapDetails.entrySet().iterator();
		
		 while(itr.hasNext())
	        {
			 HashMap.Entry<String, Integer> entry = itr.next();
			 
			 if(entry.getValue().equals(preference)) {
				 alertCodeList.add(entry.getKey());
			 }
	        }
		
		}catch(Exception ex)
		{
		
		ex.printStackTrace();
		fLogger.fatal("Exception occurred in getAlertPreferenceList: "+ex.getMessage());
		
		}finally{
			if(resultSet!=null)
				try {
					resultSet.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return alertCodeList;
		}

}
