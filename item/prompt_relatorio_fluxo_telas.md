# Prompt: relatório "Fluxo e Telas" de um app Android a partir do código-fonte

Cole tudo abaixo do traço no Claude Code, com o diretório de trabalho na raiz do projeto Android.

---

## Objetivo

Gerar **um único arquivo HTML local** (`Bradescard_fluxo_e_telas.html`) que mostra o fluxo de telas do app Android deste projeto e, para cada tela, seus detalhes: componentes de UI, argumentos de navegação, textos, endpoints e modelos de API. O diagrama do fluxo é clicável: cada caixa leva ao detalhe da tela, e cada detalhe tem um link de volta para a caixa no diagrama.

Trabalhe só com leitura do código. Não publique nada na internet, não suba o arquivo em nenhum serviço e não altere o código do projeto. Escreva todos os arquivos gerados em uma pasta nova `_analise/` na raiz do projeto.

Se existir um `Bradescard_fluxo_e_telas.html` de uma execução anterior (na raiz ou em `_analise/`), use-o como referência visual e de estrutura. Reproduza a mesma aparência e os mesmos recursos, mas com os dados extraídos do código.

## Contexto

Uma execução anterior foi feita sobre o APK compilado (sem código-fonte), e por isso tinha limitações: nomes de campos de modelos ofuscados, ordem de etapas deduzida por nomes, telas em Compose sem textos. Com o código-fonte, resolva essas limitações e **marque como "inferido" só o que realmente não puder confirmar no código**.

O app usa conteúdo dirigido pelo servidor (CMS), então muitas telas têm poucos textos locais e mais contratos de API. Isso é esperado, não trate como erro.

## Passo a passo

### 1. Inventário

- Liste módulos Gradle, `AndroidManifest.xml` (Activities, launcher, `exported`), pastas `res/navigation`, `res/layout`, `res/menu`, `res/values*/strings.xml`.
- Identifique a arquitetura (Activities + Fragments, Navigation Component, Compose com `NavHost`, Hilt, Retrofit, ViewModel, UseCase, Repository). Anote o que encontrar em um resumo curto para o rodapé do relatório.

### 2. Telas

Uma "tela" é cada Activity, Fragment, Dialog/BottomSheet de destino e cada rota `composable(...)` de um `NavHost`.

Para cada uma, registre:
- `id` estável em snake_case (ex.: `login`, `reg_card`, `cl_resume`), `title` legível em português, `flow` (agrupamento funcional, ex.: Autenticação, Cadastro, Recuperar senha, Home, Pagamentos, Aclaraciones), `kind` (Activity, Fragment, Dialog, Compose) e o caminho/classe no código-fonte.
- Ao definir fluxos, siga a estrutura de pacotes e os grafos de navegação, e mantenha no máximo 6 a 8 fluxos.

### 3. Navegação

Extraia as transições de todas as fontes abaixo e una:
- `res/navigation/*.xml`: `<action>`, `startDestination`, `popUpTo`, `<argument>` (nome, tipo, nullable, valor padrão).
- Código: `findNavController().navigate(...)`, `NavDirections` geradas (Safe Args), `startActivity(Intent(..., X::class.java))`, `X.start(...)`/`Companion` helpers, `supportFragmentManager.beginTransaction()`, `navController.navigate("rota")` no Compose, `show()` de dialogs e bottom sheets.
- Menu inferior/drawer (`res/menu`, `BottomNavigationView`, Compose `NavigationBar`) para identificar as abas da Home.

Para cada aresta, guarde `from`, `to`, `label` opcional (ex.: "logout", "cancelar") e `confirmed: true|false`. Só use `confirmed: false` quando a ordem vier de suposição (por exemplo, etapas de um wizard cuja navegação é feita por uma variável de estado que você não conseguiu seguir).

### 4. Componentes de UI

Para cada tela, ache o layout XML (via ViewBinding, `setContentView`, `inflate`, construtor `Fragment(R.layout.x)`) ou as funções `@Composable`. Expanda `<include>` e `<merge>` recursivamente. Extraia:
- **input**: id, hint/label, `inputType` decodificado (text, number, phone, email, password, numberPassword…), `maxLength`, máscaras e validações que existirem no código (regex, tamanho mínimo).
- **button**: id, rótulo.
- **texto**, **toggle** (Switch/CheckBox/Radio), **imagem** (só com `contentDescription`), **lista** (RecyclerView/LazyColumn e o layout do item, marcado como "item de lista").
- Em Compose: `TextField`, `OutlinedTextField`, `Button`, `Text(stringResource(...))`, etc.
- Resolva `@string/...` para o texto real (todas as línguas em `values*/`, priorizando espanhol e português; se houver mais de uma, mostre a principal).

