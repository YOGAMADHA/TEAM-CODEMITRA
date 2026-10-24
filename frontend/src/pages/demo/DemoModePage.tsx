import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Alert,
  Divider,
  Grid,
  Paper,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Chip,
} from '@mui/material';
import InfoIcon from '@mui/icons-material/Info';
import SecurityIcon from '@mui/icons-material/Security';
import SmartToyIcon from '@mui/icons-material/SmartToy';
import GavelIcon from '@mui/icons-material/Gavel';
import { PageHeader } from '../../components/common/PageHeader';

export const DemoModePage: React.FC = () => {
  return (
    <Box>
      <PageHeader
        title="Demonstration Prototype & System Disclaimers"
        subtitle="Mandatory academic transparency statements, security model, and AI governance policies"
      />

      <Alert severity="warning" sx={{ mb: 3 }} icon={<InfoIcon fontSize="large" />}>
        <Typography variant="subtitle1" fontWeight={700} gutterBottom>
          Statutory Notice & Academic Prototype Disclaimer
        </Typography>
        <Typography variant="body2">
          This is a prototype workflow-management system for academic demonstration. It supports human-supervised administrative processing and does not replace official AICTE systems, statutory scrutiny, inspection, committee review, or competent-authority decisions.
        </Typography>
      </Alert>

      <Grid container spacing={3}>
        {/* Core Principles */}
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={1} mb={2}>
                <GavelIcon color="primary" />
                <Typography variant="h6" fontWeight={600}>
                  Administrative Integrity & Authority Rules
                </Typography>
              </Box>
              <Typography variant="body2" paragraph>
                The system strictly adheres to administrative process principles:
              </Typography>
              <ul>
                <li>
                  <Typography variant="body2">
                    <strong>Zero Automated Statutory Approvals:</strong> No algorithm, AI model, or automated script can issue approvals, reject applications, or declare compliance.
                  </Typography>
                </li>
                <li>
                  <Typography variant="body2">
                    <strong>Mandatory Human Officer Action:</strong> All stage transitions, document verifications, deficiency reviews, and EVC decisions require authenticated human officer confirmation.
                  </Typography>
                </li>
                <li>
                  <Typography variant="body2">
                    <strong>Segregation of Duties:</strong> Officers cannot approve applications they scrutinized; applicants cannot view other institutions; auditors have strictly read-only access.
                  </Typography>
                </li>
                <li>
                  <Typography variant="body2">
                    <strong>Immutable Audit Chain:</strong> Every event is cryptographically sealed in a SHA-256 hash-chain ledger that detects any retroactive tampering.
                  </Typography>
                </li>
              </ul>
            </CardContent>
          </Card>
        </Grid>

        {/* AI Governance Policy */}
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={1} mb={2}>
                <SmartToyIcon color="primary" />
                <Typography variant="h6" fontWeight={600}>
                  AI-Assisted Features Governance
                </Typography>
              </Box>
              <Alert severity="info" sx={{ mb: 2 }}>
                <Typography variant="caption" fontWeight={600}>
                  Mandatory Warning on All AI Features:
                </Typography>
                <Typography variant="caption" display="block">
                  &ldquo;AI-assisted information only. Human officer confirmation is required. This output is not an approval, rejection, inspection result, or statutory decision.&rdquo;
                </Typography>
              </Alert>
              <Typography variant="body2" paragraph>
                AI features serve solely as productivity accelerators:
              </Typography>
              <ul>
                <li>
                  <Typography variant="body2">
                    <strong>OCR Assistance:</strong> Extracts text suggestions (certificate numbers, dates) for officer verification.
                  </Typography>
                </li>
                <li>
                  <Typography variant="body2">
                    <strong>Document Classification:</strong> Suggests potential document types; officer manually verifies category.
                  </Typography>
                </li>
                <li>
                  <Typography variant="body2">
                    <strong>Missing Document Cross-Check:</strong> Heuristically flags checklist gaps for officer inspection.
                  </Typography>
                </li>
                <li>
                  <Typography variant="body2">
                    <strong>Parameterized Natural Search:</strong> Translates natural language queries into safe parameterized SQL queries — never raw AI-generated SQL.
                  </Typography>
                </li>
              </ul>
            </CardContent>
          </Card>
        </Grid>

        {/* Demo Credentials Table */}
        <Grid item xs={12}>
          <Paper sx={{ p: 3 }}>
            <Box display="flex" alignItems="center" gap={1} mb={2}>
              <SecurityIcon color="primary" />
              <Typography variant="h6" fontWeight={600}>
                Demonstration Pre-Seeded Accounts
              </Typography>
            </Box>
            <Typography variant="body2" color="text.secondary" mb={2}>
              The system includes pre-seeded roles to demonstrate multi-stakeholder approval workflows. All demo accounts use the standard demonstration password: <code>Password@123</code>
            </Typography>

            <Table size="small">
              <TableHead>
                <TableRow sx={{ backgroundColor: 'grey.100' }}>
                  <TableCell fontWeight={600}>Username</TableCell>
                  <TableCell fontWeight={600}>Role</TableCell>
                  <TableCell fontWeight={600}>Institution</TableCell>
                  <TableCell fontWeight={600}>Purpose in Demo</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                <TableRow>
                  <TableCell><strong>admin</strong></TableCell>
                  <TableCell><Chip label="ADMIN" size="small" color="error" /></TableCell>
                  <TableCell>AICTE Headquarters</TableCell>
                  <TableCell>Full administrative oversight, role assignments, audit inspection</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell><strong>scrutiny_officer</strong></TableCell>
                  <TableCell><Chip label="SCRUTINY_OFFICER" size="small" color="primary" /></TableCell>
                  <TableCell>Regional Office</TableCell>
                  <TableCell>Document verification, raising deficiencies, initial scrutiny</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell><strong>evc_coordinator</strong></TableCell>
                  <TableCell><Chip label="EVC_COORDINATOR" size="small" color="secondary" /></TableCell>
                  <TableCell>Regional Office</TableCell>
                  <TableCell>Expert Visit Committee scheduling, member roster, visit tracking</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell><strong>auditor</strong></TableCell>
                  <TableCell><Chip label="AUDITOR" size="small" color="warning" /></TableCell>
                  <TableCell>Independent Vigilance</TableCell>
                  <TableCell>Read-only access, SHA-256 hash-chain verification, compliance logs</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell><strong>abc_applicant</strong></TableCell>
                  <TableCell><Chip label="APPLICANT" size="small" variant="outlined" /></TableCell>
                  <TableCell>ABC Engineering College</TableCell>
                  <TableCell>New Institution Application (AICTE-2024-001) at Scrutiny stage</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell><strong>xyz_applicant</strong></TableCell>
                  <TableCell><Chip label="APPLICANT" size="small" variant="outlined" /></TableCell>
                  <TableCell>XYZ Polytechnic Institute</TableCell>
                  <TableCell>Extension of Approval (AICTE-2024-002) with Open Deficiency</TableCell>
                </TableRow>
                <TableRow>
                  <TableCell><strong>pqr_applicant</strong></TableCell>
                  <TableCell><Chip label="APPLICANT" size="small" variant="outlined" /></TableCell>
                  <TableCell>PQR Management Institute</TableCell>
                  <TableCell>Increase in Intake (AICTE-2024-003) at EVC Report Pending stage</TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};
