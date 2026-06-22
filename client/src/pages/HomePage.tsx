import { Link } from "react-router-dom";

export default function HomePage() {
    return (
        <div>
            <h1>Order Management</h1>
            <p>Frontend is connected and ready.</p>

            <nav>
                <Link to="/login">Login</Link>{" | "}
                <Link to="/register">Register</Link>{" | "}
                <Link to="/products">Products</Link>
            </nav>
        </div>
    );
}