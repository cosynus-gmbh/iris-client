import { normalizeData } from "@/utils/data";
import {
  KirTracingAidRequest,
  KirTracingAidRequestData,
  KirTracingAssessment,
  Place,
} from "@/api";

export const normalizeKirTracingPlace = (
  source?: Partial<Place>,
  parse?: boolean
): Partial<Place> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        loc_id: normalizer("loc_id"),
        city: normalizer("city"),
        postcode: normalizer("postcode"),
        latitude: normalizer("latitude"),
        longitude: normalizer("longitude"),
      };
    },
    parse,
    "KirTracingAssessment.personalData"
  );
};

export const normalizeKirTracingAssessmentPersonalData = (
  source?: Partial<KirTracingAssessment["personalData"]>,
  parse?: boolean
): Partial<KirTracingAssessment["personalData"]> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        firstName: normalizer("firstName"),
        lastName: normalizer("lastName"),
        email: normalizer("email"),
        phone: normalizer("phone"),
      };
    },
    parse,
    "KirTracingAssessment.personalData"
  );
};

export const normalizeKirTracingAssessmentExposition = (
  source?: Partial<KirTracingAssessment["exposition"]>,
  parse?: boolean
): Partial<KirTracingAssessment["exposition"]> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        location: normalizeKirTracingPlace(source?.location),
        dateTime: normalizer("dateTime", undefined, "dateString"),
        locationNotes: normalizer("locationNotes"),
        protectiveMask: normalizer("protectiveMask"),
      };
    },
    parse,
    "KirTracingAssessment.exposition"
  );
};

export const normalizeKirTracingAssessmentCurrentLocation = (
  source?: Partial<KirTracingAssessment["currentLocation"]>,
  parse?: boolean
): Partial<KirTracingAssessment["currentLocation"]> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        location: normalizeKirTracingPlace(source?.location),
        dateTime: normalizer("dateTime", undefined, "dateString"),
        locationType: normalizer("locationType"),
        locationType_other: normalizer("locationType_other"),
        locationType_other_details: normalizer("locationType_other_details"),
        numberOfPeople: normalizer("numberOfPeople"),
        vulnerablePeople: normalizer("vulnerablePeople"),
        vulnerablePeople_yes_details: normalizer(
          "vulnerablePeople_yes_details"
        ),
        spatialSeparationPossible: normalizer("spatialSeparationPossible"),
        spatialSeparationPossible_details: normalizer(
          "spatialSeparationPossible_details"
        ),
      };
    },
    parse,
    "KirTracingAssessment.currentLocation"
  );
};

export const normalizeKirTracingAssessmentSymptoms = (
  source?: Partial<KirTracingAssessment["symptoms"]>,
  parse?: boolean
): Partial<KirTracingAssessment["symptoms"]> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        healthProblems: normalizer("healthProblems"),
        healthProblems_afterTheEvent_dateTime: normalizer(
          "healthProblems_afterTheEvent_dateTime",
          undefined,
          "dateString"
        ),
        breathingDifficulties: normalizer("breathingDifficulties"),
        breathingDifficulties_yes_details: normalizer(
          "breathingDifficulties_yes_details",
          undefined,
          "array"
        ),
        bodyTemperature: normalizer("bodyTemperature"),
        nauseaOrVomiting: normalizer("nauseaOrVomiting"),
        nauseaOrVomiting_yes_details: normalizer(
          "nauseaOrVomiting_yes_details",
          undefined,
          "array"
        ),
        nauseaOrVomiting_yes_details_vomiting_numberOfTimes: normalizer(
          "nauseaOrVomiting_yes_details_vomiting_numberOfTimes"
        ),
        diarrhea: normalizer("diarrhea", undefined),
        diarrhea_yes_numberOfTimes: normalizer("diarrhea_yes_numberOfTimes"),
        rash: normalizer("rash", undefined),
        rash_yes_details: normalizer("rash_yes_details", undefined),
        unusualBleeding: normalizer("unusualBleeding", undefined),
        unusualBleeding_yes_details: normalizer(
          "unusualBleeding_yes_details",
          undefined,
          "array"
        ),
        unusualBleeding_yes_details_other_details: normalizer(
          "unusualBleeding_yes_details_other_details"
        ),
        healthConditionInfo: normalizer("healthConditionInfo"),
      };
    },
    parse,
    "KirTracingAssessment.symptoms"
  );
};

