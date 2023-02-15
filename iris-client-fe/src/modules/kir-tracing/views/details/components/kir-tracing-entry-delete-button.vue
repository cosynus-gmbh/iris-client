<template>
  <v-dialog max-width="400" v-model="confirmDialog">
    <template v-slot:activator="{ on, attrs }">
      <v-btn v-bind="{ ...$attrs, ...attrs }" v-on="on">
        <slot />
      </v-btn>
    </template>
    <v-card data-test="user-delete.confirm-dialog">
      <v-card-title> Kontaktdaten löschen? </v-card-title>
      <v-card-text>
        Sind Sie sicher, dass Sie die Kontaktdaten für {{ personPhone }} löschen
        möchten?
      </v-card-text>
      <v-card-actions>
        <v-btn
          color="secondary"
          text
          @click="confirmDialog = false"
          data-test="cancel"
        >
          Abbrechen
        </v-btn>
        <v-spacer></v-spacer>
        <v-btn
          color="primary"
          @click="() => deleteEntry(true)"
          data-test="confirm"
        >
          Löschen
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";

const KirTracingEntryDeleteButtonProps = Vue.extend({
  props: {
    personPhone: {
      type: String,
      default: "",
    },
  },
});

@Component
export default class KirTracingEntryDeleteButton extends KirTracingEntryDeleteButtonProps {
  confirmDialog = false;
  deleteEntry(confirmed: boolean): void {
    this.confirmDialog = !confirmed;
    if (confirmed) {
      this.$emit("click");
    }
  }
}
</script>
