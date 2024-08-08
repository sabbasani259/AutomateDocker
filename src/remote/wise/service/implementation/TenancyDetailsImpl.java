package remote.wise.service.implementation;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import remote.wise.businessentity.AccountTenancyMapping;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.AssetExtendedDetailsEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.EventSubscriptionMapping;
import remote.wise.businessentity.TenancyEntity;
import remote.wise.businessentity.TenancyTypeEntity;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.businessobject.EventDetailsBO;
import remote.wise.businessobject.TenancyBO;
import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.TenancyCreationReqContract;
import remote.wise.service.datacontract.TenancyDetailsReqContract;
import remote.wise.service.datacontract.TenancyDetailsRespContract;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

/** Implementation class to handle Tenancy details
 * @author Rajani Nagaraju
 *
 */
public class TenancyDetailsImpl 
{

	//DefectId:1337 -Suprava - 20130923 - Log4j Changes - Using static logger object all throughout the application

	//public static WiseLogger infoLogger = WiseLogger.getLogger("TenancyDetailsImpl:","info");
	//public static WiseLogger businessError = WiseLogger.getLogger("TenancyDetailsImpl:","businessError");
	//public static WiseLogger fatalError = WiseLogger.getLogger("TenancyDetailsImpl:","fatalError");
	/** This method sets the Tenancy Details 
	 * @param reqObj Data to create a tenancy and set the details
	 * @return returns the status String
	 * @throws IOException 
	 * @throws CustomFault
	 * @throws ParseException
	 */
	public String createTenancy(TenancyCreationReqContract reqObj) throws CustomFault, ParseException, IOException
	{

		String status = "SUCCESS";

		TenancyBO tenancyBoObj = new TenancyBO();
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		//Step 0: Get the tenancy type of the tenancy to be created/update from accountID
		int tenancyTypeId=0;
		//DefectId:950,1015 - Modified by Rajani Nagaraju - Receiver1,2 swapping issue
		String actualTenancyTypeName =null;

		String oemTenancyType = null;
		String dealerTenancyType = null;
		String customerTenancyType = null;

		String oemAccount = null;
		String dealerAccount = null;
		String customerAccount = null;

		/*Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger businessError = Logger.getLogger("businessErrorLogger");*/
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Logger iLogger = InfoLoggerClass.logger;
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		     session.beginTransaction();

		try
		{
			if(reqObj.getLoginId()==null)
				throw new CustomFault("LoginId not specified");

			SimpleDateFormat datetimeFormat = new SimpleDateFormat("hh:mm:ss a");
			Timestamp startTime = null;
			Timestamp endTime = null;

			if(reqObj.getOperatingStartTime()!=null)
			{
				Date operatingStartDate = datetimeFormat.parse(reqObj.getOperatingStartTime());
				startTime = new Timestamp(operatingStartDate.getTime());
			}


			if(reqObj.getOperatingEndTime()!=null)
			{
				Date operatingEndDate = datetimeFormat.parse(reqObj.getOperatingEndTime());
				endTime = new Timestamp(operatingEndDate.getTime());
			}

			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			oemTenancyType= prop.getProperty("ZonalTenancyType");
			dealerTenancyType= prop.getProperty("DealerTenancyType");
			customerTenancyType= prop.getProperty("CustomerTenancyType");

			//Logic to get TenancyTypeId
			if(reqObj.getChildTenancyId()==0)
			{
				oemAccount= prop.getProperty("OEMAccount");
				dealerAccount= prop.getProperty("DealerAccount");
				customerAccount = prop.getProperty("CustomerAccount");

				if(reqObj.getAccountId()==0)
					throw new CustomFault("Account Id is not specified");
				
				//DF20171011: KO369761 - Security Check added for input text fields.
				CommonUtil util = new CommonUtil();
				String isValidinput=null;
				
				isValidinput = util.inputFieldValidation(reqObj.getChildTenancyName());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}


				/*if(reqObj.getTenancyAdminEmailId()==null)
					throw new CustomFault("Tenancy Admin emailId to specified");*/



				Query query = session.createQuery("select b.reversePartnerRole from PartnershipMapping a, PartnerRoleEntity b where " +
						" a.partnerId = b.partnerId and a.accountToId= "+reqObj.getAccountId());
				Iterator itr = query.list().iterator();

				String tenancyTypeName = null;

				while(itr.hasNext())
				{
					String accountType = (String)itr.next();
					if(accountType.contains(oemAccount))
					{
						tenancyTypeName = oemTenancyType;
						actualTenancyTypeName = oemTenancyType;
					}
					else if (accountType.equalsIgnoreCase(dealerAccount))
					{
						tenancyTypeName = dealerTenancyType;
						actualTenancyTypeName = dealerTenancyType;
					}
					else if (accountType.equalsIgnoreCase(customerAccount))
					{
						tenancyTypeName = customerTenancyType;
						actualTenancyTypeName = customerTenancyType;
					}
				}


				Query query2 = session.createQuery(" from TenancyTypeEntity where tenancy_type_name='"+tenancyTypeName+"'");
				Iterator itr2 = query2.list().iterator();

				while(itr2.hasNext())
				{
					TenancyTypeEntity tenancyType = (TenancyTypeEntity) itr2.next();
					tenancyTypeId = tenancyType.getTenancy_type_id();
				}


				if(tenancyTypeId==0)
					throw new CustomFault("Could not determine Tenancy Type");
			}

			//DF20140224 - Rajani Nagaraju - Check for TA duplicate contactID or emailID before creating tenancy
			//STEP0: If it is the new Tenancy Creation, check for the duplicate Mobile Number / Email Address in which case user cannot be created,
			// and without the TA, Tenancy cannot be created


			//DF20141219 - Rajani Nagaraju - Removing all the checks related to user creation, Since tenancy should be created irrespective of 
			//TA creation which can be done later in application. If the tenancy is not created, machine alerts will not appear even to JCB/Delaer
			/*if(reqObj.getChildTenancyId()==0)
			{
				Query query = session.createQuery("from ContactEntity where primary_mobile_number ='"
						+ reqObj.getTenancyAdminPhoneNumber()+ "'and active_status=true");
				List list = query.list();
				Iterator iterator = list.iterator();
				if ((iterator.hasNext()) && (list.size() > 0)&& (list.get(0) != null))
				{
					while (iterator.hasNext()) 
					{
						ContactEntity contact = (ContactEntity) iterator.next();
						businessError.error(" Mobile Number already exists. Please enter different Mobile Number .");
						throw new CustomFault("Mobile Number already exists. Please enter different Mobile Number .");
					}
				}

				Query query1 = session.createQuery("from ContactEntity where primary_email_id ='"+ reqObj.getTenancyAdminEmailId()
							+ "' and active_status=true ");
				List list1 = query1.list();
				Iterator iterator1 = list1.iterator();
				if ((iterator1.hasNext()) && (list1.size() > 0)	&& (list1.get(0) != null)) 
				{
					while (iterator1.hasNext()) 
					{
						ContactEntity contact = (ContactEntity) iterator1.next();
						businessError.error("Email Id already exists. Please enter different Email Id .");
						throw new CustomFault("Email Id already exists. Please enter different Email Id .");
					}
				}
			}*/


			//STEP1: create or Edit tenancy details
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
				if(session.getTransaction().isActive() && session.isDirty())
				{
					iLogger.info("Opening a new session");
					session = HibernateUtil.getSessionFactory().openSession();
				}

			}
			
