<template>
  <div v-bind="$attrs">
    <v-row :key="rowIndex" v-for="(row, rowIndex) in notEmpty(content)" dense>
      <v-col
        cols="12"
        v-for="([title, info], index) in notEmpty(row)"
        :key="index"
      >
        <v-row no-gutters>
          <v-col cols="12" sm="6">
            <strong> {{ title }}: </strong>
          </v-col>
          <v-col cols="12" sm="6">
            <template v-if="info && info.length > 0">
              <template v-if="Array.isArray(info)">
                <span
                  class="d-block"
                  v-for="(item, itemIndex) in info"
                  :key="itemIndex"
                >
                  {{ item }}
                </span>
              </template>
              <template v-else>
                {{ info }}
              </template>
            </template>
            <span v-else class="d-block"> - </span>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { PropType } from "vue";
import _isEmpty from "lodash/isEmpty";

export type InfoListContent = Array<string | string[] | InfoListContent>;

const InfoListProps = Vue.extend({
  inheritAttrs: false,
  props: {
    content: {
      type: Array as PropType<InfoListContent | null>,
      default: null,
    },
  },
});

@Component
export default class InfoList extends InfoListProps {
  notEmpty(data: Array<unknown>): Array<unknown> {
    return data.filter((v) => !_isEmpty(v));
  }
}
</script>
