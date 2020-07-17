import React from 'react';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';

import { Switch, Route, NavLink, useHistory } from 'react-router-dom';
import { CSSTransition, TransitionGroup } from 'react-transition-group';
import Home from './Home';
import Login from './Login';
import Users from './Users';
import Map from './Map/Map';
import useLocalState from './Hooks/useLocalState';
import Scoreboard from './Scoreboard';

export default function App() {
  const history = useHistory();

  document.title = 'Game';

  const initState = {
    isLogged: false,
    user: {
      userId: {
        domain: null,
        email: null,
      },
      username: null,
      role: null,
      avatar: null,
    },
    playerId: {
      domain: null,
      id: null,
    },
  };

  const [userState, setUserState] = useLocalState('user', initState);

  function logout() {
    setUserState(initState);
    history.push('/');
  }

  return (
    <div className='App'>
      <div className='nav'>
        <h1 style={{ fontSize: 15, color: 'white' }}>
          {userState.isLogged && (
            <>
              <p>{userState.user.userId.email}</p>
              <button onClick={logout}>Logout</button>
            </>
          )}
        </h1>
        <NavLink exact to='/' activeClassName='active'>
          Home
        </NavLink>
        <NavLink to='/login' activeClassName='active'>
          Login
        </NavLink>
        {userState.user.role === 'PLAYER' && (
          <>
            <NavLink to='/map' activeClassName='active'>
              Map
            </NavLink>
            <NavLink to='/scoreboard' activeClassName='active'>
              Scoreboard
            </NavLink>
          </>
        )}
        {userState.user.role === 'ADMIN' && (
          <NavLink to='/users' activeClassName='active'>
            Users
          </NavLink>
        )}
      </div>
      <Route
        render={({ location }) => (
          <TransitionGroup>
            <CSSTransition key={location.key} timeout={450} classNames='fade'>
              <Switch location={location}>
                <Route exact path='/' component={Home} />
                <Route
                  path='/login'
                  render={() => (
                    <Login userState={userState} setUserState={setUserState} />
                  )}
                />
                {userState.user.role === 'PLAYER' && (
                  <>
                    <Route
                      path='/map'
                      render={() => (
                        <Map
                          userState={userState}
                          setUserState={setUserState}
                        />
                      )}
                    />
                    <Route
                      path='/scoreboard'
                      render={() => <Scoreboard userState={userState} />}
                    />
                  </>
                )}
                {userState.user.role === 'ADMIN' && (
                  <Route
                    path='/users'
                    render={() => (
                      <Users
                        userState={userState}
                        setUserState={setUserState}
                      />
                    )}
                  />
                )}
              </Switch>
            </CSSTransition>
          </TransitionGroup>
        )}
      />{' '}
    </div>
  );
}
