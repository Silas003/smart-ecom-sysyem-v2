"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getAllCategories, type Category, createCategory } from "../../../../lib/categories";
import { useAuthStore } from "../../../../lib/auth-store";
import { useToast } from "../../../../components/ui/toaster";

export default function AdminCategoriesPage() {
  const router = useRouter();
  const { user, isLoading, hydrate } = useAuthStore();
  const { addToast } = useToast();
  const [categories, setCategories] = useState<Category[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [newCategoryName, setNewCategoryName] = useState<string>("");
  const [creating, setCreating] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);

  const refreshCategories = async () => {
    try {
      const res = await getAllCategories();
      setCategories(res.data);
    } catch (err) {
      console.error("Failed to load categories", err);
      const message = err instanceof Error ? err.message : "Failed to load categories";
      setError(message);
      addToast(message, "error");
    }
  };

  useEffect(() => {
    hydrate();
  }, [hydrate]);

  useEffect(() => {
    if (isLoading) return;

    if (!user) {
      router.push(`/login?redirect=${encodeURIComponent("/admin/categories")}`);
      return;
    }
    if (user.userRole !== "admin") {
      router.push("/");
      return;
    }

    refreshCategories()
      .catch(() => {})
      .finally(() => setInitialLoading(false));
  }, [isLoading, router, user, addToast, hydrate]);

  const handleCreateCategory = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newCategoryName.trim()) {
      const message = "Category name is required";
      setError(message);
      addToast(message, "error");
      return;
    }

    try {
      setCreating(true);
      setError(null);
      await createCategory({ name: newCategoryName.trim() });
      setNewCategoryName("");
      addToast("Category created", "success");
      await refreshCategories();
    } catch (err) {
      console.error("Failed to create category", err);
      const message = err instanceof Error ? err.message : "Failed to create category";
      setError(message);
      addToast(message, "error");
    } finally {
      setCreating(false);
    }
  };

  if (isLoading || (!user && initialLoading)) {
    return (
      <div className="space-y-3">
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Categories
        </h1>
        <p className="text-sm text-zinc-500 dark:text-zinc-400">Checking permissions...</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <header className="flex flex-col gap-1 sm:flex-row sm:items-baseline sm:justify-between">
        <div className="space-y-1">
          <p className="text-xs font-semibold uppercase tracking-[0.18em] text-zinc-500 dark:text-zinc-400">
            Categories
          </p>
          <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
            Manage categories
          </h1>
          <p className="text-xs text-zinc-500 dark:text-zinc-400">
            Organize products into categories to improve discovery and navigation.
          </p>
        </div>
      </header>

      {error && (
        <div className="rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-xs text-red-700 dark:border-red-800 dark:bg-red-950/40 dark:text-red-200">
          {error}
        </div>
      )}

      <form
        onSubmit={handleCreateCategory}
        className="grid gap-3 rounded-2xl border border-zinc-200 bg-white p-4 text-sm shadow-sm dark:border-zinc-800 dark:bg-zinc-950"
      >
        <div className="flex items-baseline justify-between gap-2">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.18em] text-zinc-500 dark:text-zinc-400">
              Create category
            </p>
            <p className="text-[11px] text-zinc-500 dark:text-zinc-400">
              Add a new category to better structure your catalog.
            </p>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <input
            type="text"
            value={newCategoryName}
            onChange={(e) => setNewCategoryName(e.target.value)}
            placeholder="New category name (e.g. Electronics)"
            className="flex-1 rounded-lg border border-zinc-300 bg-white px-3 py-2 text-xs outline-none focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
          />
          <button
            type="submit"
            disabled={creating}
            className="inline-flex items-center justify-center rounded-full bg-zinc-900 px-4 py-1.5 text-xs font-medium text-white transition hover:bg-zinc-700 disabled:cursor-not-allowed disabled:opacity-60 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200"
          >
            {creating ? "Creating..." : "Create category"}
          </button>
        </div>
      </form>

      <div className="overflow-hidden rounded-2xl border border-zinc-200 bg-white text-sm shadow-sm dark:border-zinc-800 dark:bg-zinc-950">
        <div className="flex items-center justify-between border-b border-zinc-200 px-3 py-2 text-[11px] text-zinc-500 dark:border-zinc-800 dark:text-zinc-400">
          <span>Categories</span>
          {initialLoading && <span>Loading...</span>}
        </div>
        {categories.length === 0 && !initialLoading ? (
          <div className="px-3 py-6 text-center text-xs text-zinc-500 dark:text-zinc-400">
            No categories created yet.
          </div>
        ) : (
          <table className="w-full border-collapse text-left text-sm">
            <thead>
              <tr className="border-b border-zinc-200 text-xs uppercase tracking-[0.18em] text-zinc-500 dark:border-zinc-800 dark:text-zinc-400">
                <th className="py-2 pl-3 pr-4">ID</th>
                <th className="py-2 pr-4">Name</th>
              </tr>
            </thead>
            <tbody>
              {categories.map((c) => (
                <tr
                  key={c.id}
                  className="border-b border-zinc-100 text-xs text-zinc-700 last:border-0 dark:border-zinc-800 dark:text-zinc-200"
                >
                  <td className="py-2 pl-3 pr-4">{c.id}</td>
                  <td className="py-2 pr-4">{c.name}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
