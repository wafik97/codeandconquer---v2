   // Minimal changes: use native WebSocket instead of SockJS+STOMP
    let socket = null;
    let playerName = "";
    let playerRole = ""; // "player" or "spectator"
    let selectedRoom = "";
    let playerColor = "";
    let gameStarted = false;
    let rooms = ["Room1", "Room2", "Room3", "Room4"]; // example rooms
    let playersInRooms = {}; // { roomName: number of players }
    let connected = "first";
    let selectedCellId = null;
    let codeAnswer = "hello world";
    let currentPuzzle = { question: "What is 2 + 2?", answer: "4" }; // example puzzle
    const JUDGE0_URL = `http://${window.location.hostname}:2358`;

    const starterJavaCode =
    `     import java.util.*;


          public class Main {
             public static void main(String[] args) {
              // Write your code here
             }
          }`;




// ----- ROLE & ROOM SELECTION -----
// keep
window.onload = function() {

    connectWebSocket();

    document.getElementById('roleModal').style.display = "block";

    document.getElementById('playerButton').addEventListener('click', function() {
        playerRole = "player";
        showRoomSelection();
         backButton.style.display = 'block';
    });

    document.getElementById('spectatorButton').addEventListener('click', function() {
        playerRole = "spectator";
        showRoomSelection();
         backButton.style.display = 'block';
    });
};


function connectWebSocket() {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const host = window.location.host;
    socket = new WebSocket(`${protocol}//${host}/game`);

    socket.onopen = () => {
        if (connected === "first") {
            connected = "connected";
        }
        if (connected === "lost") {
            window.location.reload();
        }
        console.log("✅ Connected to WebSocket");
        const statusDiv = document.getElementById('status');
        statusDiv.innerHTML = "Connected";
    };

    socket.onmessage = (event) => {
        try {
            const data = JSON.parse(event.data);
            console.log("📩 Received:", data);
            handleServerMessage(data);
        } catch (e) {
            console.error("Invalid message from server:", event.data);
        }
    };

    socket.onclose = (ev) => {
        console.error("❌ WebSocket closed:", ev);
        const statusDiv = document.getElementById('status');
        statusDiv.innerHTML = "Connection lost buddy...";
        connected = "lost";
        setTimeout(connectWebSocket, 3000); // Auto-reconnect
    };

    socket.onerror = (error) => {
        console.error("WebSocket error:", error);
    };
}






// keep 3
const backButton = document.getElementById('backButton');


// keep
function showRoleSelection() {
    const modalContent = document.getElementById('roleModalContent');
    modalContent.innerHTML = `<h3>Choose your role</h3>
        <button id="playerButton">Play as Player</button>
        <button id="spectatorButton">Play as Spectator</button>`;

    // Add back button at the end, hide it
    backButton.style.display = 'none';
    modalContent.appendChild(backButton);

    // Re-add event listeners for role buttons
    document.getElementById('playerButton').addEventListener('click', function() {
        playerRole = "player";
        showRoomSelection();
    });
    document.getElementById('spectatorButton').addEventListener('click', function() {
        playerRole = "spectator";
        showRoomSelection();
    });
}


// keep
backButton.addEventListener('click', function() {
    showRoleSelection();
});




// keep 2
function showRoomSelection() {
    const modalContent = document.getElementById('roleModalContent');

    // Build the inner modal HTML with room select + name input
    modalContent.innerHTML = `
        <h3>Select a Room</h3>
        <select id="roomSelect"></select>
        <br><br>

        <label for="playerName" style="color: white;">Enter your name:</label>
        <br>
        <input type="text" id="playerName" maxlength="10" placeholder="Your name..." style="margin-top: 8px;">
        <br><br>

        <button id="joinRoomButton" onclick="joinSelectedRoom()">Join Room</button>
    `;

    // Show Back button and append it after the new content
    backButton.style.display = 'block';
    modalContent.appendChild(backButton);

    // Populate the room list
    const roomSelect = document.getElementById('roomSelect');
    rooms.forEach(room => {
        let playerCount = playersInRooms[room] || 0;
        if (playerRole === "spectator" || playerCount < 2) {
            const option = document.createElement('option');
            option.value = room;
            option.text = room ;
            roomSelect.appendChild(option);
        }
    });
}



    // show modal
    //   document.getElementById('roleModal').style.display = "block";




function joinSelectedRoom() {



    selectedRoom = document.getElementById('roomSelect').value;
    playerName = document.getElementById('playerName').value.trim();

    if (!playerName) {
        alert("Please enter a name before joining!");
        return;
    }

    if (!socket || socket.readyState !== WebSocket.OPEN) {
        alert("WebSocket not connected yet. Please wait a moment.");
        return;
    }

    socket.send(JSON.stringify({
      type: "check",
      roomName: selectedRoom,
      playerName: playerName,
      role: playerRole
    }));







}





