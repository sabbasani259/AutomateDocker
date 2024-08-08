//CR334 : 20220830 : Dhiraj K : Billability Module Integration changes
//ME100005235 : 20230306 : Dhiraj K : WS to send the renewal date to MoolDA 
package remote.wise.service.webservice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONObject;

import com.wipro.mda.AssetProfileDetails;
import com.wipro.mda.AssetSubscriptionDetails;

import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.businessentity.FileWiseTracking;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.InstallationDateDetailsImpl;
import remote.wise.util.BillingSubscriptionHistory;
import remote.wise.util.CommonUtil;
//import remote.wise.util.WiseLogger;
import remote.wise.util.HibernateUtil;

/** Webservice method to set the Asset Installation details at Customer End
 * @author Smitha
 * Modified by Rajani Nagaraju
 */

@WebService(name = "InstallationDateDetailsService")
public class InstallationDateDetailsService 
{
	/** Web method to set Service Schedules Details for a Machine
	 * @param serialNumber - VIN Number
	 * @param installationDate - Date of Installation of a machine at Customer end
	 * @param dealerCode - Dealer of the Machine
	 * @param customerCode - Customer who owns the given machine
	 * @param messageId
	 * @param fileRef
	 * @param process
	 * @param reprocessJobCode
	 * @return
	 * @throws CustomFault 
	 */
	@WebMethod(operationName = "setAssetServiceSchedule", action = "setAssetServiceSchedule")
	public String setAssetServiceSchedule(@WebParam(name="serialNumber" ) String serialNumber, 
			@WebParam(name="installationDate" ) String installationDate,
			@WebParam(name="dealerCode" ) String dealerCode,
			@WebParam(name="customerCode" ) String customerCode,
			@WebParam(name="messageId" ) String messageId,
			@WebParam(name="fileRef" ) String fileRef,
			@WebParam(name="process" ) String process,
			@WebParam(name="reprocessJobCode" ) String reprocessJobCode) throws CustomFault
	{

		String response = "SUCCESS-Record Processed";

		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//WiseLogger infoLogger = WiseLogger.getLogger("InstallationDateDetailsService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger"); 
		iLogger.info("EA Processing: AssetInstallation: "+messageId+": ---- Webservice Input ------");
		iLogger.info("EA Processing: AssetInstallation: "+messageId+": Serial Number:"+serialNumber+",  "+"Installation Date:"+installationDate+",   " +
				"DealerCode: "+dealerCode+",  "+"Customer Code: "+customerCode);


		long startTime = System.currentTimeMillis();
		
		// DF20181016 Avinash Xavier A : CSRF Token Validation ---Start---.

		CommonUtil util = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;

		if (serialNumber != null && !serialNumber.equalsIgnoreCase("null")) {
			if (serialNumber.split("\\|").length > 2) {
				String[] vinNum = serialNumber.split("\\|");
				serialNumber = vinNum[0];
				loginId = vinNum[1];
				csrfToken = vinNum[2];
				if (csrfToken != null) {
					isValidCSRF = util.validateANTICSRFTOKEN(loginId, csrfToken);
				}
				iLogger.info("AssetInstallation ::   csrftoken isValidCSRF :: "
						+ isValidCSRF);
				if (!isValidCSRF) {
					iLogger.info("AssetInstallation ::  Invalid request.");
					throw new CustomFault("Invalid request.");
				} else {

					util.deleteANTICSRFTOKENS(loginId, csrfToken, "one");
				}
			}
		}

		// DF20181016 Avinash Xavier A : CSRF Token Validation ---End---.
		
		//DF20181008 - XSS validation for Security Fixes.
		String isValidinput=null;

		isValidinput = util.inputFieldValidation(installationDate);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(dealerCode);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(customerCode);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(serialNumber);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}

		iLogger.info("AssetInstallationLog: "+serialNumber+":Be1fore calling InstallationDateDetailsImpl setAssetserviceSchedule");

		response = new InstallationDateDetailsImpl().setAssetserviceSchedule(serialNumber,installationDate,dealerCode,customerCode,messageId, false);

		iLogger.info("AssetInstallationLog: "+serialNumber+":After calling InstallationDateDetailsImpl setAssetserviceSchedule:"+response);

		long endTime = System.currentTimeMillis();
		iLogger.info("serviceName:InstallationDateDetailsService~executionTime:"+(endTime-startTime)+"~"+loginId+"~"+response);

