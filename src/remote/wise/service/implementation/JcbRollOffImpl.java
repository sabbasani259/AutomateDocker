/*
 * CR395 : 20230425 : Dhiraj K :Rolloff date change
 */
package remote.wise.service.implementation;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
//import remote.wise.util.WiseLogger;

/** Implementation Class to set the VIN details received during JCB rollOff
 * @author Rajani Nagaraju
 *
 */

public class JcbRollOffImpl 
{

	//public static WiseLogger businessError = WiseLogger.getLogger("JcbRollOffImpl:","businessError");
	
	/** This method sets the VIN details
	 * @param serialNumber VIN as String input
	 * @param nickName Engine Number as String input
	 * @param chasisNumber ChasisNumber as String input
	 * @param messageId Uniquely Identifies the record
	 * @return Returns the status string
	 */
	public String vinMachineNameMapping(String serialNumber, String nickName, String chasisNumber, String make, String builtDate, String machineNumber,
										//String messageId)//CR395.o
										String messageId, String rollOffDate,String machineCategory)//CR395.n
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		String status = "FAILURE - Reason Couldnot be determined";
		AssetDetailsBO assetDetailsBo = new AssetDetailsBO();
		if(serialNumber==null || serialNumber.replaceAll("\\s","").length()==0)
		{
			//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
			status = "FAILURE-Mandatory Parameter Serial Number is NULL";
			bLogger.error("EA Processing: AssetRolloffFromJCB: "+messageId+" : Mandatory Parameter Serial Number is NULL ");
			return status;
		}
		//CR395.sn
		if(builtDate==null || builtDate.replaceAll("\\s","").length()==0)
		{
			status = "FAILURE-Mandatory Parameter Built/Rolloff Date is NULL";
			bLogger.error("EA Processing: AssetRolloffFromJCB: "+messageId+" : Mandatory Parameter Built/Rolloff Date is NULL ");
			return status;
		}//CR395.en
		//DF20141209 - Rajani Nagaraju - Removing Engine Number as a mandatory parameter
		/*if(nickName == null || nickName.replaceAll("\\s","").length()==0 )
		{
			//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
			status = "FAILURE-Mandatory Parameter Nick Name(Engine Number) is NULL";
			businessError.error("EA Processing: AssetRolloffFromJCB: "+messageId+" : Mandatory Parameter Nick Name(Engine Number) is NULL ");
			return status;
		}*/
		
		if( machineNumber ==null || machineNumber.replaceAll("\\s","").length()==0)
		{
			//DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing 
			status = "FAILURE-Mandatory Parameter Machine Number is NULL";
			bLogger.error("EA Processing: AssetRolloffFromJCB: "+messageId+" : Mandatory Parameter Machine Number is NULL ");
			return status;
		}
		
		
		//Df20140411 - Rajani Nagaraju - To trim any spaces in serialnumber i/p
		serialNumber = serialNumber.replaceAll("\\s","") ;
		
		//DF20140715 - Rajani Nagaraju - Removing Extra spaces
		//DF20141209 - Rajani Nagaraju - Removing Engine Number as a mandatory parameter
		if(nickName!=null)
		{
			nickName = nickName.replaceAll("\\s","") ;
			if(nickName.length()==0)
				nickName=null;
		}
		
		if(chasisNumber!=null)
			chasisNumber=chasisNumber.replaceAll("\\s","") ;
		if(make!=null)
			make=make.replaceAll("\\s","") ;
		machineNumber=machineNumber.replaceAll("\\s","") ;
		
		//2014-06-18 : Machine Number check to extract the last 7 digits : Deepthi 
		/*if(machineNumber.trim().length() >7){
			machineNumber = machineNumber.substring(machineNumber.length()-7 , machineNumber.length());
		}*/
		
		//20140715 - Rajani Nagaraju - Removing preceeding zeros from Machine Number
		machineNumber=machineNumber.replaceFirst("^0+(?!$)", "");
		
		//2014-06-18 : Machine Number check to extract the last 7 digits : Deepthi 
		//status = assetDetailsBo.setVinMachineNameAssociation(serialNumber,nickName, chasisNumber,make,builtDate,machineNumber,messageId);//CR395.o
		status = assetDetailsBo.setVinMachineNameAssociation(serialNumber,nickName, chasisNumber,make,builtDate,machineNumber,messageId, rollOffDate,machineCategory);//CR395.n
		
		//DF20150311 - Rajani Nagaraju - Updating AssetOwnerSnapshot for a VIN on real time
		new CurrentAssetOwnerDetailsImpl().setVinOwnerDetails(serialNumber);
				
		return status;
	}
}
