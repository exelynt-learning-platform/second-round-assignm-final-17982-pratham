import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch('/api/auth/signin', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });
      const data = await res.json();
      if (res.ok) {
        login(data, data.token); // Assumes backend returns { token, username, roles, etc }
        navigate('/');
      } else {
        setError(data.message || 'Login failed');
      }
    } catch (err) {
      setError('Connection error. Is backend running?');
    }
  };

  return (
    <div className="auth-container">
      <form onSubmit={handleSubmit} className="auth-form glass">
        <h2>Welcome Back</h2>
        {error && <p className="error">{error}</p>}
        <input type="text" placeholder="Username" value={username} onChange={e => setUsername(e.target.value)} required />
        <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} required />
        <button type="submit" className="auth-btn">Sign In</button>
        <p>Don't have an account? <Link to="/register">Register</Link></p>
      </form>
    </div>
  );
}
