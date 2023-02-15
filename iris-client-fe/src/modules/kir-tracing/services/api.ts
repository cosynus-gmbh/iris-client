import authClient from "@/api-client";
import asyncAction from "@/utils/asyncAction";
import { DataQuery } from "@/api/common";
import { apiBundleProvider } from "@/utils/api";
import {
  normalizeKirTracingEntry,
  normalizePageKirTracingEntry,
} from "@/modules/kir-tracing/services/normalizer";
import { KirTracingEntryUpdate } from "@/api";

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

export const kirTracingApi = {
  fetchPageTracingEntry,
  fetchTracingEntryDetails,
  fetchUnsubmittedTracingEntryCount,
  patchTracingEntry,
  deleteTracingEntry,
};

export const bundleKirTracingApi = apiBundleProvider(kirTracingApi);
