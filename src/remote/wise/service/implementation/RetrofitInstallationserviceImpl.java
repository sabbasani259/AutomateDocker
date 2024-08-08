/**
 * CR334 : 20221118 : Dhiraj K : Changes for Billing and ARD table update
 * CR352 : 20230505 : Dhiraj K : Retrofitment Changes
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetRenewalDataEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//DF20180302 @Maniratnam :: Retrofitment Installation
public class RetrofitInstallationserviceImpl {

	public List<String> doRetrofitInstallation(
			List<HashMap<String, String>> inputObj) {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		HashMap<String, List<String>> Result = new HashMap<String, List<String>>();
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		List<String> responses=new LinkedList<String>();

		//String out = sdf2.format(sdf.parse("22/11/2007"));
		String vin = null;
		HashMap<String, String> vins = new HashMap<String, String>();
		Query query = null;
		AssetEntity asset = null;
		AssetEntity assetEntity = null;
		String msgId=null;
		Calendar c1 = Calendar.getInstance();
		Date todayDate = new Date();
		String ddyymm=null;
		Timestamp renewal_date = null,updated_on=null,subscribed_from=null;
		int hours;
		int minutes;
		int seconds;
		String fileref;
		String contact_id = null;
		ContactEntity contact = null;
		Connection con = null;
		Statement statement = null;
		String selectQuery = null;
		String insertQuery = null;
		String updateQuery = null;
		String response = null;
		ResultSet resultSet=null;

		try {
			
			for (int i = 0; i < inputObj.size(); i++) {
				String dealercode = null;
				if(session== null || !session.isOpen()){
					session = HibernateUtil.getSessionFactory().openSession();}
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.beginTransaction();
				vins = (HashMap<String, String>) inputObj.get(i);
				vin = vins.get("serial_number");
				iLogger.info("Retrofitment :: AssetInstallation for the vin :"+vin);
				String validation=new CommonUtil().inputFieldValidation(vin);
				if(validation.equalsIgnoreCase("SUCCESS")){
				query=session.createQuery("from AssetEntity where serial_number like '%"+vin+"%'");
				Iterator itr=query.list().iterator();
				if((itr.hasNext()))
				{
					assetEntity= (AssetEntity) itr.next();
				}
				if(assetEntity!=null)
				{
				if (vin.length() > 7) {
					query = session
							.createQuery("select accountCode from AccountEntity where account_id=(select accountId from AssetOwnerSnapshotEntity where serialNumber='"
									+ vin
									+ "' and accountType='"
									+ "Dealer"
									+ "')");
				} else {
					query = session
							.createQuery("select accountCode from AccountEntity where account_id=(select accountId from AssetOwnerSnapshotEntity where serialNumber like'%"
									+ vin
									+ "%' and accountType='"
									+ "Dealer"
									+ "')");
				}
				Iterator dealer = query.list().iterator();
				if (dealer.hasNext()) {
					dealercode = (String) dealer.next();
					// vinDealer.put(vin,dealercode.toString());
					// dealercodes.add(vinDealer);
				}
				if (dealercode!=null){
				if(!(assetEntity.getRetrofitFlag()==1))
				{	
				c1.setTime(todayDate);
				SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
				ddyymm = f.format(new Date());
				hours = c1.get(Calendar.HOUR_OF_DAY);
				minutes = c1.get(Calendar.MINUTE);
				seconds = c1.get(Calendar.SECOND);
				msgId="MSGAssetInstallation_"+ddyymm+"_"+hours+""+minutes+""+seconds;
				fileref="AssetInstallation_"+ddyymm+"_"+hours+""+minutes+""+seconds;
				//response=vin+":"+new InstallationDateDetailsImpl().setAssetserviceSchedule(vin,vins.get("install_date"),dealercode,"",msgId);//CR352.o
				response=vin+":"+new InstallationDateDetailsImpl().setAssetserviceSchedule(vin,vins.get("install_date"),dealercode,"",msgId, false);//CR352.n
				responses.add(response);
				iLogger.info("Retrofitment:: AssetInstallation #After calling InstallationDateDetailsImpl().setAssetserviceSchedule() :"+response);
				String faultCause =null;
				String status=null;
				if(response.split(":").length>1)
				{
					faultCause=response.split(":")[1];
					status = faultCause.split("-")[0].trim();

				}
				if(status.equalsIgnoreCase("SUCCESS"))
				{	if(session== null || !session.isOpen()){
					session = HibernateUtil.getSessionFactory().openSession();
				} 
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.beginTransaction();
					Query renewquery = session.createQuery("from AssetEntity where serial_number ='"+vin+"'");
					Iterator assetItr = renewquery.list().iterator();
					if(assetItr.hasNext())
					{
						asset = (AssetEntity) assetItr.next();
					}
					if(asset==null)
					{
						Query machNumQ = session.createQuery(" from AssetEntity where machineNumber='"+vin+"'");
						Iterator machNumItr = machNumQ.list().iterator();
						if(machNumItr.hasNext())
						{
							asset = (AssetEntity) machNumItr.next();
						}
					}
					if(asset!=null)
					{
						if(!(asset.getRetrofitFlag()==1))
						{	
						asset.setRenewal_flag(1);
						asset.setRetrofitFlag(1);
						c1.setTime(sdf2.parse(vins.get("renewal_date")));
						renewal_date = new Timestamp(c1.getTime().getTime());
						asset.setRenewal_date(renewal_date);
						AssetRenewalDataEntity assetRenewalObj = new AssetRenewalDataEntity();
						assetRenewalObj.setSerial_number(asset);
						assetRenewalObj.setMode_of_subscription(Integer.parseInt(vins.get("mode_of_subscription")));
						c1.setTime(todayDate);
						updated_on = new Timestamp(c1.getTime().getTime());
						assetRenewalObj.setUpdated_on(updated_on);
						c1.setTime(sdf2.parse(vins.get("subscribed_from")));
						subscribed_from = new Timestamp(c1.getTime().getTime());
						assetRenewalObj.setSubscribed_from(subscribed_from);
						assetRenewalObj.setSubscribed_to(renewal_date); //CR334.n
						CommonUtil utilObj = new CommonUtil();
						contact_id = utilObj.getUserId(vins.get("login_id"));
						Query contactQ = session.createQuery("from ContactEntity where contact_id = '"+contact_id+"'");
						Iterator contactItr = contactQ.list().iterator();
						if(contactItr.hasNext()){
							contact = (ContactEntity) contactItr.next();
						}
						assetRenewalObj.setUpdated_by(contact);
						session.update(asset);
						//session.save(assetRenewalObj);//CR334.o
						session.saveOrUpdate(assetRenewalObj); //CR334.n
						ConnectMySQL connectionObj = new ConnectMySQL();
						con = connectionObj.getEdgeProxyConnection();
						statement = con.createStatement(); 
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
					}
						else
						{
							response=vin+":FAILURE- for "+vin+" retrofitment is already done";
							responses.add(response);
							Result.put("response", responses);
						}
					}
					}
			

			}
				else
				{
					response=vin+":FAILURE- for "+vin+" retrofitment is already done";
					responses.add(response);
					Result.put("response", responses);
				}
		}
				else{
					response=vin+":FAILURE-"+vin+" is not with the Dealer";
					responses.add(response);
					Result.put("response", responses);

				}
			}
				else{
					response=vin+":FAILURE-"+vin+" is not Valid";
					responses.add(response);
					Result.put("response", responses);
				}
			if(session== null || !session.isOpen()){
				session = HibernateUtil.getSessionFactory().openSession();
			} 
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.beginTransaction();
			}}} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception :" + e);
		} finally {
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
		return responses;
	}
	public List<HashMap<String,String>> downloadRetrofitReport(int tenancyId)
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<Integer> accountIdList = new LinkedList<Integer>();
		List<HashMap<String,String>> report = new LinkedList<HashMap<String,String>>();
		List<Integer> tenancyIdList=new LinkedList<Integer>();
		Object[] resultSet = null;
		String dealer = null;
		String zone = null;
		AssetControlUnitEntity acuEntity = null;
		HashMap<String, String> retrofitRecord = null;
		Calendar c1 = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");


		try{
			tenancyIdList.add(tenancyId);
			String accountIdStringList=new DateUtil().getAccountListForTheTenancy(tenancyIdList);
			Query query = session.createQuery("select a.serial_number,a.install_date,ac.account_name,ac.mobile_no,ac.parent_account_id from AssetEntity a,AccountEntity ac,AssetOwnerSnapshotEntity aos where aos.accountId in ("+accountIdStringList+") and a.active_status =1 and a.serial_number = aos.serialNumber and a.primary_owner_id = ac.account_id and a.retrofitFlag=1");
			Iterator assetItr = query.list().iterator();
			iLogger.info("Retrofitment report query :"+query);
			while(assetItr.hasNext()){
				resultSet = (Object[]) assetItr.next();
				AccountEntity dealerAccount = (AccountEntity) resultSet[4];
				if(dealerAccount != null)
					dealer = dealerAccount.getAccount_name();
				else
					dealer = "";
				if(dealerAccount != null && dealerAccount.getParent_account_id() != null)
					zone = dealerAccount.getParent_account_id().getAccount_name();
				else
					zone = "";
				acuEntity = (AssetControlUnitEntity) resultSet[0];
				c1.setTime((Date) resultSet[1]);
				retrofitRecord = new LinkedHashMap<String, String>();
				retrofitRecord.put("serial_number", acuEntity.getSerialNumber());
				retrofitRecord.put("install_date",sdf.format(resultSet[1]));
				retrofitRecord.put("customer_name",(String) resultSet[2]);
				retrofitRecord.put("customer_contact",(String) resultSet[3]);
				retrofitRecord.put("zone", zone);
				retrofitRecord.put("dealer", dealer);
				retrofitRecord.put("IMSI", acuEntity.getSimNo());
				report.add(retrofitRecord);
				}
			

		}catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception :" + e);
		} finally {
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
		return report;
	}
}