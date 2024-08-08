package remote.wise.businessobject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountTenancyMapping;
import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetEventEntity;
import remote.wise.businessentity.AssetExtendedDetailsEntity;
import remote.wise.businessentity.AssetMonitoringHeaderEntity;
import remote.wise.businessentity.ClientEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.GroupUserMapping;
import remote.wise.dal.DynamicAMS_DAL;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.pojo.AmsDAO;
import remote.wise.service.implementation.MapImpl;
import remote.wise.service.webservice.MapOverviewCacheRESTService;
import remote.wise.util.AssetUtil;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;

public class MapBO2 {
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("MapBO2:","businessError");
	//public static WiseLogger fatalError = WiseLogger.getLogger("MapBO2:","fatalError");
	/*public static WiseLogger infoLogger = WiseLogger.getLogger("MapBO2:","info");
	public static WiseLogger fatalError = WiseLogger.getLogger("MapBO2:","fatalError");*/
	/**
	 * This method will return List of Map Details for given LoginId and List of
	 * SerialNumbers and filters if any
	 * 
	 * @param LoginId
	 *            :LoginId
	 * @param SerialNumberList
	 *            :List of SerialNumbers
	 * @param AlertSeverityList
	 *            :List of AlertSeverity
	 * @param AlertTypeIdList
	 *            :List of AlertTypeId
	 * @param Landmark_IdList
	 *            :List of Landmark_Id
	 * @param LandmarkCategory_IdList
	 *            :List of LandmarkCategory_Id
	 * @param Tenancy_ID
	 *            :List of Tenancy_ID
	 * @param machineGroupIdList
	 *            :List of machineGroupId
	 * @param machineProfileIdList
	 *            :List of machineProfileId
	 * @param modelIdList
	 *            :List of modelId
	 * @return mapImplList::Returns List of Map Details
	 * @throws CustomFault
	 *             :custom exception is thrown when the LoginId
	 *             ,Period,SerialNumber is not specified, SerialNumber is
	 *             invalid or not specified
	 */
	public List<MapImpl> getMap(ContactEntity loginId, List<String> SerialNumberList,
			List<String> AlertSeverityList, List<Integer> AlertTypeIdList,
			List<Integer> Landmark_IdList,
			List<Integer> LandmarkCategory_IdList, List<Integer> Tenancy_ID,
			List<Integer> customMachineGroupIdList,
			List<Integer> machineProfileIdList, List<Integer> modelIdList,
			boolean isOwnStock,List<Integer> loginUserTenancyList) throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;
    	//Logger fLogger = FatalLoggerClass.logger;
    	
