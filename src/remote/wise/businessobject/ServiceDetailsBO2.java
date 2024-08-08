package remote.wise.businessobject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetServiceScheduleEntity;
import remote.wise.businessentity.EngineTypeEntity;
import remote.wise.businessentity.ProductEntity;
import remote.wise.businessentity.ServiceScheduleEntity;
import remote.wise.dal.DynamicAMS_DAL;
import remote.wise.dal.DynamicAMS_Doc_DAL;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.pojo.AMSDoc_DAO;
import remote.wise.pojo.AmsDAO;
import remote.wise.service.implementation.AssetServiceScheduleImpl;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;

/**
 * BO class to set and get the
 * serviceScheduleId,serviceName,scheduleName,durationSchedule
 * ,engineHoursSchedule for the specified productId
 * 
 * @author Smitha
 * 
 */
public class ServiceDetailsBO2 {
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("ServiceDetailsBO2:","businessError");
	/*public static WiseLogger fatalError = WiseLogger.getLogger("ServiceDetailsBO2:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("ServiceDetailsBO2:","info");*/
	
	
	private int serviceScheduleId;
	private String serviceName;
	private String scheduleName;
	private int durationSchedule;
	private long engineHoursSchedule;
	private int productId;
	private AssetEntity serialNumber;
	private float hoursToNextService;
	private String dbmsPartCode;
	private String jobCardNumber;
	private Timestamp serviceDate;
	private int assetTypeId;
	private int engineTypeId;
	private int assetGroupId;
	private String engineTypeName;
	private String asset_group_name;
	private String asset_type_name;
	private int EventId;

	public EngineTypeEntity getEngineTypeEntity(int engineTypeId) {
		EngineTypeEntity EngineTypeEntity = new EngineTypeEntity(engineTypeId);
		return EngineTypeEntity;
	}

	public String getEngineTypeName() {
		return engineTypeName;
	}

	public void setEngineTypeName(String engineTypeName) {
		this.engineTypeName = engineTypeName;
	}

	public String getAsset_group_name() {
		return asset_group_name;
	}

	public void setAsset_group_name(String asset_group_name) {
		this.asset_group_name = asset_group_name;
	}

	public String getAsset_type_name() {
		return asset_type_name;
	}

	public void setAsset_type_name(String asset_type_name) {
		this.asset_type_name = asset_type_name;
	}

	public int getAssetTypeId() {
		return assetTypeId;
	}

	public void setAssetTypeId(int assetTypeId) {
		this.assetTypeId = assetTypeId;
	}

	public int getEngineTypeId() {
		return engineTypeId;
	}

	public void setEngineTypeId(int engineTypeId) {
		this.engineTypeId = engineTypeId;
	}

	public int getAssetGroupId() {
		return assetGroupId;
	}

	public void setAssetGroupId(int assetGroupId) {
		this.assetGroupId = assetGroupId;
	}

	public Timestamp getServiceDate() {
		return serviceDate;
	}

	public void setServiceDate(Timestamp serviceDate) {
		this.serviceDate = serviceDate;
	}

	public String getJobCardNumber() {
		return jobCardNumber;
	}

	public void setJobCardNumber(String jobCardNumber) {
		this.jobCardNumber = jobCardNumber;
	}

	public float getHoursToNextService() {
		return hoursToNextService;
	}

	public void setHoursToNextService(float hoursToNextService) {
		this.hoursToNextService = hoursToNextService;
	}

	public AssetEntity getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(AssetEntity assetEntity) {
		this.serialNumber = assetEntity;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productEntity) {
		this.productId = productEntity;
	}

	public int getDurationSchedule() {
		return durationSchedule;
	}

	public void setDurationSchedule(int durationSchedule) {
		this.durationSchedule = durationSchedule;
	}

	public long getEngineHoursSchedule() {
		return engineHoursSchedule;
	}

	public void setEngineHoursSchedule(long engineHoursSchedule) {
		this.engineHoursSchedule = engineHoursSchedule;
	}

	public int getServiceScheduleId() {
		return serviceScheduleId;
	}

