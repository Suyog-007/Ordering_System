// src/hooks/useRedirectIfAuthenticated.js
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const useRedirectIfAuthenticated = () => {
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("jwtToken");
        if (token) {
            navigate("/dashboard");
        }
    }, []);
};

export default useRedirectIfAuthenticated;
