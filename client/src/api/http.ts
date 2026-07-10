const API_BASE_URL = (
    import.meta.env.VITE_API_BASE_URL || "http://localhost:8081"
).replace(/\/+$/, "");

type RequestOptions = {
    method?: string;
    body?: unknown;
    token?: string | null;
};

export async function apiRequest<T>(
    path: string,
    options: RequestOptions = {}
): Promise<T> {
    const headers: HeadersInit = {
        "Content-Type": "application/json",
    };

    const token = options.token ?? localStorage.getItem("token");

    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    const normalizedPath = path.startsWith("/") ? path : `/${path}`;

    const response = await fetch(
        `${API_BASE_URL}/api${normalizedPath}`,
        {
            method: options.method ?? "GET",
            headers,
            body: options.body
                ? JSON.stringify(options.body)
                : undefined,
        }
    );

    if (!response.ok) {
        let message = "Request failed";

        try {
            const errorBody = await response.json();
            message = errorBody.message ?? message;
        } catch {
        }

        throw new Error(message);
    }

    if (response.status === 204) {
        return undefined as T;
    }

    return response.json() as Promise<T>;
}