type Props = {
  onSelect: (expression: string) => void;
};

const EXAMPLES = [
  "2 + 2 * 5 + 5",
  "sqrt(4) + 2^3",
  "2^3^2",
  "-2^2",
  "(-2)^2",
  "2^-2"
];

export function ExpressionExamples({ onSelect }: Props) {
  return (
    <section className="examples" aria-labelledby="examples-title">
      <div className="section-heading">
        <div>
          <span className="eyebrow">Try the grammar</span>
          <h2 id="examples-title">Examples</h2>
        </div>
      </div>

      <div className="example-list">
        {EXAMPLES.map((expression) => (
          <button
            className="example-chip"
            key={expression}
            type="button"
            onClick={() => onSelect(expression)}
          >
            {expression}
          </button>
        ))}
      </div>
    </section>
  );
}
