/**
 * 
 */
package remote.wise.dal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AssetEventLogImpl;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.GmtLtTimeConversion;
import remote.wise.util.StaticProperties;

/**
 * @author roopn5
 *
 */
public class DynamicAssetEvent_DAL {

	public List<AssetEventLogImpl> getActiveAlertsListtForEventMap(String query, String alertState, String timeZone) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;


		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		GmtLtTimeConversion convertedTime = new GmtLtTimeConversion();
		String convertedTimestamp=null;
		//String AMHAMDSelectQuery=query;

		List<AssetEventLogImpl> assetEventLogImplList =new ArrayList<AssetEventLogImpl>();



		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal(":AssetEvent:DAL-getActiveAlertsListtForEventMap"+"Error in intializing property File :"+e.getMessage());
		}



		String PersistToInMemory = prop.getProperty("PersistTo_InMemory");

		if(Boolean.parseBoolean(PersistToInMemory)){
			/*<!=========START persisting in In memory==============>*/
			iLogger.info(":AssetEvent:DAL-getActiveAlertsListtForEventMap"+"Persist to In Memory storage");
			/*<!=========END persisting in In memory==============>*/
		}
		else{

			/*<!=========START fetching from Native Database==============>*/


			try{

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(query);

				while(rs.next()){

					AssetEventLogImpl implObj = new AssetEventLogImpl();

					//implObj.setEventGeneratedTime(String.valueOf(rs.getTimestamp("Event_Generated_Time")));
					//DF20170809: @SU334449 - Updating Specific TimeZone changes corresponding to VIN.
					if(String.valueOf(rs.getTimestamp("Event_Generated_Time"))!=null)
						convertedTimestamp = convertedTime.convertGmtToLocal(timeZone, String.valueOf(rs.getTimestamp("Event_Generated_Time")));
					implObj.setEventGeneratedTime(convertedTimestamp);

					//DF20170814 @Roopa appending error code corresponding to dtc code

					if(rs.getInt("DTC_code")!=0){
						if(rs.getString("Error_Code")!=null)
							implObj.setParamName(rs.getInt("DTC_code")+"-"+rs.getString("Event_Name")+"-"+rs.getString("Error_Code"));
						else
							implObj.setParamName(rs.getInt("DTC_code")+"-"+rs.getString("Event_Name"));

					}
					else
						implObj.setParamName(rs.getString("Event_Name"));
					
					if(alertState.equalsIgnoreCase("open"))
						implObj.setParameterValue("1");
					else
						implObj.setParameterValue(String.valueOf(rs.getInt("Active_Status")));
					
					implObj.setAlertSeverity(rs.getString("Event_Severity"));
					//implObj.setDtcCode(rs.getInt("DTC_code"));
					if(alertState.equalsIgnoreCase("close") && rs.getString("UpdateSource").equalsIgnoreCase("reset"))
					{
						iLogger.info("UpdatedSource:"+rs.getString("UpdateSource"));
						String temp=implObj.getParamName();

					    // Trim  ".0" from the timestamp
					    String cleanedTimestamp = convertedTimestamp;
					    if (convertedTimestamp != null && convertedTimestamp.endsWith(".0")) {
					        cleanedTimestamp = convertedTimestamp.substring(0, convertedTimestamp.length() - 2);
					    }

						implObj.setParamName(temp+"- refreshed on "+cleanedTimestamp);
					}
					
					//Df20180103 @Roopa taking the lat and long from the assetevent for application generated alerts purpose
					//DF20190111-KO369761-Null check condition added.
					if(rs.getString("Location") != null){
					implObj.setLatitude(rs.getString("Location").split(",")[0]);
					implObj.setLongitude(rs.getString("Location").split(",")[1]);
					}

					assetEventLogImplList.add(implObj);

				}
			}
			catch (SQLException e) {

				e.printStackTrace();
				fLogger.fatal(":AssetEvent:DAL-getActiveAlertsListtForEventMap"+"SQL Exception in fetching data from mysql::"+e.getMessage());
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal(":AssetEvent:DAL-getActiveAlertsListtForEventMap"+"Exception in fetching data from mysql::"+e.getMessage());
			}

			finally {
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

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
		}

		return assetEventLogImplList;
	}	

}
