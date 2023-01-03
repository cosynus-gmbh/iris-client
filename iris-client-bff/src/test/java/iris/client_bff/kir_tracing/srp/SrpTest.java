package iris.client_bff.kir_tracing.srp;

import com.bitbucket.thinbus.srp6.js.*;
import com.nimbusds.srp6.SRP6ClientCredentials;
import iris.client_bff.IrisWebIntegrationTest;
import iris.client_bff.config.SrpParamsConfig;
import iris.client_bff.kir_tracing.ObjectSerializationHelper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


import static com.nimbusds.srp6.BigIntegerUtils.toHex;
import static org.junit.Assert.assertEquals;

@IrisWebIntegrationTest
@RequiredArgsConstructor
@Tag("kir-tracing")
@Tag("srp")
@DisplayName("IT of srp")
class SrpTest {

    final private SrpParamsConfig config;

    final static String username = "tom@arcot.com";
    final static String password = "password1234";


    @Test
    void testSrp() throws Exception {

        // ---------------------------------------------------
        //
        // Client Registration. Note logic in this section can
        // be done entirely without any server.
        //

        // We have a client
        final SRP6JavaClientSessionSHA256 client = new SRP6JavaClientSessionSHA256(
                config.getNBase10(), config.getGBase10());

        // Once and only once a random salt needs to be generated.
        final String salt = client
                .generateRandomSalt(SRP6JavascriptServerSessionSHA256.HASH_BYTE_LENGTH);

        // The client needs to generate a verifier based on N, g, salt and H (hash).
        final HexHashedVerifierGenerator generator = new HexHashedVerifierGenerator(
                config.getNBase10(), config.getGBase10(), SRP6JavascriptServerSessionSHA256.SHA_256);

        // The client cooks the verifier. This must be securely registered with the server.
        // Note: The verifier includes the hash of the username. So if the user changes either
        // their password or their username they must generate a new verifier and securely
        // register it with the server.
        final String verifier = generator.generateVerifier(salt, username, password);

        // ---------------------------------------------------
        //
        // Client Authentication
        //

        // The server
        SRP6JavascriptServerSession server = new SRP6JavascriptServerSessionSHA256(
                config.getNBase10(), config.getGBase10());


        // The client is initialised with username and password
        client.step1(username, password);

        // The server generates a public challenge B based on the username, salt and verifier.
        // Note: The verifier should have been kept secret.
        String B = server.step1(username, salt, verifier);

        String srpServiceSerialized = ObjectSerializationHelper.toString(server);

        server = (SRP6JavascriptServerSessionSHA256) ObjectSerializationHelper.fromString(srpServiceSerialized);

        // The server sends the public challenge B to the client.
        // The server can can also send the salt as it is public in the protocol.
        // The client computes a proof-of-password which is the credential.
        SRP6ClientCredentials credentials = client.step2(salt, B);

        // The server uses the client public value A and client proof M1
        // Note: this method will throw if the client proof M1 is bad.
        String M2 = server.step2(toHex(credentials.A), toHex(credentials.M1));

        // Success! The last step did not throw so the client knows the password that
        // created the verifier!
        System.out.println("The server has verified the client knows the password that matches the verifier!");

        // ---------------------------------------------------
        //
        // Server Authentication. Note in Java-2-Java this is a very good idea.
        // It doesn't make much sense in a browser JavaScript-2-Java scenario
        // when the server send the JavaScript to the client.
        //

        // The server can send the M2 to the client to check.
        // Note this will throw an exception if the server does not know the true verifier.
        client.step3(M2);

        // Success! The last step did not throw so the client knows that the server knows the verifier.
        System.out.println("The client has verified the server knows the verifier!");

        // ---------------------------------------------------
        //
        // Follow-On Cryptography. A side effect of the password proof is that the client
        // and server share a secret key 'k'. This can be used to sign the payloads of
        // both the client and the server using something like HMAC. That way if there is
        // some man-in-the-middle they cannot inject fake traffic into the conversation.
        //

        // Now both share a strong session key.
        String cS = client.getSessionKey(false);
        String sS = server.getSessionKey(false);
        assertEquals(cS, sS);

        // The hash value may be more useful as a secret key.
        String cK = client.getSessionKey(true);
        String sK = server.getSessionKey(true);
        assertEquals(cK, sK);

        System.out.printf("We have a shared session key we can use! %s%n", sK);
    }
}
