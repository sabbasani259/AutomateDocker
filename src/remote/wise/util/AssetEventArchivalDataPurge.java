package remote.wise.util;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;

public class AssetEventArchivalDataPurge {
	String time_key;
	public AssetEventArchivalDataPurge(){
		this.time_key = time_key;
		//Thread.currentThread().setName(this.time_key);
	}
	
	
	public String archiveAssetEventData() {
		String result = "SUCCESS";
		Connection prodConnection = null;
		Statement statement = null;
		PreparedStatement pst= null;
		ResultSet rs = null;
		ConnectMySQL connMySql = new ConnectMySQL();
		prodConnection = connMySql.getConnection();
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("AssetEventArchivalDataPurge: archiveAssetEventData START");
		try {
			prodConnection.setAutoCommit(false);
			statement = prodConnection.createStatement();
		
		String archivalQuery = "select Serial_Number , Event_Generated_Time ,Event_ID , Event_Type_ID  " +
				"from asset_event where active_status = 0 and Serial_Number is not null and DATEDIFF(Date(NOW()),DATE(Event_Generated_Time)) > 15";
		rs = statement.executeQuery(archivalQuery);
		pst = prodConnection.prepareStatement("update asset_event set active_status = 2 where Serial_Number = ? and Event_Generated_Time = ? and Event_ID = ? and Event_Type_ID = ?");
		int row =1;
		while(rs.next()){
			pst.setString(1, rs.getString("Serial_Number"));
			//pst.setDouble(2, new BigDecimal(rs.getDouble("ResolutionTimeInHrs")).setScale(4, BigDecimal.ROUND_CEILING).doubleValue());
		
			//pst.setDate(4, new java.sql.Date(sdf.parse(timeKey).getTime()));
			
			pst.setTimestamp(2, rs.getTimestamp("Event_Generated_Time"));
			pst.setInt(3,rs.getInt("Event_ID"));
			pst.setInt(4, rs.getInt("Event_Type_ID"));	
			pst.addBatch();
			if(row%1000 == 0)
			{
				//System.out.println(timeKey+"roww:"+row);
				long iteratorEnd = System.currentTimeMillis();
				
				try{
				//iLogger.info("Alert Detail Migration "+timeKey+"before executingbatch insertions for 1000 records");
				long preparedStmtStart = System.currentTimeMillis();
				pst.executeBatch();
				
				if(prodConnection.isClosed()){
					prodConnection = new ConnectMySQL().getConnection();
					}
				prodConnection.commit();
				pst.clearBatch();
				
				}catch(BatchUpdateException bue){
					//iLogger.info("Alert Detail Migration  inner BatchUpdateexception");
					bue.printStackTrace();
					int[] updatedCount = bue.getUpdateCounts();
					
				}
			}
				
			row++;
		}
		
		
		int[] updatedRows = pst.executeBatch();
		iLogger.info("AssetEventArchivalDataPurge: number if records archived are "+updatedRows.length);
		if(prodConnection.isClosed()){
			prodConnection = new ConnectMySQL().getConnection();
			}
		prodConnection.commit();

		pst.clearBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
			result = "FAIILURE";
			e.printStackTrace();
		}
	finally{
				if(pst!=null){
					try {
						pst.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(prodConnection != null){
					try {
						prodConnection.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
				
			}

		
		return result;
	}
}
