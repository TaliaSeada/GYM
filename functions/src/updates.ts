import { https, logger } from 'firebase-functions';
import { firestore } from 'firebase-admin';

const db = firestore();

exports.getUpdates = https.onCall(async (data, context) => {
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' +
        'while authenticated.');
  }

  const now = new Date();

  const snapshot = await db.collection('updates').where('date', '>=', now).orderBy('date').get();
  const updates = [];

  snapshot.forEach(doc => {
    const update = { id: doc.id, ...doc.data() };
    logger.log(update);
    updates.push(update);
  })
  return updates;
});


