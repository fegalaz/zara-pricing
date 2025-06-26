// Script de inicialización para MongoDB
// Se ejecuta automáticamente cuando el contenedor de MongoDB se inicia por primera vez

// Crear la base de datos para el microservicio consumidor
db = db.getSiblingDB('preciosdb');

// Crear un usuario específico para la aplicación (opcional, pero recomendado para seguridad)
db.createUser({
  user: 'price_consumer',
  pwd: 'price_consumer_password',
  roles: [
    {
      role: 'readWrite',
      db: 'preciosdb'
    }
  ]
});

// Crear una colección inicial para los eventos de precios
db.createCollection('pricing_events');

// Crear índices para optimizar las consultas
db.pricing_events.createIndex({ "timestamp": -1 });
db.pricing_events.createIndex({ "productId": 1 });
db.pricing_events.createIndex({ "changeType": 1 });

print('MongoDB inicializado correctamente para el microservicio consumidor de precios'); 