export const normalizeKirTracingAssessmentPreviousIllness = (
  source?: Partial<KirTracingAssessment["previousIllness"]>,
  parse?: boolean
): Partial<KirTracingAssessment["previousIllness"]> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        chronicDisease: normalizer("chronicDisease"),
        chronicRespiratoryDisease: normalizer("chronicRespiratoryDisease"),
        chronicRespiratoryDisease_yes_details: normalizer(
          "chronicRespiratoryDisease_yes_details",
          undefined,
          "array"
        ),
        chronicRespiratoryDisease_yes_details_other_details: normalizer(
          "chronicRespiratoryDisease_yes_details_other_details"
        ),
        cardiovascularDisease: normalizer("cardiovascularDisease"),
        cardiovascularDisease_yes_details: normalizer(
          "cardiovascularDisease_yes_details"
        ),
        kidneyDisease: normalizer("kidneyDisease"),
        kidneyDisease_yes_details: normalizer("kidneyDisease_yes_details"),
        diabetes: normalizer("diabetes"),
        immuneDeficiency: normalizer("immuneDeficiency"),
        immuneDeficiency_yes_details: normalizer(
          "immuneDeficiency_yes_details"
        ),
        medication: normalizer("medication"),
        medication_yes_details: normalizer("medication_yes_details"),
      };
    },
    parse,
    "KirTracingAssessment.previousIllness"
  );
};

export const normalizeKirTracingAssessment = (
  source?: Partial<KirTracingAssessment>,
  parse?: boolean
): Partial<KirTracingAssessment> => {
  return normalizeData(
    source,
    () => {
      return {
        personalData: normalizeKirTracingAssessmentPersonalData(
          source?.personalData
        ),
        exposition: normalizeKirTracingAssessmentExposition(source?.exposition),
        currentLocation: normalizeKirTracingAssessmentCurrentLocation(
          source?.currentLocation
        ),
        symptoms: normalizeKirTracingAssessmentSymptoms(source?.symptoms),
        previousIllness: normalizeKirTracingAssessmentPreviousIllness(
          source?.previousIllness
        ),
      };
    },
    parse,
    "KirTracingAssessment"
  );
};

export const normalizeKirTracingKirAidRequestResources = (
  source?: Partial<KirTracingAidRequestData["resources"]>,
  parse?: boolean
): Partial<KirTracingAidRequestData["resources"]> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        resourceDemand: normalizer("resourceDemand", undefined, "array"),
        resourceDemand_other_details: normalizer(
          "resourceDemand_other_details"
        ),
        resourceUsage: normalizer("resourceUsage"),
      };
    },
    parse,
    "KirTracingAidRequestData.resources"
  );
};

export const normalizeKirTracingKirAidRequestData = (
  source?: Partial<KirTracingAidRequestData>,
  parse?: boolean
): Partial<KirTracingAidRequestData> => {
  return normalizeData(
    source,
    () => {
      return {
        resources: normalizeKirTracingKirAidRequestResources(source?.resources),
      };
    },
    parse,
    "KirTracingAidRequestData"
  );
};

export const normalizeKirTracingKirAidRequest = (
  source?: Partial<KirTracingAidRequest>,
  parse?: boolean
): Partial<KirTracingAidRequest> => {
  return normalizeData(
    source,
    (normalizer) => {
      return {
        data: normalizeKirTracingKirAidRequestData(source?.data),
        createdAt: normalizer("createdAt", undefined, "dateString"),
      };
    },
    parse,
    "KirTracingAidRequest"
  );
};
