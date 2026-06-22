export type CategoryRequest = {
    name: string;
    description: string;
};

export type CategoryResponse = {
    id: number;
    name: string;
    description: string | null;
    createdAt: string;
    updatedAt: string;
};