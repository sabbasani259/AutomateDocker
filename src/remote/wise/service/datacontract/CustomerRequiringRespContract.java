package remote.wise.service.datacontract;

import java.util.List;

public class CustomerRequiringRespContract {
private String CustomerName;
private String DealerName;
private String CustomerContactNumber;
private List<String> MachineServiceDetails;
private int NumberOfMachineDueForService;

public int getNumberOfMachineDueForService() {
	return NumberOfMachineDueForService;
}
public void setNumberOfMachineDueForService(int numberOfMachineDueForService) {
	NumberOfMachineDueForService = numberOfMachineDueForService;
}
public List<String> getMachineServiceDetails() {
	return MachineServiceDetails;
}
public void setMachineServiceDetails(List<String> machineServiceDetails) {
	MachineServiceDetails = machineServiceDetails;
}
public String getCustomerName() {
	return CustomerName;
}
public void setCustomerName(String customerName) {
	CustomerName = customerName;
}
public String getDealerName() {
	return DealerName;
}
public void setDealerName(String dealerName) {
	DealerName = dealerName;
}
public String getCustomerContactNumber() {
	return CustomerContactNumber;
}
public void setCustomerContactNumber(String customerContactNumber) {
	CustomerContactNumber = customerContactNumber;
}
}
