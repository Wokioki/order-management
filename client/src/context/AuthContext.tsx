import {
    createContext,
    useContext,
    useEffect,
    useState,
    type ReactNode,
} from "react";
import { getCurrentUser, login as loginRequest } from "../api/authApi";
import type { LoginRequest, UserResponse } from "../types/auth";

type AuthContextValue = {
    user: UserResponse | null;
    token: string | null;
    isAuthenticated: boolean;
    login: (request: LoginRequest) => Promise<void>;
    logout: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

type AuthProviderProps = {
    children: ReactNode;
};

export function AuthProvider({ children }: AuthProviderProps) {
    const [user, setUser] = useState<UserResponse | null>(null);
    const [token, setToken] = useState<string | null>(() =>
        localStorage.getItem("token")
    );

    const isAuthenticated = user !== null && token !== null;

    useEffect(() => {
        if (!token) {
            setUser(null);
            return;
        }

        getCurrentUser()
            .then(setUser)
            .catch(() => {
                localStorage.removeItem("token");
                setToken(null);
                setUser(null);
            });
    }, [token]);

    async function login(request: LoginRequest) {
        const response = await loginRequest(request);

        localStorage.setItem("token", response.token);
        setToken(response.token);
        setUser(response.user);
    }

    function logout() {
        localStorage.removeItem("token");
        setToken(null);
        setUser(null);
    }

    return (
        <AuthContext.Provider
            value={{
                user,
                token,
                isAuthenticated,
                login,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error("useAuth must be used inside AuthProvider");
    }

    return context;
}