# SemProtDB na Locaweb

## Estado de HTTPS em 20/10/2026

As rotas de `semprotdb.org` e `www.semprotdb.org` foram ativadas no Nginx da Locaweb,
com certificado Let's Encrypt válido até 18/01/2027 e renovação simulada aprovada.
Origem e Cloudflare passaram nos testes de página HTTP 200, saúde `UP` e
redirecionamento HTTP 301. O CNAME de `www` foi criado pelo usuário; alguns caches
recursivos ainda retornavam NXDOMAIN, apesar de 1.1.1.1 e 8.8.8.8 já resolverem o
novo registro. Para os testes públicos de `www`, foi usado `curl --resolve` com
um endereço confirmado da Cloudflare, sem desabilitar a validação TLS.

A fonte ativa do proxy é o projeto Locaweb, em
`config/nginx/conf.d/70-semprotdb.conf`; a operação está registrada em
`docs/roteamento-semprotdb.md` desse projeto. Os arquivos HTTP em `ops/nginx`
servem para a preparação inicial de uma nova instalação. Não os reaplique sobre
a configuração HTTPS ativa. A aplicação e os bancos não foram reiniciados.

## Arquitetura

O Compose inicia somente a aplicação. PostgreSQL e Nginx continuam nos containers
existentes da plataforma; não há novo banco, restauração ou alteração de DNS neste
fluxo.

| Acesso                                     | Destino                            |
| ------------------------------------------ | ---------------------------------- |
| Nginx compartilhado, rede `platform_proxy` | `semprotdb:3001`                   |
| Porta publicada no host                    | `2929` → `3001`                    |
| PostgreSQL, rede `platform_database`       | `postgres:5432`, banco `semprotdb` |

`localhost` dentro do container aponta para a própria aplicação. Use o alias
`postgres` do banco da plataforma na URL JDBC. As duas redes são externas: devem
existir antes do deploy e não são criadas nem removidas por este projeto.

## Configuração no servidor

Requisitos: Docker com BuildKit e Docker Compose v2 com suporte a `up --wait`,
redes compartilhadas disponíveis e banco já restaurado com permissões para o
usuário configurado.

O checkout no servidor fica em `/root/semprotdb`; o arquivo de ambiente fica em
`/root/semprotdb/.env`. Esse caminho pertence ao host. Dentro do container, o JAR
fica em `/app/app.jar` e os arquivos persistentes em `/app/data`, no volume Docker
`semprotdb-files`.

Na raiz do checkout do SemProtDB:

```bash
cd /root/semprotdb
cp .env.example .env
chmod 600 .env
# Edite .env com as credenciais reais antes de continuar.
docker network inspect platform_proxy platform_database >/dev/null
```

| Variável                                             | Obrigatória | Finalidade                                                                    |
| ---------------------------------------------------- | ----------- | ----------------------------------------------------------------------------- |
| `SPRING_DATASOURCE_URL`                              | Sim         | URL JDBC, por exemplo `jdbc:postgresql://postgres:5432/semprotdb`             |
| `SPRING_DATASOURCE_USERNAME`                         | Sim         | Usuário existente no PostgreSQL                                               |
| `SPRING_DATASOURCE_PASSWORD`                         | Sim         | Senha do usuário do banco                                                     |
| `JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET` | Sim         | Segredo JWT em Base64, no mínimo 256 bits; gere com `openssl rand -base64 64` |
| `SERVER_PORT`                                        | Não         | Porta interna; padrão `3001`. Mantenha `3001` para o upstream fornecido       |
| `JAVA_TOOL_OPTIONS`                                  | Não         | Opções da JVM, se necessário ajustar a memória                                |

Essas variáveis são de execução; credenciais não participam do build. O Compose
interrompe a configuração se os campos obrigatórios estiverem ausentes ou vazios.
O arquivo `.env` também é entregue ao container, permitindo configurar propriedades
Spring adicionais, como SMTP. Para senhas com `$` ou `#`, use aspas simples no
`.env`, por exemplo `SPRING_DATASOURCE_PASSWORD='valor-com-$-e-#'`. Não execute
`source .env`: quem interpreta o arquivo é o Compose.

