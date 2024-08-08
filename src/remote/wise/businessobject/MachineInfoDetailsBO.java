/**
 * 
 */
package remote.wise.businessobject;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetMonitoringDetailEntity;
import remote.wise.businessentity.AssetMonitoringHeaderEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.dal.DynamicAMS_DAL;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.pojo.AmsDAO;
import remote.wise.service.implementation.AuditLogDetailsImpl;
import remote.wise.service.implementation.DetailMachineImpl;
import remote.wise.service.implementation.MachineHoursReportImpl;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;

/**
 * @author sunayak
 *
 */
public class MachineInfoDetailsBO {
	/*public static WiseLogger businessError = WiseLogger.getLogger("MachineInfoDetailsBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("MachineInfoDetailsBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("MachineInfoDetailsBO:","info");*/
	/*public List<DetailMachineImpl> getMachineListInfo() {
		// TODO Auto-generated method stub
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		List<DetailMachineImpl> MachineListImpl = new LinkedList<DetailMachineImpl>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Connection con=null;
		Statement statement =null;
		try
		{
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			} 
			List<String> ownershipStartDateList = new ArrayList<String>();
			List<String> createdtransactionTimeStamp = new ArrayList<String>();
			List<String> machineHourlist = new ArrayList<String>();
			List<String> serialNumberList = new LinkedList<String>();
			List<Integer> transactionNumberList = new LinkedList<Integer>();
			ListToStringConversion conversionObj = new ListToStringConversion();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//To get the current ownershipStartDate 
			Query query1 = session
			//			.createQuery("select max(a.ownershipStartDate),c.serialNumber from AssetAccountMapping a ,AssetEntity c where c group by c.serialNumber");
			.createQuery("select max(a.ownershipStartDate),a.serialNumber from AssetAccountMapping a group by a.serialNumber");
			Iterator contactItr = query1.list().iterator();
			while (contactItr.hasNext())

			{
				//System.out.println("Inside first while");
				Object[] result = (Object[]) contactItr.next();
				if (result[0] != null) {
					Timestamp transactionTime = (Timestamp) result[0];
					String transactionTimeStamp = dateFormat.format(transactionTime);
					ownershipStartDateList.add(transactionTimeStamp);
					//infoLogger.info("ownershipStartDateList"+ownershipStartDateList);
				}
			}
			String ownershipStartDateStringList = conversionObj.getStringList(ownershipStartDateList).toString();
			iLogger.info("ownershipStartDateStringList"+ownershipStartDateStringList);

			//To get the latest packet received time
			Timestamp transactionTimestamp = null;
			String transactionTimestampString =null;
			Query query2 = session
			.createQuery("select max(a.createdTimestamp),a.serialNumber from AssetMonitoringHeaderEntity a group by a.serialNumber");
			Iterator transactionTimeItr = query2.list().iterator();
			while (transactionTimeItr.hasNext())
			{
				AssetEntity asset =null;
				//System.out.println("Inside 2nd while");
				Object[] result = (Object[]) transactionTimeItr.next();
				if (result[0] != null) {
					transactionTimestamp = (Timestamp) result[0];
					transactionTimestampString = dateFormat.format(transactionTimestamp);
					//String transactionTimestampString = transactionTimestamp.toString();
					createdtransactionTimeStamp.add(transactionTimestampString);
					infoLogger.info("createdtransactionTimeStamp"+createdtransactionTimeStamp);
				}
				if (result[1] != null) {
					asset = (AssetEntity) result[1];
					serial_num =asset.getSerial_number().getSerialNumber();
					serialNumberList.add(serial_num);
				}
			}
			String createdtransactionTimeStampStringList = conversionObj.getStringList(createdtransactionTimeStamp).toString();
			int transactionNumber =0;
			String serial_num = null;
			Query query3 = session
			.createQuery("select max(a.transactionNumber),a.serialNumber from AssetMonitoringHeaderEntity a group by a.serialNumber");
			Iterator transactionNumItr = query3.list().iterator();
			while (transactionNumItr.hasNext())
			{
				AssetEntity asset =null;
				//System.out.println("Inside 2nd while");
				Object[] result = (Object[]) transactionNumItr.next();
				if (result[0] != null) {
					transactionNumber = (Integer)result[0];
					transactionNumberList.add(transactionNumber);
					//infoLogger.info("transactionNumberList"+transactionNumberList);
				}
				if (result[1] != null) {
					asset = (AssetEntity) result[1];
					serial_num =asset.getSerial_number().getSerialNumber();
					serialNumberList.add(serial_num);
				}
			}
			String transactionNumberAsStringList = conversionObj.getIntegerListString(transactionNumberList).toString();
			iLogger.info("transactionNumberAsStringList"+transactionNumberAsStringList);
			String serialNumberListStringList = conversionObj.getStringList(serialNumberList).toString();

					String Hour ="Hour";
			String machineHour=null;
			Query last1HourPkt = session.createQuery(" select b.parameterValue,a.serialNumber from AssetMonitoringHeaderEntity a, " +
					" AssetMonitoringDetailEntity b, MonitoringParameters c " +
					" where " +
					" a.createdTimestamp in ("+createdtransactionTimeStampStringList+")" +
					" and a.transactionNumber=b.transactionNumber " +
					" and b.parameterId = c.parameterId " +
					" and c.parameterName='"+Hour+"'");
			Iterator last1HourPktItr = last1HourPkt.list().iterator();
			Object[] ResultSet = null;
			while(last1HourPktItr.hasNext())
			{
				System.out.println("Inside 3rd while");
				ResultSet = (Object[]) last1HourPktItr.next();
				machineHour = (String)ResultSet[0];
				machineHourlist.add(machineHour);
			}
			//To get the communicated machine details
			String serialNumber;
			String account_name;
			String machine_Hour;
			Timestamp CreatedTimestamp;
			String fW_Version_Number;
			Query query = session
				.createQuery(" select b.serial_number,b.dateTime,c.account_name,e.parameterValue,d.createdTimestamp ,d.fwVersionNumber from AssetAccountMapping a JOIN a.serialNumber b "
						+ " JOIN  a.accountId c ,AssetMonitoringDetailEntity e JOIN e.transactionNumber d where a.ownershipStartDate in("+ownershipStartDateStringList+")"
						+ " and d.transactionNumber in("+transactionNumberAsStringList+")"
						+ " and e.parameterId=4 "
						//+ " and d.serialNumber in("+serialNumberListStringList+")"
						+ " and d.transactionNumber=e.transactionNumber "
						+ " and a.accountId=c.account_id " 
						+ " and a.serialNumber=b.serial_number "
						+ " and a.serialNumber=d.serialNumber "
						+ " group by b.serial_number ");

				Iterator queryItr = query.list().iterator();
				Object[] ResultSet1 = null;
				while(queryItr.hasNext())
				{
					//System.out.println("Inside 4th while");
					ResultSet1 = (Object[]) queryItr.next();
					DetailMachineImpl newimplObj = new DetailMachineImpl();
					serialNumber=null;
					AssetEntity asset =null;
					AccountEntity account =null;
					AssetMonitoringDetailEntity AssetMonitoringDetail=null;
					AssetMonitoringHeaderEntity AssetMonitoringHeader=null;
					AssetControlUnitEntity AssetControlUnit =null;
					Timestamp transactionTime =null;
					if(ResultSet1[0]!=null)
					{					
						AssetControlUnit = (AssetControlUnitEntity)ResultSet1[0];
						serialNumber = AssetControlUnit.getSerialNumber();
						newimplObj.setSerialNumber(serialNumber);
						serialNumberList.add(serialNumber);
					}
					if(ResultSet1[1]!=null)
					{
						transactionTime = (Timestamp)ResultSet1[1];
						String transactionTimeInString = dateFormat.format(transactionTime);
						newimplObj.setRollOff_Date(transactionTimeInString);
					}
					if(ResultSet1[2]!=null)
					{
						account_name =(String)ResultSet1[2];
						newimplObj.setCurrent_Owner(account_name);

					}
					if(ResultSet1[3]!=null)
					{
						machine_Hour = (String)ResultSet1[3];
						newimplObj.setMachineHour(machine_Hour);
					}
					if(ResultSet1[4]!=null)
					{
						CreatedTimestamp=(Timestamp)ResultSet1[4];
						String transactionTimeInString = dateFormat.format(CreatedTimestamp);
						newimplObj.setLast_Reported(transactionTimeInString);
					}
					if(ResultSet1[5]!=null)
					{
						fW_Version_Number=(String)ResultSet1[5];
						newimplObj.setFW_Version_Number(fW_Version_Number);
					}
					MachineListImpl.add(newimplObj);
				}
			
			//To get the Non-communicated machine details
			AssetControlUnitEntity AssetControlUnit =null;
			AccountEntity Account =null;
			String selectQuery ="SELECT acu.serial_number,acc.account_name";
			String fromQuery =" FROM AssetEntity acu,AccountEntity acc";
			String whereQuery =" WHERE acc.status=true and acu.serial_number NOT IN "
				+ " (SELECT distinct(amh.serialNumber) FROM AssetMonitoringHeaderEntity amh) and acu.primary_owner_id = acc.account_id";
			String finalQuery= selectQuery+fromQuery+whereQuery;
			Query query4 = session.createQuery(finalQuery);
			Iterator query4Itr = query4.list().iterator();
			while (query4Itr.hasNext())
			{
				//System.out.println("Inside 5th while");
				Object[] result = (Object[]) query4Itr.next();
				DetailMachineImpl newimplObj1 = new DetailMachineImpl();
				if (result[0] != null) {
					AssetControlUnit = (AssetControlUnitEntity)result[0];
					serialNumber = AssetControlUnit.getSerialNumber();
					newimplObj1.setSerialNumber(serialNumber);				
				}
				if (result[1] != null) {
					account_name=(String)result[1];
					newimplObj1.setCurrent_Owner(account_name);
				}
				newimplObj1.setFW_Version_Number("NA");
				newimplObj1.setRollOff_Date("NA");
				newimplObj1.setLast_Reported("NA");
				newimplObj1.setMachineHour("NA");
				MachineListImpl.add(newimplObj1);
			}
			ConnectMySQL connectionObj = new ConnectMySQL();
				con = connectionObj.getConnection();
				statement = con.createStatement(); 
				ResultSet resultSet=null;
				String query = "SELECT table2.serial_number,table1.account_name as Owner,table4.parameterValue as MachineHours,table3.fwVersionNumber,table3.createdTimestamp as LastReported,"
					+ " table2.dateTime FROM "
					+ "  AccountEntity table1 "
					+ " INNER JOIN "
					+ "AssetEntity table2 ON table2.primary_owner_id = table1.account_id "
					+ " JOIN "
					+ "(SELECT "
					+ " b1 . * FROM AssetMonitoringHeaderEntity b1 "
					+ " JOIN (SELECT " 
					+ "serialNumber, MAX(createdTimestamp) As maxDate FROM "
					+ " AssetMonitoringHeaderEntity "
					+ "GROUP BY serialNumber) b2 ON b1.serialNumber = b2.serialNumber "
					+ "AND b1.createdTimestamp = b2.maxDate) table3 on table2.serial_number = table3.serialNumber "
					+ " INNER JOIN "
					+ " AssetMonitoringDetailEntity table4 ON table4.transactionNumber = table3.transactionNumber "
					+ " AND table4.parameterId = 4"
					+ "group by table2.serial_number"
					+ "union SELECT" 
					+ " a.serial_number,c.account_name,'','','',a.dateTime"
					+ "FROM"
					+ "  AssetEntity a,"
					+ " AccountEntity c"
					+ "  where"
					+ " a.serial_number not in (select distinct "
					+ " b.serialNumber "
					+ "from"
					+ "AssetMonitoringHeaderEntity b)"
					+ "and a.primary_owner_id = c.account_id";
				resultSet = statement.executeQuery(query);
				while (resultSet.next()) {
					DetailMachineImpl MachineInfoBO=new DetailMachineImpl();
					MachineInfoBO.setSerialNumber("serialNumber");
					MachineInfoBO.setCurrent_Owner("current_Owner");
					MachineInfoBO.setFW_Version_Number("fW_Version_Number");
					MachineListImpl.add(MachineInfoBO);
				 }	


		}

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		}
		finally
		{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			} 

		}
		return MachineListImpl;
	}
*/
	
