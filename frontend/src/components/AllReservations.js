import React, { useState, useEffect } from 'react';
import { Card, Table, Alert, Spinner, Badge, Form, Row, Col } from 'react-bootstrap';
import { reservationAPI } from '../services/api';

const AllReservations = () => {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [filterRoom, setFilterRoom] = useState('');

  useEffect(() => {
    fetchAllReservations();
  }, []);

  const fetchAllReservations = async () => {
    try {
      const response = await reservationAPI.getAllActiveReservations();
      setReservations(response.data);
    } catch (err) {
      setError('Failed to load reservations');
    }
    setLoading(false);
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

  // Filter reservations based on selected filters
  const filteredReservations = reservations.filter(reservation => {
    const matchesStatus = filterStatus === '' || reservation.status === filterStatus;
    const matchesRoom = filterRoom === '' || reservation.room.name.toLowerCase().includes(filterRoom.toLowerCase());
    return matchesStatus && matchesRoom;
  });

  // Get unique rooms for filter dropdown
  const uniqueRooms = [...new Set(reservations.map(r => r.room.name))];

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
        <div>
          <h2>All Guest Bookings</h2>
          <p className="text-muted">See when other guests have booked rooms - view all current and upcoming reservations</p>
        </div>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}

      {/* Filters */}
      <Card className="mb-4">
        <Card.Body>
          <h6>Filters</h6>
          <Row>
            <Col md={4}>
              <Form.Group>
                <Form.Label>Status</Form.Label>
                <Form.Select
                  value={filterStatus}
                  onChange={(e) => setFilterStatus(e.target.value)}
                >
                  <option value="">All Statuses</option>
                  <option value="PENDING">Pending</option>
                  <option value="CONFIRMED">Confirmed</option>
                  <option value="CANCELLED">Cancelled</option>
                  <option value="COMPLETED">Completed</option>
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={4}>
              <Form.Group>
                <Form.Label>Room</Form.Label>
                <Form.Control
                  type="text"
                  placeholder="Search by room name..."
                  value={filterRoom}
                  onChange={(e) => setFilterRoom(e.target.value)}
                />
              </Form.Group>
            </Col>
            <Col md={4} className="d-flex align-items-end">
              <div className="text-muted small">
                Showing {filteredReservations.length} of {reservations.length} reservations
              </div>
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {filteredReservations.length === 0 ? (
        <Card>
          <Card.Body className="text-center py-5">
            <h4>No reservations found</h4>
            <p className="text-muted">
              {reservations.length === 0 
                ? "There are no active reservations at the moment." 
                : "No reservations match your current filters."}
            </p>
          </Card.Body>
        </Card>
      ) : (
        <Card>
          <Card.Body className="p-0">
            <Table responsive hover className="mb-0">
              <thead className="table-light">
                <tr>
                  <th>Reservation #</th>
                  <th>Guest</th>
                  <th>Room</th>
                  <th>Check-in</th>
                  <th>Check-out</th>
                  <th>Nights</th>
                  <th>Guests</th>
                  <th>Status</th>
                  <th>Booked On</th>
                </tr>
              </thead>
              <tbody>
                {filteredReservations.map(reservation => {
                  const nights = Math.ceil(
                    (new Date(reservation.checkOutDate) - new Date(reservation.checkInDate)) / 
                    (1000 * 60 * 60 * 24)
                  );
                  
                  return (
                    <tr key={reservation.id}>
                      <td><strong>#{reservation.id}</strong></td>
                      <td>
                        <div>
                          <strong>{reservation.user.firstName} {reservation.user.lastName}</strong>
                          <br />
                          <small className="text-muted">{reservation.user.username}</small>
                        </div>
                      </td>
                      <td>{reservation.room.name}</td>
                      <td>{formatDate(reservation.checkInDate)}</td>
                      <td>{formatDate(reservation.checkOutDate)}</td>
                      <td>{nights}</td>
                      <td>{reservation.numberOfGuests}</td>
                      <td>{getStatusBadge(reservation.status)}</td>
                      <td>{formatDate(reservation.createdAt)}</td>
                    </tr>
                  );
                })}
              </tbody>
            </Table>
          </Card.Body>
        </Card>
      )}

      {/* Summary Card */}
      {filteredReservations.length > 0 && (
        <Card className="mt-4">
          <Card.Body>
            <h6>Summary</h6>
            <Row>
              <Col md={3}>
                <div className="text-center">
                  <div className="h4 text-primary mb-0">
                    {filteredReservations.filter(r => r.status === 'CONFIRMED').length}
                  </div>
                  <small className="text-muted">Confirmed</small>
                </div>
              </Col>
              <Col md={3}>
                <div className="text-center">
                  <div className="h4 text-warning mb-0">
                    {filteredReservations.filter(r => r.status === 'PENDING').length}
                  </div>
                  <small className="text-muted">Pending</small>
                </div>
              </Col>
              <Col md={3}>
                <div className="text-center">
                  <div className="h4 text-success mb-0">
                    Free Service!
                  </div>
                  <small className="text-muted">No Revenue (Free)</small>
                </div>
              </Col>
              <Col md={3}>
                <div className="text-center">
                  <div className="h4 text-info mb-0">
                    {filteredReservations
                      .filter(r => ['CONFIRMED', 'PENDING'].includes(r.status))
                      .reduce((sum, r) => {
                        const nights = Math.ceil(
                          (new Date(r.checkOutDate) - new Date(r.checkInDate)) / 
                          (1000 * 60 * 60 * 24)
                        );
                        return sum + nights;
                      }, 0)}
                  </div>
                  <small className="text-muted">Total Nights</small>
                </div>
              </Col>
            </Row>
          </Card.Body>
        </Card>
      )}
    </div>
  );
};

export default AllReservations;
