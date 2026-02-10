"use client";

import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { registerUser, loginUser } from "../../lib/auth-api";
import { useAuthStore } from "../../lib/auth-store";

export default function RegisterPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const redirect = searchParams?.get("redirect") || "/";
  const setUser = useAuthStore((state) => state.setUser);

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role] = useState<"customer">("customer");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);

    try {
      setIsSubmitting(true);
      await registerUser({ username, email, password, userRole: role });
      const loginResponse = await loginUser({ email, password });
      setUser(loginResponse.data);
      router.push(redirect);
    } catch (err) {
      const fallback = "Registration failed. Please check your details and try again.";
      if (err instanceof Error) {
        setError(err.message || fallback);
      } else {
        setError(fallback);
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="mx-auto max-w-md space-y-6">
      <div className="space-y-2">
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Create your account
        </h1>
        <p className="text-sm text-zinc-500 dark:text-zinc-400">
          Sign up to save your cart, track orders, and manage your purchases.
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="space-y-1">
          <label className="block text-xs font-medium text-zinc-700 dark:text-zinc-300">
            Username
          </label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            className="w-full rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none ring-0 transition focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
          />
        </div>

        <div className="space-y-1">
          <label className="block text-xs font-medium text-zinc-700 dark:text-zinc-300">
            Email
          </label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            className="w-full rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none ring-0 transition focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
          />
        </div>

        <div className="space-y-1">
          <label className="block text-xs font-medium text-zinc-700 dark:text-zinc-300">
            Password
          </label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
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
          {isSubmitting ? "Creating account..." : "Create account"}
        </button>
      </form>
    </div>
  );
}
