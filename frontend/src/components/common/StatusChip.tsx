import React from 'react';
import { Chip, ChipProps } from '@mui/material';

interface StatusChipProps {
  status?: string;
  size?: 'small' | 'medium';
}

export const StatusChip: React.FC<StatusChipProps> = ({ status = 'UNKNOWN', size = 'small' }) => {
  let color: ChipProps['color'] = 'default';

  switch (status.toUpperCase()) {
    case 'APPROVED':
    case 'RESOLVED':
    case 'VERIFIED':
    case 'ACTIVE':
    case 'FULFILLED':
      color = 'success';
      break;
    case 'REJECTED':
    case 'CANCELLED':
    case 'OVERDUE':
      color = 'error';
      break;
    case 'DRAFT':
    case 'SUBMITTED':
    case 'UNDER_REVIEW':
    case 'PENDING':
    case 'DATE_PROPOSED':
      color = 'info';
      break;
    case 'DEFICIENCY_RAISED':
    case 'RESCHEDULED':
    case 'LEVEL_1':
    case 'LEVEL_2':
    case 'LEVEL_3':
      color = 'warning';
      break;
    default:
      color = 'default';
  }

  return (
    <Chip
      label={status.replace(/_/g, ' ')}
      color={color}
      size={size}
      variant="filled"
      sx={{ fontWeight: 600, fontSize: '0.75rem' }}
    />
  );
};
