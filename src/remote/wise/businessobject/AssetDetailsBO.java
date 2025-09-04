/*
 * CR337 : 20220721 : Dhiraj K : Property file read.
 * CR334 : 20220830 : Dhiraj K : Billability Module Integration changes
 * JCB6243 : 20220907 : Dhiraj K : Duplicate entries for machines in Redis for weather details
 * CR395:20230206 : Dhiraj K : SAP to send rolloff date in the interface file for each VIN
 * JCB6341 : 20230306 : Dhiraj K : Sale date is not getting updated as part of Direct Gateout sale
 * 20230404-Prasad-CR308-Configuration based approach for Weather data and New model codes addition from Wise
 * CR395 : 20230425 : Dhiraj K :Rolloff date change
 * JCB6444 : 20230725 : Prasanna Lakshmi :  Getting Oops Error while clicking on Image
 * CR482 : 20240726 : Dhiraj Kumar : Ignore Sale date update for C2C sale 
 * LLOPS - 182 : Sai Divya : 20250904 : File Processing failure for C2C .o
 */
package remote.wise.businessobject;
	
	import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
	import java.io.InputStreamReader;
	import java.io.PrintWriter;
	import java.io.StringWriter;
	import java.io.Writer;
	import java.net.HttpURLConnection;
	import java.net.URL;
	import java.net.URLEncoder;
	import java.sql.CallableStatement;
	import java.sql.Connection;
import java.sql.DriverManager;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.sql.SQLException;
	import java.sql.Statement;
	import java.sql.Timestamp;
	import java.text.DateFormat;
	import java.text.DecimalFormat;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
	import java.util.Calendar;
	import java.util.Collections;
	import java.util.Date;
	import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
	import java.util.LinkedList;
	import java.util.List;
import java.util.Map;
import java.util.Properties;
	import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
	
	import org.apache.logging.log4j.Logger;
	import org.codehaus.jackson.map.ObjectMapper;
	import org.hibernate.Query;
	import org.hibernate.Session;
	import org.hibernate.Transaction;
import org.json.simple.JSONObject;
	
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
	import remote.wise.EAintegration.dataContract.CustomerInfoInputContract;
	import remote.wise.businessentity.AccountEntity;
	import remote.wise.businessentity.AccountTenancyMapping;
	import remote.wise.businessentity.AssetAccountMapping;
	import remote.wise.businessentity.AssetClassEntity;
	import remote.wise.businessentity.AssetControlUnitEntity;
	import remote.wise.businessentity.AssetEntity;
	import remote.wise.businessentity.AssetExtendedDetailsEntity;
	import remote.wise.businessentity.AssetGroupEntity;
	import remote.wise.businessentity.AssetGroupProfileEntity;
	import remote.wise.businessentity.AssetOwnerSnapshotEntity;
	import remote.wise.businessentity.AssetServiceScheduleEntity;
	import remote.wise.businessentity.AssetTypeEntity;
	import remote.wise.businessentity.CMHRLogDetails;
	import remote.wise.businessentity.ClientEntity;
	import remote.wise.businessentity.ContactEntity;
	import remote.wise.businessentity.ControlUnitEntity;
	import remote.wise.businessentity.CustomerMasterEntity;
	import remote.wise.businessentity.EngineTypeEntity;
	import remote.wise.businessentity.FaultDetails;
	import remote.wise.businessentity.FleetSummaryChartTempDataEntity;
	import remote.wise.businessentity.PartnerRoleEntity;
	import remote.wise.businessentity.PartnershipMapping;
	import remote.wise.businessentity.ProductEntity;
	import remote.wise.businessentity.ProductProfileEntity;
	import remote.wise.businessentity.TenancyEntity;
	import remote.wise.dal.DynamicAMS_DAL;
	import remote.wise.dal.DynamicAMS_Doc_DAL;
	import remote.wise.exception.CustomFault;
import remote.wise.handler.WeatherDataProducer;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
	import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
	import remote.wise.log.InfoLogging.InfoLoggerClass;
	import remote.wise.pojo.AMSDoc_DAO;
	import remote.wise.pojo.AmsDAO;
	import remote.wise.pojo.AsseControlUnitDAO;
	import remote.wise.service.implementation.AccountDetailsImpl;
	import remote.wise.service.implementation.AssetDashboardImpl;
import remote.wise.service.implementation.AssetExtendedImpl;
	import remote.wise.service.implementation.AssetProvisioningImpl;
	import remote.wise.service.implementation.DomainServiceImpl;
	import remote.wise.service.implementation.FleetSummaryImpl;
import remote.wise.service.implementation.InstallationDateDetailsImpl;
import remote.wise.service.implementation.MachineProfileImpl;
	import remote.wise.service.implementation.RolledOffMachinesImpl;
	import remote.wise.service.implementation.StockSummaryImpl;
	import remote.wise.util.AssetUtil;
	import remote.wise.util.CommonUtil;
	import remote.wise.util.ConnectMySQL;

	import remote.wise.util.DalConnectionUtil;
	import remote.wise.util.DateUtil;
	import remote.wise.util.HibernateUtil;
	import remote.wise.util.ListToStringConversion;
	import remote.wise.util.StaticProperties;
	
	import com.google.gson.Gson;
	import com.google.gson.reflect.TypeToken;
import com.wipro.mcoreapp.implementation.AlertSubscriptionImpl;
	//import remote.wise.util.OrientAppDbDatasource;
	public class AssetDetailsBO extends BaseBusinessObject
	{
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		/*public static WiseLogger infoLogger = WiseLogger.getLogger("AssetDetailsBO:","info");
		public static WiseLogger businessError = WiseLogger.getLogger("AssetDetailsBO:","businessError");
		public static WiseLogger fatalError = WiseLogger.getLogger("AssetDetailsBO:","fatalError");*/
	
	
		String serialNumber,nick_name,description;
		Timestamp purchase_date,install_date,saleDate;
		String assetClassName, assetGroupName, assetTypeName,model,productName,renewalDate;
		String imeiNumber;
	
		String simNumber;
	
		String iccidNumber;
		//20240916 : Prasanna : CR486,CR487 :MSGID 022,023
		String fuelLevel;
		//CR512
	String machineCategory;

	public String getMachineCategory() {
		return machineCategory;
	}

	public void setMachineCategory(String machineCategory) {
		this.machineCategory = machineCategory;
	}
		public String getFuelLevel() {
			return fuelLevel;
		}
		public void setFuelLevel(String fuelLevel) {
			this.fuelLevel = fuelLevel;
		}

		//ramu b added on 20200512 extendedWarrantytype
		String extendedWarrantytype ;
		String assetTypeCode;    //CR353.n
		private int subscription; //CR353.n
		public void setExtendedWarrantytype(String extendedWarrantytype) {
			this.extendedWarrantytype = extendedWarrantytype;
		}
		public String getExtendedWarrantytype() {
			return  extendedWarrantytype;
		}
		/**
		 * @return the imeiNumber
		 */
		public String getImeiNumber() {
			return imeiNumber;
		}
	
	
		/**
		 * @param imeiNumber the imeiNumber to set
		 */
		public void setImeiNumber(String imeiNumber) {
			this.imeiNumber = imeiNumber;
		}
	
	
		/**
		 * @return the simNumber
		 */
		public String getSimNumber() {
			return simNumber;
		}
	
	
		/**
		 * @param simNumber the simNumber to set
		 */
		public void setSimNumber(String simNumber) {
			this.simNumber = simNumber;
		}
	
		/**
		 * @return the iccidNumber
		 */
		public String getIccidNumber() {
			return iccidNumber;
		}
	
		/**
		 * @param simNumber the iccidNumber to set
		 */
		public void setIccidNumber(String iccidNumber) {
			this.iccidNumber = iccidNumber;
		}
	
		/**
		 * @return the renewalDate
		 */
		//added by smitha on 26th june 2013...Defect ID 136
		String driverName;
		String driverContactNumber;
	
		public String getDriverName() {
			return driverName;
		}
	
	
		public void setDriverName(String driverName) {
			this.driverName = driverName;
		}
	
	
		public String getDriverContactNumber() {
			return driverContactNumber;
		}
	
	
		public void setDriverContactNumber(String driverContactNumber) {
			this.driverContactNumber = driverContactNumber;
		}
	
	
		//end
		public String getRenewalDate() {
			return renewalDate;
		}
	
	
		/**
		 * @param renewalDate the renewalDate to set
		 */
		public void setRenewalDate(String renewalDate) {
			this.renewalDate = renewalDate;
		}
	
	
	
		int make;
	
	
		public String getSerialNumber() {
			return serialNumber;
		}
	
	
		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}
	
	
		public String getNick_name() {
			return nick_name;
		}
	
	
		public void setNick_name(String nick_name) {
			this.nick_name = nick_name;
		}
	
	
		public String getDescription() {
			return description;
		}
	
	
		public void setDescription(String description) {
			this.description = description;
		}
	
	
		public Timestamp getPurchase_date() {
			return purchase_date;
		}
	
	
		public void setPurchase_date(Timestamp purchase_date) {
			this.purchase_date = purchase_date;
		}
	
	
		public Timestamp getInstall_date() {
			return install_date;
		}
	
	
		public void setInstall_date(Timestamp install_date) {
			this.install_date = install_date;
		}
	
		//Deepthi : CR 256 - Add sale Date in Fleet General
		
		public Timestamp getSaleDate() {
			return saleDate;
		}


		public void setSaleDate(Timestamp saleDate) {
			this.saleDate = saleDate;
		}
	
		public String getAssetClassName() {
			return assetClassName;
		}
	
	
		public void setAssetClassName(String assetClassName) {
			this.assetClassName = assetClassName;
		}
	
	
		public String getAssetGroupName() {
			return assetGroupName;
		}
	
	
		public void setAssetGroupName(String assetGroupName) {
			this.assetGroupName = assetGroupName;
		}
	
	
		public String getAssetTypeName() {
			return assetTypeName;
		}
	
	
		public void setAssetTypeName(String assetTypeName) {
			this.assetTypeName = assetTypeName;
		}
	
	
		public String getModel() {
			return model;
		}
	
	
		public void setModel(String model) {
			this.model = model;
		}
	
	
		public String getProductName() {
			return productName;
		}
	
	
		public void setProductName(String productName) {
			this.productName = productName;
		}
	
	
		public int getMake() {
			return make;
		}
	
	
		public void setMake(int make) {
			this.make = make;
		}
	
		public AssetDetailsBO(){
			DalConnectionUtil dlObj=null;
			Connection con=null;
			CallableStatement cstmt=null;
	
		}
	
	
		//METHOD 1:
		/** This method returns the Asset Entity for a given serialNumber
		 * @param serialNumber VIN as String
		 * @return AssetEntity is returned
		 */
		public AssetEntity getAssetEntity(String serialNumber)
		{
			AssetEntity assetEntity = null;
	
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			Logger iLogger = InfoLoggerClass.logger;
			//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
			session.beginTransaction();
	
			//Logger fatalError = Logger.getLogger("fatalErrorLogger");
			Logger fLogger = FatalLoggerClass.logger;
	
			//Logger bLogger = BusinessErrorLoggerClass.logger;
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
					//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
					if(session.getTransaction().isActive() && session.isDirty())
					{
						iLogger.info("Opening a new session");
						session = HibernateUtil.getSessionFactory().openSession();
					}
	
					session.getTransaction().begin();
				}
	
				Query query = session.createQuery("from AssetEntity where serial_number ='"+serialNumber+"' and active_status=true and client_id="+clientEntity.getClient_id()+"");
				Iterator itr = query.list().iterator();
	
				while(itr.hasNext())
				{
					assetEntity = (AssetEntity) itr.next();
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
	
	
			return assetEntity;
		} 
		//get the tenancyId
		//Defect ID : Asset Owner changes - 2013-07-19- Rajani
		public TenancyEntity getTenancyId(String serialNumber)
		{
			TenancyEntity tenancyEntity = null;		
	
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
	
			//Logger fatalError = Logger.getLogger("fatalErrorLogger");
			Logger fLogger = FatalLoggerClass.logger;
	
			//Logger bLogger = BusinessErrorLoggerClass.logger;
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
	
				//Query to get tenancyId
				Query query = session.createQuery(" select b.tenancy_id from AssetEntity a, AccountTenancyMapping b " +
						" where a.primary_owner_id = b.account_id and a.serial_number='"+serialNumber+"' and a.active_status=true and a.client_id="+clientEntity.getClient_id()+"");
				Iterator itr = query.list().iterator();
				while(itr.hasNext())
				{
					tenancyEntity = (TenancyEntity)itr.next();
				}
	
			}
			catch(Exception e)
			{
				fLogger.fatal("Exception :"+e.getMessage());
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
	
	
		public AssetEntity getAssetByNickName(String nickName)
		{
			AssetEntity assetEntityObj = null;
			//  Logger fatalError = Logger.getLogger("fatalErrorLogger");
			Logger fLogger = FatalLoggerClass.logger;
	
			//Logger bLogger = BusinessErrorLoggerClass.logger;
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
	
				Query q = session.createQuery("from AssetEntity where nick_name = '"+nickName+"' and active_status=true and client_id="+clientEntity.getClient_id()+"");
				Iterator i = q.list().iterator();
				while(i.hasNext())
				{
					assetEntityObj = (AssetEntity) i.next();
				}}
			catch(Exception e)
			{
				fLogger.fatal("Exception :"+e.getMessage());
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
	
			return assetEntityObj;
		}
	
		//METHOD 2:
		//get AssetControlUnit entity for given serial number
		public AssetControlUnitEntity getAssetControlUnitEntity(String serialNumber)
		{
			AssetControlUnitEntity assetControlUnit = new AssetControlUnitEntity(serialNumber);
			if(assetControlUnit==null || assetControlUnit.getSerialNumber()==null)
				return null;
			else
				return assetControlUnit;
		}
	
		//get asset class entity for a given asset_class_id
		public AssetClassEntity getAssetClassEntity(int asset_class_id)
		{
			AssetClassEntity assetClassEntityobj=new AssetClassEntity(asset_class_id);
			return assetClassEntityobj;
		} 
	
		//get asset class entity for a given assetClass Name
		/** This method returns the AssetClassEntity for the given asset class name
		 * @param assetClassName asset class name as String input
		 * @return AssetClassEntity
		 */
		public AssetClassEntity getAssetClassEntity(String assetClassName)
		{
			AssetClassEntity assetClassEntityobj=null;
			//Logger fatalError = Logger.getLogger("fatalErrorLogger");
			Logger fLogger = FatalLoggerClass.logger;
	
			//Logger bLogger = BusinessErrorLoggerClass.logger;
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
	
				Query assetClassQuery = session.createQuery("from AssetClassEntity where AssetClassName='"+assetClassName+"' and client_id="+clientEntity.getClient_id()+"");
				Iterator assetClassItr = assetClassQuery.list().iterator();
				while(assetClassItr.hasNext())
				{
					assetClassEntityobj = (AssetClassEntity)assetClassItr.next();
				}
			}
	
			catch(Exception e)
			{
				fLogger.fatal("Exception :"+e.getMessage());
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
	
			return assetClassEntityobj;
		}
		//get asset class entity for a given assetClass Name
		/** This method returns the AssetClassEntity for the given asset class name
		 * @param assetClassName asset class name as String input
		 * @return AssetClassEntity
		 */
		public EngineTypeEntity getEngineTypeEntity(String engineTypeName)
		{
			EngineTypeEntity engineTypeEntityObj=null;
			//Logger fatalError = Logger.getLogger("fatalErrorLogger");
			Logger fLogger = FatalLoggerClass.logger;
	
			//Logger bLogger = BusinessErrorLoggerClass.logger;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try
			{
				Query assetClassQuery = session.createQuery("from EngineTypeEntity where engineTypeName='"+engineTypeName+"'");
				Iterator engineTypeItr = assetClassQuery.list().iterator();
				while(engineTypeItr.hasNext())
				{
					engineTypeEntityObj = (EngineTypeEntity)engineTypeItr.next();
				}
			}
	
			catch(Exception e)
			{
				fLogger.fatal("Exception :"+e.getMessage());
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
	
			return engineTypeEntityObj;
		}
	
		/** This method set the AssetClass Entity
		 * @param assetClassName Name of the Asset class as String
		 * @param clientId clientId as Integer input
		 */
		public void setAssetClassEntity(String assetClassName, int clientId)
		{
			AssetClassEntity assetClass = getAssetClassEntity(assetClassName);
	
			//No updates are possible: Only insertion
			if(! ((assetClass!=null && assetClass.getClient_id().getClient_id()==clientId) ) )
			{
				DomainServiceImpl domainService = new DomainServiceImpl();
				ClientEntity clientEntity = domainService.getClientDetails(clientId);
	
				AssetClassEntity newAssetClassEntity = new AssetClassEntity();
				newAssetClassEntity.setAssetClassName(assetClassName);
				newAssetClassEntity.setClient_id(clientEntity);
				newAssetClassEntity.save();
	
			}
		}
		//get asset type entity for a given asset_type_id
		public AssetTypeEntity getAssetTypeEntity(int asset_type_id)
		{
			AssetTypeEntity assetTypeEntityobj=new AssetTypeEntity(asset_type_id);
			return assetTypeEntityobj;
		} 
	
		/** This method returns AssetTypeEntity for the given assetTypeName
		 * @param assetTypeName Name of the asset type
		 * @return AssetTypeEntity
		 */
		public AssetTypeEntity getAssetTypeEntity(String assetTypeName)
		{
			AssetTypeEntity assetTypeEntityobj=null;
			//Logger fatalError = Logger.getLogger("fatalErrorLogger");
			Logger fLogger = FatalLoggerClass.logger;
	
			//Logger bLogger = BusinessErrorLoggerClass.logger;
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
	
				Query assetTypeQuery = session.createQuery("from AssetTypeEntity where asset_type_name='"+assetTypeName+"' and client_id="+clientEntity.getClient_id()+"");
				Iterator assetTypeItr = assetTypeQuery.list().iterator();
				while(assetTypeItr.hasNext())
				{
					assetTypeEntityobj = (AssetTypeEntity)assetTypeItr.next();
				}
			}
			catch(Exception e)
			{
				fLogger.fatal("Exception :"+e.getMessage());
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
	
			return assetTypeEntityobj;
		}
	
	
	
		/** This method sets the AssetTypeEntity
		 * @param assetTypeName Name of the asset type
		 * @param clientId clientId as Integer input
		 */
		public void setAssetTypeEntity(String assetTypeName, int clientId)
		{
			AssetTypeEntity assetType = getAssetTypeEntity(assetTypeName);
	
			//No updates are possible: Only insertion
			if(! ((assetType!=null && assetType.getClient_id().getClient_id()==clientId) ) )
			{
				DomainServiceImpl domainService = new DomainServiceImpl();
				ClientEntity clientEntity = domainService.getClientDetails(clientId);
	
				AssetTypeEntity newAssetTypeEntity = new AssetTypeEntity();
				newAssetTypeEntity.setAsset_type_name(assetTypeName);
				newAssetTypeEntity.setClient_id(clientEntity);
				newAssetTypeEntity.save();
	
			}
		}
	
	
	
		//get asset group entity for a given asset_group_id
		public AssetGroupEntity getAssetGroupEntity(int asset_group_id)
		{
			AssetGroupEntity assetGroupEntityobj=new AssetGroupEntity(asset_group_id);
			return assetGroupEntityobj;
		} 
	
	
		/** This method returns the asset group entity for the given asset group name
		 * @param assetGroupName name of the asset group
		 * @return AssetGroupEntity
		 */
		public AssetGroupEntity getAssetGroupEntity(String assetGroupName)
		{
			AssetGroupEntity assetGroupEntityobj=null;
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
	
				Query assetGroupQuery = session.createQuery("from AssetGroupEntity where asset_group_name='"+assetGroupName+"' and client_id="+clientEntity.getClient_id()+"");
				Iterator assetGroupItr = assetGroupQuery.list().iterator();
				while(assetGroupItr.hasNext())
				{
					assetGroupEntityobj = (AssetGroupEntity)assetGroupItr.next();
				}
			}
	
			catch(Exception e)
			{
				fLogger.fatal("Exception :"+e.getMessage());
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
	
			return assetGroupEntityobj;
		}
	
	
		/** This method sets the AssetGroupEntity
		 * @param assetGroupName Name of the asset Group
		 * @param clientId clientId as Integer input
		 */
		public void setAssetGroupEntity(String assetGroupName, int clientId)
		{
			AssetGroupEntity assetGroup = getAssetGroupEntity(assetGroupName);
	
			//No updates are possible: Only insertion
			if(! ((assetGroup!=null && assetGroup.getClient_id().getClient_id()==clientId) ) )
			{
				DomainServiceImpl domainService = new DomainServiceImpl();
				ClientEntity clientEntity = domainService.getClientDetails(clientId);
	
				AssetGroupEntity newAssetGroupEntity = new AssetGroupEntity();
				newAssetGroupEntity.setAsset_group_name(assetGroupName);
				newAssetGroupEntity.setClient_id(clientEntity);
				newAssetGroupEntity.save();
	
			}
		}
	
		/** This method sets the engine type entity
		 * @param assetGroupName Name of the asset Group
		 * @param clientId clientId as Integer input
		 */
		public void setEngineTypeEntity(String engineTypeName)
		{
			EngineTypeEntity engineEntity = getEngineTypeEntity(engineTypeName);
	
			engineEntity.setEngineTypeName(engineTypeName);
			engineEntity.save();
	
	
		}
	
		/** This method sets the productId
		 * @param assetClassId assetClassId as Integer input
		 * @param assetGroupId assetGroupId as Integer input
		 * @param assetTypeId assetTypeId as Integer input
		 * @param make make of the product
		 * @param clientId clientId as Integer input
		 * @return
		 */
		public ProductEntity getProductEntity(int assetClassId, int assetGroupId, int assetTypeId, int make, int clientId, int engineTypeId)
		{
			ProductEntity prodEntityObj = null;
	
			//Logger fatalError = Logger.getLogger("fatalErrorLogger");
			Logger fLogger = FatalLoggerClass.logger;
			//Logger bLogger = BusinessErrorLoggerClass.logger;
			//Logger iLogger = InfoLoggerClass.logger;
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
				String basicquery = "from ProductEntity where assetGroupId="+assetGroupId+" and assetTypeId="+assetTypeId+" and make="+make+" and clientId="+clientId
						+" and engineTypeId="+engineTypeId;
				if(assetClassId!=0)
					basicquery = basicquery + " and assetClassId="+assetClassId;
	
				Query productQuery = session.createQuery(basicquery);
				Iterator prodItr = productQuery.list().iterator();
				while(prodItr.hasNext())
				{
					prodEntityObj = (ProductEntity) prodItr.next();
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
	
			return prodEntityObj;
		}
	
	
		/** This method sets the productEntity
		 * @param productName Name of the product
		 * @param clientId clientId as Integer input
		 * @param assetClassId assetClassId as Integer input
		 * @param assetGroupId assetGroupId as Integer input
		 * @param assetTypeId assetTypeId as Integer input
		 * @param make make of the product
		 */
		public void setProductEntity(String productName, int clientId, int assetClassId, int assetGroupId, int assetTypeId, int make,int engineTypeId)
		{
			ProductEntity prodEntity = getProductEntity(assetClassId, assetGroupId, assetTypeId, make, clientId,engineTypeId);
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
	
				AssetClassEntity assetClass = null;
				AssetGroupEntity assetGroup = null;
				AssetTypeEntity assetType = null;
				EngineTypeEntity engineType = null;
				if(prodEntity!=null)
				{
					if(productName!=null)
					{
						prodEntity.setProductName(productName);
						session.update(prodEntity);
	
					}
				}
	
				else
				{
					DomainServiceImpl domainService = new DomainServiceImpl();
					ClientEntity client = domainService.getClientDetails(clientId);
	
					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}
	
					Query query1 = session.createQuery("from AssetClassEntity where AssetClassId="+assetClassId+" and client_id="+clientEntity.getClient_id()+"");
					Iterator itr1 = query1.list().iterator();
					while(itr1.hasNext())
					{
						assetClass = (AssetClassEntity) itr1.next();
					}
	
					Query query2 = session.createQuery("from AssetGroupEntity where asset_group_id = "+assetGroupId+" and client_id="+clientEntity.getClient_id()+"");
					Iterator itr2 = query2.list().iterator();
					while(itr2.hasNext())
					{
						assetGroup = (AssetGroupEntity) itr2.next();
					}
	
					Query query3 = session.createQuery("from AssetTypeEntity where asset_type_id = "+assetTypeId+" and client_id="+clientEntity.getClient_id()+"");
					Iterator itr3 = query3.list().iterator();
					while(itr3.hasNext())
					{
						assetType = (AssetTypeEntity) itr3.next();
					}
	
					query2 = session.createQuery("from EngineTypeEntity where engineTypeId = "+engineTypeId );
					itr2 = query2.list().iterator();
					while(itr2.hasNext())
					{
						engineType = (EngineTypeEntity) itr2.next();
					}
	
	
					ProductEntity productEntity = new ProductEntity();
					productEntity.setClientId(client);
					productEntity.setProductName(productName);
					productEntity.setAssetClassId(assetClass);
					productEntity.setAssetGroupId(assetGroup);
					productEntity.setAssetTypeId(assetType);
					productEntity.setMake(make);
					productEntity.setEngineTypeId(engineType);
					session.save(productEntity);
	
				}
			}
	
			catch(Exception e)
			{
				fLogger.fatal("Exception :"+e.getMessage());
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
	
		//************************************************* Create New Product Entity ***************************************************
		/** This method creates a new Product Entity
		 * @param productName Product Name in String
		 * @param clientId Client under which the product has to be defined
		 * @param assetGroup Asset Profile
		 * @param assetType Asset type - Model
		 * @param engineType Engine Type Details
		 * @param make product Make year
		 * @return Returns the ProductEntity that has been created
		 */
		public ProductEntity createProductEntity(String productName, ClientEntity clientId, AssetGroupEntity assetGroup, AssetTypeEntity assetType,
				EngineTypeEntity engineType, int make)
		{
			ProductEntity product = null;
	
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			Logger fLogger = FatalLoggerClass.logger;
	
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
				product = new ProductEntity();
				product.setAssetGroupId(assetGroup);
				product.setAssetTypeId(assetType);
				product.setClientId(clientId);
				product.setEngineTypeId(engineType);
				product.setMake(make);
				product.setProductName(productName);
	
				session.save(product);
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
			return product;
		}
	
		//*************************************************END of Create New Product Entity ***************************************************
	
		//METHOD 3:
		//For the given list of input filter criteria, frame a sql query
		/*public String getUserAccessibleAssetQuery(String userTenancyList, String childTenancyList,String serialNumber,String assetProductIdList,
													String customGroupIdList,String eventTypeIdList,String eventSeverityList)
		{
	
			String fromQueryString="";
			String whereQueryString="";
	
			String basicQuery = "Select c.Serial_Number as serialNumber from tenancy_dimension a, tenancy_bridge B, " +
								"remote_monitoring_fact_data_yearagg c where a.Tenancy_ID= B.Parent_Id and B.Child_ID= c.Tenancy_ID	" +
								"and a.Tenancy_ID in ("+userTenancyList+") and c.year = (select max(year) from remote_monitoring_fact_data_yearagg c)";
	
			if(childTenancyList!=null)
			{
				basicQuery = basicQuery + "and B.Child_ID in ("+childTenancyList+")";
			}
	
	
			if(assetProductIdList!=null || customGroupIdList!=null || eventTypeIdList!=null || eventSeverityList!=null || serialNumber!=null)
			{
				fromQueryString = "select a.serialNumber from ("+basicQuery+")a";
				whereQueryString = " where 1";
			}
			if(serialNumber!=null || assetProductIdList!=null)
			{
				fromQueryString=fromQueryString+",asset c";
				whereQueryString = whereQueryString + " and a.serialNumber = c.Serial_Number ";
				if(serialNumber!=null)
					whereQueryString=whereQueryString+" and c.Serial_Number='"+serialNumber+"'";
				if(assetProductIdList!=null)
					whereQueryString = whereQueryString+" and c.Product_ID in ("+assetProductIdList+") ";
				return (fromQueryString+whereQueryString);
	
			}
			if(customGroupIdList!=null)
			{
				fromQueryString=fromQueryString+",custom_asset_group_member b";
				whereQueryString = whereQueryString+" and a.serialNumber = b.Serial_Number and b.Group_ID in ("+customGroupIdList+")";
			}
			if(assetProductIdList!=null)
			{
				fromQueryString=fromQueryString+",asset c";
				whereQueryString = whereQueryString+" and a.serialNumber = c.Serial_Number and c.productId in ("+assetProductIdList+") ";
	
			}
			if(eventTypeIdList!=null || eventSeverityList!=null)
			{
				fromQueryString=fromQueryString+",asset_event d";
				whereQueryString = whereQueryString+" and a.serialNumber = d.Serial_Number";
				if(eventTypeIdList!=null)
					whereQueryString = whereQueryString+" and d.Event_Type_ID in ("+eventTypeIdList+")";
				if(eventSeverityList!=null)
					whereQueryString = whereQueryString+" and d.Event_Severity in("+eventSeverityList+")";
			}
	
	
			if(assetProductIdList!=null || customGroupIdList!=null || eventTypeIdList!=null || eventSeverityList!=null || serialNumber!=null)
			{
				return (fromQueryString+whereQueryString);
			} 
			else
			{
				return basicQuery;
			}
		}
		 */
	
		public String getUserAccessibleAssetQuery(String userTenancyList){
	
			String basicQuery = "select a.serial_number from asset_owners a, account_tenancy b where a.account_id = b.account_id and b.tenancy_id in ("+userTenancyList+")";
			return basicQuery;
	
		}
		//******************************************************** Get asset dashboard details ******************************************************
	
		/** This method returns the dashboard details of user accessible list of Assets
		 * DefectID:1120 Modified by Rajani Nagaraju - 20130813 - Tweaking the query to reduce the turn around time
		 * DefectId: - 1434 - Rajani Nagaraju - 20131010 - Connectivity status redefined
		 * DF20140115 - Rajani Nagaraju - To display only those VINs that are in the current hierarchy
		 * @param userTenancyList List of userTenancy
		 * @param childTenancyList List of child tenancy
		 * @param serialNumber VIN as String input
		 * @param assetGroupIdList List of Machine Profile Id
		 * @param modelList List of modelId
		 * @param customGroupIdList List of customAssetGroup
		 * @param customGroupTypeIdList List of customAssetGroupType
		 * @param eventTypeIdList List of eventTypeId
		 * @param eventSeverityList List of eventSeverity
		 * @param pageNumber current Pagenumber
		 * @param landmarkId List of landmarkId 
		 * @param MobileNumber as String input
		 * @param landmarkCategoryId list of landmark category Id
		 * @return List of VINs with their dashboard details
		 * @throws CustomFault 
		 */
		//DF20160715 @Roopa Fetching assetdashboard details from New AMS table
		public List<AssetDashboardImpl> getAssetDashboardDetails_Old(List<Integer> userTenancyList, List<Integer> childTenancyList,String serialNumber,
				List<Integer> assetGroupIdList,List<Integer> modelList,List<Integer> customGroupIdList,List<Integer> customGroupTypeIdList,
				List<Integer> eventTypeIdList,List<String> eventSeverityList,int pageNumber, List<Integer> landmarkId, List<Integer> landmarkCategoryId,
				boolean isOwnStock,String mobileNumber,String loginId) throws CustomFault
	
				{
	
			List<AssetDashboardImpl> assetDashboardList = new LinkedList<AssetDashboardImpl>();
	
			int retroFLag=0;
	
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
	
			long startTime = System.currentTimeMillis();
			iLogger.info("into the bo class ::loginid"+loginId);
			//to get only the 50 records, according to the page number calculate X for Limit X,Y - 
			//Y is always 50: number of records to be retrieved.
			//X: start point of the record to be retrieved. (first record is 0)
			int pge;
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
	
			Session session = HibernateUtil.getSessionFactory().openSession();
	
	
			try
			{
				//Keerthi : 12/02/14 : SEARCH : if m/c no.(7 digits) provided , get corresponding PIN 
				if(serialNumber!=null)
				{
					if(serialNumber.trim().length()==7)
					{
						String machineNumber = serialNumber;
						serialNumber = getSerialNumberMachineNumber(machineNumber);
						if(serialNumber==null)
						{//invalid machine number
							iLogger.info("Machine number "+ machineNumber + "does not exist !!!");
							return assetDashboardList ;
						}
					}
				}
				//DefectId:20150706 @Suprava Adding Mobile Number as New Search criteria in Fleet General Tab.	
				ListToStringConversion conversion = new ListToStringConversion();
				List<String> contactIdList = new LinkedList<String>();
				List<Integer> contactTeancyIdList =new ArrayList<Integer>();
				List<Integer> accountIdList = new LinkedList<Integer>();
				String primaryMobileNumber = null;
				int contactTeancyId = 0;
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().openSession();
				}
				if(mobileNumber!=null)
				{
					iLogger.info("search with mobile number::"+mobileNumber+"  loginId::"+loginId);
					if(mobileNumber.trim().length()==10)
					{
						//aj20119610 group based view changes
						Query queryContact = session.createQuery("select c.primary_mobile_number,ac.account_id,c.contact_id,at.tenancy_id from ContactEntity c,AccountContactMapping ac,AccountTenancyMapping at  where c.primary_mobile_number = '"+mobileNumber+"'" +
								" and c.contact_id=ac.contact_id and ac.account_id=at.account_id and c.active_status=true");
						Object[] resultSet = null;
						Iterator itrContact = queryContact.list().iterator();
	
						while(itrContact.hasNext())
						{
							resultSet = (Object[]) itrContact.next();
							if(resultSet[0]!=null){
								primaryMobileNumber = (String) resultSet[0];
							}
							if(resultSet[1]!=null){
								AccountEntity accountEntityObj = (AccountEntity)resultSet[1];
								//int accountId = accountEntityObj.getAccount_id();
								accountIdList.add(accountEntityObj.getAccount_id());
							}
							if(resultSet[2]!=null){
								String contactId = (String) resultSet[2];
								contactIdList.add(contactId);
							}
							if(resultSet[3]!=null){
								TenancyEntity tenancyEntityObj = (TenancyEntity)resultSet[3];
								contactTeancyId = tenancyEntityObj.getTenancy_id();
							}
						}
	
						//To validate the Tenancy Hierarchy
						String hierarchyLevel = null;
						int level = 0;
						String userloginIdListAsString = conversion.getIntegerListString(userTenancyList).toString();
						Query queryTenancy = session.createQuery("select parentId,childId,level from TenancyBridgeEntity where parentId in ("+userloginIdListAsString+")" +
								" and childId ='"+contactTeancyId+"' ");
						Object[] resultSetTenancy = null;
						Iterator itrTenancy = queryTenancy.list().iterator();
						while(itrTenancy.hasNext())
						{
							resultSetTenancy = (Object[]) itrTenancy.next();
							if(resultSetTenancy[2]!=null){
								level = (Integer) resultSetTenancy[2];
							}
							hierarchyLevel = String.valueOf(level);
						}
						// System.out.println("hierarchyLevel:"+hierarchyLevel);
	
						if(hierarchyLevel==null){
							//Not to dispaly the above Hierarchy machine Details
							iLogger.info("Mobile Number "+ mobileNumber + "does not exist !!!");
							return assetDashboardList ;
						}
						if(primaryMobileNumber==null)
						{//invalid Mobile number
							iLogger.info("Mobile Number "+ mobileNumber + "does not exist !!!");
							return assetDashboardList ;
						}
						if(accountIdList.size()==0){
							//invalid account
							iLogger.info("No Contact exist for Given Mobile Number: "+ mobileNumber + " !!!");
							return assetDashboardList ;
						}
					}
					// DF20180307 @Mani :: display retrofitment machines only in the
					// fleet page ::in mobilenum field Retrofit value is received if
					// retrofit machines filter is selected for the display
					if(mobileNumber.equalsIgnoreCase("Retrofit"))
					{
						retroFLag=1;	
					}
				}
				//DefectId:20150706 End
	
				//get Client Details
				Properties prop1 = new Properties();
				String clientName=null;
	
				prop1.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				clientName= prop1.getProperty("ClientName");
	
	
				//END of get Client Details	  
	
				String userTenancyListAsString ;
	
				String accountIdListAsString = conversion.getIntegerListString(accountIdList).toString();
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().openSession();
				}
	
				if( ! (childTenancyList==null || childTenancyList.isEmpty())) 
				{
					//Get the list of accounts corresponding to the child tenancy
					userTenancyListAsString = conversion.getIntegerListString(childTenancyList).toString(); 
	
				}
				else
				{
					userTenancyListAsString = conversion.getIntegerListString(userTenancyList).toString(); 
				}
	
				List<Integer> userAccList = new LinkedList<Integer>();
				Query accountQ = session.createQuery(" from AccountTenancyMapping where tenancy_id in ("+userTenancyListAsString+")");
				Iterator accountItr = accountQ.list().iterator();
				while(accountItr.hasNext())
				{
					AccountTenancyMapping accTen = (AccountTenancyMapping)accountItr.next();
					userAccList.add(accTen.getAccount_id().getAccount_id());
				}
	
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
	
				String userAccListAsString = conversion.getIntegerListString(userAccList).toString();
	
				String mainSelectQuery="";
				String mainFromQuery="";
				String mainWhereQuery="";
				String mainGroupByQuery="";
				String mainOrderByQuery="";
	
				String topSelectQuery="";
	
				topSelectQuery = "select aa.*,bb.* from ";
	
				/*mainSelectQuery = " select ams.Serial_Number, ams.Latest_Transaction_Timestamp as transactionTime, ams.Latest_Created_Timestamp, ams.Fuel_Level, ams.parameters, a.Engine_Number, a.Product_ID, ap.notes";
				mainFromQuery = " from asset_monitoring_snapshot_new ams, asset a, asset_owner_snapshot aos, asset_profile ap " ;*/
	
				//DF20161222 @Roopa Fetching assetdashboard details from new json from ams
				//DF20170803 @KO369761 Passing TimeZone with the query from Asset table
				//DF20180808 @KO369761 fetching Country code from Asset table
				mainSelectQuery = " select ams.Serial_Number, ams.Latest_Transaction_Timestamp as transactionTime, ams.Latest_Created_Timestamp, ams.TxnData, a.Engine_Number, a.Product_ID, ap.notes, a.timeZone, a.country_code";
				mainFromQuery = " from asset_monitoring_snapshot ams, asset a, asset_owner_snapshot aos, asset_profile ap " ;
	
				if(accountIdListAsString!=null && primaryMobileNumber!=null){
					mainWhereQuery = " where ams.Serial_Number=aos.Serial_Number and aos.Account_ID in ("+accountIdListAsString+")" +
							" and ams.Latest_Transaction_Timestamp >= aos.Ownership_Start_Date " +
							" and ams.Serial_Number=a.Serial_Number and a.Status= 1 ";
				}
				else{
					mainWhereQuery = " where ams.Serial_Number=aos.Serial_Number  and aos.Account_ID in ("+userAccListAsString+")" +
							" and ams.Latest_Transaction_Timestamp >= aos.Ownership_Start_Date " +
							" and ams.Serial_Number=a.Serial_Number and a.Status= 1 ";
				}
	
				mainWhereQuery = mainWhereQuery + " and ap.serialNumber=ams.Serial_Number ";
	
				mainGroupByQuery = " group by ams.Serial_Number";
	
				mainOrderByQuery = " order by ams.Latest_Transaction_Timestamp desc ";
	
	
				//If any additional filters are applied
				//Get only the list of machines currently owned by logged in user
	
	
				if(isOwnStock)
				{
					mainWhereQuery = mainWhereQuery + " and a.Primary_Owner_ID=aos.Account_ID ";
				}
				//DF20180307 @Mani :: display retrofitment machines only in the fleet page
				if(retroFLag==1)
				{
					mainWhereQuery=mainWhereQuery+"and a.Retrofit_Flag=1";
				}
				if(! (customGroupIdList==null || customGroupIdList.isEmpty()) )
				{
					String customAssetGroupStringList = conversion.getIntegerListString(customGroupIdList).toString();
					mainFromQuery = mainFromQuery + " , custom_asset_group d, custom_asset_group_member e ";
					mainWhereQuery = mainWhereQuery + " and d.Group_ID = e.Group_ID and d.Group_ID in ("+customAssetGroupStringList+") and " +
							" e.Serial_Number = ams.Serial_Number and d.Client_ID=(select cl.Client_ID from clients cl where cl.Client_Name='"+clientName+"') and d.Active_Status=1 ";
				}
	
				else if (! (customGroupTypeIdList==null || customGroupTypeIdList.isEmpty()) )
				{
					String customAssetGroupTypeStringList = conversion.getIntegerListString(customGroupTypeIdList).toString();
					mainFromQuery = mainFromQuery + " , custom_asset_group d, custom_asset_group_member e ";
					mainWhereQuery = mainWhereQuery + " and d.Group_ID = e.Group_ID and d.Parent_Group_ID in ("+customAssetGroupTypeStringList+") and " +
							" e.Serial_Number = ams.Serial_Number and d.Client_ID=(select cl.Client_ID from clients cl where cl.Client_Name='"+clientName+"') and d.Active_Status=1 ";
				}
	
				if(! (assetGroupIdList==null || assetGroupIdList.isEmpty()) )
				{
					String assetGroupStringList = conversion.getIntegerListString(assetGroupIdList).toString();
					mainWhereQuery = mainWhereQuery + " and aos.Asset_Group_ID in ( "+ assetGroupStringList +" ) "; 
				}
	
				if ( ! (modelList==null || modelList.isEmpty()) ) 
				{
					String modelIdStringList = conversion.getIntegerListString(modelList).toString();
					mainWhereQuery = mainWhereQuery +" and aos.Asset_Type_ID in ( "+ modelIdStringList +" ) ";
	
				}
	
				if(! (serialNumber==null || serialNumber.isEmpty() ) )
				{
					iLogger.info("search with serial number::"+serialNumber+" login id::"+loginId);
					mainWhereQuery = mainWhereQuery + " and ams.Serial_Number= '"+ serialNumber+"' ";
				}
	
				if(! (landmarkId==null || landmarkId.isEmpty() ) )
				{
					String landmarkIdStringList = conversion.getIntegerListString(landmarkId).toString();
					mainFromQuery = mainFromQuery + " , landmark g, landmark_asset h ";
					mainWhereQuery = mainWhereQuery + " and g.landmark_id = h.landmark_id and g.ActiveStatus=1 and g.landmark_id in ("+landmarkIdStringList+") and "+
							" h.serial_number = ams.Serial_Number " ;
				}
	
				else if(! (landmarkCategoryId==null || landmarkCategoryId.isEmpty() ) )
				{
					String landmarkCategoryIdStringList = conversion.getIntegerListString(landmarkCategoryId).toString();
					mainFromQuery = mainFromQuery + " , landmark g, landmark_asset h ";
					mainWhereQuery = mainWhereQuery + " and g.landmark_id = h.landmark_id and g.ActiveStatus=1 and g.landmark_category_id in ("+landmarkCategoryIdStringList+") and "+
							" h.serial_number = ams.Serial_Number " ;
				}
	
				if( (! (eventTypeIdList==null || eventTypeIdList.isEmpty()) ) || 
						(! (eventSeverityList==null || eventSeverityList.isEmpty()) )	)
				{
					//DF20170130 @Roopa for Role based alert implementation
					DateUtil utilObj=new DateUtil();
					List<String> alertCodeList= utilObj.roleAlertMapDetails(null,userTenancyList.get(0), "Display");
	
					StringBuilder alertCodeListAsString=conversion.getStringList(alertCodeList);
	
					mainFromQuery = mainFromQuery+" , asset_event aee, business_event be ";
					/*mainWhereQuery = mainWhereQuery + " and aee.Event_ID = ( select max(aevt.Event_ID) from " +
							" asset_event aevt where aevt.Serial_Number=ams.Serial_Number and aevt.Active_Status=1 ";*/
					//DF20190204 @abhishek---->add partition key for faster retrieval
					mainWhereQuery = mainWhereQuery + " and aee.Serial_Number=ams.Serial_Number and aee.Active_Status=1 and aee.PartitionKey =1 ";
	
					mainWhereQuery=mainWhereQuery+" and be.Event_ID=aee.Event_ID and (be.Alert_Code in ("+alertCodeListAsString+")) ";
				}
	
	
	
				if(! (eventTypeIdList==null || eventTypeIdList.isEmpty()) )
				{
					String eventTypeIdStringList = conversion.getIntegerListString(eventTypeIdList).toString();
					//mainWhereQuery = mainWhereQuery+ " and aevt.Event_Type_ID in ("+eventTypeIdStringList+")";
					mainWhereQuery = mainWhereQuery+ " and aee.Event_Type_ID in ("+eventTypeIdStringList+")";
				}
	
				if(! (eventSeverityList==null || eventSeverityList.isEmpty()) )
				{
					String eventSeverityListAsString = conversion.getStringList(eventSeverityList).toString();
					//mainWhereQuery = mainWhereQuery + " and aevt.Event_Severity in ("+eventSeverityListAsString+")";
					mainWhereQuery = mainWhereQuery + " and aee.Event_Severity in ("+eventSeverityListAsString+")";
	
				}
				/*if( (! (eventTypeIdList==null || eventTypeIdList.isEmpty()) ) || 
						(! (eventSeverityList==null || eventSeverityList.isEmpty())))
				{
					mainWhereQuery=mainWhereQuery+")";
				}*/
	
				String leftjoinSelectQuery="";
				String leftjoinFromQuery="";
				String leftjoinWhereQuery="";
	
				leftjoinSelectQuery=" select p.Product_ID, ag.Asseet_Group_Name, aty.Asset_Type_Name, aty.Asset_ImageFile_Name, et.Engine_Type_Name";
	
				leftjoinFromQuery=" from asset_group ag, asset_type aty, engine_type et,products p";
	
				leftjoinWhereQuery=" where p.Asset_Group_ID=ag.Asset_Group_ID and p.Asset_Type_ID=aty.Asset_Type_ID and p.Engine_Type_id=et.Engine_Type_id";
	
				String leftjoinONQuery=" on aa.Product_ID=bb.Product_ID ";
	
				String limitQuery=" LIMIT "+startLimit+""+","+""+endLimit+"";
	
	
				String mainQuery = topSelectQuery+"("+mainSelectQuery+mainFromQuery+mainWhereQuery+mainGroupByQuery+mainOrderByQuery+") aa left outer join ("+leftjoinSelectQuery+leftjoinFromQuery+leftjoinWhereQuery+") bb"+leftjoinONQuery+limitQuery;
	
				iLogger.info("loginiD::"+loginId+"::finalQueryString for AssetDashboardService::getAssetDashboardDetails: "+mainQuery);
	
	
				DynamicAMS_DAL amsDaoObj=new DynamicAMS_DAL();
	
				long startTime1=System.currentTimeMillis();
	
				assetDashboardList=amsDaoObj.getQuerySpecificDetailsForAssetDashBoard(mainQuery,loginId);
	
				long endTime1=System.currentTimeMillis();
	
				iLogger.info("loginiD::"+loginId+" AssetDashboardService BO DAL Query From New AMS Webservice Execution Time in ms:"+(endTime1-startTime1));
	
				//iLogger.info("AssetDashboardService:AMS DAL::getQuerySpecificDetails Size:"+assetDashboardList.size());
	
				mainSelectQuery ="select count(*) as count ";
				mainQuery = mainSelectQuery+mainFromQuery+mainWhereQuery;
	
				//iLogger.info("Count Query for AssetDashboardService::getAssetDashboardDetails: "+mainQuery);
	
	
	
				//Get Total Number of mahcines in fleet 
	
	
				AssetDashboardImpl assetDashboard = new AssetDashboardImpl();
				assetDashboard = new AssetDashboardImpl();
	
				long count =0;
				count=amsDaoObj.getAssetDashBoardTotalCount(mainQuery);
	
				String countInString=null;
				countInString=String.valueOf(count);
				iLogger.info("AssetDashBoard Total Count::"+countInString);
				assetDashboard.setSerialNumber(countInString);
				assetDashboard.setNotes("Sumarry Count");
				assetDashboardList.add(assetDashboard);
	
	
			}
	
			/**
			 * KO369761 - DF20181114
			 * Throwing sql exception as custom fault exception to UI.
			 **/
			catch(CustomFault e){
				System.out.println(e.getFaultInfo());
				String exceptionType = e.getFaultInfo();
				exceptionType = exceptionType.toLowerCase();
				System.out.println(exceptionType);
				if(exceptionType.contains("sql")){
					System.out.println("SQL Exception occurred.");
					throw new CustomFault("SQL Exception");
				}
			}
	
			catch(Exception e)
			{
				fLogger.fatal("loginId::"+loginId+" Exception :"+e);
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				e.printStackTrace();
				fLogger.fatal(stack.toString());
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
			iLogger.info("loginId::"+loginId+"::assetDashboardList Total size::"+assetDashboardList.size());
			long endTime = System.currentTimeMillis();
			iLogger.info("loginId::"+loginId+"::BO End time ::"+(endTime-startTime));
			return assetDashboardList;
	
	
	
				}
	
		//DF20190205 Abhishek:: Created new method to improve performance
		public List<AssetDashboardImpl> getAssetDashboardDetails(List<Integer> userTenancyList, List<Integer> childTenancyList,String serialNumber,
				String nickName,List<Integer> assetGroupIdList,List<Integer> modelList,List<Integer> customGroupIdList,List<Integer> customGroupTypeIdList,
				List<Integer> eventTypeIdList,List<String> eventSeverityList,int pageNumber, List<Integer> landmarkId, List<Integer> landmarkCategoryId,
				boolean isOwnStock,String mobileNumber,String loginId) throws CustomFault
				{



			List<AssetDashboardImpl> assetDashboardList = new LinkedList<AssetDashboardImpl>();
			
			int retroFLag=0;
			
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			
			long startTime = System.currentTimeMillis();
			iLogger.info("into the bo class ::loginid"+loginId);
			//to get only the 50 records, according to the page number calculate X for Limit X,Y - 
			//Y is always 50: number of records to be retrieved.
			//X: start point of the record to be retrieved. (first record is 0)
			int pge;
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
			
			Session session = HibernateUtil.getSessionFactory().openSession();
			
			
			try
			{
				//Keerthi : 12/02/14 : SEARCH : if m/c no.(7 digits) provided , get corresponding PIN 
				if(serialNumber!=null)
				{
					if(serialNumber.trim().length()==7)
					{
						String machineNumber = serialNumber;
						serialNumber = getSerialNumberMachineNumber(machineNumber);
						if(serialNumber==null)
						{//invalid machine number
							iLogger.info("Machine number "+ machineNumber + "does not exist !!!");
							return assetDashboardList ;
						}
					}
				}
			  //DefectId:20150706 @Suprava Adding Mobile Number as New Search criteria in Fleet General Tab.	
				ListToStringConversion conversion = new ListToStringConversion();
				List<String> contactIdList = new LinkedList<String>();
				List<Integer> accountIdList = new LinkedList<Integer>();
				List<Integer> contactTeancyIdList =new ArrayList<Integer>();//100016276.n
				String primaryMobileNumber = null;
				int contactTeancyId = 0;
				if(! (session.isOpen() ))
	            {
	                 session = HibernateUtil.getSessionFactory().openSession();
	             }
				if(mobileNumber!=null)
				{
					iLogger.info("search with mobile number::"+mobileNumber+"  loginId::"+loginId);
					if(mobileNumber.trim().length()==10)
					{
						//DF20190802:Jayanthi::To show all the machines related to mobile no.
						/*Query queryContact = session.createQuery("select c.primary_mobile_number,ac.account_id,c.contact_id,at.tenancy_id from ContactEntity c,AccountContactMapping ac,AccountTenancyMapping at where " +
	                                          "c.primary_mobile_number in ( select a.mobile_no from AccountEntity a where a.mappingCode in ( select acc.mappingCode from "+
	                                          "AccountEntity acc where acc.mobile_no = '"+mobileNumber+"'))" +
	                                          " and c.contact_id=ac.contact_id and ac.account_id=at.account_id and c.active_status=true");*/
						
						//DF20190910: @Mamatha Below query modified for incorrect data while Search based on mobile number from Fleet page.
						Query queryContact = session.createQuery("select c.primary_mobile_number,bp.account_id,c.contact_id,at.tenancy_id " +
								" from ContactEntity c,AccountContactMapping ac,AccountEntity a,AccountEntity bp,AccountTenancyMapping at " +
								" where c.contact_id=ac.contact_id and ac.account_id=a.account_id and c.primary_mobile_number = '"+mobileNumber+"' " +
								" and a.mappingCode=bp.mappingCode and bp.account_id=at.account_id and c.active_status=1");
						
						
						Object[] resultSet = null;
						Iterator itrContact = queryContact.list().iterator();

						while(itrContact.hasNext())
						{
							resultSet = (Object[]) itrContact.next();
							if(resultSet[0]!=null){
								primaryMobileNumber = (String) resultSet[0];
							}
							//100016953 : Sai Divya : 20250821 : MobileNumberSearch.sn
						/*	if(resultSet[1]!=null){
								//DF20190910: @Mamatha Below query modified for incorrect data while Search based on mobile number from Fleet page.
								//AccountEntity accountEntityObj = (AccountEntity)resultSet[1];
								//int accountId = accountEntityObj.getAccount_id();
								//accountIdList.add(accountEntityObj.getAccount_id());
								
								accountIdList.add((Integer)resultSet[1]);

							}*/
							//100016953 : Sai Divya : 20250821 : MobileNumberSearch.en
							if(resultSet[2]!=null){
							String contactId = (String) resultSet[2];
							contactIdList.add(contactId);
							}
							if(resultSet[3]!=null){
								TenancyEntity tenancyEntityObj = (TenancyEntity)resultSet[3];
								contactTeancyId = tenancyEntityObj.getTenancy_id();
								contactTeancyIdList.add(tenancyEntityObj.getTenancy_id());//100016276.n
								
							}
						}
						
//						String contactTeancyIdListAsString = contactTeancyIdList.stream()
//							    .map(String::valueOf)
//							    .collect(Collectors.joining(","));//100016276.n
						//100016953 : Sai Divya : 20250821 : MobileNumberSearch.sn
						String contactTeancyIdListAsString = contactTeancyIdList.stream()
							    .map(id -> "'" + id + "'")
							    .collect(Collectors.joining(","));
						//100016953 : Sai Divya : 20250821 : MobileNumberSearch.en
						//To validate the Tenancy Hierarchy
						String hierarchyLevel = null;
						int level = 0;
//						int childId=0;
						List<Integer> childId=new LinkedList<>();//100016953.n
						String userloginIdListAsString = conversion.getIntegerListString(userTenancyList).toString();
//						Query queryTenancy = session.createQuery("select parentId,childId,level from TenancyBridgeEntity where parentId in ("+userloginIdListAsString+")" +
//								" and childId ='"+contactTeancyId+"' ");//100016276.o
//						Query queryTenancy = session.createQuery("select parentId,childId,level from TenancyBridgeEntity where parentId in ("+userloginIdListAsString+")" +
//						" and childId ='"+contactTeancyIdListAsString+"' ");//100016276.n
						
						String hql = "select parentId, childId, level from TenancyBridgeEntity " +
					             "where parentId in (" + userloginIdListAsString + ") " +
					             "and childId in (" + contactTeancyIdListAsString + ")";//100016953.n
						Query queryTenancy = session.createQuery(hql);//100016953.n
						iLogger.info("queryTenancy :"+queryTenancy);
			        	Object[] resultSetTenancy = null;
				        Iterator itrTenancy = queryTenancy.list().iterator();
				        while(itrTenancy.hasNext())
						{
				        	resultSetTenancy = (Object[]) itrTenancy.next();
							if(resultSetTenancy[2]!=null){
						    level = (Integer) resultSetTenancy[2];
							}
							//100016953 : Sai Divya : 20250821 : MobileNumberSearch.sn
							if(resultSetTenancy[1]!=null){
								childId.add((Integer) resultSetTenancy[1]);
							}
							//100016953 : Sai Divya : 20250821 : MobileNumberSearch.en
							hierarchyLevel = String.valueOf(level);
						}
				      //100016953 : Sai Divya : 20250821 : MobileNumberSearch.sn
				        String childIdList = childId.stream()
							    .map(id -> "'" + id + "'")
							    .collect(Collectors.joining(","));
				       // System.out.println("hierarchyLevel:"+hierarchyLevel);
				      //get account list from contactTeancyIdListAsString(list of child tenancy for logged in user for the searched mobile no.) from account_tenancy table
						String accountQuery = "SELECT Account_ID FROM account_tenancy WHERE Tenancy_ID IN (" + childIdList + ")";
						iLogger.info("accountQuery "+accountQuery);
						ConnectMySQL connMySql = new ConnectMySQL();

						try (Connection prodConnection = connMySql.getConnection();
						     PreparedStatement preparedStatement = prodConnection.prepareStatement(accountQuery)) {

						    ResultSet resultSet1 = preparedStatement.executeQuery();
						    while (resultSet1.next()) {
						        accountIdList.add(resultSet1.getInt("Account_ID"));
						    }

						} catch (SQLException e) {
						    e.printStackTrace();
						    fLogger.fatal("Exception Occured:"+e.getMessage());
						}
						iLogger.info("accountIdList "+accountIdList);
						//100016953 : Sai Divya : 20250821 : MobileNumberSearch.en
				        if(hierarchyLevel==null){
				        	//Not to dispaly the above Hierarchy machine Details
				        	iLogger.info("Mobile Number "+ mobileNumber + "does not exist !!!");
							return assetDashboardList ;
				        }
						if(primaryMobileNumber==null)
						{//invalid Mobile number
							iLogger.info("Mobile Number "+ mobileNumber + "does not exist !!!");
							return assetDashboardList ;
						}
						if(accountIdList.size()==0){
							//invalid account
							iLogger.info("No Contact exist for Given Mobile Number: "+ mobileNumber + " !!!");
							return assetDashboardList ;
						}
					}
					// DF20180307 @Mani :: display retrofitment machines only in the
					// fleet page ::in mobilenum field Retrofit value is received if
					// retrofit machines filter is selected for the display
					if(mobileNumber.equalsIgnoreCase("Retrofit"))
					{
						retroFLag=1;	
					}
				}
				//DefectId:20150706 End
				
				//get Client Details
				Properties prop1 = new Properties();
				String clientName=null;
					
				prop1.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				clientName= prop1.getProperty("ClientName");
	      
				
				//END of get Client Details	  
				
				String userTenancyListAsString ;
			
				String accountIdListAsString = conversion.getIntegerListString(accountIdList).toString();
				if(! (session.isOpen() ))
	            {
	                 session = HibernateUtil.getSessionFactory().openSession();
	             }
			
				if( ! (childTenancyList==null || childTenancyList.isEmpty())) 
				{
					//Get the list of accounts corresponding to the child tenancy
					userTenancyListAsString = conversion.getIntegerListString(childTenancyList).toString(); 
					
				}
				else
				{
					userTenancyListAsString = conversion.getIntegerListString(userTenancyList).toString(); 
				}
				
				List<Integer> userAccList = new LinkedList<Integer>();
				if(!userTenancyListAsString.isEmpty()){
				Query accountQ = session.createQuery(" from AccountTenancyMapping where tenancy_id in ("+userTenancyListAsString+")");
				Iterator accountItr = accountQ.list().iterator();
				while(accountItr.hasNext())
				{
					AccountTenancyMapping accTen = (AccountTenancyMapping)accountItr.next();
					userAccList.add(accTen.getAccount_id().getAccount_id());
				}
				}
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
				
				String userAccListAsString = conversion.getIntegerListString(userAccList).toString();
				
				String mainSelectQuery="";
				String mainFromQuery="";
				String mainInnerQuery="";
				String topSelectQuery="";
				String mainCountQuery="";
				String leftOuterJoin1 = "";
				String innerJoin1 = "";
				String innerJoin2 = "";
				String leftouterJoin2 = "";

				if(customGroupIdList==null || (customGroupIdList != null && customGroupIdList.isEmpty())){

					topSelectQuery = "select aa.* ,bb.* ,aes.AlertStatus from (";
					mainSelectQuery = " select c.*, a.Engine_Number, a.Product_ID, a.timeZone, a.country_code ,d.notes ";
					mainFromQuery = " from asset a , asset_owner_snapshot b " ;
					mainFromQuery = mainFromQuery + " ,(select serial_number, latest_Transaction_Timestamp as transactionTime, Latest_Created_Timestamp, TxnData  from asset_monitoring_snapshot  ";
					mainFromQuery = mainFromQuery + ") c, asset_profile d ";
					
					if((!(eventTypeIdList==null || eventTypeIdList.isEmpty())) || (!(eventSeverityList==null || eventSeverityList.isEmpty()))){
						mainFromQuery = mainFromQuery + ", asset_event aee";
						mainFromQuery = mainFromQuery + ", business_event be";
					}
					
					if(accountIdListAsString!=null && primaryMobileNumber!=null){
						//old
						//mainInnerQuery = " where a.serial_number = d.serialnumber and  a.serial_number=c.serial_number and a.serial_number =b.serial_number "
						//		+ " and b.Account_ID in ("+accountIdListAsString+") and  c.transactionTime > '2014-01-01 00:00:00' ";
					    //JCB6444 : 20230719 : Prasanna Lakshmi : Getting Oops Error 
						//JCB6444.sn
						mainInnerQuery = " where a.status=1 and a.serial_number = d.serialnumber and  a.serial_number=c.serial_number and a.serial_number =b.serial_number "
								+ " and b.Account_ID in ("+accountIdListAsString+") and  c.transactionTime > '2014-01-01 00:00:00' ";
					    //JCB6444.en
					}else {
						//old
						//mainInnerQuery = " where a.serial_number = d.serialnumber and  a.serial_number=c.serial_number and a.serial_number =b.serial_number "
						//	+ " and b.Account_ID in ("+userAccListAsString+") and  c.transactionTime > '2014-01-01 00:00:00' ";
						//JCB6444 : 20230719 : Prasanna Lakshmi : Getting Oops Error 
						//JCB6444.sn
						mainInnerQuery = " where a.status=1 and a.serial_number = d.serialnumber and  a.serial_number=c.serial_number and a.serial_number =b.serial_number "
								+ " and b.Account_ID in ("+userAccListAsString+") and  c.transactionTime > '2014-01-01 00:00:00' ";
						//JCB6444.en
					}
					
					if((!(eventTypeIdList==null || eventTypeIdList.isEmpty())) || (!(eventSeverityList==null || eventSeverityList.isEmpty()))){
						DateUtil utilObj=new DateUtil();
						List<String> alertCodeList= utilObj.roleAlertMapDetails(null,userTenancyList.get(0), "Display");
						StringBuilder alertCodeListAsString=conversion.getStringList(alertCodeList);
						mainInnerQuery = mainInnerQuery+" AND aee.Serial_Number = c.Serial_Number AND aee.Active_Status=1 and aee.PartitionKey=1";
						mainInnerQuery = mainInnerQuery+" AND be.Event_ID=aee.Event_ID and (be.Alert_Code in ("+alertCodeListAsString+"))";
					}
					
					if(! (eventTypeIdList==null || eventTypeIdList.isEmpty()) ){
						String eventTypeIdStringList = conversion.getIntegerListString(eventTypeIdList).toString();
						mainInnerQuery = mainInnerQuery+ " and aee.Event_Type_ID in ("+eventTypeIdStringList+")";
					}
					
					if(! (eventSeverityList==null || eventSeverityList.isEmpty()) ){
						String eventSeverityListAsString = conversion.getStringList(eventSeverityList).toString();
						mainInnerQuery = mainInnerQuery + " and aee.Event_Severity in ("+eventSeverityListAsString+")";
					}
					
					if(! (nickName==null || nickName.isEmpty() ) ){
						iLogger.info("search with nickName::"+nickName+" login id::"+loginId);
						mainInnerQuery = mainInnerQuery + " and a.nickName like '%"+ nickName+"%' ";
					}
					if(isOwnStock){
						mainInnerQuery = mainInnerQuery + " and a.Primary_Owner_ID=b.Account_ID ";
					}
					if(retroFLag==1){
						mainInnerQuery=mainInnerQuery+" and a.Retrofit_Flag=1";
					}

					if(! (assetGroupIdList==null || assetGroupIdList.isEmpty()) ){
						String assetGroupStringList = conversion.getIntegerListString(assetGroupIdList).toString();
						mainInnerQuery = mainInnerQuery + " and b.Asset_Group_ID in ( "+ assetGroupStringList +" ) "; 
					}
					if ( ! (modelList==null || modelList.isEmpty()) ) {
						String modelIdStringList = conversion.getIntegerListString(modelList).toString();
						mainInnerQuery = mainInnerQuery +" and b.Asset_Type_ID in ( "+ modelIdStringList +" ) ";
					}
					//
					if(! (serialNumber==null || serialNumber.isEmpty() ) ){
						mainInnerQuery = mainInnerQuery +" and a.serial_number ='"+ serialNumber +"'";
					}
					//

					mainInnerQuery =  mainInnerQuery + " order by c.transactionTime desc LIMIT " +startLimit+""+","+""+endLimit+") aa ";
					leftOuterJoin1 = " left outer join (select p.Product_ID, ag.Asseet_Group_Name, aty.Asset_Type_Name, aty.Asset_ImageFile_Name, et.Engine_Type_Name from products p ";
					innerJoin1 = " inner join asset_group ag on ag.Asset_Group_ID = p.Asset_Group_ID inner join asset_type aty on aty.Asset_Type_ID = p.Asset_Type_ID ";
					innerJoin2 = " inner join engine_type et on et.Engine_Type_id = p.Engine_Type_id) bb on aa.product_id =bb.product_id ";
					leftouterJoin2 = " left outer join asset_event_snapshot aes on aa.Serial_Number=aes.serialNumber ";

					mainCountQuery = "select count(*) as count from ( "+
							" (select Serial_Number from asset_owner_snapshot where account_id in ("+userAccListAsString+") ) a "+ 
							" inner join "+
							" (Select Serial_Number from asset where status = 1) b "+
							" on a.serial_number = b.serial_number "+
							")";
				}else{
					//topSelectQuery = "select aa.* ,bb.* ,aes.AlertStatus from (";//ME100012110.o
					topSelectQuery = "select distinct(serial_number) serial_number,aa.transactionTime,aa.Latest_Created_Timestamp,aa.TxnData ,aa.product_id, aa. Engine_Number,aa.timeZone, aa.country_code ,aa.notes, bb.* ,aes.AlertStatus from (";//ME100012110.n
					mainSelectQuery = " select c.*, a.Engine_Number, a.Product_ID, a.timeZone, a.country_code ,d.notes ";
					mainFromQuery = " from asset a , asset_owner_snapshot b " ;
					mainFromQuery = mainFromQuery + " ,(select serial_number, latest_Transaction_Timestamp as transactionTime, Latest_Created_Timestamp, TxnData  from asset_monitoring_snapshot  ";
					mainFromQuery = mainFromQuery + ") c, asset_profile d,  custom_asset_group_snapshot cags";
					
					if((!(eventTypeIdList==null || eventTypeIdList.isEmpty())) || (!(eventSeverityList==null || eventSeverityList.isEmpty()))){
						mainFromQuery = mainFromQuery + ", asset_event aee";
						mainFromQuery = mainFromQuery + ", business_event be";
					}
				//old 	
				//	mainInnerQuery = " where a.serial_number = d.serialnumber and  a.serial_number=c.serial_number and a.serial_number =b.serial_number"
				//			+ " and b.Account_ID in ("+userAccListAsString+") and  c.transactionTime > '2014-01-01 00:00:00' ";
				//JCB6444 : 20230719 : Prasanna Lakshmi : Getting Oops Error 
				//JCB6444.sn
					mainInnerQuery = " where a.status=1 and a.serial_number = d.serialnumber and  a.serial_number=c.serial_number and a.serial_number =b.serial_number"
							+ " and b.Account_ID in ("+userAccListAsString+") and  c.transactionTime > '2014-01-01 00:00:00' ";
				//JCB6444.en	
					
					if(accountIdListAsString!=null && primaryMobileNumber!=null){
						mainInnerQuery = mainInnerQuery + " AND c.serial_number = cags.Asset_Id AND cags.user_Id in('" + loginId + "') AND b.Account_ID in ("+accountIdListAsString+")";
					}else {
						mainInnerQuery = mainInnerQuery + " AND c.serial_number = cags.Asset_Id AND cags.user_Id in('" + loginId + "') ";
					}
					String customGroupIdStringList = null;
					if(! (customGroupIdList==null || customGroupIdList.isEmpty()) ){
						customGroupIdStringList = conversion.getIntegerListString(customGroupIdList).toString();
						mainInnerQuery = mainInnerQuery + " and cags.Group_ID in ("+customGroupIdStringList+") "; 
					}
					
					if((!(eventTypeIdList==null || eventTypeIdList.isEmpty())) || (!(eventSeverityList==null || eventSeverityList.isEmpty()))){
						DateUtil utilObj=new DateUtil();
						List<String> alertCodeList= utilObj.roleAlertMapDetails(null,userTenancyList.get(0), "Display");
						StringBuilder alertCodeListAsString=conversion.getStringList(alertCodeList);
						mainInnerQuery = mainInnerQuery+" AND aee.Serial_Number = c.Serial_Number AND aee.Active_Status=1 and aee.PartitionKey=1";
						mainInnerQuery = mainInnerQuery+" AND be.Event_ID=aee.Event_ID and (be.Alert_Code in ("+alertCodeListAsString+"))";
					}
					
					if(! (eventTypeIdList==null || eventTypeIdList.isEmpty()) ){
						String eventTypeIdStringList = conversion.getIntegerListString(eventTypeIdList).toString();
						mainInnerQuery = mainInnerQuery+ " and aee.Event_Type_ID in ("+eventTypeIdStringList+")";
					}
					
					if(! (eventSeverityList==null || eventSeverityList.isEmpty()) ){
						String eventSeverityListAsString = conversion.getStringList(eventSeverityList).toString();
						mainInnerQuery = mainInnerQuery + " and aee.Event_Severity in ("+eventSeverityListAsString+")";
					}
					
					if(! (nickName==null || nickName.isEmpty() ) ){
						iLogger.info("search with nickName::"+nickName+" login id::"+loginId);
						mainInnerQuery = mainInnerQuery + " and a.nickName like '%"+ nickName+"%' ";
					}
					if(isOwnStock){
						mainInnerQuery = mainInnerQuery + " and a.Primary_Owner_ID=b.Account_ID ";
					}
					if(retroFLag==1){
						mainInnerQuery=mainInnerQuery+"and a.Retrofit_Flag=1";
					}

					if(! (assetGroupIdList==null || assetGroupIdList.isEmpty()) ){
						String assetGroupStringList = conversion.getIntegerListString(assetGroupIdList).toString();
						mainInnerQuery = mainInnerQuery + " and b.Asset_Group_ID in ( "+ assetGroupStringList +" ) "; 
					}
					if ( ! (modelList==null || modelList.isEmpty()) ) {
						String modelIdStringList = conversion.getIntegerListString(modelList).toString();
						mainInnerQuery = mainInnerQuery +" and b.Asset_Type_ID in ( "+ modelIdStringList +" ) ";
					}
					//
					if(! (serialNumber==null || serialNumber.isEmpty() ) ){
						mainInnerQuery = mainInnerQuery +" and a.serial_number ='"+ serialNumber +"'";
					}
					//
					
					mainInnerQuery =  mainInnerQuery + " order by c.transactionTime desc LIMIT " +startLimit+""+","+""+endLimit+") aa ";
					leftOuterJoin1 = " left outer join (select p.Product_ID, ag.Asseet_Group_Name, aty.Asset_Type_Name, aty.Asset_ImageFile_Name, et.Engine_Type_Name from products p ";
					innerJoin1 = " inner join asset_group ag on ag.Asset_Group_ID = p.Asset_Group_ID inner join asset_type aty on aty.Asset_Type_ID = p.Asset_Type_ID ";
					innerJoin2 = " inner join engine_type et on et.Engine_Type_id = p.Engine_Type_id) bb on aa.product_id =bb.product_id ";
					leftouterJoin2 = " left outer join asset_event_snapshot aes on aa.Serial_Number=aes.serialNumber ";

					mainCountQuery = "select count(distinct(Asset_Id)) as count from custom_asset_group_snapshot where user_Id in('"+loginId+"')" +
							" and Group_ID in("+customGroupIdStringList+")";
				}
				
				String mainQuery = topSelectQuery + mainSelectQuery + mainFromQuery + mainInnerQuery + leftOuterJoin1 + innerJoin1 + innerJoin2 + leftouterJoin2;
				iLogger.info(loginId + ":ADS:finalQueryString for AssetDashboardService::getAssetDashboardDetails: Before query execution:" + mainQuery);
				
				DynamicAMS_DAL amsDaoObj=new DynamicAMS_DAL();
				
				long startTime1=System.currentTimeMillis();
				assetDashboardList=amsDaoObj.getQuerySpecificDetailsForAssetDashBoard(mainQuery,loginId);
				long endTime1=System.currentTimeMillis();
				
				iLogger.info(loginId+":ADS: AssetDetailsBO: AssetDashBoard Query Execution Time in ms Dashboard:"+(endTime1-startTime1));

				//Get Total Number of machines in fleet 
				AssetDashboardImpl assetDashboard = new AssetDashboardImpl();
				assetDashboard = new AssetDashboardImpl();
				
				long count =0;
				long t2 = System.currentTimeMillis();
				count=amsDaoObj.getAssetDashBoardTotalCount(mainCountQuery);
				iLogger.info("Time take to get assetdashboard count" + (System.currentTimeMillis()-t2) + "ms");
				
				String countInString=null;
				countInString=String.valueOf(count);
				iLogger.info("AssetDashBoard Total Count::"+countInString);
				assetDashboard.setSerialNumber(countInString);
				assetDashboard.setNotes("Sumarry Count");
				assetDashboardList.add(assetDashboard);
			}
			
			/**
			 * KO369761 - DF20181114
			 * Throwing sql exception as custom fault exception to UI.
			 **/
			catch(CustomFault e){
				System.out.println(e.getFaultInfo());
				String exceptionType = e.getFaultInfo();
				exceptionType = exceptionType.toLowerCase();
				System.out.println(exceptionType);
				if(exceptionType.contains("sql")){
					System.out.println("SQL Exception occurred.");
					throw new CustomFault("SQL Exception");
				}
			}
			
			catch(Exception e)
			{
				fLogger.fatal("loginId::"+loginId+" Exception :"+e);
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				e.printStackTrace();
				fLogger.fatal(stack.toString());
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
			iLogger.info("loginId::"+loginId+"::assetDashboardList Total size::"+assetDashboardList.size());
			long endTime = System.currentTimeMillis();
			iLogger.info("loginId::"+loginId+"::BO End time ::"+(endTime-startTime));
			return assetDashboardList;
			
		
			
		}
	
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
		/*public List<AssetDashboardImpl> getAssetDashboardDetails(List<Integer> userTenancyList, List<Integer> childTenancyList,String serialNumber,
				List<Integer> assetGroupIdList,List<Integer> modelList,List<Integer> customGroupIdList,List<Integer> customGroupTypeIdList,
				List<Integer> eventTypeIdList,List<String> eventSeverityList,int pageNumber, List<Integer> landmarkId, List<Integer> landmarkCategoryId,
				boolean isOwnStock,String mobileNumber)
	
		{
			List<AssetDashboardImpl> assetDashboardList = new LinkedList<AssetDashboardImpl>();
	
			List<String> assetMonitoringParameters = new LinkedList<String>();
			List<String> assetMonitoringValues = new LinkedList<String>();
	
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
	
			//Performance Analysis
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startDate = sdf.format(cal.getTime());
	
	
			try
			{
				//Keerthi : 12/02/14 : SEARCH : if m/c no.(7 digits) provided , get corresponding PIN 
				if(serialNumber!=null)
				{
					if(serialNumber.trim().length()==7)
					{
						String machineNumber = serialNumber;
						serialNumber = getSerialNumberMachineNumber(machineNumber);
						if(serialNumber==null)
						{//invalid machine number
							iLogger.info("Machine number "+ machineNumber + "does not exist !!!");
							return assetDashboardList ;
						}
					}
				}
			  //DefectId:20150706 @Suprava Adding Mobile Number as New Search criteria in Fleet General Tab.	
				ListToStringConversion conversion = new ListToStringConversion();
				List<String> contactIdList = new LinkedList<String>();
				List<Integer> accountIdList = new LinkedList<Integer>();
				String primaryMobileNumber = null;
				int contactTeancyId = 0;
				if(! (session.isOpen() ))
	            {
	                 session = HibernateUtil.getSessionFactory().getCurrentSession();
	                 session.getTransaction().begin();
	            }
				if(mobileNumber!=null)
				{
					if(mobileNumber.trim().length()==10)
					{
						Query queryContact = session.createQuery("select c.primary_mobile_number,ac.account_id,c.contact_id,at.tenancy_id from ContactEntity c,AccountContactMapping ac,AccountTenancyMapping at  where c.primary_mobile_number = '"+mobileNumber+"'" +
								" and c.contact_id=ac.contact_id and ac.account_id=at.account_id ");
						Object[] resultSet = null;
						Iterator itrContact = queryContact.list().iterator();
	
						while(itrContact.hasNext())
						{
							resultSet = (Object[]) itrContact.next();
							if(resultSet[0]!=null){
								primaryMobileNumber = (String) resultSet[0];
							}
							if(resultSet[1]!=null){
								AccountEntity accountEntityObj = (AccountEntity)resultSet[1];
								//int accountId = accountEntityObj.getAccount_id();
								accountIdList.add(accountEntityObj.getAccount_id());
							}
							if(resultSet[2]!=null){
							String contactId = (String) resultSet[2];
							contactIdList.add(contactId);
							}
							if(resultSet[3]!=null){
								TenancyEntity tenancyEntityObj = (TenancyEntity)resultSet[3];
								contactTeancyId = tenancyEntityObj.getTenancy_id();
							}
						}
	
						//To validate the Tenancy Hierarchy
						String hierarchyLevel = null;
						int level = 0;
						String userloginIdListAsString = conversion.getIntegerListString(userTenancyList).toString();
						Query queryTenancy = session.createQuery("select parentId,childId,level from TenancyBridgeEntity where parentId in ("+userloginIdListAsString+")" +
						" and childId ='"+contactTeancyId+"' ");
			        	Object[] resultSetTenancy = null;
				        Iterator itrTenancy = queryTenancy.list().iterator();
				        while(itrTenancy.hasNext())
						{
				        	resultSetTenancy = (Object[]) itrTenancy.next();
							if(resultSetTenancy[2]!=null){
						    level = (Integer) resultSetTenancy[2];
							}
							hierarchyLevel = String.valueOf(level);
						}
				        System.out.println("hierarchyLevel:"+hierarchyLevel);
	
				        if(hierarchyLevel==null){
				        	//Not to dispaly the above Hierarchy machine Details
				        	iLogger.info("Mobile Number "+ mobileNumber + "does not exist !!!");
							return assetDashboardList ;
				        }
						if(primaryMobileNumber==null)
						{//invalid Mobile number
							iLogger.info("Mobile Number "+ mobileNumber + "does not exist !!!");
							return assetDashboardList ;
						}
						if(accountIdList.size()==0){
							//invalid account
							iLogger.info("No Contact exist for Given Mobile Number: "+ mobileNumber + " !!!");
							return assetDashboardList ;
						}
					}
				}
				//DefectId:20150706 End
	
				//get Client Details
				Properties prop1 = new Properties();
				String clientName=null;
	
				prop1.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				clientName= prop1.getProperty("ClientName");
	
				IndustryBO industryBoObj = new IndustryBO();
				ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
				//END of get Client Details	  
	
				if(! (session.isOpen() ))
	            {
	                 session = HibernateUtil.getSessionFactory().getCurrentSession();
	                 session.getTransaction().begin();
	            }
	
				Timestamp transactionTime=null;
	
				String basicSelectQuery1=null;
				String basicFromQuery1=null;
				String basicWhereQuery1=null;
				String basicGroupByQuery1=null;
				String basicOrderByQuery1=null;
	
				String basicSelectQuery2=null;
				String basicFromQuery2=null;
				String basicWhereQuery2=null;
				String basicGroupByQuery2=null;
	
	
				//to get only the 50 records, according to the page number calculate X for Limit X,Y - 
				//Y is always 50: number of records to be retrieved.
				//X: start point of the record to be retrieved. (first record is 0)
				int pge;
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
	
	
				//------------------- get the required parameters from properties file
				String longitude=null;
				String latitude = null;
				String fuelLevel= null;
				String engineHours = null;
				String engineON = null;
				String serviceAlerts = null;
				String ExternalBatteryVoltage = null;
				String HighEngineCoolantTemp = null;
				String LowEngineOilPressure=null;
				String ExternalBatteryVoltageStatus=null;
	
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				longitude= prop.getProperty("Longitude");
				latitude= prop.getProperty("Latitude");
				fuelLevel= prop.getProperty("FuelLevel");
				engineHours= prop.getProperty("TotalEngineHours");
				engineON= prop.getProperty("EngineON");
				serviceAlerts = prop.getProperty("ServiceAlerts");
				ExternalBatteryVoltage = prop.getProperty("ExternalBatteryVoltage");
				HighEngineCoolantTemp = prop.getProperty("HighEngineCoolantTemp");
				LowEngineOilPressure = prop.getProperty("LowEngineOilPressure");
				ExternalBatteryVoltageStatus = prop.getProperty("ExternalBatteryVoltageStatus");
	
				//get the corresponding parameters id
				List<Integer> paramIdList = new LinkedList<Integer>();
	
	
	
	
				Query query1 = session.createQuery("select max(parameterId),parameterName from MonitoringParameters where parameterName in ('"+longitude+"',"+ 	
												"'"+latitude+"', '"+fuelLevel+"', '"+engineHours+"', '"+engineON+"', '"+ExternalBatteryVoltage+"', '"+HighEngineCoolantTemp+"', '"+LowEngineOilPressure+"', '"+ExternalBatteryVoltageStatus+"'  ) group by parameterName");
				Iterator itr1 = query1.list().iterator();
				Object[] resultObj = null;
	
				while(itr1.hasNext())
				{
					resultObj = (Object[]) itr1.next(); 
					paramIdList.add((Integer)resultObj[0]);
				}
	
				Query query2 = session.createQuery(" from EventTypeEntity where eventTypeName='"+serviceAlerts+"'");
				Iterator itr2 = query2.list().iterator();
				int serviceEventTypeId =0;
				while(itr2.hasNext())
				{
					EventTypeEntity eventType = (EventTypeEntity) itr2.next();
					serviceEventTypeId = eventType.getEventTypeId();
				}
	
	
				String userTenancyListAsString ;
				String parameterIdList = conversion.getIntegerListString(paramIdList).toString(); //Ex:1,2,5,7,9
	
				//DefectId:20150706 @Suprava Adding Mobile Number as New Search criteria in Fleet General Tab.
				String accountIdListAsString = conversion.getIntegerListString(accountIdList).toString();
				//System.out.println("accountIdListAsString:"+accountIdListAsString);
	
				if(! (session.isOpen() ))
	            {
	                 session = HibernateUtil.getSessionFactory().getCurrentSession();
	                 session.getTransaction().begin();
	            }
	
				//DF20141217 - Rajani Nagaraju - Changing dashboard query to return the data only based on transaction tables
				//Get the accountId list from the tenancyId list
	
				//Get the list of machines owned by the selected Org Group
				if( ! (childTenancyList==null || childTenancyList.isEmpty())) 
				{
					//Get the list of accounts corresponding to the child tenancy
					userTenancyListAsString = conversion.getIntegerListString(childTenancyList).toString(); 
	
				}
				else
				{
					userTenancyListAsString = conversion.getIntegerListString(userTenancyList).toString(); 
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
	
				String mainSelectQuery="";
				String mainFromQuery="";
				String mainWhereQuery="";
				String mainOrderByQuery="";
	
	
				mainSelectQuery = " select ams.transactionNumber ";
				mainFromQuery = " from AssetMonitoringSnapshotEntity ams join ams.serialNumber asset , AssetOwnerSnapshotEntity aos " ;
				//DefectId:20150706 @Suprava Adding Mobile Number as New Search criteria in Fleet General Tab.
				if(accountIdListAsString!=null && primaryMobileNumber!=null){
				mainWhereQuery = " where ams.serialNumber=aos.serialNumber  and aos.accountId in ("+accountIdListAsString+")" +
								 " and ams.transactionTime >= aos.assetOwnershipDate " +
								 " and asset.active_status= true ";
	
				DF 20151006 : Commented the main query to for performance analysis - Removed the client Id check - Deepthi
		 * mainWhereQuery = " where ams.serialNumber=aos.serialNumber  and aos.accountId in ("+accountIdListAsString+")" +
				 " and ams.transactionTime >= aos.assetOwnershipDate and asset.client_id="+clientEntity.getClient_id() +
				 " and asset.active_status= true ";
				}
				else{
				mainWhereQuery = " where ams.serialNumber=aos.serialNumber  and aos.accountId in ("+userAccListAsString+")" +
				 " and ams.transactionTime >= aos.assetOwnershipDate " +
				 " and asset.active_status= true ";
	
	
				DF 20151006 : Commented the main query to for performance analysis - Removed the client Id check - Deepthi
		 * mainWhereQuery = " where ams.serialNumber=aos.serialNumber  and aos.accountId in ("+userAccListAsString+")" +
				 " and ams.transactionTime >= aos.assetOwnershipDate and asset.client_id="+clientEntity.getClient_id() +
				 " and asset.active_status= true ";
				}
				// DefectId:20150706 End
				mainOrderByQuery = " order by ams.transactionTime desc ";
				//If any additional filters are applied
				//Get only the list of machines currently owned by logged in user
	
	
				if(isOwnStock)
				{
					mainWhereQuery = mainWhereQuery + " and asset.primary_owner_id=aos.accountId ";
				}
				if(! (customGroupIdList==null || customGroupIdList.isEmpty()) )
				{
					String customAssetGroupStringList = conversion.getIntegerListString(customGroupIdList).toString();
					mainFromQuery = mainFromQuery + " , CustomAssetGroupEntity d, AssetCustomGroupMapping e ";
					mainWhereQuery = mainWhereQuery + " and d.group_id = e.group_id and d.group_id in ("+customAssetGroupStringList+") and " +
										" e.serial_number = ams.serialNumber and d.client_id="+clientEntity.getClient_id()+" and d.active_status=1 ";
				}
	
				else if (! (customGroupTypeIdList==null || customGroupTypeIdList.isEmpty()) )
				{
					String customAssetGroupTypeStringList = conversion.getIntegerListString(customGroupTypeIdList).toString();
					mainFromQuery = mainFromQuery + " , CustomAssetGroupEntity d, AssetCustomGroupMapping e ";
					mainWhereQuery = mainWhereQuery + " and d.group_id = e.group_id and d.asset_group_type in ("+customAssetGroupTypeStringList+") and " +
										" e.serial_number = ams.serialNumber and d.client_id="+clientEntity.getClient_id()+" and d.active_status=1  ";
				}
	
				if(! (assetGroupIdList==null || assetGroupIdList.isEmpty()) )
				{
					String assetGroupStringList = conversion.getIntegerListString(assetGroupIdList).toString();
					mainWhereQuery = mainWhereQuery + " and aos.assetGroupId in ( "+ assetGroupStringList +" ) "; 
				}
	
				if ( ! (modelList==null || modelList.isEmpty()) ) 
				{
					String modelIdStringList = conversion.getIntegerListString(modelList).toString();
					mainWhereQuery = mainWhereQuery +" and aos.assetTypeId in ( "+ modelIdStringList +" ) ";
	
				}
	
				if(! (serialNumber==null || serialNumber.isEmpty() ) )
				{
					mainWhereQuery = mainWhereQuery + " and ams.serialNumber= '"+ serialNumber+"' ";
				}
	
				if(! (landmarkId==null || landmarkId.isEmpty() ) )
				{
					String landmarkIdStringList = conversion.getIntegerListString(landmarkId).toString();
					mainFromQuery = mainFromQuery + " , LandmarkEntity g, LandmarkAssetEntity h ";
					mainWhereQuery = mainWhereQuery + " and g.Landmark_id = h.Landmark_id and g.ActiveStatus=1 and g.Landmark_id in ("+landmarkIdStringList+") and "+
														" h.Serial_number = ams.serialNumber " ;
				}
	
				else if(! (landmarkCategoryId==null || landmarkCategoryId.isEmpty() ) )
				{
					String landmarkCategoryIdStringList = conversion.getIntegerListString(landmarkCategoryId).toString();
					mainFromQuery = mainFromQuery + " , LandmarkEntity g, LandmarkAssetEntity h ";
					mainWhereQuery = mainWhereQuery + " and g.Landmark_id = h.Landmark_id and g.ActiveStatus=1 and g.Landmark_Category_ID in ("+landmarkCategoryIdStringList+") and "+
												" h.Serial_number = ams.serialNumber " ;
				}
	
				if( (! (eventTypeIdList==null || eventTypeIdList.isEmpty()) ) || 
						(! (eventSeverityList==null || eventSeverityList.isEmpty()) )	)
				{
					mainFromQuery = mainFromQuery+" , AssetEventEntity aee ";
					mainWhereQuery = mainWhereQuery + " and aee.assetEventId = ( select max(aevt.assetEventId) from " +
							" AssetEventEntity aevt where aevt.serialNumber=ams.serialNumber and aevt.activeStatus=1 ";
				}
	
	
	
				if(! (eventTypeIdList==null || eventTypeIdList.isEmpty()) )
				{
					String eventTypeIdStringList = conversion.getIntegerListString(eventTypeIdList).toString();
					//basicWhereQuery2 = basicWhereQuery2 + " and j.eventTypeId in ("+eventTypeIdStringList+") ";
					mainWhereQuery = mainWhereQuery+ " and aevt.eventTypeId in ("+eventTypeIdStringList+")";
				}
	
				if(! (eventSeverityList==null || eventSeverityList.isEmpty()) )
				{
					String eventSeverityListAsString = conversion.getStringList(eventSeverityList).toString();
					//basicWhereQuery2 = basicWhereQuery2 + " and j.eventSeverity in ("+eventSeverityListAsString+") ";
					mainWhereQuery = mainWhereQuery + " and aevt.eventSeverity in ("+eventSeverityListAsString+")";
	
				}
				if( (! (eventTypeIdList==null || eventTypeIdList.isEmpty()) ) || 
						(! (eventSeverityList==null || eventSeverityList.isEmpty())))
				{
					mainWhereQuery=mainWhereQuery+")";
				}
	
	
				String mainQuery = mainSelectQuery+mainFromQuery+mainWhereQuery+mainOrderByQuery;
				Query finalTxnQuery = session.createQuery(mainQuery);
	
	
				finalTxnQuery.setFirstResult(startLimit);
				finalTxnQuery.setMaxResults(51);
	
				Iterator finalTxnItr = finalTxnQuery.list().iterator();
	
	
	
				List<Integer> finalTxnList = new LinkedList<Integer>();
				while(finalTxnItr.hasNext())
				{
					AssetMonitoringHeaderEntity amh = (AssetMonitoringHeaderEntity) finalTxnItr.next();
					finalTxnList.add(amh.getTransactionNumber());
				}
				//System.out.println("finalTxnList"+ finalTxnList.size());
				String finnalTxnListAsString = conversion.getIntegerListString(finalTxnList).toString();
	
				if(finalTxnList==null || finalTxnList.size()==0)
				{
					iLogger.info(" No machines exists for the selected Criteria  !!!");
					return assetDashboardList ;
				}
	
	
	
	
				//--------------------------------------- Get the remote monitoring data for the given Txns ----------------------------------------
				basicSelectQuery1 = " select amh.serialNumber, " +
									" CAST(GROUP_CONCAT(l.parameterName) As string ) as parameterNames, "+
									" CAST(GROUP_CONCAT(amd.parameterValue) As string ) as parameterValues, "+
									" n.notes as notes, amh.transactionTime as transactionTimeStamp," +
									" ams.fuelLevel as actualFuelLevel, amh.transactionNumber, amh.createdTimestamp ";
	
				basicFromQuery1 = " from AssetMonitoringSnapshotEntity ams, AssetMonitoringDetailEntity amd, " +
						" MonitoringParameters l, AssetExtendedDetailsEntity n RIGHT OUTER JOIN n.serial_number d,AssetMonitoringHeaderEntity amh ";
	
	
				basicWhereQuery1 = " where amh.transactionNumber = amd.transactionNumber and " +
					//	" ams.transactionNumber in ("+finnalTxnListAsString+") " +
					    " amh.transactionNumber in ("+finnalTxnListAsString+") " +
					    " and ams.serialNumber= amh.serialNumber " +
						//" and ams.serialNumber= n.serial_number " +
						" and ams.serialNumber= d.serial_number " +
						" and amd.parameterId = l.parameterId " +
						" and l.parameterId in ("+parameterIdList+")";
	
				basicGroupByQuery1 = " group by amh.serialNumber ";
				basicOrderByQuery1 = " order by amh.transactionTime desc ";
	
				//--------------------------------------------- Get the Event status for each of these VINs --------------------------------------
				basicSelectQuery2 = " select CAST(GROUP_CONCAT(j.eventSeverity ) As string ) as severity, " +
									" CAST(GROUP_CONCAT(p.eventTypeId ) As string ) as eventTypeId,"+
									" k.serial_number as serial_number ";
	
				basicFromQuery2	= " from AssetEventEntity j JOIN j.eventTypeId p RIGHT OUTER JOIN j.serialNumber k  ";
	
				basicWhereQuery2 = " where j.activeStatus=1 ";
	
				basicGroupByQuery2 = " group by k.serial_number ";
	
	
				//DefectID:1965 - 20140311 - Rajani Nagaraju - To return correct count of resultset when AlertSeverity/Alert Type is selected as filter 
				if(! (eventTypeIdList==null || eventTypeIdList.isEmpty()) )
				{
					String eventTypeIdStringList = conversion.getIntegerListString(eventTypeIdList).toString();
					basicWhereQuery2 = basicWhereQuery2 + " and j.eventTypeId in ("+eventTypeIdStringList+") ";
				}
	
				//DefectID:1965 - 20140311 - Rajani Nagaraju - To return correct count of resultset when AlertSeverity/Alert Type is selected as filter 
				if(! (eventSeverityList==null || eventSeverityList.isEmpty()) )
				{
					String eventSeverityListAsString = conversion.getStringList(eventSeverityList).toString();
					basicWhereQuery2 = basicWhereQuery2 + " and j.eventSeverity in ("+eventSeverityListAsString+") ";
				}
				//-----------------------------------------------------------------------------------------------
	
				String finalQuery = basicSelectQuery1+basicFromQuery1+basicWhereQuery1+basicGroupByQuery1+basicOrderByQuery1;
	
	
				Query query3 = session.createQuery(finalQuery);
	
				//System.out.println("Itr3Count"+query3.list().size());
				Iterator itr3 = query3.list().iterator();
	
	
				Object[] result = null;
	
				//DefectID: DF20131212 - Rajani Nagaraju - To return the last communicated timestamp of the machine.
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
				while(itr3.hasNext())
				{
					List<String> eventSeverityValues = new LinkedList<String>();
					List<String> eventTypeIdValues = new LinkedList<String>();
	
					String serviceAlert=null;
	
					result = (Object[])itr3.next();
	
					AssetDashboardImpl assetDashboard = new AssetDashboardImpl();
	
					AssetEntity assetEnt = (AssetEntity)result[0];
					String serialNum = assetEnt.getSerial_number().getSerialNumber();
					assetDashboard.setSerialNumber(serialNum);
	
					//DefectId:20141208 - Rajani Nagaraju - Return the last Pkt Received time (Either through GPRS / SMS ) - Start 
					Timestamp smsEventReceivedTime = null;
					String smsChmr =null;
					Query smsEventQ = session.createQuery(" from SmsEventDetailsEntity where serialNumber='"+serialNum+"' order by eventGeneratedTime desc ");
					Iterator smsEventItr = smsEventQ.list().iterator();
					if(smsEventItr.hasNext())
					{
						SmsEventDetailsEntity smsEvent = (SmsEventDetailsEntity)smsEventItr.next();
						smsEventReceivedTime = smsEvent.getEventGeneratedTime();
						smsChmr = smsEvent.getCmhr();
					}
	
					if(assetEnt.getProductId()!=null)
					{
						if(assetEnt.getProductId().getEngineTypeId()!=null)
								assetDashboard.setEngineTypeName(assetEnt.getProductId().getEngineTypeId().getEngineTypeName());
	
						if(assetEnt.getProductId().getAssetGroupId()!=null)
								assetDashboard.setProfileName(assetEnt.getProductId().getAssetGroupId().getAsset_group_name());
	
						if(assetEnt.getProductId().getAssetTypeId()!=null)
						{
							assetDashboard.setModelName(assetEnt.getProductId().getAssetTypeId().getAsset_type_name());
							assetDashboard.setAssetImage(assetEnt.getProductId().getAssetTypeId().getAssetImage());
						}
					}
	
					assetDashboard.setNickName(assetEnt.getNick_name());
	
					if(result[1]!=null)
						assetMonitoringParameters = Arrays.asList(result[1].toString().split(","));
					else
						assetMonitoringParameters = new LinkedList<String>();
	
					if(result[2] !=null)
						assetMonitoringValues = Arrays.asList(result[2].toString().split(","));
					else
						assetMonitoringValues = new LinkedList<String>();
	
	
					if(result[3]!=null)
						assetDashboard.setNotes(result[3].toString());
	
					if(result[4]!=null)
					{
						transactionTime = (Timestamp) result[4];
						//DefectID: DF20131212 - Rajani Nagaraju - To return the last communicated timestamp of the machine.
						String transactionTimeInString = dateFormat.format(transactionTime);
						assetDashboard.setLastReportedTime(transactionTimeInString);
	
					}
	
					if(result[6]!=null)
					{
						AssetMonitoringHeaderEntity amh = (AssetMonitoringHeaderEntity)result[6];
						Timestamp createdTime = amh.getCreatedTimestamp();
						Timestamp createdTime = null;
						if(result[7]!=null)
						{
							createdTime = (Timestamp)result[7];
						}
	
						//DefectId:20141208 - Rajani Nagaraju - Return the last Pkt Received time (Either through GPRS / SMS ) - Start 
	
						assetDashboard.setLastPktReceivedTime(dateFormat.format(createdTime));
						if(smsEventReceivedTime!=null)
						{
							if(smsEventReceivedTime.getTime()>createdTime.getTime())
							{
								assetDashboard.setLastPktReceivedTime(dateFormat.format(smsEventReceivedTime));
							}
							else
							{
								assetDashboard.setLastPktReceivedTime(dateFormat.format(createdTime));
							}
	
						}
	
						else
						{
							assetDashboard.setLastPktReceivedTime(dateFormat.format(createdTime));
						}
						//DefectId:20141208 - Rajani Nagaraju - Return the last Pkt Received time (Either through GPRS / SMS ) - End
	
					}
	
					//DF20140409 - Rajani Nagaraju - Get the Latest transaction from Snapshot table
					if(result[5]!=null)
					{
						assetDashboard.setFuelLevel(result[5].toString());
					}
	
					String basicQuery2 = basicSelectQuery2+basicFromQuery2+basicWhereQuery2+" and k.serial_number = '"+serialNum+"' "+basicGroupByQuery2;
					//DF20150929 : Perfomance Analysis for FleetDashboard
	
					Query query5 = session.createQuery(basicQuery2);
					Iterator itr5 = query5.list().iterator();
	
	
					//DF20150929 : Perfomance Analysis for FleetDashboard
					Object[] result1 = null;
	
					int eventPresent=0;
	
					while(itr5.hasNext())
					{
						eventPresent=1;
	
						result1 = (Object[])itr5.next();
	
						if(result1[0] !=null )
							eventSeverityValues = Arrays.asList(result1[0].toString().split(","));
						else
							eventSeverityValues=new LinkedList<String>();
	
						if(result1[1] !=null)
							eventTypeIdValues = Arrays.asList(result1[1].toString().split(","));
						else
							eventTypeIdValues=new LinkedList<String>();
					}
	
					if( (eventPresent==0) && ( (!(eventTypeIdList==null || eventTypeIdList.isEmpty())) || (!(eventSeverityList==null || eventSeverityList.isEmpty()))) )
					{
						continue;
					}
	
					HashMap<String,String> monitoringParametersMap = new HashMap<String,String>();
	
					for(int i=0; i<assetMonitoringParameters.size();i++)
					{
						monitoringParametersMap.put(assetMonitoringParameters.get(i), assetMonitoringValues.get(i));
					}
	
					//DefectId:20141208 - Rajani Nagaraju - Return the last Pkt Received time (Either through GPRS / SMS ) - Start 
					String modifiedEngineHours = monitoringParametersMap.get(engineHours);
					if(smsChmr!=null)
					{
						double engineHoursInDouble = Double.valueOf(modifiedEngineHours);
						double smsChmrInDouble = Double.valueOf(smsChmr);
						if(smsChmrInDouble>engineHoursInDouble)
							modifiedEngineHours = smsChmr;
					}
					assetDashboard.setLifeHours(modifiedEngineHours);
					//DefectId:20141208 - Rajani Nagaraju - Return the last Pkt Received time (Either through GPRS / SMS ) - END
	
					//DF20140409 - Rajani Nagaraju - Get the Latest transaction from Snapshot table - Commenting the below Code
					//assetDashboard.setFuelLevel(monitoringParametersMap.get(fuelLevel));
	
					assetDashboard.setLatitude(monitoringParametersMap.get(latitude));
					assetDashboard.setLongitude(monitoringParametersMap.get(longitude));
					assetDashboard.setExternalBatteryInVolts(monitoringParametersMap.get(ExternalBatteryVoltage));
					assetDashboard.setHighCoolantTemperature(monitoringParametersMap.get(HighEngineCoolantTemp));
					assetDashboard.setLowEngineOilPressure(monitoringParametersMap.get(LowEngineOilPressure));
					//DefectId:201402076 Engine_Status newParameter added 2014-02-06 @Suprava 
					assetDashboard.setEngineStatus(monitoringParametersMap.get(engineON));
					if(monitoringParametersMap.containsKey(ExternalBatteryVoltageStatus))
						assetDashboard.setExternalBatteryStatus(monitoringParametersMap.get(ExternalBatteryVoltageStatus));
	
					//find whether service notification is sent or not
					String currentServiceAlert=null;
					for(int j=0; j<eventTypeIdValues.size();j++)
					{
						if(Integer.parseInt(eventTypeIdValues.get(j))==(serviceEventTypeId))
						{
	
								currentServiceAlert=eventSeverityValues.get(j);
								if(serviceAlert==null)
								{
									serviceAlert=currentServiceAlert;
								}
								else if(currentServiceAlert.compareToIgnoreCase("Red")==0)
								{
									serviceAlert=currentServiceAlert;
								}
	
						}
					}
	
					if(serviceAlert==null)
					{
						assetDashboard.setDueForService("Green");
					}
	
					else
					{
						assetDashboard.setDueForService(serviceAlert);
					}
	
					//find whether any notifications has been sent or not for a given serial number
					if( !(eventSeverityValues==null || eventSeverityValues.isEmpty()) )
					{
						if( (eventSeverityValues.contains("Red")) || (eventSeverityValues.contains("RED")) || (eventSeverityValues.contains("red")) )
						{
							assetDashboard.setMachineStatus("Red");
						}
						else if ( (eventSeverityValues.contains("Yellow")) || (eventSeverityValues.contains("YELLOW")) || (eventSeverityValues.contains("yellow")) )
						{
							assetDashboard.setMachineStatus("Yellow");
						}
					}
					else
					{
						//DefectID: 1394 - Rajani Nagaraju - 20131011 - HH corresponds to 24 hour format and earlier it was hh which is 12hr am/pm format
						Timestamp maxTransactionTime = null;
						if(result[4]!=null)
							maxTransactionTime = transactionTime;
	
						//get yesturday date time (past 24 hour)
						Date yesturday = null;
						Date maxTransDate = null;
	
						cal = Calendar.getInstance();
						cal.add(Calendar.DATE, -1);
	
						//DefectId: 1440 - 20131010 - Rajani Nagaraju - GMT conversion for Machine Idle state calculation 
						SimpleDateFormat dateFrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						dateFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
						String str = dateFrmt.format(cal.getTime());
						yesturday = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
	
						String maxDateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(maxTransactionTime);
						maxTransDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(maxDateString);
	
						if(maxTransDate.after(yesturday))
						{
							if(monitoringParametersMap!=null)
								assetDashboard.setMachineStatus(monitoringParametersMap.get(engineON));
						}
	
						else
						{
							assetDashboard.setMachineStatus("Idle");
						}
	
					}
	
					//get the connectivity status of the machine
					// If the Engine is ON and if the log packets are not received for more than 20min, connectivity is said to be lost
					//DefectId: 1434 - Rajani Nagaraju - 20131010 - Connectivity status is redefined as : If the machine has communicated in last 20 mins
					//										   (Log/Event/Hello packet), then the connectivity is ON. In all other conditions it is OFF			
					Timestamp maxTransactionTime = null;
					if(result[4]!=null)
						maxTransactionTime = transactionTime;
	
					//DefectID: 1394 - Rajani Nagaraju - 20131011 - HH corresponds to 24 hour format and earlier it was hh which is 12hr am/pm format
					//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
					String currentTime = sdf.format(new Date());
					String maxTxnTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(maxTransactionTime);
	
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
					Date currentDate = sf.parse(currentTime);
					Date maxTxnDate = sf.parse(maxTxnTime);
	
					long t1= currentDate.getTime()/60000 ;
					long t2 = maxTxnDate.getTime()/60000;
	
					//If the difference is greater than 20 minutes
					if ( (t1-t2) > 20)
					{
						assetDashboard.setConnectivityStatus("0");
					}
					else
					{
						assetDashboard.setConnectivityStatus("1");
					}
	
	
	
	
					assetDashboardList.add(assetDashboard);
	
				}
				//20151008: Performance improvement - Replaced the mainselect query 	-- Deepthi		
	
				mainSelectQuery ="select count(*) ";
				mainQuery = mainSelectQuery+mainFromQuery+mainWhereQuery;
	
	
	
				//DefectId:20140924 @Suprava Total Number of mahcines in fleet 
	
	
				AssetDashboardImpl assetDashboard = new AssetDashboardImpl();
				assetDashboard = new AssetDashboardImpl();
				Query queryCount = session.createQuery(mainQuery);
				Iterator implitr = queryCount.list().iterator();
				long count =0;
				while(implitr.hasNext()){
					count = (Long)implitr.next();
				}
	
	
	
				int count = queryCount.list().size(); 
				String countInString=null;
				countInString=String.valueOf(count);
				//System.out.println("countInString"+countInString);
				assetDashboard.setSerialNumber(countInString);
				assetDashboard.setNotes("Sumarry Count");
				assetDashboardList.add(assetDashboard);
	
	
			}
	
	
			catch(ParseException e)
			{
				fLogger.fatal("Exception :"+e);
			}
	
			catch(IOException e)
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
			//System.out.println("assetDashboardList"+assetDashboardList.size());
			return assetDashboardList;
	
		}
		 */
	
		//*************************************************** End of get Asset Dashboard details***********************************************
	
	
	
	
	
	
		//METHOD 5:
		//************************************************ Get AssetprofileDetails ****************************************************
		public AssetDetailsBO getAssetProfile(String serialNumber)
		{
	
			//DefectId:1200 - Rajani Nagaraju - 20130917 - Session not getting closed
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			//Logger iLogger = InfoLoggerClass.logger;
			try{
				//validate serialNumber
				AssetEntity assetEntity = getAssetEntity(serialNumber);
				if(assetEntity.getSerial_number()==null || assetEntity.isActive_status()==false)
				{
					bLogger.error("Invalid Serial Number");
					new CustomFault("Invalid Serial Number");
				}
				//added by smitha on june 26th 2013...Defect ID 136
	
				String query = " from AssetExtendedDetailsEntity where serial_number ='"+ serialNumber+"'";
				Iterator itr=session.createQuery(query).list().iterator();  
				while(itr.hasNext())
				{
					AssetExtendedDetailsEntity assetExtDetEntity = (AssetExtendedDetailsEntity)itr.next();
					driverContactNumber = assetExtDetEntity.getDriverContactNumber();
					driverName = assetExtDetEntity.getDriverName();
				}
				//end [june 26th 2013]
	
				//get Asset Details
				subscription = assetEntity.getPremFlag(); //CR353.n 
				this.serialNumber=serialNumber;
				nick_name=assetEntity.getNick_name();
				description=assetEntity.getDescription();
				purchase_date=assetEntity.getPurchase_date();
				install_date=assetEntity.getInstall_date();
				saleDate=assetEntity.getSale_Date(); // CR256-Sale Date in Fleet General- Deepthi
				//ramu b added on 20200512 getExtendedWarrantyType
				if(assetEntity.getExtendedWarrantyType()!=null &&assetEntity.getExtendedWarrantyType()!="")
				{
					this.extendedWarrantytype=assetEntity.getExtendedWarrantyType();
					//iLogger.info("extendedWarrantytype..."+assetEntity.getExtendedWarrantyType());
				}else
				{
					this.extendedWarrantytype="NA";
					//iLogger.info("extendedWarrantytype..."+"NA");
				}	
	
				if(assetEntity.getProductId()!=null)
				{
					if(assetEntity.getProductId().getAssetClassId()!=null)
						assetClassName=assetEntity.getProductId().getAssetClassId().getAssetClassName();
					if(assetEntity.getProductId().getAssetGroupId()!=null)
						assetGroupName=assetEntity.getProductId().getAssetGroupId().getAsset_group_name();
					if(assetEntity.getProductId().getAssetTypeId()!=null)
						assetTypeName= assetEntity.getProductId().getAssetTypeId().getAsset_type_name();
					if(assetEntity.getProductId().getAssetTypeId()!=null)
						assetTypeCode= assetEntity.getProductId().getAssetTypeId().getAssetTypeCode();		//CR353.n
					productName= assetEntity.getProductId().getProductName();
					make = assetEntity.getProductId().getMake();
				}
	
				if(install_date!=null){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar c1 = Calendar.getInstance();
					/*c1.setTime(install_date);
			    //2016-02-16: changed the Renewal date to Install Date+24 Months instead of 26 Months as per the confirmation mail from Gunjan		    
			    //c1.add(Calendar.MONTH,24);   // or  Calendar.DAY_OF_MONTH which is a synonym
			    //renewalDate = sdf.format(c1.getTime());
	
			    // START Aj286551 2017-01-30, for Backhoe renewal date is installation date+48 months where installation date<15th
				// Nov,2016 otherwise installation date+24 months
		    	//Start
				Date compDate = sdf.parse("2016-11-15");
				if (assetGroupName!=null && assetGroupName.equalsIgnoreCase("Backhoe")) {
						if (install_date.compareTo(compDate) < 0) {
							c1.add(Calendar.MONTH, 24);
						}
						else {
							c1.add(Calendar.MONTH, 48);
						}
				}
				else {
					c1.add(Calendar.MONTH, 24); // or Calendar.DAY_OF_MONTH which is a synonym
				}
	
				renewalDate = sdf.format(c1.getTime());*/
					// End Aj286551, 2017-01-30
					//DF20171207: KO369761 - Now renewal date is fetching directly from asset table.
					c1.setTime(assetEntity.getRenewal_date());
					renewalDate = sdf.format(c1.getTime());
				}  //CR256: Deepthi: Added Sale Date - Fleet General 
				if(saleDate!=null){
					SimpleDateFormat saleDate = new SimpleDateFormat("yyyy-MM-dd");
				    Calendar cal = Calendar.getInstance();
				    cal.setTime(assetEntity.getSale_Date());
					String sDate = saleDate.format(cal.getTime());
			}   
				
				String AssetControlQuery = " from AssetControlUnitEntity where serial_number ='"+ serialNumber+"'";
				Iterator AssetControlitr=session.createQuery(AssetControlQuery).list().iterator();  
				while(AssetControlitr.hasNext())
				{
					AssetControlUnitEntity assetControlUnitEntity = (AssetControlUnitEntity)AssetControlitr.next();
					imeiNumber = assetControlUnitEntity.getImeiNo();
					simNumber = assetControlUnitEntity.getSimNo();
					iccidNumber = assetControlUnitEntity.getIccidNo();
				}
			}
	
			//DefectId:1200 - Rajani Nagaraju - 20130917 - Session not getting closed
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
			//iLogger.info("this object"+this);
			return this;
		}
		//************************************************ End of Get AssetprofileDetails ****************************************************
	
	
		@SuppressWarnings("unused")
		public AssetDetailsBO getFuel(String serialNumber) {
		    Session session = HibernateUtil.getSessionFactory().openSession();
		    session.beginTransaction();
		    
		    Logger fLogger = FatalLoggerClass.logger;
		    Logger bLogger = BusinessErrorLoggerClass.logger;
		    Logger iLogger = InfoLoggerClass.logger;
		    ConnectMySQL connectionObj = new ConnectMySQL();
		    AssetDetailsBO assetDetails = new AssetDetailsBO();
		    String query ="select JSON_UNQUOTE(json_extract(TxnData,'$.CURRENT_FUEL_AVAILABLE_IN_TANK')) as fuel_Level from asset_monitoring_snapshot where Serial_Number='"+ serialNumber+"'";
		    iLogger.info(query);
		    try (Connection con = connectionObj.getConnection();
				    Statement st = con.createStatement();
				    ResultSet rs = st.executeQuery(query)) {
		    	while(rs.next()) {
		            AssetEntity assetEntity = getAssetEntity(serialNumber);
		            if (assetEntity.getSerial_number() == null || !assetEntity.isActive_status()) {
		                bLogger.error("Invalid Serial Number");
		                throw new CustomFault("Invalid Serial Number");
		            }
		                String fuelLevel = rs.getString("fuel_Level");
		                iLogger.info("FuelLevel :"+fuelLevel);
		                if(fuelLevel!=null || fuelLevel!="")
		                {
		                	assetDetails.setFuelLevel(fuelLevel);
		                }
		                else
		                {
		                	assetDetails.setFuelLevel(" ");
		                }
		                
		    	}
		    } catch (Exception e) {
		        fLogger.fatal("Exception: " + e.getMessage(), e);
		        throw new RuntimeException("Error retrieving fuel level", e);
		    } finally {
		        session.getTransaction().commit();
		        session.close();
		    }
		    return assetDetails;
		}
	
		//Method 6: 
		/** This method sets the asset personality details
		 * DefectID: 735, Rajani Nagaraju, 20130704 
		 * @param engineNumber engine Number of the Machine
		 * @param assetGroupCode Machine Profile Code
		 * @param assetTypeCode Model Code
		 * @param engineTypeCode Code of Engine Type
		 * @param assetBuiltDate Machine Built Date
		 * @param make Machine Make
		 * @param fuelCapacity Fuel Capacity of fuel Tank
		 * @param serialNumber VIN
		 * @return Returns the status String
		 */
		public String setAssetPersonalityDetails(String engineNumber, String assetGroupCode, String assetTypeCode, String engineTypeCode,
				String assetBuiltDate, String make, String fuelCapacity, String serialNumber, String messageId)
		{
			String status = "SUCCESS-Record Processed";
	
			//Logger businessError = Logger.getLogger("businessErrorLogger");
			//Logger fatalError = Logger.getLogger("fatalErrorLogger");
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			Logger iLogger = InfoLoggerClass.logger;
			String groupName = null, typeName = null;
			int groupId = 0, typeId = 0;
			
			Jedis jedis = null;
			 iLogger.info("WISE:WEATHERDATA:UPDATE:START:FOR:VIN:ENTERING THE setAssetPersonalityDetails"+serialNumber +" AssetTypeCode:"+assetTypeCode);
			//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
			session.beginTransaction();
	
			try
			{
				//get Client Details
				Properties prop = new Properties();
				String clientName=null;
				String databaseIP=null; //CR337.n
				String databaseRedisPort=null;//CR337.n
	
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				clientName= prop.getProperty("ClientName");
				databaseIP =  prop.getProperty("DataBaseIP");//CR337.n
				databaseRedisPort = prop.getProperty("DataBaseRedisPort");
		        //String bsivAssetTypeCodesString = prop.getProperty("bsiv_Asset_Type_code");//CR337.n //CR308.o
				 Properties reader = new Properties(); //CR308.sn
				 String path = "/data3/JCBLiveLink/WeatherDataBSIVModelConfiguration/WeatherDataBSIVModelConfiguration.properties";
		            String bsivAssetTypeCodesString = null;
		            try {
						reader.load(new FileInputStream(path));
						bsivAssetTypeCodesString = reader.getProperty("bsiv_Asset_Type_code");    
					} catch (IOException e1) {
						e1.printStackTrace();
					}//CR308.en
				
				iLogger.info("AssetDetailBO :: RedisIP : " + databaseIP  + " :: RedidPort :" + databaseRedisPort +" :: bsivAssetTypeCodesString "+bsivAssetTypeCodesString );

				IndustryBO industryBoObj = new IndustryBO();
				ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
				//END of get Client Details
	
	
				//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
				if(clientEntity==null)
				{
					status = "FAILURE-Client Undefined";
					bLogger.error("EA Processing: AssetPersonality:"+messageId+": FAILURE - Client Undefined ");
					return status;
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
	
				SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date builtDate=null;

				//DF20210412 automating Weather Data update ****START*********
				iLogger.info("WISE:WEATHERDATA:UPDATE:START1:FOR:VIN:"+serialNumber +" AssetTypeCode:"+assetTypeCode);

				AssetEntity assetentityObj = null;
				if(bsivAssetTypeCodesString != null && !bsivAssetTypeCodesString.equals("")){
				List<String> bsivAlertCodesList = new ArrayList<String>(Arrays.asList(bsivAssetTypeCodesString.split(",")));
				if(bsivAlertCodesList!=null && bsivAlertCodesList.size()>0   && bsivAlertCodesList.contains(assetTypeCode)){
					iLogger.info("WISE:WEATHERDATA:UPDATE:START2:FOR:VIN:"+serialNumber +" AssetTypeCode:"+assetTypeCode);



					Query machNumQ = session.createQuery(" from AssetEntity where Serial_Number like '%"+serialNumber+"%'");
					Iterator machNumItr = machNumQ.list().iterator();
					while(machNumItr.hasNext())
					{
						assetentityObj = (AssetEntity) machNumItr.next();
						serialNumber=assetentityObj.getSerial_number().getSerialNumber();
					}


				     
				     
					try
					{
						// Dhiraj K : 20220630 : Change in DB ip for redis for AWS server
						//jedis = new Jedis("10.179.12.74", 29000);
						//jedis = new Jedis("10.210.196.240", 6379);//CR337.o
						jedis = new Jedis(databaseIP, Integer.parseInt(databaseRedisPort));//CR337.n
						//JCB6243.sn
						//delete serial number if available
						jedis.lrem("WEATHER_VIN_LIST", 0, serialNumber);
						//JCB6243.sn
						jedis.lpush("WEATHER_VIN_LIST", serialNumber);
						jedis.hset("WEATHER_DETAILS" ,serialNumber, "+029_+029_044_01_Talegaon Dabhad******_0530");

						iLogger.info("WISE:WEATHERDATA:UPDATE:SUCCESS:FOR:VIN:"+serialNumber);
						iLogger.info("WISE:WEATHERDATA:UPDATE:START:FOR:VIN:"+serialNumber);
					}
					catch(JedisConnectionException e1)
					{
						throw new Exception("Exception in getting Jedis Connection");
					}
					catch(Exception e)
					{
						throw new Exception(e.getMessage());
					}
					finally
					{
						if (jedis != null) 
						{
							jedis.disconnect();
						}
					}
					
				iLogger.info("Invoking WeatherDataProducer to write on kafka topic: "+serialNumber);
				new WeatherDataProducer(serialNumber);


				}
				}
				//DF20210412 automating Weather Data update ****END*********
				//convert String to date
				if(assetBuiltDate!=null && assetBuiltDate!="")
				{
					String chkAssetBuiltDate = assetBuiltDate.replaceAll("\\s","") ;
					if(chkAssetBuiltDate.length()>0)
					{
						try
						{
							builtDate = datetimeFormat.parse(assetBuiltDate);
						}
						catch(ParseException e)
						{
							status = "FAILURE-Unparsable Build Date";
							throw new CustomFault("Unparsable Build Date. Format should be yyyy-MM-dd");
						}
					}
	
				}
	
				AssetGroupEntity assetGroupObj=null;
				AssetGroupProfileEntity assetGroupProfile =null;
				AssetTypeEntity assetTypeObj=null;
				EngineTypeEntity engineTypeObj=null;
	
	
				//get Asset Group Details
				Query assetGroupQuery = session.createQuery("from AssetGroupProfileEntity where asset_grp_code like '"+assetGroupCode+"'");
				Iterator assetGroupItr = assetGroupQuery.list().iterator();
				while(assetGroupItr.hasNext())
				{
					assetGroupProfile = (AssetGroupProfileEntity)assetGroupItr.next();
					assetGroupObj = assetGroupProfile.getAsset_grp_id();
				}
				if(assetGroupObj==null)
				{
					status = "FAILURE-Invalid Asset Group Code";
					throw new CustomFault("Invalid Asset Group Code");
				}
				groupId= assetGroupObj.getAsset_group_id();
				groupName = assetGroupObj.getAsset_group_name();
	
	
				//get Asset Type Details
				Query assetTypeQuery = session.createQuery("from AssetTypeEntity where assetTypeCode like '"+assetTypeCode+"'");
				Iterator assetTypeItr = assetTypeQuery.list().iterator();
				while(assetTypeItr.hasNext())
				{
					assetTypeObj = (AssetTypeEntity)assetTypeItr.next();
				}
				if(assetTypeObj==null)
				{
					status = "FAILURE-Invalid Asset Type Code";
					throw new CustomFault("Invalid Asset Type Code");
				}
				typeId = assetTypeObj.getAsset_type_id();
				typeName = assetTypeObj.getAsset_type_name();
	
	
				//get the Engine type details
				Query engineTypeQuery = session.createQuery("from EngineTypeEntity where engineTypeCode like '"+engineTypeCode+"'");
				Iterator engineTypeItr = engineTypeQuery.list().iterator();
				while(engineTypeItr.hasNext())
				{
					engineTypeObj = (EngineTypeEntity)engineTypeItr.next();
				}
				if(engineTypeObj==null)
				{
					status = "FAILURE-Invalid Engine Type Code";
					throw new CustomFault("Invalid Engine Type Code");
				}
	
				int assetMake=0;
				try
				{
					if(! (make==null || make.trim().length()==0) )
						assetMake=Integer.parseInt(make);
				}
				catch(Exception e)
				{
					status="FAILURE-Number Format Exception for Make";
					fLogger.fatal("EA Processing: AssetPersonality: "+messageId+ " Number Format Exception for Make:"+e);
					return status;
				}
	
				//get the product details
				ProductEntity productEntity=null;
				productEntity = getProductEntity(0,assetGroupObj.getAsset_group_id(),assetTypeObj.getAsset_type_id(),
						assetMake,clientEntity.getClient_id(),engineTypeObj.getEngineTypeId());
	
				if(productEntity==null)
				{
					//create Product Entity
					String productName = assetGroupProfile.getAsset_grp_code()+" - "+assetTypeObj.getAssetTypeCode()+" - " +
							" "+engineTypeObj.getEngineTypeCode();
	
					productEntity = createProductEntity(productName, clientEntity , assetGroupObj,assetTypeObj,engineTypeObj, assetMake);
				}
	
				if(productEntity==null)
				{
					status = "FAILURE-Error in Creation of product";
					bLogger.error("EA Processing: AssetPersonality:"+messageId+": FAILURE - Error in Creation of product ");
					return status;
				}
	
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
	
				//set Product Profile - Fuel Capacity In Litres
				if(fuelCapacity!=null && fuelCapacity.trim()!=null)
				{
					String chkFuelCapacity = fuelCapacity.replaceAll("\\s","") ;
					if(chkFuelCapacity.length()>0)
					{
						Double fuelCapacityInDouble = Double.valueOf(chkFuelCapacity);
						Query productProfileQuery = session.createQuery("from ProductProfileEntity where productId="+productEntity.getProductId());
						Iterator productProfileItr = productProfileQuery.list().iterator();
						int update=0;
						while(productProfileItr.hasNext())
						{
							ProductProfileEntity productProfile = (ProductProfileEntity)productProfileItr.next();
							update=1;
							productProfile.setFuelCapacityInLitres(fuelCapacityInDouble);
							session.update(productProfile);
						}
	
						if(update==0)
						{
							ProductProfileEntity newProductProfile = new ProductProfileEntity();
							newProductProfile.setProductId(productEntity);
							newProductProfile.setFuelCapacityInLitres(fuelCapacityInDouble);
							session.save(newProductProfile);
						}
					}
				}
	
	
				//Set the Product Details to Asset Entity
				////DF20141209 - Rajani Nagaraju - Remove Logic based on Engine Number 
				// Query assetQuery = session.createQuery("from AssetEntity where nick_name like '"+engineNumber+"'");
				Query assetQuery = session.createQuery("from AssetEntity where serial_number = '"+serialNumber+"'");
				Iterator assetItr = assetQuery.list().iterator();
				while(assetItr.hasNext())
				{
					AssetEntity asset = (AssetEntity)assetItr.next();
					asset.setProductId(productEntity);
					if(builtDate!=null)
						asset.setPurchase_date(new Timestamp(builtDate.getTime()));
					asset.setClient_id(clientEntity);
					asset.setActive_status(true);
					session.update(asset);
				}
	
				//CR487 : Sai Divya : Service Schedules update after personality details update.sn
				Date installDate = null;
				String dealerCode = null;
				int productId = 0;
				String installDateStr = null;
				String vin = null;

				String selectVinQuery = "SELECT a.serial_number, a.install_date, ac.mapping_code, a.Product_ID FROM asset a " +
				                         "INNER JOIN asset_owner_snapshot aos ON a.serial_number = aos.serial_number " +
				                         "INNER JOIN account ac ON ac.account_id = aos.account_id " +
				                         "WHERE aos.account_type = 'Dealer' AND ac.status = 1 AND a.serial_number  like '%"+serialNumber+"'";

				iLogger.info("selectVinQuery: " + selectVinQuery);
				ConnectMySQL connFactory = new ConnectMySQL();
				Connection conn = connFactory.getConnection();
				Statement st = conn.createStatement();

				try (ResultSet rs = st.executeQuery(selectVinQuery)) {
				    if (rs.next()) {
				        vin = rs.getString("serial_number");
				        installDate = rs.getDate("install_date");
				        dealerCode = rs.getString("mapping_code");
				        productId = rs.getInt("Product_ID");
				        iLogger.info("productID"+productId);
				    }

				    String query = "SELECT ass.serialnumber, ss.serviceScheduleId " +
				                   "FROM asset_service_schedule ass " +
				                   "INNER JOIN service_schedule ss ON ass.Service_Schedule_Id = ss.serviceScheduleId " +
				                   "INNER JOIN products p ON p.Asset_Type_ID = ss.Asset_Type_ID " +
				                   "WHERE ass.serialnumber = '" + vin + "' AND p.Product_ID = '" + productId + "' AND ass.alert_gen_flag = 1";

				    iLogger.info("query: " + query);
				    try (ResultSet rs1 = st.executeQuery(query)) {
				    	if (rs1.next()) {
			                // If any data is found 
				    		 iLogger.info("Service schedule already exists for VIN: " + vin);
			                }
				    	else
				    	{
				    		// No service schedule found
				            if (installDate != null) {
				                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				                installDateStr = sdf.format(installDate);
				                String updateQueryString = "UPDATE asset_service_schedule SET alert_gen_flag = 0 WHERE SerialNumber = '" + serialNumber + "'";
				                int rowsAffected = st.executeUpdate(updateQueryString);
				                iLogger.info(updateQueryString);

				                // Add service schedule for the machine
				                String response = new InstallationDateDetailsImpl().setAssetserviceSchedule(serialNumber, installDateStr,
				                        dealerCode, "", "", true);
				                if (!response.contains("SUCCESS")) {
				                    iLogger.info("Failure occurred in setting up Service Schedule for VIN " + serialNumber);
				                    status = "FAILURE: Failure occurred in setting up Service Schedule for VIN " + serialNumber;
				                }
				            } else {
				                iLogger.info("Install Date is not available.");
				            }			
				    	}
				     }
			            	    
				} catch (Exception e) {
				    e.printStackTrace();
				    status = "FAILURE";
				    fLogger.fatal("Exception occurred: " + e.getMessage());
				}
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				//CR487 : Sai Divya : Service Schedules update after personality details update.en
				//Set the Product Details to assetOwnerSnapshot Entity
				//DefectId:20150519 @ suprava AssetPersonality updation in AssetOwnerSnapshot table at run time
				Query assetOwnerSnapshotQuery = session.createQuery("from AssetOwnerSnapshotEntity where serialNumber = '"+serialNumber+"'");
				Iterator assetOwnerSnapshotItr = assetOwnerSnapshotQuery.list().iterator();
				while(assetOwnerSnapshotItr.hasNext())
				{
					AssetOwnerSnapshotEntity AssetOwnerSnapshot = (AssetOwnerSnapshotEntity)assetOwnerSnapshotItr.next();
					if(AssetOwnerSnapshot.getAssetGroupId()==null)
					{
						AssetOwnerSnapshot.setAssetGroupId(productEntity.getAssetGroupId());
					}
					if(AssetOwnerSnapshot.getAssetTypeId()==null)
					{
						AssetOwnerSnapshot.setAssetTypeId(productEntity.getAssetTypeId());
					}
					session.update(AssetOwnerSnapshot);
				}
				//DefectId:20150519 End
	
				// Invoking the Method to update the Edge Proxy device status info table to remove the dependency od data from WISE tables.
				setDeviceStatusInfo(groupId, groupName,typeId, typeName, serialNumber);
	
	
			}
	
			catch(CustomFault e)
			{
				status = "FAILURE-"+e.getFaultInfo();
				bLogger.error("EA Processing: AssetPersonality: "+messageId+" FAILURE - "+e.getFaultInfo());
				return status;
			}
	
			catch(Exception e)
			{
				status = "FAILURE-"+e.getMessage();
				fLogger.fatal("EA Processing: AssetPersonality: "+messageId+ " Fatal Exception :"+e);
	
			}
	
			finally
			{
				/* if(session.getTransaction().isActive())
	           {
	                 session.getTransaction().commit();
	           }*/
	
				//DF20140715 - Rajani Nagaraju - To Avoid Session Close Exception
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
					status = "FAILURE-"+e.getMessage();
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
		//***********************************************END of Set Asset Personality details ***************************************************
	
		//Method 6: 
		/* This method sets the asset personality details
		 * DefectID: 735, Rajani Nagaraju, 20130704 
		 * @param machineName MachineNickName
		 * @param assetGroupName Machine Profile
		 * @param assetTypeName Model
		 * @param installDate installation Date
		 * @param serialNumber VIN
		 * @param description Machine description
		 * @param purchaseDate Asset purchase date
		 * @param assetClassName AssetClass
		 * @param make Machine Make
		 * @return Returns the status String
		 * @throws CustomFault
	
		/*
		public String setAssetPersonalityDetails(String machineName, String assetGroupName, String assetTypeName, String installDate,
												String serialNumber, String description, String purchaseDate, String assetClassName,String engineTypeName, int make) throws CustomFault
		{
			String status = "SUCCESS";
	
			Logger businessError = Logger.getLogger("businessErrorLogger");
	       Logger fatalError = Logger.getLogger("fatalErrorLogger");
	
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
	
				SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date installedDate=null;
				Date purchasedDate = null;
	
				//convert String to date
				if(installDate!=null)
				{
					try
					{
						installedDate = datetimeFormat.parse(installDate);
					}
					catch(ParseException e)
					{
						status = "FAILURE";
						throw new CustomFault("Invalid date Format. Format should be yyyy-MM-dd" + e.getMessage());
					}
	
				}
	
				if(purchaseDate!=null)
				{
					try
					{
						purchasedDate = datetimeFormat.parse(purchaseDate);
					}
					catch(ParseException e)
					{
						status = "FAILURE";
						throw new CustomFault("Invalid date Format. Format should be yyyy-MM-dd" + e.getMessage());
					}
	
				}
	
				int assetGroupId=0;
				int assetTypeId=0;
				int assetClassId=0;
				int engineTypeId=0;	
				//get OR set asset Group Details
				AssetGroupEntity assetGroup = getAssetGroupEntity(assetGroupName);
				if(assetGroup==null)
				{
					setAssetGroupEntity(assetGroupName, clientEntity.getClient_id());
				}
				assetGroup = getAssetGroupEntity(assetGroupName);
				if(assetGroup!=null)
				{
					assetGroupId = assetGroup.getAsset_group_id(); 
				}
	
	
				//get OR set asset Type details
				AssetTypeEntity assetType = getAssetTypeEntity(assetTypeName);
				if(assetType==null)
				{
					setAssetTypeEntity(assetTypeName, clientEntity.getClient_id());
				}
				assetType = getAssetTypeEntity(assetTypeName);
				if(assetType!=null)
				{
					assetTypeId = assetType.getAsset_type_id();
				}
	
	
				//get OR set asset Class details
				if(assetClassName!=null)
				{
					AssetClassEntity assetClass = getAssetClassEntity(assetClassName);
	
					if(assetClass==null)
					{
						setAssetClassEntity(assetClassName, clientEntity.getClient_id());
					}
					assetClass = getAssetClassEntity(assetClassName);
	
					if(assetClass!=null)
					{
						assetClassId = assetClass.getAssetClassId();
					}	
				}
	
				//get OR set engine type Class details
				if(engineTypeName!=null)
				{
	//				setEngineTypeEntity
					EngineTypeEntity engineTypeClass = getEngineTypeEntity(engineTypeName);
	
					if(engineTypeClass==null)
					{
						setEngineTypeEntity(engineTypeName);
					}
					//DefectID: 735, Rajani Nagaraju, 20130704 
					engineTypeClass = getEngineTypeEntity(engineTypeName);
	
					if(engineTypeClass!=null)
					{
						engineTypeId = engineTypeClass.getEngineTypeId();
					}	
				}
	
				//get the product details
				ProductEntity productEntity=null;
				productEntity = getProductEntity(assetClassId,assetGroupId,assetTypeId,make,clientEntity.getClient_id(),engineTypeId);
	
				if(productEntity==null)
				{
					setProductEntity(null, clientEntity.getClient_id(), assetClassId, assetGroupId, assetTypeId, make,engineTypeId);
	
					productEntity = getProductEntity(assetClassId,assetGroupId,assetTypeId,make,clientEntity.getClient_id(),engineTypeId);
	
				}
	
				Query query = null;
	
				if(! (session.isOpen() ))
	           {
	                       session = HibernateUtil.getSessionFactory().getCurrentSession();
	                       session.getTransaction().begin();
	           }
	
				//Validate Machine Name
				if(machineName!=null)
				{
					//machineName = machineName.toUpperCase();
					query = session.createQuery("from AssetEntity where nick_name='"+machineName+"'and active_status=true and client_id="+clientEntity.getClient_id()+"");
				}
	
				else if(serialNumber!=null)
				{
					query = session.createQuery("from AssetEntity where serial_number='"+serialNumber+"'and active_status=true and client_id="+clientEntity.getClient_id()+"");
				}
	
				Iterator itr = query.list().iterator();
				while(itr.hasNext())
				{
					AssetEntity assetEntity = (AssetEntity)itr.next();
					assetEntity.setDescription(description);
					if(purchasedDate!=null)
						assetEntity.setPurchase_date(new Timestamp(purchasedDate.getTime()));
					if(installedDate!=null)
						assetEntity.setInstall_date(new Timestamp(installedDate.getTime()));
					assetEntity.setActive_status(true);
					assetEntity.setClient_id(clientEntity);
					assetEntity.setProductId(productEntity);
					session.update(assetEntity);
	
				}
	       }
	
	       catch(CustomFault e)
			{
	       	status = "FAILURE";
	       	businessError.error("Custom Fault: "+ e.getFaultInfo());
			}
	
			catch(Exception e)
			{
				status = "FAILURE";
				fatalError.fatal("Exception :"+e.getMessage());
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
	
			return status;
		}*/
	
		//***********************************************END of Set Asset Personality details ***************************************************
	
	
		//***********************************************get owner details for a given serial number****************************************************
		//METHOD 6:
		/** This method returns the AssetOwners for a given SerialNumber 
		 * @param serialNumber serialNumber is specified as String input
		 * @return Returns HashMap<stakeHolder, AccountId>
		 * @throws CustomFault
		 */
		//defect id : asset owner changes - 2013-07-19 - rajani
		public HashMap<String,Integer> getAssetOwners(String serialNumber) throws CustomFault
		{
			HashMap<String,Integer> assetOwnersMap = new HashMap<String,Integer>();
			AccountEntity assetOwnerAccount = null;
			HashMap<String,Integer> localHashMap = new HashMap<String,Integer>();
	
			//Logger businessError = Logger.getLogger("businessErrorLogger");
			// Logger fatalError = Logger.getLogger("fatalErrorLogger");
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
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
	
				//validate Serial number
				AssetEntity assetEntity = getAssetEntity(serialNumber);
				if(assetEntity == null)
				{
					throw new CustomFault("Invalid Serial Number");
				}
	
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
	
				int currentOwnerAccountId= assetEntity.getPrimary_owner_id();
	
				if(currentOwnerAccountId == 0)
				{
					throw new CustomFault("Asset is not assigned to any account");
				}
	
	
				Query q = session.createQuery("from AccountEntity where account_id="+currentOwnerAccountId+" and client_id="+clientEntity.getClient_id()+" and status=true ");
				Iterator itr = q.list().iterator();
				while(itr.hasNext())
				{
					assetOwnerAccount = (AccountEntity)itr.next();
				}
	
	
				//get the account IDs of all the accounts up above the hierarchy until OEM
				AccountEntity childAccount = assetOwnerAccount;
				AccountEntity parentAccount = assetOwnerAccount.getParent_account_id();
				int counter =1;
	
				//DefectID:  - 1288 - Rajani Nagaraju - Custom SMS implementation - This should not goto Infinte Loop
				while(parentAccount != null && counter<10)
				{
	
					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}
					Query query = session.createQuery("from PartnershipMapping where accountFromId ="
							+parentAccount.getAccount_id()+" and accountToId ="+childAccount.getAccount_id());
					Iterator it = query.list().iterator();
					while(it.hasNext())
					{
						PartnershipMapping accountPartner = (PartnershipMapping)it.next();
						localHashMap.put(accountPartner.getPartnerId().getReversePartnerRole(), childAccount.getAccount_id());
	
						childAccount = parentAccount;
						parentAccount = childAccount.getParent_account_id();
	
					}
	
					counter++;
				}
	
				localHashMap.put("OEM", childAccount.getAccount_id());
	
	
				//get the account Ids of OEM, Dealer and Customer
				assetOwnersMap.put("OEM", localHashMap.get("OEM"));
				assetOwnersMap.put("Dealer", localHashMap.get("Dealer"));
				assetOwnersMap.put("Customer", localHashMap.get("Customer"));
	
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
	
			return assetOwnersMap;
		}
	
		//***********************************************END of get owner details for a given serial number****************************************************
	
		//METHOD 7:
		/** This method sets the Asset-owner mapping
		 * @param serialNumber serilNumber as String input
		 * @param accountId AccountId as Integer input
		 * @return Returns the status String
		 * @throws CustomFault customException is thrown if the input parameters are not specified/Invalid
		 */
		public String setAssetOwner(String serialNumber, int accountId) throws CustomFault
		{
			String status = "SUCCESS";
	
			//Logger businessError = Logger.getLogger("businessErrorLogger");
			//Logger fatalError = Logger.getLogger("fatalErrorLogger");
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
	
			try
			{
				//validate serialNumber
				AssetEntity assetEntity = getAssetEntity(serialNumber);
				if(assetEntity == null || assetEntity.isActive_status()==false)
				{
					throw new CustomFault("Invalid Serial Number");
				}
	
				//validate account ID
				DomainServiceImpl domainService = new DomainServiceImpl();
	
				AccountEntity accountEntity = domainService.getAccountObj(accountId);
				if(accountEntity == null)
				{
					throw new CustomFault("Invalid Account ID");
				}
	
	
				//Insert/update record into asset account mapping
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
	
				Query q = session.createQuery("from AssetAccountMapping where serialNumber='"+serialNumber+"'");
				Iterator itr = q.list().iterator();
				int update = 0;
	
				while(itr.hasNext())
				{
					update=1;
	
					AssetAccountMapping assetAccount = (AssetAccountMapping)itr.next();
	
					String hql = "update AssetAccountMapping set accountId= :newOwner where serialNumber='"+assetAccount.getSerialNumber().getSerial_number().getSerialNumber()+"'";
					Query query = session.createQuery(hql);
					query.setInteger("newOwner",accountEntity.getAccount_id());
					int rowCount = query.executeUpdate();
	
				}
	
				if(update == 0)
				{
					AssetAccountMapping assetAccount = new AssetAccountMapping();
					assetAccount.setAccountId(accountEntity);
					assetAccount.setSerialNumber(assetEntity);
					session.save(assetAccount);
				}
			}
	
			catch(CustomFault e)
			{
				status="FAILURE";
				bLogger.error("Custom Fault: "+ e.getFaultInfo());
			}
	
			catch(Exception e)
			{
				status="FAILURE";
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
	
			return status;
		}
		//*********************************************************** END of Mapping account owners to Asset****************************************
		/** method get Asset Extended Entity for a given Serial Number	 * 
		 * @param serial_number
		 * @return AssetExtendedDetailsEntity
		 */
	
		public AssetExtendedDetailsEntity getAssetExtended(String serial_number)
		{
			AssetExtendedDetailsEntity assetExtendedObj =null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();   
			try
			{
				Query query = session.createQuery("from AssetExtendedDetailsEntity where serial_number='"+serial_number+"'");
				Iterator itr = query.list().iterator();
				while(itr.hasNext())
				{
					assetExtendedObj = (AssetExtendedDetailsEntity) itr.next();			
				}	
			}
			finally
			{
				if(session.getTransaction().isActive())
				{
					session.getTransaction().commit();
				}
				if(session.isOpen()){
					session.flush();
					session.close();
				} 
			} 
	
			return assetExtendedObj;			
		} 
	
	
		//METHOD 09:
		//DefectID: Asset Owner Changes - 2013-07-19 - Rajani Nagaraju
		/** This method returns the List of AssetEntity mapped to the specified account
		 * @param accountId AccountId as Integer input
		 * @return Returns the List of AssetEntity
		 */
		public List<AssetEntity> getAccountAssets(int accountId)
		{
			List<AssetEntity> assetEntityList = new LinkedList<AssetEntity>();
	
			//Logger fatalError = Logger.getLogger("fatalErrorLogger");
			Logger fLogger = FatalLoggerClass.logger;
			//Logger bLogger = BusinessErrorLoggerClass.logger;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			Logger iLogger = InfoLoggerClass.logger;
			//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
			//  session.beginTransaction();
	
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
					//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
					if(session.getTransaction().isActive() && session.isDirty())
					{
						iLogger.info("Opening a new session");
						session = HibernateUtil.getSessionFactory().openSession();
					}
					session.getTransaction().begin();
				} 
	
				Query query = session.createQuery("from AssetEntity where primary_owner_id="+accountId+" and active_status=true and client_id="+clientEntity.getClient_id()+"");
				Iterator itr = query.list().iterator();
				while(itr.hasNext())
				{
					AssetEntity asset = (AssetEntity)itr.next();
					assetEntityList.add(asset);
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
			return assetEntityList;
		}
	
	
		//************************************** set VIN - MachineName association during Roll Off from JCB ********************************
		/** This method sets the VIN details
		 * @param serialNumber serialNumber VIN as String input
		 * @param nickName Engine Number as String input
		 * @param chasisNumber ChasisNumber as String input
		 * @param messageId Uniquely Identifies the record
		 * @return Returns the status string
		 */
	
	
		//Defect Id : Asset Owner changes - 2013- 07-19- Rajani Nagaraju
	
		public String setVinMachineNameAssociation(String serialNumber, String nickName, String chasisNumber, String make, String builtDate, 
				//String machineNumber, String messageId)//CR395.o
				String machineNumber, String messageId, String rollOffDate,String machineCategory)//CR395.n
		{
			String status ="SUCCESS-Record Processed";
			String serialNum=null, imeiNumber=null,simNumber=null;
			Calendar c = Calendar.getInstance();
			//Timestamp date2 =new Timestamp(c.getTime().getTime());//CR395.o
			int segId=0;
			// check to refrain invoking billing module again for already rolled off machines 
			boolean reRollOff = false;//JCB6332.n

			//Logger fatalError = Logger.getLogger("fatalErrorLogger");
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
	
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
				AssetControlUnitEntity assetControl = getAssetControlUnitEntity(serialNumber);
	
				//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
				if(assetControl==null)
				{
					status = "FAILURE-Registration Data not received for the VIN: "+serialNumber;
					bLogger.error("EA Processing: AssetRolloffFromJCB: "+messageId+" FAILURE - Registration Data not received for the VIN: "+serialNumber);
					return status;
				}
	
				/*DF:JCB0213 - Rajani Nagaraju - START - Commenting the below line -
				 * No Need to open a session here as we are moving across different sessions and open a session 
				 * only when we are querying using that session 
				 */
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
				/*DF:JCB0213 - Rajani Nagaraju - END - */
	
				if(assetControl!=null && assetControl.getSerialNumber()!=null)
				{
	
					Properties prop = new Properties();
					String clientName=null;
					String primaryOwner = null;
	
					prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
					clientName= prop.getProperty("ClientName");
					//primaryOwner = prop.getProperty("PrimaryOwner");
					//DF20170801 - SU334449 : Primary Owner now pointing to GlobalOwner=JCB_Asia from properties file for SAARC changes.
					primaryOwner = prop.getProperty("GlobalOwner");
	
	
					IndustryBO industryBoObj = new IndustryBO();
					ClientEntity client = industryBoObj.getClientEntity(clientName);
	
					//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
					if(client==null)
					{
						status = "FAILURE-Client Undefined";
						bLogger.error("EA Processing: AssetRolloffFromJCB: "+messageId+" FAILURE - Client Undefined ");
						return status;
					}
	
					TenancyBO tenancyBoObj = new TenancyBO();
					AccountEntity accountEntity = tenancyBoObj.getAccountObj(primaryOwner);
					//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
					if(accountEntity==null)
					{
						status = "FAILURE-Primary Owner Undefined";
						bLogger.error("EA Processing: AssetRolloffFromJCB: "+messageId+" FAILURE - Primary Owner Undefined");
						return status;
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
	
						//DF20150928 - Rajani Nagaraju - Error in any Session is getting perculated to other new sessions and hence which is thrown to client.(WSDL not available)
						//Hence replacing session.getTransaction().begin() to session.beginTransaction();
						//session.getTransaction().begin();
						session.beginTransaction();
					}
	
					//Check for the existence of AssetEntity
					/*DF:JCB0213 - Rajani Nagaraju - START - Getting the AssetEntity in the same session, 
					 * so that everything is maintained as a single transaction*/
					//AssetEntity assetEntity = getAssetEntity(serialNumber);
					AssetEntity assetEntity = null;
					Query query = session.createQuery("from AssetEntity where serial_number ='"+serialNumber+"' and active_status=true and client_id="+client.getClient_id()+"");
					Iterator itr = query.list().iterator();
	
					while(itr.hasNext())
					{
						assetEntity = (AssetEntity) itr.next();
					}
					/*DF:JCB0213 - Rajani Nagaraju - END */ 
	
					//DefectID:20140116 @suprava 
					//Validate Built Date
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					c = Calendar.getInstance();
					Timestamp scheduledDate=null;
					Date vinBuiltDate=null;
	
					//DF20140715 - Rajani Nagaraju - Null Check
					if(builtDate!=null)
					{
						vinBuiltDate = dateFormat.parse(builtDate);
						//update the Built Date
						c.setTime(vinBuiltDate);
						scheduledDate = new Timestamp(c.getTime().getTime());
					}
					//CR395.sn
					Date vinRollOffDate=null;
					Timestamp vinRollOffTs=null;
					if(rollOffDate!=null){
						vinRollOffDate = dateFormat.parse(rollOffDate);
						//update the roll off Date
						c.setTime(vinRollOffDate);
						vinRollOffTs = new Timestamp(c.getTime().getTime());
					}
					//CR395.en
					
					//CR395.so
					//Get Current date and time
					/*Date date = new Date();
					c.setTime(date);
					date2 =new Timestamp(c.getTime().getTime());*/
					//CR395.eo
					if(assetEntity!=null && assetEntity.getSerial_number()!=null)
					{
						reRollOff=true;//JCB6332.n
						assetEntity.setNick_name(nickName);
						assetEntity.setChasisNumber(chasisNumber);
						assetEntity.setMake(make);
						//CR512
						assetEntity.setMachineCategory(machineCategory);
						assetEntity.setMachineNumber(machineNumber);
						//DF20170710: SU334449 - Setting TimeZone for the SAARC countries to IST time for initial Roll-Off
						assetEntity.setTimeZone("(GMT+05:30)");
						session.update(assetEntity);
					}
	
					else
					{
						//Df20150330 - Rajani Nagaraju - Adding SegmentID determines the partition to which the row belongs to
						Query assetCountQ = session.createQuery(" select count(*) from AssetEntity ");
						Iterator assetCountItr = assetCountQ.list().iterator();
						long assetCount=0;
						while(assetCountItr.hasNext())
						{
							assetCount = (Long)assetCountItr.next();
						}
						int segmentId = ((int)assetCount/500);
	
						AssetEntity newAssetEntity = new AssetEntity();
						newAssetEntity.setSerial_number(assetControl);
						newAssetEntity.setNick_name(nickName);
						newAssetEntity.setPrimary_owner_id(accountEntity.getAccount_id());
						newAssetEntity.setActive_status(true);
						newAssetEntity.setClient_id(client);
						//CR512
						assetEntity.setMachineCategory(machineCategory);
						newAssetEntity.setChasisNumber(chasisNumber);
						newAssetEntity.setMake(make);
						newAssetEntity.setPurchase_date(scheduledDate);
						newAssetEntity.setMachineNumber(machineNumber);
						//newAssetEntity.setDateTime(date2);//CR395.o
						newAssetEntity.setDateTime(vinRollOffTs);//CR395.n
						newAssetEntity.setSegmentId(segmentId);
						//DF20170801: SU334449 - Setting TimeZone for the SAARC countries to IST time for initial Roll-Off
						newAssetEntity.setTimeZone("(GMT+05:30)");
						//DF20180904: KO369761 - Setting Renewal flag default value as 1. 
						newAssetEntity.setRenewal_flag(1);
						session.save(newAssetEntity);
	
						//DF20150612 - Rajani Nagaraju - Comitting the asset save before call to getAssetEntity, since getAssetEntity will open a new session which doesnot have this information
						/*DF:JCB0213 - Rajani Nagaraju - START - Commenting the below line of code to make sure that if the record gets inserted into Asset,
						 * then it has to insert mandatorily into assetprofile and assetowners - i.e., Combining all into single transaction
						 * Issue with this DefectID was Fleet General was diplaying blank since asset profile table doesn't have entry for the machine
						 */
						/*try
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
				            	status="FAILURE-"+e.getMessage();
				            	//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
				            	fLogger.fatal("Exception in commiting the record:"+e);
				            }
	
						assetEntity = getAssetEntity(serialNumber);*/
						assetEntity = newAssetEntity;
	
						/*DF:JCB0213 - Rajani Nagaraju - END */
					}
	
					/*DF:JCB0213 - Rajani Nagaraju - START - Commenting the below line -
					 * No Need to open a session here as we are moving across different sessions and open a session 
					 * only when we are querying using that session 
					 */
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
					/*DF:JCB0213 - Rajani Nagaraju - END */
	
					if(assetEntity!=null && accountEntity!=null)
					{
						/*DF:JCB0213 - Rajani Nagaraju - START - adding logger to track the flow */
						iLogger.info("Set Asset Profile and Asset Ownership for the VIN: "+serialNumber);
						/*DF:JCB0213 - Rajani Nagaraju - END - adding logger to track the flow */
	
						//Set primary Contact to JCB India
						//Date currentDate = new Date();//Get the current date to set ownership_date in asset_owners//CR395.o
						Query query2 = session.createQuery("from AssetAccountMapping where serialNumber='"+assetControl.getSerialNumber()+"'");
						Iterator itr2 = query2.list().iterator();
						boolean present=false;
						int ownerCount =0;
	
						AssetAccountMapping assetOwners =null;
						while(itr2.hasNext())
						{
							assetOwners = (AssetAccountMapping)itr2.next();
	
							if(accountEntity.getAccount_id()==assetOwners.getAccountId().getAccount_id())
							{
								present = true;
							}
	
						}
	
						if(present==false)
						{
							AssetAccountMapping assetAccountMap = new AssetAccountMapping();
							assetAccountMap.setAccountId(accountEntity);
							assetAccountMap.setSerialNumber(assetEntity);
							//assetAccountMap.setOwnershipStartDate(currentDate);//CR395.o
							assetAccountMap.setOwnershipStartDate(vinRollOffTs);//CR395.n
							session.save(assetAccountMap);
						}
	
	
						//set AssetExtended details
						Query query3 = session.createQuery("from AssetExtendedDetailsEntity where serial_number='"+assetControl.getSerialNumber()+"'");
						Iterator itr3 = query3.list().iterator();
						boolean extendedPresent = false;
						while(itr3.hasNext())
						{
							AssetExtendedDetailsEntity assetExtended = (AssetExtendedDetailsEntity) itr3.next();
							extendedPresent = true;
						}
	
						if(extendedPresent==false)
						{
							AssetExtendedDetailsEntity extendedDetails = new AssetExtendedDetailsEntity();
							extendedDetails.setSerial_number(assetEntity);
							//DF20140122 - Rajani Nagaraju - Default state of the Machine should be TRANSIT
							extendedDetails.setDevice_Status("TRANSIT");
							session.save(extendedDetails);
						} 
	
	
					}
	
					//DF20131108 - Rajani Nagaraju - To update Edge Proxy table on RollOff with status of machine as Normal
					//String epUpdateStatus = updateVinStatusToEP(assetControl.getSerialNumber(),0);
					//DF20140122 - Rajani Nagaraju - Default state of the Machine should be TRANSIT
					//DefectId:20141021 @Suprava Refresh CMH Feature Changes 
					iLogger.info("calling updateVinStatusToEP");
					String epUpdateStatus = updateVinStatusToEP(assetControl.getSerialNumber(),1,null,null);
					iLogger.info("updateVinStatusToEP returned : "+epUpdateStatus);
					if(epUpdateStatus.equalsIgnoreCase("FAILURE"))
					{
						fLogger.error("EA Processing: AssetRolloffFromJCB: "+messageId+ "EdgeProxy- Device Status table not updated for the VIN: "+assetControl.getSerialNumber());
					}
	
					serialNum=assetEntity.getSerial_number().getSerialNumber();
					imeiNumber=assetEntity.getSerial_number().getImeiNo();
					simNumber=assetEntity.getSerial_number().getSimNo();
					segId = assetEntity.getSegmentId();
				}
	
			}
	
			catch(Exception e)
			{
				status = " FAILURE-"+e.getMessage();
				fLogger.fatal("EA Processing: AssetRolloffFromJCB: "+messageId+ " Fatal Exception :"+e);
			}
	
			finally
			{
				/* if(session.getTransaction().isActive())
	              {
	                    session.getTransaction().commit();
	              }*/
	
				//DF20140715 - Rajani Nagaraju - To Avoid Session Close Exception
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
					status="FAILURE-"+e.getMessage();
					//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
					fLogger.fatal("Exception in commiting the record:"+e);
				}
	
				if(session.isOpen())
				{
					session.flush();
					session.close();
				}
	
			}
			//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
			/*try
			{
				//DF20151019 - Rajani Nagaraju - Populating required details in OrientDB
				iLogger.info("EA Processing: AssetRolloffFromJCB: "+messageId+ ": Insert details into OrientAppDB");
				String messageString=serialNum+"|"+machineNumber+"|"+nickName+"|"
						+ imeiNumber+"|"+simNumber+"|"+
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date2);
				String orientDbStatus = new EADataPopulationBO().AssetRollOff(messageId, messageId, messageString, segId);
				iLogger.info("EA Processing: AssetRolloffFromJCB: "+messageId+ ": Insert details into OrientAppDB Status:"+orientDbStatus);
			}
	
			catch(Exception e)
			{
				fLogger.fatal("EA Processing: AssetRolloffFromJCB: "+messageId+ ": Exception in inserting details into OrientAppDB: "+e.getMessage());
				e.printStackTrace();
			}*/
	
			//return status; //CR334.o
			//return status + "|" + date2; //CR334.n
			 //return status + "|" + date2 + "|" + reRollOff; //JCB6332.n//CR395.o
			 return status + "|" + reRollOff;//CR395.n
		}
	
	
	
	
		/**
		 * method to get asset extended details for a serial number
		 * @param serial_number
		 * @return AssetExtendedImpl
		 * @throws CustomFault
		 */
	
		public AssetExtendedImpl getAssetExtendedDetails(String serial_number) throws CustomFault
		{
			//validate the serial number
			AssetEntity assetEntity = getAssetEntity(serial_number);
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			if(assetEntity == null)
			{
				bLogger.error("Invalid Serial Number");
				throw new CustomFault("Invalid Serial Number");
			}
			//get the tenancyId
			TenancyEntity tenancyId = getTenancyId(serial_number);
			AssetExtendedImpl assetImplObj  = new AssetExtendedImpl();
			long startTime = System.currentTimeMillis();
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();  
			Timestamp cmhUpdatedTime=null;
			long cmhMillis=0L,cmhDiff=0L;
			try
			{        
				Query assetExtendedDetails = session.createQuery("from AssetExtendedDetailsEntity where serial_number = '"+serial_number +"'");
				Iterator it = assetExtendedDetails.list().iterator();
				AssetExtendedDetailsEntity assetExtendedentity =null;
				while(it.hasNext())
				{
					assetExtendedentity =(AssetExtendedDetailsEntity)it.next() ;
	
					//DF20131108 - Rajani Nagaraju - To update Edge Proxy table on RollOff with status of machine as Normal
					assetImplObj.setDeviceStatus(assetExtendedentity.getDevice_Status());
	
					if(assetExtendedentity.getDriverContactNumber()!=null){
						assetImplObj.setDriverContactNumber(assetExtendedentity.getDriverContactNumber());
					}
					if(assetExtendedentity.getDriverName()!=null){
						assetImplObj.setDriverName(assetExtendedentity.getDriverName());
					}
					//DF20181211 : Mani: cmhupdatedtime should be >20mins of currenttime to send the offset value as a response
					if(assetExtendedentity.getCmhUpdatedTime()!=null){
						cmhUpdatedTime=assetExtendedentity.getCmhUpdatedTime();
						cmhMillis=cmhUpdatedTime.getTime();
						Date todayDate = new Date();
						Calendar c1 = Calendar.getInstance();
						c1.setTime(todayDate);
						Timestamp currentTime = new Timestamp(c1.getTime().getTime());
						cmhDiff=(currentTime.getTime()-cmhMillis); 
						if((cmhDiff/1000)<1200){ //20 mins in seconds
							if(assetExtendedentity.getOffset()!=null){
								assetImplObj.setOffset(assetExtendedentity.getOffset());
							}
						}
						else
						{
							if(assetExtendedentity.getOffset()!=null){
								assetImplObj.setOffset("");
							}
						}
	
					}
					else
					{
						if(assetExtendedentity.getOffset()!=null){
							assetImplObj.setOffset(assetExtendedentity.getOffset());
						}
					}
					/*if(assetExtendedentity.getOffset()!=null){
						assetImplObj.setOffset(assetExtendedentity.getOffset());
					}*/
					if(assetExtendedentity.getOperatingEndTime()!=null){
						assetImplObj.setOperatingEndTime(assetExtendedentity.getOperatingEndTime().toString().substring(11));
					}
					if(assetExtendedentity.getOperatingStartTime()!=null){
						assetImplObj.setOperatingStartTime(assetExtendedentity.getOperatingStartTime().toString().substring(11));
					}
					if(assetExtendedentity.getSerial_number()!=null){
						assetImplObj.setSerialNumber(assetExtendedentity.getSerial_number());
					}
					if(assetExtendedentity.getUsageCategory()!=null){
						assetImplObj.setUsageCategory(assetExtendedentity.getUsageCategory());
					}
					if(assetExtendedentity.getNotes()!=null){
						assetImplObj.setNotes(assetExtendedentity.getNotes());       
					}
					if(tenancyId != null){
						assetImplObj.setPrimaryOwnerId(tenancyId.getTenancy_id());
	
					}
				}
	
				//FW Version Number : To be displayed in the portal: 2015-03-04 : Deepthi
	
				/*String FWVersionNumber =null;
				Query fwVersionNumber = session.createQuery( " select a.fwVersionNumber from AssetMonitoringHeaderEntity a, " +
									" AssetMonitoringSnapshotEntity b " +
									" where a.transactionNumber = b.transactionNumber " +
									" and b.serialNumber = '" + serial_number +"'" );
	
				Iterator fwVersionNumberItr = fwVersionNumber.list().iterator();
				String ResultSet = null;
				while(fwVersionNumberItr.hasNext())
				{
					FWVersionNumber = (String) fwVersionNumberItr.next();				
	
				}	*/
	
				//DF20160715 @Roopa Fetching FWversion from New AMS table
	
				String txnKey="AssetExtendedService::setAssetExtendedDetails::Get FwVersion";
	
				//DF20161222 @Roopa changing asset_monitoring_snapshot_new to asset_monitoring_snapshot which is having txndata as json column
	
				List<AMSDoc_DAO> snapshotObj=new ArrayList<AMSDoc_DAO> ();
	
				DynamicAMS_Doc_DAL amsDaoObj=new DynamicAMS_Doc_DAL();
	
				snapshotObj=amsDaoObj.getAMSData(txnKey, serial_number);
	
				iLogger.info(txnKey+"::"+"AssetExtendedService::getAssetExtendedDetails::AMS DAL::getAMSData Size:"+snapshotObj.size());
	
				HashMap<String,String> txnDataMap=new HashMap<String, String>();
	
				if(snapshotObj.size()>0)
					txnDataMap=snapshotObj.get(0).getTxnData();
	
				/*
				String parameters=snapshotObj.get(0).getParameters();
				if(parameters!=null){
	
				String [] currParamList=parameters.split("\\|", -1);
	
				if(currParamList.length>8){
	
				FWVersionNumber=currParamList[8];
				}
				}*/
	
				//to display NIP versions for LL4 machines
				if(txnDataMap!=null && txnDataMap.size()>0){
					String fw_nip_versions=txnDataMap.get("FW_VER")+"|"+txnDataMap.get("NIPFWVersion");
					assetImplObj.setFwVersionNumber(fw_nip_versions);
				}
	
			}
			catch(Exception e){
				fLogger.fatal("Exception :"+e);
			}
			finally{
				if(session.getTransaction().isActive()){
					session.getTransaction().commit();
				}
				if(session.isOpen()){
					session.flush();
					session.close();
				} 
			}
			long endTime=System.currentTimeMillis();
			iLogger.info("Total execution time "+ String.valueOf(endTime-startTime)+ "(ms)");       
			return assetImplObj;
		}
	
	
		//------------------------------------------------ To Set the Asset Profile Details for a given Machine -----------------------------------
		/** This Method to sets the Asset Profile details of a machine
		 * //DF20131108 - Rajani Nagaraju - To update Edge Proxy table on RollOff with status of machine as Normal
		 * @param serial_number
		 * @param OperatingStarttime
		 * @param OperatingEndtime
		 * @param offset
		 * @param DriverName
		 * @param DrivercontactNo
		 * @param UsageCategory
		 * @param notes
		 * @return String
		 * @throws CustomFault
		 */
	
		public String setAssetExtendedDetails(String serial_number,String OperatingStarttime,String OperatingEndtime,String offset, 
				String DriverName, String DrivercontactNo, String UsageCategory, String notes, String transitStatus,String cmhLoginId)throws CustomFault
	
				{
			//Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			String response= "SUCCESS";
			if(transitStatus==null)
				transitStatus="NORMAL";
	
			SimpleDateFormat datetimeFormat = new SimpleDateFormat("hh:mm:ss a");
			Timestamp startTime = null;
			Timestamp endTime = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			if(OperatingStarttime!=null)
			{
				if(!OperatingStarttime.equals(""))
				{
					try
					{
						Date operatingStartDate = datetimeFormat.parse(OperatingStarttime);
						startTime = new Timestamp(operatingStartDate.getTime());
					}
					catch(ParseException e)
					{
						bLogger.error("Operating Start time is not in the format of hh:mm:ss a");
						throw new CustomFault("Please provide the time in the format hh:mm:ss a");
					}
				}			
			}
	
			if(OperatingEndtime!=null)
			{
				if(!OperatingEndtime.equals(""))
				{	
	
					try
					{
						Date operatingEndDate = datetimeFormat.parse(OperatingEndtime);
						endTime = new Timestamp(operatingEndDate.getTime());
					}
					catch(ParseException e)
					{
						bLogger.error("Operating End time is not in the format of hh:mm:ss a");
						throw new CustomFault("Please provide the time in the format hh:mm:ss a");
					}
				}
			}
	
			AssetEntity assetEntity = getAssetEntity(serial_number);
			if(assetEntity==null || assetEntity.getSerial_number()==null)
			{
				bLogger.error("Invalid Serial Number");
				throw new CustomFault("Invalid Serial Number");
			}
	
			AssetExtendedDetailsEntity assetDet = getAssetExtended (serial_number);
	
			//Update Asset Profile Details
			if(!((assetDet==null)||(assetDet.getSerial_number()==null)))
			{
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				try
				{
					AssetExtendedDetailsEntity assetExtendedDet1 = (AssetExtendedDetailsEntity) session.merge(assetDet);                     
					//DF20181211 : Mani : insert cmhUpdatedTime when stored offset and input offset values are different
					if((assetDet.getOffset()==null||assetDet.getOffset().isEmpty())&& offset!=null)
					{
						Date todayDate = new Date();
						Calendar c1 = Calendar.getInstance();
						c1.setTime(todayDate);
						Timestamp cmhUpdatedTime = new Timestamp(c1.getTime().getTime());
						assetExtendedDet1.setCmhUpdatedTime(cmhUpdatedTime);
					}
					if(offset!=null && assetDet.getOffset()!=null)
					{
						if(!(assetDet.getOffset().equals(offset)))
						{
							Date todayDate = new Date();
							Calendar c1 = Calendar.getInstance();
							c1.setTime(todayDate);
							Timestamp cmhUpdatedTime = new Timestamp(c1.getTime().getTime());
							assetExtendedDet1.setCmhUpdatedTime(cmhUpdatedTime);
						}
					}
					if(DrivercontactNo!=null)
						assetExtendedDet1.setDriverContactNumber(DrivercontactNo);            
					if(DriverName!=null)
						assetExtendedDet1.setDriverName(DriverName);
					//Changes done by Juhi on 18 July 2013 
					if(offset!=null)
						assetExtendedDet1.setOffset(offset);
					assetExtendedDet1.setOperatingEndTime(endTime);
					assetExtendedDet1.setOperatingStartTime(startTime);
					if(UsageCategory!=null)
						assetExtendedDet1.setUsageCategory(UsageCategory);
					if(notes!=null)
						assetExtendedDet1.setNotes(notes);		
	
					//DF20131108 - Rajani Nagaraju - To update Edge Proxy table on RollOff with status of machine as Normal
					assetExtendedDet1.setDevice_Status(transitStatus);
					session.save(assetExtendedDet1);
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
	
			//Insert Asset Profile Details
			else
			{
				AssetExtendedDetailsEntity assetentObj = new AssetExtendedDetailsEntity();
				assetentObj.setDriverContactNumber(DrivercontactNo);            
				assetentObj.setDriverName(DriverName);
				assetentObj.setOffset(offset);
				assetentObj.setOperatingEndTime(endTime);
				assetentObj.setOperatingStartTime(startTime);
				assetentObj.setUsageCategory(UsageCategory);
				assetentObj.setNotes(notes);
				assetentObj.setSerial_number(assetEntity);
				//DF20131108 - Rajani Nagaraju - To update Edge Proxy table on RollOff with status of machine as Normal
				assetentObj.setDevice_Status(transitStatus);
				//DF20181211 : Mani: new column cmhUpdatedTime
				Date todayDate = new Date();
				Calendar c1 = Calendar.getInstance();
				c1.setTime(todayDate);
				Timestamp cmhUpdatedTime = new Timestamp(c1.getTime().getTime());
				assetentObj.setCmhUpdatedTime(cmhUpdatedTime);
				assetentObj.save();			
			}
			// Shajesh : 13-10-2021 : MA Report Changes 
			try{
				response = updateNotesForVinInMoolDA(serial_number,notes);
				}catch (Exception e) {
					fLogger.fatal("Exception in updating  the {/MoolDA/assetProfile/} URL for Notes"+ e.getMessage()+" for VIN:"+serial_number);
					return "FAILURE";
				}
	
			//DF20131108 - Rajani Nagaraju - To update Edge Proxy table on RollOff with status of machine as Normal
			int status=0;
			if(transitStatus.equalsIgnoreCase("TRANSIT"))
				status=1;
			//DefectId offset changes
			String createdTimeStamp=null;
			//Get Current date and time
			/*Calendar c = Calendar.getInstance();
			Date date = new Date();
			c.setTime(date);
			Timestamp date2 =new Timestamp(c.getTime().getTime());*/
			Timestamp currentTime = new Timestamp(new Date().getTime());
			updateVinStatusToEP(serial_number, status,offset,currentTime);
	
			//DefectId:20141028 @Suprava Refresh CMH Feature Changes - To Update the contactId ,refresh CMH,Application Timestamp to cmhr_log_details table
			String previousCMHR=null;
			String Hour="Hour";
			if(serial_number!=null && !serial_number.isEmpty())
			{
				/*if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
	
				Query last1HourPkt = session.createQuery(" select b.parameterValue, a.transactionTime from AssetMonitoringHeaderEntity a, " +
						" AssetMonitoringDetailEntity b, MonitoringParameters c " +
						" where a.serialNumber='"+serial_number+"'" +
						" and a.recordTypeId=3 " +
						" and a.createdTimestamp = (select max(createdTimestamp) from  AssetMonitoringHeaderEntity where serialNumber='"+serial_number+"'" +
						" and recordTypeId=3 )" +
						" and a.transactionNumber=b.transactionNumber " +
						" and b.parameterId = c.parameterId " +
						" and c.parameterName='"+Hour+"'");
				Iterator last1HourPktItr = last1HourPkt.list().iterator();
				Object[] ResultSet = null;
				while(last1HourPktItr.hasNext())
				{
					ResultSet = (Object[]) last1HourPktItr.next();
					previousCMHR = (String)ResultSet[0];
					}*/
	
				//DF20160715 @Roopa Fetching CMH from New AMS table
	
				String txnKey="AssetExtendedService::setAssetExtendedDetails";
	
				/*List<AmsDAO> snapshotObj=new ArrayList<AmsDAO> ();
	
				DynamicAMS_DAL amsDaoObj=new DynamicAMS_DAL();
	
				snapshotObj=amsDaoObj.getAMSData(txnKey, serial_number);
	
				iLogger.info(txnKey+"::"+"AssetExtendedService::setAssetExtendedDetails::AMS DAL::getAMSData Size:"+snapshotObj.size());
	
	
				String parameters=snapshotObj.get(0).getParameters();
				if(parameters!=null){
	
				String [] currParamList=parameters.split("\\|", -1);
	
				previousCMHR=currParamList[3];
				}*/
	
				//DF20161222 @Roopa changing asset_monitoring_snapshot_new to asset_monitoring_snapshot which is having txndata as json column
	
				List<AMSDoc_DAO> snapshotObj=new ArrayList<AMSDoc_DAO> ();
	
				DynamicAMS_Doc_DAL amsDaoObj=new DynamicAMS_Doc_DAL();
	
				snapshotObj=amsDaoObj.getAMSData(txnKey, serial_number);
	
				iLogger.info(txnKey+"::"+"AssetExtendedService::getAssetExtendedDetails::AMS DAL::getAMSData Size:"+snapshotObj.size());
	
				HashMap<String,String> txnDataMap=new HashMap<String, String>();
	
	
				if(snapshotObj!=null && snapshotObj.size()>0)
					txnDataMap=snapshotObj.get(0).getTxnData();
	
				if(txnDataMap!=null && txnDataMap.size()>0)
					previousCMHR=txnDataMap.get("CMH");
			}
			ContactEntity contact =null;
			if(!(cmhLoginId.isEmpty()))
			{
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.getTransaction().begin();
				}
				Query cmhLoginIdQuery = session.createQuery(" from ContactEntity where active_status=true and contact_id='"+cmhLoginId+"'");
				Iterator cmhLoginIdQueryItr = cmhLoginIdQuery.list().iterator();
				while(cmhLoginIdQueryItr.hasNext())
				{
					contact = (ContactEntity)cmhLoginIdQueryItr.next();
				}
			}
			if(contact==null || contact.getContact_id()==null)
			{
				bLogger.error("Invalid Contact_id");
				throw new CustomFault("Invalid Contact_id");
			}
			if(offset!=null)
			{
				CMHRLogDetails CMHRLogDetailsObj = new CMHRLogDetails();
				CMHRLogDetailsObj.setApplication_timestamp(currentTime);
				CMHRLogDetailsObj.setCmhr_loginID(contact);
				CMHRLogDetailsObj.setPreviousCMHR(previousCMHR);
				CMHRLogDetailsObj.setRefreshCHM(offset);
				CMHRLogDetailsObj.setSerial_number(assetEntity);
				CMHRLogDetailsObj.save();			
			}
	
			return "SUCCESS";
	
				}
		@SuppressWarnings("unchecked")
		private String updateNotesForVinInMoolDA(String serial_number,String notes) throws Exception {
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			String connIP, connPort,finalJsonString ;
			connIP = connPort=finalJsonString =  null;
			String result = "FAILURE";
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			HashMap<String,String> finalNotesDetailsMap = new HashMap<String,String>();
			finalNotesDetailsMap.put("AssetID", serial_number);
			finalNotesDetailsMap.put("Notes", notes);
			JSONObject jsonObj = new JSONObject();
			jsonObj.putAll(finalNotesDetailsMap);
			finalJsonString = jsonObj.toString();
			try{
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream
						("remote/wise/resource/properties/configuration.properties"));
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");		
				}catch (Exception e) {
					fLogger.fatal("AssetDetailsBO :updateNotesForVinInMoolDA: " +
							"Exception in getting Server Details for MDA Layer from properties file: " +e+" for VIN:"+serial_number );
					throw new Exception("Error reading from properties file");
				}
				try{
					//Invoking the REST URL from WISE to MOOL DA.
					URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/assetProfile/" +
							"inputPayloadDetails?inputPayloadMap="+URLEncoder.encode(finalJsonString, "UTF-8"));
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
					conn.setRequestProperty("Accept", "text/plain");
					conn.setRequestMethod("GET");
					if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
						throw new Exception("Failed : HTTP error code : "
								+ conn.getResponseCode());
					}
					BufferedReader br = new BufferedReader(new InputStreamReader(
							(conn.getInputStream())));
					String outputResponse = null;
					while((outputResponse = br.readLine()) != null){
						iLogger.info("AssetDetailsBO :updateNotesForVinInMoolDA: " +
								" Response from {/MoolDA/assetProfile/}is: "+outputResponse+" for VIN:"+serial_number);
					}
					iLogger.info("AssetDetailsBO :updateNotesForVinInMoolDA: : Mool DA updated succesfully" );
					return "SUCCESS";
				}catch (Exception e) {
					fLogger.fatal("AssetDetailsBO :updateNotesForVinInMoolDA:  " +
							" Exception in invoking the {/MoolDA/assetProfile/} URL: "+ e.getMessage()+" for VIN:"+serial_number);				
					
				}
			return result;
		}
		//******************************************* END of setting Asset Profile Details for a given Machine *******************************************
		//DefectId:20141029 @suprava Update CHMRDetails table 
	
	
		public String UpdateCHMRDetails(String serial_number, String offset,
				String application_timestamp, String firmware_timestamp,
				String cmhLoginId, String cmhrflag, String previousCMHR) throws CustomFault{
			// TODO Auto-generated method stub
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			//Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			//Logger iLogger = InfoLoggerClass.logger;
			/*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Timestamp firmware_timestamp1=Timestamp.valueOf(dateFormat.format(firmware_timestamp));*/
			Timestamp firmware_timestamp1=Timestamp.valueOf(firmware_timestamp);
			//  System.out.println("firmware_timestamp1"+firmware_timestamp1);
			//   System.out.println("cmhrflag"+cmhrflag);
			AssetEntity assetEntity = getAssetEntity(serial_number);
			String message=null;
			if(assetEntity==null || assetEntity.getSerial_number()==null)
			{
				bLogger.error("Invalid Serial Number");
				throw new CustomFault("Invalid Serial Number");
			}
	
			CMHRLogDetails logDetails = null;
			try{
				if(!(serial_number.isEmpty()))
				{
					if(! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						session.getTransaction().begin();
					}
					Query cmhLoginIdQuery = session.createQuery(" from CMHRLogDetails where application_timestamp='"+application_timestamp+"' and serial_number ='"+serial_number+"' and refreshCHM='"+offset+"'");
					Iterator cmhLoginIdQueryItr = cmhLoginIdQuery.list().iterator();
					while(cmhLoginIdQueryItr.hasNext())
					{
						//System.out.println("Inside while");
						logDetails = (CMHRLogDetails)cmhLoginIdQueryItr.next();
						logDetails.setCmhrflag(cmhrflag);
						logDetails.setFirmware_timestamp(firmware_timestamp1);
						session.update(logDetails);	
					}
					//System.out.println("After While");
					/*if(logDetails==null || logDetails.getSerial_number()==null)
				{
					businessError.error("Invalid Serial_number");
					throw new CustomFault("Invalid Serial_number");
				}*/
					/*Query updateQuery = session.createQuery(
						  "UPDATE CMHRLogDetails cmh "
						+ " SET cmh.firmware_timestamp='" + firmware_timestamp1+ "',cmh.cmhrflag = " + cmhrflag
						+ " WHERE cmh.serial_number = '" +serial_number+ "' AND cmh.application_timestamp like '" +application_timestamp+ "%' AND cmh.refreshCHM like '" +offset);
		infoLogger.info("query for updation :" + updateQuery);
		int rows = updateQuery.executeUpdate();
		if(rows>0){
			message = "SUCCESS";
		}
		else{
			message = "FAILURE";
		}*/
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				//System.out.println("After While");
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
	
			/*if(logDetails!=null && logDetails.getSerial_number()!=null)
			{
				CMHRLogDetailsObj = new CMHRLogDetails();
				CMHRLogDetailsObj.setCmhrflag(cmhrflag);
				CMHRLogDetailsObj.setFirmware_timestamp(firmware_timestamp1);
				//CMHRLogDetailsObj.save();
				session.update(CMHRLogDetailsObj);			
			}*/
	
			return "SUCCESS";
		}
	
		public String clearBacklogTracebilityData(String serial_number,String loginId,
				String sentTimeStamp,String fwTimeStamp,int sFlag) throws CustomFault{
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			ConnectMySQL connFactory = new ConnectMySQL();
			String result = "SUCCESS";
			String isDataPresent="false";
	
			String query=null;
			//		String fwTimeIsNull;
	
			query="select * from clearBacklogTracebility where serial_number="+"'"+serial_number+"'"+" and StatusFlag=1";
			iLogger.info("Query for clear backlog : "+query);

			try (Connection connection = connFactory.getConnection();
					PreparedStatement statement = connection
							.prepareStatement(query);
					ResultSet rs=statement.executeQuery();) {
				while(rs.next())
					isDataPresent="true";
	
			} catch (Exception e) {
				result="FAILURE";
				fLogger.fatal("clearBacklogTracebilityData()::issue while connecting to DB "
						+ e.getMessage());
			}
	
			if(isDataPresent.equalsIgnoreCase("false"))
			{
				if(fwTimeStamp==null)
				{
					query = "insert into clearBacklogTracebility (serial_number,contact_id,RequestTS,FWUpdatedTS,StatusFlag)" +
							" values("+"'"+serial_number+"',"+"'"+loginId+"',"+"'"+sentTimeStamp+"',"+null+","+sFlag+")";
					
					try (Connection connection = connFactory.getConnection();
							PreparedStatement statement = connection
									.prepareStatement(query);
							) {
						statement.executeUpdate();
			
					} catch (Exception e) {
						result="FAILURE";
						fLogger.fatal("clearBacklogTracebilityData()::issue while insertion  to DB "
								+ e.getMessage());
					}
				}
				 
			}
			else
			{
				result="Entry already there in progress";
			}
		//	int flag=1;
			if(fwTimeStamp!=null)
			{
				result="Success";
				
//				String formattedFwTS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fwTimeStamp);
				
try
{
//				Timestamp requestTS=new Timestamp(new Date().getTime());
				 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
			        Date date1 = simpleDateFormat.parse(fwTimeStamp);
			      
			        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			        String strDate = dateFormat.format(date1);
				
				iLogger.info("fwTimeStamp : "+fwTimeStamp+" :: formattedFwTS : "+strDate);
				query = "update clearBacklogTracebility set FWUpdatedTS="+"'"+strDate+"',StatusFlag=0"+
						" where serial_number="+"'"+serial_number+"'"+"  order by RequestTS desc limit 1";
				//flag=0;
			}catch(Exception e)

			{
				e.printStackTrace();
			}
try 
{
	Connection connection = connFactory.getConnection();
	PreparedStatement statement = connection.prepareStatement(query); 
	iLogger.info("Query for clear backlog : "+query);
	statement.executeUpdate(query);
} catch (Exception e) {
	result="FAILURE";
	fLogger.fatal("clearBacklogTracebilityData()::issue while updating/inserting data in DB "
			+ e.getMessage());
}
			}
						
	
	
			return result;
		}
		//******************************************* Get Stock Summary Details *************************************************************
		/** This method returns the dealer wise and zonal wise stock balance
		 * DefectId 912 - Rajani Nagaraju - 2013/07/08 -  Replace direct SQL query to Hibernate
		 * @param loginId userLoginId
		 * @param tenancyId tenancyId as integer input
		 * @return List of zonal and dealer wise stock 
		 * @throws CustomFault
		 */
		public List<StockSummaryImpl> getStockSummaryDetails(String loginId, int tenancyId)
		{
			List<StockSummaryImpl> responseList = new LinkedList<StockSummaryImpl>();
	
			//Logger businessError = Logger.getLogger("businessErrorLogger");
			//Logger fatalError = Logger.getLogger("fatalErrorLogger");
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			//Logger iLogger = InfoLoggerClass.logger;
	
			Connection prodConnection = null;
			Statement statement = null;
			ResultSet rs = null;
			Session session = HibernateUtil.getSessionFactory().openSession();
	
			try
			{
				if(tenancyId==0)
				{
					throw new CustomFault("Please provide Tenancy Id of the Login User");
				}
	
				//Validate tenancyId and get Tenancy Entity Obj
				DomainServiceImpl domainService = new DomainServiceImpl();
				TenancyEntity tenancyEntity = domainService.getTenancyDetails(tenancyId);
	
				if(tenancyEntity==null || tenancyEntity.getTenancy_id()==0)
				{
					throw new CustomFault("Invalid Tenancy Id");
				}
	
				AccountEntity account=null;
	
	
				Query accountQ = session.createQuery(" select at.account_id from AccountTenancyMapping at where at.tenancy_id="+tenancyId+" ");
				Iterator accItr = accountQ.list().iterator();
				if(accItr.hasNext())
				{
					account = (AccountEntity)accItr.next();
				}
	
	
				//get the tenancy Type of user tenancy
				String userTenancyType = tenancyEntity.getTenancy_type_id().getTenancy_type_name();
	
				if(session!=null && session.isOpen())
				{
					session.close();
				}
	
	
				//get Tenancy Type Names for Dealer and Zonal from properties file
				String Zonal =null;
				String Regional=null;
				String Global=null;
	
				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				Zonal= prop.getProperty("ZonalTenancyType");
				Regional = prop.getProperty("RegionalTenancyType");
				//DF20170619: SU334449 Adding new JCB Global tenancy type in the properties file
				Global = prop.getProperty("GlobalTenancyType");
	
	
	
	
	
				//DefectId 912 - Rajani Nagaraju - 2013/07/08 -  Replace direct SQL query to Hibernate
				//DefectID: DF20131115 - Rajani Nagaraju - To Pick the Count of machines only for Current Owner as Dealer
				/*		String basicQueryString = " select  b.childId as Dealer_Id, d.tenancyName as Dealer_Tenancy_Name, b.parentId as Zonal_ID," +
						" a.tenancyName as Zonal_Tenancy_Name, count(*) as machineCount " +
						" from TenancyDimensionEntity a , TenancyBridgeEntity b, TenancyDimensionEntity d , AssetEntity qq, AccountTenancyMapping rr, " +
						" AssetMonitoringFactDataYearAgg e where a.tenancyId = b.parentId " +
						" and e.serialNumber = qq.serial_number and d.tenancyId = rr.tenancy_id" +
						" and rr.account_id=qq.primary_owner_id " +
						" and a.tenancyTypeName='"+Zonal+"' and " +
						" b.parentId  != b.childId and b.childId = d.tenancyId and d.tenacy_Dimension_Id = e.tenancyId and " +
						" d.tenancyTypeName='"+Dealer+"' "+
	//			Keerthi : 27/01/14 : taking max year for each PIN
						" and e.year = ( select max(year) from AssetMonitoringFactDataYearAgg agg where agg.serialNumber = e.serialNumber ) ";
	
	
				String groupByQuery= " group by b.childId ";
	
	
	
				if(userTenancyType.equalsIgnoreCase(Zonal))
				{
					basicQueryString = basicQueryString + " and b.parentId ="+tenancyId;
				}//Keerthi : 22/10/13 : Fetching zone details of only logged-in user
				else if(userTenancyType.equalsIgnoreCase(Regional))
				{
					basicQueryString = basicQueryString + " and a.parentTenancyId ="+tenancyId;
				}
	
				basicQueryString = basicQueryString + groupByQuery;*/
	
				//DF20160726 @Roopa Query change for stock summary service
	
				String basicQueryString =null;
	
				String groupByQuery =null;
	
				if(userTenancyType.equalsIgnoreCase(Zonal)) //RO Role
				{
	
					basicQueryString ="select at.tenancy_id, t.tenancy_name, t.parent_tenancy_id, t.parent_tenancy_name, count(*) as machine_count" +
							" from asset a," +
							" (SELECT account_to_id ad FROM partnership where partner_id=1 and account_from_id="+account.getAccount_id()+") b," +
							" account_tenancy at, tenancy t" +
							" where a.primary_owner_id=b.ad" +
							" and at.account_id=b.ad" +
							" and t.tenancy_id=at.tenancy_id";
	
	
				}
				//DF20170803: KO369761 Adding JCB Regional tenancy type
				else if(userTenancyType.equalsIgnoreCase(Regional)) //HO Role
				{
					basicQueryString ="select at.tenancy_id, t.tenancy_name, t.parent_tenancy_id, t.parent_tenancy_name, count(*) as machine_count" +
							" from asset a," +
							" (select p.account_to_id ad from partnership p join partnership p1 where p.account_from_id = p1.account_to_id and p1.account_from_id="+account.getAccount_id()+
							" and p.partner_id = 1) b," +
							" account_tenancy at, tenancy t" +
							" where a.primary_owner_id=b.ad" +
							" and at.account_id=b.ad" +
							" and t.tenancy_id=at.tenancy_id";
				}
				else if(userTenancyType.equalsIgnoreCase(Global)) //Admin
				{
					basicQueryString ="select at.tenancy_id, t.tenancy_name, t.parent_tenancy_id, t.parent_tenancy_name, count(*) as machine_count" +
							" from asset a," +
							" (SELECT account_to_id ad FROM partnership where partner_id=1) b," +
							" account_tenancy at, tenancy t" +
							" where a.primary_owner_id=b.ad" +
							" and at.account_id=b.ad" +
							" and t.tenancy_id=at.tenancy_id";	
				}
	
				groupByQuery =" group by at.tenancy_id";
	
				basicQueryString = basicQueryString + groupByQuery;
	
				//System.out.println("Stock Summary final query::"+basicQueryString);
	
				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				rs = statement.executeQuery(basicQueryString);
	
	
	
				HashMap<Integer, StockSummaryImpl> zonalCkeckMap = new HashMap<Integer, StockSummaryImpl>();
	
	
				while(rs.next())
				{
	
					if(zonalCkeckMap.containsKey(rs.getInt("parent_tenancy_id")))
					{
	
						StockSummaryImpl StockSummaryImplData = zonalCkeckMap.get(rs.getInt("parent_tenancy_id"));
						HashMap<Integer, HashMap<String,Long>> dealerIdNameCount = StockSummaryImplData.getDealerIdNameCountMap();
	
						long newZonalMachineCount = StockSummaryImplData.getZonalMachineCount();
						newZonalMachineCount= newZonalMachineCount+rs.getLong("machine_count");
						StockSummaryImplData.setZonalMachineCount(newZonalMachineCount);
	
						HashMap<String,Long> dealerNameCount = new HashMap<String,Long>();
						dealerNameCount.put(rs.getString("tenancy_name"), rs.getLong("machine_count"));
	
						dealerIdNameCount.put(rs.getInt("tenancy_id"), dealerNameCount);
	
						StockSummaryImplData.setDealerIdNameCountMap(dealerIdNameCount);
						zonalCkeckMap.put(rs.getInt("parent_tenancy_id"), StockSummaryImplData);
	
					}
	
					else
					{
						StockSummaryImpl stockSummary = new StockSummaryImpl();
						stockSummary.setZonalTenancyId(rs.getInt("parent_tenancy_id"));
						stockSummary.setZonalTenancyName(rs.getString("parent_tenancy_name"));
						stockSummary.setZonalMachineCount(rs.getLong("machine_count"));
	
						HashMap<Integer, HashMap<String,Long>> dealerIdNameCount = new HashMap<Integer, HashMap<String,Long>>();
	
						HashMap<String,Long> dealerNameCount = new HashMap<String,Long>();
	
						dealerNameCount.put(rs.getString("tenancy_name"), rs.getLong("machine_count"));
						dealerIdNameCount.put(rs.getInt("tenancy_id"), dealerNameCount);
						stockSummary.setDealerIdNameCountMap(dealerIdNameCount);
	
						zonalCkeckMap.put(rs.getInt("parent_tenancy_id"), stockSummary);
	
					}
	
	
				}
	
	
				for(int i=0; i<zonalCkeckMap.size(); i++)
				{
					StockSummaryImpl response = (StockSummaryImpl) zonalCkeckMap.values().toArray()[i];
					responseList.add(response);
				}
	
	
			}
	
			catch(CustomFault e)
			{
				//e.printStackTrace();
				bLogger.error("Custom Fault: "+ e.getFaultInfo());
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
			return responseList;
		}
		//******************************************* END of Stock Summary Details *************************************************************
	
	
		//*************************************************** Asset Sale from Dealer to Customer *******************************************
	
		//*************************************************** Set Asset Sale from Dealer to Customer *******************************************
		/** DefectId:839 - Rajani Nagaraju - 20131128 - To enable Machine Movement between tenancies (This supports D2C, C2C, D2D and C2D)
		 * This method updates the asset ownership from Seller to Buyer and ties the Buyer to the given Dealer
		 * @param dealerCode dealerCode as String input - Dealer who will be the further Service contact for the machine
		 * @param sellerCode Seller Code as String input - Current owner of the Machine who is selling the Machine
		 * @param buyerCode Buyer Code as String input - Buyer who is buying the specified machine
		 * @param serialNumber VIN - Machine which is getting transferred
		 * @param transferDate Date of transfer of the machine
		 * @param messageId MessageID of the received message to append to error if raised
		 * @return Returns status String
		 */
		public String assetSaleFromDealerToCustomer(String sellerCode, String buyerCode, String dealerCode, String serialNumber, String transferDate, String messageId,String saleDate)
		{
			String status= "SUCCESS-Record Processed";
	
	
	
			//		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	
			Session session = HibernateUtil.getSessionFactory().openSession();
			Transaction tx = null;
	
			Logger iLogger = InfoLoggerClass.logger;
			iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber +"start of assetSaleFromDealerToCustomer");
	
			iLogger.info("SaleFromDealerToCustomer:::"+"sellerCode:" + sellerCode + "buyerCode:"+buyerCode + "dealerCode:"+dealerCode + "serialNumber:"+serialNumber + "transferDate:"+transferDate);
	
	
			//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
	
			if ((session == null) || (!session.isOpen())) {
				session = HibernateUtil.getSessionFactory().openSession();
	
			}
			if ((session.getTransaction().isActive()) && (session.isDirty()))
			{
				iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber +"Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
			tx = session.beginTransaction();
	
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			//Logger iLogger = InfoLoggerClass.logger;
			try
			{
			    	boolean c2cSale =false;
				AccountEntity accountEntity =null; //New account to be inserted to asset_owner and the primary_owner_id of the machine
				List<AccountEntity> sellerAccEntList = new LinkedList<AccountEntity>(); 
				List<AccountEntity> buyerAccEntList = new LinkedList<AccountEntity>();
				List<Integer> sellerAccIDList = new LinkedList<Integer>();
	
				Properties prop = new Properties();
				String dealerAccountType=null;
				String custAccountType=null;
	
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				dealerAccountType= prop.getProperty("DealerAccount");
				custAccountType = prop.getProperty("CustomerAccount");
	
				String sellerAccountType =null;
				String buyerAccountType =null;
	
	
				//------------------------Step1: validate serial number of the machine
				Query query = session.createQuery("from AssetEntity where serial_number= '"+serialNumber+"' ");
				Iterator itr = query.list().iterator();
				AssetEntity asset = null;
	
				while(itr.hasNext())
				{
					asset = (AssetEntity) itr.next();
				} 
				System.out.print(asset);
	
				if(asset==null)
				{
					//2014-06-18 : Machine Number check for 7 digits - Deepthi
					/*if(serialNumber.trim().length() >7)
					{
								serialNumber = serialNumber.substring(serialNumber.length()-7 , serialNumber.length());
					}*/
	
					//DF20140715 - Rajani Nagaraju - Remove Preceeding zeros in Machine Number
					serialNumber=serialNumber.replaceFirst("^0+(?!$)", "");
					Query qryMacNo =session.createQuery("from AssetEntity a where a.machineNumber='"+serialNumber+"'");
					Iterator itrMacNo=qryMacNo.list().iterator();
					while(itrMacNo.hasNext())
					{
						asset= (AssetEntity) itrMacNo.next();			
						serialNumber= asset.getSerial_number().getSerialNumber();
					}
	
	
				}
				if(asset==null)
				{
					AssetControlUnitEntity assetControl=null;
	
					Query assetControlQ = session.createQuery(" from AssetControlUnitEntity where serialNumber like '%"+serialNumber+"%'");
					Iterator assetControlItr = assetControlQ.list().iterator();
					while(assetControlItr.hasNext())
					{
						assetControl= (AssetControlUnitEntity)assetControlItr.next();
					}
	
					if(assetControl==null)
					{
						status = "FAILURE-PIN Registration Data not received";
						bLogger.error("EA Processing: SaleFromD2C: "+messageId+" : PIN Registration Data not received");
						return status;
					}
					else
					{
						status = "FAILURE-Roll off Data not received";
						bLogger.error("EA Processing: SaleFromD2C: "+messageId+" : Roll off Data not received");
						return status;
					}
	
	
				}
				/*Shajesh : 202010507 : Update Sale_date colum in asset table when AssetSaleFromD2C Processed
				String vin = asset.getSerial_number().getSerialNumber();
				String response = updateSaleDateForVin(vin,transferDate);
				iLogger.info("SaleDate "+transferDate+ "successfully update for the VIN "+vin + "::: response"+response);*/
	
	
				//2014-06-18 : Machine Number check for 7 digits - Deepthi
				//------------------------ Step2: Validate the TransferDate
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date vinTransferDate=null;
				String transferDateInString =null;
				try
				{
					vinTransferDate = dateFormat.parse(transferDate);
					transferDateInString = dateFormat.format(vinTransferDate);
				}
	
				catch(Exception e)
				{
					status = "FAILURE-"+e.getMessage();
					bLogger.error("EA Processing: SaleFromD2C: "+messageId+" : Date Parse Exception for TransferDate"+e);
	
	
					return status;
				}
	
				//DF20140804 - Rajani Nagaraju - If Trasnfer Date > latest trasfer date present for the machine, Consider Transfer Date as Last Trasfer Date+1
				Date latestOwnershipDate=null;
	
				Query assetOwnQuery = session.createQuery(" select ownershipStartDate from AssetAccountMapping where serialNumber='"+asset.getSerial_number().getSerialNumber()+"' order by ownershipStartDate desc ");
				Iterator assetOwnItr = assetOwnQuery.list().iterator();
				if(assetOwnItr.hasNext())
				{
					latestOwnershipDate=(Date)assetOwnItr.next();
				}
				if(latestOwnershipDate!=null)
				{
					if(vinTransferDate.before(latestOwnershipDate))
					{
						asset.setTransferDate(new Timestamp(vinTransferDate.getTime()));
						session.update(asset);
	
						vinTransferDate=new Date(latestOwnershipDate.getTime() + (1000 * 60 * 60 * 24));
						transferDateInString = dateFormat.format(vinTransferDate);
	
	
					}
				}
				//DF20140804 - Rajani Nagaraju - End
	
				//----------------------------- Step3: Validate Dealer Code - Also Dealer AccountType
				//get the Account details of dealer
	
				AccountEntity dealerAccount =null;
				//DF20190313:mani:to pass account id instead of account code for checking in partnership
				//int dealerAccId=0;// LLOPS - 182.o
				List <Integer> dealerAccId=new LinkedList<>();// LLOPS - 182.n
				Query dealerQuery = session.createQuery("from AccountEntity where status=true and accountCode='"+dealerCode+"'");
				Iterator dealerItr = dealerQuery.list().iterator();
				while(dealerItr.hasNext())
				{
					dealerAccount = (AccountEntity)dealerItr.next();
					//DF20190313:mani:to pass account id instead of account code for checking in partnership
					//dealerAccId=dealerAccount.getAccount_id();//LLOPS-182.o
					dealerAccId.add(dealerAccount.getAccount_id());//LLOPS-182.n
				}
				 String dealerAccIdList = dealerAccId.stream()
						    .map(id -> "'" + id + "'")
						    .collect(Collectors.joining(","));// LLOPS - 182.n
				if(dealerAccount==null)
				{
					throw new CustomFault("Dealer Master not received for the Dealer Code: "+dealerCode);
				}
	
				iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber + "dealerAccountID:"+dealerAccount.getAccount_id());
				//Get the Account type of Dealer
				Query dealerAccTypeQuery = session.createQuery(" from PartnershipMapping where accountToId='"+dealerAccount.getAccount_id()+"'");
				Iterator dealerAccTypeItr = dealerAccTypeQuery.list().iterator();
				while(dealerAccTypeItr.hasNext())
				{
					PartnershipMapping  partnership = (PartnershipMapping)dealerAccTypeItr.next();
					String actualDealerAccType = partnership.getPartnerId().getReversePartnerRole();
	
					iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber + "dealerAccountType:"+actualDealerAccType);
	
					if(!(actualDealerAccType.equalsIgnoreCase(dealerAccountType)))
					{
						throw new CustomFault("DelaerCode specified: "+dealerCode+" is not of Dealer Account Type");
					}
				}
	
				//-------------------------------- Step4: Validate Buyer Account and set Buyer Account Type
				//get the Account Details of Buyer
				iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber + "buyerCode:"+buyerCode);
				//DF20190313:mani:to pass account id instead of account code for checking in partnership
				int buyerAccId=0;
				Query query1 = session.createQuery("from AccountEntity where status=true and accountCode='"+buyerCode+"'");
				Iterator itr1 = query1.list().iterator();
				while(itr1.hasNext())
				{
					AccountEntity saleToAccEntity = (AccountEntity) itr1.next();
					buyerAccEntList.add(saleToAccEntity);
					//DF20190313:mani:to pass account id instead of account code for checking in partnership
					buyerAccId=saleToAccEntity.getAccount_id();
				}
	
				for(int i=0;i<buyerAccEntList.size();i++){
					iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber + "buyerAccount:"+i+":"+buyerAccEntList.get(i).getAccount_id());
				}
	
				//DF20190313:mani:for partnership table, accountFromId and accountToId are accountid's not accountcodes 
				//because of which every time a sale is happened to an existing customer the account details are getting updated.
	
				/*Query PartnershipMappingQry = session.createQuery(" from PartnershipMapping where  accountFromId = '"+dealerCode+"' and " +
						" accountToId='"+buyerCode+"' " );*/
				/*Query PartnershipMappingQry = session.createQuery(" from PartnershipMapping where  accountFromId = '"+dealerAccId+"' and " +
						" accountToId='"+buyerAccId+"' " );*/
	
				//DF20201111 : Zakir :  Updating PartnershipMappingQuery, since the old query was not completely working in certain scenarios
//				Query PartnershipMappingQry = session.createQuery(" from PartnershipMapping a, AccountEntity b  where a.accountToId=b.account_id and b.accountCode = '"
//						+buyerCode+"' and a.accountFromId='"+dealerAccId+"' " );// LLOPS - 182.o
//	
				Query PartnershipMappingQry = session.createQuery(" from PartnershipMapping a, AccountEntity b  where a.accountToId=b.account_id and b.accountCode = '"
						+buyerCode+"' and a.accountFromId='"+dealerAccIdList+"' " );// LLOPS - 182.n
	
				Iterator PartnershipMappingItr = PartnershipMappingQry.list().iterator();
	
				tx.commit();
	
				if(session!=null && session.isOpen()){
	
					session.close();
				}
	
				if(buyerAccEntList.isEmpty() ||(!PartnershipMappingItr.hasNext()) )
				{ 
					//20140609 - Rajani Nagaraju - Create Customer Account if the buyer is a customer and Customer data is received from EA
					//				Keerthi : 2017-02-01 : dealer code should be taken from SAP file instead from the customer master
					//AccountEntity custAccEntity = createCustomerAcc_New(buyerCode,dealerCode,messageId);
					//TaskID 1481 - JCB4908 - Rajani Nagaraju - contact creation failure - New Implementation
					AccountEntity custAccEntity = new CustomerAccCreationBO().createCustomer(buyerCode,dealerCode,messageId,serialNumber,sellerCode);
					
					if(custAccEntity==null)
					{
						throw new CustomFault("Master Data not received for the Buyer Code: "+buyerCode+" ");
					}
	
					//else
					{
						buyerAccEntList.add(custAccEntity);
	
						for(int i=0;i<buyerAccEntList.size();i++){
							iLogger.info("SaleFromDealerToCustomer:::After Customer creation"+"serialNumber:"+serialNumber + "buyerAccount:"+i+":"+buyerAccEntList.get(i).getAccount_id());
						}
					}
	
				}
				//CR431.sn
				else {
					
					 PreparedStatement selectStmt = null;
					    ResultSet resultSet = null;
					    Connection prodConnection = null;
					    Statement statement = null;
					    PreparedStatement updateStmt=null;
					    
					    int totalRowsAffected =0;

					    try {
					        ConnectMySQL connMySql = new ConnectMySQL();
					        prodConnection = connMySql.getConnection();
					        statement = prodConnection.createStatement();

					        int accountID = buyerAccEntList.get(0).getAccount_id(); 
					        String selectContactQuery = "SELECT ac.Contact_ID " +
					                                    "FROM account_contact ac " +
					                                    "JOIN account a ON ac.Account_ID = a.Account_ID " +
					                                    "JOIN contact c ON ac.contact_id = c.contact_id " +
					                                    "WHERE a.Account_ID = ? AND c.status = 1 AND a.status = 1 " +
					                                    "ORDER BY a.account_id DESC";
					        
					        selectStmt = prodConnection.prepareStatement(selectContactQuery);
					        selectStmt.setInt(1, accountID);  // Set the accountID parameter
					        resultSet = selectStmt.executeQuery();
					        
					        String contactID = null;
					        if (resultSet.next()) {
					            contactID = resultSet.getString("Contact_ID");
					            iLogger.info("Contact_ID found for account ID " + accountID + ": " + contactID);
					        }

					        // Fetch AssetID based on buyerCode
					        String selectQuery = "SELECT AssetID FROM MAlertSubsriberGroup WHERE AssetID IN " +
					                             "(SELECT aos.Serial_Number FROM asset_owner_snapshot aos " +
					                             "JOIN account a ON aos.Account_ID = a.Account_ID WHERE a.Account_Code = ?)";
					        List<String> assetIDs = new ArrayList<>();

					        selectStmt = prodConnection.prepareStatement(selectQuery);
					        selectStmt.setString(1, buyerCode);
					        resultSet = selectStmt.executeQuery();

					        while (resultSet.next()) {
					            String assetID = resultSet.getString("AssetID");
					            assetIDs.add(assetID);
					            iLogger.info("AssetID found: " + assetID);
					        }
					        iLogger.info("Total AssetIDs found: " + assetIDs.size());
					        
					        // Update SubscriberGroup for each assetID
					        for (String assetID : assetIDs) {
					            
					            String selectSubscriber3Query = "SELECT JSON_UNQUOTE(JSON_EXTRACT(SubscriberGroup, '$.Subscriber3')) AS Subscriber3 " +
					                                           "FROM MAlertSubsriberGroup " +
					                                           "WHERE AssetID = ?";
					            selectStmt = prodConnection.prepareStatement(selectSubscriber3Query);
					            selectStmt.setString(1, assetID);
					            resultSet = selectStmt.executeQuery();

					            String subscriber3 = null;
					            if (resultSet.next()) {
					                subscriber3 = resultSet.getString("Subscriber3");
					                iLogger.info("Subscriber3 found for AssetID " + assetID + ": " + subscriber3);
					            }

					            // Step 3.2: Convert Subscriber3 JSON to Map and extract SMS1 and EMAIL1
					            if (subscriber3 != null) {
					                try {
					                    ObjectMapper objectMapper = new ObjectMapper();
					                    Map<String, Object> subscriber3Map = objectMapper.readValue(subscriber3, Map.class);

					                    // Extract SMS1 and EMAIL1 values from the Map
					                    String sms1 = (String) subscriber3Map.get("SMS1");
					                    String email1 = (String) subscriber3Map.get("EMAIL1");
					                    String whatsApp1 = (String) subscriber3Map.get("WHATSAPP1");//CR500.n

					                    iLogger.info("SMS1: " + sms1);
					                    iLogger.info("EMAIL1: " + email1);
					                    iLogger.info("WHATSAPP1: " + whatsApp1);//CR500.n

					                    if (contactID != null) {
					                    	  String updateQuery = "UPDATE wise.MAlertSubsriberGroup " +
				                                         "SET SubscriberGroup = JSON_SET(SubscriberGroup, '$.Subscriber3', " +
				                                         "'{\"SMS2\": \"" + contactID + "\", \"EMAIL2\": \"" + contactID 
				                                         + "\", \"WHATSAPP2\": \"" + contactID + "\""; //CR500.n

						                    if (sms1 != null) {
						                        updateQuery += ", \"SMS1\": \"" + sms1 + "\"";
						                    }
						                    else
						                    {
						                    	updateQuery += ", \"SMS1\": \"" + null + "\"";
						                    }

						                    if (email1 != null) {
						                        updateQuery += ", \"EMAIL1\": \"" + email1 + "\"}'";
						                    }else {
						                        updateQuery += ", \"EMAIL1\": \"" + null + "\"}'";

						                    }
						                    if (whatsApp1 != null) {
						                        updateQuery += ", \"WHATSAPP1\": \"" + whatsApp1 + "\"}'";
						                    }else {
						                        updateQuery += ", \"WHATSAPP1\": \"" + null + "\"}'";

						                    }

						                    // Complete the WHERE clause
						                    updateQuery += ") WHERE AssetID = '"+assetID+"'";

					                        updateStmt = prodConnection.prepareStatement(updateQuery);
					                       // updateStmt.setString(1, assetI);
					                        iLogger.info(updateQuery);
					                        System.out.print(updateQuery);
					                        int rowsAffected = updateStmt.executeUpdate();
					                        totalRowsAffected += rowsAffected;
					                        iLogger.info("Updated SubscriberGroup for AssetID: " + assetID + ", rows affected: " + rowsAffected);
					                    }
					                } catch (IOException e) {
					                    fLogger.error("Error converting Subscriber3 JSON to Map: " + e.getMessage());
					                }
					            }   
					        }
					        iLogger.info("Total rows affected: " + totalRowsAffected);
					        System.out.println("MAlertSubscriberGroup:Subscriber3 is updated Successfully!!");
					        System.out.println("MAlertSubscriberGroup:Subscriber3 is updated Successfully!!");
					        
					    } catch (SQLException e) {
					        e.printStackTrace();
					    } finally {
					        try {
					            if (resultSet != null) resultSet.close();
					            if (selectStmt != null) selectStmt.close();
					            if (statement != null) statement.close();
					            if (prodConnection != null) prodConnection.close();
					        } catch (SQLException e) {
					            e.printStackTrace();
					        }
					    }

					
				}//CR431.en
				
				
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().openSession();
					if(session.getTransaction().isActive() && session.isDirty())
					{
						iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber +"Opening a new session");
						session = HibernateUtil.getSessionFactory().openSession();
					}
				}
				tx= session.beginTransaction();
				/* tx.begin();
				 iLogger.info("Step -Error --");
				 */
				//Get the AccountType of Buyer
				Query accountTypeQuery = session.createQuery(" from PartnershipMapping where accountToId='"+buyerAccEntList.get(0).getAccount_id()+"'");
				Iterator accountTypeItr = accountTypeQuery.list().iterator();
				//iLogger.info("Step -Error After--");
				while(accountTypeItr.hasNext())
				{
					PartnershipMapping  partnership = (PartnershipMapping)accountTypeItr.next();
					buyerAccountType = partnership.getPartnerId().getReversePartnerRole();
				}
	
	
				//----------------------------------- Step5: Set sellerCode if SellerCode is not specified
				//SellerCode can be NULL only for D2C
				if(sellerCode==null)
				{
					sellerCode = dealerCode;
				}
	
				iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber + "sellerCode:"+sellerCode);
	
				//-------------------------------- Step6: Validate Seller Account and set Seller Account Type
				//get the Account Details of Seller
	
	
				iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber + "sellerCode:"+sellerCode);
	
				Query query2 = session.createQuery("from AccountEntity where status=true and accountCode='"+sellerCode+"'");
				Iterator itr2 = query2.list().iterator();
				while(itr2.hasNext())
				{
					AccountEntity saleFromAccEntity = (AccountEntity) itr2.next();
					sellerAccEntList.add(saleFromAccEntity);
					sellerAccIDList.add(saleFromAccEntity.getAccount_id());
				}
	
				tx.commit();
	
				for(int i=0;i<sellerAccIDList.size();i++){
					iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber + "sellerAccount:"+i+":"+sellerAccIDList.get(i));
				}
	
				if(sellerAccEntList.isEmpty())
				{
					//20140609 - Rajani Nagaraju - Create Customer Account if the seller is a customer and Customer data is received from EA
					//				Keerthi : 2017-02-01 : dealer code should be taken from SAP file instead from the customer master
					//AccountEntity custAccEntity = createCustomerAcc_New(sellerCode,dealerCode,messageId);
					//TaskID 1481 - JCB4908 - Rajani Nagaraju - contact creation failure - New Implementation
					AccountEntity custAccEntity = new CustomerAccCreationBO().createCustomer(buyerCode,dealerCode,messageId,serialNumber,sellerCode);
					
					if(custAccEntity==null)
					{
						throw new CustomFault("Master Data not received for the Seller Code: "+sellerCode+" ");
					}
	
					else
					{
						sellerAccEntList.add(custAccEntity);
						//System.out.println("custAccEntity.getAccount_id():"+custAccEntity.getAccount_id());
						sellerAccIDList.add(custAccEntity.getAccount_id());
					}
	
					for(int i=0;i<sellerAccIDList.size();i++){
						iLogger.info("SaleFromDealerToCustomer:::After new sellerAccount creation"+"serialNumber:"+serialNumber + "sellerAccount:"+i+":"+sellerAccIDList.get(i));
					}	
				}
	
	
	
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().openSession();
					if(session.getTransaction().isActive() && session.isDirty())
					{
						iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber +"Opening a new session");
						session = HibernateUtil.getSessionFactory().openSession();
					}
	
				}
				tx=session.beginTransaction();
	
				//Get the AccountType of Seller
				Query sellerAccTypeQuery = session.createQuery(" from PartnershipMapping where accountToId='"+sellerAccEntList.get(0).getAccount_id()+"'");
				Iterator sellerAccTypeItr = sellerAccTypeQuery.list().iterator();
				while(sellerAccTypeItr.hasNext())
				{
					PartnershipMapping  partnership = (PartnershipMapping)sellerAccTypeItr.next();
					sellerAccountType = partnership.getPartnerId().getReversePartnerRole();
				}
	
				iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber + "sellerAccountType:"+sellerAccountType);
	
				//------------------------------------- Step7: Validate the Ownership of the Machine with Seller
	
	
				if(! (sellerAccIDList.contains(asset.getPrimary_owner_id())) )
				{
	
					iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber + "sellerAccID"+sellerAccIDList.get(0)+" is not equal to asset primary owner::"+asset.getPrimary_owner_id());
	
					throw new CustomFault("Seller: "+sellerCode+" is not the Current Owner of the Machine");
				}
				iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber +"dealerAccountType :" + sellerAccountType.equalsIgnoreCase(dealerAccountType));
				//-------------------------------------- Step8: Change the Machine Ownership
				//----- Case1: Machine Sale from Dealer to Dealer (D2D)
				if( (sellerAccountType.equalsIgnoreCase(dealerAccountType)) && (buyerAccountType.equalsIgnoreCase(dealerAccountType)) )
				{
					//Task1: Add the record that specifies the new Owner in AssetOwner table
					//Task2: Update primary_owner_id in Asset table
					accountEntity = buyerAccEntList.get(0); //Get(0) because of the Assumption that Dealer cannot be distributed accross the zone unless
					//two different dealer account exists (with different Dealer Code)
	
					iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber+"Machine Sale from Dealer to Dealer"+sellerAccountType+buyerAccountType+accountEntity.getAccount_id());
				}
	
	
	
				//------- Case 2: Sale from Customer to Dealer (C2D)
				else if( (sellerAccountType.equalsIgnoreCase(custAccountType)) && (buyerAccountType.equalsIgnoreCase(dealerAccountType)) )
				{
	
					//Task1: Add the record that specifies the new Owner in AssetOwner table
					//Task2: Update primary_owner_id in Asset table
					accountEntity = buyerAccEntList.get(0); //Get(0) because of the Assumption that Dealer cannot be distributed accross the zone unless
					//two different dealer account exists (with different Dealer Code)
	
					iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber+"Sale from Customer to Dealer"+sellerAccountType+buyerAccountType+accountEntity.getAccount_id());
				}
	
	
				//----------------------------------------- Step9: Change the Machine Ownership and build new Hierarchy structure(Pseudo Customer Accounts) if required
				//------------------------ Case 3: Machine Sale from Dealer to Customer (D2C) OR Machine Sale from Customer to Customer (C2C)
				else if( ( (sellerAccountType.equalsIgnoreCase(dealerAccountType)) && (buyerAccountType.equalsIgnoreCase(custAccountType)) ) ||
						( (sellerAccountType.equalsIgnoreCase(custAccountType)) && (buyerAccountType.equalsIgnoreCase(custAccountType)) ) )
				{
				    //CR482.sn
				    if ((sellerAccountType.equalsIgnoreCase(custAccountType)) && (buyerAccountType.equalsIgnoreCase(custAccountType))){
					c2cSale = true;
				    }//CR482.en
				    
					//Get the Partnership id for Dealer to Customer
					PartnerRoleEntity partnerRole =null;
					Query partnerQuery = session.createQuery("from PartnerRoleEntity where partnerRole='"+dealerAccountType+"' and " +
							" reversePartnerRole='"+custAccountType+"' ");
					Iterator partnerItr = partnerQuery.list().iterator();
					while(partnerItr.hasNext())
					{
						partnerRole = (PartnerRoleEntity)partnerItr.next();
					}
	
					AccountEntity actualBuyerAcc = null;
					//validate the existence of customer account under the given dealer
					for(int i=0; i<buyerAccEntList.size(); i++)
					{
						if(buyerAccEntList.get(i).getParent_account_id().getAccount_id()==dealerAccount.getAccount_id())
						{
							actualBuyerAcc = buyerAccEntList.get(i);
						}
					}
	
					if(actualBuyerAcc==null)
					{
	
						//Customer Account doesn't exists under the given dealer, In this case create a pseudo account for the customer under the given dealer
	
						//Get the Tenancy details of the Dealer Account - To set the parent details of Pseudo Tenancy
						//If the Dealer information is not available in the LL(Dealer Tenancy), then the sale details will not be processed. Put it to Error queue - which would be reprocessed in the next scheduled job
						Query parentTenQuery = session.createQuery(" from AccountTenancyMapping where account_id='"+dealerAccount.getAccount_id()+"'");
						Iterator parentTenItr = parentTenQuery.list().iterator();
						TenancyEntity dealerTen =null;
						while(parentTenItr.hasNext())
						{
							AccountTenancyMapping accTen = (AccountTenancyMapping) parentTenItr.next();
							dealerTen = accTen.getTenancy_id();
						}
	
						if(dealerTen==null)
						{
							throw new CustomFault("Dealer OrgGroup details for "+dealerAccount+" not available in LL, hence rejecting the packet");
						}
	
						//Create a pseudo customer account; buyerAccEntList.get(0) is considered as reference because ideally other than account id, rest all parameters should be alike for all the entities under buyerAccEntList
						//DF20150408 - Rajani Nagaraju - Introducing sleep time for random seconds(between 1 and 5) to ensure no duplicate Customer acc/ten will be created
						//(Especially if sale details for different machines for the same customer under same dealer is received in same file - and customer acc/ten does not exists,
						//In that case, since each record will be processed as parallel thread from Q object there might be a possibility of duplicate acc/ten creation) 
						Random r = new Random();
						//int i1 = r.nextInt(max - min + 1) + min;
						int delayPeriod = r.nextInt(5 - 1 + 1) + 1;
						int waitTimeInMilliSec = delayPeriod*1000;
						Thread.currentThread().sleep(waitTimeInMilliSec);
	
						AccountEntity pseudoCustAccount = new AccountEntity();
						pseudoCustAccount.setAccount_name(buyerAccEntList.get(0).getAccount_name());
						pseudoCustAccount.setAccountCode(buyerCode);
						pseudoCustAccount.setAddressId(buyerAccEntList.get(0).getAddressId());
						pseudoCustAccount.setClient_id(buyerAccEntList.get(0).getClient_id());
						pseudoCustAccount.setDescription(buyerAccEntList.get(0).getDescription());
						pseudoCustAccount.setEmailId(buyerAccEntList.get(0).getEmailId());
						pseudoCustAccount.setFax(buyerAccEntList.get(0).getFax());
						pseudoCustAccount.setMobile_no(buyerAccEntList.get(0).getMobile_no());
						pseudoCustAccount.setNo_of_employees(buyerAccEntList.get(0).getNo_of_employees());
						pseudoCustAccount.setParent_account_id(dealerAccount);
						pseudoCustAccount.setPhone_no(buyerAccEntList.get(0).getPhone_no());
						pseudoCustAccount.setStatus(buyerAccEntList.get(0).isStatus());
						pseudoCustAccount.setYear_started(buyerAccEntList.get(0).getYear_started());
						//DF20190517 :Abhishek: inserting countrycode and mapping code in account table
						pseudoCustAccount.setCountryCode(buyerAccEntList.get(0).getCountryCode());
						pseudoCustAccount.setMappingCode(buyerAccEntList.get(0).getMappingCode());					
	
						//DF20190312 :mani: account creation or updation tracebility
						Date currentDate = new Date();
						Timestamp currentTimeStamp = new Timestamp(currentDate.getTime());	
						pseudoCustAccount.setCreatedOn(currentTimeStamp);
						pseudoCustAccount.setUpdatedOn(currentTimeStamp);
						session.beginTransaction();
						session.save(pseudoCustAccount);
	
	
						//Step2: Update Partnership table
						PartnershipMapping newPartnerMapping = new PartnershipMapping();
						newPartnerMapping.setAccountFromId(dealerAccount);
						newPartnerMapping.setAccountToId(pseudoCustAccount);
						newPartnerMapping.setPartnerId(partnerRole);
						session.save(newPartnerMapping);
	
						//Step3:
						//Task1: Add the record that specifies the new pseudo customer account in AssetOwner table
						//Task2: Update primary_owner_id in Asset table
						accountEntity = pseudoCustAccount;
	
						//Step4: Create a pseudoTenancy for the new pseudoAccount, if tenancy exists for any one of the buyerAccEntList.
						//If the tenancy does not exists for any member of buyerAccEntList, during the tenancy creation of any first member of buyerAccEntList will be responsible for creating all other pseudo tenancies
						TenancyEntity buyerTenancy =null;
						Query tenancyQuery = session.createQuery(" from AccountTenancyMapping where account_id in (:list)").setParameterList("list", buyerAccEntList);
						Iterator tenancyItr = tenancyQuery.list().iterator();
						while(tenancyItr.hasNext())
						{
							AccountTenancyMapping accountTenancy = (AccountTenancyMapping)tenancyItr.next();
							buyerTenancy = (TenancyEntity)accountTenancy.getTenancy_id();
						}
						if(buyerTenancy!=null)
						{
	
							//Only if the Dealer Tenancy exists, then create the Child Pseudo tenancy
							if(dealerTen!=null)
							{
								//DefectId:20150911 @Suprava TenancyName creation by appending AccountCode
								//Task1: Create a pseudo Tenancy
								TenancyEntity pseudoCustTenancy = new TenancyEntity();
								pseudoCustTenancy.setTenancy_name(buyerTenancy.getTenancy_name());
								//End DefectId:20150911
								pseudoCustTenancy.setParent_tenancy_name(dealerTen.getTenancy_name());
								pseudoCustTenancy.setParent_tenancy_id(dealerTen);
								pseudoCustTenancy.setClient_id(buyerTenancy.getClient_id());
								pseudoCustTenancy.setTenancy_type_id(buyerTenancy.getTenancy_type_id());
								pseudoCustTenancy.setOperating_Start_Time(buyerTenancy.getOperating_Start_Time());
								pseudoCustTenancy.setOperating_End_Time(buyerTenancy.getOperating_End_Time());
								pseudoCustTenancy.setCreatedBy(buyerTenancy.getCreatedBy());
								//Created Date will alone be the actual date of creation. Rest all will be the same details
								pseudoCustTenancy.setCreatedDate(new Timestamp((new Date()).getTime()));
								pseudoCustTenancy.setTenancyCode(buyerTenancy.getTenancyCode());
								session.save(pseudoCustTenancy); 
	
								//Task2: Connect newly created pseudoAccount and pseudoTenancy
								AccountTenancyMapping pseudoAccTen = new AccountTenancyMapping();
								pseudoAccTen.setAccount_id(pseudoCustAccount);
								pseudoAccTen.setTenancy_id(pseudoCustTenancy);
	
	
								session.save(pseudoAccTen);
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
	
	
	
					else
					{
						//Task1: Add the record that specifies the new pseudo customer account in AssetOwner table
						//Task2: Update primary_owner_id in Asset table
						accountEntity = actualBuyerAcc;
					}
	
	
					iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber+"Machine Sale from Dealer to Customer (D2C) OR Machine Sale from Customer to Customer (C2C)"+sellerAccountType+buyerAccountType+accountEntity.getAccount_id());
					/*Shajesh : 20220104 : Update Sale_date colum in asset table only when AssetSaleFromD2C Processed*/
					//if(saleDate != null)//CR482.o-Sale date should not get updated in case of c2c sale
					if(saleDate != null && !c2cSale)//CR482.n
					{
					    String vin = asset.getSerial_number().getSerialNumber();
					    String response = updateSaleDateForVin(vin,saleDate);
					    iLogger.info("SaleDate "+transferDate+ "successfully update for the VIN "+vin + "::: response  ::: "+response);
					}
				}
	
				//----------------------------------------- Step 10: Perform Machine movement 
				//insert the sale record details into asset_owners
	
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					if(session.getTransaction().isActive() && session.isDirty())
					{
						iLogger.info("Opening a new session");
						session = HibernateUtil.getSessionFactory().openSession();
					}
	
				}
				tx=session.beginTransaction();
				Query query3 = session.createQuery(" from AssetAccountMapping where serialNumber='"+asset.getSerial_number().getSerialNumber()+"'" +
						" order by ownershipStartDate");
				Iterator itr3 = query3.list().iterator();
				int previousAssetOwnerId =0;
				while(itr3.hasNext())
				{
					AssetAccountMapping assetAccount = (AssetAccountMapping) itr3.next();
					previousAssetOwnerId = assetAccount.getAccountId().getAccount_id();
				}
	
				iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber+"previousAssetOwnerId:"+previousAssetOwnerId+"Current Id:"+accountEntity.getAccount_id());
	
				if(accountEntity.getAccount_id()!=previousAssetOwnerId)
				{
					//Check for the primary key constraint
					Query query4 = session.createQuery(" from AssetAccountMapping where serialNumber='"+asset.getSerial_number().getSerialNumber()+"' and " +
							" accountId="+accountEntity.getAccount_id()+" and ownershipStartDate='"+transferDateInString+"' ");
					System.out.print(query4);
					Iterator itr4 = query4.list().iterator();
					boolean duplicate = false;
					System.out.print(duplicate);
					while(itr4.hasNext())
					{
						AssetAccountMapping assetAcc = (AssetAccountMapping) itr4.next();
						duplicate = true;
					}
	
					if(duplicate)
					{
	
						iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber+"" +"Duplicate sale details received"+accountEntity.getAccount_id()+":"+previousAssetOwnerId);
	
						throw new CustomFault("Duplicate SaleDetails received for the VIN: "+serialNumber+" to the buyer: "+buyerCode);
					}
	
					AssetAccountMapping newAssetOwner = new AssetAccountMapping();
					newAssetOwner.setAccountId(accountEntity);
					newAssetOwner.setSerialNumber(asset);
	
					newAssetOwner.setOwnershipStartDate(vinTransferDate);
					session.beginTransaction();
					session.save(newAssetOwner);
	
					//Update Primary_owner_id in asset to reflect the current owner
					/*asset.setPrimary_owner_id(accountEntity.getAccount_id());				
					session.update(asset);*/						
					asset.setPrimary_owner_id(accountEntity.getAccount_id());
					//Shajesh : 2022018 : SaleDate Update issue in asset table - For primary Owner change
					if(saleDate != null){
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						Date date = df.parse(saleDate);
				        long time = date.getTime();
						Timestamp timestamp = new Timestamp(time);
						asset.setSale_Date(timestamp);						
					}
					session.update(asset);
	
					iLogger.info("SaleFromDealerToCustomer:::"+"serialNumber:"+serialNumber+"current account id and previous account id in assetowners are not same so inserting asset owners and updating asset:"+accountEntity.getAccount_id()+":"+previousAssetOwnerId);
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
	
	
	
	
				//DefectId:839 - Rajani Nagaraju - 20131226 - To enable Machine Movement between tenancies
				//--------------------------------------------Step 11: If the Machine Movement is from C2C OR C2D OR C2C, then 
				// 1. remove the association of the machine to MachineGroup
				// 2. remove the association of the machine to Landmark
				// 3. If service schedule is defined for the machine, then update the DealerId for the remaining services to be done.
	
				if(! (session.isOpen() ))
				{
					session = HibernateUtil.getSessionFactory().openSession();
					if(session.getTransaction().isActive() && session.isDirty())
					{
						iLogger.info("Opening a new session");
						session = HibernateUtil.getSessionFactory().openSession();
					}
					tx=session.beginTransaction();
				}
				if( ( (sellerAccountType.equalsIgnoreCase(dealerAccountType)) && (buyerAccountType.equalsIgnoreCase(dealerAccountType)) ) ||
						( (sellerAccountType.equalsIgnoreCase(custAccountType)) && (buyerAccountType.equalsIgnoreCase(dealerAccountType)) )	 ||
						( (sellerAccountType.equalsIgnoreCase(custAccountType)) && (buyerAccountType.equalsIgnoreCase(custAccountType)) ) 
						)
				{
					//Task1: remove the association of the machine to MachineGroup
					Query mgQuery = session.createQuery(" delete from AssetCustomGroupMapping where serial_number='"+asset.getSerial_number().getSerialNumber()+"'");
					int rowsAffected = mgQuery.executeUpdate();
	
					//Task2: remove the association of the machine to Landmark
					Query landmarkQuery = session.createQuery(" delete from LandmarkAssetEntity where Serial_number='"+asset.getSerial_number().getSerialNumber()+"'");
					int rowsDeleted = landmarkQuery.executeUpdate();
	
					//Task3: If service schedule is defined for the machine, then update the DealerId for the remaining services to be done.
					Query serschQuery = session.createQuery(" select a from AssetServiceScheduleEntity a, ServiceScheduleEntity b " +
							" where a.serviceScheduleId = b. serviceScheduleId and a.serialNumber='"+asset.getSerial_number().getSerialNumber()+"' " +
							" and b.dbmsPartCode NOT IN ( select c.dbmsPartCode from ServiceHistoryEntity c  where " +
							" c.serialNumber='"+asset.getSerial_number().getSerialNumber()+"')");
					Iterator serSchItr = serschQuery.list().iterator();
					while(serSchItr.hasNext())
					{
						AssetServiceScheduleEntity assetSerSch = (AssetServiceScheduleEntity)serSchItr.next();
						assetSerSch.setDealerId(dealerAccount);
						session.update(assetSerSch);
					}
	
				}
	
				//DefectId:20140214 Primary_dealer will transfer based upon transfer date @Suprava
				//Task4: Update asset_service_schedule table with new DealerId 
				Query query5 = session.createQuery(" from AssetServiceScheduleEntity where scheduledDate>='"+transferDate+"' and serialNumber='"+serialNumber+"'");
				Iterator itr5 = query5.list().iterator();
				while(itr5.hasNext())
				{
					AssetServiceScheduleEntity AssetServiceSchedule = (AssetServiceScheduleEntity)itr5.next();
					AssetServiceSchedule.setDealerId(dealerAccount);
					session.update(AssetServiceSchedule);
				}
	
	
				//DF20150107 - Rajani Nagaraju - Adding Receiver2 for SMS subscription as Customer User during SaleFromD2C
				//If it is a sale from Dealer to Customer:
				// 1.Add any TA of customer as Receiver2 for SMS subscription 
				// 2.Receiver1 remains the same(Ideally should be the DealerUser if the dealer has set the same)
				// 3.Existing Receiver3(which can be dealeruser) has to be removed
				//DF20180301 - KO369761 - below block commented because we are not using event_subscription table.
				/*if( (sellerAccountType.equalsIgnoreCase(dealerAccountType)) && (buyerAccountType.equalsIgnoreCase(custAccountType)) )
		        {
		        	//Delete the SMS subscription record for Receiver2 and Receiver3 for the given machine
		        	Query eventSubsQ = session.createQuery(" delete from EventSubscriptionMapping where serialNumber='"+serialNumber+"'" +
		        			" and priority in (2,3)");
		        	eventSubsQ.executeUpdate();
	
		        	//Set Receiver2 as Customer user
		        	Query receiver2Q = session.createQuery(" select b from AccountContactMapping a , ContactEntity b, AccountEntity c " +
		        			" where a.contact_id=b.contact_id and a.account_id=c.account_id and c.status=true and c.accountCode='"+buyerCode+"' " +
		        					" and b.is_tenancy_admin=1 ");
		        	Iterator receiver2Itr = receiver2Q.list().iterator();
		        	ContactEntity receiver2 = null;
		        	if(receiver2Itr.hasNext())
		        	{
		        		receiver2 = (ContactEntity)receiver2Itr.next();
		        	}
		        	if(receiver2!=null)
		        	{
		        		EventSubscriptionMapping eventSubObj = new EventSubscriptionMapping();
		        		eventSubObj.setContactId(receiver2);
		        		eventSubObj.setPriority(2);
		        		eventSubObj.setSerialNumber(asset);
		        		session.save(eventSubObj);
	
		        	}
	
		        }*/
	
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
	
			catch(CustomFault e)
			{
				status = "FAILURE-"+e.getFaultInfo();
				bLogger.error("EA Processing: SaleFromD2C: "+messageId+" : "+e.getFaultInfo());
			}
	
	
			catch(Exception e)
			{
				status = "FAILURE-"+e.getMessage();
				fLogger.fatal("EA Processing: SaleFromD2C: "+messageId+ " Fatal Exception :"+e);
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
		//*************************************************** END of Asset Sale from Dealer to Customer *******************************************
	
		/*Shajesh : 202010507 : Update Sale_date colum in asset table when AssetSaleFromD2C Processed*/
		private String updateSaleDateForVin(String vin, String transferDate) 
		{
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			/*ConnectMySQL connFactory = new ConnectMySQL();
			String response = "Success";
			String updateQuery = "update asset set sale_Date="
					+ "'" + transferDate + "'" + " where Serial_Number=" + "'"
					+ vin + "'";
			iLogger.info("updateQueryForSaleD2C : "+updateQuery);
			
			
			try (Connection connection = connFactory.getConnection();
					PreparedStatement statement = connection
							.prepareStatement(updateQuery);){
				statement.executeUpdate(updateQuery);
			} catch (Exception e) {
				response = "failure";
				fLogger.fatal("updateSaleDateForVin()::issue while updating DB "
						+ e.getMessage());
			}*/
			
			//TaskID 1481 - JCB4908 - Rajani Nagaraju - Connection not getting closed in above code
			String response = "Success";
			
			Connection con=null;
			Statement stmt=null;
			
			try
			{
				con = new ConnectMySQL().getConnection();
				stmt = con.createStatement();
				
				String query = "update asset set sale_Date='"+transferDate+"' where serial_number='"+vin+"'";
				iLogger.info("EA EAProcessing:AssetDetailsBO:updateSaleDateForVin: Update Sale Date Query: "+query);
				int row =stmt.executeUpdate(query);
				iLogger.info("AssetDetails BO: updateSaleDateForVin :: Row getting updated in executeUpdate :: row "+vin +" "+row);
				
				
			}
			catch(Exception e)
			{
				response = "failure";
				fLogger.fatal("EAProcessing:AssetDetailsBO:updateSaleDateForVin: Exception :"+e.getMessage(),e);
			}
			finally
			{
				try
				{
					if(stmt!=null)
						stmt.close();
					if(con!=null)
						con.close();
					
				}
				catch(Exception e)
				{
					fLogger.fatal("EA EAProcessing:AssetDetailsBO:updateSaleDateForVin: Exception in closing the MySQL connection:"+e.getMessage(),e);
				}
			}
			
			return response;
		}
		/*private static String updateSaleDateForVin(String vin, String transferDate) 
		{
			Logger fLogger = FatalLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			Connection con=null;
			Statement stmt=null;
			String response = "Success";
			try
			{
				ConnectMySQL connFactory = new ConnectMySQL();
				con = connFactory.getConnection();
				String dbUrl  = "jdbc:mysql://10.179.12.74:3306/wise";
				Class.forName("com.mysql.jdbc.Driver");
				con=DriverManager.getConnection(dbUrl, "root", "admin");
				stmt = con.createStatement();				
				String query = "update asset set sale_Date='"+transferDate+"' where serial_number='"+vin+"'";
				iLogger.info("EA EAProcessing:AssetDetailsBO:updateSaleDateForVin: Update Sale Date Query: "+query);
				int row =stmt.executeUpdate(query);
				iLogger.info("AssetDetails BO: updateSaleDateForVin :: Row getting updated in executeUpdate :: row "+vin +" "+row);
				
				
			}
			catch(Exception e)
			{
				response = "failure";
				fLogger.info("EAProcessing:AssetDetailsBO:updateSaleDateForVin: Exception :"+e.getMessage(),e);
			}			
			finally
			{
				try
				{
					if(stmt!=null)
						stmt.close();
					if(con!=null)
						con.close();
					
				}
				catch(Exception e)
				{
					fLogger.fatal("EA EAProcessing:AssetDetailsBO:updateSaleDateForVin: Exception in closing the MySQL connection:"+e.getMessage(),e);
				}
			}
			
			return response;
		}*/
		/*
		/** This method updates the asset owner from dealer to specified customer
		 * @param dealerCode dealerCode as String input
		 * @param customerCode customerCode as String input
		 * @param machineNumber Engine number of the machine
		 * @return Returns status String
		 *//*
		 *
		 *
		// Defect I D: Asset Owner changes - Rajani - 2013-07-19
		public String assetSaleFromDealerToCustomer(String dealerCode, String customerCode, String machineNumber)
		{
			String status= "SUCCESS";
	
			Logger fatalError = Logger.getLogger("fatalErrorLogger");
	
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
	
	        	//validate machine number
				Query query = session.createQuery("from AssetEntity where nick_name= '"+machineNumber+"' and active_status=true and client_id="+clientEntity.getClient_id()+"");
				Iterator itr = query.list().iterator();
				AssetEntity asset = null;
				AccountEntity accountEntity =null;
	
				while(itr.hasNext())
				{
					asset = (AssetEntity) itr.next();
	
				} 
	
				if(asset==null)
				{
					return "FAILURE";
				}
	
	
				//get the Customer Account for the given customer code
				Query query1 = session.createQuery("from AccountEntity where accountCode='"+customerCode+"' and status=true and client_id="+clientEntity.getClient_id()+"");
				Iterator itr1 = query1.list().iterator();
				while(itr1.hasNext())
				{
					accountEntity = (AccountEntity) itr1.next();
	
				}
	
				if(accountEntity ==null)
				{
					return "FAILURE";
				}
	
	
				//insert the sale record details into asset_owners
				Query query3 = session.createQuery(" from AssetAccountMapping where serialNumber='"+asset.getSerial_number().getSerialNumber()+"'");
				Iterator itr3 = query3.list().iterator();
				int previousAssetOwnerId =0;
				while(itr3.hasNext())
				{
					AssetAccountMapping assetAccount = (AssetAccountMapping) itr3.next();
					previousAssetOwnerId = assetAccount.getAccountId().getAccount_id();
				}
	
				if(accountEntity.getAccount_id()!=previousAssetOwnerId)
				{
					//get the current Date - For purchase date
					Date currentDate = new Date();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					String currentDateInString = dateFormat.format(currentDate);
	
					//Check for the primary key constraint
					Query query4 = session.createQuery(" from AssetAccountMapping where serialNumber='"+asset.getSerial_number().getSerialNumber()+"' and " +
							" accountId="+accountEntity.getAccount_id()+" and ownershipStartDate='"+currentDateInString+"' ");
					Iterator itr4 = query4.list().iterator();
					boolean duplicate = false;
					while(itr4.hasNext())
					{
						AssetAccountMapping assetAcc = (AssetAccountMapping) itr4.next();
						duplicate = true;
					}
	
					if(duplicate)
					{
						return "FAILURE";
					}
	
					AssetAccountMapping newAssetOwner = new AssetAccountMapping();
					newAssetOwner.setAccountId(accountEntity);
					newAssetOwner.setSerialNumber(asset);
	
					newAssetOwner.setOwnershipStartDate(currentDate);
					session.save(newAssetOwner);
	
					//Update Primary_owner_id in asset to reflect the current owner
					asset.setPrimary_owner_id(accountEntity.getAccount_id());
					session.update(asset);
				}
	
	        }
	
	        catch(Exception e)
			{
				status = "FAILURE";
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
	
			return status;
	
		}
		//*************************************************** END of Asset Sale from Dealer to Customer *******************************************
		  */
		/**
		 * 
		 * @param serialNumberList
		 * @return assetEntityList
		 * @throws IOException 
		 */
		public List<AssetEntity> assetEntityList (List<String> serialNumberList) throws IOException
		{
			//Logger fLogger = FatalLoggerClass.logger;
			//Logger bLogger = BusinessErrorLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			iLogger.info("serialNumberList size"+serialNumberList.size());
	
	
			List<AssetEntity> assetEntityList = new LinkedList<AssetEntity>();
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
				List<AssetControlUnitEntity> assetControlUnitEntityList = new LinkedList<AssetControlUnitEntity>();
	
				Query asset = session.createQuery("from AssetControlUnitEntity where serialNumber in (:list)").setParameterList("list", serialNumberList);
				Iterator it = asset.list().iterator();
				while(it.hasNext())
				{
					AssetControlUnitEntity assetControlUnitEntity = (AssetControlUnitEntity) it.next();			
					assetControlUnitEntityList.add(assetControlUnitEntity);
				}
				iLogger.info("assetControlUnitEntityList size"+assetControlUnitEntityList.size());
				Query assetData = session.createQuery("from AssetEntity where active_status=true and client_id="+clientEntity.getClient_id()+" and serial_number in (:list1)").setParameterList("list1", assetControlUnitEntityList);
				Iterator itr = assetData.list().iterator();
				while(itr.hasNext())
				{
					AssetEntity assetEntity = (AssetEntity) itr.next();			
					assetEntityList.add(assetEntity);
				}
			}
			finally
	
			{
				if(session.isOpen()){
	
					if(session.getTransaction().isActive()){
						session.getTransaction().commit();
					}
					if(session.isOpen())
					{
						session.flush();
						session.close();
					}
	
				}
			}
			iLogger.info("assetEntityList size"+assetEntityList.size());
			return assetEntityList;
		}
	
	
	
	
	
		/** Method to record asset gate-out details - Sale to dealer
		 * @param machineNumber,accountCode
		 * @return String
		 * @throws CustomFault
		 */
	
	
		//Defect  ID : Asset Owner changes - Rajani Nagaraju - 2013-07-19
		/*	public String setAssetGateoutDetails(String machineNumber, String accountCode)
		{
			String status ="SUCCESS";
	
			Logger businessError = Logger.getLogger("businessErrorLogger");
			Logger fatalError = Logger.getLogger("fatalErrorLogger");
	
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
	
	        	AccountEntity accountEntity= null;	
	        	AssetEntity assetEntity = null; 
	
				//----- Validate Asset through given machineNumber
	        	Query q2 = session.createQuery("from AssetEntity where nick_name='"+machineNumber+"' and active_status=true and client_id="+clientEntity.getClient_id()+"");
				Iterator iterator = q2.list().iterator();			
				while(iterator.hasNext())
				{	
					assetEntity = (AssetEntity)iterator.next();			
				}
	
				if(assetEntity==null)
				{
					throw new CustomFault("Asset Entity is null..");
				}
	
	
				//------- Validate Account through given accountCode
				Query query = session.createQuery("from AccountEntity where accountCode='"+accountCode+"' and status=true and client_id="+clientEntity.getClient_id()+"");
				Iterator itr = query.list().iterator();
				while(itr.hasNext())
				{	
					accountEntity = (AccountEntity)itr.next();				
				}
	
				if(accountEntity==null)
				{
					throw new CustomFault("Invalid Account");
				}
	
				//--------- Map the asset to given Dealer Account
				Query typequery= session.createQuery("from AssetAccountMapping where serialNumber = '"+assetEntity.getSerial_number().getSerialNumber()+"' and accountId="+accountEntity.getAccount_id());
				Iterator itr1=typequery.list().iterator();
				int flag=0;
				while(itr1.hasNext())
				{
					AssetAccountMapping ownerEntity = (AssetAccountMapping)itr1.next();
					flag=1;
				}
	
				if(flag==0)
				{
					AssetAccountMapping ownerObj = new AssetAccountMapping();
					ownerObj.setSerialNumber(assetEntity);
					ownerObj.setAccountId(accountEntity);
	
					//get the current Date
					Date currentDate = new Date();
					ownerObj.setOwnershipStartDate(currentDate);
					session.save(ownerObj);
	
					//Update the owner in Asset Table
					assetEntity.setPrimary_owner_id(accountEntity.getAccount_id());
					session.update(assetEntity);
				}
	        }
	
	        catch(CustomFault e)
			{
	        	status = "FAILURE";
	        	businessError.error("Custom Fault: "+ e.getFaultInfo());
			}
	
	        catch(Exception e)
			{
	        	status = "FAILURE";
	        	fatalError.fatal("Exception :"+e);
			}
	
	        finally
	        {
	        	if(session.getTransaction().isActive()){
	                  session.getTransaction().commit();
	            }
	            if(session.isOpen()){
	                  session.flush();
	                  session.close();
	            } 
	        }
	
			return status;
		} */
	
	
	
		//Defect  ID : Asset Owner changes - Rajani Nagaraju - 2013-07-19
		/** DefectId:839 - Rajani Nagaraju - 20131128 - To enable Machine Movement between tenancies  
		 * Method to record asset gate-out details - Sale to dealer/ Direct sale to customer
		 * @param dealerCode DealerCode
		 * @param customerCode CustomerCode if it is a direct sale to Customer
		 * @param engineNumber engineNumber of the Asset
		 * @param serialNumber VIN - Optional parameter
		 * @return Returns the status String
		 */
		//public String setAssetGateoutDetails(String dealerCode, String customerCode, String engineNumber, String serialNumber, String messageId, String gateoutDateString)//JCB6341.o
		public String setAssetGateoutDetails(String dealerCode, String customerCode, String engineNumber, String serialNumber, String messageId, String gateoutDateString)//JCB6341.n
		{
			String status ="SUCCESS-Record Processed";
	
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
	
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
				AccountEntity dealerEntity= null;	
				AssetEntity assetEntity = null; 
				List<AccountEntity> custAccEntList = new LinkedList<AccountEntity>();
	
				Properties prop = new Properties();
				String dealerAccountType=null;
				String custAccountType=null;
	
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				dealerAccountType= prop.getProperty("DealerAccount");
				custAccountType = prop.getProperty("CustomerAccount");
	
				// ------------------------Step1: Validate Asset through given machineNumber
				//DF20141209 - Rajani Nagaraju - Removing getting asset based on Engine Number
				/*Query q2 = session.createQuery("from AssetEntity where nick_name='"+engineNumber+"'");
				Iterator iterator = q2.list().iterator();			
				while(iterator.hasNext())
				{	
					assetEntity = (AssetEntity)iterator.next();			
				}*/
	
				Query q2 = session.createQuery("from AssetEntity where serial_number='"+serialNumber+"'");
				Iterator iterator = q2.list().iterator();			
				while(iterator.hasNext())
				{	
					assetEntity = (AssetEntity)iterator.next();			
				}
	
				if(assetEntity==null)
				{
					String machineNumber=serialNumber.replaceFirst("^0+(?!$)", "");
					Query machNumQ = session.createQuery(" from AssetEntity where machineNumber='"+machineNumber+"'");
					Iterator machNumItr = machNumQ.list().iterator();
					while(machNumItr.hasNext())
					{
						assetEntity = (AssetEntity) machNumItr.next();
						serialNumber=assetEntity.getSerial_number().getSerialNumber();
					}
				}
	
				if(assetEntity==null)
				{
					AssetControlUnitEntity assetControl=null;
					Query assetControlQ = session.createQuery(" from AssetControlUnitEntity where serialNumber='"+serialNumber+"'");
					Iterator assetControlItr = assetControlQ.list().iterator();
					while(assetControlItr.hasNext())
					{
						assetControl= (AssetControlUnitEntity)assetControlItr.next();
					}
	
					if(assetControl==null)
					{
						status = "FAILURE-PIN Registration Data not received";
						bLogger.error("EA Processing: AssetGateOut: "+messageId+" : PIN Registration Data not received");
						return status;
					}
					else
					{
						status = "FAILURE-Roll off Data not received for the given Engine Number";
						bLogger.error("EA Processing: AssetGateOut: "+messageId+" : Roll off Data not received for the given Engine Number");
						return status;
					}
	
	
				}
	
	
				// ---------------------------Step2: Validate Dealer Account through given dealer Code and also the Dealer Account Type
				TenancyBO tenancyBoObj = new TenancyBO();
				dealerEntity = tenancyBoObj.getAccountCodeObj(dealerCode);
	
				iLogger.info("AssetGateOutLog: "+serialNumber+": dealer Account:"+dealerEntity);
	
				if(dealerEntity==null)
				{
					throw new CustomFault("Dealer Master not received for Dealer Code: "+dealerCode);
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
	
				//Get the Account type of Dealer
				Query dealerAccTypeQuery = session.createQuery(" from PartnershipMapping where accountToId='"+dealerEntity.getAccount_id()+"'");
				Iterator dealerAccTypeItr = dealerAccTypeQuery.list().iterator();
				while(dealerAccTypeItr.hasNext())
				{
					PartnershipMapping  partnership = (PartnershipMapping)dealerAccTypeItr.next();
					String actualDealerAccType = partnership.getPartnerId().getReversePartnerRole();
	
					iLogger.info("AssetGateOutLog: "+serialNumber+": actualDealerAccType:"+actualDealerAccType);
	
					//DF20160808 @Roopa if dealer account matches exiting the loop, not checking for next record
					if(actualDealerAccType.equalsIgnoreCase(dealerAccountType))
					{
						break;
					}
					if(!(actualDealerAccType.equalsIgnoreCase(dealerAccountType)))
					{
						throw new CustomFault("DelaerCode:"+ dealerCode+ " is not of Dealer Account Type");
					}
				}
	
	
				//--------------------------- Step3: Validate Customer Account through given customer Code and also the Customer Acc Type, in case of direct sale
				if(customerCode!=null)
				{
	
					iLogger.info("AssetGateOutLog: "+serialNumber+": direct sale to the customerCode:"+customerCode);
	
	
					//get the Account Details of Customer
					Query custAccQuery = session.createQuery("from AccountEntity where status=true and accountCode='"+customerCode+"'");
					Iterator custAccItr = custAccQuery.list().iterator();
					while(custAccItr.hasNext())
					{
						AccountEntity customerAccEntity = (AccountEntity) custAccItr.next();
						custAccEntList.add(customerAccEntity);
					}
	
					iLogger.info("AssetGateOutLog: "+serialNumber+": check customer account present for the given custCode:"+custAccEntList);	 
	
					if(custAccEntList==null || custAccEntList.isEmpty())
					{
	
						iLogger.info("AssetGateOutLog: "+serialNumber+": customer account is not exist:So calling createCustomerAcc for the custcode:"+customerCode);	 
	
						//20140606 - Rajani Nagaraju - Create Customer Account if the Customer data is received from EA
						iLogger.info("AssetGateOutLog: "+serialNumber+"Inside first if case");
						//					Keerthi : 2017-02-01 : dealer code should be taken from SAP file instead from the customer master
						//AccountEntity custAccEntity = createCustomerAcc(customerCode,dealerCode,messageId);
						//TaskID 1481 - JCB4908 - Rajani Nagaraju - contact creation failure - New Implementation
						AccountEntity custAccEntity = new CustomerAccCreationBO().createCustomer(customerCode,dealerCode,messageId,serialNumber,dealerCode);
						
						iLogger.info("AssetGateOutLog: "+serialNumber+": after calling createCustomerAcc:"+custAccEntity);	 
	
						//System.out.println("custAccEntity*****"+custAccEntity);
						if(custAccEntity==null)
						{
							iLogger.info("AssetGateOutLog: "+serialNumber+"Inside first if case again custAccEntity is null");
							//DefectId:20141211 @Suprava Nayak Primary Dealer code Not Present in customerInfo String Changes
							String customerCode1=null;
							String customerName=null;
							String addressLine1=null;
							String addressLine2=null;
							String city=null;
							String zipCode=null;
							String state=null;
							String zone=null;
							String country=null;
							String email=null;
							String contactNumber=null;
							String fax=null;
							String primaryDealerCode=null;
							String customerDetails1=null;
							StringBuffer customerDetails = new StringBuffer();
	
							if(session==null || ! (session.isOpen() ))
							{
								session = HibernateUtil.getSessionFactory().getCurrentSession();
								//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
								if(session.getTransaction().isActive() && session.isDirty())
								{
									iLogger.info("AssetGateOutLog: "+serialNumber+":Opening a new session");
									session = HibernateUtil.getSessionFactory().openSession();
								}
								session.getTransaction().begin();
							}
	
							Query custMasterQuery = session.createQuery(" from CustomerMasterEntity where customerCode='"+customerCode+"'");
							Iterator custMasterItr = custMasterQuery.list().iterator();
	
							while(custMasterItr.hasNext())
							{
								CustomerMasterEntity custMaster = (CustomerMasterEntity)custMasterItr.next();
	
								if(custMaster.getCustomerdetail()!=null)
								{
									String[] msgString = custMaster.getCustomerdetail().split("\\|");
	
									if(msgString.length>0)
										customerCode1=msgString[0];
									//	System.out.println("customerCode1"+customerCode1);
									if(msgString.length>1)
										customerName=msgString[1];
									//System.out.println("customerName"+customerName);
									if(msgString.length>2)
										addressLine1=msgString[2];
									//System.out.println("addressLine1"+addressLine1);
									if(msgString.length>3)
										addressLine2=msgString[3];
									//System.out.println("addressLine2"+addressLine2);
									if(msgString.length>4)
										city=msgString[4];
									//System.out.println("city"+city);
									if(msgString.length>5)
										zipCode=msgString[5];
									//System.out.println("zipCode"+zipCode);
									if(msgString.length>6)
										state=msgString[6];
									//System.out.println("state"+state);
									if(msgString.length>7)
										zone=msgString[7];
									//System.out.println("zone"+zone);
									if(msgString.length>8)
										country=msgString[8];
									//System.out.println("country"+country);
									if(msgString.length>9)
										email=msgString[9];
									//System.out.println("email"+email);
									if(msgString.length>10)
										contactNumber=msgString[10];
									//System.out.println("contactNumber"+contactNumber);
									if(msgString.length>11)
										fax=msgString[11];
									//System.out.println("fax"+fax);
									if(msgString.length>12)
										primaryDealerCode=msgString[12];
									//System.out.println("primaryDealerCode"+primaryDealerCode);
									if(primaryDealerCode==null ||primaryDealerCode.isEmpty())
									{
										primaryDealerCode=dealerCode;
										//System.out.println("dealerCode=primaryDealerCode"+primaryDealerCode);
									}
								}
								customerDetails.append(customerCode1).append("|").append(customerName).append("|").append(addressLine1).append("|")
								.append(addressLine2).append("|").append(city).append("|").append(zipCode).append("|").append(state).append("|").append(zone)
								.append("|").append(country).append("|").append(email).append("|").append(contactNumber).append("|").append(fax).append("|")
								.append(primaryDealerCode);
	
								customerDetails1=customerDetails.toString();
								iLogger.info("AssetGateOutLog: "+serialNumber+":customerDetails1:"+customerDetails1);
							}
							if(session==null || ! (session.isOpen() ))
							{
								session = HibernateUtil.getSessionFactory().getCurrentSession();
								//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
								if(session.getTransaction().isActive() && session.isDirty())
								{
									iLogger.info("AssetGateOutLog: "+serialNumber+":Opening a new session");
									session = HibernateUtil.getSessionFactory().openSession();
								}
								session.getTransaction().begin();
							}
	
							Query query = session.createQuery("from CustomerMasterEntity where customerCode='"+customerCode+"'");
							Iterator itr = query.list().iterator();
							CustomerMasterEntity CustomerMasterObj= null;
							while(itr.hasNext())
							{
								//System.out.println("customerDetails1"+customerDetails1);
								CustomerMasterObj = (CustomerMasterEntity)itr.next();
								CustomerMasterObj.setCustomerdetail(customerDetails1);
								CustomerMasterObj.setCustomerCode(customerCode);
								session.update(CustomerMasterObj);
								iLogger.info("AssetGateOutLog: "+serialNumber+":CustomerMaster Table with "+customerCode+" Updated Successfully");
							}
							if(session==null || ! (session.isOpen() ))
							{
								session = HibernateUtil.getSessionFactory().getCurrentSession();
								//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
								if(session.getTransaction().isActive() && session.isDirty())
								{
									iLogger.info("AssetGateOutLog: "+serialNumber+":Opening a new session");
									session = HibernateUtil.getSessionFactory().openSession();
								}
								session.getTransaction().begin();
							}
	
							iLogger.info("AssetGateOutLog: "+serialNumber+"Before calling createCustomerAcc for the second case");
							//						Keerthi : 2017-02-01 : dealer code should be taken from SAP file instead from the customer master
							//AccountEntity custAccEntity1 = createCustomerAcc(customerCode,dealerCode,messageId);
							//TaskID 1481 - JCB4908 - Rajani Nagaraju - contact creation failure - New Implementation
							AccountEntity custAccEntity1 = new CustomerAccCreationBO().createCustomer(customerCode,dealerCode,messageId,serialNumber,dealerCode);
							
							iLogger.info("AssetGateOutLog: "+serialNumber+"After calling createCustomerAcc for the second case:"+custAccEntity1);
	
							if(custAccEntity1==null)
							{
								//System.out.println("Customer Master data not received for Customer Code: "+customerCode);
								throw new CustomFault("Customer Master data not received for Customer Code: "+customerCode);
							}
	
							else
							{
	
								iLogger.info("AssetGateOutLog: "+serialNumber+"Customer Account got craeted in the second case");
								custAccEntList.add(custAccEntity1);
							}
						}
	
						else
						{
	
							iLogger.info("AssetGateOutLog: "+serialNumber+"Customer Account got craeted in the first case");
							custAccEntList.add(custAccEntity);
						}
	
					}
					//20160819 - @suresh - added for the creation of partnership though is account is already created
					else{
	
						iLogger.info("AssetGateOutLog: "+serialNumber+"Customer Account is already present");
						//    				Keerthi : 2017-02-01 : dealer code should be taken from SAP file instead from the customer master
						//AccountEntity custAccEntity1 = createCustomerAcc(customerCode,dealerCode,messageId);
						//TaskID 1481 - JCB4908 - Rajani Nagaraju - contact creation failure - New Implementation
						AccountEntity custAccEntity1 = new CustomerAccCreationBO().createCustomer(customerCode,dealerCode,messageId,serialNumber,dealerCode);
					}
	
	
	
					if(session==null || ! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
						if(session.getTransaction().isActive() && session.isDirty())
						{
							iLogger.info("AssetGateOutLog: "+serialNumber+":Opening a new session");
							session = HibernateUtil.getSessionFactory().openSession();
						}
						session.getTransaction().begin();
					}
	
					//Validate the AccountType of Customer
					Query custAccTypeQuery = session.createQuery(" from PartnershipMapping where accountToId='"+custAccEntList.get(0).getAccount_id()+"'");
					Iterator custAccTypeItr = custAccTypeQuery.list().iterator();
					while(custAccTypeItr.hasNext())
					{
						PartnershipMapping  partnership = (PartnershipMapping)custAccTypeItr.next();
						String customerAccountType = partnership.getPartnerId().getReversePartnerRole();
	
						iLogger.info("AssetGateOutLog: "+serialNumber+":Validate the AccountType of Customer:"+customerAccountType);
	
	
						if(!(customerAccountType.equalsIgnoreCase(custAccountType)))
						{
							throw new CustomFault("Customer specified:"+customerCode+" is not of Customer Account Type");
						}
					}
	
				}
	
	
				//------------------ Step4: Case1:  Map the asset to given Dealer Account - If the GateOut is from JCB to Dealer
				//JCB6341.so
				//get the current Date
				//Date currentDate = new Date();
				//String currentDateInString = new SimpleDateFormat("yyyy-MM-dd").format(currentDate);
				//JCB6341.eo
				//JCB6341.sn
				Date gateoutDate = new SimpleDateFormat("yyyy-MM-dd").parse(gateoutDateString);
				//JCB6341.en
				iLogger.info("AssetGateOutLog: "+serialNumber+":custAccEntList:"+custAccEntList);
	
				if(custAccEntList==null || custAccEntList.isEmpty())
				{
	
					iLogger.info("AssetGateOutLog: "+serialNumber+":If the GateOut is from JCB to Dealer");
					int previousAssetOwnerId = assetEntity.getPrimary_owner_id();
	
					if(dealerEntity.getAccount_id() != previousAssetOwnerId)
					{
	
						//Check for the primary key constraint
						Query chkPrimaryKeyQuery = session.createQuery(" from AssetAccountMapping where serialNumber='"+assetEntity.getSerial_number().getSerialNumber()+"' and " +
								//" accountId="+dealerEntity.getAccount_id()+" and ownershipStartDate='"+currentDateString+"' ");//JCB6341.o
								" accountId="+dealerEntity.getAccount_id()+" and ownershipStartDate='"+gateoutDateString+"' ");//JCB6341.n
						Iterator chkPrimaryKeyItr = chkPrimaryKeyQuery.list().iterator();
						boolean duplicate = false;
						while(chkPrimaryKeyItr.hasNext())
						{
							AssetAccountMapping assetAcc = (AssetAccountMapping) chkPrimaryKeyItr.next();
							duplicate = true;
						}
	
						if(duplicate)
						{
							throw new CustomFault("Duplicate Gate Out Data Received for the VIN : "+serialNumber+" and DealerCode: "+dealerCode);
						}
	
	
						AssetAccountMapping ownerObj = new AssetAccountMapping();
						ownerObj.setSerialNumber(assetEntity);
						ownerObj.setAccountId(dealerEntity);
						//ownerObj.setOwnershipStartDate(currentDate);//JCB6341.o
						ownerObj.setOwnershipStartDate(gateoutDate);//JCB6341.n
						session.save(ownerObj);
	
						iLogger.info("AssetGateOutLog: "+serialNumber+":If the GateOut is from JCB to Dealer:Insert asset_owners table");
	
						//Update the owner in Asset Table
						assetEntity.setPrimary_owner_id(dealerEntity.getAccount_id());
						/*//DF20170803 : SU334449 - Updating the TimeZone with Dealer's Country TimeZone when gate-out takes place
						String countryCode = null;
						String timeZone = null;
						//Step 1: Fetching contactId and countryCode from account_contact based on the dealer account id
						String accntContactQ = ("from AccountContactMapping where account_id="+dealerEntity.getAccount_id());
						Query accntQuery = session.createQuery(accntContactQ);
						@SuppressWarnings("rawtypes")
						Iterator accntContactItr = accntQuery.list().iterator();
						if(accntContactItr.hasNext()){
							AccountContactMapping contactMap = (AccountContactMapping) accntContactItr.next();
							countryCode = contactMap.getContact_id().getCountryCode();
						}
						//Step 2: Fetching TimeZone from CountryCodes based on the countryCode
						if(!(countryCode==null||countryCode.isEmpty())){
							String timeZoneQ = ("from CountryCodesEntity where country_name='"+countryCode+"'");
							Query timeZoneQuery = session.createQuery(timeZoneQ);
							@SuppressWarnings("rawtypes")
							Iterator timeZoneItr = timeZoneQuery.list().iterator();
							if(timeZoneItr.hasNext()){
								CountryCodesEntity countryCodes = (CountryCodesEntity) timeZoneItr.next();
								timeZone = countryCodes.getTimeZone();
							}
						}
						if(timeZone != null){
						assetEntity.setTimeZone(timeZone);
						}*/
	
						//DF20170822 : KO369761: Updating the TimeZone with Dealer's Country TimeZone when gate-out takes place
						//DF20180502:KO369761 - If timezone & conuntry code null we are setting to default values.
						if(dealerEntity.getTimeZone() != null)
							assetEntity.setTimeZone(dealerEntity.getTimeZone());
						else
							assetEntity.setTimeZone("(GMT+05:30)");
	
						////DF20171218 : @Roopa: Updating the countrycode with Dealer's code 
						if(dealerEntity.getCountryCode() != null)
							assetEntity.setCountrycode(dealerEntity.getCountryCode());
						else
							assetEntity.setCountrycode("+91");
						session.update(assetEntity);
	
						iLogger.info("AssetGateOutLog: "+serialNumber+":If the GateOut is from JCB to Dealer:Update asset table");
					}
	
				}
	
				//--------------------- Step4: Case2: If it is a direct sale, Map the asset to given Customer Account 
				else
				{
					iLogger.info("AssetGateOutLog: "+serialNumber+":If the GateOut is direct sale");
	
					iLogger.info("AssetGateOutLog: "+serialNumber+":Check for the existence of the Customer account under the given dealer");
	
					//Check for the existence of the Customer account under the given dealer
					AccountEntity custOwnerAccEntity =null;
					AccountEntity actualBuyerAcc = null;
					for(int i=0; i<custAccEntList.size(); i++)
					{
						if(custAccEntList.get(i).getParent_account_id().getAccount_id()==dealerEntity.getAccount_id())
						{
							actualBuyerAcc = custAccEntList.get(i);
	
							iLogger.info("AssetGateOutLog: "+serialNumber+":actualBuyerAcc:"+actualBuyerAcc);
						}
					}
	
	
					if(actualBuyerAcc==null)
					{
	
						iLogger.info("AssetGateOutLog: "+serialNumber+":Customer Account doesn't exists under the given dealer:Create pseudo account");
	
						if(session==null || ! (session.isOpen() ))
						{
							session = HibernateUtil.getSessionFactory().getCurrentSession();
							if(session.getTransaction().isActive() && session.isDirty())
							{
								iLogger.info("AssetGateOutLog: "+serialNumber+":Opening a new session");
								session = HibernateUtil.getSessionFactory().openSession();
							}
							session.getTransaction().begin();
						}
	
	
						//Customer Account doesn't exists under the given dealer, In this case create a pseudo account for the customer under the given dealer
	
						//Get the Tenancy details of the Dealer Account - To set the parent details of Pseudo Tenancy
						//If the Dealer information is not available in the LL(Dealer Tenancy), then the sale details will not be processed. Put it to Error queue - which would be reprocessed in the next scheduled job
						Query parentTenQuery = session.createQuery(" from AccountTenancyMapping where account_id='"+dealerEntity.getAccount_id()+"'");
						Iterator parentTenItr = parentTenQuery.list().iterator();
						TenancyEntity dealerTen =null;
						while(parentTenItr.hasNext())
						{
							AccountTenancyMapping accTen = (AccountTenancyMapping) parentTenItr.next();
							dealerTen = accTen.getTenancy_id();
	
							iLogger.info("AssetGateOutLog: "+serialNumber+":dealerTen in case of creating pseudo account with the diff dealer:"+dealerTen);
						}
	
						if(dealerTen==null)
						{
							throw new CustomFault("Dealer OrgGroup details for"+dealerCode+" not available in LL, hence rejecting the packet for the VIN: "+serialNumber);
						}
	
	
	
						//Get the Partnership id for Dealer to Customer
						PartnerRoleEntity partnerRole =null;
						Query partnerQuery = session.createQuery("from PartnerRoleEntity where partnerRole='"+dealerAccountType+"' and " +
								" reversePartnerRole='"+custAccountType+"' ");
						Iterator partnerItr = partnerQuery.list().iterator();
						while(partnerItr.hasNext())
						{
							partnerRole = (PartnerRoleEntity)partnerItr.next();
	
							iLogger.info("AssetGateOutLog: "+serialNumber+":partnerRole:"+partnerRole.getPartnerId());
						}
	
						//Hibernate.initialize(dealerEntity);
	
						iLogger.info("AssetGateOutLog: "+serialNumber+":custAccEntList Account_name:"+custAccEntList.get(0).getAccount_name());
	
						iLogger.info("AssetGateOutLog: "+serialNumber+":dealerEntity accId:"+dealerEntity.getAccount_id());
	
						//Create a pseudo customer account; custAccEntList.get(0) is considered as reference because ideally other than account id, rest all parameters should be alike for all the entities under custAccEntList
						AccountEntity pseudoCustAccount = new AccountEntity();
						pseudoCustAccount.setAccount_name(custAccEntList.get(0).getAccount_name());
						pseudoCustAccount.setAccountCode(customerCode);
						//DF20190103-KO369761- Updating mapping code in pseudo account.
						pseudoCustAccount.setMappingCode(customerCode);
						pseudoCustAccount.setAddressId(custAccEntList.get(0).getAddressId());
						pseudoCustAccount.setClient_id(custAccEntList.get(0).getClient_id());
						pseudoCustAccount.setDescription(custAccEntList.get(0).getDescription());
						pseudoCustAccount.setEmailId(custAccEntList.get(0).getEmailId());
						pseudoCustAccount.setFax(custAccEntList.get(0).getFax());
						pseudoCustAccount.setMobile_no(custAccEntList.get(0).getMobile_no());
						pseudoCustAccount.setNo_of_employees(custAccEntList.get(0).getNo_of_employees());
						pseudoCustAccount.setParent_account_id(dealerEntity);
						pseudoCustAccount.setPhone_no(custAccEntList.get(0).getPhone_no());
						pseudoCustAccount.setStatus(custAccEntList.get(0).isStatus());
						pseudoCustAccount.setYear_started(custAccEntList.get(0).getYear_started());
						pseudoCustAccount.setTimeZone(custAccEntList.get(0).getTimeZone());
						pseudoCustAccount.setCountryCode(custAccEntList.get(0).getCountryCode());
						//DF20190312 :mani: account creation or updation tracebility
						Date currentDate1 = new Date();
						Timestamp currentTimeStamp = new Timestamp(currentDate1.getTime());
						pseudoCustAccount.setCreatedOn(currentTimeStamp);
						pseudoCustAccount.setUpdatedOn(currentTimeStamp);
	
						if(session==null || ! (session.isOpen() ))
						{
							session = HibernateUtil.getSessionFactory().getCurrentSession();
							if(session.getTransaction().isActive() && session.isDirty())
							{
								iLogger.info("AssetGateOutLog: "+serialNumber+":Opening a new session");
								session = HibernateUtil.getSessionFactory().openSession();
							}
							session.getTransaction().begin();
						}
	
						session.save(pseudoCustAccount);
	
						iLogger.info("AssetGateOutLog: "+serialNumber+":insert the record the account table:with pseudoCustAccount");
	
						//Update Partnership table
						PartnershipMapping newPartnerMapping = new PartnershipMapping();
						newPartnerMapping.setAccountFromId(dealerEntity);
						newPartnerMapping.setAccountToId(pseudoCustAccount);
						newPartnerMapping.setPartnerId(partnerRole);
						session.save(newPartnerMapping);
	
						iLogger.info("AssetGateOutLog: "+serialNumber+":update Partnership table");
	
						//Task1: Add the record that specifies the new pseudo customer account in AssetOwner table
						//Task2: Update primary_owner_id in Asset table
						custOwnerAccEntity = pseudoCustAccount;
	
						//Create a pseudoTenancy for the new pseudoAccount, if tenancy exists for any one of the custAccEntList.
						//If the tenancy does not exists for any member of custAccEntList, during the tenancy creation of any first member of custAccEntList will be responsible for creating all other pseudo tenancies
						TenancyEntity buyerTenancy =null;
						Query tenancyQuery = session.createQuery(" from AccountTenancyMapping where account_id in (:list)").setParameterList("list", custAccEntList);
						Iterator tenancyItr = tenancyQuery.list().iterator();
						while(tenancyItr.hasNext())
						{
							AccountTenancyMapping accountTenancy = (AccountTenancyMapping)tenancyItr.next();
							buyerTenancy = (TenancyEntity)accountTenancy.getTenancy_id();
	
							iLogger.info("AssetGateOutLog: "+serialNumber+":Existing cust tenancy:"+buyerTenancy.getTenancy_id());
						}
						if(buyerTenancy!=null)
						{
							//Only if the Dealer Tenancy exists, then create the Child Pseudo tenancy
							if(dealerTen!=null)
							{
								//DefectId:20150911 @Suprava TenancyName creation by appending AccountCode
								//Task1: Create a pseudo Tenancy
								TenancyEntity pseudoCustTenancy = new TenancyEntity();
								pseudoCustTenancy.setTenancy_name(buyerTenancy.getTenancy_name());
								//End DefectId:20150911
								pseudoCustTenancy.setParent_tenancy_name(dealerTen.getTenancy_name());
								//DF20150319 - Rajani Nagaraju - Customer is getting created under RO Instead of dealer
								//pseudoCustTenancy.setParent_tenancy_id(dealerTen.getParent_tenancy_id());
								pseudoCustTenancy.setParent_tenancy_id(dealerTen);
								pseudoCustTenancy.setClient_id(buyerTenancy.getClient_id());
								pseudoCustTenancy.setTenancy_type_id(buyerTenancy.getTenancy_type_id());
								pseudoCustTenancy.setOperating_Start_Time(buyerTenancy.getOperating_Start_Time());
								pseudoCustTenancy.setOperating_End_Time(buyerTenancy.getOperating_End_Time());
								pseudoCustTenancy.setCreatedBy(buyerTenancy.getCreatedBy());
								//Created Date will alone be the actual date of creation. Rest all will be the same details
								pseudoCustTenancy.setCreatedDate(new Timestamp((new Date()).getTime()));
								pseudoCustTenancy.setTenancyCode(buyerTenancy.getTenancyCode());
								pseudoCustTenancy.setMappingCode(buyerTenancy.getTenancyCode());
								session.save(pseudoCustTenancy); 
	
								iLogger.info("AssetGateOutLog: "+serialNumber+":Creating pseudoCustTenancy");
	
								//Task2: Connect newly created pseudoAccount and pseudoTenancy
								AccountTenancyMapping pseudoAccTen = new AccountTenancyMapping();
								pseudoAccTen.setAccount_id(pseudoCustAccount);
								pseudoAccTen.setTenancy_id(pseudoCustTenancy);
								session.save(pseudoAccTen);
	
								iLogger.info("AssetGateOutLog: "+serialNumber+":Creating pseudoCust AccountTenancy");
							}
						}
					}
	
					else
					{
	
						iLogger.info("AssetGateOutLog: "+serialNumber+":Customer Account exists under the given dealer");
	
						//Task1: Add the record that specifies the new pseudo customer account in AssetOwner table
						//Task2: Update primary_owner_id in Asset table
						custOwnerAccEntity = actualBuyerAcc;
					}
	
	
					//----------------------------------------- Step 5: Perform Machine movement 
					int previousAssetOwnerId = assetEntity.getPrimary_owner_id();
	
					if(custOwnerAccEntity.getAccount_id()!=previousAssetOwnerId)
					{
						//Check for the primary key constraint
						Query chkPrimaryKeyQuery = session.createQuery(" from AssetAccountMapping where serialNumber='"+assetEntity.getSerial_number().getSerialNumber()+"' and " +
								//" accountId="+custOwnerAccEntity.getAccount_id()+" and ownershipStartDate='"+currentDateString+"' ");//JCB6341.o
								" accountId="+custOwnerAccEntity.getAccount_id()+" and ownershipStartDate='"+gateoutDateString+"' ");//JCB6341.n
						Iterator chkPrimaryKeyItr = chkPrimaryKeyQuery.list().iterator();
						boolean duplicate = false;
						while(chkPrimaryKeyItr.hasNext())
						{
							AssetAccountMapping assetAcc = (AssetAccountMapping) chkPrimaryKeyItr.next();
							duplicate = true;
						}
	
						if(duplicate)
						{
							throw new CustomFault("Duplicate Gate Out Sale to Customer Data Received for the CustCode: "+customerCode+" and the VIN: "+serialNumber);
						}
	
						//insert the sale record details into asset_owners
						AssetAccountMapping newAssetOwner = new AssetAccountMapping();
						newAssetOwner.setAccountId(custOwnerAccEntity);
						newAssetOwner.setSerialNumber(assetEntity);
						//newAssetOwner.setOwnershipStartDate(currentDate);//JCB6341.o
						newAssetOwner.setOwnershipStartDate(gateoutDate);//JCB6341.n
						session.save(newAssetOwner);
	
						iLogger.info("AssetGateOutLog: "+serialNumber+":Insert the record into asset_owners table");
	
						//Update Primary_owner_id in asset to reflect the current owner
						assetEntity.setPrimary_owner_id(custOwnerAccEntity.getAccount_id());
						/*//DF20170803 : SU334449 - Updating the TimeZone with Customer's Country TimeZone when gate-out takes place
						String countryCode = null;
						String timeZone = null;
						//Step 1: Fetching contactId and countryCode from account_contact based on the dealer account id
						String accntContactQ = ("from AccountContactMapping where account_id="+custOwnerAccEntity.getAccount_id());
						Query accntQuery = session.createQuery(accntContactQ);
						Iterator accntContactItr = accntQuery.list().iterator();
						if(accntContactItr.hasNext()){
							AccountContactMapping contactMap = (AccountContactMapping) accntContactItr.next();
							countryCode = contactMap.getContact_id().getCountryCode();
						}
						//Step 2: Fetching TimeZone from CountryCodes based on the countryCode
						if(!(countryCode==null||countryCode.isEmpty())){
							String timeZoneQ = ("from CountryCodesEntity where country_name='"+countryCode+"'");
							Query timeZoneQuery = session.createQuery(timeZoneQ);
							Iterator timeZoneItr = timeZoneQuery.list().iterator();
							if(timeZoneItr.hasNext()){
								CountryCodesEntity countryCodes = (CountryCodesEntity) timeZoneItr.next();
								timeZone = countryCodes.getTimeZone();
							}
						}
						if(timeZone != null){
							assetEntity.setTimeZone(timeZone);
						}*/
	
						//DF20170822 : KO369761: Updating the TimeZone with Customer's Country TimeZone when gate-out takes place
						//DF20180502:KO369761 - If timezone & conuntry code null we are setting to default values.
						if(custOwnerAccEntity.getTimeZone() != null)
							assetEntity.setTimeZone(custOwnerAccEntity.getTimeZone());
						else
							assetEntity.setTimeZone("(GMT+05:30)");
	
						////DF20171218 : @Roopa: Updating the countrycode with Dealer's code 
						if(custOwnerAccEntity.getCountryCode() != null)
							assetEntity.setCountrycode(custOwnerAccEntity.getCountryCode());
						else
							assetEntity.setCountrycode("+91");
						//JCB6341.sn
						//Update sale date in case of direct customer gateout
						assetEntity.setSale_Date(new Timestamp(gateoutDate.getTime()));
						//JCB6341.en
						session.update(assetEntity);
	
						iLogger.info("AssetGateOutLog: "+serialNumber+":update the asset table");
					}
	
	
				}
	
	
			}
	
			catch(CustomFault e)
			{
				status = "FAILURE-"+e.getFaultInfo();
				bLogger.error("EA Processing: AssetGateOut: "+messageId+ " :"+e.getFaultInfo());
			}
	
			catch(Exception e)
			{
				status = "FAILURE-"+e.getMessage();
	
				e.printStackTrace();
				iLogger.info("AssetGateOutLog: "+serialNumber+":"+messageId+ " Exception Cause: "+e);
				Writer result = new StringWriter();
				PrintWriter printWriter = new PrintWriter(result);
				e.printStackTrace(printWriter);
				String err = result.toString();
				fLogger.fatal("EA Processing: AssetGateOut: "+messageId+ " Fatal Exception :"+err);
	
				iLogger.info("AssetGateOutLog: "+serialNumber+":"+messageId+ " Exception Trace:"+err);
	
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
	
	
	
			}
	
			finally
			{
				/* if(session.getTransaction().isActive())
	            {
	                  session.getTransaction().commit();
	            }*/
	
				//DF20140715 - Rajani Nagaraju - To Avoid Session Close Exception
	
				try
				{if(session!=null && session.isOpen())
				{
					if(session.getTransaction().isActive())
					{
						session.getTransaction().commit();
					}
	
	
				}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					status = "FAILURE-"+e.getMessage();
					//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
					fLogger.fatal("Exception in commiting the record:"+e);
				}
				if(session!=null && session.isOpen())
				{
					session.flush();
					session.close();
				}
			}
	
			return status;
		}
	
		//************************************************ END of Set Asset GateOut Details *****************************************************
	
	
	
		//************************************************ Start of Asset Provisioning Service (VIN Provisioning **** /
	
		/** This method sets the VIN registration details into DB
		 * @param serial_number VIN as String input
		 * @param Imei IMEI number as String input
		 * @param SimNo SIM number as String input
		 * @return Returns status message as String
		 * @throws CustomFault
		 * @throws SQLException
		 */
		public String setAssetProvisionDetails(String serial_number, String Imei, String SimNo, Timestamp registrationDate) throws CustomFault, SQLException
	
		{
			String flag="SUCCESS";
	
	
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			//DF20160503 - Roopa - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("Set AssetProvisionDetails - "+"Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
	
			session.beginTransaction();
	
			iLogger.info("Inside AssetDetailsBO: setAssetProvisionDetails() - Session Created Successfully" );
	
			try
			{
				boolean temp=true;
				AssetProvisioningImpl implObj =null;
				String EngineHours=null;
	
				iLogger.info("Inside AssetDetailsBO: setAssetProvisionDetails() - call getAssetControlDetails" );
				AssetControlUnitEntity assetControl = getAssetControlDetails (serial_number);
				iLogger.info("Inside AssetDetailsBO: setAssetProvisionDetails() - call getAssetControlDetails over" );
	
				/*if(! (session.isOpen() ))
	            {
	                        session = HibernateUtil.getSessionFactory().getCurrentSession();
	                        session.getTransaction().begin();
	            }*/
	
				ControlUnitEntity controlUnitEntity = null;
				Query query1 = session.createQuery("from ControlUnitEntity where controlUnitId='"+1+"'");
				Iterator itr1 = query1.list().iterator();
	
				//infoLogger.info("Inside AssetDetailsBO: setAssetProvisionDetails() - Data returned");
				while(itr1.hasNext())
				{
					//System.out.println("Inside while");
					controlUnitEntity = (ControlUnitEntity) itr1.next();
	
				}
	
				if((assetControl==null))
				{
					//System.out.println("Inside if");	
					AssetControlUnitEntity assetProvision = new AssetControlUnitEntity();
					assetProvision.setControlUnitId(controlUnitEntity);
					assetProvision.setSerialNumber(serial_number);		
					assetProvision.setImeiNo(Imei);
					assetProvision.setSimNo(SimNo);		
					assetProvision.setRegistrationDate(registrationDate);
					session.save(assetProvision);
					//System.out.println("Session saved");
	
				}
	
				else
				{
					/*
					//System.out.println("Inside else");
					Properties prop = new Properties();
					prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
					String engineHours = prop.getProperty("TotalEngineHours");
	
					//System.out.println("After Engine Hours");
					Timestamp maxTxnTime=null;
					Query maxTxQuery = session.createQuery(" select max(transactionTime) from AssetMonitoringHeaderEntity where serialNumber  ='"+serial_number+"' ");
					List maxTxList = maxTxQuery.list();
					//System.out.println("After Lits");
					Iterator maxTxItr = maxTxList.iterator();
					//System.out.println("After Iterator");
	
					if(maxTxList!=null && maxTxList.size()>0 && maxTxList.get(0)!=null)
					{
						while(maxTxItr.hasNext())
						{
							//System.out.println("Inside max while");
							//if(maxTxList!=null && maxTxList.size()>0 && maxTxList.get(0)!=null)
							//{
							//System.out.println("Get MaxTxnTime");
							maxTxnTime = (Timestamp)maxTxItr.next();
							//System.out.println("Get MaxTxnTime - Done");
						}
					}
	
					//System.out.println("maxTxnTime:"+maxTxnTime);
	//				Keerthi : 07/08/14 : delta engine hours 5
					if(maxTxnTime!=null)
					{
					String basicSelectQuery = " SELECT c. parameterId, c.transactionNumber  ,c.parameterValue ";
					String basicFromQuery = " FROM AssetMonitoringDetailEntity c ";
					//DF20140319 - Changing IN to = in the below query
					String basicWhere = " where c. parameterId  IN (select max(d.parameterId ) from MonitoringParameters d where d.parameterName ='"+engineHours+"' )" ;
					if(maxTxnTime!=null)
					{
						basicWhere=basicWhere+" and c.transactionNumber= ( select transactionNumber from AssetMonitoringHeaderEntity where transactionTime = '"+maxTxnTime+"' and serialNumber='"+serial_number+"')";
					}
	
					+
										" and c.transactionNumber   = (select max(transactionNumber ) from   AssetMonitoringHeaderEntity   where serialNumber  ='"+serial_number+"')";
	
					String finalQuery =basicSelectQuery+ basicFromQuery + basicWhere;
					Object[] result=null;
	
					Query q = session.createQuery(finalQuery);
					Iterator ittr = q.list().iterator();
	
					while(ittr.hasNext())
					{
						temp=false;
						result = (Object[]) ittr.next();
						EngineHours=(String)result[2];
					}
					}else{//auth packet has come for the 2nd or other and not yet communicating
						EngineHours="SUCCESS";
					}
					assetControl.setImeiNo(Imei);
					assetControl.setSimNo(SimNo);
					assetControl.setRegistrationDate(registrationDate);
					session.update(assetControl);
	
					 */
					//DF20161222 - @Supriya - format change for asset monitoring snapshot
					String txnKey = "AssetDetailsBO:setAssetProvisionDetails";
	
					/*List<AmsDAO> snapshotObj=new ArrayList<AmsDAO> ();
	
					DynamicAMS_DAL amsDaoObj=new DynamicAMS_DAL();
	
					snapshotObj=amsDaoObj.getAMSData(txnKey, serial_number);
	
					//iLogger.debug(txnKey+"::"+"AMS:persistDetailsToDynamicMySql::AMS DAL::getAMSData Size:"+snapshotObj.size());
	
					if(snapshotObj.size()>0){
	
						//parameters format in AMS
						//String currParam= LAT|LONG|Enginestatus|Machinehours|ExternalBatteryVoltage|HCT|LOP|InternalBatteryLow
						temp = false;
					String parameters=snapshotObj.get(0).getParameters();
					String [] currParamList=parameters.split("\\|", -1);
	
					EngineHours = currParamList[3];
					}
					else{//auth packet has come for the 2nd or other and not yet communicating
						EngineHours="SUCCESS";
					}*/
	
					List<AMSDoc_DAO> snapshotObj=new ArrayList<AMSDoc_DAO> ();
	
					DynamicAMS_Doc_DAL amsDaoObj=new DynamicAMS_Doc_DAL();
	
					snapshotObj=amsDaoObj.getAMSData(txnKey, serial_number);
	
					iLogger.info(txnKey+"::"+"AssetExtendedService::getAssetExtendedDetails::AMS DAL::getAMSData Size:"+snapshotObj.size());
	
					HashMap<String,String> txnDataMap=new HashMap<String, String>();
	
					if(snapshotObj.size()>0){
						txnDataMap=snapshotObj.get(0).getTxnData();
						EngineHours=txnDataMap.get("CMH");
					}
					else{//auth packet has come for the 2nd or other and not yet communicating
						EngineHours="SUCCESS";
					}
	
					assetControl.setImeiNo(Imei);
					assetControl.setSimNo(SimNo);
					assetControl.setRegistrationDate(registrationDate);
					session.update(assetControl);
	
				}
	
				if(temp==true)
				{
					flag= "SUCCESS";
				}
				else
					flag= EngineHours;
	
	
				//Defect Id:20140905 FailureCounter value updatation in FaultDetails Table @Suprava
				String machineNumber = null;
				if(serial_number!=null){
					machineNumber = serial_number.substring(10, 17);
				}
				int failureCounter =0;
				Timestamp currentTime = new Timestamp(new Date().getTime());
				//System.out.println("currentTime"+currentTime);
				int lastPkt = 0;
				FaultDetails faultDetailsObj = null;
				Query query = session.createQuery("from FaultDetails where messageString like '%"+machineNumber+"%'");
				Iterator itr = query.list().iterator();
				Object[] ResultSet1 = null;
				while(itr.hasNext())
				{
					faultDetailsObj = (FaultDetails)itr.next();
					faultDetailsObj.setFailureCounter((faultDetailsObj.getFailureCounter())-1);
					faultDetailsObj.setReprocessTimeStamp(currentTime);
					session.update(faultDetailsObj);
				}
				//Defect Id:20140905 End 
			}
	
			catch(Exception e)
			{
				fLogger.fatal("Exception :"+e);
			}
	
			finally
			{
	
				//DF201605003 - Roopa - Addig try catch around commit
				try
				{
					if(session.isOpen())
						if(session.getTransaction().isActive())
						{
							session.getTransaction().commit();
						}
				}
				catch(Exception e2)
				{
					fLogger.fatal("Set AssetProvisionDetails - "+"Exception in commiting the record:"+e2);
	
				}
	
				if(session.isOpen())
				{
					session.flush();
					session.close();
				}
			}
	
			return flag;
		}
	
		/** This method returns the AssetControlUnitEntity for a given serialNumber
		 * @param serial_number VIN as String input
		 * @return AssetControlUnitEntity
		 */
		public AssetControlUnitEntity getAssetControlDetails(String serial_number)
		{
			//infoLogger.info("Inside AssetDetailsBO: getAssetControlDetails() - get Details" );
			/*AssetControlUnitEntity assetControlObj=new AssetControlUnitEntity(serial_number);
			if(assetControlObj.getSerialNumber()==null)
				return null;
			else
				return assetControlObj;*/
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
	
			AssetControlUnitEntity assetControlObj =null;
	
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			//DF20160503 - Roopa - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("Get AssetControlDetails - "+"Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
	
			session.beginTransaction();
	
			try
			{
				Query q = session.createQuery(" from AssetControlUnitEntity where serialNumber='"+serial_number+"'");
				Iterator itr = q.list().iterator();
				while(itr.hasNext())
				{
					assetControlObj = (AssetControlUnitEntity) itr.next();
				}
			}
	
			catch(Exception e)
			{
				fLogger.fatal("Exception :"+e);
			}
	
			finally
			{
	
				//DF20160503-Roopa - Addig try catch around commit
				try
				{
					if(session.isOpen())
						if(session.getTransaction().isActive())
						{
							session.getTransaction().commit();
						}
				}
				catch(Exception e2)
				{
					fLogger.fatal("getAssetControlDetails - "+"Exception in commiting the record:"+e2);
	
				}
	
				if(session.isOpen())
				{
					session.flush();
					session.close();
				}
			}
	
			return assetControlObj;
	
		} 
		// ******* End of Asset Provisioning Service (VIN Provisioning **** /
	
		// **************** Machine Profile Details-- start ***** /
		/*
		public List<MachineProfileImpl> getMachineProfileDetails(String loginId)throws CustomFault
		{
	
	
			List<MachineProfileImpl> macProImpl=new LinkedList<MachineProfileImpl>();;
	
			HibernateSessionConfig hy2=new HibernateSessionConfig();
			Session ss2=hy2.createSession();
			Transaction tr2=ss2.beginTransaction();
	
		    String queryString="from AssetGroupEntity"; where Asset_Group_ID="+AssetGroupId;
			Iterator itr2=ss2.createQuery(queryString).list().iterator();
			MachineProfileImpl machinePro=new MachineProfileImpl();
			while (itr2.hasNext())
			{
	
	
				AssetGroupEntity machineEntity=(AssetGroupEntity)itr2.next();
				int assetGroupId=machineEntity.getAsset_group_id();
		    	machinePro.setAssetGroupId(assetGroupId);
				machinePro.setAssetGroupName(machineEntity.getAsset_group_name());
				//machinePro.setAssetGroupCode(machineEntity.getAsset_groyp_code());
				Query queryString1=ss2.createQuery("from AssetEntity where asset_group_id="+assetGroupId);
				Iterator itr3=queryString1.list().iterator();
				while(itr3.hasNext())
				{
					AssetEntity ety=(AssetEntity)itr3.next();
					String serilaNumber=ety.getSerial_number().getSerialNumber();
					Query queryString3=ss2.createQuery("from assetProfileEntity where serialNumber='"+serilaNumber+"'");
					Iterator itr4=queryString3.list().iterator();
					while(itr4.hasNext())
					{
						AssetExtendedDetailsEntity assetProEty=(AssetExtendedDetailsEntity)itr4.next();
						machinePro.setAsseetOperatingStartTime(assetProEty.getOperatingStartTime().toString());
						machinePro.setAsseetOperatingEndTime(assetProEty.getOperatingEndTime().toString());
						macProImpl.add(machinePro);
	
					}	
				}
			}
	
	
			return macProImpl;
		}
	
	
	
	
		// **************** Machine Profile Details-- Get End ****** /
	
		// Machine Profile Set Methos *****
	
		public String setMachineProfileDetails(int asset_group_id,String asset_group_name,String assetOperatingStartTime,String asseetOperatingEndTime)throws CustomFault
		{
			HibernateSessionConfig hy3=new HibernateSessionConfig();
			Session ss3=hy3.createSession();
			Transaction tr3=ss3.beginTransaction();
	
			//String qr2="from AssetGroupEntity where asset_groyp_code= '"+assetGroupCode+"'";
	
	
		    Query query=ss3.createQuery("from AssetGroupEntity where asset_group_id="+asset_group_id);
			Iterator itr3=query.list().iterator();
	
			int update=0;
			while(itr3.hasNext())
			{
				update=1;
				AssetGroupEntity etty=(AssetGroupEntity)itr3.next();
	
				etty.setAsset_group_id(asset_group_id);
				etty.setAsset_group_name(asset_group_name);
				ss3.update(etty);
	
				Query query1=ss3.createQuery("from AssetEntity where asset_group_id="+asset_group_id);
				Iterator itr4=query1.list().iterator();
				while(itr4.hasNext())
				{
					update=1;
					AssetEntity eety=(AssetEntity)itr4.next();
					String serialNumber=eety.getSerial_number().getSerialNumber();
	
					eety.setAsset_group_id(eety.getProductId().getAssetGroupId());
					eety.setSerialNumber(serialNumber);
	
					ss3.update(eety);
	
	
					Query query2=ss3.createQuery("from assetProfileEntity where serialNumber='"+serialNumber+"'");
					Iterator itr5=query2.list().iterator();
					while(itr5.hasNext())
					{
						AssetExtendedDetailsEntity proeety=(AssetExtendedDetailsEntity)itr5.next();
						proeety.setOperatingStartTime(assetOperatingStartTime());
						proeety.setOperatingEndTime(asseetOperatingEndTime);
	
						ss3.update(proeety);
					}
					if(update==0)
					{
						throw new CustomFault("serialNumber  IS NOT THEIR TO UPDATE");
					}
	
				}
				if(update==0)
				{
					throw new CustomFault("asset_group_id  IS NOT THEIR TO UPDATE");
				}
	
	
				tr3.commit(); 
		    } 
			if(update == 0)
			{
				throw new CustomFault("ASSET GROUP ID IS NOT THEIR TO UPDATE");
			}
	
	
	
	
				else
				{
					h.getSession().getTransaction().begin();
					AssetEntity editAssetEntity= (AssetEntity) h.getSession().merge(asset_entity);
					editAssetEntity.setEquipment(equipment);
					//editAssetEntity.setMake(make);
					editAssetEntity.setModel(model);
					editAssetEntity.setNick_name(nick_name);
					editAssetEntity.setActive_status(active_status);
					editAssetEntity.setAsset_class_id(asset_class_entity);
					editAssetEntity.setAsset_group_id(asset_group_entity);
					editAssetEntity.setAsset_type_id(asset_type_entity);	
					h.getSession().save(editAssetEntity);
					h.getSession().getTransaction().commit();
				}
	
	
			//	update=1;	
	
			if(update==0)
	
			{
				AssetGroupEntity macProEty=new AssetGroupEntity();
				assetProfileEntity assProEty=new assetProfileEntity();
	
				macProEty.setAsset_group_id(asset_group_id);
				macProEty.setAsset_group_name(asset_group_name);
	
	
				assProEty.setOperatingStartTime(assetOperatingStartTime);
				assProEty.setOperatingEndTime(asseetOperatingEndTime);
	
				Timestamp ts = Timestamp.valueOf(assetOperatingStartTime);
	
				Timestamp ts1 = Timestamp.valueOf(asseetOperatingEndTime);
				assProEty.setOperatingStartTime(ts);
	
				assProEty.setOperatingEndTime(ts1);
				macProEty.setAssetOperatingStartTime(ts);	
				macProEty.setAsseetOperatingEndTime(ts1);
				macProEty.save();
				assProEty.save();
				}
				catch (ParseException e) {		
					e.printStackTrace();
				}
	
		       return "SUCCESS";
		}
	
		// End Set Method for Machine profile sericve
		 */
	
		public List<MachineProfileImpl> getMachineProfileDetails(String loginId)throws CustomFault
		{
	
	
			List<MachineProfileImpl> macProImpl=new LinkedList<MachineProfileImpl>();;
	
			//Logger fatalError = Logger.getLogger("fatalErrorLogger");
			Logger fLogger = FatalLoggerClass.logger;
	
			//Logger iLogger = InfoLoggerClass.logger;
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
	
				String queryString="from AssetGroupEntity where client_id="+clientEntity.getClient_id()+""; /*where Asset_Group_ID="+AssetGroupId*/;
				Iterator itr2=session.createQuery(queryString).list().iterator();
				MachineProfileImpl machinePro=new MachineProfileImpl();
				while (itr2.hasNext())
				{
	
	
					AssetGroupEntity machineEntity=(AssetGroupEntity)itr2.next();
					int assetGroupId=machineEntity.getAsset_group_id();
					machinePro.setAssetGroupId(assetGroupId);
					//machinePro.setAssetGroupName(machineEntity.getAsset_group_name());
					//machinePro.setAssetGroupCode(machineEntity.getAsset_groyp_code());
					Query queryString1=session.createQuery("from AssetEntity where asset_group_id="+assetGroupId+" and active_status=1 and client_id="+clientEntity.getClient_id());
					Iterator itr3=queryString1.list().iterator();
					while(itr3.hasNext())
					{
						AssetEntity ety=(AssetEntity)itr3.next();
						String serilaNumber=ety.getSerial_number().getSerialNumber();
						Query queryString3=session.createQuery("from assetProfileEntity where serialNumber='"+serilaNumber+"'");
						Iterator itr4=queryString3.list().iterator();
						while(itr4.hasNext())
						{
							AssetExtendedDetailsEntity assetProEty=(AssetExtendedDetailsEntity)itr4.next();
							//machinePro.setOperatingStartTime(assetProEty.getOperatingStartTime());
							//machinePro.setOperatingEndTime(assetProEty.getOperatingEndTime());
							macProImpl.add(machinePro);
	
						}	
					}
				}
			}
	
			catch(Exception e)
			{
				fLogger.fatal("Exception :"+e.getMessage());
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
	
			return macProImpl;
		}
	
		////*******START OF GET MACHINEPROFILESERVICE********************************
	
		/**
		 * 
		 * @param loginId , gets the profile of the machine 
		 * @return listOfMachineProfileImpl provides the list of profile on individual machine including the start and end time of the machine 
		 * @throws CustomFault
		 */
		public List<MachineProfileImpl> getMachineProfile(String loginId)throws CustomFault
		{
			Logger fLogger = FatalLoggerClass.logger;
			//Logger bLogger = BusinessErrorLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			iLogger.info("Entering getMachineProfile()");
			long startTimeTaken=System.currentTimeMillis();
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<MachineProfileImpl> listOfMachineProfileImpl=new LinkedList<MachineProfileImpl>();
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
	
				Query query=session.createQuery("select a.asset_group_id, a.asset_group_name, b.asset_grp_code, b.operating_Start_Time, " +
						" b.Operating_end_time from AssetGroupEntity a, AssetGroupProfileEntity b where a.asset_group_id=b.asset_grp_id and a.client_id="+clientEntity.getClient_id()+"" +
						" order by a.asset_group_id asc ");
				iLogger.info("Query is   "+query);
				Iterator iterator=query.list().iterator();
				Object[] result = null;
				SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
				while(iterator.hasNext())
				{
					MachineProfileImpl machineProfileImpl=new MachineProfileImpl();
					result = (Object[]) iterator.next();
	
	
					machineProfileImpl.setAssetGroupId((Integer)result[0]);
					if(result[1]!=null)
						machineProfileImpl.setProfileName(result[1].toString());
					if(result[2]!=null)
						machineProfileImpl.setAssetGroupCode(result[2].toString());
					String startTime="";
					String endTime="";
					if(result[3]!=null)
					{
						Timestamp operatingStartTime = (Timestamp)result[3];
						startTime = dateFormat.format(operatingStartTime);
					}
					if(result[4]!=null)
					{
						Timestamp operatingEndTime = (Timestamp)result[4];
						endTime = dateFormat.format(operatingEndTime);
					}
	
					machineProfileImpl.setOperatingStartTime(startTime);
					machineProfileImpl.setOperatingEndTime(endTime);
	
					listOfMachineProfileImpl.add(machineProfileImpl);
				}
			}
			catch(Exception e)
			{
				fLogger.fatal("Hello this is an Fatal Error. Need immediate Action   "+e.getMessage());
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
			long endTimeTaken=System.currentTimeMillis();
			long totalTimeTaken=endTimeTaken-startTimeTaken;
			iLogger.info("totalTimeTaken by getMachineProfile() is   "+totalTimeTaken);
			iLogger.info("Exiting getMachineProfile()");
			return listOfMachineProfileImpl;
		}
		//*******END OF GETMACHINEPROFILESERVICE********************************
	
	
	
		/**
		 * 
		 * @param assetGroupId is passed to set the id of the asset group
		 * @param profileName is passed to set profileName 
		 * @param assetGroupCode is passed to set assetGroupCode
		 * @param operatingStartTime is passed to set operatingStartTime
		 * @param operatingEndTime is passed to set operatingEndTime
		 * @return SUCCESS if the values are successfully set's
		 * @throws CustomFault
		 */
		public String setMachineProfileService(int assetGroupId,String profileName,String assetGroupCode,String operatingStartTime,String operatingEndTime)throws CustomFault
		{
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			iLogger.info("Entering setMachineProfileService()");
			long startTimeTaken=System.currentTimeMillis();
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try
			{		
	
				Query query2=session.createQuery("from AssetGroupEntity where asset_group_id="+assetGroupId);
				iLogger.info("query2 is   "+query2);
				Iterator iterator2=query2.list().iterator();
				int update=0;
				int assetGroupID=0;
	
				while(iterator2.hasNext())
				{
					update=1;
					AssetGroupEntity assetGroupEntity=(AssetGroupEntity)iterator2.next();		
					assetGroupID=assetGroupEntity.getAsset_group_id();
					assetGroupEntity.setAsset_group_name(profileName);
					session.update(assetGroupEntity);
				}
				Query query3=session.createQuery("from AssetGroupProfileEntity where asset_grp_id="+assetGroupID);
				iLogger.info("query3 is   "+query3);
				Iterator iterator3=query3.list().iterator();
	
				while(iterator3.hasNext())
				{
					SimpleDateFormat datetimeFormat = new SimpleDateFormat("hh:mm:ss");			    
					Timestamp startTime = null;
					Timestamp endTime = null;
	
					if(operatingStartTime!=null)
					{
						try
						{
							Date operatingStartDate = datetimeFormat.parse(operatingStartTime);
							startTime = new Timestamp(operatingStartDate.getTime());
						}
						catch(ParseException e)
						{
							bLogger.error("Please provide the time in the format hh:mm:ss");
							throw new CustomFault("Please provide the time in the format hh:mm:ss");
						}
					}
	
					if(operatingEndTime!=null)
					{
						try
						{
							Date operatingEndDate = datetimeFormat.parse(operatingEndTime);
							endTime = new Timestamp(operatingEndDate.getTime());
						}
						catch(ParseException e)
						{
							bLogger.error("Please provide the time in the format hh:mm:ss");
							throw new CustomFault("Please provide the time in the format hh:mm:ss");
						}
					}   
					AssetGroupProfileEntity assetGroupProfile=(AssetGroupProfileEntity)iterator3.next();
					assetGroupProfile.setAsset_grp_code(assetGroupCode);
					assetGroupProfile.setOperating_Start_Time(startTime);
					assetGroupProfile.setOperating_end_time(endTime);
					session.update(assetGroupProfile);					
				}
				if(update == 0)
				{
					bLogger.error("ASSET GROUP ID DOES NOT EXIST");
					//throw new CustomFault("ASSET GROUP ID DOES NOT EXIST");
				}
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
			long endTimeTaken=System.currentTimeMillis();
			long totalTimeTaken=endTimeTaken-startTimeTaken;
			iLogger.info("totalTimeTaken for method setMachineProfileService() is   "+totalTimeTaken);
			iLogger.info("Exiting setMachineProfileService()");
			return "SUCCESS";
		}
		//*******END OF SETMACHINEPROFILESERVICE********************************
	
		/**
		 * method to clear alerts active status to 0 after 24 hours
		 * @return String 
		 */
		public String clearAlertAfter24Hrs(){
	
			Logger iLogger = InfoLoggerClass.logger;
			Calendar today1 = Calendar.getInstance();  
			today1.add(Calendar.DATE, -1);  
			java.sql.Date yesterday = new java.sql.Date(today1.getTimeInMillis());  
	
			String strYesterday = yesterday.toString();
			String strYesterdayLL =strYesterday +" 00:00:00";
			String strYesterdayUL =strYesterday +" 23:59:59";
			long startTime = System.currentTimeMillis();
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();  
			int rows =0;
			Timestamp currentTime = new Timestamp(new Date().getTime());
			try{
	
				/*Query updateQuery = session.createQuery("update AssetEventEntity t1 set t1.activeStatus=0 " +
						" where t1.eventId =(SELECT eventId FROM EventEntity WHERE eventName ='Usage outside Operating Hours')" +
						" AND t1.eventGeneratedTime between '"+strYesterdayLL+"' AND '"+strYesterdayUL+"'");*/
				//DefectID:20140520 ClearAlert after 24 hours @Suprava
				Query updateQuery = session.createQuery("update AssetEventEntity t1 set t1.activeStatus=0,t1.eventClosedTime='"+currentTime +
						"',t1.created_timestamp = '"+currentTime+"' where t1.eventId =(SELECT eventId FROM EventEntity WHERE eventName ='Usage outside Operating Hours')" +
						" AND t1.eventGeneratedTime <='"+strYesterdayUL+"'");
				iLogger.info("query for updation :"+updateQuery);
				rows =updateQuery.executeUpdate();
			}
			finally{
				if(session.getTransaction().isActive()){      
					session.getTransaction().commit();
				}
				if(session.isOpen()){ 
					session.flush();
					session.close();
				} 
			}
			long endTime=System.currentTimeMillis();
			iLogger.info("Total execution time "+ String.valueOf(endTime-startTime)+ "(ms)");
			if(rows>0){
				iLogger.info("Successfully updated "+rows+" rows.");
				return "Successfully updated "+rows+" rows.";
	
			}
			else{
				return "Either data not found OR not able to update !";
			}
		}
	
	
	
		//*******START OF GET FleetSummaryService********************************
		public List<FleetSummaryImpl> getFleetSummaryService(String period,String contactId,List<Integer> tenancyIdList,List<Integer> assetGroupIdList,List<Integer> assetTypeIdList,List<Integer> customAssetGroupIdList,List<Integer> landmarkIdList,List<String> alertSeverityList,List<Integer> alertTypeIdList,List<Integer> machineGroupId,List<Integer> notificationDimensionID,boolean isOwnStock)
		{
	
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			iLogger.info("Entering getFleetSummaryService()");
			// long startTimeTaken=System.currentTimeMillis();
	
			List<FleetSummaryImpl> listFleetSummaryImpl = new LinkedList<FleetSummaryImpl>();
			Calendar c = Calendar.getInstance();
			String MDAassetTypeCodeList=null;
			// int currentYear = c.get(Calendar.YEAR);
			String MDAassetGroupCodeList=null;
			String MDAaccountIdListAsString=null;
			String MDAoutput=null;
			String MDAresult=null;
			String selectQuery = null;
			String fromQuery = null;
			String whereQuery = null;
			String finalQuery = null;
	
			StringBuilder tenancyListAsString = null;
			StringBuilder assetGroupIdListAsString = null;
			StringBuilder assetTypeIdListAsString = null;
			StringBuilder alertTypeIdListAsString = null;
			StringBuilder alertSeverityListAsString = null;
			StringBuilder alertCodeListAsString = null;
			StringBuilder customAssetGroupIdListAsString = null;
			/*
			 * StringBuilder landmarkIdListAsString = null; StringBuilder
			 * machineGroupIdAsString = null; StringBuilder
			 * notificationDimensionIDAsString = null;
			 */
			
			//CR<xxxx>.sn
			String connIP=null;
			String connPort=null;
			Properties prop = null;
			try{
				prop = CommonUtil.getDepEnvProperties();
				connIP = prop.getProperty("MDA_ServerIP");
				connPort = prop.getProperty("MDA_ServerPort");
				iLogger.info("AssetDetailsBO :: MDAIP:"+ connIP + " :: MDAPort:" + connPort);
			}catch(Exception e){
				fLogger.fatal("AssetDetailsBO : getFleetSummaryService : " +
						"Exception in getting Server Details for MDA Layer from properties file: " +e);
			}
			//CR<xxxx>.en
			ListToStringConversion conversionObj = new ListToStringConversion();
			DateUtil dateUtil1 = new DateUtil();
			DateUtil thisDate = dateUtil1.getCurrentDateUtility(new Date());
			FleetSummaryImpl MDAResponse=new FleetSummaryImpl();
			HashMap<String,Double> MDAOutputMap = null;
	
			if (tenancyIdList != null && tenancyIdList.size() > 0) {
				tenancyListAsString = conversionObj
						.getIntegerListString(tenancyIdList);
			}
	
			if (assetGroupIdList != null && assetGroupIdList.size() > 0) {
				assetGroupIdListAsString = conversionObj
						.getIntegerListString(assetGroupIdList);
			}
	
			if (assetTypeIdList != null && assetTypeIdList.size() > 0) {
				assetTypeIdListAsString = conversionObj
						.getIntegerListString(assetTypeIdList);
			}
	
			if (alertTypeIdList != null && alertTypeIdList.size() > 0) {
				alertTypeIdListAsString = conversionObj
						.getIntegerListString(alertTypeIdList);
			}
	
			if (alertSeverityList != null && alertSeverityList.size() > 0) {
				alertSeverityListAsString = conversionObj
						.getStringList(alertSeverityList);
			}
			if (customAssetGroupIdList != null && customAssetGroupIdList.size() > 0) {
				customAssetGroupIdListAsString = conversionObj
						.getIntegerListString(customAssetGroupIdList);
			}
	
			/*
			 * if (landmarkIdList != null && landmarkIdList.size() > 0) {
			 * landmarkIdListAsString = conversionObj
			 * .getIntegerListString(landmarkIdList); }
			 * 
			 * if (machineGroupId != null && machineGroupId.size() > 0) {
			 * machineGroupIdAsString = conversionObj
			 * .getIntegerListString(machineGroupId); }
			 * 
			 * if (notificationDimensionID != null && notificationDimensionID.size()
			 * > 0) { notificationDimensionIDAsString = conversionObj
			 * .getIntegerListString(notificationDimensionID); }
			 */
	
			Session session = HibernateUtil.getSessionFactory().openSession();
			Session sessionForAccount = HibernateUtil.getSessionFactory().openSession();
			try {
				if(customAssetGroupIdList==null || (customAssetGroupIdList != null && customAssetGroupIdList.isEmpty()))
				{
					List<Integer> AccountIdList = new ArrayList<Integer>();
	
					List<String> AccountCodeList = new ArrayList<String>();
	
					AccountEntity account = null;
	
					Query accountQ = session
							.createQuery(" select at.account_id from AccountTenancyMapping at where at.tenancy_id in("
									+ tenancyListAsString + ") ");
					Iterator accItr = accountQ.list().iterator();
					while (accItr.hasNext()) {
						account = (AccountEntity) accItr.next();
						AccountIdList.add(account.getAccount_id());
	
						//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
	
						//AccountCodeList.add(account.getAccountCode());
						AccountCodeList.add(account.getMappingCode());
					}
	
					if (session != null && session.isOpen()) {
						session.close();
					}
	
					StringBuilder AccountIdListAsString = conversionObj
							.getIntegerListString(AccountIdList);
					StringBuilder MDAaccountIdListAsStringBuilder=conversionObj.getStringWithoutQuoteList(AccountCodeList);
					MDAaccountIdListAsString=conversionObj.removeLastComma(MDAaccountIdListAsStringBuilder.toString());	
					StringBuilder AccountCodelistAsString = conversionObj
							.getStringList(AccountCodeList);
	
					if ((!((alertSeverityList == null) || (alertSeverityList
							.isEmpty())))
							|| (!((alertTypeIdList == null) || (alertTypeIdList
									.isEmpty())))) {
						DateUtil utilObj = new DateUtil();
						List<String> alertCodeList = utilObj.roleAlertMapDetails(contactId,
								0, "Display");
	
						if (alertCodeList != null && alertCodeList.size() > 0) {
							alertCodeListAsString = conversionObj
									.getStringList(alertCodeList);
						}
					}
	
					Session session12 = HibernateUtil.getSessionFactory().openSession();
					session12.beginTransaction();
					if (period.equalsIgnoreCase("Today")) {
	
						Connection prodConn = null;
						Statement stmnt = null;
						ResultSet res = null;
						// DF20190201 :: Mani : checking if the account id exists in
						// Temp data, if exists fetch from temp table if not hit the
						// mool to fetch the data
						String findQ = ("from FleetSummaryChartTempDataEntity where accountId = "+AccountIdList.get(0));
						Query query2 = sessionForAccount.createQuery(findQ);
						Iterator iterator2 = query2.list().iterator();
						if(!iterator2.hasNext()){
							/**
							 * DF20181115-KO369761 Fetching Fleet utillization Data from
							 * Mongo URL instead of wise DB
							 **/
							try{
								iLogger.info("MDA FleetSummaryService For Mongo");
								//HashMap<String,Double> MDAOutputMap = null;
								String accountFilter="";
								String countryCode = null;
								//FleetSummaryImpl MDAResponse = new FleetSummaryImpl();
	
								//Fetching yesterday's data.
								Calendar cal = Calendar.getInstance();
								cal.add(Calendar.DATE, -1);
								DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
								String yesterdayDate = dateFormat.format(cal.getTime());
	
								ConnectMySQL connSql = new ConnectMySQL();
								prodConn = connSql.getConnection();
								stmnt = prodConn.createStatement();
	
								//For accountFilter filling
								if(AccountIdList!=null && AccountIdList.size()>0){
	
									String countryCodeQ = "select countryCode from account where account_id ="+AccountIdList.get(0);
									res=stmnt.executeQuery(countryCodeQ);
									if(res.next())
									{
										countryCode=res.getString("countryCode");
									}
	
									String acntTncyQuery = "select Tenancy_Type_Name from tenancy_type where Tenancy_Type_ID in (select Tenancy_Type_ID from tenancy where Tenancy_ID in (select Tenancy_ID from account_tenancy where Account_ID='"
											+ AccountIdList.get(0) + "'))";
									iLogger.info("acntTncyQuery "+acntTncyQuery);
									res=stmnt.executeQuery(acntTncyQuery);
									String tenancy_type_name="";
									if(res.next())
									{
										tenancy_type_name=res.getString("Tenancy_Type_Name");
									}
	
									if(tenancy_type_name!=null && !tenancy_type_name.equalsIgnoreCase(""))
									{
										if(tenancy_type_name.equalsIgnoreCase("Global"))
										{
											accountFilter=null;
											countryCode = null;
										}
										else if(tenancy_type_name.equalsIgnoreCase("Regional"))
										{
											accountFilter="RegionCode";
										}
										else if(tenancy_type_name.equalsIgnoreCase("Zonal"))
										{
											accountFilter="ZonalCode";
										}
										else if(tenancy_type_name.equalsIgnoreCase("Dealer"))
										{
											accountFilter="DealerCode";
										}
										else if(tenancy_type_name.equalsIgnoreCase("Customer"))
										{
											accountFilter="CustCode";
										}
										else
										{
											accountFilter="";
										}
									}
								}
	
								//DF20181113 @KO369761: fetching fleet summary chart data from MongoDB Service.
								//DF20181219:KO369761 - URL Changed for SIT MOOLDA Reports.
								//20220705: Dhiraj K : Changes for AWS ip and port changes
								//"http://10.179.12.25:26030/MoolDAReports/LLFleetSummaryService/getLLFleetSummary?accountFilter="
								//String url = "http://10.210.196.206:26030/MoolDAReports/LLFleetSummaryService/getLLFleetSummary?accountFilter="  //CR337.o
								String url = "http://"+connIP+":"+connPort+"/MoolDAReports/LLFleetSummaryService/getLLFleetSummary?accountFilter=" //CR337.n
										+ accountFilter
										+ "&accountIDList="
										+ AccountIdListAsString
										+ "&period=Date"
										+ "&value1="
										+ yesterdayDate
										+ "&value2="
										+ yesterdayDate
										+ "&loginID="
										+ contactId
										+ "&countryCode=";
	
								if(countryCode != null)
									url = url+URLEncoder.encode(countryCode,"UTF-8");
								else
									url = url+countryCode;
	
								iLogger.info("MDA FleetSummaryService URL : "+url);
								URL MDAUrl = new URL(url);
								HttpURLConnection conn =(HttpURLConnection) MDAUrl.openConnection();
								conn.setRequestMethod("GET"); 
								conn.setRequestProperty("Accept", "application/json");
								if (conn.getResponseCode() != 200  && conn.getResponseCode() != 204) {
									iLogger.info("MDAReports report status: FAILURE for FleetSummaryService Report contactId: "+contactId+" ::Response Code:"+conn.getResponseCode());
									throw new RuntimeException("Failed : HTTP error code : " +conn.getResponseCode()); 
								}
								iLogger.info("MDAReports report status: SUCCESS for FleetSummaryService Report contactId: "+contactId+" ::Response Code:"+conn.getResponseCode());
								BufferedReader br = new BufferedReader(new  InputStreamReader((conn.getInputStream())));
	
								System.out.println("MDA FleetSummaryService Output from Server .... \n");
								while ((MDAoutput = br.readLine()) != null) { 
									//System.out.println("MDA json output "+MDAoutput); 
									iLogger.info("MDA FleetSummaryService json output "+MDAoutput);
									MDAresult =MDAoutput; } 
								if(!MDAresult.contains("Error")){
									MDAOutputMap = new Gson().fromJson(MDAresult, new TypeToken<HashMap<String, Double>>(){}.getType());
									//MDAResponse = mapper.convertValue(MDAOutputMap, FleetSummaryImpl.class);
									MDAResponse.setTotalIdleHours(MDAOutputMap.get("idletime"));
									MDAResponse.setTotalOffHours(MDAOutputMap.get("EngineOffTime"));
									MDAResponse.setTotalWorkingHours(MDAOutputMap.get("WorkingTime"));
									//DF20190308:Abhishek:To display date in FleetUtilization chart in UI
									MDAResponse.setResp_Date(yesterdayDate);
									//System.out.println("MDAResponse outmap : "+MDAResponse);
									iLogger.info("MDAResponse outmap : "+MDAResponse);
									listFleetSummaryImpl.add(MDAResponse);
								}
								conn.disconnect();
	
							}catch (Exception e) {
								// TODO: handle exception
								fLogger.error("Exception: "+e);
								e.printStackTrace();
							}
							finally{
	
								if (res != null)
									try {
										res.close();
									} catch (SQLException e1) {
										e1.printStackTrace();
									}
	
								if (stmnt != null)
									try {
										stmnt.close();
									} catch (SQLException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
	
								if (prodConn != null) {
									try {
										prodConn.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
	
							}
						}
						else{
	
							try {
								//DF20170717 @KO369761: fetching fleet summary chart data from temporary chart data table.
								iLogger.info("MDA FleetSummaryService For TempData");
								//String selectQ = "select sum(engineOffHours),sum(engineIdleHours),sum(engineWorkingHours) from FleetSummaryChartTempDataEntity where accountId in ("+AccountIdListAsString+")";						
								String selectQ = "from FleetSummaryChartTempDataEntity where accountId in ("+AccountIdListAsString+")";						
								Query query = session12.createQuery(selectQ);
								Iterator iterator = query.list().iterator();
								DecimalFormat decimalFormat = new DecimalFormat("###.#");
								Object[] result1 = null;
								//DF20190308:Abhishek:To display date in FleetUtilization chart in UI
								Date yest_date;
								Calendar cal;
								SimpleDateFormat dateformat;
								while (iterator.hasNext()) {
									FleetSummaryChartTempDataEntity fleetSummaryEntity = (FleetSummaryChartTempDataEntity) iterator.next();
									FleetSummaryImpl fleetSummaryImpl = new FleetSummaryImpl();
									fleetSummaryImpl.setTotalOffHours(Double.valueOf(decimalFormat.format(fleetSummaryEntity.getEngineOffHours())));
									fleetSummaryImpl.setTotalIdleHours(Double.valueOf(decimalFormat.format(fleetSummaryEntity.getEngineIdleHours())));
									fleetSummaryImpl.setTotalWorkingHours(Double.valueOf(decimalFormat.format(fleetSummaryEntity.getEngineWorkingHours())));
									//DF20190308:Abhishek:To display date in FleetUtilization chart in UI
									dateformat=new SimpleDateFormat("yyyy-mm-dd");
									yest_date = dateformat.parse(fleetSummaryEntity.getTimeStamp());
									cal = Calendar.getInstance();
									cal.setTime(yest_date);
									cal.add(cal.DAY_OF_YEAR,-1);
									fleetSummaryImpl.setResp_Date(String.valueOf(dateformat.format(cal.getTime())));
									listFleetSummaryImpl.add(fleetSummaryImpl);
	
	
									/*result1 = (Object[]) iterator.next();
	
						//	FleetSummaryImpl fleetSummaryImpl = new FleetSummaryImpl();
							if (result1[0] != null) {
								fleetSummaryImpl.setTotalOffHours(Double
										.valueOf(decimalFormat
												.format((Double) result1[0])));
							}
							if (result1[1] != null) {
								fleetSummaryImpl.setTotalIdleHours(Double
										.valueOf(decimalFormat
												.format((Double) result1[1])));
							}
							if (result1[2] != null) {
								fleetSummaryImpl.setTotalWorkingHours(Double
										.valueOf(decimalFormat
												.format((Double) result1[2])));
							}
							listFleetSummaryImpl.add(fleetSummaryImpl);
	
						}
						if(listFleetSummaryImpl.size()>0){
	
						}
						else{
							//Df20170606 @Roopa pointing to right dayagg table
	
							selectQuery = "select sum(g.engineOffHours),sum(g.EngineRunningBand1+g.EngineRunningBand2),sum(g.EngineRunningBand3+g.EngineRunningBand4+g.EngineRunningBand5+g.EngineRunningBand6+g.EngineRunningBand7+g.EngineRunningBand8)";
												fromQuery = " from AssetMonitoringFactDataDayAgg_json g, TenancyBridgeEntity b,TenancyDimensionEntity t ";
												whereQuery = " where g.tenancyId = t.tenacy_Dimension_Id and  g.timeKey = (select max(timeKey) from AssetMonitoringFactDataDayAgg_json) and b.parentId in ("
														+ tenancyListAsString + " ) and b.childId=t.tenancyId ";
	
							//Df20170704 changing the query @Roopa
	
							selectQuery = "select sum(g.engineOffHours),sum(g.EngineRunningBand1+g.EngineRunningBand2),sum(g.EngineRunningBand3+g.EngineRunningBand4+g.EngineRunningBand5+g.EngineRunningBand6+g.EngineRunningBand7+g.EngineRunningBand8)";
							fromQuery = " from AssetMonitoringFactDataDayAgg_json g, AssetOwnerSnapshotEntity aos ";
							whereQuery = " where aos.accountId in("+AccountIdListAsString+") and aos.serialNumber=g.serialNumber and g.timeKey = (select max(timeKey) from AssetMonitoringFactDataDayAgg_json)";
	
	
									 * if (alertCodeListAsString != null) { fromQuery = fromQuery +
									 * ", business_event be "; whereQuery = whereQuery +
									 * " and ae.Event_ID=be.Event_ID and be.Alert_Code in(" +
									 * alertCodeListAsString + ")";
									 * 
									 * }
	
	
							if (isOwnStock == true) {
								whereQuery = whereQuery + " AND b.childId IN ("
															+ tenancyListAsString + ")";
	
								//Df20170704 changing the query @Roopa
	
								fromQuery = fromQuery + " , AssetEntity a ";
								whereQuery = whereQuery + " AND aos.serialNumber=a.serial_number AND a.primary_owner_id="+AccountIdList.get(0)+"";
							}
	
							if ((!(assetGroupIdList == null || assetGroupIdList.isEmpty()))
									|| (!((assetTypeIdList == null) || (assetTypeIdList
											.isEmpty())))) {
	
								fromQuery = fromQuery + " , AssetClassDimensionEntity a ";
								whereQuery = whereQuery
										+ " and g.assetClassDimensionId=a.assetClassDimensionId";
							}
							if (!((assetGroupIdList == null) || (assetGroupIdList.isEmpty()))) {
								whereQuery = whereQuery + " and a.assetGroupId in ( "
										+ assetGroupIdListAsString + " )";
							}
							if (!((assetTypeIdList == null) || (assetTypeIdList.isEmpty()))) {
								whereQuery = whereQuery + " and a.assetTypeId in ( "
										+ assetTypeIdListAsString + " )";
							}
	
							if (!((customAssetGroupIdList == null) || (customAssetGroupIdList
									.isEmpty()))) {
								fromQuery = fromQuery
										+ " ,AssetCustomGroupMapping m,CustomAssetGroupEntity cu";
								whereQuery = whereQuery
										+ " and g.serialNumber=m.serial_number and m.group_id=cu.group_id and  cu.group_id in ( "
										+ customAssetGroupIdListAsString
										+ " ) and cu.active_status=1";
							}
	
	
									 * if (!((landmarkIdList == null) ||
									 * (landmarkIdList.isEmpty()))) { fromQuery = fromQuery +
									 * " , LandmarkAssetEntity l "; whereQuery = whereQuery +
									 * " and g.serialNumber=l.Serial_number and l.Landmark_id in ( "
									 * + landmarkIdListAsString + " )"; }
	
	
							if ((!((alertSeverityList == null) || (alertSeverityList
									.isEmpty())))
									|| (!((alertTypeIdList == null) || (alertTypeIdList
											.isEmpty())))) {
	
								fromQuery = fromQuery
										+ " , AssetEventEntity ae, EventEntity be ";
	
								whereQuery = whereQuery
										+ "  and g.serialNumber=ae.serialNumber and ae.eventId=be.eventId and be.eventCode in("
										+ alertCodeListAsString + ")";
	
							}
							if (!((alertSeverityList == null) || (alertSeverityList
									.isEmpty()))) {
								whereQuery = whereQuery + " and ae.eventSeverity in ( "
										+ alertSeverityListAsString + " )";
							}
	
							if (!((alertTypeIdList == null) || (alertTypeIdList.isEmpty()))) {
								whereQuery = whereQuery + " and ae.eventTypeId in( "
										+ alertTypeIdListAsString + " ) ";
							}
	
	
									 * if (!((notificationDimensionID == null) ||
									 * (notificationDimensionID .isEmpty()))) { whereQuery =
									 * whereQuery + " and n.Notification_Id in ( " +
									 * notificationDimensionIDAsString + " ) "; }
	
							finalQuery = selectQuery + fromQuery + whereQuery;
	
							// System.out.println("Fleet summary final query::"+finalQuery);
	
							//	iLogger.info("After Final Query: FleetSummaryService");
	
							iLogger.info("FleetSummaryService Final Query::"+finalQuery);
							query = session12.createQuery(finalQuery);
							//	iLogger.info("After Final Query: FleetSummaryService");
							iterator = query.list().iterator();
							Object[] result = null;
							while (iterator.hasNext()) {
								result = (Object[]) iterator.next();
								// Keerthi : 19/12/2013 : ID: 1828 : fleet utilization
								// service not returning data
								FleetSummaryImpl fleetSummaryImpl = new FleetSummaryImpl();
								if (result[0] != null) {
									fleetSummaryImpl.setTotalOffHours(Double
											.valueOf(decimalFormat
													.format((Double) result[0])));
								}
								if (result[1] != null) {
									fleetSummaryImpl.setTotalIdleHours(Double
											.valueOf(decimalFormat
													.format((Double) result[1])));
								}
								if (result[2] != null) {
									fleetSummaryImpl.setTotalWorkingHours(Double
											.valueOf(decimalFormat
													.format((Double) result[2])));
								}
								listFleetSummaryImpl.add(fleetSummaryImpl);
							}*/
								}
							}catch (Exception e) {
								fLogger.error("Exception: "+e);
							}
							finally {
								if (session12.isOpen()) {
									session12.close();
								}
								//DF20190201
							}
	
							//**************************Commenting this block of code because code was modified for fleet summary service chart data.**********//
							//				//Df20170606 @Roopa pointing to right dayagg table
							//
							//				/*selectQuery = "select sum(g.engineOffHours),sum(g.EngineRunningBand1+g.EngineRunningBand2),sum(g.EngineRunningBand3+g.EngineRunningBand4+g.EngineRunningBand5+g.EngineRunningBand6+g.EngineRunningBand7+g.EngineRunningBand8)";
							//				fromQuery = " from AssetMonitoringFactDataDayAgg_json g, TenancyBridgeEntity b,TenancyDimensionEntity t ";
							//				whereQuery = " where g.tenancyId = t.tenacy_Dimension_Id and  g.timeKey = (select max(timeKey) from AssetMonitoringFactDataDayAgg_json) and b.parentId in ("
							//						+ tenancyListAsString + " ) and b.childId=t.tenancyId ";*/
							//				
							//				//Df20170704 changing the query @Roopa
							//				
							//				selectQuery = "select sum(g.engineOffHours),sum(g.EngineRunningBand1+g.EngineRunningBand2),sum(g.EngineRunningBand3+g.EngineRunningBand4+g.EngineRunningBand5+g.EngineRunningBand6+g.EngineRunningBand7+g.EngineRunningBand8)";
							//				fromQuery = " from AssetMonitoringFactDataDayAgg_json g, AssetOwnerSnapshotEntity aos ";
							//				whereQuery = " where aos.accountId in("+AccountIdListAsString+") and aos.serialNumber=g.serialNumber and g.timeKey = (select max(timeKey) from AssetMonitoringFactDataDayAgg_json)";
							//
							//				/*
							//				 * if (alertCodeListAsString != null) { fromQuery = fromQuery +
							//				 * ", business_event be "; whereQuery = whereQuery +
							//				 * " and ae.Event_ID=be.Event_ID and be.Alert_Code in(" +
							//				 * alertCodeListAsString + ")";
							//				 * 
							//				 * }
							//				 */
							//
							//				if (isOwnStock == true) {
							//					/*whereQuery = whereQuery + " AND b.childId IN ("
							//							+ tenancyListAsString + ")";*/
							//					
							//					//Df20170704 changing the query @Roopa
							//					
							//					fromQuery = fromQuery + " , AssetEntity a ";
							//					whereQuery = whereQuery + " AND aos.serialNumber=a.serial_number AND a.primary_owner_id="+AccountIdList.get(0)+"";
							//				}
							//
							//				if ((!(assetGroupIdList == null || assetGroupIdList.isEmpty()))
							//						|| (!((assetTypeIdList == null) || (assetTypeIdList
							//								.isEmpty())))) {
							//
							//					fromQuery = fromQuery + " , AssetClassDimensionEntity a ";
							//					whereQuery = whereQuery
							//							+ " and g.assetClassDimensionId=a.assetClassDimensionId";
							//				}
							//				if (!((assetGroupIdList == null) || (assetGroupIdList.isEmpty()))) {
							//					whereQuery = whereQuery + " and a.assetGroupId in ( "
							//							+ assetGroupIdListAsString + " )";
							//				}
							//				if (!((assetTypeIdList == null) || (assetTypeIdList.isEmpty()))) {
							//					whereQuery = whereQuery + " and a.assetTypeId in ( "
							//							+ assetTypeIdListAsString + " )";
							//				}
							//
							//				if (!((customAssetGroupIdList == null) || (customAssetGroupIdList
							//						.isEmpty()))) {
							//					fromQuery = fromQuery
							//							+ " ,AssetCustomGroupMapping m,CustomAssetGroupEntity cu";
							//					whereQuery = whereQuery
							//							+ " and g.serialNumber=m.serial_number and m.group_id=cu.group_id and  cu.group_id in ( "
							//							+ customAssetGroupIdListAsString
							//							+ " ) and cu.active_status=1";
							//				}
							//
							//				/*
							//				 * if (!((landmarkIdList == null) ||
							//				 * (landmarkIdList.isEmpty()))) { fromQuery = fromQuery +
							//				 * " , LandmarkAssetEntity l "; whereQuery = whereQuery +
							//				 * " and g.serialNumber=l.Serial_number and l.Landmark_id in ( "
							//				 * + landmarkIdListAsString + " )"; }
							//				 */
							//
							//				if ((!((alertSeverityList == null) || (alertSeverityList
							//						.isEmpty())))
							//						|| (!((alertTypeIdList == null) || (alertTypeIdList
							//								.isEmpty())))) {
							//
							//					fromQuery = fromQuery
							//							+ " , AssetEventEntity ae, EventEntity be ";
							//
							//					whereQuery = whereQuery
							//							+ "  and g.serialNumber=ae.serialNumber and ae.eventId=be.eventId and be.eventCode in("
							//							+ alertCodeListAsString + ")";
							//
							//				}
							//				if (!((alertSeverityList == null) || (alertSeverityList
							//						.isEmpty()))) {
							//					whereQuery = whereQuery + " and ae.eventSeverity in ( "
							//							+ alertSeverityListAsString + " )";
							//				}
							//
							//				if (!((alertTypeIdList == null) || (alertTypeIdList.isEmpty()))) {
							//					whereQuery = whereQuery + " and ae.eventTypeId in( "
							//							+ alertTypeIdListAsString + " ) ";
							//				}
							//
							//				/*
							//				 * if (!((notificationDimensionID == null) ||
							//				 * (notificationDimensionID .isEmpty()))) { whereQuery =
							//				 * whereQuery + " and n.Notification_Id in ( " +
							//				 * notificationDimensionIDAsString + " ) "; }
							//				 */
							//				finalQuery = selectQuery + fromQuery + whereQuery;
							//
							//				// System.out.println("Fleet summary final query::"+finalQuery);
							//
							//			//	iLogger.info("After Final Query: FleetSummaryService");
							//
							//				try {
							//					iLogger.info("FleetSummaryService Final Query::"+finalQuery);
							//					Query query = session12.createQuery(finalQuery);
							//				//	iLogger.info("After Final Query: FleetSummaryService");
							//					Iterator iterator = query.list().iterator();
							//					DecimalFormat decimalFormat = new DecimalFormat("###.#");
							//					Object[] result = null;
							//					while (iterator.hasNext()) {
							//						result = (Object[]) iterator.next();
							//						// Keerthi : 19/12/2013 : ID: 1828 : fleet utilization
							//						// service not returning data
							//						FleetSummaryImpl fleetSummaryImpl = new FleetSummaryImpl();
							//						if (result[0] != null) {
							//							fleetSummaryImpl.setTotalOffHours(Double
							//									.valueOf(decimalFormat
							//											.format((Double) result[0])));
							//						}
							//						if (result[1] != null) {
							//							fleetSummaryImpl.setTotalIdleHours(Double
							//									.valueOf(decimalFormat
							//											.format((Double) result[1])));
							//						}
							//						if (result[2] != null) {
							//							fleetSummaryImpl.setTotalWorkingHours(Double
							//									.valueOf(decimalFormat
							//											.format((Double) result[2])));
							//						}
							//						listFleetSummaryImpl.add(fleetSummaryImpl);
							//					}
							//				} catch (Exception e) {
							//					e.printStackTrace();
							//				}
							//
							//				finally {
							//					/*if (session12.getTransaction().isActive()) {
							//						session12.getTransaction().commit();
							//					}*/
							//
							//					if (session12.isOpen()) {
							//						//session12.flush();
							//						session12.close();
							//					}
							//
							//				}
							//
							//			
	
						}
					}
					else {
						if (period.equalsIgnoreCase("Year")){
	
							selectQuery = "select sum(fi.EngineOffTime) as engineOffHrs, sum(fi.IdleTime) as engineIdleHrs, sum(fi.WorkingTime) as engineOnHrs";
							fromQuery = " from factInsight_dayAgg fi ";
	
							whereQuery = " where (fi.ZonalCode in ("
									+ AccountCodelistAsString + ") or fi.DealerCode in("
									+ AccountCodelistAsString + ") or fi.CustCode in("
									+ AccountCodelistAsString + ")) ";
	
							int dateAsNum = 0;
							int calYear = 0;
							String Timeperiod = null;
							Calendar cal = Calendar.getInstance();
	
							if (period.equalsIgnoreCase("Week")) {
								Timeperiod = "Week";
								calYear = cal.get(Calendar.YEAR);
								dateAsNum = cal.get(Calendar.WEEK_OF_YEAR);
							} else if (period.equalsIgnoreCase("Last Week")) {
								Timeperiod = "Week";
								calYear = cal.get(Calendar.YEAR);
								cal.set(Calendar.WEEK_OF_YEAR,
										cal.get(Calendar.WEEK_OF_YEAR) - 1);
								dateAsNum = cal.get(Calendar.WEEK_OF_YEAR);
							}
	
							else if (period.equalsIgnoreCase("Month")) {
								Timeperiod = "Month";
								calYear = cal.get(Calendar.YEAR);
								dateAsNum = cal.get(Calendar.MONTH);
							} else if (period.equalsIgnoreCase("Last Month")) {
								Timeperiod = "Month";
								calYear = cal.get(Calendar.YEAR);
								cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
								dateAsNum = cal.get(Calendar.MONTH);
							}
	
							else if (period.equalsIgnoreCase("Quarter")) {
								Timeperiod = "Quarter";
								calYear = cal.get(Calendar.YEAR);
								dateAsNum = ((cal.get(Calendar.MONTH))) / 3 + 1;
							}
	
							else if (period.equalsIgnoreCase("Last Quarter")) {
								Timeperiod = "Quarter";
								calYear = cal.get(Calendar.YEAR);
								cal.set((cal.get(Calendar.MONTH)), -1);
								dateAsNum = ((cal.get(Calendar.MONTH))) / 3 + 1;
							} else if (period.equalsIgnoreCase("Year")) {
								Timeperiod = "Year";
								dateAsNum = cal.get(Calendar.YEAR);
							} else if (period.equalsIgnoreCase("Last Year")) {
								Timeperiod = "Year";
								cal.set((cal.get(Calendar.YEAR)), -1);
								dateAsNum = cal.get(Calendar.YEAR);
							}
	
							if (dateAsNum != 0) {
	
								if (Timeperiod != null) {
									if (Timeperiod.equalsIgnoreCase("Week")) {
	
										whereQuery = whereQuery + " and fi.TxnWeek="
												+ dateAsNum + " and fi.TxnYear=" + calYear;
									}
									if (Timeperiod.equalsIgnoreCase("Month")) {
	
										whereQuery = whereQuery + " and fi.TxnMonth="
												+ dateAsNum + " and fi.TxnYear=" + calYear;
									}
									if (Timeperiod.equalsIgnoreCase("Quarter")) {
	
										whereQuery = whereQuery + " and fi.TxnQuarter="
												+ dateAsNum + " and fi.TxnYear=" + calYear;
									}
									if (Timeperiod.equalsIgnoreCase("Year")) {
	
										whereQuery = whereQuery + " and fi.TxnYear="
												+ dateAsNum;
									}
								}
	
							}
	
							if (assetTypeIdListAsString != null) {
								StringBuilder assetTypeCodeListAsString = null;
	
								List<String> assetTypeCodeList = dateUtil1
										.getAssetTypeIdList(assetTypeIdListAsString);
								//System.out.println("assetTypeCodeList "+assetTypeCodeList);
								if (assetTypeCodeList != null
										&& assetTypeCodeList.size() > 0){
									assetTypeCodeListAsString = conversionObj
											.getStringList(assetTypeCodeList);
									/*	StringBuilder MDAassetTypeCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetTypeCodeList);
							MDAassetTypeCodeList=MDAassetTypeCodeListBuilder.toString();
							System.out.println("MDAassetTypeCodeList "+MDAassetTypeCodeList);*/
								}
								whereQuery = whereQuery + " and fi.ModelCode in ("
										+ assetTypeCodeListAsString + ") ";
							}
	
							if (assetGroupIdListAsString != null) {
								List<String> assetGroupCodeList = dateUtil1
										.getAssetGroupIdList(assetGroupIdListAsString);
								//System.out.println("assetGroupCodeList "+assetGroupCodeList);
								StringBuilder assetGroupCodeListAsString = null;
								if (assetGroupCodeList != null
										&& assetGroupCodeList.size() > 0){
									assetGroupCodeListAsString = conversionObj
											.getStringList(assetGroupCodeList);
									/*StringBuilder MDAassetGroupCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetGroupCodeList);
							MDAassetGroupCodeList=MDAassetGroupCodeListBuilder.toString();
							System.out.println("MDAassetGroupCodeList "+MDAassetGroupCodeList);*/
								}
								whereQuery = whereQuery + " and fi.ProfileCode in ("
										+ assetGroupCodeListAsString + ") ";
	
							}
	
							/*
							 * if ((!((alertSeverityList == null) || (alertSeverityList
							 * .isEmpty()))) || (!((alertTypeIdList == null) ||
							 * (alertTypeIdList .isEmpty())))) { fromQuery
							 * =fromQuery+", alertInsight_dayAgg ai ";
							 * 
							 * if (Timeperiod != null) { if
							 * (Timeperiod.equalsIgnoreCase("Week")) {
							 * 
							 * whereQuery = whereQuery +
							 * " and fi.AssetID=ai.AssetID and fi.TxnWeek=ai.TxnWeek"; } if
							 * (Timeperiod.equalsIgnoreCase("Month")) {
							 * 
							 * whereQuery = whereQuery +
							 * " and fi.AssetID=ai.AssetID and fi.TxnMonth=ai.TxnMonth"; }
							 * if (Timeperiod.equalsIgnoreCase("Quarter")) {
							 * 
							 * whereQuery = whereQuery +
							 * "  and fi.AssetID=ai.AssetID and fi.TxnQuarter=ai.TxnQuarter"
							 * ; } if (Timeperiod.equalsIgnoreCase("Year")) {
							 * 
							 * whereQuery = whereQuery +
							 * " and fi.AssetID=ai.AssetID and fi.TxnYear=ai.TxnYear"; } }
							 */
	
							/*
							 * if (alertCodeListAsString != null) {
							 * 
							 * List<Integer>
							 * eventIdList=dateUtil1.getEventIdListForAlertCodes
							 * (alertCodeListAsString);
							 * 
							 * StringBuilder EventIdListAsString =
							 * conversionObj.getIntegerListString(eventIdList);
							 * 
							 * whereQuery = whereQuery + " and ai.AlertCode in(" +
							 * EventIdListAsString + ")";
							 * 
							 * }
							 */
	
	
	
							/*
							 * if (alertTypeIdList != null && alertTypeIdList.size()>0) {
							 * StringBuilder
							 * alertTypeIdsListAsString=conversionObj.getIntegerListString
							 * (alertTypeIdList); whereQuery = whereQuery +
							 * " and ai.AlertTypeCode in (" + alertTypeIdsListAsString + ") "; }
							 * 
							 * if (alertSeverityList != null && alertSeverityList.size()>0) {
							 * StringBuilder alertSeverityiesListAsString=null;
							 * alertSeverityiesListAsString
							 * =conversionObj.getStringList(alertSeverityList); whereQuery =
							 * whereQuery + " and ai.AlertSeverity in (" +
							 * alertSeverityiesListAsString + ") "; }
							 */
	
							finalQuery = selectQuery + fromQuery + whereQuery;
	
							// System.out.println("Fleet summary final query::"+finalQuery);
	
							iLogger.info("After Final Query: FleetSummaryService");
	
							Connection prodConnection = null;
							Statement statement = null;
							ResultSet rs = null;
	
							try {
								iLogger.info("After Final Query: FleetSummaryService");
	
								ConnectMySQL connMySql = new ConnectMySQL();
								prodConnection = connMySql.getProdDb2Connection();
								statement = prodConnection.createStatement();
	
								rs = statement.executeQuery(finalQuery);
	
								while (rs.next()) {
	
									FleetSummaryImpl fleetSummaryImpl = new FleetSummaryImpl();
	
									fleetSummaryImpl.setTotalOffHours(rs
											.getDouble("engineOffHrs"));
									fleetSummaryImpl.setTotalIdleHours(rs
											.getDouble("engineIdleHrs"));
									fleetSummaryImpl.setTotalWorkingHours(rs
											.getDouble("engineOnHrs"));
	
									listFleetSummaryImpl.add(fleetSummaryImpl);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
	
							finally {
								if (rs != null)
									try {
										rs.close();
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
	
								if (prodConnection != null) {
									try {
										prodConnection.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
	
							}
							//here
						}
						else{
							//MOOL DB changes
							Connection prodConn = null;
							Statement stmnt = null;
							ResultSet res = null;
							try{
								ConnectMySQL connSql = new ConnectMySQL();
								prodConn = connSql.getConnection();
								stmnt = prodConn.createStatement();
								//HashMap<String,Double> MDAOutputMap = null;
								//For accountFilter filling
								if(AccountIdList!=null && AccountIdList.size()>0){
									String acntTncyQuery = "select Tenancy_Type_Name from tenancy_type where Tenancy_Type_ID in (select Tenancy_Type_ID from tenancy where Tenancy_ID in (select Tenancy_ID from account_tenancy where Account_ID='"
											+ AccountIdList.get(0) + "'))";
									System.out.println("acntTncyQuery "+acntTncyQuery);
									iLogger.info("acntTncyQuery "+acntTncyQuery);
									res=stmnt.executeQuery(acntTncyQuery);
									String tenancy_type_name="";
									if(res.next())
									{
										tenancy_type_name=res.getString("Tenancy_Type_Name");
									}
									String accountFilter="";
									if(tenancy_type_name!=null && !tenancy_type_name.equalsIgnoreCase(""))
									{
										if(tenancy_type_name.equalsIgnoreCase("Global"))
										{
											accountFilter=null;
										}
										else if(tenancy_type_name.equalsIgnoreCase("Regional"))
										{
											accountFilter="RegionCode";
										}
										else if(tenancy_type_name.equalsIgnoreCase("Zonal"))
										{
											accountFilter="ZonalCode";
										}
										else if(tenancy_type_name.equalsIgnoreCase("Dealer"))
										{
											accountFilter="DealerCode";
										}
										else if(tenancy_type_name.equalsIgnoreCase("Customer"))
										{
											accountFilter="CustCode";
										}
										else
										{
											accountFilter="";
										}
										int MDAstock=0;
										if(isOwnStock)
										{
											MDAstock=1;
										}
										if (assetTypeIdListAsString != null) {
											StringBuilder assetTypeCodeListAsString = null;
	
											List<String> assetTypeCodeList = dateUtil1
													.getAssetTypeIdList(assetTypeIdListAsString);
											//System.out.println("assetTypeCodeList "+assetTypeCodeList);
											if (assetTypeCodeList != null
													&& assetTypeCodeList.size() > 0){
												assetTypeCodeListAsString = conversionObj
														.getStringList(assetTypeCodeList);
												StringBuilder MDAassetTypeCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetTypeCodeList);
												MDAassetTypeCodeList=MDAassetTypeCodeListBuilder.toString();
												MDAassetTypeCodeList=ListToStringConversion.removeLastComma(MDAassetTypeCodeList);
												//System.out.println("MDAassetTypeCodeList "+MDAassetTypeCodeList);
											}
											/*whereQuery = whereQuery + " and fi.ModelCode in ("
									+ assetTypeCodeListAsString + ") ";*/
										}
	
										if (assetGroupIdListAsString != null) {
											List<String> assetGroupCodeList = dateUtil1
													.getAssetGroupIdList(assetGroupIdListAsString);
											//System.out.println("assetGroupCodeList "+assetGroupCodeList);
											StringBuilder assetGroupCodeListAsString = null;
											if (assetGroupCodeList != null
													&& assetGroupCodeList.size() > 0){
												assetGroupCodeListAsString = conversionObj
														.getStringList(assetGroupCodeList);
												StringBuilder MDAassetGroupCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetGroupCodeList);
												MDAassetGroupCodeList=MDAassetGroupCodeListBuilder.toString();
												MDAassetGroupCodeList=ListToStringConversion.removeLastComma(MDAassetGroupCodeList);
												//System.out.println("MDAassetGroupCodeList "+MDAassetGroupCodeList);
											}
											/*whereQuery = whereQuery + " and fi.ProfileCode in ("
									+ assetGroupCodeListAsString + ") ";*/
	
										}
	
										//	FleetSummaryImpl MDAResponse=new FleetSummaryImpl();
										ObjectMapper mapper = new ObjectMapper();
	
	
										try{
											String url=
													//20220705: Dhiraj K : Changes for AWS ip and port changes
													//"http://10.179.12.25:26030/MoolDAReports/LLFleetSummaryService/getLLFleetSummary?accountFilter="
													//"http://10.210.196.206:26030/MoolDAReports/LLFleetSummaryService/getLLFleetSummary?accountFilter="  //CR337.o
													"http://"+connIP+":"+connPort+"/MoolDAReports/LLFleetSummaryService/getLLFleetSummary?accountFilter=" //CR337.n
															+ accountFilter
															+ "&accountIDList="
															+ MDAaccountIdListAsString
															+ "&period="
															+ period
															+ "&modelCodeList="
															+ MDAassetTypeCodeList
															+ "&profileCodeList="
															+ MDAassetGroupCodeList
															+ "&isOwnStock="
															+ MDAstock
															+ "&loginID="
															+ contactId
															+ "&countryCode=null";
											//System.out.println("MDA FleetSummaryService URL="+url);
											iLogger.info("MDA FleetSummaryService URL : "+url);
											URL MDAUrl = new URL(url);
											HttpURLConnection conn =(HttpURLConnection) MDAUrl.openConnection();
											conn.setRequestMethod("GET"); 
											conn.setRequestProperty("Accept", "application/json");
											if (conn.getResponseCode() != 200  && conn.getResponseCode() != 204) {
												iLogger.info("MDAReports report status: FAILURE for FleetSummaryService Report contactId:"+contactId+" ::Response Code:"+conn.getResponseCode());
												throw new RuntimeException("Failed : HTTP error code : " +conn.getResponseCode()); 
											}
											iLogger.info("MDAReports report status: SUCCESS for FleetSummaryService Report contactId:"+contactId+" ::Response Code:"+conn.getResponseCode());
											BufferedReader br = new BufferedReader(new  InputStreamReader((conn.getInputStream())));
	
											System.out.println("MDA FleetSummaryService Output from Server .... \n");
											while ((MDAoutput = br.readLine()) != null) { 
												//System.out.println("MDA json output "+MDAoutput); 
												iLogger.info("MDA FleetSummaryService json output "+MDAoutput);
												MDAresult =MDAoutput; } 
											MDAOutputMap = new Gson().fromJson(MDAresult, new TypeToken<HashMap<String, Double>>(){}.getType());
											//MDAResponse = mapper.convertValue(MDAOutputMap, FleetSummaryImpl.class);
											MDAResponse.setTotalIdleHours(MDAOutputMap.get("idletime"));
											MDAResponse.setTotalOffHours(MDAOutputMap.get("EngineOffTime"));
											MDAResponse.setTotalWorkingHours(MDAOutputMap.get("WorkingTime"));
											//System.out.println("MDAResponse outmap : "+MDAResponse);
											iLogger.info("MDAResponse outmap : "+MDAResponse);
											listFleetSummaryImpl.add(MDAResponse);
											conn.disconnect();
										}catch(Exception e)
										{
											e.printStackTrace();
											fLogger.fatal("Error occured while connecting to Mongo DB "+e.getMessage());
										}
									}
	
	
								}
								else{
									fLogger.fatal("AccountIdList is empty");
									throw new CustomFault("AccountIdList is empty");
								}
	
							}catch(Exception e)
							{	e.printStackTrace();
							fLogger.fatal("Exception occured "+e.getMessage());
							}
							finally {
								if (res != null)
									try {
										res.close();
									} catch (SQLException e1) {
										e1.printStackTrace();
									}
	
								if (stmnt != null)
									try {
										stmnt.close();
									} catch (SQLException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
	
								if (prodConn != null) {
									try {
										prodConn.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
	
							}
						}
					}//end of else
				}//end of very first if
				//20119610 Ajay this change is part of group based view enhacement
				else{
					if (period.equalsIgnoreCase("Today")) {
						//Fetching yesterday's data.
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DATE, -1);
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						String yesterdayDate = dateFormat.format(cal.getTime());
	
						// yesterdayDate="2021-01-13";
						String countryCode = null;
						String customAssetGroupIdListAsStringg;
						customAssetGroupIdListAsStringg=	AssetUtil.getFormatedVinsToQuery(customAssetGroupIdList);
						if(customAssetGroupIdListAsStringg!=null && customAssetGroupIdListAsStringg.isEmpty())
							customAssetGroupIdListAsStringg=null;
						if (assetTypeIdListAsString != null) {
							StringBuilder assetTypeCodeListAsString = null;
	
							List<String> assetTypeCodeList = dateUtil1
									.getAssetTypeIdList(assetTypeIdListAsString);
							//System.out.println("assetTypeCodeList "+assetTypeCodeList);
							if (assetTypeCodeList != null
									&& assetTypeCodeList.size() > 0){
								assetTypeCodeListAsString = conversionObj
										.getStringList(assetTypeCodeList);
								StringBuilder MDAassetTypeCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetTypeCodeList);
								MDAassetTypeCodeList=MDAassetTypeCodeListBuilder.toString();
								MDAassetTypeCodeList=ListToStringConversion.removeLastComma(MDAassetTypeCodeList);
								//System.out.println("MDAassetTypeCodeList "+MDAassetTypeCodeList);
							}
						}
	
						if (assetGroupIdListAsString != null) {
							List<String> assetGroupCodeList = dateUtil1
									.getAssetGroupIdList(assetGroupIdListAsString);
							//System.out.println("assetGroupCodeList "+assetGroupCodeList);
							StringBuilder assetGroupCodeListAsString = null;
							if (assetGroupCodeList != null
									&& assetGroupCodeList.size() > 0){
								assetGroupCodeListAsString = conversionObj
										.getStringList(assetGroupCodeList);
								StringBuilder MDAassetGroupCodeListBuilder=conversionObj.getStringWithoutQuoteList(assetGroupCodeList);
								MDAassetGroupCodeList=MDAassetGroupCodeListBuilder.toString();
								MDAassetGroupCodeList=ListToStringConversion.removeLastComma(MDAassetGroupCodeList);
							}
	
						}
	
	
						int stock=1;
						if(isOwnStock==false){
							stock=0;
						}
						String url="http://localhost:26030/MoolDAReports/LLFleetSummaryService/getLLFleetSummaryMachineGroup?accountFilter=" 
								+"null" 
								+"&accountIDList=" 
								+"null"
								+"&period=Date"
								+ "&value1="
								+ yesterdayDate
								+ "&value2="
								+ yesterdayDate
								+"&modelCodeList=" 
								+MDAassetTypeCodeList
								+"&profileCodeList=" 
								+MDAassetGroupCodeList
								+"&isOwnStock=" 
								+stock
								+"&loginID=" 
								+contactId
								+"&countryCode=" 
								+countryCode 
								+"&MachineGrpIDList=" 
								+customAssetGroupIdListAsStringg;
	
	
						//HashMap<String,Double> MDAOutputMap = null;
	
	
						/*if(countryCode != null)
								url = url+URLEncoder.encode(countryCode,"UTF-8");
							else
								url = url+countryCode;
						 */
						iLogger.info("MDA FleetSummaryService URL : "+url);
						URL MDAUrl = new URL(url);
						HttpURLConnection conn =(HttpURLConnection) MDAUrl.openConnection();
						conn.setConnectTimeout(60000); //set timeout to 1 min
						conn.setReadTimeout(60000); 
	
						conn.setRequestMethod("GET"); 
						conn.setRequestProperty("Accept", "application/json");
						if (conn.getResponseCode() != 200  && conn.getResponseCode() != 204) {
							throw new RuntimeException("Failed : HTTP error code : " +conn.getResponseCode()); 
						}
						BufferedReader br = new BufferedReader(new  InputStreamReader((conn.getInputStream())));
	
						System.out.println("MDA FleetSummaryService Output from Server .... \n");
						while ((MDAoutput = br.readLine()) != null) { 
							//System.out.println("MDA json output "+MDAoutput); 
							iLogger.info("MDA FleetSummaryService json output "+MDAoutput);
							MDAresult =MDAoutput; } 
						if(!MDAresult.contains("Error")){
							MDAOutputMap = new Gson().fromJson(MDAresult, new TypeToken<HashMap<String, Double>>(){}.getType());
							//MDAResponse = mapper.convertValue(MDAOutputMap, FleetSummaryImpl.class);
							MDAResponse.setTotalIdleHours(MDAOutputMap.get("idletime"));
							MDAResponse.setTotalOffHours(MDAOutputMap.get("EngineOffTime"));
							MDAResponse.setTotalWorkingHours(MDAOutputMap.get("WorkingTime"));
							//DF20190103:Abhishek:To display date in FleetUtilization chart in UI
							MDAResponse.setResp_Date(yesterdayDate);
							//System.out.println("MDAResponse outmap : "+MDAResponse);
							iLogger.info("MDAResponse outmap : "+MDAResponse);
							listFleetSummaryImpl.add(MDAResponse);
						}
						conn.disconnect();
	
					}}				
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				if(sessionForAccount.getTransaction().isActive()){
					sessionForAccount.getTransaction().commit();
				}if(sessionForAccount.isOpen()){
					sessionForAccount.flush();
					sessionForAccount.close();
				}
			}
	
			return listFleetSummaryImpl;
	
		}
		//*******END OF GET FleetSummaryService********************************
	
		/**
		 * method to get rolled off machines for the tenancy id list or login id
		 * @param loginId
		 * @param tenancyIdList
		 * @param fromDate 
		 * @param toDate 
		 * @param vin 
		 * @return list
		 */
		/*public List<RolledOffMachinesImpl> getRolledOffMachines(String loginId,
				List<Integer> tenancyIdList, String vin, String toDate, String fromDate) 
		{
	
			Logger iLogger = InfoLoggerClass.logger;
			long startTime = System.currentTimeMillis();
			List<RolledOffMachinesImpl> implList = null;
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			try {
				//get Client Details
				Properties prop = new Properties();
				String clientName=null;
	
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				clientName= prop.getProperty("ClientName");      
				IndustryBO industryBoObj = new IndustryBO();
				ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
				//END of get Client Details			
	
				if (tenancyIdList != null) 
				{
					int isJcbUser=0;
	
					//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
					Query roleQ= session.createQuery(" from ContactEntity where contact_id='"+loginId+"'");
					Iterator roleItr = roleQ.list().iterator();
					while(roleItr.hasNext())
					{
						ContactEntity contact = (ContactEntity) roleItr.next();
						if( (contact.getRole().getRole_name().equalsIgnoreCase("JCB Admin")) ||
							(contact.getRole().getRole_name().equalsIgnoreCase("JCB HO"))	||
							//DefectId:20150518 @Suprava Remove JCB RO for non communicated machines 
							//(contact.getRole().getRole_name().equalsIgnoreCase("JCB RO")) ||
							(contact.getRole().getRole_name().equalsIgnoreCase("Customer Care")) )
						{
							isJcbUser=1;
						}
					}
					//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
	
					ListToStringConversion conversionObj = new ListToStringConversion();
					String tenancyIdString = conversionObj.getIntegerListString(
							tenancyIdList).toString();
	
	
					String serialNumber = null;
					Object[] resultSet = null,assetResult = null;
					ProductEntity product = null;
					AssetGroupEntity assetGroup = null;
					AssetTypeEntity assetType = null;
					EngineTypeEntity engineType = null;
					RolledOffMachinesImpl implObj = null;
	
					Iterator iterProductId= null;
					//DefectId:20140922 new Search Tab @Suprava
					String finalQuery =null;
					String selectQuery =null;
					String fromQuery =null;
					String whereQuery=null;
					String orderByQuery=null;
					String serNumber=null;
					String fromDate1 = fromDate + " 00:00:00";
					String toDate1 = toDate + " 23:59:59";
					List<String> serialNumberList = new ArrayList<String>();
					String serialNumberAsStringList =null;
	
					//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
					if(isJcbUser==1)
					//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
					{
						if(vin!=null && !vin.isEmpty())
						{
							Query q = session.createQuery("SELECT serialNumber,registrationDate from AssetControlUnitEntity where serialNumber like'%"+vin+"%'");
				        	Iterator itrtr = q.list().iterator();
				        	Object[] resultset = null;
				        	while(itrtr.hasNext())
				        	{
				        		//serNumber=(String)itrtr.next();
								resultset = (Object[]) itrtr.next();
								//	if (resultset[0] != null) {
										serialNumberList.add((String) resultset[0]);
								//	}
				        	}
	
				        	if(serialNumberList!=null && serialNumberList.size()>0){
				    			serialNumberAsStringList = conversionObj.getStringList(
				    					serialNumberList).toString();
		                    }
		                    else
		                    {
		                    	iLogger.info("Machine Number Doesn't exit");
		                        return implList;	
		                    }
	
						}
						selectQuery ="SELECT acu.serialNumber,acu.simNo,acu.imeiNo,acu.registrationDate";
						fromQuery =" FROM AssetControlUnitEntity acu";
						whereQuery =" WHERE acu.serialNumber NOT IN "
							// + " (SELECT distinct(amh.serialNumber) FROM AssetMonitoringHeaderEntity amh)";
								//2014-09-29: Modified the query to take the data from Remote Year so that the machine appears on the New Machine tab or on the fleet dashboard. - Deepthi
							//	+ " (SELECT distinct(amh.serialNumber) FROM AssetMonitoringFactDataYearAgg amh)";
							// 20150130 @Suprava new machine tab changes
							+ " (SELECT distinct(amh.serialNumber) FROM AssetMonitoringSnapshotEntity amh)";
	
						if(serialNumberAsStringList!=null && !serialNumberAsStringList.isEmpty())
						{
							whereQuery = whereQuery + " and acu.serialNumber in("+ serialNumberAsStringList +")";
						}
						else if(fromDate!=null && toDate!=null)
						{
							whereQuery =whereQuery+ " and acu.registrationDate >= '"+fromDate1+"%' and acu.registrationDate <='"+toDate1+"%' ";
						}
						else
						{
							whereQuery =whereQuery +"";
						}
						orderByQuery=" ORDER BY acu.registrationDate desc";
						//DF20140602 - Rajani Nagaraju - Changing order by from VIN asc to registration date desc - Newly registered machines should come first
						String finalQuery = "SELECT acu.serialNumber,acu.simNo,acu.imeiNo"					
		//					+ " FROM AssetEntity a RIGHT OUTER JOIN a.serial_number acu"
							+ " FROM AssetControlUnitEntity acu"
							+ " WHERE acu.serialNumber NOT IN "
							+ " (SELECT distinct(amh.serialNumber) FROM AssetMonitoringHeaderEntity amh)"
							+ " ORDER BY acu.registrationDate desc"; 
						finalQuery= selectQuery+fromQuery+whereQuery+orderByQuery;
						Query serialNoQuery = session.createQuery(finalQuery);
	
						Iterator iterSerialNo = serialNoQuery.list().iterator();
	
	
						while (iterSerialNo.hasNext()) {
							resultSet = (Object[]) iterSerialNo.next();
							implObj = new RolledOffMachinesImpl();
							if (resultSet[0] != null) {
								serialNumber = (String)resultSet[0];
								implObj.setSerialNumber((String)resultSet[0]);
							}					
							if (resultSet[1] != null) {
								implObj.setSimNumber((String)resultSet[1]);
							}
							if (resultSet[2] != null) {
								implObj.setIMEINumber((String)resultSet[2]);
							}	
							if (resultSet[3] != null) {
								Timestamp registrationDate = (Timestamp)resultSet[3];
								String regDate1 = registrationDate.toString();
								implObj.setRegDate((regDate1));
							}
							implObj.setMachineName("");
							implObj.setProfileName("");
							implObj.setModelName("");
							implObj.setEngineName("");
	
		//					Keerthi : 28/10/13 : getting product details
							finalQuery = "SELECT a.nick_name,a.productId FROM AssetEntity a WHERE a.serial_number ='"+serialNumber+"'";
							serialNoQuery = session.createQuery(finalQuery);				
							iterProductId = serialNoQuery.list().iterator();
	
							while(iterProductId.hasNext()){
								assetResult = (Object[])iterProductId.next();
								if(assetResult[0] !=null){
									implObj.setMachineName((String)assetResult[0]);
								}
								if(assetResult[1] !=null){
									product = (ProductEntity) assetResult[1];
									if (product != null) {
										assetGroup = product.getAssetGroupId();
										if (assetGroup != null) {
											implObj.setAssetGroupId(assetGroup.getAsset_group_id());
											implObj.setProfileName(assetGroup.getAsset_group_name());
										}
										assetType = product.getAssetTypeId();
										if (assetType != null) {
											implObj.setAssetTypeId(assetType.getAsset_type_id());
											implObj.setModelName(assetType.getAsset_type_name());
										}
										engineType = product.getEngineTypeId();
										if (engineType != null) {
											implObj.setEngineTypeId(engineType.getEngineTypeId());
											implObj.setEngineName(engineType.getEngineTypeName());
										}
									}
								}
							}		
							if (implList == null) {
								implList = new ArrayList<RolledOffMachinesImpl>();
							}	
	
							implList.add(implObj);													
						}	
					}
	
					//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
					else
					{
						if (implList == null) {
							implList = new ArrayList<RolledOffMachinesImpl>();
						}
						// 20150130 @Suprava new machine tab changes sn
						HashMap<String,String> assetOwnerMap = new HashMap<String,String>();
						List<String> serialNumList= new LinkedList<String>();
						Query assetOwnerSnapshotQ = session.createQuery(" select a.serialNumber,a.assetOwnershipDate from AssetOwnerSnapshotEntity a,AccountTenancyMapping b" +
								" where a.accountId=b.account_id" +
						        " and b.tenancy_id in ("+tenancyIdString+") ");
						Iterator assetOwnerItr = assetOwnerSnapshotQ.list().iterator();
						Object[] result=null;
						while(assetOwnerItr.hasNext())
						{
							result = (Object[])assetOwnerItr.next();
							String serialNum =null;
							Date ownerDate =null;
							String ownerShipDate =null;
							if(result[0]!=null)
							{
								serialNum =(String) result[0];
							}
							if(result[1]!=null)
							{
								ownerDate =(Date)result[1];
							}
							ownerShipDate =ownerDate.toString();
							assetOwnerMap.put(serialNum, ownerShipDate);
						}
						for(String key:assetOwnerMap.keySet())
						{
							String assetOwnerDate=assetOwnerMap.get(key);
	
							Query assetMonitoringSnapshotQ = session.createQuery(" select a.serialNumber from AssetMonitoringSnapshotEntity a " +
									" where a.serialNumber='"+key+"' " +
							        " and a.transactionTime >= '"+assetOwnerDate+"' ");
							int list_size =assetMonitoringSnapshotQ.list().size();	
							if(list_size > 0)
							{
								continue;
							}
							else
							{
							  Query assetMonitoringDetailQ = session.createQuery(" select a.serialNumber,a.transactionTime,b.parameterValue,a.fuelLevel " +
								    	" from AssetMonitoringSnapshotEntity a ," +
										" AssetMonitoringDetailEntity b " +
										" where a.serialNumber='"+key+"' and a.transactionNumber=b.transactionNumber and b.parameterId=4 ");
							  Iterator query4Itr = assetMonitoringDetailQ.list().iterator();
								while (query4Itr.hasNext())
								{
									Object[] result1 = (Object[]) query4Itr.next();
									String lastReportedTime=null;
									AssetEntity assetContrl =null;
									String chmrvalue =null;
									String fuelLevel=null;
									implObj = new RolledOffMachinesImpl();
									if(result1[0]!=null)
									{
										assetContrl= (AssetEntity)result1[0];
										implObj.setSerialNumber(assetContrl.getSerial_number().getSerialNumber());
	
									}
									if(result1[1]!=null)
									{
										Timestamp lastReported = (Timestamp)result1[1];
										lastReportedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastReported);
										implObj.setLastReportedTime(lastReportedTime);
									}
									if(result1[2]!=null)
									{
										chmrvalue = (String)result1[2];
										implObj.setEnginehours(chmrvalue);
									}
									if(result1[3]!=null)
									{
										fuelLevel =(String)result1[3];
										implObj.setFuelLevel(fuelLevel);
									}
									implList.add(implObj);
								}
							}
	
						}
						// 20150130 @Suprava new machine tab changes
						for(Entry<String, String> entry : assetOwnerMap.entrySet()) {
							    String serlNumber = entry.getKey();
							    String value = entry.getValue();
							    System.out.println(key + " " + value);
							}
	
						String queryString = " select a, r, r.transactionTime " +
						" from AssetMonitoringSnapshotEntity r RIGHT OUTER JOIN r.serialNumber a ," +
						" AccountTenancyMapping b  " +
						" where a.primary_owner_id = b.account_id " +
						" and b.tenancy_id in ("+tenancyIdString+") " +
						" and a.serial_number NOT IN " +
								" ( select distinct w.serialNumber from AssetMonitoringFactDataYearAgg w, " +
									" TenancyDimensionEntity y where w.tenancyId= y.tenacy_Dimension_Id " +
									" and y.tenancyId in ("+tenancyIdString+") )" ;
	
						if(vin!=null && !vin.isEmpty())
						{
							vin=vin.replaceAll("\\s+","");
							queryString = queryString + " and a.serial_number like '%"+vin+"%' ";
						}
	
	
						queryString = queryString + " group by a.serial_number ";
	
						Query assetQ = session.createQuery(queryString);
						Iterator assetItr = assetQ.list().iterator();
						Object[] result=null;
						while(assetItr.hasNext())
						{
							result = (Object[])assetItr.next();
							String lastReportedTime=null;
							AssetEntity assetContrl = (AssetEntity)result[0];
	
							if(result[2]!=null)
							{
								Timestamp lastReported = (Timestamp)result[2];
								lastReportedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastReported);
							}
	
							implObj = new RolledOffMachinesImpl();
							implObj.setSerialNumber(assetContrl.getSerial_number().getSerialNumber());
							implObj.setLastReportedTime(lastReportedTime);
	
							int transactionNumber=0;
							if(result[1]!=null)
							{
								AssetMonitoringSnapshotEntity txn = (AssetMonitoringSnapshotEntity)result[1];
								transactionNumber=txn.getTransactionNumber().getTransactionNumber();
							}
	
							if(transactionNumber!=0)
							{
								Query monitoringDataQ = session.createQuery(" from AssetMonitoringDetailEntity where transactionNumber="+transactionNumber +
										" and parameterId in (4,5) ");
								Iterator monitoringDataItr = monitoringDataQ.list().iterator();
								while(monitoringDataItr.hasNext())
								{
									AssetMonitoringDetailEntity detailData = (AssetMonitoringDetailEntity)monitoringDataItr.next();
									if(detailData.getParameterId().getParameterId()==4)
										implObj.setEnginehours(detailData.getParameterValue());
									if(detailData.getParameterId().getParameterId()==5)
										implObj.setFuelLevel(detailData.getParameterValue());
								}
							}
	
							implList.add(implObj);
						}
	
	
					}
					//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
	
					if (implList != null) {
						iLogger.info("serial no list size  "+implList.size());
					}
				}
			    }
			catch (Exception e) {
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
			long endTime = System.currentTimeMillis();
			long totalTimeTaken = endTime - startTime;
			iLogger.info("totalTimeTaken  " + totalTimeTaken);
			iLogger.info("Exiting getRolledOffMachines()");
			return implList;
		}*/
	
	
		//20160713 - @suresh implementing DAL Layer for AMS segregating business logic and database calls
		/**
		 * method to get rolled off machines for the tenancy id list or login id
		 * @param loginId
		 * @param tenancyIdList
		 * @param fromDate 
		 * @param toDate 
		 * @param vin 
		 * @return list
		 * @throws CustomFault 
		 */
		public List<RolledOffMachinesImpl> getRolledOffMachines(String loginId,
				List<Integer> tenancyIdList, String vin, String toDate, String fromDate) throws CustomFault 
				{
	
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			long startTime = System.currentTimeMillis();
			List<RolledOffMachinesImpl> implList = null;
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			ContactEntity contact=null;
			try {
				//get Client Details
				Properties prop = new Properties();
				String clientName=null;
	
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				clientName= prop.getProperty("ClientName");      
				IndustryBO industryBoObj = new IndustryBO();
				ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
				//END of get Client Details			
	
				if (tenancyIdList != null) 
				{
					int isJcbUser=0;
	
					//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
					Query roleQ= session.createQuery(" from ContactEntity where contact_id='"+loginId+"'");
					Iterator roleItr = roleQ.list().iterator();
					while(roleItr.hasNext())
					{
						contact = (ContactEntity) roleItr.next();
						if( (contact.getRole().getRole_name().equalsIgnoreCase("JCB Admin")) ||
								(contact.getRole().getRole_name().equalsIgnoreCase("JCB HO"))	||
								//DefectId:20150518 @Suprava Remove JCB RO for non communicated machines 
								(contact.getRole().getRole_name().equalsIgnoreCase("Super Admin")) ||
								(contact.getRole().getRole_name().equalsIgnoreCase("Customer Care")) )
						{
							isJcbUser=1;
						}
					}
					//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
	
					ListToStringConversion conversionObj = new ListToStringConversion();
					String tenancyIdString = conversionObj.getIntegerListString(
							tenancyIdList).toString();
	
	
					String serialNumber = null;
					Object[] resultSet = null,assetResult = null;
					ProductEntity product = null;
					AssetGroupEntity assetGroup = null;
					AssetTypeEntity assetType = null;
					EngineTypeEntity engineType = null;
					RolledOffMachinesImpl implObj = null;
	
					Iterator iterProductId= null;
					//DefectId:20140922 new Search Tab @Suprava
					String finalQuery =null;
					String selectQuery =null;
					String fromQuery =null;
					String whereQuery=null;
					String orderByQuery=null;
					String serNumber=null;
					String fromDate1 = fromDate + " 00:00:00";
					String toDate1 = toDate + " 23:59:59";
					List<String> serialNumberList = new ArrayList<String>();
					String serialNumberAsStringList =null;
					//DF20180918-Mani-New machines tab FUTURE dates issue,FIX: change MM to mm in hh:mm:ss in dateStr
					DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
					if(isJcbUser==1)
						//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
					{
						if(vin!=null && !vin.isEmpty() && !vin.equalsIgnoreCase("ExcelDownload"))
						{
							Query q = session.createQuery("SELECT serialNumber,registrationDate from AssetControlUnitEntity where serialNumber like'%"+vin+"%'");
							Iterator itrtr = q.list().iterator();
							Object[] resultset = null;
							while(itrtr.hasNext())
							{
								//serNumber=(String)itrtr.next();
								resultset = (Object[]) itrtr.next();
								//	if (resultset[0] != null) {
								serialNumberList.add((String) resultset[0]);
								//	}
							}
	
							if(serialNumberList!=null && serialNumberList.size()>0){
								serialNumberAsStringList = conversionObj.getStringList(
										serialNumberList).toString();
							}
							else
							{
								iLogger.info("Machine Number Doesn't exit");
								return implList;	
							}
	
						}
	
	
						/*selectQuery ="SELECT acu.serialNumber,acu.simNo,acu.imeiNo,acu.registrationDate";
						fromQuery =" FROM AssetControlUnitEntity acu";
						whereQuery =" WHERE acu.serialNumber NOT IN "
							// + " (SELECT distinct(amh.serialNumber) FROM AssetMonitoringHeaderEntity amh)";
								//2014-09-29: Modified the query to take the data from Remote Year so that the machine appears on the New Machine tab or on the fleet dashboard. - Deepthi
							//	+ " (SELECT distinct(amh.serialNumber) FROM AssetMonitoringFactDataYearAgg amh)";
							// 20150130 @Suprava new machine tab changes
							+ " (SELECT distinct(amh.serialNumber) FROM AssetMonitoringSnapshotEntity amh)";
	
						if(serialNumberAsStringList!=null && !serialNumberAsStringList.isEmpty())
						{
							whereQuery = whereQuery + " and acu.serialNumber in("+ serialNumberAsStringList +")";
						}
						else if(fromDate!=null && toDate!=null)
						{
							whereQuery =whereQuery+ " and acu.registrationDate >= '"+fromDate1+"%' and acu.registrationDate <='"+toDate1+"%' ";
						}
						else
						{
							whereQuery =whereQuery +"";
						}
						orderByQuery=" ORDER BY acu.registrationDate desc ";*/
	
	
						// S Suresh new DAL Layer implementation
						String txnKey = "AssetDetailsBO:getRolledMachines ";
						DynamicAMS_DAL dalObj= new DynamicAMS_DAL();
						List<AsseControlUnitDAO> acuList =dalObj.getAMSDataOn_RegistrationDate(txnKey,fromDate,toDate,serialNumberAsStringList);
						//DF20140602 - Rajani Nagaraju - Changing order by from VIN asc to registration date desc - Newly registered machines should come first
						/*String finalQuery = "SELECT acu.serialNumber,acu.simNo,acu.imeiNo"					
		//					+ " FROM AssetEntity a RIGHT OUTER JOIN a.serial_number acu"
							+ " FROM AssetControlUnitEntity acu"
							+ " WHERE acu.serialNumber NOT IN "
							+ " (SELECT distinct(amh.serialNumber) FROM AssetMonitoringHeaderEntity amh)"
							+ " ORDER BY acu.registrationDate desc"; */
						/*finalQuery= selectQuery+fromQuery+whereQuery+orderByQuery;
						Query serialNoQuery = session.createQuery(finalQuery);*/
	
						Iterator iterSerialNo = acuList.iterator();
	
	
						while (iterSerialNo.hasNext()) {
							AsseControlUnitDAO daoObject = (AsseControlUnitDAO)iterSerialNo.next();;
							//resultSet = (Object[]) iterSerialNo.next();
							//2021-03-18 : Shajesh : addition of dealer name in NMT
							implObj = new RolledOffMachinesImpl();
							if (daoObject.getSerialNumber() != null) {
								serialNumber = daoObject.getSerialNumber().split("~")[0];
								iLogger.info("---------------serialNumber  --------------------------"+serialNumber);
								String accountName=daoObject.getSerialNumber().split("~")[1];
								iLogger.info("---------------accountName  --------------------------"+accountName);
								//2021-03-18:Shajesh : add dealer name in NMT
								String dealerName =daoObject.getSerialNumber().split("~")[2];
								iLogger.info("---------------dealerName  --------------------------"+dealerName);
								implObj.setSerialNumber(serialNumber+"~"+accountName+"~"+dealerName);
							}					
							if (daoObject.getSimNo() != null) {
								implObj.setSimNumber(daoObject.getSimNo());
							}
							if (daoObject.getImeiNo() != null) {
								implObj.setIMEINumber(daoObject.getImeiNo());
							}	
							if (daoObject.getRegistrationDate() != null) {
								Timestamp registrationDate = daoObject.getRegistrationDate() ;
								String regDate1 = registrationDate.toString();
								implObj.setRegDate((regDate1));
							}
	
							//DF20200429 - Zakir : setting Comments to impl obj
							if (daoObject.getComment() != null) {
								implObj.setComment(daoObject.getComment());
							}
							//CR481-20240731-Sai Divya-Status,proposedFWVersion,fWVersion columns are added to NMT
//							if (daoObject.getfWVersion() != null) {
//								implObj.setfWVersion(daoObject.getfWVersion());
//							}
//							if (daoObject.getProposedFWVersion() != null) {
//								implObj.setProposedFWVersion(daoObject.getProposedFWVersion());
//							}
//							if (daoObject.getStatus() != null) {
//								implObj.setStatus(daoObject.getStatus());
//							}
	
							implObj.setMachineName("");
							implObj.setProfileName("");
							implObj.setModelName("");
							implObj.setEngineName("");
	
							//					Keerthi : 28/10/13 : getting product details
	
							//Df20160811 @Roopa for excel Download of new machines below fields are not required.
	
							if(vin!=null && !vin.equalsIgnoreCase("ExcelDownload")){
								finalQuery = "SELECT a.nick_name,a.productId FROM AssetEntity a WHERE a.serial_number ='"+serialNumber+"'";
								Query serialNoQuery = session.createQuery(finalQuery);				
								iterProductId = serialNoQuery.list().iterator();
	
								while(iterProductId.hasNext()){
									assetResult = (Object[])iterProductId.next();
									if(assetResult[0] !=null){
										implObj.setMachineName((String)assetResult[0]);
									}
									if(assetResult[1] !=null){
										product = (ProductEntity) assetResult[1];
										if (product != null) {
											assetGroup = product.getAssetGroupId();
											if (assetGroup != null) {
												implObj.setAssetGroupId(assetGroup.getAsset_group_id());
												implObj.setProfileName(assetGroup.getAsset_group_name());
											}
											assetType = product.getAssetTypeId();
											if (assetType != null) {
												implObj.setAssetTypeId(assetType.getAsset_type_id());
												implObj.setModelName(assetType.getAsset_type_name());
											}
											engineType = product.getEngineTypeId();
											if (engineType != null) {
												implObj.setEngineTypeId(engineType.getEngineTypeId());
												implObj.setEngineName(engineType.getEngineTypeName());
											}
										}
									}
								}
							}
							if (implList == null) {
								implList = new ArrayList<RolledOffMachinesImpl>();
							}	
	
							implList.add(implObj);													
						}	
					}
	
					//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
					else
					{
						if (implList == null) {
							implList = new ArrayList<RolledOffMachinesImpl>();
						}
	
						/**
						 * New Machines query(Dealer,Customer,RO,Dealer Admin) changed to improve the performance.
						 * Using JDBC Query
						 * Previous existing code was commented.
						 * Modified by KO369761 on 22-10-18
						 **/
						Connection prodConnection = null,prodConnection1 = null;
						Statement statement = null, statement1 = null;
						ResultSet rs = null,rs1 = null;
	
						try{
							ConnectMySQL connMySql = new ConnectMySQL();
							prodConnection = connMySql.getConnection();
							statement = prodConnection.createStatement();
							prodConnection1 = connMySql.getConnection();
							statement1 = prodConnection1.createStatement();
							//DF-2019-10-04- Abhishek - Code changes done to handle a scearnio where the machine is present in the AOS but not in the AMs.
							/*String newMachinesQuery = "select a.serial_number, ams.TxnData, ams.Latest_Transaction_Timestamp from asset_owner_snapshot a inner join asset_monitoring_snapshot ams on a.Serial_Number=ams.Serial_Number and a.Ownership_Start_Date > ams.Latest_Transaction_Timestamp, account_tenancy b where a.account_id=b.account_id and b.tenancy_id in ("
									+ tenancyIdString + ")";*/
							//DF-2020-02-04- Mamatha - Code changes done to handle a scearnio where the machine is present in the AOS and AMs but a.Ownership_Start_Date is greater than Latest_Transaction_Timestamp.
							//@Shajesh : 20201215 : correcting Syntax issue 
							String newMachinesQuery = "select a.serial_number,b.TxnData, b.Latest_Transaction_Timestamp ,croe.Dealer_Name,if(ast.serial_number is null, 'Machine is not yet rolled off','Machine has not communicated after owner movement') as Comment from "+
									" (select Serial_Number,Ownership_Start_Date from asset_owner_snapshot aos,account_tenancy at " +
									"where at.tenancy_id in("+ tenancyIdString +")"+
									" and aos.account_id=at.account_id) a "+
									" left outer join"+  
									" (select Serial_Number,TxnData,Latest_Transaction_Timestamp from asset_monitoring_snapshot) b "+
									" on  (a.serial_number = b.serial_number)"+
									"left outer join com_rep_oem_enhanced croe on a.serial_number=croe.serial_number "+
									"left outer join asset ast on a.Serial_number = ast.Serial_number "+
									"where (Latest_Transaction_Timestamp is null or a.Ownership_Start_Date > Latest_Transaction_Timestamp)";
	
							if(vin != null && !vin.equalsIgnoreCase("ExcelDownload")){
								newMachinesQuery = newMachinesQuery+" and a.Serial_Number like '%"+vin+"%'";
							}
							iLogger.info("********* newMachinesQuery ********* "+newMachinesQuery);
							rs = statement.executeQuery(newMachinesQuery);
	
							String serialNum =null;
							String txnMapString = null;
							Timestamp latestTransTimeStamp = null;
							HashMap<String,String> txnDataMap=new HashMap<String, String>();
							String lastReportedTime = null;
							String AMSSelectQuery=null;
							while(rs.next()){
								implObj = new RolledOffMachinesImpl();
								serialNum = rs.getString("serial_number");
								latestTransTimeStamp = rs.getTimestamp("Latest_Transaction_Timestamp");
								txnMapString = rs.getString("TxnData");
								//DF20181107 :MA369757: For RO :: to display IMEI,Registration date and SIM details
								if(serialNum!=null){
									if((contact.getRole().getRole_name().equalsIgnoreCase("JCB RO")))
									{
										AMSSelectQuery = "select SIM_No,IMEI_Number,Registration_Date "+
												"from asset_control_unit where Serial_Number=\'"+serialNum+"\';";
										rs1 = statement1.executeQuery(AMSSelectQuery);
										if(rs1.next())
										{
											implObj.setSimNumber(rs1.getString("SIM_No"));
											implObj.setIMEINumber(rs1.getString("IMEI_Number"));
											Timestamp registrationDate = rs1.getTimestamp("Registration_Date") ;
											String regDate = registrationDate.toString();
											if(fromDate!=null && toDate !=null){
												Date regdate = new Date(rs1.getTimestamp("Registration_Date").getTime());
												SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
												String formattedregdate = f.format(regdate);
												Date formattedtoDate = f.parse(toDate);
												 Date formattedfromDate = f.parse(fromDate);
												 String formattedtodate1 = f.format(formattedtoDate);
												 String formattedfromDate1 = f.format(formattedfromDate);
												 if((formattedregdate.compareTo(formattedfromDate1)<0)||(formattedregdate.compareTo(formattedtodate1)>0))
													 continue;
												
												}
											implObj.setRegDate(regDate);
										}
									}
								}
								if(txnMapString != null)
									txnDataMap = new Gson().fromJson(txnMapString, new TypeToken<HashMap<String, Object>>() {}.getType());
								else{
									implObj.setLastReportedTime("Not Communicated");	
								}
	
								if(serialNum != null)
								{
									if( rs.getString("Dealer_Name")!=null)
									implObj.setSerialNumber(serialNum+"~"+"NA"+"~"+rs.getString("Dealer_Name"));
									else
									implObj.setSerialNumber(serialNum+"~"+"NA"+"~"+"NA");
									iLogger.info("---------------serialNumber&dealername --------------------------"+serialNumber+" "+rs.getString("Dealer_Name"));
								}
								if(rs.getString("Comment")!=null)
									implObj.setComment(rs.getString("Comment"));
	
								if(latestTransTimeStamp != null){
									lastReportedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(latestTransTimeStamp);
									implObj.setLastReportedTime(lastReportedTime);
								}
	
								if(txnDataMap != null){
									implObj.setFuelLevel(txnDataMap.get("FUEL_PERCT"));
									implObj.setEnginehours(txnDataMap.get("CMH"));
								}
								if(txnMapString == null){
									implObj.setFuelLevel("Not Communicated");
									implObj.setEnginehours("Not Communicated");
								}
	
								implList.add(implObj);
							}
	
							if(implList != null)
								iLogger.info("serial no list size  "+implList.size());
	
						}
	
						/**
						 * KO369761 - DF20181114 Capturing communication link failure
						 * exception and throwing as custom fault exception to UI.
						 ***/
						catch(CommunicationsException e){
							StringWriter stack = new StringWriter();
							e.printStackTrace(new PrintWriter(stack));
							e.printStackTrace();
							fLogger.fatal(stack.toString());
	
							throw new CustomFault("SQL Exception");
						}
	
						catch (SQLException e) {
							StringWriter stack = new StringWriter();
							e.printStackTrace(new PrintWriter(stack));
							e.printStackTrace();
							fLogger.fatal(stack.toString());
	
							/**
							 * KO369761 - DF20181114 Throwing sql exception as custom fault
							 * exception to UI.
							 ***/
							throw new CustomFault("SQL Exception");
						}
	
						catch (Exception e) {
							// TODO: handle exception
							fLogger.fatal("Exception Caught ::"+e.getMessage());
							e.printStackTrace();
						}finally{
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
							if(rs1!=null)
								try {
									rs1.close();
								} catch (SQLException e1) {
									e1.printStackTrace();
								}
	
							if(statement1!=null)
								try {
									statement1.close();
								} catch (SQLException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
	
							if (prodConnection1 != null) {
								try {
									prodConnection1.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
	
						// 20150130 @Suprava new machine tab changes sn
						/*HashMap<String,String> assetOwnerMap = new HashMap<String,String>();
						List<String> serialNumList= new LinkedList<String>();
						Query assetOwnerSnapshotQ = session.createQuery(" select a.serialNumber,a.assetOwnershipDate from AssetOwnerSnapshotEntity a,AccountTenancyMapping b" +
								" where a.accountId=b.account_id" +
						        " and b.tenancy_id in ("+tenancyIdString+") ");
						Iterator assetOwnerItr = assetOwnerSnapshotQ.list().iterator();
						Object[] result=null;
						while(assetOwnerItr.hasNext())
						{
							result = (Object[])assetOwnerItr.next();
							String serialNum =null;
							Date ownerDate =null;
							String ownerShipDate =null;
							if(result[0]!=null)
							{
								serialNum =(String) result[0];
							}
							if(result[1]!=null)
							{
								ownerDate =(Date)result[1];
							}
							ownerShipDate =ownerDate.toString();
							assetOwnerMap.put(serialNum, ownerShipDate);
						}
						for(String key:assetOwnerMap.keySet())
						{
							String assetOwnerDate=assetOwnerMap.get(key);
	
							Query assetMonitoringSnapshotQ = session.createQuery(" select a.serialNumber from AssetMonitoringSnapshotEntity a " +
									" where a.serialNumber='"+key+"' " +
							        " and a.transactionTime >= '"+assetOwnerDate+"' ");
	
	
							//int list_size =assetMonitoringSnapshotQ.list().size();
	
	
							//S Suresh DAL Layer for AMS
	
							String txnKey = "AssetDetaisBO : getRolledOffMachines";
							iLogger.info(txnKey+" before calling getAMSDataOnTS ");
							//System.out.println(txnKey+" before calling getAMSDataOnTS ");
							DynamicAMS_Doc_DAL dalObj= new DynamicAMS_Doc_DAL();
							int list_size = dalObj.getAMSDataOnTS(txnKey, key, assetOwnerDate).size();
							iLogger.info(txnKey+" after calling getAMSDataOnTS size "+list_size);
							//System.out.println(txnKey+" after calling getAMSDataOnTS size "+list_size);
							if(list_size > 0)
							{
								continue;
							}
							else
							{
							  Query assetMonitoringDetailQ = session.createQuery(" select a.serialNumber,a.transactionTime,b.parameterValue,a.fuelLevel " +
								    	" from AssetMonitoringSnapshotEntity a ," +
										" AssetMonitoringDetailEntity b " +
										" where a.serialNumber='"+key+"' and a.transactionNumber=b.transactionNumber and b.parameterId=4 ");
							  Iterator query4Itr = assetMonitoringDetailQ.list().iterator();
								iLogger.info(txnKey+" before calling getAMSDataOnTS ");
								//System.out.println(txnKey+" before calling getAMSDataOnTS ");
	
	
								List<AMSDoc_DAO> AmsDAOList = dalObj.getAMSData(txnKey, key);
								Iterator query4Itr = AmsDAOList.iterator();
								iLogger.info(txnKey+" after calling getAMSData size "+AmsDAOList.size());
								//System.out.println(txnKey+" after calling getAMSData size "+AmsDAOList.size());
	
								while (query4Itr.hasNext())
								{
								//	Object[] result1 = (Object[]) query4Itr.next();
									AMSDoc_DAO daoObj = (AMSDoc_DAO)query4Itr.next();
									String lastReportedTime=null;
									AssetEntity assetContrl =null;
									String chmrvalue =null;
									String fuelLevel=null;
									Calendar cal = Calendar.getInstance();
									 //cal.setTime(dateStr.parse(servicedDate));
									 Date EvtTxnDate = null;
									 Date LogTxnDate = null;
	
									 Timestamp Txn_TS = null;//new Timestamp(cal.getTimeInMillis());
									 Date Txndate = null;
									 //Timestamp LogTxnTimestamp = null;//new Timestamp(cal.getTimeInMillis());
									implObj = new RolledOffMachinesImpl();
									if(result1[0]!=null)
									{
										assetContrl= (AssetEntity)result1[0];
										implObj.setSerialNumber(assetContrl.getSerial_number().getSerialNumber());
	
									}
	
									if(daoObj == null)
									{
										continue;
									}
									if(daoObj.getSerial_Number()!=null){
										implObj.setSerialNumber(daoObj.getSerial_Number());
									}
	
									if(daoObj.getTransaction_Timestamp_Evt()!=null)
									{
										EvtTxnDate = dateStr.parse(daoObj.getTransaction_Timestamp_Evt());
										//System.out.println("formatted : "+dateStr.format(daoObj.getTransaction_Timestamp_Evt()));
										Timestamp lastReported = (Timestamp)result1[1];
										lastReportedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastReported);
										implObj.setLastReportedTime(lastReportedTime);
									}
									if(daoObj.getTransaction_Timestamp_Log()!=null && !(daoObj.getTransaction_Timestamp_Log().equalsIgnoreCase("null")))
									{
										LogTxnDate = dateStr.parse(daoObj.getTransaction_Timestamp_Log());
										//System.out.println("formatted : "+dateStr.format(daoObj.getTransaction_Timestamp_Log()));
										Timestamp lastReported = (Timestamp)result1[1];
										lastReportedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastReported);
										implObj.setLastReportedTime(lastReportedTime);
									}
									if(EvtTxnDate!=null && LogTxnDate !=null)
									{
										if(LogTxnDate.before(EvtTxnDate)){
											Txndate = EvtTxnDate;
										}
										else
											Txndate = LogTxnDate;
									}
									else
										Txndate = EvtTxnDate == null ? LogTxnDate : EvtTxnDate;
									if(Txndate != null){
										cal.setTime(Txndate);
										Txn_TS = new Timestamp(cal.getTimeInMillis());
										lastReportedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Txn_TS);
										implObj.setLastReportedTime(lastReportedTime);
									}
	
									implObj.setFuelLevel(daoObj.getTxnData().get("FUEL_PERCT"));
									if(result1[2]!=null)
									{
										chmrvalue = (String)result1[2];
										implObj.setEnginehours(chmrvalue);
									}
									if(result1[3]!=null)
									{
										fuelLevel =(String)result1[3];
										implObj.setFuelLevel(fuelLevel);
									}
	//								Keerthi : 21.09.2016 : fix for AIOBExc - changed from serial number to parameters
									String parameters= daoObj.getParameters();
									String [] currParamList=parameters.split("\\|", -1);
	
	
									implObj.setEnginehours(currParamList[3]);
	
									implObj.setEnginehours(daoObj.getTxnData().get("CMH"));
									implList.add(implObj);
								}
							}
	
						}*/
						// 20150130 @Suprava new machine tab changes
						/*for(Entry<String, String> entry : assetOwnerMap.entrySet()) {
							    String serlNumber = entry.getKey();
							    String value = entry.getValue();
							    System.out.println(key + " " + value);
							}*/
	
						/*String queryString = " select a, r, r.transactionTime " +
						" from AssetMonitoringSnapshotEntity r RIGHT OUTER JOIN r.serialNumber a ," +
						" AccountTenancyMapping b  " +
						" where a.primary_owner_id = b.account_id " +
						" and b.tenancy_id in ("+tenancyIdString+") " +
						" and a.serial_number NOT IN " +
								" ( select distinct w.serialNumber from AssetMonitoringFactDataYearAgg w, " +
									" TenancyDimensionEntity y where w.tenancyId= y.tenacy_Dimension_Id " +
									" and y.tenancyId in ("+tenancyIdString+") )" ;
	
						if(vin!=null && !vin.isEmpty())
						{
							vin=vin.replaceAll("\\s+","");
							queryString = queryString + " and a.serial_number like '%"+vin+"%' ";
						}
	
	
						queryString = queryString + " group by a.serial_number ";
	
						Query assetQ = session.createQuery(queryString);
						Iterator assetItr = assetQ.list().iterator();
						Object[] result=null;
						while(assetItr.hasNext())
						{
							result = (Object[])assetItr.next();
							String lastReportedTime=null;
							AssetEntity assetContrl = (AssetEntity)result[0];
	
							if(result[2]!=null)
							{
								Timestamp lastReported = (Timestamp)result[2];
								lastReportedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastReported);
							}
	
							implObj = new RolledOffMachinesImpl();
							implObj.setSerialNumber(assetContrl.getSerial_number().getSerialNumber());
							implObj.setLastReportedTime(lastReportedTime);
	
							int transactionNumber=0;
							if(result[1]!=null)
							{
								AssetMonitoringSnapshotEntity txn = (AssetMonitoringSnapshotEntity)result[1];
								transactionNumber=txn.getTransactionNumber().getTransactionNumber();
							}
	
							if(transactionNumber!=0)
							{
								Query monitoringDataQ = session.createQuery(" from AssetMonitoringDetailEntity where transactionNumber="+transactionNumber +
										" and parameterId in (4,5) ");
								Iterator monitoringDataItr = monitoringDataQ.list().iterator();
								while(monitoringDataItr.hasNext())
								{
									AssetMonitoringDetailEntity detailData = (AssetMonitoringDetailEntity)monitoringDataItr.next();
									if(detailData.getParameterId().getParameterId()==4)
										implObj.setEnginehours(detailData.getParameterValue());
									if(detailData.getParameterId().getParameterId()==5)
										implObj.setFuelLevel(detailData.getParameterValue());
								}
							}
	
							implList.add(implObj);
						}*/
	
	
					}
					//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
	
					if (implList != null) {
						iLogger.info("serial no list size  "+implList.size());
					}
				}
			}
	
			/**
			 * KO369761 - DF20181114
			 * Throwing sql exception as custom fault exception to UI.
			 * **/
			catch(CustomFault e){
				System.out.println(e.getFaultInfo());
				String exceptionType = e.getFaultInfo();
				exceptionType = exceptionType.toLowerCase();
				if(exceptionType.contains("sql")){
					System.out.println("SQL Exception occurred.");
					throw new CustomFault("SQL Exception");
				}
			}
	
			catch (Exception e) {
				e.printStackTrace();
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				fLogger.fatal(stack.toString());
	
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
			long totalTimeTaken = endTime - startTime;
			iLogger.info("totalTimeTaken  " + totalTimeTaken);
			iLogger.info("Exiting getRolledOffMachines()");
			return implList;
				}
	
	
		//***************************************************** Set the Asset Profile Details *********************************************
		/** This method sets the Asset Profile Details
		 * @param profileName Name of the assetGroup
		 * @param profileCode Asset Group Code
		 * @return Returns the status
		 */
		//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
		public String setAssetProfile(String profileName, String profileCode, String messageId)
		{
			String status = "SUCCESS-Record Processed";
			Logger iLogger = InfoLoggerClass.logger;
	
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
			session.beginTransaction();
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
	
			try
			{
				Query query = session.createQuery(" from AssetGroupProfileEntity where asset_grp_code like '"+profileCode+"'");
				Iterator itr = query.list().iterator();
	
				AssetGroupProfileEntity assetGroupProfile =null;
	
				while(itr.hasNext())
				{
					assetGroupProfile = (AssetGroupProfileEntity) itr.next();
				}
	
				//Update the AssetGroup Name for the given profile
				if(assetGroupProfile!=null)
				{
					AssetGroupEntity assetGroup = assetGroupProfile.getAsset_grp_id();
					assetGroup.setAsset_group_name(profileName);
					session.update(assetGroup);
				}
	
				//Create a new AssetGroup and set the Profile Code for the same
				else
				{
					//get Client Details
					Properties prop = new Properties();
					String clientName=null;
	
					prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
					clientName= prop.getProperty("ClientName");
	
					IndustryBO industryBoObj = new IndustryBO();
					ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
					//END of get Client Details
	
					//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
					if(clientEntity==null)
					{
						status = "FAILURE-Client Undefined";
						bLogger.error("EA Processing: AssetGroupDetails: "+messageId+" : Client Undefined");
						return status;
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
	
					//Create Asset Group Object
					AssetGroupEntity newAssetGroup = new AssetGroupEntity();
					newAssetGroup.setAsset_group_name(profileName);
					if(clientEntity!=null)
						newAssetGroup.setClient_id(clientEntity);
					session.save(newAssetGroup);
	
					//Create Asset Group Profile Object
					AssetGroupProfileEntity newAssetGroupProfile = new AssetGroupProfileEntity();
					newAssetGroupProfile.setAsset_grp_id(newAssetGroup);
					newAssetGroupProfile.setAsset_grp_code(profileCode);
					session.save(newAssetGroupProfile);
				}
			}
	
			catch(Exception e)
			{
				//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
				status = "FAILURE-Exception:"+e.getMessage();
				fLogger.fatal("EA Processing: AssetGroupDetails: "+messageId+ " Fatal Exception :"+e);
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
	
		//************************************************** END of set Asset Profile Details *********************************************
	
		//*************************************************** Set Asset Type Details **************************************************
		/** This method sets the details of Asset Type (Machine Model)
		 * @param assetTypeName Name of the Model
		 * @param assetTypeCode Model Code
		 * @return Returns the processing status
		 */
		public String setAssetType(String assetTypeName, String assetTypeCode, String messageId)
		{
			String status = "SUCCESS-Record Processed";
			Logger iLogger = InfoLoggerClass.logger;
	
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
			session.beginTransaction();
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
	
			try
			{
				Query query = session.createQuery(" from AssetTypeEntity where assetTypeCode like '"+assetTypeCode+"'");
				Iterator itr = query.list().iterator();
	
				AssetTypeEntity assetType =null;
	
				while(itr.hasNext())
				{
					assetType = (AssetTypeEntity) itr.next();
				}
	
				//Update the AssetType Name for the given Type Code
				if(assetType!=null)
				{
					assetType.setAsset_type_name(assetTypeName);
					session.update(assetType);
				}
	
				//Create a new AssetType along with the Type Code for the same
				else
				{
					//get Client Details
					Properties prop = new Properties();
					String clientName=null;
	
					prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
					clientName= prop.getProperty("ClientName");
	
					IndustryBO industryBoObj = new IndustryBO();
					ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
					//END of get Client Details
	
	
					//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
					if(clientEntity==null)
					{
						status = "FAILURE-Client Undefined";
						bLogger.error("EA Processing: AssetTypeDetails: "+messageId+" : Client Undefined");
						return status;
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
	
					//Create Asset Type Object
					AssetTypeEntity newAssetType = new AssetTypeEntity();
					newAssetType.setAsset_type_name(assetTypeName);
					newAssetType.setAssetTypeCode(assetTypeCode);
					if(clientEntity!=null)
						newAssetType.setClient_id(clientEntity);
					session.save(newAssetType);
	
				}
			}
	
			catch(Exception e)
			{
				//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
				status = "FAILURE-Exception:"+e.getMessage();
				fLogger.fatal("EA Processing: AssetTypeDetails: "+messageId+ " Fatal Exception :"+e);
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
	
		//************************************************ END of Set Asset Type Details *******************************************
	
	
		//***************************************************** Set Engine Type Details ***************************************************
		/** This method sets the Engine Type details
		 * @param engineTypeName Engine Type Name
		 * @param engineTypeCode Engine Type Code
		 * @return Returns the processing status
		 */
		public String setEngineType(String engineTypeName, String engineTypeCode)
		{
			String status = "SUCCESS";
	
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			Logger iLogger = InfoLoggerClass.logger;
			//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
			session.beginTransaction();
			Logger fLogger = FatalLoggerClass.logger;
			//Logger bLogger = BusinessErrorLoggerClass.logger;
			//Logger iLogger = InfoLoggerClass.logger;
			try
			{
				Query query = session.createQuery(" from EngineTypeEntity where engineTypeCode like '"+engineTypeCode+"'");
				Iterator itr = query.list().iterator();
	
				EngineTypeEntity engineType =null;
	
				while(itr.hasNext())
				{
					engineType = (EngineTypeEntity) itr.next();
				}
	
				//Update the EngineType Name for the given Engine Type Code
				if(engineType!=null)
				{
					engineType.setEngineTypeName(engineTypeName);
					session.update(engineType);
				}
	
				//Create a new EngineType along with the Type Code for the same
				else
				{
					EngineTypeEntity newEngineType = new EngineTypeEntity();
					newEngineType.setEngineTypeCode(engineTypeCode);
					newEngineType.setEngineTypeName(engineTypeName);
					session.save(newEngineType);
				}
			}
	
			catch(Exception e)
			{
				status="FAILURE-"+e.getMessage();
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
					status="FAILURE-"+e.getMessage();
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
	
		//***************************************************** END of Set Engine Type Details ***************************************************
	
	
		//*************************************************** To Capture Primary Dealer Transfer *************************************************
		//*************************************************** To Capture Primary Dealer Transfer *************************************************
		/** This method captures the details of primary dealer transfer
		 * @param customerCode customer Code
		 * @param dealerCode New primary dealer Code
		 * @param transferDate primary dealer transfer date
		 * @return Returns the status String
		 */
		public String setPrimaryDealerTransfer(String customerCode, String dealerCode, String transferDate, String messageId)
		{
			String status = "SUCCESS-Record Processed";
	
			//Logger businessError = Logger.getLogger("businessErrorLogger");
			// Logger fatalError = Logger.getLogger("fatalErrorLogger");
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			//Logger iLogger = InfoLoggerClass.logger;
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
				AccountEntity customerAccount = null;
				AccountEntity dealerAccount = null;
	
				//Validate CustomerCode
				TenancyBO tenancyBoObj = new TenancyBO();
				customerAccount = tenancyBoObj.getAccountCodeObj(customerCode);
	
				if(customerAccount==null)
				{
					throw new CustomFault("Customer Master not received for Customer Code:"+customerCode);
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
	
				//Validate DealerCode
				dealerAccount = tenancyBoObj.getAccountCodeObj(dealerCode);
				if(dealerAccount==null)
				{
					throw new CustomFault("Dealer Master data not received for the Dealer Code:"+dealerCode);
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
	
				//Validate the TransferDate
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date vinTransferDate=null;
				String transferDateInString=null;
	
				try
				{
					vinTransferDate = dateFormat.parse(transferDate);
					transferDateInString = dateFormat.format(vinTransferDate);
				}
	
				catch(Exception e)
				{
					status = "FAILURE-"+e.getMessage();
					bLogger.error("EA Processing: PrimaryDealerTransfer: "+messageId+" : Date Parse Exception for Transfer Date"+e);
					return status;
				}
	
				//Task1 : Update Account Table
				Query query1 = session.createQuery(" from AccountEntity where status=true and accountCode='"+customerCode+"'");
				Iterator itr1 = query1.list().iterator();
				while(itr1.hasNext())
				{
					AccountEntity custAcc = (AccountEntity) itr1.next();
					custAcc.setParent_account_id(dealerAccount);
					//DF20190312 :mani: account creation or updation tracebility
					Date currentDate = new Date();
					Timestamp currentTimeStamp = new Timestamp(currentDate.getTime());
					custAcc.setUpdatedOn(currentTimeStamp);
					session.update(custAcc);
				}
	
				//Task2 : Insert the record for machine ownership with dealer in asset owners
				List<String> serialNumList = new LinkedList<String>();
				Query query2 = session.createQuery("from AssetAccountMapping where ownershipStartDate ='"+transferDateInString+"' and " +
						" accountId ='"+dealerAccount.getAccount_id()+"' and serialNumber in ( select serial_number from AssetEntity " +
						" where primary_owner_id='"+customerAccount.getAccount_id()+"' )");
				Iterator itr2 = query2.list().iterator();
				while(itr2.hasNext())
				{
					AssetAccountMapping assetOwner = (AssetAccountMapping) itr2.next();
					serialNumList.add(assetOwner.getSerialNumber().getSerial_number().getSerialNumber());
				}
	
				List<AssetEntity> assetEntityList = new LinkedList<AssetEntity>();
				Query query3 = session.createQuery(" from AssetEntity where primary_owner_id ='"+customerAccount.getAccount_id()+"'");
				Iterator itr3 = query3.list().iterator();
				while(itr3.hasNext())
				{
					AssetEntity assetEnt = (AssetEntity) itr3.next();
					if(!(serialNumList.contains(assetEnt.getSerial_number().getSerialNumber())))
					{
						assetEntityList.add(assetEnt);
					}
				}
	
				for(int i=0; i<assetEntityList.size(); i++)
				{
					AssetAccountMapping newAssetAccount = new AssetAccountMapping();
					newAssetAccount.setAccountId(dealerAccount);
					newAssetAccount.setOwnershipStartDate(vinTransferDate);
					newAssetAccount.setSerialNumber(assetEntityList.get(i));
					session.save(newAssetAccount);
				}
	
	
				//Task3: Update Tenancy table
				Query q = session.createQuery(" from AccountTenancyMapping where account_id='"+dealerAccount.getAccount_id()+"'");
				TenancyEntity dealerTenancy =null;
				Iterator it = q.list().iterator();
				while(it.hasNext())
				{
					AccountTenancyMapping accountTen = (AccountTenancyMapping)it.next();
					dealerTenancy = accountTen.getTenancy_id();
				}
	
				//TenancyEntity dealerTenancy = tenancyBoObj.getTenancyId(dealerAccount.getAccount_id());
	
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
	
	
				if(dealerTenancy!=null)
				{
					Query query4 = session.createQuery("from AccountTenancyMapping where account_id='"+customerAccount.getAccount_id()+"'");
					Iterator itr4 = query4.list().iterator();
					while(itr4.hasNext())
					{
						AccountTenancyMapping accountTenancy = (AccountTenancyMapping)itr4.next();
						TenancyEntity customerTenancy = accountTenancy.getTenancy_id();
						customerTenancy.setParent_tenancy_id(dealerTenancy);
						customerTenancy.setParent_tenancy_name(dealerTenancy.getTenancy_name());
						session.update(customerTenancy);
					}
				}
				//DefectId:20140212 Primary_dealer will transfer based upon transfer date @Suprava
				//Task4: Update asset_service_schedule table with new DealerId 
				Query query5 = session.createQuery(" from AssetServiceScheduleEntity where scheduledDate>='"+transferDate+"' and serialNumber in ( select serial_number from AssetEntity " +
						" where primary_owner_id='"+customerAccount.getAccount_id()+"' )");
				Iterator itr5 = query5.list().iterator();
				while(itr5.hasNext())
				{
					AssetServiceScheduleEntity AssetServiceSchedule = (AssetServiceScheduleEntity)itr5.next();
					AssetServiceSchedule.setDealerId(dealerAccount);
					session.update(AssetServiceSchedule);
				}
	
			}
	
			catch(CustomFault e)
			{
				status = "FAILURE-"+e.getFaultInfo();
				bLogger.error("EA Processing: PrimaryDealerTransfer: "+messageId+" : "+e.getFaultInfo());
			}
	
			catch(Exception e)
			{
				status = "FAILURE-"+e.getMessage();
				fLogger.fatal("EA Processing: PrimaryDealerTransfer: "+messageId+ " Fatal Exception :"+e);
	
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
					session.flush();
					session.close();
				}
	
			}
	
			return status;
	
		}
		//*************************************************** END of Capture Primary Dealer Transfer *************************************************
	
		/**
		 * method to get rolled off machines for the tenancy id list or login id
		 * @param loginId
		 * @param tenancyIdList
		 * @return list
		 */
		public List<RolledOffMachinesImpl> getRolledOffMachines2(String loginId,
				List<Integer> tenancyIdList) 
				{
	
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
	
			long startTime = System.currentTimeMillis();
			List<RolledOffMachinesImpl> implList = null;
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			try {
				//get Client Details
				Properties prop = new Properties();
				String clientName=null;
	
				prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				clientName= prop.getProperty("ClientName");
	
				IndustryBO industryBoObj = new IndustryBO();
				ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
				//END of get Client Details
				String query = null;
				Query getMachinesQuery = null;
				String serialNoString = null;
				//query to list of serial no. for either tenancy id or login id
				if (tenancyIdList != null) 
				{
					ListToStringConversion conversionObj = new ListToStringConversion();
					String tenancyIdString = conversionObj.getIntegerListString(
							tenancyIdList).toString();
	
					String childTenancyIdList = "SELECT tb.childId FROM TenancyBridgeEntity tb"
							+ " WHERE tb.parentId IN (" + tenancyIdString + ")";
					/*serialNoString = "SELECT owneres.serialNumber FROM AssetAccountMapping owneres"
							+ " WHERE owneres.accountId IN ("
							+ "SELECT atm.account_id FROM AccountTenancyMapping atm "
							+ "WHERE atm.tenancy_id IN ("
							+ childTenancyIdList
							+ "))";*/
					//DefectID: - Asset Owner Changes - 2013-07-19 - Rajani Nagaraju
					serialNoString = "SELECT owneres.serial_number FROM AssetEntity owneres"
							+ " WHERE owneres.primary_owner_id IN ("
							+ "SELECT atm.account_id FROM AccountTenancyMapping atm "
							+ "WHERE atm.tenancy_id IN ("
							+ childTenancyIdList
							+ ") and owneres.active_status=true and owneres.client_id="+clientEntity.getClient_id()+")";
				} 
				else if (loginId != null) 
				{
					/*serialNoString = "SELECT  aam.serialNumber"
							+ " FROM AccountContactMapping acm,AssetAccountMapping aam"
							+ " WHERE acm.contact_id ='" + loginId + "'"
							+ " AND acm.account_id=aam.accountId";*/
					//DefectID: - Asset Owner Changes - 2013-07-19 - Rajani Nagaraju
					serialNoString = "SELECT  aam.serial_number"
							+ " FROM AccountContactMapping acm,AssetEntity aam"
							+ " WHERE acm.contact_id ='" + loginId + "'"
							+ " AND acm.account_id=aam.primary_owner_id AND aam.active_status=true AND aam.client_id="+clientEntity.getClient_id()+"";
				}
				String finalQuery = "SELECT asset.serial_number,asset.productId,asset.nick_name"
						+ " FROM AssetEntity asset"
						+ " WHERE asset.serial_number IN (" + serialNoString + ") and asset.active_status=true and asset.client_id="+clientEntity.getClient_id()+" ";
				Query serialNoQuery = session.createQuery(finalQuery);
	
				String serialNo = null;
	
				Iterator iterSerialNo = serialNoQuery.list().iterator();
				Object[] resultSet = null;
				List<String> serialNumberList = new ArrayList<String>();
				AssetControlUnitEntity acuObj = null;
				ProductEntity product = null;
				AssetGroupEntity assetGroup = null;
				AssetTypeEntity assetType = null;
				EngineTypeEntity engineType = null;
				RolledOffMachinesImpl implObj = null;
				while (iterSerialNo.hasNext()) {
					resultSet = (Object[]) iterSerialNo.next();
					implObj = new RolledOffMachinesImpl();
					if (resultSet[0] != null) {
						acuObj = (AssetControlUnitEntity) resultSet[0];
					}
					if (acuObj != null) {
						serialNo = acuObj.getSerialNumber();
						if (!serialNumberList.contains(serialNo)) {
							serialNumberList.add(serialNo);
						}
	
						implObj.setSerialNumber(acuObj.getSerialNumber());
						implObj.setSimNumber(acuObj.getSimNo());
						implObj.setIMEINumber(acuObj.getImeiNo());
	
					}
					if (resultSet[1] != null) {
						product = (ProductEntity) resultSet[1];
					}
					if (product != null) {
						assetGroup = product.getAssetGroupId();
						if (assetGroup != null) {
							implObj.setAssetGroupId(assetGroup.getAsset_group_id());
							implObj.setProfileName(assetGroup.getAsset_group_name());
						}
						assetType = product.getAssetTypeId();
						if (assetType != null) {
							implObj.setAssetTypeId(assetType.getAsset_type_id());
							implObj.setModelName(assetType.getAsset_type_name());
						}
						engineType = product.getEngineTypeId();
						if (engineType != null) {
							implObj.setEngineTypeId(engineType.getEngineTypeId());
							implObj.setEngineName(engineType.getEngineTypeName());
						}
					}
					if (resultSet[2] != null) {
						implObj.setMachineName((String) resultSet[2]);
					}
					if (implList == null) {
						implList = new ArrayList<RolledOffMachinesImpl>();
					}
					implList.add(implObj);
				}
				//	iLogger.info("serialNumberList size ="+ serialNumberList.size());
				if (serialNumberList != null && serialNumberList.size() > 0) {
					ListToStringConversion conversionObj = new ListToStringConversion();
					serialNoString = conversionObj.getStringList(serialNumberList)
							.toString();
					iLogger.info("serialNoString  " + serialNoString);
					//				query to get location details for the machines
					String parameterIds = "SELECT parameterId FROM MonitoringParameters WHERE parameterName IN('Latitude','Longitude') ORDER BY parameterId ASC";
					query = "SELECT amh.serialNumber,mp.parameterId,mp.parameterName,amd.parameterValue"
							+ " FROM AssetMonitoringHeaderEntity amh,AssetMonitoringDetailEntity amd,MonitoringParameters mp"
							+ " WHERE amh.serialNumber IN ("
							+ serialNoString
							+ ") AND amh.transactionNumber = amd.transactionNumber"
							+ " AND amd.transactionNumber IN "
							+ "(SELECT MAX(transactionNumber) FROM AssetMonitoringHeaderEntity WHERE  serialNumber IN ("
							+ serialNoString
							+ ") GROUP BY serialNumber)"
							+ " AND amd.parameterId = mp.parameterId AND mp.parameterId IN"
							+ " (" + parameterIds + ")";
					getMachinesQuery = session.createQuery(query);
					List serialNoList = getMachinesQuery.list();
					Iterator iterList = serialNoList.iterator();
					serialNo = null;
	
					HashMap<String, HashMap<String, String>> serialNoMap = null;
					HashMap<String, String> locationMap = null;
					resultSet = null;
					String parameterName = null, parameterValue = null, latitude = null, longitude = null;
					int parameterID = 0;
					AssetEntity asset = null;
					while (iterList.hasNext()) {
	
						resultSet = (Object[]) iterList.next();
	
						if (resultSet[0] != null) {
							asset = (AssetEntity) resultSet[0];
						}
						if (asset != null) {
							acuObj = (AssetControlUnitEntity) asset
									.getSerial_number();
						}
	
						if (acuObj != null) {
							serialNo = acuObj.getSerialNumber();
						}
						if (serialNoMap == null) {
							serialNoMap = new HashMap<String, HashMap<String, String>>();
						}
						if (!serialNoMap.containsKey(serialNo)) {
							serialNoMap.put(serialNo, null);
						}
						locationMap = serialNoMap.get(serialNo);
						if (locationMap == null) {
							locationMap = new HashMap<String, String>();
						}
						if (resultSet[1] != null) {
							parameterID = (Integer) resultSet[1];
						}
						if (resultSet[2] != null) {
							parameterName = (String) resultSet[2];
						}
						if (resultSet[3] != null) {
							parameterValue = (String) resultSet[3];
						}
	
						if (parameterID == 1) {
							latitude = parameterValue;
							locationMap.put("Latitude", latitude);
						} else if (parameterID == 2) {
							longitude = parameterValue;
							locationMap.put("Longitude", longitude);
						}
						serialNoMap.remove(serialNo);
						serialNoMap.put(serialNo, locationMap);
					}
	
					iterList = implList.iterator();
					while (iterList.hasNext()) {
						implObj = (RolledOffMachinesImpl) iterList.next();
						serialNo = implObj.getSerialNumber();
						locationMap = serialNoMap.get(serialNo);
						if (locationMap != null) {
							implObj.setLatitude(locationMap.get("Latitude"));
							implObj.setLongitude(locationMap.get("Longitude"));
						}
	
					}
				}
			} catch (Exception e) {
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
			long endTime = System.currentTimeMillis();
			long totalTimeTaken = endTime - startTime;
			iLogger.info("totalTimeTaken  " + totalTimeTaken);
			iLogger.info("Exiting getRolledOffMachines()");
			return implList;
				}
	
	
		//------------------------------ Update the Status(Normal/Intransit) of the Machine to Edge Proxy table ---------------------------
		//DefectID: DF20131107 - Rajani Nagaraju - To update the status(Normal/Intransit) of the machine to EdgeProxy table
		//DefectId:20141021 @Suprava Refresh CMH Feature Changes
		public String updateVinStatusToEP(String serialNumber, int statusFlag,String offset,Timestamp createdTimestamp)
		{
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
	
			iLogger.info("inside updateVinStatusToEP : serialNumber"+serialNumber+" statusFlag"+statusFlag+" offset"+offset+" createdTimestamp"+createdTimestamp);
			String status =null;
			String updateStatus ="SUCCESS";  
			Connection con=null;
			Statement statement =null;
	
			int insert=1;
	
	
	
			//Make a direct JDBC connection to EdgeProxy - smart_systems database
			try
			{
				if(statusFlag==0)
					status = "NORMAL";
				else if(statusFlag==1)
					status = "TRANSIT";
	
				ConnectMySQL connectionObj = new ConnectMySQL();
				con = connectionObj.getEdgeProxyConnection();
	
				statement = con.createStatement(); 
				ResultSet resultSet=null;
	
	
				String updateQuery =null;
				String insertQuery =null;
				String selectQuery = "select device_status,node_name from device_status_info where vin_no='"+serialNumber+"'";
				iLogger.info("SELECT query : "+selectQuery);
				resultSet = statement.executeQuery(selectQuery);
				String currentNodeName=null;
				String min_node_name=null;
	
				while(resultSet.next())
				{
					insert=0;
					currentNodeName=resultSet.getString(2);
				}
				iLogger.info("insert : "+insert+" currentNodeName : "+currentNodeName);
				//KIRAN:20160119:getting minNodeName
				if(currentNodeName==null)
				{
					min_node_name=new AssetDetailsBO().getMinNodeName(statement);
	
				}
				iLogger.info("min_node_name : "+min_node_name);
				//DefectId:20141021 @Suprava Refresh CMH Feature Changes
				//New VIN for which RollOff data is received.
				if(insert==1)
				{
					//System.out.println("Inside if");
					if(offset==null){
						//System.out.println("Inside if offset null");
						if(min_node_name!=null)
						{insertQuery = "INSERT INTO device_status_info(vin_no,device_status,node_name) VALUES ('"+serialNumber+"', '"+status+"','"+min_node_name+"')";}
						else
						{insertQuery = "INSERT INTO device_status_info(vin_no,device_status) VALUES ('"+serialNumber+"', '"+status+"')";}
					}
					else{
						//System.out.println("Inside if offset");
						if(min_node_name!=null)
						{insertQuery = "INSERT INTO device_status_info(vin_no,device_status,refresh_CMH,CMH_updated_timestamp,node_name) VALUES ('"+serialNumber+"', '"+status+"','"+offset+"', '"+createdTimestamp+"','"+min_node_name+"')";}
						else
						{insertQuery = "INSERT INTO device_status_info(vin_no,device_status,refresh_CMH,CMH_updated_timestamp) VALUES ('"+serialNumber+"', '"+status+"','"+offset+"', '"+createdTimestamp+"')";}	
					}
					iLogger.info("insertQuery_NODE_NAME : "+insertQuery);
					statement.execute(insertQuery);	
				}
	
				//Updating the status of the VIN
				else
				{
					//System.out.println("Inside else");
					if(offset==null){
						//System.out.println("Inside else offset null");
						if(min_node_name!=null)
						{updateQuery = "UPDATE device_status_info SET device_status='"+status+"',node_name='"+min_node_name+"' where vin_no='"+serialNumber+"'";}
						else
						{updateQuery = "UPDATE device_status_info SET device_status='"+status+"' where vin_no='"+serialNumber+"'";}
					}
					else 
					{
						//System.out.println("Inside else offset");
						if(min_node_name!=null)
						{updateQuery = "UPDATE device_status_info SET device_status='"+status+"',refresh_CMH='"+offset+"',CMH_updated_timestamp= '"+createdTimestamp+"',node_name='"+min_node_name+"' where vin_no='"+serialNumber+"'";}
						else
						{updateQuery = "UPDATE device_status_info SET device_status='"+status+"',refresh_CMH='"+offset+"',CMH_updated_timestamp= '"+createdTimestamp+"' where vin_no='"+serialNumber+"'";}
					}
					iLogger.info("updateQuery_NODE_NAME : "+updateQuery);
					statement.executeUpdate(updateQuery);
				}
			}
	
			catch(SQLException e)
			{
				//e.printStackTrace();
				updateStatus ="FAILURE"; 
				fLogger.fatal("SQLException :",e);
			}
	
			catch(Exception e)
			{
				//e.printStackTrace();
				updateStatus ="FAILURE";
				fLogger.fatal("Exception :",e);
			}
			finally
			{
				try
				{
					if (statement != null) 
						statement.close(); 
	
					if(con!=null)
						con.close();
	
				}
	
				catch(SQLException e)
				{
					fLogger.fatal("SQLException :"+e);
				}
			}
	
			return updateStatus;
		}
	
		//------------------------------ END of Updating the Status(Normal/Intransit) of the Machine to Edge Proxy table ---------------------------
	
		//method to clear backlogs on device_status_info
	
		public String updateClearBacklogFlag(String serialNumber, int statusFlag)
		{
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
	
			iLogger.info("inside updateClearBacklogFlag : serialNumber"+serialNumber+" statusFlag"+statusFlag);
			String updateStatus ="SUCCESS";  
			Connection con=null;
			Statement statement =null;
	
	
			//Make a direct JDBC connection to EdgeProxy - smart_systems database
			try
			{
				ConnectMySQL connectionObj = new ConnectMySQL();
				con = connectionObj.getEdgeProxyConnection();
				//int clearFlag=Integer.parseInt(updateStatus);
				statement = con.createStatement(); 
				ResultSet resultSet=null;
				String updateQuery = null;
				updateQuery="update device_status_info set clear_backlog ="+statusFlag +" where vin_no="+"'"+serialNumber+"'";
				iLogger.info("updateQuery for clearing data : "+updateQuery);
				statement.executeUpdate(updateQuery);
	
			}
	
			catch(SQLException e)
			{
				//e.printStackTrace();
				updateStatus ="FAILURE"; 
				fLogger.fatal("SQLException :",e);
			}
	
			catch(Exception e)
			{
				//e.printStackTrace();
				updateStatus ="FAILURE";
				fLogger.fatal("Exception :",e);
			}
			finally
			{
				try
				{
					if (statement != null) 
						statement.close(); 
	
					if(con!=null)
						con.close();
	
				}
	
				catch(SQLException e)
				{
					fLogger.fatal("SQLException :"+e);
				}
			}
	
			return updateStatus;
		}
	
	
		//--------------------------------------- Get the List of Child Accounts for the given tenancy input---------------------------------------------
		//DF20140115 - Rajani Nagaraju - To display only those VINs that are in the current hierarchy
		/** This method returns the list of child accounts for the given input tenancy
		 * @param parentTenancyList parent Tenancy List as input
		 * @return returns the List of child accounts
		 */
		public List<Integer> getChildAccounts(Session session,List<Integer> parentTenancyList)
		{
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
	
			List<Integer> childAccIdList = new LinkedList<Integer>();
			boolean sessionCreated=false;
			if(null==session){
				sessionCreated=true;
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.beginTransaction();
			}
	
	
			try
			{
				if(parentTenancyList==null || parentTenancyList.isEmpty())
				{
					throw new CustomFault("Input Tenancy List is null");
				}
	
				ListToStringConversion conversion = new ListToStringConversion();
				String parentTenancyListAsString = conversion.getIntegerListString(parentTenancyList).toString();
	
				Query accQuery = session.createQuery(" select a from AccountTenancyMapping a, TenancyBridgeEntity b " +
						" where a.tenancy_id= b.childId and b.parentId in ("+parentTenancyListAsString+")");
				Iterator accItr = accQuery.list().iterator();
				while(accItr.hasNext())
				{
					AccountTenancyMapping accountTenancy = (AccountTenancyMapping)accItr.next();
					childAccIdList.add(accountTenancy.getAccount_id().getAccount_id());
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
				if(sessionCreated){
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
			return childAccIdList;
		}
	
		//--------------------------------------- END of Getting the List of Child Accounts for the given tenancy input---------------------------------------------
	
		//added by smitha to get the serialNumber for a given machineNumber-----20th jan 2014-----DefectID:20140120.
		public String getSerialNumberMachineNumber(String machineNumber){
	
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
	
			long startTime = System.currentTimeMillis();
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
	
			AssetControlUnitEntity serNum=null;
			String serialNumber=null;
	
			try {
				if(machineNumber!=null){
					String query = "select a.serial_number from AssetEntity a where a.machineNumber='"+ machineNumber + "'";
					Iterator itrquery = session.createQuery(query).list().iterator();
					while (itrquery.hasNext()) {
						serNum = (AssetControlUnitEntity) itrquery.next();
						serialNumber = serNum.getSerialNumber();
					}
				}
			}
			catch (Exception e) {
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
			long endTime = System.currentTimeMillis();
			long totalTimeTaken = endTime - startTime;
			iLogger.info("totalTimeTaken  " + totalTimeTaken);
			return serialNumber;		
		}
		//end of getSerialNumberMachineNumber method-----20th jan 2014
	
		public void populateGroupVersionVolume(){
			Logger fLogger = FatalLoggerClass.logger;
			//Logger bLogger = BusinessErrorLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			long startTime = System.currentTimeMillis();
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
	
			Connection con=null;
			Statement statement =null;
			try {
				boolean tableEmpty = true;
				ConnectMySQL connectionObj = new ConnectMySQL();
				con = connectionObj.getEdgeProxyConnection();
	
				statement = con.createStatement(); 
				ResultSet resultSet=null;
				String query = "SELECT * FROM modRegVersionTable";
				resultSet = statement.executeQuery(query);
	
				while(resultSet.next()){
					tableEmpty = false;
				}
				if(!tableEmpty)	{
					iLogger.info("modRegVersionTable is not empty");
					String insertQuery = "DELETE FROM modRegVersionTable";
					statement.execute(insertQuery);
				}
				else{
					iLogger.info("modRegVersionTable is empty");
				}
				Object[] resultList= null;
	
				/*query = "select z.gName, z.Vrsn, count(*) from "+
					"(select c.serialNumber as Pin, max(c.fwVersionNumber) as Vrsn ,"+
					" g.asset_group_name as gName from AssetMonitoringHeaderEntity c, AssetEntity a,"+
					" ProductEntity p,AssetGroupEntity g where c.serialNumber=a.serial_number and "+
					" a.productId = p.productId and p.assetGroupId = g.asset_group_id and "+
					" c.fwVersionNumber IS NOT NULL group by c.serialNumber) as z group by z.gName, z.Vrsn";*/
	
				/*query ="select g.asset_group_name, max(c.fwVersionNumber),c.serialNumber"+
					" from AssetMonitoringHeaderEntity c, AssetEntity a,"+
					" ProductEntity p,AssetGroupEntity g where c.serialNumber=a.serial_number and "+
					" a.productId = p.productId and p.assetGroupId = g.asset_group_id and "+
					" c.fwVersionNumber IS NOT NULL group by c.serialNumber order by c.fwVersionNumber,g.asset_group_name";*/
				List<AmsDAO> snapshotObj=new ArrayList<AmsDAO> ();
				DynamicAMS_DAL amsDaoObj=new DynamicAMS_DAL();
				String txnKey = "AssetDetailsBO:populateGroupVersionVolume";
				snapshotObj=amsDaoObj.getFWversion(txnKey);
				List result = snapshotObj;
				if(result!=null){
					iLogger.info("group version volume query size "+result.size());
				}
				AmsDAO daoObect = null;
				Iterator itrquery = result.iterator();
				//The above query returns list of serial nos with max version and repeated group name and version
				//				insert all values
	
				String groupName = null, fwVersionNumber=null;
				HashMap<String,HashMap<String,Integer>> grpVersionCountMap = new HashMap<String,HashMap<String,Integer>>();
				HashMap<String,Integer> versionCountMap = new HashMap<String,Integer>();
				int vCount=0;
				while (itrquery.hasNext()) {
					daoObect = (AmsDAO)itrquery.next();
					if(daoObect.getAsset_group_name()!=null){
						groupName = daoObect.getAsset_group_name();
					}
					/*if(daoObect.getParameters()!=null){
	
							String parameters=daoObect.getParameters();
							String [] currParamList=parameters.split("\\|", -1);
							if(currParamList.length>8){
								fwVersionNumber = currParamList[8];
							}
					 */
					HashMap<String,String> txnDataMap=new HashMap<String, String>();	
					if(daoObect.getTxnData()!=null){
						txnDataMap = daoObect.getTxnData();
						fwVersionNumber = txnDataMap.get("FW_VER");
	
					}
	
					//						insert into hashmap
					if(grpVersionCountMap.containsKey(groupName)){
						versionCountMap = grpVersionCountMap.get(groupName);
						if(versionCountMap!=null){
							if(versionCountMap.containsKey(fwVersionNumber)){
								vCount = versionCountMap.get(fwVersionNumber);
								++vCount;
								versionCountMap.put(fwVersionNumber, vCount);
								grpVersionCountMap.put(groupName, versionCountMap);
							}
							else{
								versionCountMap = grpVersionCountMap.get(groupName);
								versionCountMap.put(fwVersionNumber, 1);
								grpVersionCountMap.put(groupName, versionCountMap);
							}
						}
					}
					else{
						versionCountMap = new HashMap<String, Integer>();
						versionCountMap.put(fwVersionNumber, 1);
						grpVersionCountMap.put(groupName, versionCountMap);
					}
				}
				//				insert into modRegVersionTable
				if(!grpVersionCountMap.isEmpty()){
					for (String group : grpVersionCountMap.keySet() ) {
						versionCountMap = grpVersionCountMap.get(group);
						if(!versionCountMap.isEmpty()){
							for (String version : versionCountMap.keySet() ) {
								vCount = versionCountMap.get(version);
								iLogger.info ("inserting "+group+" "+version+" "+vCount);
								String insertQuery = "INSERT INTO modRegVersionTable(region,version,model,count) VALUES ('INDIA','"+version+"','"+group+"','"+vCount+"')";
								statement.execute(insertQuery);
							}
						}
					}
	
	
				}			
			}
			catch (Exception e) {
				e.printStackTrace();
			}
	
			finally {
	
				try
				{
					if (statement != null) 
						statement.close(); 
	
					if(con!=null)
						con.close();
	
				}
	
				catch(SQLException e)
				{
					fLogger.fatal("SQLException :"+e);
				}
				if (session.getTransaction().isActive()) {
					session.getTransaction().commit();
				}
	
				if (session.isOpen()) {
					session.flush();
					session.close();
				}
	
			}
			long endTime = System.currentTimeMillis();
			long totalTimeTaken = endTime - startTime;
			iLogger.info("totalTimeTaken  " + totalTimeTaken);
	
		}
		public void setGropTypeForDeviceStatusInfo(){
			Logger fLogger = FatalLoggerClass.logger;
			//Logger bLogger = BusinessErrorLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;
			long startTime = System.currentTimeMillis();
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
	
			Connection con=null;
			Statement statement =null;
			try {
				ConnectMySQL connectionObj = new ConnectMySQL();
				con = connectionObj.getEdgeProxyConnection();
	
				statement = con.createStatement(); 
				ResultSet resultSet=null;
				//				take the list from device status info and update with group and type
				Object[] resultList,groupTypeList= null;
				String query = "SELECT vin_no,group_name,type_name FROM device_status_info";
	
				resultSet = statement.executeQuery(query);
				/* if(result!=null){
						 infoLogger.info("device status info query size "+result.size());
					 }*/
	
				Iterator iterator = null;
				Iterator iterateGpTp =null;
				String serialNumber = null,groupName=null,typeName=null,groupTypeQuery=null;
				List<String> pinsToBeUpdated = new ArrayList<String>();
				while (resultSet.next()) {
					serialNumber =  resultSet.getString("vin_no");
					groupName = resultSet.getString("group_name");
					typeName = resultSet.getString("type_name");
					if(groupName ==null || typeName == null){
						pinsToBeUpdated.add(serialNumber);
					}
				}		
				if(pinsToBeUpdated.size()>0){
					iLogger.info("pins to be updated list size "+pinsToBeUpdated.size());
					//						get group and type from WISE and update row
					iterator = pinsToBeUpdated.iterator();
					while(iterator.hasNext()){
						serialNumber = (String)iterator.next();
						groupTypeQuery = "SELECT ast.serial_number,ast.productId FROM AssetEntity ast WHERE ast.serial_number='"+serialNumber+"'";
						iterateGpTp = session.createQuery(groupTypeQuery).list().iterator();
						ProductEntity prod = null;
						while(iterateGpTp.hasNext()){
							resultList = (Object[])iterateGpTp.next();
							if(resultList[1]!=null){
								prod = (ProductEntity)resultList[1];
								//									update group and type for each PIN
								if(prod!=null){
									groupName = prod.getAssetGroupId().getAsset_group_name();
									typeName = prod.getAssetTypeId().getAsset_type_name();
									iLogger.info("updating "+serialNumber+" "+groupName+" "+typeName);
									String updateQuery = "UPDATE device_status_info SET group_name='"+groupName+"', type_name = '"+typeName+"' where vin_no='"+serialNumber+"'";
									statement.executeUpdate(updateQuery);
								}								
							}
	
						}						
					}
				}						
			}
			catch (Exception e) {
				e.printStackTrace();
			}
	
			finally {
	
				try
				{
					if (statement != null) 
						statement.close(); 
	
					if(con!=null)
						con.close();
	
				}
	
				catch(SQLException e)
				{
					fLogger.fatal("SQLException :"+e);
				}
				if (session.getTransaction().isActive()) {
					session.getTransaction().commit();
				}
	
				if (session.isOpen()) {
					session.flush();
					session.close();
				}
	
			}
			long endTime = System.currentTimeMillis();
			long totalTimeTaken = endTime - startTime;
			iLogger.info("totalTimeTaken  " + totalTimeTaken);
	
		}
	
	
		// New Method for Creating CustomerAccount for Sale Interface 
	
		//***************************** Create Customer Account for the given Customer Code *********************************
		//20140609 - Rajani Nagaraju - Create Customer Account if the Customer data is received from EA
	
	
		//		Keerthi : 2017-02-01 : dealer code should be taken from SAP file instead from the customer master
		public AccountEntity createCustomerAcc_New(String custCode, String dealerCode, String messageId)
		{
	
	
			AccountEntity custEntity = null;
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;  
	
			iLogger.info("CusomerAccount Creation START"+"custCode:"+custCode+" dealerCode:"+dealerCode+" messageId:"+messageId);
			//Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	
			Session session = HibernateUtil.getSessionFactory().openSession();
			Transaction tx = null;
			//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
			String custStatus="FAILURE";
			try
			{
				if(custCode!=null)
				{
					try
					{
						tx=session.beginTransaction();
						Query custMasterQuery = session.createQuery(" from CustomerMasterEntity where customerCode='"+custCode+"'");
						Iterator custMasterItr = custMasterQuery.list().iterator();
	
						while(custMasterItr.hasNext())
						{
							CustomerMasterEntity custMaster = (CustomerMasterEntity)custMasterItr.next();
	
							if(custMaster.getCustomerdetail()!=null)
							{
								String[] msgString = custMaster.getCustomerdetail().split("\\|");
								CustomerInfoInputContract inputObject = new CustomerInfoInputContract();
	
								if(msgString.length>0)
									inputObject.setCustomerCode(msgString[0]);
	
								if(msgString.length>1)
									inputObject.setCustomerName(msgString[1]);
	
								if(msgString.length>2)
									inputObject.setAddressLine1(msgString[2]);
	
								if(msgString.length>3)
									inputObject.setAddressLine2(msgString[3]);
	
								if(msgString.length>4)
									inputObject.setCity(msgString[4]);
	
								if(msgString.length>5)
									inputObject.setZipCode(msgString[5]);
	
								if(msgString.length>6)
									inputObject.setState(msgString[6]);
	
								if(msgString.length>7)
									inputObject.setZone(msgString[7]);
	
								if(msgString.length>8)
									inputObject.setCountry(msgString[8]);
	
								if(msgString.length>9)
									inputObject.setEmail(msgString[9]);
	
								if(msgString.length>10)
									inputObject.setContactNumber(msgString[10]);
	
								if(msgString.length>11)
									inputObject.setFax(msgString[11]);
	
								if(msgString.length>12)
									inputObject.setPrimaryDealerCode(msgString[12]);
	
								if(inputObject.getEmail().equalsIgnoreCase(""))
									inputObject.setEmail(null);
								//								Keerthi : 2017-02-01 : dealer code should be taken from SAP file instead from the customer master
								inputObject.setPrimaryDealerCode(dealerCode);
	
	
								//Call the Impl Class directly that process the customer information as LL account
								custStatus = new AccountDetailsImpl().setCustomerAccountDetails_New(inputObject.getCustomerCode(), 
										inputObject.getCustomerName(), inputObject.getAddressLine1(), inputObject.getAddressLine2(), 
										inputObject.getCity(), inputObject.getZipCode(), inputObject.getState(), inputObject.getZone(), 
										inputObject.getCountry(), inputObject.getEmail(), inputObject.getContactNumber(), inputObject.getFax(), 
										inputObject.getPrimaryDealerCode(),messageId);
	
								if(custStatus.contains("SUCCESS")){
	
									if(! (session.isOpen() ))
									{
										session = HibernateUtil.getSessionFactory().getCurrentSession();
										if(session.getTransaction().isActive() && session.isDirty())
										{
											iLogger.info("Opening a new session");
											session = HibernateUtil.getSessionFactory().openSession();
										}
										tx=session.beginTransaction();
									}
									custMaster.setProcessFlag(1);
									session.save(custMaster);
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
					}
	
					catch(Exception e)
					{
						fLogger.fatal("EA Processing: Customer Account Creation: "+messageId+ " Fatal Exception :"+e);
	
					}
	
				}
	
				//DF20140715 - Rajani Nagaraju - Handle Robust Logging
				if(custStatus.split("-").length>1)
				{
					custStatus = custStatus.split("-")[0].trim();
	
				}
	
				if(!(custStatus.equalsIgnoreCase("SUCCESS")))
				{
					throw new CustomFault("EA Processing: Customer Account Creation: "+messageId+": Invalid Customer Code: "+custCode);
				}
	
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
	
					//get the Account Details of Customer
					Query custAccQ = session.createQuery("from AccountEntity where status=true and accountCode='"+custCode+"'");
					Iterator custAccIter = custAccQ.list().iterator();
					while(custAccIter.hasNext())
					{
						custEntity = (AccountEntity) custAccIter.next();
	
					}
				}
			}
	
			catch(CustomFault e)
			{
				bLogger.error("EA Processing: Customer Account Creation: "+messageId+": "+ e.getFaultInfo());
			}
	
			catch(Exception e)
			{
				fLogger.fatal("EA Processing: Customer Account Creation: "+messageId+": Exception :"+e);
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
					//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
					fLogger.fatal("Exception in commiting the record:"+e);
				}
	
				if(session.isOpen())
				{
	
					session.close();
				}
	
			}
	
			return custEntity;
		}
	
	
	
	
	
	
		//***************************** Create Customer Account for the given Customer Code *********************************
		//20140609 - Rajani Nagaraju - Create Customer Account if the Customer data is received from EA
	
	
		//		Keerthi : 2017-02-01 : dealer code should be taken from SAP file instead from the customer master
		public AccountEntity createCustomerAcc(String custCode, String dealerCode, String messageId)
		{
			AccountEntity custEntity = null;
			Logger fLogger = FatalLoggerClass.logger;
			Logger bLogger = BusinessErrorLoggerClass.logger;
			Logger iLogger = InfoLoggerClass.logger;       
	
			Transaction tx = null;
	
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	
			//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("AssetGateOutLog: "+custCode+": Opening a new session"); 	
				session = HibernateUtil.getSessionFactory().openSession();
			}
	
			session.beginTransaction();
	
			String custStatus="FAILURE";
			try
			{
				if(custCode!=null)
				{
					try
					{
						tx=session.beginTransaction();
						Query custMasterQuery = session.createQuery(" from CustomerMasterEntity where customerCode='"+custCode+"'");
						Iterator custMasterItr = custMasterQuery.list().iterator();
	
						while(custMasterItr.hasNext())
						{
							CustomerMasterEntity custMaster = (CustomerMasterEntity)custMasterItr.next();
	
							if(custMaster.getCustomerdetail()!=null)
							{
								String[] msgString = custMaster.getCustomerdetail().split("\\|");
								CustomerInfoInputContract inputObject = new CustomerInfoInputContract();
	
								if(msgString.length>0)
									inputObject.setCustomerCode(msgString[0]);
	
								if(msgString.length>1)
									inputObject.setCustomerName(msgString[1]);
	
								if(msgString.length>2)
									inputObject.setAddressLine1(msgString[2]);
	
								if(msgString.length>3)
									inputObject.setAddressLine2(msgString[3]);
	
								if(msgString.length>4)
									inputObject.setCity(msgString[4]);
	
								if(msgString.length>5)
									inputObject.setZipCode(msgString[5]);
	
								if(msgString.length>6)
									inputObject.setState(msgString[6]);
	
								if(msgString.length>7)
									inputObject.setZone(msgString[7]);
	
								if(msgString.length>8)
									inputObject.setCountry(msgString[8]);
	
								if(msgString.length>9)
									inputObject.setEmail(msgString[9]);
	
								if(msgString.length>10)
									inputObject.setContactNumber(msgString[10]);
	
								if(msgString.length>11)
									inputObject.setFax(msgString[11]);
	
								if(msgString.length>12)
									inputObject.setPrimaryDealerCode(msgString[12]);
	
								if(inputObject.getEmail().equalsIgnoreCase(""))
									inputObject.setEmail(null);
	
								//								Keerthi : 2017-02-01 : use dealer code from SAP file, instead from the customer master
								inputObject.setPrimaryDealerCode(dealerCode);	
	
								//Call the Impl Class directly that process the customer information as LL account
	
								iLogger.info("AssetGateOutLog: "+custCode+": Before calling setCustomerAccountDetails"); 	
	
	
								custStatus = new AccountDetailsImpl().setCustomerAccountDetails(inputObject.getCustomerCode(), 
										inputObject.getCustomerName(), inputObject.getAddressLine1(), inputObject.getAddressLine2(), 
										inputObject.getCity(), inputObject.getZipCode(), inputObject.getState(), inputObject.getZone(), 
										inputObject.getCountry(), inputObject.getEmail(), inputObject.getContactNumber(), inputObject.getFax(), 
										inputObject.getPrimaryDealerCode(),messageId);
	
								iLogger.info("AssetGateOutLog: "+custCode+": After calling setCustomerAccountDetails:"+custStatus); 	
	
								if(custStatus.contains("SUCCESS")){
	
									if(session==null || ! (session.isOpen() ))
									{
										session = HibernateUtil.getSessionFactory().getCurrentSession();
										if(session.getTransaction().isActive() && session.isDirty())
										{
											iLogger.info("AssetGateOutLog: "+custCode+":Opening a new session");
											session = HibernateUtil.getSessionFactory().openSession();
										}
										tx=session.beginTransaction();
									}
									custMaster.setProcessFlag(1);
									session.save(custMaster);
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
									fLogger.fatal("Exception in commiting the record:"+e.getMessage());
								}
	
	
							}
						}
					}
	
					catch(Exception e)
					{
						fLogger.fatal("EA Processing: Customer Account Creation: "+messageId+ " Fatal Exception :"+e);
	
					}
	
				}
	
				//DF20140715 - Rajani Nagaraju - Handle Robust Logging
				if(custStatus.split("-").length>1)
				{
					custStatus = custStatus.split("-")[0].trim();
	
				}
	
				if(!(custStatus.equalsIgnoreCase("SUCCESS")))
				{
					throw new CustomFault("EA Processing: Customer Account Creation: "+messageId+": Invalid Customer Code: "+custCode);
				}
	
				else
				{
					if(session==null || ! (session.isOpen() ))
					{
						session = HibernateUtil.getSessionFactory().getCurrentSession();
						//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
						if(session.getTransaction().isActive() && session.isDirty())
						{
							iLogger.info("AssetGateOutLog: "+custCode+":Opening a new session");
							session = HibernateUtil.getSessionFactory().openSession();
						}
	
					}
	
					//get the Account Details of Customer
					Query custAccQ = session.createQuery("from AccountEntity where status=true and accountCode='"+custCode+"'");
					Iterator custAccIter = custAccQ.list().iterator();
					while(custAccIter.hasNext())
					{
						custEntity = (AccountEntity) custAccIter.next();
	
					}
				}
			}
	
			catch(CustomFault e)
			{
				bLogger.error("EA Processing: Customer Account Creation: "+messageId+": "+ e.getFaultInfo());
			}
	
			catch(Exception e)
			{
				fLogger.fatal("EA Processing: Customer Account Creation: "+messageId+": Exception :"+e);
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
					//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
					fLogger.fatal("Exception in commiting the record:"+e);
				}
	
				if(session!=null && session.isOpen())
				{
	
					session.close();
				}
	
			}
	
			return custEntity;
		}
	
	
		//****************************************** Populate Staging table with the current ownership details ******************************************
		public void populateOwnerDetails(String serialNumber)	
		{
			//			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			Session session = HibernateUtil.getSessionFactory().openSession();
			Logger iLogger = InfoLoggerClass.logger;
	
			//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			/*if(session.getTransaction().isActive() && session.isDirty())
				{
				   	iLogger.info("Opening a new session");
				   	session = HibernateUtil.getSessionFactory().openSession();
				}*/
	
	
			session.beginTransaction();
			long oldCount=0;
			long newCount=0;
			List<AssetOwnerSnapshotEntity> assetOwnerSnapshotList = new LinkedList<AssetOwnerSnapshotEntity>();
			Logger fLogger = FatalLoggerClass.logger;
			//Logger bLogger = BusinessErrorLoggerClass.logger;
	
			//DF20160114 @Roopa for Set AssetOwnerSnapshot details into OrientDB
			HashMap<String,String> OAssetOwnerSnapshotMap=new HashMap<String,String>();
	
			OAssetOwnerSnapshotMap.put("@type","d");
			String OCurrentOwner = null;
	
			String oSerialNumber=null;
	
			try
			{ 
	
				Properties dtabaseInsertion = new Properties();
				dtabaseInsertion = CommonUtil.getDepEnvProperties();
	
				String assetOwnerSnapMysqlInsertionCheck = dtabaseInsertion.getProperty("InsertMySQL");
	
				String assetOwnerSnapOrientDBInsertionCheck = dtabaseInsertion.getProperty("InsertOrientDB");
	
				//DF20150402 - Rajani Nagaraju - To remove preceeding zeros in the VIN - Check based on Machine number
				if(serialNumber!=null && (serialNumber.length()>7))
				{
					serialNumber=serialNumber.substring(serialNumber.length()-7);
				}
	
				iLogger.info(serialNumber+": START AssetOwner snapshot population");
	
				Query assetsnapshotQ = null;
	
				if(serialNumber==null)
				{
					assetsnapshotQ=	session.createQuery(" select count(*) from AssetOwnerSnapshotEntity a, AssetEntity b " +
							" where a.serialNumber= b.serial_number and b.active_status=true");
				}
	
				else
				{
					assetsnapshotQ=	session.createQuery(" select count(*) from AssetOwnerSnapshotEntity a, AssetEntity b " +
							" where a.serialNumber= b.serial_number and b.active_status=true and a.serialNumber like'%"+serialNumber+"'");
				}
	
				Iterator assetsnapshotItr = assetsnapshotQ.list().iterator();
				while(assetsnapshotItr.hasNext())
				{
					oldCount = (Long)assetsnapshotItr.next();
				}
	
	
				Query assetQ = null;
				if(serialNumber==null)
					assetQ=session.createQuery(" from AssetEntity where active_status=true ");
				else
					assetQ=session.createQuery(" from AssetEntity where active_status=true and serial_number like '%"+serialNumber+"'");
	
				Iterator assetItr = assetQ.list().iterator();
				while(assetItr.hasNext())
				{
					AssetEntity asset = (AssetEntity) assetItr.next();
					oSerialNumber =asset.getSerial_number().getSerialNumber();
	
					OCurrentOwner=String.valueOf(asset.getPrimary_owner_id());
	
					AccountEntity assetOwnerAccount = null;
					Query primaryAccountQ = session.createQuery("from AccountEntity where account_id="+asset.getPrimary_owner_id()+" and client_id="+1+" and status=true ");
					Iterator primaryAccItr = primaryAccountQ.list().iterator();
					while(primaryAccItr.hasNext())
					{
						assetOwnerAccount = (AccountEntity)primaryAccItr.next();
					}
	
	
					try
					{
	
						AccountEntity childAccount = assetOwnerAccount;
						AccountEntity parentAccount = assetOwnerAccount.getParent_account_id();
						int counter =1;
	
						Date ownershipStartDate=null;
						//******************* Starting of business logic for SAARC implementation in populating data in AOS during gate-out and roll-off **************
						//DF20170801: SU334449 - Checking for childAccount not equal to null	
						while(childAccount != null && counter<10)
						{
							iLogger.info(serialNumber+" :AssetOwnerSnapshot: PartnershipMapping loop Counter::"+counter);
							Query query = null;
							if(parentAccount !=null){
								query = session.createQuery("from PartnershipMapping where accountFromId ="
										+parentAccount.getAccount_id()+" and accountToId ="+childAccount.getAccount_id());
								Iterator it = query.list().iterator();
								while(it.hasNext())
								{
									iLogger.info(serialNumber+" :AssetOwnerSnapshot: PartnershipMapping exists from" +parentAccount.getAccount_id()+" To "+childAccount.getAccount_id());
	
									PartnershipMapping accountPartner = (PartnershipMapping)it.next();
	
	
									Query ownershipDateQ = session.createQuery("from AssetAccountMapping where serialNumber='"+asset.getSerial_number().getSerialNumber()+"' " +
											" and accountId='"+childAccount.getAccount_id()+"'");
									Iterator ownershipDateItr = ownershipDateQ.list().iterator();
									while(ownershipDateItr.hasNext())
									{
										AssetAccountMapping assetAccount = (AssetAccountMapping) ownershipDateItr.next();
										ownershipStartDate = assetAccount.getOwnershipStartDate();	
	
										if (assetOwnerSnapOrientDBInsertionCheck.equalsIgnoreCase("true")) {
											//OAssetOwnerSnapshotMap.put(String.valueOf(assetAccount.getAccountId().getAccount_id()), ownershipStartDate.toString());
											OAssetOwnerSnapshotMap.put("_"+String.valueOf(assetAccount.getAccountId().getAccount_id()), ownershipStartDate.toString());
										}
	
									}
	
	
									iLogger.info(asset.getSerial_number().getSerialNumber()+" : "+ childAccount.getAccount_id()+ " : "+ownershipStartDate+" : "+accountPartner.getPartnerId().getReversePartnerRole() );
									Calendar cal = Calendar.getInstance();
									cal.setTime(ownershipStartDate);
									cal.add(Calendar.DATE, -1);
									Date ownershipStartDateMinus1 = cal.getTime();
	
									//Enter the details into AssetOwnerSnapshotEntity
									if (assetOwnerSnapMysqlInsertionCheck.equalsIgnoreCase("true")) {
										AssetOwnerSnapshotEntity assetOwnerSnapshot = new AssetOwnerSnapshotEntity();
										assetOwnerSnapshot.setAccountId(childAccount);
										assetOwnerSnapshot.setAccountType(accountPartner.getPartnerId().getReversePartnerRole());
										if(asset.getProductId()!=null)
											assetOwnerSnapshot.setAssetGroupId(asset.getProductId().getAssetGroupId());
										assetOwnerSnapshot.setAssetOwnershipDate(ownershipStartDateMinus1);
										if(asset.getProductId()!=null)
											assetOwnerSnapshot.setAssetTypeId(asset.getProductId().getAssetTypeId());
										assetOwnerSnapshot.setSerialNumber(asset.getSerial_number().getSerialNumber());
										//session.save(assetOwnerSnapshot);
										assetOwnerSnapshotList.add(assetOwnerSnapshot);
									}
	
									newCount++;
	
									childAccount = parentAccount;
									parentAccount = childAccount.getParent_account_id();
	
								}
	
								counter++;
							}else{
								iLogger.info(serialNumber+" :AssetOwnerSnapshot:");
	
								Query ownershipDateQ = session.createQuery("from AssetAccountMapping where serialNumber='"+asset.getSerial_number().getSerialNumber()+"' " +
										" and accountId='"+childAccount.getAccount_id()+"'");
								Iterator ownershipDateItr = ownershipDateQ.list().iterator();
								while(ownershipDateItr.hasNext())
								{
									AssetAccountMapping assetAccount = (AssetAccountMapping) ownershipDateItr.next();
									ownershipStartDate = assetAccount.getOwnershipStartDate();	
	
									if (assetOwnerSnapOrientDBInsertionCheck.equalsIgnoreCase("true")) {
										//OAssetOwnerSnapshotMap.put(String.valueOf(assetAccount.getAccountId().getAccount_id()), ownershipStartDate.toString());
										OAssetOwnerSnapshotMap.put("_"+String.valueOf(assetAccount.getAccountId().getAccount_id()), ownershipStartDate.toString());
									}
								}
								if (assetOwnerSnapMysqlInsertionCheck.equalsIgnoreCase("true")) {
									Calendar cal = Calendar.getInstance();
									cal.setTime(ownershipStartDate);
									//cal.add(Calendar.DATE, -1);//CR395.o
									//Date ownershipStartDateMinus1 = cal.getTime();//CR395.o
									AssetOwnerSnapshotEntity assetOwnerSnapshot = new AssetOwnerSnapshotEntity();
									assetOwnerSnapshot.setAccountId(childAccount);
									assetOwnerSnapshot.setAccountType("OEM Global");
	
									if(asset.getProductId()!=null){
										assetOwnerSnapshot.setAssetGroupId(asset.getProductId().getAssetGroupId());
										assetOwnerSnapshot.setAssetTypeId(asset.getProductId().getAssetTypeId());
									}
	
									//assetOwnerSnapshot.setAssetOwnershipDate(ownershipStartDateMinus1);//CR395.o
									assetOwnerSnapshot.setAssetOwnershipDate(cal.getTime());//CR395.n
									assetOwnerSnapshot.setSerialNumber(asset.getSerial_number().getSerialNumber());
									assetOwnerSnapshotList.add(assetOwnerSnapshot);
								}
								childAccount = parentAccount;
							}
						}
						//******************* End of the business logic for SAARC implementation in populating data in AOS during Roll-off and Gate-out. ****************
					}
	
					catch(Exception e)
					{
						e.printStackTrace();
						fLogger.error(serialNumber+"AssetOwnerSnapshot: Exception Cause: "+e);
						Writer result = new StringWriter();
						PrintWriter printWriter = new PrintWriter(result);
						e.printStackTrace(printWriter);
						String err = result.toString();
						fLogger.fatal(serialNumber+"Exception trace: "+err);
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
					}
	
	
				}
	
				//DefectId:20150417 Updation not happening in AssetOwnerSnapshot Table Hence Condition Remove
				//if(newCount>=oldCount)
				//{
				//Truncate the table
				iLogger.info(serialNumber+" :AssetOwnerSnapshot: Truncate table, Number of rows :"+oldCount);
	
				//DF20160909 @Roopa session not open check
	
				if(session==null || ! (session.isOpen() ))
				{
					//session = HibernateUtil.getSessionFactory().getCurrentSession();
					/*if(session.getTransaction().isActive() && session.isDirty())
		    				{
		    					iLogger.info(serialNumber+" :AssetOwnerSnapshot: Opening a new session"); 
		    					session = HibernateUtil.getSessionFactory().openSession();
		    				}*/
					iLogger.info(serialNumber+" :AssetOwnerSnapshot: Opening a new session");
					session = HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction();
				}
	
	
	
				Query deleteQ=null;
				if(serialNumber==null)
					deleteQ = session.createQuery(" delete from AssetOwnerSnapshotEntity where serialNumber is not null");
				else
					deleteQ = session.createQuery(" delete from AssetOwnerSnapshotEntity where serialNumber like '%"+serialNumber+"'");
				try{
					deleteQ.executeUpdate();
				}catch(Exception e){
					iLogger.info(serialNumber+"error in excecuting query -> delete from AssetOwnerSnapshotEntity where serialNumber like '%"+serialNumber+"'");
	
					//Re-trying again 
					if(session==null || ! (session.isOpen() ))
					{
						//session = HibernateUtil.getSessionFactory().getCurrentSession();
						/*if(session.getTransaction().isActive() && session.isDirty())
			    				{
			    					iLogger.info(serialNumber+" :AssetOwnerSnapshot: Opening a new session"); 
			    					session = HibernateUtil.getSessionFactory().openSession();
			    				}*/
						iLogger.info(serialNumber+" :AssetOwnerSnapshot: Opening a new session");
						session = HibernateUtil.getSessionFactory().openSession();
						session.beginTransaction();
					}
	
					try{
						deleteQ.executeUpdate();
					}catch(Exception e1){
						iLogger.info("Failed in Retry "+serialNumber);
						iLogger.info(serialNumber+"error in excecuting query -> delete from AssetOwnerSnapshotEntity where serialNumber like '%"+serialNumber+"'");
					}
					// end of retry
				}
	
				//Commit the new record list
				iLogger.info(serialNumber+" AssetOwnerSnapshot: Insert table, Number of rows :"+newCount+" assetOwnerSnapshotList.size():"+assetOwnerSnapshotList.size());
				try
				{
					for(int i=0; i<assetOwnerSnapshotList.size(); i++)
					{
						//DefectID: 20170502 - Customer sale details not getting updated in AOS but updated correctly in AO - Logger to catch the source of failure
						iLogger.info(serialNumber+":AssetOwnerSnapshot:"+assetOwnerSnapshotList.get(i).getSerialNumber()+":"
								+assetOwnerSnapshotList.get(i).getAccountType()+":"+assetOwnerSnapshotList.get(i).getAccountId()+":"+
								assetOwnerSnapshotList.get(i).getAssetGroupId()+":"+assetOwnerSnapshotList.get(i).getAssetOwnershipDate()+
								":"+assetOwnerSnapshotList.get(i).getAssetTypeId());
						session.save(assetOwnerSnapshotList.get(i));
					}
	
					if (session!=null && session.getTransaction().isActive()) 
					{
						session.getTransaction().commit();
					}
				}
				//DefectID: 20170508 - Customer sale details not getting updated in AOS but updated correctly in AO - to catch the source of failure
				catch(Exception e)
				{
					e.printStackTrace();
					fLogger.error(serialNumber+":AssetOwnerSnapshot: Object save Exception Cause: "+e);
					Writer result = new StringWriter();
					PrintWriter printWriter = new PrintWriter(result);
					e.printStackTrace(printWriter);
					String err = result.toString();
					fLogger.fatal(serialNumber+":AssetOwnerSnapshot: Object save Exception trace: "+err);
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
				}
				//}
				//DefectId:20150417 Updation not happening in AssetOwnerSnapshot Table Hence Condition Remove
				//else
				//{
				//Report into Error Log
				//fLogger.error(serialNumber+" AssetOwnerSnapshot: OldRecordCount:"+oldCount+", NewRecordCount:"+newCount+" , Hence not updated");
				//}
	
			}
	
			catch(Exception e)
			{
	
				try
				{if (session.getTransaction().isActive()) 
				{
					session.getTransaction().rollback();
				}
				}
	
				catch(Exception e1)
				{
	
					//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
					fLogger.fatal("Exception in rolling back the record:"+e1);
				}
	
				e.printStackTrace();
				fLogger.error("AssetOwnerSnapshot: Exception Cause: "+e);
			}
	
			finally
			{
				/*if(session.getTransaction().isActive())
		              {
		                    session.getTransaction().commit();
		              }
				 */
				if( session!=null && (session.isOpen()) )
				{
					session.flush();
					session.close();
				}
			}
	
			/*  if(! OAssetOwnerSnapshotMap.isEmpty()){
	
		        	//DF20160504 - Roopa- Populate ownership details into orientAppdb TAssetSnapshot table
	
		        	//Commenting below part since ODB TAssetSnapshot is not getting used
					iLogger.info("Populate ownership details into orientAppdb TAssetSnapshot table Input-----> VIN:"+oSerialNumber);
					long startTime = System.currentTimeMillis();
	
		        	String status  = new OAssetSnapshotImpl().setOAssetSnapshotDetails(oSerialNumber, OAssetOwnerSnapshotMap, OCurrentOwner);
	
		        	long endTime=System.currentTimeMillis();
					iLogger.info("Populate ownership details into orientAppdb TAssetSnapshot table Output -----> status:"+status+"; Total Time taken in ms:"+(endTime - startTime));
	
				}*/
	
			//DF20160411 - Rajani Nagaraju - Populate the Default Notification Subscriber List in OrientDB - Used by Communication Module for Alert broadcasting
			iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:WebService Input-----> VIN:"+oSerialNumber);
			long startTime = System.currentTimeMillis();
	
			String status = new AlertSubscriptionImpl().setDefaultSubscribers(oSerialNumber);
	
			long endTime=System.currentTimeMillis();
			iLogger.info("MCoreApp:AlertSubscription:setDefaultSubscribers:WebService Output -----> status:"+status+"; Total Time taken in ms:"+(endTime - startTime));
	
	
	
		}
		//****************************************** END of Populate Staging table with the current ownership details ******************************************	
	
		//KIRAN:20160119:method to get the minNodeName
		public String getMinNodeName(Statement stmt)
		{
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
			ResultSet rs=null;
			HashMap<String,String> m1 = new HashMap<String,String>();
	
			Properties prop=null;
			try
			{
				prop= StaticProperties.getConfProperty();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				fLogger.fatal("Error in intializing property File :"+e.getMessage());
				return "FAILURE";
			}
	
	
	
			String minNodeName = null;
			try
			{
	
				String query="SELECT a.node_name,b.count FROM ep_stage2_instances a left outer join (select count(*) " +
						"as count, node_name from device_status_info group by node_name ) b on a.node_name=b.node_name where a.status='1' " +
						"group by a.node_name";
				iLogger.info("Min_NodeName_Query : "+query);
				rs=stmt.executeQuery(query);
				String val=null;
				String num=null;
				while(rs.next())
				{
					val=rs.getString(1);
					num=rs.getString(2);
					iLogger.info("NodeName : "+val+" NodeName_Num: "+num);
					if(num==null){num="0";}
					m1.put(num, val);
				}
				String minKey = Collections.min(m1.keySet());
				minNodeName=m1.get(minKey);
				iLogger.info("Min_NodeName = "+minNodeName);
				if(minNodeName==null )
				{
					minNodeName = prop.getProperty("NodeName");
				}
				if(minNodeName!=null && (minNodeName.equalsIgnoreCase("null")||minNodeName.isEmpty()||minNodeName==""))
				{
	
					minNodeName = prop.getProperty("NodeName");
				}
	
			}catch(Exception e)
			{
				fLogger.error("Error while getting the node name : ",e);
			}	
	
			return minNodeName;
		}
	
		// Method to update the Edge Proxy device status info table to remove the dependency od data from WISE tables.
		public String setDeviceStatusInfo(int groupId, String groupName ,int typeId, String typeName , String vin)
		{
			Connection con=null;
			Statement statement =null;
			Logger iLogger = InfoLoggerClass.logger;
			Logger fLogger = FatalLoggerClass.logger;
	
	
			try{
				ConnectMySQL connectionObj = new ConnectMySQL();
				con = connectionObj.getEdgeProxyConnection();
	
				statement = con.createStatement(); 
				String updateQuery = "UPDATE device_status_info set group_id="+groupId+",type_id="+typeId+" ,group_name='"+groupName+"' ,type_name='"+typeName+"' where vin_no='"+vin+"'";
				//   System.out.println(updateQuery);
				statement.executeUpdate(updateQuery);
	
				iLogger.info("Updated DeviceStatusinfor table for the VIN:" + vin +", group Id " + groupId + " Type Id" + typeId);
				return "SUCCESS";
			}
			catch(Exception e)
			{
				fLogger.error("did not update DeviceStatusinfo for the VIN:" + vin +", group Id " + groupId + " Type Id" + typeId+" --- ", e.getMessage());
				e.printStackTrace();        
				return "FAILURE";
			}
			finally
			{
				try
				{
					if (statement != null) 
						statement.close(); 
	
					if(con!=null)
						con.close();
	
				}
	
				catch(SQLException e)
				{
					return "FAILURE";
				}
			}
		}
		public String getAssetTypeCode() {
			return assetTypeCode;
		}
		public void setAssetTypeCode(String assetTypeCode) {
			this.assetTypeCode = assetTypeCode;
		}
		public int getSubscription() {
			return subscription;
		}
		public void setSubscription(int subscription) {
			this.subscription = subscription;
		}
	
		/*		@Override
			public String toString() {
				return "AssetDetailsBO [serialNumber=" + serialNumber + ", nick_name="
						+ nick_name + ", description=" + description
						+ ", purchase_date=" + purchase_date + ", install_date="
						+ install_date + ", assetClassName=" + assetClassName
						+ ", assetGroupName=" + assetGroupName + ", assetTypeName="
						+ assetTypeName + ", model=" + model + ", productName="
						+ productName + ", renewalDate=" + renewalDate
						+ ", imeiNumber=" + imeiNumber + ", simNumber=" + simNumber
						+ ", iccidNumber=" + iccidNumber + ", extendedWarrantytype="
						+ extendedWarrantytype + ", driverName=" + driverName
						+ ", driverContactNumber=" + driverContactNumber + ", make="
						+ make + "]";
			}*/		
		
		
		
		
	}
	
	

	
	
