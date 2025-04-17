package remote.wise.service.datacontract;
//LL98: 04172025: Prapoorna: DefLevel value for Genset Machines

public class GensetDefFillLevelResponseContract {
	protected String def_Level;
    protected String status;
	protected String txnTimestampPT;
	public String getDef_Level() {
		return def_Level;
	}
	public void setDef_Level(String def_Level) {
		this.def_Level = def_Level;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTxnTimestampPT() {
		return txnTimestampPT;
	}
	public void setTxnTimestampPT(String txnTimestampPT) {
		this.txnTimestampPT = txnTimestampPT;
	}
}
