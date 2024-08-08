package remote.wise.businessentity;
//DF20180207 @Maniratnam :: Adding new table Interface Error codes as part of Interfaces Backtracking
public class InterfaceErrorCodes extends BaseBusinessEntity{
	private String errorcode;
	private String errorMessage;
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getErrorcode() {
		return errorcode;
	}
	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}
	
}
