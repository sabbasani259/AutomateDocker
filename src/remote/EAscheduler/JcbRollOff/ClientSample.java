package remote.EAscheduler.JcbRollOff;

public class ClientSample {

	public static void main(String[] args) {
	        
	        JcbRollOffSampleService service1 = new JcbRollOffSampleService();
	        JcbRollOffSample port1 = service1.getJcbRollOffSamplePort();
	        port1.processEARollOffdata("Sample");
	}
}
