//CR321-20220712-Vidya SagarM-CombineToFotaReport to combine required fields to wisetraceability schema .
package remote.wise.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.logging.log4j.Logger;
import org.hibernate.JDBCException;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;


public class ConnectMySQL 
 {

	private Connection con = null;
/*
	// String dbUrl = "jdbc:mysql://10.106.68.5:3306/eaintegrationwise"; //Dev
	// String dbUrl = "jdbc:mysql://localhost:3306/wiseintegratedtest"; //local
	// String dbUrl = "jdbc:mysql://10.106.68.8:3306/wiseintegratednew"; //SIT
	String dbUrl = "jdbc:mysql://10.106.68.8:3306/wiseintegratednew"; // SIT

	// String dbUrl = "jdbc:mysql://10.179.12.66:3306/wise"; //Production

	// String dbUrl = "jdbc:mysql://10.106.68.13:3306/wiseintegratedQA"; //QA

	// String edgeProxyUrl = "jdbc:mysql://10.106.68.5:3306/smart_systems";
	// //Dev
	// String edgeProxyUrl = "jdbc:mysql://localhost:3306/smart_systems";
	// //local

	String edgeProxyUrl = "jdbc:mysql://10.106.68.8:3306/smart_systems"; // SIT
	// String edgeProxyUrl = "jdbc:mysql://10.179.12.66:3306/smart_systems"; //
	// prod
	// String edgeProxyUrl = "jdbc:mysql://10.106.68.13:3306/smart_systems";
	
		// //QA
*/
	
	//Adding datasource for DB1 wise, DB1 Smart systems and DB2 insight
	
	/** Method to Get a connection from orientdb datasource
	 * @return SQL Connection object on SUCCESS;
	 * 		   NULL otherwise
	 */
	public static DataSource prodDB1Datasource=getProdDB1DS();
	
	public static DataSource prodDB2Datasource=getProdDB2DS();
	
	public static DataSource prodDB1EdgeProxyDatasource=getProdDB1EPDS();
	
	public static DataSource prodDB1EPSmartDatasource=getProdDB1EPSMARTDS();
	
	public static DataSource datasource1=getWISEDS_PERCONA();
	
	public static DataSource datasource2=getWISEDS_DATALAKE();
	
	public static DataSource datasource3=getPRODDB1DS_UPTIME();
	
	public static DataSource prodDB1ETLDatasource=getProdDB1ETLDS();
	
	public static DataSource DataCommSchemaDS=getDATACOMMDS();
	public static DataSource tracebilityDS=getTracebilityDS();
	public static DataSource wise_tracebilityDS=getWiseTracebility3306DS();//CR321.n
	
	public static DataSource getProdDB1ETLDS()
	{
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		DataSource datasource=null;
		
		try 
		{
			Context initialContext = new InitialContext();
			iLogger.info("Mysql PRODDB1DS Datasource: Get New DS");
			datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PRODDB1ETLDS");
					
		}
		
		catch(Exception e)
		{
			fLogger.error("Error in connecting to Mysql PRODDB1DS Datasource: Exception caught : "+e.getMessage());
		}
		
		return datasource;
	}
	
	public static DataSource getWISEDS_DATALAKE(){

		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		DataSource datasource=null;
		
		try 
		{
			Context initialContext = new InitialContext();
			iLogger.info("Mysql WISEDS_DATALAKE Datasource: Get New DS");
			datasource = (DataSource)initialContext.lookup("java:jboss/datasources/WISEDS_DATALAKE");
					
		}
		
		catch(Exception e)
		{
			fLogger.error("Error in connecting to Mysql WISEDS_DATALAKE Datasource: Exception caught : "+e.getMessage());
		}
		
		return datasource;
	
		
	}
	
	public static DataSource getPRODDB1DS_UPTIME()
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		DataSource datasource=null;

		try 
		{
			Context initialContext = new InitialContext();
			iLogger.info("Mysql PRODDB1DS_UPTIME Datasource: Get New DS");
			datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PRODDB1DS_UPTIME");

		}

		catch(Exception e)
		{
			fLogger.error("Error in connecting to Mysql PRODDB1DS_UPTIME Datasource: Exception caught : "+e.getMessage());
		}

		return datasource;
	}
		
	
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
	}
	
	public static DataSource getProdDB1EPDS()
	{
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		DataSource datasource=null;
		
		try 
		{
			Context initialContext = new InitialContext();
			iLogger.info("Mysql PRODDB1EPDS Datasource: Get New DS");
			datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PRODDB1EPDS");
					
		}
		
		catch(Exception e)
		{
			fLogger.error("Error in connecting to Mysql PRODDB1EPDS Datasource: Exception caught : "+e.getMessage());
		}
		
		return datasource;
	}
	
	public static DataSource getProdDB1EPSMARTDS()
	{
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		DataSource datasource=null;
		
		try 
		{
			Context initialContext = new InitialContext();
			iLogger.info("Mysql PRODDB1EPSMARTDS Datasource: Get New DS");
			datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PRODDB1EPSMARTDS");
					
		}
		
		catch(Exception e)
		{
			fLogger.error("Error in connecting to Mysql PRODDB1EPSMARTDS Datasource: Exception caught : "+e.getMessage());
		}
		
		return datasource;
	}
	
	public static DataSource getProdDB2DS()
	{
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		DataSource datasource=null;
		
		try 
		{
			Context initialContext = new InitialContext();
			iLogger.info("Mysql ProdDB2 Datasource: Get New DS");
			datasource = (DataSource)initialContext.lookup("java:jboss/datasources/PRODDB2DS");
					
		}
		
		catch(Exception e)
		{
			fLogger.error("Error in connecting to Mysql ProdDB2 Datasource: Exception caught : "+e.getMessage());
		}
		
		return datasource;
	}
	//DF20190110 :ma369757: data source for data_comm schema
	public static DataSource getDATACOMMDS()
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		DataSource datasource=null;

		try 
		{
			Context initialContext = new InitialContext();
			iLogger.info("Mysql DCDS Datasource: Get New DS");
			datasource = (DataSource)initialContext.lookup("java:jboss/datasources/DCDS");

		}

		catch(Exception e)
		{
			fLogger.error("Error in connecting to Mysql DCDS Datasource: Exception caught : "+e.getMessage());
		}

		return datasource;
	}
	//DF20190104 :: MANI :: Smslog details from new db, post 2019-01-08
	public Connection getConnection_data_comm()
	{
		Connection conn = null;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		try 
		{
			iLogger.info("Mysql DCDS Datasource: Get New Connection");
			conn = DataCommSchemaDS.getConnection();
			iLogger.info("Mysql DCDS Datasource: Connection Established.");


		}

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in connecting to Mysql DCDS Datasource: Exception caught : "+e.getMessage());
		}
		return conn;
	}
	
