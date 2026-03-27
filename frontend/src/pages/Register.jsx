import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

export default function Register() {
  const [formData, setFormData] = useState({ username: '', email: '', password: '', role: ['user'] });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch('/api/auth/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });
      if (res.ok) navigate('/login');
      else {
        const data = await res.json();
        setError(data.message || 'Registration failed');
      }
    } catch (err) {
      setError('Connection error');
    }
  };

  return (
    <div className="auth-container">
      <form onSubmit={handleSubmit} className="auth-form glass">
        <h2>Join Premium Store</h2>
        {error && <p className="error">{error}</p>}
        <input type="text" placeholder="Username" onChange={e => setFormData({ ...formData, username: e.target.value })} required />
        <input type="email" placeholder="Email" onChange={e => setFormData({ ...formData, email: e.target.value })} required />
        <input type="password" placeholder="Password" onChange={e => setFormData({ ...formData, password: e.target.value })} required />
        <button type="submit" className="auth-btn">Create Account</button>
        <p>Already have an account? <Link to="/login">Login</Link></p>
      </form>
    </div>
  );
}
