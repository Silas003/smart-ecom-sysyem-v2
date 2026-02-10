"use client";

import { useState } from "react";
import { addItemToCart } from "../../../../lib/cart-api";
import { useCartStore } from "../../../../lib/cart-store";
import { useRouter } from "next/navigation";
import { getCurrentUserId } from "../../../../lib/user";

export default function ProductDetailActions({ productId }: { productId: number }) {
  const [isAdding, setIsAdding] = useState(false);
  const addOrUpdateItem = useCartStore((state) => state.addOrUpdateItem);
  const router = useRouter();

  const handleAddToCart = async () => {
    try {
      setIsAdding(true);
      const userId = getCurrentUserId();
      if (!userId) {
        router.push(`/login?redirect=${encodeURIComponent(`/products/${productId}`)}`);
        return;
      }
      const response = await addItemToCart({ userId, productId, quantity: 1 });
      addOrUpdateItem(response.data);
    } catch (error) {
      console.error("Failed to add product to cart", error);
    } finally {
      setIsAdding(false);
    }
  };

  return (
    <div className="flex flex-wrap gap-3 text-sm">
      <button
        type="button"
        onClick={handleAddToCart}
        disabled={isAdding}
        className="inline-flex flex-1 items-center justify-center rounded-full bg-zinc-900 px-5 py-2 text-sm font-medium text-white transition hover:bg-zinc-700 disabled:cursor-not-allowed disabled:opacity-60 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200 md:flex-none md:px-6"
      >
        {isAdding ? "Adding..." : "Add to cart"}
      </button>
      <button
        type="button"
        className="inline-flex flex-1 items-center justify-center rounded-full border border-zinc-300 px-5 py-2 text-sm font-medium text-zinc-900 transition hover:border-zinc-400 hover:bg-zinc-50 dark:border-zinc-700 dark:text-zinc-50 dark:hover:border-zinc-500 dark:hover:bg-zinc-900 md:flex-none md:px-6"
      >
        Save for later
      </button>
    </div>
  );
}
