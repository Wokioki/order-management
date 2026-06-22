import { useEffect, useState, type FormEvent } from "react";
import {
    createCategory,
    deleteCategory,
    getCategories,
    updateCategory,
} from "../api/categoryApi";
import type { CategoryResponse } from "../types/category";

type CategoryFormState = {
    name: string;
    description: string;
};

const initialFormState: CategoryFormState = {
    name: "",
    description: "",
};

export default function AdminCategoriesPage() {
    const [categories, setCategories] = useState<CategoryResponse[]>([]);
    const [form, setForm] = useState<CategoryFormState>(initialFormState);

    const [editingCategoryId, setEditingCategoryId] = useState<number | null>(null);
    const [deletingCategoryId, setDeletingCategoryId] = useState<number | null>(null);

    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const [error, setError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    const isEditing = editingCategoryId !== null;

    useEffect(() => {
        loadCategories();
    }, []);

    function loadCategories() {
        setIsLoading(true);
        setError(null);

        getCategories()
            .then(setCategories)
            .catch((error) => {
                setError(error instanceof Error ? error.message : "Failed to load categories");
            })
            .finally(() => {
                setIsLoading(false);
            });
    }

    function updateForm(field: keyof CategoryFormState, value: string) {
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
        };

        try {
            if (isEditing) {
                const updatedCategory = await updateCategory(editingCategoryId, request);

                setCategories((current) =>
                    current.map((category) =>
                        category.id === updatedCategory.id ? updatedCategory : category
                    )
                );

                setSuccessMessage("Category updated successfully.");
            } else {
                const createdCategory = await createCategory(request);

                setCategories((current) => [createdCategory, ...current]);
                setSuccessMessage("Category created successfully.");
            }

            resetForm();
        } catch (error) {
            setError(error instanceof Error ? error.message : "Failed to save category");
        } finally {
            setIsSubmitting(false);
        }
    }

    function startEdit(category: CategoryResponse) {
        setEditingCategoryId(category.id);
        setSuccessMessage(null);
        setError(null);

        setForm({
            name: category.name,
            description: category.description ?? "",
        });
    }

    function resetForm() {
        setForm(initialFormState);
        setEditingCategoryId(null);
    }

    async function handleDeleteCategory(id: number) {
        const confirmed = window.confirm("Delete this category?");

        if (!confirmed) {
            return;
        }

        setError(null);
        setSuccessMessage(null);
        setDeletingCategoryId(id);

        try {
            await deleteCategory(id);

            setCategories((current) =>
                current.filter((category) => category.id !== id)
            );

            setSuccessMessage("Category deleted successfully.");

            if (editingCategoryId === id) {
                resetForm();
            }
        } catch (error) {
            setError(error instanceof Error ? error.message : "Failed to delete category");
        } finally {
            setDeletingCategoryId(null);
        }
    }

    return (
        <main className="app-page">
            <section className="page-header">
                <div>
                    <span className="section-badge">Admin</span>
                    <h1 className="page-title">Categories management</h1>
                    <p className="page-subtitle">
                        Create, update and organize product categories.
                    </p>
                </div>
            </section>

            {error && <div className="error-message">{error}</div>}
            {successMessage && <div className="success-message">{successMessage}</div>}

            <section className="admin-grid">
                <form className="admin-form-card" onSubmit={handleSubmit}>
                    <div className="admin-form-header">
                        <h2>{isEditing ? "Edit category" : "Create category"}</h2>

                        {isEditing && (
                            <button className="secondary-button" type="button" onClick={resetForm}>
                                Cancel
                            </button>
                        )}
                    </div>

                    <div className="form-group">
                        <label className="form-label" htmlFor="categoryName">
                            Category name
                        </label>
                        <input
                            id="categoryName"
                            className="form-input"
                            value={form.name}
                            onChange={(event) => updateForm("name", event.target.value)}
                            placeholder="Electronics"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label" htmlFor="categoryDescription">
                            Description
                        </label>
                        <input
                            id="categoryDescription"
                            className="form-input"
                            value={form.description}
                            onChange={(event) => updateForm("description", event.target.value)}
                            placeholder="Devices, gadgets and accessories"
                        />
                    </div>

                    <button className="primary-button" type="submit" disabled={isSubmitting}>
                        {isSubmitting
                            ? "Saving..."
                            : isEditing
                                ? "Update category"
                                : "Create category"}
                    </button>
                </form>

                <section className="admin-table-card">
                    {isLoading ? (
                        <div className="state-card">Loading categories...</div>
                    ) : (
                        <table className="admin-table">
                            <thead>
                            <tr>
                                <th>Category</th>
                                <th>Description</th>
                                <th>Created</th>
                                <th>Actions</th>
                            </tr>
                            </thead>

                            <tbody>
                            {categories.map((category) => (
                                <tr key={category.id}>
                                    <td>
                                        <strong>{category.name}</strong>
                                    </td>

                                    <td>{category.description || "—"}</td>

                                    <td>{new Date(category.createdAt).toLocaleString()}</td>

                                    <td>
                                        <div className="table-actions">
                                            <button
                                                className="secondary-button"
                                                type="button"
                                                onClick={() => startEdit(category)}
                                            >
                                                Edit
                                            </button>

                                            <button
                                                className="danger-button"
                                                type="button"
                                                onClick={() => handleDeleteCategory(category.id)}
                                                disabled={deletingCategoryId === category.id}
                                            >
                                                {deletingCategoryId === category.id
                                                    ? "Deleting..."
                                                    : "Delete"}
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