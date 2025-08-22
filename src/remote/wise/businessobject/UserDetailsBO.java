/*
 * JCB6239, JCB6240 : 20220829 : Dhiraj K : AssetSaleFromD2C session issue
 *  * CR500 : 20241128 : Dhiraj Kumar : WHatsApp Integration with LL
 */
package remote.wise.businessobject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
////import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import remote.wise.businessentity.AccountContactMapping;
import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetCustomGroupMapping;
import remote.wise.businessentity.AssetTypeEntity;
import remote.wise.businessentity.CatalogValuesEntity;
import remote.wise.businessentity.ClientEntity;
import remote.wise.businessentity.ContactActivityLogEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.CountryCodesEntity;
import remote.wise.businessentity.CustomAssetGroupEntity;
import remote.wise.businessentity.CustomAssetGroupSnapshotEntity;
import remote.wise.businessentity.EventEntity;
import remote.wise.businessentity.EventTypeEntity;
import remote.wise.businessentity.GroupUserMapping;
import remote.wise.businessentity.LanguageTrackingEntity;
import remote.wise.businessentity.LanguagesEntity;
import remote.wise.businessentity.PreferenceCatalogEntity;
import remote.wise.businessentity.PreferenceEntity;
import remote.wise.businessentity.RoleEntity;
import remote.wise.businessentity.TenancyEntity;
import remote.wise.exception.CustomFault;
import remote.wise.handler.ContactDetailsProducerThread;
import remote.wise.handler.EmailHandler;
import remote.wise.handler.EmailTemplate;
import remote.wise.handler.SmsHandler;
import remote.wise.handler.SmsTemplate;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.MachineGroupDetails;
import remote.wise.service.datacontract.ModelCodeResponseContract;
import remote.wise.service.datacontract.UserAlertPreferenceRespContract;
import remote.wise.service.implementation.AuditLogDetailsImpl;
import remote.wise.service.implementation.CityImpl;
import remote.wise.service.implementation.CustomersCodeUnderDealerCodeImpl;
import remote.wise.service.implementation.CustomersUnderDealerImpl;
import remote.wise.service.implementation.DealersUnderZoneImpl;
import remote.wise.service.implementation.DomainServiceImpl;
import remote.wise.service.implementation.EventNameImpl;
import remote.wise.service.implementation.EventTypeImpl;
import remote.wise.service.implementation.MGLandMarkDetailsImpl;
import remote.wise.service.implementation.StateImpl;
import remote.wise.service.implementation.UserAlertPreferenceImpl;
import remote.wise.service.implementation.UserPreferenceImpl;
import remote.wise.service.implementation.ZonalAccountCodeImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;

public class UserDetailsBO extends BaseBusinessObject {

	// DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static
	// logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger(
			"UserDetailsBO:", "businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger(
			"UserDetailsBO:", "fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger(
			"UserDetailsBO:", "info");*/

	int role_id;
	String error_msg;
	String role_name;
	String last_login_date;
	int account_id;
	ContactEntity contact;
	int tenancyId;
	boolean isSMS;
	boolean isMap;
	private String loginId;
	private String first_name;
	private String last_name;
	private int Is_tenancy_admin;
	public String countryCode;
	public String primaryMobileNumber;
	public String language;
	public String timeZone;
	private String primaryEmailId;
	private int sysGeneratedPassword;
	private int tenancyAdminCount;

	/**
	 * @return the sysGeneratedPassword
	 */
	public int getSysGeneratedPassword() {
		return sysGeneratedPassword;
	}

	/**
	 * @param sysGeneratedPassword
	 *            the sysGeneratedPassword to set
	 */
	public void setSysGeneratedPassword(int sysGeneratedPassword) {
		this.sysGeneratedPassword = sysGeneratedPassword;
	}

	public String getPrimaryEmailId() {
		return primaryEmailId;
	}

	public void setPrimaryEmailId(String primaryEmailId) {
		this.primaryEmailId = primaryEmailId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPrimaryMobileNumber() {
		return primaryMobileNumber;
	}

	public void setPrimaryMobileNumber(String primaryMobileNumber) {
		this.primaryMobileNumber = primaryMobileNumber;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public int getIs_tenancy_admin() {
		return Is_tenancy_admin;
	}

	public void setIs_tenancy_admin(int is_tenancy_admin) {
		Is_tenancy_admin = is_tenancy_admin;
	}

	private List<Integer> asset_group_id;
	private List<String> asset_group_name;

	public List<Integer> getAsset_group_id() {
		return asset_group_id;
	}

	public void setAsset_group_id(List<Integer> asset_group_id) {
		this.asset_group_id = asset_group_id;
	}

	public List<String> getAsset_group_name() {
		return asset_group_name;
	}

	public void setAsset_group_name(List<String> asset_group_name) {
		this.asset_group_name = asset_group_name;
	}

	public int getTenancyId() {
		return tenancyId;
	}

	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}

	public UserDetailsBO() {
		error_msg = null;
	}

	public int getAccount_id() {
		return account_id;
	}

	public void setAccount_id(int account_id) {
		this.account_id = account_id;
	}

	public int getRole_id() {
		return role_id;
	}

	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}

	public String getError_msg() {
		return error_msg;
	}

	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	public String getLast_login_date() {
		return last_login_date;
	}

	public void setLast_login_date(String last_login_date) {
		this.last_login_date = last_login_date;
	}

	public ContactEntity getContact() {
		return contact;
	}

	public void setContact(ContactEntity contact) {
		this.contact = contact;
	}

	public boolean isSMS() {
		return isSMS;
	}

	public void setSMS(boolean isSMS) {
		this.isSMS = isSMS;
	}

	public boolean isMap() {
		return isMap;
	}

	public void setMap(boolean isMap) {
		this.isMap = isMap;
	}

	// ******************************************** get RoleEntity for a given
	// RoleId******************************************************
	public RoleEntity getRoleEntity(int role_id) {
		RoleEntity roleEntityobj = new RoleEntity(role_id);
		return roleEntityobj;
	}

	// ******************************************** get contactEntity for a
	// given LoginId******************************************************
	public ContactEntity getContactEntity(String login_id) {
		ContactEntity contactEntityobj = new ContactEntity(login_id);
		return contactEntityobj;
	}

	// *************************************** get List of Contact Entity for
	// given list of contact Id ******************************************
	public List<ContactEntity> getContactEntityList(List<String> contactIdList) {
		if (contactIdList == null || contactIdList.isEmpty())
			return null;

		List<ContactEntity> contactEntityList = new LinkedList<ContactEntity>();
		// Logger businessError = Logger.getLogger("businessErrorLogger");
		// Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Logger iLogger = InfoLoggerClass.logger;
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();

		try {

			Query query = session
					.createQuery(
							"from ContactEntity where active_status=true and contact_id in (:list)")
							.setParameterList("list", contactIdList);
			Iterator itr = query.list().iterator();
			while (itr.hasNext()) {
				ContactEntity contactEntity = (ContactEntity) itr.next();
				contactEntityList.add(contactEntity);
			}
		}

		catch (Exception e) {
			fLogger.fatal("Exception :" + e.getMessage());
		}

		finally {

			try
			{
				if (session.getTransaction().isActive()) {
					session.getTransaction().commit();
				}
			}

			catch(Exception e)
			{
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}
		return contactEntityList;
	}

	// ToDo: To be done by Juhi
	// ******************************************************* set User Details
	// ******************************************************************
	/*
	 * public ContactEntity setUserDetails(String contactId,String firstName,
	 * String lastName, String emailId, String phoneNumber,int isTenancyAdmin,
	 * int roleId, String CountryCode) {
	 * 
	 * Properties prop = new Properties(); String clientName=null;
	 * 
	 * try { prop.load(getClass().getClassLoader().getResourceAsStream(
	 * "remote/wise/resource/properties/configuration.properties"));
	 * //prop.load(new FileInputStream(
	 * "E:\\JCB_Workspace_19012013\\WISE\\src\\remote\\wise\\resource\\properties\\configuration.properties"
	 * )); clientName= prop.getProperty("ClientName");
	 * 
	 * } catch(Exception e) { infoLogger.info(e); }
	 * 
	 * IndustryBO industryBoObj = new IndustryBO(); ClientEntity clientEntity =
	 * industryBoObj.getClientEntity(clientName);
	 * 
	 * //If contactId is specified update only the fields which is not null
	 * ContactEntity contactEntity=null; HibernateSessionConfig h= new
	 * HibernateSessionConfig(); Session session = h.createSession();
	 * Transaction txn = session.beginTransaction();
	 * 
	 * RoleEntity role = null; Query query =
	 * session.createQuery("from RoleEntity where role_id="+roleId); Iterator
	 * itr = query.list().iterator(); while(itr.hasNext()) { role = (RoleEntity)
	 * itr.next(); }
	 * 
	 * ContactEntity contact = new ContactEntity();
	 * contact.setContact_id(contactId); contact.setFirst_name(firstName);
	 * contact.setLast_name(lastName); contact.setPrimary_email_id(emailId);
	 * contact.setPrimary_mobile_number(phoneNumber);
	 * contact.setIs_tenancy_admin(isTenancyAdmin); contact.setRole(role);
	 * contact.setActive_status(true); contact.setClient_id(clientEntity);
	 * contact.setCountryCode(CountryCode); contact.save();
	 * 
	 * contactEntity = getContactEntity(contactId); return contactEntity; }
	 */

	// Defect ID 1442: Settings-Audit Log - Number of records displayed are
	// varying for particular date when there is a change in start date
	// selection.- Start
	// IST TO GMT Conversion

	private static String stringISTtoGMTConversion(String date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

		sdf.setTimeZone(TimeZone.getTimeZone("IST"));

		Date newDate;
		try {
			newDate = sdf.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
			return date;
		}

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));

		return sdf1.format(newDate);
		// // Defect ID 1442: Settings-Audit Log - Number of records displayed
		// are varying for particular date when there is a change in start date
		// selection.- End
	}

	// *******************************Get Audit Log Details for given Tenancy
	// **************************************
	/**
	 * This method will return List of Audit Log Details for given Tenancy
	 * 
	 * @param TenancyId
	 *            : Tenancy Id
	 * @param FromDate
	 *            : FromDate should be less than ToDate in TimeStamp format
	 * @param ToDate
	 *            : ToDate in TimeStamp format
	 * @return auditLogImpl :List of Audit Log Details
	 * @throws CustomFault
	 */

	public List<AuditLogDetailsImpl> getAuditLogDetails(String TenancyId,
			String FromDate, String ToDate) throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;

		List<ContactEntity> contactList = new LinkedList<ContactEntity>();
		List<AuditLogDetailsImpl> auditLogImpl = new LinkedList<AuditLogDetailsImpl>();
	//	AccountEntity account = null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try {
			// Defect ID 1442: Settings-Audit Log - Number of records displayed
			// are varying for particular date when there is a change in start
			// date selection.
			FromDate = stringISTtoGMTConversion(FromDate + " 00:00:00.0");
			iLogger.info("FromDate : " + FromDate);

			ToDate = stringISTtoGMTConversion(ToDate + " 23:59:59.0");
			iLogger.info("ToDate :" + ToDate);
			// Defect ID 1442: Settings-Audit Log - Number of records displayed
			// are varying for particular date when there is a change in start
			// date selection.- End
			
			
		/*	Query queryObj = session
					.createQuery("from AccountTenancyMapping where tenancy_id='"
							+ TenancyId + "'");
			Iterator it = queryObj.list().iterator();
			while (it.hasNext()) {
				AccountTenancyMapping accountTenancy = (AccountTenancyMapping) it
						.next();
				account = accountTenancy.getAccount_id();
			}
			iLogger.info("Account:" + account.getAccount_id());

			Query accountQuery = session
					.createQuery("from AccountContactMapping where account_id = "
							+ account.getAccount_id());*/
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			
			
               List<Integer> tenancyIdList=new ArrayList<Integer>();
			
			tenancyIdList.add(Integer.parseInt(TenancyId));
			String accountIdListAsString=new DateUtil().getAccountListForTheTenancy(tenancyIdList);
			//DF20190430 I Anudeep Adding loggers to check how inputs are coming
			iLogger.info("accountIdListAsString " + accountIdListAsString);
			System.out.println("accountIdListAsString " + accountIdListAsString);
			
			Query accountQuery = session
					.createQuery("from AccountContactMapping where account_id in ("+accountIdListAsString+")");
			
			Iterator itr = accountQuery.list().iterator();
			iLogger.info("accountQuery.list() size is :"
					+ accountQuery.list().size());
			while (itr.hasNext()) {
				AccountContactMapping accountMapping = (AccountContactMapping) itr
						.next();
				contactList.add(accountMapping.getContact_id());
			}

			iLogger.info("Todate is " + ToDate);
			// Defect ID: 634 : Keerthi : Audit Log user list : Taking all
			// entries instead of max of login date for user
			String queryString = "SELECT e.login_date, e.contact_id FROM ContactActivityLogEntity e where e.login_date between '"
					+ FromDate
					+ "' and '"
					+ ToDate

					+ "' and e.contact_id in (:list)  order by e.login_date desc";

			iLogger.info("Query executed :");

			iLogger.info("contactList details " + contactList.size());
			//			Keerthi : 17/02/14 : ID 2054 : checking for contact list size 
			if(contactList.size()>0){
				Query contactQuery = session.createQuery(queryString)
						.setParameterList("list", contactList);
				//DF20190430 I Anudeep Logging Query
				iLogger.info("ContactQuery is : " + contactQuery.getQueryString());

				Iterator contactItr = contactQuery.list().iterator();
				while (contactItr.hasNext())

				{
					AuditLogDetailsImpl implObj = new AuditLogDetailsImpl();

					Object[] result = (Object[]) contactItr.next();
					ContactEntity ContactName = (ContactEntity) result[1];
					ContactEntity entity = (ContactEntity) session.get(
							ContactEntity.class,
							(Serializable) ContactName.getContact_id());

					ContactEntity userId = (ContactEntity) result[1];
					implObj.setUserId(userId.getContact_id());
					if( entity.getLast_name()!=null){
						implObj.setUserName(entity.getFirst_name() + " "+ entity.getLast_name());
					}
					else{
						implObj.setUserName(entity.getFirst_name());
					}
					implObj.setLastLoginDate((String) result[0].toString());
					auditLogImpl.add(implObj);
				}
			}

		} finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}
		return auditLogImpl;
	}

	//************************************************** Get Group user List ***************************************************
	/** DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
	 * This method returns the users attached to a Asset Group or to a Tenancy if the Asset group is not selected
	 * @param loginId  User LoginId
	 * @param AssetGroupId Asset Group id
	 * @param TenancyId tenancy Id to be selected
	 * @param LoginTenancyId Tenancy Id of the Login User
	 * @return List of users belonging to a Asset Group.
	 * @throws CustomFault custom Exception is thrown .
	 */
	public List<UserDetailsBO> getAllUsers(String login_Id, int AssetGroupId,
			List<Integer> tenancyId, int LoginTenancyId) throws CustomFault {

		List<UserDetailsBO> userDetails = new LinkedList<UserDetailsBO>();
		// Logger businessError = Logger.getLogger("businessErrorLogger");
		// Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try {

			ContactEntity contactEntity = getContactEntity(login_Id);
			if (contactEntity.getContact_id() == null) {
				throw new CustomFault("Please pass a valid LogInId");
			}

			/*
			 * if (contactEntity.getIs_tenancy_admin() == 0) { throw new
			 * CustomFault( "The loginuser specified is not a tenancy admin"); }
			 */

			if (AssetGroupId != 0) {

				List<Integer> tenancyIdList = new LinkedList<Integer>();
				if (!(tenancyId == null || tenancyId.isEmpty())) {

					tenancyIdList.addAll(tenancyId);
				}

				tenancyIdList.add(LoginTenancyId);

				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
				//Get the List of all tenancyIds - Including Pseudo Tenancies
				List<Integer> newTenancyIdList = new LinkedList<Integer>();
				ListToStringConversion conversion = new ListToStringConversion();
				String tenancyIdListString = conversion.getIntegerListString(tenancyIdList).toString();
				
				//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
				//Query q = session.createQuery(" from TenancyEntity where tenancyCode in ( select a.tenancyCode from TenancyEntity a where a.tenancy_id in ("+tenancyIdListString+"))");
				
				Query q = session.createQuery(" from TenancyEntity where mappingCode in ( select a.mappingCode from TenancyEntity a where a.tenancy_id in ("+tenancyIdListString+"))");
				
				Iterator it = q.list().iterator();
				while(it.hasNext())
				{
					TenancyEntity tenancy = (TenancyEntity)it.next();
					newTenancyIdList.add(tenancy.getTenancy_id());
				}


				DomainServiceImpl domainService = new DomainServiceImpl();
				CustomAssetGroupEntity customEntity = domainService
						.getCustomAssetGroupDetails(AssetGroupId);
				if (customEntity.getGroup_id() == 0) {
					throw new CustomFault("Invalid Group ID");
				}

				if (!(newTenancyIdList == null || newTenancyIdList.isEmpty())) 
				{
					//if (!(tenancyIdList == null || tenancyIdList.isEmpty())) {
					int present = 0;

					for (int i = 0; i < newTenancyIdList.size(); i++)
					{
						if (customEntity.getTenancy_id().getTenancy_id() == newTenancyIdList.get(i)) 
						{

							present = 1;
						}
					}
					if (present == 0) 
					{
						throw new CustomFault("AssetGroup specified is not under the login user tenancy");
					}
				}

				AssetCustomGroupDetailsBO assetGroupObj = new AssetCustomGroupDetailsBO();
				List<ContactEntity> contactEntityList = assetGroupObj.getMachineGroupUsers(AssetGroupId);
				if (!(session.isOpen()))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}

				if (!(contactEntityList == null || contactEntityList.isEmpty())) 
				{

					List<String> contactEntityObj = new LinkedList<String>();
					for (int k = 0; k < contactEntityList.size(); k++) 
					{
						contactEntityObj.add(contactEntityList.get(k).getContact_id());
					}

					ListToStringConversion listObj = new ListToStringConversion();
					String ContactList = listObj.getStringList(contactEntityObj).toString();
					/*Query maxDateQuery = session
							.createQuery("select max(a.login_date), b from ContactActivityLogEntity a RIGHT OUTER JOIN a.contact_id b where b.contact_id in ("
									+ ContactList + ") group by a.contact_id");*/
					//20140113 - DefectID: 1809: Rajani Nagaraju - Only Active uers has to be listed
					//20140113: Defect Id : 1978 : To get the list of users from Contact Table instead of ContactActivityLog Table

					//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
					Query maxDateQuery = session.createQuery("select max(a.login_date), b from ContactActivityLogEntity a RIGHT OUTER JOIN a.contact_id b where b.contact_id in ("
							+ ContactList + ") and b.active_status=1 group by b.contact_id");

					if (maxDateQuery != null) 
					{
						Iterator itr = maxDateQuery.list().iterator();
						// UserDetailsBO userBO =null ;
						while (itr.hasNext()) 
						{
							UserDetailsBO userBO = new UserDetailsBO();
							Object[] result = (Object[]) itr.next();
							userBO.setContact((ContactEntity) result[1]);
							Timestamp ts = (Timestamp) result[0];

							// last_login_date = ts.toString();
							Timestamp loginDate = (Timestamp) result[0];
							if (loginDate != null) 
							{
								userBO.setLast_login_date(loginDate.toString());
							}
							userBO.setTenancyId(customEntity.getTenancy_id().getTenancy_id());
							userDetails.add(userBO);
						}

						/*
						 * userBO.setTenancyId(customEntity.getTenancy_id()
						 * .getTenancy_id()); userDetails.add(userBO);
						 */
					}

				}
				return userDetails;
			}


			else 
			{
				List<Integer> totalTenancyId = new LinkedList<Integer>();
				if ((tenancyId == null || tenancyId.isEmpty())) 
				{
					totalTenancyId.add(LoginTenancyId);
				} 

				else 
				{
					totalTenancyId = tenancyId;
				}


				for (int j = 0; j < totalTenancyId.size(); j++) 
				{
					TenancyBO tenancyDetails = new TenancyBO();

					List<ContactEntity> contactEntityList = new LinkedList<ContactEntity>();
					if (contactEntity.getIs_tenancy_admin() == 0) 
					{
						contactEntityList.add(contactEntity);

					} 

					else 
					{

						contactEntityList = tenancyDetails.getTenancyUsers(totalTenancyId.get(j));

					}

					if (!(session.isOpen()))
					{

						session = HibernateUtil.getSessionFactory().openSession();
						session.getTransaction().begin();
					}

					List<String> contactEntityObj1 = new LinkedList<String>();
					for (int k = 0; k < contactEntityList.size(); k++) 
					{
						contactEntityObj1.add(contactEntityList.get(k)
								.getContact_id());
					}

					ListToStringConversion listObj = new ListToStringConversion();
					String ContactList = listObj.getStringList(contactEntityObj1).toString();
					Query maxDateQuery = session
							.createQuery(
									/*
									 * "select max(a.login_date), a.contact_id from ContactActivityLogEntity a where a.contact_id in (:list) group by a.contact_id"
									 * ) .setParameterList("list", contactEntityList);
									 */
									/*
									 * "select max(a.login_date), b from ContactActivityLogEntity a RIGHT OUTER JOIN a.contact_id b where b.contact_id in (:list) group by a.contact_id"
									 * ) .setParameterList("list", contactEntityList);
									 */
									// DF 1978 : Get the list of users from Contact tbale instead of Contact Activity Log Table.
									//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
									/*"select max(a.login_date), b from ContactActivityLogEntity a RIGHT OUTER JOIN a.contact_id b where b.contact_id in ("
									+ ContactList
									+ ") and b.active_status=1 group by a.contact_id");*/
									"select max(a.login_date), b from ContactActivityLogEntity a RIGHT OUTER JOIN a.contact_id b where b.contact_id in ("
									+ ContactList + ") and b.active_status=1 group by b.contact_id");
					if (maxDateQuery != null) 
					{
						Iterator itr = maxDateQuery.list().iterator();

						Object result[] = null;
						while (itr.hasNext()) 
						{
							UserDetailsBO userBO = new UserDetailsBO();
							result = (Object[]) itr.next();

							Timestamp loginDate = (Timestamp) result[0];
							if (loginDate != null) 
							{
								last_login_date = loginDate.toString();
								userBO.setLast_login_date(last_login_date);
							}
							userBO.setTenancyId(totalTenancyId.get(j));
							userBO.setContact((ContactEntity) result[1]);

							userDetails.add(userBO);
						}
					}

				}

			}
		} /*
		 * catch (Exception e) { e.printStackTrace();
		 * fatalError.fatal("Exception :" + e); // e.printStackTrace(); }
		 */
		catch (Exception e) 
		{ 
			fLogger.fatal("Exception :" + e); // e.printStackTrace(); 
		}

		finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}
		return userDetails;

	}

	// ************* Start of Set Group User List *********************

	//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
	public String setAssetGroupUsers(String login_id, int groupId,
			List<String> contactId, int tenancyId) throws CustomFault {
		String status = "SUCCESS";

		// Logger businessError = Logger.getLogger("businessErrorLogger");
		// Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try {
			if (login_id == null)
				throw new CustomFault("Please pass a LogInId");

			ContactEntity contactEntity = getContactEntity(login_id);
			if (contactEntity.getContact_id() == null)
				throw new CustomFault("Please pass a valid LogInId");
			if (groupId == 0)
				throw new CustomFault("Please pass a Group Id");

			DomainServiceImpl domainService = new DomainServiceImpl();
			CustomAssetGroupEntity customEntity = domainService
					.getCustomAssetGroupDetails(groupId);
			if (customEntity.getGroup_id() == 0) {
				throw new CustomFault("Invalid Group ID");
			}

			List<Integer> totalTenancyId = new LinkedList<Integer>();
			totalTenancyId.add(tenancyId);


			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
			//Get the List of all tenancyIds - Including Pseudo Tenancies
			List<Integer> newTenancyIdList = new LinkedList<Integer>();
			ListToStringConversion conversion = new ListToStringConversion();
			String tenancyIdListString = conversion.getIntegerListString(totalTenancyId).toString();
			//Query q = session.createQuery(" from TenancyEntity where tenancyCode in ( select a.tenancyCode from TenancyEntity a where a.tenancy_id in ("+tenancyIdListString+"))");
			
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			Query q = session.createQuery(" from TenancyEntity where mappingCode in ( select a.mappingCode from TenancyEntity a where a.tenancy_id in ("+tenancyIdListString+"))");
			
			Iterator it = q.list().iterator();
			while(it.hasNext())
			{
				TenancyEntity tenancy = (TenancyEntity)it.next();
				newTenancyIdList.add(tenancy.getTenancy_id());
			}

			int flag = 1;
			for (int i = 0; i < newTenancyIdList.size(); i++) 
			{
				if (newTenancyIdList.get(i) == customEntity.getTenancy_id().getTenancy_id())
					flag = 0;
			}
			if (flag == 1)
				throw new CustomFault(
						"AssetGroup specified is not under the specified tenancy");

			if (!(session.isOpen())) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			//	ListToStringConversion conversion = new ListToStringConversion();
			String contactIdAsString = conversion.getStringList(contactId)
					.toString();

			Query query = session
					.createQuery("from GroupUserMapping where group_id="
							+ groupId);
			Iterator itr = query.list().iterator();

			List<String> existingContacts = new LinkedList<String>();
			while (itr.hasNext()) {
				GroupUserMapping groupUser = (GroupUserMapping) itr.next();
				existingContacts.add(groupUser.getContact_id().getContact_id());
			}

			// New contacts to be added to the Asset Group
			List<String> newContacts = new LinkedList<String>();
			if (!(contactId == null || contactId.isEmpty()))
				newContacts.addAll(contactId);
			newContacts.removeAll(existingContacts);

			String newContactIdAsString = conversion.getStringList(newContacts)
					.toString();
			HashMap<String, ContactEntity> newContactIdMap = new HashMap<String, ContactEntity>();
			if (newContacts.size() > 0) {
				Query query2 = session
						.createQuery(" from ContactEntity where active_status=true and contact_id in ("
								+ newContactIdAsString + ")");
				Iterator itr2 = query2.list().iterator();
				while (itr2.hasNext()) {
					ContactEntity contact = (ContactEntity) itr2.next();
					newContactIdMap.put(contact.getContact_id(), contact);
				}

			}

			// Existing contacts of the Asset Group to be removed/replaced
			if (!(contactId == null || contactId.isEmpty()))
				existingContacts.removeAll(contactId);

			if (newContacts.size() > existingContacts.size()) {
				for (int i = 0, j = 0; (i < newContacts.size() || j < existingContacts
						.size());) {
					if ((i == j)
							&& (i < newContacts.size() && j < existingContacts
									.size())) {
						Query query3 = session
								.createQuery("update GroupUserMapping set contact_id='"
										+ newContacts.get(j)
										+ "'  where "
										+ " group_id ="
										+ groupId
										+ " and contact_id='"
										+ existingContacts.get(j) + "'");
						query3.executeUpdate();
						i++;
						j++;
					} else {
						GroupUserMapping newGroupUser = new GroupUserMapping();
						ContactEntity contactEnt = newContactIdMap
								.get(newContacts.get(i));
						newGroupUser.setGroup_id(customEntity);
						newGroupUser.setContact_id(contactEnt);
						session.save(newGroupUser);
						i++;
					}
				}
			}

			else if (existingContacts.size() > newContacts.size()) {
				for (int i = 0, j = 0; (i < newContacts.size() || j < existingContacts
						.size());) {
					if ((i == j)
							&& (i < newContacts.size() && j < existingContacts
									.size())) {
						Query query3 = session
								.createQuery("update GroupUserMapping set contact_id='"
										+ newContacts.get(j)
										+ "'  where "
										+ " group_id ="
										+ groupId
										+ " and contact_id='"
										+ existingContacts.get(j) + "'");
						query3.executeUpdate();
						i++;
						j++;
					} else {
						Query query4 = session
								.createQuery(" delete from GroupUserMapping where group_id ="
										+ groupId
										+ " and "
										+ " contact_id='"
										+ existingContacts.get(j) + "'");
						query4.executeUpdate();
						j++;
					}
				}
			}

			else if (existingContacts.size() == newContacts.size()) {
				for (int i = 0; i < newContacts.size(); i++) {
					Query query3 = session
							.createQuery("update GroupUserMapping set contact_id='"
									+ newContacts.get(i)
									+ "'  where "
									+ " group_id ="
									+ groupId
									+ " and contact_id='"
									+ existingContacts.get(i) + "'");
					query3.executeUpdate();
				}
			}
			//DF20190523:Abhishek::TO insert the record in custom_asset_group_sanpshot table.
			if(groupId!=0&&contactId.isEmpty()==false){
				System.out.println("Inserting records in custom asset group snapshot table");
				ConnectMySQL connMySql = new ConnectMySQL();
				Connection conn = null;
				Statement stmt = null;
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				Query CAGSQuery = session
						.createQuery("from AssetCustomGroupMapping where Group_ID="+groupId);
				Iterator CAGSItr = CAGSQuery.list().iterator();
				try
				{
					conn = connMySql.getConnection();
					stmt = conn.createStatement();
					stmt.executeUpdate("Delete from custom_asset_group_snapshot where Group_ID ="+groupId);
					while (CAGSItr.hasNext()) {
						AssetCustomGroupMapping cag = (AssetCustomGroupMapping) CAGSItr.next();
						String assetId=cag.getSerial_number().getSerial_number().getSerialNumber();
						for(String userId:contactId){
						
							stmt.executeUpdate("Insert into custom_asset_group_snapshot Values("+groupId+",'"+userId+"','"+assetId+"')");
						}
					
					}
				}
				catch(Exception e){
					e.printStackTrace();
	
					
				}
				finally
				{
					if(stmt!=null)
						try {
							stmt.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					if (conn != null) {
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				
				}		

			}

		}

		catch (CustomFault e) {
			e.printStackTrace();
			status = "FAILURE";
			bLogger.error("Custom Fault: " + e.getFaultInfo());
		}

		catch (Exception e) {
			e.printStackTrace();
			status = "FAILURE";
			fLogger.fatal("Exception :" + e);
		}

		finally {

			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}
		}

		return status;
	}

	// ************************************************** End of GetUserList
	// ***************************************************

	/**
	 * method to get User Alert Preference
	 * 
	 * @param loginId
	 * @return List<UserAlertPreferenceImpl>
	 * @throws CustomFault
	 */
	public List<UserAlertPreferenceImpl> getUserAlertPreference(String loginId,
			String roleName,int pageNumber,int eventTypeID) throws CustomFault {
		List<UserAlertPreferenceImpl> respList = new ArrayList<UserAlertPreferenceImpl>();
		// Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Properties prop = new Properties();
		Map<Integer, Integer> catlogMap = new TreeMap<Integer, Integer>();
		String customer = null;
		String JCBAdmin = null;
		String DealerAdmin=null;
		String DealerAccount=null;

		UserAlertPreferenceImpl impl = null;
		UserAlertPreferenceImpl impl1 = null;
		try {
			try {
				prop.load(getClass()
						.getClassLoader()
						.getResourceAsStream(
								"remote/wise/resource/properties/configuration.properties"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				fLogger.fatal("Exception:: " + e.getMessage());

			}
			customer = prop.getProperty("CustomerCare");
			JCBAdmin = prop.getProperty("JCBAdmin");
			DealerAdmin = prop.getProperty("DealerAdmin");
			DealerAccount = prop.getProperty("DealerAccount");

			//DF20170130 @Roopa for Role based alert implementation
			DateUtil utilObj=new DateUtil();

			List<String> alertCodeList= utilObj.roleAlertMapDetailsNew(loginId,0, "Display",pageNumber,eventTypeID);

			ListToStringConversion conversion = new ListToStringConversion();

			StringBuilder alertCodeListAsString=conversion.getStringList(alertCodeList);


			if (roleName != null) {
				if (customer.equalsIgnoreCase(roleName)
						|| JCBAdmin.endsWith(roleName)|| DealerAdmin.equalsIgnoreCase(roleName) || DealerAccount.equalsIgnoreCase(roleName) ) {

					// get all event names along with the catalog value id
					Query query1 = session
							.createQuery("SELECT b1.eventId,b1.eventName,p1.catalogValueId,b1.eventTypeId,p4.eventTypeName"
									+ " FROM EventEntity b1,CatalogValuesEntity p1,EventTypeEntity p4"
									+ " WHERE b1.eventCode in ("+alertCodeListAsString+")"
									+ " AND b1.eventName=p1.catalogValue AND b1.eventTypeId = p4.eventTypeId"
									+ " order by b1.eventId ");
					// query to get all user preferences along with the catalog
					// id
					Query query2 = session
							.createQuery("SELECT c1.catalogId,c1.catalogValueId"
									+ " FROM PreferenceEntity p1, CatalogValuesEntity c1"
									+ " WHERE p1.contact='"
									+ loginId
									+ "'"
									+ " AND c1.catalogValueId=p1.catalogValueId");
					Iterator iterat = query2.list().iterator();
					Object[] result = null;
					int eventId = 0;
					String eventName = null;
					int catalogValueId = 0;
					int catalogId;
					int eventTypeId = 0;
					String eventTypeName = null;
					while (iterat.hasNext()) {
						result = (Object[]) iterat.next();
						PreferenceCatalogEntity pce = (PreferenceCatalogEntity) result[0];
						catalogId = pce.getCatalogId();
						if (result[1] != null) {
							catalogValueId = (Integer) result[1];
						}
						catlogMap.put(catalogValueId, catalogId);
					}
					// iterate over first query and add it to response list
					iterat = query1.list().iterator();
					result = null;
					boolean idExists;
					while (iterat.hasNext()) {
						result = (Object[]) iterat.next();
						impl = new UserAlertPreferenceImpl();
						if (result[0] != null) {
							eventId = (Integer) result[0];
						}
						if (result[1] != null) {
							eventName = (String) result[1];
						}
						if (result[2] != null) {
							catalogValueId = (Integer) result[2];
						}
						if (result[3] != null) {
							eventTypeId = ((EventTypeEntity) result[3])
									.getEventTypeId();
						}
						if (result[4] != null) {
							eventTypeName = (String) result[4];
						}
						impl.setEventId(eventId);
						impl.setEventName(eventName);
						impl.setEventTypeId(eventTypeId);
						impl.setEventTypeName(eventTypeName);
						impl.setSMSEvent(false);
						impl.setEmailEvent(false);
						impl.setWhatsAppEvent(false);//CR500.n
						if (catlogMap != null
								&& catlogMap.containsKey(catalogValueId)) {
							catalogId = catlogMap.get(catalogValueId);
							if (catalogId == 9) {
								impl.setSMSEvent(true);
							} else if (catalogId == 10) {
								impl.setEmailEvent(true);
							}
							//CR500.sn
							else if (catalogId == 13) {
								impl.setWhatsAppEvent(true);
							}//CR500.en
						}
						idExists = false;
						Iterator<UserAlertPreferenceImpl> listIter = respList
								.iterator();
						while (listIter.hasNext() && !idExists) {
							impl1 = listIter.next();
							if (impl1.getEventId() == eventId) {// event id
								// already
								// exists in
								// list. so set
								// only boolean
								// values
								idExists = true;
								if (catlogMap != null
										&& catlogMap
										.containsKey(catalogValueId)) {
									catalogId = catlogMap.get(catalogValueId);
									if (catalogId == 9) {
										impl1.setSMSEvent(true);
									} else if (catalogId == 10) {
										impl1.setEmailEvent(true);
									}
									//CR500.sn
									else if (catalogId == 13) {
										impl.setWhatsAppEvent(true);
									}//CR500.en
								}
							}
						}
						if (!idExists) {// event id does not exist. so add the
							// new object here
							respList.add(impl);
						}
					}
				} else {/*
				 * //logged in user is a customer Query query1 =
				 * session.
				 * createQuery("from PreferenceEntity where contact ='"
				 * +loginId +"'");
				 * 
				 * Iterator itr = query1.list().iterator();
				 * while(itr.hasNext()) { int update = 0; int index=0;
				 * PreferenceEntity preferenceEntity =
				 * (PreferenceEntity) itr.next(); String eventTypeName =
				 * preferenceEntity
				 * .getCatalogValueId().getCatalogValue();
				 * 
				 * for(int i=0; i< respList.size(); i++){
				 * if(respList.get
				 * (i).getEventTypeName().equals(eventTypeName)){ update
				 * =1; index=i; } }
				 * 
				 * if (update==0){ UserAlertPreferenceImpl userAlertImpl
				 * = new UserAlertPreferenceImpl();
				 * if(preferenceEntity.getCatalogValueId
				 * ().getCatalogId().getCatalogId()==1){
				 * userAlertImpl.setSMSEvent(true); }
				 * 
				 * else
				 * if(preferenceEntity.getCatalogValueId().getCatalogId
				 * ().getCatalogId()==2){
				 * userAlertImpl.setEmailEvent(true); }
				 * 
				 * userAlertImpl.setEventTypeName(preferenceEntity.
				 * getCatalogValueId().getCatalogValue());
				 * respList.add(userAlertImpl); } if (update==1){
				 * 
				 * if(preferenceEntity.getCatalogValueId().getCatalogId()
				 * .getCatalogId()==1){
				 * respList.get(index).setSMSEvent(true); } else
				 * if(preferenceEntity
				 * .getCatalogValueId().getCatalogId()
				 * .getCatalogId()==2){
				 * respList.get(index).setEmailEvent(true); }
				 * 
				 * } } for(int j=0;j <respList.size();j++){
				 * 
				 * String eventTypeName =
				 * respList.get(j).getEventTypeName();
				 * 
				 * Query query = session.createQuery(
				 * "from EventTypeEntity where eventTypeName ='"
				 * +eventTypeName +"'"); Iterator iterator =
				 * query.list().iterator(); while(iterator.hasNext()){
				 * EventTypeEntity eventTypeId =
				 * (EventTypeEntity)iterator.next();
				 * respList.get(j).setEventTypeId
				 * (eventTypeId.getEventTypeId()); }
				 * 
				 * }
				 */

					// get all event names along with the catalog value id
					Query query1 = session
							.createQuery("SELECT p1.catalogValueId,p4.eventTypeId,p4.eventTypeName FROM CatalogValuesEntity p1,"
									+ "EventTypeEntity p4 WHERE p4.eventTypeName=p1.catalogValue "
									+ " order by p4.eventTypeId asc ");
					// query to get all user preferences along with the catalog
					// id
					Query query2 = session
							.createQuery("SELECT c1.catalogId,c1.catalogValueId"
									+ " FROM PreferenceEntity p1, CatalogValuesEntity c1"
									+ " WHERE p1.contact='"
									+ loginId
									+ "'"
									+ " AND c1.catalogValueId=p1.catalogValueId");
					Iterator iterat = query2.list().iterator();
					Object[] result = null;
					int catalogValueId = 0;
					int catalogId;
					int eventTypeId = 0;
					String eventTypeName = null;
					while (iterat.hasNext()) {
						result = (Object[]) iterat.next();
						PreferenceCatalogEntity pce = (PreferenceCatalogEntity) result[0];
						catalogId = pce.getCatalogId();
						if (result[1] != null) {
							catalogValueId = (Integer) result[1];
						}
						catlogMap.put(catalogValueId, catalogId);
					}
					// iterate over first query and add it to response list
					iterat = query1.list().iterator();
					result = null;
					boolean idExists;
					while (iterat.hasNext()) {
						result = (Object[]) iterat.next();
						impl = new UserAlertPreferenceImpl();
						/*
						 * if(result[0]!=null){ eventId = (Integer)result[0]; }
						 * if(result[1]!=null){ eventName =(String)result[1]; }
						 */
						if (result[0] != null) {
							catalogValueId = (Integer) result[0];
						}
						if (result[1] != null) {
							eventTypeId = ((Integer) result[1]);
						}
						if (result[2] != null) {
							eventTypeName = (String) result[2];
						}
						/*
						 * impl.setEventId(eventId);
						 * impl.setEventName(eventName);
						 */
						impl.setEventTypeId(eventTypeId);
						impl.setEventTypeName(eventTypeName);
						impl.setSMSEvent(false);
						impl.setEmailEvent(false);
						if (catlogMap != null
								&& catlogMap.containsKey(catalogValueId)) {
							catalogId = catlogMap.get(catalogValueId);
							if (catalogId == 1) {
								impl.setSMSEvent(true);
							} else if (catalogId == 2) {
								impl.setEmailEvent(true);
							}
							//CR500.sn
							else if (catalogId == 14) {
								impl.setWhatsAppEvent(true);
							}//CR500.en
						}
						idExists = false;
						Iterator<UserAlertPreferenceImpl> listIter = respList
								.iterator();
						while (listIter.hasNext() && !idExists) {
							impl1 = listIter.next();
							if (impl1.getEventTypeId() == eventTypeId) {// event
								// id
								// already
								// exists
								// in
								// list.
								// so
								// set
								// only
								// boolean
								// values
								idExists = true;
								if (catlogMap != null
										&& catlogMap
										.containsKey(catalogValueId)) {
									catalogId = catlogMap.get(catalogValueId);
									if (catalogId == 1) {
										impl1.setSMSEvent(true);
									} else if (catalogId == 2) {
										impl1.setEmailEvent(true);
									}
									//CR500.sn
									else if (catalogId == 14) {
										impl1.setWhatsAppEvent(true);
									}//CR500.en
								}
							}
						}
						if (!idExists) {// event id does not exist. so add the
							// new object here
							respList.add(impl);
						}
					}

				}
			}
		}
		/*
		 * catch(Exception e){
		 * businessError.error("Error while reading properties file"); throw new
		 * CustomFault("Error while reading properties file"); }
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
		long endTime = System.currentTimeMillis();
		iLogger.info("Total execution time getUserAlertPreference"
				+ String.valueOf(endTime - startTime) + "(ms)");

		return respList;

	}

	public List<UserAlertPreferenceImpl> newGetUserAlertPreference(String loginId)  throws CustomFault{
		List<UserAlertPreferenceImpl> respList = new ArrayList<UserAlertPreferenceImpl>();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		long startTime = System.currentTimeMillis();

		UserAlertPreferenceImpl impl = null;

		Connection prodConnection = null;
		Statement statement = null;
		Statement statement1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		List<Integer> eventTypeIDList = new ArrayList<>();;
		Map<String, Map<String, String>> smsMap = null;
		Map<String, Map<String, String>> emailMap = null;
		List<String> eventTypeCodes = new ArrayList<>(); ;
		List<String> eventTypeNames = new ArrayList<>(); ;
		try {
			ConnectMySQL connMySql = new ConnectMySQL();
			prodConnection = connMySql.getConnection();
			statement = prodConnection.createStatement();
			rs = statement.executeQuery("select * from  event_type");
			while (rs.next()) {
				 
				String eventTypeName = rs.getString("Event_Type_Name");
				String eventTypeCode = rs.getString("Event_Type_Code");
				int eventTypeID = rs.getInt("Event_Type_ID");
				eventTypeIDList.add(eventTypeID);
				eventTypeCodes.add(eventTypeCode);
				eventTypeNames.add(eventTypeName);
				
			}
			iLogger.info("eventTypeIDList" + eventTypeIDList);
			iLogger.info("eventTypeCodes" + eventTypeCodes);
			iLogger.info("eventTypeNames" + eventTypeNames);
			
				statement1 = prodConnection.createStatement();

				String querry = "select * from MUserAlertPref where userid ='" + loginId + "'";
				iLogger.info("querry for MUserAlertPref" + querry);
				rs1 = statement1.executeQuery(querry);
				
				if(rs1.next() == false ){
					iLogger.info("enter into rs empty block");
					for (int i = 0; i < eventTypeCodes.size(); i++) {
						impl = new UserAlertPreferenceImpl();
						impl.setSMSEvent(false);
						impl.setEmailEvent(false);
						impl.setEventTypeName(eventTypeNames.get(i));
						impl.setEventTypeId(eventTypeIDList.get(i));
						//impl.setEventTypeCode(eventTypeCodes.get(i));
						respList.add(impl);
					}
					return respList;
				}else{
				do {
					String prefLevel = rs1.getString("PrefLevel");
					String commMode = rs1.getString("CommMode");
                     
					if (prefLevel != null && prefLevel.equalsIgnoreCase("AlertType")) {

						String preference = rs1.getString("Preference");
						ObjectMapper mapper = new ObjectMapper();
						if (commMode != null && commMode.equalsIgnoreCase("SMS")) {
							smsMap = mapper.readValue(preference,
									new TypeReference<Map<String, Map<String, String>>>() {
									}); 
						}
						if (commMode != null && commMode.equalsIgnoreCase("Email")) {
							emailMap = mapper.readValue(preference,
									new TypeReference<Map<String, Map<String, String>>>() {
									});
						}

					}
					else{
						bLogger.error("for this user alredy prefernce set prefernce by Alert level ");
						throw new CustomFault("for this user alredy prefernce set prefernce by Alert level");
					}

				} while (rs1.next()); }
				iLogger.info("emailMap" + emailMap);
				iLogger.info("smsMap" + smsMap);
				for (int i = 0; i < eventTypeCodes.size(); i++) {
					impl = new UserAlertPreferenceImpl();
					String smsCheck = smsMap.get(eventTypeCodes.get(i)).get("Pref");
					iLogger.info("CheckSms" + smsCheck);
					if (smsCheck != null && smsCheck.equalsIgnoreCase("1")) {
						impl.setSMSEvent(true);
					} else {
						impl.setSMSEvent(false);
					}

					String emailCheck = emailMap.get(eventTypeCodes.get(i)).get("Pref");
					iLogger.info("emailCheck" + emailCheck);
					if (emailCheck != null && emailCheck.equalsIgnoreCase("1")) {
						impl.setEmailEvent(true);
					} else {
						impl.setEmailEvent(false);
					}

					impl.setEventTypeName(eventTypeNames.get(i));
					impl.setEventTypeId(eventTypeIDList.get(i));
					//impl.setEventTypeCode(eventTypeCodes.get(i));
					respList.add(impl);
				}
			

		} catch (SQLException e) {

			e.printStackTrace();
			fLogger.fatal("SQL Exception in fetching data from mysql::" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace(); // n
			System.out.println("getUserAlertPreference : Exception" + e.toString());// n
			fLogger.error("getUserAlertPreference : Exception " + e.getMessage());// n
		}

		finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (rs1 != null)
				try {
					rs1.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			if (statement1 != null)
				try {
					statement1.close();
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

		}
		long endTime = System.currentTimeMillis();
		iLogger.info("Total execution time getUserAlertPreference" + String.valueOf(endTime - startTime) + "(ms)");

		return respList;

	}

	// ********************* End of Get User Alert Preference
	// Type*******************************************
	// **** Set Method for UserAlertPreference***//

	/**
	 * method to set user alert preference
	 * 
	 * @param loginId
	 *            , EventTypeId, isSms, isEmail
	 * @return String
	 * @throws CustomFault
	 */

	public String setUserAlertPreference(
			List<UserAlertPreferenceRespContract> respContractList)
					throws CustomFault {/*
					 * 
					 * int EventTypeId =0; boolean isSms,isEmail;
					 * UserAlertPreferenceRespContract respObj
					 * =null; DomainServiceImpl domainService = new
					 * DomainServiceImpl();
					 * Iterator<UserAlertPreferenceRespContract>
					 * listIter = respContractList.iterator(); long
					 * startTime = System.currentTimeMillis();
					 * Session session =
					 * HibernateUtil.getSessionFactory
					 * ().getCurrentSession();
					 * session.beginTransaction(); try {
					 * while(listIter.hasNext()){ respObj=
					 * listIter.next(); EventTypeId=
					 * respObj.getEventTypeId(); isSms =
					 * respObj.isSMSEvent();
					 * isEmail=respObj.isEmailEvent(); loginId =
					 * respObj.getLoginId(); EventTypeEntity
					 * eventEntity =
					 * domainService.getEventTypeDetails
					 * (EventTypeId); String eventName=null;
					 * if(eventEntity == null) throw new
					 * CustomFault("Event Type is Invalid"); else
					 * eventName= eventEntity.getEventTypeName();
					 * 
					 * 
					 * 
					 * List<Integer> currentCatalogValues = new
					 * LinkedList<Integer>(); if(! (session.isOpen()
					 * )){ session =
					 * HibernateUtil.getSessionFactory(
					 * ).openSession();
					 * session.getTransaction().begin(); } Query q =
					 * session.createQuery(
					 * "from PreferenceEntity where contact='"
					 * +loginId+"'"); Iterator itr =
					 * q.list().iterator();
					 * 
					 * while(itr.hasNext()){
					 * infoLogger.info("inside preference query" );
					 * PreferenceEntity prefEntity =
					 * (PreferenceEntity)itr.next();
					 * currentCatalogValues
					 * .add(prefEntity.getCatalogValueId
					 * ().getCatalogValueId());
					 * 
					 * }
					 * 
					 * List<Integer> catalogValues = new
					 * LinkedList<Integer>();
					 * 
					 * if(isSms==true || isEmail==true) {
					 * List<PreferenceCatalogEntity> mode = new
					 * LinkedList<PreferenceCatalogEntity>();
					 * 
					 * if(isSms==true) { PreferenceCatalogEntity
					 * prefObj = getPreferenceCatalogEntity(1);
					 * 
					 * mode.add(prefObj);
					 * 
					 * } if(isEmail==true) { PreferenceCatalogEntity
					 * prefObj = getPreferenceCatalogEntity(2);
					 * 
					 * mode.add(prefObj); } if(!
					 * (session.isOpen())){ session =
					 * HibernateUtil.getSessionFactory
					 * ().openSession();
					 * session.getTransaction().begin(); }
					 * infoLogger
					 * .info("before CatalogValuesEntity query");
					 * Query q1 = session.createQuery(
					 * "from CatalogValuesEntity where catalogValue='"
					 * +eventName+"' and catalogId in (:list)").
					 * setParameterList("list", mode); Iterator
					 * itrtr = q1.list().iterator();
					 * 
					 * while(itrtr.hasNext()) { infoLogger.info(
					 * "inside CatalogValuesEntity query");
					 * CatalogValuesEntity catalogValueEntity =
					 * (CatalogValuesEntity)itrtr.next();
					 * catalogValues
					 * .add(catalogValueEntity.getCatalogValueId());
					 * } } List<Integer> retainValues = new
					 * LinkedList<Integer>();
					 * retainValues.addAll(currentCatalogValues);
					 * if(!(catalogValues == null ||
					 * catalogValues.isEmpty())){
					 * retainValues.retainAll(catalogValues);
					 * currentCatalogValues.removeAll(retainValues);
					 * catalogValues.removeAll(retainValues); } int
					 * index=0; int i=0;
					 * 
					 * for(i=0; i<currentCatalogValues.size(); i++)
					 * { if(index <= i && catalogValues.size()>0) {
					 * Transaction tx = session.getTransaction();
					 * if(tx==null) tx=session.beginTransaction();
					 * tx.begin();
					 * infoLogger.info("before update query"); Query
					 * query1 = session.createQuery(
					 * "update PreferenceEntity set catalogValueId='"
					 * +catalogValues.get(index)+"' where " +
					 * 
					 * "contact='"+loginId+"' and catalogValueId='"+
					 * currentCatalogValues.get(i)+"'");
					 * 
					 * query1.executeUpdate(); index++; tx.commit();
					 * } else break;
					 * 
					 * }
					 * 
					 * while(i < currentCatalogValues.size())
					 * 
					 * { Transaction tx1 = session.getTransaction();
					 * if(tx1==null)
					 * 
					 * tx1=session.beginTransaction();
					 * 
					 * tx1.begin(); if(! (session.isOpen() )){
					 * session =
					 * HibernateUtil.getSessionFactory().openSession
					 * (); session.getTransaction().begin(); }
					 * infoLogger.info("before delete query"); Query
					 * query2 = session.createQuery(
					 * "delete from PreferenceEntity where catalogValueId='"
					 * +currentCatalogValues.get(i)+
					 * "' and contact='"+loginId+"'");
					 * query2.executeUpdate(); tx1.commit(); i++; }
					 * 
					 * while(index < catalogValues.size())
					 * 
					 * { infoLogger.info("setting the values");
					 * PreferenceEntity newPreferenceEntity = new
					 * PreferenceEntity(); DomainServiceImpl
					 * domainSvc = new DomainServiceImpl();
					 * ContactEntity contact =
					 * domainSvc.getContactDetails(loginId);
					 * 
					 * CatalogValuesEntity catValues =
					 * getCatalogValuesEntity
					 * (currentCatalogValues.get(index));
					 * newPreferenceEntity
					 * .setCatalogValueId(catValues);
					 * newPreferenceEntity.setContact(contact);
					 * newPreferenceEntity.save();
					 * infoLogger.info("after saving the data");
					 * index++;
					 * 
					 * } } } finally {
					 * if(session.getTransaction().isActive()) {
					 * session.getTransaction().commit(); }
					 * if(session.isOpen()) { session.flush();
					 * session.close(); } } long
					 * endTime=System.currentTimeMillis();
					 * infoLogger.info("Total execution time "+
					 * String.valueOf(endTime-startTime)+ "(ms)");
					 * 
					 * 
					 * return "succcess";
					 */
		Logger iLogger = InfoLoggerClass.logger;

		Iterator i = respContractList.iterator();
		String evtTypeName = null;
		while (i.hasNext()) {
			UserAlertPreferenceRespContract resp = (UserAlertPreferenceRespContract) i
					.next();
			evtTypeName = resp.getEventTypeName();

		}

		long startTime = System.currentTimeMillis();
		List<Integer> newCatalogValueIds = new ArrayList<Integer>();
		Map<String, HashMap<Integer, Integer>> catlogMap = new HashMap<String, HashMap<Integer, Integer>>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		//JCB6239.sn
        if(session.getTransaction().isActive() && session.isDirty())
        {
            iLogger.info("Opening a new session");
            session = HibernateUtil.getSessionFactory().openSession();
        }
        //JCB6239.en
		session.beginTransaction();
		HashMap<Integer, Integer> valueMap = null;
		String eventTypeName = null;
		try {
			// get all event names along with their catalog value ids in hashmap
			iLogger
			.info("Query execution in setUserAlertPreference Method : ");
			Query q = session
					.createQuery("select e1.eventTypeName,c2.catalogValueId,c2.catalogId from EventTypeEntity e1,CatalogValuesEntity c2 where e1.eventTypeName=c2.catalogValue");
			Iterator iterat = q.list().iterator();
			Object[] result = null;
			while (iterat.hasNext()) {
				result = (Object[]) iterat.next();
				PreferenceCatalogEntity pce = (PreferenceCatalogEntity) result[2];
				int catalogId = pce.getCatalogId();
				int catalogValueId = (Integer) result[1];
				eventTypeName = (String) result[0];

				if (catlogMap.containsKey(eventTypeName)) {
					valueMap = catlogMap.get((String) result[0]);
					if (!valueMap.containsKey(catalogId)) {
						valueMap.put(catalogId, catalogValueId);
					}
					catlogMap.remove(eventTypeName);
					catlogMap.put(eventTypeName, valueMap);
				} else {
					valueMap = new HashMap<Integer, Integer>();
					valueMap.put(catalogId, catalogValueId);
					catlogMap.put((String) result[0], valueMap);
				}
			}

			// iterate over response
			UserAlertPreferenceRespContract respObj1 = null;
			Iterator<UserAlertPreferenceRespContract> listIter1 = respContractList
					.iterator();
			while (listIter1.hasNext()) {
				respObj1 = listIter1.next();
				loginId = respObj1.getLoginId();
				if (respObj1.isSMSEvent()) {
					newCatalogValueIds.add(catlogMap.get(
							respObj1.getEventTypeName()).get(1));
				}
				if (respObj1.isEmailEvent()) {
					newCatalogValueIds.add(catlogMap.get(
							respObj1.getEventTypeName()).get(2));
				}
				if (respObj1.isWhatsappEvent()) {
					newCatalogValueIds.add(catlogMap.get(respObj1.getEventTypeName()).get(14));
				}
			}
			// get all existing catalog id values for user
			List<Integer> existingCatalogIdValues = new ArrayList<Integer>();
			Query qu = session
					.createQuery("select pe.catalogValueId from PreferenceEntity pe,CatalogValuesEntity cve where pe.contact='"
							+ loginId
							+ "' and pe.catalogValueId = cve.catalogValueId"
							//+ " and cve.catalogId in (1,2)");//CR500.o
							+ " and cve.catalogId in (1,2,14)");//CR500.n

			Iterator itr = qu.list().iterator();
			CatalogValuesEntity cvEntity = null;
			while (itr.hasNext()) {
				cvEntity = (CatalogValuesEntity) itr.next();

				existingCatalogIdValues.add(cvEntity.getCatalogValueId());
			}

			session.close();
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			//JCB6239.sn
	        if(session.getTransaction().isActive() && session.isDirty())
	        {
	            iLogger.info("Opening a new session");
	            session = HibernateUtil.getSessionFactory().openSession();
	        }
	        //JCB6239.en
			session.beginTransaction();
			List<Integer> differList1 = new ArrayList<Integer>();
			differList1.addAll(newCatalogValueIds);
			differList1.removeAll(existingCatalogIdValues); // add this list

			Iterator<Integer> iterIds = differList1.iterator();
			cvEntity = null;
			while (iterIds.hasNext()) {
				PreferenceEntity pe = new PreferenceEntity();
				pe.setContact(new ContactEntity(loginId));
				cvEntity = new CatalogValuesEntity();
				cvEntity.setCatalogValueId(iterIds.next());
				pe.setCatalogValueId(cvEntity);
				pe.save();
			}
			if (session != null && session.isOpen()) {
				if (session.getTransaction().isActive()) {
					session.getTransaction().commit();
				}
				if (session.isOpen()) {
					session.flush();
					session.close();
				}
			}

			session = HibernateUtil.getSessionFactory().getCurrentSession();
			//JCB6239.sn
	        if(session.getTransaction().isActive() && session.isDirty())
	        {
	            iLogger.info("Opening a new session");
	            session = HibernateUtil.getSessionFactory().openSession();
	        }
	        //JCB6239.en
			session.beginTransaction();
			differList1 = new ArrayList<Integer>();
			differList1.addAll(existingCatalogIdValues);
			differList1.removeAll(newCatalogValueIds);// delete these
			ListToStringConversion conversionObj = new ListToStringConversion();
			String catlogValueIds = null;
			if (differList1 != null && differList1.size() > 0) {

				catlogValueIds = conversionObj
						.getIntegerListString(differList1).toString();
			}

			/*
			 * String queryString =
			 * "select catalogValueId from CatalogValuesEntity where catalogValue ='"
			 * + evtTypeName + "'";
			 * 
			 * Iterator itr1 =
			 * session.createQuery(queryString).list().iterator(); List<Integer>
			 * catalogValueId = new ArrayList<Integer>();
			 * 
			 * while (itr1.hasNext()) { int catalog = (Integer) itr1.next();
			 * infoLogger.info("catalog " + catalog);
			 * catalogValueId.add(catalog); }
			 * catalogValueId.removeAll(newCatalogValueIds);
			 * 
			 * String catlogValueIds1 = null; if (catalogValueId != null &&
			 * catalogValueId.size() > 0) {
			 * 
			 * catlogValueIds1 = conversionObj.getIntegerListString(
			 * catalogValueId).toString(); infoLogger.info("catlogValueIds1 " +
			 * catlogValueIds1); }
			 */
			// delete these
			Query qu2 = session
					.createQuery("delete from PreferenceEntity where catalogValueId in ("
							+ catlogValueIds
							+ ") and contact ='"
							+ loginId
							+ "' ");
			int row1 = qu2.executeUpdate();
		} finally {
			if (session != null && session.isOpen()) {
				if (session.getTransaction().isActive()) {
					session.getTransaction().commit();
				}
				if (session.isOpen()) {
					session.flush();
					session.close();
				}
			}

		}
		long endTime = System.currentTimeMillis();
		iLogger.info("Total execution time for setUserAlertPreference "
				+ String.valueOf(endTime - startTime) + "(ms)");
		return "Success";

	}

	/**
	 * method to set admin alert preference
	 * 
	 * @param respContractList
	 * @return String
	 */
	public String setAdminAlertPreference(
			List<UserAlertPreferenceRespContract> respContractList)
					throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entered into the setAdminAlertPreference Method");
		long startTime = System.currentTimeMillis();
		List<Integer> newCatalogValueIds = new ArrayList<Integer>();
		Map<Integer, HashMap<Integer, Integer>> catlogMap = new HashMap<Integer, HashMap<Integer, Integer>>();
		List<Integer> eventIds=new LinkedList<Integer>();
		eventIds=respContractList.stream().map(UserAlertPreferenceRespContract :: getEventId).collect(Collectors.toList());
		Session session = HibernateUtil.getSessionFactory().openSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
//		if(session.getTransaction().isActive() && session.isDirty())
//		{
//			iLogger.info("Opening a new session");
//			session = HibernateUtil.getSessionFactory().openSession();
//		}
		session.beginTransaction();
		HashMap<Integer, Integer> valueMap = null;
		try {
			iLogger.info("Entered into the try block");
			// get all event names along with their catalog value ids in hashmap
			Query q = session
					.createQuery("select e1.eventId,c2.catalogValueId,c2.catalogId from EventEntity e1,CatalogValuesEntity c2 where e1.eventName=c2.catalogValue and e1.eventId in (:eventIds)");
			q.setParameterList("eventIds", eventIds);
			Iterator iterat = q.list().iterator();
			Object[] result = null;
			iLogger.info("Check1");
			int eventId, catalogId, catalogValueId;
			while (iterat.hasNext()) {
				result = (Object[]) iterat.next();
				PreferenceCatalogEntity pce = (PreferenceCatalogEntity) result[2];
				catalogId = pce.getCatalogId();
				catalogValueId = (Integer) result[1];
				eventId = (Integer) result[0];

				if (catlogMap.containsKey(eventId)) {
					valueMap = catlogMap.get((Integer) result[0]);
					if (!valueMap.containsKey(catalogId)) {
						valueMap.put(catalogId, catalogValueId);
					}
					catlogMap.remove(eventId);
					catlogMap.put(eventId, valueMap);
				} else {
					valueMap = new HashMap<Integer, Integer>();
					valueMap.put(catalogId, catalogValueId);
					catlogMap.put((Integer) result[0], valueMap);
				}
			}
			iLogger.info("Check2");
			// iterate over response
			UserAlertPreferenceRespContract respObj1 = null;
			Iterator<UserAlertPreferenceRespContract> listIter1 = respContractList
					.iterator();
			while (listIter1.hasNext()) {
				respObj1 = listIter1.next();
				loginId = respObj1.getLoginId();


				if (respObj1.isSMSEvent()) {
					newCatalogValueIds.add(catlogMap.get(respObj1.getEventId())
							.get(9));
				}
				if (respObj1.isEmailEvent()) {
					newCatalogValueIds.add(catlogMap.get(respObj1.getEventId())
							.get(10));
				}
			}
			// get all existing catalog id values for user
			List<Integer> existingCatalogIdValues = new ArrayList<Integer>();
			Query qu = session
					.createQuery("select pe.catalogValueId from PreferenceEntity pe,CatalogValuesEntity cve where pe.contact='"
							+ loginId
							+ "'and pe.catalogValueId = cve.catalogValueId"
							+ " and cve.catalogId in (9,10)");
			Iterator itr = qu.list().iterator();
			iLogger.info("Check3");
			CatalogValuesEntity cvEntity = null;
			while (itr.hasNext()) {
				cvEntity = (CatalogValuesEntity) itr.next();

				existingCatalogIdValues.add(cvEntity.getCatalogValueId());
			}

			session.close();
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
			session.beginTransaction();
			List<Integer> differList1 = new ArrayList<Integer>();
			differList1.addAll(newCatalogValueIds);
			differList1.removeAll(existingCatalogIdValues); // add this list
			iLogger.info("Check4");
			Iterator<Integer> iterIds = differList1.iterator();
			cvEntity = null;
			while (iterIds.hasNext()) {
				PreferenceEntity pe = new PreferenceEntity();
				pe.setContact(new ContactEntity(loginId));
				cvEntity = new CatalogValuesEntity();
				cvEntity.setCatalogValueId(iterIds.next());
				pe.setCatalogValueId(cvEntity);
				pe.save();
			}
			if (session != null && session.isOpen()) {
				try
				{
					if (session.getTransaction().isActive()) {
						session.getTransaction().commit();
					}
				}

				catch(Exception e)
				{

					//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
					Logger fLogger = FatalLoggerClass.logger;
					fLogger.fatal("Exception in commiting the record:"+e);
				}
				if (session.isOpen()) {
					session.flush();
					session.close();
				}
			}

			session = HibernateUtil.getSessionFactory().getCurrentSession();
			//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
			session.beginTransaction();
			differList1 = new ArrayList<Integer>();
			differList1.addAll(existingCatalogIdValues);
			differList1.removeAll(newCatalogValueIds);// delete these
			ListToStringConversion conversionObj = new ListToStringConversion();
			String catlogValueIds = null;
			if (differList1 != null && differList1.size() > 0) {

				catlogValueIds = conversionObj
						.getIntegerListString(differList1).toString();
			}
			// delete these
			Query qu2 = session
					.createQuery("delete from PreferenceEntity where catalogValueId in ("
							+ catlogValueIds
							+ ") and contact ='"
							+ loginId
							+ "' ");
			int row1 = qu2.executeUpdate();
		} finally {

			try
			{if (session != null && session.isOpen()) {
				if (session.getTransaction().isActive()) {
					session.getTransaction().commit();
				}
			}
			}

			catch(Exception e)
			{
				e.printStackTrace();
				Logger fLogger = FatalLoggerClass.logger;
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}
			if (session.isOpen()) {
				session.flush();
				session.close();
			}
		}


		long endTime = System.currentTimeMillis();
		iLogger.info("Total execution time for setAdminAlertPreference "
				+ String.valueOf(endTime - startTime) + "(ms)");
		return "Success";
	}
	//DF20170315 Supriya - Dealer AdminAlert Preference
	/**
	 * method to set dealeradmin alert preference
	 * 
	 * @param respContractList
	 * @return String
	 */
	public String setDealerAdminAlertPreference(
			List<UserAlertPreferenceRespContract> respContractList)
					throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("Entered setDealerAdminAlertPreference");
		long startTime = System.currentTimeMillis();
		List<Integer> newCatalogValueIds = new ArrayList<Integer>();
		Map<Integer, HashMap<Integer, Integer>> catlogMap = new HashMap<Integer, HashMap<Integer, Integer>>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();
		HashMap<Integer, Integer> valueMap = null;
		try {
			iLogger.info("Entered into try Block");
			// get all event names along with their catalog value ids in hashmap
			Query q = session
					.createQuery("select e1.eventId,c2.catalogValueId,c2.catalogId from EventEntity e1,CatalogValuesEntity c2 where e1.eventName=c2.catalogValue and e1.eventTypeId =2");
			iLogger.info("Query Created");
			List<Integer> eventIds = Arrays.asList(4, 5, 6, 16, 17);
			Iterator iterat = q.list().iterator();
			Object[] result = null;
			int eventId,catalogId, catalogValueId;
			while (iterat.hasNext()) {
				result = (Object[]) iterat.next();
				PreferenceCatalogEntity pce = (PreferenceCatalogEntity) result[2];
				catalogId = pce.getCatalogId();
				catalogValueId = (Integer) result[1];
				eventId = (Integer) result[0];
				if(!eventIds.contains(eventId)){
					if (catlogMap.containsKey(eventId)) {
						valueMap = catlogMap.get((Integer) result[0]);
						if (!valueMap.containsKey(catalogId)) {
							valueMap.put(catalogId, catalogValueId);
						}
						catlogMap.remove(eventId);
						catlogMap.put(eventId, valueMap);
					}
					else {
						valueMap = new HashMap<Integer, Integer>();
						valueMap.put(catalogId, catalogValueId);
						catlogMap.put((Integer) result[0], valueMap);
					}

				}
			}
			iLogger.info("eventIds of health alerts chosen");
			// iterate over response
			UserAlertPreferenceRespContract respObj1 = null;
			Iterator<UserAlertPreferenceRespContract> listIter1 = respContractList
					.iterator();
			iLogger.info("iterating over response");
			while (listIter1.hasNext()) {
				respObj1 = listIter1.next();
				loginId = respObj1.getLoginId();
				if (respObj1.isSMSEvent()) {
					newCatalogValueIds.add(catlogMap.get(respObj1.getEventId())
							.get(9));
				}
				if (respObj1.isEmailEvent()) {
					newCatalogValueIds.add(catlogMap.get(respObj1.getEventId())
							.get(10));
				}
			}
			// get all existing catalog id values for user
			List<Integer> existingCatalogIdValues = new ArrayList<Integer>();
			Query qu = session
					.createQuery("select pe.catalogValueId from PreferenceEntity pe,CatalogValuesEntity cve where pe.contact='"
							+ loginId
							+ "'and pe.catalogValueId = cve.catalogValueId"
							//+ " and cve.catalogId in (9,10)");
							+ " and cve.catalogId in (9,10,13)");//CR500.n
			Iterator itr = qu.list().iterator();
			CatalogValuesEntity cvEntity = null;
			while (itr.hasNext()) {
				cvEntity = (CatalogValuesEntity) itr.next();

				existingCatalogIdValues.add(cvEntity.getCatalogValueId());
			}

			session.close();
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
			session.beginTransaction();
			List<Integer> differList1 = new ArrayList<Integer>();
			differList1.addAll(newCatalogValueIds);
			differList1.removeAll(existingCatalogIdValues); // add this list

			differList1.add(45);
			differList1.add(46);

			Iterator<Integer> iterIds = differList1.iterator();
			cvEntity = null;
			while (iterIds.hasNext()) {
				PreferenceEntity pe = new PreferenceEntity();
				pe.setContact(new ContactEntity(loginId));
				cvEntity = new CatalogValuesEntity();
				cvEntity.setCatalogValueId(iterIds.next());
				pe.setCatalogValueId(cvEntity);
				pe.save();
			}

			if (session != null && session.isOpen()) {
				try
				{
					if (session.getTransaction().isActive()) {
						session.getTransaction().commit();
					}
				}

				catch(Exception e)
				{
                     e.printStackTrace();
					Logger fLogger = FatalLoggerClass.logger;
					fLogger.fatal("Exception in commiting the record:"+e);
				}
				if (session.isOpen()) {
					session.flush();
					session.close();
				}
			}

			session = HibernateUtil.getSessionFactory().getCurrentSession();
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
			session.beginTransaction();
			iLogger.info("Catalog values");
			differList1 = new ArrayList<Integer>();
			differList1.addAll(existingCatalogIdValues);
			differList1.removeAll(newCatalogValueIds);// delete these
			ListToStringConversion conversionObj = new ListToStringConversion();
			String catlogValueIds = null;
			if (differList1 != null && differList1.size() > 0) {

				catlogValueIds = conversionObj
						.getIntegerListString(differList1).toString();
			}

			// delete these
			Query qu2 = session
					.createQuery("delete from PreferenceEntity where catalogValueId in ("
							+ catlogValueIds
							+ ") and contact ='"
							+ loginId
							+ "' ");
			int row1 = qu2.executeUpdate();	
		}finally {

			try
			{if (session != null && session.isOpen()) {
				if (session.getTransaction().isActive()) {
					session.getTransaction().commit();
				}
			}
			}

			catch(Exception e)
			{
				e.printStackTrace();
				Logger fLogger = FatalLoggerClass.logger;
				fLogger.fatal("Exception in commiting the record:"+e);
			}
			if (session.isOpen()) {
				session.flush();
				session.close();
			}
		}


		long endTime = System.currentTimeMillis();
		iLogger.info("Total execution time for setAdminAlertPreference "
				+ String.valueOf(endTime - startTime) + "(ms)");
		return "Success";
	}

	public CatalogValuesEntity getCatalogValuesEntity(int eventTypeId)

	{

		CatalogValuesEntity catalogValObj = new CatalogValuesEntity(eventTypeId);

		if (catalogValObj != null && catalogValObj.getCatalogValueId() != 0)

			return catalogValObj;

		else

			return null;

	}

	/* *** Get Preference Catalog entity ****** */
	public PreferenceCatalogEntity getPreferenceCatalogEntity(int catalogId)

	{

		PreferenceCatalogEntity prefcatalogValObj = new PreferenceCatalogEntity(
				catalogId);

		if (prefcatalogValObj != null && prefcatalogValObj.getCatalogId() != 0)

			return prefcatalogValObj;
		else
			return null;

	}

	// End set method for useralert preference

	// ******************** Start of setUserPreference *************************

	/**
	 * 
	 * @param contactId
	 *            is passed as an input to set the values
	 * @param catalogValueId
	 *            is passed as an input to set the values
	 * @return success if the values are set successfully
	 * @throws CustomFault
	 */
	public String setUserPreference(String contactId, int catalogValueId)
			throws CustomFault {

		Logger bLogger = BusinessErrorLoggerClass.logger;

		DomainServiceImpl domainService1 = new DomainServiceImpl();
		PreferenceEntity userPreference = domainService1
				.getUserPreference(catalogValueId);

		int catalogValueID = 0;
		if (userPreference == null)
			throw new CustomFault("Invalid Catalog Value ID");
		else
			catalogValueID = userPreference.getCatalogValueId()
			.getCatalogValueId();

		Session session4 = HibernateUtil.getSessionFactory()
				.getCurrentSession();
		session4.beginTransaction();

		try {
			List<Integer> currCatalogValues = new LinkedList<Integer>();
			Query qury = session4
					.createQuery("from PreferenceEntity where contact='"
							+ contactId + "'");

			Iterator itr = qury.list().iterator();
			while (itr.hasNext()) {

				PreferenceEntity prefEntity = (PreferenceEntity) itr.next();
				currCatalogValues.add(prefEntity.getCatalogValueId()
						.getCatalogValueId());

			}
			for (int l = 0; l < currCatalogValues.size(); l++) {
//				bLogger.debug("currentCatalogValues**&&&& K * + "
//						+ currCatalogValues.get(l));
			}
			List<Integer> catalogValues1 = new LinkedList<Integer>();
			Query qry = session4
					.createQuery("from CatalogValuesEntity where catalogValueId="
							+ catalogValueID);

			Iterator itr4 = qry.list().iterator();

			while (itr4.hasNext()) {
				CatalogValuesEntity catalogValueEntity = (CatalogValuesEntity) itr4
						.next();
				catalogValues1.add(catalogValueEntity.getCatalogValueId());

			}

			/*
			 * infoLogger.info("CatalogValues*** + " +
			 * catalogValues1.size()); for (int p1 = 0; p1 <
			 * catalogValues1.size(); p1++) {
			 * infoLogger.info("catalogValues**&&&& K * + " +
			 * catalogValues1.get(p1)); }
			 */
			List<Integer> retainValues = new LinkedList<Integer>();
			retainValues.addAll(currCatalogValues);

			if (!(catalogValues1 == null || catalogValues1.isEmpty())) {
				retainValues.retainAll(catalogValues1);

				currCatalogValues.removeAll(retainValues);
				catalogValues1.removeAll(retainValues);
			}

			int index = 0;
			int i = 0;
			for (i = 0; i < currCatalogValues.size(); i++) {

				if (index <= i && catalogValues1.size() > 0) {
					Transaction tz = session4.getTransaction();
					if (tz == null)
						tz = session4.beginTransaction();
					tz.begin();

					Query query1 = session4
							.createQuery("update PreferenceEntity set catalogValueId='"
									+ catalogValues1.get(index)
									+ "'"
									+ " where "
									+ "contact='"
									+ contactId
									+ "' and  catalogValueId='"
									+ currCatalogValues.get(i) + "'");
					query1.executeUpdate();
					index++;
					tz.commit();
				} else
					break;
			}
			while (i < currCatalogValues.size()) {

				Transaction tx1 = session4.getTransaction();
				if (tx1 == null)
					tx1 = session4.beginTransaction();
				tx1.begin();
				Query query2 = session4
						.createQuery("delete from PreferenceEntity where catalogValueId='"
								+ currCatalogValues.get(i)
								+ "' and contact='"
								+ contactId + "'");
				query2.executeUpdate();
				tx1.commit();
				i++;
			}
			while (index < catalogValues1.size()) {
				PreferenceEntity newPreferenceEntity = new PreferenceEntity();
				DomainServiceImpl domainSvc = new DomainServiceImpl();

				ContactEntity contact = domainSvc.getContactDetails(contactId);
				// CatalogValuesEntity catValues =
				// getCatalogValuesEntity(catalogValues1.get(i));
				CatalogValuesEntity catValues = getCatalogValuesEntity(catalogValues1
						.get(index));

				newPreferenceEntity.setContact(contact);
				newPreferenceEntity.setCatalogValueId(catValues);

				newPreferenceEntity.save();

				index++;

			}

		} finally {
			if (session4.getTransaction().isActive()) {
				session4.getTransaction().commit();
			}

			if (session4.isOpen()) {
				session4.flush();
				session4.close();
			}

		}

		return "SUCCESS";

	}

	// ******************** End of setUserPreference *************************

	// ******************** Start of getUserPreference *************************

	/**
	 * 
	 * @param contactId
	 *            is passed to get preference based on individual user
	 * @return listUserPreferenceImpl list of the preference that includes the
	 *         catalodId,calalogValue
	 */
	public List<UserPreferenceImpl> getUserPreference(String contactId) {
		List<UserPreferenceImpl> listUserPreferenceImpl = new LinkedList<UserPreferenceImpl>();
		// Logger infoLogger = Logger.getLogger("infoLogger");
		Logger iLogger = InfoLoggerClass.logger;

		Session session4 = HibernateUtil.getSessionFactory()
				.getCurrentSession();
		session4.beginTransaction();
		try {
			String query = "from PreferenceEntity where contact='" + contactId
					+ "'";
			Iterator itr3 = session4.createQuery(query).list().iterator();

			while (itr3.hasNext()) {
				PreferenceEntity prefEntity = (PreferenceEntity) itr3.next();
				UserPreferenceImpl userPreImpl = new UserPreferenceImpl();
				userPreImpl.setContactId(prefEntity.getContact()
						.getContact_id());
				userPreImpl.setCatalogValueId(prefEntity.getCatalogValueId()
						.getCatalogValueId());
				userPreImpl.setCatalogId(prefEntity.getCatalogValueId()
						.getCatalogId().getCatalogId());
				userPreImpl.setCatalogValue(prefEntity.getCatalogValueId()
						.getCatalogValue());
				userPreImpl.setCatalogName(prefEntity.getCatalogValueId()
						.getCatalogId().getCatalogName());

				listUserPreferenceImpl.add(userPreImpl);

			}
		} catch (Exception e) {
			iLogger.info("This is showing error as   " + e.getMessage());
		} finally {
			if (session4.getTransaction().isActive()) {
				session4.getTransaction().commit();
			}

			if (session4.isOpen()) {
				session4.flush();
				session4.close();
			}

		}
		return listUserPreferenceImpl;
	}

	public PreferenceEntity getUserPreferenceEntity(int catalogValueId)
			throws CustomFault {
		// Logger businessError = Logger.getLogger("businessErrorLogger");
		// Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		PreferenceEntity prefEntity = null;

		try {

			Query qury = session.createQuery("from PreferenceEntity"); /*
			 * where
			 * contactId
			 * =
			 * "+catalogValueId);
			 */
			Iterator itr4 = qury.list().iterator();

			prefEntity = (PreferenceEntity) itr4.next();


			// PreferenceEntity assetGroupEntityobj=new
			// PreferenceEntity(catalogValueId);
			if (prefEntity.getCatalogValueId() == null) {
				throw new CustomFault("Contact ID specified is Invalid");
			}
		} catch (CustomFault e) {
			bLogger.error("Custom Fault: " + e.getFaultInfo());
		}

		catch (Exception e) {
			fLogger.fatal("Exception :" + e);
		}

		finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}
		return prefEntity;
	}

	/**
	 * method for authenticate the user
	 * 
	 * @param login_id
	 * @param password
	 * @return boolean
	 * @throws CustomFault
	 */

	public boolean authenticateUser(String login_id, String password)
			throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;

		boolean valid_user = true;
		String pwd;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try {
			/*
			 * contact = (ContactEntity) session .get(ContactEntity.class,
			 * login_id);
			 */
			if (!(session.isOpen())) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			// defetc Id1200: QA Server issue**
			Query q = session
					.createQuery("from ContactEntity where contact_id='"
							+ login_id + "'");
			Iterator itr = q.list().iterator();
			while (itr.hasNext()) {
				contact = (ContactEntity) itr.next();
			}
			// defetc Id1200: QA Server issue**
			if (contact == null) {
				valid_user = false;
				error_msg = "Invalid LoginId";
			} else {
				boolean status = contact.isActive_status();
				if (status == false) {
					throw new CustomFault("the user has been deleted");
				} else {
					pwd = contact.getPassword();
					if (pwd.equals(password)) {
						sysGeneratedPassword = contact
								.getSysGeneratedPassword();
						// System.out
						iLogger.info("role "
								+ contact.getRole().getRole_id());
					} else {
						valid_user = false;
						error_msg = "Invalid Password";
					}
				}
			}
		} finally

		{
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}
			if (session.isOpen()) {
				session.flush();
				session.close();
			}
		}
		return valid_user;
	}

	/**
	 * method to get logged-in user's details
	 * 
	 * @param login_id
	 * @param password
	 * @return UserDetailsBO
	 * @throws CustomFault
	 * @throws IOException
	 */

	public UserDetailsBO getUserDetails(String login_id, String password)
			throws CustomFault, IOException {
		Logger iLogger = InfoLoggerClass.logger;

		long startTime = System.currentTimeMillis();
		boolean validUser = authenticateUser(login_id, password);
		if (validUser == false) {
			return this;
		} else {
			Session session = HibernateUtil.getSessionFactory()
					.getCurrentSession();
			session.beginTransaction();
			try {
				// get role details
				String smsService = null;
				String mapService = null;
				String JCBAdmin = null;

				RoleEntity role = contact.getRole();
				role_id = role.getRole_id();
				Query q = session.createQuery("from RoleEntity where role_id='"
						+ role_id + "'");
				Iterator ity = q.list().iterator();
				while (ity.hasNext()) {
					role = (RoleEntity) ity.next();
				}

				role_name = role.getRole_name();
				// added by smitha on july 1st 2013...DefectId: 790
				Properties prop = new Properties();
				prop.load(getClass()
						.getClassLoader()
						.getResourceAsStream(
								"remote/wise/resource/properties/configuration.properties"));
				smsService = prop.getProperty("SMSService");
				mapService = prop.getProperty("MapServiceConfig");
				// added by smitha on Aug 7th 2013...Defect Id:1116
				// JCBAdmin=prop.getProperty("JCBAdmin");
				// if(role_name.equalsIgnoreCase(JCBAdmin)){
				// ended ...Defect Id:1116
				ListToStringConversion conversionObj = new ListToStringConversion();
				List<String> configId = new LinkedList<String>();
				configId.add(smsService);
				configId.add(mapService);
				String configIdList = conversionObj.getStringList(configId)
						.toString();
				String configquery = "select isStatus,services from ConfigAppEntity where configuration_id in ("
						+ configIdList + ")";
				Iterator configlist = session.createQuery(configquery).list()
						.iterator();
				Boolean Status = null;
				String serviceName = null;
				List<Boolean> statuss = new LinkedList<Boolean>();
				Object result[] = null;
				while (configlist.hasNext()) {
					result = (Object[]) configlist.next();
					Status = (Boolean) result[0];
					serviceName = (String) result[1];
					statuss.add(Status);
				}
				if (statuss.size() != 0) {
					if (statuss.get(0) == true) {
						isSMS = true;
					}
					if (statuss.get(1) == true) {
						isMap = true;
					}
				}
				// }
				// ended on july 1st 2013...DefectId: 790
				// get account details
				String accountquery = "select account_id from AccountContactMapping where contact_id= '"
						+ login_id + "'";

				Iterator accountlist = session.createQuery(accountquery).list()
						.iterator();
				AccountEntity account = (AccountEntity) accountlist.next();
				account_id = account.getAccount_id();

				// get user login details from activity log
				String maxDatequery = "select max(login_date) from ContactActivityLogEntity where contact_id= '"
						+ login_id + "'";
				@SuppressWarnings("rawtypes")
				Iterator list = session.createQuery(maxDatequery).list()
				.iterator();
				//DF20170616 - SU334449 - Last login date is also passed through the response from now onwards
				if(list.hasNext()){
					Timestamp result1 = (Timestamp) list.next();
					if(result1 != null)
						last_login_date = result1.toString();
				}
				iLogger.info("**************USER DETAILS************");
				iLogger.info("Login ID: " + login_id);
				iLogger.info("Login User Name: " + contact.getFirst_name()
						+ " " + contact.getLast_name());
				iLogger.info("Role ID:" + role_id);
				iLogger.info("Role Name: " + role_name);
				iLogger.info("Account ID: " + account_id);
				iLogger.info("Last Login Date: " + last_login_date);
				iLogger.info("smsService: " + isSMS);
				iLogger.info("mapService : " + isMap);
			
			iLogger.info("isGeneratedPassword:"+sysGeneratedPassword);
			iLogger.info("isGeneratedPassword:"+sysGeneratedPassword);

			} finally {
				if (session.getTransaction().isActive()) {
					session.getTransaction().commit();
				}
				if (session.isOpen()) {
					session.flush();
					session.close();
				}
			}
			// long endTime = System.currentTimeMillis();
			// infoLogger.info("Total execution time "+
			// String.valueOf(endTime - startTime) + "(ms)");
			return this;
		}

	}

	public void setActivityLog(String login_id)

	{

		/*
		 * Timestamp current_date = new java.sql.Timestamp( (new
		 * java.util.Date()).getTime());


		// DefectId: DF20131015 - Rajani Nagaraju - User Login Timestamp should
		// be stored in GMT into Database
		//DefectId: 20140918 @Suprava 
		Properties prop = new Properties();
		String loginId = null;
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream(
					"remote/wise/resource/properties/configuration.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loginId = prop.getProperty("LoginId");
		//DefectId: 20140918 End
		Date currDate = new Date();
		SimpleDateFormat dateFrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateStr = dateFrmt.format(currDate);
		Timestamp current_date = Timestamp.valueOf(dateStr);

		ContactActivityLogEntity contactActivityLog = new ContactActivityLogEntity();
		ContactEntity contact = getContactEntity(login_id);
		//DefectId: 20140918
		if(!contact.getContact_id().equalsIgnoreCase(loginId) && contact!=null)
		{
		contactActivityLog.setContact_id(contact);
		contactActivityLog.setLogin_date(current_date);
		contactActivityLog.save();
		}*/
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;


		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		ContactEntity contact=null;
		Timestamp current_date=null;

		try
		{
			if(login_id==null || login_id.equalsIgnoreCase("deve0011"))
				return;

			Date currDate = new Date();
			SimpleDateFormat dateFrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
			String dateStr = dateFrmt.format(currDate);
			current_date = Timestamp.valueOf(dateStr);

			Query contactQ = session.createQuery(" from ContactEntity where contact_id='"+login_id+"'");
			Iterator contactItr = contactQ.list().iterator();
			if(contactItr.hasNext())
			{
				contact = (ContactEntity)contactItr.next();
				ContactActivityLogEntity contactActivityLog = new ContactActivityLogEntity();
				contactActivityLog.setContact_id(contact);
				contactActivityLog.setLogin_date(current_date);
				iLogger.info("ContactActivityLog: Contact"+contact+", CurrentDate:"+current_date);
				session.save(contactActivityLog);
				iLogger.info("ContactActivityLog: Contact"+contact+", CurrentDate:"+current_date+"ID: "+contactActivityLog.getContact_activitylog_id());
			}

		}
		catch(Exception e)
		{
			//fatalError.error(e.getMessage());
			fLogger.error("ContactActivityLog:"+contact+":"+current_date+":"+" :Exception :"+e);

			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			String err = result.toString();
			fLogger.error("ContactActivityLog:"+contact+":"+current_date+":"+" :Exception trace: "+err);
			try 
			{
				printWriter.close();
				result.close();
			} 

			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (session.isOpen()) 
			{
				session.clear();
			}
			if (session.isOpen()) 
			{
				session.close();
			}
		}

		finally
		{
			try
			{
				if (session.getTransaction().isActive()) 
				{
					session.getTransaction().commit();
				}
			}
			catch(Exception e)
			{
				fLogger.error("ContactActivityLog:"+contact+":"+current_date+":"+" :Exception :"+e);

				Writer result = new StringWriter();
				PrintWriter printWriter = new PrintWriter(result);
				e.printStackTrace(printWriter);
				String err = result.toString();
				fLogger.error("ContactActivityLog:"+contact+":"+current_date+":"+" :Exception trace: "+err);
				try 
				{
					printWriter.close();
					result.close();
				} 

				catch (IOException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				if (session.isOpen()) 
				{
					session.clear();
				}
				if (session.isOpen()) 
				{
					session.close();
				}
			}

			if (session.isOpen()) 
			{
				session.flush();
				session.close();
			}
		}

	}

	/**
	 * This method will return list of user Details for given user_Id,List of
	 * tenancy_id
	 * 
	 * @param user_Id
	 *            :Login_id user_Id
	 * @param tenancyId
	 *            :tenancyId
	 * @return userDetails:Return User Details for given list of tenancyId
	 * @throws CustomFault
	 *             :custom exception is thrown when the user_Id is not specified
	 *             or invalid,tenancy_id is invalid when specified
	 */
	/*
	 * public List<UserDetailsBO> getUserDetails(String user_Id,List<Integer>
	 * tenancyId ) throws CustomFault { long startTime
	 * =System.currentTimeMillis(); List<UserDetailsBO> userDetails = new
	 * LinkedList<UserDetailsBO>(); int flag=0; if(user_Id!=null) { flag=1; }
	 * DomainServiceImpl domainService = new DomainServiceImpl();
	 * 
	 * 
	 * if( (tenancyId==null || tenancyId.isEmpty()))
	 * {businessError.error("Please pass a valid tenancyId"); throw new
	 * CustomFault("Please pass a valid tenancyId");
	 * 
	 * } ListToStringConversion conversionObj = new ListToStringConversion();
	 * String tenancyIdStringList =
	 * conversionObj.getIntegerListString(tenancyId).toString();
	 * 
	 * Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	 * session.beginTransaction();
	 * 
	 * 
	 * try{ if(flag==1) { ContactEntity contactEntity =
	 * domainService.getContactDetails(user_Id);
	 * if(contactEntity.getContact_id()==null)
	 * 
	 * {businessError.error("Please pass a valid UserId"); throw new
	 * CustomFault("Please pass a valid UserId"); } if(! (session.isOpen() )) {
	 * session = HibernateUtil.getSessionFactory().openSession();
	 * session.getTransaction().begin(); } String queryString1=
	 * " select group_id,group_name from CustomAssetGroupEntity where tenancy_id  in ('"
	 * +tenancyIdStringList+"')"; Iterator
	 * list=session.createQuery(queryString1).list().iterator(); Object
	 * result1[]=null; List<String> asset_group_name=new LinkedList<String>() ;
	 * List<Integer> asset_group_id =new LinkedList<Integer>();
	 * 
	 *  if(list.hasNext()){
	 * 
	 * while(list.hasNext()) { result1=(Object[])list.next(); int
	 * group_id=(Integer)result1[0]; asset_group_id.add(group_id); String
	 * group_name =(String)result1[1]; asset_group_name.add(group_name); }
	 * 
	 * } // UserDetailsBO userBO1 = new UserDetailsBO(); //
	 * userBO1.setAsset_group_id(asset_group_id); //
	 * userBO1.setAsset_group_name(asset_group_name); //
	 * userDetails.add(userBO1); String queryString=
	 * "select a.contact_id,a.first_name,a.last_name,a.is_tenancy_admin,e.role_id,e.role_name,a.primary_mobile_number,a.primary_email_id, "
	 * +
	 * " a.countryCode ,a.language,a.timezone from ContactEntity a, AccountTenancyMapping b,AccountContactMapping c,RoleEntity e where "
	 * +
	 * "a.contact_id=c.contact_id and c.account_id=b.account_id and a.role=e.role_id and b.tenancy_id in ('"
	 * +tenancyIdStringList+"') and a.contact_id ='"+user_Id+
	 * "' and a.active_status=1";
	 * 
	 * Iterator l=session.createQuery(queryString).list().iterator(); Object
	 * result[]=null; while(l.hasNext()) {result=(Object[])l.next();
	 * 
	 * UserDetailsBO userBO = new UserDetailsBO();
	 * 
	 * userBO.setLoginId(result[0].toString());
	 * userBO.setFirst_name(result[1].toString());
	 * userBO.setLast_name(result[2].toString());
	 * userBO.setIs_tenancy_admin((Integer)result[3]);
	 * userBO.setRole_id((Integer)result[4]);
	 * userBO.setRole_name((String)result[5]);
	 * userBO.setPrimaryMobileNumber((String)result[6]);
	 * userBO.setPrimaryEmailId((String)result[7]);
	 * userBO.setCountryCode(result[8].toString());
	 * userBO.setLanguage(result[9].toString());
	 * userBO.setTimeZone(result[10].toString()); if(asset_group_id.size() !=0){
	 * userBO.setAsset_group_id(asset_group_id);
	 * userBO.setAsset_group_name(asset_group_name); } userDetails.add(userBO);
	 * 
	 * } } else { String queryString1=
	 * " select group_id,group_name from CustomAssetGroupEntity where tenancy_id  in ('"
	 * +tenancyIdStringList+"')"; Iterator
	 * list=session.createQuery(queryString1).list().iterator(); Object
	 * result1[]=null; List<String> asset_group_name=new LinkedList<String>() ;
	 * List<Integer> asset_group_id =new LinkedList<Integer>();
	 * if(list.hasNext()){ 
	 * while(list.hasNext()) { result1=(Object[])list.next(); int
	 * group_id=(Integer)result1[0]; asset_group_id.add(group_id); String
	 * group_name =(String)result1[1]; asset_group_name.add(group_name); } } //
	 * UserDetailsBO userBO1 = new UserDetailsBO(); //
	 * userBO1.setAsset_group_id(asset_group_id); //
	 * userBO1.setAsset_group_name(asset_group_name); //
	 * userDetails.add(userBO1); String queryString=
	 * "select a.contact_id,a.first_name,a.last_name,a.is_tenancy_admin,e.role_id,e.role_name,a.primary_mobile_number,a.primary_email_id, "
	 * +
	 * " a.countryCode ,a.language,a.timezone from ContactEntity a, AccountTenancyMapping b,AccountContactMapping c,RoleEntity e where "
	 * +
	 * "a.contact_id=c.contact_id and c.account_id=b.account_id and a.role=e.role_id and b.tenancy_id in ('"
	 * +tenancyIdStringList+"') and a.active_status=1";
	 * 
	 * Iterator l=session.createQuery(queryString).list().iterator(); Object
	 * result[]=null; while(l.hasNext()) {result=(Object[])l.next();
	 * 
	 * UserDetailsBO userBO = new UserDetailsBO();
	 * 
	 * userBO.setLoginId(result[0].toString());
	 * userBO.setFirst_name(result[1].toString());
	 * userBO.setLast_name(result[2].toString());
	 * userBO.setIs_tenancy_admin((Integer)result[3]);
	 * userBO.setRole_id((Integer)result[4]);
	 * userBO.setRole_name((String)result[5]);
	 * userBO.setPrimaryMobileNumber((String)result[6]);
	 * userBO.setPrimaryEmailId((String)result[7]);
	 * userBO.setCountryCode(result[8].toString());
	 * userBO.setLanguage(result[9].toString());
	 * userBO.setTimeZone(result[10].toString()); if(asset_group_id.size() !=0){
	 * userBO.setAsset_group_id(asset_group_id);
	 * userBO.setAsset_group_name(asset_group_name); } userDetails.add(userBO);
	 * 
	 * } } long endTime =System.currentTimeMillis();
	 * infoLogger.info("service time"+(endTime-startTime));
	 * 
	 * 
	 * }catch(Exception e){ 
	 * fatalError.fatal("Hello this is an Fatal Error. Need immediate Action"
	 * +e.getMessage());
	 * 
	 * }
	 * 
	 * finally { if(session.getTransaction().isActive()) {
	 * session.getTransaction().commit(); }
	 * 
	 * if(session.isOpen()) { session.flush(); session.close(); }
	 * 
	 * } return userDetails;
	 * 
	 * }
	 */

	//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
	public List<UserDetailsBO> getUserDetails(String user_Id,
			List<Integer> tenancyId, int AssetGroupId) throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		//long startTime = System.currentTimeMillis();
		List<UserDetailsBO> userDetails = new LinkedList<UserDetailsBO>();
		int flag = 0;
		//DF20180614 -ma369757
		//adding split because in settings->user->user_id is being used to handle fname and mobile number search
		if (user_Id != null && !(user_Id.contains("|")) ) {
			flag = 1;
		}
		DomainServiceImpl domainService = new DomainServiceImpl();

		if ((tenancyId == null || tenancyId.isEmpty())) {
			//businessError.error("Please pass a valid tenancyId");
			throw new CustomFault("Please pass a valid tenancyId");

		}
		ListToStringConversion conversionObj = new ListToStringConversion();
		String tenancyIdStringList = conversionObj.getIntegerListString(
				tenancyId).toString();

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		tenancyAdminCount = 0;
		try {
			// get Client Details
			Properties prop = new Properties();
			String clientName = null;

			prop.load(getClass().getClassLoader().getResourceAsStream(
					"remote/wise/resource/properties/configuration.properties"));
			clientName = prop.getProperty("ClientName");

			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj
					.getClientEntity(clientName);
			// END of get Client Details
			if (!(session.isOpen())) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}
			//If user_Id is specified, reutrn the user details and the custom asset group details of the same
			if (flag == 1) {
				ContactEntity contactEntity = domainService
						.getContactDetails(user_Id);
				if (contactEntity.getContact_id() == null)

				{
					//	businessError.error("Please pass a valid UserId");
					throw new CustomFault("Please pass a valid UserId");
				}
				if (!(session.isOpen())) {
					session = HibernateUtil.getSessionFactory().openSession();
					session.getTransaction().begin();
				}

				List<String> asset_group_name = new LinkedList<String>();
				List<Integer> asset_group_id = new LinkedList<Integer>();
				String queryString1 = null;
				if (AssetGroupId == 0) {
					/*if (user_Id == null) {
						queryString1 = " select group_id,group_name from CustomAssetGroupEntity where active_status=1 and client_id="
								+ clientEntity.getClient_id()
								+ " and tenancy_id  in ('"
								+ tenancyIdStringList + "')";
					} else {
						queryString1 = " select a.group_id,a.group_name from CustomAssetGroupEntity a  , GroupUserMapping b "
								+ " where a.active_status=1 and a.client_id="
								+ clientEntity.getClient_id()
								+ " and b.contact_id = '"
								+ user_Id
								+ "' "
								+ " and a.group_id = b.group_id  ";
					}*/
					//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
					//user_id cannot be null here. Since this is inside the IF cluase of flag=1(which means user_id!=null)
					queryString1	 = " select a.group_id,a.group_name from CustomAssetGroupEntity a  , GroupUserMapping b " +
							" where a.active_status=1 and a.client_id="+clientEntity.getClient_id()+" and b.contact_id = '" + user_Id + "' "+  
							" and a.group_id = b.group_id  " ;
				}
				else
				{
					//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
					//This is inside the if clause where falg=1, which means user_id!=null. In such case, group details for the given group and given user has to be returned
					queryString1	 = " select a.group_id,a.group_name from CustomAssetGroupEntity a  , GroupUserMapping b " +
							" where a.active_status=1 and a.client_id="+clientEntity.getClient_id()+" and b.contact_id = '" + user_Id + "' "+  
							" and a.group_id = b.group_id and  a.group_id ="+AssetGroupId ;

				}
				Iterator list = session.createQuery(queryString1).list()
						.iterator();
				Object result2[] = null;


				while (list.hasNext()) 
				{
					result2 = (Object[]) list.next();
					int group_id = (Integer) result2[0];
					asset_group_id.add(group_id);
					String group_name = (String) result2[1];
					asset_group_name.add(group_name);
				}

				//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
				//Commenting the below code, as it is handled above

				/*else {
					queryString1 = " select group_id,group_name from CustomAssetGroupEntity where group_id ="
							+ AssetGroupId
							+ "  and active_status=1 and client_id="
							+ clientEntity.getClient_id()
							+ " and tenancy_id  in ('"
							+ tenancyIdStringList
							+ "')";
					Iterator list = session.createQuery(queryString1).list()
							.iterator();
					Object result1[] = null;

					if (list.hasNext()) {

						while (list.hasNext()) {
							result1 = (Object[]) list.next();
							int group_id = (Integer) result1[0];
							asset_group_id.add(group_id);
							String group_name = (String) result1[1];
							asset_group_name.add(group_name);
						}
					}
				}*/

				// UserDetailsBO userBO1 = new UserDetailsBO();
				// userBO1.setAsset_group_id(asset_group_id);
				// userBO1.setAsset_group_name(asset_group_name);
				// userDetails.add(userBO1);


				//Return the contact details of the specified user
				//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
				/*String queryString = "select a.contact_id,a.first_name,a.last_name,a.is_tenancy_admin,e.role_id,e.role_name,a.primary_mobile_number,a.primary_email_id, "
						+ " a.countryCode ,a.language,a.timezone from ContactEntity a, AccountTenancyMapping b,AccountContactMapping c,RoleEntity e where "
						+ "a.contact_id=c.contact_id and c.account_id=b.account_id and a.role=e.role_id and b.tenancy_id in ('"
						+ tenancyIdStringList
						+ "') and a.contact_id ='"
						+ user_Id
						+ "' and a.active_status=1 and e.client_id="
						+ clientEntity.getClient_id() + "";*/
				String queryString = "select a.contact_id,a.first_name,a.last_name,a.is_tenancy_admin,e.role_id,e.role_name,a.primary_mobile_number,a.primary_email_id, "
						+ " a.countryCode ,a.language,a.timezone from ContactEntity a, RoleEntity e where "
						+ " a.role=e.role_id and a.contact_id ='"+ user_Id + "' and a.active_status=1 and a.client_id="+clientEntity.getClient_id()+"";

				Iterator l = session.createQuery(queryString).list().iterator();
				Object result[] = null;
				while (l.hasNext()) 
				{
					result = (Object[]) l.next();

					UserDetailsBO userBO = new UserDetailsBO();
					if(result[0]!=null){
						userBO.setLoginId(result[0].toString());
					}
					if(result[1]!=null){
						userBO.setFirst_name(result[1].toString());
					}					
					if(result[2]!=null){
						userBO.setLast_name(result[2].toString());
					}
					if(result[3]!=null){
						userBO.setIs_tenancy_admin((Integer) result[3]);
					}

					if (result[3] != null) {
						if ((Integer) result[3] == 1) {
							++tenancyAdminCount;
						}
					}
					if(result[4]!=null){
						userBO.setRole_id((Integer) result[4]);
					}
					if(result[5]!=null){
						userBO.setRole_name((String) result[5]);
					}					
					if (result[6] != null){
						userBO.setPrimaryMobileNumber((String) result[6]);
					}						
					if (result[7] != null){
						userBO.setPrimaryEmailId((String) result[7]);
					}						
					if (result[8] != null){
						userBO.setCountryCode(result[8].toString());
					}						
					if (result[9] != null){
						userBO.setLanguage(result[9].toString());
					}						
					if (result[10] != null){
						userBO.setTimeZone(result[10].toString());
					}

					if (asset_group_id.size() != 0) {
						userBO.setAsset_group_id(asset_group_id);
						userBO.setAsset_group_name(asset_group_name);
					}
					userDetails.add(userBO);

				}
			} 


			//If userId is not specified return the list of all users under the given tenancy or the List of users under the given assetGroup, if specified
			else 
			{
				//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
				//Get the List of tenancy Ids including the duplicate tenancy
				List<TenancyEntity> totalTenancyList = new LinkedList<TenancyEntity>();
				List<Integer> tenancyIdList = new LinkedList<Integer>();
				int mobUserSearch=0;
				Query tenancyListQuery = session.createQuery(" from TenancyEntity a where a.tenancyCode in ( select b.tenancyCode from TenancyEntity b " +
						" where b.tenancy_id in ("+tenancyIdStringList+") )");
				Iterator tenancyListItr = tenancyListQuery.list().iterator();
				while(tenancyListItr.hasNext())
				{
					TenancyEntity tenancy = (TenancyEntity)tenancyListItr.next();
					totalTenancyList.add(tenancy);
					tenancyIdList.add(tenancy.getTenancy_id());
				}


				String contactQuery = null;
				if(AssetGroupId==0)
				{
					//DF20180614 -ma369757
					//user id is being used for firstname and mobile number search userid="searchBy|value"
					if(user_Id != null && user_Id.split("\\|").length>1)
					{
						mobUserSearch=1;
						if(user_Id.split("\\|")[0].equalsIgnoreCase("name")){
							
							//DF20190307 :mani: Modifying the query, introducing tenancy bridge and account tenancy join to list the users under a specific parent
							/*contactQuery = " select a.contact_id, a.first_name, a.last_name, a.is_tenancy_admin, b.role_id, b.role_name," +
									" a.primary_mobile_number, a.primary_email_id, a.countryCode, a.language,a.timezone , " +
									" CAST(GROUP_CONCAT(cust.group_id) As string ) as groupId, CAST(GROUP_CONCAT(cust.group_name) As string ) as groupName," +
									" CAST(GROUP_CONCAT(cust.active_status) As string ) as activeStatus " +
									" from GroupUserMapping e RIGHT OUTER JOIN e.group_id cust " +
									" RIGHT OUTER JOIN e.contact_id a, RoleEntity b, AccountContactMapping c, AccountTenancyMapping d " +
									" where a.role = b.role_id and a.contact_id=c.contact_id and c.account_id= d.account_id " +
									" and a.active_status=1 and (a.first_name like \'%"+user_Id.split("\\|")[1]+"%\' or a.last_name like \'%"+user_Id.split("\\|")[1]+"%\') and a.client_id='"+clientEntity.getClient_id()+"'" +
									" and d.tenancy_id in (:list) group by a.contact_id ";*/
							contactQuery="select a.contact_id, a.first_name, a.last_name, a.is_tenancy_admin, b.role_id, b.role_name," +
									" a.primary_mobile_number, a.primary_email_id, a.countryCode, a.language,a.timezone , " +
									" CAST(GROUP_CONCAT(cust.group_id) As string ) as groupId, CAST(GROUP_CONCAT(cust.group_name) As string ) as groupName," +
									" CAST(GROUP_CONCAT(cust.active_status) As string ) as activeStatus,d.tenancy_id " +
									" from GroupUserMapping e RIGHT OUTER JOIN e.group_id cust " +
									" RIGHT OUTER JOIN e.contact_id a, RoleEntity b, AccountContactMapping c, AccountTenancyMapping d, TenancyEntity ten, TenancyBridgeEntity tb " +
									" where a.role = b.role_id and a.contact_id=c.contact_id and c.account_id= d.account_id and d.tenancy_id=ten.tenancy_id and ten.tenancy_id=tb.childId " +
									" and a.active_status=1 and (a.first_name like \'%"+user_Id.split("\\|")[1]+"%\' or a.last_name like \'%"+user_Id.split("\\|")[1]+"%\') and a.client_id='"+clientEntity.getClient_id()+"'" +
									" and tb.parentId in (:list) group by a.contact_id";
							
						}
						if(user_Id.split("\\|")[0].equalsIgnoreCase("mobile")){
							//DF20190307 :mani: Modifying the query, introducing tenancy bridge and account tenancy join to list the users under a specific tenancy
							/*contactQuery = " select a.contact_id, a.first_name, a.last_name, a.is_tenancy_admin, b.role_id, b.role_name," +
									" a.primary_mobile_number, a.primary_email_id, a.countryCode, a.language,a.timezone , " +
									" CAST(GROUP_CONCAT(cust.group_id) As string ) as groupId, CAST(GROUP_CONCAT(cust.group_name) As string ) as groupName," +
									" CAST(GROUP_CONCAT(cust.active_status) As string ) as activeStatus " +
									" from GroupUserMapping e RIGHT OUTER JOIN e.group_id cust " +
									" RIGHT OUTER JOIN e.contact_id a, RoleEntity b, AccountContactMapping c, AccountTenancyMapping d " +
									" where a.role = b.role_id and a.contact_id=c.contact_id and c.account_id= d.account_id " +
									" and a.active_status=1 and a.primary_mobile_number like \'%"+user_Id.split("\\|")[1]+"%\' and a.client_id='"+clientEntity.getClient_id()+"'" +
									" and d.tenancy_id in (:list) group by a.contact_id ";*/
							contactQuery = " select a.contact_id, a.first_name, a.last_name, a.is_tenancy_admin, b.role_id, b.role_name," +
									" a.primary_mobile_number, a.primary_email_id, a.countryCode, a.language,a.timezone , " +
									" CAST(GROUP_CONCAT(cust.group_id) As string ) as groupId, CAST(GROUP_CONCAT(cust.group_name) As string ) as groupName," +
									" CAST(GROUP_CONCAT(cust.active_status) As string ) as activeStatus,d.tenancy_id " +
									" from GroupUserMapping e RIGHT OUTER JOIN e.group_id cust " +
									" RIGHT OUTER JOIN e.contact_id a, RoleEntity b, AccountContactMapping c, AccountTenancyMapping d ,TenancyEntity ten, TenancyBridgeEntity tb  " +
									" where a.role = b.role_id and a.contact_id=c.contact_id and c.account_id= d.account_id and d.tenancy_id=ten.tenancy_id and ten.tenancy_id=tb.childId " +
									" and a.active_status=1 and a.primary_mobile_number like \'%"+user_Id.split("\\|")[1]+"%\' and a.client_id='"+clientEntity.getClient_id()+"'" +
									" and tb.parentId in (:list) group by a.contact_id ";
							
						}
						//LL21 :Sai Divya :20250409 :SearchBYEmailId in UserTab.n
						if(user_Id.split("\\|")[0].equalsIgnoreCase("email")){
							contactQuery = " select a.contact_id, a.first_name, a.last_name, a.is_tenancy_admin, b.role_id, b.role_name," +
									" a.primary_mobile_number, a.primary_email_id, a.countryCode, a.language,a.timezone , " +
									" CAST(GROUP_CONCAT(cust.group_id) As string ) as groupId, CAST(GROUP_CONCAT(cust.group_name) As string ) as groupName," +
									" CAST(GROUP_CONCAT(cust.active_status) As string ) as activeStatus,d.tenancy_id " +
									" from GroupUserMapping e RIGHT OUTER JOIN e.group_id cust " +
									" RIGHT OUTER JOIN e.contact_id a, RoleEntity b, AccountContactMapping c, AccountTenancyMapping d ,TenancyEntity ten, TenancyBridgeEntity tb  " +
									" where a.role = b.role_id and a.contact_id=c.contact_id and c.account_id= d.account_id and d.tenancy_id=ten.tenancy_id and ten.tenancy_id=tb.childId " +
									" and a.active_status=1 and a.primary_email_id like \'%"+user_Id.split("\\|")[1]+"%\' and a.client_id='"+clientEntity.getClient_id()+"'" +
									" and tb.parentId in (:list) group by a.contact_id ";
							
						}
					}
					else
					{
						contactQuery = " select a.contact_id, a.first_name, a.last_name, a.is_tenancy_admin, b.role_id, b.role_name," +
								" a.primary_mobile_number, a.primary_email_id, a.countryCode, a.language,a.timezone , " +
								" CAST(GROUP_CONCAT(cust.group_id) As string ) as groupId, CAST(GROUP_CONCAT(cust.group_name) As string ) as groupName," +
								" CAST(GROUP_CONCAT(cust.active_status) As string ) as activeStatus,d.tenancy_id " +
								" from GroupUserMapping e RIGHT OUTER JOIN e.group_id cust " +
								" RIGHT OUTER JOIN e.contact_id a, RoleEntity b, AccountContactMapping c, AccountTenancyMapping d " +
								" where a.role = b.role_id and a.contact_id=c.contact_id and c.account_id= d.account_id " +
								" and a.active_status=1 and a.client_id='"+clientEntity.getClient_id()+"'" +
								" and d.tenancy_id in (:list) group by a.contact_id ";
					}
					
				}

				else
				{
					//DF20180614 -ma369757
					//user id is being used for firstname and mobile number search userid="searchBy|value"
					if(user_Id != null && user_Id.split("\\|").length>1)
					{
						mobUserSearch=1;
					if(user_Id.split("\\|")[0].equalsIgnoreCase("name")){
						
						//DF20190307 :mani: Modifying the query, introducing tenancy bridge and account tenancy join to list the users under a specific tenancy
						/*contactQuery = " select a.contact_id, a.first_name, a.last_name, a.is_tenancy_admin, b.role_id, b.role_name," +
								" a.primary_mobile_number, a.primary_email_id, a.countryCode, a.language,a.timezone , " +
								" CAST(GROUP_CONCAT(cust.group_id) As string ) as groupId, CAST(GROUP_CONCAT(cust.group_name) As string ) as groupName " +
								" CAST(GROUP_CONCAT(cust.active_status) As string ) as activeStatus " +
								" from GroupUserMapping e RIGHT OUTER JOIN e.group_id cust " +
								" RIGHT OUTER JOIN e.contact_id a, RoleEntity b, AccountContactMapping c, AccountTenancyMapping d " +
								" where a.role = b.role_id and a.contact_id=c.contact_id and c.account_id= d.account_id " +
								" and a.active_status=1 and (a.first_name like \'%"+user_Id.split("\\|")[1]+"%\' or a.last_name like \'%"+user_Id.split("\\|")[1]+"%\') and a.client_id='"+clientEntity.getClient_id()+"'" +
								" and cust.group_id ="+AssetGroupId +
								" and d.tenancy_id in (:list) group by a.contact_id ";*/
						contactQuery = " select a.contact_id, a.first_name, a.last_name, a.is_tenancy_admin, b.role_id, b.role_name," +
								" a.primary_mobile_number, a.primary_email_id, a.countryCode, a.language,a.timezone , " +
								" CAST(GROUP_CONCAT(cust.group_id) As string ) as groupId, CAST(GROUP_CONCAT(cust.group_name) As string ) as groupName " +
								" CAST(GROUP_CONCAT(cust.active_status) As string ) as activeStatus,d.tenancy_id " +
								" from GroupUserMapping e RIGHT OUTER JOIN e.group_id cust " +
								" RIGHT OUTER JOIN e.contact_id a, RoleEntity b, AccountContactMapping c, AccountTenancyMapping d , TenancyEntity ten, TenancyBridgeEntity tb " +
								" where a.role = b.role_id and a.contact_id=c.contact_id and c.account_id= d.account_id and d.tenancy_id=ten.tenancy_id and ten.tenancy_id=tb.childId " +
								" and a.active_status=1 and (a.first_name like \'%"+user_Id.split("\\|")[1]+"%\' or a.last_name like \'%"+user_Id.split("\\|")[1]+"%\') and a.client_id='"+clientEntity.getClient_id()+"'" +
								" and cust.group_id ="+AssetGroupId +
								" and tb.parentId in (:list) group by a.contact_id ";
					}
					if(user_Id.split("\\|")[0].equalsIgnoreCase("mobile")){
						
						//DF20190307 :mani: Modifying the query, introducing tenancy bridge and account tenancy join to list the users under a specific tenancy
						/*contactQuery = " select a.contact_id, a.first_name, a.last_name, a.is_tenancy_admin, b.role_id, b.role_name," +
								" a.primary_mobile_number, a.primary_email_id, a.countryCode, a.language,a.timezone , " +
								" CAST(GROUP_CONCAT(cust.group_id) As string ) as groupId, CAST(GROUP_CONCAT(cust.group_name) As string ) as groupName " +
								" CAST(GROUP_CONCAT(cust.active_status) As string ) as activeStatus " +
								" from GroupUserMapping e RIGHT OUTER JOIN e.group_id cust " +
								" RIGHT OUTER JOIN e.contact_id a, RoleEntity b, AccountContactMapping c, AccountTenancyMapping d " +
								" where a.role = b.role_id and a.contact_id=c.contact_id and c.account_id= d.account_id " +
								" and a.active_status=1 and a.primary_mobile_number like \'%"+user_Id.split("\\|")[1]+"%\' and a.client_id='"+clientEntity.getClient_id()+"'" +
								" and cust.group_id ="+AssetGroupId +
								" and d.tenancy_id in (:list) group by a.contact_id ";*/
						contactQuery = " select a.contact_id, a.first_name, a.last_name, a.is_tenancy_admin, b.role_id, b.role_name," +
								" a.primary_mobile_number, a.primary_email_id, a.countryCode, a.language,a.timezone , " +
								" CAST(GROUP_CONCAT(cust.group_id) As string ) as groupId, CAST(GROUP_CONCAT(cust.group_name) As string ) as groupName " +
								" CAST(GROUP_CONCAT(cust.active_status) As string ) as activeStatus,d.tenancy_id " +
								" from GroupUserMapping e RIGHT OUTER JOIN e.group_id cust " +
								" RIGHT OUTER JOIN e.contact_id a, RoleEntity b, AccountContactMapping c, AccountTenancyMapping d, TenancyEntity ten, TenancyBridgeEntity tb " +
								" where a.role = b.role_id and a.contact_id=c.contact_id and c.account_id= d.account_id and d.tenancy_id=ten.tenancy_id and ten.tenancy_id=tb.childId  " +
								" and a.active_status=1 and a.primary_mobile_number like \'%"+user_Id.split("\\|")[1]+"%\' and a.client_id='"+clientEntity.getClient_id()+"'" +
								" and cust.group_id ="+AssetGroupId +
								" and tb.parentId in (:list) group by a.contact_id ";
						
						
					}
					//LL21 :Sai Divya :20250409 :SearchBYEmailId in UserTab.n
						if (user_Id.split("\\|")[0].equalsIgnoreCase("email")) {
							contactQuery = " select a.contact_id, a.first_name, a.last_name, a.is_tenancy_admin, b.role_id, b.role_name,"
									+ " a.primary_mobile_number, a.primary_email_id, a.countryCode, a.language,a.timezone , "
									+ " CAST(GROUP_CONCAT(cust.group_id) As string ) as groupId, CAST(GROUP_CONCAT(cust.group_name) As string ) as groupName "
									+ " CAST(GROUP_CONCAT(cust.active_status) As string ) as activeStatus,d.tenancy_id "
									+ " from GroupUserMapping e RIGHT OUTER JOIN e.group_id cust "
									+ " RIGHT OUTER JOIN e.contact_id a, RoleEntity b, AccountContactMapping c, AccountTenancyMapping d, TenancyEntity ten, TenancyBridgeEntity tb "
									+ " where a.role = b.role_id and a.contact_id=c.contact_id and c.account_id= d.account_id and d.tenancy_id=ten.tenancy_id and ten.tenancy_id=tb.childId  "
									+ " and a.active_status=1 and a.primary_email_id like \'%"
									+ user_Id.split("\\|")[1] + "%\' and a.client_id='" + clientEntity.getClient_id()
									+ "'" + " and cust.group_id =" + AssetGroupId
									+ " and tb.parentId in (:list) group by a.contact_id ";

						}
					
					}
					else{
					contactQuery = " select a.contact_id, a.first_name, a.last_name, a.is_tenancy_admin, b.role_id, b.role_name," +
							" a.primary_mobile_number, a.primary_email_id, a.countryCode, a.language,a.timezone , " +
							" CAST(GROUP_CONCAT(cust.group_id) As string ) as groupId, CAST(GROUP_CONCAT(cust.group_name) As string ) as groupName " +
							" CAST(GROUP_CONCAT(cust.active_status) As string ) as activeStatus,d.tenancy_id " +
							" from GroupUserMapping e RIGHT OUTER JOIN e.group_id cust " +
							" RIGHT OUTER JOIN e.contact_id a, RoleEntity b, AccountContactMapping c, AccountTenancyMapping d " +
							" where a.role = b.role_id and a.contact_id=c.contact_id and c.account_id= d.account_id " +
							" and a.active_status=1 and a.client_id='"+clientEntity.getClient_id()+"'" +
							" and cust.group_id ="+AssetGroupId +
							" and d.tenancy_id in (:list) group by a.contact_id ";
				}
				}
				iLogger.info(" Userdetails service userDetailsQuery : "+contactQuery);
				//DF20190307 :mani: Modifying the query, introducing tenancy bridge and account tenancy join to list the users under a specific tenancy
				Query userDetailsQuery = null;
				if(mobUserSearch==1)
					userDetailsQuery=session.createQuery(contactQuery).setParameterList("list",tenancyIdList);
				else
					userDetailsQuery=session.createQuery(contactQuery).setParameterList("list", totalTenancyList);
				Iterator userDetailsItr = userDetailsQuery.list().iterator();
				Object result1[] = null;
				while(userDetailsItr.hasNext())
				{

					result1 = (Object[])userDetailsItr.next();

					UserDetailsBO userBO = new UserDetailsBO();
					List<String> groupIdList = new LinkedList<String>();
					List<Integer> groupIdIntegerList = new LinkedList<Integer>();
					List<String> groupNameList = new LinkedList<String>();
					List<String> groupNameListTemp = new LinkedList<String>();
					List<String> groupStatus = new LinkedList<String>();
					List<String> groupStatusTemp = new LinkedList<String>();

					userBO.setLoginId(result1[0].toString());

					if(result1[1] != null)
						userBO.setFirst_name(result1[1].toString());

					if(result1[2] != null)
						userBO.setLast_name(result1[2].toString());

					if(result1[3]!=null)
					{
						userBO.setIs_tenancy_admin((Integer) result1[3]);
						if((Integer) result1[3]==1)
							++tenancyAdminCount; 
					}

					if(result1[4]!=null)
						userBO.setRole_id((Integer) result1[4]);

					if(result1[5]!=null)
						userBO.setRole_name((String) result1[5]);

					if(result1[6]!=null)
						userBO.setPrimaryMobileNumber((String) result1[6]);

					if( result1[7]!=null)
						userBO.setPrimaryEmailId((String) result1[7]);

					if(result1[8]!=null)
						userBO.setCountryCode(result1[8].toString());

					if(result1[9]!=null)
						userBO.setLanguage(result1[9].toString());

					if(result1[10]!=null)
						userBO.setTimeZone(result1[10].toString());

					if(result1[11]!=null)
					{
						groupIdList = Arrays.asList(result1[11].toString().split(","));
						for(String s : groupIdList) groupIdIntegerList.add(Integer.valueOf(s));
					}

					if(result1[12] !=null){
						groupNameList = Arrays.asList(result1[12].toString().split(","));
						for(String s : groupNameList) groupNameListTemp.add(s);
					}


					if(result1[13] !=null){
						groupStatus = Arrays.asList(result1[13].toString().split(","));
						for(String s : groupStatus) groupStatusTemp.add(s);
					}
					
					if(result1[14] !=null){
						userBO.setTenancyId(((TenancyEntity)result1[14]).getTenancy_id());
					}

					//Get only the Active Machine Groups - Same cannot be added in the where clause of the Query since it will have a tight binding with machine group,
					//which returns only those users that are tied to Machine Group
					for(int i=0; i<groupStatusTemp.size(); i++)
					{
						if(groupStatusTemp.get(i).equalsIgnoreCase("0"))
						{							
							groupStatusTemp.remove(i);
							//							Keerthi : 30/01/14 :
							if(groupNameListTemp.size()>=i+1){
								groupNameListTemp.remove(i);
							}
							if(groupIdIntegerList.size()>=i+1){
								groupIdIntegerList.remove(i);
							}

						}
					}

					userBO.setAsset_group_id(groupIdIntegerList);
					userBO.setAsset_group_name(groupNameListTemp);

					userDetails.add(userBO);
				}


				/*

				String queryString1 = " select group_id,group_name from CustomAssetGroupEntity where active_status=1 and client_id="
						+ clientEntity.getClient_id()
						+ " and tenancy_id  in ('" + tenancyIdStringList + "')";
				Iterator list = session.createQuery(queryString1).list()
						.iterator();
				Object result1[] = null;
				List<String> asset_group_name = new LinkedList<String>();
				List<Integer> asset_group_id = new LinkedList<Integer>();
				if (list.hasNext()) {
					while (list.hasNext()) {
						result1 = (Object[]) list.next();
						int group_id = (Integer) result1[0];
						asset_group_id.add(group_id);
						String group_name = (String) result1[1];
						asset_group_name.add(group_name);
					}
				}
				// UserDetailsBO userBO1 = new UserDetailsBO();
				// userBO1.setAsset_group_id(asset_group_id);
				// userBO1.setAsset_group_name(asset_group_name);
				// userDetails.add(userBO1);
				tenancyAdminCount = 0;
				String queryString = "select a.contact_id,a.first_name,a.last_name,a.is_tenancy_admin,e.role_id,e.role_name,a.primary_mobile_number,a.primary_email_id, "
						+ " a.countryCode ,a.language,a.timezone from ContactEntity a, AccountTenancyMapping b,AccountContactMapping c,RoleEntity e where "
						+ "a.contact_id=c.contact_id and c.account_id=b.account_id and a.role=e.role_id and b.tenancy_id in ('"
						+ tenancyIdStringList
						+ "') and a.active_status=true and e.client_id="
						+ clientEntity.getClient_id() + "";

				Iterator l = session.createQuery(queryString).list().iterator();
				Object result[] = null;
				while (l.hasNext()) {
					result = (Object[]) l.next();

					UserDetailsBO userBO = new UserDetailsBO();

					userBO.setLoginId(result[0].toString());
					userBO.setFirst_name(result[1].toString());
					userBO.setLast_name(result[2].toString());
					userBO.setIs_tenancy_admin((Integer) result[3]);
					if (result[3] != null) {
						if ((Integer) result[3] == 1) {
							++tenancyAdminCount;
						}
					}
					userBO.setRole_id((Integer) result[4]);
					userBO.setRole_name((String) result[5]);
					if (result[6] != null)
						userBO.setPrimaryMobileNumber((String) result[6]);
					if (result[7] != null)
						userBO.setPrimaryEmailId((String) result[7]);
					if (result[8] != null)
						userBO.setCountryCode(result[8].toString());
					if (result[9] != null)
						userBO.setLanguage(result[9].toString());
					if (result[10] != null)
						userBO.setTimeZone(result[10].toString());
					if (asset_group_id.size() != 0) {
						userBO.setAsset_group_id(asset_group_id);
						userBO.setAsset_group_name(asset_group_name);
					}
					userDetails.add(userBO);

				}*/
			}
			// Keerthi : Defect ID : 1069 : Tenancy admin count : Deleting user
			Iterator iterateUserDetails = userDetails.iterator();
			UserDetailsBO userBO = null;
			while (iterateUserDetails.hasNext()) {
				userBO = (UserDetailsBO) iterateUserDetails.next();
				if (userBO != null) {
					// if(userBO.getIs_tenancy_admin()==1) {
					userBO.setTenancyAdminCount(tenancyAdminCount);
					// }
				}

			}


		} catch (Exception e) {
			e.printStackTrace();

			fLogger
			.fatal("Hello this is an Fatal Error. Need immediate Action"
					+ e.getMessage());
			e.printStackTrace();

		}

		finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}
		//System.out.println("userDetails size : "+userDetails.size());
		return userDetails;

	}

	// ******************************************end of Get UserDetails for
	// given user_Id,List of tenancy_id***************************************


	// ******************************************Set UserDetails for given LoginId****************************************************
	/** DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
	 * This method will set user Details for given LoginId
	 * @param LoginId
	 * @param first_name
	 * @param last_name
	 * @param role_id
	 * @param role_name
	 * @param primaryMobileNumber
	 * @param Is_tenancy_admin
	 * @param CountryCode
	 * @param tenancy_id
	 * @param asset_group_id
	 * @param asset_group_name
	 * @param language
	 * @param timeZone
	 * @param primaryEmailId
	 * @return SUCCESS:Return the status String as either Success/Failure.
	 * @throws CustomFault custom exception is thrown when the user login_id,tenancy_id,role_id is not specified or invalid
	 * @throws IOException 
	 */
	public String setUserDetails(String LoginId, String first_name, String last_name, int role_id, String role_name,
			String primaryMobileNumber, int Is_tenancy_admin, String CountryCode, int tenancy_id, 
			List<Integer> asset_group_id, List<String> asset_group_name, String language, String timeZone,
			String primaryEmailId) throws CustomFault, IOException  
			{


		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		DomainServiceImpl domainService = new DomainServiceImpl();
		String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

		iLogger.info("Inside setUserDetails ****  "+LoginId);

		int exists = 0;
		iLogger.info(tenancy_id);
		TenancyEntity tenancyEntity = domainService.getTenancyDetails(tenancy_id);
		if (tenancyEntity == null) 
		{
			throw new CustomFault("Invalid Tenancy_ID");
		}

		RoleEntity roleEntity = domainService.getRoleDetails(role_id);
		iLogger.info(role_id);
		if (roleEntity == null) 
		{
			throw new CustomFault("Invalid Role_id");
		}
		
		//DF20190102-KO369761-Sending edited user id to BO Class for logging edited user data.
		String editedByUserId = LoginId;
		if(LoginId!=null && LoginId.split("\\|").length > 1){
			editedByUserId = LoginId.split("\\|")[1];
			LoginId = LoginId.split("\\|")[0];
		}
		
		//DF20181109 :MANI: checking role hierachy while creating a user. ex : Dealer should not create a user as JCB Admin
		if(roleEntity!=null && tenancyEntity!=null){
			String rolName=roleEntity.getRole_name();
			iLogger.info(rolName);
			
			String tenancyType=tenancyEntity.getTenancy_type_id().getTenancy_type_name();
			iLogger.info(tenancyType);
			if((tenancyType.equalsIgnoreCase("Global"))||(tenancyType.equalsIgnoreCase("Regional")))
			{
				if(!(rolName.equalsIgnoreCase("JCB Admin") || rolName.equalsIgnoreCase("Customer Care") || rolName.equalsIgnoreCase("JCB HO") ||
						rolName.equalsIgnoreCase("Super Admin")))
				{
					throw new CustomFault("Invalid request,role id mismatch while user creation.");
				}
			}
			//DF20181115 :: As discussed, Global and Regional can create the above roles.
			/*if(tenancyType.equalsIgnoreCase("Regional"))
					{
						if(!(rolName.equalsIgnoreCase("JCB HO")))
						{
							throw new CustomFault("Invalid request,Role id Mismatch while user creation.");
						}
					}*/
			if(tenancyType.equalsIgnoreCase("Zonal"))
			{
				if(!(rolName.equalsIgnoreCase("JCB RO")))				{
					throw new CustomFault("Invalid request,role id mismatch while user creation.");
				}
			}
			if(tenancyType.equalsIgnoreCase("Dealer"))
			{
				if(!(rolName.equalsIgnoreCase("Dealer Admin") || (rolName.equalsIgnoreCase("Dealer") ||rolName.equalsIgnoreCase("Super Admin")|| rolName.equalsIgnoreCase("MA Manager"))))
				{
					throw new CustomFault("Invalid request,role id mismatch while user creation.");
				}
			}
			if(tenancyType.equalsIgnoreCase("Customer"))
			{
				if(!(rolName.equalsIgnoreCase("Customer Fleet Manager") || (rolName.equalsIgnoreCase("Customer")) || rolName.equalsIgnoreCase("MA Manager") || (rolName.equalsIgnoreCase("Customer Care"))))
				{
					throw new CustomFault("Invalid request,role id mismatch while user creation.");
				}
			}

		}

		TenancyBO tenancyDetails = new TenancyBO();
		ContactEntity contactEntity = null;

		iLogger.info("Inside setUserDetails - check1**** "+LoginId);
		if (LoginId != null)
			contactEntity = tenancyDetails.getContactUser(tenancy_id, LoginId);

		Session session = HibernateUtil.getSessionFactory().openSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();

		String email_id = null;
		iLogger.info("Inside setUserDetails - check2**** "+LoginId);
		try 
		{
			/*String queryString = "select account_id from AccountTenancyMapping where tenancy_id ="
						+ tenancy_id;
				Iterator l = session.createQuery(queryString).list().iterator();
				AccountEntity account_id = null;
				if (l.hasNext()) {
					account_id = (AccountEntity) l.next();
				}*/

			//DF20170621 - SU334449 - Setting Time Zones for different SAARC regions
			if(!CountryCode.isEmpty()){
				String queryCountryCode = ("from CountryCodesEntity where countryName='"+CountryCode+"'");
				Query query = session.createQuery(queryCountryCode);
				Iterator countryItr = query.list().iterator();
				CountryCodesEntity countryCodesTime = new CountryCodesEntity();
				if(countryItr.hasNext()){
					countryCodesTime = (CountryCodesEntity) countryItr.next();
				}
				timeZone = countryCodesTime.getTimeZone();
			}
			//DF20140811 - Rajani Nagaraju - Default Alert Preference should be ON for all users.
			//Read the required data from Properties file
			Properties prop = new Properties();
			String customerCareRoleName=null;
			String jcbAdminRoleName=null;
			String dealerAdminRoleName=null;
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			customerCareRoleName= prop.getProperty("CustomerCare");
			jcbAdminRoleName= prop.getProperty("OEMadmin");
			dealerAdminRoleName= prop.getProperty("DealerAdmin");
			iLogger.info("Inside setUserDetails - check3**** "+LoginId);
			String queryString = " select a.account_id from AccountTenancyMapping a, TenancyEntity b where a.tenancy_id =b.tenancy_id and" +
					" b.tenancyCode = (select c.tenancyCode from TenancyEntity c where c.tenancy_id='"+ tenancy_id+"') order by a.account_id ";
			iLogger.info(queryString);
			Iterator l = session.createQuery(queryString).list().iterator();
			AccountEntity account_id = null;
			if (l.hasNext()) 
			{
				account_id = (AccountEntity) l.next();
			}
			iLogger.info("Inside setUserDetails - check4**** "+LoginId);
			//Editing the Contact Details
			if (contactEntity != null) 
			{
				// Code change done by Juhi on 21-11-2013 for not updating
				// records for Login if primaryMobileNumber and primaryEmailId
				// already exist.
				iLogger.info("Inside setUserDetails - check5**** "+LoginId);
				Query query = session.createQuery("from ContactEntity where primary_mobile_number ='"
						+ primaryMobileNumber
						+ "'and active_status=true"
						+ " and contact_id!='"
						+ contactEntity.getContact_id() + "' ");
				Iterator iterator = query.list().iterator();
				if ((iterator.hasNext()) && (query.list().size() > 0)
						&& (query.list().get(0) != null))
					while (iterator.hasNext()) 
					{
						ContactEntity contact = (ContactEntity) iterator.next();
						//Change in Error Message displayed By Juhi on 11-12-2013 DF:1764
						exists = 1;
						bLogger
						.error(" Mobile Number already exists. Please enter different Mobile Number .");

						throw new CustomFault("Mobile Number already exists. Please enter different Mobile Number .");
					}

				//DF20150318 - Rajani Nagaraju - If the Email Id is null, dont check for duplicate emailId

				//DF20160808 @Roopa Removing Duplicate EmailId check since same email id can exist for different user id's

				/*if (exists == 0 && (!(primaryEmailId==null || primaryEmailId.trim().length()==0)) ) 
					{
						Query querys = session.createQuery("from ContactEntity where primary_email_id ='"
									+ primaryEmailId
									+ "' and active_status=true  and contact_id!='"+ contactEntity.getContact_id() + "'");
						Iterator itrr = querys.list().iterator();
						if ((itrr.hasNext()) && (querys.list().size() > 0)
								&& (querys.list().get(0) != null)) 
						{
							while (itrr.hasNext()) 
							{
								ContactEntity contact = (ContactEntity) itrr.next();
								exists = 1;
								//Change in Error Message displayed By Juhi on 11-12-2013 DF:1764
								bLogger.error("Email Id already exists. Please enter different Email Id .");

								throw new CustomFault("Email Id already exists. Please enter different Email Id .");
							}
						}
					}*/


				if (exists == 0) 
				{
					Query q = session.createQuery("from ContactEntity where Contact_ID ='"+ LoginId + "' and active_status=true");
					int updateFlag = 0;
					Iterator itr = q.list().iterator();
					String old_lang = null;
					while (itr.hasNext()) 
					{
						ContactEntity contact = (ContactEntity) itr.next();
						old_lang = contact.getLanguage();
						
						/*contact.setFirst_name(first_name);
						contact.setLast_name(last_name);
						contact.setIs_tenancy_admin(Is_tenancy_admin);
						contact.setRole(roleEntity);
						contact.setPrimary_mobile_number(primaryMobileNumber);
						contact.setCountryCode(CountryCode);
						contact.setPrimary_email_id(primaryEmailId);
						contact.setTimezone(timeZone);
						contact.setLanguage(language);

						session.beginTransaction();
						session.update(contact);*/
						if(old_lang!=null && !(old_lang.equalsIgnoreCase(language)))
						{
							setLanguageSettingsTrack(contact,old_lang,language);
						}
						updateFlag = 1;
						
						//Df20170921 @Roopa AES password encryption changes
						
						String storedPwd=new CommonUtil().getDecryptedPassword(LoginId);
						
						if(language == null || language.isEmpty()){
							language = "English";
						}
						
						if(storedPwd==null){
							throw new CustomFault(
									"Problem in password decryption.");
						}
						else{
						    //Deepthi: Added Last Updated : 20210927
						    String updatQuery="update contact set password=AES_ENCRYPT('"+storedPwd+"','"+primaryMobileNumber+"'),First_Name='"+first_name+"',Last_Name='"+last_name+"' " +
							    ",Is_Tenancy_Admin="+Is_tenancy_admin+",Role_ID="+roleEntity.getRole_id()+",Primary_Moblie_Number='"+primaryMobileNumber+"',countrycode='"+CountryCode+"'" +
							    ",Primary_Email_ID='"+primaryEmailId+"',TimeZone='"+timeZone+"',Language='"+language+"',LastUpdatedTime ='"+currentDate+"' where contact_id='"+LoginId+"'";

						    iLogger.info("User Creation Service:: "+updatQuery);

						    try{
							String result=new CommonUtil().insertData(updatQuery);

							iLogger.info("User Creation Service::"+"update contat details with encrypted password into contact table status::"+result);
							//DF20181030 :: To capture the failure status while editing the user,logging the failure status into fatal logs.
							if(result.equalsIgnoreCase("FAILURE") && updateFlag==1 ){
							    fLogger.info("User Creation Service:: Failed to Update the details :: updatQuery ::"+updatQuery+" :: status ::"+result);
							}else{

							 // Send Details to ContactDetails kafka topic
								HashMap<String, String> payloadMap = new HashMap<>();
								payloadMap = new HashMap<>();
								payloadMap.put("Contact_ID", LoginId);
								payloadMap.put("First_Name", first_name);
								payloadMap.put("Last_Name", last_name);
								payloadMap.put("Is_Tenancy_Admin", String.valueOf(Is_tenancy_admin));
								payloadMap.put("Role_ID", String.valueOf(roleEntity.getRole_id()));
								payloadMap.put("Password", storedPwd);
								payloadMap.put("Primary_Moblie_Number", primaryMobileNumber);
								payloadMap.put("countryCode", CountryCode);
								payloadMap.put("Primary_Email_ID", primaryEmailId);
								payloadMap.put("TimeZone", timeZone);
								payloadMap.put("Language", language);
								payloadMap.put("LastUpdatedTime", currentDate);
								
								new ContactDetailsProducerThread(payloadMap, LoginId+"_"+currentDate);
				
							    //DF20190102-KO369761-logging user edited data in Database.
							    String newUserData = first_name+"|"+last_name+"|"+primaryMobileNumber+"|"+primaryEmailId+"|"+roleEntity.getRole_id()+"|"+CountryCode+"|"+timeZone+"|"+language+"|"+Is_tenancy_admin;
							    String oldUserData = contact.getFirst_name()+"|"+contact.getLast_name()+"|"+contact.getPrimary_mobile_number()+"|"+contact.getPrimary_email_id()+"|"+contact.getRole().getRole_id()+"|"+contact.getCountryCode()+"|"+contact.getTimezone()+"|"+contact.getLanguage()+"|"+contact.getIs_tenancy_admin();

							    Calendar c1 = Calendar.getInstance();
							    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
							    c1.setTime(new Date());
							    Timestamp currTime = new Timestamp(c1.getTime().getTime());
							    String partitionKey = dateFormat.format(c1.getTime());

							    String insertQ = "INSERT INTO user_details_log VALUES('"+LoginId+"','"+editedByUserId+"','"+oldUserData+"','"+newUserData+"','"+currTime+"','"+partitionKey+"')";
							    new CommonUtil().insertData(insertQ);
							}
						    }				
						    catch(Exception e){
							e.printStackTrace();
						    }
						}

						//Df20170921 @Roopa AES password encryption changes END
					}
					/*	try
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

						fLogger.fatal("Exception in commiting the record:"+e.getMessage());
					}*/
	
				}
			} 

			//Insert new Contact 

			else {
				iLogger.info("Inside setUserDetails - check6**** "+LoginId);
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					if(session.getTransaction().isActive() && session.isDirty())
					{
						iLogger.info("Opening a new session");
						session = HibernateUtil.getSessionFactory().openSession();
					}
					session.beginTransaction();

				}
				// Code added by Juhi on 7-november-2013 :if primaryMobileNumber
				// and primaryEmailId
				// exists in Contact table then don't add new user
				iLogger.info("Inside setUserDetails - check7**** "+LoginId);
				iLogger.info(primaryMobileNumber);
				Query query = session.createQuery("from ContactEntity where primary_mobile_number ='"
						+ primaryMobileNumber
						+ "'and active_status=true");
				List resultList = query.list();
				Iterator itr = resultList.iterator();
				iLogger.info("-----------"+query);
				//DF20140225 - Rajani Nagaraju - To handle the case if email_id or contact is passed as NULL
				if ( (resultList.size() > 0) && (resultList.get(0)!=null) ){
					//if (resultList.size() > 0){
					/*while (itr.hasNext()) 
					{
						ContactEntity contact = (ContactEntity) itr.next();*/
					//DefectId:20140207 @suprava
					exists = 1;
					// Defect Id 1612 by Manish for error message.
					bLogger
					.error(" Phone Number already exists. Please provide unique Phone Number for creating new user.");

					throw new CustomFault(
							"Phone Number already exists. Please provide unique Phone Number for creating new user.");
					/*}*/

				}

				//DF20160808 @Roopa Removing Duplicate EmailId check since same email id can exist for different user id's
				/*if(exists == 0 && (!(primaryEmailId==null || primaryEmailId.trim().length()==0)) ) 
				{
					iLogger.info("Inside setUserDetails - check8**** ");
					Query querys = session.createQuery("from ContactEntity where primary_email_id ='"
									+ primaryEmailId
									+ "' and active_status=true");
					resultList = query.list();
					Iterator itrr = resultList.iterator();
					//DF20140225 - Rajani Nagaraju - To handle the case if email_id or contact is passed as NULL
					if ( (resultList.size() > 0) && (resultList.get(0)!=null) ){
					//if (resultList.size() > 0){
						while (itrr.hasNext()) 
						{
//							ContactEntity contact = (ContactEntity) itrr.next();
							exists = 1;
							// Defect Id 1612 by Manish for error message.
							bLogger.error("Email Id already exists. Please provide unique Email Id for creating new user.");

							throw new CustomFault("Email Id already exists. Please provide unique Email Id for creating new user.");
						}
					}

				}*/


				if (exists == 0) 
				{

					iLogger.info("last_name:"+last_name);
					iLogger.info("first_name:"+first_name);
					String first_name1 = StringUtils.deleteWhitespace(first_name);
					String last_name1 =null;
					String full_name =null;
					if(last_name!=null && !(last_name.isEmpty())){
						last_name1 = StringUtils.deleteWhitespace(last_name);
						full_name = first_name1 + last_name1;
					}
					else 
					{
						full_name = first_name1;
					}
					//String full_name = first_name1 + last_name1;
					// DefectID: 20141215 @Suprava Nayak Remove All Special characters from User Name
					full_name=full_name.replaceAll("[-+/.^:,$&!#@*]","");
					iLogger.info("full_name:"+full_name);
					// End DefectID: 20141215

					// DefectID: 1454: Rajani Nagaraju - 20131126
					if (full_name == null || full_name.length() < 4) 
					{
						throw new CustomFault("First Name and LastName together has to be atleast 4 characters in size");
					}
					// DefectID: 1454: Rajani Nagaraju - 20131126

					String uname = full_name.substring(0, 4);

					/*String uid = null;
					Query q1 = session.createQuery("select count(*) from ContactEntity ");
					Iterator it = q1.list().iterator();
					if (it.hasNext()) 
					{
						Long counter = (Long) it.next();
						Long counter1 = counter % 10000;
						String string = counter1.toString();
						int length = string.length();

						if (length == 1) 
						{
							uid = "0" + "0" + "0" + "0" + string;
						} 
						else if (length == 2) 
						{
							uid = "0" + "0" + "0" + string;
						} 
						else if (length == 3) 
						{
							uid = "0" + "0" + string;
						} 
						//to handle null contact Id @Suprava
						else if (length == 4) 
						{
							uid = "0" + string;
						}

					}*/
					
					/**
					 * DF20181214- Adding 6 digit random number to first 4
					 * characters name to form contact ID(Above code block
					 * commented for this block) to avoid duplicate user Ids
					 * Creation - Changes made by KO369761
					 */
					String uid = null;
					String tempCID = null;
					ContactEntity contactObj = null;
					int loopVar = 0;

					for(loopVar = 0;loopVar<3;loopVar++){
						Random r = new Random();
						long nextLong = Math.abs(r.nextLong());
						uid = String.valueOf(nextLong).substring(0, 6);
						tempCID = uname+uid;
						iLogger.info("trying for the "+loopVar+" time with formed user id :"+tempCID+"::user name:"+first_name);
						
						//checking if duplicate contact exists or not.
						contactObj = domainService.getContactDetails(tempCID);

						if(contactObj == null || contactObj.getContact_id() == null)
							break;
					}
					
					//After 3 attempts, if there is no unique id generation, throwing exception
					if(loopVar == 3){
						fLogger.fatal("failed in contact creation after 3 attempts::"+first_name);
						throw new CustomFault("Exception occured.");
					}
					
					iLogger.info("Inside setUserDetails - check9**** "+LoginId);
					email_id = uname + uid;
					iLogger.info("contactId:"+email_id);
					LoginId = email_id;
					
					String validChars = "abcdefghijklmnopqrstuvwxyz";
					String validCapital = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
					String password = "";
					//DF20140805 - Rajani Nagaraju - Removing some special characters since they are not allowed in HTTP Request URL during SMS send
				//	String validSpecial = "*$#@&!";
					String validSpecial = "*#@!";
					String numeric = "1234567890";
					Random generator = new Random();

					for (int i = 0; i < 7; i++) 
					{
						password += validChars.charAt(generator.nextInt(validChars.length()));
					}

					password += validSpecial.charAt(generator.nextInt(validSpecial.length()));

					password += numeric.charAt(generator.nextInt(numeric.length()));
					password += validCapital.charAt(generator.nextInt(validCapital.length()));

					for (int i = 0; i < 2; i++) 
					{
						password += validChars.charAt(generator.nextInt(validChars.length()));
					}

					//DF20170113 - Supriya -  password simplification chnages
					/*String password = "";
					String validSpecial = "@_";
					String numeric = "1234567890";
					Random generator = new Random();
					password += uname;

					password += validSpecial.charAt(generator.nextInt(validSpecial.length()));

					for(int i=0;i<4;i++){
						password += numeric.charAt(generator.nextInt(numeric.length()));
					}*/

					// infoLogger.info("password is" + password);
					// infoLogger.info("email_id is" + email_id);

					// DefectId: 1322 - Rajani Nagaraju - To handle case
					// sensitive for user login - Always userId is lowercase
					email_id = email_id.toLowerCase();
					LoginId = LoginId.toLowerCase();

					//Df20170921 @Roopa AES password encryption changes -->Changes to SQL query

					/*ContactEntity contactEntity1 = new ContactEntity();
					contactEntity1.setFirst_name(first_name);
					contactEntity1.setLast_name(last_name);
					contactEntity1.setIs_tenancy_admin(Is_tenancy_admin);
					contactEntity1.setContact_id(email_id);
					contactEntity1.setRole(roleEntity);
					contactEntity1.setPassword(password);
					contactEntity1.setSysGeneratedPassword(1);
					contactEntity1.setPrimary_mobile_number(primaryMobileNumber);
					contactEntity1.setCountryCode(CountryCode);
					contactEntity1.setPrimary_email_id(primaryEmailId);
					contactEntity1.setTimezone(timeZone);
					contactEntity1.setLanguage(language);
					contactEntity1.setActive_status(true);*/
					// setting client id for the user : 2013/06/11
					iLogger.info("Inside setUserDetails - check10**** "+LoginId);
					Properties property = new Properties();
					try 
					{
						property.load(getClass()
								.getClassLoader()
								.getResourceAsStream(
										"remote/wise/resource/properties/configuration.properties"));

					} catch (IOException e) {
						e.printStackTrace();

					}
					ClientEntity ce = null;
					String clientName = property.getProperty("ClientName");
					IndustryBO industryBoObj = new IndustryBO();
					ClientEntity clientEntity = industryBoObj
							.getClientEntity(clientName);
					if (clientName == null) {
						throw new CustomFault(
								"Configuration property does not exist !!");
					}
					Query clientQuery = session
							.createQuery("FROM ClientEntity WHERE client_name='"
									+ clientName
									+ "' and client_id="
									+ clientEntity.getClient_id() + "");
					Iterator clientIter = clientQuery.list().iterator();
					while (clientIter.hasNext()) {
						ce = (ClientEntity) clientIter.next();
					}
					//contactEntity1.setClient_id(ce);
					//contactEntity1.save();
					
					if(language == null || language.isEmpty()){
						language = "English";
					}
//Deepthi: Added LastUpdatedTime:20210927
					String inserQuery="Insert into contact(First_Name,Last_Name,Is_Tenancy_Admin,Contact_ID,Role_ID,Password,sys_gen_password,Primary_Moblie_Number," +
							"countrycode,Primary_Email_ID,TimeZone,Language,Status,Client_ID,LastUpdatedTime) values" +
							"('"+first_name+"','"+last_name+"',"+Is_tenancy_admin+",'"+email_id+"',"+roleEntity.getRole_id()+",AES_ENCRYPT('"+password+"','"+primaryMobileNumber+"'),1,'"+primaryMobileNumber+"'" +
							",'"+CountryCode+"','"+primaryEmailId+"','"+timeZone+"','"+language+"',1,"+ce.getClient_id()+",'"+currentDate+"')";
					
					iLogger.info("User Creation Service:: "+inserQuery);

					//Df20170921 @Roopa AES password encryption changes

					try{

						String result=new CommonUtil().insertData(inserQuery);

						iLogger.info("User Creation Service::"+"insert into contact table with encrypted password status::"+result);
						
						//DF20181214-KO369761-Not to map older contact to new account during customer creation.Hence exiting the process.
						if(result.equalsIgnoreCase("FAILURE"))
							throw new CustomFault("Exception occured.");
						
						 // Send Details to ContactDetails kafka topic
						HashMap<String, String> payloadMap = new HashMap<>();
						payloadMap = new HashMap<>();
						payloadMap.put("Contact_ID", LoginId);
						payloadMap.put("First_Name", first_name);
						payloadMap.put("Last_Name", last_name);
						payloadMap.put("Is_Tenancy_Admin", String.valueOf(Is_tenancy_admin));
						payloadMap.put("Role_ID", String.valueOf(roleEntity.getRole_id()));
						payloadMap.put("Password", password);
						payloadMap.put("sys_gen_password", "1");
						payloadMap.put("Primary_Moblie_Number", primaryMobileNumber);
						payloadMap.put("countryCode", CountryCode);
						payloadMap.put("Primary_Email_ID", primaryEmailId);
						payloadMap.put("TimeZone", timeZone);
						payloadMap.put("Language", language);
						payloadMap.put("Status", "1");
						payloadMap.put("Client_ID", String.valueOf(ce.getClient_id()));
						payloadMap.put("LastUpdatedTime", currentDate);
						
						new ContactDetailsProducerThread(payloadMap, LoginId+"_"+currentDate);
					}
					catch(Exception e){
						e.printStackTrace();
						fLogger.fatal("failed in contact creation. exception occured while inserting");
						//DF20181214-KO369761-Not to map older contact to new account during customer creation.Hence exiting the process.
						throw new CustomFault("Exception occured.");
					}

					//Df20170921 @Roopa AES password encryption changes END

					iLogger.info("Inside setUserDetails - check11**** "+LoginId);
					//Map the newly created user to the account
					AccountContactMapping account = new AccountContactMapping();
					account.setAccount_id(account_id);
					ContactEntity contactEntity2 = domainService
							.getContactDetails(email_id);
					account.setContact_id(contactEntity2);

					account.save();
					iLogger.info("Inside setUserDetails - Saved******* "+LoginId);
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

						fLogger.fatal("Exception in commiting the record:"+e.getMessage());
					}
					iLogger.info("Inside setUserDetails - Committed******* "+LoginId);



					//START - DF20140811 - Rajani Nagaraju - Add Try/catch block to handle the Exception if there is an issue in sending SMS/Email
					try
					{
						// send user the user name and password 2013/06/11
						//					Keerthi : 03/02/14 : checking whether primaryEmailId is null
						if(primaryEmailId!=null && !primaryEmailId.equals("")){
							sendMailToUser(primaryEmailId, LoginId, password);
						}
						iLogger.info("EmailSent ");
						//					Keerthi : 03/02/14 : sending SMS with registration details
						if(primaryMobileNumber!=null && !primaryMobileNumber.equals("") ){
							sendSMSToUser(primaryMobileNumber,CountryCode,LoginId,password);
						}

						iLogger.info("SMS Sent ");
					}
					catch(Exception e)
					{
						fLogger.fatal("Exception in Sending SMS / Email for contact:"+LoginId+", "+e.getMessage());
					}
					//END - DF20140811 - Rajani Nagaraju - Add Try/catch block to handle the Exception if there is an issue in sending SMS/Email

					//START - DF20140811 - Rajani Nagaraju - Default Alert Preference should be ON for all users.
					if (!(session.isOpen())) {
						session = HibernateUtil.getSessionFactory().openSession();
						//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
						if(session.getTransaction().isActive() && session.isDirty())
						{
							iLogger.info("Opening a new session");
							session = HibernateUtil.getSessionFactory().openSession();
						}

						//	session.getTransaction().begin();
					}

					List<UserAlertPreferenceRespContract> reqContractObj = new LinkedList<UserAlertPreferenceRespContract>();
					//DF20181102 :: MA369757 :: setting the alert preferences asynchronously after user creation [Timeout Issue in settings->add user]
					iLogger.info("UserDetailsBO ::"+LoginId+" :: invoking the SetAlertPreferencesThread");
					new Thread(new SetAlertPreferencesThread(roleEntity.getRole_name(),
							LoginId, role_name)).start();
					//DF20181102 :: Commenting out because handling user preferences using async threads
					/*//If the user is CC/Admin - Event wise preference
					if( (roleEntity.getRole_name().equalsIgnoreCase(customerCareRoleName)) || (roleEntity.getRole_name().equalsIgnoreCase(jcbAdminRoleName)) )
					{
						//get the List of Events from Business Event table
						Query eventListQ = session.createQuery(" from EventEntity ");
						Iterator eventListItr = eventListQ.list().iterator();
						while(eventListItr.hasNext())
						{
							EventEntity event = (EventEntity) eventListItr.next();
							UserAlertPreferenceRespContract response= new UserAlertPreferenceRespContract();
							response.setEmailEvent(true);
							response.setEventId(event.getEventId());
							response.setEventName(event.getEventName());
							response.setEventTypeId(event.getEventTypeId().getEventTypeId());
							response.setEventTypeName(event.getEventTypeId().getEventTypeName());
							response.setLoginId(LoginId);
							response.setRoleName(role_name);
							response.setSMSEvent(true);

							reqContractObj.add(response);
						}
						iLogger.info("Set Admin Alert Preference for Alerts :"+LoginId);
						UserAlertPreferenceImpl implObj = new UserAlertPreferenceImpl();
						implObj.setAdminAlertPreference(reqContractObj);

					}
					//DF20170315 Supriya - If the user is DealerAdmin - Event wise preference		
					else if( (roleEntity.getRole_name().equalsIgnoreCase(dealerAdminRoleName)))
					{
						Query eventListQ = session.createQuery("from EventEntity where eventTypeId =2");
						List<Integer> eventIds = Arrays.asList(4, 5, 6, 16, 17);
						Iterator eventListItr = eventListQ.list().iterator();
						while(eventListItr.hasNext())
						{
							EventEntity event = (EventEntity) eventListItr.next();	
							int eventId = event.getEventId();
							if(!eventIds.contains(eventId)){
								UserAlertPreferenceRespContract response= new UserAlertPreferenceRespContract();
								response.setEmailEvent(true);
								response.setEventId(event.getEventId());
								response.setEventName(event.getEventName());
								response.setEventTypeId(event.getEventTypeId().getEventTypeId());
								response.setEventTypeName(event.getEventTypeId().getEventTypeName());
								response.setLoginId(LoginId);
								response.setRoleName(role_name);
								response.setSMSEvent(true);

								reqContractObj.add(response);
							}

						}
						iLogger.info("Set Dealer Admin Alert Preference for Alerts :"+LoginId);
						UserAlertPreferenceImpl implObj = new UserAlertPreferenceImpl();
						implObj.setDealerAdminAlertPreference(reqContractObj);

					}


					//If it is any other user other than  CC/Admin - Event Type wise preference
					else
					{
						//get the List of Event Type from Business Event table
						Query eventTypeListQ = session.createQuery(" from EventTypeEntity ");
						Iterator eventTypeListItr = eventTypeListQ.list().iterator();
						while(eventTypeListItr.hasNext())
						{
							EventTypeEntity eventType = (EventTypeEntity) eventTypeListItr.next();
							UserAlertPreferenceRespContract response= new UserAlertPreferenceRespContract();
							response.setEmailEvent(true);
							response.setEventTypeId(eventType.getEventTypeId());
							response.setEventTypeName(eventType.getEventTypeName());
							response.setLoginId(LoginId);
							response.setRoleName(role_name);
							response.setSMSEvent(true);

							reqContractObj.add(response);
						}
						iLogger.info("Set User Alert Preference for Alerts :"+LoginId);
						UserAlertPreferenceImpl implObj = new UserAlertPreferenceImpl();
						implObj.setUserAlertPreference(reqContractObj);
					}*/

					//END - DF20140811 - Rajani Nagaraju - Default Alert Preference should be ON for all users.
				}
			}
			if (!(session.isOpen())) {
				session = HibernateUtil.getSessionFactory().openSession();
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
				if(session.getTransaction().isActive() && session.isDirty())
				{
					iLogger.info("Opening a new session");
					session = HibernateUtil.getSessionFactory().openSession();
				}


			}

			if (exists == 0) 
			{
				UserDetailsBO userBO = new UserDetailsBO();
				List<Integer> groupUserList = new LinkedList<Integer>();

				// Changes Done by Juhi on 25-november-2013 for defect ID:1628
				if (!(asset_group_id == null || asset_group_id.isEmpty())) {
					iLogger
					.info("For given Contact_ID get list of Group_id");
					Query q = session
							.createQuery("from GroupUserMapping where Contact_ID ='"
									+ LoginId + "'");
					int Flag = 0;
					Iterator itr = q.list().iterator();
					while (itr.hasNext()) {
						GroupUserMapping group_user = (GroupUserMapping) itr
								.next();
						groupUserList.add(group_user.getGroup_id()
								.getGroup_id());
						Flag = 1;
					}
					List<Integer> differList1 = new ArrayList<Integer>();
					if (asset_group_id != null && !asset_group_id.isEmpty()) {
						differList1.addAll(asset_group_id);
					}
					differList1.removeAll(groupUserList); // add this list

					Iterator<Integer> iterIds = differList1.iterator();
					int assetGroupId = 0;
					CustomAssetGroupEntity customAssetGroup = null;
					while (iterIds.hasNext()) {
						assetGroupId = (Integer) iterIds.next();
						GroupUserMapping gum = new GroupUserMapping();
						ContactEntity ce = domainService
								.getContactDetails(LoginId);
						gum.setContact_id(ce);
						customAssetGroup = domainService
								.getCustomAssetGroupDetails(assetGroupId);
						gum.setGroup_id(customAssetGroup);
						session.beginTransaction();
						gum.save();

					}


					if (!(session.isOpen())) {
						session = HibernateUtil.getSessionFactory().openSession();
						//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
						if(session.getTransaction().isActive() && session.isDirty())
						{
							iLogger.info("Opening a new session");
							session = HibernateUtil.getSessionFactory().openSession();
						}
					}

					differList1 = new ArrayList<Integer>();
					differList1.addAll(groupUserList);
					if (asset_group_id != null && !asset_group_id.isEmpty()) {
						differList1.removeAll(asset_group_id);// delete these
					}
					ListToStringConversion conversionObj = new ListToStringConversion();
					String assetGroupIdString = null;
					if (differList1 != null && differList1.size() > 0) {

						assetGroupIdString = conversionObj
								.getIntegerListString(differList1).toString();
					}
					// delete these
					Query qu2 = session
							.createQuery("delete from GroupUserMapping where group_id in ("
									+ assetGroupIdString
									+ ") AND Contact_ID ='" + LoginId + "'");
					session.beginTransaction();
					int row1 = qu2.executeUpdate();
					//Changes for Machine Group Defect – Start
					Query qu3 = session.createQuery("delete from CustomAssetGroupSnapshotEntity where user_Id ='"+LoginId+"'");
							int row2 = qu3.executeUpdate();
							
					//Get the number of machines assigned to each group
							for(int i=0; i<asset_group_id.size(); i++)
							{
					Query CAGMQuery = session.createQuery("from AssetCustomGroupMapping where	Group_ID="+asset_group_id.get(i));
								
							   Iterator CAGMItr = CAGMQuery.list().iterator();
							   while (CAGMItr.hasNext()) 
							   {
							       AssetCustomGroupMapping cag = 
					(AssetCustomGroupMapping) CAGMItr.next();
								CustomAssetGroupSnapshotEntity cagsObj =  new 
					CustomAssetGroupSnapshotEntity();
												
					cagsObj.setAsset_Id(cag.getSerial_number().getSerial_number
					  ().getSerialNumber());
													
					cagsObj.setGroup_ID(asset_group_id.get(i));
								cagsObj.setUser_Id(LoginId);
								session.save(cagsObj);
							   }
					}
					//Changes for Machine Group Defect - End

				}

				else{
					//DF20160923 @Roopa Changing the place of session check
					if (!(session.isOpen())) {
						session = HibernateUtil.getSessionFactory().openSession();
						//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
						if(session.getTransaction().isActive() && session.isDirty())
						{
							iLogger.info("Opening a new session");
							session = HibernateUtil.getSessionFactory().openSession();
						}
					}
					//user is not assigned to any groups. so delete all existing, if any
					Query qu2 = session
							.createQuery("delete from GroupUserMapping where Contact_ID ='" + LoginId + "'");
					session.beginTransaction();
					int row1 = qu2.executeUpdate();
					//Changes for Machine Group Defect – Start
					Query qu3 = session.createQuery("delete from CustomAssetGroupSnapshotEntity where user_Id ='" + LoginId + "'");
							session.beginTransaction();
							int row2 = qu3.executeUpdate();

							//Changes for Machine Group Defect - End

				}


			}

			/*
			 * if (!(asset_group_id == null || asset_group_id.isEmpty())) {
			 * infoLogger.info("For given Contact_ID get list of Group_id");
			 * Query q = session
			 * .createQuery("from GroupUserMapping where Contact_ID ='" +
			 * LoginId + "'"); int Flag = 0; Iterator itr = q.list().iterator();
			 * while (itr.hasNext()) { GroupUserMapping group_user =
			 * (GroupUserMapping) itr.next();
			 * groupUserList.add(group_user.getGroup_id().getGroup_id()); Flag =
			 * 1; } // infoLogger.info("list extracted"); //
			 * infoLogger.info("database value"+groupUserList.size()); //
			 * infoLogger
			 * .info("group id size coming from user"+asset_group_id.size());
			 * 
			 * List<Integer> retainValues = new LinkedList<Integer>();
			 * retainValues.addAll(groupUserList);
			 * infoLogger.info("retainValues" + retainValues.size());
			 * 
			 * if (!(asset_group_id == null || asset_group_id.isEmpty())) {
			 * retainValues.retainAll(asset_group_id);
			 * infoLogger.info("retainValues----" + retainValues.size());
			 * groupUserList.removeAll(retainValues);
			 * asset_group_id.removeAll(retainValues); } //
			 * infoLogger.info("groupUserList" + groupUserList.size()); //
			 * infoLogger.info("asset_group_id" + asset_group_id.size());
			 * 
			 * int count = 0; int i = 0;
			 * 
			 * for (i = 0; i < groupUserList.size(); i++) {
			 * infoLogger.info("Inside the update loop***");
			 * 
			 * if (count >= i && asset_group_id.size() > 0) {
			 * 
			 * Query query1 = session
			 * .createQuery("update GroupUserMapping set Group_ID='" +
			 * retainValues.get(count) + "' where Contact_ID='" + LoginId +
			 * "' and Group_ID='" + retainValues.get(i) + "'");
			 * query1.executeUpdate(); count++;
			 * infoLogger.info(" GroupUser Updated successfully"); //
			 *  } else break;
			 * 
			 * }
			 * 
			 * while (i < groupUserList.size())
			 * 
			 * { infoLogger.info("Inside the delete loop ***"); Query query2 =
			 * session
			 * .createQuery("delete from GroupUserMapping where Group_ID='" +
			 * groupUserList.get(i) + "' and Contact_ID='" + LoginId + "'");
			 * query2.executeUpdate();
			 * 
			 * i++; } // infoLogger.info("Session&&& :" + session); //
			 * infoLogger.info("Session&&& ISOPENED:" + session.isOpen()); if
			 * (asset_group_id != null) { while (count < asset_group_id.size())
			 * 
			 * {
			 * 
			 * infoLogger.info("Before setting values "); GroupUserMapping
			 * groupUserMapping = new GroupUserMapping();
			 * 
			 * ContactEntity contact = domainService
			 * .getContactDetails(LoginId); //
			 * infoLogger.info("asset_group_id.get(count) value " +
			 * asset_group_id.get(count)); CustomAssetGroupEntity
			 * customAssetGroup = domainService
			 * .getCustomAssetGroupDetails(asset_group_id .get(count)); //
			 * infoLogger.info("customAssetGroup " + customAssetGroup);
			 * 
			 * if (!(session.isOpen()))
			 * 
			 * {
			 * 
			 * session = HibernateUtil.getSessionFactory() .openSession();
			 * 
			 * session.getTransaction().begin();
			 * 
			 * } if (!(customAssetGroup == null || customAssetGroup
			 * .getGroup_id() == 0)) { infoLogger.info("%%% " +
			 * customAssetGroup.getGroup_id()); // infoLogger.info("contact" +
			 * // contact.getContact_id());
			 * groupUserMapping.setGroup_id(customAssetGroup);
			 * groupUserMapping.setContact_id(contact); //
			 * groupUserMapping.save(); session.save(groupUserMapping); count++;
			 * 
			 * } } } }
			 */

		}
		/*//DF20181214-KO369761-uncommented catch block to cath exceptions during contact creation.
		catch(CustomFault e){
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		//DF20181214-KO369761-uncommented catch block to cath exceptions during contact creation.
		catch(Exception e){ 
			e.printStackTrace();
			fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());
		}*/
		finally 
		{

			try
			{
				if (session.getTransaction().isActive()) {
					session.flush();
					session.getTransaction().commit();
				}
			}

			catch(Exception e)
			{
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);

			}

			if (session.isOpen()) {

				session.close();
			}

		}
		return "SUCCESS";
			}

	// ******************************************End of Set UserDetails for
	// given LoginId****************************************************

	// Added by Keerthi

	/**
	 * method to get all dealers under all zone(s) for logged-in user's tenancy
	 * id
	 * 
	 * @param loginId
	 * @param loginTenancyId
	 * @return DealersUnderZoneImpl
	 */

	public DealersUnderZoneImpl getDealersForZone(String loginId,
			int loginTenancyId) {

		Logger iLogger = InfoLoggerClass.logger;

		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		DealersUnderZoneImpl impl = new DealersUnderZoneImpl();

		/*
		 * Query select t2.child_id,t1.tenancy_name,t4.tenancy_type_id from
		 * tenancy t1,tenancy_bridge t2, tenancy_type t4 where t2.parent_id in
		 * (201) and t4.tenancy_type_id in (2,3) and t2.child_id=t1.tenancy_id
		 * and t1.tenancy_type_id =t4.tenancy_type_id and t2.child_id not in
		 * (select distinct child_id from tenancy_bridge where child_id=1);
		 */
		try {
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			List<Integer> tenancyIdList=new ArrayList<Integer>();
			tenancyIdList.add(loginTenancyId);
			
			tenancyIdList.addAll(new DateUtil().getLinkedTenancyListForTheTenancy(tenancyIdList));
			
			ListToStringConversion conversion = new ListToStringConversion();
			String tenancyIdListString = conversion.getIntegerListString(tenancyIdList).toString();
			
			String queryString = "select t2.childId,t1.tenancy_name,t4.tenancy_type_id,t1.parent_tenancy_id,t1.parent_tenancy_name "
					+ " from TenancyEntity t1,TenancyBridgeEntity t2, TenancyTypeEntity t4"
					+ " where t2.parentId in ("
					+ tenancyIdListString
					+ ") "
					+ " and t4.tenancy_type_id in (2,3)  and t2.childId=t1.tenancy_id"
					+ " and t1.tenancy_type_id =t4.tenancy_type_id"
					+ " order by t2.childId asc ";

			iLogger.info("Query : " + queryString);
			Map<Integer, TreeMap<Integer, String>> zoneDealerMap = new TreeMap<Integer, TreeMap<Integer, String>>();

			Object[] result = null;
			int tenancyTypeId = 0;
			int tenancyID = 0;
			int parentID = 0;
			String parentName = null;
			String zoneName = null;
			TreeMap<Integer, String> zoneMap = new TreeMap<Integer, String>();
			TreeMap<Integer, String> dealerMap = null;

			Iterator itr = session.createQuery(queryString).list().iterator();

			while (itr.hasNext()) {
				result = (Object[]) itr.next();
				// get tenancy type id. if it is 2(zone) and to map
				if (result[2] != null) {
					tenancyTypeId = (Integer) result[2];
				}
				if (tenancyTypeId == 2) {
					if (result[0] != null) {
						tenancyID = (Integer) result[0];
					}
					if (result[1] != null) {
						zoneName = (String) result[1];
					}
					zoneMap.put(tenancyID, zoneName);
					zoneDealerMap.put(tenancyID, null);
				} else if (tenancyTypeId == 3) {
					if (result[0] != null) {
						tenancyID = (Integer) result[0];
					}
					if (result[3] != null) {
						parentID = ((TenancyEntity) result[3]).getTenancy_id();
					}
					if (result[4] != null) {
						parentName = (String) result[4];
					}

					// if map contains already zone id, remove list and add this
					// and then add.
					if (zoneDealerMap.containsKey(parentID)) {
						dealerMap = zoneDealerMap.get(parentID);
						if (dealerMap == null) {
							dealerMap = new TreeMap<Integer, String>();
						}
						dealerMap.put(tenancyID, (String) result[1]);
						zoneDealerMap.remove(parentID);

					} else {
						// zone id (parent)does not exist in map. create key for
						// parent
						dealerMap = new TreeMap<Integer, String>();
						dealerMap.put(tenancyID, (String) result[1]);

					}
					zoneDealerMap.put(parentID, dealerMap);
				}
			}
			/*if (zoneMap.size() == 0) {
				zoneMap.put(parentID, parentName);
			}*/
			impl.setZoneMap(zoneMap);
			impl.setZoneDealerMap(zoneDealerMap);
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
		iLogger.info("Total execution time "
				+ String.valueOf(endTime - startTime) + "(ms)");
		return impl;
	}

	/**
	 * method to get customers for a dealer
	 * 
	 * @param dealerTenancyId
	 * @return CustomersUnderDealerImpl
	 */

	public CustomersUnderDealerImpl getCustomersForDealer(int dealerTenancyId) {

		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Logger iLogger = InfoLoggerClass.logger;

		/*
		 * Query select t2.child_id,t1.tenancy_name,t4.tenancy_type_id from
		 * tenancy t1,tenancy_bridge t2, tenancy_type t4 where t2.parent_id in
		 * (301) and t4.tenancy_type_id in (3,4) and t2.child_id=t1.tenancy_id
		 * and t1.tenancy_type_id =t4.tenancy_type_id and t2.child_id not in
		 * (select distinct child_id from tenancy_bridge where child_id=301);
		 */
		CustomersUnderDealerImpl impl = new CustomersUnderDealerImpl();
		try {
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			
			List<Integer> tenancyIdList=new ArrayList<Integer>();
			
			tenancyIdList.add(dealerTenancyId);
			
			tenancyIdList.addAll(new DateUtil().getLinkedTenancyListForTheTenancy(tenancyIdList));
			
			ListToStringConversion conversion = new ListToStringConversion();
			String tenancyIdListString = conversion.getIntegerListString(tenancyIdList).toString();
			
			
			String queryString = "select t2.childId,t1.tenancy_name,t4.tenancy_type_id"
					+ " from TenancyEntity t1,TenancyBridgeEntity t2,TenancyTypeEntity t4"
					+ " where t2.parentId in ("
					+ tenancyIdListString
					+ ")"
					+ " and t4.tenancy_type_id in (3,4)  and t2.childId=t1.tenancy_id"
					+ " and t1.tenancy_type_id =t4.tenancy_type_id"
					+ " order by t1.tenancy_name asc ";
			//" order by t2.childId asc ";

			iLogger.info("Query : " + queryString);

			//DF23/03/2015 for alphabetical order of customer name

			LinkedHashMap<Integer, String> customerMap = null;
			Iterator itr = session.createQuery(queryString).list().iterator();
			Object[] result = null;
			int tenancyTypeId = 0;
			int tenancyId = 0;
			String tenancyName = null;

			while (itr.hasNext()) {
				result = (Object[]) itr.next();
				if (result[0] != null) {
					tenancyId = (Integer) result[0];
				}
				// get tenancy type id. if it is 3(dealer) .
				if (result[2] != null) {
					tenancyTypeId = (Integer) result[2];
				}
				if (tenancyTypeId == 3) {
					impl.setDealerID(tenancyId);
				} else if (tenancyTypeId == 4) { // 4= customer
					if (customerMap == null) {
						customerMap = new LinkedHashMap<Integer, String>();
					}
					if (result[1] != null) {
						tenancyName = (String) result[1];
					}
					customerMap.put(tenancyId, tenancyName);
				}
			}
			impl.setCustomerMap(customerMap);
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
		iLogger.info("Total execution time "
				+ String.valueOf(endTime - startTime) + "(ms)");

		return impl;
	}
	
	public CustomersUnderDealerImpl getCustomersForDealerForRest(int dealerTenancyId) {

		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Logger iLogger = InfoLoggerClass.logger;

		/*
		 * Query select t2.child_id,t1.tenancy_name,t4.tenancy_type_id from
		 * tenancy t1,tenancy_bridge t2, tenancy_type t4 where t2.parent_id in
		 * (301) and t4.tenancy_type_id in (3,4) and t2.child_id=t1.tenancy_id
		 * and t1.tenancy_type_id =t4.tenancy_type_id and t2.child_id not in
		 * (select distinct child_id from tenancy_bridge where child_id=301);
		 */
		CustomersUnderDealerImpl impl = new CustomersUnderDealerImpl();
		try {
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			
			List<Integer> tenancyIdList=new ArrayList<Integer>();
			
			tenancyIdList.add(dealerTenancyId);
			
			tenancyIdList.addAll(new DateUtil().getLinkedTenancyListForTheTenancy(tenancyIdList));
			
			ListToStringConversion conversion = new ListToStringConversion();
			String tenancyIdListString = conversion.getIntegerListString(tenancyIdList).toString();
			
			
			String queryString = "select t2.childId,t1.tenancy_name,t4.tenancy_type_id"
					+ " from TenancyEntity t1,TenancyBridgeEntity t2,TenancyTypeEntity t4"
					+ " where t2.parentId in ("
					+ tenancyIdListString
					+ ")"
					+ " and t4.tenancy_type_id in (3,4)  and t2.childId=t1.tenancy_id"
					+ " and t1.tenancy_type_id =t4.tenancy_type_id"
					+ " order by t1.tenancy_name asc ";
			//" order by t2.childId asc ";

			iLogger.info("Query : " + queryString);

			//DF23/03/2015 for alphabetical order of customer name

			LinkedHashMap<Integer, String> customerMap = null;
			Iterator itr = session.createQuery(queryString).list().iterator();
			Object[] result = null;
			int tenancyTypeId = 0;
			int tenancyId = 0;
			String tenancyName = null;

			while (itr.hasNext()) {
				result = (Object[]) itr.next();
				if (result[0] != null) {
					tenancyId = (Integer) result[0];
				}
				// get tenancy type id. if it is 3(dealer) .
				if (result[2] != null) {
					tenancyTypeId = (Integer) result[2];
				}
				if (tenancyTypeId == 3) {
					impl.setDealerID(tenancyId);
				} else if (tenancyTypeId == 4) { // 4= customer
					if (customerMap == null) {
						customerMap = new LinkedHashMap<Integer, String>();
					}
					if (result[1] != null) {
						tenancyName = (String) result[1];
						tenancyName = tenancyName.split("-")[0].trim();
					}
					customerMap.put(tenancyId, tenancyName);
				}
			}
			impl.setCustomerMap(customerMap);
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
		iLogger.info("Total execution time "
				+ String.valueOf(endTime - startTime) + "(ms)");

		return impl;
	}
	
	
	/**
	 * method to get the machine group and landmark details for a tenancyId
	 * 
	 * @param tenancyId
	 * @return MGLandMarkDetailsImpl
	 */
	public MGLandMarkDetailsImpl getMGLandMarkDetails(int tenancyId) {
		MGLandMarkDetailsImpl impl = new MGLandMarkDetailsImpl();
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Map<Integer, String> landmarkCategory = null;
		int landmarkCategoryId = 0, landmarkId = 0;
		String landMarkName = null;
		List<Landmark> lIds = null;
		Map<Integer, List<Landmark>> landMarkMap = new HashMap<Integer, List<Landmark>>();
		List<Integer> landmarkIds = null;
		try {
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			List<Integer> tenancyIdList=new ArrayList<Integer>();
			tenancyIdList.add(tenancyId);
			
			tenancyIdList.addAll(new DateUtil().getLinkedTenancyListForTheTenancy(tenancyIdList));
			
			ListToStringConversion conversion = new ListToStringConversion();
			String tenancyIdListString = conversion.getIntegerListString(tenancyIdList).toString();
			
			String query1 = "SELECT l1.Landmark_Category_ID,l1.Landmark_Category_Name,l2.Landmark_id,l2.Landmark_Name"
					+ " FROM LandmarkCategoryEntity l1, LandmarkEntity l2 WHERE l1.Tenancy_ID in("+ tenancyIdListString
					+ ") and l1.Landmark_Category_ID=l2.Landmark_Category_ID";

			iLogger.info("Query : " + query1);
			Iterator itr1 = session.createQuery(query1).list().iterator();
			Object[] result = null;
			while (itr1.hasNext()) {
				result = (Object[]) itr1.next();
				if (landmarkCategory == null) {
					landmarkCategory = new HashMap<Integer, String>();
				}
				if (landmarkIds == null) {
					landmarkIds = new ArrayList<Integer>();
				}
				if (result[0] != null) {
					landmarkCategoryId = (Integer) result[0];
				}
				if (result[2] != null) {
					landmarkId = (Integer) result[2];
				}
				if (result[3] != null) {
					landMarkName = (String) result[3];
				}
				if (!landmarkCategory.containsKey(landmarkCategoryId)) {
					landmarkCategory
					.put(landmarkCategoryId, (String) result[1]);
				}

				if (landMarkMap.containsKey(landmarkCategoryId)) {
					lIds = landMarkMap.get(landmarkCategoryId);
					Landmark ID = new Landmark();
					ID.setLandmarkID(landmarkId);
					ID.setLandmarkName(landMarkName);
					lIds.add(ID);
					landMarkMap.remove(landmarkCategoryId);
					landMarkMap.put(landmarkCategoryId, lIds);

				} else {
					List<Landmark> ids = new ArrayList<Landmark>();
					Landmark id = new Landmark();
					id.setLandmarkID(landmarkId);
					id.setLandmarkName(landMarkName);
					ids.add(id);
					landMarkMap.put(landmarkCategoryId, ids);
				}
			}
			iLogger.info("Execution time for fetching landmark details : "
					+ String.valueOf(System.currentTimeMillis() - startTime
							+ " (ms)"));
			impl.setLandmarkCategory(landmarkCategory);

			// impl.setLandmarkMap(landMarkMap);

			// query to get machine group type and machine group details
			query1 = "from CustomAssetGroupEntity t1 where t1.active_status=1 and t1.tenancy_id in("+ tenancyIdListString+")";
			CustomAssetGroupEntity cag = null;
			int level;
			int groupId, parentId = 0;
			;
			Map<Integer, List<MachineGroupDetails>> machineGroupMap = null;
			Query query = session.createQuery(query1);
			List list1 = query.list();
			itr1 = list1.iterator();

			List<MachineGroupDetails> machineGroupList = null;
			MachineGroupDetails mgdetails = null;
			Map<Integer, String> machineGroupTypeMap = new HashMap<Integer, String>();

			while (itr1.hasNext()) {
				cag = (CustomAssetGroupEntity) itr1.next();
				groupId = cag.getGroup_id();
				level = cag.getLevel();

				if (machineGroupMap == null) {
					machineGroupMap = new HashMap<Integer, List<MachineGroupDetails>>();
				}

				if (level == 1) {// its m/c group type
					if (!machineGroupMap.containsKey(groupId)) {
						machineGroupMap.put(groupId, null);
					}
					if (!machineGroupTypeMap.containsKey(groupId)) {
						machineGroupTypeMap.put(groupId, cag.getGroup_name());
					}
				} else if (level == 2) {
					if (cag.getAsset_group_type() != null) {
						parentId = cag.getAsset_group_type().getGroup_id();
					}
					if (!machineGroupMap.containsKey(parentId)) {
						machineGroupMap.put(parentId, null);
					}
					machineGroupList = machineGroupMap.get(parentId);

					if (machineGroupList == null) {
						machineGroupList = new ArrayList<MachineGroupDetails>();
					}

					mgdetails = new MachineGroupDetails();
					mgdetails.setMachineGroupName(cag.getGroup_name());
					mgdetails.setMachineGrouptId(groupId);
					machineGroupList.add(mgdetails);
					machineGroupMap.remove(parentId);
					machineGroupMap.put(parentId, machineGroupList);
				}

			}
			impl.setMachineGroupMap(machineGroupMap);
			impl.setMachineGroupTypeMap(machineGroupTypeMap);
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
		iLogger.info("Total execution time "
				+ String.valueOf(endTime - startTime) + "(ms)");

		return impl;

	}

	/**
	 * method to delete the user by making status as 0.
	 * 
	 * @param loginID
	 * @return String
	 * @throws CustomFault
	 */
	public String deleteUser(String loginID) throws CustomFault { 

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		String message = null;

		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Properties prop = new Properties();
		String OEMadmin=null;
		String CustomerFleetManager=null;
		String OEMHO=null;
		try {
			// DefectID:1822-----Smitha-----Only JCB Admin,JCB HO and CFM and if the user is tenancy admin and down the hierarchy if other JCB Admin,JCB HO and CFM exists then it sets the status as false.
			try {
				prop.load(getClass()
						.getClassLoader()
						.getResourceAsStream(
								"remote/wise/resource/properties/configuration.properties"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				fLogger.fatal("Exception:: " + e.getMessage());

			}

			OEMadmin = prop.getProperty("OEMadmin");
			OEMHO = prop.getProperty("OEMHO");
			CustomerFleetManager = prop.getProperty("CustomerFleetManager");

			// get account details
			String accountquery = "select account_id from AccountContactMapping where contact_id= '"
					+ loginID + "'";

			Iterator accountlist = session.createQuery(accountquery).list()
					.iterator();
			AccountEntity account = (AccountEntity) accountlist.next();
			account_id = account.getAccount_id();                       

			String contactquery = " from ContactEntity c where c.contact_id= '"
					+ loginID + "'";
			Iterator contactlist = session.createQuery(contactquery).list().iterator();                    
			String roleName=null;
			int roleID=0;
			int isTenancyAdmin=0;
			RoleEntity role = null;
			while(contactlist.hasNext()){
				ContactEntity contact = (ContactEntity) contactlist.next();
				if(contact!=null){
					role=contact.getRole();
					roleID=role.getRole_id();
					roleName=role.getRole_name();
					isTenancyAdmin=contact.getIs_tenancy_admin();
				}

			}
			if((roleName.equalsIgnoreCase(OEMadmin) && (isTenancyAdmin==1))|| (roleName.equalsIgnoreCase(OEMHO) && (isTenancyAdmin==1))||(roleName.equalsIgnoreCase(CustomerFleetManager) && (isTenancyAdmin==1))){
				String query = " from AccountContactMapping a,ContactEntity c where a.contact_id = c.contact_id and c.contact_id != '"
						+ loginID + "' and c.role = "+ roleID + "and a.account_id="+ account_id + " and c.is_tenancy_admin=1 ";
				int present=0;
				Iterator querylist = session.createQuery(query).list().iterator();
				Object[] result=null;
				while(querylist.hasNext()){
					result = (Object[])querylist.next();
					present=1;
				}
				if(present==1){
					Query queryString = session
							.createQuery("UPDATE ContactEntity SET active_status=false WHERE contact_id='"
									+ loginID + "'");
					int rows = queryString.executeUpdate();
					iLogger.info(rows + " got updated for " + loginID);
					if (rows > 0) {
						iLogger.info("User " + loginID
								+ " got deleted successfully !");
						message = "User " + loginID + " got deleted successfully !";
					} else {
						bLogger.error("User " + loginID + " does not exist !");
						message = "User " + loginID + " does not exist!";
						throw new CustomFault("User " + loginID + " does not exist!");
					}
				}
			}
			else if((!roleName.equalsIgnoreCase(OEMadmin)) || (!roleName.equalsIgnoreCase(OEMHO))||(!roleName.equalsIgnoreCase(CustomerFleetManager))){
				if(isTenancyAdmin==1){
					String query2=" from AccountContactMapping a,ContactEntity c where a.contact_id = c.contact_id and c.contact_id != '"
							+ loginID + "' and c.is_tenancy_admin=1 ";
					Iterator query2list = session.createQuery(query2).list().iterator();
					Object[] result=null; 
					int present=0;
					while(query2list.hasNext()){
						result = (Object[])query2list.next();
						present=1;
					}
					if(present==1){

						Query queryString = session
								.createQuery("UPDATE ContactEntity SET active_status=false WHERE contact_id='"
										+ loginID + "'");
						int rows = queryString.executeUpdate();
						iLogger.info(rows + " got updated for " + loginID);
						if (rows > 0) {
							iLogger.info("User " + loginID
									+ " got deleted successfully !");
							message = "User " + loginID + " got deleted successfully !";
						} else {
							bLogger.error("User " + loginID + " does not exist !");
							message = "User " + loginID + " does not exist!";
							throw new CustomFault("User " + loginID + " does not exist!");
						}

					}
				} else {

					Query queryString = session
							.createQuery("UPDATE ContactEntity SET active_status=false WHERE contact_id='"
									+ loginID + "'");
					int rows = queryString.executeUpdate();
					iLogger.info(rows + " got updated for " + loginID);
					if (rows > 0) {
						iLogger.info("User " + loginID
								+ " got deleted successfully !");
						message = "User " + loginID + " got deleted successfully !";
					} else {
						bLogger.error("User " + loginID + " does not exist !");
						message = "User " + loginID + " does not exist!";
						throw new CustomFault("User " + loginID + " does not exist!");
					}


				}

			}
			else {


				Query queryString = session
						.createQuery("UPDATE ContactEntity SET active_status=false WHERE contact_id='"
								+ loginID + "'");
				int rows = queryString.executeUpdate();
				iLogger.info(rows + " got updated for " + loginID);
				if (rows > 0) {
					iLogger.info("User " + loginID
							+ " got deleted successfully !");
					message = "User " + loginID + " got deleted successfully !";
				} else {
					bLogger.error("User " + loginID + " does not exist !");
					message = "User " + loginID + " does not exist!";
					throw new CustomFault("User " + loginID + " does not exist!");
				}                 

			}
			
			//DF20180731 - KO369761 - Deleting existing token ids for the user.
			new CommonUtil().deleteUserTokenIds(loginID);
			
		} finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}
			if (session.isOpen()) { 
				session.flush();
				session.close();
			}
		}
		//ended DefectID:1822-----Smitha----28th jan 2014
		long endTime = System.currentTimeMillis();
		iLogger.info("Total execution time "
				+ String.valueOf(endTime - startTime) + "(ms)");

		return message;

	}

	/*public String deleteUser(String loginID) throws CustomFault {
		String message = null;

		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Properties prop = new Properties();
		String OEMadmin=null;
		String CustomerFleetManager=null;
		String OEMHO=null;
		try {
				// DefectID:1822-----Smitha-----Only JCB Admin,JCB HO and CFM and if the user is tenancy admin and down the hierarchy if other JCB Admin,JCB HO and CFM exists then it sets the status as false.
			try {
				prop.load(getClass()
						.getClassLoader()
						.getResourceAsStream(
								"remote/wise/resource/properties/configuration.properties"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				fatalError.fatal("Exception:: " + e.getMessage());

			}

			OEMadmin = prop.getProperty("OEMadmin");
			OEMHO = prop.getProperty("OEMHO");
			CustomerFleetManager = prop.getProperty("CustomerFleetManager");

				// get account details
				String accountquery = "select account_id from AccountContactMapping where contact_id= '"
						+ loginID + "'";

				Iterator accountlist = session.createQuery(accountquery).list()
						.iterator();
				AccountEntity account = (AccountEntity) accountlist.next();
				account_id = account.getAccount_id();				

				String contactquery = " select a.account_id, c.contact_id, c.role,c.is_tenancy_admin from AccountContactMapping a ,ContactEntity c where a.contact_id = c.contact_id and c.contact_id= '"
						+ loginID + "'";
				Iterator contactlist = session.createQuery(contactquery).list().iterator();
				Object[] result=null;
				String roleName=null;
				int roleID=0;
				int isTenancyAdmin=0;
				while(contactlist.hasNext()){
					result = (Object[]) contactlist.next();
					if(result[2]!=null){
						RoleEntity roletemp = (RoleEntity)result[2];
						roleID=roletemp.getRole_id();
						roleName=roletemp.getRole_name();						
					}
					if(result[3]!=null){
						isTenancyAdmin=(Integer)result[3];
					}
				}

				if(roleName.equalsIgnoreCase(OEMadmin) && (isTenancyAdmin==1)){
					Query queryString = session
							.createQuery("UPDATE ContactEntity SET active_status=false WHERE contact_id='"
									+ loginID + "'");
					int rows = queryString.executeUpdate();
					infoLogger.info(rows + " got updated for " + loginID);
					if (rows > 0) {
						infoLogger.info("User " + loginID
								+ " got deleted successfully !");
						message = "User " + loginID + " got deleted successfully !";
					} else {
						businessError.error("User " + loginID + " does not exist !");
						message = "User " + loginID + " does not exist!";
						throw new CustomFault("User " + loginID + " does not exist!");
					}
				}
				else if(roleName.equalsIgnoreCase(OEMHO)&& (isTenancyAdmin==1)){
					Query queryString = session
							.createQuery("UPDATE ContactEntity SET active_status=false WHERE contact_id='"
									+ loginID + "'");
					int rows = queryString.executeUpdate();
					infoLogger.info(rows + " got updated for " + loginID);
					if (rows > 0) {
						infoLogger.info("User " + loginID
								+ " got deleted successfully !");
						message = "User " + loginID + " got deleted successfully !";
					} else {
						businessError.error("User " + loginID + " does not exist !");
						message = "User " + loginID + " does not exist!";
						throw new CustomFault("User " + loginID + " does not exist!");
					}
				}
				else if(roleName.equalsIgnoreCase(CustomerFleetManager)&& (isTenancyAdmin==1)){
					Query queryString = session
							.createQuery("UPDATE ContactEntity SET active_status=false WHERE contact_id='"
									+ loginID + "'");
					int rows = queryString.executeUpdate();
					infoLogger.info(rows + " got updated for " + loginID);
					if (rows > 0) {
						infoLogger.info("User " + loginID
								+ " got deleted successfully !");
						message = "User " + loginID + " got deleted successfully !";
					} else {
						businessError.error("User " + loginID + " does not exist !");
						message = "User " + loginID + " does not exist!";
						throw new CustomFault("User " + loginID + " does not exist!");
					}
				}
				else{
					if(isTenancyAdmin==1){
						Query queryString = session
						.createQuery("UPDATE ContactEntity SET active_status=false WHERE contact_id='"
								+ loginID + "'");
				int rows = queryString.executeUpdate();
				infoLogger.info(rows + " got updated for " + loginID);
				if (rows > 0) {
					infoLogger.info("User " + loginID
							+ " got deleted successfully !");
					message = "User " + loginID + " got deleted successfully !";
				} else {
					businessError.error("User " + loginID + " does not exist !");
					message = "User " + loginID + " does not exist!";
					throw new CustomFault("User " + loginID + " does not exist!");
				}	
				}	
			}

				// DefectID:1822---ended.
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
		infoLogger.info("Total execution time "
				+ String.valueOf(endTime - startTime) + "(ms)");

		return message;

	}*/


	/**
	 * method to get the model map
	 * 
	 * @return HashMap of model details
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<Integer, String> getModelMap() {
		Logger iLogger = InfoLoggerClass.logger;

		HashMap<Integer, String> modelMap = null;
		AssetTypeEntity ate = null;
		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			Query query = session
					.createQuery("FROM AssetTypeEntity order by asset_type_id asc");
			Iterator iter = query.list().iterator();
			while (iter.hasNext()) {
				ate = (AssetTypeEntity) iter.next();
				if (modelMap == null) {
					modelMap = new HashMap<Integer, String>();
				}
				if (ate.getAsset_type_id() != 0) {
					modelMap.put(ate.getAsset_type_id(),
							ate.getAsset_type_name());
				}
			}
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
		iLogger.info("Total execution time "
				+ String.valueOf(endTime - startTime) + "(ms)");
		return modelMap;
	}

	public String sendMailToUser(String primaryEmailId, String LoginId,
			String password) {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		String message = null;
		try {
			//get LiveLink URL Details
			Properties prop = new Properties();
			String liveLink_URL=null;

			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));

			if (prop.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_SIT");
			} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_DEV");
			} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_QA");
			} else if (prop.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
				liveLink_URL= prop.getProperty("LiveLink_URL_PROD");
			} else {
				liveLink_URL= prop.getProperty("LiveLink_URL");
			}

			//			liveLink_URL= prop.getProperty("LiveLink_URL");
			String subject = "User registration with JCB LiveLink";
			StringBuffer body = new StringBuffer();
			body.append("Hello " + LoginId + ",\n\n");
			body.append("Thanks for registering with JCB. \n");
			body.append("Please find the registration details below :\n\n");
			body.append("User Name : " + LoginId + "\n");
			body.append("Password  : " + password + "\n\n");
			//			Keerthi : 19/12/2013 : ID : 1847  : application spelling mistake
			body.append("Please use these credentials when you log in to LiveLink application.\n");
			//			Keerthi : 17/12/2013 : ID : 1833 : URL is taken from properties file
			body.append("Application URL : "+liveLink_URL+" \n\n");

			//			Juhi : 17/01/2014 : Include a note on browser compatibility 
			body.append("On logging in, Please refer to Help section -> Browser compatibility on the list of supported browsers for Livelink website. \n\n");
			body.append("With regards, \n");
			body.append("JCB LiveLink Team.");

			EmailTemplate emailTemplate = new EmailTemplate(primaryEmailId,
					subject, body.toString(), null);
			//new EmailHandler().handleEmail("jms/queue/emailQ", emailTemplate, 0);
			//DF20171016 - KO369761 : Changed EmailQ Service from Hornet to Kafka Queue.
			new EmailHandler().handleEmailInKafka("EmailQueue", emailTemplate, 0);
			message = "Email with login details has been sent to user !";
			iLogger.info("Email with login details has been sent to user !");
		} catch (Exception e) {
			message = "Failed while sending email to user !!";
			fLogger.error("Failed while sending email to user !!");
			e.printStackTrace();
		}
		return message;
	}

	public String sendSMSToUser(String mobileNumber, String country, String LoginId,String password) throws CustomFault {
		String message = null;
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		List<String> toList = new ArrayList<String>();
		if(country !=null){
			country = new CommonUtil().getCountryCode(country);
		}	
		else{
			country = "";
		}
		toList.add(country+mobileNumber);

		//DF20160511 @Roopa Adding msg to the msg body.
		String body = "Thanks for registering with JCB. Pls login to jcblivelink.in with Login ID: "+LoginId+" Password: "+password;

		body = body + " . JCB LiveLink Team.";
		List<String> msgBody = new ArrayList<String>();
		msgBody.add(body);
		boolean isRegStatus = false;
		String ResetService="Registration SMS";
		SmsTemplate smsTemplate = new SmsTemplate();			
		smsTemplate.setTo(toList);
		smsTemplate.setMsgBody(msgBody);
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try{

			Query query = session.createQuery("select a.isStatus from ConfigAppEntity a where a.services='"+ResetService+"'");
			Iterator itr1=query.list().iterator();
			while(itr1.hasNext()){
				isRegStatus = (Boolean) itr1.next();				
			}
			iLogger.info ("registration status from DB : "+ isRegStatus);
			if(isRegStatus==true)
			{
				iLogger.info ("sending SMS : START");
				//new SmsHandler().handleSms("jms/queue/smsQ", smsTemplate,0);
				
				//DF20171016 - KO369761 : Changed SMSQ Service to Kafka Queue.
				new SmsHandler().handleSmsInKafka("SMSQueue", smsTemplate,0);
				message ="Registration details successfully sent to registered mobile number."; 
				iLogger.info("SMS with Registration details has been sent to user !");
			}
			else
			{
				bLogger.error("SMS with registration details cannot be sent : SMS Service turned OFF");
				message ="SMS with registration details cannot be sent : SMS Service turned OFF";
			}
		}
		catch (Exception e) {
			message ="Failed while sending SMS to user !!";
			fLogger.error("Failed while sending Registration details SMS to user !!" + e.getMessage());	
			throw new remote.wise.exception.CustomFault("Problem while sending SMS. Please try again after some time.");
		}
		return message;
	}

	public int getTenancyAdminCount() {
		return tenancyAdminCount;
	}

	public void setTenancyAdminCount(int tenancyAdminCount) {
		this.tenancyAdminCount = tenancyAdminCount;
	}

	//	method to check whether phone no. already exists for user
	public boolean mobileNumberExists(String mobileNumber){

		Logger bLogger = BusinessErrorLoggerClass.logger;
		boolean exists = false;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try{
			if(mobileNumber!=null && mobileNumber!="" && (mobileNumber.replaceAll("\\s","").length()>0)){
				Query query = session.createQuery("from ContactEntity where primary_mobile_number ='"
						+ mobileNumber
						+ "'and active_status=true");
				List resultList = query.list();
				if (resultList.size() > 0){

					exists = true;
					bLogger
					.error(" Phone Number already exists. Please provide diff no.");

					throw new CustomFault(
							"Phone Number already exists. Please provide diff no.");

				}
			}

		}
		catch(Exception e){
			e.printStackTrace();
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
		return exists;
	}

	//	method to check whether email id already exists for user
	public boolean emailIDExists(String emailID){

		Logger bLogger = BusinessErrorLoggerClass.logger;
		boolean exists = false;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try{
			//	        	Keerthi : 18/09/14 : checking email for empty string
			if(emailID!=null && emailID!="" && (emailID.replaceAll("\\s","").length()>0)){
				Query query = session.createQuery("from ContactEntity where primary_email_id ='"
						+ emailID
						+ "'and active_status=true");
				List resultList = query.list();
				if (resultList.size() > 0){

					exists = true;
					bLogger
					.error(" Email ID already exists. Please provide diff id.");

					throw new CustomFault(
							"Email ID already exists. Please provide diff id.");
				}
			}        	
		}
		catch(Exception e){
			e.printStackTrace();
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
		return exists;
	}

	//DF20150827 - sureshs
	//method to return all the supported languages for multi lingual SMS
	public List<String> getLanguages() {
		// TODO Auto-generated method stub
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		LanguagesEntity language = null;
		List<String> languagesList = new LinkedList<String>();
		try{
			Query languagesQuery = session.createQuery("from LanguagesEntity order by language");
			Iterator itr = languagesQuery.list().iterator();
			while(itr.hasNext())
			{
				language = (LanguagesEntity) itr.next();
				languagesList.add(language.getLanguage());
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		finally
		{
			if(session.isOpen())
			{
				session.flush();
				session.close();
			}

		}
		return languagesList;
	}

	//method to track the language preference settings 
	//DF20150827 - suresh - trackig the language preference settings
	public void setLanguageSettingsTrack(ContactEntity loginId, String old_lang,
			String language) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
		Calendar cal = Calendar.getInstance();
		String current_date = sdf.format(cal.getTime());
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try{
			LanguageTrackingEntity newLanguage = new LanguageTrackingEntity();
			newLanguage.setLoginId(loginId);
			newLanguage.setOld_lang(old_lang);
			newLanguage.setNew_lang(language);
			newLanguage.setLang_set_date(current_date);
			session.save(newLanguage);
		}catch(Exception e){
			e.printStackTrace();
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
	}


	/*Strat report filter new SErvices @Roopa DF20160317*/


	/**
	 * method to get the cities from DB2
	 * 
	 * @return List<CityImpl>
	 * @throws CustomFault
	 */

	public List<CityImpl> getCities(String stateId) throws CustomFault {

		String cityQuery=null;
		if(stateId.isEmpty() || stateId==null || stateId.equalsIgnoreCase("null")){
			cityQuery="select * from City";	
		}
		else{
			//int stId=Integer.parseInt(stateId);	
			cityQuery="select * from City where StateID in ("+stateId+")";	
		}

		List<CityImpl> respList = new ArrayList<CityImpl>();

		CityImpl implObj=null;

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		long startTime = System.currentTimeMillis();

		Connection db2Connection = null;
		Statement st=null;
		ResultSet rs=null;

		iLogger.info("getting proddb2 connection");
		try{
			db2Connection = new ConnectMySQL().getProdDb2Connection();
			iLogger.info("connected to proddb2 connection");



			try {
				st = db2Connection.createStatement();
				//System.out.println("stateid--"+stateId);
				//System.out.println("cityQuery----"+cityQuery);
				iLogger.info("stateId----"+stateId);
				iLogger.info("cityQuery----"+cityQuery);

				rs = st.executeQuery(cityQuery);

				while(rs.next()){
					implObj=new CityImpl();
					int cityID = rs.getInt("CityID");
					String CityName = rs.getString("CityName");
					implObj.setCityId(cityID);
					implObj.setCityName(CityName);
					respList.add(implObj);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				fLogger.fatal("City service SQL exception::"+e);
			}



		}
		catch(Exception e){
			fLogger.fatal("City service exception::"+e);

		}
		finally{

			iLogger.info("City service:DB2 Close Connection - START");
			try
			{
				if(rs!=null)
					rs.close();

				if(st!=null)
					st.close();

				if(db2Connection!=null)
					db2Connection.close();
			}
			catch(Exception e)
			{
				iLogger.info("Problem in closing the connection");
				//e.printStackTrace();
				fLogger.fatal("City service::"+"Exception in closing DB2 Connection:"+e.getMessage());
			}
			iLogger.info("City service:DB2 Close Connection - END");

		}

		long endTime = System.currentTimeMillis();
		iLogger.info("Total execution time getEventTypes"
				+ String.valueOf(endTime - startTime) + "(ms)");

		return respList;

	}

	/**
	 * method to get the states from DB2
	 * 
	 * @return List<StateImpl>
	 * @throws CustomFault
	 */

	public List<StateImpl> getStates() throws CustomFault {

		List<StateImpl> respList = new ArrayList<StateImpl>();

		StateImpl implObj=null;

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		long startTime = System.currentTimeMillis();

		Connection db2Connection = null;
		Statement st=null;
		ResultSet rs=null;

		iLogger.info("getting proddb2 connection");
		try{
			db2Connection = new ConnectMySQL().getProdDb2Connection();
			iLogger.info("connected to proddb2 connection");



			try {
				if(db2Connection.isClosed())
					db2Connection = new ConnectMySQL().getProdDb2Connection();
				st = db2Connection.createStatement();
				rs = st.executeQuery("select * from State");

				while(rs.next()){
					implObj=new StateImpl();
					int stateID = rs.getInt("StateID");
					String StateName = rs.getString("StateName");
					implObj.setStateId(stateID);
					implObj.setStateName(StateName);
					respList.add(implObj);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				fLogger.fatal("State service SQL exception::"+e);
			}

		}
		catch(Exception e){
			fLogger.fatal("State service exception::"+e);

		}
		finally{

			iLogger.info("State service:DB2 Close Connection - START");
			try
			{
				if(rs!=null)
					rs.close();

				if(st!=null)
					st.close();

				if(db2Connection!=null)
					db2Connection.close();
			}
			catch(Exception e)
			{
				iLogger.info("Problem in closing the connection");
				//e.printStackTrace();
				fLogger.fatal("State service::"+"Exception in closing DB2 Connection:"+e.getMessage());
			}
			iLogger.info("State service:DB2 Close Connection - END");



		}

		long endTime = System.currentTimeMillis();
		iLogger.info("Total execution time getEventTypes"
				+ String.valueOf(endTime - startTime) + "(ms)");



		return respList;

	}

	/**
	 * method to get event types
	 * 
	 * @return List<EventTypeImpl>
	 * @throws CustomFault
	 */

	public List<EventTypeImpl> getEventTypes() throws CustomFault {

		List<EventTypeImpl> respList = new ArrayList<EventTypeImpl>();

		EventTypeImpl implObj=null;

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try{


			Query eventTypeQuery = session.createQuery("from EventTypeEntity");

			Iterator evtItr = eventTypeQuery.list().iterator();

			while(evtItr.hasNext())
			{
				implObj=new EventTypeImpl();
				EventTypeEntity evt = (EventTypeEntity)evtItr.next();
				implObj.setEventTypeId(evt.getEventTypeId());
				implObj.setEventTypeName(evt.getEventTypeName());

				respList.add(implObj);

			}

		}
		catch (Exception e) {
			fLogger.fatal("Event Type Service Exception:: " + e.getMessage());

		}

		finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}
			if (session.isOpen()) {
				session.flush();
				session.close();
			}
		}
		long endTime = System.currentTimeMillis();
		iLogger.info("Total execution time getEventTypes"
				+ String.valueOf(endTime - startTime) + "(ms)");


		return respList;

	}

	/**
	 * method to get the Event names for the given event type id
	 * @param eventTpeId
	 * @return List<EventNameImpl>
	 * @throws CustomFault
	 */
	public List<EventNameImpl> getEventNames(List<Integer> eventTpeId) throws CustomFault {


		List<EventNameImpl> respList = new ArrayList<EventNameImpl>();

		EventNameImpl implObj=null;

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		ListToStringConversion conversion = new ListToStringConversion();

		String eventTypeIdList=null;
		String evtQuery=null;

		HashMap<Integer,String> eventMap=null;

		if(eventTpeId!=null){
			if(eventTpeId.size()!=0){

				eventTypeIdList = conversion.getIntegerListString(eventTpeId).toString(); 
			}
		}

		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try{
			if(eventTypeIdList!=null){

				evtQuery="from EventEntity where eventTypeId.eventTypeId in ("+eventTypeIdList+")";
			}
			else{

				evtQuery="from EventEntity";
			}


			Query eventTypeQuery = session.createQuery(evtQuery);

			Iterator evtItr = eventTypeQuery.list().iterator();

			while(evtItr.hasNext())
			{

				implObj=new EventNameImpl();
				EventEntity evt = (EventEntity)evtItr.next();
				implObj.setEventTypeId(evt.getEventTypeId().getEventTypeId());
				implObj.setEventId(evt.getEventId());
				implObj.setEventName(evt.getEventName());
				respList.add(implObj);

			}

		}
		catch (Exception e) {
			fLogger.fatal("Event Name Service Exception:: " + e.getMessage());

		}

		finally {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}
			if (session.isOpen()) {
				session.flush();
				session.close();
			}
		}
		long endTime = System.currentTimeMillis();
		iLogger.info("Total execution time getEventTypes"
				+ String.valueOf(endTime - startTime) + "(ms)");


		return respList;



	}

	/**
	 * method to get customers for a dealer
	 * 
	 * @param dealerTenancyId
	 * @return CustomersUnderDealerImpl
	 */

	public CustomersCodeUnderDealerCodeImpl getCustomersCodeForDealer(List<String> dealerCode) {

		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Logger iLogger = InfoLoggerClass.logger;

		List<Integer> accountIdList = null;
		String accountIdStringList=null;
		List<Integer> dealerTenancyId=null;
		String dealerTenancyIdStringList=null;

		String custAccountCode=null;
		String custAccountName=null;

		String dealerCodeList=null;

		ListToStringConversion conversion = new ListToStringConversion();

		if( ! (dealerCode==null || dealerCode.isEmpty())) 
		{
			dealerCodeList = conversion.getStringList(dealerCode).toString(); 
		}


		CustomersCodeUnderDealerCodeImpl impl = new CustomersCodeUnderDealerCodeImpl();

		//String dealerAccquery="select ae.account_id from AccountEntity ae where ae.accountCode='"+dealerCode+"'";
		
		//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code

		//String dealerAccquery="select ae.account_id from AccountEntity ae where ae.accountCode in ("+dealerCodeList+")";
		
		String dealerAccquery="select ae.account_id from AccountEntity ae where ae.mappingCode in ("+dealerCodeList+")";

		Iterator itr1 = session.createQuery(dealerAccquery).list().iterator();

		accountIdList=new LinkedList<Integer>();
		while (itr1.hasNext()) {
			int acc=(Integer)itr1.next();
			//System.out.println("Account Id :"+acc);
			accountIdList.add(acc);
		}
		accountIdStringList=conversion.getIntegerListString(accountIdList).toString();

		//String dealerTenancyquery="select am.tenancy_id from AccountTenancyMapping am where am.account_id.account_id="+accountId+"";

		String dealerTenancyquery="select am.tenancy_id from AccountTenancyMapping am where am.account_id.account_id in ("+accountIdStringList+")";

		Iterator itr2 = session.createQuery(dealerTenancyquery).list().iterator();

		dealerTenancyId=new LinkedList<Integer>();
		while (itr2.hasNext()) {
			TenancyEntity dealerTenancy=(TenancyEntity) itr2.next();
			dealerTenancyId.add(dealerTenancy.getTenancy_id());

		}

		dealerTenancyIdStringList=conversion.getIntegerListString(dealerTenancyId).toString();


		try {
			String queryString = "select t2.childId,t1.tenancy_name,t4.tenancy_type_id, at.account_id"
					+ " from TenancyEntity t1,TenancyBridgeEntity t2,TenancyTypeEntity t4, AccountTenancyMapping at"
					+ " where t2.parentId in ("
					+ dealerTenancyIdStringList
					+ ")"
					+ " and t4.tenancy_type_id in (3,4)  and t2.childId=t1.tenancy_id"
					+ " and t1.tenancy_type_id =t4.tenancy_type_id"
					+ " and at.tenancy_id.tenancy_id =t1.tenancy_id"
					+ " order by t1.tenancy_name asc ";


			iLogger.info("Query : " + queryString);

			LinkedHashMap<String, String> customerMap = null;
			Iterator itr = session.createQuery(queryString).list().iterator();
			Object[] result = null;
			int tenancyTypeId = 0;


			while (itr.hasNext()) {
				result = (Object[]) itr.next();
				if (result[0] != null) {
					tenancyId = (Integer) result[0];
				}
				// get tenancy type id. if it is 3(dealer) .
				if (result[2] != null) {
					tenancyTypeId = (Integer) result[2];
				}
				if (tenancyTypeId == 3) {
					impl.setDealerTCode(dealerCode);
				} else if (tenancyTypeId == 4) { // 4= customer
					if (customerMap == null) {
						customerMap = new LinkedHashMap<String, String>();
					}
					if (result[3] != null) {
						//custAccountCode=((AccountEntity) result[3]).getAccountCode();
						//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
						custAccountCode=((AccountEntity) result[3]).getMappingCode();
						
						custAccountName=((AccountEntity) result[3]).getAccount_name();
					}
					customerMap.put(custAccountCode, custAccountName);
				}
			}
			impl.setCustomerMap(customerMap);
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
		iLogger.info("Total execution time "
				+ String.valueOf(endTime - startTime) + "(ms)");

		return impl;
	}

	/**
	 * method to get all dealers under all zone(s) for logged-in user's tenancy
	 * id
	 * 
	 * @param loginId
	 * @param loginTenancyId
	 * @return DealersUnderZoneImpl
	 */

	public ZonalAccountCodeImpl getZonalAccount(String loginId,
			int loginTenancyId) {

		Logger iLogger = InfoLoggerClass.logger;

		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		ZonalAccountCodeImpl impl = new ZonalAccountCodeImpl();
		
		

		try {
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			ListToStringConversion conversion = new ListToStringConversion();
			List<Integer> tenancyIdList=new ArrayList<Integer>();
			tenancyIdList.add(loginTenancyId);
			
			tenancyIdList.addAll(new DateUtil().getLinkedTenancyListForTheTenancy(tenancyIdList));
			
			String tenancyIdListAsString = conversion.getIntegerListString(tenancyIdList).toString();
			
			
			String queryString = "select t2.childId,t1.tenancy_name,t4.tenancy_type_id,t1.parent_tenancy_id,t1.parent_tenancy_name, at.account_id"
					+ " from TenancyEntity t1,TenancyBridgeEntity t2, TenancyTypeEntity t4, AccountTenancyMapping at"
					+ " where t2.parentId in ("
					+ tenancyIdListAsString
					+ ") "
					+ " and t4.tenancy_type_id in (2,3)  and t2.childId=t1.tenancy_id"
					+ " and t1.tenancy_type_id =t4.tenancy_type_id"
					+ " and at.tenancy_id.tenancy_id =t1.tenancy_id"
					+ " order by t2.childId asc ";

			iLogger.info("Query : " + queryString);


			Object[] result = null;
			int tenancyTypeId = 0;
			int tenancyID = 0;
			String zoneName = null;
			int parentID = 0;
			String parentName = null;

			String parentCode=null;
			String parentCodeName=null;

			String zonalAccountCode=null;
			String zonalAccountName=null;


			String dealerAccountCode=null;
			String dealerAccountName=null;

			TreeMap<Integer, String> zoneMap = new TreeMap<Integer, String>();
			HashMap<String,String> zoneCodeMap=new HashMap<String,String>();

			TreeMap<Integer, String> dealerMap = null;
			TreeMap<String, String> dealerCodeMap = null;

			Map<Integer, TreeMap<Integer, String>> zoneDealerMap = new TreeMap<Integer, TreeMap<Integer, String>>();

			Map<String, TreeMap<String, String>> zoneDealerCodeMap = new TreeMap<String, TreeMap<String, String>>();

			Iterator itr = session.createQuery(queryString).list().iterator();

			while (itr.hasNext()) {
				result = (Object[]) itr.next();
				// get tenancy type id. if it is 2(zone) and to map
				if (result[2] != null) {
					tenancyTypeId = (Integer) result[2];
				}
				if (tenancyTypeId == 2) {
					if (result[0] != null) {
						tenancyID = (Integer) result[0];
					}
					if (result[1] != null) {
						zoneName = (String) result[1];
					}
					if (result[5] != null) {
						//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
						//zonalAccountCode=((AccountEntity) result[5]).getAccountCode();
						zonalAccountCode=((AccountEntity) result[5]).getMappingCode();
						zonalAccountName=((AccountEntity) result[5]).getAccount_name();
						zoneCodeMap.put(zonalAccountCode, zonalAccountName);
					}

					zoneDealerCodeMap.put(zonalAccountCode, null);

					zoneMap.put(tenancyID, zoneName);
					zoneDealerMap.put(tenancyID, null);
				} 

				else if (tenancyTypeId == 3) {
					if (result[0] != null) {
						tenancyID = (Integer) result[0];
					}
					if (result[5] != null) {
						//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
						//dealerAccountCode=((AccountEntity) result[5]).getAccountCode();
						dealerAccountCode=((AccountEntity) result[5]).getMappingCode();
						dealerAccountName=((AccountEntity) result[5]).getAccount_name();
					}
					if (result[3] != null) {
						parentID = ((TenancyEntity) result[3]).getTenancy_id();
					}
					if (result[4] != null) {
						parentName = (String) result[4];
					}

					Query parentCodeQuery = session.createQuery("select at.account_id from AccountTenancyMapping at where at.tenancy_id.tenancy_id="+parentID+"");

					Iterator parentCodefItr = parentCodeQuery.list().iterator();

					while(parentCodefItr.hasNext())
					{
						AccountEntity acode = (AccountEntity)parentCodefItr.next();
						//parentCode=acode.getAccountCode();
						parentCode=acode.getMappingCode();
						parentCodeName=acode.getAccount_name();
					}


					// if map contains already zone id, remove list and add this
					// and then add.
					if (zoneDealerCodeMap.containsKey(parentCode)) {
						dealerCodeMap = zoneDealerCodeMap.get(parentCode);
						//Sai Divya : 20250409 : niti231894- is not able to download Reports
						if(dealerAccountCode==null || dealerAccountCode.isEmpty())
						{
					        iLogger.error("dealerAccountCode is null or empty. Skipping insertion.");
					        continue;
						}
						if(dealerAccountName==null || dealerAccountName.isEmpty())
						{
							iLogger.error("dealerAccountName is null or empty. Skipping insertion.");
							continue;
						}
						if (dealerCodeMap == null || dealerCodeMap.isEmpty()) {
							dealerCodeMap = new TreeMap<String, String>();
						}
						dealerCodeMap.put(dealerAccountCode, dealerAccountName);
						zoneDealerCodeMap.remove(parentCode);
						
					} else {
						// zone id (parent)does not exist in map. create key for
						// parent
						dealerCodeMap = new TreeMap<String, String>();
						dealerCodeMap.put(dealerAccountCode, dealerAccountName);

					}
					zoneDealerCodeMap.put(parentCode, dealerCodeMap);


					/*if (zoneDealerMap.containsKey(parentID)) {
								dealerMap = zoneDealerMap.get(parentID);
								if (dealerMap == null) {
									dealerMap = new TreeMap<Integer, String>();
								}
								dealerMap.put(tenancyID, (String) result[1]);
								zoneDealerMap.remove(parentID);

							} else {

								dealerMap = new TreeMap<Integer, String>();
								dealerMap.put(tenancyID, (String) result[1]);

							}
							zoneDealerMap.put(parentID, dealerMap);*/
				}
			}

			impl.setZoneMap(zoneCodeMap);
			impl.setZoneDealerMap(zoneDealerCodeMap);

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
		iLogger.info("Total execution time "
				+ String.valueOf(endTime - startTime) + "(ms)");
		return impl;
	}

	/**
	 * method to get the model code map
	 * 
	 * @return HashMap of model code details
	 */
	@SuppressWarnings("rawtypes")
	public ModelCodeResponseContract getModelCodeMap() {
		Logger iLogger = InfoLoggerClass.logger;

		HashMap<String, String> modelCodeMap = null;

		ModelCodeResponseContract respObj = new ModelCodeResponseContract();
		AssetTypeEntity ate = null;
		long startTime = System.currentTimeMillis();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			Query query = session
					.createQuery("FROM AssetTypeEntity order by asset_type_id asc");
			Iterator iter = query.list().iterator();
			while (iter.hasNext()) {
				ate = (AssetTypeEntity) iter.next();
				if (modelCodeMap == null) {
					modelCodeMap = new HashMap<String, String>();
				}
				if (ate.getAsset_type_id() != 0) {
					modelCodeMap.put(ate.getAsset_type_name(),ate.getAssetTypeCode());
				}
			}

			//System.out.println("modelCodeMap size :"+modelCodeMap.size());

			respObj.setModelCodeMap(modelCodeMap);
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
		iLogger.info("Total execution time "
				+ String.valueOf(endTime - startTime) + "(ms)");
		return respObj;
	}


	/*End report filter new SErvices @Roopa DF20160317*/



	//20200220--Ramu B getting tenancy id
	public List<Integer> getTenancyIds(String accountId) {
		
			Logger bLogger = BusinessErrorLoggerClass.logger;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<Integer> tenancyidList=new LinkedList();
			
			try{
				SQLQuery query = session.createSQLQuery("select * from account_tenancy where account_id in(select account_id from account_contact where contact_id=?)");
				query.setString(0, accountId);
				List<Object[]> itr = query.list();
				for (Object[] row : itr) {
					tenancyidList.add(Integer.parseInt(row[1].toString()));
				}
				
			} catch(Exception e){
				e.printStackTrace();
			}
			finally
			{
				if(session.isOpen())
				{
					session.flush();
					session.close();
				}

			}
			return tenancyidList;
		}
	
	/**
	 * method to get the UserRole
	 * @param accountId
	 * @return roleName
	 */
	//20200220--Ramu B getting UserRole for user id
	public String getUserRole(String accountId) {
			Logger bLogger = BusinessErrorLoggerClass.logger;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			String roleName=null; 			
			try{
				//SQLQuery query  = session.createSQLQuery("select  a.role_name from role a left join contact b on a.role_id=b.role_id where b.Contact_ID=?");
				SQLQuery query = session.createSQLQuery("select role_name from role where role_id in (select role_id from contact where contact_id=?)");
				query.setString(0, accountId);
				Iterator itr = query.list().iterator();
				while(itr.hasNext())
				{
					roleName=(String)itr.next();
				}
				
			} catch(Exception e){
				e.printStackTrace();
			}
			finally
			{
				if(session.isOpen())
				{
					session.flush();
					session.close();
				}

			}
			return roleName;
		}


}
