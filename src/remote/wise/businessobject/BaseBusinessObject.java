package remote.wise.businessobject;

import remote.wise.businessentity.IBusinessEntity;

public class BaseBusinessObject implements IBusinessObject 
{
	IBusinessEntity entity;
	
	public BaseBusinessObject(IBusinessEntity Entity)
	{
		this.entity = Entity;
	}
	public BaseBusinessObject()
	{
		
	}
}