		long startTime = System.currentTimeMillis();
		List<MapImpl> mapImplList = new ArrayList<MapImpl>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		try {
			Query query = null;
			String selectQuery = null, fromQuery = null, whereQuery = " WHERE", finalQuery = null,orderByQuery=null;
			String serialNoString = null;
			ListToStringConversion conversionObj = new ListToStringConversion();

			Iterator iterator = null;
			String tenancyIdStringList = conversionObj.getIntegerListString(Tenancy_ID).toString();
			List<String> serialNumberList = new ArrayList<String>();
			//get Client Details
			Properties prop1 = new Properties();
			String clientName=null;
				
			prop1.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop1.getProperty("ClientName");
      
			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details	 
			//			Keerthi : 24/02/14 : only machine group users
			if ( (loginId.getIs_tenancy_admin()==0)){
			//get the list of machines under the machine group to which the user belongs to
			AssetCustomGroupDetailsBO assetCustomGroup = new AssetCustomGroupDetailsBO();
			List<AssetCustomGroupDetailsBO> BoObj = assetCustomGroup.getAssetGroup(loginId.getContact_id(),0,0,null,loginUserTenancyList,false);
			if(! (BoObj==null || BoObj.isEmpty()) )
			{
				for(int u=0; u<BoObj.size(); u++)
				{
					if(loginUserTenancyList.contains(BoObj.get(u).getTenancyId()) )
					  serialNumberList.addAll(BoObj.get(u).getSerialNumberList());
				}
			}
			}
			// get parameter names and values for above filtered serial nos.

			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			String longitude = prop.getProperty("Longitude");
			String latitude = prop.getProperty("Latitude");
			String hour = prop.getProperty("TotalEngineHours");
			String engineON = prop.getProperty("EngineON");
			String Hello=prop.getProperty("HelloPacketParamName");
			String parameterIds = "SELECT max(parameterId),parameterName FROM MonitoringParameters WHERE parameterName IN('"
					+ hour
					+ "','"
					+ latitude
					+ "','"
					+ longitude
					+ "','"
					+ engineON + "') GROUP BY parameterName";
			query = session.createQuery(parameterIds);

			iterator = query.list().iterator();
			List<Integer> parameterIdList = new ArrayList<Integer>();
			Object[] resultObj = null;
			while (iterator.hasNext()) {
				resultObj = (Object[]) iterator.next();
				parameterIdList.add((Integer) resultObj[0]);
			}
			parameterIds = conversionObj.getIntegerListString(parameterIdList)
					.toString();
			iLogger.info("parameterIds  : " + parameterIds);

			//DefectId:20141216 Query Tuning @Suprava 
			Query serialnumQuery = null;
	    	List<String> serialNumListAll=new LinkedList<String>();
	    	List<String> serialNumListRed=new LinkedList<String>();
	    	List<String> serialNumListYellow=new LinkedList<String>();
	    	List<String> serialNumListEngine=new LinkedList<String>();
	    	List<String> serialNumListHello=new LinkedList<String>();
	    	List<Integer> transactionNumberList=new LinkedList<Integer>();
	    	List<Integer> childIdList=new LinkedList<Integer>();
	    	AssetControlUnitEntity assetControlUnitObj=null;
	    	AssetMonitoringHeaderEntity assetHeaderObj =null;
	    	AssetEntity assetObj1 =null;
	    	String serialNum = null;
	    	int transactionNum =0;
	    	String eventSeverityEngOn =null;
	    	int childId =0;
	    	
	    	if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
	    	Query childIdQuery =session.createQuery("SELECT distinct f.childId ,f.parentId FROM TenancyBridgeEntity f where f.parentId in("+tenancyIdStringList+")");
	    	Iterator childIdItr = childIdQuery.list().iterator();
			while (childIdItr.hasNext())
			{
				Object[] childIdresult1 = (Object[]) childIdItr.next();
				if (childIdresult1[0] != null) {
					childId = (Integer) childIdresult1[0];
					childIdList.add(childId);
				}
			}
			String childIdListAsStringList = conversionObj.getIntegerListString(childIdList).toString();
		//	System.out.println("childIdListAsStringList:"+childIdListAsStringList);
			
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			if(childIdListAsStringList!=null && (!childIdListAsStringList.isEmpty())){
			String serialnumlistQuery = "SELECT distinct a.serial_number,b.transactionNumber FROM AssetEntity a ,AssetMonitoringSnapshotEntity b,AssetMonitoringHeaderEntity c where a.primary_owner_id in" +
					"(SELECT e.account_id FROM AccountTenancyMapping e where e.tenancy_id in ("+childIdListAsStringList+") )" +
							" and b.serialNumber=a.serial_number and c.serialNumber=a.serial_number group by a.serial_number";
			serialnumQuery = session.createQuery(serialnumlistQuery);
			Iterator iterator1 = serialnumQuery.list().iterator();
			Object[] resultObj1 = null;
			while (iterator1.hasNext()) {
				resultObj1 = (Object[]) iterator1.next();
				if (resultObj1[0] != null) {
					assetControlUnitObj = (AssetControlUnitEntity) resultObj1[0];
					if (assetControlUnitObj != null) {
						serialNum = assetControlUnitObj.getSerialNumber();
						serialNumListAll.add(serialNum);
					}
			      }
				if (resultObj1[1] != null) {
					assetHeaderObj = (AssetMonitoringHeaderEntity) resultObj1[1];
					if (assetHeaderObj != null) {
						transactionNum = assetHeaderObj.getTransactionNumber();
						transactionNumberList.add(transactionNum);
					}
				}
			}
			}
			String transactionNumberAsStringList = conversionObj.getIntegerListString(transactionNumberList).toString();
			iLogger.info("transactionNumberAsStringList"+transactionNumberAsStringList);
			String serialNumberListStringList = conversionObj.getStringList(serialNumListAll).toString();
			
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			if(serialNumberListStringList!=null && (!serialNumberListStringList.isEmpty())){
				//DF20190204 @abhishek---->add partition key for faster retrieval
			Query query1 = session.createQuery("select distinct a.serialNumber, a.eventSeverity from AssetEventEntity a where a.serialNumber in("+serialNumberListStringList+") and " +
					"a.activeStatus=1 and a.partitionKey =1 and UPPER(a.eventSeverity)='RED'");
			Iterator Itr1 = query1.list().iterator();
			while (Itr1.hasNext())
			{
				Object[] result1 = (Object[]) Itr1.next();
				if (result1[0] != null) {
					assetObj1 = (AssetEntity) result1[0];
					serialNum =assetObj1.getSerial_number().getSerialNumber();
					serialNumListRed.add(serialNum);
				}
			}
			}
			serialNumListAll.removeAll(serialNumListRed);
			String serialNumberListStringL1 = conversionObj.getStringList(serialNumListAll).toString();
			
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			if(serialNumberListStringL1!=null && (!serialNumberListStringL1.isEmpty())){
				//DF20190204 @abhishek---->add partition key for faster retrieval
			Query query2 = session.createQuery("select distinct a.serialNumber, a.eventSeverity from AssetEventEntity a where a.serialNumber in("+serialNumberListStringL1+") and " +
			"a.activeStatus=1 and a.partitionKey =1 and UPPER(a.eventSeverity)='YELLOW'");
	        Iterator Itr2 = query2.list().iterator();
	        while (Itr2.hasNext())
	         {
		        Object[] result2 = (Object[]) Itr2.next();
		        if (result2[0] != null) {
		        	assetObj1 = (AssetEntity) result2[0];
					serialNum =assetObj1.getSerial_number().getSerialNumber();
					serialNumListYellow.add(serialNum);
		           }
	         }
			}
	        serialNumListAll.removeAll(serialNumListYellow);
			String serialNumberListStringL2 = conversionObj.getStringList(serialNumListAll).toString();
			
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			if(serialNumberListStringL2!=null && (!serialNumberListStringL2.isEmpty())){
			Query severityQuery1=session.createQuery("select a.serialNumber ,b.parameterValue from AssetMonitoringSnapshotEntity a," +
					" AssetMonitoringDetailEntity b,MonitoringParameters c, AssetMonitoringHeaderEntity d where a.transactionNumber=b.transactionNumber " +
					" and b.transactionNumber in(select e.transactionNumber from AssetMonitoringSnapshotEntity e where " +
					" e.serialNumber in("+serialNumberListStringL2+")) and a.serialNumber=d.serialNumber and a.transactionNumber=d.transactionNumber" +
							" and b.parameterId=c.parameterId and b.parameterValue=1 and c.parameterName='"+engineON+"'");
			Iterator iterator2 = severityQuery1.list().iterator();
			 while (iterator2.hasNext())
	         {
		        Object[] result3 = (Object[]) iterator2.next();
		        if (result3[0] != null) {
		        	assetObj1 = (AssetEntity) result3[0];
					serialNum =assetObj1.getSerial_number().getSerialNumber();
					serialNumListEngine.add(serialNum);
		           }
	         }
			}
	        serialNumListAll.removeAll(serialNumListEngine);
			String serialNumberListStringL3 = conversionObj.getStringList(serialNumListAll).toString();
			
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			if(serialNumberListStringL3!=null && (!serialNumberListStringL3.isEmpty()) ){
			Query severityQuery2=session.createQuery("select a.serialNumber ,b.parameterValue from AssetMonitoringSnapshotEntity a," +
					" AssetMonitoringDetailEntity b,MonitoringParameters c, AssetMonitoringHeaderEntity d where  a.transactionNumber=b.transactionNumber " +
					" and b.transactionNumber in(select e.transactionNumber from AssetMonitoringSnapshotEntity e where " +
					" e.serialNumber in("+serialNumberListStringL3+")) and a.serialNumber=d.serialNumber and a.transactionNumber=d.transactionNumber" +
							"  and b.parameterId=c.parameterId and b.parameterValue=1 and c.parameterName='"+Hello+"'");
			Iterator iterator3 = severityQuery2.list().iterator();
			 while (iterator3.hasNext())
	         {
		        Object[] result4 = (Object[]) iterator3.next();
		        if (result4[0] != null) {
		        	assetObj1 = (AssetEntity) result4[0];
					serialNum =assetObj1.getSerial_number().getSerialNumber();
					serialNumListHello.add(serialNum);
		           }
	         }
			}
	        serialNumListAll.removeAll(serialNumListHello);
			String serialNumberListStringL4 = conversionObj.getStringList(serialNumListAll).toString();
			
			//End DefectId:20141216
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			//DF20130115 - Rajani Nagaraju - For the Machines that are communicated before GateOut(Personality), Map should display those machines also.
			/*selectQuery = "SELECT i.serialNumber,"
					+ "i.transactionNumber,i.transactionTime,"
					+ "l.parameterId ,l.parameterName,m.parameterValue,n.OperatingStartTime,n.OperatingEndTime,ag.asset_group_id,ag.asset_group_name";*/
			selectQuery = "SELECT i.serialNumber,"
					+ "i.transactionNumber,i.transactionTime,"
					+ "l.parameterId ,l.parameterName,m.parameterValue,n.OperatingStartTime,n.OperatingEndTime ";

			//DF20130115 - Rajani Nagaraju - For the Machines that are communicated before GateOut(Personality), Map should display those machines also.
			/*fromQuery = " FROM AssetEntity k, AssetMonitoringFactDataYearAgg c"
					+ ",AssetMonitoringHeaderEntity i,"
					+ "MonitoringParameters l, AssetMonitoringDetailEntity m, AssetExtendedDetailsEntity n,AssetGroupEntity ag,ProductEntity prod";*/
			fromQuery = " FROM AssetEntity k, AssetMonitoringFactDataYearAgg c"
					+ ",AssetMonitoringHeaderEntity i,"
					+ "MonitoringParameters l, AssetMonitoringDetailEntity m, AssetExtendedDetailsEntity n ,AssetMonitoringSnapshotEntity p ";
			
			if (!(tenancyIdStringList == null || tenancyIdStringList.isEmpty())) {
				fromQuery = fromQuery +" ,TenancyBridgeEntity b,TenancyDimensionEntity a";
				whereQuery = whereQuery + " b.parentId in ("	+ tenancyIdStringList
					+ ") and b.childId = a.tenancyId and a.tenacy_Dimension_Id = c.tenancyId and";
			}
			
			whereQuery = whereQuery
//			Keerthi : 27/01/14 : taking max year for each PIN
					//+ " c.year = (select max(year) from AssetMonitoringFactDataYearAgg agg where agg.serialNumber = c.serialNumber) and "
					+ " c.serialNumber = i.serialNumber "
					+ " and c.serialNumber=k.serial_number "
					+ " and i.transactionNumber=p.transactionNumber "
					//+ " and i.transactionNumber = ( select max(transactionNumber) from AssetMonitoringHeaderEntity a where a.serialNumber = "
					//+ " c.serialNumber)" +
						+ " and m.parameterId = l.parameterId and i.transactionNumber = m.transactionNumber and "
					+ " l.parameterId in ("
					+ parameterIds
					+ ") and c.serialNumber = n.serial_number and i.serialNumber=k.serial_number and i.serialNumber=p.serialNumber " ;
					//DF20130115 - Rajani Nagaraju - For the Machines that are communicated before GateOut(Personality), Map should display those machines also.
					//" and k.productId=prod.productId and prod.assetGroupId=ag.asset_group_id ";

			orderByQuery = " ORDER BY c.serialNumber";

			if (!(SerialNumberList == null || SerialNumberList.isEmpty())) {
				serialNoString = conversionObj.getStringList(SerialNumberList).toString();
				whereQuery = whereQuery + " and c.serialNumber IN ("+ serialNoString + ") ";
			}
			if (!(AlertSeverityList == null || AlertSeverityList.isEmpty())
					|| (!(AlertTypeIdList == null || AlertTypeIdList.isEmpty()))) {

				fromQuery = fromQuery + " ,AssetEventEntity aee";
				//DF20190204 @abhishek---->add partition key for faster retrieval
				whereQuery = whereQuery	+ " AND c.serialNumber =aee.serialNumber AND aee.activeStatus=1 and aee.partitionKey =1";
				if (!(AlertSeverityList == null || AlertSeverityList.isEmpty())) {
					String AlertSeverityString = conversionObj.getStringList(AlertSeverityList).toString();
					whereQuery = whereQuery + " AND aee.eventSeverity IN ("+ AlertSeverityString + ")";
				}

				if (!(AlertTypeIdList == null || AlertTypeIdList.isEmpty())) {
					String AlertTypeIdListStringList = conversionObj
							.getIntegerListString(AlertTypeIdList).toString();
					whereQuery = whereQuery + " AND aee.eventTypeId in ( "+ AlertTypeIdListStringList + " )";
				}
			}

			if (!(customMachineGroupIdList == null || customMachineGroupIdList.isEmpty())) {
				String machineGroupIdStringList = conversionObj
						.getIntegerListString(customMachineGroupIdList).toString();
				fromQuery = fromQuery+ " , CustomAssetGroupEntity cae, AssetCustomGroupMapping acgm ";
				whereQuery = whereQuery	+ " AND cae.group_id = acgm.group_id AND cae.group_id IN ("
						+ machineGroupIdStringList
						+ ") AND acgm.serial_number = c.serialNumber and cae.client_id="+clientEntity.getClient_id()+" and cae.active_status=1 ";
			}

			if ((!(machineProfileIdList == null || machineProfileIdList.isEmpty()))
					|| (!(modelIdList == null || modelIdList.isEmpty()))) {
				fromQuery = fromQuery + " ,AssetClassDimensionEntity acd";
				whereQuery = whereQuery	+ " AND c.assetClassDimensionId = acd.assetClassDimensionId";

				if (!(machineProfileIdList == null || machineProfileIdList.isEmpty())) {
					String machineProfileIdStringList = conversionObj.getIntegerListString(machineProfileIdList).toString();
					whereQuery = whereQuery + " AND acd.assetGroupId in ( "	+ machineProfileIdStringList + " )";
				}

				if (!(modelIdList == null || modelIdList.isEmpty())) {
					String modelIdStringList = conversionObj
							.getIntegerListString(modelIdList).toString();
					whereQuery = whereQuery + " AND acd.assetTypeId in ( "+ modelIdStringList + " )";
				}
			}
			// check for own stock
			if (isOwnStock) {
//				Keerthi : 13/11/13 : own stock changes
//				Keerthi : 26/12/13 : own stock query changes
				fromQuery = fromQuery+ " ,AccountTenancyMapping atm ";
//				whereQuery = whereQuery+ " AND b.childId in ("	+ tenancyIdStringList+ ")";
				whereQuery = whereQuery + " and k.primary_owner_id=atm.account_id" +
				" and atm.tenancy_id in ("+tenancyIdStringList+")";
			}
			
			//DF20140219 - Rajani Nagaraju - To display only those VINs that are in the current hierarchy
			else
			{
				List<Integer> accountIdLsit = new AssetDetailsBO().getChildAccounts(session,Tenancy_ID);
				String accountIdListAsString = conversionObj.getIntegerListString(accountIdLsit).toString();
				whereQuery = whereQuery+" and k.primary_owner_id in ("+accountIdListAsString+") ";
			}
			finalQuery = selectQuery + fromQuery + whereQuery + orderByQuery;// groupByQuery;//;

			//infoLogger.info("finalQuery : " + finalQuery);
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
			String startDate = sdf.format(cal.getTime());
			long startTime1 = System.currentTimeMillis();
			query = session.createQuery(finalQuery);
			Calendar cal1 = Calendar.getInstance();
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
			String endDate = sdf1.format(cal1.getTime());
			long endTime=System.currentTimeMillis();
			iLogger.info("Query1 Execution Time in ms:"+(endTime-startTime1));
			
			MapImpl mapImplObj = null;
			String parameterName = null, parameterValue = null;
			
			//Added by Juhi on 10-September-2013
			//List<String> SerialNumberForSeverityList=new LinkedList<String>();
			 
			int parameterID = 0;
			iterator = query.list().iterator();
			String serialNumber = null;
			Object[] results = null;
			String prevSerNo = null;
			AssetEntity assetObj = null;
			String startDate2 = sdf.format(cal.getTime());
			long startTime2 = System.currentTimeMillis();
			iLogger.info("startDate2:"+startDate2);
			while (iterator.hasNext()) {
				results = (Object[]) iterator.next();
				if (results[0] != null) {
					assetObj = (AssetEntity) results[0];
					if (assetObj != null) {
						serialNumber = ((AssetControlUnitEntity) assetObj.getSerial_number()).getSerialNumber();
					}
					//DefectId:20140225 To Handle Null pointer Exception
					if( (!(SerialNumberList==null)) && (!(SerialNumberList.size()>0))) {
						if(serialNumberList.size()>0){
							if(!serialNumberList.contains(serialNumber)){//if its not a machine group user
								continue;
							}					
						}
					}					
					if (results[3] != null) {
						parameterID = (Integer) results[3];
					}
					if (results[4] != null) {
						parameterName = (String) results[4];
					}
					if (results[5] != null) {
						parameterValue = (String) results[5];
					}
					if (!serialNumber.equals(prevSerNo)) {
					//	infoLogger.info("serialNo  : " + serialNumber);
						mapImplObj = new MapImpl();
						mapImplObj.setSerialNumber(serialNumber);
					}
					if (assetObj != null) {
						mapImplObj.setNickname(assetObj.getNick_name());
					}
					if (results[2] != null) {
						mapImplObj.setLastReportedTime((String) results[5]);
					}
					if (parameterID == 1) {
						mapImplObj.setLatitude(parameterValue);
					} else if (parameterID == 2) {
						mapImplObj.setLongitude(parameterValue);
					} else if (parameterID == 4) {
						mapImplObj.setTotalMachineHours(parameterValue);
					} else if (parameterID == 18) {
						mapImplObj.setEngineStatus(parameterValue);
						String engineOn=parameterValue;
				
					}
					if (results[6] != null) {
						mapImplObj
								.setOperatingStartTime((Timestamp) results[6]);
					}
					if (results[7] != null) {
						mapImplObj.setOperatingEndTime((Timestamp) results[7]);
					}
					/*if (results[8] != null) {
						mapImplObj.setProfileCode((Integer) results[8]);
					}
					if (results[9] != null) {
						mapImplObj.setProfileName((String) results[9]);
					}*/
					
					//DF20130115 - Rajani Nagaraju - For the Machines that are communicated before GateOut(Personality), Map should display those machines also.
					if(assetObj!=null)
					{
						if(assetObj.getProductId()!=null && assetObj.getProductId().getAssetGroupId()!=null)
						{
							mapImplObj.setProfileCode(assetObj.getProductId().getAssetGroupId().getAsset_group_id());
							mapImplObj.setProfileName(assetObj.getProductId().getAssetGroupId().getAsset_group_name());
						}
						
					}
					
					//Added by Juhi on 10-September-2013
					String eventSeverity1=null;
					String eventSeverity=null;
					/*Query severityQuery=session.createQuery("select eventSeverity from AssetEventEntity where serialNumber='"+serialNumber+"' and activeStatus=1");
					Iterator iterator1 = severityQuery.list().iterator();
				
					if(iterator1.hasNext())
					{
						while(iterator1.hasNext())
						{
							eventSeverity1=(String)iterator1.next();
							if(eventSeverity1.equalsIgnoreCase("Red"))
							{
								eventSeverity=eventSeverity1;
								break;
							}
							else
							{
								eventSeverity=eventSeverity1;
								continue;
							}
						}
						mapImplObj.setSeverity(eventSeverity);
					}*/
//					Keerthi : 28/01/14 : selecting severity : check for red, set if any. else check for yellow, set if any.
					if (!serialNumber.equals(prevSerNo)) {
						
						if(serialNumListRed.contains(serialNumber))
			    		{
							mapImplObj.setSeverity("RED");
							//System.out.println("eventSeverity Red:"+serialNumber);
			    		}
			    		else if(serialNumListYellow.contains(serialNumber))
			    		{
			    			//System.out.println("eventSeverity Yellow"+serialNumber);
			    			mapImplObj.setSeverity("YELLOW");
			    		}
			    		else if(serialNumListEngine.contains(serialNumber))
			    		{
			    			//System.out.println("eventSeverity green"+serialNumber);
			    			mapImplObj.setSeverity("GREEN");
			    		}
			    		else if(serialNumListHello.contains(serialNumber))
			    		{
			    			//System.out.println("eventSeverity Organge"+serialNumber);
			    			mapImplObj.setSeverity("ORANGE");
			    		}
			    		else
			    		{
			    			//System.out.println("eventSeverity Grey"+serialNumber);
			    			mapImplObj.setSeverity("Grey");
			    		}
						/*
						Query severityQuery=session.createQuery("select eventSeverity from AssetEventEntity where serialNumber='"+serialNumber+"' and activeStatus=1 and UPPER(eventSeverity)='RED'");
						if(severityQuery.list().size()>0){
							eventSeverity="RED";
							mapImplObj.setSeverity(eventSeverity);
						}
						else{
							severityQuery =session.createQuery("select eventSeverity from AssetEventEntity where serialNumber='"+serialNumber+"' and activeStatus=1 and UPPER(eventSeverity)='YELLOW'");
							if(severityQuery.list().size()>0){
								eventSeverity="YELLOW";
								mapImplObj.setSeverity(eventSeverity);
							}
						}
						if(eventSeverity==null)
						{
							//DefectId: - Rajani Nagaraju - 20130930 - To Fix Query Exception - Subquery returns more than one row
							Query severityQuery1=session.createQuery("select parameterValue from AssetMonitoringDetailEntity " +
									"  where " +
									" transactionNumber =" +
									"(select transactionNumber from  AssetMonitoringHeaderEntity" +
									" where transactionTime =" +
									"(select max(transactionTime)from AssetMonitoringHeaderEntity " +
									"where serialNumber='"+serialNumber+"') and serialNumber='"+serialNumber+"'" +
											") and parameterId=(select max(parameterId) from MonitoringParameters where parameterName like '"+engineON+"')"  +
											" ");
							Iterator iterator2 = severityQuery1.list().iterator();
							if(iterator2.hasNext())
							{
								eventSeverity1=(String)iterator2.next();
							}
							if(eventSeverity1.equalsIgnoreCase("1"))
							{
								eventSeverity="Green";
								mapImplObj.setSeverity(eventSeverity);
							}
							else 
							{
								//DefectId: 1382 - Rajani Nagaraju - 20130930 - To Fix Query Exception - Subquery returns more than one row
								Query severityQuery2=session.createQuery("select parameterValue from AssetMonitoringDetailEntity where transactionNumber =" +
										"(select transactionNumber from  AssetMonitoringHeaderEntity" +
										" where transactionTime =" +
										"(select max(transactionTime)from AssetMonitoringHeaderEntity " +
										"where serialNumber='"+serialNumber+"') and serialNumber='"+serialNumber+"'" +
												") and parameterId=(select max(parameterId) from MonitoringParameters where parameterName like '"+Hello+"')"  +
												"");
								Iterator iterator3 = severityQuery1.list().iterator();
								if(iterator3.hasNext())
								{
									eventSeverity1=(String)iterator3.next();
								}
								if(eventSeverity1.equalsIgnoreCase("1"))
								{
									eventSeverity="Orange";
									mapImplObj.setSeverity(eventSeverity);
								}
								else
								{
									eventSeverity="Grey";
									mapImplObj.setSeverity(eventSeverity);
								}
							}
							
						}
					*/}
					
						
					
					
					if (!serialNumber.equals(prevSerNo)) {
						mapImplList.add(mapImplObj);
					}
					prevSerNo = serialNumber;
				
				//	SerialNumberForSeverityList.add(prevSerNo);
				}
			}
			String endDate1 = sdf1.format(cal1.getTime());
			long endTime2=System.currentTimeMillis();
			iLogger.info("Query2 Execution Time in ms:"+(endTime2-startTime2));

		//	String SerialNumberForSeverityStringList = conversionObj.getStringList(SerialNumberForSeverityList).toString();
			
			iLogger.info("mapImplList size : " + mapImplList.size());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		iLogger.info("Total time taken by getMap() : " + duration);
		return mapImplList;

	}
	
//	Keerthi : 2017.02.07 : asset profile and model filter for overview page
	public List<MapImpl> getOverviewMapDetails(String loginId,
            List<String> alertSeverityList, List<Integer> alertTypeList,
            List<Integer> tenancyIdList, boolean isOwnStock,
            List<Integer> loginUserTenancyList,List<Integer> machineProfileIdList,List<Integer> modelIdList,int mapMarkerKey,String countryCode,List<Integer> userMachineGroupIdList) {

	      Logger iLogger = InfoLoggerClass.logger;
	      Logger fLogger = FatalLoggerClass.logger;

	      long startTime = System.currentTimeMillis();

	      List<MapImpl> responseImplList = new LinkedList<MapImpl>();
	      List<MapImpl> response = new LinkedList<MapImpl>();

	      Session session = HibernateUtil.getSessionFactory().openSession();
	     // session.beginTransaction();

	      try {

	    	  //DF20171120:KO369761-For Chunking we are considering Limits in query.
	    	  int startLimit = mapMarkerKey*5000;
	    	  int endLimit =5000;
	    	  
	    	  iLogger.info("Into the Implementaiton Class login id ::"+loginId);

	    	 //DF20190830::Abhishek:Changed logic to show the machines which are under the machine group.
	    	  // Get the Contact Details - To find whether the user is TA or not
	    	  /*Query contactQ = session
	    			  .createQuery(" from ContactEntity where contact_id='"
	    					  + loginId + "'");
	    	  Iterator contactItr = contactQ.list().iterator();
	    	  ContactEntity contact = null;
	    	  while (contactItr.hasNext()) {
	    		  contact = (ContactEntity) contactItr.next();
	    	  }

	    	  List<Integer> userMachineGroupIdList = null;
	    	  if (contact.getIs_tenancy_admin() == 0) {
	    		  Query groupUserQ = session
	    				  .createQuery(" from GroupUserMapping where contact_id='"
	    						  + loginId + "'");
	    		  Iterator groupUserItr = groupUserQ.list().iterator();
	    		  while (groupUserItr.hasNext()) {
	    			  if (userMachineGroupIdList == null)
	    				  userMachineGroupIdList = new LinkedList<Integer>();
	    			  GroupUserMapping groupUser = (GroupUserMapping) groupUserItr
	    					  .next();
	    			  userMachineGroupIdList.add(groupUser.getGroup_id()
	    					  .getGroup_id());
	    		  }
	    	  }*/

	    	  String finalQueryString = "", outerSelectQuery = "", productQuery = "";
	    	  String selectQuery = "", fromQuery = "", whereQuery = "", groupByQuery = "", joinClause = "";
	    	  ListToStringConversion conversion = new ListToStringConversion();

	    	  // --------------------------- Step1: Get the AccountId List
	    	  // corresponding to Login User Tenancy and Zone/Dealer/Customer
	    	  // filter if applied
	    	  String userTenancyListAsString;

	    	  if (!(tenancyIdList == null || tenancyIdList.isEmpty())) {
	    		  // Get the list of accounts corresponding to the child tenancy
	    		  // (If Zone/Dealer/Customer filter is applied)
	    		  userTenancyListAsString = conversion.getIntegerListString(
	    				  tenancyIdList).toString();
	    	  } else {
	    		  userTenancyListAsString = conversion.getIntegerListString(
	    				  loginUserTenancyList).toString();
	    	  }
	    	  
	    	//DF20171206: KO369761 considering account numbers where are not there in static hashmap for further processing.
	    	  int[] accountList = {1001,1200,1201,1202,1203,1204,1205,1206,1207,2001,2002,2003,2004,2005,2006,2007,2008,2009,50000};
	    	  List<Integer> userAccList = new LinkedList<Integer>();
	    	  
	    	//Df20171218 @Roopa including country code filter in the map service and applying static data check only when filter's not applied(static map contains only account specific data)
	    	  
	    	  if((countryCode==null || countryCode.equalsIgnoreCase("null")) && (!isOwnStock) && (userMachineGroupIdList==null || userMachineGroupIdList.size()==0) && (alertSeverityList == null || alertSeverityList.size() == 0)
	    			  && (alertTypeList == null || alertTypeList.size() == 0) && (machineProfileIdList==null || machineProfileIdList.size()==0) && (modelIdList==null || modelIdList.size()==0)){
	    	  Query accountQ = session
	    			  .createQuery(" from AccountTenancyMapping where tenancy_id in ("
	    					  + userTenancyListAsString + ")");
	    	  Iterator accountItr = accountQ.list().iterator();
	    	  int i = 0;
	    	  while (accountItr.hasNext()) {
	    		  AccountTenancyMapping accTen = (AccountTenancyMapping) accountItr
	    				  .next();
	    		  //userAccList.add(accTen.getAccount_id().getAccount_id());
	    		  //DF20171206: KO369761 considering account numbers where are not there in static hashmap for further processing.
	    		  for(i=0;i<accountList.length;i++){
	    			  if(accountList[i]==accTen.getAccount_id().getAccount_id())
	    				  break;
	    		  }
	    		  if(i == accountList.length)
	    			  userAccList.add(accTen.getAccount_id().getAccount_id());
	    		  else{
	    			  if(!(MapOverviewCacheRESTService.mapCache == null || MapOverviewCacheRESTService.mapCache.get(accTen.getAccount_id().getAccount_id()) == null || MapOverviewCacheRESTService.mapCache.get(accTen.getAccount_id().getAccount_id()).size() == 0))
	    				  response = MapOverviewCacheRESTService.mapCache.get(accTen.getAccount_id().getAccount_id()).get("call"+mapMarkerKey);
	    			  if(response == null || response.size() == 0){
	    				  userAccList.add(accTen.getAccount_id().getAccount_id());
	    			  }
	    			  else{
	    				  responseImplList.addAll(response);
	    				  iLogger.info("Map Service:"+loginId+":Response taken from Static varaible.");
	    			  }
	    		  }
	    	  }
	    	  
	    	  if(userAccList.size() == 0)
	    		  return responseImplList;
	    	  
	    	  }
	    	  if(userAccList.size() == 0 ||  userAccList.isEmpty()) {
	    		  Query accountQ = session
		    			  .createQuery(" from AccountTenancyMapping where tenancy_id in ("
		    					  + userTenancyListAsString + ")");
		    	  Iterator accountItr = accountQ.list().iterator(); 
		    	  while (accountItr.hasNext()) {
		    		  AccountTenancyMapping accTen = (AccountTenancyMapping) accountItr
		    				  .next();
		    		 userAccList.add(accTen.getAccount_id().getAccount_id());
		    	  }
	    	  }

	    	  if (session!=null && session.isOpen()) {
	    		  // session.flush();
	    		  session.close();
	    	  }

	    	  outerSelectQuery = "select aa.*,product.* from ";
	    	  if(userMachineGroupIdList!=null)
	    		  selectQuery = " select distinct(ams.Serial_Number), ams.TxnData, a.Engine_Number,a.Product_ID ";
	    	  else
	    		  selectQuery = " select ams.Serial_Number, ams.TxnData, a.Engine_Number,a.Product_ID ";
	    	  fromQuery = "     from asset_monitoring_snapshot ams, asset a";
	    	  whereQuery = " where a.Serial_Number=ams.Serial_Number and a.Status=1 and ams.Latest_Transaction_Timestamp > '2014-01-01 00:00:00' ";
	    	  //groupByQuery = " group by ams.Serial_Number ";
	    	  //DF20171127 - Rajani Nagaraju - Map service performance issue - GroupBy not required as AMS table should have only one record per VIN
	    	  groupByQuery = "";

	    	  whereQuery = whereQuery + " and ams.TxnData IS NOT NULL ";

	    	  /*productQuery = "SELECT ag.Asseet_Group_Name, p.Product_ID "
	    			  + "FROM  asset_group ag, products p "
	    			  + "WHERE p.Asset_Group_ID=ag.Asset_Group_ID";*/
	    	  
	    	  
	    	  //DF20190213 @abhishek for query performence
	    	  productQuery = "SELECT ag.Asseet_Group_Name, p.Product_ID "
	    			  //+ " FROM  asset_group ag, products p "
	    			  //+ " WHERE p.Asset_Group_ID=ag.Asset_Group_ID"
	    			  + " FROM products p"
                      + " inner join asset_group ag"
                      + " ON ag.Asset_Group_ID = p.Asset_Group_ID ";
	    	  
	    	  joinClause = " ON aa.Product_ID=product.Product_ID ";
	    	//Df20171218 @Roopa including country code filter in the map service
	    	  
	    	  if(countryCode!=null && ! countryCode.equalsIgnoreCase("null")){
	    		  whereQuery = whereQuery
	    				  + " and a.country_code='"+countryCode+"'";
	    	  }

	    	  if (isOwnStock) {
	    		  whereQuery = whereQuery
	    				  + " and a.Primary_Owner_ID=aos.Account_ID ";
	    	  }

	    	 
	    	
	    	  // ************************************* Case1: Get the List of all
	    	  // VINs under the hierarchy of user account
	    	  /*if (userMachineGroupIdList == null) {*/
	    	  //aj20119610 changes done as a part of group based view
	    	  if(userMachineGroupIdList!=null){
	    		  // --------------------------- Step2: Main Query when no filters
	    		  // are applied
	    		  fromQuery = fromQuery + ", custom_asset_group_snapshot cags ";
	    		  whereQuery = whereQuery+"  and ams.Serial_Number = cags.Asset_Id and cags.user_Id IN ('"+loginId+"') ";
	    		  if(userMachineGroupIdList !=null){
	    			  String groupIdStringList = conversion.getIntegerListString(userMachineGroupIdList).toString();
	    			  whereQuery = whereQuery+" and cags.Group_ID in("+groupIdStringList+") ";
	    		  }
	    	  }

	    	  // -------------------------------- Case2: Get the List of VINs
	    	  // under the machine group to which login user is tagged to
              //aj20119610 changes done as a part of group based view
	    	  else {
	    		  String userAccListAsString = conversion.getIntegerListString(userAccList).toString();

	    		  fromQuery = fromQuery + ", asset_owner_snapshot aos ";
	    		  whereQuery = whereQuery+" and ams.Serial_Number=aos.Serial_Number and ams.Latest_Transaction_Timestamp >= aos.Ownership_Start_Date and aos.Account_ID in ("
	    				  + userAccListAsString + ")";
	    	  }
	    	  // --------------------------- Step4: If AlertType/Alert Severity is
	    	  // selected
	    	  if ((alertSeverityList != null && alertSeverityList.size() > 0)
	    			  || (alertTypeList != null && alertTypeList.size() > 0)) {

	    		  //DF20170130 @Roopa for Role based alert implementation
	    		  DateUtil utilObj=new DateUtil();
	    		  List<String> alertCodeList= utilObj.roleAlertMapDetails(null,loginUserTenancyList.get(0), "Display");

	    		  StringBuilder alertCodeListAsString=conversion.getStringList(alertCodeList);
	    		//DF20190204 @abhishek---->add partition key for faster retrieval
	    		  String subQuery = " select distinct ae.Serial_Number from asset_event ae, business_event be where ae.Serial_Number=ams.Serial_Number and ae.Active_Status=1 and ae.PartitionKey =1";

	    		  subQuery=subQuery+" and be.Event_ID=ae.Event_ID and (be.Alert_Code in ("+alertCodeListAsString+")) ";

	    		  //  String subQuery = " select distinct ae.Serial_Number from asset_event ae where ae.Serial_Number=ams.Serial_Number and ae.Active_Status=1";
	    		  if (alertSeverityList != null && alertSeverityList.size() > 0) {
	    			  String eventSeverityListAsString = conversion
	    					  .getStringList(alertSeverityList).toString();
	    			  subQuery = subQuery + " and ae.Event_Severity in ("
	    					  + eventSeverityListAsString + ")";
	    		  }

	    		  if (alertTypeList != null && alertTypeList.size() > 0) {
	    			  String eventTypeIdStringList = conversion
	    					  .getIntegerListString(alertTypeList).toString();
	    			  subQuery = subQuery + " and ae.Event_Type_ID in ("
	    					  + eventTypeIdStringList + ")";
	    		  }

	    		  whereQuery = whereQuery + " and ams.Serial_Number=(" + subQuery
	    				  + ")";
	    	  }
	    	  if((machineProfileIdList!=null && machineProfileIdList.size()>0) || (modelIdList!=null && modelIdList.size()>0))
	    	  {	
	    		  //DF20190627: Abhishek:: ModelId Map filter fix
	    		  outerSelectQuery="select * from (";
	    		  //DF20170724: SU334449 - Added asset group name with the query for filter search for machineProfile and model
	    		  selectQuery = " select ams.Serial_Number, ams.TxnData, a.Engine_Number,a.Product_ID, ag.Asseet_Group_Name";
	    		  fromQuery = fromQuery +",products p, asset_group ag ";
	    		  whereQuery = whereQuery+" and a.product_id = p.product_id ";

	    		  if(machineProfileIdList!=null && machineProfileIdList.size()>0){
	    			  String machineProfileIdString = conversion.getIntegerListString(machineProfileIdList).toString();
	    			  whereQuery = whereQuery+" and p.asset_group_id = ag.asset_group_id "
	    					  + " and ag.asset_group_id in ("+machineProfileIdString+")";
	    		  }
	    		  if(modelIdList!=null && modelIdList.size()>0){
	    			  String modelIdString = conversion.getIntegerListString(modelIdList).toString();
	    			  fromQuery = fromQuery +",asset_type at ";
	    			  whereQuery = whereQuery+" and p.asset_group_id = ag.asset_group_id and p.asset_type_id = at.asset_type_id "
	    					  + " and at.asset_type_id in ("+modelIdString+")"; //DF20180607 @Roopa adding asset group join condition, which is required because we are fetching profile name
	    		  }
	    		  finalQueryString = outerSelectQuery+selectQuery+fromQuery+whereQuery+groupByQuery+")aa";
	    	  }


	    	  else{
	    		  finalQueryString = outerSelectQuery + "(" + selectQuery + fromQuery
	    				  + whereQuery + groupByQuery + ") aa left outer join ("
	    				  + productQuery + ") product " + joinClause;
	    	  }

	    	  // finalQueryString = selectQuery+fromQuery+whereQuery+groupByQuery;
	    	  //DF20171120:KO369761-For Chunking we are considering Limits in query.
	    	  //DF20190214:AB369654-Added order by clause in query.
	    	  finalQueryString = finalQueryString+" order by aa.Serial_Number limit "+startLimit+","+endLimit;


	    	  iLogger.info("Map Service:"+loginId+":"+loginUserTenancyList+":finalQueryString for MapService::getOverviewMapDetails: "
	    			  + finalQueryString);

	    	  DynamicAMS_DAL amsDaoObj = new DynamicAMS_DAL();

	    	  long startTime1 = System.currentTimeMillis();

	    	  /*responseImplList = amsDaoObj
	    			  .getQuerySpecificDetailsForMap(finalQueryString);*/
	    	  response = amsDaoObj
	    			  .getQuerySpecificDetailsForMap(finalQueryString,loginId);
	    	  responseImplList.addAll(response);

	    	  long endTime1 = System.currentTimeMillis();

	    	  iLogger.info("Map Service:"+loginId+":"+loginUserTenancyList+":MapService MAP BO2 DAL Query + Fill response object Execution Time in ms:"
	    			  + (endTime1 - startTime1));


	      }

	      catch (Exception e) {
	    	  e.printStackTrace();
	            fLogger.fatal("login id::"+loginId+" Exception :" + e);
	            StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				fLogger.fatal(stack.toString());
	      }

	      finally {
	          /*  if (session.isOpen())
	                  if (session.getTransaction().isActive()) {
	                        session.getTransaction().commit();
	                  }*/

	            if (session!=null && session.isOpen()) {
	                 // session.flush();
	                  session.close();
	            }
	      }

	      long endTime = System.currentTimeMillis();

	      iLogger.info("Map Service:"+loginId+":"+loginUserTenancyList+":MapService MAP BO From New AMS Webservice Execution Time in ms:"
	                  + (endTime - startTime));
	      iLogger.info("End of the Implementaiton Class login id ::"+loginId);

	      return responseImplList;

}
	
