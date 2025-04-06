import React, { useState, useEffect } from 'react';
import {
    Table,
    Button,
    Modal,
    Form,
    Select,
    message,
    Spin,
    Tag,
    Space,
    Popconfirm,
    Drawer,
    Divider,
    List,
    InputNumber
} from 'antd';
import {
    getOrders,
    getOrderById,
    createOrder,
    updateOrder,
    deleteOrder,
    updateOrderStatus,
    getOrdersByUser,
    createBulkOrders,
    getUsers,
    getProducts
} from '../services/api';

const { Option } = Select;

const OrdersPage = () => {
    const [orders, setOrders] = useState([]);
    const [users, setUsers] = useState([]);
    const [products, setProducts] = useState([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [loading, setLoading] = useState(false);
    const [searchUserId, setSearchUserId] = useState(null);
    const [form] = Form.useForm();

    // Состояния для массового создания
    const [bulkCreateVisible, setBulkCreateVisible] = useState(false);
    const [selectedUsers, setSelectedUsers] = useState([]);
    const [selectedProducts, setSelectedProducts] = useState([]);
    const [productQuantities, setProductQuantities] = useState({});

    const statusColors = {
        CREATED: 'blue',
        IN_PROCESS: 'orange',
        SHIPPED: 'geekblue',
        DELIVERED: 'green',
        CANCELLED: 'red',
        RETURNED: 'purple'
    };

    useEffect(() => {
        fetchOrders();
        fetchUsersAndProducts();
    }, [searchUserId]);

    const fetchOrders = async () => {
        try {
            setLoading(true);
            const data = searchUserId
                ? await getOrdersByUser(searchUserId)
                : await getOrders();
            setOrders(data || []);
        } catch (error) {
            message.error('Failed to fetch orders');
        } finally {
            setLoading(false);
        }
    };

    const fetchUsersAndProducts = async () => {
        try {
            const [usersData, productsData] = await Promise.all([
                getUsers(),
                getProducts()
            ]);
            setUsers(usersData || []);
            setProducts(productsData || []);
        } catch (error) {
            message.error('Failed to fetch additional data');
        }
    };

    const enrichOrders = (ordersData) => {
        return ordersData.map(order => ({
            ...order,
            user: users.find(u => u.id === order.userId),
            products: products.filter(p => order.productIds.includes(p.id))
        }));
    };

    const handleCreate = async () => {
        try {
            const values = await form.validateFields();
            // Преобразуем массив товаров с учетом количества
            const productIds = [];
            values.products.forEach(item => {
                for (let i = 0; i < item.quantity; i++) {
                    productIds.push(item.productId);
                }
            });

            await createOrder({
                userId: values.userId,
                productIds,
                status: 'CREATED'
            });
            message.success('Order created successfully');
            resetModal();
            await fetchOrders();
        } catch (error) {
            message.error('Error creating order');
        }
    };

    const handleDelete = async (id) => {
        try {
            await deleteOrder(id);
            message.success('Order deleted successfully');
            await fetchOrders();
        } catch (error) {
            message.error('Error deleting order');
        }
    };

    const handleStatusChange = async (id, status) => {
        try {
            await updateOrderStatus(id, status);
            message.success('Order status updated');
            await fetchOrders();
        } catch (error) {
            message.error('Error updating status');
        }
    };

    const handleBulkCreate = async () => {
        try {
            if (selectedUsers.length === 0 || selectedProducts.length === 0) {
                message.warning('Please select at least one user and one product');
                return;
            }

            const bulkOrders = selectedUsers.flatMap(userId =>
                selectedProducts.map(productId => ({
                    userId,
                    productIds: Array(productQuantities[productId] || 1).fill(productId),
                    status: 'CREATED'
                }))
            );

            await createBulkOrders({ orders: bulkOrders });
            message.success(`Successfully created ${bulkOrders.length} orders`);
            setBulkCreateVisible(false);
            setSelectedUsers([]);
            setSelectedProducts([]);
            setProductQuantities({});
            await fetchOrders();
        } catch (error) {
            console.error('Bulk create error:', error);
            message.error(`Failed to create bulk orders: ${error.message}`);
        }
    };

    const resetModal = () => {
        form.resetFields();
        setIsModalVisible(false);
    };

    const handleQuantityChange = (productId, value) => {
        setProductQuantities(prev => ({
            ...prev,
            [productId]: value
        }));
    };

    const columns = [
        {
            title: 'ID',
            dataIndex: 'id',
            key: 'id',
            sorter: (a, b) => a.id - b.id,
        },
        {
            title: 'User',
            key: 'user',
            render: (_, order) => order.user ? (
                <div>
                    <div>{order.user.email}</div>
                    <div style={{ fontSize: 12, color: '#888' }}>{order.user.username}</div>
                </div>
            ) : 'N/A',
            filters: users.map(user => ({
                text: user.email,
                value: user.id,
            })),
            onFilter: (value, record) => record.user?.id === value,
        },
        {
            title: 'Products',
            key: 'products',
            render: (_, order) => {
                const productCounts = {};
                order.products?.forEach(product => {
                    productCounts[product.id] = (productCounts[product.id] || 0) + 1;
                });

                return (
                    <Space wrap>
                        {Object.keys(productCounts).length > 0 ? (
                            Object.entries(productCounts).map(([productId, count]) => {
                                const product = products.find(p => p.id === parseInt(productId));
                                return (
                                    <Tag key={productId}>
                                        {product?.title} (${product?.price}) × {count}
                                    </Tag>
                                );
                            })
                        ) : (
                            <Tag color="warning">No products</Tag>
                        )}
                    </Space>
                );
            },
        },
        {
            title: 'Status',
            key: 'status',
            render: (_, order) => (
                <Select
                    value={order.status}
                    onChange={(value) => handleStatusChange(order.id, value)}
                    style={{ width: 150 }}
                >
                    {Object.keys(statusColors).map(status => (
                        <Option key={status} value={status}>
                            <Tag color={statusColors[status]}>
                                {status.split('_').map(word =>
                                    word.charAt(0) + word.slice(1).toLowerCase()
                                ).join(' ')}
                            </Tag>
                        </Option>
                    ))}
                </Select>
            ),
            filters: Object.keys(statusColors).map(status => ({
                text: status.split('_').join(' '),
                value: status,
            })),
            onFilter: (value, record) => record.status === value,
        },
        {
            title: 'Created At',
            key: 'createdAt',
            render: (_, order) => new Date(order.createdAt).toLocaleString(),
            sorter: (a, b) => new Date(a.createdAt) - new Date(b.createdAt),
        },
        {
            title: 'Actions',
            key: 'actions',
            render: (_, order) => (
                <Space>
                    <Popconfirm
                        title="Are you sure to delete this order?"
                        onConfirm={() => handleDelete(order.id)}
                        okText="Yes"
                        cancelText="No"
                    >
                        <Button danger>Delete</Button>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <Spin spinning={loading}>
            <div style={{ marginBottom: 16, display: 'flex', gap: 8 }}>
                <Button
                    type="primary"
                    onClick={() => setIsModalVisible(true)}
                >
                    Create Order
                </Button>
                <Button onClick={() => setBulkCreateVisible(true)}>
                    Bulk Create Orders
                </Button>

                <Select
                    placeholder="Filter by user"
                    style={{ width: 200 }}
                    allowClear
                    onChange={setSearchUserId}
                    options={users.map(user => ({
                        value: user.id,
                        label: `${user.email} (${user.username})`
                    }))}
                />
            </div>

            <Table
                columns={columns}
                dataSource={enrichOrders(orders)}
                rowKey="id"
                loading={loading}
                pagination={{ pageSize: 10 }}
                scroll={{ x: true }}
            />

            {/* Модальное окно для создания заказа */}
            <Modal
                title="Create Order"
                visible={isModalVisible}
                onOk={handleCreate}
                onCancel={resetModal}
                okText="Create"
                cancelText="Cancel"
                width={700}
            >
                <Form form={form} layout="vertical">
                    <Form.Item
                        name="userId"
                        label="User"
                        rules={[{ required: true, message: 'Please select a user' }]}
                    >
                        <Select
                            placeholder="Select user"
                            loading={loading}
                            showSearch
                            optionFilterProp="children"
                            filterOption={(input, option) =>
                                option.children.toLowerCase().includes(input.toLowerCase())
                            }
                        >
                            {users.map(user => (
                                <Option key={user.id} value={user.id}>
                                    {user.email} ({user.username})
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>

                    <Form.List name="products">
                        {(fields, { add, remove }) => (
                            <>
                                {fields.map(({ key, name, ...restField }) => (
                                    <Space key={key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'productId']}
                                            rules={[{ required: true, message: 'Select a product' }]}
                                            style={{ width: 300 }}
                                        >
                                            <Select
                                                placeholder="Select product"
                                                showSearch
                                                optionFilterProp="children"
                                                filterOption={(input, option) =>
                                                    option.children.toLowerCase().includes(input.toLowerCase())
                                                }
                                            >
                                                {products.map(product => (
                                                    <Option key={product.id} value={product.id}>
                                                        {product.title} (${product.price})
                                                    </Option>
                                                ))}
                                            </Select>
                                        </Form.Item>
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'quantity']}
                                            initialValue={1}
                                            rules={[{ required: true, message: 'Enter quantity' }]}
                                        >
                                            <InputNumber min={1} max={100} placeholder="Qty" />
                                        </Form.Item>
                                        <Button type="link" danger onClick={() => remove(name)}>
                                            Remove
                                        </Button>
                                    </Space>
                                ))}
                                <Form.Item>
                                    <Button type="dashed" onClick={() => add()} block>
                                        Add Product
                                    </Button>
                                </Form.Item>
                            </>
                        )}
                    </Form.List>
                </Form>
            </Modal>

            {/* Drawer для массового создания заказов */}
            <Drawer
                title="Bulk Create Orders"
                width={720}
                visible={bulkCreateVisible}
                onClose={() => setBulkCreateVisible(false)}
                footer={
                    <div style={{ textAlign: 'right' }}>
                        <Button onClick={() => setBulkCreateVisible(false)} style={{ marginRight: 8 }}>
                            Cancel
                        </Button>
                        <Button onClick={handleBulkCreate} type="primary">
                            Create {selectedUsers.length * selectedProducts.length} Orders
                        </Button>
                    </div>
                }
            >
                <Form layout="vertical">
                    <Form.Item label="Select Users">
                        <Select
                            mode="multiple"
                            placeholder="Select users"
                            value={selectedUsers}
                            onChange={setSelectedUsers}
                            style={{ width: '100%' }}
                            showSearch
                            optionFilterProp="children"
                            filterOption={(input, option) =>
                                option.children.toLowerCase().includes(input.toLowerCase())
                            }
                        >
                            {users.map(user => (
                                <Option key={user.id} value={user.id}>
                                    {user.email} ({user.username})
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>

                    <Divider orientation="left">Products</Divider>

                    <List
                        dataSource={products}
                        renderItem={product => (
                            <List.Item>
                                <div style={{ display: 'flex', alignItems: 'center', width: '100%' }}>
                                    <div style={{ flex: 1 }}>
                                        {product.title} (${product.price})
                                    </div>
                                    <InputNumber
                                        min={1}
                                        max={100}
                                        defaultValue={1}
                                        onChange={(value) => handleQuantityChange(product.id, value)}
                                        style={{ width: 80 }}
                                    />
                                </div>
                            </List.Item>
                        )}
                    />

                    <Divider orientation="left">Orders Preview</Divider>
                    <div style={{ marginBottom: 16 }}>
                        <span>Total orders to be created: </span>
                        <strong>{selectedUsers.length * selectedProducts.length}</strong>
                    </div>
                </Form>
            </Drawer>
        </Spin>
    );
};

export default OrdersPage;