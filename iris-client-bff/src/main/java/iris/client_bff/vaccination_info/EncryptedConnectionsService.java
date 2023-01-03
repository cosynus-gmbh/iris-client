package iris.client_bff.vaccination_info;

import static java.nio.charset.StandardCharsets.*;
import static org.springframework.util.Base64Utils.*;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iris.client_bff.vaccination_info.eps.InvalidPublicKeyException;
import iris.client_bff.vaccination_info.eps.VaccinationInfoController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Jens Kutzsche
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EncryptedConnectionsService {


	private static final String ALGORITHM = "AES/GCM/NoPadding";
	private static final String PROVIDER = "BCFIPS";

	private final ObjectMapper objectMapper;

	public static record EncryptedDataDto(

			@NotBlank String hdPublicKey,
			@NotBlank String iv,
			@NotBlank String tokens) {}

	public KeyPair generateKeyPair() throws GeneralSecurityException {

		KeyPairGenerator keyPair = KeyPairGenerator.getInstance("EC", PROVIDER);
		keyPair.initialize(384);

		return keyPair.generateKeyPair();
	}

	public String encodeToBase64(PublicKey key) {
		return encodeToString(key.getEncoded());
	}

	public PublicKey decodeFromBase64(String encodedKey) throws GeneralSecurityException {

		var kf = KeyFactory.getInstance("EC", PROVIDER);

		var keySpecX509 = new X509EncodedKeySpec(decodeFromString(encodedKey));

		return kf.generatePublic(keySpecX509);
	}

	public SecretKey generateAgreedKey(PrivateKey ownPrivate, PublicKey foreignPublic)
			throws GeneralSecurityException {

		KeyAgreement agreement = KeyAgreement.getInstance("ECDH", PROVIDER);
		agreement.init(ownPrivate);
		agreement.doPhase(foreignPublic, true);

		return agreement.generateSecret("AES[256]");
	}

	public EncryptedData encryptAndEncode(SecretKey key, String data)
			throws GeneralSecurityException {

		var iv = SecureRandom.getSeed(12);

		Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
		cipher.init(Cipher.ENCRYPT_MODE, key, // taglength, nonce
				new GCMParameterSpec(128, iv));

		return new EncryptedData(encodeToString(iv), encodeToString(cipher.doFinal(data.getBytes(UTF_8))));
	}

	public String decodeAndDecrypt(SecretKey key, String encodedIv, String encodedEncryptedData)
			throws GeneralSecurityException {

		Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
		cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, decodeFromString(encodedIv)));

		return new String(cipher.doFinal(decodeFromString(encodedEncryptedData)), UTF_8);
	}

	public EncryptedDataDto encryptAndCreateResult(Object plainObject, String submitterPublicKeyBase64) {

		PublicKey submitterPublicKey;
		try {
			submitterPublicKey = decodeFromBase64(submitterPublicKeyBase64);
		} catch (GeneralSecurityException e) {

			var msg = "The passed public key contains errors and cannot be used";
			log.error(msg + ": ", e);

			throw new InvalidPublicKeyException("submitterPublicKey: " + msg, e);
		}

		try {
			var keyPair = generateKeyPair();
			var key = generateAgreedKey(keyPair.getPrivate(), submitterPublicKey);

			var pubKeyBase64 = encodeToBase64(keyPair.getPublic());
			var encryptionData = encryptAndEncode(key, objectMapper.writeValueAsString(plainObject));

			return new EncryptedDataDto(pubKeyBase64, encryptionData.iv(), encryptionData.data());
		} catch (JsonProcessingException | GeneralSecurityException e) {

			var msg = e instanceof JsonProcessingException
					? "Can't write tokens to JSON"
					: "Error during token encryption (response to the announcement)";
			log.error(msg + ": ", e);

			throw new VaccinationInfoAnnouncementException(msg, e);
		}
	}

	public record EncryptedData(String iv, String data) {}
}


