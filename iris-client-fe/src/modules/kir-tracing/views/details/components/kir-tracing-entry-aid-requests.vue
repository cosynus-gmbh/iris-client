<template>
  <v-row>
    <v-col cols="12">
      <div v-for="(aidRequest, index) in aidRequests" :key="index" class="mb-4">
        <v-card outlined rounded="0">
          <v-card-text>
            <div class="text-right">
              {{ getFormattedDate(aidRequest.createdAt) }}
            </div>
            <v-divider class="my-4" />
            <info-grid :content="resources(aidRequest)" />
          </v-card-text>
        </v-card>
      </div>
    </v-col>
  </v-row>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { KirTracingAidRequest, KirTracingEntry } from "@/api";
import { PropType } from "vue";
import InfoList from "@/components/info-list.vue";
import kirTracingConstants from "@/modules/kir-tracing/services/constants";
import InfoGrid from "@/components/info-grid.vue";
import { getFormattedDate } from "@/utils/date";

const KirTracingEntryAidRequestsProps = Vue.extend({
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
export default class KirTracingEntryAidRequests extends KirTracingEntryAidRequestsProps {
  get aidRequests() {
    return this.tracingEntry?.aidRequests ?? [];
  }
  getFormattedDate = getFormattedDate;

  resources(aidRequest: KirTracingAidRequest | undefined) {
    const data = aidRequest?.data?.resources;
    return [
      [
        [
          "Ressourcen",
          (data?.resourceDemand ?? []).map((value) => {
            const valueLabel = kirTracingConstants.valueLabel(
              value,
              "resourceDemand"
            );
            if (value === "other") {
              return kirTracingConstants.withInlineDetails(
                valueLabel,
                data?.resourceDemand_other_details
              );
            }
            return valueLabel;
          }),
        ],
        ["Benötigt für (wen / was)", data?.resourceUsage],
      ],
    ];
  }
}
</script>
