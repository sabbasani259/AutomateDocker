package remote.wise.service.implementation;

import java.util.Properties;
import java.util.Random;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.TenancyBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
//import remote.wise.util.WiseLogger;


/** Implementation Class to set Account Details
 * @author Rajani Nagaraju
 *
 */
public class AccountDetailsImpl 
{	
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("AccountDetailsImpl:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("AccountDetailsImpl:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("AccountDetailsImpl:","info");*/
	
	/** This method sets the Dealer Account Details received from ERP system
	 * @param accountCode DealerCode
	 * @param accountName Name of the Account
	 * @param addressLine1 AddressDetailsLine1
	 * @param addressLine2 AddressDetailsLine2
	 * @param city 
	 * @param zipCode
	 * @param state
	 * @param zone
	 * @param country
	 * @param email
	 * @param contactNumber
	 * @param fax
	 * @param parentAccountCode parent Account Code that builds the hierarchy
	 * @return
	 */
	public String setDealerAccountDetails (String accountCode, String accountName, String addressLine1, String addressLine2, String city,
									 String zipCode, String state, String zone, String country, String email,
									 String contactNumber, String fax, String parentAccountCode, String messageId)
	{
		String status = "SUCCESS-Record Processed";
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		
    	Logger fLogger = FatalLoggerClass.logger;
    	Logger bLogger = BusinessErrorLoggerClass.logger;
    	
		
		String PatnershipOEM=null;
		String ParnershipDealer = null;
		
		
		//Check for Mandatory fields
		try
		{
			if(accountCode==null || accountCode.replaceAll("\\s","").length()==0)
			{
				throw new CustomFault("Mandatory parameter Dealer AccountCode is not received !!");
			}
			
			TenancyBO tenancyBoObj = new TenancyBO();
			//DF20140325 - Rajani Nagaraju - Get the Corresponding LL Account Code from ECC/CRM Dealer Code from Mapping table
			String llAccountCode = tenancyBoObj.getLLAccountCode(accountCode);
			
			if(llAccountCode==null)
			{
				throw new CustomFault("Data not found in Mapping table for the Dealer AccountCode:"+accountCode);
			}
			else
			{
				//DF20140327 - Rajani Nagaraju - Imposing the condition ecc_acc code should be equal to LL account code. Otherwise data with diff ecc_code but same ll_code
				//can get inserted twice since it is running as a parallel thread
				if(! (accountCode.equalsIgnoreCase(llAccountCode)) )
				{
					//DF20140328 - Rajani Nagaraju - Such record should not get inserted into fault_details 
					bLogger.error("Input AccountCode: "+accountCode+" is not matching with the Corresponding LL Account Code in Mapping table");
					//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
					return "SUCCESS-Record Processed";
				}
				
				accountCode = llAccountCode;
			}
			
			//DF20140402 - Rajani Nagaraju - Placing this Mandatory check after checking on Mapping table
			if(accountName==null || accountName.replaceAll("\\s","").length()==0)
			{
				throw new CustomFault("Mandatory parameter Dealer AccountName is not received !!");
			}
			
			if( parentAccountCode==null || parentAccountCode.replaceAll("\\s","").length()==0)
			{
				throw new CustomFault("Mandatory parameter ParentAccountCode (Ro Code) is not received !!");
			}
			
			
			/*String chkAccountCode = accountCode.replaceAll("\\s","") ;
			String chkAccountName = accountName.replaceAll("\\s","") ;
			String chkParentAccountCode = parentAccountCode.replaceAll("\\s","") ;
			
			if((!(chkAccountCode.length()>0))|| (!(chkAccountName.length()>0)) || (!(chkParentAccountCode.length()>0)) )
			{
				status = "FAILURE";
				return status;
			}
			*/
			//------------------- get the required parameters from properties file
			
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			PatnershipOEM= prop.getProperty("PatnershipOEM");
			ParnershipDealer= prop.getProperty("ParnershipDealer");
			//2014-06-18 : Removing # in the Emaild and Mobile Number - Deepthi
			if(email!=null && email.contains("#")){
				email = null;
			}
			if(contactNumber!=null && contactNumber.contains("#")){
				contactNumber=null;
			}
			//2014-06-18 : Removing # in the Emaild and Mobile Number - Deepthi
			status =tenancyBoObj.setAccountDetails(accountCode, accountName, addressLine1, addressLine2, city, zipCode, state, zone, 
					country, email, contactNumber, fax, parentAccountCode,PatnershipOEM, ParnershipDealer,true, messageId);
			
		}
		catch(CustomFault e)
		{
			//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
			status = "FAILURE-"+e.getFaultInfo();
			bLogger.error("EA Processing: DealerInfo: "+messageId+" : "+e.getFaultInfo());
		}
		
		catch(Exception e)
		{
			//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
			status = "FAILURE-"+e.getMessage();
			fLogger.fatal("EA Processing: DealerInfo: "+messageId+ " Fatal Exception :"+e);
		}
			
		return status;
		
	}
	
	
	//New Customer Account Details - For Sale Interfac
	
	
	/** This method sets the Customer Account Details received from ERP system
	 * @param accountCode Customer Code
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
	 * @param parentAccountCode
	 * @return
	 */
	public String setCustomerAccountDetails_New (String accountCode, String accountName, String addressLine1, String addressLine2, String city,
			 String zipCode, String state, String zone, String country, String email,String contactNumber, String fax, String parentAccountCode, String messageId)
	{
		String status = "SUCCESS-Record Processed";
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
		String ParnershipDealer=null;
		String PartnershipCustomer = null;
	
	
		//Check for Mandatory fields
		try
		{
			if(accountCode==null || accountCode.replaceAll("\\s","").length()==0)
			{
				throw new CustomFault("Mandatory parameter Customer AccountCode is not received !!");
			}
			
			if(accountName==null || accountName.replaceAll("\\s","").length()==0)
			{
				throw new CustomFault("Mandatory parameter Customer AccountName is not received !!");
			}
			
			if( parentAccountCode==null || parentAccountCode.replaceAll("\\s","").length()==0)
			{
				throw new CustomFault("Mandatory parameter ParentAccountCode(Dealer Account Code) is not received !!");
			}
			
			//DF20141211 - Rajani Nagaraju - Removing contact number as mandatory for account creation. Even if automatic Org group creation fails, 
			//JCB Admin can create Org Group on LL where in account deatails will not be sent again from SAP
			/*if( contactNumber==null || contactNumber.replaceAll("\\s","").length()==0)
			{
				throw new CustomFault("Mandatory parameter contactNumber is not received !!");
			}*/
			
			//DF20140723 - Rajani Nagaraju - Introducing sleep time for random seconds(between 1 and 5) to ensure no duplicate Customer acc/ten will be created
			//(Especially if sale details for different machines for the same customer under same dealer is received in same file - and customer acc/ten does not exists,
			   //In that case, since each record will be processed as parallel thread from Q object there might be a possibility of duplicate acc/ten creation) 
			Random r = new Random();
			//int i1 = r.nextInt(max - min + 1) + min;
			int delayPeriod = r.nextInt(5 - 1 + 1) + 1;
			int waitTimeInMilliSec = delayPeriod*1000;
			iLogger.info("EA Processing: CustomerInfo: "+accountCode+" :"+messageId+"Sleep for "+delayPeriod+" sec");
    		Thread.currentThread().sleep(waitTimeInMilliSec);
			
			/*if(accountCode==null || accountName==null || parentAccountCode==null || contactNumber==null)
			{
				throw new CustomFault("Either accountCode OR accountName OR parentAccountCode OR contactNumber is not received !!");
			}*/
			
			TenancyBO tenancyBoObj = new TenancyBO();
			//DF20140325 - Rajani Nagaraju - Get the Corresponding LL Account Code from ECC/CRM Dealer Code from Mapping table
			String llAccountCode = tenancyBoObj.getLLAccountCode(parentAccountCode);
			
			if(llAccountCode==null)
			{
				throw new CustomFault("Data not found in Mapping table for the Dealer AccountCode:"+accountCode);
			}
			else
			{
				parentAccountCode = llAccountCode;
			}

			/*String chkAccountCode = accountCode.replaceAll("\\s","") ;
			String chkAccountName = accountName.replaceAll("\\s","") ;
			String chkParentAccountCode = parentAccountCode.replaceAll("\\s","") ;

			if((!(chkAccountCode.length()>0))|| (!(chkAccountName.length()>0)) || (!(chkParentAccountCode.length()>0)) )
			{
				status = "FAILURE";
				return status;
			}*/

			//------------------- get the required parameters from properties file

			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			ParnershipDealer= prop.getProperty("ParnershipDealer");
			PartnershipCustomer= prop.getProperty("PartnershipCustomer");
			//2014-06-18 : Removing # in the Emaild and Mobile Number - Deepthi
			if(email!=null && email.contains("#")){
				email = null;
			}
			if(contactNumber!=null && contactNumber.contains("#")){
				contactNumber=null;
			}
		
			status =tenancyBoObj.setAccountDetails_new(accountCode, accountName, addressLine1, addressLine2, city, zipCode, state, zone, 
					country, email, contactNumber, fax, parentAccountCode,ParnershipDealer, PartnershipCustomer,false,messageId);


		}
		catch(CustomFault e)
		{
			//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
			status = "FAILURE-"+e.getFaultInfo();
			bLogger.error("EA Processing: CustomerInfo: "+messageId+" : "+e.getFaultInfo());
		}
		
		catch(Exception e)
		{
			//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
			status = "FAILURE-"+e.getMessage();
			fLogger.fatal("EA Processing: CustomerInfo: "+messageId+ " Fatal Exception :"+e);
		}
			

		return status;
		
	}
	
	
	
	
	/** This method sets the Customer Account Details received from ERP system
	 * @param accountCode Customer Code
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
	 * @param parentAccountCode
	 * @return
	 */
	public String setCustomerAccountDetails (String accountCode, String accountName, String addressLine1, String addressLine2, String city,
			 String zipCode, String state, String zone, String country, String email,String contactNumber, String fax, String parentAccountCode, String messageId)
	{
		String status = "SUCCESS-Record Processed";
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
		String ParnershipDealer=null;
		String PartnershipCustomer = null;
	
	
		//Check for Mandatory fields
		try
		{
			if(accountCode==null || accountCode.replaceAll("\\s","").length()==0)
			{
				throw new CustomFault("Mandatory parameter Customer AccountCode is not received !!");
			}
			
			if(accountName==null || accountName.replaceAll("\\s","").length()==0)
			{
				throw new CustomFault("Mandatory parameter Customer AccountName is not received !!");
			}
			
			if( parentAccountCode==null || parentAccountCode.replaceAll("\\s","").length()==0)
			{
				throw new CustomFault("Mandatory parameter ParentAccountCode(Dealer Account Code) is not received !!");
			}
			
			//DF20141211 - Rajani Nagaraju - Removing contact number as mandatory for account creation. Even if automatic Org group creation fails, 
			//JCB Admin can create Org Group on LL where in account deatails will not be sent again from SAP
			/*if( contactNumber==null || contactNumber.replaceAll("\\s","").length()==0)
			{
				throw new CustomFault("Mandatory parameter contactNumber is not received !!");
			}*/
			
			//DF20140723 - Rajani Nagaraju - Introducing sleep time for random seconds(between 1 and 5) to ensure no duplicate Customer acc/ten will be created
			//(Especially if sale details for different machines for the same customer under same dealer is received in same file - and customer acc/ten does not exists,
			   //In that case, since each record will be processed as parallel thread from Q object there might be a possibility of duplicate acc/ten creation) 
			Random r = new Random();
			//int i1 = r.nextInt(max - min + 1) + min;
			int delayPeriod = r.nextInt(5 - 1 + 1) + 1;
			int waitTimeInMilliSec = delayPeriod*1000;
			iLogger.info("EA Processing: CustomerInfo: "+accountCode+" :"+messageId+"Sleep for "+delayPeriod+" sec");
    		Thread.currentThread().sleep(waitTimeInMilliSec);
			
			/*if(accountCode==null || accountName==null || parentAccountCode==null || contactNumber==null)
			{
				throw new CustomFault("Either accountCode OR accountName OR parentAccountCode OR contactNumber is not received !!");
			}*/
			
			TenancyBO tenancyBoObj = new TenancyBO();
			//DF20140325 - Rajani Nagaraju - Get the Corresponding LL Account Code from ECC/CRM Dealer Code from Mapping table
			String llAccountCode = tenancyBoObj.getLLAccountCode(parentAccountCode);
			
			iLogger.info("AssetGateOutLog: "+accountCode+": llAccountCode for the dealer:"+llAccountCode); 	
			
			if(llAccountCode==null)
			{
				throw new CustomFault("Data not found in Mapping table for the Dealer AccountCode:"+accountCode);
			}
			else
			{
				parentAccountCode = llAccountCode;
			}

			/*String chkAccountCode = accountCode.replaceAll("\\s","") ;
			String chkAccountName = accountName.replaceAll("\\s","") ;
			String chkParentAccountCode = parentAccountCode.replaceAll("\\s","") ;

			if((!(chkAccountCode.length()>0))|| (!(chkAccountName.length()>0)) || (!(chkParentAccountCode.length()>0)) )
			{
				status = "FAILURE";
				return status;
			}*/

			//------------------- get the required parameters from properties file

			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			ParnershipDealer= prop.getProperty("ParnershipDealer");
			PartnershipCustomer= prop.getProperty("PartnershipCustomer");
			//2014-06-18 : Removing # in the Emaild and Mobile Number - Deepthi
			if(email!=null && email.contains("#")){
				email = null;
			}
			if(contactNumber!=null && contactNumber.contains("#")){
				contactNumber=null;
			}
			
			
			iLogger.info("AssetGateOutLog: "+accountCode+": before calling setAccountDetails_new"); 	
			
			
			status =tenancyBoObj.setAccountDetails_new(accountCode, accountName, addressLine1, addressLine2, city, zipCode, state, zone, 
					country, email, contactNumber, fax, parentAccountCode,ParnershipDealer, PartnershipCustomer,false,messageId);

			iLogger.info("AssetGateOutLog: "+accountCode+": After calling setAccountDetails_new:"+status); 	
		}
		catch(CustomFault e)
		{
			//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
			status = "FAILURE-"+e.getFaultInfo();
			bLogger.error("EA Processing: CustomerInfo: "+messageId+" : "+e.getFaultInfo());
		}
		
		catch(Exception e)
		{
			//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
			status = "FAILURE-"+e.getMessage();
			fLogger.fatal("EA Processing: CustomerInfo: "+messageId+ " Fatal Exception :"+e);
		}
			

		return status;
		
	}
	
}
