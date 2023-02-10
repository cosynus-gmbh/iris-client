<template>
  <div v-bind="$attrs">
    <v-row
      :key="rowIndex"
      v-for="(row, rowIndex) in withoutEmpty(content)"
      dense
    >
      <template v-for="([title, info], index) in withoutEmpty(row)">
        <v-col
          v-if="infoRequired ? !isEmpty(info) : true"
          cols="12"
          :key="index"
        >
          <v-row no-gutters>
            <v-col cols="12" sm="6">
              <strong> {{ title }}: </strong>
            </v-col>
            <v-col cols="12" sm="6">
              <template v-if="info && info.length > 0">
                <template v-if="Array.isArray(info)">
                  <ul class="info-list">
                    <li v-for="(item, itemIndex) in info" :key="itemIndex">
                      {{ item }}
                    </li>
                  </ul>
                </template>
                <template v-else>
                  {{ info }}
                </template>
              </template>
              <span v-else class="d-block"> - </span>
            </v-col>
          </v-row>
        </v-col>
      </template>
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
    infoRequired: {
      type: Boolean,
      default: true,
    },
  },
});

@Component
export default class InfoList extends InfoListProps {
  isEmpty(value: unknown) {
    const val = Array.isArray(value) ? this.withoutEmpty(value) : value;
    return _isEmpty(val);
  }
  withoutEmpty(data: Array<unknown>): Array<unknown> {
    return data.filter((v) => !_isEmpty(v));
  }
}
</script>

<style lang="scss">
ul.info-list {
  list-style: none;
  padding-left: 0;
}
</style>
