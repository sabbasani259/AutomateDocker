package remote.wise.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class ConnectMySqlfota {

	private Connection con = null;
	public static DataSource prodDB1Datasource=getProdDB1DS();
/*
	// String dbUrl = "jdbc:mysql://10.106.68.5:3306/fota_smart_systems"; //Dev
	// String dbUrl = "jdbc:mysql://localhost:3306/fota_smart_systems"; //local
	String dbUrl = "jdbc:mysql://10.106.68.8:3306/fota_smart_systems"; // SIT

	// String dbUrl = "jdbc:mysql://10.106.68.13:3306/fota_smart_systems"; //QA
*/
	//CR337.sn
	public static DataSource getProdDB1DS()
	{
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		DataSource datasource=null;
		
		try 
		{
			Context initialContext = new InitialContext();
			iLogger.info("Mysql PRODDB1DS Datasource: Get New DS");
			datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PRODDB1DS");
		}
		
		catch(Exception e)
		{
			fLogger.error("Error in connecting to Mysql PRODDB1DS Datasource: Exception caught : "+e.getMessage());
		}
		
		return datasource;
	}//CR337.en
	
	public Connection getConnection() {
		//CR337.so
		/*String dbUrl = "";
		Properties deployEnvProp = new Properties();
		deployEnvProp = CommonUtil.getDepEnvProperties();
		String productionEnv = deployEnvProp.getProperty("deployenvironment");
		
		if (productionEnv.equalsIgnoreCase("SIT")) {
			dbUrl = "jdbc:mysql://10.106.68.8:3306/wiseintegratednew"; // SIT
		} else if (productionEnv.equalsIgnoreCase("DEV")) {
			 dbUrl = "jdbc:mysql://10.106.68.5:3306/eaintegrationwise"; //Dev
		} else if (productionEnv.equalsIgnoreCase("QA")) {
			 dbUrl = "jdbc:mysql://10.106.68.13:3306/wiseintegratedQA"; //QA
		} else if (productionEnv.equalsIgnoreCase("PROD")) {
			dbUrl = "jdbc:mysql://10.179.12.66:3306/wise"; //Production
		}else{
			dbUrl = "jdbc:mysql://localhost:3306/wise"; //localhost
		}
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbUrl, "epuser", "epuser");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;*/
		//CR337.eo
		//CR337.sn
		Connection conn = null;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		try 
		{
			iLogger.info("Mysql ProdDB1 Datasource: Get New Connection");
			conn = prodDB1Datasource.getConnection();
		}

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in connecting to Mysql ProdDB1 Datasource: Exception caught : "+e.getMessage());
		}		
		return conn;
		//CR337.en
	}

	public void closeConnection(Connection conn) {
		Logger fLogger = FatalLoggerClass.logger;
		try {
			conn.close();
		} catch (SQLException e) {
			fLogger.fatal("SQL Exception : " + e);
		}
	}

}
