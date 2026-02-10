"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getUsers, createUser, deleteUser, type User, type UserRole } from "../../../../lib/auth-api";
import { useAuthStore } from "../../../../lib/auth-store";
import { useToast } from "../../../../components/ui/toaster";

export default function AdminUsersPage() {
  const router = useRouter();
  const { user, isAuthenticated } = useAuthStore();
  const { addToast } = useToast();
  const [users, setUsers] = useState<User[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [creating, setCreating] = useState(false);
  const [form, setForm] = useState<{ username: string; email: string; password: string; userRole: UserRole }>(
    { username: "", email: "", password: "", userRole: "customer" }
  );

  useEffect(() => {
    if (!isAuthenticated() || !user) {
      router.push(`/login?redirect=${encodeURIComponent("/admin/users")}`);
      return;
    }
    if (user.userRole !== "admin") {
      router.push("/");
      return;
    }

    getUsers({ page: 0, size: 50 })
      .then((res) => setUsers(res.data.content))
      .catch((err) => {
        console.error("Failed to load users", err);
        setError(err instanceof Error ? err.message : "Failed to load users");
      });
  }, [isAuthenticated, router, user]);

  if (!isAuthenticated() || !user || user.userRole !== "admin") {
    return null;
  }

  const refresh = async () => {
    try {
      const res = await getUsers({ page: 0, size: 50 });
      setUsers(res.data.content);
    } catch (err) {
      console.error("Failed to refresh users", err);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setCreating(true);
      setError(null);
      await createUser(form);
      setForm({ username: "", email: "", password: "", userRole: "customer" });
      await refresh();
      addToast("User created successfully", "success");
    } catch (err) {
      const fallback = "Failed to create user.";
      const message = err instanceof Error ? err.message || fallback : fallback;
      setError(message);
      addToast(message, "error");
    } finally {
      setCreating(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm("Delete this user?")) return;
    try {
      await deleteUser(id);
      setUsers((prev) => prev.filter((u) => u.id !== id));
      addToast("User deleted", "success");
    } catch (err) {
      console.error("Failed to delete user", err);
      const message = "Failed to delete user.";
      setError(message);
      addToast(message, "error");
    }
  };

  if (error) {
    return (
      <div className="space-y-3">
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">Users</h1>
        <p className="text-sm text-red-500 dark:text-red-400">{error}</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">Users</h1>

      <form onSubmit={handleCreate} className="grid gap-3 rounded-2xl border border-zinc-200 bg-white p-4 text-sm dark:border-zinc-800 dark:bg-zinc-950">
        <p className="text-xs font-semibold uppercase tracking-[0.18em] text-zinc-500 dark:text-zinc-400">
          Create user
        </p>
        <div className="grid gap-3 sm:grid-cols-3">
          <input
            type="text"
            placeholder="Username"
            value={form.username}
            onChange={(e) => setForm({ ...form, username: e.target.value })}
            required
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-xs outline-none focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
          />
          <input
            type="email"
            placeholder="Email"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            required
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-xs outline-none focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
          />
          <input
            type="password"
            placeholder="Password"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            required
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2 text-xs outline-none focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
          />
        </div>
        <div className="flex items-center justify-between gap-3">
          <select
            value={form.userRole}
            onChange={(e) => setForm({ ...form, userRole: e.target.value as UserRole })}
            className="w-40 rounded-lg border border-zinc-300 bg-white px-2 py-1 text-xs outline-none focus:border-zinc-500 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-50"
          >
            <option value="customer">customer</option>
            <option value="admin">admin</option>
            <option value="seller">seller</option>
          </select>
          <button
            type="submit"
            disabled={creating}
            className="inline-flex items-center justify-center rounded-full bg-zinc-900 px-4 py-1.5 text-xs font-medium text-white transition hover:bg-zinc-700 disabled:cursor-not-allowed disabled:opacity-60 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-zinc-200"
          >
            {creating ? "Creating..." : "Create"}
          </button>
        </div>
      </form>

      <table className="w-full border-collapse text-left text-sm">
        <thead>
          <tr className="border-b border-zinc-200 text-xs uppercase tracking-[0.18em] text-zinc-500 dark:border-zinc-800 dark:text-zinc-400">
            <th className="py-2 pr-4">ID</th>
            <th className="py-2 pr-4">Username</th>
            <th className="py-2 pr-4">Email</th>
            <th className="py-2 pr-4">Role</th>
            <th className="py-2 pr-4">Created at</th>
            <th className="py-2 pr-4">Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr
              key={u.id}
              className="border-b border-zinc-100 text-xs text-zinc-700 last:border-0 dark:border-zinc-800 dark:text-zinc-200"
            >
              <td className="py-2 pr-4">{u.id}</td>
              <td className="py-2 pr-4">{u.username}</td>
              <td className="py-2 pr-4">{u.email}</td>
              <td className="py-2 pr-4">{u.userRole}</td>
              <td className="py-2 pr-4">{u.createdAt}</td>
              <td className="py-2 pr-4">
                <button
                  type="button"
                  onClick={() => handleDelete(u.id)}
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
