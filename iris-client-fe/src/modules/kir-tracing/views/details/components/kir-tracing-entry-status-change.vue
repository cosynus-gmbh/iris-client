<template>
  <div class="mt-5">
    <span class="d-inline-block mr-3"> Status ändern: </span>
    <v-row dense>
      <v-col
        v-for="selectOption in statusSelectOptions"
        :key="selectOption.value"
        cols="12"
      >
        <v-btn
          small
          outlined
          :color="selectOption.color"
          @click="$emit('update', selectOption.value)"
        >
          {{ selectOption.text }}
        </v-btn>
      </v-col>
    </v-row>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { EditableKirTracingStatus } from "@/api";
import BtnToggleSelect from "@/components/btn-toggle-select.vue";
import { getEnumKeys } from "@/utils/data";
import kirTracingConstants from "@/modules/kir-tracing/services/constants";
import { PropType } from "vue";

const KirTracingEntryStatusChangeProps = Vue.extend({
  props: {
    status: {
      type: String as PropType<EditableKirTracingStatus>,
      default: null,
    },
  },
});
@Component({
  components: { BtnToggleSelect },
})
export default class KirTracingEntryStatusChange extends KirTracingEntryStatusChangeProps {
  get statusSelectOptions() {
    return getEnumKeys(EditableKirTracingStatus)
      .filter((status) => status !== this.status)
      .map((key) => {
        return {
          text: kirTracingConstants.getStatusButtonLabel(
            EditableKirTracingStatus[key]
          ),
          color: kirTracingConstants.getStatusColor(
            EditableKirTracingStatus[key]
          ),
          value: EditableKirTracingStatus[key],
        };
      });
  }
}
</script>

<style scoped></style>