O segredo JWT é exigido para evitar o valor padrão versionado pelo JHipster. Se
precisar manter sessões existentes, reutilize com segurança o segredo de produção;
um novo segredo invalida os tokens antigos. A senha do banco compartilhada no chat
deve ser trocada pelo responsável e o novo valor deve ficar somente no `.env`.

O runtime força os perfis `prod,docker`, HTTP em `0.0.0.0` e tratamento dos cabeçalhos
encaminhados pelo Nginx. A porta publicada é sempre `2929`; se mudar `SERVER_PORT`,
ajuste também `12-semprotdb-upstream.conf` e recarregue o Nginx após validação.

## Build e deploy

```bash
cd /root/semprotdb
bash commands/deploy.sh
```

O script funciona independentemente do diretório de onde for chamado. Ele entra
na raiz, exige `.env`, valida o Compose sem imprimir segredos, compila a imagem e
recria somente `semprotdb`. Aguarda até 300 segundos pelo healthcheck; uma falha
retorna erro e requer consultar os logs. Não há rollback automático.

O Dockerfile usa duas etapas: JDK 17 para compilar e JRE 17 para executar com
usuário sem privilégios. As imagens Java são fixadas por digest; atualizações
devem trocar esses digests deliberadamente. O perfil `docker` envia logs somente
ao console, coletados pelo Docker com rotação. A execução manual sem esse perfil
grava logs em `./logs`, relativo ao diretório de execução. Fora do Docker, configure
`LOGGING_FILE_PATH=/root/semprotdb/logs` para gravar no checkout da Locaweb.
O Maven Wrapper usa a versão do repositório e instala
Node/npm conforme `pom.xml`, incluindo o frontend no JAR. O comando de build é:

```bash
./mvnw --batch-mode --no-transfer-progress -Pprod clean verify -DskipTests=true
```

Não existe perfil Maven `https` neste checkout. O TLS fica no Nginx compartilhado;
o tráfego entre Nginx e aplicação é HTTP. Os testes são pulados como no fluxo
manual informado; o build não comprova testes funcionais. O build precisa de
acesso aos registros Docker, Maven e npm. `.env`, `.git`, `target` e `node_modules`
locais ficam fora do contexto Docker.

O Liquibase mantém o comportamento atual da aplicação e pode aplicar changelogs
pendentes ao iniciar contra o banco restaurado. Confirme a compatibilidade do dump
e das tabelas `databasechangelog`/`databasechangeloglock` antes do primeiro start.
O script não executa SQL ou restauração separadamente.

O volume `semprotdb-files` preserva `/app/data`. O Compose define
`SEMPROTDB_FILES_PATH=/app/data` e o código grava em
`<SEMPROTDB_FILES_PATH>/<DBConfig.backup.path>` quando essa opção está habilitada.
Fora do Docker, a base padrão é `./data`; use
`SEMPROTDB_FILES_PATH=/root/semprotdb/data` para armazenar os arquivos no checkout.
O diretório é criado com seus pais quando necessário. Essa configuração não
cria rotina adicional de backup nem altera essa configuração no banco. Caso esses
arquivos sejam necessários, sua cópia do servidor antigo deve ser planejada
separadamente, mantendo escrita para UID/GID `10001`. O nome do volume permanece
o mesmo; sua montagem passa a ser `/app/data`. Não use `docker compose down -v`
se precisar preservar esses arquivos.

## Nginx compartilhado

Os arquivos em `ops/nginx` seguem a estrutura do projeto Locaweb. A aplicação deve
estar na mesma rede `platform_proxy` do `platform-nginx`. O upstream usa DNS Docker
com `resolve`, aproveitando `resolver 127.0.0.11` já definido no `nginx.conf` da
plataforma, inclusive quando o IP do container mudar após deploy.

Integre os arquivos ao projeto de infraestrutura, mantendo-o como fonte da
configuração compartilhada:

| Origem neste repositório               | Destino no projeto Locaweb                       |
| -------------------------------------- | ------------------------------------------------ |
| `ops/nginx/12-semprotdb-upstream.conf` | `config/nginx/conf.d/12-semprotdb-upstream.conf` |
| `ops/nginx/70-semprotdb.conf`          | `config/nginx/conf.d/70-semprotdb.conf`          |
| `ops/nginx/semprotdb-locations.conf`   | `config/nginx/snippets/semprotdb-locations.conf` |

