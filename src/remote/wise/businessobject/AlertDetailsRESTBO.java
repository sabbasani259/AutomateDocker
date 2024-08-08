/**
 * 
 */
package remote.wise.businessobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.simple.JSONObject;

import remote.wise.businessentity.AccountEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.AssetUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;

/**
 * @author roopn5
 * DF20160727 @Roopa New REST service which returns the combined response for Alert summary service, Notification summary service and Service due overdue summary
 *
 */
public class AlertDetailsRESTBO {
	
	

	public String getAlertDetails(List<Integer> loginTenancyIdList, List<Integer> customGroupIdList){
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		String alertJSONArray=null;

		HashMap<String, HashMap<String,String>> finalAlertMap = new HashMap<String, HashMap<String, String>>();

		HashMap<String,String> alertSummaryMap =new HashMap<String, String>();
		HashMap<String,String> notificationSummaryMap =new HashMap<String, String>();
		HashMap<String,String> dueOverdueSummaryMap =new HashMap<String, String>();
		//ramu b adding extendedwarrantyDueOverdueSummaryMap
		//aj20119610 changes done as a part of group based view
		HashMap<String,String> extendedwarrantyDueOverdueSummaryMap =new HashMap<String, String>();

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;

		String basicQueryString=null;
		
		String alertsDetails=null;
		
		ListToStringConversion conversion = new ListToStringConversion();

		Session session = HibernateUtil.getSessionFactory().openSession();
		String userAccListAsString=null;
		String groupIdList=null;

		try{
			if(customGroupIdList!=null ||(customGroupIdList!=null && !(customGroupIdList.isEmpty())))
			{
					groupIdList=AssetUtil.getFormatedVinsToQuery(customGroupIdList);
					
					basicQueryString="select ae.Event_type_ID, ae.Event_Severity, count(*) as alertCount from asset_event ae " +
							"inner join custom_asset_group_snapshot cags ON cags.Asset_Id=ae.Serial_Number  and ae.Event_type_ID!=1 " +
							"and cags.Group_ID in("+groupIdList+")  where ae.Event_ID  NOT IN ('16','17','839','1185','6428','6876','7310','10452','13535','13670') and ae.Active_Status=1 and ae.PartitionKey=1 group by ae.Event_type_ID, ae.Event_Severity"
					 +" union all"
					 +" select aes.Event_type_ID, aes.Event_Severity, count(*) as alertCount from " +
					 		"(select * from asset_event ae , service_schedule ss   where ae.Event_ID  NOT IN ('16','17','839','1185','6428','6876','7310','10452','13535','13670') and ae.service_schedule_id =ss.servicescheduleid " +
					 		"and ss.service_type ='Free' and ae.PartitionKey=1) aes inner join custom_asset_group_snapshot cags ON cags.Asset_Id=aes.Serial_Number  " +
					 		"and cags.Group_ID in("+groupIdList+")  where aes.Active_Status=1 and aes.Event_type_ID=1 " +
					 	"group by aes.Event_type_ID, aes.Event_Severity";
			}
			//AccountEntity account=null;
			else{
			List<Integer> userAccList = new LinkedList<Integer>();

		//	Query accountQ = session.createQuery(" select at.account_id from AccountTenancyMapping at where at.tenancy_id="+loginTenancyId+" ");
			
			String tenancies=AssetUtil.getFormatedVinsToQuery(loginTenancyIdList);
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			Query accountQ=session.createQuery("select a.account_id from AccountTenancyMapping a, AccountEntity b where b.status=true and a.account_id=b.account_id " +
					"and b.mappingCode in (select c.mappingCode from AccountEntity c where c.status=true and c.account_id in(select account_id from AccountTenancyMapping where tenancy_id in ("+tenancies+")))");
				
			Iterator accItr = accountQ.list().iterator();
		/*	if(accItr.hasNext())
			{
				account = (AccountEntity)accItr.next();
			}*/
			
			while(accItr.hasNext())
			{
				AccountEntity account = (AccountEntity)accItr.next();
				
				userAccList.add(account.getAccount_id());
			}

			if(session!=null && session.isOpen())
			{
				session.close();
			}
			
			userAccListAsString = conversion.getIntegerListString(userAccList).toString();

			/*basicQueryString="select ae.Event_type_ID, ae.Event_Severity, count(*) as alertCount" + 
					" from asset_event ae inner join asset_owner_snapshot aos ON aos.Serial_Number=ae.Serial_Number " +
					" and aos.account_ID="+account.getAccount_id()+" " +
					" where ae.Active_Status=1 group by ae.Event_type_ID, ae.Event_Severity";*/
			
			//DF20190204 @abhishek---->add partition key for faster retrieval
			/*basicQueryString="select ae.Event_type_ID, ae.Event_Severity, count(*) as alertCount" + 
					" from asset_event ae inner join asset_owner_snapshot aos ON aos.Serial_Number=ae.Serial_Number " +
					" and aos.account_ID in("+userAccListAsString+") " +
					" where ae.Active_Status=1 and ae.PartitionKey =1 group by ae.Event_type_ID, ae.Event_Severity";*/
			//DF20190711:Abhishek--->Changed query to union service alert for service service only.
//			basicQueryString="select ae.Event_type_ID, ae.Event_Severity, count(*) as alertCount from asset_event ae " +
//									"inner join asset_owner_snapshot aos ON aos.Serial_Number=ae.Serial_Number  and ae.Event_type_ID!=1 " +
//									"and aos.account_ID in("+userAccListAsString+")  where ae.Active_Status=1 and ae.PartitionKey=1 group by ae.Event_type_ID, ae.Event_Severity"
//							 +" union all"
//							 +" select aes.Event_type_ID, aes.Event_Severity, count(*) as alertCount from " +
//							 		"(select * from asset_event ae , service_schedule ss   where ae.service_schedule_id =ss.servicescheduleid " +
//							 		"and ss.service_type ='Free' and ae.PartitionKey=1) aes inner join asset_owner_snapshot aos ON aos.Serial_Number=aes.Serial_Number  " +
//							 		"and aos.account_ID in("+userAccListAsString+")  where aes.Active_Status=1 and aes.Event_type_ID=1 " +
//							 	"group by aes.Event_type_ID, aes.Event_Severity";
			//DF20210409 Avinash Xavier A over view chart Query optimization 
			basicQueryString=" select ae.Event_type_ID, ae.Event_Severity, count(*) as alertCount from asset_event as ae " +
					" inner join asset_owner_snapshot aos ON aos.Serial_Number = ae.Serial_Number and aos.account_ID in ("+userAccListAsString+ ") " +
							" left outer join service_schedule ss ON ss.servicescheduleid = ae.service_schedule_id where " +
							" ae.Event_ID  NOT IN ('16','17','839','1185','6428','6876','7310','10452','13535','13670') and " +
							" ae.Active_Status = 1 and ae.PartitionKey = 1 and (ss.service_type = 'Free' or ss.service_type is null) " +
							" group by ae.Event_type_ID, ae.Event_Severity ";
			
			iLogger.info("AlertDetailsRestBo::basicQueryString ::"+basicQueryString);
			// s.n : 100000924: 20220405:Shajesh:  Live link Portal - XP machine group -charts are not loading on the portal
			}
			// e.n : 100000924: 20220405:Shajesh:  Live link Portal - XP machine group -charts are not loading on the portal
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(basicQueryString);

			int redCount=0;

			int yellowCount=0;

			int dueCount=0;

			int overDueCount =0;

			int serviceCount = 0;

			int healthCount = 0;

			int landmarkCount = 0;

			int securityCount = 0;

			int utiliZationCount = 0;

			int extendedWarrantydueCount=0;

			int extendedWarrantyOverDueCount =0;


			while(rs.next()){

				String severity=rs.getString("Event_Severity");

				int count = rs.getInt("alertCount");

				int eventTypeID=rs.getInt("Event_type_ID");

				//total alert red count

				if(severity.equalsIgnoreCase("RED")){

					redCount = redCount + count;
				}

				//total alert yellow count

				if(severity.equalsIgnoreCase("YELLOW")){

					yellowCount = yellowCount + count;
				}

				//total service overdue count

				if(eventTypeID==1 && severity.equalsIgnoreCase("RED")){

					overDueCount = overDueCount + count;
				}

				//total service due count

				if(eventTypeID==1 && severity.equalsIgnoreCase("YELLOW")){

					dueCount = dueCount + count;
				}
				//Total Service count
				if(eventTypeID==1){
					serviceCount= serviceCount + count;
				}

				//Total Health count

				if(eventTypeID==2){
					healthCount= healthCount + count;
				}

				//Total Utilization count

				if(eventTypeID==3){
					utiliZationCount= utiliZationCount + count;
				}

				//Total Security count

				if(eventTypeID==4){
					securityCount= securityCount + count;
				}

				//Total Landmark count

				if(eventTypeID==5){
					landmarkCount= landmarkCount + count;
				}
			}
			//Filling Alert summary details
			alertSummaryMap.put("Red_Count", String.valueOf(redCount));
			alertSummaryMap.put("Yellow_Count", String.valueOf(yellowCount));

			//Filling Notification summary details
			notificationSummaryMap.put("Service", String.valueOf(serviceCount));
			notificationSummaryMap.put("Health", String.valueOf(healthCount));
			notificationSummaryMap.put("Utilization", String.valueOf(utiliZationCount));
			notificationSummaryMap.put("Security", String.valueOf(securityCount));
			notificationSummaryMap.put("Landmark", String.valueOf(landmarkCount));

			//Filling Service due and overdue details

			dueOverdueSummaryMap.put("dueCount", String.valueOf(dueCount));
			dueOverdueSummaryMap.put("overDueCount", String.valueOf(overDueCount));
			
			//Ramu B added on 20200512 extended warranty due and over due count.
			
			String extendedWarrantyQuery="select  aes.Event_Severity, count(*) as alertCount from " +
			 		"(select * from asset_event ae , service_schedule ss   where ae.service_schedule_id =ss.servicescheduleid "+" and ss.service_type="+"'"+"Extended Warranty"+"'"+
			 		"  and ae.PartitionKey=1) aes inner join asset_owner_snapshot aos ON aos.Serial_Number=aes.Serial_Number  " +
			 		"and aos.account_ID in("+userAccListAsString+")  where aes.Active_Status=1 and aes.Event_type_ID=1 " +
			 	"group by  aes.Event_Severity";
				
				iLogger.info("extendedWarrantyQuery ::: "+extendedWarrantyQuery);
			
				
				rs = statement.executeQuery(extendedWarrantyQuery);
				while(rs.next())
				{
					String severity=rs.getString("Event_Severity");

					int count = rs.getInt("alertCount");
					

					if(severity.equalsIgnoreCase("RED")){
						extendedWarrantyOverDueCount = count;
						iLogger.info("AlertDetailsRestBo::RED count :: "+extendedWarrantyOverDueCount);
					}

					
					if(severity.equalsIgnoreCase("YELLOW")){
						extendedWarrantydueCount = count;
						iLogger.info("AlertDetailsRestBo::YELLOW count :: "+extendedWarrantydueCount);
					}
				}
				extendedwarrantyDueOverdueSummaryMap.put("ExtendedWarrantydueCount", String.valueOf(extendedWarrantydueCount));
				
				extendedwarrantyDueOverdueSummaryMap.put("ExtendedWarrantyOverDueCount", String.valueOf(extendedWarrantyOverDueCount));
				

			//Filling final map

			finalAlertMap.put("AlerSummary", alertSummaryMap);
			finalAlertMap.put("NotificationSummary", notificationSummaryMap);
			finalAlertMap.put("ServiceDueOverdueSummary", dueOverdueSummaryMap);
			//Ramu B added on 20200512 extended warranty due and over due count.
			finalAlertMap.put("ExtendedwarrantyDueOverdueSummary", extendedwarrantyDueOverdueSummaryMap);

			alertJSONArray = new JSONObject(finalAlertMap).toString();
			iLogger.info("alertJSONArray...."+alertJSONArray);
			//System.out.println("alertJSONArray::"+alertJSONArray);
			// 100000924: 20220405:Shajesh:  Live link Portal - XP machine group -charts are not loading on the portal so commented out the closing parentheses and its open for only else block rather than the entire block
		//}
			}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception :"+e.getMessage(),e.getMessage());
		}

		finally
		{
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

			if(session!=null && session.isOpen())
			{
				session.close();
			}

		}

		return alertJSONArray;


	}

}
