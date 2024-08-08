package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class SessionMapImpl {
	Logger iLogger = InfoLoggerClass.logger;
public HashMap<String,String> getSessionMap(String clientId){
	HashMap<String,String> list = new HashMap<String,String>();
	ConnectMySQL connFactory = new ConnectMySQL();
	String sessionMapQuery = null;
	sessionMapQuery = "select * from user_session where clientId = '"+clientId+"'";
	iLogger.info("query : "+sessionMapQuery);
	//Connection to DB
	try (Connection connection=connFactory.getConnection();
		 Statement	statement = connection.createStatement();
		 ResultSet rs = statement.executeQuery(sessionMapQuery))
	    {
		
		while(rs.next()) {
			list = new HashMap<String,String>();
			list.put("clientId", rs.getString("clientId"));
			list.put("loginId", rs.getString("loginId"));
			list.put("userCountry", rs.getString("userCountry"));
			list.put("tenancyIdList", rs.getString("tenIdList"));
			list.put("isTenancyAdmin", rs.getString("isTenancyAdmin"));
			list.put("RoleId", rs.getString("RoleId"));
			list.put("roleName", rs.getString("roleName"));
			list.put("loggedInUserTenancyList", rs.getString("loggedInUsertenList"));
			list.put("loggedInUserTenancyName", rs.getString("loggedInUserTenName"));
			list.put("isMapService", rs.getString("isMapService"));
			list.put("isSmsService", rs.getString("isSmsService"));
			list.put("isPseudoLogin", rs.getString("isPseudoLogin"));
			list.put("headerRoleName", rs.getString("headerRoleName"));
			list.put("country", rs.getString("country"));
			list.put("country_tenancy", rs.getString("country_tenancy"));
			list.put("userName", rs.getString("userName"));
			list.put("referrer", rs.getString("referrer"));
		}
	}
	catch(SQLException ex){
		iLogger.info("exception while executing query"+ex.getMessage());
	}catch(Exception e){
		iLogger.info("exception in getSessionMap() api "+e.getMessage());
	}
	return list;
}

public String setSessionMap(Map<String, Object> sessionMap) {
	ConnectMySQL connFactory = new ConnectMySQL();
	String sessionMapQuery = null;
	String loggedInUserTenancyIdList =sessionMap.get("loggedInUserTenancyIdList").toString();
	String tenencyIdList=sessionMap.get("tenencyIdList").toString();
	
	loggedInUserTenancyIdList=loggedInUserTenancyIdList.substring(1, loggedInUserTenancyIdList.length()-1);
	tenencyIdList=tenencyIdList.substring(1, tenencyIdList.length()-1);
	sessionMapQuery = "Insert into user_session(clientId ,loginId ,userCountry,tenIdList,isTenancyAdmin,RoleId,roleName,loggedInUsertenList,loggedInUserTenName,isMapService,isSmsService,isPseudoLogin,headerRoleName,country,country_tenancy,userName,referrer) values('"
			+ sessionMap.get("clientId") +"','"+ sessionMap.get("loginId")+"','"+sessionMap.get("userCountry")+"','"+tenencyIdList
			+"','"+sessionMap.get("isTenancyAdmin")+"','"+sessionMap.get("roleId")+"','"+sessionMap.get("roleName")+"','"+loggedInUserTenancyIdList
			+"','"+sessionMap.get("loggedInUserTenancyName")+"','"+sessionMap.get("isMapService")
			+"','"+sessionMap.get("isSmsService")+"','"+sessionMap.get("isPseudoLogin")+"','"+sessionMap.get("headerRoleName")
			+"','"+sessionMap.get("country")+"','"+sessionMap.get("country_tenancy")+"','"+sessionMap.get("userName")+"','"+sessionMap.get("referrer")+"')";
	
	iLogger.info("Query : "+sessionMapQuery);
	//Connection to DB
	try (Connection connection=connFactory.getConnection();
		 Statement	statement = connection.createStatement(); )
    {
		int r = statement.executeUpdate(sessionMapQuery);
		iLogger.info("Result of executeUpdate "+r);
	}catch(SQLException ex){
		iLogger.info("exception while executing query"+ex.getMessage());
	}catch(Exception e){
		iLogger.info("exception in setSessionMap() api "+e.getMessage());
	}
		//String sessionMapQuery = null;
	return "Success";
}
}
