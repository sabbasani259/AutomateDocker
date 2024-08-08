package remote.wise.businessentity;

public class EventActionEntity extends BaseBusinessEntity
{
	private int actionTypeId;
	private String actionTypeName;
	
	public EventActionEntity() {}
	
	public EventActionEntity(int actionTypeId )
	{
		super.key= new Integer(actionTypeId);
		EventActionEntity e = (EventActionEntity)read(this);
		if(e!=null)
		{
			 setActionTypeId(e.getActionTypeId());
			 setActionTypeName(e.getActionTypeName());
		}
	}
	public int getActionTypeId() {
		return actionTypeId;
	}
	public void setActionTypeId(int actionTypeId) {
		this.actionTypeId = actionTypeId;
	}
	
	
	public String getActionTypeName() {
		return actionTypeName;
	}
	public void setActionTypeName(String actionTypeName) {
		this.actionTypeName = actionTypeName;
	}
	
	
}
