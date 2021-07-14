import firebase from "firebase";

const firebaseConfig = {
  apiKey: "AIzaSyCJnkbNvkV1BVl9qAUjKfwgxlv-YvvZers",
  authDomain: "todolistchat.firebaseapp.com",
  databaseURL: "https://todolistchat-default-rtdb.firebaseio.com",
  projectId: "todolistchat",
  storageBucket: "todolistchat.appspot.com",
  messagingSenderId: "805043621041",
  appId: "1:805043621041:web:2f2a72fa18a55070ee6690",
  measurementId: "G-HMFX5ZFXJE",
};
firebase.initializeApp(firebaseConfig);
export default firebase;