// ----- MAP & UI (kept unchanged) -----
    function initializeMap() {
        const mapDiv = document.getElementById('map');
        mapDiv.innerHTML = ''; // clear previous

        for (let i = 0; i < 25; i++) { // 5x5 grid
            const cell = document.createElement('div');
            cell.className = 'cell';
            cell.dataset.index = i;
            cell.addEventListener('click', () => claimCell(i));
            cell.innerHTML = `<span>${i + 1}</span>`; // optional cell number
            mapDiv.appendChild(cell);
        }
    }



    function updateStatus() {
        const statusDiv = document.getElementById('status');

        if (gameStarted === false) {
            if (playerRole === 'spectator') {
                statusDiv.innerHTML = "Waiting for the players to join...";
            } else if (playerRole === 'player') {
                statusDiv.innerHTML = `Welcome ${playerName}, waiting for the other player to join...`;
            }
        } else if (gameStarted === true) {
            if (playerRole === 'spectator') {
                statusDiv.innerHTML = "Game Started! Enjoy watching!!";
            } else if (playerRole === 'player') {
                statusDiv.innerHTML = `Game Started! Your color: ${playerColor}`;
            }
        }
    }


// Handle incoming server JSON messages (minimal handling)
function handleServerMessage(data) {
    // If server sends an error object
    if (data.error) {
        console.error("Server error:", data.error);
        return;
    }

    if (data.type === "player_left" && gameStarted === true) {

        surrenderModal.style.display = 'block';
        createConfetti();
        createStarBurst();

    }
    if (data.type === "player_left" && gameStarted === false) {

        const playersDiv = document.getElementById('players');
        playersDiv.innerHTML = "";

    }


     if (data.type === "check_result") {

                console.log(data.type);
                console.log(data.status);

                switch (data.status) {
                    case 'full':
                           alert("The room is full!");
                           return;
                        break;
                    case 'already':
                         alert("The name already exists!");
                           return;
                        break;
                    case 'safe':
                            // Hide modal after join
                            document.getElementById('roleModal').style.display = "none";

                            // Send join info to backend (native WebSocket)
                            const joinMessage = {
                                roomName: selectedRoom,
                                playerName: playerName,
                                role: playerRole
                            };

                            initializeMap();

                            // attach a `type` so server knows this is a join request
                            socket.send(JSON.stringify(Object.assign({type: "join_room"}, joinMessage)));
                            console.log("📤 Sent join request:", joinMessage);
                            updateStatus();
                        break;
                    default:
                        console.log('Unknown status');
                }
            }

    // Room update (players/spectators/claimedLands/gameStarted)
    if (data.type === "room_update" || data.players || data.claimedLands) {
         //console.log('do u hear me ?');
        // update players container
        const playersDiv = document.getElementById('players');
        let html = "";

        if (data.players) {
            const entries = Object.entries(data.players);
            html += "<strong>Players:</strong> " + entries.map(e => `${e[0]} (${e[1]})`).join(", ");
            // set playerColor if this client is one of the players
            if (playerName && data.players[playerName]) {
                playerColor = data.players[playerName];
            }
            playersDiv.innerHTML = html;
        }

      //  if (data.spectators) {
      //      html += "<br><strong>Spectators:</strong> " + Array.from(data.spectators).join(", ");
      // }

      //  playersDiv.innerHTML = html;

        if (typeof data.gameStarted !== 'undefined') {
            gameStarted = data.gameStarted;
        }

        // update claimed lands (if present)
        if (data.claimedLands) {
            const claimed = data.claimedLands;
            for (const key in claimed) {
                const idx = parseInt(key);
                const col = claimed[key];
                const cell = document.querySelector(`.cell[data-index='${idx}']`);
                if (cell) {
                    // minimal visual change: change border color per owner
                    if (col === null || col === undefined) {
                        cell.style.borderColor = "#00ff88";
                    } else if (col === "red") {
                        cell.style.borderColor = "#ff8888";
                    } else if (col === "blue") {
                        cell.style.borderColor = "#8888ff";
                    } else {
                        cell.style.borderColor = "#ffffff";
                    }
                }
            }
        }

        updateStatus();
    }

    // Single tile update
    if (data.type === "tile_update" && data.success) {

        const id = data.tileId;
        if(selectedCellId === id){
           document.getElementById('puzzle-panel').style.display = 'none';
        }
        const col = data.playerColor;
        const cell = document.querySelector(`.cell[data-index='${id}']`);
        if (cell) {
            if (col === null || col === undefined) {
                cell.style.borderColor = "#00ff88";
            } else if (col === "red") {
                cell.style.borderColor = "#ff8888";
            } else if (col === "blue") {
                cell.style.borderColor = "#8888ff";
            } else {
                cell.style.borderColor = "#ffffff";
            }
        }

        if(data.count_lands===25){
            phaseModal.style.display = 'block';
        }

    }
}

