"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useAuthStore } from "../../../lib/auth-store";
import { getUsers } from "../../../lib/auth-api";
import { listProducts } from "../../../lib/api";
import { getAllOrders } from "../../../lib/orders";

export default function AdminDashboardPage() {
  const router = useRouter();
  const { user, isAuthenticated } = useAuthStore();
  const [loading, setLoading] = useState(true);
  const [counts, setCounts] = useState<{
    users: number;
    products: number;
    orders: number;
  } | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!isAuthenticated() || !user) {
      router.push(`/login?redirect=${encodeURIComponent("/admin")}`);
      return;
    }
    if (user.userRole !== "admin") {
      router.push("/");
      return;
    }

    async function loadCounts() {
      try {
        const [usersRes, productsRes, ordersRes] = await Promise.all([
          getUsers({ page: 0, size: 1 }),
          listProducts({ page: 0, size: 1, sort: "price,asc" }),
          getAllOrders({ page: 0, size: 1 }),
        ]);
        setCounts({
          users: usersRes.data.totalElements,
          products: productsRes.data.totalElements,
          orders: ordersRes.data.totalElements,
        });
      } catch (err) {
        console.error("Failed to load admin summary", err);
        setError("Failed to load admin summary. Please try again.");
      } finally {
        setLoading(false);
      }
    }

    loadCounts();
  }, [isAuthenticated, router, user]);

  if (!isAuthenticated() || !user || user.userRole !== "admin") {
    return null;
  }

  if (loading) {
    return (
      <div className="space-y-3">
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Admin dashboard
        </h1>
        <p className="text-sm text-zinc-500 dark:text-zinc-400">Loading summary...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="space-y-3">
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Admin dashboard
        </h1>
        <p className="text-sm text-red-500 dark:text-red-400">{error}</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="space-y-2">
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Admin dashboard
        </h1>
        <p className="text-sm text-zinc-500 dark:text-zinc-400">
          Overview of users, orders, and products.
        </p>
      </div>

      {counts && (
        <div className="grid gap-4 sm:grid-cols-3">
          <div className="rounded-2xl border border-zinc-200 bg-white p-4 text-sm dark:border-zinc-800 dark:bg-zinc-950">
            <p className="text-xs uppercase tracking-[0.18em] text-zinc-500 dark:text-zinc-400">
              Users
            </p>
            <p className="mt-1 text-2xl font-semibold text-zinc-900 dark:text-zinc-50">
              {counts.users}
            </p>
          </div>
          <div className="rounded-2xl border border-zinc-200 bg-white p-4 text-sm dark:border-zinc-800 dark:bg-zinc-950">
            <p className="text-xs uppercase tracking-[0.18em] text-zinc-500 dark:text-zinc-400">
              Orders
            </p>
            <p className="mt-1 text-2xl font-semibold text-zinc-900 dark:text-zinc-50">
              {counts.orders}
            </p>
          </div>
          <div className="rounded-2xl border border-zinc-200 bg-white p-4 text-sm dark:border-zinc-800 dark:bg-zinc-950">
            <p className="text-xs uppercase tracking-[0.18em] text-zinc-500 dark:text-zinc-400">
              Products
            </p>
            <p className="mt-1 text-2xl font-semibold text-zinc-900 dark:text-zinc-50">
              {counts.products}
            </p>
          </div>
        </div>
      )}

      <div className="grid gap-4 sm:grid-cols-2">
        <Link
          href="/admin/users"
          className="rounded-2xl border border-zinc-200 bg-white p-4 text-sm text-zinc-700 transition hover:border-zinc-300 hover:bg-zinc-50 dark:border-zinc-800 dark:bg-zinc-950 dark:text-zinc-200 dark:hover:border-zinc-600 dark:hover:bg-zinc-900"
        >
          <p className="text-base font-semibold text-zinc-900 dark:text-zinc-50">Manage users</p>
          <p className="mt-1 text-xs text-zinc-500 dark:text-zinc-400">
            View and update user accounts.
          </p>
        </Link>
        <Link
          href="/admin/orders"
          className="rounded-2xl border border-zinc-200 bg-white p-4 text-sm text-zinc-700 transition hover:border-zinc-300 hover:bg-zinc-50 dark:border-zinc-800 dark:bg-zinc-950 dark:text-zinc-200 dark:hover:border-zinc-600 dark:hover:bg-zinc-900"
        >
          <p className="text-base font-semibold text-zinc-900 dark:text-zinc-50">Manage orders</p>
          <p className="mt-1 text-xs text-zinc-500 dark:text-zinc-400">
            Review and update orders.
          </p>
        </Link>
        <Link
          href="/admin/products"
          className="rounded-2xl border border-zinc-200 bg-white p-4 text-sm text-zinc-700 transition hover:border-zinc-300 hover:bg-zinc-50 dark:border-zinc-800 dark:bg-zinc-950 dark:text-zinc-200 dark:hover:border-zinc-600 dark:hover:bg-zinc-900"
        >
          <p className="text-base font-semibold text-zinc-900 dark:text-zinc-50">Manage products</p>
          <p className="mt-1 text-xs text-zinc-500 dark:text-zinc-400">
            Edit and publish products.
          </p>
        </Link>
        <Link
          href="/admin/categories"
          className="rounded-2xl border border-zinc-200 bg-white p-4 text-sm text-zinc-700 transition hover:border-zinc-300 hover:bg-zinc-50 dark:border-zinc-800 dark:bg-zinc-950 dark:text-zinc-200 dark:hover:border-zinc-600 dark:hover:bg-zinc-900"
        >
          <p className="text-base font-semibold text-zinc-900 dark:text-zinc-50">Manage categories</p>
          <p className="mt-1 text-xs text-zinc-500 dark:text-zinc-400">
            Organize products into categories.
          </p>
        </Link>
      </div>
    </div>
  );
}
