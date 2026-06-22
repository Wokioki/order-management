export type Role = "CUSTOMER" | "MANAGER" | "ADMIN";

export type UserResponse = {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    role: Role;
};

export type RegisterRequest = {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
};

export type LoginRequest = {
    email: string;
    password: string;
};

export type AuthResponse = {
    token: string;
    tokenType: string;
    expiresIn: number;
    user: UserResponse;
};