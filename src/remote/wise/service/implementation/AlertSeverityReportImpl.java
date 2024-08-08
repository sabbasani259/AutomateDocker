/**
 * CR317:Add VIN city district state in Alert Severity Report
 * CR328:Display the DTC Error code on the Alert Severity Report
 */
package remote.wise.service.implementation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import com.opencsv.CSVWriter;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessobject.EventDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.AlertSeverityReportReqContract;
import remote.wise.service.datacontract.AlertSeverityReportRespContract;
import remote.wise.util.AssetUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.IstGmtTimeConversion;
import remote.wise.util.ListToStringConversion;


//CR316 : Prafull  : 2022-05-10 : Block Notifications for Tamper Alert _ EFD 


/**
 * @author sunayak
 *
 */
public class AlertSeverityReportImpl {
  

	/**
	 * Implemention get's the summary on the alerts
	 * @param request,Passed to get response as alert summary
	 * @return response as summary on alerts
	 * @throws CustomFault
	 * 
	 */
	String serialNumber;
	String dealerName;
	String customerName;
	String alertDescription;
	String latestReceivedTime;
	String alertSeverity;
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * @return the dealerName
	 */
	public String getDealerName() {
		return dealerName;
	}

	/**
	 * @param dealerName the dealerName to set
	 */
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * @param customerName the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * @return the alertDescription
	 */
	public String getAlertDescription() {
		return alertDescription;
	}

	/**
	 * @param alertDescription the alertDescription to set
	 */
	public void setAlertDescription(String alertDescription) {
		this.alertDescription = alertDescription;
	}

	/**
	 * @return the latestReceivedTime
	 */
	public String getLatestReceivedTime() {
		return latestReceivedTime;
	}

	/**
	 * @param latestReceivedTime the latestReceivedTime to set
	 */
	public void setLatestReceivedTime(String latestReceivedTime) {
		this.latestReceivedTime = latestReceivedTime;
	}

	/**
	 * @return the alertSeverity
	 */
	public String getAlertSeverity() {
		return alertSeverity;
	}

	/**
	 * @param alertSeverity the alertSeverity to set
	 */
	public void setAlertSeverity(String alertSeverity) {
		this.alertSeverity = alertSeverity;
	}

	//DF20160919 @Roopa AlertSeverityReport performance improvement
	
