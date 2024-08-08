/**
 * 
 */
package remote.wise.service.webservice;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * @author KI270523
 *
 */
public class AppicationCodeProfileCodeApplication extends Application {
	
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
	
	public AppicationCodeProfileCodeApplication(){
		
		singletons.add(new AccountTenancyService());
		singletons.add(new ProfileCodeService());
	    
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
