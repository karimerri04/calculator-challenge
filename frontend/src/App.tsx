import { FormEvent, useMemo, useState } from "react";
import {
  CalculationApiError,
  calculateExpression
} from "./api/calculations";
import { CalculationHistory } from "./components/CalculationHistory";
import { ExpressionExamples } from "./components/ExpressionExamples";
import {
  HISTORY_STORAGE_KEY,
  addHistoryEntry,
  loadHistory,
  saveHistory,
  type CalculationHistoryEntry
} from "./history/calculationHistory";

const SYMBOLS = [
  { label: "+", value: "+" },
  { label: "−", value: "-" },
  { label: "×", value: "*" },
  { label: "÷", value: "/" },
  { label: "^", value: "^" },
  { label: "(", value: "(" },
  { label: ")", value: ")" },
  { label: "sqrt", value: "sqrt(" }
];

export default function App() {
  const [expression, setExpression] = useState("sqrt(4) + 2^3");
  const [result, setResult] = useState<string | null>(null);
  const [error, setError] = useState<CalculationApiError | null>(null);
  const [isCalculating, setIsCalculating] = useState(false);
  const [history, setHistory] = useState<CalculationHistoryEntry[]>(() =>
    loadHistory(window.localStorage)
  );

  const errorTitle = useMemo(() => formatErrorCode(error?.code), [error]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (expression.trim().length === 0 || isCalculating) {
      return;
    }

    setIsCalculating(true);
    setError(null);

    try {
      const calculation = await calculateExpression(expression);
      setResult(calculation.result);

      const nextHistory = addHistoryEntry(history, calculation);
      setHistory(nextHistory);
      saveHistory(window.localStorage, nextHistory);
    } catch (caught) {
      setResult(null);
      setError(
        caught instanceof CalculationApiError
          ? caught
          : new CalculationApiError("UNKNOWN_ERROR", "Unexpected client error.")
      );
    } finally {
      setIsCalculating(false);
    }
  }

  function chooseExpression(nextExpression: string) {
    setExpression(nextExpression);
    setResult(null);
    setError(null);
  }

  function appendSymbol(symbol: string) {
    setExpression((current) => `${current}${symbol}`);
    setResult(null);
    setError(null);
  }

  function clearHistory() {
    setHistory([]);
    window.localStorage.removeItem(HISTORY_STORAGE_KEY);
  }

  return (
    <main className="app-shell">
      <header className="hero">
        <div>
          <span className="hero-badge">Java 21 · Parser · AST · BigDecimal</span>
          <h1>Calculator Challenge</h1>
          <p>
            Enter an expression. The browser sends it to the Java calculator;
            parsing and arithmetic stay entirely on the backend.
          </p>
        </div>
        <div className="architecture-pill" aria-label="Calculation pipeline">
          <span>String</span>
          <span aria-hidden="true">→</span>
          <span>Lexer</span>
          <span aria-hidden="true">→</span>
          <span>AST</span>
          <span aria-hidden="true">→</span>
          <span>Result</span>
        </div>
      </header>

      <div className="workspace-grid">
        <section className="calculator-card" aria-labelledby="calculator-title">
          <div className="section-heading">
            <div>
              <span className="eyebrow">Backend source of truth</span>
              <h2 id="calculator-title">Evaluate expression</h2>
            </div>
            <span className="api-route">POST /api/v1/calculations</span>
          </div>

          <form onSubmit={handleSubmit}>
            <label className="input-label" htmlFor="expression">
              Expression
            </label>
            <div className="expression-row">
              <input
                id="expression"
                name="expression"
                value={expression}
                maxLength={1000}
                autoComplete="off"
                spellCheck={false}
                onChange={(event) => {
                  setExpression(event.target.value);
                  setResult(null);
                  setError(null);
                }}
                placeholder="e.g. (2 + 5) * 3"
              />
              <button
                className="primary-button"
                type="submit"
                disabled={expression.trim().length === 0 || isCalculating}
              >
                {isCalculating ? "Calculating…" : "Calculate"}
              </button>
            </div>

            <div className="symbol-row" aria-label="Expression shortcuts">
              {SYMBOLS.map((symbol) => (
                <button
                  className="symbol-button"
                  type="button"
                  key={symbol.label}
                  onClick={() => appendSymbol(symbol.value)}
                  aria-label={`Insert ${symbol.label}`}
                >
                  {symbol.label}
                </button>
              ))}
            </div>
          </form>

          <div className="response-zone" aria-live="polite">
            {result !== null && (
              <div className="result-panel">
                <span className="response-label">Result</span>
                <output>{result}</output>
              </div>
            )}

            {error && (
              <div className="error-panel" role="alert">
                <div>
                  <span className="response-label">{errorTitle}</span>
                  <strong>{error.message}</strong>
                </div>
                <code>{error.code}</code>
              </div>
            )}

            {result === null && !error && (
              <div className="idle-panel">
                <span className="response-label">Ready</span>
                <p>
                  Supports decimals, negative numbers, parentheses, precedence,
                  power and <code>sqrt(...)</code>.
                </p>
              </div>
            )}
          </div>

          <ExpressionExamples onSelect={chooseExpression} />
        </section>

        <aside className="side-column">
          <CalculationHistory
            history={history}
            onReuse={chooseExpression}
            onClear={clearHistory}
          />

          <section className="contract-card" aria-labelledby="contract-title">
            <span className="eyebrow">Error contract</span>
            <h2 id="contract-title">Clear failure modes</h2>
            <ul>
              <li><code>VALIDATION_ERROR</code></li>
              <li><code>LEXICAL_ERROR</code></li>
              <li><code>SYNTAX_ERROR</code></li>
              <li><code>CALCULATION_ERROR</code></li>
            </ul>
            <p>
              The UI displays backend errors; it does not reinterpret grammar or
              arithmetic rules locally.
            </p>
          </section>
        </aside>
      </div>

      <footer>
        <span>Calculator Challenge</span>
        <span>React + TypeScript adapter over a pure Java core</span>
      </footer>
    </main>
  );
}

function formatErrorCode(code?: string): string {
  switch (code) {
    case "VALIDATION_ERROR":
      return "Validation error";
    case "LEXICAL_ERROR":
      return "Lexical error";
    case "SYNTAX_ERROR":
      return "Syntax error";
    case "CALCULATION_ERROR":
      return "Calculation error";
    case "NETWORK_ERROR":
      return "Connection error";
    default:
      return "Request failed";
  }
}
