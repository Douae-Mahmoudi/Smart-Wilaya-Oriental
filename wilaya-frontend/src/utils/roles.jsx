export const ROLES = {
  ADMIN: 'ADMIN',
  AGENT: 'AGENT',
  SUPERVISEUR: 'SUPERVISEUR',
};

export function getDashboardPath(roles = []) {
  if (roles.includes(ROLES.ADMIN)) return '/admin/dashboard';
  if (roles.includes(ROLES.SUPERVISEUR)) return '/superviseur/dashboard';
  if (roles.includes(ROLES.AGENT)) return '/agent/dashboard';
  return '/'; 
}