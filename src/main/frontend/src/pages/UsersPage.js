import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Modal, Form, Input, message, Spin, Popconfirm } from 'antd';
import {
    getUsers,
    getUserById,
    createUser,
    updateUser,
    deleteUser
} from '../services/api';

const UsersPage = () => {
    const [users, setUsers] = useState([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingUserId, setEditingUserId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [form] = Form.useForm();

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            setLoading(true);
            const data = await getUsers();
            setUsers(data || []);
        } catch (error) {
            message.error('Failed to fetch users');
        } finally {
            setLoading(false);
        }
    };

    const handleCreate = async () => {
        try {
            const values = await form.validateFields();
            await createUser(values);
            message.success('User created successfully');
            resetModal();
            await fetchUsers();
        } catch (error) {
            message.error('Error creating user');
        }
    };

    const handleUpdate = async () => {
        try {
            const values = await form.validateFields();
            await updateUser(editingUserId, values);
            message.success('User updated successfully');
            resetModal();
            await fetchUsers();
        } catch (error) {
            message.error('Error updating user');
        }
    };

    const handleDelete = async (id) => {
        try {
            await deleteUser(id);
            message.success('User deleted successfully');
            await fetchUsers();
        } catch (error) {
            message.error('Error deleting user');
        }
    };

    const handleEdit = async (id) => {
        try {
            setLoading(true);
            const user = await getUserById(id);
            form.setFieldsValue({
                username: user.username,
                email: user.email
            });
            setEditingUserId(id);
            setIsModalVisible(true);
        } catch (error) {
            message.error('Error loading user data');
        } finally {
            setLoading(false);
        }
    };

    const resetModal = () => {
        form.resetFields();
        setEditingUserId(null);
        setIsModalVisible(false);
    };

    const columns = [
        {
            title: 'ID',
            dataIndex: 'id',
            key: 'id',
            sorter: (a, b) => a.id - b.id,
        },
        {
            title: 'Username',
            dataIndex: 'username',
            key: 'username',
            sorter: (a, b) => a.username.localeCompare(b.username),
        },
        {
            title: 'Email',
            dataIndex: 'email',
            key: 'email',
            sorter: (a, b) => a.email.localeCompare(b.email),
        },
        {
            title: 'Actions',
            key: 'actions',
            render: (_, user) => (
                <Space>
                    <Button onClick={() => handleEdit(user.id)}>Edit</Button>
                    <Popconfirm
                        title="Are you sure to delete this user?"
                        onConfirm={() => handleDelete(user.id)}
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
            <Button
                type="primary"
                onClick={() => setIsModalVisible(true)}
                style={{ marginBottom: 16 }}
            >
                Create User
            </Button>

            <Table
                columns={columns}
                dataSource={users}
                rowKey="id"
                loading={loading}
                bordered
                pagination={{ pageSize: 10 }}
            />

            <Modal
                title={editingUserId ? 'Edit User' : 'Create User'}
                visible={isModalVisible}
                onOk={editingUserId ? handleUpdate : handleCreate}
                onCancel={resetModal}
                okText={editingUserId ? 'Update' : 'Create'}
                cancelText="Cancel"
                confirmLoading={loading}
            >
                <Form form={form} layout="vertical">
                    <Form.Item
                        name="username"
                        label="Username"
                        rules={[
                            { required: true, message: 'Please input username!' },
                            { min: 3, message: 'Username must be at least 3 characters' },
                            { max: 50, message: 'Username cannot exceed 50 characters' }
                        ]}
                    >
                        <Input placeholder="Enter username" />
                    </Form.Item>

                    <Form.Item
                        name="email"
                        label="Email"
                        rules={[
                            { required: true, message: 'Please input email!' },
                            { type: 'email', message: 'Please enter a valid email' }
                        ]}
                    >
                        <Input placeholder="Enter email" />
                    </Form.Item>
                </Form>
            </Modal>
        </Spin>
    );
};

export default UsersPage;