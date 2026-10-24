import React, { useEffect, useState } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  Button,
  Divider,
} from '@mui/material';
import {
  Description as AppIcon,
  CheckCircle as ApprovedIcon,
  Warning as PendingIcon,
  Error as OverdueIcon,
  Add as AddIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { PageHeader } from '../../components/common/PageHeader';
import { reportsApi } from '../../api/reports';
import { useAuth } from '../../contexts/AuthContext';
import { ApplicationSummaryDto, PendencyReportDto } from '../../types';

export const DashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const { user, hasPermission } = useAuth();
  const [summary, setSummary] = useState<ApplicationSummaryDto | null>(null);
  const [pendency, setPendency] = useState<PendencyReportDto | null>(null);

  useEffect(() => {
    const fetchDashboardData = () => {
      reportsApi.getSummary().then((res) => {
        if (res.data) setSummary(res.data);
      }).catch(() => {});

      reportsApi.getPendency().then((res) => {
        if (res.data) setPendency(res.data);
      }).catch(() => {});
    };

    fetchDashboardData();
    const interval = setInterval(fetchDashboardData, 30000);
    return () => clearInterval(interval);
  }, []);

  return (
    <Box>
      <PageHeader
        title={`Welcome, ${user?.firstName || 'User'}`}
        subtitle="AICTE Human-Supervised Application Workflow & Governance Platform"
        action={
          hasPermission('APPLICATION_CREATE') ? (
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => navigate('/applications/new')}
            >
              New Application
            </Button>
          ) : undefined
        }
      />

      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ borderLeft: '4px solid #1976D2' }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Box>
                  <Typography variant="caption" color="text.secondary" fontWeight={600}>
                    TOTAL APPLICATIONS
                  </Typography>
                  <Typography variant="h4" fontWeight={700} color="primary.main">
                    {summary?.totalApplications ?? 0}
                  </Typography>
                </Box>
                <AppIcon sx={{ fontSize: 40, color: 'primary.light' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ borderLeft: '4px solid #2E7D32' }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Box>
                  <Typography variant="caption" color="text.secondary" fontWeight={600}>
                    APPROVED
                  </Typography>
                  <Typography variant="h4" fontWeight={700} color="success.main">
                    {summary?.totalApproved ?? 0}
                  </Typography>
                </Box>
                <ApprovedIcon sx={{ fontSize: 40, color: 'success.light' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ borderLeft: '4px solid #F57C00' }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Box>
                  <Typography variant="caption" color="text.secondary" fontWeight={600}>
                    UNDER REVIEW
                  </Typography>
                  <Typography variant="h4" fontWeight={700} color="warning.main">
                    {summary?.totalUnderReview ?? 0}
                  </Typography>
                </Box>
                <PendingIcon sx={{ fontSize: 40, color: 'warning.light' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ borderLeft: '4px solid #D32F2F' }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Box>
                  <Typography variant="caption" color="text.secondary" fontWeight={600}>
                    OVERDUE / ESCALATED
                  </Typography>
                  <Typography variant="h4" fontWeight={700} color="error.main">
                    {pendency?.totalOverdue ?? 0}
                  </Typography>
                </Box>
                <OverdueIcon sx={{ fontSize: 40, color: 'error.light' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        <Grid item xs={12} md={7}>
          <Card>
            <CardContent>
              <Typography variant="h6" fontWeight={600} gutterBottom>
                Pendency by Workflow Stage
              </Typography>
              <Divider sx={{ mb: 2 }} />
              {pendency?.stageCounts && Object.keys(pendency.stageCounts).length > 0 ? (
                <Grid container spacing={1.5}>
                  {Object.entries(pendency.stageCounts).map(([stage, count]) => (
                    <Grid item xs={12} sm={6} key={stage}>
                      <Box
                        p={1.5}
                        bgcolor="grey.50"
                        borderRadius={1}
                        display="flex"
                        justifyContent="space-between"
                        alignItems="center"
                      >
                        <Typography variant="body2" fontWeight={500}>
                          {stage.replace(/_/g, ' ')}
                        </Typography>
                        <Typography variant="subtitle2" fontWeight={700} color="primary.main">
                          {count}
                        </Typography>
                      </Box>
                    </Grid>
                  ))}
                </Grid>
              ) : (
                <Typography variant="body2" color="text.secondary" py={3} textAlign="center">
                  No applications currently pending in workflow stages.
                </Typography>
              )}
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={5}>
          <Card>
            <CardContent>
              <Typography variant="h6" fontWeight={600} gutterBottom>
                Quick Actions
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <Box display="flex" flexDirection="column" gap={1.5}>
                <Button
                  variant="outlined"
                  fullWidth
                  onClick={() => navigate('/applications')}
                  sx={{ justifyContent: 'flex-start', py: 1 }}
                >
                  View All Applications
                </Button>
                {hasPermission('DEFICIENCY_CREATE') || hasPermission('DEFICIENCY_RESPOND') ? (
                  <Button
                    variant="outlined"
                    fullWidth
                    onClick={() => navigate('/deficiencies')}
                    sx={{ justifyContent: 'flex-start', py: 1 }}
                  >
                    Manage Deficiencies
                  </Button>
                ) : null}
                {hasPermission('EVC_SCHEDULE') || hasRole('EVC_MEMBER') ? (
                  <Button
                    variant="outlined"
                    fullWidth
                    onClick={() => navigate('/evc')}
                    sx={{ justifyContent: 'flex-start', py: 1 }}
                  >
                    EVC Visit Schedule
                  </Button>
                ) : null}
                {hasPermission('REPORT_VIEW') ? (
                  <Button
                    variant="outlined"
                    fullWidth
                    onClick={() => navigate('/reports')}
                    sx={{ justifyContent: 'flex-start', py: 1 }}
                  >
                    View Analytics & Reports
                  </Button>
                ) : null}
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};
