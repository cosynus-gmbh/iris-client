<template>
  <data-query-handler @update:query="handleQueryUpdate" #default="{ query }">
    <v-row class="mt-4">
      <v-col cols="12">
        Status:
        <btn-toggle-select
          :select-options="statusSelectOptions"
          data-test-key="status"
          v-model="query.status"
        />
      </v-col>
    </v-row>
    <v-card class="my-7">
      <v-card-title>Kontakt Infizierte Risikogruppen</v-card-title>
      <v-card-subtitle v-if="fetchCount.state.result > 0">
        {{ fetchCount.state.result }} von
        {{ totalElements + fetchCount.state.result }} Person(en) haben das
        Formular ausgefüllt, jedoch nicht abgesendet.
      </v-card-subtitle>
      <v-card-text>
        <search-field v-model="query.search" />
        <sortable-data-table
          class="mt-5"
          :item-class="() => 'cursor-pointer'"
          :headers="tableHeaders"
          :sort.sync="query.sort"
          :items="tableRows"
          :loading="fetchPage.state.loading"
          :page.sync="query.page"
          :items-per-page.sync="query.size"
          :server-items-length="totalElements"
          @click:row="handleRowClick"
          data-test="view.data-table"
          multi-sort
        >
          <template #item.therapyRecommendation="{ item }">
            <v-chip :color="item.therapyRecommendation.color" dark>
              {{ item.therapyRecommendation.label }}
            </v-chip>
          </template>
          <template #item.status="{ item }">
            <status-chip :mapper="kirTracingConstants" :status="item.status" />
          </template>
          <template #item.actions="{ item }">
            <kir-tracing-entry-delete-button
              icon
              large
              color="error"
              @click="() => deleteKirTracingEntry(item.id, query)"
              :disabled="deleteEntry.state.loading"
              :person-phone="item.person.mobilePhone"
              data-test="delete"
            >
              <v-icon> mdi-delete </v-icon>
            </kir-tracing-entry-delete-button>
          </template>
        </sortable-data-table>
        <error-message-alert :errors="fetchPage.state.error" />
        <error-message-alert :errors="deleteEntry.state.error" />
      </v-card-text>
    </v-card>
  </data-query-handler>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import ErrorMessageAlert from "@/components/error-message-alert.vue";
import SortableDataTable from "@/components/sortable-data-table.vue";
import SearchField from "@/components/pageable/search-field.vue";
import DataQueryHandler from "@/components/pageable/data-query-handler.vue";
import {
  getKirTracingEntryTableHeaders,
  getKirTracingEntryTableRows,
} from "@/modules/kir-tracing/services/mappedData";
import { kirTracingApi } from "@/modules/kir-tracing/services/api";
import { DataQuery } from "@/api/common";
import StatusChip from "@/components/status-chip.vue";
import kirTracingConstants from "@/modules/kir-tracing/services/constants";
import BtnToggleSelect from "@/components/btn-toggle-select.vue";
import { getEnumKeys } from "@/utils/data";
import { KirTracingStatus } from "@/api";
import KirTracingEntryDeleteButton from "@/modules/kir-tracing/views/details/components/kir-tracing-entry-delete-button.vue";

@Component({
  components: {
    KirTracingEntryDeleteButton,
    BtnToggleSelect,
    StatusChip,
    ErrorMessageAlert,
    SortableDataTable,
    SearchField,
    DataQueryHandler,
  },
})
export default class KirTracingEntryListView extends Vue {
  tableHeaders = getKirTracingEntryTableHeaders();
  fetchPage = kirTracingApi.fetchPageTracingEntry();
  fetchCount = kirTracingApi.fetchUnsubmittedTracingEntryCount();
  deleteEntry = kirTracingApi.deleteTracingEntry();
  kirTracingConstants = kirTracingConstants;
  handleQueryUpdate(newValue: DataQuery) {
    this.fetchCount.execute();
    if (newValue) {
      this.fetchPage.execute(newValue);
    } else {
      this.fetchPage.reset(["result"]);
    }
  }
  async deleteKirTracingEntry(id: string, query: DataQuery) {
    await this.deleteEntry.execute(id);
    this.handleQueryUpdate(query);
  }
  get tableRows() {
    return getKirTracingEntryTableRows(this.fetchPage.state.result?.content);
  }
  get totalElements(): number | undefined {
    return this.fetchPage.state.result?.totalElements;
  }
  get statusSelectOptions() {
    return getEnumKeys(KirTracingStatus).map((key) => {
      return {
        text: kirTracingConstants.getStatusName(KirTracingStatus[key]),
        value: KirTracingStatus[key],
      };
    });
  }
  handleRowClick(row: { id?: string }) {
    if (row.id) {
      this.$router.push({
        name: "kir-tracing-entry-details",
        params: { id: row.id },
      });
    }
  }
}
</script>
