import { ResponseDto } from "./api";
import { authorizedFetch } from "./secured-fetch";

export type Review = {
  id: number;
  productId: number;
  reviewerDisplay: string;
  rating: number;
  description: string | null;
  createdAt: string;
};

export type ReviewRequest = {
  productId: number;
  userId: number;
  rating: number;
  description?: string;
};

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    let errorBody: unknown = null;
    try {
      errorBody = await res.json();
    } catch {
      // ignore
    }

    const message =
      typeof errorBody === "object" && errorBody !== null && "message" in errorBody
        ? // eslint-disable-next-line @typescript-eslint/no-explicit-any
          (errorBody as any).message
        : `Request failed with status ${res.status}`;

    const error = new Error(message) as Error & { status?: number; body?: unknown };
    error.status = res.status;
    error.body = errorBody;
    throw error;
  }

  return (await res.json()) as T;
}

export async function getReviewsForProduct(productId: number): Promise<ResponseDto<Review[]>> {
  const res = await fetch(`${API_BASE_URL}/api/v1/reviews/products/${productId}`, {
    method: "GET",
    cache: "no-store",
  });
  return handleResponse<ResponseDto<Review[]>>(res);
}

export async function createReview(body: ReviewRequest): Promise<ResponseDto<Review>> {
  const res = await authorizedFetch(`${API_BASE_URL}/api/v1/reviews`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      productId: body.productId,
      userId: body.userId,
      rating: body.rating,
      description: body.description ?? "",
    }),
  });
  return handleResponse<ResponseDto<Review>>(res as Response);
}

export async function deleteReview(id: number): Promise<void> {
  const res = await authorizedFetch(`${API_BASE_URL}/api/v1/reviews/${id}`, {
    method: "DELETE",
  });

  if (res.status !== 204 && !res.ok) {
    await handleResponse(res as Response);
  }
}
