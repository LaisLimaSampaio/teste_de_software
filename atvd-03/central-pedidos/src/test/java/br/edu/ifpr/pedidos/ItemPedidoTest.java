package br.edu.ifpr.pedidos;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ItemPedidoTest {

    @Test
    void deveCalcularTotalDaLinha() {
        assertEquals(30_000L, new ItemPedido("SKU", 10_000, 3, 5, 500, false).totalCentavos());
    }

    @Test
    void linhaInativaDeveTerTotalZero() {
        assertEquals(0L, new ItemPedido("SKU", 10_000, 0, 5, 500, false).totalCentavos());
    }

    @Test
    void deveEstarDisponivelQuandoQuantidadeIgualAoEstoque() {
        assertTrue(new ItemPedido("SKU", 10_000, 5, 5, 500, false).disponivel());
    }

    @Test
    void naoDeveEstarDisponivelQuandoQuantidadeExcedeEstoque() {
        assertFalse(new ItemPedido("SKU", 10_000, 6, 5, 500, false).disponivel());
    }

    @Test
    void skuNuloDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido(null, 10_000, 1, 5, 500, false));
    }

    @Test
    void skuEmBrancoDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("   ", 10_000, 1, 5, 500, false));
    }

    @Test
    void precoForaDoIntervaloDeveLancarExcecao() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("SKU", 0, 1, 5, 500, false)),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("SKU", 1_000_001, 1, 5, 500, false))
        );
    }

    @Test
    void deveAceitarPrecoNosLimites() {
        assertAll(
            () -> assertEquals(1L, new ItemPedido("SKU", 1, 1, 5, 500, false).precoCentavos()),
            () -> assertEquals(1_000_000L,
                new ItemPedido("SKU", 1_000_000, 1, 5, 500, false).precoCentavos())
        );
    }

    @Test
    void quantidadeForaDoIntervaloDeveLancarExcecao() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("SKU", 10_000, -1, 5, 500, false)),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("SKU", 10_000, 101, 200, 500, false))
        );
    }

    @Test
    void estoqueNegativoDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> new ItemPedido("SKU", 10_000, 1, -1, 500, false));
    }

    @Test
    void pesoForaDoIntervaloDeveLancarExcecao() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("SKU", 10_000, 1, 5, 0, false)),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("SKU", 10_000, 1, 5, 100_001, false))
        );
    }
}