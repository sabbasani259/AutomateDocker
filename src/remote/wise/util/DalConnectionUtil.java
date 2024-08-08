/**
 * CR337 : 20220721 : Dhiraj K : Property file read.
 */

package remote.wise.util;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
//import com.mysql.jdbc.Connection; //CR337.o
import java.sql.Connection; //CR337.n
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.logging.log4j.Logger;


import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class DalConnectionUtil {

	//  private static Logger lProxylogger = Logger.getLogger(DalConnectionUtil.class);
	//CR337.sn
	public static DataSource prodDB1Datasource=getProdDB1DS();

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
	
	
	private static DalConnectionUtil singleton = new DalConnectionUtil( );

	/* A private Constructor prevents any other 
	 * class from instantiating.
	 */
	private DalConnectionUtil(){ }

	/* Static 'instance' method */
	public static DalConnectionUtil getInstance( ) {
		//lProxylogger.info("Returning Singleton Instance of DAL Utilubs Class");

		return singleton;
	}
	/* Other methods protected by singleton-ness */

	/*
	 * getConnection - returns a connection object to database
	 * @arg 
	 * @return Connection - connection object
	 */
	public static Connection getConnection()
	{
		//CR337.so
		/*//  String query =  "/data/M2M/conInfo.properties";
		Logger fLogger = FatalLoggerClass.logger;
		  String query =  "/remote/wise/resource/properties/conInfo.properties";
		  Properties dbprop=DalConnectionUtil.setProperties(query);
		           //String conParam=new StringBuffer().append("jdbc:mysql://").append(dbprop.getProperty("serverName")).append(":").append(dbprop.getProperty("port")).append("/").append(dbprop.getProperty("dbase")).toString();
		String conParam;
		Properties deployEnvProp = new Properties();
		deployEnvProp = CommonUtil.getDepEnvProperties();
		String productionEnv = deployEnvProp.getProperty("deployenvironment");

		if (productionEnv.equalsIgnoreCase("SIT")) {
			conParam = new String("jdbc:mysql://10.106.68.8:3306/wiseintegratednew");// SIT
		} else if (productionEnv.equalsIgnoreCase("DEV")) {
			conParam = new String("jdbc:mysql://10.106.68.5:3306/eaintegrationwise");// DEV
		} else if (productionEnv.equalsIgnoreCase("QA")) {
			conParam = new String("jdbc:mysql://10.106.68.13:3306/wiseintegratedQA");// QA
		} else if (productionEnv.equalsIgnoreCase("PROD"))  {
			conParam = new String("jdbc:mysql://10.179.12.66:3306/wise");// production
		} else {
			conParam = new String("jdbc:mysql://localhost:3306/wise");// localhost
		}

		  //  String conParam=new String("jdbc:mysql://localhost:3306/wiseintegratedtest");		
		     //String conParam=new String("jdbc:mysql://10.106.68.5:3306/eaintegrationwise");
		//		  String conParam=new String("jdbc:mysql://10.106.68.8:3306/wiseintegratednew");		   
		   //   String conParam=new String("jdbc:mysql://10.106.68.13:3306/wiseintegratedQA");

		//		  String conParam=new String("jdbc:mysql://10.179.12.66:3306/wise");


		//String uname=dbprop.getProperty("uname");
		  String uname="root";
		//String pwd=dbprop.getProperty("pwd");
		  String pwd="admin";
		 // lProxylogger.debug("Connection Parameters:-"+conParam+"/uname-"+uname);

		Connection con=null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con =(Connection) DriverManager.getConnection (conParam,uname,pwd);
		    }
		catch(ClassNotFoundException e){fLogger.fatal("Class Not Found"+e);} 
		catch (SQLException e) {
		 //     lProxylogger.info("Connection Failed");
		}
		return con;*/
		//CR33.eo
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

	public static CallableStatement getStatement(String query)
	{
		CallableStatement cstmt=null;
		Connection c=null;

		try
		{
			c=DalConnectionUtil.getConnection();
			if (c!=null)
				cstmt=(CallableStatement) c.prepareCall(query);

		}
		catch (SQLException e) {
			//  lProxylogger.info("Statement Failed");
		}
		return cstmt;

	}
	public static void closeConnection(java.sql.Connection con)
	{
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//   lProxylogger.info("Error in Closing Connection");
		}
	}
	public static void closeConnection(java.sql.CallableStatement o)
	{
		try {
			o.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//    lProxylogger.info("Error in Closing Statement");
		}
	}
	public static Properties setProperties(String query) {
		Properties dbprop = new Properties();
		try {
			dbprop.load(new FileInputStream(query));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//   lProxylogger.info("Error in Opening Properties File");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//    lProxylogger.info("IO Exception");
		}
		return dbprop;
	}
}

