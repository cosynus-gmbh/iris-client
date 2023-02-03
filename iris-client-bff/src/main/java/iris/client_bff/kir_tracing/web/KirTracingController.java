package iris.client_bff.kir_tracing.web;

import iris.client_bff.core.validation.NoSignOfAttack;
import iris.client_bff.kir_tracing.eps.KirTracingFormDto;
import iris.client_bff.kir_tracing.KirTracingService;
import iris.client_bff.kir_tracing.mapper.KirTracingFormDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.websocket.server.PathParam;
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

		var newPageable = adaptPageable(pageable);

		var kirTracingForms = search
				.map(it -> service.search(it, pageable))
				.orElseGet(() -> service.getAll(newPageable));

		System.out.println(kirTracingForms.getContent());

		return kirTracingForms.map(mapper::toDto);
	}

	@PatchMapping("/{formId}")
	public KirTracingFormDto updateKirTracingFormStatus(
			@PathVariable UUID formId, @RequestBody @Valid @NotNull KirTracingFormStatusUpdateDto updatedStatus) {

		return service.updateFormStatus(formId, updatedStatus);
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
