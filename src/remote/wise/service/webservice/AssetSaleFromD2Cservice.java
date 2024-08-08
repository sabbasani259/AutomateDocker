package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.Iterator;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONObject;

import com.wipro.mda.AssetOwnershipDetails;

import remote.wise.EAintegration.Qhandler.InterfaceDataProducer;
import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.businessentity.FileWiseTracking;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AssetSaleFromD2CImpl;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;
import remote.wise.util.InterfaceProducerCaller;

/**
 * WebService to handle Asset sale from Dealer to Customer
 * 
 * @author Rajani Nagaraju
 * 
 * ME100030804 : Dhiraj Kumar : 20220826 : Incorrect SAP data (Roll off, Personality, Gate out Sale and D2C sale) 
 * 		   		 being sent to Mobile App
 */
@WebService(name = "AssetSaleFromD2Cservice")
public class AssetSaleFromD2Cservice {

	/**
	 * DefectId:839 - Rajani Nagaraju - 20131114 - To enable Machine Movement
	 * between tenancies WebMethod that sets the asset sale from dealer to
	 * customer
	 * 
	 * @param dealerCode
	 *            dealerCode as String input
	 * @param customerCode
	 *            customerCode as String input
	 * @param serialNumber
	 *            VIN
	 * @param transferDate
	 *            Date of transfer of Asset
	 * @return Returns status String
	 * @throws CustomFault
	 */
	@WebMethod(operationName = "assetSaleFromDealerToCust", action = "assetSaleFromDealerToCust")
	public String assetSaleFromDealerToCust(
			@WebParam(name = "sellerCode") String sellerCode,
			@WebParam(name = "buyerCode") String buyerCode,
			@WebParam(name = "dealerCode") String dealerCode,
			@WebParam(name = "serialNumber") String serialNumber,
			@WebParam(name = "transferDate") String transferDate,
			@WebParam(name = "messageId") String messageId,
			@WebParam(name = "fileRef") String fileRef,
			@WebParam(name = "process") String process,
			@WebParam(name = "reprocessJobCode") String reprocessJobCode
			
			/*@WebParam(name="rollOffDate" ) String rollOffDate,
			@WebParam(name="profile" ) String profile,
			@WebParam(name="model" ) String model,
			@WebParam(name="llPlusFlag" ) String llPlusFlag,			
			@WebParam(name="customerCode" ) String customerCode,
			@WebParam(name="customerName" ) String customerName,
			@WebParam(name="zonalCode" ) String zonalCode*/) throws CustomFault {
		String response = "SUCCESS-Record Processed";
		AssetSaleFromD2CImpl assetSaleImplObj = new AssetSaleFromD2CImpl();

		// DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using
		// static logger object all throught the application
		// WiseLogger infoLogger =
		// WiseLogger.getLogger("AssetSaleFromD2Cservice:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		// Logger infoLogger = Logger.getLogger("infoLogger");
		iLogger.info("EA Processing: SaleFromD2C: " + messageId
				+ ": ---- Webservice Input ------");
		iLogger.info("EA Processing: SaleFromD2C: " + messageId
				+ ": sellerCode:" + sellerCode + ",  " + "buyerCode:"
				+ buyerCode + ",  " + "dealerCode:" + dealerCode + ",  "
				+ "serialNumber:" + serialNumber + ",  " + "transferDate:"
				+ transferDate + ",  messageId:" + messageId);

		long startTime = System.currentTimeMillis();
		
		//DF20181016 Avinash Xavier A : CSRF Token Validation ---Start---.

		CommonUtil util = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;
		
		if(serialNumber !=null && !serialNumber.equalsIgnoreCase("null")){
			if(serialNumber.split("\\|").length > 2){
				String[] vinNum = serialNumber.split("\\|");
				serialNumber = vinNum[0];
				loginId = vinNum[1];
				csrfToken =  vinNum[2];

				if(csrfToken!=null){
					isValidCSRF=util.validateANTICSRFTOKEN(loginId,csrfToken);
				}
				iLogger.info("AssetSaleFromD2Cservice :: assetSaleFromDealerToCust ::   csrftoken isValidCSRF :: "+isValidCSRF);
				if(!isValidCSRF)
				{
					iLogger.info("AssetSaleFromD2Cservice :: assetSaleFromDealerToCust ::  Invalid request.");
					throw new CustomFault("Invalid request.");
				}else{
					util.deleteANTICSRFTOKENS(loginId, csrfToken, "one");
				}
			}

		}

		//DF20181016 Avinash Xavier A : CSRF Token Validation ---End---.
		
		//DF20181008 - XSS validation for Security Fixes.
		String isValidinput=null;

		isValidinput = util.inputFieldValidation(sellerCode);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(buyerCode);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(dealerCode);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(serialNumber);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.inputFieldValidation(transferDate);
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		try {
			response = assetSaleImplObj.assetSaleFromDealerToCust(sellerCode,
					buyerCode, dealerCode, serialNumber, transferDate,
					messageId,transferDate);
		} catch (CustomFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		iLogger.info("EA Processing: SaleFromD2C: " + messageId
				+ ": Webservice Execution Time in ms:" + (endTime - startTime));

		iLogger.info("EA Processing: SaleFromD2C: " + messageId
				+ ": ----- Webservice Output-----");
		iLogger.info("EA Processing: SaleFromD2C: " + messageId + ": Status:"
				+ response);

		// DF20140715 - Rajani Nagaraju - Robust Logging for EA File Processing
		String faultCause = null;
		if (response.split("-").length > 1) {
			faultCause = response.split("-")[1];
			response = response.split("-")[0].trim();

		}

		// If Failure in insertion, put the message to fault_details table
		if (response.equalsIgnoreCase("FAILURE")) {
			if (sellerCode == null)
				sellerCode = "";
			if (buyerCode == null)
				buyerCode = "";
			if (dealerCode == null)
				dealerCode = "";
			if (serialNumber == null)
				serialNumber = "";
			if (transferDate == null)
				transferDate = "";

			String messageString = sellerCode + "|" + buyerCode + "|"
					+ dealerCode + "|" + serialNumber + "|" + transferDate;
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			/*errorHandler.handleErrorMessages(messageId, messageString, fileRef,
					process, reprocessJobCode, faultCause);*/
			// DF20180214 :@Maniratnam ## Interfaces backTracking--inserting
			// into fault details with new parameters and updating failure count
			// for interface log details table
			errorHandler.handleErrorMessages_new(messageId, messageString,
					fileRef, process, reprocessJobCode, faultCause, "0003",
					"Service Layer");
			if (messageId!=null && messageId.split("\\|").length < 2) {
				String uStatus = CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", 1);
				iLogger.info("SaleFromD2C: Status on Updating data into interface log details table :"
						+ uStatus);
			}

		} else {
			/*HashMap<String,String> InterfacePoducerDataPayloadMap = new HashMap<String,String>();
			InterfacePoducerDataPayloadMap.put("TXN_KEY","InterfaceProducerData"+"_"+serialNumber);
			InterfacePoducerDataPayloadMap.put("rollOffDate",rollOffDate);
			InterfacePoducerDataPayloadMap.put("profile",profile);
			InterfacePoducerDataPayloadMap.put("model",model);
			InterfacePoducerDataPayloadMap.put("llPlusFlag",llPlusFlag);
			InterfacePoducerDataPayloadMap.put("dealerCode",dealerCode);
			InterfacePoducerDataPayloadMap.put("customerCode",customerCode);
			InterfacePoducerDataPayloadMap.put("customerName",customerName);
			InterfacePoducerDataPayloadMap.put("zonalCode",zonalCode);	*/
			//ME100030804.so
			//SH20011298-20211216 : CR264-20211118-LLPlusAPI-MachinesAPI_Changes_v00_00
			/*HashMap<String,String> InterfacePoducerDataPayloadMap = new HashMap<String,String>();
			InterfacePoducerDataPayloadMap.put("sellerCode",sellerCode);
			InterfacePoducerDataPayloadMap.put("buyerCode",buyerCode);
			InterfacePoducerDataPayloadMap.put("dealerCode",dealerCode);
			InterfacePoducerDataPayloadMap.put("serialNumber",serialNumber);
			InterfacePoducerDataPayloadMap.put("transferDate",transferDate);
			
			InterfacePoducerDataPayloadMap.put("rollOffDate","NA");
			InterfacePoducerDataPayloadMap.put("profile","NA");
			InterfacePoducerDataPayloadMap.put("model","NA");
			InterfacePoducerDataPayloadMap.put("llPlusFlag","NA");			
			InterfacePoducerDataPayloadMap.put("dealerName","NA");
			InterfacePoducerDataPayloadMap.put("customerCode","NA");
			InterfacePoducerDataPayloadMap.put("customerName","NA");
			InterfacePoducerDataPayloadMap.put("zonalCode","NA");*/
			//ME100030804.eo
			
			// DF20180108: @SU334449 - Invoking MOOL DA Layer from WISE post
			// successful persistence of machine ownership details in AOS table
			//new AssetOwnershipDetails().setAssetOwnershipDetails(serialNumber);//20220921.o.Additional log added
			new AssetOwnershipDetails().setAssetOwnershipDetails(serialNumber, "D2CSale");//20220921.n.Additional log added
			
			//ME100030804.so
			//SH20011298-20211216 : CR264-20211118-LLPlusAPI-MachinesAPI_Changes_v00_00
			//new InterfaceDataProducer(InterfacePoducerDataPayloadMap.get("TXN_KEY"), InterfacePoducerDataPayloadMap);
			//new InterfaceDataProducer().publishMachineData(InterfacePoducerDataPayloadMap.get("TXN_KEY"), InterfacePoducerDataPayloadMap);
			//ME100030804.eo

			//ME100030804.sn
			// Invoke Interface Producer to publish data on kafka topic
			new InterfaceProducerCaller().invokeInterfaceProducer(serialNumber, "D2CSALE");
			//ME100030804.en
			
			// DF20180207 :@Maniratnam ## Interfaces backTracking--updating the
			// interface log details table
			String uStatus = CommonUtil.updateInterfaceLogDetails(fileRef, "sucessCount", 1);
			iLogger.info("AssetInstallation: Status on Updating data into interface log details table :"+ uStatus);
			if (messageId!=null && messageId.split("\\|").length > 1) {
				uStatus = CommonUtil.updateInterfaceLogDetails(fileRef,"failureCount", -1);
				iLogger.info("SaleFromD2C: Status on decrementing the failure count data into interface log details table :"
						+ uStatus);
			}
			// DF20180208:KO369761 - deleting message from fault details table if it is there.
			ErrorMessageHandler messageHandlerObj = new ErrorMessageHandler();
			messageHandlerObj.deleteErrorMessage(messageId);
		}

		//DF20180214 :@Maniratnam ## Interfaces backTracking--to insert into the filewisetracking table
		//Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Session session = null;
		
		FileWiseTracking trackingtable=null;
		HashMap<String, String> data=new HashMap<String,String>();
		data.put(fileRef,response);

		try{
			session=HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
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
				trackingtable.setSaleD2C(jsondata);
				session.update(trackingtable);
				iLogger.info("SaleFromD2C:Updated the file wise tracking table");
			}
			else{
				trackingtable=new FileWiseTracking();
				trackingtable.setSerialNumber(serialNumber);
				String jsondata= new JSONObject(data).toString();
				trackingtable.setSaleD2C(jsondata);
				session.save(trackingtable);
				iLogger.info("SaleFromD2C:Inserted into the file wise tracking table");

			}

		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("SaleFromD2C::Exception occured in inserting/updating the data into File wise tracking table"+e.getMessage());
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