/*	//DF20190104 :: MANI :: Smslog details from new db, post 2019-01-08
	public Connection getConnection_data_comm() 
	{
		Connection conn = null;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		try 
		{
			iLogger.info("Mysql ProdDB1 Datasource: Get New Connection");
			 Properties deployEnvProp = new Properties();
             deployEnvProp = CommonUtil.getDepEnvProperties();
			String productionEnv = deployEnvProp.getProperty("deployenvironment");
			String dbUrl=null;
			 if (productionEnv.equalsIgnoreCase("PROD")){
                 dbUrl = "jdbc:mysql://10.179.12.67:3306/data_comm"; //Production
			 }
			 else{
			 dbUrl  = "jdbc:mysql://10.179.12.74:3306/data_comm";
			 }
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(dbUrl, "root", "admin");
		}

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in connecting to Mysql ProdDB1 Datasource: Exception caught : "+e.getMessage());
		}

		return conn;
	}*/
	//DF20190321:mani:new schema connection for wise_tracebility schema in 67:3306
	public static DataSource getTracebilityDS()
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		DataSource datasource=null;
		try{
			iLogger.info("Mysql getTracebilityDS Datasource: Get DS");
			Context initialContext=new InitialContext();
			datasource=(DataSource)initialContext.lookup("java:jboss/datasources/Traceability");
			
		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in connecting to Mysql wise_tracebility Datasource: Exception caught : "+e.getMessage());
		}
		return datasource;
		
	}
	
	//CR321.sn data source for WiseTracebility3306
	public static DataSource getWiseTracebility3306DS()
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		DataSource datasource=null;
		try{
			iLogger.info("Mysql WiseTracebility (3306) Datasource: Get DS");
			Context initialContext=new InitialContext();
			datasource=(DataSource)initialContext.lookup("java:jboss/datasources/wisetraceability");
			
		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in connecting to Mysql wise_tracebility(3306) Datasource: Exception caught : "+e.getMessage());
		}
		return datasource;
		
	}
	//CR321.en
	//DF20190321:mani:new schema connection for wise_tracebility schema in 67:3306
	public Connection getConnection_tracebility()
	{
		Connection conn=null;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		try{
			iLogger.info("Mysql tracebilityDS Datasource: Get New Connection");
			conn = tracebilityDS.getConnection();
			iLogger.info("Mysql tracebilityDS Datasource: Connection Established.");
		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in connecting to Mysql tracebilityDS Datasource: Exception caught : "+e.getMessage());

		}		
		return conn;
		
	}
	
	
	
	//DF20190321:mani:new schema connection for wise_tracebility schema in 67:3306
