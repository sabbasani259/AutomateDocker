package remote.wise.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;

/** This class handles XSD parsing using DOM parser
 * @author Rajani Nagaraju
 *
 */
public class XsdParser 
{
	String version;
	HashMap<String, List<String>> parametersMasterMap = new HashMap<String, List<String>>();
	
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return HashMap<ParameterType, ParameterList>
	 */
	public HashMap<String, List<String>> getParametersMasterMap() {
		return parametersMasterMap;
	}

	/**
	 * @param parametersMasterMap HashMap input with ParameterType as key and ParameterList as value
	 */
	public void setParametersMasterMap(HashMap<String, List<String>> parametersMasterMap) {
		this.parametersMasterMap = parametersMasterMap;
	}



	/** This method parse the XSD file placed in some location using DOM parser
	 * @return Returns the parsing output
	 * @throws IOException
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public XsdParser parseXsd() 
	{
	//	Logger fatalError = Logger.getLogger("fatalErrorLogger");
		Logger fatalError = FatalLoggerClass.logger;
		
		try
		{
			Properties prop = new Properties();
			String x=null;
			String nonAEMPparameters = null;
			String engineRunningBand = null;
			String eventTypeConvention = null;
		
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			
			if(prop.getProperty("deployenvironment").equalsIgnoreCase("SIT")){
				 x = prop.getProperty("xsdFilePath_SIT");
			}else if(prop.getProperty("deployenvironment").equalsIgnoreCase("DEV")){
				 x= prop.getProperty("xsdFilePath_DEV");
			}else if(prop.getProperty("deployenvironment").equalsIgnoreCase("QA")){
				 x= prop.getProperty("xsdFilePath_QA");
			}else if(prop.getProperty("deployenvironment").equalsIgnoreCase("PROD")){
				 x= prop.getProperty("xsdFilePath_PROD");
			}else {
				 x= prop.getProperty("xsdFilePath");
			}
			
           
            nonAEMPparameters = prop.getProperty("Non_AEMP_Parameters");
            engineRunningBand = prop.getProperty("EngineRunningBand");
            eventTypeConvention = prop.getProperty("XSDconvention");
            
		
            String reqExp = eventTypeConvention.replaceAll(",", "|");
		
			XsdParser s = new XsdParser();
			String xsdInput = s.readFile(x);
			
			InputSource xsdFile = new InputSource(new ByteArrayInputStream(xsdInput.getBytes("UTF-8")));
		
			//HashMap<String, List<String>> parametersMasterMap = new HashMap<String, List<String>>();
			List<String> msgAttributes = new LinkedList<String>();
		
			//Map of parameters and their corresponding Complex types
			HashMap<String, String> parametersComplexTypeMap = new HashMap<String, String>();
			
			//Each Complex type and thier Names Map
			HashMap<String, String> complexTypeNameMap = new HashMap<String, String>();
			
			
			//Using factory get an instance of document builder
			DocumentBuilderFactory buildFactory = DocumentBuilderFactory.newInstance();
			
			DocumentBuilder documentBuilder = buildFactory.newDocumentBuilder();
			
			//parse the XSD file
			Document document = documentBuilder.parse (xsdFile);
			
			//document.getDocumentElement () - returns the root node
			Element rootNode =document.getDocumentElement();
			rootNode.normalize();
			
			//get the xsd version
			version=rootNode.getAttribute("version");
			
			NodeList childNodes = rootNode.getChildNodes();
			int totalChildNodes = childNodes.getLength();
						
						
			//Iterate through each child - Get the start element of the message
			String messageAttributeType=null;
			for(int i=1; i<totalChildNodes; i=i+2)
			{
				Node childNode = childNodes.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element childElement = (Element)childNode;
					
					if(childElement.getAttribute("name")!= null && !(childElement.getAttribute("type").isEmpty()) )
					{
						
						//type of the element will be always prefixed with "xs:" which is not there in the corresponding Complex Type element names
						messageAttributeType = childElement.getAttribute("type").substring(3);
						break;
												
					}
				}
				
			}
			
			String nonAEMPparamEleType = null;
			String engineRunningBandType = null;
			
			//iterate through all the child nodes
			for(int i=1; i<totalChildNodes; i=i+2)
			{
				Node childNode = childNodes.item(i);
				Element childElement = (Element)childNode;
				
				//Skip the basic node that specifies the message start
				if(! (childElement.getAttribute("name")!= null && !(childElement.getAttribute("type").isEmpty()) ) )
				{
					//get the parameter types and put to complexTypeNameMap
					if(childElement.getAttribute("name").equals(messageAttributeType))
					{
						//iterate through the childs to get parameter types and attributes
						NodeList messageTypeChildElement = childElement.getChildNodes();
						
						for(int r=1; r<messageTypeChildElement.getLength(); r=r+2 )
						{
							//get the parameter types
							if(messageTypeChildElement.item(r).getNodeName().equals("xs:sequence"))
							{
								Element parameterTypeElement = (Element)messageTypeChildElement;
								NodeList parameterTypeList = parameterTypeElement.getElementsByTagName("xs:sequence");
								
								for(int h=0; h<parameterTypeList.getLength(); h++)
								{
									NodeList parameterType = parameterTypeList.item(h).getChildNodes();
									for(int u=1; u<parameterType.getLength(); u=u+2)
									{
										Element parameterTypeEle = (Element)parameterType.item(u);
										complexTypeNameMap.put(parameterTypeEle.getAttribute("type").substring(3), parameterTypeEle.getAttribute("name"));
										
									}
								}
							}
							
							//get the attributes
							else
							{
								Element messageTypeAttributes = (Element)messageTypeChildElement.item(r);
								msgAttributes.add(messageTypeAttributes.getAttribute("name"));
							}
						}
						
						
					}
					
					
					//parse only the complex types to get monitoring parameters
					else if(childElement.getNodeName().substring(3).equalsIgnoreCase("complexType"))
					{
										
						//get the elements within the sequence of complex types
						NodeList elementsWithinSeqList = childElement.getElementsByTagName("xs:sequence");
												
						for(int y=0; y < elementsWithinSeqList.getLength(); y++)
						{
								NodeList parameterElementList = elementsWithinSeqList.item(y).getChildNodes();
								for(int p=1; p<parameterElementList.getLength(); p=p+2)
								{
									Element parameterElement = (Element)parameterElementList.item(p);
									
									if( (parameterElement.getAttribute("name").equalsIgnoreCase("TimeStamp")) || (parameterElement.getAttribute("name").toLowerCase().contains("unit")) )
									{
										continue;
									}
									

									if(childElement.getAttribute("name").equalsIgnoreCase(nonAEMPparameters+"Type"))
									{
													
										complexTypeNameMap.put(parameterElement.getAttribute("type").substring(3), parameterElement.getAttribute("name"));
										continue;
									}
									
									if(parameterElement.getAttribute("name").equalsIgnoreCase("REC_"+engineRunningBand))
									{
										complexTypeNameMap.put(parameterElement.getAttribute("type").substring(3), parameterElement.getAttribute("name"));
										continue;
									}
									
									if(childElement.getAttribute("name").equalsIgnoreCase("REC_"+engineRunningBand+"Type"))
									{
															
										parametersComplexTypeMap.put("REC_"+parameterElement.getAttribute("name"), "LogParametersType");
										continue;
									}
									
									Pattern pattern = Pattern.compile(reqExp);
									Matcher m = pattern.matcher(parameterElement.getAttribute("name"));
									if(m.find())
										parametersComplexTypeMap.put(parameterElement.getAttribute("name"), childElement.getAttribute("name"));
									else
										parametersComplexTypeMap.put("REC_"+parameterElement.getAttribute("name"), childElement.getAttribute("name"));
								}
						}
						
											
					}
				}
			}
			
			//set parameter and parameter Type HashMap
			Iterator itr = parametersComplexTypeMap.entrySet().iterator();
			while(itr.hasNext())
			{
				Map.Entry pairs = (Map.Entry) itr.next();
				pairs.setValue(complexTypeNameMap.get(pairs.getValue()));
				
			}
			
			for(int y=0; y<parametersComplexTypeMap.size(); y++)
			{
				String key = (String) parametersComplexTypeMap.keySet().toArray()[y];
				String value = (String) parametersComplexTypeMap.values().toArray()[y];
				
				if(value!=null)
				{
					if(parametersMasterMap.containsKey(value))
					{
						List<String> parameters = parametersMasterMap.get(value);
						parameters.add(key);
					}
					else
					{
						List<String> parameterList = new LinkedList<String>();
						parameterList.add(key);
						parametersMasterMap.put(value, parameterList);
					}
				}
			}
			
						
			for(int w=0; w<parametersMasterMap.size(); w++)
			{
				String key = (String)parametersMasterMap.keySet().toArray()[w];
				List<String> valueList = new LinkedList<String>();
				
				if(parametersMasterMap.values()!=null)
				{
					valueList = (List<String>)parametersMasterMap.values().toArray()[w];
				}
				
			}
		} 
		catch(Exception e)
		{
			fatalError.fatal("Exception :"+e);
		}
		
		return this;
	}
	
	
	
	//To read the file and place it into String
	/** This method reads the file and place the contents into a String
	 * @param xsdInput XSD file path as String
	 * @return Returns contents of file as String
	 * @throws IOException
	 */
	private String readFile( String xsdInput ) throws IOException 
	{
		BufferedReader reader = new BufferedReader( new FileReader (xsdInput));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    while( ( line = reader.readLine() ) != null ) {
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }

	    return stringBuilder.toString();
	}
	

}

