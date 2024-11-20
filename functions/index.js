const functions = require("firebase-functions");
const admin = require("firebase-admin");
const nodemailer = require("nodemailer");

admin.initializeApp();

// Configure your email service (e.g., Gmail or another SMTP provider)
const transporter = nodemailer.createTransport({
    service: "gmail",
    auth: {
        user: "your-email@gmail.com", // Your email
        pass: "your-email-password", // Your email password or app password
    },
});

exports.sendOrderEmail = functions.firestore
    .document("orders/{orderId}")
    .onCreate(async (snap, context) => {
        const orderData = snap.data();
        const email = orderData.userEmail;
        const orderSummary = `
            Order Summary:
            Name: ${orderData.name}
            Address: ${orderData.address}
            Payment Method: ${orderData.paymentMethod}
            Items:
            ${orderData.orderItems.map(item => `${item.itemName} x${item.itemQuantity} - $${item.itemPrice}`).join("\n")}
            
            Total: $${orderData.totalCost}
            Thank you for ordering from MoodBites!
        `;

        const mailOptions = {
            from: "your-email@gmail.com",
            to: email,
            subject: "Your MoodBites Order Summary",
            text: orderSummary,
        };

        try {
            await transporter.sendMail(mailOptions);
            console.log("Order email sent to:", email);
        } catch (error) {
            console.error("Error sending email:", error);
        }
    });
