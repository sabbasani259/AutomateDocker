package remote.wise.client.HAJassetLocationDetailsService;

public class ClientSample {

	public static void main(String[] args) throws CustomFault {
	        HAJassetLocationDetailsServiceService service1 = new HAJassetLocationDetailsServiceService();
	        HAJassetLocationDetailsService port1 = service1.getHAJassetLocationDetailsServicePort();
	        String methodcall = args[0];
			if (methodcall.equalsIgnoreCase("1")){
			   port1.setassetLocationDetails();
			}
			else if(methodcall.equalsIgnoreCase("2")){
				port1.getHAJassetLocationDetails();	
			}
	        System.out.println("Call Over!");
	}
}
