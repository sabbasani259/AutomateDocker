package remote.wise.util;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.LocationDetails;
import remote.wise.service.datacontract.LocationDetailsMMI;


public class GetSetLocationJedis {
	
	
	/**
	 * 
	 * @param lattitude - trims the decimal point to 3 digits
	 * @param longitude - trims the decimal point to 3 digits
	 * @param redisPool - REDIS object
	 * @return exposed API to fetch the location from latlong
	 */
	
	/**
	 *  RC201603181245 update address redis cache changes by Shrini
	 */
	public  static LocationDetails getLocationDetails(final String lattitude, final String longitude, final Jedis redisPool){
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info(" ::: inside getLocationDetails ::: ");
		
		  LocationDetails locObj=null;
		
		ExecutorService executor = Executors.newCachedThreadPool();
		Callable<Object> task = new Callable<Object>() {
		   public Object call() {
			   //return storeRetriveLocation(lattitude.length()>6?lattitude.substring(0,6):lattitude,longitude.length()>6?longitude.substring(0,6):longitude, redisPool);
			   //CR-270  : Shajesh : 20220107 : Google Map Address changes
			   return storeRetriveLocation(lattitude,longitude, redisPool);
			  
			   
		   }
		};
		Future<Object> future = executor.submit(task);
		try {
			locObj = (LocationDetails) future.get(10, TimeUnit.SECONDS); 
		} catch (TimeoutException ex) {
		   // handle the timeout
		} catch (InterruptedException e) {
		   // handle the interrupts
		} catch (ExecutionException e) {
		   // handle other exceptions
		} finally {
		   future.cancel(true); // may or may not desire this
		}

		return locObj;
		//return storeRetriveLocation(lattitude.length()>6?lattitude.substring(0,6):lattitude,longitude.length()>6?longitude.substring(0,6):longitude, redisPool);
	}
	
	//MMI.sn
	public  static LocationDetailsMMI getLocationDetailsMMI(final String lattitude, final String longitude){
		Logger iLogger = InfoLoggerClass.logger;
		//iLogger.info(" ::: inside getLocationDetails mmi ::: ");
		
		  LocationDetailsMMI locObj= new LocationDetailsMMI();
		
		ExecutorService executor = Executors.newCachedThreadPool();
		Callable<Object> task = new Callable<Object>() {
		   public Object call() {
			   return storeRetriveLocationMMI(lattitude,longitude);
			  
			   
		   }
		};
		Future<Object> future = executor.submit(task);
		try {
			locObj = (LocationDetailsMMI) future.get(10, TimeUnit.SECONDS); 
		} catch (TimeoutException ex) {
		   // handle the timeout
		} catch (InterruptedException e) {
		   // handle the interrupts
		} catch (ExecutionException e) {
		   // handle other exceptions
		} finally {
		   future.cancel(true); // may or may not desire this
		}

		return locObj;
		//return storeRetriveLocation(lattitude.length()>6?lattitude.substring(0,6):lattitude,longitude.length()>6?longitude.substring(0,6):longitude, redisPool);
	}
	
	public  static LocationDetails getLocationDetailsCommReportMMI(final String lattitude, final String longitude){
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info(" ::: inside getLocationDetails mmi ::: ");
		
		  LocationDetails locObj=null;
		
		ExecutorService executor = Executors.newCachedThreadPool();
		Callable<Object> task = new Callable<Object>() {
		   public Object call() {
			   //return storeRetriveLocation(lattitude.length()>6?lattitude.substring(0,6):lattitude,longitude.length()>6?longitude.substring(0,6):longitude, redisPool);
			   //CR-270  : Shajesh : 20220107 : Google Map Address changes
			   return storeRetriveLocationMMI(lattitude,longitude);
			  
			   
		   }
		};
		Future<Object> future = executor.submit(task);
		try {
			locObj = (LocationDetails) future.get(10, TimeUnit.SECONDS); 
		} catch (TimeoutException ex) {
		   // handle the timeout
		} catch (InterruptedException e) {
		   // handle the interrupts
		} catch (ExecutionException e) {
		   // handle other exceptions
		} finally {
		   future.cancel(true); // may or may not desire this
		}

		return locObj;
		//return storeRetriveLocation(lattitude.length()>6?lattitude.substring(0,6):lattitude,longitude.length()>6?longitude.substring(0,6):longitude, redisPool);
	}
	//MMI.en
	
