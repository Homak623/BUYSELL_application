import axios from 'axios';

const getBaseUrl = () => {
    if (typeof window !== 'undefined') {
        // В продакшене используем относительные пути (проксируются через Nginx)
        return '';
    }
    return 'http://buysell-backend:8080';
};

const api = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {'Content-Type': 'application/json'},
    timeout: 30000
});
// ================== Orders API ==================
export const getOrders = () => api.get('/orders').then(res => res.data);
export const getOrderById = (id) => api.get(`/orders/${id}`).then(res => res.data);
export const getOrdersByUser = (userId) => api.get(`/orders/user/${userId}`).then(res => res.data);
export const createOrder = (order) => api.post('/orders', order).then(res => res.data);
export const createBulkOrders = (orders) => api.post('/orders/bulk', orders).then(res => res.data);
export const updateOrderStatus = (id, status) =>
    api.put(`/orders/${id}/status?status=${status}`).then(res => res.data);
export const deleteOrder = (id) => api.delete(`/orders/${id}`);
export const updateOrder = (id, orderData) =>
    api.put(`/orders/${id}`, orderData).then(res => res.data);

// ================== Products API ==================
export const getProducts = () => api.get('/products/all').then(res => res.data);
export const getProductById = (id) => api.get(`/products/${id}`).then(res => res.data);
export const searchProducts = (params) =>
    api.get('/products', { params }).then(res => res.data);
export const searchProductsJPQL = (params) =>
    api.get('/products/', { params }).then(res => res.data);
export const createProduct = (product) => api.post('/products', product).then(res => res.data);
export const updateProduct = (id, product) =>
    api.put(`/products/${id}`, product).then(res => res.data);
export const deleteProduct = (id) => api.delete(`/products/${id}`);
export const searchProductsByPriceRange = (params) =>
    api.get('/products/range', { params }).then(res => res.data);
// ================== Users API ==================
export const getUsers = () => api.get('/users').then(res => res.data);
export const getUserById = (id) => api.get(`/users/${id}`).then(res => res.data);
export const createUser = (user) => api.post('/users', user).then(res => res.data);
export const updateUser = (id, user) => api.put(`/users/${id}`, user).then(res => res.data);
export const deleteUser = (id) => api.delete(`/users/${id}`);

export const getProductsWithFilters = async (filters = {}) => {
    const params = {
        title: filters.title || undefined,
        price: filters.price || undefined,
        city: filters.city || undefined,
        author: filters.author || undefined,
        orderStatus: filters.orderStatus || undefined
    };
    return api.get('/products', { params }).then(res => res.data);
};

export const getOrdersWithDetails = async () => {
    const [orders, users, products] = await Promise.all([
        getOrders(),
        getUsers(),
        getProducts()
    ]);

    return orders.map(order => ({
        ...order,
        user: users.find(u => u.id === order.userId),
        products: products.filter(p => order.productIds.includes(p.id))
    }));
};

api.interceptors.response.use(
    response => response,
    error => {
        console.error('API Error:', error);
        return Promise.reject(error);
    }
);

export default api;