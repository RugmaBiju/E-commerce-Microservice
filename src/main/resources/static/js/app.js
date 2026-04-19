/**
 * MicroStore - Main Application
 * Single Page Application with client-side routing
 */

// ====== STATE MANAGEMENT ======
const Store = {
    user: JSON.parse(localStorage.getItem('ms_user') || 'null'),
    cartCount: 0,
    categories: [],

    setUser(user) {
        this.user = user;
        if (user) {
            localStorage.setItem('ms_user', JSON.stringify(user));
        } else {
            localStorage.removeItem('ms_user');
        }
        App.updateAuthUI();
    },

    isLoggedIn() { return !!this.user; },
    isAdmin() { return this.user?.role === 'ADMIN'; },
    getUserId() { return this.user?.id; }
};

// ====== TOAST NOTIFICATIONS ======
const Toast = {
    show(message, type = 'success') {
        const container = document.getElementById('toast-container');
        const icons = { success: 'fa-check-circle', error: 'fa-times-circle', warning: 'fa-exclamation-triangle', info: 'fa-info-circle' };
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `<i class="fas ${icons[type]}"></i><span>${message}</span>`;
        container.appendChild(toast);
        setTimeout(() => { toast.style.opacity = '0'; toast.style.transform = 'translateX(100px)'; setTimeout(() => toast.remove(), 300); }, 3000);
    },
    success(msg) { this.show(msg, 'success'); },
    error(msg) { this.show(msg, 'error'); },
    info(msg) { this.show(msg, 'info'); }
};

// ====== ROUTER ======
const Router = {
    routes: {},
    register(path, handler) { this.routes[path] = handler; },
    navigate(path) {
        window.location.hash = path;
    },
    init() {
        window.addEventListener('hashchange', () => this.resolve());
        this.resolve();
    },
    resolve() {
        const hash = window.location.hash.slice(1) || '/';
        const parts = hash.split('/').filter(Boolean);
        let handler = this.routes['/' + (parts[0] || '')];
        if (!handler) handler = this.routes['/'];
        if (handler) {
            handler(parts.slice(1));
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }
    }
};

// ====== MAIN APP ======
const App = {
    async init() {
        this.content = document.getElementById('page-content');
        this.setupRoutes();
        this.setupHeader();
        this.updateAuthUI();
        this.updateCartBadge();
        Router.init();
    },

    setupRoutes() {
        Router.register('/', () => Pages.home());
        Router.register('/products', (p) => Pages.products(p[0]));
        Router.register('/product', (p) => Pages.productDetail(p[0]));
        Router.register('/cart', () => Pages.cart());
        Router.register('/checkout', () => Pages.checkout());
        Router.register('/login', () => Pages.login());
        Router.register('/register', () => Pages.register());
        Router.register('/orders', () => Pages.orders());
        Router.register('/admin', () => Pages.admin());
        Router.register('/search', (p) => Pages.searchResults(p[0]));
    },

    setupHeader() {
        // Scroll effect
        window.addEventListener('scroll', () => {
            document.querySelector('.header')?.classList.toggle('scrolled', window.scrollY > 10);
        });
        // Search
        const searchInput = document.getElementById('search-input');
        if (searchInput) {
            searchInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter' && e.target.value.trim()) {
                    Router.navigate('/search/' + encodeURIComponent(e.target.value.trim()));
                }
            });
        }
    },

    updateAuthUI() {
        const authBtn = document.getElementById('auth-btn');
        const adminNav = document.getElementById('admin-nav');
        const ordersBtn = document.getElementById('orders-btn');
        if (authBtn) {
            if (Store.isLoggedIn()) {
                authBtn.innerHTML = `<i class="fas fa-user"></i><span>${Store.user.firstName}</span>`;
                authBtn.onclick = () => {
                    if (confirm('Logout?')) { Store.setUser(null); Router.navigate('/'); }
                };
            } else {
                authBtn.innerHTML = `<i class="fas fa-user"></i><span>Login</span>`;
                authBtn.onclick = () => Router.navigate('/login');
            }
        }
        if (adminNav) adminNav.style.display = Store.isAdmin() ? 'flex' : 'none';
        if (ordersBtn) ordersBtn.style.display = Store.isLoggedIn() ? 'flex' : 'none';
    },

    async updateCartBadge() {
        const badge = document.getElementById('cart-badge');
        if (!badge) return;
        if (Store.isLoggedIn()) {
            try {
                const res = await API.cart.getCount(Store.getUserId());
                Store.cartCount = res.data || 0;
            } catch { Store.cartCount = 0; }
        } else { Store.cartCount = 0; }
        badge.textContent = Store.cartCount;
        badge.style.display = Store.cartCount > 0 ? 'flex' : 'none';
    },

    render(html) {
        this.content.innerHTML = html;
    }
};

