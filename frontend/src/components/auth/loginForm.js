import React, { useState } from "react";
import {api} from "../../utils/api"; // Import the API utility
import "./styles/loginForm.css"; // Import your CSS styles
import useRedirectIfAuthenticated from "./useRedirectIfAuthenticated";

const LoginForm = () => {
    useRedirectIfAuthenticated(); // Redirect if already authenticated
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError("");
        
        try {
            const response = await api.post("/auth/login", { email, password });
            localStorage.setItem("jwtToken", response.data.token); // Store the token in localStorage
            console.log("Login successful");
            window.location.href = "/dashboard"; // Redirect to the dashboard
        } catch (err) {
            setError("Login failed: " + err.response?.data || err.message);
        }finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <div className="login-form">
                <h2>Login</h2>
                {error && <div className="error">{error}</div>}
                <form onSubmit={handleLogin}>
                    <div className="input-group">
                        <label htmlFor="email">Email</label>
                        <input
                            type="email"
                            id="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>
                    <div className="input-group">
                        <label htmlFor="password">Password</label>
                        <input
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit" className="btn" disabled={loading}>
                        {loading ? "Logging In..." : "Login"}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default LoginForm;