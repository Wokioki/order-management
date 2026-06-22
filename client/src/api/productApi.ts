import { apiRequest } from "./http";
import type {
    PageResponse,
    ProductRequest,
    ProductResponse,
} from "../types/product";

export type ProductQueryParams = {
    search?: string;
    page?: number;
    size?: number;
    sort?: string;
};

export function getProducts(
    params: ProductQueryParams = {}
): Promise<PageResponse<ProductResponse>> {
    const searchParams = new URLSearchParams();

    searchParams.set("page", String(params.page ?? 0));
    searchParams.set("size", String(params.size ?? 12));
    searchParams.set("sort", params.sort ?? "createdAt,desc");

    if (params.search && params.search.trim()) {
        searchParams.set("search", params.search.trim());
    }

    return apiRequest<PageResponse<ProductResponse>>(
        `/products?${searchParams.toString()}`
    );
}

export function getProductById(id: number): Promise<ProductResponse> {
    return apiRequest<ProductResponse>(`/products/${id}`);
}

export function createProduct(
    request: ProductRequest
): Promise<ProductResponse> {
    return apiRequest<ProductResponse>("/products", {
        method: "POST",
        body: request,
    });
}

export function updateProduct(
    id: number,
    request: ProductRequest
): Promise<ProductResponse> {
    return apiRequest<ProductResponse>(`/products/${id}`, {
        method: "PUT",
        body: request,
    });
}

export function deleteProduct(id: number): Promise<void> {
    return apiRequest<void>(`/products/${id}`, {
        method: "DELETE",
    });
}