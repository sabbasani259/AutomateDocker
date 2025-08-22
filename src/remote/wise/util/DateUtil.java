package remote.wise.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.TenancyEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DateUtil 
{
	int week;
	int month;
	int quarter;
	int year;
	int currentYear;

	
	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getQuarter() {
		return quarter;
	}

	public void setQuarter(int quarter) {
		this.quarter = quarter;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	
	public int getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(int currentYear) {
		this.currentYear = currentYear;
	}

	//************************* Get the WeekNumber, Month, Quarter and Year for the inputDate ********************************
	public DateUtil getCurrentDateUtility(Date inputDate)
	{
		Calendar cal = Calendar.getInstance(); 
        cal.setTime(inputDate); 
        
        this.week = cal.get(Calendar.WEEK_OF_YEAR);
          
        int monthValue = cal.get(Calendar.MONTH);
        this.month = monthValue+1;
                
        this.quarter = (monthValue/3)+1;
        
        this.year = cal.get(Calendar.YEAR);
       
        if(month==12 && week==1)
        	this.week=53;
        
		return this;
	}
	
	//*************************** Get the previous Week, previous month, previous Quarter, previous Year and current Year ****************************
	public DateUtil getPreviousDateUtility(Date inputDate)
	{
		Calendar cal = Calendar.getInstance(); 
        cal.setTime(inputDate); 
        
        int currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        this.week = cal.get(Calendar.WEEK_OF_YEAR);
                  
        cal.setTime(inputDate);
        cal.add(Calendar.MONTH, -1);
        int monthValue = cal.get(Calendar.MONTH);
        this.month = monthValue+1;
                
        cal.setTime(inputDate);
        int currentMonth = cal.get(Calendar.MONTH)+1;
        if(currentMonth==1 || currentMonth==2 || currentMonth==3)
        	this.quarter=4;
        else
        	this.quarter = ((currentMonth-1)/3);
        
        
        cal.setTime(inputDate);
        cal.add(Calendar.YEAR, -1);
        this.year = cal.get(Calendar.YEAR);
       
        cal.setTime(inputDate);
        this.currentYear = cal.get(Calendar.YEAR);
        
        /*if( (month==12 && week==1) || (currentWeek==1 && currentMonth==1) )*/
        //DF20140102 - Rajani Nagaraju - To match the Week number in date dimension
        if( (month==12 && week==1))
        	this.week=53;
        
		return this;
	}
	
	public HashMap getCurrentWeekDifference(Date TxnDate){
		boolean backlogPacket = false;
		
		
		Properties prop = CommonUtil.getDepEnvProperties();
		String amhTable = null;
		String amdTable = null;
		String amdETable = null;
		String JCB_AMH_TableKey = prop.getProperty("JCB_AMH_TableKey");
		String JCB_AMD_TableKey = prop.getProperty("JCB_AMD_TableKey");
		String JCB_AMDE_TableKey = prop.getProperty("JCB_AMDE_TableKey");
		
		Calendar cal = Calendar.getInstance();
		int currentWeekNo = cal.get(Calendar.WEEK_OF_YEAR);
		
		int transactionWeekNo = 0;
		int transactionYear = 0;
		
		HashMap<String,String> dynamicTableMapping = new HashMap<String,String>();
		
		if(TxnDate!=null){
			cal.setTime(TxnDate);
			transactionWeekNo = cal.get(Calendar.WEEK_OF_YEAR);
			transactionYear = cal.get(Calendar.YEAR);
			
			
			if(currentWeekNo - transactionWeekNo < 8){
				/*amhTable = JCB_AMH_TableKey.replace("week", transactionWeekNo+"");
				dynamicTableMapping.put("AMH", amhTable);
				amdTable = JCB_AMD_TableKey.replace("week", transactionWeekNo+"");
				dynamicTableMapping.put("AMD", amdTable);
				amdETable = JCB_AMDE_TableKey.replace("week", transactionWeekNo+"");
				dynamicTableMapping.put("AMDE", amdETable);*/
				
				amhTable = JCB_AMH_TableKey.replace("week", transactionWeekNo+"");
                amhTable =amhTable.replace("year", transactionYear+"");
                dynamicTableMapping.put("AMH", amhTable);
                amdTable = JCB_AMD_TableKey.replace("week", transactionWeekNo+"");
                amdTable =amdTable.replace("year", transactionYear+"");
                dynamicTableMapping.put("AMD", amdTable);
                amdETable = JCB_AMDE_TableKey.replace("week", transactionWeekNo+"");
                amdETable =amdETable.replace("year", transactionYear+"");
                dynamicTableMapping.put("AMDE", amdETable);

				
				
			}
		}
		return dynamicTableMapping;
	}
	/*
	public HashMap getTxnCurrentWeekDifference(Date TxnDate){
		boolean backlogPacket = false;


		Properties prop = CommonUtil.getDepEnvProperties();
		//String amhTable = null;
		
		String amhTable = prop.getProperty("default_TAssetMonData_Table");
		
		String JCB_AMH_TableKey = prop.getProperty("JCB_TAMD_TableKey");


		Calendar cal = Calendar.getInstance();
		int currentWeekNo = cal.get(Calendar.WEEK_OF_YEAR);

		int transactionWeekNo = 0;
		int transactionYear = 0;
		int currYear=cal.get(Calendar.YEAR);
		HashMap<String,String> dynamicTableMapping = new HashMap<String,String>();

		if(TxnDate!=null){
			cal.setTime(TxnDate);
			transactionWeekNo = cal.get(Calendar.WEEK_OF_YEAR);
			transactionYear = cal.get(Calendar.YEAR);
			int noofweeksintransactionYear=cal.getActualMaximum(Calendar.WEEK_OF_YEAR);

			if(currYear!=transactionYear){

				currentWeekNo =noofweeksintransactionYear+currentWeekNo;

			}



			if(currentWeekNo - transactionWeekNo < 8){
				amhTable = JCB_AMH_TableKey.replace("week", transactionWeekNo+"");
					dynamicTableMapping.put("AMH", amhTable);
					amdTable = JCB_AMD_TableKey.replace("week", transactionWeekNo+"");
					dynamicTableMapping.put("AMD", amdTable);
					amdETable = JCB_AMDE_TableKey.replace("week", transactionWeekNo+"");
					dynamicTableMapping.put("AMDE", amdETable);

				amhTable = JCB_AMH_TableKey.replace("week", transactionWeekNo+"");
				amhTable =amhTable.replace("year", transactionYear+"");
				dynamicTableMapping.put("AMH", amhTable);
				//amdTable = JCB_AMD_TableKey.replace("week", transactionWeekNo+"");


			}
		}
		return dynamicTableMapping;
	}*/
	
	public String getDynamicTable(String txnKey, Date TxnDate){

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;


		Properties prop=null;
		try
		{
			prop= StaticProperties.getConfProperty();
		}
		catch(Exception e)
		{
			fLogger.fatal(txnKey+":AMS:DAL:TAssetMonData-getTAssetMonData"+"Error in intializing property File :"+e.getMessage());
		}

		//String TAssetMonTable = null;
		
		String TAssetMonTable = prop.getProperty("default_TAssetMonData_Table");

		String jcbTAssetMonTableFormat = prop.getProperty("JCB_TAssetMonData_TableKey");


		Calendar cal = Calendar.getInstance();
		int currYear=cal.get(Calendar.YEAR);
		int currentWeekNo = cal.get(Calendar.WEEK_OF_YEAR);

		int transactionWeekNo = 0;
		int transactionYear=0;


		if(TxnDate!=null){
			cal.setTime(TxnDate);
			transactionWeekNo = cal.get(Calendar.WEEK_OF_YEAR);
			transactionYear = cal.get(Calendar.YEAR);
			int noofweeksintransactionYear=cal.getActualMaximum(Calendar.WEEK_OF_YEAR);

			if(currYear!=transactionYear){

				currentWeekNo =noofweeksintransactionYear+currentWeekNo;

			}
			
			//DF20180104 @Roopa if the days in the year end falls in next year of first week than inserting those days to last table of the year
			
			Calendar futurecal = Calendar.getInstance();
			
			futurecal.add(Calendar.DAY_OF_YEAR, 6);
			int futureYear=futurecal.get(Calendar.YEAR);
			
			
			
			if(transactionWeekNo==1 && futureYear==transactionYear+1){
				transactionWeekNo=noofweeksintransactionYear;
			}
			
			if(currentWeekNo - transactionWeekNo < 8){
				TAssetMonTable = jcbTAssetMonTableFormat.replace("week", transactionWeekNo+"");
				TAssetMonTable =TAssetMonTable.replace("year", transactionYear+"");

			}
		}
		return TAssetMonTable;
	}
	
public List<String> roleAlertMapDetails(String loginId, int loginTenancyId, String Mode){
		

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		HashMap<String,HashMap<String,String>> roleAlertMap=new HashMap<String,HashMap<String,String>>();

		List<String> alertCodeList=new ArrayList<String>();
		String roleId = null;

		Connection prodConnection = null;
		Statement statement = null;
		Statement statement1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		
		String selectQuery=null;

		try{

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery("select * from alert_role_mapping");

			String alertMap = null;
			HashMap<String,String> alertMapDetails=new HashMap<String, String>();

			String roleNAme;

			while(rs.next()){

				roleNAme=String.valueOf(rs.getInt("role_id"));
				if(Mode.equalsIgnoreCase("SMS")){
					alertMap=rs.getObject("alertmap_sms").toString();
				}
				if(Mode.equalsIgnoreCase("EMAIL")){
					alertMap=rs.getObject("alertmap_email").toString();
				}

				if(Mode.equalsIgnoreCase("Display")){
					alertMap=rs.getObject("alertmap_display").toString();			
				}
				
				alertMapDetails=new Gson().fromJson(alertMap, new TypeToken<HashMap<String, Object>>() {}.getType());
				
				roleAlertMap.put(roleNAme, alertMapDetails);

			}

			iLogger.info("roleAlertMap ::"+roleAlertMap.size());

			statement1 = prodConnection.createStatement();
			
			if(loginId!=null){
				selectQuery="select c.Role_ID from contact c where c.Contact_ID='"+loginId+"' ";	
			}
			
			else if(loginTenancyId!=0){
				
				//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
				List<Integer> tenancyIdList=new ArrayList<Integer>();
				tenancyIdList.add(loginTenancyId);
				
				tenancyIdList.addAll(new DateUtil().getLinkedTenancyListForTheTenancy(tenancyIdList));
				
				ListToStringConversion conversion = new ListToStringConversion();
				String tenancyIdListString = conversion.getIntegerListString(tenancyIdList).toString();
				System.out.println(tenancyIdListString);
				selectQuery="select c.Role_ID from contact c, account_contact ac, account_tenancy at where at.Tenancy_ID in ("+tenancyIdListString+") and " +
						"at.Account_ID=ac.Account_ID and ac.Contact_ID=c.Contact_ID";
			}
			else{
				return alertCodeList;
			}


			rs1 = statement1.executeQuery(selectQuery);
			rs1.setFetchSize(1);

			while(rs1.next()){
				roleId=String.valueOf(rs1.getInt("Role_ID"));
			}



			iLogger.info("roleId :"+roleId);

			if(roleAlertMap!=null && roleAlertMap.size()>0){

				HashMap<String,String> roleMap=roleAlertMap.get(roleId);

				if(roleMap!=null && roleMap.size()>0){

					for(Map.Entry pair: roleMap.entrySet()){

						if(String.valueOf(pair.getValue()).equalsIgnoreCase("1")){

							alertCodeList.add(String.valueOf(pair.getKey()));

						}

					}


				}

			}
		}

		catch (SQLException e) {

			e.printStackTrace();
			fLogger.fatal("SQL Exception in fetching data from mysql::"+e.getMessage());
		} 

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception in fetching data from mysql::"+e.getMessage());
		}

		finally {
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if(rs1!=null)
				try {
					rs1.close();
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

			if(statement1!=null)
				try {
					statement1.close();
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
		return alertCodeList;

	}

public List<String> roleAlertMapDetailsNew(String loginId, int loginTenancyId, String Mode,int pageNumber,int eventTypeID){
	

	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;

	TreeMap<String,TreeMap<String,String>> roleAlertMap=new TreeMap<String,TreeMap<String,String>>();

	List<String> alertCodeList=new ArrayList<String>();
	List<String> alertCodeList1=new ArrayList<String>();
	String roleId = null;

	Connection prodConnection = null;
	Statement statement = null;
	Statement statement1 = null;
	Statement statement2 = null;
	ResultSet rs = null;
	ResultSet rs1 = null;
	ResultSet rs2 = null;
	String selectQuery=null;
	int pge;
//	iLogger.info("pageNumber % 5 ::"+pageNumber % 5);
//	if ((pageNumber % 5) == 0) {
//		pge = pageNumber / 5;
//		iLogger.info("pageNumber::"+pge );
//	}
//
//	else if (pageNumber == 1) {
//		pge = 1;
//	}
//
//	else {
//		while (((pageNumber) % 5) != 0) {
//			pageNumber = pageNumber - 1;
//		}
//
//		pge = ((pageNumber) / 5) + 1;
//	}
	iLogger.info("pageNumber"+pageNumber);
	int startLimit = (pageNumber - 1)*50;
	int endLimit = startLimit+ 50;
	iLogger.info("startLimit:1:"+startLimit );
	
	if(startLimit!=0)
	{
	
		startLimit=startLimit+1;
		iLogger.info("startLimit:2:"+startLimit );
	}
	iLogger.info("startLimit:3:"+startLimit );
	
	
	iLogger.info("endLimit::"+endLimit );

	try{

		ConnectMySQL connMySql = new ConnectMySQL();
		prodConnection = connMySql.getConnection();
		statement = prodConnection.createStatement();
		rs = statement.executeQuery("select * from alert_role_mapping ");
		
		String query ="select a.Event_Type_Name,b1.Event_ID from event_type a join  business_event b1 on  b1.Event_Type_ID = a.Event_Type_ID where a.Event_Type_ID='"+eventTypeID+"'";
		statement2 = prodConnection.createStatement();
		rs2 = statement2.executeQuery(query);
		while(rs2.next()){
			alertCodeList1.add(rs2.getString("Event_ID"));
		}
			Set<String> normalizedAlertCodeList1 = alertCodeList1.stream().map(code -> String.format("%03d", Integer.parseInt(code))).collect(Collectors.toSet());
		String alertMap = null;
		TreeMap<String, String> alertMapDetails=new TreeMap<String, String>();

		String roleNAme;

		while(rs.next()){

			roleNAme=String.valueOf(rs.getInt("role_id"));
			if(Mode.equalsIgnoreCase("SMS")){
				alertMap=rs.getObject("alertmap_sms").toString();
			}
			if(Mode.equalsIgnoreCase("EMAIL")){
				alertMap=rs.getObject("alertmap_email").toString();
			}

			if(Mode.equalsIgnoreCase("Display")){
				alertMap=rs.getObject("alertmap_display").toString();			
			}
			
			alertMapDetails=new Gson().fromJson(alertMap, new TypeToken<TreeMap<String, Object>>() {}.getType());
			
			roleAlertMap.put(roleNAme, alertMapDetails);

		}

		iLogger.info("roleAlertMap ::"+roleAlertMap.size());

		statement1 = prodConnection.createStatement();
		iLogger.info("loginTenancyId"+loginTenancyId);
		if(loginId!=null){
			selectQuery="select c.Role_ID from contact c where c.Contact_ID='"+loginId+"'";	
		}
		
		else if(loginTenancyId!=0){
			iLogger.info("loginTenancyId"+loginTenancyId);
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			List<Integer> tenancyIdList=new ArrayList<Integer>();
			tenancyIdList.add(loginTenancyId);
			
			tenancyIdList.addAll(new DateUtil().getLinkedTenancyListForTheTenancy(tenancyIdList));
			
			ListToStringConversion conversion = new ListToStringConversion();
			String tenancyIdListString = conversion.getIntegerListString(tenancyIdList).toString();
			iLogger.info("tenancyIdListString"+tenancyIdListString);
			selectQuery="select c.Role_ID from contact c, account_contact ac, account_tenancy at where at.Tenancy_ID in ("+tenancyIdListString+") and " +
					"at.Account_ID=ac.Account_ID and ac.Contact_ID=c.Contact_ID";
		}
		else{
			return alertCodeList;
		}

		iLogger.info("selectQuery"+selectQuery);
		rs1 = statement1.executeQuery(selectQuery);
		rs1.setFetchSize(1);

		while(rs1.next()){
			roleId=String.valueOf(rs1.getInt("Role_ID"));
		}



		iLogger.info("roleId :"+roleId);

		if(roleAlertMap!=null && roleAlertMap.size()>0){

			TreeMap<String, String> roleMap=roleAlertMap.get(roleId);

			if(roleMap!=null && roleMap.size()>0){

				for(Map.Entry pair: roleMap.entrySet()){
					if(String.valueOf(pair.getValue()).equalsIgnoreCase("1") && normalizedAlertCodeList1.contains(String.valueOf(pair.getKey()))) 
					{
						alertCodeList.add(String.valueOf(pair.getKey()));
					}
				}
				int listSize = alertCodeList.size();
				iLogger.info("listSize"+listSize);
				if (startLimit < listSize) {
				    endLimit = Math.min(endLimit, listSize);
				    alertCodeList = alertCodeList.subList(startLimit, endLimit);
				} else {
				    alertCodeList = null; // return empty list for out-of-range page
				}
                iLogger.info("alertCodeList"+alertCodeList);

			}

		}
	}

	catch (SQLException e) {

		e.printStackTrace();
		fLogger.fatal("SQL Exception in fetching data from mysql::"+e.getMessage());
	} 

	catch(Exception e)
	{
		e.printStackTrace();
		fLogger.fatal("Exception in fetching data from mysql::"+e.getMessage());
	}

	finally {
		if(rs!=null)
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		if(rs1!=null)
			try {
				rs1.close();
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

		if(statement1!=null)
			try {
				statement1.close();
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
	return alertCodeList;

}
	public static  String filteralertMap="";
	
	public List<String> roleAlertMapDetailsForHealthTab(String loginId, int loginTenancyId, String Mode){
		

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		HashMap<String,HashMap<String,String>> roleAlertMap=new HashMap<String,HashMap<String,String>>();

		List<String> alertCodeList=new ArrayList<String>();
		String roleId = null;

		Connection prodConnection = null;
		Statement statement = null;
		Statement statement1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		
		String selectQuery=null;

		try{

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery("select * from alert_role_mapping");

			String alertMap = null;
			HashMap<String,String> alertMapDetails=new HashMap<String, String>();

			String roleNAme;

			while(rs.next()){

				roleNAme=String.valueOf(rs.getInt("role_id"));
				if(Mode.equalsIgnoreCase("SMS")){
					alertMap=rs.getObject("alertmap_sms").toString();
				}
				if(Mode.equalsIgnoreCase("EMAIL")){
					alertMap=rs.getObject("alertmap_email").toString();
				}

				if(Mode.equalsIgnoreCase("Display")){
					alertMap=rs.getObject("alertmap_display").toString();			
				}
				
				 filteralertMap=ListToStringConversion.filterAlertmap(alertMap);
				//CR350-two new DTC codes added for gas genset CR for model G125NG
				//CR350.SN
				if (Mode.equalsIgnoreCase("Display")) {
					int ppmShutdownIndex = alertMap.indexOf("14719");
					if (ppmShutdownIndex != -1)
						filteralertMap += alertMap.substring(ppmShutdownIndex - 3, ppmShutdownIndex + 11);
					
					int ppmWarningIndex = alertMap.indexOf("15131");
					if (ppmShutdownIndex != -1)
						filteralertMap += alertMap.substring(ppmWarningIndex - 3, ppmWarningIndex + 11);
					
					filteralertMap +="}";
					
				}else{
					filteralertMap +="}";
				}
				iLogger.info("filteralertMap ::" + filteralertMap);
				//CR350.EN
				//alertMapDetails=new Gson().fromJson(alertMap, new TypeToken<HashMap<String, Object>>() {}.getType());
				alertMapDetails=new Gson().fromJson(filteralertMap, new TypeToken<HashMap<String, Object>>() {}.getType());
				roleAlertMap.put(roleNAme, alertMapDetails);

			}

			iLogger.info("roleAlertMap ::"+roleAlertMap.size());

			statement1 = prodConnection.createStatement();
			
			if(loginId!=null){
				selectQuery="select c.Role_ID from contact c where c.Contact_ID='"+loginId+"' ";	
			}
			else if(loginTenancyId!=0){
				
				//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
				List<Integer> tenancyIdList=new ArrayList<Integer>();
				tenancyIdList.add(loginTenancyId);
				
				tenancyIdList.addAll(new DateUtil().getLinkedTenancyListForTheTenancy(tenancyIdList));
				
				ListToStringConversion conversion = new ListToStringConversion();
				String tenancyIdListString = conversion.getIntegerListString(tenancyIdList).toString();
				System.out.println(tenancyIdListString);
				selectQuery="select c.Role_ID from contact c, account_contact ac, account_tenancy at where at.Tenancy_ID in ("+tenancyIdListString+") and " +
						"at.Account_ID=ac.Account_ID and ac.Contact_ID=c.Contact_ID";
			}
			else{
				return alertCodeList;
			}


			rs1 = statement1.executeQuery(selectQuery);
			rs1.setFetchSize(1);

			while(rs1.next()){
				roleId=String.valueOf(rs1.getInt("Role_ID"));
			}



			iLogger.info("roleId :"+roleId);

			if(roleAlertMap!=null && roleAlertMap.size()>0){

				HashMap<String,String> roleMap=roleAlertMap.get(roleId);

				if(roleMap!=null && roleMap.size()>0){

					for(Map.Entry pair: roleMap.entrySet()){

						if(String.valueOf(pair.getValue()).equalsIgnoreCase("1")){

							alertCodeList.add(String.valueOf(pair.getKey()));

						}

					}


				}

			}
		}

		catch (SQLException e) {

			e.printStackTrace();
			fLogger.fatal("SQL Exception in fetching data from mysql::"+e.getMessage());
		} 

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception in fetching data from mysql::"+e.getMessage());
		}

		finally {
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			if(rs1!=null)
				try {
					rs1.close();
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

			if(statement1!=null)
				try {
					statement1.close();
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
		return alertCodeList;

	}
	
	
	public List<Integer> getEventIdListForAlertCodes(StringBuilder alertCodeList){
		
		List<Integer> eventIdList=new ArrayList<Integer>();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		String selectQuery=null;

		try{

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			
			selectQuery="select b.Event_ID from business_event b where b.Alert_Code in("+alertCodeList+")";
			rs = statement.executeQuery(selectQuery);
			
			while(rs.next()){
				eventIdList.add(rs.getInt("Event_ID"));	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception in fetching data from mysql::"+e.getMessage());
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
		
		return eventIdList;
		
	}

	public List<String> getAssetGroupIdList(StringBuilder assetGroupIds){
	
	List<String> assetGroupIdList=new ArrayList<String>();
	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;
	

	Connection prodConnection = null;
	Statement statement = null;
	ResultSet rs = null;
	
	String selectQuery=null;

	try{

		ConnectMySQL connMySql = new ConnectMySQL();
		prodConnection = connMySql.getConnection();
		statement = prodConnection.createStatement();
		
		selectQuery="select ag.asset_grp_code from asset_group_profile ag where ag.asset_grp_id in("+assetGroupIds+")";
		rs = statement.executeQuery(selectQuery);
		
		while(rs.next()){
			assetGroupIdList.add(rs.getString("asset_grp_code"));	
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
		fLogger.fatal("Exception in fetching data from mysql::"+e.getMessage());
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
	
	return assetGroupIdList;
	
}

public List<String> getAssetTypeIdList(StringBuilder assetTypeIds){
		
		List<String> assetTypeIdList=new ArrayList<String>();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		String selectQuery=null;

		try{

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			
			selectQuery="select at.Asset_Type_Code from asset_type at where at.Asset_Type_ID in("+assetTypeIds+")";
			rs = statement.executeQuery(selectQuery);
			
			while(rs.next()){
				assetTypeIdList.add(rs.getString("Asset_Type_Code"));	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception in fetching data from mysql::"+e.getMessage());
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
		
		return assetTypeIdList;
		
	}
//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
public String getAccountListForTheTenancy(List<Integer> tenancyIdList){
	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;
	Session session = HibernateUtil.getSessionFactory().openSession();
	
	List<Integer> accountIdList = new LinkedList<Integer>();
	
	ListToStringConversion conversion = new ListToStringConversion();
	String accountIdListAsString=null;
	
	try{
	
		String tenancyIdListString = conversion.getIntegerListString(tenancyIdList).toString();
		
		if(session==null || !session.isOpen() || session.isDirty())
		{
			session = HibernateUtil.getSessionFactory().openSession();
		}
	
	Query accountQ = session.createQuery("select a.account_id from AccountTenancyMapping a, AccountEntity b where b.status=true and a.account_id=b.account_id " +
			"and b.mappingCode in (select c.mappingCode from AccountEntity c where c.status=true and c.account_id in (select account_id from AccountTenancyMapping where tenancy_id in ("+tenancyIdListString+")))");
	
	Iterator accItr = accountQ.list().iterator();
	while(accItr.hasNext())
	{
		AccountEntity account = (AccountEntity)accItr.next();
		accountIdList.add(account.getAccount_id());
	}
	

	if(session!=null && session.isOpen())
	{
		session.close();
	}
	
	

	accountIdListAsString = conversion.getIntegerListString(accountIdList).toString();
	}
	catch(Exception e){
		e.printStackTrace();
		fLogger.fatal("Exception in fetching data from mysql::"+e.getMessage());
	}
	finally{
		if(session!=null && session.isOpen())
		{
			session.close();
		}
	}

	
	return accountIdListAsString;
}

public String getAccountCodeListForTheTenancy(List<Integer> tenancyIdList){
	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;
	Session session = HibernateUtil.getSessionFactory().openSession();
	
	List<String> accountCodeList = new LinkedList<String>();
	
	ListToStringConversion conversion = new ListToStringConversion();
	String accountCodeListAsString=null;
	
	try{
		
		String tenancyIdListString = conversion.getIntegerListString(tenancyIdList).toString();
	

		
		if(session==null || !session.isOpen() || session.isDirty())
		{
			session = HibernateUtil.getSessionFactory().openSession();
		}
	
	Query accountQ = session.createQuery("select a.account_id from AccountTenancyMapping a, AccountEntity b where b.status=true and a.account_id=b.account_id " +
			"and b.mappingCode in (select c.mappingCode from AccountEntity c where c.status=true and c.account_id in (select account_id from AccountTenancyMapping where tenancy_id in ("+tenancyIdListString+")))");
	
	Iterator accItr = accountQ.list().iterator();
	while(accItr.hasNext())
	{
		AccountEntity account = (AccountEntity)accItr.next();
		accountCodeList.add(account.getAccountCode());
	}
	

	if(session!=null && session.isOpen())
	{
		session.close();
	}
	
	

	 accountCodeListAsString = conversion.getStringList(accountCodeList).toString();
	}
	catch(Exception e){
		e.printStackTrace();
		fLogger.fatal("Exception in fetching data from mysql::"+e.getMessage());
	}
	finally{
		if(session!=null && session.isOpen())
		{
			session.close();
		}
	}

	
	return accountCodeListAsString;
}


public List<Integer> getLinkedTenancyListForTheTenancy(List<Integer> tenancyIdList){
	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;
	Session session = HibernateUtil.getSessionFactory().openSession();
	
	List<Integer> updatedTenancyIdList = new LinkedList<Integer>();
	
	
	try{
		
		ListToStringConversion conversion = new ListToStringConversion();
		String tenancyIdListString = conversion.getIntegerListString(tenancyIdList).toString();
	
	
		
		if(session==null || !session.isOpen() || session.isDirty())
		{
			session = HibernateUtil.getSessionFactory().openSession();
		}
	
	Query accountQ = session.createQuery("select a.tenancy_id from AccountTenancyMapping a, AccountEntity b where b.status=true and a.account_id=b.account_id " +
			"and b.mappingCode in (select c.mappingCode from AccountEntity c where c.status=true and c.account_id in (select account_id from AccountTenancyMapping where tenancy_id in ("+tenancyIdListString+")))");
	
	Iterator accItr = accountQ.list().iterator();
	while(accItr.hasNext())
	{
		TenancyEntity tenancy = (TenancyEntity)accItr.next();
		updatedTenancyIdList.add(tenancy.getTenancy_id());
	}
	

	if(session!=null && session.isOpen())
	{
		session.close();
	}
	
	

	
	}
	catch(Exception e){
		e.printStackTrace();
		fLogger.fatal("Exception in fetching data from mysql::"+e.getMessage());
	}
	finally{
		if(session!=null && session.isOpen())
		{
			session.close();
		}
	}

	
	return updatedTenancyIdList;
}
public String getMOSPDynamicTable(String txnKey, Date TxnDate){
	 
	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;


	Properties prop=null;
	try	{
		prop= StaticProperties.getConfProperty();
	}
	catch(Exception e) {
		fLogger.fatal(txnKey+":AMS:DAL:TAssetMOSPMonData-getTAssetMOSPMonData"+"Error in intializing property File :"+e.getMessage());
	}

	String TAssetMonTable = prop.getProperty("default_TAssetMOSPMonData_TableKey");
	String jcbTAssetMonTableFormat = prop.getProperty("JCB_TAssetMOSPMonData_TableKey");

	Calendar cal = Calendar.getInstance();
	int currYear=cal.get(Calendar.YEAR);
	int currentWeekNo = cal.get(Calendar.WEEK_OF_YEAR);

	int transactionWeekNo = 0;
	int transactionYear=0;

	if(TxnDate!=null){
		cal.setTime(TxnDate);
		transactionWeekNo = cal.get(Calendar.WEEK_OF_YEAR);
		transactionYear = cal.get(Calendar.YEAR);
		int noofweeksintransactionYear=cal.getActualMaximum(Calendar.WEEK_OF_YEAR);

		if(currYear!=transactionYear){
			currentWeekNo =noofweeksintransactionYear+currentWeekNo;
		}
		Calendar futurecal = Calendar.getInstance();
		futurecal.add(Calendar.DAY_OF_YEAR, 6);
		int futureYear=futurecal.get(Calendar.YEAR);
		if(transactionWeekNo==1 && futureYear==transactionYear+1){
			transactionWeekNo=noofweeksintransactionYear;
		}
		if(currentWeekNo - transactionWeekNo < 8){
			TAssetMonTable = jcbTAssetMonTableFormat.replace("week", transactionWeekNo+"");
			TAssetMonTable =TAssetMonTable.replace("year", transactionYear+"");
		}
	}
	return TAssetMonTable;
}
}
