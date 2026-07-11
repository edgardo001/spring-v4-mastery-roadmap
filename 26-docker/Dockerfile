# =========================================================
# Modulo 26 - Dockerfile Multi-Stage
# =========================================================
# Un "multi-stage build" usa VARIAS imagenes intermedias durante la
# construccion, pero solo la ULTIMA se guarda como imagen final.
# Beneficios:
#   - Imagen final ligera (JRE + JAR, ~150 MB) en lugar de pesada
#     (JDK + Maven + codigo fuente, ~800 MB).
#   - Mas segura: en produccion no viajan Maven ni herramientas de
#     compilacion (menor superficie de ataque).
#   - Repetible: no depende de que tengas Maven instalado en tu PC.
# =========================================================

# ---------------------------------------------------------
# Stage 1: build
# Imagen "pesada" con JDK 21 completo. Solo vive durante el build.
# ---------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copiamos primero el pom.xml para aprovechar la cache de capas:
# si el pom no cambia, Docker reutiliza la capa que descarga
# dependencias en builds futuros.
COPY pom.xml .
COPY src ./src

# Instalamos Maven en la imagen Alpine y compilamos el fat-JAR
# saltando los tests (los tests se ejecutan en el pipeline CI/CD,
# NO dentro del docker build).
RUN apk add --no-cache maven && mvn -B clean package -DskipTests

# ---------------------------------------------------------
# Stage 2: runtime
# Imagen "ligera" con JRE 21 (sin compilador). Esta es la que
# realmente se despliega en produccion.
# ---------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos SOLO el artefacto final desde la stage anterior.
# El nombre "docker-1.0.0.jar" es fijo (definido por <finalName>
# en el pom.xml). Renombramos a "app.jar" para simplificar el
# ENTRYPOINT.
COPY --from=builder /app/target/docker-1.0.0.jar app.jar

# Documentamos el puerto que la app expone. NO abre puertos por si
# solo: se necesita el flag -p 8080:8080 al hacer "docker run".
EXPOSE 8080

# Buena practica de seguridad: NO ejecutar como root dentro del
# contenedor. Creamos un usuario del sistema sin privilegios.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# ENTRYPOINT define el comando que se ejecuta al arrancar el
# contenedor. Formato "exec" (JSON array) para que la senal
# SIGTERM llegue directamente al proceso Java (permite shutdown
# limpio con "docker stop").
ENTRYPOINT ["java", "-jar", "app.jar"]
