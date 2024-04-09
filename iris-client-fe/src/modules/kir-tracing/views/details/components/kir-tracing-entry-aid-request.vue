<template>
  <v-row>
    <v-col cols="12" md="6">
      <v-row>
        <v-col cols="12">
          <h3 class="mb-3">Benötigte Ressourcen</h3>
          <info-list :content="resources" />
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
import InfoGrid from "@/components/info-grid.vue";

const KirTracingEntryAidRequestProps = Vue.extend({
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
export default class KirTracingEntryAidRequest extends KirTracingEntryAidRequestProps {
  get resources() {
    const data = this.tracingEntry?.aidRequest?.resources;
    return [
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
    ];
  }
}
</script>
