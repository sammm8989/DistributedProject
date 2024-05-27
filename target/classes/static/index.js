import {
  initializeApp
} from "https://www.gstatic.com/firebasejs/9.9.4/firebase-app.js";
import {
  getAuth,
  connectAuthEmulator,
  onAuthStateChanged,
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
} from "https://www.gstatic.com/firebasejs/9.9.4/firebase-auth.js";

// we setup the authentication, and then wire up some key events to event handlers
setupAuth();
wireGuiUpEvents();
wireUpAuthChange();

let token;

//setup authentication with local or cloud configuration. 
function setupAuth() {
  let firebaseConfig;
  if (location.hostname === "localhost") {
    firebaseConfig = {
      apiKey: "AIzaSyBoLKKR7OFL2ICE15Lc1-8czPtnbej0jWY",
      projectId: "demo-distributed-systems-kul",
    };
  } else {
    firebaseConfig = {
      // TODO: for level 2, paste your config here
    };
  }

  // signout any existing user. Removes any token still in the auth context
  const firebaseApp = initializeApp(firebaseConfig);
  let auth = getAuth(firebaseApp);
  try {
    auth.signOut();
  } catch (err) { }

  // connect to local emulator when running on localhost
  if (location.hostname === "localhost") {
    connectAuthEmulator(auth, "http://localhost:8082", { disableWarnings: true });
  }
}

function wireGuiUpEvents() {
  // Get references to the email and password inputs, and the sign in, sign out and sign up buttons
  var email = document.getElementById("email");
  var password = document.getElementById("password");
  var signInButton = document.getElementById("btnSignIn");
  var signUpButton = document.getElementById("btnSignUp");
  var logoutButton = document.getElementById("btnLogout");

  // Add event listeners to the sign in and sign up buttons
  signInButton.addEventListener("click", function () {
    // Sign in the user using Firebase's signInWithEmailAndPassword method

    signInWithEmailAndPassword(getAuth(), email.value, password.value)
      .then(function () {

        console.log("signedin");
      })
      .catch(function (error) {
        // Show an error message
        console.log("error signInWithEmailAndPassword:")
        console.log(error.message);
        alert(error.message);
      });
  });

  signUpButton.addEventListener("click", function () {
    // Sign up the user using Firebase's createUserWithEmailAndPassword method

    createUserWithEmailAndPassword(getAuth(), email.value, password.value)
      .then(function () {
        console.log("created");
      })
      .catch(function (error) {
        // Show an error message
        console.log("error createUserWithEmailAndPassword:");
        console.log(error.message);
        alert(error.message);
      });
  });

  logoutButton.addEventListener("click", function () {
    try {
      var auth = getAuth();
      auth.signOut();
    } catch (err) { }
  });

}

function wireUpAuthChange() {

  var auth = getAuth();
  onAuthStateChanged(auth, (user) => {
    console.log("onAuthStateChanged");
    if (user == null) {
      console.log("user is null");
      showUnAuthenticated();
      return;
    }
    if (auth == null) {
      console.log("auth is null");
      showUnAuthenticated();
      return;
    }
    if (auth.currentUser === undefined || auth.currentUser == null) {
      console.log("currentUser is undefined or null");
      showUnAuthenticated();
      return;
    }

    auth.currentUser.getIdTokenResult().then((idTokenResult) => {
      console.log("Hello " + auth.currentUser.email)

      //update GUI when user is authenticated
      showAuthenticated(auth.currentUser.email);

      console.log("Token: " + idTokenResult.token);

      //fetch data from server when authentication was successful. 
      token = idTokenResult.token;
    });

  });
}

function showAuthenticated(username) {
  document.getElementById("namediv").innerHTML = "Hello " + username;
  document.getElementById("logindiv").style.display = "none";
  document.getElementById("contentdiv").style.display = "block";
  addContent();
}