			int tenancyId = tenancyBoObj.createTenancy(reqObj.getLoginId(), reqObj.getAccountId(), reqObj.getParentTenancyId(), reqObj.getChildTenancyId(), 
					reqObj.getChildTenancyName(), tenancyTypeId, startTime, endTime);
			if(tenancyId==-1)
				return "Child tenancy with the specified name already exists !!";

	
			//STEP2: Map the account to newly created Tenancy
			if(reqObj.getAccountId()!=0)
				tenancyBoObj.accountTenancyMapping(reqObj.getAccountId(), tenancyId);

			

			//STEP3: Create a tenancyAdmin user for the newly created tenancy
			/*if(reqObj.getChildTenancyId()==0)
			{

				//DF20141219 - Rajani Nagaraju - Removing all the checks related to user creation, Since tenancy should be created irrespective of 
	    		//TA creation which can be done later in application. If the tenancy is not created, machine alerts will not appear even to JCB/Delaer
				if(reqObj.getChildTenancyId()==0)
				{
					Query query = session.createQuery("from ContactEntity where primary_mobile_number ='"
							+ reqObj.getTenancyAdminPhoneNumber()+ "'and active_status=true");
					List list = query.list();
					Iterator iterator = list.iterator();
					if ((iterator.hasNext()) && (list.size() > 0)&& (list.get(0) != null))
					{
						while (iterator.hasNext()) 
						{
							ContactEntity contact = (ContactEntity) iterator.next();
							businessError.error(" Mobile Number already exists. Please enter different Mobile Number .");
							throw new CustomFault("Mobile Number already exists. Please enter different Mobile Number .");
						}
					}

					Query query1 = session.createQuery("from ContactEntity where primary_email_id ='"+ reqObj.getTenancyAdminEmailId()
								+ "' and active_status=true ");
					List list1 = query1.list();
					Iterator iterator1 = list1.iterator();
					if ((iterator1.hasNext()) && (list1.size() > 0)	&& (list1.get(0) != null)) 
					{
						while (iterator1.hasNext()) 
						{
							ContactEntity contact = (ContactEntity) iterator1.next();
							businessError.error("Email Id already exists. Please enter different Email Id .");
							throw new CustomFault("Email Id already exists. Please enter different Email Id .");
						}
					}
				}


				UserDetailsBO userDetailsBo = new UserDetailsBO();
				List<Integer> asset_group_id = new LinkedList<Integer>();
				List<String> asset_group_name = new LinkedList<String>();
				status = userDetailsBo.setUserDetails(null, reqObj.getTenancyAdminFirstName(), reqObj.getTenancyAdminLastName(),
						reqObj.getTenancyAdminRoleId(),null,reqObj.getTenancyAdminPhoneNumber(), 1, reqObj.getCountryCode(), tenancyId, asset_group_id 	, asset_group_name, null, null,reqObj.getTenancyAdminEmailId());

				if(! (session.isOpen() ))
                {
                            session = HibernateUtil.getSessionFactory().getCurrentSession();
                            session.getTransaction().begin();
                }

				ContactEntity tenancyAdmin = null;
				Query query1 = session.createQuery("from ContactEntity where primary_email_id='"+reqObj.getTenancyAdminEmailId()+"'");
				Iterator itr1 = query1.list().iterator();
				while(itr1.hasNext())
				{
					tenancyAdmin = (ContactEntity)itr1.next();
				}

				if(tenancyAdmin==null)
					return "FAILURE";

				//Assign tenancy Admin to the corresponding account
				tenancyBoObj.setAccountContact(reqObj.getAccountId(), tenancyAdmin.getContact_id());


				//STEP4: For all machines under the tenancy make tenancy admin as default primary contact
				AssetDetailsBO assetDetails = new AssetDetailsBO();
				List<AssetEntity> assetEntityList = assetDetails.getAccountAssets(reqObj.getAccountId());
				EventDetailsBO eventDetailsBO = new EventDetailsBO();

				if(! (session.isOpen() ))
                {
                            session = HibernateUtil.getSessionFactory().getCurrentSession();
                            session.getTransaction().begin();
                }

				List<ContactEntity> primaryContactId = new LinkedList<ContactEntity>();
				primaryContactId.add(tenancyAdmin);

//				Keerthi : Defect ID : 1177 :Receiver 1,2,3 : set method takes list of string
				//DefectId:950,1015 - Modified by Rajani Nagaraju - Receiver1,2 swapping issue
				List<String> primaryContactIdList = new ArrayList<String>();

				if(actualTenancyTypeName.equalsIgnoreCase(customerTenancyType))
				{
					primaryContactIdList.add(tenancyAdmin.getContact_id()+",2");
				}
				else
				{
					primaryContactIdList.add(tenancyAdmin.getContact_id()+",1");
				}

				//If there are any assets tagged to the account
				if( ! (assetEntityList==null || assetEntityList.isEmpty()) )
				{
					for(int j=0; j<assetEntityList.size(); j++)
					{
						if(! (session.isOpen() ))
		                {
		                            session = HibernateUtil.getSessionFactory().getCurrentSession();
		                            session.getTransaction().begin();
		                }

						//DefectId:950,1015 - Modified by Rajani Nagaraju - Receiver1,2 swapping issue
						List<String> newContactEntity = new LinkedList<String>();
						newContactEntity.addAll(primaryContactIdList);
						if(actualTenancyTypeName.equalsIgnoreCase(customerTenancyType))
						{
							Query q = session.createQuery(" from EventSubscriptionMapping where serialNumber='"+assetEntityList.get(j).getSerial_number().getSerialNumber()+"'" +
									" and priority=1");
							Iterator itrt = q.list().iterator();
							while(itrt.hasNext())
							{
								EventSubscriptionMapping eventSubs = (EventSubscriptionMapping)itrt.next();
								newContactEntity.add((eventSubs.getContactId().getContact_id())+",1");
							}
						}
						//DefectId:950,1015 - Modified by Rajani Nagaraju - Receiver1,2 swapping issue
						eventDetailsBO.setEventSubscription(assetEntityList.get(j),newContactEntity);


					}
				}
			}*/

	
			//STEP5: Assign the list of Users to the Child tenancy 
			if(! (reqObj.getParentTenancyUserIdList()==null || reqObj.getParentTenancyUserIdList().isEmpty()))
				tenancyBoObj.setDelegateTenancyUsers(tenancyId, reqObj.getParentTenancyUserIdList());


