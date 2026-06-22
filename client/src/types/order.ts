export type OrderStatus = "PENDING" | "PAID" | "SHIPPED" | "DELIVERED" | "CANCELLED";

export type OrderItemRequest = {
    productId: number;
    quantity: number;
};

export type CreateOrderRequest = {
    items: OrderItemRequest[];
};

export type OrderItemResponse = {
    id: number;
    productId: number;
    productName: string;
    unitPrice: number;
    quantity: number;
    lineTotal: number;
};

export type OrderResponse = {
    id: number;
    userId: number;
    userEmail: string;
    status: OrderStatus;
    totalAmount: number;
    items: OrderItemResponse[];
    createdAt: string;
    updatedAt: string;
};