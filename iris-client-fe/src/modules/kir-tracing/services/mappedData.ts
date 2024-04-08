import { KirTracingEntry } from "@/api";
import kirTracingConstants from "@/modules/kir-tracing/services/constants";
import { getFormattedDate } from "@/utils/date";

export type KirTracingEntryTableRow = {
  id?: string;
  person: {
    mobilePhone: string;
  };
  status: string;
  riskFactor?: string;
  metadata: {
    created: string;
  };
};
export const getKirTracingEntryTableHeaders = () => [
  { text: "Kontakt", value: "person.mobilePhone", sortable: true },
  { text: "Risiko", value: "riskFactor", sortable: true },
  { text: "Status", value: "status", sortable: true },
  { text: "Eingegangen am", value: "metadata.created", sortable: true },
  {
    text: "",
    value: "actions",
    sortable: false,
    align: "end",
    cellClass: "text-no-wrap",
  },
];

export const getKirTracingEntryTableRows = (
  entries: KirTracingEntry[] | undefined
): KirTracingEntryTableRow[] => {
  return (entries || []).map((entry) => {
    return {
      id: entry.id,
      person: entry.person,
      status: entry.status,
      riskFactor: kirTracingConstants.getRiskFactorLabel(
        entry.riskFactor,
        true
      ),
      metadata: {
        created: getFormattedDate(entry.createdAt, "L LT"),
      },
    };
  });
};
