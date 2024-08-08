package com.wipro.MachineDown;

import java.sql.Timestamp;
import java.util.Date;

public class MachineDownEntity {
	
	private int PartitionKey;
	private String B1CallId;
	private String CreatedDate;
	private String CreatedTime;
	private String AssignmentDate;
	private String AssignmentTime;
	private String ServiceCall;
	private String Description;
	private String Status;
	private String CustomerName;
	private String MobileNumber;
	private String MachineNumber;
	private String Model;
	private String ServiceType;
	private String CallType;
	private String CallSubType;
	private String Dealer;
	private String AssignedEngName;
	private String MachineStatus;
	private String CallHMR;
	private String Remarks;
	private String LastUpdated;
	public int getPartitionKey() {
		return PartitionKey;
	}
	public void setPartitionKey(int partitionKey) {
		PartitionKey = partitionKey;
	}
	public String getB1CallId() {
		return B1CallId;
	}
	public void setB1CallId(String b1CallId) {
		B1CallId = b1CallId;
	}
	public String getCreatedDate() {
		return CreatedDate;
	}
	public void setCreatedDate(String createdDate) {
		CreatedDate = createdDate;
	}
	public String getCreatedTime() {
		return CreatedTime;
	}
	public void setCreatedTime(String createdTime) {
		CreatedTime = createdTime;
	}
	public String getAssignmentDate() {
		return AssignmentDate;
	}
	public void setAssignmentDate(String assignmentDate) {
		AssignmentDate = assignmentDate;
	}
	public String getAssignmentTime() {
		return AssignmentTime;
	}
	public void setAssignmentTime(String assignmentTime) {
		AssignmentTime = assignmentTime;
	}
	public String getServiceCall() {
		return ServiceCall;
	}
	public void setServiceCall(String serviceCall) {
		ServiceCall = serviceCall;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getCustomerName() {
		return CustomerName;
	}
	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}
	public String getMobileNumber() {
		return MobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		MobileNumber = mobileNumber;
	}
	public String getMachineNumber() {
		return MachineNumber;
	}
	public void setMachineNumber(String machineNumber) {
		MachineNumber = machineNumber;
	}
	public String getModel() {
		return Model;
	}
	public void setModel(String model) {
		Model = model;
	}
	public String getServiceType() {
		return ServiceType;
	}
	public void setServiceType(String serviceType) {
		ServiceType = serviceType;
	}
	public String getCallType() {
		return CallType;
	}
	public void setCallType(String callType) {
		CallType = callType;
	}
	public String getCallSubType() {
		return CallSubType;
	}
	public void setCallSubType(String callSubType) {
		CallSubType = callSubType;
	}
	public String getDealer() {
		return Dealer;
	}
	public void setDealer(String dealer) {
		Dealer = dealer;
	}
	public String getAssignedEngName() {
		return AssignedEngName;
	}
	public void setAssignedEngName(String assignedEngName) {
		AssignedEngName = assignedEngName;
	}
	public String getMachineStatus() {
		return MachineStatus;
	}
	public void setMachineStatus(String machineStatus) {
		MachineStatus = machineStatus;
	}
	public String getCallHMR() {
		return CallHMR;
	}
	public void setCallHMR(String callHMR) {
		CallHMR = callHMR;
	}
	public String getRemarks() {
		return Remarks;
	}
	public void setRemarks(String remarks) {
		Remarks = remarks;
	}
	public String getLastUpdated() {
		return LastUpdated;
	}
	public void setLastUpdated(String lastUpdated) {
		LastUpdated = lastUpdated;
	}
	@Override
	public String toString() {
		return "MachineDownEntity [PartitionKey=" + PartitionKey
				+ ", B1CallId=" + B1CallId + ", CreatedDate=" + CreatedDate
				+ ", CreatedTime=" + CreatedTime + ", AssignmentDate="
				+ AssignmentDate + ", AssignmentTime=" + AssignmentTime
				+ ", ServiceCall=" + ServiceCall + ", Description="
				+ Description + ", Status=" + Status + ", CustomerName="
				+ CustomerName + ", MobileNumber=" + MobileNumber
				+ ", MachineNumber=" + MachineNumber + ", Model=" + Model
				+ ", ServiceType=" + ServiceType + ", CallType=" + CallType
				+ ", CallSubType=" + CallSubType + ", Dealer=" + Dealer
				+ ", AssignedEngName=" + AssignedEngName + ", MachineStatus="
				+ MachineStatus + ", CallHMR=" + CallHMR + ", Remarks="
				+ Remarks + ", LastUpdated=" + LastUpdated + "]";
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
