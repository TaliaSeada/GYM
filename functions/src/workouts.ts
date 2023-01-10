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
    const exercises = [];

    snapshot.forEach(doc => {
      const exe = {id: doc.id, ...doc.data()};
      logger.log(exe);
      exercises.push(exe);
    });

    return exercises;

});

exports.getTraineeList = https.onCall(async (data, context) => {
    if (!context.auth) {
      // Throwing an HttpsError so that the client gets the error details.
      throw new https.HttpsError('failed-precondition', 'The function must be called ' + 'while authenticated.');
    }

    const snapshot = await db.collection('users').where('role', '==', 'trainee').get();
    const trainees = [];

    snapshot.forEach(doc => {
      const trainee = {id: doc.id, ...doc.data()};
      logger.log(trainee);
      trainees.push(trainee);
    });

    return trainees;

});

exports.getWorkoutList = https.onCall(async (data, context) => {
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' + 'while authenticated.');
  }

  const email = data.email;

  const snapshot = await db.collection('user-info').doc(email).collection('workouts').get();
  const wo_list = [];

  snapshot.forEach(doc => {
    const wo = {id: doc.id, ...doc.data()};
    logger.log(wo);
    wo_list.push(wo);
  });

  return wo_list;

});

exports.getExercise = https.onCall(async (data, context) => {
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' + 'while authenticated.');
  }

  const email = data.email;
  const name_wo = data.name_wo;
  const name_exe = data.name_exe;

  const snapshot = await db.collection('user-info').doc(email).
  collection('workouts').doc(name_wo).collection('exercises').where('name', '==', name_exe).get();
    const exercises = [];

    snapshot.forEach(doc => {
      const exe = {id: doc.id, ...doc.data()};
      logger.log(exe);
      exercises.push(exe);
    });
  return exercises;

});

// Create exercise
exports.createExercise = https.onCall(async (data, context) => {
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' + 'while authenticated.');
  }

  const email = data.email;
  const name_wo = data.name_wo;
  const name_exe = data.name_exe;
  const exe = data.exe;

    await db.collection('user-info').doc(email).
    collection('workouts').doc(name_wo).collection('exercises').doc(name_exe).set(exe);
    return;
});


// Delete exercise
exports.deleteExercise = https.onCall(async (data, context) => {
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' + 'while authenticated.');
  }

    const email = data.email;
    const name_wo = data.name_wo;
    const name_exe = data.name_exe;

    const res = await db.collection('users').doc(email)
    .collection('workouts').doc(name_wo)
    .collection('exercises').doc(name_exe).delete();
    try{
      await res;
      return {message: 'Exercise was deleted successfully'};
    }catch(err){
      return {error: err};
    }
});

// Create workout
exports.createWorkout = https.onCall(async (data, context) => {
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' + 'while authenticated.');
  }

  const email = data.email;
  const name_wo = data.name_wo;
  const name = data.name;

  await db.collection('user-info').doc(email).
  collection('workouts').doc(name_wo).set(name);
  return;
});

// Delete workout
exports.deleteWorkout = https.onCall(async (data, context) => {
  if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new https.HttpsError('failed-precondition', 'The function must be called ' + 'while authenticated.');
  }

    const email = data.email;
    const name_wo = data.name_wo;

    const res = await db.collection('users').doc(email)
    .collection('workouts').doc(name_wo).delete();
    try{
      await res;
      return {message: 'Workout was deleted successfully'};
    }catch(err){
      return {error: err};
    }
});