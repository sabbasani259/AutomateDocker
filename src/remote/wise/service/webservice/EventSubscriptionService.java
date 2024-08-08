package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;



import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.EventSubscriptionReqContract;
import remote.wise.service.datacontract.EventSubscriptionRespContract;
import remote.wise.service.implementation.EventSubscriptionImpl;
//import remote.wise.util.WiseLogger;

/** Webservice to handle SMS subscription users for a given serialNumbers
 * @author Rajani Nagaraju
 *
 */
@WebService(name = "EventSubscriptionService")
public class EventSubscriptionService 
{
	
	/** This method sets the SMS subscribers for a given serialNumber
	 * @param reqObj serialNumber and contactList for SMS subscription
	 * @return Returns the status String
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "SetEventSubscription", action = "SetEventSubscription")
	public String setEventSubscription(@WebParam(name="reqObj" ) EventSubscriptionReqContract reqObj) throws CustomFault
	{ 

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("EventSubscriptionService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Serial Number:"+reqObj.getSerialNumber()+",  "+"primaryContactList:"+reqObj.getPrimaryContactList());
		String response = new EventSubscriptionImpl().setEventSubscription(reqObj);
		iLogger.info("----- Webservice Output-----");
		iLogger.info("Status:"+response);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:EventSubscriptionService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;

	}


	/** This method returns the List of SMS subscription contacts with their details
	 * Defect Id: 902 - Rajani Nagaraju - 20130705 - DealerUser as first PrimarySMS contact.
	 * @param reqObj serialNumber for which SMS subscriber details to be returned
	 * @return Returns SMS subscriber details for a given serialNumber
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "GetEventSubscription", action = "GetEventSubscription")
	public List<EventSubscriptionRespContract> getEventSubscription(@WebParam(name="reqObj" ) EventSubscriptionReqContract reqObj) throws CustomFault
	{

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("EventSubscriptionService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Serial Number:"+reqObj.getSerialNumber());
		List<EventSubscriptionRespContract> response = new EventSubscriptionImpl().getEventSubscription(reqObj);
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++)
		{
			iLogger.info("Serial Number:"+response.get(i).getSerialNumber()+",  "+"contactId:"+response.get(i).getContactId()+",   " +
					"primaryMobileNumber:"+response.get(i).getPrimaryMobileNumber()+",  "+"primaryEmailId:"+response.get(i).getPrimaryEmailId()+",  " +
					"userName: "+response.get(i).getUserName() + 
					//Defect Id: 902 - Rajani Nagaraju - 20130705 - DealerUser as first PrimarySMS contact.
					",  "+"IsDealerUser: "+response.get(i).isDealerUser()+", Priority : "+response.get(i).getPriority());
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:EventSubscriptionService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;

	}

}
