/**
 * 
 */
package remote.wise.businessobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.logging.log4j.Logger;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

/**
 * @author roopn5
 *
 */
public class OassetPersonalityBO {

	//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
	/*public String setOrientAssetPersonalityDetails(String OSerialNumber, String OAssetGroupCode, String OassetTypeCode, String profileName, String modelName){

		Logger fLogger = FatalLoggerClass.logger;

		Logger iLogger = InfoLoggerClass.logger;

		OrientAppDbDatasource connObj = new OrientAppDbDatasource();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs=null;
		int updateCount = 0;


		String response="SUCCESS";


		try{
			iLogger.info("AssetPersonalityDetails"+"setAssetPersonalityDetails to OrientDB- START");
			conn = connObj.getConnection();
			iLogger.info("AssetPersonalityDetails:"+"setAssetPersonalityDetails to OrientDB- Got Connection");



			stmt = conn.createStatement();
			String selectquery = "select @RID from TAssetSnapshot where AssetID='"+OSerialNumber+"'";

			iLogger.info("AssetSnapshot:setAssetPersonalityDetails:"+"Select query :"+selectquery);
			rs = stmt.executeQuery(selectquery);

			int rsCount=rs.getFetchSize();

			if(rsCount==0){
				fLogger.error("Orient AssetSnapshot:setAssetPersonalityDetails:"+"Serial Number :"+OSerialNumber+"is not found in TAssetSnapshot table");
				return "FAILURE";	
			}
			else{


				String query = "UPDATE TAssetSnapshot SET Profile='"+OAssetGroupCode+"',Model='"+OassetTypeCode+"',profileName='"+profileName+"',modelName='"+modelName+"' where AssetID='"+OSerialNumber+"'";
				updateCount=stmt.executeUpdate(query);	
				iLogger.info("AssetPersonalityDetails updateCount :"+updateCount);

				iLogger.info("TAssetSnapshot AssetPersonalityDetails update Count :"+updateCount);
			}

		}
		catch(Exception e)
		{
			//e.printStackTrace();

			//Error in getting OrientDB session
			fLogger.error("AssetSnapshotDetails:"+"Exception in getting OrientDB Connection:"+e.getMessage());
			response="Failure";
		}

		finally
		{
			iLogger.info("OrientDB-AssetSnapshotDetails:Close Connection - START");
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
				iLogger.info("Problem in closing the connection");
				//e.printStackTrace();
				fLogger.error("AssetSnapshotDetails:"+"Exception in closing OrientDB Connection:"+e.getMessage());
			}
			iLogger.info("OrientDB-AssetSnapshotDetails:Close Connection - END");

		}
		if(updateCount==0){
			response="Failure";
		}


		return response;

	}*/




}
