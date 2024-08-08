/*
 * CR428 : 20230830 : Dhiraj Kumar : Sea Ports (Landmark) Configurations
 */
package remote.wise.businessobject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
/**
 * LandmarkCategoryBO contain methods i.e
 * 1.getLandmarkCategory : to get LandmarkCategory
 * 2.setLandmarkCategory : to set LandmarkCategory
 * 3.getLandmarkAsset : to get LandmarkAsset
 * 4.setLandmarkAsset :to set LandmarkAsset
 * 5. DeleteLandmarkCategory : to delete LandmarkCategory
 * 6. getLandmarkDetails: to get LandmarkDetails
 * 7. setLandmarkDetails : to set LandmarkDetails 
 */
import java.util.ArrayList;
import java.util.Arrays;
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

import remote.wise.businessentity.AssetCustomGroupMapping;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.ClientEntity;
import remote.wise.businessentity.CustomAssetGroupEntity;
import remote.wise.businessentity.LandmarkAssetEntity;
import remote.wise.businessentity.LandmarkCategory2;
import remote.wise.businessentity.LandmarkCategoryEntity;
import remote.wise.businessentity.LandmarkDimensionEntity;
import remote.wise.businessentity.LandmarkEntity;
import remote.wise.businessentity.TenancyEntity;
import remote.wise.exception.CustomFault;
import remote.wise.handler.SendEmailWithKafka;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.implementation.DomainServiceImpl;
import remote.wise.service.implementation.LandmarkAssetImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;
/**
 * 
 * @author jgupta41
 *
 */