// ====== PAGES ======
const Pages = {

    // ====== HOME PAGE ======
    async home() {
        App.render(`<div class="loading-spinner"><div class="spinner"></div></div>`);
        try {
            const [featuredRes, categoriesRes, dealsRes] = await Promise.all([
                API.products.getFeatured(),
                API.admin.getCategories(),
                API.products.getDeals()
            ]);
            const featured = featuredRes.data || [];
            const categories = categoriesRes.data || [];
            const deals = dealsRes.data || [];
            Store.categories = categories;

            App.render(`
                <!-- Promo Banner -->
                <div class="promo-banner">
                    🔥 MEGA FASHION SALE — Up to 70% OFF on Top Brands! Free shipping on orders above ₹999 🛍️
                </div>

                <!-- Hero Section -->
                <section class="hero">
                    <div class="hero-inner">
                        <div class="hero-content">
                            <div class="hero-badge"><i class="fas fa-sparkles"></i> New Season Collection 2026</div>
                            <h1 class="hero-title">Discover Your <span class="highlight">Perfect Style</span></h1>
                            <p class="hero-subtitle">Curated fashion from 500+ premium brands. From everyday essentials to runway trends — your wardrobe transformation starts here.</p>
                            <div class="hero-actions">
                                <button class="btn btn-primary btn-lg" onclick="Router.navigate('/products')">
                                    <i class="fas fa-shopping-bag"></i> Shop Now
                                </button>
                                <button class="btn btn-outline btn-lg" onclick="Router.navigate('/products/MEN')">
                                    Explore Men
                                </button>
                            </div>
                        </div>
                        <div class="hero-visual">
                            <div class="hero-image-grid">
                                <img class="hero-img" src="https://images.unsplash.com/photo-1483985988355-763728e1935b?w=400&h=500&fit=crop" alt="Fashion">
                                <img class="hero-img" src="https://images.unsplash.com/photo-1490578474895-699cd4e2cf59?w=400&h=500&fit=crop" alt="Fashion">
                                <img class="hero-img" src="https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=400&h=500&fit=crop" alt="Fashion">
                                <img class="hero-img" src="https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=400&h=500&fit=crop" alt="Fashion">
                            </div>
                            <div class="hero-floating-card card-1">
                                <div><div class="hero-stat-number">20K+</div><div class="hero-stat-label">Happy Customers</div></div>
                            </div>
                            <div class="hero-floating-card card-2">
                                <div class="hero-stat-number">500+</div>
                                <div class="hero-stat-label">Premium Brands</div>
                            </div>
                        </div>
                    </div>
                </section>

                <!-- Categories Section -->
                <section class="section">
                    <div class="container">
                        <div class="section-header">
                            <h2 class="section-title">Shop by Category</h2>
                            <button class="btn btn-ghost" onclick="Router.navigate('/products')">View All →</button>
                        </div>
                        <div class="categories-grid">
                            ${categories.map(cat => `
                                <div class="category-card" onclick="Router.navigate('/products/${cat.name}')">
                                    <img src="${cat.imageUrl || 'https://images.unsplash.com/photo-1483985988355-763728e1935b?w=400'}" alt="${cat.name}" loading="lazy">
                                    <div class="category-card-overlay">
                                        <div class="category-card-name">${cat.name}</div>
                                    </div>
                                </div>
                            `).join('')}
                        </div>
                    </div>
                </section>

                <!-- Featured Products -->
                <section class="section" style="background: var(--white);">
                    <div class="container">
                        <div class="section-header">
                            <h2 class="section-title">Trending Now</h2>
                            <button class="btn btn-ghost" onclick="Router.navigate('/products')">View All →</button>
                        </div>
                        <div class="products-grid">
                            ${featured.map(p => Components.productCard(p)).join('')}
                        </div>
                    </div>
                </section>

                <!-- Deals Section -->
                ${deals.length > 0 ? `
                <section class="section">
                    <div class="container">
                        <div class="section-header">
                            <h2 class="section-title">Best Deals 🔥</h2>
                        </div>
                        <div class="products-grid">
                            ${deals.slice(0, 8).map(p => Components.productCard(p)).join('')}
                        </div>
                    </div>
                </section>` : ''}

                ${Components.footer()}
            `);
        } catch (err) {
            App.render(`<div class="empty-state"><i class="fas fa-exclamation-triangle"></i><h3>Error loading page</h3><p>${err.message}</p>
                <button class="btn btn-primary" onclick="Pages.home()">Retry</button></div>`);
        }
    },

    // ====== PRODUCTS PAGE ======
    async products(filter) {
        App.render(`<div class="loading-spinner"><div class="spinner"></div></div>`);
        try {
            let products, title = 'All Products';
            const categoriesRes = await API.admin.getCategories();
            Store.categories = categoriesRes.data || [];

            if (filter && ['MEN', 'WOMEN', 'KIDS', 'UNISEX'].includes(filter.toUpperCase())) {
                const res = await API.products.getByGender(filter.toUpperCase());
                products = res.data || [];
                title = filter.charAt(0).toUpperCase() + filter.slice(1).toLowerCase() + "'s Collection";
            } else if (filter && !isNaN(filter)) {
                const res = await API.products.getByCategory(filter);
                products = res.data || [];
                const cat = Store.categories.find(c => c.id == filter);
                title = cat ? cat.name : 'Category';
            } else if (filter) {
                // Category name filter
                const cat = Store.categories.find(c => c.name.toLowerCase() === filter.toLowerCase());
                if (cat) {
                    const res = await API.products.getByCategory(cat.id);
                    products = res.data || [];
                    title = cat.name;
                } else {
                    const res = await API.products.getAll();
                    products = res.data || [];
                }
            } else {
                const res = await API.products.getAll();
                products = res.data || [];
            }

            App.render(`
                <div class="page-title-bar"><h1>${title} <span class="text-muted" style="font-weight:400;font-size:14px;">(${products.length} items)</span></h1></div>
                <div class="filter-bar">
                    <div class="filter-bar-inner">
                        <div class="filter-chip ${!filter ? 'active' : ''}" onclick="Router.navigate('/products')"><i class="fas fa-th-large"></i> All</div>
                        <div class="filter-chip ${filter === 'MEN' ? 'active' : ''}" onclick="Router.navigate('/products/MEN')">Men</div>
                        <div class="filter-chip ${filter === 'WOMEN' ? 'active' : ''}" onclick="Router.navigate('/products/WOMEN')">Women</div>
                        <div class="filter-chip ${filter === 'KIDS' ? 'active' : ''}" onclick="Router.navigate('/products/KIDS')">Kids</div>
                        ${Store.categories.map(c => `
                            <div class="filter-chip ${filter === c.name ? 'active' : ''}" onclick="Router.navigate('/products/${c.name}')">${c.name}</div>
                        `).join('')}
                    </div>
                </div>
                <section class="section">
                    <div class="container">
                        ${products.length > 0 ? `
                            <div class="products-grid">
                                ${products.map(p => Components.productCard(p)).join('')}
                            </div>
                        ` : `
                            <div class="empty-state">
                                <i class="fas fa-box-open"></i>
                                <h3>No products found</h3>
                                <p>Try a different filter or browse all products</p>
                                <button class="btn btn-primary" onclick="Router.navigate('/products')">Browse All</button>
                            </div>
                        `}
                    </div>
                </section>
            `);
        } catch (err) {
            App.render(`<div class="empty-state"><i class="fas fa-exclamation-triangle"></i><h3>Error</h3><p>${err.message}</p></div>`);
        }
    },

    // ====== PRODUCT DETAIL ======
    async productDetail(id) {
        App.render(`<div class="loading-spinner"><div class="spinner"></div></div>`);
        try {
            const res = await API.products.getById(id);
            const p = res.data;
            const sizes = p.sizes ? p.sizes.split(',') : [];
            const images = [p.imageUrl, p.imageUrl2, p.imageUrl3].filter(Boolean);
            const discount = p.discountPrice > 0 ? Math.round(((p.price - p.discountPrice) / p.price) * 100) : 0;

            App.render(`
                <div class="product-detail">
                    <div class="product-detail-grid">
                        <div class="product-gallery">
                            <div class="product-thumbnails">
                                ${images.map((img, i) => `
                                    <img class="product-thumbnail ${i === 0 ? 'active' : ''}" src="${img}" alt="View ${i+1}"
                                         onclick="document.getElementById('main-product-img').src='${img}'; document.querySelectorAll('.product-thumbnail').forEach(t=>t.classList.remove('active')); this.classList.add('active');">
                                `).join('')}
                            </div>
                            <div class="product-main-image">
                                <img id="main-product-img" src="${images[0] || ''}" alt="${p.name}">
                            </div>
                        </div>
                        <div class="product-info">
                            <div class="product-info-brand">${p.brand || ''}</div>
                            <h1 class="product-info-name">${p.name}</h1>
                            <div class="product-info-rating">
                                <i class="fas fa-star"></i> ${p.rating}
                                <span style="color:var(--text-light);margin-left:4px;">| ${p.reviewCount} Ratings</span>
                            </div>
                            <div class="product-info-pricing">
                                <span class="price-current">₹${p.discountPrice > 0 ? p.discountPrice : p.price}</span>
                                ${p.discountPrice > 0 ? `
                                    <span class="price-original">MRP ₹${p.price}</span>
                                    <span class="price-discount">(${discount}% OFF)</span>
                                ` : ''}
                                <span class="tax-info">inclusive of all taxes</span>
                            </div>

                            ${sizes.length > 0 ? `
                            <div class="size-selector">
                                <div class="size-selector-header">
                                    <span class="size-selector-title">SELECT SIZE</span>
                                </div>
                                <div class="size-options" id="size-options">
                                    ${sizes.map((s, i) => `
                                        <div class="size-option ${i === 0 ? 'selected' : ''}" onclick="selectSize(this, '${s.trim()}')">${s.trim()}</div>
                                    `).join('')}
                                </div>
                            </div>` : ''}

                            <div class="product-actions">
                                <button class="btn btn-primary btn-lg" onclick="addToCart(${p.id})">
                                    <i class="fas fa-shopping-bag"></i> ADD TO BAG
                                </button>
                                <button class="btn btn-outline btn-lg" onclick="buyNow(${p.id})">
                                    <i class="fas fa-bolt"></i> BUY NOW
                                </button>
                            </div>

                            <div class="product-description">
                                <h3>Product Details</h3>
                                <p>${p.description || 'No description available.'}</p>
                            </div>

                            <div class="product-meta">
                                ${p.color ? `<div class="product-meta-item"><span class="label">Color:</span><span class="value">${p.color}</span></div>` : ''}
                                ${p.material ? `<div class="product-meta-item"><span class="label">Material:</span><span class="value">${p.material}</span></div>` : ''}
                                ${p.gender ? `<div class="product-meta-item"><span class="label">Gender:</span><span class="value">${p.gender}</span></div>` : ''}
                                <div class="product-meta-item"><span class="label">Stock:</span><span class="value">${p.stockQuantity > 0 ? `${p.stockQuantity} available` : '<span style="color:var(--error)">Out of Stock</span>'}</span></div>
                            </div>
                        </div>
                    </div>
                </div>
            `);
        } catch (err) {
            App.render(`<div class="empty-state"><i class="fas fa-exclamation-triangle"></i><h3>Product not found</h3><p>${err.message}</p>
                <button class="btn btn-primary" onclick="Router.navigate('/products')">Browse Products</button></div>`);
        }
    },

    // ====== CART PAGE ======
    async cart() {
        if (!Store.isLoggedIn()) { Router.navigate('/login'); return; }
        App.render(`<div class="loading-spinner"><div class="spinner"></div></div>`);
        try {
            const res = await API.cart.getSummary(Store.getUserId());
            const cart = res.data;
            const items = cart.items || [];

            App.render(`
                <div class="page-title-bar"><h1>Shopping Bag</h1></div>
                <div class="cart-page">
                    ${items.length > 0 ? `
                    <div class="cart-layout">
                        <div class="cart-items-list">
                            ${items.map(item => `
                                <div class="cart-item" id="cart-item-${item.id}">
                                    <div class="cart-item-image" onclick="Router.navigate('/product/${item.productId}')">
                                        <img src="${item.image || ''}" alt="${item.name}">
                                    </div>
                                    <div class="cart-item-details">
                                        <div class="cart-item-brand">${item.brand || ''}</div>
                                        <div class="cart-item-name">${item.name}</div>
                                        <div class="cart-item-meta">
                                            <span>Size: ${item.size || 'M'}</span>
                                        </div>
                                        <div class="cart-item-pricing">
                                            <span class="product-price-current">₹${item.discountPrice}</span>
                                            ${item.discountPrice < item.price ? `<span class="product-price-original">₹${item.price}</span>` : ''}
                                        </div>
                                        <div class="cart-item-actions">
                                            <div class="quantity-control">
                                                <button class="quantity-btn" onclick="updateCartQty(${item.id}, ${item.quantity - 1})">−</button>
                                                <span class="quantity-value">${item.quantity}</span>
                                                <button class="quantity-btn" onclick="updateCartQty(${item.id}, ${item.quantity + 1})">+</button>
                                            </div>
                                            <button class="remove-btn" onclick="removeCartItem(${item.id})">
                                                <i class="fas fa-trash-alt"></i> Remove
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            `).join('')}
                        </div>
                        <div class="cart-summary">
                            <div class="cart-summary-title">Price Details (${cart.itemCount} Item${cart.itemCount > 1 ? 's' : ''})</div>
                            <div class="cart-summary-row">
                                <span class="label">Total MRP</span>
                                <span class="value">₹${Math.round(cart.subtotal + cart.discount)}</span>
                            </div>
                            <div class="cart-summary-row discount">
                                <span class="label">Discount on MRP</span>
                                <span class="value">-₹${Math.round(cart.discount)}</span>
                            </div>
                            <div class="cart-summary-row ${cart.deliveryFee === 0 ? 'free' : ''}">
                                <span class="label">Delivery Fee</span>
                                <span class="value">${cart.deliveryFee === 0 ? 'FREE' : '₹' + cart.deliveryFee}</span>
                            </div>
                            <div class="cart-summary-total">
                                <span>Total Amount</span>
                                <span>₹${Math.round(cart.total)}</span>
                            </div>
                            <button class="btn btn-primary btn-full btn-lg" onclick="Router.navigate('/checkout')">
                                PLACE ORDER
                            </button>
                        </div>
                    </div>
                    ` : `
                    <div class="container">
                        <div class="empty-state">
                            <i class="fas fa-shopping-bag"></i>
                            <h3>Your bag is empty</h3>
                            <p>Looks like you haven't added anything to your bag. Let's go shopping!</p>
                            <button class="btn btn-primary" onclick="Router.navigate('/products')">Continue Shopping</button>
                        </div>
                    </div>
                    `}
                </div>
            `);
        } catch (err) {
            App.render(`<div class="empty-state"><i class="fas fa-exclamation-triangle"></i><h3>Error loading cart</h3><p>${err.message}</p></div>`);
        }
    },

    // ====== CHECKOUT PAGE ======
    async checkout() {
        if (!Store.isLoggedIn()) { Router.navigate('/login'); return; }
        App.render(`<div class="loading-spinner"><div class="spinner"></div></div>`);
        try {
            const res = await API.cart.getSummary(Store.getUserId());
            const cart = res.data;
            if (!cart.items || cart.items.length === 0) { Router.navigate('/cart'); return; }

            App.render(`
                <div class="page-title-bar"><h1>Checkout</h1></div>
                <div class="checkout-page">
                    <div class="checkout-form">
                        <div class="checkout-section">
                            <div class="checkout-section-title"><i class="fas fa-map-marker-alt"></i> Delivery Address</div>
                            <div class="form-grid">
                                <div class="form-group"><label class="form-label">Full Name *</label>
                                    <input type="text" id="co-name" value="${Store.user.firstName + ' ' + Store.user.lastName}" required></div>
                                <div class="form-group"><label class="form-label">Phone *</label>
                                    <input type="tel" id="co-phone" value="${Store.user.phone || ''}" required></div>
                                <div class="form-group full-width"><label class="form-label">Street Address *</label>
                                    <input type="text" id="co-street" placeholder="House no., Building, Street" required></div>
                                <div class="form-group"><label class="form-label">City *</label>
                                    <input type="text" id="co-city" placeholder="City" required></div>
                                <div class="form-group"><label class="form-label">State *</label>
                                    <input type="text" id="co-state" placeholder="State" required></div>
                                <div class="form-group"><label class="form-label">PIN Code *</label>
                                    <input type="text" id="co-zip" placeholder="PIN Code" required></div>
                            </div>
                        </div>

                        <div class="checkout-section">
                            <div class="checkout-section-title"><i class="fas fa-receipt"></i> Order Summary</div>
                            ${cart.items.map(item => `
                                <div class="order-item-row">
                                    <img class="order-item-thumb" src="${item.image}" alt="${item.name}">
                                    <div style="flex:1">
                                        <div style="font-weight:600;font-size:14px;">${item.brand} — ${item.name}</div>
                                        <div style="color:var(--text-light);font-size:13px;">Size: ${item.size} | Qty: ${item.quantity}</div>
                                    </div>
                                    <div style="font-weight:700;">₹${Math.round(item.subtotal)}</div>
                                </div>
                            `).join('')}
                            <div class="cart-summary-total" style="margin-top:var(--space-md);">
                                <span>Total</span><span>₹${Math.round(cart.total)}</span>
                            </div>
                        </div>

                        <button class="btn btn-primary btn-full btn-lg" onclick="placeOrder()">
                            <i class="fas fa-lock"></i> PAY ₹${Math.round(cart.total)}
                        </button>
                    </div>
                </div>
            `);
        } catch (err) {
            App.render(`<div class="empty-state"><i class="fas fa-exclamation-triangle"></i><h3>Error</h3><p>${err.message}</p></div>`);
        }
    },

    // ====== LOGIN PAGE ======
    login() {
        App.render(`
            <div class="auth-page">
                <div class="auth-card">
                    <div class="auth-card-header">
                        <h2>Welcome Back</h2>
                        <p>Login to access your account</p>
                    </div>
                    <div class="auth-form">
                        <div class="form-group">
                            <label class="form-label">Email</label>
                            <input type="email" id="login-email" placeholder="Enter your email">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Password</label>
                            <input type="password" id="login-password" placeholder="Enter your password">
                        </div>
                        <button class="btn btn-primary btn-full btn-lg" onclick="handleLogin()">LOGIN</button>
                    </div>
                    <div class="auth-divider">OR</div>
                    <div class="auth-switch">
                        New to MicroStore? <a onclick="Router.navigate('/register')">Create an account</a>
                    </div>
                    <div style="margin-top:var(--space-md);padding:var(--space-md);background:var(--bg);border-radius:var(--radius-md);font-size:13px;">
                        <strong>Demo Accounts:</strong><br>
                        👤 Customer: rahul@email.com / password123<br>
                        🔧 Admin: admin@microstore.com / admin123
                    </div>
                </div>
            </div>
        `);
    },

    // ====== REGISTER PAGE ======
    register() {
        App.render(`
            <div class="auth-page">
                <div class="auth-card">
                    <div class="auth-card-header">
                        <h2>Create Account</h2>
                        <p>Join MicroStore today</p>
                    </div>
                    <div class="auth-form">
                        <div class="form-grid">
                            <div class="form-group"><label class="form-label">First Name</label>
                                <input type="text" id="reg-first" placeholder="First name"></div>
                            <div class="form-group"><label class="form-label">Last Name</label>
                                <input type="text" id="reg-last" placeholder="Last name"></div>
                        </div>
                        <div class="form-group"><label class="form-label">Email</label>
                            <input type="email" id="reg-email" placeholder="you@email.com"></div>
                        <div class="form-group"><label class="form-label">Phone</label>
                            <input type="tel" id="reg-phone" placeholder="Phone number"></div>
                        <div class="form-group"><label class="form-label">Password</label>
                            <input type="password" id="reg-password" placeholder="Create password"></div>
                        <button class="btn btn-primary btn-full btn-lg" onclick="handleRegister()">CREATE ACCOUNT</button>
                    </div>
                    <div class="auth-divider">OR</div>
                    <div class="auth-switch">
                        Already have an account? <a onclick="Router.navigate('/login')">Login</a>
                    </div>
                </div>
            </div>
        `);
    },

    // ====== ORDERS PAGE ======
    async orders() {
        if (!Store.isLoggedIn()) { Router.navigate('/login'); return; }
        App.render(`<div class="loading-spinner"><div class="spinner"></div></div>`);
        try {
            const res = await API.orders.getByUser(Store.getUserId());
            const orders = res.data || [];

            App.render(`
                <div class="page-title-bar"><h1>My Orders</h1></div>
                <section class="section">
                    ${orders.length > 0 ? `
                    <div class="orders-list">
                        ${orders.map(o => `
                            <div class="order-card">
                                <div class="order-card-header">
                                    <div>
                                        <div style="font-weight:700;font-size:14px;">Order #${o.id}</div>
                                        <div style="font-size:12px;color:var(--text-light);">${new Date(o.createdAt).toLocaleDateString('en-IN', {day:'numeric',month:'short',year:'numeric'})}</div>
                                    </div>
                                    <div style="display:flex;gap:var(--space-sm);align-items:center;">
                                        <span class="status-badge ${o.status.toLowerCase()}">${o.status}</span>
                                        <span class="status-badge ${o.paymentStatus === 'PAID' ? 'paid' : 'pending'}">${o.paymentStatus}</span>
                                    </div>
                                </div>
                                <div class="order-card-body">
                                    ${(o.items || []).map(item => `
                                        <div class="order-item-row">
                                            <img class="order-item-thumb" src="${item.productImage || 'https://via.placeholder.com/56'}" alt="${item.productName}">
                                            <div style="flex:1">
                                                <div style="font-weight:600;font-size:14px;">${item.productBrand || ''} ${item.productName}</div>
                                                <div style="font-size:13px;color:var(--text-light);">Size: ${item.selectedSize || '-'} | Qty: ${item.quantity}</div>
                                            </div>
                                            <div style="font-weight:600;">₹${Math.round(item.price * item.quantity)}</div>
                                        </div>
                                    `).join('')}
                                    <div style="display:flex;justify-content:space-between;margin-top:var(--space-md);padding-top:var(--space-md);border-top:1px solid var(--border);font-weight:700;">
                                        <span>Total</span><span>₹${Math.round(o.totalAmount)}</span>
                                    </div>
                                </div>
                            </div>
                        `).join('')}
                    </div>` : `
                    <div class="container">
                        <div class="empty-state">
                            <i class="fas fa-box-open"></i>
                            <h3>No orders yet</h3>
                            <p>Your order history will appear here</p>
                            <button class="btn btn-primary" onclick="Router.navigate('/products')">Start Shopping</button>
                        </div>
                    </div>`}
                </section>
            `);
        } catch (err) {
            App.render(`<div class="empty-state"><i class="fas fa-exclamation-triangle"></i><h3>Error</h3><p>${err.message}</p></div>`);
        }
    },

    // ====== SEARCH RESULTS ======
    async searchResults(query) {
        if (!query) { Router.navigate('/products'); return; }
        query = decodeURIComponent(query);
        App.render(`<div class="loading-spinner"><div class="spinner"></div></div>`);
        try {
            const res = await API.products.search(query);
            const products = res.data || [];
            App.render(`
                <div class="page-title-bar"><h1>Search: "${query}" <span class="text-muted" style="font-weight:400;font-size:14px;">(${products.length} results)</span></h1></div>
                <section class="section">
                    <div class="container">
                        ${products.length > 0 ? `
                            <div class="products-grid">${products.map(p => Components.productCard(p)).join('')}</div>
                        ` : `
                            <div class="empty-state"><i class="fas fa-search"></i><h3>No results found</h3>
                            <p>Try different keywords</p>
                            <button class="btn btn-primary" onclick="Router.navigate('/products')">Browse All</button></div>
                        `}
                    </div>
                </section>
            `);
        } catch (err) {
            App.render(`<div class="empty-state"><i class="fas fa-exclamation-triangle"></i><h3>Error</h3><p>${err.message}</p></div>`);
        }
    },

    // ====== ADMIN DASHBOARD ======
    async admin() {
        if (!Store.isAdmin()) { Router.navigate('/login'); Toast.error('Admin access required'); return; }
        App.render(`<div class="loading-spinner"><div class="spinner"></div></div>`);
        try {
            const [dashRes, productsRes, ordersRes, usersRes] = await Promise.all([
                API.admin.getDashboard(), API.products.getAll(), API.orders.getAll(), API.users.getAll()
            ]);
            const dash = dashRes.data;
            const products = productsRes.data || [];
            const orders = ordersRes.data || [];
            const users = usersRes.data || [];

            App.render(`
                <div class="admin-page">
                    <div class="admin-layout">
                        <div class="admin-header">
                            <h1 style="font-family:var(--font-display);font-size:28px;">Admin Dashboard</h1>
                            <button class="btn btn-primary" onclick="showAddProductModal()"><i class="fas fa-plus"></i> Add Product</button>
                        </div>

                        <div class="admin-stats-grid">
                            <div class="stat-card">
                                <div class="stat-card-icon pink"><i class="fas fa-box"></i></div>
                                <div class="stat-card-value">${dash.products?.totalProducts || 0}</div>
                                <div class="stat-card-label">Total Products</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-card-icon blue"><i class="fas fa-shopping-cart"></i></div>
                                <div class="stat-card-value">${dash.orders?.totalOrders || 0}</div>
                                <div class="stat-card-label">Total Orders</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-card-icon green"><i class="fas fa-rupee-sign"></i></div>
                                <div class="stat-card-value">₹${Math.round(dash.orders?.totalRevenue || 0).toLocaleString()}</div>
                                <div class="stat-card-label">Total Revenue</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-card-icon orange"><i class="fas fa-users"></i></div>
                                <div class="stat-card-value">${dash.users?.totalUsers || 0}</div>
                                <div class="stat-card-label">Total Users</div>
                            </div>
                        </div>

                        <div class="admin-tabs">
                            <div class="admin-tab active" onclick="switchAdminTab(this, 'products-tab')">Products</div>
                            <div class="admin-tab" onclick="switchAdminTab(this, 'orders-tab')">Orders</div>
                            <div class="admin-tab" onclick="switchAdminTab(this, 'users-tab')">Users</div>
                        </div>

                        <!-- Products Tab -->
                        <div id="products-tab" class="admin-tab-content">
                            <div class="admin-table-container">
                                <table class="admin-table">
                                    <thead><tr><th>Image</th><th>Product</th><th>Brand</th><th>Price</th><th>Stock</th><th>Actions</th></tr></thead>
                                    <tbody>
                                        ${products.map(p => `<tr>
                                            <td><img class="product-thumb" src="${p.imageUrl || ''}" alt="${p.name}"></td>
                                            <td><strong>${p.name}</strong><br><span style="font-size:12px;color:var(--text-light)">${p.gender || ''} | ${p.category?.name || ''}</span></td>
                                            <td>${p.brand || ''}</td>
                                            <td>₹${p.discountPrice || p.price}</td>
                                            <td>${p.stockQuantity}</td>
                                            <td><div class="action-btns">
                                                <button class="action-btn edit" onclick="showEditProductModal(${p.id})"><i class="fas fa-edit"></i></button>
                                                <button class="action-btn delete" onclick="deleteProduct(${p.id})"><i class="fas fa-trash"></i></button>
                                            </div></td>
                                        </tr>`).join('')}
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <!-- Orders Tab -->
                        <div id="orders-tab" class="admin-tab-content hidden">
                            <div class="admin-table-container">
                                <table class="admin-table">
                                    <thead><tr><th>ID</th><th>Customer</th><th>Amount</th><th>Status</th><th>Payment</th><th>Date</th><th>Actions</th></tr></thead>
                                    <tbody>
                                        ${orders.map(o => `<tr>
                                            <td>#${o.id}</td>
                                            <td>${o.shippingName || 'User #' + o.userId}</td>
                                            <td>₹${Math.round(o.totalAmount)}</td>
                                            <td><span class="status-badge ${o.status.toLowerCase()}">${o.status}</span></td>
                                            <td><span class="status-badge ${o.paymentStatus === 'PAID' ? 'paid' : 'pending'}">${o.paymentStatus}</span></td>
                                            <td>${new Date(o.createdAt).toLocaleDateString()}</td>
                                            <td>
                                                <select onchange="updateOrderStatus(${o.id}, this.value)" style="padding:4px 8px;font-size:12px;border-radius:4px;">
                                                    ${['PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED'].map(s => `<option value="${s}" ${o.status === s ? 'selected' : ''}>${s}</option>`).join('')}
                                                </select>
                                            </td>
                                        </tr>`).join('')}
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <!-- Users Tab -->
                        <div id="users-tab" class="admin-tab-content hidden">
                            <div class="admin-table-container">
                                <table class="admin-table">
                                    <thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Role</th><th>Actions</th></tr></thead>
                                    <tbody>
                                        ${users.map(u => `<tr>
                                            <td>#${u.id}</td>
                                            <td>${u.firstName} ${u.lastName}</td>
                                            <td>${u.email}</td>
                                            <td>${u.phone || '-'}</td>
                                            <td><span class="status-badge ${u.role === 'ADMIN' ? 'confirmed' : 'pending'}">${u.role}</span></td>
                                            <td><div class="action-btns">
                                                <button class="action-btn delete" onclick="deleteUser(${u.id})"><i class="fas fa-trash"></i></button>
                                            </div></td>
                                        </tr>`).join('')}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            `);
        } catch (err) {
            App.render(`<div class="empty-state"><i class="fas fa-exclamation-triangle"></i><h3>Error</h3><p>${err.message}</p></div>`);
        }
    }
};

