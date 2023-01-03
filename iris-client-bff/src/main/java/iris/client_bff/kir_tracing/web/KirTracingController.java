package iris.client_bff.kir_tracing.web;

import iris.client_bff.config.MapStructCentralConfig;
import iris.client_bff.core.validation.NoSignOfAttack;
import iris.client_bff.kir_tracing.KirTracingForm;
import iris.client_bff.kir_tracing.KirTracingFormDto;
import iris.client_bff.kir_tracing.KirTracingService;
import iris.client_bff.kir_tracing.eps.KirTracingController.KirConnectionDto;
import iris.client_bff.kir_tracing.mapper.KirTracingFormDataMapper;
import iris.client_bff.vaccination_info.EncryptedConnectionsService;
import iris.client_bff.vaccination_info.VaccinationInfo;
import iris.client_bff.vaccination_info.VaccinationInfo.Employee;
import iris.client_bff.vaccination_info.VaccinationInfo.VaccinationInfoIdentifier;
import iris.client_bff.vaccination_info.VaccinationInfoService;
import iris.client_bff.vaccination_info.VaccinationStatus;
import iris.client_bff.vaccination_info.web.VaccinationInfoDto;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kir-tracing")
@Validated
public class KirTracingController {

	private final KirTracingService service;
	private final KirTracingFormDataMapper mapper;

	@GetMapping()
	public Page<KirTracingFormDto> getKirTracingForms(
			@RequestParam Optional<@NoSignOfAttack String> search,
			Pageable pageable) {

		var newPageable = adaptPageable(pageable);

		var kirTracingForms = search
				.map(it -> service.search(it, pageable))
				.orElseGet(() -> service.getAll(newPageable));

		return kirTracingForms.map(mapper::toDto);
	}

	private PageRequest adaptPageable(Pageable pageable) {

		var sort = pageable.getSort();
		var orders = sort.map(it -> {
			if ("reportedAt".equals(it.getProperty())) {
				return it.withProperty("metadata.created");
			}

			return it;
		}).toList();

		sort = Sort.by(orders);

		return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
	}
}
