/**
 * MicroStore API Client
 * Handles all REST API communication with the Spring Boot backend.
 */
const API = {
    BASE_URL: '/api',

    // ====== GENERIC REQUEST ======
    async request(endpoint, options = {}) {
        const url = `${this.BASE_URL}${endpoint}`;
        const config = {
            headers: { 'Content-Type': 'application/json', ...options.headers },
            ...options
        };
        if (config.body && typeof config.body === 'object') {
            config.body = JSON.stringify(config.body);
        }
        try {
            const response = await fetch(url, config);
            const data = await response.json();
            if (!response.ok) {
                throw new Error(data.message || 'Request failed');
            }
            return data;
        } catch (error) {
            console.error(`API Error [${endpoint}]:`, error);
            throw error;
        }
    },

    // ====== USERS ======
    users: {
        register(userData) {
            return API.request('/users/register', { method: 'POST', body: userData });
        },
        login(email, password) {
            return API.request('/users/login', { method: 'POST', body: { email, password } });
        },
        getById(id) {
            return API.request(`/users/${id}`);
        },
        getAll() {
            return API.request('/users');
        },
        update(id, userData) {
            return API.request(`/users/${id}`, { method: 'PUT', body: userData });
        },
        delete(id) {
            return API.request(`/users/${id}`, { method: 'DELETE' });
        },
        search(query) {
            return API.request(`/users/search?q=${encodeURIComponent(query)}`);
        }
    },

    // ====== PRODUCTS ======
    products: {
        getAll() {
            return API.request('/products');
        },
        getById(id) {
            return API.request(`/products/${id}`);
        },
        create(productData) {
            return API.request('/products', { method: 'POST', body: productData });
        },
        update(id, productData) {
            return API.request(`/products/${id}`, { method: 'PUT', body: productData });
        },
        delete(id) {
            return API.request(`/products/${id}`, { method: 'DELETE' });
        },
        search(query) {
            return API.request(`/products/search?q=${encodeURIComponent(query)}`);
        },
        getFeatured() {
            return API.request('/products/featured');
        },
        getByCategory(categoryId) {
            return API.request(`/products/category/${categoryId}`);
        },
        getByGender(gender) {
            return API.request(`/products/gender/${gender}`);
        },
        filter(params) {
            const qs = new URLSearchParams(params).toString();
            return API.request(`/products/filter?${qs}`);
        },
        getBrands() {
            return API.request('/products/brands');
        },
        getColors() {
            return API.request('/products/colors');
        },
        getDeals() {
            return API.request('/products/deals');
        }
    },

    // ====== CART ======
    cart: {
        getItems(userId) {
            return API.request(`/cart/${userId}`);
        },
        getSummary(userId) {
            return API.request(`/cart/${userId}/summary`);
        },
        addItem(userId, productId, quantity = 1, size = 'M') {
            return API.request(`/cart/${userId}`, {
                method: 'POST',
                body: { productId, quantity, size }
            });
        },
        updateQuantity(cartItemId, quantity) {
            return API.request(`/cart/item/${cartItemId}`, {
                method: 'PUT',
                body: { quantity }
            });
        },
        removeItem(cartItemId) {
            return API.request(`/cart/item/${cartItemId}`, { method: 'DELETE' });
        },
        clear(userId) {
            return API.request(`/cart/${userId}/clear`, { method: 'DELETE' });
        },
        getCount(userId) {
            return API.request(`/cart/${userId}/count`);
        }
    },

    // ====== ORDERS ======
    orders: {
        create(userId, shippingInfo) {
            return API.request(`/orders/${userId}`, { method: 'POST', body: shippingInfo });
        },
        getById(id) {
            return API.request(`/orders/${id}`);
        },
        getByUser(userId) {
            return API.request(`/orders/user/${userId}`);
        },
        getAll() {
            return API.request('/orders');
        },
        updateStatus(id, status) {
            return API.request(`/orders/${id}/status`, { method: 'PUT', body: { status } });
        },
        getStats() {
            return API.request('/orders/stats');
        }
    },

    // ====== PAYMENTS ======
    payments: {
        createOrder(orderId) {
            return API.request(`/payments/create/${orderId}`, { method: 'POST' });
        },
        verify(paymentData) {
            return API.request('/payments/verify', { method: 'POST', body: paymentData });
        }
    },

    // ====== ADMIN ======
    admin: {
        getDashboard() {
            return API.request('/admin/dashboard');
        },
        getCategories() {
            return API.request('/admin/categories');
        },
        createCategory(categoryData) {
            return API.request('/admin/categories', { method: 'POST', body: categoryData });
        },
        updateCategory(id, categoryData) {
            return API.request(`/admin/categories/${id}`, { method: 'PUT', body: categoryData });
        },
        deleteCategory(id) {
            return API.request(`/admin/categories/${id}`, { method: 'DELETE' });
        }
    }
};
