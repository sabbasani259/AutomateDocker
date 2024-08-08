package remote.wise.businessentity;

public interface IBusinessEntity 
{
	//save method persists the data of the entity with which it is called
	public void save(IBusinessEntity entity);
	public IBusinessEntity read(IBusinessEntity entity);
}
