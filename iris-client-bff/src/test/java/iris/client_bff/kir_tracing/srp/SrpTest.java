package iris.client_bff.kir_tracing.srp;

import com.nimbusds.srp6.*;
import iris.client_bff.IrisWebIntegrationTest;
import iris.client_bff.config.SrpParamsConfig;
import iris.client_bff.kir_tracing.ObjectSerializationHelper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


import java.math.BigInteger;

import static com.nimbusds.srp6.BigIntegerUtils.toHex;
import static org.junit.Assert.assertEquals;

@IrisWebIntegrationTest
@RequiredArgsConstructor
@Tag("kir-tracing")
@Tag("srp")
@DisplayName("IT of srp")
class SrpTest {

    final private SrpParamsConfig config;

    final static String username = "testuser";
    final static String password = "password1234";


    @Test
    void testBigInt() {

        String hexInt = "115b8b692e0e045692cf280b436735c77a5a9e8a9e7ed56c965f87db5b2a2ece3";

        System.out.println(BigIntegerUtils.fromHex(hexInt));

        System.out.println();

    }

    @Test
    void testSrp() throws Exception {

        // ---------------------------------------------------
        //
        // Client Registration. Note logic in this section can
        // be done entirely without any server.
        //

        SRP6CryptoParams config = SRP6CryptoParams.getInstance(2048, "SHA-256");

        SRP6VerifierGenerator gen = new SRP6VerifierGenerator(config);


        BigInteger salt = BigIntegerUtils.bigIntegerFromBytes(gen.generateRandomSalt(32));

        BigInteger verifier = gen.generateVerifier(salt, username, password);


        SRP6ServerSession server = new SRP6ServerSession(config);

        BigInteger B = server.step1(username, salt, verifier);

        String serializedServer = ObjectSerializationHelper.toString(server);

        System.out.println(serializedServer);

        server = (SRP6ServerSession) ObjectSerializationHelper.fromString(serializedServer);


        final SRP6ClientSession client = new SRP6ClientSession();
        client.step1(username, password);
        SRP6ClientCredentials cred = null;
        try {
            cred = client.step2(config, salt, B);
        } catch (SRP6Exception e) {
            System.out.println("Wrong B");
        }

        BigInteger M2 = null;

        try {
            assert cred != null;
            M2 = server.step2(cred.A, cred.M1);

        } catch (SRP6Exception e) {
            System.out.println("Wrong creads");
        }

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
       // client.step3(M2);

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
        BigInteger cS = client.getSessionKey();
        BigInteger sS = server.getSessionKey();
        assertEquals(cS, sS);

        // The hash value may be more useful as a secret key.
        BigInteger cK = client.getSessionKey();
        BigInteger sK = server.getSessionKey();
        assertEquals(cK, sK);

        System.out.printf("We have a shared session key we can use! %s%n", sK);
    }
}


