/**
 * 
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import remote.wise.handler.EmailHandler;
import remote.wise.handler.EmailTemplate;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;

/**
 * @author roopn5
 *
 */
public class DynamicAMH_AMD_AMDE_TableImpl {

	public String createTable(int weekNo, int year){
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Properties prop = CommonUtil.getDepEnvProperties();
		String amhTable = null;
		String amdTable = null;
		String amdETable = null;
		String tAssetMonTable=null;
		String JCB_AMH_TableKey = prop.getProperty("JCB_AMH_TableKey");
		String JCB_AMD_TableKey = prop.getProperty("JCB_AMD_TableKey");
		String JCB_AMDE_TableKey = prop.getProperty("JCB_AMDE_TableKey");
		
		String JCB_tAssetMonTableKey = prop.getProperty("JCB_TAssetMonData_TableKey");

		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.WEEK_OF_YEAR, 1); //DF20170102 @Roopa incrementing the week by 1 to get next week number

		int currentWeekNo = cal.get(Calendar.WEEK_OF_YEAR);
		int currentYear = cal.get(Calendar.YEAR);


			//currentWeekNo = currentWeekNo + 1;
    

		if(weekNo!=0 && year!=0){
			/*amhTable = JCB_AMH_TableKey.replace("week", weekNo+"");
			amhTable =amhTable.replace("year", year+"");
			amdTable = JCB_AMD_TableKey.replace("week", weekNo+"");
			amdTable =amdTable.replace("year", year+"");
			amdETable = JCB_AMDE_TableKey.replace("week", weekNo+"");
			amdETable =amdETable.replace("year", year+"");*/
			
			tAssetMonTable = JCB_tAssetMonTableKey.replace("week", weekNo+"");
			tAssetMonTable =tAssetMonTable.replace("year", year+"");
		}
		else{
			/*amhTable = JCB_AMH_TableKey.replace("week", currentWeekNo+"");
			amhTable =amhTable.replace("year", currentYear+"");
			amdTable = JCB_AMD_TableKey.replace("week", currentWeekNo+"");
			amdTable =amdTable.replace("year", currentYear+"");
			amdETable = JCB_AMDE_TableKey.replace("week", currentWeekNo+"");
			amdETable =amdETable.replace("year", currentYear+"");*/
			
			tAssetMonTable = JCB_tAssetMonTableKey.replace("week", currentWeekNo+"");
			tAssetMonTable =tAssetMonTable.replace("year", currentYear+"");
		}

		iLogger.info("AMH::"+amhTable+","+"AMD::"+amdTable+","+"AMDE::"+amdETable+"TASSETMON::"+tAssetMonTable);

