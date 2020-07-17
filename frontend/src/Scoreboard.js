import { Container, Row, Col } from 'react-bootstrap';
import React, { useState, useEffect } from 'react';
import invokeAction from './API/invokeAction';
import UserScoreTable from './UserScoreTable';

class Scoreboard extends React.Component {
  constructor(props) {
    super(props);
    this._isMounted = false;
    this.id = 'GAME_LOGIC';
    this.email = props.userState.user.userId.email;
    this.state = {
      users: [],
    };
  }

  loadScores() {
    this._isMounted &&
      invokeAction('PLAYER_SCORE', this.id, this.email)
        .then((reponse) => reponse.json())
        .then((records) => this.setState({ users: records }))
        .then(console.log('loaded scores'))
        .catch((e) => this.setState({ users: [] }));
  }

  componentDidMount() {
    this._isMounted = true;
    this.loadScores();
  }
  componentWillUnmount() {
    this._isMounted = false;
  }

  render() {
    return (
      <Container className='Scores'>
        <Row>
          <Col>
            <h1 style={{ margin: '25px 0' }}>Scoreboard</h1>
          </Col>
        </Row>
        <Row>
          <Col>
            <UserScoreTable users={this.state.users} />
          </Col>
        </Row>
      </Container>
    );
  }
}

export default Scoreboard;
