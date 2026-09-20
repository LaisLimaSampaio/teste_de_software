const http = require('http');
const { URL } = require('url');

const PORT = 3000;
const VALID_EMAIL = 'ana@exemplo.com';
const VALID_PASSWORD = 'SenhaSegura123!';

const styles = `
  :root {
    --ink: #16212e;
    --muted: #5b6b7c;
    --line: #d8dee5;
    --accent: #1f5c8a;
    --accent-ink: #ffffff;
    --danger: #8a2a1f;
    --danger-bg: #fbeae7;
    --bg: #f6f7f9;
    --card: #ffffff;
  }
  * { box-sizing: border-box; }
  body {
    margin: 0;
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--bg);
    font-family: -apple-system, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
    color: var(--ink);
  }
  main {
    width: 100%;
    max-width: 360px;
    padding: 2rem;
  }
  .card {
    background: var(--card);
    border: 1px solid var(--line);
    border-radius: 10px;
    padding: 2rem;
  }
  h1 {
    font-size: 1.25rem;
    margin: 0 0 1.5rem;
  }
  label {
    display: block;
    font-size: 0.875rem;
    color: var(--muted);
    margin-bottom: 0.35rem;
  }
  input {
    width: 100%;
    padding: 0.6rem 0.7rem;
    border: 1px solid var(--line);
    border-radius: 6px;
    font-size: 1rem;
    margin-bottom: 1.1rem;
  }
  input:focus {
    outline: 2px solid var(--accent);
    outline-offset: 1px;
  }
  button {
    width: 100%;
    padding: 0.7rem;
    background: var(--accent);
    color: var(--accent-ink);
    border: none;
    border-radius: 6px;
    font-size: 1rem;
    cursor: pointer;
  }
  button:hover { opacity: 0.92; }
  .alert {
    background: var(--danger-bg);
    color: var(--danger);
    border: 1px solid var(--danger);
    border-radius: 6px;
    padding: 0.6rem 0.8rem;
    margin-bottom: 1.1rem;
    font-size: 0.9rem;
  }
  .hint {
    margin-top: 1rem;
    font-size: 0.8rem;
    color: var(--muted);
  }
`;

function loginPage({ error } = {}) {
  return `<!doctype html>
<html lang="pt-br">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Entrar — Loja Demo</title>
  <style>${styles}</style>
</head>
<body>
  <main>
    <div class="card">
      <h1>Entrar na sua conta</h1>
      ${error ? `<div role="alert" class="alert">${error}</div>` : ''}
      <form method="POST" action="/login">
        <label for="email">E-mail</label>
        <input id="email" name="email" type="email" autocomplete="username" />

        <label for="senha">Senha</label>
        <input id="senha" name="senha" type="password" autocomplete="current-password" />

        <button type="submit">Entrar</button>
      </form>
      <p class="hint">Credencial de teste: ana@exemplo.com / SenhaSegura123!</p>
    </div>
  </main>
</body>
</html>`;
}

function contaPage() {
  return `<!doctype html>
<html lang="pt-br">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Minha conta — Loja Demo</title>
  <style>${styles}</style>
</head>
<body>
  <main>
    <div class="card">
      <h1>Minha conta</h1>
      <p>Bem-vinda, Ana. Esta é a sua área autenticada.</p>
      <form method="POST" action="/logout">
        <button type="submit">Sair</button>
      </form>
    </div>
  </main>
</body>
</html>`;
}

function parseCookies(req) {
  const header = req.headers.cookie || '';
  return Object.fromEntries(
    header.split(';').filter(Boolean).map((p) => {
      const [k, ...v] = p.trim().split('=');
      return [k, decodeURIComponent(v.join('='))];
    })
  );
}

function readBody(req) {
  return new Promise((resolve) => {
    let data = '';
    req.on('data', (chunk) => (data += chunk));
    req.on('end', () => resolve(data));
  });
}

const server = http.createServer(async (req, res) => {
  const url = new URL(req.url, `http://${req.headers.host}`);
  const cookies = parseCookies(req);
  const authenticated = cookies.session === 'ana';

  if (url.pathname === '/' ) {
    res.writeHead(302, { Location: authenticated ? '/conta' : '/login' });
    return res.end();
  }

  if (url.pathname === '/login' && req.method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    return res.end(loginPage());
  }

  if (url.pathname === '/login' && req.method === 'POST') {
    const body = await readBody(req);
    const params = new URLSearchParams(body);
    const email = params.get('email') || '';
    const senha = params.get('senha') || '';

    if (email === VALID_EMAIL && senha === VALID_PASSWORD) {
      res.writeHead(302, {
        'Set-Cookie': 'session=ana; Path=/; HttpOnly',
        Location: '/conta',
      });
      return res.end();
    }

    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    return res.end(loginPage({ error: 'E-mail ou senha inválidos' }));
  }

  if (url.pathname === '/conta' && req.method === 'GET') {
    if (!authenticated) {
      res.writeHead(302, { Location: '/login' });
      return res.end();
    }
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    return res.end(contaPage());
  }

  if (url.pathname === '/logout' && req.method === 'POST') {
    res.writeHead(302, {
      'Set-Cookie': 'session=; Path=/; Max-Age=0',
      Location: '/login',
    });
    return res.end();
  }

  res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
  res.end('Não encontrado');
});

server.listen(PORT, () => {
  console.log(`App de demonstração rodando em http://localhost:${PORT}/login`);
});
