/**
 * CR334 : 20221118 : Dhiraj K : Changes for Billing and ARD table update
 * CR388 :20230125 - Prasad -PaginationLogic_LLIRenewal
 * ME100009742 : 20231117 : Dhiraj K : Fleet shows "Vin expired" but appearing on the Billing report
 */
package remote.wise.service.implementation;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.wipro.mda.AssetProfileDetails;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AccountTenancyMapping;
import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetRenewalDataEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.AssetUtil;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;


public class AssetRenewalRESTImpl {

	@SuppressWarnings("rawtypes")
public List<HashMap<String,String>> getRenewalMachines(int tenancyId ,int pagNum, int pageSiz ){

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		int fromIndex = (pagNum -1)* pageSiz;
		
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<HashMap<String,String>> expiredAssetList = new LinkedList<HashMap<String,String>>();

		try{
			long startTime = System.currentTimeMillis();
			AssetControlUnitEntity asset = null;
			Date renewalDate=null;
			//CR 265-20211203-Deepthi: To block renewals for future months: formmating the date as MM and YYYY and using this in the below query. 
			SimpleDateFormat sdf = new SimpleDateFormat("MM");
			Calendar c1 = Calendar.getInstance();
			Calendar calendar = Calendar.getInstance();
			
			SimpleDateFormat sdf1 =new SimpleDateFormat("yyyy");
			Calendar c2=Calendar.getInstance();
			Calendar calendar1 = Calendar.getInstance();
			
			Date YearDate=calendar1.getTime();
			
		
	        Date todayDate =calendar.getTime();
	        
	        
	        SimpleDateFormat sdf2 =new SimpleDateFormat("yyyy-MM-dd");
	        Calendar c3=Calendar.getInstance();
	        Calendar calendar3 = Calendar.getInstance();
	        Date formattedDate=calendar3.getTime();
			
	        //CR265- commented the below code to do the date formatting
			/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c1 = Calendar.getInstance();
			Calendar calendar = Calendar.getInstance();
			// Add 2 months to current date
	        calendar.add(Calendar.MONTH, 2);
	        Date todayDate =calendar.getTime();*/
			AccountTenancyMapping atm = null;
			/*int accountId = 0;
			
			Query accountQ = session.createQuery("from AccountTenancyMapping where tenancy_id ="+tenancyId);
			Iterator accountItr = accountQ.list().iterator();
			if(accountItr.hasNext()){
				atm = (AccountTenancyMapping) accountItr.next();
				accountId = atm.getAccount_id().getAccount_id();
				
			}*/
			
//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			List<Integer> accountIdList = new LinkedList<Integer>();
			
			Query accountQ = session.createQuery("select a.account_id from AccountTenancyMapping a, AccountEntity b where b.status=true and a.account_id=b.account_id " +
					"and b.mappingCode = (select c.mappingCode from AccountEntity c where c.status=true and c.account_id=(select account_id from AccountTenancyMapping where tenancy_id in ("+tenancyId+")))");
			
			Iterator accItr = accountQ.list().iterator();
			while(accItr.hasNext())
			{
				AccountEntity account = (AccountEntity)accItr.next();
				accountIdList.add(account.getAccount_id());
			}
			
			ListToStringConversion conversion = new ListToStringConversion();

			String accountIdListAsString = conversion.getIntegerListString(accountIdList).toString();

			//Query query1 = session.createQuery("from AssetEntity where install_date is not null and date(renewal_date) <= '"+sdf.format(todayDate)+"'");
			//Query query1 = session.createQuery("select a.serial_number,a.renewal_date from AssetEntity a,AccountEntity ac,AssetOwnerSnapshotEntity aos where aos.accountId ="+accountId+" and a.active_status =1 and a.serial_number = aos.serialNumber and a.primary_owner_id = ac.account_id and date(renewal_date) <= '"+sdf.format(todayDate)+"'");
			
			//Query query1 = session.createQuery("select a.serial_number,a.renewal_date,a.countrycode from AssetEntity a,AccountEntity ac,AssetOwnerSnapshotEntity aos where aos.accountId in ("+accountIdListAsString+") and a.active_status =1 and a.serial_number = aos.serialNumber and a.primary_owner_id = ac.account_id and date(renewal_date) <= '"+sdf.format(todayDate)+"'");
		//Deepthi: JCB6184: 20220127-Query Commented to take the renewal date based on year and month combination
			Query query1 = session.createQuery("select a.serial_number,a.renewal_date, a.countrycode from AssetEntity a,AccountEntity ac,AssetOwnerSnapshotEntity aos where aos.accountId in ("+accountIdListAsString+") and a.active_status =1 and a.serial_number = aos.serialNumber and a.primary_owner_id = ac.account_id and "
					+ "((year(renewal_date) < '"+sdf1.format(YearDate)+"') or ((( year(renewal_date) = '"+sdf1.format(YearDate) +"') and (Month(renewal_date) <= '"+sdf.format(todayDate)+"'"+"))))");
			//limit "+fromIndex+","+toIndex+"
			iLogger.info("Executing query: "+query1.getQueryString());
			query1.setFirstResult(fromIndex);
			query1.setMaxResults(pageSiz);
			iLogger.info("Executing query: "+query1.getQueryString());
			Iterator assetItr = query1.list().iterator();
			long intermediateTime = System.currentTimeMillis();
			iLogger.info("executionTime for Query:"+(intermediateTime-startTime)+"~"+""+"~");
			Object[] resultSet = null;
			while(assetItr.hasNext()){
				resultSet = (Object[]) assetItr.next();
				asset = (AssetControlUnitEntity) resultSet[0];
				renewalDate = c1.getTime();
				HashMap<String, String> expiredAsset = new LinkedHashMap<String, String>();
				expiredAsset.put("serial_number", asset.getSerialNumber());
				expiredAsset.put("expiry_date",sdf2.format(resultSet[1]));
				//20220127: Handling a NULL Check for the Country code as a part of the Defect Id: JCB6184
				if(resultSet[2]!=null)
				{
				expiredAsset.put("country_code",resultSet[2].toString());
				}
				else
				{
				expiredAsset.put("country_code","null");
				}
				expiredAsset.put("country_code",resultSet[2].toString());
				try{
					if(!(resultSet[2].toString() ==null || resultSet[2].toString().isEmpty())){
					iLogger.info(" ------- Country code : AssetRenewalRESTService --------: " + (String)expiredAsset.get("country_code"));
					}else				
						fLogger.fatal("CountryCode is NULL :");
					}catch(Exception c){
						c.printStackTrace();
					}
				iLogger.info(" ------- Country code --------: "+expiredAsset.get("country_code"));
				expiredAssetList.add(expiredAsset);
			}
			iLogger.info("expiredAsset size" + expiredAssetList.size());
			intermediateTime = System.currentTimeMillis();
			iLogger.info("executionTime to put in expiredAssetList:"+(intermediateTime-startTime)+"~"+""+"~");


		}catch(Exception e){
			fLogger.fatal("Exception :"+e);
			e.printStackTrace();
		}finally{
			if(session.isOpen()){
				session.close();
			}
		}
		return expiredAssetList;
	}

