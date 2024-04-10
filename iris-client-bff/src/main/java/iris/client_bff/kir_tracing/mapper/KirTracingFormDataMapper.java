package iris.client_bff.kir_tracing.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import iris.client_bff.config.MapStructCentralConfig;

import iris.client_bff.kir_tracing.KirBiohazardEvent;
import iris.client_bff.kir_tracing.KirTracingAidRequest;
import iris.client_bff.kir_tracing.KirTracingForm;
import iris.client_bff.kir_tracing.KirTracingMessage;
import iris.client_bff.kir_tracing.eps.KirBiohazardEventDto;
import iris.client_bff.kir_tracing.eps.KirTracingFormDto;
import iris.client_bff.kir_tracing.web.KirBiohazardEventUpdateDto;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(config = MapStructCentralConfig.class, imports = UUID.class)
public interface KirTracingFormDataMapper {

    KirTracingForm toEntity(KirTracingFormDto formDto);

    KirTracingForm.Person toEntity(KirTracingFormDto.PersonDto personDto);

    KirTracingMessage toEntity(KirTracingFormDto.MessageDto messageDto);

    KirTracingAidRequest toEntity(KirTracingFormDto.AidRequestDto aidRequestDto);

    KirTracingFormDto toDto(KirTracingForm form);

    KirBiohazardEventDto.LocationDto toDto(KirBiohazardEvent.Location location);

    KirBiohazardEventDto toDto(KirBiohazardEvent event);

    KirBiohazardEvent toEntity(KirBiohazardEventUpdateDto dto);

    KirBiohazardEvent.Location toEntity(KirBiohazardEventUpdateDto.LocationDto dto);

    default JsonNode map(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString, JsonNode.class);
        } catch (Exception e) {
            return null;
        }
    }

    default String map(JsonNode json) {
      return json.toString();
    }

    @Mapping(target = "event", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE)
    KirTracingForm update(@MappingTarget KirTracingForm form, KirTracingFormDto formDto);

    @Mapping(target = "tracingForms", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE)
    KirBiohazardEvent update(@MappingTarget KirBiohazardEvent event, KirBiohazardEventUpdateDto updateDto);

}
