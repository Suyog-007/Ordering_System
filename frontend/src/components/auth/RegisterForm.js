import React, { use, useState } from "react";
import {api} from "../../utils/api"; // Import the API utility
import "./styles/RegisterForm.css"; // Import your CSS styles  
import useRedirectIfAuthenticated from "./useRedirectIfAuthenticated"; // Custom hook to redirect if authenticated

const RegisterForm = () => {
    useRedirectIfAuthenticated(); // Redirect if already authenticated
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [fullName, setFullName] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleRegister = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError("");
        try {
            const response = await api.post("/auth/signup", { email, password, fullName });
            console.log("Registration successful");
            window.location.href = "/login"; // Redirect to login page after successful registration
        } catch (err) {
            setError("Registration failed: " + err.response?.data || err.message);
        }finally {
            setLoading(false);
        }
    };

    return (
        <div className="register-container">
            <div className="register-form">
                <h2>Register</h2>
                {error && <div className="error">{error}</div>}
                <form onSubmit={handleRegister}>
                    <div className="input-group">
                        <label htmlFor="fullName">Full Name</label>
                        <input
                            type="text"
                            id="fullName"
                            value={fullName}
                            onChange={(e) => setFullName(e.target.value)}
                            required
                        />
                    </div>
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
                        {loading ? "Registering..." : "Register"}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default RegisterForm;