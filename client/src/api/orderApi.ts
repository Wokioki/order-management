import { apiRequest } from "./http";
import type {
    CreateOrderRequest,
    OrderResponse,
    OrderStatus,
} from "../types/order";
import type { PageResponse } from "../types/product";

export function createOrder(request: CreateOrderRequest): Promise<OrderResponse> {
    return apiRequest<OrderResponse>("/orders", {
        method: "POST",
        body: request,
    });
}

export function getMyOrders(): Promise<OrderResponse[]> {
    return apiRequest<OrderResponse[]>("/orders/my");
}

export function getAllOrders(): Promise<PageResponse<OrderResponse>> {
    return apiRequest<PageResponse<OrderResponse>>(
        "/orders?page=0&size=20&sort=createdAt,desc"
    );
}

export function updateOrderStatus(
    orderId: number,
    status: OrderStatus
): Promise<OrderResponse> {
    return apiRequest<OrderResponse>(`/orders/${orderId}/status`, {
        method: "PATCH",
        body: { status },
    });
}