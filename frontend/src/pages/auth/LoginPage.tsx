import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  Alert,
  CircularProgress,
  Container,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../../api/auth';
import { useAuth } from '../../contexts/AuthContext';

export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const res = await authApi.login({ email, password });
      if (res.success && res.data) {
        login(res.data.accessToken, res.data.refreshToken, res.data.user, res.data.permissions);
        navigate('/dashboard');
      } else {
        setError(res.message || 'Login failed');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Invalid email or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #0D47A1 0%, #1976D2 50%, #42A5F5 100%)',
        p: 2,
      }}
    >
      <Container maxWidth="xs">
        <Card sx={{ p: 2, boxShadow: 6, borderRadius: 2 }}>
          <CardContent>
            <Box textAlign="center" mb={3}>
              <Typography variant="h5" component="h1" fontWeight={700} color="primary.main">
                AICTE Workflow Portal
              </Typography>
              <Typography variant="body2" color="text.secondary" mt={0.5}>
                Human-Supervised Approval System
              </Typography>
            </Box>

            {error && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {error}
              </Alert>
            )}

            <form onSubmit={handleSubmit}>
              <TextField
                fullWidth
                label="Email Address"
                type="email"
                variant="outlined"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                sx={{ mb: 2 }}
                autoComplete="email"
              />
              <TextField
                fullWidth
                label="Password"
                type="password"
                variant="outlined"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                sx={{ mb: 3 }}
                autoComplete="current-password"
              />
              <Button
                fullWidth
                variant="contained"
                size="large"
                type="submit"
                disabled={loading}
                sx={{ py: 1.2, fontWeight: 600 }}
              >
                {loading ? <CircularProgress size={24} color="inherit" /> : 'Sign In'}
              </Button>
            </form>

            <Box mt={3} p={1.5} bgcolor="grey.50" borderRadius={1} border="1px dashed #CCC">
              <Typography variant="caption" display="block" fontWeight={600} color="text.secondary">
                Demo Accounts:
              </Typography>
              <Typography variant="caption" display="block" color="text.secondary">
                • Admin: admin@aicte.gov.in / Admin@123
              </Typography>
              <Typography variant="caption" display="block" color="text.secondary">
                • Officer: officer@aicte.gov.in / Officer@123
              </Typography>
              <Typography variant="caption" display="block" color="text.secondary">
                • Applicant: applicant@demo.com / Applicant@123
              </Typography>
            </Box>
          </CardContent>
        </Card>
      </Container>
    </Box>
  );
};
