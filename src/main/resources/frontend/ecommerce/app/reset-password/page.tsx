"use client";

import { useState } from "react";
import { useToast } from "../../components/ui/toaster";

export default function ResetPasswordRequestPage() {
  const { addToast } = useToast();
  const [email, setEmail] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    // Backend does not yet expose a reset-password endpoint; this is a UX stub.
    setSubmitting(true);
    try {
      // Here you would call something like: await requestPasswordReset({ email })
      addToast("If an account exists for this email, you'll receive reset instructions.", "info");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="flex min-h-[70vh] items-center justify-center px-4">
      <div className="w-full max-w-sm rounded-3xl border border-zinc-200 bg-white p-6 shadow-sm dark:border-zinc-800 dark:bg-zinc-950">
        <h1 className="mb-1 text-xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
          Reset password
        </h1>
        <p className="mb-6 text-sm text-zinc-500 dark:text-zinc-400">
          Enter the email associated with your account. If it exists, we&apos;ll send a link to reset your password.
        </p>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-1">
            <label className="text-xs font-medium text-zinc-600 dark:text-zinc-300">
              Email
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="w-full rounded-2xl border border-zinc-200 bg-zinc-50 px-3 py-2 text-sm text-zinc-900 outline-none ring-0 transition focus:border-zinc-400 focus:bg-white dark:border-zinc-800 dark:bg-zinc-900 dark:text-zinc-50 dark:focus:border-zinc-600"
              placeholder="you@example.com"
            />
          </div>
          <button
            type="submit"
            disabled={submitting}
            className="inline-flex w-full items-center justify-center rounded-full bg-zinc-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-zinc-700 disabled:cursor-not-allowed disabled:opacity-70 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200"
          >
            {submitting ? "Sending instructions..." : "Send reset link"}
          </button>
        </form>
      </div>
    </div>
  );
}
