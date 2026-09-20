package br.edu.ifpr.pedidos;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PagamentoServiceTest {

    static class ProcessadorFalso implements ProcessadorPagamento {
        int chamadas = 0;
        long ultimoValor = 0;
        private final int falhasAntesDeResponder;
        private final boolean resposta;

        ProcessadorFalso(int falhasAntesDeResponder, boolean resposta) {
            this.falhasAntesDeResponder = falhasAntesDeResponder;
            this.resposta = resposta;
        }

        @Override
        public boolean autorizar(long totalCentavos) {
            chamadas++;
            ultimoValor = totalCentavos;
            if (chamadas <= falhasAntesDeResponder) {
                throw new IllegalStateException("indisponível");
            }
            return resposta;
        }
    }

    @Test
    void deveAprovarNaPrimeiraTentativa() {
        ProcessadorFalso stub = new ProcessadorFalso(0, true);
        PagamentoService servico = new PagamentoService(stub);

        assertAll(
            () -> assertTrue(servico.pagar(11_200, 3)),
            () -> assertEquals(1, stub.chamadas),
            () -> assertEquals(11_200L, stub.ultimoValor)
        );
    }

    @Test
    void recusaDefinitivaNaoDeveRepetirTentativa() {
        ProcessadorFalso stub = new ProcessadorFalso(0, false);
        PagamentoService servico = new PagamentoService(stub);

        assertAll(
            () -> assertFalse(servico.pagar(11_200, 3)),
            () -> assertEquals(1, stub.chamadas)
        );
    }

    @Test
    void deveAprovarNaTerceiraTentativaAposDuasIndisponibilidades() {
        ProcessadorFalso stub = new ProcessadorFalso(2, true);
        PagamentoService servico = new PagamentoService(stub);

        assertAll(
            () -> assertTrue(servico.pagar(11_200, 3)),
            () -> assertEquals(3, stub.chamadas)
        );
    }

    @Test
    void deveRecusarAoEsgotarAsTresTentativas() {
        ProcessadorFalso stub = new ProcessadorFalso(99, true);
        PagamentoService servico = new PagamentoService(stub);

        assertAll(
            () -> assertFalse(servico.pagar(11_200, 3)),
            () -> assertEquals(3, stub.chamadas)
        );
    }

    @Test
    void deveRecusarComUmaUnicaTentativaPermitida() {
        ProcessadorFalso stub = new ProcessadorFalso(99, true);
        PagamentoService servico = new PagamentoService(stub);

        assertAll(
            () -> assertFalse(servico.pagar(11_200, 1)),
            () -> assertEquals(1, stub.chamadas)
        );
    }

    @Test
    void outrasExcecoesDevemPropagar() {
        PagamentoService servico = new PagamentoService(total -> {
            throw new RuntimeException("falha inesperada");
        });
        assertThrows(RuntimeException.class, () -> servico.pagar(11_200, 3));
    }

    @Test
    void totalNaoPositivoDeveLancarExcecao() {
        PagamentoService servico = new PagamentoService(total -> true);
        assertThrows(IllegalArgumentException.class, () -> servico.pagar(0, 1));
    }

    @Test
    void limiteDeTentativasForaDoIntervaloDeveLancarExcecao() {
        PagamentoService servico = new PagamentoService(total -> true);
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> servico.pagar(100, 0)),
            () -> assertThrows(IllegalArgumentException.class, () -> servico.pagar(100, 4))
        );
    }

    @Test
    void processadorNuloDeveLancarNullPointer() {
        assertThrows(NullPointerException.class, () -> new PagamentoService(null));
    }
}