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

import remote.wise.service.datacontract.ApprovedFeedBackReqContract;
import remote.wise.service.datacontract.TestimonialFeedBackReqContract;
import remote.wise.service.datacontract.TestimonialFeedBackRespContract;

import remote.wise.service.implementation.TestimonialFeedBackImpl;
import remote.wise.util.CommonUtil;

@WebService(name = "TestimonialFeedBackService")
public class TestimonialFeedBackService {

	@WebMethod(operationName = "setFeedBackForm" , action = "setFeedBackForm")
	public String setFeedBackForm(@WebParam(name = "reqObj")TestimonialFeedBackReqContract reqObj) throws CustomFault
	{
		String response = "Failure";
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("Customer/Dealer Id:"+reqObj.getContact_ID()+"image:  "+reqObj.getImage()+"FeedBack:"+reqObj.getFeedBack()+"Rating:"+reqObj.getRating());
		
		// DF20181015 Avinash Xavier : CSRF Token Validation ---Start---.
		CommonUtil util = new CommonUtil();
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;

		if (reqObj.getEdited_By() != null) {
			if (reqObj.getEdited_By().split("\\|").length > 1) {
				csrfToken = reqObj.getEdited_By().split("\\|")[1];
				loginId = reqObj.getEdited_By().split("\\|")[0];
				
				String UserID=new CommonUtil().getUserId(loginId);
				reqObj.setEdited_By(UserID);
			}
			
			if (csrfToken != null) {
				isValidCSRF = util.validateANTICSRFTOKEN(loginId, csrfToken);
			}
			iLogger.info("TestimonialFeedBackService :: setFeedBackForm ::   csrftoken isValidCSRF :: "
					+ isValidCSRF);
			if (!isValidCSRF) {
				iLogger.info("TestimonialFeedBackService :: setFeedBackForm ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			}
			else
			{
				util.deleteANTICSRFTOKENS(loginId,csrfToken,"one");
			}
		}else{
			//DF20170919 @Roopa getting decoded UserId
			String UserID=new CommonUtil().getUserId(reqObj.getContact_ID());
			reqObj.setContact_ID(UserID);
			iLogger.info("Decoded userId::"+reqObj.getContact_ID());
		}

		// DF20181015 Avinash Xavier CsrfToken Validation ---End----.
		
		//DF20180713: KO369761 - Security Check added for input text fields.
		String isValidinput=null;
		isValidinput = util.responseValidation(reqObj.getFeedBack());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(reqObj.getApproved_By());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(reqObj.getApproved_Date());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(reqObj.getCategory_Type());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(reqObj.getCurrent_Date());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(reqObj.getEdited_By());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(reqObj.getOrganisation_Name());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(String.valueOf(reqObj.getFeedback_ID()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(String.valueOf(reqObj.getRating()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(String.valueOf(reqObj.isApproved()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		response = new TestimonialFeedBackImpl().setFeedBackForm(reqObj);
		
		iLogger.info("----- Webservice Output-----");
		iLogger.info("response:"+response);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("Webservice Execution Time in ms:"+(endTime-startTime));
		return response;
	}
	@WebMethod(operationName = "setFeedBackApproval" , action = "setFeedBackApproval")
	public String setFeedBackApproval(@WebParam(name="reqObj")TestimonialFeedBackReqContract reqObj) throws CustomFault
	{
		String response = "Failure";
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		iLogger.info("FeedBack Id:"+reqObj.getFeedback_ID()+"Approved:  "+reqObj.isApproved()+"Approved By:"+reqObj.getApproved_By()+"Approved Date:"+reqObj.getApproved_Date());
		
		// DF20181015 Avinash Xavier : CSRF Token Validation ---Start---.
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;
		CommonUtil util = new CommonUtil();

		if (reqObj.getApproved_By() != null) {
			if (reqObj.getApproved_By().split("\\|").length > 1) {
				csrfToken = reqObj.getApproved_By().split("\\|")[1];
				loginId = reqObj.getApproved_By().split("\\|")[0];
				reqObj.setApproved_By(reqObj.getApproved_By().split("\\|")[0]);
			}
		}

		if (csrfToken != null) {
			isValidCSRF = util.validateANTICSRFTOKEN(loginId, csrfToken);
		}
		iLogger.info("TestimonialFeedBackService :: setFeedBackApproval ::   csrftoken isValidCSRF :: "
				+ isValidCSRF);
		if (!isValidCSRF) {
			iLogger.info("TestimonialFeedBackService :: setFeedBackApproval ::  Invalid request.");
			throw new CustomFault("Invalid request.");
		}
		else
		{
			util.deleteANTICSRFTOKENS(loginId,csrfToken,"one");
		}

		// DF20181015 Avinash Xavier CsrfToken Validation ---End----.
		
		//DF20180713: KO369761 - Security Check added for input text fields.
		String isValidinput=null;
		isValidinput = util.responseValidation(reqObj.getFeedBack());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		//DF20190430 I Anudeep removing validations for login id as it is encrypted
//		isValidinput = util.responseValidation(reqObj.getApproved_By());
//		if(!isValidinput.equals("SUCCESS")){
//			throw new CustomFault(isValidinput);
//		}
		isValidinput = util.responseValidation(reqObj.getApproved_Date());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(reqObj.getCategory_Type());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(reqObj.getCurrent_Date());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(reqObj.getEdited_By());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(reqObj.getOrganisation_Name());
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(String.valueOf(reqObj.getFeedback_ID()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(String.valueOf(reqObj.getRating()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		isValidinput = util.responseValidation(String.valueOf(reqObj.isApproved()));
		if(!isValidinput.equals("SUCCESS")){
			throw new CustomFault(isValidinput);
		}
		
		//DF20170919 @Roopa getting decoded UserId
		String UserID=new CommonUtil().getUserId(reqObj.getContact_ID());
		reqObj.setContact_ID(UserID);
		iLogger.info("Decoded userId::"+reqObj.getContact_ID());
		
		//DF20190430 Null check for login ID and decrypting it and storing it in approved by
		if(!(loginId==null || loginId.trim()==null || loginId.replaceAll("\\s","").length()==0)){
			reqObj.setApproved_By(util.getUserId(loginId));
		}
				
		response = new TestimonialFeedBackImpl().setFeedBackApproval(reqObj);
		
		iLogger.info("----- Webservice Output-----");
		iLogger.info("response:"+response);
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:TestimonialFeedBackService~executionTime:"+(endTime-startTime)+"~"+loginId+"~"+response);
		return response;
	}
	@WebMethod(operationName="getApprovedFeedBacks" , action = "getApprovedFeedBacks")
	public List<TestimonialFeedBackRespContract> getApprovedFeedBacks(@WebParam(name = "reqObj")ApprovedFeedBackReqContract reqObj) throws CustomFault{
		List<TestimonialFeedBackRespContract> resObj = null;
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		if(reqObj!=null)
		iLogger.info("Dealer/Customer ID:"+reqObj.getContact_Id());
		
		//DF20181008 - XSS validation for Security Fixes.
		CommonUtil util = new CommonUtil();
		String isValidinput=null;
		
		// DF20181015 Avinash Xavier : CSRF Token Validation ---Start---.
		String csrfToken = null;
		boolean isValidCSRF = false;
		String loginId = null;

		if (reqObj.getContact_Id() != null) {
			if (reqObj.getContact_Id().split("\\|").length > 1) {
				csrfToken = reqObj.getContact_Id().split("\\|")[2];
				loginId = reqObj.getContact_Id().split("\\|")[1];
				reqObj.setContact_Id(reqObj.getContact_Id().split("\\|")[0]);
				
				if (csrfToken != null) {
					isValidCSRF = util.validateANTICSRFTOKEN(loginId, csrfToken);
					iLogger.info("TestimonialFeedBackService :: getApprovedFeedBacks ::   csrftoken isValidCSRF :: "
							+ isValidCSRF);
					if (!isValidCSRF) {
						iLogger.info("TestimonialFeedBackService :: getApprovedFeedBacks ::  Invalid request.");
						throw new CustomFault("Invalid request.");
					}
				}
				
				if(reqObj.getContact_Id().equalsIgnoreCase("NA")){
					//DF20170919 @Roopa getting decoded UserId
					String UserID=new CommonUtil().getUserId(loginId);
					reqObj.setContact_Id(UserID);
					iLogger.info("Decoded userId::"+reqObj.getContact_Id());
				}
			}
		}

		// DF20181015 Avinash Xavier CsrfToken Validation ---End----.
		 
		resObj = new TestimonialFeedBackImpl().getApprovedFeedBacks(reqObj);
		
		iLogger.info("----- Webservice Output-----");
		if(resObj.size()>0)
		{
			for(int i=0;i<resObj.size();i++){
				iLogger.info("created Date:"+resObj.get(i).getCurrent_Date()+"FeedBack:"+resObj.get(i).getFeedBack()+"Customer ID:"+resObj.get(i).getContact_ID()+"Role:"+resObj.get(i).getRole());
				
				//DF20181008-KO369761-XSS validation of output response contract
				isValidinput = util.responseValidation(resObj.get(i).getApproved_By());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.responseValidation(resObj.get(i).getApproved_Date());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.responseValidation(resObj.get(i).getCategory_Type());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.responseValidation(resObj.get(i).getContact_ID());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.responseValidation(resObj.get(i).getCurrent_Date());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.responseValidation(resObj.get(i).getEdited_By());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.responseValidation(resObj.get(i).getFeedBack());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.responseValidation(resObj.get(i).getOrganisation_Name());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.responseValidation(resObj.get(i).getRole());
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.responseValidation(String.valueOf(resObj.get(i).getCount()));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.responseValidation(String.valueOf(resObj.get(i).getFeedback_ID()));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.responseValidation(String.valueOf(resObj.get(i).getRating()));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				isValidinput = util.responseValidation(String.valueOf(resObj.get(i).isApproved()));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
			}
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:TestimonialFeedBackService~executionTime:"+(endTime-startTime)+"~"+loginId+"~");
		return resObj;
	}
}
