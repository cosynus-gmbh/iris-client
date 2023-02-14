import {
  KirTracingAssessmentCovid19,
  KirTracingTherapyResultsCovid19,
} from "@/api";
import { normalizeData } from "@/utils/data";

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

export const normalizeKirTracingTherapyResultsCovid19Feedback = (
  source?: Partial<KirTracingTherapyResultsCovid19["feedback"]>,
  parse?: boolean
): Partial<KirTracingTherapyResultsCovid19["feedback"]> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        satisfiedWithMedicalCare: normalizer(
          "satisfiedWithMedicalCare",
          undefined
        ),
        satisfiedWithHealthDepartment: normalizer(
          "satisfiedWithHealthDepartment",
          undefined
        ),
        betterInformedByApp: normalizer("betterInformedByApp", undefined),
        suggestionsOrFeedback: normalizer("suggestionsOrFeedback", undefined),
      };
    },
    parse,
    "KirTracingTherapyResultsCovid19.feedback"
  );
};

export const normalizeKirTracingTherapyResultsCovid19Paxlovid = (
  source?: Partial<KirTracingTherapyResultsCovid19["paxlovid"]>,
  parse?: boolean
): Partial<KirTracingTherapyResultsCovid19["paxlovid"]> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        medication: normalizer("medication", undefined),
        medication_startDate: normalizer(
          "medication_startDate",
          undefined,
          "dateString"
        ),
        prescription: normalizer("prescription", undefined),
        prescription_others: normalizer("prescription_others", undefined),
        noMedication: normalizer("noMedication", undefined),
        noMedication_others: normalizer("noMedication_others", undefined),
        symptoms_endDate: normalizer("symptoms_endDate", undefined),
      };
    },
    parse,
    "KirTracingTherapyResultsCovid19.paxlovid"
  );
};

export const normalizeKirTracingTherapyResultsCovid19MedicalCare = (
  source?: Partial<KirTracingTherapyResultsCovid19["medicalCare"]>,
  parse?: boolean
): Partial<KirTracingTherapyResultsCovid19["medicalCare"]> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        treatment: normalizer("treatment", undefined),
        treatment_location: normalizer(
          "treatment_location",
          undefined,
          "array"
        ),
        hospital_inpatient: normalizer("hospital_inpatient", undefined),
        hospital_inpatient_duration: normalizer(
          "hospital_inpatient_duration",
          undefined,
          "number"
        ),
        hospital_icu: normalizer("hospital_icu", undefined),
        hospital_oxygen: normalizer("hospital_oxygen", undefined),
      };
    },
    parse,
    "KirTracingTherapyResultsCovid19.medicalCare"
  );
};

export const normalizeKirTracingTherapyResultsCovid19 = (
  source?: Partial<KirTracingTherapyResultsCovid19>,
  parse?: boolean
): Partial<KirTracingTherapyResultsCovid19> => {
  return normalizeData(
    source,
    () => {
      return {
        medicalCare: normalizeKirTracingTherapyResultsCovid19MedicalCare(
          source?.medicalCare
        ),
        paxlovid: normalizeKirTracingTherapyResultsCovid19Paxlovid(
          source?.paxlovid
        ),
        feedback: normalizeKirTracingTherapyResultsCovid19Feedback(
          source?.feedback
        ),
      };
    },
    parse,
    "KirTracingTherapyResultsCovid19"
  );
};
