/**
 * 
 */
package remote.wise.service.datacontract;

/**
 * @author sunayak
 *
 */
public class EAInterfaceDetailRespContract {

	String record;
	/**
	 * @return the record
	 */
	public String getRecord() {
		return record;
	}
	/**
	 * @param record the record to set
	 */
	public void setRecord(String record) {
		this.record = record;
	}
	/**
	 * @return the failureForRejection
	 */
	public String getFailureForRejection() {
		return failureForRejection;
	}
	/**
	 * @param failureForRejection the failureForRejection to set
	 */
	public void setFailureForRejection(String failureForRejection) {
		this.failureForRejection = failureForRejection;
	}
	/**
	 * @return the reProcessCount
	 */
	public int getReProcessCount() {
		return reProcessCount;
	}
	/**
	 * @param reProcessCount the reProcessCount to set
	 */
	public void setReProcessCount(int reProcessCount) {
		this.reProcessCount = reProcessCount;
	}
	String failureForRejection;
	int reProcessCount ;
}
