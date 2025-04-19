import axios from "axios";

// Create an Axios instance
const api = axios.create({
    baseURL: "http://localhost:8005", // Backend API base URL
});

// Add a request interceptor to include the JWT token in the headers if it's available
api.interceptors.request.use((config) => {
    const token = localStorage.getItem("jwtToken");
    if (token) {
        config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
}, (error) => Promise.reject(error));

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 403) {
            // Token is expired or invalid. Remove from localStorage and redirect.
            localStorage.removeItem("jwtToken");
            // Optionally, you can also redirect to login.
            window.location.href = "/login";
        }
        return Promise.reject(error);
    }
);

const orderApi = axios.create({
    baseURL: "http://localhost:8082", // Backend API base URL for order-related endpoints
});
orderApi.interceptors.request.use(
    (config) => config,
    (error) => Promise.reject(error)
  );

  const paymentApi = axios.create({
    baseURL: "http://localhost:8080", // Backend API base URL for order-related endpoints
});
orderApi.interceptors.request.use(
    (config) => config,
    (error) => Promise.reject(error)
  );

export { api, orderApi , paymentApi };
