package remote.wise.service.implementation;

import java.text.ParseException;
import java.util.Iterator;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.wipro.mcoreapp.businessobject.EADataPopulationBO;

import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.EngineTypeEntity;
import remote.wise.businessentity.ProductProfileEntity;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

/** Implementation class to set Asset Personality details
 * @author Rajani Nagaraju
 *
 */
public class AssetPersonalityImpl 
{
	/*public static WiseLogger fatalError = WiseLogger.getLogger("AssetPersonalityImpl:","fatalError");
	public static WiseLogger businessError = WiseLogger.getLogger("AssetPersonalityImpl:","businessError");*/
	
	/*
	/** This method sets the Personality details of a Machine
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
	 
	public String setAssetPersonalityDetails(String machineName, String assetGroupName, String assetTypeName, String installDate,
											String serialNumber, String description, String purchaseDate, String assetClassName,String engineTypeName, int make) throws CustomFault
	{
		AssetDetailsBO assetDetails = new AssetDetailsBO();
		
		Logger businessError = Logger.getLogger("businessErrorLogger");
		
		
			if( (machineName == null && serialNumber==null ) || assetGroupName==null || assetTypeName ==null)
			{
				return "FAILURE";
			}
		
			if(machineName!=null)
			{
				AssetEntity asset = assetDetails.getAssetByNickName(machineName);
				if(asset==null || asset.getSerial_number()==null)
				{
					return "FAILURE";
				}
			}
		
			if(serialNumber!=null)
			{
				AssetEntity asset = assetDetails.getAssetEntity(serialNumber);
				if(asset==null || asset.getSerial_number()==null)
				{
					return "FAILURE";
				}
				
				if(machineName!=null)
				{
					if( !(asset.getNick_name().equalsIgnoreCase(machineName)) )
					{
						return "FAILURE";
					}
				}
				
			}
		
		String status = assetDetails.setAssetPersonalityDetails(machineName,assetGroupName,assetTypeName,installDate,serialNumber,
																description,purchaseDate, assetClassName,engineTypeName, make);
		
		return status;
	}*/
	
	
	/** This method sets the Personality details of a Machine
	 * @param engineNumber engine Number of the Machine
	 * @param assetGroupCode Machine Profile Code
	 * @param assetTypeCode Model Code
	 * @param engineTypeCode Code of Engine Type
	 * @param assetBuiltDate Machine Built Date
	 * @param make Machine Make
	 * @param fuelCapacity Fuel Capacity of fuel Tank
	 * @param serialNumber VIN
	 * @return Returns the status String
	 * @throws CustomFault
	 */
	public String setAssetPersonalityDetails(String engineNumber, String assetGroupCode, String assetTypeCode, String engineTypeCode,
											String assetBuiltDate, String make, String fuelCapacity, String serialNumber, String messageId)
	{
		
		String status = "SUCCESS-Record Processed";
		AssetDetailsBO assetDetails = new AssetDetailsBO();
		String makeTemp=null;
		int productId=0;
		
		
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
	    
	    //DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
		if(assetGroupCode==null || assetGroupCode.trim()==null ||  assetGroupCode.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter assetGroupCode is NULL";
			bLogger.error("EA Processing: AssetPersonality: "+messageId+" : Mandatory Parameter assetGroupCode is NULL ");
			return status;
		}
		
		if(assetTypeCode==null || assetTypeCode.trim()==null ||  assetTypeCode.replaceAll("\\s","").length()==0 )
		{
			status = "FAILURE-Mandatory Parameter assetTypeCode is NULL";
			bLogger.error("EA Processing: AssetPersonality: "+messageId+" : Mandatory Parameter assetTypeCode is NULL ");
			return status;
		}
		
		//DF20140715 - Rajani Nagaraju -Remove extra Spaces
		assetGroupCode = assetGroupCode.replaceAll("\\s","") ;
		assetTypeCode= assetTypeCode.replaceAll("\\s","") ;
		if(engineNumber!=null)
			engineNumber=engineNumber.replaceAll("\\s","") ;
		if(serialNumber!=null)
			serialNumber=serialNumber.replaceAll("\\s","") ;
		
		
		//Added by Juhi DF:20140117
		if( (engineNumber==null || engineNumber.length()==0) && (serialNumber==null||serialNumber.length()==0))
		{
			status = "FAILURE-Both EngineNumber and SerialNumber is NULL";
			bLogger.error("EA Processing: AssetPersonality: "+messageId+" : Both EngineNumber and SerialNumber is NULL ");
			return status;
		}
		
		
		//DF20140712 - Rajani Nagaraju - Check for blank EngineType
		if(engineTypeCode!=null)
			engineTypeCode = engineTypeCode.replaceAll("\\s","") ;
		if(make!=null)
			make=make.replaceAll("\\s","") ;
		if(fuelCapacity!=null)
			fuelCapacity=fuelCapacity.replaceAll("\\s","");
		
		try
	     {
			//DF20140712 - Rajani Nagaraju - Check for the Machine Number in Serial Number field in the incoming data packet
			AssetEntity asset = null;
			
			if(serialNumber!=null)
			{
				Query assetQ = session.createQuery(" from AssetEntity where serial_number='"+serialNumber+"'");
				Iterator assetItr = assetQ.list().iterator();
				while(assetItr.hasNext())
				{
					asset = (AssetEntity) assetItr.next();
				}
				if(asset==null)
				{
					int assetPresent=0;
					String machineNumber=serialNumber.replaceFirst("^0+(?!$)", "");
					Query machNumQ = session.createQuery(" from AssetEntity where machineNumber='"+machineNumber+"'");
					Iterator machNumItr = machNumQ.list().iterator();
					while(machNumItr.hasNext())
					{
						asset = (AssetEntity) machNumItr.next();
						assetPresent=1;
						serialNumber=asset.getSerial_number().getSerialNumber();
					}
					
					
					if(assetPresent==0)
					{
						AssetControlUnitEntity assetControl=null;
						Query assetControlQ = session.createQuery(" from AssetControlUnitEntity where serialNumber like '%"+machineNumber+"%'");
						Iterator assetControlItr = assetControlQ.list().iterator();
						while(assetControlItr.hasNext())
						{
							assetControl= (AssetControlUnitEntity)assetControlItr.next();
						}
						
						if(assetControl==null)
						{
							status = "FAILURE-PIN Registration Data not received";
							bLogger.error("EA Processing: AssetPersonality: "+messageId+" : PIN Registration Data not received");
							return status;
						}
						else
						{
							status = "FAILURE-Roll off Data not received";
							bLogger.error("EA Processing: AssetPersonality: "+messageId+" : Roll off Data not received");
							return status;
						}
					}
				}


				
				
			}
			
			//DF20140715 - Rajani Nagaraju - Adding else block
			else
			{
				Query engineNumQ = session.createQuery(" from AssetEntity where nick_name='"+engineNumber+"'");
				Iterator engineNumItr = engineNumQ.list().iterator();
				while(engineNumItr.hasNext())
				{
					asset = (AssetEntity) engineNumItr.next();
					serialNumber=asset.getSerial_number().getSerialNumber();
				}
				
				
				if(asset==null)
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
						bLogger.error("EA Processing: AssetPersonality: "+messageId+" : PIN Registration Data not received");
						return status;
					}
					else
					{
						status = "FAILURE-Roll off Data not received for the specified Engine Number";
						bLogger.error("EA Processing: AssetPersonality: "+messageId+" : Roll off Data not received for the specified Engine Number");
						return status;
					}
				}
			}
			

