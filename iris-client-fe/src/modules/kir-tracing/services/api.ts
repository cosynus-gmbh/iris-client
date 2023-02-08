import authClient from "@/api-client";
import asyncAction from "@/utils/asyncAction";
import { DataQuery } from "@/api/common";
import { apiBundleProvider } from "@/utils/api";
import { normalizePageKirTracingEntry } from "@/modules/kir-tracing/services/normalizer";

const fetchPageTracingEntry = () => {
  const action = async (query: DataQuery) => {
    return normalizePageKirTracingEntry(
      (await authClient.kirTracingEntriesGet({ params: query })).data,
      true
    );
  };
  return asyncAction(action);
};

export const kirTracingApi = {
  fetchPageTracingEntry,
};

export const bundleKirTracingApi = apiBundleProvider(kirTracingApi);
