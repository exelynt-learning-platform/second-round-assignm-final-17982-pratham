import { useState, useEffect } from 'react'
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Login from './pages/Login'
import Register from './pages/Register'
import Cart from './pages/Cart'
import Checkout from './pages/Checkout'
import './App.css'

function Navbar({ cartCount }) {
  const { user, logout, authenticated } = useAuth()

  return (
    <nav className="navbar">
      <Link to="/" className="logo">Premium <span>Store</span></Link>
      <div className="nav-links">
        <Link to="/" className="nav-btn">Home</Link>
        <Link to="/products" className="nav-btn">Products</Link>
        {authenticated ? (
          <div className="user-info">
            <span className="user-name">Welcome, {user?.username}</span>
            <button onClick={logout} className="logout-btn">Log Out</button>
          </div>
        ) : (
          <Link to="/login" className="login-btn">Log In</Link>
        )}
        <Link to="/cart" className="cart-btn">Cart ({cartCount})</Link>
      </div>
    </nav>
  )
}

function ProductList({ addToCart }) {
  const [products, setProducts] = useState([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    fetch('/api/products')
      .then(res => res.json())
      .then(data => {
        setProducts(Array.isArray(data) ? data : [])
        setIsLoading(false)
      })
      .catch(() => setIsLoading(false))
  }, [])

  if (isLoading) return <div className="loading">Loading products...</div>

  return (
    <section className="product-grid">
      {products.map(p => (
        <div key={p.id} className="product-card">
          <img src={p.imageUrl} alt={p.name} />
          <h3>{p.name}</h3>
          <p>${p.price}</p>
          <button className="add-btn" onClick={() => addToCart(p.id)}>Add to Cart</button>
        </div>
      ))}
    </section>
  )
}

function AppContent() {
  const [cartCount, setCartCount] = useState(0)
  const { authenticated, token } = useAuth()

  const fetchCart = async () => {
    if (!authenticated) return
    try {
      const res = await fetch('/api/cart', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      const data = await res.json();
      setCartCount(data.items?.length || 0);
    } catch (err) { console.error("Cart fetch error", err) }
  };

  useEffect(() => { fetchCart() }, [authenticated])

  const addToCart = async (productId) => {
    if (!authenticated) {
      alert("Please log in first!");
      return;
    }
    try {
      const res = await fetch(`/api/cart/add?productId=${productId}&quantity=1`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        alert("Success! Product added to your cart.");
        fetchCart();
      } else {
        const errorData = await res.json();
        alert(`Failed: ${errorData.message || "Unknown Error"}`);
      }
    } catch (err) { alert("Connection Error: Is the backend running?") }
  };

  return (
    <Router>
      <div className="app-container">
        <Navbar cartCount={cartCount} />
        <Routes>
          <Route path="/" element={<Hero />} />
          <Route path="/products" element={<ProductList addToCart={addToCart} />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/checkout" element={<Checkout />} />
        </Routes>
      </div>
    </Router>
  )
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  )
}

function Hero() {
  return (
    <main className="hero-section">
      <div className="hero-content">
        <h2>Modern Tech & Fashion</h2>
        <p>Curated collections for the modern minimalist.</p>
        <Link to="/products" className="explore-btn">Shop Now</Link>
      </div>
    </main>
  )
}

export default App
