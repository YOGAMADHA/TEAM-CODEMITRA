import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  List,
  ListItem,
  ListItemText,
  Typography,
  Button,
  Divider,
  IconButton,
} from '@mui/material';
import { CheckCircle as ReadIcon } from '@mui/icons-material';
import { PageHeader } from '../../components/common/PageHeader';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { notificationsApi } from '../../api/notifications';
import { NotificationDto } from '../../types';

export const NotificationPage: React.FC = () => {
  const [notifications, setNotifications] = useState<NotificationDto[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchNotifications = () => {
    setLoading(true);
    notificationsApi
      .getAll()
      .then((res) => {
        if (res.data) setNotifications(res.data);
      })
      .catch(() => setNotifications([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  const handleMarkAllRead = async () => {
    await notificationsApi.markAllRead();
    fetchNotifications();
  };

  const handleMarkRead = async (id: string) => {
    await notificationsApi.markRead(id);
    fetchNotifications();
  };

  return (
    <Box maxWidth="800px" mx="auto">
      <PageHeader
        title="Notifications & Alerts"
        subtitle="In-app reminders, deficiency notices, and stage update broadcasts"
        action={
          <Button variant="outlined" size="small" onClick={handleMarkAllRead}>
            Mark All as Read
          </Button>
        }
      />

      <Card>
        <CardContent sx={{ p: 0 }}>
          {loading ? (
            <LoadingSpinner message="Loading notifications..." />
          ) : notifications.length === 0 ? (
            <Typography variant="body2" color="text.secondary" textAlign="center" py={4}>
              No notifications in your inbox.
            </Typography>
          ) : (
            <List sx={{ p: 0 }}>
              {notifications.map((n, i) => (
                <React.Fragment key={n.id}>
                  <ListItem
                    sx={{
                      backgroundColor: n.read ? 'inherit' : 'rgba(25, 118, 210, 0.05)',
                      py: 2,
                    }}
                    secondaryAction={
                      !n.read ? (
                        <IconButton edge="end" size="small" onClick={() => handleMarkRead(n.id)}>
                          <ReadIcon color="primary" fontSize="small" />
                        </IconButton>
                      ) : null
                    }
                  >
                    <ListItemText
                      primary={
                        <Box display="flex" justifyContent="space-between" alignItems="center">
                          <Typography variant="subtitle2" fontWeight={n.read ? 500 : 700}>
                            {n.type.replace(/_/g, ' ')}
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            {new Date(n.createdAt).toLocaleString()}
                          </Typography>
                        </Box>
                      }
                      secondary={
                        <Typography variant="body2" color="text.primary" sx={{ mt: 0.5 }}>
                          {n.payload}
                        </Typography>
                      }
                    />
                  </ListItem>
                  {i < notifications.length - 1 && <Divider />}
                </React.Fragment>
              ))}
            </List>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};
