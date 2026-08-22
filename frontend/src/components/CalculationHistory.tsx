import type { CalculationHistoryEntry } from "../history/calculationHistory";

type Props = {
  history: CalculationHistoryEntry[];
  onReuse: (expression: string) => void;
  onClear: () => void;
};

export function CalculationHistory({ history, onReuse, onClear }: Props) {
  return (
    <section className="history-card" aria-labelledby="history-title">
      <div className="section-heading history-heading">
        <div>
          <span className="eyebrow">Local only</span>
          <h2 id="history-title">Recent calculations</h2>
        </div>
        {history.length > 0 && (
          <button className="text-button" type="button" onClick={onClear}>
            Clear
          </button>
        )}
      </div>

      {history.length === 0 ? (
        <p className="empty-state">
          Successful calculations will appear here in this browser.
        </p>
      ) : (
        <ol className="history-list">
          {history.map((entry) => (
            <li key={entry.id}>
              <button
                type="button"
                className="history-item"
                onClick={() => onReuse(entry.expression)}
                title="Reuse this expression"
              >
                <span className="history-expression">{entry.expression}</span>
                <span className="history-result">= {entry.result}</span>
              </button>
            </li>
          ))}
        </ol>
      )}
    </section>
  );
}
