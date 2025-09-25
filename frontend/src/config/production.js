// Production environment configuration
export const config = {
  API_URL: process.env.REACT_APP_API_URL || 'https://reserveease.com/api',
  APP_NAME: 'ReserveEase',
  VERSION: '1.0.0',
  DESCRIPTION: 'Free reservation system for easy booking management',
  CONTACT_EMAIL: 'admin@reserveease.com',
  SUPPORT_URL: 'https://reserveease.com/support',
  ENVIRONMENT: 'production'
};

export default config;
