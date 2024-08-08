package remote.wise.service.webservice;

import java.util.Set;
import java.util.HashSet;
import javax.ws.rs.core.Application;

public class PullSMS extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
	public PullSMS(){
	     singletons.add(new HelloWorldResource());
	}
	@Override
	public Set<Class<?>> getClasses() {
	     return empty;
	}
	@Override
	public Set<Object> getSingletons() {
	     return singletons;
	}
}
