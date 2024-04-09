<template>
  <v-row>
    <v-col cols="12" md="6">
      <v-row>
        <v-col cols="12">
          <h3 class="mb-3">Personendaten</h3>
          <info-list :content="personalData" />
          <v-divider class="mt-6" />
        </v-col>
        <v-col cols="12">
          <h3 class="mb-3">Exposition</h3>
          <info-list :content="exposition" />
          <v-divider class="mt-6" />
        </v-col>
        <v-col cols="12">
          <h3 class="mb-3">Aktueller Aufenthaltsort</h3>
          <info-list :content="currentLocation" />
          <v-divider class="mt-6" />
        </v-col>
      </v-row>
    </v-col>
    <v-col cols="12" md="6">
      <v-row>
        <v-col cols="12">
          <h3 class="mb-3">Symptomatik</h3>
          <info-list :content="symptoms" />
          <v-divider class="mt-6" />
        </v-col>
        <v-col cols="12">
          <h3 class="mb-3">Vorerkrankungen</h3>
          <info-list :content="previousIllness" />
          <v-divider class="mt-6" />
        </v-col>
      </v-row>
    </v-col>
  </v-row>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { KirTracingEntry } from "@/api";
import { PropType } from "vue";
import InfoList from "@/components/info-list.vue";
import kirTracingConstants from "@/modules/kir-tracing/services/constants";
import { getFormattedDate } from "@/utils/date";
import { join } from "@/utils/misc";
import InfoGrid from "@/components/info-grid.vue";

