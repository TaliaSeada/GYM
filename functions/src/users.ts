import { https, logger, auth } from 'firebase-functions';
import { firestore } from 'firebase-admin';
import { auth as adminAuth } from 'firebase-admin'

const db = firestore();

export enum ROLES {
    MANAGER = 'manager',
    TRAINEE = 'trainee',
    TRAINER = 'trainer',
}

// Get all users
exports.getAllUsers = https.onCall(async (data, context) => {
  if (!context.auth || context.auth.token.role != ROLES.MANAGER) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' +
        'while authenticated.');
  }
    const role = data.role;
    const snapshot = await db.collection('users').where('role', '==', role).get();
    const users = [];

    snapshot.forEach(doc => {
      const user = { email: doc.id, ...doc.data() };
      logger.log(user);
      users.push(user);
    });
    return users;
});


// Create user
exports.createUser = https.onCall(async (data, context) => {
  if (!context.auth || context.auth.token.role != ROLES.MANAGER) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' +
        'while authenticated.');
  }
  const email = data.email.toLowerCase();
    await db.collection('users').doc(email).set({"role": data.role, "full_name": data.full_name});
    return;
});


// Delete user
exports.deleteUser = https.onCall(async (data, context) => {
  if (!context.auth || context.auth.token.role != ROLES.MANAGER) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' +
        'while authenticated.');
  }
    const email = data.email;
    const res = await db.collection('users').doc(email).delete();
    return;
});


// Before User Sign In- blocks user that the manager don't signed to the gym
exports.beforeSignIn = auth.user().beforeSignIn( async (user, context) => {

    const docSnapshot = await db.collection('users').doc(user?.email).get();

    if (!docSnapshot.exists) {
        adminAuth().deleteUser(user.uid);
        throw new https.HttpsError('permission-denied', 'The user not allowed to connect,please talk to the manager');
    }
    const userDoc = docSnapshot.data();

    return {
         sessionClaims: {
            role: userDoc.role,
         },
    };
});
