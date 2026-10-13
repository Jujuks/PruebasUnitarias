package com.hotel.Hotel.mapper;

import com.hotel.Hotel.domain.Reserva;
import com.hotel.Hotel.dto.response.ReservaResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-12T21:11:14-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12 (Oracle Corporation)"
)
@Component
public class ReservaMapperImpl implements ReservaMapper {

    @Override
    public ReservaResponse toResponse(Reserva reserva) {
        if ( reserva == null ) {
            return null;
        }

        UUID id = null;
        double costoTotal = 0.0d;

        id = reserva.getId();
        costoTotal = reserva.getCostoTotal();

        String estado = reserva.getEstado().name();
        String nombreHuesped = null;
        String habitacionNumero = null;
        LocalDateTime fechaInicio = null;
        LocalDateTime fechaFin = null;

        ReservaResponse reservaResponse = new ReservaResponse( id, nombreHuesped, habitacionNumero, fechaInicio, fechaFin, costoTotal, estado );

        return reservaResponse;
    }

    @Override
    public List<ReservaResponse> toResponseList(List<Reserva> reservas) {
        if ( reservas == null ) {
            return null;
        }

        List<ReservaResponse> list = new ArrayList<ReservaResponse>( reservas.size() );
        for ( Reserva reserva : reservas ) {
            list.add( toResponse( reserva ) );
        }

        return list;
    }
}
