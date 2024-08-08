package remote.wise.service.implementation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.LocationDetails;
import remote.wise.service.datacontract.LocationDetailsMMI;
import remote.wise.util.GetSetLocationJedis;

public class ReverseGeoCodingMMIImpl implements Runnable{
	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;
	

	private ResultSet rs;
	private PreparedStatement updateStmt;
	private int startRow;
	private int endRow;
	private AtomicInteger updatedCount;
	
	public ReverseGeoCodingMMIImpl(ResultSet rs,PreparedStatement updateStmt,int startRow,int endRow,AtomicInteger updatedCount) throws SQLException {
		this.rs = rs;
		this.updateStmt = updateStmt;
		this.startRow = startRow;
		this.endRow = endRow;
		this.updatedCount = updatedCount;
	}
	
	@Override
	public void run() {
		try {
			for(int i= startRow;i<=endRow && rs.next();i++) {
				String serialNumber = rs.getString("serial_number");
				String lat = rs.getString("lat");
				String lon = rs.getString("lon");
				
				LocationDetailsMMI locObj = GetSetLocationJedis.getLocationDetailsMMI(lat,lon);
				//update comm_report_mmi set Comm_Village_MMI=?,Comm_City_MMI=?,Comm_District_MMI=?,Comm_State_MMI=?,Country_MMI=?,Comm_Address_MMI=? where serial_number = ?;
				updateStmt.setString(1, locObj.getVillage());
				updateStmt.setString(2, locObj.getCity());
				updateStmt.setString(3, locObj.getDistrict());
				updateStmt.setString(4, locObj.getState());
				updateStmt.setString(5, locObj.getCountry());
				updateStmt.setString(6, locObj.getAddress());
				updateStmt.setString(7, serialNumber);
				
				
				updateStmt.addBatch();				
			}
			int[] updatedRows = updateStmt.executeBatch();
			
			//updatedCount.addAndGet(Arrays.stream(updatedRows).sum());
		}catch(Exception e) {
			e.printStackTrace();
			fLogger.fatal("Error occured while updating comm_report_mmi table: "+e.getMessage());
		}
	}
	
	
}
