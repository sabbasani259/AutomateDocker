/**
 * 
 */
package remote.wise.businessobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

/**
 * @author roopn5
 *
 */
public class OAssetSnapshotBO {

	//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
	/*public String setOrientAssetSnapshotDetails(String OserialNumber, HashMap<String,String> OAssetOwnerSnapshotMap, String OCurrentOwner){

		Logger fLogger = FatalLoggerClass.logger;

		Logger iLogger = InfoLoggerClass.logger;

		iLogger.info("setOrientAssetSnapshotDetails input :"+"Serial number :"+OserialNumber+","+"AssetOwnerSnapshotMap :"+OAssetOwnerSnapshotMap+","+"CurrentOwner :"+OCurrentOwner);

		OrientAppDbDatasource connObj = new OrientAppDbDatasource();
		Connection conn = null;
		Statement stmt = null;

		ResultSet rs=null;
		int updateCount = 0;
		int TAssetSnapshotCount = 0;

		String response="SUCCESS";

		String assetOwnerQuery=null;

		String assetQuery=null;


		try{
			iLogger.info(OserialNumber+"AssetSnapshotDetails"+"setAssetSnapshotDetails to OrientDB- START");
			
			try{
			conn = connObj.getConnection();
			stmt = conn.createStatement();
			iLogger.info(OserialNumber+"AssetSnapshotDetails:"+"setAssetSnapshotDetails to OrientDB- Got Connection");
			}
			catch(Exception e)
			{
				
				fLogger.error(OserialNumber+"AssetSnapshotDetails:"+"Exception in getting OrientDB Connection:"+e.getMessage());
				response="FAILURE";
			}
			

			JSONObject json = new JSONObject(OAssetOwnerSnapshotMap);
			String finalJson = json.toJSONString();
			
			 iLogger.info(OserialNumber+"finalJson"+finalJson);
			
			String finalJson1=null;

			Iterator it = OAssetOwnerSnapshotMap.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pair = (Map.Entry)it.next();
			       // iLogger.info(pair.getKey() + " = " + pair.getValue());
			        
			        if(finalJson1!=null)
			        finalJson1=finalJson1+"'"+pair.getKey()+"'"+":"+"'"+pair.getValue()+"'"+",";
			        else
			        	 finalJson1="'"+pair.getKey()+"'"+":"+"'"+pair.getValue()+"'"+",";	
			    }
			    
			    finalJson1=finalJson1.substring(0, finalJson1.length()-1);
			    
			   // iLogger.info("finalJson1"+finalJson1);
			    
			    String ConvertedfinalJson="{"+finalJson1+"}";
			    
			   // iLogger.info("ConvertedfinalJson"+ConvertedfinalJson);

			
			assetOwnerQuery = "UPDATE TAssetOwnerSnapshot SET AssetID='"+OserialNumber+"', OwnershipDetails="+ConvertedfinalJson+", CurrentOwner='"+OCurrentOwner+"' UPSERT where AssetID='"+OserialNumber+"'";

			iLogger.info(OserialNumber+"TAssetSnapshotOwner Query"+assetOwnerQuery);

			assetQuery = "UPDATE TAssetSnapshot SET AssetID='"+OserialNumber+"',CurrentOwner='"+OCurrentOwner+"' UPSERT where AssetID='"+OserialNumber+"'";

			iLogger.info(OserialNumber+"TAssetSnapshot Query"+assetQuery);

			updateCount=stmt.executeUpdate(assetOwnerQuery);	



			TAssetSnapshotCount=stmt.executeUpdate(assetQuery);	
			
			iLogger.info(OserialNumber+"AssetSnapshotDetails:"+"setTAssetOwnerSnapshot to OrientDB updateCount :"+updateCount);

			iLogger.info(OserialNumber+"AssetSnapshotDetails:"+"setTAssetSnapshot to OrientDB updateCount :"+TAssetSnapshotCount);
			
			
			

		}
		catch(Exception e)
		{
			//e.printStackTrace();
			//Error in getting OrientDB session
			fLogger.error(OserialNumber+"AssetSnapshotDetails:"+"Exception in inserting record to TAssetOwnerSnapshot and TAssetSnapshot:"+e);
			response="FAILURE";
		}

		finally
		{
			iLogger.info(OserialNumber+"OrientDB-AssetSnapshotDetails:Close Connection - START");
			try
			{
                if(rs!=null)
					rs.close();

				if(stmt!=null)
					stmt.close();

				if(conn!=null)
					conn.close();

			}
			catch(Exception e)
			{
			
				fLogger.error(OserialNumber+"AssetSnapshotDetails:"+"Exception in closing OrientDB Connection:"+e.getMessage());
			}
			iLogger.info(OserialNumber+"OrientDB-AssetSnapshotDetails:Close Connection - END");

		}
		//creating link from TassetSnapshot table to TAssetOwnerSnapshot table
		
		try{
			
                   iLogger.info(OserialNumber+"AssetSnapshotDetails"+"setAssetSnapshotDetails to OrientDB- START");
			
			try{
			conn = connObj.getConnection();
			stmt = conn.createStatement();
			iLogger.info(OserialNumber+"AssetSnapshotDetails:"+"setAssetSnapshotDetails to OrientDB- Got Connection");
			}
			catch(Exception e)
			{
				
				fLogger.error(OserialNumber+"AssetSnapshotDetails:"+"Exception in getting OrientDB Connection:"+e.getMessage());
				response="FAILURE";
			}
       //update TAssetSnapshot set AssetOwnerDetails =#11:0 where @rid=#12:0
			
			String selectQuery = "select @RID from TAssetOwnerSnapshot where AssetID='"+OserialNumber+"'";
			
			iLogger.info("selectQuery::"+selectQuery);
			
			rs = stmt.executeQuery(selectQuery);
			
			//String columnName="rid";
			
			String Rid=null;
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int columns = rsmd.getColumnCount();
			for (int x = 1; x <= columns; x++) {
				if (columnName.equals(rsmd.getColumnName(x))) {
					Rid= (String) rs.getObject(1);	
				}
			}
			
			while(rs.next()){
				
				Object entry = rs.getObject(1);

				Rid= entry.toString();
				
				Rid=Rid.split("#")[1];
				
				Rid=Rid.split("\\{")[0];
				Rid="#"+Rid;
			}
			String updateQuery=null;
			int linkCount=0;
			iLogger.info(OserialNumber+"AssetSnapshotDetails Rid:"+Rid);
			if(Rid!=null){
				updateQuery="update TAssetSnapshot set AssetOwnerDetails="+Rid+" where AssetID='"+OserialNumber+"'";	
				
			}
			if(updateQuery!=null)
				linkCount=stmt.executeUpdate(updateQuery);
			
			iLogger.info(OserialNumber+"TAssetSnapshot LINK update Count for the AssetID::"+OserialNumber+"::"+linkCount);	
			
		}
		
		catch(Exception e)
		{
			//e.printStackTrace();
			//Error in getting OrientDB session
			fLogger.error(OserialNumber+"AssetSnapshotDetails:"+"Exception in inserting record to TAssetOwnerSnapshot and TAssetSnapshot:"+e);
			response="FAILURE";
		}

		finally
		{
			iLogger.info(OserialNumber+"OrientDB-AssetSnapshotDetails:Close Connection - START");
			try
			{
                if(rs!=null)
					rs.close();

				if(stmt!=null)
					stmt.close();

				if(conn!=null)
					conn.close();

			}
			catch(Exception e)
			{
			
				fLogger.error(OserialNumber+"AssetSnapshotDetails:"+"Exception in closing OrientDB Connection:"+e.getMessage());
			}
			iLogger.info(OserialNumber+"OrientDB-AssetSnapshotDetails:Close Connection - END");

		}
	
		return response;

	}*/

}