/*public Connection getConnection_tracebility()
	{
		Connection conn=null;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		try
		{
			String dbUrl="jdbc:mysql://10.179.12.67:3306/wise_traceability";
			//FOR SIT
			//String dbUrl  = "jdbc:mysql://10.106.68.8:3306/wise_prod"; //ProductionDump
			Class.forName("com.mysql.jdbc.Driver");
			conn=DriverManager.getConnection(dbUrl, "root", "admin");
		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in connecting to Mysql wise_tracebility Datasource: Exception caught : "+e.getMessage());
		}
		return conn;

	}*/
	
  public Connection getEdgeProxyConnection() {
		Connection conn = null;
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		
		try 
		{
			iLogger.info("Mysql ProdDB1EPSMARTDS Datasource: Get New Connection");
			conn = prodDB1EPSmartDatasource.getConnection();
			
		
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in connecting to Mysql ProdDB1EPSMARTDS Datasource: Exception caught : "+e.getMessage());
		}		
		return conn;
	}
	
	public Connection getEdgeProxyNewConnection() {//connection to schema edgeproxy
		Connection conn = null;
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		
		try 
		{
			iLogger.info("Mysql ProdDB1EPDS Datasource: Get New Connection");
			conn = prodDB1EdgeProxyDatasource.getConnection();
			
		
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in connecting to Mysql ProdDB1EPDS Datasource: Exception caught : "+e.getMessage());
		}		
		return conn;	
	}
	
	
	//end
	
	public static DataSource getWISEDS_PERCONA()
	{
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		DataSource datasource=null;
		
		try 
		{
			Context initialContext = new InitialContext();
			iLogger.info("MySQL Datasource for wise : Get New DS");
			datasource = (DataSource)initialContext.lookup("java:jboss/datasources/WISEDS_PERCONA");
					
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in connecting to MySQL Datasource for wise PERCONA Schema: Exception caught : "+e.getMessage());
		}
		
		return datasource;
	}
	
	public Connection getETLConnection() 
	{
		Connection conn = null;
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		
		try 
		{
			iLogger.info("Mysql ProdDB1 Datasource: Get New Connection");
			conn = prodDB1ETLDatasource.getConnection();
			
		
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in connecting to Mysql ProdDB1 Datasource: Exception caught : "+e.getMessage());
		}		
		return conn;
	}
	
	public Connection getConnection() 
{
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
		
//		String dbUrl = "";
//
//		dbUrl = "jdbc:mysql://10.210.196.240:3306/wise"; //Stagging
//	
//	
//	try {
//		Class.forName("com.mysql.jdbc.Driver");
//		con = DriverManager.getConnection(dbUrl, "root", "root@123#");
//	} catch (Exception e) {
//		e.printStackTrace();
//	}
//	return con;
	}
	
	//CR321.sn
	public Connection getConnection_wise_traceability3306() 
	{
		Connection conn = null;
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		
		try 
		{
			iLogger.info("Mysql ProdDB1 Datasource: Get New Connection");
			conn = wise_tracebilityDS.getConnection();
	
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in connecting to Mysql ProdDB1 Datasource: Exception caught : "+e.getMessage());
		}		
		return conn;
	}
	//CR321.en
	public Connection getConnection_UPTIME() 
	{
		Connection conn = null;
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		
		try 
		{
			iLogger.info("Mysql PRODDB1DS_UPTIME Datasource: Get New Connection");
			conn = datasource3.getConnection();
			
		
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in connecting to Mysql PRODDB1DS_UPTIME Datasource: Exception caught : "+e.getMessage());
		}		
		return conn;
	}
	
	public Connection getConnection_percona() 
	{
		Connection conn = null;
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		
		try 
		{
		   iLogger.info("MySQL Datasource: Get New Connection from WISEDS_PERCONA: wise");
			conn = datasource1.getConnection();
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in getting a new connection from MySQL WISEDS_PERCONA Datasource: Exception caught : "+e.getMessage());
		}		
		return conn;
	}
	
	public Connection getDatalakeConnection_3309() 
	{
		Connection conn = null;
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		
		try 
		{
		   iLogger.info("MySQL Datasource: Get New Connection from WISEDS_DATALAKE: wise");
			conn = datasource2.getConnection();
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in getting a new connection from MySQL WISEDS_DATALAKE Datasource: Exception caught : "+e.getMessage());
		}		
		return conn;
	}
	
	public Connection getProdDb2Connection() {
		Connection conn = null;
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		
		try 
		{
			iLogger.info("Mysql ProdDB2 Datasource: Get New Connection");
			conn = prodDB2Datasource.getConnection();
			
		
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in connecting to Mysql ProdDB2 Datasource: Exception caught : "+e.getMessage());
		}		
		return conn;
	}
	

	/*public Connection getConnection() {
		String dbUrl = "";

			dbUrl = "jdbc:mysql://10.179.12.66:3306/wise"; //Production
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbUrl, "epuser", "epuser");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	
	
	public Connection getConnection_percona() 
	{
		String dbUrl = "";
		Properties deployEnvProp = new Properties();
		deployEnvProp = CommonUtil.getDepEnvProperties();
		String productionEnv = deployEnvProp.getProperty("deployenvironment");

	    if (productionEnv.equalsIgnoreCase("PROD")){
			dbUrl = "jdbc:mysql://10.179.12.66:5306/wise"; //Production
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbUrl, "root", "admin");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	public Connection getDatalakeConnection_3309() {
		String dbUrl = "";
		Logger fLogger = FatalLoggerClass.logger;
			dbUrl = "jdbc:mysql://10.179.12.66:3309/wise"; //Production
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbUrl, "root", "admin");
		} catch (JDBCException e) {
			
			fLogger.fatal("JDBCException Exception : " + e);
		}
		catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("Exception : " + e);
		}
		return con;
	}
	
	public Connection getProdDb2Connection() {
		String dbUrl = "";
		Logger fLogger = FatalLoggerClass.logger;
			dbUrl = "jdbc:mysql://10.179.12.67:3306/mool_insight"; //Production
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbUrl, "root", "admin");
		} catch (Exception e) {
			
			fLogger.fatal("SQL Exception : " + e);
		}
		return con;
	}*/
	
	/*	public Connection getEdgeProxyNewConnection_local() {//connection to schema edgeproxy
		
			String edgeProxyUrl = "jdbc:mysql://10.179.12.66:4306/edgeproxy"; //Production
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(edgeProxyUrl, "root", "admin");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	
	
	public Connection getConnection() {
		String dbUrl = "";
		Properties deployEnvProp = new Properties();
		deployEnvProp = CommonUtil.getDepEnvProperties();
		String productionEnv = deployEnvProp.getProperty("deployenvironment");

		if (productionEnv.equalsIgnoreCase("SIT")) {
			dbUrl = "jdbc:mysql://10.106.68.8:3306/wisetest"; // SIT
		} else if (productionEnv.equalsIgnoreCase("DEV")) {
			 dbUrl = "jdbc:mysql://10.106.68.5:3306/eaintegrationwise"; //Dev
		} else if (productionEnv.equalsIgnoreCase("QA")) {
			 dbUrl = "jdbc:mysql://10.106.68.13:3306/wiseintegratedQA"; //QA
		} else if (productionEnv.equalsIgnoreCase("PROD")){
			dbUrl = "jdbc:mysql://10.179.12.66:3306/wise"; //Production
		}else {
			dbUrl = "jdbc:mysql://localhost:3306/wise"; //Localhost
		} 
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbUrl, "epuser", "epuser");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public void closeConnection(Connection conn) {
		Logger fLogger = FatalLoggerClass.logger;
		try {
			conn.close();
		} catch (SQLException e) {
			fLogger.fatal("SQL Exception : " + e);
		}
	}

	// DefectID: DF20131107 - Rajani Nagaraju - Update the
	// Status(Normal/Intransit) of the Machine to Edge Proxy table
	public Connection getEdgeProxyConnection() {
		String edgeProxyUrl = "jdbc:mysql://10.106.68.8:3306/smart_systems"; // SIT
		Properties deployEnvProp = new Properties();
		deployEnvProp = CommonUtil.getDepEnvProperties();
		String productionEnv = deployEnvProp.getProperty("deployenvironment");

		if (productionEnv.equalsIgnoreCase("SIT")) {
			edgeProxyUrl = "jdbc:mysql://10.106.68.8:3306/smart_systems"; // SIT
		} else if (productionEnv.equalsIgnoreCase("DEV")) {
			edgeProxyUrl = "jdbc:mysql://10.106.68.5:3306/smart_systems"; // Dev
		} else if (productionEnv.equalsIgnoreCase("QA")) {
			edgeProxyUrl = "jdbc:mysql://10.106.68.13:3306/smart_systems"; // QA
		} else if (productionEnv.equalsIgnoreCase("PROD") ){
			edgeProxyUrl = "jdbc:mysql://10.179.12.66:3306/smart_systems"; //Production
		}else{
			edgeProxyUrl = "jdbc:mysql://localhost:3306/smart_systems"; //localhost
		}
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(edgeProxyUrl, "epuser", "epuser");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	public Connection getEdgeProxyNewConnection() {//connection to schema edgeproxy
		String edgeProxyUrl = "jdbc:mysql://10.106.68.8:3306/edgeproxy"; // SIT
		Properties deployEnvProp = new Properties();
		deployEnvProp = CommonUtil.getDepEnvProperties();
		String productionEnv = deployEnvProp.getProperty("deployenvironment");

		if (productionEnv.equalsIgnoreCase("SIT")) {
			edgeProxyUrl = "jdbc:mysql://10.106.68.8:3306/edgeproxy"; // SIT
		} else if (productionEnv.equalsIgnoreCase("DEV")) {
			edgeProxyUrl = "jdbc:mysql://10.106.68.5:3306/edgeproxy"; // Dev
		} else if (productionEnv.equalsIgnoreCase("QA")) {
			edgeProxyUrl = "jdbc:mysql://10.106.68.13:3306/edgeproxy"; // QA
		} else if (productionEnv.equalsIgnoreCase("PROD") ){
			edgeProxyUrl = "jdbc:mysql://10.179.12.66:3306/edgeproxy"; //Production
		}else{
			edgeProxyUrl = "jdbc:mysql://localhost:3306/edgeproxy"; //localhost
		}
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(edgeProxyUrl, "epuser", "epuser");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public Connection getProdDb2Connection() {
		String dbUrl = "";
		Logger fLogger = FatalLoggerClass.logger;
			dbUrl = "jdbc:mysql://10.179.12.67:3306/mool_insight"; //Production
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbUrl, "root", "admin");
		} catch (Exception e) {
			
			fLogger.fatal("SQL Exception : " + e);
		}
		return con;
	}
	
	*/
}