function showUnAuthenticated() {
  document.getElementById("namediv").innerHTML = "";
  document.getElementById("email").value = "";
  document.getElementById("password").value = "";
  document.getElementById("logindiv").style.display = "block";
  document.getElementById("contentdiv").style.display = "none";
}

function addContent(text) {
    const htmlContent = `
    <div id="bus">
        <h2>Bus</h2>
        <label for="busDatetime">Pick a DateTime:</label>
        <input type="datetime-local" id="busDatetime" name="busDatetime"><br><br>

        <label for="roundtrip">Roundtrip:</label>
        <input type="checkbox" id="roundtrip" name="roundtrip"><br><br>

        <label for="pickupPlace">Place to get up the bus:</label>
        <select id="pickupPlace" name="pickupPlace">
            <option value="LEUVEN">LEUVEN</option>
            <option value="AARSCHOT">AARSCHOT</option>
            <option value="TIENEN">TIENEN</option>
            <option value="HOEGAARDEN">HOEGAARDEN</option>
            <option value="SINT_TRUIDEN">SINT_TRUIDEN</option>
            <option value="BEAUVECHAIN">BEAUVECHAIN</option>
            <option value="JODOIGNE">JODOIGNE</option>
        </select>
    </div>

    <div id="camping">
        <h2>Camping</h2>
        <label for="startDate">Start Date:</label>
        <input type="date" id="startDate" name="startDate"><br><br>

        <label for="endDate">End Date:</label>
        <input type="date" id="endDate" name="endDate"><br><br>

        <label for="package">Package:</label>
        <select id="package" name="package">
            <option value="TENT">TENT</option>
            <option value="CAMPER">CAMPER</option>
            <option value="HOTEL">HOTEL</option>
        </select>
    </div>

    <div id="festival">
        <h2>Festival</h2>
        <label for="festivalType">Type:</label>
        <select id="festivalType" name="festivalType">
            <option value="COMBI">COMBI</option>
            <option value="FRIDAY">FRIDAY</option>
            <option value="SATURDAY">SATURDAY</option>
            <option value="SUNDAY">SUNDAY</option>
        </select>
    </div>
    <div class='login-button' id='btnAsk'>Ask</div>`;

  document.getElementById("contentdiv").innerHTML = (htmlContent);

  var ask = document.getElementById("btnAsk");
  ask.addEventListener("click", function () {
    var busDate = document.getElementById("busDatetime").value;
    var roundTrip = document.getElementById("roundtrip").value;
    var pickup = document.getElementById("pickupPlace").value;
    var startDate = document.getElementById("startDate").value;
    var endDate = document.getElementById("endDate").value;
    var packageFestival = document.getElementById("package").value;
    var festivalType = document.getElementById("festivalType").value;


    fetch(`/api/request/${busDate}/${roundTrip}/${pickup}/${startDate}/${endDate}/${packageFestival}/${festivalType}`, {
    headers: { Authorization: `Bearer ${token}` }
    })
    .then((response) => {
      console.log(response.text());
    })
    .then((data) => {

      console.log(data);
    })
    .catch(function (error) {
      console.log(error);
    });

    })
  }

// calling /api/hello on the rest service to illustrate text based data retrieval
function getHello(token) {

  fetch('/api/hello', {
    headers: { Authorization: 'Bearer {token}' }
  })
    .then((response) => {
      return response.text();
    })
    .then((data) => {

      console.log(data);
      addContent(data);
    })
    .catch(function (error) {
      console.log(error);
    });


}
// calling /api/whoami on the rest service to illustrate JSON based data retrieval
function whoami(token) {

  fetch('/api/whoami', {
    headers: { Authorization: 'Bearer ' + token }
  })
    .then((response) => {
      return response.json();
    })
    .then((data) => {
      console.log(data.email + data.role);
      addContent("Whoami at rest service: " + data.email + " - " + data.role);

    })
    .catch(function (error) {
      console.log(error);
    });


}

