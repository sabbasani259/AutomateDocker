/**
 * 
 */
package remote.wise.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.logging.log4j.Logger;





import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

/**
 * @author roopn5
 *
 */
public class OrientAppDbDatasource {

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
			
						
		}
		
		catch(Exception e)
		{
			fLogger.error("Error in connecting to OrientDB Datasource: Exception caught : "+e.getMessage());
		}
		
		return conn;
	}
	
	
	//Connecting to local orientDB
	
		/*public Connection getConnection() 
		{
			Connection conn = null;
			try 
			{
				Class.forName("com.orientechnologies.orient.jdbc.OrientJdbcDriver") ;
				Properties info = new Properties();
				info.put("user", "root");
				info.put("password", "admin");
				info.put("db.usePool", "true"); // USE THE POOL
				info.put("db.pool.min", "3");   // MINIMUM POOL SIZE
				info.put("db.pool.max", "30");  // MAXIMUM POOL SIZE
				//jdbc:orient:remote:localhost/test
								
				conn = (OrientJdbcConnection) DriverManager.getConnection("jdbc:orient:remote:localhost/vorcletdemo", info);
				//conn = (OrientJdbcConnection) DriverManager.getConnection("jdbc:orient:plocal:F:\\Softwares\\orientdb-community-2.0.12\\orientdb-community-2.0.12\\orientdb-community-2.0.12\\databases\\Sample", "root", "admin");
				System.out.println("connection done");
			}
			
			catch(Exception e)
			{
				System.out.println(e);
				e.printStackTrace();
			}
			
			return conn;
		}*/
}
