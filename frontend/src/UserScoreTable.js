import React from 'react';
import { Table } from 'react-bootstrap';

function UserScoreTable(props) {
  const users = props.users.map((user, index) => {
    return (
      <tr key={index}>
        <td>{user.username}</td>
        <td>{user.score}</td>
        <td>{user.trophies}</td>
      </tr>
    );
  });

  return (
    <Table responsive hover>
      <thead>
        <tr>
          <th>Username</th>
          <th>Score</th>
          <th>Trophies</th>
        </tr>
      </thead>
      <tbody>{users}</tbody>
    </Table>
  );
}

export default UserScoreTable;
