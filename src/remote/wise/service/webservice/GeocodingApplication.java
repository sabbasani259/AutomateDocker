package remote.wise.service.webservice;

import java.util.Set;
import java.util.HashSet;
import javax.ws.rs.core.Application;

public class GeocodingApplication extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
	public GeocodingApplication(){
	     singletons.add(new GeocodingService());
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




