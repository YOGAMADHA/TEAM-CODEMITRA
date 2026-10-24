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
} from '@mui/material';
import { PageHeader } from '../../components/common/PageHeader';
import { StatusChip } from '../../components/common/StatusChip';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { complianceApi } from '../../api/compliance';
import { ComplianceItemDto } from '../../types';

export const ComplianceListPage: React.FC = () => {
  const [items, setItems] = useState<ComplianceItemDto[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    complianceApi
      .getExpiring()
      .then((res) => {
        if (res.data) setItems(res.data);
      })
      .catch(() => setItems([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <Box>
      <PageHeader
        title="Compliance & Undertaking Tracking"
        subtitle="Monitor statutory compliance, mandatory undertakings, and renewal expirations"
      />

      <Card>
        <CardContent>
          {loading ? (
            <LoadingSpinner message="Loading compliance records..." />
          ) : items.length === 0 ? (
            <Typography variant="body2" color="text.secondary" textAlign="center" py={4}>
              All compliance items and undertakings are up-to-date.
            </Typography>
          ) : (
            <Table size="small">
              <TableHead>
                <TableRow sx={{ backgroundColor: 'grey.50' }}>
                  <TableCell fontWeight={600}>Category</TableCell>
                  <TableCell fontWeight={600}>Description</TableCell>
                  <TableCell fontWeight={600}>Responsible Party</TableCell>
                  <TableCell fontWeight={600}>Due Date</TableCell>
                  <TableCell fontWeight={600}>Verified</TableCell>
                  <TableCell fontWeight={600}>Status</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {items.map((c) => (
                  <TableRow key={c.id} hover>
                    <TableCell fontWeight={600}>{c.category}</TableCell>
                    <TableCell>{c.description}</TableCell>
                    <TableCell>{c.responsibleParty}</TableCell>
                    <TableCell>{c.dueDate ? new Date(c.dueDate).toLocaleDateString() : '—'}</TableCell>
                    <TableCell>{c.verified ? 'Verified' : 'Pending'}</TableCell>
                    <TableCell><StatusChip status={c.status} /></TableCell>
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
