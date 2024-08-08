package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import remote.wise.businessobject.RoleBO;
import remote.wise.service.datacontract.RoleRespContract;

public class RoleImpl {
	String roleName;
	int roleId;
	
	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the roleId
	 */
	public int getRoleId() {
		return roleId;
	}

	/**
	 * @param roleId the roleId to set
	 */
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	/**
	 * method to get the list of roles
	 * @return list of roles
	 */

	public List<RoleRespContract> getRoles(){
		RoleBO bo=new RoleBO();		
		List<RoleImpl> roleList=bo.getRoles();
		
		List<RoleRespContract> responseList =new ArrayList<RoleRespContract>();
		Iterator<RoleImpl> roleIter = roleList.iterator();
		RoleImpl impl=null;RoleRespContract response = null;
		while(roleIter.hasNext()){
			impl = roleIter.next();
			response = new RoleRespContract();
			response.setRoleId(impl.getRoleId());
			response.setRoleName(impl.getRoleName());
			responseList.add(response);
		}
		
		return responseList;
		
	}

}