### 5. Mensagens e textos

Por tela: strings de `R.string.*` usadas na classe da tela, no ViewModel e em classes de UI do mesmo pacote (erros, validações, diálogos, permissões). Guarde `key` e `text`. Ignore strings de bibliotecas (`abc_*`, `material_*`, `mtrl_*`, etc.).

### 6. API: endpoints e modelos

- Localize as interfaces Retrofit (`@GET`, `@POST`, `@PUT`, `@DELETE`, `@Headers`, `@Path`, `@Query`, `@Body`) e monte a lista: método HTTP, caminho, request e response.
- Ligue cada tela aos endpoints seguindo a cadeia **Fragment/Activity/Composable → ViewModel → UseCase → Repository → Service**. Se a cadeia não for resolvível, aproxime pelo nome do módulo e marque `inferred: true`.
- Para cada request/response ligado à tela, liste os **campos reais** com tipo (`nome: tipo`), incluindo `@SerializedName`/`@Json(name=...)` quando diferir do nome da propriedade. Aninhados: mostre o nome do tipo e liste os campos dele uma vez, na tela onde aparecer primeiro.
- **Nunca inclua no relatório** chaves de API, tokens, senhas, segredos, certificados, URLs internas com credenciais ou valores de `local.properties`/`BuildConfig`. Se encontrar algo assim, não copie; anote só que existe, sem o valor.

### 7. Consolidação em JSON

Gere `_analise/detalhe_telas.json`: uma lista com um objeto por tela.

```json
{
  "id": "login", "flow": "Autenticação", "title": "Login", "kind": "Fragment",
  "cls": "caminho/para/LoginFragment.kt",
  "layouts": ["fragment_login"],
  "inferred": false,
  "args": [{"name": "card_number", "type": "string", "nullable": false, "default": null}],
  "components": [
    {"k": "input", "id": "edTxtCard", "label": "Tu número de tarjeta", "type": "numberPassword", "max": "19", "item": false},
    {"k": "button", "id": "btnLogin", "label": "Entrar"}
  ],
  "messages": [{"key": "error_default", "text": "Ocurrió un error inesperado"}],
  "endpoints": ["POST /auth/customer/by-card-number"],
  "apis": [{"cls": "LoginRequest", "kind": "request", "fields": ["cardNumber: string", "password: string"]}],
  "next": ["logged", "reg_card"],
  "navInferred": false
}
```

Valores de `k`: `input`, `button`, `text`, `toggle`, `image`, `container`. Valores de `kind` em `apis`: `request`, `response`.

### 8. Diagrama do fluxo (Mermaid clicável)

Gere `_analise/fluxo_telas.mmd` (`flowchart LR`):
- Um `subgraph` por Activity ou módulo (ex.: `LoginActivity`, `RegisterActivity`, `HomeActivity`, `ClarificationsActivity`).
- Um nó por tela, com id curto único e rótulo legível. Dialogs em hexágono `{{ }}`, Splash em estádio `([ ])`.
- Aresta contínua (`-->`) para navegação confirmada; tracejada (`-.->`) para inferida. Rótulos só onde ajudam ("logout", "cancelar").
- Ao final, uma linha `click NÓ "#id_da_tela"` para **todo nó que corresponda a uma tela** (o id é o mesmo do JSON).

Renderize para SVG com o Mermaid CLI (`npx -y @mermaid-js/mermaid-cli -i fluxo_telas.mmd -o fluxo.svg -b transparent -c config.json`), com `securityLevel: "loose"` no config, para os `click` virarem links. Se o Chromium do Puppeteer não funcionar, use um Chrome/Chromium instalado via `-p puppeteer.json` (`executablePath`, `args: ["--no-sandbox"]`). O SVG precisa ficar **embutido no HTML** (funcionar offline). Não dependa de CDN para o diagrama.

Pós-processe o SVG:
- Troque o `id` do `<svg>` para `flowsvg` (ajuste também as regras `#id` do `<style>` interno).
- Remova `max-width` e largura fixa do `<svg>`; o zoom é controlado por JavaScript.
- Em cada `<a xlink:href="#id_da_tela">`, adicione `data-fi="N"`, sendo N o índice do fluxo da tela (para colorir por fluxo).

### 9. O HTML final

Um arquivo, `<!doctype html>` completo, com `<meta charset>` e `viewport`, CSS e JavaScript embutidos, dados embutidos como JSON (escape `</` como `<\/`). Só as fontes (IBM Plex Sans, IBM Plex Mono e Fraunces) podem vir do Google Fonts, com fallback (`system-ui`, `ui-monospace`, `Georgia`).

