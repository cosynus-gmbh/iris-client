import {
  KirTracingAssessmentThresholds,
  KirTracingRiskFactorThresholds,
  KirTracingStatus,
} from "@/api";

import { isFalse, isTrue, join } from "@/utils/misc";

const formValueLabels: Record<string, string> = {
  0: "Nein",
  1: "Ja",
  ffp2: "FFP2-Maske",
  mouthNoseProtection: "Mund-Nasen-Schutz",
  none: "Nein",
  "locationType.atHome": "Zuhause",
  "locationType.friendsPlace": "Der Wohnort von Bekannten",
  "locationType.other": "Ein öffentliches Gebäude oder Sonstiges",
  "healthProblems.none": "keine Beschwerden",
  "healthProblems.beforeTheEvent":
    "Beschwerden bereits vor dem Zeitpunkt des Ereignisses",
  "healthProblems.afterTheEvent":
    "Beschwerden seit dem Zeitpunkt des Ereignisses",
  "breathingDifficulties.breathing": "Schwierigkeiten beim Ein- oder Ausatmen",
  "breathingDifficulties.cough": "Husten (trocken oder mit Auswurf)",
  "bodyTemperature.low": "<36 – 37,5 Grad",
  "bodyTemperature.medium": "37,5 – 38 Grad",
  "bodyTemperature.high": ">38 Grad",
  nausea: "Übelkeit",
  vomiting: "Erbrechen",
  "unusualBleeding.nose": "Nasenbluten",
  "unusualBleeding.gums": "Zahnfleischbluten",
  "unusualBleeding.stool": "Blut im Stuhl",
  "unusualBleeding.urine": "Blut im Urin",
  "unusualBleeding.vomit": "Blutiges Erbrechen",
  "unusualBleeding.other": "Sonstiges",
  "chronicRespiratoryDisease.bronchialAsthma": "Asthma bronchiale",
  "chronicRespiratoryDisease.copd": "COPD / chronische Bronchitis",
  "chronicRespiratoryDisease.other": "Sonstige",
  "resourceDemand.medicalCare": "Medizinische Versorgung",
  "resourceDemand.drugs": "Medikamente",
  "resourceDemand.groceries": "Lebensmittel",
  "resourceDemand.protectiveGear": "Schutzausrüstung",
  "resourceDemand.other": "Sonstiges",
};

const valueLabel = (
  value: string | undefined,
  prefix?: string,
  fallback?: string
): string => {
  return value
    ? formValueLabels[join([prefix, value], ".")] ??
        formValueLabels[value] ??
        value
    : fallback ?? "";
};

const getStatusName = (status?: string): string => {
  switch (status) {
    case KirTracingStatus.NEW:
      return "Neu";
    case KirTracingStatus.CLOSED:
      return "Abgeschlossen";
    case KirTracingStatus.DATA_CHANGED:
      return "Daten geändert";
    case KirTracingStatus.PERSON_CONTACTED:
      return "Person kontaktiert";
    case KirTracingStatus.AID_REQUEST_RECEIVED:
      return "Ressourcenanfrage erhalten";
    case KirTracingStatus.MESSAGE_RECEIVED:
      return "Nachricht erhalten";
    default:
      return status ?? "Unbekannt";
  }
};

const getStatusButtonLabel = (status?: string): string => {
  switch (status) {
    case KirTracingStatus.CLOSED:
      return "Fall abgeschlossen";
    default:
      return getStatusName(status);
  }
};

const getStatusColor = (status?: string): string => {
  switch (status) {
    case KirTracingStatus.NEW:
      return "red";
    case KirTracingStatus.CLOSED:
      return "black";
    case KirTracingStatus.DATA_CHANGED:
      return "blue";
    case KirTracingStatus.PERSON_CONTACTED:
      return "green";
    case KirTracingStatus.AID_REQUEST_RECEIVED:
      return "red";
    case KirTracingStatus.MESSAGE_RECEIVED:
      return "blue";
    default:
      return "gray";
  }
};

const getThreshold = (
  value: number | undefined
): keyof KirTracingAssessmentThresholds | undefined => {
  const thresholds = KirTracingRiskFactorThresholds;
  if (value === undefined) return;
  if (value > thresholds.high) {
    return "high";
  }
  if (value >= thresholds.medium) {
    return "medium";
  }
  if (value >= thresholds.low) {
    return "low";
  }
  if (value >= thresholds.none) {
    return "none";
  }
};

const withInlineDetails = (
  value: string,
  details: string | undefined | null
): string => {
  return join([value, details], ": ");
};

const withCount = (
  value: string,
  count: string | number | undefined | null
): string => {
  return !count ? value : `${value} (${count}x)`;
};

interface YesNoWithDetailsOptions {
  yesDetails?: string | undefined;
  noDetails?: string | undefined;
  includeYesNo?: boolean | undefined;
}

const yesNoWithDetails = (
  yesNo: string | number | boolean | undefined,
  options: YesNoWithDetailsOptions
): string | undefined => {
  const { yesDetails, noDetails, includeYesNo } = options;
  if (isTrue(yesNo)) {
    if (includeYesNo !== false) {
      return withInlineDetails(valueLabel("1"), yesDetails);
    }
    return yesDetails ?? valueLabel("1");
  }
  if (isFalse(yesNo)) {
    if (includeYesNo !== false) {
      return withInlineDetails(valueLabel("0"), noDetails);
    }
    return noDetails ?? valueLabel("0");
  }
  return;
};

interface YesNoWithDetailsListOptions {
  mapper: (value: string | undefined) => string | undefined;
  yesDetails?: Array<string | undefined>;
  noDetails?: Array<string | undefined>;
}

const yesNoWithDetailsList = (
  yesNo: string | number | boolean | undefined,
  options: YesNoWithDetailsListOptions
): Array<string | undefined> => {
  const { mapper, yesDetails, noDetails } = options;
  if (isTrue(yesNo)) {
    return yesDetails?.map(mapper).filter((v) => v) ?? [valueLabel("1")];
  }
  if (isFalse(yesNo)) {
    return noDetails?.map(mapper).filter((v) => v) ?? [valueLabel("0")];
  }
  return [];
};

const getRiskFactorLabel = (
  value: number | undefined,
  short?: boolean
): string => {
  const label = () => {
    switch (getThreshold(value)) {
      case "high":
        return short ? "Hoch" : "Hohes Risiko";
      case "medium":
        return short ? "Mittel" : "Mittleres Risiko";
      case "low":
        return short ? "Niedrig" : "Niedriges Risiko";
      case "none":
        return short ? "Kein" : "Kein Risiko";
      default:
        return "";
    }
  };
  return thresholdLabel(label(), value);
};

const thresholdLabel = (label: string, value: number | undefined): string => {
  return join([label, value !== undefined ? `(${value})` : undefined], " ");
};

const getThresholdColor = (
  threshold?: keyof KirTracingAssessmentThresholds
): string => {
  switch (threshold) {
    case "high":
      return "red";
    case "medium":
      return "yellow";
    case "low":
      return "blue";
    case "none":
    default:
      return "green";
  }
};

const kirTracingConstants = {
  getStatusName,
  getStatusButtonLabel,
  getStatusColor,
  valueLabel,
  getRiskFactorLabel,
  getThresholdColor,
  withInlineDetails,
  withCount,
  yesNoWithDetails,
  yesNoWithDetailsList,
};

export default kirTracingConstants;
