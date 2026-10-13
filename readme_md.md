# Proyecto Hotel - Pruebas Unitarias (TDD)

Este repositorio contiene la implementación de pruebas unitarias para la capa de servicios del sistema de reservas (`ReservaService`). Las pruebas fueron diseñadas tomando como base los principios de **Test-Driven Development (TDD)** para garantizar un código robusto y optimizable.

## 🧪 Resumen de las Pruebas Implementadas

Se desarrollaron **5 escenarios de prueba** utilizando **JUnit 5** y **Mockito**, combinando pruebas de dominio puro y pruebas con dependencias simuladas (Mocks):

1. **Lógica de Dominio (Cálculo puro):** 
   Se verificó que la clase `RangoFechas` calcule de forma exacta la cantidad de días de hospedaje. Es una prueba directa que no requiere dependencias externas.

2. **Caso Feliz (Creación exitosa):** 
   Se simuló el flujo ideal (Happy Path). Con la ayuda de Mocks, se comprobó que si un cliente existe y la habitación está libre, el servicio genera y devuelve la reserva correctamente.

3. **Manejo de Excepciones (Cliente no encontrado):** 
   Se evaluó la respuesta del sistema ante datos inválidos. Si el repositorio simulado no encuentra un cliente, la prueba verifica que se dispare correctamente la excepción `RecursoNoEncontradoException`.

4. **Reglas de Negocio (Habitación ocupada):** 
   Se probó una validación crítica del negocio. Si la habitación solicitada tiene un estado de ocupada, el servicio bloquea el proceso lanzando la excepción `HabitacionNoDisponibleException`.

5. **Verificación de Comportamiento (Interacción):** 
   Mediante la función `verify` de Mockito, se garantizó que, al procesar una reserva válida, el sistema se comunique con la base de datos (`reservaRepository.save`) **exactamente una vez**, previniendo registros duplicados.

## 🛠️ Tecnologías y Herramientas
* **Java / Spring Boot**
* **JUnit 5** (Framework de pruebas)
* **Mockito** (Simulación de dependencias e interacciones)