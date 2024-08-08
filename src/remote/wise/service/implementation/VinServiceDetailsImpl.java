package remote.wise.service.implementation;

import remote.wise.businessobject.ReportDetailsBO;

/**
 * @author Rajani Nagaraju
 *
 */
public class VinServiceDetailsImpl 
{
	/** Implementation class to set the service details of all VINs for which service details are not defined
	 * @return Returns the status String
	 */
	public String setServiceDetails()
	{
		String status = "SUCCESS";
	
		ReportDetailsBO reportBoObj = new ReportDetailsBO();
		status = reportBoObj.setServiceDetails();
		
		return status;
	}
	
	
	//DF20191021 - Rajani Nagaraju - Extended Warranty Changes - START 
		//To include VIN as input to populate data for a single VIN if required.
		public String setServiceDetails(String serialNumber)
		{
			String status = "SUCCESS";
		
			ReportDetailsBO reportBoObj = new ReportDetailsBO();
			status = reportBoObj.setServiceDetails(serialNumber);
			
			return status;
		}
		//DF20191021 - Rajani Nagaraju - Extended Warranty Changes - START 
}
