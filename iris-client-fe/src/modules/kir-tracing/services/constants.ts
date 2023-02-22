import { KirTracingDisease, KirTracingStatus } from "@/api";

import { join } from "@/utils/misc";

const formValueLabels: Record<string, string> = {
  pcr: "PCR",
  antigen: "Antigentest",
  0: "Nein",
  1: "Ja",
  cough: "Husten",
  cold: "Schnupfen",
  fever: "Fieber (>38 Grad Celsius)",
  headache: "Kopfschmerzen",
  breathlessness: "Atemnot",
  weakness: "Schwächegefühl oder Unwohlsein",
  lossOfSmellOrTaste: "Geruchs- oder Geschmacksverlust",
  others: "Andere",
  "vaccinationCount.0": "Keine",
  "vaccinationCount.gt4": "Mehr als 4",
  lt30: "Unter 30 Jahre",
  "30-49": "30 bis 49",
  "50-59": "50 bis 59",
  "60-69": "60 bis 69",
  "70-79": "70 bis 79",
  gte80: "80 oder älter",
  cardiovascular: "Herzkreislauferkrankungen",
  lung: "Lungenerkrankungen",
  diabetes: "Diabetes Typ 1 oder 2",
  liver: "Lebererkrankungen",
  neurological: "Neurologisch-psychiatrische Erkrankung",
  "familyDoctorInformed.supervised": "Ja, Patient:in wird medizinisch betreut",
  "familyDoctorInformed.notInformed": "Nein",
  "familyDoctorInformed.noFamilyDoctor":
    "Patient:in hat keinen Hausarzt/Hausärztin",
  "medicalTherapy.informed": "Ja, Patient:in wurde informiert",
  "medicalTherapy.notInformed": "Nein, Patient:in weiß nichts darüber",
  "medicalTherapy.unobtainable":
    "Patient:in weiß von dem Medikament, weiß aber nicht, wie er/sie dieses erhalten kann",
  "treatment_location.familyDoctor": "Hausarzt/Hausärztin",
  "treatment_location.specialist": "Bei einem Facharzt/einer Fachärztin",
  "treatment_location.healthDepartment": "Im Gesundheitsamt",
  "treatment_location.hospital": "Im Krankenhaus",
  "noMedication.patientDeclined": "Weil Patient:in dies nicht wollte",
  "noMedication.notRecommended":
    "Weil Patient:in eine Krankheit habe oder ein Medikament einnehme, bei der eine Paxlovid Einnahme nicht empfohlen wird",
  "noMedication.noPrescription": "Weil es Patient:in nicht verschrieben wurde",
  familyDoctor: "Hausarzt/Hausärztin",
  healthDepartment: "Gesundheitsamt",
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
    case KirTracingStatus.DONE:
      return "Abgeschlossen";
    case KirTracingStatus.DATA_CHANGED:
      return "Daten geändert";
    case KirTracingStatus.PERSON_CONTACTED:
      return "Person kontaktiert";
    case KirTracingStatus.THERAPY_RESULTS_RECEIVED:
      return "Therapieergebnisse erhalten";
    case KirTracingStatus.MESSAGE_RECEIVED:
      return "Nachricht erhalten";
    default:
      return status ?? "Unbekannt";
  }
};

const getStatusButtonLabel = (status?: string): string => {
  switch (status) {
    case KirTracingStatus.DONE:
      return "Fall abgeschlossen";
    default:
      return getStatusName(status);
  }
};

const getStatusColor = (status?: string): string => {
  switch (status) {
    case KirTracingStatus.NEW:
      return "red";
    case KirTracingStatus.DONE:
      return "black";
    case KirTracingStatus.DATA_CHANGED:
      return "blue";
    case KirTracingStatus.PERSON_CONTACTED:
      return "green";
    case KirTracingStatus.THERAPY_RESULTS_RECEIVED:
      return "red";
    case KirTracingStatus.MESSAGE_RECEIVED:
      return "blue";
    default:
      return "gray";
  }
};

const getDisease = (type?: string): string => {
  switch (type) {
    case KirTracingDisease.COVID_19:
      return "Covid-19";
    default:
      return KirTracingDisease.COVID_19;
  }
};

const kirTracingConstants = {
  getStatusName,
  getStatusButtonLabel,
  getStatusColor,
  getDisease,
  valueLabel,
};

export default kirTracingConstants;
