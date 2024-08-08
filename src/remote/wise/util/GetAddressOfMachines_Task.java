package remote.wise.util;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.net.ssl.SSLHandshakeException;

import remote.wise.service.implementation.CommunicationReportEnhancedServiceImpl;

//This task helps in processing data got from AMS table or fetching data from googleAPI
public class GetAddressOfMachines_Task implements Callable<Map<String,String>>{
	
	private Map<String,String> vinLatLongMap;
	
	public GetAddressOfMachines_Task(Map<String,String> vinLatLongMap){
		this.vinLatLongMap=vinLatLongMap;
	}

	@Override
	public Map<String, String> call() throws SSLHandshakeException,Exception {
		Map<String,String> addressMap = CommunicationReportEnhancedServiceImpl.getAddressOfMachines(vinLatLongMap);
		return addressMap;
	}

	

}

