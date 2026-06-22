export type ProductResponse = {
    id: number;
    name: string;
    description: string | null;
    price: number;
    stockQuantity: number;
    imageUrl: string | null;
    categoryId: number;
    categoryName: string;
    createdAt: string;
    updatedAt: string;
};

export type PageResponse<T> = {
    content: T[];
    number: number;
    size: number;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
};

export type ProductRequest = {
    name: string;
    description: string;
    price: number;
    stockQuantity: number;
    imageUrl: string;
    categoryId: number;
};