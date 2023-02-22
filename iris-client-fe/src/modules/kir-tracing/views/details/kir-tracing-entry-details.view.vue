<template>
  <v-card>
    <v-card-title> Patientendaten </v-card-title>
    <v-card-text>
      <v-row>
        <v-col cols="12" sm="6">
          <info-grid :content="reportInfo" />
        </v-col>
        <v-col cols="12" sm="6" data-test="event.status">
          <span class="d-inline-block mr-3">
            <strong> Aktueller Status: </strong>
          </span>
          <status-chip :status="status" :mapper="kirTracingConstants" />
          <kir-tracing-entry-status-change
            :status="status"
            @update="handleStatusUpdate"
          />
        </v-col>
      </v-row>
      <v-divider class="my-4" />
      <v-tabs v-model="currentTab" @change="handleTabsChange">
        <v-tab>Bewertungsbogen</v-tab>
        <v-tab :disabled="!hasTherapyResults">Therapieergebnisse</v-tab>
        <v-tab :disabled="tracingMessages.length <= 0">
          {{ tracingMessages.length === 1 ? "Nachricht" : "Nachrichten" }}
        </v-tab>
      </v-tabs>
      <v-tabs-items v-model="currentTab" class="mt-6">
        <v-tab-item>
          <kir-tracing-entry-assessment :tracing-entry="kirTracingEntry" />
        </v-tab-item>
        <v-tab-item>
          <kir-tracing-entry-therapy-results :tracing-entry="kirTracingEntry" />
        </v-tab-item>
        <v-tab-item>
          <v-row>
            <v-col cols="12">
              <div
                v-for="(message, index) in tracingMessages"
                :key="index"
                class="mb-4"
              >
                <v-card outlined rounded="0">
                  <v-card-text>
                    <div class="text-right">
                      {{ getFormattedDate(message.createdAt) }}
                    </div>
                    <v-divider class="my-4" />
                    <div class="text-pre-line">
                      {{ message.text }}
                    </div>
                  </v-card-text>
                </v-card>
              </div>
            </v-col>
          </v-row>
        </v-tab-item>
      </v-tabs-items>
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
import { getFormattedDate } from "@/utils/date";
import InfoGrid from "@/components/info-grid.vue";
import ErrorMessageAlert from "@/components/error-message-alert.vue";
import KirTracingEntryStatusChange from "@/modules/kir-tracing/views/details/components/kir-tracing-entry-status-change.vue";
import { KirTracingStatus } from "@/api";
import { ErrorMessage } from "@/utils/axios";
import { getApiErrorMessages, getApiLoading } from "@/utils/api";
import kirTracingConstants from "@/modules/kir-tracing/services/constants";
import InfoList from "@/components/info-list.vue";
import StatusChip from "@/components/status-chip.vue";
import KirTracingEntryAssessment from "@/modules/kir-tracing/views/details/components/kir-tracing-entry-assessment.vue";
import KirTracingEntryTherapyResults from "@/modules/kir-tracing/views/details/components/kir-tracing-entry-therapy-results.vue";
import { pathsIn } from "@/utils/misc";

@Component({
  components: {
    KirTracingEntryAssessment,
    KirTracingEntryTherapyResults,
    StatusChip,
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

  currentTab = 0;
  handleTabsChange(index: number): void {
    this.currentTab = index;
  }

  mounted() {
    this.kirTracingApi.fetchTracingEntryDetails.execute(this.$route.params.id);
  }

  get reportInfo() {
    return [
      [["Kontakt", this.kirTracingEntry?.person.mobilePhone]],
      [["Meldung vom", getFormattedDate(this.kirTracingEntry?.createdAt)]],
    ];
  }

  get tracingMessages() {
    return this.kirTracingEntry?.messages ?? [];
  }

  kirTracingConstants = kirTracingConstants;
  getFormattedDate = getFormattedDate;

  get kirTracingEntry() {
    return this.kirTracingApi.fetchTracingEntryDetails.state.result;
  }

  get hasTherapyResults() {
    return pathsIn(this.kirTracingEntry?.therapyResults ?? {}).length > 0;
  }

  get status(): KirTracingStatus {
    return this.kirTracingEntry?.status ?? KirTracingStatus.NEW;
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
