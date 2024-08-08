package remote.wise.businessobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.EventTypeEntity;
import remote.wise.businessentity.TenancyDimensionEntity;
import remote.wise.dal.DynamicAMS_DAL;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AmsDAO;
import remote.wise.service.datacontract.AssetServiceScheduleGetReqContract;
import remote.wise.service.datacontract.AssetServiceScheduleGetRespContract;
import remote.wise.service.implementation.AssetServiceScheduleImpl;
import remote.wise.service.implementation.CustomerRequiringImpl;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;

/**
 * CustomerRequiringBO will allow to get Customers and related assets that
 * pending for Service under given Dealer
 * 
 * @author jgupta41
 * 
 */
public class CustomerRequiringBO {


	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("CustomerRequiringBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("CustomerRequiringBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("CustomerRequiringBO:","info");*/


	/* Commented by Juhi on 12 september 2013 for defect id :1245


	static Logger fatalError = Logger.getLogger("fatalErrorLogger");
	static Logger businessError = Logger.getLogger("businessErrorLogger");
	static Logger infoLogger = Logger.getLogger("infoLogger");

	// ******************************Get the customers which requires service
	// under given List of LoginTenancy_ID*****************
	 *//**
	 * This method will return List of Customers and related assets that pending
	 * for Service for given List of LoginTenancy_ID
	 * 
	 * @param LoginTenancy_IDList
	 *            :List of Tenancy_ID
	 * @return customerRequiringImplList:Returns List of Customers and related
	 *         assets that pending for Service
	 * @throws CustomFault
	 *//*
	@SuppressWarnings({ "rawtypes", "unused" })
	public List<CustomerRequiringImpl> getCustomerRequiring(
			List<Integer> LoginTenancy_IDList) throws CustomFault {
		List<CustomerRequiringImpl> customerRequiringImplList = new LinkedList<CustomerRequiringImpl>();
		ListToStringConversion conversionObj = new ListToStringConversion();
		String LoginTenancy_IDList1 = conversionObj.getIntegerListString(
				LoginTenancy_IDList).toString();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			Query query = session
					.createQuery("from TenancyDimensionEntity where tenancyId in ( "
							+ LoginTenancy_IDList1
							+ " ) and tenancyTypeName like 'Dealer%'");
			Iterator itr = query.list().iterator();
			List<Integer> tenancyIdList = new LinkedList<Integer>();
			int tenancyId = 0;
			while (itr.hasNext()) {
				TenancyDimensionEntity tenancyDimensionEntity = (TenancyDimensionEntity) itr
						.next();
				infoLogger.info("Dealer Logined");
				tenancyId = tenancyDimensionEntity.getTenancyId();
				infoLogger.info(" tenancyID" + tenancyId);
				tenancyIdList.add(tenancyId);
			}

			String tenancyIdStringList = conversionObj.getIntegerListString(
					tenancyIdList).toString();
			String DealerName = null;
			String queryString5 = "select account_name from AccountEntity where status=true and account_id in(select account_id from AccountTenancyMapping where tenancy_id in('"
					+ tenancyIdStringList + "'))";
			Object res[] = null;
			Query query1 = session.createQuery(queryString5);
			Iterator itr1 = query1.list().iterator();
			while (itr1.hasNext()) {
				DealerName = (String) itr1.next();
			}
			List<String> serialList = new LinkedList<String>();
			String queryString2 = "select a.serialNumber,f.account_name, f.mobile_no "
					+ "from AssetEventEntity a, AssetAccountMapping e, AccountEntity f "
					+ "where a.activeStatus=1 and f.status=true and a.serialNumber in "
					+ "(select b.serialNumber from AssetAccountMapping b ,TenancyBridgeEntity c ,AccountTenancyMapping d"
					+ "	where c.parentId in ('"
					+ tenancyIdStringList
					+ "') and d.tenancy_id=c.childId and c.level<>0 and b.accountId=d.account_id group by b.serialNumber )"
					+ " and a.eventTypeId=1 and a.eventSeverity like 'RED'"
					+ "and e.serialNumber=a.serialNumber and f.account_id=e.accountId group by e.serialNumber";
			Query query2 = session.createQuery(queryString2);
			Iterator itr2 = query2.list().iterator();
			Object result4[] = null;
			while (itr2.hasNext()) {
				result4 = (Object[]) itr2.next();
				AssetEntity asset = (AssetEntity) result4[0];
				serialList.add(asset.getSerial_number().getSerialNumber());
			}
			infoLogger.info(" serialList" + serialList.size());
			String serialStringList = conversionObj.getStringList(serialList)
					.toString();
			infoLogger.info(" serialStringList" + serialStringList);

			infoLogger
					.info(" *******Get maximum Parameter id for given Engine hours*********************");
			Query query3 = session
					.createQuery("select max(d.parameterId ) from MonitoringParameters d where d.parameterName like 'Hour'");
			Iterator itr3 = query3.list().iterator();
			int parameterId2 = 0;
			while (itr3.hasNext()) {
				parameterId2 = (Integer) itr3.next();
				infoLogger.info(" parameterId for EngineHours is "
						+ parameterId2);
			}

			infoLogger.info(" CurrentEngineHours");
			double currentHours = 0;
			List<Double> currentHoursList = new LinkedList<Double>();
			if ((serialList.size()) > 0 || (!serialList.isEmpty())) {
				String queryString = "select b.serialNumber,a.parameterValue from AssetMonitoringDetailEntity a ,AssetMonitoringHeaderEntity b where a.transactionNumber in(select max(c.transactionNumber )from AssetMonitoringHeaderEntity c where c.serialNumber in ("
						+ serialStringList
						+ ") group by  c.serialNumber)and a.parameterId = "
						+ parameterId2
						+ " and b.transactionNumber=a.transactionNumber ";

				Query query4 = session.createQuery(queryString);

				Object result[] = null;
				Iterator itr4 = query4.list().iterator();
				while (itr4.hasNext()) {
					result = (Object[]) itr4.next();

					currentHours = Double.parseDouble(result[1].toString());

					currentHoursList.add(currentHours);
				}
				infoLogger.info(" currentHoursList" + currentHoursList.size());

				infoLogger.info(" ScheduleEngineHours");
				List<Double> ScheduleHoursList = new LinkedList<Double>();

	  * String queryString1=
	  * "select i.serialNumber, h.engineHoursSchedule from ServiceScheduleEntity h,ServiceHistoryEntity i where h.serviceScheduleId in"
	  * +
	  * "(select c.serviceScheduleId+1 from ServiceScheduleEntity c,ServiceHistoryEntity d where c.serviceName in "
	  * +
	  * "(select e.serviceName from ServiceHistoryEntity e where e.serviceDate in "
	  * +
	  * "(select max(f.serviceDate) from ServiceHistoryEntity f where f.serialNumber in ("
	  * +serialStringList+") group by f.serialNumber))" +
	  * "and c.scheduleName in" +
	  * "(select g.ScheduleName from ServiceHistoryEntity g where g.serviceDate in"
	  * +
	  * "(select max(f.serviceDate)	from ServiceHistoryEntity f where f.serialNumber in ("
	  * +serialStringList+")group by f.serialNumber))" +
	  * " and c.productId in(select a.productId from AssetEntity a, ProductEntity b where a.serial_number in("
	  * +serialStringList+
	  * ") and a.productId=b.productId group by  a.serial_number ) group by c.serviceScheduleId) and i.serialNumber in("
	  * +serialStringList+")group by i.serialNumber";



	  * String queryString1=
	  * "select i.serialNumber, h.engineHoursSchedule from ServiceScheduleEntity h,ServiceHistoryEntity i where h.serviceScheduleId in"
	  * +
	  * "(select c.serviceScheduleId from ServiceScheduleEntity c,ServiceHistoryEntity d where c.serviceName in "
	  * +
	  * "(select e.serviceName from ServiceHistoryEntity e where e.serviceDate in "
	  * +
	  * "(select max(f.serviceDate) from ServiceHistoryEntity f where f.serialNumber in ("
	  * +serialStringList+") group by f.serialNumber))" +
	  * "and c.scheduleName in" +
	  * "(select g.ScheduleName from ServiceHistoryEntity g where g.serviceDate in"
	  * +
	  * "(select max(f.serviceDate)	from ServiceHistoryEntity f where f.serialNumber in ("
	  * +serialStringList+")group by f.serialNumber))" +
	  * " and c.assetTypeId in (select b.assetTypeId from AssetEntity a, ProductEntity b where a.serial_number in("
	  * +serialStringList+
	  * ") and a.productId=b.productId group by  a.serial_number) " +
	  * " and c.engineTypeId in (select b.engineTypeId from AssetEntity a, ProductEntity b where a.serial_number in("
	  * +serialStringList+
	  * ") and a.productId=b.productId group by  a.serial_number )" +
	  * " and c.assetGroupId in (select b.assetGroupId from AssetEntity a, ProductEntity b where a.serial_number in("
	  * +serialStringList+
	  * ") and a.productId=b.productId group by  a.serial_number ) group by c.serviceScheduleId) and i.serialNumber in("
	  * +serialStringList+")group by i.serialNumber";


	  * query select max(b.EngineHours_Schedule), a.serialNumber from
	  * service_history a, service_schedule b where a.serialNumber
	  * IN( 'VINHAR2DX01E01487521' ,'VINHAR3DXSSE31234567')and
	  * b.serviceName = a.serviceName and b.scheduleName =
	  * a.scheduleName group by a.serialNumber


	  * String
	  * queryString1="SELECT a.serialNumber,MAX(b.engineHoursSchedule) "
	  * + " FROM ServiceHistoryEntity a,ServiceScheduleEntity b "+
	  * " WHERE a.serialNumber in ("+serialStringList+
	  * " ) AND b.serviceName = a.serviceName AND b.scheduleName = a.ScheduleName"
	  * + " GROUP BY a.serialNumber";


				// Changes done by Juhi on 29-july-2013 Logic added to get
				// Scheduled hour for given serial number list ----------

				AssetServiceScheduleGetReqContract reqObj = new AssetServiceScheduleGetReqContract();
				List<Integer> serviceScheduleIdList = new LinkedList<Integer>();
				for (int i = 0; i < serialList.size(); i++) {
					reqObj.setSerialNumber(serialList.get(i));

					List<AssetServiceScheduleGetRespContract> respobj = new AssetServiceScheduleImpl()
							.getAssetserviceSchedule(reqObj);

					for (int sch = 0; sch < respobj.size(); sch++) {

						// change done on 31 july 2013 by Juhi
						// Check if HoursToNextService is present ,if yes add it
						// to serviceScheduleId List
						if (respobj.get(sch).getHoursToNextService() != null) {
							serviceScheduleIdList.add(respobj.get(sch)
									.getServiceScheduleId());
						}

					}
				}
				String serviceScheduleIdStringList = conversionObj
						.getIntegerListString(serviceScheduleIdList).toString();
				String queryString1 = "SELECT b.engineHoursSchedule "
						+ " FROM ServiceScheduleEntity b "
						+ " where  b.serviceScheduleId in("
						+ serviceScheduleIdStringList + ") "
						+ " GROUP BY b.serviceScheduleId";


	  * String
	  * queryString1="SELECT a.serialNumber,MAX(b.engineHoursSchedule) "
	  * + " FROM AssetEventEntity a,ServiceScheduleEntity b "+
	  * " WHERE a.serialNumber in ("+serialStringList+" ) "+
	  * " GROUP BY a.serialNumber";


				Query query5 = session.createQuery(queryString1);
				// Object result1[]=null;
				Iterator itr5 = query5.list().iterator();
				while (itr5.hasNext()) {
					long result1 = (Long) itr5.next();
					// ScheduleHoursList.add((double)((Long)result1[0]));
					ScheduleHoursList.add((double) result1);
				}
				infoLogger
						.info(" ScheduleHoursList" + ScheduleHoursList.size());

				List<Double> OverDueHoursList = new LinkedList<Double>();
				for (int i = 0; i < serialList.size(); i++) {
					if (currentHoursList.size() >= i + 1
							&& ScheduleHoursList.size() >= i + 1) {

						double differenceHours = (currentHoursList.get(i))
								- (ScheduleHoursList.get(i));

						OverDueHoursList.add(differenceHours);

					}
				}

				int evtTypeId1 = 0;
				int ActiveAlert = 1;

				String string1 = "from EventTypeEntity where eventTypeName LIKE 'Service'";
				Query query6 = session.createQuery(string1);
				Iterator itr6 = query6.list().iterator();
				while (itr6.hasNext()) {
					EventTypeEntity eventTypeEntity1 = (EventTypeEntity) itr6
							.next();
					evtTypeId1 = eventTypeEntity1.getEventTypeId();
					infoLogger.info(" evtTypeId  +++++" + evtTypeId1);
				}

				List<Long> ActiveAlertList = new LinkedList<Long>();
				Query query7 = session
						.createQuery(" select count(activeStatus) from AssetEventEntity where eventTypeId="
								+ evtTypeId1
								+ " and activeStatus="
								+ ActiveAlert
								+ " and eventSeverity like 'RED' and serialNumber in ("
								+ serialStringList + " ) group by serialNumber");
				Iterator itr7 = query7.list().iterator();
				while (itr7.hasNext()) {
					ActiveAlertList.add((Long) itr7.next());
				}
				List<Long> OtherAlertList = new LinkedList<Long>();
				Query query8 = session
						.createQuery(" select count(activeStatus) from AssetEventEntity where eventTypeId!="
								+ evtTypeId1
								+ " and activeStatus="
								+ ActiveAlert
								+ "  and serialNumber in ("
								+ serialStringList + " ) group by serialNumber");
				Iterator itr8 = query8.list().iterator();
				while (itr8.hasNext()) {
					OtherAlertList.add((Long) itr8.next());
				}
				String MachineServiceDetails = null;
				List<String> MachineServiceDetailsList = new LinkedList<String>();

				for (int j = 0; j < serialList.size(); j++) {
					// if(serialList.size()>0 && OverDueHoursList.size()>0 &&
					// ActiveAlertList.size()>0 && OtherAlertList.size()>0){
					if (serialList.size() >= j + 1
							&& OverDueHoursList.size() >= j + 1
							&& ActiveAlertList.size() >= j + 1
							&& OtherAlertList.size() >= j + 1) {
						MachineServiceDetails = serialList.get(j) + ","
								+ OverDueHoursList.get(j) + ","
								+ ActiveAlertList.get(j) + ","
								+ OtherAlertList.get(j);
						MachineServiceDetailsList.add(MachineServiceDetails);
					}
				}

				// CustomerRequiringImpl customerRequiringImpl=new
				// CustomerRequiringImpl();
				// customerRequiringImpl.setMachineServiceDetails(MachineServiceDetailsList);
				// customerRequiringImpl.setDealerName(DealerName);
				// customerRequiringImplList.add(customerRequiringImpl);

				String queryString3 = "select a.accountId,count( a.accountId) from AssetAccountMapping a where a.serialNumber in ("
						+ serialStringList + ")group by a.accountId";
				Object result3[] = null;
				Query query9 = session.createQuery(queryString3);
				List<Integer> accountIdList = new LinkedList<Integer>();
				List<Integer> NumberOfMachineDueList = new LinkedList<Integer>();
				Iterator itr9 = query9.list().iterator();
				while (itr9.hasNext()) {
					result3 = (Object[]) itr9.next();
					AccountEntity account = (AccountEntity) result3[0];
					accountIdList.add(account.getAccount_id());
					Integer number = ((Long) result3[1]).intValue();
					NumberOfMachineDueList.add(number);
				}
				String accountIdStringList = conversionObj
						.getIntegerListString(accountIdList).toString();
				Query query10 = session
						.createQuery("select account_name,mobile_no from AccountEntity where status=true and account_id in("
								+ accountIdStringList + ")group by account_id");
				List<String> customerNameList = new LinkedList<String>();
				List<String> contactList = new LinkedList<String>();
				Object result2[] = null;
				Iterator itr10 = query10.list().iterator();
				while (itr10.hasNext()) {
					result2 = (Object[]) itr10.next();
					customerNameList.add((String) result2[0]);
					contactList.add((String) result2[1]);
				}
				for (int i = 0; i < customerNameList.size(); i++) {
					CustomerRequiringImpl customerRequiringImpl1 = new CustomerRequiringImpl();
					customerRequiringImpl1.setCustomerContactNumber(contactList
							.get(i));
					customerRequiringImpl1.setCustomerName(customerNameList
							.get(i));
					customerRequiringImpl1
							.setMachineServiceDetails(MachineServiceDetailsList);
					customerRequiringImpl1.setDealerName(DealerName);
					customerRequiringImpl1
							.setNumberOfMachineDueForService(NumberOfMachineDueList
									.get(i));
					customerRequiringImplList.add(customerRequiringImpl1);

				}
			}
		}

	  * catch(Exception e){
	  * 
	  * fatalError.fatal("Hello this is an Fatal Error. Need immediate Action"
	  * +e.getMessage());
	  * 
	  * }

		finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}
		return customerRequiringImplList;
	}

	// ******************************End of Get the customers which requires
	// service under given List of LoginTenancy_ID*****************

	  */

	// ******************************Get the customers which requires service
	// under given List of LoginTenancy_ID*****************
	/**
	 * This method will return List of Customers and related assets that pending
	 * for Service for given List of LoginTenancy_ID
	 * 
	 * @param LoginTenancy_IDList
	 *            :List of Tenancy_ID
	 * @return customerRequiringImplList:Returns List of Customers and related
	 *         assets that pending for Service
	 * @throws CustomFault
	 */
	@SuppressWarnings({ "rawtypes", "unused" })

	//DF20160729 @Roopa Calling CustomerRequiring Service new method with Query tweaking 
	
	public List<CustomerRequiringImpl> getCustomerRequiring_new(
			List<Integer> LoginTenancy_IDList) throws CustomFault {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		List<CustomerRequiringImpl> customerRequiringImplList = new LinkedList<CustomerRequiringImpl>();	

		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;

		String basicQueryString=null;

		Session session = HibernateUtil.getSessionFactory().openSession();
		try{

			AccountEntity account=null;

			
			
			Query accountQ = session.createQuery(" select at.account_id from AccountTenancyMapping at where at.tenancy_id="+LoginTenancy_IDList.get(0)+" ");
			Iterator accItr = accountQ.list().iterator();
			if(accItr.hasNext())
			{
				account = (AccountEntity)accItr.next();
			}

			if(session!=null && session.isOpen())
			{
				session.close();
			}
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			

			
			String accountIdListAsString=new DateUtil().getAccountListForTheTenancy(LoginTenancy_IDList);

			/*String SelectQuery="select aos.Serial_Number, acc.Account_ID, acc.account_name, acc.Mobile, sdr.next_service_hours as nsh, sdr.next_service_date as nsd," +
					" substring_index(substring_index(parameters,'|',4),'|',-1) as cmh," +
					" count(ae.serial_number) as otheralertcount";

			String FromQuery=" from (select Serial_Number from asset_owner_snapshot where account_id="+account.getAccount_id()+") aos" + 
					" inner join service_details_report sdr" +
					" on sdr.Serial_Number=aos.Serial_Number" +
					" inner join asset a" +
					" on a.Serial_Number=aos.Serial_Number" +
					" inner join account acc" +
					" on acc.account_id=a.Primary_Owner_ID" +
					" inner join asset_monitoring_snapshot_new ams" +
					" on ams.serial_number = aos.Serial_Number" +
					" left outer join asset_event ae" +
					" on ae.serial_number = aos.serial_number" +
					" and ae.active_status = 1" +
					" and ae.event_type_id = 2";*/
			
			//DF20161222 @Roopa changing asset_monitoring_snapshot_new to asset_monitoring_snapshot which is having txndata as json column
			/*String SelectQuery="select aos.Serial_Number, acc.Account_ID, acc.account_name, acc.Mobile, sdr.next_service_hours as nsh, sdr.next_service_date as nsd," +
					" ams.TxnData," +
					" count(ae.serial_number) as otheralertcount";
			String FromQuery=" from (select Serial_Number from asset_owner_snapshot where account_id in ("+accountIdListAsString+")) aos" + 
					" inner join service_details_report sdr" +
					" on sdr.Serial_Number=aos.Serial_Number" +
					" inner join asset a" +
					" on a.Serial_Number=aos.Serial_Number" +
					" inner join account acc" +
					" on acc.account_id=a.Primary_Owner_ID" +
					" inner join asset_monitoring_snapshot ams" +
					" on ams.serial_number = aos.Serial_Number" +
					" left outer join asset_event ae" +
					" on ae.serial_number = aos.serial_number" +
					" and ae.active_status = 1 and ae.PartitionKey =1" +
					" and ae.event_type_id = 2";*/
			
			//DF20190205-KO369761-Above query commented and modified as below query for better performance
			String SelectQuery = "select aos.Serial_Number, acc.Account_ID, acc.account_name, acc.Mobile, " +
					" sdr.next_service_hours as nsh, sdr.next_service_date as nsd, " +
					" ams.TxnData, count(ae.serial_number) as otheralertcount";

			String FromQuery = " from (select distinct Serial_Number from asset_owner_snapshot where account_id in ("+ accountIdListAsString+ ")) aos" +
					" inner join service_details_report sdr on sdr.Serial_Number=aos.Serial_Number" +
					" inner join asset a on a.Serial_Number=aos.Serial_Number " +
					" inner join account acc on acc.account_id=a.Primary_Owner_ID " +
					" inner join asset_monitoring_snapshot ams on ams.serial_number = aos.Serial_Number " +
					" left outer join asset_event ae on ae.serial_number = aos.serial_number and " +
					" ae.active_status = 1 and ae.PartitionKey = 1 and ae.event_type_id = 2 order by acc.Account_ID";

			String groupByQuery="";
			
			String orderByQuery="";

			basicQueryString = SelectQuery + FromQuery + groupByQuery + orderByQuery;

			//System.out.println("basicQueryString::"+basicQueryString);

			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery(basicQueryString);

			int prevAccountId=0;
			int assetCounter=0;
			int accountID = 0;
			int otherAlertCount = 0;
			int ActiveAlerts = 0;

			long diff = 0;
			long diffDays = 0;

			double diffHours = 0.0;
			double nsh = 0.0;
			double cmh = 0.0;

			String mobileNumber = null;
			String custName = null;
			String nshString = null;
			String cmhString = null;
			String nsdString = null;
			String MachineServiceDetails = null;
			
			// Get the date today using Calendar object.

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date today = Calendar.getInstance().getTime();
			String cuurDayString = df.format(today);
			Date cuurDay = df.parse(cuurDayString);
			Date NextSeviceDate = null;
			String txnData;
			
			HashMap<String,String> txnDataMap=new HashMap<String, String>();

			List<String> MachineServiceDetailsList = new LinkedList<String>();

			CustomerRequiringImpl implObj = new CustomerRequiringImpl();

			while(rs.next()){

				accountID = rs.getInt("Account_ID");

				if(prevAccountId!=0 && (prevAccountId!=accountID)){

					implObj = new CustomerRequiringImpl();

					implObj.setCustomerContactNumber(mobileNumber);
					implObj.setCustomerName(custName);
					implObj.setDealerName(account.getAccount_name());
					implObj.setNumberOfMachineDueForService(assetCounter);
					implObj.setMachineServiceDetails(MachineServiceDetailsList);

					customerRequiringImplList.add(implObj);

					MachineServiceDetailsList = new LinkedList<String>();
					assetCounter =0;
				}

				nsdString = String.valueOf(rs.getTimestamp("nsd"));
				nshString=rs.getString("nsh");
				
				txnData=rs.getObject("TxnData").toString();
				
				if(txnData!=null)
					txnDataMap = new Gson().fromJson(txnData, new TypeToken<HashMap<String, Object>>() {}.getType());
				
				cmhString =txnDataMap.get("CMH");
				otherAlertCount = rs.getInt("otheralertcount");

				if((nsdString==null || nshString==null) && otherAlertCount==0){
					continue;
				}

				if(nsdString !=null && nshString!=null){
					NextSeviceDate = df.parse(nsdString);
					diff = NextSeviceDate.getTime() - cuurDay.getTime();
					diffDays = diff / (24 * 60 * 60 * 1000);
					nsh = Double.parseDouble(nshString);
					if ( cmhString == null ) {
						cmh = 0.0;
					} else {
						cmh = Double.parseDouble(cmhString);
					}
					diffHours = nsh - cmh ;
					ActiveAlerts = 1;
					if ( diffDays > 7 && diffHours > 50.0) {
						ActiveAlerts = 0;
					} else {
						ActiveAlerts = 1;
					}
				}
				else {
					ActiveAlerts = 0;
					diffHours = 0.0;
				}

				if( (ActiveAlerts == 0) && (otherAlertCount==0)){
					continue;
				}

				MachineServiceDetails = rs.getString("Serial_Number") + "," + diffHours + ","+ ActiveAlerts+ ","+ otherAlertCount;
				MachineServiceDetailsList.add(MachineServiceDetails);

				assetCounter = assetCounter + 1;
				prevAccountId = accountID;
				mobileNumber = rs.getString("Mobile");
				custName= rs.getString("account_name");
			}

			implObj = new CustomerRequiringImpl();
			implObj.setCustomerContactNumber(mobileNumber);
			implObj.setCustomerName(custName);
			implObj.setDealerName(account.getAccount_name());
			implObj.setNumberOfMachineDueForService(assetCounter);
			implObj.setMachineServiceDetails(MachineServiceDetailsList);

			customerRequiringImplList.add(implObj);
		}

		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception :"+e.getMessage());
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

		return customerRequiringImplList;
	}
	public List<CustomerRequiringImpl> getCustomerRequiring(
			List<Integer> LoginTenancy_IDList) throws CustomFault {

		Logger iLogger = InfoLoggerClass.logger;

		List<CustomerRequiringImpl> customerRequiringImplList = new LinkedList<CustomerRequiringImpl>();
		ListToStringConversion conversionObj = new ListToStringConversion();
		String LoginTenancy_IDList1 = conversionObj.getIntegerListString(
				LoginTenancy_IDList).toString();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		session.beginTransaction();
		try {

			// int tenancyId = new Integer(LoginTenancy_IDList1).intValue();
			Query query = session
					.createQuery("from TenancyDimensionEntity where tenancyId in ( "
							+ LoginTenancy_IDList1
							+ " ) and tenancyTypeName like 'Dealer%'");


			Iterator itr = query.list().iterator();

			List<Integer> tenancyIdList = new LinkedList<Integer>();
			int tenancyId = 0;
			while (itr.hasNext()) {

				TenancyDimensionEntity tenancyDimensionEntity = (TenancyDimensionEntity) itr
						.next();
				iLogger.info("Dealer Logined");
				tenancyId = tenancyDimensionEntity.getTenancyId();

				iLogger.info(" tenancyID" + tenancyId);
				tenancyIdList.add(tenancyId);
			}

			String tenancyIdStringList = conversionObj.getIntegerListString(
					tenancyIdList).toString();
			String DealerName = null;
			String queryString5 = "select account_name from AccountEntity where status=true and account_id in(select account_id from AccountTenancyMapping where tenancy_id in('"
					+ tenancyIdStringList + "'))";
			Object res[] = null;
			Query query1 = session.createQuery(queryString5);
			Iterator itr1 = query1.list().iterator();
			while (itr1.hasNext()) {
				DealerName = (String) itr1.next();

			}

			//added by S Suresh defectId 20151028
			//getting Serial Numbers of all customer machines for a login tenancy seperately instead sub query to improve performance 
			Query serialListSubQuery = session.createQuery("select b.serialNumber from AssetAccountMapping b ,TenancyBridgeEntity c ,AccountTenancyMapping d"
					+ "   where c.parentId in ('"
					+ tenancyIdStringList 
					+ "') and c.parentId != c.childId and d.tenancy_id=c.childId and c.level<>0 and b.accountId=d.account_id group by b.serialNumber )");
			Iterator subQueryItr = serialListSubQuery.list().iterator();
			List<String> subQuerySerialList = new LinkedList<String>();
			while(subQueryItr.hasNext())
			{
				AssetEntity asset = (AssetEntity)subQueryItr.next();
				subQuerySerialList.add(asset.getSerial_number().getSerialNumber());
			}
			StringBuilder subQuerySerialNumbers = conversionObj.getStringList(subQuerySerialList);
			int first = 0;
			int maxCount = 5;
			List<String> serialList = new LinkedList<String>();

			String queryString2 = "select a.serialNumber,f.account_name, f.mobile_no  "
					+ "from AssetEventEntity a, AssetAccountMapping e, AccountEntity f "
					+ "where a.activeStatus=1 and a.partitionKey =1 and f.status=true and a.serialNumber in "
					+ "("+subQuerySerialNumbers+")"
					+ " and a.eventTypeId=1 and a.eventSeverity like 'RED'"
					+ "and e.serialNumber=a.serialNumber and f.account_id=e.accountId group by e.serialNumber";

			/* String queryString2 = "select a.serialNumber,f.account_name, f.mobile_no  "
                        + "from AssetEventEntity a, AssetAccountMapping e, AccountEntity f "
                        + "where a.activeStatus=1 and f.status=true and a.serialNumber in "
                        + "(select b.serialNumber from AssetAccountMapping b ,TenancyBridgeEntity c ,AccountTenancyMapping d"
                        + "   where c.parentId in ('"
                        + tenancyIdStringList 
                        + "') and c.parentId != c.childId and d.tenancy_id=c.childId and c.level<>0 and b.accountId=d.account_id group by b.serialNumber )"
                        + " and a.eventTypeId=1 and a.eventSeverity like 'RED'"
                        + "and e.serialNumber=a.serialNumber and f.account_id=e.accountId group by e.serialNumber";*/
			Query query2 = session.createQuery(queryString2);

			Iterator itr2 = query2.list().iterator();

			Object result4[] = null;
			while (itr2.hasNext()) {
				result4 = (Object[]) itr2.next();
				AssetEntity asset = (AssetEntity) result4[0];
				serialList.add(asset.getSerial_number().getSerialNumber());

			}
			iLogger.info(" serialList" + serialList.size());
			//            Keerthi : 21/10/13 : Checking for serial number list size
			if(serialList.size()>0){
				String serialStringList = conversionObj.getStringList(serialList)
						.toString();
				iLogger.info(" serialStringList" + serialStringList);

				iLogger
				.info(" *******Get maximum Parameter id for given Engine hours*********************");
				Query query3 = session
						.createQuery("select max(d.parameterId ) from MonitoringParameters d where d.parameterName like 'Hour'");
				Iterator itr3 = query3.list().iterator();
				int parameterId2 = 0;
				while (itr3.hasNext()) {
					parameterId2 = (Integer) itr3.next();
					iLogger.info(" parameterId for EngineHours is "
							+ parameterId2);
				}

				List<String> customerNameList = new LinkedList<String>();
				List<String> contactList = new LinkedList<String>();


				//Added by Juhi for defect :1245
				List<Integer> primaryId = new LinkedList<Integer>();
				//2014-08-18 : Modified the query from = to In : Reason: Subquery returns more than 1 row.
				Query query10 = session.createQuery("select account_name,mobile_no ,account_id from AccountEntity where status=true and account_id in (select primary_owner_id from AssetEntity where serial_number in ("+serialStringList+")  group by primary_owner_id ) group by account_id ");
				Object result2[] = null;
				query10.setFirstResult(first);
				query10.setMaxResults(maxCount);
				Iterator itr10 = query10.list().iterator();
				while (itr10.hasNext()) {
					result2 = (Object[]) itr10.next();
					customerNameList.add((String) result2[0]);
					contactList.add((String) result2[1]);
					primaryId.add((Integer)result2[2]);
				}
				//Added by Juhi for defect :1245
				/*    Query quer=session.createQuery("select primary_owner_id from AssetEntity where serial_number in ("+serialStringList+") group by primary_owner_id");

          Iterator itr9 = quer.list().iterator();
          while (itr9.hasNext()) {

                primaryId.add((Integer) itr9.next());
          //    Integer number = ((Long) result3[1]).intValue();
                //NumberOfMachineDueList.add(number);
          }*/
				String primaryIdStringList = conversionObj
						.getIntegerListString(primaryId).toString();

				//List<AssetControlUnitEntity> serialNumber=new LinkedList<AssetControlUnitEntity>();
				List<String> MachineServiceDetailsList = new LinkedList<String>();

				for (int i = 0; i < customerNameList.size(); i++) 
				{
					//DefectId:2014-08-19 @Suprava Nayak Hashmap value change
					MachineServiceDetailsList = new LinkedList<String>();
					int NumberOfMachineDue=0;
					CustomerRequiringImpl customerRequiringImpl1 = new CustomerRequiringImpl();
					if(contactList.get(i)!=null)

						customerRequiringImpl1.setCustomerContactNumber(contactList.get(i));    
					if(customerNameList.get(i)!=null)
						customerRequiringImpl1.setCustomerName(customerNameList.get(i));
					//Serial_numbers for customer
					Query quer1=session.createQuery("select a.serial_number from AssetEntity a,AssetControlUnitEntity b where a.serial_number=b.serialNumber and a.primary_owner_id ="+primaryId.get(i)+" and a.serial_number in ("+serialStringList+")");
					Iterator quer1iterator=quer1.list().iterator();
					while(quer1iterator.hasNext())
					{
						NumberOfMachineDue++;
						List<Double> ScheduleHoursList = new LinkedList<Double>();

						AssetControlUnitEntity serialNumber=(AssetControlUnitEntity)quer1iterator.next();
						String serial_Number=serialNumber.getSerialNumber();
						double currentHours = 0;
						List<Double> currentHoursList = new LinkedList<Double>();
						//DF 2013-012-06: Deepthi : Modified the query to improve performance.
						/*  String queryString = "select b.serialNumber,a.parameterValue from AssetMonitoringDetailEntity a ,AssetMonitoringHeaderEntity b where a.transactionNumber = (select max(c.transactionNumber )from AssetMonitoringHeaderEntity c where c.serialNumber ='"
                              + serial_Number
                              + "' group by  c.serialNumber)and a.parameterId = "
                              + parameterId2
                              + " and b.transactionNumber=a.transactionNumber ";

                        Query query4 = session.createQuery(queryString);

                        Object result[] = null;
                        Iterator itr4 = query4.list().iterator();
                        while (itr4.hasNext())
                        {
                        result = (Object[]) itr4.next();

                        currentHours = Double.parseDouble(result[1].toString());*/

						//20160718 - @suresh DAL layer implemntation for ams where latest cmh for a VIN can be found in parameter coloumn 
						String txnKey = "CustomerRequiringBO:setAssetProvisionDetails";

						List<AmsDAO> snapshotObj=new ArrayList<AmsDAO> ();

						DynamicAMS_DAL amsDaoObj=new DynamicAMS_DAL();

						snapshotObj=amsDaoObj.getAMSData(txnKey, serial_Number);

						//iLogger.debug(txnKey+"::"+"AMS:persistDetailsToDynamicMySql::AMS DAL::getAMSData Size:"+snapshotObj.size());

						if(snapshotObj.size()>0){

							//parameters format in AMS
							//String currParam= LAT|LONG|Enginestatus|Machinehours|ExternalBatteryVoltage|HCT|LOP|InternalBatteryLow
							//temp = false;
							String parameters=snapshotObj.get(0).getParameters();
							String [] currParamList=parameters.split("\\|", -1);

							currentHours = Double.parseDouble(currParamList[3]);

							currentHoursList.add(currentHours);
						}


						// Changes done by Juhi on 29-july-2013 Logic added to get
						// Scheduled hour for given serial number list ----------

						AssetServiceScheduleGetReqContract reqObj = new AssetServiceScheduleGetReqContract();
						List<Integer> serviceScheduleIdList = new LinkedList<Integer>();

						reqObj.setSerialNumber(serial_Number);

						List<AssetServiceScheduleGetRespContract> respobj = new AssetServiceScheduleImpl()
						.getAssetserviceSchedule(reqObj);

						for (int sch = 0; sch < respobj.size(); sch++) {

							// change done on 31 july 2013 by Juhi
							// Check if HoursToNextService is present ,if yes add it
							// to serviceScheduleId List
							if (respobj.get(sch).getHoursToNextService() != null) {
								serviceScheduleIdList.add(respobj.get(sch)
										.getServiceScheduleId());
							}

						}        
						//Check on maxDatenew placed by Juhi on 2013-09-18 for defect id:959
						double ScheduleHours=0;
						if(serviceScheduleIdList!=null)
						{
							String serviceScheduleIdStringList = conversionObj
									.getIntegerListString(serviceScheduleIdList).toString();


							String queryString1 = "SELECT b.engineHoursSchedule "
									+ " FROM ServiceScheduleEntity b "
									+ " where  b.serviceScheduleId in("
									+ serviceScheduleIdStringList + ") "
									+ " GROUP BY b.serviceScheduleId";


							Query query5 = session.createQuery(queryString1);
							// Object result1[]=null;
							Iterator itr5 = query5.list().iterator();
							while (itr5.hasNext()) {
								long result1 = (Long) itr5.next();
								// ScheduleHoursList.add((double)((Long)result1[0]));
								ScheduleHours=(double) result1;
							}

						}
						double OverDueHours = currentHours- ScheduleHours;

						int evtTypeId1 = 0;
						int ActiveAlert = 1;

						String string1 = "from EventTypeEntity where eventTypeName LIKE 'Service'";
						Query query6 = session.createQuery(string1);
						Iterator itr6 = query6.list().iterator();
						while (itr6.hasNext()) {
							EventTypeEntity eventTypeEntity1 = (EventTypeEntity) itr6
									.next();
							evtTypeId1 = eventTypeEntity1.getEventTypeId();
							iLogger.info(" evtTypeId  +++++" + evtTypeId1);
						}

						Long ActiveAlerts = null;
						Query query7 = session
								.createQuery(" select count(activeStatus) from AssetEventEntity where eventTypeId="
										+ evtTypeId1
										+ " and activeStatus="
										+ ActiveAlert
										+ " and partitionKey =1 and eventSeverity like 'RED' and serialNumber ='"
										+ serial_Number + "' group by serialNumber");
						Iterator itr7 = query7.list().iterator();
						while (itr7.hasNext()) {
							ActiveAlerts=(Long) itr7.next();
						}
						Long OtherAlert =null;
						Query query8 = session
								.createQuery(" select count(activeStatus) from AssetEventEntity where eventTypeId!="
										+ evtTypeId1
										+ " and activeStatus="
										+ ActiveAlert
										+ " and partitionKey =1 and serialNumber ='"
										+ serial_Number + "' group by serialNumber");
						Iterator itr8 = query8.list().iterator();
						while (itr8.hasNext()) {
							OtherAlert=(Long) itr8.next();
						}
						String MachineServiceDetails = null;
						MachineServiceDetails = serial_Number + ","     + OverDueHours + ","+ ActiveAlerts+ ","+ OtherAlert;
						MachineServiceDetailsList.add(MachineServiceDetails);
					}//end of Serial_numbers for customer



					customerRequiringImpl1
					.setMachineServiceDetails(MachineServiceDetailsList);
					customerRequiringImpl1.setDealerName(DealerName);

					customerRequiringImpl1.setNumberOfMachineDueForService(NumberOfMachineDue);
					customerRequiringImplList.add(customerRequiringImpl1);

				} //end of impl addition
			}


		}catch(Exception e)
		{
			e.printStackTrace();
		}
		/*
		 * catch(Exception e){
		 * 
		 * fatalError.fatal("Hello this is an Fatal Error. Need immediate Action"
		 * +e.getMessage());
		 * 
		 * }
		 */
		finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}
		return customerRequiringImplList;
	}

	// ******************************End of Get the customers which requires
	// service under given List of LoginTenancy_ID*****************

}
