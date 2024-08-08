package remote.wise.service.implementation;

import remote.wise.dao.VinDisconnectedStatusDAO;


public class VinDisconnectedStatusImpl {

	public String updateDisconnectedVINSFromVF(String vin){
		VinDisconnectedStatusDAO daoObj = new VinDisconnectedStatusDAO();
		String response=daoObj.getDisconnectionStatusOfVin(vin);
		return response;
	}
	public String blockingMachineCommunication(String vin){
		VinDisconnectedStatusDAO daoObj = new VinDisconnectedStatusDAO();
		String response=daoObj.getCommunicationStatus(vin);
		return response;
	}

}