	/**
	 * 
	 * @param lattitude
	 * @param longitude
	 * @param pool - Redis object
	 * @return location of latlon - first checks if location present in REDIS, if not retrieve location from osm/google API and updates
	 *  and returns the location 
	 */
	
	//DF20190405:Abhishek::commented to change the order of address retrival. #Previous retrival: redis->OSM->google Api #current retrival: google Api->OSM->redis.
	/*public static LocationDetails storeRetriveLocation(String lattitude, String longitude, Jedis pool){
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		LocationDetails locationDetails = new LocationDetails();
		String location = null;
		String latlonbucket = lattitude.split("\\.")[0]+":"+longitude.split("\\.")[0];
		String key = lattitude+"#"+longitude;
		try{
			iLogger.info(" ::: inside storeRetriveLocation ::: ");
			if(pool.hexists(latlonbucket, key)){
				iLogger.info(" ::: inside REDIS ::: ");
				location = pool.hget(latlonbucket, key);
				String addressSplit[] = location.split("\\|");
				
				if(addressSplit[3].equalsIgnoreCase("undefined") || addressSplit[1].equalsIgnoreCase("undefined")){
					iLogger.info(" ::: inside REDIS ::: but its undefined ");
					locationDetails = new GeocodingLibrary().getLocationDetails(lattitude, longitude);
					try{
						pool.hset(latlonbucket, key, locationDetails.toString());
					}catch (Exception e) {
						fLogger.fatal(" error while setting the location "+e.getMessage());
					}
				}else{
					iLogger.info(" ::: inside REDIS ::: its fetched ");
					locationDetails.setCity(addressSplit[0]);
					locationDetails.setState(addressSplit[1]);
					locationDetails.setCountry(addressSplit[2]);
					locationDetails.setAddress(addressSplit[3]);
				}
				addressSplit = null;
			}else{
					locationDetails = new GeocodingLibrary().getLocationDetails(lattitude, longitude);
				try{
					pool.hset(latlonbucket, key, locationDetails.toString());
				}catch (Exception e) {
					fLogger.fatal(" error while setting the location "+e.getMessage());
				}
			}
		}catch (Exception e) {
			// Invoking OSM/Google incase REDIS is down by Shrini 080220160200
			locationDetails = new GeocodingLibrary().getLocationDetails(lattitude, longitude);
			fLogger.fatal("Exception while connecting to Jedis:::::: "+e.getMessage());
		}finally{
			try{
				latlonbucket = null;
				key = null;
				location = null;
			}catch (Exception e) {
				fLogger.fatal("Exception while setting objects to null "+e.getMessage());
			}
		}
		return  locationDetails;
	}*/
	
