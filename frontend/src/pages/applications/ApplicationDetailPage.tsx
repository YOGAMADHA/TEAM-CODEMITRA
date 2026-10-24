import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Tabs,
  Tab,
  Button,
  Grid,
  Divider,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Alert,
} from '@mui/material';
import {
  Send as SubmitIcon,
  Timeline as TimelineIcon,
  ArrowForward as TransitionIcon,
  UploadFile as UploadIcon,
  CheckCircle as VerifyIcon,
  ReportProblem as DeficiencyIcon,
  SmartToy as AiIcon,
  CheckCircleOutline,
} from '@mui/icons-material';
import { useParams, useNavigate } from 'react-router-dom';
import { PageHeader } from '../../components/common/PageHeader';
import { StatusChip } from '../../components/common/StatusChip';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { applicationsApi } from '../../api/applications';
import { documentsApi } from '../../api/documents';
import { deficienciesApi } from '../../api/deficiencies';
import { complianceApi } from '../../api/compliance';
import { aiApi } from '../../api/ai';
import { useAuth } from '../../contexts/AuthContext';
import {
  ApplicationDto,
  ApplicationStageHistoryDto,
  ComplianceItemDto,
  DeficiencyDto,
  DocumentChecklistDto,
} from '../../types';

export const ApplicationDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user, hasPermission } = useAuth();

  const [application, setApplication] = useState<ApplicationDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [tab, setTab] = useState(0);

  // Data for tabs
  const [checklist, setChecklist] = useState<DocumentChecklistDto[]>([]);
  const [deficiencies, setDeficiencies] = useState<DeficiencyDto[]>([]);
  const [compliance, setCompliance] = useState<ComplianceItemDto[]>([]);
  const [timeline, setTimeline] = useState<ApplicationStageHistoryDto[]>([]);

  // Dialogs
  const [transitionOpen, setTransitionOpen] = useState(false);
  const [targetStage, setTargetStage] = useState('');
  const [transitionRemarks, setTransitionRemarks] = useState('');

  const [uploadOpen, setUploadOpen] = useState(false);
  const [selectedReqId, setSelectedReqId] = useState<string | null>(null);
  const [uploadFile, setUploadFile] = useState<File | null>(null);

  const [deficiencyOpen, setDeficiencyOpen] = useState(false);
  const [defCategory, setDefCategory] = useState('');
  const [defDesc, setDefDesc] = useState('');
  const [defAction, setDefAction] = useState('');
  const [defDueDate, setDefDueDate] = useState('');

  const [error, setError] = useState<string | null>(null);

  // AI Assistance state
  const [aiSummary, setAiSummary] = useState<any>(null);
  const [aiMissingDocs, setAiMissingDocs] = useState<any>(null);
  const [aiLoading, setAiLoading] = useState(false);
  const [aiConfirmed, setAiConfirmed] = useState(false);
  const [aiRemarks, setAiRemarks] = useState('');

  const handleFetchAiSummary = async () => {
    if (!id) return;
    setAiLoading(true);
    try {
      const res = await aiApi.getApplicationSummary(id);
      if (res.data) setAiSummary(res.data);
    } catch (e: any) {
      setError(e.response?.data?.message || 'Failed to generate AI summary');
    } finally {
      setAiLoading(false);
    }
  };

  const handleCheckMissingDocs = async () => {
    if (!id) return;
    setAiLoading(true);
    try {
      const res = await aiApi.checkMissingDocuments(id);
      if (res.data) setAiMissingDocs(res.data);
    } catch (e: any) {
      setError(e.response?.data?.message || 'Failed to check missing documents');
    } finally {
      setAiLoading(false);
    }
  };

  const handleConfirmAi = () => {
    setAiConfirmed(true);
  };

  const fetchApplicationData = () => {
    if (!id) return;
    setLoading(true);
    applicationsApi
      .getById(id)
      .then((res) => {
        if (res.data) setApplication(res.data);
      })
      .finally(() => setLoading(false));

    documentsApi.getChecklist(id).then((res) => {
      if (res.data) setChecklist(res.data);
    });

    deficienciesApi.getByApplication(id).then((res) => {
      if (res.data) setDeficiencies(res.data);
    });

    complianceApi.getByApplication(id).then((res) => {
      if (res.data) setCompliance(res.data);
    });

    applicationsApi.getTimeline(id).then((res) => {
      if (res.data) setTimeline(res.data);
    });
  };

  useEffect(() => {
    fetchApplicationData();
  }, [id]);

  const handleSubmit = async () => {
    if (!id) return;
    try {
      await applicationsApi.submit(id);
      fetchApplicationData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error submitting application');
    }
  };

  const handleTransition = async () => {
    if (!id || !targetStage) return;
    try {
      await applicationsApi.transition(id, { targetStage, remarks: transitionRemarks });
      setTransitionOpen(false);
      fetchApplicationData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error updating stage');
    }
  };

  const handleUpload = async () => {
    if (!id || !uploadFile) return;
    try {
      const fd = new FormData();
      fd.append('applicationId', id);
      if (selectedReqId) fd.append('requirementId', selectedReqId);
      fd.append('file', uploadFile);
      await documentsApi.upload(fd);
      setUploadOpen(false);
      setUploadFile(null);
      fetchApplicationData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Upload failed');
    }
  };

  const handleRaiseDeficiency = async () => {
    if (!id) return;
    try {
      await deficienciesApi.create({
        applicationId: id,
        category: defCategory,
        description: defDesc,
        correctiveAction: defAction,
        dueDate: defDueDate ? `${defDueDate}T17:00:00` : new Date().toISOString(),
      });
      setDeficiencyOpen(false);
      fetchApplicationData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to raise deficiency');
    }
  };

  if (loading || !application) {
    return <LoadingSpinner message="Loading application details..." />;
  }

  return (
    <Box>
      <PageHeader
        title={`Application: ${application.applicationNumber}`}
        subtitle={`${application.institutionName || 'Institution'} | ${application.category}`}
        action={
          <Box display="flex" gap={1.5}>
            {application.currentStage === 'DRAFT' && hasPermission('APPLICATION_SUBMIT') && (
              <Button variant="contained" color="primary" startIcon={<SubmitIcon />} onClick={handleSubmit}>
                Submit Application
              </Button>
            )}

            {hasPermission('APPLICATION_ROUTE') && application.currentStage !== 'DRAFT' && (
              <Button
                variant="contained"
                color="secondary"
                startIcon={<TransitionIcon />}
                onClick={() => setTransitionOpen(true)}
              >
                Transition Stage
              </Button>
            )}

            {hasPermission('DEFICIENCY_CREATE') && (
              <Button
                variant="outlined"
                color="warning"
                startIcon={<DeficiencyIcon />}
                onClick={() => setDeficiencyOpen(true)}
              >
                Raise Deficiency
              </Button>
            )}
          </Box>
        }
      />

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* Header Info Card */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Grid container spacing={2} alignItems="center">
            <Grid item xs={12} sm={3}>
              <Typography variant="caption" color="text.secondary">CURRENT STAGE</Typography>
              <Box mt={0.5}><StatusChip status={application.currentStage} size="medium" /></Box>
            </Grid>
            <Grid item xs={12} sm={3}>
              <Typography variant="caption" color="text.secondary">APPLICATION STATUS</Typography>
              <Box mt={0.5}><StatusChip status={application.status} size="medium" /></Box>
            </Grid>
            <Grid item xs={12} sm={3}>
              <Typography variant="caption" color="text.secondary">ACADEMIC YEAR</Typography>
              <Typography variant="body1" fontWeight={600}>{application.academicYear}</Typography>
            </Grid>
            <Grid item xs={12} sm={3}>
              <Typography variant="caption" color="text.secondary">ASSIGNED OFFICER</Typography>
              <Typography variant="body1" fontWeight={600}>
                {application.assignedOfficerName || 'Not Assigned'}
              </Typography>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Tabs */}
      <Card>
        <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tab label="Overview" />
          <Tab label={`Documents (${checklist.length})`} />
          <Tab label={`Deficiencies (${deficiencies.length})`} />
          <Tab label={`Compliance (${compliance.length})`} />
          <Tab label={`Timeline (${timeline.length})`} />
          <Tab label="AI Assistance" />
        </Tabs>

        {/* Tab 0: Overview */}
        {tab === 0 && (
          <CardContent sx={{ p: 3 }}>
            <Grid container spacing={3}>
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2" color="text.secondary">Application Number</Typography>
                <Typography variant="body1" fontWeight={600}>{application.applicationNumber}</Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2" color="text.secondary">Institution Name</Typography>
                <Typography variant="body1">{application.institutionName}</Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2" color="text.secondary">Category</Typography>
                <Typography variant="body1">{application.category}</Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2" color="text.secondary">Submission Timestamp</Typography>
                <Typography variant="body1">
                  {application.submittedAt ? new Date(application.submittedAt).toLocaleString() : 'Not yet submitted'}
                </Typography>
              </Grid>
              <Grid item xs={12}>
                <Typography variant="subtitle2" color="text.secondary">Remarks / Administrative Notes</Typography>
                <Typography variant="body2">{application.remarks || 'No remarks recorded.'}</Typography>
              </Grid>
            </Grid>
          </CardContent>
        )}

        {/* Tab 1: Documents Checklist */}
        {tab === 1 && (
          <CardContent>
            <Box display="flex" justifyContent="space-between" mb={2}>
              <Typography variant="subtitle1" fontWeight={600}>Mandatory Document Checklist</Typography>
              {hasPermission('DOCUMENT_UPLOAD') && (
                <Button
                  size="small"
                  variant="contained"
                  startIcon={<UploadIcon />}
                  onClick={() => {
                    setSelectedReqId(null);
                    setUploadOpen(true);
                  }}
                >
                  Upload Supporting Doc
                </Button>
              )}
            </Box>
            <Table size="small">
              <TableHead>
                <TableRow sx={{ backgroundColor: 'grey.50' }}>
                  <TableCell fontWeight={600}>Requirement Name</TableCell>
                  <TableCell fontWeight={600}>Mandatory</TableCell>
                  <TableCell fontWeight={600}>Allowed Types</TableCell>
                  <TableCell fontWeight={600}>Uploaded File</TableCell>
                  <TableCell fontWeight={600}>Verification Status</TableCell>
                  <TableCell align="right" fontWeight={600}>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {checklist.map((req) => {
                  const doc = req.documents?.[0];
                  return (
                    <TableRow key={req.requirementId} hover>
                      <TableCell fontWeight={600}>{req.requirementName}</TableCell>
                      <TableCell>{req.mandatory ? 'Yes' : 'Optional'}</TableCell>
                      <TableCell>{req.allowedFileTypes}</TableCell>
                      <TableCell>{doc?.fileName || '—'}</TableCell>
                      <TableCell>
                        <StatusChip status={doc?.verificationStatus || 'MISSING'} />
                      </TableCell>
                      <TableCell align="right">
                        {doc ? (
                          <Button size="small" onClick={() => documentsApi.download(doc.id)}>
                            Download
                          </Button>
                        ) : hasPermission('DOCUMENT_UPLOAD') ? (
                          <Button
                            size="small"
                            variant="outlined"
                            onClick={() => {
                              setSelectedReqId(req.requirementId);
                              setUploadOpen(true);
                            }}
                          >
                            Upload
                          </Button>
                        ) : null}
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </CardContent>
        )}

        {/* Tab 2: Deficiencies */}
        {tab === 2 && (
          <CardContent>
            {deficiencies.length === 0 ? (
              <Typography variant="body2" color="text.secondary" py={3} textAlign="center">
                No deficiencies raised on this application.
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
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </CardContent>
        )}

        {/* Tab 3: Compliance */}
        {tab === 3 && (
          <CardContent>
            {compliance.length === 0 ? (
              <Typography variant="body2" color="text.secondary" py={3} textAlign="center">
                No compliance items registered for this application.
              </Typography>
            ) : (
              <Table size="small">
                <TableHead>
                  <TableRow sx={{ backgroundColor: 'grey.50' }}>
                    <TableCell fontWeight={600}>Category</TableCell>
                    <TableCell fontWeight={600}>Description</TableCell>
                    <TableCell fontWeight={600}>Responsible Party</TableCell>
                    <TableCell fontWeight={600}>Due Date</TableCell>
                    <TableCell fontWeight={600}>Status</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {compliance.map((c) => (
                    <TableRow key={c.id} hover>
                      <TableCell fontWeight={600}>{c.category}</TableCell>
                      <TableCell>{c.description}</TableCell>
                      <TableCell>{c.responsibleParty}</TableCell>
                      <TableCell>{c.dueDate ? new Date(c.dueDate).toLocaleDateString() : '—'}</TableCell>
                      <TableCell><StatusChip status={c.status} /></TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </CardContent>
        )}

        {/* Tab 4: Timeline */}
        {tab === 4 && (
          <CardContent sx={{ p: 3 }}>
            {timeline.length === 0 ? (
              <Typography variant="body2" color="text.secondary" textAlign="center" py={3}>
                No timeline events recorded.
              </Typography>
            ) : (
              <Box>
                {timeline.map((h, i) => (
                  <Box key={h.id} display="flex" mb={2}>
                    <Box mr={2} display="flex" flexDirection="column" alignItems="center">
                      <TimelineIcon color="primary" fontSize="small" />
                      {i < timeline.length - 1 && (
                        <Box sx={{ width: 2, flexGrow: 1, backgroundColor: 'grey.300', my: 0.5 }} />
                      )}
                    </Box>
                    <Box pb={2}>
                      <Typography variant="subtitle2" fontWeight={700}>
                        {h.stage.replace(/_/g, ' ')}
                      </Typography>
                      <Typography variant="caption" color="text.secondary" display="block">
                        Entered: {new Date(h.stageEnteredAt).toLocaleString()} by {h.performedByName}
                      </Typography>
                      {h.remarks && (
                        <Typography variant="body2" color="text.secondary" mt={0.5}>
                          Remarks: {h.remarks}
                        </Typography>
                      )}
                    </Box>
                  </Box>
                ))}
              </Box>
            )}
          </CardContent>
        )}

        {/* Tab 5: AI Assistance */}
        {tab === 5 && (
          <CardContent sx={{ p: 3 }}>
            {/* Mandatory Statutory Warning Banner */}
            <Alert severity="warning" sx={{ mb: 3 }} icon={<AiIcon />}>
              <Typography variant="subtitle2" fontWeight={700}>
                AI-Assisted Information Only
              </Typography>
              <Typography variant="body2">
                Human officer confirmation is required. This output is not an approval, rejection, inspection result, or statutory decision.
              </Typography>
            </Alert>

            <Grid container spacing={3}>
              <Grid item xs={12} md={6}>
                <Card variant="outlined" sx={{ p: 2, height: '100%' }}>
                  <Typography variant="subtitle1" fontWeight={600} gutterBottom>
                    Application Status Summary
                  </Typography>
                  <Typography variant="body2" color="text.secondary" mb={2}>
                    Synthesizes institution records, uploaded documents, open deficiencies, deadlines, and timeline events.
                  </Typography>
                  <Button
                    variant="contained"
                    size="small"
                    startIcon={<AiIcon />}
                    onClick={handleFetchAiSummary}
                    disabled={aiLoading}
                  >
                    {aiLoading ? 'Analyzing...' : 'Generate AI Summary'}
                  </Button>

                  {aiSummary && (
                    <Box mt={2} p={2} sx={{ backgroundColor: 'grey.50', borderRadius: 1 }}>
                      <Typography variant="caption" color="primary" fontWeight={600} display="block">
                        Model: {aiSummary.modelName || 'MockAiService'}
                      </Typography>
                      <Typography variant="body2" mt={1}><strong>Overview:</strong> {aiSummary.summary?.overview}</Typography>
                      <Typography variant="body2" mt={1}><strong>Documents:</strong> {aiSummary.summary?.documentStatus}</Typography>
                      <Typography variant="body2" mt={1}><strong>Deficiencies:</strong> {aiSummary.summary?.openDeficiencies}</Typography>
                      <Typography variant="body2" mt={1}><strong>Deadlines:</strong> {aiSummary.summary?.deadlineStatus}</Typography>
                      <Typography variant="body2" mt={1} color="text.secondary">
                        <em>{aiSummary.summary?.recommendation}</em>
                      </Typography>
                    </Box>
                  )}
                </Card>
              </Grid>

              <Grid item xs={12} md={6}>
                <Card variant="outlined" sx={{ p: 2, height: '100%' }}>
                  <Typography variant="subtitle1" fontWeight={600} gutterBottom>
                    Missing Documents Cross-Check
                  </Typography>
                  <Typography variant="body2" color="text.secondary" mb={2}>
                    Compares uploaded files against category document requirements to suggest potential missing items.
                  </Typography>
                  <Button
                    variant="outlined"
                    size="small"
                    startIcon={<AiIcon />}
                    onClick={handleCheckMissingDocs}
                    disabled={aiLoading}
                  >
                    {aiLoading ? 'Checking...' : 'Check Missing Documents'}
                  </Button>

                  {aiMissingDocs && (
                    <Box mt={2} p={2} sx={{ backgroundColor: 'grey.50', borderRadius: 1 }}>
                      <Typography variant="caption" color="primary" fontWeight={600} display="block">
                        Model: {aiMissingDocs.modelName || 'MockAiService'}
                      </Typography>
                      <Typography variant="body2" mt={1} fontWeight={600}>Potentially Missing Items:</Typography>
                      <ul>
                        {aiMissingDocs.possiblyMissing?.map((item: any, idx: number) => (
                          <li key={idx}>
                            <Typography variant="body2">
                              <strong>{item.requirementName}:</strong> {item.reason}
                            </Typography>
                          </li>
                        ))}
                      </ul>
                      <Typography variant="caption" color="text.secondary">
                        {aiMissingDocs.note}
                      </Typography>
                    </Box>
                  )}
                </Card>
              </Grid>

              {/* Officer Confirmation Section */}
              <Grid item xs={12}>
                <Card variant="outlined" sx={{ p: 2, backgroundColor: '#FAFAFA' }}>
                  <Typography variant="subtitle1" fontWeight={600} gutterBottom>
                    Human Officer Review & Confirmation
                  </Typography>
                  <Typography variant="body2" color="text.secondary" mb={2}>
                    As per administrative protocol, an authorized officer must verify and sign off on any AI assistive output before taking administrative actions.
                  </Typography>

                  {aiConfirmed ? (
                    <Alert severity="success" icon={<CheckCircleOutline />}>
                      AI-assisted analysis confirmed and signed off by officer. Recorded in immutable audit log.
                    </Alert>
                  ) : (
                    <Box display="flex" gap={2} alignItems="center" flexWrap="wrap">
                      <TextField
                        size="small"
                        label="Officer Confirmation Remarks"
                        placeholder="e.g. Verified AI observations against hardcopy files; verified fire safety requirement"
                        value={aiRemarks}
                        onChange={(e) => setAiRemarks(e.target.value)}
                        sx={{ flexGrow: 1, minWidth: 300 }}
                      />
                      <Button
                        variant="contained"
                        color="success"
                        startIcon={<VerifyIcon />}
                        onClick={handleConfirmAi}
                        disabled={!aiSummary && !aiMissingDocs}
                      >
                        Sign-off & Confirm Analysis
                      </Button>
                    </Box>
                  )}
                </Card>
              </Grid>
            </Grid>
          </CardContent>
        )}
      </Card>

      {/* Transition Dialog */}
      <Dialog open={transitionOpen} onClose={() => setTransitionOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle>Transition Application Stage</DialogTitle>
        <DialogContent>
          <TextField
            select
            fullWidth
            label="Next Stage"
            value={targetStage}
            onChange={(e) => setTargetStage(e.target.value)}
            sx={{ mt: 2, mb: 2 }}
          >
            <MenuItem value="INITIAL_VALIDATION">INITIAL VALIDATION</MenuItem>
            <MenuItem value="SCRUTINY">SCRUTINY</MenuItem>
            <MenuItem value="DEFICIENCY_RAISED">DEFICIENCY RAISED</MenuItem>
            <MenuItem value="RESCRUTINY">RESCRUTINY</MenuItem>
            <MenuItem value="EVC_REQUIRED">EVC REQUIRED</MenuItem>
            <MenuItem value="EVC_SCHEDULING">EVC SCHEDULING</MenuItem>
            <MenuItem value="COMMITTEE_REVIEW">COMMITTEE REVIEW</MenuItem>
            <MenuItem value="COMPETENT_AUTHORITY_DECISION">COMPETENT AUTHORITY DECISION</MenuItem>
            <MenuItem value="APPROVED">APPROVED</MenuItem>
            <MenuItem value="REJECTED">REJECTED</MenuItem>
          </TextField>
          <TextField
            fullWidth
            multiline
            rows={3}
            label="Officer Remarks"
            value={transitionRemarks}
            onChange={(e) => setTransitionRemarks(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setTransitionOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleTransition} disabled={!targetStage}>
            Transition
          </Button>
        </DialogActions>
      </Dialog>

      {/* Document Upload Dialog */}
      <Dialog open={uploadOpen} onClose={() => setUploadOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle>Upload Document</DialogTitle>
        <DialogContent>
          <Box mt={2}>
            <input
              type="file"
              onChange={(e) => {
                if (e.target.files && e.target.files[0]) {
                  setUploadFile(e.target.files[0]);
                }
              }}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setUploadOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleUpload} disabled={!uploadFile}>
            Upload
          </Button>
        </DialogActions>
      </Dialog>

      {/* Raise Deficiency Dialog */}
      <Dialog open={deficiencyOpen} onClose={() => setDeficiencyOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Raise Deficiency</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="Category"
            value={defCategory}
            onChange={(e) => setDefCategory(e.target.value)}
            sx={{ mt: 2, mb: 2 }}
            placeholder="e.g. Land Document Incomplete, Building Plan Unapproved"
          />
          <TextField
            fullWidth
            multiline
            rows={3}
            label="Deficiency Description"
            value={defDesc}
            onChange={(e) => setDefDesc(e.target.value)}
            sx={{ mb: 2 }}
          />
          <TextField
            fullWidth
            multiline
            rows={2}
            label="Required Corrective Action"
            value={defAction}
            onChange={(e) => setDefAction(e.target.value)}
            sx={{ mb: 2 }}
          />
          <TextField
            fullWidth
            type="date"
            label="Response Due Date"
            InputLabelProps={{ shrink: true }}
            value={defDueDate}
            onChange={(e) => setDefDueDate(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeficiencyOpen(false)}>Cancel</Button>
          <Button variant="contained" color="warning" onClick={handleRaiseDeficiency} disabled={!defCategory || !defDesc}>
            Raise Deficiency
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
