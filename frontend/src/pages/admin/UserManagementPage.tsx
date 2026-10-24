import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Typography,
  Button,
  Chip,
} from '@mui/material';
import { PageHeader } from '../../components/common/PageHeader';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { usersApi } from '../../api/users';
import { UserDto } from '../../types';

export const UserManagementPage: React.FC = () => {
  const [users, setUsers] = useState<UserDto[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchUsers = () => {
    setLoading(true);
    usersApi
      .getAll(0, 50)
      .then((res) => {
        if (res.data?.content) setUsers(res.data.content);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleToggleActive = async (user: UserDto) => {
    if (user.active) {
      await usersApi.deactivate(user.id);
    } else {
      await usersApi.activate(user.id);
    }
    fetchUsers();
  };

  return (
    <Box>
      <PageHeader
        title="User & Access Management"
        subtitle="Manage administrative accounts, role assignments, and permission authorizations"
      />

      <Card>
        <CardContent>
          {loading ? (
            <LoadingSpinner message="Loading user directory..." />
          ) : (
            <Table size="small">
              <TableHead>
                <TableRow sx={{ backgroundColor: 'grey.50' }}>
                  <TableCell fontWeight={600}>Name</TableCell>
                  <TableCell fontWeight={600}>Email</TableCell>
                  <TableCell fontWeight={600}>Roles</TableCell>
                  <TableCell fontWeight={600}>Status</TableCell>
                  <TableCell align="right" fontWeight={600}>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {users.map((u) => (
                  <TableRow key={u.id} hover>
                    <TableCell fontWeight={600}>{u.firstName} {u.lastName}</TableCell>
                    <TableCell>{u.email}</TableCell>
                    <TableCell>
                      {u.roles?.map((r) => (
                        <Chip key={r} label={r} size="small" sx={{ mr: 0.5 }} />
                      ))}
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={u.active ? 'Active' : 'Deactivated'}
                        color={u.active ? 'success' : 'default'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell align="right">
                      <Button
                        size="small"
                        color={u.active ? 'error' : 'success'}
                        onClick={() => handleToggleActive(u)}
                      >
                        {u.active ? 'Deactivate' : 'Activate'}
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};
