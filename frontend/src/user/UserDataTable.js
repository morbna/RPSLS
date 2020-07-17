import React from 'react';
import { Table } from 'react-bootstrap';
import UserModal from './UserModal';

function UserDataTable(props) {
  const users = props.users.map((user, index) => {
    return (
      <tr key={index}>
        <td>{user.userId.domain}</td>
        <td>{user.userId.email}</td>
        <td>{user.role}</td>
        <td>{user.username}</td>
        <td>{user.avatar}</td>
        <td>
          <div style={{ width: '100px' }}>
            <UserModal label='Update' user={user}></UserModal>
          </div>
        </td>
      </tr>
    );
  });

  return (
    <Table responsive hover>
      <thead>
        <tr>
          <th>Domain</th>
          <th>Email</th>
          <th>Role</th>
          <th>Username</th>
          <th>Avatar</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>{users}</tbody>
    </Table>
  );
}

export default UserDataTable;
