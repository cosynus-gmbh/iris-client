package iris.client_bff.kir_tracing;

import iris.client_bff.core.alert.AlertService;
import iris.client_bff.core.database.HibernateSearcher;
import iris.client_bff.kir_tracing.IncomingKirConnection.IncomingKirConnectionIdentifier;
import iris.client_bff.kir_tracing.eps.KirTracingController.KirConnectionDto;
import iris.client_bff.kir_tracing.eps.KirTracingController.KirConnectionResultDto;
import iris.client_bff.kir_tracing.eps.KirTracingController.KirFormSubmissionResultDto;
import iris.client_bff.kir_tracing.mapper.KirTracingFormDataMapper;
import iris.client_bff.proxy.IRISAnnouncementException;
import iris.client_bff.proxy.ProxyServiceClient;
import iris.client_bff.vaccination_info.*;
import iris.client_bff.vaccination_info.eps.VaccinationInfoController;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
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

	private static final String[] FIELDS = { "password" };

	private final ProxyServiceClient proxyClient;
	private final IncomingKirConnectionRepository incomingConnections;
	private final KirTracingFormRepository tracingForms;

	private final KirTracingFormDataMapper mapper;

	private final PasswordEncoder passwordEncoder;

	private final Properties properties;
	private final AlertService alertService;
	private final HibernateSearcher searcher;
	private final EncryptedConnectionsService encryptedConnectionsService;

	public KirConnectionResultDto requestIncomingKirConnectionParameters(KirConnectionDto connectionData) {

		IncomingKirConnection connection = announceIncomingKirConnection();

		try {

			var tokens = new VaccinationInfoController.Tokens(
					connection.getAnnouncementToken(),
					connection.getId().toString());

			EncryptedConnectionsService.EncryptedDataDto encryptedData =
					encryptedConnectionsService.encryptAndCreateResult(tokens, connectionData.submitterPublicKey());

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
			proxyClient.abortAnnouncement(connection.get().getAnnouncementToken());
		} catch (IRISAnnouncementException e) {
			log.error(String.format("Unable to abort announcement %s", connection.get().getAnnouncementToken()));
		}
		incomingConnections.deleteById(connectionId);

		log.debug("Removing IncomingKirConnection was successful");
	}

	private IncomingKirConnection announceIncomingKirConnection() {

		log.trace("Creating IncomingKirConnection");

		String announcementToken;
		try {
			announcementToken = proxyClient.announce(Instant.now().plus(properties.getExpirationDuration()));
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
			removeIncomingConnection(incomingKirConnection.getId());
		});
		return connection.isPresent();
	}

	public KirFormSubmissionResultDto submitKirTracingForm(String password, KirTracingFormDto form) {

		KirTracingForm tracingForm = KirTracingForm.builder()
				.password(passwordEncoder.encode(password))
				.person(mapper.toEntity(form.getPerson()))
				.build();

		tracingForms.save(tracingForm);

		return new KirFormSubmissionResultDto(tracingForm.getAccessToken());
	}

	public KirFormSubmissionResultDto updateKirTracingForm(String password, String accessToken, KirTracingFormDto formDto) {

		KirTracingForm form = tracingForms.findByAccessToken(accessToken).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

		if (!passwordEncoder.matches(password, form.getPassword())) throw new IllegalArgumentException("Invalid credentials");

		return new KirFormSubmissionResultDto(accessToken);
	}

	public Page<KirTracingForm> search(String searchString, Pageable pageable) {

		var result = searcher.search(
				searchString,
				pageable,
				FIELDS,
				it -> it,
				KirTracingForm.class);

		return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
	}

	public Page<KirTracingForm> getAll(Pageable pageable) {
		return tracingForms.findAll(pageable);
	}

	@ConfigurationProperties("iris.client.kirtracing")
	@ConstructorBinding
	@Validated
	@Value
	static class Properties {

		/**
		 * Defines the {@link Duration} after that a vaccination info announcement will be expire.
		 */
		@NotNull
		private Duration expirationDuration;
	}
}
