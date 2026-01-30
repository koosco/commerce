export const localConfig = {
  name: 'local',
  authService: 'http://localhost:8089',
  userService: 'http://localhost:8081',
  catalogService: 'http://localhost:8084',
  inventoryService: 'http://localhost:8083',
  orderService: 'http://localhost:8085',
  paymentService: 'http://localhost:8087',
  paths: {
    auth: '/api/auth',
    users: '/api/users',
    catalog: '/api/catalog/products',
    categories: '/api/catalog/categories',
    inventory: '/api/inventories',
    orders: '/api/orders',
    payments: '/api/payments',
  },
};