			//STEP6: Set Machine Operating hours
			AssetDetailsBO assetDetails = new AssetDetailsBO();
			List<AssetEntity> assetEntityList = new LinkedList<AssetEntity>();

		
			 
			/*Query query3 = session.createQuery("select a.serialNumber from AssetAccountMapping a, AccountTenancyMapping b where" +
			" a.accountId = b.account_id and b.tenancy_id ="+tenancyId);*/

			//DefectId:839 - Rajani Nagaraju - 20131213 - To enable Machine Movement between tenancies
			//ASSUMPTION: Once the tenancy is created, only the owner can change the Tenancy Name, Tenancy OP hours and also the CheckBox:Make this operating hours applicable to all machines
			/*Query query3 = session.createQuery("select a from AssetEntity a, AccountTenancyMapping b where" +
			" a.primary_owner_id = b.account_id and b.tenancy_id ="+tenancyId);*/
			 if((session!=null &&  session.isOpen() )){
				 session.close();
			 }
			 
			 if(session==null || ! (session.isOpen() )) 
				{
					session = HibernateUtil.getSessionFactory().openSession();	
					
					session.beginTransaction();
				}
			 
			/*Query assetQuery = session.createQuery("select a from AssetEntity a, AccountTenancyMapping b, TenancyEntity c " +
					" where a.primary_owner_id = b.account_id and b.tenancy_id=c.tenancy_id" +
					" and c.tenancyCode = (select d.tenancyCode from TenancyEntity d where d.tenancy_id ='"+tenancyId+"') ");
			
			Query assetQuery = session.createQuery("select a from AssetEntity a, AccountTenancyMapping b " +
					" where a.primary_owner_id = b.account_id and b.tenancy_id='"+tenancyId+"' ");
			
			Query assetQuery = session.createQuery("from AssetEntity a " +
					" where a.primary_owner_id = '" + reqObj.getAccountId() +"' ");
			*/
			 