// ====== REUSABLE COMPONENTS ======
const Components = {
    productCard(p) {
        const discount = p.discountPrice > 0 ? Math.round(((p.price - p.discountPrice) / p.price) * 100) : 0;
        return `
            <div class="product-card" onclick="Router.navigate('/product/${p.id}')">
                <div class="product-card-image">
                    <img src="${p.imageUrl || 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400'}" alt="${p.name}" loading="lazy">
                    ${discount > 0 ? `<div class="product-card-badge">${discount}% OFF</div>` : ''}
                    <div class="product-card-wishlist" onclick="event.stopPropagation()"><i class="fas fa-heart"></i></div>
                </div>
                <div class="product-card-info">
                    <div class="product-card-brand">${p.brand || ''}</div>
                    <div class="product-card-name">${p.name}</div>
                    <div class="product-card-pricing">
                        <span class="product-price-current">₹${p.discountPrice > 0 ? p.discountPrice : p.price}</span>
                        ${p.discountPrice > 0 ? `<span class="product-price-original">₹${p.price}</span>` : ''}
                        ${discount > 0 ? `<span class="product-price-discount">(${discount}% off)</span>` : ''}
                    </div>
                    ${p.rating > 0 ? `<div class="product-card-rating"><i class="fas fa-star"></i> ${p.rating} | ${p.reviewCount}</div>` : ''}
                </div>
            </div>
        `;
    },

    footer() {
        return `
            <footer class="footer">
                <div class="footer-grid">
                    <div class="footer-brand">
                        <div class="logo-text">Micro<span>Store</span></div>
                        <p>Your one-stop destination for curated fashion from premium brands. Discover the latest trends and timeless styles.</p>
                        <div class="footer-social">
                            <a href="#"><i class="fab fa-facebook-f"></i></a>
                            <a href="#"><i class="fab fa-instagram"></i></a>
                            <a href="#"><i class="fab fa-twitter"></i></a>
                            <a href="#"><i class="fab fa-youtube"></i></a>
                        </div>
                    </div>
                    <div class="footer-column">
                        <h4>Shop</h4>
                        <a href="#/products/MEN">Men</a>
                        <a href="#/products/WOMEN">Women</a>
                        <a href="#/products/KIDS">Kids</a>
                        <a href="#/products/Footwear">Footwear</a>
                        <a href="#/products/Accessories">Accessories</a>
                    </div>
                    <div class="footer-column">
                        <h4>Help</h4>
                        <a href="#">Customer Service</a>
                        <a href="#">Track Order</a>
                        <a href="#">Returns & Exchanges</a>
                        <a href="#">Shipping Info</a>
                        <a href="#">FAQs</a>
                    </div>
                    <div class="footer-column">
                        <h4>Company</h4>
                        <a href="#">About Us</a>
                        <a href="#">Careers</a>
                        <a href="#">Terms of Use</a>
                        <a href="#">Privacy Policy</a>
                        <a href="#">Contact Us</a>
                    </div>
                </div>
                <div class="footer-bottom">
                    <p>© 2026 MicroStore. All rights reserved. Built with ❤️ using Java Spring Boot.</p>
                </div>
            </footer>
        `;
    }
};

