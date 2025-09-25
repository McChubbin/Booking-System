import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Form, Alert, Spinner } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { roomAPI } from '../services/api';
import { useAuth } from '../contexts/AuthContext';

const Rooms = () => {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [showAvailableOnly, setShowAvailableOnly] = useState(false);
  
  const { user } = useAuth();

  useEffect(() => {
    fetchRooms();
  }, []);

  const fetchRooms = async () => {
    try {
      const response = await roomAPI.getAllRooms();
      setRooms(response.data);
    } catch (err) {
      setError('Failed to load rooms');
    }
    setLoading(false);
  };

  const handleAvailabilityCheck = async () => {
    if (!startDate || !endDate) {
      setError('Please select both start and end dates');
      return;
    }

    if (new Date(startDate) >= new Date(endDate)) {
      setError('End date must be after start date');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await roomAPI.getAvailableRooms(startDate, endDate);
      setRooms(response.data);
      setShowAvailableOnly(true);
    } catch (err) {
      setError('Failed to check availability');
    }
    
    setLoading(false);
  };

  const resetFilter = () => {
    setShowAvailableOnly(false);
    setStartDate('');
    setEndDate('');
    fetchRooms();
  };

  // Removed price formatting as this is now a free reservation system

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
      <Row className="mb-4">
        <Col>
        <h2>Available Reservations</h2>
        <p className="lead">Browse and book from our available options - all completely free!</p>
        </Col>
      </Row>

      {/* Availability Filter */}
      <Card className="mb-4">
        <Card.Body>
          <h5>Check Availability</h5>
          <Row className="align-items-end">
            <Col md={4}>
              <Form.Group>
                <Form.Label>Check-in Date</Form.Label>
                <Form.Control
                  type="date"
                  value={startDate}
                  onChange={(e) => setStartDate(e.target.value)}
                  min={new Date().toISOString().split('T')[0]}
                />
              </Form.Group>
            </Col>
            <Col md={4}>
              <Form.Group>
                <Form.Label>Check-out Date</Form.Label>
                <Form.Control
                  type="date"
                  value={endDate}
                  onChange={(e) => setEndDate(e.target.value)}
                  min={startDate || new Date().toISOString().split('T')[0]}
                />
              </Form.Group>
            </Col>
            <Col md={4}>
              <div className="d-grid gap-2 d-md-flex">
                <Button 
                  variant="primary" 
                  onClick={handleAvailabilityCheck}
                  disabled={loading}
                >
                  Check Availability
                </Button>
                {showAvailableOnly && (
                  <Button variant="outline-secondary" onClick={resetFilter}>
                    Show All
                  </Button>
                )}
              </div>
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {error && <Alert variant="danger">{error}</Alert>}

      {showAvailableOnly && (
        <Alert variant="info">
          Showing rooms available from {startDate} to {endDate}
          {rooms.length === 0 && " - No rooms available for these dates"}
        </Alert>
      )}

      <Row>
        {rooms.map((room) => (
          <Col lg={6} className="mb-4" key={room.id}>
            <Card className="h-100 room-card">
              <Card.Body>
                <div className="d-flex justify-content-between align-items-start mb-3">
                  <Card.Title>{room.name}</Card.Title>
                  <span className="badge bg-success">
                    Free!
                  </span>
                </div>
                
                <Card.Text>{room.description}</Card.Text>
                
                <div className="mb-3">
                  <small className="text-muted">
                    <strong>Max Occupancy:</strong> {room.maxOccupancy} guests
                  </small>
                </div>

                <div className="d-flex justify-content-between align-items-center">
                  <span className={`badge ${room.isAvailable ? 'bg-success' : 'bg-danger'}`}>
                    {room.isAvailable ? 'Available' : 'Not Available'}
                  </span>
                  
                  {user && room.isAvailable ? (
                    <Button 
                      as={Link} 
                      to="/book" 
                      state={{ 
                        selectedRoom: room,
                        checkIn: startDate,
                        checkOut: endDate
                      }}
                      variant="primary"
                    >
                      Book Now
                    </Button>
                  ) : !user ? (
                    <Button as={Link} to="/login" variant="outline-primary">
                      Login to Book
                    </Button>
                  ) : (
                    <Button variant="secondary" disabled>
                      Not Available
                    </Button>
                  )}
                </div>
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>

      {rooms.length === 0 && !loading && (
        <div className="text-center mt-5">
          <h4>No rooms found</h4>
          <p>Please try different dates or check back later.</p>
        </div>
      )}
    </div>
  );
};

export default Rooms;
