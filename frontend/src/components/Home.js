import React from 'react';
import { Container, Row, Col, Button, Card } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Home = () => {
  const { user } = useAuth();

  return (
    <div>
      <div className="hero-section">
        <Container>
          <Row>
            <Col lg={8} className="mx-auto text-center">
              <h1 className="display-4 mb-4">Welcome to ReserveEase</h1>
              <p className="lead mb-4">
                Your gateway to effortless reservations. Experience seamless booking 
                for accommodations, events, and more - all completely free!
              </p>
              <div>
                <Button 
                  as={Link} 
                  to="/rooms" 
                  variant="light" 
                  size="lg" 
                  className="me-3"
                >
                  Browse Options
                </Button>
                {user ? (
                  <Button 
                    as={Link} 
                    to="/book" 
                    variant="outline-light" 
                    size="lg"
                  >
                    Book Now
                  </Button>
                ) : (
                  <Button 
                    as={Link} 
                    to="/register" 
                    variant="outline-light" 
                    size="lg"
                  >
                    Sign Up to Book
                  </Button>
                )}
              </div>
            </Col>
          </Row>
        </Container>
      </div>

      <Container>
        <Row>
          <Col lg={12} className="text-center mb-5">
            <h2>Why Choose ReserveEase?</h2>
          </Col>
        </Row>
        <Row>
          <Col md={4}>
            <Card className="text-center h-100 room-card">
              <Card.Body>
                <div className="mb-3" style={{ fontSize: '3rem' }}>💰</div>
                <Card.Title>Completely Free</Card.Title>
                <Card.Text>
                  No booking fees, no hidden charges. Make reservations 
                  without any cost - it's completely free!
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4}>
            <Card className="text-center h-100 room-card">
              <Card.Body>
                <div className="mb-3" style={{ fontSize: '3rem' }}>⚡</div>
                <Card.Title>Lightning Fast</Card.Title>
                <Card.Text>
                  Quick and easy booking process. Get instant confirmation 
                  and manage your reservations effortlessly.
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4}>
            <Card className="text-center h-100 room-card">
              <Card.Body>
                <div className="mb-3" style={{ fontSize: '3rem' }}>📅</div>
                <Card.Title>Transparent & Open</Card.Title>
                <Card.Text>
                  See all bookings from other users. Complete transparency 
                  helps you plan better and avoid conflicts.
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
        </Row>

        <Row className="mt-5">
          <Col lg={8} className="mx-auto text-center">
            <h3>Ready to Book Your Stay?</h3>
            <p>
              Create an account to start making reservations and managing your bookings.
            </p>
            {!user && (
              <Button 
                as={Link} 
                to="/register" 
                variant="primary" 
                size="lg"
              >
                Get Started
              </Button>
            )}
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default Home;
