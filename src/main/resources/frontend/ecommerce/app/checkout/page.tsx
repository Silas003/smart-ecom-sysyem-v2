"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useCartStore } from "../../lib/cart-store";
import { createOrder } from "../../lib/orders";
import { getCurrentUserId } from "../../lib/user";

export default function CheckoutPage() {
  const router = useRouter();
  const items = useCartStore((state) => state.items);
  const cartId = useCartStore((state) => state.cartId);
  const clearCart = useCartStore((state) => state.clearCart);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const subtotal = items.reduce((sum, item) => sum + item.totalPrice, 0);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!items.length) {
      setError("Your cart is empty.");
      return;
    }

    const userId = getCurrentUserId();
    if (!userId) {
      setError("You need to be signed in to place an order.");
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);

      const orderItems = items.map((item) => ({
        productId: item.productId,
        quantity: item.quantity,
      }));

      const response = await createOrder({ userId, items: orderItems });
      const order = response.data;

      clearCart();
      const params = new URLSearchParams();
      params.set("orderId", String(order.id));
      if (cartId) params.set("cartId", String(cartId));
      router.push(`/checkout/success?${params.toString()}`);
    } catch (err) {
      console.error("Failed to create order", err);
      const fallbackMessage = "Something went wrong while placing your order. Please try again.";
      if (err instanceof Error) {
        setError(err.message || fallbackMessage);
      } else {
        setError(fallbackMessage);
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!items.length) {
    return (
      <div className="space-y-4">
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Checkout
        </h1>
        <p className="text-sm text-zinc-500 dark:text-zinc-400">
          Your cart is empty. Add items to your cart before proceeding to checkout.
        </p>
      </div>
    );
  }

  return (
    <div className="grid gap-10 md:grid-cols-[minmax(0,2fr),minmax(0,2fr)]">
      <section className="space-y-4">
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Checkout
        </h1>
        <p className="text-sm text-zinc-500 dark:text-zinc-400">
          Enter your details to place your order. Payment can be wired in later; this flow
          focuses on creating an order record.
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid gap-3 sm:grid-cols-2">
            <div className="space-y-1">
              <label className="block text-xs font-medium text-zinc-700 dark:text-zinc-300">
                First name
              </label>
              <input
                type="text"
                required
                className="w-full rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none ring-0 transition focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
              />
            </div>
            <div className="space-y-1">
              <label className="block text-xs font-medium text-zinc-700 dark:text-zinc-300">
                Last name
              </label>
              <input
                type="text"
                required
                className="w-full rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none ring-0 transition focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
              />
            </div>
          </div>

          <div className="space-y-1">
            <label className="block text-xs font-medium text-zinc-700 dark:text-zinc-300">
              Email
            </label>
            <input
              type="email"
              required
              className="w-full rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none ring-0 transition focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
            />
          </div>

          <div className="space-y-1">
            <label className="block text-xs font-medium text-zinc-700 dark:text-zinc-300">
              Shipping address
            </label>
            <textarea
              required
              rows={3}
              className="w-full rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none ring-0 transition focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
            />
          </div>

          {error && (
            <p className="text-xs text-red-500 dark:text-red-400">{error}</p>
          )}

          <button
            type="submit"
            disabled={isSubmitting}
            className="inline-flex w-full items-center justify-center rounded-full bg-zinc-900 px-5 py-2 text-sm font-medium text-white transition hover:bg-zinc-700 disabled:cursor-not-allowed disabled:opacity-60 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200"
          >
            {isSubmitting ? "Placing order..." : "Place order"}
          </button>
        </form>
      </section>

      <section className="space-y-4 rounded-2xl border border-zinc-200 bg-zinc-50 p-4 text-sm text-zinc-700 dark:border-zinc-800 dark:bg-zinc-950 dark:text-zinc-200">
        <h2 className="text-base font-semibold text-zinc-900 dark:text-zinc-50">
          Order summary
        </h2>
        <ul className="space-y-2 text-xs">
          {items.map((item) => (
            <li key={item.cartItemId} className="flex items-center justify-between">
              <span>
                Product #{item.productId} x {item.quantity}
              </span>
              <span>${item.totalPrice.toFixed(2)}</span>
            </li>
          ))}
        </ul>
        <div className="mt-3 flex items-center justify-between text-sm font-medium">
          <span>Total</span>
          <span>${subtotal.toFixed(2)}</span>
        </div>
        <p className="text-xs text-zinc-500 dark:text-zinc-400">
          Taxes and shipping are calculated separately. This demo focuses on order creation.
        </p>
      </section>
    </div>
  );
}
