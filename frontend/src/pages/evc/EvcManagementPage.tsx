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
} from '@mui/material';
import { PageHeader } from '../../components/common/PageHeader';
import { StatusChip } from '../../components/common/StatusChip';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { evcApi } from '../../api/evc';
import { EvcVisitDto } from '../../types';

export const EvcManagementPage: React.FC = () => {
  const [visits, setVisits] = useState<EvcVisitDto[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    evcApi
      .getByApplication('00000000-0000-0000-0000-000000000000')
      .then((res) => {
        if (res.data) setVisits(res.data);
      })
      .catch(() => setVisits([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <Box>
      <PageHeader
        title="Expert Visiting Committee (EVC) Operations"
        subtitle="Schedule visits, assign peer reviewers with conflict detection, and record findings"
      />

      <Card>
        <CardContent>
          {loading ? (
            <LoadingSpinner message="Loading EVC visits..." />
          ) : visits.length === 0 ? (
            <Typography variant="body2" color="text.secondary" textAlign="center" py={4}>
              No EVC visits currently scheduled.
            </Typography>
          ) : (
            <Table size="small">
              <TableHead>
                <TableRow sx={{ backgroundColor: 'grey.50' }}>
                  <TableCell fontWeight={600}>Application #</TableCell>
                  <TableCell fontWeight={600}>Meeting Mode</TableCell>
                  <TableCell fontWeight={600}>Proposed Date</TableCell>
                  <TableCell fontWeight={600}>Confirmed Date</TableCell>
                  <TableCell fontWeight={600}>Venue</TableCell>
                  <TableCell fontWeight={600}>Status</TableCell>
                  <TableCell align="right" fontWeight={600}>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {visits.map((v) => (
                  <TableRow key={v.id} hover>
                    <TableCell fontWeight={600}>{v.applicationNumber}</TableCell>
                    <TableCell>{v.meetingMode || '—'}</TableCell>
                    <TableCell>{v.proposedDate ? new Date(v.proposedDate).toLocaleDateString() : '—'}</TableCell>
                    <TableCell>{v.confirmedDate ? new Date(v.confirmedDate).toLocaleDateString() : '—'}</TableCell>
                    <TableCell>{v.venue || '—'}</TableCell>
                    <TableCell><StatusChip status={v.status} /></TableCell>
                    <TableCell align="right">
                      <Button size="small" variant="outlined">Manage</Button>
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
