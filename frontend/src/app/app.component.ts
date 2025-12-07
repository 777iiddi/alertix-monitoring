import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <div class="app-container">
      <header class="app-header">
        <h1>{{ title }}</h1>
      </header>
      
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>

      <footer class="app-footer">
        <p>&copy; 2025 Alertix Monitoring. All rights reserved.</p>
      </footer>
    </div>
  `,
  styles: [`
    .app-container {
      display: flex;
      flex-direction: column;
      min-height: 100vh;
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
    }

    .app-header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 1rem 2rem;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .app-header h1 {
      margin: 0;
      font-size: 1.5rem;
      font-weight: 600;
    }

    .main-content {
      flex: 1;
      padding: 2rem;
      background-color: #f5f7fa;
    }

    .app-footer {
      background-color: #2d3748;
      color: #cbd5e0;
      padding: 1rem 2rem;
      text-align: center;
    }

    .app-footer p {
      margin: 0;
      font-size: 0.875rem;
    }
  `]
})
export class AppComponent {
  title = 'Alertix Monitoring';
}