// ====== GLOBAL ACTION HANDLERS ======

let selectedSize = null;

function selectSize(el, size) {
    document.querySelectorAll('.size-option').forEach(s => s.classList.remove('selected'));
    el.classList.add('selected');
    selectedSize = size;
}

async function addToCart(productId) {
    if (!Store.isLoggedIn()) { Router.navigate('/login'); return; }
    const size = selectedSize || document.querySelector('.size-option.selected')?.textContent || 'M';
    try {
        await API.cart.addItem(Store.getUserId(), productId, 1, size);
        Toast.success('Added to bag!');
        App.updateCartBadge();
    } catch (err) { Toast.error(err.message); }
}

async function buyNow(productId) {
    if (!Store.isLoggedIn()) { Router.navigate('/login'); return; }
    try {
        const size = selectedSize || document.querySelector('.size-option.selected')?.textContent || 'M';
        await API.cart.addItem(Store.getUserId(), productId, 1, size);
        App.updateCartBadge();
        Router.navigate('/checkout');
    } catch (err) { Toast.error(err.message); }
}

async function updateCartQty(cartItemId, newQty) {
    try {
        if (newQty <= 0) {
            await API.cart.removeItem(cartItemId);
        } else {
            await API.cart.updateQuantity(cartItemId, newQty);
        }
        App.updateCartBadge();
        Pages.cart();
    } catch (err) { Toast.error(err.message); }
}

