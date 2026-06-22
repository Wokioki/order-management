import { useEffect, useState } from "react";
import { getAllOrders, updateOrderStatus } from "../api/orderApi";
import type { OrderResponse, OrderStatus } from "../types/order";

const ORDER_STATUSES: OrderStatus[] = [
    "PENDING",
    "PAID",
    "SHIPPED",
    "DELIVERED",
    "CANCELLED",
];

export default function AdminOrdersPage() {
    const [orders, setOrders] = useState<OrderResponse[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [updatingOrderId, setUpdatingOrderId] = useState<number | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        loadOrders();
    }, []);

    function loadOrders() {
        setIsLoading(true);
        setError(null);

        getAllOrders()
            .then((response) => {
                setOrders(response.content);
            })
            .catch((error) => {
                setError(error instanceof Error ? error.message : "Failed to load orders");
            })
            .finally(() => {
                setIsLoading(false);
            });
    }

    async function handleStatusChange(orderId: number, status: OrderStatus) {
        setUpdatingOrderId(orderId);
        setError(null);

        try {
            const updatedOrder = await updateOrderStatus(orderId, status);

            setOrders((currentOrders) =>
                currentOrders.map((order) =>
                    order.id === orderId ? updatedOrder : order
                )
            );
        } catch (error) {
            setError(error instanceof Error ? error.message : "Failed to update order");
        } finally {
            setUpdatingOrderId(null);
        }
    }

    return (
        <main className="app-page">
            <section className="page-header">
                <div>
                    <span className="section-badge">Admin</span>
                    <h1 className="page-title">Orders management</h1>
                    <p className="page-subtitle">
                        Review customer orders and update fulfillment statuses.
                    </p>
                </div>
            </section>

            {isLoading && <div className="state-card">Loading orders...</div>}

            {error && <div className="error-message">{error}</div>}

            {!isLoading && !error && orders.length === 0 && (
                <div className="state-card">No orders found.</div>
            )}

            {!isLoading && orders.length > 0 && (
                <section className="admin-table-card">
                    <table className="admin-table">
                        <thead>
                        <tr>
                            <th>Order</th>
                            <th>Customer</th>
                            <th>Items</th>
                            <th>Total</th>
                            <th>Status</th>
                            <th>Created</th>
                        </tr>
                        </thead>

                        <tbody>
                        {orders.map((order) => (
                            <tr key={order.id}>
                                <td>
                                    <strong>#{order.id}</strong>
                                </td>

                                <td>{order.userEmail}</td>

                                <td>
                                    <div className="table-items">
                                        {order.items.map((item) => (
                                            <span key={item.id}>
                          {item.productName} × {item.quantity}
                        </span>
                                        ))}
                                    </div>
                                </td>

                                <td>
                                    <strong>${order.totalAmount.toFixed(2)}</strong>
                                </td>

                                <td>
                                    <select
                                        className={`status-select status-${order.status.toLowerCase()}`}
                                        value={order.status}
                                        disabled={updatingOrderId === order.id}
                                        onChange={(event) =>
                                            handleStatusChange(
                                                order.id,
                                                event.target.value as OrderStatus
                                            )
                                        }
                                    >
                                        {ORDER_STATUSES.map((status) => (
                                            <option key={status} value={status}>
                                                {status}
                                            </option>
                                        ))}
                                    </select>
                                </td>

                                <td>{new Date(order.createdAt).toLocaleString()}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </section>
            )}
        </main>
    );
}