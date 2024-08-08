package remote.wise.client.HAJassetContactDetailsService;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	     
		HAJassetContactDetailsServiceService service1 = new HAJassetContactDetailsServiceService();
        HAJassetContactDetailsService port1 = service1.getHAJassetContactDetailsServicePort();
		
        String methodcall = args[0];
		if (methodcall.equalsIgnoreCase("1")){
		   port1.setHAJassetContactDetails();
		}
		else if(methodcall.equalsIgnoreCase("2")){
			port1.getHAJassetContactDetails();	
		}
	}
}
