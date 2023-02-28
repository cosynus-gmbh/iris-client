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
  riskFactor?: string;
  symptomSeverity?: string;
  therapyRecommendation: {
    label: string;
    color: string;
  };
  metadata: {
    created: string;
  };
};
export const getKirTracingEntryTableHeaders = () => [
  { text: "Kontakt", value: "person.mobilePhone", sortable: true },
  { text: "Erkrankung", value: "targetDisease", sortable: true },
  {
    text: "Therapieempfehlung",
    value: "therapyRecommendation",
    sortable: false,
  },
  { text: "Risiko", value: "riskFactor", sortable: true },
  { text: "Symptomstärke", value: "symptomSeverity", sortable: true },
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
      targetDisease: kirTracingConstants.getDisease(entry.targetDisease),
      status: entry.status,
      riskFactor: kirTracingConstants.getRiskFactorLabel(
        entry.riskFactor,
        entry.targetDisease,
        true
      ),
      symptomSeverity: kirTracingConstants.getSymptomSeverityLabel(
        entry.symptomSeverity,
        entry.targetDisease,
        true
      ),
      therapyRecommendation: {
        label: kirTracingConstants.getTherapyRecommendationLabel(
          entry.riskFactor,
          entry.symptomSeverity,
          entry.targetDisease,
          true
        ),
        color: kirTracingConstants.getThresholdColor(
          kirTracingConstants.getTherapyRecommendationThreshold(
            entry?.riskFactor,
            entry?.symptomSeverity,
            entry?.targetDisease
          )
        ),
      },
      metadata: {
        created: getFormattedDate(entry.createdAt, "L LT"),
      },
    };
  });
};
