package iris.client_bff.kir_tracing;

import iris.client_bff.core.model.AggregateRepository;
import iris.client_bff.kir_tracing.IncomingKirConnection.IncomingKirConnectionIdentifier;
import iris.client_bff.vaccination_info.VaccinationInfo;
import iris.client_bff.vaccination_info.VaccinationInfo.VaccinationInfoIdentifier;

/**
 * @author Tim Lusa
 */
public interface IncomingKirConnectionRepository extends AggregateRepository<IncomingKirConnection, IncomingKirConnectionIdentifier> {

}
