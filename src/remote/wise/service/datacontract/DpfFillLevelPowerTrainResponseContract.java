/**
 * CR397 : 20230831 : Dhiraj Kumar : StageV VD development changes
 */
package remote.wise.service.datacontract;

public class DpfFillLevelPowerTrainResponseContract {

	protected String dpfFillLevel;
	protected String status;
	protected String message;
	protected String txnTimestampPT;

	public String getDpfFillLevel() {
		return dpfFillLevel;
	}

	public void setDpfFillLevel(String dpfFillLevel) {
		this.dpfFillLevel = dpfFillLevel;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTxnTimestampPT() {
		return txnTimestampPT;
	}

	public void setTxnTimestampPT(String txnTimestampPT) {
		this.txnTimestampPT = txnTimestampPT;
	}
}
