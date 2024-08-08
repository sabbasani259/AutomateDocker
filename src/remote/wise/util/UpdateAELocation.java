package remote.wise.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import redis.clients.jedis.Jedis;
import remote.wise.businessobject.EventDetailsBO;
import remote.wise.businessobject.ServiceDetailsBO;
import remote.wise.service.datacontract.LocationDetails;

public class UpdateAELocation {
	private static final String HOST = "localhost";
	private static final int PORT = 6379;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/*EventDetailsBO eventDetailsBO = new EventDetailsBO();
		eventDetailsBO.migrateAELocation();*/
		new EventDetailsBO().updateAddess(); 
		
		/*
		PreparedStatement pst= null;
		Connection db2Connection = null;
		Jedis redisPool = new Jedis(HOST,PORT);
		//Connection prodConnection = null;
		Statement statement = null; 
		ResultSet rs = null;
		long startTimeQuery = System.currentTimeMillis();
		GeocodingLibrary geoCoding = new GeocodingLibrary();
		
	try{
		
		db2Connection = new ConnectMySQL().getProdDb2Connection();
		ConnectMySQL connMySql = new ConnectMySQL();
		//prodConnection = connMySql.getConnection();
		String updateStatement = "update factInsight_dayAgg set Address = ?,State=?,City =? where AssetID = ? and TxnDate = ?";
		pst = db2Connection.prepareStatement(updateStatement);
		statement = db2Connection.createStatement();
		//locationQuery.setMaxResults(1000);
		//Iterator locationIterator = locationQuery.list().iterator();
		String addressQuery = "select AssetID,TxnDate,Latitude,Longitude from factInsight_dayAgg where (Address is null || State is null || City is null )";
		rs = statement.executeQuery(addressQuery);
				int counterNoRowsUpdated = 0;
			while (rs.next()) {
				counterNoRowsUpdated++;
				
				System.out.println("counterNoRowsUpdated:"+counterNoRowsUpdated);
							
				String location[];
				String latitude = rs.getString("Latitude");
				String longitude = rs.getString("Longitude");
				String Serial_Number = rs.getString("AssetID");
				//Date TxnDate = rs.getDate("TxnDate");
				
				LocationDetails locObj = GetSetLocationJedis.getLocationDetails(latitude,longitude, redisPool);
				
				//System.out.println("returned address"+locObj+" for "+latitude+" "+longitude);
				// exception scenario by osm/google by shrini 201605041040
				pst.setString(1, locObj.getAddress());
				pst.setString(3, locObj.getCity());
				pst.setString(2, locObj.getState());
				
				pst.setString(4, Serial_Number);
				pst.setDate(5, rs.getDate("TxnDate"));
				pst.addBatch();
				
				if(counterNoRowsUpdated > 200){
					counterNoRowsUpdated = 1;
					pst.executeBatch();
				}
			}
			pst.executeBatch();
	}catch(Exception e){
		e.printStackTrace();
	}
	finally{
		try {
		if(db2Connection!=null && db2Connection.isClosed()){
			
				db2Connection.close();
			
		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
		
	}

}
