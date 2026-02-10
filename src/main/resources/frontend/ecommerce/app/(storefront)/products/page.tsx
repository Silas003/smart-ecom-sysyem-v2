"use client";

import { useEffect, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import Link from "next/link";
import { listProducts, type Product, type Page } from "../../../lib/api";

export default function ProductsPage() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const pageParam = searchParams?.get("page");
  const currentPage = pageParam ? Number(pageParam) || 0 : 0;
  const pageSize = 12;

  const [page, setPage] = useState<Page<Product> | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    async function load() {
      try {
        setLoading(true);
        setError(null);
        const res = await listProducts({ page: currentPage, size: pageSize, sort: "price,asc" });
        if (!cancelled) setPage(res.data);
      } catch (err) {
        if (!cancelled) {
          const msg =
            err instanceof Error
              ? err.message
              : "Failed to load products. Please try again.";
          setError(msg);
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }
    load();
    return () => {
      cancelled = true;
    };
  }, [currentPage]);

  const goToPage = (pageIndex: number) => {
    const params = new URLSearchParams(searchParams ?? undefined);
    if (pageIndex <= 0) {
      params.delete("page");
    } else {
      params.set("page", String(pageIndex));
    }
    router.push(`/products${params.toString() ? `?${params.toString()}` : ""}`);
  };

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <p className="text-xs font-semibold uppercase tracking-[0.18em] text-zinc-500 dark:text-zinc-400">
          Products
        </p>
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Browse our catalog
        </h1>
      </header>

      {loading && !page && (
        <p className="text-sm text-zinc-500 dark:text-zinc-400">Loading products…</p>
      )}
      {error && (
        <p className="text-sm text-red-500 dark:text-red-400">{error}</p>
      )}

      {page && (
        <>
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {page.content.map((product) => (
              <Link
                key={product.id}
                href={`/products/${product.id}`}
                className="group rounded-2xl border border-zinc-200 bg-white p-4 text-sm transition hover:border-zinc-300 hover:bg-zinc-50 dark:border-zinc-800 dark:bg-zinc-950 dark:hover:border-zinc-600 dark:hover:bg-zinc-900"
              >
                <div className="flex h-40 items-center justify-center rounded-xl bg-zinc-100 text-xs text-zinc-400 transition group-hover:bg-zinc-200 dark:bg-zinc-900 dark:text-zinc-500 dark:group-hover:bg-zinc-800">
                  Image
                </div>
                <div className="mt-3 space-y-1">
                  <p className="line-clamp-1 text-sm font-medium text-zinc-900 dark:text-zinc-50">
                    {product.name}
                  </p>
                  <p className="text-xs font-semibold text-zinc-900 dark:text-zinc-50">
                    ${product.price.toFixed(2)}
                  </p>
                  <p className="text-[11px] text-zinc-500 dark:text-zinc-400">
                    Stock: {product.stockQuantity}
                  </p>
                </div>
              </Link>
            ))}
          </div>

          <div className="mt-4 flex items-center justify-between text-xs text-zinc-500 dark:text-zinc-400">
            <span>
              Page {page.number + 1} of {page.totalPages || 1}
            </span>
            <div className="flex items-center gap-2">
              <button
                type="button"
                onClick={() => goToPage(currentPage - 1)}
                disabled={page.first}
                className="inline-flex items-center justify-center rounded-full border border-zinc-200 px-3 py-1 text-[11px] font-medium text-zinc-700 transition hover:bg-zinc-50 disabled:cursor-not-allowed disabled:opacity-50 dark:border-zinc-800 dark:text-zinc-200 dark:hover:bg-zinc-900"
              >
                Previous
              </button>
              <button
                type="button"
                onClick={() => goToPage(currentPage + 1)}
                disabled={page.last}
                className="inline-flex items-center justify-center rounded-full border border-zinc-200 px-3 py-1 text-[11px] font-medium text-zinc-700 transition hover:bg-zinc-50 disabled:cursor-not-allowed disabled:opacity-50 dark:border-zinc-800 dark:text-zinc-200 dark:hover:bg-zinc-900"
              >
                Next
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
