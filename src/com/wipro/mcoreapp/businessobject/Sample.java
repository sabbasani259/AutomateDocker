package com.wipro.mcoreapp.businessobject;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.wipro.mcoreapp.implementation.AlertSubscriptionImpl;

public class Sample 
{
	public static void main(String args[]) throws  IOException
	{
		/*String assetId="HAR3DX0012";
		String loginId = "deve0011";
		
		HashMap<String, String> data = new HashMap<String, String>();
	      data.put("EMAIL1", "deve0011");
	      data.put("EMAIL2", "gunj00199");
	      data.put("SMS1", "deve0011");
	      
	      
	      HashMap<String, String> receiver2 = new HashMap<String, String>();
	      receiver2.put("EMAIL1", "anim00198");
	      receiver2.put("EMAIL2", "chan00247");
	      receiver2.put("SMS1", "anim00198");
	      receiver2.put("SMS2", "kaus0010");
	      
	      HashMap<String, HashMap> data1 = new HashMap<String, HashMap>();
	      
	     // data1.put("Subscriber1", data);
	     data1.put("Subscriber2", receiver2);
	 
	     //Object subscriberGroup = new JSONObject(data1);
	     JSONObject json = new JSONObject(data1);
	      String subscriberGroupJSON = json.toJSONString(); */
	      
	     // System.out.println("Before Call: subscriberGroupJSON"+subscriberGroupJSON);
	    //  String status = new AlertSubscriptionImpl().setSubscriberGroupDetails(assetId, loginId, subscriberGroupJSON);
	     // System.out.println("After call: status: "+status);
		
		
		//---------------------------Get Method
		/*String json = new AlertSubscriptionImpl().getSubscriberGroupDetails("HAR3DX0012");
		System.out.println(json);*/
		
		//---------------------------Get Contact details
		/*List<SubscriberContactEntity> contactList = new AlertSubscriptionImpl().getContactDetails("HAR4DXLDV01414772", "Subscriber3");
		String contactFinalList = new Gson().toJson(contactList);
		
		System.out.println(contactFinalList);*/
		/*
		HashMap<String,HashMap<String,String>> map1 = new HashMap<String,HashMap<String,String>> ();
		HashMap<String,String> innerMap = new HashMap<String,String>();
		innerMap.put("SMS", "deve0011");
		
		map1.put("HAR3DX001", innerMap);
		
		if(map1.containsKey("HAR3DX001"))
		{
			HashMap<String,String> newMap = new HashMap<String,String>();
			newMap.put("Email", "deve0011");
			HashMap<String,String> prevMap = map1.get("HAR3DX001");
			prevMap.putAll(newMap);
		}
		
		System.out.println(map1);*/
		HashMap finalMap = new HashMap();
		
		HashMap<String,String> newMap = new HashMap<String,String>();
		newMap.put("SMS", "m/s.00153");
		newMap.put("Email", "deve0011");
		/*String finalValue = new JSONObject(newMap).toString();
		finalValue = finalValue.replaceAll("\\\\","");
		System.out.println("finalValue:"+finalValue);*/
		
		finalMap.put("HAR3DX001", newMap);
		//System.out.println(new JSONObject(finalMap));
		/*String subscriberGroupJSON = new JSONObject(finalMap).toString();
		//subscriberGroupJSON = subscriberGroupJSON.replaceAll("/","\\/");
		System.out.println("subscriberGroupJSON:"+subscriberGroupJSON);
		
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String,Object> groupContactMap = mapper.readValue(subscriberGroupJSON, new TypeReference<Map<String, String>>(){});
		HashMap<String,String> modeContactMap = null;
		for(Map.Entry entry : groupContactMap.entrySet())
		{
			System.out.println(entry.getValue().toString());
			modeContactMap = mapper.readValue(entry.getValue().toString(), new TypeReference<Map<String, String>>(){});
		}
		
		HashMap<String,Object> groupContactMap = new Gson().fromJson(subscriberGroupJSON, new TypeToken<HashMap<String, Object>>() {}.getType());
		HashMap<String,String> modeContactMap = null;
		for(Map.Entry entry : groupContactMap.entrySet())
		{
			String value=entry.getValue().toString();
			System.out.println("valueBefore:"+value);
			value=value.replaceAll("\\s+","");
			value= value.replaceAll("\\{", "\\{\"");
			value =value.replaceAll("\\=", "\":\"");
			value=value.replaceAll(",", "\",\"");
			value=value.replaceAll("\\}", "\"\\}");
			
			System.out.println("valueAfter:"+value);
			Gson gson = new Gson();
			JsonReader reader = new JsonReader(new StringReader(entry.getValue().toString()));
			reader.setLenient(true);
			modeContactMap = new Gson().fromJson(value, new TypeToken<HashMap<String, String>>() {}.getType());
		}
	   
		System.out.println("modeContactMap:"+modeContactMap);*/
	}
}
