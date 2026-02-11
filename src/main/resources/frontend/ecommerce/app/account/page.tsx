"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { getCurrentUser, getCurrentUserId } from "../../lib/user";
import { listUserOrders } from "../../lib/orders";
import type { Order } from "../../lib/orders";

export default function AccountPage() {
  const router = useRouter();
  const initialUserId = getCurrentUserId();
  const [loading, setLoading] = useState(!initialUserId);
  const [ordersLoading, setOrdersLoading] = useState(true);
  const [orders, setOrders] = useState<Order[]>([]);
  const user = getCurrentUser();

  useEffect(() => {
    const userId = getCurrentUserId();
    if (!userId) {
      router.push(`/login?redirect=${encodeURIComponent("/account")}`);
      return;
    }
    listUserOrders(userId)
      .then((res) => setOrders(res.data))
      .catch((err) => console.error("Failed to load orders", err))
      .finally(() => setOrdersLoading(false));
  }, [router]);

  if (loading) {
    return (
      <div className="space-y-3 animate-pulse">
        <div className="h-6 w-32 rounded bg-zinc-200 dark:bg-zinc-800" />
        <div className="h-20 rounded-2xl bg-zinc-100 dark:bg-zinc-900" />
      </div>
    );
  }

  if (!user) {
    return null;
  }

  return (
    <div className="space-y-8">
      <section className="space-y-2 rounded-3xl border border-zinc-200 bg-white p-6 text-sm dark:border-zinc-800 dark:bg-zinc-950">
        <h1 className="text-lg font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Account
        </h1>
        <p className="text-sm text-zinc-500 dark:text-zinc-400">
          Manage your profile and see recent activity.
        </p>
        <dl className="mt-4 grid gap-4 text-sm sm:grid-cols-2">
          <div>
            <dt className="text-xs uppercase tracking-wide text-zinc-500 dark:text-zinc-400">
              Name
            </dt>
            <dd className="text-zinc-900 dark:text-zinc-50">{user.username}</dd>
          </div>
          <div>
            <dt className="text-xs uppercase tracking-wide text-zinc-500 dark:text-zinc-400">
              Email
            </dt>
            <dd className="text-zinc-900 dark:text-zinc-50">{user.email}</dd>
          </div>
          <div>
            <dt className="text-xs uppercase tracking-wide text-zinc-500 dark:text-zinc-400">
              Role
            </dt>
            <dd className="capitalize text-zinc-900 dark:text-zinc-50">{user.userRole}</dd>
          </div>
        </dl>
      </section>

      <section className="space-y-3">
        <div className="flex items-center justify-between">
          <h2 className="text-sm font-semibold uppercase tracking-wide text-zinc-500 dark:text-zinc-400">
            Recent orders
          </h2>
          <Link
            href="/account/orders"
            className="text-xs font-medium text-zinc-600 underline-offset-2 hover:underline dark:text-zinc-300"
          >
            View all
          </Link>
        </div>
        <div className="space-y-3">
          {ordersLoading ? (
            <div className="h-16 animate-pulse rounded-2xl bg-zinc-100 dark:bg-zinc-900" />
          ) : orders.length === 0 ? (
            <p className="text-sm text-zinc-500 dark:text-zinc-400">
              You haven&apos;t placed any orders yet.
            </p>
          ) : (
            orders.slice(0, 3).map((order) => (
              <div
                key={order.id}
                className="flex items-center justify-between rounded-2xl border border-zinc-200 bg-white px-4 py-3 text-sm dark:border-zinc-800 dark:bg-zinc-950"
              >
                <div>
                  <p className="font-medium text-zinc-900 dark:text-zinc-50">
                    Order #{order.id}
                  </p>
                  <p className="text-xs text-zinc-500 dark:text-zinc-400">
                    {order.items?.length || 0} items · ${order.totalAmount?.toFixed(2) ?? "0.00"}
                  </p>
                </div>
                <p className="text-xs capitalize text-zinc-500 dark:text-zinc-400">
                  {order.status}
                </p>
              </div>
            ))
          )}
        </div>
      </section>
    </div>
  );
}
