# 🚀 API Wizard

[![Java](https://img.shields.io/badge/Java-17%2B-007396?logo=java&logoColor=white)](https://www.java.com/)
[![Picocli](https://img.shields.io/badge/Picocli-4.7.0-000000?logo=openjdk&logoColor=white)](https://picocli.info/)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36?logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**CLI interactivo para generar APIs REST Spring Boot desde un template seguro, en segundos.**

Olvídate de la configuración manual. Responde 3 preguntas y obtén un proyecto completo con autenticación JWT + OAuth2, monitoreo Prometheus, Flyway, Docker y más.

---

## 🌟 Características

### ⚡ Generación Automática

- Clona el template [spring-secure-api-starter](https://github.com/FedeHerrera10/spring-secure-api-starter) con shallow clone
- Refactoriza packages, artifactId y groupId en todo el proyecto
- Renombra la clase principal automáticamente

### 🗄️ Base de Datos

- Configura el nombre de la BD en `docker-compose.yaml` y `application.yml`
- Soporta MySQL con Flyway migrations

### 📁 Control de Salida

- Elige el directorio donde se creará el proyecto
- Default: carpeta padre del wizard

### 🔧 Stack del Proyecto Generado

- **Auth:** Spring Security + JWT + OAuth2 (Google/GitHub)
- **DB:** MySQL + Flyway
- **Monitoreo:** Prometheus + Grafana + Loki
- **Documentación:** Swagger UI / OpenAPI
- **Correo:** SMTP con rate limiting

---

## 📋 Requisitos

| Herramienta | Versión                   |
| ----------- | ------------------------- |
| Java        | 17+                       |
| Git         | Cualquier versión moderna |

> **Nota:** Maven solo es necesario si compilás desde el código fuente. Si usás el JAR directo no hace falta.

---

## 📦 Instalación

### Opción 1 — Descargar el JAR (recomendado)

**Linux / macOS:**

```bash
curl -L -o api-wizard.jar "https://github.com/FedeHerrera10/api-wizard/releases/download/v1.0.0/api-wizard.jar"
```

**Windows (cmd o PowerShell):**

```powershell
curl.exe -L -o api-wizard.jar "https://github.com/FedeHerrera10/api-wizard/releases/download/v1.0.0/api-wizard.jar"
```

### Opción 2 — Clonar y compilar

```bash
git clone https://github.com/FedeHerrera10/api-wizard.git
cd api-wizard
mvn clean package -q
```

El JAR se genera en `target/api-wizard.jar`.

---

## 🚀 Cómo Usar

### Sintaxis

```bash
java -jar api-wizard.jar init [opciones]
```

### Opciones del Comando `init`

| Flag               | Descripción                         | Default                  |
| ------------------ | ----------------------------------- | ------------------------ |
| `-n`, `--name`     | Nombre del proyecto                 | `my-api`                 |
| `-p`, `--package`  | Package base (ej: `com.acme.miapi`) | `com.fedeherrera.{name}` |
| `-db`, `--db-name` | Nombre de la base de datos          | `{name}` en snake_case   |
| `-t`, `--db-type`  | Tipo de base de datos               | `postgres`               |
| `-o`, `--output`   | Directorio de salida                | Carpeta padre `../`      |
| `-h`, `--help`     | Muestra la ayuda                    | -                        |
| `-V`, `--version`  | Muestra la versión                  | -                        |

### Modo Interactivo

```bash
java -jar api-wizard.jar init
```

El asistente te guiará paso a paso:

```
Nombre del proyecto [my-api]: ecommerce-api
Package Base [com.fedeherrera.ecommerceapi]: com.acme.ecommerce
Tipo de base de datos [postgres]: mysql
Nombre de la Base de Datos [ecommerce_api]:
```

### Modo No Interactivo (Flags)

```bash
java -jar api-wizard.jar init \
  -n ecommerce-api \
  -p com.acme.ecommerce \
  -t mysql \
  -db ecommerce_db \
  -o ~/workspace
```

---

## 🛠️ ¿Qué Hace el Wizard?

Cuando ejecutás `init`, el wizard realiza estas transformaciones automáticamente:

1. **Clona** el template (shallow clone) en el directorio de salida
2. **Limpia** los metadatos Git del template original
3. **Actualiza** `pom.xml` con el nuevo artifactId y groupId
4. **Refactoriza** todos los packages Java (`src/main/java` y `src/test/java`)
5. **Configura** `application.yml` con el nombre de la aplicación
6. **Renombra** la clase principal a `{Proyecto}Application`
7. **Aplica** el nombre de la base de datos en `docker-compose.yaml` y `application.yml`
8. **Copia** `.env_example` a `.env` y configura las variables `APP_NAME`, `DB_TYPE` y `DB_NAME` con los valores indicados.

### Árbol de Directorio Generado

```
ecommerce-api/
├── docker-compose.yaml
├── Dockerfile
├── pom.xml
├── mvnw / mvnw.cmd
├── src/
│   ├── main/
│   │   ├── java/com/acme/ecommerce/
│   │   │   ├── EcommerceApiApplication.java
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   └── entity/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/acme/ecommerce/
└── prometheus_config/
```

---

## 💻 Ejemplos Rápidos

```bash
# Proyecto básico
java -jar api-wizard.jar init -n blog-api

# Proyecto completo con todas las opciones
java -jar api-wizard.jar init \
  -n inventario-api \
  -p com.empresa.inventario \
  -t postgres \
  -db inventario_db \
  -o ~/dev

# Ver ayuda
java -jar api-wizard.jar init --help
```

---

## 🔧 Alias (Opcional)

### Linux / macOS

Agregar al `~/.bashrc` o `~/.zshrc`:

```bash
alias api-wizard='java -jar /ruta/completa/api-wizard.jar'
```

### Windows

Crear `api-wizard.bat` en una carpeta del `PATH`:

```bat
@java -jar C:\ruta\completa\api-wizard-1.0-SNAPSHOT.jar %*
```

Después de configurar el alias:

```bash
api-wizard init -n mi-api
```

---

## 📁 Estructura del Proyecto

```
api-wizard/
├── src/main/java/com/fedeherrera/cli/
│   ├── Main.java                          # Entry point
│   ├── ApiWizardCommand.java              # Comando raiz con subcomandos
│   └── command/
│       ├── InitCommand.java               # Orquestacion del wizard
│       ├── NamingUtils.java               # Utilidades de naming
│       ├── GitUtils.java                  # Operaciones Git
│       ├── ProjectConfigurator.java       # Configuracion de archivos
│       └── PackageRefactorer.java         # Refactor de packages
├── pom.xml                                # Build con maven-shade-plugin
├── DEPLOY.md                              # Instrucciones de deploy
└── README.md
```

---

## 📄 Licencia

MIT