	public List<AlertSeverityReportRespContract> getAlertSeverityReportDetailsNew(AlertSeverityReportReqContract request) throws CustomFault{		
		
		List<AlertSeverityReportRespContract> responseList = new LinkedList<AlertSeverityReportRespContract>();
		

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		iLogger.info("Inside getAlertSeverityReportDetailsWithoutFilters::"+request.getLoginTenancyIdList().get(0));

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		Connection prodConnection1 = null;
		Statement statement1 = null;
		ResultSet rs1= null;
		int roleid=0;
		String basicQueryString=null;
		
		String whereQuery=null;
		String groupByQuery=null;
		String orderbyQuery=null;
		
		String finalQuery=null;
		
		ListToStringConversion conversionObj = new ListToStringConversion();

		Session session = HibernateUtil.getSessionFactory().openSession();

		try{
			//AccountEntity account=null;
			
			//String tenancyIdString=null;
			String accountIdString=null;
			iLogger.info(request.getLoginId());
			if(request.getLoginId()!=null)
			{
				ConnectMySQL connMySql1 = new ConnectMySQL();
				prodConnection1 = connMySql1.getConnection();
				statement1 = prodConnection1.createStatement();
				String roleQuery="select Role_ID from contact where Contact_ID=\'"+request.getLoginId()+"\'";
				iLogger.info("AlertSevertiy roleQuery "+roleQuery);
				rs1 = statement1.executeQuery(roleQuery);
				if(rs1.next())
				{
					roleid=rs1.getInt(1);
					iLogger.info(roleid);
				}
			}
			iLogger.info("AlertSevertiy role "+roleid);
			List<Integer> selectedTenancy = new LinkedList<Integer>();
			
			//List<Integer> accountIDList = new LinkedList<Integer>();
			if(!(request.getTenancyIdList()==null || request.getTenancyIdList().isEmpty())){
				selectedTenancy.addAll(request.getTenancyIdList());
				
				
			}
			else
			{
				selectedTenancy.addAll(request.getLoginTenancyIdList());
			}
			/*tenancyIdString = conversionObj.getIntegerListString(selectedTenancy).toString();

			Query accountQ = session.createQuery(" select at.account_id from AccountTenancyMapping at where at.tenancy_id in ("+tenancyIdString+") ");
			Iterator accItr = accountQ.list().iterator();
			while(accItr.hasNext())
			{
				account = (AccountEntity)accItr.next();
				accountIDList.add(account.getAccount_id());
			}
			
			accountIdString = conversionObj.getIntegerListString(accountIDList).toString();

			if(session!=null && session.isOpen())
			{
				session.close();
			}*/
			
//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			
		
			
			 accountIdString=new DateUtil().getAccountListForTheTenancy(selectedTenancy);
			 //DF20180709 @MANI : Alert severity report for dealers query change for dealer and dealer admin because of tenancy name mismatch
			 iLogger.info("is own stock : "+request.isOwnStock());
			 String customAssetGroupIdListAsString=AssetUtil.getIDListAsCommaSeperated(request.getCustomAssetGroupIdList());
			 List<Integer> customAssetGroupIdList = request.getCustomAssetGroupIdList();
			 
			 
			 if(customAssetGroupIdList==null ||(customAssetGroupIdList!=null && customAssetGroupIdList.isEmpty()))
			 {
			 if(roleid==5||roleid==6)
			 {
				 if(request.isOwnStock()==true){
					//DF20190204 @abhishek---->add partition key for faster retrieval
					//@shajesh =>  : 20201211 : Alert Severity Report customer Mobile number is overlapped by dealer mobile number
					//@shajesh =>  : 20211109 : Alert Severity Report ::remove low fuel event description from the report
					//CR317-added three columns Comm_City,Comm_District,Comm_State 
					//CR328-added one column Error_Code 
						//basicQueryString = "select ae.Serial_Number, be.Event_Description, ae.Event_Severity, convert_tz(ae.Event_Generated_Time,'+00:00','+05:30') as Event_Generated_Time,aa.account_name as tenancy_name,ab.account_name as parent_tenancy_name, croe.Zone, croe.Profile, croe.Model, croe.tmh, croe.Pkt_Recd_TS, croe.Roll_Off_Date, croe.Installed_date, aa.mobile, croe.Comm_City, croe.Comm_District, croe.Comm_State, mp.Error_Code from asset_event ae, asset_owner_snapshot aos, asset a, business_event be, account aa, account ab, com_rep_oem_enhanced croe, monitoring_parameters mp";//ME100011766.o
					 basicQueryString = "select ae.Serial_Number, be.Event_Description, ae.Event_Severity, convert_tz(ae.Event_Generated_Time,'+00:00','+05:30') as Event_Generated_Time,croe.CustomerName AS tenancy_name, croe.Dealer_Name AS parent_tenancy_name, croe.Zone, croe.Profile, croe.Model, croe.tmh, croe.Pkt_Recd_TS, croe.Roll_Off_Date, croe.Installed_date, aa.mobile, croe.Comm_City, croe.Comm_District, croe.Comm_State, mp.Error_Code from asset_event ae, asset_owner_snapshot aos, asset a, business_event be, account aa, account ab, com_rep_oem_enhanced croe, monitoring_parameters mp";//ME100011766.n

					 whereQuery = " where a.primary_owner_id in ("
								+ accountIdString
								+ ") and aos.Serial_Number=ae.Serial_Number and aos.Serial_Number=croe.serial_Number and be.parameter_id = mp.parameter_id and aos.account_ID in ("
								+ accountIdString
								+ ") and ae.Active_Status=1  AND ae.PartitionKey =1 and a.serial_number=ae.serial_Number and croe.serial_Number=ae.serial_Number and a.primary_owner_id=aa.account_id and be.Event_ID=ae.Event_ID and ab.Account_ID in ("
								+ accountIdString + ") and be.Event_Description != 'Fuel Level is low' and be.Event_Description != 'Low Fuel Level' ";
						groupByQuery = " group by ae.serial_number, ae.Event_ID";
						orderbyQuery = " order by ae.event_Severity asc,ae.event_Generated_Time desc";
				 }
				 else
				 {
					//DF20190204 @abhishek---->add partition key for faster retrieval
					//@shajesh =>  : 20201211 : Alert Severity Report customer Mobile number is overlapped by dealer mobile number
					//@shajesh =>  : 20211109 : Alert Severity Report ::remove low fuel event description from the report
					//CR317-added three columns Comm_City,Comm_District,Comm_State 
					//CR328-added one column Error_Code 
					//basicQueryString = "select ae.Serial_Number, be.Event_Description, ae.Event_Severity, convert_tz(ae.Event_Generated_Time,'+00:00','+05:30') as Event_Generated_Time,aa.account_name as tenancy_name,ab.account_name as parent_tenancy_name,croe.Zone,croe.Profile,croe.Model,croe.tmh,croe.Pkt_Recd_TS,croe.Roll_Off_Date,croe.Installed_date,aa.mobile,croe.Comm_City, croe.Comm_District, croe.Comm_State, mp.Error_Code from asset_event ae, asset_owner_snapshot aos, asset a, business_event be, account aa, account ab, com_rep_oem_enhanced croe, monitoring_parameters mp";//ME100011766.o
					 basicQueryString = "select ae.Serial_Number, be.Event_Description, ae.Event_Severity, convert_tz(ae.Event_Generated_Time,'+00:00','+05:30') as Event_Generated_Time,croe.CustomerName AS tenancy_name, croe.Dealer_Name AS parent_tenancy_name,croe.Zone,croe.Profile,croe.Model,croe.tmh,croe.Pkt_Recd_TS,croe.Roll_Off_Date,croe.Installed_date,aa.mobile,croe.Comm_City, croe.Comm_District, croe.Comm_State, mp.Error_Code from asset_event ae, asset_owner_snapshot aos, asset a, business_event be, account aa, account ab, com_rep_oem_enhanced croe, monitoring_parameters mp";//ME100011766.n
						
					 whereQuery = " where aos.Serial_Number=ae.Serial_Number and aos.Serial_Number=croe.serial_Number and be.parameter_id = mp.parameter_id and aos.account_ID in ("
								+ accountIdString
								+ ") and ae.Active_Status=1  AND ae.PartitionKey =1 and a.serial_number=ae.serial_Number and a.serial_number=croe.serial_Number and a.primary_owner_id=aa.account_id and be.Event_ID=ae.Event_ID and ab.Account_ID in ("
								+ accountIdString + ") and be.Event_Description != 'Fuel Level is low' and be.Event_Description != 'Low Fuel Level' ";
						groupByQuery = " group by ae.serial_number, ae.Event_ID";
						orderbyQuery = " order by ae.event_Severity asc,ae.event_Generated_Time desc";
				 }
				 
			 }
			 else{
			if(request.isOwnStock()==true){
				//DF20190328 @abhishek---->change query to remove dependency from tenancy table.
				//@shajesh =>  : 20201211 : Alert Severity Report customer Mobile number is overlapped by dealer mobile number
				//@shajesh =>  : 20211109 : Alert Severity Report ::remove low fuel event description from the report
				//CR317-added three columns Comm_City,Comm_District,Comm_State 
				//CR328-added one column Error_Code 
				/*basicQueryString = "SELECT ae.Serial_Number, be.Event_Description, ae.Event_Severity, CONVERT_TZ(ae.Event_Generated_Time,'+00:00','+05:30') AS Event_Generated_Time, aa.account_name AS tenancy_name, ab.account_name AS parent_tenancy_name, croe.Zone, croe.Profile, croe.Model, croe.tmh, croe.Pkt_Recd_TS, croe.Roll_Off_Date, croe.Installed_date, aa.mobile, croe.Comm_City, croe.Comm_District, croe.Comm_State, mp.Error_Code"
						+ " FROM asset_event ae, asset_owner_snapshot aos, asset a, business_event be, account aa, account ab, com_rep_oem_enhanced croe, monitoring_parameters mp";*///ME100011766.n
				basicQueryString = "SELECT ae.Serial_Number, be.Event_Description, ae.Event_Severity, CONVERT_TZ(ae.Event_Generated_Time,'+00:00','+05:30') AS Event_Generated_Time, croe.CustomerName AS tenancy_name, croe.Dealer_Name AS parent_tenancy_name, croe.Zone, croe.Profile, croe.Model, croe.tmh, croe.Pkt_Recd_TS, croe.Roll_Off_Date, croe.Installed_date, aa.mobile, croe.Comm_City, croe.Comm_District, croe.Comm_State, mp.Error_Code"
						+ " FROM asset_event ae, asset_owner_snapshot aos, asset a, business_event be, account aa, account ab, com_rep_oem_enhanced croe, monitoring_parameters mp";//ME100011766.n
				
				whereQuery = " WHERE a.primary_owner_id in ("
						+ accountIdString
						+ ") and aos.Serial_Number = ae.Serial_Number AND aos.Serial_Number=croe.serial_Number AND be.parameter_id = mp.parameter_id AND aos.account_ID IN ("
						+ accountIdString
						+ ") AND ae.Active_Status = 1  AND ae.PartitionKey =1 AND a.serial_number = ae.serial_Number AND a.serial_number=croe.serial_Number  AND a.primary_owner_id = aa.account_id AND be.Event_ID = ae.Event_ID AND a.serial_number = croe.serial_Number "
						+ " AND ab.Account_ID = case when aa.parent_id is null"
						+ " AND be.Event_Description != 'Fuel Level is low' and be.Event_Description != 'Low Fuel Level' "
						+ " then 1001" + " else aa.parent_id" + " end";
				groupByQuery = " group by ae.serial_number, ae.Event_ID";
				orderbyQuery = " order by ae.event_Severity asc,ae.event_Generated_Time desc";
				
			}
			else
			{
				//DF20190328 @abhishek---->change query to remove dependency from tenancy table.
				//@shajesh =>  : 20201211 : Alert Severity Report customer Mobile number is overlapped by dealer mobile number
				//@shajesh =>  : 20211109 : Alert Severity Report ::remove low fuel event description from the report
				//CR317-added three columns Comm_City,Comm_District,Comm_State 
				//CR328-added one column Error_Code 
				/*basicQueryString = "SELECT ae.Serial_Number, be.Event_Description, ae.Event_Severity, CONVERT_TZ(ae.Event_Generated_Time,'+00:00','+05:30') AS Event_Generated_Time, aa.account_name AS tenancy_name, ab.account_name AS parent_tenancy_name, croe.Zone, croe.Profile, croe.Model, croe.tmh, croe.Pkt_Recd_TS, croe.Roll_Off_Date, croe.Installed_date, aa.mobile, croe.Comm_City, croe.Comm_District, croe.Comm_State, mp.Error_Code"
						+ " FROM asset_event ae, asset_owner_snapshot aos, asset a, business_event be, account aa, account ab, com_rep_oem_enhanced croe, monitoring_parameters mp";*///ME100011766.o
				
				basicQueryString = "SELECT ae.Serial_Number, be.Event_Description, ae.Event_Severity, CONVERT_TZ(ae.Event_Generated_Time,'+00:00','+05:30') AS Event_Generated_Time, croe.CustomerName AS tenancy_name, croe.Dealer_Name AS parent_tenancy_name, croe.Zone, croe.Profile, croe.Model, croe.tmh, croe.Pkt_Recd_TS, croe.Roll_Off_Date, croe.Installed_date, aa.mobile, croe.Comm_City, croe.Comm_District, croe.Comm_State, mp.Error_Code"
						+ " FROM asset_event ae, asset_owner_snapshot aos, asset a, business_event be, account aa, account ab, com_rep_oem_enhanced croe, monitoring_parameters mp";//ME100011766.n
				
				whereQuery = " WHERE aos.Serial_Number = ae.Serial_Number AND aos.Serial_Number=croe.serial_Number AND be.parameter_id = mp.parameter_id AND aos.account_ID IN ("
						+ accountIdString
						+ ") AND ae.Active_Status = 1  AND ae.PartitionKey =1 AND a.serial_number = ae.serial_Number AND croe.serial_Number=ae.serial_Number  AND a.primary_owner_id = aa.account_id AND be.Event_ID = ae.Event_ID"
						+ " AND ab.Account_ID = case when aa.parent_id is null"
						+ " AND be.Event_Description != 'Fuel Level is low' and be.Event_Description != 'Low Fuel Level' "
						+ " then 1001" + " else aa.parent_id" + " end";
				groupByQuery = " group by ae.serial_number, ae.Event_ID";
				orderbyQuery = " order by ae.event_Severity asc,ae.event_Generated_Time desc";
			}
			 }
			
			String alertTypeIdString =null;
			
			if(request.getEventTypeIdList()!=null && (!request.getEventTypeIdList().isEmpty()))
			{
				alertTypeIdString = conversionObj.getIntegerListString(request.getEventTypeIdList()).toString();
				whereQuery= whereQuery + " and ae.Event_Type_ID in ("+alertTypeIdString+")";
			}
			else{
				whereQuery= whereQuery + " and ae.Event_Type_ID in (2, 4)";
			}
			
			// s.n : CR-316 : 20220509 : Prafull : adding where clause to exclude the event Ids from the output 
			String BlockedaletIdString = null;
			
			if(request.getBlockedEventIdList()!=null && (!request.getBlockedEventIdList().isEmpty()))
			{
				BlockedaletIdString = conversionObj.getIntegerListString(request.getBlockedEventIdList()).toString();
				whereQuery= whereQuery + " and ae.Event_ID not in ("+BlockedaletIdString+")";
			}
			// e.n : CR-316 : 20220509 : Prafull : adding where clause to exclude the event Ids from the output 
			
			
			if(request.getEventSeverityList()!=null && (!request.getEventSeverityList().isEmpty()))
			{
				String alertSeverityString = conversionObj.getStringList(request.getEventSeverityList()).toString();
				
				whereQuery= whereQuery + " and ae.Event_Severity in ("+alertSeverityString+")";
			}

			
			
			finalQuery = basicQueryString+whereQuery+groupByQuery+orderbyQuery;
			
			iLogger.info("getAlertSeverityReportDetails finalQuery ::"+finalQuery);

		/*	Shajesh : 202010709 ; Duplicate entry in Active alert report So commenting the duplicate code */
			/*ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(finalQuery);

			

			while(rs.next()){
				AlertSeverityReportRespContract respObj=new AlertSeverityReportRespContract();
				
				respObj.setSerialNumber(rs.getString("Serial_Number"));
				respObj.setAlertDescription(rs.getString("Event_Description"));
				respObj.setAlertSeverity(rs.getString("Event_Severity"));
				respObj.setLatestReceivedTime(String.valueOf(rs.getTimestamp("Event_Generated_Time")));
				respObj.setCustomerName(rs.getString("tenancy_name"));
				respObj.setDealerName(rs.getString("parent_tenancy_name"));
				if(rs.getString("zone") != null){respObj.setZone(rs.getString("Zone"));}else{respObj.setZone("NA");}
				if(rs.getString("Model") !=null){respObj.setModel(rs.getString("Model"));}else{respObj.setModel("NA");}
				if(rs.getString("Profile") != null){respObj.setProfile(rs.getString("Profile"));}else{respObj.setProfile("NA");}
				if(rs.getString("tmh") !=null){respObj.setMachineHrs(rs.getString("tmh"));}else{respObj.setMachineHrs("NA");}
				if(rs.getString("Pkt_Recd_TS") !=null){respObj.setPktReceivedDate(rs.getString("Pkt_Recd_TS"));}else{respObj.setPktReceivedDate("NA");}
				if(rs.getString("Roll_Off_Date") !=null){respObj.setMachineRollOffDate(rs.getString("Roll_Off_Date"));}else{respObj.setMachineRollOffDate("NA");}
				if(rs.getString("Installed_date") !=null){respObj.setMachineInstallationDate(rs.getString("Installed_date"));}else{respObj.setMachineInstallationDate("NA");}
				if(rs.getString("mobile") !=null){respObj.setCustomerNumber(rs.getString("mobile"));}else{respObj.setCustomerNumber("NA");}
				
				responseList.add(respObj);	

			}*/
			 }//end of outer if
			 else{
				 //query for custom group id passed as input
				//@shajesh =>  : 20211109 : Alert Severity Report ::remove low fuel event description from the report
				 //CR317-added three columns Comm_City,Comm_District,Comm_State
				 //CR328-added one column Error_Code 
				 /*basicQueryString = "SELECT ae.Serial_Number, be.Event_Description, ae.Event_Severity, CONVERT_TZ(ae.Event_Generated_Time,'+00:00','+05:30') AS Event_Generated_Time,aa.account_name AS tenancy_name, ab.account_name AS parent_tenancy_name, cag.group_Name AS groupName, croe.Zone, croe.Profile, croe.Model, croe.tmh, croe.Pkt_Recd_TS, croe.Roll_Off_Date, croe.Installed_date,aa.mobile, croe.Comm_City, croe.Comm_District, croe.Comm_State, mp.Error_Code "
							+ " FROM asset_event ae, custom_asset_group_snapshot cags, asset a,asset_owner_snapshot aos, business_event be,account aa, account ab,  com_rep_oem_enhanced croe,custom_asset_group cag, monitoring_parameters mp";*///ME100011766.o
				 
				 
				 basicQueryString = "SELECT ae.Serial_Number, be.Event_Description, ae.Event_Severity, CONVERT_TZ(ae.Event_Generated_Time,'+00:00','+05:30') AS Event_Generated_Time,croe.CustomerName AS tenancy_name, croe.Dealer_Name AS parent_tenancy_name, croe.Zone, cag.group_Name AS groupName, croe.Zone, croe.Profile, croe.Model, croe.tmh, croe.Pkt_Recd_TS, croe.Roll_Off_Date, croe.Installed_date,aa.mobile, croe.Comm_City, croe.Comm_District, croe.Comm_State, mp.Error_Code "
							+ " FROM asset_event ae, custom_asset_group_snapshot cags, asset a,asset_owner_snapshot aos, business_event be,account aa, account ab,  com_rep_oem_enhanced croe,custom_asset_group cag, monitoring_parameters mp";//ME100011766.n

					
					whereQuery = " WHERE cags.Asset_Id = ae.Serial_Number AND cags.Asset_Id=croe.serial_Number AND " +
							"aos.Serial_Number = ae.Serial_Number AND be.parameter_id = mp.parameter_id AND cags.Group_ID in(" +
							customAssetGroupIdListAsString+")"+
							" AND aos.Serial_Number=croe.serial_Number AND aos.account_ID IN ("
							+ accountIdString
							+ ") AND ae.Active_Status = 1  AND ae.PartitionKey =1 AND a.serial_number = ae.serial_Number AND croe.serial_Number=ae.serial_Number  AND a.primary_owner_id = aa.account_id AND be.Event_ID = ae.Event_ID"
							+ " AND ab.Account_ID = case when aa.parent_id is null"
							+ " AND be.Event_Description != 'Fuel Level is low' and be.Event_Description != 'Low Fuel Level' "
							+ " then 1001" + " else aa.parent_id" + " end";
					groupByQuery = " group by ae.serial_number, ae.Event_ID";
					orderbyQuery = " order by ae.event_Severity asc,ae.event_Generated_Time desc";
			 }
			 
			 finalQuery = basicQueryString+whereQuery+groupByQuery+orderbyQuery;
				
				iLogger.info("getAlertSeverityReportDetails finalQuery ::"+finalQuery);

				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(finalQuery);

				

				while(rs.next()){
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
					AlertSeverityReportRespContract respObj=new AlertSeverityReportRespContract();
					
					respObj.setSerialNumber(rs.getString("Serial_Number"));
					respObj.setAlertDescription(rs.getString("Event_Description"));
					respObj.setAlertSeverity(rs.getString("Event_Severity"));
					respObj.setLatestReceivedTime(String.valueOf(rs.getTimestamp("Event_Generated_Time")));
					
					if(rs.getString("tenancy_name") != null && !rs.getString("tenancy_name").isEmpty()) {
					    respObj.setCustomerName(rs.getString("tenancy_name"));
					} else {
					    respObj.setCustomerName("NA");
					}

					if(rs.getString("parent_tenancy_name") != null && !rs.getString("parent_tenancy_name").isEmpty()) {
					    respObj.setDealerName(rs.getString("parent_tenancy_name"));
					} else {
					    respObj.setDealerName("NA");
					}

					if(rs.getString("zone") != null){respObj.setZone(rs.getString("Zone"));}else{respObj.setZone("NA");}
					if(rs.getString("Model") !=null){respObj.setModel(rs.getString("Model"));}else{respObj.setModel("NA");}
					if(rs.getString("Profile") != null){respObj.setProfile(rs.getString("Profile"));}else{respObj.setProfile("NA");}
					if(rs.getString("tmh") !=null){respObj.setMachineHrs(rs.getString("tmh"));}else{respObj.setMachineHrs("NA");}
					if(rs.getString("Pkt_Recd_TS") !=null){respObj.setPktReceivedDate(rs.getString("Pkt_Recd_TS"));}else{respObj.setPktReceivedDate("NA");}
					if(rs.getString("Roll_Off_Date") !=null){respObj.setMachineRollOffDate(rs.getString("Roll_Off_Date"));}else{respObj.setMachineRollOffDate("NA");}
					if(rs.getString("Installed_date") !=null){respObj.setMachineInstallationDate(rs.getString("Installed_date"));}else{respObj.setMachineInstallationDate("NA");}
					if(rs.getString("mobile") !=null){respObj.setCustomerNumber(rs.getString("mobile"));}else{respObj.setCustomerNumber("NA");}
					//CR-317-SN
					if(rs.getString("Comm_City") !=null){respObj.setCity(rs.getString("Comm_City"));}else{respObj.setCity("NA");}
					if(rs.getString("Comm_District") !=null){respObj.setDistrict(rs.getString("Comm_District"));}else{respObj.setDistrict("NA");}
					if(rs.getString("Comm_State") !=null){respObj.setState(rs.getString("Comm_State"));}else{respObj.setState("NA");}
					//CR-317-EN
					//CR-328-SN
					if(rs.getString("Error_Code") !=null){respObj.setDtcCode(rs.getString("Error_Code"));}else{respObj.setDtcCode("NA");}
					//CR-328-EN
					responseList.add(respObj);	

				}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception :"+e.getMessage());
		}

		finally
		{
			
			if(rs1!=null)
				try {
					rs1.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if(statement1!=null)
				try {
					statement1.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			if (prodConnection1 != null) {
				try {
					prodConnection1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
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

		return responseList;
	}
private Logger fLogger = FatalLoggerClass.logger;
private Logger iLogger = InfoLoggerClass.logger;

	public String saveToCopy(List<AlertSeverityReportRespContract> alertSeverityReportDetailsNew, String sourceDir) {

		String response = "FAILURE";
		
		File file = new File(sourceDir + "/AlertSeverityReport.csv");
		iLogger.info("path for AlertSeverityReport" + file);
		if (alertSeverityReportDetailsNew.isEmpty()) {
			iLogger.info("Data is empty. Nothing to save to CSV.");
		} else {
			AlertSeverityReportRespContract firstRecord = alertSeverityReportDetailsNew.get(0);
			try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
				String[] header1 = { "serialNumber", "dealerName", "customerName", "alertDescription",
						"latestReceivedTime", "alertSeverity", "zone", "profile", "model", "machineHrs",
						"pktReceivedDate", "machineRollOffDate", "machineInstallationDate", "customerNumber", "city",
						"district", "state", "dtcCode" };
				writer.writeNext(header1);

				// Write data from alertSeverityReportDetails to the CSV file
				SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S"); // Adjusted format
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

				for (AlertSeverityReportRespContract detail : alertSeverityReportDetailsNew) {
					 Date date;
					    try {
					        date = inputDateFormat.parse(detail.getLatestReceivedTime());
					    } catch (ParseException e) {
					        e.printStackTrace();
					        fLogger.fatal("Error parsing timestamp: " + e.getMessage());
					        continue; // Skip this record if timestamp parsing fails
					    }
					    // Format the timestamp using the outputDateFormat
					    String formattedDate = outputDateFormat.format(date);
					    Date pktReceivedDate;
					    Date machineRollOffDate;
					    Date machineInstallationDate;
					    try {
					        pktReceivedDate = inputDateFormat.parse(detail.getPktReceivedDate());
					        machineRollOffDate = inputDateFormat.parse(detail.getMachineRollOffDate());
					        machineInstallationDate= inputDateFormat.parse(detail.getMachineInstallationDate());
					    } catch (ParseException e) {
					        e.printStackTrace();
					        fLogger.fatal("Error parsing pktReceivedDate or machineRollOffDate: " + e.getMessage());
					        continue; // Skip this record if timestamp parsing fails
					    }
					    String formattedPktReceivedDate = outputDateFormat.format(pktReceivedDate);
					    String formattedMachineRollOffDate = outputDateFormat.format(machineRollOffDate);
					    String formattedMachineInstallationDate= outputDateFormat.format(machineInstallationDate);
					String[] recordValues = { detail.getSerialNumber(), detail.getDealerName(),
							detail.getCustomerName(), detail.getAlertDescription(), formattedDate,
							detail.getAlertSeverity(), detail.getZone(), detail.getProfile(), detail.getModel(),
							detail.getMachineHrs(),formattedPktReceivedDate,formattedMachineRollOffDate,
							formattedMachineInstallationDate, detail.getCustomerNumber(), detail.getCity(),
							detail.getDistrict(), detail.getState(), detail.getDtcCode() };
					writer.writeNext(recordValues);
				}
				iLogger.info("Data has been saved to filepath: " + file.getAbsolutePath());
				response = "SUCCESS";
			} catch (IOException e) {
				e.printStackTrace();
				fLogger.fatal("Error occurred while saving to CSV: " + e.getMessage());
				return response;
			}
		}
		return response;
	}

	public List<AlertSeverityReportRespContract> getAlertSeverityReportDetails(AlertSeverityReportReqContract request) throws CustomFault{		

		Logger bLogger = BusinessErrorLoggerClass.logger;

		ContactEntity contact;String userRole;
		String customerCare = null,admin = null;
		List<AlertSeverityReportRespContract> responseList = new LinkedList<AlertSeverityReportRespContract>();	
		try
		{

			DomainServiceImpl domainService = new DomainServiceImpl();
			contact = domainService.getContactDetails(request.getLoginId());
			if(contact==null || contact.getContact_id()==null)
			{
				throw new CustomFault("Invalid LoginId");
			}

			if(contact.getRole()!=null)
				userRole = contact.getRole().getRole_name();
			else
				throw new CustomFault("User is not assigned a role");

			//2.TenancyIdList
			if( (userRole.equalsIgnoreCase(customerCare) || userRole.equalsIgnoreCase(admin)) &&  
					(request.isOwnStock()==false) &&  
					(request.getTenancyIdList()==null || request.getTenancyIdList().isEmpty()))
			{
				throw new CustomFault("Please Provide Tenancy List");
			}


			//4.UserTenancyIdList
			if(request.getLoginTenancyIdList()==null || request.getLoginTenancyIdList().isEmpty())
			{
				throw new CustomFault("User Tenancy Id should be passed");
			}
			if(request.getTenancyIdList()==null)
			{
				throw new CustomFault("Tenancy Id List should be specified");
			}

			if(request.getLoginId()==null || request.getLoginId().isEmpty()){
				bLogger.error("The Login Id is not available.");
				throw new CustomFault("The Login Id is not available.");
			}
			if(request.getPeriod()!=null)
			{
				if( !(request.getPeriod().equalsIgnoreCase("Week") || request.getPeriod().equalsIgnoreCase("Month") ||
						request.getPeriod().equalsIgnoreCase("Quarter") || request.getPeriod().equalsIgnoreCase("Year") ||
						request.getPeriod().equalsIgnoreCase("Last Week") ||      request.getPeriod().equalsIgnoreCase("Last Month") ||
						request.getPeriod().equalsIgnoreCase("Last Quarter") || request.getPeriod().equalsIgnoreCase("Last Year") ) )
				{
					throw new CustomFault("Invalid Time Period");

				}
			}

			int pageNumber =0;
			EventDetailsBO eventBO=new EventDetailsBO();	
			IstGmtTimeConversion gmtIstConv = new IstGmtTimeConversion();
			if(request.isActiveAlerts()){//call user alerts method
				List<UserAlertsImpl> userAlertsList = eventBO.getUserAlertsNew(contact,userRole,request.getLoginTenancyIdList(),request.getTenancyIdList(),
						null,null,request.getEventTypeIdList(),
						request.getEventSeverityList(),request.isOwnStock(),false,pageNumber);

				for(int i=0; i<userAlertsList.size(); i++)
				{
					AlertSeverityReportRespContract alertResponse= new AlertSeverityReportRespContract();
					alertResponse.setAlertDescription(userAlertsList.get(i).getAlertDescription());
					alertResponse.setAlertSeverity(userAlertsList.get(i).getAlertSeverity());
					alertResponse.setCustomerName(userAlertsList.get(i).getAlertTypeName());
					//alertResponse.setLatestReceivedTime(userAlertsList.get(i).getLatestReceivedTime());
					//DefectID:20150810 @Suprava GMT to IST Conversion 
					if(userAlertsList.get(i).getLatestReceivedTime()!=null && !(userAlertsList.get(i).getLatestReceivedTime().isEmpty())){
						String latestReceivedTime = userAlertsList.get(i).getLatestReceivedTime();
						Timestamp latestReceivedTimestamp = gmtIstConv.convertGmtToIst(latestReceivedTime);
						String latestReceivedTimeString = latestReceivedTimestamp.toString();
					alertResponse.setLatestReceivedTime(latestReceivedTimeString);
					}
					//End 20150810
					alertResponse.setDealerName(userAlertsList.get(i).getRemarks());
					alertResponse.setSerialNumber(userAlertsList.get(i).getSerialNumber());
					responseList.add(alertResponse);
				}
			}
			else{List<UserAlertsImpl> userAlertsList = eventBO.getUserAlertsNew(contact,userRole,request.getLoginTenancyIdList(),request.getTenancyIdList(),
					null,request.getPeriod(),request.getEventTypeIdList(),
					request.getEventSeverityList(),request.isOwnStock(),false,pageNumber);

			for(int i=0; i<userAlertsList.size(); i++)
			{
				AlertSeverityReportRespContract alertResponse= new AlertSeverityReportRespContract();
				alertResponse.setAlertDescription(userAlertsList.get(i).getAlertDescription());
				alertResponse.setAlertSeverity(userAlertsList.get(i).getAlertSeverity());
				alertResponse.setCustomerName(userAlertsList.get(i).getAlertTypeName());
			//	alertResponse.setLatestReceivedTime(userAlertsList.get(i).getLatestReceivedTime());
				//DefectID:20150810 @Suprava GMT to IST Conversion 
				if(userAlertsList.get(i).getLatestReceivedTime()!=null && !(userAlertsList.get(i).getLatestReceivedTime().isEmpty())){
					String latestReceivedTime = userAlertsList.get(i).getLatestReceivedTime();
					Timestamp latestReceivedTimestamp = gmtIstConv.convertGmtToIst(latestReceivedTime);
					String latestReceivedTimeString = latestReceivedTimestamp.toString();
				alertResponse.setLatestReceivedTime(latestReceivedTimeString);
				}
				//End 20150810
				alertResponse.setDealerName(userAlertsList.get(i).getRemarks());
				alertResponse.setSerialNumber(userAlertsList.get(i).getSerialNumber());
				responseList.add(alertResponse);
			}}

		}

		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}

		return responseList;
	}
	

}
