export type CalculationResponse = {
  expression: string;
  result: string;
};

type ApiErrorBody = {
  code: string;
  message: string;
};

export class CalculationApiError extends Error {
  constructor(
    readonly code: string,
    message: string
  ) {
    super(message);
    this.name = "CalculationApiError";
  }
}

const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL ?? "").replace(/\/$/, "");

export async function calculateExpression(
  expression: string
): Promise<CalculationResponse> {
  let response: Response;

  try {
    response = await fetch(`${apiBaseUrl}/api/v1/calculations`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ expression })
    });
  } catch {
    // Normalize browser-specific fetch failures into one stable client error.
    throw new CalculationApiError(
      "NETWORK_ERROR",
      "The calculator service is unavailable. Check that the backend is running."
    );
  }

  if (response.ok) {
    return (await response.json()) as CalculationResponse;
  }

  const error = await readApiError(response);
  throw new CalculationApiError(error.code, error.message);
}

async function readApiError(response: Response): Promise<ApiErrorBody> {
  try {
    const body = (await response.json()) as Partial<ApiErrorBody>;

    if (typeof body.code === "string" && typeof body.message === "string") {
      return {
        code: body.code,
        message: body.message
      };
    }
  } catch {
    // Ignore malformed/non-JSON error bodies and use the HTTP fallback below.
  }

  return {
    code: "HTTP_ERROR",
    message: `The calculator service returned HTTP ${response.status}.`
  };
}
