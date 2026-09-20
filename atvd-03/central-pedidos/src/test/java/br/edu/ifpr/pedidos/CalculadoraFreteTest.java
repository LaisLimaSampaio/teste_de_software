package br.edu.ifpr.pedidos;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class CalculadoraFreteTest {

    private final CalculadoraFrete frete = new CalculadoraFrete();
    private final Cliente comum = new Cliente(false, false, 3);
    private final Cliente vip = new Cliente(true, false, 3);

    private ItemPedido item(int peso, int quantidade, boolean fragil) {
        return new ItemPedido("SKU", 10_000, quantidade, 10, peso, fragil);
    }

    private Pedido pedido(String uf, boolean expresso, ItemPedido... itens) {
        return new Pedido(List.of(itens), uf, expresso, null);
    }

    @Test
    void deveCobrarTarifaDoParana() {
        assertEquals(1_200L, frete.calcular(pedido("PR", false, item(1_000, 1, false)), comum, 10_000));
    }

    @Test
    void deveCobrarTarifaDeSaoPaulo() {
        assertEquals(2_000L, frete.calcular(pedido("SP", false, item(1_000, 1, false)), comum, 10_000));
    }

    @Test
    void deveCobrarTarifaDoRioDeJaneiro() {
        assertEquals(2_000L, frete.calcular(pedido("RJ", false, item(1_000, 1, false)), comum, 10_000));
    }

    @Test
    void deveCobrarTarifaPadraoParaDemaisEstados() {
        assertEquals(3_000L, frete.calcular(pedido("MG", false, item(1_000, 1, false)), comum, 10_000));
    }

    @Test
    void naoDeveCobrarAdicionalComPesoExatoDeDoisQuilos() {
        assertEquals(1_200L, frete.calcular(pedido("PR", false, item(2_000, 1, false)), comum, 10_000));
    }

    @Test
    void deveCobrarUmaFracaoAcimaDeDoisQuilos() {
        assertEquals(1_500L, frete.calcular(pedido("PR", false, item(2_001, 1, false)), comum, 10_000));
    }

    @Test
    void deveCobrarTresFracoesComQuatroQuilosEUmGrama() {
        assertEquals(2_100L, frete.calcular(pedido("PR", false, item(4_001, 1, false)), comum, 10_000));
    }

    @Test
    void deveZerarFreteComValorLiquidoAcimaDeTrezentosEEntregaNormal() {
        assertEquals(0L, frete.calcular(pedido("PR", false, item(1_000, 1, false)), comum, 30_000));
    }

    @Test
    void naoDeveZerarFreteQuandoEntregaEExpressa() {
        assertEquals(2_700L, frete.calcular(pedido("PR", true, item(1_000, 1, false)), comum, 30_000));
    }

    @Test
    void vipDevePagarMetadeDoFrete() {
        assertEquals(600L, frete.calcular(pedido("PR", false, item(1_000, 1, false)), vip, 10_000));
    }

    @Test
    void adicionalDeFragilIncideMesmoComBaseZerada() {
        assertEquals(500L, frete.calcular(pedido("PR", false, item(1_000, 1, true)), vip, 30_000));
    }

    @Test
    void adicionalDeFragilDeveSerCobradoUmaUnicaVez() {
        Pedido pedido = pedido("PR", false, item(500, 1, true), item(500, 1, true));
        assertEquals(1_700L, frete.calcular(pedido, comum, 10_000));
    }

    @Test
    void itemInativoNaoDeveAtivarAdicionalDeFragil() {
        Pedido pedido = pedido("PR", false, item(1_000, 1, false), item(500, 0, true));
        assertEquals(1_200L, frete.calcular(pedido, comum, 10_000));
    }

    @Test
    void valorLiquidoNegativoDeveLancarExcecao() {
        Pedido pedido = pedido("PR", false, item(1_000, 1, false));
        assertThrows(IllegalArgumentException.class, () -> frete.calcular(pedido, comum, -1));
    }
}