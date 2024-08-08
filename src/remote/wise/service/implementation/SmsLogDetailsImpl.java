//CR393 - 20230201 - Prasad - SmsLogsDetails
package remote.wise.service.implementation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;
import org.hibernate.Query;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class SmsLogDetailsImpl {
	
	public List<HashMap<String,String>> getSmslogdetails(String vin,String startdate,String enddate){
	Logger infoLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	//DF20190104 :: MANI :: Smslog details from new db, post 2019-01-08
	HashMap<String,String> smsLog = null;
	List<HashMap<String,String>> smsList=new LinkedList<HashMap<String,String>>();
	Session session=null;
	Date StartDate = null;
	Date EndDate = null;
	Date rangeDate = null;
	String comparedate= "2019-01-08";
	Connection prodConnection = null,prodConnection1 = null;
	Statement statement = null,statement1 = null;
	ConnectMySQL connMySql = new ConnectMySQL();
	String Query=null;
	ResultSet rs=null,rs1=null;
	AssetEntity asset = null;
	try{
		StartDate=format.parse(startdate);
		EndDate=format.parse(enddate);
		rangeDate=format.parse(comparedate);
		session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		//DF20190129 :: MANI :: adding the machine num logic, for 7 digits vin better performance
		vin=vin.replaceFirst("^0+(?!$)", "");
		if(vin.length()==7)
		{
			Query machNumQ = session.createQuery(" from AssetEntity where machineNumber='"+vin+"'");
			Iterator machNumItr = machNumQ.list().iterator();
			if(machNumItr.hasNext())
			{
				asset = (AssetEntity) machNumItr.next();
			}
			vin=asset.getSerial_number().getSerialNumber();
		}
		String pattern = "yyyy-MM-dd";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date1 = simpleDateFormat.format(StartDate);    
		String date2 = simpleDateFormat.format(EndDate); 
		String startDateSMSSentMonthToString = date1.toString();
		String endDateSMSSentMonthToString = date2.toString();
		String startDateSMSSentMonth = startDateSMSSentMonthToString.replace("-", "").substring(0,6);
		String endDateSMSSentMonth = endDateSMSSentMonthToString.replace("-", "").substring(0, 6); 
	if(StartDate.after(rangeDate)&& EndDate.after(rangeDate))
	{
		
		Query = "select SerialNumber, MobileNumber,SMSContent,SMSSentTime from DC_SMSAlertLogger where SerialNumber =\'"
				+ vin
				+ "\' and DATE(SMSSentTime)>='"
				+ startdate
				+ "' and DATE(SMSSentTime)<='" + enddate + "' and SMSSentMonth in ("+startDateSMSSentMonth+","+endDateSMSSentMonth+") order by SMSSentTime ";
		infoLogger.info("Webservice /LogDetailsService/getsmslogdetails query :"+Query);
		//For SIT
//		prodConnection = connMySql.getConnection();
		//For PROD
		prodConnection = connMySql.getConnection_data_comm();
		statement = prodConnection.createStatement();
		rs=statement.executeQuery(Query);
		try{
		while(rs.next()){
			String serialnum =(String) rs.getString(1);
			String mobilenum =  (String) rs.getString(2);
			String smsbody=(String) rs.getString(3);
		
			Timestamp timestamp=(Timestamp) rs.getTimestamp(4);
			String smstime=timestamp.toString();
			smsLog = new HashMap<String,String>();
			smsLog.put("serialnum", serialnum);
			smsLog.put("mobilenum",mobilenum);
			smsLog.put("smsbody", smsbody);
			smsLog.put("smstime",smstime);
			smsList.add(smsLog);
		}
		}catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		}
		
	}
	else if(StartDate.before(rangeDate)&& EndDate.before(rangeDate))
	{
		/*session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();*/
		String selectQuery="";

		selectQuery = "select serialNumber, mobileNumber,smsBody,smsSentTime from SmsLogDetailsEntity where serialNumber =\'"
				+ vin
				+ "\' and DATE(smsSentTime)>='"
				+ startdate
				+ "' and DATE(smsSentTime)<='" + enddate + "' and SMSSentMonth in ("+startDateSMSSentMonth+","+endDateSMSSentMonth+")";
		infoLogger.info("Webservice /LogDetailsService/getsmslogdetails query :"+selectQuery);
		long startTime = System.currentTimeMillis();
		Query query = session.createQuery(selectQuery);
		

		try{
			Iterator iterator = query.list().iterator();
			long endTime = System.currentTimeMillis();
			infoLogger.info("Webservice /LogDetailsService/getsmslogdetails query Execution Time in ms:"+(endTime-startTime));
			Object[] resultQ = null;
			while(iterator.hasNext()){
				resultQ = (Object[]) iterator.next();
				String serialnum = (String) resultQ[0];
				String mobilenum =  (String) resultQ[1];
				String smsbody=(String) resultQ[2];
			
				Timestamp timestamp=(Timestamp) resultQ[3];
				String smstime=timestamp.toString();
				smsLog = new HashMap<String,String>();
				smsLog.put("serialnum", serialnum);
				smsLog.put("mobilenum",mobilenum);
				smsLog.put("smsbody", smsbody);
				smsLog.put("smstime",smstime);
				smsList.add(smsLog);
			}

		}catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		}
	}
	else if(StartDate.before(rangeDate)&& EndDate.after(rangeDate))
	{
		//DATA_COMM schema
		
		Query = "select SerialNumber, MobileNumber,SMSContent,SMSSentTime from DC_SMSAlertLogger where SerialNumber =\'"
				+ vin
				+ "\' and DATE(SMSSentTime)>='"
				+ comparedate
				+ "' and DATE(SMSSentTime)<='" + enddate + "' and SMSSentMonth in ("+startDateSMSSentMonth+","+endDateSMSSentMonth+") order by SMSSentTime ";
		infoLogger.info("Webservice /LogDetailsService/getsmslogdetails query :"+Query);
		//For SIT
		//prodConnection1 = connMySql.getConnection();
		//For PROD
		prodConnection1 = connMySql.getConnection_data_comm();
		statement1 = prodConnection1.createStatement();
		rs1=statement1.executeQuery(Query);
		try{
		while(rs1.next()){
			String serialnum =(String) rs1.getString(1);
			String mobilenum =  (String) rs1.getString(2);
			String smsbody=(String) rs1.getString(3);
		
			Timestamp timestamp=(Timestamp) rs1.getTimestamp(4);
			String smstime=timestamp.toString();
			smsLog = new HashMap<String,String>();
			smsLog.put("serialnum", serialnum);
			smsLog.put("mobilenum",mobilenum);
			smsLog.put("smsbody", smsbody);
			smsLog.put("smstime",smstime);
			smsList.add(smsLog);
		}
		}catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		}
		//WISE Schema
		/*session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();*/
		String selectQuery="";

		selectQuery = "select serialNumber, mobileNumber,smsBody,smsSentTime from SmsLogDetailsEntity where serialNumber =\'"
				+ vin
				+ "\' and DATE(smsSentTime)>='"
				+ startdate
				+ "' and DATE(smsSentTime)<='" + comparedate + "' ";
		infoLogger.info("Webservice /LogDetailsService/getsmslogdetails query :"+selectQuery);
		long startTime = System.currentTimeMillis();
		Query query = session.createQuery(selectQuery);
		

		try{
			Iterator iterator = query.list().iterator();
			long endTime = System.currentTimeMillis();
			infoLogger.info("Webservice /LogDetailsService/getsmslogdetails query Execution Time in ms:"+(endTime-startTime));
			Object[] resultQ = null;
			while(iterator.hasNext()){
				resultQ = (Object[]) iterator.next();
				String serialnum = (String) resultQ[0];
				String mobilenum =  (String) resultQ[1];
				String smsbody=(String) resultQ[2];
		
				Timestamp timestamp=(Timestamp) resultQ[3];
				String smstime=timestamp.toString();
				smsLog = new HashMap<String,String>();
				smsLog.put("serialnum", serialnum);
				smsLog.put("mobilenum",mobilenum);
				smsLog.put("smsbody", smsbody);
				smsLog.put("smstime",smstime);
				smsList.add(smsLog);
			}

		}catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		}
		
	
	}
	else
	{
/*	session = HibernateUtil.getSessionFactory().openSession();
	session.beginTransaction();*/
	String selectQuery="";

	selectQuery = "select serialNumber, mobileNumber,smsBody,smsSentTime from SmsLogDetailsEntity where serialNumber =\'"
			+ vin
			+ "\' and DATE(smsSentTime)>='"
			+ startdate
			+ "' and DATE(smsSentTime)<='" + enddate + "' and SMSSentMonth in ("+startDateSMSSentMonth+","+endDateSMSSentMonth+")";
	infoLogger.info("Webservice /LogDetailsService/getsmslogdetails query :"+selectQuery);
	long startTime = System.currentTimeMillis();
	Query query = session.createQuery(selectQuery);
	

	try{
		Iterator iterator = query.list().iterator();
		long endTime = System.currentTimeMillis();
		infoLogger.info("Webservice /LogDetailsService/getsmslogdetails query Execution Time in ms:"+(endTime-startTime));
		Object[] resultQ = null;
		while(iterator.hasNext()){
			resultQ = (Object[]) iterator.next();
			String serialnum = (String) resultQ[0];
			String mobilenum =  (String) resultQ[1];
			String smsbody=(String) resultQ[2];

			Timestamp timestamp=(Timestamp) resultQ[3];
			String smstime=timestamp.toString();
			smsLog = new HashMap<String,String>();
			smsLog.put("serialnum", serialnum);
			smsLog.put("mobilenum",mobilenum);
			smsLog.put("smsbody", smsbody);
			smsLog.put("smstime",smstime);
			smsList.add(smsLog);
		}

	}catch(Exception e){
		e.printStackTrace();
		fLogger.fatal("Exception :"+e);
	}
	}
	}catch(Exception e)
	{
		e.printStackTrace();
		fLogger.fatal("Exception occured in smslogdetailsimpl :"+e);
	}
	finally{
		
		List<String> mobileNumbers= new ArrayList<>();  //393.sn
		

		String querry = "select  c.primary_mobile_number from AssetEntity a , AccountContactMapping b, ContactEntity c  "
				+ "where a.serial_number = '"+vin +"' and  a.primary_owner_id = b.account_id and c.contact_id in (b.contact_id)";
		infoLogger.info("Webservice /LogDetailsService/getsmslogdetails query :"+querry);
		long startTime = System.currentTimeMillis();
		Query query = session.createQuery(querry);

		try{
			Iterator iterator = query.list().iterator();
			long endTime = System.currentTimeMillis();
			infoLogger.info("Webservice /LogDetailsService/getsmslogdetails query Execution Time in ms:"+(endTime-startTime));
			String resultQ = null;
			while(iterator.hasNext()){
				resultQ =  (String) iterator.next();
//				String serialnum = vin;
				String mobileNumber =  resultQ;
				mobileNumbers.add(mobileNumber);
			
			}
			infoLogger.info("Webservice /LogDetailsService/getsmslogdetails list of mobile numbers:"+mobileNumbers);
		}catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		} //393.en
	