			//DF20141103 - Rajani Nagaraju - To Process the record even if engineNumber is ""(blank)
			if( (engineNumber==null )	|| (engineNumber.length()==0 ) )
			{
				engineNumber=asset.getNick_name();
	    		makeTemp=asset.getMake();
	    		if(asset.getProductId()!=null)
	    			productId=asset.getProductId().getProductId();
			}
			
			if(engineTypeCode==null || engineTypeCode.length()==0)
			{
			   	 Query query = session.createQuery(" from EngineTypeEntity ");
			   	 Iterator itr = query.list().iterator();
			   	 if(itr.hasNext())
			   	 {
			   		 EngineTypeEntity engineType=(EngineTypeEntity)itr.next();	    		
			   		 engineTypeCode=engineType.getEngineTypeCode();
			   	 }
			}
		
			if(make==null || make.length()==0)
			{
				if(makeTemp==null || makeTemp.length()==0)
				{ 
					makeTemp=asset.getMake();
				}
				make=makeTemp;
			}
			
			if(fuelCapacity==null || fuelCapacity.length()==0)
			{
				if(productId!=0)
				{
					Query query = session.createQuery("from ProductProfileEntity where productId = '"+productId+"'");
			    	Iterator itr = query.list().iterator();
			    	while(itr.hasNext())
			    	{
			    		ProductProfileEntity productProfile=(ProductProfileEntity)itr.next();
			    		fuelCapacity=Double.toString(productProfile.getFuelCapacityInLitres());
			    	}	
				}
				
			}
	     }
	     
	      
	    catch(Exception e)
		{
	    	status = " FAILURE-"+e.getMessage();
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
            	//DF20150603 - Rajani Nagaraju - WISE going down issue - Adding try catch against commit
            	fLogger.fatal("Exception in commiting the record:"+e);
            	status = " FAILURE-"+e.getMessage();
            }
            
            if(session.isOpen())
            {
          	  	session.flush();
                session.close();
            }
                  
         }
		
        //Validate engineNumber
		//DF20141209 - Rajani Nagaraju - Remove Logic based on Engine Number 
		/*AssetEntity asset = assetDetails.getAssetByNickName(engineNumber);
		if(asset==null || asset.getSerial_number()==null)
		{
			//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
			status = "FAILURE-Invalid Engine Number";
			businessError.error("EA Processing: AssetPersonality: "+messageId+" : Invalid Engine Number");
			return status;
			
		}*/
		
		
		status = assetDetails.setAssetPersonalityDetails(engineNumber,assetGroupCode,assetTypeCode,engineTypeCode,assetBuiltDate,
															make,fuelCapacity, serialNumber, messageId);
		
		//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
		//DF20151019 - Rajani Nagaraju - Populating required details in OrientDB
		/*iLogger.info("EA Processing: AssetPersonality: "+messageId+ ": Insert details into OrientAppDB");
		String messageString=serialNumber+"|"+assetGroupCode+"|"+assetTypeCode+"|"+engineTypeCode;
		String orientDbStatus = new EADataPopulationBO().AssetPersonality(messageId, messageId, messageString);
		iLogger.info("EA Processing: AssetPersonality:  "+messageId+ ": Insert details into OrientAppDB Status:"+orientDbStatus);*/
				
		return status;
	
	}
}
