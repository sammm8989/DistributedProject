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

function addContent() {
    const htmlContent = `
    <div class='login-button' id='getAvailabilities'>Get Availabilities</div>`;
    document.getElementById("contentdiv").innerHTML = htmlContent;

    var available = document.getElementById("getAvailabilities");
    available.addEventListener("click", function () {
        fetch(`/broker/getAllAvailable`, {
            headers: { Authorization: 'Bearer ' + token } // Correct token usage
        })
        .then((response) => {
            return response.json(); // Parse response as JSON
        })
        .then((data) => {
            let new_text = `<div id="responseContainer">`;

            // Iterate over each key in the JSON object and create dropdowns or display values
            Object.keys(data).forEach(key => {
                if (Array.isArray(data[key])) {
                    new_text += `<div class="selection">
                                    <label for="${key}">${key}:</label>
                                    <select id="${key}">`;
                    data[key].forEach(item => {
                        // For dropdowns, we need to handle objects, so we stringify the object for the value
                        new_text += `<option value='${JSON.stringify(item)}'>${item.type || item.departure_time}</option>`;
                    });
                    new_text += `</select>
                                </div>`;
                } else {
                    new_text += `<div class="selection">
                                    <label>${key}:</label>
                                    <span>${data[key]}</span>
                                </div>`;
                }
            });

            new_text += `
                </div>
                <div class="login-button" id='chooseSelection'>Choose Selected</div>`;
            document.getElementById("contentdiv").innerHTML = new_text;

            var select = document.getElementById("chooseSelection");
            select.addEventListener("click", function () {
                const requestData = {};
                Object.keys(data).forEach(key => {
                    const element = document.getElementById(key);
                    if (element) {
                        requestData[key] = JSON.parse(element.value) || data[key];
                    } else {
                        requestData[key] = data[key];
                    }
                });

                // Construct the request URL based on the selected options
                const bus_departure_time = requestData.bus.departure_time;
                const round_trip = requestData.bus.round_trip;
                const start_place = requestData.bus.start_place;
                const camping_type = requestData.camping.type;
                const festival_type = requestData.ticket.type;

                fetch(`/broker/request/${bus_departure_time}/${round_trip}/${start_place}/${camping_type}/${festival_type}`, {
                    headers: {
                        Authorization: 'Bearer ' + token
                    }
                })
                .then((response) => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then((data) => {
                    console.log(data); // Log the response from the server

                    // Update the screen with the chosen data and price
                    let confirmation_text = `
                        <div id="confirmationContainer">
                            <h2>Selected Options</h2>
                            <div class="selection"><label>Bus Departure Time:</label><span>${bus_departure_time}</span></div>
                            <div class="selection"><label>Round Trip:</label><span>${round_trip}</span></div>
                            <div class="selection"><label>Start Place:</label><span>${start_place}</span></div>
                            <div class="selection"><label>Camping Type:</label><span>${camping_type}</span></div>
                            <div class="selection"><label>Festival Type:</label><span>${festival_type}</span></div>
                            <div class="selection"><label>Price:</label><span>${data.price}</span></div>
                            <div class="login-button" id='payNow'>Pay</div>
                            <div id="payStatus"></div>
                        </div>`;

                    document.getElementById("contentdiv").innerHTML = confirmation_text;

                    // Add event listener for the 'Pay' button
                    var payNow = document.getElementById("payNow");
                    payNow.addEventListener("click", function () {
                        fetch(`/broker/paid`, {
                            headers: {
                                Authorization: 'Bearer ' + token
                            }
                        })
                        .then((response) => {
                            if (!response.ok) {
                                throw new Error(`HTTP error! status: ${response.status}`);
                            }
                            return response.json();
                        })
                        .then((data) => {
                            console.log(data); // Log the response from the server
                            alert("Payment successful! Confirmation received.");
                            // Remove the "Pay" button after successful payment
                            payNow.style.display = 'none';
                            document.getElementById("payStatus").innerHTML = `<div class="selection"><label>Status:</label><span>Paid</span></div>`;
                        })
                        .catch(function (error) {
                            console.log(error); // Log any errors
                            alert("Payment failed. Please try again.");
                        });
                    });
                })
                .catch(function (error) {
                    console.log(error); // Log any errors
                });
            });
        })
        .catch(function (error) {
            console.log("Error fetching data:", error); // Log any errors
        });
    });
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
