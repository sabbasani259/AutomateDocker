/**
 * 
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ApplicationAlertClosureExtended;
import remote.wise.util.ConnectMySQL;

/**
 * @author ROOPN5
 *
 */
public class ApplicationAlertClosureImpl {
	private static List<Integer> dtcEventIds = getDtcEventIds();
	private static List<Integer> normalEventIds = getNormalEventIds();
	private static  HashMap<String,String> vinAlertStatusMap=getAlertStatusFromAMS();
	private static String dtcPreparedQuery= createDtcQuery();
	private static String normalEventPreparedQuery= createNormalEventQuery();
	

	public String closeAlerts(){

		String result="SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;

		int maxNoOfThreads = 0;

		try{
		

			iLogger.info("ApplicationAlertClosureRESTService: STEP 1 Getting Max(segmentId)");

			//STEP 1 : getting max segmentID from asset table ,NO. of Threads = No. of Segments 
			try {
				connection = connFactory.getConnection();
				statement = connection.createStatement();
				rs = statement.executeQuery("select max(Segment_ID) as Segment from asset");
				while(rs.next()){
					maxNoOfThreads = rs.getInt("Segment");
				}
			}catch (SQLException e) {
				e.printStackTrace();
			}
			finally{
				
				try {
					if(rs != null && !rs.isClosed()){
						rs.close();

					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					if(statement != null && !statement.isClosed()){
						statement.close();

					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					if(connection!= null && !connection.isClosed()){
						connection.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			iLogger.info("ApplicationAlertClosureRESTService: Max(segmentId)::"+maxNoOfThreads);

			int segment=0;

			if(maxNoOfThreads!=0)
			{
				while(segment <= maxNoOfThreads ){
					//Call the segmentId thread.

					try{

						iLogger.info("ApplicationAlertClosureRESTService: Invoking Segmnet::"+segment);
						//STEP 2:: Calling AlertClosure Thread for Each segment.

						new ApplicationAlertClosureExtended(segment,dtcEventIds,normalEventIds,vinAlertStatusMap,dtcPreparedQuery,normalEventPreparedQuery);
						
					}

					catch(Exception e){
						e.printStackTrace();
						fLogger.fatal("ApplicationAlertClosureRESTService::Exception in invoking Segmnet:"+segment+":"+e.getMessage());
					}
					segment++;

				}

			}
		}
		catch(Exception e){
			e.printStackTrace();
			return "FAILURE";
		}

		return result;
	}

	//Getting all the Health EventId's and assigning it to static variable(Instead of calling for every VIN), which will be used further
	private static List<Integer> getNormalEventIds() {
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		int event_id = 0;
		List<Integer> normalEvent_list = new LinkedList<Integer>();
		try {
			connection = connFactory.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery("select be.Event_ID from monitoring_parameters m, business_event be where m.DTC_code=0 and m.Parameter_ID=be.Parameter_ID and be.Event_Type_ID=2");
			while(rs.next()){
				event_id = rs.getInt("Event_ID");
				normalEvent_list.add(event_id);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			
			try {
				if(rs != null && !rs.isClosed()){
					rs.close();

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(statement != null && !statement.isClosed()){
					statement.close();

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(connection!= null && !connection.isClosed()){
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return normalEvent_list;
	}
	//Getting all the DTC EventId's and assigning it to static variable, which will be used further
	private static List<Integer> getDtcEventIds() {
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		int event_id = 0;
		List<Integer> dtc_list = new LinkedList<Integer>();
		try {
			connection = connFactory.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery("select be.Event_ID from monitoring_parameters m, business_event be where m.DTC_code!=0 and m.Parameter_ID=be.Parameter_ID");
			while(rs.next()){
				event_id = rs.getInt("Event_ID");
				dtc_list.add(event_id);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			
			try {
				if(rs != null && !rs.isClosed()){
					rs.close();

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(statement != null && !statement.isClosed()){
					statement.close();

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(connection!= null && !connection.isClosed()){
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dtc_list;
	}
	
	//Creating VIN map , for which Alerts status has been received from the FirmWare and assigning it to static map. which will be used further
	private static HashMap<String,String> getAlertStatusFromAMS(){
		
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		HashMap<String,String> vinAlertStatusMap=new HashMap<String, String>();
	
		try {
			connection = connFactory.getConnection();
			statement = connection.createStatement();
			//Alert status is being sent in the name of Faults_Status key name
			rs = statement.executeQuery("select Serial_Number,Transaction_Timestamp_Log,CAST(TRIM(BOTH '\"' FROM  TxnData ->'$.Faults_Status') AS CHAR) as alertStatus  from asset_monitoring_snapshot where TxnData ->'$.Faults_Status' is not null");
			
			while(rs.next()){
				vinAlertStatusMap.put(rs.getString("Serial_Number"), String.valueOf(rs.getTimestamp("Transaction_Timestamp_Log"))+","+rs.getString("alertStatus"));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			
			try {
				if(rs != null && !rs.isClosed()){
					rs.close();

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(statement != null && !statement.isClosed()){
					statement.close();

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(connection!= null && !connection.isClosed()){
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return vinAlertStatusMap;
	}
	
	//Creating Prepared statement for DTC query
	
	private static String createDtcQuery() {
		String query = "update asset_event set Active_Status=?,Event_Closed_Time=?,created_timestamp=?"+
				" where Serial_Number = ?" +
				" and Active_Status = ? " +
				" and Event_ID in (";
		StringBuilder queryBuilder = new StringBuilder(query);
		for( int i = 0; i<dtcEventIds.size(); i++){
			queryBuilder.append(" ?");
			if(i != dtcEventIds.size() -1) queryBuilder.append(",");
		}
		queryBuilder.append(")");
		return queryBuilder.toString();
	}
	//Creating Prepared statement for Health events query
	private  static String createNormalEventQuery() {
		String query = "update asset_event set Active_Status=?,Event_Closed_Time=?,created_timestamp=?"+
				" where Serial_Number = ?" +
				" and Active_Status = ? " +
				" and Event_ID in (";
		StringBuilder queryBuilder = new StringBuilder(query);
		for( int i = 0; i<normalEventIds.size(); i++){
			queryBuilder.append(" ?");
			if(i != normalEventIds.size() -1) queryBuilder.append(",");
		}
		queryBuilder.append(")");
		return queryBuilder.toString();
	}

}
