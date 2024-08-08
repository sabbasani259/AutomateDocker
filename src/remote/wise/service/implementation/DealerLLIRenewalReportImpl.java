package remote.wise.service.implementation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountTenancyMapping;
import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;


public class DealerLLIRenewalReportImpl {

	@SuppressWarnings("rawtypes")
	public List<HashMap<String,String>> getRenewalApproachingMachines(LinkedHashMap<String,Object> reqObj){

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<HashMap<String,String>> AssetList = new LinkedList<HashMap<String,String>>();

		try{
			AssetControlUnitEntity asset = null;
			Date renewalDate=null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c1 = Calendar.getInstance();
			Date todayDate = new Date();
			Date advanceMonth;
			c1.setTime(todayDate);
			c1.add(Calendar.MONTH,1);
			advanceMonth = c1.getTime();
			long duration,daysDiff;
			Query query1 = null;
			//int dealerAccountId=0;
			AccountTenancyMapping atm = null;

			String reportFilter = (String)reqObj.get("filter_flag");
			int tenancyId = (Integer) reqObj.get("tenancy_id");
			
			/*Query accountQ = session.createQuery("from AccountTenancyMapping where tenancy_id ="+tenancyId);
			Iterator accountItr = accountQ.list().iterator();
			if(accountItr.hasNext()){
				atm = (AccountTenancyMapping) accountItr.next();
				dealerAccountId = atm.getAccount_id().getAccount_id();
				
			}*/
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			
			List<Integer> tenancyIdList=new ArrayList<Integer>();
			
			tenancyIdList.add(tenancyId);
			
			String accountIdListAsSyting=new DateUtil().getAccountListForTheTenancy(tenancyIdList);

			if(reportFilter.equalsIgnoreCase("All"))
				//query1 = session.createQuery("select a.serial_number,a.install_date,a.renewal_date,ac.account_name,ac.mobile_no from AssetEntity a,AccountEntity ac,TenancyBridgeEntity tb,AccountTenancyMapping atm where tb.parentId = "+tenancyId+" and atm.tenancy_id = tb.childId and ac.account_id = atm.account_id and a.primary_owner_id = ac.account_id and  date(a.renewal_date) <= '"+sdf.format(advanceMonth)+"'");
				query1 = session.createQuery("select a.serial_number,a.install_date,a.renewal_date,ac.account_name,ac.mobile_no from AssetEntity a,AccountEntity ac,AssetOwnerSnapshotEntity aos where aos.accountId in ("+accountIdListAsSyting+") and a.active_status =1 and a.serial_number = aos.serialNumber and a.primary_owner_id = ac.account_id and  date(a.renewal_date) <= '"+sdf.format(advanceMonth)+"'");
			else if(reportFilter.equalsIgnoreCase("approachingRenewal"))
				query1 = session.createQuery("select a.serial_number,a.install_date,a.renewal_date,ac.account_name,ac.mobile_no from AssetEntity a,AccountEntity ac,AssetOwnerSnapshotEntity aos where aos.accountId in ("+accountIdListAsSyting+") and a.active_status =1 and a.serial_number = aos.serialNumber and a.primary_owner_id = ac.account_id and date(a.renewal_date) <= '"+sdf.format(advanceMonth)+"' and date(a.renewal_date) >= '"+sdf.format(todayDate)+"'");
			else if(reportFilter.equalsIgnoreCase("overdueRenewal"))
				query1 = session.createQuery("select a.serial_number,a.install_date,a.renewal_date,ac.account_name,ac.mobile_no from AssetEntity a,AccountEntity ac,AssetOwnerSnapshotEntity aos where aos.accountId in ("+accountIdListAsSyting+") and a.active_status =1 and a.serial_number = aos.serialNumber and a.primary_owner_id = ac.account_id and  date(a.renewal_date) <= '"+sdf.format(todayDate)+"'");

			iLogger.info("Executing query :"+query1.getQueryString());

			Iterator assetItr = query1.list().iterator();
			Object[] resultSet = null;

			while(assetItr.hasNext()){
				resultSet = (Object[]) assetItr.next();
				asset = (AssetControlUnitEntity) resultSet[0];
				c1.setTime((Date) resultSet[2]);
				renewalDate = c1.getTime();
				duration = renewalDate.getTime() - todayDate.getTime();
				daysDiff = TimeUnit.MILLISECONDS.toDays(duration);
				HashMap<String, String> renewalApproachingAsset = new LinkedHashMap<String, String>();
				renewalApproachingAsset.put("serial_number", asset.getSerialNumber());
				renewalApproachingAsset.put("install_date",sdf.format(resultSet[1]));
				renewalApproachingAsset.put("renewal_date",sdf.format(resultSet[2]));
				renewalApproachingAsset.put("days_to_renewal", String.valueOf(daysDiff));
				renewalApproachingAsset.put("customer_name",(String) resultSet[3]);
				renewalApproachingAsset.put("customer_contact",(String) resultSet[4]);
				AssetList.add(renewalApproachingAsset);
			}

		}catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("Exception :"+e.getMessage());
		}finally{
			if(session.isOpen()){
				session.close();
			}
		}
		return AssetList;
	}

}
