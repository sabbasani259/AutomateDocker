package remote.wise.businessentity;

public class MonitoringParameters extends BaseBusinessEntity 
{
	private int parameterId;
	private ParameterTypeEntity parameterTypeId;
	private String parameterName;
	private String RecordType;
	
	//DF20151005 - Rajani Nagaraju - Adding the association of parameter Code (Required by MOOL for data processing)
	private String parameterCode;
	private String parameterKey;
	//DF20170811: SU334449 - Adding DTC_code column in the POJO
	private int dtcCode;
	//DF20170811: SU334449 - Adding Error_Code column in the POJO
	private String errorCode;
	
	public MonitoringParameters(){
	}
	
	public MonitoringParameters(int parameterId)
	{
		super.key = new Integer(parameterId);
		MonitoringParameters m=(MonitoringParameters) read(this);
		if(m!=null)
		{
			setParameterName(m.getParameterName());
			setParameterId(m.getParameterId());
			setParameterTypeId(m.getParameterTypeId());
			setRecordType(m.getRecordType());
		}
	}
	
	public String getParameterName() {
		return parameterName;
	}
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	
	
	public int getParameterId() {
		return parameterId;
	}
	public void setParameterId(int parameterId) {
		this.parameterId = parameterId;
	}
	
	
	public ParameterTypeEntity getParameterTypeId() {
		return parameterTypeId;
	}
	public void setParameterTypeId(ParameterTypeEntity parameterTypeId) {
		this.parameterTypeId = parameterTypeId;
	}

	public String getRecordType() {
		return RecordType;
	}

	public void setRecordType(String recordType) {
		RecordType = recordType;
	}

	public String getParameterCode() {
		return parameterCode;
	}

	public void setParameterCode(String parameterCode) {
		this.parameterCode = parameterCode;
	}

	public String getParameterKey() {
		return parameterKey;
	}

	public void setParameterKey(String parameterKey) {
		this.parameterKey = parameterKey;
	}
	
	public int getDtcCode() {
		return dtcCode;
	}

	public void setDtcCode(int dtcCode) {
		this.dtcCode = dtcCode;
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
}