async function removeCartItem(cartItemId) {
    try {
        await API.cart.removeItem(cartItemId);
        Toast.success('Item removed');
        App.updateCartBadge();
        Pages.cart();
    } catch (err) { Toast.error(err.message); }
}

async function handleLogin() {
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    if (!email || !password) { Toast.error('Please fill all fields'); return; }
    try {
        const res = await API.users.login(email, password);
        Store.setUser(res.data);
        Toast.success('Welcome back, ' + res.data.firstName + '!');
        App.updateCartBadge();
        Router.navigate('/');
    } catch (err) { Toast.error(err.message || 'Invalid credentials'); }
}

async function handleRegister() {
    const data = {
        firstName: document.getElementById('reg-first').value,
        lastName: document.getElementById('reg-last').value,
        email: document.getElementById('reg-email').value,
        phone: document.getElementById('reg-phone').value,
        password: document.getElementById('reg-password').value
    };
    if (!data.firstName || !data.lastName || !data.email || !data.password) { Toast.error('Please fill required fields'); return; }
    try {
        const res = await API.users.register(data);
        Store.setUser(res.data);
        Toast.success('Account created successfully!');
        Router.navigate('/');
    } catch (err) { Toast.error(err.message); }
}

async function placeOrder() {
    const shipping = {
        name: document.getElementById('co-name')?.value,
        phone: document.getElementById('co-phone')?.value,
        street: document.getElementById('co-street')?.value,
        city: document.getElementById('co-city')?.value,
        state: document.getElementById('co-state')?.value,
        zip: document.getElementById('co-zip')?.value
    };
    if (!shipping.name || !shipping.street || !shipping.city || !shipping.state || !shipping.zip) {
        Toast.error('Please fill all address fields'); return;
    }
    try {
        const orderRes = await API.orders.create(Store.getUserId(), shipping);
        const order = orderRes.data;

        // Try Razorpay payment
        try {
            const payRes = await API.payments.createOrder(order.id);
            if (payRes.data?.success) {
                openRazorpay(payRes.data, order.id);
                return;
            }
        } catch (e) { console.log('Razorpay not configured, marking as COD'); }

        // Fallback: mark as Cash on Delivery
        Toast.success('Order placed successfully! (Cash on Delivery)');
        App.updateCartBadge();
        Router.navigate('/orders');
    } catch (err) { Toast.error(err.message); }
}

