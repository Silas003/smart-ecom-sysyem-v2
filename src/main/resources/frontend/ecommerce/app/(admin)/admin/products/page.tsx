"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { listProducts, createProduct, updateProduct, deleteProduct, type Product } from "../../../../lib/api";
import { useAuthStore } from "../../../../lib/auth-store";

export default function AdminProductsPage() {
  const router = useRouter();
  const { user, isAuthenticated } = useAuthStore();
  const [products, setProducts] = useState<Product[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [creating, setCreating] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [form, setForm] = useState<{ name: string; price: number; stockQuantity: number; categoryId: number }>(
    { name: "", price: 0, stockQuantity: 0, categoryId: 0 }
  );

  useEffect(() => {
    if (!isAuthenticated() || !user) {
      router.push(`/login?redirect=${encodeURIComponent("/admin/products")}`);
      return;
    }
    if (user.userRole !== "admin") {
      router.push("/");
      return;
    }

    refresh();
  }, [isAuthenticated, router, user]);

  const refresh = async () => {
    try {
      const res = await listProducts({ page: 0, size: 50, sort: "price,asc" });
      setProducts(res.data.content);
    } catch (err) {
      console.error("Failed to load products", err);
      setError(err instanceof Error ? err.message : "Failed to load products");
    }
  };

  if (!isAuthenticated() || !user || user.userRole !== "admin") {
    return null;
  }

  const resetForm = () => {
    setForm({ name: "", price: 0, stockQuantity: 0, categoryId: 0 });
    setEditingId(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setCreating(true);
      setError(null);
      if (editingId === null) {
        await createProduct(form);
      } else {
        await updateProduct(editingId, form);
      }
      resetForm();
      await refresh();
    } catch (err) {
      const fallback = "Failed to save product.";
      if (err instanceof Error) setError(err.message || fallback);
      else setError(fallback);
    } finally {
      setCreating(false);
    }
  };

  const startEdit = (p: Product) => {
    setEditingId(p.id);
    setForm({
      name: p.name,
      price: p.price,
      stockQuantity: p.stockQuantity,
      categoryId: p.categoryId,
    });
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm("Delete this product?")) return;
    try {
      await deleteProduct(id);
      setProducts((prev) => prev.filter((p) => p.id !== id));
    } catch (err) {
      console.error("Failed to delete product", err);
      setError("Failed to delete product.");
    }
  };

  if (error) {
    return (
      <div className="space-y-3">
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Products
        </h1>
        <p className="text-sm text-red-500 dark:text-red-400">{error}</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
        Products
      </h1>

      <form
        onSubmit={handleSubmit}
        className="grid gap-3 rounded-2xl border border-zinc-200 bg-white p-4 text-sm dark:border-zinc-800 dark:bg-zinc-950"
      >
        <p className="text-xs font-semibold uppercase tracking-[0.18em] text-zinc-500 dark:text-zinc-400">
          {editingId === null ? "Create product" : `Edit product #${editingId}`}
        </p>
        <div className="grid gap-3 sm:grid-cols-4">
          <input
            type="text"
            placeholder="Name"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            required
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-xs outline-none focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
          />
          <input
            type="number"
            step="0.01"
            placeholder="Price"
            value={form.price}
            onChange={(e) => setForm({ ...form, price: Number(e.target.value) })}
            required
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-xs outline-none focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
          />
          <input
            type="number"
            placeholder="Stock"
            value={form.stockQuantity}
            onChange={(e) => setForm({ ...form, stockQuantity: Number(e.target.value) })}
            required
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-xs outline-none focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
          />
          <input
            type="number"
            placeholder="Category ID"
            value={form.categoryId}
            onChange={(e) => setForm({ ...form, categoryId: Number(e.target.value) })}
            required
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-xs outline-none focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
          />
        </div>
        <div className="flex items-center justify-between gap-3">
          <button
            type="submit"
            disabled={creating}
            className="inline-flex items-center justify-center rounded-full bg-zinc-900 px-4 py-1.5 text-xs font-medium text-white transition hover:bg-zinc-700 disabled:cursor-not-allowed disabled:opacity-60 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200"
          >
            {creating ? "Saving..." : editingId === null ? "Create" : "Update"}
          </button>
          {editingId !== null && (
            <button
              type="button"
              onClick={resetForm}
              className="text-xs text-zinc-500 hover:text-zinc-700 dark:text-zinc-400 dark:hover:text-zinc-200"
            >
              Cancel edit
            </button>
          )}
        </div>
      </form>

      <table className="w-full border-collapse text-left text-sm">
        <thead>
          <tr className="border-b border-zinc-200 text-xs uppercase tracking-[0.18em] text-zinc-500 dark:border-zinc-800 dark:text-zinc-400">
            <th className="py-2 pr-4">ID</th>
            <th className="py-2 pr-4">Name</th>
            <th className="py-2 pr-4">Price</th>
            <th className="py-2 pr-4">Stock</th>
            <th className="py-2 pr-4">Category</th>
            <th className="py-2 pr-4">Actions</th>
          </tr>
        </thead>
        <tbody>
          {products.map((p) => (
            <tr
              key={p.id}
              className="border-b border-zinc-100 text-xs text-zinc-700 last:border-0 dark:border-zinc-800 dark:text-zinc-200"
            >
              <td className="py-2 pr-4">{p.id}</td>
              <td className="py-2 pr-4">{p.name}</td>
              <td className="py-2 pr-4">${p.price.toFixed(2)}</td>
              <td className="py-2 pr-4">{p.stockQuantity}</td>
              <td className="py-2 pr-4">{p.categoryId}</td>
              <td className="py-2 pr-4 space-x-2">
                <button
                  type="button"
                  onClick={() => startEdit(p)}
                  className="rounded-full border border-zinc-300 px-3 py-1 text-[11px] font-medium text-zinc-700 hover:bg-zinc-50 dark:border-zinc-700 dark:text-zinc-200 dark:hover:bg-zinc-900"
                >
                  Edit
                </button>
                <button
                  type="button"
                  onClick={() => handleDelete(p.id)}
                  className="rounded-full border border-red-300 px-3 py-1 text-[11px] font-medium text-red-600 hover:bg-red-50 dark:border-red-700 dark:text-red-300 dark:hover:bg-red-900/30"
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
