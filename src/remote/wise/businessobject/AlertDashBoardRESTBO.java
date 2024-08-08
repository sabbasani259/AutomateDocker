/**
 * CR308 : 20220613 : Dhiraj K : Code Fix for BW service closures from Portal
 */
package remote.wise.businessobject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.hibernate.SQLQuery;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.simple.JSONObject;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import remote.wise.businessentity.AccountEntity;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.UserAlertsRespContract;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.GmtLtTimeConversion;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;

/**
 * @author roopn5
 * 
 */
public class AlertDashBoardRESTBO {
	
	public List<UserAlertsRespContract> getUserAlerts(int loginTenancyId, int pageNumber, String serialNumber, String loginId) throws CustomFault{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		
		List<UserAlertsRespContract> userAlertsList = new ArrayList<UserAlertsRespContract>();
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;

		String basicQueryString=null;
		
		int pge;
		if((pageNumber%5)==0)
		{
			pge = pageNumber/5;
		}
		
		else if(pageNumber==1)
		{
			pge=1;
		}
					
		else
		{
			while( ((pageNumber)%5) != 0 )
			{
				pageNumber = pageNumber-1;
			}
			
			pge = ((pageNumber)/5)+1;
		}
		int startLimit = (pge-1)*50;
		
		int endLimit =50;

		Session session = HibernateUtil.getSessionFactory().openSession();

		try{
			
			if(serialNumber!=null)
			{
				if(serialNumber.trim().length()==7)
				{
					String machineNumber = serialNumber;
					
					AssetDetailsBO AssetDetailsBOObj=new AssetDetailsBO();
					serialNumber = AssetDetailsBOObj.getSerialNumberMachineNumber(machineNumber);
					if(serialNumber==null)
					{//invalid machine number
						iLogger.info("Machine number "+ machineNumber + "does not exist !!!");
						return userAlertsList ;
					}
				}
			}
			
			/*AccountEntity account=null;


			Query accountQ = session.createQuery(" select at.account_id from AccountTenancyMapping at where at.tenancy_id="+loginTenancyId+" ");
			Iterator accItr = accountQ.list().iterator();
			if(accItr.hasNext())
			{
				account = (AccountEntity)accItr.next();
			}

			if(session!=null && session.isOpen())
			{
				session.close();
			}*/
			
			//DF20171011 @KO369761 fixing the issue of same customer under different dealers by considering all accounts in query.
			String tenancyCode = null;
			List<Integer> accountCodeList = new LinkedList<Integer>();
			
			
			
		/*	Query tenancyQ = session.createQuery(" select t.tenancyCode from TenancyEntity t where t.tenancy_id="+loginTenancyId);
			Iterator tenancyItr = tenancyQ.list().iterator();
			if(tenancyItr.hasNext())
			{
				tenancyCode = (String)tenancyItr.next();
			}
			
			Query accountQ = session.createQuery("select b.account_id from AccountTenancyMapping a, AccountEntity b where a.account_id=b.account_id and a.tenancy_id in (select t.tenancy_id from TenancyEntity t where t.tenancyCode='"+tenancyCode+"')");
			Iterator accItr = accountQ.list().iterator();
			while(accItr.hasNext())
			{
				accountCodeList.add((Integer) accItr.next());
			}*/
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			
			Query accountQ = session.createQuery("select a.account_id from AccountTenancyMapping a, AccountEntity b where b.status=true and a.account_id=b.account_id " +
					"and b.mappingCode in (select c.mappingCode from AccountEntity c where c.status=true and c.account_id in (select account_id from AccountTenancyMapping where tenancy_id in ("+loginTenancyId+")))");
			
			Iterator accItr = accountQ.list().iterator();
			while(accItr.hasNext())
			{
				AccountEntity account = (AccountEntity)accItr.next();
				accountCodeList.add(account.getAccount_id());
			}

		/*	if(session!=null && session.isOpen())
			{
				session.close();
			}*/
			
			//DF20170130 @Roopa for Role based alert implementation
			DateUtil utilObj=new DateUtil();
			
			List<String> alertCodeList= utilObj.roleAlertMapDetails(null,loginTenancyId, "Display");
			
			//CR308.sn
			EventDetailsBO eventDetailsBoObj = new EventDetailsBO();
			//CR308.en
			
			ListToStringConversion conversion = new ListToStringConversion();

			StringBuilder alertCodeListAsString=conversion.getStringList(alertCodeList);
			String accountCodeListAsString = conversion.getIntegerListString(accountCodeList).toString();

			/*String SelectQuery="select ae.Serial_Number,ae.Event_Type_ID, et.Event_Type_Name, be.Event_Description, ae.Event_Generated_Time, ae.Event_Severity, ae.Asset_Event_ID " +
			" from business_event be, event_type et, asset_event ae " +
			" inner join asset_owner_snapshot aos ON aos.Serial_Number=ae.Serial_Number " +
			" and aos.account_ID="+account.getAccount_id()+" " +
			" where ae.Active_Status=1 and be.Event_ID=ae.Event_ID and et.Event_Type_ID= ae.Event_Type_ID";*/
			
			//DF20170803: SU334449 - Inclusion of timeZone with the query from Asset table
			/*String SelectQuery="select ae.Serial_Number,ae.Event_Type_ID, et.Event_Type_Name, be.Event_Description, ae.Event_Generated_Time, ae.Event_Severity, ae.Asset_Event_ID, ass.timeZone " +
					" from business_event be, event_type et, asset_event ae " +
					" inner join asset_owner_snapshot aos ON aos.Serial_Number=ae.Serial_Number " +
					" inner join asset ass ON ass.Serial_Number=ae.Serial_Number"+
					" and aos.account_ID in ("+accountCodeListAsString+") " +
					" where be.Alert_Code in ("+alertCodeListAsString+") "  +
					" and ae.Active_Status=1 and ae.PartitionKey =1 and be.Event_ID=ae.Event_ID and et.Event_Type_ID= ae.Event_Type_ID";*/
			
			//DF20190205 Abhishek:: change in method to improve performance
			/*String SelectQuery = "select ae1.*, et.Event_Type_Name, be.Event_Description, ass.timeZone from" +
					" ( select ae.Serial_Number, ae.Event_Type_ID, ae.Event_Generated_Time, ae.Event_Severity," +
					" ae.Asset_Event_ID, ae.Event_ID from asset_event ae" +
					" where ae.Active_Status = 1 and ae.PartitionKey = 1 order by ae.Event_Generated_Time desc  ) as ae1" +
					" inner join asset_owner_snapshot aos ON aos.Serial_Number = ae1.Serial_Number and aos.account_ID in ("+accountCodeListAsString+")" +
					" inner join asset ass ON ass.Serial_Number = ae1.Serial_Number" +
					" inner join event_type et ON et.Event_Type_ID = ae1.Event_Type_ID" +
					" inner join business_event be ON be.Event_ID = ae1.Event_ID and be.Alert_Code in ("+alertCodeListAsString+")";*/
			
			String limitQuery=" LIMIT "+startLimit+""+","+""+endLimit+"";

			String serialNumQ = "";
			if(serialNumber!=null){
				serialNumQ = " ae.Serial_Number='"+serialNumber+"' and";	
			}
			//DF20193112 Ramu B: Now user can see only assigned machine's alerts
			String selectQuery="";
			SQLQuery query = session.createSQLQuery("select * from group_user where Contact_ID=?");
			query.setString(0, loginId);
			List<Object[]> groupUser = query.list();
			
			if(groupUser.size()>0)
			{
				selectQuery="select ae1.Serial_Number, ae1.Event_Type_ID,et.Event_Type_Name, ae1.Event_Description," +
						" ae1.Event_Generated_Time, ae1.Event_Severity, ae1.Asset_Event_ID, ass.timeZone, ae1.DTC_code, ae1.Error_Code from" +
						" ( select ae.Serial_Number, ae.Event_Type_ID, ae.Event_Generated_Time, ae.Event_Severity," +
						" ae.Asset_Event_ID, ae.Event_ID, be.Event_Description, mp.DTC_code, mp.Error_Code from asset_event ae" +
						" inner join custom_asset_group_snapshot cags ON cags.asset_id = ae.Serial_Number and cags.user_Id = "+"'"+loginId+"'"+
						" inner join business_event be ON be.Event_ID = ae.Event_ID and be.Alert_Code in ("+alertCodeListAsString+")"+
						" inner join monitoring_parameters mp ON mp.Parameter_ID = be.Parameter_ID" +
						" where "+serialNumQ+" ae.Active_Status = 1 and ae.PartitionKey = 1 " +
						" order by ae.Event_Generated_Time desc "+limitQuery+" ) as ae1"+
						" inner join asset ass ON ass.Serial_Number = ae1.Serial_Number" +
						" inner join event_type et ON et.Event_Type_ID = ae1.Event_Type_ID"+
						" Union all "+
						"select ae1.Serial_Number, ae1.Event_Type_ID,et.Event_Type_Name, ae1.Event_Description," +
						" ae1.Event_Generated_Time, ae1.Event_Severity, ae1.Asset_Event_ID, ass.timeZone, 0 as DTC_code, 0 as Error_Code from" +
						" ( select ae.Serial_Number, ae.Event_Type_ID, ae.Event_Generated_Time, ae.Event_Severity," +
						" ae.Asset_Event_ID, ae.Event_ID, be.Event_Description from asset_event ae" +
						" inner join custom_asset_group_snapshot cags ON cags.asset_id = ae.Serial_Number and cags.user_Id = "+"'"+loginId+"'" + 
						" inner join business_event be ON be.Event_ID = ae.Event_ID and be.Parameter_ID=0 and be.Alert_Code in ("+alertCodeListAsString+")"+
						" where "+serialNumQ+" ae.Active_Status = 1 and ae.PartitionKey = 1 " +
						" order by ae.Event_Generated_Time desc "+limitQuery+" ) as ae1"+
						" inner join asset ass ON ass.Serial_Number = ae1.Serial_Number" +
						" inner join event_type et ON et.Event_Type_ID = ae1.Event_Type_ID";
				
			}else{
				//DF20190208 - Abhishek:: Query modified for better performance.
				//DF20190925 @Mamatha Appending DTC code and Error code to Alert description.
				//DF20191108 - Abhishek:: Modified query to show missing vins.
				selectQuery="select ae1.Serial_Number, ae1.Event_Type_ID,et.Event_Type_Name, ae1.Event_Description," +
						" ae1.Event_Generated_Time, ae1.Event_Severity, ae1.Asset_Event_ID, ass.timeZone, ae1.DTC_code, ae1.Error_Code from" +
						" ( select ae.Serial_Number, ae.Event_Type_ID, ae.Event_Generated_Time, ae.Event_Severity," +
						" ae.Asset_Event_ID, ae.Event_ID, be.Event_Description, mp.DTC_code, mp.Error_Code from asset_event ae" +
						" inner join asset_owner_snapshot aos ON aos.Serial_Number = ae.Serial_Number and aos.account_ID in ("+accountCodeListAsString+")" +
						" inner join business_event be ON be.Event_ID = ae.Event_ID and be.Alert_Code in ("+alertCodeListAsString+")"+
						" inner join monitoring_parameters mp ON mp.Parameter_ID = be.Parameter_ID" +
						" where "+serialNumQ+" ae.Active_Status = 1 and ae.PartitionKey = 1 " +
						" order by ae.Event_Generated_Time desc "+limitQuery+" ) as ae1"+
						" inner join asset ass ON ass.Serial_Number = ae1.Serial_Number" +
						" inner join event_type et ON et.Event_Type_ID = ae1.Event_Type_ID"+
						" Union all "+
						"select ae1.Serial_Number, ae1.Event_Type_ID,et.Event_Type_Name, ae1.Event_Description," +
						" ae1.Event_Generated_Time, ae1.Event_Severity, ae1.Asset_Event_ID, ass.timeZone, 0 as DTC_code, 0 as Error_Code from" +
						" ( select ae.Serial_Number, ae.Event_Type_ID, ae.Event_Generated_Time, ae.Event_Severity," +
						" ae.Asset_Event_ID, ae.Event_ID, be.Event_Description from asset_event ae" +
						" inner join asset_owner_snapshot aos ON aos.Serial_Number = ae.Serial_Number and aos.account_ID in ("+accountCodeListAsString+")" +
						" inner join business_event be ON be.Event_ID = ae.Event_ID and be.Parameter_ID=0 and be.Alert_Code in ("+alertCodeListAsString+")"+
						" where "+serialNumQ+" ae.Active_Status = 1 and ae.PartitionKey = 1 " +
						" order by ae.Event_Generated_Time desc "+limitQuery+" ) as ae1"+
						" inner join asset ass ON ass.Serial_Number = ae1.Serial_Number" +
						" inner join event_type et ON et.Event_Type_ID = ae1.Event_Type_ID";;
				}
			/*if(serialNumber!=null){
				SelectQuery =SelectQuery + " and ae.Serial_Number='"+serialNumber+"'";	
			}*/
			if(session!=null && session.isOpen())
			{
				session.close();
			}
			//String OrderByQuery = " order by ae.Event_Generated_Time desc ";
			String oderByQuery = " order by Event_Generated_Time desc";
			
			//String limitQuery=" LIMIT "+startLimit+""+","+""+endLimit+"";
			
			basicQueryString = selectQuery + oderByQuery;

			iLogger.info("AlertDashBoardBO :: basicQueryString ::"+basicQueryString);

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(basicQueryString);

			while(rs.next()){
				UserAlertsRespContract respObj = new UserAlertsRespContract();
				
				//DF20190925 @Mamatha Appending DTC code and Error code to Alert description.
				//respObj.setAlertDescription(rs.getString("Event_Description"));
				
				if(rs.getInt("DTC_code")!=0){
					if(rs.getString("Error_Code")!=null)
						respObj.setAlertDescription(rs.getInt("DTC_code")+"-"+rs.getString("Event_Description")+"-"+rs.getString("Error_Code"));
					else
						respObj.setAlertDescription(rs.getInt("DTC_code")+"-"+rs.getString("Event_Description"));

				}
				else
					respObj.setAlertDescription(rs.getString("Event_Description"));
				
				respObj.setAlertSeverity(rs.getString("Event_Severity"));
				respObj.setAlertTypeId(rs.getInt("Event_Type_ID"));
				respObj.setAlertTypeName(rs.getString("Event_Type_Name"));
				respObj.setAssetEventId(rs.getInt("Asset_Event_ID"));
				//DF20170803: SU334449 - Converting GMT to local time of respective SAARC countries
				String timeZone = rs.getString("timeZone");
				String convReceivedTime = new GmtLtTimeConversion().convertGmtToLocal(timeZone, String.valueOf(rs.getTimestamp("Event_Generated_Time")));
				respObj.setLatestReceivedTime(convReceivedTime);
				//respObj.setLatestReceivedTime(String.valueOf(rs.getTimestamp("Event_Generated_Time")));
				respObj.setSerialNumber(rs.getString("Serial_Number"));
				//CR308.sn
				respObj.setServiceName(eventDetailsBoObj.getEventsServiceName(rs.getInt("Asset_Event_ID")));
				//CR308.en
                userAlertsList.add(respObj);
			}
		

		}
		
		/**
		 * KO369761 - DF20181114 Capturing communication link failure
		 * exception and throwing as custom fault exception to UI.
		 ***/
		catch(CommunicationsException e){
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			e.printStackTrace();
			fLogger.fatal(stack.toString());

			throw new CustomFault("SQL Exception");
		}

		catch (SQLException e) {
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			e.printStackTrace();
			fLogger.fatal(stack.toString());
			
			/**
			 * KO369761 - DF20181114 Throwing sql exception as custom fault
			 * exception to UI.
			 ***/
			throw new CustomFault("SQL Exception");
		} 
		
		catch(Exception e)
		{
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			e.printStackTrace();
			fLogger.fatal("Exception :"+e.getMessage());
			fLogger.fatal(stack.toString());
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

		return userAlertsList;


	}

}
