import {
  HISTORY_LIMIT,
  HISTORY_STORAGE_KEY,
  addHistoryEntry,
  loadHistory,
  saveHistory,
  type CalculationHistoryEntry
} from "./calculationHistory";

describe("calculation history", () => {
  it("prepends a result and caps history size", () => {
    const previous: CalculationHistoryEntry[] = Array.from(
      { length: HISTORY_LIMIT },
      (_, index) => ({
        id: String(index),
        expression: `${index}+1`,
        result: String(index + 1)
      })
    );

    const next = addHistoryEntry(
      previous,
      { expression: "sqrt(4)", result: "2" },
      new Date("2026-08-22T12:00:00.000Z")
    );

    expect(next).toHaveLength(HISTORY_LIMIT);
    expect(next[0]).toMatchObject({ expression: "sqrt(4)", result: "2" });
    expect(next).not.toContainEqual(previous.at(-1));
  });

  it("returns an empty history for corrupted storage", () => {
    const storage = {
      getItem: vi.fn().mockReturnValue("not-json")
    };

    expect(loadHistory(storage)).toEqual([]);
  });

  it("stores and reloads valid history", () => {
    const values = new Map<string, string>();
    const storage = {
      getItem: (key: string) => values.get(key) ?? null,
      setItem: (key: string, value: string) => values.set(key, value)
    };
    const history = addHistoryEntry(
      [],
      { expression: "2^8", result: "256" },
      new Date("2026-08-22T12:00:00.000Z")
    );

    saveHistory(storage, history);

    expect(values.has(HISTORY_STORAGE_KEY)).toBe(true);
    expect(loadHistory(storage)).toEqual(history);
  });
});
