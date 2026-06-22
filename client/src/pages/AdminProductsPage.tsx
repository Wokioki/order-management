import { useEffect, useState, type FormEvent } from "react";
import { getCategories } from "../api/categoryApi";
import {
    createProduct,
    deleteProduct,
    getProducts,
    updateProduct,
} from "../api/productApi";
import type { CategoryResponse } from "../types/category";
import type { ProductResponse } from "../types/product";

type ProductFormState = {
    name: string;
    description: string;
    price: string;
    stockQuantity: string;
    imageUrl: string;
    categoryId: string;
};

const initialFormState: ProductFormState = {
    name: "",
    description: "",
    price: "",
    stockQuantity: "",
    imageUrl: "",
    categoryId: "",
};

export default function AdminProductsPage() {
    const [products, setProducts] = useState<ProductResponse[]>([]);
    const [categories, setCategories] = useState<CategoryResponse[]>([]);
    const [form, setForm] = useState<ProductFormState>(initialFormState);

    const [editingProductId, setEditingProductId] = useState<number | null>(null);
    const [deletingProductId, setDeletingProductId] = useState<number | null>(null);

    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const [error, setError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    const isEditing = editingProductId !== null;

    useEffect(() => {
        loadData();
    }, []);

    function loadData() {
        setIsLoading(true);
        setError(null);

        Promise.all([
            getProducts({ page: 0, size: 50, sort: "createdAt,desc" }),
            getCategories(),
        ])
            .then(([productsResponse, categoriesResponse]) => {
                setProducts(productsResponse.content);
                setCategories(categoriesResponse);
            })
            .catch((error) => {
                setError(error instanceof Error ? error.message : "Failed to load admin data");
            })
            .finally(() => {
                setIsLoading(false);
            });
    }

    function updateForm(field: keyof ProductFormState, value: string) {
        setForm((current) => ({
            ...current,
            [field]: value,
        }));
    }

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        setError(null);
        setSuccessMessage(null);
        setIsSubmitting(true);

        const request = {
            name: form.name,
            description: form.description,
            price: Number(form.price),
            stockQuantity: Number(form.stockQuantity),
            imageUrl: form.imageUrl,
            categoryId: Number(form.categoryId),
        };

        try {
            if (isEditing) {
                const updatedProduct = await updateProduct(editingProductId, request);

                setProducts((current) =>
                    current.map((product) =>
                        product.id === updatedProduct.id ? updatedProduct : product
                    )
                );

                setSuccessMessage("Product updated successfully.");
            } else {
                const createdProduct = await createProduct(request);

                setProducts((current) => [createdProduct, ...current]);
                setSuccessMessage("Product created successfully.");
            }

            resetForm();
        } catch (error) {
            setError(error instanceof Error ? error.message : "Failed to save product");
        } finally {
            setIsSubmitting(false);
        }
    }

    function startEdit(product: ProductResponse) {
        setEditingProductId(product.id);
        setSuccessMessage(null);
        setError(null);

        setForm({
            name: product.name,
            description: product.description ?? "",
            price: String(product.price),
            stockQuantity: String(product.stockQuantity),
            imageUrl: product.imageUrl ?? "",
            categoryId: String(product.categoryId),
        });
    }

    function resetForm() {
        setForm(initialFormState);
        setEditingProductId(null);
    }

    async function handleDeleteProduct(id: number) {
        const confirmed = window.confirm("Delete this product?");

        if (!confirmed) {
            return;
        }

        setError(null);
        setSuccessMessage(null);
        setDeletingProductId(id);

        try {
            await deleteProduct(id);

            setProducts((current) => current.filter((product) => product.id !== id));
            setSuccessMessage("Product deleted successfully.");

            if (editingProductId === id) {
                resetForm();
            }
        } catch (error) {
            setError(error instanceof Error ? error.message : "Failed to delete product");
        } finally {
            setDeletingProductId(null);
        }
    }

    return (
        <main className="app-page">
            <section className="page-header">
                <div>
                    <span className="section-badge">Admin</span>
                    <h1 className="page-title">Products management</h1>
                    <p className="page-subtitle">
                        Create, update and maintain the product catalog.
                    </p>
                </div>
            </section>

            {error && <div className="error-message">{error}</div>}
            {successMessage && <div className="success-message">{successMessage}</div>}

            <section className="admin-grid">
                <form className="admin-form-card" onSubmit={handleSubmit}>
                    <div className="admin-form-header">
                        <h2>{isEditing ? "Edit product" : "Create product"}</h2>

                        {isEditing && (
                            <button className="secondary-button" type="button" onClick={resetForm}>
                                Cancel
                            </button>
                        )}
                    </div>

                    <div className="form-group">
                        <label className="form-label" htmlFor="name">
                            Product name
                        </label>
                        <input
                            id="name"
                            className="form-input"
                            value={form.name}
                            onChange={(event) => updateForm("name", event.target.value)}
                            placeholder="Laptop Pro"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label" htmlFor="description">
                            Description
                        </label>
                        <input
                            id="description"
                            className="form-input"
                            value={form.description}
                            onChange={(event) => updateForm("description", event.target.value)}
                            placeholder="Powerful laptop for work"
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label" htmlFor="price">
                                Price
                            </label>
                            <input
                                id="price"
                                className="form-input"
                                type="number"
                                min="0.01"
                                step="0.01"
                                value={form.price}
                                onChange={(event) => updateForm("price", event.target.value)}
                                placeholder="1499.99"
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label" htmlFor="stockQuantity">
                                Stock
                            </label>
                            <input
                                id="stockQuantity"
                                className="form-input"
                                type="number"
                                min="0"
                                value={form.stockQuantity}
                                onChange={(event) => updateForm("stockQuantity", event.target.value)}
                                placeholder="10"
                                required
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label className="form-label" htmlFor="categoryId">
                            Category
                        </label>
                        <select
                            id="categoryId"
                            className="form-input"
                            value={form.categoryId}
                            onChange={(event) => updateForm("categoryId", event.target.value)}
                            required
                        >
                            <option value="">Select category</option>
                            {categories.map((category) => (
                                <option key={category.id} value={category.id}>
                                    {category.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label className="form-label" htmlFor="imageUrl">
                            Image URL
                        </label>
                        <input
                            id="imageUrl"
                            className="form-input"
                            value={form.imageUrl}
                            onChange={(event) => updateForm("imageUrl", event.target.value)}
                            placeholder="https://example.com/image.jpg"
                        />
                    </div>

                    <button className="primary-button" type="submit" disabled={isSubmitting}>
                        {isSubmitting
                            ? "Saving..."
                            : isEditing
                                ? "Update product"
                                : "Create product"}
                    </button>
                </form>

                <section className="admin-table-card">
                    {isLoading ? (
                        <div className="state-card">Loading products...</div>
                    ) : (
                        <table className="admin-table">
                            <thead>
                            <tr>
                                <th>Product</th>
                                <th>Category</th>
                                <th>Price</th>
                                <th>Stock</th>
                                <th>Actions</th>
                            </tr>
                            </thead>

                            <tbody>
                            {products.map((product) => (
                                <tr key={product.id}>
                                    <td>
                                        <strong>{product.name}</strong>
                                        <p className="table-muted">{product.description}</p>
                                    </td>

                                    <td>{product.categoryName}</td>

                                    <td>${product.price.toFixed(2)}</td>

                                    <td>{product.stockQuantity}</td>

                                    <td>
                                        <div className="table-actions">
                                            <button
                                                className="secondary-button"
                                                type="button"
                                                onClick={() => startEdit(product)}
                                            >
                                                Edit
                                            </button>

                                            <button
                                                className="danger-button"
                                                type="button"
                                                onClick={() => handleDeleteProduct(product.id)}
                                                disabled={deletingProductId === product.id}
                                            >
                                                {deletingProductId === product.id ? "Deleting..." : "Delete"}
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    )}
                </section>
            </section>
        </main>
    );
}