import { https, logger } from 'firebase-functions';
import { firestore } from 'firebase-admin';

const db = firestore();

exports.getExercisesList = https.onCall(async (data, context) => {
    if (!context.auth) {
      // Throwing an HttpsError so that the client gets the error details.
      throw new https.HttpsError('failed-precondition', 'The function must be called ' + 'while authenticated.');
    }

    const email = data.email;
    const name = data.name;

    const snapshot = await db.collection('user-info').doc(email).collection('workouts').doc(name).collection('exercises').get();
//     const docsSnap = await getDocs(snapshot);
    const exercises = [];

    snapshot.forEach(doc => {
//       const exe = console.log(doc.data());
      const exe = {id: doc.id, ...doc.data()};
      logger.log(exe);
      exercises.push(exe);
    });

    return exercises;

});