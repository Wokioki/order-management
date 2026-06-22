const API_BASE_URL = "/api";

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

    const response = await fetch(`${API_BASE_URL}${path}`, {
        method: options.method ?? "GET",
        headers,
        body: options.body ? JSON.stringify(options.body) : undefined,
    });

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

    return response.json();
}