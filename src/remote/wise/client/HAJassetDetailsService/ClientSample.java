package remote.wise.client.HAJassetDetailsService;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        
	        HAJassetDetailsServiceService service1 = new HAJassetDetailsServiceService();
	        HAJassetDetailsService port1 = service1.getHAJassetDetailsServicePort();
	        
	        String methodcall = args[0];
			if (methodcall.equalsIgnoreCase("1")){
			   port1.setHAJassetDetails();
			}
			else if(methodcall.equalsIgnoreCase("2")){
				port1.getHAJassetDetails();	
			}
	}
}
