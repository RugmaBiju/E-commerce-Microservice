# Razorpay Payment Gateway Integration - Java Spring Boot

A complete, production-ready Razorpay payment gateway integration for Java Spring Boot applications.

## 📋 Features

### ✅ Complete Integration
- **Order Creation**: Create Razorpay orders via REST API
- **Payment Verification**: HMAC-SHA256 signature verification
- **Webhook Handling**: Async payment event processing
- **Database Persistence**: Store all payment records in database
- **Error Handling**: Global exception handling with structured responses

### 💎 Frontend
- **Modern UI**: Beautiful glass-morphism design with gradient effects
- **Form Validation**: Real-time client-side and server-side validation
- **Responsive Design**: Fully mobile-friendly interface
- **Payment Gateway**: Integrated Razorpay Checkout JS
- **Status Pages**: Success and failure pages with animations

### 🔐 Security
- **Signature Verification**: Verify payment signatures using HMAC-SHA256
- **Input Validation**: Comprehensive validation using Jakarta Validation
- **CORS Configuration**: Secure cross-origin request handling
- **Error Messages**: Safe error messages without exposing sensitive data

### 📊 Data Management
- **Payment Tracking**: Full audit trail of all payments
- **Payment History**: Query payments by customer, date range, status
- **Revenue Reports**: Calculate total revenue from successful payments
- **Status Management**: Track payment lifecycle (CREATED, AUTHORIZED, CAPTURED, FAILED, REFUNDED)

