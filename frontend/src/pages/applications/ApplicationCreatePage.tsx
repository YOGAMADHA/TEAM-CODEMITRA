import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  MenuItem,
  Grid,
  Alert,
  CircularProgress,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { PageHeader } from '../../components/common/PageHeader';
import { applicationsApi } from '../../api/applications';
import { institutionsApi } from '../../api/institutions';
import { InstitutionDto } from '../../types';

export const ApplicationCreatePage: React.FC = () => {
  const navigate = useNavigate();
  const [institutions, setInstitutions] = useState<InstitutionDto[]>([]);
  const [institutionId, setInstitutionId] = useState('');
  const [category, setCategory] = useState('New Institution Approval');
  const [academicYear, setAcademicYear] = useState('2026-2027');
  const [remarks, setRemarks] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    institutionsApi.getAll(0, 50).then((res) => {
      if (res.data?.content) {
        setInstitutions(res.data.content);
        if (res.data.content.length > 0) {
          setInstitutionId(res.data.content[0].id);
        }
      }
    });
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const res = await applicationsApi.create({
        institutionId,
        category,
        academicYear,
        remarks,
      });

      if (res.success && res.data) {
        navigate(`/applications/${res.data.id}`);
      } else {
        setError(res.message || 'Failed to create application');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error creating application');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box maxWidth="800px" mx="auto">
      <PageHeader
        title="Create New Application"
        subtitle="Initiate a draft application for institutional approval"
      />

      <Card>
        <CardContent sx={{ p: 4 }}>
          {error && (
            <Alert severity="error" sx={{ mb: 3 }}>
              {error}
            </Alert>
          )}

          <form onSubmit={handleSubmit}>
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <TextField
                  select
                  fullWidth
                  label="Select Institution"
                  value={institutionId}
                  onChange={(e) => setInstitutionId(e.target.value)}
                  required
                >
                  {institutions.map((inst) => (
                    <MenuItem key={inst.id} value={inst.id}>
                      {inst.name} ({inst.code})
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  select
                  fullWidth
                  label="Application Category"
                  value={category}
                  onChange={(e) => setCategory(e.target.value)}
                  required
                >
                  <MenuItem value="New Institution Approval">New Institution Approval</MenuItem>
                  <MenuItem value="Extension or Renewal of Approval">Extension or Renewal of Approval</MenuItem>
                  <MenuItem value="Additional Course or Intake Approval">Additional Course or Intake Approval</MenuItem>
                </TextField>
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Academic Year"
                  value={academicYear}
                  onChange={(e) => setAcademicYear(e.target.value)}
                  required
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  multiline
                  rows={3}
                  label="Remarks / Notes"
                  value={remarks}
                  onChange={(e) => setRemarks(e.target.value)}
                />
              </Grid>

              <Grid item xs={12} display="flex" justifyContent="flex-end" gap={2}>
                <Button variant="outlined" onClick={() => navigate('/applications')}>
                  Cancel
                </Button>
                <Button variant="contained" type="submit" disabled={loading}>
                  {loading ? <CircularProgress size={24} color="inherit" /> : 'Create Draft'}
                </Button>
              </Grid>
            </Grid>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
};
