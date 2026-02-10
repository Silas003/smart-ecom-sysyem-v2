"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getAllCategories, type Category } from "../../../../lib/categories";
import { useAuthStore } from "../../../../lib/auth-store";

export default function AdminCategoriesPage() {
  const router = useRouter();
  const { user, isAuthenticated } = useAuthStore();
  const [categories, setCategories] = useState<Category[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [newCategoryName, setNewCategoryName] = useState<string>("");

  useEffect(() => {
    if (!isAuthenticated() || !user) {
      router.push(`/login?redirect=${encodeURIComponent("/admin/categories")}`);
      return;
    }
    if (user.userRole !== "admin") {
      router.push("/");
      return;
    }

    getAllCategories()
      .then((res) => setCategories(res.data))
      .catch((err) => {
        console.error("Failed to load categories", err);
        setError(err instanceof Error ? err.message : "Failed to load categories");
      });
  }, [isAuthenticated, router, user]);

  const handleCreateCategory = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newCategoryName.trim()) {
      setError("Category name is required");
      return;
    }
    // Placeholder for create category API call to keep UX consistent
    setNewCategoryName("");
    setError(null);
  };

  if (!isAuthenticated() || !user || user.userRole !== "admin") {
    return null;
  }

  if (error) {
    return (
      <div className="space-y-3">
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Categories
        </h1>
        <p className="text-sm text-red-500 dark:text-red-400">{error}</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
        Categories
      </h1>

      <form
        onSubmit={handleCreateCategory}
        className="grid gap-3 rounded-2xl border border-zinc-200 bg-white p-4 text-sm dark:border-zinc-800 dark:bg-zinc-950"
      >
        <p className="text-xs font-semibold uppercase tracking-[0.18em] text-zinc-500 dark:text-zinc-400">
          Create category
        </p>
        <div className="flex items-center gap-3">
          <input
            type="text"
            value={newCategoryName}
            onChange={(e) => setNewCategoryName(e.target.value)}
            placeholder="New category name"
            className="flex-1 rounded-lg border border-zinc-300 bg-white px-3 py-2 text-xs outline-none focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
          />
          <button
            type="submit"
            className="inline-flex items-center justify-center rounded-full bg-zinc-900 px-4 py-1.5 text-xs font-medium text-white transition hover:bg-zinc-700 disabled:cursor-not-allowed disabled:opacity-60 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200"
          >
            Create
          </button>
        </div>
      </form>

      <table className="w-full border-collapse text-left text-sm">
        <thead>
          <tr className="border-b border-zinc-200 text-xs uppercase tracking-[0.18em] text-zinc-500 dark:border-zinc-800 dark:text-zinc-400">
            <th className="py-2 pr-4">ID</th>
            <th className="py-2 pr-4">Name</th>
          </tr>
        </thead>
        <tbody>
          {categories.map((c) => (
            <tr
              key={c.id}
              className="border-b border-zinc-100 text-xs text-zinc-700 last:border-0 dark:border-zinc-800 dark:text-zinc-200"
            >
              <td className="py-2 pr-4">{c.id}</td>
              <td className="py-2 pr-4">{c.name}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
