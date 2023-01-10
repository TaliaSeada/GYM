import { https, logger } from 'firebase-functions';
import { firestore } from 'firebase-admin';

const db = firestore();


exports.getMessageListTrainee = https.onCall(async (data, context) => {
    if (!context.auth) {
      // Throwing an HttpsError so that the client gets the error details.
      throw new https.HttpsError('failed-precondition', 'The function must be called ' + 'while authenticated.');
    }
    const email = data.email;
    const snapshot = await db.collection('message').where('trainee', '==', email).get();
//     const snapshot = db.collection('message', ref => ref.where('trainee', '==', email)).get();
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
    const snapshot = await db.collection('message').where('trainer', 'in', [email, 'all']).get();
//     const snapshot = db.collection("message").whereIn("trainer", Arrays.asList("all" , email)).get();
    const messages = [];
    snapshot.forEach(doc => {
      const message = {id: doc.id, ...doc.data()};
      logger.log(message);
      messages.push(message);
    });

    return messages;
});

// Create message
exports.addMessageTrainee = https.onCall(async (data, context) => {
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' +
        'while authenticated.');
  }
//     await db.collection('message').doc().set({"trainee": data.trainee, "trainer": data.trainer, "message": data.message, "answer": data.answer, "title": data.title});
//                                                "date": data.date
//     await db.collection('users').doc(email).set({"role": data.role, "full_name": data.full_name});
    await db.collection('message').doc().set(data.mess);
    return {message: 'message was update successfully'};
});

// update message
exports.updateMessage = https.onCall(async (data, context) => {
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' +
        'while authenticated.');
  }
    const snapshot = db.collection("message").doc(data.id);
     await snapshot.update("answer", data.message);
     await snapshot.update("trainer", data.email);
    return {message: 'message was update successfully'};
});


