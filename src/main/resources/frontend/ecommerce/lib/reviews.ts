import type { Product } from "./api";
import type { User } from "./user";
import { ResponseDto } from "./api";
import { authorizedFetch } from "./secured-fetch";

export type Review = {
  id: number | string;
  product: Product | null;
  user: User | null;
  rating: number;
  description: string | null;
};

export type ReviewRequest = {
  productId: number;
  userId: number;
  rating: number;
  description?: string;
};

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

async function handleGraphQL<T>(
  fetchImpl: typeof fetch,
  body: { query: string; variables?: Record<string, unknown> }
): Promise<T> {
  const res = await fetchImpl(`${API_BASE_URL}/graphql`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
  });

  if (!res.ok) {
    let errorBody: unknown = null;
    try {
      errorBody = await res.json();
    } catch {
      // ignore
    }
    const message =
      typeof errorBody === "object" &&
      errorBody !== null &&
      "message" in (errorBody as any)
        ? // eslint-disable-next-line @typescript-eslint/no-explicit-any
          (errorBody as any).message
        : `Request failed with status ${res.status}`;
    const error = new Error(message) as Error & { status?: number; body?: unknown };
    error.status = res.status;
    error.body = errorBody;
    throw error;
  }

  const json = (await res.json()) as {
    data?: unknown;
    errors?: { message: string }[];
  };

  if (json.errors && json.errors.length > 0) {
    throw new Error(json.errors.map((e) => e.message).join("; "));
  }

  return json.data as T;
}

export async function getReviewsForProduct(productId: number): Promise<ResponseDto<Review[]>> {
  const query = `
    query ReviewsByProduct($productId: ID!) {
      reviewsByProduct(productId: $productId) {
        id
        rating
        description
        product { id }
        user { id username email userRole }
      }
    }
  `;

  const data = await handleGraphQL<{ reviewsByProduct: Review[] }>(fetch, {
    query,
    variables: { productId: String(productId) },
  });

  return {
    status: 200,
    message: "reviews retrieved",
    data: data.reviewsByProduct,
  };
}

export async function createReview(body: ReviewRequest): Promise<ResponseDto<Review>> {
  const mutation = `
    mutation CreateReview($input: ReviewInput!, $userId: ID!) {
      createReview(input: $input, userId: $userId) {
        id
        rating
        description
        product { id }
        user { id username email userRole }
      }
    }
  `;

  const variables = {
    input: {
      productId: String(body.productId),
      userId: String(body.userId),
      rating: body.rating,
      description: body.description ?? "",
    },
    userId: String(body.userId),
  };

  const data = await handleGraphQL<{ createReview: Review }>(
    (input, init) => authorizedFetch(input.toString(), init as RequestInit) as Promise<Response>,
    {
      query: mutation,
      variables,
    }
  );

  return {
    status: 201,
    message: "review created",
    data: data.createReview,
  };
}

// Delete review is still available via REST if needed; keeping a no-op wrapper or converting to GraphQL later.
export async function deleteReview(id: number): Promise<void> {
  console.warn("deleteReview via GraphQL not implemented; backend only exposes REST DELETE.");
}
