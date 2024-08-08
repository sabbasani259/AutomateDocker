package remote.wise.service.datacontract;



public class UserAuthenticationReqContract 
{
	String login_id;
	String password;
	//TESTING//HttpServletRequest request;
	
	public String getLogin_id() {
		return login_id;
	}
	public void setLogin_id(String login_id) {
		this.login_id = login_id;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	//TESTING//
	/*public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}*/
	
	
}