**Conteúdo, de cima para baixo:**
1. Cabeçalho: título "Nome do app: fluxo e telas", uma frase com pacote e versão do app (do `build.gradle`/manifest) e uma linha de contagens (telas, fluxos, campos de entrada, endpoints, modelos de API).
2. Barra fixa no topo: campo de busca (pesquisa em todo o conteúdo da tela) e chips de filtro por fluxo ("Todos" + um por fluxo).
3. Legenda curta: input, button, "inferido", linha tracejada.
4. Seção **Fluxo de telas**: botões de zoom (−, +, "Ajustar à largura", 100%) com o percentual atual, legenda de cores por fluxo e o SVG dentro de um contêiner rolável (altura `min(72vh, 680px)`), com **arrastar para mover**. Zoom inicial de 65%.
5. Grade de cartões agrupada por fluxo (título do fluxo + quantidade). Cada cartão tem borda superior na cor do fluxo e traz:
   - título, etiqueta do tipo (Fragment/Activity/Dialog), etiqueta "layout inferido" quando aplicável e o link **"↑ ver no fluxo"**;
   - nome da classe;
   - **Componentes** (tipo, rótulo, id e, para inputs, tipo e tamanho máximo); se não houver componentes, uma frase dizendo que o conteúdo é Compose ou vem do servidor;
   - **Argumentos de navegação**;
   - **Navegação**: "Vem de" e "Vai para", como links para os outros cartões (tracejados quando inferidos);
   - **API** (aberto por padrão): endpoints (host/serviço em destaque) e modelos, request com "→" e response com "←", campos como chips;
   - **Mensagens e textos** (recolhido), com `key` em fonte monoespaçada.
6. Rodapé com as limitações da extração (o que foi inferido e por quê) e o resumo da arquitetura.

**Comportamento:**
- Clique numa caixa do diagrama: limpa busca/filtro se necessário, rola até o cartão e atualiza o hash da URL.
- "↑ ver no fluxo": rola até o diagrama, destaca a caixa (contorno de destaque) e a centraliza no contêiner.
- Links de "Vem de/Vai para" também navegam até o cartão.
- Arrastar o diagrama não pode disparar clique nas caixas.
- Selecione as caixas do SVG por `id` do nó (ex.: `[id^="flowsvg-flowchart-LOGIN-"]`), lembrando que os links são `xlink:href`.

**Visual:** ferramenta de referência, sóbria, sem ornamentos. Fundo neutro levemente quente, cartões brancos, um único acento (vermelho) para estado ativo, e 6 cores de categoria para os fluxos.
- Defina todas as cores como tokens em `:root`, com versão escura em `@media (prefers-color-scheme: dark)` (`:root:not([data-theme="light"])`) e em `:root[data-theme="dark"]`, além de `color-scheme`. O diagrama também precisa mudar com o tema: force fundo, traço, texto e setas do SVG via CSS com `!important`, usando os tokens.
- Layout responsivo (grade `repeat(auto-fill, minmax(min(100%, 400px), 1fr))`), sem rolagem horizontal da página; só o diagrama rola.
- Foco visível, `overflow-wrap: anywhere` em ids, caminhos e classes, números tabulares.
- A página abre já completa: nada escondido esperando rolagem ou observer.

### 10. Verificação (obrigatória)

Antes de entregar, confirme com um script ou navegador headless (Playwright/Chromium):
1. `node --check` no JavaScript embutido.
2. Nº de cartões = nº de telas do JSON, e nº de links `<a>` no SVG = nº de nós com tela. Liste telas sem nó e nós sem tela.
3. Sem erros de console (ignore falha de carregamento das fontes se estiver offline).
4. Clique numa caixa do diagrama leva ao cartão correto; "ver no fluxo" destaca a caixa certa; clicar numa caixa de outro fluxo com um filtro ativo funciona.
5. Screenshot em tema claro e escuro do diagrama e de um cartão; olhe as imagens e corrija texto ilegível, cortado ou com contraste ruim.

## Regras gerais

- Não invente. Se um dado não estiver no código, deixe vazio ou marque como inferido, e explique no rodapé.
- Diferencie sempre **confirmado no código** de **deduzido**.
- Não copie segredos (ver seção 6).
- Mostre o progresso de forma curta e, no fim, diga só: onde ficaram os arquivos, contagens (telas, fluxos, endpoints, modelos) e o que ficou inferido.

## Entregáveis (em `_analise/`)

- `Bradescard_fluxo_e_telas.html`: o relatório final, um arquivo só.
- `fluxo_telas.mmd`: fonte do diagrama, com os `click`.
- `detalhe_telas.json`: dados por tela.
