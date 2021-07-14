import React from "react";
import "./App.css";
import ChatRoom from "./ChatRoom";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";

function App() {
  return (
    <div className="App">
      <header>
        <h1>Chat Room 💬</h1>
      </header>
      <Router>
        <Switch>
          <Route exact path="/chat/:id1/:id2" component={ChatRoom}></Route>
          <Route>
            <header>NOT FOUND</header>
          </Route>
        </Switch>
      </Router>
    </div>
  );
}

export default App;
