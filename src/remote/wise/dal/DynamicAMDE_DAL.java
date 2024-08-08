package remote.wise.dal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import remote.wise.businessentity.AssetMonitoringDetailEntity;
import remote.wise.interfaces.AMDE;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AmdeDAO;
import remote.wise.pojo.AssetMonitoringParametersDAO;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;

public class DynamicAMDE_DAL implements AMDE{

	@Override
	public String setAMDEData(int transaction_number, int parameter_id,
			String param_value, int segmentid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updateAMDEData(int transaction_number, int parameter_id,
			String param_value, int segmentid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AmdeDAO> getAMDEData(Timestamp txnTimestamp,
			int transaction_number, int segmentid,String Serial_Number) {
		// TODO Auto-generated method stub
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<AssetMonitoringParametersDAO> AMPList = new LinkedList<AssetMonitoringParametersDAO>();
		
		iLogger.info("AMDE_DAL getAMDEData: for "+Serial_Number+" and SegmentID "+segmentid+" on "+txnTimestamp+" START");
		
		String ampSelectQuery = "";
		String ampFromQuery = "";
		String ampWhereQuery = "";
		String ampGroupNyQuery = "";
		String ampOrderByQuery = "";
		String amhTable = null;
		String amdTable = null;
		String amdETable = null;
		String PersistTo_InMemory = null;
		Properties prop = null;
		
		long startTime = 0;
		long endTime = 0;
		Calendar cal = Calendar.getInstance();
		//cal.setTime(txnTimestamp);
		StringBuilder parameterListString = null;
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String TxnDate = null;
		
		//Session session = HibernateUtil.getSessionFactory().openSession();
		
		try{
			
			
			 prop = CommonUtil.getDepEnvProperties();
			 amhTable = prop.getProperty("default_AMH_Table");
			 amdTable = prop.getProperty("default_AMD_Table");
			 amdETable = prop.getProperty("default_AMDE_Table");
			
			 PersistTo_InMemory = prop.getProperty("PersistTo_InMemory");
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				fLogger.fatal("AMH_DAL getLatestEngineONTxn: for "+Serial_Number+" and SegmentID "+segmentid+" on "+txnTimestamp+" exception while retrieving properties file");
			}
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		List<AmdeDAO> responseList = new LinkedList<AmdeDAO>();
		//Timestamp TxnTime = null;
		double totalFuelUsed = 0.0;
		
		if(Boolean.parseBoolean(PersistTo_InMemory)){
			//System.out.println("Persist to In Memory storage");
		}
		else{
			
			//use Query Param to determine which query to execute   
			
			try{
			if(txnTimestamp!=null){
				HashMap<String, String> dynamicTables = new DateUtil().getCurrentWeekDifference(txnTimestamp);
				
					amhTable = dynamicTables.get("AMH");
					amdTable = dynamicTables.get("AMD");
					amdETable = dynamicTables.get("AMDE");
			}
			
			iLogger.info("AMDE_DAL getAMDEData Query starts ");
			
			TxnDate = sdf1.format(txnTimestamp);
			
			ampSelectQuery = " select sum(c.Extended_Parameter_Value) as fuelLevel ";
			ampFromQuery = " from "+amdETable+" as c right outer join "+amhTable+" as a on c.Transaction_Number = a.Transaction_Number ";
				 
			ampWhereQuery = " where a.Segment_ID_TxnDate = "+segmentid+" and a.Serial_Number = '" + Serial_Number + "'"
					+ " and a.Transaction_Timestamp like '" + TxnDate +"%' and c.Segment_ID_TxnDate = "+segmentid ;
			
			connection = connFactory.getConnection_percona();
			statement = connection.createStatement();
			startTime = System.currentTimeMillis();
			
			String mainQuery = ampSelectQuery+ampFromQuery+ampWhereQuery;
			//System.out.println(mainQuery);
			//iLogger.info("AMDE_DAL getAMDEData main Query "+mainQuery);
			iLogger.info("AMDE_DAL getAMDEData: for "+Serial_Number+" and SegmentID "+segmentid+" on "+txnTimestamp+" main Query: "+mainQuery);
			
			rs = statement.executeQuery(mainQuery);
			
			
			while(rs.next())
			{
				AmdeDAO daoObject = new AmdeDAO();
				daoObject.setExtended_Parameter_Value(rs.getString("fuelLevel"));
				responseList.add(daoObject);
			}
			endTime = System.currentTimeMillis();
			iLogger.info("AMDE_DAL getAMDEData: for "+Serial_Number+" and SegmentID "+segmentid+" on "+txnTimestamp+" Query ends in "+(endTime-startTime));
			
			
			}catch(Exception ex){
				ex.printStackTrace();
				fLogger.fatal("AMDE_DAL getAMDEData: for "+Serial_Number+" and SegmentID "+segmentid+" on "+txnTimestamp+" Exception "+ex.getMessage());
				
			}
			finally{
				try {
					if(statement != null && !statement.isClosed()){
						statement.close();
						
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getAMDEData exception "+e.getMessage());
					e.printStackTrace();
				}
				try {
					if(connection!= null && !connection.isClosed()){
						connection.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					fLogger.fatal("getAMDEData exception "+e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		return responseList;
	}

}