// keep the leftover placeholder (but now socket used)
function claimCell(i) {

    if (playerRole === "spectator") return;

    if (!gameStarted) {
        alert("⏳ The game hasn't started yet!");
        return;
    }




    const cell = document.querySelector(`.cell[data-index='${i}']`);
    const currentBorder = cell.style.borderColor;

    // Check if already claimed
    if (currentBorder && currentBorder !== 'rgb(0, 255, 136)' && currentBorder !== '') {
        alert("This cell is already conquered!");
        return;
    }

    // Save selected cell and show puzzle
    selectedCellId = i;
    showPuzzle(i);
    }

    function showPuzzle(cellIndex) {
        document.getElementById('puzzle-title').innerText = `Puzzle for cell ${cellIndex + 1}`;
        document.getElementById('puzzle-question').innerText = "Print \"hello world\"";

        document.getElementById('puzzle-panel').style.display = 'block';
        editor.setValue(starterJavaCode);
    }

    function submitAnswer() {
        //const answer = document.getElementById('puzzle-answer').value.trim();
        var answer = editor.getValue();
        if (answer === "") {
            alert("Please enter an answer.");
            return;
        }

        runUserCode(answer)
            .then(result => {
              const out = (result.stdout || result.stderr || "").trim();


              // compare output to expected answer
              if (out === String(codeAnswer).trim()) {
                // correct -> notify server to claim tile

                socket.send(JSON.stringify({
                  type: "select",
                  roomName: selectedRoom,
                  playerName: playerName,
                  tileId: selectedCellId
                }));
                document.getElementById('puzzle-panel').style.display = 'none';
                selectedCellId = null;
              } else {

        		document.getElementById('output').textContent = "❌ Wrong answer, output:\n" + out; // show to user
              }
            })
            .catch(err => {
              console.error(err);
              alert("Execution error, check console.");
            });


    }


    // Celebrate
    function createConfetti() {
        const modal = document.getElementById('surrenderModal');
        const colors = ['#00ff88', '#00ffff', '#88ff00', '#ffff00'];

        for (let i = 0; i < 50; i++) {
            setTimeout(() => {
                const confetti = document.createElement('div');
                confetti.className = 'confetti';
                confetti.style.left = Math.random() * 100 + '%';
                confetti.style.background = colors[Math.floor(Math.random() * colors.length)];
                confetti.style.animationDelay = Math.random() * 0.5 + 's';
                confetti.style.animationDuration = (Math.random() * 2 + 2) + 's';
                modal.appendChild(confetti);

                setTimeout(() => confetti.remove(), 5000);
            }, i * 50);
        }
    }

    function createStarBurst() {
        const modalContent = document.querySelector('#surrenderModal .modal-content');
        const stars = ['✨', '⭐', '🌟', '💫'];

        for (let i = 0; i < 12; i++) {
            const star = document.createElement('div');
            star.className = 'star';
            star.textContent = stars[Math.floor(Math.random() * stars.length)];

            const angle = (i / 12) * Math.PI * 2;
            const distance = 150;
            const tx = Math.cos(angle) * distance;
            const ty = Math.sin(angle) * distance;

            star.style.setProperty('--tx', tx + 'px');
            star.style.setProperty('--ty', ty + 'px');
            star.style.left = '50%';
            star.style.top = '20%';

            modalContent.appendChild(star);

            setTimeout(() => star.remove(), 1000);
        }
    }

    // Initialize CodeMirror
    var editor = CodeMirror.fromTextArea(document.getElementById('code-editor'), {
        lineNumbers: true,          // Show line numbers
        mode: "text/x-java",         // Set the mode for JavaScript (you can change this to other languages like python, html, etc.)
        theme: "dracula",           // Optional: a dark theme, you can choose others
        indentUnit: 4,              // Indentation settings
        smartIndent: true,          // Enable smart indentation
        tabSize: 4,                 // Tab size
        matchBrackets: true,        // Highlight matching brackets
        autoCloseBrackets: true,    // Auto-close brackets
        extraKeys: { "Ctrl-Space": "autocomplete" },
    });


    function runUserCode(sourceCode) {
      return fetch(`${JUDGE0_URL}/submissions?base64_encoded=false&wait=true`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          source_code: sourceCode,
          language_id: 62
        })
      }).then(r => r.json());
    }


