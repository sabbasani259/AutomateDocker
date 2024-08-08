/*package remote.wise.businessobject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;

public class Sample {
	public static void main(String args[]) throws ParseException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, DecoderException
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		
		String date="2017-12-29";
		
		cal.setTime(sdf1.parse(date));
		
		
		Timestamp maxOlapDae = new Timestamp(cal.getTime().getTime());
		
        String date1="2017-12-31";
		
		cal.setTime(sdf1.parse(date1));
		
		Timestamp nextday = new Timestamp(cal.getTime().getTime());
		
		List<String> dynamicAMHTables=getAMHTablesPerVIN_N_OLAPDate("HAR3DXSSA01875094", maxOlapDae, 0, nextday);
		String TAssetMonTable=null;
		String jcbTAssetMonTableFormat="TAssetMonData_JCB_week_year";
		String date="2018-12-31";
		
		String date1="2019-01-01";
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		
		Date TxnDate=sdf1.parse(date);
		Calendar cal = Calendar.getInstance();
		
		Date TxnDate1=sdf1.parse(date1);
		
		cal.setTime(TxnDate1);
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
			if(currentWeekNo - transactionWeekNo < 8){
				TAssetMonTable = jcbTAssetMonTableFormat.replace("week", transactionWeekNo+"");
				TAssetMonTable =TAssetMonTable.replace("year", transactionYear+"");

			}
		}
		
		System.out.println("TAssetMonTable:"+TAssetMonTable);
		
		
		
		String value="17_0_0_0_MinuteConvertor_hello$GPSFixRepCoord$GPSBounceRepCoord";
		
		value=value.replace("$GPSFixRepCoord$GPSBounceRepCoord", "").replaceAll("\\s","").trim();
		
		System.out.println(value);
		
		String a="HII Roopa ";
		
		System.out.println(a);
		
		a=a.replaceAll("\\s","").trim();
		
		System.out.println(a);
		
		Timestamp currentTime = new Timestamp(new Date().getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String uniqueTime=sdf.format(currentTime).toString().replaceAll("-","").replaceAll(":","").replaceAll("\\s","");
		
		String uniqueUserID="deve0011"+uniqueTime;
		
		byte[] encodedBytes = Base64.encodeBase64(uniqueUserID.getBytes());
		String uniqueKey= new String(encodedBytes);
		
		System.out.println("encode:"+uniqueKey);
		
		byte[] encodedBytes1 = Base64.decodeBase64(uniqueKey.getBytes());
		String uniqueKey1= new String(encodedBytes1).replaceAll(uniqueTime, "").replaceAll("\\s","");
		
		System.out.println("decode:"+uniqueKey1);
		
		System.out.println("contact_Id="+ new String(encodedBytes1).replaceAll("[0-9]", "").replaceAll("\\s",""));
		
	
		    String md5 = null;
		    
		        String orig="336ca0d4b87b2a6ef08aa9c4dbe09e6c";
                String compare="wipro2014";
		    try{
		        MessageDigest md = MessageDigest.getInstance("MD5");
		        md.update(compare.getBytes());
		        byte[] digest = md.digest();
		        md5 = new BigInteger(1, digest).toString(16);

		        if(md5.equals(orig)){
		        System.out.println("true");
		        }
		        else{
		        	System.out.println("false");	
		        }

		    } catch (NoSuchAlgorithmException e) {
		    	e.printStackTrace();
		       
		    }
		    
				
		 
		    final Cipher decryptCipher = Cipher.getInstance("AES");	        				
			decryptCipher.init(Cipher.DECRYPT_MODE, generateMySQLAESKey("9036384317", "UTF-8"));
			System.out.println(new String(decryptCipher.doFinal(Hex.decodeHex("1b5fdedc1ccf071ccb5e65b3a2d21c4f".toCharArray()))));
	 
			// Encrypt
			final Cipher encryptCipher = Cipher.getInstance("AES");	        				
			encryptCipher.init(Cipher.ENCRYPT_MODE, generateMySQLAESKey("9036384317", "UTF-8"));		
			System.out.println(new String(Hex.encodeHex(encryptCipher.doFinal("wipro2014".getBytes("UTF-8")))));
		
		
		
		int max = 2000;
		int min = 1500;
		int rand = min+(int)(Math.random()*((max-min)+1));
		
		System.out.println("rand :"+rand);
		
		 int [] delays = {1000, 3000, 5000};
		 
		 Random random = new Random();
		 int randomInt = random.nextInt(delays.length);
		 
		 System.out.println("rand1 :"+delays[randomInt]);
		 
		String address="Scholars' Avenue, Kharagpur, Paschim Medinipur, West Bengal, 721301";
		
		System.out.println(address);
		
		if(address.contains("'")){
			address=address.replace("'", "");	
		}
		System.out.println(address);
		
		Date currentDate = new Date();
		Calendar cal = Calendar.getInstance(); 
        cal.setTime(currentDate); 
        int week = Calendar.WEEK_OF_YEAR;
       // System.out.println("Week number:"+week);
        * 
        * 

		
		HashSet set = new HashSet<E>();
		set.
		String a="20150520112000";
		Double d = Double.parseDouble(a);
		System.out.println(d.doubleValue());
	
		
		String loginID = "deve0011";
		LinkedList tenancyIDList  = new LinkedList();
		tenancyIDList.add(201);
		String Serial_Number = null;
		AssetDetailsBO bo = new AssetDetailsBO();
		List<RolledOffMachinesImpl> rollOffList =   bo.getRolledOffMachines(loginID, tenancyIDList, Serial_Number, null, null);
		
		System.out.println("rollOffList : "+rollOffList.size());
		if(rollOffList!=null){
			Iterator rollOffItr = rollOffList.iterator();
			while(rollOffItr.hasNext()){
				System.out.println(((RolledOffMachinesImpl)rollOffItr.next()).getSerialNumber());
			}
		}
		ConnectMySQL connMySql = new ConnectMySQL();
		Connection prodConnection = null;
		prodConnection = connMySql.getConnection();
		
		try {
			System.out.println(prodConnection.getAutoCommit()+"");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ReportDetailsBO reportBO = new ReportDetailsBO();
		reportBO.setServiceDetails();
		Calendar calendar1 = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//dateStr.setLenient(false);
				String strCurrDate = currDate;

		Date date3 = dateFormat.parse(strCurrDate);
		Timestamp currTimestamp=new Timestamp(date3.getTime());
		//				Date date4 = dateFormat1.parse(currDate);
		calendar1.setTime(date3);
		// get previous day
		calendar1.add(Calendar.DAY_OF_YEAR, -1);

		String prevDate = dateFormat.format(calendar1.getTime());
		// data will be take from previous day 6:30 to today 6:20
		prevDate = prevDate + " 18:30:00";
		strCurrDate = strCurrDate + " 18:20:00";
		
		
		
		System.out.println(prevDate+" currnetDate "+strCurrDate);
		 Calendar cal = Calendar.getInstance();
		 Date st = dateStr.parse(prevDate);
		 System.out.println(st);
		 cal.setTime(dateStr.parse(prevDate));
		 
		 Timestamp txnTimestamp = new Timestamp(cal.getTimeInMillis());
		 HashMap<String, String> dynamicTables = new DateUtil().getCurrentWeekDifference(txnTimestamp);
		 
		 System.out.println(dynamicTables.get("AMH")); 
		 
		 //Calendar cal = Calendar.getInstance();
		 cal.setTime(dateStr.parse(strCurrDate));
		 
		 Timestamp txnTimestamp1 = new Timestamp(cal.getTimeInMillis());
		 HashMap<String, String> dynamicTables2 = new DateUtil().getCurrentWeekDifference(txnTimestamp1);
		 System.out.println(dynamicTables2.get("AMH")); 
		 
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;
		
		ConnectMySQL connMySql = new ConnectMySQL();
		prodConnection = connMySql.getConnection();
		try {
			//statement = prodConnection.createStatement();
		
		//PreparedStatement ps = null;
		Query query = session2.createQuery(" from ServiceDetailsReportEntity where serialNumber='"+assetID+"'");
		Iterator itr = query.list().iterator();
		if(session !=null){
			session.close();
		}
		 DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(dateStr.parse("2016-05-24 00:00:00"));
		 
		 Timestamp txnTimestamp = new Timestamp(cal.getTimeInMillis());
		String insertStatement = "insert into service_details_report values(?,?,?,?,?,?,?,?,?,?,?)";
		
		String updateStatement = "UPDATE service_details_report "+   //Serial_Number` = {Serial_Number: },
		"SET "+
		"Service_Schedule_Name = ?,"+
		"Last_Service_Name = ?,"+
		"Last_Service_Date =?,"+
		"Last_Service_Hours = ?,"+
		"Next_Service_Name = ?,"+
		"Next_Service_Date = ?,"+
		"Next_Service_Hours = ?,"+
		"Total_Engine_Hours = ?,"+
		"Last_Updated = ?,"+
		"Event_ID = ? "+
		"WHERE Serial_Number = ?";
		
		ps = prodConnection.prepareStatement(insertStatement);
		ps.setString(11,"HAR3DXSSA01885589");
		ps.setString(1, "Backhoe - Z3IT - JCBEngine");
		ps.setString(2, "4th Service");
		ps.setTimestamp(3, txnTimestamp);
		ps.setString(4, "1736.8");
		ps.setString(5, "6th Service");
		cal.setTime(dateStr.parse("2016-07-24 00:00:00"));
		 
		 Timestamp txnTimestamp1 = new Timestamp(cal.getTimeInMillis());
		ps.setTimestamp(6, txnTimestamp1);
		ps.setString(7, "2500");
		ps.setString(8, "2018.8");
		cal = Calendar.getInstance();
		ps.setTimestamp(9,  new Timestamp(cal.getTimeInMillis()));
		ps.setInt(10, 3);
		ps.addBatch();
		ps.executeBatch();
		ps.clearBatch();
		ps.setString(1,"HAR3DXSSA01885589");
		ps.setString(2, "Backhoe - Z3IT - JCBEngine");
		ps.setString(3, "4th Service");
		ps.setTimestamp(4, txnTimestamp);
		ps.setString(5, "1736.8");
		ps.setString(6, "5th Service");
		cal.setTime(dateStr.parse("2016-07-24 00:00:00"));
		 
		 Timestamp txnTimestamp1 = new Timestamp(cal.getTimeInMillis());
		ps.setTimestamp(7, txnTimestamp1);
		ps.setString(8, "2000");
		ps.setString(9, "2018.8");
		cal = Calendar.getInstance();
		ps.setTimestamp(10,  new Timestamp(cal.getTimeInMillis()));
		ps.setInt(11, 3);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashMap asset2SegID = new HashMap();
		List parametreIDList = new LinkedList();
		parametreIDList.add(4);
		AssetEntity asset = null;
		//for(String assetID : serialNumList){
			asset = new AssetDetailsBO().getAssetEntity("HAR3DXSSC01875358");
			asset2SegID.put(asset.getSerial_number().getSerialNumber(), asset.getSegmentId());
		List<AssetMonitoringParametersDAO> responseList = new DynamicAMH_DAL().getMaxTxsInTxnRange(asset2SegID,"2016-07-14", parametreIDList);
		Iterator it = responseList.iterator();
		List<String> tsList = new LinkedList<String>();
		while(it.hasNext()){
			//result = (Object[])itr.next();
			
			AssetMonitoringParametersDAO daoObject = (AssetMonitoringParametersDAO) it.next();
			
			//AssetMonitoringParametersDAO daoObject = new AssetMonitoringParametersDAO();
			tsList.add(daoObject.getTransactionTS().toString());
			//System.out.println(rs.getTimestamp("Transaction_Timestamp"));
			//AMPList.add(daoObject);
		}
		List<AssetMonitoringParametersDAO> amhDaoList = new DynamicAMH_DAL().getAMhDataOnTxnTS(asset2SegID,tsList, parametreIDList);
		
		System.out.println(amhDaoList.size());
		
		
		//try {
			
		List tenancyList = new LinkedList();
		tenancyList.add(201);
		List<RolledOffMachinesImpl> implList = new AssetDetailsBO().getRolledOffMachines("deve0011", tenancyList, null, null, null);
			System.out.println(implList.size());
		AssetEntity asset = new AssetEntity();
		asset.setSerial_number(new AssetControlUnitEntity("HAR3DXSSH01880542"));
		
		List<AssetEventEntity> eventList = new EventDetailsBO().checkDuplicateAlert("HAR3DXSSH01880542",0,1,1);
		Iterator itr = eventList.iterator();
		while(itr.hasNext()){
			
			AssetEventEntity entity = (AssetEventEntity) itr.next();
			
			System.out.println(entity.getEventTypeId().getEventTypeId());
		System.out.println(entity.getAssetEventId());
		
		}

		Session session = new HibernateUtil().getSessionFactory().openSession();
		try{
			session.getTransaction().begin();
			
			int tenancyId = 24744;
			String query = "select a from AssetEntity a, AccountTenancyMapping b, TenancyEntity c " +
                    " where a.primary_owner_id = b.account_id and b.tenancy_id=c.tenancy_id" +
                    " and c.tenancyCode = (select d.tenancyCode from TenancyEntity d where d.tenancy_id ='24744') ";
			
			String query1 = "select a from AssetEntity a, AccountTenancyMapping b, TenancyEntity c " +
            " where a.primary_owner_id = b.account_id and b.tenancy_id=c.tenancy_id" +
            " and c.tenancyCode = (select d.tenancyCode from TenancyEntity d where d.tenancy_id ='"+tenancyId+"') ";

			Query hqlQuery = session.createQuery(query);
			
			Iterator itr1 = hqlQuery.list().iterator();
			System.out.println(itr1.hasNext());
			
			hqlQuery = session.createQuery(query1);
			
			Iterator itr2 = hqlQuery.list().iterator();
			System.out.println(itr2.hasNext());
			
			Query hqlQuery3 = session.createQuery("select a from AssetEntity a, AccountTenancyMapping b, TenancyEntity c " +
		            " where a.primary_owner_id = b.account_id and b.tenancy_id=c.tenancy_id" +
		            " and c.tenancyCode = (select d.tenancyCode from TenancyEntity d where d.tenancy_id ='"+tenancyId+"') ");
			
			Iterator itr3 = hqlQuery3.list().iterator();
			System.out.println(itr3.hasNext());

		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println(responseList.size());
		
		List tenancyList = new LinkedList();
		tenancyList.add(233);
		List<String> serNumList = new LinkedList<String>();
		serNumList.add("HAR3DXSSC01875358");
		List<UtilizationDetailReportImpl> implList =new ReportDetailsBO().getFleetUtilizationDetails(
				"shiv00761", null, null, null, null, "Today", serNumList);

		System.out.println(implList.size());
		for (int i = 0; i < implList.size(); i++) {
			//UtilizationDetailServiceImpl response = new UtilizationDetailServiceImpl();
			System.out.println(implList.get(i).getSerialNumber());
			response.setSerialNumber(implList.get(i).getSerialNumber());
			response.setTimeMachineStatusMap(implList.get(i)
					.getTimeMachineStatusMap());
			System.out.println(implList.get(i).getDateInString());
			
			Iterator it = implList.get(i)
					.getTimeMachineStatusMap().entrySet().iterator();
			while(it.hasNext()){
				Map.Entry entry = (Entry) it.next();
				System.out.println(entry.getKey());
				int seg_ID = (Integer) entry.getValue();
			}
			response.setTimeDuration(implList.get(i).getDateInString());					

			implResponseList.add(response);
		
	}
	
	public static SecretKeySpec generateMySQLAESKey(final String key, final String encoding) {
		try {
			final byte[] finalKey = new byte[16];
			int i = 0;
			for(byte b : key.getBytes(encoding))
				finalKey[i++%16] ^= b;			
			return new SecretKeySpec(finalKey, "AES");
		} catch(UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static List<String> getAMHTablesPerVIN_N_OLAPDate(String serial_Number,Timestamp maxOlapDt,int segID, Timestamp nextday){
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		List<String> dynamicAMHTables = new LinkedList<String>();
		Calendar cal = Calendar.getInstance();
		int weekNo = cal.get(Calendar.WEEK_OF_YEAR);
		
		int currweekNo = cal.get(Calendar.WEEK_OF_YEAR);
		
		int currYear=cal.get(Calendar.YEAR);
		
		cal.setTime(maxOlapDt);
		
		int lastolapdateweek=cal.get(Calendar.WEEK_OF_YEAR);;
		int transactionYear = cal.get(Calendar.YEAR);
		int noofweeksintransactionYear=cal.getActualMaximum(Calendar.WEEK_OF_YEAR);
		
		//DF20170522 @Roopa handling year change scenario where txndate is diff year and curr year is different
		
		if(currYear!=transactionYear){

			weekNo =noofweeksintransactionYear+weekNo;

		}
		
		String amhTable = null;
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null; 
		
		
		Timestamp ts = new Timestamp(cal.getTimeInMillis());

		Properties prop = CommonUtil.getDepEnvProperties();
		String JCB_AMH_TableKey = prop.getProperty("JCB_TAMD_TableKey");
		
		//DF20170512 @Roopa Taking maxolapdate for the given VIn and iterating from that maxolapdate table onwards to the current week table.
		
		//amhTable = JCB_AMH_TableKey.replace("week", weekNo+"");
		
		
		amhTable = JCB_AMH_TableKey.replace("week", lastolapdateweek+"");
		
		
        amhTable =amhTable.replace("year", transactionYear+"");
        boolean recordNotFound = true;
        
       // dynamicAMHTables.add("TAssetMonData_JCB_17_2017");
   	connection = connFactory.getDatalakeConnection_3309();
		try {
			statement = connection.createStatement();
			
			 for(int i=lastolapdateweek;i<=weekNo;i++){
		        	try{
		        	
		        	//String query = "select * from "+amhTable +" where Segment_ID_TxnDate = "+segID+" and Serial_Number = '"+serial_Number+"' and Last_Updated_Timestamp >= '"+maxOlapDt+"' and Last_Updated_Timestamp < Date('"+nextday+"')";
		        	
		        		
		        		//Df20170601 @Roopa ETL is scheduled after 12 so changing the query to not take the update records for maxolapdate condition
		        		String query = "select * from "+amhTable +" where Segment_ID_TxnDate = "+segID+" and Serial_Number = '"+serial_Number+"' and Date(Last_Updated_Timestamp) > Date('"+maxOlapDt+"') and Date(Last_Updated_Timestamp) < Date('"+nextday+"')";
		        		
		        		rs = statement.executeQuery(query);
		        	if(rs.next()){
		        		recordNotFound = false;
		        		dynamicAMHTables.add(amhTable);
		        	}
		        	//amhTable = JCB_AMH_TableKey.replace("week", (weekNo-i)+"");
		        	
		        	if((currYear==transactionYear) || i<weekNo){
		        	
		        	amhTable = JCB_AMH_TableKey.replace("week", (i+1)+"");
		        	
		        	amhTable =amhTable.replace("year", transactionYear+"");
		        	}
		        	else{
		        		amhTable = JCB_AMH_TableKey.replace("week", currweekNo+"");
			        	
			        	amhTable =amhTable.replace("year", currYear+"");
		        		
		        	}
		        	
		        	}catch(SQLException se){
		        
		        		if(se instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException)
		        		{
		        			dynamicAMHTables.remove(amhTable);
		        			return dynamicAMHTables;
		        		}
		        		else
		        			return null;
		        	}
		        }
		
        for(int i=1;i<=8;i++){
        	try{
        	
        	String query = "select * from "+amhTable +" where Segment_ID_TxnDate = "+segID+" and Serial_Number = '"+serial_Number+"' and Last_Updated_Timestamp >= '"+maxOlapDt+"' and Last_Updated_Timestamp < Date('"+nextday+"')";
        	rs = statement.executeQuery(query);
        	if(rs.next()){
        		recordNotFound = false;
        		dynamicAMHTables.add(amhTable);
        	}
        	amhTable = JCB_AMH_TableKey.replace("week", (weekNo-i)+"");
        	amhTable =amhTable.replace("year", transactionYear+"");
        	}catch(SQLException se){
        
        		if(se instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException)
        		{
        			dynamicAMHTables.remove(amhTable);
        			return dynamicAMHTables;
        		}
        		else
        			return null;
        	}
        }
		} catch (SQLException e) {
			//fLogger.fatal("getAMHTablesPerVIN_N_OLAPDate exception "+e.getMessage());
			e.printStackTrace();
		}
		finally{
			try {
				
				if(rs != null && !rs.isClosed()){
					rs.close();
					
				}
				
				if(statement != null && !statement.isClosed()){
					statement.close();
					
				}
				if(connection!= null && !connection.isClosed()){
					connection.close();
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				//fLogger.fatal("getAMHTablesPerVIN_N_OLAPDate exception "+e.getMessage());
				e.printStackTrace();
			}
			
		}
		if(recordNotFound)
			return dynamicAMHTables;
		return dynamicAMHTables;
	}
}
*/