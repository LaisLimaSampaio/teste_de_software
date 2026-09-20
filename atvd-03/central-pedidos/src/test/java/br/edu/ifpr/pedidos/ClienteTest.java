package br.edu.ifpr.pedidos;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ClienteTest {

    @Test
    void deveCriarClienteValido() {
        Cliente cliente = new Cliente(true, false, 5);
        assertAll(
            () -> assertTrue(cliente.vip()),
            () -> assertFalse(cliente.bloqueado()),
            () -> assertEquals(5, cliente.comprasAnteriores())
        );
    }

    @Test
    void deveAceitarHistoricoZerado() {
        assertEquals(0, new Cliente(false, false, 0).comprasAnteriores());
    }

    @Test
    void historicoNegativoDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Cliente(false, false, -1));
    }
}