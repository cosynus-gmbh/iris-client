package iris.client_bff.kir_tracing;

import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.srp6.BigIntegerUtils;
import com.nimbusds.srp6.SRP6ClientCredentials;
import com.nimbusds.srp6.SRP6ServerSession;
import iris.client_bff.config.SrpParamsConfig;
import iris.client_bff.core.alert.AlertService;
import iris.client_bff.core.database.HibernateSearcher;
import iris.client_bff.kir_tracing.IncomingKirConnection.IncomingKirConnectionIdentifier;
import iris.client_bff.kir_tracing.eps.KirChallengeDto;
import iris.client_bff.kir_tracing.eps.KirTracingController;
import iris.client_bff.kir_tracing.eps.KirTracingController.KirAuthorizationResponseDto;
import iris.client_bff.kir_tracing.eps.KirTracingController.KirConnectionDto;
import iris.client_bff.kir_tracing.eps.KirTracingController.KirConnectionResultDto;
import iris.client_bff.kir_tracing.eps.KirTracingController.KirFormSubmissionResultDto;
import iris.client_bff.kir_tracing.eps.KirTracingFormDto;
import iris.client_bff.kir_tracing.mapper.KirTracingFormDataMapper;
import iris.client_bff.kir_tracing.web.KirTracingFormStatusUpdateDto;
import iris.client_bff.proxy.IRISAnnouncementException;
import iris.client_bff.proxy.ProxyServiceClient;
import iris.client_bff.vaccination_info.EncryptedConnectionsService;
import iris.client_bff.vaccination_info.eps.VaccinationInfoController;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Tim Lusa
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KirTracingService {

    private static final String[] FIELDS = {"assessment", "therapyResults", "person.mobilePhone"};

    private final ProxyServiceClient proxyClient;
    private final IncomingKirConnectionRepository incomingConnections;
    private final KirTracingFormRepository tracingForms;

    private final SrpParamsConfig srpParamsConfig;

    private final KirTracingFormDataMapper mapper;

    private final PasswordEncoder passwordEncoder;

    private final Properties properties;
    private final AlertService alertService;
    private final HibernateSearcher searcher;
    private final EncryptedConnectionsService encryptedConnectionsService;

    public KirConnectionResultDto requestIncomingKirConnectionParameters(KirConnectionDto connectionData) {

        IncomingKirConnection connection = announceIncomingKirConnection();

        try {

            var tokens = new VaccinationInfoController.Tokens(connection.getAnnouncementToken(), connection.getId()
                    .toString());

            EncryptedConnectionsService.EncryptedDataDto encryptedData = encryptedConnectionsService.encryptAndCreateResult(tokens, connectionData.submitterPublicKey());

            return (new KirConnectionResultDto(encryptedData.hdPublicKey(), encryptedData.iv(), encryptedData.tokens()));
        } catch (Exception e) {
            removeIncomingConnection(connection.getId());
            throw e;
        }

    }

    private void removeIncomingConnection(IncomingKirConnectionIdentifier connectionId) {

        log.trace("Removing IncomingKirConnection");

        Optional<IncomingKirConnection> connection = incomingConnections.findById(connectionId);
        if (connection.isEmpty()) return;
        try {
            proxyClient.abortAnnouncement(connection.get()
                    .getAnnouncementToken());
        } catch (IRISAnnouncementException e) {
            log.error(String.format("Unable to abort announcement %s", connection.get()
                    .getAnnouncementToken()));
        }
        incomingConnections.deleteById(connectionId);

        log.debug("Removing IncomingKirConnection was successful");
    }

    private IncomingKirConnection announceIncomingKirConnection() {

        log.trace("Creating IncomingKirConnection");

        String announcementToken;
        try {
            announcementToken = proxyClient.announce(Instant.now()
                    .plus(properties.getExpirationDuration()));
        } catch (IRISAnnouncementException e) {

            var msg = "Proxy announcement failed";
            log.error(msg + ": ", e);

            throw new IncomingKirConnectionAnnouncementException(msg, e);
        }

        var incomingKirConnection = IncomingKirConnection.of(announcementToken);

        incomingKirConnection = incomingConnections.save(incomingKirConnection);

        log.debug("Creating IncomingKirConnection was successful");

        return incomingKirConnection;
    }

    public Boolean validateConnection(UUID dat) {
        log.debug("Validatiing connection");
        Optional<IncomingKirConnection> connection = incomingConnections.findById(IncomingKirConnectionIdentifier.of(dat));
        log.debug(String.format("Connection is valid: %s", connection.isPresent()));
        connection.ifPresent(incomingKirConnection -> {
          //  removeIncomingConnection(incomingKirConnection.getId());
        });
        return connection.isPresent();
    }

    public KirFormSubmissionResultDto submitKirTracingForm(String srpSalt, String srpVerifier, String accessToken, KirTracingFormDto form) {

        KirTracingForm targetForm = tracingForms.findByAccessToken(accessToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        mapper.update(targetForm, form);
        targetForm.setSrpSalt(srpSalt);
        targetForm.setSrpVerifier(srpVerifier);

        log.info(targetForm.toString());

        tracingForms.save(targetForm);

        return new KirFormSubmissionResultDto(targetForm.getAccessToken());
    }

    public KirFormSubmissionResultDto updateKirTherapyResults(SRP6ClientCredentials credentials, String accessToken, JsonNode therapyResults) {

        KirTracingForm form = authorize(credentials, accessToken).form;
        KirTracingFormDto updatedForm = KirTracingFormDto.builder()
                .therapyResults(therapyResults)
                .build();
        form = mapper.update(form, updatedForm);
        form.setSrpSession(null);
        form.setStatus(KirTracingForm.Status.THERAPY_RESULTS_RECEIVED);
        tracingForms.save(form);

        return new KirFormSubmissionResultDto(accessToken);
    }

    public KirAuthorizationResponseDto authorizeKir(SRP6ClientCredentials credentials, String accessToken) {
        return new KirAuthorizationResponseDto(authorize(credentials, accessToken).M2);
    }

    private AuthorizationResult authorize(SRP6ClientCredentials credentials, String accessToken) {
        KirTracingForm form = tracingForms.findByAccessToken(accessToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        SRP6ServerSession srpValidator = form.getSrpServerSession();
        if (srpValidator == null) throw new IllegalArgumentException("No session available");
        try {
            BigInteger M2 = srpValidator.step2(credentials.A, credentials.M1);

            return new AuthorizationResult(form, BigIntegerUtils.toHex(M2));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public Page<KirTracingForm> search(String searchString, Pageable pageable) {

        var result = searcher.search(searchString, pageable, FIELDS, it -> it, KirTracingForm.class);

        return new PageImpl<>(result.hits(), pageable, result.total()
                .hitCount());
    }

    public Optional<KirTracingForm> findById(KirTracingForm.KirTracingFormIdentifier formId) {
        return tracingForms.findById(formId);
    }

    public Page<KirTracingForm> getAll(Pageable pageable) {
        return tracingForms.findAll(pageable);
    }

    public Page<KirTracingForm> findAllByPersonNotNull(Pageable pageable) {
        return tracingForms.findAllByPersonNotNull(pageable);

    }

    public Integer countUnsubmittedKirTracingForms() {
        return tracingForms.countAllByPersonNull();
    }

    public KirFormSubmissionResultDto generateAccessToken() {
        KirTracingForm tracingForm = KirTracingForm.builder()
                .build();

        tracingForms.save(tracingForm);

        return new KirFormSubmissionResultDto(tracingForm.getAccessToken());
    }

    public KirChallengeDto createChallenge(String accessToken) {

        KirTracingForm form = tracingForms.findByAccessToken(accessToken)
                .orElseThrow(() -> new InvalidParameterException("Invalid access token"));

        if (form.getSrpSalt()
                .isEmpty() || form.getSrpVerifier()
                .isEmpty()) throw new InvalidParameterException("Invalid credentials");

        SRP6ServerSession serverSession = new SRP6ServerSession(srpParamsConfig.getConfig());

        BigInteger challenge = serverSession.step1(form.getAccessToken(), BigIntegerUtils.fromHex(form.getSrpSalt()), BigIntegerUtils.fromHex(form.getSrpVerifier()));

        form.setSrpSession(serverSession);

        tracingForms.save(form);

        return KirChallengeDto.builder()
                .challenge(BigIntegerUtils.toHex(challenge))
                .salt(form.getSrpSalt())
                .build();
    }

    public KirTracingFormDto updateFormStatus(UUID formId, KirTracingFormStatusUpdateDto updatedStatus) {

        KirTracingForm form = tracingForms.findById(KirTracingForm.KirTracingFormIdentifier.of(formId))
                .orElseThrow(EntityNotFoundException::new);

        form.setStatus(updatedStatus.getStatus());
        tracingForms.save(form);

        return mapper.toDto(form);

    }

    record AuthorizationResult(KirTracingForm form, String M2) {
    }

    @ConfigurationProperties("iris.client.kirtracing")
    @ConstructorBinding
    @Validated
    @Data
    static class Properties {

        /**
         * Defines the {@link Duration} after that a vaccination info announcement will be expire.
         */
        @NotNull
        private Duration expirationDuration;
    }
}
