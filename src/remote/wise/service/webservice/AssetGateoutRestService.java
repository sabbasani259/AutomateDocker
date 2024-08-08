/*
 * JCB6341 : 20230306 : Dhiraj K : Sale date is not getting updated as part of Direct Gateout sale 
 */
package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.jws.WebMethod;
import javax.jws.WebParam;
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
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AssetGateoutIml;
import remote.wise.util.CommonUtil;
import remote.wise.util.HibernateUtil;

import com.wipro.mda.AssetOwnershipDetails;

//TaskID 1481 - JCB4908 - Rajani Nagaraju - contact creation failure - New Implementation
@Path("/AssetSaleFromD2CRestService")
public class AssetGateoutRestService 
{
	@GET
	@Path("setAssetGateoutService")
	@Produces("text/plain")
	public String setAssetGateoutService(@QueryParam("dealerCode") String dealerCode,
			@QueryParam("customerCode") String customerCode,
			@QueryParam("engineNumber") String engineNumber,
			@QueryParam("serialNumber") String serialNumber,
			@QueryParam("messageId") String messageId,
			@QueryParam("fileRef") String fileRef,
			@QueryParam("process") String process,
			@QueryParam("reprocessJobCode") String reprocessJobCode)
	{


		String response = "SUCCESS";

		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		//Logger infoLogger = Logger.getLogger("infoLogger");
		iLogger.info("EA Processing: AssetGateoutRestService: "+messageId+":---- Webservice Input ------");
		iLogger.info("EA Processing: AssetGateoutRestService: "+messageId+": dealerCode: "+dealerCode+",  "+"customerCode: "+customerCode+",   " +
				"engineNumber: "+engineNumber+",  "+"serialNumber: "+serialNumber+",  messageId:"+messageId);

		long startTime = System.currentTimeMillis();

		iLogger.info("AssetGateoutRestService: "+serialNumber+": Before calling IMPL");

		//JCB6341.sn
		//Current date will be gateout date(Gateout to Dealer) or sale date(Gateout to Customer)
		Date currentDate = new Date();
		String gateoutDateString = new SimpleDateFormat("yyyy-MM-dd").format(currentDate);
		//JCB6341.en
		//response = new AssetGateoutIml().setAssetGateoutDetails(dealerCode, customerCode, engineNumber, serialNumber, messageId);//JCB6341.o
		response = new AssetGateoutIml().setAssetGateoutDetails(dealerCode, customerCode, engineNumber, serialNumber, messageId, gateoutDateString);//JCB6341.n

		iLogger.info("AssetGateoutRestService: "+serialNumber+": After calling IMPL:"+response);


		long endTime=System.currentTimeMillis();
		iLogger.info("AssetGateoutRestService:executionTime:"+(endTime-startTime)+"~"+""+"~");
		iLogger.info("EA Processing: AssetGateoutRestService: "+messageId+": ----- Webservice Output-----");
		iLogger.info("EA Processing: AssetGateoutRestService: "+messageId+": Status:"+response);

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
			if(dealerCode==null)
				dealerCode="";
			if(customerCode==null)
				customerCode="";
			if(engineNumber==null)
				engineNumber="";
			if(serialNumber==null)
				serialNumber="";

			String messageString = dealerCode+"|"+customerCode+"|"+engineNumber+"|"+serialNumber;
			ErrorMessageHandler errorHandler = new ErrorMessageHandler();
			//errorHandler.handleErrorMessages(messageId, messageString, fileRef, process, reprocessJobCode, faultCause);
			errorHandler.handleErrorMessages_new(messageId, messageString, fileRef, process, reprocessJobCode, faultCause,"0003","Service Layer");

			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			if(messageId!=null && messageId.split("\\|").length<2){
				String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", 1);
				iLogger.info("Status on updating data into interface log details table :"+uStatus);
			}
		}else{
			//DF20180108: @SU334449 - Invoking MOOLDA Layer from WISE post successful persistence of machine ownership details in AOS table
			//new AssetOwnershipDetails().setAssetOwnershipDetails(serialNumber);//20220921.o.Additional log added
			new AssetOwnershipDetails().setAssetOwnershipDetails(serialNumber, "GateoutSale");//20220921.n.Additional log added

			//DF20180207:KO369761 - Inserting datacount to log details table for tracing.
			String uStatus=CommonUtil.updateInterfaceLogDetails(fileRef, "sucessCount", 1);
			iLogger.info("Status on updating data into interface log details table :"+uStatus);
			if(messageId!=null && messageId.split("\\|").length>1){
				CommonUtil.updateInterfaceLogDetails(fileRef, "failureCount", -1);
			}
			//DF20180207:KO369761 - deleting message from fault details table if it is there.
			ErrorMessageHandler messageHandlerObj = new ErrorMessageHandler();
			messageHandlerObj.deleteErrorMessage(messageId);
		}

		//DF20180208 - KO369761: Inserting file and response into interaface tracking table.
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		FileWiseTracking trackingtable=null;
		HashMap<String, String> data=new HashMap<String, String>();
		data.put(fileRef, response);

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
				trackingtable.setGateOut(jsondata);
				session.update(trackingtable);
				iLogger.info("AssetGateout:Updated the file wise tracking table");
			}
			else{
				trackingtable = new FileWiseTracking();
				trackingtable.setSerialNumber(serialNumber);
				String jsondata= new JSONObject(data).toString();
				trackingtable.setGateOut(jsondata);
				session.save(trackingtable);
				iLogger.info("AssetGateout:Inserted into the file wise tracking table");

			}

		}catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception occured in inserting/updating the data into File wise tracking table"+e.getMessage());

		}finally{
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