			 Query assetQuery = session.createQuery("from AssetEntity a" +
			 		" where a.primary_owner_id = " +
			 		" ( select b.account_id" +
			 		" from AccountTenancyMapping b, TenancyEntity c" +
			 		" where  b.account_id = c.tenancy_id" +
			 		" and c.tenancyCode = " +
			 		" (select d.tenancyCode from TenancyEntity d where d.tenancy_id ='"+tenancyId+"')" +
			 		" ) ");
			 
			 
			 
			List assetList = assetQuery.list();
			Iterator assetItr = assetList.iterator();
			
			
			while(assetItr.hasNext())
			{
			
				AssetEntity asset = (AssetEntity) assetItr.next();
				assetEntityList.add(asset);
			
			}

			
			if(! (assetEntityList==null || assetEntityList.isEmpty()))
			{
				
				if(reqObj.isOverrideMachineOperatingHours()==true)
				{
					Query query4 = session.createQuery("update AssetExtendedDetailsEntity set OperatingStartTime='"+startTime+"' , " +
							" OperatingEndTime ='"+endTime+"' where serial_number in (:list)").setParameterList("list", assetEntityList);
					query4.executeUpdate();
					
				}
				else
				{
					
					Query query5 = session.createQuery("from AssetExtendedDetailsEntity where OperatingStartTime=NULL and serial_number in (:list)").setParameterList("list", assetEntityList);
					
					Iterator itr5 = query5.list().iterator();
					
					while(itr5.hasNext())
					{
						AssetExtendedDetailsEntity assetExtended = (AssetExtendedDetailsEntity)itr5.next();
						assetExtended.setOperatingStartTime(startTime);
						assetExtended.setOperatingEndTime(endTime);
						session.update(assetExtended);
					}


				}
				

				try
				{ 
					if(session.isOpen())            	
					{
						if(session.getTransaction().isActive())
						{
							session.flush();
							session.getTransaction().commit();
						}              	 
					}
				}

				catch(Exception e)
				{
					status = "FAILURE-"+e.getMessage();
					e.printStackTrace();
					fLogger.fatal("Exception in commiting the record:"+e.getMessage());
				}
			}
				

