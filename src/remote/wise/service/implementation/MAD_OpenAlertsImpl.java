/**
 * 
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;

/**
 * @author ROOPN5
 *
 */
public class MAD_OpenAlertsImpl {

	public List<HashMap<String, Object>> getMADOpenAlertDetails(String loginID, List tenancyIdList,int pageNumber, int pageSize){



		HashMap<String, Object> respObj = null;
		List<HashMap<String, Object>> responseList=new ArrayList<HashMap<String,Object>>();

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String accountIdListAsString=null;


		String selectQuery=null;
		String fromQuery=null;
		String whereQuery=null;
		String orderByQuery=null;
		String limitQuery=null;
		String finalQuery=null;

		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
		try{
			if(tenancyIdList!=null){

				accountIdListAsString=new DateUtil().getAccountListForTheTenancy(tenancyIdList);
			}

			int startLimit=0;
			int endLimit=0;

			startLimit=pageNumber*pageSize;
			endLimit=pageSize;

			selectQuery= "select ae.Serial_Number,be.Event_Name,ae.Event_Generated_Time ";

			fromQuery= " from asset a,asset_owner_snapshot aos,asset_event ae,business_event be ";
			//DF20190204 @abhishek---->add partition key for faster retrieval
			whereQuery=	" where aos.Account_ID in ("+accountIdListAsString+") and a.Serial_Number=aos.Serial_Number and ae.Serial_Number=aos.Serial_Number and ae.Event_ID=be.Event_ID and active_status=1 and ae.Event_Type_ID!=1 and ae.PartitionKey =1 ";

			orderByQuery= " order by ae.Event_Generated_Time";
			
			

			limitQuery=" limit "+startLimit+","+endLimit+"";

			if(startLimit!=0 || endLimit!=0)
			finalQuery=selectQuery+fromQuery+whereQuery+orderByQuery+limitQuery;
			else
				finalQuery=selectQuery+fromQuery+whereQuery+orderByQuery; //Detailed view of the alerts 

			iLogger.info("MADashBoardDetailsRESTService:getMADOpenAlertDetails: Final Query::"+finalQuery);

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(finalQuery);



			while(rs.next()){
				respObj = new HashMap<String, Object>();

				respObj.put("Serial_Number", rs.getString("Serial_Number"));
				respObj.put("Event_Name", rs.getString("Event_Name"));
				respObj.put("Event_Generated_Time", rs.getString("Event_Generated_Time"));

				responseList.add(respObj);
			}

		}

		catch(Exception e){
			fLogger.fatal("MADashBoardDetailsRESTService:getMADOpenAlertDetails:: Exception Caught:"+e.getMessage());
		}
		finally{
			try { 
				if (rs != null)
					rs.close(); 
			} catch (Exception e) {
				fLogger.fatal("MADashBoardDetailsRESTService:getMADOpenAlertDetails:: Exception Caught:"+e.getMessage());
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("MADashBoardDetailsRESTService:getMADOpenAlertDetails:: Exception Caught:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("MADashBoardDetailsRESTService:getMADOpenAlertDetails:: Exception Caught:"+e.getMessage());
				}
			}
		}

		return responseList;


	}

}
