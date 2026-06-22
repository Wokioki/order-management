import { useEffect, useState, type FormEvent } from "react";
import { Link, useParams } from "react-router-dom";
import { createOrder } from "../api/orderApi";
import { getProductById } from "../api/productApi";
import type { ProductResponse } from "../types/product";

export default function ProductDetailsPage() {
    const { id } = useParams();

    const [product, setProduct] = useState<ProductResponse | null>(null);
    const [quantity, setQuantity] = useState(1);
    const [isLoading, setIsLoading] = useState(true);
    const [isOrdering, setIsOrdering] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    useEffect(() => {
        if (!id) {
            setError("Product id is missing");
            setIsLoading(false);
            return;
        }

        getProductById(Number(id))
            .then(setProduct)
            .catch((error) => {
                setError(error instanceof Error ? error.message : "Failed to load product");
            })
            .finally(() => {
                setIsLoading(false);
            });
    }, [id]);

    async function handleCreateOrder(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        if (!product) {
            return;
        }

        setError(null);
        setSuccessMessage(null);
        setIsOrdering(true);

        try {
            const order = await createOrder({
                items: [
                    {
                        productId: product.id,
                        quantity,
                    },
                ],
            });

            setSuccessMessage(`Order #${order.id} created successfully.`);

            const updatedProduct = await getProductById(product.id);
            setProduct(updatedProduct);
            setQuantity(1);
        } catch (error) {
            setError(error instanceof Error ? error.message : "Failed to create order");
        } finally {
            setIsOrdering(false);
        }
    }

    return (
        <main className="app-page">
            <Link className="back-link" to="/products">
                ← Back to products
            </Link>

            {isLoading && <div className="state-card">Loading product...</div>}

            {error && <div className="error-message">{error}</div>}

            {successMessage && <div className="success-message">{successMessage}</div>}

            {!isLoading && !error && product && (
                <section className="details-card">
                    <div className="details-image">
                        <div className="product-placeholder">
                            <span className="product-placeholder-icon">📦</span>
                            <span>{product.categoryName}</span>
                        </div>
                    </div>

                    <div className="details-content">
                        <span className="section-badge">{product.categoryName}</span>

                        <h1 className="details-title">{product.name}</h1>

                        <p className="details-description">
                            {product.description || "No description provided."}
                        </p>

                        <div className="details-stats">
                            <div>
                                <span>Price</span>
                                <strong>${product.price.toFixed(2)}</strong>
                            </div>

                            <div>
                                <span>Stock</span>
                                <strong>{product.stockQuantity}</strong>
                            </div>
                        </div>

                        <form className="order-form" onSubmit={handleCreateOrder}>
                            <div className="form-group">
                                <label className="form-label" htmlFor="quantity">
                                    Quantity
                                </label>
                                <input
                                    id="quantity"
                                    className="form-input"
                                    type="number"
                                    min={1}
                                    max={product.stockQuantity}
                                    value={quantity}
                                    onChange={(event) => {
                                        const nextQuantity = Number(event.target.value);

                                        if (Number.isNaN(nextQuantity)) {
                                            setQuantity(1);
                                            return;
                                        }

                                        setQuantity(Math.min(Math.max(nextQuantity, 1), product.stockQuantity));
                                    }}
                                    disabled={product.stockQuantity === 0}
                                />
                            </div>

                            <button
                                className="primary-button details-button"
                                type="submit"
                                disabled={isOrdering || product.stockQuantity === 0}
                            >
                                {product.stockQuantity === 0
                                    ? "Out of stock"
                                    : isOrdering
                                        ? "Creating order..."
                                        : "Add to order"}
                            </button>
                        </form>
                    </div>
                </section>
            )}
        </main>
    );
}