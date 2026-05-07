export function truncate(text?: string, maxLength: number = 15): string {
  if (!text) return "";

  return text.length > maxLength
    ? text.slice(0, maxLength) + "..."
    : text;
}