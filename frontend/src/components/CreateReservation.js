import React, { useState, useEffect } from 'react';
import { Form, Button, Card, Row, Col, Alert, Spinner } from 'react-bootstrap';
import { useLocation, useNavigate } from 'react-router-dom';
import { roomAPI, reservationAPI } from '../services/api';

const CreateReservation = () => {
  const location = useLocation();
  const navigate = useNavigate();
  
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  
  const [formData, setFormData] = useState({
    roomId: location.state?.selectedRoom?.id || '',
    checkInDate: location.state?.checkIn || '',
    checkOutDate: location.state?.checkOut || '',
    numberOfGuests: 1,
    notes: ''
  });

  useEffect(() => {
    fetchRooms();
  }, []);

  const fetchRooms = async () => {
    try {
      const response = await roomAPI.getAllRooms();
      setRooms(response.data.filter(room => room.isAvailable));
    } catch (err) {
      setError('Failed to load rooms');
    }
    setLoading(false);
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  // Removed pricing calculation as this is now a free reservation system

  const getSelectedRoom = () => {
    return rooms.find(r => r.id === parseInt(formData.roomId));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validation
    if (!formData.roomId || !formData.checkInDate || !formData.checkOutDate) {
      setError('Please fill in all required fields');
      return;
    }

    const checkIn = new Date(formData.checkInDate);
    const checkOut = new Date(formData.checkOutDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (checkIn < today) {
      setError('Check-in date cannot be in the past');
      return;
    }

    if (checkOut <= checkIn) {
      setError('Check-out date must be after check-in date');
      return;
    }

    const room = getSelectedRoom();
    if (formData.numberOfGuests > room.maxOccupancy) {
      setError(`Number of guests cannot exceed ${room.maxOccupancy} for this room`);
      return;
    }

    setSubmitting(true);
    setError('');

    try {
      await reservationAPI.createReservation({
        roomId: parseInt(formData.roomId),
        checkInDate: formData.checkInDate,
        checkOutDate: formData.checkOutDate,
        numberOfGuests: parseInt(formData.numberOfGuests),
        notes: formData.notes
      });
      
      setSuccess('Reservation created successfully! Redirecting to your reservations...');
      setTimeout(() => navigate('/my-reservations'), 2000);
    } catch (err) {
      setError(err.response?.data || 'Failed to create reservation. Please try again.');
    }
    
    setSubmitting(false);
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

  const selectedRoom = getSelectedRoom();

  return (
    <div>
      <Row>
        <Col lg={8}>
          <Card>
            <Card.Header>
              <h3>Create New Reservation</h3>
            </Card.Header>
            <Card.Body>
              {error && <Alert variant="danger">{error}</Alert>}
              {success && <Alert variant="success">{success}</Alert>}
              
              <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                  <Form.Label>Room/Cottage *</Form.Label>
                  <Form.Select
                    name="roomId"
                    value={formData.roomId}
                    onChange={handleChange}
                    required
                  >
                    <option value="">Select a room...</option>
                    {rooms.map(room => (
                      <option key={room.id} value={room.id}>
                        {room.name} - {formatPrice(room.pricePerNight)}/night (Max: {room.maxOccupancy} guests)
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>

                <Row>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label>Check-in Date *</Form.Label>
                      <Form.Control
                        type="date"
                        name="checkInDate"
                        value={formData.checkInDate}
                        onChange={handleChange}
                        min={new Date().toISOString().split('T')[0]}
                        required
                      />
                    </Form.Group>
                  </Col>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label>Check-out Date *</Form.Label>
                      <Form.Control
                        type="date"
                        name="checkOutDate"
                        value={formData.checkOutDate}
                        onChange={handleChange}
                        min={formData.checkInDate || new Date().toISOString().split('T')[0]}
                        required
                      />
                    </Form.Group>
                  </Col>
                </Row>

                <Form.Group className="mb-3">
                  <Form.Label>Number of Guests *</Form.Label>
                  <Form.Control
                    type="number"
                    name="numberOfGuests"
                    value={formData.numberOfGuests}
                    onChange={handleChange}
                    min="1"
                    max={selectedRoom ? selectedRoom.maxOccupancy : 10}
                    required
                  />
                  {selectedRoom && (
                    <Form.Text className="text-muted">
                      Maximum {selectedRoom.maxOccupancy} guests for this room
                    </Form.Text>
                  )}
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Notes (Optional)</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={3}
                    name="notes"
                    value={formData.notes}
                    onChange={handleChange}
                    placeholder="Any special requests or notes..."
                  />
                </Form.Group>

                <div className="d-grid">
                  <Button 
                    variant="primary" 
                    type="submit" 
                    size="lg"
                    disabled={submitting}
                  >
                    {submitting ? 'Creating Reservation...' : 'Book Now - Free!'}
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>
        </Col>

        <Col lg={4}>
          {selectedRoom && (
            <Card>
              <Card.Header>
                <h5>Booking Summary</h5>
              </Card.Header>
              <Card.Body>
                <div className="mb-3">
                  <strong>{selectedRoom.name}</strong>
                  <p className="text-muted small mb-1">{selectedRoom.description}</p>
                  <span className="text-muted small">Max: {selectedRoom.maxOccupancy} guests</span>
                </div>
                
                {formData.checkInDate && formData.checkOutDate && (
                  <>
                    <div className="mb-2">
                      <small className="text-muted">Check-in:</small><br />
                      <strong>{new Date(formData.checkInDate).toLocaleDateString()}</strong>
                    </div>
                    <div className="mb-2">
                      <small className="text-muted">Check-out:</small><br />
                      <strong>{new Date(formData.checkOutDate).toLocaleDateString()}</strong>
                    </div>
                    <div className="mb-2">
                      <small className="text-muted">Nights:</small><br />
                      <strong>{Math.ceil((new Date(formData.checkOutDate) - new Date(formData.checkInDate)) / (1000 * 60 * 60 * 24))}</strong>
                    </div>
                    <div className="mb-3">
                      <small className="text-muted">Guests:</small><br />
                      <strong>{formData.numberOfGuests}</strong>
                    </div>
                    <hr />
                    <div className="mb-0">
                      <h5 className="text-success">Total: Free!</h5>
                    </div>
                  </>
                )}
              </Card.Body>
            </Card>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CreateReservation;
