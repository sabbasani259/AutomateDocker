package remote.wise.businessentity;

import java.util.HashSet;
import java.util.Set;

public class RoleEntity extends BaseBusinessEntity 
{
	private int role_id;
	private String role_name;
	private Set<ContactEntity> contact_list;
	private ClientEntity client_id;
	
	public RoleEntity(){}

	public RoleEntity(int role_id)
	{
		super.key = new Integer(role_id);
		RoleEntity r= (RoleEntity)read(this);
		
		setRole_id(r.role_id);
		setRole_name(r.role_name);
		setClient_id(r.client_id);
	}
	
	public RoleEntity(int role_id, String role_name,RoleEntity parent_role,ClientEntity client_id ){
		setRole_id(role_id);
		setRole_name(role_name);
		setClient_id(client_id);
			}
	
	public ClientEntity getClient_id() {
		return client_id;
	}


	public void setClient_id(ClientEntity client_id) {
		this.client_id = client_id;
	}


	public int getRole_id() {
		return role_id;
	}
	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}


	public String getRole_name() {
		return role_name;
	}
	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	
	
	public Set<ContactEntity> getContact_list() {
		return contact_list;
	}
	public void setContact_list(Set<ContactEntity> contact_list) {
		this.contact_list = contact_list;
	}

	

	public void addContact(ContactEntity newContact)
	{
		if(newContact==null)
			throw new IllegalArgumentException("Trying to add NULL conatct to role");
		
		if(newContact.getRole() != null)
			newContact.getRole().getContact_list().remove(newContact);
		
		newContact.setRole(this);
		contact_list.add(newContact);
	}
	
}

