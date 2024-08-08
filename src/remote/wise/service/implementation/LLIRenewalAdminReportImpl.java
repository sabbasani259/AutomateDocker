package remote.wise.service.implementation;

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
import remote.wise.businessentity.AccountTenancyMapping;
import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.ProductEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;


public class LLIRenewalAdminReportImpl {

	@SuppressWarnings("rawtypes")
	public List<HashMap<String,String>> getLLIRenewalAdminReport(LinkedHashMap<String,Object> reqObj){

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<HashMap<String,String>> LLIAssetList = new LinkedList<HashMap<String,String>>();

		try{


			List tenancyIdList = (List) reqObj.get("tenancyIdList");
			String reportFilter = (String)reqObj.get("filter_flag");
			String period =  (String) reqObj.get("period");
			String UserID=new CommonUtil().getUserId((String)reqObj.get("loginId"));
			//String UserID=(String)reqObj.get("loginId");
			/*ListToStringConversion conversionObj = new ListToStringConversion();
			String tenancyIdStringList = conversionObj.getIntegerListString(
					tenancyIdList).toString();*/
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			Date todayDate = new Date();
			Calendar c1 = Calendar.getInstance();
			AssetControlUnitEntity acuEntity = null;
			String zone = null;
			String dealer = null;
			String profile = null;
			String model = null;
			String queryString=null;
			AccountTenancyMapping atm = null;
			Object[] resultSet = null;
			HashMap<String, String> assetRenewalRecord = null;
			List<Integer> accountIdList = new LinkedList<Integer>();
			
//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			
			
			iLogger.info("Input to the LLIRenewal report loginId "+UserID);
			String accountIdStringList=new DateUtil().getAccountListForTheTenancy(tenancyIdList);

		/*	Query accountQ = session.createQuery("from AccountTenancyMapping where tenancy_id in ("+tenancyIdStringList+")");
			Iterator accountItr = accountQ.list().iterator();
			if(accountItr.hasNext()){
				atm = (AccountTenancyMapping) accountItr.next();
				accountIdList.add(atm.getAccount_id().getAccount_id());
			}

			String accountIdStringList = conversionObj.getIntegerListString(
					accountIdList).toString();*/
		//DF20190703:Abhishek::Implemented logic to fetch the report for machine group vins.
		if(!checkGroupUser(UserID)){	
			//DF20180201 :KO369761 - Machine approaching renewal filter also added in the LLI Renewal report.
			if(reportFilter.equalsIgnoreCase("approachingRenewal")){
				String startDateString = (String) reqObj.get("start_date");
				String endDateString = (String) reqObj.get("end_date");
				Date startDate = sdf.parse(startDateString);
				Date endDate = sdf.parse(endDateString);
				Query query = session.createQuery("select a.serial_number,a.install_date,a.renewal_date,com.customerName,com.dealerName,com.customerMobile,com.dealerAccountId,com.zone,com.model,com.profile,com.pktRecdTS from AssetEntity a,AssetOwnerSnapshotEntity aos,CommReportEnhancedEntity com where aos.accountId in ("+accountIdStringList+") and a.active_status =1 and a.serial_number=com.serialNumber and a.serial_number=aos.serialNumber  and  date(a.renewal_date) <= '"+sdf2.format(endDate)+"' and date(a.renewal_date) >= '"+sdf2.format(startDate)+"'");
				iLogger.info(query);
				Iterator assetItr = query.list().iterator();
				while(assetItr.hasNext()){
					resultSet = (Object[]) assetItr.next();
					
						dealer = (String) resultSet[4];
						zone = (String) resultSet[7];

					acuEntity = (AssetControlUnitEntity) resultSet[0];
					c1.setTime((Date) resultSet[2]);
					assetRenewalRecord = new LinkedHashMap<String, String>();
					assetRenewalRecord.put("serial_number", acuEntity.getSerialNumber());
					if(resultSet[1]!=null)
						assetRenewalRecord.put("install_date",sdf.format(resultSet[1]));
					else
						assetRenewalRecord.put("install_date",(String) resultSet[1]);
					if(resultSet[2]!=null)
						assetRenewalRecord.put("renewal_date",sdf.format(resultSet[2]));
					else
						assetRenewalRecord.put("renewal_date",(String) resultSet[2]);
					assetRenewalRecord.put("customer_name",(String) resultSet[3]);
					assetRenewalRecord.put("customer_contact",(String) resultSet[5]);
					assetRenewalRecord.put("zone", zone);
					assetRenewalRecord.put("dealer", dealer);
					assetRenewalRecord.put("IMSI", acuEntity.getSimNo());
					assetRenewalRecord.put("IMEI", acuEntity.getImeiNo());
					assetRenewalRecord.put("ICCID", acuEntity.getIccidNo());
					assetRenewalRecord.put("model", (String) resultSet[8]);
					assetRenewalRecord.put("profile", (String) resultSet[9]);
					if(resultSet[10]!=null)
						assetRenewalRecord.put("latestPacketReceivedTime",sdf.format(resultSet[10]));
					else
						assetRenewalRecord.put("latestPacketReceivedTime",(String) resultSet[10]);
					
					LLIAssetList.add(assetRenewalRecord);
				}
				return LLIAssetList;
			}

			else if(reportFilter.equalsIgnoreCase("MachinesOverdue")){
				queryString = "select a.serial_number,com.dealerName,com.customerMobile,com.dealerAccountId,a.renewal_date,a.install_date,com.zone,com.customerName,com.model,com.profile,com.pktRecdTS from AssetEntity a,AssetOwnerSnapshotEntity aos,CommReportEnhancedEntity com where aos.accountId in ("+accountIdStringList+") and a.active_status =1 and a.serial_number=com.serialNumber and a.serial_number=aos.serialNumber and ";
			}
			else if(reportFilter.equalsIgnoreCase("MachinesRenewed")){
				//DF20190402 ::Change in requirement,changing the query, taking the vins whose renewalflag=1 and subscribed date<= current date instead of renewal date>currentdate
				//queryString = "select a.serial_number,ac.account_name,ac.mobile_no,ac.parent_account_id,max(ard.subscribed_from),a.install_date,ard.mode_of_subscription from AssetEntity a,AssetRenewalDataEntity ard,AccountEntity ac,AssetOwnerSnapshotEntity aos where aos.accountId in ("+accountIdStringList+") and a.active_status =1 and a.retrofitFlag = 0 and a.serial_number = aos.serialNumber and a.primary_owner_id = ac.account_id and ard.serial_number = a.serial_number and date(a.renewal_date) >'"+sdf2.format(todayDate)+"' and ";
				//DF20210525 PunchedInDate column included for the machines renewed report
				queryString = "select a.serial_number,com.dealerName,com.customerMobile,com.dealerAccountId,max(ard.subscribed_from),a.install_date,com.zone,com.customerName,ard.mode_of_subscription,com.model,com.profile,com.pktRecdTS,max(ard.updated_on)" +
						" from AssetEntity a,AssetRenewalDataEntity ard,AssetOwnerSnapshotEntity aos,CommReportEnhancedEntity com " +
						"where aos.accountId in ("+accountIdStringList+") and a.active_status =1 " +
								"and a.serial_number = aos.serialNumber and ard.serial_number = a.serial_number and a.renewal_flag=1 and a.serial_number=com.serialNumber and "
								+ "ard.subscribed_from != a.install_date and "; //ME100004449.n
                                
			}
			if(reportFilter.equalsIgnoreCase("approachingRenewal")){

			}
		}
		else{
			if(reportFilter.equalsIgnoreCase("approachingRenewal")){
				String startDateString = (String) reqObj.get("start_date");
				String endDateString = (String) reqObj.get("end_date");
				Date startDate = sdf.parse(startDateString);
				Date endDate = sdf.parse(endDateString);
				Query query = session.createQuery("select a.serial_number,a.install_date,a.renewal_date,com.customerName,com.dealerName,com.customerMobile,com.dealerAccountId,com.zone,com.model,com.profile,com.pktRecdTS from AssetEntity a,CustomAssetGroupSnapshotEntity cags,CommReportEnhancedEntity com where cags.user_Id in('"+UserID+"') and a.active_status =1 and a.serial_number = cags.Asset_Id and com.serialNumber=a.serial_number and date(a.renewal_date) <= '"+sdf2.format(endDate)+"' and date(a.renewal_date) >= '"+sdf2.format(startDate)+"'");
				//Query query = session.createQuery("from CustomAssetGroupSnapshotEntity a where a.Asset_Id='HAR155WSP02413852'");
				iLogger.info(query);
				Iterator assetItr = query.list().iterator();
				while(assetItr.hasNext()){
					resultSet = (Object[]) assetItr.next();
					dealer = (String) resultSet[4];
					zone = (String) resultSet[7];

					acuEntity = (AssetControlUnitEntity) resultSet[0];
					c1.setTime((Date) resultSet[2]);
					assetRenewalRecord = new LinkedHashMap<String, String>();
					assetRenewalRecord.put("serial_number", acuEntity.getSerialNumber());
					if(resultSet[1]!=null)
						assetRenewalRecord.put("install_date",sdf.format(resultSet[1]));
					else
						assetRenewalRecord.put("install_date",(String) resultSet[1]);
					if(resultSet[2]!=null)
						assetRenewalRecord.put("renewal_date",sdf.format(resultSet[2]));
					else
						assetRenewalRecord.put("renewal_date",(String) resultSet[2]);
					assetRenewalRecord.put("customer_name",(String) resultSet[3]);
					assetRenewalRecord.put("customer_contact",(String) resultSet[5]);
					assetRenewalRecord.put("zone", zone);
					assetRenewalRecord.put("dealer", dealer);
					assetRenewalRecord.put("IMSI", acuEntity.getSimNo());
					assetRenewalRecord.put("IMEI", acuEntity.getImeiNo());
					assetRenewalRecord.put("ICCID", acuEntity.getIccidNo());
					assetRenewalRecord.put("model", (String) resultSet[8]);
					assetRenewalRecord.put("profile", (String) resultSet[9]);
					if(resultSet[10]!=null)
						assetRenewalRecord.put("latestPacketReceivedTime",sdf.format(resultSet[10]));
					else
						assetRenewalRecord.put("latestPacketReceivedTime",(String) resultSet[10]);
					LLIAssetList.add(assetRenewalRecord);
				}
				return LLIAssetList;
			}
				 

			else if(reportFilter.equalsIgnoreCase("MachinesOverdue")){
				queryString = "select a.serial_number,com.dealerName,com.customerMobile,com.dealerAccountId,a.renewal_date,a.install_date,com.zone,com.customerName,com.model,com.profile,com.pktRecdTS from AssetEntity a,CustomAssetGroupSnapshotEntity cags,CommReportEnhancedEntity com where cags.user_Id in('"+UserID+"') and a.active_status =1 and a.serial_number = cags.Asset_Id  and a.serial_number =com.serialNumber and ";
			}
			else if(reportFilter.equalsIgnoreCase("MachinesRenewed")){
				//DF20190402 ::Change in requirement,changing the query, taking the vins whose renewalflag=1 and subscribed date<= current date instead of renewal date>currentdate
				//queryString = "select a.serial_number,ac.account_name,ac.mobile_no,ac.parent_account_id,max(ard.subscribed_from),a.install_date,ard.mode_of_subscription from AssetEntity a,AssetRenewalDataEntity ard,AccountEntity ac,AssetOwnerSnapshotEntity aos where aos.accountId in ("+accountIdStringList+") and a.active_status =1 and a.retrofitFlag = 0 and a.serial_number = aos.serialNumber and a.primary_owner_id = ac.account_id and ard.serial_number = a.serial_number and date(a.renewal_date) >'"+sdf2.format(todayDate)+"' and ";
				//DF20210525 PunchedInDate column included for the machines renewed report
				queryString = "select a.serial_number,com.dealerName,com.customerMobile,com.dealerAccountId,max(ard.subscribed_from),a.install_date,com.zone,com.customerName,ard.mode_of_subscription,com.model,com.profile,com.pktRecdTS,max(ard.updated_on)" +
						" from AssetEntity a,AssetRenewalDataEntity ard,CustomAssetGroupSnapshotEntity cags,CommReportEnhancedEntity com " +
						"where cags.user_Id in('"+UserID+"') and a.active_status =1 and a.serial_number = cags.Asset_Id " +
								"and ard.serial_number = a.serial_number and a.renewal_flag=1 and a.serial_number=com.serialNumber and "
								+ "ard.subscribed_from != a.install_date and "; //ME100004449.n
			}
			if(reportFilter.equalsIgnoreCase("approachingRenewal")){

			}
			
			
		}

			if(period.equalsIgnoreCase("All")){

				if(reportFilter.equalsIgnoreCase("MachinesOverdue")){
					//DF20190405:mani:vins which are expiring on current date should not be included in the overdue report,only expired vins should be the result
					queryString = queryString+" date(a.renewal_date) < '"+sdf2.format(todayDate)+"'";
				}			  

				else if(reportFilter.equalsIgnoreCase("MachinesRenewed")){
					queryString = queryString+"date(ard.subscribed_from) <= '"+sdf2.format(todayDate)+"'";
				}

			}
			if(period.equalsIgnoreCase("month")){
				String duration = (String) reqObj.get("duration");
				duration = "01/"+duration;
				Date monthStart = sdf.parse(duration);
				c1.setTime(monthStart);
				c1.add(Calendar.DATE,c1.getActualMaximum(Calendar.DAY_OF_MONTH));
				Date monthEnd = c1.getTime();
				
				if(todayDate.compareTo(monthEnd)<0)
					monthEnd = todayDate;
				
				
				//2018-11-02: Mani : Changed the following: date(ard.subscribed_from) < which was date(ard.subscribed_from) <= 
				if(reportFilter.equalsIgnoreCase("MachinesOverdue")){
					queryString = queryString+"  date(a.renewal_date) < '"+sdf2.format(monthEnd)+"' and date(a.renewal_date) >= '"+sdf2.format(monthStart)+"'";
				}
				else if(reportFilter.equalsIgnoreCase("MachinesRenewed")){
					//DF20210423 Avinash Xavier A MachinesRenwed Report when fetched with custom date filter shouldbe based on Renewal updated date
					queryString = queryString+"  date(ard.updated_on) < '"+sdf2.format(monthEnd)+"' and date(ard.updated_on) >= '"+sdf2.format(monthStart)+"'";
				}
			}
		 
			else if(period.equalsIgnoreCase("week")){
				String duration = (String) reqObj.get("duration");
				Date weekEnd = sdf.parse(duration);
				c1.setTime(weekEnd);
				c1.add(Calendar.DATE,-6);
				Date weekStart = c1.getTime();
				if(reportFilter.equalsIgnoreCase("MachinesOverdue")){
					//DF20190405:mani:vins which are expiring on current date should not be included in the overdue report,only expired vins should be the result
					queryString = queryString+"  date(a.renewal_date) < '"+sdf2.format(weekEnd)+"' and date(a.renewal_date) >= '"+sdf2.format(weekStart)+"'";
				}
					else if(reportFilter.equalsIgnoreCase("MachinesRenewed"))
						//DF20210423 Avinash Xavier A MachinesRenwed Report when fetched with custom date filter shouldbe based on Renewal updated date
						queryString = queryString+"  date(ard.updated_on) <= '"+sdf2.format(weekEnd)+"' and date(ard.updated_on) >= '"+sdf2.format(weekStart)+"'";
			}
			else if(period.equalsIgnoreCase("date")){
				String startDateString = (String) reqObj.get("start_date");
				String endDateString = (String) reqObj.get("end_date");
				Date startDate = sdf.parse(startDateString);
				Date endDate = sdf.parse(endDateString);
				if(reportFilter.equalsIgnoreCase("MachinesOverdue")){
					//DF20190405:mani:vins which are expiring on current date should not be included in the overdue report,only expired vins should be the result
					queryString = queryString+"  date(a.renewal_date) < '"+sdf2.format(endDate)+"' and date(a.renewal_date) >= '"+sdf2.format(startDate)+"'";//DF20210525 mounika:limit clause was removed from query since it's altering the query sequence
				}
			else if(reportFilter.equalsIgnoreCase("MachinesRenewed"))
					//DF20210423 Avinash Xavier A MachinesRenwed Report when fetched with custom date filter shouldbe based on Renewal updated date
					queryString = queryString+"   date(ard.updated_on) <= '"+sdf2.format(endDate)+"' and date(ard.updated_on) >= '"+sdf2.format(startDate)+"'";//DF20210525 mounika:limit clause was removed from query since it's altering the query sequence
			}
			
			if(reportFilter.equalsIgnoreCase("MachinesRenewed")){
				queryString = queryString+" group by ard.serial_number";
			}


			//Query query = session.createQuery("select ac.account_name,ac.mobile_no,ac.parent_account_id from AccountEntity ac,TenancyBridgeEntity tb,AccountTenancyMapping atm where tb.parentId in ("+tenancyIdStringList+") and atm.tenancy_id = tb.childId and ac.account_id = atm.account_id");
			iLogger.info("Executing query :"+queryString);
			Query query = session.createQuery(queryString);
			
			//long eT3 = System.currentTimeMillis();
			Iterator assetItr = query.list().iterator();
			//long eT4 = System.currentTimeMillis();
			//System.out.println("Query execution took " + (eT4-eT3) +" ms");
			SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
			iLogger.info("Query Execution Completed " + sdf3.format(new Date()));
			
			while(assetItr.hasNext()){
				resultSet = (Object[]) assetItr.next();
					dealer = (String) resultSet[1];
					zone = (String) resultSet[6];
				acuEntity = (AssetControlUnitEntity) resultSet[0];

				assetRenewalRecord = new LinkedHashMap<String, String>();
				assetRenewalRecord.put("serial_number",acuEntity.getSerialNumber());
				assetRenewalRecord.put("zone", zone);
				assetRenewalRecord.put("dealer", dealer);
				assetRenewalRecord.put("customer_name",(String)resultSet[7]);
				assetRenewalRecord.put("customer_contact",(String) resultSet[2]);
				if(resultSet[5]!=null)
					assetRenewalRecord.put("install_date",sdf.format(resultSet[5]));
				else
					assetRenewalRecord.put("install_date",(String) resultSet[5]);
				assetRenewalRecord.put("IMSI", acuEntity.getSimNo());
				assetRenewalRecord.put("IMEI", acuEntity.getImeiNo());
				assetRenewalRecord.put("ICCID", acuEntity.getIccidNo());
				
				if(!reportFilter.equalsIgnoreCase("MachinesRenewed"))
				{
				assetRenewalRecord.put("model", (String) resultSet[8]);
				assetRenewalRecord.put("profile", (String) resultSet[9]);
				if(resultSet[10]!=null)
					assetRenewalRecord.put("latestPacketReceivedTime",sdf.format(resultSet[10]));
				else
					assetRenewalRecord.put("latestPacketReceivedTime",(String) resultSet[10]);
				if(reportFilter.equalsIgnoreCase("MachinesOverdue"))
					assetRenewalRecord.put("OverDueRenewalDate", sdf.format(resultSet[4]));
				}
				
				else{
					assetRenewalRecord.put("model", (String) resultSet[9]);
					assetRenewalRecord.put("profile", (String) resultSet[10]);
					if(resultSet[11]!=null)
						assetRenewalRecord.put("latestPacketReceivedTime",sdf.format(resultSet[11]));
					else
						assetRenewalRecord.put("latestPacketReceivedTime",(String) resultSet[11]);
					assetRenewalRecord.put("renewedDate", sdf.format(resultSet[4]));
					//DF20210525 PunchedInDate column included for the machines renewed report
					assetRenewalRecord.put("PunchedInDate", sdf.format(resultSet[12]));
					assetRenewalRecord.put("mode_of_subscription", String.valueOf(resultSet[8]));
					}
				LLIAssetList.add(assetRenewalRecord);
				}
				
				
				
				
				/*if(reportFilter.equalsIgnoreCase("MachinesOverdue"))
					assetRenewalRecord.put("OverDueRenewalDate", sdf.format(resultSet[4]));
				else{
					assetRenewalRecord.put("renewedDate", sdf.format(resultSet[4]));
					assetRenewalRecord.put("mode_of_subscription", String.valueOf(resultSet[6]));
				
				}

				LLIAssetList.add(assetRenewalRecord);*/	
			
			//long eT5 = System.currentTimeMillis();
			//System.out.println("Data fetching took " + (eT5-eT4) +" ms");
			iLogger.info("Other Data Retrival Completed " + sdf3.format(new Date()));
		}catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("Exception :"+e.getMessage());
		}finally{
			if(session.isOpen()){
				session.close();
			}
		}
		return LLIAssetList;
	}

	private boolean checkGroupUser(String loginId) {
		Session session=null;
		boolean status=false;
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		try{
		List<Integer> groupIdList = new LinkedList<Integer>();
		 
		session = HibernateUtil.getSessionFactory().openSession();
		Query queryGroupUser = session.createQuery("from GroupUserMapping where contact_id ='"+loginId+"'");
		Iterator groupUserItr = queryGroupUser.list().iterator();
		status= groupUserItr.hasNext();
		}
		catch(Exception e)
		{
			fLogger.fatal(" Exception in retriving data from Group user:"+e);
			
		}
		finally
		{
			try
			{
			if(session!=null && session.isOpen())
			{
				session.close();
			}
			}
			catch(Exception e){
				fLogger.fatal("Exception in closing the AssetDashboard session::"+e);
			}
		
		}
		long endTime = System.currentTimeMillis();
		iLogger.info("Time required to fetch the record from group user for loginId::"+loginId+" in ms :"+(endTime-startTime));
		return status;
	}
	

}
