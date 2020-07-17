import React, { useState, useEffect } from 'react';
import { Button } from 'react-bootstrap';
import invokeAction from '../API/invokeAction';
import useInterval from '../Hooks/useInterval';

import rock from '../media/rock.svg';
import paper from '../media/paper.svg';
import scissors from '../media/scissors.svg';
import lizard from '../media/lizard.svg';
import spock from '../media/spock.svg';

import rockC from '../media/rockC.svg';
import paperC from '../media/paperC.svg';
import scissorsC from '../media/scissorsC.svg';
import lizardC from '../media/lizardC.svg';
import spockC from '../media/spockC.svg';

export default function Shapes(props) {
  const [myShape, setMyShape] = useState();
  const [otherShape, setOtherShape] = useState();
  const [confirm, setConfirm] = useState(false);
  const [ready, setReady] = useState(false);
  const [replyRead, setReplyRead] = useState(false);

  const [wins, setWins] = useState(0);
  const [loses, setLoses] = useState(0);

  const [round, setRound] = useState(1);
  const [roundResult, setRoundResult] = useState();
  const [phrase, setPhrase] = useState("Waiting for opponent's choice...");
  const [over, setOver] = useState(false);
  const [winner, setWinner] = useState(-1);

  useEffect(() => {
    confirm && battleAction();
  }, [confirm]);

  // cleanup
  useEffect(() => {
    if (winner >= 0)
      setTimeout(() => {
        props.setBattle(null);
      }, 5000);
  }, [winner]);

  useEffect(() => {
    if (roundResult) {
      roundResult === 1 && setWins(wins + 1);
      roundResult === -1 && setLoses(loses + 1);
    }
  }, [roundResult]);

  useEffect(() => {
    props.setTitle(
      'Wins: ' +
        wins +
        ' ---------------- Round: ' +
        round +
        ' ---------------- Losses: ' +
        loses
    );
  }, [wins, loses]);

  // check for round reply
  useInterval(() => {
    confirm && battleReply();
  }, 1000);

  const battleAction = () => {
    console.log('battleAction');
    invokeAction('BATTLE_ACTION', props.id, props.email, {
      myId: props.myId,
      battleId: props.battleId,
      shape: myShape,
    });
  };

  const battleReply = () => {
    console.log('battleReply');
    invokeAction('BATTLE_REPLY', props.id, props.email, {
      round: round,
      myId: props.myId,
      battleId: props.battleId,
    })
      .then((reponse) => reponse.json())
      .then((reply) => {
        if (reply.ready && !replyRead) {
          console.log('battleReply::GOT');
          setReplyRead(true);
          setOtherShape(reply.otherShape);
          setRoundResult(reply.result);
          setPhrase(reply.phrase);
          setReady(true);

          setTimeout(() => {
            if (reply.over) {
              setOver(reply.over);
              setWinner(reply.winner);
            } else {
              console.log('next');
              // next round
              setRound(round + 1);
              setRoundResult(null);
              setPhrase("Waiting for opponent's choice...");
              setOtherShape(null);
              setConfirm(false);
              setReady(false);
              setReplyRead(false);
            }
          }, 5000);
        }
      });
  };

  const chooseRock = () => {
    setMyShape('ROCK');
  };
  const choosePaper = () => {
    setMyShape('PAPER');
  };
  const chooseScissors = () => {
    setMyShape('SCISSORS');
  };
  const chooseLizard = () => {
    setMyShape('LIZARD');
  };
  const chooseSpock = () => {
    setMyShape('SPOCK');
  };

  const getShapeImg = (shape) => {
    var s;
    if (shape === 'ROCK') s = rockC;
    if (shape === 'PAPER') s = paperC;
    if (shape === 'SCISSORS') s = scissorsC;
    if (shape === 'LIZARD') s = lizardC;
    if (shape === 'SPOCK') s = spockC;

    return <img src={s} className='shape-icon' alt='shape' />;
  };

  return confirm ? (
    ready ? (
      over ? (
        <div>You {winner ? <p>Win!</p> : <p>Lose!</p>}</div>
      ) : (
        <div className='shape-panel'>
          <div className='shape-chosen'>
            {getShapeImg(otherShape)}
            <p>{[phrase]}</p>
            {getShapeImg(myShape)}
          </div>
        </div>
      )
    ) : (
      <div className='shape-panel'>
        <div className='shape-chosen'>
          <p>{[phrase]}</p>
          {getShapeImg(myShape)}
        </div>
      </div>
    )
  ) : (
    <div>
      <div className='shape-panel'>
        <div className='shape-choice'>
          <p>Rock</p>
          {myShape === 'ROCK' ? (
            <img src={rockC} className='shape-icon' alt='shape' />
          ) : (
            <img src={rock} className='shape-icon' alt='shape' />
          )}
          <Button variant='outline-dark' onClick={chooseRock}>
            Select
          </Button>
        </div>
        <div className='shape-choice'>
          <p>Paper</p>
          {myShape === 'PAPER' ? (
            <img src={paperC} className='shape-icon' alt='shape' />
          ) : (
            <img src={paper} className='shape-icon' alt='shape' />
          )}{' '}
          <Button variant='outline-dark' onClick={choosePaper}>
            Select
          </Button>
        </div>
        <div className='shape-choice'>
          <p>Scissors</p>
          {myShape === 'SCISSORS' ? (
            <img src={scissorsC} className='shape-icon' alt='shape' />
          ) : (
            <img src={scissors} className='shape-icon' alt='shape' />
          )}{' '}
          <Button variant='outline-dark' onClick={chooseScissors}>
            Select
          </Button>
        </div>
        <div className='shape-choice'>
          <p>Lizard</p>
          {myShape === 'LIZARD' ? (
            <img src={lizardC} className='shape-icon' alt='shape' />
          ) : (
            <img src={lizard} className='shape-icon' alt='shape' />
          )}{' '}
          <Button variant='outline-dark' onClick={chooseLizard}>
            Select
          </Button>
        </div>
        <div className='shape-choice'>
          <p>Spock</p>
          {myShape === 'SPOCK' ? (
            <img src={spockC} className='shape-icon' alt='shape' />
          ) : (
            <img src={spock} className='shape-icon' alt='shape' />
          )}{' '}
          <Button variant='outline-dark' onClick={chooseSpock}>
            Select
          </Button>
        </div>
      </div>
      {myShape && (
        <div style={{ paddingTop: '2vmin' }}>
          <Button onClick={setConfirm} variant='outline-info' block>
            Confirm
          </Button>
        </div>
      )}
    </div>
  );
}
