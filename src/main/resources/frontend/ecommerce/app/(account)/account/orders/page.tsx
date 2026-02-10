"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { getUserOrders, type Order } from "../../../../lib/orders";
import { getCurrentUserId } from "../../../../lib/user";

export default function AccountOrdersPage() {
  const router = useRouter();
  const [orders, setOrders] = useState<Order[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const userId = getCurrentUserId();
    if (!userId) {
      router.push(`/login?redirect=${encodeURIComponent("/account/orders")}`);
      return;
    }
    getUserOrders(userId)
      .then((res) => setOrders(res.data))
      .catch((err) => {
        console.error("Failed to load orders", err);
        setError(err instanceof Error ? err.message : "Failed to load orders");
      });
  }, [router]);

  if (error) {
    return (
      <div className="space-y-3">
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Your orders
        </h1>
        <p className="text-sm text-red-500 dark:text-red-400">{error}</p>
      </div>
    );
  }

  if (!orders) {
    return (
      <div className="space-y-3">
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Your orders
        </h1>
        <p className="text-sm text-zinc-500 dark:text-zinc-400">
          Loading your order history...
        </p>
      </div>
    );
  }

  if (orders.length === 0) {
    return (
      <div className="space-y-3">
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Your orders
        </h1>
        <p className="text-sm text-zinc-500 dark:text-zinc-400">
          You haven&apos;t placed any orders yet.
        </p>
        <Link
          href="/products"
          className="inline-flex items-center justify-center rounded-full bg-zinc-900 px-5 py-2 text-sm font-medium text-white transition hover:bg-zinc-700 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200"
        >
          Start shopping
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
        Your orders
      </h1>
      <ul className="space-y-3 text-sm">
        {orders.map((order) => (
          <li
            key={order.id}
            className="flex items-center justify-between rounded-2xl border border-zinc-200 bg-white px-4 py-3 dark:border-zinc-800 dark:bg-zinc-950"
          >
            <div>
              <p className="font-medium text-zinc-900 dark:text-zinc-50">
                Order #{order.id}
              </p>
              <p className="text-xs text-zinc-500 dark:text-zinc-400">
                Status: {order.status}
              </p>
              <p className="text-xs text-zinc-500 dark:text-zinc-400">
                Total: ${order.totalAmount.toFixed(2)}
              </p>
            </div>
            <Link
              href={`/account/orders/${order.id}`}
              className="text-xs font-medium text-zinc-900 underline hover:text-zinc-700 dark:text-zinc-100 dark:hover:text-zinc-300"
            >
              View details
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
