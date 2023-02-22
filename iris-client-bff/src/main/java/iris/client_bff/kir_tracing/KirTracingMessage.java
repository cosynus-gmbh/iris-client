package iris.client_bff.kir_tracing;

import iris.client_bff.core.model.Aggregate;
import iris.client_bff.core.model.IdWithUuid;
import lombok.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "kir_tracing_message")
@Indexed
@Data
@EqualsAndHashCode(callSuper = true, exclude = "form")
@NoArgsConstructor
public class KirTracingMessage extends Aggregate<KirTracingMessage, KirTracingMessage.KirTracingMessageIdentifier> {

    {
        id = KirTracingMessageIdentifier.random();
    }

    @FullTextField
    private String text;

    @ManyToOne
    @JoinColumn(name = "form_id")
    private KirTracingForm form;

    @EqualsAndHashCode(callSuper = false)
    @RequiredArgsConstructor(staticName = "of")
    @NoArgsConstructor(force = true, access = AccessLevel.PRIVATE) // for JPA
    public static class KirTracingMessageIdentifier extends IdWithUuid {

        private static final long serialVersionUID = 6173839403279494401L;

        final UUID id;

        static KirTracingMessageIdentifier random() {
            return of(UUID.randomUUID());
        }

        @Override
        protected UUID getBasicId() {
            return id;
        }
    }
}