	//DF20190405:Abhishek::new method to change the order of address retrival. #Previous retrival: redis->OSM->google Api #current retrival: google Api->OSM->redis.
	public static LocationDetails storeRetriveLocation(String lattitude, String longitude, Jedis pool){
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		LocationDetails locationDetails = new LocationDetails();
		String location = null;
		String latlonbucket = lattitude.split("\\.")[0]+":"+longitude.split("\\.")[0];
		String key = lattitude+"#"+longitude;
			try
			{
				int updateRedis=0;
				
				//********************* STEP1: Fetch the location details corresponding to the LAT LONG from Google API for India
				long startTime = System.currentTimeMillis();
				locationDetails = new GeocodingLibrary().getAddressGoogleJSON(lattitude+","+longitude);	
				System.out.println("storeRetriveLocation : locationDetails "+ locationDetails );
				long endTime = System.currentTimeMillis();
				
				if(locationDetails==null || locationDetails.getCountry()==null || locationDetails.getCountry().equalsIgnoreCase("undefined"))
				{
					iLogger.info("WISE:DInt:SecondaryData:getAddress for Lat:"+lattitude+" and Long:"+longitude+"; addressDetails:"+locationDetails.toString()+";" +
							"From Google Maps India:FAILURE: Total time in ms:"+(endTime-startTime));
				}
				else
				{
					updateRedis=1;
					iLogger.info("WISE:DInt:SecondaryData:getAddress for Lat:"+lattitude+" and Long:"+longitude+"; From Google Maps India:SUCCESS: Total time in ms:"+(endTime-startTime));
				}
					
			
	     		//********************* STEP2: Fetch the location details corresponding to the LAT LONG from OSM
				if(locationDetails==null || locationDetails.getCountry()==null || locationDetails.getCountry().equalsIgnoreCase("undefined")|| 
						locationDetails.getState()==null || locationDetails.getState().equalsIgnoreCase("undefined")	)
				{
					startTime = System.currentTimeMillis();
					locationDetails = new GeocodingLibrary().getLocationDetailsFromOSM(lattitude,longitude);
					endTime = System.currentTimeMillis();
				
					if(locationDetails==null || locationDetails.getCountry()==null || locationDetails.getCountry().equalsIgnoreCase("undefined"))
					{
						iLogger.info("WISE:DInt:SecondaryData:getAddress for Lat:"+lattitude+" and Long:"+longitude+"; addressDetails:"+locationDetails.toString()+";" +
								"From OSM India:FAILURE: Total time in ms:"+(endTime-startTime));
					}
					else
					{
						updateRedis=1;
						iLogger.info("WISE:DInt:SecondaryData:getAddress for Lat:"+lattitude+" and Long:"+longitude+"; From OSM India:SUCCESS: Total time in ms:"+(endTime-startTime));
					}
				}	
				
				
				//DF20190624:Abhishek::Commented the logic to fetch the data from Redis
				//********************* STEP3: Fetch the location details from Redis if available 
				/*if(locationDetails==null || locationDetails.getCountry()==null || locationDetails.getCountry().equalsIgnoreCase("undefined")|| 
						locationDetails.getState()==null || locationDetails.getState().equalsIgnoreCase("undefined")	)
				{
					startTime = System.currentTimeMillis();
					locationDetails = new GeocodingLibrary().getLocationDetailsFromRedis(lattitude, longitude, pool);
					endTime = System.currentTimeMillis();
					if(locationDetails==null || locationDetails.getCountry()==null || locationDetails.getCountry().equalsIgnoreCase("undefined")|| 
							locationDetails.getState()==null || locationDetails.getState().equalsIgnoreCase("undefined")	)
					{						
							iLogger.info("WISE:DInt:SecondaryData:getAddress for Lat:"+lattitude+" and Long:"+longitude+";addressDetails:"+locationDetails.toString()+";" +
								"From Redis:FAILURE: Total time in ms:"+(endTime-startTime));
					}
					else
						iLogger.info("WISE:DInt:SecondaryData:getAddress for Lat:"+lattitude+" and Long:"+longitude+"; From Redis:SUCCESS: Total time in ms:"+(endTime-startTime));
				}*/
				//**************************** STEP4: Validate the address since the value is returned from Third party service
				//(For some Myanmar location, address will be returned in the local language)
				if(locationDetails!=null && locationDetails.toString().length()>0)
				{
					String validAddressStringSet = "[^A-Za-z0-9\\s\\,\\~\\`\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\+\\=\\{\\}\\[\\]\\:\\;\"\'\\<\\>\\/\\\\\\|\\,\\.]*";
					/*Pattern pattern = Pattern.compile(validAddressStringSet);
					for(Map.Entry<String, String> address: addressDetails.entrySet())
					{
						Matcher m = pattern.matcher(address.getValue());
						StringBuffer sb = new StringBuffer();
						while (m.find()) 
						{
							String s1 =m.group(0);
							sb.append(s1);
						}
						addressDetails.put(address.getKey(), sb.toString());
					}*/
					locationDetails.setCity(locationDetails.getCity().replaceAll(validAddressStringSet, ""));
					locationDetails.setState(locationDetails.getState().replaceAll(validAddressStringSet, ""));
					locationDetails.setCountry(locationDetails.getCountry().replaceAll(validAddressStringSet, ""));
					locationDetails.setPin(locationDetails.getPin().replaceAll(validAddressStringSet, ""));
					locationDetails.setAddress(locationDetails.getAddress().replaceAll(validAddressStringSet, ""));
					
					//DF20190624:Abhishek::Commented the logic to insert the data in Redis
					//********************************* STEP5: Insert the details in Redis
					/*if(updateRedis==1 && locationDetails!=null && locationDetails.getCountry()!=null && !(locationDetails.getCountry().equalsIgnoreCase("undefined"))
							&& locationDetails.getState()!=null || ! (locationDetails.getState().equalsIgnoreCase("undefined")) )
					{
						String redisUpdateStatus = new GeocodingLibrary().setLocationDetailsToRedis(lattitude, longitude, locationDetails, pool);
						iLogger.debug("WISE:DInt:SecondaryData:setAddress for Lat:"+lattitude+" and Long:"+longitude+"; " +
								"Update details in Redis:"+redisUpdateStatus);
						
					}*/
				}				
		}catch (Exception e) {
			// Invoking OSM/Google incase REDIS is down by Shrini 080220160200
			locationDetails = new GeocodingLibrary().getLocationDetails(lattitude, longitude);
			fLogger.fatal("Exception while connecting to Jedis:::::: "+e.getMessage());
		}finally{
			try{
				latlonbucket = null;
				key = null;
				location = null;
			}catch (Exception e) {
				fLogger.fatal("Exception while setting objects to null "+e.getMessage());
			}
		}
		return  locationDetails;
	}
	
