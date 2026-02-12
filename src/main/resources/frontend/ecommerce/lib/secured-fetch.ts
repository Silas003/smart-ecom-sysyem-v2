import { getAuthHeader } from "./auth-store";

export async function authorizedFetch(
  input: RequestInfo | URL,
  init: RequestInit = {}
): Promise<Response> {
  const authHeader = getAuthHeader();
  const headers = {
    ...(init.headers || {}),
    ...authHeader,
  } as Record<string, string>;

  return fetch(input, { ...init, headers });
}

