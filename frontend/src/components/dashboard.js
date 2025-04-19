import React, { useEffect, useState } from "react";
import { api, orderApi, paymentApi } from "../utils/api";
import "./Dashboard.css";
import itemData from "../data/item.json";
import { jsPDF } from "jspdf";

const Dashboard = () => {
  const [user, setUser] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [address, setAddress] = useState("");
  const [contactNumber, setContactNumber] = useState("");
  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(true);
  const [transactionId, setTransactionId] = useState(null);
  const [orders, setOrders] = useState([]);
  const [showOrders, setShowOrders] = useState(false);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await api.get("/users/me");
        setUser(response.data);
      } catch (err) {
        console.error("Error fetching user:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchUser();
  }, []);

  const handleLogout = async () => {
    try {
      await api.post("/auth/logout");
    } catch (err) {
      console.error("Logout failed:", err);
    } finally {
      localStorage.removeItem("jwtToken");
      localStorage.setItem("logout", Date.now());
      window.location.href = "/login";
    }
  };

  const handleSubmitOrder = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await orderApi.post("/api/orders", {
        orderRequest: { quantity, address, contactNumber },
        transaction: {
          userId: user.id,
          userName: user.fullName,
          userEmail: user.email,
          itemId: itemData.itemId,
          itemName: itemData.itemName,
          itemPrice: itemData.itemPrice,
          itemPhotoUrl: itemData.itemPhotoUrl,
        },
      });
      setTransactionId(res.data.transactionId || "N/A");
      alert("Order placed successfully!");
      setQuantity("");
      setAddress("");
      setContactNumber("");
      setShowOrders(false);
    } catch (err) {
      console.error("Order failed:", err.response?.data || err.message);
    } finally {
      setLoading(false);
    }
  };

  const fetchOrders = async () => {
    try {
      const res = await paymentApi.get(`/api/transaction/${user.id}`);
      console.log("Fetched orders:", res.data);
      const sortedOrders = res.data.orders.sort((a, b) => new Date(b.paymentDetails.timestamp) - new Date(a.paymentDetails.timestamp));
      setOrders(sortedOrders);
      setShowOrders(!showOrders);
    } catch (err) {
      console.error("Failed to fetch orders:", err);
      setOrders([]);
    }
  };
  const handleDownloadInvoice = (order) => {
    const doc = new jsPDF();

    doc.setFontSize(16);
    doc.text("Order Invoice", 20, 20);

    doc.setFontSize(12);
    doc.text(`Transaction ID: ${order.transactionId}`, 20, 35);
    doc.text(`Item Name: ${order.orderDetails.itemName}`, 20, 45);
    doc.text(`Quantity: ${order.orderDetails.quantity}`, 20, 55);
    doc.text(`Price: ₹${itemData.itemPrice}`, 20, 65);
    doc.text(`Total: ₹${order.orderDetails.totalAmount}`, 20, 75);
    doc.text(`Address: ${order.orderDetails.address}`, 20, 85);
    doc.text(`Contact: ${order.orderDetails.contactNumber}`, 20, 95);
    doc.text(`Payment ID: ${order.paymentDetails.paymentId}`, 20, 105);
    doc.text(`Date: ${new Date(order.paymentDetails.timestamp).toLocaleString()}`, 20, 115);

    doc.save(`invoice-${order.transactionId}.pdf`);
  };
  if (loading) return <div className="loader">Loading...</div>;

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <h1>Welcome, {user?.fullName}</h1>
        <button className="logout-button" onClick={handleLogout}>
          Logout
        </button>
      </header>

      <section className="product-card">
        <img src={itemData.itemPhotoUrl} alt={itemData.itemName} />
        <h2>{itemData.itemName}</h2>
        <p className="price">INR {itemData.itemPrice}</p>
        <form onSubmit={handleSubmitOrder}>
          <input
            type="number"
            min="1"
            value={quantity}
            onChange={(e) => setQuantity(Number(e.target.value))}
            placeholder="Quantity"
            required
          />
          <input
            type="text"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            placeholder="Delivery Address"
            required
          />
          <input
            type="text"
            value={contactNumber}
            onChange={(e) => setContactNumber(e.target.value)}
            placeholder="Contact Number"
            required
          />
          <button type="submit">Place Order</button>
        </form>
      </section>

      <section className="order-toggle">
        <button onClick={fetchOrders}>
          {showOrders ? "Hide Orders" : "Show My Orders"}
        </button>
      </section>

      {showOrders && (
          <section className="order-history">
          {orders.length > 0 ? (
              orders.map((order, idx) => (
                  <div className="order-card" key={idx}>
                    <h4>{order.orderDetails.itemName}</h4>
                <img src={order.orderDetails.itemPhotoUrl} alt={order.orderDetails.itemName} />
                <div className="order-details">
                  <p><strong>Quantity:</strong> {order.orderDetails.quantity}</p>
                  <p><strong>Total Amount:</strong> INR {order.orderDetails.totalAmount}</p>
                  <p><strong>Address:</strong> {order.orderDetails.address}</p>
                  <p><strong>Contact Number:</strong> {order.orderDetails.contactNumber}</p>
                  {/* <p><strong>Payment ID : </strong>{order.paymentDetails.paymentId}</p> */}
                  <p><strong>Transaction ID : </strong>{order.transactionId}</p>
                  <em>{new Date(order.paymentDetails.timestamp).toLocaleString()}</em>
                  <button
                onClick={() => handleDownloadInvoice(order)}
                className="download-invoice-btn"
              >
                Download Invoice
              </button>
                </div>
              </div>
            ))
          ) : (
            <p className="empty-message">No orders found.</p>
          )}
        </section>
      )}
    </div>
  );
};

export default Dashboard;
