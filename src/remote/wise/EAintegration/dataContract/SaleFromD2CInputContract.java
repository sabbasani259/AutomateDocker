package remote.wise.EAintegration.dataContract;

import java.io.Serializable;

/**
 * @author Rajani Nagaraju
 *
 */
public class SaleFromD2CInputContract implements Serializable
{
	private String sellerCode;
	private String buyerCode;
	private String dealerCode;
	private String serialNumber;
	private String transferDate;
	private String messageId;
	private String fileRef;
	private String process;
	private String reprocessJobCode;
	
	/**
	 * @return the sellerCode
	 */
	public String getSellerCode() {
		return sellerCode;
	}
	/**
	 * @param sellerCode the sellerCode to set
	 */
	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}
	/**
	 * @return the buyerCode
	 */
	public String getBuyerCode() {
		return buyerCode;
	}
	/**
	 * @param buyerCode the buyerCode to set
	 */
	public void setBuyerCode(String buyerCode) {
		this.buyerCode = buyerCode;
	}
	/**
	 * @return the dealerCode
	 */
	public String getDealerCode() {
		return dealerCode;
	}
	/**
	 * @param dealerCode the dealerCode to set
	 */
	public void setDealerCode(String dealerCode) {
		this.dealerCode = dealerCode;
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
	 * @return the transferDate
	 */
	public String getTransferDate() {
		return transferDate;
	}
	/**
	 * @param transferDate the transferDate to set
	 */
	public void setTransferDate(String transferDate) {
		this.transferDate = transferDate;
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
	 * @return the fileRef
	 */
	public String getFileRef() {
		return fileRef;
	}
	/**
	 * @param fileRef the fileRef to set
	 */
	public void setFileRef(String fileRef) {
		this.fileRef = fileRef;
	}
	/**
	 * @return the process
	 */
	public String getProcess() {
		return process;
	}
	/**
	 * @param process the process to set
	 */
	public void setProcess(String process) {
		this.process = process;
	}
	/**
	 * @return the reprocessJobCode
	 */
	public String getReprocessJobCode() {
		return reprocessJobCode;
	}
	/**
	 * @param reprocessJobCode the reprocessJobCode to set
	 */
	public void setReprocessJobCode(String reprocessJobCode) {
		this.reprocessJobCode = reprocessJobCode;
	}
	
	
}