		iLogger.info("EA Processing: AssetInstallation: "+messageId+": ----- Webservice Output-----");
		iLogger.info("EA Processing: AssetInstallation: "+messageId+": Status:"+response);

		//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
		String faultCause =null;
		if(response.split("-").length>1)
		{
			faultCause=response.split("-")[1];
			response = response.split("-")[0].trim();

		}

		//If Failure in insertion, put the message to fault_details table
		if(response.equalsIgnoreCase("FAILURE"))
		{
			if(serialNumber==null)
				serialNumber="";
			if(installationDate==null)
				installationDate="";
			if(dealerCode==null)
				dealerCode="";
			if(customerCode==null)
				customerCode="";

			String messageString = serialNumber+"|"+installationDate+"|"+dealerCode+"|"+customerCode;
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			//errorHandler.handleErrorMessages(messageId, messageString, fileRef, process, reprocessJobCode,faultCause);
			// DF20180207 :@Maniratnam ## Interfaces backTracking--inserting
			// into fault details with new parameters and updating failure count
			// for interface log details table
			errorHandler.handleErrorMessages_new(messageId, messageString, fileRef, process, reprocessJobCode,faultCause,"0003","Service Layer");
			if(messageId!=null && messageId.split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount",1);
				iLogger.info("AssetInstallation: Status on Updating data into interface log details table :"+uStatus);
			}
		}else{
			//CR334.sn
			//Invoke Billability module service to update billing.billing_subsHistory Table
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String installDate = LocalDate.parse(installationDate).format(dtf) + " 00:00:00";
			String billingHistoryStatus = new BillingSubscriptionHistory().updateSubsHistory(serialNumber,installDate, installDate);
			iLogger.info("Billing history : installation date update status : " + billingHistoryStatus);
			//CR334.en
			//DF20180108: @SU334449 - Invoking MOOL DA Layer from WISE post successful persistence of machine profile details in MySQL database
			new AssetProfileDetails().setAssetProfileInstallationDateDetails(serialNumber, installationDate);
			new AssetSubscriptionDetails().setAssetSubscriptionDetails(serialNumber);//ME100005235.n
			//DF20180207 :@Maniratnam ## Interfaces backTracking--updating the interface log details table
			String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "sucessCount",1);
			iLogger.info("AssetInstallation: Status on Updating data into interface log details table :"+uStatus);
			if(messageId!=null && messageId.split("\\|").length>1){
				uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", -1);
				iLogger.info("AssetInstallation: Status on decrementing the failure count data into interface log details table :"+uStatus);

			}
			//DF20180207:KO369761 - deleting message from fault details table if it is there.
			ErrorMessageHandler messageHandlerObj = new ErrorMessageHandler();
			messageHandlerObj.deleteErrorMessage(messageId);
		}
		
		//DF20180207 :@Maniratnam ## Interfaces backTracking--to insert into the filewisetracking table
		 Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			FileWiseTracking trackingtable=null;
			HashMap<String, String> data=new HashMap<String,String>();
			data.put(fileRef,response);

			try{
				serialNumber=serialNumber.replaceFirst("^0+(?!$)", "");
				if(serialNumber.length()>7)
					serialNumber = serialNumber.substring(serialNumber.length()-7);

				Query query=session
						.createQuery("from FileWiseTracking where serialNumber ='"+serialNumber +"'");
				Iterator itr = query.list().iterator();
				if(itr.hasNext()) {
					trackingtable = (FileWiseTracking) itr.next();
				}

				if(trackingtable != null) {
					String jsondata= new JSONObject(data).toString();
					trackingtable.setInstallation(jsondata);
					session.update(trackingtable);
					iLogger.info("Asset Installation:Updated the file wise tracking table");
				}
			else{
				trackingtable=new FileWiseTracking();
				trackingtable.setSerialNumber(serialNumber);
				String jsondata= new JSONObject(data).toString();
				trackingtable.setInstallation(jsondata);
				session.save(trackingtable);
				iLogger.info("Asset Installation:Inserted into the file wise tracking table");

			}

		   }catch(Exception e)
		   {
			   e.printStackTrace();
			   fLogger.fatal("Asset Installation:Exception occured in inserting/updating the data into File wise tracking table"+e.getMessage());
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
		
		return response;
	}
}
