import React, { useState, useEffect } from 'react';
import {
    Table,
    Button,
    Modal,
    Form,
    Input,
    InputNumber,
    message,
    Spin,
    Popconfirm,
    Space,
    Select
} from 'antd';
import {
    getProductById,
    createProduct,
    updateProduct,
    deleteProduct,
    searchProducts,
    searchProductsByPriceRange
} from '../services/api';

const { Option } = Select;

const ProductsPage = () => {
    const [products, setProducts] = useState([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingProductId, setEditingProductId] = useState(null);
    const [loading, setLoading] = useState(false);
    const [searchParams, setSearchParams] = useState({});
    const [form] = Form.useForm();
    const [searchForm] = Form.useForm();
    const [cities] = useState(['Moscow', 'Saint Petersburg', 'New York', 'London']);

    useEffect(() => {
        fetchProducts();
    }, [searchParams]);

    const fetchProducts = async () => {
        try {
            setLoading(true);

            // Проверяем, есть ли параметры для диапазона цен
            const hasPriceRange = searchParams.minPrice !== undefined || searchParams.maxPrice !== undefined;

            // Подготовка параметров
            const params = {
                title: searchParams.title || undefined,
                city: searchParams.city || undefined,
                author: searchParams.author || undefined,
                minPrice: searchParams.minPrice !== undefined ?
                    parseFloat(searchParams.minPrice) : undefined,
                maxPrice: searchParams.maxPrice !== undefined ?
                    parseFloat(searchParams.maxPrice) : undefined
            };

            // Удаляем undefined параметры
            const cleanParams = Object.fromEntries(
                Object.entries(params).filter(([_, value]) => value !== undefined)
            );

            let data;
            if (hasPriceRange) {
                // Используем endpoint для диапазона цен
                data = await searchProductsByPriceRange(cleanParams);
            } else {
                // Используем обычный endpoint поиска
                data = await searchProducts(cleanParams);
            }

            setProducts(data || []);
        } catch (error) {
            message.error('Failed to fetch products');
            console.error('Error fetching products:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = (values) => {
        try {
            const priceRange = values.priceRange || {};

            // Валидация диапазона цен
            if (priceRange.min !== undefined && priceRange.max !== undefined) {
                const min = parseFloat(priceRange.min);
                const max = parseFloat(priceRange.max);

                if (min > max) {
                    message.error('Minimum price cannot be greater than maximum price');
                    return;
                }
            }

            setSearchParams({
                title: values.title || undefined,
                city: values.city || undefined,
                author: values.author || undefined,
                minPrice: priceRange.min !== undefined ? parseFloat(priceRange.min) : undefined,
                maxPrice: priceRange.max !== undefined ? parseFloat(priceRange.max) : undefined
            });
        } catch (error) {
            message.error('Invalid search parameters');
            console.error('Search error:', error);
        }
    };

    const handleCreate = async () => {
        try {
            const values = await form.validateFields();
            await createProduct(values);
            message.success('Product created successfully');
            resetModal();
            await fetchProducts();
        } catch (error) {
            message.error('Error creating product');
            console.error('Error creating product:', error);
        }
    };

    const handleUpdate = async () => {
        try {
            const values = await form.validateFields();
            await updateProduct(editingProductId, values);
            message.success('Product updated successfully');
            resetModal();
            await fetchProducts();
        } catch (error) {
            message.error('Error updating product');
            console.error('Error updating product:', error);
        }
    };

    const handleDelete = async (id) => {
        try {
            await deleteProduct(id);
            message.success('Product deleted successfully');
            await fetchProducts();
        } catch (error) {
            message.error('Error deleting product');
            console.error('Error deleting product:', error);
        }
    };

    const handleEdit = async (id) => {
        try {
            setLoading(true);
            const product = await getProductById(id);
            form.setFieldsValue({
                title: product.title,
                description: product.description,
                price: product.price,
                city: product.city,
                author: product.author
            });
            setEditingProductId(id);
            setIsModalVisible(true);
        } catch (error) {
            message.error('Error loading product data');
            console.error('Error loading product:', error);
        } finally {
            setLoading(false);
        }
    };

    const resetModal = () => {
        form.resetFields();
        setEditingProductId(null);
        setIsModalVisible(false);
    };

    const resetSearch = () => {
        searchForm.resetFields();
        setSearchParams({});
    };

    const columns = [
        {
            title: 'ID',
            dataIndex: 'id',
            key: 'id',
            sorter: (a, b) => a.id - b.id,
            width: 80
        },
        {
            title: 'Title',
            dataIndex: 'title',
            key: 'title',
            sorter: (a, b) => a.title.localeCompare(b.title),
            width: 150
        },
        {
            title: 'Description',
            dataIndex: 'description',
            key: 'description',
            ellipsis: true,
            width: 200
        },
        {
            title: 'Price',
            dataIndex: 'price',
            key: 'price',
            render: (price) => `$${price?.toFixed(2) || '0.00'}`,
            sorter: (a, b) => a.price - b.price,
            width: 120
        },
        {
            title: 'City',
            dataIndex: 'city',
            key: 'city',
            filters: cities.map(city => ({ text: city, value: city })),
            onFilter: (value, record) => record.city === value,
            width: 150
        },
        {
            title: 'Author',
            dataIndex: 'author',
            key: 'author',
            sorter: (a, b) => a.author.localeCompare(b.author),
            width: 150
        },
        {
            title: 'Actions',
            key: 'actions',
            fixed: 'right',
            width: 150,
            render: (_, product) => (
                <Space size="small">
                    <Button onClick={() => handleEdit(product.id)} size="small">Edit</Button>
                    <Popconfirm
                        title="Are you sure to delete this product?"
                        onConfirm={() => handleDelete(product.id)}
                        okText="Yes"
                        cancelText="No"
                    >
                        <Button danger size="small">Delete</Button>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <div style={{ padding: 24 }}>
            <Spin spinning={loading}>
                {/* Блок управления - увеличенные отступы */}
                <div style={{
                    marginBottom: 8,  // Уменьшил отступ до таблицы
                    background: '#fff',
                    padding: 24,      // Увеличил внутренний padding
                    borderRadius: 8
                }}>
                    <div style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        marginBottom: 16 // Увеличил отступ
                    }}>
                        <Button
                            type="primary"
                            onClick={() => setIsModalVisible(true)}
                        >
                            Create Product
                        </Button>
                    </div>

                    <Form
                        layout="inline"
                        form={searchForm}
                        onFinish={handleSearch}
                        style={{ marginBottom: 0 }}
                    >
                        <Form.Item
                            name="title"
                            label="Title"
                            style={{ marginBottom: 16, marginRight: 16 }} // Увеличил отступы
                        >
                            <Input
                                placeholder="Search by title"
                                allowClear
                                style={{ width: 180 }} // Увеличил ширину
                            />
                        </Form.Item>

                        <Form.Item
                            name="author"
                            label="Author"
                            style={{ marginBottom: 16, marginRight: 16 }}
                        >
                            <Input
                                placeholder="Search by author"
                                allowClear
                                style={{ width: 180 }}
                            />
                        </Form.Item>

                        <Form.Item
                            name="city"
                            label="City"
                            style={{ marginBottom: 16, marginRight: 16 }}
                        >
                            <Select
                                placeholder="Select city"
                                allowClear
                                style={{ width: 180 }} // Увеличил ширину
                            >
                                {cities.map(city => (
                                    <Option key={city} value={city}>{city}</Option>
                                ))}
                            </Select>
                        </Form.Item>

                        <Form.Item
                            label="Price Range"
                            style={{ marginBottom: 16, marginRight: 16 }}
                        >
                            <Space size={8}> {/* Увеличил расстояние между элементами */}
                                <Form.Item
                                    name={['priceRange', 'min']}
                                    noStyle
                                >
                                    <InputNumber
                                        placeholder="Min $"
                                        min={0}
                                        precision={2}
                                        style={{ width: 100 }} // Увеличил ширину
                                    />
                                </Form.Item>
                                <span>-</span>
                                <Form.Item
                                    name={['priceRange', 'max']}
                                    noStyle
                                >
                                    <InputNumber
                                        placeholder="Max $"
                                        min={0}
                                        precision={2}
                                        style={{ width: 100 }}
                                    />
                                </Form.Item>
                            </Space>
                        </Form.Item>

                        <Form.Item style={{ marginBottom: 16 }}>
                            <Button
                                type="primary"
                                htmlType="submit"
                            >
                                Search
                            </Button>
                            <Button
                                style={{ marginLeft: 16 }} // Увеличил отступ
                                onClick={resetSearch}
                            >
                                Reset
                            </Button>
                        </Form.Item>
                    </Form>
                </div>

                {/* Блок таблицы - уменьшил верхний отступ */}
                <div style={{
                    background: '#fff',
                    padding: 24,
                    borderRadius: 8
                }}>
                    <Table
                        columns={columns}
                        dataSource={products}
                        rowKey="id"
                        loading={loading}
                        scroll={{ x: 1000 }}
                        pagination={{
                            pageSize: 10,
                            showSizeChanger: true,
                            pageSizeOptions: ['10', '20', '50', '100']
                        }}
                        bordered
                    />
                </div>

                <Modal
                    title={editingProductId ? 'Edit Product' : 'Create Product'}
                    visible={isModalVisible}
                    onOk={editingProductId ? handleUpdate : handleCreate}
                    onCancel={resetModal}
                    okText={editingProductId ? 'Update' : 'Create'}
                    cancelText="Cancel"
                    confirmLoading={loading}
                    width={700}
                    destroyOnClose
                >
                    <Form form={form} layout="vertical" preserve={false}>
                        <Form.Item
                            name="title"
                            label="Title"
                            rules={[
                                { required: true, message: 'Please input product title!' },
                                { min: 2, message: 'Title must be at least 2 characters' },
                                { max: 100, message: 'Title cannot exceed 100 characters' }
                            ]}
                            style={{ marginBottom: 16 }} // Увеличил отступ
                        >
                            <Input placeholder="Enter product title" />
                        </Form.Item>

                        <Form.Item
                            name="description"
                            label="Description"
                            rules={[
                                { required: true, message: 'Please input product description!' },
                                { min: 10, message: 'Description must be at least 10 characters' },
                                { max: 1000, message: 'Description cannot exceed 1000 characters' }
                            ]}
                            style={{ marginBottom: 16 }}
                        >
                            <Input.TextArea rows={4} placeholder="Enter detailed description" />
                        </Form.Item>

                        <Form.Item
                            name="price"
                            label="Price ($)"
                            rules={[
                                { required: true, message: 'Please input product price!' },
                                { type: 'number', min: 0, message: 'Price cannot be negative' }
                            ]}
                            style={{ marginBottom: 16 }}
                        >
                            <InputNumber
                                style={{ width: '100%' }}
                                min={0}
                                precision={2}
                                step={0.01}
                                placeholder="Enter price in dollars"
                            />
                        </Form.Item>

                        <Form.Item
                            name="city"
                            label="City"
                            rules={[
                                { required: true, message: 'Please select city!' }
                            ]}
                            style={{ marginBottom: 16 }}
                        >
                            <Select
                                placeholder="Select city"
                                showSearch
                                optionFilterProp="children"
                                filterOption={(input, option) =>
                                    option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                                }
                            >
                                {cities.map(city => (
                                    <Option key={city} value={city}>{city}</Option>
                                ))}
                            </Select>
                        </Form.Item>

                        <Form.Item
                            name="author"
                            label="Author/Seller"
                            rules={[
                                { required: true, message: 'Please input author/seller name!' },
                                { min: 2, message: 'Author name must be at least 2 characters' }
                            ]}
                        >
                            <Input placeholder="Enter author or seller name" />
                        </Form.Item>
                    </Form>
                </Modal>
            </Spin>
        </div>
    );
};

export default ProductsPage;