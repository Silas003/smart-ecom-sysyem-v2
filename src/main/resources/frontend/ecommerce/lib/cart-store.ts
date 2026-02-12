"use client";

import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { CartItem, CartSummary } from "./cart-api";

export type CartState = {
  items: CartItem[];
  cartId?: number;
  userId?: number;
  status?: string;
  hydrateFromServer: (cart: CartSummary) => void;
  addOrUpdateItem: (item: CartItem) => void;
  removeItemLocally: (cartItemId: number) => void;
  clearCart: () => void;
  totalQuantity: () => number;
};

export const useCartStore = create<CartState>()(
  persist(
    (set, get) => ({
      items: [],
      cartId: undefined,
      userId: undefined,
      status: undefined,
      hydrateFromServer: (cart) => {
        set({
          cartId: cart.cartId,
          userId: cart.userId,
          status: cart.status,
          items: cart.items,
        });
      },
      addOrUpdateItem: (item) => {
        const existing = get().items.find((i) => i.id === item.id);
        if (existing) {
          set({
            items: get().items.map((i) =>
              i.id === item.id ? { ...i, ...item } : i
            ),
          });
        } else {
          set({ items: [...get().items, item] });
        }
      },
      removeItemLocally: (cartItemId) => {
        set({ items: get().items.filter((i) => i.id !== cartItemId) });
      },
      clearCart: () =>
        set({ items: [], cartId: undefined, userId: undefined, status: undefined }),
      totalQuantity: () => get().items.reduce((sum, item) => sum + item.quantity, 0),
    }),
    {
      name: "cart-store",
    }
  )
);