function openRazorpay(paymentData, orderId) {
    const options = {
        key: paymentData.keyId,
        amount: paymentData.amount,
        currency: paymentData.currency,
        name: 'MicroStore',
        description: 'Fashion Purchase',
        order_id: paymentData.razorpayOrderId,
        handler: async function(response) {
            try {
                await API.payments.verify({
                    razorpay_order_id: response.razorpay_order_id,
                    razorpay_payment_id: response.razorpay_payment_id,
                    razorpay_signature: response.razorpay_signature,
                    order_id: orderId.toString()
                });
                Toast.success('Payment successful! 🎉');
                App.updateCartBadge();
                Router.navigate('/orders');
            } catch (e) { Toast.error('Payment verification failed'); }
        },
        prefill: {
            name: Store.user.firstName + ' ' + Store.user.lastName,
            email: Store.user.email,
            contact: Store.user.phone
        },
        theme: { color: '#ff3f6c' }
    };
    const rzp = new Razorpay(options);
    rzp.open();
}

// Admin functions
function switchAdminTab(el, tabId) {
    document.querySelectorAll('.admin-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.admin-tab-content').forEach(c => c.classList.add('hidden'));
    el.classList.add('active');
    document.getElementById(tabId)?.classList.remove('hidden');
}

async function deleteProduct(id) {
    if (!confirm('Delete this product?')) return;
    try { await API.products.delete(id); Toast.success('Product deleted'); Pages.admin(); }
    catch (err) { Toast.error(err.message); }
}

