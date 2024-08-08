/**
 * 
 */
package remote.wise.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

/**
 * @author ROOPN5
 *
 */
public class MOOLDARejectedRecordsBO {

	public void persistPacketDetails(String txnKey, String assetId, String txnTimestamp, String messageDetailsJSON ,String rejectionCause, String rejectionPoint)
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Connection prodConnection = null;
		Statement statement = null;
		int insertCount=0;

		String insertQuery="Insert into MOOLDA_rejectedEvents(Serial_Number,Transaction_Timestamp,TxnData,rejectionCause,rejectionPoint) values" +
				"('"+assetId+"','"+txnTimestamp+"','"+messageDetailsJSON+"','"+rejectionCause+"','"+rejectionPoint+"')";

		iLogger.info(txnKey+":MOOLDA-setRejectedRecords"+"Insert Query::"+insertQuery);

		try{

			//String result=obj.setMOODARejectedPkts(insertQuery, txnKey);
			
			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();

				 insertCount= statement.executeUpdate(insertQuery);

				iLogger.info(txnKey+":AMS:DAL-AMS-setAMSData"+"AMS insert count::"+insertCount);	

			}

			catch (SQLException e) {

				e.printStackTrace();
				fLogger.fatal(txnKey+":AMS:DAL-AMS-setAMSData"+"SQL Exception in inserting recors to table::asset_monitoring_snapshot::"+e.getMessage());
				
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal(txnKey+":AMS:DAL-AMS-setAMSData"+"Exception in inserting recors to table::asset_monitoring_snapshot::"+e.getMessage());
				
			}

			finally {
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

			iLogger.info(txnKey+":MOOLDA-setRejectedRecords"+"Insert count::"+insertCount);
		}
		catch(Exception e){

			e.printStackTrace();
			fLogger.fatal(txnKey+":MOOLDA-setRejectedRecords"+"Exception in inserting records to table::MOOLDA_rejectedRecords::"+e.getMessage());

		}

	}

}
