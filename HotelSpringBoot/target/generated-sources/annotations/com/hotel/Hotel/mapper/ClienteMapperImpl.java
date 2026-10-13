package com.hotel.Hotel.mapper;

import com.hotel.Hotel.domain.Cliente;
import com.hotel.Hotel.dto.request.CrearClienteRequest;
import com.hotel.Hotel.dto.response.ClienteResponse;
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
public class ClienteMapperImpl implements ClienteMapper {

    @Override
    public ClienteResponse toResponse(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }

        UUID id = null;
        String nombre = null;
        String email = null;
        boolean activo = false;
        int penalizaciones = 0;

        id = cliente.getId();
        nombre = cliente.getNombre();
        email = cliente.getEmail();
        activo = cliente.isActivo();
        penalizaciones = cliente.getPenalizaciones();

        ClienteResponse clienteResponse = new ClienteResponse( id, nombre, email, activo, penalizaciones );

        return clienteResponse;
    }

    @Override
    public List<ClienteResponse> toResponseList(List<Cliente> clientes) {
        if ( clientes == null ) {
            return null;
        }

        List<ClienteResponse> list = new ArrayList<ClienteResponse>( clientes.size() );
        for ( Cliente cliente : clientes ) {
            list.add( toResponse( cliente ) );
        }

        return list;
    }

    @Override
    public Cliente toEntity(CrearClienteRequest request) {
        if ( request == null ) {
            return null;
        }

        String nombre = null;
        String email = null;

        nombre = request.nombre();
        email = request.email();

        Cliente cliente = new Cliente( nombre, email );

        return cliente;
    }
}
