<template>
  <v-row>
    <v-col cols="12" md="6">
      <v-row>
        <v-col cols="12">
          <h3 class="mb-3">Ärztliche Behandlung</h3>
          <info-list :content="medicalCare" />
          <template v-if="hasHospitalInfo">
            <h4 class="mb-3 mt-6 text-decoration-underline">
              Krankenhausaufenthalt
            </h4>
            <info-list :content="medicalCare_hospital" />
          </template>
          <v-divider class="mt-6" />
        </v-col>
        <v-col cols="12">
          <h3 class="mb-3">Medikation</h3>
          <info-list :content="paxlovid" />
          <v-divider class="mt-6" />
        </v-col>
      </v-row>
    </v-col>
    <v-col cols="12" md="6">
      <v-row>
        <v-col cols="12">
          <h3 class="mb-3">Feedback</h3>
          <info-list :content="feedback" />
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
import { getFormattedDate } from "@/utils/date";

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
    return [
      [
        "Patient:in war in ärztlicher Behandlung",
        kirTracingConstants.valueLabel(data?.treatment),
      ],
      [
        "Behandlungsort",
        (data?.treatment_location ?? []).map((item) =>
          kirTracingConstants.valueLabel(item, "treatment_location")
        ),
      ],
    ];
  }
  get medicalCare_hospital() {
    const data = this.tracingEntry?.therapyResults?.medicalCare;
    return [
      [
        "Stationärer Aufenthalt",
        kirTracingConstants.valueLabel(data?.hospital_inpatient),
      ],
      [
        "Stationäre Aufenthaltsdauer",
        data?.hospital_inpatient_duration
          ? kirTracingConstants.valueLabel(data?.hospital_inpatient_duration) +
            " Tage"
          : null,
      ],
      [
        "Aufenthalt auf der Intensivstation",
        kirTracingConstants.valueLabel(data?.hospital_icu),
      ],
      [
        "Sauerstoff erhalten",
        kirTracingConstants.valueLabel(data?.hospital_oxygen),
      ],
    ];
  }
  get hasHospitalInfo() {
    const data = this.tracingEntry?.therapyResults?.medicalCare;
    const values = [
      data?.hospital_inpatient,
      data?.hospital_inpatient_duration,
      data?.hospital_icu,
      data?.hospital_oxygen,
    ].filter((v) => v);
    return values.length > 0;
  }
  get paxlovid() {
    const data = this.tracingEntry?.therapyResults?.paxlovid;
    return [
      [
        "Paxlovid eingenommen",
        kirTracingConstants.valueLabel(data?.medication),
      ],
      [
        "Beginn der Einname von Paxlovid",
        getFormattedDate(data?.medication_startDate, "LL", ""),
      ],
      [
        "Verschreibung erhalten von",
        [
          kirTracingConstants.valueLabel(data?.prescription),
          data?.prescription_others,
        ],
      ],
      ["Ende der Symptome", getFormattedDate(data?.symptoms_endDate, "LL", "")],
      [
        "Gründe gegen die Einnahme",
        [
          kirTracingConstants.valueLabel(data?.noMedication, "noMedication"),
          data?.noMedication_others,
        ],
      ],
    ];
  }
  get feedback() {
    const data = this.tracingEntry?.therapyResults?.feedback;
    return [
      [
        "Mit ärztlicher Betreuung im Hinblick auf COVID-19 zufrieden",
        kirTracingConstants.valueLabel(data?.satisfiedWithMedicalCare),
      ],
      [
        "Patient:in hat sich durch das Gesundheitsamt Frankfurt gut betreut gefühlt",
        kirTracingConstants.valueLabel(data?.satisfiedWithHealthDepartment),
      ],
      [
        "Patient:in hat sich durch die KIR-App besser informiert gefühlt",
        kirTracingConstants.valueLabel(data?.betterInformedByApp),
      ],
      [
        "Verbesserungsvorschläge oder anderes Feedback",
        data?.suggestionsOrFeedback,
      ],
    ];
  }
}
</script>
