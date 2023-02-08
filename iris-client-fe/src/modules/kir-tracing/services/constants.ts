import {
  KirTracingDisease,
  KirTracingStatus,
  VaccinationExtendedStatus,
} from "@/api";

const getStatusName = (status?: string): string => {
  switch (status) {
    case KirTracingStatus.NEW:
      return "Neu";
    case KirTracingStatus.DONE:
      return "Abgeschlossen";
    case KirTracingStatus.DATA_CHANGED:
      return "Geänderte Daten";
    case KirTracingStatus.PERSON_CONTACTED:
      return "Person kontaktiert";
    case KirTracingStatus.THERAPY_RESULTS_RECEIVED:
      return "Therapieergebnisse erhalten";
    default:
      return "Unbekannt";
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
  getDisease,
};

export default kirTracingConstants;
