import { Button, Form, FormGroup, Modal } from 'react-bootstrap';
import React, { useState } from 'react';
import { useHistory } from 'react-router-dom';
import UserCreateForm from './user/UserCreateForm';
import UserUpdateForm from './user/UserUpdateForm';
import invokeAction from './API/invokeAction';

export default function Login(props) {
  const [domain] = useState('2020b.morb2');
  const [email, setEmail] = useState();
  const [register, setRegister] = useState(false);
  const [error, setError] = useState(false);

  const history = useHistory();

  const getPlayer = (user) => {
    console.log('getting player');

    invokeAction('PLAYER_GET', 'GAME_LOGIC', user.userId.email, {})
      .then((response) => response.json())
      .then((element) => {
        console.log(element);
        console.log(user);
        props.setUserState({
          isLogged: true,
          user: {
            userId: {
              domain: user.userId.domain,
              email: user.userId.email,
            },
            username: user.username,
            role: user.role,
            avatar: user.avatar,
          },
          playerId: {
            domain: element.elementId.domain,
            id: element.elementId.id,
          },
        });
      })
      .then(() => history.push('/map'))
      .catch(() => console.log('error getting player element'));
  };

  const login = (e) => {
    e.preventDefault();

    fetch('http://localhost:3000/acs/users/login/' + domain + '/' + email, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    })
      .then((response) => {
        if (!response.ok) return Promise.reject();
        return response.json();
      })
      .then((user) => {
        console.log(user);
        setError(false);
        console.log('logged in');

        props.setUserState({
          isLogged: true,
          user: {
            userId: {
              domain: user.userId.domain,
              email: user.userId.email,
            },
            username: user.username,
            role: user.role,
            avatar: user.avatar,
          },
        });

        if (user.role === 'PLAYER') getPlayer(user);
      })
      .catch((e) => {
        setError(true);
        props.setUserState({
          ...props.userState,
          isLogged: false,
        });
        console.log('login failed');
      });
  };

  function hideRegister() {
    setRegister(false);
  }
  function showRegister() {
    setRegister(true);
  }

  return (
    <div className='Login'>
      {error ? (
        <h1 style={{ color: 'red', fontSize: 22, padding: 10 }}>
          Login Failed!
        </h1>
      ) : null}

      {props.userState.isLogged ? (
        <div>
          <h3>Update Details</h3>
          <UserUpdateForm
            administrative={false}
            userState={props.userState}
            setUserState={props.setUserState}
          />
        </div>
      ) : (
        <Form onSubmit={login}>
          <FormGroup>
            <Form.Label>Email</Form.Label>
            <Form.Control
              required
              type='email'
              id='email'
              onChange={(e) => setEmail(e.target.value)}
              value={email}
            />
          </FormGroup>

          <Button block type='submit'>
            Login
          </Button>
          <Button variant={'info'} block type='button' onClick={showRegister}>
            Register
          </Button>
        </Form>
      )}

      <Modal show={register} onHide={hideRegister}>
        <Modal.Header closeButton>New User Details</Modal.Header>
        <Modal.Body>
          {(() => {
            return <UserCreateForm />;
          })()}
        </Modal.Body>
      </Modal>
    </div>
  );
}
