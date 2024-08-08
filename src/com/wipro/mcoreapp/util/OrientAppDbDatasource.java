package com.wipro.mcoreapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.logging.log4j.Logger;

/*import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.serialization.serializer.record.string.ORecordSerializerSchemaAware2CSV;
import com.orientechnologies.orient.jdbc.OrientJdbcConnection;*/

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class OrientAppDbDatasource 
{
	/** Method to Get a connection from orientdb datasource
	 * @return SQL Connection object on SUCCESS;
	 * 		   NULL otherwise
	 */
	public static DataSource datasource=getOrientDS();
		
	
	public static DataSource getOrientDS()
	{
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		DataSource datasource=null;
		
		try 
		{
			Context initialContext = new InitialContext();
			iLogger.info("Orient App DB Datasource: Get New DS");
			datasource = (DataSource)initialContext.lookup("java:jboss/datasources/OrientAppdbDS");
					
		}
		
		catch(Exception e)
		{
			fLogger.error("Error in connecting to OrientDB Datasource: Exception caught : "+e.getMessage());
		}
		
		return datasource;
	}
	
	public Connection getConnection() 
	{
		Connection conn = null;
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		
		try 
		{
			iLogger.info("Orient App DB Datasource: Get New Connection");
			conn = datasource.getConnection();
			
			/* OGlobalConfiguration.DB_DOCUMENT_SERIALIZER.setValue(ORecordSerializerSchemaAware2CSV.NAME); 			
			 Class.forName("com.orientechnologies.orient.jdbc.OrientJdbcDriver");
		     Properties info = new Properties();
		     info.put("user", "admin");
		     info.put("password", "admin");
		     conn = (OrientJdbcConnection)DriverManager.getConnection("jdbc:orient:plocal:E:\\2015-2016\\Installations\\orientdb-community-2.0.6\\databases\\Mool_S01", info);
		    */
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.error("Error in connecting to OrientDB Datasource: Exception caught : "+e.getMessage());
		}
		
		return conn;
	}
	
}
