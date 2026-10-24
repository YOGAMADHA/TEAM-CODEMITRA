import React, { useState, useEffect } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Divider,
  Button,
  LinearProgress,
} from '@mui/material';
import DownloadIcon from '@mui/icons-material/Download';
import RefreshIcon from '@mui/icons-material/Refresh';
import { PageHeader } from '../../components/common/PageHeader';
import { StatusChip } from '../../components/common/StatusChip';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { reportsApi } from '../../api/reports';
import { ApplicationDto, PendencyReportDto } from '../../types';

export const ReportDashboard: React.FC = () => {
  const [pendency, setPendency] = useState<PendencyReportDto | null>(null);
  const [overdue, setOverdue] = useState<ApplicationDto[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchReports = () => {
    setLoading(true);
    Promise.all([reportsApi.getPendency(), reportsApi.getOverdue()])
      .then(([penRes, overRes]) => {
        if (penRes.data) setPendency(penRes.data);
        if (overRes.data) setOverdue(overRes.data);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchReports();
    const interval = setInterval(fetchReports, 60000);
    return () => clearInterval(interval);
  }, []);

  const handleExportOverdueCsv = () => {
    const headers = ['Application Number', 'Current Stage', 'Category', 'Due Date'];
    const rows = overdue.map((a) => [
      a.applicationNumber,
      a.currentStage,
      a.category,
      a.dueDate || '',
    ]);

    const csvContent = 'data:text/csv;charset=utf-8,' +
      [headers.join(','), ...rows.map((e) => e.map((x) => `"${x}"`).join(','))].join('\n');

    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `aicte_overdue_applications_${new Date().toISOString().slice(0, 10)}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleExportPendencyCsv = () => {
    if (!pendency?.stageCounts) return;
    const headers = ['Workflow Stage', 'Pending Count'];
    const rows = Object.entries(pendency.stageCounts).map(([stage, count]) => [stage, count]);

    const csvContent = 'data:text/csv;charset=utf-8,' +
      [headers.join(','), ...rows.map((e) => `"${e[0]}",${e[1]}`)].join('\n');

    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `aicte_pendency_report_${new Date().toISOString().slice(0, 10)}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  if (loading && !pendency) return <LoadingSpinner message="Generating analytics reports..." />;

  const maxStageCount = pendency?.stageCounts
    ? Math.max(...Object.values(pendency.stageCounts), 1)
    : 1;

  return (
    <Box>
      <PageHeader
        title="Pendency & Performance Reports"
        subtitle="Live administrative metrics and SLA tracking across regional jurisdictions"
        action={
          <Button variant="outlined" startIcon={<RefreshIcon />} onClick={fetchReports}>
            Refresh Metrics
          </Button>
        }
      />

      <Grid container spacing={3} mb={3}>
        {/* Pendency Overview */}
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
                <Typography variant="h6" fontWeight={600}>
                  Workflow Pendency Overview
                </Typography>
                <Button size="small" startIcon={<DownloadIcon />} onClick={handleExportPendencyCsv}>
                  Export CSV
                </Button>
              </Box>
              <Divider sx={{ mb: 2 }} />

              <Box display="flex" justifyContent="space-between" mb={1.5}>
                <Typography variant="body2" color="text.secondary">Total Pending Applications:</Typography>
                <Typography variant="subtitle2" fontWeight={700}>{pendency?.totalPending ?? 0}</Typography>
              </Box>
              <Box display="flex" justifyContent="space-between" mb={2}>
                <Typography variant="body2" color="text.secondary">Total Overdue / SLA Breached:</Typography>
                <Typography variant="subtitle2" fontWeight={700} color="error.main">
                  {pendency?.totalOverdue ?? 0}
                </Typography>
              </Box>

              <Typography variant="subtitle2" fontWeight={600} mt={3} mb={1}>
                Applications Per Workflow Stage
              </Typography>
              <Table size="small">
                <TableHead>
                  <TableRow sx={{ backgroundColor: 'grey.50' }}>
                    <TableCell fontWeight={600}>Workflow Stage</TableCell>
                    <TableCell align="right" fontWeight={600}>Count</TableCell>
                    <TableCell sx={{ width: '40%' }} fontWeight={600}>Relative Distribution</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {pendency?.stageCounts &&
                    Object.entries(pendency.stageCounts).map(([st, cnt]) => (
                      <TableRow key={st} hover>
                        <TableCell>{st.replace(/_/g, ' ')}</TableCell>
                        <TableCell align="right" fontWeight={600}>{cnt}</TableCell>
                        <TableCell>
                          <LinearProgress
                            variant="determinate"
                            value={(cnt / maxStageCount) * 100}
                            sx={{ height: 8, borderRadius: 4 }}
                          />
                        </TableCell>
                      </TableRow>
                    ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </Grid>

        {/* Overdue Applications */}
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
                <Typography variant="h6" fontWeight={600} color="error.main">
                  Overdue Applications List
                </Typography>
                {overdue.length > 0 && (
                  <Button size="small" startIcon={<DownloadIcon />} onClick={handleExportOverdueCsv}>
                    Export CSV
                  </Button>
                )}
              </Box>
              <Divider sx={{ mb: 2 }} />

              {overdue.length === 0 ? (
                <Typography variant="body2" color="text.secondary" py={4} textAlign="center">
                  No applications currently overdue. All SLA deadlines within compliance window.
                </Typography>
              ) : (
                <Table size="small">
                  <TableHead>
                    <TableRow sx={{ backgroundColor: 'grey.50' }}>
                      <TableCell fontWeight={600}>Application #</TableCell>
                      <TableCell fontWeight={600}>Stage</TableCell>
                      <TableCell fontWeight={600}>Due Date</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {overdue.map((a) => (
                      <TableRow key={a.id} hover>
                        <TableCell fontWeight={600}>{a.applicationNumber}</TableCell>
                        <TableCell><StatusChip status={a.currentStage} /></TableCell>
                        <TableCell sx={{ color: 'error.main', fontWeight: 600 }}>
                          {a.dueDate ? new Date(a.dueDate).toLocaleDateString() : '—'}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};
