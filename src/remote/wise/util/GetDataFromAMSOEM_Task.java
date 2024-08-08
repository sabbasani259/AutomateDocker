package remote.wise.util;

import java.util.Map;
import java.util.concurrent.Callable;

import remote.wise.service.implementation.CommunicationReportEnhancedServiceImpl;

//This task is used for fetching Data from AMS table on some condition
public class GetDataFromAMSOEM_Task implements Callable<Map<String, String>> {
	private int startLimit, endLimit;
	boolean forAll;
	String vins;

	public GetDataFromAMSOEM_Task(int startLimit, int endLimit, boolean forAll,
			String vins) {
		this.startLimit = startLimit;
		this.endLimit = endLimit;
		this.forAll = forAll;
		this.vins = vins;
	}

	@Override
	public Map<String, String> call() throws Exception {
		Map<String, String> vinLatLongMap = CommunicationReportEnhancedServiceImpl
				.getDataFromAMSOEM(startLimit, endLimit, forAll, vins);
		return vinLatLongMap;
	}

}
