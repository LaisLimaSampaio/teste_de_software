# Atividade: automatizar o login

Este projeto tem tudo pronto para você seguir os 8 passos da atividade da
Semana 05 sem precisar de uma aplicação própria — o `server.js` é uma app de
login mínima (sem dependências) só para praticar.

## 1. Instale as dependências

Abra um terminal **nesta pasta** e rode:

```bash
npm install
npx playwright install
```

O primeiro comando instala o Playwright Test (definido no `package.json`).
O segundo baixa os navegadores (Chromium, Firefox, WebKit).

## 2. Confira o ambiente (opcional, mas recomendado)

```bash
node --version
npm --version
```

## 3. Suba a aplicação de demonstração

Você **não precisa** rodar isso manualmente: o `playwright.config.ts` já está
configurado para subir o `server.js` sozinho antes dos testes (bloco
`webServer`). Mas se quiser ver a tela no navegador antes de tudo:

```bash
npm start
```

E acesse http://localhost:3000/login no seu navegador. Credencial de teste:
`ana@exemplo.com` / `SenhaSegura123!`. Pare com Ctrl+C antes do próximo passo.

## 4. O arquivo do teste já existe

`tests/login.spec.ts` — já implementa:
- ✅ um cenário **válido** (login com credenciais corretas)
- ✅ um cenário **inválido** (senha errada → mensagem genérica, sem sessão)
- ✅ locators por **papel/rótulo** (`getByLabel`, `getByRole`), nunca CSS
- ✅ verificação de **URL**, **mensagem de erro** e **estado autenticado**

## 5. Execute em Chromium, com o navegador visível

```bash
npx playwright test --project=chromium --headed
```

Você vai ver o Chromium abrir, preencher os campos e clicar em "Entrar" —
duas vezes (um teste válido, um inválido).

## 6. Provoque uma falha de propósito

Para treinar o passo 8 da atividade (investigar uma falha no UI Mode), edite
temporariamente `tests/login.spec.ts` e troque a senha válida por uma errada:

```ts
await page.getByLabel('Senha').fill('SenhaErradaDePropósito!');
```

Salve, e rode o UI Mode:

```bash
npx playwright test --ui
```

Clique no teste que falhou. Você vai poder navegar pelos passos, ver
screenshots de cada ação e entender exatamente onde a expectativa não bateu
com o observado. Depois, desfaça a alteração para o teste voltar a passar.

## 7. Veja o relatório

```bash
npx playwright show-report
```

## Checklist da atividade (para conferir com sua dupla)

- [x] requisito e resultado esperado definidos (comentário no topo do spec)
- [x] ambiente configurado (`baseURL` no `playwright.config.ts`)
- [x] dados controlados (credencial fixa no `server.js`)
- [x] teste independente (cada teste começa em `/login`, sem depender do outro)
- [x] locators semânticos (`getByLabel`, `getByRole`)
- [x] ações aguardadas com `await`
- [x] asserções web específicas (`toHaveURL`, `toBeVisible`, `toHaveText`)
- [x] efeitos importantes verificados (URL + mensagem + tentativa de acesso direto)
- [ ] execução em navegador relevante — **rode você**: `--project=chromium`
- [ ] relatório/trace disponível — **rode você**: `show-report` / `--trace on`
- [x] segredos fora do código — aqui é só uma demo local, sem segredo real