	//DF20160711 @Roopa reading map details from NEW AMS table via DAL Layer
	public List<MapImpl> getOverviewMapDetailsOld(String loginId, List<String> alertSeverityList, List<Integer> alertTypeList,
											   List<Integer> tenancyIdList, boolean isOwnStock,List<Integer> loginUserTenancyList)
	{
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	long startTime = System.currentTimeMillis();

		List<MapImpl> responseImplList = new LinkedList<MapImpl>();
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		List<String> assetMonitoringParameters = new LinkedList<String>();
		List<String> assetMonitoringValues = new LinkedList<String>();
		
		try
		{
			//Get the Contact Details - To find whether the user is TA or not
			Query contactQ = session.createQuery(" from ContactEntity where contact_id='"+loginId+"'");
			Iterator contactItr = contactQ.list().iterator();
			ContactEntity contact = null;
			while(contactItr.hasNext())
			{
				contact = (ContactEntity) contactItr.next();
			}
			
			
			List<Integer> userMachineGroupIdList = null;
			if(contact.getIs_tenancy_admin()==0)
			{
				Query groupUserQ = session.createQuery(" from GroupUserMapping where contact_id='"+loginId+"'");
				Iterator groupUserItr = groupUserQ.list().iterator();
				while(groupUserItr.hasNext())
				{
					if(userMachineGroupIdList==null)
						userMachineGroupIdList = new LinkedList<Integer>();
					GroupUserMapping groupUser = (GroupUserMapping)groupUserItr.next();
					userMachineGroupIdList.add(groupUser.getGroup_id().getGroup_id());
				}
			}
			
			String finalQueryString="",outerSelectQuery="",productQuery="";
			String selectQuery="",fromQuery="",whereQuery="",groupByQuery="",joinClause="";
			ListToStringConversion conversion = new ListToStringConversion();
			
			//--------------------------- Step1: Get the AccountId List corresponding to Login User Tenancy and Zone/Dealer/Customer filter if applied
			String userTenancyListAsString ;
			
			if( ! (tenancyIdList==null || tenancyIdList.isEmpty())) 
			{
				//Get the list of accounts corresponding to the child tenancy (If Zone/Dealer/Customer filter is applied)
				userTenancyListAsString = conversion.getIntegerListString(tenancyIdList).toString(); 
			}
			else
			{
				userTenancyListAsString = conversion.getIntegerListString(loginUserTenancyList).toString(); 
			}
			
			List<Integer> userAccList = new LinkedList<Integer>();
			Query accountQ = session.createQuery(" from AccountTenancyMapping where tenancy_id in ("+userTenancyListAsString+")");
			Iterator accountItr = accountQ.list().iterator();
			while(accountItr.hasNext())
			{
				AccountTenancyMapping accTen = (AccountTenancyMapping)accountItr.next();
				userAccList.add(accTen.getAccount_id().getAccount_id());
			}
			String userAccListAsString = conversion.getIntegerListString(userAccList).toString();
			
			
			//************************************* Case1: Get the List of all VINs under the hierarchy of user account
			if(userMachineGroupIdList==null)
			{
				//--------------------------- Step2: Main Query when no filters are applied
				
				//commenting old query
			/*	selectQuery=" select ams.serialNumber, CAST(GROUP_CONCAT(amd.parameterValue) As string ) as parameterValues," +
						" CAST(GROUP_CONCAT(amd.parameterId) As string ) as parameterId ";
				fromQuery=" from AssetEntity ass, AssetMonitoringSnapshotEntity ams, AssetMonitoringDetailEntity amd , AssetOwnerSnapshotEntity aos ";
				whereQuery =" where ass.serial_number=ams.serialNumber and ass.active_status=1 " +
						" and ams.transactionNumber=amd.transactionNumber and amd.parameterId in (1,2,4,18)" +
						" and ams.serialNumber=aos.serialNumber and ams.transactionTime >= aos.assetOwnershipDate " +
						" and aos.accountId in ("+userAccListAsString+")";
				groupByQuery=" group by ams.serialNumber ";
				
				//---------------------------- Step3: If 'own stock' filter is selected
				if(isOwnStock)
				{
					whereQuery=whereQuery+" and ass.primary_owner_id=aos.accountId ";
				}*/
				//commenting old query end
				
				/*selectQuery=" select ams.Serial_Number, ams.parameters, a.Engine_Number, ag.Asseet_Group_Name ";
                fromQuery="	from asset_monitoring_snapshot_new ams, asset a, asset_owner_snapshot aos, asset_group ag, products p"; 
				whereQuery=" where a.Serial_Number=ams.Serial_Number and a.Status=1 and ams.Serial_Number=aos.Serial_Number and ams.Latest_Transaction_Timestamp >= aos.Ownership_Start_Date and aos.Account_ID in ("+userAccListAsString+") and a.Product_ID=p.Product_ID and p.Asset_Group_ID=ag.Asset_Group_ID ";
				groupByQuery=" group by ams.Serial_Number ";
				
				whereQuery=whereQuery+" and ams.parameters IS NOT NULL ";*/
				
				/*selectQuery=" select ams.Serial_Number, ams.TxnData, a.Engine_Number, ag.Asseet_Group_Name ";
                fromQuery="	from asset_monitoring_snapshot ams, asset a, asset_owner_snapshot aos, asset_group ag, products p"; 
				whereQuery=" where a.Serial_Number=ams.Serial_Number and a.Status=1 and ams.Serial_Number=aos.Serial_Number and ams.Latest_Transaction_Timestamp >= aos.Ownership_Start_Date and aos.Account_ID in ("+userAccListAsString+") and a.Product_ID=p.Product_ID and p.Asset_Group_ID=ag.Asset_Group_ID ";
				groupByQuery=" group by ams.Serial_Number ";
				
				whereQuery=whereQuery+" and ams.TxnData IS NOT NULL ";*/
				
				//DF20170206 @Roopa Join query to get product details
				
				 outerSelectQuery = "select aa.*,product.* from ";
				selectQuery=" select ams.Serial_Number, ams.parameters, a.Engine_Number,a.Product_ID ";
                fromQuery="	from asset_monitoring_snapshot_new ams, asset a, asset_owner_snapshot aos"; 
				whereQuery=" where a.Serial_Number=ams.Serial_Number and a.Status=1 and ams.Serial_Number=aos.Serial_Number and ams.Latest_Transaction_Timestamp >= aos.Ownership_Start_Date and aos.Account_ID in ("+userAccListAsString+") ";
				groupByQuery=" group by ams.Serial_Number ";
				
				whereQuery=whereQuery+" and ams.parameters IS NOT NULL ";
				
				
	            
	             productQuery = "SELECT ag.Asseet_Group_Name, p.Product_ID "+
	                                  "FROM  asset_group ag, products p "+
	                                  "WHERE p.Asset_Group_ID=ag.Asset_Group_ID";
	             joinClause = "ON aa.Product_ID=product.Product_ID ";
	            
	           
	            
	            
				
				if(isOwnStock)
				{
					whereQuery=whereQuery+" and a.Primary_Owner_ID=aos.Account_ID ";
				}
				
			}
			
			//-------------------------------- Case2: Get the List of VINs under the machine group to which login user is tagged to
			else
			{
				String groupIdStringList = conversion.getIntegerListString(userMachineGroupIdList).toString();
				
				//commenting old query
				
		/*	//	System.out.println("groupIdStringList:"+ groupIdStringList);
				//--------------------------- Step2: Main Query when no filters are applied
				//DefectId:20150217 @ Suprava For IsTenancyAdmin 0 Query returning exception
				selectQuery=" select ams.serialNumber, CAST(GROUP_CONCAT(amd.parameterValue) As string ) as parameterValues," +
							" CAST(GROUP_CONCAT(amd.parameterId) As string ) as parameterId ";
				fromQuery=" from AssetEntity ass, AssetMonitoringSnapshotEntity ams, AssetMonitoringDetailEntity amd , AssetCustomGroupMapping cagm ";
				whereQuery =" where ass.serial_number=ams.serialNumber and ass.active_status=1 " +
				" and ams.transactionNumber=amd.transactionNumber and amd.parameterId in (1,2,4,18)" +
				" and ams.serialNumber=cagm.serial_number " +
				" and cagm.group_id in ("+groupIdStringList+")";
				groupByQuery=" group by ams.serialNumber ";
				
				//---------------------------- Step3: If 'own stock' filter is selected
				if(isOwnStock)
				{
					whereQuery=whereQuery+" and ass.primary_owner_id in ("+userAccListAsString+")";
				}*/
				
				//commenting old query end
				
				/*selectQuery=" select ams.Serial_Number, ams.parameters, a.Engine_Number,ag.Asseet_Group_Name ";
                fromQuery="	from asset_monitoring_snapshot_new ams, asset a, custom_asset_group_member cagm, asset_group ag, products p "; 
				whereQuery=" where a.Serial_Number=ams.Serial_Number and a.Status=1 and ams.Serial_Number=cagm.Serial_Number and cagm.Group_ID in ("+groupIdStringList+") and a.Product_ID=p.Product_ID and p.Asset_Group_ID=ag.Asset_Group_ID ";
				groupByQuery=" group by ams.Serial_Number ";
				whereQuery=whereQuery+" and ams.parameters IS NOT NULL ";*/
				
				//DF20170206 @Roopa Join query to get product details
				
				
				/*selectQuery=" select ams.Serial_Number, ams.TxnData, a.Engine_Number,ag.Asseet_Group_Name ";
                fromQuery="	from asset_monitoring_snapshot ams, asset a, custom_asset_group_member cagm, asset_group ag, products p "; 
				whereQuery=" where a.Serial_Number=ams.Serial_Number and a.Status=1 and ams.Serial_Number=cagm.Serial_Number and cagm.Group_ID in ("+groupIdStringList+") and a.Product_ID=p.Product_ID and p.Asset_Group_ID=ag.Asset_Group_ID ";
				groupByQuery=" group by ams.Serial_Number ";
				whereQuery=whereQuery+" and ams.TxnData IS NOT NULL ";*/
				
				outerSelectQuery = "select aa.*,product.* from ";
				selectQuery=" select ams.Serial_Number, ams.parameters, a.Engine_Number, a.Product_ID ";
                fromQuery="	from asset_monitoring_snapshot_new ams, asset a, custom_asset_group_member cagm "; 
				whereQuery=" where a.Serial_Number=ams.Serial_Number and a.Status=1 and ams.Serial_Number=cagm.Serial_Number and cagm.Group_ID in ("+groupIdStringList+") ";
				groupByQuery=" group by ams.Serial_Number ";
				whereQuery=whereQuery+" and ams.parameters IS NOT NULL ";
				
				 productQuery = "SELECT ag.Asseet_Group_Name, p.Product_ID "+
                         "FROM  asset_group ag, products p "+
                         "WHERE p.Asset_Group_ID=ag.Asset_Group_ID";
                 joinClause = "ON aa.Product_ID=product.Product_ID ";
				
				if(isOwnStock)
				{
					whereQuery=whereQuery+" and a.Primary_Owner_ID in ("+userAccListAsString+")";
				}
			}
			
			
			//--------------------------- Step4: If AlertType/Alert Severity is selected
			if((alertSeverityList!=null && alertSeverityList.size()>0) || (alertTypeList!=null && alertTypeList.size()>0))
			{
				//commenting old query
				
				/*String subQuery = " select distinct ae.serialNumber from AssetEventEntity ae where ae.serialNumber=ams.serialNumber and ae.activeStatus=1";
				if(alertSeverityList!=null && alertSeverityList.size()>0)
				{
					String eventSeverityListAsString = conversion.getStringList(alertSeverityList).toString();
					subQuery=subQuery+" and ae.eventSeverity in ("+eventSeverityListAsString+")";
				}
				
				if(alertTypeList!=null && alertTypeList.size()>0)
				{
					String eventTypeIdStringList = conversion.getIntegerListString(alertTypeList).toString();
					subQuery = subQuery+ " and ae.eventTypeId in ("+eventTypeIdStringList+")";
				}
				
				whereQuery=whereQuery+" and ams.serialNumber=("+subQuery+")";*/
				
				//commenting old query end
				//DF20190204 @abhishek---->add partition key for faster retrieval
				String subQuery = " select distinct ae.Serial_Number from asset_event ae where ae.Serial_Number=ams.Serial_Number and ae.Active_Status=1 and ae.PartitionKey =1 ";
				if(alertSeverityList!=null && alertSeverityList.size()>0)
				{
				String eventSeverityListAsString = conversion.getStringList(alertSeverityList).toString();
				subQuery=subQuery+" and ae.Event_Severity in ("+eventSeverityListAsString+")";
				}
				
				if(alertTypeList!=null && alertTypeList.size()>0)
				{
					String eventTypeIdStringList = conversion.getIntegerListString(alertTypeList).toString();
					subQuery = subQuery+ " and ae.Event_Type_ID in ("+eventTypeIdStringList+")";
				}
				
				whereQuery=whereQuery+" and ams.Serial_Number=("+subQuery+")";
			}
			
			//finalQueryString = selectQuery+fromQuery+whereQuery+groupByQuery;
			
			 finalQueryString = outerSelectQuery+"("+selectQuery+fromQuery+whereQuery+groupByQuery+") aa left outer join ("+productQuery+") product "+joinClause;
			 
			 iLogger.info("finalQueryString for MapService::getOverviewMapDetails: "+finalQueryString);
			
			
			DynamicAMS_DAL amsDaoObj=new DynamicAMS_DAL();
			
			long startTime1=System.currentTimeMillis();

			responseImplList=amsDaoObj.getQuerySpecificDetailsForMap(finalQueryString,loginId);
			
			long endTime1=System.currentTimeMillis();
			
			iLogger.info("MapService MAP BO DAL Query From New AMS Webservice Execution Time in ms:"+(endTime1-startTime1));

			//iLogger.info("AMS:persistDetailsToDynamicMySql::AMS DAL::getQuerySpecificDetails Size:"+responseImplList.size());
			
			//commenting old logic
			
			/*Query mapDetailsQ = session.createQuery(finalQueryString);
			Object[] resultSet = null;
			Iterator mapDetailsItr = mapDetailsQ.list().iterator();
			while(mapDetailsItr.hasNext())
			{
				resultSet = (Object[]) mapDetailsItr.next();
				MapImpl implObj = new MapImpl();
				
				if(resultSet[0]!=null)
				{
					AssetEntity asset = (AssetEntity)resultSet[0];
					implObj.setSerialNumber(asset.getSerial_number().getSerialNumber());
					implObj.setNickname(asset.getNick_name());
					
					if(asset.getProductId()!=null && asset.getProductId().getAssetGroupId()!=null)
						implObj.setProfileName(asset.getProductId().getAssetGroupId().getAsset_group_name());
				}
				
				if(resultSet[1] !=null )
					assetMonitoringValues = Arrays.asList(resultSet[1].toString().split(","));
				else
					assetMonitoringValues=new LinkedList<String>();
			
				if(resultSet[2] !=null)
					assetMonitoringParameters = Arrays.asList(resultSet[2].toString().split(","));
				else
					assetMonitoringParameters=new LinkedList<String>();
				
				HashMap<String,String> monitoringParametersMap = new HashMap<String,String>();
				for(int i=0; i<assetMonitoringParameters.size();i++)
				{
					monitoringParametersMap.put(assetMonitoringParameters.get(i), assetMonitoringValues.get(i));
				}
				
				implObj.setLatitude(monitoringParametersMap.get("1"));
				implObj.setLongitude(monitoringParametersMap.get("2"));
				implObj.setTotalMachineHours(monitoringParametersMap.get("4"));
				implObj.setEngineStatus(monitoringParametersMap.get("18"));
			
				
				responseImplList.add(implObj);
			}*/
			
			
			//commenting old logic end
		}
		
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}
		
