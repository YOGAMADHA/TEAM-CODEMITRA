import React, { useState, useEffect, useCallback } from 'react';
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
  TextField,
  MenuItem,
  Grid,
  Alert,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  Tooltip,
  TablePagination,
} from '@mui/material';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import DownloadIcon from '@mui/icons-material/Download';
import VisibilityIcon from '@mui/icons-material/Visibility';
import RefreshIcon from '@mui/icons-material/Refresh';
import FilterAltIcon from '@mui/icons-material/FilterAlt';
import { PageHeader } from '../../components/common/PageHeader';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { auditApi } from '../../api/audit';
import { AuditLog, HashChainVerificationResult } from '../../types';

export const AuditLogPage: React.FC = () => {
  const [logs, setLogs] = useState<AuditLog[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(15);

  // Filters
  const [entityType, setEntityType] = useState('');
  const [action, setAction] = useState('');
  const [performedBy, setPerformedBy] = useState('');
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');

  // Chain verification
  const [verifying, setVerifying] = useState(false);
  const [verificationResult, setVerificationResult] = useState<HashChainVerificationResult | null>(null);

  // Detail Modal
  const [selectedLog, setSelectedLog] = useState<AuditLog | null>(null);

  const fetchLogs = useCallback(() => {
    setLoading(true);
    auditApi
      .getAll(page, rowsPerPage, {
        entityType: entityType || undefined,
        action: action || undefined,
        performedBy: performedBy || undefined,
        from: from ? `${from}T00:00:00` : undefined,
        to: to ? `${to}T23:59:59` : undefined,
      })
      .then((res) => {
        if (res.data?.content) {
          setLogs(res.data.content);
          setTotalElements(res.data.totalElements || 0);
        }
      })
      .catch((err) => console.error('Failed to load audit logs', err))
      .finally(() => setLoading(false));
  }, [page, rowsPerPage, entityType, action, performedBy, from, to]);

  useEffect(() => {
    fetchLogs();
  }, [fetchLogs]);

  const handleVerifyChain = () => {
    setVerifying(true);
    auditApi
      .verifyChain()
      .then((res) => {
        if (res.data) setVerificationResult(res.data);
      })
      .catch((err) => console.error('Verification failed', err))
      .finally(() => setVerifying(false));
  };

  const handleExportCsv = () => {
    auditApi
      .exportLogs({
        entityType: entityType || undefined,
        action: action || undefined,
        performedBy: performedBy || undefined,
        from: from ? `${from}T00:00:00` : undefined,
        to: to ? `${to}T23:59:59` : undefined,
      })
      .then((res) => {
        const data = res.data || logs;
        const headers = ['Timestamp', 'Entity Type', 'Entity ID', 'Action', 'Performed By', 'IP Address', 'Previous Hash', 'Current Hash', 'Remarks'];
        const rows = data.map((l) => [
          l.performedAt || l.timestamp || '',
          l.entityType || '',
          l.entityId || '',
          l.action || '',
          l.performedBy || l.performedByName || '',
          l.ipAddress || '',
          l.previousHash || '',
          l.currentHash || '',
          (l.remarks || '').replace(/"/g, '""'),
        ]);

        const csvContent = 'data:text/csv;charset=utf-8,' +
          [headers.join(','), ...rows.map((e) => e.map((x) => `"${x}"`).join(','))].join('\n');

        const encodedUri = encodeURI(csvContent);
        const link = document.createElement('a');
        link.setAttribute('href', encodedUri);
        link.setAttribute('download', `aicte_audit_log_${new Date().toISOString().slice(0, 10)}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      });
  };

  return (
    <Box>
      <PageHeader
        title="Audit Trail & System Integrity Logs"
        subtitle="Cryptographically sealed immutable transaction ledger with SHA-256 hash chaining"
      />

      {/* Chain Verification Card */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Box display="flex" justifyContent="space-between" alignItems="center" flexWrap="wrap" gap={2}>
            <Box>
              <Typography variant="h6" fontWeight={600} display="flex" alignItems="center" gap={1}>
                <VerifiedUserIcon color="primary" /> SHA-256 Hash Chain Integrity Status
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Each audit entry is sealed with currentHash = SHA-256(previousHash + event data). Verify the entire ledger's immutability.
              </Typography>
            </Box>
            <Box display="flex" gap={1}>
              <Button
                variant="contained"
                color="primary"
                startIcon={<VerifiedUserIcon />}
                onClick={handleVerifyChain}
                disabled={verifying}
              >
                {verifying ? 'Verifying Chain...' : 'Verify Hash Chain'}
              </Button>
              <Button
                variant="outlined"
                startIcon={<DownloadIcon />}
                onClick={handleExportCsv}
              >
                Export CSV
              </Button>
            </Box>
          </Box>

          {verificationResult && (
            <Box mt={2}>
              {verificationResult.chainIntact ? (
                <Alert severity="success" icon={<VerifiedUserIcon />}>
                  <strong>Cryptographic Chain Intact!</strong> Verified {verificationResult.verifiedRecords} of{' '}
                  {verificationResult.totalRecords} records at{' '}
                  {new Date(verificationResult.verifiedAt).toLocaleString()}. Zero tampering detected.
                </Alert>
              ) : (
                <Alert severity="error" icon={<ErrorOutlineIcon />}>
                  <strong>Chain Integrity Broken!</strong> {verificationResult.brokenLinks.length} compromised link(s) found.
                  <ul>
                    {verificationResult.brokenLinks.map((b, i) => (
                      <li key={i}>{b}</li>
                    ))}
                  </ul>
                </Alert>
              )}
            </Box>
          )}
        </CardContent>
      </Card>

      {/* Filter Section */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Box display="flex" alignItems="center" gap={1} mb={2}>
            <FilterAltIcon color="action" />
            <Typography variant="subtitle1" fontWeight={600}>
              Search & Filter Audit Records
            </Typography>
          </Box>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6} md={2.4}>
              <TextField
                select
                fullWidth
                size="small"
                label="Entity Type"
                value={entityType}
                onChange={(e) => setEntityType(e.target.value)}
              >
                <MenuItem value="">All Entities</MenuItem>
                <MenuItem value="APPLICATION">Application</MenuItem>
                <MenuItem value="DOCUMENT">Document</MenuItem>
                <MenuItem value="DEFICIENCY">Deficiency</MenuItem>
                <MenuItem value="EVC_VISIT">EVC Visit</MenuItem>
                <MenuItem value="DEADLINE">Deadline</MenuItem>
                <MenuItem value="USER">User</MenuItem>
                <MenuItem value="AI_ASSISTANCE">AI Assistance</MenuItem>
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6} md={2.4}>
              <TextField
                fullWidth
                size="small"
                label="Action"
                value={action}
                placeholder="e.g. STAGE_CHANGED"
                onChange={(e) => setAction(e.target.value)}
              />
            </Grid>
            <Grid item xs={12} sm={6} md={2.4}>
              <TextField
                fullWidth
                size="small"
                label="Performed By"
                value={performedBy}
                placeholder="Username or email"
                onChange={(e) => setPerformedBy(e.target.value)}
              />
            </Grid>
            <Grid item xs={12} sm={6} md={2.4}>
              <TextField
                fullWidth
                size="small"
                type="date"
                label="From Date"
                InputLabelProps={{ shrink: true }}
                value={from}
                onChange={(e) => setFrom(e.target.value)}
              />
            </Grid>
            <Grid item xs={12} sm={6} md={2.4}>
              <TextField
                fullWidth
                size="small"
                type="date"
                label="To Date"
                InputLabelProps={{ shrink: true }}
                value={to}
                onChange={(e) => setTo(e.target.value)}
              />
            </Grid>
          </Grid>
          <Box display="flex" justifyContent="flex-end" mt={2} gap={1}>
            <Button
              variant="outlined"
              size="small"
              onClick={() => {
                setEntityType('');
                setAction('');
                setPerformedBy('');
                setFrom('');
                setTo('');
                setPage(0);
              }}
            >
              Reset
            </Button>
            <Button
              variant="contained"
              size="small"
              startIcon={<RefreshIcon />}
              onClick={() => {
                setPage(0);
                fetchLogs();
              }}
            >
              Apply Filter
            </Button>
          </Box>
        </CardContent>
      </Card>

      {/* Audit Log Table */}
      <Card>
        <CardContent sx={{ p: 0 }}>
          {loading ? (
            <Box p={4}>
              <LoadingSpinner message="Querying tamper-evident ledger..." />
            </Box>
          ) : logs.length === 0 ? (
            <Typography variant="body2" color="text.secondary" textAlign="center" py={6}>
              No audit records found matching criteria.
            </Typography>
          ) : (
            <>
              <Table size="small">
                <TableHead>
                  <TableRow sx={{ backgroundColor: 'grey.100' }}>
                    <TableCell fontWeight={600}>Timestamp</TableCell>
                    <TableCell fontWeight={600}>Entity</TableCell>
                    <TableCell fontWeight={600}>Action</TableCell>
                    <TableCell fontWeight={600}>Performed By</TableCell>
                    <TableCell fontWeight={600}>IP Address</TableCell>
                    <TableCell fontWeight={600}>Current Hash (SHA-256)</TableCell>
                    <TableCell align="center" fontWeight={600}>Details</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {logs.map((l) => (
                    <TableRow key={l.id} hover>
                      <TableCell sx={{ whiteSpace: 'nowrap' }}>
                        {new Date(l.performedAt || l.timestamp || '').toLocaleString()}
                      </TableCell>
                      <TableCell>
                        <Chip label={l.entityType} size="small" variant="outlined" />
                      </TableCell>
                      <TableCell>
                        <Typography variant="body2" fontWeight={500}>
                          {l.action}
                        </Typography>
                      </TableCell>
                      <TableCell>{l.performedBy || l.performedByName || 'System'}</TableCell>
                      <TableCell sx={{ fontFamily: 'monospace', fontSize: '0.8rem' }}>
                        {l.ipAddress || '—'}
                      </TableCell>
                      <TableCell sx={{ fontFamily: 'monospace', fontSize: '0.75rem', color: 'primary.main' }}>
                        {l.currentHash ? `${l.currentHash.substring(0, 16)}...` : '—'}
                      </TableCell>
                      <TableCell align="center">
                        <Tooltip title="View Before/After & Cryptographic Proof">
                          <IconButton size="small" onClick={() => setSelectedLog(l)}>
                            <VisibilityIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
              <TablePagination
                rowsPerPageOptions={[10, 15, 25, 50]}
                component="div"
                count={totalElements}
                rowsPerPage={rowsPerPage}
                page={page}
                onPageChange={(_, newPage) => setPage(newPage)}
                onRowsPerPageChange={(e) => {
                  setRowsPerPage(parseInt(e.target.value, 10));
                  setPage(0);
                }}
              />
            </>
          )}
        </CardContent>
      </Card>

      {/* Details Dialog */}
      <Dialog open={!!selectedLog} onClose={() => setSelectedLog(null)} maxWidth="md" fullWidth>
        <DialogTitle>Audit Record Details & Cryptographic Chain Proof</DialogTitle>
        <DialogContent dividers>
          {selectedLog && (
            <Box display="flex" flexDirection="column" gap={2}>
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">Record ID</Typography>
                  <Typography variant="body2" fontFamily="monospace">{selectedLog.id}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">Timestamp</Typography>
                  <Typography variant="body2">
                    {new Date(selectedLog.performedAt || selectedLog.timestamp || '').toLocaleString()}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">Entity</Typography>
                  <Typography variant="body2">{selectedLog.entityType} ({String(selectedLog.entityId)})</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">Action</Typography>
                  <Typography variant="body2" fontWeight={600}>{selectedLog.action}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">Performed By</Typography>
                  <Typography variant="body2">{selectedLog.performedBy || selectedLog.performedByName}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="caption" color="text.secondary">IP Address / User Agent</Typography>
                  <Typography variant="body2">{selectedLog.ipAddress || '—'} {selectedLog.userAgent ? `(${selectedLog.userAgent})` : ''}</Typography>
                </Grid>
              </Grid>

              <Box sx={{ backgroundColor: 'grey.50', p: 2, borderRadius: 1 }}>
                <Typography variant="subtitle2" gutterBottom>Cryptographic Hash Chain</Typography>
                <Typography variant="caption" color="text.secondary">Previous Hash:</Typography>
                <Typography variant="body2" fontFamily="monospace" sx={{ wordBreak: 'break-all' }} color="text.secondary">
                  {selectedLog.previousHash || 'GENESIS'}
                </Typography>
                <Typography variant="caption" color="text.secondary" mt={1} display="block">Current Hash (SHA-256):</Typography>
                <Typography variant="body2" fontFamily="monospace" sx={{ wordBreak: 'break-all' }} color="primary.main" fontWeight={600}>
                  {selectedLog.currentHash || '—'}
                </Typography>
              </Box>

              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Typography variant="subtitle2" gutterBottom>Old Value</Typography>
                  <Box sx={{ p: 1, backgroundColor: 'grey.100', borderRadius: 1, minHeight: 80, maxHeight: 200, overflow: 'auto' }}>
                    <Typography variant="body2" component="pre" sx={{ margin: 0, fontSize: '0.8rem', whiteSpace: 'pre-wrap' }}>
                      {selectedLog.oldValue || 'None'}
                    </Typography>
                  </Box>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="subtitle2" gutterBottom>New Value</Typography>
                  <Box sx={{ p: 1, backgroundColor: 'grey.100', borderRadius: 1, minHeight: 80, maxHeight: 200, overflow: 'auto' }}>
                    <Typography variant="body2" component="pre" sx={{ margin: 0, fontSize: '0.8rem', whiteSpace: 'pre-wrap' }}>
                      {selectedLog.newValue || 'None'}
                    </Typography>
                  </Box>
                </Grid>
              </Grid>

              {selectedLog.remarks && (
                <Box>
                  <Typography variant="subtitle2">Remarks</Typography>
                  <Typography variant="body2">{selectedLog.remarks}</Typography>
                </Box>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setSelectedLog(null)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
