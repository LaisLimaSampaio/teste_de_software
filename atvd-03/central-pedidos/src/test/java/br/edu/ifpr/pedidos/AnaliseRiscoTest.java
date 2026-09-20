package br.edu.ifpr.pedidos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class AnaliseRiscoTest {

    private final AnaliseRisco risco = new AnaliseRisco();

    @Test
    void clienteBloqueadoDeveSerRecusado() {
        assertEquals("RECUSADO", risco.avaliar(new Cliente(false, true, 3), 0, false));
    }

    @Test
    void clienteNovoComTotalAltoVaiParaRevisao() {
        assertEquals("REVISAO", risco.avaliar(new Cliente(false, false, 0), 100_001, false));
    }

    @Test
    void clienteNovoComEntregaExpressaVaiParaRevisao() {
        assertEquals("REVISAO", risco.avaliar(new Cliente(false, false, 0), 100_000, true));
    }

    @Test
    void clienteNovoNoLimiteSemExpressoDeveSerAprovado() {
        assertEquals("APROVADO", risco.avaliar(new Cliente(false, false, 0), 100_000, false));
    }

    @Test
    void clienteComHistoricoAcimaDeCincoMilVaiParaRevisao() {
        assertEquals("REVISAO", risco.avaliar(new Cliente(false, false, 1), 500_001, false));
    }

    @Test
    void vipComHistoricoEValorAltoDeveSerAprovado() {
        assertEquals("APROVADO", risco.avaliar(new Cliente(true, false, 1), 500_001, false));
    }

    @Test
    void clienteComHistoricoNoLimiteDeveSerAprovado() {
        assertEquals("APROVADO", risco.avaliar(new Cliente(false, false, 1), 500_000, false));
    }

    @Test
    void totalNegativoDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> risco.avaliar(new Cliente(false, false, 1), -1, false));
    }
}