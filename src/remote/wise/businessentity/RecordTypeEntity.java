package remote.wise.businessentity;

public class RecordTypeEntity extends BaseBusinessEntity
{
	int recordTypeId;
	String messageId;
	String recordTypeName;
	//DF20160324 - Rajani Nagaraju - Using PacketIdentifier instead of MessageID for the identification of PacketType - Log/Event
	String packetId;
		
	/**
	 * @return the recordTypeId
	 */
	public int getRecordTypeId() {
		return recordTypeId;
	}
	/**
	 * @param recordTypeId the recordTypeId to set
	 */
	public void setRecordTypeId(int recordTypeId) {
		this.recordTypeId = recordTypeId;
	}
	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}
	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	/**
	 * @return the recordTypeName
	 */
	public String getRecordTypeName() {
		return recordTypeName;
	}
	/**
	 * @param recordTypeName the recordTypeName to set
	 */
	public void setRecordTypeName(String recordTypeName) {
		this.recordTypeName = recordTypeName;
	}
	public String getPacketId() {
		return packetId;
	}
	public void setPacketId(String packetId) {
		this.packetId = packetId;
	}
	
	
}