public class LandmarkCategoryBO extends BaseBusinessObject 
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("LandmarkCategoryBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("LandmarkCategoryBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("LandmarkCategoryBO:","info"); */
	 
	 

	
	private TenancyEntity Tenancy_ID;
	private int Landmark_id;
	private int Landmark_Category_ID;
	private String Landmark_Name;
	private String Latitude;
	private String Longitude;
	private double Radius;
	private String Address;	
	private int IsArrival;
	private int IsDeparture;
	private String Landmark_Category_Name;
	private String Landmark_Category_Color_Code;
	public String getLandmark_Category_Color_Code() {
		return Landmark_Category_Color_Code;
	}
	public void setLandmark_Category_Color_Code(String landmark_Category_Color_Code) {
		Landmark_Category_Color_Code = landmark_Category_Color_Code;
	}
	public int getLandmark_id() {
		return Landmark_id;
	}
	public void setLandmark_id(int landmark_id) {
		Landmark_id = landmark_id;
	}
	public String getLandmark_Name() {
		return Landmark_Name;
	}
	public void setLandmark_Name(String landmark_Name) {
		Landmark_Name = landmark_Name;
	}
	public String getLatitude() {
		return Latitude;
	}
	public void setLatitude(String latitude) {
		Latitude = latitude;
	}
	public String getLongitude() {
		return Longitude;
	}
	public void setLongitude(String longitude) {
		Longitude = longitude;
	}
	public double getRadius() {
		return Radius;
	}
	public void setRadius(double radius) {
		Radius = radius;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public int getIsArrival() {
		return IsArrival;
	}
	public void setIsArrival(int isArrival) {
		IsArrival = isArrival;
	}
	public int getIsDeparture() {
		return IsDeparture;
	}
	public void setIsDeparture(int isDeparture) {
		IsDeparture = isDeparture;
	}
	private String Color_Code;

		
	
	public TenancyEntity getTenancy_ID() {
		return Tenancy_ID;
	}
	public void setTenancy_ID(TenancyEntity tenancy_ID) {
		Tenancy_ID = tenancy_ID;
	}
	public int getLandmark_Category_ID() {
		return Landmark_Category_ID;
	}
	public void setLandmark_Category_ID(int landmark_Category_ID) {
		Landmark_Category_ID = landmark_Category_ID;
	}
	public String getLandmark_Category_Name() {
		return Landmark_Category_Name;
	}
	public void setLandmark_Category_Name(String landmark_Category_Name) {
		Landmark_Category_Name = landmark_Category_Name;
	}
	public String getColor_Code() {
		return Color_Code;
	}
	public void setColor_Code(String color_Code) {
		Color_Code = color_Code;
	}
	
	
	/**
	 * 
	 * @param Landmark_IdList
	 * @return landmarkEntityList
	 */
	
	public List<LandmarkEntity> LandmarkEntityList (List<Integer> Landmark_IdList)
	{
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
		iLogger.info("LandmarkEntityList returned size is"+Landmark_IdList.size());
		
		List<LandmarkEntity> landmarkEntityList = new LinkedList<LandmarkEntity>();		
     
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
    

        try
        {

		Query asset = session.createQuery("from LandmarkEntity where ActiveStatus=1 AND Landmark_id in (:list)").setParameterList("list", Landmark_IdList);
		Iterator it = asset.list().iterator();
		while(it.hasNext())
		{
			LandmarkEntity landmarkEntity = (LandmarkEntity) it.next();			
			landmarkEntityList.add(landmarkEntity);
		}
		iLogger.info("LandmarkEntity returned size is"+landmarkEntityList.size());
		    }
        catch(Exception e){

        	fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());

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

		return landmarkEntityList;
		}
	
/**
 * get list of LandmarkEntity for a given list of Landmark_CategoryIdList
 * @param LandmarkCategory_IdList
 * @return landmarkEntityList
 */
public List<LandmarkEntity> LandmarkCategoryEntityList1 (List<Integer> LandmarkCategory_IdList)
	{
	
	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;
		List<LandmarkEntity> landmarkEntityList = new LinkedList<LandmarkEntity>();	
		iLogger.info("LandmarkCategory_IdList returned size is"+LandmarkCategory_IdList.size());
		
	
		List<Integer> landmarkEntityList2 = new LinkedList<Integer>();	

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
    

        try
        {

		Query query = session.createQuery("from LandmarkDimensionEntity where  landMarkCategoryId in (:list)").setParameterList("list", LandmarkCategory_IdList);
		Iterator itr = query.list().iterator();
		while(itr.hasNext())
		{
			LandmarkDimensionEntity landmarkDimensionEntity = (LandmarkDimensionEntity)itr.next();
			landmarkEntityList2.add(landmarkDimensionEntity.getLandMarkId());
			
		}
		iLogger.info("LandmarkDimensionEntity returned size is"+landmarkEntityList2.size());
		
			Query query1 = session.createQuery("from LandmarkEntity where ActiveStatus=1 AND Landmark_id in (:list)").setParameterList("list", landmarkEntityList2);
		Iterator itr1 = query1.list().iterator();
		while(itr1.hasNext())
		{
			LandmarkEntity landmarkEntity = (LandmarkEntity)itr1.next();
			landmarkEntityList.add(landmarkEntity);
			
		}
	    }catch(Exception e){

        	fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());

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
        
        iLogger.info("LandmarkEntity returned size is"+landmarkEntityList.size());
		
		return landmarkEntityList;
		}
	/**
	 * 
	 * @param landmark_Category_ID
	 * @return landmarkCategoryEntityobj
	 */
	public LandmarkCategoryEntity getLandmark(int landmark_Category_ID)
	{
		LandmarkCategoryEntity landmarkCategoryEntityobj=new LandmarkCategoryEntity(landmark_Category_ID);
		
		if(landmarkCategoryEntityobj.getLandmark_Category_ID()==0)
			return null;
		else
			return landmarkCategoryEntityobj;
	} 
	/**
	 * 
	 * @param landmarkCategoryId
	 * @return landmarkIdList
	 */
	public List<Integer> getLandmarkId (List<Integer> landmarkCategoryId)
    {
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
          List<Integer> landmarkIdList = new LinkedList<Integer>();
          
          if(landmarkCategoryId==null)
          {
                return null;
          }
              
          ListToStringConversion conversionObj = new ListToStringConversion();
          String landmarkCategoryIdStringList = conversionObj.getIntegerListString(landmarkCategoryId).toString();
          Session session = HibernateUtil.getSessionFactory().getCurrentSession();
          session.beginTransaction();
      
          try
          {
          Query query = session.createQuery(" from LandmarkEntity where ActiveStatus=1 AND Landmark_Category_ID in ("+landmarkCategoryIdStringList+") ");
          Iterator itr = query.list().iterator();
          while(itr.hasNext())
          {
                LandmarkEntity landmark = (LandmarkEntity) itr.next();
                landmarkIdList.add(landmark.getLandmark_id());
          }
          }
          catch(Exception e){

            	fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());

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
            iLogger.info("landmarkIdList size is"+landmarkIdList.size());
          return landmarkIdList;
    }

		
	/**
	 * 
	 * @param Tenancy_ID
	 * @return landmarkCategoryEntityList
	 */
	public List<LandmarkCategory2> getLandmarkCategoryEntity(String Tenancy_ID)
	{
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	List<LandmarkCategory2> landmarkCategoryList = new LinkedList<>();
//		List<LandmarkCategoryEntity> landmarkCategoryEntityList = new LinkedList<LandmarkCategoryEntity>();
//		  Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//          session.beginTransaction();
//      
//          try
//          {
//        	  Query query = session.createQuery("from LandmarkCategoryEntity where ActiveStatus=1 AND Tenancy_ID="+Tenancy_ID );
//      		Iterator itr = query.list().iterator();
//      		while(itr.hasNext())
//      		{
//      			LandmarkCategoryEntity landmarkCategoryEntity = (LandmarkCategoryEntity) itr.next();
//      			landmarkCategoryEntityList.add(landmarkCategoryEntity);
//      				
//      		}
//          }
//          catch(Exception e){
//
//          	fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());
//
//          }
//          finally
//          {
//                if(session.getTransaction().isActive())
//                {
//                      session.getTransaction().commit();
//                }
//                
//                if(session.isOpen())
//                {
//                      session.flush();
//                      session.close();
//                }
//                
//          }
    	
    	//JCB6290.sn
		ConnectMySQL connFactory = new ConnectMySQL();
		try(Connection conn = connFactory.getConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery("SELECT * from landmark_catagory where ActiveStatus=1 AND Tenancy_ID in ("+Tenancy_ID+")"   ) ){
			while(rs.next())
      		{
      			LandmarkCategory2 landmarkCategory = new LandmarkCategory2();
      			landmarkCategory.setActiveStatus(rs.getInt("ActiveStatus"));
      			landmarkCategory.setColor_Code(rs.getString("Color_Code"));
      			landmarkCategory.setLandmark_Category_Color_Code(rs.getString("Landmark_Category_Color_Code"));
      			landmarkCategory.setLandmark_Category_ID(rs.getInt("Landmark_Category_ID"));
      			landmarkCategory.setLandmark_Category_Name(rs.getString("Landmark_Category_Name"));
      			landmarkCategory.setTenancy_ID(rs.getInt("Tenancy_ID"));
      			
      			landmarkCategoryList.add(landmarkCategory);
      				
      		}
		}catch (Exception e) {
			fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());
		}
		//JCB6290.en
		  iLogger.info("landmarkCategoryEntityList size is"+landmarkCategoryList.size());
			return landmarkCategoryList;
	}
	
	/**
	 * 
	 * @param landmark_id
	 * @return landmarkEntityobj
	 */
	public LandmarkEntity getLandmarkEntity(int landmark_id)
	{
		Logger iLogger = InfoLoggerClass.logger;
		LandmarkEntity landmarkEntityobj=new LandmarkEntity(landmark_id);
		  iLogger.info("To check whether the record with given Landmark_id exists");
		if(landmarkEntityobj.getLandmark_id()==0)
			return null;
		else
			return landmarkEntityobj;
	} 
	//********************************************** get Landmark Category for given Tenancy and Login_ID*****************************************
	/** DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
	 * This method gets all landmark category that belongs specified Tenancy ID and Login Id
	 * @param login_id:login_id
	 * @param Tenancy_ID:Tenancy_ID
	 * @return landmarkCategory_BO_list:Returns list of landmark categories for given Tenancy ID and Login Id 
	 * @throws CustomFault:custom exception is thrown when the login_id is not specified or invalid,Tenancy ID is invalid when specified
	 */
	public List<LandmarkCategoryBO> getLandmarkCategoryBOList(String login_id,int inputTenancyId) throws CustomFault 
	{
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger bLogger = BusinessErrorLoggerClass.logger;
		long startTime =System.currentTimeMillis();

		if(login_id==null)
			{
			bLogger.error("Please pass a login_id");
			throw new CustomFault("Please pass a login_id");
			}
		
		if(inputTenancyId==0){
			bLogger.error("Please pass a Tenancy_ID");
			throw new CustomFault("Please pass a Tenancy_ID");
		}
			
		//Get the List of all tenancyIds - Including Pseudo Tenancies
		 List<LandmarkCategoryBO> landmarkCategory_BO_list = new LinkedList<LandmarkCategoryBO>();
			
		 Session session = HibernateUtil.getSessionFactory().getCurrentSession();
         session.beginTransaction();
     
         try
         {
        	 int Tenancy_ID=0;
        	 Query q = session.createQuery(" from TenancyEntity t where t.tenancyCode = ( select a.tenancyCode from TenancyEntity a where a.tenancy_id='"+inputTenancyId+"') " +
											" order by t.tenancy_id ");
			 Iterator it = q.list().iterator();
			 if(it.hasNext())
			 {
				TenancyEntity tenancy = (TenancyEntity)it.next();
				Tenancy_ID = tenancy.getTenancy_id();
			 }
		
		
			 //Changes done BY Juhi for ordering by Landmark_Category_Name DF:20140114
			 String queryString = "from LandmarkCategoryEntity where ActiveStatus=1 and Tenancy_ID= "+Tenancy_ID+" order by Landmark_Category_Name ";
        	  Iterator itr=session.createQuery(queryString).list().iterator();
      		  while(itr.hasNext())
      		  { 
      			LandmarkCategoryEntity landmarkCategoryEntity = (LandmarkCategoryEntity) itr.next();
      		
      			LandmarkCategoryBO landmarkCategoryBO=new LandmarkCategoryBO();
      			
      			landmarkCategoryBO.setLandmark_Category_ID(landmarkCategoryEntity.getLandmark_Category_ID());
      			landmarkCategoryBO.setLandmark_Category_Name(landmarkCategoryEntity.getLandmark_Category_Name());
      			landmarkCategoryBO.setColor_Code(landmarkCategoryEntity.getColor_Code());
      			landmarkCategoryBO.setTenancy_ID(landmarkCategoryEntity.getTenancy_ID());
      			landmarkCategoryBO.setLandmark_Category_Color_Code(landmarkCategoryEntity.getLandmark_Category_Color_Code());
      			landmarkCategory_BO_list.add(landmarkCategoryBO);
      		  } 
      		long endTime =System.currentTimeMillis();
      		 iLogger.info("service time"+(endTime-startTime));
      		
          }
          catch(Exception e){

          	fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());

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
          
	
		return landmarkCategory_BO_list;
			
	}
	//*************************************************END of get Landmark Category for given Tenancy and Login_id************************************
	//********************************************** set Landmark Category for given Tenancy  *****************************************
	/** DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
	 * This method sets Color Code that belongs specified Landmark Category Name and Tenancy ID
	 * @param Landmark_Category_Name:Landmark_Category_Name
	 * @param Color_Code:Color_Code
	 * @param Tenancy_ID:Tenancy_ID
	 * @return SUCCESS:Return the status String as either Success/Failure.
	 * @throws CustomFault:custom exception is thrown when the Landmark_Category_Name,Tenancy ID is not specified or invalid
	 */
	public String setLandmarkCategory(int Landmark_Category_ID,String Landmark_Category_Name,String Color_Code,int inputTenancyId,String Landmark_Category_Color_Code) throws CustomFault
    {
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger bLogger = BusinessErrorLoggerClass.logger;
          long startTime =System.currentTimeMillis();
          if(Landmark_Category_Name==null){bLogger.error("Please pass a Landmark_Category_Name");
         
                throw new CustomFault("Please pass a Landmark_Category_Name");}
         
          if(Color_Code==null)
          {bLogger.error("Please pass a Color_Code");
                            throw new CustomFault("Please pass a Color_Code");
          }

          if(Landmark_Category_Color_Code==null)
          {bLogger.error("Please pass a Landmark_Category_Color_Code");
			throw new CustomFault("Please pass a Landmark_Category_Color_Code");
          }
          if(inputTenancyId==0)
          {
                bLogger.error("Please pass a Tenancy_ID");
                throw new CustomFault("Please pass a Tenancy_ID");
          }
          
          //DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
          //Get the List of all tenancyIds - Including Pseudo Tenancies
          Session session = HibernateUtil.getSessionFactory().openSession();
          session.beginTransaction();
 
          try
          {
        	  int Tenancy_ID=0;
        	  Query q = session.createQuery(" from TenancyEntity t where t.tenancyCode = ( select a.tenancyCode from TenancyEntity a where a.tenancy_id='"+inputTenancyId+"') " +
											" order by t.tenancy_id ");
        	  Iterator it = q.list().iterator();
        	  if(it.hasNext())
        	  {
        		  TenancyEntity tenancy = (TenancyEntity)it.next();
        		  Tenancy_ID = tenancy.getTenancy_id();
        	  }
			
			
        	  DomainServiceImpl domainService = new DomainServiceImpl();
        	  TenancyEntity tenancyEntity=domainService.getTenancyDetails(Tenancy_ID);
        	  if(tenancyEntity== null)
        	  {
	                bLogger.error("Invalid Tenancy_ID");
	                throw new CustomFault("Invalid Tenancy_ID");
	               
        	  }	
         
        	  if(Landmark_Category_ID!=0)
              {
                      Query query = session.createQuery("from LandmarkCategoryEntity where Tenancy_ID ="+Tenancy_ID+"and Landmark_Category_ID ="+Landmark_Category_ID+" and ActiveStatus=1 ");
                      Iterator itr = query.list().iterator();
                      while(itr.hasNext())
                            {    
                            LandmarkCategoryEntity landmarkCategoryObj = (LandmarkCategoryEntity)itr.next();
                            landmarkCategoryObj.setColor_Code(Color_Code);
                            landmarkCategoryObj.setLandmark_Category_Name(Landmark_Category_Name);
                            landmarkCategoryObj.setLandmark_Category_Color_Code(Landmark_Category_Color_Code);
                            session.update(landmarkCategoryObj);
                            }
                }
                else
                {
                	//DF:20140110 Code change done by Juhi on 2014-01-10 for Landmark_Category_Name already exists for given tenancy
                	Query query = session.createQuery("from LandmarkCategoryEntity where Tenancy_ID ="+Tenancy_ID+"and Landmark_Category_Name ='"+Landmark_Category_Name+"' and ActiveStatus=1 ");
                    Iterator itr = query.list().iterator();
                    if(itr.hasNext())
                         {
                    	bLogger.error("Landmark_Category_Name already exists for given tenancy");
                        throw new CustomFault("Landmark Category Name already exists for given tenancy");
                         }
                    else
                    {
                	
                	        LandmarkCategoryEntity landmarkCategoryObj =new LandmarkCategoryEntity();
                            landmarkCategoryObj.setLandmark_Category_Name(Landmark_Category_Name);
                            landmarkCategoryObj.setColor_Code(Color_Code);
                            landmarkCategoryObj.setTenancy_ID(tenancyEntity);
                            landmarkCategoryObj.setLandmark_Category_Color_Code(Landmark_Category_Color_Code);
                            landmarkCategoryObj.setActiveStatus(1);
                            landmarkCategoryObj.save();
                    }
         
                }
                long endTime =System.currentTimeMillis();
          iLogger.info("service time"+(endTime-startTime));
        }
       /* catch(Exception e){

          fatalError.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());

        }*/
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
         
          return "SUCCESS";
         
    }	//********************************************** End of set Landmark Category for given Tenancy  *****************************************
	//********************************************** get Asset for given Landmark *****************************************
	/**
	 *  This method will allow to get Asset for a given Landmark_id and Login_Id
	 * @param login_id :login_id
	 * @param Landmark_id:Landmark_id
	 * @return landmarkAssetImpl_list:List of assets for a given Landmark_id and Login_Id 
	 * @throws CustomFault custom exception is thrown when the login_id is not specified or invalid,Landmark_id is invalid when specified
	 */
	public List<LandmarkAssetImpl> getLandmarkAssetObj(String login_id,int Landmark_id , int pageNumber , int pageSize ) throws CustomFault 
	{
		
		long startTime =System.currentTimeMillis();
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger bLogger = BusinessErrorLoggerClass.logger;
    	int fromIndex = (pageNumber -1)* pageSize;
		if(login_id==null){
			bLogger.error("Please pass a login_id");
			throw new CustomFault("Please pass a login_id");
		}

			
				if(Landmark_id==0)
			{bLogger.error("Please pass a Landmark_id");
					throw new CustomFault("Please pass a Landmark_id");}
			
				
		
		List<LandmarkAssetImpl> landmarkAssetImpl_list = new LinkedList<LandmarkAssetImpl>();
		LandmarkAssetImpl landmarkAsset_obj=null;
		  Session session = HibernateUtil.getSessionFactory().getCurrentSession();
          session.beginTransaction();
      
          try
          {
        		String queryString = "from LandmarkAssetEntity where Landmark_id= "+Landmark_id;
        		Query query =session.createQuery(queryString);
        		query.setFirstResult(fromIndex);
        		query.setMaxResults(pageSize);
        		iLogger.info("Executing query: "+query.getQueryString());
        		Iterator itr= query.list().iterator();
        		List<String> Serial_number = new LinkedList<String>();
        		
        		while(itr.hasNext())
        		{ 
        			LandmarkAssetEntity landmarkEntity = (LandmarkAssetEntity) itr.next();
        			Serial_number.add(landmarkEntity.getSerial_number().getSerial_number().getSerialNumber());
        			
        		}
        		LandmarkAssetImpl landmarkAsset_obj1=new LandmarkAssetImpl();
        		landmarkAsset_obj1.setLandmark_id(Landmark_id);
        		landmarkAsset_obj1.setSerial_number(Serial_number);
        		landmarkAssetImpl_list.add(landmarkAsset_obj1);
        	
            
          
        /*  ListToStringConversion conversion = new ListToStringConversion();
      	String machineGroupIdAsString = conversion.getIntegerListString(assetGroupId).toString();
          try
          {
        		String queryString = "select l.Serial_number,c.group_name from LandmarkAssetEntity l,AssetCustomGroupMapping cu,CustomAssetGroupEntity c" +
        				" where l.Serial_number=cu.serial_number and cu.group_id=c.group_id and l.Landmark_id = "+Landmark_id+" and c.group_id in ("+machineGroupIdAsString+")";
        		Iterator itr=session.createQuery(queryString).list().iterator();
        		
        		//List<String> machineGroupName=new LinkedList<String>();
        		String machineGroupName=null;
        		Object[] result=null;
        		while(itr.hasNext())
        		{ 
        			//String Serial_number=null;
        			List<String> Serial_number = new LinkedList<String>();
        			 landmarkAsset_obj=new LandmarkAssetImpl();
        			result=(Object[])itr.next();
        			AssetEntity getSerialNumber=(AssetEntity)result[0];
        			Serial_number.add(getSerialNumber.getSerial_number().getSerialNumber());
      
        
        			
        			//machineGroupName.add(result[1].toString());
        			machineGroupName=result[1].toString();
        			landmarkAsset_obj.setLandmark_id(Landmark_id);
            		landmarkAsset_obj.setSerial_number(Serial_number);
            		landmarkAsset_obj.setAssetGroupName(machineGroupName);
            		landmarkAssetImpl_list.add(landmarkAsset_obj);
        			//LandmarkAssetEntity landmarkEntity = (LandmarkAssetEntity) itr.next();
        			
        			//Serial_number.add(landmarkEntity.getSerial_number().getSerial_number().getSerialNumber());
        			
        		}*/
        		
        		
        		long endTime =System.currentTimeMillis();
        		 iLogger.info("service time"+(endTime-startTime));
            
          }
         /* catch(Exception e){

          	fatalError.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());

          }*/
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
		return landmarkAssetImpl_list;
		
	}
	//*************************************************END of  get Landmark for a given Landmark************************************
	/**
	 * 
	 * 
	 * @param serial_number
	 * @param landmark_id
	 * @return true
	 */
	 boolean getLandmarkAsset(String serial_number,int landmark_id)
	 { 
		 
	    	Logger fLogger = FatalLoggerClass.logger;
		 Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		  session.beginTransaction();
		 try{
			 
		 Query query = session.createQuery("from LandmarkAssetEntity where Serial_number='"+serial_number+"' and Landmark_id="+landmark_id);
			Iterator itr = query.list().iterator();
			
			while(itr.hasNext())
			{
				return true;
				
			}
	 } catch(Exception e){

     	fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());

     }
     finally
     {
         
   	  
           if(session.isOpen())
           {
                 session.flush();
                 session.close();
           }
           
     }
		
		 return false;
		
	 }
	//**********************************************get serialNumber - AssetEntity Map for Serial number array****************************************
		/** This method returns a Map with serialNumber as key and the corresponding AssetEntity as its value
		 * @param sl_no list of serialNumbers
		 * @return Returns HashMap<serialNumber,AssetEntity>
		 * @throws CustomFault
		 */
		public HashMap<String,AssetEntity> getVINAssetEntityMap(List<String> sl_no) throws CustomFault
		{
	    	Logger fLogger = FatalLoggerClass.logger;
	    	
			HashMap<String,AssetEntity> asset_entity_map= new  HashMap<String,AssetEntity>();
			
			//Logger fatalError = Logger.getLogger("fatalErrorLogger");
			
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

				ListToStringConversion conversion = new ListToStringConversion();
	        	String serialNumberListAsString = conversion.getStringList(sl_no).toString();
				
				Query query= session.createQuery("from AssetEntity where serial_number in ("+serialNumberListAsString+") and active_status=true and client_id="+clientEntity.getClient_id()+"");
				Iterator itr=query.list().iterator();
				while(itr.hasNext())
				{
					AssetEntity asset = (AssetEntity)itr.next();
					asset_entity_map.put(asset.getSerial_number().getSerialNumber(), asset);
								
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
			
			return asset_entity_map;
		}
		
	 public void LandmarkSerialNumMapping(List<String> serialNumberList,LandmarkEntity landmark) throws CustomFault
		{

	    	Logger fLogger = FatalLoggerClass.logger;
	  
			HashMap<String,AssetEntity> assetEntityDetails=getVINAssetEntityMap(serialNumberList);
			
			HashMap<String,LandmarkAssetEntity> serialNumLandmarkMapEntity = new HashMap<String,LandmarkAssetEntity>();
			
			//Logger fatalError = Logger.getLogger("fatalErrorLogger");
	        
	        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	        session.beginTransaction();
	        
	        try
	        {
	        	
				Query query= session.createQuery("from LandmarkAssetEntity where Landmark_id="+landmark.getLandmark_id());
				Iterator itr=query.list().iterator();
				while(itr.hasNext())
				{
					LandmarkAssetEntity landmarkAssetMap = (LandmarkAssetEntity)itr.next();
					String serialNumber=landmarkAssetMap.getSerial_number().getSerial_number().getSerialNumber();
					serialNumLandmarkMapEntity.put(landmarkAssetMap.getSerial_number().getSerial_number().getSerialNumber(), landmarkAssetMap);
					
				}
				
				if(! (session.isOpen() ))
	            {
	                        session = HibernateUtil.getSessionFactory().getCurrentSession();
	                        session.getTransaction().begin();
	            }
				
				List<String> newSerialNumberList = new LinkedList<String>();		
				newSerialNumberList.addAll(serialNumberList);
							
				for(int i=0; i<serialNumberList.size(); i++)
				{
					if( (serialNumLandmarkMapEntity.containsKey(serialNumberList.get(i))) )
					{
						serialNumLandmarkMapEntity.remove(serialNumberList.get(i));
						newSerialNumberList.remove(serialNumberList.get(i));
					}
				}
				
				
				int j,k;
				for(j=0, k=0; j<newSerialNumberList.size()|| k<serialNumLandmarkMapEntity.size(); j++,k++)
				{
					
					//update the LandmarkAssetEntity with different serialNumber for the existing Landmark_id
					if(j<newSerialNumberList.size()&& k<serialNumLandmarkMapEntity.size())
					{
						LandmarkAssetEntity landmarkAssetMap = (LandmarkAssetEntity)serialNumLandmarkMapEntity.values().toArray()[k];
						Query updateQuery = session.createQuery("update LandmarkAssetEntity set Serial_number='"+newSerialNumberList.get(j)+"' " +
								" where Landmark_id="+landmarkAssetMap.getLandmark_id().getLandmark_id()+" and Serial_number='"+landmarkAssetMap.getSerial_number().getSerial_number().getSerialNumber()+"' ");
						updateQuery.executeUpdate();
					}
					
					//New LandmarkAssetEntity
					else if(j<newSerialNumberList.size() && k>=serialNumLandmarkMapEntity.size())
					{
						LandmarkAssetEntity newLandmarkAssetMap = new LandmarkAssetEntity();
						newLandmarkAssetMap.setLandmark_id(landmark);
						newLandmarkAssetMap.setSerial_number(assetEntityDetails.get(newSerialNumberList.get(j)));
						session.save(newLandmarkAssetMap);
					}
					
					//Delete LandmarkAssetEntity
					else if (j>=newSerialNumberList.size() && k<serialNumLandmarkMapEntity.size())
					{
						LandmarkAssetEntity landmarkAssetMap = (LandmarkAssetEntity)serialNumLandmarkMapEntity.values().toArray()[k];
						Query deleteQuery = session.createQuery("delete from LandmarkAssetEntity where Landmark_id="+landmarkAssetMap.getLandmark_id().getLandmark_id()+" and Serial_number='"+landmarkAssetMap.getSerial_number().getSerial_number().getSerialNumber()+"' ");
						deleteQuery.executeUpdate();
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
		
		}
	//********************************************** set Asset for given Landmark *****************************************
		/**
		 * This method will allow to set Asset for a given Landmark_id
		 * @param Serial_numbers: List of Serial_number
		 * @param Landmark_id:Landmark_id
		 * @param asset_group_id: List of asset_group_id
		 * @return SUCCESS:Returns the status String as either SUCCESS/FAILURE
		 * @throws CustomFault:custom exception is thrown when the Landmark_id is not specified or invalid,Landmark_id is invalid when specified
		 */
		public String setLandmarkAsset( String Login_Id,List<String> Serial_numbers,int Landmark_id ,List<Integer> asset_group_id) throws CustomFault
		{
			Logger iLogger = InfoLoggerClass.logger;
	    	Logger fLogger = FatalLoggerClass.logger;
	    	Logger bLogger = BusinessErrorLoggerClass.logger;
			long startTime =System.currentTimeMillis();
			
			
			if(Landmark_id==0){
		bLogger.error("Please pass a Landmark_id");
	throw new CustomFault("Please pass a Landmark_id");
	}
		
			List<AssetEntity> assetEntityList=null;
		int flag=0;
		AssetDetailsBO  assetDetailsObj = new AssetDetailsBO();
			
	  Session session = HibernateUtil.getSessionFactory().getCurrentSession();
      session.beginTransaction();
      List<String> serial_NumberList=new LinkedList<String>();
  List<AssetEntity> serialNumberList=new LinkedList<AssetEntity>();
  List<AssetEntity> existingSerialNumberList = new ArrayList<AssetEntity>();
 	          try
      
 	          {
 	        	  if(Serial_numbers!= null)
			{
 	        		 if(! (session.isOpen() ))

 	   	         {

 	   	                         session = HibernateUtil.getSessionFactory().getCurrentSession();

 	   	                         session.getTransaction().begin();

 	   	         }
		  		 assetEntityList=assetDetailsObj.assetEntityList(Serial_numbers);

			}
    	  
    	/*  Query query = session.createQuery("select max(Landmark_id) from LandmarkEntity where Login_Id='"+Login_Id+"'");
			Iterator itr = query.list().iterator();
			int Landmark_ID=0;
			while(itr.hasNext())
			{
				Landmark_ID=(Integer)itr.next();
				
				
			}*/
			DomainServiceImpl domainService = new DomainServiceImpl();
			if(! (session.isOpen() ))

	         {

	                         session = HibernateUtil.getSessionFactory().getCurrentSession();

	                         session.getTransaction().begin();

	         }
	  		LandmarkEntity landmarkEntity=domainService.getLandmarkEntity(Landmark_id);
    	  boolean exist=false;
    	//  if( (Serial_numbers == null) || (Serial_numbers.isEmpty()) ) 
  		if( (assetEntityList == null) || (assetEntityList.isEmpty()) )
  		{
  			iLogger.info("check if the asset is already there for given Asset_group_id");
  			
  			
  			AssetCustomGroupDetailsBO  assetCustomGroupDetailsBO  =new AssetCustomGroupDetailsBO ();
  			if(! (session.isOpen() ))

	         {

	                         session = HibernateUtil.getSessionFactory().getCurrentSession();

	                         session.getTransaction().begin();

	         }
  			List<AssetCustomGroupMapping> assetList=assetCustomGroupDetailsBO.getSerial_numbers(asset_group_id);
  			for(int j=0; j<assetList.size(); j++)
  			{
  				serial_NumberList.add(assetList.get(j).getSerial_number().getSerial_number().getSerialNumber());
  			/*	String serial_number=assetList.get(j).getSerial_number().getSerial_number().getSerialNumber();
  				
  				exist=getLandmarkAsset(serial_number, Landmark_id);
  			 if(!exist)
  			 {
  				serialNumberList.add(assetList.get(j).getSerial_number());
  				flag=0;
  			}
  			 else{
  				existingSerialNumberList.add(assetList.get(j).getSerial_number());
  			 }
  			      				
  			}
  			if(flag==0)
  				{
  					for(int s=0;s<serialNumberList.size();s++)
  					{
  				LandmarkAssetEntity landmarkAsset=new LandmarkAssetEntity();
  				landmarkAsset.setLandmark_id(landmarkEntity);
  				landmarkAsset.setSerial_number(serialNumberList.get(s));
  				landmarkAsset.save();
  				}
  				}*/
  			
  			}
  			if(! (session.isOpen() ))

	         {

	                         session = HibernateUtil.getSessionFactory().getCurrentSession();

	                         session.getTransaction().begin();

	         }
  			
  			LandmarkSerialNumMapping(serial_NumberList, landmarkEntity); 
  			
  		}
  		else
  		{
  			if(! (session.isOpen() ))

	         {

	                         session = HibernateUtil.getSessionFactory().getCurrentSession();

	                         session.getTransaction().begin();

	         }
  			LandmarkSerialNumMapping(Serial_numbers, landmarkEntity); 
  			/*
  			int update=0;
  		for(int i=0; i<assetEntityList.size(); i++)
  		{
  			
  			infoLogger.info("Check if the asset is already there to the given Landmark_id and serial no.");
  			String serial_number=assetEntityList.get(i).getSerial_number().getSerialNumber();
  			exist=getLandmarkAsset(serial_number, Landmark_id);
 			 if(!exist)
 			 {
 				serialNumberList.add(assetEntityList.get(i));
 				update=0;
 			}
 			 else{
   				existingSerialNumberList.add(assetEntityList.get(i));
   			 }
 			
 			}
  			  			if(update == 0)
  			{
  				for(int s=0;s<serialNumberList.size();s++)
					{
				LandmarkAssetEntity landmarkAsset=new LandmarkAssetEntity();
				landmarkAsset.setLandmark_id(landmarkEntity);
				landmarkAsset.setSerial_number(serialNumberList.get(s));
				landmarkAsset.save();
				}
  				
  			}
  		
  		
  		*/}
  		
//  		delete existig serial no.s if exists in DB 
		
/*  		List<String> db_serial_number_list =null;List<String> differList1=null;
			if(existingSerialNumberList.size()>0){
				db_serial_number_list =getLandmarkAssetObj(Login_Id,Landmark_id).get(0).getSerial_number();
				if(db_serial_number_list !=null && db_serial_number_list.size()>0){
					differList1 = new ArrayList<String>();
					differList1.addAll(db_serial_number_list);
					differList1.removeAll(serialNumberList);// delete these
					ListToStringConversion conversionObj = new ListToStringConversion();
					String serialNoToBeDeleted = null;
					if (differList1 != null && differList1.size() > 0) {

						serialNoToBeDeleted = conversionObj.getStringList(differList1).toString();
					}
					// delete these
					Query qu2 = session
							.createQuery("delete from LandmarkAssetEntity where Serial_number in ("
									+ serialNoToBeDeleted + ") AND Landmark_id = "+Landmark_id);
					int row1 = qu2.executeUpdate();
					
				}
			}*/
  		long endTime =System.currentTimeMillis();
		 iLogger.info("service time"+(endTime-startTime));
      }
      catch(Exception e){

      	fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());
      	e.printStackTrace();

      }
      finally
      {

        /*  if(session.getTransaction().isActive())
          {
                session.getTransaction().commit();
          }*/
          
    	   
         /*   if(session.isOpen())
            {
                  session.flush();
                  session.close();
            }*/
            
      }
	return "SUCCESS";
	}
		
		//*************************************************END of  set Landmark for a given Landmark************************************
	/**
	 * 
	 * @param Landmark_id
	 * @return SUCCESS
	 * @throws CustomFault
	 */
		public String setDeleteLandmarkCategory(int Landmark_Category_id) throws CustomFault
		{long startTime =System.currentTimeMillis();
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger bLogger = BusinessErrorLoggerClass.logger;
			if(Landmark_Category_id==0){
				bLogger.error("Please pass a  Landmark_Category_id");
			
				throw new CustomFault("Please pass a Landmark_Category_id");
			}
			
			LandmarkCategoryEntity landmarkEntity= getLandmark(Landmark_Category_id);
	  		int ActiveStatus=0;
			 Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	         session.beginTransaction();
	     
	         try
	         {
	        	
	     		if(landmarkEntity!= null)
	     		{
	     		
	     			Query query = session.createQuery("from LandmarkCategoryEntity where Landmark_Category_ID="+ Landmark_Category_id+" and ActiveStatus=1");
	     			Iterator itr = query.list().iterator();
	     			
	     			if(itr.hasNext())
	     			{
	     				LandmarkCategoryEntity landmarkcategory=(LandmarkCategoryEntity)itr.next();
	     				landmarkcategory.setActiveStatus((Integer)ActiveStatus);
	     				session.update(landmarkcategory);
	     				
	     				Query query1 = session.createQuery("from LandmarkEntity where Landmark_Category_ID="+ Landmark_Category_id+" and ActiveStatus=1");
	         			Iterator itr1 = query1.list().iterator();
	         			
	         			while(itr1.hasNext())
	         			{
	         				LandmarkEntity landmark=(LandmarkEntity)itr1.next();
	         				landmark.setActiveStatus((Integer)ActiveStatus);
	         				session.update(landmark);
	         				
	         			}
	     			}
	     			
	     		}
	     		else
	     		{
	     			bLogger.error(" Landmark_Category_ID "+Landmark_Category_ID+"doesn't exist in Landmark_Category Table");
	     			throw new CustomFault(" Landmark_Category_ID "+Landmark_Category_ID+"doesn't exist in Landmark_Category Table");
	     			
	     		}long endTime =System.currentTimeMillis();
	    		 iLogger.info("service time"+(endTime-startTime));
	     		  
	         }
	         catch(Exception e){

	         	fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());

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
			
			
			return "SUCCESS";
		}
	//********************************************** Delete Landmark for a given Landmark_id****************************
	/**
	 * Delete Landmark for a given Landmark_id
	 * @param Landmark_id  Landmark_Id
	 * @return SUCCESS
	 * @throws CustomFault custom exception is thrown when the user login id is not specified or invalid,Landmark Id  is invalid when specified
	 */
	public String setDeleteLandmark(int Landmark_id) throws CustomFault
	{long startTime =System.currentTimeMillis();
	
	Logger iLogger = InfoLoggerClass.logger;
	Logger fLogger = FatalLoggerClass.logger;
	Logger bLogger = BusinessErrorLoggerClass.logger;
		if(Landmark_id==0){
			bLogger.error("Please pass a  Landmark_id");
		
			throw new CustomFault("Please pass a Landmark_id");
		}
		 DomainServiceImpl domainServiceImpl =new DomainServiceImpl();
  		LandmarkEntity landmarkEntity= domainServiceImpl.getLandmarkEntity(Landmark_id);
  		int ActiveStatus=0;
		 Session session = HibernateUtil.getSessionFactory().getCurrentSession();
         session.beginTransaction();
     
         try
         {
        	
     		if(landmarkEntity!= null)
     		{
     		
     			Query query = session.createQuery("from LandmarkEntity where Landmark_id="+ Landmark_id+" and ActiveStatus=1");
     			Iterator itr = query.list().iterator();
     			
     			while(itr.hasNext())
     			{
     				LandmarkEntity landmark=(LandmarkEntity)itr.next();
     				landmark.setActiveStatus((Integer)ActiveStatus);
     				session.update(landmark);
     				
     			}
     		}
     		else
     		{
     			bLogger.error(" Landmark_id "+Landmark_id+"doesn't exist in Landmark Table");
     			throw new CustomFault(" Landmark_id "+Landmark_id+"doesn't exist in Landmark Table");
     			
     		}long endTime =System.currentTimeMillis();
    		 iLogger.info("service time"+(endTime-startTime));
     		  
         }
         catch(Exception e){

         	fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());

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
		
		
		return "SUCCESS";
	}
	//*************************************************END of  Delete Landmark for a given Landmark_id************************************
	//********************************************** get Landmark Details for given Tenancy and Login_Id *****************************************
/** DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
 * This method gets all landmark details that belongs specified Tenancy ID and Login Id
 * @param login_id:login_id
 * @param Tenancy_ID:Tenancy_ID
 * @return landmarkCategory_BO_list:Returns list of landmark details attached for the Tenancy ID  defined. 
 * @throws CustomFault:custom exception is thrown when the login_id is not specified or invalid,Tenancy ID is invalid when specified
 */


	public List<LandmarkCategoryBO> getLandmarkDetailsObjList(String login_id,int inputTenancyId) throws CustomFault 
	{
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger bLogger = BusinessErrorLoggerClass.logger;
		long startTime =System.currentTimeMillis();
		if(login_id==null){
			bLogger.error(" Please pass a Login Id");
			throw new CustomFault("Please pass a login Id");
		}
		if(inputTenancyId==0){
			bLogger.error("Please pass a Tenancy_ID");
			throw new CustomFault("Please pass a Tenancy_ID");
			}
		
		 //Session session = HibernateUtil.getSessionFactory().getCurrentSession(); //JCB6290.o
//		 Session session = HibernateUtil.getSessionFactory().openSession();//JCB6290.n
//         session.beginTransaction();
//         
//         List<LandmarkCategoryBO> landmarkCategory_BO_list = new LinkedList<LandmarkCategoryBO>();
//         
//		try
//		{
//			//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
//			//Get the List of all tenancyIds - Including Pseudo Tenancies
//			int Tenancy_ID=0;
//			Query q = session.createQuery(" from TenancyEntity t where t.tenancyCode = ( select a.tenancyCode from TenancyEntity a where a.tenancy_id='"+inputTenancyId+"') " +
//											" order by t.tenancy_id ");
//			Iterator it = q.list().iterator();
//			if(it.hasNext())
//			{
//				TenancyEntity tenancy = (TenancyEntity)it.next();
//				Tenancy_ID = tenancy.getTenancy_id();
//			}
//		
//		
//			
//			List<LandmarkCategory> landmarkList=getLandmarkCategoryEntity(Tenancy_ID);
//			List<Integer> landmarkCategoryList=new LinkedList<Integer>();
//		   	 for(int i=0;i<landmarkList.size();i++)
//		   	 {
//		   		 landmarkCategoryList.add(landmarkList.get(i).getLandmark_Category_ID());
//		   	 }
////		   	 Keerthi : 26/03/14 : checking for landmarkCategoryList size
//		   	 if(landmarkCategoryList.size()>0){
//		   		iLogger.info("landmarkCategoryList size is"+landmarkCategoryList.size());
//				ListToStringConversion conversionObj = new ListToStringConversion();
//				String landmarkCategory = conversionObj.getIntegerListString(landmarkCategoryList).toString();
//			
//				//DefectId:839 - Rajani Nagaraju - 20131216 - To enable Machine Movement between tenancies
//				if(! (session.isOpen() ))
//	            {
//	                        session = HibernateUtil.getSessionFactory().getCurrentSession();
//	                        session.getTransaction().begin();
//	            }
//				
//				/*String queryString="select a.Landmark_id,a.Landmark_Name,a.Latitude,a.Longitude,a.Radius,a.Address,a.IsArrival,a.IsDeparture,b.Landmark_Category_ID,b.Landmark_Category_Name" +
//					" ,b.Landmark_Category_Color_Code from LandmarkEntity a,LandmarkCategoryEntity b where a.Landmark_Category_ID=b.Landmark_Category_ID and b.Landmark_Category_ID  in ("+landmarkCategory+") and a.ActiveStatus=1 and b.ActiveStatus=1" +
//						//Changes done BY Juhi for ordering by Landmark_Name DF:20140110
//					"order by a.Landmark_Name ";*/
//				//DF20180920 :MANI:adding countrycode column in the query to fetch the countrycode from landmark table:
//				String queryString="select a.Landmark_id,a.Landmark_Name,a.Latitude,a.Longitude,a.Radius,a.Address,a.IsArrival,a.IsDeparture,b.Landmark_Category_ID,b.Landmark_Category_Name" +
//					" ,b.Landmark_Category_Color_Code,a.country_code from LandmarkEntity a,LandmarkCategoryEntity b where a.Landmark_Category_ID=b.Landmark_Category_ID and b.Landmark_Category_ID  in ("+landmarkCategory+") and a.ActiveStatus=1 and b.ActiveStatus=1" +
//						//Changes done BY Juhi for ordering by Landmark_Name DF:20140110
//					"order by a.Landmark_Name ";
//				Query query = session.createQuery(queryString);
//			
//			
//				Object result[]=null;
//			
//				Iterator itr=query.list().iterator();
//				while(itr.hasNext())
//				{ 
//					result=(Object[])itr.next();
//					String countrycode=(String) result[11];
//					LandmarkCategoryBO landmarkCategoryBO=new LandmarkCategoryBO();
//					landmarkCategoryBO.setLandmark_id((Integer)result[0]);
//					//DF20180920 :sending countrycode of the landmark for differentiating google india and googleSAARC for displaying in the UI front.
//					landmarkCategoryBO.setLandmark_Name(result[1].toString()+"|"+countrycode);
//					//landmarkCategoryBO.setLandmark_Name(result[1].toString());
//					landmarkCategoryBO.setLatitude(result[2].toString());
//					landmarkCategoryBO.setLongitude(result[3].toString());
//					landmarkCategoryBO.setRadius((Double)result[4]);
//					// DF20131022.sn
//					if ( result[5] != null ) {
//						landmarkCategoryBO.setAddress(result[5].toString());
//					}
//					// DF20131022.en
//					landmarkCategoryBO.setIsArrival((Integer)result[6]);
//					landmarkCategoryBO.setIsDeparture((Integer)result[7]);
//					landmarkCategoryBO.setLandmark_Category_ID((Integer)result[8]);
//					landmarkCategoryBO.setLandmark_Category_Name(result[9].toString());
//		//Added by Juhi on 24 july 2013 DefectId- 1043
//					landmarkCategoryBO.setLandmark_Category_Color_Code(result[10].toString());
//					landmarkCategory_BO_list.add(landmarkCategoryBO);
//				}
//		   	 }
//		   	 
//	         
//			long endTime =System.currentTimeMillis();
//			 iLogger.info("service time"+(endTime-startTime));
//         }catch(Exception e){
//
//         	e.printStackTrace();
//        	 fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());
//
//         }
//         
//         finally
//         {
//        	
//         
//               if(session.isOpen())
//               {
//                     session.flush();
//                     session.close();
//               }
//            }
		List<LandmarkCategoryBO> landmarkCategory_BO_list = new LinkedList<LandmarkCategoryBO>();
		ConnectMySQL connFactory = new ConnectMySQL();
		try(Connection conn = connFactory.getConnection();
				Statement statement = conn.createStatement()){
			String tenacySelectQuery = " Select tenancy_id from tenancy t where t.mapping_code = ( select a.mapping_code from tenancy a where a.tenancy_id='"+inputTenancyId+"') " +
					" order by t.tenancy_id ";
			ResultSet rs = statement.executeQuery(tenacySelectQuery);
			int Tenancy_ID = 0;
			List<Integer> listOfTenancyId = new ArrayList<Integer> ();
			while(rs.next()) {
				Tenancy_ID = rs.getInt("tenancy_id");
				listOfTenancyId.add(Tenancy_ID);
			}
			
			ListToStringConversion conversionObj = new ListToStringConversion();
			String tenancyIds = conversionObj.getIntegerListString(listOfTenancyId).toString();
			List<LandmarkCategory2> landmarkList=getLandmarkCategoryEntity(tenancyIds);
			
			List<Integer> landmarkCategoryList=new LinkedList<Integer>();
		   	 for(int i=0;i<landmarkList.size();i++){
		   		 landmarkCategoryList.add(landmarkList.get(i).getLandmark_Category_ID());
		   	 }
		   	 if(landmarkCategoryList.size()>0){
		   		iLogger.info("landmarkCategoryList size is"+landmarkCategoryList.size());
				String landmarkCategory = conversionObj.getIntegerListString(landmarkCategoryList).toString();

				String queryString="select a.Landmark_id,a.Landmark_Name,a.Latitude,a.Longitude,a.Radius,a.Address,a.IsArrival,a.IsDeparture,b.Landmark_Category_ID,b.Landmark_Category_Name" +
					" ,b.Landmark_Category_Color_Code,a.country_code from landmark a,landmark_catagory b where a.Landmark_Category_ID=b.Landmark_Category_ID and b.Landmark_Category_ID  in ("+landmarkCategory+") and a.ActiveStatus=1 and b.ActiveStatus=1" +
					" order by a.Landmark_Name ";
				iLogger.info("Executing query : "+ queryString);
				ResultSet rs2 = statement.executeQuery(queryString);
			
				while(rs2.next())
				{ 
					String countrycode=rs2.getString("country_code");
					LandmarkCategoryBO landmarkCategoryBO=new LandmarkCategoryBO();
					landmarkCategoryBO.setLandmark_id(rs2.getInt("Landmark_id"));
					landmarkCategoryBO.setLandmark_Name(rs2.getString("Landmark_Name")+"|"+countrycode);
					landmarkCategoryBO.setLatitude(rs2.getString("Latitude"));
					landmarkCategoryBO.setLongitude(rs2.getString("Longitude"));
					landmarkCategoryBO.setRadius(rs2.getDouble("Radius"));
					if (rs2.getString("Address") != null) {
						landmarkCategoryBO.setAddress(rs2.getString("Address"));
					}
					landmarkCategoryBO.setIsArrival(rs2.getInt("IsArrival"));
					landmarkCategoryBO.setIsDeparture(rs2.getInt("IsDeparture"));
					landmarkCategoryBO.setLandmark_Category_ID(rs2.getInt("Landmark_Category_ID"));
					landmarkCategoryBO.setLandmark_Category_Name(rs2.getString("Landmark_Category_Name"));
					landmarkCategoryBO.setLandmark_Category_Color_Code(rs2.getString("Landmark_Category_Color_Code"));
					landmarkCategory_BO_list.add(landmarkCategoryBO);
				}
		   	 }
		   	long endTime =System.currentTimeMillis();
		   	iLogger.info("service time"+(endTime-startTime));
			
		}catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());
		}
		

		return landmarkCategory_BO_list;
	}
	
	//*************************************************END of get Landmark Details for given Tenancy and Login_Id ************************************
		//********************************************** set Landmark Details for given Landmark_Category_ID  and Landmark_Name*****************************************
	/**
	 *  This method sets Landmark Details that belongs specified Landmark Category Name and Landmark_Name
	 * @param login_id:login_id
	 * * @param Landmark_id:Landmark_id
	 * @param Landmark_Category_ID:Landmark_Category_ID
	 * @param Landmark_Name:Landmark_Name:
	 * @param Landmark_Category_Name:Landmark_Category_Name
	 * @param Latitude:Latitude
	 * @param Longitude:Longitude
	 * @param Radius:Radius
	 * @param Address:Address
	 * @param IsArrival:IsArrival
	 * @param IsDeparture:IsDeparture
	 * @return int:Return Landmark_ID
	 * @throws CustomFault:custom exception is thrown when input like Landmark_Category_Name,Landmark_Name,Landmark_Category_ID is not specified or invalid
	 */
	public int setLandmarkDetails(String login_id,int Landmark_id,int Landmark_Category_ID,String Landmark_Name,String Landmark_Category_Name,String Latitude, String Longitude,Double Radius,String Address,int IsArrival,int IsDeparture) throws CustomFault
	{
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger bLogger = BusinessErrorLoggerClass.logger;
		long startTime =System.currentTimeMillis();
		if(login_id==null)
		{		bLogger.error("Please pass a login_id");
			throw new CustomFault("Please pass a login_id");
	}
		if(Landmark_Category_ID==0)
		{		bLogger.error("Please pass a Landmark_Category_ID");
			throw new CustomFault("Please pass a Landmark_Category_ID");
	}
	if(Landmark_Name==null)
		{		bLogger.error("Please pass a Landmark_Name");
			throw new CustomFault("Please pass a Landmark_Name");
	}
		if(Latitude==null)
		{		bLogger.error("Please pass a Latitude");
			throw new CustomFault("Please pass a Latitude");
	}
		if(Longitude==null)
		{			bLogger.error("Please pass a Longitude");
			throw new CustomFault("Please pass a Longitude");
		}
			LandmarkCategoryEntity landmarkCategoryEntity=getLandmark(Landmark_Category_ID);
      	 if(landmarkCategoryEntity== null)
      		{
      		 bLogger.error("Invalid Landmark_Category_ID");
      			throw new CustomFault("Invalid Landmark_Category_ID");
      		}
      	 int Landmark_Id=0;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try
        {
        	
       
       	 if(Landmark_id==0){
       		 
       		//DF:20140110 Code change done by Juhi on 2014-01-10 for Same Landmark_Name already exists for given Landmark_Category_ID
       			Query q = session.createQuery("from LandmarkEntity where ActiveStatus=1 and Landmark_Category_ID ="+Landmark_Category_ID+"and Landmark_Name ='"+Landmark_Name+"'");
       			int updateFlag=0;
       			Iterator itr = q.list().iterator();
       			/*while(itr.hasNext())
   				{	
   		      
   						updateFlag=1; 
   						break;
   				}*/
       			if(itr.hasNext())
       				{	
       				bLogger.error("Same Landmark_Name already exists for given Landmark_Category_ID");
                    throw new CustomFault("Landmark already exists with the same name for given landmark category.Please create another landmark with different landmark name");
       						//updateFlag=1; 
       						//break;
       				}
       			
       			if(updateFlag == 0)
       			{
       				LandmarkEntity landmarkObj =new LandmarkEntity();
       			    landmarkObj.setAddress(Address)	;
       			    landmarkObj.setIsArrival(IsArrival);
       			    landmarkObj.setIsDeparture(IsDeparture);
       			    landmarkObj.setLandmark_Category_ID(landmarkCategoryEntity);
       			// DF20180920 ::MANI ::Adding new column country_code in landmark
					// table. getting the countrycode in landmark name with |
					// seperated."landmarkName|countrycode"
       			    if(Landmark_Name.split("\\|").length>1)
       			    {
       			    	landmarkObj.setLandmark_Name(Landmark_Name.split("\\|")[0]);
		       		 		landmarkObj.setCountry_code(Landmark_Name.split("\\|")[1]);
       			    }
       			    else
       			    {
       			    	landmarkObj.setLandmark_Name(Landmark_Name);
       			    }
       			   	// landmarkObj.setLandmark_Name(Landmark_Name);
       			    landmarkObj.setLatitude(Latitude);
       			    landmarkObj.setLongitude(Longitude);
       			    landmarkObj.setRadius(Radius);
       			 landmarkObj.setLogin_Id(login_id);
       			    int activestatus=1;
       		 		landmarkObj.setActiveStatus(activestatus);
       			    landmarkObj.save();
       			} 
       	 }
       	 else
       	 {
       		 	
       		 	LandmarkEntity landmarkObj=getLandmarkEntity(Landmark_id);
       		 if(! (session.isOpen() ))

             {

                             session = HibernateUtil.getSessionFactory().getCurrentSession();

                             session.getTransaction().begin();

             }
       		 	if(landmarkObj!= null)
       		 	{
       		 	
   		 		landmarkObj.setAddress(Address)	;
   		 		landmarkObj.setIsArrival(IsArrival);
   		 		landmarkObj.setIsDeparture(IsDeparture);
   		 		landmarkObj.setLandmark_Category_ID(landmarkCategoryEntity);
   		 	// DF20180920 :MANI: Adding new column country_code in landmark
				// table. getting the countrycode in landmark name with |
				// seperated."landmarkName|countrycode"
		 	  if(Landmark_Name.split("\\|").length>1)
			    {
			    	landmarkObj.setLandmark_Name(Landmark_Name.split("\\|")[0]);
       		 		landmarkObj.setCountry_code(Landmark_Name.split("\\|")[1]);
			    }
			    else
			    {
			    	landmarkObj.setLandmark_Name(Landmark_Name);
			    }
   		 		//landmarkObj.setLandmark_Name(Landmark_Name);
   		 		landmarkObj.setLatitude(Latitude);
   		 		landmarkObj.setLongitude(Longitude);
   		 		landmarkObj.setRadius(Radius);
   		 		int activestatus=1;
   		 	 landmarkObj.setLogin_Id(login_id);
   		 		landmarkObj.setActiveStatus(activestatus);
   		 	session.update(landmarkObj);    		 		
   	    
       		 		
       		 	}
       		 	else
       		 	{
       		 	
       		 	}
       	 }
    	 if(! (session.isOpen() ))

         {

                         session = HibernateUtil.getSessionFactory().getCurrentSession();

                         session.getTransaction().begin();

         }
       	Query query = session.createQuery("select max(Landmark_id)from LandmarkEntity where ActiveStatus=1 and Login_Id='"+login_id+"'");
       	Iterator itr1 = query.list().iterator();
			while(itr1.hasNext())
				{	
				Landmark_Id=(Integer)itr1.next();
				
				}
       	long endTime =System.currentTimeMillis();
		 iLogger.info("service time"+(endTime-startTime));
       	}/*catch(Exception e){

        	fatalError.fatal("Hello this is an Fatal Error. Need immediate Action"+e.getMessage());

        }*/
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
       
	return Landmark_Id;
	}
	//*************************************************END of set Landmark Details for given Landmark_Category_ID  and Landmark_Name ************************************
	//CR428.sn
	public String setSeaportLandmarkAsset(String loginId, int landMarkId) throws CustomFault{

		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger bLogger = BusinessErrorLoggerClass.logger;
    	String response = "FAILURE";

    	Properties prop = CommonUtil.getDepEnvProperties();
    	String emailId = prop.getProperty("SeaPortLandmarkCreationMailId");
    	Timestamp createdTimestamp = new Timestamp(new Date().getTime());
		String transactionTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdTimestamp.getTime());
    	
    	List<String> vinList = new ArrayList<>();
    	
		if(landMarkId==0){
			bLogger.error("Please pass a landMarkId");
			throw new CustomFault("Please pass a landMarkId");
		}
		
		String selectQuery = "SELECT serial_number from asset where status=1";
		ConnectMySQL conFactory = new ConnectMySQL();
		try (Connection con = conFactory.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(selectQuery)){
			while(rs.next()) {
				vinList.add(rs.getString("serial_number"));
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("setSeaportLandmarkAsset : Exception occured : ", e.getMessage());
			return response;
		}
		
		int MAXBATCHCOUNT = 50;
		int MAXROWS = 1000;
		int rowCount = 0;
		int batchCount = 0;
		int[] insertCount = null;
		int totalInsertedCount = 0;
		
		try (Connection con = conFactory.getConnection();
				Statement st = con.createStatement()){
			StringBuilder insertQBuilder = new StringBuilder(
					"INSERT IGNORE INTO landmark_asset (landmark_id, serial_number) VALUES ");

			for (String vin : vinList) {
				insertQBuilder.append("(");
				insertQBuilder.append(landMarkId);
				insertQBuilder.append(",'");
				insertQBuilder.append(vin);
				insertQBuilder.append("'),");
				rowCount++;
				
				if (rowCount % MAXROWS == 0) {
					st.addBatch(insertQBuilder.toString().substring(0, insertQBuilder.toString().length() - 1));
					batchCount++;
					insertQBuilder = new StringBuilder(
							"INSERT IGNORE INTO landmark_asset (landmark_id, serial_number) VALUES ");

					// Execute the the batch
					if (batchCount % MAXBATCHCOUNT == 0) {
						insertCount = st.executeBatch();
						totalInsertedCount += Arrays.stream(insertCount).sum();
						iLogger.info("setSeaportLandmarkAsset : Inserted Count : " + Arrays.stream(insertCount).sum());
					}
				}
			}
			if (insertQBuilder.toString().replace(" ", "").endsWith("'),")) {
				st.addBatch(insertQBuilder.toString().substring(0, insertQBuilder.toString().length() - 1));
			}
			// execute remaining
			insertCount = st.executeBatch();
			totalInsertedCount += Arrays.stream(insertCount).sum();
			iLogger.info("setSeaportLandmarkAsset : Inserted Count :" + Arrays.stream(insertCount).sum());
			iLogger.info("setSeaportLandmarkAsset : Total Row Inserted Count:" + totalInsertedCount);

			String emailSubject = "Landmark added to GeoFenceForPortArea";
			String emailBody = "Hi,\n\nLandmark has been added to GeoFenceForPortArea with id "+landMarkId+" by user " + loginId
					+ " at  " + transactionTime + ".\n\nRegards,\nLiveLinkIndia";
			
			//send confirmation mail
			SendEmailWithKafka sendEmail = new SendEmailWithKafka();
			iLogger.info("send Email to: emailID= " + emailId);
			String result = sendEmail.sendMail(emailId, emailSubject, emailBody,"", transactionTime);

			iLogger.info("Mail status : " + result);
			
			response= "SUCCESS";

		}catch (Exception e) {
			iLogger.info("setSeaportLandmarkAsset : Issue ocurred while updating landmark_asset table : " + e.getMessage());
			e.printStackTrace();
			return response;
		}
		
		return response;
	}
	//CR428.en
}
