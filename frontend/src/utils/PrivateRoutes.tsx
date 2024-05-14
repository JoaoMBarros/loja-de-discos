import { useAuth } from '@/hooks/UseAuth'
import { Navigate, Outlet } from 'react-router-dom';

export function PrivateRoutes() {
  const { isAuthenticated, isLoading } = useAuth();
    return isLoading ? <div hidden></div> : isAuthenticated ? <Outlet /> : <Navigate to='/login' />;
}