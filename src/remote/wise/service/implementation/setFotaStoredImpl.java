package remote.wise.service.implementation;

import java.util.List;
////import org.apache.log4j.Logger;

import remote.wise.businessobject.EventDetailsBO;
import remote.wise.businessobject.FotaStoredBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.FotaStoredProcRespContract;
//import remote.wise.util.WiseLogger;

public class setFotaStoredImpl {
	//public static WiseLogger businessError = WiseLogger.getLogger("setFotaStoredImpl:","businessError");
	public String setFotaStored() {
		// TODO Auto-generated method stub
		//Logger businessError = Logger.getLogger("businessErrorLogger");
			
			FotaStoredBO fotastoreDetails = new FotaStoredBO();
			String status = fotastoreDetails.setFotaStoredProc();	
	
	return status;
}
}
