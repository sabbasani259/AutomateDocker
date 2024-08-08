package remote.wise.service.datacontract;

import remote.wise.businessentity.ProductEntity;

public class AssetServiceScheduleReqContract {
	
	private String serialNumber;
	private ProductEntity productId;
	
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public ProductEntity getProductId() {
		return productId;
	}

	public void setProductId(ProductEntity productId) {
		this.productId = productId;
	}

}
