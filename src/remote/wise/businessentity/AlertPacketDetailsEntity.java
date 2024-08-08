package remote.wise.businessentity;

import java.io.Serializable;

public class AlertPacketDetailsEntity implements Serializable
{
	private int packetDetailsId;
	private String serialNumber;
	private String transactionTime;
	private String createdTimestamp;
	private byte[] parserObject;
	private String otherInfo;
	
	/**
	 * @return the packetDetailsId
	 */
	public int getPacketDetailsId() {
		return packetDetailsId;
	}
	/**
	 * @param packetDetailsId the packetDetailsId to set
	 */
	public void setPacketDetailsId(int packetDetailsId) {
		this.packetDetailsId = packetDetailsId;
	}
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	/**
	 * @return the transactionTime
	 */
	public String getTransactionTime() {
		return transactionTime;
	}
	/**
	 * @param transactionTime the transactionTime to set
	 */
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}
	/**
	 * @return the createdTimestamp
	 */
	public String getCreatedTimestamp() {
		return createdTimestamp;
	}
	/**
	 * @param createdTimestamp the createdTimestamp to set
	 */
	public void setCreatedTimestamp(String createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	/**
	 * @return the parserObject
	 */
	public byte[] getParserObject() {
		return parserObject;
	}
	/**
	 * @param parserObject the parserObject to set
	 */
	public void setParserObject(byte[] parserObject) {
		this.parserObject = parserObject;
	}
	/**
	 * @return the otherInfo
	 */
	public String getOtherInfo() {
		return otherInfo;
	}
	/**
	 * @param otherInfo the otherInfo to set
	 */
	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}
	
	
	
}
