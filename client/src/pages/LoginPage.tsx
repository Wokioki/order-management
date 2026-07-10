import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

type DemoAccount = {
    role: string;
    description: string;
    email: string;
    password: string;
};

const demoAccounts: DemoAccount[] = [
    {
        role: "Administrator",
        description: "Manage products, categories and customer orders",
        email: "admin@example.com",
        password: "admin123",
    },
    {
        role: "Customer",
        description: "Browse products and manage personal orders",
        email: "customer@example.com",
        password: "customer123",
    },
];

export default function LoginPage() {
    const navigate = useNavigate();
    const { login } = useAuth();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    function selectDemoAccount(account: DemoAccount) {
        setEmail(account.email);
        setPassword(account.password);
        setError(null);
    }

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        setError(null);
        setIsSubmitting(true);

        try {
            await login({
                email,
                password,
            });

            navigate("/products");
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : "Login failed"
            );
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <main className="page">
            <section className="auth-card">
                <div className="auth-header">
                    <span className="auth-badge">
                        Order Management
                    </span>

                    <h1 className="auth-title">
                        Welcome back
                    </h1>

                    <p className="auth-subtitle">
                        Sign in to manage products, categories and customer
                        orders.
                    </p>
                </div>

                <form className="form" onSubmit={handleSubmit}>
                    {error && (
                        <div className="error-message">
                            {error}
                        </div>
                    )}

                    <div className="form-group">
                        <label className="form-label" htmlFor="email">
                            Email
                        </label>

                        <input
                            id="email"
                            className="form-input"
                            type="email"
                            value={email}
                            onChange={(event) =>
                                setEmail(event.target.value)
                            }
                            placeholder="you@example.com"
                            autoComplete="email"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label" htmlFor="password">
                            Password
                        </label>

                        <input
                            id="password"
                            className="form-input"
                            type="password"
                            value={password}
                            onChange={(event) =>
                                setPassword(event.target.value)
                            }
                            placeholder="Enter your password"
                            autoComplete="current-password"
                            required
                        />
                    </div>

                    <button
                        className="primary-button"
                        type="submit"
                        disabled={isSubmitting}
                    >
                        {isSubmitting ? "Signing in..." : "Sign in"}
                    </button>
                </form>

                <div className="demo-accounts">
                    <div className="demo-accounts__heading">
                        <span className="demo-accounts__title">
                            Demo accounts
                        </span>

                        <span className="demo-accounts__hint">
                            Select an account to fill the form
                        </span>
                    </div>

                    <div className="demo-accounts__list">
                        {demoAccounts.map((account) => (
                            <button
                                key={account.email}
                                className="demo-account"
                                type="button"
                                onClick={() => selectDemoAccount(account)}
                            >
                                <span className="demo-account__content">
                                    <span className="demo-account__role">
                                        {account.role}
                                    </span>

                                    <span className="demo-account__description">
                                        {account.description}
                                    </span>

                                    <span className="demo-account__credentials">
                                        <span>{account.email}</span>
                                        <span>{account.password}</span>
                                    </span>
                                </span>

                                <span className="demo-account__action">
                                    Use
                                </span>
                            </button>
                        ))}
                    </div>
                </div>

                <p className="auth-footer">
                    New here?{" "}
                    <Link to="/register">
                        Create an account
                    </Link>
                </p>
            </section>
        </main>
    );
}