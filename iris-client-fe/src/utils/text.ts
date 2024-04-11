export const parseText = (
  value: string | number | undefined | null,
  fallback?: string
): string => {
  const fallbackValue = fallback ?? "";
  if (!value) return fallbackValue;
  const text = `${value}`.trim();
  return text.length > 0 ? text : fallbackValue;
};
