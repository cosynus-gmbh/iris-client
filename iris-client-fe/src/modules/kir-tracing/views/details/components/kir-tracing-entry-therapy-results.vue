<!--<template>-->
<!--  <div>-->
<!--    <kir-tracing-entry-therapy-results-covid19-->
<!--      :tracing-entry="tracingEntry"-->
<!--      v-if="disease === diseaseTypes.COVID_19 || true"-->
<!--    />-->
<!--  </div>-->
<!--</template>-->

<!--<script lang="ts">-->
<!--import { Component, Vue } from "vue-property-decorator";-->
<!--import { KirTracingDisease, KirTracingEntry } from "@/api";-->
<!--import { PropType } from "vue";-->
<!--import KirTracingEntryTherapyResultsCovid19 from "@/modules/kir-tracing/modules/covid19/components/kir-tracing-entry-therapy-results.covid19.vue";-->

<!--const KirTracingEntryTherapyResultsProps = Vue.extend({-->
<!--  props: {-->
<!--    tracingEntry: {-->
<!--      type: Object as PropType<KirTracingEntry | undefined>,-->
<!--      default: null,-->
<!--    },-->
<!--  },-->
<!--});-->
<!--@Component({-->
<!--  components: { KirTracingEntryTherapyResultsCovid19 },-->
<!--})-->
<!--export default class KirTracingEntryTherapyResults extends KirTracingEntryTherapyResultsProps {-->
<!--  get disease(): KirTracingDisease | undefined {-->
<!--    return this.tracingEntry?.targetDisease;-->
<!--  }-->
<!--  diseaseTypes = KirTracingDisease;-->
<!--}-->
<!--</script>-->
