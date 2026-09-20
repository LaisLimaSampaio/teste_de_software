package br.edu.ifpr.pedidos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class PoliticaDescontoTest {

    private final PoliticaDesconto politica = new PoliticaDesconto();

    @Test
    void vipDeveReceberDezPorCento() {
        assertEquals(10_000L, politica.calcular(new Cliente(true, false, 3), 100_000, null));
    }

    @Test
    void comumDeveReceberCincoPorCentoNoLimiteDeQuinhentos() {
        assertEquals(2_500L, politica.calcular(new Cliente(false, false, 3), 50_000, null));
    }

    @Test
    void comumAbaixoDoLimiteNaoRecebeDesconto() {
        assertEquals(0L, politica.calcular(new Cliente(false, false, 3), 49_999, null));
    }

    @Test
    void cupomEmBrancoMantemDescontoBase() {
        assertEquals(2_500L, politica.calcular(new Cliente(false, false, 3), 50_000, "   "));
    }

    @Test
    void bemvindoDeveSomarVinteReaisComTrimEMinuscula() {
        assertEquals(2_000L, politica.calcular(new Cliente(false, false, 0), 10_000, " bemvindo "));
    }

    @Test
    void bemvindoNaoSeAplicaComComprasAnteriores() {
        assertEquals(0L, politica.calcular(new Cliente(false, false, 1), 10_000, "BEMVINDO"));
    }

    @Test
    void bemvindoNaoSeAplicaAbaixoDeCemReais() {
        assertEquals(0L, politica.calcular(new Cliente(false, false, 0), 9_900, "BEMVINDO"));
    }

    @Test
    void extra10DeveSomarDezPorCentoNoLimiteDeDuzentos() {
        assertEquals(2_000L, politica.calcular(new Cliente(false, false, 3), 20_000, "EXTRA10"));
    }

    @Test
    void extra10NaoSeAplicaAbaixoDoLimite() {
        assertEquals(0L, politica.calcular(new Cliente(false, false, 3), 19_999, "EXTRA10"));
    }

    @Test
    void cupomDesconhecidoDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> politica.calcular(new Cliente(false, false, 3), 50_000, "XPTO"));
    }

    @Test
    void descontoCombinadoDeveRespeitarTetoDeVintePorCento() {
        assertEquals(2_000L, politica.calcular(new Cliente(true, false, 0), 10_000, "BEMVINDO"));
    }

    @Test
    void subtotalNegativoDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> politica.calcular(new Cliente(false, false, 3), -1, null));
    }
}