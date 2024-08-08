package remote.wise.service.webservice;

import javax.jws.WebService;
/**
 *  This Service Class will allow to Update AssetClassDimensionId in Fact Tables.
 * @author jgupta41
 *
 */
@WebService(name = "UpdateRemoteFactService")
public class UpdateRemoteFactService {
	/*@WebMethod(operationName = "updateRemoteFact", action = "updateRemoteFact")
public String updateRemoteFact() throws CustomFault {
		
		Logger infoLogger = Logger.getLogger("infoLogger");
		long startTime = System.currentTimeMillis();
		String response_msg = new UpdateRemoteFactImpl().updateRemoteFact();
		long endTime = System.currentTimeMillis();
		infoLogger.info("Webservice Execution Time in ms:"+ (endTime - startTime));
		return response_msg;
	}*/
}
