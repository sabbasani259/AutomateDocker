package remote.wise.businessentity;

import java.io.Serializable;

public class EventCondition extends BaseBusinessEntity implements Serializable {

	private EventEntity EventId;
	
	private ConditionsEntity ConditionId;
	private String BooleanOperator;
	private int Rank;
	

	public String getBooleanOperator() {
		return BooleanOperator;
	}

	public void setBooleanOperator(String booleanOperator) {
		BooleanOperator = booleanOperator;
	}

	public int getRank() {
		return Rank;
	}

	public void setRank(int rank) {
		Rank = rank;
	}

	public EventEntity getEventId() {
		return EventId;
	}

	public void setEventId(EventEntity eventId) {
		EventId = eventId;
	}

	public ConditionsEntity getConditionId() {
		return ConditionId;
	}

	public void setConditionId(ConditionsEntity conditionId) {
		ConditionId = conditionId;
	}
	
}
