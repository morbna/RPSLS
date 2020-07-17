import React, { useRef, useState, useEffect } from "react";
import { Button } from "react-bootstrap";
import ReactMapGL, { Marker, Popup } from "react-map-gl";
import Shapes from "./Shapes";
import useInterval from "../Hooks/useInterval";
import "mapbox-gl/dist/mapbox-gl.css";
import player_icon from "../media/player_icon.svg";
import invokeAction from "../API/invokeAction";
import getElement from "../API/getElement";

export default function Map(props) {
  const id = "GAME_LOGIC";
  const email = props.userState.user.userId.email;
  const UPDATE_RATE = 2500;

  const firstRender = useRef(true);
  const [data, setData] = useState({ records: [] });
  const [viewport, setViewport] = useState({
    latitude: 32.1,
    longitude: 34.85,
    width: "100vw",
    height: "100vh",
    zoom: 10,
  });

  const [playerLocation, setPlayerLocation] = useState(null);
  const [selectedPlayer, setSelectedPlayer] = useState(null); // opponent element

  const [battle, setBattle] = useState(null); // battle attributes
  const [initiated, setInitiated] = useState(false); // if i initiated
  const [challenged, setChallenged] = useState(false); // if i was challenged
  const [busy, setBusy] = useState(false); // if opponent busy
  const [rejected, setRejected] = useState(false); // if opponent rejected
  const [loading, setLoading] = useState(false); // battle starting
  const [started, setStarted] = useState(false); // if battle started

  const [title, setTitle] = useState(null);
  const [closeable, setClosable] = useState(true);

  // update Map players from server
  const loadPlayers = () => {
    invokeAction("MAP_LOAD", id, email, {
      lat: playerLocation.lat,
      lng: playerLocation.lng,
      distance: 5,
    })
      .then((reponse) => reponse.json())
      .then((records) => setData({ records }))
      // .then(console.log('updating map'))
      .catch((e) => console.log("failed to update map"));
  };

  // update server on geolocation change
  useEffect(() => {
    if (playerLocation == null) return;
    const notifyLocation = () => {
      // console.log('notifying location');
      invokeAction("MAP_EVENT", id, email, {
        lat: playerLocation.lat,
        lng: playerLocation.lng,
        id: props.userState.playerId.id,
      })
        .then((response) => {
          if (!response.ok) return Promise.reject();
        })
        .catch((e) => console.log("failed to notifyLocation"));
    };

    notifyLocation();

    if (firstRender.current) {
      setViewport({
        latitude: playerLocation.lat,
        longitude: playerLocation.lng,
        width: "100vw",
        height: "100vh",
        zoom: 15,
      });
      firstRender.current = false;
    }
  }, [playerLocation]);

  // get brwoser geolocation data
  const updateLocation = () => {
    navigator.geolocation.getCurrentPosition(function (location) {
      setPlayerLocation({
        lat: location.coords.latitude,
        lng: location.coords.longitude,
      });
    });
  };

  // update geolocation and get current map users from server
  useInterval(() => {
    updateLocation();
    if (playerLocation) loadPlayers();
  }, 2000);

  // ask server for any challenges
  const askForChallenge = () => {
    (!battle || initiated) &&
      invokeAction("CHALLENGE_ASK", id, email, {
        myId: props.userState.playerId.id,
      })
        .then((reponse) => reponse.json())
        .then((response) => {
          if (response.askResponse) {
            if (response.status === "REJECTED") {
              console.log("rejected");
              setRejected(true);
            } else {
              console.log("found battle!");
              setBattle({
                battleId: response.battleId,
                otherId: response.otherId,
                status: response.status,
              });

              if (!initiated) {
                console.log("challenged!");
                // set opponent
                getElement(email, response.otherId)
                  .then((response) => {
                    return response.json();
                  })
                  .then((opponent) => {
                    setSelectedPlayer(opponent);
                    setChallenged(true);
                  });
              }
            }
          }
        });
  };

  useEffect(() => {
    console.log(battle);
    if (battle) {
      if (battle.status === "PERI") {
        console.log("battle to PERI");
        setLoading(true);
      }
    } else {
      console.log("battle to null");
      setInitiated(false);
      setChallenged(false);
      setRejected(false);
      setBusy(false);
      setLoading(false);
      setStarted(false);
      setSelectedPlayer(null);
    }
  }, [battle]);

  useInterval(() => {
    if (!loading && !rejected) askForChallenge();
  }, UPDATE_RATE);

  //
  //

  // show reject msg for some time
  useEffect(() => {
    rejected &&
      setTimeout(() => {
        setBattle(false);
      }, 3000);
  }, [rejected]);

  // disable closing when abttle started
  useEffect(() => {
    setClosable(started ? false : true);
  }, [started]);

  // show loading msg for some time
  useEffect(() => {
    loading &&
      setTimeout(() => {
        setStarted(true);
      }, 3000);
  }, [loading]);

  const challengePlayer = () => {
    console.log("challengePlayer");
    setInitiated(true);
    invokeAction("CHALLENGE_PLAYER", id, email, {
      myId: props.userState.playerId.id,
      otherId: selectedPlayer.elementId.id,
    })
      .then((response) => response.json())
      .then((available) => {
        if (!available) setBusy(true);
      });
  };

  // show busy msg for some time
  useEffect(() => {
    if (busy) {
      console.log("battle");
      setTimeout(() => {
        setInitiated(false);
        setBusy(false);
        setBattle(false);
      }, 3000);
    }
  }, [busy]);

  const accept = () => {
    battle &&
      invokeAction("CHALLENGE_REPLY", id, email, {
        battleId: battle.battleId,
        replyResponse: true,
      }).then(setLoading(true));
  };
  const reject = () => {
    battle &&
      invokeAction("CHALLENGE_REPLY", id, email, {
        battleId: battle.battleId,
        replyResponse: false,
      });
    setBattle(null);
    setSelectedPlayer(null);
    setChallenged(false);
  };

  useEffect(() => {
    if (selectedPlayer) setTitle("Name:  " + selectedPlayer.name);
  }, [selectedPlayer]);

  return (
    <div>
      <ReactMapGL
        {...viewport}
        mapStyle="mapbox://styles/mapbox/dark-v10"
        mapboxApiAccessToken={""}
        onViewportChange={(viewport) => {
          setViewport(viewport);
        }}
      >
        {data.records.length > 0
          ? data.records.map((playerElement) => (
              <Marker
                key={playerElement.elementId.id}
                latitude={playerElement.location.lat}
                longitude={playerElement.location.lng}
                className={
                  playerElement.elementId.id === props.userState.playerId.id
                    ? "player-marker-me"
                    : "player-marker"
                }
              >
                <button
                  className="marker-btn"
                  onClick={(e) => {
                    e.preventDefault();
                    setSelectedPlayer(playerElement);
                  }}
                >
                  <img src={player_icon} alt="player" />
                </button>
              </Marker>
            ))
          : null}
        {selectedPlayer && (
          <Popup
            closeButton={closeable}
            closeOnClick={false}
            latitude={selectedPlayer.location.lat}
            longitude={selectedPlayer.location.lng}
            onClose={() => {
              setSelectedPlayer(null);
            }}
          >
            <div>
              <h6 style={{ padding: 10 }}>{title}</h6>
            </div>
            {initiated ? (
              busy ? (
                <p>Opponent Unavilable...</p>
              ) : loading ? (
                started ? (
                  battle && (
                    <Shapes
                      setTitle={setTitle}
                      id={id}
                      email={email}
                      battleId={battle.battleId}
                      myId={props.userState.playerId.id}
                      setBattle={setBattle}
                    />
                  )
                ) : (
                  <div>
                    <p>Opponent Accepted!</p>
                    <p>Loading...</p>
                  </div>
                )
              ) : rejected ? (
                <p>Opponent Rejected...</p>
              ) : (
                <p>Waiting for Response...</p>
              )
            ) : challenged ? (
              loading ? (
                started ? (
                  battle && (
                    <Shapes
                      setTitle={setTitle}
                      id={id}
                      email={email}
                      battleId={battle.battleId}
                      myId={props.userState.playerId.id}
                      setBattle={setBattle}
                    />
                  )
                ) : (
                  <p>Loading...</p>
                )
              ) : (
                <div>
                  <p>You Were Challenged!</p>

                  <Button style={{ margin: 5 }} variant="dark" onClick={accept}>
                    Accept
                  </Button>
                  <Button style={{ margin: 5 }} variant="dark" onClick={reject}>
                    Reject
                  </Button>
                </div>
              )
            ) : (
              selectedPlayer.elementId.id !== props.userState.playerId.id && (
                <Button variant="dark" onClick={challengePlayer}>
                  Challenge
                </Button>
              )
            )}
          </Popup>
        )}
      </ReactMapGL>
    </div>
  );
}
