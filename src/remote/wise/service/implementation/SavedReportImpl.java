package remote.wise.service.implementation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.SavedReportReqContract;
import remote.wise.service.datacontract.SavedReportRespContract;

public class SavedReportImpl {
	private int reportId;
	private String reportName;
	private String reportDescription;
	private String contactId;
    HashMap<String,String> filterNameFieldValue=new HashMap<String,String>();
    
    
    
	public int getReportId() {
		return reportId;
	}



	public void setReportId(int reportId) {
		this.reportId = reportId;
	}



	public String getReportName() {
		return reportName;
	}



	public void setReportName(String reportName) {
		this.reportName = reportName;
	}



	public String getReportDescription() {
		return reportDescription;
	}



	public void setReportDescription(String reportDescription) {
		this.reportDescription = reportDescription;
	}



	public String getContactId() {
		return contactId;
	}



	public void setContactId(String contactId) {
		this.contactId = contactId;
	}



	public HashMap<String, String> getFilterNameFieldValue() {
		return filterNameFieldValue;
	}



	public void setFilterNameFieldValue(HashMap<String, String> filterNameFieldValue) {
		this.filterNameFieldValue = filterNameFieldValue;
	}



	public List<SavedReportRespContract> getSavedReportService(SavedReportReqContract reqObj)throws CustomFault
	{
		List<SavedReportRespContract> listResponseObj=new LinkedList<SavedReportRespContract>();
		SavedReportRespContract responseObj=null;
		ReportDetailsBO ObjBo=new ReportDetailsBO();
		List<SavedReportImpl> savedReportImpl=ObjBo.getSavedReportService(reqObj.getReportId(),reqObj.getContactId());
		for(int i=0;i<savedReportImpl.size();i++)
		{
			responseObj=new SavedReportRespContract();
			responseObj.setReportId(savedReportImpl.get(i).getReportId());
			responseObj.setReportName(savedReportImpl.get(i).getReportName());
			responseObj.setReportDescription(savedReportImpl.get(i).getReportDescription());
			responseObj.setContactId(savedReportImpl.get(i).getContactId());
			responseObj.setFilterNameFieldValue(savedReportImpl.get(i).getFilterNameFieldValue());
			listResponseObj.add(responseObj);
		}
	return listResponseObj;
	}
	
	
	
	public String setSavedReportService(SavedReportRespContract resp)throws CustomFault
	{
		ReportDetailsBO ObjBo=new ReportDetailsBO();
		String response=ObjBo.setSavedReportService(resp.getContactId(),resp.getReportId(),resp.getReportName(),resp.getFilterNameFieldValue());
		return response;
	}
}
