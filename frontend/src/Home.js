import React, { Component } from 'react';
import logo from './media/game_logo.svg';
class Home extends Component {
  render() {
    return (
      <div className='content-home'>
        <h1>Welcome To</h1>
        <h2 className='h2-home'>Rock Paper Scissors Lizard Spock</h2>
        <img src={logo} className='App-logo' alt='logo' />
        <br />
        <br />
        <br />

        <h3>What the Spock is this?</h3>
        <p className='p-home'>
          Play a GPS based rock paper scissors lizard spock game with a friend
          over the web to break a tie, resolve a ‘debate’, or kill some time in
          quarantine.
        </p>
        <h3>Rock paper scissors lizard what now?</h3>
        <p className='p-home'>
          <i>Lizard spock</i> is a free expansion pack for the much-loved game
          of rock paper scissors. Lizard and Spock reduce the chance of a tie by
          eating, smashing, poisoning or vaporising their opponents.
        </p>
        <h3>The rules</h3>
        <p className='p-home'>
          Scissors cuts paper. Paper covers rock. Rock crushes lizard.
          <br /> Lizard poisons Spock. Spock smashes scissors. Scissors
          decapitates lizard. Lizard eats paper. Paper disproves Spock. Spock
          vaporizes rock.
          <br /> and as it always has, Rock crushes scissors.
        </p>
        <h3>How to play</h3>
        <ol className='ol-home' style={{ listStyleType: 'square' }}>
          <li>
            Head over to the login page and <b>Register</b>
          </li>
          <li>
            <b>Login,</b> and the game map will appear
          </li>
          <li>
            <b>Click</b> on a player marker to challenge them!
          </li>
        </ol>
      </div>
    );
  }
}

export default Home;
