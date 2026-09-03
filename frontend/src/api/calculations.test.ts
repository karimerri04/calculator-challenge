import {
  calculateExpression
} from "./calculations";

describe("calculateExpression", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("returns the backend result without recalculating it in the browser", async () => {
    const fetchMock = vi.fn().mockResolvedValue(
      new Response(
        JSON.stringify({ expression: "2 + 2 * 5 + 5", result: "17" }),
        {
          status: 200,
          headers: { "Content-Type": "application/json" }
        }
      )
    );
    vi.stubGlobal("fetch", fetchMock);

    await expect(calculateExpression("2 + 2 * 5 + 5")).resolves.toEqual({
      expression: "2 + 2 * 5 + 5",
      result: "17"
    });

    expect(fetchMock).toHaveBeenCalledTimes(1);
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/v1/calculations",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ expression: "2 + 2 * 5 + 5" })
      })
    );
  });

  it("preserves the backend error code and message", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(
        new Response(
          JSON.stringify({
            code: "CALCULATION_ERROR",
            message: "Division by zero",
            path: "/api/v1/calculations"
          }),
          {
            status: 400,
            headers: { "Content-Type": "application/json" }
          }
        )
      )
    );

    await expect(calculateExpression("1/0")).rejects.toMatchObject({
      name: "CalculationApiError",
      code: "CALCULATION_ERROR",
      message: "Division by zero"
    });
  });

  it("converts a fetch failure into a stable network error", async () => {
    vi.stubGlobal("fetch", vi.fn().mockRejectedValue(new TypeError("offline")));

    await expect(calculateExpression("1+2")).rejects.toMatchObject({
      code: "NETWORK_ERROR"
    });
  });

  it("falls back to a stable HTTP error for a non-JSON error response", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(
        new Response("Bad Gateway", {
          status: 502,
          headers: { "Content-Type": "text/plain" }
        })
      )
    );

    await expect(calculateExpression("1+2")).rejects.toMatchObject({
      code: "HTTP_ERROR",
      message: "The calculator service returned HTTP 502."
    });
  });
});
