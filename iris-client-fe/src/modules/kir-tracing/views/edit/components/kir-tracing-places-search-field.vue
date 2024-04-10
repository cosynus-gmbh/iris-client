<template>
  <v-autocomplete
    label="Ort"
    :items="fetchPlaces.state.result"
    item-value="location_id"
    :item-text="itemText"
    v-model="model"
    :rules="validationRules"
    :clearable="true"
  >
    <template #item="{ item }">
      <v-row align="center">
        <v-col> {{ itemText(item) }}&nbsp; </v-col>
        <v-col cols="auto">
          <span>
            <small>lat: {{ item.latitude }}</small>
            <br />
            <small>lon: {{ item.longitude }}</small>
          </span>
        </v-col>
      </v-row>
    </template>
  </v-autocomplete>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { kirTracingApi } from "@/modules/kir-tracing/services/api";
import { KirTracingBiohazardEventLocation, KirTracingPlace } from "@/api";
import { PropType } from "vue";
import { join } from "@/utils/misc";
import rules, { ValidationRule } from "@/common/validation-rules";
import SortableDataTable from "@/components/sortable-data-table.vue";

const KirTracingPlacesSearchFieldProps = Vue.extend({
  inheritAttrs: false,
  props: {
    value: {
      type: Object as PropType<
        Partial<KirTracingBiohazardEventLocation> | undefined
      >,
      default: undefined,
    },
    rules: {
      type: Array as PropType<ValidationRule[]>,
      default: () => [],
    },
  },
});

@Component({
  components: { SortableDataTable },
})
export default class KirTracingPlacesSearchField extends KirTracingPlacesSearchFieldProps {
  active = false;

  mounted() {
    this.fetchPlaces.execute();
  }

  fetchPlaces = kirTracingApi.fetchPlaces();

  itemText(item: KirTracingPlace): string {
    return join([item.postcode, item.city], " ");
  }

  set model(value: string | undefined) {
    this.$emit("input", this.getLocation(value));
  }

  get model(): string | undefined {
    return this.getPlace(this.value)?.location_id;
  }

  getLocation(
    value: KirTracingPlace | string | undefined
  ): Partial<KirTracingBiohazardEventLocation> | undefined {
    if (!value) return;
    const place = typeof value === "string" ? this.getPlace(value) : value;
    if (!place) return;
    const { postcode, city, latitude, longitude, location_id } = place;
    return {
      id: location_id,
      postcode,
      city,
      latitude,
      longitude,
    };
  }

  getPlace(
    location: Partial<KirTracingBiohazardEventLocation> | string | undefined
  ): KirTracingPlace | undefined {
    if (!location) return undefined;
    return this.fetchPlaces.state.result?.find((place) => {
      if (typeof location === "string") {
        return place.location_id == location;
      }
      const { id, latitude, longitude } = location;
      if (id && place.location_id == id) return true;
      if (!latitude || !longitude) return false;
      return (
        `${place.latitude}` == `${latitude}` &&
        `${place.longitude}` == `${longitude}`
      );
    });
  }

  get validationRules(): ValidationRule[] {
    return [
      ...(this.rules ?? []),
      (v: string | undefined) => rules.geoLocation(this.getPlace(v)),
    ];
  }
}
</script>