const KirTracingEntryAssessmentProps = Vue.extend({
  props: {
    tracingEntry: {
      type: Object as PropType<KirTracingEntry | undefined>,
      default: null,
    },
  },
});
@Component({
  components: { InfoGrid, InfoList },
})
export default class KirTracingEntryAssessment extends KirTracingEntryAssessmentProps {
  get personalData() {
    const data = this.tracingEntry?.assessment?.personalData;
    return [
      ["Vorname", data?.firstName],
      ["Nachname", data?.lastName],
      ["Telefonnummer", data?.phone],
      ["E-Mail-Adresse", data?.email],
    ];
  }
  get exposition() {
    const data = this.tracingEntry?.assessment?.exposition;
    return [
      ["Zeitpunkt", getFormattedDate(data?.dateTime)],
      [
        "Aufenthaltsort",
        [
          join([data?.location?.postcode, data?.location?.city], " "),
          join(["Breitengrad", data?.location?.latitude ?? "-"], ": "),
          join(["Längengrad", data?.location?.longitude ?? "-"], ": "),
        ],
      ],
      ["Details zum Aufenthaltsort", data?.locationNotes],
      ["Schutzmaske ", kirTracingConstants.valueLabel(data?.protectiveMask)],
    ];
  }
  get currentLocation() {
    const data = this.tracingEntry?.assessment?.currentLocation;
    return [
      ["Seit:", getFormattedDate(data?.dateTime)],
      [
        "Ort",
        [
          join([data?.location?.postcode, data?.location?.city], " "),
          join(["Breitengrad", data?.location?.latitude ?? "-"], ": "),
          join(["Längengrad", data?.location?.longitude ?? "-"], ": "),
        ],
      ],
      [
        "Art",
        kirTracingConstants.withInlineDetails(
          kirTracingConstants.valueLabel(data?.locationType, "locationType"),
          data?.locationType_other_details
        ),
      ],
      ["Anzahl weiterer Personen", data?.numberOfPeople],
      [
        "Schutzbedürftige Personen anwesend",
        kirTracingConstants.yesNoWithDetails(data?.vulnerablePeople, {
          yesDetails: data?.vulnerablePeople_yes_details,
        }),
      ],
      [
        "Räumliche Trennung möglich",
        kirTracingConstants.yesNoWithDetails(data?.spatialSeparationPossible, {
          yesDetails: data?.spatialSeparationPossible_details,
        }),
      ],
    ];
  }
  get symptoms() {
    const data = this.tracingEntry?.assessment?.symptoms;
    return [
      [
        "Symptome seit Exposition",
        kirTracingConstants.valueLabel(data?.healthProblems, "healthProblems"),
      ],
      [
        "Symptome bemerkt",
        getFormattedDate(data?.healthProblems_afterTheEvent_dateTime),
      ],
      [
        "Atembeschwerden",
        kirTracingConstants.yesNoWithDetailsList(data?.breathingDifficulties, {
          yesDetails: data?.breathingDifficulties_yes_details,
          mapper: (value) =>
            kirTracingConstants.valueLabel(value, "breathingDifficulties"),
        }),
      ],
      [
        "Körpertemperatur",
        kirTracingConstants.valueLabel(
          data?.bodyTemperature,
          "bodyTemperature"
        ),
      ],
      [
        "Übelkeit oder Erbrechen",
        kirTracingConstants.yesNoWithDetailsList(data?.nauseaOrVomiting, {
          yesDetails: data?.nauseaOrVomiting_yes_details,
          mapper: (value) => {
            const valueLabel = kirTracingConstants.valueLabel(value);
            if (value === "vomiting") {
              return kirTracingConstants.withCount(
                valueLabel,
                data?.nauseaOrVomiting_yes_details_vomiting_numberOfTimes
              );
            }
            return valueLabel;
          },
        }),
      ],
      [
        "Durchfall",
        kirTracingConstants.withCount(
          kirTracingConstants.valueLabel(data?.diarrhea),
          data?.diarrhea_yes_numberOfTimes
        ),
      ],
      [
        "Hautausschlag oder Hautveränderungen bemerkt",
        kirTracingConstants.yesNoWithDetails(data?.rash, {
          yesDetails: data?.rash_yes_details,
        }),
      ],
      [
        "Ungewöhnliche Blutungen bemerkt",
        kirTracingConstants.yesNoWithDetailsList(data?.unusualBleeding, {
          yesDetails: data?.unusualBleeding_yes_details,
          mapper: (value) => {
            const valueLabel = kirTracingConstants.valueLabel(
              value,
              "unusualBleeding"
            );
            if (value == "other") {
              return kirTracingConstants.withInlineDetails(
                valueLabel,
                data?.unusualBleeding_yes_details_other_details
              );
            }
            return valueLabel;
          },
        }),
      ],
      ["Ergänzungen zum Gesundheitszustand", data?.healthConditionInfo],
    ];
  }
  get previousIllness() {
    const data = this.tracingEntry?.assessment?.previousIllness;
    return [
      [
        "Chronischen Krankheiten",
        kirTracingConstants.valueLabel(data?.chronicDisease),
      ],
      [
        "Chronische Atemwegserkrankung",
        kirTracingConstants.yesNoWithDetailsList(
          data?.chronicRespiratoryDisease,
          {
            yesDetails: data?.chronicRespiratoryDisease_yes_details,
            mapper: (value) => {
              const valueLabel = kirTracingConstants.valueLabel(
                value,
                "chronicRespiratoryDisease"
              );
              if (value === "other") {
                return kirTracingConstants.withInlineDetails(
                  valueLabel,
                  data?.chronicRespiratoryDisease_yes_details_other_details
                );
              }
              return valueLabel;
            },
          }
        ),
      ],
      [
        "Erkrankung des Herz-Kreislauf-Systems",
        kirTracingConstants.yesNoWithDetails(data?.cardiovascularDisease, {
          yesDetails: data?.cardiovascularDisease_yes_details,
        }),
      ],
      [
        "Erkrankung der Nieren",
        kirTracingConstants.yesNoWithDetails(data?.kidneyDisease, {
          yesDetails: data?.kidneyDisease_yes_details,
        }),
      ],
      ["Diabetes Mellitus", kirTracingConstants.valueLabel(data?.diabetes)],
      [
        "Immunschwäche",
        kirTracingConstants.yesNoWithDetails(data?.immuneDeficiency, {
          yesDetails: data?.immuneDeficiency_yes_details,
        }),
      ],
      [
        "regelmäßige Einnahme von Medikamenten",
        kirTracingConstants.yesNoWithDetails(data?.medication, {
          yesDetails: data?.medication_yes_details,
        }),
      ],
    ];
  }
}
</script>
