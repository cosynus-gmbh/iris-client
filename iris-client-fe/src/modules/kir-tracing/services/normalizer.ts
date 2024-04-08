import {
  KirTracingEntry,
  KirTracingMessage,
  KirTracingPerson,
  KirTracingStatus,
  Page,
} from "@/api";
import { normalizeData } from "@/utils/data";
import { normalizePage } from "@/common/normalizer";
import {
  normalizeKirTracingAssessment,
  normalizeKirTracingKirAidRequest,
} from "@/modules/kir-tracing/modules/biohazard/services/normalizer";

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
      const messages = normalizer("messages", undefined, "array") || [];
      return {
        id: normalizer("id"),
        assessment: normalizeKirTracingAssessment(source?.assessment),
        aidRequest: normalizeKirTracingKirAidRequest(source?.aidRequest),
        person: normalizeKirTracingPerson(source?.person),
        status: normalizer("status", KirTracingStatus.NEW),
        createdAt: normalizer("createdAt", undefined, "dateString"),
        messages: messages.map((message) =>
          normalizeKirTracingMessage(message)
        ),
        riskFactor: normalizer("riskFactor", undefined, "number"),
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
