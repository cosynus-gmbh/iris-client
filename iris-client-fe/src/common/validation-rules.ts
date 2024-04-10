import config from "@/config";
import dayjs from "@/utils/date";
import { some } from "lodash";
import _get from "lodash/get";
import _isFinite from "lodash/isFinite";
import { GeoLocation } from "@/api";

export type ValidationRule<T = never> = (value: T) => string | boolean;

export type FormValidationRules<T = Record<string, unknown>> = {
  [K in keyof T]: FormValidationRules<T[K]> | ValidationRule[];
};

const minLength =
  (min: number) =>
  (v?: string): string | boolean => {
    if (typeof v !== "string") return true; // we only check the length for strings
    return v.length <= 0 || v.length >= min || `Mindestens ${min} Zeichen`;
  };

const maxLength =
  (max: number) =>
  (v?: string): string | boolean => {
    if (typeof v !== "string") return true; // we only check the length for strings
    return v.length <= max || `Höchstens ${max} Zeichen`;
  };

const password = (v: string): string | boolean => {
  if (!v || config.passwordRegExp.test(v)) return true;
  return `Bitte geben Sie ein gültiges Passwort an: \n${config.passwordRules}`;
};

const defined = (v: unknown): string | boolean => {
  if (Array.isArray(v) && v.length <= 0) return "Pflichtfeld";
  return !!v || "Pflichtfeld";
};

const location = (v: unknown): string | boolean =>
  !!v || "Bitte wählen Sie einen Ereignisort aus";

const geoLocation = (v: GeoLocation | undefined): string | boolean => {
  const latitude = _get(v, "latitude");
  const longitude = _get(v, "longitude");
  if (latitude && longitude) {
    if (sanitised(`${latitude}`) && sanitised(`${longitude}`)) {
      return true;
    }
  }
  return `Bitte geben Sie eine gültige Geolocation an`;
};

const positiveNumber = (v: unknown): string | boolean => {
  if (!v) return true;
  const value = Number(`${v}`);
  return (
    (_isFinite(value) && value > 0) ||
    "Bitte geben Sie eine gültige Zahl > 0 an"
  );
};

const sanitised = (v?: string): string | boolean => {
  if (typeof v !== "string") return true; // we only check the length for strings
  if (v.length <= 0) return true;

  if (v.includes("<script")) {
    return "Aus Sicherheitsgründen ist die Sequenz <script nicht erlaubt. Bitte modifizieren Sie die Eingabe.";
  }

  if (v.includes("SELECT") && v.includes("FROM")) {
    return "Aus Sicherheitsgründen ist die Sequenz SELECT .... FROM ... nicht erlaubt. Bitte modifizieren Sie die Eingabe.";
  }

  if (
    startMatches(v, [
      "=",
      "<",
      ">",
      "!",
      '"',
      "§",
      "$",
      "%",
      "&",
      "/",
      "(",
      ")",
      "?",
      "´",
      "`",
      "¿",
      "≠",
      "¯",
      "}",
      "·",
      "{",
      "˜",
      "\\",
      "]",
      "^",
      "ﬁ",
      "[",
      "¢",
      "¶",
      "“",
      "¡",
      "¬",
      "”",
      "#",
      "£",
      "+",
      "*",
      "±",
      "",
      "‘",
      "’",
      "'",
      "-",
      "_",
      ".",
      ":",
      "…",
      "÷",
      "∞",
      ";",
      "˛",
      "æ",
      "Æ",
      "œ",
      "Œ",
      "@",
      "•",
      "°",
      "„",
    ])
  ) {
    return "Aus Sicherheitsgründen ist ein Spezialcharakter nicht als erstes Symbol erlaubt. Bitte modifizieren Sie die Eingabe.";
  }

  return true;
};

const startMatches = (query: string, match: string[]): boolean => {
  return some(match, (item) => query.startsWith(item));
};

const anyMatches = (query: string, match: string[]): boolean => {
  return some(match, (item) => query.includes(item));
};

const nameConventions = (v?: string): string | boolean => {
  if (typeof v !== "string") return true; // we only check strings
  if (
    anyMatches(v, [
      "=",
      "<",
      ">",
      "!",
      '"',
      "§",
      "$",
      "%",
      "&",
      "/",
      "(",
      ")",
      "?",
      "´",
      "`",
      "¿",
      "≠",
      "¯",
      "}",
      "·",
      "{",
      "˜",
      "\\",
      "]",
      "^",
      "ﬁ",
      "[",
      "¢",
      "¶",
      "“",
      "¡",
      "¬",
      "”",
      "#",
      "£",
      "+",
      "*",
      "±",
      "",
      "_",
      ":",
      "…",
      "÷",
      "∞",
      ";",
      "˛",
      "æ",
      "Æ",
      "œ",
      "Œ",
      "@",
      "•",
      "°",
      "„",
    ])
  ) {
    return "Aus Sicherheitsgründen ist die Verwendung von Spezialcharakter in Namen eingeschränkt.";
  }

  return true;
};

const dateStart = (v: string): string | boolean => {
  return (
    dayjs(v).isSameOrBefore(dayjs(), "minute") ||
    "Bitte geben Sie einen Zeitpunkt in der Vergangenheit an"
  );
};

const dateEnd =
  (start: string | Date | undefined) =>
  (v: string | undefined): string | boolean => {
    if (!start || !v) return true;
    return (
      dayjs(v).isSameOrAfter(dayjs(start), "minute") ||
      "Bitte geben Sie einen Zeitpunkt an, der nach dem Beginn liegt"
    );
  };

const rules = {
  defined,
  minLength,
  maxLength,
  password,
  sanitised,
  nameConventions,
  dateStart,
  dateEnd,
  location,
  geoLocation,
  positiveNumber,
};

export default rules;