	public void setServiceScheduleId(int serviceScheduleId) {
		this.serviceScheduleId = serviceScheduleId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public String getDbmsPartCode() {
		return dbmsPartCode;
	}

	public void setDbmsPartCode(String dbmsPartCode) {
		this.dbmsPartCode = dbmsPartCode;
	}

	public int getEventId() {
		return EventId;
	}

	public void setEventId(int eventId) {
		EventId = eventId;
	}

//	Logger infoLogger = Logger.getLogger("infoLogger");

	// get method for asset service schedule

	/**
	 * This method gets serviceScheduleId,dealerId,scheduled_date,serialNumber
	 * for the specified serialNumber
	 * 
	 * @param reqObj
	 *            Get the details of serialNumber by passing the same to this
	 *            request Object
	 * 
	 * @return Returns respObj for the specified serialNumber
	 * 
	 * @throws CustomFault
	 */
	// added by smitha Defect Id 1121....aug 13th 2013
	public List<AssetServiceScheduleImpl> getAssetServiceScheduleBO(
			String serialNumber) throws CustomFault {

		//	Logger businessError = Logger.getLogger("businessErrorLogger");
		//	Logger fatalError = Logger.getLogger("fatalErrorLogger");
			
	    	Logger fLogger = FatalLoggerClass.logger;
	    	Logger iLogger = InfoLoggerClass.logger;
	    	
		//	List<ServiceDetailsBO2> serviceBOList = new LinkedList<ServiceDetailsBO2>();

		//	List<AssetServiceScheduleImpl> assetServiceImpl = new LinkedList<AssetServiceScheduleImpl>();

			List<AssetServiceScheduleImpl> assetServiceImpll = new LinkedList<AssetServiceScheduleImpl>();

		//	AssetServiceScheduleImpl assetServiceImpl1 = new AssetServiceScheduleImpl();

			AssetServiceScheduleImpl assetimplObj = null;
			//List<Integer> listOfIds = new LinkedList<Integer>();
			//List<Integer> tempListOfIds = new LinkedList<Integer>();
			Long hours = 0L;
			String parameterValue = null;
			Long paramValue = 0L;
			String serviceNamee = null;
		//	Long engineHourRecursive = 0L;
			String scheduleNamee = null;
			int serviceScheduleIdDate = 0;
			Long engineHours1 = 0L;
			String serviceEventTypeId = null;
			int scheduleIds = 0, scheduleId1 = 0;
		//	List<Integer> listOfIds2 = new ArrayList<Integer>();
			List<Long> engineHoursList = new LinkedList<Long>();
			String dealerNamee = null;
		//	String formattedDate = null;
			int dealeridss = 0;
			String scheduleDateService = null;
			int finalEventID = 0;
			int finaltempEventID = 0;
			int finaltempEventID1 = 0;
			Long engineHours = null;

			Session session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();

			try {

				Properties prop = new Properties();
				try {
					prop.load(getClass()
							.getClassLoader()
							.getResourceAsStream(
									"remote/wise/resource/properties/configuration.properties"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				serviceEventTypeId = prop.getProperty("ServiceEventTypeId");
			//	int serviceScheduleIdtemp = 0;
				int EventId = 0;
				String eventdueIdone = null;
				String eventdueIdseven = null;
				String eventoverdueId = null;
				String eventdueHour = null;
				String eventdueHour2 = null;
				eventdueIdseven = prop.getProperty("Reminder1forServiceDue");// 1
				eventdueIdone = prop.getProperty("Reminder2forServiceDue");// 2
				eventoverdueId = prop.getProperty("ServiceOverDue");// 3
				eventdueHour = prop.getProperty("Reminder1forServiceDueHour");// 50
				eventdueHour2 = prop.getProperty("Reminder2forServiceDueHour");// 10

				String finalQuery = null;

				finalQuery = "select c.assetServiceScheduleId,c.serialNumber,c.serviceScheduleId,c.scheduledDate,"
						+

						"c.dealerId from AssetServiceScheduleEntity c "
						+

						"where c.serviceScheduleId in (select b.serviceScheduleId+1 from ServiceHistoryEntity a,ServiceScheduleEntity b "
						+

						" where a.serialNumber = '"
						+ serialNumber
						+ " ' and a.serviceDate in (select max(serviceDate) from ServiceHistoryEntity l where serialNumber='"
						+ serialNumber
						+ "' )"
						+

						" and a.dbmsPartCode =b.dbmsPartCode) and c.serialNumber='"
						+ serialNumber + " ' order by c.scheduledDate";

				int scheduleId = 0;

				Object result[] = null;
				boolean flag = false;
				Query query1 = session.createQuery(finalQuery);
				List resultList2 = query1.list();
				int resultSize = resultList2.size();
				if (resultSize == 0) {
					flag = true;
				}
				if (!flag) {
					Iterator itr = resultList2.iterator();

					while (itr.hasNext()) {

						result = (Object[]) itr.next();

						int asset = (Integer) result[0];

						ServiceScheduleEntity serviceId = (ServiceScheduleEntity) result[2];

						scheduleId = serviceId.getServiceScheduleId();

						Timestamp date = (Timestamp) result[3];

					}
				}
//				Keerthi : 28/02/14 : ordering by schedule date : file based MSService may not have same order during insertion to DB
				String querySch = "from AssetServiceScheduleEntity where serialNumber = '"
//						+ serialNumber + "'order by serviceScheduleId";
					+ serialNumber + "'order by scheduledDate";
				query1 = session.createQuery(querySch);

				List resultList = query1.list();

				Iterator itrSch = resultList.iterator();

				int id = 0;

				int dealerids = 0;

				Timestamp scheduledates = null;

//				List<Integer> reqScheduleIdList = new ArrayList<Integer>();

				List<Integer> reqDealerIdList = new ArrayList<Integer>();

				HashMap<Integer, String> map = new HashMap<Integer, String>();
				HashMap<Integer, Long> Enginemap = new HashMap<Integer, Long>();
				HashMap<Integer, Long> tempEnginemap = new HashMap<Integer, Long>();
				int firstSchedueleId = 0;

				while (itrSch.hasNext()) {

					assetimplObj = new AssetServiceScheduleImpl();

					AssetServiceScheduleEntity assetSchedule = (AssetServiceScheduleEntity) itrSch
							.next();
					if (assetSchedule != null) {
						id = assetSchedule.getServiceScheduleId()
								.getServiceScheduleId();
						if (firstSchedueleId == 0) {
							firstSchedueleId = id;
						}
//						reqScheduleIdList.add(id);

						dealerids = assetSchedule.getDealerId().getAccount_id();

						reqDealerIdList.add(dealerids);

						scheduledates = assetSchedule.getScheduledDate();

						ListToStringConversion conversionObj = new ListToStringConversion();

						/*String reqScheduleIdStringList = conversionObj
								.getIntegerListString(

								reqScheduleIdList).toString();*/
//						Keerthi : 28/02/14 : taking a single service schedule id instead of list of ids(which is not required)
//						String queryall = "from ServiceScheduleEntity where serviceScheduleId in ("
							String queryall = "from ServiceScheduleEntity where serviceScheduleId ="+id;
//								+ reqScheduleIdStringList + " )";

						Iterator itrall = session.createQuery(queryall).list()
								.iterator();

						while (itrall.hasNext()) {

							ServiceScheduleEntity serviceScheduleEntity1 = (ServiceScheduleEntity) itrall
									.next();

							engineHours1 = serviceScheduleEntity1
									.getEngineHoursSchedule();

							scheduleNamee = serviceScheduleEntity1
									.getScheduleName();

							serviceNamee = serviceScheduleEntity1.getServiceName();

							scheduleIds = serviceScheduleEntity1
									.getServiceScheduleId();

						}

						ListToStringConversion conversionObj1 = new ListToStringConversion();

						String reqDealerIdStringList = conversionObj1
								.getIntegerListString(

								reqDealerIdList).toString();

						String queryAccount1 = "from AccountEntity where status=true and account_id in ("

								+ reqDealerIdStringList + " )";

						Iterator itrAccount1 = session.createQuery(queryAccount1)
								.list().iterator();

						while (itrAccount1.hasNext()) {

							AccountEntity accountEntity = (AccountEntity) itrAccount1
									.next();

							dealerNamee = accountEntity.getAccount_name();

							dealeridss = accountEntity.getAccount_id();

						}

						assetimplObj.setServiceScheduleId(scheduleIds);

						assetimplObj.setDealerId(dealeridss);

						assetimplObj.setScheduledDate(scheduledates.toString());

						assetimplObj.setEngineHoursSchedule(engineHours1);

						assetimplObj.setScheduleName(scheduleNamee);

						assetimplObj.setServiceName(serviceNamee);

						assetimplObj.setDealerName(dealerNamee);

						assetServiceImpll.add(assetimplObj);
					}
				}
				// added on Aug 8th
				Calendar currentDate = Calendar.getInstance();
				SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
				String dateNow = formatter1.format(currentDate.getTime());
				String rangeDateQuery = "select max(scheduledDate) from AssetServiceScheduleEntity where scheduledDate<='"
						+ dateNow + "' and serialNumber ='" + serialNumber + "'";
				List res = session.createQuery(rangeDateQuery).list();
				Iterator itrd = res.iterator();
				Timestamp maxDate1 = null;
				String maxDate = null;
				if (!res.contains(null)) {
					while (itrd.hasNext()) {
						maxDate1 = (Timestamp) itrd.next();
						maxDate = new SimpleDateFormat("yyyy-MM-dd")
								.format(maxDate1);
					}
				}

				String rangeDateMinQuery = "select min(scheduledDate) from AssetServiceScheduleEntity where scheduledDate>'"
						+ dateNow + "' and serialNumber ='" + serialNumber + "'";
				List res1 = session.createQuery(rangeDateMinQuery).list();
				Iterator itrdd = res1.iterator();
				Timestamp minDate1 = null;
				String minDate = null;
				if (!res1.contains(null)) {
					while (itrdd.hasNext()) {
						minDate1 = (Timestamp) itrdd.next();
						minDate = new SimpleDateFormat("yyyy-MM-dd")
								.format(minDate1);
					}
				}
				Date maxDatenew1 = null;
				try {
					if (maxDate != null) {
						maxDatenew1 = formatter1.parse(maxDate);
					}
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String maxDatenew = null;
				ServiceScheduleEntity serviceentity = null;
				int scheduleIdmax = 0;

				// Check on maxDatenew placed by Juhi on 2013-09-18 for defect
				// id:959
				if (maxDatenew1 != null) {
					maxDatenew = formatter1.format(maxDatenew1);

					String datess = "select a.serviceScheduleId from AssetServiceScheduleEntity a where a.scheduledDate like '"
							+ maxDatenew
							+ "%' and a.serialNumber='"
							+ serialNumber
							+ "'";
					Iterator itrds = session.createQuery(datess).list().iterator();

					while (itrds.hasNext()) {
						serviceentity = (ServiceScheduleEntity) itrds.next();
						scheduleIdmax = serviceentity.getServiceScheduleId();
					}
				}
				if (scheduleIdmax == scheduleId - 1) {
					 iLogger.info("record exists in service history");
					// checking for service due based on date
					Date date_new = null;
					Date current = null;
					try {
						date_new = formatter1.parse(minDate);
					} catch (ParseException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					String datemin = formatter1.format(date_new.getTime());
					try {
						current = formatter1.parse(dateNow);
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					int miliss1 = (int) (current.getTime() / (24 * 60 * 60 * 1000));
					int miliss2 = (int) (date_new.getTime() / (24 * 60 * 60 * 1000));
					int differences = miliss2 - miliss1;
					if (differences >= 0 && differences <= 1) {
						iLogger.info("due for service by 1 day");
						scheduleDateService = minDate;
						EventId = Integer.parseInt(eventdueIdone);
					} else if (differences >= 1 && differences <= 7) {
						iLogger.info("due for service by 1 week");
						scheduleDateService = minDate;
						EventId = Integer.parseInt(eventdueIdseven);
					} else {
						iLogger.info("nothing");
						scheduleDateService = minDate;
						EventId = 0;
					}
				} else {

					iLogger.info("record doesnt exist in service history");
					// checking for service due based on date
					Date date_new = null;
					Date current = null;
					try {
						if (minDate != null) {
							date_new = formatter1.parse(minDate);
						}
					} catch (ParseException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					if (date_new != null) {
						String datemin = formatter1.format(date_new.getTime());
					}
					try {
						current = formatter1.parse(dateNow);
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int miliss1 = (int) (current.getTime() / (24 * 60 * 60 * 1000));
					int miliss2 = 0;
					if (date_new != null) {
						miliss2 = (int) (date_new.getTime() / (24 * 60 * 60 * 1000));
					}
					int differences = miliss2 - miliss1;
					if (differences >= 0 && differences <= 1) {
						iLogger.info("due for service by 1 day");
						scheduleDateService = minDate;
						EventId = Integer.parseInt(eventdueIdone);
					} else if (differences >= 1 && differences <= 7) {
						iLogger.info("due for service by 1 week");
						scheduleDateService = minDate;
						EventId = Integer.parseInt(eventdueIdseven);
					}
					// DefectID:1320 Rajani Nagaraju - 20130921 - Service alerts not
					// getting generated properly
					else if (differences < 0) {
						iLogger.info("overdue");
						scheduleDateService = maxDate;
						EventId = Integer.parseInt(eventoverdueId);
					}
					else{
						iLogger.info("overdue");
						scheduleDateService = maxDate;
						EventId = Integer.parseInt(eventoverdueId);
					}

				}
				finaltempEventID = EventId;
				// checking for service due based on engine hours
				/*String hql = "select c.parameterValue from AssetMonitoringDetailEntity c where c.transactionNumber=( select max(b.transactionNumber) from AssetMonitoringHeaderEntity b where b.serialNumber='"
						+ serialNumber
						+ "')and c.parameterId=(select max(a.parameterId) from MonitoringParameters a where a.parameterName like 'Hour')";

				Query query = session.createQuery(hql);
				Iterator itr2 = query.list().iterator();
				String param[] = null;
				while (itr2.hasNext()) {
					parameterValue = itr2.next().toString();
					if (parameterValue.contains(".")) {
						int fullLength = parameterValue.length();
						int len = parameterValue.substring(
								parameterValue.indexOf(".")).length() - 1;
						int index = (fullLength - len) - 1;
						parameterValue = parameterValue.substring(0, index);
					} else {
						parameterValue = parameterValue;
					}

				}
//				removing zeroes from the beginning
				if((parameterValue!=null)&&(Double.parseDouble(parameterValue)!=0)){
					parameterValue = parameterValue.replaceFirst("^0+(?!$)", "");
				}*/
				
				/* String txnKey = "EventDetailsBO:getPullSmsDetails";
					
					List<AmsDAO> snapshotObj=new ArrayList<AmsDAO> ();

					DynamicAMS_DAL amsDaoObj=new DynamicAMS_DAL();

					snapshotObj=amsDaoObj.getAMSData(txnKey, serialNumber);

					//iLogger.debug(txnKey+"::"+"AMS:persistDetailsToDynamicMySql::AMS DAL::getAMSData Size:"+snapshotObj.size());
				//	String cummOpHours = null;
					if(snapshotObj.size()>0){
						
						//parameters format in AMS
						//String currParam= LAT|LONG|Enginestatus|Machinehours|ExternalBatteryVoltage|HCT|LOP|InternalBatteryLow
						//temp = false;
					String parameters=snapshotObj.get(0).getParameters();
					String [] currParamList=parameters.split("\\|", -1);
					 //DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					 
					 //CMH
					if(currParamList.length>2)
						parameterValue = currParamList[3];
					}
//				removing zeroes from the beginning
				if((parameterValue!=null)&&(Double.parseDouble(parameterValue)!=0)){
					parameterValue = parameterValue.replaceFirst("^0+(?!$)", "");
				}*/
				
				 String txnKey = "ServiceDetailsBO:getAssetServiceScheduleBO";
				 
				 //DF20161228 @Roopa changing asset_monitoring_snapshot_new to asset_monitoring_snapshot which is having txndata as json column	
	             
	             List<AMSDoc_DAO> snapshotObj=new ArrayList<AMSDoc_DAO> ();

	     		DynamicAMS_Doc_DAL amsDaoObj=new DynamicAMS_Doc_DAL();

	     		snapshotObj=amsDaoObj.getAMSData(txnKey, serialNumber);
	     		HashMap<String,String> txnDataMap=new HashMap<String, String>();
	     		
	     		txnDataMap=snapshotObj.get(0).getTxnData();
	     		parameterValue = txnDataMap.get("CMH");
	     		
//				removing zeroes from the beginning
				if((parameterValue!=null)&&(Double.parseDouble(parameterValue)!=0)){
					parameterValue = parameterValue.replaceFirst("^0+(?!$)", "");
				}
	     		
				 //DF20161228 END
				
				
				String rangehourQuery = "select max(a.engineHoursSchedule) from ServiceScheduleEntity a where a.engineHoursSchedule<='"
						+ parameterValue
						+ "' and a.serviceScheduleId in (select b.serviceScheduleId from AssetServiceScheduleEntity b where b.serialNumber ='"
						+ serialNumber + "')";
				Iterator itrh = session.createQuery(rangehourQuery).list()
						.iterator();
				Long maxHour = 0L;
				while (itrh.hasNext()) {
					maxHour = (Long) itrh.next();
				}

				String rangehourMinQuery = "select min(a.engineHoursSchedule) from ServiceScheduleEntity a where a.engineHoursSchedule>'"
						+ parameterValue
						+ "' and a.serviceScheduleId in (select b.serviceScheduleId from AssetServiceScheduleEntity b where b.serialNumber ='"
						+ serialNumber + "')";
				Iterator itrdhh = session.createQuery(rangehourMinQuery).list()
						.iterator();
				Long minHour = 0L;
				while (itrdhh.hasNext()) {
					minHour = (Long) itrdhh.next();
				}

				// ended on Aug 12th

				try {
					if (scheduleDateService != null) {
						Date dd = formatter1.parse(scheduleDateService);
						scheduleDateService = formatter1.format(dd);
					}
					String queryDate = "from AssetServiceScheduleEntity  where scheduledDate like '"
							+ scheduleDateService
							+ "%' and serialNumber='"
							+ serialNumber + "'";
					Iterator itrDate = session.createQuery(queryDate).list()
							.iterator();

					while (itrDate.hasNext()) {

						AssetServiceScheduleEntity assetService = (AssetServiceScheduleEntity) itrDate
								.next();
						serviceScheduleIdDate = assetService.getServiceScheduleId()
								.getServiceScheduleId();

					}

					String DateQuery = "from ServiceScheduleEntity where serviceScheduleId="
							+ serviceScheduleIdDate;
					Iterator itrDate1 = session.createQuery(DateQuery).list()
							.iterator();

					String serviceNameDate = null;
					String scheduleNameDate = null;

					while (itrDate1.hasNext()) {

						ServiceScheduleEntity ServiceDate = (ServiceScheduleEntity) itrDate1
								.next();
						serviceNameDate = ServiceDate.getServiceName();
						scheduleNameDate = ServiceDate.getScheduleName();
					}
					List<Integer> frequencyList = new LinkedList<Integer>();
					List<Integer> tempOrderedFreqList = new LinkedList<Integer>();
					Object intResult = null;
					String hql1 = "select frequency from EventEntity where eventTypeId ="
							+ serviceEventTypeId;
					Iterator itr3 = session.createQuery(hql1).list().iterator();
					while (itr3.hasNext()) {
						intResult = (Object) itr3.next();
						frequencyList.add((Integer) intResult);
					}
					Collections.sort(frequencyList);
					/*
					 * for (int o : frequencyList) { if (o != 0) {
					 * tempOrderedFreqList.add(o); }
					 * 
					 * }
					 */

					tempOrderedFreqList = frequencyList;
					// DefectID:1320 Rajani Nagaraju - 20130921 - Service alerts not
					// getting generated properly
					/*
					 * if(maxHour!=null && minHour!=null){
					 * engineHoursList.add(maxHour); engineHoursList.add(minHour); }
					 */
					if (maxHour != null)
						engineHoursList.add(maxHour);
					if (minHour != null)
						engineHoursList.add(minHour);
					// DefectID:1320 Rajani Nagaraju - 20130921 - Service alerts not
					// getting generated properly

					Long finalEngineHour = 0L;
					int engineHourDifference = 0;
					int freg = 0;
					Long eHour = 0L;
					int ind = 0;
					if (!(engineHoursList == null && tempOrderedFreqList == null)) {
						for (int i = 0; i < tempOrderedFreqList.size(); i++) {
							freg = tempOrderedFreqList.get(i);
							for (int j = 0; j < engineHoursList.size(); j++) {

								eHour = engineHoursList.get(j);
								engineHourDifference = (int) (engineHoursList
										.get(j) - freg);
								if (Long.parseLong(parameterValue) >= engineHourDifference
										&& Long.parseLong(parameterValue) <= eHour) {
									finalEngineHour = eHour;
									ind = i;
								}
							}
							if (ind != 0) {
								break;
							}
						}
					}
					long diff1 = 0L;
					long diff2 = 0L;
					Long quHour = 0L;
					
					if (finalEngineHour == 0L
							&& Long.parseLong(parameterValue) != 0L) {
						if (maxHour != null) {
							diff1 = Long.parseLong(parameterValue) - maxHour;
						} else {
							diff1 = Long.parseLong(parameterValue);
						}
						if (minHour != null) {
							diff2 = minHour - Long.parseLong(parameterValue);
						} else {
							diff2 = -Long.parseLong(parameterValue);
						}
						if (diff1 != 0L && diff2 != 0L) {
							if (diff1 > diff2) {
								iLogger
										.info("current engine hours is nearer to minhour");	
								/*if(minHour!=null && Long.parseLong(parameterValue) != 0L) {
								if(minHour>=Long.parseLong(parameterValue)){
									if(tempOrderedFreqList.contains(diff2)){
										finalEngineHour = minHour;
									}else{
										finalEngineHour=maxHour;
									}
								}
							}*/
								//ID20131021....check if no other service schedule exists for a VIN...smitha on 21st oct 2013
								if(diff2<0){
									finalEngineHour=maxHour;
								}
								else { 
								//smitha:dec 5th 2013[Dealer demo issue fixed]
								long differences1=0l;
								
								differences1=minHour-Long.parseLong(parameterValue);
								for(int k=0;k<tempOrderedFreqList.size();k++){
									if(differences1<tempOrderedFreqList.get(k)){
										finalEngineHour = minHour;
									}
								}
								finalEngineHour=maxHour;
								//ended on dec 5th 2013
								}
							} else {
								iLogger
										.info("current engine hours is nearer to maxhour");
								/*if(maxHour!=null && Long.parseLong(parameterValue) != 0L) {
								if(maxHour<=Long.parseLong(parameterValue)){
									Integer diff1temp = (int) (long) diff1;
									if(tempOrderedFreqList.contains(diff1temp)){
										finalEngineHour=maxHour;
									}else{
										String qu="select engineHoursSchedule from ServiceScheduleEntity where engineHoursSchedule <'"+ maxHour+ "'";
										Iterator itrqu = session.createQuery(qu).list().iterator();
										while (itrqu.hasNext()) {
											quHour=(Long) itrqu.next();
										}
										finalEngineHour=quHour;
									}
								}
							}*/
								//smitha:dec 5th 2013[Dealer demo issue fixed]
								long differences2=0l;
								if(maxHour!=null){
								differences2=Long.parseLong(parameterValue)-maxHour;
								for(int k=0;k<tempOrderedFreqList.size();k++){
									if(differences2<tempOrderedFreqList.get(k)){
										finalEngineHour = maxHour;
									}
								}
								finalEngineHour = minHour;
								}
								//ended on dec 5th 2013
							}
							EventId = Integer.parseInt(eventoverdueId);
							finaltempEventID1 = EventId;
						}
					}
					int EventIds = tempOrderedFreqList.get(ind);
					if (EventIds == Integer.parseInt(eventdueHour)) {
						EventId = Integer.parseInt(eventdueIdseven);
					} else if (EventIds == Integer.parseInt(eventdueHour2)) {
						EventId = Integer.parseInt(eventdueIdone);
					} else {
						EventId = Integer.parseInt(eventoverdueId);
					}
					finaltempEventID1 = EventId;
					int prodID = 0;
					ProductEntity pe = null;
					String querys = "select a.productId from AssetEntity a where a.serial_number='"
							+ serialNumber + "'";
					Iterator itrs = session.createQuery(querys).list().iterator();
					while (itrs.hasNext()) {
						pe = (ProductEntity) itrs.next();
						if (pe != null) {
							prodID = pe.getProductId();
						}
					}
					int assetGroupId = 0, assetTypeId = 0, engineTypeId = 0;
					String queryp = " from ProductEntity where productId=" + prodID;
					Iterator itrp = session.createQuery(queryp).list().iterator();
					while (itrp.hasNext()) {
						ProductEntity prod = (ProductEntity) itrp.next();
						assetGroupId = prod.getAssetGroupId().getAsset_group_id();
						assetTypeId = prod.getAssetTypeId().getAsset_type_id();
						engineTypeId = prod.getEngineTypeId().getEngineTypeId();
					}

					int ServiceScheduleIdFreq = 0;
					if (!(finalEngineHour == null || finalEngineHour == 0L)) {
						String queryFreq1 = "from ServiceScheduleEntity where engineHoursSchedule="
								+ finalEngineHour
								+ "and assetGroupId="
								+ assetGroupId
								+ "and assetTypeId="
								+ assetTypeId
								+ "and engineTypeId=" + engineTypeId;
						Iterator itrFreq1 = session.createQuery(queryFreq1).list()
								.iterator();
						while (itrFreq1.hasNext()) {
							ServiceScheduleEntity service = (ServiceScheduleEntity) itrFreq1
									.next();
							ServiceScheduleIdFreq = service.getServiceScheduleId();
						}

					}

					// ended Defect ID 690
					String last = null;
					if (serviceScheduleIdDate < ServiceScheduleIdFreq) {
						last = "from AssetServiceScheduleEntity where serviceScheduleId="
								+ ServiceScheduleIdFreq
								+ " and serialNumber='"
								+ serialNumber + "'";
					} else {
						last = "from AssetServiceScheduleEntity where serviceScheduleId="
								+ serviceScheduleIdDate
								+ " and serialNumber='"
								+ serialNumber + "'";
					}
					Iterator itrlast = session.createQuery(last).list().iterator();
					while (itrlast.hasNext()) {
						AssetServiceScheduleEntity assetServicescheduleEntity = (AssetServiceScheduleEntity) itrlast
								.next();
						scheduleId1 = assetServicescheduleEntity
								.getServiceScheduleId().getServiceScheduleId();
						String scheduledDate = assetServicescheduleEntity
								.getScheduledDate().toString();
						int dealerId = assetServicescheduleEntity.getDealerId()
								.getAccount_id();
						String queryfinal = "from ServiceScheduleEntity where serviceScheduleId= "
								+ scheduleId1;
						Iterator itrfinal = session.createQuery(queryfinal).list()
								.iterator();

						String serviceName1 = null;
						String scheduleName1 = null;

						while (itrfinal.hasNext()) {
							ServiceScheduleEntity serviceScheduleEntity = (ServiceScheduleEntity) itrfinal
									.next();
							engineHours = serviceScheduleEntity
									.getEngineHoursSchedule();
							scheduleName1 = serviceScheduleEntity.getScheduleName();
							serviceName1 = serviceScheduleEntity.getServiceName();
							// added by smitha on june 27th 2013 Defect ID 690
							paramValue = Long.parseLong(parameterValue);
							//hours = engineHours - paramValue;
							//DefectId:20140515 OverdueHour @Suprava
							hours = paramValue - engineHours;
							// end [june 27th 2013]
						}
						String queryAccount = "from AccountEntity where status=true and account_id= "
								+ dealerId;
						Iterator itrAccount = session.createQuery(queryAccount)
								.list().iterator();
						String dealerName = null;
						while (itrAccount.hasNext()) {
							AccountEntity accountEntity = (AccountEntity) itrAccount
									.next();
							dealerName = accountEntity.getAccount_name();
						}
					}
					if (serviceScheduleIdDate == ServiceScheduleIdFreq) {
						if (finaltempEventID > finaltempEventID1) {
							finalEventID = finaltempEventID;
						} else {
							finalEventID = finaltempEventID1;
						}
					} else {
						if (scheduleId1 == serviceScheduleIdDate) {
							finalEventID = finaltempEventID;
						} else {
							finalEventID = finaltempEventID1;
						}
					}

					//if the new machine hasn't taken any service till current date and the diff in engine hours is not < 50 or 10 (Smitha :3rd oct 2013)
					String newQuery="select engineHoursSchedule,serviceScheduleId from ServiceScheduleEntity where serviceScheduleId in (select serviceScheduleId from AssetServiceScheduleEntity where serialNumber='"
								+ serialNumber + "') ORDER BY engineHoursSchedule";//Keerthi : ID : 2104 : 05/03/14
					Iterator itrNewMach = session.createQuery(newQuery).list().iterator();
					Object resultNew[] = null;
			long newMachEngineHours=0L;
			int newMachScheduleId=0;
			while (itrNewMach.hasNext()) {
				resultNew = (Object[]) itrNewMach.next();
				
				 newMachEngineHours=(Long)resultNew[0];
				 newMachScheduleId=(Integer) resultNew[1];
				break;
			}			
			//	ended (3rd oct 2013)	 
					Iterator iterResult = assetServiceImpll.iterator();
					AssetServiceScheduleImpl resultlist = null;
					while (iterResult.hasNext()) {
						if (iterResult != null) {
							resultlist = (AssetServiceScheduleImpl) iterResult
									.next();
							if (resultlist.getServiceScheduleId() == scheduleId1) {
								resultlist.setHoursToNextService(hours);
								//start (Smitha :3rd oct 2013)
								if(hours==0){
									finalEventID=Integer.parseInt(eventoverdueId);							
								}
								resultlist.setEventId(finalEventID);
							}
							////start (Smitha :3rd oct 2013)
							else if((hours==null || hours==0 )&& scheduleId==0 && (Long.parseLong(parameterValue)<newMachEngineHours)){
								 if (resultlist.getServiceScheduleId() == newMachScheduleId) {
										//resultlist.setHoursToNextService(newMachEngineHours-Long.parseLong(parameterValue));
									 //DefectId:20140515 OverdueHour @Suprava
									 resultlist.setHoursToNextService(Long.parseLong(parameterValue)-newMachEngineHours);
									}
							 }
							 ////ended (3rd oct 2013)
						}
					}
			
					
				} catch (Exception e) {
					e.printStackTrace();
					fLogger.fatal("Exception :" + e);
				}
			} finally {
				if (session.getTransaction().isActive())
					session.getTransaction().commit();

				if (session.isOpen()) {
					session.flush();
					session.close();

				}
			}
			return assetServiceImpll;
		}
}
// ended on Aug 13th 2013