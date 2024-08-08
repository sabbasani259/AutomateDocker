package remote.wise.businessentity;

import java.io.Serializable;

/**
 * @author Rajani Nagaraju
 *
 */
public class ProductProfileEntity extends BaseBusinessEntity implements Serializable
{
	ProductEntity productId;
	double fuelCapacityInLitres;
/*String ServiceScheduleName;*/
/*	
	public String getServiceScheduleName() {
	return ServiceScheduleName;
}
public void setServiceScheduleName(String serviceScheduleName) {
	ServiceScheduleName = serviceScheduleName;
}*/
	/**
	 * @return the productId
	 */
	public ProductEntity getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(ProductEntity productId) {
		this.productId = productId;
	}
	/**
	 * @return the Capacity of the fueltank in Litres
	 */
	public double getFuelCapacityInLitres() {
		return fuelCapacityInLitres;
	}
	/**
	 * @param fuelCapacityInLitres the Capacity of fuel tank to set
	 */
	public void setFuelCapacityInLitres(double fuelCapacityInLitres) {
		this.fuelCapacityInLitres = fuelCapacityInLitres;
	}
	

	
	
}
