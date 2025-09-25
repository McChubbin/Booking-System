import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API calls
export const authAPI = {
  login: (credentials) => api.post('/auth/signin', credentials),
  register: (userData) => api.post('/auth/signup', userData),
  getCurrentUser: () => api.get('/auth/me'),
};

// Room API calls
export const roomAPI = {
  getAllRooms: () => api.get('/rooms'),
  getRoomById: (id) => api.get(`/rooms/${id}`),
  getAvailableRooms: (startDate, endDate) => 
    api.get('/rooms/available', {
      params: { startDate, endDate }
    }),
};

// Reservation API calls
export const reservationAPI = {
  createReservation: (reservationData) => api.post('/reservations', reservationData),
  getMyReservations: () => api.get('/reservations'),
  getAllActiveReservations: () => api.get('/reservations/all'),
  getReservationById: (id) => api.get(`/reservations/${id}`),
  updateReservation: (id, reservationData) => api.put(`/reservations/${id}`, reservationData),
  cancelReservation: (id) => api.delete(`/reservations/${id}`),
  getReservationCalendar: (startDate, endDate) => 
    api.get('/reservations/calendar', {
      params: { startDate, endDate }
    }),
};

export default api;
