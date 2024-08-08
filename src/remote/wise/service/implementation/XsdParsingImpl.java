package remote.wise.service.implementation;

import java.io.IOException;

import remote.wise.businessobject.AssetMonitoringDetailsBO;
import remote.wise.exception.CustomFault;

/** Implementation class to parse XSD
 * @author Rajani Nagaraju
 *
 */
public class XsdParsingImpl 
{
	/** This method parse the XSD placed in a specific location using DOM parser
	 * @return Returns the status String
	 */
	public String parserXSD() 
	{
		AssetMonitoringDetailsBO assetMonitoringDetails = new AssetMonitoringDetailsBO();
		
		String status = assetMonitoringDetails.setMonitoringParameters();
		
		return status;
	}
}
