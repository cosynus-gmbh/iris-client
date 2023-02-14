<template>
  <v-row>
    <v-col cols="12" md="6">
      <v-row>
        <v-col cols="12">
          <h3 class="mb-3">Virusnachweis</h3>
          <info-list :content="virusDetection" />
          <v-divider class="mt-6" />
        </v-col>
        <v-col cols="12">
          <h3 class="mb-3">Immunstatus</h3>
          <info-list :content="immuneStatus" />
          <v-divider class="mt-6" />
        </v-col>
        <v-col cols="12">
          <h3 class="mb-3">Symptomatik</h3>
          <info-list :content="symptoms" />
          <v-divider class="mt-6" />
        </v-col>
      </v-row>
    </v-col>
    <v-col cols="12" md="6">
      <v-row>
        <v-col cols="12">
          <h3 class="mb-3">Risikofaktoren</h3>
          <info-list :content="risk" />
          <v-divider class="mt-6" />
        </v-col>
        <v-col cols="12">
          <h3 class="mb-3">Ärztliche Betreuung</h3>
          <info-list :content="medicalCare" />
          <v-divider class="mt-6" />
        </v-col>
      </v-row>
    </v-col>
  </v-row>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { getFormattedDate, getRelativeTime } from "@/utils/date";
import { KirTracingDisease, KirTracingEntry } from "@/api";
import kirTracingConstants from "@/modules/kir-tracing/services/constants";
import { isTrue } from "@/utils/misc";
import { PropType } from "vue";
import BtnToggleSelect from "@/components/btn-toggle-select.vue";
import InfoList from "@/components/info-list.vue";

const KirTracingEntryAssessmentCovid19Props = Vue.extend({
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
export default class KirTracingEntryAssessmentCovid19 extends KirTracingEntryAssessmentCovid19Props {
  get virusDetection() {
    const data = this.tracingEntry?.assessment?.virusDetection;
    return [
      [
        ["Nachweismethode", kirTracingConstants.valueLabel(data?.method)],
        [
          "Positiv getestet",
          getRelativeTime(data?.date, {
            prefix: ["", "am"],
            format: "L",
            defaultValue: "",
          }),
        ],
      ],
    ];
  }

  get symptoms() {
    const data = this.tracingEntry?.assessment?.symptoms;
    return [
      [
        [
          "Grippeähnliche Symptome oder Unwohlsein",
          kirTracingConstants.valueLabel(data?.fluLike),
        ],
        [
          "Symptome",
          [
            ...(data?.occurrence ?? [])
              // .filter((item) => item !== "others")
              .map((item) => kirTracingConstants.valueLabel(item)),
            data?.occurrence_others,
          ],
        ],
        [
          "Symptombeginn",
          getRelativeTime(data?.date, {
            prefix: ["", "am"],
            format: "L",
            defaultValue: "",
          }),
        ],
      ],
    ];
  }

  get immuneStatus() {
    const data = this.tracingEntry?.assessment?.immuneStatus;
    return [
      [
        [
          "Anzahl der Impfungen",
          kirTracingConstants.valueLabel(
            data?.vaccinationCount,
            "vaccinationCount"
          ),
        ],
        [
          "In der Vergangenheit bereits mit dem Coronavirus infiziert",
          kirTracingConstants.valueLabel(data?.infection),
        ],
        [
          "Infektionsdaten",
          [
            ...(data?.infection_dates ?? []).map((item) =>
              getFormattedDate(item.value, "LL")
            ),
          ],
        ],
      ],
    ];
  }

  get risk() {
    const data = this.tracingEntry?.assessment?.risk;
    return [
      [
        ["Alter", kirTracingConstants.valueLabel(data?.age)],
        [
          "Chronische Krankheit",
          [
            kirTracingConstants.valueLabel(data?.chronicDisease),
            data?.chronicDisease_details,
          ],
        ],
        [
          "Medikamente die das Immunsystem unterdrücken",
          kirTracingConstants.valueLabel(data?.drugsImmune),
        ],
        [
          "Chronischen Nierenerkrankung",
          kirTracingConstants.valueLabel(data?.kidneyDisease),
        ],
        isTrue(data?.kidneyDisease)
          ? [
              "Dialyse erforderlich",
              kirTracingConstants.valueLabel(data?.kidneyDisease_dialysis),
            ]
          : [],
        ["Trisomie 21", kirTracingConstants.valueLabel(data?.trisomy)],
        [
          "Stark übergewichtig (Body mass index > 35)",
          kirTracingConstants.valueLabel(data?.fat),
        ],
        [
          "HIV-Infektion mit weniger als 200 CD4-Zellen",
          kirTracingConstants.valueLabel(data?.hiv),
        ],
        [
          "Erkrankungen",
          [
            ...(data?.diseases ?? []).map((item) =>
              kirTracingConstants.valueLabel(item)
            ),
            data?.neurological_details,
          ],
        ],
        [
          "Regelmäßige Einnahme von Medikamenten",
          [kirTracingConstants.valueLabel(data?.drugs), data?.drugs_details],
        ],
      ],
    ];
  }

  get medicalCare() {
    const data = this.tracingEntry?.assessment?.medicalCare;
    return [
      [
        [
          "Hausarzt:in über positiven Test informiert",
          kirTracingConstants.valueLabel(
            data?.familyDoctorInformed,
            "familyDoctorInformed"
          ),
        ],
        [
          "Therapiemöglichkeit bekannt",
          kirTracingConstants.valueLabel(
            data?.medicalTherapy,
            "medicalTherapy"
          ),
        ],
        [
          "Interesse an Aufklärung über medikamentöse COVID-19-Therapie",
          kirTracingConstants.valueLabel(data?.interest),
        ],
        !!data?.interest && !!data?.interest_phone
          ? [
              "Patient möchte unter folgender Telefonnummer erreicht werden",
              data?.interest_phone,
            ]
          : [],
      ],
    ];
  }
}
</script>
