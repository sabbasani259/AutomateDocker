package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.businessobject.UserDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.ReportMailSubscriptionListReqContract;
import remote.wise.service.datacontract.ReportSubscriptionReqContract;
import remote.wise.service.datacontract.ReportSubscriptionRespContract;
import remote.wise.service.datacontract.UserAlertPreferenceRespContract;
//import remote.wise.util.WiseLogger;
/**
 * 
 * @author tejgm
 * Implementation provides the subscription on the reports
 */
public class ReportSubscriptionImpl {
	//Defect Id 1337 - Logger changes
		//public static WiseLogger businessError = WiseLogger.getLogger("ReportSubscriptionImpl:","businessError");
/**
		 * @param businessError the businessError to set
		 */
		/*public static void setBusinessError(WiseLogger businessError) {
			ReportSubscriptionImpl.businessError = businessError;
		}*/

	//	static Logger businessError = Logger.getLogger("businessErrorLogger");
	private int reportId;
	private String reportName;
	private String contactId;	
	private boolean weeklyReportSubscription;
	private boolean monthlyReportSubscription;
	
    public boolean isWeeklyReportSubscription() {
		return weeklyReportSubscription;
	}
	public void setWeeklyReportSubscription(boolean weeklyReportSubscription) {
		this.weeklyReportSubscription = weeklyReportSubscription;
	}
	public boolean isMonthlyReportSubscription() {
		return monthlyReportSubscription;
	}
	public void setMonthlyReportSubscription(boolean monthlyReportSubscription) {
		this.monthlyReportSubscription = monthlyReportSubscription;
	}
	
	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public int getReportId() {
		return reportId;
	}
	public void setReportId(int reportId) {
		this.reportId = reportId;
	}
	/**
	 * 
	 * @param reqObj is reportId and ContactId
	 * @return listRespContract reurns the list of reportName and calalogName
	 * @throws CustomFault
	 */
	public List<ReportSubscriptionRespContract> getreportSubscriptionService(ReportSubscriptionReqContract reqObj)throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		ReportSubscriptionRespContract respContract=null;
		List<ReportSubscriptionRespContract> listRespContract =new LinkedList<ReportSubscriptionRespContract>();
		ReportDetailsBO ObjOfBo=new ReportDetailsBO();
		if(reqObj.getContactId()==null)
		{
		
			bLogger.error("Hello this is an Business Error message");
			throw new CustomFault("Please provide contactId,It should not be null");
		}
			
		List<ReportSubscriptionImpl> responseOfBo=ObjOfBo.getreportSubscriptionServiceFinal(reqObj.getContactId(),reqObj.getReportId());
		for(int i=0;i<responseOfBo.size();i++)
		{
			respContract=new ReportSubscriptionRespContract();
			respContract.setReportId(responseOfBo.get(i).getReportId());	
			respContract.setReportName(responseOfBo.get(i).getReportName());	
			respContract.setContactId(responseOfBo.get(i).getContactId());
			respContract.setWeeklyReportSubscription(responseOfBo.get(i).isWeeklyReportSubscription());	
			respContract.setMonthlyReportSubscription(responseOfBo.get(i).isMonthlyReportSubscription());
			listRespContract.add(respContract);		
	}
		return listRespContract;
	}
	
	public String setreportSubscriptionService(List<ReportSubscriptionRespContract> resp)
	{
		ReportDetailsBO ObjOfBo=new ReportDetailsBO();
		
			String response=ObjOfBo.setreportSubscriptionService(resp);
			
		return "success";
				
		//return "SUCCESS";
	}
	/*public String setreportSubscriptionService(ReportSubscriptionRespContract resp)
	{
		ReportDetailsBO ObjOfBo=new ReportDetailsBO();
		String response=ObjOfBo.setreportSubscriptionService2(resp.getReportId(),resp.getContactId(),resp.isMonthlyReportSubscription(),resp.isWeeklyReportSubscription());
		for(int i=0;i<resp.size();i++)
		{
			String response=ObjOfBo.setreportSubscriptionService2(resp.get(i).getReportId(),resp.get(i).getContactId(),resp.get(i).getReportName(),resp.get(i).isWeeklyReportSubscription(),resp.get(i).isMonthlyReportSubscription());

		}
		
		return "SUCCESS";
	}*/
	
	public String sendReportSubscriptionMails(ReportMailSubscriptionListReqContract reqObj) throws CustomFault{
		List<ReportSubscriptionRespContract> response = null;
		if(reqObj.getFrequencyType()==null && reqObj.getFrequencyType().equals(" ") ){
			throw new CustomFault("Provide Frequency type (Weekly/Monthly) !!" );
		}
		String message = null;
		message = new ReportDetailsBO().sendReportSubscriptionMails(reqObj.getFrequencyType());
		return message;
	}
	
	}
