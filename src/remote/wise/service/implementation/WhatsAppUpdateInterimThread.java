package remote.wise.service.implementation;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;

public class WhatsAppUpdateInterimThread implements Runnable 
{
	Thread t;
	String NotificationTimeSlot;
	
	public WhatsAppUpdateInterimThread()
	{
		
	}
	
	public WhatsAppUpdateInterimThread(String NotificationTimeSlot)
	{
		t = new Thread(this, "WhatsAppUpdateInterimThread");
		this.NotificationTimeSlot = NotificationTimeSlot;
		t.start();
	}
	
	@Override
	public void run() {
		
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		String status = new WhatsAppUpdateDetailsImpl().sendWhatsAppNotification(this.NotificationTimeSlot);
		long endTime = System.currentTimeMillis();
		
		iLogger.info("WhatsAppUpdateService:WhatsAppUpdateInterimThread:NotificationTimeSlot:"+this.NotificationTimeSlot+";" +
		 		"WebService output:"+status+"; Total execution time in ms:"+(endTime-startTime));
		
	}
}
