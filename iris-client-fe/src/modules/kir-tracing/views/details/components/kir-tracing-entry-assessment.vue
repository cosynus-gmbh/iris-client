<template>
  <div>
    <kir-tracing-entry-assessment-covid19
      :tracing-entry="tracingEntry"
      v-if="disease === diseaseTypes.COVID_19"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { KirTracingDisease, KirTracingEntry } from "@/api";
import { PropType } from "vue";
import KirTracingEntryAssessmentCovid19 from "@/modules/kir-tracing/modules/covid19/components/kir-tracing-entry-assessment.covid19.vue";

const KirTracingEntryAssessmentProps = Vue.extend({
  props: {
    tracingEntry: {
      type: Object as PropType<KirTracingEntry | undefined>,
      default: null,
    },
  },
});
@Component({
  components: { KirTracingEntryAssessmentCovid19 },
})
export default class KirTracingEntryAssessment extends KirTracingEntryAssessmentProps {
  get disease(): KirTracingDisease | undefined {
    return this.tracingEntry?.targetDisease;
  }
  diseaseTypes = KirTracingDisease;
}
</script>
