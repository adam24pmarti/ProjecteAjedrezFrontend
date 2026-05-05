import { Component, computed, signal } from '@angular/core';
import { ChessReservationComponent } from './chess-reservation.component';

@Component({
  selector: 'app-root',
  host: {
    '[class.light-mode]': 'isLightMode()',
    '[class.dark-mode]': '!isLightMode()'
  },
  imports: [ChessReservationComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App {
  protected readonly title = signal('Ajedrez');
  protected readonly isLightMode = signal(false);
  protected readonly themeButtonLabel = computed(() => this.isLightMode() ? 'oscuro' : 'claro');

  protected readonly toggleColorScheme = () => {
    this.isLightMode.update(value => !value);
  };
}
