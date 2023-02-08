import {
  KirTracingDisease,
  KirTracingEntry,
  KirTracingPerson,
  KirTracingStatus,
  Page,
} from "@/api";
import { normalizeData } from "@/utils/data";
import { normalizePage } from "@/common/normalizer";

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
export const normalizeKirTracingEntry = (
  source?: KirTracingEntry,
  parse?: boolean
): KirTracingEntry => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        id: normalizer("id", undefined),
        targetDisease: normalizer("targetDisease", KirTracingDisease.COVID_19),
        assessment: normalizer("assessment", undefined),
        therapyResults: normalizer("therapyResults", undefined),
        person: normalizeKirTracingPerson(source?.person),
        status: normalizer("status", KirTracingStatus.NEW),
        createdAt: normalizer("createdAt", undefined, "dateString"),
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
