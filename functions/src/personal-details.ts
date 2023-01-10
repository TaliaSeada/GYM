import { https, logger } from 'firebase-functions';
import { firestore } from 'firebase-admin';

const db = firestore();

exports.getPersonalDetails = https.onCall(async (data, context) => {
  // Checking that the user is authenticated.
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' +
        'while authenticated.');
  }
  const email = context.auth.token.email;
  logger.log(`email: "${email}"`);

  const userInfoRef = db.collection('user-info').doc(email);
  const doc = await userInfoRef.get();
  if(!doc.exists) {
    logger.log(`user info for email: "${email}" not found`);
    throw new https.HttpsError('invalid-argument', `user info for email: "${email}" not found`);
  } else {
    return doc.data();
  }
});
exports.addDate = https.onCall(async (data, context) => {
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' +
        'while authenticated.');
  }
    const snapshot = db.collection("user-info").doc(data.email);
    await snapshot.update("dateBirth", data.date);
    return {message: 'date was update successfully'};
});
exports.addDetails = https.onCall(async (data, context) => {
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' +
        'while authenticated.');
  }
    const snapshot = db.collection("user-info").doc(data.email);
     await snapshot.update("height", data.height);
     await snapshot.update("weight", data.weight);
    return {message: 'details was update successfully'};
});
exports.addGender = https.onCall(async (data, context) => {
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' +
        'while authenticated.');
  }
    const snapshot = db.collection("user-info").doc(data.email);
     await snapshot.update("gender", data.gender);
    return {message: 'gender was update successfully'};
});

exports.getName = https.onCall(async (data, context) => {
    if (!context.auth) {
      // Throwing an HttpsError so that the client gets the error details.
      throw new https.HttpsError('failed-precondition', 'The function must be called ' + 'while authenticated.');
    }
    const email = data.email;
    const snapshot = await db.collection('users').doc(email);
    const doc = await snapshot.get();


    return doc.data();
});



