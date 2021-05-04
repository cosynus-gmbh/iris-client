/*
 * IRIS-Gateway API
 * ### Encryption of the data to be transmitted (contact data) In order to be not limited in the amount of data, a hybrid encryption with symmetric encryption of the data and asymmetric encryption of the symmetric key is used for the encryption of the contact data.    1. The apps and applications get the public key of the health department as a 4096-bit RSA key from the IRIS+ server. This key is base64-encoded similar to the Private Enhanced Mail (PEM) format but without key markers (-----BEGIN PUBLIC KEY----- / -----END PUBLIC KEY-----).   2. The app generates a 256-bit AES key.   3. The data is encrypted with this key (algorithm: AES/CBC/PKCS5Padding and 16 byte IV)   4. IV bytes are prepended to the cipher text. Those merged bytes represent the encrypted content.   5. The AES key must be encrypted with the public RSA key of the health department. (algorithm: RSA with Optimal Asymmetric Encryption Padding (OAEP) \"RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING\")   6. The encrypted AES key and the encrypted content must be transmitted base64 encoded.    #### Schematic sequence    ```   pubKeyEncryption = publicKeyFromBase64(givenPublicKey);   contentKey = generateAESKey();   iv = generateRandomBytes(16);    encrypted = contentKey.encrypt(content, \"AES/CBC/PKCS5Padding\", iv);   keyEncrypted = pubKeyEncryption.encrypt(contentKey, \"RSA/NONE/OAEPWithSHA3-256AndMGF1Padding\");    submissionDto.encryptedData = base64Encode(concat(iv,encrypted));   submissionDto.secret = base64Encode(keyEncrypted);   ``` 
 *
 * The version of the OpenAPI document: 0.2.0
 * Contact: jens.kutzsche@gebea.de
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package iris.client_bff.users.web.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * UserInsert
 */
@JsonPropertyOrder({
		UserInsertDTO.JSON_PROPERTY_FIRST_NAME,
		UserInsertDTO.JSON_PROPERTY_LAST_NAME,
		UserInsertDTO.JSON_PROPERTY_USER_NAME,
		UserInsertDTO.JSON_PROPERTY_PASSWORD,
		UserInsertDTO.JSON_PROPERTY_ROLE
})
@JsonTypeName("UserInsert")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen",
		date = "2021-05-03T15:54:40.838481+02:00[Europe/Berlin]")
public class UserInsertDTO {
	public static final String JSON_PROPERTY_FIRST_NAME = "firstName";
	private String firstName;

	public static final String JSON_PROPERTY_LAST_NAME = "lastName";
	private String lastName;

	public static final String JSON_PROPERTY_USER_NAME = "userName";
	private String userName;

	public static final String JSON_PROPERTY_PASSWORD = "password";
	private String password;

	public static final String JSON_PROPERTY_ROLE = "role";
	private UserRoleDTO role;

	public UserInsertDTO firstName(String firstName) {

		this.firstName = firstName;
		return this;
	}

	/**
	 * Get firstName
	 * 
	 * @return firstName
	 **/
	@javax.annotation.Nullable
	@ApiModelProperty(value = "")
	@JsonProperty(JSON_PROPERTY_FIRST_NAME)
	@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public UserInsertDTO lastName(String lastName) {

		this.lastName = lastName;
		return this;
	}

	/**
	 * Get lastName
	 * 
	 * @return lastName
	 **/
	@javax.annotation.Nullable
	@ApiModelProperty(value = "")
	@JsonProperty(JSON_PROPERTY_LAST_NAME)
	@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public UserInsertDTO userName(String userName) {

		this.userName = userName;
		return this;
	}

	/**
	 * Get userName
	 * 
	 * @return userName
	 **/
	@ApiModelProperty(required = true, value = "")
	@JsonProperty(JSON_PROPERTY_USER_NAME)
	@JsonInclude(value = JsonInclude.Include.ALWAYS)

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public UserInsertDTO password(String password) {

		this.password = password;
		return this;
	}

	/**
	 * Get password
	 * 
	 * @return password
	 **/
	@ApiModelProperty(required = true, value = "")
	@JsonProperty(JSON_PROPERTY_PASSWORD)
	@JsonInclude(value = JsonInclude.Include.ALWAYS)

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserInsertDTO role(UserRoleDTO role) {

		this.role = role;
		return this;
	}

	/**
	 * Get role
	 * 
	 * @return role
	 **/
	@ApiModelProperty(required = true, value = "")
	@JsonProperty(JSON_PROPERTY_ROLE)
	@JsonInclude(value = JsonInclude.Include.ALWAYS)

	public UserRoleDTO getRole() {
		return role;
	}

	public void setRole(UserRoleDTO role) {
		this.role = role;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserInsertDTO userInsert = (UserInsertDTO) o;
		return Objects.equals(this.firstName, userInsert.firstName) &&
				Objects.equals(this.lastName, userInsert.lastName) &&
				Objects.equals(this.userName, userInsert.userName) &&
				Objects.equals(this.password, userInsert.password) &&
				Objects.equals(this.role, userInsert.role);
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName, userName, password, role);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class UserInsert {\n");
		sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
		sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
		sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
		sb.append("    password: ").append(toIndentedString(password)).append("\n");
		sb.append("    role: ").append(toIndentedString(role)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces (except the first line).
	 */
	private String toIndentedString(Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}

}
