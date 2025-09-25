import React, { useState, useEffect } from 'react';
import { Card, Table, Button, Alert, Spinner, Badge, Modal, Form } from 'react-bootstrap';
import { reservationAPI } from '../services/api';

const MyReservations = () => {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showEditModal, setShowEditModal] = useState(false);
  const [editingReservation, setEditingReservation] = useState(null);
  const [editFormData, setEditFormData] = useState({});

  useEffect(() => {
    fetchMyReservations();
  }, []);

  const fetchMyReservations = async () => {
    try {
      const response = await reservationAPI.getMyReservations();
      setReservations(response.data);
    } catch (err) {
      setError('Failed to load reservations');
    }
    setLoading(false);
  };

  const handleCancelReservation = async (reservationId) => {
    if (!window.confirm('Are you sure you want to cancel this reservation?')) {
      return;
    }

    try {
      await reservationAPI.cancelReservation(reservationId);
      setSuccess('Reservation cancelled successfully');
      fetchMyReservations();
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError(err.response?.data || 'Failed to cancel reservation');
      setTimeout(() => setError(''), 3000);
    }
  };

  const handleEditClick = (reservation) => {
    setEditingReservation(reservation);
    setEditFormData({
      roomId: reservation.room.id,
      checkInDate: reservation.checkInDate,
      checkOutDate: reservation.checkOutDate,
      numberOfGuests: reservation.numberOfGuests,
      notes: reservation.notes || ''
    });
    setShowEditModal(true);
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    
    try {
      await reservationAPI.updateReservation(editingReservation.id, editFormData);
      setSuccess('Reservation updated successfully');
      setShowEditModal(false);
      fetchMyReservations();
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError(err.response?.data || 'Failed to update reservation');
    }
  };

  // Removed price formatting as this is now a free reservation system

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString();
  };

  const getStatusBadge = (status) => {
    const statusMap = {
      'PENDING': { variant: 'warning', text: 'Pending' },
      'CONFIRMED': { variant: 'success', text: 'Confirmed' },
      'CANCELLED': { variant: 'danger', text: 'Cancelled' },
      'COMPLETED': { variant: 'info', text: 'Completed' }
    };
    
    const statusInfo = statusMap[status] || { variant: 'secondary', text: status };
    return <Badge bg={statusInfo.variant}>{statusInfo.text}</Badge>;
  };

  const canEditOrCancel = (reservation) => {
    return ['PENDING', 'CONFIRMED'].includes(reservation.status) && 
           new Date(reservation.checkInDate) > new Date();
  };

  if (loading) {
    return (
      <div className="text-center mt-5">
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading...</span>
        </Spinner>
      </div>
    );
  }

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>My Reservations</h2>
        <Button 
          variant="primary" 
          href="/book"
        >
          New Reservation
        </Button>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}
      {success && <Alert variant="success">{success}</Alert>}

      {reservations.length === 0 ? (
        <Card>
          <Card.Body className="text-center py-5">
            <h4>No reservations found</h4>
            <p className="text-muted">You haven't made any reservations yet.</p>
            <Button variant="primary" href="/book">
              Make Your First Reservation
            </Button>
          </Card.Body>
        </Card>
      ) : (
        <Card>
          <Card.Body className="p-0">
            <Table responsive hover className="mb-0">
              <thead className="table-light">
                <tr>
                  <th>Reservation #</th>
                  <th>Room</th>
                  <th>Check-in</th>
                  <th>Check-out</th>
                  <th>Guests</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {reservations.map(reservation => (
                  <tr key={reservation.id}>
                    <td><strong>#{reservation.id}</strong></td>
                    <td>{reservation.room.name}</td>
                    <td>{formatDate(reservation.checkInDate)}</td>
                    <td>{formatDate(reservation.checkOutDate)}</td>
                    <td>{reservation.numberOfGuests}</td>
                    <td>{getStatusBadge(reservation.status)}</td>
                    <td>
                      {canEditOrCancel(reservation) && (
                        <>
                          <Button 
                            variant="outline-primary" 
                            size="sm" 
                            className="me-2"
                            onClick={() => handleEditClick(reservation)}
                          >
                            Edit
                          </Button>
                          <Button 
                            variant="outline-danger" 
                            size="sm"
                            onClick={() => handleCancelReservation(reservation.id)}
                          >
                            Cancel
                          </Button>
                        </>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </Card.Body>
        </Card>
      )}

      {/* Edit Modal */}
      <Modal show={showEditModal} onHide={() => setShowEditModal(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Edit Reservation #{editingReservation?.id}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleEditSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Check-in Date</Form.Label>
              <Form.Control
                type="date"
                value={editFormData.checkInDate || ''}
                onChange={(e) => setEditFormData({...editFormData, checkInDate: e.target.value})}
                min={new Date().toISOString().split('T')[0]}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Check-out Date</Form.Label>
              <Form.Control
                type="date"
                value={editFormData.checkOutDate || ''}
                onChange={(e) => setEditFormData({...editFormData, checkOutDate: e.target.value})}
                min={editFormData.checkInDate}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Number of Guests</Form.Label>
              <Form.Control
                type="number"
                value={editFormData.numberOfGuests || 1}
                onChange={(e) => setEditFormData({...editFormData, numberOfGuests: parseInt(e.target.value)})}
                min="1"
                max={editingReservation?.room.maxOccupancy || 10}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Notes</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={editFormData.notes || ''}
                onChange={(e) => setEditFormData({...editFormData, notes: e.target.value})}
                placeholder="Any special requests or notes..."
              />
            </Form.Group>

            <div className="d-flex justify-content-end">
              <Button variant="secondary" className="me-2" onClick={() => setShowEditModal(false)}>
                Cancel
              </Button>
              <Button variant="primary" type="submit">
                Update Reservation
              </Button>
            </div>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
};

export default MyReservations;
