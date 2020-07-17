import React from 'react';
import { Button, Modal } from 'react-bootstrap';
import UserCreateForm from './UserCreateForm';
import UserUpdateForm from './UserUpdateForm';

class UserModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isOpen: false,
    };
  }

  handleClose = () => {
    this.setState({ isOpen: false });
  };

  handleShow = () => this.setState({ isOpen: true });

  render() {
    let title;
    let color;

    switch (this.props.label) {
      case 'Create':
        color = 'success';
        title = 'Create User';
        break;
      case 'Update':
        color = 'warning';
        title = 'Update User';
        break;
      default:
        break;
    }

    const button = (
      <Button variant={color} onClick={this.handleShow}>
        {this.props.label}
      </Button>
    );

    return (
      <div>
        {button}
        <Modal
          show={this.state.isOpen}
          className={this.props.className}
          onHide={this.handleClose}>
          <Modal.Header closeButton>{title}</Modal.Header>
          <Modal.Body>
            {(() => {
              switch (this.props.label) {
                case 'Create':
                  return <UserCreateForm />;
                case 'Update':
                  return (
                    <UserUpdateForm
                      administrative={true}
                      userState={{ user: this.props.user }}
                    />
                  );
                default:
                  return;
              }
            })()}
          </Modal.Body>
        </Modal>
      </div>
    );
  }
}

export default UserModal;
