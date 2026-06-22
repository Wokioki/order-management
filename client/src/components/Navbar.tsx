import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
    const navigate = useNavigate();
    const { user, isAuthenticated, logout } = useAuth();

    function handleLogout() {
        logout();
        navigate("/login");
    }

    return (
        <header className="navbar">
            <Link to="/" className="navbar-logo">
                Order Management
            </Link>

            <nav className="navbar-links">
                <Link to="/products">Products</Link>

                {isAuthenticated && <Link to="/my-orders">My Orders</Link>}

                {(user?.role === "ADMIN" || user?.role === "MANAGER") && (
                    <Link to="/admin/orders">Admin Orders</Link>
                )}
            </nav>

            <div className="navbar-actions">
                {isAuthenticated && user ? (
                    <>
            <span className="navbar-user">
              {user.firstName} · {user.role}
            </span>
                        <button className="secondary-button" onClick={handleLogout}>
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <Link className="secondary-link" to="/login">
                            Sign in
                        </Link>
                        <Link className="primary-link" to="/register">
                            Register
                        </Link>
                    </>
                )}
            </div>
        </header>
    );
}