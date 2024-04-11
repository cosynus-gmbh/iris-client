<template>
  <v-card outlined rounded="0">
    <v-card-text>
      <v-row>
        <v-col>
          <info-grid :content="biohazardEvent" />
        </v-col>
        <v-col cols="auto" v-if="editable">
          <v-btn
            icon
            large
            class="text-decoration-none"
            :to="{ name: 'kir-tracing-biohazard-event-edit' }"
            data-test="edit"
          >
            <v-icon> mdi-pencil </v-icon>
          </v-btn>
        </v-col>
      </v-row>
      <error-message-alert :errors="fetchBiohazardEvent.state.error" />
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { kirTracingApi } from "@/modules/kir-tracing/services/api";
import InfoGrid from "@/components/info-grid.vue";
import { getFormattedDate } from "@/utils/date";
import { join } from "@/utils/misc";
import { parseText } from "@/utils/text";
import ErrorMessageAlert from "@/components/error-message-alert.vue";

const KirTracingBiohazardEventOverviewProps = Vue.extend({
  props: {
    editable: {
      type: Boolean,
      default: false,
    },
  },
});

@Component({
  components: {
    ErrorMessageAlert,
    InfoGrid,
  },
})
export default class KirTracingBiohazardEventOverview extends KirTracingBiohazardEventOverviewProps {
  fetchBiohazardEvent = kirTracingApi.fetchBiohazardEvent();
  mounted() {
    this.fetchBiohazardEvent.execute();
  }
  get biohazardEvent() {
    const data = this.fetchBiohazardEvent.state.result;
    const location = data?.location;
    return [
      [
        [
          "Merkmale",
          [
            ["Stoff", parseText(data?.substance, "-")],
            ["Aktiv", data?.active ? "Ja" : "Nein"],
            ["Geändert am", getFormattedDate(data?.lastModifiedAt)],
          ],
        ],
        [
          "Ort",
          [
            join([location?.postcode, location?.city], " "),
            ["Breitengrad", parseText(location?.latitude, "-")],
            ["Längengrad", parseText(location?.longitude, "-")],
            ["Radius", parseText(data?.locationRadius, "-")],
          ],
        ],
        [
          "Zeitraum",
          [
            ["Von", getFormattedDate(data?.startDate)],
            ["Bis", getFormattedDate(data?.endDate)],
          ],
        ],
      ],
    ];
  }
}
</script>
