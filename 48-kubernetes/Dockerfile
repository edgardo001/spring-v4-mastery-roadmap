# =========================================================
# Modulo 48 - Dockerfile Multi-Stage (imagen para Kubernetes)
# =========================================================
# Misma tecnica que el modulo 26, pero pensada para K8s:
#   - Ejecutamos como usuario NO root (K8s aplica securityContext
#     runAsNonRoot=true en el deployment.yaml).
#   - -XX:MaxRAMPercentage=75.0 para que la JVM respete el limite
#     de memoria del contenedor (evita OOMKilled).
# =========================================================

# ---------------------------------------------------------
# Stage 1: build (JDK + Maven, solo para compilar)
# ---------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn -B clean package -DskipTests

# ---------------------------------------------------------
# Stage 2: runtime (solo JRE + el fat-JAR)
# ---------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos el artefacto producido por el pom (finalName=kubernetes-1.0.0).
COPY --from=builder /app/target/kubernetes-1.0.0.jar app.jar

EXPOSE 8080

# Usuario no root (buena practica + requerido por runAsNonRoot del deployment).
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# MaxRAMPercentage: la JVM lee el cgroup del contenedor y calcula heap
# como 75% del limite de memoria (resources.limits.memory). Sin esto,
# la JVM asume la RAM del NODO y termina en OOMKilled.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
