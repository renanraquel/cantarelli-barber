# Deploy no Render.com

Cada serviço no Render roda **isolado** com sua própria URL. **Não há conflito de portas** – o Render cuida disso.

---

## 1. PostgreSQL (para o backend)

1. No [Render Dashboard](https://dashboard.render.com) → **New** → **PostgreSQL**
2. Nome: `cantarelli-barber-db`
3. Crie o banco e copie a **Internal Database URL** (para o backend usar)

---

## 2. Backend (Web Service)

1. **New** → **Web Service**
2. Conecte ao repositório: `renanraquel/cantarelli-barber`
3. **Root Directory:** `backend`
4. **Runtime:** **Docker** (Render não tem Java nativo – use Docker)
5. O Render detecta o `Dockerfile` na pasta backend e faz o build automaticamente
6. **Nenhum comando extra** – o Dockerfile já define build e start

**Environment Variables:**

| Key | Value |
|-----|-------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `JDBC_DATABASE_URL` | *(Internal URL do PostgreSQL – copie do banco)* |
| `JDBC_DATABASE_USERNAME` | *(do PostgreSQL)* |
| `JDBC_DATABASE_PASSWORD` | *(do PostgreSQL)* |
| `CORS_ALLOWED_ORIGINS` | `https://cantarelli-barber-1.onrender.com` *(URL do frontend no Render)* |

Depois de criar, copie a **URL do backend** (ex: `https://cantarelli-barber.onrender.com`).

---

## 3. Frontend (Static Site ou Web Service)

1. **New** → **Static Site** (recomendado) ou **Web Service**
2. Repositório: `renanraquel/cantarelli-barber`
3. **Root Directory:** `frontend`
4. **Build Command:** `npm install && npm run build`
5. **Publish Directory:** `dist`

**Environment Variables:**

| Key | Value |
|-----|-------|
| `VITE_BACKEND_BASE_URL` | `https://cantarelli-barber.onrender.com` |

Obrigatório: essa variável é embutida no build. Sem ela, o front chama localhost:8080 em produção.

---

## 4. Resumo de URLs

| App | URL no Render |
|-----|---------------|
| Go (existente) | sua-url-go.onrender.com |
| React (existente) | sua-url-react.onrender.com |
| **Cantarelli Backend** | cantarelli-barber-api.onrender.com |
| **Cantarelli Frontend** | cantarelli-barber.onrender.com |

Cada um usa uma URL própria, sem conflito de porta.

---

## Plano de $7

No plano Starter, serviços **free** podem "dormir" após inatividade. Os Web Services pagos ficam sempre ativos. Confira quantos serviços você pode criar no seu plano.
