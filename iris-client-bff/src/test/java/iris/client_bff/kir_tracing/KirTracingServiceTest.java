package iris.client_bff.kir_tracing;

import iris.client_bff.IrisWebIntegrationTest;
import iris.client_bff.proxy.EPSProxyServiceServiceClient;
import iris.client_bff.proxy.IRISAnnouncementException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@IrisWebIntegrationTest
@RequiredArgsConstructor
@Tag("kir-tracing")
@Tag("service")
@DisplayName("IT of kir tracing service")
class KirTracingServiceTest {

    final IncomingKirConnectionRepository incomingConnections;

    final KirTracingService service;

    @SpyBean
    final EPSProxyServiceServiceClient proxyClient;

    @Captor
    ArgumentCaptor<String> announcementCaptor;

    @Test
    void connectionIsValid() throws Throwable {

        UUID cat = UUID.randomUUID();

        IncomingKirConnection incomingKirConnection = IncomingKirConnection.of(cat.toString());

        UUID dat = incomingKirConnection.getId().toUuid();

        incomingConnections.save(incomingKirConnection);

        var count = incomingConnections.count();

        var result = service.validateConnection(dat);

        verify(proxyClient).abortAnnouncement(announcementCaptor.capture());
        assertThat(announcementCaptor.getValue().equals(cat.toString()));

        assertTrue(result);

        assertEquals(count - 1, incomingConnections.count());

    }

    @Test
    void catIsValidDatIsNot() {

        UUID cat = UUID.randomUUID();

        IncomingKirConnection incomingKirConnection = IncomingKirConnection.of(cat.toString());

        UUID dat = UUID.randomUUID();

        incomingConnections.save(incomingKirConnection);

        var count = incomingConnections.count();

        var result = service.validateConnection(dat);

        assertFalse(result);

        assertEquals(count, incomingConnections.count());

    }


}