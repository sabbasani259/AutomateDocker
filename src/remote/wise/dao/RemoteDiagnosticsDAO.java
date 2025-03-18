package remote.wise.dao;
/**
 * CR517-20408644:Sai Divya:API for RemoteDiagnostics
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class RemoteDiagnosticsDAO {

	private static SecretKeySpec secretKey;
	private static byte[] key;

	public static void setKey(String mykey) {
		Logger iLogger = InfoLoggerClass.logger;
		MessageDigest sha = null;
		try {
			key = mykey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			iLogger.error("Unable to find secret key generation algo: " + e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			iLogger.error("Encoding not supported: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Encrypt the String by using secret
	 * 
	 * @param strToEncryptis
	 *            credentials passed
	 * @param secret
	 *            is secret generated from setSecret
	 * @return ecrypted crendentials
	 **/
	public static String encrypt(String strToEncrypt, String secret) {
		Logger iLogger = InfoLoggerClass.logger;
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			iLogger.error("Encoding not supported: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Decrypt the String by using secret
	 * 
	 * @param strToDecryptencrypted string to decrypt
	 * 
	 * @param secretkey used to decrypt
	 * 
	 * @return the decrypt credentials
	 */

	public static String decrypt(String strToDecrypt, String secret) {
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info(strToDecrypt + "," + secret);
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			e.printStackTrace();
			iLogger.error("Error while decryting: " + e.getMessage());
		}
		return null;
	}

	public Map<String, String> passWordFromDB(String contactId) {
		Map<String, String> resultMap = new HashMap<>();
		// Initialize the decrypted password as null
		String decryptedPassword = null;
		String roleName = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		ConnectMySQL connMySql = new ConnectMySQL();
		iLogger.info(contactId);

		try (Connection prodConnection = connMySql.getConnection()) {
			String query = "SELECT rr.Role_Name,CAST(AES_DECRYPT(password, primary_moblie_number) AS CHAR(50)) "
					+ "password_decrypt "
					+ "FROM  wise.contact c, role rr where status = 1 and c.role_id=rr.role_id and contact_id= '"
					+ contactId + "'";

			stmt = prodConnection.prepareStatement(query);
			rs = stmt.executeQuery();
			iLogger.info(query);

			if (rs.next()) {
				// Retrieve the role_id and decrypted password
				roleName = rs.getString("Role_Name");
				decryptedPassword = rs.getString("password_decrypt");
			}
			resultMap.put("roleName", roleName);
			resultMap.put("decryptedPassword", decryptedPassword);
		} catch (SQLException e) {
			e.printStackTrace(); 
			fLogger.fatal("Error processing the response: ", e);
		} finally {
			// Ensure resources are closed
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return resultMap;
	}

	public  String postCall(String url, String jsonStringInput) {
		String result = null;
		String output = null;
		OutputStream os = null;
		BufferedReader br = null;
		Logger infoLogger = InfoLoggerClass.logger;

		long startTime = System.currentTimeMillis();
		infoLogger.info("Before calling URL: " + url + "\nInputs are: \n JSON Input: " + jsonStringInput);

		try {
			// Create a URL object from the provided URL string
			URL apiUrl = new URL(url);

			// Open a connection to the URL
			HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();

			// Set the HTTP method to POST
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			// Set the content type to application/json
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(30000);
			infoLogger.info("check....1");
			os = (OutputStream) conn.getOutputStream();
			os.write(jsonStringInput.getBytes());
			os.flush();
			infoLogger.info("check....2");
			// Check if the response code is 200 (HTTP OK)
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			while ((output = br.readLine()) != null) {
				result = output;
			}

			// Disconnect the connection after the operation
			conn.disconnect();
			long endTime = System.currentTimeMillis();

		} catch (MalformedURLException e) {
	        infoLogger.error("MalformedURLException thrown while calling " + url + " :: " + e.getMessage());
	    } catch (IOException e) {
	        infoLogger.error("IOException thrown while calling " + url + " :: " + e.getMessage());
	    } catch (Exception e) {
	        infoLogger.error("Exception thrown while calling " + url + " :: " + e.getMessage());
	    } finally {
	        if (os != null) {
	            try {
	                os.close();
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
	        }
	        if (br != null) {
	            try {
	                br.close();
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
	        }
	    }
		infoLogger.info(result);
	    return result;
	}

}
