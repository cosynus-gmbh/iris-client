import authClient from "@/api-client";
import asyncAction from "@/utils/asyncAction";
import { DataQuery } from "@/api/common";
import { apiBundleProvider } from "@/utils/api";
import {
  normalizeKirTracingEntry,
  normalizePageKirTracingEntry,
} from "@/modules/kir-tracing/services/normalizer";
import {
  KirTracingBiohazardEventUpdate,
  KirTracingEntryUpdate,
  KirTracingPlace,
} from "@/api";
import { normalizeKirTracingBiohazardEvent } from "@/modules/kir-tracing/modules/biohazard/services/normalizer";
import axios from "axios";
import { parse } from "csv-parse/browser/esm/sync";

const fetchPageTracingEntry = () => {
  const action = async (query: DataQuery) => {
    return normalizePageKirTracingEntry(
      (await authClient.kirTracingEntriesGet({ params: query })).data,
      true
    );
  };
  return asyncAction(action);
};

const fetchTracingEntryDetails = () => {
  const action = async (id: string) => {
    return normalizeKirTracingEntry(
      (await authClient.kirTracingEntryDetailsGet(id)).data,
      true
    );
  };
  return asyncAction(action);
};

const patchTracingEntry = () => {
  const action = async (id: string, data: KirTracingEntryUpdate) => {
    return normalizeKirTracingEntry(
      (await authClient.kirTracingEntryPatch(id, data)).data,
      true
    );
  };
  return asyncAction(action);
};

const deleteTracingEntry = () => {
  const action = async (id: string) => {
    return await authClient.kirTracingEntryDelete(id);
  };
  return asyncAction(action);
};

const fetchUnsubmittedTracingEntryCount = () => {
  const action = async () => {
    return (await authClient.kirTracingEntriesCountUnsubmitted()).data;
  };
  return asyncAction(action);
};

const fetchBiohazardEvent = () => {
  const action = async () => {
    return normalizeKirTracingBiohazardEvent(
      (await authClient.kirTracingBiohazardEventGet()).data,
      true
    );
  };
  return asyncAction(action);
};

const patchBiohazardEvent = () => {
  const action = async (id: string, data: KirTracingBiohazardEventUpdate) => {
    return normalizeKirTracingBiohazardEvent(
      (await authClient.kirTracingBiohazardEventPatch(id, data)).data,
      true
    );
  };
  return asyncAction(action);
};

const fetchPlaces = () => {
  const action = async () => {
    const response = await axios.get<string>("/zip_coordinates.csv", {
      headers: { "Content-Type": "text/csv" },
    });
    const places: KirTracingPlace[] = parse(response.data, {
      columns: true,
      skip_empty_lines: true,
      delimiter: ";",
    });
    return places;
  };
  return asyncAction(action);
};

export const kirTracingApi = {
  fetchPageTracingEntry,
  fetchTracingEntryDetails,
  fetchUnsubmittedTracingEntryCount,
  patchTracingEntry,
  deleteTracingEntry,
  fetchBiohazardEvent,
  patchBiohazardEvent,
  fetchPlaces,
};

export const bundleKirTracingApi = apiBundleProvider(kirTracingApi);
