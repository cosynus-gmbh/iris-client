import { flattenDeep } from "lodash";

export const join = (arr: Array<unknown>, separator: string): string => {
  return flattenDeep(arr)
    .filter((n) => n)
    .join(separator);
};

export const getValidPhoneNumber = (
  ...numbers: Array<string | undefined>
): string | undefined => {
  const validNumber = numbers.find(isPossiblePhoneNumber);
  return validNumber ? validNumber : numbers[0];
};

const isPossiblePhoneNumber = (phoneNumber?: string): boolean => {
  if (!phoneNumber) return false;
  const number = phoneNumber.replace(/[\s\-_+#*.,:;()/|]/g, "");
  return /^\d+$/.test(number);
};

export const isTrue = (
  value: string | number | boolean | undefined,
  checkNumber?: boolean
) =>
  value === true ||
  value === "true" ||
  (checkNumber !== false ? value === 1 || value === "1" : false);

export const isFalse = (
  value: string | number | boolean | undefined,
  checkNumber?: boolean
) =>
  value === false ||
  value === "false" ||
  (checkNumber !== false ? value === 0 || value === "0" : false);
