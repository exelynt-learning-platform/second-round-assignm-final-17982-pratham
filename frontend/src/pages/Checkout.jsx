import React, { useState, useEffect } from 'react';
import { loadStripe } from '@stripe/stripe-js';
import { Elements, CardElement, useStripe, useElements } from '@stripe/react-stripe-js';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

// Replace with your Stripe Publishable Key
const stripePromise = loadStripe('pk_test_51TFYmaRhKmDVVRDTJiGnAdyarneFpQmtvSxfaJQuHbxU0UJN6PKtJD78CqPfKXVD2ayC4bt6xTD3E7HL4ThgYGAL00LOtWDjg3');

function CheckoutForm({ cart, clientSecret }) {
    const stripe = useStripe();
    const elements = useElements();
    const { token } = useAuth();
    const navigate = useNavigate();
    const [isProcessing, setIsProcessing] = useState(false);
    const [shipping, setShipping] = useState({ address: '', city: '', zip: '' });

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!stripe || !elements) return;

        setIsProcessing(true);

        try {
            const cardElement = elements.getElement(CardElement);
            const { error, paymentIntent } = await stripe.confirmCardPayment(clientSecret, {
                payment_method: {
                    card: cardElement,
                    billing_details: { name: 'Customer Name' }
                }
            });

            if (error) {
                alert(`Payment failed: ${error.message}`);
                setIsProcessing(false);
            } else if (paymentIntent.status === 'succeeded') {
                alert("Payment Successful! Your order has been placed.");
                navigate('/');
            }
        } catch (err) {
            alert(`Network Error: ${err.message}`);
            setIsProcessing(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="checkout-form glass">
            <h2>Secure Final Checkout</h2>
            <div className="order-summary">
                <p>Order Total: <strong>${cart?.totalAmount}</strong></p>
                <p>Items in Cart: {cart?.items?.length}</p>
            </div>

            <h3>Shipping Details</h3>
            <input type="text" placeholder="Street Address" onChange={p => setShipping({...shipping, address: p.target.value})} required />
            <div className="expiry-cvv">
                <input type="text" placeholder="City" onChange={p => setShipping({...shipping, city: p.target.value})} required />
                <input type="text" placeholder="Zip Code" onChange={p => setShipping({...shipping, zipCode: p.target.value})} required />
            </div>

            <h3>Credit Card Details</h3>
            <div className="card-input glass" style={{ padding: '1rem' }}>
                <CardElement options={{
                    style: {
                        base: { fontSize: '16px', color: '#fff', '::placeholder': { color: '#94a3b8' } },
                        invalid: { color: '#ff4b4b' }
                    }
                }} />
            </div>

            <button type="submit" className="pay-btn" disabled={isProcessing || !stripe}>
                {isProcessing ? "Processing Payment..." : `Pay $${cart?.totalAmount}`}
            </button>
        </form>
    );
}

export default function Checkout() {
    const [cart, setCart] = useState(null);
    const [clientSecret, setClientSecret] = useState('');
    const { token, authenticated } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (!authenticated) return navigate('/login');

        // Fetch Cart AND Create Payment Intent
        fetch('/api/orders/checkout', {
            method: 'POST',
            headers: { 
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json' 
            },
            body: JSON.stringify({ shippingAddress: 'Pending Input' })
        })
        .then(async res => {
            if (!res.ok) {
                const text = await res.text();
                throw new Error(text || "Backend Server Error (Check your Stripe Keys!)");
            }
            return res.json();
        })
        .then(data => {
            setCart(data);
            setClientSecret(data.clientSecret);
        })
        .catch(err => {
            alert(`Oops! Something went wrong: ${err.message}`);
            navigate('/cart');
        });
    }, [authenticated]);

    return (
        <div className="checkout-page">
            <Elements stripe={stripePromise}>
                {clientSecret && <CheckoutForm cart={cart} clientSecret={clientSecret} />}
            </Elements>
        </div>
    );
}
