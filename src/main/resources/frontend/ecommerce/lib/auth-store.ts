"use client";

import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { User } from "./auth-api";

export type AuthState = {
  user: User | null;
  setUser: (user: User | null) => void;
  isAuthenticated: () => boolean;
};

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      setUser: (user) => set({ user }),
      isAuthenticated: () => get().user !== null,
    }),
    {
      name: "auth-store",
    }
  )
);
