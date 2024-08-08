package remote.wise.businessentity;

import java.sql.Timestamp;

public class RejectedPacketDetailsEntity extends BaseBusinessEntity
{
	int rejectionId;
	String serialNumber;
	Timestamp transactionTime;
	Timestamp createdTime;
	String rejectionCause;
	//DF20141215 - Rajani Nagaraju - To store the txn against which the packet was rejected
	int txnForRejection;
	
	public int getRejectionId() {
		return rejectionId;
	}
	public void setRejectionId(int rejectionId) {
		this.rejectionId = rejectionId;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public Timestamp getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(Timestamp transactionTime) {
		this.transactionTime = transactionTime;
	}
	public Timestamp getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}
	public String getRejectionCause() {
		return rejectionCause;
	}
	public void setRejectionCause(String rejectionCause) {
		this.rejectionCause = rejectionCause;
	}
	public int getTxnForRejection() {
		return txnForRejection;
	}
	public void setTxnForRejection(int txnForRejection) {
		this.txnForRejection = txnForRejection;
	}
	
	
}
