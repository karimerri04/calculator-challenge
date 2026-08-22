export type CalculationResponse = {
  expression: string;
  result: string;
};

export type ApiErrorBody = {
  code: string;
  message: string;
  path: string;
};

export class CalculationApiError extends Error {
  constructor(
    readonly code: string,
    message: string,
    readonly path?: string,
    readonly status?: number
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
    throw new CalculationApiError(
      "NETWORK_ERROR",
      "The calculator service is unavailable. Check that the backend is running."
    );
  }

  if (response.ok) {
    return (await response.json()) as CalculationResponse;
  }

  const error = await readApiError(response);
  throw new CalculationApiError(
    error.code,
    error.message,
    error.path,
    response.status
  );
}

async function readApiError(response: Response): Promise<ApiErrorBody> {
  try {
    const body = (await response.json()) as Partial<ApiErrorBody>;

    if (body.code && body.message) {
      return {
        code: body.code,
        message: body.message,
        path: body.path ?? "/api/v1/calculations"
      };
    }
  } catch {
    // The fallback below keeps the UI useful even for a non-JSON gateway error.
  }

  return {
    code: "HTTP_ERROR",
    message: `The calculator service returned HTTP ${response.status}.`,
    path: "/api/v1/calculations"
  };
}
