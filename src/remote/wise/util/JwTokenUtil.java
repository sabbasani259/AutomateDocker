/**
 * CR406 : 20231117 : Dhiraj K : Fleet Management tab
 */
package remote.wise.util;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class JwTokenUtil {

	static Logger ilogger = InfoLoggerClass.logger;
	private static final String secret = "HUuTmHvxHg0NYY4j5NbSVaEEyM4Glx98VpqQ1HiehNb2sb3H73Uocso+BuUfvLmm";

	public static String generateJWToken(Map<String, String> payload) {
		ilogger.info("Generating JWToken");
		
		SecretKey secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
		Instant now = Instant.now();
		JwtBuilder jwtTokenBuilder = Jwts.builder()
				.issuedAt(Date.from(now))
				//.expiration(Date.from(now.plusSeconds(3600))) // To be enabled later on prod deployment
				.signWith(secretKey);
		
		payload.entrySet().forEach(action -> jwtTokenBuilder.claim(action.getKey(),action.getValue()));
		String jwToken = jwtTokenBuilder.compact();
		ilogger.info("Generated JWToken : " + jwToken);
		//System.out.println(jwToken);
		return jwToken;
	}

	public static HashMap<String, String> validateJwt(String jwToken) {
		HashMap<String, String> validationMap = new HashMap<>();
		try {
			ilogger.info("Decoding JWToken");
			SecretKey secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
			JwtParser parser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
			Claims claims = parser.parseSignedClaims(jwToken).getPayload();
			validationMap.put("status", "SUCCESS");
			validationMap.put("issuedAt", String.valueOf(claims.get("iat")));
			validationMap.put("expiration", String.valueOf(claims.get("exp")));
			validationMap.put("roleName", String.valueOf(claims.get("role_name")));
			validationMap.put("userName", String.valueOf(claims.get("user_name")));
			validationMap.put("loginId", String.valueOf(claims.get("loginId")));
		}catch (Exception e) {
			validationMap.put("status", "FAILURE");
			validationMap.put("reason", e.getMessage());
			
			ilogger.info("Exception : "+e.getMessage());
			//System.out.println("Exception JWToken : "+e.getMessage());
		}
		//System.out.println(validationMap);
		ilogger.info(validationMap);
		return validationMap;
	}
	

	
}