package remote.wise.businessentity;

public class ServiceHistoryFilesEntity extends BaseBusinessEntity{

	private String fileName;
	private String serviceTktNumber;
	private String machineNumber;
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getServiceTktNumber() {
		return serviceTktNumber;
	}
	public void setServiceTktNumber(String serviceTktNumber) {
		this.serviceTktNumber = serviceTktNumber;
	}
	
	public String getMachineNumber() {
		return machineNumber;
	}
	
	public void setMachineNumber(String machineNumber) {
		this.machineNumber = machineNumber;
	}
}
