package iris.client_bff.kir_tracing;

import com.nimbusds.srp6.SRP6ServerSession;
import iris.client_bff.core.model.Aggregate;
import iris.client_bff.core.model.IdWithUuid;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import javax.persistence.*;
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
    private String accessToken = RandomStringUtils.randomAlphanumeric(10)
            .toUpperCase();

    @IndexedEmbedded
    private Person person;

    private String assessment;

    private String therapyResults;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @GenericField(sortable = Sortable.YES)
    @Builder.Default
    private Status status = Status.NEW;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @GenericField(sortable = Sortable.YES)
    @Builder.Default
    private Disease targetDisease = Disease.COVID_19;

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
        NEW, PERSON_CONTACTED, DATA_CHANGED, THERAPY_END, DONE
    }

    public enum Disease {
        COVID_19
    }
}
