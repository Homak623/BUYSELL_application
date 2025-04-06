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
    InputNumber,
    Card,
    Row,
    Col
} from 'antd';
import {
    AppstoreOutlined,
    PlusOutlined,
    DeleteOutlined
} from '@ant-design/icons';
import {
    getOrders,
    createOrder,
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
    const [loading, setLoading] = useState(false);
    const [searchUserId, setSearchUserId] = useState(null);

    const [isCreateModalVisible, setIsCreateModalVisible] = useState(false);
    const [createForm] = Form.useForm();

    const [isBulkCreateVisible, setIsBulkCreateVisible] = useState(false);
    const [bulkCreateForm] = Form.useForm();
    const [selectedBulkProducts, setSelectedBulkProducts] = useState([]);

    const statusColors = {
        CREATED: 'blue',
        IN_PROCESS: 'orange',
        SHIPPED: 'geekblue',
        DELIVERED: 'green',
        CANCELLED: 'red',
        RETURNED: 'purple'
    };

    useEffect(() => {
        fetchData();
    }, [searchUserId]);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [ordersData, usersData, productsData] = await Promise.all([
                searchUserId ? getOrdersByUser(searchUserId) : getOrders(),
                getUsers(),
                getProducts()
            ]);

            setOrders(ordersData || []);
            setUsers(usersData || []);
            setProducts(productsData || []);
        } catch (error) {
            message.error('Failed to fetch data');
        } finally {
            setLoading(false);
        }
    };

    const enrichOrders = () => {
        return orders.map(order => ({
            ...order,
            user: users.find(u => u.id === order.userId),
            products: products.filter(p => order.productIds.includes(p.id))
        }));
    };

    const handleCreateOrder = async () => {
        try {
            const values = await createForm.validateFields();

            // Формируем productIds с учетом количества
            const productIds = values.products.flatMap(item =>
                Array(item.quantity).fill(item.productId)
            );

            await createOrder({
                userId: values.userId,
                productIds,
                status: 'CREATED'
            });

            message.success('Order created successfully');
            createForm.resetFields();
            setIsCreateModalVisible(false);
            await fetchData();
        } catch (error) {
            message.error('Error creating order');
        }
    };

    const handleBulkCreate = async () => {
        try {
            const values = await bulkCreateForm.validateFields();

            // Формируем массив заказов
            const ordersToCreate = values.users.map(userId => ({
                userId,
                productIds: selectedBulkProducts.flatMap(product =>
                    Array(product.quantity).fill(product.id))
            }));

            await createBulkOrders({ orders: ordersToCreate });

            message.success(`Successfully created ${ordersToCreate.length} orders`);
            bulkCreateForm.resetFields();
            setSelectedBulkProducts([]);
            setIsBulkCreateVisible(false);
            await fetchData();
        } catch (error) {
            message.error('Error creating bulk orders');
        }
    };

    const handleDeleteOrder = async (id) => {
        try {
            await deleteOrder(id);
            message.success('Order deleted successfully');
            await fetchData();
        } catch (error) {
            message.error('Error deleting order');
        }
    };

    const handleStatusChange = async (id, status) => {
        try {
            await updateOrderStatus(id, status);
            message.success('Order status updated');
            await fetchData();
        } catch (error) {
            message.error('Error updating status');
        }
    };

    const addBulkProduct = () => {
        bulkCreateForm.validateFields(['product', 'quantity']).then(values => {
            setSelectedBulkProducts(prev => [
                ...prev,
                {
                    id: values.product,
                    quantity: values.quantity,
                    product: products.find(p => p.id === values.product)
                }
            ]);
            bulkCreateForm.resetFields(['product', 'quantity']);
        }).catch(() => {
            message.warning('Please select a product and quantity');
        });
    };

    const removeBulkProduct = (productId) => {
        setSelectedBulkProducts(prev => prev.filter(p => p.id !== productId));
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
                order.productIds.forEach(id => {
                    productCounts[id] = (productCounts[id] || 0) + 1;
                });

                return (
                    <Space wrap>
                        {Object.keys(productCounts).length > 0 ? (
                            Object.entries(productCounts).map(([id, count]) => {
                                const product = products.find(p => p.id === parseInt(id));
                                return product ? (
                                    <Tag key={id}>
                                        {product.title} (${product.price}) × {count}
                                    </Tag>
                                ) : null;
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
                                {status.split('_').join(' ')}
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
                        onConfirm={() => handleDeleteOrder(order.id)}
                        okText="Yes"
                        cancelText="No"
                    >
                        <Button danger icon={<DeleteOutlined />}>Delete</Button>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <Spin spinning={loading}>
            <div style={{ marginBottom: 16 }}>
                <Card>
                    <Row gutter={16} align="middle">
                        <Col>
                            <Button
                                type="primary"
                                icon={<PlusOutlined />}
                                onClick={() => setIsCreateModalVisible(true)}
                            >
                                Create Order
                            </Button>
                        </Col>
                        <Col>
                            <Button
                                icon={<AppstoreOutlined />}
                                onClick={() => setIsBulkCreateVisible(true)}
                            >
                                Bulk Create
                            </Button>
                        </Col>
                        <Col flex="auto">
                            <Select
                                placeholder="Filter by user"
                                style={{ width: '100%' }}
                                allowClear
                                onChange={setSearchUserId}
                                options={users.map(user => ({
                                    value: user.id,
                                    label: `${user.email} (${user.username})`
                                }))}
                            />
                        </Col>
                    </Row>
                </Card>
            </div>

            <Table
                columns={columns}
                dataSource={enrichOrders()}
                rowKey="id"
                loading={loading}
                pagination={{ pageSize: 10 }}
                scroll={{ x: true }}
            />

            {/* Модальное окно создания одиночного заказа */}
            <Modal
                title="Create New Order"
                visible={isCreateModalVisible}
                onOk={handleCreateOrder}
                onCancel={() => {
                    createForm.resetFields();
                    setIsCreateModalVisible(false);
                }}
                width={700}
            >
                <Form form={createForm} layout="vertical">
                    <Form.Item
                        name="userId"
                        label="User"
                        rules={[{ required: true, message: 'Please select a user' }]}
                    >
                        <Select
                            placeholder="Select user"
                            loading={loading}
                            showSearch
                            optionFilterProp="label"
                        >
                            {users.map(user => (
                                <Option
                                    key={user.id}
                                    value={user.id}
                                    label={`${user.email} (${user.username})`}
                                >
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
                                            rules={[{ required: true, message: 'Please select a product' }]}
                                            style={{ width: '60%' }}
                                        >
                                            <Select
                                                placeholder="Select product"
                                                showSearch
                                                optionFilterProp="label"
                                            >
                                                {products.map(product => (
                                                    <Option
                                                        key={product.id}
                                                        value={product.id}
                                                        label={`${product.title} ($${product.price})`}
                                                    >
                                                        {product.title} (${product.price})
                                                    </Option>
                                                ))}
                                            </Select>
                                        </Form.Item>
                                        <Form.Item
                                            {...restField}
                                            name={[name, 'quantity']}
                                            initialValue={1}
                                            rules={[{ required: true, message: 'Please enter quantity' }]}
                                        >
                                            <InputNumber min={1} max={100} />
                                        </Form.Item>
                                        <Button
                                            type="text"
                                            danger
                                            icon={<DeleteOutlined />}
                                            onClick={() => remove(name)}
                                        />
                                    </Space>
                                ))}
                                <Form.Item>
                                    <Button
                                        type="dashed"
                                        onClick={() => add()}
                                        block
                                        icon={<PlusOutlined />}
                                    >
                                        Add Product
                                    </Button>
                                </Form.Item>
                            </>
                        )}
                    </Form.List>
                </Form>
            </Modal>

            {/* Drawer для массового создания */}
            <Drawer
                title="Bulk Create Orders"
                width={720}
                visible={isBulkCreateVisible}
                onClose={() => {
                    bulkCreateForm.resetFields();
                    setSelectedBulkProducts([]);
                    setIsBulkCreateVisible(false);
                }}
                footer={
                    <div style={{ textAlign: 'right' }}>
                        <Button
                            onClick={() => {
                                bulkCreateForm.resetFields();
                                setSelectedBulkProducts([]);
                                setIsBulkCreateVisible(false);
                            }}
                            style={{ marginRight: 8 }}
                        >
                            Cancel
                        </Button>
                        <Button
                            onClick={handleBulkCreate}
                            type="primary"
                            disabled={selectedBulkProducts.length === 0}
                        >
                            Create Orders
                        </Button>
                    </div>
                }
            >
                <Form form={bulkCreateForm} layout="vertical">
                    <Form.Item
                        name="users"
                        label="Users"
                        rules={[{ required: true, message: 'Please select at least one user' }]}
                    >
                        <Select
                            mode="multiple"
                            placeholder="Select users"
                            showSearch
                            optionFilterProp="label"
                        >
                            {users.map(user => (
                                <Option
                                    key={user.id}
                                    value={user.id}
                                    label={`${user.email} (${user.username})`}
                                >
                                    {user.email} ({user.username})
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>

                    <Divider orientation="left">Products</Divider>

                    <Form.Item
                        name="product"
                        label="Product"
                    >
                        <Select
                            placeholder="Select product"
                            showSearch
                            optionFilterProp="label"
                        >
                            {products.map(product => (
                                <Option
                                    key={product.id}
                                    value={product.id}
                                    label={`${product.title} ($${product.price})`}
                                >
                                    {product.title} (${product.price})
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>

                    <Form.Item
                        name="quantity"
                        label="Quantity"
                        initialValue={1}
                    >
                        <InputNumber min={1} max={100} style={{ width: '100%' }} />
                    </Form.Item>

                    <Button
                        type="primary"
                        onClick={addBulkProduct}
                        icon={<PlusOutlined />}
                        style={{ marginBottom: 16 }}
                    >
                        Add Product
                    </Button>

                    {selectedBulkProducts.length > 0 && (
                        <>
                            <Divider orientation="left">Selected Products</Divider>
                            <List
                                size="small"
                                bordered
                                dataSource={selectedBulkProducts}
                                renderItem={item => (
                                    <List.Item>
                                        <Row align="middle" style={{ width: '100%' }}>
                                            <Col flex="auto">
                                                {item.product.title} (${item.product.price}) × {item.quantity}
                                            </Col>
                                            <Col>
                                                <Button
                                                    type="text"
                                                    danger
                                                    icon={<DeleteOutlined />}
                                                    onClick={() => removeBulkProduct(item.id)}
                                                />
                                            </Col>
                                        </Row>
                                    </List.Item>
                                )}
                            />
                        </>
                    )}

                    <Divider orientation="left">Summary</Divider>
                    <div style={{ marginBottom: 16 }}>
                        <p>
                            <strong>Total Orders:</strong> {bulkCreateForm.getFieldValue('users')?.length || 0}
                        </p>
                        <p>
                            <strong>Total Products:</strong> {selectedBulkProducts.reduce((sum, item) => sum + item.quantity, 0)}
                        </p>
                    </div>
                </Form>
            </Drawer>
        </Spin>
    );
};

export default OrdersPage;