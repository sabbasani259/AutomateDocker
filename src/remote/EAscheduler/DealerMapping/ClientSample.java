package remote.EAscheduler.DealerMapping;


public class ClientSample 
{

	public static void main(String[] args) 
	{
	       DealerMappingSampleService service1 = new DealerMappingSampleService();
	       DealerMappingSample port1 = service1.getDealerMappingSamplePort();
	       port1.processEAdealerMappingData("DealerMapping");
	    
	}
}
