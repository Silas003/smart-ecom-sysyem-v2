import { getOrderById } from "../../../../../lib/orders";

export default async function AccountOrderDetailPage({
  params,
}: {
  params: { id: string };
}) {
  const orderId = Number(params.id);
  if (!Number.isFinite(orderId)) {
    return null;
  }

  const response = await getOrderById(orderId);
  const order = response.data;

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">
        Order #{order.id}
      </h1>
      <div className="space-y-1 text-sm text-zinc-700 dark:text-zinc-200">
        <p>Status: {order.status}</p>
        <p>Total: ${order.totalAmount.toFixed(2)}</p>
      </div>
      <div className="space-y-2 rounded-2xl border border-zinc-200 bg-zinc-50 p-4 text-sm text-zinc-700 dark:border-zinc-800 dark:bg-zinc-950 dark:text-zinc-200">
        <h2 className="text-base font-semibold text-zinc-900 dark:text-zinc-50">
          Items
        </h2>
        <ul className="space-y-1 text-xs">
          {order.items.map((item) => (
            <li key={item.productId} className="flex items-center justify-between">
              <span>
                {item.productName} x {item.quantity}
              </span>
              <span>${item.subtotal.toFixed(2)}</span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
