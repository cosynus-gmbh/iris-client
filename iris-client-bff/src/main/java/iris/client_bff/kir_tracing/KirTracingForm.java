package iris.client_bff.kir_tracing;

import com.nimbusds.srp6.SRP6ServerSession;
import iris.client_bff.core.model.Aggregate;
import iris.client_bff.core.model.IdWithUuid;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Tim Lusa
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true, of = {})
@Builder
@ToString
@Indexed
@AllArgsConstructor
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KirTracingForm extends Aggregate<KirTracingForm, KirTracingForm.KirTracingFormIdentifier> {

    {
        id = KirTracingFormIdentifier.random();
    }

    private String srpSalt;

    private String srpVerifier;

    private String srpSession;

    @Builder.Default
    @Column(unique = true)
    private String accessToken = RandomStringUtils.random(10, "23456789ABCDEFGHKLMNPQRSTUVWXYZ")
            .toUpperCase();

    @IndexedEmbedded
    private Person person;

    @Column(columnDefinition = "DECIMAL(30,10)")
    @GenericField(sortable = Sortable.YES, searchable = Searchable.YES)
    private Double riskFactor;

    @FullTextField
    private String assessment;

    @OrderBy("metadata.created DESC")
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<KirTracingAidRequest> aidRequests = new ArrayList<>();

    @OrderBy("metadata.created DESC")
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<KirTracingMessage> messages = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @IndexedEmbedded(includeEmbeddedObjectId = true)
    private KirBiohazardEvent event;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @GenericField(sortable = Sortable.YES)
    @Builder.Default
    private Status status = Status.NEW;

    @Column
    @Builder.Default
    @GenericField(sortable = Sortable.YES, searchable = Searchable.YES)
    private Instant deletedAt = null;

    public KirTracingForm markDeleted() {
        setDeletedAt(Instant.now());
        setSrpSalt(null);
        setSrpVerifier(null);
        setSrpSession(null);
        // maybe clear accessToken as well?
        setAssessment("");
        setPerson(Person.builder().mobilePhone(getId().toString()).build());
        return this;
    }

    @EqualsAndHashCode(callSuper = false)
    @RequiredArgsConstructor(staticName = "of")
    @NoArgsConstructor(force = true, access = AccessLevel.PRIVATE) // for JPA
    public static class KirTracingFormIdentifier extends IdWithUuid {

        private static final long serialVersionUID = 6173839403279494401L;

        final UUID id;

        static KirTracingFormIdentifier random() {
            return of(UUID.randomUUID());
        }

        @Override
        protected UUID getBasicId() {
            return id;
        }
    }

    @SneakyThrows
    public void setSrpSession(SRP6ServerSession session) {
        this.srpSession = ObjectSerializationHelper.toString(session);
    }

    public SRP6ServerSession getSrpServerSession() {
        if (this.srpSession.isBlank()) return null;
        try {
            return (SRP6ServerSession) ObjectSerializationHelper.fromString(this.srpSession);
        } catch (Exception e) {
            return null;
        }
    }

    @Embeddable
    @Data
    @Setter(AccessLevel.PACKAGE)
    @Builder
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Person {

        @Column(name = "person_mobile_phone")
        @GenericField(sortable = Sortable.YES)
        private String mobilePhone;

    }

    public enum Status {
        NEW, PERSON_CONTACTED, DATA_CHANGED, AID_REQUEST_RECEIVED, MESSAGE_RECEIVED, CLOSED
    }
}
