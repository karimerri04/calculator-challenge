import type { CalculationResponse } from "../api/calculations";

export const HISTORY_LIMIT = 8;
export const HISTORY_STORAGE_KEY = "calculator-challenge.history.v1";

export type CalculationHistoryEntry = CalculationResponse & {
  id: string;
};

export function addHistoryEntry(
  history: CalculationHistoryEntry[],
  calculation: CalculationResponse,
  now = new Date()
): CalculationHistoryEntry[] {
  const entry: CalculationHistoryEntry = {
    id: `${now.getTime()}-${calculation.expression}-${calculation.result}`,
    expression: calculation.expression,
    result: calculation.result
  };

  return [entry, ...history].slice(0, HISTORY_LIMIT);
}

export function loadHistory(storage: Pick<Storage, "getItem">): CalculationHistoryEntry[] {
  const raw = storage.getItem(HISTORY_STORAGE_KEY);

  if (!raw) {
    return [];
  }

  try {
    const parsed = JSON.parse(raw) as unknown;
    return isHistory(parsed) ? parsed.slice(0, HISTORY_LIMIT) : [];
  } catch {
    // Corrupted browser storage must not prevent the calculator from starting.
    return [];
  }
}

export function saveHistory(
  storage: Pick<Storage, "setItem">,
  history: CalculationHistoryEntry[]
): void {
  storage.setItem(HISTORY_STORAGE_KEY, JSON.stringify(history));
}

function isHistory(value: unknown): value is CalculationHistoryEntry[] {
  return (
    Array.isArray(value) &&
    value.every(
      (entry) =>
        typeof entry === "object" &&
        entry !== null &&
        typeof (entry as CalculationHistoryEntry).id === "string" &&
        typeof (entry as CalculationHistoryEntry).expression === "string" &&
        typeof (entry as CalculationHistoryEntry).result === "string"
    )
  );
}