	//CR388 :st
	@SuppressWarnings("rawtypes")
 public List<HashMap<String,String>> getSelectVins(int tenancyId  ,List<String> serialNumbers){

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<HashMap<String,String>> expiredAssetList = new LinkedList<HashMap<String,String>>();
		List<String> sls = new ArrayList<>();
		for(int i =0; i <= serialNumbers.size()-1; i++){
			String sn=	serialNumbers.get(i);
			int index = serialNumbers.indexOf(i);
			if(sn.length() < 14){
				System.out.println("serialNumber in if " + sn);
			String  vin =	AssetUtil.getVinNoUsingMachineNo(sn);
			
			System.out.println("vin in if " + vin);
			
		    if( vin.length() == 17){
		    	sls.add(vin);
		    }
		    else{
		    	HashMap<String, String> vinError = new LinkedHashMap<String, String>();
		    	vinError.put("Error",vin);
		    	expiredAssetList.add(vinError);
		    	iLogger.info("No VINs found for the machine :"+sn);
		    	return expiredAssetList;
		    }
			}
			else {
				sls.add(sn);
			}
			}
		System.out.println("new list sls" + sls);
		try{
			AssetControlUnitEntity asset = null;
			Date renewalDate=null;
			//CR 265-20211203-Deepthi: To block renewals for future months: formmating the date as MM and YYYY and using this in the below query. 
			SimpleDateFormat sdf = new SimpleDateFormat("MM");
			Calendar c1 = Calendar.getInstance();
			Calendar calendar = Calendar.getInstance();
			
			SimpleDateFormat sdf1 =new SimpleDateFormat("yyyy");
			Calendar c2=Calendar.getInstance();
			Calendar calendar1 = Calendar.getInstance();
			
			Date YearDate=calendar1.getTime();
			
		
	        Date todayDate =calendar.getTime();
	        
	        
	        SimpleDateFormat sdf2 =new SimpleDateFormat("yyyy-MM-dd");
	        Calendar c3=Calendar.getInstance();
	        Calendar calendar3 = Calendar.getInstance();
	        Date formattedDate=calendar3.getTime();
		
			AccountTenancyMapping atm = null;
			
		
			
//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			List<Integer> accountIdList = new LinkedList<Integer>();
			
			Query accountQ = session.createQuery("select a.account_id from AccountTenancyMapping a, AccountEntity b where b.status=true and a.account_id=b.account_id " +
					"and b.mappingCode = (select c.mappingCode from AccountEntity c where c.status=true and c.account_id=(select account_id from AccountTenancyMapping where tenancy_id in ("+tenancyId+")))");
			
			Iterator accItr = accountQ.list().iterator();
			while(accItr.hasNext())
			{
				AccountEntity account = (AccountEntity)accItr.next();
				accountIdList.add(account.getAccount_id());
			}
			
			ListToStringConversion conversion = new ListToStringConversion();

			String accountIdListAsString = conversion.getIntegerListString(accountIdList).toString();
			String vinListAsString = conversion.getStringList(sls).toString();
			System.out.println("vinListAsString : " + vinListAsString);
			//Query query1 = session.createQuery("from AssetEntity where i nstall_date is not null and date(renewal_date) <= '"+sdf.format(todayDate)+"'");
			//Query query1 = session.createQuery("select a.serial_number,a.renewal_date from AssetEntity a,AccountEntity ac,AssetOwnerSnapshotEntity aos where aos.accountId ="+accountId+" and a.active_status =1 and a.serial_number = aos.serialNumber and a.primary_owner_id = ac.account_id and date(renewal_date) <= '"+sdf.format(todayDate)+"'");
			
			//Query query1 = session.createQuery("select a.serial_number,a.renewal_date,a.countrycode from AssetEntity a,AccountEntity ac,AssetOwnerSnapshotEntity aos where aos.accountId in ("+accountIdListAsString+") and a.active_status =1 and a.serial_number = aos.serialNumber and a.primary_owner_id = ac.account_id and date(renewal_date) <= '"+sdf.format(todayDate)+"'");
		//Deepthi: JCB6184: 20220127-Query Commented to take the renewal date based on year and month combination
			Query query1 = session.createQuery("select a.serial_number,a.renewal_date, a.countrycode from AssetEntity a,AccountEntity ac,AssetOwnerSnapshotEntity aos where aos.accountId in ("+accountIdListAsString+")"
					+ " and a.active_status =1 and a.serial_number = aos.serialNumber and a.primary_owner_id = ac.account_id and ((year(renewal_date) < '"+sdf1.format(YearDate)+"') or ((( year(renewal_date) = '"+sdf1.format(YearDate) +"')"
							+ " and (Month(renewal_date) <= '"+sdf.format(todayDate)+"'"+" ))))  and a.serial_number in ("+vinListAsString+")");
//			 and a.serial_number in ("+vinListAsString+")
			
			iLogger.info("Executing query: "+query1.getQueryString());
			Iterator assetItr = query1.list().iterator();
			Object[] resultSet = null;
			while(assetItr.hasNext()){
				resultSet = (Object[]) assetItr.next();
				asset = (AssetControlUnitEntity) resultSet[0];
				renewalDate = c1.getTime();
				HashMap<String, String> expiredAsset = new LinkedHashMap<String, String>();
				expiredAsset.put("serial_number", asset.getSerialNumber());
				expiredAsset.put("expiry_date",sdf2.format(resultSet[1]));
				//20220127: Handling a NULL Check for the Country code as a part of the Defect Id: JCB6184
				if(resultSet[2]!=null)
				{
				expiredAsset.put("country_code",resultSet[2].toString());
				}
				else
				{
				expiredAsset.put("country_code","null");
				}
				//expiredAsset.put("country_code",resultSet[2].toString());
				iLogger.info(" ------- Country code --------: "+expiredAsset.get("country_code"));
				System.out.println("expiredAsset : " + expiredAsset);
				expiredAssetList.add(expiredAsset);
			}

		}catch(Exception e){
			fLogger.fatal("Exception :"+e);
			e.printStackTrace();
		}finally{
			if(session.isOpen()){
				session.close();
			}
		}
		return expiredAssetList;
	}
	//CR388 :en
	
