package remote.wise.businessentity;
//DF20180207 @Maniratnam :: Adding new table Interface Log details as part of Interfaces Backtracking
public class InterfaceLogDetails extends BaseBusinessEntity{
	private String filename;
	private int totalCount;
	private int successfullyputtoqueue;
	private int successfullServiceInvocation;
	private int sucessCount;
	private int failureCount;
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getSuccessfullyputtoqueue() {
		return successfullyputtoqueue;
	}
	public void setSuccessfullyputtoqueue(int successfullyputtoqueue) {
		this.successfullyputtoqueue = successfullyputtoqueue;
	}
	public int getSuccessfullServiceInvocation() {
		return successfullServiceInvocation;
	}
	public void setSuccessfullServiceInvocation(int successfullServiceInvocation) {
		this.successfullServiceInvocation = successfullServiceInvocation;
	}
	public int getSucessCount() {
		return sucessCount;
	}
	public void setSucessCount(int sucessCount) {
		this.sucessCount = sucessCount;
	}
	public int getFailureCount() {
		return failureCount;
	}
	public void setFailureCount(int failureCount) {
		this.failureCount = failureCount;
	}
	
	
}
