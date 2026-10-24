import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  Button,
  Typography,
  TextField,
  InputAdornment,
} from '@mui/material';
import { Search as SearchIcon, Add as AddIcon, Visibility as ViewIcon } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { PageHeader } from '../../components/common/PageHeader';
import { StatusChip } from '../../components/common/StatusChip';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { applicationsApi } from '../../api/applications';
import { useAuth } from '../../contexts/AuthContext';
import { ApplicationDto } from '../../types';

export const ApplicationListPage: React.FC = () => {
  const navigate = useNavigate();
  const { hasPermission } = useAuth();
  const [applications, setApplications] = useState<ApplicationDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [total, setTotal] = useState(0);
  const [search, setSearch] = useState('');

  const fetchApplications = () => {
    setLoading(true);
    applicationsApi
      .getAll(page, rowsPerPage)
      .then((res) => {
        if (res.data) {
          setApplications(res.data.content || []);
          setTotal(res.data.totalElements || 0);
        }
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchApplications();
  }, [page, rowsPerPage]);

  const filtered = applications.filter((app) =>
    (app.applicationNumber?.toLowerCase() || '').includes(search.toLowerCase()) ||
    (app.institutionName?.toLowerCase() || '').includes(search.toLowerCase()) ||
    (app.category?.toLowerCase() || '').includes(search.toLowerCase())
  );

  return (
    <Box>
      <PageHeader
        title="Applications"
        subtitle="Manage and track institutional applications throughout their lifecycle"
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

      <Card>
        <CardContent>
          <Box mb={2} display="flex" justifyContent="space-between">
            <TextField
              size="small"
              placeholder="Search by application #, institution, category..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              sx={{ width: 350 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon fontSize="small" />
                  </InputAdornment>
                ),
              }}
            />
          </Box>

          {loading ? (
            <LoadingSpinner message="Loading applications..." />
          ) : filtered.length === 0 ? (
            <Typography variant="body2" color="text.secondary" textAlign="center" py={4}>
              No applications found.
            </Typography>
          ) : (
            <TableContainer>
              <Table size="small">
                <TableHead>
                  <TableRow sx={{ backgroundColor: 'grey.50' }}>
                    <TableCell fontWeight={600}>Application #</TableCell>
                    <TableCell fontWeight={600}>Institution</TableCell>
                    <TableCell fontWeight={600}>Category</TableCell>
                    <TableCell fontWeight={600}>Academic Year</TableCell>
                    <TableCell fontWeight={600}>Current Stage</TableCell>
                    <TableCell fontWeight={600}>Status</TableCell>
                    <TableCell fontWeight={600}>Assigned Officer</TableCell>
                    <TableCell align="right" fontWeight={600}>Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {filtered.map((app) => (
                    <TableRow key={app.id} hover>
                      <TableCell fontWeight={600} color="primary.main">
                        {app.applicationNumber}
                      </TableCell>
                      <TableCell>{app.institutionName || '—'}</TableCell>
                      <TableCell>{app.category}</TableCell>
                      <TableCell>{app.academicYear}</TableCell>
                      <TableCell>
                        <StatusChip status={app.currentStage} />
                      </TableCell>
                      <TableCell>
                        <StatusChip status={app.status} />
                      </TableCell>
                      <TableCell>{app.assignedOfficerName || 'Unassigned'}</TableCell>
                      <TableCell align="right">
                        <Button
                          size="small"
                          variant="outlined"
                          startIcon={<ViewIcon />}
                          onClick={() => navigate(`/applications/${app.id}`)}
                        >
                          View
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}

          <TablePagination
            component="div"
            count={total}
            page={page}
            onPageChange={(_, newPage) => setPage(newPage)}
            rowsPerPage={rowsPerPage}
            onRowsPerPageChange={(e) => {
              setRowsPerPage(parseInt(e.target.value, 10));
              setPage(0);
            }}
          />
        </CardContent>
      </Card>
    </Box>
  );
};
