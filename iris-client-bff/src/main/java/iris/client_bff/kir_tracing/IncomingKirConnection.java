package iris.client_bff.kir_tracing;

import iris.client_bff.core.model.Aggregate;
import iris.client_bff.core.model.IdWithUuid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import javax.persistence.Entity;

/**
 * @author Tim Lusa
 */
@Entity
@Data
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true, of = {})
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IncomingKirConnection extends Aggregate<IncomingKirConnection, IncomingKirConnection.IncomingKirConnectionIdentifier> {

    {
        id = IncomingKirConnectionIdentifier.random();
    }

    private String announcementToken;

    @EqualsAndHashCode(callSuper = false)
    @RequiredArgsConstructor(staticName = "of")
    @NoArgsConstructor(force = true, access = AccessLevel.PRIVATE) // for JPA
    public static class IncomingKirConnectionIdentifier extends IdWithUuid {

        private static final long serialVersionUID = 6173839403279494401L;

        final UUID id;

        static IncomingKirConnectionIdentifier random() {
            return of(UUID.randomUUID());
        }

        @Override
        protected UUID getBasicId() {
            return id;
        }
    }
}
