package iris.client_bff.kir_tracing.web;

import iris.client_bff.core.validation.NoSignOfAttack;
import iris.client_bff.kir_tracing.KirTracingForm;
import iris.client_bff.kir_tracing.eps.KirTracingFormDto;
import iris.client_bff.kir_tracing.KirTracingService;
import iris.client_bff.kir_tracing.mapper.KirTracingFormDataMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kir-tracing")
@Validated
public class KirTracingController {

	private final KirTracingService service;
	private final KirTracingFormDataMapper mapper;

	@GetMapping()
	public Page<KirTracingFormDto> getKirTracingForms(
			@RequestParam(required = false) KirTracingForm.Status status,
			@RequestParam(required = false) @NoSignOfAttack String search,
			Pageable pageable) {
		if (StringUtils.isNotEmpty(search)) {
			return service.search(status, search, pageable).map(mapper::toDto);
		}
		if (status != null) {
			return service.findByStatus(status, pageable).map(mapper::toDto);
		}
		return service.findAllByPersonNotNull(pageable).map(mapper::toDto);
	}

	@GetMapping("/count/unsubmitted")
	public Integer countUnsubmittedKirTracingForms() {
		return service.countUnsubmittedKirTracingForms();
	}

	@GetMapping("/{formId}")
	public ResponseEntity<KirTracingFormDto> getDetails(@PathVariable KirTracingForm.KirTracingFormIdentifier formId) {
		return service.findById(formId)
				.map(mapper::toDto)
				.map(ResponseEntity::ok)
				.orElseGet(ResponseEntity.notFound()::build);
	}

	@PatchMapping("/{formId}")
	public ResponseEntity<KirTracingFormDto> updateKirTracingFormStatus(
			@PathVariable UUID formId, @RequestBody @Valid @NotNull KirTracingFormStatusUpdateDto updatedStatus) {

		try {
			var formDto = service.updateFormStatus(formId, updatedStatus);
			return ResponseEntity.ok(formDto);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

}
