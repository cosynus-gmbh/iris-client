import {
  KirTracingAssessment,
  KirTracingAssessmentCovid19,
  KirTracingDisease,
  KirTracingEntry,
  KirTracingPerson,
  KirTracingStatus,
  Page,
} from "@/api";
import { normalizeData } from "@/utils/data";
import { normalizePage } from "@/common/normalizer";

export const normalizeKirTracingAssessmentCovid19VirusDetection = (
  source?: Partial<KirTracingAssessmentCovid19["virusDetection"]>,
  parse?: boolean
): Partial<KirTracingAssessmentCovid19["virusDetection"]> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        method: normalizer("method", undefined),
        date: normalizer("date", undefined, "dateString"),
      };
    },
    parse,
    "KirTracingAssessmentCovid19.virusDetection"
  );
};

export const normalizeKirTracingAssessmentCovid19ImmuneStatus = (
  source?: Partial<KirTracingAssessmentCovid19["immuneStatus"]>,
  parse?: boolean
): Partial<KirTracingAssessmentCovid19["immuneStatus"]> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        infection: normalizer("infection", undefined),
        infection_dates: normalizer("infection_dates", undefined, "array"),
        vaccinationCount: normalizer("vaccinationCount", undefined),
      };
    },
    parse,
    "KirTracingAssessmentCovid19.immuneStatus"
  );
};

export const normalizeKirTracingAssessmentCovid19Symptoms = (
  source?: Partial<KirTracingAssessmentCovid19["symptoms"]>,
  parse?: boolean
): Partial<KirTracingAssessmentCovid19["symptoms"]> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        fluLike: normalizer("fluLike", undefined),
        occurrence: normalizer("occurrence", undefined, "array"),
        occurrence_others: normalizer("occurrence_others", undefined),
        date: normalizer("date", undefined, "dateString"),
      };
    },
    parse,
    "KirTracingAssessmentCovid19.symptoms"
  );
};

export const normalizeKirTracingAssessmentCovid19Risk = (
  source?: Partial<KirTracingAssessmentCovid19["risk"]>,
  parse?: boolean
): Partial<KirTracingAssessmentCovid19["risk"]> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        age: normalizer("age", undefined),
        chronicDisease: normalizer("chronicDisease", undefined),
        chronicDisease_details: normalizer("chronicDisease_details", undefined),
        drugsImmune: normalizer("drugsImmune", undefined),
        kidneyDisease: normalizer("kidneyDisease", undefined),
        kidneyDisease_dialysis: normalizer("kidneyDisease_dialysis", undefined),
        drugs: normalizer("drugs", undefined),
        drugs_details: normalizer("drugs_details", undefined),
        fat: normalizer("fat", undefined),
        hiv: normalizer("fat", undefined),
        neurological_details: normalizer("neurological_details", undefined),
        trisomy: normalizer("trisomy", undefined),
        diseases: normalizer("diseases", undefined, "array"),
      };
    },
    parse,
    "KirTracingAssessmentCovid19.risk"
  );
};

export const normalizeKirTracingAssessmentCovid19MedicalCare = (
  source?: Partial<KirTracingAssessmentCovid19["medicalCare"]>,
  parse?: boolean
): Partial<KirTracingAssessmentCovid19["medicalCare"]> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        familyDoctorInformed: normalizer("familyDoctorInformed", undefined),
        medicalTherapy: normalizer("medicalTherapy", undefined),
        interest: normalizer("interest", undefined),
        interest_phone: normalizer("interest_phone", undefined),
      };
    },
    parse,
    "KirTracingAssessmentCovid19.medicalCare"
  );
};

export const normalizeKirTracingAssessmentCovid19 = (
  source?: Partial<KirTracingAssessmentCovid19>,
  parse?: boolean
): Partial<KirTracingAssessmentCovid19> => {
  return normalizeData(
    source,
    () => {
      return {
        virusDetection: normalizeKirTracingAssessmentCovid19VirusDetection(
          source?.virusDetection
        ),
        immuneStatus: normalizeKirTracingAssessmentCovid19ImmuneStatus(
          source?.immuneStatus
        ),
        symptoms: normalizeKirTracingAssessmentCovid19Symptoms(
          source?.symptoms
        ),
        risk: normalizeKirTracingAssessmentCovid19Risk(source?.risk),
        medicalCare: normalizeKirTracingAssessmentCovid19MedicalCare(
          source?.medicalCare
        ),
      };
    },
    parse,
    "KirTracingAssessmentCovid19"
  );
};

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
      const targetDisease = normalizer(
        "targetDisease",
        KirTracingDisease.COVID_19
      );
      return {
        id: normalizer("id", undefined),
        targetDisease: targetDisease,
        assessment: normalizeKirTracingAssessment<typeof targetDisease>(
          source?.assessment,
          targetDisease
        ),
        therapyResults: normalizer("therapyResults", undefined, "any"),
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
