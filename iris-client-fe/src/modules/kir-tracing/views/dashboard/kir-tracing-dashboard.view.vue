<template>
  <data-query-handler
    :data-query="{
      status: 'NEW',
    }"
    :route-control="false"
    @update:query="handleQueryUpdate"
    #default="{ query }"
  >
    <v-row>
      <v-col>
        <v-card color="primary" class="mt-4">
          <v-container>
            <h2 class="light-font">
              Herzlich willkommen bei IRIS connect - Die öffentliche
              Datenschnittstelle des Gesundheitsamts
            </h2>
          </v-container>
        </v-card>
      </v-col>
    </v-row>
    <v-card class="my-7">
      <v-card-title>Neue KIR Formulareingänge</v-card-title>
      <v-card-text>
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
export default class KirTracingDashboardView extends Vue {
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
  get tableRows() {
    return getKirTracingEntryTableRows(this.fetchPage.state.result?.content);
  }
  get totalElements(): number | undefined {
    return this.fetchPage.state.result?.totalElements;
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
