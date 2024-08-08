package remote.wise.util;

import java.util.List;
import java.util.Set;

public class ListToStringConversion 
{
	public StringBuilder getIntegerListString(List<Integer> integerList)
	{
		//Get a comma delimited string from IntegerList
		StringBuilder integerListString= new StringBuilder();
		if(! (integerList==null || integerList.isEmpty()) )
		{
			String delimiter = "";
			for(int i :integerList)
			{
				integerListString.append(delimiter).append(i);
				delimiter = ",";
			}
		}
		//System.out.println(integerListString);
		return integerListString;
	}
	
	
	//Get a comma delimited string from StringList
	public StringBuilder getStringList(List<String> stringList)
	{
		//Get a comma delimited string from StringList
		StringBuilder delimitedString= new StringBuilder();
		if(! (stringList==null || stringList.isEmpty()) )
		{
			String delimiter = "'";
			for(String i :stringList)
			{
				delimitedString.append(delimiter).append(i).append("'");
				delimiter = ",'";
			}
		}
		//System.out.println(delimitedString);
		return delimitedString;
	}
	//@Roopa DF20160318 Get StringWithoutQuoteList from StringList
	public StringBuilder getStringWithoutQuoteList(List<String> stringList)
	{
		//Get a comma delimited string from StringList
		StringBuilder delimitedString= new StringBuilder();
		if(! (stringList==null || stringList.isEmpty()) )
		{
			for(String i :stringList)
			{
				delimitedString.append(i).append(",");
				
			}
		}
		//System.out.println(delimitedString);
		return delimitedString;
	}
	//Get a comma delimited string from set
	public StringBuilder getStringListFromSet(Set<String> stringSet)
	{
		//Get a comma delimited string from StringList
		StringBuilder delimitedString= new StringBuilder();
		if(! (stringSet==null || stringSet.isEmpty()) )
		{
			String delimiter = "'";
			for(String i :stringSet)
			{
				delimitedString.append(delimiter).append(i).append("'");
				delimiter = ",'";
			}
		}
		//System.out.println(delimitedString);
		return delimitedString;
	}
	
	public StringBuilder getCommaSeperatedStringList(List<String> stringList)
	{
		//Get a comma delimited string from StringList
		StringBuilder delimitedString= new StringBuilder();
		if(! (stringList==null || stringList.isEmpty()) )
		{
			String delimiter="";
			for(String i :stringList)
			{
				delimitedString.append(delimiter).append(i);
				delimiter = ",";
			}
		}
		//System.out.println(delimitedString);
		return delimitedString;
	}
	public static String removeLastComma(String str) {
	    final int len = str.length();
	    return len > 0 && str.charAt(len - 1) == ',' ?
		str.substring(0, len - 1) : 
		str;
	  }
	
	//CR350-two new DTC codes added for gas genset CR for model G125NG
	public static String  filterAlertmap(String alertMap){
		String filter=null;
		int index=alertMap.indexOf("026");
		if(index>0)
//		filter=alertMap.substring(0,index-3)+"}";   //CR350.o
		filter=alertMap.substring(0,index-3);		//CR350.n
		return filter;
	}
	
	public	static String vinsToSQlInVinsList (String inputString){

		if( inputString !=null && !inputString.isEmpty()){
		String singleQuote = "'";
		StringBuilder updatedString = new StringBuilder();;
		String[] split = inputString.split(",");

		for(String i : split){
			updatedString.append(singleQuote+i+singleQuote+",");
		}
		
		updatedString.deleteCharAt(updatedString.length() -1);
		
		System.out.println(updatedString);
	     return updatedString.toString();
		}else{
			return null;
		}
	}
}
