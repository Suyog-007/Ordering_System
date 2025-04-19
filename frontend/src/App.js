import React from "react";
import { useEffect } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import LoginForm from "./components/auth/loginForm";
import RegisterForm from "./components/auth/RegisterForm";
import Dashboard from "./components/dashboard";
import PrivateRoute from "./components/PrivateRoute";

const App = () => {
    useEffect(() => {
        const syncLogout = (event) => {
            if (event.key === "logout") {
                // Token removed in another tab → force logout here
                window.location.href = "/login";
            }
        };

        window.addEventListener("storage", syncLogout);

        return () => {
            window.removeEventListener("storage", syncLogout);
        };
    }, []);
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<LoginForm />} />
                <Route path="/register" element={<RegisterForm />} />
                <Route
                    path="/dashboard"
                    element={
                        <PrivateRoute>
                            <Dashboard />
                        </PrivateRoute>
                    }
                />
            </Routes>
        </Router>
    );
};

export default App;
