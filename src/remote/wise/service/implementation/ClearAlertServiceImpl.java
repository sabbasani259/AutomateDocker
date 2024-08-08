package remote.wise.service.implementation;

import remote.wise.businessobject.AssetDetailsBO;

public class ClearAlertServiceImpl {
	
	/**
	 * method to clear alerts active status to 0 after 24 hours
	 * @return String 
	 */
	public String clearAlertAfter24Hrs(){
		AssetDetailsBO bo = new AssetDetailsBO();
		String message= bo.clearAlertAfter24Hrs();
		return message;
	}

}
