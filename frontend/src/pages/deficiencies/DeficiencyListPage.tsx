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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
} from '@mui/material';
import { PageHeader } from '../../components/common/PageHeader';
import { StatusChip } from '../../components/common/StatusChip';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { deficienciesApi } from '../../api/deficiencies';
import { useAuth } from '../../contexts/AuthContext';
import { DeficiencyDto } from '../../types';

export const DeficiencyListPage: React.FC = () => {
  const { hasPermission } = useAuth();
  const [deficiencies, setDeficiencies] = useState<DeficiencyDto[]>([]);
  const [loading, setLoading] = useState(true);

  // Response dialog
  const [responseOpen, setResponseOpen] = useState(false);
  const [selectedDefId, setSelectedDefId] = useState<string | null>(null);
  const [responseText, setResponseText] = useState('');
  const [error, setError] = useState<string | null>(null);

  const fetchDeficiencies = () => {
    setLoading(true);
    // In demo, fetch for common app or all
    deficienciesApi
      .getByApplication('00000000-0000-0000-0000-000000000000') // fallback or empty
      .then((res) => {
        if (res.data) setDeficiencies(res.data);
      })
      .catch(() => setDeficiencies([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchDeficiencies();
  }, []);

  const handleResponseSubmit = async () => {
    if (!selectedDefId || !responseText) return;
    try {
      await deficienciesApi.respond(selectedDefId, { responseText });
      setResponseOpen(false);
      setResponseText('');
      fetchDeficiencies();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error submitting response');
    }
  };

  return (
    <Box>
      <PageHeader
        title="Deficiency Tracking"
        subtitle="Manage identified defects, corrective actions, and applicant responses"
      />

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Card>
        <CardContent>
          {loading ? (
            <LoadingSpinner message="Loading deficiencies..." />
          ) : deficiencies.length === 0 ? (
            <Typography variant="body2" color="text.secondary" textAlign="center" py={4}>
              No pending deficiencies found across applications.
            </Typography>
          ) : (
            <Table size="small">
              <TableHead>
                <TableRow sx={{ backgroundColor: 'grey.50' }}>
                  <TableCell fontWeight={600}>Category</TableCell>
                  <TableCell fontWeight={600}>Description</TableCell>
                  <TableCell fontWeight={600}>Corrective Action</TableCell>
                  <TableCell fontWeight={600}>Due Date</TableCell>
                  <TableCell fontWeight={600}>Status</TableCell>
                  <TableCell align="right" fontWeight={600}>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {deficiencies.map((d) => (
                  <TableRow key={d.id} hover>
                    <TableCell fontWeight={600}>{d.category}</TableCell>
                    <TableCell>{d.description}</TableCell>
                    <TableCell>{d.correctiveAction || '—'}</TableCell>
                    <TableCell>{d.dueDate ? new Date(d.dueDate).toLocaleDateString() : '—'}</TableCell>
                    <TableCell><StatusChip status={d.status} /></TableCell>
                    <TableCell align="right">
                      {d.status === 'OPEN' && hasPermission('DEFICIENCY_RESPOND') && (
                        <Button
                          size="small"
                          variant="outlined"
                          onClick={() => {
                            setSelectedDefId(d.id);
                            setResponseOpen(true);
                          }}
                        >
                          Respond
                        </Button>
                      )}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      <Dialog open={responseOpen} onClose={() => setResponseOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Submit Corrective Action Response</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            multiline
            rows={4}
            label="Applicant Explanation & Corrective Action Details"
            value={responseText}
            onChange={(e) => setResponseText(e.target.value)}
            sx={{ mt: 2 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setResponseOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleResponseSubmit} disabled={!responseText}>
            Submit Response
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
