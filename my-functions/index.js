const functions = require('firebase-functions');
const admin = require('firebase-admin');
const axios = require('axios');

admin.initializeApp();

const db = admin.firestore();

exports.scheduledFunction = functions.pubsub.schedule('every 1 minutes').onRun(async (context) => {
  const ordersRef = db.collection('orders');
  const now = admin.firestore.Timestamp.now();

  const snapshot = await ordersRef.where('timestamp', '<=', new Date(now.toMillis() - 15 * 60 * 1000)).get();

  const promises = snapshot.docs.map(async (doc) => {
    const orderData = doc.data();
    await doc.ref.delete();

    // Notify broker
    try {
      await axios.get(`http://localhost:8080/broker/remove/${doc.id}`);
      console.log(`Notified broker about the deletion of order: ${doc.id}`);
    } catch (error) {
      console.error(`Failed to notify broker about the deletion of order: ${doc.id}`, error);
    }
  });

  await Promise.all(promises);

  return null;
});
