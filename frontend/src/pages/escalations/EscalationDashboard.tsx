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
} from '@mui/material';
import { PageHeader } from '../../components/common/PageHeader';
import { StatusChip } from '../../components/common/StatusChip';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { escalationsApi } from '../../api/escalations';
import { useAuth } from '../../contexts/AuthContext';
import { EscalationDto } from '../../types';

export const EscalationDashboard: React.FC = () => {
  const { hasPermission } = useAuth();
  const [escalations, setEscalations] = useState<EscalationDto[]>([]);
  const [loading, setLoading] = useState(true);

  const [resolveOpen, setResolveOpen] = useState(false);
  const [selectedEscId, setSelectedEscId] = useState<string | null>(null);
  const [resolutionRemarks, setResolutionRemarks] = useState('');

  const fetchEscalations = () => {
    setLoading(true);
    escalationsApi
      .getActive()
      .then((res) => {
        if (res.data) setEscalations(res.data);
      })
      .catch(() => setEscalations([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchEscalations();
  }, []);

  const handleResolve = async () => {
    if (!selectedEscId || !resolutionRemarks) return;
    try {
      await escalationsApi.resolve(selectedEscId, { remarks: resolutionRemarks });
      setResolveOpen(false);
      setResolutionRemarks('');
      fetchEscalations();
    } catch {}
  };

  return (
    <Box>
      <PageHeader
        title="Escalations & Overdue Dashboard"
        subtitle="Hierarchical alert system for statutory deadlines and pending actions"
      />

      <Card>
        <CardContent>
          {loading ? (
            <LoadingSpinner message="Loading escalations..." />
          ) : escalations.length === 0 ? (
            <Typography variant="body2" color="text.secondary" textAlign="center" py={4}>
              No active escalations. All deadlines are within SLA.
            </Typography>
          ) : (
            <Table size="small">
              <TableHead>
                <TableRow sx={{ backgroundColor: 'grey.50' }}>
                  <TableCell fontWeight={600}>Level</TableCell>
                  <TableCell fontWeight={600}>Application #</TableCell>
                  <TableCell fontWeight={600}>Reason</TableCell>
                  <TableCell fontWeight={600}>Escalated Date</TableCell>
                  <TableCell align="right" fontWeight={600}>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {escalations.map((e) => (
                  <TableRow key={e.id} hover>
                    <TableCell><StatusChip status={e.level} /></TableCell>
                    <TableCell fontWeight={600}>{e.applicationNumber}</TableCell>
                    <TableCell>{e.reason}</TableCell>
                    <TableCell>{new Date(e.createdAt).toLocaleDateString()}</TableCell>
                    <TableCell align="right">
                      {hasPermission('ESCALATION_MANAGE') && (
                        <Button
                          size="small"
                          variant="contained"
                          color="success"
                          onClick={() => {
                            setSelectedEscId(e.id);
                            setResolveOpen(true);
                          }}
                        >
                          Resolve
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

      <Dialog open={resolveOpen} onClose={() => setResolveOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle>Resolve Escalation</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            multiline
            rows={3}
            label="Resolution Remarks"
            value={resolutionRemarks}
            onChange={(e) => setResolutionRemarks(e.target.value)}
            sx={{ mt: 2 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setResolveOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleResolve} disabled={!resolutionRemarks}>
            Confirm Resolution
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