		Connection prodConnection = null;
		Statement statement = null;
		ConnectMySQL connMySql = new ConnectMySQL();

/*		//STEP 1:Dynamic AMH table creation

		try{
			prodConnection = connMySql.getConnection_percona();
			statement = prodConnection.createStatement();

			String amhCreateQuery="CREATE TABLE IF NOT EXISTS `wise`.`"+amhTable+"` (" +

  "`Transaction_Number` INT(11) NOT NULL AUTO_INCREMENT ," +

  "`Transaction_Timestamp` DATETIME NULL ,"+

  "`Serial_Number` VARCHAR(45) NULL DEFAULT NULL ," +

  "`Created_Timestamp` TIMESTAMP NULL DEFAULT NULL ," +

  "`Record_Type_Id` INT(11) NULL DEFAULT NULL ," +

  "`FW_Version_Number` VARCHAR(11) NULL DEFAULT NULL ," +

  "`Last_Updated_Timestamp` TIMESTAMP NULL ," +

  "`Update_Count` INT(11) NULL DEFAULT '0' ," +

  "`Segment_ID_TxnDate` INT(11) NOT NULL DEFAULT '0' ," +

  "PRIMARY KEY (`Transaction_Number`, `Segment_ID_TxnDate`) ," +

  "UNIQUE INDEX `Transaction_Timestamp` (`Serial_Number` ASC, `Transaction_Timestamp` ASC, `Segment_ID_TxnDate` ASC) ," +

  "INDEX `FK_AssetHeader_VIN_idx` (`Serial_Number` ASC) ) PARTITION BY HASH(Segment_ID_TxnDate) PARTITIONS 300";


			//iLogger.info("amhCreateQuery :: "+amhCreateQuery);

			statement.executeUpdate(amhCreateQuery);
			iLogger.info("Created "+amhTable+" table in given database...");
		}

		catch (SQLException e) {

			e.printStackTrace();
			fLogger.fatal("DynamicAMH_AMD_AMDE_TableCreationRESTService::createTable:"+"SQL Exception in creating "+amhTable+" table in mysql::"+e.getMessage());
			return "FAILURE";
		} 

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("DynamicAMH_AMD_AMDE_TableCreationRESTService::createTable:"+"Exception in creating "+amhTable+" table in mysql::"+e.getMessage());
			return "FAILURE";
		}

		finally {

			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		//STEP 2:Dynamic AMD table creation

		try{
			prodConnection = connMySql.getConnection_percona();
			statement = prodConnection.createStatement();

			String amdCreateQuery="CREATE TABLE IF NOT EXISTS `wise`.`"+amdTable+"` (" +

  "`Transaction_Number` INT(11) NOT NULL ," +

  "`Parameter_ID` INT(11) NOT NULL ," +

  "`Parameter_Value` VARCHAR(45) NULL ," +

  "`Segment_ID_TxnDate` INT(11) NOT NULL DEFAULT '0' ," +

  "PRIMARY KEY (`Transaction_Number`, `Parameter_ID`, `Segment_ID_TxnDate`) )" +

  " PARTITION BY HASH(Segment_ID_TxnDate) PARTITIONS 300";



			statement.executeUpdate(amdCreateQuery);
			iLogger.info("Created "+amdTable+" table in given database...");
		}

		catch (SQLException e) {

			e.printStackTrace();
			fLogger.fatal("DynamicAMH_AMD_AMDE_TableCreationRESTService::createTable:"+"SQL Exception in creating "+amdTable+" table in mysql::"+e.getMessage());
			return "FAILURE";
		} 

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("DynamicAMH_AMD_AMDE_TableCreationRESTService::createTable:"+"Exception in creating "+amdTable+" table in mysql::"+e.getMessage());
			return "FAILURE";
		}

		finally {

			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		//STEP 3:Dynamic AMDE table creation

		try{
			prodConnection = connMySql.getConnection_percona();
			statement = prodConnection.createStatement();

			String amdCreateQuery="CREATE TABLE IF NOT EXISTS `wise`.`"+amdETable+"` (" +

  "`Transaction_Number` INT(11) NOT NULL ," +

  "`Extended_Parameter_ID` INT(11) NOT NULL ," +

  "`Extended_Parameter_Value` VARCHAR(45) NULL ," +

  "`Segment_ID_TxnDate` INT(11) NOT NULL DEFAULT '0' ," +

  "PRIMARY KEY (`Transaction_Number`, `Extended_Parameter_ID`, `Segment_ID_TxnDate`) ," +

  "INDEX `FK_Trans_num_idx` (`Transaction_Number` ASC) )" +

  " PARTITION BY HASH(Segment_ID_TxnDate) PARTITIONS 300";




			statement.executeUpdate(amdCreateQuery);
			iLogger.info("Created "+amdETable+" table in given database...");
		}

		catch (SQLException e) {

			e.printStackTrace();
			fLogger.fatal("DynamicAMH_AMD_AMDE_TableCreationRESTService::createTable:"+"SQL Exception in creating "+amdETable+" table in mysql::"+e.getMessage());
			return "FAILURE";
		} 

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("DynamicAMH_AMD_AMDE_TableCreationRESTService::createTable:"+"Exception in creating "+amdETable+" table in mysql::"+e.getMessage());
			return "FAILURE";
		}

		finally {

			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}*/
		
		//STEP 3:Dynamic TAssetMonData table creation in 3309 port

				try{
					prodConnection = connMySql.getDatalakeConnection_3309();
					statement = prodConnection.createStatement();

					String amdCreateQuery="CREATE TABLE IF NOT EXISTS `wise`.`"+tAssetMonTable+"` (" +

		  "`Serial_Number` VARCHAR(45) NOT NULL ," +

		  "`Transaction_Timestamp` DATETIME NOT NULL ," +

		  "`Created_Timestamp` TIMESTAMP NULL ," +

		  "`Last_Updated_Timestamp` TIMESTAMP NULL ," +
		  "`Message_ID` JSON NULL ," +
		  "`Events` JSON NULL," +
		  "`TxnData` JSON NULL," +
		  "`Segment_ID_TxnDate` INT(4) NOT NULL," +
		  "`FW_Version_Number` VARCHAR(11) NULL," +
		   "`Update_Count` INT(11) NULL," +

		  "PRIMARY KEY (`Serial_Number`, `Transaction_Timestamp`, `Segment_ID_TxnDate`) ," +

		  "INDEX `AssetMon_VIN_Txn_idx` (`Serial_Number` ASC,`Transaction_Timestamp` ASC,`Segment_ID_TxnDate` ASC) )" +

		  " PARTITION BY HASH(Segment_ID_TxnDate) PARTITIONS 300";




					statement.executeUpdate(amdCreateQuery);
					iLogger.info("Created "+tAssetMonTable+" table in given database...");
				}

