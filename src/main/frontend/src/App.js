// src/App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { Layout, Menu, Typography } from 'antd';
import OrdersPage from './pages/OrdersPage';
import ProductsPage from './pages/ProductsPage';
import UsersPage from './pages/UsersPage';

const { Header, Content, Footer } = Layout;
const { Text } = Typography;

function App() {
    return (
        <Router>
            <Layout style={{ minHeight: '100vh' }}>
                {/* Шапка с навигацией */}
                <Header style={{
                    position: 'sticky',
                    top: 0,
                    zIndex: 1,
                    width: '100%',
                    display: 'flex',
                    alignItems: 'center',
                    background: '#001529',
                    padding: '0 24px'
                }}>
                    <Text style={{
                        color: 'white',
                        marginRight: '24px',
                        fontSize: '18px',
                        fontWeight: 'bold'
                    }}>
                        BUYSELL App
                    </Text>

                    <Menu
                        theme="dark"
                        mode="horizontal"
                        defaultSelectedKeys={['orders']}
                        style={{
                            flex: 1,
                            background: 'transparent',
                            borderBottom: 'none'
                        }}
                    >
                        <Menu.Item key="orders">
                            <Link to="/">Orders</Link>
                        </Menu.Item>
                        <Menu.Item key="products">
                            <Link to="/products">Products</Link>
                        </Menu.Item>
                        <Menu.Item key="users">
                            <Link to="/users">Users</Link>
                        </Menu.Item>
                    </Menu>

                    <Text style={{ color: 'rgba(255, 255, 255, 0.65)' }}>
                        Developer: Nikita Kevra
                    </Text>
                </Header>

                <Content style={{
                    padding: '24px',
                    background: '#f0f2f5'
                }}>
                    <div style={{
                        background: '#fff',
                        padding: '24px',
                        borderRadius: '8px',
                        minHeight: 'calc(100vh - 134px)',
                        boxShadow: '0 1px 2px rgba(0,0,0,0.1)'
                    }}>
                        <Routes>
                            <Route path="/" element={<OrdersPage />} />
                            <Route path="/products" element={<ProductsPage />} />
                            <Route path="/users" element={<UsersPage />} />
                        </Routes>
                    </div>
                </Content>

                <Footer style={{
                    textAlign: 'center',
                    background: '#f0f2f5',
                    padding: '16px 50px'
                }}>
                    <Text type="secondary">
                        © {new Date().getFullYear()} BUYSELL Application - BSUIR Project
                    </Text>
                </Footer>
            </Layout>
        </Router>
    );
}

export default App;