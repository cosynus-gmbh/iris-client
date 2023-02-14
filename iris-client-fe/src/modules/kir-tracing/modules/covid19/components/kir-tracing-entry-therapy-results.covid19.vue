<template>
  <v-row>
    <v-col cols="12" md="6">
      <v-row>
        <v-col cols="12">
          <h3 class="mb-3">Ärztliche Behandlung</h3>
          <info-list :content="medicalCare" />
          <v-divider class="mt-6" />
        </v-col>
      </v-row>
    </v-col>
  </v-row>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { KirTracingDisease, KirTracingEntry } from "@/api";
import kirTracingConstants from "@/modules/kir-tracing/services/constants";
import { PropType } from "vue";
import BtnToggleSelect from "@/components/btn-toggle-select.vue";
import InfoList from "@/components/info-list.vue";

const KirTracingEntryTherapyResultsCovid19Props = Vue.extend({
  props: {
    tracingEntry: {
      type: Object as PropType<
        KirTracingEntry<KirTracingDisease.COVID_19> | undefined
      >,
      default: null,
    },
  },
});
@Component({
  components: { InfoList, BtnToggleSelect },
})
export default class KirTracingEntryTherapyResultsCovid19 extends KirTracingEntryTherapyResultsCovid19Props {
  get medicalCare() {
    const data = this.tracingEntry?.therapyResults?.medicalCare;
    console.log("data", data);
    return [
      [
        [
          "Patient war in ärztlicher Behandlung",
          kirTracingConstants.valueLabel(data?.treatment),
        ],
        [
          "Behandlungsort",
          (data?.treatment_location ?? []).map((item) =>
            kirTracingConstants.valueLabel(item, "treatment_location")
          ),
        ],
      ],
    ];
  }
}
</script>
