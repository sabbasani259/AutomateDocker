package remote.wise.service.webservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.Logger;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ReverseGeoCodingMMIImpl;
import remote.wise.util.ConnectMySQL;

@Path("/ReverseGeoCodingMMI")
public class ReverseGeoCodingMMI {
	
	private static final int BATCH_SIZE = 1000;
	private static final int NUM_THREAD = 5;
	
	@GET
	@Path("/updateCommReportMMI")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateCommReportMMI(@DefaultValue("-1") @QueryParam("limit") int limit,@QueryParam("executeAll") Boolean executeAll) throws Exception {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String response = "success";
		
		ConnectMySQL connectionObj = new ConnectMySQL();
		Connection conn = null;
		PreparedStatement selectStmt = null;
		PreparedStatement updateStmt = null;
		String selectSQL = "select * from comm_report_mmi where comm_address_mmi is NULL";
		if(executeAll !=null && executeAll) {
			selectSQL = "select * from comm_report_mmi";
		}
		
		if(limit != -1) {
			selectSQL = selectSQL+" limit "+limit;
		}
		String updateSQL = "update comm_report_mmi set Comm_Village_MMI=?,Comm_City_MMI=?,Comm_District_MMI=?,Comm_State_MMI=?,Country_MMI=?,Comm_Address_MMI=? where serial_number = ?";
		
		try {
			conn = connectionObj.getConnection();
			selectStmt = conn.prepareStatement(selectSQL);
			updateStmt = conn.prepareStatement(updateSQL);
			
			ResultSet rs = selectStmt.executeQuery();
			rs.last();
			int rowCount = rs.getRow();
			rs.beforeFirst();
			infoLogger.info("updateCommReportMMI total records in comm_report_mmi table: "+rowCount);
			int numBatches = (int) Math.ceil((double) rowCount/BATCH_SIZE);
			
			ExecutorService executor = Executors.newFixedThreadPool(NUM_THREAD);
			AtomicInteger updatedCount = new AtomicInteger(0);
			long startTime = System.currentTimeMillis();
			
			for(int i=0;i<numBatches;i++) {
				int startRow = i*BATCH_SIZE +1;
				int endRow = Math.min(startRow+BATCH_SIZE-1,rowCount);				
				executor.execute(new ReverseGeoCodingMMIImpl(rs,updateStmt,startRow,endRow,updatedCount));
			}
			
			executor.shutdown();
			while(!executor.isTerminated()) {
				Thread.sleep(1000);
			}
			//int count = updatedCount.get();
			long endTime = System.currentTimeMillis();
			//infoLogger.info("updateCommReportMMI total Updated rows: "+count);
			infoLogger.info("updateCommReportMMI total time taken to update table: "+(endTime - startTime)+" milliseconds");
			
			
		}catch(Exception e) {
			e.printStackTrace();
			response = "failure";
			fLogger.fatal("Error occured in updateCommReportMMI method: "+e.getMessage());
		}
		finally {
			try {
				if(selectStmt != null) {
					selectStmt.close();
				}
				if(updateStmt != null) {
					updateStmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return response;

	}


}
