package remote.wise.businessentity;

import java.util.Set;

public class IndustryEntity extends BaseBusinessEntity 
{
	private int industry_id;
	private String industry_name;
	private Set<ClientEntity> client_list;
	
	public IndustryEntity(){}
	
	public IndustryEntity(int industryId)
	{
		super.key= new Integer(industryId);
		IndustryEntity i = (IndustryEntity) read (this);
		
		if(i!=null)
		{
			setIndustry_id(i.getIndustry_id());
			setIndustry_name(i.getIndustry_name());
		}
	}
	
	
	public int getIndustry_id() {
		return industry_id;
	}
	public void setIndustry_id(int industry_id) {
		this.industry_id = industry_id;
	}
	

	public Set<ClientEntity> getClient_list() {
		return client_list;
	}
	public void setClient_list(Set<ClientEntity> client_list) {
		this.client_list = client_list;
	}
	
	public String getIndustry_name() {
		return industry_name;
	}
	public void setIndustry_name(String industry_name) {
		this.industry_name = industry_name;
	}
	
	public void addClient(ClientEntity newClient)
	{
		if(newClient==null)
			throw new IllegalArgumentException("Trying to add NULL client to Industry");
		
		if(newClient.getIndustry_id() != null)
			newClient.getIndustry_id().getClient_list().remove(newClient);
		
		newClient.setIndustry_id(this);
		client_list.add(newClient);
	}
	
	
}
