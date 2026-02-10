import Link from "next/link";
import { ProductGrid } from "../../../components/products/ProductGrid";
import { listProducts, listProductsByCategory } from "../../../lib/api";
import { getAllCategories } from "../../../lib/categories";

export default async function ProductsPage({
  searchParams,
}: {
  searchParams?: { page?: string; sort?: string; categoryId?: string; q?: string };
}) {
  const pageParam = Number(searchParams?.page ?? "1");
  const apiPage = Number.isFinite(pageParam) && pageParam > 0 ? pageParam - 1 : 0;

  const sort = searchParams?.sort || "price,asc";
  const categoryId = searchParams?.categoryId ? Number(searchParams.categoryId) : undefined;

  const categoriesPromise = getAllCategories().catch(() => null);

  const productsPromise = categoryId
    ? listProductsByCategory({ categoryId, page: apiPage, size: 12, sort })
    : listProducts({ page: apiPage, size: 12, sort });

  const [productsResponse, categoriesResponse] = await Promise.all([
    productsPromise,
    categoriesPromise,
  ]);

  const page = productsResponse.data;
  const categories = categoriesResponse ? categoriesResponse.data : [];

  const currentPage = page.number + 1;
  const totalPages = page.totalPages;

  const buildPageLink = (pageNumber: number) => {
    const params = new URLSearchParams();
    params.set("page", String(pageNumber));
    if (sort) params.set("sort", sort);
    if (categoryId) params.set("categoryId", String(categoryId));
    if (searchParams?.q) params.set("q", searchParams.q);
    return `/products?${params.toString()}`;
  };

  const buildCategoryLink = (id?: number) => {
    const params = new URLSearchParams();
    params.set("page", "1");
    if (sort) params.set("sort", sort);
    if (searchParams?.q) params.set("q", searchParams.q);
    if (id) {
      params.set("categoryId", String(id));
    }
    const qs = params.toString();
    return `/products${qs ? `?${qs}` : ""}`;
  };

  return (
    <div className="space-y-6">
      <header className="space-y-2">
        <p className="text-xs font-semibold uppercase tracking-[0.2em] text-zinc-500 dark:text-zinc-400">
          Shop
        </p>
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          All products
        </h1>
        <p className="text-sm text-zinc-500 dark:text-zinc-400">
          Browse our full collection of everyday essentials.
        </p>
      </header>

      {categories.length > 0 && (
        <div className="flex flex-wrap gap-2 text-xs">
          <Link
            href={buildCategoryLink(undefined)}
            className={`inline-flex items-center rounded-full border px-3 py-1 transition ${
              !categoryId
                ? "border-zinc-900 bg-zinc-900 text-white dark:border-zinc-100 dark:bg-zinc-100 dark:text-zinc-900"
                : "border-zinc-200 text-zinc-700 hover:border-zinc-300 hover:bg-zinc-50 dark:border-zinc-700 dark:text-zinc-200 dark:hover:border-zinc-500 dark:hover:bg-zinc-900"
            }`}
          >
            All categories
          </Link>
          {categories.map((cat) => (
            <Link
              key={cat.id}
              href={buildCategoryLink(cat.id)}
              className={`inline-flex items-center rounded-full border px-3 py-1 transition ${
                categoryId === cat.id
                  ? "border-zinc-900 bg-zinc-900 text-white dark:border-zinc-100 dark:bg-zinc-100 dark:text-zinc-900"
                  : "border-zinc-200 text-zinc-700 hover:border-zinc-300 hover:bg-zinc-50 dark:border-zinc-700 dark:text-zinc-200 dark:hover:border-zinc-500 dark:hover:bg-zinc-900"
              }`}
            >
              {cat.name}
            </Link>
          ))}
        </div>
      )}

      <ProductGrid
        products={page.content.map((p) => ({
          id: p.id,
          name: p.name,
          price: p.price,
        }))}
      />

      {totalPages > 1 && (
        <div className="mt-4 flex items-center justify-between border-t border-zinc-200 pt-4 text-sm text-zinc-600 dark:border-zinc-800 dark:text-zinc-300">
          <div>
            <span className="text-xs uppercase tracking-[0.18em] text-zinc-500 dark:text-zinc-400">
              Page
            </span>
            <span className="ml-1 font-medium">
              {currentPage} of {totalPages}
            </span>
          </div>

          <div className="flex gap-2">
            <Link
              href={buildPageLink(Math.max(1, currentPage - 1))}
              aria-disabled={currentPage === 1}
              className={`inline-flex items-center justify-center rounded-full border px-3 py-1 text-xs font-medium transition ${
                currentPage === 1
                  ? "cursor-not-allowed border-zinc-200 text-zinc-400 dark:border-zinc-800 dark:text-zinc-600"
                  : "border-zinc-300 text-zinc-800 hover:border-zinc-400 hover:bg-zinc-50 dark:border-zinc-700 dark:text-zinc-100 dark:hover:border-zinc-500 dark:hover:bg-zinc-900"
              }`}
            >
              Previous
            </Link>
            <Link
              href={buildPageLink(Math.min(totalPages, currentPage + 1))}
              aria-disabled={currentPage === totalPages}
              className={`inline-flex items-center justify-center rounded-full border px-3 py-1 text-xs font-medium transition ${
                currentPage === totalPages
                  ? "cursor-not-allowed border-zinc-200 text-zinc-400 dark:border-zinc-800 dark:text-zinc-600"
                  : "border-zinc-300 text-zinc-800 hover:border-zinc-400 hover:bg-zinc-50 dark:border-zinc-700 dark:text-zinc-100 dark:hover:border-zinc-500 dark:hover:bg-zinc-900"
              }`}
            >
              Next
            </Link>
          </div>
        </div>
      )}
    </div>
  );
}
