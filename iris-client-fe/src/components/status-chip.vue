<template>
  <v-chip :color="statusColor" :data-test="`status.${status}`" dark>
    {{ statusName }}
  </v-chip>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { PropType } from "vue";

interface StatusChipMapper {
  getStatusColor: (status: string) => string;
  getStatusName: (status: string) => string;
}

const StatusChipProps = Vue.extend({
  inheritAttrs: false,
  props: {
    mapper: {
      type: Object as PropType<StatusChipMapper>,
    },
    status: {
      type: String,
      default: "",
    },
  },
});
@Component
export default class StatusChip extends StatusChipProps {
  get statusColor() {
    return this.mapper.getStatusColor(this.status);
  }
  get statusName() {
    return this.mapper.getStatusName(this.status);
  }
}
</script>
