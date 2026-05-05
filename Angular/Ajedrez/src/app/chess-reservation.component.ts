import { CommonModule } from '@angular/common';
import { Component, computed, inject, output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

export interface ChessReservation {
  id: string;
  playerName: string;
  dateTime: string;
  board: string;
  duration: string;
  notes: string;
  createdAt: string;
}

@Component({
  selector: 'app-chess-reservation-form',
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <section class="reservation-form" aria-label="Formulario de reserva de ajedrez">
      <h2>Reserva tu tablero</h2>
      <form [formGroup]="form" (ngSubmit)="submit()">
        <label for="playerName">Nombre del jugador</label>
        <input
          id="playerName"
          type="text"
          formControlName="playerName"
          placeholder="Ej. Alicia"
          autocomplete="name"
          required
        />
        <p class="field-error" *ngIf="form.controls.playerName.invalid && form.controls.playerName.touched">
          Ingresa el nombre del jugador.
        </p>

        <label for="dateTime">Fecha y hora</label>
        <input
          id="dateTime"
          type="datetime-local"
          formControlName="dateTime"
          required
        />
        <p class="field-error" *ngIf="form.controls.dateTime.invalid && form.controls.dateTime.touched">
          Selecciona fecha y hora.
        </p>

        <label for="board">Tipo de tablero</label>
        <select id="board" formControlName="board">
          <option value="Clásico">Clásico</option>
          <option value="Rápido">Rápido</option>
          <option value="Blitz">Blitz</option>
        </select>

        <label for="duration">Duración</label>
        <select id="duration" formControlName="duration">
          <option value="30 minutos">30 minutos</option>
          <option value="60 minutos">60 minutos</option>
          <option value="90 minutos">90 minutos</option>
        </select>

        <label for="notes">Notas adicionales</label>
        <textarea
          id="notes"
          formControlName="notes"
          rows="3"
          placeholder="Ej. Preferencia de color o comentarios"
        ></textarea>

        <button type="submit" [disabled]="!canSubmit()">Crear reserva</button>
      </form>
    </section>
  `,
  styles: [
    `
      .reservation-form {
        background: rgba(15, 23, 42, 0.95);
        border: 1px solid rgba(148, 163, 184, 0.2);
        border-radius: 1rem;
        padding: 1.5rem;
        display: grid;
        gap: 1rem;
      }

      .reservation-form h2 {
        margin: 0;
        color: #f8fafc;
        font-size: 1.35rem;
      }

      label {
        font-weight: 600;
        color: #cbd5e1;
      }

      input,
      select,
      textarea {
        width: 100%;
        max-width: 100%;
        box-sizing: border-box;
        border: 1px solid #334155;
        border-radius: 0.75rem;
        background: #020617;
        color: #f8fafc;
        padding: 0.85rem 1rem;
      }

      input:focus,
      select:focus,
      textarea:focus {
        outline: 2px solid #38bdf8;
        outline-offset: 2px;
      }

      .field-error {
        margin: 0;
        color: #f87171;
        font-size: 0.9rem;
      }

      button {
        margin-top: 0.25rem;
        padding: 0.9rem 1.2rem;
        border: none;
        border-radius: 0.9rem;
        background: #38bdf8;
        color: #020617;
        font-weight: 700;
        cursor: pointer;
        transition: transform 0.15s ease-in-out, opacity 0.15s ease-in-out;
      }

      button:disabled {
        opacity: 0.55;
        cursor: not-allowed;
      }

      button:not(:disabled):hover {
        transform: translateY(-1px);
      }
    `,
  ],
})
export class ChessReservationFormComponent {
  readonly reservationCreated = output<ChessReservation>();

  readonly form = inject(FormBuilder).nonNullable.group({
    playerName: ['', Validators.required],
    dateTime: ['', Validators.required],
    board: ['Clásico', Validators.required],
    duration: ['60 minutos', Validators.required],
    notes: [''],
  });

  readonly canSubmit = computed(() => this.form.valid);

  submit(): void {
    if (!this.form.valid) {
      this.form.markAllAsTouched();
      return;
    }

    const values = this.form.getRawValue();

    this.reservationCreated.emit({
      id: Math.random().toString(36).slice(2) + Date.now().toString(36),
      playerName: values.playerName,
      dateTime: values.dateTime,
      board: values.board,
      duration: values.duration,
      notes: values.notes,
      createdAt: new Date().toLocaleString('es-ES'),
    });

    this.form.reset({
      playerName: '',
      dateTime: '',
      board: 'Clásico',
      duration: '60 minutos',
      notes: '',
    });
  }
}

@Component({
  selector: 'app-chess-reservation',
  imports: [CommonModule, ChessReservationFormComponent],
  template: `
    <section class="reservation-layout">
      <app-chess-reservation-form
        (reservationCreated)="addReservation($event)"
      ></app-chess-reservation-form>

      <section class="reservation-details" aria-label="Historial de reservas">
        <div class="summary-card">
          <h2>Reservas programadas</h2>
          <p>{{ reservationCount() }} reserva(s)</p>
        </div>

        <ul class="reservation-list" *ngIf="reservations().length > 0; else emptyState">
          <li class="reservation-card" *ngFor="let reservation of reservations()">
            <div class="reservation-row">
              <span class="reservation-player">{{ reservation.playerName }}</span>
              <time>{{ reservation.createdAt }}</time>
            </div>
            <p><strong>Fecha:</strong> {{ reservation.dateTime }}</p>
            <p><strong>Tablero:</strong> {{ reservation.board }}</p>
            <p><strong>Duración:</strong> {{ reservation.duration }}</p>
            <p *ngIf="reservation.notes"><strong>Notas:</strong> {{ reservation.notes }}</p>
          </li>
        </ul>

        <ng-template #emptyState>
          <div class="empty-state">
            <p>No hay reservas programadas aún. Completa el formulario para reservar un tablero.</p>
          </div>
        </ng-template>
      </section>
    </section>
  `,
  styles: [
    `
      .reservation-layout {
        display: grid;
        gap: 1.5rem;
      }

      .summary-card,
      .reservation-card,
      .empty-state {
        background: rgba(15, 23, 42, 0.95);
        border: 1px solid rgba(148, 163, 184, 0.2);
        border-radius: 1rem;
        padding: 1.25rem;
      }

      .summary-card h2,
      .reservation-card strong {
        color: #f8fafc;
      }

      .summary-card p,
      .reservation-card p {
        margin: 0.5rem 0 0;
        color: #cbd5e1;
      }

      .reservation-list {
        list-style: none;
        padding: 0;
        margin: 0;
        display: grid;
        gap: 1rem;
      }

      .reservation-card {
        display: grid;
        gap: 0.5rem;
      }

      .reservation-row {
        display: flex;
        justify-content: space-between;
        gap: 1rem;
        flex-wrap: wrap;
        align-items: baseline;
      }

      .reservation-player {
        font-size: 1rem;
        font-weight: 700;
        color: #38bdf8;
      }

      .empty-state p {
        margin: 0;
        color: #cbd5e1;
      }
    `,
  ],
})
export class ChessReservationComponent {
  readonly reservations = signal<ChessReservation[]>([]);
  readonly reservationCount = computed(() => this.reservations().length);

  addReservation(reservation: ChessReservation): void {
    this.reservations.update((current) => [reservation, ...current]);
  }
}
