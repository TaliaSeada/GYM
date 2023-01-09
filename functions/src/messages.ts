import { https, logger } from 'firebase-functions';
import { firestore } from 'firebase-admin';

const db = firestore();


exports.getMessageListTrainee = https.onCall(async (data, context) => {
    if (!context.auth) {
      // Throwing an HttpsError so that the client gets the error details.
      throw new https.HttpsError('failed-precondition', 'The function must be called ' + 'while authenticated.');
    }
    const email = data.email;
    const snapshot = db.collection("message").whereEqualTo("trainee", email).get();
    const messages = [];
    snapshot.forEach(doc => {
      const message = {id: doc.id, ...doc.data()};
      logger.log(message);
      messages.push(message);
    });

    return messages;
});

exports.getMessageListTrainer = https.onCall(async (data, context) => {
    if (!context.auth) {
      // Throwing an HttpsError so that the client gets the error details.
      throw new https.HttpsError('failed-precondition', 'The function must be called ' + 'while authenticated.');
    }
    const email = data.email;
    const snapshot = db.collection("message").whereIn("trainer", Arrays.asList("all" , email)).get();
    const messages = [];
    snapshot.forEach(doc => {
      const message = {id: doc.id, ...doc.data()};
      logger.log(message);
      messages.push(message);
    });

    return messages;
});

// Create message
exports.addMessage = https.onCall(async (data, context) => {
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' +
        'while authenticated.');
  }
    await db.collection('message').doc().set({"message": data.message,
                                              "title": data.title,
                                              "answer": data.answer,
                                              "trainee": data.trainee,
                                              "trainer": data.trainer,
                                              "date": data.date
                                              });
    return;
});

// update message
exports.updateMessage = https.onCall(async (data, context) => {
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' +
        'while authenticated.');
  }
    const snapshot = db.collection("message").document(data.id);
     await docRef.update("answer", message);
     await docRef.update("trainer", email);
    return;
});