		finally
		{
			if(session.isOpen())
			if(session.getTransaction().isActive())
			{
				session.getTransaction().commit();
			}
		
			if(session.isOpen())
			{
				session.flush();
				session.close();
			}
		}
		
        long endTime=System.currentTimeMillis();
		
        iLogger.info("MapService MAP BO From New AMS Webservice Execution Time in ms:"+(endTime-startTime));
		
		return responseImplList;
		
	}
	
	//DF20160714 @Roopa Fleet map details from new AMS table
	
	
	public MapImpl getFleetMapDetails(String SerialNumber,String loginId, List<Integer> customMachineGroupIdList)
	{
		MapImpl implObj = new MapImpl();
		
		Logger iLogger = InfoLoggerClass.logger;
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	String finalQueryString="";
		String selectQuery="",fromQuery="",whereQuery="";
		
		try
		{
			iLogger.info("Into the BO Class serial_number ::"+SerialNumber+" loginId::"+loginId);
//			Keerthi : 21.09.2016: outer join to product table
			
			/*selectQuery=" select ams.Serial_Number, ams.parameters, a.Engine_Number, ag.Asseet_Group_Name, ap.operatingStartTime, ap.operatingEndTime ";
            fromQuery="	from asset_monitoring_snapshot_new ams, asset a, asset_group ag, products p, asset_profile ap"; 
			whereQuery=" where a.Serial_Number=ams.Serial_Number and a.Product_ID=p.Product_ID and p.Asset_Group_ID=ag.Asset_Group_ID and ams.Serial_Number=ap.serialNumber and ams.Serial_Number='"+SerialNumber+"' ";*/
			
			/*String outerSelectQuery = "select aa.*,product.* from ";
			selectQuery=" select ams.Serial_Number, ams.parameters, a.Engine_Number,a.Product_ID";
            fromQuery=" from asset_monitoring_snapshot_new ams, asset a ";
            whereQuery=" where a.Serial_Number=ams.Serial_Number and ams.Serial_Number='"+SerialNumber+"' ";
            
            String productQuery = "SELECT ag.Asseet_Group_Name, ap.operatingStartTime, ap.operatingEndTime,p.Product_ID "+
                                  "FROM  asset_group ag, products p, asset_profile ap "+
                                  "WHERE p.Asset_Group_ID=ag.Asset_Group_ID";
            String joinClause = "ON aa.Product_ID=product.Product_ID ";
            finalQueryString = outerSelectQuery+"("+selectQuery+fromQuery+whereQuery+") aa left outer join ("+productQuery+") product "+joinClause;*/
			
			//DF20161221 @Roopa FEtching fleet map details from the txndata json column in assetmonitoringsnapshot table
			//DF20171117 : Deepthi: added the serialNumber in the where clause
			
			//KO369761 : 11/07/18 : SEARCH : if m/c no.(7 digits) provided , get corresponding PIN
			if(SerialNumber!=null)
			{
				if(SerialNumber.trim().length()==7)
				{
					String machineNumber = SerialNumber;
					SerialNumber = new AssetDetailsBO().getSerialNumberMachineNumber(machineNumber);
					if(SerialNumber==null)
					{//invalid machine number
						iLogger.info("Machine number "+ machineNumber + "does not exist !!!");
						return implObj ;
					}
				}
			}
			
			String outerSelectQuery = "";
			selectQuery="";
            fromQuery="";
            whereQuery="";
            
            String productQuery = "SELECT ag.Asseet_Group_Name, ap.operatingStartTime, ap.operatingEndTime,p.Product_ID "+
                                  "FROM  asset_group ag, products p, asset_profile ap "+
                                  "WHERE p.Asset_Group_ID=ag.Asset_Group_ID and ap.serialNumber = '"+SerialNumber+"' ";
            String joinClause = "ON aa.Product_ID=product.Product_ID ";
            
			if(customMachineGroupIdList==null ||(customMachineGroupIdList!=null && customMachineGroupIdList.isEmpty()))
		{		
			outerSelectQuery = "select aa.*,product.* from ";
			selectQuery=" select ams.Serial_Number, ams.TxnData, a.Engine_Number,a.Product_ID";
            fromQuery=" from asset_monitoring_snapshot ams, asset a ";
            whereQuery=" where a.Serial_Number=ams.Serial_Number and ams.Serial_Number='"+SerialNumber+"' ";
            
		}else{
			outerSelectQuery = "select aa.*,product.* from ";
			selectQuery=" select ams.Serial_Number, ams.TxnData, a.Engine_Number,a.Product_ID";
            fromQuery=" from asset_monitoring_snapshot ams, asset a,custom_asset_group_snapshot cags ";
            whereQuery=" where a.Serial_Number=ams.Serial_Number and ams.Serial_Number = cags.Asset_Id and ams.Serial_Number='"+SerialNumber+"' ";
            if(customMachineGroupIdList !=null){
  			  String groupIdStringList = AssetUtil.getIDListAsCommaSeperated(customMachineGroupIdList);
  			  whereQuery = whereQuery+" and cags.Group_ID in("+groupIdStringList+") ";
            }
		}
            finalQueryString = outerSelectQuery+"("+selectQuery+fromQuery+whereQuery+") aa left outer join ("+productQuery+") product "+joinClause;
            
            
            
			iLogger.info("Map Service:"+SerialNumber+": loginid ::"+loginId+" finalQueryString for MapService::getFleetMapDetails: "+finalQueryString);
			
			
			DynamicAMS_DAL amsDaoObj=new DynamicAMS_DAL();
			
			long startTime1=System.currentTimeMillis();

			implObj=amsDaoObj.getQuerySpecificDetailsForFleetMap(finalQueryString,loginId);
			
			long endTime1=System.currentTimeMillis();
			
			iLogger.info("Map Service:"+SerialNumber+": loginid ::"+loginId+":MapService Fleet MAP BO DAL Query Execution From New AMS Webservice Execution Time in ms:"+(endTime1-startTime1));

			
		//commenting below part since Severity is not getting used in UI
			
			/*Query alertQ = session.createQuery(" from AssetEventEntity where serialNumber='"+SerialNumber+"' and activeStatus=1" +
					" group by eventSeverity order by eventSeverity");
			Iterator alertItr = alertQ.list().iterator();
			if(alertItr.hasNext())
			{
				AssetEventEntity assetEvent = (AssetEventEntity)alertItr.next();
				implObj.setSeverity(assetEvent.getEventSeverity());
			}*/
			
		}
		
		catch(Exception e)
		{
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			e.printStackTrace();
			fLogger.fatal("login id::"+loginId+"Exception :"+e);
			fLogger.fatal(stack.toString());
		}
		
		iLogger.info("End of the BO Class serial_number ::"+SerialNumber+"loginId:"+loginId);
		return implObj;
	}
	
	//commenting old method
	