	public List<DetailMachineImpl> getMachineListInfo() {
		// TODO Auto-generated method stub
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		List<DetailMachineImpl> MachineListImpl = new LinkedList<DetailMachineImpl>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		/*Connection con=null;
		Statement statement =null;*/
		try
		{
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			} 
			List<String> ownershipStartDateList = new ArrayList<String>();
			List<String> createdtransactionTimeStamp = new ArrayList<String>();
			List<String> machineHourlist = new ArrayList<String>();
			List<String> serialNumberList = new LinkedList<String>();
			List<Integer> transactionNumberList = new LinkedList<Integer>();
			ListToStringConversion conversionObj = new ListToStringConversion();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//To get the current ownershipStartDate 
			Query query1 = session
			//			.createQuery("select max(a.ownershipStartDate),c.serialNumber from AssetAccountMapping a ,AssetEntity c where c group by c.serialNumber");
			.createQuery("select max(a.ownershipStartDate),a.serialNumber from AssetAccountMapping a group by a.serialNumber");
			Iterator contactItr = query1.list().iterator();
			while (contactItr.hasNext())
			{
				//System.out.println("Inside first while");
				Object[] result = (Object[]) contactItr.next();
				if (result[0] != null) {
					Timestamp transactionTime = (Timestamp) result[0];
					String transactionTimeStamp = dateFormat.format(transactionTime);
					ownershipStartDateList.add(transactionTimeStamp);
					//infoLogger.info("ownershipStartDateList"+ownershipStartDateList);
				}
			}
			String ownershipStartDateStringList = conversionObj.getStringList(ownershipStartDateList).toString();
			iLogger.info("ownershipStartDateStringList"+ownershipStartDateStringList);

			//To get the latest packet received time
		/*	Timestamp transactionTimestamp = null;
			String transactionTimestampString =null;
			Query query2 = session
			.createQuery("select max(a.createdTimestamp),a.serialNumber from AssetMonitoringHeaderEntity a group by a.serialNumber");
			Iterator transactionTimeItr = query2.list().iterator();
			while (transactionTimeItr.hasNext())
			{
				AssetEntity asset =null;
				//System.out.println("Inside 2nd while");
				Object[] result = (Object[]) transactionTimeItr.next();
				if (result[0] != null) {
					transactionTimestamp = (Timestamp) result[0];
					transactionTimestampString = dateFormat.format(transactionTimestamp);
					//String transactionTimestampString = transactionTimestamp.toString();
					createdtransactionTimeStamp.add(transactionTimestampString);
					infoLogger.info("createdtransactionTimeStamp"+createdtransactionTimeStamp);
				}
				if (result[1] != null) {
					asset = (AssetEntity) result[1];
					serial_num =asset.getSerial_number().getSerialNumber();
					serialNumberList.add(serial_num);
				}
			}
			String createdtransactionTimeStampStringList = conversionObj.getStringList(createdtransactionTimeStamp).toString();*/
			int transactionNumber =0;
			String serial_num = null;
			/*Query query3 = session
			.createQuery("select max(a.transactionNumber),a.serialNumber from AssetMonitoringHeaderEntity a group by a.serialNumber");
			Iterator transactionNumItr = query3.list().iterator();
			while (transactionNumItr.hasNext())
			{
				AssetEntity asset =null;
				//System.out.println("Inside 2nd while");
				Object[] result = (Object[]) transactionNumItr.next();
				if (result[0] != null) {
					transactionNumber = (Integer)result[0];
					transactionNumberList.add(transactionNumber);
					//infoLogger.info("transactionNumberList"+transactionNumberList);
				}
				if (result[1] != null) {
					asset = (AssetEntity) result[1];
					serial_num =asset.getSerial_number().getSerialNumber();
					serialNumberList.add(serial_num);
				}
			}
			String transactionNumberAsStringList = conversionObj.getIntegerListString(transactionNumberList).toString();
			iLogger.info("transactionNumberAsStringList"+transactionNumberAsStringList);
			String serialNumberListStringList = conversionObj.getStringList(serialNumberList).toString();*/

			/*		String Hour ="Hour";
			String machineHour=null;
			Query last1HourPkt = session.createQuery(" select b.parameterValue,a.serialNumber from AssetMonitoringHeaderEntity a, " +
					" AssetMonitoringDetailEntity b, MonitoringParameters c " +
					" where " +
					" a.createdTimestamp in ("+createdtransactionTimeStampStringList+")" +
					" and a.transactionNumber=b.transactionNumber " +
					" and b.parameterId = c.parameterId " +
					" and c.parameterName='"+Hour+"'");
			Iterator last1HourPktItr = last1HourPkt.list().iterator();
			Object[] ResultSet = null;
			while(last1HourPktItr.hasNext())
			{
				System.out.println("Inside 3rd while");
				ResultSet = (Object[]) last1HourPktItr.next();
				machineHour = (String)ResultSet[0];
				machineHourlist.add(machineHour);
			}*/
			//To get the communicated machine details
			String serialNumber;
			String account_name;
			String machine_Hour;
			Timestamp CreatedTimestamp;
			String fW_Version_Number;
			
			//20160718 - @suresh DAL layer implemntation for ams where latest cmh for a VIN can be found in parameter coloumn
			 //DF20161229 @Supriya changing asset_monitoring_snapshot_new to asset_monitoring_snapshot which is having txndata as json column		
			DynamicAMS_DAL amsDaoObj=new DynamicAMS_DAL();
			String txnKey = "MachineInfoDetailsBO:getMachineListInfo";
			List<AmsDAO> amsList = amsDaoObj.getAMSDataOn_OwnershipDateList(txnKey, ownershipStartDateStringList);
			
			/*Query query = session
				.createQuery(" select b.serial_number,b.dateTime,c.account_name,e.parameterValue,d.createdTimestamp ,d.fwVersionNumber from AssetAccountMapping a JOIN a.serialNumber b "
						+ " JOIN  a.accountId c ,AssetMonitoringDetailEntity e JOIN e.transactionNumber d where a.ownershipStartDate in("+ownershipStartDateStringList+")"
						+ " and d.transactionNumber in("+transactionNumberAsStringList+")"
						+ " and e.parameterId=4 "
						//+ " and d.serialNumber in("+serialNumberListStringList+")"
						+ " and d.transactionNumber=e.transactionNumber "
						+ " and a.accountId=c.account_id " 
						+ " and a.serialNumber=b.serial_number "
						+ " and a.serialNumber=d.serialNumber "
						+ " group by b.serial_number ");*/

				Iterator queryItr = amsList.iterator();
				Object[] ResultSet1 = null;
				while(queryItr.hasNext())
				{
					//System.out.println("Inside 4th while");
					AmsDAO daoOject = (AmsDAO) queryItr.next();
					DetailMachineImpl newimplObj = new DetailMachineImpl();
					serialNumber=null;
					/*AssetEntity asset =null;
					AccountEntity account =null;
					AssetMonitoringDetailEntity AssetMonitoringDetail=null;
					AssetMonitoringHeaderEntity AssetMonitoringHeader=null;
					AssetControlUnitEntity AssetControlUnit =null;*/
					Timestamp transactionTime =null;
					if(daoOject.getSerial_Number()!=null)
					{					
						serialNumber = daoOject.getSerial_Number();
						newimplObj.setSerialNumber(serialNumber);
						serialNumberList.add(serialNumber);
					}
					if(daoOject.getRollOffDate()!=null)
					{
						
						newimplObj.setRollOff_Date(daoOject.getRollOffDate());
					}
					if(daoOject.getAccount_name()!=null)
					{
					//	account_name =(String)ResultSet1[2];
						newimplObj.setCurrent_Owner(daoOject.getAccount_name());

					}
					/*if(daoOject.getParameters()!=null)
					{
						String parameters=daoOject.getParameters();
						String [] currParamList=parameters.split("\\|", -1);
						
						//String currParam= LAT|LONG|Enginestatus|Machinehours|ExternalBatteryVoltage|HCT|LOP|InternalBatteryLow
						if(currParamList.length>2){
							machine_Hour = currParamList[3];
							newimplObj.setMachineHour(machine_Hour);
						}
						if(currParamList.length>8)
							newimplObj.setFW_Version_Number(currParamList[8]);
					}*/
					HashMap<String,String> txnDataMap=new HashMap<String, String>();
					if(daoOject.getTxnData()!=null)
					{
						txnDataMap = daoOject.getTxnData();
						machine_Hour = txnDataMap.get("CMH");
						newimplObj.setMachineHour(machine_Hour);
						String fw_version = txnDataMap.get("FW_VER");
						newimplObj.setFW_Version_Number(fw_version);
					}
					if(daoOject.getLatest_Created_Timestamp()!=null)
					{
						
						newimplObj.setLast_Reported(daoOject.getLatest_Created_Timestamp());
					}
					/*if(ResultSet1[5]!=null)
					{
						fW_Version_Number=(String)ResultSet1[5];
						newimplObj.setFW_Version_Number(fW_Version_Number);
					}*/
					MachineListImpl.add(newimplObj);
				}
			
			//To get the Non-communicated machine details
			/*AssetControlUnitEntity AssetControlUnit =null;
			AccountEntity Account =null;
			String selectQuery ="SELECT acu.serial_number,acc.account_name";
			String fromQuery =" FROM AssetEntity acu,AccountEntity acc";
			String whereQuery =" WHERE acc.status=true and acu.serial_number NOT IN "
				+ " (SELECT distinct(amh.serialNumber) FROM AssetMonitoringHeaderEntity amh) and acu.primary_owner_id = acc.account_id";
			String finalQuery= selectQuery+fromQuery+whereQuery;
			Query query4 = session.createQuery(finalQuery);*/
				DynamicAMS_DAL amsDaoObj1=new DynamicAMS_DAL();
				List<AmsDAO> amsList1 = amsDaoObj1.getNonCommMachinesFromAMS(txnKey);
			Iterator query4Itr = amsList1.iterator();
			while (query4Itr.hasNext())
			{
				//System.out.println("Inside 5th while");
				AmsDAO daoOject = (AmsDAO) query4Itr.next();
				DetailMachineImpl newimplObj1 = new DetailMachineImpl();
				if (daoOject.getSerial_Number() != null) {
					
					newimplObj1.setSerialNumber(daoOject.getSerial_Number());				
				}
				if (daoOject.getAccount_name() != null) {
					account_name=(String)daoOject.getAccount_name();
					newimplObj1.setCurrent_Owner(account_name);
				}
				newimplObj1.setFW_Version_Number("NA");
				newimplObj1.setRollOff_Date("NA");
				newimplObj1.setLast_Reported("NA");
				newimplObj1.setMachineHour("NA");
				MachineListImpl.add(newimplObj1);
			}
			/*ConnectMySQL connectionObj = new ConnectMySQL();
				con = connectionObj.getConnection();
				statement = con.createStatement(); 
				ResultSet resultSet=null;
				String query = "SELECT table2.serial_number,table1.account_name as Owner,table4.parameterValue as MachineHours,table3.fwVersionNumber,table3.createdTimestamp as LastReported,"
					+ " table2.dateTime FROM "
					+ "  AccountEntity table1 "
					+ " INNER JOIN "
					+ "AssetEntity table2 ON table2.primary_owner_id = table1.account_id "
					+ " JOIN "
					+ "(SELECT "
					+ " b1 . * FROM AssetMonitoringHeaderEntity b1 "
					+ " JOIN (SELECT " 
					+ "serialNumber, MAX(createdTimestamp) As maxDate FROM "
					+ " AssetMonitoringHeaderEntity "
					+ "GROUP BY serialNumber) b2 ON b1.serialNumber = b2.serialNumber "
					+ "AND b1.createdTimestamp = b2.maxDate) table3 on table2.serial_number = table3.serialNumber "
					+ " INNER JOIN "
					+ " AssetMonitoringDetailEntity table4 ON table4.transactionNumber = table3.transactionNumber "
					+ " AND table4.parameterId = 4"
					+ "group by table2.serial_number"
					+ "union SELECT" 
					+ " a.serial_number,c.account_name,'','','',a.dateTime"
					+ "FROM"
					+ "  AssetEntity a,"
					+ " AccountEntity c"
					+ "  where"
					+ " a.serial_number not in (select distinct "
					+ " b.serialNumber "
					+ "from"
					+ "AssetMonitoringHeaderEntity b)"
					+ "and a.primary_owner_id = c.account_id";
				resultSet = statement.executeQuery(query);
				while (resultSet.next()) {
					DetailMachineImpl MachineInfoBO=new DetailMachineImpl();
					MachineInfoBO.setSerialNumber("serialNumber");
					MachineInfoBO.setCurrent_Owner("current_Owner");
					MachineInfoBO.setFW_Version_Number("fW_Version_Number");
					MachineListImpl.add(MachineInfoBO);
				 }	*/


		}

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
		}
		finally
		{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			} 

		}
		return MachineListImpl;
	}
}

