import {
  KirTracingAssessment,
  KirTracingDisease,
  KirTracingEntry,
  KirTracingMessage,
  KirTracingPerson,
  KirTracingStatus,
  KirTracingTherapyResults,
  Page,
} from "@/api";
import { normalizeData } from "@/utils/data";
import { normalizePage } from "@/common/normalizer";
import {
  normalizeKirTracingAssessmentCovid19,
  normalizeKirTracingTherapyResultsCovid19,
} from "@/modules/kir-tracing/modules/covid19/services/normalizer";

export const normalizeKirTracingAssessment = <D extends KirTracingDisease>(
  source?: KirTracingAssessment[D],
  disease?: D,
  parse?: boolean
): KirTracingAssessment[D] => {
  switch (disease) {
    case KirTracingDisease.COVID_19:
      return normalizeKirTracingAssessmentCovid19(source, parse);
    default:
      throw Error("invalid disease type");
  }
};

export const normalizeKirTracingTherapyResults = <D extends KirTracingDisease>(
  source?: KirTracingTherapyResults[D],
  disease?: D,
  parse?: boolean
): KirTracingTherapyResults[D] => {
  switch (disease) {
    case KirTracingDisease.COVID_19:
      return normalizeKirTracingTherapyResultsCovid19(source, parse);
    default:
      throw Error("invalid disease type");
  }
};

export const normalizeKirTracingPerson = (
  source?: KirTracingPerson,
  parse?: boolean
): KirTracingPerson => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        mobilePhone: normalizer("mobilePhone", ""),
      };
    },
    parse,
    "KirTracingPerson"
  );
};

export const normalizeKirTracingMessage = (
  source?: KirTracingMessage,
  parse?: boolean
): KirTracingMessage => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        text: normalizer("text", ""),
        createdAt: normalizer("createdAt", undefined, "dateString"),
      };
    },
    parse,
    "KirTracingMessage"
  );
};

export const normalizeKirTracingEntry = (
  source?: KirTracingEntry,
  parse?: boolean
): KirTracingEntry => {
  return normalizeData(
    source,
    (normalizer) => {
      const targetDisease = normalizer(
        "targetDisease",
        KirTracingDisease.COVID_19
      );
      const messages = normalizer("messages", undefined, "array") || [];
      return {
        id: normalizer("id", undefined),
        targetDisease: targetDisease,
        assessment: normalizeKirTracingAssessment<typeof targetDisease>(
          source?.assessment,
          targetDisease
        ),
        therapyResults: normalizeKirTracingTherapyResults<typeof targetDisease>(
          source?.therapyResults,
          targetDisease
        ),
        person: normalizeKirTracingPerson(source?.person),
        status: normalizer("status", KirTracingStatus.NEW),
        createdAt: normalizer("createdAt", undefined, "dateString"),
        messages: messages.map((message) =>
          normalizeKirTracingMessage(message)
        ),
        riskFactor: normalizer("riskFactor", undefined, "number"),
        symptomSeverity: normalizer("symptomSeverity", undefined, "number"),
      };
    },
    parse,
    "KirTracingEntry"
  );
};

export const normalizePageKirTracingEntry = (
  source?: Page<KirTracingEntry>,
  parse?: boolean
): Page<KirTracingEntry> => {
  return normalizePage(normalizeKirTracingEntry, source, parse);
};
