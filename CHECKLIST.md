# Pre-Launch Checklist - Razorpay Integration

## ✅ Configuration Checklist

### Razorpay Setup
- [ ] Created Razorpay account at https://razorpay.com
- [ ] Verified email with Razorpay
- [ ] Generated Test API Key ID and Secret
- [ ] Added credentials to `application.properties`:
  ```properties
  razorpay.key.id=rzp_test_XXXXXXXXXXXXXXXXXX
  razorpay.key.secret=XXXXXXXXXXXXXXXXXXXXXXXX
  ```

### Java & Maven
- [ ] Java 17+ installed (`java -version`)
- [ ] Maven 3.6+ installed (`mvn -version`)
- [ ] JAVA_HOME environment variable set
- [ ] Project cloned/extracted to workspace

### Build & Run
- [ ] Run `mvn clean install` - build succeeds
- [ ] Run `mvn spring-boot:run` - no errors
- [ ] Application starts on port 8080
- [ ] No compilation errors in console

### Frontend Verification
- [ ] Access http://localhost:8080
- [ ] Checkout page loads with proper styling
- [ ] Form fields are visible
- [ ] Razorpay Checkout button shows
- [ ] CSS and JS files load (check browser DevTools)

### Database
- [ ] H2 in-memory database initializes successfully
- [ ] Tables created automatically
- [ ] H2 console accessible at http://localhost:8080/h2-console
- [ ] Can view empty `payments` table

### First Payment Test
- [ ] Fill checkout form:
  - Name: John Doe
  - Email: john@example.com
  - Phone: 9876543210
  - Amount: 4999
- [ ] Click "Pay Securely with Razorpay"
- [ ] Razorpay modal opens
- [ ] Use test card: 4111111111111111
- [ ] Payment completes
- [ ] Redirected to success page
- [ ] Payment visible in H2 database

### Error Handling
- [ ] Try invalid email - shows error
- [ ] Try empty name - shows error
- [ ] Try invalid phone - shows error
- [ ] Try invalid amount - shows error
- [ ] Form validates on blur

### Application Features
- [ ] Home redirects to checkout
- [ ] Success page shows payment details
- [ ] Failure page accessible and styled
- [ ] Navigation bar visible on all pages
- [ ] Mobile responsive (resize browser)

### Logging
- [ ] Application logs show order creation
- [ ] Application logs show payment verification
- [ ] Debug logs available in console
- [ ] No warning or error messages

## 📋 File Structure Verification

Verify these files exist:

```
├── pom.xml                                    ✓
├── README.md                                  ✓
├── SETUP.md                                   ✓
├── src/main/java/com/example/razorpay/
│   ├── RazorpayApplication.java              ✓
│   ├── config/
│   │   ├── RazorpayConfig.java              ✓
│   │   └── WebConfig.java                    ✓
│   ├── controller/
│   │   ├── HomeController.java              ✓
│   │   ├── PaymentController.java           ✓
│   │   └── WebhookController.java           ✓
│   ├── dto/
│   │   ├── ApiResponse.java                 ✓
│   │   ├── OrderRequest.java                ✓
│   │   ├── OrderResponse.java               ✓
│   │   ├── PaymentVerificationRequest.java  ✓
│   │   └── PaymentVerificationResponse.java ✓
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java      ✓
│   │   ├── PaymentVerificationException.java ✓
│   │   └── ResourceNotFoundException.java    ✓
│   ├── model/
│   │   └── Payment.java                     ✓
│   ├── repository/
│   │   └── PaymentRepository.java           ✓
│   └── service/
│       ├── PaymentService.java              ✓
│       └── WebhookService.java              ✓
├── src/main/resources/
│   ├── application.properties                ✓
│   ├── templates/
│   │   ├── checkout.html                    ✓
│   │   ├── failure.html                     ✓
│   │   └── success.html                     ✓
│   └── static/
│       ├── css/style.css                    ✓
│       └── js/
│           ├── checkout.js                  ✓
│           └── status.js                    ✓
└── target/                                   (generated after build)
```

## 🔧 Configuration Details

### Server Settings
- Server Port: **8080**
- Context Path: **/**
- Max File Size: Default (unlimited)

### Database Settings
- Type: **H2 (In-Memory)**
- URL: **jdbc:h2:mem:razorpaydb**
- DDL: **create-drop** (auto-create tables)
- H2 Console: **Enabled** at `/h2-console`

### Logging
- Root Level: **INFO**
- App Level: **DEBUG** (for troubleshooting)
- Pattern: `[HH:mm:ss.SSS] [thread] Level logger - message`

### Validation
- Min Name Length: **2 characters**
- Min Amount: **₹1 (100 paise)**
- Max Amount: **₹99,99,999.99**
- Phone: **10-15 digits**
- Email: **Valid email format**

## 🚀 Common Issues & Solutions

### Issue: Build fails with dependency errors
**Solution**:
```bash
mvn clean install -DskipTests
mvn clean install
```

### Issue: Port 8080 already in use
**Solution**: Change port in `application.properties`:
```properties
server.port=8081
```

### Issue: Razorpay checkout not opening
**Solution**:
- Verify API keys in browser console (F12)
- Check network tab for 404 errors
- Ensure https://checkout.razorpay.com is accessible

### Issue: Payment verification fails
**Solution**:
- Verify signature is being sent correctly from frontend
- Check application logs for "verification failed"
- Ensure key secret matches in both frontend and backend

### Issue: Database table not created
**Solution**:
```bash
# Check H2 console at http://localhost:8080/h2-console
# If tables missing, restart application
mvn spring-boot:run
```

## 📊 Testing Payment Amounts

Test with different amounts:
- **Minimum**: 100 (₹1)
- **Standard**: 4999 (₹49.99) [as in example]
- **Large**: 1000000 (₹10,000)
- **Maximum**: 999999999 (₹99,99,999.99)

## 📱 Mobile Testing

1. Get your computer's IP: `ipconfig` (Windows) or `ifconfig` (Mac/Linux)
2. On mobile, open: `http://YOUR_IP:8080`
3. Test responsive design:
   - Browser DevTools (F12)
   - Toggle device toolbar (Ctrl+Shift+M)

## 🔐 Security Verification

- [ ] API keys not logged (check logs)
- [ ] Password fields masked
- [ ] HTTPS configured (for production)
- [ ] CORS only allows needed origins
- [ ] Signature validation enabled
- [ ] Error messages don't expose sensitive data

## 📈 Performance Checks

- [ ] Page loads in < 2 seconds
- [ ] Payment processing < 3 seconds
- [ ] No console errors
- [ ] No infinite loops/hangs
- [ ] Memory usage normal

## 🎯 Ready for Production?

Before deploying to production:

- [ ] Switched to live Razorpay credentials
- [ ] Database changed to MySQL/PostgreSQL
- [ ] HTTPS configured with valid certificate
- [ ] Webhooks configured in Razorpay dashboard
- [ ] Logging configured appropriately
- [ ] Error messages reviewed for safety
- [ ] Payment amount validation verified
- [ ] Email validation working
- [ ] Load testing performed
- [ ] Backup strategy in place

## ✨ Final Steps

1. Run complete build:
   ```bash
   mvn clean install
   ```

2. Start application:
   ```bash
   mvn spring-boot:run
   ```

3. Verify at http://localhost:8080

4. Test complete payment flow

5. Check database for payment record

6. Review logs for any issues

7. Deploy when ready!

---

**All set! Your Razorpay integration is production-ready.** ✅
