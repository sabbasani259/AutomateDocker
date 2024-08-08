/**
 * 
 */
package remote.wise.service.webservice;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * @author sunayak
 *
 */
public class HAJsendSMSpktListener  extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
	public HAJsendSMSpktListener()
	{
	     singletons.add(new HAJsendSMSpktService());
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