				catch (SQLException e) {

					e.printStackTrace();
					fLogger.fatal("DynamicAMH_AMD_AMDE_TableCreationRESTService::createTable:"+"SQL Exception in creating "+tAssetMonTable+" table in mysql::"+e.getMessage());
					return "FAILURE";
				} 

				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("DynamicAMH_AMD_AMDE_TableCreationRESTService::createTable:"+"Exception in creating "+tAssetMonTable+" table in mysql::"+e.getMessage());
					return "FAILURE";
				}

				finally {

					if(statement!=null)
						try {
							statement.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					if (prodConnection != null) {
						try {
							prodConnection.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}

				iLogger.info ("DynamicAMH_AMD_AMDE_TableCreationRESTService::createTable:"+"sending Email : START");
				
				try{
					
					String toList="deepthi.rao@wipro.com,roopa.n87@wipro.com";
					
					String body="Hi, The following tables created successfully in production database.;;AMH::"+amhTable+", "+"AMD::"+amdTable+", "+"AMDE::"+amdETable+", "+"TASSETMON::"+tAssetMonTable+".";
				
					body = body.replaceAll(";", "\n");
					
					
				EmailTemplate emailObj = new EmailTemplate();
				emailObj.setTo(toList);
				emailObj.setSubject("Alert for Dynamic Table creation");
				emailObj.setBody(body);
				
				new EmailHandler().handleEmail("jms/queue/emailQ", emailObj,0);
				}
				catch(Exception e){
					e.printStackTrace();
					fLogger.fatal("DynamicAMH_AMD_AMDE_TableCreationRESTService::createTable:"+"Exception in sending email::"+e.getMessage());
					
				}
			

		return "SUCCESS "+tAssetMonTable;


	}

	public String purgeTable(int weekNo, int year){

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Properties prop = CommonUtil.getDepEnvProperties();
		String amhTable = null;
		String amdTable = null;
		String amdETable = null;
		String tAssetMonTable=null;
		String JCB_AMH_TableKey = prop.getProperty("JCB_AMH_TableKey");
		String JCB_AMD_TableKey = prop.getProperty("JCB_AMD_TableKey");
		String JCB_AMDE_TableKey = prop.getProperty("JCB_AMDE_TableKey");
		String JCB_tAssetMonTableKey = prop.getProperty("JCB_TAssetMonData_TableKey");

		Calendar cal = Calendar.getInstance();
		
		//DF20160102 @Roopa subtracting 8 week's from java calendar to get 8 week's old table
		
		cal.add(Calendar.WEEK_OF_YEAR, -8); 

		int currentWeekNo = cal.get(Calendar.WEEK_OF_YEAR);
		int currentYear = cal.get(Calendar.YEAR);
		
		/*int currentWeekNo = cal.get(Calendar.WEEK_OF_YEAR);

		currentWeekNo = currentWeekNo - 8;

		int currentYear = cal.get(Calendar.YEAR);*/

		if(weekNo!=0 && year!=0){
			/*amhTable = JCB_AMH_TableKey.replace("week", weekNo+"");
			amhTable =amhTable.replace("year", year+"");
			amdTable = JCB_AMD_TableKey.replace("week", weekNo+"");
			amdTable =amdTable.replace("year", year+"");
			amdETable = JCB_AMDE_TableKey.replace("week", weekNo+"");
			amdETable =amdETable.replace("year", year+"");*/
			
			tAssetMonTable = JCB_tAssetMonTableKey.replace("week", weekNo+"");
			tAssetMonTable =tAssetMonTable.replace("year", year+"");
		}
		else{
			/*amhTable = JCB_AMH_TableKey.replace("week", currentWeekNo+"");
			amhTable =amhTable.replace("year", currentYear+"");
			amdTable = JCB_AMD_TableKey.replace("week", currentWeekNo+"");
			amdTable =amdTable.replace("year", currentYear+"");
			amdETable = JCB_AMDE_TableKey.replace("week", currentWeekNo+"");
			amdETable =amdETable.replace("year", currentYear+"");*/
			
			tAssetMonTable = JCB_tAssetMonTableKey.replace("week", currentWeekNo+"");
			tAssetMonTable =tAssetMonTable.replace("year", currentYear+"");
		}

		iLogger.info("AMH::"+amhTable+","+"AMD::"+amdTable+","+"AMDE::"+amdETable);

		Connection prodConnection = null;
		Statement statement = null;
		ConnectMySQL connMySql = new ConnectMySQL();

		/*//STEP 1: Purge Dynamic AMH table 

		try{
			prodConnection = connMySql.getConnection_percona();
			statement = prodConnection.createStatement();

			String amhPurgeQuery="drop table IF EXISTS `wise`.`"+amhTable+"`";

			statement.executeUpdate(amhPurgeQuery);
			iLogger.info(" "+amhTable+" table dropped from the given database...");

		}
		catch (SQLException e) {

			e.printStackTrace();
			fLogger.fatal("DynamicAMH_AMD_AMDE_PurgeRESTService::purgeTable:"+"SQL Exception in dropping "+amhTable+" table in mysql::"+e.getMessage());
			return "FAILURE";
		} 

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("DynamicAMH_AMD_AMDE_PurgeRESTService::purgeTable:"+"Exception in dropping "+amhTable+" table in mysql::"+e.getMessage());
			return "FAILURE";
		}

		finally {

			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		//STEP 2: Purge Dynamic AMD table 

		try{
			prodConnection = connMySql.getConnection_percona();
			statement = prodConnection.createStatement();

			String amhPurgeQuery="drop table IF EXISTS `wise`.`"+amdTable+"`";

			statement.executeUpdate(amhPurgeQuery);
			iLogger.info(" "+amdTable+" table dropped from the given database...");

		}
		catch (SQLException e) {

			e.printStackTrace();
			fLogger.fatal("DynamicAMH_AMD_AMDE_PurgeRESTService::purgeTable:"+"SQL Exception in dropping "+amdTable+" table in mysql::"+e.getMessage());
			return "FAILURE";
		} 

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("DynamicAMH_AMD_AMDE_PurgeRESTService::purgeTable:"+"Exception in dropping "+amdTable+" table in mysql::"+e.getMessage());
			return "FAILURE";
		}

		finally {

			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		//STEP 2: Purge Dynamic AMDE table 

		try{
			prodConnection = connMySql.getConnection_percona();
			statement = prodConnection.createStatement();

			String amhPurgeQuery="drop table IF EXISTS `wise`.`"+amdETable+"`";

			statement.executeUpdate(amhPurgeQuery);
			iLogger.info(" "+amdETable+" table dropped from the given database...");

		}
		catch (SQLException e) {

			e.printStackTrace();
			fLogger.fatal("DynamicAMH_AMD_AMDE_PurgeRESTService::purgeTable:"+"SQL Exception in dropping "+amdETable+" table in mysql::"+e.getMessage());
			return "FAILURE";
		} 

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("DynamicAMH_AMD_AMDE_PurgeRESTService::purgeTable:"+"Exception in dropping "+amdETable+" table in mysql::"+e.getMessage());
			return "FAILURE";
		}

		finally {

			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}*/
		
		//STEP 2: Purge Dynamic TAssetMonData table in 3309 port

				try{
					prodConnection = connMySql.getDatalakeConnection_3309();
					statement = prodConnection.createStatement();

					String amhPurgeQuery="drop table IF EXISTS `wise`.`"+tAssetMonTable+"`";

					statement.executeUpdate(amhPurgeQuery);
					iLogger.info(" "+tAssetMonTable+" table dropped from the given database...");

				}
				catch (SQLException e) {

					e.printStackTrace();
					fLogger.fatal("DynamicAMH_AMD_AMDE_PurgeRESTService::purgeTable:"+"SQL Exception in dropping "+tAssetMonTable+" table in mysql::"+e.getMessage());
					return "FAILURE";
				} 

				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal("DynamicAMH_AMD_AMDE_PurgeRESTService::purgeTable:"+"Exception in dropping "+tAssetMonTable+" table in mysql::"+e.getMessage());
					return "FAILURE";
				}

				finally {

					if(statement!=null)
						try {
							statement.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					if (prodConnection != null) {
						try {
							prodConnection.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}

		return "SUCCESS "+tAssetMonTable;
	}

}
