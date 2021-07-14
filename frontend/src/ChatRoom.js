import "./App.css";
import React, { useRef, useState } from "react";

import { FaPaperPlane } from "react-icons/fa";

import firebase from "firebase/app";
import "firebase/firestore";
import "firebase/auth";

import { useDocument } from "react-firebase-hooks/firestore";

firebase.initializeApp({
  apiKey: "AIzaSyCJnkbNvkV1BVl9qAUjKfwgxlv-YvvZers",
  authDomain: "todolistchat.firebaseapp.com",
  databaseURL: "https://todolistchat-default-rtdb.firebaseio.com",
  projectId: "todolistchat",
  storageBucket: "todolistchat.appspot.com",
  messagingSenderId: "805043621041",
  appId: "1:805043621041:web:2f2a72fa18a55070ee6690",
  measurementId: "G-HMFX5ZFXJE",
});

const firestore = firebase.firestore();

export default function ChatRoom({ match }) {
  const dummy = useRef();
  let { id1, id2 } = match.params;
  const [value, loading, error] = useDocument(
    firestore.doc(`frontend/${id1}`),
    { snapshotListenOptions: { includeMetadataChanges: true } }
  );
  if (loading) console.log("loading .......");
  if (error) console.log("error is: ", error);

  const [formValue, setFormValue] = useState("");
  const sendMessage = async (e) => {
    e.preventDefault();

    // TODO: should call netty server with this info
    let documentRefSender = firestore.doc(`frontend/${id1}`);
    let documentRefReciver = firestore.doc(`frontend/${id2}`);
    let msgBody = {
      text: formValue,
      uid: id1,
      createdAt: new Date(),
    };

    var objSender = {};
    objSender[id2] = firebase.firestore.FieldValue.arrayUnion(msgBody);
    await documentRefSender.update(objSender);

    var objReciver = {};
    objReciver[id1] = firebase.firestore.FieldValue.arrayUnion(msgBody);
    await documentRefReciver.update(objReciver);

    setFormValue("");
    dummy.current.scrollIntoView({ behavior: "smooth" });
  };

  return (
    <>
      <main>
        {value &&
          value
            .data()
            [id2].map((msg, index) => (
              <ChatMessage key={index} message={msg} userID={id1} />
            ))}
        <span ref={dummy}></span>
      </main>

      <form onSubmit={sendMessage}>
        <input
          value={formValue}
          onChange={(e) => setFormValue(e.target.value)}
          placeholder="say something nice"
        />

        <button type="submit" disabled={!formValue}>
          <FaPaperPlane />
        </button>
      </form>
    </>
  );
}

function ChatMessage(props) {
  const { text, uid } = props.message;
  const id = props.userID;

  const messageClass = uid === id ? "sent" : "received";
  return (
    <>
      <div className={`message ${messageClass}`}>
        <p>{text}</p>
      </div>
    </>
  );
}