//			Connection connection2 = null;   //393.so
//			Statement statement2 = null;
//			ResultSet rs2 = null;
//			try {
//
//				ConnectMySQL connMySql2 = new ConnectMySQL();
//				connection2 = connMySql2.getConnection();
//				statement2 = connection2.createStatement();
//				String selectQueryForMobile = "select Mobile from account where Account_ID=(select Account_ID from asset_owner_snapshot where Serial_Number in('"+vin+"') and Account_Type in ('Customer'))";
//				infoLogger.info(" constructed Query : ---> " + selectQueryForMobile);
//				rs2 = statement2.executeQuery(selectQueryForMobile);
//				while (rs2.next()) {			
//					if(rs2.getString("Mobile") != null)
//					{mobile=rs2.getString("Mobile");}
//					else{mobile="NA";}
//				}
//			}catch(Exception e){
//				e.printStackTrace();
//			}                                    //393.eo

/*	session = HibernateUtil.getSessionFactory().openSession();
	session.beginTransaction();*/
		for(int i =0 ; i< mobileNumbers.size() ; i++){ //393.sn
			String mobileNumber = mobileNumbers.get(i);
			
			String selectQuery="";

			selectQuery = "select serialNumber, mobileNumber,smsBody,smsSentTime from SmsLogDetailsEntity where Mobile_Number like '%"+mobileNumber+"%'"+" and DATE(smsSentTime)>='"
					+ startdate
					+ "' and DATE(smsSentTime)<='" + enddate + "')";  //393.en
			infoLogger.info("Webservice /LogDetailsService/getsmslogdetails query :"+selectQuery);
			long startTime4 = System.currentTimeMillis();
			Query query2 = session.createQuery(selectQuery);
			

			try{
				Iterator iterator = query2.list().iterator();
				long endTime = System.currentTimeMillis();
				infoLogger.info("Webservice /LogDetailsService/getsmslogdetails query Execution Time in ms:"+(endTime-startTime));
				Object[] resultQ = null;
				while(iterator.hasNext()){
					resultQ = (Object[]) iterator.next();
					String serialnum = vin;
					String mobilenum =  (String) resultQ[1];
					String smsbody=(String) resultQ[2];
					Timestamp timestamp=(Timestamp) resultQ[3];
					String smstime=timestamp.toString();
					
					smsLog = new HashMap<String,String>();
					smsLog.put("serialnum", serialnum);
					smsLog.put("mobilenum",mobilenum);
					smsLog.put("smsbody", smsbody);
					smsLog.put("smstime",smstime);
					smsList.add(smsLog);
				}

			}catch(Exception e){
				e.printStackTrace();
				fLogger.fatal("Exception :"+e);
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
		if(session!=null){
		if(session.getTransaction().isActive()){
			session.getTransaction().commit();
		}if(session.isOpen()){
			session.flush();
			session.close();
		}
		}
	}
	return smsList;
}
	
	public static void main(String args[]){
		
		List<HashMap<String,String>> getSmslogdetails=new SmsLogDetailsImpl().getSmslogdetails("HAR3DXS4J03066972", "2021-07-29", "2021-08-31");
		System.out.println(getSmslogdetails);
	}
	
}
