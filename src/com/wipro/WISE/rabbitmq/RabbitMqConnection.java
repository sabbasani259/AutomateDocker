package com.wipro.WISE.rabbitmq;



import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultSaslConfig;

public class RabbitMqConnection {
	private static Connection connection;
	static SSLContext context;
	static Logger iLogger = InfoLoggerClass.logger;
	static Logger fLogger = FatalLoggerClass.logger;
	private RabbitMqConnection(){

	}
	static TrustManager[] trustManager = { new X509TrustManager() {
		public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
				throws CertificateException { }

		public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException { }

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}};
	
	public synchronized static Connection getConnection() {
		if(connection ==null|| (connection !=null && ! connection.isOpen())){
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("13.127.149.159");
			factory.setUsername("external_publisher");
			factory.setPort(5678);
			factory.setPassword("c3f3cc444a614c1fb5769e7052975b9d");
			factory.setSaslConfig(DefaultSaslConfig.PLAIN);
			factory.setConnectionTimeout(1000);		//wait for 1000 milli seconds to get connection. time out in milliseconds.
			char[] passphrase = "MySecretPassword".toCharArray();
			try
			{
				KeyStore keystore = KeyStore.getInstance("PKCS12");
				keystore.load(new FileInputStream("/user/JCBLiveLink/EdgeProxy/data/M2M/properties/client.p12"), passphrase);
				KeyManagerFactory keyManagerFactory = 
						KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				keyManagerFactory.init(keystore, passphrase);
				context = SSLContext.getInstance("TLSv1.2");
				context.init(keyManagerFactory.getKeyManagers(), trustManager, null);
				factory.useSslProtocol(context);
//		      factory.setAutomaticRecoveryEnabled(true);

				connection = factory.newConnection();
				iLogger.info("Testing: Connection complete");

			}
			catch(Exception e){
				iLogger.info("Exception occored while creating RabbitMQConnection :: check fatal loggers");
				fLogger.fatal("Exception occured while creating connection: \n"+e.getMessage());
				e.printStackTrace();
			}
		}
		return connection;
	}
}
