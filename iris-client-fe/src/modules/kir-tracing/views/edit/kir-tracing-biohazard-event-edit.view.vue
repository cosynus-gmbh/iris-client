<template>
  <div class="my-3">
    <v-form
      ref="form"
      v-model="form.valid"
      lazy-validation
      :disabled="disabled"
    >
      <v-card>
        <v-card-title>Ereignis bearbeiten</v-card-title>
        <v-card-text>
          <v-row>
            <v-col cols="12">
              <h3 class="mb-3">Merkmale</h3>
              <v-row dense>
                <v-col cols="12" md="6">
                  <v-text-field
                    label="Stoff"
                    v-model="form.model.substance"
                    :rules="validationRules.substance"
                  ></v-text-field>
                </v-col>
                <v-col cols="12" md="6">
                  <v-switch
                    v-model="form.model.active"
                    label="Ereignis aktiv"
                  ></v-switch>
                </v-col>
              </v-row>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
              <h3 class="mb-3">Ereignisort</h3>
              <v-row dense>
                <v-col cols="12">
                  <kir-tracing-places-search-field
                    v-model="form.model.location"
                    :rules="validationRules.location"
                  />
                </v-col>
                <v-col cols="12">
                  <v-text-field
                    label="Umkreis (Radius in m)"
                    v-model="form.model.locationRadius"
                    :rules="validationRules.locationRadius"
                  ></v-text-field>
                </v-col>
              </v-row>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
              <h3 class="mb-3">Zeitraum</h3>
              <v-row dense>
                <v-col cols="12" md="6">
                  <date-time-input-field
                    v-model="form.model.startDate"
                    :date-props="{
                      label: 'Datum (Beginn)',
                    }"
                    :time-props="{
                      label: 'Uhrzeit (Beginn)',
                    }"
                    data-test="start"
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <date-time-input-field
                    v-model="form.model.endDate"
                    :date-props="{
                      label: 'Datum (Ende)',
                    }"
                    :time-props="{
                      label: 'Uhrzeit (Ende)',
                    }"
                    :rules="validationRules.endDate"
                    data-test="end"
                  />
                </v-col>
              </v-row>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-btn
            color="secondary"
            :disabled="disabled"
            plain
            :to="{ name: 'iris-message-list' }"
            replace
            data-test="cancel"
          >
            Abbrechen
          </v-btn>
          <v-spacer></v-spacer>
          <v-btn
            :disabled="disabled"
            color="primary"
            @click="submit"
            data-test="submit"
          >
            Speichern
          </v-btn>
        </v-card-actions>
      </v-card>
      <error-message-alert :errors="errors" />
    </v-form>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from "vue-property-decorator";
import ErrorMessageAlert from "@/components/error-message-alert.vue";
import IrisMessageDataSelectDialog from "@/views/iris-message-create/components/iris-message-data-select-dialog.vue";
import ConfirmDialog from "@/components/confirm-dialog.vue";
import {
  KirTracingBiohazardEvent,
  KirTracingBiohazardEventUpdate,
} from "@/api";
import { kirTracingApi } from "@/modules/kir-tracing/services/api";
import _defaultsDeep from "lodash/defaultsDeep";
import rules, { FormValidationRules } from "@/common/validation-rules";
import KirTracingPlacesSearchField from "@/modules/kir-tracing/views/edit/components/kir-tracing-places-search-field.vue";
import { ErrorMessage } from "@/utils/axios";
import PasswordInputField from "@/components/form/password-input-field.vue";
import DateTimeInputField from "@/components/form/date-time-input-field.vue";
import InfoList from "@/components/info-list.vue";

type KirTracingBiohazardEventEditForm = {
  model: KirTracingBiohazardEventUpdate;
  valid: boolean;
};

@Component({
  components: {
    InfoList,
    DateTimeInputField,
    PasswordInputField,
    KirTracingPlacesSearchField,
    ConfirmDialog,
    IrisMessageDataSelectDialog,
    ErrorMessageAlert,
  },
})
export default class KirTracingBiohazardEventEditView extends Vue {
  fetchBiohazardEvent = kirTracingApi.fetchBiohazardEvent();
  patchBiohazardEvent = kirTracingApi.patchBiohazardEvent();
  $refs!: {
    form: HTMLFormElement;
  };

  eventId = "";

  form: KirTracingBiohazardEventEditForm = {
    model: {
      substance: undefined,
      startDate: undefined,
      endDate: undefined,
      location: {
        latitude: undefined,
        longitude: undefined,
        city: undefined,
        postcode: undefined,
      },
      locationRadius: undefined,
    },
    valid: false,
  };

  get validationRules(): FormValidationRules<KirTracingBiohazardEventUpdate> {
    return {
      substance: [rules.sanitised],
      endDate: [rules.dateEnd(this.form.model.startDate)],
      location: [rules.defined],
      locationRadius: [rules.positiveNumber],
    };
  }

  mounted() {
    this.fetchBiohazardEvent.execute();
  }

  @Watch("biohazardEvent")
  onBiohazardEventChanged(newValue: KirTracingBiohazardEvent | null): void {
    if (newValue) {
      const { id, ...restProps } = newValue;
      this.eventId = id || "";
      this.form.model = _defaultsDeep({}, restProps, this.form.model);
    }
    this.$refs.form.resetValidation();
  }

  get biohazardEvent(): KirTracingBiohazardEvent | null {
    return this.fetchBiohazardEvent.state.result;
  }

  get disabled(): boolean {
    return this.fetchBiohazardEvent.state.loading;
  }

  get errors(): ErrorMessage[] {
    return this.fetchBiohazardEvent.state.error;
  }

  async submit(): Promise<void> {
    const valid = this.$refs.form.validate() as boolean;
    if (this.eventId && valid) {
      const payload: KirTracingBiohazardEventUpdate = this.form.model;
      await this.patchBiohazardEvent.execute(this.eventId, payload);
      await this.$router.replace({ name: "kir-tracing-entry-list" });
    }
  }
}
</script>
