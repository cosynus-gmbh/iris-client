import { KirTracingEntry } from "@/api";
import kirTracingConstants from "@/modules/kir-tracing/services/constants";
import { getFormattedDate } from "@/utils/date";

export type KirTracingEntryTableRow = {
  id?: string;
  person: {
    mobilePhone: string;
  };
  targetDisease: string;
  status: string;
  metadata: {
    created: string;
  };
};
export const getKirTracingEntryTableHeaders = () => [
  { text: "Kontakt", value: "person.mobilePhone", sortable: true },
  { text: "Erkrankung", value: "targetDisease", sortable: true },
  { text: "Status", value: "status", sortable: true },
  { text: "Eingegangen am", value: "metadata.created", sortable: true },
];

export const getKirTracingEntryTableRows = (
  entries: KirTracingEntry[] | undefined
): KirTracingEntryTableRow[] => {
  return (entries || []).map((entry) => {
    return {
      id: entry.id,
      person: entry.person,
      targetDisease: kirTracingConstants.getDisease(entry.targetDisease),
      status: entry.status,
      metadata: {
        created: getFormattedDate(entry.createdAt, "L LT"),
      },
    };
  });
};
