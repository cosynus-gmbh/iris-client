package iris.client_bff.vaccination_info.eps;

import iris.client_bff.config.MapStructCentralConfig;
import iris.client_bff.vaccination_info.EncryptedConnectionsService;
import iris.client_bff.vaccination_info.VaccinationInfo;
import iris.client_bff.vaccination_info.VaccinationInfoAnnouncement.AnnouncementIdentifier;
import iris.client_bff.vaccination_info.VaccinationInfoService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Jens Kutzsche
 */
@Service
@RequiredArgsConstructor
@Slf4j
class VaccinationInfoControllerImpl implements VaccinationInfoController {

	private final VaccinationInfoService service;
	private final EncryptedConnectionsService encryptedConnectionsService;
	private final VaccinationInfoMapper vaccinationInfoMapper;


	@Override
	public AnnouncementResultDto announceVaccinationInfoList(AnnouncementDataDto announcementData) {

		log.debug("Start announce vaccination info list (JSON-RPC interface)");

		var vacInfo = service.announceVaccinationInfo(announcementData.externalId());

		log.trace("Finish announce vaccination info list (JSON-RPC interface)");

		var tokens = new Tokens(
				vacInfo.getAnnouncementToken(),
				vacInfo.getId().toString());

		try {
			EncryptedConnectionsService.EncryptedDataDto encryptedData =
					encryptedConnectionsService.encryptAndCreateResult(tokens, announcementData.submitterPublicKey());

			return (new AnnouncementResultDto(encryptedData.hdPublicKey(), encryptedData.iv(), encryptedData.tokens()));
		}  catch (Exception e)  {
			service.deleteAnnouncement(vacInfo.getId());
			throw e;
		}

	}

	@Override
	public String submitVaccinationInfoList(UUID dataAuthorizationToken, Facility facility, Set<Employee> employees) {

		log.debug("Start submit vaccination info list (JSON-RPC interface)");

		service.createVaccinationInfo(AnnouncementIdentifier.of(dataAuthorizationToken),
				vaccinationInfoMapper.fromFacilityDto(facility),
				vaccinationInfoMapper.fromEmployeeDtos(employees));

		log.trace("Finish submit vaccination info list (JSON-RPC interface)");

		return "OK";
	}

	@Mapper(config = MapStructCentralConfig.class)
	interface VaccinationInfoMapper {

		VaccinationInfo.Facility fromFacilityDto(Facility facilityDto);

		Set<VaccinationInfo.Employee> fromEmployeeDtos(Set<Employee> employees);

		VaccinationInfo.Employee fromEmployeeDto(Employee employee);
	}
}