/*	public MapImpl getFleetMapDetails(String SerialNumber)
	{
		MapImpl implObj = new MapImpl();
		
    	Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		List<String> assetMonitoringParameters = new LinkedList<String>();
		List<String> assetMonitoringValues = new LinkedList<String>();
		
		try
		{
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Query mapDetailsQ = session.createQuery(" select ams.serialNumber, " +
					" CAST(GROUP_CONCAT(amd.parameterValue) As string ) as parameterValues, " +
					" CAST(GROUP_CONCAT(amd.parameterId) As string ) as parameterId, "+
					" ap " +
					" from AssetEntity ass, AssetMonitoringSnapshotEntity ams, AssetMonitoringDetailEntity amd," +
					" AssetExtendedDetailsEntity ap " +
					" where ass.serial_number=ams.serialNumber and ass.serial_number=ap.serial_number" +
					" and ams.serialNumber='"+SerialNumber+"'" +
					" and ams.transactionNumber=amd.transactionNumber" +
					" and amd.parameterId in (1,2,4,18) ");
			Object[] resultSet=null;
			Iterator mapDetailsItr = mapDetailsQ.list().iterator();
			while(mapDetailsItr.hasNext())
			{
				resultSet = (Object[]) mapDetailsItr.next();
				
				if(resultSet[0]!=null)
				{
					AssetEntity asset = (AssetEntity)resultSet[0];
					implObj.setSerialNumber(asset.getSerial_number().getSerialNumber());
					implObj.setNickname(asset.getNick_name());
					
					if(asset.getProductId()!=null && asset.getProductId().getAssetGroupId()!=null)
						implObj.setProfileName(asset.getProductId().getAssetGroupId().getAsset_group_name());
				}
				
				if(resultSet[1] !=null )
					assetMonitoringValues = Arrays.asList(resultSet[1].toString().split(","));
				else
					assetMonitoringValues=new LinkedList<String>();
			
				if(resultSet[2] !=null)
					assetMonitoringParameters = Arrays.asList(resultSet[2].toString().split(","));
				else
					assetMonitoringParameters=new LinkedList<String>();
				
				HashMap<String,String> monitoringParametersMap = new HashMap<String,String>();
				for(int i=0; i<assetMonitoringParameters.size();i++)
				{
					monitoringParametersMap.put(assetMonitoringParameters.get(i), assetMonitoringValues.get(i));
				}
				
				implObj.setLatitude(monitoringParametersMap.get("1"));
				implObj.setLongitude(monitoringParametersMap.get("2"));
				implObj.setTotalMachineHours(monitoringParametersMap.get("4"));
				implObj.setEngineStatus(monitoringParametersMap.get("18"));
				
				if(resultSet[1]!=null)
				{
					String parameterValues[]=resultSet[1].toString().split(",");
					if(parameterValues.length>3)
					{
						implObj.setLatitude(parameterValues[0]);
						implObj.setLongitude(parameterValues[1]);
						implObj.setTotalMachineHours(parameterValues[2]);
						implObj.setEngineStatus(parameterValues[3]);
					}
				}
				
				if(resultSet[3]!=null)
				{
					AssetExtendedDetailsEntity assetProfile = (AssetExtendedDetailsEntity)resultSet[3];
					implObj.setOperatingStartTime(assetProfile.getOperatingStartTime());
					implObj.setOperatingEndTime(assetProfile.getOperatingEndTime());	
					
				}
					
				
			}
			
			Query alertQ = session.createQuery(" from AssetEventEntity where serialNumber='"+SerialNumber+"' and activeStatus=1" +
					" group by eventSeverity order by eventSeverity");
			Iterator alertItr = alertQ.list().iterator();
			if(alertItr.hasNext())
			{
				AssetEventEntity assetEvent = (AssetEventEntity)alertItr.next();
				implObj.setSeverity(assetEvent.getEventSeverity());
			}
			
		}
		
		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}
		
		finally
		{
			if(session.isOpen())
			if(session.getTransaction().isActive())
			{
				session.getTransaction().commit();
			}
		
			if(session.isOpen())
			{
				session.flush();
				session.close();
			}
		}
		
		return implObj;
	}*/
	
	//DF20190523:Abhishek::Metod added to check the user in group user table.
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

