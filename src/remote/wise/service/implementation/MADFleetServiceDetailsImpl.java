/**
 * 
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
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
public class MADFleetServiceDetailsImpl {

	public List<HashMap<String, Object>> getFleetServiceDetails(String loginID, List tenancyIdList, String detailedView){



		HashMap<String, Object> respObj = null;
		List<HashMap<String, Object>> responseList=new ArrayList<HashMap<String,Object>>();
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		String accountIdListAsString=null;


		String selectQuery=null;
		String leftQuery=null;
		String rightQuery=null;

		String finalQuery=null;

		DecimalFormat decformat=new DecimalFormat("0.0");

		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
		try{
			if(tenancyIdList!=null){

				accountIdListAsString=new DateUtil().getAccountListForTheTenancy(tenancyIdList);
			}



			/*selectQuery= "select aa.eventCount,aa.event_id,bb.completed from ";

			leftQuery="(select aos.account_id,count(ae.event_id) as eventCount,ae.event_id " +
					"from asset_event ae, business_event be,asset_owner_snapshot aos " +
					"where aos.serial_number=ae.serial_number and aos.account_id in("+accountIdListAsString+") and " +
					"ae.event_id in (1,2,3) and ae.active_status=1 and ae.event_id=be.event_id group by ae.event_id)";

			rightQuery="(select aos.account_id,count(*) as completed " +
					"from service_history sh,asset_owner_snapshot aos " +
					"where sh.serialnumber=aos.serial_number and aos.account_id in("+accountIdListAsString+"))";

			finalQuery=selectQuery+" "+leftQuery+" aa "+"join "+rightQuery+" bb "+" ON aa.account_id=bb.account_id";*/

			if(detailedView==null || detailedView.equalsIgnoreCase("false")){
				//DF20190204 @abhishek---->add partition key for faster retrieval
				finalQuery="select count(ae.event_id) as eventCount,ae.event_id " +
						"from asset_event ae, business_event be,asset_owner_snapshot aos " +
						"where aos.serial_number=ae.serial_number and aos.account_id in("+accountIdListAsString+") and " +
						"ae.event_id in (1,2,3) and ae.active_status=1 and ae.PartitionKey =1 and ae.event_id=be.event_id group by ae.event_id";
			}
			else{
				//Detailed view as for the service due report DF20180329

				finalQuery="select aa.Serial_Number,aa.Service_Schedule_Name as Schedule_Name,aa.Total_Engine_Hours,aa.Last_Service_Name,"+
						"aa.Last_Service_Date,aa.Last_Service_Hours,aa.Next_Service_Name,aa.Next_Service_Date,"+
						"(case when aa.Next_Service_Name = '1st Service' then aa.Next_Service_Hours else round((aa.Next_Service_Hours - aa.Total_Engine_Hours)) END)as HoursToNxtService," +
						"aa.DealerName,bb.Address"+
						" from "+
						"(SELECT a.account_name as DealerName,sdr.* FROM wise.service_details_report sdr,asset_owner_snapshot aos,account a"+
						" WHERE a.account_id in("+accountIdListAsString+") and aos.account_id = a.account_id and sdr.Serial_Number = aos.Serial_Number) aa"+
						" LEFT OUTER JOIN"+
						"(SELECT a.* FROM(select ae.Serial_Number,ae.Address,Event_Generated_time FROM asset_event ae,asset_owner_snapshot aos,account a"+
						" WHERE a.account_id in("+accountIdListAsString+") and aos.account_id = a.account_id and ae.Serial_Number = aos.Serial_Number and ae.event_id =3"+
						" order by Event_Generated_Time desc) a"+
						" GROUP BY Serial_Number) bb"+
						" on bb.Serial_Number = aa.Serial_Number";

			}

			iLogger.info("MADashBoardDetailsRESTService:getMADFleetServiceDetails: Final Query::"+finalQuery);

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(finalQuery);

			double approachCount=0;
			double overdueCount=0;
			/*double completedCount=0;

			double ApproachingPerct=0.0;
			double OverduePerct=0.0;
			double CompletedPerct=0.0;*/


			if(detailedView==null || detailedView.equalsIgnoreCase("false")){
			while(rs.next()){


				if(rs.getInt("event_id")==1 || rs.getInt("event_id")==2){
					approachCount=approachCount+rs.getInt("eventCount");
				}

				if(rs.getInt("event_id")==3){
					overdueCount=rs.getInt("eventCount");
				}

				//completedCount=rs.getInt("completed");


			}

			/* ApproachingPerct=approachCount/(approachCount+overdueCount+completedCount)*100;
			 OverduePerct=overdueCount/(approachCount+overdueCount+completedCount)*100;
			 CompletedPerct=completedCount/(approachCount+overdueCount+completedCount)*100;

			respObj.put("ApproachingPerct", decformat.format(ApproachingPerct));
			respObj.put("OverduePerct", decformat.format(OverduePerct));
			respObj.put("CompletedPerct", decformat.format(CompletedPerct));*/
			respObj = new HashMap<String, Object>();

			respObj.put("Approaching", approachCount);
			respObj.put("Overdue", overdueCount);
			responseList.add(respObj);
			}
			else{
				while(rs.next()){

					respObj = new HashMap<String, Object>();
					
					respObj.put("Serial_Number", rs.getString("Serial_Number"));
					respObj.put("Schedule_Name", rs.getString("Schedule_Name"));
					respObj.put("Total_Engine_Hours", rs.getString("Total_Engine_Hours"));
					respObj.put("Last_Service_Name", rs.getString("Last_Service_Name"));
					respObj.put("Last_Service_Date", rs.getString("Last_Service_Date"));
					respObj.put("Last_Service_Hours", rs.getString("Last_Service_Hours"));
					respObj.put("Next_Service_Name", rs.getString("Next_Service_Name"));
					respObj.put("Next_Service_Date", rs.getString("Next_Service_Date"));
					
					respObj.put("HoursToNxtService", rs.getString("HoursToNxtService"));
					respObj.put("DealerName", rs.getString("DealerName"));
					respObj.put("Address", rs.getString("Address"));
					
					responseList.add(respObj);	

				}
			}




		}

		catch(Exception e){
			fLogger.fatal("MADashBoardDetailsRESTService:getMADFleetServiceDetails:: Exception Caught:"+e.getMessage());
		}
		finally{
			try { 
				if (rs != null)
					rs.close(); 
			} catch (Exception e) {
				fLogger.fatal("MADashBoardDetailsRESTService:getMADFleetServiceDetails:: Exception Caught:"+e.getMessage());
			}

			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					fLogger.fatal("MADashBoardDetailsRESTService:getMADFleetServiceDetails:: Exception Caught:"+e.getMessage());
				}
			}
			if(prodConnection != null){
				try {
					prodConnection.close();
				} catch (SQLException e) {
					fLogger.fatal("MADashBoardDetailsRESTService:getMADFleetServiceDetails:: Exception Caught:"+e.getMessage());
				}
			}
		}

		return responseList;

	}

}
