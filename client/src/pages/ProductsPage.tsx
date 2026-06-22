import {useEffect, useState} from "react";
import {getProducts} from "../api/productApi";
import type {ProductResponse} from "../types/product";
import { Link } from "react-router-dom";

export default function ProductsPage() {
    const [products, setProducts] = useState<ProductResponse[]>([]);
    const [search, setSearch] = useState("");
    const [sort, setSort] = useState("createdAt,desc");
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        setIsLoading(true);
        setError(null);

        getProducts({
            search,
            sort,
            page: 0,
            size: 12,
        })
            .then((response) => {
                setProducts(response.content);
            })
            .catch((error) => {
                setError(error instanceof Error ? error.message : "Failed to load products");
            })
            .finally(() => {
                setIsLoading(false);
            });
    }, [search, sort]);

    return (
        <main className="app-page">
            <section className="page-header">
                <div>
                    <span className="section-badge">Product catalog</span>
                    <h1 className="page-title">Products</h1>
                    <p className="page-subtitle">
                        Browse available products and manage inventory from one place.
                    </p>
                </div>
            </section>

            <section className="toolbar">
                <input
                    className="toolbar-input"
                    value={search}
                    onChange={(event) => setSearch(event.target.value)}
                    placeholder="Search products..."
                />

                <div className="select-wrapper">
                    <select
                        className="toolbar-select"
                        value={sort}
                        onChange={(event) => setSort(event.target.value)}
                    >
                        <option value="createdAt,desc">Newest first</option>
                        <option value="price,asc">Price: low to high</option>
                        <option value="price,desc">Price: high to low</option>
                        <option value="name,asc">Name: A to Z</option>
                    </select>
                </div>
            </section>

            {isLoading && <div className="state-card">Loading products...</div>}

            {error && <div className="error-message">{error}</div>}

            {!isLoading && !error && products.length === 0 && (
                <div className="state-card">No products found.</div>
            )}

            {!isLoading && !error && products.length > 0 && (
                <section className="product-grid">
                    {products.map((product) => (
                        <article className="product-card" key={product.id}>
                            <div className="product-image">
                                <div className="product-placeholder">
                                    <span className="product-placeholder-icon">📦</span>
                                    <span>{product.categoryName}</span>
                                </div>
                            </div>

                            <div className="product-content">
                                <div className="product-meta">
                                    <span>{product.categoryName}</span>
                                    <span>{product.stockQuantity} in stock</span>
                                </div>

                                <h2 className="product-title">{product.name}</h2>

                                <p className="product-description">
                                    {product.description || "No description provided."}
                                </p>

                                <div className="product-footer">
                                    <strong>${product.price.toFixed(2)}</strong>
                                    <Link className="secondary-button" to={`/products/${product.id}`}>
                                        View details
                                    </Link>
                                </div>
                            </div>
                        </article>
                    ))}
                </section>
            )}
        </main>
    );
}