package remote.wise.service.implementation;

import remote.wise.businessobject.LandmarkFactDataBO;

/**
 * @author kprabhu5
 *
 */
public class LandmarkFactDataImpl {

	/**
	 * method to set landmark fact table 
	 * @return String
	 */
	public String setLandmarkFactData(){
		String message =null;
		LandmarkFactDataBO bo=new LandmarkFactDataBO();
		message = bo.setLandmarkFactData();
		return message;
	}
}