				//DF
				//STEP3: Create a tenancyAdmin user for the newly created tenancy
				
				if(session ==null || ! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
					if(session.getTransaction().isActive() && session.isDirty())
					{
						iLogger.info("Opening a new session");
						session = HibernateUtil.getSessionFactory().openSession();
					}

				}
				
				session.beginTransaction();
				
				if(reqObj.getChildTenancyId()==0)
				{

					
					//DF20141219 - Rajani Nagaraju - Removing all the checks related to user creation, Since tenancy should be created irrespective of 
					//TA creation which can be done later in application. If the tenancy is not created, machine alerts will not appear even to JCB/Delaer
					Query query = session.createQuery("from ContactEntity where primary_mobile_number ='"
							+ reqObj.getTenancyAdminPhoneNumber()+ "'and active_status=true");
					List list = query.list();
					Iterator iterator = list.iterator();
					if ((iterator.hasNext()) && (list.size() > 0)&& (list.get(0) != null))
					{
						while (iterator.hasNext()) 
						{
							ContactEntity contact = (ContactEntity) iterator.next();
							bLogger.error(" Mobile Number already exists. Please enter different Mobile Number .");
							throw new CustomFault("Mobile Number already exists. Please enter different Mobile Number .");
						}
					}

					
					//DF20150318 - Rajani Nagaraju - If the Email Id is null, dont check for duplicate emailId
					if(!(reqObj.getTenancyAdminEmailId()==null || reqObj.getTenancyAdminEmailId().trim().length()==0))
					{
						Query query1 = session.createQuery("from ContactEntity where primary_email_id ='"+ reqObj.getTenancyAdminEmailId()
								+ "' and active_status=true ");
						List list1 = query1.list();
						Iterator iterator1 = list1.iterator();
						if ((iterator1.hasNext()) && (list1.size() > 0)	&& (list1.get(0) != null)) 
						{
							while (iterator1.hasNext()) 
							{
								ContactEntity contact = (ContactEntity) iterator1.next();
								bLogger.error("Email Id already exists. Please enter different Email Id .");
								throw new CustomFault("Email Id already exists. Please enter different Email Id .");
							}
						}
					}
					


					UserDetailsBO userDetailsBo = new UserDetailsBO();
					List<Integer> asset_group_id = new LinkedList<Integer>();
					List<String> asset_group_name = new LinkedList<String>();
					
					status = userDetailsBo.setUserDetails(null, reqObj.getTenancyAdminFirstName(), reqObj.getTenancyAdminLastName(),
							reqObj.getTenancyAdminRoleId(),null,reqObj.getTenancyAdminPhoneNumber(), 1, reqObj.getCountryCode(), tenancyId, asset_group_id 	, asset_group_name, null, null,reqObj.getTenancyAdminEmailId());
					
					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
						if(session.getTransaction().isActive() && session.isDirty())
						{
							iLogger.info("Opening a new session");
							session = HibernateUtil.getSessionFactory().openSession();
						}
						
					}
					 session.getTransaction().begin();
					ContactEntity tenancyAdmin = null;
					//DF20150318 - Rajani Nagaraju - Get the ContactEntity from Mobile Number and not EmailId as EmailID can be null for auto user creation
					Query query1 = session.createQuery("from ContactEntity where primary_mobile_number='"+reqObj.getTenancyAdminPhoneNumber()+"'");
					Iterator itr1 = query1.list().iterator();
					while(itr1.hasNext())
					{
						tenancyAdmin = (ContactEntity)itr1.next();
					}

