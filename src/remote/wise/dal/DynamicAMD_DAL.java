package remote.wise.dal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import remote.wise.businessentity.AssetMonitoringDetailEntity;
import remote.wise.interfaces.AMD;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AmdDAO;
import remote.wise.service.implementation.AssetEventLogImpl;
import remote.wise.service.implementation.MachineHealthDetailsImpl;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.StaticProperties;

/*
 * author S Suresh 
 * DT 20160616 
 * Introducing new layer DAL in between BO and Data Storage 
 * by writing core business logic in BO and data access logic in DAL    
 */

public class DynamicAMD_DAL implements AMD{

	@Override
	public String setAMDData(int transaction_number, int parameter_id,
			String param_value, int segmentid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updateAMDData(int transaction_number, int parameter_id,
			String param_value, int segmentid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AmdDAO> getAMDData(String txnKey, int transaction_number, String txnTimestamp, int seg_id, String parameterListAsString) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String AMDSelectQuery=null;

		List<AmdDAO>AmdDAOList=new ArrayList<AmdDAO>();


		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal(txnKey+":AMS:DAL-AMD-getAMDData"+"Error in intializing property File :"+e.getMessage());
		}

		String amdTable = prop.getProperty("default_AMD_Table");


		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(txnKey+":AMS:DAL-AMD-getAMDData"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{
			if(txnTimestamp!=null){

				/*<!=========START persisting in In Native Database==============>*/

				SimpleDateFormat dateTimeFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


				Date txnDate = null;
				try {
					txnDate = dateTimeFormat.parse(txnTimestamp);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				HashMap<String, String> dynamicTables = new DateUtil().getCurrentWeekDifference(txnDate);

				if(dynamicTables!=null && dynamicTables.size()>0){

					amdTable = dynamicTables.get("AMD");

					//iLogger.info(txnKey+":AMS:DAL-AMD-getAMDData"+"AMD Table::"+amdTable);
				}

				if(amdTable!=null){
					if(parameterListAsString!=null)
					AMDSelectQuery="Select * from "+amdTable+" where Segment_ID_TxnDate="+seg_id+" and Transaction_Number="+transaction_number+" and parameter_id in("+parameterListAsString+")";
					else
						AMDSelectQuery="Select * from "+amdTable+" where Segment_ID_TxnDate="+seg_id+" and Transaction_Number="+transaction_number+"";
					
					//iLogger.info(txnKey+":AMS:DAL-AMD-getAMDData"+"AMD Select Query::"+AMDSelectQuery);
				}

				try{

					ConnectMySQL connMySql = new ConnectMySQL();
					prodConnection = connMySql.getConnection_percona();
					statement = prodConnection.createStatement();
					rs = statement.executeQuery(AMDSelectQuery);

					AmdDAO amdDAOobject; 

					while(rs.next()){
						amdDAOobject=new AmdDAO();

						amdDAOobject.setParameter_ID(rs.getInt("Parameter_ID"));
						amdDAOobject.setParameter_Value(rs.getString("Parameter_Value"));
						amdDAOobject.setSegment_ID_TxnDate(rs.getInt("Segment_ID_TxnDate"));
						amdDAOobject.setTransaction_Number(rs.getInt("Transaction_Number"));
						AmdDAOList.add(amdDAOobject);
					}
				}

				catch (SQLException e) {

					e.printStackTrace();
					fLogger.fatal(txnKey+":AMS:DAL-AMD-getAMDData"+"SQL Exception in fetching data from mysql::"+e.getMessage());
				} 

				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.fatal(txnKey+":AMS:DAL-AMD-getAMDData"+"Exception in fetching data from mysql::"+e.getMessage());
				}

				finally {
					if(rs!=null)
						try {
							rs.close();
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

					if (prodConnection != null) {
						try {
							prodConnection.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}

				}



				/*<!=========END persisting in In Native Database==============>*/
			}

		}


		return AmdDAOList;
	}
	
	//DF20161021 @Roopa Machine health detail changes
	
	public HashMap<String,String>  getMachineHealthData(String query) {
		
		HashMap<String,String> transactionDataMap = null;

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		

		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal(":AMD:DAL-AMD-getMachineHealthData"+"Error in intializing property File :"+e.getMessage());
		}
		
	

		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(":AMD:DAL-AMD-getMachineHealthData"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{

			/*<!=========START fetching from Native Database==============>*/


			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection_percona();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(query);

				
				while(rs.next()){
					
					
					List<String> paramIdList = new LinkedList<String>();
					List<String> paramValueList = new LinkedList<String>();
					
					String paramId= rs.getString("Parameter_ID");
					String paramValue= rs.getString("Parameter_Value");
					
					transactionDataMap = new HashMap<String, String>();
					
					if(paramId!=null)
						paramIdList = Arrays.asList(paramId.split(","));
						else
							paramIdList=new LinkedList<String>();
						
						if(paramValue!=null)
							paramValueList = Arrays.asList(paramValue.split(","));
							else
								paramValueList=new LinkedList<String>();
						
						
						for(int i=0;i<paramIdList.size();i++){
							transactionDataMap.put(paramIdList.get(i), paramValueList.get(i));	
						}
						
					
				}
			}
			catch (SQLException e) {

				e.printStackTrace();
				fLogger.fatal(":AMD:DAL-AMD-getMachineHealthData"+"SQL Exception in fetching data from mysql::"+e.getMessage());
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal(":AMD:DAL-AMD-getMachineHealthData"+"Exception in fetching data from mysql::"+e.getMessage());
			}

			finally {
				if(rs!=null)
					try {
						rs.close();
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

				if (prodConnection != null) {
					try {
						prodConnection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
			
		
		
		return transactionDataMap;
		
	}

}
