import { https, logger } from 'firebase-functions';
import { firestore } from 'firebase-admin';

const db = firestore();

exports.getPersonalDetails = https.onCall(async (data, context) => {
  // Checking attribute.
//   if (!(typeof data.text === 'string') || text.length === 0) {
//     // Throwing an HttpsError so that the client gets the error details.
//     throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
//         'one arguments "text" containing the message text to add.');
//   }
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


