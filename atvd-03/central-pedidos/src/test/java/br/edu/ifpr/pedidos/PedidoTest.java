package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PedidoTest {

    private ItemPedido item(int quantidade, int estoque, int peso, boolean fragil) {
        return new ItemPedido("SKU", 10_000, quantidade, estoque, peso, fragil);
    }

    @Test
    void subtotalDeListaVaziaDeveSerZero() {
        assertEquals(0L, new Pedido(List.of(), "PR", false, null).subtotalCentavos());
    }

    @Test
    void subtotalDeUmaLinhaAtiva() {
        Pedido pedido = new Pedido(List.of(item(2, 5, 500, false)), "PR", false, null);
        assertEquals(20_000L, pedido.subtotalCentavos());
    }

    @Test
    void linhaInativaNaoDeveSomarNoSubtotal() {
        Pedido pedido = new Pedido(List.of(item(2, 5, 500, false), item(0, 5, 500, false)),
            "PR", false, null);
        assertEquals(20_000L, pedido.subtotalCentavos());
    }

    @Test
    void deveSomarOPesoDeTodasAsLinhas() {
        Pedido pedido = new Pedido(List.of(item(2, 5, 500, false), item(1, 5, 300, false)),
            "PR", false, null);
        assertEquals(1_300, pedido.pesoGramas());
    }

    @Test
    void deveDetectarItemFragilAtivo() {
        Pedido pedido = new Pedido(List.of(item(1, 5, 500, true)), "PR", false, null);
        assertTrue(pedido.temFragil());
    }

    @Test
    void itemFragilInativoNaoDeveContar() {
        Pedido pedido = new Pedido(List.of(item(0, 5, 500, true)), "PR", false, null);
        assertFalse(pedido.temFragil());
    }

    @Test
    void pedidoSemItemFragil() {
        Pedido pedido = new Pedido(List.of(item(1, 5, 500, false)), "PR", false, null);
        assertFalse(pedido.temFragil());
    }

    @Test
    void listaVaziaTemEstoqueSuficiente() {
        assertTrue(new Pedido(List.of(), "PR", false, null).estoqueSuficiente());
    }

    @Test
    void deveDetectarFaltaDeEstoqueNaPrimeiraLinha() {
        Pedido pedido = new Pedido(List.of(item(9, 1, 500, false), item(1, 5, 500, false)),
            "PR", false, null);
        assertFalse(pedido.estoqueSuficiente());
    }

    @Test
    void deveDetectarFaltaDeEstoqueNaUltimaLinha() {
        Pedido pedido = new Pedido(List.of(item(1, 5, 500, false), item(9, 1, 500, false)),
            "PR", false, null);
        assertFalse(pedido.estoqueSuficiente());
    }

    @Test
    void deveCopiarAListaDefensivamente() {
        List<ItemPedido> original = new ArrayList<>(List.of(item(1, 5, 500, false)));
        Pedido pedido = new Pedido(original, "PR", false, null);

        original.clear();

        assertEquals(1, pedido.itens().size());
    }

    @Test
    void listaNulaDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(null, "PR", false, null));
    }

    @Test
    void ufInvalidaDeveLancarExcecao() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Pedido(List.of(), null, false, null)),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Pedido(List.of(), "P", false, null)),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Pedido(List.of(), "pr", false, null)),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Pedido(List.of(), "PRR", false, null))
        );
    }

    @Test
    void ufDesconhecidaComFormatoValidoDeveSerAceita() {
        assertEquals("ZZ", new Pedido(List.of(), "ZZ", false, null).uf());
    }
    @Test
    void listaComMaisDeCemLinhasDeveLancarExcecao() {
        List<ItemPedido> itens = new ArrayList<>();
        for (int i = 0; i < 101; i++) {
            itens.add(item(1, 5, 500, false));
        }
        assertThrows(IllegalArgumentException.class,
            () -> new Pedido(itens, "PR", false, null));
    }
}