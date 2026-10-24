import React, { useState, useEffect } from 'react';
import {
  AppBar,
  Box,
  CssBaseline,
  IconButton,
  Toolbar,
  Typography,
  Menu,
  MenuItem,
  Badge,
  Avatar,
  Chip,
  Button,
} from '@mui/material';
import {
  Menu as MenuIcon,
  Notifications as NotificationsIcon,
  AccountCircle,
  Logout,
  InfoOutlined as InfoIcon,
} from '@mui/icons-material';
import { Outlet, useNavigate } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { useAuth } from '../../contexts/AuthContext';
import { notificationsApi } from '../../api/notifications';

const DRAWER_WIDTH = 260;

export const MainLayout: React.FC = () => {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [unreadCount, setUnreadCount] = useState<number>(0);
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  // Poll for notifications count every 30 seconds
  useEffect(() => {
    const fetchCount = () => {
      notificationsApi
        .getCount()
        .then((res) => {
          if (typeof res.data === 'number') {
            setUnreadCount(res.data);
          }
        })
        .catch(() => {});
    };

    fetchCount();
    const interval = setInterval(fetchCount, 30000);
    return () => clearInterval(interval);
  }, []);

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', backgroundColor: 'background.default' }}>
      <CssBaseline />
      <AppBar
        position="fixed"
        sx={{
          width: { md: `calc(100% - ${DRAWER_WIDTH}px)` },
          ml: { md: `${DRAWER_WIDTH}px` },
          backgroundColor: 'white',
          color: 'text.primary',
          boxShadow: '0 1px 3px rgba(0,0,0,0.08)',
        }}
      >
        {/* Top prototype disclaimer strip */}
        <Box
          sx={{
            backgroundColor: '#FFF3E0',
            color: '#E65100',
            px: 2,
            py: 0.5,
            fontSize: '0.72rem',
            textAlign: 'center',
            borderBottom: '1px solid #FFE0B2',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: 1,
          }}
        >
          <InfoIcon sx={{ fontSize: '0.9rem' }} />
          <span>
            <strong>PROTOTYPE DEMONSTRATION MODEL</strong> — For academic demonstration only. Does not replace official AICTE statutory scrutiny, inspection, or competent-authority decisions.
          </span>
          <Button
            size="small"
            sx={{ fontSize: '0.65rem', py: 0, px: 0.5, minWidth: 'auto', textTransform: 'none', color: '#B26A00' }}
            onClick={() => navigate('/demo-mode')}
          >
            Learn More
          </Button>
        </Box>

        <Toolbar>
          <IconButton
            color="inherit"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { md: 'none' } }}
          >
            <MenuIcon />
          </IconButton>
          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="subtitle1" fontWeight={600} color="primary.main">
              AICTE Approval Workflow Management System
            </Typography>
          </Box>

          <IconButton color="inherit" onClick={() => navigate('/notifications')} sx={{ mr: 1 }}>
            <Badge badgeContent={unreadCount} color="error">
              <NotificationsIcon />
            </Badge>
          </IconButton>

          {user && (
            <Box display="flex" alignItems="center">
              <Box textAlign="right" mr={1.5} sx={{ display: { xs: 'none', sm: 'block' } }}>
                <Typography variant="body2" fontWeight={600}>
                  {user.firstName} {user.lastName}
                </Typography>
                <Chip
                  label={user.roles?.[0]?.replace(/_/g, ' ') || 'USER'}
                  size="small"
                  sx={{ height: 18, fontSize: '0.65rem', fontWeight: 600 }}
                  color="primary"
                />
              </Box>
              <IconButton onClick={handleMenu} color="inherit">
                <Avatar sx={{ width: 36, height: 36, bgcolor: 'primary.main', fontSize: '0.9rem' }}>
                  {user.firstName?.charAt(0) || <AccountCircle />}
                </Avatar>
              </IconButton>
              <Menu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleClose}
                transformOrigin={{ horizontal: 'right', vertical: 'top' }}
                anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
              >
                <MenuItem disabled>
                  <Typography variant="caption" color="text.secondary">
                    {user.email}
                  </Typography>
                </MenuItem>
                <MenuItem onClick={() => { handleClose(); navigate('/demo-mode'); }}>
                  <InfoIcon fontSize="small" sx={{ mr: 1 }} /> Prototype Notice
                </MenuItem>
                <MenuItem
                  onClick={() => {
                    handleClose();
                    logout();
                  }}
                >
                  <Logout fontSize="small" sx={{ mr: 1 }} /> Logout
                </MenuItem>
              </Menu>
            </Box>
          )}
        </Toolbar>
      </AppBar>

      <Sidebar mobileOpen={mobileOpen} handleDrawerToggle={handleDrawerToggle} />

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { md: `calc(100% - ${DRAWER_WIDTH}px)` },
          mt: '96px',
        }}
      >
        <Outlet />
      </Box>
    </Box>
  );
};
