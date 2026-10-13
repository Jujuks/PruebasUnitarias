package com.hotel.Hotel.service;

import com.hotel.Hotel.domain.*;
import com.hotel.Hotel.dto.request.CrearReservaRequest;
import com.hotel.Hotel.dto.response.ReservaResponse;
import com.hotel.Hotel.exception.HabitacionNoDisponibleException;
import com.hotel.Hotel.exception.RecursoNoEncontradoException;
import com.hotel.Hotel.mapper.ReservaMapper;
import com.hotel.Hotel.repository.ClienteRepository;
import com.hotel.Hotel.repository.HabitacionRepository;
import com.hotel.Hotel.repository.ReservaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private HabitacionRepository habitacionRepository;

    @Mock
    private ReservaMapper reservaMapper;

    @InjectMocks
    private ReservaService reservaService;

    private Cliente clienteEjemplo;
    private Habitacion habitacionEjemplo;
    private CrearReservaRequest requestEjemplo;

    @BeforeEach
    void setUp() {
        // Datos de prueba con entidades y DTOs del dominio
        clienteEjemplo = new Cliente("Juan Pérez", "juan@correo.com");
        habitacionEjemplo = new HabitacionEstandar("101", 2, 100.0, 1);

        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fin = LocalDateTime.now().plusDays(5);

        requestEjemplo = new CrearReservaRequest(
                clienteEjemplo.getId(),
                habitacionEjemplo.getId(),
                inicio,
                fin);
    }

    // ------------------------------------------------------------------------
    // PRUEBA 1: Lógica de Dominio / Cálculo Directo (Caso Feliz sin Mocks)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("Prueba 1: Debe calcular correctamente el número de días de un rango de fechas")
    void testCalculoDiasEnRangoFechas() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 1, 14, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 11, 5, 14, 0);

        RangoFechas rango = new RangoFechas(inicio, fin);

        assertEquals(4, rango.getDias(), "El número de días calculado debe ser 4");
    }

    // ------------------------------------------------------------------------
    // PRUEBA 2: Servicio - Caso Feliz creando Reserva (Con Mock)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("Prueba 2: Debe crear una reserva exitosamente cuando los datos son válidos")
    void testCrearReservaExitoso() {
        when(clienteRepository.findById(clienteEjemplo.getId())).thenReturn(Optional.of(clienteEjemplo));
        when(habitacionRepository.findById(habitacionEjemplo.getId())).thenReturn(Optional.of(habitacionEjemplo));

        RangoFechas periodo = new RangoFechas(requestEjemplo.fechaInicio(), requestEjemplo.fechaFin());
        Reserva reservaGuardada = new Reserva(clienteEjemplo, habitacionEjemplo, periodo);

        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaGuardada);

        ReservaResponse responseEsperado = new ReservaResponse(
                reservaGuardada.getId(),
                clienteEjemplo.getNombre(),
                habitacionEjemplo.getNumero(),
                requestEjemplo.fechaInicio(),
                requestEjemplo.fechaFin(),
                reservaGuardada.getCostoTotal(),
                reservaGuardada.getEstado().name());

        when(reservaMapper.toResponse(reservaGuardada)).thenReturn(responseEsperado);

        ReservaResponse resultado = reservaService.crear(requestEjemplo);

        assertNotNull(resultado);
        assertEquals(reservaGuardada.getId(), resultado.id());
        assertEquals("Juan Pérez", resultado.nombreHuesped());
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    // ------------------------------------------------------------------------
    // PRUEBA 3: Excepción - Cliente no encontrado (Con Mock)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("Prueba 3: Debe lanzar RecursoNoEncontradoException si el cliente no existe")
    void testCrearReservaClienteNoEncontrado() {
        when(clienteRepository.findById(requestEjemplo.clienteId())).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> {
            reservaService.crear(requestEjemplo);
        });

        verify(reservaRepository, never()).save(any());
    }

    // ------------------------------------------------------------------------
    // PRUEBA 4: Regla de Negocio - Habitación no disponible (Con Mock)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("Prueba 4: Debe lanzar HabitacionNoDisponibleException si la habitación no está libre")
    void testCrearReservaHabitacionNoDisponible() {

        habitacionEjemplo.asignarAReserva();

        when(clienteRepository.findById(clienteEjemplo.getId())).thenReturn(Optional.of(clienteEjemplo));
        when(habitacionRepository.findById(habitacionEjemplo.getId())).thenReturn(Optional.of(habitacionEjemplo));

        assertThrows(HabitacionNoDisponibleException.class, () -> {
            reservaService.crear(requestEjemplo);
        });
        verify(reservaRepository, never()).save(any());
    }

    // ------------------------------------------------------------------------
    // PRUEBA 5: Verificación de Comportamiento / Interacción con Mocks (verify)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("Prueba 5: Debe verificar que el repositorio guarde la reserva exactamente 1 vez")
    void testVerificarInteraccionGuardarReserva() {

        when(clienteRepository.findById(clienteEjemplo.getId())).thenReturn(Optional.of(clienteEjemplo));
        when(habitacionRepository.findById(habitacionEjemplo.getId())).thenReturn(Optional.of(habitacionEjemplo));

        RangoFechas periodo = new RangoFechas(requestEjemplo.fechaInicio(), requestEjemplo.fechaFin());
        Reserva reservaGuardada = new Reserva(clienteEjemplo, habitacionEjemplo, periodo);

        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaGuardada);

        reservaService.crear(requestEjemplo);

        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Nested
    @DisplayName("Operaciones de Confirmación")
    class ConfirmacionTests {

        @Test
        @DisplayName("Debe confirmar exitosamente cuando la reserva existe y está PENDIENTE")
        void debeConfirmarReservaExitosamente() {
            UUID reservaId = UUID.randomUUID();
            Cliente cliente = new Cliente("Beatriz Morales", "beatriz@empresa.com");
            Habitacion habitacion = new HabitacionEstandar("201", 2, 200.0, 1);

            RangoFechas fechas = new RangoFechas(
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(3));
            Reserva reservaExistente = new Reserva(cliente, habitacion, fechas);

            ReservaResponse responseEsperado = new ReservaResponse(
                    reservaId,
                    "Beatriz Morales",
                    "201",
                    fechas.fechaInicio(),
                    fechas.fechaFin(),
                    400.0,
                    "CONFIRMADA");

            when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reservaExistente));
            when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(reservaMapper.toResponse(any(Reserva.class))).thenReturn(responseEsperado);

            ReservaResponse resultado = reservaService.confirmar(reservaId);

            assertNotNull(resultado);
            assertEquals("CONFIRMADA", resultado.estado());
            assertEquals(EstadoReserva.CONFIRMADA, reservaExistente.getEstado());

            verify(reservaRepository, times(1)).findById(reservaId);
            verify(reservaRepository, times(1)).save(reservaExistente);
            verify(reservaMapper, times(1)).toResponse(reservaExistente);
        }
    }
}