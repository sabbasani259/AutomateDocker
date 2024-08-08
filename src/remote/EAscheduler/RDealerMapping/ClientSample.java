package remote.EAscheduler.RDealerMapping;


public class ClientSample 
{

	public static void main(String[] args) 
	{
	        RDealerMappingSampleService service1 = new RDealerMappingSampleService();
	        RDealerMappingSample port1 = service1.getRDealerMappingSamplePort();
	        port1.reprocessEADealerMappingData("RDealerMapping");
	}
}
