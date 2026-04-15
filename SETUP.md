# Quick Setup Guide - Razorpay Integration

## 🚀 Get Started in 5 Minutes

### Step 1: Get Razorpay API Keys
1. Go to https://razorpay.com/signup
2. Create account and verify email
3. Login to dashboard
4. Go to **Settings** → **API Keys**
5. Copy your **Test Key ID** and **Test Secret**

### Step 2: Configure Your Project
Open `src/main/resources/application.properties` and replace:

```properties
# BEFORE:
razorpay.key.id=rzp_test_XXXXXXXXXXXXXXXXXX
razorpay.key.secret=XXXXXXXXXXXXXXXXXXXXXXXX

# AFTER:
razorpay.key.id=YOUR_TEST_KEY_ID
razorpay.key.secret=YOUR_TEST_KEY_SECRET
```

### Step 3: Run the Application
```bash
cd /path/to/RazorPay
mvn clean install
mvn spring-boot:run
```

### Step 4: Open in Browser
Go to: **http://localhost:8080**

### Step 5: Test Payment
1. Fill in the checkout form:
   - Name: John Doe
   - Email: john@example.com
   - Phone: 9876543210
   - Amount: 4999 (₹49.99)

2. Click "Pay Securely with Razorpay"

3. Use test card: **4111 1111 1111 1111**
   - Any future expiry date
   - Any CVV (e.g., 123)

4. Verify: You should see the success page!

## 📋 Test Credentials Cheat Sheet

### Test Cards
| Card | Number | Expiry | CVV |
|------|--------|--------|-----|
| Visa | 4111111111111111 | Any future | Any |
| MasterCard | 5555555555554444 | Any future | Any |
| Amex | 378282246310005 | Any future | Any |

### Test UPI
- **UPI ID**: `test@upi`

### Test Netbanking
- **Bank**: Any bank
- **Login**: Any value
- **Password**: Any value

## 🔍 Key Files to Know

| File | Purpose |
|------|---------|
| `application.properties` | Configuration (API keys, database, logging) |
| `controller/PaymentController.java` | Payment endpoints |
| `service/PaymentService.java` | Business logic |
| `templates/checkout.html` | Checkout form |
| `static/js/checkout.js` | Frontend payment handling |

## 💾 Database Access

Access H2 database console:
- URL: **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:mem:razorpaydb`
- Username: `sa`
- Password: (leave empty)

## 🐛 Troubleshooting

### Payment page not loading?
- Check browser console for errors (F12)
- Verify API keys in `application.properties`
- Make sure Razorpay API keys are correct

### "Failed to create order" error?
- Confirm API credentials are correct
- Check you're using test (not live) keys
- Verify internet connection
- Check application logs: `mvn spring-boot:run` output

### Can't access http://localhost:8080?
- Ensure Maven build succeeded: `mvn clean install`
- Confirm Spring Boot is running (check console output)
- Try http://127.0.0.1:8080 instead
- Check if port 8080 is available

## 📊 Verify It Works

After successful payment, you should see:
1. ✅ Checkout page with beautiful form
2. ✅ Razorpay payment modal opens
3. ✅ Success page showing payment ID
4. ✅ Payment record in database (access via H2 console)

## 🏭 Production Deployment

When ready for live payments:

1. Get live credentials from Razorpay
2. Update `application.properties`:
   ```properties
   razorpay.key.id=rzp_live_XXXXXXXXXXX
   razorpay.key.secret=XXXXXXXXXXXXXXXXXXX
   ```
3. Configure proper database (MySQL/PostgreSQL)
4. Enable HTTPS
5. Configure webhooks in Razorpay dashboard
6. Deploy to server

## 📞 Need Help?

1. **Check README.md** - Comprehensive documentation
2. **Review error logs** - Run with debug logging:
   ```bash
   export SPRING_LOG_LEVEL=DEBUG
   mvn spring-boot:run
   ```
3. **Razorpay Docs** - https://razorpay.com/docs/
4. **Spring Boot Docs** - https://spring.io/projects/spring-boot

## ✨ What You Can Do Now

✅ Accept payments via cards, UPI, netbanking  
✅ Track all transactions in database  
✅ Receive webhook notifications  
✅ Generate revenue reports  
✅ Handle payment failures gracefully  
✅ Verify payment authenticity  

---

**Congratulations! Your Razorpay integration is ready!** 🎉

For advanced configurations, see **README.md**
