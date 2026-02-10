"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useCartStore } from "../../lib/cart-store";

const navItems = [
  { href: "/", label: "Home" },
  { href: "/products", label: "Shop" },
  { href: "/account/orders", label: "My orders" },
];

function classNames(...classes: (string | false | null | undefined)[]) {
  return classes.filter(Boolean).join(" ");
}

export function Header() {
  const pathname = usePathname();
  const totalQuantity = useCartStore((state) => state.totalQuantity());

  return (
    <header className="sticky top-0 z-30 border-b border-zinc-200 bg-white/80 backdrop-blur-sm dark:border-zinc-800 dark:bg-zinc-950/80">
      <div className="mx-auto flex h-16 max-w-6xl items-center justify-between px-4 sm:px-6 lg:px-8">
        <div className="flex items-center gap-2">
          <Link href="/" className="flex items-center gap-2">
            <span className="rounded-full bg-zinc-900 px-2 py-1 text-xs font-semibold uppercase tracking-wide text-white dark:bg-zinc-100 dark:text-zinc-900">
              Shop
            </span>
            <span className="text-sm font-semibold text-zinc-900 dark:text-zinc-50">
              Ecommerce
            </span>
          </Link>
        </div>

        <nav className="hidden items-center gap-6 text-sm font-medium text-zinc-600 sm:flex dark:text-zinc-300">
          {navItems.map((item) => {
            const active =
              item.href === "/"
                ? pathname === "/"
                : pathname.startsWith(item.href);

            return (
              <Link
                key={item.href}
                href={item.href}
                className={classNames(
                  "transition-colors hover:text-zinc-900 dark:hover:text-zinc-50",
                  active && "text-zinc-900 dark:text-zinc-50"
                )}
              >
                {item.label}
              </Link>
            );
          })}
        </nav>

        <div className="flex items-center gap-4">
          <form
            action="/search"
            className="hidden items-center gap-2 rounded-full border border-zinc-200 bg-white px-3 py-1 text-xs text-zinc-500 shadow-sm sm:flex dark:border-zinc-800 dark:bg-zinc-900 dark:text-zinc-400"
          >
            <input
              type="text"
              name="q"
              placeholder="Search products"
              className="w-40 bg-transparent text-xs outline-none placeholder:text-zinc-400 dark:placeholder:text-zinc-500"
            />
          </form>

          <Link
            href="/cart"
            className="relative flex items-center justify-center rounded-full border border-zinc-200 bg-white p-2 text-zinc-700 shadow-sm transition-colors hover:bg-zinc-50 dark:border-zinc-800 dark:bg-zinc-900 dark:text-zinc-200 dark:hover:bg-zinc-800"
          >
            <span className="sr-only">View cart</span>
            <span className="text-lg">🛒</span>
            {totalQuantity > 0 && (
              <span className="absolute -right-1 -top-1 inline-flex h-4 min-w-4 items-center justify-center rounded-full bg-zinc-900 px-1 text-[10px] font-semibold text-white dark:bg-zinc-100 dark:text-zinc-900">
                {totalQuantity}
              </span>
            )}
          </Link>
        </div>
      </div>
    </header>
  );
}
