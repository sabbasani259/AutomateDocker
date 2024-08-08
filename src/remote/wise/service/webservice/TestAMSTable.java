package remote.wise.service.webservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

//prasad for testing AMS table
@Path("/TestAMSTable")
public class TestAMSTable {
	

	@GET
	@Path("/updateAMSTable")
	@Produces(MediaType.TEXT_PLAIN)
	public String testAMSTable(@QueryParam("count") int count) {
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connectPool = new ConnectMySQL();
		int totalloopCount =0;
		try (Connection connection = connectPool.getConnection();
				Statement stmt = connection.createStatement();
				Statement updateStmt = connection.createStatement();) {
			
			for(int j =1 ; j<=count ; j++){
				
			List<String> serialNumberList = new ArrayList<>();
			int cunt = 0;
			String selectQuery = "select Serial_Number from asset_monitoring_snapshot_test";
			ResultSet rs = stmt.executeQuery(selectQuery);
			while (rs.next()) {
				serialNumberList.add(rs.getString("Serial_Number"));
			}
			for (int i = 0; i < serialNumberList.size(); i++) {
				
				String updateQuerry = "update asset_monitoring_snapshot_test set"
						+ " TxnData = JSON_SET(TxnData,'$.CMH','" + totalloopCount +"') where Serial_Number = '" + serialNumberList.get(i) + "'";
			//	iLogger.info("updateQuerry " + updateQuerry);
				int val = updateStmt.executeUpdate(updateQuerry);
				if (val > 0) {
					cunt = cunt + 1;
				}

			}
			iLogger.info("Iteration "+ totalloopCount+" :: Total no of records update :"+ count);
			totalloopCount++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		
		}
		return "No of time total table update " + totalloopCount;

	}

	@GET
	@Path("InsertAMS")
	@Produces(MediaType.TEXT_PLAIN)
	public String insertAMS(@QueryParam("count") int count) {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		ConnectMySQL connectPool = new ConnectMySQL();
		int totalloopCount =0;
		Connection connection = connectPool.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement psInsert = null;
		int cunt = 0;
		try  {
			
		
				
			String txnData = null;
			
			String selectQuery = "select TxnData  from asset_monitoring_snapshot_test where Serial_Number = 'BLRDANONIMIZEDVI2'";
			stmt= connection.createStatement();
			rs = stmt.executeQuery(selectQuery);
			while (rs.next()) {
				txnData=	rs.getString("TxnData");
			}
			 for(int j =1 ; j<=count ; j++){
				 
				 
//					String updateQueryInServiceHistory = "insert into service_history  (serviceTicketNumber, serialNumber  , dealerId  ,"
//							+ "serviceDate , serviceName  ,scheduleName  ,DBMS_partCode ,serviceScheduleId  , CMH ,call_type_id ) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

				 String insertQ ="insert into asset_monitoring_snapshot_test (Serial_Number,Transaction_Timestamp_Log,Transaction_Timestamp_Evt,Transaction_Timestamp_Fuel,"
				 		+ "Transaction_Timestamp_PT,Latest_Created_Timestamp,TxnData,fault_status_TS)  VALUES(?, ?, ?, ?, ?, ?, ?, ?) ";
				

				
				
				iLogger.info("updateQuerry " + insertQ);
				 psInsert = connection.prepareStatement(insertQ);
				String s = String.valueOf(j);
				Timestamp currentTime = new Timestamp(new Date().getTime());
				String currentTimeInString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime);
				 psInsert.setString(1,s );
				 psInsert.setString(2, currentTimeInString);
				 psInsert.setString(3, currentTimeInString);
				 psInsert.setString(4, currentTimeInString);
				 psInsert.setString(5, currentTimeInString);
				 psInsert.setString(6, currentTimeInString);
				 psInsert.setString(7, txnData);
				 psInsert.setString(8, currentTimeInString);
				
				 
				 int executeUpdate = psInsert.executeUpdate();
				
			
				if (executeUpdate > 0) {
					cunt = cunt + 1;
				}

			}
			//iLogger.info("Iteration "+ totalloopCount+" :: Total no of records update :"+ count);
			totalloopCount++;
			
		} catch (SQLException e) {
			fLogger.fatal(e.getMessage());
			e.printStackTrace();
		
		}
		catch(Exception e)
		{
			fLogger.fatal(e.getMessage());
			e.printStackTrace();
		}
		
		finally
		{
			try
			{
				if(stmt!=null)
					stmt.close();
				if(psInsert!=null)
					psInsert.close();
				
				if(connection!=null)
					connection.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return "No of time total table update " + cunt;

	}
}
