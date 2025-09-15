package remote.wise.businessobject;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import remote.wise.businessentity.AccountContactMapping;
import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AccountMapping;
import remote.wise.businessentity.AccountTenancyMapping;
import remote.wise.businessentity.AddressEntity;
import remote.wise.businessentity.ClientEntity;
import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.CountryCodesEntity;
import remote.wise.businessentity.PartnerRoleEntity;
import remote.wise.businessentity.PartnershipMapping;
import remote.wise.businessentity.RoleEntity;
import remote.wise.businessentity.TenancyBridgeEntity;
import remote.wise.businessentity.TenancyDelegationEntity;
import remote.wise.businessentity.TenancyEntity;
import remote.wise.businessentity.TenancyTypeEntity;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.TenancyCreationReqContract;
import remote.wise.service.implementation.DomainServiceImpl;
import remote.wise.service.implementation.PendingTenancyCreationImpl;
import remote.wise.service.implementation.TenancyDetailsImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;

public class TenancyBO extends BaseBusinessObject 
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("TenancyBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("TenancyBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("TenancyBO:","info"); */

	int tenancyId;
	String tenancyName;
	int parentTenancyId;
	String parentTenancyName;
	String createdBy;
	String createdDate;
	String operatingStartTime;
	String operatingEndTime;
	//DF20190516: Anudeep adding count to implement pagination
	int size;
	//HashMap<Integer,String> tenancy_ID_Name = new HashMap<Integer,String>();
	//DefectId: 839 - Rajani Nagaraju - Handle Machine Movement between tenancies.
	private String tenancyCode;

	private String accountName;

	List<String> tenancyAdminList= new LinkedList<String>();



	public List<String> getTenancyAdminList() {
		return tenancyAdminList;
	}
	public void setTenancyAdminList(List<String> tenancyAdminList) {
		this.tenancyAdminList = tenancyAdminList;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getTenancyCode() {
		return tenancyCode;
	}
	public void setTenancyCode(String tenancyCode) {
		this.tenancyCode = tenancyCode;
	}

	TreeMap<String, TreeMap<Integer,String>> tenancyNameIDProxyUser = new TreeMap<String, TreeMap<Integer,String>>();
	private Properties prop;	

	public TreeMap<String, TreeMap<Integer, String>> getTenancyNameIDProxyUser() {
		return tenancyNameIDProxyUser;
	}
	public void setTenancyNameIDProxyUser(
			TreeMap<String, TreeMap<Integer, String>> tenancyNameIDProxyUser) {
		this.tenancyNameIDProxyUser = tenancyNameIDProxyUser;
	}
	public int getTenancyId() {
		return tenancyId;
	}
	public void setTenancyId(int tenancyId) {
		this.tenancyId = tenancyId;
	}
	
	//DF20190516: Anudeep adding count to implement pagination
	public int getSize() {
		return size;
	}
	//DF20190516: Anudeep adding count to implement pagination
	public void setSize(int size) {
		this.size = size;
	}
	
	

	public String getTenancyName() {
		return tenancyName;
	}
	public void setTenancyName(String tenancyName) {
		this.tenancyName = tenancyName;
	}

	public int getParentTenancyId() {
		return parentTenancyId;
	}
	public void setParentTenancyId(int parentTenancyId) {
		this.parentTenancyId = parentTenancyId;
	}

	public String getParentTenancyName() {
		return parentTenancyName;
	}
	public void setParentTenancyName(String parentTenancyName) {
		this.parentTenancyName = parentTenancyName;
	}


	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getOperatingStartTime() {
		return operatingStartTime;
	}
	public void setOperatingStartTime(String operatingStartTime) {
		this.operatingStartTime = operatingStartTime;
	}

	public String getOperatingEndTime() {
		return operatingEndTime;
	}
	public void setOperatingEndTime(String operatingEndTime) {
		this.operatingEndTime = operatingEndTime;
	}



	//*********************************************** get tenancy Entity for a given TenancyId********************************************************
	public TenancyEntity getTenancyObj(int tenancy_id)
	{
		TenancyEntity tenancyObj=new TenancyEntity(tenancy_id);

		//to check whether the record with given tenancy id exists
		if(tenancyObj.getTenancy_id()== 0)
			return null;
		else
			return tenancyObj; 
	}



	//*********************************************** get tenancy Entity List for a given TenancyId List********************************************************
	/** This method returns the TenancyEntity List for the given list of TenancyId
	 * @param tenancy_id list of tenancyId as integer input
	 * @return Returns the List of tenancyEntity
	 */
	public List<TenancyEntity> getTenancyObjList(List<Integer> tenancyId)
	{
		List<TenancyEntity> tenancyEntityList = new LinkedList<TenancyEntity>();

		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{
			//get Client Details
			Properties prop = new Properties();
			String clientName=null;

			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");

			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details		
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			Query q = session.createQuery("from TenancyEntity where client_id="+clientEntity.getClient_id()+" and tenancy_id in (:list)").setParameterList("list", tenancyId);
			Iterator itr=q.list().iterator();
			while(itr.hasNext())
			{
				TenancyEntity tenancyObj= (TenancyEntity)itr.next();
				tenancyEntityList.add(tenancyObj);
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

		return tenancyEntityList;
	}

	//*********************************************** END of get tenancy Entity List for a given TenancyId List********************************************************

	//******************************************************get AccountEntity for a given accountId*****************************************
	public AccountEntity getAccountObj(int accountId)
	{
		AccountEntity accountObj=new AccountEntity(accountId);

		//to check whether the record with given account id exists
		if(accountObj.getAccount_id()== 0)
			return null;
		else
			return accountObj; 
	}


	//******************************************************get AccountEntity for a given account Name *****************************************
	public AccountEntity getAccountObj(String accountName)
	{
		AccountEntity accountEntity = null;

		//	Logger businessError = Logger.getLogger("businessErrorLogger");
		//    Logger fatalError = Logger.getLogger("fatalErrorLogger");

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

		try{
			//get Client Details
			Properties prop = new Properties();
			String clientName=null;

			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");

			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();

				//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
				if(session.getTransaction().isActive() && session.isDirty())
				{
					iLogger.info("Opening a new session");
					session = HibernateUtil.getSessionFactory().openSession();
				}

				//DF20150928 - Rajani Nagaraju - Error in any Session is getting perculated to other new sessions and hence which is thrown to client.(WSDL not available)
				//Hence replacing session.getTransaction().begin() to session.beginTransaction();
				//session.getTransaction().begin();
				session.beginTransaction();
			}

			Query query = session.createQuery("from AccountEntity where account_name = '"+accountName+"' and status=true and client_id="+clientEntity.getClient_id()+"");
			Iterator itr = query.list().iterator();

			while(itr.hasNext())
			{
				accountEntity = (AccountEntity) itr.next();
			}
		}

		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}

		finally
		{
			try
			{
				if(session.getTransaction().isActive())
				{
					session.getTransaction().commit();
				}
			}

			catch(Exception e)
			{
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}

		}
		return accountEntity;
	}


	/** This method returns the list of AccountEntity for the given list of tenancyId
	 * @param tenancyIdList List of TenancyId
	 * @return Returns the list of Account Entity
	 */
	public List<AccountEntity> getAccountEntity(List<Integer> tenancyIdList)
	{
		List<AccountEntity> accountEntityList=new LinkedList<AccountEntity>();

		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{
			//get the tenancy Entity List for the given list of tenancyId List
			List<TenancyEntity> tenancyEntityList = getTenancyObjList(tenancyIdList);

			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			Query query = session.createQuery("from AccountTenancyMapping where tenancy_id in (:list)").setParameterList("list", tenancyEntityList);
			Iterator itr = query.list().iterator();

			while(itr.hasNext())
			{
				AccountTenancyMapping accountTenancy = (AccountTenancyMapping) itr.next();
				accountEntityList.add(accountTenancy.getAccount_id());

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

		return accountEntityList;
	}

	//******************************************** get Account id for the given tenancy ID *********************************************8
	/** This method returns the accountId corresponding to the specified tenancyID
	 * @param tenancyId tenancyId as integer input
	 * @return accountId
	 */
	public int getAccountId(int tenancyId)
	{
		int accountId = 0;

		//	Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{
			Query query = session.createQuery("from AccountTenancyMapping where tenancy_id ="+ tenancyId);
			Iterator itr = query.list().iterator();

			while(itr.hasNext())
			{
				AccountTenancyMapping accountTenancy = (AccountTenancyMapping) itr.next();
				accountId =  accountTenancy.getAccount_id().getAccount_id();

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

		return accountId;
	}

	public TenancyEntity getTenancyId(int accountId)
	{

		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//  Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		TenancyEntity tenancyEntity = null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{

			Query query = session.createQuery("from AccountTenancyMapping where account_id ="+ accountId);
			Iterator itr = query.list().iterator();

			while(itr.hasNext())
			{
				AccountTenancyMapping accountTenancy = (AccountTenancyMapping) itr.next();
				tenancyEntity = accountTenancy.getTenancy_id();

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

		return tenancyEntity;

	}


	//*********************************************** get tenancyType Entity for a given TenancyTypeId********************************************************
	public TenancyTypeEntity getTenancyTypeObj(int tenancyTypeId)
	{
		TenancyTypeEntity tenancyTypeObj=new TenancyTypeEntity(tenancyTypeId);

		//to check whether the record with given tenancy Type id exists
		if(tenancyTypeObj.getTenancy_type_id()== 0)
			return null;
		else
			return tenancyTypeObj; 
	}


	//******************************************************** Map tenancy and account ************************************************************
	/** This method Maps the Account and the Tenancy
	 * @param accountId accountId as integer input
	 * @param tenancyId tenancyId as integer input
	 * @throws CustomFault
	 */
	public void accountTenancyMapping(int accountId, int tenancyId) throws CustomFault
	{
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
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
			//Validate account Id and get Account Entity
			//AccountEntity accountEntity = getAccountObj(accountId);
			AccountEntity accountEntity = null;
			Query accQuery = session.createQuery("from AccountEntity where account_id='"+accountId+"'");
			Iterator accItr = accQuery.list().iterator();
			while(accItr.hasNext())
			{
				accountEntity = (AccountEntity) accItr.next();
			}
			if(accountEntity==null || accountEntity.getAccount_id()==0)
			{
				throw new CustomFault("Invalid account Id");
			}

			//Validate tenancyId and get Tenancy Entity
			//TenancyEntity tenancyEntity = getTenancyObj(tenancyId);
			TenancyEntity tenancyEntity = null;
			Query tenQuery = session.createQuery("from TenancyEntity where tenancy_id='"+tenancyId+"'");
			Iterator tenItr = tenQuery.list().iterator();
			while(tenItr.hasNext())
			{
				tenancyEntity = (TenancyEntity) tenItr.next();
			}
			if(tenancyEntity==null || tenancyEntity.getTenancy_id()==0)
			{
				throw new CustomFault("Invalid Tenancy Id");
			}



			Query query = session.createQuery("from AccountTenancyMapping where account_id = "+accountEntity.getAccount_id()+" and tenancy_id="+tenancyEntity.getTenancy_id());
			int mapped=0;

			if(query.list().size()>0)
				mapped=1;

			//Map account and tenancy
			if(mapped==0)
			{
				AccountTenancyMapping accountTenancyMap = new AccountTenancyMapping();
				accountTenancyMap.setAccount_id(accountEntity);
				accountTenancyMap.setTenancy_id(tenancyEntity);
				session.beginTransaction();
				session.save(accountTenancyMap);
			}
		}

		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}

		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}

		finally
		{

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
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}

			if(session.isOpen())
			{

				session.close();
			}

		}


	}



	//******************************************** Create a new Tenancy *************************************************************
	/** This method creates a new tenancy OR updates the existing one
	 * DefectId: 839 - Rajani Nagaraju - Handle Machine Movement between tenancies.
	 * @param loginId userLoginId
	 * @param accountId AccountId under which tenancy has to be created
	 * @param parentTenancyId parentTenancyId as Integer input
	 * @param childTenancyId childTenancyId as Integer input
	 * @param childTenancyName name of the child tenancy to be created
	 * @param childTenancyTypeId tenancyType of the child tenancy
	 * @param operatingStartTime operatingStartTime to be defined for the child tenancy
	 * @param operatingEndTime operatingEndTime to be defined for the child tenancy
	 * @return Returns the tenancyId
	 * @throws CustomFault
	 */
	public int createTenancy(String loginId, int accountId, int parentTenancyId, int childTenancyId, String childTenancyName, int childTenancyTypeId,
			Timestamp startTime, Timestamp endTime)
	{
		int tenancyId =0;		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}


		try
		{
			//get Client Details

			Properties prop = new Properties();
			String clientName=null;

			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");

			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);

			iLogger.info("TenancyBO - Chk1");
			//END of get Client Details


			if(childTenancyName==null)
			{
				throw new CustomFault("Tenancy name should be provided");
			}
			else{//Keerthi : 2017-04-13 : fix: machine not visible in portal : replace comma with the space
				childTenancyName = childTenancyName.replaceAll(",", " ");
			}

			if(loginId==null)
			{
				throw new CustomFault("LoginId not specified");
			}

			//get current date
			Date currentDate = new Date();
			Timestamp currentTimeStamp = new Timestamp(currentDate.getTime());


			//Create a new tenancy
			if(childTenancyId==0)
			{
				iLogger.info("TenancyBO - Chk2");
				//Validate and get tenancy object for the parent tenancy Id
				if(parentTenancyId==0)
				{
					throw new CustomFault("Parent tenancy ID should be specified");
				}
				TenancyEntity parentTenancy = getTenancyObj(parentTenancyId);
				if(parentTenancy==null || parentTenancy.getTenancy_id()==0)
				{
					throw new CustomFault("Invalid Parent tenancy ID");
				}

				//Validate the account ID and get the account Obj
				if(accountId==0)
				{
					throw new CustomFault("Account ID should be specified");
				}
				AccountEntity accountObj = getAccountObj(accountId);
				if(accountObj==null || accountObj.getAccount_id()==0)
				{
					throw new CustomFault("Invalid Account");
				}

				//Validate parent tenancy Id and account
				/*if(! (session.isOpen() ))
	            {
	                session = HibernateUtil.getSessionFactory().getCurrentSession();
	              //DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
	                if(session.getTransaction().isActive() && session.isDirty())
	                {
	                   	iLogger.info("Opening a new session");
	                   	session = HibernateUtil.getSessionFactory().openSession();
	                }
	                session.getTransaction().begin();
	            }*/

				int parentAccountId=0;
				AccountTenancyMapping accountTenancyObj=null;
				iLogger.info("TenancyBO - Chk3");
				if(session!=null){
					if(session.getTransaction().isActive() && session.isDirty())
					{
						iLogger.info("Opening a new session");
						session = HibernateUtil.getSessionFactory().openSession();
					}

				}
				iLogger.info("TenancyBO - Chk3-a");
				session.beginTransaction();

				iLogger.info("TenancyBO - Chk4");
				Query accountTenancyQuery = session.createQuery("from AccountTenancyMapping where tenancy_id="+parentTenancy.getTenancy_id());
				Iterator accountTenancyItr = accountTenancyQuery.list().iterator();
				while(accountTenancyItr.hasNext())
				{
					accountTenancyObj = (AccountTenancyMapping)accountTenancyItr.next();
					parentAccountId = accountTenancyObj.getAccount_id().getAccount_id();

				}
				iLogger.info("TenancyBO - Chk5");
				if(! (accountObj.getParent_account_id().getAccount_id()==parentAccountId) )
				{
					throw new CustomFault("Tenancy for the given account cannot be created under the specified tenancy");
				}


				//validate tenancy type ID
				TenancyTypeEntity tenancyTypeEntity = getTenancyTypeObj(childTenancyTypeId);
				iLogger.info("TenancyBO - Chk6");
				if(tenancyTypeEntity==null)
				{
					throw new CustomFault("Invalid Tenancy type");
				}

				/*if(! (session.isOpen() ))
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
				 */
				//DefectId:20150831 @Suprava TenancyName creation by appending AccountCode & check whether the child tenancy with the same tenancy code exists
				/*Query getChildTenanciesQuery = session.createQuery("from TenancyEntity where parent_tenancy_id = "+parentTenancy.getTenancy_id()+" " +
						" and  tenancy_name= '"+childTenancyName+"' and client_id="+clientEntity.getClient_id()+" ");
				Iterator getChildTenanciesItr = getChildTenanciesQuery.list().iterator();
				while(getChildTenanciesItr.hasNext())
				{
					tenancyId = -1;
					throw new CustomFault("Child tenancy with the specified name already exists");

				}*/
				if(session.getTransaction().isActive() && session.isDirty())
				{
					iLogger.info("Opening a new session");
					session = HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction();
				}

				iLogger.info("TenancyBO - Chk7");
				Query getChildTenanciesQuery = session.createQuery("from TenancyEntity where parent_tenancy_id = "+parentTenancy.getTenancy_id()+" " +
						" and  tenancyCode= '"+accountObj.getAccountCode()+"' and client_id="+clientEntity.getClient_id()+" ");
				Iterator getChildTenanciesItr = getChildTenanciesQuery.list().iterator();
				while(getChildTenanciesItr.hasNext())
				{
					tenancyId = -1;
					throw new CustomFault("Child tenancy with the specified name already exists");

				}
				String acctenancy_name =null;
				if(tenancyId!= -1)
				{
					iLogger.info("TenancyBO - Chk8");
					//DefectId:20150831 @Suprava TenancyName creation by appending AccountCode
					//String tenancy_name =childTenancyName+"-"+accountObj.getAccountCode();
					//acctenancy_name =accountObj.getAccountCode()+"-"+childTenancyName;
					//DefectId:20150910 @Suprava TenancyName creation by appending AccountCode After TenancyName
					acctenancy_name =childTenancyName+"-"+accountObj.getAccountCode();
					//End DefectId:20150910
					//create a new tenancy
					TenancyEntity tenancyEntity = new TenancyEntity();
					//tenancyEntity.setTenancy_name(childTenancyName);
					tenancyEntity.setTenancy_name(acctenancy_name);
					//End DefectId:20150831
					tenancyEntity.setParent_tenancy_id(parentTenancy);
					tenancyEntity.setParent_tenancy_name(parentTenancy.getTenancy_name());
					tenancyEntity.setClient_id(parentTenancy.getClient_id());
					tenancyEntity.setTenancy_type_id(tenancyTypeEntity);
					tenancyEntity.setOperating_Start_Time(startTime);
					tenancyEntity.setOperating_End_Time(endTime);
					tenancyEntity.setCreatedBy(loginId);
					tenancyEntity.setCreatedDate(currentTimeStamp);

					//DefectId: 839 - Rajani Nagaraju - Handle Machine Movement between tenancies.
					tenancyEntity.setTenancyCode(accountObj.getAccountCode());
					
					//Df20180122 @Roopa Updating the tenancy mapping code(used for Multiple BP code changes)
					
					tenancyEntity.setMappingCode(accountObj.getAccountCode());



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
					tenancyEntity.save();
					iLogger.info("TenancyBO - Chk8- Saved");
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

					//get the tenancyId of the newly created tenancy
					int newTenancyId=0;
					TenancyEntity newTenancyEntity=null;
					//DefectId:20150831 @Suprava TenancyName creation by appending AccountCode
					//Query query1 = session.createQuery("from TenancyEntity where tenancy_name='"+childTenancyName+"' and parent_tenancy_id="+parentTenancy.getTenancy_id()+" and client_id="+clientEntity.getClient_id()+"");

					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						if(session.getTransaction().isActive() && session.isDirty())
						{
							iLogger.info("Opening a new session");
							session = HibernateUtil.getSessionFactory().openSession();
						}

					}
					session.getTransaction().begin();
					iLogger.info("TenancyBO - Chk9");
					Query query1 = session.createQuery("from TenancyEntity where tenancy_name='"+acctenancy_name+"' and parent_tenancy_id="+parentTenancy.getTenancy_id()+" and client_id="+clientEntity.getClient_id()+"");
					Iterator itr1 = query1.list().iterator();
					while(itr1.hasNext())
					{
						newTenancyEntity = (TenancyEntity)itr1.next();
						newTenancyId = newTenancyEntity.getTenancy_id();
					}

					tenancyId = newTenancyId;

					//DefectId: 839 - Rajani Nagaraju - Handle Machine Movement between tenancies.
					//If more than one account is present with the same account code but with different accounIds, and if the Tenancy has to be created for such account - 
					//Tenancy has to be created for all those accounts

					//Get the Account Code from the input accountId
					String accountCode = accountObj.getAccountCode();
					iLogger.info("TenancyBO - Chk10");
					//session.getTransaction().begin();
					Query getPseudoTenancyQuery = session.createQuery(" from AccountEntity where status=true and account_id not in ( select account_id from AccountTenancyMapping) and " +
							" accountCode='"+accountCode+"'");
					Iterator pseudoTenancyItr = getPseudoTenancyQuery.list().iterator();
					while(pseudoTenancyItr.hasNext())
					{
						AccountEntity pendingAccounts = (AccountEntity)pseudoTenancyItr.next();
						if(pendingAccounts.getAccount_id()!= accountObj.getAccount_id())
						{
							TenancyEntity parentTenEnt = null;
							//get the Parent Account and hence Parent tenancy details
							Query parTenQuery = session.createQuery(" from AccountTenancyMapping where account_id='"+pendingAccounts.getParent_account_id().getAccount_id()+"'");
							Iterator parTenItr = parTenQuery.list().iterator();
							while(parTenItr.hasNext())
							{
								AccountTenancyMapping accTen = (AccountTenancyMapping)parTenItr.next();
								parentTenEnt = accTen.getTenancy_id();
							}

							//Parent Tenancy should exists in the application
							if(parentTenEnt!=null)
							{
								iLogger.info("TenancyBO - Chk11");
								//DefectId:20150831 @Suprava TenancyName creation by appending AccountCode
								//String pseudotenancy_name =pendingAccounts.getAccountCode()+"-"+childTenancyName;
								//DefectId:20150910 @Suprava TenancyName creation by appending AccountCode after TenancyName
								String pseudotenancy_name =childTenancyName+"-"+pendingAccounts.getAccountCode();
								//Task1: Create Pseudo Tenancies for the Pseudo Accounts
								TenancyEntity pseudoTenancyEntity = new TenancyEntity();
								//pseudoTenancyEntity.setTenancy_name(childTenancyName);
								pseudoTenancyEntity.setTenancy_name(pseudotenancy_name);
								//End DefectId:20150831
								iLogger.info("TenancyBO - Chk12");
								pseudoTenancyEntity.setParent_tenancy_id(parentTenEnt);
								pseudoTenancyEntity.setParent_tenancy_name(parentTenEnt.getTenancy_name());
								pseudoTenancyEntity.setClient_id(parentTenEnt.getClient_id());
								pseudoTenancyEntity.setTenancy_type_id(tenancyTypeEntity);
								pseudoTenancyEntity.setOperating_Start_Time(startTime);
								pseudoTenancyEntity.setOperating_End_Time(endTime);
								pseudoTenancyEntity.setCreatedBy(loginId);
								pseudoTenancyEntity.setCreatedDate(currentTimeStamp);
								pseudoTenancyEntity.setTenancyCode(pendingAccounts.getAccountCode());
								
								//Df20180122 @Roopa Updating the tenancy mapping code(used for Multiple BP code changes)
								
								pseudoTenancyEntity.setMappingCode(pendingAccounts.getAccountCode());


								iLogger.info("TenancyBO - Chk13");

								session.save(pseudoTenancyEntity);

								//Task2: Map Pseudo Account and the corresponding Pseudo Tenancy created
								AccountTenancyMapping pseudoAccTen = new AccountTenancyMapping();
								pseudoAccTen.setAccount_id(pendingAccounts);
								pseudoAccTen.setTenancy_id(pseudoTenancyEntity);
								session.save(pseudoAccTen);
								iLogger.info("TenancyBO - Chk14");

							}

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

						fLogger.fatal("Exception in commiting the record:"+e.getMessage());
					}
				}
			}


			//Update the tenancy details
			else
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

				}
				session.beginTransaction();
				//DefectId: 1286 - Rajani Nagaraju - Editing tenancy details by tenancy Admin
				//validate and get tenancy entity for the given tenancy Id

				//DefectId:839 - Rajani Nagaraju - 20131213 - To enable Machine Movement between tenancies
				//TenancyEntity tenancyEntity =null;
				List<TenancyEntity> tenancyEntityList = new LinkedList<TenancyEntity>();

				//DefectId:839 - Rajani Nagaraju - 20131213 - To enable Machine Movement between tenancies
				//Update Tenancy Details to all the Tenancies with the same tenancy code as that of childTenancyId
				//	Query tenQuery = session.createQuery(" from TenancyEntity where tenancy_id="+childTenancyId);
				iLogger.info("TenancyBO - Chk15");
				Query tenQuery = session.createQuery(" from TenancyEntity where tenancyCode = ( select a.tenancyCode from " +
						" TenancyEntity a where a.tenancy_id='"+childTenancyId+"') ");
				Iterator tenQueryItr = tenQuery.list().iterator();
				while(tenQueryItr.hasNext())
				{
					TenancyEntity tenancyEntity = (TenancyEntity)tenQueryItr.next();
					tenancyEntityList.add(tenancyEntity);
				}

				if(tenancyEntityList==null || tenancyEntityList.isEmpty())
				{
					throw new CustomFault("Invalid Tenancy Id");
				}
				iLogger.info("TenancyBO - Chk16");

				for(int i=0; i<tenancyEntityList.size(); i++)
				{
					TenancyEntity updateTen = tenancyEntityList.get(i); 
					//DefectId:20150831 @Suprava TenancyName creation by appending AccountCode
					//String updateTenancyName =updateTen.getTenancyCode()+"-"+childTenancyName;
					//DefectId:20150910 @Suprava TenancyName creation by appending AccountCode after tenancyName
					String childTenancyNameUpdate =null;
					String updateTenancyName = null;
					String tenancyCode =null;
					tenancyCode ="-"+updateTen.getTenancyCode();
					if(tenancyCode!=null && childTenancyName.contains(tenancyCode))
					{
						childTenancyNameUpdate = childTenancyName.replaceAll(tenancyCode, "");	
						updateTenancyName =childTenancyNameUpdate+"-"+updateTen.getTenancyCode();
					}
					else{
						updateTenancyName =childTenancyName+"-"+updateTen.getTenancyCode();
					}
					//String updateTenancyName =childTenancyNameUpdate+"-"+updateTen.getTenancyCode();
					//End DefectId:20150910
					//updateTen.setTenancy_name(childTenancyName);
					updateTen.setTenancy_name(updateTenancyName);
					//End DefectId:20150831
					updateTen.setOperating_Start_Time(startTime);
					updateTen.setOperating_End_Time(endTime);
					session.getTransaction().begin();
					session.update(updateTen);
					iLogger.info("TenancyBO - Chk17");

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

					fLogger.fatal("Exception in commiting the record:"+e.getMessage());
				}
				//DefectId:20150831 @Suprava TenancyName creation by appending AccountCode :get Tenancy code for the existing TenancyId 
				String updateTenancyCode =null;

				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					if(session.getTransaction().isActive() && session.isDirty())
					{
						iLogger.info("Opening a new session");
						session = HibernateUtil.getSessionFactory().openSession();
					}

				}
				session.beginTransaction();
				iLogger.info("TenancyBO - Chk18");
				Query tenCodeQuery = session.createQuery("select a.tenancyCode from TenancyEntity a where a.tenancy_id='"+childTenancyId+"' ");
				Iterator tenCodeQueryItr = tenCodeQuery.list().iterator();
				while(tenCodeQueryItr.hasNext())
				{
					updateTenancyCode = (String) tenCodeQueryItr.next();
				}
				//DefectId:1845 - Rajani Nagaraju - When the Tenancy Name is updated, same has to be reflected in the parent_tenancy_name of the child tenancies
				Query updateParTenNameQ = session.createQuery(" from TenancyEntity where parent_tenancy_id in (:list)").setParameterList("list", tenancyEntityList);
				Iterator updateParTenNameItr = updateParTenNameQ.list().iterator();
				iLogger.info("TenancyBO - Chk19");

				while(updateParTenNameItr.hasNext())
				{
					TenancyEntity updateTenancy = (TenancyEntity)updateParTenNameItr.next();
					//DefectId:20150831 @Suprava TenancyName creation by appending AccountCode
					//String updateTenancyName =updateTenancyCode+"-"+childTenancyName;
					//DefectId:20150910 @Suprava TenancyName creation by appending AccountCode after teancyName
					//String updateTenancyName =childTenancyName+"-"+updateTenancyCode;
					String childTenancyNameUpdate =null;
					String updateTenancyName = null;
					String tenancyCode =null;
					tenancyCode ="-"+updateTenancyCode;
					if(tenancyCode!=null && childTenancyName.contains(tenancyCode))
					{
						childTenancyNameUpdate = childTenancyName.replaceAll(tenancyCode, "");	
						updateTenancyName =childTenancyNameUpdate+"-"+updateTenancyCode;
					}
					else{
						updateTenancyName =childTenancyName+"-"+updateTenancyCode;
					}
					//EndDecfetId:20150910
					updateTenancy.setParent_tenancy_name(updateTenancyName);
					//DefectId:20150831 End 

					session.update(updateTenancy);

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

					fLogger.fatal("Exception in commiting the record:"+e.getMessage());
				}

				tenancyId = childTenancyId;

			}
		}

		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}

		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}

		finally
		{
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
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}

			if(session.isOpen())
			{

				session.close();
			}

		}

		return tenancyId;

	}



	//********************************************************* set Account Contact **************************************************************
	/** This method maps the Account and Contact
	 * @param accountId accountId as Integer input
	 * @param contactId contactId as String input
	 * @return Returns the Status message
	 * @throws CustomFault
	 */
	public String setAccountContact(int accountId, String contactId) throws CustomFault
	{
		String status = "SUCCESS";

		//Logger businessError = Logger.getLogger("businessErrorLogger");
		// Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();

		try
		{
			AccountEntity accountEntity = getAccountObj(accountId);
			if(accountEntity==null || accountEntity.getAccount_id()==0)
			{
				throw new CustomFault("Invalid Account ID");
			}

			DomainServiceImpl domainServiceImpl = new DomainServiceImpl();
			ContactEntity contactEntity = domainServiceImpl.getContactDetails(contactId);
			if(contactEntity == null || contactEntity.getContact_id()==null)
			{
				throw new CustomFault("Invalid Contact ID");
			}

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

			Query query = session.createQuery("from AccountContactMapping where account_id="+accountId+" and contact_id='"+contactId+"'");
			Iterator itr = query.list().iterator();
			int present =0;
			while(itr.hasNext())
			{
				AccountContactMapping map = (AccountContactMapping)itr.next();
				present=1;
			}
			if(present==0)
			{
				AccountContactMapping accountContact = new AccountContactMapping();
				accountContact.setAccount_id(accountEntity);
				accountContact.setContact_id(contactEntity);
				accountContact.save();
			}
		}

		catch(CustomFault e)
		{
			status = "FAILURE";
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}

		catch(Exception e)
		{
			status = "FAILURE";
			fLogger.fatal("Exception :"+e);
		}

		finally
		{

			try
			{if(session.getTransaction().isActive())
			{
				session.getTransaction().commit();
			}
			}

			catch(Exception e)
			{
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}

			if(session.isOpen())
			{

				session.flush();
				session.close();
			}

		}

		return status;
	}

	//*****************************get the list of contacts for the given accountId ******************************************
	public List<ContactEntity> getAccountUsers (int accountId)
	{
		List<ContactEntity> contactList = new LinkedList<ContactEntity>();

		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{
			Query query = session.createQuery("from AccountContactMapping where account_id="+accountId);
			Iterator itr = query.list().iterator();
			while(itr.hasNext())
			{
				AccountContactMapping accountContact = (AccountContactMapping) itr.next();
				contactList.add(accountContact.getContact_id());
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

		return contactList;
	}


	//************************************************* Set Tenancy Delegation Users *****************************************************
	/** This method sets the access for the parent tenancy users specified to view the details of child tenancy completely
	 * @param tenancyId tenancyId as Integer input
	 * @param parentTenancyUserIdList List of parent tenancy users
	 * @return Returns the status String
	 * @throws CustomFault
	 */
	public String setDelegateTenancyUsers(int tenancyId, List<String> parentTenancyUserIdList) throws CustomFault
	{
		String status = "SUCCESS";

		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();

		try
		{
			//Validate and get tenancyEntity
			TenancyEntity tenancyEntity = getTenancyObj(tenancyId);
			if(tenancyEntity==null || tenancyEntity.getTenancy_id()==0)
			{
				throw new CustomFault("Invalid TenancyId");
			}


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

			List<String> existingParentTenancyUsers = new LinkedList<String>();
			Query query = session.createQuery("from TenancyDelegationEntity where tenancyId="+tenancyId);
			Iterator itr = query.list().iterator();
			while(itr.hasNext())
			{
				TenancyDelegationEntity tenancyDelegation = (TenancyDelegationEntity) itr.next();
				existingParentTenancyUsers.add(tenancyDelegation.getContactId().getContact_id());
			}


			List<String> commonTenancyUserList = new LinkedList<String>();
			commonTenancyUserList.addAll(parentTenancyUserIdList);


			if(! (existingParentTenancyUsers==null || existingParentTenancyUsers.isEmpty()) )
			{
				if( !(parentTenancyUserIdList==null || parentTenancyUserIdList.isEmpty()) )
				{
					parentTenancyUserIdList.removeAll(existingParentTenancyUsers);
					existingParentTenancyUsers.removeAll(commonTenancyUserList);
				}
			}


			UserDetailsBO userDetails = new UserDetailsBO();
			List<ContactEntity> contactEntityList = userDetails.getContactEntityList(parentTenancyUserIdList);

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

			if(contactEntityList!=null)
			{
				for(int i=0; i<contactEntityList.size(); i++)
				{

					TenancyDelegationEntity tenancyDelegationUser = new TenancyDelegationEntity();
					tenancyDelegationUser.setTenancyId(tenancyEntity);
					tenancyDelegationUser.setContactId(contactEntityList.get(i));
					tenancyDelegationUser.save();


				}
			}

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

			for(int j=0; j<existingParentTenancyUsers.size(); j++)
			{
				Query query1 = session.createQuery("delete from TenancyDelegationEntity where tenancyId="+tenancyEntity.getTenancy_id()+" and contactId='"+existingParentTenancyUsers.get(j)+"'"); 
				query1.executeUpdate();

			}
		}

		catch(CustomFault e)
		{
			status = "FAILURE";
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}

		catch(Exception e)
		{
			status = "FAILURE";
			fLogger.fatal("Exception :"+e);
		}

		finally
		{

			try
			{if(session.getTransaction().isActive())
			{
				session.getTransaction().commit();
			}
			}

			catch(Exception e)
			{
				status = "FAILURE";
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}

		}

		return status;
	}

	//*************************************************END of set Tenancy Delegation Users *****************************************************
	//*************************************************END of set Tenancy Delegation Users *****************************************************


	//********************************************** get Tenancy Delegated users *************************************************

	/** This method returns the list of parent tenancy users who have the access to view the specified child tenancy completely
	 * @param tenancyId tenancyId as Integer input
	 * @return Returns the list of ContactEntity of parent tenancy
	 * @throws CustomFault
	 */
	public List<ContactEntity> getDelegateTenancyUsers(int tenancyId) throws CustomFault
	{
		List<ContactEntity> contactEntitylist = new LinkedList<ContactEntity>();

		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{
			//Validate Tenancy Id
			if(tenancyId==0)
			{
				return null;
			}

			TenancyEntity tenancyEntity = getTenancyObj(tenancyId);

			if( (tenancyEntity==null || tenancyEntity.getTenancy_id()==0) )
			{
				throw new CustomFault("Invalid Tenancy Id");
			}

			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			Query query = session.createQuery("from TenancyDelegationEntity where tenancyId="+tenancyId);
			Iterator itr = query.list().iterator();
			while(itr.hasNext())
			{
				TenancyDelegationEntity tenancyDelegation = (TenancyDelegationEntity) itr.next();
				contactEntitylist.add(tenancyDelegation.getContactId());
			}
		}

		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
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

		return contactEntitylist;
	}


	//********************************************** END of get Tenancy Delegated users *************************************************


	//************************************************ Get Tenancy Details ******************************************************
	/** DefectId:839 - Rajani Nagaraju - 20131213 - To enable Machine Movement between tenancies 
	 * This method returns the tenancy details
	 * @param loginId userLoginId
	 * @param parentTenancyIdList List of parentTenancyId
	 * @param childTenancyIdList List of childTenancyId
	 * @return returns the tenancy details
	 * @throws CustomFault
	 */
	public List<TenancyBO> getTenancyDetails(String loginId, List<Integer> parentTenancyIdList, List<Integer> childTenancyIdList, int pageNumber)
	{
		List<TenancyBO> responseList = new LinkedList<TenancyBO>();

		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		//DF20190228 :: service execution time check
		long startTime = System.currentTimeMillis();
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger=InfoLoggerClass.logger;
		
		//DF20190509: Anudeep Immidisetty adding page number to acheive pagination for faster response
		//Adding logic for pagination, displaying 50 records per page
		//to get only the 50 records, according to the page number calculate X for Limit X,Y - 
		//Y is always 50: number of records to be retrieved.
		//X: start point of the record to be retrieved. (first record is 0)
		
		/*int pge;

		iLogger.info("pageNumber: " + pageNumber);
		if((pageNumber%5)==0)
		{
			pge = pageNumber/5;
		}
		
		else if(pageNumber==1)
		{
			pge=1;
		}
					
		else
		{
			while( ((pageNumber)%5) != 0 )
			{
				pageNumber = pageNumber-1;
			}
			
			pge = ((pageNumber)/5)+1;
		}
		int startLimit = (pge-1)*50;
		
		int endLimit =50;
		iLogger.info("pge: " + pge +"," + " startLimit: " + startLimit + "," + " endLimit: " + endLimit);*/
		
		int startLimit = (pageNumber-1)*10;
		int endLimit =10;
		iLogger.info("pageNumber: " + pageNumber +"," + " startLimit: " + startLimit + "," + " endLimit: " + endLimit);
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{
			Query query=null;
			//get Client Details
			Properties prop = new Properties();
			String clientName=null;

			//DF20190509: Anudeep Immidisetty adding page number to acheive pagination for faster response
			//String limitQuery=" LIMIT "+startLimit+""+","+""+endLimit+"";

			//DefectId:839 - Rajani Nagaraju - 20131213 - To enable Machine Movement between tenancies
			boolean childTenancy = false;

			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");

			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details		  
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			//Check if child tenancy is specified
			if(! (childTenancyIdList==null || childTenancyIdList.isEmpty()) )
			{
				ListToStringConversion conversionObj = new ListToStringConversion();
				String childTenancyListAsString = conversionObj.getIntegerListString(childTenancyIdList).toString();

				//get the tenancy details
				//query = session.createQuery("from TenancyEntity where client_id="+clientEntity.getClient_id()+" and tenancy_id in (:list)").setParameterList("list", childTenancyIdList); 
				//DefectId:839 - Rajani Nagaraju - 20131213 - To enable Machine Movement between tenancies
				//DF20181129 :ma369757: instead of a.parent_tenancy taking a (entity)
				//DF20190509: Anudeep Immidisetty adding page number to acheive pagination for faster response
				query = session.createQuery("select a.createdBy, a.createdDate,a.Operating_End_Time, a.Operating_Start_Time, a,a.tenancyCode, a.parent_tenancy_name, a.tenancy_id, a.tenancy_name, b.account_id, CAST(GROUP_CONCAT(c.contact_id) As string ) as contact_id from TenancyEntity a, AccountTenancyMapping b, ContactEntity c, AccountContactMapping d where a.client_id="+clientEntity.getClient_id()+" and  a.tenancy_id in ( " +childTenancyListAsString+" ) and a.tenancy_id=b.tenancy_id and b.account_id = d.account_id and d.contact_id = c.contact_id and c.is_tenancy_admin=1 and c.active_status =1 GROUP BY a.tenancy_id");
				iLogger.info("Child Query created");
				//query = session.createQuery("select a.createdBy, a.createdDate,a.Operating_End_Time, a.Operating_Start_Time, a,a.tenancyCode, a.parent_tenancy_name, a.tenancy_id, a.tenancy_name, b.account_id, CAST(GROUP_CONCAT(c.contact_id) As string ) as contact_id from TenancyEntity a, AccountTenancyMapping b, ContactEntity c, AccountContactMapping d where a.client_id="+clientEntity.getClient_id()+" and  a.tenancy_id in ( " +childTenancyListAsString+" ) and a.tenancy_id=b.tenancy_id and b.account_id = d.account_id and d.contact_id = c.contact_id and c.is_tenancy_admin=1 and c.active_status =1 GROUP BY a.tenancy_id");
				childTenancy = true;
			}

			//Parent Tenancy is a mandatory field to be specified
			if( !(parentTenancyIdList==null || parentTenancyIdList.isEmpty()) )
			{
				ListToStringConversion conversionObj = new ListToStringConversion();
				String parentTenancyListAsString = conversionObj.getIntegerListString(parentTenancyIdList).toString();
				//query = session.createQuery("from TenancyEntity where client_id="+clientEntity.getClient_id()+" and  parent_tenancy_id in ( " +parentTenancyListAsString+" )"); 

				//DF20160901 @Roopa Query twaeking to increase the performance
				//DF20181129 :ma369757: instead of a.parent_tenancy taking a (entity)
				//DF20190509: Anudeep Immidisetty adding page number to acheive pagination for faster response
				query = session.createQuery("select a.createdBy, a.createdDate,a.Operating_End_Time, a.Operating_Start_Time, a,a.tenancyCode, a.parent_tenancy_name, a.tenancy_id, a.tenancy_name, b.account_id, CAST(GROUP_CONCAT(c.contact_id) As string ) as contact_id from TenancyEntity a, AccountTenancyMapping b, ContactEntity c, AccountContactMapping d where a.client_id="+clientEntity.getClient_id()+" and  a.parent_tenancy_id in ( " +parentTenancyListAsString+" ) and a.tenancy_id=b.tenancy_id and b.account_id = d.account_id and d.contact_id = c.contact_id and c.is_tenancy_admin=1 and c.active_status =1 GROUP BY a.tenancy_id");
				iLogger.info("Parent Query created");
				//query = session.createQuery("select a.createdBy, a.createdDate,a.Operating_End_Time, a.Operating_Start_Time, a,a.tenancyCode, a.parent_tenancy_name, a.tenancy_id, a.tenancy_name, b.account_id, CAST(GROUP_CONCAT(c.contact_id) As string ) as contact_id from TenancyEntity a, AccountTenancyMapping b, ContactEntity c, AccountContactMapping d where a.client_id="+clientEntity.getClient_id()+" and  a.parent_tenancy_id in ( " +parentTenancyListAsString+" ) and a.tenancy_id=b.tenancy_id and b.account_id = d.account_id and d.contact_id = c.contact_id and c.is_tenancy_admin=1 and c.active_status =1 GROUP BY a.tenancy_id");
			}
			//DF20190509: Anudeep Immidisetty adding page number to acheive pagination for faster response
			int size = query.list().size();
			query.setFirstResult(startLimit);
			query.setMaxResults(endLimit);
			iLogger.info("Query is : " + query.getQueryString());
			Object[] result = null;
			Iterator itr = query.list().iterator();
			while(itr.hasNext())
			{
				//TenancyEntity tenancyEntity = (TenancyEntity)itr.next();
				result = (Object[]) itr.next();
				String parentTenancyName =null;	

				//get the tenancy details
				TenancyBO tenancyBO = new TenancyBO();
				/*tenancyBO.setCreatedBy(tenancyEntity.getCreatedBy());
				if(tenancyEntity.getCreatedDate()!=null)
					tenancyBO.setCreatedDate(tenancyEntity.getCreatedDate().toString());
				if(tenancyEntity.getOperating_End_Time()!=null)
					tenancyBO.setOperatingEndTime(tenancyEntity.getOperating_End_Time().toString().substring(11));
				if(tenancyEntity.getOperating_Start_Time()!=null)
					tenancyBO.setOperatingStartTime(tenancyEntity.getOperating_Start_Time().toString().substring(11));
				if(tenancyEntity.getParent_tenancy_id()!=null)
					tenancyBO.setParentTenancyId(tenancyEntity.getParent_tenancy_id().getTenancy_id());*/
				tenancyBO.setSize(size);
				tenancyBO.setCreatedBy(result[0].toString());
				if(result[1]!=null)
					tenancyBO.setCreatedDate(result[1].toString());
				if(result[2]!=null)
					tenancyBO.setOperatingEndTime(result[2].toString().substring(11));
				if(result[3]!=null)
					tenancyBO.setOperatingStartTime(result[3].toString().substring(11));
				if(result[4]!=null)
				{
					TenancyEntity tenancyEntity=(TenancyEntity)result[4];
					//DF20170803: SU334449: Null check done for JCB_Asia tenancy Id to avoid null pointer exception
					if(tenancyEntity.getParent_tenancy_id() != null)
						tenancyBO.setParentTenancyId(tenancyEntity.getParent_tenancy_id().getTenancy_id());
				}

				if(childTenancy)
				{
					Query parentTenQuery = session.createQuery(" from TenancyEntity where tenancyCode= '"+result[5].toString()+"'");
					Iterator parentTenItr = parentTenQuery.list().iterator();
					while(parentTenItr.hasNext())
					{
						TenancyEntity parentTenEnt = (TenancyEntity)parentTenItr.next();
						if(parentTenancyName==null)
							parentTenancyName=parentTenEnt.getParent_tenancy_name();
						else
							parentTenancyName = parentTenancyName+", " +parentTenEnt.getParent_tenancy_name();
					}
					tenancyBO.setParentTenancyName(parentTenancyName);
				}

				else
				{
					tenancyBO.setParentTenancyName(result[6].toString());
				}

				tenancyBO.setTenancyId((Integer)result[7]);
				tenancyBO.setTenancyName(result[8].toString());
				tenancyBO.setTenancyCode(result[5].toString());

				if(result[9]!=null)
				{
					AccountEntity accountEntity=(AccountEntity)result[9];
					tenancyBO.setAccountName(accountEntity.getAccount_name());
				}

				if(result[10]!=null)
				{
					String contactId[]=	result[10].toString().split(",");

					tenancyBO.setTenancyAdminList(Arrays.asList(contactId));
				}

				responseList.add(tenancyBO);
			}
			iLogger.info("Count output from query is " + size);

			//Commenting below part @Roopa since TenancyDelegationEntity is not getting used

			//get the delegated tenancy details
			/*if(! (childTenancyIdList==null || childTenancyIdList.isEmpty()) )
			{
				Query query1 = session.createQuery("select a.tenancyId from TenancyDelegationEntity a where a.contactId='"+loginId+"'");
				Iterator itr1 = query1.list().iterator();
				while(itr1.hasNext())
				{
					TenancyEntity delegatedTenancy = (TenancyEntity)itr1.next();

					TenancyBO tenancyBO = new TenancyBO();
					tenancyBO.setCreatedBy(delegatedTenancy.getCreatedBy());
					if(delegatedTenancy.getCreatedDate()!=null)
						tenancyBO.setCreatedDate(delegatedTenancy.getCreatedDate().toString());
					if(delegatedTenancy.getOperating_End_Time()!=null)
						tenancyBO.setOperatingEndTime(delegatedTenancy.getOperating_End_Time().toString().substring(11));
					if(delegatedTenancy.getOperating_Start_Time()!=null)
						tenancyBO.setOperatingStartTime(delegatedTenancy.getOperating_Start_Time().toString().substring(11));
					if(delegatedTenancy.getParent_tenancy_id()!=null)
						tenancyBO.setParentTenancyId(delegatedTenancy.getParent_tenancy_id().getTenancy_id());
					tenancyBO.setParentTenancyName(delegatedTenancy.getParent_tenancy_name());
					tenancyBO.setTenancyId(delegatedTenancy.getTenancy_id());
					tenancyBO.setTenancyName(delegatedTenancy.getTenancy_name());

					//DefectId:839 - Rajani Nagaraju - 20131213 - To enable Machine Movement between tenancies
					tenancyBO.setTenancyCode(delegatedTenancy.getTenancyCode());

					responseList.add(tenancyBO);
				}
			}*/
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
		//DF20190228 :: service execution time check
		long endTime = System.currentTimeMillis();
		iLogger.info("TenancyDetailsService : TenancyBO execution time in ms : "+(endTime-startTime));
		iLogger.info("Size of the output list from query as obtained in BO " + responseList.size());
		return responseList;
	}


	//************************************************END of Get Tenancy Details ******************************************************

	//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
	/** This method returns the List of childTenancyIds under a given tenancy
	 * @param tenancyIdList List of tenancyId as Integer input
	 * @return Returns the list of child tenancy Id
	 */
	public List<Integer> getChildTenancyId (List<Integer> tenancyIdList)
	{
		List<Integer> childTenancyIdList = new LinkedList<Integer>();

		Logger fLogger = FatalLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{
			//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
			List<Integer> newTenancyIdList = new LinkedList<Integer>();
			ListToStringConversion conversion = new ListToStringConversion();
			String tenancyIdListString = conversion.getIntegerListString(tenancyIdList).toString();
			Query totalTenQuery = session.createQuery(" from TenancyEntity where tenancyCode in (select b.tenancyCode from TenancyEntity b where " +
					"b.tenancy_id in ("+tenancyIdListString+") )");
			Iterator totalTenItr = totalTenQuery.list().iterator();
			while(totalTenItr.hasNext())
			{
				TenancyEntity ten = (TenancyEntity) totalTenItr.next();
				newTenancyIdList.add(ten.getTenancy_id());
			}

			Query query = session.createQuery("from TenancyBridgeEntity where parentId in (:list) and childId not in (:list)").setParameterList("list", newTenancyIdList);
			Iterator itr = query.list().iterator();
			while(itr.hasNext())
			{
				TenancyBridgeEntity tenancyEnt = (TenancyBridgeEntity) itr.next();
				childTenancyIdList.add(tenancyEnt.getChildId());
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

		return childTenancyIdList;
	}


	/** This method returns the list of child Accounts that are pending for tenancy creation
	 * @param loginId userLoginId as String input
	 * @param tenancyIdList list of parentTenancy
	 * @return Returns the List of accounts under parentTenancy that are pending for Tenancy creation
	 */
	public List<PendingTenancyCreationImpl> getPendingTenancies(String loginId, List<Integer> tenancyIdList)
	{

		List<PendingTenancyCreationImpl> responseList = new LinkedList<PendingTenancyCreationImpl>();

		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{
			//get AccountEntity List for the given list of parent Tenancy Id list
			List<AccountEntity> parentAccountEntityList = getAccountEntity(tenancyIdList);

			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			}

			//get the list of Child accounts
			for(int i=0; i<parentAccountEntityList.size(); i++)
			{
				Query query1 = session.createQuery(" from AccountTenancyMapping where account_id="+parentAccountEntityList.get(i).getAccount_id());
				Iterator itr1 = query1.list().iterator();
				TenancyEntity parentTenancy = null;
				while(itr1.hasNext())
				{
					AccountTenancyMapping  accountTenancy = (AccountTenancyMapping) itr1.next();
					parentTenancy = accountTenancy.getTenancy_id();
				}

				String queryString = "select a.account_id as account_id , a.account_name as account_name " +
						" from AccountTenancyMapping b RIGHT JOIN b.account_id a " +
						" where a.parent_account_id ="+parentAccountEntityList.get(i).getAccount_id()+"  and b.account_id is NULL";

				Query query = session.createQuery(queryString);
				Iterator itr = query.list().iterator();
				Object[] result=null;

				while(itr.hasNext())
				{
					result = (Object[]) itr.next();
					int accountId = (Integer)result[0];
					String accountName = result[1].toString();

					PendingTenancyCreationImpl response = new PendingTenancyCreationImpl();
					response.setAccountId(accountId);
					response.setAccountName(accountName);
					if(parentTenancy!=null)
					{
						response.setParentTenancyId(parentTenancy.getTenancy_id());
						response.setParentTenancyName(parentTenancy.getTenancy_name());
					}
					responseList.add(response);	
				}

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

	//************************************************END of Get List of Accounts pending for tenancy creation *******************************************

	//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
	public List<ContactEntity> getTenancyUsers(int tenancy_Id ) throws CustomFault
	{
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<ContactEntity>  contactEntity = new LinkedList<ContactEntity> ();

		Logger fLogger = FatalLoggerClass.logger;

		try
		{
			TenancyEntity tenancyEntityObj=getTenancyObj(tenancy_Id);
			if(tenancyEntityObj==null)
				return contactEntity;

			if(tenancyEntityObj.getTenancy_id()==0)
				throw new CustomFault("Invalid TenancyID");


			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.beginTransaction();
			}

			//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
			Query query = session.createQuery(" select a from AccountTenancyMapping a, TenancyEntity b where a.tenancy_id=b.tenancy_id and " +
					" b.tenancyCode = (select c.tenancyCode from TenancyEntity c where c.tenancy_id ='"+tenancy_Id+"')");
			Iterator it = query.list().iterator();
			List<AccountEntity> accountEntity = new LinkedList<AccountEntity>();
			while(it.hasNext())
			{
				AccountTenancyMapping accountmapping = (AccountTenancyMapping)it.next();
				accountEntity.add(accountmapping.getAccount_id());
			}

			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.beginTransaction();
			}

			Query query1 = session.createQuery("From AccountContactMapping where account_id in (:List) ").setParameterList("List", accountEntity);
			Iterator itr = query1.list().iterator();
			while(itr.hasNext())
			{
				AccountContactMapping contactMapping = (AccountContactMapping)itr.next();
				contactEntity.add(contactMapping.getContact_id());
			}

		}

		catch(Exception e)
		{
			e.printStackTrace();
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

		return contactEntity;

	}

	//Method added for User Authencitcation Service:

	/*public TenancyBO getUserTenancy(String login_id,int account_id )

    {

            Session session= new HibernateSessionConfig().createSession();

            Transaction tx = session.beginTransaction();


`
            String queryString = "from AccountTenancyMapping where account_id= "+account_id;

            Iterator itr=session.createQuery(queryString).list().iterator();



            while(itr.hasNext())

            {

                    AccountTenancyMapping accountTenancy = (AccountTenancyMapping)itr.next();

                    TenancyEntity tenancy= accountTenancy.getTenancy_id();

                    tenancy_ID_Name.put(tenancy.getTenancy_id(), tenancy.getTenancy_name());



            }

            tx.commit();

            return this;

    }*/
	/*public HashMap<Integer, String> getTenancy_ID_Name() {
		return tenancy_ID_Name;
	}
	public void setTenancy_ID_Name(HashMap<Integer, String> tenancy_ID_Name) {
		this.tenancy_ID_Name = tenancy_ID_Name;
	}*/


	public TenancyBO getUserTenancy(String login_id,int account_id )
	{
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{
			//get login User tenancy List
			//DefectId:839 - Rajani Nagaraju - 20131213 - To enable Machine Movement between tenancies
			/*String queryString = " from AccountTenancyMapping where account_id= "+account_id;*/
			
			/*String queryString = " select a from AccountTenancyMapping a, AccountEntity b where b.status=true and a.account_id=b.account_id " +
					" and b.accountCode = (select c.accountCode from AccountEntity c where c.status=true and c.account_id='"+account_id+"')";*/
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			
			String queryString = " select a from AccountTenancyMapping a, AccountEntity b where b.status=true and a.account_id=b.account_id " +
					" and b.mappingCode = (select c.mappingCode from AccountEntity c where c.status=true and c.account_id='"+account_id+"')";
			
			Iterator itr=session.createQuery(queryString).list().iterator();

			//DefectId:839 - Rajani Nagaraju - 20131213 - To enable Machine Movement between tenancies
			String tenancyName =null;
			int first=0;
			while(itr.hasNext())
			{
				AccountTenancyMapping accountTenancy = (AccountTenancyMapping)itr.next();
				TreeMap<Integer,String> tenancyIdProxyUser = new TreeMap<Integer,String>();
				TenancyEntity tenancy= accountTenancy.getTenancy_id();
				//Assumption : All Tenancies with same tenancy Code ( Tenancy and PseudoTenancy for the same) will have same tenancyName
				//DefectId:839 - Rajani Nagaraju - 20131213 - To enable Machine Movement between tenancies
				tenancyName = tenancy.getTenancy_name();
				tenancyIdProxyUser.put(tenancy.getTenancy_id(), login_id);              
				//tenancyNameIDProxyUser.put(tenancy.getTenancy_name(), tenancyIdProxyUser);
				//DefectId:839 - Rajani Nagaraju - 20131213 - To enable Machine Movement between tenancies
				if(first==0)
					tenancyNameIDProxyUser.put(tenancyName, tenancyIdProxyUser);
				else
					tenancyNameIDProxyUser.put(tenancyName+" - "+(first+1), tenancyIdProxyUser);

				first++;
			}



			//DefectId:839 - Rajani Nagaraju - 20131213 - To enable Machine Movement between tenancies - Commenting out the below Code
			// Beacause, It is not required to send the Delegated Tenancy Details, since as agreed upon: During Login, user can login only to his tenancy
			//Delegated tenancy list will be seen only under Org group - My org Group Tab - There is no proxy user login as such provided by application
			//get delegated tenancy ID list for the login User
			/*Query delegatedTenancyQuery = session.createQuery("select a.tenancyId,d.contact_id from TenancyDelegationEntity a, AccountTenancyMapping b," +
            		" AccountContactMapping c, ContactEntity d where a.tenancyId = b.tenancy_id and b.account_id = c.account_id" +
            		" and c.contact_id = d.contact_id and a.contactId='"+login_id+"' and d.is_tenancy_admin=1 and d.active_status=true");
            Iterator delegatedTenancyItr = delegatedTenancyQuery.list().iterator();
            Object[] result = null;
            while(delegatedTenancyItr.hasNext())
            {
            	result = (Object[])delegatedTenancyItr.next();
            	TenancyEntity tenancyEntity = (TenancyEntity)result[0];
            	String delegatedTenancyAdmin = (String)result[1];

            	HashMap<Integer,String> tenancyIdProxyUser = new HashMap<Integer,String>();
                tenancyIdProxyUser.put(tenancyEntity.getTenancy_id(), delegatedTenancyAdmin);              
                tenancyNameIDProxyUser.put(tenancyEntity.getTenancy_name(), tenancyIdProxyUser);
            }*/

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

		return this;

	}
	//End User Authentication 


	//******************************************* Start of set Account Details_New - Dealer/Customer Details *********************************************
	/** This method sets the Account Details received from EA system
	 * @param accountCode DealerCode/Customer Code
	 * @param accountName Name of the Account
	 * @param addressLine1
	 * @param addressLine2
	 * @param city
	 * @param zipCode
	 * @param state
	 * @param zone
	 * @param country
	 * @param email
	 * @param contactNumber
	 * @param fax
	 * @param parentAccountCode AccountCode of the parent to build hierarchy
	 * @return
	 */

	public String setAccountDetails_new(String accountCode, String accountName, String addressLine1, String addressLine2, String city,
			String zipCode, String state, String zone, String country, String email,String contactNumber, String fax, String parentAccountCode, 
			String partnerRole, String reversePartnerRole,boolean isDealer, String messageId)	
	{
		String status = "SUCCESS-Record Processed";

		//Logger businessError = Logger.getLogger("businessErrorLogger");
		// Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;

		//F20160909 @Roopa taking the client details at the starting itself, opening the session after this

		//get Client Details
		Properties prop = new Properties();
		String clientName=null;
		ClientEntity clientEntity = null;
		try{
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");

			IndustryBO industryBoObj = new IndustryBO();
			clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details

			//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
			if(clientEntity==null)
			{
				status = "FAILURE-Client Undefined";
				bLogger.error("EA Processing: DealerInfo: "+messageId+" : Client Undefined");
				return status;
			}

		}
		catch(Exception e){
			fLogger.fatal("EA Processing:Exception in getting client deatils");
		}

		//Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx =null;
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)

		//  session.beginTransaction();
		tx=session.beginTransaction();
		try
		{
			AccountEntity accountEntity =getAccountCodeObj(accountCode);
			iLogger.info("AssetGateOutLog: "+accountCode+": accountEntity:"+accountEntity); 	


			//Start - DF20140220 - Rajani Nagaraju - If the Customer Zone is not specified, dealer zone details will be used for the same
			//AccountEntity parentAccEntity = getAccountCodeObj(parentAccountCode);

			AccountEntity parentAccEntity =null;
			Query query = session.createQuery("from AccountEntity where status=true and accountCode = '"+parentAccountCode+"'");
			Iterator itr = query.list().iterator();

			while(itr.hasNext())
			{
				parentAccEntity = (AccountEntity) itr.next();
			}

			if(parentAccEntity==null)
			{
				throw new CustomFault("Parent Account Doesnot Exists in LL");
			}

			if( zone == null || zone.equals("") || (!(zone.replaceAll("\\s","").length()>0)) )
			{
				if(parentAccEntity.getAddressId() != null)
					zone = parentAccEntity.getAddressId().getZone();
			}
			//End - DF20140220 - Rajani Nagaraju - If the Customer Zone is not specified, dealer zone details will be used for the same






			//Get the Partner Details

			PartnerRoleEntity partnerRoleObj=null;
			Query partnerQuery = session.createQuery(" from PartnerRoleEntity where partnerRole like '"+partnerRole+"' and reversePartnerRole " +
					" like '"+reversePartnerRole+"'");
			Iterator partnerItr = partnerQuery.list().iterator();
			while(partnerItr.hasNext())
			{
				partnerRoleObj = (PartnerRoleEntity) partnerItr.next();

				iLogger.info("AssetGateOutLog: "+accountCode+": partnerRoleObj:"+partnerRoleObj); 	
			}

			//Get the Address Details
			int adressInput = 1;
			AddressEntity addressEntity =null;

			if ( ( addressLine1 == null || addressLine1.equals("") || (!(addressLine1.replaceAll("\\s","").length()>0)) ) &&
					( addressLine2 == null || addressLine2.equals("") || (!(addressLine2.replaceAll("\\s","").length()>0)) ) &&
					( city == null || city.equals("") || (!(city.replaceAll("\\s","").length()>0)) ) &&
					( zipCode == null || zipCode.equals("") || (!(zipCode.replaceAll("\\s","").length()>0)) ) &&
					( state == null || state.equals("") || (!(state.replaceAll("\\s","").length()>0)) ) &&
					( zone == null || zone.equals("") || (!(zone.replaceAll("\\s","").length()>0)) ) &&
					( country == null || country.equals("") || (!(country.replaceAll("\\s","").length()>0)) ) 
					)
			{
				adressInput=0;
			}

			if(adressInput==1)
			{
				//DF20150928 - Rajani Nagaraju - ' giving the problem incorrect query syntax exception that gets carried forward to all the consequent sessions
				addressLine1 = addressLine1.replaceAll("'", "");
				addressLine2 = addressLine2.replaceAll("'", "");
				city = city.replaceAll("'", "");
				zipCode = zipCode.replaceAll("'", "");
				state = state.replaceAll("'", "");
				zone = zone.replaceAll("'", "");
				country =country.replaceAll("'", "");


				addressLine1 = addressLine1.replaceAll("\"", "");
				addressLine2 = addressLine2.replaceAll("\"", "");
				city = city.replaceAll("\"", "");
				zipCode = zipCode.replaceAll("\"", "");
				state = state.replaceAll("\"", "");
				zone = zone.replaceAll("\"", "");
				country =country.replaceAll("\"", "");

				int update =0;
				String addressQueryString = null;
				String whereQuery = "";

				if(addressLine1!=null && addressLine1!="" && (addressLine1.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " addressLine1 like '"+addressLine1+"' ";
				else
					whereQuery = whereQuery + " addressLine1 = null";

				if(addressLine2!=null && addressLine2!="" && (addressLine2.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and addressLine2 like '"+addressLine2+"' ";
				else
					whereQuery = whereQuery + " and addressLine2 = null";

				if(city!=null && city!="" && (city.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and city like '"+city+"' ";
				else
					whereQuery = whereQuery + " and city = null";

				if(zipCode!=null && zipCode!="" && (zipCode.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and zipCode like '"+zipCode+"' ";
				else
					whereQuery = whereQuery + " and zipCode = null";

				if(state!=null && state!="" && (state.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and state like '"+state+"' ";
				else
					whereQuery = whereQuery + " and state = null";

				if(zone!=null && zone!="" && (zone.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and zone like '"+zone+"' ";
				else
					whereQuery = whereQuery + " and zone = null";

				if(country!=null && country!="" && (country.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and country like '"+country+"' ";
				else
					whereQuery = whereQuery + " and country = null";

				addressQueryString = " from AddressEntity where " + whereQuery;

				if(session==null || ! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					if(session.getTransaction().isActive() && session.isDirty())
					{
						iLogger.info("AssetGateOutLog: "+accountCode+":Opening a new session");
						session = HibernateUtil.getSessionFactory().openSession();
					}
					tx=session.beginTransaction();
				}


				Query addressQuery = session.createQuery(addressQueryString);
				Iterator addQueryItr = addressQuery.list().iterator();

				while(addQueryItr.hasNext())
				{
					update=1;
					addressEntity = (AddressEntity) addQueryItr.next();
				}

				//Create a new AddressEntity
				if(addressEntity==null)
				{
					addressEntity = new  AddressEntity();
				}

				if(addressLine1!=null && addressLine1!="" && (addressLine1.replaceAll("\\s","").length()>0) ) 
					addressEntity.setAddressLine1(addressLine1);
				else
					addressEntity.setAddressLine1(null);

				if(addressLine2!=null && addressLine2!="" && (addressLine2.replaceAll("\\s","").length()>0) ) 
					addressEntity.setAddressLine2(addressLine2);
				else
					addressEntity.setAddressLine2(null);

				if(city!=null && city!="" && (city.replaceAll("\\s","").length()>0) ) 
					addressEntity.setCity(city);
				else
					addressEntity.setCity(null);

				if(zipCode!=null && zipCode!="" && (zipCode.replaceAll("\\s","").length()>0) ) 
					addressEntity.setZipCode(zipCode);
				else
					addressEntity.setZipCode(null);

				if(state!=null && state!="" && (state.replaceAll("\\s","").length()>0) ) 
					addressEntity.setState(state);
				else
					addressEntity.setState(null);

				if(zone!=null && zone!="" && (zone.replaceAll("\\s","").length()>0) ) 
					addressEntity.setZone(zone);
				else
					addressEntity.setZone(null);

				if(country!=null && country!="" && (country.replaceAll("\\s","").length()>0) ) 
					addressEntity.setCountry(country);
				else
					addressEntity.setCountry(null);

				if(session==null || ! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
					if(session.getTransaction().isActive() && session.isDirty())
					{
						iLogger.info("AssetGateOutLog: "+accountCode+": Opening a new session"); 	
						session = HibernateUtil.getSessionFactory().openSession();
					}

				}

				try{
					//session.getTransaction().begin();
					tx=session.beginTransaction();
					if(update==0)
					{

						session.save(addressEntity);
					}
					//UPdate the Adderss Entity
					else
					{
						session.update(addressEntity);
					}
				}catch(Exception e){
					fLogger.fatal("Exception in commiting the Address :"+e.getMessage());
				}
				finally
				{
					try
					{
						if(session!=null && session.isOpen())
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
						status = "FAILURE-Exception:"+e.getMessage();
						//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
						fLogger.fatal("Exception in commiting the record:"+e);
					}


				}

			}

			if(session==null || ! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
				if(session.getTransaction().isActive() && session.isDirty())
				{
					iLogger.info("AssetGateOutLog: "+accountCode+": Opening a new session"); 
					session = HibernateUtil.getSessionFactory().openSession();
				}

			}
			session.beginTransaction();
			AccountEntity newAccount = new AccountEntity();
			//Update the Account Details

			//DF20170822: KO369761 - fetching timezone, countrycode from country code table to  update in account table.
			//DF20180502: KO369761 - Setting to defualt timezone and countrycode whenever input is in the wrong format.
			String timeZone="(GMT+05:30)";
			String countryCode = "+91";

			if(country !=null){
				String timeZoneQ = ("from CountryCodesEntity where country_name='"+country+"'");
				Query timeZoneQuery = session.createQuery(timeZoneQ);
				Iterator timeZoneItr = timeZoneQuery.list().iterator();
				if(timeZoneItr.hasNext()){
					CountryCodesEntity countryCodes = (CountryCodesEntity) timeZoneItr.next();
					timeZone = countryCodes.getTimeZone();
					countryCode = countryCodes.getCountryCode();
				}
			}

			if(accountEntity!=null)

			{

				iLogger.info("AssetGateOutLog: "+accountCode+": if customer account is already present update the account:"+accountEntity); 

				String prevMobileNum= accountEntity.getMobile_no();

				accountEntity.setAccount_name(accountName);
				accountEntity.setAddressId(addressEntity);

				if(email!=null && email!="" && (email.replaceAll("\\s","").length()>0) ) 
					accountEntity.setEmailId(email);
				else
					accountEntity.setEmailId(null);

				if(contactNumber!=null && contactNumber!="" && (contactNumber.replaceAll("\\s","").length()>0) ) 
					accountEntity.setMobile_no(contactNumber);
				else
					accountEntity.setMobile_no(null);

				if(fax!=null && fax!="" && (fax.replaceAll("\\s","").length()>0) ) 
					accountEntity.setFax(fax);
				else
					accountEntity.setFax(null);

				accountEntity.setParent_account_id(parentAccEntity);
				accountEntity.setStatus(true);
				accountEntity.setClient_id(clientEntity);

				//DF20170905 - KO369761 - Updating account table with timezone and countryCode
				if(timeZone != null){
					accountEntity.setTimeZone(timeZone);
				}
				
				if(countryCode != null){
					accountEntity.setCountryCode(countryCode);
				}
				//DF20190312 :mani: account creation or updation tracebility
				Date currentDate = new Date();
				Timestamp currentTimeStamp = new Timestamp(currentDate.getTime());
				accountEntity.setUpdatedOn(currentTimeStamp);
				
				session.update(accountEntity);



				//DF20150602 - Rajani Nagaraju - Update Contact table when mobile number of dealer account is modified
				/*if(prevMobileNum!=null)
				{
					ContactEntity contactObj = null;
					Query conQuery = session.createQuery("from ContactEntity where primary_mobile_number='"+prevMobileNum+"'");
					Iterator conItr = conQuery.list().iterator();
					while(conItr.hasNext())
					{
						contactObj = (ContactEntity)conItr.next();
						contactObj.setPrimary_mobile_number(contactNumber);
						session.update(contactObj);
					}
				}*/
				//2020-10-12 :Shajesh : Update Contact table when mobile number of dealer account is modified
				if(prevMobileNum!=null)
				{
					        ContactEntity contactObj = null;
				            Query conQuery = session.createQuery("from ContactEntity where primary_mobile_number='"+prevMobileNum+"'");
				            Iterator conItr = conQuery.list().iterator();
				            String LoginId=null;
				            while(conItr.hasNext())
				            {
				                  contactObj = (ContactEntity)conItr.next();
				                  LoginId = contactObj.getContact_id();
				            }
				            
				            if(LoginId!=null)
				            {
				                  String storedPwd=new CommonUtil().getDecryptedPassword(LoginId);				                 
				                  String updatQuery="update contact set password=AES_ENCRYPT('"+storedPwd+"','"+contactNumber+"'),Primary_Moblie_Number='"+contactNumber+"' where contact_id='"+LoginId+"'";
				                  iLogger.info("User Details Update::Update Query:"+ updatQuery);
				                  String result=new CommonUtil().insertData(updatQuery);				                  
				                  iLogger.info("User Details Update::"+"update contact details with encrypted password into contact table status::"+result+" with new mobile number:"+contactNumber+"; Old Mobile number:"+prevMobileNum);
				                  
				            }
				} 
				newAccount = accountEntity;

				try
				{

					if(session!=null && session.isOpen())            	
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

			//Create new Account
			else
			{

				iLogger.info("AssetGateOutLog: "+accountCode+": if customer account is not present create the account:"+accountEntity); 

				newAccount.setAccount_name(accountName);
				newAccount.setAccountCode(accountCode);
				//Df20180122 @Roopa Updating the account mapping code(used for Multiple BP code changes)

				newAccount.setMappingCode(accountCode);

				newAccount.setAddressId(addressEntity);

				if(email!=null && email!="" && (email.replaceAll("\\s","").length()>0) ) 
					newAccount.setEmailId(email);
				else
					newAccount.setEmailId(null);

				if(contactNumber!=null && contactNumber!="" && (contactNumber.replaceAll("\\s","").length()>0) ) 
					newAccount.setMobile_no(contactNumber);
				else
					newAccount.setMobile_no(null);

				if(fax!=null && fax!="" && (fax.replaceAll("\\s","").length()>0) ) 
					newAccount.setFax(fax);
				else
					newAccount.setFax(null);
				newAccount.setStatus(true);

				newAccount.setParent_account_id(parentAccEntity);
				newAccount.setClient_id(clientEntity);

				//DF20170905 - KO369761 - Updating account table with timezone and countryCode
				if(timeZone != null)
					newAccount.setTimeZone(timeZone);

				if(countryCode != null)
					newAccount.setCountryCode(countryCode);
				//DF20190312 :mani: account creation or updation tracebility
				Date currentDate = new Date();
				Timestamp currentTimeStamp = new Timestamp(currentDate.getTime());
				newAccount.setCreatedOn(currentTimeStamp);
				newAccount.setUpdatedOn(currentTimeStamp);
				session.save(newAccount);
				accountEntity = newAccount;
				try
				{
					if(session!=null && session.isOpen())            	
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

			//Update PartnerShip table

			if(session==null || ! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				if(session.getTransaction().isActive() && session.isDirty())
				{
					iLogger.info("AssetGateOutLog: "+accountCode+": Opening a new session"); 
					session = HibernateUtil.getSessionFactory().openSession();
				}

			}
			session.getTransaction().begin();
			Query PartnershipMappingQry = null;
			//Suresh 

			PartnershipMappingQry = session.createQuery(" from PartnershipMapping where  accountFromId = '"+parentAccEntity.getAccount_id()+"' and  " +
					" accountToId='"+accountEntity.getAccount_id()+"' and  partnerId='"+partnerRoleObj.getPartnerId()+"' " );


			//Iterator PartnershipMappingItr = PartnershipMappingQry.list().iterator();

			Iterator PartnershipMappingItr = PartnershipMappingQry.list().iterator();

			/*if(PartnershipMappingItr.hasNext())
			{
				iLogger.info("partnerRoleObj is not null");
				Query q = session.createQuery(" update PartnershipMapping set accountFromId = '"+parentAccEntity.getAccount_id()+"', " +
						" accountToId='"+accountEntity.getAccount_id()+"', partnerId='"+partnerRoleObj.getPartnerId()+"' " +
						" where accountToId='"+accountEntity.getAccount_id()+"' and accountFromId = '"+parentAccEntity.getAccount_id()+"'");
				int rowCount = q.executeUpdate();
			}*/
			if(!PartnershipMappingItr.hasNext()){
				//Insert into partnership table


				PartnershipMapping partnership = new PartnershipMapping();
				partnership.setAccountFromId(parentAccEntity);
				partnership.setAccountToId(newAccount);
				partnership.setPartnerId(partnerRoleObj);
				session.save(partnership);


			}

			try
			{

				if(session!=null && session.isOpen())            	
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
				fLogger.fatal("Exception in commiting the record : TenancyBO-partnerRoleObj::"+e.getMessage());
			}

			if(session==null || ! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
				if(session.getTransaction().isActive() && session.isDirty())
				{
					iLogger.info("AssetGateOutLog: "+accountCode+": Opening a new session"); 
					session = HibernateUtil.getSessionFactory().openSession();
				}

			}
			AccountEntity custAccountEntity = getAccountCodeObj(accountCode);
			AccountTenancyMapping parentAccountTenancy = getTenancyEntity(parentAccEntity.getAccount_id());
			if(session==null || ! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				if(session.getTransaction().isActive() && session.isDirty())
				{
					iLogger.info("AssetGateOutLog: "+accountCode+": Opening a new session"); 
					session = HibernateUtil.getSessionFactory().openSession();
				}

			}
			session.getTransaction().begin();
			Query getAccTenancyQuery = session.createQuery("from TenancyEntity where parent_tenancy_id = '"+parentAccountTenancy.getTenancy_id().getTenancy_id()+"' " +
					" and  tenancyCode= '"+custAccountEntity.getAccountCode()+"' and client_id= '"+clientEntity.getClient_id()+"' ");
			iLogger.info("getAccTenancyQuery executed:" + getAccTenancyQuery.getQueryString());
			List getChildTenanciesList = getAccTenancyQuery.list();

			if (getChildTenanciesList!=null &&   getChildTenanciesList.isEmpty() )   
			{	

				//Df20140327 - Rajani Nagaraju - Adding try/Catch block here so that any error thrown for Tenancy Creation will not return Failure
				try

				{

					//                	Keerthi : 11/02/14 : creating tenancy for the user

					if(parentAccEntity!=null){

						boolean emailIDExists = false;
						UserDetailsBO userDetailsBO = new UserDetailsBO();
						//            		Keerthi : 18/09/14 : checking email for empty string
						if(email!=null && email!="" && (email.replaceAll("\\s","").length()>0)){
							if(userDetailsBO.emailIDExists(email)){
								emailIDExists = true;
							}            			
						}
						//            		Keerthi : 27/03/14 : checking null for contact number

						//DF20141219 - Rajani Nagaraju - Removing all the checks related to user creation, Since tenancy should be created irrespective of 
						//TA creation which can be done later in application. If the tenancy is not created, machine alerts will not appear even to JCB/Delaer
						//	if(contactNumber!=null && contactNumber!="" && (contactNumber.replaceAll("\\s","").length()>0))
						//	{
						//	 if(!userDetailsBO.mobileNumberExists(contactNumber))
						//	 {
						//		if(!emailIDExists)
						//		{
						String roleName;
						if(isDealer)
						{
							roleName = "DealerAdmin";
						}
						else
						{
							roleName = "CustomerFleetManager";
						}

						TenancyCreationReqContract tenancyReqContract  = new TenancyCreationReqContract();
						tenancyReqContract.setLoginId("batch");
						tenancyReqContract.setAccountId(newAccount.getAccount_id());

						AccountTenancyMapping accountTenancy = getTenancyEntity(parentAccEntity.getAccount_id());


						//DF20140328 - Rajani Nagaraju - IF Parent tenancy is not present then child tenancy cannot be created
						if(accountTenancy!=null)
						{

							tenancyReqContract.setParentTenancyId(accountTenancy.getTenancy_id().getTenancy_id());

							tenancyReqContract.setChildTenancyId(0);
							tenancyReqContract.setChildTenancyName(accountName);
							RoleEntity roleEntity = getRoleEntity(roleName);
							if(roleEntity!=null){

								tenancyReqContract.setTenancyAdminRoleId(roleEntity.getRole_id());
							}    
							tenancyReqContract.setTenancyAdminFirstName(accountName);
							tenancyReqContract.setTenancyAdminPhoneNumber(contactNumber);
							tenancyReqContract.setTenancyAdminEmailId(email);
							tenancyReqContract.setCountryCode(country);
							//                          createTenancyForUser
							iLogger.info("EA Processing: AccountDetails: "+messageId+": INPUTS to automated tenancy creation :");
							iLogger.info("EA Processing: AccountDetails: "+messageId+": Tenancy Name: "+ accountName+", Account ID: "+newAccount.getAccount_id()+", ParentAccountId: "+accountTenancy.getTenancy_id().getTenancy_id());
							String tenancyCreationStatus =new TenancyDetailsImpl().createTenancy(tenancyReqContract);

							iLogger.info("AssetGateOutLog: "+accountCode+": AccountDetails: "+messageId+":TenancyCreationStatus  "+tenancyCreationStatus);

							iLogger.info("EA Processing: AccountDetails: "+messageId+":TenancyCreationStatus  "+tenancyCreationStatus);


						}

						/*		}
        			else{
            			//DF20140327 - Rajani Nagaraju - Commenting the below code. Even if the tenancy is not created, it should return SUCCESS. 
        				//If it returns FAILURE, i/p account  data will be placed into fault_details table even if the account is successfully created
        				//status = "FAILURE";
        				businessError.error("EA Processing: AccountDetails: "+messageId+": Email ID already exists for account Name "+accountName);
            			//throw new CustomFault("Email ID already exists for account Name "+accountName);
            		}
        		 }
        		 else{

         			//DF20140327 - Rajani Nagaraju - Commenting the below code. Even if the tenancy is not created, it should return SUCCESS. 
     				//If it returns FAILURE, i/p account  data will be placed into fault_details table even if the account is successfully created
         			//status = "FAILURE";
        			 businessError.error("EA Processing: AccountDetails: "+messageId+": Mobile no already exists for account Name "+accountName+" and hence Tenancy not created by automation");
         			//throw new CustomFault("Mobile no already exists for account Name "+accountName);
         		}
        		}
        		else{
        			businessError.error("EA Processing: AccountDetails: "+messageId+": Mobile no. is not provided for "+accountName+"  and hence Tenancy not created by automation ");
        		}           */		

					}      

				}

				catch(Exception e)
				{
					status = "FAILURE-"+e.getMessage();
					fLogger.fatal("EA Processing: AccountDetails: "+messageId+ " Fatal Exception :"+e);
				}
			}

		}
		catch(CustomFault e)
		{
			status = "FAILURE-"+e.getFaultInfo();
			bLogger.error("EA Processing: AccountDetails: "+messageId+" : "+e.getFaultInfo());
		}

		catch(Exception e)
		{
			status = "FAILURE-"+e.getMessage();
			fLogger.fatal("EA Processing: AccountDetails: "+messageId+ " Fatal Exception :"+e);
		}

		finally
		{
			//DF20140715 - Rajani Nagaraju - To Avoid Session Close Exception

			try
			{
				if(session!=null && session.isOpen())

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
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}

			if(session!=null && session.isOpen())
			{

				session.close();
			}
		}

		return status;
	}


	//******************************************* Start of set Account Details - Dealer/Customer Details *********************************************
	/** This method sets the Account Details received from EA system
	 * @param accountCode DealerCode/Customer Code
	 * @param accountName Name of the Account
	 * @param addressLine1
	 * @param addressLine2
	 * @param city
	 * @param zipCode
	 * @param state
	 * @param zone
	 * @param country
	 * @param email
	 * @param contactNumber
	 * @param fax
	 * @param parentAccountCode AccountCode of the parent to build hierarchy
	 * @return
	 */
	public String setAccountDetails(String accountCode, String accountName, String addressLine1, String addressLine2, String city,
			String zipCode, String state, String zone, String country, String email,String contactNumber, String fax, String parentAccountCode, 
			String partnerRole, String reversePartnerRole,boolean isDealer, String messageId)	
	{
		String status = "SUCCESS-Record Processed";

		//Logger businessError = Logger.getLogger("businessErrorLogger");
		// Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		//Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx =null;
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)

		//  session.beginTransaction();
		tx=session.beginTransaction();
		try
		{
			AccountEntity accountEntity =getAccountCodeObj(accountCode);


			//Start - DF20140220 - Rajani Nagaraju - If the Customer Zone is not specified, dealer zone details will be used for the same
			//AccountEntity parentAccEntity = getAccountCodeObj(parentAccountCode);

			AccountEntity parentAccEntity =null;
			Query query = session.createQuery("from AccountEntity where status=true and accountCode = '"+parentAccountCode+"'");
			Iterator itr = query.list().iterator();

			while(itr.hasNext())
			{
				parentAccEntity = (AccountEntity) itr.next();
			}

			if(parentAccEntity==null)
			{
				throw new CustomFault("Parent Account Doesnot Exists in LL");
			}

			if( zone == null || zone.equals("") || (!(zone.replaceAll("\\s","").length()>0)) )
			{
				zone = parentAccEntity.getAddressId().getZone();
			}
			//End - DF20140220 - Rajani Nagaraju - If the Customer Zone is not specified, dealer zone details will be used for the same


			//get Client Details
			Properties prop = new Properties();
			String clientName=null;
			String timeZone = null;
			String countryCode = null;

			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");

			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details

			//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
			if(clientEntity==null)
			{
				status = "FAILURE-Client Undefined";
				bLogger.error("EA Processing: DealerInfo: "+messageId+" : Client Undefined");
				return status;
			}



			//Get the Partner Details

			PartnerRoleEntity partnerRoleObj=null;
			Query partnerQuery = session.createQuery(" from PartnerRoleEntity where partnerRole like '"+partnerRole+"' and reversePartnerRole " +
					" like '"+reversePartnerRole+"'");
			Iterator partnerItr = partnerQuery.list().iterator();
			while(partnerItr.hasNext())
			{
				partnerRoleObj = (PartnerRoleEntity) partnerItr.next();
			}

			//Get the Address Details
			int adressInput = 1;
			AddressEntity addressEntity =null;

			if ( ( addressLine1 == null || addressLine1.equals("") || (!(addressLine1.replaceAll("\\s","").length()>0)) ) &&
					( addressLine2 == null || addressLine2.equals("") || (!(addressLine2.replaceAll("\\s","").length()>0)) ) &&
					( city == null || city.equals("") || (!(city.replaceAll("\\s","").length()>0)) ) &&
					( zipCode == null || zipCode.equals("") || (!(zipCode.replaceAll("\\s","").length()>0)) ) &&
					( state == null || state.equals("") || (!(state.replaceAll("\\s","").length()>0)) ) &&
					( zone == null || zone.equals("") || (!(zone.replaceAll("\\s","").length()>0)) ) &&
					( country == null || country.equals("") || (!(country.replaceAll("\\s","").length()>0)) ) 
					)
			{
				adressInput=0;
			}

			if(adressInput==1)
			{
				//DF20150928 - Rajani Nagaraju - ' giving the problem incorrect query syntax exception that gets carried forward to all the consequent sessions
				//DF20170809: SU334449 - Checking Null Constraints for all fields in Address table. 
				if(addressLine1!=null)
					addressLine1 = addressLine1.replaceAll("'", "");
				if(addressLine2!=null)
					addressLine2 = addressLine2.replaceAll("'", "");
				if(city!=null)
					city = city.replaceAll("'", "");
				if(zipCode!=null)
					zipCode = zipCode.replaceAll("'", "");
				if(state!=null)
					state = state.replaceAll("'", "");
				if(zone!=null)
					zone = zone.replaceAll("'", "");
				if(country!=null)
					country =country.replaceAll("'", "");

				if(addressLine1!=null)
					addressLine1 = addressLine1.replaceAll("\"", "");
				if(addressLine2!=null)
					addressLine2 = addressLine2.replaceAll("\"", "");
				if(city!=null)
					city = city.replaceAll("\"", "");
				if(zipCode!=null)
					zipCode = zipCode.replaceAll("\"", "");
				if(state!=null)
					state = state.replaceAll("\"", "");
				if(zone!=null)
					zone = zone.replaceAll("\"", "");
				if(country!=null)
					country =country.replaceAll("\"", "");

				int update =0;
				String addressQueryString = null;
				String whereQuery = "";

				if(addressLine1!=null && addressLine1!="" && (addressLine1.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " addressLine1 like '"+addressLine1+"' ";
				else
					whereQuery = whereQuery + " addressLine1 = null";

				if(addressLine2!=null && addressLine2!="" && (addressLine2.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and addressLine2 like '"+addressLine2+"' ";
				else
					whereQuery = whereQuery + " and addressLine2 = null";

				if(city!=null && city!="" && (city.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and city like '"+city+"' ";
				else
					whereQuery = whereQuery + " and city = null";

				if(zipCode!=null && zipCode!="" && (zipCode.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and zipCode like '"+zipCode+"' ";
				else
					whereQuery = whereQuery + " and zipCode = null";

				if(state!=null && state!="" && (state.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and state like '"+state+"' ";
				else
					whereQuery = whereQuery + " and state = null";

				if(zone!=null && zone!="" && (zone.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and zone like '"+zone+"' ";
				else
					whereQuery = whereQuery + " and zone = null";

				if(country!=null && country!="" && (country.replaceAll("\\s","").length()>0) ) 
					whereQuery = whereQuery + " and country like '"+country+"' ";
				else
					whereQuery = whereQuery + " and country = null";

				addressQueryString = " from AddressEntity where " + whereQuery;

				Query addressQuery = session.createQuery(addressQueryString);
				Iterator addQueryItr = addressQuery.list().iterator();

				while(addQueryItr.hasNext())
				{
					update=1;
					addressEntity = (AddressEntity) addQueryItr.next();
				}

				//Create a new AddressEntity
				if(addressEntity==null)
				{
					addressEntity = new  AddressEntity();
				}

				if(addressLine1!=null && addressLine1!="" && (addressLine1.replaceAll("\\s","").length()>0) ) 
					addressEntity.setAddressLine1(addressLine1);
				else
					addressEntity.setAddressLine1(null);

				if(addressLine2!=null && addressLine2!="" && (addressLine2.replaceAll("\\s","").length()>0) ) 
					addressEntity.setAddressLine2(addressLine2);
				else
					addressEntity.setAddressLine2(null);

				if(city!=null && city!="" && (city.replaceAll("\\s","").length()>0) ) 
					addressEntity.setCity(city);
				else
					addressEntity.setCity(null);

				if(zipCode!=null && zipCode!="" && (zipCode.replaceAll("\\s","").length()>0) ) 
					addressEntity.setZipCode(zipCode);
				else
					addressEntity.setZipCode(null);

				if(state!=null && state!="" && (state.replaceAll("\\s","").length()>0) ) 
					addressEntity.setState(state);
				else
					addressEntity.setState(null);

				if(zone!=null && zone!="" && (zone.replaceAll("\\s","").length()>0) ) 
					addressEntity.setZone(zone);
				else
					addressEntity.setZone(null);

				if(country!=null && country!="" && (country.replaceAll("\\s","").length()>0) ) 
					addressEntity.setCountry(country);
				else
					addressEntity.setCountry(null);

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

				try{
					//session.getTransaction().begin();
					tx=session.beginTransaction();
					if(update==0)
					{

						session.save(addressEntity);
					}
					//UPdate the Adderss Entity
					else
					{
						session.update(addressEntity);
					}
				}catch(Exception e){
					fLogger.fatal("Exception in commiting the Address :"+e.getMessage());
				}
				finally
				{
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
						status = "FAILURE-Exception:"+e.getMessage();
						//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
						fLogger.fatal("Exception in commiting the record:"+e);
					}


				}

			}

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

			//DF20170905: KO369761 - fetching timezone,countryCode from country code table to  update in account table.
			if(country !=null){
				String timeZoneQ = ("from CountryCodesEntity where country_name='"+country+"'");
				Query timeZoneQuery = session.createQuery(timeZoneQ);
				Iterator timeZoneItr = timeZoneQuery.list().iterator();
				if(timeZoneItr.hasNext()){
					CountryCodesEntity countryCodes = (CountryCodesEntity) timeZoneItr.next();
					timeZone = countryCodes.getTimeZone();
					countryCode = countryCodes.getCountryCode();
				}
			}

			//Update the Account Details
			if(accountEntity!=null)
			{
				String prevMobileNum= accountEntity.getMobile_no();

				accountEntity.setAccount_name(accountName);
				accountEntity.setAddressId(addressEntity);

				if(email!=null && email!="" && (email.replaceAll("\\s","").length()>0) ) 
					accountEntity.setEmailId(email);
				else
					accountEntity.setEmailId(null);

				if(contactNumber!=null && contactNumber!="" && (contactNumber.replaceAll("\\s","").length()>0) ) 
					accountEntity.setMobile_no(contactNumber);
				else
					accountEntity.setMobile_no(null);

				if(fax!=null && fax!="" && (fax.replaceAll("\\s","").length()>0) ) 
					accountEntity.setFax(fax);
				else
					accountEntity.setFax(null);

				accountEntity.setParent_account_id(parentAccEntity);
				accountEntity.setStatus(true);
				accountEntity.setClient_id(clientEntity);

				//DF20170905 - KO369761 - Updating account table with timezone and countryCode.
				if(timeZone != null){
					accountEntity.setTimeZone(timeZone);
				}
				if(countryCode != null){
					accountEntity.setCountryCode(countryCode);
				}
				//DF20190312 :mani: account creation or updation tracebility
				Date currentDate = new Date();
				Timestamp currentTimeStamp = new Timestamp(currentDate.getTime());
				accountEntity.setUpdatedOn(currentTimeStamp);
				session.beginTransaction();
				//tx=session.beginTransaction();
				session.update(accountEntity);

				//Update PartnerShip table
				if(partnerRoleObj!=null)
				{
					Query q = session.createQuery(" update PartnershipMapping set accountFromId = '"+parentAccEntity.getAccount_id()+"', " +
							" accountToId='"+accountEntity.getAccount_id()+"', partnerId='"+partnerRoleObj.getPartnerId()+"' " +
							" where accountToId='"+accountEntity.getAccount_id()+"'");
					int rowCount = q.executeUpdate();
				}

				//DF20150602 - Rajani Nagaraju - Update Contact table when mobile number of dealer account is modified
				if(prevMobileNum!=null)
				{
					ContactEntity contactObj = null;
					Query conQuery = session.createQuery("from ContactEntity where primary_mobile_number='"+prevMobileNum+"'");
					Iterator conItr = conQuery.list().iterator();
					while(conItr.hasNext())
					{
						contactObj = (ContactEntity)conItr.next();
						contactObj.setPrimary_mobile_number(contactNumber);
						session.update(contactObj);
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

			//Create new Account
			else
			{
				AccountEntity newAccount = new AccountEntity();
				newAccount.setAccount_name(accountName);
				newAccount.setAccountCode(accountCode);
				newAccount.setAddressId(addressEntity);

				if(email!=null && email!="" && (email.replaceAll("\\s","").length()>0) ) 
					newAccount.setEmailId(email);
				else
					newAccount.setEmailId(null);

				if(contactNumber!=null && contactNumber!="" && (contactNumber.replaceAll("\\s","").length()>0) ) 
					newAccount.setMobile_no(contactNumber);
				else
					newAccount.setMobile_no(null);

				if(fax!=null && fax!="" && (fax.replaceAll("\\s","").length()>0) ) 
					newAccount.setFax(fax);
				else
					newAccount.setFax(null);
				newAccount.setStatus(true);

				newAccount.setParent_account_id(parentAccEntity);
				newAccount.setClient_id(clientEntity);

				//DF20170905 - KO369761 - Updating account table with timezone and countryCode.
				if(timeZone!=null){
					newAccount.setTimeZone(timeZone);
				}
				if(countryCode != null){
					newAccount.setCountryCode(countryCode);
				}
				//DF20190312 :mani: account creation or updation tracebility
				Date currentDate = new Date();
				Timestamp currentTimeStamp = new Timestamp(currentDate.getTime());
				newAccount.setCreatedOn(currentTimeStamp);
				newAccount.setUpdatedOn(currentTimeStamp);
				session.beginTransaction();
				session.save(newAccount);


				//Insert into partnership table
				if(partnerRoleObj!=null)
				{
					PartnershipMapping partnership = new PartnershipMapping();
					partnership.setAccountFromId(parentAccEntity);
					partnership.setAccountToId(newAccount);
					partnership.setPartnerId(partnerRoleObj);
					session.save(partnership);
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

				//Df20140327 - Rajani Nagaraju - Adding try/Catch block here so that any error thrown for Tenancy Creation will not return Failure
				try
				{
					//                	Keerthi : 11/02/14 : creating tenancy for the user

					if(parentAccEntity!=null){
						boolean emailIDExists = false;
						UserDetailsBO userDetailsBO = new UserDetailsBO();
						//            		Keerthi : 18/09/14 : checking email for empty string
						if(email!=null && email!="" && (email.replaceAll("\\s","").length()>0)){
							if(userDetailsBO.emailIDExists(email)){
								emailIDExists = true;
							}            			
						}
						//            		Keerthi : 27/03/14 : checking null for contact number

						//DF20141219 - Rajani Nagaraju - Removing all the checks related to user creation, Since tenancy should be created irrespective of 
						//TA creation which can be done later in application. If the tenancy is not created, machine alerts will not appear even to JCB/Delaer
						//	if(contactNumber!=null && contactNumber!="" && (contactNumber.replaceAll("\\s","").length()>0))
						//	{
						//	 if(!userDetailsBO.mobileNumberExists(contactNumber))
						//	 {
						//		if(!emailIDExists)
						//		{
						String roleName;
						if(isDealer)
						{
							roleName = "DealerAdmin";
						}
						else
						{
							roleName = "CustomerFleetManager";
						}

						TenancyCreationReqContract tenancyReqContract  = new TenancyCreationReqContract();
						tenancyReqContract.setLoginId("batch");
						tenancyReqContract.setAccountId(newAccount.getAccount_id());
						AccountTenancyMapping accountTenancy = getTenancyEntity(parentAccEntity.getAccount_id());

						//DF20140328 - Rajani Nagaraju - IF Parent tenancy is not present then child tenancy cannot be created
						if(accountTenancy!=null)
						{
							tenancyReqContract.setParentTenancyId(accountTenancy.getTenancy_id().getTenancy_id());

							tenancyReqContract.setChildTenancyId(0);
							tenancyReqContract.setChildTenancyName(accountName);
							RoleEntity roleEntity = getRoleEntity(roleName);
							if(roleEntity!=null){
								tenancyReqContract.setTenancyAdminRoleId(roleEntity.getRole_id());
							}    
							tenancyReqContract.setTenancyAdminFirstName(accountName);
							tenancyReqContract.setTenancyAdminPhoneNumber(contactNumber);
							tenancyReqContract.setTenancyAdminEmailId(email);
							tenancyReqContract.setCountryCode(country);
							//                          createTenancyForUser
							iLogger.info("EA Processing: AccountDetails: "+messageId+": INPUTS to automated tenancy creation :");
							iLogger.info("EA Processing: AccountDetails: "+messageId+": Tenancy Name: "+ accountName+", Account ID: "+newAccount.getAccount_id()+", ParentAccountId: "+accountTenancy.getTenancy_id().getTenancy_id());
							String tenancyCreationStatus =new TenancyDetailsImpl().createTenancy(tenancyReqContract);
							iLogger.info("EA Processing: AccountDetails: "+messageId+":TenancyCreationStatus  "+tenancyCreationStatus);
						}
						/*		}
            			else{
                			//DF20140327 - Rajani Nagaraju - Commenting the below code. Even if the tenancy is not created, it should return SUCCESS. 
            				//If it returns FAILURE, i/p account  data will be placed into fault_details table even if the account is successfully created
            				//status = "FAILURE";
            				businessError.error("EA Processing: AccountDetails: "+messageId+": Email ID already exists for account Name "+accountName);
                			//throw new CustomFault("Email ID already exists for account Name "+accountName);
                		}
            		 }
            		 else{

             			//DF20140327 - Rajani Nagaraju - Commenting the below code. Even if the tenancy is not created, it should return SUCCESS. 
         				//If it returns FAILURE, i/p account  data will be placed into fault_details table even if the account is successfully created
             			//status = "FAILURE";
            			 businessError.error("EA Processing: AccountDetails: "+messageId+": Mobile no already exists for account Name "+accountName+" and hence Tenancy not created by automation");
             			//throw new CustomFault("Mobile no already exists for account Name "+accountName);
             		}
            		}
            		else{
            			businessError.error("EA Processing: AccountDetails: "+messageId+": Mobile no. is not provided for "+accountName+"  and hence Tenancy not created by automation ");
            		}           */		

					}      

				}

				catch(Exception e)
				{
					status = "FAILURE-"+e.getMessage();
					fLogger.fatal("EA Processing: AccountDetails: "+messageId+ " Fatal Exception :"+e);
				}


			}	
		}
		catch(CustomFault e)
		{
			status = "FAILURE-"+e.getFaultInfo();
			bLogger.error("EA Processing: AccountDetails: "+messageId+" : "+e.getFaultInfo());
		}

		catch(Exception e)
		{
			status = "FAILURE-"+e.getMessage();
			fLogger.fatal("EA Processing: AccountDetails: "+messageId+ " Fatal Exception :"+e);
		}

		finally
		{
			//DF20140715 - Rajani Nagaraju - To Avoid Session Close Exception

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
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}

			if(session.isOpen())
			{

				session.close();
			}
		}

		return status;
	}
	//******************************************* End of set Account Details - Dealer/Customer Details *********************************************



	public RoleEntity getRoleEntity(String roleName){

		Logger fLogger = FatalLoggerClass.logger;

		RoleEntity roleEntity = null;
		try {

			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();

			if(roleName!=null){
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream(
						"remote/wise/resource/properties/configuration.properties"));
				String cfmValue = prop.getProperty(roleName);

				Query query = session.createQuery("FROM RoleEntity re WHERE re.role_name LIKE '%"+cfmValue+"%'");
				Iterator iterator = query.list().iterator();
				while(iterator.hasNext()){
					roleEntity = (RoleEntity)iterator.next();
				}


			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			fLogger.fatal("Exception:: " + e.getMessage());

		}



		return roleEntity;
	}
	/* *********** Implementation for Set Account Details ******* */

	/*	public String setAccountDetails(String accountCode, String accountName, String Location, String Email, String Address, String phoneNumber,String region, 
			String zone, String MobNumber, Long NoOfEmployees, String parentaccCode,Date yearStarted, String country, String state, String city, String zipCode ) 
	throws CustomFault{


		Logger businessError = Logger.getLogger("businessErrorLogger");
        Logger fatalError = Logger.getLogger("fatalErrorLogger");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

try{
		AccountEntity accountEntity = null;
		AccountEntity parentAccCode = null;
		if(accountCode==null || parentaccCode == null ||accountName==null || zone==null )
			throw new CustomFault("AccountCode/ParentAccCode/AccountName/Zone does not exist in the input. Please enter a valid one.");

		accountEntity =getAccountObj(accountCode);
		parentAccCode = getAccountObj(parentaccCode);
		int update = 0;

		if(accountEntity!=null){
			AccountEntity mergeEntity = (AccountEntity) session.merge(accountEntity);

			update=1;
			if(yearStarted!=null)
				mergeEntity.setYear_started(yearStarted);
				if(phoneNumber!=null)
					mergeEntity.setPhone_no(phoneNumber);
				if(MobNumber!=null)
					mergeEntity.setMobile_no(MobNumber);
				if(NoOfEmployees!=null)
					mergeEntity.setNo_of_employees(NoOfEmployees);
				mergeEntity.setParent_account_id(parentAccCode);
				session.save(mergeEntity);
				//tx.commit();
		}

		if(update==0){
			AccountEntity newAccEntity = new AccountEntity();
			newAccEntity.setAccount_name(accountName);
			newAccEntity.setMobile_no(MobNumber);
			newAccEntity.setNo_of_employees(NoOfEmployees);
			newAccEntity.setYear_started(yearStarted);
			newAccEntity.setPhone_no(phoneNumber);

			newAccEntity.setAccountCode(accountCode);

			newAccEntity.setParent_account_id(parentAccCode);
			newAccEntity.save();
		//	tx.commit();



			accountEntity =getAccountObj(accountCode);
		}

		Query addressQuery =session.createQuery("from AddressEntity where accountId ='"+accountEntity.getAccount_id() +"'");
		Iterator addressItr = addressQuery.list().iterator();
		int flag=0;
		while(addressItr.hasNext()){
			flag=1;
			AddressEntity addressEntity = (AddressEntity) addressItr.next();

			if(city!=null)
			addressEntity.setCity(city);
			if(country!=null)
			addressEntity.setCountry(country);
			if(state!=null)
			addressEntity.setState(state);
			if(zipCode!=null)
			addressEntity.setZipCode(zipCode);
			if(zone!=null)
				addressEntity.setZone(zone);
			if(region!=null)
				addressEntity.setRegion(region);

			session.update(addressEntity);
		//	txn.commit();
		}
		if(flag==0){
			AddressEntity newAddress = new AddressEntity();
			newAddress.setCountry(country);
			newAddress.setState(state);
			newAddress.setZipCode(zipCode);
			newAddress.setZone(zone);
			//newAddress.setAccountId(accountEntity.getAccount_id());
			newAddress.setRegion(region);
			newAddress.setCity(city);
			newAddress.save();

		//	txn.commit();

		}
}
catch(CustomFault e)
			{
				businessError.error("Custom Fault: "+ e.getFaultInfo());
			}

			catch(Exception e)
			{
				fatalError.fatal("Exception :"+e);
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
		return "Success";
	}*/

	//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
	public ContactEntity getContactUser(int tenancy_Id , String LoginId) throws CustomFault
	{

		Logger fLogger = FatalLoggerClass.logger;

		TenancyEntity tenancyEntityObj=getTenancyObj(tenancy_Id);

		if(tenancyEntityObj.getTenancy_id()==0)
			throw new CustomFault("Invalid TenancyID");

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Logger iLogger = InfoLoggerClass.logger;
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		//Df20160804 @Roopa doing begin transaction when the current session is taken
		session.beginTransaction();

		try
		{
//			Query query = session.createQuery(" select a from AccountTenancyMapping a, TenancyEntity b where " +
//					" a.tenancy_id=b.tenancy_id and b.tenancyCode = (select d.tenancyCode from TenancyEntity d" +
//					" where d.tenancy_id ='" +tenancy_Id+"')");//100017461.o
			Query query = session.createQuery(" select a from AccountTenancyMapping a, TenancyEntity b where " +
					" a.tenancy_id=b.tenancy_id and b.mappingCode = (select d.mappingCode from TenancyEntity d" +
					" where d.tenancy_id ='" +tenancy_Id+"')");//100017461.n
			Iterator it = query.list().iterator();
			List<AccountEntity> accountEntity = new LinkedList<AccountEntity>();
			while(it.hasNext())
			{
				AccountTenancyMapping accountmapping = (AccountTenancyMapping)it.next();
				accountEntity.add(accountmapping.getAccount_id());
			}

			/*for(int i=0;i<accountEntity.size();i++)
			{
				Query query1 = session.createQuery("From AccountContactMapping where Contact_ID ='"+LoginId+"'and Account_ID="+accountEntity.get(i).getAccount_id());
				Iterator itr = query1.list().iterator();

				if(itr.hasNext())
				{
					AccountContactMapping contactMapping = (AccountContactMapping)itr.next();
					return contactMapping.getContact_id();

				}
			}*/

			//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
			Query query1 = session.createQuery(" from AccountContactMapping where contact_id='"+LoginId+"' and account_id in (:list)").setParameterList("list", accountEntity);
			Iterator itr1 = query1.list().iterator();
			while(itr1.hasNext())
			{
				AccountContactMapping accountContact = (AccountContactMapping) itr1.next();
				if(accountContact.getContact_id().isActive_status())
					return accountContact.getContact_id();
			}

		}

		catch(Exception e)
		{
			e.printStackTrace();	
			fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());
		}

		finally
		{
			try
			{
				if(session.getTransaction().isActive())
				{
					session.flush();    
					session.getTransaction().commit();
				}
			}

			catch(Exception e)
			{
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}

			if(session.isOpen())
			{

				session.close();
			}
		}

		return null;

	}


	public AccountEntity getAccountCodeObj(String accountCode)
	{
		AccountEntity accountEntity = null;

		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

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

		try{

			Query query = session.createQuery("from AccountEntity where status=true and accountCode = '"+accountCode+"'");
			Iterator itr = query.list().iterator();

			while(itr.hasNext())
			{
				accountEntity = (AccountEntity) itr.next();

			}


		}

		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}

		finally
		{
			try
			{
				if(session.getTransaction().isActive())
				{
					session.getTransaction().commit();
				}
			}

			catch(Exception e)
			{
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}

		}
		return accountEntity;
	}

	public AccountTenancyMapping getTenancyEntity(int accountID)
	{
		AccountTenancyMapping accountTenancyEntity = null;

		//Logger fatalError = Logger.getLogger("fatalErrorLogger");

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();

		try{

			Query query = session.createQuery("from AccountTenancyMapping where account_id = "+accountID);
			Iterator itr = query.list().iterator();

			while(itr.hasNext())
			{
				accountTenancyEntity = (AccountTenancyMapping) itr.next();

			}


		}

		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e.getMessage());
		}

		finally
		{
			try
			{
				if(session.getTransaction().isActive())
				{
					session.flush();  
					session.getTransaction().commit();
				}
			}

			catch(Exception e)
			{
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);

			}

			if(session.isOpen())
			{

				session.close();
			}

		}
		return accountTenancyEntity;
	}
	public String setZonalDetails(String zonalName, String zonalCode, String messageId)
	{

		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		String result = "SUCCESS-Record Processed";
		try{
			//infoLogger.info(" Entered setZonalDetails()");
			String parentAccountCode = readValueForKey("JCBINDIA_Acount_Code");
			String clientName = readValueForKey("ClientName");
			String partnerRole= readValueForKey("PatnershipOEM"); 
			String reversePartnerRole = readValueForKey("PartnershipOEMRO");
			//infoLogger.info("value for key JCBINDIA_Acount_Code "+parentAccountCode );
			//infoLogger.info("value for key ClientName "+clientName );
			//infoLogger.info("value for key PatnershipOEM "+partnerRole );
			//infoLogger.info("value for key PartnershipOEMRO "+reversePartnerRole );

			AccountEntity parentAccountEntity = null;
			if(parentAccountCode!=null)
			{
				parentAccountEntity = getAccountCodeObj(parentAccountCode);
			}

			AccountEntity zonalAccountEntity =  getAccountCodeObj(zonalCode);
			PartnerRoleEntity partnerRoleEntity = getPartnerRoleEntity(partnerRole,reversePartnerRole);
			zonalAccountEntity = createOrEditAccountForZone(zonalAccountEntity,zonalName,zonalCode,parentAccountEntity,clientName);
			if(partnerRoleEntity!=null){
				insertPartnership(partnerRoleEntity,parentAccountEntity,zonalAccountEntity);
			}	
			else
			{
				bLogger.error("EA Processing: ZonalInformation: "+messageId+" : Not able to read partnership OEM to RO");

			}
		}
		catch(Exception e)
		{
			result = "FAILURE-"+e.getMessage();
			fLogger.fatal("EA Processing: ZonalInformation: "+messageId+ " Fatal Exception :"+e);
		}		

		return result;
	}



	public String insertPartnership(PartnerRoleEntity partnerRoleEntity,AccountEntity parentAccountEntity,AccountEntity zonalAccountEntity){
		String result = "SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();
		try{
			//			infoLogger.info("inserting partnership for account ID "+zonalAccountEntity.getAccount_id());
			iLogger.info("partnership ID "+partnerRoleEntity.getPartnerId());
			PartnershipMapping partnership = new PartnershipMapping();
			partnership.setAccountFromId(parentAccountEntity);
			partnership.setAccountToId(zonalAccountEntity);
			partnership.setPartnerId(partnerRoleEntity);
			session.save(partnership);
		}
		catch(Exception e){
			e.printStackTrace();
			fLogger.error("Failed while inserting partnership for account ID "+zonalAccountEntity.getAccount_id());
			result ="FAILURE";
		}		
		finally{

			try
			{if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}             
			}

			catch(Exception e)
			{
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			}    
			iLogger.info("Status after inserting to partnership for account ID "+zonalAccountEntity.getAccount_id()+" is :"+result);

		}
		return result;
	}	
	public AccountEntity createOrEditAccountForZone(AccountEntity zonalAccountEntity,String zonalName,String zonalCode, AccountEntity parentAccountEntity,String clientName){
		Logger iLogger = InfoLoggerClass.logger;
		//Logger fLogger = FatalLoggerClass.logger;


		iLogger.info("Entered createOrEditAccountForZone()");
		AccountEntity accountEntity = null;

		IndustryBO industryBoObj = new IndustryBO();
		ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();
		try{
			if(zonalAccountEntity!=null){//upate zone details
				iLogger.info("upating zone details as zone already exists !!!");
				iLogger.info("zonal account id "+zonalAccountEntity.getAccount_id()+", name "+zonalAccountEntity.getAccount_name());
				zonalAccountEntity.setAccount_name(zonalName);
				session.update(zonalAccountEntity);   
			}
			else if(parentAccountEntity!=null){
				if(zonalAccountEntity==null){//create a new account
					iLogger.info("creating a new account for the zone !!!!");


					accountEntity = new AccountEntity();
					accountEntity.setAccount_name(zonalName);
					accountEntity.setAccountCode(zonalCode);        		
					accountEntity.setStatus(true);        		
					accountEntity.setParent_account_id(parentAccountEntity);
					accountEntity.setClient_id(clientEntity);        		
					session.save(accountEntity);   
				}
			}       	    		 
		}   	 

		catch(Exception e){
			e.printStackTrace();
		}
		finally
		{
			try
			{if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}   
			}

			catch(Exception e)
			{
				Logger fLogger = FatalLoggerClass.logger;
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}
			if(session.isOpen()){
				session.flush();
				session.close();
			}              
		}

		return accountEntity;
	}

	public String readValueForKey(String accountCodeKey){
		String value = null;
		try{
			Properties prop = new Properties();

			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			value = prop.getProperty(accountCodeKey);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return value;

	}

	public PartnerRoleEntity getPartnerRoleEntity (String partnerRole,String reversePartnerRole){
		Logger iLogger = InfoLoggerClass.logger;


		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();
		iLogger.info("Entered getPartnerRoleEntity()");
		PartnerRoleEntity partnerRoleEntity=null;
		try{
			Query partnerQuery = session.createQuery(" from PartnerRoleEntity where partnerRole like '"+partnerRole+"' and reversePartnerRole " +
					" like '"+reversePartnerRole+"'");
			Iterator partnerItr = partnerQuery.list().iterator();
			while(partnerItr.hasNext())
			{
				partnerRoleEntity = (PartnerRoleEntity) partnerItr.next();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(session.getTransaction().isActive())
				{
					session.getTransaction().commit();
				}
			}

			catch(Exception e)
			{
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				Logger fLogger = FatalLoggerClass.logger;
				fLogger.fatal("Exception in commiting the record:"+e);
			}
			if(session.isOpen())
			{
				session.flush();
				session.close();
			}

		}
		return partnerRoleEntity;
	}



	//************************** Get the LL AccountCode for the given ECC/CRM DealerCode from Acc Mapping Table ******************
	/** Rajani Nagaraju - 2014-03-25 - Get the LL Account Code for the gievn ECC/CRM Dealer Code from Mapping table which would be created manually
	 * @param mappingAccCode - ECC Dealer Code OR CRM Dealer Code
	 * @return Returns the corresponding LL Account Code
	 */
	public String getLLAccountCode(String mappingAccCode)
	{
		String llAccountCode = null;

		Logger fLogger = FatalLoggerClass.logger;

		Logger bLogger = BusinessErrorLoggerClass.logger;
		//Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Session session = HibernateUtil.getSessionFactory().openSession();
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
			if(mappingAccCode==null)
				throw new CustomFault("TenancyBO: getLLAccountCode(): mappingAccCode input is null");

			Query accCodeQuery = session.createQuery(" from AccountMapping where eccAccCode='"+mappingAccCode+"' OR crmAccCode='"+mappingAccCode+"'");
			Iterator accCodeItr = accCodeQuery.list().iterator();
			iLogger.info("Log3:"+accCodeQuery);
			if(accCodeItr.hasNext())
			{
				AccountMapping accMappingObj = (AccountMapping)accCodeItr.next();
				llAccountCode = accMappingObj.getLlAccCode();
			}
		}

		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}

		catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}

		finally
		{
			try
			{
				if(session.getTransaction().isActive())
				{
					session.getTransaction().commit();
				}
			}

			catch(Exception e)
			{
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}

		}
		return llAccountCode;
	}

	//************************** END of Get the LL AccountCode for the given ECC/CRM DealerCode from Acc Mapping Table ******************


	//******************************* Map ECC/CRM Dealer Codes with LL Dealer Code *********************************************
	public String mapDealerCodes(String eccCode, String crmCode, String llCode, String dealerName, String messageId)
	{
		String status ="SUCCESS-Record Processed";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		Logger bLogger = BusinessErrorLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();

		try
		{  
			//If blank is sent in eccCode/crmCode, set null in database
			if(eccCode==null)
				eccCode="";
			if(crmCode==null)
				crmCode="";

			//Check for the duplicate record - EccCode+CrmCode forms the composite primary key 
			Query dupChkQ = session.createQuery(" from AccountMapping where eccAccCode='"+eccCode+"' and crmAccCode='"+crmCode+"'");
			Iterator dupChkItr = dupChkQ.list().iterator();
			while(dupChkItr.hasNext())
			{
				AccountMapping accountMap = (AccountMapping)dupChkItr.next();
				if(accountMap.getLlAccCode().trim().equalsIgnoreCase(llCode))
				{
					iLogger.info("EA Processing: DealerMapping: "+messageId+ " Duplicate Record Received. Hence Ignoring the same");
					return status;
				}
				else
				{
					status = "FAILURE-Duplicate ECC and CRM Code with different LL Code";
					bLogger.error("EA Processing: DealerMapping: "+messageId+" : Duplicate ECC and CRM Code with different LL Code");
					return status;
				}
			}

			//Insert the record into AccountMapping table
			AccountMapping accMap = new AccountMapping();
			accMap.setEccAccCode(eccCode);
			accMap.setCrmAccCode(crmCode);
			accMap.setLlAccCode(llCode);
			accMap.setEccAccName(dealerName);
			session.save(accMap);
		}

		catch(Exception e)
		{
			status = "FAILURE-Exception:"+e.getMessage();
			fLogger.fatal("EA Processing: DealerMapping: "+messageId+ " Fatal Exception :"+e);
		}

		finally
		{
			try
			{
				if(session.isOpen())
				{
					if(session.getTransaction().isActive())
					{
						session.getTransaction().commit();
					}

				}
			}

			catch(Exception e)
			{
				status = "FAILURE-Exception:"+e.getMessage();
				//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				fLogger.fatal("Exception in commiting the record:"+e);
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}

		}

		return status;
	}
	//******************************* END of Map ECC/CRM Dealer Codes with LL Dealer Code *********************************************
}
