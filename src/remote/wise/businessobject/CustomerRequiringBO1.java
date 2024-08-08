package remote.wise.businessobject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.EventTypeEntity;
import remote.wise.businessentity.TenancyDimensionEntity;
import remote.wise.dal.DynamicAMS_DAL;
import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AmsDAO;
import remote.wise.service.datacontract.AssetServiceScheduleGetReqContract;
import remote.wise.service.datacontract.AssetServiceScheduleGetRespContract;
import remote.wise.service.implementation.AssetServiceScheduleImpl;
import remote.wise.service.implementation.CustomerRequiringImpl;
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
public class CustomerRequiringBO1 {

	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("CustomerRequiringBO1:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("CustomerRequiringBO1:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("CustomerRequiringBO1:","info");*/
	
	
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
			List<String> serialList = new LinkedList<String>();
			//DF20190204 @abhishek---->add partition key for faster retrieval
			String queryString2 = "select a.serialNumber,f.account_name, f.mobile_no  "
					+ "from AssetEventEntity a, AssetAccountMapping e, AccountEntity f "
					+ "where a.activeStatus=1 and a.partitionKey =1 and f.status=true and a.serialNumber in "
					+ "(select b.serialNumber from AssetAccountMapping b ,TenancyBridgeEntity c ,AccountTenancyMapping d"
					+ "	where c.parentId in ('"
					+ tenancyIdStringList 
					+ "') and c.parentId != c.childId and d.tenancy_id=c.childId and c.level<>0 and b.accountId=d.account_id group by b.serialNumber )"
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
			iLogger.info(" serialList" + serialList.size());
			String serialStringList = conversionObj.getStringList(serialList)
					.toString();
			iLogger.info(" serialStringList" + serialStringList);

			iLogger
					.info(" *******Get maximum Parameter id for given Engine hours*********************");
		/*	Query query3 = session
					.createQuery("select max(d.parameterId ) from MonitoringParameters d where d.parameterName like 'Hour'");
			Iterator itr3 = query3.list().iterator();
			int parameterId2 = 0;
			while (itr3.hasNext()) {
				parameterId2 = (Integer) itr3.next();
				iLogger.info(" parameterId for EngineHours is "
						+ parameterId2);
			}*/
									
					List<String> customerNameList = new LinkedList<String>();
				List<String> contactList = new LinkedList<String>();
		

				//Added by Juhi for defect :1245
				List<Integer> primaryId = new LinkedList<Integer>();
				Query query10 = session.createQuery("select account_name,mobile_no ,account_id from AccountEntity where status=true and account_id =(select primary_owner_id from AssetEntity where serial_number in ("+serialStringList+")  group by primary_owner_id ) group by account_id ");
				Object result2[] = null;
				Iterator itr10 = query10.list().iterator();
				while (itr10.hasNext()) {
					result2 = (Object[]) itr10.next();
					customerNameList.add((String) result2[0]);
					contactList.add((String) result2[1]);
					primaryId.add((Integer)result2[2]);
				}
				//Added by Juhi for defect :1245
			/*	Query quer=session.createQuery("select primary_owner_id from AssetEntity where serial_number in ("+serialStringList+") group by primary_owner_id");
				
				Iterator itr9 = quer.list().iterator();
				while (itr9.hasNext()) {
				
					primaryId.add((Integer) itr9.next());
				//	Integer number = ((Long) result3[1]).intValue();
					//NumberOfMachineDueList.add(number);
				}*/
				String primaryIdStringList = conversionObj
				.getIntegerListString(primaryId).toString();
				
//		List<AssetControlUnitEntity> serialNumber=new LinkedList<AssetControlUnitEntity>();
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
	                          //DF20190204 @abhishek---->add partition key for faster retrieval
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
	                          //DF20190204 @abhishek---->add partition key for faster retrieval
	                            Query query8 = session
	                                        .createQuery(" select count(activeStatus) from AssetEventEntity where eventTypeId!="
	                                                    + evtTypeId1
	                                                    + " and activeStatus="
	                                                    + ActiveAlert
	                                                    + "  and partitionKey =1 and serialNumber ='"
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

}//end of impl addition
			
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
