# 📱 Como gerar o APK — Guia Passo a Passo

## ✅ Opção MAIS FÁCIL: GitHub Actions (sem instalar nada no PC)

### Passo 1 — Criar conta no GitHub
Acesse https://github.com e crie uma conta gratuita (se ainda não tiver).

### Passo 2 — Criar repositório
1. Clique no botão **"New"** (ou **"+"** no canto superior direito → "New repository")
2. Nome: `collaboraai-app`
3. Deixe como **Public** (grátis para Actions)
4. Clique em **"Create repository"**

### Passo 3 — Subir os arquivos
No GitHub, clique em **"uploading an existing file"** e arraste a pasta
`CollaboraAI2` inteira para lá. Ou use o GitHub Desktop (mais fácil):
- Baixe em: https://desktop.github.com
- Clone o repositório → copie os arquivos → Commit → Push

### Passo 4 — Aguardar o build
1. Vá na aba **"Actions"** do repositório
2. Você verá o workflow **"Build APK"** rodando automaticamente
3. Aguarde ~3-5 minutos (ícone laranja = rodando, verde = pronto)

### Passo 5 — Baixar o APK
1. Clique no workflow concluído (ícone ✅ verde)
2. Role para baixo até **"Artifacts"**
3. Clique em **"CollaboraAI-debug"** para baixar o ZIP com o APK dentro

### Passo 6 — Instalar no S25
1. Transfira o APK para o celular (WhatsApp, cabo, Google Drive, etc.)
2. No S25: **Configurações → Segurança → Instalar apps desconhecidos** → ative para o app que você vai usar para abrir o APK
3. Abra o arquivo APK e instale

---

## 🛠️ Opção 2: Android Studio (no seu PC)

### Problema que você teve — SOLUÇÃO:

O botão Run estava desabilitado porque o projeto estava **sem os arquivos do Gradle Wrapper**.
Este ZIP corrigido já tem tudo. Mas se ainda travar:

1. Abra o Android Studio
2. **File → Open** → selecione a pasta `CollaboraAI2` (NÃO a pasta de dentro)
3. Aguarde a barra de progresso no rodapé terminar de sincronizar (~2-5 min na primeira vez)
4. Se aparecer "Gradle sync failed": **File → Invalidate Caches → Invalidate and Restart**
5. Após reiniciar e sincronizar → o botão ▶ Run ficará verde

### Gerar APK pelo Android Studio:
**Build → Build Bundle(s)/APK(s) → Build APK(s)**
O APK fica em: `app/build/outputs/apk/debug/app-debug.apk`

---

## ⚠️ Para instalar no S25 (Android 15)

O S25 usa o Android 15 com proteção extra. Ao instalar:
- Pode aparecer aviso "App de fonte desconhecida" → clique em **Instalar mesmo assim**
- Se o Samsung bloquear: **Configurações → Segurança e Privacidade → Instalar apps desconhecidos**
