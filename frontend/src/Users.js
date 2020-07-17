import React from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import UserDataTable from './user/UserDataTable.js';
import UserModal from './user/UserModal';

class Users extends React.Component {
  constructor(props) {
    super(props);
    this._isMounted = false;
    this.state = {
      users: [],
    };
  }

  loadUsers() {
    this._isMounted &&
      fetch(
        'http://localhost:3000/acs/admin/users/' +
          this.props.userState.user.userId.domain +
          '/' +
          this.props.userState.user.userId.email,
        {
          method: 'GET',
          headers: { 'Content-Type': 'application/json' },
        }
      )
        .then((reponse) => reponse.json())
        .then((records) => this.setState({ users: records }))
        .then(console.log('loaded users'))
        .catch((e) => this.setState({ users: [] }));
  }

  componentDidMount() {
    this._isMounted = true;
    this.loadUsers();
  }
  componentWillUnmount() {
    this._isMounted = false;
  }

  render() {
    return (
      <Container className='Users'>
        <Row>
          <Col>
            <h1 style={{ margin: '25px 0' }}>Users</h1>
          </Col>
        </Row>
        <Row>
          <Col>
            <UserDataTable users={this.state.users} />
          </Col>
        </Row>
        <Row style={{ margin: '10px 0' }}>
          <UserModal label='Create' />
        </Row>
      </Container>
    );
  }
}

export default Users;