	//ME100009742.sn
	public String setRenewalMachinesV2(List<HashMap<String,String>> inputObj){
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String response = "FAILURE";
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

		ConnectMySQL connFactory = new ConnectMySQL();
		try(Connection con1 = connFactory.getConnection();
				Statement st1 = con1.createStatement();
				Connection con2 = connFactory.getEdgeProxyConnection();
				Statement st2 = con2.createStatement()){
			
			//Validate contact id user
			CommonUtil utilObj = new CommonUtil();
			String contactid = utilObj.getUserId(inputObj.get(0).get("login_id"));
			String contactSelectQuery = "SELECT contact_id from contact where contact_id='" + contactid + "'";
			ResultSet rs13 = st1.executeQuery(contactSelectQuery);
			if(rs13.next()) {
				contactid = rs13.getString("contact_id");
			}
			if (contactid == null || contactid.trim().isEmpty()) {
				iLogger.info("Contact id is not provided. So can't proceed further");
				return response;
			}
			
			for(HashMap<String,String> renewalAsset : inputObj){
				String vin = null;
				String vinQuery = "SELECT serial_number from asset where  serial_number ='"+renewalAsset.get("serial_number")+"'";
				ResultSet rs11 = st1.executeQuery(vinQuery);
				if(rs11.next()) {
					vin = rs11.getString("serial_number");
				}
				if(vin==null){
					vin = renewalAsset.get("serial_number");
					vin=vin.replaceFirst("^0+(?!$)", "");
					vinQuery = "SELECT serial_number from asset where  where machine_number ='"+renewalAsset.get("serial_number")+"'";
					ResultSet rs12 = st1.executeQuery(vinQuery);
					if(rs11.next()) {
						vin = rs12.getString("serial_number");
					}
				}
				if(vin!=null) {
					iLogger.info("Updating Renewal data in required tables for vin : " + vin);
					String subscribedFrom = renewalAsset.get("subscribed_from") + " 00:00:00";
					String renewalDate = renewalAsset.get("renewal_date") + " 00:00:00";
					String noOfYrs = renewalAsset.get("mode_of_subscription");
										
					String subscriptionStartDate = LocalDateTime.parse(subscribedFrom, dtf2).format(dtf);
					String subscriptionEndDate = LocalDateTime.parse(renewalDate, dtf2).format(dtf);
					String currentDate = LocalDateTime.now().format(dtf);
					
					//Insert the renewal data of the VIN in ard table
					String ardInsertQuery = "INSERT INTO asset_renewal_data (Serial_Number,NoOfYrs,Updated_On,Updated_By,SubsStartDate,SubsEndDate) VALUES "
											+ "('"+vin+"',"+noOfYrs+",'"+currentDate+"','"+contactid+"','"+subscriptionStartDate+"','"+subscriptionEndDate+"')";
					st1.execute(ardInsertQuery)	;
					
					//Update the renewal data of the VIN in asset table
					String assetUpdateQuery = "UPDATE asset SET renewal_date='"+subscriptionEndDate+"', renewal_flag=1 WHERE serial_number='"+vin+"'";
					st1.execute(assetUpdateQuery);
					
					
					String selectQuery = "select * from device_status_info where vin_no='"+vin+"'";
					ResultSet rs21 = st2.executeQuery(selectQuery);
					//Update the renewal flag of the VIN
					if(rs21.next()){
						String updateQuery = "UPDATE device_status_info SET Renewal_Flag="+1+" where vin_no='"+vin+"'";
						st2.executeUpdate(updateQuery);
					}
					//Insert the renewal flag of the VIN
					else{
						String insertQuery = "INSERT INTO device_status_info(vin_no,Renewal_Flag) VALUES ('"+vin+"', "+1+")";
						st2.execute(insertQuery);	
					}
					//Update Renewal Details in Mongo DB:
					new AssetProfileDetails().setAssetRenewalDateDetails(vin, subscriptionEndDate, "1");
					iLogger.info("Renewal data updated for vin : "+vin);
				}
			}
			response = "SUCCESS";
		}catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception :"+e.getMessage());
			response = "FAILURE";
		}
		return response;
	}
	//ME100009742.en
	
	public String setRenewalMachines(List<HashMap<String,String>> inputObj){

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Connection con = null;
		Statement statement = null;
		ResultSet resultSet=null;
		String response = "FAILURE";

		try{
			AssetEntity asset = null;
			HashMap<String, String> renewalAsset = null;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Calendar c1 = Calendar.getInstance();
			Date todayDate = new Date();
			Timestamp renewal_date = null,updated_on=null,subscribed_from=null;
			String contact_id = null;
			ContactEntity contact = null;
			String selectQuery = null;
			String insertQuery = null;
			String updateQuery = null;
			String renewalDateStr = null;
			
			ConnectMySQL connectionObj = new ConnectMySQL();
			con = connectionObj.getEdgeProxyConnection();
			statement = con.createStatement(); 
			
			iLogger.info("Into the Implementaion class");

			for(int i=0; i<inputObj.size();i++){

				renewalAsset = (HashMap<String, String>) inputObj.get(i);
				Query query = session.createQuery("from AssetEntity where serial_number ='"+renewalAsset.get("serial_number")+"'");
				Iterator assetItr = query.list().iterator();
				if(assetItr.hasNext())
				{
					asset = (AssetEntity) assetItr.next();
				}
				if(asset==null)
				{
					String serialNumber = renewalAsset.get("serial_number");
					serialNumber=serialNumber.replaceFirst("^0+(?!$)", "");
					Query machNumQ = session.createQuery(" from AssetEntity where machineNumber='"+serialNumber+"'");
					Iterator machNumItr = machNumQ.list().iterator();
					if(machNumItr.hasNext())
					{
						asset = (AssetEntity) machNumItr.next();
					}
				}
				if(asset!=null)
				{
					asset.setRenewal_flag(1);
					c1.setTime(sdf.parse(renewalAsset.get("renewal_date")));
					renewalDateStr =  new SimpleDateFormat("yyyy-MM-dd").format(c1.getTime());
					renewal_date = new Timestamp(c1.getTime().getTime());
					asset.setRenewal_date(renewal_date);
					AssetRenewalDataEntity assetRenewalObj = new AssetRenewalDataEntity();
					assetRenewalObj.setSerial_number(asset);
					assetRenewalObj.setMode_of_subscription(Integer.parseInt(renewalAsset.get("mode_of_subscription")));
					c1.setTime(todayDate);
					updated_on = new Timestamp(c1.getTime().getTime());
					assetRenewalObj.setUpdated_on(updated_on);
					c1.setTime(sdf.parse(renewalAsset.get("subscribed_from")));
					subscribed_from = new Timestamp(c1.getTime().getTime());
					assetRenewalObj.setSubscribed_from(subscribed_from);
					assetRenewalObj.setSubscribed_to(renewal_date); //CR334.n
					CommonUtil utilObj = new CommonUtil();
					contact_id = utilObj.getUserId(renewalAsset.get("login_id"));
					Query contactQ = session.createQuery("from ContactEntity where contact_id = '"+contact_id+"'");
					Iterator contactItr = contactQ.list().iterator();
					if(contactItr.hasNext()){
						contact = (ContactEntity) contactItr.next();
					}
					assetRenewalObj.setUpdated_by(contact);
					session.update(asset);
					session.save(assetRenewalObj);
					
					selectQuery = "select * from device_status_info where vin_no='"+asset.getSerial_number().getSerialNumber()+"'";
					resultSet = statement.executeQuery(selectQuery);

					if(resultSet.next()){
						updateQuery = "UPDATE device_status_info SET Renewal_Flag="+1+" where vin_no='"+asset.getSerial_number().getSerialNumber()+"'";
						statement.executeUpdate(updateQuery);
					}
					//Updating the renewal flag of the VIN
					else{
						insertQuery = "INSERT INTO device_status_info(vin_no,Renewal_Flag) VALUES ('"+asset.getSerial_number().getSerialNumber()+"', "+1+")";
						statement.execute(insertQuery);	
					}
					//DF20200714 Avinash Xavier A :Updating Renewal Details in Mongo DB:
					new AssetProfileDetails().setAssetRenewalDateDetails(asset.getSerial_number().getSerialNumber(), renewalDateStr, String.valueOf(asset.getRenewal_flag()));
				}
				asset = null;
			}

			response = "SUCCESS";

		}catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("Exception :"+e);
			if(session != null && session.isOpen())
				session.getTransaction().rollback();
			response = "FAILURE";
		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			}

			try{			
				if(resultSet != null){
					resultSet.close();
				}
				if(statement != null){
					statement.close();
				}
				if(con != null){
					con.close();
				}
			}catch (Exception e) {
				e.printStackTrace();
				fLogger.fatal("Exception in closing the connection. :"+e);
			}

		}
		return response;
	}
	//CR334.sn
	public boolean doSubsriptionValidation(String vin, String subStrtDt) {

		boolean flag = true;
		String tempVin = null;
		Logger fLogger = FatalLoggerClass.logger;
		String query = "SELECT serial_number, renewal_date FROM asset WHERE serial_number = '" + vin + "'";
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet  rs = statement.executeQuery(query)) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
			subStrtDt = LocalDateTime.parse(subStrtDt+ " 00:00:00", dtf2).format(dtf) ;
			LocalDateTime subStrtDtTime = LocalDateTime.parse(subStrtDt, dtf);
			LocalDateTime renewalDtTime = null;
			
			while(rs.next()) {
				tempVin = rs.getString("serial_number");
				renewalDtTime = LocalDateTime.parse(rs.getString("renewal_date").split("\\.")[0], dtf);
			}
			if (null == tempVin) {
				// Its a machine number
				vin=vin.replaceFirst("^0+(?!$)", "");
				query = "SELECT renewal_date FROM asset WHERE serial_number like '%" + vin + "'";
				ResultSet rs2 = statement.executeQuery(query);
				while(rs2.next()) {
					renewalDtTime = LocalDateTime.parse(rs2.getString("renewal_date").split("\\.")[0], dtf);
				}
			}
			if(subStrtDtTime.isBefore(renewalDtTime)) {
				flag = false;
			}
		}catch (Exception e) {
			flag = false;
			e.printStackTrace();
			fLogger.fatal("Exception in fetching renewal date for machine:" + vin + ":"+ e);
		}
		return flag;
	}
	//CR334.en
}
