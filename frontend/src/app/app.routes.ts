import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

export const appRoutes: Routes = [
    {
        path: '',
        redirectTo: '/dashboard',
        pathMatch: 'full'
    },
    {
        path: 'login',
        loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule)
    },
    {
        path: 'dashboard',
        canActivate: [AuthGuard],
        loadChildren: () => import('./features/dashboard/dashboard.module').then(m => m.DashboardModule)
    },
    {
        path: 'hosts',
        canActivate: [AuthGuard],
        loadChildren: () => import('./features/hosts/hosts.module').then(m => m.HostsModule)
    },
    {
        path: 'services',
        canActivate: [AuthGuard],
        loadChildren: () => import('./features/services/services.module').then(m => m.ServicesModule)
    },
    {
        path: 'alerts',
        canActivate: [AuthGuard],
        loadChildren: () => import('./features/alerts/alerts.module').then(m => m.AlertsModule)
    },
    {
        path: 'metrics',
        canActivate: [AuthGuard],
        loadChildren: () => import('./features/metrics/metrics.module').then(m => m.MetricsModule)
    },
    {
        path: 'settings',
        canActivate: [AuthGuard],
        loadChildren: () => import('./features/settings/settings.module').then(m => m.SettingsModule)
    },
    {
        path: '**',
        redirectTo: '/dashboard'
    }
];
