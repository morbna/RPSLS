import React from 'react';
import { Button, Form, FormGroup } from 'react-bootstrap';

class UserCreateForm extends React.Component {
  state = {
    email: '',
    role: 'PLAYER',
    username: '',
    avatar: '',
  };

  handleUpdate = (e) => {
    this.setState({ [e.target.name]: e.target.value });
  };

  submit = (e) => {
    fetch('http://localhost:3000/acs/users/', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        email: this.state.email,
        role: this.state.role,
        username: this.state.username,
        avatar: this.state.avatar,
      }),
    });
  };

  render() {
    return (
      <Form onSubmit={this.submit}>
        <FormGroup>
          <Form.Label>Email</Form.Label>
          <Form.Control
            required
            type='email'
            name='email'
            id='email'
            onChange={this.handleUpdate}
            value={this.state.role === null ? '' : this.state.email}
          />
        </FormGroup>
        <FormGroup>
          <Form.Label>Role</Form.Label>
          <Form.Control
            required
            as='select'
            type='text'
            name='role'
            id='role'
            onChange={this.handleUpdate}
            value={this.state.role === null ? '' : this.state.role}>
            <option>PLAYER</option>
            <option>ADMIN</option>
            <option>MANAGER</option>
          </Form.Control>
        </FormGroup>
        <FormGroup>
          <Form.Label>Username</Form.Label>
          <Form.Control
            required
            type='text'
            name='username'
            id='username'
            onChange={this.handleUpdate}
            value={this.state.username === null ? '' : this.state.username}
          />
        </FormGroup>
        <FormGroup>
          <Form.Label>Avatar</Form.Label>
          <Form.Control
            required
            type='text'
            name='avatar'
            id='avatar'
            onChange={this.handleUpdate}
            value={this.state.avatar === null ? '' : this.state.avatar}
          />
        </FormGroup>
        <Button type='submit'>Submit</Button>
      </Form>
    );
  }
}

export default UserCreateForm;
