package iris.client_bff.kir_tracing.web;

import iris.client_bff.core.validation.NoSignOfAttack;
import iris.client_bff.kir_tracing.KirTracingForm;
import iris.client_bff.kir_tracing.eps.KirTracingFormDto;
import iris.client_bff.kir_tracing.KirTracingService;
import iris.client_bff.kir_tracing.mapper.KirTracingFormDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;
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
			@RequestParam Optional<@NoSignOfAttack String> search,
			Pageable pageable) {

		var kirTracingForms = search
				// searching without person null check should be fine as null values are not indexed
				.map(it -> service.search(it, pageable))
				.orElseGet(() -> service.findAllByPersonNotNull(pageable));

		System.out.println(kirTracingForms.getContent());

		return kirTracingForms.map(mapper::toDto);
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
	public KirTracingFormDto updateKirTracingFormStatus(
			@PathVariable UUID formId, @RequestBody @Valid @NotNull KirTracingFormStatusUpdateDto updatedStatus) {

		return service.updateFormStatus(formId, updatedStatus);
	}

}
