import { apiRequest } from "./http";
import type { CategoryRequest, CategoryResponse } from "../types/category";

export function getCategories(): Promise<CategoryResponse[]> {
    return apiRequest<CategoryResponse[]>("/categories");
}

export function createCategory(
    request: CategoryRequest
): Promise<CategoryResponse> {
    return apiRequest<CategoryResponse>("/categories", {
        method: "POST",
        body: request,
    });
}

export function updateCategory(
    id: number,
    request: CategoryRequest
): Promise<CategoryResponse> {
    return apiRequest<CategoryResponse>(`/categories/${id}`, {
        method: "PUT",
        body: request,
    });
}

export function deleteCategory(id: number): Promise<void> {
    return apiRequest<void>(`/categories/${id}`, {
        method: "DELETE",
    });
}