async function deleteUser(id) {
    if (!confirm('Delete this user?')) return;
    try { await API.users.delete(id); Toast.success('User deleted'); Pages.admin(); }
    catch (err) { Toast.error(err.message); }
}

async function updateOrderStatus(orderId, status) {
    try { await API.orders.updateStatus(orderId, status); Toast.success('Status updated'); }
    catch (err) { Toast.error(err.message); }
}

function showAddProductModal() {
    const modal = document.getElementById('modal-container');
    modal.innerHTML = `
        <div class="modal-overlay" onclick="if(event.target===this)closeModal()">
            <div class="modal">
                <div class="modal-header">
                    <h3 class="modal-title">Add New Product</h3>
                    <button class="modal-close" onclick="closeModal()"><i class="fas fa-times"></i></button>
                </div>
                <div class="auth-form">
                    <div class="form-group"><label class="form-label">Product Name *</label><input type="text" id="ap-name"></div>
                    <div class="form-group"><label class="form-label">Brand *</label><input type="text" id="ap-brand"></div>
                    <div class="form-grid">
                        <div class="form-group"><label class="form-label">Price *</label><input type="number" id="ap-price"></div>
                        <div class="form-group"><label class="form-label">Discount Price</label><input type="number" id="ap-dprice"></div>
                    </div>
                    <div class="form-group"><label class="form-label">Image URL</label><input type="url" id="ap-image"></div>
                    <div class="form-group"><label class="form-label">Description</label><textarea id="ap-desc" rows="3" style="resize:vertical"></textarea></div>
                    <div class="form-grid">
                        <div class="form-group"><label class="form-label">Sizes</label><input type="text" id="ap-sizes" placeholder="S,M,L,XL"></div>
                        <div class="form-group"><label class="form-label">Stock</label><input type="number" id="ap-stock" value="50"></div>
                    </div>
                    <div class="form-grid">
                        <div class="form-group"><label class="form-label">Color</label><input type="text" id="ap-color"></div>
                        <div class="form-group"><label class="form-label">Gender</label>
                            <select id="ap-gender"><option value="MEN">Men</option><option value="WOMEN">Women</option><option value="KIDS">Kids</option><option value="UNISEX">Unisex</option></select>
                        </div>
                    </div>
                    <button class="btn btn-primary btn-full" onclick="submitAddProduct()">Add Product</button>
                </div>
            </div>
        </div>
    `;
}

