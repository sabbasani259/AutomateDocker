package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONObject;

import remote.wise.EAintegration.handler.ErrorMessageHandler;
import remote.wise.businessentity.FileWiseTracking;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AssetSaleFromD2CImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;

import com.wipro.mda.AssetOwnershipDetails;

//TaskID 1481 - JCB4908 - Rajani Nagaraju - contact creation failure - New Implementation
@Path("/AssetSaleFromD2CRestService")
public class AssetSaleFromD2CRestService 
{
	@GET
	@Path("assetSaleFromDealerToCust")
	@Produces("text/plain")
	public String assetSaleFromDealerToCust( @QueryParam("sellerCode") String sellerCode, @QueryParam("buyerCode") String buyerCode,
	@QueryParam("dealerCode") String dealerCode , @QueryParam("serialNumber") String serialNumber , 
	@QueryParam("transferDate") String transferDate , @QueryParam("messageId") String messageId, @QueryParam("fileRef") String fileRef,
	@QueryParam("process") String process, @QueryParam("reprocessJobCode") String reprocessJobCode) throws CustomFault 
	{
		String response = "SUCCESS-Record Processed";
		
		AssetSaleFromD2CImpl assetSaleImplObj = new AssetSaleFromD2CImpl();

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		iLogger.info("EA Processing: AssetSaleFromD2CRestService: " +": ---- Webservice Input ------");
		iLogger.info("EA Processing: AssetSaleFromD2CRestService:messageId: " + messageId
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


		if(serialNumber !=null && !serialNumber.equalsIgnoreCase("null"))
		{
			if(serialNumber.split("\\|").length > 2){
				String[] vinNum = serialNumber.split("\\|");
				serialNumber = vinNum[0];
				loginId = vinNum[1];
				csrfToken =  vinNum[2];

				if(csrfToken!=null){
					isValidCSRF=util.validateANTICSRFTOKEN(loginId,csrfToken);
				}
				iLogger.info("AssetSaleFromD2CRestService :: assetSaleFromDealerToCust ::   csrftoken isValidCSRF :: "+isValidCSRF);
				if(!isValidCSRF)
				{
					iLogger.info("AssetSaleFromD2CRestService :: assetSaleFromDealerToCust ::  Invalid request.");
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
		iLogger.info("EA Processing: AssetSaleFromD2CRestService: " + messageId
				+ ": Webservice Execution Time in ms:" + (endTime - startTime));

		iLogger.info("EA Processing: AssetSaleFromD2CRestService: " + messageId
				+ ": ----- Webservice Output-----");
		iLogger.info("EA Processing: AssetSaleFromD2CRestService: " + messageId + ": Status:"
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
				iLogger.info("AssetSaleFromD2CRestService: Status on Updating data into interface log details table :"
						+ uStatus);
			}

		} else {
			// DF20180108: @SU334449 - Invoking MOOL DA Layer from WISE post
			// successful persistence of machine ownership details in AOS table
			//new AssetOwnershipDetails().setAssetOwnershipDetails(serialNumber);//20220921.o.Additional log added
			new AssetOwnershipDetails().setAssetOwnershipDetails(serialNumber, "D2CSale");//20220921.n.Additional log added

			// DF20180207 :@Maniratnam ## Interfaces backTracking--updating the
			// interface log details table
			String uStatus = CommonUtil.updateInterfaceLogDetails(fileRef, "sucessCount", 1);
			iLogger.info("AssetSaleFromD2CRestService: Status on Updating data into interface log details table :"+ uStatus);
			if (messageId!=null && messageId.split("\\|").length > 1) {
				uStatus = CommonUtil.updateInterfaceLogDetails(fileRef,"failureCount", -1);
				iLogger.info("AssetSaleFromD2CRestService: Status on decrementing the failure count data into interface log details table :"
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
				iLogger.info("AssetSaleFromD2CRestService:Updated the file wise tracking table");
			}
			else{
				trackingtable=new FileWiseTracking();
				trackingtable.setSerialNumber(serialNumber);
				String jsondata= new JSONObject(data).toString();
				trackingtable.setSaleD2C(jsondata);
				session.save(trackingtable);
				iLogger.info("AssetSaleFromD2CRestService:Inserted into the file wise tracking table");

			}

		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("AssetSaleFromD2CRestService::Exception occured in inserting/updating the data into File wise tracking table"+e.getMessage());
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
