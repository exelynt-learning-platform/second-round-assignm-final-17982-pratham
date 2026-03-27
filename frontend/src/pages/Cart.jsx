import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';

export default function Cart() {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const { token, authenticated } = useAuth();

  const fetchCart = async () => {
    if (!authenticated) return;
    try {
      const res = await fetch('/api/cart', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      const data = await res.json();
      setCart(data);
      setLoading(false);
    } catch (err) {
      setLoading(false);
    }
  };

  useEffect(() => { fetchCart() }, [authenticated]);

  const removeItem = async (productId) => {
    try {
      await fetch(`/api/cart/remove/${productId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      fetchCart();
    } catch (err) { alert("Failed to remove item") }
  };

  if (!authenticated) return <div className="cart-empty">Please <Link to="/login">Login</Link> to see your cart.</div>;
  if (loading) return <div className="loading">Loading your cart...</div>;
  if (!cart || cart.items.length === 0) return <div className="cart-empty">Your cart is empty. <Link to="/products">Shop Now</Link></div>;

  return (
    <div className="cart-page">
      <h1>Your Shopping Cart</h1>
      <div className="cart-items">
        {cart.items.map(item => (
          <div key={item.productId} className="cart-item glass">
            <div className="item-info">
              <h3>{item.productName}</h3>
              <p>${item.price} x {item.quantity}</p>
            </div>
            <button onClick={() => removeItem(item.productId)} className="remove-btn">Remove</button>
          </div>
        ))}
      </div>
      <div className="cart-summary glass">
        <h2>Total: ${cart.totalPrice}</h2>
        <Link to="/checkout" className="checkout-btn">Proceed to Checkout</Link>
      </div>
    </div>
  );
}
