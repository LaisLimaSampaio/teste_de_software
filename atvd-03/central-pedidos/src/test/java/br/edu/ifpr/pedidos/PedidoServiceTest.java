package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PedidoServiceTest {

    private final List<Long> cobrancas = new ArrayList<>();

    private PedidoService servico(boolean aprovar) {
        return new PedidoService(total -> {
            cobrancas.add(total);
            return aprovar;
        });
    }

    @Test
    void deveFecharPedidoDeClienteComumComFreteDoParanaEPagamentoAprovado() {
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        ResultadoPedido resultado = servico(true).fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("PAGO", resultado.status()),
            () -> assertEquals(10_000L, resultado.subtotalCentavos()),
            () -> assertEquals(0L, resultado.descontoCentavos()),
            () -> assertEquals(1_200L, resultado.freteCentavos()),
            () -> assertEquals(11_200L, resultado.totalCentavos()),
            () -> assertEquals(List.of(11_200L), cobrancas)
        );
    }

    @Test
    void clienteBloqueadoDeveEncerrarSemCobranca() {
        Cliente cliente = new Cliente(false, true, 3);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        ResultadoPedido resultado = servico(true).fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("BLOQUEADO", resultado.status()),
            () -> assertEquals(0L, resultado.subtotalCentavos()),
            () -> assertEquals(0L, resultado.descontoCentavos()),
            () -> assertEquals(0L, resultado.freteCentavos()),
            () -> assertEquals(0L, resultado.totalCentavos()),
            () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void pedidoSemItensAtivosDeveLancarExcecao() {
        Cliente cliente = new Cliente(false, false, 3);
        ItemPedido inativo = new ItemPedido("LIVRO-JAVA", 10_000, 0, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(inativo), "PR", false, null);

        PedidoService servico = servico(true);
        assertThrows(IllegalArgumentException.class, () -> servico.fechar(pedido, cliente));
        assertTrue(cobrancas.isEmpty());
    }

    @Test
    void faltaDeEstoqueDeveEncerrarSemCobranca() {
        Cliente cliente = new Cliente(false, false, 3);
        ItemPedido item = new ItemPedido("X", 10_000, 2, 1, 500, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        ResultadoPedido resultado = servico(true).fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("SEM_ESTOQUE", resultado.status()),
            () -> assertEquals(0L, resultado.totalCentavos()),
            () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void estoqueDeveSerAvaliadoAntesDoCupom() {
        Cliente cliente = new Cliente(false, false, 3);
        ItemPedido item = new ItemPedido("X", 10_000, 2, 1, 500, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, "XPTO");
        
        ResultadoPedido resultado = servico(true).fechar(pedido, cliente);

        assertEquals("SEM_ESTOQUE", resultado.status());
    }

    @Test
    void clienteNovoComEntregaExpressaDeveFicarEmRevisaoSemCobranca() {
        Cliente cliente = new Cliente(false, false, 0);
        ItemPedido item = new ItemPedido("FONE", 20_000, 1, 3, 800, false);
        Pedido pedido = new Pedido(List.of(item), "SP", true, null);

        ResultadoPedido resultado = servico(true).fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("REVISAO", resultado.status()),
            () -> assertEquals(20_000L, resultado.subtotalCentavos()),
            () -> assertEquals(0L, resultado.descontoCentavos()),
            () -> assertEquals(3_500L, resultado.freteCentavos()),
            () -> assertEquals(23_500L, resultado.totalCentavos()),
            () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void vipDeveTerDescontoEFreteGratuito() {
        Cliente cliente = new Cliente(true, false, 3);
        ItemPedido item = new ItemPedido("MOUSE", 30_000, 2, 5, 500, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        ResultadoPedido resultado = servico(true).fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("PAGO", resultado.status()),
            () -> assertEquals(60_000L, resultado.subtotalCentavos()),
            () -> assertEquals(6_000L, resultado.descontoCentavos()),
            () -> assertEquals(0L, resultado.freteCentavos()),
            () -> assertEquals(54_000L, resultado.totalCentavos()),
            () -> assertEquals(List.of(54_000L), cobrancas)
        );
    }

    @Test
    void recusaDoProcessadorDevePreservarOsValoresCalculados() {
        Cliente cliente = new Cliente(true, false, 3);
        ItemPedido item = new ItemPedido("MOUSE", 30_000, 2, 5, 500, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        ResultadoPedido resultado = servico(false).fechar(pedido, cliente);

        assertAll(
            () -> assertEquals("PAGAMENTO_RECUSADO", resultado.status()),
            () -> assertEquals(54_000L, resultado.totalCentavos()),
            () -> assertEquals(List.of(54_000L), cobrancas)
        );
    }
}