Os destinos remotos correspondentes ficam em `/srv/platform/nginx/`. O snippet
reutiliza `/etc/nginx/snippets/proxy.conf` da plataforma para `Host`, IP do cliente,
protocolo e upgrade. Mantém o comportamento anterior de uploads sem limite no
Nginx e buffering desativado. Limites de multipart configurados no Spring ainda
se aplicam.

Depois de integrar e revisar a configuração no projeto Locaweb, uma aplicação
autorizada deve usar `scripts/Sync-Nginx.ps1` desse projeto, que valida o Nginx antes
do reload. Não use `Sync-Vm.ps1` para uma mudança exclusiva de proxy. Este
repositório não instala configurações nem recarrega o proxy automaticamente.

Comece com `70-semprotdb.conf`, que serve HTTP e mantém o caminho ACME. Após os dois
domínios apontarem para a Locaweb e o certificado ser emitido pelo fluxo Certbot
existente, substitua seu conteúdo pelo de `semprotdb-https.conf.example`. O
certificado `semprotdb.org` deve cobrir também `www.semprotdb.org`. Preserve o
webroot ACME, o diretório Certbot montado e o timer/hook de renovação da plataforma.
Valide antes de recarregar. Não ative o arquivo HTTPS enquanto o certificado não
existir.

## Verificação após deploy

Na raiz do checkout no servidor:

```bash
cd /root/semprotdb
docker compose --env-file .env -f docker-compose.yml ps
docker compose --env-file .env -f docker-compose.yml logs --tail=100 semprotdb
curl --fail http://127.0.0.1:2929/management/health
curl --fail -I http://127.0.0.1:2929/
```

Depois da aplicação da rota HTTP, teste a partir da VM, sem depender da propagação
DNS:

```bash
curl --fail --resolve semprotdb.org:80:127.0.0.1 http://semprotdb.org/management/health
curl --fail --resolve www.semprotdb.org:80:127.0.0.1 http://www.semprotdb.org/
```

Após ativar HTTPS, valide os dois nomes com `curl --resolve` na porta `443`, sem
`-k`, e confirme o redirecionamento HTTP para HTTPS. Execute também a validação da
plataforma pelo projeto Locaweb. Healthcheck saudável confirma a checagem de saúde
do Spring, incluindo o banco; valide ainda login, consultas, downloads e uploads
com dados reais antes de concluir a migração.

Alterações no `.env` exigem recriação do container (execute novamente o deploy).
Um simples `docker restart` não recarrega as variáveis. A imagem deve ser
recompilada quando houver mudanças no código ou nas dependências.

## Validação local desta implementação

Em 19/10/2026, o build Docker completo passou com Java 17 e o frontend empacotado.
O Compose foi validado com credenciais fictícias, incluindo senha com `$` e `#`,
e rejeitou a ausência de senha obrigatória. A sintaxe do script Bash passou.

Em redes Docker isoladas, a imagem iniciou com um PostgreSQL 18 descartável e o
healthcheck ficou saudável. `/management/health` retornou `UP` diretamente e pelo
Nginx; `/` retornou HTTP 200 pelo proxy com o domínio `www.semprotdb.org`. O
processo executou como UID `10001`, com escrita no volume de arquivos. As rotas
HTTP e o modelo HTTPS passaram em `nginx -t` usando a imagem fixada no projeto
Locaweb; para HTTPS foi usado um certificado temporário de teste.

Essa validação não acessou o banco migrado nem aplicou arquivos no servidor.
Certificados reais, DNS público, permissões do banco restaurado e os fluxos de
login, consultas, downloads e uploads ainda precisam de verificação no destino.
A instalação npm também reportou vulnerabilidades nas dependências existentes;
esta alteração não atualiza essas dependências.

Após o ajuste dos caminhos, o build foi repetido e a aplicação retornou `UP`
com o volume montado em `/app/data`, permitindo criar diretórios aninhados como
UID `10001`. A sintaxe do script legado também passou na validação.
