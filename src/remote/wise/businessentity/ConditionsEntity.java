package remote.wise.businessentity;

public class ConditionsEntity {
	
	private int ConditionId;
	private MonitoringParameters ParameterId;
	private String ConditionOperator;
	private int ConditionValue;
	
	
	public String getConditionOperator() {
		return ConditionOperator;
	}
	public void setConditionOperator(String conditionOperator) {
		ConditionOperator = conditionOperator;
	}
	public int getConditionValue() {
		return ConditionValue;
	}
	public void setConditionValue(int conditionValue) {
		ConditionValue = conditionValue;
	}
	public int getConditionId() {
		return ConditionId;
	}
	public void setConditionId(int conditionId) {
		ConditionId = conditionId;
	}
	public MonitoringParameters getParameterId() {
		return ParameterId;
	}
	public void setParameterId(MonitoringParameters parameterId) {
		ParameterId = parameterId;
	}
	

}
