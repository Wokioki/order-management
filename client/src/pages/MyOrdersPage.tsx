import { useEffect, useState } from "react";
import { getMyOrders } from "../api/orderApi";
import type { OrderResponse } from "../types/order";

export default function MyOrdersPage() {
    const [orders, setOrders] = useState<OrderResponse[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        getMyOrders()
            .then(setOrders)
            .catch((error) => {
                setError(error instanceof Error ? error.message : "Failed to load orders");
            })
            .finally(() => {
                setIsLoading(false);
            });
    }, []);

    return (
        <main className="app-page">
            <section className="page-header">
                <div>
                    <span className="section-badge">My orders</span>
                    <h1 className="page-title">Order history</h1>
                    <p className="page-subtitle">
                        Track your orders, totals and current order statuses.
                    </p>
                </div>
            </section>

            {isLoading && <div className="state-card">Loading orders...</div>}

            {error && <div className="error-message">{error}</div>}

            {!isLoading && !error && orders.length === 0 && (
                <div className="state-card">You have not placed any orders yet.</div>
            )}

            {!isLoading && !error && orders.length > 0 && (
                <section className="orders-list">
                    {orders.map((order) => (
                        <article className="order-card" key={order.id}>
                            <div className="order-card-header">
                                <div>
                                    <h2>Order #{order.id}</h2>
                                    <p>{new Date(order.createdAt).toLocaleString()}</p>
                                </div>

                                <span className={`status-badge status-${order.status.toLowerCase()}`}>
                  {order.status}
                </span>
                            </div>

                            <div className="order-items">
                                {order.items.map((item) => (
                                    <div className="order-item" key={item.id}>
                                        <div>
                                            <strong>{item.productName}</strong>
                                            <p>
                                                {item.quantity} × ${item.unitPrice.toFixed(2)}
                                            </p>
                                        </div>

                                        <strong>${item.lineTotal.toFixed(2)}</strong>
                                    </div>
                                ))}
                            </div>

                            <div className="order-total">
                                <span>Total</span>
                                <strong>${order.totalAmount.toFixed(2)}</strong>
                            </div>
                        </article>
                    ))}
                </section>
            )}
        </main>
    );
}