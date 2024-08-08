package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.RegConfirmationBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.RegConfirmationReqContract;
import remote.wise.service.datacontract.RegConfirmationRespContract;
//import remote.wise.util.WiseLogger;

public class RegConfirmationImpl {

	//public static WiseLogger businessError = WiseLogger.getLogger("RegConfirmationImpl:","businessError");
	String IMEI_number;
	String phone_number;
	/**
	 * @return the iMEI_number
	 */
	public String getIMEI_number() {
		return IMEI_number;
	}

	/**
	 * @param iMEI_number the iMEI_number to set
	 */
	public void setIMEI_number(String iMEI_number) {
		IMEI_number = iMEI_number;
	}

	/**
	 * @return the phone_number
	 */
	public String getPhone_number() {
		return phone_number;
	}

	/**
	 * @param phone_number the phone_number to set
	 */
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}


	public String setRegConfirmation(RegConfirmationRespContract reqObj) throws CustomFault{
		// TODO Auto-generated method stub
		Logger bLogger = BusinessErrorLoggerClass.logger;
		String status ="SUCCESS";
		try
		{
			if((reqObj.getIMEINumber()==null ) || (reqObj.getIMEINumber().isEmpty()))
			{
				throw new CustomFault("Provide the IMEI Number to delete");
			}

			if(reqObj.isFlag()==false)
			{
				throw new CustomFault("Insertion not possible when flag is false");
			}

			status=new RegConfirmationBO().setConfirmation(reqObj.getIMEINumber(),reqObj.getPhoneNumber(),reqObj.getUserID());
		}
		catch(CustomFault e)
		{
			status = "FAILURE";
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}

		return status;


	}

	public RegConfirmationRespContract getRegConfirmation(RegConfirmationReqContract reqObj) throws CustomFault{
		// TODO Auto-generated method stub
		Logger bLogger = BusinessErrorLoggerClass.logger;
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		RegConfirmationBO regConfirmBo = new RegConfirmationBO();

		//to get the RegistrationConfirmation details IMEINumber is a mandatory parameter.
		try
		{
			if(reqObj.getIMEINumber()==null)
			{
				throw new CustomFault("Please provide IMEINumber");
			}
		}
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}

		RegConfirmationBO regConfirmBoObj =regConfirmBo.getConfirmation(reqObj.getIMEINumber());

		RegConfirmationRespContract response = new RegConfirmationRespContract();
		response.setIMEINumber(regConfirmBoObj.getIMEI_number());
		response.setPhoneNumber(regConfirmBoObj.getPhone_number());		
		response.setUserID(regConfirmBoObj.getUser_ID());
		if(regConfirmBoObj.getIMEI_number()==null)
		{
			response.setFlag(false);
		}
		else
		{
			response.setFlag(true);
		}
		return response;
	}

	public String deleteRegConfirmation(RegConfirmationRespContract reqObj) throws CustomFault{

		// TODO Auto-generated method stub
		Logger bLogger = BusinessErrorLoggerClass.logger;
		String status ="SUCCESS";
		//Logger businessError = Logger.getLogger("businessErrorLogger");

		try
		{
			if((reqObj.getIMEINumber()==null ) || (reqObj.getIMEINumber().isEmpty()))
			{
				throw new CustomFault("Provide the IMEI Number to delete");
			}

			if(reqObj.isFlag()==true)
			{
				throw new CustomFault("Deletion not possible when flag is true");
			}

			status=new RegConfirmationBO().deleteConfirmation(reqObj.getIMEINumber(),reqObj.isFlag());
		}
		catch(CustomFault e)
		{
			status = "FAILURE";
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}

		return status;

	}

}