	//MMI.sn
	public static LocationDetailsMMI storeRetriveLocationMMI(String lattitude, String longitude){
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		LocationDetailsMMI locationDetails = new LocationDetailsMMI();
		String location = null;
		String latlonbucket = lattitude.split("\\.")[0]+":"+longitude.split("\\.")[0];
		String key = lattitude+"#"+longitude;
			try
			{
				int updateRedis=0;
				//********************* STEP1: Fetch the location details corresponding to the LAT LONG from MAPmyIndia API for India
				long startTime = System.currentTimeMillis();
				locationDetails = new GeocodingLibrary().getAddressMMIJSON(lattitude,longitude);	
				iLogger.info("storeRetriveLocation MMI : locationDetails "+ locationDetails );
				System.out.println("storeRetriveLocationMMI : locationDetails "+ locationDetails );
				long endTime = System.currentTimeMillis();
				iLogger.info("getAddress from MMI total time in ms "+ (endTime-startTime) );
	     		//********************* STEP2: Fetch the location details corresponding to the LAT LONG from OSM
				if(locationDetails==null || locationDetails.getCountry()==null || locationDetails.getCountry().equalsIgnoreCase("undefined")
						|| locationDetails.getState()==null || locationDetails.getState().equalsIgnoreCase("undefined"))
				{
					iLogger.info("WISE:DInt:SecondaryData:getAddress for Lat:"+lattitude+" and Long:"+longitude+"; addressDetails:"+locationDetails.toString()+";" +
							"From Maps My India MMI :FAILURE: Total time in ms:"+(endTime-startTime));
				}
				else
				{
					updateRedis=1;
					iLogger.info("WISE:DInt:SecondaryData:getAddress for Lat:"+lattitude+" and Long:"+longitude+"; From Maps My India MMI :SUCCESS: Total time in ms:"+(endTime-startTime));
				}
									
	     		//********************* STEP2: Fetch the location details corresponding to the LAT LONG from OSM
				if(locationDetails==null || locationDetails.getCountry()==null || locationDetails.getCountry().equalsIgnoreCase("undefined")|| 
						locationDetails.getState()==null || locationDetails.getState().equalsIgnoreCase("undefined"))
				{
					startTime = System.currentTimeMillis();
					locationDetails = new GeocodingLibrary().getLocationDetailsFromOSMMMI(lattitude,longitude);
					endTime = System.currentTimeMillis();
					iLogger.info("get LocationDetails From OSM MMI  total time in ms "+ (endTime-startTime) );
					
					if(locationDetails==null || locationDetails.getCountry()==null || locationDetails.getCountry().equalsIgnoreCase("undefined"))
					{
						iLogger.info("WISE:DInt:SecondaryData:getAddress for Lat:"+lattitude+" and Long:"+longitude+"; addressDetails:"+locationDetails.toString()+";" +
								"From MMI OSM India:FAILURE: Total time in ms:"+(endTime-startTime));
					}
					else
					{
						updateRedis=1;
						iLogger.info("WISE:DInt:SecondaryData:getAddress for Lat:"+lattitude+" and Long:"+longitude+"; From MMI OSM India:SUCCESS: Total time in ms:"+(endTime-startTime));
					}
				}
				
				//(For some Myanmar location, address will be returned in the local language)
				if(locationDetails!=null && locationDetails.toString().length()>0)
				{
					String validAddressStringSet = "[^A-Za-z0-9\\s\\,\\~\\`\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\+\\=\\{\\}\\[\\]\\:\\;\"\'\\<\\>\\/\\\\\\|\\,\\.]*";
					
					locationDetails.setCity(locationDetails.getCity().replaceAll(validAddressStringSet, ""));
					locationDetails.setState(locationDetails.getState().replaceAll(validAddressStringSet, ""));
					locationDetails.setCountry(locationDetails.getCountry().replaceAll(validAddressStringSet, ""));
					locationDetails.setPin(locationDetails.getPin().replaceAll(validAddressStringSet, ""));
					locationDetails.setAddress(locationDetails.getAddress().replaceAll(validAddressStringSet, ""));

				}				
		}catch (Exception e) {
			// Invoking OSM/MApmyIndia incase REDIS is down by Shrini 080220160200
			locationDetails = new GeocodingLibrary().getLocationDetailsFromOSMMMI(lattitude, longitude);
			fLogger.fatal("Exception while getting the location details :::::: "+e.getMessage());
		}finally{
			try{
				latlonbucket = null;
				key = null;
				location = null;
			}catch (Exception e) {
				fLogger.fatal("Exception while setting objects to null "+e.getMessage());
			}
		}
		return  locationDetails;
	}
	//MMI.en
	
	
	
