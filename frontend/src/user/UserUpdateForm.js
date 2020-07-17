import React, { useState } from 'react';
import { Button, Form, FormGroup } from 'react-bootstrap';

export default function UserUpdateForm(props) {
  const [role, setRole] = useState(props.userState.user.role);
  const [username, setUsername] = useState(props.userState.user.username);
  const [avatar, setAvatar] = useState(props.userState.user.avatar);

  function submit(e) {
    console.log('submit');
    if (!props.administrative) e.preventDefault();
    fetch(
      'http://localhost:3000/acs/users/' +
        props.userState.user.userId.domain +
        '/' +
        props.userState.user.userId.email,
      {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          role: role,
          username: username,
          avatar: avatar,
        }),
      }
    ).then(() => {
      if (!props.administrative)
        props.setUserState({
          ...props.userState,
          user: {
            ...props.userState.user,
            role: role,
            username: username,
            avatar: avatar,
          },
        });
    });
  }

  return (
    <Form onSubmit={submit}>
      <FormGroup>
        <Form.Label>Role</Form.Label>
        <Form.Control
          as='select'
          type='text'
          name='role'
          id='role'
          onChange={(e) => e && setRole(e.target.value)}
          value={role}>
          <option>PLAYER</option>
          <option>ADMIN</option>
          <option>MANAGER</option>
        </Form.Control>
      </FormGroup>
      <FormGroup>
        <Form.Label>Username</Form.Label>
        <Form.Control
          type='text'
          name='username'
          id='username'
          onChange={(e) => e && setUsername(e.target.value)}
          value={username}
        />
      </FormGroup>
      <FormGroup>
        <Form.Label>Avatar</Form.Label>
        <Form.Control
          type='text'
          name='avatar'
          id='avatar'
          onChange={(e) => e && setAvatar(e.target.value)}
          value={avatar}
        />
      </FormGroup>
      <Button block variant='primary' type='submit'>
        Update
      </Button>
    </Form>
  );
}
