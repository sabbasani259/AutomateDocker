package remote.wise.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class StaticProperties 
{
	public static final SimpleDateFormat dateTimeSecFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SS");
	public static final SimpleDateFormat dateTimeFormat= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	public static final Properties prop= loadProperties();
	
	public static Properties loadProperties()
	{
		
		Properties prop1 =new Properties();
		try
		{
			prop1.load(StaticProperties.class.getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		
		return prop1;
	}
	
	public static Properties getConfProperty()
	{
		return prop;
	}
	
}
