<template>
  <v-card>
    <v-card-title> Patientendaten </v-card-title>
    <v-card-text>
      <v-row>
        <v-col cols="12" md="6">
          <info-grid :content="reportInfo" />
        </v-col>
        <v-col cols="12" md="6" data-test="event.status">
          <span class="d-inline-block mr-3">
            <strong> Aktueller Status: </strong>
          </span>
          <v-chip :color="statusColor" dark class="my-n2">
            {{ statusName }}
          </v-chip>
          <kir-tracing-entry-status-change
            :status="status"
            @update="handleStatusUpdate"
          />
        </v-col>
      </v-row>
      <v-divider class="my-4" />
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
      <error-message-alert :errors="errorMessages" />
    </v-card-text>
    <v-card-actions>
      <v-btn
        color="secondary"
        plain
        :to="{ name: 'kir-tracing-entry-list' }"
        replace
        data-test="cancel"
      >
        Zurück
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script lang="ts">
import { Component, Mixins } from "vue-property-decorator";
import HistoryBack from "@/mixins/HistoryBack";
import { bundleKirTracingApi } from "@/modules/kir-tracing/services/api";
import { getFormattedDate, getRelativeTime } from "@/utils/date";
import InfoGrid from "@/components/info-grid.vue";
import ErrorMessageAlert from "@/components/error-message-alert.vue";
import KirTracingEntryStatusChange from "@/modules/kir-tracing/views/details/components/kir-tracing-entry-status-change.vue";
import { KirTracingStatus } from "@/api";
import { ErrorMessage } from "@/utils/axios";
import { getApiErrorMessages, getApiLoading } from "@/utils/api";
import kirTracingConstants from "@/modules/kir-tracing/services/constants";
import InfoList from "@/components/info-list.vue";
import { isTrue } from "@/utils/misc";

@Component({
  components: {
    InfoList,
    KirTracingEntryStatusChange,
    ErrorMessageAlert,
    InfoGrid,
  },
})
export default class KirTracingEntryDetailsView extends Mixins(
  HistoryBack("kir-tracing-entry-list")
) {
  kirTracingApi = bundleKirTracingApi([
    "fetchTracingEntryDetails",
    "patchTracingEntry",
  ]);

  mounted() {
    this.kirTracingApi.fetchTracingEntryDetails.execute(this.$route.params.id);
  }

  get reportInfo() {
    return [
      [["Kontakt", this.kirTracingEntry?.person.mobilePhone]],
      [["Meldung vom", getFormattedDate(this.kirTracingEntry?.createdAt)]],
    ];
  }

  get virusDetection() {
    const data = this.kirTracingEntry?.assessment?.virusDetection;
    return [
      [
        ["Nachweismethode", kirTracingConstants.valueLabel(data?.method)],
        ["Positiver Test", getRelativeTime(data?.date, ["", "am"], "L LT", "")],
      ],
    ];
  }

  get symptoms() {
    const data = this.kirTracingEntry?.assessment?.symptoms;
    return [
      [
        [
          "Grippeähnliche Symptome oder Unwohlsein",
          kirTracingConstants.valueLabel(data?.fluLike),
        ],
        [
          "Symptome",
          [
            ...(data?.occurrence ?? []).map((item) =>
              kirTracingConstants.valueLabel(item)
            ),
            data?.occurrence_others,
          ],
        ],
        ["Symptombeginn", getRelativeTime(data?.date, ["", "am"], "L LT", "")],
      ],
    ];
  }

  get immuneStatus() {
    const data = this.kirTracingEntry?.assessment?.immuneStatus;
    return [
      [
        [
          "Anzahl der Impfungen",
          kirTracingConstants.valueLabel(data?.vaccinationCount),
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
    const data = this.kirTracingEntry?.assessment?.risk;
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
    const data = this.kirTracingEntry?.assessment?.medicalCare;
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
          "Medikamentöse Therapie thematitisiert",
          kirTracingConstants.valueLabel(
            data?.medicalTherapy,
            "medicalTherapy"
          ),
        ],
        [
          "Patient hat Interesse an einer Aufklärung über eine medikamentöse COVID-19-Therapie durch das Gesundheitsamt",
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

  get kirTracingEntry() {
    return this.kirTracingApi.fetchTracingEntryDetails.state.result;
  }

  get status(): KirTracingStatus {
    return this.kirTracingEntry?.status ?? KirTracingStatus.NEW;
  }

  get statusName(): string {
    return kirTracingConstants.getStatusName(this.kirTracingEntry?.status);
  }

  get statusColor(): string {
    return kirTracingConstants.getStatusColor(this.kirTracingEntry?.status);
  }

  get loading(): boolean {
    return getApiLoading(this.kirTracingApi);
  }

  get errorMessages(): ErrorMessage[] {
    return getApiErrorMessages(this.kirTracingApi);
  }

  async handleStatusUpdate(status: KirTracingStatus): Promise<void> {
    await this.kirTracingApi.patchTracingEntry.execute(this.$route.params.id, {
      status,
    });
    await this.kirTracingApi.fetchTracingEntryDetails.execute(
      this.$route.params.id
    );
  }
}
</script>