async function submitAddProduct() {
    const product = {
        name: document.getElementById('ap-name').value,
        brand: document.getElementById('ap-brand').value,
        price: parseFloat(document.getElementById('ap-price').value),
        discountPrice: parseFloat(document.getElementById('ap-dprice').value) || 0,
        imageUrl: document.getElementById('ap-image').value,
        description: document.getElementById('ap-desc').value,
        sizes: document.getElementById('ap-sizes').value,
        stockQuantity: parseInt(document.getElementById('ap-stock').value) || 50,
        color: document.getElementById('ap-color').value,
        gender: document.getElementById('ap-gender').value,
        featured: false
    };
    if (!product.name || !product.brand || !product.price) { Toast.error('Fill required fields'); return; }
    try { await API.products.create(product); Toast.success('Product added!'); closeModal(); Pages.admin(); }
    catch (err) { Toast.error(err.message); }
}

async function showEditProductModal(id) {
    try {
        const res = await API.products.getById(id);
        const p = res.data;
        const modal = document.getElementById('modal-container');
        modal.innerHTML = `
        <div class="modal-overlay" onclick="if(event.target===this)closeModal()">
            <div class="modal">
                <div class="modal-header">
                    <h3 class="modal-title">Edit Product</h3>
                    <button class="modal-close" onclick="closeModal()"><i class="fas fa-times"></i></button>
                </div>
                <div class="auth-form">
                    <div class="form-group"><label class="form-label">Product Name</label><input type="text" id="ep-name" value="${p.name || ''}"></div>
                    <div class="form-group"><label class="form-label">Brand</label><input type="text" id="ep-brand" value="${p.brand || ''}"></div>
                    <div class="form-grid">
                        <div class="form-group"><label class="form-label">Price</label><input type="number" id="ep-price" value="${p.price}"></div>
                        <div class="form-group"><label class="form-label">Discount Price</label><input type="number" id="ep-dprice" value="${p.discountPrice || ''}"></div>
                    </div>
                    <div class="form-group"><label class="form-label">Image URL</label><input type="url" id="ep-image" value="${p.imageUrl || ''}"></div>
                    <div class="form-group"><label class="form-label">Stock</label><input type="number" id="ep-stock" value="${p.stockQuantity}"></div>
                    <button class="btn btn-primary btn-full" onclick="submitEditProduct(${p.id})">Save Changes</button>
                </div>
            </div>
        </div>`;
    } catch (err) { Toast.error(err.message); }
}

async function submitEditProduct(id) {
    const product = {
        name: document.getElementById('ep-name').value,
        brand: document.getElementById('ep-brand').value,
        price: parseFloat(document.getElementById('ep-price').value),
        discountPrice: parseFloat(document.getElementById('ep-dprice').value) || 0,
        imageUrl: document.getElementById('ep-image').value,
        stockQuantity: parseInt(document.getElementById('ep-stock').value)
    };
    try { await API.products.update(id, product); Toast.success('Product updated!'); closeModal(); Pages.admin(); }
    catch (err) { Toast.error(err.message); }
}

function closeModal() {
    document.getElementById('modal-container').innerHTML = '';
}

// ====== INITIALIZE ======
document.addEventListener('DOMContentLoaded', () => App.init());
