import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function HomePage() {
    const { isAuthenticated, user } = useAuth();

    return (
        <main className="home-page">
            <section className="hero-section">
                <div className="hero-content">
                    <span className="section-badge">Full-stack portfolio project</span>

                    <h1 className="hero-title">
                        Manage products, orders and customers in one modern platform.
                    </h1>

                    <p className="hero-subtitle">
                        Order Management is a full-stack application built with Spring Boot,
                        PostgreSQL, JWT authentication and React.
                    </p>

                    <div className="hero-actions">
                        <Link className="primary-link hero-link" to="/products">
                            Browse products
                        </Link>

                        {!isAuthenticated && (
                            <Link className="secondary-link hero-link" to="/register">
                                Create account
                            </Link>
                        )}

                        {isAuthenticated && (
                            <Link className="secondary-link hero-link" to="/my-orders">
                                View my orders
                            </Link>
                        )}
                    </div>

                    {isAuthenticated && user && (
                        <p className="hero-user-note">
                            Signed in as <strong>{user.firstName}</strong> · {user.role}
                        </p>
                    )}
                </div>

                <div className="hero-card">
                    <div className="hero-card-header">
                        <span>System status</span>
                        <strong>Online</strong>
                    </div>

                    <div className="hero-metrics">
                        <div>
                            <span>API</span>
                            <strong>Spring Boot</strong>
                        </div>

                        <div>
                            <span>Auth</span>
                            <strong>JWT</strong>
                        </div>

                        <div>
                            <span>Database</span>
                            <strong>PostgreSQL</strong>
                        </div>
                    </div>

                    <div className="hero-flow">
                        <div>Register/Login</div>
                        <div>Browse products</div>
                        <div>Create order</div>
                        <div>Admin manages status</div>
                    </div>
                </div>
            </section>

            <section className="features-grid">
                <article className="feature-card">
                    <span>🛒</span>
                    <h3>Customer orders</h3>
                    <p>Create orders, view history and track order status.</p>
                </article>

                <article className="feature-card">
                    <span>📦</span>
                    <h3>Product catalog</h3>
                    <p>Search products, view details and manage stock levels.</p>
                </article>

                <article className="feature-card">
                    <span>🛡️</span>
                    <h3>Role-based admin</h3>
                    <p>Admins and managers can manage products, categories and orders.</p>
                </article>
            </section>
        </main>
    );
}