package remote.wise.service.implementation;

import remote.wise.dao.UnblockVINSDAO;

public class UnblockVinsImpl {
		public String unblockVin(String vin, String loginId) {
		String response="Invalid Vin, try again";
		UnblockVINSDAO daoObj= new UnblockVINSDAO();
		String responseForVinPresent="false";
		if(vin!=null)
		responseForVinPresent=daoObj.ifVinExistsInDB(vin);
		if(vin==null || responseForVinPresent.equalsIgnoreCase("FALSE")){
		}
		else{
			response=daoObj.updateVinToUnblockedState(vin,loginId);
		}
		return response;
	}

}
