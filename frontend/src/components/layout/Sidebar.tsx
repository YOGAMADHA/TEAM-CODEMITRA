import React from 'react';
import {
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Divider,
  Box,
  Typography,
} from '@mui/material';
import {
  Dashboard as DashboardIcon,
  Description as DescriptionIcon,
  ReportProblem as DeficiencyIcon,
  Event as EvcIcon,
  AssignmentTurnedIn as ComplianceIcon,
  Warning as EscalationIcon,
  Assessment as ReportIcon,
  People as UserIcon,
  History as AuditIcon,
  InfoOutlined as InfoIcon,
} from '@mui/icons-material';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const DRAWER_WIDTH = 260;

interface SidebarProps {
  mobileOpen: boolean;
  handleDrawerToggle: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ mobileOpen, handleDrawerToggle }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const { hasPermission, hasRole } = useAuth();

  const menuItems = [
    { text: 'Dashboard', path: '/dashboard', icon: <DashboardIcon />, visible: true },
    { text: 'Applications', path: '/applications', icon: <DescriptionIcon />, visible: true },
    { text: 'Deficiencies', path: '/deficiencies', icon: <DeficiencyIcon />, visible: hasPermission('DEFICIENCY_RESPOND') || hasPermission('DEFICIENCY_CREATE') },
    { text: 'EVC Visits', path: '/evc', icon: <EvcIcon />, visible: hasPermission('EVC_SCHEDULE') || hasRole('EVC_MEMBER') || hasRole('SYSTEM_ADMINISTRATOR') },
    { text: 'Compliance', path: '/compliance', icon: <ComplianceIcon />, visible: hasPermission('COMPLIANCE_MANAGE') || hasRole('APPLICANT') },
    { text: 'Escalations', path: '/escalations', icon: <EscalationIcon />, visible: hasPermission('ESCALATION_MANAGE') || hasRole('REGIONAL_OFFICER') },
    { text: 'Reports', path: '/reports', icon: <ReportIcon />, visible: hasPermission('REPORT_VIEW') },
    { text: 'User Management', path: '/admin/users', icon: <UserIcon />, visible: hasPermission('USER_MANAGE') },
    { text: 'Audit Trail', path: '/admin/audit', icon: <AuditIcon />, visible: hasPermission('AUDIT_VIEW') || hasRole('AUDITOR') || hasRole('ADMIN') },
    { text: 'Demo Prototype Notice', path: '/demo-mode', icon: <InfoIcon />, visible: true },
  ];

  const drawerContent = (
    <div>
      <Toolbar sx={{ px: 2, py: 1, backgroundColor: 'primary.dark', color: 'white' }}>
        <Box>
          <Typography variant="h6" fontWeight={700} sx={{ letterSpacing: 0.5, lineHeight: 1.2 }}>
            AICTE
          </Typography>
          <Typography variant="caption" sx={{ opacity: 0.8 }}>
            Approval Workflow Platform
          </Typography>
        </Box>
      </Toolbar>
      <Divider />
      <List sx={{ pt: 1 }}>
        {menuItems
          .filter((item) => item.visible)
          .map((item) => {
            const selected = location.pathname.startsWith(item.path);
            return (
              <ListItem key={item.text} disablePadding sx={{ mb: 0.5 }}>
                <ListItemButton
                  selected={selected}
                  onClick={() => {
                    navigate(item.path);
                    if (mobileOpen) handleDrawerToggle();
                  }}
                  sx={{
                    mx: 1,
                    borderRadius: 1,
                    '&.Mui-selected': {
                      backgroundColor: 'primary.light',
                      color: 'primary.contrastText',
                      '&:hover': {
                        backgroundColor: 'primary.main',
                      },
                      '& .MuiListItemIcon-root': {
                        color: 'primary.contrastText',
                      },
                    },
                  }}
                >
                  <ListItemIcon sx={{ minWidth: 40, color: selected ? 'inherit' : 'primary.main' }}>
                    {item.icon}
                  </ListItemIcon>
                  <ListItemText
                    primary={item.text}
                    primaryTypographyProps={{ fontSize: '0.9rem', fontWeight: selected ? 600 : 500 }}
                  />
                </ListItemButton>
              </ListItem>
            );
          })}
      </List>
    </div>
  );

  return (
    <Box component="nav" sx={{ width: { md: DRAWER_WIDTH }, flexShrink: { md: 0 } }}>
      {/* Mobile Drawer */}
      <Drawer
        variant="temporary"
        open={mobileOpen}
        onClose={handleDrawerToggle}
        ModalProps={{ keepMounted: true }}
        sx={{
          display: { xs: 'block', md: 'none' },
          '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH },
        }}
      >
        {drawerContent}
      </Drawer>
      {/* Desktop Drawer */}
      <Drawer
        variant="permanent"
        sx={{
          display: { xs: 'none', md: 'block' },
          '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH, borderRight: '1px solid #E0E0E0' },
        }}
        open
      >
        {drawerContent}
      </Drawer>
    </Box>
  );
};
