import React, { use, useEffect, useState } from "react";
import { api, orderApi, paymentApi } from "../utils/api";
import "./Dashboard.css";
import itemData from "../data/item.json";
import { jsPDF } from "jspdf";
import QRCode from "qrcode"

import { loadStripe } from "@stripe/stripe-js";
import {  Elements,  CardElement,  useStripe,  useElements,
} from "@stripe/react-stripe-js";

const stripePromise = loadStripe("pk_test_51REZVAQ5D2JtcDJ3ccch09J91ZFlGkQ2lFyvlpzgbMo3Qb8sUkc9bGZQKcYPBBtzBkLDdvnNdD6UeOjkzQm2iprf00DXvPBaQq");

const PaymentPopup = ({ onClose, onSuccess }) => {
  const stripe = useStripe();
  const elements = useElements();
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);

  const handlePayment = async (e) => {
    e.preventDefault();
    setLoading(true);
    const cardElement = elements.getElement(CardElement);

    const { error, paymentMethod } = await stripe.createPaymentMethod({
      type: "card",
      card: cardElement,
    });

    if (error) {
      setMessage("Payment Failed: " + error.message);
      setLoading(false);
    } else {
      setMessage("Payment Completed");
      onSuccess(paymentMethod.id);
    }
  };

  return (
    <div style={styles.overlay}>
      <form style={styles.popup} onSubmit={handlePayment}>
        <h2>Enter Payment Details</h2>
        <CardElement />
        <button disabled={loading} style={styles.button}>
          {loading ? "Processing..." : "Submit Payment"}
        </button>
        {message && <p>{message}</p>}
        <button type="button" onClick={onClose} style={styles.button}>Close</button>
      </form>
    </div>
  );
};

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
  const [cardNumber, setCardNumber] = useState("");
  const [showPayment, setShowPayment] = useState(false);

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

  const handleSubmitOrder = (e) => {
    e.preventDefault();
    setShowPayment(true);
  };

  const handlePaymentSuccess = async (paymentMethodId) => {
    setCardNumber(paymentMethodId);
    // console.log("Payment Method ID:", paymentMethodId);
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
          cardNumber: paymentMethodId,
        }
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
      setShowPayment(false)
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

  
  const handleDownloadInvoice = async (order) => {
    const doc = new jsPDF();
    const pageWidth = doc.internal.pageSize.getWidth();
    const pageHeight = doc.internal.pageSize.getHeight();
    const margin = 20;
  
    // Generate QR Code
    const qrText = `Transaction ID: ${order.transactionId}
  Item: ${order.orderDetails.itemName}
  Qty: ${order.orderDetails.quantity}
  Total: INR ${order.orderDetails.totalAmount}`;
    const qrDataUrl = await QRCode.toDataURL(qrText);
  
    // ----- Header -----
    doc.setFillColor(44, 62, 80); // Dark blue
    doc.rect(0, 0, pageWidth, 40, "F");
    doc.setFontSize(22);
    doc.setTextColor(255, 255, 255);
    doc.text("INVOICE", margin, 25);
  
    // ----- Order Info Box -----
    let y = 50;
    doc.setFontSize(14);
    doc.setTextColor(33, 33, 33);
    doc.text("Order Summary", margin, y);
  
    doc.setFontSize(12);
    y += 10;
    doc.text(`Transaction ID: ${order.transactionId}`, margin, y); y += 8;
    doc.text(`Item Name: ${order.orderDetails.itemName}`, margin, y); y += 8;
    doc.text(`Quantity: ${order.orderDetails.quantity}`, margin, y); y += 8;
    doc.text(`Price per item: INR ${itemData.itemPrice}`, margin, y); y += 8;
  
    doc.setDrawColor(200);
    doc.line(margin, y + 2, pageWidth - margin, y + 2); // separator
    y += 10;
  
    // ----- Billing Info -----
    doc.setFontSize(14);
    doc.text("Billing Information", margin, y); y += 10;
  
    doc.setFontSize(12);
    doc.text(`Name: ${user.fullName}`, margin, y); y += 8;
    doc.text(`Email: ${user.email}`, margin, y); y += 8;
    doc.text(`Address: ${order.orderDetails.address}`, margin, y); y += 8;
    doc.text(`Contact: ${order.orderDetails.contactNumber}`, margin, y); y += 10;
  
    // ----- Total Box -----
    doc.setFillColor(231, 76, 60); // Red tone
    doc.setTextColor(255);
    doc.setFontSize(16);
    doc.rect(margin, y, pageWidth - 2 * margin, 15, "F");
    doc.text(`Total Amount: INR ${order.orderDetails.totalAmount}`, margin + 10, y + 11);
    y += 30;
  
    // ----- QR Code -----
    doc.setTextColor(33, 33, 33);
    doc.setFontSize(12);
    doc.text("Scan for Order Details", margin, y);
    doc.addImage(qrDataUrl, "PNG", margin, y + 5, 40, 40);
  
    // ----- Footer -----
    doc.setFontSize(10);
    doc.setTextColor(150);
    doc.text("Thank you for your purchase!", margin, pageHeight - 20);
  
    // Save
    doc.save(`Invoice_${user.fullName}_${order.transactionId}.pdf`);
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
        {showPayment && (
        <Elements stripe={stripePromise}>
          <PaymentPopup
            onClose={() => setShowPayment(false)}
            onSuccess={handlePaymentSuccess}
          />
        </Elements>
      )}
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
const styles = {
  overlay: {
    position: "fixed",
    top: 0,
    left: 0,
    width: "100vw",
    height: "100vh",
    backgroundColor: "rgba(0, 0, 0, 0.5)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    zIndex: 1000,
  },
  popup: {
    background: "white",
    padding: 20,
    borderRadius: 10,
    width: 400,
  },
  button: {
    marginTop: 10,
    padding: 10,
    backgroundColor: "#4CAF50",
    color: "white",
    border: "none",
    borderRadius: 5,
    cursor: "pointer",
  },
};
export default Dashboard;