### 🔧 Development Features
- **H2 Database**: In-memory database for development (easy setup)
- **Spring Data JPA**: Object-relational mapping with repository pattern
- **Lombok**: Reduce boilerplate code
- **SLF4J Logging**: Comprehensive logging with multiple levels
- **Thymeleaf**: Server-side template rendering

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Razorpay Account (https://razorpay.com)

### Step 1: Get Razorpay Credentials
1. Create a Razorpay account at https://razorpay.com
2. Go to Dashboard → Settings → API Keys
3. Copy your **Key ID** and **Key Secret** (Test/Live)

### Step 2: Configure Application
Edit `src/main/resources/application.properties`:

```properties
razorpay.key.id=rzp_test_XXXXXXXXXXXXXXXXXX
razorpay.key.secret=XXXXXXXXXXXXXXXXXXXXXXXX
```

**Important**: Use test keys during development. Switch to live keys only in production.

### Step 3: Build and Run

```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Step 4: Access the Application
- **Checkout Page**: http://localhost:8080/payment/checkout
- **H2 Console**: http://localhost:8080/h2-console (for debugging)

## 📁 Project Structure

```
src/main/
├── java/com/example/razorpay/
│   ├── config/
│   │   ├── RazorpayConfig.java      # Razorpay client configuration
│   │   └── WebConfig.java           # Web MVC configuration
│   ├── controller/
│   │   ├── HomeController.java      # Home page redirection
│   │   ├── PaymentController.java   # Payment endpoints (MVC + REST)
│   │   └── WebhookController.java   # Razorpay webhook handler
│   ├── dto/
│   │   ├── OrderRequest.java        # Order creation request
│   │   ├── OrderResponse.java       # Order creation response
│   │   ├── PaymentVerificationRequest.java
│   │   ├── PaymentVerificationResponse.java
│   │   └── ApiResponse.java         # Generic API response wrapper
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── PaymentVerificationException.java
│   │   └── ResourceNotFoundException.java
│   ├── model/
│   │   └── Payment.java             # Payment entity (JPA)
│   ├── repository/
│   │   └── PaymentRepository.java   # Payment data access
│   ├── service/
│   │   ├── PaymentService.java      # Payment business logic
│   │   └── WebhookService.java      # Webhook event processing
│   └── RazorpayApplication.java     # Spring Boot entry point
├── resources/
│   ├── application.properties        # Application configuration
│   ├── templates/
│   │   ├── checkout.html            # Checkout page
│   │   ├── success.html             # Success page
│   │   └── failure.html             # Failure page
│   └── static/
│       ├── css/style.css            # Styling
│       └── js/
│           ├── checkout.js          # Checkout logic
│           └── status.js            # Status page animations
└── test/
    └── java/com/example/razorpay/   # Unit tests
```

## 🔌 API Endpoints

### 1. Create Order
**Endpoint**: `POST /payment/create-order`

**Request Body**:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "amount": 50000,
  "currency": "INR",
  "description": "Product Description"
}
```

**Response**:
```json
{
  "status": "success",
  "data": {
    "orderId": "order_XXXXXXXXXX",
    "razorpayKeyId": "rzp_test_XXXXXXXXX",
    "amount": 50000,
    "currency": "INR",
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "9876543210",
    "description": "Product Description",
    "callbackUrl": "/payment/success"
  }
}
```

### 2. Verify Payment
**Endpoint**: `POST /payment/verify`

**Request Body**:
```json
{
  "razorpayPaymentId": "pay_XXXXXXXXXX",
  "razorpayOrderId": "order_XXXXXXXXX",
  "razorpaySignature": "SIGNATURE_STRING"
}
```

**Response**:
```json
{
  "status": "success",
  "data": {
    "paymentId": "pay_XXXXXXXXXX",
    "orderId": "order_XXXXXXXXX",
    "verified": true,
    "amount": 50000,
    "currency": "INR"
  }
}
```

### 3. Get Payment Details
**Endpoint**: `GET /payment/details/{paymentId}`

**Response**:
```json
{
  "status": "success",
  "data": "{ ... Razorpay payment details ... }"
}
```

### 4. Webhook Handler
**Endpoint**: `POST /webhook/razorpay`

Razorpay will POST webhook events to this endpoint. Configure in Razorpay Dashboard:
- Go to Settings → Webhooks
- Add URL: `https://yourdomain.com/webhook/razorpay`
- Subscribe to events: payment.authorized, payment.captured, payment.failed, order.paid, refund.created

## 📋 Payment Statuses

| Status | Description |
|--------|-------------|
| CREATED | Order created, awaiting payment |
| AUTHORIZED | Payment authorized by payment method |
| CAPTURED | Payment successfully captured |
| FAILED | Payment failed |
| REFUNDED | Payment refunded |
| CANCELLED | Payment cancelled by user |
| PENDING | Payment pending confirmation |

## 🗄️ Database Schema

### Payments Table

```sql
CREATE TABLE payments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  razorpay_payment_id VARCHAR(255) UNIQUE,
  razorpay_order_id VARCHAR(255) UNIQUE,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL,
  phone VARCHAR(15) NOT NULL,
  amount BIGINT NOT NULL,
  currency VARCHAR(3) NOT NULL,
  description VARCHAR(255),
  payment_method VARCHAR(50),
  status ENUM ('CREATED', 'AUTHORIZED', 'CAPTURED', 'FAILED', 'REFUNDED', 'CANCELLED', 'PENDING'),
  razorpay_signature VARCHAR(255),
  signature_verified BOOLEAN DEFAULT FALSE,
  razorpay_response TEXT,
  error_message TEXT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  INDEX idx_razorpay_payment_id (razorpay_payment_id),
  INDEX idx_razorpay_order_id (razorpay_order_id),
  INDEX idx_email (email),
  INDEX idx_status (status)
);
```

## 🧪 Testing the Integration

### Test with Razorpay Test Credentials

Use these test card numbers:

**Visa Cards**:
- 4111111111111111 (Any future date, any CVV)

**MasterCard**:
- 5555555555554444 (Any future date, any CVV)

**Amex**:
- 378282246310005 (Any future date, any CVV)

### Test UPI
- UPI ID: `test@upi`

### Test Netbanking
- Bank: Select any bank
- Login ID: Any value
- Password: Any value

## 🌐 Production Deployment

### 1. Switch to Live Credentials
In `application.properties`:
```properties
razorpay.key.id=rzp_live_XXXXXXXXXXXXXXXXXX
razorpay.key.secret=XXXXXXXXXXXXXXXXXXXXXXXX
```

### 2. Configure Database
Replace H2 with MySQL/PostgreSQL in `application.properties`:

**MySQL**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/razorpay_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=validate
```

### 3. Set Environment Variables
Store credentials securely using environment variables:

```bash
export RAZORPAY_KEY_ID=rzp_live_XXXXXXXXX
export RAZORPAY_KEY_SECRET=XXXXXXXXXXX
```

Then update `application.properties`:
```properties
razorpay.key.id=${RAZORPAY_KEY_ID}
razorpay.key.secret=${RAZORPAY_KEY_SECRET}
```

### 4. HTTPS Configuration
Enable SSL/TLS (required for production):

```properties
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your_password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
```

### 5. Configure Webhooks
In Razorpay Dashboard:
- Settings → Webhooks
- Add webhook URL: `https://yourdomain.com/webhook/razorpay`
- Select events to monitor

## 📚 Dependencies

- **Spring Boot**: Web framework
- **Spring Data JPA**: Database access
- **Thymeleaf**: Template engine
- **Razorpay Java SDK**: Payment gateway client
- **Lombok**: Code generation
- **Jakarta Validation**: Input validation
- **H2 Database**: In-memory database (dev)
- **Jackson**: JSON processing

See `pom.xml` for complete dependency list.

## 🐛 Troubleshooting

### Issue: "Razorpay API key not found"
**Solution**: Ensure `razorpay.key.id` and `razorpay.key.secret` are correctly set in `application.properties`

### Issue: "Payment verification failed"
**Solution**: Verify that:
- Payment signature from frontend matches backend calculation
- Using correct key secret
- Order and payment IDs are correct

### Issue: "Database connection error"
**Solution**: Check `spring.datasource.url` matches your database configuration

### Issue: "Static resources not loading (CSS/JS)"
**Solution**: Ensure static files are in `src/main/resources/static/` with correct directory structure

### Issue: "Webhook not being triggered"
**Solution**: 
- Verify webhook URL is public and accessible
- Check Razorpay webhook logs in dashboard
- Ensure webhook secret is correct

## 📖 Additional Resources

- **Razorpay Documentation**: https://razorpay.com/docs/
- **Razorpay Java SDK**: https://github.com/razorpay/razorpay-java
- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **Thymeleaf Documentation**: https://www.thymeleaf.org

## 📄 License

This project is open source and available under the MIT License.

## 💡 Support

For issues and questions:
1. Check the troubleshooting section above
2. Review Razorpay documentation
3. Check application logs for error messages
4. Enable DEBUG logging: `logging.level.com.example.razorpay=DEBUG`

## 🎯 Next Steps

1. ✅ Set up Razorpay account and get API credentials
2. ✅ Configure credentials in `application.properties`
3. ✅ Build and run the application
4. ✅ Test payment flow with test credentials
5. ✅ Deploy to production with live credentials
6. ✅ Configure webhooks in Razorpay dashboard

## ✨ Features to Extend

- Multi-currency support
- Refund handling
- Payment plan/subscription support
- Analytics dashboard
- Invoice generation
- Email notifications
- SMS notifications
- Payment history API
- Admin panel for payment management

Good luck with your Razorpay integration! 🚀
