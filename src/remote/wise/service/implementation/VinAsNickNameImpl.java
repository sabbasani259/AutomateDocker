package remote.wise.service.implementation;

import remote.wise.dao.VinAsNickNameDAO;

public class VinAsNickNameImpl {

	public String setNicknameForVin(String vin, String nickName) {
		VinAsNickNameDAO daoObj = new VinAsNickNameDAO();
		String response = null;
		boolean isVinFound=daoObj.searchVin(vin);
		//boolean isNickNamePresent =true;
		
		
		if(isVinFound){
			//isNickNamePresent= daoObj.checkIfNickNameIsPresentInSystem(nickName);
		//	if(!isNickNamePresent){
				response= daoObj.updateNicknameForVin(vin,nickName);
			//}else
				//response="Ooops !! "+nickName+" is already in the system !! Please try with other names";
		}
		else
			response="Ooops Machine:"+vin+" not available in the system !! Please try with valid machine";
		
		return response;
	}
}
