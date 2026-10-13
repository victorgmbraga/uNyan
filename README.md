uNyan
=====
A very simple Nyan Cat example made using [uImpala](https://github.com/matheuscscp/uImpala) engine.

## Executar no Windows

Na raiz do projeto, execute no terminal do Zed ou no PowerShell:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\run.ps1
```

O script prepara um JDK 8 e Maven 3.9.9 portáteis em `target/tools`, compila o
projeto, extrai as bibliotecas nativas e abre o jogo. Não é necessário instalar
Java/Maven globalmente nem alterar o `PATH`. É necessário Windows 64 bits com
`curl.exe` disponível, além de suporte a OpenGL e saída de áudio.

Na primeira execução, é necessário acesso à internet para baixar as ferramentas
e dependências (aproximadamente 265 MiB nos três arquivos ZIP, além das
dependências Maven). O repositório original do Google Code foi desativado; o
script recupera as bibliotecas do arquivo oficial hospedado pelo Google e usa
um mirror local, sem modificar o `pom.xml`. As dependências são antigas e o
repositório arquivado pode gerar avisos de checksum no primeiro build.

Para apenas compilar e preparar as dependências, sem abrir a janela:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\run.ps1 -BuildOnly
```

O Nyan Cat acompanha o mouse; a tecla `Z` diminui sua vida.

**Atenção:** apagar `target` ou executar `mvn clean` também remove as ferramentas
portáteis e o cache local. A próxima execução do script precisará baixá-los
novamente. O script sempre inicia o jogo na raiz para encontrar `img/` e `sfx/`.
