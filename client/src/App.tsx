import { Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import ProductsPage from "./pages/ProductsPage";
import RegisterPage from "./pages/RegisterPage";
import ProductDetailsPage from "./pages/ProductDetailsPage";
import MyOrdersPage from "./pages/MyOrdersPage";
import AdminOrdersPage from "./pages/AdminOrdersPage";
import AdminProductsPage from "./pages/AdminProductsPage";
import AdminCategoriesPage from "./pages/AdminCategoriesPage";

function AppLayout({ children }: { children: React.ReactNode }) {
    return (
        <>
            <Navbar />
            {children}
        </>
    );
}

export default function App() {
    return (
        <Routes>
            <Route
                path="/"
                element={
                    <AppLayout>
                        <HomePage />
                    </AppLayout>
                }
            />

            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            <Route
                path="/products"
                element={
                    <AppLayout>
                        <ProductsPage />
                    </AppLayout>
                }
            />

            <Route
                path="/products/:id"
                element={
                    <AppLayout>
                        <ProductDetailsPage />
                    </AppLayout>
                }
            />

            <Route
                path="/my-orders"
                element={
                    <AppLayout>
                        <MyOrdersPage />
                    </AppLayout>
                }
            />

            <Route
                path="/admin/orders"
                element={
                    <AppLayout>
                        <AdminOrdersPage />
                    </AppLayout>
                }
            />

            <Route
                path="/admin/products"
                element={
                    <AppLayout>
                        <AdminProductsPage />
                    </AppLayout>
                }
            />

            <Route
                path="/admin/categories"
                element={
                    <AppLayout>
                        <AdminCategoriesPage />
                    </AppLayout>
                }
            />
        </Routes>
    );
}