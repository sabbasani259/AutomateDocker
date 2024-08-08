package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.service.datacontract.ReportMasterListReqContract;
import remote.wise.service.datacontract.ReportMasterListRespContract;

public class ReportMasterListImpl {
	private int reportId;
	private String reportName;
	private String reportDescription;
	
	
	
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



	public List<ReportMasterListRespContract> getReportMasterListService(ReportMasterListReqContract regObj)
	{
		ReportMasterListRespContract Resp=null;
		List<ReportMasterListRespContract> listResp=new LinkedList<ReportMasterListRespContract>();
		List<ReportMasterListImpl> listImpl=new LinkedList<ReportMasterListImpl>();
		
		ReportDetailsBO ObjBo=new ReportDetailsBO();
		List<ReportMasterListImpl> implObj=ObjBo.getReportMasterListService(regObj.getLoginId());
		for(int i=0;i<implObj.size();i++)
		{
			Resp=new ReportMasterListRespContract();
			Resp.setReportId(implObj.get(i).getReportId());
			Resp.setReportName(implObj.get(i).getReportName());
			Resp.setReportDescription(implObj.get(i).getReportDescription());
			listResp.add(Resp);
		}
			
			
		return listResp;
	}

}