	public static LocationDetails RetriveLocationDetailsForSAARC(String lattitude, String longitude, Jedis pool){
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		LocationDetails locationDetails = new LocationDetails();
		String location = null;
		String latlonbucket = lattitude.split("\\.")[0]+":"+longitude.split("\\.")[0];
		String key = lattitude+"#"+longitude;
		try{
			iLogger.info(" ::: inside storeRetriveLocation ::: ");
			if(pool.hexists(latlonbucket, key)){
				iLogger.info(" ::: inside REDIS ::: ");
				location = pool.hget(latlonbucket, key);
				String addressSplit[] = location.split("\\|");

				if(addressSplit[3].equalsIgnoreCase("undefined") || addressSplit[1].equalsIgnoreCase("undefined")){
					iLogger.info(" ::: inside REDIS ::: but its undefined ");
					locationDetails = new GeocodingLibrary().getLocationDetailsSAARC(lattitude, longitude);
					try{
						pool.hset(latlonbucket, key, locationDetails.toString());
					}catch (Exception e) {
						fLogger.fatal(" error while setting the location "+e.getMessage());
					}
				}else{
					iLogger.info(" ::: inside REDIS ::: its fetched ");
					locationDetails.setCity(addressSplit[0]);
					locationDetails.setState(addressSplit[1]);
					locationDetails.setCountry(addressSplit[2]);
					locationDetails.setAddress(addressSplit[3]);
				}
				addressSplit = null;
			}else{
				locationDetails = new GeocodingLibrary().getLocationDetailsSAARC(lattitude, longitude);
				try{
					pool.hset(latlonbucket, key, locationDetails.toString());
				}catch (Exception e) {
					fLogger.fatal(" error while setting the location "+e.getMessage());
				}
			}
		}catch (Exception e) {
			// Invoking OSM/Google incase REDIS is down by Shrini 080220160200
			locationDetails = new GeocodingLibrary().getLocationDetailsSAARC(lattitude, longitude);
			fLogger.fatal("Exception while connecting to Jedis:::::: "+e.getMessage());
		}finally{
			try{
				latlonbucket = null;
				key = null;
				location = null;
			}catch (Exception e) {
				fLogger.fatal("Exception while setting objects to null "+e.getMessage());
			}
		}
		return  locationDetails;
	}
}
