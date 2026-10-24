import dayjs from 'dayjs';

export function formatDate(date: string | undefined | null): string {
  if (!date) return '—';
  return dayjs(date).format('DD MMM YYYY');
}

export function formatDateTime(date: string | undefined | null): string {
  if (!date) return '—';
  return dayjs(date).format('DD MMM YYYY, hh:mm A');
}

export function formatStage(stage: string): string {
  return stage
    .split('_')
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
    .join(' ');
}

export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

export function truncateText(text: string, maxLength: number): string {
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '…';
}
