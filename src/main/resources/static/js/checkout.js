/**
 * Checkout.js
 * Handles Razorpay payment initialization and form validation
 */

document.addEventListener('DOMContentLoaded', function () {
    const checkoutForm = document.getElementById('checkoutForm');
    const payBtn = document.getElementById('payBtn');
    const payBtnText = document.getElementById('payBtnText');
    const payBtnSpinner = document.getElementById('payBtnSpinner');

    // Form field references
    const nameInput = document.getElementById('name');
    const emailInput = document.getElementById('email');
    const phoneInput = document.getElementById('phone');
    const amountInput = document.getElementById('amount');

    // Error message containers
    const nameErr = document.getElementById('nameErr');
    const emailErr = document.getElementById('emailErr');
    const phoneErr = document.getElementById('phoneErr');
    const amountErr = document.getElementById('amountErr');

    /**
     * Validate form inputs
     */
    function validateForm() {
        let isValid = true;

        // Clear all error messages
        nameErr.textContent = '';
        emailErr.textContent = '';
        phoneErr.textContent = '';
        amountErr.textContent = '';

        // Validate name
        if (!nameInput.value.trim()) {
            nameErr.textContent = 'Name is required';
            isValid = false;
        } else if (nameInput.value.trim().length < 2) {
            nameErr.textContent = 'Name must be at least 2 characters';
            isValid = false;
        }

        // Validate email
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailInput.value.trim()) {
            emailErr.textContent = 'Email is required';
            isValid = false;
        } else if (!emailRegex.test(emailInput.value)) {
            emailErr.textContent = 'Please enter a valid email address';
            isValid = false;
        }

        // Validate phone
        const phoneRegex = /^[0-9]{10,15}$/;
        if (!phoneInput.value.trim()) {
            phoneErr.textContent = 'Phone number is required';
            isValid = false;
        } else if (!phoneRegex.test(phoneInput.value.replace(/\s/g, ''))) {
            phoneErr.textContent = 'Phone number must be 10-15 digits';
            isValid = false;
        }

        // Validate amount
        const amount = parseInt(amountInput.value);
        if (!amountInput.value) {
            amountErr.textContent = 'Amount is required';
            isValid = false;
        } else if (amount < 100) {
            amountErr.textContent = 'Minimum amount is ₹1 (100 paise)';
            isValid = false;
        } else if (amount > 999999999) {
            amountErr.textContent = 'Maximum amount exceeded';
            isValid = false;
        }

        return isValid;
    }

    /**
     * Create order by calling backend API
     */
    async function createOrder() {
        try {
            const response = await fetch('/payment/create-order', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name: nameInput.value.trim(),
                    email: emailInput.value.trim(),
                    phone: phoneInput.value.replace(/\s/g, ''),
                    amount: parseInt(amountInput.value),
                    currency: 'INR',
                    description: 'Pro Developer Plan - Annual Subscription'
                })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Failed to create order');
            }

            return data;

        } catch (error) {
            console.error('Error creating order:', error);
            showError('Failed to create order. Please try again.');
            throw error;
        }
    }

    /**
     * Initialize Razorpay checkout
     */
    function initializeRazorpayCheckout(orderData) {
        const options = {
            // Order reference
            key: orderData.razorpayKeyId,
            order_id: orderData.orderId,
            amount: orderData.amount,
            currency: orderData.currency,

            // User details
            name: 'RazorPay Demo',
            description: orderData.description || 'Payment',
            customer_id: 'customer_' + Date.now(),

            // Customer contact info
            prefill: {
                name: orderData.name,
                email: orderData.email,
                contact: orderData.phone
            },

            // Appearance settings
            theme: {
                color: '#3B82F6'
            },

            // Enable payment methods
            method: {
                emandate: false,
                netbanking: true,
                card: true,
                upi: true,
                wallet: false
            },

            // Callbacks
            handler: handlePaymentSuccess,
            modal: {
                // Auto-open the modal
                ondismiss: handlePaymentDismissed
            },

            // Additional options
            readonly: {
                email: true,
                contact: true
            },

            // Timeout handling (optional)
            timeout: 900 // 15 minutes
        };

        // Open Razorpay Checkout
        const razorpay = new Razorpay(options);
        razorpay.open();

        // Handle checkout close without payment
        razorpay.on('payment.failed', handlePaymentFailed);
    }

    /**
     * Handle successful payment
     */
    async function handlePaymentSuccess(response) {
        console.log('Payment successful:', response);

        // Update UI to show processing
        payBtnText.textContent = 'Verifying payment...';
        payBtnSpinner.classList.remove('hidden');
        payBtn.disabled = true;

        try {
            // Verify payment signature on backend
            const verifyResponse = await fetch('/payment/verify', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    razorpayPaymentId: response.razorpay_payment_id,
                    razorpayOrderId: response.razorpay_order_id,
                    razorpaySignature: response.razorpay_signature
                })
            });

            const verifyData = await verifyResponse.json();

            if (verifyResponse.ok && verifyData.status === 'success') {
                console.log('Payment verified successfully');
                
                // Redirect to success page
                setTimeout(() => {
                    window.location.href = `/payment/success?paymentId=${response.razorpay_payment_id}&orderId=${response.razorpay_order_id}`;
                }, 1000);
            } else {
                console.error('Verification failed:', verifyData);
                showError('Payment verification failed. Please contact support.');
                resetPaymentButton();
            }

        } catch (error) {
            console.error('Error verifying payment:', error);
            showError('An error occurred while verifying your payment. Please contact support.');
            resetPaymentButton();
        }
    }

    /**
     * Handle payment failure
     */
    function handlePaymentFailed(error) {
        console.error('Payment failed:', error);
        showError(`Payment failed: ${error.description || 'Unknown error'}`);
        resetPaymentButton();
    }

    /**
     * Handle checkout dismissed by user
     */
    function handlePaymentDismissed() {
        console.log('Checkout dismissed');
        showError('Payment was cancelled. Please try again.');
        resetPaymentButton();
    }

    /**
     * Show error message to user
     */
    function showError(message) {
        alert(message);
    }

    /**
     * Reset payment button state
     */
    function resetPaymentButton() {
        payBtnText.textContent = 'Pay Securely with Razorpay';
        payBtnSpinner.classList.add('hidden');
        payBtn.disabled = false;
    }

    /**
     * Form submission handler
     */
    checkoutForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        // Disable button and show spinner
        payBtn.disabled = true;
        payBtnText.textContent = 'Processing...';
        payBtnSpinner.classList.remove('hidden');

        try {
            // Create order on backend
            const orderData = await createOrder();

            // Reset button state before showing Razorpay
            resetPaymentButton();

            // Initialize Razorpay checkout
            initializeRazorpayCheckout(orderData);

        } catch (error) {
            resetPaymentButton();
            console.error('Checkout error:', error);
        }
    });

    /**
     * Real-time validation on field blur
     */
    nameInput.addEventListener('blur', () => {
        if (!nameInput.value.trim()) {
            nameErr.textContent = 'Name is required';
        } else if (nameInput.value.trim().length < 2) {
            nameErr.textContent = 'Name must be at least 2 characters';
        } else {
            nameErr.textContent = '';
        }
    });

    emailInput.addEventListener('blur', () => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailInput.value.trim()) {
            emailErr.textContent = 'Email is required';
        } else if (!emailRegex.test(emailInput.value)) {
            emailErr.textContent = 'Please enter a valid email address';
        } else {
            emailErr.textContent = '';
        }
    });

    phoneInput.addEventListener('blur', () => {
        const phoneRegex = /^[0-9]{10,15}$/;
        if (!phoneInput.value.trim()) {
            phoneErr.textContent = 'Phone number is required';
        } else if (!phoneRegex.test(phoneInput.value.replace(/\s/g, ''))) {
            phoneErr.textContent = 'Phone number must be 10-15 digits';
        } else {
            phoneErr.textContent = '';
        }
    });

    amountInput.addEventListener('blur', () => {
        const amount = parseInt(amountInput.value);
        if (!amountInput.value) {
            amountErr.textContent = 'Amount is required';
        } else if (amount < 100) {
            amountErr.textContent = 'Minimum amount is ₹1 (100 paise)';
        } else if (amount > 999999999) {
            amountErr.textContent = 'Maximum amount exceeded';
        } else {
            amountErr.textContent = '';
        }
    });

    // Auto-format phone number with spaces
    phoneInput.addEventListener('input', (e) => {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 0) {
            value = value.slice(0, 15);
            if (value.length <= 5) {
                value = value;
            } else if (value.length <= 10) {
                value = value.slice(0, 5) + ' ' + value.slice(5);
            } else {
                value = value.slice(0, 5) + ' ' + value.slice(5, 10) + ' ' + value.slice(10);
            }
        }
        e.target.value = value;
    });

    console.log('Checkout form initialized');
});