					if(tenancyAdmin==null)
						return "FAILURE";
					
					//Assign tenancy Admin to the corresponding account
					tenancyBoObj.setAccountContact(reqObj.getAccountId(), tenancyAdmin.getContact_id());

					
					//STEP4: For all machines under the tenancy make tenancy admin as default primary contact
					assetDetails = new AssetDetailsBO();
					assetEntityList = assetDetails.getAccountAssets(reqObj.getAccountId());
					EventDetailsBO eventDetailsBO = new EventDetailsBO();

					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
						if(session.getTransaction().isActive() && session.isDirty())
						{
							iLogger.info("Opening a new session");
							session = HibernateUtil.getSessionFactory().openSession();
						}
						      session.getTransaction().begin();
					}

					List<ContactEntity> primaryContactId = new LinkedList<ContactEntity>();
					primaryContactId.add(tenancyAdmin);

					//				Keerthi : Defect ID : 1177 :Receiver 1,2,3 : set method takes list of string
					//DefectId:950,1015 - Modified by Rajani Nagaraju - Receiver1,2 swapping issue
					List<String> primaryContactIdList = new ArrayList<String>();

					if(actualTenancyTypeName.equalsIgnoreCase(customerTenancyType))
					{
						primaryContactIdList.add(tenancyAdmin.getContact_id()+",2");
					}
					else
					{
						primaryContactIdList.add(tenancyAdmin.getContact_id()+",1");
					}

					//If there are any assets tagged to the account
					if( ! (assetEntityList==null || assetEntityList.isEmpty()) )
					{
						for(int j=0; j<assetEntityList.size(); j++)
						{
							if(! (session.isOpen() ))
							{
								session = HibernateUtil.getSessionFactory().getCurrentSession();
								//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
								if(session.getTransaction().isActive() && session.isDirty())
								{
									iLogger.info("Opening a new session");
									session = HibernateUtil.getSessionFactory().openSession();
								}
								 session.getTransaction().begin();
							}

							//DefectId:950,1015 - Modified by Rajani Nagaraju - Receiver1,2 swapping issue
							List<String> newContactEntity = new LinkedList<String>();
							newContactEntity.addAll(primaryContactIdList);
							if(actualTenancyTypeName.equalsIgnoreCase(customerTenancyType))
							{
								Query q = session.createQuery(" from EventSubscriptionMapping where serialNumber='"+assetEntityList.get(j).getSerial_number().getSerialNumber()+"'" +
								" and priority=1");
								Iterator itrt = q.list().iterator();
								while(itrt.hasNext())
								{
									EventSubscriptionMapping eventSubs = (EventSubscriptionMapping)itrt.next();
									newContactEntity.add((eventSubs.getContactId().getContact_id())+",1");
								}
							}
							//DefectId:950,1015 - Modified by Rajani Nagaraju - Receiver1,2 swapping issue
							eventDetailsBO.setEventSubscription(assetEntityList.get(j),newContactEntity);


						}
					}

				}

			
		}
			catch(CustomFault e)
			{
				//status = "FAILURE";
				bLogger.error("Custom Fault: "+ e.getFaultInfo());
				return status;
			}
			catch(Exception e)
			{
				//e.printStackTrace();
				//	status = "FAILURE";
				fLogger.fatal("Exception :"+e);
				return status;
			}

			finally
			{
				try
				{
					if(session.isOpen()){
						if(session.getTransaction().isActive())
						{
							session.flush();
							session.getTransaction().commit();
						} 
					}
				}
				catch(Exception e)
				{
					//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
					status = "FAILURE-"+e.getMessage();
					//Logger fLogger = FatalLoggerClass.logger;
					fLogger.fatal("Exception in commiting the record:"+e);
				}

				if(session.isOpen())
				{

					session.close();
				}

			}



			return status;
		}



		//******************************************************** Get Tenancy Details *******************************************************
		/** This method returns the List of tenancies with their details
		 * @param reqObj parentTenancyList and the childTenancyList specified through this reqObj
		 * @return Returns the list of tenancies with their details
		 * @throws CustomFault
		 */
		public List<TenancyDetailsRespContract> getTenancyDetails (TenancyDetailsReqContract reqObj, int pageNumber)
		{
			List<TenancyDetailsRespContract> responseList = new LinkedList<TenancyDetailsRespContract>();
			
			

			//	Logger businessError = Logger.getLogger("businessErrorLogger");
			Logger bLogger = BusinessErrorLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			//int pageNumber = reqObj.getPageNumber();
			try
			{
				if( ( ! (reqObj.getParentTenancyIdList()==null || reqObj.getParentTenancyIdList().isEmpty()) ) && 
						( ! (reqObj.getChildTenancyIdList()==null || reqObj.getChildTenancyIdList().isEmpty()) ) )
				{
					throw new CustomFault("Invalid tenancy input");
				}

				if( (reqObj.getParentTenancyIdList()==null || reqObj.getParentTenancyIdList().isEmpty()) && 
						(reqObj.getChildTenancyIdList()==null || reqObj.getChildTenancyIdList().isEmpty()) )
				{
					throw new CustomFault("Invalid tenancy input");
				}

				if(reqObj.getLoginId()==null)
				{
					throw new CustomFault("LoginId is not specified");
				}
			}

			catch(CustomFault e)
			{
				bLogger.error("Custom Fault: "+ e.getFaultInfo());
			}

			//	Logger fatalError = Logger.getLogger("fatalErrorLogger");

			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			//DF20190509: Anudeep Immidisetty adding page number to acheive pagination for faster response
			//check whether the page number is specified
			if(pageNumber==0)
			{
				pageNumber=1;
			}
			try
			{
				TenancyBO tenancyBO = new TenancyBO();
				//DF20190509: Anudeep Immidisetty adding page number to acheive pagination for faster response
				List<TenancyBO> tenancyBoList = tenancyBO.getTenancyDetails(reqObj.getLoginId(), reqObj.getParentTenancyIdList(), reqObj.getChildTenancyIdList(),pageNumber);

				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				iLogger.info("Output list size received in IMPL " + tenancyBoList.size());
				for(int i=0; i<tenancyBoList.size(); i++)
				{
					TenancyDetailsRespContract response = new TenancyDetailsRespContract();

					//set Tenancy details to response Object
					response.setCreatedBy(tenancyBoList.get(i).getCreatedBy());
					response.setCreatedDate(tenancyBoList.get(i).getCreatedDate());
					response.setOperatingEndTime(tenancyBoList.get(i).getOperatingEndTime());
					response.setOperatingStartTime(tenancyBoList.get(i).getOperatingStartTime());
					response.setParentTenancyId(tenancyBoList.get(i).getParentTenancyId());
					response.setParentTenancyName(tenancyBoList.get(i).getParentTenancyName());
					response.setTenancyId(tenancyBoList.get(i).getTenancyId());
					response.setTenancyName(tenancyBoList.get(i).getTenancyName());
					//DF20190516: Adding size attribute to implement pagination
					response.setSize(tenancyBoList.get(i).getSize());

					//DefectId: 839 - Rajani Nagaraju - Handle Machine Movement between tenancies.
					response.setTenancyCode(tenancyBoList.get(i).getTenancyCode());
					
					//commented below part @Roopa Now taking the account name from main query itself instead of iterating for each record

					//DefectId: To be provided by testing team
					//Rajani Nagaraju - 20130704 - AccountName to be returned from TenancyDetailsService 
					//get the accountId
					/*String accountName=null;
					Query accountQuery = session.createQuery(" from AccountTenancyMapping where tenancy_id="+tenancyBoList.get(i).getTenancyId());
					Iterator accountItr = accountQuery.list().iterator();
					while(accountItr.hasNext())
					{
						AccountTenancyMapping accountTenancy = (AccountTenancyMapping)accountItr.next();
						accountName = accountTenancy.getAccount_id().getAccount_name();
					}
					if(accountName!=null)
					{
						response.setAccountName(accountName);
					}*/
					
					response.setAccountName(tenancyBoList.get(i).getAccountName());


					//get the TenancyAdmin List
					//List<String> tenancyAdminList= new LinkedList<String>();

					//DefectId: 839 - Rajani Nagaraju - Handle Machine Movement between tenancies.
					
					/*
					List<TenancyEntity> tenancyIdList = new LinkedList<TenancyEntity>();
					 Query tenListQuery = session.createQuery(" from TenancyEntity where tenancyCode = (select a.tenancyCode from TenancyEntity a" +
							" where a.tenancy_id='"+tenancyBoList.get(i).getTenancyId()+"')");
					Iterator tenListItr = tenListQuery.list().iterator();
					while(tenListItr.hasNext())
					{
						TenancyEntity tenancy = (TenancyEntity)tenListItr.next();
						tenancyIdList.add(tenancy);
					}*/

					/*Query query = session.createQuery("select a.contact_id from ContactEntity a, AccountContactMapping b, AccountTenancyMapping c " +
							" where c.account_id = b.account_id and b.contact_id = a.contact_id and c.tenancy_id in (:list)" +
					" and a.is_tenancy_admin=1  and a.active_status =1 ").setParameterList("list", tenancyIdList);*/
					
					/*Query query = session.createQuery("select a.contact_id from ContactEntity a, AccountContactMapping b, AccountTenancyMapping c " +
							" where c.account_id = b.account_id and b.contact_id = a.contact_id and c.tenancy_id in ("+tenancyBoList.get(i).getTenancyId()+")" +
					" and a.is_tenancy_admin=1  and a.active_status =1 ");
					
					Iterator itr = query.list().iterator();
					while(itr.hasNext())
					{
						String contactId = (String) itr.next();
						tenancyAdminList.add(contactId);
					}
					response.setTenancyAdminList(tenancyAdminList);*/
					
					response.setTenancyAdminList(tenancyBoList.get(i).getTenancyAdminList());
					
					
					
//Commenting below part @Roopa since TenancyDelegationEntity is not getting used

					//get the delegated parent Tenancy Users List
					//DefectId: 839 - Rajani Nagaraju - Handle Machine Movement between tenancies.
					/*HashMap<String,String> parentTenancyUserIdMailIdList= new HashMap<String,String>();
					Query query1=null;
					
					//query1 = session.createQuery("select a.contactId from TenancyDelegationEntity a where a.tenancyId in (:list)").setParameterList("list", tenancyIdList);
					
					query1 = session.createQuery("select a.contactId from TenancyDelegationEntity a where a.tenancyId in ("+tenancyBoList.get(i).getTenancyId()+")");
					
					

					Iterator itr1 = query1.list().iterator();
					while(itr1.hasNext())
					{
						ContactEntity parentTenancyUsers = (ContactEntity) itr1.next();
						parentTenancyUserIdMailIdList.put(parentTenancyUsers.getContact_id(), parentTenancyUsers.getPrimary_email_id());
					}
					response.setParentTenancyUserIdMailIdList(parentTenancyUserIdMailIdList);*/


					responseList.add(response);
				}
			}

			catch(Exception e)
			{
				fLogger.fatal("Exception :"+e);
			}

			finally
			{
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

			return responseList;
		}


	}
