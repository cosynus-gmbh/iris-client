package iris.client_bff.kir_tracing;

import iris.client_bff.core.model.Aggregate;
import iris.client_bff.core.model.IdWithUuid;
import lombok.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serial;
import java.util.UUID;

@Entity
@Table(name = "kir_tracing_aid_request")
@Indexed
@Data
@EqualsAndHashCode(callSuper = true, exclude = "form")
@NoArgsConstructor
public class KirTracingAidRequest extends Aggregate<KirTracingAidRequest, KirTracingAidRequest.KirTracingAidRequestIdentifier> {

    {
        id = KirTracingAidRequestIdentifier.random();
    }

    @FullTextField
    private String data;

    @ManyToOne
    @JoinColumn(name = "form_id")
    private KirTracingForm form;

    @EqualsAndHashCode(callSuper = false)
    @RequiredArgsConstructor(staticName = "of")
    @NoArgsConstructor(force = true, access = AccessLevel.PRIVATE) // for JPA
    public static class KirTracingAidRequestIdentifier extends IdWithUuid {

        @Serial
        private static final long serialVersionUID = 5225891072421600826L;

        final UUID id;

        static KirTracingAidRequestIdentifier random() {
            return of(UUID.randomUUID());
        }

        @Override
        protected UUID getBasicId() {
            return id;
        }
    }
}
